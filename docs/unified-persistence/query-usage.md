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
- **String functions**: `path(name).toLower().eq("bob")`, `path(name).length().gt(3)` —
  `toLower/toUpper/trim/length`, chainable (`trim().toLower()`); JPA renders JPQL
  functions, Mongo an `$expr` aggregation expression.
- **Arithmetic** (#76): `path(age).plus(10).times(2).gt(90)`, statics
  `add/sub/mul/div/mod/neg(…)` — chainable, values auto-boxed. Semantics: type-preserving
  Java promotion, except `div`, which is **always floating-point**
  (`path(age).dividedBy(4).eq(7.5)` matches age 30). Division by a literal zero is
  refused at validation; a runtime zero surfaces the backend's error. JPA renders JPQL
  operators (`* 1.0 /` for div), Mongo `$expr` (`$add`…`$mod`, root paths only).
- **Numeric functions** (#78): `path(age).dividedBy(4).round().eq(13)`,
  `round/floor/ceiling` on paths and arithmetic steps (statics too). **`round` is half
  away from zero** (OData semantics; Mongo's half-to-even `$round` is emulated via
  `$cond`), the result is integral.
- **Pipeline compute + HAVING** (#82): `.computeAs("avgAge", div(aliasRef("total"),
  aliasRef("cnt")).toExpression())` adds alias-bound computed columns (terminal without
  a grouping — one row per entity, attributes first — or after the grouping over
  aggregate aliases/group keys via `aliasRef(...)`); `.having(aliasRef("cnt").ge(2))`
  filters grouped rows. JPA renders GROUP BY/HAVING (columns re-rendered — JPQL result
  variables are not addressable there), Mongo `$set`/`$match`, memory evaluates in row
  space. A compute **before** the grouping is refused for now.
- **Collection counts** (#81): `count(propertyPath(addresses)).ge(2)` and the filtered
  `count(propertyPath(reviews), r -> r.path(rating).gt(3)).ge(2)` — value expressions,
  missing/empty collections count 0. JPA renders `SIZE` resp. a correlated
  `SELECT COUNT` subquery; Mongo supports the plain form over embedded collections
  (`$size`) and refuses the filtered form for now.
- **Type predicates** (#80): `isOf(carClass)` tests the root with **kind-of** semantics
  (type or subtype; `isOf(path, type)` for navigations), `pathAs(carClass, horsepower)`
  downcasts the root before navigating (JPA `TREAT`; non-instances yield null — the
  comparison is false, not an error). JPA only for now — Mongo refuses via capability
  until documents carry a type discriminator.
- **Temporal part extraction** (#79): `path(birthday).year().eq(1996)`,
  `year/month/day/hour/minute/second` on paths — **UTC-normative** (BSON dates are UTC
  natively; the zone-less SQL TIMESTAMP carries the writing session's wall-clock, so
  run your JVM/DB sessions in UTC). `second` is integral; time parts of date-only
  values are 0; null propagates. Mongo requires native BSON dates (emf.codec#97 —
  temporal attributes without a configured `dateFormat` now store as `BsonDateTime`).
- **Extended string functions** (#77): `concat(path(name), "!")`,
  `path(name).indexOf("o")` (**0-based**, `-1` when absent),
  `path(name).substring(start[, length])` (**0-based**; a negative start counts from
  the end, start beyond the end or a negative length yield `""` —
  [OData-URL] 5.1.1.7). All chainable with the v1 string functions; a null concat part
  poisons the result. JPA renders `CONCAT`/`LOCATE(…)-1`/`SUBSTRING` with CASE
  clamping, Mongo `$concat`/`$indexOfCP`/`$substrCP` (root paths only).
- **Field-to-field**: `path(a).eq(path(b))` compares two features of the root type;
  either side may wrap string functions. Comparisons involving null/missing values are
  false on every backend (SQL semantics).
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

### Saved queries (`saveQuery`)

Queries are EMF objects and persist through the same machinery (concept §14, P2):

```java
Query adults = QueryBuilder.from(personClass)
    .where(path(age).ge(param("minAge")))
    .parameter("minAge", null)
    .named("adults")                       // sets name + saveQuery
    .build();
resource.query(adults, Map.of("minAge", 40), null);   // persists (upsert by name) + executes

// any later resource of the same backend store executes it by name:
try (QueryResult result = resource.query("adults", Map.of("minAge", 50), null)) { … }
```

- Executing a query with `saveQuery=true` and a name **upserts** it into the backend's
  query catalog before execution — last write wins; `saveQuery` without a name is refused.
- The catalog payload is the query's **XMI document**; metamodel references (root type,
  path segments) are stored as nsURI hrefs and resolve against the resource set's package
  registry on load — the model package must be registered, otherwise execution is refused
  with a precise diagnostic. An unknown name is an `IOException`.
- Storage: Mongo collection `fennec.queries` (`_id` = name), JPA table `FENNEC_QUERIES`
  (created on first use, outside the mapped model). The memory backend has no store and
  therefore no catalog.

## 2a. Query-backed derived references

A derived reference annotated with an OCL derivation (unchanged m2x delegate vocabulary)
computes its value through the query stack — concept: `query-derived-references.md`:

```xml
<eAnnotations source="http://www.eclipse.org/emf/2002/Ecore">      <!-- on the EPackage -->
  <details key="settingDelegates" value="http://www.eclipse.org/fennec/m2x/ocl/1.0"/>
</eAnnotations>

<eStructuralFeatures xsi:type="ecore:EReference" name="adultFriends" upperBound="-1"
    eType="#//Person" changeable="false" volatile="true" transient="true" derived="true">
  <eAnnotations source="http://www.eclipse.org/fennec/m2x/ocl/1.0">
    <details key="derivation" value="self.friends->select(f | f.age >= 18)"/>
  </eAnnotations>
</eStructuralFeatures>
```

`person.eGet(adultFriends)` then routes **object-specifically per access**:

- Owner loaded from **XMI** (or any non-`QueryableResource`): standard in-memory OCL
  semantics — local evaluation, or the wrapped m2x delegate for non-pushdown shapes.
- Owner loaded from a **JPA/Mongo resource** with unresolved reference proxies: a native
  backend query `… WHERE id IN (:ownerIds) AND <predicate>` — the proxies are never
  resolved, their ids correlate the query (documented limit, refusal beyond it).
- Containment or already-resolved references evaluate locally — the data is there.

v1 recognizes the shape `self.<manyReference>->select(v | <predicate>)` with the
predicate in the blessed expression subset; other derivations run through the plain m2x
OCL delegate (in an engine-less runtime they are refused with a diagnostic). Results are
unmodifiable and **volatile** — every access queries; snapshot the list in hot loops.
The delegate factory registers on the emf.osgi whiteboard (higher service ranking than
the m2x factory) or programmatically via
`SettingDelegate.Factory.Registry.INSTANCE.put(uri, new QueryBackedSettingDelegateFactory())`.

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

UpdateCommand update = CommandFactory.eINSTANCE.createUpdateCommand();
update.setSelector(QueryBuilder.from(personClass)
    .where(path(name).eq("Bob")).build());
ChangeSet template = StreamFactory.eINSTANCE.createChangeSet();
ChangeEntry setAge = StreamFactory.eINSTANCE.createChangeEntry();
setAge.setKind(DeltaKind.SET);
setAge.setFeatureId(personClass.getFeatureID(age));   // EMF feature id of the root type
setAge.setValueNew("41");                             // EMF string literal
template.getEntries().add(setAge);
update.setTemplate(template);
long updated = ((CommandResource) resource).execute(update);
```

- Insert = the resource's save semantics over **copies** of the contained payload.
- Delete = selector-scoped removal; JPA removes matches **children-first** (containment
  FK safety), Mongo uses `deleteMany(filter)`.
- Update = the template is applied **per match** by the patch-apply engine
  (`ChangeTemplates` in `org.eclipse.fennec.persistence.query.support`): entry
  coordinates address features of the selector's root type (`featureId` = EMF feature
  id, values as EMF string literals). Supported kinds: `SET`/`UNSET` on single-valued
  attributes, `ADD`/`REMOVE`/`MOVE` on many-valued attributes. Reference patching and
  map/array kinds are refused with a precise diagnostic; `valueOld` is not evaluated as
  an optimistic guard. JPA patches the managed entities in one transaction; Mongo
  decodes, patches and replaces each document under its `_id`.
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
| String functions (toLower/toUpper/trim/length) | ✅ | ✅ `$expr` | Mongo: root-based paths only (not inside quantifier predicates) |
| Field-to-field comparisons | ✅ | ✅ `$expr` | Mongo: root-based paths only; `$ne null` guards keep null-comparisons false |
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

## 5. The memory backend

`MemoryQueryProcessor` (`persistence.query.backend=memory`, issue #62) is the third
backend: it has **no store** — its plan evaluates the query against caller-provided
objects (`Resource.getContents()`, any EMF collection):

```java
QueryResult result = MemoryQueries.execute(query, resource.getContents(), parameters);
// or two-step: MemoryQueryPlan plan = MemoryQueries.translate(query, parameters);
//              plan.execute(objects);
```

Its capability set is near-complete (everything except the reserved temporal features,
including multi-stage pipelines and field-to-field comparisons); values stay in EMF
space. Two roles: the **reference oracle** — the TCK differential test runs the same
conformance corpus against every database binding *and* in memory and requires identical
results — and the in-memory execution option for IR consumers such as the OData layer
(`jpa`, `mongo` or `memory` through one SPI). Comparison semantics mirror the database
backends: comparisons with `null` are false, `IsNull` probes explicitly, `forAll` is
vacuously true on empty collections.

## 6. OSGi wiring

All three processors are DS components registered as `QueryProcessor` services carrying
`persistence.query.backend` (`jpa` / `mongo` / `memory`) for selection. The `jpa`
whiteboard resource factory holds an **optional greedy** reference to the `jpa`-backend
service and hands it to every resource it creates — no service means the resources'
local default processor, a higher-ranked service (decorator, reconfiguration) wins for
subsequently created resources. The programmatic `MongoResourceFactory` takes the
processor as an optional constructor argument.

## 7. The OCL bridge

`org.eclipse.fennec.expression.ocl` connects the IR to the m2x Essential-OCL AST
(consumed binary; m2x untouched): `ExprToOcl` is **total** over the blessed subset
(parameters bound before mapping), `OclToExpr` is **partial** and refuses anything
outside it. This is the entry path for OCL-producing frontends — notably the OData
`$filter` pipeline in its phase-1 migration.

## 8. Behind the scenes

`validate → translate → execute`, unchanged: `ExpressionAnalyzer` walks envelope +
expression trees into the shared `QueryAnalysis`/`QueryValidator`/`QueryCapabilities`
mechanism; `ExpressionValues` resolves typed literals (target-narrowing, enum/temporal
resolution) and parameter bindings, with the `ConverterService` owning EMF→persistence
conversion; the processors emit `JpaQueryPlan` (JPQL + named parameters) or
`MongoQueryPlan` (filter/sort or pipeline + row metadata); the resources execute and
stream. Conformance: the TCK query + command suites run against H2/EclipseLink and
MongoDB in all four id-type bindings.
