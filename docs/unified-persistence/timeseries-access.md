# Time-series access on the stream model — TIMESERIES profile store SPI + series queries

**Status:** draft concept for discussion (2026-08-05, issue #96) — nothing here is decided.
Companions: `concept.md` §2 (requirements 2/4/5), §9 (storage profiles), §10 (keyframing),
§14 (query integration), §19 (technology mapping); `query-ir-redesign.md` (Expression IR,
capability discipline); `query-processor-spi.md` (per-backend translation). Consumer
trajectory: eclipse-fennec/emf.odata#12 (`$apply` on pipeline stages), emf.odata#11
(`$delta`, currently a service-layer journal).

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

This document proposes the access design in three separable cuts: a stream-store SPI
(§4), series queries in the IR (§5), and CDC capture as a deliberately later cut (§6).

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
into `CommandResource`/`QueryableResource` is open question O1 (§9). What is NOT open is
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

## 6. Cut 3 — CDC capture (deliberately later)

Mongo change streams / Postgres logical decoding as *sources* that emit CHANGELOG-profile
ChangeSets into a `StreamStore` — a durable, cross-process change feed for data that is
also written outside the service layer. Separate cut because the failure modes are
different (resume tokens, WAL slots, snapshot/backfill coordination) and nothing in cuts
1–2 depends on it. Design constraint to keep in view: the capture side must stamp
`contextFingerprint` correctly, which requires the metadata registry at capture time.

## 7. Consumers

- **OData `$apply`** folds onto pipeline stages; with `TIME_BUCKET` OData serves
  dashboard-style aggregations over sensor history via pushdown instead of in-memory work
  (extends the emf.odata#12 trajectory; `$apply` remains OData's own aggregation submodel,
  bridged at the boundary — same division of labor as `$filter`/OCL today).
- **OData `$delta`** currently rides bounded in-memory service-layer journals
  (emf.odata#11). The CHANGELOG profile replaces that with a durable journal shared across
  instances; `asOf` and audit come with it.
- **Derived references over history** (`query-derived-references.md`) get a natural
  extension target: an OCL derivation over a series subject.

## 8. Semantic ground rules (proposed as binding)

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
   during ingest is O2.

## 9. Open decisions

| # | Question | Leaning |
|---|---|---|
| O1 | SPI shape: own `StreamStore` service vs. folding into `CommandResource`/`QueryableResource` (§14 leans "same machinery") | own SPI for append/replay, series *queries* through the existing `QueryableResource` path |
| O2 | Ingest coupling: does `append` update the current-state projection transactionally, eventually, or not at all (capture-side decides)? | per-profile default: TIMESERIES eventual/optional, CHANGELOG transactional |
| O3 | Who owns housekeeping (manifest synthesis, retention execution) — store-side or a shared service over the SPI? | shared service; stores expose primitives only |
| O4 | Storage granularity: table/collection per aggregate root vs. per package vs. generic | start generic (one per unit), measure, specialize behind the SPI |
| O5 | Capability naming + granularity for the series vocabulary | proposal in §5, to be settled with the IR maintainers |
| O6 | Does `ReplayFilter` belong in the SPI or is replay always whole-stream (filter = query concern)? | in the SPI — TS engines answer filtered ranges natively, whole-stream replay would be the N+1 of series access |

## 10. Phasing proposal

1. **P1 — store SPI + JPA narrow table + memory reference** (smallest end-to-end slice;
   proves boundary discipline and round-tripping via fingerprint typing).
2. **P2 — series subject + TimeBucket in the IR**, memory reference semantics + JPA
   pushdown (`date_trunc`), TCK differential corpus memory vs. JPA.
3. **P3 — Mongo TS collections** (store mapping + `$dateTrunc` pushdown, TCK green).
4. **P4 — Timescale dialect** (hypertable DDL, `time_bucket`, compression/retention).
5. **P5 — series extras** behind capabilities (first/last, gapfill, delta/rate).
6. **P6 — CDC capture** (own concept round first, see §6).

Each phase is issue-sized in the spirit of the #76–#84 wave: JPA + Mongo + memory + TCK
as the definition of done per construct.
