# Repository User Guide

The repository is the user-facing facade over the persistence layer: a configured OSGi
service bound to **one** backend location that takes care of all URI handling and
`Resource` lifecycle, so consuming code only ever touches `EObject`s, canonical queries
and prepared queries. It is the successor of the Gecko `EMFRepository`, rebuilt on the
unified persistence layer — same job, new mechanics. If you would rather work with the
EMF `Resource` API directly, see the [JPA](jpa-user-guide.md) and
[MongoDB](mongo-user-guide.md) user guides; everything the repository does can also be
done by hand.

## Contents

1. [The interfaces](#the-interfaces)
2. [Configuring a repository](#configuring-a-repository)
3. [Consuming a repository — prototype scope](#consuming-a-repository--prototype-scope)
4. [Reading and writing](#reading-and-writing)
5. [Queries and prepared queries](#queries-and-prepared-queries)
6. [Commands](#commands)
7. [URI mechanics under the hood](#uri-mechanics-under-the-hood)
8. [Building your own flavour (SPI)](#building-your-own-flavour-spi)
9. [Configuration reference](#configuration-reference)

## The interfaces

All interfaces live in `org.eclipse.fennec.persistence.repository.api` (generated from
`repository-api.ecore`):

| Interface | Role |
|-----------|------|
| `RepositoryService` | Base: identity (`id()`, `baseUri()`), `capabilities()`, lifecycle, and the URI/resource machinery (`createUri`, `createProxy`, `proxify`, `attach`/`detach`, `getResourceSet`/`createResourceSet`) |
| `ReadRepository` | Keyed reads (`getEObject`), enumeration (`getAllEObjects`), `count`/`exist`, `reload`, canonical query execution (`find`) and `prepare` |
| `WriteRepository` | `save`/`saveAll`, `delete`, canonical command execution (`execute`) |
| `Repository` | `ReadRepository` + `WriteRepository` — the full facade |
| `PreparedQuery` | A validated query handle executed with just its parameter bindings |

A repository configured with `readOnly=true` registers **only** `RepositoryService` and
`ReadRepository` — the write interfaces are withheld from the service registry entirely
instead of failing at call time. Read-only exposure is a deployment decision, not a
wrapper.

`capabilities()` answers the effective `PersistenceCapabilities` of the bound backend —
the same contract as `PersistenceResource.capabilities()`.

## Configuring a repository

One factory configuration = one repository, bound to one backend location. The flavour
bundles resolve the backend through a standard DS reference target, so the repository
appears exactly when its backend is available and disappears with it (including
[connection-liveness](concept-connection-liveness.md) gating on the Mongo side — the
`MongoDatabase` service is already gated, the repository inherits that through the DS
cascade).

**JPA** (`org.eclipse.fennec.persistence.repository.jpa`, factory PID
`fennec.repository.jpa`) — binds a `JPAUnit` and derives `jpa://<unitName>`:

```properties
# fennec.repository.jpa~shop.cfg
repositoryId=shop
unit.target=(osgi.unit.name=shop)
readOnly=false
```

**MongoDB** (`org.eclipse.fennec.persistence.repository.mongo`, factory PID
`fennec.repository.mongo`) — binds a `MongoDatabase` and derives `mongodb://<alias>`:

```properties
# fennec.repository.mongo~assets.cfg
repositoryId=assets
database.target=(mongo.database.alias=assets)
```

The registered services carry these properties for targeting:

| Service property | Value |
|------------------|-------|
| `persistence.repository.id` | the configured `repositoryId` |
| `persistence.repository.baseUri` | e.g. `jpa://shop` |
| `persistence.repository.backend` | the URI scheme: `jpa`, `mongodb`, … |
| `persistence.repository.readOnly` | whether the write side is withheld |

An invalid configuration never yields a service: a `unit.target`/`database.target`
matching nothing keeps the component unsatisfied, a missing `repositoryId` is refused at
activation.

## Consuming a repository — prototype scope

A repository instance owns a `ResourceSet`, and EMF ResourceSets are **not thread-safe**.
The services are therefore registered with prototype scope: every consumer gets its own
instance (own ResourceSet, shared backend), and a released instance is disposed.

```java
@Reference(target = "(persistence.repository.id=shop)")
private Repository repository;   // this component's own instance
```

Plain DS injection as above gives each consuming component its own instance. If one
bundle needs several independent instances (one per worker thread, for example), use
`ServiceObjects`:

```java
ServiceObjects<Repository> so = context.getServiceObjects(reference);
Repository mine = so.getService();   // fresh instance, own ResourceSet
...
so.ungetService(mine);               // disposes it
```

## Reading and writing

The repository derives every URI itself — you work with `EClass` + id:

```java
// write
EObject person = factory.createPerson();
person.setId("p1");
repository.save(person);                         // upsert, exactly this object

// keyed read — one backend find
EObject loaded = repository.getEObject(personClass, "p1");

// existence and cardinality without loading
boolean there = repository.exist(personClass, "p1");
long count    = repository.count(personClass);

// enumeration — a lazy stream, close it (it holds a backend cursor)
try (Stream<EObject> all = repository.getAllEObjects(personClass)) {
    all.forEach(...);
}

// discard unsaved local changes — state is re-read in place
repository.reload(person);

// delete by object or by URI
repository.delete(person);
```

Two contracts worth knowing, because the underlying resource layer works
per-*collection* (one `Resource` per type, objects identified by URI fragment — see
below):

- **`save(object)` writes exactly the given object.** Other objects that happen to share
  its loaded collection resource are not touched; the repository isolates the object for
  the save and restores its attachment afterwards. `saveAll` does the same with one
  backend save per type.
- **`delete(object)` deletes exactly the given object** — never the rest of the loaded
  resource.

Per-call option maps can be passed to every operation; configured defaults (if any) are
merged underneath.

## Queries and prepared queries

`find` executes a canonical [query-model](../org.eclipse.fennec.query.model/) query
against the bound backend. The root type comes from `Query.from`; capability violations
are refused with a diagnostic — never silently post-filtered:

```java
Query byAge = QueryBuilder.from(personClass)
        .where(Expressions.path(ageAttribute).ge(Expressions.param("min")))
        .build();

try (QueryResult result = repository.find(byAge, Map.of("min", 18), null)) {
    result.objects().forEach(...);
}
```

A **prepared query** is the prepared-statement analogue: validated once against the
backend's `QueryProcessor` at `prepare` time (fail early, not at first execution),
executed with just the parameter bindings:

```java
PreparedQuery adults = repository.prepare(byAge);
try (QueryResult result = adults.execute(Map.of("min", 18))) { ... }
```

Prepared queries also come into existence by name from the backend's saved-query catalog
(`QueryBuilder.named(...)` persists on first execution): `repository.find(name, params,
options)` and `repository.prepare(name)`. One current limitation: the catalog has no
load-back API, so the repository must know the query's root type — either because the
saving query was executed through this repository instance, or via the per-call option
`RepositoryConstants.OPTION_QUERY_ROOT` carrying the root `EClass`.

## Commands

`execute` runs canonical write commands (insert, delete-by-selector, update-by-selector)
through the backend's `CommandResource`, routed by the command's target type:

```java
DeleteCommand cleanup = CommandFactory.eINSTANCE.createDeleteCommand();
cleanup.setSelector(QueryBuilder.from(personClass)
        .where(Expressions.path(ageAttribute).lt(0)).build());
long removed = repository.execute(cleanup);
```

Unsupported command features are refused per the backend's declared
`CommandCapabilities`.

## URI mechanics under the hood

The persistence layer keeps **one `Resource` per type** (table/collection); a single
object is addressed by the URI *fragment*, which carries the id
([composite ids](unified-persistence/composite-identity.md) use the canonical
`k1=v1,k2=v2` fragment form):

```
<baseUri>/<EClassName>            the collection resource
<baseUri>/<EClassName>#<id>       one object
```

This is a deliberate departure from the old one-resource-per-object model: the
collection resource is the identity anchor — keyed loads dedup against it, and
cross-references resolve through it. The repository hides the consequences (see the
save/delete contracts above) and exposes the URI arithmetic for interop:
`createUri(object)`, `createUri(eClass, id)`, `createProxy(eClass, id)` for wiring
references without loading, `proxify(object)` for turning loaded objects back into
proxies, and `attach`/`detach` for resource membership without saving.

## Building your own flavour (SPI)

The exported `org.eclipse.fennec.persistence.repository.spi` package carries the entire
backend-neutral implementation. A new flavour (the Lucene one is tracked as
[emf.search#42](https://github.com/eclipse-fennec/emf.search/issues/42)) is one factory
component of ~100 lines:

1. extend `AbstractRepositoryComponent`,
2. bind the backend's lifecycle service (`JPAUnit`, `MongoDatabase`, `IndexUnit`, …) via
   a config-targetable reference and derive the base URI from its identifying property,
3. implement `createRepository()` with an anonymous `AbstractRepository` subclass fed by
   a `ResourceSetFactory` supplier and the backend's optional `QueryProcessor`,
4. call `register(...)` on activation, `unregister()` on deactivation.

`JPARepositoryComponent` and `MongoRepositoryComponent` are the reference
implementations.

## Configuration reference

Factory PIDs `fennec.repository.jpa` / `fennec.repository.mongo`:

| Key | Type | Default | Purpose |
|-----|------|---------|---------|
| `repositoryId` | String | — (required) | Stable id, published as `persistence.repository.id` |
| `readOnly` | boolean | `false` | Withhold `WriteRepository`/`Repository` from the registry |
| `unit.target` | filter | — | JPA only: selects the `JPAUnit`, e.g. `(osgi.unit.name=shop)` |
| `database.target` | filter | — | Mongo only: selects the `MongoDatabase`, e.g. `(mongo.database.alias=assets)` |
