# Working document: Unified persistence concept — queries, change streams, time series, metadata

**Status:** working document — concept and design discussion, not yet an implementation plan.
Captures the architecture discussion of 2026-07-18 (JPA/Mongo unified query language, diff/patch
persistence, time series, metamodel evolution, fault tolerance, transport). Verified against the
existing assets in `fennec.common.models`, `emf.model.metadata`, `emf.util`
(`org.eclipse.fennec.sensinact.mapping`), `org.eclipse.emf-compare` and
`org.eclipse.emf` (`ecore.change`, `emf.edit`).

> **Metadata service moved (2026-07-31).** Everything this document attributes to
> `emf.model.metadata` now lives in `emf.osgi`: `org.eclipse.fennec.emf.osgi.metadata`
> (metadata model and service API) and `org.eclipse.fennec.emf.osgi.api`
> (`FingerprintService`, `ArtifactStore`). The donor repo is being archived. The aspect
> *structure* described in §3.2 changed with the move — see the note there. Reference
> documentation: `emf.osgi/docs/metadata-service-guide.md` and
> `emf.osgi/docs/model-fingerprint-guide.md`.

---

## 1. Requirements & scope

Seven requirement clusters drive this concept:

1. **Unified query language** over all persistence backends (JPA/EclipseLink, MongoDB, future
   ones), including CRUD. A read-only query metamodel already exists
   (`fennec.common.models/org.eclipse.fennec.query.model`).
2. **Time series persistence** — individual features of an EClass (e.g. `Sensor.temp`) kept as
   a history of values, next to the current-state EObject.
3. **Diff/patch processing** — produce diffs, persist them, apply them to objects, invert them.
   Diffs are themselves EMF objects. EMF Compare is the reference point, but its runtime
   suitability is limited (see §3.3).
4. **Two kinds of data** — plain current-state EObject persistence (as today) and/or history
   (changelog / time series) per feature. Both representations coexist: the Sensor holds the
   *current* temperature, the series holds the *history*.
5. **The history is a configurable changelog** — per feature: whether to track, under which
   conditions (thresholds), and how long to keep it (retention).
6. **Batched changes** — one save in an editor changes several features at once, yet single
   changes must be selectively undoable/appliable (cf. `org.eclipse.emf.edit` Command/CommandStack).
7. **Model metadata** — documentation, privacy descriptions, genmodels, codec mappings,
   units of measurement — held *apart* from the EObjects
   (`org.eclipse.fennec.emf.osgi.metadata`).

Plus, from the discussion:

8. **Nesting** — containment hierarchies must be representable in diffs.
9. **Fault tolerance** — explicit policies for missing sequences, out-of-order and duplicate
   deliveries; repair mechanisms (get-full / state resync, keyframing).
10. **Transport** — diffs must be transferable between systems without the receiver knowing
    the sender's domain model version in advance.

---

## 2. Core principles

Everything below derives from four principles. If a detail decision is unclear, these break the tie.

**P1 — Streams are the truth; everything else is a projection.**
The change stream (ordered log of deltas) is the authoritative record. The current-state
EObject, indexes, derived metadata (`lastSequence`, `changeDate`, `creationDate`), and future
aggregates (min/max/avg) are *materialized projections* over streams: rebuildable, asynchronously
maintained, eventually consistent. Losing a projection means re-indexing, never data loss
(the Lucene model: documents are truth, the index is disposable).
Where ChangeRules filter a stream (§8.1), the invariant is tolerance-bounded: the stream is
lossless *relative to the rules in effect*, and `fold(stream) ≈ current state` within the
declared tolerance — exact equality when no rules are configured.

**P2 — Everything is EMF.**
Queries, patches, series points, keyframes, metadata snapshots are EMF objects and travel through
the same persistence machinery ("dogfooding" — `Query.saveQuery` is the first existing example).

**P3 — Capture happens at the persistence boundary; state heals events.**
No system-wide always-on change notification bus. Change capture is local to the process/
transaction that performs the write. Between systems, the persisted log (pull/replay) or full
state transfer is used — never fire-and-forget events. A lost message costs a sample, not
consistency, because the next full state repairs everything (state-based, self-healing).

**P4 — The stable coordinates are IDs, not names.**
Objects are addressed by stable object IDs, features by stable feature IDs, the interpretation
of values by a context fingerprint. Names are metadata and may change freely.

### 2.1 Architecture overview: three planes

Everything below arranges into three planes — all of them EMF (P2):

```
┌─ Metamodel plane ─────────────────────────────────────────────────────────┐
│ Ecore + aspect models: eorm, codec mapping, docs, privacy, units,         │
│ tracking config (§8) — registered in the aspect registry (§3.2)           │
│ → declares WHAT is persisted/tracked and HOW                              │
├─ Data plane ──────────────────────────────────────────────────────────────┤
│ Current state (EObjects, as today) + change streams (ChangeSets /        │
│ series samples — themselves EMF objects) + metadata snapshot chain (§6)   │
│ → the current state is the materialized view of the stream (P1)           │
├─ Service plane ───────────────────────────────────────────────────────────┤
│ PersistenceResource / EntityManager (today)                               │
│ QueryService     — query model → backend interpreter (§3.1, §14)          │
│ PatchService     — record / apply / invert / conflict-check (§5, §13)     │
│ SeriesService    — append / range query / housekeeping (§9, §10)          │
│ FingerprintService + snapshot registry (§6)                               │
│ backend adapters (EclipseLink, Mongo, …), each declaring which            │
│ capabilities it serves natively                                           │
└───────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Existing assets and their roles

### 3.1 Query model (`fennec.common.models/org.eclipse.fennec.query.model`)

`Query` (subjects = FeaturePaths, where-tree of `Comparator`s, sortBy/groupBy/limit/skip/distinct,
`saveQuery`) is backend-neutral and covers the read case. It becomes the **source model of a
per-backend interpreter SPI**: a `QueryProcessor` service per backend translates the EMF query
into the native form (JPQL/Criteria for EclipseLink, filter documents for Mongo).

Known gaps to close:
- **Capability declaration per backend.** Not every backend supports everything (e.g. `Average` +
  `groupBy` over deep FeaturePaths). Processors declare native capabilities and fail with
  Diagnostics rather than silently post-filtering in memory.
- **Parameters/placeholders** for prepared queries (near-mandatory once `saveQuery` is used).
- **Typed comparator values** — comparators carry `EString` values today; conversion belongs in
  one place (the `ConverterService`), not in every backend processor.
- **Non-EObject projections** — `aliasFeature` hints at it, the result type is unspecified.
- **Time dimension** — `asOf` (point-in-time reads) and range queries over series (§14).

**CRUD:** Insert and Delete are trivial (Delete = query selector). **Update = query selector +
patch** — once the patch model exists, the query language needs no update vocabulary of its own.

### 3.2 Metadata service (`org.eclipse.fennec.emf.osgi.metadata`)

The metadata model already has the aspect-registry structure this concept needs, keyed by Ecore
elements. Aspects are separate models (not EAnnotations) — correct, because aspects have their
own lifecycle, authors and loadability.

> **Changed with the move to `emf.osgi` (2026-07-31).** The typed aspect hierarchy this section
> was written against — `Aspect` / `PackageAspect` / `ClassAspect` / `FeatureAspect` plus
> `PackageProfile` / `ClassProfile`, extended by consumers in their own Ecore — is gone.
> Replacement: one opaque `AspectEntry { typeId, content: EObject (containment), diagnostics }`,
> so an aspect model no longer needs an Ecore dependency on the metadata model. Consequences for
> this concept: aspects are *composed*, not subclassed; every `MetadataService` lookup returns
> `Optional`; the traversal SPI is a single `MetadataHandler` hook
> (`onPackageRegistered`/`onPackageUnregistered`) that walks the mirror tree itself instead of
> per-element `buildXxxAspect` callbacks; the pre-merged annotation hierarchy of the profiles has
> no replacement and must be computed in one's own handler. Registration is keyed by model
> fingerprint, so one nsURI can legitimately hold several live versions —
> `getPackageMetadataVersions(nsURI)` / `getPackageMetadataByFingerprint` select among them. Any
> registry in this concept that keys models by nsURI alone has to key by fingerprint instead.

Generalized, this becomes an **aspect registry**: an OSGi service where aspect models (orm,
codec mapping, docs, privacy, units, tracking config) register and are queried under the key
`(nsURI, EClass, feature)`.

Open question there — *registry vs. index holder* — is resolved by P1: **both, layered.** The
registry is the API (single point of contact as a service interface); the index is a materialized
view behind it (per EClass/object: references to metadata files and to time series), maintained
asynchronously. It must be **reconstructible from the sources**; then it is a cache, not truth,
and the single-point-of-failure concern dissolves.

### 3.3 EMF Compare (`org.eclipse.emf-compare`)

EMF Compare solves *two-state comparison at design time*: both model versions fully in memory,
expensive matching (identifier- or proximity-based), and the `Comparison` model is **not
self-contained** — `Match.left/right/origin` and the diffs reference the *live* EObjects of both
sides. Serialized comparisons hold proxy URIs to states that no longer exist later.

Role in this concept: **optional offline generator** (compare two persisted snapshots →
convert `Comparison` → patch batches) and the reference for **conflict detection** logic
(§13). It is a supplier, not the foundation.

### 3.4 EMF change model (`org.eclipse.emf.ecore.change`)

`ChangeRecorder`/`ChangeDescription` records `FeatureChange`s via adapters, is an EMF model,
supports `applyAndReverse()`. But it references objects directly (not by stable ID), is designed
as a transient undo artifact, and — decisive — **does not carry for state-based ingest** (§8.1):
when a full new state arrives (TTN JSON → Sensor EObject), there is nothing to record.
Role: design inspiration for the recording capture source, not the persistence format.

### 3.5 EMF Edit (`org.eclipse.emf/plugins/org.eclipse.emf.edit`)

Command/CommandStack is the **in-memory undo** within an editing domain. The patch batch is its
**persistent counterpart**: one batch = one commit = one editor save. The commit boundary is also
the answer to event noise (§8.2).

### 3.6 sensiNact mapping (`emf.util/org.eclipse.fennec.sensinact.mapping`)

The `PersistenceRuleRegistry` in `sensinact-mapping.ecore` already contains, field-tested, exactly
the change/housekeeping rules required here:

| Rule | Semantics |
|---|---|
| `AbsoluteChangeRule(delta)` | store only if |Δ| ≥ delta (e.g. "track only changes > 0.5") |
| `PercentageChangeRule(percentage)` | store only if change ≥ n% of last stored value ("±10%") |
| `CountChangeRule(n)` | store 1 out of every n notifications |
| `TimeThrottleChangeRule(interval, unit)` | store at most once per interval |
| `DeletionRule(retention, retentionUnit, maxCount, cleanupInterval)` | purge by age and/or cap per resource ("keep last 100 values", "keep 3 days for feature A, 5 days for feature B") |

These are conceptually right but live in the wrong namespace. **They should be lifted into a
neutral Fennec tracking model** (the series/changelog config aspect); the sensiNact mapping then
references them. The same rules must apply on the JPA, Mongo and sensiNact paths.

---

## 4. The coordinate system

Every change entry carries six coordinates:

```
(objectId, featureId, sequence, timestamp, contextFingerprint, delta)
 identity  location   order     domain time interpretation      change
```

### 4.1 Stable object IDs — the identity contract

**Precondition for everything else.** Patches, series, keyframes address objects by ID. The
requirement follows **stream visibility**, not containment per se: whatever appears in a stream
— as entry subject or as reference target in a delta value — needs a stable ID, and the
requirement inherits *down* tracked containments (children referenced in containment deltas
need IDs even when their own features are untracked). This is a framework contract, no longer
"solved differently per backend" (decides open decision §17.1).

Identity is declared per containment feature in the tracking aspect (`IdentityStrategy`;
aggregate roots use `NATURAL` or `SYNTHETIC`):

- **`NATURAL`** — the EMF ID attribute, or declared key features (`matchKeys`, reusing the
  id-strategy infrastructure of the metadata model, §3.2). **Required** for multi-valued
  containments filled by snapshot ingest (§7.1) — nothing else can correlate fresh
  deserializations.
- **`SLOT`** — identity *derived from the place*: `parentId/featureId` (single-valued — always
  safe, the slot can never reorder) or `parentId/featureId[index]` (multi-valued — **only**
  where the index is domain identity, e.g. fixed channel positions; reorderable lists must not
  use it). This is the answer for value-like children without any ID, like
  `GeoLocation{lat, lon}`: the identity is "the location *of* sensor 42" — replacing the child
  instance is indistinguishable from mutating it *by design*, and the `lat` series stays
  coherent across instance churn. Slot IDs are deterministic and coordination-free (every node
  derives the same ID — ingest-friendly). Constraints: no incoming cross-references, no
  migration. **Re-parenting a slot-identified child is not a move but an end and a beginning**:
  the history of `42/f3` ends (`DELETE`), the history of `43/f3` begins (`CREATE` + values),
  transaction-linked when two streams are involved — fully logged, deliberately without a
  `MIGRATE` pair (an origin reference would smuggle instance identity back in). The modeling
  question is therefore: *does the history belong to the place or to the thing?* Place → `SLOT`
  (re-parent = end + begin); thing (e.g. a battery pack swapped between devices, tracked for
  its own wear history) → `NATURAL`/`SYNTHETIC`, and re-parenting becomes a migration whose
  history follows (§5.4e).
- **`SYNTHETIC`** — UUID minted at creation and **persisted with the current state**: the
  correlation stream-ID ↔ persisted object must survive the DB roundtrip (for JPA this can mean
  a schema addition — enabling tracking is then a migration). Fine for RecordingCapture paths;
  **not usable** for pure snapshot ingest of multi-valued children (a fresh deserialization
  cannot be correlated to a UUID it never carried).
- **`EMBEDDED`** — no identity: the child is an atomic *value* of the parent feature (the whole
  encoded object travels in `SET`/`ADD` deltas, analogous to `ArrayMode.ATOMIC`). No
  `CREATE`/`DELETE`, no matching, no ID inflation for value objects (`Money{amount,currency}`).
  Constraints: not referenced from elsewhere, not tracked itself.

`AUTO` resolves: EMF ID attribute present → `NATURAL`; single-valued containment → `SLOT`;
otherwise tooling demands an explicit choice (a multi-valued containment without natural key is
a *decision*, not a default).

#### Identity rules (normative)

Binding for capture, apply, housekeeping and validation (decided 2026-07-18, §22 decision #9):

- **I1 — Stream visibility requires identity.** Whatever appears in a stream — as entry subject
  or as reference target in a delta — has a stable ID; the requirement inherits down tracked
  containments. `EMBEDDED` children are the sole exception: they never appear as subjects or
  targets, only as encoded values.
- **I2 — `SLOT` identity is place history.** `parentId/featureId` (single-valued: always
  permitted) or `parentId/featureId[index]` (multi-valued: only with declared positional
  semantics — reorderable lists never). Slot IDs are derived, deterministic and
  coordination-free; every node computes the same ID.
- **I3 — Re-parenting a `SLOT` child is an end and a beginning.** `DELETE` of the old slot
  object + `CREATE` of the new one with value copy, linked by `transactionId` when two streams
  are involved. Never a `MIGRATE` pair. History continuity across re-parenting is deliberately
  *not provided* — if it is needed, `SLOT` is the wrong strategy (I5).
- **I4 — `SLOT` objects take no incoming cross-references.** Their identity dissolves on
  re-parenting, so nothing outside the parent may point at them; validation enforces this.
- **I5 — `NATURAL`/`SYNTHETIC` identity is thing history.** Re-parenting across aggregates is a
  migration pair (§5.4e); the history follows the object.
- **I6 — `SYNTHETIC` IDs persist with the current state.** The stream-ID ↔ object correlation
  must survive the DB roundtrip (JPA: possibly a schema addition); `SYNTHETIC` is unusable for
  snapshot ingest of multi-valued children.
- **I7 — Multi-valued snapshot ingest requires `NATURAL` matchKeys.** Without them capture
  degrades to replace-all (log churn) and tooling warns.
- **I8 — `AUTO` resolution.** EMF ID attribute → `NATURAL`; single-valued containment →
  `SLOT`; otherwise an explicit choice is demanded.

### 4.2 Stable feature IDs (the protobuf discipline)

The tracking aspect model assigns every tracked feature an **immutable ID** (the protobuf field
number principle). Log entries store the feature ID, never the name. Consequences:

- A rename `temp` → `temperatur` changes *only metadata* — zero log entries. This resolves the
  fan-out problem: with 1M instances of an EClass, a naive "inject model events into object logs"
  design would write the rename event 1M times. With feature IDs, it is written **zero** times
  (once in the metadata snapshot chain, §6.4).
- Discipline rules (enforced by tooling where possible): an ID is bound to the *semantics* of a
  feature, not its name; IDs are never reused; renames never change the ID.
- Discipline cannot be enforced across repo boundaries and years — which is exactly why the
  context fingerprint (§6) exists as a safety net underneath.

The natural home for the ID assignment is the eorm/metadata aspect (where feature-level mapping
config already lives).

### 4.3 Sequence vs. timestamp — two times

Sensor reality has **event time** (device timestamp; can arrive late and out of order — LoRaWAN
retransmits) and **commit time** (when the entry lands in the log). If "version = timestamp" and
a late packet arrives, versions are no longer append-monotonic. Therefore:

- `sequence` — **per-stream monotonic long**, authoritative order. Used for gap detection,
  replay, undo, idempotency.
- `timestamp` — domain attribute (event time). Used for range queries and time series semantics.

In the simple case both coincide (sequence may literally be epoch millis when the producer is
the single writer); the model carries both fields so the first late uplink does not corrupt the
order.

**Streams are single-master (normative, §22 decision #6).** The contiguous sequence requires exactly
one sequencer per stream: the **canonical log store assigns sequences on write**. Producers
(e.g. two gateways hearing the same sensor) are *upstream* of sequencing — they deduplicate via
`ChangeSet.id`, and gap detection exists only from the store towards replicas. Replicas are
read/apply-only. Multi-master writes to one stream are impossible *by construction* — this is a
declared constraint, not an oversight; cross-node synchronization is state-based or
replay-based (P3, §12). *Time series and changelog are technically the same thing* — the series is a changelog
whose version happens to be time-correlated; unification is intentional (§9).

### 4.4 Context fingerprint

See §6. An 8-byte content-derived value identifying the interpretation context (model +
log-relevant metadata) under which the entry was written.

---

## 5. The change entry and patch metamodel

### 5.1 Entry structure (sketch, names TBD)

```
ChangeSet (= batch)                     // one commit / one editor save / one ingest message
  id            : UUID
  streamId      : object/aggregate stream this batch belongs to (§5.4)
  sequence      : long                  // per-stream, contiguous
  timestamp     : long                  // event time (epoch millis)
  commitTime    : long                  // ingest/commit time
  contextFp     : bytes[8]              // composite context fingerprint (§6.4, §22 decision #2)
  author/cause  : provenance (user, device, import job, ...)
  entries       : ChangeEntry[*]        // ordered!

ChangeEntry
  objectId      : stable object id
  eClassRef     : via contextFp + class id (not by name)
  kind          : CREATE | SET | UNSET | ADD | REMOVE | MOVE | PUT | REMOVE_KEY
                | SET_AT | RESHAPE | DELETE | KEYFRAME
  featureId     : stable feature id     // absent for CREATE/DELETE/KEYFRAME
  address       : sub-feature address (§5.4h/i) — index:int (ordered lists),
                  key:encoded literal|objectId (maps), coords:int[] (arrays)
  toIndex       : int                   // MOVE only
  valueOld      : encoded literal | objectId   // enables inversion + conflict detection
  valueNew      : encoded literal | objectId
  state         : full feature map      // KEYFRAME only (§10)
```

Values are encoded literals (via `ConverterService`/emf.codec); references carry the target
object ID. `valueOld` makes every entry **invertible** (undo = swap old/new, reverse ADD/REMOVE)
and gives cheap conflict detection. `valueOld` is always the last **stored** value
(rule-relative closure, §8.1 R4): per `(objectId, featureId, address)` it equals the predecessor
entry's `valueNew`.

Note that the **ChangeSet header is already the object-level audit record**: "who changed this
EObject, when, why" is the sequence of batch headers of its stream — no separate object-level
stream exists or is needed (it would be dual bookkeeping, violating P1). The object history view
is a projection over headers (§15); audit *without* value capture is a class-level tracking mode
(§8).

### 5.2 Delta kinds

| kind | meaning | inverse |
|---|---|---|
| `CREATE` | genesis of an object (id, class, initial values may follow as SETs in the same batch) | `DELETE` |
| `SET` / `UNSET` | single-valued feature | `SET`/`UNSET` with swapped values |
| `ADD` / `REMOVE` | many-valued feature, at `index` | `REMOVE`/`ADD` |
| `MOVE` | many-valued feature, `index` → `toIndex` | `MOVE` back |
| `PUT` | map feature, addressed by `key` | `PUT` with swapped values; if key was absent: `REMOVE_KEY` |
| `REMOVE_KEY` | map feature, key removed | `PUT` restoring the old value |
| `SET_AT` | array cell at coordinate vector `coords` | `SET_AT` with swapped values |
| `RESHAPE` | array dimensionality change (`dimsOld` → `dimsNew`) | `RESHAPE` back |
| `DELETE` | tombstone; object leaves the stream | `CREATE` + replay (or keyframe restore) |
| `KEYFRAME` | full tracked state of an object at this sequence (§10) | n/a (informational) |
| `TOUCH` | audit mode `TOUCHED_FEATURES` (§8): feature was modified, values elided | n/a (informational) |
| `MIGRATE_OUT` | object leaves this stream (moved to another aggregate); `valueNew` = target streamId (§5.4e) | mirrored `MIGRATE_IN`/`OUT` pair |
| `MIGRATE_IN` | object enters this stream by migration; `valueOld` = origin `streamId#seq`; followed by a `KEYFRAME` (§5.4e) | mirrored pair |

### 5.3 Worked example (single object)

Model genesis (metadata chain, §6.4): `EClass 0 (Sensor)` with `feature 0 (id)`,
`feature 1 (temp)`, `feature 2 (hum)` — model sequence 0, fingerprint `fpA`.

Instance stream of Sensor `42`:

```
seq 0  fp=fpA  obj=42  CREATE  (class 0)
seq 0  fp=fpA  obj=42  SET  f1  old=n/a  new=6      // same ChangeSet as CREATE
seq 1  fp=fpA  obj=42  SET  f2  old=n/a  new=10
seq 2  fp=fpA  obj=42  SET  f1  old=6    new=12
seq 3  fp=fpA  obj=42  SET  f1  old=12   new=50
```

Every entry is self-contained interpretable: `(fpA, f1)` resolves to "Sensor.temp, double, °C"
via the snapshot registry — regardless of what the model looks like today.

### 5.4 References and nesting: the full taxonomy

Ecore references span several independent axes, and the log must handle every combination
explicitly: containment vs. cross-reference, single- vs. many-valued, `ordered`/`unique`
variants, unidirectional vs. bidirectional (`eOpposite`), nesting depth (containment in
containment), self-references (with cycles) and cross-aggregate references.

**Base rules:**

1. **Every stream-addressable object has a stable ID** per the identity contract (§4.1:
   `NATURAL`, `SLOT`, `SYNTHETIC` — or none for `EMBEDDED` value children, which appear only as
   encoded values). *Free* path addressing ("third element of `locations`") remains forbidden —
   it breaks under reordering; `SLOT` identity is the disciplined exception where the slot *is*
   the domain identity.
2. **Entries are flat; containment is a reference delta.** A containment feature holds child
   object IDs like any reference. The tree shape is reconstructed from containment deltas.
3. **Stream granularity: one stream per aggregate root** (per top-level object the persistence
   layer stores). Child entries carry their own `objectId` but live in the root's stream. This
   keeps sequences and gap detection per aggregate and batches atomic within one stream.
   **Cross-aggregate batches** (one editor save touching two roots) are split into one batch per
   stream, linked by a shared batch/transaction ID — atomicity across streams is then a
   transaction concern of the backend, not of the log format (open decision §17).

**Per axis:**

**(a) Single-valued containment.** `SET` with child ID. Replacing a child (`old=→A new=→B`)
*displaces* A — EMF removes it from the container. The batch must make the consequence explicit:
either `DELETE` tombstones for A's subtree, or A's re-attachment elsewhere (implicit move, see (e)).
Never implicit — standalone interpretability and invertibility must not depend on EMF's live
invariant enforcement at apply time.

**(b) Many-valued features (containment or cross).** `ADD`/`REMOVE`/`MOVE` with `index`.
The Ecore flags matter:
- `ordered=true`: indices are semantics; entries must apply in order (strict fault policies, §11).
- `ordered=false`: the index is a storage artifact — `ADD`/`REMOVE` **commute**; relaxed fault
  policies and out-of-order application become legal for these features.
- `unique=false`: lists may hold duplicates — `REMOVE` always addresses by index, never by value.

**(c) Bidirectional references (`eOpposite`).** EMF maintains the inverse automatically; naively
recording both sides would double every change and break apply (after the first entry
auto-updates the inverse, the second entry's `valueOld` guard fails). Rule: **exactly one
canonical side is recorded**; the inverse is derived — on apply by EMF's own eOpposite handling,
in queries/projections by the reader. Canonical choice:
- containment/container pairs: the **containment side**. `eContainer` is never recorded.
- non-containment pairs (incl. many-to-many): a deterministic rule — proposal: the side with the
  **lower stable featureId**. The choice is fixed in the tracking aspect and is part of the
  interpretation context (§6): readers must know which side carries truth.

Two consequences, decided and empirically validated (§22 decision #3, dynamic-EMF notification test
2026-07-18; mechanism: `EcoreEList.eInverseAdd`, EMF sources):
- **Capture normalizes.** EMF fires notifications on *both* sides of a bidi change (one logical
  change = two events; order depends on the mutation path). The capture records the canonical
  side regardless of which side the code mutated, and must not depend on notification order.
- **Cross-aggregate bidi pairs: the inverse side is a projection.** If the canonical side lives
  in another aggregate's stream, replaying one stream alone reconstructs the inverse-side
  feature *incompletely* — by design. `get-full`/resync therefore always serves the
  current-state projection (complete, because apply maintains eOpposites), never a single-stream
  replay; queries on incoming references use the index. Tooling warns when a bidi pair's
  canonical side lies outside the referrer's aggregate, so the modeler can choose the canonical
  side deliberately.

**(d) Containment in containment (deep nesting).** Flattening handles arbitrary depth — each
level is just another object with its own `CREATE` and a containment delta on its parent.
Containment cannot cycle (EMF invariant), so a topological order always exists: **creation
parent-first, deletion tombstones child-first**, all within one atomic batch.

**(e) The single-container invariant (implicit moves).** An object has exactly one container.
Adding child X to B while X is contained in A is an implicit move — the log makes it explicit:
`REMOVE` from `A.f` + `ADD` to `B.g` in one batch (or an explicit cross-object `MOVE` delta,
open decision §17). Same rule for the single-valued displacement in (a). This covers moves
*within* one aggregate; when old and new container belong to **different aggregates**, the move
spans two streams and becomes a **migration pair** (decided, §22 decision #4), linked by
`transactionId`:

```
Stream S42 (batch a, txn T):              Stream S43 (batch b, txn T):
  obj=42  SET f3  old=→99  new=null         obj=99  MIGRATE_IN   valueOld="S42#7"
  obj=99  MIGRATE_OUT  valueNew="S43"       obj=99  KEYFRAME  state=[lat=52.51, lon=13.40]
                                            obj=43  SET f3  old=null  new=→99
```

`MIGRATE_OUT` acts as a tombstone in the source stream (with a forward reference),
`MIGRATE_IN` as a genesis in the target stream (with an origin reference); the `KEYFRAME` makes
the receiving stream replay-self-contained. History queries follow the origin/forward
references across streams. Rules:

- **One pair per migrated object — children are never implied.** A migrating subtree gets
  explicit pairs for *every* contained object (otherwise a per-object history query would have
  to reconstruct the historical containment topology just to find the right stream).
  `MIGRATE_OUT` entries child-first (like deletion), `MIGRATE_IN`+`KEYFRAME` parent-first
  (like creation).
- **Containment-feature deltas are orthogonal** and use the feature's normal addressing mode
  (§5.4b/h/i): single-valued `SET`, ordered list `REMOVE index=2` / `ADD index=0`,
  map `REMOVE_KEY`/`PUT`. Migration kinds concern stream membership, never feature values.
- **Roots may demote, children may promote.** Stream ownership follows the instance's
  containment state: a root demoted into another aggregate migrates (with its subtree) into the
  host's stream, ending its own stream with `MIGRATE_OUT`s; a child promoted to root
  `MIGRATE_IN`s as genesis of a brand-new stream. `ClassTracking.aggregateRoot` declares the
  *capability* (may instances own a stream), not the per-instance state.
- **Context resolution is uniform**: both batches stamp the same composite root (§6.4), so
  class/feature ids resolve identically on both sides.
- **Undo of a migration** is the mirrored pair, again transaction-linked across both streams.

**(f) Self-references and cycles.** Self-*containment* (`Category.subCategories: Category`) is
ordinary nesting — a self-nested tree is one aggregate, depth unbounded. Non-containment self- or
cross-references may form **cycles** (`Person.knows: Person`); a topological entry order then
does not exist. Rule: batches apply in **two phases** — phase 1: all `CREATE`s; phase 2: all
feature deltas. Forward references within a batch are legal (the deferred-constraints pattern).
This subsumes (d)'s ordering rule and makes an object referencing itself trivial.

**(g) Cross-aggregate references.** A non-containment reference to another root carries the
target's object ID; the target lives in a *different stream*. Apply does not require the target
to exist (proxy semantics) — streams synchronize independently, so cross-aggregate consistency
is eventual by construction. Dangling references are therefore a configurable *policy*, not an
error class: on target `DELETE`, per reference — `UNSET`/`REMOVE` the referrer's value (the
FK-nullify analog), `CASCADE` (tombstone the referrer too; rare), or `KEEP` (dangling allowed,
resolved lazily, queries see a proxy). Configured in the tracking aspect; interacts with the
fault policies (§11).

**(h) Maps (EMap).** In Ecore an EMap is technically a many-valued containment of `Map$Entry`
EClasses (cf. `StringToStringMap` in sensinact-mapping). Logging it literally (entry-object
`CREATE` + key/value `SET`s + `ADD`) works but is noisy and — worse — *loses map semantics*:
maps are **key-addressed**, and index-based conflict detection is simply wrong for them. Rule:
the tracking aspect marks such features as `MAP`, and capture emits first-class map deltas —
`PUT(key, valueOld, valueNew)` / `REMOVE_KEY(key, valueOld)`. Keys are encoded literals (or
object IDs for object keys); containment *values* still get their own `CREATE`/`DELETE` like any
child (§5.4a applies per key). Entry EClasses then need no synthetic IDs and no genesis.
`PUT`s on distinct keys commute; same key = LWW/conflict, exactly like `SET`.

**(i) Arrays, including multidimensional.** Ecore has no native multidimensional feature —
arrays appear either as an EAttribute with an array/tensor data type or as nested lists via
intermediate EClasses (which reduces to (b)+(d)). For genuine array features the tracking aspect
chooses one of two representations:
- `ATOMIC` (default): the whole array is one value — plain `SET` with the encoded array as
  literal. Right for the sensor world, where arrays are measurement frames (spectra, images,
  matrices) replaced wholesale per timestamp; in the TIMESERIES profile each sample is the whole
  frame, and storage-level delta compression is a backend concern, not a log concern.
- `ELEMENT_WISE`: per-cell deltas `SET_AT(coords[n], valueOld, valueNew)` with a coordinate
  *vector* (n-dimensional), plus `RESHAPE(dimsOld, dimsNew)` for dimension changes. Right for
  large, long-lived, editable grids. `SET_AT`s on distinct coordinates commute; the declared
  dimensionality/element type is part of the interpretation context (§6).

**Addressing modes — the unifying view.** (b), (h) and (i) are one concept: a delta addresses a
*slot within a feature*, and the addressing mode determines commutativity and conflict
granularity uniformly:

| feature shape | address | deltas | distinct addresses commute? |
|---|---|---|---|
| single-valued | — | `SET`/`UNSET` | n/a (same slot = LWW/conflict) |
| ordered list | `index` | `ADD`/`REMOVE`/`MOVE` | **no** (indices shift) |
| unordered multi | value itself | `ADD`/`REMOVE` | yes |
| map | `key` | `PUT`/`REMOVE_KEY` | yes |
| array | `coords[n]` | `SET_AT`/`RESHAPE` | yes (`RESHAPE` conflicts with everything) |

Conflict detection (§5.5) and the commutativity rules for fault policies (§11) always operate on
`(objectId, featureId, address)` — one rule, five shapes.

**Examples.** Subtree creation — Sensor gets a nested `GeoLocation` child
(feature 3 = `location`, containment, with eOpposite `GeoLocation.sensor`):

```
ChangeSet seq 4 (one batch, atomic, two-phase apply):
  obj=99   CREATE  (class GeoLocation, synthetic id 99)     // phase 1
  obj=99   SET  lat   old=n/a  new=52.51                    // phase 2
  obj=99   SET  lon   old=n/a  new=13.40
  obj=42   SET  f3    old=null new=→99      // canonical containment side;
                                            // GeoLocation.sensor is derived, never logged
```

Implicit move *within one aggregate* — location 99 moves between two containment features of
the same Sensor (f3 `location` → f4 `previousLocations`):

```
ChangeSet seq 7 (one batch, one stream):
  obj=42   SET  f3    old=→99  new=null     // explicit removal from old feature
  obj=42   ADD  f4    index=0  →99          // attachment to new feature
  // no DELETE: 99 survives, it moved. Invertible, standalone-interpretable.
```

(A move between two *aggregates* — e.g. Sensor 42 → Sensor 43, both roots — is a migration
pair instead, see (e): two streams, two batches, `MIGRATE_OUT`/`MIGRATE_IN` + `KEYFRAME`.)

### 5.5 Batching and selective application

One `ChangeSet` = one commit (editor save: several features change "at the same time").

**Coalescing (normative, decided — §22 decision #8).** A ChangeSet contains **at most one entry per
`(objectId, featureId, address)`**: `valueOld` = the value *before* the batch, `valueNew` = the
value *after* it. Intra-batch intermediate states (`temp` set 12 → 15 → 14 within one unit of
work) were never observable reality — the log records `12 → 14`, mirroring the rule-relative
closure idea (§8.1 R4) at batch granularity. Net-zero changes (`old == new` after coalescing)
are not written at all. **Exception: ordered-list features** — index operations do not commute,
their order *is* the semantics, so `ADD`/`REMOVE`/`MOVE` sequences on one ordered feature are
kept as an ordered operation list within the batch (§5.1 orders entries anyway). `CREATE` +
initial `SET`s in one batch (§5.3) are untouched — coalescing only concerns repeated changes to
the *same* address.

Requirements from emf.edit-style tooling:

- **Undo a whole batch**: invert all entries, apply in reverse order.
- **Undo/apply a single entry from the middle of history**: possible because entries are
  invertible — but requires a **conflict check**: has any later entry touched the same
  `(objectId, featureId, address)` (§5.4 addressing modes — ordered lists: overlapping index
  range; unordered features: same value; maps: same key; arrays: same coordinates, `RESHAPE`
  conflicts with all cells; bidi: check the canonical side, §5.4c)? If yes → refuse with
  Diagnostics or
  hand over to a merge decision. This is the one place where EMF Compare's conflict logic
  returns, scoped small. Never "apply and hope".

---

## 6. Interpretation context & fingerprint

### 6.1 The problem, precisely

A log entry `(f1, old=12, new=50)` is only meaningful relative to a model/metadata state.
Two changes alter that meaning:

- **Model refactoring.** Continuing §5.3: a (disciplined) rename `temp → temperatur` is a pure
  metadata event — feature 1 keeps its ID, all log entries stay valid, nothing is written to any
  object log. But an *undisciplined* change — rebinding feature IDs, e.g. swapping
  `1↔temp, 2↔hum` to `1↔hum, 2↔temp` — silently turns "seq 2: f1=12 (12 °C)" and
  "seq 3: f1=50" into *12 degrees followed by 50 % humidity*. Reading the object stream alone,
  the corruption is invisible.
- **Metadata changes with semantic force.** Changing `temp`'s unit from °C to °F is *the same
  class of problem as a rename*: it changes the interpretation of historical values without any
  object-log entry. Units of measurement are not cosmetic metadata once histories exist.

Both are covered uniformly by one principle: *models are just metadata to the instance.* The
**interpretation context** is the set of all metadata whose change alters the reading of
historical values — Ecore structure, feature-ID binding, data types, units, codec mappings, orm
mappings that affect value encoding. (When in doubt, include it: a superfluous fingerprint
change is harmless; a missing one is not.)

### 6.2 The fingerprint contract

The mechanism is deliberately abstracted (hash, Merkle root, structured key — implementation
detail behind a service interface). The contract is three properties:

1. **Reproducible** — deterministically computable from the canonical form of the context.
   Same metadata state ⇒ same fingerprint, on every node, without coordination. (Requires a
   specified canonical serialization: element order, default handling.)
2. **Identifying** — different states ⇒ different fingerprints, negligible collision
   probability. Sizing note: the number of *distinct* metadata states over a system's lifetime is
   tiny (dozens–thousands); an 8-byte truncated content hash is collision-safe for that and
   costs one long per entry — after columnar compression (millions of identical values),
   effectively nothing.
3. **Resolvable** — fingerprint → full state and its provenance, via the snapshot store (§6.4).
   A fingerprint alone identifies; it never *describes*. "The fingerprint says what changed" is
   only true as fingerprint + snapshot registry + diff.

This is the schema-fingerprint pattern known from Kafka Schema Registry (every message carries a
schema ID; consumers resolve against the registry).

### 6.3 Every entry carries the fingerprint

"Absence means unchanged" (delta-encoding the fingerprint) was considered and **rejected as a
logical rule**: it makes entries context-dependent (interpreting seq 3 requires scanning back to
the last entry *with* a fingerprint), breaks random access to log ranges, and — worst — breaks
retention: a `DeletionRule` ("keep last 100") eventually deletes exactly the entry that carried
the last fingerprint, leaving the surviving head uninterpretable.

Resolution: **logically every entry has an effective fingerprint; physically the storage format
may run-length/delta-encode it.** Same data minimization, but as a property of the encoding, not
the semantics. (In columnar storage the fingerprint column compresses to ~nothing anyway.)

### 6.4 Snapshot chain = the model stream

Every metadata snapshot is stored content-addressed (under its fingerprint) and **embeds the
fingerprint of its predecessor** (the Git commit principle). Consequences:

- The provenance chain is contained in the snapshot store itself — **the "model stream" is not a
  separate structure**, it *is* the linked snapshot sequence.
- "Version of an EClass" is a **position in the chain**, not an attribute. No dual bookkeeping
  (a version attribute can diverge from actual changes; the chain cannot — it *is* the changes).
- Any historical model state is addressable; "model as of X" is well-defined.
- Human-readable versions ("1.2") are **tags/labels on chain positions** (the Git-tag principle).
  Machines compare fingerprints/positions; release management attaches names.
- **Bootstrap**: the initial model import writes a genesis snapshot ("EClass Sensor created with
  features …"). Without it, "oldest entry" means "since we started tracking", not "since it
  exists". The same genesis principle applies to object streams (`CREATE`).
- Snapshots referenced by any surviving log entry are **never garbage-collected**.

Fingerprint and chain are not either/or — they are **identity vs. order**: the fingerprint says
(coordination-free) *which* state, the chain says *in which order* and *via which path*. Git
keeps both for good reason — content hashes *and* parent pointers.

**Model governance is centralized (normative, §22 decision #6).** The chain is a linear list — there
is deliberately no fork/merge (no DAG). One writer per package chain; a publish whose
predecessor is not the current head is **refused** ("fast-forward only", like
`git push --ff-only`) and the author rebases. Model evolution is a deliberate governance act,
never a concurrent write path.

Internally, structure the fingerprint as a **Merkle tree** — feature hashes → EClass hashes →
package root, and above that the **composite root**: one hash over the ordered list of
`(packageUri, packageFingerprint)` pairs of all packages in the deployment scope.
**ChangeSets stamp the composite root** (decided 2026-07-18, §22 decision #2): one batch may touch
classes from several packages (cross-package containment within one aggregate), and a single
composite stamp keeps every entry resolvable — `(compositeFp, classId)` → package state →
feature — provided classIds are unique *registry-wide*, not merely per package (tracking
aspect). The `CompositeSnapshot` is tiny (the pair list), content-addressed and
predecessor-chained like everything else; **per-package chains remain the units of evolution
and governance** — the composite merely references their states. Single-package deployments
degenerate cleanly: the composite has one entry and changes exactly when the package
fingerprint does.

"What exactly changed between two composite states" is a cheap tree comparison with drill-down
(composite → package → EClass → feature), so a reader learns precisely whether *its* entries
are affected. This also makes the log-relevant/log-neutral metadata distinction (docs vs.
units) largely self-answering: a documentation edit changes a leaf that no reader of value
semantics consults.

### 6.5 Three layers of defense (summary)

| Layer | Role | Covers |
|---|---|---|
| Feature IDs | discipline — makes the common case (rename) a non-event | renames, moves of names |
| Context fingerprint | safety net — makes *every* change detectable | unit changes, discipline violations, anything unforeseen |
| Snapshot chain | memory — makes changes *interpretable* (upcasting path from fpA to fpB) | type changes, splits/merges, unit conversions |

Reading old entries under a new model = **upcasting on read** (the event-sourcing pattern):
resolve entry's fingerprint → diff to current → apply converters (e.g. °C→°F) on the fly.
Optionally, housekeeping may **compact** old entries to the current context during retention
cleanup (transform-on-write, amortized) — an optimization, never a requirement.

---

## 7. Capture: the hybrid

Two capture sources, one output format (ChangeSets), one downstream pipeline.

### 7.1 SnapshotCapture (state-based ingest) — the common case

Scenario: TTN JSON arrives, is deserialized into a Sensor EObject — a *complete new state*
(new temp, location, device timestamp). There are no change events to record; the
ChangeRecorder architecture does not carry here. **These cases are very frequent.**

The needed comparison is *not* EMF-Compare-like, because **identity is known** (the sensor ID is
in the payload). EMF Compare's core cost — matching which object corresponds to which — vanishes
entirely. What remains is a **keyed snapshot diff**: load last known state (cache/DB), compare
*only the tracked features*, flat, typed — O(#tracked features), no tree matching. Emit the
deltas as one ChangeSet.

One caveat, dissolved by the identity contract (§4.1): for **containment children** the keyed
diff needs the child's identity. Single-valued/`SLOT` is trivial (the slot correlates);
multi-valued requires `NATURAL` matchKeys — without them capture degrades to replace-all
(REMOVE all + ADD all, log churn) and tooling warns; `EMBEDDED` sidesteps matching entirely by
value comparison.

Properties:
- **Self-healing**: state-based transfer means a lost message costs a sample, not consistency —
  the next full state repairs everything. "No change event may be lost" only holds for
  event-based systems; here the state is the truth, not the event.
- **Degenerates to append** for high-frequency numeric features with TIMESERIES profile:
  apply ChangeRules filter, append sample, set current value — no diff at all.

### 7.2 RecordingCapture (editing / programmatic writes)

A recorder (ChangeRecorder-style adapter) buffers into a unit of work and emits the ChangeSet
**only on commit** — CommandStack semantics. This answers the noise/unwanted-events concern:
it is a commit-boundary problem, not a recorder problem. No system-wide always-on recorder;
one per editing session/transaction. The recorder **normalizes bidi notifications to the
canonical side** (§5.4c): EMF fires events on both ends of an eOpposite pair, in
mutation-path-dependent order — one logical change must yield exactly one entry.

### 7.3 Shared pipeline

```
SnapshotCapture ──┐
                  ├─→ ChangeRule filter (deadband/throttle, §3.6) ─→ sinks:
RecordingCapture ─┘                                                  • current state (as today)
                                                                     • change stream (CHANGELOG / TIMESERIES)
```

Distributed rule (P3): change events never travel between systems as the source of truth.
Capture is at the persistence boundary; lossless-ness is required only within one transaction.
Cross-node sync = state-based or replay from the persisted log (§12).

---

## 8. Tracking configuration (the aspect)

Per EClass/feature, in the metadata/eorm aspect plane (following the pattern the `batch`
fetch property already established in the eorm model):

```
TrackingConfig (per class)
  audit         : OFF | HEADER | TOUCHED_FEATURES
                  // whole-object audit without value capture: HEADER records who/when/why
                  // (batch header only); TOUCHED_FEATURES additionally records WHICH feature
                  // ids were touched — but no values (data minimization / privacy).
                  // Composes with feature tracking: features may be NONE while audit is on;
                  // with feature tracking active, HEADER adds nothing (headers always exist).

TrackingConfig (per feature)
  mode          : NONE | CHANGELOG | TIMESERIES
  changeRules   : ChangeRule[*]        // lifted from sensinact-mapping, §3.6
  deletionRule  : DeletionRule         // retention: age and/or maxCount
  keyframe      : every N entries / every T duration / NONE (§10)
  faultPolicy   : per-stream policy overrides (§11)
  featureId     : stable id assignment (§4.2)
  shape         : SINGLE | ORDERED_LIST | UNORDERED | MAP | ARRAY (§5.4 addressing modes)
  arrayMode     : ATOMIC | ELEMENT_WISE  // ARRAY only (§5.4i)
  danglingRefs  : KEEP | UNSET | CASCADE // cross-aggregate references (§5.4g)
```

Concrete examples from the discussion: feature A keeps the last 100 values
(`DeletionRule.maxCount=100`); feature B keeps 3 days, feature C 5 days
(`retention=3/5 DAYS`); track temp only on |Δ| > 0.5 (`AbsoluteChangeRule`) or ±10 %
(`PercentageChangeRule`).

Privacy metadata is not passive here — but its role is deliberately bounded (decided,
§22 decision #7): **collecting personal data is legitimate; what must exist is a purpose and its
documentation.** The privacy aspect therefore primarily *documents*: which features are
personal, the purpose/legal basis, who the data subject is, and the directives in force. *How*
data is protected — encryption, anonymization, pseudonymization, retention limits — is
**dictated externally** (compliance/DPO/customer), never decided by the framework. The
framework contributes mechanics, not policy:

- Externally dictated retention **constrains** the tracking config ("this feature may keep at
  most 30 days of history") — one more reason both aspects live in the same registry plane.
- **Enforcement runs as separate jobs**: the DeletionRule housekeeping and backend TTL
  mechanisms (Mongo TTL indexes, TimescaleDB retention policies). Targeted erasure, redaction
  and crypto-shredding are *capabilities those jobs execute* on instruction — never inline
  magic in the write path (capture may still encrypt values where so directed).
- An **advisor** may analyze models and aspects and *suggest* configurations ("feature
  `location` looks personal, no retention configured") — suggestions always require human
  approval. Privacy configuration is never fully automated.

### 8.1 Rule semantics (normative)

These conditions are binding for capture, apply and housekeeping implementations
(decided 2026-07-18, incl. sub-decision 1a — see §22 decision #1):

- **R1 — No aspect, no stream.** An EClass without tracking config gets current-state
  persistence only: no stream, no audit. Audit is opt-in (`AuditMode`).
- **R2 — Cascading activation.** `mode = CHANGELOG | TIMESERIES` on any feature of a package
  activates that package's snapshot chain; the first activation writes the genesis snapshot
  automatically (§6.4).
- **R3 — Rules gate the stream only.** ChangeRules decide what enters the *stream*; the current
  state always follows live values (the sensiNact semantics: rules control forwarding to
  history, never the live value).
- **R4 — Rule-relative closure.** Per `(streamId, objectId, featureId, address)`, each stored
  entry's `valueOld` equals its predecessor's `valueNew`; the first stored value has no
  `valueOld`. Dropped transitions are *unknown*, not *lost* — sampling semantics: reality
  between stored samples was never part of the persisted record.
- **R5 — Tolerance-bounded fold.** `fold(stream) ≈ current state` within the tolerance declared
  by the active ChangeRules; without rules the equality is exact (per tracked feature).
- **R6 — Rules are context.** ChangeRules live in the tracking aspect and are log-relevant:
  any rule change produces a new snapshot ⇒ new fingerprint (§6). Entries are interpreted under
  the rules of *their* fingerprint.
- **R7 — Baseline after change.** The first value for a feature is always stored; likewise the
  first value after any rule change (new fingerprint) — unconditionally, to establish the
  baseline the next rule evaluation compares against.
- **R8 — Undo restores stored reality.** Inversion targets the last *stored* value — correct by
  declaration, because rules are declared sampling intent.

---

## 9. Storage profiles

Time series and changelog are **technically the same thing** — an ordered, versioned stream of
changes; in the series the "version" is time-correlated. Equivalently, from the other direction:
*diff persistence is a batched time series without (or with) a time element* — the version is an
ascending long: epoch millis, a counter, or merely the entry order. One conceptual model, two
storage profiles:

| | CHANGELOG | TIMESERIES |
|---|---|---|
| granularity | ChangeSets (multi-feature batches) | single-feature samples |
| typical frequency | low (user edits, imports) | high (sensor values) |
| layout | document/row per batch | narrow columnar `(objectId, featureId, seq, ts, fp, value)`; Mongo time-series collections; JPA: narrow table (per series or generic); later TSDB backends |
| entry nature | deltas (old/new) | absolute values — every sample is effectively its own keyframe |
| use | audit, undo, versioning, replication | metrics, dashboards, analytics |

The current-state EObject is the materialized projection of the stream in both cases (P1);
today's persistence remains as-is and *is* that projection. Two representations, one source:
Sensor with current `temp` value + the series for `temp`. Both profiles are **lossless relative
to the ChangeRules in effect** (§8.1): a rule-free CHANGELOG records every transition; with
rules, the stream is the declared sampled reality.

---

## 10. Keyframing

The video-codec idea (I-frames): periodically write a `KEYFRAME` entry — the full tracked state
of the object at that sequence — into the stream.

Purposes:
1. **Bounded replay.** "Object as of seq N" / `asOf` queries start at the latest keyframe ≤ N
   and replay forward — not from genesis.
2. **Retention-safe truncation.** `DeletionRule` cuts *at manifests* (below): everything older
   can go; the stream head remains self-contained. If a cut would remove the last manifest,
   housekeeping synthesizes one first.
3. **Compaction.** Housekeeping may replace long delta runs older than a manifest by the
   manifest itself (changelog → snapshot + recent deltas).
4. **Resync anchor** for transport (§12): a receiver recovering from a gap is served
   "latest manifest + deltas since" instead of the full history (§11.2).

**Stream manifests (decided, §22 decision #5).** Per-object `KEYFRAME`s cannot express *absence* —
and "the aggregate as of seq N" needs all objects. A ChangeSet flagged **`manifest = true`**
carries `KEYFRAME` entries for **every live object** of the aggregate at that sequence:

- **Absence from the manifest = deleted.** Tombstones (`DELETE`; `MIGRATE_OUT` once its forward
  chain is acknowledged) older than the manifest become purgeable — the classic
  tombstone-resurrection problem dissolves, because "object 77 no longer exists" is encoded in
  the manifest's completeness.
- The flag is **explicit**, never a convention ("batch containing only keyframes"): completeness
  *is* the semantics, and a partial keyframe batch must never be mistakable for a manifest.
- `KeyframeConfig` cadence governs manifests; per-object keyframes *between* manifests remain a
  replay optimization.

TIMESERIES profiles rarely need explicit keyframes — every sample is an absolute value, i.e.
its own keyframe; this is also why the series profile is naturally fault-tolerant (§11.3).

---

## 11. Fault tolerance: ordering, gaps, duplicates

Deltas are only applicable when the receiver holds the predecessor state of the stream. The
`sequence` (contiguous per stream) makes anomalies *detectable*; **fault policies** define the
response. Policies are configurable per stream/profile (defaults below).

### 11.1 Anomalies and policies

**Gap (missing sequence)** — seq 7 arrives, head is 5:
- `BUFFER_AND_WAIT(timeout, maxBuffer)` — hold 7, wait for 6 (network reordering case);
  escalate on timeout.
- `RESYNC` — request state (§11.2). Default for CHANGELOG.
- `SKIP_AND_MARK` — apply anyway, record a **hole marker** in the stream. Only sound for
  absolute-valued streams (TIMESERIES) or when a keyframe arrives; the hole is visible to
  queries/projections ("data missing between seq 5–7").
- `QUARANTINE` — stop applying, park the stream, raise a diagnostic. For "must never guess" data.

**Out-of-order (stale entry)** — seq 6 arrives after 7 was applied:
- within a configured **reorder window**: insert, re-apply from 6 (or apply directly if it
  commutes — see below).
- older than the window: CHANGELOG → conflict path (it may invalidate later `valueOld`s) or
  drop-with-audit; TIMESERIES → **accept** — samples are addressed by event time, insertion is
  order-independent; late LoRaWAN uplinks are business as usual.

**Duplicate** — apply is **idempotent** by `(streamId, sequence)`: already-applied sequences are
skipped silently. This makes at-least-once transports safe.

Commutativity note: `SET`s of absolute values with distinct event times commute (last-write-wins
by event time for the current-state projection; both land in the series). **Ordered** list
operations (`ADD/REMOVE/MOVE` with meaningful indices) do **not** commute — streams containing
them must use strict policies (`BUFFER_AND_WAIT` → `RESYNC`). Deltas on **distinct addresses**
commute (§5.4 addressing modes): `ADD`/`REMOVE` on unordered features, `PUT`/`REMOVE_KEY` on
distinct map keys, `SET_AT` on distinct array coordinates — these may keep relaxed policies
(`RESHAPE` is the exception: it conflicts with every cell and forces strict handling).

### 11.2 Get-full (state resync)

The universal repair, and the same mechanism as first-contact synchronization: the receiver
requests the **current full state** of the aggregate (or: latest manifest + deltas since, if it
wants to preserve local history continuity) together with the current sequence watermark, then
resumes delta application from there. This is P3's self-healing applied to replication — the
identical pattern the TTN ingest lives on. Deltas for the normal case, state sync for bootstrap
and repair: one protocol, no special-case zoo.

### 11.3 Default policy matrix

| | CHANGELOG | TIMESERIES |
|---|---|---|
| gap | `BUFFER_AND_WAIT` → `RESYNC` | `SKIP_AND_MARK` |
| out-of-order (in window) | reorder & re-apply | insert by event time |
| out-of-order (stale) | conflict path / drop-with-audit | insert by event time |
| duplicate | idempotent skip | idempotent skip |
| repair anchor | manifest + deltas | n/a (samples self-contained) |

---

## 12. Transport & replication

### 12.1 The stable envelope

**The only schema sender and receiver must share is the diff-Ecore itself** — tiny, generic,
effectively freezable. On the wire (protobuf or any codec), the message schema is the *patch
metamodel*, never the changed domain model. Domain values inside deltas are encoded literals
whose typing resolves from `(contextFingerprint, featureId)` at *apply* time, not receive time.

Consequences:
- **Domain model evolution never touches the wire.** A 2026 receiver can accept diffs from a
  2030 sender without knowing its model. Model changes transport without any protocol change.
- **Store-and-forward for free.** A receiver can accept, persist and forward diffs with an
  unknown fingerprint; only `apply` needs the snapshot.
- **Wire format = storage format = API payload** (the Git pack principle: objects on the wire
  are the objects in the store). Replication is "copy the log"; export is "serialize the log";
  audit is "read the log". No translation layers.

### 12.2 In-band metadata snapshots

So that apply never blocks on a remote registry: metadata snapshots travel **in-band**, in the
same envelope — a model change is itself just a changeset in the snapshot chain, and small.
The predecessor links let the receiver verify chain completeness; a missing link is *pulled* on
demand (never waited for blindly).

### 12.3 The one place needing manual discipline

The diff-Ecore cannot fingerprint-bootstrap itself (chicken-and-egg). It gets the protobuf
doctrine applied by hand: stable field IDs, additive-only evolution, never rebind a field's
semantics. Therefore: keep it deliberately small and boring — the coordinate tuple plus delta,
and resist every temptation to design convenience fields into it. Convenience belongs in
projections.

### 12.4 Replication protocol (sketch)

```
normal operation : subscribe to per-aggregate diff streams (sequence-contiguous)
first contact    : get-full (state or keyframe+deltas) + watermark, then subscribe
gap detected     : fault policy → usually get-full resync (§11.2)
metadata         : snapshot chain replicated in-band; gaps pulled by predecessor link
idempotency      : (streamId, sequence) — at-least-once delivery is safe
sequencing       : single-master — the canonical log store assigns sequences (§4.3);
                   producers dedupe via ChangeSet.id, replicas are read/apply-only
governance       : one writer per package chain, publishes are fast-forward only (§6.4)
```

---

## 13. Undo/redo, selective apply, conflicts

- **In-memory**: emf.edit CommandStack, unchanged — it is the interactive undo within a session.
- **Persistent**: ChangeSets are the durable counterpart (batch = commit). Undo of the last
  batch = invert entries, apply reversed. Redo = re-apply.
- **Selective undo/apply from mid-history** (requirement 6): allowed, guarded by the conflict
  check of §5.5 — a later entry touching the same `(objectId, featureId)` blocks silent
  application and produces Diagnostics / a merge decision. EMF Compare's conflict logic is the
  reference implementation for this check (its only load-bearing runtime role).
- **valueOld as guard**: on apply, an entry whose `valueOld` does not match the current value
  signals divergence (optimistic-concurrency check at delta granularity) — policy: fail,
  force (LWW), or three-way merge.

---

## 14. Query integration

- **QueryProcessor SPI** per backend translates the query model; capability declaration +
  Diagnostics on unsupported constructs (§3.1).
- **Time dimension**: `asOf(sequence | timestamp)` — resolve via latest keyframe ≤ target +
  replay (§10); **series queries** — range/aggregation over TIMESERIES streams (the
  `DateComparator`s exist; what is missing is the notion "subject is a series, not the current
  state").
- **CRUD**: Delete = selector; **Update = selector + patch** — an `UpdateCommand` references a
  Query and a ChangeSet template; no separate update vocabulary.
- Saved queries (`saveQuery`) persist through the same machinery (P2).

---

## 15. Metadata & projections

- **Registry (API)** — the metadata service (§3.2) as single point of contact.
- **Index holder (projection)** — per EClass/object: references to metadata files, to time
  series, to snapshot-chain positions. Asynchronously maintained, rebuildable (P1).
- **Derived versions** — "version of an EClass" = last chain position that touched it
  (a filtered projection over the snapshot chain; well-defined because the chain is totally
  ordered per package). `changeDate` = youngest, `creationDate` = oldest (genesis!) relevant
  chain entry. Stored copies of these are *consistency-checkable* (recompute vs. stored) by
  housekeeping — they are derivation rules, not constraints; the chain stays the truth.
- **Object history / audit view** — "who changed this object, when, why" as a projection over
  the ChangeSet headers of its stream (values elided); the cross-cutting variant ("who changed
  anything today") is the same projection over all streams. Materializable in the index
  (Lucene); never a second stream.
- **Future aggregates** (min/max/avg over series) — same mechanism, different stream:
  continuous-aggregate projections over object/series streams. Deliberately deferred; the
  projection concept keeps the door open at zero extra mechanism cost.

---

## 16. Metamodel evolution — worked walkthrough

Continuing §5.3 / §6.1, with everything in place:

1. **Rename `temp` → `temperatur`** (disciplined): metadata snapshot `fpB` written, predecessor
   `fpA`. Merkle diff: name leaf of feature 1. Object logs: **untouched** (feature IDs).
   Readers resolve `(fpA, f1)` and `(fpB, f1)` to the same semantic feature. Cost: one snapshot.
   (Contrast: the naive inject-into-object-logs design would have written this 1M times for 1M
   instances.)
2. **Unit change °C → °F** on feature 1: snapshot `fpC` (unit leaf changed). New entries carry
   `fpC`. Reading a mixed range: entries stamped `fpA/fpB` are upcast on read (×9/5+32) — or
   compacted lazily by housekeeping. Queries see one coherent series.
3. **Discipline violation** (feature-ID swap `1↔hum, 2↔temp`): snapshot `fpD`; the fingerprint
   change makes the rebinding *detectable* (Merkle: semantics leaves of f1/f2 changed, not just
   names) — tooling can refuse or demand an explicit migration mapping. Without the fingerprint,
   this silently corrupts history (12 °C, then 50 % humidity, §6.1).
4. **Structural change** (feature split/merge, type change): snapshot + an **upcaster**
   registered for the fpX→fpY transition; replay/read applies it. This is the standard
   event-sourcing upcasting chain.

---

## 17. Open decisions

1. **ID strategy contract** — *decided* (§22 decision #9): identity per containment feature via
   `IdentityStrategy` (NATURAL / SLOT / SYNTHETIC / EMBEDDED, AUTO default), see §4.1. Still
   open: where enforcement lives (orm processor vs. runtime validation).
2. **Canonicalization spec** for the fingerprint (element order, defaults, encoding) and
   algorithm/length choice (8-byte truncated content hash proposed; registry-assigned compact
   local IDs as a *measured* optimization only).
3. **Interpretation-context scope** — Ecore structure, feature-ID binding, types, units,
   array dimensionality/element types, map key/value types, canonical-side choices are in;
   codec/eorm mappings in when they affect value encoding; rule of thumb: when in doubt, in.
4. **Context granularity** — *decided* (§22 decision #2): ChangeSets stamp the **composite root**
   (deployment-level Merkle root over the per-package fingerprints); per-package chains remain
   the evolution/governance units; classIds are registry-wide unique.
5. **Cross-aggregate batch atomicity** — shared transaction ID + backend transaction vs.
   log-level two-phase; per-backend capabilities (JPA yes, Mongo multi-doc TX limited).
6. **Transactionality current-state + log** — same TX in JPA (easy); Mongo: change stream entry
   and current-state doc in one multi-document TX or outbox pattern.
7. **MOVE across containers** — explicit cross-object delta vs. REMOVE+ADD pair.
8. **Where the new metamodels live** — `fennec.common.models` (next to query model) proposed:
   patch/stream model, tracking aspect (incl. rules lifted from sensinact-mapping), snapshot
   chain model.
9. **Privacy enforcement** — *decided* (§22 decision #7): the privacy aspect documents purpose,
   subject and directives; enforcement = separate housekeeping jobs + backend TTL mechanisms
   executing externally dictated directives; advisor suggests, human approves. Open remains the
   privacy aspect model itself (where the purpose/subject declarations live).
10. **Reorder-window and buffer sizing defaults** per profile.
11. **Canonical-side rule for non-containment bidi pairs** (§5.4c) — confirm "lower stable
    featureId" or choose an explicit flag in the tracking aspect.
12. **Dangling-reference default policy** for cross-aggregate references (§5.4g) —
    `KEEP` vs `UNSET` as default; per-reference override in the tracking aspect.
13. **Element-wise arrays in v1?** `ATOMIC` covers the sensor cases; `SET_AT`/`RESHAPE`
    (§5.4i) could be deferred to a later phase — but the `address` field and delta kinds must be
    reserved in the diff-Ecore *now* (additive-evolution discipline, §12.3).
14. **Map detection** — how the tracking aspect identifies EMap features (Ecore `Map$Entry`
    pattern) automatically vs. explicit `shape=MAP` declaration.

---

## 18. Phasing & v1 scope

### 18.1 v1 scope (decided 2026-07-18)

The concept is a platform; the biggest project risk is attempting all of it at once. v1 is cut
hard — everything omitted stays **reserved in the models** (the additive-evolution discipline
§12.3 guarantees retrofitting never breaks the wire format).

**In v1:**

1. **Query SPI** + JPA/Mongo bindings (cleanly separable, immediate value; typed comparator
   values via ConverterService; capability model).
2. **Patch/stream metamodel** + apply/invert engine + conflict check (pure model + library,
   testable without any backend).
3. **Capture hybrid** (SnapshotCapture keyed diff; RecordingCapture with commit boundary) +
   CHANGELOG storage profile; tracking aspect with rules lifted from sensinact-mapping.
4. **TIMESERIES profile** with `ATOMIC` arrays only, + retention/housekeeping
   (columnar/Mongo time-series layouts).
5. **Fingerprint + snapshot/composite chain** (canonical form, Merkle, content-addressed store,
   upcasting on read); genesis snapshots for existing deployments.
6. **Manifests**; fault policies **`RESYNC` + `SKIP_AND_MARK` only**.
7. **One transport binding**: CloudEvents over MQTT *or* RabbitMQ — not both.
8. **Metadata projections** (index holder, derived versions) — accompanying, from the moment
   the second aspect (tracking config) exists.

**Explicitly not in v1** (reserved, documented, not built): `ELEMENT_WISE` arrays
(`SET_AT`/`RESHAPE`), selective mid-history undo (batch-level undo only),
`BUFFER_AND_WAIT`/`QUARANTINE` fault policies, continuous aggregates, the
EMF-Compare→ChangeSet offline converter, the privacy advisor, the second transport binding.

**Permanent constraints, not v1 cuts** (§22 decision #6): single-master streams; centralized
model governance (fast-forward-only chains).

Each phase is independently useful; nothing later reworks anything earlier (the coordinates and
principles are fixed now precisely so that this holds).

---

## 19. Technology mapping (deployment view)

How the concept lands on the existing stack — MongoDB, PostgreSQL, Lucene, RabbitMQ, MQTT,
CloudEvents — plus the in-house building blocks Model Atlas (`fennec-model.atlas`, Apicurio- or
file-backed, on `emf.osgi`), DDSR (`kloster-prototype`) and the metadata service
(`org.eclipse.fennec.emf.osgi.metadata`). Guiding rule: every component below implements a *role* from §2.1; the
role contract stays technology-neutral, so any row can be swapped per deployment.

### 19.1 Component → technology

| Concept component | Primary fit | Notes / alternative |
|---|---|---|
| Current state (projection) | MongoDB / PostgreSQL via the existing backends | unchanged — today's persistence *is* this projection |
| CHANGELOG stream | PostgreSQL append-only table (`(stream_id, seq)` PK, deltas as JSONB, partitioned) or MongoDB collection with unique `(streamId, sequence)` index | the classic event-store-on-RDBMS pattern; pick **one canonical log store per deployment**, never mirror the log across two |
| TIMESERIES stream | MongoDB **Time Series Collections**; on PostgreSQL: **TimescaleDB** | Timescale maps 1:1: hypertables = series storage, retention policies = `DeletionRule`, continuous aggregates = our projections (§15), native compression = the fingerprint-column argument (§6.3) |
| Snapshot chain / fingerprint registry | **Model Atlas** | see §19.2 (note: the former Apicurio backend was customer-specific and has been removed) |
| Aspect/metadata registry (API) | metadata service (`org.eclipse.fennec.emf.osgi.metadata`) as OSGi service, artifacts stored via Model Atlas | key `(fingerprint, EClass, feature)`, nsURI only as a secondary index (§3.2) |
| Index holder / search projection | **Lucene** (Model Atlas already ships `management.lucene`) | rebuildable-by-design is exactly Lucene's contract — P1 embodied |
| Projection feeding (async) | MongoDB Change Streams / PostgreSQL `LISTEN/NOTIFY` or logical replication | drives index/aggregate updates without polling |
| Transport envelope | **CloudEvents** | see §19.3 — `dataschema` carries the fingerprint resolution URI |
| Ingest (south) | **MQTT** (QoS 1) | TTN is MQTT anyway; at-least-once + idempotent apply (§11.1) fit; retained messages ≈ a per-topic mini get-full |
| Inter-service backbone (north) | **RabbitMQ Streams** (not classic queues) | streams give server-side replay with offsets ≈ our sequences; classic queues destroy history — use them only for work distribution, never as the log |
| Replication / stream discovery, get-full RPC | **DDSR** channel model | prototype status — see §19.4 |
| Runtime substrate | **emf.osgi** | all services from §2.1 are DS components; EPackages/ResourceSets as services; Model Atlas already builds on it |

### 19.2 Model Atlas as the snapshot chain

Model Atlas is the natural home of the snapshot registry: it already manages EMF models at
runtime behind a REST API, with pluggable storage backends (file-based, Lucene-indexed) on
`emf.osgi`. (An Apicurio Registry backend existed as a customer-specific integration and has
been removed — the schema-registry *pattern* it embodied, content-addressed artifacts +
versions + fingerprint resolution, remains the conceptual reference, cf. Kafka Schema Registry
in §6.2; it is now ours to provide.)

Division of labor:
- **FingerprintService** (new, in or alongside Model Atlas): canonical form and fingerprint
  computation — semantic canonicalization of Ecore + log-relevant aspects, Merkle structure
  (§6.2/§6.4). A byte-level hash of a serialized file is *not* sufficient; canonicalization is
  the essential part.
- **Model Atlas storage SPI**: content-addressed, immutable snapshot storage (store under
  fingerprint, never mutate, never GC while referenced — §6.4). This is a storage-backend
  requirement to add, not a given; the existing file/Lucene backends are the starting point.
- **Chain semantics** (predecessor fingerprint embedded in each snapshot) are validated by the
  FingerprintService on write; human-readable release labels are tags on chain positions (§6.4)
  and align with Model Atlas's model-management workflow.
- The file-backed variant keeps small/edge deployments dependency-free — same registry API, no
  extra infrastructure (matching the in-band transport guarantee, §12.2).

### 19.3 CloudEvents as the wire envelope

CloudEvents is transport-agnostic (MQTT, AMQP/RabbitMQ, HTTP, Kafka bindings) — one envelope
over every hop, which is §12.1 verbatim. Attribute mapping:

| CloudEvents attribute | Concept coordinate |
|---|---|
| `id` | ChangeSet id (idempotency support) |
| `source` | `streamId` (aggregate stream URI) |
| `type` | diff-Ecore message type (stable, frozen) |
| `time` | commit time |
| `dataschema` | **URI into Model Atlas resolving the `contextFingerprint`** |
| `sequence` (extension) | per-stream `sequence` — the official CE sequence extension exists |
| `data` | the ChangeSet payload (emf.codec-encoded) |

`dataschema` pointing at the Model Atlas artifact URL makes fingerprint resolution part of the
standard envelope — receivers that have never seen the model pull the snapshot from there
(store-and-forward still holds: resolution is only needed at apply, §12.1).

### 19.4 Honest notes

- **Kafka is the textbook fit** for the stream layer (log, offsets, compaction ≈ keyframes,
  schema registry) but is *not* in the stack. That is fine: RabbitMQ Streams + the DB-based log
  cover the same roles at lower operational cost; keyframes were designed in-log (§10), so no
  compaction feature is required from the broker. If Kafka ever arrives, it slots into the
  backbone row without concept changes.
- **DDSR is a prototype** (cross-language service registry, EMF wire, request/response + stream
  channels with capability/requirement matching). Its channel model matches the replication
  protocol exactly — diff subscription = stream channel, get-full/resync = request/response
  channel, "who offers which streams" = capability matching. Treat it as the *candidate* for
  stream discovery and the wire-channel abstraction, not as a production dependency yet.
- **MQTT gives no ordering guarantee across reconnects** — that is not a problem but a
  validation: sequence + fault policies (§11) were designed for exactly this transport reality.
- **Two log-capable databases** (Mongo, PostgreSQL) mean per-deployment choice, not redundancy:
  the log format (diff-Ecore) is the portability layer; migrating the log store is "replay the
  log", not an ETL project.

---

## 20. Metamodel drafts (Ecore)

Two draft `.ecore` files live next to this document in `model/` — discussion artifacts, not yet
in a model project (final home: open decision §17.8; no genmodel/codegen yet):

- `model/fennec-stream.ecore` — the **diff-Ecore** (package `stream`,
  nsURI `https://org.eclipse/fennec/stream/1.0.0`)
- `model/fennec-tracking.ecore` — the **tracking aspect** (package `tracking`,
  nsURI `https://org.eclipse/fennec/tracking/1.0.0`)

### 20.1 Package `stream` — the wire/log format

```
ChangeSet                          (one batch = one commit, §5.1)
  id : String (iD, UUID)           streamId : String
  sequence : long                  timestamp : long (event time)
  commitTime : long                contextFingerprint : String
  author, cause : String           transactionId : String (cross-aggregate link)
  manifest : boolean = false       (stream manifest: KEYFRAMEs of ALL live objects, §10)
  entries : ChangeEntry[*] (containment, ordered)

ChangeEntry                        (invertible delta, §5.2)
  objectId : String                classId : int (CREATE; via fingerprint)
  kind : DeltaKind                 featureId : int (-1 for CREATE/DELETE/KEYFRAME)
  index, toIndex : int = -1        key : String            coords : int[*]   ← address (§5.4)
  valueOld, valueNew : String      state : SlotValue[*] (containment, KEYFRAME only)

SlotValue                          (one addressed keyframe slot, §10)
  featureId : int   index : int = -1   key : String   coords : int[*]   value : String

ContextSnapshot                    (per-package chain link = the model stream, §6.4)
  fingerprint : String (iD)        predecessorFingerprint : String (absent = genesis)
  created : long                   author : String
  labels : String[*] (tags)        packageUri : String (chain is per EPackage)
  content : EObject[*] (containment — Ecore + log-relevant aspects)

CompositeSnapshot                  (deployment root — what ChangeSets stamp, §6.4)
  fingerprint : String (iD)        predecessorFingerprint : String (absent = genesis)
  created : long                   packages : PackageFingerprint[*] (containment)

PackageFingerprint
  packageUri : String              packageFingerprint : String (→ ContextSnapshot)

enum DeltaKind { CREATE, SET, UNSET, ADD, REMOVE, MOVE, PUT, REMOVE_KEY,
                 SET_AT, RESHAPE, DELETE, KEYFRAME, TOUCH, MIGRATE_OUT, MIGRATE_IN }
```

Design decisions taken while modeling:

1. **No type flags on values.** `valueOld`/`valueNew`/`value` are encoded strings (literals or
   object IDs); whether a value is a reference, a double, °C or °F resolves *exclusively* from
   `(contextFingerprint, featureId)`. Any type flag in the wire model would duplicate the
   context and could contradict it — the boring model is the correct one (§12.3).
2. **`RESHAPE` dims travel in `valueOld`/`valueNew`** as encoded int vectors — no extra fields
   for one delta kind.
3. **Keyframes are flat slot lists** (`SlotValue`), reusing the same addressing modes as
   entries — no parallel state representation to keep consistent.
4. **`ContextSnapshot` sits in the stream package** because it travels in-band in the same
   envelope (§12.2). Merkle internals stay out — they are FingerprintService implementation,
   not wire format.
5. **The additive-evolution discipline is written into the package documentation** — the one
   schema that cannot fingerprint-bootstrap itself (§12.3).

### 20.2 Package `tracking` — the aspect

```
TrackingRegistry                   (root; rules defined once, referenced — sensinact pattern)
  packages : PackageTracking[*]    changeRules : ChangeRule[*]
  deletionRules : DeletionRule[*]  faultPolicies : FaultPolicy[*]     (all containment)

PackageTracking
  ePackage : →EPackage             classes : ClassTracking[*]

ClassTracking
  eClass : →EClass                 classId : int (stable, never reused)
  aggregateRoot : boolean = true   keyframe : KeyframeConfig
  audit : AuditMode = OFF          features : FeatureTracking[*]

FeatureTracking
  feature : →EStructuralFeature    featureId : int (stable, semantics-bound, §4.2)
  mode : TrackingMode = NONE       shape : FeatureShape = AUTO
  arrayMode : ArrayMode = ATOMIC   canonicalSide : Boolean (bidi only, §5.4c)
  childIdentity : IdentityStrategy = AUTO (containment only, §4.1)
  matchKeys : →EStructuralFeature[*] (NATURAL child key for ingest matching, §7.1)
  danglingPolicy : DanglingRefPolicy = KEEP (§5.4g)
  changeRules : →ChangeRule[*]     deletionRule : →DeletionRule
  faultPolicy : →FaultPolicy       (non-containment refs into the registry)

PersistenceRule (abstract: id, name, description)
  ├─ ChangeRule (abstract)                          — §3.6, lifted from sensinact-mapping
  │   ├─ AbsoluteChangeRule   { delta }
  │   ├─ PercentageChangeRule { percentage }
  │   ├─ CountChangeRule      { n }
  │   └─ TimeThrottleChangeRule { interval, intervalUnit }
  ├─ DeletionRule { retention, retentionUnit, maxCount, cleanupInterval, cleanupIntervalUnit }
  └─ FaultPolicy  { gapPolicy, bufferTimeout(+Unit), reorderWindow(+Unit), stalePolicy } — §11

KeyframeConfig { everyEntries, everyDuration, durationUnit }                          — §10

enums: TrackingMode { NONE, CHANGELOG, TIMESERIES }
       AuditMode { OFF, HEADER, TOUCHED_FEATURES }   — whole-object audit, §8
       IdentityStrategy { AUTO, NATURAL, SLOT, SYNTHETIC, EMBEDDED }   — child identity, §4.1
       FeatureShape { AUTO, SINGLE, ORDERED_LIST, UNORDERED, MAP, ARRAY }
       ArrayMode { ATOMIC, ELEMENT_WISE }        DanglingRefPolicy { KEEP, UNSET, CASCADE }
       GapPolicy { BUFFER_AND_WAIT, RESYNC, SKIP_AND_MARK, QUARANTINE }
       StalePolicy { CONFLICT, DROP_AND_AUDIT, INSERT_BY_EVENT_TIME }
       DurationUnit { MILLIS, SECONDS, MINUTES, HOURS, DAYS }
```

Design decisions taken while modeling:

6. **The tracking model references Ecore elements directly** (`ePackage`/`eClass`/`feature` as
   EReferences into the Ecore metamodel) — the established eorm/sensinact-mapping/metadata
   pattern, resolvable at load time.
7. **Rules are defined once, referenced many times** (non-containment references into the
   registry) — taken verbatim from sensinact's `PersistenceRuleRegistry`; `FaultPolicy` joins
   `ChangeRule`/`DeletionRule` as a third reusable rule family under the same
   `PersistenceRule` base.
8. **`shape = AUTO`** derives the addressing mode from the Ecore flags (many/ordered/unique;
   `Map$Entry` detection is open decision §17.14) — explicit values override, so the common
   case costs no configuration.
9. **`aggregateRoot`** marks stream ownership (§5.4 base rule 3): classes occurring only as
   containment children set it to `false` and write into their root's stream.
10. **The tracking model is itself log-relevant metadata**: id bindings, shapes and
    canonical-side choices affect the reading of historical entries — tracking changes flow
    through the snapshot chain and enter the fingerprint (§17.3). This is stated in the package
    documentation so it survives into generated code.

Duplicates are handled by idempotent apply (`(streamId, sequence)`, §11.1) and therefore appear
in no policy enum — there is nothing to configure.

---

## 21. Glossary

| Term | Meaning |
|---|---|
| **Addressing mode** | How a delta locates the slot it changes within a feature: none (single), index (ordered list), value (unordered), key (map), coordinate vector (array); determines commutativity and conflict granularity (§5.4) |
| **Aggregate root** | Top-level persisted object; owns one change stream; containment children live in its stream |
| **Canonical side** | The one side of a bidirectional reference pair that is recorded in the log; the inverse is always derived (§5.4c) |
| **Capture source** | Producer of ChangeSets: SnapshotCapture (state-based, keyed diff) or RecordingCapture (commit-scoped recorder) |
| **ChangeSet / batch** | Atomic group of ChangeEntries; one commit / editor save / ingest message |
| **ChangeRule** | Deadband/throttle filter deciding whether a change is stored (absolute Δ, %, count, time throttle) |
| **Composite snapshot** | Deployment-level chain link: the list of `(packageUri, packageFingerprint)` pairs, content-addressed and predecessor-chained; its fingerprint (the composite root) is what ChangeSets stamp (§6.4) |
| **Context fingerprint** | Reproducible, identifying, resolvable value naming the interpretation context an entry was written under |
| **Genesis** | First entry of a stream (object `CREATE`) or chain (initial model snapshot) |
| **Interpretation context** | All metadata whose change alters the reading of historical values (model structure, feature-ID binding, types, units, value-affecting mappings) |
| **Keyframe** | Full-state entry in a stream; replay/retention/resync anchor (video I-frame principle) |
| **Keyed snapshot diff** | Flat per-feature comparison of two states of a *known* object identity — no matching, O(#tracked features) |
| **Manifest (stream manifest)** | ChangeSet with `manifest=true`: `KEYFRAME`s of *every* live object of the aggregate; absence = deleted; retention/resync anchor (§10) |
| **Migration pair** | `MIGRATE_OUT`/`MIGRATE_IN` entries, transaction-linked across two streams, recording an object's move between aggregates; per object, never implied for children (§5.4e) |
| **Projection** | Derived, rebuildable, asynchronously maintained view over streams (current state, index, derived versions, aggregates) |
| **Sequence** | Per-stream contiguous monotonic long; authoritative order; gap/duplicate detection |
| **Slot identity** | Child identity derived from the place (`parentId/featureId[index]`): deterministic, coordination-free; replacement ≡ mutation by design; only where the slot is domain identity (§4.1) |
| **Snapshot chain** | Content-addressed metadata snapshots, each linking its predecessor; *is* the model stream; versions are positions, labels are tags |
| **Storage profile** | CHANGELOG (delta batches) or TIMESERIES (absolute samples) — same stream concept, different layout |
| **Stream** | Ordered log of entries for one aggregate (object stream) or one EPackage (snapshot chain) |
| **Tombstone** | `DELETE` entry ending an object's life in the stream |
| **Two-phase apply** | Batch application order: all `CREATE`s first, then all feature deltas — makes forward references and cycles within a batch legal (§5.4f) |
| **Upcasting** | Transform-on-read of old entries into the current interpretation context (unit conversions, structural migrations) |

---

## 22. Decision log (consistency review, 2026-07-18)

Findings of the adversarial consistency review, walked through and decided one by one; each is
folded into the normative text above. This table is the durable record (the former working file
`gaps.md` is retired).

| # | Decision | Folded into |
|---|----------|-------------|
| 1 | Streams are **lossless relative to the ChangeRules in effect**; `valueOld` = last stored value (rule-relative closure); rules are part of the interpretation context (rule change ⇒ new fingerprint, next value stored as baseline); cascading activation with auto-genesis; **rules gate the stream only** — the current state stays live, `fold(stream) ≈ current state` within declared tolerance | §2 (P1), §5.1, **§8.1 R1–R8**, §9 |
| 2 | ChangeSets stamp the **composite root** (one Merkle level above the per-package roots; `CompositeSnapshot` = chained `(packageUri, packageFp)` list); per-package chains remain the evolution units; **classIds registry-wide unique**. Rejected: batch splitting by package (breaks single-stream atomicity), multi-stamp, transient composite | §5.1, §6.4, §17.4, §20.1; stream.ecore, tracking.ecore |
| 3 | Bidi pairs: **only the canonical side is recorded**, the inverse is derived; capture **normalizes** (EMF fires notifications on both sides, order mutation-path-dependent — empirically validated, `EcoreEList.eInverseAdd`); cross-aggregate inverse = projection, get-full serves current state; tooling warns | §5.4c, §7.2; tracking.ecore |
| 4 | Cross-aggregate moves = **migration pairs** `MIGRATE_OUT`/`MIGRATE_IN` (+`KEYFRAME`), transaction-linked; one pair per object, children never implied; OUT child-first, IN parent-first; containment deltas orthogonal (any addressing mode); roots may demote, children may promote (aggregateRoot = capability) | §5.2, §5.4e, §20.1; stream.ecore, tracking.ecore |
| 5 | **Stream manifest**: ChangeSet with explicit `manifest=true` carrying KEYFRAMEs of *every* live object; absence = deleted (tombstone GC safe); retention cuts only at manifests; resync = latest manifest + deltas | §10, §11.2/.3, §20.1; stream.ecore, tracking.ecore |
| 6 | **Single-master declared**: the canonical log store assigns sequences (producers dedupe via `ChangeSet.id`, replicas apply-only); **model governance centralized**: linear chain, publishes fast-forward only; multi-master deliberately not designed | §4.3, §6.4, §12.4 |
| 7 | **Privacy: the framework documents and executes, never decides policy.** Purpose + documentation make collection legitimate (privacy aspect); protection mechanics dictated externally; enforcement = separate housekeeping jobs + backend TTL mechanisms; erasure/redaction/crypto-shredding are capabilities those jobs execute; advisor suggests, human approves | §8, §17.9 |
| 8 | **Batches are coalesced**: at most one entry per `(objectId, featureId, address)`, `valueOld`/`valueNew` = before/after the batch, net-zero not written; exception: ordered-list operation sequences | §5.5; stream.ecore |
| 9 | **Identity contract**: `IdentityStrategy` = AUTO / NATURAL / SLOT / SYNTHETIC / EMBEDDED per containment feature; SLOT = place history (re-parent = end + beginning, never MIGRATE); NATURAL matchKeys required for multi-valued ingest; SYNTHETIC persisted with current state; normative rules **I1–I8** | **§4.1 I1–I8**, §5.4, §7.1, §17.1; tracking.ecore |
| — | **v1 scope cut** — see §18.1 | §18.1 |
