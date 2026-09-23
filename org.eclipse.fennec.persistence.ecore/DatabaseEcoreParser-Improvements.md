# DatabaseEcoreParser - Feature Status

> Letzte Aktualisierung: 2026-09-23 (#294, #295, #305, #298)

## Implemented Features

- ✅ Standard JDBC `DatabaseMetaData` (no external dependencies)
- ✅ Table → EClass with naming transformation (SNAKE_CASE → PascalCase)
- ✅ Column → EAttribute with naming transformation (SNAKE_CASE → camelCase)
- ✅ Vendor-independent `JDBCType` mapping (20+ types), refined by precision, scale and `TYPE_NAME` (#295):
  - `DATE`/`TIME`/`TIMESTAMP`/`TIMESTAMP WITH TIME ZONE` → `LocalDate`/`LocalTime`/`LocalDateTime`/`OffsetDateTime`, declared once per package as `E<Type>` data types (the core converters bind them to the native SQL types); `TIME WITH TIME ZONE` stays `EDate` with a warning (no converter)
  - `uuid` by type name → `java.util.UUID` (PostgreSQL reports `OTHER`, H2 `BINARY`)
  - `... UNSIGNED` widens: `INT UNSIGNED` → `ELongObject`, `BIGINT UNSIGNED` → `EBigInteger`
  - `DECIMAL`/`NUMERIC` with scale 0 → `ELongObject` (≤ 18 digits) / `EBigInteger`; unconstrained numerics (no or > 1000 declared digits) stay `EBigDecimal`
  - inline `ENUM('a', 'b')` type names (H2, MySQL/MariaDB `COLUMN_TYPE` syntax) → `EEnum` `<Class><Column>`, the DB value as literal, a Java-safe literal name
- ✅ FK → ManyToOne EReference + auto OneToMany reverse with **EOpposite**
- ✅ **Junction table detection → ManyToMany** with EOpposite (table removed from model)
- ✅ **Containment heuristic**: NOT NULL FK + ON DELETE CASCADE → containment
- ✅ **View support**: configurable, views become read-only EClasses (annotation)
- ✅ **Multi-namespace support**: one EPackage per namespace. The namespace is the **schema** on PostgreSQL/H2 and the **catalog** (= database) on MariaDB/MySQL, decided from `supportsSchemasInTableDefinitions()` / `supportsCatalogsInTableDefinitions()`; every metadata call passes it in the matching argument, so nothing outside it is read. Default = the connection's current schema resp. catalog — never an assumed `PUBLIC`; none → ERROR diagnostic. Package annotation `schema` resp. `catalog`; `TableFacts.catalog()`/`schema()` set on the level the database addresses tables with (#305)
- ✅ Non-OSGi entry point `DatabaseEcoreParser.parse(DataSource, ParserSettings)` (#305)
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
- ✅ **Schema facts on the result** (#295): `ParseResult.tables()` (`TableFacts`: columns with type name, size, scale, nullability, auto-increment, generated, raw default, comment, and the feature carrying each column; primary key; foreign keys with their reference; indexes incl. unique constraints) and `ParseResult.junctions()` (`JunctionFacts`). This — not the annotations — is the input for the eorm generation (#296)
- ✅ Original names also as annotations (`tableName` on classes, `columnName` on attributes, `joinColumns` on references), so they survive a serialized `.ecore`; the ORM side does not read them
- ✅ **Defaults** (#295): literal defaults (`'NEW'`, `0`, `-1`, `TRUE`, PostgreSQL `'x'::type`) → `defaultValueLiteral` when they fit the attribute type; expressions (`CURRENT_TIMESTAMP`, `nextval(...)`) stay a fact with an INFO diagnostic
- ✅ **Comments** (`REMARKS`) → GenModel `documentation` on classes and features (#295)
- ✅ **Naming** (#295): a reference is named after its FK column without `_ID` (`MANAGER_ID` → `manager`); reverse names are English plurals (`orders`, `categories`, `addresses`; a name ending in `s` counts as plural); Java keywords and shadowed `java.lang` types get a `_` suffix with an INFO diagnostic
- ✅ A table without primary key is reported as WARNING — no guessed id; the mapping decides (#296)
- ℹ️ Unique constraints are **facts only**: `EAttribute.unique` means "no duplicates in a many-valued feature" in EMF, not column uniqueness
- ✅ OSGi component with configuration
- ✅ H2 integration tests (15+ scenarios)

## Remaining Improvements (Future)

### Priority 1
- ❌ Enumerations from PostgreSQL `CREATE TYPE … AS ENUM`, MariaDB `information_schema`, and `CHECK (col IN (...))`; boolean emulation from `CHECK` constraints — vendor readers, #303

### Priority 2
- ❌ Cross-namespace FK handling (references between packages) — today a warning and a plain attribute
- ❌ Custom type extensions (PostGIS geometry, JSON, etc.)
- ❌ Incremental model update (compare DB vs. existing Ecore)

## Round trip onto the existing schema (#298)

`JpaSchemaImportRoundTripTest` (TCK) parses a schema with rows, maps it back with DDL generation `none` and compares every row, every reference and a set of writes with plain SQL — 16 cases × parse/read/navigate/write/schema-unchanged, on h2, PostgreSQL and MariaDB. It is **test-first**: aspects that cannot pass yet are listed in `KNOWN_GAPS` with the issue that closes them; they still run, a failure is reported as skipped, and a gap that starts passing fails the suite.

State 2026-09-23, identical on all three flavors: 47 green, 22 known gaps —
- **#296** (eorm onto the existing schema): column, join-column, join-table and table names of the default ORM derivation; sequence instead of IDENTITY; tables without primary key; views
- **#297** (EclipseLink join columns): composite many-to-one, junction join columns
- **#307** deleting a fragment-resolved object deletes the whole table
- **#308** a dynamic UUID data type cannot be read
- **#309** a new object's many-to-one is inserted with a NULL foreign key

Green already with the default mapping: plain tables, java.time/decimal/boolean types, a single-column FK (read, navigate, update), literal defaults on insert, inline enums.

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
| Type rules, defaults, plurals, enum names (#295, unit) | 11 | ✅ |
| H2: schema facts, defaults, types, names (#295) | 12 | ✅ |
| Namespace isolation on h2, PostgreSQL, MariaDB (#305, `JpaSchemaImportNamespaceTest` in the TCK, flavor runs) | 2 | ✅ |
| Round trip onto the existing schema (#298, `JpaSchemaImportRoundTripTest` in the TCK, all flavors) | 69 | 47 ✅ / 22 known gaps |
