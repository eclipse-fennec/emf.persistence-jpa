# Overview

Eclipse Fennec Persistence JPA is an OSGi-based persistence framework that bridges
[EMF](https://eclipse.dev/modeling/emf/) (Eclipse Modeling Framework) with real
databases. Its original and most complete backend maps Ecore metamodels to
[Jakarta Persistence](https://jakarta.ee/specifications/persistence/) entities via
[EclipseLink](https://eclipse.dev/eclipselink/) — EObjects are persisted to relational
databases **without writing JPA entity classes**. A document-oriented backend for
MongoDB shares the same EMF resource contract.

## What it does

- **Automatic ORM mapping** from Ecore models — no hand-written JPA entities required
- **Custom ORM mapping** via EORM metadata for existing database schemas
- **Dynamic JPA entities** at runtime using EclipseLink's Dynamic Entity API — no
  compile-time entity classes, no bytecode weaving
- **All relationship types**: OneToOne, OneToMany, ManyToOne, ManyToMany (containment
  and non-containment), inheritance, lazy/eager fetching with batch support
- **EMF Resource integration**: persistence is a `Resource` — load, save and delete
  EObjects through `jpa://` (relational) and `mongodb://` (document) URIs, mixable in
  one ResourceSet
- **Type conversion** for common Java types (`java.time.*`, UUID, BigDecimal,
  BigInteger, arrays, …) through a pluggable `ConverterService`
- **OSGi-native lifecycle**: persistence units, data sources and Mongo clients are
  configured via Configurator/ConfigAdmin and appear as services — including
  [connection liveness](concept-connection-liveness.md): a connection service is only
  registered while the database is actually reachable
- **Reverse engineering** of Ecore models from existing database schemas via JDBC
  metadata

## Modules

| Module | Role |
|--------|------|
| `org.eclipse.fennec.persistence` | Core API: `PersistenceResource`, `Options`, `ConverterService`, type converters, connection-liveness core (gate, runtime) |
| `org.eclipse.fennec.persistence.orm` | Ecore-based ORM metadata model (`eorm.ecore`) + processors transforming EClass→Entity, EAttribute→Basic, EReference→relationships |
| `org.eclipse.fennec.persistence.eclipselink` | EclipseLink JPA provider: dynamic type generation, descriptors, `JPAUnit` lifecycle, the `jpa://` resource whiteboard, the gated `DataSource` factory |
| `org.eclipse.fennec.persistence.mongo` | MongoDB backend: liveness-gated client/database services, `mongodb://` resources, codec-based BSON mapping |
| `org.eclipse.fennec.persistence.ecore` | `DatabaseEcoreParser` — reverse-engineers Ecore models from database schemas |
| `org.eclipse.fennec.persistence.pushstreams` | OSGi PushStream integration for persistence results |
| `org.eclipse.fennec.persistence.tck` | Backend-agnostic compatibility test kit, run against both the JPA and the Mongo backend |

## How it works (relational path)

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
 (EclipseLink descriptors, mappings, dynamic types at runtime)
       |
       v
 EclipseLink EntityManagerFactory  ->  Relational Database
```

The Mongo path replaces the lower half: EObjects are (de)serialized to BSON documents
through the Fennec codec framework — see the
[MongoDB Backend Architecture](mongo-architecture.md).

## Where to go next

| You want to… | Read |
|--------------|------|
| Set the framework up end-to-end (OSGi and plain Java) | [Getting Started](getting-started.md) |
| Work with `jpa://` resources, mappings, options day-to-day | [JPA User Guide](jpa-user-guide.md) |
| Persist EMF models in MongoDB | [MongoDB User Guide](mongo-user-guide.md) |
| Look up a configuration property | [Configuration Reference](configuration-reference.md) |
| Understand persistence units, `JPAUnit` and the whiteboard | [JPA & OSGi Architecture](osgi-architecture.md) |
| Understand the Mongo backend internals | [MongoDB Backend Architecture](mongo-architecture.md) |
| Understand liveness-gated connection services | [Connection Liveness](concept-connection-liveness.md) |

## Releases

- `snapshot` is the active development branch; every push publishes `-SNAPSHOT`
  artifacts to [Sonatype Central snapshots](https://central.sonatype.com/repository/maven-snapshots/org/eclipse/fennec/persistence/jpa/).
- `main` holds the latest release, published to
  [Maven Central](https://repo1.maven.org/maven2/org/eclipse/fennec/persistence/jpa/)
  under `org.eclipse.fennec.persistence.jpa:*`.

Fennec Persistence JPA is part of the
[Eclipse Fennec](https://projects.eclipse.org/projects/modeling.fennec) project and
licensed under the [EPL-2.0](https://www.eclipse.org/legal/epl-2.0/).
