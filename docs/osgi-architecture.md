# OSGi Architecture: Persistence Units, JPAUnit and the `jpa://` Whiteboard

How Fennec Persistence JPA runs inside an OSGi framework (with
[emf.osgi](https://github.com/eclipse-fennec/emf.osgi)): what you configure, which
services appear, what happens by default — and why the defaults are the safe ones.
Introduced with [issue #20](https://github.com/eclipse-fennec/emf.persistence-jpa/issues/20).

## The picture

```
factory config                       services                            consumers
──────────────                       ────────                            ─────────
fennec.jpa.PersistenceUnit  ─────►   JPAUnit                ◄─────┐
  name="shop"                          osgi.unit.name=shop        │
  (DataSource, model,                EntityManagerFactory         │      ResourceSet (injected)
   mapping, unit name, …)              osgi.unit.name=shop        │        │
                                       (lazy ServiceFactory)      │        │  jpa://shop/Product
fennec.jpa.PersistenceUnit  ─────►   JPAUnit                ◄──┐  │        ▼
  name="crm"                           osgi.unit.name=crm      │  └── JPAResourceFactory ("jpa")
                                     EntityManagerFactory      │        one service for the
                                       osgi.unit.name=crm      └──      whole jpa scheme
```

- **One configuration = one persistence unit** (one database), delivered as two services.
- **One `JPAResourceFactory` serves the whole `jpa` URI scheme.** The emf.osgi
  resource-factory registry keeps a single factory per protocol (last one wins), so
  per-database factories would overwrite each other. Instead the single factory
  **dispatches by URI**: the authority of `jpa://<unitName>/<Entity>` is matched against
  the `osgi.unit.name` service property of the tracked `JPAUnit` services.

## The services per persistence unit

Both are registered by the configurator component as soon as its configuration exists,
and both carry the same identifying properties:

| Property | Value |
|----------|-------|
| `osgi.unit.name` | the configured persistence-unit name (= the URI authority) |
| `osgi.unit.version` | bundle version of the providing bundle |
| `osgi.unit.provider` | `org.eclipse.persistence.jpa.PersistenceProvider` |

### `JPAUnit` — the primary capability

`org.eclipse.fennec.persistence.eclipselink.spi.JPAUnit` is the narrow interface a
`jpa://` resource actually needs — deliberately *not* the full
`jakarta.persistence.EntityManagerFactory`:

```java
try (JPAUnit.Lease lease = unit.lease()) {
    EntityManager em = lease.createEntityManager(); // fresh, non-shared, per operation
    Server session  = lease.getServerSession();     // shared, thread-safe descriptor access
}
```

Every use goes through a **lease**. The lease guarantees a live EclipseLink factory for
its duration and drives the usage counting described below. The heavyweight EclipseLink
`EntityManagerFactory` stays private inside the unit.

### `EntityManagerFactory` — interop registration

For consumers that want plain JPA (including `JpaHelper.getServerSession(emf)`), the
*real* EclipseLink factory is also registered — via an OSGi **`ServiceFactory`**:

- registering costs nothing (no EclipseLink deploy),
- the first `getService` of a consuming bundle opens a lease and returns the live
  factory,
- `ungetService` closes that lease again.

While any bundle holds the service, the unit counts as "in use" and will not be closed.
Do not cache the instance beyond your get/unget window.

## Lifecycle: lazy build, usage counting, idle close

The expensive part of a persistence unit — the EclipseLink deploy (descriptors, dynamic
types, optional DDL) — is **not** done at configuration time. The unit follows a strict
pay-per-use lifecycle:

```
config created ──► JPAUnit registered (cheap, "cold")
first lease    ──► real EclipseLink factory built           (first access pays the deploy)
leases > 0     ──► factory guaranteed open
last lease closed ──► idle timer armed (default 60 s)
timer expires  ──► factory closed: caches, connection pool, session released
next lease     ──► fresh factory built transparently
config deleted ──► services unregistered, factory closed
```

Consequences an operator should know:

- **A unit that is never addressed never touches the database.** Registering ten
  configured units costs ten cheap service registrations, nothing more.
- **The first operation on a unit is slower** — it pays the EclipseLink deploy (and DDL,
  if enabled). Subsequent operations run on the warm factory.
- **After the idle timeout the JVM holds no connections, caches or sessions for the
  unit.** The next access rebuilds; the second-level cache starts cold again.
- The idle timeout is one knob, `emfIdleTimeout` (seconds), on both PU factory PIDs:

| Value | Behaviour |
|-------|-----------|
| `> 0` (default `60`) | close the real factory after that many seconds without use |
| `0` | close immediately when the last use ends |
| `< 0` | never close automatically; only when the configuration is deleted |

## Configuring a unit

Everything is driven by factory configurations — no code, no persistence.xml editing.
See the [Configuration Reference](configuration-reference.md) for the complete key
tables. Minimal example (two units, two databases):

```properties
# fennec.jpa.PersistenceUnit~shop.cfg
fennec.jpa.persistenceUnitName=shop
fennec.jpa.model=(emf.name=shopmodel)
fennec.jpa.mappingFile=file:/…/shop.eorm
fennec.jpa.dataSource.target=(dataSourceName=shopDs)

# fennec.jpa.PersistenceUnit~crm.cfg
fennec.jpa.persistenceUnitName=crm
fennec.jpa.model=(emf.name=crmmodel)
fennec.jpa.mappingFile=file:/…/crm.eorm
fennec.jpa.dataSource.target=(dataSourceName=crmDs)
fennec.jpa.emfIdleTimeout=300
```

A consumer then only needs a `ResourceSet` (from emf.osgi) and URIs:

```java
Resource products = resourceSet.getResource(URI.createURI("jpa://shop/Product"), true);
Resource contacts = resourceSet.getResource(URI.createURI("jpa://crm/Contact"), true);
```

Both go through the same single factory; the URI authority decides which database
answers.

## Error behaviour: fail loud, never fall back

A URI whose authority matches **no** available unit still yields a resource — but every
operation on it fails with an error diagnostic naming the missing unit
(`"No persistence unit 'x' is available for URI …"`), surfaced through the standard EMF
`Resource#getErrors()` and an `IOException`/wrapped exception. There is **no silent
fallback** to another unit and no partially working resource. The same applies when a
unit's configuration is deleted while resources on it are still around.

## Security by default

The defaults are chosen so that an unconfigured or minimally configured system exposes
and risks as little as possible:

| Concern | Default | Rationale / override |
|---------|---------|----------------------|
| Schema changes | `eclipselink.ddl-generation` = **`none`** | The framework never alters a database schema unless explicitly configured (`fennec.jpa.ext.eclipselink.ddl-generation=create-or-extend-tables`, …) |
| Database credentials | **never** in the PU configuration | Credentials live in the `DataSource` service (own PID, e.g. the H2/daanse factory); the PU config only carries a *service filter* (`fennec.jpa.dataSource.target`) |
| Unit selection | explicit by name | Dispatch is exact-match on `osgi.unit.name`; unknown names fail loudly with a diagnostic — no guessing, no fallback |
| Resource holding | idle close after **60 s** | Unused units release DB connections, sessions and caches automatically; `-1` (keep open) is an explicit opt-in |
| Bytecode weaving | **off** (`eclipselink.weaving=false`) | No runtime class transformation |
| Transactions | `RESOURCE_LOCAL` | No implicit JTA enlistment |
| JPQL construction | entity alias from the validated descriptor | Resource queries are built from EclipseLink descriptor metadata, not from URI input, preventing JPQL injection via crafted URIs |
| SQL/JPQL by consumers | not exposed via `JPAUnit` | The capability surface is `createEntityManager()` + read-only descriptor access; consumers needing more must consume the `EntityManagerFactory` service explicitly |
| Logging | EclipseLink at `WARNING`, no thread/timestamp detail | Raise via `fennec.jpa.ext.eclipselink.logging.level` when debugging |

## Testing hooks

The OSGi integration test
`org.eclipse.fennec.persistence.test/src/…/JPAUnitWhiteboardTest.java` demonstrates and
verifies the architecture end to end: two units on two H2 databases with URI dispatch
and data isolation, the missing-unit diagnostic, and the full usage-count lifecycle
(hold keeps the factory open → idle closes it → next lease rebuilds). The unit-level
lifecycle logic is covered by `LazyJPAUnitTest` in the eclipselink module.
