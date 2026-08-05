# Geospatial predicate vocabulary in the query IR

**Status:** draft concept for discussion (2026-08-05, issue #101) — nothing here is decided.
Companions: `query-ir-redesign.md` (Expression IR, capability discipline),
`query-processor-spi.md` (per-backend translation), `timeseries-access.md` §6.3
(enrichment meta axis — lat/lon tags are a primary data source for these predicates).
Consumers: the Lucene backend (upcoming `emf.search` repository, spatial module), Mongo
`2dsphere`, OData geo functions ([OData-URL] `geo.distance`/`geo.intersects`/`geo.length`).

## 1. Why in the canonical IR

Geo filters are not a Lucene specialty: Mongo answers them natively (`$geoWithin`,
`$nearSphere` on `2dsphere` indexes), PostgreSQL has PostGIS, and the memory engine can
carry reference semantics over plain coordinate features (haversine is just arithmetic).
Per the one-IR discipline the *vocabulary* lands here once, every backend translates or
refuses by capability — the alternative is every consumer smuggling geo through
backend-specific escape hatches (the rung-4 pattern of the ingest ladder, but for
queries, where no such rung exists).

## 2. Value model — the smaller question first

Proposal: **one new literal, no new EMF typing requirements.**

- **`GeoPointLiteral (lon, lat: EDouble)`** — WGS84, D1 literal pattern (#83). Longitude
  first (GeoJSON order), degrees. No altitude in v1.
- **No geometry-valued model features in v1.** The *subject* side of a geo predicate is a
  model feature (or feature pair) that the mapping layer declares as a coordinate — an
  eorm/esearch/codec concern, not an Ecore one:
  - split representation: two numeric EAttributes (`lat`, `lon`) — the dominant shape in
    existing models and the #96 §6.3 enrichment tags;
  - packed representation: a single attribute holding a `GeoJSON`-style point (Mongo's
    native form; the codec already round-trips such shapes).
  The IR addresses the subject as a **`GeoSubject(pathLon, pathLat | pathPoint)`** —
  open decision G1 below.
- **Shapes for `within`**: `GeoBox(southWest, northEast)` and `GeoPolygon(points[3..*])`
  as literal-like value objects. No WKT parsing in the IR — WKT/GeoJSON text is an
  authoring/transport concern of the consumers (OData parses its own literals anyway).

## 3. Predicate set v1

Two constructs, mirroring what all three target engines answer natively:

1. **`GeoWithin(subject, shape)`** — containment in a box or polygon.
2. **`GeoDistance(subject, point)`** — a *value* expression (meters, EDouble), usable in
   comparisons (`geoDistance(...) <= 500`) and as a sort key via the #84 seam
   ("nearest first" = `orderByAsc(geoDistance(...))`). This deliberately avoids a
   dedicated nearest-neighbour construct in v1 — `ORDER BY distance LIMIT n` expresses
   k-NN, and backends may recognize the pattern (Mongo `$nearSphere`, Lucene
   `LatLonPoint.newDistanceSort`) as an optimization, not as semantics.

Deliberately **not** v1: intersects/crosses over non-point geometries (needs
geometry-valued features), geodesic lengths/areas, CRS beyond WGS84, altitude.

## 4. Capabilities and backend mapping

`QueryFeature.GEO_WITHIN` (76), `QueryFeature.GEO_DISTANCE` (77).

| Backend | GEO_WITHIN | GEO_DISTANCE |
|---|---|---|
| memory (reference) | point-in-box / point-in-polygon (ray casting), declared | haversine (spherical WGS84, meters), declared |
| Mongo (2dsphere) | `$geoWithin` `$box`/`$polygon` | `$geoNear` stage or `$nearSphere`; plain distance compare via aggregation `$let` + formula — needs the packed (GeoJSON) representation, split lat/lon computes in `$expr` |
| Lucene (emf.search) | `LatLonPoint.newBoxQuery`/`newPolygonQuery` | `newDistanceQuery` for the compare shape, `newDistanceSort` for ordering |
| JPA | refused until a PostGIS dialect story exists (EclipseLink `FUNCTION()` pushdown is feasible — own follow-up, same dialect-detection pattern as TimescaleDB in #96 §4.2) | refused |

The memory engine declares both — unlike SCORE (#100) geo *has* reference semantics, so
the differential corpus (memory vs. Mongo) applies, and the TCK pins the numbers.

## 5. Semantics to pin (proposed as binding)

1. **Spherical WGS84, meters.** Haversine over the mean earth radius (6 371 008.8 m) is
   the reference formula; backends whose native distance differs (Mongo uses a spherical
   model too; Lucene's `LatLonPoint` is accurate to centimeters) stay within a declared
   tolerance — the TCK compares with an epsilon band, not exact equality.
2. **Null-poisoned subjects are UNKNOWN** — a null lat/lon (or missing point) makes
   `GeoWithin`/distance comparisons UNKNOWN per the 3VL discipline (#94): excluded
   positively, excluded under `not(...)`, guarded in the Mongo/Lucene push-downs exactly
   like NE/`$nin` (#97).
3. **Boxes may cross the antimeridian** (southWest.lon > northEast.lon is legal and
   means the wrap-around box); polygons must not (refused by validation, code TBD) —
   matching Mongo's `$box` limitation and Lucene's polygon contract.
4. **Polygons are implicitly closed**, vertices in counter-clockwise order; degenerate
   polygons (< 3 distinct points) are a validation error (structural, like
   CODE_INVALID_AGGREGATE from #87).
5. **Distance is symmetric and total on valid coordinates** — out-of-range coordinates
   (|lat| > 90, |lon| > 180) are a validation error, not a runtime surprise.

## 6. Open decisions

| # | Question | Leaning |
|---|---|---|
| G1 | Subject shape: feature-pair (`pathLat`×`pathLon`) vs. single packed point path vs. both | both, one `GeoSubject` node with either binding — split is what real Ecore models have, packed is what Mongo indexes; refusing one form per backend stays capability-honest |
| G2 | Where the coordinate declaration lives: purely structural in the query (G1 paths) vs. a model aspect ("this feature pair is a position") | structural in v1 — an aspect can later *derive* the paths, same relationship as TrackingConfig to IngestMapping (#96 §6) |
| G3 | `GeoDistance` as value expression (proposal) vs. dedicated `GeoNear(point, maxDistance)` predicate | value expression — composes with #84 sort and comparisons for free; `GeoNear` would be redundant vocabulary |
| G4 | Feature numbering: 76/77 next to SCORE=75 vs. an own 9x band for geo | 76/77 — the band idea (#76 note) never materialized, contiguous is the de-facto convention |
| G5 | Epsilon band for the TCK differential (rule 1) | relative 1e-3 on distances > 1 m, absolute 1 mm below — generous enough for model differences, tight enough to catch degree/radian bugs |

## 7. Phasing

1. **G-P1** — IR nodes + capabilities + memory reference + validation rules (§5.3–5.5),
   TCK cases behind a `supportsGeo()`-style hook (both backends here refuse in the
   beginning: Mongo needs G1 packed-form work, JPA refuses per §4).
2. **G-P2** — Mongo `2dsphere` translation + differential corpus memory vs. Mongo.
3. **G-P3** — Lucene translation (in `emf.search`, against the published vocabulary).
4. **G-P4** — PostGIS dialect for JPA (own issue, own concept note).
