# Time-series access on the stream model — TIMESERIES profile store SPI + series queries

**Status:** draft concept for discussion (2026-08-05, issue #96) — nothing here is decided.
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
`$filter`) — one OCL, not two; evaluation is in-memory per message, so the reference
evaluator carries. Note: that evaluator currently lives in `odata.query` —
eclipse-fennec/emf.odata#14 (neutral home) gains its second consumer and moves up in
priority. The sensinact-mapping precursor (`collectionIndex`/`collectionFilter` with
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
