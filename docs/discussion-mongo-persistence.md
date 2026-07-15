# Discussion: adding a MongoDB persistence backend

**Status:** design/handoff note — basis for discussion in the persistence session. Nothing built yet.
**Author of the notes:** cross-session handoff (drafted from the emf.util session, 2026-07-14).

## Context & goal

We have a working **JPA** persistence backend in this repo, plus a **backend-agnostic
persistence API** (`org.eclipse.fennec.persistence`). We want a **MongoDB** backend.

An old MongoDB integration exists in `org.gecko.emf.persistence/org.gecko.emf.persistence.mongo`.
It plugged Mongo in via EMF **`URIConverter` + `MongoInputStream`/`MongoOutputStream` +
a `MongoResourceSetConfigurator`** — i.e. it forced a document store through a byte-stream
keyhole, and needed global ResourceSet wiring (URI handler + stream factory + id factory).
That was necessary in the pre-codec era but is ugly to configure and a poor fit for a
document DB (no natural place for upsert / query / projection / native BSON types).

A later prototype lived in `fennec-codec/org.eclipse.fennec.codec.mongo`
(`CodecMongoResource extends CodecResource`, `MongoResourceFactory`) — the right direction
(ResourceFactory + a Mongo-aware Resource that talks to the driver directly), but built on the
**old** codec SPI (`CodecModelInfo`/`CodecModule`/`ObjectMapperBuilderFactory` + a bespoke
`MongoCodecProvider`) and left much WIP (query engine, upsert, load-into-contents all commented out).

**This note captures the agreed modern approach.**

## Decision summary

| Aspect | Decision |
| --- | --- |
| **Repo** | Rename `emf.persistence-jpa` → **`emf.persistence`**; Mongo lives here as a sibling backend. **No** new `emf.mongo-persistence` / separate API repo (premature — see rationale). |
| **Bundle** | `org.eclipse.fennec.persistence.mongo` (backend). Optionally `…persistence.mongo.tests`. |
| **Contract** | Implement **`PersistenceResource`** (from `org.eclipse.fennec.persistence`) — gives `delete` / `count` / `exist` uniformly with JPA, which a bare EMF `Resource` lacks. |
| **Blueprint** | Mirror **`JPAResourceImpl`** exactly (see below). |
| **PersistenceEngine SPI** | **Not used.** The live JPA backend does not use it (`JPAResourceImpl.getEngine()` throws). Mongo mirrors that. The `BasicPersistenceEngine`/`DefaultPersistenceEngine` machinery is legacy/optional. |
| **Serialization** | Reuse the **bson codec** (`org.eclipse.fennec.codec.bson`) for EObject ↔ `org.bson.BsonDocument`, **BsonDocument-direct** (no byte stream). |
| **Connection** | **Config-driven `MongoClient`** — a ConfigurationAdmin factory config (connectionString + db) builds a `MongoDatabase` and registers the `Resource.Factory`. Connection is **not** in the EMF URI. |
| **v1 scope** | CRUD per collection + `_id`. Query translation / projection / lazy cursor → v2. |

### Why rename-in-repo, not new repos

- This repo is already **not** JPA-only: `org.eclipse.fennec.persistence` (the general API) is a
  separate bundle here already. The name `emf.persistence-jpa` is already a misnomer.
- Mongo is a **persistence backend** (same `PersistenceResource` contract), so it belongs next to
  the API and the JPA sibling — sharing API, BOM, workspace library, test harness, docs.
- A separate Mongo repo only pays off with a different release cadence / maintainers / a desire to
  keep the MongoDB driver footprint out of this CI — none apply.
- A separate API repo only pays off when multiple **independent** repos consume it and need
  independent versioning. Today only JPA (+ soon Mongo) consume it, both here. The API bundle is
  already published via the workspace library, so external consumers can still depend on just it.
- Independent Maven artifacts are preserved anyway: `…persistence.mongo` is its own artifact — JPA
  users never pull the MongoDB driver.

### Why this supersedes the earlier "put it in emf.util" idea

Mongo implements `PersistenceResource`/persistence semantics — it is a persistence backend, not a
general EMF resource-adapter utility like protobuf/soap/openapi. It belongs with the persistence
API + JPA backend, and it follows **this repo's convention**: a backend puts its `Resource.Factory`
`@Component` **in the same bundle** (see `JPAResourceFactory`), with no core/`.osgi` split like emf.util.

## The blueprint: `JPAResourceImpl`

`org.eclipse.fennec.persistence.eclipselink.resource.JPAResourceImpl`:

```java
public class JPAResourceImpl extends ResourceImpl implements PersistenceResource {
    private final EntityManagerFactory emf;
    public JPAResourceImpl(URI uri, EntityManagerFactory emf) { super(uri); this.emf = emf; }

    // load  -> doLoad: entity name from URI segment; SELECT all (or paginated via Options.getPageSize);
    //          add EObjects to getContents()
    // save  -> doSave: per content EObject -> upsert(em, …) keyed on eClass().getEIDAttribute(), in a tx
    // delete(options)      : implemented directly
    // count() / count(opts): implemented directly
    // exist() / exist(opts): implemented directly
    // getEngine(): throws UnsupportedOperationException — "managed directly via EntityManagerFactory"
}
```

Key takeaways for Mongo:
- **`extends ResourceImpl implements PersistenceResource`**, constructor takes the backend handle
  (`MongoDatabase` for us), injected by the factory.
- Everything (load/save/delete/count/exist) is implemented **on the resource**, directly against the
  backend. No engine indirection.
- The **`_id` strategy is already established by JPA: `eClass().getEIDAttribute()`** is the identity.
  Mongo should use the same → maps to Mongo `_id`.
- Reuse the **`Options`** vocabulary (`Options.getPageSize`, `Options.getTableEClass`,
  `Options.READ_FILTER_ECLASS`, …) so option semantics stay uniform across backends. `getTableEClass`
  → the collection / type filter maps naturally to "collection per EClass".

## Proposed Mongo mapping

URI carries **addressing only** (no connection): `mongodb://<db>/<collection>[/<id>][?<filter>]`
- collection = URI segment (or `Options.getTableEClass().getName()`),
- id = last segment → Mongo `_id`,
- filter = query (v2).

| PersistenceResource op | MongoDB |
| --- | --- |
| `load` / `doLoad` | `collection.find(filter)` → `BsonDocument` → bson **reader** delegate → EObject → `getContents()`. Pagination via `Options.getPageSize` → `skip/limit`. |
| `save` / `doSave` | per content EObject → bson **writer** delegate → `BsonDocument`, set `_id` from the EID attribute → `collection.replaceOne(eq("_id", id), doc, upsert=true)`. (Not the old `insertMany`, which broke re-save.) |
| `delete(options)` | `deleteOne(eq("_id", id))` or `deleteMany(filter)`. |
| `count()` / `count(options)` | `collection.countDocuments(filter)`. |
| `exist()` / `exist(options)` | `collection.countDocuments(filter, new CountOptions().limit(1)) > 0` (or `find(filter).limit(1).first() != null`). |
| `getEngine()` | throw `UnsupportedOperationException` (mirror JPA). |

### Serialization: reuse the bson codec, BsonDocument-direct

The new codec's BSON delegates are **`BsonDocument`-native**, not byte-oriented:
`BsonFormatDelegate implements FormatDelegate<BsonDocument>` (writes into a `BsonDocument` via
`BsonDocumentWriter`), `BsonFormatReaderDelegate` reads from a `BsonDocument`. The `.bson` file
format only wraps these with a stream (de)serializer. The MongoDB Java driver works with
`org.bson.BsonDocument` natively.

⇒ For Mongo we want **EObject ↔ `BsonDocument` with no byte round-trip**, reusing
`BsonFormatDelegate` / `BsonFormatReaderDelegate`.

**Open implementation question (verify first):** how to drive the codec's EObject→delegate
traversal writing into a `BsonFormatDelegate(bsonDoc)` **without** going through the stream
`CodecFormatProvider`. Need to check what `CodecResource` exposes as a serialization entry point
independent of the stream I/O type. Fallback for v1 if no clean hook exists: use the existing
`BsonFormatProvider` to get BSON **bytes**, then `RawBsonDocument`/`BsonDocument.parse` for the
driver — one extra serialization round, zero new codec code, optimize to direct later.

## What carries over from the old gecko mongo

| Old (`org.gecko.emf.persistence.mongo`) | Now |
| --- | --- |
| `converter/*` (BigDecimal, BigInteger, Array, XMLGregorianCalendar) | ✅ handled by the bson codec + `CodecValueRegistry` |
| `codecs/*` (EObjectCodecProvider, FeatureMapEntry-, MapEntryCodec) | ✅ replaced by the bson delegates |
| `handler/*` (URIHandler, ResourceSetConfigurator, streams) | ❌ **dropped** — this is exactly what goes away |
| `SimpleMongoIdFactory` / `_id` mapping | ⚠️ **new**, but the strategy is decided: `getEIDAttribute()` ↔ `_id` |
| `NativeQueryEngine` / `EMongoQuery` / `ProjectionHelper` | ⚠️ v2 — real sub-project |
| `EMongoCursor` / lazy streaming of large collections | ⚠️ v2 — v1 loads bounded by query/pagination |

## Proposed bundle shape

`org.eclipse.fennec.persistence.mongo` (follow `.eclipselink` bnd conventions):
- `MongoResourceImpl extends ResourceImpl implements PersistenceResource`
- `MongoResourceFactory` — `@Component service = Resource.Factory.class`, **config-driven**
  (ConfigurationAdmin factory config: `connectionString`, `database`); builds a `MongoClient` /
  `MongoDatabase`, hands the `MongoDatabase` to each created resource. Bind by URI scheme
  `mongodb` (mirror how `JPAResourceFactory` binds).
- Depends on: `org.eclipse.fennec.persistence` (API) + `org.eclipse.fennec.codec.bson` +
  `org.mongodb:mongodb-driver-sync` + `org.mongodb:bson`.
- `getEngine()` throws (mirror JPA).

## Open questions for the persistence session

1. **Codec hook** — confirm the BsonDocument-direct entry point into `CodecResource`'s
   serialization, else adopt the bytes-round-trip fallback for v1.
2. **`_id` types** — EID attribute may be `String`/`long`/`EString`; decide how it maps to Mongo
   `_id` (raw value vs `ObjectId` when it looks like one — the bson delegate already
   `supportsNativeObjectId()`), and how a generated `_id` is written back into the EObject on insert.
3. **Collection resolution precedence** — URI segment vs `Options.getTableEClass()` vs an explicit
   option; and single-collection-per-resource vs mixed contents.
4. **Save semantics** — `replaceOne(upsert)` per object (agreed) vs a bulk write; transaction
   scope (Mongo multi-doc tx needs a replica set — probably out of v1).
5. **Rename mechanics** — `github_repository` in `gradle.properties`, docs base path, workflow
   references, and the EF/otterdog org settings if this repo is org-managed. GitHub redirects old
   URLs automatically.
6. **BOM / workspace library** — add `…persistence.mongo` to the persistence BOM and the
   `…jpa.library.workspace` (or a renamed persistence workspace library) closure.

## Cross-references

- API bundle: `org.eclipse.fennec.persistence` (`PersistenceResource`, `Options`, `Keywords`,
  `PersistenceException`; the `PersistenceEngine`/`Basic`/`Default` + `Deletable`/`Countable`/
  `Readable`/`Updateable` set — legacy, not used by the live JPA backend).
- JPA blueprint: `org.eclipse.fennec.persistence.eclipselink.resource.JPAResourceImpl` /
  `JPAResourceFactory`.
- bson codec: `org.eclipse.fennec.codec.bson` (`BsonFormatDelegate`, `BsonFormatReaderDelegate`,
  `BsonFormatProvider`, `BsonResourceFactoryComponent`) in the `emf.codec` repo.
- Old gecko impl (reference only): `org.gecko.emf.persistence/org.gecko.emf.persistence.mongo`.
- Old codec-mongo prototype (reference only): `fennec-codec/org.eclipse.fennec.codec.mongo`.
