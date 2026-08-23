# Interval predicates and group representatives — the joint IR round (#215, #214)

**Status:** proposed (concept round 2026-08-23); Part A is implemented (I-P1) and Part B's
R-P1 is implemented. Each round corrected one proposed rule — see §A.5.4 with the §A.8
changelog, and §B.4 with the §B.8 changelog.
Decisions I1–I7 and R1–R7 are open until the maintainer round closes them; the semantic
rules are proposed as binding.
No capability literal and no model element is written before that — per the #207 rule
("add only what a phase declares"), the literals land with their implementation phase.
Companions: `query-ir-redesign.md` (Expression IR, capability discipline, D4 on result
shape), `geo-vocabulary.md` (the precedent this round leans on twice),
`conformance-and-capabilities.md` §2/§9.1/§9.3 (what may be a capability and at what
grain), `search-access.md` §3/§5/§7 (the consumer and the ground rule),
`query-processor-spi.md` §5 (the result model).
Consumers: `emf.search` S15 (eclipse-fennec/emf.search#17) and S19 (…#21).

---

## 1. Why one round

Two issues, one origin: the Lucene backend hit two things the canonical IR cannot say, and
followed the `search-access.md` §3 ground rule — missing query vocabulary comes back here
rather than being invented there. Neither is blocking: #215 has a correct fallback (two
scalar comparisons) that ships meanwhile, #214 ships as an own API next to the persistence
contract.

They are answered together because they share their hard parts, not their subject matter:

- **Both are settled by an existing precedent rather than by new philosophy.** The interval
  subject is the geo G1/G2 question again, verbatim. The representative ordering is the geo
  G3 question again (a dedicated construct versus composing what exists).
- **Both turn on the same two disciplines**: the capability grain of
  `conformance-and-capabilities.md` §9.3 (split only when a backend can answer the halves
  differently) and the row-space discipline of D4 (`query-ir-redesign.md` §8 — projection
  results stay tuple rows, materialisation is layered on top).
- **#214 touches `QueryResult`**, and so does #212 (series subjects yield
  `(objectId, ts, seq, value)` rows). Deciding the result-shape question once, for both, is
  cheaper than deciding it twice and discovering the answers differ.

The two parts below are independent from there on: Part A is a predicate, Part B is a result
shape. They can be implemented in either order.

---

# Part A — Interval predicates (#215)

## A.1 What exists today

An interval is two features, `validFrom`/`validTo`, and every question about it is
hand-wired from two comparisons:

```java
and(path(validFrom).le(t), path(validTo).ge(t))          // valid at t
and(path(validFrom).le(b), path(validTo).ge(a))          // overlaps [a, b]
```

Correct on every backend, and it stays correct — this round does not retire it. What it
does not give: the IR does not know the two features are one interval, so nothing validates
the pair, nothing can push it down as one predicate, and every consumer re-derives the
boundary convention on its own. `Between` is the neighbouring construct and deliberately a
different one: `Between` asks whether a *point* lies in an interval; this part asks how two
*intervals* relate.

## A.2 The subject — the geo precedent applies unchanged

`GeoSubject` answered "where does the coordinate pair come from" with: the **query** names
the paths (G1), **structurally**, not as a model aspect (G2), and the mapping layer decides
how that is indexed. The same answer fits here with no new argument, so this round proposes
to adopt it rather than re-open it:

```
IntervalSubject                       (no supertype — an argument, not an Expression)
  pathLower   : PropertyPath [1]      containment
  pathUpper   : PropertyPath [1]      containment
  lowerIncluded : EBoolean = true     the DATA's boundary convention
  upperIncluded : EBoolean = true
  nullMeansUnbounded : EBoolean = false
```

The split binding is the only binding in v1. A **packed** binding (one column holding a
range value — PostgreSQL `tstzrange`) is the exact analogue of `GeoSubject.pathPoint` and
stays additive: §9.3 says a form is added when a backend forces it, and no backend in our
set has a range-typed column today.

The last three attributes are the part that is *not* geo, and A.4 explains why they are on
the subject rather than on the predicate.

## A.3 The predicate

Modelled after `StringMatch` — one construct, one relation enum (the §3 house style:
explicit constructs, not generic operation calls):

```
IntervalMatch : Expression
  subject       : IntervalSubject [1]  containment
  lower         : Expression [1]       containment   (literal or ParameterRef)
  upper         : Expression [1]       containment
  lowerIncluded : EBoolean = true      the QUESTION's boundary convention
  upperIncluded : EBoolean = true
  relation      : IntervalRelation [1]

IntervalRelation = { INTERSECTS, WITHIN, CONTAINS }
```

- `INTERSECTS` — subject and query overlap at all.
- `WITHIN` — the subject lies inside the query interval.
- `CONTAINS` — the subject covers the query interval. **"Valid at t" is this relation with a
  degenerate query interval** (`lower == upper`, both bounds included) — no separate
  construct, no `AT` relation.

The `lowerIncluded`/`upperIncluded` pair mirrors `Between`, which already carries exactly
these two flags with exactly these defaults.

## A.4 The one genuinely new question: whose boundary, and what does null mean

Geo had no analogue for either, and both decide whether two backends answering the same
query agree. They are stated as data properties on the **subject**, not as options on the
predicate, for one reason: an index encodes them at write time, so the query cannot choose
them after the fact.

**Boundaries are two conventions, not one.** Four flags look heavy until you see that they
answer different questions. Half-open `[from, to)` is the temporal-database convention
precisely so that adjacent periods do not overlap — with a closed-closed reading, `[1,2]`
and `[2,3]` intersect at a point, which is wrong for validity periods and right for
measurement ranges. A query-side flag cannot express it: it governs the query interval, and
the adjacency question is about the subject's own upper bound. So the subject states the
convention of the data (`IntervalSubject`), the predicate states the convention of the
question (`IntervalMatch`), and a mapping aspect may later *derive* the subject side — the
same relationship G2 set up for coordinates.

**Null is a modelling decision, not an accident.** `validTo = null` meaning "still valid" is
the normal shape of temporal models; a missing upper bound on a measurement range is
missing data. The two need opposite answers, so `nullMeansUnbounded` picks one, defaulting
to `false` — the 3VL discipline of #94/#97. With `true`, a null lower bound is −∞ and a null
upper bound is +∞. What `false` means precisely is the one thing the implementation round
had to sharpen: UNKNOWN attaches to the *bound comparison*, not to the predicate as a whole
(§A.5.4).

The index-encoding consequence is the thing to write down loudly: Lucene stores an
unbounded end as `Long.MIN_VALUE`/`MAX_VALUE` and an exclusive bound as the adjacent
representable value, both **at index time**. A `RangeFieldMapping` whose flags disagree with
the `IntervalSubject` of a query is a defect that produces wrong answers silently, so
`emf.search` must compare the two and refuse the mismatch by name. On JPA/Mongo/memory the
flags cost nothing — they only pick which comparison operator is rendered.

## A.5 Semantics to pin (proposed as binding)

1. **Reference definitions.** With `≤ᴸ`/`≥ᵁ` standing for the operator the four inclusion
   flags select (`≤` when included, `<` when not):
   - `INTERSECTS` ≡ `subject.lower ≤ᵁ query.upper AND subject.upper ≥ᴸ query.lower`
   - `WITHIN` ≡ `subject.lower ≥ᴸ query.lower AND subject.upper ≤ᵁ query.upper`
   - `CONTAINS` ≡ `subject.lower ≤ᴸ query.lower AND subject.upper ≥ᵁ query.upper`
2. **One ordered domain.** Both subject paths and both query bounds are the same comparable
   domain (numeric or temporal). A mixed pair is a validation error, not a runtime surprise
   — the geo §5.5 pattern (`CODE_INVALID_INTERVAL`, next free diagnostic code).
3. **An inverted query interval** (`lower > upper` on two literals) is a static validation
   error, like `CODE_DIVISION_BY_ZERO`. An **inverted subject row** is the empty interval and
   matches **no** relation, including `WITHIN` — vacuous truth is the answer nobody wants,
   and Lucene cannot index an inverted range at all, so the writer refuses it there.
4. **Null follows `nullMeansUnbounded`** (A.4), and UNKNOWN belongs to the single bound
   comparison. Under `false` a missing bound makes *that comparison* UNKNOWN and the
   conjunction then decides under ordinary 3VL — so a row whose end is unknown answers
   UNKNOWN where the end would have decided (excluded positively and under `not(…)`), and
   FALSE where its start already rules the question out (so its negation holds).

   This replaces the concept round's first proposal, which read "a missing bound makes the
   predicate UNKNOWN, guarded explicitly in every push-down". That rule is **not
   expressible in SQL**: the predicate is a conjunction, `FALSE AND UNKNOWN` is `FALSE`, and
   no arrangement of conjuncts preserves the UNKNOWN once another conjunct is FALSE.
   Reaching it would need a negation-aware translation in every backend — and even then only
   for a directly negated `IntervalMatch`, not for one nested under `not(and(…))`. The rule
   above, by contrast, is what SQL and Mongo already do with these two comparisons: no
   guards, no negation surgery, correct under arbitrary nesting. The cost is stated in §A.6.
5. **Boundary flags are semantics, not hints.** A backend that cannot render an exclusive
   bound refuses the query; it never rounds it to the inclusive one.

## A.6 Capability and backend mapping

One literal, `INTERVAL_MATCH` — not one per relation. §9.3 decides it: a capability splits
when a backend exists that can answer the halves differently, and none does. Lucene serves
all three natively (`newIntersectsQuery`/`newWithinQuery`/`newContainsQuery`), PostgreSQL
range types serve all three (`&&`, `<@`, `@>`), and the two-comparison fallback serves all
three. When a backend appears that overlaps but cannot contain, the split is additive.

| Backend | Translation |
|---|---|
| memory (reference) | the §A.5 definitions directly over the two features — the oracle the TCK differential compares against |
| JPA | the two-comparison form per relation, plus the null guards; correct, and **not** index-accelerated |
| Mongo | the same two-comparison form on the two fields, negations with explicit non-null guards (#97) |
| Lucene (`emf.search` S15) | one `LongRange`/`DoubleRange` field, BKD tree over both bounds, one query per relation — the reason this vocabulary exists |
| PostgreSQL range types + GiST | not reachable through JPQL; a dialect follow-up in the shape of G-P4/PostGIS, own issue |

**The consequence of §A.5.4 for range indexes.** A row with a missing bound and
`nullMeansUnbounded = false` is not representable as a range field: an index stores one
interval per document, so a document with an unknown end is either absent (then it can never
answer FALSE, only "no match", which loses the negation case) or stored with a sentinel (then
it is indistinguishable from an unbounded one). So the mapping side owes one of two things —
refuse such rows at write time and say so, or carry a "bound present" marker field next to
the range and combine it into the query. `emf.search` picks one in I-P2; a backend that picks
neither diverges from the reference on exactly those rows, which is a divergence to declare,
not to discover. Under `nullMeansUnbounded = true` the question disappears: the extremal
value *is* the meaning, and that is the case a range index serves natively.

**The honest note about why the literal exists.** Unlike geo, every backend here *can*
serve the vocabulary through the fallback, so the literal is not "this store cannot do it"
— it is the ordinary translator-gap declaration §9.1 already blesses, and the thing an
analyzer needs in order to refuse a construct a translator has not implemented yet.
**Performance is not declared**: `INTERVAL_MATCH` says the answer is correct, never that it
used a range index. Whether a plan touched an index is the subject of #199, and that is the
right place for it.

## A.7 Decisions to settle (I1–I7)

| # | Question | Leaning |
|---|---|---|
| I1 | Subject binding: split pair only, or split + packed from the start | **split only** — §9.3, no backend forces the packed form yet; additive later like `GeoSubject.pathPoint` |
| I2 | Where the interval is declared: structurally in the query (G1/G2) or as a mapping aspect | **structurally** — the geo precedent, unchanged; an aspect may later derive the paths |
| I3 | Boundary semantics: fixed closed-closed, or modelled flags | **modelled, on both sides** (A.4), defaulting to closed-closed — `Between` already carries the query-side pair |
| I4 | Null bounds: always UNKNOWN, or a `nullMeansUnbounded` switch | **the switch, default false** — both readings are real, and the default stays with the 3VL discipline; UNKNOWN attaches per bound comparison (§A.5.4, corrected in implementation) |
| I5 | Capability grain: one literal or one per relation | **one** — §9.3; no backend can answer the halves differently |
| I6 | Feature id | **84** — the next free value; 82 is an unassigned gap in the sequence and stays unused (#207 rule 5 discipline) |
| I7 | Is an interval also a *sortable* value ("order by duration/start") | **no in v1** — `Arithmetic` over the two paths already sorts by duration; an interval-valued expression has no use case yet |

## A.8 Changelog

- **2026-08-23, I-P1 implementation:** §A.5.4 replaced. The proposed "a missing bound makes
  the whole predicate UNKNOWN, guarded in every push-down" cannot be rendered in SQL, where
  a FALSE conjunct swallows the UNKNOWN; the rule is now ordinary 3VL over the two bound
  comparisons, which needs no guards and survives nested negation. §A.6 records what that
  costs a range-indexing backend. Everything else in Part A went in as proposed.

---

# Part B — Group representatives (#214)

## B.1 What exists today

`GroupByStage` produces exactly one row per group: the group keys plus the aggregates
(`query.ecore#GroupByStage`, `paths` + `keys` + `aggregates[1..*]`). There is no way to ask
for the top **documents** of each group — SQL's `ROW_NUMBER() OVER (PARTITION BY …)`,
OData's `groupby(…, topcount(N, …))`, Lucene's `GroupingSearch`. `query-ir-redesign.md` §4.1
reserved a slot for it by name ("Further stages (`Concat`, `BottomTop`) are additive later").

## B.2 The result shape — the part this actually turns on

A grouped answer with representatives is neither of the two shapes `QueryResult` knows. The
three candidates, against what the SPI is today (`query-api.ecore`, `QueryResult` /
`QueryResultRow`):

| Candidate | Cost |
|---|---|
| **a row whose cell holds the objects** | none in the SPI — `QueryResultRow.get(alias)` is already declared `EJavaObject`, and `values()` is `List<Object>`; a `List<EObject>` cell is a value the contract already permits |
| a nested `QueryResult` per row | needs its own lifetime against the same cursor: who closes it, what happens when the outer stream advances first. A second `AutoCloseable` inside a streamed row is a contract we would be maintaining forever |
| a fourth `QueryShape` | forks every consumer's `switch` over `OBJECTS`/`PROJECTION`/`AGGREGATION`/`COUNT` for a variant of one of them |

**Proposal: the cell.** It is the option D4 already implies (rows are the projection shape;
richer materialisation is layered on top of rows, not beside them), the only one that needs
no SPI change, and the only one that leaves #212's series rows and this feature in the same
row space. `QueryShape` stays `AGGREGATION`.

The cost to state plainly: a representative cell is **materialised eagerly** — N objects per
group are built while the row stream is walked, bounded by N × groups, which the caller's
`top` bounds in turn. Streaming semantics and `SERVER_CURSORS` are unaffected, but a
representative query is not a constant-memory query and the guide has to say so.

## B.3 The stage shape — and why not `BottomTop`

The reserved name covers OData's `topcount`/`bottomcount`/`toppercent` family, which
operates on **rows**: "the five best groups by revenue". That is expressible today — an
aggregate alias, `orderBy` on it, `TopStage`. Representatives are **documents inside** a
group, a different thing, and giving it the reserved name would spend the slot on the wrong
feature. Proposal: leave `BottomTop` reserved, and put representatives where the grouping is:

```
GroupByStage
  … paths, keys, aggregates as today …
  representatives : RepresentativeSpec [0..1]   containment   (new)

RepresentativeSpec
  count   : Expression [1]       literal or ParameterRef
  offset  : Expression [0..1]    literal or ParameterRef
  orderBy : OrderBy [*]          within-group ordering
  alias   : EString [1]          names the cell, like GroupKey.alias
```

An explicit construct rather than a nested pipeline inside `groupby` (R3: a curated subset,
so that illegal queries are unbuildable rather than capability-refused everywhere), and it
carries exactly what the three engines need as arguments.

**No `totalAlias`.** The issue asks for the per-group total so that "3 of many" and "3 in
all" stay distinguishable — and that is an ordinary `COUNT` aggregate on the same stage.
Aggregates see the whole group, representatives are a window over it, so truncation is
visible as `count > representatives.size()` with vocabulary that already exists. One concept
less, and it works identically on all four engines.

## B.4 Ordering — the geo G3 move again

Two orderings, kept apart:

- **within a group**: `RepresentativeSpec.orderBy`, and nothing else. The concept round
  proposed a fallback to the envelope's `orderBy`; implementation dropped it, because a query
  with representatives is grouped by construction, so its envelope ordering addresses output
  columns rather than the documents inside a group — the fallback could only ever sort by a
  path that does not resolve against a group member. Left empty, the window is unspecified.
- **between groups**: the ordinary post-group row ordering over group keys and aggregate
  aliases. Nothing new.

"Order groups by their best representative" then needs **no vocabulary at all**: a
`MIN`/`MAX` aggregate over the sort key, and `orderBy` on that alias. A backend may
recognise the pattern and serve it with its native group sort (Lucene's group sort is a
separate knob from the within-group sort) — an optimisation, not semantics, exactly as G3
left k-NN to `ORDER BY distance LIMIT n`.

## B.5 Semantics to pin (proposed as binding)

1. **Representatives are whole EObjects of `Query.from`**, in the declared within-group
   order, never null (an empty `List` instead). A per-representative *projection* is out of
   v1 and refused.
2. **`count` must be statically known** — a literal or a bound `ParameterRef`, never a
   computed expression. Lucene needs it when the search is constructed; this is the same
   rule `MapValue`'s key already carries, for the same reason. `count ≤ 0` is a validation
   error.
3. **A group is never dropped by its window.** An `offset` past the end of a group yields an
   empty representative cell; the group's row, keys and aggregates still appear.
4. **Fewer members than `count` yields fewer representatives** — no padding, no error.
5. **Reproducibility is the caller's**: ties under a non-total within-group order are broken
   by the backend. The TCK corpora use unique sort keys, the same discipline as elsewhere.

## B.6 Capability and backend mapping

One literal, `GROUP_REPRESENTATIVES` — §9.3 again: no backend in sight can return
representatives but not their group's count, or vice versa.

| Backend | Translation |
|---|---|
| memory (reference) | sort each group's members, window them — the oracle |
| Mongo | `$topN`/`$bottomN` accumulators inside `$group` (5.2+), or `$push` + `$slice` on older flavors; per-flavor declaration, which the flavor split already handles |
| Lucene (`emf.search` S19) | `GroupingSearch` — one two-pass search, top-N per group plus the group's total hit count, natively and cheaply |
| JPA | **the open one.** JPQL has no window functions and EclipseLink 4 does not add them (no `ROW_NUMBER` in its JPQL parser or expression framework), so the choices are a native-SQL route that breaks dialect neutrality, or an N+1 two-pass execution. Proposal: **refuse in v1** and settle the route in its own issue — the geo-on-JPA precedent |

Note the shape of the matrix: this is a capability whose false state is genuinely observed
on a real backend from day one, which is the §2B test passing on the first try — the
opposite situation to `INTERVAL_MATCH` in A.6.

## B.7 Decisions to settle (R1–R7)

| # | Question | Leaning |
|---|---|---|
| R1 | Result shape: cell holding objects / nested result / new `QueryShape` | **cell** (B.2) — no SPI change, consistent with D4, keeps #212 in the same row space; the contract statement it needed lives in `conformance-and-capabilities.md` §9.5 |
| R2 | Stage shape: `RepresentativeSpec` on `GroupByStage` vs. a `BottomTopStage` vs. a nested pipeline | **`RepresentativeSpec` on the stage**; `BottomTop` stays reserved for the OData row-space family (B.3) |
| R3 | Per-group total: a dedicated `totalAlias` or an ordinary `COUNT` aggregate | **the `COUNT` aggregate** — the vocabulary already exists and already means this |
| R4 | Group ordering by "best representative": dedicated syntax or composed | **composed** (`MIN`/`MAX` + `orderBy` on the alias), G3 precedent; native group sort is an optimisation |
| R5 | Cell content: whole EObjects or a projection per representative | **whole EObjects** in v1; projection additive |
| R6 | JPA: two-pass, native SQL, or refuse | **refuse in v1**, own issue for the route — implemented as an undeclared capability plus a backstop in the pipeline translation |
| R7 | Feature id | **85**, after `INTERVAL_MATCH`=84 |

## B.8 Changelog

- **2026-08-23, R-P1 implementation:** §B.4's fallback to the envelope ordering removed (see
  above). Mongo serves the window as a pre-`$sort` plus `$push` and `$slice` rather than the
  `$topN` accumulator: `$topN` needs MongoDB 5.2 and this shape works on every flavour the TCK
  runs, while the group ordering it disturbs is undefined anyway. The representative cell is
  decoded through the codec in the resource, which is the one place where this feature is more
  than translation — the plan carries which of its output keys are object-valued. Everything
  else in Part B went in as proposed: no `totalAlias`, and outside the reserved `BottomTop`
  slot.

---

## 3. Phasing

Independent tracks; either may go first.

1. **I-P1** — `IntervalSubject`/`IntervalMatch` + `INTERVAL_MATCH` + validator rules (A.5.2,
   A.5.3) + memory reference + `Expressions` DSL + TCK cases behind `@RequiresCapabilities`
   *and* a `featureProbes()` entry. Unlike geo, **both** of our backends translate the
   fallback form in this phase — it is cheap and correct.
2. **I-P2** — `emf.search` S15: the Lucene range-field translation against the published
   vocabulary, including the mapping/query flag agreement of A.4.
3. **I-P3** — PostgreSQL range types as a JPA dialect. Own issue, own note, like G-P4.
4. **R-P1** — `RepresentativeSpec` + `GROUP_REPRESENTATIVES` + memory reference + the
   `List<EObject>` cell contract in the guide; JPA refuses (R6), Mongo translates.
5. **R-P2** — `emf.search` S19 against the published vocabulary.
6. **R-P3** — the JPA route, if the two-pass cost turns out acceptable.

Each phase follows the checklist the geo round established: `expression.ecore`/`query.ecore`
+ genmodel → regen → `capabilities.ecore` literal + regen → `ExpressionAnalyzer` (feature
detection, structural scan) → `QueryAnalysis` field + `QueryValidator` code →
`MemoryPredicate`/`MemoryQueryProcessor` → backend translator + `*FlavorCapabilities` →
`ExprToOcl` (translation or documented totality exception) → `Expressions` DSL → TCK
(`featureProbes()` entry *and* gated positive cases, fixtures in
`tck.ecore`/`tck-string.ecore`) → docs (`query-user-guide.md` + the docs-site mirror).

## 4. Non-goals

- **Allen's thirteen interval relations.** Three cover the asked questions; `MEETS`,
  `STARTS`, `OVERLAPS-BEFORE` and the rest are expressible from the bounds and have no
  consumer.
- **Interval arithmetic** — union, intersection, duration as interval-valued *results*. This
  round adds a predicate, not a type.
- **A range `EDataType` in Ecore.** The subject stays two features; a packed column is a
  mapping form (I1), never a modelling requirement on the user's metamodel.
- **Index-usage guarantees.** `INTERVAL_MATCH` declares correctness; whether a plan used a
  range index belongs to #199.
- **Percent-based representatives** (`toppercent`) and representatives without grouping.
- **A per-representative projection**, and representatives of a *series* subject (#212) —
  both additive, neither with a consumer today.
