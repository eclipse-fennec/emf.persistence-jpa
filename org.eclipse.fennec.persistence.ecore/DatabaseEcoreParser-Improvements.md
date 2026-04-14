# DatabaseEcoreParser - Feature Status

> Letzte Aktualisierung: 2026-04-14

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
- ✅ Primary key detection (composite-safe)
- ✅ NOT NULL → `lowerBound=1` (required)
- ✅ Original table name preserved in EAnnotation for EORM mapping
- ✅ OSGi component with configuration
- ✅ H2 integration tests (15+ scenarios)

## Remaining Improvements (Future)

### Priority 1
- ❌ Column size/precision capture (VARCHAR(255), DECIMAL(10,2))
- ❌ Unique constraints → `EAttribute.unique=true`
- ❌ Self-referencing FK handling (e.g., employees.manager_id → employees.id)
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
| H2: composite PK | 1 | ✅ |
