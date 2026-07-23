# Querying and writing persistence resources — user guide

**Status:** rewritten for the expression IR (2026-07-24, Query IR v2, issues #47–#58).
Companions: `query-ir-redesign.md` (decision record R1–R10), `query-processor-spi.md`
(SPI architecture), `concept.md` §3.1/§14.

One canonical IR — the **Fennec Expression Model** (`org.eclipse.fennec.model.expression`)
inside the **query envelope** (`org.eclipse.fennec.model.query`) — serves both backends:
JPA/EclipseLink translates to JPQL, MongoDB to filter documents or aggregation pipelines.
A query either runs natively or is **refused with diagnostics**; there is no silent
in-memory post-filtering. Write commands (CUD) live in the **command model**
(`org.eclipse.fennec.model.command`), not in the query language.

---

## 1. Building queries

Compose predicates with the static `Expressions` factory, envelopes with `QueryBuilder`
(both in `org.eclipse.fennec.model.query.builder`):

```java
import static org.eclipse.fennec.model.query.builder.Expressions.*;
import org.eclipse.fennec.model.query.builder.QueryBuilder;

Query query = QueryBuilder.from(personClass)
    .where(and(
        or(path(name).eq("smith"), path(name).containsIgnoreCase("x")),
        path(age).ge(18),
        path(age).ne(65),
        path(nickname).isNull(),
        path(age).in(30, 40, param("more")),
        any(propertyPath(addresses), a -> a.path(street).startsWith("Main"))))
    .orderByAsc(age)
    .top(10).skip(5)
    .build();
```

- **Real expression trees**: `and`/`or` are n-ary, arbitrarily nested — `(a OR b) AND c`
  is a first-class shape.
- **Comparisons**: `eq ne lt le gt ge` (typed values, auto-boxed literals), `isNull`/
  `isNotNull`, `between(lo, hi[, loIncl, hiIncl])`, `in(...)`.
- **String matching**: `contains/startsWith/endsWith/like` with `…IgnoreCase` variants —
  case-insensitivity is a model flag, translated natively (LOWER both sides / regex `i`).
- **Quantifiers**: `any`/`all(propertyPath(ref), it -> …)` over multi-valued references,
  with a scoped iterator variable. `all` is vacuously true on empty collections.
- **Parameters**: `param("name")` is a first-class `ParameterRef`; declare with
  `.parameter("name", type)` and bind at execution. Unbound parameters fail translation.

### Projection, aggregation, fetch hints

```java
// projection → rows
QueryBuilder.from(personClass)
    .where(path(age).ge(18))
    .selectAs("n", name)
    .distinct()
    .build();

// aggregation → pipeline with a GroupBy stage
QueryBuilder.from(personClass)
    .groupBy(department)
    .avg("avgAge", age)
    .countOf("cnt")
    .countDistinct("streets", addresses, street)
    .orderByDesc(avgAgeFeature)      // row sorting addresses OUTPUT KEYS
    .build();

// eager-fetch hint (JPA: LEFT JOIN FETCH; Mongo: refused)
QueryBuilder.from(personClass).expand(addresses).build();
```

Aggregates without `groupBy` aggregate the whole result set (single row). Group-key
columns are alias-addressable under their derived name. Plain selections and `apply`
are mutually exclusive. Richer pipelines (extra `Filter`/`Top`/`Skip` stages) can be
composed via the model — Mongo executes them natively, JPA refuses them (see the
matrix).

## 2. Executing queries

Both persistence resources implement `QueryableResource`
(`org.eclipse.fennec.persistence.query.api`):

```java
Resource resource = resourceSet.createResource(URI.createURI("jpa://tck/Person"));
try (QueryResult result = ((QueryableResource) resource).query(query)) {
    switch (result.shape()) {
    case OBJECTS     -> result.objects().forEach(this::handle);   // lazy Stream<EObject>
    case PROJECTION,
         AGGREGATION -> result.rows().forEach(row ->
                            log(row.get("n") + " / " + row.get(0)));
    case COUNT       -> log("count = " + result.count());
    }
}
```

Rules unchanged from v1: **close the result** (cursor/lease lives until close), accessors
are shape-guarded, refusals surface as `IOException` with the `Diagnostic` in
`resource.getErrors()` and `QueryException.getDiagnostic()`.

## 3. Write commands (CUD v1)

Per concept §14 the query language is read-only; writes are commands
(`org.eclipse.fennec.model.command`) executed via `CommandResource`:

```java
InsertCommand insert = CommandFactory.eINSTANCE.createInsertCommand();
insert.getObjects().add(person);                       // command owns its payload
long inserted = ((CommandResource) resource).execute(insert);   // executes on copies

DeleteCommand delete = CommandFactory.eINSTANCE.createDeleteCommand();
delete.setSelector(QueryBuilder.from(personClass)
    .where(path(age).ge(40)).build());                 // plain filter only
long deleted = ((CommandResource) resource).execute(delete);

UpdateCommand update = …;   // selector + ChangeSet template (stream model)
// v1: refused with a diagnostic — execution follows the patch-apply engine
```

- Insert = the resource's save semantics over **copies** of the contained payload.
- Delete = selector-scoped removal; JPA removes matches **children-first** (containment
  FK safety), Mongo uses `deleteMany(filter)`.
- Command selectors must be **plain filters** — projection/aggregation/ordering/paging
  on a selector are refused.

## 4. Capability matrix

Declared via `QueryProcessor.capabilities()`; `validate()` reports every violation as a
`Diagnostic` ERROR before anything executes.

| Feature | JPA (JPQL) | Mongo | Notes |
|---|---|---|---|
| Comparisons incl. `ne`, `isNull`, `between`, `in` | ✅ | ✅ | Mongo IsNull = missing-or-null |
| Logic trees (n-ary and/or, not, nested) | ✅ | ✅ (`$nor`) | |
| String matching + case-insensitive flag | ✅ LOWER both sides | ✅ regex `i` | LIKE `%`/`_` translated |
| String functions (toLower/toUpper/trim/length) | ✅ | ❌ (needs `$expr`) | |
| Field-to-field comparisons | ✅ | ❌ | |
| `exists` / `forAll` quantifiers | ✅ correlated `[NOT] EXISTS`, nested | ⚠️ **embedded collections only** (`$elemMatch`; code 100) | vacuous truth on empty |
| Path navigation | ✅ joins, unlimited | ⚠️ containment only, unlimited (code 100) | |
| Sort / top / skip / count | ✅ | ✅ | row sorting addresses output keys |
| DISTINCT | ✅ incl. whole entities | ⚠️ projection only (code 101) | |
| Projection / grouped + whole-set aggregation, COUNT_DISTINCT | ✅ | ✅ (`$addToSet`+`$size`) | group keys alias-addressable |
| **Multi-stage pipelines** | ❌ refused | ✅ **native** ($match/$limit/$skip in stage order) | the capability asymmetry showcase |
| `expand` fetch hints | ✅ LEFT JOIN FETCH (depth 1) | ❌ | |
| Parameters (`ParameterRef`) | ✅ | ✅ | model-level, no string convention |
| Type filter | ✅ FROM clause | ✅ collection-per-type | structural |
| AS_OF / SERIES_RANGE | ❌ | ❌ | reserved (concept §14) |

Refusal codes (`QueryValidator.DIAGNOSTIC_SOURCE`): `1` unsupported feature, `2` depth
exceeded, `100` Mongo cross-document/non-embedded, `101` Mongo distinct without
projection.

## 5. The OCL bridge

`org.eclipse.fennec.expression.ocl` connects the IR to the m2x Essential-OCL AST
(consumed binary; m2x untouched): `ExprToOcl` is **total** over the blessed subset
(parameters bound before mapping), `OclToExpr` is **partial** and refuses anything
outside it. This is the entry path for OCL-producing frontends — notably the OData
`$filter` pipeline in its phase-1 migration.

## 6. Behind the scenes

`validate → translate → execute`, unchanged: `ExpressionAnalyzer` walks envelope +
expression trees into the shared `QueryAnalysis`/`QueryValidator`/`QueryCapabilities`
mechanism; `ExpressionValues` resolves typed literals (target-narrowing, enum/temporal
resolution) and parameter bindings, with the `ConverterService` owning EMF→persistence
conversion; the processors emit `JpaQueryPlan` (JPQL + named parameters) or
`MongoQueryPlan` (filter/sort or pipeline + row metadata); the resources execute and
stream. Conformance: the TCK query + command suites run against H2/EclipseLink and
MongoDB in all four id-type bindings.
