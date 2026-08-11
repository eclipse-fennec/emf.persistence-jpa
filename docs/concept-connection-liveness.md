# Connection Liveness — "Registered means working"

Status: **Implemented** (first iteration 2026-07-20, tracked in
[issue #21](https://github.com/eclipse-fennec/emf.persistence-jpa/issues/21)); the
remaining open questions are listed at the end.

## 1. Problem

OSGi Declarative Services follow the credo: *only register services that actually work — the
presence of a service indicates functionality, its absence indicates non-functionality.*

Both persistence backends currently violate this for their connection services:

### MongoDB

`MongoClientComponent` registers a `MongoClient` service straight from
`MongoClients.create(connectionString)`. The Mongo driver is lazy by design — creating a
client performs **no I/O**; server discovery runs asynchronously in the background. The
service therefore appears even if the host is unreachable, the credentials are wrong, or
the connection string points nowhere.

`MongoDatabaseComponent` makes it worse: `client.getDatabase(name)` is also just a local
handle — again no I/O. So the presence of `MongoClient` **and** `MongoDatabase` in the
service registry only means *"a configuration existed"*, not *"there is a working
connection"*.

### JDBC

A `DataSource` configured via Configurator/ConfigAdmin (e.g. through a
`DataSourceFactory`-based provider) is only a factory for connections. Whether
`getConnection()` succeeds is unknown until someone calls it.
`EntityMappingPersistenceUnitConfigurator` binds that `DataSource` and registers `JPAUnit`
/ `EntityManagerFactory` — so those services also appear without any evidence of a working
database.

### Consequence

The first party to discover a broken connection is the first *user* of the persistence
layer — a `JPAUnit` lease or a resource load/save. That is the wrong layer: connection
health is an infrastructure concern and should be discovered (and made visible) by the
components that own the connection, not by their consumers.

## 2. Goals

1. A connection service (`MongoClient`, gated `DataSource`) is **only registered while
   connectivity is verified**.
2. **Startup ordering must not matter**: if the database comes up *after* the framework,
   the service appears once the connection succeeds (retry with backoff — a one-shot
   check at `@Activate` is not enough, because DS does not retry failed activations).
3. **Runtime liveness**: if the connection breaks later, the service is unregistered;
   when it recovers, it is re-registered.
4. **Uniform semantics and configuration** across Mongo and JDBC; only the probe differs.
5. **Consumers stay unchanged**: `JPAUnit` configurators, `MongoDatabaseComponent` and
   future resource factories keep their plain `@Reference` — bind/unbind does the rest.
6. **Observability**: current state, last success/failure inspectable (fits the
   diagnostics follow-up, issue #19).

### Non-goals

- No guarantee at *access* time. "The connection was alive at the last check" is never a
  hard guarantee for the next call — consumers must still handle runtime connection
  errors. The gate moves *discovery* of broken connections to the infrastructure; it does
  not remove error handling.
- No pooling, failover or HA logic.

## 3. Key design idea: gate at the bottom, let DS cascade

Both stacks already form static `@Reference` chains:

```
JDBC:   DataSource ──► EntityMappingPersistenceUnitConfigurator ──► JPAUnit / EMF service
Mongo:  MongoClient ──► MongoDatabaseComponent ──► MongoDatabase ──► (future resource factory)
```

If the **lowest service we own** is only registered while the connection is verified,
Declarative Services propagates that truth upward for free: unregistering the gated
`DataSource` deactivates the configurator, which unregisters `JPAUnit`; unregistering
`MongoClient` cascades through `MongoDatabaseComponent`. No consumer needs new code.

Therefore:

- **Mongo**: the gate lives inside `MongoClientComponent`. The client is created eagerly
  (cheap), but the `MongoClient` service is registered only once the cluster reports a
  reachable server.
- **JDBC**: we do not own the upstream `DataSource` registration (external provider). A
  new component **`GatedDataSourceComponent`** binds an upstream `DataSource` via target
  filter and re-registers it with a marker property (see 5.2) only while probes succeed.
  Persistence-unit configurations then target the gated `DataSource` by filter.

An alternative — registering the services immediately and expressing liveness *only*
through a separate `org.osgi.service.condition.Condition` — was considered and rejected:
it forces every consumer to add a second reference, which defeats goal 5 and dilutes the
credo. Instead, the gated service stays the primary truth, and a `Condition` is
registered *in addition* (see 5.4) for consumers that want to depend on liveness without
coupling to the backend API. Introspection is served by a runtime service with DTOs
(see 5.5).

## 4. Shared building block: `LivenessGate`

A small, backend-agnostic helper (plain class, no DS) that owns the state machine and the
`ServiceRegistration`:

```
            probe ok                    N consecutive failures
  DOWN ────────────────► UP (registered) ────────────────────► DOWN (unregistered)
    ▲                                                             │
    └────────────── retry with backoff (min..max) ◄──────────────┘
```

- **States**: `DOWN` (service not registered, probing with exponential backoff between
  `retryMin` and `retryMax`) and `UP` (service registered, probing every
  `checkInterval`).
- **Debounce**: `failureThreshold` consecutive probe failures are required before
  `UP → DOWN` (avoids flapping on transient blips); a single success is enough for
  `DOWN → UP`.
- **Probe SPI**: `ConnectionProbe { void ping(Duration timeout) throws Exception; }` —
  implemented per backend.
- **Registrations owned by the gate**: the functional service (while UP), the
  `Condition` (while UP, see 5.4), and state reporting to the liveness runtime
  (always, see 5.5).
- **Threading**: each gate owns a single-threaded scheduler (daemon thread); probes never
  run on the DS actor thread, and a hanging probe can only wedge its own gate, never the
  others. `@Activate` stays cheap and never blocks on I/O — it only schedules the first
  probe.
- **Location**: `org.eclipse.fennec.persistence` (core), package
  `org.eclipse.fennec.persistence.liveness`. The backend bindings live in their backend
  bundles: the Mongo probe in the Mongo bundle, the JDBC probe in the Eclipselink bundle.
  `javax.sql` itself is JavaSE, but a `@Reference` to `DataSource` puts a mandatory
  `osgi.service` requirement into the manifest — that must not happen in the
  backend-neutral core (#124).
- **Logging**: `java.util.logging`, state transitions at `INFO`, probe failures while
  `DOWN` at `FINE` (avoid log spam during a long outage).

### Configuration (identical keys for both backends)

| Key | Default | Meaning |
|-----|---------|---------|
| `liveness.enabled` | `true` | `false` = register immediately, no probing (opt-out, e.g. tests) |
| `liveness.checkInterval` | `30` s | probe period while UP; `0` = only initial check, no periodic re-check |
| `liveness.checkTimeout` | `5` s | timeout per probe |
| `liveness.failureThreshold` | `3` | consecutive failures before unregistering |
| `liveness.retryMin` / `liveness.retryMax` | `1` s / `30` s | backoff bounds while DOWN |

## 5. Backend integration

### 5.1 Mongo — `MongoClientComponent`

- Create the `MongoClient` at `@Activate` as today, but **do not register it yet**; hand
  the registration to a `LivenessGate`.
- **Probe**: `client.getDatabase("admin").runCommand({ping: 1})` with the configured
  timeout.
- **Push instead of poll where possible**: the driver's `ClusterListener` (registered via
  `MongoClientSettings`) reports topology changes. Cluster loses its last reachable
  server → treat as probe failure immediately; a server becomes reachable while DOWN →
  trigger an immediate probe instead of waiting for the backoff timer. Periodic probing
  remains as a safety net (listener events cover topology, not e.g. auth changes).
- `MongoDatabaseComponent` stays **unchanged** — its static reference to `MongoClient`
  gives it correct appear/disappear behavior for free. A per-database probe (databases
  can have different auth) is a possible later refinement, not part of this iteration.

### 5.2 JDBC — new `GatedDataSourceComponent`

New factory component (PID `persistence.jdbc.gate`) in the Eclipselink bundle, package
`org.eclipse.fennec.persistence.eclipselink.liveness`:

- `@Reference(name = "dataSource")` — binds the upstream `DataSource` via the standard
  `dataSource.target` filter in the factory configuration.
- Re-registers the same instance as `DataSource` with all configured extra properties
  plus the marker **`fennec.liveness=checked`**, gated by a `LivenessGate`.
- **Probe**: `try (Connection c = ds.getConnection()) { c.isValid(timeout); }`.
- Persistence-unit configurations opt in by targeting
  `(fennec.liveness=checked)` (plus their usual selection properties) in their
  `fennec.jpa.dataSource.target` filter. `EntityMappingPersistenceUnitConfigurator`
  itself needs **no code change**.

Gating `JPAUnit` registration directly inside the configurator was considered and
rejected: it duplicates the mechanism per configurator variant, does not help other
`DataSource` consumers, and mixes connection health into a class whose job is JPA
configuration. The republisher is one generic component that benefits everything
downstream.

### 5.3 Interplay with issue #20 (lazy JPAUnit, idle-close)

Unchanged and complementary: `JPAUnit` activation stays cheap (no EclipseLink deploy).
The cascade only controls *when the `JPAUnit` service exists at all*. While the database
is down there is no `JPAUnit`, hence no `jpa://` dispatch — exactly the credo. When the
gate re-registers the `DataSource`, the configurator reactivates and the lazy deploy
happens on first use, as designed.

### 5.4 Condition service — declarative liveness dependency

While UP, each gate additionally registers an `org.osgi.service.condition.Condition`
(`Condition.INSTANCE`) with `osgi.condition.id = fennec.liveness.<ident>`. This is
nearly free — the gate owns the registration lifecycle anyway — and covers a case the
gated service cannot: components that want to align their lifecycle with connection
health **without importing the backend API** (no `javax.sql`, no Mongo driver on their
classpath). Examples: a schema-migration job, a readiness aggregator, ordering-only
dependencies. With DS 1.5 this is consumed elegantly via the satisfying condition
(`osgi.ds.satisfying.condition.target=(osgi.condition.id=fennec.liveness.<ident>)`),
i.e. without any reference field in the consumer.

A `Condition` is deliberately binary and presence-only — it answers *"is connection X
functional right now?"* as a declarative dependency. It never explains *why* something
is down; that is the runtime service's job (5.5).

### 5.5 Introspection — `PersistenceLivenessRuntime` with DTOs

Following the established whiteboard-runtime pattern (`HttpServiceRuntime`,
`JaxrsServiceRuntime`), one **always-on singleton** service `PersistenceLivenessRuntime`
in the core bundle aggregates the state of all gates — always-on is essential: the whole
point is being able to inspect things precisely when a connection is *down*.

- `getRuntimeDTO()` returns a DTO tree following OSGi DTO conventions (public fields,
  extending `org.osgi.dto.DTO`, directly serializable for REST/monitoring):

  ```
  LivenessRuntimeDTO { GateDTO[] gates }
  GateDTO {
      String  ident;              // gate identifier (client ident / gate config name)
      String  backendType;        // "mongo" | "jdbc"
      String  state;              // "UP" | "DOWN"
      long    lastSuccess;        // epoch millis, 0 if never
      long    lastFailure;        // epoch millis, 0 if never
      String  lastFailureMessage; // null while healthy
      int     consecutiveFailures;
      long    probeCount;
      Map<String,String> config;  // effective liveness.* settings
  }
  ```

- The runtime registration carries a `service.changecount` property incremented on every
  gate state transition, so tools can watch for changes instead of polling DTOs.
- Gates report to the runtime through a **core-internal registry** (the `LivenessGate`
  constructor takes it as collaborator) — no public whiteboard contract is needed for
  gates; only the read-only runtime service is API.
- A Gogo command and the issue #19 diagnostics work dock onto this single aggregation
  point.

Division of labor in one line: **gated service = functionality, `Condition` =
declarative liveness dependency without API coupling, runtime DTOs = diagnosis.**

## 6. Testing

(Before this feature there were **no Mongo OSGi integration tests at all** — the Mongo
TCK tests are plain JUnit and bypass the DS components. The `MongoLivenessTest` added
here closed that gap.)

1. **Unit tests for `LivenessGate`** (core module, plain JUnit + Mockito): scripted probe
   + deterministic scheduler. Cover: starts DOWN and registers after first success;
   registers late when probe succeeds only after N retries (backoff verified); stays UP
   across `failureThreshold - 1` failures; unregisters at the threshold; recovers;
   `liveness.enabled=false` registers immediately; timeout handling; clean shutdown while
   a probe is in flight. Also: `Condition` registered/unregistered in lockstep with the
   functional service, and the runtime registry receives every state transition.
2. **Unit tests for the runtime**: `getRuntimeDTO()` reflects registered gates and their
   state/counters; `service.changecount` increments on each transition; DTO snapshot is
   stable while a transition happens concurrently.
3. **JDBC integration** (`org.eclipse.fennec.persistence.test`, bndrun): H2 in **TCP
   server mode** so the database can actually be stopped/started. Assert: no gated
   `DataSource` (and consequently no `JPAUnit`) while the server is down; both appear
   after start; `JPAUnit` disappears after stopping the server and reappears on restart —
   this verifies the whole DS cascade, not just the gate. Alongside: the
   `osgi.condition.id` appears/disappears with the gate, and
   `PersistenceLivenessRuntime` reports matching `GateDTO` states throughout.
4. **Mongo integration**: new OSGi test setup (own bndrun in the test module) using the
   docker-CLI approach from `MongoTestSupport` with JUnit assumptions when unavailable.
   Cases: config with unreachable port → no `MongoClient`/`MongoDatabase` service within
   a bounded wait, but the runtime DTO shows the gate as DOWN with a failure message;
   start container → services appear; stop container → services disappear after
   `failureThreshold` probes; restart → they return.
5. **TCK stays untouched** — it wires resources directly and does not go through the
   gated components.

## 7. Migration / compatibility

- Mongo gate: **on by default**. The components are new; anyone relying on
  "service appears even without a database" is relying on a bug. Opt-out via
  `liveness.enabled=false`.
- JDBC gate: **opt-in by nature** — it only affects configurations that add the gate
  component and point their `dataSource.target` at `(fennec.liveness=checked)`. Existing
  setups keep working unchanged.
- New config keys documented in `docs/configuration-reference.md`.

## 8. Open questions

1. Should `MongoDatabaseComponent` get its own (per-database) probe in a second
   iteration, or is client-level gating sufficient long-term?
2. Should the gated `DataSource` *replace* visibility of the upstream one (e.g. recommend
   `service.ranking`), or purely coexist via the marker property? (Proposal: coexist;
   selection is explicit via target filter.)
3. Defaults: is 30 s check interval / threshold 3 the right trade-off for your
   deployments, or should the defaults be more conservative?

*(Resolved: observability is covered by the always-on `PersistenceLivenessRuntime` with
DTOs plus a per-gate `Condition` — see 5.4/5.5; the earlier ad-hoc per-gate status
service idea is dropped, and the Gogo/REST exposure on top of the runtime DTOs belongs
to the issue #19 diagnostics work.)*
