# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Eclipse Fennec Persistence JPA — an OSGi-based persistence framework bridging EMF (Eclipse Modeling Framework) with Jakarta Persistence (JPA) via EclipseLink. It maps ECore metamodels (EClass, EAttribute, EReference) to JPA entities through a processor-based transformation pipeline.

**Java version**: 17 (source and target)
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

## Key Architectural Patterns

- **Processor pattern**: `Processor<T,S>` interface for source→target transformations. `ProcessorFactory` creates chains: `EntityProcessor` → `BasicProcessor` / `OneToManyProcessor` / `ManyToOneProcessor` etc. Each processor writes to a `MappingContext`.
- **OSGi Declarative Services**: Components use `@Component`, `@Reference`, `@ObjectClassDefinition` for configuration. Tests use `@InjectService` and `@WithFactoryConfiguration`.
- **Generated code**: `src-gen/` directories (especially in `org.eclipse.fennec.persistence.orm`) contain EMF-generated Java from `.genmodel` files. Do not edit these manually.

## Configuration

- `cnf/build.bnd`: Workspace-level bnd config (libraries, repositories, version)
- `cnf/ext/libraries.bnd`: bnd library references (DIMC, JaCoCo, OSGi test, EMF)
- `cnf/ext/libraries.maven`: Maven repository configuration (Data In Motion Nexus, Sonatype)
- `cnf/central.mvn`: Maven Central dependency index

## Testing

- **Framework**: JUnit 5.14 + Mockito 5.21 + AssertJ 3.27.7
- **OSGi tests**: Run in OSGi container via `org.osgi.test.junit5` with service injection
- **Test data**: H2 in-memory database, ecore models, eorm mappings, SQL fixtures in `org.eclipse.fennec.persistence.test/data/`
- **Performance tests**: Tagged `@Tag("perf")`, excluded from normal build, run via `./gradlew perfTest`
