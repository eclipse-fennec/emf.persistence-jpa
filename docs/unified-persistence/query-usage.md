# Querying persistence resources — user guide

**Status:** implemented (2026-07-23, issues #32–#45 on `feature/unified-query-spi`).
Companion documents: `query-processor-spi.md` (SPI design), `concept.md` §3.1/§14 (architecture).

One canonical, backend-neutral query model (`org.eclipse.fennec.query.model`) serves **both**
backends: JPA/EclipseLink translates it to JPQL, MongoDB to filter documents or aggregation
pipelines. A query either runs natively or is **refused with diagnostics** — there is no silent
in-memory post-filtering.

---

## 1. Building a query

Use the fluent `QueryBuilder` (`org.eclipse.fennec.model.query.builder`) — never hand-assemble
the containment trees:

```java
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.SortOrder;
import org.eclipse.fennec.model.query.builder.QueryBuilder;

Query adults = QueryBuilder.create()
    .from(personClass)                       // optional type filter
    .where(nameFeature).toLower().contains("smith")
    .and(ageFeature).gte(18)
    .or(ageFeature).eq(65)
    .not(nameFeature).like("%test%")
    .sortBy(ageFeature, SortOrder.ASC)
    .skip(20).limit(10)
    .build();
```

Chaining semantics: the **first** `where` entry is the base predicate; every further entry
chains with the semantics of its call (`and`/`or`); `not` negates its own predicate and chains
conjunctively.

Comparators: `eq`, `lt/lte/gt/gte`, `contains/startsWith/endsWith/like`,
`isBefore/isAfter(+OrEqual)`, `inRange(start, end[, startIncl, endIncl])`, `isLiteral` (enums),
`isBool`. `toLower()`/`toUpper()` before the comparator makes string matching case-insensitive
on both backends.

Nested paths traverse references: `where(addressRef, streetAttr)` — see the capability matrix
for backend limits.

### Projection and aggregation

```java
// projection: rows instead of whole objects
Query names = QueryBuilder.create()
    .where(ageFeature).gte(18)
    .selectAs("n", nameFeature)
    .distinct()
    .build();

// aggregation: group keys + aggregate functions
Query stats = QueryBuilder.create()
    .select(departmentFeature)               // must match a groupBy path
    .avg("avgAge", ageFeature)
    .countOf("cnt", ageFeature)
    .groupBy(departmentFeature)
    .sortBy(avgAgeFeature, SortOrder.DESC)   // row sorting addresses OUTPUT KEYS (aliases)
    .build();
```

Aggregates without `groupBy` aggregate the **whole result set** into a single row (SQL
semantics). Plain subjects in an aggregation must match a `groupBy` path. `count()` (on the
builder) requests a count-only result — distinct from `countOf(alias, path)`, the per-group
count aggregate.

### Prepared queries / parameters

A comparator value `":name"` is a **named placeholder**, bound at execution time; `"::x"`
escapes a literal leading colon. Unbound placeholders fail translation loudly.

```java
Query byAge = QueryBuilder.create().where(ageFeature).eqParam("wanted").build();
// bind later:
resource.query(byAge, Map.of("wanted", 50), null);
```

`named("id")` sets `saveQuery` + name for persisting the query itself (queries are EMF objects).

---

## 2. Executing

Every persistence resource (JPA and Mongo) implements `QueryableResource`
(`org.eclipse.fennec.persistence.query.api`):

```java
Resource resource = resourceSet.createResource(URI.createURI("jpa://tck/Person"));
try (QueryResult result = ((QueryableResource) resource).query(adults)) {
    switch (result.shape()) {
    case OBJECTS     -> result.objects().forEach(this::handle);   // Stream<EObject>, lazy
    case PROJECTION,
         AGGREGATION -> result.rows().forEach(row ->              // Stream<QueryResultRow>
                            log(row.get("n") + " / " + row.get(0)));
    case COUNT       -> log("count = " + result.count());
    }
}
```

Rules:

- **Always close the result** (try-with-resources): it holds the backend cursor — for JPA the
  `EntityManager`/lease stays open until close; for Mongo the cursor.
- The accessor must match `result.shape()`; the others throw `IllegalStateException`.
- Row cells are addressed by subject **alias** or **ordinal** (subject order).
- A query the backend cannot serve natively fails with an `IOException`; the EMF `Diagnostic`
  naming the offending constructs is recorded in `resource.getErrors()` and attached to the
  cause (`QueryException.getDiagnostic()`).

Streaming results plug into PushStreams via the existing add-on
(`PersistencePushStreams`) when consuming `OBJECTS` shapes reactively.

---

## 3. Capability matrix

Both backends declare their capabilities (`QueryProcessor.capabilities()`); `validate(query,
rootEClass)` reports every violation as a `Diagnostic` ERROR before anything executes. Current
declarations:

| `QueryFeature` | JPA (JPQL) | Mongo | Notes |
|---|---|---|---|
| WHERE_EQ / WHERE_COMPARISON | ✅ | ✅ | |
| WHERE_STRING_MATCH | ✅ `LIKE` (escaped, `ESCAPE '\'`) | ✅ anchored regex | `Like` `%`/`_` translated |
| WHERE_RANGE / WHERE_DATE / WHERE_ENUM / WHERE_BOOL | ✅ | ✅ | enums compare by literal name |
| LOGICAL_AND / OR / NOT | ✅ | ✅ (`$nor` for NOT) | chaining semantics identical |
| SORT / LIMIT / SKIP | ✅ | ✅ | row shapes: sort addresses **output keys** |
| DISTINCT | ✅ (also whole entities) | ⚠️ **projection only** (`$group` over keys); whole documents refused (code 101) | |
| COUNT | ✅ `COUNT(e)` | ✅ `countDocuments` | |
| PROJECTION / PROJECTION_NESTED | ✅ result variables | ✅ `$project` | rows, not EObjects |
| GROUP_BY + AGG_AVG/MIN/MAX/SUM/COUNT | ✅ | ✅ pipeline accumulators | ungrouped = whole-set |
| OP_TO_LOWER / OP_TO_UPPER | ✅ `LOWER/UPPER` both sides | ✅ case-insensitive regex | |
| FEATUREPATH_NESTED | ✅ joins, unlimited depth | ⚠️ **containment (embedded) only**, unlimited depth; cross-document paths refused (code 100 — no join) | |
| TYPE_FILTER (+STRICT) | ✅ FROM clause | ✅ collection-per-type layout | satisfied structurally |
| PARAMETERS | ✅ named JPQL parameters | ✅ resolved at translate | |
| AS_OF / SERIES_RANGE | ❌ | ❌ | reserved (concept §14) |

Refusal codes (`QueryValidator.DIAGNOSTIC_SOURCE = org.eclipse.fennec.persistence.query`):
`1` unsupported feature, `2` path depth exceeded, `100` Mongo cross-document path,
`101` Mongo distinct without projection.

---

## 4. Behind the scenes

`QueryableResource.query(...)` = **validate → translate → execute**:

1. `QueryProcessor.validate(query, rootEClass)` — shared `QueryValidator` + backend rules.
2. `translate(query, context)` — pure translation into a `JpaQueryPlan` (JPQL + named
   parameters) or `MongoQueryPlan` (filter/sort or aggregation pipeline + row metadata).
   Values are typed centrally (`QueryValues` via the feature's `EDataType` and the
   `ConverterService`); placeholders resolve from the parameter map (`QueryParameters`).
3. The resource executes the plan against its backend and wraps cursors into a streamed
   `QueryResult` (`QueryResults` factories guarantee shape guarding and idempotent close).

Conformance is guaranteed by the TCK query suite (`AbstractPersistenceTCK`), executed against
H2/EclipseLink and MongoDB in all id-type bindings.
