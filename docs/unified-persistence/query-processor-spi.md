# QueryProcessor SPI & Capability model

**Status:** design proposal (2026-07-22). Companion to `concept.md` §3.1 and §14.
Turns the existing, backend-neutral query metamodel
(`fennec.common.models/org.eclipse.fennec.query.model`) into a per-backend interpreter SPI
serving **both** JPA/EclipseLink and MongoDB, with an honest capability declaration instead of
silent in-memory post-filtering.

Scope of the first stage (decided 2026-07-22): **full** — `where` + `sort` + `skip`/`limit` +
`count` + `distinct` + type filter + **projection** + **aggregation & groupBy**; **both backends
in parallel**.

---

## 1. Principles

- **P-A — One canonical query IR.** `query.model` (`Query`) is the single source model. No
  backend-native query type ever leaks into user code. (concept P2: everything is EMF.)
- **P-B — Translate, then execute.** A `QueryProcessor` *translates* a `Query` into a
  backend-typed `QueryPlan`; the backend resource *executes* the plan and maps rows back to EMF.
  Translation is pure and unit-testable without a live database.
- **P-C — Capabilities are declared, violations are diagnosed.** Every processor publishes the
  set of `QueryFeature`s it serves natively. A query using an unsupported feature fails
  `validate()` with an EMF `Diagnostic` — **never** a silent in-memory fallback (concept §3.1,
  aligns with issue #19).
- **P-D — Type conversion lives in one place.** Comparator values are `EString` in the model;
  conversion to the target feature's `EDataType` happens once, in a shared helper backed by the
  core `ConverterService` — not per backend.
- **P-E — Streaming-first results.** Whole-object results are a `Stream<EObject>` and reuse the
  existing `StreamingResource`/`PersistencePushStreams` machinery. Projection/aggregation results
  are streamed rows.

---

## 2. Module layout

| Bundle | Content |
|---|---|
| `org.eclipse.fennec.persistence.query` (**new**) | The SPI: `QueryProcessor`, `QueryPlan`, `QueryShape`, `QueryCapabilities`, `QueryFeature`, `QueryContext`, `QueryResult`/`QueryResultRow`, `QueryException`, `QueryableResource`, shared `QueryValues` (typed conversion) + `FeaturePathResolver`. Depends on `query.model` + core `persistence`. |
| `org.eclipse.fennec.persistence.query.builder` (**new**, optional) | Fluent `QueryBuilder` → `Query` EObject. Separated so the SPI stays dependency-light. |
| `org.eclipse.fennec.persistence.eclipselink` | `JpaQueryProcessor implements QueryProcessor` (→ Criteria), `JpaQueryPlan`. Execution in `JPAResourceImpl`. |
| `org.eclipse.fennec.persistence.mongo` | `MongoQueryProcessor implements QueryProcessor` (→ aggregation pipeline / find), `MongoQueryPlan`. Execution in `MongoResourceImpl`. |
| `org.eclipse.fennec.persistence.tck` | Backend-neutral query conformance suite. |

The SPI deliberately sits in its own bundle rather than in core `persistence`, so consumers that
do not query are not forced to resolve `query.model`.

---

## 3. Capability model

```java
package org.eclipse.fennec.persistence.query;

/** A single, backend-neutral query capability. Declared per backend, checked per query. */
public enum QueryFeature {
    // --- predicates (where) ---
    WHERE_EQ, WHERE_COMPARISON,        // Eq / Lt,Lte,Gt,Gte
    WHERE_STRING_MATCH,                // Contains, StartWith, EndsWith, Like
    WHERE_RANGE,                       // IsInRange
    WHERE_DATE,                        // IsBefore/After(OrEqual)
    WHERE_ENUM,                        // IsLiteral
    WHERE_BOOL,                        // IsBool
    LOGICAL_AND, LOGICAL_OR, LOGICAL_NOT,

    // --- shaping ---
    SORT, LIMIT, SKIP, DISTINCT, COUNT,
    PROJECTION,                        // subject/FeaturePath selection of scalar fields
    PROJECTION_NESTED,                 // projection through references (joins)

    // --- aggregation ---
    GROUP_BY, AGG_AVG, AGG_MIN, AGG_MAX, AGG_SUM, AGG_COUNT,

    // --- operations on values/paths ---
    OP_TO_LOWER, OP_TO_UPPER,          // StringOperation
    OP_AVERAGE,                        // NumberOperation (subject-level Average)

    // --- structural ---
    FEATUREPATH_NESTED,                // where/sort over a multi-segment FeaturePath (join)
    TYPE_FILTER, TYPE_FILTER_STRICT,   // QObject.rootEClass polymorphic / exact type filter
    PARAMETERS,                        // bound placeholders (prepared queries)

    // --- future (concept §14) ---
    AS_OF, SERIES_RANGE
}
```

```java
public interface QueryCapabilities {
    boolean supports(QueryFeature feature);
    java.util.Set<QueryFeature> supported();

    /** Max FeaturePath depth for where/sort/projection joins; -1 = unlimited, 1 = local only. */
    int maxFeaturePathDepth();
}
```

**Rationale.** The enum is intentionally fine-grained: Mongo can do `WHERE_*`/`SORT`/`DISTINCT`
through `find`, but `GROUP_BY`/`AGG_*` only through the aggregation pipeline, and
`PROJECTION_NESTED`/`FEATUREPATH_NESTED` (joins across references) are limited (`$lookup`).
JPA/Criteria covers essentially all of them but with its own join-depth realities. Declaring at
this granularity lets `validate()` give a precise, actionable diagnostic ("`AVG` over a nested
FeaturePath is not supported by backend `mongo`").

---

## 4. The SPI

```java
package org.eclipse.fennec.persistence.query;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.model.query.Query;

/**
 * Translates a canonical {@link Query} into a backend-typed {@link QueryPlan}.
 * Registered as an OSGi service with property {@code persistence.query.backend=<id>}.
 */
public interface QueryProcessor {

    /** Stable backend id, e.g. {@code "mongo"} or {@code "jpa"}. */
    String backend();

    /** Features this processor serves natively. */
    QueryCapabilities capabilities();

    /**
     * Validates a query against {@link #capabilities()} for the given root type.
     * @return a {@link Diagnostic} tree; {@link Diagnostic#ERROR} entries make the query
     *         non-executable (each names the offending {@link QueryFeature} and model element).
     *         {@link Diagnostic#OK} means the query translates natively.
     */
    Diagnostic validate(Query query, EClass rootEClass);

    /**
     * Translates the query. Callers must {@link #validate(Query, EClass) validate} first;
     * a processor may assume a validated query and throw {@link QueryException} otherwise.
     */
    QueryPlan translate(Query query, QueryContext context) throws QueryException;
}
```

```java
/** Everything translation needs that is not in the {@link Query} itself. */
public interface QueryContext {
    EClass rootEClass();
    ConverterService converter();           // core type conversion (P-D)
    java.util.Map<String, Object> parameters();  // bound placeholders (§7)
    java.util.Map<?, ?> options();          // page size, batch size, read options
}
```

```java
/** Backend-typed, executable translation. Marker + shape; concrete plans add native payload. */
public interface QueryPlan {
    Query source();
    QueryShape shape();
}

public enum QueryShape { OBJECTS, PROJECTION, AGGREGATION, COUNT }
```

Concrete plans live in the backend bundles and are only ever handled by their own resource:

```java
// mongo bundle
public final class MongoQueryPlan implements QueryPlan {
    List<org.bson.conversions.Bson> pipeline();  // find-filter path OR aggregation pipeline
    boolean aggregation();
    // shape(), source() ...
}

// eclipselink bundle
public final class JpaQueryPlan implements QueryPlan {
    jakarta.persistence.criteria.CriteriaQuery<?> criteria();
    List<ParameterBinding> bindings();
    // shape(), source() ...
}
```

**Why translate→execute, not execute-in-processor:** execution needs the live backend handle
(`MongoCollection`, `EntityManager`) and reuses the resource's existing BSON/row→EObject mapping.
Keeping execution in the resource avoids duplicating that mapping and keeps `translate()` a pure
function — the unit-test seam (assert the produced pipeline/criteria, no DB required).

---

## 5. Result model

```java
public interface QueryResult extends AutoCloseable {
    QueryShape shape();

    /** OBJECTS shape: whole entities. Must be closed by the caller (backend cursor). */
    java.util.stream.Stream<org.eclipse.emf.ecore.EObject> objects();

    /** PROJECTION / AGGREGATION shape: streamed rows. */
    java.util.stream.Stream<QueryResultRow> rows();

    /** COUNT shape. */
    long count();

    @Override void close();
}

/** One projection/aggregation row: cells addressed by subject alias or ordinal. */
public interface QueryResultRow {
    Object get(String alias);
    Object get(int index);
    java.util.List<Object> values();
}
```

**Projection result type — the hard decision (concept §3.1).** v1 returns lightweight *tuple
rows* (`QueryResultRow`), which is the natural shape for `groupBy`/aggregation and avoids
minting a dynamic `EClass` per query. Materialising rows into a dynamic `EClass` (or a
user-supplied target `EClass` via `QSubject.aliasFeature`) is a **follow-up**, layered on top of
rows — it does not change the SPI.

---

## 6. Resource entry point

```java
package org.eclipse.fennec.persistence.query;   // in the query bundle, NOT core

/** Optional capability of a PersistenceResource: execute canonical queries. */
public interface QueryableResource {
    QueryResult query(Query query) throws java.io.IOException;
    QueryResult query(Query query, java.util.Map<String, Object> parameters,
                      java.util.Map<?, ?> options) throws java.io.IOException;
}
```

`QueryableResource` lives in the **query bundle**, not the core `persistence` API — otherwise
core would depend on `query.model` and the query bundle (which depends on core) would form a
cycle. Resources implement it as an add-on capability, exactly like `StreamingResource`.

`MongoResourceImpl` and `JPAResourceImpl` implement `QueryableResource`. Flow:

1. resource resolves its `QueryProcessor` (OSGi `@Reference` filtered by `persistence.query.backend`).
2. `Diagnostic d = processor.validate(query, root)` → on `ERROR`, throw `QueryException(d)`
   (surfaced as `Resource` errors, consistent with issue #19 diagnostics).
3. `QueryPlan plan = processor.translate(query, ctx)`.
4. resource executes its concrete plan type and returns a `QueryResult` (streamed).

OBJECTS-shape results plug straight into `PersistencePushStreams` for a `PushStream<EObject>`.

---

## 7. Closing the model gaps (before/with implementation)

These are the `query.model` gaps from `concept.md` §3.1, with the chosen resolution:

1. **Typed comparator values.** Model keeps `EString value`; `QueryValues` (SPI, shared) converts
   to the target feature's `EDataType` via `ConverterService` at translate time. No model change.
2. **Parameters / placeholders.** Short term: a placeholder convention in comparator values
   (`":name"`) resolved from `QueryContext.parameters()`; declared capability `PARAMETERS`.
   Model-level `Parameter` EClass is a **later** enhancement (keeps `saveQuery` prepared-query
   ergonomics clean).
3. **Projection result type.** Tuple rows now; dynamic-EClass materialisation later (§5).
4. **Capability declaration + Diagnostics.** `QueryCapabilities` + `validate()` (§3, §4).
5. **Aggregation breadth.** Model has only `Average`. Add `Min`/`Max`/`Sum`/`Count` as
   `NumberOperation` subclasses (small `query.ecore` extension) to make `AGG_*` meaningful.
6. **FeaturePath joins.** `FeaturePath` (multi-segment) drives where/sort/projection across
   references; `FeaturePathResolver` (SPI helper) maps segments to backend joins; depth honoured
   by `maxFeaturePathDepth()`.

---

## 8. Testing

- **Translation unit tests** per backend: build a `Query`, assert the produced pipeline/criteria
  (no DB). This is the primary correctness seam.
- **TCK conformance**: extend `AbstractPersistenceTCK` with a query suite run against JPA and
  Mongo bindings (mirrors the existing int/String-id matrix), plus the capability-refusal cases
  (assert an `ERROR` diagnostic, not an empty result).

---

## 9. Open decisions

- **D1** — Dynamic-EClass projection materialisation: opt-in per query, or always rows? (proposal:
  rows default, EClass opt-in via target type.)
- **D2** — Parameter model: convention-only vs. `Parameter` EClass in `query.ecore` (proposal:
  convention v1, model later).
- **D3** — Cross-reference joins in Mongo: `$lookup` (native, limited) vs. declared unsupported
  beyond depth 1 (proposal: start depth 1, declare the limit honestly).
- **D4** — `saveQuery` execution: reuse the same processor path for persisted queries (yes, P2).
