# Eclipse Fennec Persistence JPA

An OSGi-based persistence framework bridging [EMF](https://eclipse.dev/modeling/emf/) (Eclipse Modeling Framework) with [Jakarta Persistence](https://jakarta.ee/specifications/persistence/) (JPA) via [EclipseLink](https://eclipse.dev/eclipselink/).

It maps ECore metamodels (EClass, EAttribute, EReference) to JPA entities through a processor-based transformation pipeline, allowing EObjects to be persisted to relational databases without writing JPA entity classes.

## Key Features

- **Automatic ORM mapping** from Ecore models -- no hand-written JPA entities required
- **Custom ORM mapping** via EORM metadata for existing database schemas
- **Dynamic JPA entities** at runtime using EclipseLink's Dynamic Entity API
- **All relationship types**: OneToOne, OneToMany, ManyToOne, ManyToMany (containment and non-containment)
- **Inheritance support** (SINGLE_TABLE strategy)
- **Type conversion** for common Java types (java.time.*, UUID, BigDecimal, BigInteger, arrays, etc.)
- **EMF Resource integration** via `jpa://` URI scheme and proxy-based lazy loading
- **OSGi Declarative Services** for configuration and lifecycle management
- **Reverse engineering** of Ecore models from existing database schemas

## Module Overview

| Module | Role |
|--------|------|
| `org.eclipse.fennec.persistence` | Core API: `PersistenceEngine`, `ConverterService`, type converters, `EMFHelper` |
| `org.eclipse.fennec.persistence.orm` | EORM metadata model (`eorm.ecore`) + processors (Entity, Basic, OneToMany, ManyToOne, etc.) |
| `org.eclipse.fennec.persistence.eclipselink` | EclipseLink JPA provider: dynamic type generation, descriptors, accessors, `JPAResource` |
| `org.eclipse.fennec.persistence.ecore` | `DatabaseEcoreParser` -- reverse-engineers Ecore models from database schemas |
| `org.eclipse.fennec.persistence.test` | OSGi integration tests (JUnit 5, H2 database) |

## Quick Start

### Prerequisites

- Java 17+
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

## Documentation

| Document | Purpose |
|----------|---------|
| [Getting Started](docs/getting-started.md) | Full walkthrough: Ecore model, bootstrap (OSGi + Non-OSGi), CRUD via `jpa://` Resource, lazy-loading semantics |
| [Configuration Reference](docs/configuration-reference.md) | Every `fennec.jpa.*` property, forwarded EclipseLink keys, load/save `Options` |
| [Development Guide](docs/development-guide.md) | Architecture details, session continuity |
| [Review](docs/REVIEW.md) | Structured review: criteria, findings, work packages |
| [SECURITY.md](SECURITY.md) | Vulnerability reporting, security considerations |

## License

[Eclipse Public License 2.0](LICENSE)

## Contributors

* **Mark Hoffmann** @ [Data In Motion](https://www.datainmotion.de)
* **Juergen Albert** @ [Data In Motion](https://www.datainmotion.de)

Part of the [Eclipse Fennec](https://projects.eclipse.org/projects/modeling.fennec) project.
