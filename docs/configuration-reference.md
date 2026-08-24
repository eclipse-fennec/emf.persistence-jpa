# Configuration Reference

Every knob exposed by Fennec Persistence JPA at a glance. Grouped by layer:

1. [OSGi persistence-unit properties](#osgi-persistence-unit-properties)
2. [Mapping producers](#mapping-producers)
3. [Forwarded EclipseLink properties](#forwarded-eclipselink-properties)
4. [Resource load and save options](#resource-load-and-save-options)
5. [Non-OSGi bootstrap properties](#non-osgi-bootstrap-properties)
6. [Repository facade](#repository-facade)

## OSGi persistence-unit properties

Used as keys in the factory configuration for the two PIDs:

- `fennec.jpa.PersistenceUnit` -- loads a persistence unit from an XMI file or a mapping file
- `fennec.jpa.EMPersistenceUnit` -- loads from an injected `EntityMappings` OSGi service

**Every key in this table carries the `fennec.jpa.` prefix** -- both configurators
declare it as their object class definition prefix. An unprefixed
`persistenceUnitName` is not read at all and activation fails with
`ConfigurationException: No persistence unit name was provided`.

| Key | Type | Default | Purpose |
|-----|------|---------|---------|
| `fennec.jpa.persistenceUnitName` | String | -- | Logical unit name, exposed as the `osgi.unit.name` service property and used as the `jpa://puName/...` URI authority |
| `fennec.jpa.persistenceUnitFile` | String | -- | `PersistenceUnitConfigurator` only: URI of a serialised PersistenceUnit model |
| `fennec.jpa.mappingFile` | String | -- | `PersistenceUnitConfigurator` only: URI of an EntityMappings file (required when no `persistenceUnitFile`) |
| `fennec.jpa.batchWriting` | String | -- | JDBC batch mode -- one of `JDBC`, `BUFFERED`, `OracleJDBC`, or `NONE`. Empty/unset disables explicit batch configuration |
| `fennec.jpa.batchSize` | int | 0 | Statements per batch. `0` leaves EclipseLink's default |
| `fennec.jpa.emfIdleTimeout` | long | 60 | Seconds without any use after which the lazily-built EclipseLink factory is closed (releasing caches and connections); the next use rebuilds it. `0` = close immediately after the last use, `-1` = keep open until the configuration is deleted. See [OSGi architecture](osgi-architecture.md) |

`batchWriting`, `batchSize` and `emfIdleTimeout` are additionally accepted
unprefixed, because they are read from the raw property map rather than through the
typed configuration. Prefer the prefixed form; it is what the metatype describes.

Each configuration registers two services carrying `osgi.unit.name`: the narrow
`JPAUnit` capability (used by the `jpa://` whiteboard resource factory) and the real
`EntityManagerFactory` (via an OSGi `ServiceFactory` -- built lazily on first
`getService`, released on `ungetService`). Details and lifecycle:
[OSGi architecture](osgi-architecture.md).

`batchWriting` and `batchSize` are forwarded to EclipseLink as
`eclipselink.jdbc.batch-writing` and `eclipselink.jdbc.batch-writing.size`.
An explicit `fennec.jpa.ext.eclipselink.jdbc.batch-writing*` override
takes precedence.

### Service references

Both configurators resolve three standard OSGi references, each with a
filter target that can be constrained per factory instance:

| Reference | Interface | Default name |
|-----------|-----------|--------------|
| `fennec.jpa.dataSource` | `javax.sql.DataSource` | -- |
| `fennec.jpa.converter` | `ConverterService` | the default service |
| `fennec.jpa.mapping` | `EntityMappings` | `EMPersistenceUnit` only |
| `fennec.jpa.model` | `EPackage` | `PersistenceUnit` only |

### Example configuration

```properties
# .cfg file -- factory PID fennec.jpa.EMPersistenceUnit, filename suffix picks the instance
fennec.jpa.persistenceUnitName=library
fennec.jpa.batchWriting=JDBC
fennec.jpa.batchSize=500
fennec.jpa.dataSource.target=(dataSourceName=libraryDs)
fennec.jpa.converter.target=(component.name=org.eclipse.fennec.persistence.converter.DefaultConverterService)
```

## Mapping producers

The `EntityMappings` service that `fennec.jpa.EMPersistenceUnit` binds is produced by one
of two factory components. **Both take their keys under the `fennec.jpa.eorm.` prefix**,
which is *not* the `fennec.jpa.` prefix of the persistence unit -- an unprefixed key is
silently ignored.

### `fennec.jpa.EORMMappingService` -- derive the mapping from an EPackage

| Key | Type | Default | Purpose |
|-----|------|---------|---------|
| `fennec.jpa.eorm.mappingName` | String | -- (required) | Published as the `fennec.jpa.eorm.mapping` service property; this is what the unit filters on |
| `fennec.jpa.eorm.eClasses` | String[] | -- | Names of the EClasses to map. **Omit the key to map every EClass of the EPackage.** A name that is not an EClass of that package is skipped with a log warning |
| `fennec.jpa.eorm.strict` | boolean | `false` | Take EClass/attribute names as authoritative, skip column-name guessing |
| `fennec.jpa.eorm.model.target` | filter | -- | Selects the `EPackage` service, e.g. `(emf.nsURI=http://example.org/library/1.0)` |
| `fennec.jpa.eorm.customizer.target` | filter | -- | Selects an `EORMMappingCustomizer`. Satisfied by the built-in `EmptyMappingCustomizer` when unset -- **a filter matching nothing leaves the component unsatisfied and no mapping appears** |

Registers `EntityMappings` with `fennec.jpa.eorm.mapping=<mappingName>` and
`fennec.jpa.eorm.model=<EPackage name>`.

### `fennec.jpa.EORMLoader` -- read a pre-serialised .eorm file

| Key | Type | Default | Purpose |
|-----|------|---------|---------|
| `fennec.jpa.eorm.name` | String | -- (required) | Published as the `fennec.jpa.orm.mapping.name` service property |
| `fennec.jpa.eorm.uri` | String | -- (required) | URI of the serialised `EntityMappings` resource |
| `fennec.jpa.eorm.model.target` | filter | -- | Selects the `EPackage` service the mapping belongs to |

Registers `EntityMappings` with `fennec.jpa.orm.mapping.name=<name>`, as a prototype
service handing out a copy per consumer.

The two publish **different** service properties, so the unit's
`fennec.jpa.mapping.target` differs accordingly:

```properties
fennec.jpa.mapping.target=(fennec.jpa.eorm.mapping=library)      # EORMMappingService
fennec.jpa.mapping.target=(fennec.jpa.orm.mapping.name=library)  # EORMLoader
```

## Forwarded EclipseLink properties

Every configuration key starting with `fennec.jpa.ext.` is passed to
EclipseLink verbatim, minus the prefix. That is the escape hatch for
arbitrary `eclipselink.*` tuning without needing first-class OCD support.

### Defaults set by Fennec

`AbstractPersistenceUnitConfigurator.setEMFProperties` pre-seeds a few
EclipseLink properties so the common path works out of the box. Any key
passed explicitly wins (Fennec uses `putIfAbsent` for the overridable ones):

| EclipseLink key | Default | Overridable |
|-----------------|---------|-------------|
| `eclipselink.weaving` | `false` | no |
| `eclipselink.target-database` | `Auto` | yes -- set `fennec.jpa.ext.eclipselink.target-database=...` |
| `eclipselink.ddl-generation` | `none` | yes -- set `fennec.jpa.ext.eclipselink.ddl-generation=create-or-extend-tables` |
| `eclipselink.transaction.join-existing` / transaction type | `RESOURCE_LOCAL` | no |
| `eclipselink.throw-exceptions` | `true` | no |
| `eclipselink.jdbc.connection-pool.min` | `1` | no |
| `eclipselink.logging.level` | `WARNING` | yes |
| `eclipselink.logging.timestamp` | `false` | yes |
| `eclipselink.logging.thread` | `false` | yes |
| `eclipselink.logging.exceptions` | `true` | yes |

### Frequently useful EclipseLink keys

| Key | Effect |
|-----|--------|
| `eclipselink.ddl-generation` | `none`, `create-tables`, `drop-and-create-tables`, `create-or-extend-tables` |
| `eclipselink.logging.level` | `OFF`, `SEVERE`, `WARNING`, `INFO`, `CONFIG`, `FINE`, `FINER`, `FINEST`, `ALL` |
| `eclipselink.cache.shared.default` | `true`/`false` -- set to `false` to disable the shared L2 cache (useful for tests) |
| `eclipselink.cache.size.default` | max entries in the shared cache |
| `eclipselink.jdbc.batch-writing` | batch mode; prefer the first-class `fennec.jpa.batchWriting` property |
| `eclipselink.jdbc.batch-writing.size` | batch size; prefer `fennec.jpa.batchSize` |
| `eclipselink.flush-clear.cache` | how the UoW resets after a flush/clear |

Example:

```properties
fennec.jpa.ext.eclipselink.logging.level=FINE
fennec.jpa.ext.eclipselink.cache.shared.default=false
```

## Connection liveness

See `docs/concept-connection-liveness.md` for the concept. Connection services are only
registered while a probe verifies the connection ("presence indicates functionality");
while a gate is UP it additionally registers an `org.osgi.service.condition.Condition`
with `osgi.condition.id=fennec.liveness.<ident>`, and the always-on
`PersistenceLivenessRuntime` service exposes all gate states as DTOs.

The `liveness.*` keys are understood by the Mongo client factory
(`persistence.mongo.client`) and the gated-DataSource factory (`persistence.jdbc.gate`):

| Key | Default | Purpose |
|-----|---------|---------|
| `liveness.enabled` | `true` | `false` registers the service immediately, without probing |
| `liveness.checkInterval` | `30` | probe period in seconds while UP; `0` = no periodic re-check |
| `liveness.checkTimeout` | `5` | timeout per probe in seconds |
| `liveness.failureThreshold` | `3` | consecutive probe failures before the service is unregistered |
| `liveness.retryMin` / `liveness.retryMax` | `1` / `30` | exponential retry backoff bounds in seconds while DOWN |

The JDBC gate (factory PID `persistence.jdbc.gate`) re-registers an upstream
`DataSource` with the marker property `fennec.liveness=checked`:

```
# .cfg file -- factory PID persistence.jdbc.gate
name=mydb
dataSource.target=(dataSourceName=mydb)
liveness.checkInterval=10
```

A persistence unit opts in by targeting the marker:

```
fennec.jpa.dataSource.target=(&(fennec.liveness=checked)(name=mydb))
```

## Resource load and save options

Pass these through the `Map<?, ?> options` argument of
`Resource.load(options)` and `Resource.save(options)`. Constants are on
`org.eclipse.fennec.persistence.Options`.

| Constant | Key | Value type | Applies to | Purpose |
|----------|-----|------------|------------|---------|
| `OPTION_PAGE_SIZE` | `fennec.jpa.page-size` | Integer | `load` | Stream the result set in pages. A positive value enables `setFirstResult`/`setMaxResults` iteration; `0` / absent loads everything in one query |
| `OPTION_WRITE_CHUNK_SIZE` | `fennec.write-chunk-size` | Integer | `execute` | Chunk size for command `DELETE`/`UPDATE` by selector (issue #227). **Defaults to 1000** — unlike page size this is on unless switched off, because the failure it prevents is an OOM. An explicit `0` or less restores the unchunked behaviour |
| `OPTION_CACHE_NEW_OBJECTS` | `fennec.jpa.cache-new-objects` | Boolean | `save` | Passed to EclipseLink's UnitOfWork as `setShouldNewObjectsBeCached(value)`. Use `FALSE` to avoid populating the shared cache with freshly persisted objects during bulk inserts |
| `OPTION_TABLE_NAME` | `TABLE_NAME` | String or EClass | `load` / `save` | Override the target table name for a single call (useful when multiple tables share a schema) |
| `READ_FILTER_ECLASS` | `FILTER_ECLASS` | EClass | `load` | Restrict results to instances of the given EClass (and its subclasses) when loading from a polymorphic alias |

Example -- paginated load with cache-tuning:

```java
Map<Object, Object> opts = new HashMap<>();
opts.put(Options.OPTION_PAGE_SIZE, 1000);
resource.load(opts);
```

## Codec settings (MongoDB)

The Mongo backend stores documents through the Fennec codec and rides its
defaults; several query features depend on them (details and the full table:
`mongo-user-guide.md` → *Codec settings for MongoDB*). Overridable through
the codec configuration chain (globally, per EPackage or EClass, e.g. via
EAnnotations):

| Codec key | Default | Purpose for MongoDB |
|-----------|---------|---------------------|
| `codec.typeInclude` / `codec.typeKey` / `codec.typeStrategy` | `true` / `_type` / `URI` | Type discriminator in every document — decode and the type predicates (`isOf`, `pathAs`) depend on it; recommended to keep |
| `codec.superTypeSerialize` | `false` | Opt-in `_supertype` array: switches `isOf` to a direct supertype match (no subtype closure) |
| `codec.dateFormat` | unset | Keep unset — temporal attributes then store as native `BsonDateTime`, required by the temporal query operators |
| `codec.smartCompression` | `false` | Keep the plain default — values stored explicitly, queries see the model state |

## Non-OSGi bootstrap properties

When assembling an `EntityManagerFactory` manually (see the Getting Started
example), pass EclipseLink keys directly through the properties map that
`PersistenceProvider.createContainerEntityManagerFactory(pui, props)`
receives. Everything listed under [Forwarded EclipseLink properties](#forwarded-eclipselink-properties)
works there too -- without the `fennec.jpa.ext.` prefix, since no
forwarding layer is involved.

The test base [`NonOsgiPersistenceTestBase`](../org.eclipse.fennec.persistence.test/test/org/eclipse/fennec/persistence/test/NonOsgiPersistenceTestBase.java)
demonstrates a minimal property set:

```java
Map<String, Object> props = new HashMap<>();
props.put(PersistenceUnitProperties.DDL_GENERATION, "create-or-extend-tables");
props.put(PersistenceUnitProperties.DDL_GENERATION_MODE, "database");
props.put(PersistenceUnitProperties.JDBC_DRIVER, "org.h2.Driver");
props.put(PersistenceUnitProperties.JDBC_URL, "jdbc:h2:mem:test");
props.put(PersistenceUnitProperties.JDBC_USER, "sa");
props.put(PersistenceUnitProperties.JDBC_PASSWORD, "");
props.put(PersistenceUnitProperties.TARGET_DATABASE, "Auto");
props.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");
props.put(PersistenceUnitProperties.WEAVING, "false");
props.put(PersistenceUnitProperties.LOGGING_LEVEL, "WARNING");
props.put(PersistenceUnitProperties.CLASSLOADER, dynamicClassLoader);
```

## Repository facade

Factory PIDs `fennec.repository.jpa` and `fennec.repository.mongo` configure the
user-facing repository services — one configuration per repository, bound to a
persistence unit or Mongo database via a reference target. Keys, service properties
and consumption semantics (prototype scope, read-only registration) are documented in
the [Repository User Guide](repository-user-guide.md#configuration-reference).
