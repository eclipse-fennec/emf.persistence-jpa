# Query User Guide

Fennec Persistence has **one query language** for every backend. You build a query as an
EMF object tree — the *canonical query model* — and the backend translates it: JPA to
JPQL, MongoDB to a filter document or an aggregation pipeline.

Two consequences are worth internalising before the first query:

- **A query either runs natively or is refused.** There is no silent in-memory
  post-filtering. If a backend cannot express what you asked for, you get an
  `IOException` carrying a `Diagnostic` that names the feature — at `prepare` time if you
  prepared, otherwise before execution.
- **The query language is read-only.** Writes are *commands* — a separate model, executed
  through the same repository or resource. See [Write commands](#write-commands).

## Contents

1. [Building a query](#building-a-query)
2. [Predicates](#predicates)
3. [Value expressions](#value-expressions)
4. [Maps](#maps)
5. [Geo](#geo)
6. [Result shapes: objects, rows, count](#result-shapes-objects-rows-count)
7. [Sorting, paging and distinct](#sorting-paging-and-distinct)
8. [Parameters and prepared queries](#parameters-and-prepared-queries)
9. [Executing a query](#executing-a-query)
10. [Saved queries](#saved-queries)
11. [Write commands](#write-commands)
12. [Capabilities and refusals](#capabilities-and-refusals)
13. [Querying in memory](#querying-in-memory)
14. [Derived references](#derived-references)

## Building a query

Two static entry points, both in `org.eclipse.fennec.model.query.builder`:
`QueryBuilder` for the envelope (root type, projection, ordering, paging) and
`Expressions` for everything inside a predicate or a computed value.

```java
import static org.eclipse.fennec.model.query.builder.Expressions.*;

import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.builder.QueryBuilder;

Query query = QueryBuilder.from(personClass)
        .where(and(
                or(path(name).eq("Smith"), path(name).containsIgnoreCase("x")),
                path(age).ge(18),
                path(nickname).isNull(),
                any(propertyPath(addresses), a -> a.path(street).startsWith("Main"))))
        .orderByAsc(age)
        .top(10).skip(5)
        .build();
```

`from(EClass)` fixes the root type — every unqualified path in the query is rooted there.
`where` takes exactly one expression; use `and`/`or` to combine, they are n-ary and
nest arbitrarily, so `(a OR b) AND c` is a first-class shape rather than a flattening.

Paths come in two flavours, and mixing them up is the most common compile error:
`path(feature)` starts a *value* (something you compare or compute with), while
`propertyPath(feature)` is the *navigation itself*, which is what quantifiers and
collection counts take. `path(a, b, c)` navigates several segments.

## Predicates

**Comparisons.** `eq ne lt le gt ge` against literals (auto-boxed), other paths, or
parameters; plus `isNull()`, `isNotNull()`, `between(lo, hi)` (with optional inclusivity
flags) and `in(...)`.

Comparisons involving `null` are **false** on every backend — SQL semantics, mirrored by
Mongo and the memory backend. Use `isNull()` to probe explicitly.

**String matching.** `contains`, `startsWith`, `endsWith`, `like`, each with an
`…IgnoreCase` variant. Case-insensitivity is a flag on the model, not a hand-written
`LOWER(...)`: JPA wraps both sides in `LOWER`, Mongo uses a case-insensitive regex.
`like` takes the SQL wildcards `%` and `_`.

`fuzzy(value[, maxEdits[, prefixLength]])` matches a whole value within an edit distance
(optimal string alignment, budget 1–2, default 2). Neither JPA nor Mongo serves it —
both refuse it at validation. It exists for search backends (Lucene) and the memory
oracle defines its reference semantics.

**Quantifiers.** `any` and `all` over a multi-valued reference, with a scoped iterator:

```java
any(propertyPath(addresses), a -> a.path(city).eq("Jena"))
all(propertyPath(orders),    o -> o.path(total).gt(0))
```

`all` is vacuously true on an empty collection. JPA renders correlated `EXISTS` /
`NOT EXISTS` subqueries and nests them without limit; Mongo serves quantifiers over
**embedded** collections only (`$elemMatch`) and refuses cross-document ones.

**Type predicates.** `isOf(carClass)` tests the root with kind-of semantics — the type or
any subtype — and `isOf(path, type)` does the same for a navigation. `pathAs(carClass,
horsepower)` downcasts before navigating (JPA `TREAT`); a row that is not an instance
yields null, so the comparison is false rather than an error. Mongo matches the codec's
type discriminator (`_type` by default), so inheritance inside one collection works —
index that field for large collections.

**Collection counts.** `count(propertyPath(addresses)).ge(2)`, and the filtered form
`count(propertyPath(reviews), r -> r.path(rating).gt(3)).ge(2)`. These are value
expressions, so they compose with any comparison. Missing or empty collections count 0.

## Value expressions

Anything that produces a value rather than a truth: paths, arithmetic, functions. They
appear in comparisons, in projections, as sort keys, as group keys and as aggregate
sources.

**Arithmetic.** `path(age).plus(10).times(2)`, or the statics `add sub mul div mod neg`.
Chainable, values auto-boxed, type-preserving Java promotion — **except `div`, which is
always floating-point**: `path(age).dividedBy(4).eq(7.5)` matches age 30. Division by a
literal zero is refused at validation; a zero that only shows up at runtime surfaces as
the backend's own error.

**Numeric functions.** `round floor ceiling`, on paths and on arithmetic steps. `round`
is **half away from zero** (OData semantics; Mongo's half-to-even `$round` is emulated),
and the result is integral.

**String functions.** `toLower toUpper trim length`, chainable
(`path(name).trim().toLower().eq("bob")`), plus the extended set: `concat(path(name),
"!")`, `path(name).indexOf("o")` (**0-based**, `-1` when absent) and
`path(name).substring(start[, length])` (**0-based**; a negative start counts from the
end, a start past the end or a negative length yield `""`). A null part poisons a
`concat`. On Mongo these work on root-based paths, not inside quantifier predicates.

**Temporal parts.** `path(birthday).year()`, and `month day hour minute second`. These
are **UTC-normative**: BSON dates are UTC natively, while a zone-less SQL `TIMESTAMP`
carries whatever wall-clock the writing session had — run your JVM and database sessions
in UTC if you extract parts. Time parts of date-only values are 0, and null propagates.

**Alias references.** `aliasRef("cnt")` addresses a column produced earlier in the same
query — a computed column, a group key or an aggregate. It is how `having` and row-space
sorting reach pipeline output.

## Maps

An `EMap` feature is addressed by key with `mapValue`:

```java
// filter by one entry
QueryBuilder.from(catalogClass)
        .where(mapValue(attributes, "color").eq("red"))
        .build();
```

`mapValue(map, key)` yields a value expression, so it composes with comparisons, string
functions and arithmetic like any other value. The key must be **constant** — a literal
or a parameter; it is never a path, on any backend. JPA renders a correlated subselect
over the entry table, Mongo a field path into the stored sub-document.

Two things about maps are worth knowing up front:

- **A quantifier over a map is a backend divergence.** JPA serves `any`/`all` over a map
  by ranging over the entry table; Mongo refuses it (code 103), because a map is stored
  as a sub-document rather than an array and the equivalent filter would silently match
  nothing. Address the entry by its key with `mapValue` if the query has to be portable.
- **Grouping by a map value changes how JPA renders it.** In a predicate the entry is a
  correlated subselect; inside a grouping that form is illegal (it references the very row
  being grouped), so the entry joins into the FROM clause instead and the value becomes an
  ordinary column. The join is *outer* with the key in its `ON` clause, so an owner without
  that key groups under `null` rather than dropping out — which is what Mongo does for a
  missing sub-document field. Both backends therefore answer the same.

Whether that `null` group belongs in the answer is a property of the question, not of the
backend, so you exclude it in the query — a predicate before the grouping, portable across
both:

```java
QueryBuilder.from(catalogClass)
        .where(mapValue(attributes, "color").isNotNull())
        .groupByAs("color", mapValue(attributes, "color").toExpression())
        .countOf("total")
        .build();
```

Note what happens on JPA: the predicate keeps the subselect form — a correlated reference
is legal in `WHERE` — while the grouping joins, so both renderings of the same map access
sit in one statement.

## Geo

MongoDB serves geospatial predicates; JPA does not declare them and refuses them.

```java
GeoSubject where = geoSubject(propertyPath(location));            // a GeoJSON point field
GeoSubject split = geoSubject(propertyPath(lat), propertyPath(lon));  // or two numbers

// inside a shape
QueryBuilder.from(stopClass)
        .where(geoWithin(where, geoBox(geoPoint(11.5, 50.9), geoPoint(11.7, 51.0))))
        .build();

// distance as a value: nearest first
QueryBuilder.from(stopClass)
        .where(geoDistance(where, geoPoint(11.58, 50.93)).le(1500))
        .orderByAsc(geoDistance(where, geoPoint(11.58, 50.93)).toExpression())
        .build();
```

`geoDistance` is the spherical WGS84 distance **in meters**, so it composes with
comparisons and with sorting. Shapes are `geoBox(southWest, northEast)` and
`geoPolygon(points…)`. Coordinates are `geoPoint(lon, lat)` — longitude first, as in
GeoJSON. Malformed geometry (a subject without exactly one binding, out-of-range
coordinates, a degenerate or antimeridian-crossing polygon) is refused at validation with
code 6.

## Result shapes: objects, rows, count

The envelope decides what comes back. `QueryResult.shape()` tells you which accessor is
legal; the others throw.

**Objects** — the default. No projection, no aggregation:

```java
QueryBuilder.from(personClass).where(path(age).ge(18)).build();   // → OBJECTS
```

**Projection** — named columns instead of objects:

```java
QueryBuilder.from(personClass)
        .selectAs("n", name)
        .selectAs("city", address, city)     // multi-segment path
        .build();                            // → PROJECTION
```

A column can also be **computed** rather than navigated — the projection counterpart of
sorting by an expression:

```java
QueryBuilder.from(personClass)
        .selectAs("n", name)
        .selectAs("nextAge", path(age).plus(1).toExpression())
        .selectAs("colour", mapValue(attributes, "color").toExpression())
        .build();
```

The alias is **mandatory** for a computed column: a path has a derivable column name, an
expression does not. A `Selection` carries either a path or an expression, never both and
never neither — the same shape `OrderBy` has. Both backends serve it; the capability is
`PROJECTION_EXPRESSION`.

**Aggregation** — a grouping stage, or aggregates without one (which aggregate the whole
result set into a single row):

```java
QueryBuilder.from(personClass)
        .groupBy(department)
        .avg("avgAge", age)
        .countOf("cnt")
        .countDistinct("streets", addresses, street)
        .having(aliasRef("cnt").ge(2))
        .orderByDesc(aliasRef("avgAge").toExpression())
        .build();                            // → AGGREGATION
```

Group-key columns are addressable under their derived name. `groupByAs(alias,
expression)` groups by a computed value instead of a path, and the aggregate overloads
taking an `Expression` (`sum avg min max countDistinct`) aggregate one:

```java
QueryBuilder.from(personClass)
        .computeAs("dec", path(age).dividedBy(10).floor().toExpression())
        .groupByAs("decade", aliasRef("dec").toExpression())
        .countOf("cnt")
        .build();
```

A `computeAs` **before** the grouping only binds a name for reuse — it is not an output
column. A `computeAs` without any grouping is terminal: one row per object, attributes
first.

**Count** — `countOnly()` returns just the cardinality:

```java
try (QueryResult r = repository.find(QueryBuilder.from(personClass).countOnly().build())) {
    long total = r.count();
}
```

**Scores** — `withScores()` asks for per-hit relevance. Only backends that declare it
(Lucene-based search) serve it; `result.hits()` then streams `(object, score)` in rank
order and `result.scores()` is the id → score view, complete before any object is
materialised.

## Sorting, paging and distinct

```java
QueryBuilder.from(personClass)
        .orderByAsc(lastName)
        .orderByDesc(age)
        .skip(20).top(10)        // page 3 at page size 10
        .build();
```

`top` is the page size, `skip` the offset; both are envelope-level and apply to whatever
shape the query has. Multiple `orderBy…` calls compose into a sort chain in call order.

Two things to know:

- **Sorting by an expression** (`orderByAsc(neg(path(age)).toExpression())`) is a JPA-only
  capability. Mongo's find-sorts are field-based and refuse it. On row shapes, sort by
  `aliasRef("column")` instead — that works on both, because it addresses an output
  column rather than re-computing an expression.
- **`distinct()`** on a projection works everywhere. On whole entities it is JPA-only;
  Mongo refuses distinct without a projection (code 101).

Paging an *unordered* query is a request for arbitrary rows — the backends do not impose
a default order, so always pair `skip`/`top` with an `orderBy…`.

## Parameters and prepared queries

`param("name")` is a first-class node, not a string convention. Declare it on the builder,
bind it at execution:

```java
Query byAge = QueryBuilder.from(personClass)
        .where(path(age).ge(param("min")))
        .parameter("min", null)
        .build();

try (QueryResult result = repository.find(byAge, Map.of("min", 18), null)) { … }
```

An unbound parameter fails translation rather than defaulting to anything.

A **prepared query** is the prepared-statement analogue: validated once, against the
backend's capabilities, at `prepare` time — so a query the backend cannot serve is
refused there, not at first execution.

```java
PreparedQuery adults = repository.prepare(byAge);
try (QueryResult result = adults.execute(Map.of("min", 18))) { … }
```

## Executing a query

**Through the repository** — the normal way. `find` executes, `count` counts, and the
root type comes from the query itself:

```java
@Reference(target = "(persistence.repository.id=shop)")
private Repository repository;

try (QueryResult result = repository.find(query, Map.of("min", 18), null)) {
    result.objects().forEach(this::handle);
}
```

See the [Repository User Guide](repository-user-guide.md) for the repository's own
contract — prototype scope, read-only registration, keyed reads.

**Through a resource** — the low-level route, when you already hold a `Resource`. Both
persistence backends implement `QueryableResource`:

```java
Resource resource = resourceSet.createResource(URI.createURI("jpa://shop/Person"));
try (QueryResult result = ((QueryableResource) resource).query(query)) {
    switch (result.shape()) {
    case OBJECTS     -> result.objects().forEach(this::handle);
    case PROJECTION,
         AGGREGATION -> result.rows().forEach(row -> log(row.get("n") + " / " + row.get(0)));
    case COUNT       -> log("count = " + result.count());
    }
}
```

Either way the same three rules hold:

- **Close the result.** It holds a cursor or a lease until you do — hence
  try-with-resources in every example here.
- **Accessors are shape-guarded.** `objects()` on an aggregation throws; check
  `shape()` when the query is not a literal in front of you.
- **Refusals arrive as `IOException`** with the `Diagnostic` on
  `QueryException.getDiagnostic()`, and on the resource in `resource.getErrors()`.

## Saved queries

Queries are EMF objects, so a backend can store them. `named(...)` marks a query for the
catalog; executing it upserts it by name and then runs it:

```java
Query adults = QueryBuilder.from(personClass)
        .where(path(age).ge(param("minAge")))
        .parameter("minAge", null)
        .named("adults")
        .build();
repository.find(adults, Map.of("minAge", 40), null);      // persists, then executes

// later, anywhere against the same store:
try (QueryResult r = repository.find("adults", Map.of("minAge", 50), null)) { … }
```

- Last write wins on the name. A `saveQuery` without a name is refused, and an unknown
  name on execution is an `IOException`.
- The stored payload is the query's XMI document. Metamodel references (root type, path
  segments) are stored as nsURI hrefs and resolve against the resource set's package
  registry on load — **the model package must be registered**, otherwise execution is
  refused with a diagnostic saying so.
- Storage is the Mongo collection `fennec.queries` (`_id` = name) or the JPA table
  `FENNEC_QUERIES`, created on first use and outside your mapped model. The memory
  backend has no store, and therefore no catalog.
- One current limitation when executing by name through a repository: the catalog has no
  load-back API for the root type, so the repository must already know it — either
  because the saving query ran through this instance, or via the per-call option
  `RepositoryConstants.OPTION_QUERY_ROOT`.

## Write commands

The query language does not write. Writes are commands from
`org.eclipse.fennec.model.command`, executed through the repository (or a
`CommandResource`), and they reuse queries only as **selectors**:

```java
// insert — the command owns its payload, executed on copies
InsertCommand insert = CommandFactory.eINSTANCE.createInsertCommand();
insert.getObjects().add(person);
long inserted = repository.execute(insert);

// delete by selector
DeleteCommand delete = CommandFactory.eINSTANCE.createDeleteCommand();
delete.setSelector(QueryBuilder.from(personClass).where(path(age).ge(40)).build());
long deleted = repository.execute(delete);

// update by selector: a ChangeSet template applied per match
UpdateCommand update = CommandFactory.eINSTANCE.createUpdateCommand();
update.setSelector(QueryBuilder.from(personClass).where(path(name).eq("Bob")).build());
ChangeSet template = StreamFactory.eINSTANCE.createChangeSet();
ChangeEntry setAge = StreamFactory.eINSTANCE.createChangeEntry();
setAge.setKind(DeltaKind.SET);
setAge.setFeatureId(personClass.getFeatureID(age));   // EMF feature id of the root type
setAge.setValueNew("41");                             // EMF string literal
template.getEntries().add(setAge);
update.setTemplate(template);
long updated = repository.execute(update);
```

- **Insert** is the resource's save semantics over copies of the payload.
- **Delete** is selector-scoped. JPA removes matches children-first, for containment FK
  safety; Mongo uses `deleteMany(filter)`.
- **Update** applies the template per match. Entry coordinates address features of the
  selector's root type (`featureId` = the EMF feature id, values as EMF string literals).
  Supported kinds are `SET`/`UNSET` on single-valued attributes and `ADD`/`REMOVE`/`MOVE`
  on many-valued ones. Reference patching and map kinds are refused with a diagnostic,
  and `valueOld` is *not* evaluated as an optimistic guard.
- **Selectors must be plain filters.** Projection, aggregation, ordering or paging on a
  selector is refused.

## Capabilities and refusals

Every backend declares what it serves (`QueryProcessor.capabilities()`), and validation
reports each violation as a `Diagnostic` ERROR before anything runs.

| Feature | JPA (JPQL) | MongoDB | Notes |
|---|---|---|---|
| Comparisons, `isNull`, `between`, `in` | ✅ | ✅ | Mongo `isNull` = missing-or-null |
| Logic trees (n-ary and/or/not, nested) | ✅ | ✅ | |
| String matching + case-insensitive flag | ✅ `LOWER` both sides | ✅ regex `i` | `like` wildcards translated |
| Fuzzy matching | ❌ refused | ❌ refused | search backends only; memory defines the semantics |
| String functions, basic and extended | ✅ | ✅ `$expr` | Mongo: root-based paths only |
| Arithmetic, numeric and temporal functions | ✅ | ✅ `$expr` | `div` is floating-point; temporal is UTC-normative |
| Field-to-field comparison | ✅ | ✅ `$expr` | root-based paths on Mongo |
| Quantifiers (`any` / `all`) | ✅ correlated `EXISTS`, nested | ⚠️ embedded collections only (code 100) | vacuous truth on empty |
| Path navigation | ✅ joins, unlimited depth | ⚠️ within the document, unlimited (code 100) | |
| Collection counts, filtered counts | ✅ `SIZE` / correlated subquery | ✅ `$size` / `$size($filter)` | |
| Type check and cast | ✅ `TREAT` | ✅ type discriminator | |
| Map access (`mapValue`) | ✅ correlated subselect | ✅ sub-document field | constant key on both; see [Maps](#maps) |
| Map value as group key | ✅ via an outer join | ✅ | owners without the key group under `null` |
| Quantifier over a map | ✅ over the entry table | ❌ refused (code 103) | prefer `mapValue` for portability |
| Sort / top / skip / count | ✅ | ✅ | |
| Sort by expression | ✅ | ❌ refused | use `aliasRef` on row shapes |
| DISTINCT | ✅ incl. whole entities | ⚠️ projection only (code 101) | |
| Projection, grouping, aggregates, COUNT_DISTINCT | ✅ | ✅ | group keys alias-addressable |
| Projection of an expression | ✅ inline in the select list | ✅ `$project` expression | alias mandatory |
| Expression group keys / aggregate sources | ✅ re-rendered inline | ✅ `$group` accumulators | |
| Multi-stage pipelines | ⚠️ filter/compute/having, and paging **after** the grouping | ✅ native, in stage order | JPA refuses paging before the grouping and a second grouping |
| `expand` fetch hints | ✅ `LEFT JOIN FETCH` (depth 1) | ❌ | |
| Geo (`geoWithin`, `geoDistance`) | ❌ | ✅ 2dsphere | see [Geo](#geo) |
| Parameters | ✅ | ✅ | model-level |
| Relevance scores | ❌ | ❌ | search backends |

The MongoDB flavours — MongoDB itself, FerretDB and the DocumentDB gateway — declare the
**same** query capabilities; the full query TCK passes against all three. They differ
outside the query plane: only the DocumentDB gateway serves multi-document transactions.

Diagnostic codes, source `org.eclipse.fennec.persistence.query`:

| Code | Meaning |
|------|---------|
| 1 | the backend does not support a feature the query uses |
| 2 | feature path deeper than the backend allows |
| 3 | arithmetic division by a literal zero |
| 4 | malformed aggregate (both or, except for COUNT, neither of path/source) |
| 5 | an output-column sort key on a query that is not row-shaped |
| 6 | malformed geo structure |
| 7 | malformed string match (fuzzy parameters on a non-fuzzy kind, budget out of range) |
| 8 | malformed map access (path does not end in a map, or a non-constant key) |
| 9 | malformed projection (both or neither of path/key, or an expression column without an alias) |
| 100 | Mongo: cross-document or non-embedded path |
| 101 | Mongo: distinct without a projection |
| 102 | Mongo: invalid map key |
| 103 | Mongo: quantifier over a map |

## Querying in memory

`MemoryQueryProcessor` is a third backend with **no store**: it evaluates a query against
objects you hand it.

```java
QueryResult result = MemoryQueries.execute(query, resource.getContents(), parameters);

// or in two steps, to reuse the plan
MemoryQueryPlan plan = MemoryQueries.translate(query, parameters);
plan.execute(objects);
```

Its capability set is near-complete — everything except the reserved temporal features —
and values stay in EMF space. It has two jobs: it is the **reference oracle** for the TCK
(the same conformance corpus runs against every database binding *and* in memory, and the
results must be identical), and it is the in-memory execution option for consumers that
build on the IR, such as the OData layer, which can target `jpa`, `mongo` or `memory`
through one SPI. Its comparison semantics mirror the databases: comparisons with null are
false, `isNull` probes explicitly, `all` is vacuously true on empty collections.

## Derived references

A derived reference can compute its value through the query stack instead of in memory.
Annotate the feature with an OCL derivation and register the query-backed setting
delegate; `person.eGet(adultFriends)` then routes per access:

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

- An owner loaded from XMI (or any non-queryable resource) evaluates locally, with
  standard OCL semantics.
- An owner loaded from a JPA or Mongo resource, whose reference proxies are unresolved,
  triggers one native backend query correlated by the owner ids — the proxies are never
  resolved.
- Containment or already-resolved references evaluate locally; the data is there.

Recognised shape: `self.<manyReference>->select(v | <predicate>)` with the predicate in
the supported expression subset. Other derivations fall back to the plain OCL delegate,
and are refused in a runtime without an OCL engine. Results are unmodifiable and
**volatile** — every access queries, so snapshot the list in hot loops.

## How it fits together

`validate → translate → execute`. `ExpressionAnalyzer` walks the envelope and the
expression trees into a shared analysis, `QueryValidator` checks it against the backend's
declared capabilities, and only then does the backend build its plan: JPQL with named
parameters (identifiers are model names, every value is a parameter — injection-safe), or
a Mongo filter/pipeline with row metadata. The resources execute and stream.

The OSGi wiring: all processors are DS components registered as `QueryProcessor` services
carrying `persistence.query.backend` (`jpa`, `mongo`, `memory`). The `jpa://` whiteboard
resource factory holds an optional greedy reference to the `jpa` one and passes it to
every resource it creates — no service means the resource's local default, and a
higher-ranked service wins for subsequently created resources.

Conformance is measured, not asserted: the TCK query and command suites run against
H2/EclipseLink and MongoDB in all four id-type bindings, and differentially against the
memory oracle.
