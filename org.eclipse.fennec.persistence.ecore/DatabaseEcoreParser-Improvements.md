# DatabaseEcoreParser - Feature Status

> Letzte Aktualisierung: 2026-09-23 (#294)

## Implemented Features

- ✅ Standard JDBC `DatabaseMetaData` (no external dependencies)
- ✅ Table → EClass with naming transformation (SNAKE_CASE → PascalCase)
- ✅ Column → EAttribute with naming transformation (SNAKE_CASE → camelCase)
- ✅ Vendor-independent `JDBCType` mapping (20+ types)
- ✅ FK → ManyToOne EReference + auto OneToMany reverse with **EOpposite**
- ✅ **Junction table detection → ManyToMany** with EOpposite (table removed from model)
- ✅ **Containment heuristic**: NOT NULL FK + ON DELETE CASCADE → containment
- ✅ **View support**: configurable, views become read-only EClasses (annotation)
- ✅ **Multi-schema support**: one EPackage per schema, schema annotation on package
- ✅ **Naming transformation**: configurable (on/off), SNAKE_CASE → CamelCase
- ✅ Primary key detection; a **composite PK** is declared via `idFeatures` (`http://eclipse.org/fennec/persistence/1.0`, key order) instead of several `isID` attributes (#294)
- ✅ **Composite FKs** become one reference (grouped by `FK_NAME`, columns in `KEY_SEQ` order) (#294)
- ✅ **Identifying FKs** (FK column inside the PK) keep the column as id attribute and add a reference named after the target (#294)
- ✅ **Unique feature names**: collisions (two FKs to one table, self-referencing junction) fall back to `<name>Via<FkColumns>` and are reported as INFO diagnostic (#294)
- ✅ Junction table = exactly two FKs covering all columns; a third FK makes it an entity (#294)
- ✅ Schema and table names are escaped for JDBC metadata patterns (`_`, `%`) (#294)
- ✅ FK targets in another schema are reported and mapped as plain attributes (#294)
- ✅ Deterministic output: tables in metadata order, no hash-order dependence (#294)
- ✅ NOT NULL → `lowerBound=1` (required)
- ✅ Original table name preserved in EAnnotation for EORM mapping
- ✅ OSGi component with configuration
- ✅ H2 integration tests (15+ scenarios)

## Remaining Improvements (Future)

### Priority 1
- ❌ Column size/precision capture (VARCHAR(255), DECIMAL(10,2))
- ❌ Unique constraints → `EAttribute.unique=true`
- ❌ Pluralization rules (smarter than just appending "s")

### Priority 2
- ❌ Cross-schema FK handling (references between packages)
- ❌ Custom type extensions (PostGIS geometry, JSON, etc.)
- ❌ Index metadata capture
- ❌ Incremental model update (compare DB vs. existing Ecore)

## Test Coverage

| Category | Tests | Status |
|----------|-------|--------|
| Type mapping (convertType) | 8 | ✅ |
| Naming transformation | 2 | ✅ |
| Static helpers | 3 | ✅ |
| H2: basic parsing + naming | 3 | ✅ |
| H2: type mapping e2e | 1 | ✅ |
| H2: FK + EOpposite | 1 | ✅ |
| H2: containment heuristic | 2 | ✅ |
| H2: ManyToMany junction | 2 | ✅ |
| H2: views | 2 | ✅ |
| H2: multi-schema | 2 | ✅ |
| H2: composite PK | 3 | ✅ |
| H2: schema patterns (#294) — every case validated with `Diagnostician` | 9 | ✅ |
