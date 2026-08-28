# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Eclipse Fennec Persistence — an OSGi-based persistence framework that stores EMF (Eclipse Modeling
Framework) models. It maps ECore metamodels (EClass, EAttribute, EReference) to a store through a
processor-based transformation pipeline.

Despite the repository name it serves **two backends**: JPA/EclipseLink (relational, tested against
h2, MariaDB and PostgreSQL) and MongoDB (tested against mongo, FerretDB and DocumentDB). Both are
driven by one backend-neutral query IR and declare what they serve through a capability model — a
query using an unserved feature is refused with a `Diagnostic`, never silently post-filtered.

**Java version**: 21 (source and target)
**Build tool**: Gradle with bnd workspace plugin (OSGi)
**License**: EPL-2.0

## Build Commands

```bash
./gradlew build                    # Build all modules (excludes @Tag("perf") tests)
./gradlew perfTest                 # Run performance tests only (@Tag("perf"), ignoreFailures=true)
./gradlew :MODULE_NAME:test        # Run tests for a single module
./gradlew codeCoverageReport       # Generate JaCoCo coverage (XML + HTML)
```

OSGi integration tests in `org.eclipse.fennec.persistence.test` run via bndrun (`test.bndrun`) and produce JaCoCo exec files at `generated/tmp/testOSGi/generated/jacoco.exec`.

## Module Architecture

| Module | Role |
|--------|------|
| `org.eclipse.fennec.persistence` | Core persistence API: `PersistenceResource` interface, `Options`, `ConverterService`, type converters (UUID, BigDecimal, Instant, etc.), `EMFHelper` |
| `org.eclipse.fennec.persistence.orm` | Ecore-based ORM metadata model (`eorm.ecore`, `epersistence.ecore`) + processors that transform EClass→Entity, EAttribute→Basic, EReference→relationships |
| `org.eclipse.fennec.persistence.eclipselink` | EclipseLink JPA provider: `EPersistenceContext`, `EMFEntityManagerProvider`, EclipseLink descriptors (`EClassDescriptor`), object builders, OSGi classloader |
| `org.eclipse.fennec.persistence.ecore` | `DatabaseEcoreParser` — reverse-engineers Ecore models from database schemas via JDBC metadata |
| `org.eclipse.fennec.persistence.test` | OSGi integration tests using JUnit 5 + `@InjectService`, H2 database, test ecore/eorm fixtures in `data/` |
| `org.eclipse.fennec.persistence.workspace.library` | bnd library template for external workspace consumption |
| `org.eclipse.fennec.persistence.bom` | Bill of Materials — re-exports the workspace library buildpath for downstream consumers |
| `org.eclipse.fennec.persistence.mongo` | MongoDB backend: `MongoResourceImpl`, the codec bridge, `MongoQueryProcessor`, per-flavor capabilities (mongo / FerretDB / DocumentDB) |
| `org.eclipse.fennec.persistence.query` | Backend-neutral query SPI: `QueryProcessor`, `ExpressionAnalyzer`, `QueryValidator`, the in-memory reference engine, `query-api.ecore` |
| `org.eclipse.fennec.persistence.capabilities` | The closed feature vocabulary (`QueryFeature`, `CommandFeature`, `StoreFeature`) a backend declares against — enums only, so declaring costs no dependency on the query model |
| `org.eclipse.fennec.persistence.repository` | User-facing repository facade: URI handling, resource lifecycle, read/write data access (`repository-api.ecore`), with `.jpa` and `.mongo` bindings |
| `org.eclipse.fennec.persistence.query.derived` | Query-backed derived references via OCL derivation annotations |
| `org.eclipse.fennec.persistence.pushstreams` | OSGi PushStream support for persistence resources |
| `org.eclipse.fennec.persistence.tck` | The conformance suite both backends run: `AbstractPersistenceTCK` plus the per-backend and per-flavor drivers. Capability-gated — an unserved feature skips rather than fails |
| `org.eclipse.fennec.query.model` | The canonical query envelope (`query.ecore`: `Query`, `Expand`, `GroupByStage`, …) and its fluent builders |
| `org.eclipse.fennec.expression.model` | The expression IR the query envelope is built from (`expression.ecore`) |
| `org.eclipse.fennec.expression.ocl` | Bidirectional bridge between the expression model and OCL |
| `org.eclipse.fennec.command.model` | Write commands — Insert, Delete-by-selector, Update-by-selector with a ChangeSet template |
| `org.eclipse.fennec.stream.model` | Change-stream metamodel (`ChangeSet`, `ChangeEntry`, `DeltaKind`) |

## Key Architectural Patterns

- **Processor pattern**: `Processor<T,S>` interface for source→target transformations. `ProcessorFactory` creates chains: `EntityProcessor` → `BasicProcessor` / `OneToManyProcessor` / `ManyToOneProcessor` etc. Each processor writes to a `MappingContext`.
- **OSGi Declarative Services**: Components use `@Component`, `@Reference`, `@ObjectClassDefinition` for configuration. Tests use `@InjectService` and `@WithFactoryConfiguration`.
- **Generated code**: `src-gen/` directories (especially in `org.eclipse.fennec.persistence.orm`) contain EMF-generated Java from `.genmodel` files. Do not edit these manually.

## Configuration

- `cnf/build.bnd`: the whole workspace bnd config in one file — `-library` (fennec, fennecTest,
  fennecJacoco, fennecEMF, fennecEMFModels, fennecCodec), the `-plugin.*` repositories,
  `-groupid`, `base-version` and `javac.source/target`. There is **no `cnf/ext/`**; the libraries
  themselves are expanded into `cnf/cache/<bnd-version>/` from `org.eclipse.fennec.bnd.library`.
- `cnf/central.mvn`: Maven Central dependency index (versions of EclipseLink, the Mongo driver, …)
- `cnf/local/index.xml`, `cnf/release/index.xml`: the local and release repository indexes

## Testing

- **Framework**: JUnit 5.14 + Mockito 5.21 + AssertJ 3.27.7
- **OSGi tests**: Run in OSGi container via `org.osgi.test.junit5` with service injection
- **Test data**: H2 in-memory database, ecore models, eorm mappings, SQL fixtures in `org.eclipse.fennec.persistence.test/data/`
- **Performance tests**: Tagged `@Tag("perf")`, excluded from normal build, run via `./gradlew perfTest`
- **Conformance suite**: `org.eclipse.fennec.persistence.tck` runs the same `AbstractPersistenceTCK`
  against every backend. Tests are **capability-gated** (`@RequiresCapabilities`): a feature the
  backend does not declare is skipped, not failed — so a rising skip count is a gap, not a pass.
- **Flavor matrix**: `./gradlew build` covers only h2 and mongo. The other flavors need containers
  and a flag, and they are where defects actually surface — PostgreSQL in particular has repeatedly
  been the only one to fail on untyped parameters and unordered results:

```bash
./gradlew :org.eclipse.fennec.persistence.tck:test -Djpa.test.flavor=postgres -Djpa.container.cli=docker
./gradlew :org.eclipse.fennec.persistence.tck:test -Djpa.test.flavor=mariadb  -Djpa.container.cli=docker
./gradlew :org.eclipse.fennec.persistence.tck:test -Dmongo.test.flavor=ferretdb -Djpa.container.cli=docker
```

  An unreachable backend fails the run rather than reporting a capability; `-Djpa.test.optional=true`
  skips instead. If podman starts reporting `exceeded num_locks`, prune containers and volumes —
  many flavor runs exhaust its lock pool and every test then fails for that reason alone.
