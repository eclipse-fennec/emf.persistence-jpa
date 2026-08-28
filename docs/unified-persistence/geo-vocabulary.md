# Geospatial predicate vocabulary in the query IR

**Status:** settled (concept round 2026-08-05, issue #101): G1 = both subject bindings,
G2 = structural in v1, G3 = distance as value expression, G4 = feature ids 76/77,
G5 = relative 1e-3 above 1 m / absolute 1 mm below; the §5 semantic rules are binding
as proposed. Implementation phases per §7 — G-P1 in progress.
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

## 6. Decisions (settled 2026-08-05 — the "leaning" column became the decision)

| # | Question | Decision |
|---|---|---|
| G1 | Subject shape: feature-pair (`pathLat`×`pathLon`) vs. single packed point path vs. both | both, one `GeoSubject` node with either binding — split is what real Ecore models have, packed is what Mongo indexes; refusing one form per backend stays capability-honest |
| G2 | Where the coordinate declaration lives: purely structural in the query (G1 paths) vs. a model aspect ("this feature pair is a position") | structural in v1 — an aspect can later *derive* the paths, same relationship as TrackingConfig to IngestMapping (#96 §6) |
| G3 | `GeoDistance` as value expression (proposal) vs. dedicated `GeoNear(point, maxDistance)` predicate | value expression — composes with #84 sort and comparisons for free; `GeoNear` would be redundant vocabulary |
| G4 | Feature numbering: 76/77 next to SCORE=75 vs. an own 9x band for geo | 76/77 — the band idea (#76 note) never materialized, contiguous is the de-facto convention |
| G5 | Epsilon band for the TCK differential (rule 1) | relative 1e-3 on distances > 1 m, absolute 1 mm below — generous enough for model differences, tight enough to catch degree/radian bugs |

## 6b. Nearest-first ordering needs two capabilities, not one

Raised by the OCL-side consumer while reviewing #232, and worth writing down because §5 and the
`GeoDistance` documentation both advertise the "sort seam" of issue #84 (k-NN = sort + limit)
without saying who can serve it.

`GEO_DISTANCE` says the backend understands the distance as an expression. It does **not** say
the backend can order by an expression — that is `SORT_EXPRESSION`, a separate feature. The two
combine, and the combination is what nearest-first actually requires:

| | `GEO_DISTANCE` | `SORT_EXPRESSION` | nearest-first `$orderby` |
|---|---|---|---|
| memory | yes | yes | **yes** |
| mongo | yes (range comparisons only) | **no** | no — refused in validation |
| jpa | no (until PostGIS) | yes | no |

So the honest statement is: **k-NN ordering is memory-only today**, and on mongo it would need
`$geoNear`, which is a pipeline stage rather than a sort key. That is a gap in coverage, not a
defect — mongo declares what it can do and refuses the rest by name, which is the contract
working. Per the #207 rule the missing piece lands when a consumer asks for it; the OCL-side
consumer explicitly is not asking yet.

Mongo's second limit belongs next to it: `GeoDistance` translates only inside a **range**
comparison (`LT/LE/GT/GE`). Equality against a continuous distance is refused by name, which is
right — floating-point equality on a haversine result is not a question anyone means to ask.

That rule is **reported by `validate()`** since issue #237, not only thrown by the translator.
It was written here before it was checked anywhere a consumer could see it: a refusal reachable
only through `query()` arrives as a plain `IOException` with no code, and the #161 doctrine has
consumers ask `validate()` and route on the code. The diagnostic carries
`CODE_UNSUPPORTED_FEATURE` rather than a new mongo-local code, deliberately — the query is
structurally valid (memory evaluates it, a PostGIS-backed JPA could), so what the consumer is
told is "this backend cannot", which is what that code already means. A backend-local code would
have landed in the consumer's structural bucket and produced a 400 where 501 is right. The
translation-time throw stays as the backstop it was always meant to be.

## 6a. The OCL form (settled 2026-08-24, issue #232)

`GeoWithin`/`GeoDistance` were the third documented totality exception of the OCL bridge: OCL
defines no geo operators, so `ExprToOcl` refused them and `OclToExpr` had no geo at all. That
made the whole vocabulary unreachable for a consumer living on the OCL side — `emf.odata`
translates `$filter` to OCL and crosses into the IR through `OclToExpr`, so the backends could
serve geo and nothing could ask.

**Settled: the vocabulary gets a dialect form, and the exception is lifted.** It was never the
same kind of exception as the other two. `AliasRef` (#82) is a stage-local name and `Score`
(#100) is an execution-time value — neither has a model-expression meaning. A geo predicate is
an ordinary predicate over stored coordinates, and every argument it takes is something OCL can
already spell: property paths and numbers. The bridge had also already shown twice that it is
not limited to operators OCL defines — it reads `toLower`/`toUpper` as an evaluator dialect, and
`IntervalMatch` (#215) got a form because it decomposes into comparisons.

```
geoWithin(lonPath, latPath, geoBox(swLon, swLat, neLon, neLat))
geoWithin(pointPath, geoPolygon(lon, lat, lon, lat, lon, lat, …))
geoDistance(lonPath, latPath, geoPoint(lon, lat))   -- composes with a comparison (G3)
```

Three properties, each chosen against an alternative:

- **The binding is told apart by arity, not by a second function name.** A split subject
  contributes two path arguments and a packed one contributes a single path, and the shape is
  always the last argument — so one name covers both forms of decision G1 and nothing has to
  stay in sync.
- **Longitude comes first, everywhere** — in the subject arguments and inside every shape. This
  deliberately differs from the latitude-first split builder, which follows the order the model
  declares its features in. Because both paths take the same type, a swapped pair compiles and
  round-trips; the builder therefore now spells the axis order in its **name**
  (`geoSubjectLatLon` / `geoSubjectLonLat`) rather than leaving it to an argument position, so a
  consumer using both routes reads the order instead of remembering it. One rule for the whole vocabulary
  is worth more than agreeing with that: `geoPoint(lon, lat)` and therefore every shape is
  longitude-first already, and a single OCL call that mixed both orders would be a trap. A test
  reads the rendered call and asserts which path landed where, because a round-trip test alone
  would pass even if both directions swapped the pair consistently.
- **Shapes are calls, not bare argument lists.** `geoBox`/`geoPolygon`/`geoPoint` are refused as
  expressions in their own right — they are arguments, and a shape that appears alone is an
  error rather than a predicate that is always true. Malformed shapes are refused by name (a
  polygon with fewer than three points, a box without four coordinates) instead of silently
  producing a different shape.

What this does **not** change: the vocabulary itself. The three expressions a consumer grammar
asks for beyond §3 — path-to-path distance, line length, shape-in-shape — stay unserved, and
issue #233 was closed with that reasoning. This round is only about the route to what already
exists.

Side effect worth naming: the ingest ladder of `timeseries-access.md` §6.1 defines its rung
boundary as "whatever `OclToExpr` refuses is rung 4 by definition". Geo predicates are therefore
now expressible at rung 2/3. That is intended rather than incidental — an ingest guard over a
bounding box is a reasonable thing to declare — but it is a widening of that boundary and is
recorded here so it is not discovered later.

## 7. Phasing

1. **G-P1** — IR nodes + capabilities + memory reference + validation rules (§5.3–5.5),
   TCK cases behind a `supportsGeo()`-style hook (both backends here refuse in the
   beginning: Mongo needs G1 packed-form work, JPA refuses per §4). The memory
   reference serves the SPLIT binding; the canonical value shape of the PACKED
   binding is defined with the Mongo work in G-P2 (GeoJSON point) — until then the
   reference engine refuses packed subjects with a precise message.
2. **G-P2** — Mongo `2dsphere` translation + differential corpus memory vs. Mongo.
   Implemented 2026-08-06 (issue #113):
   - **Canonical PACKED value shape**: a GeoJSON-style point — an EObject whose EClass
     exposes a many-valued numeric `coordinates` feature in `[lon, lat]` order (plus an
     optional `type` string, `"Point"`). Stored form in Mongo:
     `{type: "Point", coordinates: [lon, lat]}` — exactly what a `2dsphere` index accepts;
     `org.geojson.model` (fennec.common.models) is a compatible producer, not a
     dependency. A packed value of any other shape is the packed analogue of a null
     coordinate: UNKNOWN under §5.5. The memory reference reads the same shape and
     dropped its packed refusal.
   - **Mongo translation**: planar shapes go against the `<point>.coordinates` legacy
     pair to keep the §5.3 planar box/polygon semantics of the reference engine —
     `GeoWithin(box)` → `$geoWithin $box` (wrap-around boxes split into an `$or` of two),
     `GeoWithin(polygon)` → `$geoWithin $polygon`; `GeoDistance ≤/<` → `$geoWithin
     $centerSphere` (radians over the §5.4 mean earth radius — same great-circle math as
     the haversine reference). Split subjects: boxes become plain range filters (wrap →
     `$or`), distance computes via `$expr` haversine (`$degreesToRadians`/`$atan2`).
     Negations carry explicit non-null guards (§5.5, issue-#97 discipline). Refused with
     precise messages: polygon over a split subject (no index form, ray-casting is not
     expressible in `$expr` without unrolling), distance `EQ`/`NE` (measure-zero
     comparisons on a continuum). `$geoNear` nearest-first recognition remains an open
     optimisation.
3. **G-P3** — Lucene translation (in `emf.search`, against the published vocabulary).
4. **G-P4** — PostGIS dialect for JPA (own issue, own concept note).
