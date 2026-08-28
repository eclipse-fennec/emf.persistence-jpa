# Ecore → ChangeSet: the tracked-slot mapping and what counts as a change

**Status:** proposed (concept round 2026-08-24). Nothing is implemented, no model element and
no capability literal is written before the maintainer round closes decisions M1–M8 and
C1–C7 — per the #207 rule ("add only what a phase declares").
Companions: `concept.md` §7.1 (the keyed snapshot diff this round changes), §8 (the tracking
aspect it extends), §8.1/R1–R8 (the rule semantics it must not fork), §4.2 (the id space it
borrows), §20.2 (the `tracking` package it lands in);
`timeseries-access.md` §6/§6.1/§6.2 (the donor ladder, on the other side of the boundary),
§12.1 (bundle cut), §13.1 (the narrow P1 promotion this round widens).
Donor: `org.eclipse.fennec.event.atlas.mapping` in `fennec-event.atlas`
(`model/event-atlas-mapping.ecore`) — the same metamodel the ChangeRule family was already
lifted from (§3.6).
Issues: **#220** (the declaration — tracked-slot sources and the class selector),
**#221** (what it costs the keyed diff), **#222** (predicate and function ChangeRules).
Affects **#208** (tracking model as a bundle) directly — C7 decides what P1's narrow
promotion contains.

---

## 1. Why this round

The concept already answers "which features are tracked": `FeatureTracking.feature` plus
`mode` (§8). That answer is a **flat list of real `EStructuralFeature`s**, and it is the
only vocabulary the state-based capture path has.

Meanwhile the ingest side got a full declaration language — `timeseries-access.md` §6.1: a
four-rung extraction ladder (path, path+OCL, root OCL, converter service), guards, constants,
`foreach`, missing-member policy, and §6.2's virtual features for values no feature holds.
All of it is scoped to **payload → model**.

So the stack currently has two very unequal halves:

| | payload → model (ingest) | model → ChangeSet (selection) |
|---|---|---|
| value source | ladder rungs 1–4 (§6.1) | one `EStructuralFeature`, direct only |
| literals | constants (§6.1) | — |
| OCL | rungs 2/3, blessed subset | — |
| service transform | rung 4, DS whiteboard | — |
| bulk selection | n/a (per assignment) | — (one entry per feature, by hand) |
| "is this a datum" | guards (§6.1) | — |
| "does it enter history" | ChangeRules (§8, delegated) | ChangeRules: absolute, %, count, throttle |

The right half is what the **comparator** consumes: §7.1's keyed snapshot diff compares
"only the tracked features", flat and typed. Its whole cost model rests on that list being
`eGet`-able. Every gap in the right column is therefore also a gap in the comparator, and
that is the part of this round that is not just modelling.

Two concrete shapes force the widening, both from the existing installed base rather than
from speculation:

- **A domain object, not a payload, is the source.** RecordingCapture (§7.2) and any
  SnapshotCapture over an already-mapped domain model never pass through an IngestMapping.
  A computed slot (`a × b`, a discriminator, a normalised unit) has no declaration site at
  all on that path — §6.2 hands virtual features their value from the ingest ladder, which
  is not running.
- **"Everything except these three."** An EClass with forty attributes needs forty
  `FeatureTracking` entries to be tracked, and the forty-first added later is silently
  untracked. The donor already solved this with `ReferenceMapping.filter` + `exclude`.

## 2. What is lifted, and what is not

From `event-atlas-mapping.ecore`:

| Donor element | What it gives | Verdict |
|---|---|---|
| `FeatureMapping.featurePath : →EStructuralFeature[*]` | multi-step navigation, not just a direct feature | lift (§3.1) |
| `FeatureMapping.functionId : String` | named transform resolved through a whiteboard registry (`FunctionRegistry`) | lift as rung 4 (§3.1) |
| `FeatureMapping.collectionIndex` / `collectionFilter` | element selection inside a collection-valued step | lift, with a *defined* dialect (C3) |
| `ValueMapping.value` / `NameMapping.name` | a literal instead of a read | lift as `ConstantSource` (§3.1) |
| `ReferenceMapping.filter[*]` + `exclude : boolean` | include/exclude list over a type's features | lift as `FeatureSelector` (§3.2) |
| `TimestampMapping.strategy : NOW \| FEATURE \| FUNCTION` | the same three-way source choice, for time | already covered — `ChangeSet.timestamp` (§6) |
| `PersistenceRuleRegistry`, `ChangeRule`, `DeletionRule` | reusable, referenced rules | already lifted (§8, §20.2) |
| `MappingProfile` / `ProfileProvider` / `ProfileService` / `ProfileResource` | conformance template for a *target structure* | **not lifted** — sensiNact's provider/service/resource shape has no counterpart here; a ChangeSet's shape is fixed by `stream.ecore` |
| `ProviderMapping.providerTimestamp` | one timestamp for a whole provider vs. per resource | **not lifted** — a ChangeSet is already the batch granularity (§20.1) |
| `JavaStringFunction`/`JavaObjectFunction` as `EDataType` | in-model Java lambdas, `serializable="false"` | **not lifted** — a declaration that cannot be serialised cannot be fingerprinted (M6) |

The last row is the one real correction to the donor: the donor's mapping instances are built
programmatically as often as loaded from XMI, so a non-serialisable function slot is
acceptable there. Here every value-affecting declaration enters the context snapshot (R6,
§6.4), so a source must be **nameable** — `functionId`, never a lambda.

## 3. The model — extend `FeatureTracking`, do not add a sibling

### 3.1 A tracked slot has a source

`timeseries-access.md` §6.2 already states the shape: *"a virtual feature is declared in the
aspect plane exactly like a tracked real one"*. Taking that literally means no new class —
`FeatureTracking.feature` becomes optional and gains a source next to it:

```
FeatureTracking                                        (§20.2, extended)
  feature   : →EStructuralFeature   [0..1]   // exactly one of feature / source (M1)
  source    : ValueSource           [0..1]   containment
  featureId : int                            // unchanged, §4.2 id space
  name      : String                [0..1]   // required when source is set (M2)
  eType     : →EClassifier          [0..1]   // required when source is set (M3)
  mode, shape, arrayMode, changeRules, deletionRule, … unchanged

ValueSource (abstract)
  ├─ PathSource     { path : →EStructuralFeature[*], step : CollectionStep [0..1] }   rung 1
  ├─ ConstantSource { value : String }                                                rung 0
  ├─ OclSource      { expression : String, context : SELF | ROOT = SELF }           rungs 2/3
  └─ FunctionSource { functionId : String }                                           rung 4

CollectionStep { index : int = 0, selector : String [0..1] }    // selector wins over index
```

`feature` set is exactly today's behaviour and stays the 80 % case — a `PathSource` of
length 1 is deliberately *not* the canonical spelling of it, so nothing existing has to be
rewritten and the cheap case stays cheap to recognise.

The ladder is the same ladder as §6.1, same rung numbering, same dialect: m2x OCL bridged
through `OclToExpr` into the expression IR, evaluated by the memory engine's `test()`/`value()`
paths. One OCL, one coercion and 3VL semantics (#93/#94), and the rung 2/3 boundary stays
tool-checkable: whatever `OclToExpr` refuses is rung 4 by definition.

### 3.2 Bulk selection: the selector

```
ClassTracking                                          (§20.2, extended)
  selector : FeatureSelector [0..1]   containment
  features : FeatureTracking[*]                        // unchanged, wins per feature (M4)

FeatureSelector
  features : →EStructuralFeature[*]
  exclude  : boolean = false          // false = include list, true = exclude list
  defaults : FeatureDefaults [0..1]    // mode/changeRules/deletionRule for selected features
```

`exclude = true` is what makes "everything except these three" expressible, and it is also
what makes a *later added* feature tracked by default — the opposite failure mode from
today's silent omission. Which of the two silences is preferable is C1; the model can say
both, so the decision is a default, not a structure.

### 3.3 What counts as a change: two more rules (#222)

The existing four ChangeRules are numeric or temporal. A predicate is missing, and it is the
only kind that can express "a change only counts while the device reports valid", "only
transitions into `FAULT`", or "ignore changes below the sensor's declared resolution, which
lives in a sibling feature":

```
ChangeRule (abstract, §20.2)
  ├─ AbsoluteChangeRule, PercentageChangeRule, CountChangeRule, TimeThrottleChangeRule
  ├─ PredicateChangeRule { expression : String }       // OCL, blessed subset
  └─ FunctionChangeRule  { functionId : String }       // rung 4 for rules
```

The predicate's evaluation context is the one place where this round must be explicit,
because the donor is not: `self` is the **new** state of the tracked object, with
`oldValue` and `newValue` bound as variables for the slot under evaluation. A rule that
reads only `newValue` is a validity guard; one that reads both is a transition filter. Both
are wanted, and one context expresses both.

Note the boundary against §6.1's guards, which stays exactly as §6 draws it: a **guard** asks
"is there a datum at all" at payload level and belongs to the ingest aspect; a
`PredicateChangeRule` asks "does this transition enter the stream" at history level and
belongs here. The same OCL text can appear on both sides and mean two different things — that
is not duplication, it is the two-aspects-one-registry doctrine holding.

## 4. What this does to the comparator (#221)

This is the substantive half. §7.1's diff is `O(#tracked features)` because it reads a value
per tracked feature from each of two states. Derived slots break four of that sentence's
assumptions, and each break has a concrete failure mode.

**(a) The read set and the tracked set stop being the same set.** An `OclSource` over
`raw × scale` produces one tracked slot from two features, and `scale` may itself be
`mode = NONE`. The diff must therefore read the **dependency closure** of every tracked
slot, not the tracked slots themselves, and the last-known-state cache must hold that closure
— otherwise the second sample computes against a `scale` it does not have and emits a delta
that never happened. Consequence: the closure is derivable statically for rungs 0–2 (paths
and blessed-subset OCL both have inspectable feature references) and **not** derivable for
rung 4. A `FunctionSource` must therefore declare its inputs, or the closure degrades to "the
whole object" (C4).

**(b) A derived slot has no storage location, so the *old* state must be an object.** Today
the diff can in principle work against the last stored slot values alone. With derived slots
it must re-evaluate the source over the previous state, which means the previous state must
be materialised as an EObject, not as a value map. For the CHANGELOG path that is what the
cache holds anyway; for the high-frequency TIMESERIES path, §7.1's "degenerates to append —
no diff at all" is the shortcut that keeps ingest cheap, and it survives untouched **only for
slots whose rule set needs no old value**. A `PredicateChangeRule` reading `oldValue` opts
that slot out of the shortcut. Worth stating plainly because it is a performance cliff hidden
behind a declaration.

**(c) Sources must be pure functions of the object state.** An OCL expression or a function
that reads the clock, a counter, or a lookup will produce two different values for one
unchanged state — a phantom delta on every capture, and with TIMESERIES coalescing switched
off (§13.1) nothing downstream will ever notice. Purity is therefore a **rule, not an
option** (M5): impure enrichment belongs to the ingest aspect, where §6.3 already gives it a
home with a TTL cache and a failure policy, and where "the sample records what was true at
capture" is the declared semantics.

**(d) Paths may leave the aggregate, and the diff has no trigger there.** A `PathSource` of
`sensor.owner.name` depends on an object that can be its own aggregate root. Nothing changing
in `owner` produces a capture on `sensor`, so the slot silently keeps a stale value until the
sensor happens to change for an unrelated reason. This is the cross-aggregate problem of
§5.4g in a new place, and the honest answer is a restriction rather than a mechanism:
**paths may navigate containment only** (M7), non-containment steps are a declared
non-support that tooling rejects at load. Whoever needs the joined value takes it from a
projection or from ingest-time enrichment.

**(e) The deadband reference is a third state.** The donor documents this and the concept
already encodes it as R4/R7/R8: a ChangeRule compares against the **last stored** value, not
the last seen one, or a slow drift below the threshold never gets recorded at all. The
comparator holds "previous state" and "new state"; the deadband needs "last stored value per
`(objectId, featureId, address)`" as a separate, per-slot piece of state, and R7 says it is
reset on every rule change. That is not new semantics, but it is the reason the rule filter
cannot be folded into the diff step — it sits after it, per §7.3's pipeline, with its own
memory.

**(f) Every source is fingerprint-relevant.** OCL text, `functionId`, a path, a selector's
include/exclude decision and the id it assigned all determine how a historical value is to be
read. R6 already says rules are context; this round extends it to sources (M6). Editing an
OCL expression is therefore a snapshot-chain event, and entries before and after it are
interpreted under their own fingerprints. This is also the argument for §2's rejection of
in-model lambdas: an unnamed function cannot be part of a content-addressed declaration.

## 5. Semantic ground rules (proposed as binding)

- **M1 — Exactly one source.** `feature` xor `source`. Both set is a load error; neither is a
  load error. No implicit precedence, ever.
- **M2 — A derived slot is named.** `name` is required when `source` is set: there is no
  `eStructuralFeature.getName()` to fall back on, and series queries (§5) address the subject
  by name through the aspect.
- **M3 — A derived slot is typed.** `eType` is required when `source` is set, because
  `stream.ecore` resolves typing exclusively from `(contextFingerprint, featureId)` (§20.1
  decision 1). Inferring it from an OCL expression is not available at declaration time.
- **M4 — Explicit beats selected.** A `FeatureTracking` entry for a feature overrides whatever
  `ClassTracking.selector` says about it, including "not selected".
- **M5 — Sources are pure over object state.** Deterministic, side-effect-free, no clock, no
  I/O, no lookup. Impurity is an ingest concern (§6.3).
- **M6 — Sources are context.** Path, expression, `functionId`, selector outcome and assigned
  `featureId` enter the context snapshot; changing any of them yields a new fingerprint, and
  R7's baseline-after-change applies unchanged.
- **M7 — Paths stay inside the aggregate.** Containment steps only; a non-containment step is
  rejected at load, not silently followed.
- **M8 — A constant slot has one delta, ever.** `ConstantSource` yields a value at `CREATE`
  and never again — it is a stamped discriminator, not a series. A constant slot with
  `mode = TIMESERIES` is a declaration error rather than an infinite stream of equal samples.

## 6. Open decisions

- **C1 — Selector default direction.** Is a feature absent from all declarations tracked or
  untracked? Today: untracked (R1: no aspect, no stream), which makes a newly added feature
  silently unhistorised. The counter-position is that `exclude`-style selection should be the
  documented idiom for domain classes and opt-in for payload classes. Recommendation:
  **keep opt-in as the default**, because R1 is load-bearing elsewhere, and let
  `selector.exclude = true` be the explicit opt-out spelling.
- **C2 — How multiple ChangeRules compose.** `changeRules` has been `[*]` since §8 and the
  combination has never been defined. Recommendation: **AND** — every rule is a refusal to
  sample, and "throttle to 1/min *and* only past a 0.5 deadband" is the case people actually
  write. The cost is that a careless `CountChangeRule(n=10)` AND `AbsoluteChangeRule` pair can
  starve a slot, which tooling can warn about; OR would instead make each added rule *loosen*
  the filter, which no reading of R3 supports.
- **C3 — The collection-selector dialect.** The donor's `collectionFilter` is
  "implementation-specific", which §6.1 already names as "rung 2 without a defined dialect".
  Recommendation: it *is* rung 2 — a blessed-subset OCL predicate over the element, with
  `index` as the degenerate fallback, and no second expression language.
- **C4 — Dependency declaration for rung 4.** Does `FunctionSource` declare its input features
  (statically checkable closure, more to write) or not (closure = whole object, cache and diff
  costs grow, and (a)'s failure mode returns)? Recommendation: **declare them**, as an
  explicit `inputs : →EStructuralFeature[*]`, and treat an undeclared read as a programming
  error the TCK can catch.
- **C5 — Do derived slots get a current-state projection?** §6.2 says a virtual series has
  none by definition. But a slot derived from real features of the same aggregate *is*
  recomputable at read time, which is a different situation from a CayenneLPP channel.
  Recommendation: no projection in P1 (keep §6.2's line), revisit when a consumer asks.
- **C6 — Where the round lands in the model.** Extending `FeatureTracking`/`ClassTracking`
  (this proposal) versus a separate `selection.model`. Recommendation: extend — a second model
  would need its own binding to `featureId` and its own fingerprint story, and §6's
  two-aspects doctrine already puts "what is tracked" on this side of the line.
- **C7 — Consequence for #208's narrow promotion** (the decision #220 carries)**.** §13.1 promotes `tracking.model` to P1
  narrowly: `TrackingMode`, `ClassTracking`, `FeatureTracking`, `TrackingConfig`. This round
  adds candidates to exactly those classes. Recommendation: promote `ValueSource` +
  `ConstantSource` + `PathSource` and `FeatureSelector` with #208, hold `OclSource`,
  `FunctionSource`, `PredicateChangeRule` and `FunctionChangeRule` until the phase that
  actually evaluates them (they need the expression IR bridge and the function whiteboard,
  neither of which P1 has) — the same additive discipline `DeltaKind` is under. The cost of
  guessing wrong is asymmetric: an unused abstract class is harmless, an unused evaluator
  contract is not.

## 7. Test obligations

Not optional, and listed here so the round cannot be implemented without them:

- keyed diff over a `PathSource`, an `OclSource` and a `ConstantSource`, each with the
  dependency-closure feature deliberately left `mode = NONE` — the (a) failure mode;
- the same three against two identical states, asserting **zero** deltas — the (c) phantom
  test, which is the one an impure source fails;
- a `PredicateChangeRule` reading `oldValue` on a TIMESERIES slot, asserting the append
  shortcut is bypassed rather than silently producing no old value — the (b) cliff;
- a non-containment `PathSource` rejected at load with a diagnostic naming the step — M7;
- selector + explicit entry on the same feature, asserting M4's precedence in both directions;
- a source edit producing a new fingerprint, and the entries on both sides reading correctly
  under their own — M6 plus R7's baseline;
- `ConstantSource` with `mode = TIMESERIES` rejected — M8.
