# MongoDB Backend Architecture

How the MongoDB backend (`org.eclipse.fennec.persistence.mongo`) works internally: the
OSGi component chain that produces a connection, the liveness gating around it, the
resource layer that turns EMF operations into MongoDB operations, and the BSON codec
bridge that maps EObjects to documents. This is the Mongo counterpart to the
[OSGi Architecture](osgi-architecture.md) document, which covers the JPA side. Design
history and the original decision record live in the
[Mongo persistence working document](discussion-mongo-persistence.md).

The backend implements the same backend-agnostic contract as JPA —
`PersistenceResource` and `StreamingResource` from `org.eclipse.fennec.persistence` —
but takes a fundamentally different mapping route: **no eorm model, no EclipseLink, no
schema**. EObjects are (de)serialized to `BsonDocument`s through the Fennec codec
framework and written with the plain MongoDB sync driver.

## The picture

```
factory configs                      services                          resource layer
───────────────                      ────────                          ──────────────
persistence.mongo.client  ──►  MongoClientComponent
  ident="main"                   │ creates client (no I/O)
  connectionString=…             ▼
  liveness.* (optional)       [LivenessGate] ── ping probe ──► MongoDB
                                 │ registered only while UP:
                                 ├─► MongoClient                        ResourceSet
                                 │     mongo.client.ident=main            │
                                 └─► Condition                            │ mongodb://db/Person
                                       osgi.condition.id=                 ▼
                                       fennec.liveness.main         MongoResourceFactory
                                        ▲ DS reference cascade           │ creates per URI
persistence.mongo.database ─► MongoDatabaseComponent                     ▼
  alias="appdb"                  │ client.target filter             MongoResourceImpl
  database="app"                 ▼                                       │ BSON codec
  client.target=(…=main)      MongoDatabase  ───────────────────────────┘
                                 mongo.database.alias=appdb
```

- **One client configuration = one `MongoClient`**, registered only while a ping probe
  verifies the connection.
- **One database configuration = one `MongoDatabase`**, bound to its client by target
  filter. Declarative Services propagates liveness upward for free: no client service,
  no database service.
- **The resource layer is connection-agnostic**: `MongoResourceImpl` receives the
  `MongoDatabase` from its factory; the URI carries addressing only.

## Component architecture

### `MongoClientComponent` — factory PID `persistence.mongo.client`

One component instance per factory configuration (`ConfigurationPolicy.REQUIRE`).
At `@Activate` it creates the `MongoClient` from the configured connection string —
which is deliberately safe, because creating a Mongo client performs **no I/O** — and
hands the OSGi registration to a `LivenessGate` instead of registering directly. The
service carries the `mongo.client.ident` property so downstream components can select
it by filter.

Configuration (see the [Configuration Reference](configuration-reference.md)):

| Key | Meaning |
|-----|---------|
| `ident` | unique client id; becomes `mongo.client.ident` and the condition id `fennec.liveness.<ident>` |
| `connectionString` | standard MongoDB connection string (`mongodb://host:port/?…`) |
| `liveness.enabled` | default `true`; `false` registers immediately without probing |
| `liveness.checkInterval` / `checkTimeout` / `failureThreshold` / `retryMin` / `retryMax` | probe tuning, defaults 30 s / 5 s / 3 / 1 s / 30 s |

### `MongoDatabaseComponent` — factory PID `persistence.mongo.database`

Resolves a `MongoDatabase` handle (`client.getDatabase(name)` — again no I/O) from the
`MongoClient` bound via the standard `client.target` filter and registers it with the
`mongo.database.alias` property. It contains **no liveness code of its own**: its static
`@Reference` to the gated client gives it correct appear/disappear behavior for free.
This is the DS reference cascade described in the
[Connection Liveness concept](concept-connection-liveness.md) — gate the lowest service
you own, let DS propagate the truth upward.

### Resource layer wiring (in flux)

`MongoResourceFactory` is currently a **plain `Resource.Factory`**, not a DS component:
consumers register it on a `ResourceSet` for the `mongodb` protocol themselves,
passing the `MongoDatabase`, a `MetadataService` and an optional `CodecValueRegistry`.
An emf.osgi whiteboard integration analogous to the single dispatching
`JPAResourceFactory` for `jpa://` (see [OSGi Architecture](osgi-architecture.md)) does
not exist yet — a `mongodb://` resource would need URI dispatch onto the registered
`MongoDatabase` services by alias. Until then the DS chain ends at the `MongoDatabase`
service.

## Connection liveness

The full model — state machine, `Condition` services, runtime DTOs — is specified in
the [Connection Liveness concept](concept-connection-liveness.md); this section only
summarizes the Mongo-specific integration.

The `LivenessGate` (shared, backend-agnostic, in
`org.eclipse.fennec.persistence.liveness`) owns the `MongoClient` service registration:
it starts DOWN, probes with exponential backoff until the first success registers the
service, then re-probes every `checkInterval`; `failureThreshold` consecutive failures
unregister it again. While UP it additionally registers an
`org.osgi.service.condition.Condition` with id `fennec.liveness.<ident>`, and it always
reports its state to the singleton `PersistenceLivenessRuntime` (DTOs with state,
last success/failure, probe count — inspectable precisely when the connection is down).

Two Mongo-specific details:

- **Probe = `ping` with CSOT.** The probe runs
  `client.getDatabase("admin").withTimeout(timeout, MILLISECONDS).runCommand({ping: 1})`.
  The client-side operation timeout (CSOT) via `withTimeout` bounds the **whole**
  operation including server selection — without it, a probe against an unreachable
  server would block for the full `serverSelectionTimeout` (driver default 30 s)
  instead of the configured probe timeout.
- **Push instead of poll.** A driver `ClusterListener` (registered through
  `MongoClientSettings`) bridges topology events into the gate: whenever server
  reachability flips (last server lost, or a server becomes reachable while DOWN), it
  calls `gate.probeNow()`, replacing the scheduled probe with an immediate one. Both
  recovery and failure detection therefore do not wait for the next poll tick; periodic
  probing remains as a safety net for conditions topology events cannot see (e.g. auth
  changes). The listener guards against the window during `MongoClients.create`, where
  initial events fire before the gate field is assigned.

Probing runs on a gate-owned daemon thread, never on the DS actor thread, and
`@Activate` never blocks on I/O.

## Resource pipeline

### URI semantics

```
mongodb://<db>/<collection>[/<id>]
```

- The **authority** names the database — for addressing and for the proxy URIs written
  into documents. The actual connection is the `MongoDatabase` injected by the factory;
  the resource never resolves the authority itself.
- The **first segment** is the collection. By convention collection = EClass name;
  the `Options` table-EClass option overrides it per operation.
- The optional **second segment** is a single document id, turning `load()` into a
  point lookup.

The EClass for decoding is resolved from the table-EClass option first, then by
classifier-name lookup across the `ResourceSet`'s package registry and the global
`EPackage.Registry`.

### Operations

`MongoResourceImpl` extends the codec's `CodecResource` and implements
`PersistenceResource` and `StreamingResource`:

| Resource operation | MongoDB operation |
|--------------------|-------------------|
| `load()` | `find()` over the collection, or `find({_id: …})` for an id URI; optional server-side paging (`skip`/`limit`) via the page-size option |
| `save()` | one `bulkWrite` of `ReplaceOneModel` upserts (`{_id: …}` filter, `upsert: true`) — insert and update are the same path |
| `delete()` | `deleteOne({_id: …})` per contained object, then contents cleared |
| `count()` / `exist()` | `countDocuments()` / `countDocuments(limit: 1)` |
| `stream()` | cursor-backed `java.util.Stream` (lazy decode per document, `batchSize` from the page-size option, cursor closed via `Stream.onClose`) |
| `getEObject(fragment)` | `find({_id: …}).first()`, decode, attach to contents — proxy resolution, see below |

PushStream support is not Mongo-specific: the shared
`org.eclipse.fennec.persistence.pushstreams` add-on wraps any `StreamingResource`.

### Diagnostics

Failures surface through the standard EMF channels: `MongoDiagnostic` (an EMF
`Resource.Diagnostic` carrying message, URI location and the causing exception) is
added to `getErrors()`/`getWarnings()`, and load/save/delete additionally throw an
`IOException` wrapping the driver exception. Codec-level diagnostics collected during
decoding (`DiagnosticCollector`) are merged into the same lists. Missing collection
segments produce warnings, not errors; a failed proxy resolution produces a warning
and returns `null`.

## BSON (de)serialization

Mapping is delegated to the Fennec codec framework (`org.eclipse.fennec.codec`,
`org.eclipse.fennec.codec.bson`) with EClass metadata provided by a `MetadataService`
(`org.eclipse.fennec.model.metadata`) — there is no mapping model and no
transformation pipeline like the JPA side's eorm/processor chain.

- **BsonDocument-direct.** EObjects are written into / read from `BsonDocument`s
  through the codec's `BsonFormatDelegate` / `BsonFormatReaderDelegate`, bridged via
  `FormatDelegateGenerator` / `FormatDelegateParser` into the Jackson pipeline. No byte
  stream or JSON text is involved (the old gecko Mongo integration's
  `URIConverter`-and-streams keyhole is exactly what this replaces).
- **Mapper construction.** The resource lazily builds one Jackson `ObjectMapper` with a
  `CodecModule` configured from the resolver's global configuration and the optional
  `CodecValueRegistry` (copied per resource by the factory). No type-discriminator
  service is set; instead every decode passes an `EXPECTED_TYPE` hint — the published
  codec.metadata snapshot does not yet export the type-discriminator package. This is
  an acknowledged interim state.

### Identity: EMF id ↔ `_id`

The EMF ID attribute (`eClass().getEIDAttribute()`) maps to the Mongo `_id` field,
typed (`BsonInt32`/`BsonInt64`/`BsonString`) according to the attribute's instance
class. On save, an unset **String-typed** id gets a generated `ObjectId` hex string
that is **written back** into the EObject before encoding — the caller ends up with
the persisted identity. An unset numeric id is a hard error (`IOException`): the
backend never invents numeric ids. An EClass without an ID attribute cannot be saved.

### Containment, references, proxies

- **Containment children are embedded** in the parent's document — one root EObject =
  one document, including its containment tree. Loading materializes the children
  (no proxies) with correct `eContainer()` wiring.
- **Non-containment references are stored as URIs/ids**, not foreign keys.
  `getURIFragment` returns the target's EMF id, so a same-resource reference is a bare
  id fragment; on decode, unresolved references become EMF proxies. A bare id is
  expanded using the reference's effective type:
  `mongodb://<db>/<TargetType>#<id>`; absolute target URIs are kept as-is. Proxies
  resolve through the `ResourceSet` back to `getEObject(String)`, which does a point
  `_id` lookup, decodes and attaches the result (standard EMF pattern, no second round
  trip). Two fragment shapes are accepted: the plain id, and the JPA-compatible
  `//refName/idAttr/idValue` persistence proxy format.
- **Cross-backend references work in one direction.** Because references are URIs, a
  Mongo document can reference a JPA-persisted object (`jpa://…#<id>` resolves through
  a shared `ResourceSet` via `JPAResourceImpl.getEObject`); the reverse is impossible —
  JPA stores references as foreign keys within its own database. Verified by
  `MixedBackendResourceSetTest`.

### Known workaround: cross-document serialization ([emf.codec#50](https://github.com/eclipse-fennec/emf.codec/issues/50))

The codec's format-delegate write path carries no `CodecWriteContext`, so it cannot
detect that a reference target lives in a *different* resource — such targets would be
serialized as bare fragments (and unresolved proxies as their type URI), which do not
resolve across resources. `MongoResourceImpl.rewriteCrossResourceReferences` therefore
post-processes the encoded document: reference values whose targets live in another
resource (or are proxies) are rewritten to absolute EMF URIs
(`EcoreUtil.getURI` / `eProxyURI`). The rewrite is idempotent and becomes a no-op once
the codec writes absolute URIs itself. **Known limitation:** only the root object's
references are rewritten, not those of nested containment children. This is interim
code, to be removed when emf.codec#50 lands a writer-side `ContextHelper.RESOURCE`
fallback.

## Comparison: Mongo backend vs. JPA backend

Shared across both backends: the core API (`PersistenceResource`,
`StreamingResource`, `Options`), the PushStream add-on, the liveness building blocks
(`LivenessGate`, `Condition`, `PersistenceLivenessRuntime`) and the TCK contract.

| Concern | JPA backend | Mongo backend |
|---------|-------------|---------------|
| URI shape | `jpa://<unitName>/<Entity>` | `mongodb://<db>/<collection>[/<id>]` |
| Mapping metadata | eorm model → processor pipeline → EclipseLink descriptors | none — codec + `MetadataService` work directly off the Ecore model |
| Provider | EclipseLink (`EPersistenceContext`, `EClassDescriptor`, …) | MongoDB sync driver, no ORM layer |
| Schema | relational; optional DDL generation (default `none`) | schemaless — collections appear on first write, no DDL concept |
| Value conversion | `ConverterService` type converters | codec `CodecValueRegistry` |
| Containment | separate tables / joins | embedded sub-documents (one root = one document) |
| Non-containment references | foreign keys — same database only | URIs — cross-resource and cross-backend (Mongo → JPA works) |
| Fetch semantics | eorm-driven lazy/eager/batch | embedded tree loads eagerly; references are always proxies |
| Id generation | sequences/identity (numeric) | `ObjectId` hex, String-typed ids only; numeric ids fail loudly |
| Save semantics | EntityManager transaction per operation | one `bulkWrite` of upserts; no multi-document transaction |
| OSGi connection services | external `DataSource`; liveness gate **opt-in** (`persistence.jdbc.gate`) | own `MongoClient`/`MongoDatabase` components; liveness gate **on by default** |
| OSGi resource dispatch | `jpa://` whiteboard (`JPAUnit` + single dispatching factory, lazy build/idle close) | manual `MongoResourceFactory` registration; whiteboard not yet built |

## Testing architecture

### The backend-agnostic TCK (`org.eclipse.fennec.persistence.tck`)

`AbstractPersistenceTCK` is a plain-JUnit compatibility suite that encodes the shared
persistence contract once; each backend supplies a small SPI: `setUpBackend`,
`tearDownBackend`, `createBackendResourceSet` (ResourceSet wired with the backend's
factory) and `uriFor(typeName)`. The suite covers attribute round trips, containment,
single- and multi-valued non-containment references resolving as proxies through the
ResourceSet, bidirectional references, `count`/`exist`/`delete`, the id-generation
write-back contract, `stream()` and PushStream delivery.

Bindings: `JpaPersistenceTckTest` / `MongoPersistenceTckTest`, plus String-id variants
(`*StringIdPersistenceTckTest` on `data/tck-string.ecore`) — the same test bodies run
against int-typed and String-typed EMF ids. The Mongo int-id binding overrides the
id-generation test to assert the documented contract instead: numeric ids cannot be
generated, the save fails with a clear error.

`MongoTestSupport` provides the database without any test-container library: an
externally supplied instance (`-Dmongo.uri` / `MONGO_URI`) wins; otherwise a container
is started through the local **docker CLI** (podman as fallback, image overridable via
`-Dmongo.test.image`), the mapped port is read back with `docker port`, and a shutdown
hook removes the container. Without either, the Mongo tests are **skipped via JUnit
assumptions**, never failed. Each test run uses a random database name and drops it
afterwards. The TCK deliberately bypasses the DS components — it wires
`MongoResourceFactory` directly.

### OSGi liveness integration (`org.eclipse.fennec.persistence.test`)

`MongoLivenessTest` runs inside the OSGi container (bndrun) and verifies the component
architecture end to end, complementing the plain-JUnit `LivenessGateTest` /
`PersistenceLivenessRuntimeComponentTest` in the core module:

- **Unreachable server** (no docker needed): a client configuration pointing at a dead
  port registers neither `MongoClient` nor the liveness `Condition`, while the
  `PersistenceLivenessRuntime` DTO shows the gate DOWN with probe count and failure
  message — presence indicates functionality, absence is diagnosable.
- **Full lifecycle** (docker-CLI container, skipped when unavailable): client,
  cascaded `MongoDatabase` and `Condition` appear once the server is reachable,
  disappear when the container is killed (gate + DS cascade), and return when a new
  container comes up on the same address.

## Current limitations and in-flux areas

- **No `mongodb://` OSGi whiteboard yet** — resource factories are wired manually; the
  DS chain ends at the `MongoDatabase` service.
- **Cross-document reference rewrite is root-only** (emf.codec#50 workaround above).
- **No type discriminator on decode** — heterogeneous collections are not supported;
  every decode needs the collection's EClass (by option or name lookup).
- **No query translation / projections / time-series support yet** — planned per the
  [working document](discussion-mongo-persistence.md); `load()` without an id reads
  the whole collection (optionally paginated).
- **Per-database probing** (databases with differing auth) is an open question of the
  liveness concept; gating is currently client-level only.
