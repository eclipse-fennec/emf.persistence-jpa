# Dynamic schema creation in `EDynamicHelper`

## The problem

EclipseLink's dynamic entity API allows new entity types to be registered at runtime, after the
`EntityManagerFactory` has already been created. The entry point is
`EDynamicHelper.addETypes()`, which calls
`DynamicSchemaManager.createTables()` to generate the corresponding tables in the database.

`DynamicSchemaManager.createTables()` issues `CREATE TABLE <schema>.<table>` statements for
every entity that maps to a schema-qualified table. On many databases, including H2, the target
schema must already exist before the `CREATE TABLE` statement is executed. If it does not,
the database raises an error such as:

```
Schema "finance" not found; SQL statement:
    CREATE TABLE finance.invoices (...)
```

### Why this only surfaced with in-memory H2 (first layer)

With a file-based H2 database the startup I/O latency gave an external schema-creation step
(e.g. `CsvDataImporter`, which creates schemas from sub-folder names) just enough time to
finish before EclipseLink's `createTables()` ran. With an in-memory H2 database there is no
such delay: EclipseLink connects and calls `createTables()` almost immediately, winning the
race every time.

The root cause is therefore not specific to in-memory databases — it is a latent ordering
dependency that happened to be hidden by file-system I/O on slower machines.

### Why in-memory H2 can still fail even after the fix (second layer)

After `createMissingSchemas()` was added, tests using a **direct JDBC connection** (unit tests
that supply `JDBC_URL` / `JDBC_DRIVER` properties) pass reliably.  Tests that supply an
**external DataSource** via `NON_JTA_DATASOURCE` continued to fail with the same error.

When `NON_JTA_DATASOURCE` is set, EclipseLink creates an `ExternalConnectionPool` instead of
its own internal pool.  `ExternalConnectionPool.startUp()` builds only an unconnected
*template* accessor — no persistent physical connection is established:

```java
// ExternalConnectionPool.startUp()
public synchronized void startUp() {
    setCachedConnection(buildConnection());  // template only, not connected
    setIsConnected(true);
}
```

Every `acquireConnection()` clones this template and the clone opens a fresh physical JDBC
connection on first use.  Every `releaseConnection()` immediately closes that physical
connection:

```java
public void releaseConnection(Accessor connection) throws DatabaseException {
    connection.closeConnection();   // physical JDBC connection closed here
    ...
}
```

`H2DataSource` (the OSGi component that backs the external DataSource in integration tests)
wraps `org.h2.jdbcx.JdbcDataSource` directly — it has **no connection pooling of its own**.
So each EclipseLink SQL call:

1. opens a new physical H2 connection (`JdbcDataSource.getConnection()`),
2. executes the SQL,
3. closes the physical connection.

H2 destroys a named in-memory database the moment **all** connections to it are closed.
Between `createMissingSchemas()` and `DynamicSchemaManager.createTables()`, the only open
connection is the one used for schema creation — which is already closed by the time the next
SQL call acquires a new connection.  H2 therefore discards the database and creates a fresh
one, losing all schemas that were just created.

With a **file-based** H2 database the same connection lifecycle applies, but the file persists
through connection closures, so the schemas are never lost.

### The required H2 URL option

The standard H2 option `DB_CLOSE_DELAY=-1` prevents the in-memory database from being
destroyed when the last connection closes.  It must be added to the JDBC URL in any mapping
file or configuration that uses a named in-memory H2 database with an external DataSource:

```
jdbc:h2:mem:demodb;DATABASE_TO_UPPER=FALSE;DB_CLOSE_DELAY=-1
```

This option has no effect on file-based or server-mode H2 databases and is ignored by all
other databases, so it is safe to include in shared test configuration files.

---

## The fix

`EDynamicHelper.addETypes()` now calls a private `createMissingSchemas()` helper
**before** delegating to `DynamicSchemaManager.createTables()`:

```java
if (createMissingTables) {
    if (!getSession().isConnected()) {
        getSession().login();
    }
    createMissingSchemas(descriptors);                       // ← added
    new DynamicSchemaManager(session).createTables(generateFKConstraints);
}
```

`createMissingSchemas()` collects the distinct schema qualifiers from all registered
`ClassDescriptor` table definitions and creates each missing schema via a single idempotent SQL
statement:

```java
private void createMissingSchemas(Collection<ClassDescriptor> descriptors) {
    descriptors.stream()
        .flatMap(d -> d.getTables().stream())
        .map(DatabaseTable::getTableQualifier)
        .filter(s -> s != null && !s.isBlank())
        .distinct()
        .forEach(schema -> {
            try {
                ((AbstractSession) session).executeNonSelectingSQL(
                        "CREATE SCHEMA IF NOT EXISTS " + schema);
            } catch (Exception e) {
                LOG.warning("Could not create schema '" + schema + "': " + e.getMessage());
            }
        });
}
```

Using `IF NOT EXISTS` makes the call idempotent: if the schema was already created by another
component (e.g. `CsvDataImporter`) or by a previous test run, the statement succeeds silently.

The fix is covered by `EDynamicHelperSchemaTest`, which verifies schema and table creation on a
fresh in-memory H2 database for the following scenarios:

- Single entity in a non-default schema.
- Multiple non-default schemas (`FINANCE`, `HR`) all created.
- Mixed default-schema and non-default-schema entities.
- Full persist-and-find round-trip for an entity in a non-default schema.

---

## Why `jakarta.persistence.schema-generation.create-database-schemas` does not help

JPA 2.1 introduced the standard property
`jakarta.persistence.schema-generation.create-database-schemas` (EclipseLink constant:
`PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_DATABASE_SCHEMAS`). Setting it to `"true"`
instructs EclipseLink to issue `CREATE SCHEMA` statements before creating tables. This sounds
like exactly what is needed, but it does not apply to our case for two reasons.

### Reason 1 — it is gated on DDL generation being active

The property is consumed in
`EntityManagerSetupImpl.writeMetadataDDLToDatabase()`:

```java
String createSchemas = getConfigPropertyAsString(SCHEMA_GENERATION_CREATE_DATABASE_SCHEMAS, props);
mgr.setCreateDatabaseSchemas(createSchemas != null && createSchemas.equalsIgnoreCase("true"));
```

`writeMetadataDDLToDatabase()` is only reachable when `DDL_GENERATION` is not `NONE`.
`AbstractPersistenceUnitConfigurator` sets `DDL_GENERATION = NONE` by default so that
EclipseLink does not touch the database schema at startup. With that default in place, the
property is never read.

### Reason 2 — it runs at the wrong time

Even when DDL generation is enabled, `writeMetadataDDLToDatabase()` is called during
`PersistenceProvider.createContainerEntityManagerFactory()` — that is, at boot time, when only
the *static* entity types declared in the persistence unit are known. Dynamic entity types are
registered later via `EDynamicHelper.addETypes()`. By the time `addETypes()` is called, the
standard schema-generation phase has already finished and will not be re-entered.

### Reason 3 — EclipseLink's own built-in SQL is weaker

`DynamicSchemaManager` inherits the `createDatabaseSchemas` flag from `SchemaManager`, so
`setCreateDatabaseSchemas(true)` would technically reach `DynamicSchemaManager.createTables()`
as well. However, the underlying helper method
`TableDefinition.createDatabaseSchemaOnDatabase()` is marked
`@Deprecated(forRemoval = true, since = "4.0.9")`, and the SQL it generates is:

```sql
CREATE SCHEMA <schema_name>
```

The absence of `IF NOT EXISTS` means that if the schema already exists the statement fails.
Our explicit `CREATE SCHEMA IF NOT EXISTS` is therefore both on the supported API path and
semantically safer.

---

## Summary

| Approach | Runs at the right time? | `IF NOT EXISTS`? | Uses supported API? |
|---|---|---|---|
| `jakarta.persistence.schema-generation.create-database-schemas` | No — boot time only, gated on `DDL_GENERATION != NONE` | No | Yes |
| `DynamicSchemaManager.setCreateDatabaseSchemas(true)` | Yes | No | No — deprecated for removal in 4.0.9 |
| `EDynamicHelper.createMissingSchemas()` (our fix) | Yes | Yes | Yes |
