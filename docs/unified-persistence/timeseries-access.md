# Time-series access on the stream model — TIMESERIES profile store SPI + series queries

**Status:** draft concept for discussion (2026-08-05, issue #96). Settled so far:
the ingest-ladder evaluation strategy (§6.1 — expression IR, not the OData evaluator), the
project cut, repository placement and the OData boundary (§12, 2026-08-05); mode separation
per feature and the ClickHouse cut (§13, 2026-08-22 — these reorder the §11 phases and move
`tracking.model` into P1). Everything else remains open.
Companions: `concept.md` §2 (requirements 2/4/5), §7 (capture hybrid), §8 (tracking
aspect), §9 (storage profiles), §10 (keyframing), §14 (query integration), §19 (technology
mapping); `query-ir-redesign.md` (Expression IR, capability discipline);
`query-processor-spi.md` (per-backend translation). Donor pattern for the ingest mapping
(§6): `emf.util/org.eclipse.fennec.sensinact.mapping`. Consumer trajectory:
eclipse-fennec/emf.odata#12 (`$apply` on pipeline stages), emf.odata#11 (`$delta`,
currently a service-layer journal).

---

## 1. The idea

`concept.md` §9 already commits to the unification: *"time series and changelog are
technically the same thing"* — one stream metamodel (`stream.ecore`), two storage profiles.
CHANGELOG batches user edits as deltas; TIMESERIES is single-feature samples at high
frequency, every sample an absolute value and therefore its own keyframe.

What exists is the metamodel and the doctrine. What does **not** exist is any access path:

- no store SPI to append to or replay a stream, in either profile;
- no backend mapping onto the engines that are actually good at series workloads —
  **MongoDB time-series collections** and **PostgreSQL/TimescaleDB**;
- no query vocabulary for series — §14 names the gap verbatim: *"what is missing is the
  notion 'subject is a series, not the current state'"*.

This document proposes the access design in four separable cuts: a stream-store SPI
(§4), series queries in the IR (§5), a declarative ingest mapping for payload-borne
sensor data (§6), and CDC capture as a deliberately later cut (§7).

## 2. What already exists and is load-bearing

| Asset | Role here |
|---|---|
| `stream.ecore` (ChangeSet/ChangeEntry, DeltaKind incl. KEYFRAME, manifests) | the wire/log schema both cuts speak at their boundary; additive-only discipline, stays untouched |
| `sequence` vs. `timestamp` on ChangeSet | ordering vs. event time; late/out-of-order data is legal by design (§4.3) |
| `contextFingerprint` + snapshot registry | value typing resolves from `(compositeFp, classId, featureId)` — samples carry no type flags |
| Query IR + `QueryProcessor` SPI + capability analyzer | the extension seam for the series vocabulary (§5); precedent: #82 ComputeStage |
| Manifests/keyframes (§10) | bounded replay, retention cuts, resync — TIMESERIES rarely needs them (every sample is absolute) |

## 3. The one discipline: wire schema ≠ storage layout

`stream.ecore` is the **only** schema sender and receiver share (concept §12.3). It is NOT
the storage layout. §9 already says the TIMESERIES layout is *"narrow columnar
`(objectId, featureId, seq, ts, fp, value)`; Mongo time-series collections"*.

Persisting `ChangeEntry` objects 1:1 — string-encoded literals, unused `valueOld`, a
per-batch envelope with UUID and fingerprint — into a TS engine forfeits exactly what the
engine is for: columnar compression, time bucketing, chunk/bucket exclusion. The store SPI
therefore accepts and emits `stream.ecore` objects **at the boundary** and maps to the
native layout **inside**. Round-tripping is exact because value typing is recovered via
the fingerprint registry, not from the stored representation.

## 4. Cut 1 — stream-store SPI (ingest, replay, retention)

One service per backend, profile-aware, aggregate-root-scoped like everything else:

```java
public interface StreamStore {
    boolean supports(EClass rootType, StorageProfile profile);   // CHANGELOG | TIMESERIES

    /** Appends one batch; assigns the per-stream contiguous sequence (§4.3). Idempotent
     *  on ChangeSet.id — a replayed transport duplicate is a no-op (§11). */
    long append(ChangeSet batch) throws IOException;

    /** Replays (since, until] in sequence order; entryFilter narrows to features/objects
     *  server-side (a TS engine turns this into a range scan, not a log walk). */
    Stream<ChangeSet> replay(String streamId, long sinceSeq, long untilSeq,
            ReplayFilter entryFilter) throws IOException;

    /** Latest manifest/keyframe ≤ target plus deltas since — the asOf building block (§10). */
    Stream<ChangeSet> asOf(String streamId, long sequenceOrTimestamp, TimeAxis axis)
            throws IOException;

    /** Retention hook: cut at manifests only; synthesizes one first if needed (§10). */
    void truncate(String streamId, RetentionRule rule) throws IOException;
}
```

Shape is a sketch, not a signature contract — whether this is its own service or folds
into `CommandResource`/`QueryableResource` is open question O1 (§10). What is NOT open is
the boundary schema (`stream.ecore`) and idempotency on `ChangeSet.id`.

### 4.1 Backend mapping — MongoDB time-series collections

- One TS collection per aggregate root (or per package — O4): `timeField` = `timestamp`,
  `metaField` = compound `{streamId, objectId, featureId, fp}`, measurement = the decoded
  (typed) value. Mongo buckets and compresses by metaField/time — exactly the access
  pattern of `replay`/series queries.
- `sequence` rides as a plain field; ordering queries sort on it, range queries use the
  time axis (native bucket exclusion).
- Declared refusals (capability, not error): Mongo TS collections do not update or delete
  individual measurements — fine for append-only samples; `truncate` maps to
  `expireAfterSeconds`/partial drops at bucket granularity.
- CHANGELOG profile on Mongo stays a regular collection (document per batch) — TS
  collections buy nothing for low-frequency multi-feature batches.

### 4.2 Backend mapping — JPA narrow table, TimescaleDB as dialect

- Baseline (works on H2/any JPA target): one narrow table
  `(stream_id, object_id, feature_id, seq, ts, fp, val_numeric, val_text)` — generic per
  package, or per-series specialization later (O4). Index `(stream_id, seq)` +
  `(stream_id, feature_id, ts)`.
- **TimescaleDB is not a backend, it is a dialect optimization of this path**: the same
  table becomes a hypertable (`create_hypertable`), `time_bucket()` becomes pushdown for
  the §5 stage, compression/retention policies implement `truncate`. Detection via unit
  configuration, not via a new `QueryProcessor`.

## 5. Cut 2 — series queries in the IR

The §14 gap, closed with two additive IR elements plus capabilities:

1. **Series subject.** A query FROM declaration that a feature's *history* is the subject,
   not the current state: `(rootType, feature, [objectId])` + the time axis. Yields rows of
   `(objectId, ts, seq, value)` instead of EObjects. This is deliberately a *new subject
   kind*, not a magic path segment — the capability analyzer must be able to refuse it
   wholesale on backends without a store.
2. **TimeBucket pipeline stage.** `bucket(width, origin?, timezone?)` as a grouping stage;
   aggregates per bucket via the existing GROUP_BY/AGG_* vocabulary. Pushdown:

| Backend | Translation |
|---|---|
| TimescaleDB | `time_bucket(width, ts)` in GROUP BY (hypertable-optimized) |
| plain SQL/JPA | `date_trunc`/arithmetic bucketing in GROUP BY — correct, unoptimized |
| Mongo (TS collection) | `$dateTrunc` + `$group` — native on TS collections |
| memory engine | reference semantics over the replayed stream |

3. **Later increments, each behind its own capability** (same pattern as #82):
   first/last-per-bucket (Timescale `first()/last()`, Mongo `$first/$last`), gap-filling
   (`time_bucket_gapfill`/`$densify`), delta/rate between consecutive samples, continuous
   aggregates as backend-managed materializations of a saved series query.

New `QueryFeature` constants (naming = O5): `SERIES_SUBJECT`, `TIME_BUCKET`,
`SERIES_FIRST_LAST`, `SERIES_GAPFILL`. The memory engine implements all of them as the
reference; JPA/Mongo declare what they push down.

## 6. Cut 3 — declarative ingest mapping (payload → series)

The dominant real-world source is payload-borne: ChirpStack/TTN uplinks arrive as JSON,
the codec decodes them into EObjects of a *payload model* (`lorawan-uplink.ecore`,
device-specific decoder classes). `concept.md` §7.1 (SnapshotCapture) already covers the
capture mechanics for exactly this scenario — keyed snapshot diff, "degenerates to append
for high-frequency numeric features with TIMESERIES profile" — and §8 covers the *what*:
`TrackingConfig` per feature (`mode`, `changeRules` — themselves lifted from
sensinact-mapping, retention, keyframing).

What §7/§8 presuppose and nothing declares yet is the ***from where***: the payload class
is usually NOT the series subject. Identity, event time and values sit somewhere in the
decoded message, and that binding must be configuration, not code. The donor pattern
exists in `emf.util/org.eclipse.fennec.sensinact.mapping`: a small mapping metamodel
whose instances declare per payload EClass a timestamp strategy (`FEATURE` path vs.
receive time), an identity path, and feature→resource assignments — exactly the shape
needed here, retargeted from sensiNact resources to stream subjects:

```
IngestMapping (per payload EClass)
  subject      : the tracked EClass the payload feeds (the series subject / domain entity)
  identity     : payload feature path(s) → objectId/streamId  (e.g. devEUI)
  timestamp    : FEATURE(path) | RECEIVED                     → ChangeSet.timestamp
                 (commitTime is always ingest time; §4.3 axes stay separate)
  assignments  : payload feature path → tracked feature [× unit hint]
```

Division of labor (deliberately two aspects, one registry plane):

- **IngestMapping answers "where does the sample come from"** — pure extraction/binding.
  It carries NO filtering rules and NO retention: those stay in `TrackingConfig` (§8),
  otherwise there are two competing rule truths (R3/R6 interpretation would fork).
- **TrackingConfig answers "does it enter the stream, and how long does it live"** —
  unchanged.
- The capture pipeline composes them: codec → payload EObject → IngestMapping resolves
  (subject, objectId, timestamp, values) → ChangeRule filter (§7.3) → `StreamStore.append`
  (+ current-state projection per O2).

Like the tracking config, the mapping lives in the metadata/aspect plane (registered per
payload EPackage; open point O7 — aspect entry vs. standalone XMI artifacts like the
sensinact `*.xmi` mappings). Nothing in cuts 1–2 depends on this cut, but the SPI (§4)
should be reviewed against it: `append` must be callable with samples whose subject
differs from the payload class — which the `(streamId, objectId, featureId)` coordinates
already allow.

### 6.1 Extraction strategies — the ladder

Every extraction (identity, timestamp AND value — uniformly) is a two-stage declaration:
a **context path** (where in the payload to navigate) times an optional **transform**.
That yields an escalation ladder, with a binding bias toward the weakest sufficient rung —
the lower the rung, the more tooling can verify statically:

| Rung | Declaration | Typical use |
|---|---|---|
| 1 | feature path, no transform | the 80% case; statically validated against the payload metamodel |
| 2 | path + OCL (short `self` context) | select/compute locally on the navigated element |
| 3 | OCL against the payload root | n:1 combinations (raw × scale, lat/lon → point), conversions |
| 4 | converter service | binary decoding, lookup tables — anything OCL cannot |

Rung 4 is a **DS whiteboard service referenced by name/target from the mapping** (the
codec custom-handler pattern): the XMI stays serializable, the code stays discoverable.
The OCL dialect is the **same m2x OCL used everywhere else** (derived references, OData
`$filter`) — one OCL, not two. Evaluation, however, rides the **in-house expression IR**,
not the OData reference evaluator (decision 2026-08-05): the mapping's OCL text is
m2x-parsed and bridged via `OclToExpr` (the blessed subset), guards evaluate through the
memory engine's predicate path and transforms through its value path (`test()`/`value()`,
issues #84/#87). That buys three things:

- **no odata dependency for the ingest cut** — eclipse-fennec/emf.odata#14 (neutral
  evaluator home) loses its would-be second consumer and the evaluator stays OData's
  internal reference oracle;
- **one evaluation semantics** — the pinned coercion and 3VL rules (#93/#94) apply to
  guards uniformly, and the odata#11 differential suite already guards drift between the
  evaluators;
- **a crisp, tool-checkable rung boundary** — rung 2/3 is exactly the blessed subset;
  whatever `OclToExpr` refuses is rung 4 by definition. Nothing real is lost: n:1
  combinations are arithmetic/concat, iteration is the mapping's own `foreach`, and what
  genuinely exceeds the subset (logarithms for a dew-point formula) exceeds standard OCL
  too — that was always rung 4.

The sensinact-mapping precursor (`collectionIndex`/`collectionFilter` with
"implementation-specific" filter syntax) is exactly rung 2 without a defined dialect.

On top of the ladder, four orthogonal declaration elements per assignment:

- **Iteration (1:n).** A collection-valued context path with a `foreach` marker: each
  element yields its own sample, with a **sub-identity** extracted from the element
  (channel number → part of objectId/featureId resolution). Without this, CayenneLPP and
  multi-channel LoRaWAN payloads cannot be mapped; a fixed `collectionIndex` is the
  degenerate case.
- **Guards.** A boolean OCL predicate per assignment: emit only if the payload warrants it
  (`fPort = 2`, validity flag). Guards answer "is there a datum at all" (payload level);
  ChangeRules answer "does it enter the stream" (history level, §8/R3) — orthogonal
  questions, so the one-rule-truth discipline of §6 holds.
- **Constants.** Literal sources (quality flag, profile discriminator) — without them,
  every literal forces an OCL rung.
- **Missing-member policy.** `eIsSet`-based, per assignment: member absent →
  **`SKIP`** (default: no sample — never silently historize type defaults) **or
  `DEFAULT(<literal>)`** (emit the declared substitute value).

Absent a transform, the raw value is coerced through the EMF converter of the target's
declared type — rung 1 covers string→number without OCL.

### 6.2 Virtual series — subjects not in the Ecore

Historization must not be limited to real `EStructuralFeature`s. Two shapes force this:
computed series (dew point from temp+humidity — rung 3 produces a value no feature holds)
and dynamic channels (a CayenneLPP channel is not a static feature). A **virtual feature**
is declared in the aspect plane exactly like a tracked real one: stable `featureId` from
the §4.2 assignment (which already owns id-space), plus an explicit type declaration —
because `stream.ecore` resolves typing EXCLUSIVELY from `(contextFingerprint, featureId)`,
**virtual feature declarations are fingerprint-relevant** and enter the context snapshot,
consistent with R6 (rules are context). Consequences kept honest:

- a virtual series has **no current-state projection** by definition (nothing to project
  onto); P1/§9 "current state is the materialized projection" applies per *real* feature;
- series queries (§5) address virtual subjects the same way — the subject declaration
  resolves via the aspect, not via `EClass.getEStructuralFeature`.

### 6.3 Enrichment from secondary sources

Payloads are chronically incomplete: static asset data (lat/lon, install position,
customer) lives in a database, not in the uplink. The mapping therefore supports **named
lookups**: a key extracted from the payload (same ladder), resolved as a **keyed find
through the existing persistence machinery** (a configured unit/backend URI + subject
EClass — the `QueryableResource`/`getEObject` path, no ad-hoc access code). Assignments
may then draw from the payload context *or* a lookup context (`asset.lat`).

- **Enrich-at-ingest denormalizes deliberately**: the sample records what was true at
  capture — an asset later moving does not rewrite history (extraction is pre-log, §9);
  future samples carry the new values. Join-at-query stays the later alternative and
  needs series-query joins (not v1).
- The natural landing zone for enrichment values is the series **meta/tag axis** — Mongo
  `metaField` members, Timescale segment-by columns (§4.1/4.2): exactly where TS engines
  want low-cardinality static attributes.
- Operationally: per-key cache with TTL (ingest rates × keyed finds would otherwise be
  the N+1 of capture), and an explicit **failure policy** per lookup: key unresolvable →
  `SKIP` sample | `EMIT_WITHOUT` enrichment | `FAIL` (dead-letter territory, later).

## 7. Cut 4 — CDC capture (deliberately later)

Mongo change streams / Postgres logical decoding as *sources* that emit CHANGELOG-profile
ChangeSets into a `StreamStore` — a durable, cross-process change feed for data that is
also written outside the service layer. Separate cut because the failure modes are
different (resume tokens, WAL slots, snapshot/backfill coordination) and nothing in cuts
1–3 depends on it. Design constraint to keep in view: the capture side must stamp
`contextFingerprint` correctly, which requires the metadata registry at capture time.

## 8. Consumers

- **OData `$apply`** folds onto pipeline stages; with `TIME_BUCKET` OData serves
  dashboard-style aggregations over sensor history via pushdown instead of in-memory work
  (extends the emf.odata#12 trajectory; `$apply` remains OData's own aggregation submodel,
  bridged at the boundary — same division of labor as `$filter`/OCL today).
- **OData `$delta`** currently rides bounded in-memory service-layer journals
  (emf.odata#11). The CHANGELOG profile replaces that with a durable journal shared across
  instances; `asOf` and audit come with it.
- **Derived references over history** (`query-derived-references.md`) get a natural
  extension target: an OCL derivation over a series subject.

## 9. Semantic ground rules (proposed as binding)

1. **Samples are absolute values.** A TIMESERIES entry is effectively KEYFRAME-natured
   (§9): `valueOld` stays unset, invertibility is not a series property.
2. **Coalescing never swallows samples.** ChangeSet coalescing ("at most one entry per
   (objectId, featureId, address)") is a CHANGELOG batching rule. In the TIMESERIES
   profile every sample is its own entry — a temperature returning to the same value is
   two measurements, and batches group transport, not semantics.
3. **Ordering is `sequence`, range access is `timestamp`** — unchanged from §4.3; late
   data lands with a current sequence and an old timestamp, and series queries must not
   assume the axes agree.
4. **Retention cuts at manifests in CHANGELOG, at time horizons in TIMESERIES** — because
   every sample is self-contained, the tombstone/manifest machinery (§10) is not needed
   for the series profile; `DeletionRule`-by-age suffices.
5. **The current-state EObject remains the materialized projection** (P1, §9) — writing a
   sample and updating `Sensor.temp` is one logical operation; which side is authoritative
   during ingest is O2. Virtual series (§6.2) are the declared exception: no projection.
6. **Extraction is pre-log.** Mapping changes (paths, transforms, guards, enrichment)
   never reinterpret stored samples — the stored value is the truth; changes affect
   future capture only. The one exception is deliberate: **virtual feature
   *declarations* are typing** and therefore fingerprint-relevant (§6.2, R6) — their
   evolution produces a new context snapshot like any rule change.

## 10. Open decisions

| # | Question | Leaning |
|---|---|---|
| O1 | SPI shape: own `StreamStore` service vs. folding into `CommandResource`/`QueryableResource` (§14 leans "same machinery") | own SPI for append/replay, series *queries* through the existing `QueryableResource` path |
| O2 | Ingest coupling: does `append` update the current-state projection transactionally, eventually, or not at all (capture-side decides)? | per-profile default: TIMESERIES eventual/optional, CHANGELOG transactional |
| O3 | Who owns housekeeping (manifest synthesis, retention execution) — store-side or a shared service over the SPI? | shared service; stores expose primitives only |
| O4 | Storage granularity: table/collection per aggregate root vs. per package vs. generic | start generic (one per unit), measure, specialize behind the SPI |
| O5 | Capability naming + granularity for the series vocabulary | proposal in §5, to be settled with the IR maintainers |
| O6 | Does `ReplayFilter` belong in the SPI or is replay always whole-stream (filter = query concern)? | in the SPI — TS engines answer filtered ranges natively, whole-stream replay would be the N+1 of series access |
| O7 | Ingest mapping registration: metadata aspect entry per payload EPackage vs. standalone XMI artifacts (sensinact-mapping style) vs. both | aspect plane as the registry, XMI as an authoring format loaded into it |
| O8 | Units in the ingest mapping: hint-only (documentation/metadata plane) vs. converting assignments | hint-only in v1 — conversion is a projection concern, not a capture concern |
| O9 | Virtual feature mechanics: id-space shared with §4.2 real-feature ids vs. own range; where the type declaration lives in the snapshot | shared id-space (one `featureId` axis per class), type declared in the tracking aspect and stamped into the context snapshot |
| O10 | Enrichment default: denormalize into the sample/meta axis at ingest vs. join at query time | denormalize in v1 (§6.3) — capture truth is immutable, TS meta axes want it, series joins do not exist yet |

## 11. Phasing proposal

1. **P1 — store SPI + JPA narrow table + memory reference** (smallest end-to-end slice;
   proves boundary discipline and round-tripping via fingerprint typing).
2. **P2 — series subject + TimeBucket in the IR**, memory reference semantics + JPA
   pushdown (`date_trunc`), TCK differential corpus memory vs. JPA.
3. **P3 — ingest mapping + SnapshotCapture wiring** (mapping metamodel, aspect
   registration, codec→capture→store pipeline against P1; proves the ChirpStack/TTN
   path end to end, see §6).
4. **P4 — Mongo TS collections** (store mapping + `$dateTrunc` pushdown, TCK green).
5. **P5 — Timescale dialect** (hypertable DDL, `time_bucket`, compression/retention).
6. **P6 — series extras** behind capabilities (first/last, gapfill, delta/rate).
7. **P7 — CDC capture** (own concept round first, see §7).

Each phase is issue-sized in the spirit of the #76–#84 wave: JPA + Mongo + memory + TCK
as the definition of done per construct.

## 12. Project cut & consumer boundary (settled 2026-08-05)

### 12.1 Bundle cut in this workspace

New bnd projects, aligned with the §11 phases (naming follows the `*.model` /
backend-suffix conventions):

| Project | Content | Phase |
|---|---|---|
| `org.eclipse.fennec.persistence.stream` | `StreamStore` SPI (append/replay/asOf/truncate), `StorageProfile`/`ReplayFilter`/`RetentionRule`/`TimeAxis`, the shared housekeeping service (O3: stores expose primitives only) **plus the in-memory reference implementation** — P1's "smallest end-to-end slice" is one API bundle + one backend | P1 |
| `org.eclipse.fennec.persistence.stream.jpa` | narrow-table store over the exported `eclipselink.spi` (JPAUnit/lease is API since #65/#90), **including the series-query pushdown** (`date_trunc`); TimescaleDB rides inside as dialect detection — §4.2: not a backend, hence not a project | P1/P2/P5 |
| `org.eclipse.fennec.persistence.stream.mongo` | TS-collection store + `$dateTrunc` pushdown, docking onto the `MongoDatabase` whiteboard (#90) | P4 |
| `org.eclipse.fennec.tracking.model` | `fennec-tracking.ecore` (today only under `docs/…/model/`) — TrackingConfig, virtual features (fingerprint-relevant) | P3 |
| `org.eclipse.fennec.stream.ingest.model` | ingest-mapping metamodel (ladder, foreach, guards, constants, lookups) — deliberately separate from the tracking model, mirroring the two-aspects-one-registry doctrine (§6) | P3 |
| `org.eclipse.fennec.persistence.stream.ingest` | capture pipeline: codec → payload → mapping → ChangeRules → `StreamStore`; OCL evaluation via `expression.ocl` + the memory engine (§6.1), converter whiteboard (rung 4), enrichment lookups with TTL cache | P3 |
| `org.eclipse.fennec.persistence.stream.cdc.*` | CDC sources — own concept round first (§7); name reservation only | P7 |

Deliberately **no** new projects, extended additively instead: series subject + TimeBucket
stage go into `query.model`/`expression.model` (one canonical IR — pattern of #82/#84/#87);
translator extensions live in the existing backend bundles; the stream-store TCK and the
series-query cases extend `org.eclipse.fennec.persistence.tck`.

Why not folded into the existing backend bundles: dependency hygiene. Mongo TS specifics,
Timescale DDL, the m2x parser and codec must not leak into `eclipselink`/`mongo`/
`persistence.query` — a consumer of plain CRUD persistence must not carry series machinery
on its buildpath. New bundles also start at 1.0.0 without baselining pressure on the
existing ones.

### 12.2 Repository question

The stack **starts in this workspace** — P1/P2 co-evolve with the query IR and with the
young backend SPIs (`eclipselink.spi`, the Mongo whiteboard), and cross-repo snapshot
round-trips would tax exactly the fastest-iterating phase. The bnd workspace already
enforces the dependency direction (stream → persistence, never the reverse) per-project.

The natural extraction line, if one is ever drawn, is **capture vs. access — not stream
vs. persistence**: the access side (store SPI, backends, series queries) is inseparable
from the query machinery and stays here; the ingest cut (`tracking.model`,
`ingest.model`, `ingest`) has a different consumer profile (IoT gateways: ChirpStack/TTN,
codec) and different foreign dependencies, and is the designated extraction candidate
from P3 on — once the SPI and IR vocabulary have settled and release cadence actually
diverges. The §12.1 bundle cut is what keeps that extraction a wholesale project move.

### 12.3 OData boundary

OData remains a consumer (§8) — and OData is special, so OData-specific machinery is
welcome to live in `emf.odata` as special solutions. The dividing rule:

- **Protocol semantics → odata-special**: the `$apply` submodel bridge onto pipeline
  stages, `$delta` protocol mechanics (delta links, tokens), and the `OclEvaluator` as
  OData's internal reference oracle (§6.1 — emf.odata#14 is no longer a prerequisite
  here).
- **Persistence, history and aggregation → this stack, bridged at the boundary**: a
  durable `$delta` journal is exactly the CHANGELOG store of cut 1 (the bounded in-memory
  journal in odata is an acceptable interim, its durable successor comes from here), and
  time bucketing is cut 2's pushdown — re-implementing either as an odata special
  solution would fork the truth this concept just unified.

## 13. Mode separation and the ClickHouse cut (settled 2026-08-22)

Two decisions that reorder §11 and change what P1 must contain.

### 13.1 The mode is declared per feature, and there is only one enum for it

§9 has always described two profiles, but nothing in the built code knows about them:
`stream.ecore` carries no profile, and the only `TrackingMode` in existence
(`NONE | CHANGELOG | TIMESERIES`) sits in `fennec-tracking.ecore` under `docs/`, which is
not a bundle. So the distinction the whole concept rests on is, today, prose.

**Settled: the mode is a property of a tracked feature, declared in the tracking model.**
Not of a stream and not of a batch. A device wants `Sensor.temp` as a series and
`Sensor.owner` as an audit trail at the same time, which a per-stream mode cannot express
without splitting one object across two streams. Nor does the mode travel on the
`ChangeSet`: there are millions of those per day, the store knows the declaration, and a
value repeated on every batch is a second truth that can drift from the first.

**What separates the modes is not structure but which fields carry meaning.** Both use the
same `ChangeSet`/`ChangeEntry`, and that is the point of §9 — but:

| Field | CHANGELOG (audit) | TIMESERIES |
|---|---|---|
| `author`, `cause`, `transactionId` | the substance — who changed it, why, in which commit | unset; no human was involved, and per-sample overhead at ingest rates |
| `valueOld` | required for invertibility (undo, conflict guards) | unset by rule (§9.1) |
| `manifest` / KEYFRAME | the retention and replay anchor (§10) | unused — every sample is its own keyframe |
| coalescing | at most one entry per `(objectId, featureId, address)` | never — two equal temperatures are two measurements (§9.2) |
| retention | cuts at manifests | cuts at time horizons |

A store that is told the mode can enforce all six rows. A store that has to infer it from
which fields happen to be filled cannot, and will silently coalesce away samples the first
time an audit-shaped batch arrives on a series feature.

**Consequence for the bundle cut (§12.1): `org.eclipse.fennec.tracking.model` moves from
P3 to P1.** The SPI sketch in §4 writes `supports(EClass, StorageProfile)`, and
`StorageProfile` is `TrackingMode` — defining both would be two enums for one concept, the
exact duplication this section just refused. The dependency runs
`persistence.stream` → `tracking.model`, which is sound: a declaration model may exist
before its consumers.

To avoid cementing untested design, the bundle is promoted **narrowly**: `TrackingMode`,
`ClassTracking`, `FeatureTracking`, `TrackingConfig` — what P1 needs to be told what a
stream is. The capture-side content of the draft (`KeyframeConfig`, `FaultPolicy`,
`GapPolicy`, `StalePolicy`, `IdentityStrategy`, `ArrayMode`, `DanglingRefPolicy`) stays out
until P3 exercises it, and arrives additively then, under the same discipline as
`DeltaKind`.

### 13.2 ClickHouse as the first real series backend, before Mongo TS

**Settled: a ClickHouse store gets its own phase, directly after the SPI and the in-memory
reference, ahead of the Mongo and Timescale mappings.**

The reason is that it is the *honest* test of the SPI. The JPA narrow table and Mongo both
sit on engines that can update a row, delete a row and bracket a transaction, so an SPI cut
only against them will quietly assume all three. ClickHouse cannot do any of them —
append-only with asynchronous mutations, no transactions — and is nevertheless exactly the
engine a series workload wants: columnar compression, TTL-driven retention, materialized
views as rollups. If the SPI survives ClickHouse, the generalisation is real rather than a
Postgres shape with two dialects.

Two things follow:

- **It is not a JPA path.** There is no EclipseLink dialect for ClickHouse, and §4.2's
  reasoning about Timescale ("not a backend, a dialect optimization") does *not* transfer:
  this is a separate store bundle over the ClickHouse client, at the same level as
  `stream.mongo`. Project name `org.eclipse.fennec.persistence.stream.clickhouse`.
- **It answers `supports(root, CHANGELOG)` with false, and that must be a declaration
  rather than a failure.** The audit profile needs invertible entries and a retention cut
  at manifests; an engine whose deletes are asynchronous mutations is the wrong home for a
  record whose purpose is to be exact about what happened when. The mode separation of
  §13.1 is what makes this a one-line capability answer instead of a caveat.

Retention (`truncate`) maps to TTL clauses, the §5 time bucket to `toStartOfInterval`, and
first/last-per-bucket to `argMin`/`argMax` — all native, so the pushdown story is stronger
here than on plain JPA.

### 13.3 Capability vocabulary (O5, settled — issue #207)

`QueryFeature` reserved two future placeholders that nothing declared and
`MemoryQueryProcessor` excluded: `AS_OF` (100) and `SERIES_RANGE` (101). §5 meanwhile
proposed `SERIES_SUBJECT`, `TIME_BUCKET`, `SERIES_FIRST_LAST`, `SERIES_GAPFILL` — a second
vocabulary overlapping the first without saying how.

This was settled first, ahead of every other cut, because of an asymmetry: literal values
are additive and never renumbered (the `DeltaKind` discipline), so **removing** a literal is
free only while there is no baselining and no release (#177) and impossible afterwards,
whereas **adding** one has no deadline at all.

1. **`SERIES_RANGE` is retired**, and value 101 stays unused. "Range" was never the new
   capability: restricting a series to a time window is an ordinary predicate on the time
   axis, and the `WHERE_*`/date-comparison vocabulary already covers it — a backend that can
   serve series at all can serve a range. What is genuinely new is that the *subject* is a
   series rather than the current state, which is what §14 says and what the literal should
   name.
2. **`AS_OF` stays, with its meaning sharpened**: reconstructing the current state at a
   point in time via keyframe plus replay is a CHANGELOG operation, orthogonal to series
   access. A series query never needs it — every sample is absolute. Keeping them apart is
   what lets a store declare one without the other.
3. **Naming rule:** `SERIES_*` for anything that presupposes a series subject; a pipeline
   stage is named after the stage (`TIME_BUCKET`, like the existing `GROUP_BY`), not after a
   group prefix.
4. **A literal appears when something declares it**, never in advance — the rule
   `conformance-and-capabilities.md` already states for `StoreFeature`. So `SERIES_SUBJECT`
   and `TIME_BUCKET` arrive with the IR phase, `SERIES_FIRST_LAST`/`SERIES_GAPFILL`/
   delta-rate with the extras phase, and `StoreFeature.TIMESERIES`/`CHANGELOG` with the
   store SPI, as the coarse answer behind `StreamStore.supports(root, mode)`.

The reserved-placeholder habit is what this replaces. A placeholder looks like foresight and
behaves like a decision taken without the information: `SERIES_RANGE` was written before
anyone knew what a series query would look like, and by the time §5 worked that out, the
name was already wrong.
