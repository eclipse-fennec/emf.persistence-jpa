# Issue #6 — Honor `eclipselink.ddl-generation=none` in `EDynamicHelper`

GitHub: <https://github.com/eclipse-fennec/emf.persistence-jpa/issues/6>

## Problem

`EntityManagerFactoryConfigurator.configure()` hardcoded
`helper.addETypes(true, true, eTypes)`, so `EDynamicHelper` always created
schemas/tables (with FK constraints) — even when the persistence unit was
configured with `eclipselink.ddl-generation=none`. This causes redundant DDL,
race conditions, and noisy "table already exists" errors in pipelines where
another component (Liquibase, CSV importer, …) owns the schema.

## Investigation

- **`createMissingTables`** — the existing property
  `fennec.jpa.ext.eclipselink.ddl-generation` is already accessible at the
  `configure()` call site. `AbstractPersistenceUnitConfigurator.createForwardedProperties()`
  strips the `fennec.jpa.ext.` prefix, and `setEMFProperties()` defaults
  `DDL_GENERATION` to `NONE` when the consumer doesn't set it. We can derive
  `createMissingTables` from this property without introducing anything new.

- **`generateFKConstraints`** — no standard EclipseLink property maps to
  `DynamicSchemaManager.createTables(generateFKConstraints)`. The closest
  (`eclipselink.ddl-generation.index-foreign-keys`) governs **indexes** on FK
  columns, not whether FK constraints are emitted. Since EclipseLink generates
  FK constraints automatically whenever it creates tables, we decided to bind
  this boolean to the same `ddl-generation` check rather than add a separate
  property. Independent control can be revisited later if a real need appears.

## Behavior

| `eclipselink.ddl-generation`           | `createMissingTables` | `generateFKConstraints` |
|----------------------------------------|-----------------------|-------------------------|
| missing (default `NONE` from configurator) | `false`           | `false`                 |
| `none`                                 | `false`               | `false`                 |
| `create-tables`                        | `true`                | `true`                  |
| `create-or-extend-tables`              | `true`                | `true`                  |
| `drop-and-create-tables`               | `true`                | `true`                  |

Note: this is a behavior change vs. the previous hardcoded `true`. Consumers
that relied on tables being auto-created without setting `ddl-generation`
explicitly must now set it (the four `TestAnnotations` factory configs and the
two `NonOsgi*` test bases already do).

## Changes

- `org.eclipse.fennec.persistence.eclipselink/src/.../spi/EntityManagerFactoryConfigurator.java`
  — derive `createMissingTables` from `properties.get(DDL_GENERATION)` and pass
  it for both `EDynamicHelper.addETypes` parameters.
- `org.eclipse.fennec.persistence.eclipselink/test/.../spi/EntityManagerFactoryConfiguratorDdlTest.java`
  — new regression test: bootstraps the configurator with
  `eclipselink.ddl-generation=none` against an in-memory H2 instance and
  asserts that the entity's table is **not** created. Confirmed failing
  before the fix.

## Open

- FK-only control (creating tables but skipping FK constraints) is not
  exposed. If a use case appears, introduce a fennec-private key
  (e.g. `fennec.jpa.generateFKConstraints`) plumbed via the Builder.
