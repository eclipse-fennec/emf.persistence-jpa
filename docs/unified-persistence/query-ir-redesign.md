# Query IR redesign — the Fennec Expression Model

**Status:** implemented (2026-07-24, #47–#58; user guide: `query-usage.md`). Deviations from
the plan are recorded in the issues — notably: the legacy retirement was pulled forward
(maintainer call, no transitional names shipped), builder v2 is `Expressions` +
`QueryBuilder`, group keys are alias-addressable, JPA delete removes children-first, and
the OclEvaluator differential tests stay blocked until fennec-odata publishes its query
bundle (#56); the extraction of the OclEvaluator into an OData-neutral artifact is
requested upstream (eclipse-fennec/emf.odata#14) — until then the differential tests stay
deactivated by decision (no `-SNAPSHOT` test dependency on the OData repo). 2026-07-28:
the dead v1 capability literals (`WHERE_DATE`, `WHERE_ENUM`, `WHERE_BOOL`, `OP_TO_LOWER`,
`OP_TO_UPPER`, `OP_AVERAGE`, `TYPE_FILTER_STRICT`) were removed from `QueryFeature`,
completing the M11 vocabulary retirement; decisions D1–D5 are closed (§8); Mongo serves
`STRING_FUNCTIONS` and the new `FIELD_TO_FIELD` capability via `$expr` (null-guarded, SQL
semantics; root-based paths only); persisted queries (`saveQuery`, concept §14 P2) execute
on both backends — XMI payload in a per-backend catalog, named execution via
`QueryableResource.query(name, …)` (see `query-usage.md`). Originally the approved concept
of 2026-07-23. Supersedes the *IR part* of
`query-processor-spi.md`; the SPI layer described there (QueryProcessor, capabilities,
diagnostics, QueryResult, TCK) **stays** and is re-targeted. Companion: `concept.md`
§3.1/§5/§14, `query-usage.md` (to be rewritten on completion).

---

## 1. Why the redesign

The v1 IR (`query.ecore`, the where-list/comparator model) was adopted as a "first shot"
and does not carry:

- **No expression trees.** `where` is a flat list with And/Or/Not entry types; grouping
  like `(a OR b) AND (c OR d)` is inexpressible — the implemented "chaining semantics"
  is a workaround, not a design.
- **Missing basics:** no `ne`, no `isNull`, no `in`, no quantifiers over multi-valued
  references (any/all), sort cannot address nested paths, values are untyped strings,
  parameters exist only as a `":name"` string convention.
- **Dead constructs:** `suitableForType`, `execute()` EOperations, `isExclude`,
  `aliasFeature`, multi-`from` — unspecified and unimplemented.
- **Ecosystem duplication:** fennec-odata independently chose the m2x **OCL AST**
  (`org.eclipse.fennec.m2x.model.ocl`) as its internal predicate IR — a strictly more
  expressive model with production translators (`OclToCriteriaTranslator` incl.
  correlated EXISTS, `OclEvaluator` as normative in-memory semantics, `apply.ecore`
  aggregation pipelines). Two competing IRs in one ecosystem, and the weaker one is ours.

Every gap we would patch in `query.ecore` (trees, quantifiers, ne/null/in, …) re-invents
OCL badly. The redesign replaces the IR while keeping everything that proved itself.

## 2. Decision record

| # | Decision | Rationale |
|---|---|---|
| R1 | **Own expression model** ("Fennec Expression Model"), structurally informed by Essential OCL, **no type-level dependency** on `ocl.ecore` | "Own world": own evolution, no OCL/m2x version coupling, no misleading naming in persistence/OData contexts |
| R2 | **No EMF inheritance from OCL** | Subclassing couples harder (instances *are* OclExpressions), cannot subtract constructs (containments still admit any OCL child), and OCL producers never emit the subtypes — worst of both worlds |
| R3 | **Curated subset, not a copy** | ~15–20 classifiers vs ~40; `LetExp`/`MessageExp`/`StateExp`/`IterateExp`/tuple literals simply do not exist — illegal queries become unbuildable instead of capability-refused everywhere |
| R4 | **Home = this repo** (not fennec.common.models) | Development velocity (model + processors + TCK in one repo, proven); the command model couples to the stream/patch model which lives here; common.models stays the home of *stable* base models — promotion later remains possible because of R5 |
| R5 | **Repo-neutral naming** | BSN `org.eclipse.fennec.expression.model`, package `org.eclipse.fennec.model.expression` — no "persistence" in the name; consumers (OData) depend on a neutral artifact; a later move to common.models breaks nobody |
| R6 | **Bidirectional OCL bridge, asymmetric** | `Expr → OCL` is **total** (we are a semantic subset) — enables reusing the existing `OclEvaluator` as reference evaluator without porting; `OCL → Expr` is **partial** (subset, refusal with Diagnostics — consistent with the capability philosophy) |
| R7 | **Bridge lives here** (not m2x) | m2x stays 100% untouched ("damit OCL klappt"); the bridge consumes the OCL model binary (as fennec-odata does today) and sits next to the TCK where the differential tests live |
| R8 | **CUD = commands, not query vocabulary** | Per `concept.md` §14: Delete = selector; **Update = selector + ChangeSet template** (`UpdateCommand`); Insert = payload command. The query language stays read-only by design |
| R9 | **SPI, capabilities, diagnostics, builder concept, TCK stay** | They are IR-agnostic and proven; only the vocabulary and the tree walker change |
| R10 | **Migration path for OData is theirs, staged** | Phase 1: their parser keeps emitting OCL, the `OCL → Expr` bridge feeds our IR; Phase 2: `ODataToOclBuilder` becomes `ODataToExprBuilder` (their grammar is their own). No big bang; tracked in the fennec-odata repo |

## 3. The expression model (`expression.ecore`)

Bundle `org.eclipse.fennec.expression.model`, package `org.eclipse.fennec.model.expression`,
nsURI `https://eclipse.org/fennec/expression/1.0.0`, nsPrefix `expr`.
Design style: **explicit constructs instead of generic operation calls** — a `Comparison`
with an operator enum, not `OperationCallExp(referredOperation="=")`. Type-safe to build,
trivial to analyze, self-documenting. Essential OCL remains the documented semantic
reference for each construct.

### 3.1 Blessed construct catalogue (v1)

| Construct | Shape | Notes / semantic reference |
|---|---|---|
| `Expression` | abstract root | |
| `And` / `Or` | n-ary (`operands: Expression[2..*]`) | real trees, flattened n-ary for translator ergonomics |
| `Not` | unary | |
| `Comparison` | `left`, `right: Expression`, `operator: {EQ, NE, LT, LE, GT, GE}` | OCL `=`, `<>`, `<`… |
| `IsNull` | `source: Expression`, `negated: boolean` | explicit — SQL `IS NULL` vs `= NULL` pitfall |
| `Between` | `source`, `lower`, `upper`, `lowerIncluded`, `upperIncluded` | replaces `IsInRange` |
| `In` | `source: Expression`, `values: Expression[*]` | OCL `includes`; values are literals or parameters |
| `StringMatch` | `source`, `pattern: Expression`, `kind: {CONTAINS, STARTS_WITH, ENDS_WITH, LIKE}`, `caseInsensitive: boolean` | the `caseInsensitive` flag replaces the toLower/toUpper predicate hack |
| `Exists` / `ForAll` | `source: PropertyPath` (multi-valued), `variable: Variable`, `predicate: Expression` | OCL `exists`/`forAll` iterators; JPA → correlated EXISTS, Mongo → `$elemMatch` (embedded only) |
| `PropertyPath` | `segments: EStructuralFeature[*]`, `base: Variable[0..1]` | navigation; `base` scopes into an iterator variable |
| `Variable` / `VariableRef` | named | iterator scoping |
| `ParameterRef` | `name: String` | **first-class parameters** — retires the `":name"` string convention |
| Literals | `StringLiteral`, `IntegerLiteral(long)`, `RealLiteral(double)`, `BooleanLiteral`, `NullLiteral`, `EnumLiteral(name)`, `TemporalLiteral(iso8601, kind: {DATE, TIME, DATE_TIME, INSTANT})`, `GuidLiteral(canonical text)`, `DurationLiteral(iso8601)` | typed values — retires string-typed comparator values. Guid/Duration (issue #83) resolve against the target feature's type at translation: UUID/String resp. `java.time.Duration`/millis-Long/String; the builder boxes `UUID` and `Duration` values automatically |
| `StringFunction` | `kind: {TO_LOWER, TO_UPPER, TRIM, LENGTH}`, `source` | v1 minimal set |
| `Arithmetic` | `left`, `right: Expression`, `operator: {ADD, SUB, MUL, DIV, MOD}` | issue #76 (OData pushdown migration). Type-preserving Java promotion — except `DIV`, which is **always floating-point** (integer truncation deliberately not modelled; JPQL renders `* 1.0 /`, Mongo `$divide` is FP anyway). Division by a **literal zero** is refused statically (`QueryValidator.CODE_DIVISION_BY_ZERO`); a runtime zero surfaces the backend's error — the memory oracle yields `null` (comparison false) instead. OCL `+ - * / mod`; the truncating OCL `div` stays outside the subset |
| `Negate` | `operand: Expression` | OCL unary minus |
| `Concat` | `parts: Expression[2..*]` | issue #77 (OData concat). N-ary; binary OCL chains flatten on the bridge. A null part poisons the result (Mongo `$concat` semantics) |
| `IndexOf` | `source`, `search: Expression` | issue #77. **0-based, -1 when absent** (OData/Java) — JPQL renders `LOCATE(…) - 1`, Mongo `$indexOfCP` matches natively |
| `Substring` | `source`, `start: Expression`, `length: Expression[0..1]` | issue #77, [OData-URL] 5.1.1.7: 0-based; negative start counts from the end (clamped to 0), start beyond the end and negative length yield `""`. JPQL clamps with one flat CASE over LENGTH (EclipseLink mistranslates nested CASE), Mongo via `$substrCP` + `$cond` clamping |
| `NumericFunction` | `kind: {ROUND, FLOOR, CEILING}`, `source` | issue #78. **ROUND is half away from zero** (OData); result integral. JPQL `ROUND(x, 0)`/`FLOOR`/`CEILING`; Mongo `$floor`/`$ceil` — `$round` rounds half to even, so ROUND is emulated via `$cond` + `$floor(x+0.5)`/`$ceil(x-0.5)` |
| `TemporalFunction` | `kind: {YEAR, MONTH, DAY, HOUR, MINUTE, SECOND}`, `source` | issue #79. **UTC-normative**: parts are extracted from the value as a UTC instant (BSON dates are UTC natively; the zone-less SQL TIMESTAMP carries the writing session's wall-clock — run UTC). SECOND is integral (JPQL wraps `FLOOR(EXTRACT(SECOND …))` — EclipseLink types it Double); time parts of date-only values are 0. Deliberately without `date()`/`time()` (ISO-string divergence). Requires native BSON dates on Mongo (emf.codec#97) |
| `TypeCheck` | `source: PropertyPath[0..1]`, `type: EClass` | issue #80 (OData isof, OCL oclIsKindOf). **Kind-of semantics**; unset source tests the query root. JPQL `TYPE(x) IN (concrete subtypes by entity name)` — the dynamic Java classes are deliberately flat, entity names sidestep Java assignability. Mongo refuses until documents carry a type discriminator (`_eType` is reserved but unwritten) |
| `PropertyPath.castBase` | `EClass[0..1]` on PropertyPath | issue #80, the v1 cut matching OData's `Ns.SubType/prop` limit (full cast segments stay additive). JPQL `TREAT(e AS Sub).…`; non-instances yield null (verified EclipseLink three-valued behaviour inside OR); memory mirrors with a null short-circuit |

**Deliberately absent in v1** (decision list — add only with a driving use case):
`If`/`Let`, collection operations
beyond `In`/`Exists`/`ForAll` (`Select`/`Collect`/`IterateExp`), tuple/map literals.
The model can grow additively; the capability mechanism covers backend divergence.
Arithmetic left this list with issue #76, the extended string set with #77, the
numeric functions with #78, the temporal parts with #79, type test/cast with #80 —
the remaining OData-gap constructs are tracked in issues #81–#84.

### 3.2 Capability vocabulary (new `QueryFeature` terms)

The analyzer walks the expression tree; features are declared per backend as today.
Sketch of the new vocabulary and the expected initial matrix:

| Feature | JPA (JPQL) | Mongo find | Notes |
|---|---|---|---|
| COMPARISON (EQ/NE/ordering) | ✅ | ✅ | `$ne` |
| LOGICAL trees (And/Or/Not, nested) | ✅ | ✅ | real grouping now |
| IS_NULL, BETWEEN, IN | ✅ | ✅ | `$exists`/`$in` |
| STRING_MATCH (+CASE_INSENSITIVE) | ✅ | ✅ regex | |
| STRING_FUNCTIONS | ✅ | ⚠️ partial | LENGTH needs `$expr` |
| ARITHMETIC | ✅ | ✅ `$expr` | `$add/$subtract/$multiply/$divide/$mod`; root paths only (like FIELD_TO_FIELD) |
| STRING_FUNCTIONS_EXTENDED | ✅ | ✅ `$expr` | Concat/IndexOf/Substring — `$concat/$indexOfCP/$substrCP`; root paths only |
| NUMERIC_FUNCTIONS | ✅ | ✅ `$expr` | ROUND/FLOOR/CEILING — ROUND emulated half-away-from-zero (`$round` is half-to-even); root paths only |
| TEMPORAL_FUNCTIONS | ✅ EXTRACT | ✅ `$expr` | `$year..$second` on native BSON dates (emf.codec#97); UTC-normative; root paths only |
| TYPE_CAST / TYPE_CHECK | ✅ TREAT / TYPE IN | ❌ refused | Mongo needs a stored type discriminator first (`_eType` reserved) |
| EXISTS / FOR_ALL | ✅ EXISTS subquery | ⚠️ embedded only (`$elemMatch`) | cross-document refused |
| PATH navigation depth | ✅ joins, −1 | ⚠️ containment only, −1 | as today |
| PARAMETERS | ✅ | ✅ | now model-level |
| GROUP_BY / AGG_* / pipeline stages | ✅ | ✅ pipeline | envelope, §4 |

## 4. The query envelope (`query.ecore`, rebuilt)

Bundle **`org.eclipse.fennec.query.model` keeps its BSN**; the content is replaced:

```
Query
  from        : EClass                       — root type (single; the type filter)
  predicate   : Expression [0..1]            — containment
  orderBy     : OrderBy[*]                   — { path: PropertyPath, direction }  (paths, not bare features)
  select      : Selection[*]                 — { path: PropertyPath, alias }      (projection)
  apply       : Pipeline [0..1]              — aggregation pipeline (§4.1)
  expand      : PropertyPath[*]              — fetch hints (references to materialise)
  top, skip   : int;  distinct, countOnly : boolean
  parameters  : ParameterDecl[*]             — { name, typeHint }  (declared, bindable)
  name, saveQuery                            — persisted queries (P2)
```

### 4.1 Aggregation as a pipeline

Modelled after fennec-odata's `apply.ecore` (compositional, strictly more powerful than
the v1 groupBy+ops): `Pipeline { stages: Stage[*] }` with v1 stages `Filter(predicate)`,
`GroupBy(paths, aggregates: Aggregate[*]{path, method: {SUM, MIN, MAX, AVG, COUNT,
COUNT_DISTINCT}, alias})`, `Top(n)`, `Skip(n)`. Further stages (`Compute`, `Concat`,
`BottomTop`) are additive later. Structural convergence with OData's `$apply` is
intentional — a later shared model is possible, not required.

## 5. The command model (`command.ecore`) — CUD

Per `concept.md` §14, own bundle `org.eclipse.fennec.command.model`:

```
Command (abstract)
InsertCommand { objects: EObject[*] (containment) }
DeleteCommand { selector: Query }
UpdateCommand { selector: Query, template: ChangeSet }   ← stream model reference
```

Execution contract (`CommandResource` analogous to `QueryableResource`): Insert = save
semantics, Delete = selector-scoped delete (Mongo `deleteMany(filter)`, JPA
`DELETE FROM … WHERE …`), both v1. **Update is modelled in v1 but executes only once the
patch-apply engine exists** (concept §18.1 pairs them); until then `translate` refuses it
with a diagnostic. Prerequisite: `fennec-stream.ecore` (today a draft under
`docs/unified-persistence/model/`) is promoted to a real model bundle — the command model
is its first consumer.

## 6. The OCL bridge (`org.eclipse.fennec.expression.ocl`)

Consumes `org.eclipse.fennec.m2x.ocl.model` **binary** (like fennec-odata today); m2x is
not touched.

- `ExprToOcl` — **total** mapping (every blessed construct has an OCL form; iterators →
  `exists`/`forAll`, `In` → `includes`, `StringMatch` → the OCL string ops with
  `toLower` wrapping for CI). Primary use: run the existing `OclEvaluator` as the
  **reference evaluator** in TCK differential tests (JPA vs Mongo vs reference).
- `OclToExpr` — **partial** mapping (the blessed subset; anything else → Diagnostic
  ERROR naming the OCL construct). Primary use: OData phase 1, m2x-parsed OCL text.

## 7. Migration plan

Everything below preserves the public execution API: `QueryableResource.query(...)`,
`QueryResult`, shapes, refusal-as-IOException. The TCK conserves semantics through the
swap.

| Step | Content | Depends on |
|---|---|---|
| M1 | `expression.ecore` + generation (bundle, neutral naming) | — |
| M2 | `query.ecore` rebuilt (envelope, same BSN) + `command.ecore`; stream model promoted to a bundle | M1 |
| M3 | Support layer re-targeted: analyzer/validator walk expression trees; `QueryValues` consumes typed literals; `QueryParameters` binds `ParameterRef` (convention retired) | M1 |
| M4 | Builder v2: same fluent feel, new powers — `group(...)`, `ne`, `isNull`, `in`, `any/all(path, v -> …)`, typed values, `param(name)` | M2 |
| M5 | JPA processor on the new IR (JPQL stays the target; EXISTS subqueries for iterators — `OclToCriteriaTranslator` as pattern reference) | M3 |
| M6 | Mongo processor on the new IR (find + pipeline builders reused; `$elemMatch` for embedded Exists; `$ne`/`$in`/null) | M3 |
| M7 | Resource integration switched (query-api.ecore references the new Query; `QueryConstants` unchanged) | M5, M6 |
| M8 | TCK ported + extended: grouping trees, ne/null/in, exists/forAll, case-insensitivity flag, pipeline stages | M7 |
| M9 | Bridge bundle + reference-evaluator differential tests in the TCK | M1, M8 |
| M10 | CUD v1: command model execution for Insert/Delete; Update modelled + refused pending patch engine | M2, M7 |
| M11 | Legacy retirement: old `query.ecore` content, old builder, old analyzer vocabulary removed; docs rewritten (`query-usage.md`) | M8 |
| — | OData phases 1/2 | tracked in fennec-odata |

**Explicitly out of scope here:** the patch-apply engine (own epic per concept §18.1),
`asOf`/series queries (reserved), OData repo changes, repo rename.

## 8. Decisions D1–D5 (closed 2026-07-28)

All five went in as proposed; D1/D2/D5 are annotated at the model elements themselves.

- **D1** `TemporalLiteral` = ISO-8601 string + `kind` enum, parsed against the target
  feature's temporal type at translation (`expression.ecore#TemporalLiteral`).
- **D2** `EnumLiteral` = literal name only, resolved against the target feature's EEnum at
  translation — no EEnumLiteral reference, query instances stay decoupled from the
  metamodel instance (`expression.ecore#EnumLiteral`).
- **D3** Pipeline `Compute` stage: **later** — not in the v1 stage set, additive when a
  driving use case appears.
- **D4** Projection results stay **tuple rows**; dynamic-EClass materialisation remains a
  follow-up layered on rows (SPI unchanged).
- **D5** `expand` lives on the **envelope** (`query.ecore#Query.expand`), not as a backend
  option — capability `EXPAND`, refused where not translatable.
