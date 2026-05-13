# Issue #6 — Honor `eclipselink.ddl-generation` in `EDynamicHelper`

GitHub: <https://github.com/eclipse-fennec/emf.persistence-jpa/issues/6>

## Problem

`EntityManagerFactoryConfigurator.configure()` hardcoded
`helper.addETypes(true, true, eTypes)`, so `EDynamicHelper` always created
schemas/tables (with FK constraints) — even when the persistence unit was
configured with `eclipselink.ddl-generation=none`. This causes redundant DDL,
race conditions, and noisy "table already exists" errors in pipelines where
another component (Liquibase, CSV importer, …) owns the schema.

## Investigation

- **`ddl-generation` mode** — the existing property
  `fennec.jpa.ext.eclipselink.ddl-generation` is already accessible at the
  `configure()` call site. `AbstractPersistenceUnitConfigurator.createForwardedProperties()`
  strips the `fennec.jpa.ext.` prefix, and `setEMFProperties()` defaults
  `DDL_GENERATION` to `NONE` when the consumer doesn't set it. We can derive
  the schema-generation action from this property without introducing anything
  new.

- **`generateFKConstraints`** — no standard EclipseLink property maps to
  `DynamicSchemaManager.createTables(generateFKConstraints)`. The closest
  (`eclipselink.ddl-generation.index-foreign-keys`) governs **indexes** on FK
  columns, not whether FK constraints are emitted. Since EclipseLink generates
  FK constraints automatically whenever it creates tables, we decided to bind
  this boolean to the same `ddl-generation` decision rather than add a separate
  property. Independent control can be revisited later if a real need appears.

- **Full set of `ddl-generation` values** — `DynamicSchemaManager` inherits
  `createDefaultTables`, `replaceDefaultTables` and `extendDefaultTables` from
  its `SchemaManager` superclass, so all four EclipseLink modes can be honored
  for dynamic types. Caveat: `DynamicSchemaManager.createTables(boolean)` uses
  a *filtered* `TableCreator` (only descriptors in the session); the inherited
  methods use the unfiltered `getDefaultTableCreator(...)`. In the fennec setup
  the EclipseLink session only ever holds dynamic descriptors, so this isn't
  a problem in practice.

## Behavior

The `eclipselink.ddl-generation` value is parsed into a new enum
`org.eclipse.fennec.persistence.eclipselink.dynamic.DdlAction` and dispatched to
the matching `DynamicSchemaManager` operation:

| `eclipselink.ddl-generation`               | `DdlAction`              | `DynamicSchemaManager` call            | FK constraints |
|--------------------------------------------|--------------------------|----------------------------------------|----------------|
| missing (default `NONE` from configurator) | `NONE`                   | (none)                                 | n/a            |
| `none`                                     | `NONE`                   | (none)                                 | n/a            |
| `create-tables`                            | `CREATE_TABLES`          | `createTables(fk)`                     | true           |
| `drop-and-create-tables`                   | `DROP_AND_CREATE_TABLES` | `replaceDefaultTables(false, fk)`      | true           |
| `create-or-extend-tables`                  | `CREATE_OR_EXTEND_TABLES`| `extendDefaultTables(fk)`              | true           |
| anything else                              | `NONE` (after warning)   | (none) — `WARNING` logged              | n/a            |

Note: this is a behavior change vs. the previous hardcoded `addETypes(true, true)`.
Consumers that relied on tables being auto-created without setting
`ddl-generation` explicitly must now set it (the four `TestAnnotations` factory
configs and the two `NonOsgi*` test bases already do).

## Changes

- `org.eclipse.fennec.persistence.eclipselink/src/.../dynamic/DdlAction.java`
  — new enum modelling the four EclipseLink ddl-generation modes, with
  `fromEclipseLinkValue(String)` for property-string lookup.
- `org.eclipse.fennec.persistence.eclipselink/src/.../dynamic/EDynamicHelper.java`
  — new overload `addETypes(DdlAction, boolean, List<EDynamicType>)` that
  dispatches to `createTables` / `replaceDefaultTables` / `extendDefaultTables`.
  The existing `addETypes(boolean, boolean, List)` is kept as a thin delegator
  (`true → CREATE_TABLES`, `false → NONE`) for backward compatibility with
  direct callers (`NonOsgiPersistenceTestBase`, `InheritanceIntegrationTest`).
- `org.eclipse.fennec.persistence.eclipselink/src/.../spi/EntityManagerFactoryConfigurator.java`
  — parse `properties.get(DDL_GENERATION)` into a `DdlAction` via
  `DdlAction.fromEclipseLinkValue`, log a `WARNING` and fall back to `NONE` for
  unknown values, bind `generateFKConstraints` to `action != NONE`, then call
  the new `addETypes` overload.
- `org.eclipse.fennec.persistence.eclipselink/test/.../spi/EntityManagerFactoryConfiguratorDdlTest.java`
  — regression tests against an in-memory H2 instance, sharing a single
  `bootstrapAndCheckTable(String)` helper:
  - `ddl-generation=none` → table not created (the failing case from the issue,
    confirmed failing before the fix).
  - `ddl-generation` missing → falls back to `NONE`, table not created.
  - parameterized over `create-tables` / `drop-and-create-tables` /
    `create-or-extend-tables` → table created.
  - unknown value (e.g. `"bogus-mode"`) → table not created **and** a
    `WARNING` log record is captured (via an in-test `Handler` attached to the
    configurator's logger) with the offending value as a parameter.

## Open

- FK-only control (creating tables but skipping FK constraints) is not
  exposed. If a use case appears, introduce a fennec-private key
  (e.g. `fennec.jpa.generateFKConstraints`) plumbed via the Builder.
