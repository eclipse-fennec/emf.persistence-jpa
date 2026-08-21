# Eclipse Fennec Persistence JPA

An OSGi-based persistence framework for [EMF](https://eclipse.dev/modeling/emf/) (Eclipse Modeling Framework) with two backends: relational databases via [Jakarta Persistence](https://jakarta.ee/specifications/persistence/) (JPA) / [EclipseLink](https://eclipse.dev/eclipselink/), and [MongoDB](https://www.mongodb.com/).

On the relational path it maps ECore metamodels (EClass, EAttribute, EReference) to JPA entities through a processor-based transformation pipeline, allowing EObjects to be persisted without writing JPA entity classes. On the document path EObjects are (de)serialized to BSON documents through a codec bridge.

## Key Features

- **Automatic ORM mapping** from Ecore models -- no hand-written JPA entities required
- **Custom ORM mapping** via EORM metadata for existing database schemas
- **Dynamic JPA entities** at runtime using EclipseLink's Dynamic Entity API
- **MongoDB backend** with liveness-gated connection services and codec-based BSON mapping
- **All relationship types**: OneToOne, OneToMany, ManyToOne, ManyToMany (containment and non-containment)
- **Inheritance support** (SINGLE_TABLE strategy)
- **Type conversion** for common Java types (java.time.*, UUID, BigDecimal, BigInteger, arrays, etc.)
- **EMF Resource integration** via `jpa://` and `mongodb://` URI schemes and proxy-based lazy loading
- **Backend-neutral query model** -- one canonical expression IR translated to JPQL or MongoDB filters/pipelines; queries run natively or are refused with diagnostics
- **Query-backed derived references** -- OCL derivation annotations translated to backend queries
- **PushStream integration** for streaming query results
- **Connection liveness** -- backend connection services are only registered while they actually work
- **OSGi Declarative Services** for configuration and lifecycle management
- **Reverse engineering** of Ecore models from existing database schemas

## Module Overview

| Module | Role |
|--------|------|
| `org.eclipse.fennec.persistence` | Core API: `PersistenceResource`, `Options`, `ConverterService`, type converters, connection-liveness core (gate, runtime) |
| `org.eclipse.fennec.persistence.orm` | EORM metadata model (`eorm.ecore`) + processors (Entity, Basic, OneToMany, ManyToOne, etc.) |
| `org.eclipse.fennec.persistence.eclipselink` | EclipseLink JPA provider: dynamic type generation, descriptors, `JPAUnit` lifecycle, the `jpa://` resource whiteboard, the gated `DataSource` factory |
| `org.eclipse.fennec.persistence.mongo` | MongoDB backend: liveness-gated client/database services, `mongodb://` resources, codec-based BSON mapping |
| `org.eclipse.fennec.persistence.repository` | Repository facade: `ReadRepository`/`WriteRepository`/`Repository` model, prepared queries, the backend-neutral SPI |
| `org.eclipse.fennec.persistence.repository.jpa` | Repository flavour over a JPA persistence unit (`fennec.repository.jpa`) |
| `org.eclipse.fennec.persistence.repository.mongo` | Repository flavour over a MongoDB database (`fennec.repository.mongo`) |
| `org.eclipse.fennec.persistence.ecore` | `DatabaseEcoreParser` -- reverse-engineers Ecore models from database schemas |
| `org.eclipse.fennec.persistence.query` | Backend-neutral query SPI: the canonical query model translated per backend via `QueryProcessor` |
| `org.eclipse.fennec.persistence.query.derived` | Query-backed derived references: OCL derivation annotations resolved through backend queries |
| `org.eclipse.fennec.persistence.pushstreams` | OSGi PushStream integration for persistence results |
| `org.eclipse.fennec.expression.model` | Backend-neutral expression-tree IR for predicates over EMF features |
| `org.eclipse.fennec.query.model` | Query envelope around the expression model: root type, projection, ordering, paging |
| `org.eclipse.fennec.command.model` | Write commands (insert/update/delete) for the unified persistence layer |
| `org.eclipse.fennec.stream.model` | Change-stream metamodel (ChangeSet, ChangeEntry, DeltaKind) |
| `org.eclipse.fennec.expression.ocl` | Bidirectional bridge between OCL expressions and the expression model |
| `org.eclipse.fennec.persistence.tck` | Backend-agnostic compatibility test kit, run against both the JPA and the Mongo backend |
| `org.eclipse.fennec.persistence.test` | OSGi integration tests (JUnit 5, H2 database) |
| `org.eclipse.fennec.persistence.workspace.library` | bnd library template for external workspace consumption |
| `org.eclipse.fennec.persistence.bom` | Bill of Materials for downstream consumers |

## Branches & releases

* `snapshot` is the active development branch. PRs land here first; every
  push publishes `-SNAPSHOT` artifacts to
  [Sonatype Central snapshots](https://central.sonatype.com/repository/maven-snapshots/org/eclipse/fennec/persistence/).
* `main` always holds the latest released version. Released artifacts are
  available on [Maven Central](https://repo1.maven.org/maven2/org/eclipse/fennec/persistence/)
  under `org.eclipse.fennec.persistence:*`.

See [docs/ci.md](docs/ci.md) for the full CI / publishing pipeline.

## Consuming from a bnd workspace

The bundle `org.eclipse.fennec.persistence.workspace.library` is a
[bnd workspace library](https://bnd.bndtools.org/instructions/library.html)
(`bnd.library=fennecPersistence`). Two lines make the whole stack available
in another bnd workspace:

1. Add the library bundle to your workspace's Maven index (e.g.
   `cnf/central.mvn`; snapshot versions additionally need a repository
   pointing at Sonatype Central snapshots):

   ```
   org.eclipse.fennec.persistence:org.eclipse.fennec.persistence.workspace.library:<version>
   ```

2. Enable the library in `cnf/build.bnd`:

   ```properties
   -library: fennecPersistence
   ```

Enabling the library registers a read-only `MavenBndRepository` named
"Eclipse Fennec Persistence" whose index carries the full dependency closure:
the persistence bundles (core, orm, eclipselink, mongo, query stack, model
bundles, OCL bridge), EclipseLink and `jakarta.persistence-api`, the MongoDB
driver, the fennec codec, emf.osgi metadata, H2/PostgreSQL drivers with the
daanse `DataSource` factories, and the supporting EMF/OSGi runtime bundles.
After a repository refresh they can be used on any `-buildpath`/`-runbundles`
like local artifacts.

## Quick Start

### Prerequisites

- Java 21+
- OSGi runtime with Declarative Services (e.g., Apache Felix, Eclipse Equinox)
- An Ecore model (`.ecore` file)

### 1. Define your Ecore model

Create an Ecore model with EClasses, EAttributes, and EReferences. Mark one EAttribute per EClass as `iD=true` for the primary key.

### 2. Configure the persistence unit

Create an OSGi factory configuration for `fennec.jpa.EMPersistenceUnit`:

```properties
fennec.jpa.persistenceUnitName=my-pu
fennec.jpa.ext.eclipselink.ddl-generation=create-or-extend-tables
```

### 3. Provide a DataSource

Register a `javax.sql.DataSource` as OSGi service. The configurator binds to it via the `fennec.jpa.dataSource` reference.

### 4. Use the EntityManagerFactory

Once activated, the configurator registers an `EntityManagerFactory` as OSGi service. Use it directly or via `JPAResource`:

```java
@Reference(target = "(osgi.unit.name=my-pu)")
EntityManagerFactory emf;

// Via JPAResource (EMF-style)
JPAResourceImpl resource = new JPAResourceImpl(
    URI.createURI("jpa://my-pu/Person"), emf);
resource.load(null);       // loads all Person entities
resource.save(null);       // persists/merges contents
resource.delete(null);     // removes contents from DB
```

## Build

```bash
./gradlew build                    # Build all modules (excludes @Tag("perf") tests)
./gradlew perfTest                 # Run performance tests only
./gradlew :MODULE_NAME:test        # Run tests for a single module
./gradlew codeCoverageReport       # Generate JaCoCo coverage (XML + HTML)
```

## Architecture

The relational path:

```
Ecore Model (.ecore)
       |
       v
 EORM Processor Pipeline
 (EntityProcessor -> BasicProcessor -> OneToManyProcessor -> ...)
       |
       v
 EORM Metadata (eorm.ecore)
       |
       v
 EDynamicTypeGenerator / EDynamicTypeBuilder
 (Creates EclipseLink descriptors, mappings, dynamic types at runtime)
       |
       v
 EclipseLink EntityManagerFactory
 (JPA operations: persist, find, merge, remove)
       |
       v
 Relational Database
```

The framework uses EclipseLink's Dynamic Entity API to create JPA entity types at runtime from Ecore metadata, avoiding the need for compile-time entity classes or bytecode weaving.

The MongoDB path replaces the lower half of the diagram: EObjects are (de)serialized to BSON documents through a codec bridge and stored via liveness-gated `MongoClient`/`MongoDatabase` services -- see the [MongoDB Backend Architecture](docs/mongo-architecture.md).

Queries are expressed once against the backend-neutral expression IR and translated per backend (JPQL for EclipseLink, filter documents or aggregation pipelines for MongoDB). A query either runs natively on the backend or is refused with diagnostics -- there is no silent in-memory post-filtering.

## Documentation

The user documentation is published at
<https://eclipse-fennec.github.io/emf.persistence-jpa/snapshot/> (built from `docs/`
via the VitePress site in `docs-site/`).

| Document | Purpose |
|----------|---------|
| [Overview](docs/overview.md) | What the framework does, modules, where to go next |
| [Getting Started](docs/getting-started.md) | Full walkthrough: Ecore model, bootstrap (OSGi + Non-OSGi), CRUD via `jpa://` Resource, lazy-loading semantics |
| [JPA User Guide](docs/jpa-user-guide.md) | Day-to-day work with `jpa://` resources, options, eorm mapping semantics, converters, unit lifecycle |
| [MongoDB User Guide](docs/mongo-user-guide.md) | The MongoDB backend: configuration, `mongodb://` resources, codec-based BSON mapping |
| [Repository User Guide](docs/repository-user-guide.md) | The primary user-facing service: repository interfaces, configuration per flavour, prepared queries, prototype-scope contract |
| [Query User Guide](docs/query-user-guide.md) | Building and executing backend-neutral queries: `Expressions`, `QueryBuilder`, maps, geo, capability matrix, write commands |
| [Configuration Reference](docs/configuration-reference.md) | Every `fennec.jpa.*` property, forwarded EclipseLink keys, liveness keys, load/save `Options` |
| [JPA & OSGi Architecture](docs/osgi-architecture.md) | Persistence units as services, the `jpa://` whiteboard, lazy factory lifecycle (`emfIdleTimeout`) |
| [MongoDB Backend Architecture](docs/mongo-architecture.md) | Mongo component chain, liveness gating, resource pipeline, BSON codec bridge |
| [Connection Liveness](docs/concept-connection-liveness.md) | "Registered means working" — liveness-gated connection services |
| [Development Guide](docs/development-guide.md) | Architecture details, session continuity (internal) |
| [Review](docs/REVIEW.md) | Structured review: criteria, findings, work packages (internal) |
| [SECURITY.md](SECURITY.md) | Vulnerability reporting, security considerations |

## License

[Eclipse Public License 2.0](LICENSE)

## Contributors

* **Mark Hoffmann** @ [Data In Motion](https://www.datainmotion.de)
* **Juergen Albert** @ [Data In Motion](https://www.datainmotion.de)

Part of the [Eclipse Fennec](https://projects.eclipse.org/projects/modeling.fennec) project.
