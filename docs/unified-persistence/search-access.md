# Search access — blueprint for the `emf.search` repository

**Status:** working blueprint (2026-08-05). The repository decision, roles and the
persistence-side prerequisites are settled (discussion 2026-08-05); the task breakdown
in §7 is the proposed issue set for `eclipse-fennec/emf.search` — to be turned into
issues there, refined per issue. Companions: `query-ir-redesign.md` (Expression IR,
capability discipline), `query-processor-spi.md` (per-backend translation),
`timeseries-access.md` (stream store — the v2 index feed), `geo-vocabulary.md`
(issue #101, concept round pending). Prerequisites landed here: #99 (TCK as consumable
API), #100 (SCORE vocabulary).

## 1. Mission

Lucene as a **capability-honest search backend** for the Fennec persistence stack —
replacing the retired `org.gecko.search` architecture, whose pain points define the
anti-goals:

| gecko.search (old) | emf.search (new) |
|---|---|
| consumer hand-builds `IndexContextObject`s per ADD/MODIFY/REMOVE | index maintenance behind the `Resource`/store contract, v2 fed by the change stream |
| EObject→Document mapping hand-coded per use case (`ContextObjectFactory`) | declarative mapping model (`esearch.ecore`), processor pipeline like eorm |
| queries are raw Lucene `Query` objects, results are raw `Document`s | canonical query IR in, EObjects/rows out — `QueryProcessor` SPI, capability-refused where Lucene cannot |
| per-index manual DS wiring (service + analyzer + descriptor) | one whiteboard configuration per index unit (the `JPAUnit`/`mongo.database.alias` pattern) |
| suggest as a parallel second stack | suggest as one module with its own API, sharing model + lifecycle |

Two usage roles, both first-class:

- **Standalone index** ("only a Lucene index, nothing else"): the backend is a
  `QueryableResource`/`PersistenceResource` — documents are saved into and queried from
  the index directly. Honest contract limits: NRT visibility instead of
  read-your-writes transactions, reference contracts largely refused.
- **Secondary index** next to JPA/Mongo (the dominant case): v2, fed by the CHANGELOG
  stream of the timeseries/stream stack (`timeseries-access.md` cut 1) — `append` →
  incremental index update, `replay` → rebuild from scratch; query routing sends
  full-text predicates to Lucene and materializes hits via keyed finds in the primary
  store.

Consumer motivation: OData `$search` (unserved today), model-atlas search, plus every
consumer that needs ranked full-text over EMF models.

## 2. Repository layout

Bnd workspace, same conventions as this repo (`cnf/`, bnd libraries, reusable CI
workflows from `eclipse-fennec/.github`). Lucene 9.x via the OSGi-repackaged bundles
(`org.geckoprojects.search:org.apache.lucene.*` on Maven Central).

| Bundle | Content |
|---|---|
| `org.eclipse.fennec.search.model` | `esearch.ecore` — the mapping metamodel (§4), generated EMF code |
| `org.eclipse.fennec.search.lucene` | the backend: index lifecycle, `Resource.Factory` + whiteboard, mapping processors, `QueryProcessor` (`backend=lucene`) |
| `org.eclipse.fennec.search.suggest` | suggest/completion as its own API + impl (§6) — deliberately NOT query-IR vocabulary |
| `org.eclipse.fennec.search.index` | v2: stream-fed secondary-index maintenance + query routing (§1) — depends on `persistence.stream`, starts only after its P1 |
| `org.eclipse.fennec.search.tck` | TCK binding: consumes the published `org.eclipse.fennec.persistence.tck` (#99) + search-specific cases |

## 3. Dependency contract with emf.persistence-jpa

One direction only: `emf.search` consumes **published** artifacts (bnd repos / DIM
snapshots; the `fennecPersistence` workspace-library pattern, README §consuming).
Everything below is API here as of 2026-08-05:

- Expression IR + `query.model`, the `QueryProcessor` SPI, `QueryCapabilities`/
  `QueryFeature`, validation (`QueryValidator`, `ExpressionAnalyzer`).
- `PersistenceResource`/`QueryableResource` contracts (`persistence`, `persistence.query`).
- The TCK as subclass API with bundled model fixtures (#99): extend
  `AbstractPersistenceTCK`, implement `setUpBackend`/`createBackendResourceSet`/`uriFor`,
  declare variance via the `supports*()` hooks.
- `QueryFeature.SCORE` + the `Score` expression (#100) — the first vocabulary item that
  exists *for* this repo.
- Geo vocabulary lands here after the #101 concept round; `emf.search` implements it in
  G-P3 of that document.

**Ground rule:** missing query vocabulary is never invented in `emf.search` — it comes
back to `emf.persistence-jpa` as an IR issue (the SCORE/geo route). Protocol- or
engine-specific machinery (analyzers, suggest, highlighting) stays in `emf.search`.

## 4. The mapping model (`esearch.ecore`) — "eorm for the index"

Declarative EClass→index mapping, processed by a pipeline in the style of
`persistence.orm`'s `Processor`/`MappingContext`:

- **Index level** (per EClass): index name, default analyzer, NRT refresh policy,
  commit policy.
- **Field level** (per EAttribute): indexed/stored/tokenized, per-field analyzer,
  DocValues (sorting/faceting), facet dimension, boost. Absent a declaration, a
  convention default applies (id → StringField stored, strings → TextField, numerics →
  point + DocValues) so small models need no mapping at all.
- **References**: `EMBED` (denormalize the target's mapped fields under a prefix —
  containment-shaped, the Mongo-embedding analogue) or `ID_ONLY` (store the target id;
  no joins — queries over non-embedded references are capability-refused exactly like
  Mongo's cross-document paths, diagnostic code analog `CODE_NON_EMBEDDED_PATH`).
- Registration on the metadata/aspect plane per EPackage (the pattern shared with
  TrackingConfig/IngestMapping in `timeseries-access.md` §6 — one registry plane).

## 5. Query translation — capability profile

`QueryProcessor` with `backend=lucene`, IR → Lucene `Query`:

| Declared | Translation |
|---|---|
| WHERE_EQ / IN | `TermQuery` / `TermInSetQuery` (keyword fields), point queries (numerics) |
| WHERE_COMPARISON / WHERE_RANGE | point range queries; DocValues where unindexed |
| WHERE_STRING_MATCH (+CASE_INSENSITIVE) | contains/startsWith/endsWith → wildcard/prefix/regexp on keyword fields; analyzed match on text fields; LIKE → `RegexpQuery` via the shared like→regex translation |
| IS_NULL | `FieldExistsQuery` (negated for isNull) |
| LOGICAL_AND/OR/NOT | `BooleanQuery`; **NOT via negation push-down, not bare MUST_NOT** (§5.1) |
| SORT / LIMIT / SKIP | `Sort` over DocValues; `searchAfter`/`TopDocs` paging |
| SCORE (#100) | relevance sort (`Sort.RELEVANCE`) and projected score column |
| COUNT | `IndexSearcher.count` |
| GROUP_BY subset + AGG_COUNT | facets (taxonomy or SSDV) — declared only for the shapes facets actually answer (single group key, count aggregate); everything else refused |
| TYPE_CHECK / TYPE_FILTER | type discriminator field (the codec `_type` analogue, written by the mapper) |

Refused (capability, not error): EXISTS/FOR_ALL over references, FIELD_TO_FIELD,
ARITHMETIC/functions pushdown, PIPELINE beyond the facet subset, EXPAND, joins of any
kind. The refusals are the honesty of the backend — consumers route those to the
primary store (role 2) or restructure.

### 5.1 The 3VL lesson carries over

Lucene's `MUST_NOT` is two-valued and matches documents where the field is missing —
exactly Mongo's `$nor`/`$ne` situation (#97). The same recipe applies verbatim:
negation push-down (De Morgan, operator inversion, quantifier duality is n/a — no
quantifiers), `FieldExistsQuery` as the non-null guard on negated
comparisons/IN/matches, null-poisoned comparisons never match, negated or not. The TCK
cases pinning this (`queryNotOverNullableComparisonExcludesNullRows`,
`queryNegationDistributesThreeValuedOverJunctions`) run against the binding via #99 —
they are the acceptance test for this section.

## 6. Suggest — own API, shared machinery

Suggest/completion (Lucene `suggest` module: analyzing/fuzzy suggesters, weighted
completion fields) is **not** query-IR vocabulary — it is its own small service API in
`search.suggest`: suggestion sources declared in the mapping model (field + weight +
context), built from the same index lifecycle, exposed as a DS service per index unit.
The old stack's separate-suggest-stack mistake is avoided by sharing the mapping model
and lifecycle, not by forcing suggest through the query IR.

## 7. Task breakdown (proposed issue set for emf.search)

Issue-sized in the spirit of the #76–#84 wave; S1–S6 are the v1 line, each with
TCK/unit coverage as definition of done:

1. **S1 — workspace bootstrap**: bnd workspace (cnf, libraries, `fennecPersistence`
   consumption, Lucene OSGi bundles), CI via the reusable workflows, license/dash setup.
2. **S2 — `esearch.ecore` + codegen** (`search.model`): the §4 metamodel, genmodel,
   conventions documented.
3. **S3 — index lifecycle** (`search.lucene`): unit configuration (directory path,
   analyzer registry) as DS factory config, `IndexWriter`/`SearcherManager` NRT
   lifecycle, whiteboard publication per unit (the `mongo.database.alias` pattern).
4. **S4 — mapping processors + `Resource.Factory`**: EObject→Document via the §4 model
   (processor pipeline), save/delete/load-by-id through the `PersistenceResource`
   contract, honest contract notes (NRT visibility, id required).
5. **S5 — `QueryProcessor` + TCK binding**: §5 translation including the 3VL negation
   push-down, capability declaration, `search.tck` binding extending the published
   `AbstractPersistenceTCK` with the `supports*()` variance + search-specific cases.
6. **S6 — SCORE**: relevance sort + projected score (#100 vocabulary), ordinal
   conformance cases (higher score sorts first on constructed corpora).
7. **S7 — facets**: the GROUP_BY/AGG_COUNT subset of §5, taxonomy vs. SSDV decision.
8. **S8 — suggest** (`search.suggest`): §6 API + mapping-model extension + impl.
9. **S9 — geo** (after #101 settles here): `GeoWithin`/`GeoDistance` translation over
   `LatLonPoint` — G-P3 of `geo-vocabulary.md`.
10. **S10 — v2 secondary index** (`search.index`, after `timeseries-access.md` P1):
    stream-fed maintenance (append→update, replay→rebuild), query routing full-text →
    Lucene → keyed finds, consistency notes (index lag is visible and documented).

Suggested order: S1→S5 strictly sequential (each needs the previous), S6/S7/S8
parallelizable after S5, S9/S10 gated on the respective prerequisites here.

## 8. Non-goals

- No Elasticsearch/OpenSearch backend — this is embedded Lucene; a remote search
  engine would be a different backend with its own concept.
- No query-IR forks or search-only vocabulary in `emf.search` (§3 ground rule).
- No transactional guarantees beyond Lucene's commit semantics — the standalone role
  documents NRT visibility instead of pretending otherwise.
- v1 indexes a single EPackage universe per unit; cross-unit federation is out of scope.
