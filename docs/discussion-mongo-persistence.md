# Working document: MongoDB persistence backend (and friends)

**Status:** working document — decisions, assessment and work plan for the Mongo backend.
Originally a design/handoff note (drafted from the emf.util session, 2026-07-14); extended
2026-07-16 with the full 10-point assessment (verified against the `snapshot` branch of
`org.gecko.emf.persistence`, the `emf.codec` repo, the `fennec-codec` mongo prototype and
`fennec.common.models`) and the agreed work plan (part 3).

## Context & goal

We have a working **JPA** persistence backend in this repo, plus a **backend-agnostic
persistence API** (`org.eclipse.fennec.persistence`). We want a **MongoDB** backend.

An old MongoDB integration exists in `org.gecko.emf.persistence/org.gecko.emf.persistence.mongo`.
It plugged Mongo in via EMF **`URIConverter` + `MongoInputStream`/`MongoOutputStream` +
a `MongoResourceSetConfigurator`** — i.e. it forced a document store through a byte-stream
keyhole, and needed global ResourceSet wiring (URI handler + stream factory + id factory).
That was necessary in the pre-codec era but is ugly to configure and a poor fit for a
document DB. **It is also not optimized** — a guiding principle for the new backend is to
use the optimal MongoDB paths (bulk writes, projections, ID-only reads, batch cursors).

A later prototype lived in `fennec-codec/org.eclipse.fennec.codec.mongo`
(`CodecMongoResource extends CodecResource`, `MongoResourceFactory`) — the right direction
(ResourceFactory + a Mongo-aware Resource that talks to the driver directly), but built on the
**old** codec SPI and left much WIP (load-into-contents discards results, upsert and query
engine commented out).

## Decision summary

| Aspect | Decision |
| --- | --- |
| **Repo** | Rename `emf.persistence-jpa` → **`emf.persistence`**; Mongo lives here as a sibling backend. **No** new `emf.mongo-persistence` / separate API repo (premature — see rationale). |
| **Bundle** | `org.eclipse.fennec.persistence.mongo` (backend). Optionally `…persistence.mongo.tests`. |
| **Contract** | Implement **`PersistenceResource`** (from `org.eclipse.fennec.persistence`) — gives `delete` / `count` / `exist` uniformly with JPA, which a bare EMF `Resource` lacks. |
| **Blueprint** | Mirror **`JPAResourceImpl`** exactly (see below), **including `getEObject` fragment resolution** for the proxy model. |
| **PersistenceEngine SPI** | **Removed (AP-50).** The engine machinery (`PersistenceEngine`/`Basic`/`Default` + `Readable`/`Updateable`/`Deletable`/`Countable`) had no implementer and was deleted pre-release; `PersistenceResource.getEngine()` is gone. Backends work Resource-direct. |
| **Serialization** | Reuse the **bson codec** (`org.eclipse.fennec.codec.bson`) for EObject ↔ `org.bson.BsonDocument`, **BsonDocument-direct** (no byte stream). The direct entry point exists — see "Codec integration" below. |
| **Connection** | **Config-driven `MongoClient`** — ConfigurationAdmin factory configs following the proven gecko pattern (client PID + database PID, heartbeat status). Connection is **not** in the EMF URI. |
| **Optimization principle** | The old mongo impl is not optimized; the new one must be: bulk upserts, ID-only projections for proxies, batch cursors, server-side paging. |
| **Streaming API** | **First-class API**, not the gecko `EPushStreamProvider`-in-contents trick. `java.util.Stream` in the core backend; OSGi PushStream in a separate add-on bundle. Evaluate & implement the same for **JPA**. |
| **Time series** | Design for it in both backends; **implement in Mongo** (time-series collections). JPA/TimescaleDB analysis later. |
| **v1 scope** | CRUD per collection + `_id` + proxy resolution (`getEObject`). Query translation / projection / streaming → v1.5/v2 (see work plan). |

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
    // getEObject(fragment) : proxy resolution — parses //refName/idAttr/idValue, em.find, adds to contents
}
```

Key takeaways for Mongo:
- **`extends ResourceImpl implements PersistenceResource`**, constructor takes the backend handle
  (`MongoDatabase` for us), injected by the factory.
- Everything (load/save/delete/count/exist/getEObject) is implemented **on the resource**, directly
  against the backend. No engine indirection.
- The **`_id` strategy is already established by JPA: `eClass().getEIDAttribute()`** is the identity.
  Mongo uses the same → maps to Mongo `_id`.
- Reuse the **`Options`** vocabulary (`Options.getPageSize`, `Options.getTableEClass`,
  `Options.READ_FILTER_ECLASS`, …) so option semantics stay uniform across backends. `getTableEClass`
  → the collection / type filter maps naturally to "collection per EClass".

### Non-containment references & proxy resolution (must-have, v1)

The JPA backend establishes the framework-wide contract (AP-46/47/48): **non-containment
references hold lightweight EMF proxies** (id attribute + `eProxyURI`), resolved on access through
the standard EMF machinery: `ResourceSet.getEObject(proxyURI)` → backend resource
`getEObject(fragment)` → backend lookup. Documented in `getting-started.md`.

Mongo must play the same game:
- The bson codec serializes cross-document references — on load these must become EMF proxies with
  `mongodb://<db>/<collection>#…` URIs (verify codec behavior; part of the codec gap analysis).
- **`MongoResourceImpl.getEObject(fragment)` is v1 scope**: fragment → `_id` →
  `collection.find(eq("_id", id)).first()`. ~40 lines in the JPA pendant; essential — without it
  proxies from Mongo resources can never resolve.
- Payoff of the shared repo: **mixed ResourceSets work across backends** — a `jpa://` object can
  reference a `mongodb://` object and vice versa, exactly the mechanism proven by
  `NonOsgiCrossResourceRefTest` (JPA↔XMI).

## Proposed Mongo mapping

URI carries **addressing only** (no connection): `mongodb://<db>/<collection>[/<id>][?<filter>]`
- collection = URI segment (or `Options.getTableEClass().getName()`),
- id = last segment → Mongo `_id`,
- filter = query (work plan package 4).

| PersistenceResource op | MongoDB |
| --- | --- |
| `load` / `doLoad` | `collection.find(filter)` → `BsonDocument` → bson **reader** delegate → EObject → `getContents()`. Pagination via `Options.getPageSize` → `skip/limit`; `batchSize` for the cursor. |
| `save` / `doSave` | per content EObject → bson **writer** delegate → `BsonDocument`, set `_id` from the EID attribute → **`bulkWrite(ReplaceOneModel(eq("_id", id), doc, upsert))`** in batches. (Not the old `insertMany`, which broke re-save; not per-object round-trips either.) |
| `delete(options)` | `deleteOne(eq("_id", id))` or `deleteMany(filter)`. |
| `count()` / `count(options)` | `collection.countDocuments(filter)`. |
| `exist()` / `exist(options)` | `collection.countDocuments(filter, new CountOptions().limit(1)) > 0` (or `find(filter).limit(1).first() != null`). |
| `getEObject(fragment)` | fragment → typed `_id` → `find(eq("_id", id)).first()` → decode → add to contents (mirror JPA). |

### Codec integration: reuse the bson codec, BsonDocument-direct — **verified**

The new codec's BSON delegates are **`BsonDocument`-native**: `BsonFormatDelegate` has a public
constructor taking the target `BsonDocument` (+ `getTarget()`), `BsonFormatReaderDelegate` reads
from a `BsonDocument`. The `.bson` file format only wraps these with a stream (de)serializer.

**The stream-free entry point exists** (former open question 1, answered):
`FormatDelegateGenerator.create(ObjectWriteContext, ioCtxt, delegate)` and
`FormatDelegateParser.create(...)` (public, in `org.eclipse.fennec.codec.format.impl`) accept
**any** `FormatDelegate<T>` — including a directly constructed `BsonFormatDelegate(bsonDoc)`.
`CodecResource.doSaveWithFormat` uses exactly this bridge internally. No byte round-trip, no new
codec code.

Remaining design point: the generator needs the configured Jackson mapper (EMF modules), which
lives in `CodecResource`. Options: `MongoResourceImpl extends CodecResource implements
PersistenceResource` (mapper + options machinery for free; the old prototype's
`needOutputstream()==false` hook shows how to bypass stream I/O), or extract mapper construction.

**v2 optimization** (from the `fennec-codec` prototype): register a BSON `Codec<EObject>` in the
driver's `CodecRegistry` (fused with `getDefaultCodecRegistry()`) so the driver streams **directly**
via `BsonWriter`/`BsonReader` — no intermediate `BsonDocument` at all. Requires a
`BsonWriter`-backed `FormatDelegate` in `emf.codec.bson` (new delegate, moderate effort).

## What carries over from the old gecko mongo (snapshot branch)

| Old (`org.gecko.emf.persistence`, branch `snapshot`) | Now |
| --- | --- |
| `converter/*` (BigDecimal, BigInteger, Array, XMLGregorianCalendar) | ✅ handled by the bson codec + `CodecValueRegistry` |
| `codecs/*` (EObjectCodecProvider, DBObject/EObject builders, FeatureMapEntry-, MapEntryCodec) | ✅ replaced by the bson delegates (verify FeatureMap/EMap coverage in gap analysis) |
| `handler/*` (URIHandler, ResourceSetConfigurator, streams) | ❌ **dropped** — this is exactly what goes away |
| `Options`/`Keywords` vocabulary (`_id`, `_type`, `_superTypes`, `_eProxyURI`, lazy/batch/filter options) | ⚠️ **port** — this is the de-facto behavioral contract (merge into fennec `Options`/`Keywords`) |
| `SimpleMongoIdFactory` / `_id` mapping | ⚠️ **new**, strategy decided: `getEIDAttribute()` ↔ `_id`; generate `ObjectId` when absent **and write it back** to the EObject; accept raw ids otherwise |
| `NativeQueryEngine` / `EMongoQuery` / `ProjectionHelper` | ⚠️ work plan package 4 — port the *algorithm* (filter/projection/sort/skip/limit + EClass type filter + EMF projection augmentation), not the plumbing |
| `OPTION_LAZY_RESULT_LOADING` (ID-only projection → EMF proxies) | ⚠️ port — congruent with our AP-47 proxy model; Mongo projections are the ideal tool |
| `org.gecko.emf.persistence.pushstreams` + JDBC handler pair | ⚠️ v1.5 — as **first-class API** in a fennec add-on bundle (see part 2, point 7) |
| `EMongoCursor` (vestigial), stream/URIHandler plumbing | ❌ reference only |

## Proposed bundle shape

`org.eclipse.fennec.persistence.mongo` (follow `.eclipselink` bnd conventions):
- `MongoResourceImpl` — `PersistenceResource`, BsonDocument-direct via the codec bridge.
- `MongoResourceFactory` — `@Component service = Resource.Factory.class`, binds a `MongoDatabase`
  service (by alias/target), URI scheme `mongodb` (mirror how `JPAResourceFactory` binds).
- Client/DB configuration components (gecko pattern, see part 2, point 1).
- Depends on: `org.eclipse.fennec.persistence` (API) + `org.eclipse.fennec.codec.bson` +
  `org.mongodb:mongodb-driver-sync` + `org.mongodb:bson`.

Add-on (v1.5): `org.eclipse.fennec.persistence.pushstreams` — generic streaming support +
Mongo (and JPA) implementations; keeps `org.osgi.util.pushstream` out of the core backends.

---

# Teil 2 — Bewertung: die 10 Punkte (Stand 2026-07-16)

Ergebnis der Explorationen von `org.gecko.emf.persistence` (**Branch `snapshot`**),
`fennec-codec/org.eclipse.fennec.codec.mongo`, `emf.codec` und
`fennec.common.models/org.eclipse.fennec.query.model`.

## 1. Konfigurierbarer MongoClient — gecko-Muster übernehmen, modernisiert

`org.gecko.persistence.mongo` hat das bewährte Muster: **zwei Factory-PIDs**.
- `persistence.mongo.client` (`MongoClientController`): baut `MongoClient` aus vollständiger OCD
  (connectionString, Credentials, Pool-Vokabular, SSL, Timeouts, readConcern, retryWrites …),
  registriert ihn als Service. **Behaltenswert:** der Controller lauscht auf Server-Heartbeats
  (`ServerMonitorListener`) und flippt eine `status`-Service-Property, sodass Consumer per
  Target-Filter nur an *lebende* Clients binden.
- `persistence.mongo.database` (`GeckoMongoDatabaseImpl`): bindet Client per Status-Filter,
  registriert `MongoDatabase` mit `alias`; UID = `clientUID/alias`.

Die `MongoResourceFactory` bindet `MongoDatabase` per Alias — Verbindung bleibt komplett aus der
URI heraus. Für v1 genügt ggf. eine vereinfachte Ein-PID-Variante (connectionString + database);
die Zwei-PID-Trennung lohnt bei mehreren DBs pro Client.

## 2. Übernahme aus dem alten gecko-Mongo — Dreiteilung

- **Portieren (backend-neutrale Logik):** `Options`/`Keywords`-Vokabular (siehe Carry-over-Tabelle
  oben); Query-Algorithmik der `NativeQueryEngine` (filter/projection/sort/skip/limit,
  EClass-Typ-Filter über `_type`/`_superTypes`, `ProjectionHelper` ergänzt Projektionen um
  `_id`/`_type`-Schlüssel, damit Ergebnisse typisierbar bleiben); Upsert/Bulk/Id-Semantik aus
  `MongoOutputStream` (findOneAndReplace-Upsert, bulkWrite, `PrimaryKeyFactory` mit
  ObjectId-Default und Write-back der generierten Id); Lazy-Result-Loading (ID-only-Projektion →
  EMF-Proxies — deckungsgleich mit unserem AP-47-Modell).
- **Neu schreiben:** URIHandler/Stream-Fassade, ResourceSetConfigurator, StreamFactories.
- **Nur Referenz:** BSON-Codec-Klassen (ersetzt durch emf.codec), `EMongoCursor` (vestigial).
- **SPI-Erbe, das sich lohnt:** der neue `InputContentHandler<RESULT>`/`InputContext`-Seam des
  snapshot-Branches (pluggable Result-Verarbeitung) — der saubere Ort für PushStream-Handler,
  Lazy-Proxy-Projektion und Custom-Results, ohne den Kern zu belasten.

## 3. Codec-Anbindung — gestuft

- **v1: BsonDocument-direct** über `FormatDelegateGenerator/Parser` + `BsonFormatDelegate(bsonDoc)`
  (verifiziert, null neuer Codec-Code; Details oben unter „Codec integration").
- **v2: `Codec<EObject>`-Registry-Fusion** wie im fennec-codec-Prototyp — Driver streamt direkt in
  `BsonWriter`/`BsonReader`, kein Zwischen-Dokument. Voraussetzung: `BsonWriter`-basiertes
  `FormatDelegate` im emf.codec.
- Der Prototyp liefert außerdem: `needOutputstream()==false`-Hook, Collection-Name-Auflösung
  (Option EClass/String, sonst URI-Segment), `_id`-Politik mit ObjectId-Write-back,
  `CodecMongoOptions`-Vokabular + Options-Builder.

## 4. PersistenceResource / PersistenceEngine — entschieden & umgesetzt

`JPAResourceImpl` **implementiert** `PersistenceResource` — der Contract ist backend-neutral und
trägt für beide Backends. Die ungenutzte `PersistenceEngine`-Maschinerie wurde **entfernt (AP-50,
pre-release, kein Implementierer)**: Engine-Interfaces + `Readable/Updateable/Deletable/Countable`
gelöscht, `getEngine()` aus dem Interface gestrichen. Sinnvolle Erweiterungspunkte des alten
Systems (QueryEngine, `PrimaryKeyFactory`, `InputContentHandler`) werden bei Bedarf als fokussierte
Services neu eingeführt — nicht als Monolith-Engine.

## 5. Query-Modell (`org.eclipse.fennec.query.model`) — Vokabular ja, Komponente nein

Befund: modellseitig brauchbarer Seed (Subject/From/Where, Comparator-Hierarchie, Sort,
`limit`/`skip`), aber: keine Builder, keine Übersetzer, keine Tests, `compare()`/`execute()` sind
`UnsupportedOperationException`-Stubs, von niemandem konsumiert. Strukturelle Lücken: kein
Boolean-Operand-Baum (And/Or/Not ohne Kinder!), `Eq` nur als Number-Comparator, kein
`IN`/`EXISTS`/Regex, Sort ohne Nested-Paths (rohes `EStructuralFeature` statt `FeaturePath`),
Werte stringly-typed, einzige Aggregation `Average`, kein Zeit-Bucketing.

**Strategie:** Mongo v1 nicht daran hängen — v1 nutzt native JSON-Queries (Punkt 2-Portierung).
Strategisch ist das Modell als gemeinsame Query-Schicht der richtige Ansatz (EMF-nativ,
backend-neutral); Ausbau als eigenes Arbeitspaket (Fixes + Builder + Übersetzer: BSON zuerst,
JPQL/Criteria danach; Zeitreihen-Konzepte von Anfang an, siehe Punkt 10). Brücke bei Bedarf:
geckos kleine, funktionierende `org.gecko.emf.repository.query`-API (`IQueryBuilder`,
Range/Value/OperatorQuery).

## 6. Mongo-Features & Optimierungen (Leitprinzip: die neue Impl ist optimiert)

Relevante Feature-Basis (Driver 5.x / Server 7–8, gegen aktuelle Releases verifizieren):
Client-BulkWrite-API (8.0, cross-collection Bulk in einem Kommando), Time-Series-Collections
(seit 5.0, in 7/8 deutlich verbessert), Change Streams, Queryable Encryption.

Konkrete Optimierungen für unser Backend:
- **Upsert-Batches** via `bulkWrite(ReplaceOneModel(upsert))` statt Einzel-Roundtrips (`doSave`).
- **Projection-basiertes Lazy-Loading** kongruent zu AP-47: nur `_id` (+`_type`) laden →
  EMF-Proxies; Auflösung on access via ResourceSet.
- **`batchSize`-Cursor + serverseitige Pagination** (`Options.getPageSize` → skip/limit).
- `countDocuments(limit 1)` für `exist`.
- Change Streams → EMF-Notifications / PushStream (v2+, Add-on).
- Index-Handling: `_id` ist implizit indiziert; sekundäre Indizes später deklarativ (eorm-artig
  oder Options), nicht in v1.

## 7. Streaming: PushStream & Java-Stream — **korrigierter Befund + Entscheidung**

Der `snapshot`-Branch enthält (anders als der zuerst untersuchte Branch) eine vollständige
PushStream-Architektur: eigenes Bundle `org.gecko.emf.persistence.pushstreams` mit
`OPTION_QUERY_PUSHSTREAM` als Load-Option; statt Materialisierung landet ein
`EPushStreamProvider`-EObject in `getContents()`, der Consumer baut daraus seinen
`PushStream<EObject>` mit eigener Buffer-/Backpressure-Policy. Der Pull-Loop
(`PushEventSourceRunnable`) honoriert Backpressure über den Rückgabewert von `consumer.accept()`
(`>0` → sleep, `<0` → stop), Fehler/Ende als `error`/`close`-Events; sync/async wählbar
(`OPTION_QUERY_PUSHSTREAM_MULTITHREAD`, optional eigener Executor).

Einschränkungen des Originals: **für Mongo nie implementiert** (nur ein unbenutzter
`MongoInputContext`-Stub; einzige Impl ist JDBC), und eine **Cleanup-Lücke** (Stream-Close schließt
nur das `ResultSet`, nie Connection/Statement) — nicht kopieren.

**Entscheidung:** beides, gestuft, mit **erstklassiger API** statt des Provider-in-Contents-Tricks
(der verbiegt den Resource-Contract — Contents ≠ Domänenobjekte):
- Kern-Backend: pull-basiertes Laden + Pagination; zusätzlich `java.util.Stream<EObject>` über den
  `MongoCursor` (Cleanup via `Stream.onClose`, passt zu `PersistenceResource extends AutoCloseable`).
- Add-on-Bundle `org.eclipse.fennec.persistence.pushstreams`: generischer Runnable/EventSource-Teil
  + Mongo-Implementierung über `MongoCursor`; API erstklassig (z.B. Streaming-Interface, das die
  Backend-Resource optional implementiert), `org.osgi.util.pushstream` bleibt aus dem Kern heraus.
- **JPA zieht mit:** prüfen und umsetzen — EclipseLink bietet mit `CursoredStream`/
  `ScrollableCursor` die passenden serverseitigen Cursor; derselbe generische Teil trägt dann
  beide Backends.
- Cleanup-Contract von Anfang an: Close/Cancel/Error schließt Cursor **und** zugrunde liegende
  Ressourcen.

## 8. Codec-Feature-Parität — Gap-Analyse als Vorab-Paket

Verifiziert im neuen `emf.codec.bson`: BsonDocument-native Delegates, ObjectId-Support
(`writeObjectId`/`readObjectId`/`supportsNativeObjectId`), Decimal128-Pfade im Provider.

Zu verifizieren / ggf. nachzurüsten (Stärken des alten Mongo-Codecs):
- `_type`/`_superTypes`-Schlüssel (Typ-Diskriminierung im schemalosen Store — Basis für
  EClass-Filter und Vererbung),
- `_eProxyURI`-Serialisierung für Non-Containments (unser Proxy-Modell!),
- Id-Attribut ↔ `_id`-Mapping als Option; **Write-back generierter ObjectIds** ins EObject,
- Extended-Metadata-Feature-Namen, Enum-Literal vs. -Name,
- FeatureMap-/EMap-Abdeckung,
- Default-Werte-Serialisierung (an/aus).

Arbeitsform: Gap-Analyse mit Testfällen im `emf.codec`-Repo (Arbeitsplan Paket 1).

**Bereits gefundene Export-Lücken (behoben im emf.codec-Branch `exports`, Publish ausstehend):**
`org.eclipse.fennec.codec.deser`, `…codec.ser`, `…codec.module` (Core) und
`…codec.metadata.type` (metadata-Bundle) waren im publizierten Snapshot nicht exportiert.

**Weiterer Codec-Gap (Format-Delegate-Pfad):** `ReferenceSerializationEntry.isCrossDocument`
braucht einen `CodecWriteContext` am Generator — der `FormatDelegateGenerator` (bson/xlsx/…)
hat nur Jacksons `SimpleStreamWriteContext`, daher werden Cross-Resource-Referenzen als nackte
Fragmente und Proxy-Ziele als Typ-URI geschrieben. Vorschlag: Writer-seitiger Fallback über ein
`SerializationContext`-Attribut (`ContextHelper.RESOURCE`), wie es der Lesepfad bereits kennt.
Bis dahin zieht `MongoResourceImpl.rewriteCrossResourceReferences` die URIs nach (idempotent).
Konsequenz bis zum nächsten Snapshot-Publish: der Gradle-Build des Mongo-Bundles ist grün
(javac kennt keine Access-Rules), aber die IDE markiert die Imports und das Bundle würde in
OSGi nicht resolven. Nach dem Publish: TypeDiscriminator-Service in
`MongoResourceImpl.mongoMapper()` wieder aktivieren (metadata.type ist dann sichtbar).

## 9. PostgreSQL-BSON-Store — Architektur-Leitplanke

Ziel-Store: die BSON-basierte DocumentDB-Extension für PostgreSQL (FerretDB 2.x setzt darauf auf
und spricht das **Mongo-Wire-Protokoll**). Konsequenzen:
- Zugriff via FerretDB → unser Mongo-Backend funktioniert **unverändert** (nur anderer
  connectionString). Größte Synergie, null Zusatzcode.
- Nativer Zugriff (SQL-Funktionen) → späteres JDBC-Transport-Bundle; die
  EObject↔BsonDocument-Schicht bleibt identisch.
- **Leitplanke ab v1:** die Codec-Brücke strikt transportfrei halten (BsonDocument rein/raus,
  kein Driver-Typ in Signaturen) — dann ist der Postgres-Weg ein reines Transport-Bundle.

## 10. Zeitreihen — für JPA und Mongo mitdenken, in Mongo implementieren

- **Mongo:** Time-Series-Collections (Creation-Options `timeField`/`metaField`/`granularity`).
  In der Impl vorsehen: Collection-Erzeugung mit TS-Optionen (Config/Options), damit
  Zeitreihen-EClasses auf TS-Collections landen; Abfragen profitieren von Datums-Range +
  serverseitiger Aggregation. Umsetzung: Arbeitsplan Paket 5.
- **JPA:** TimescaleDB/Hypertables als Analysepunkt (DDL-Integration, `time_bucket`-Queries) —
  eigenes Paket nach Mongo.
- **Query-Modell:** beim Ausbau (Punkt 5) Zeit-Bucketing/Downsampling/Aggregationsfenster von
  Anfang an modellieren — heute fehlen sie dort komplett (nur `IsBefore/IsAfter/IsInRange`,
  einzige Aggregation `Average`).

---

# Teil 3 — Arbeitsplan

Reihenfolge wie vereinbart: Start mit Mongo v1, dann v1.5. Pakete sind einzeln abarbeitbar;
Status wird hier gepflegt.

| # | Paket | Inhalt | Status |
| --- | --- | --- | --- |
| 0 | **API-Bereinigung** | `PersistenceEngine`-SPI entfernt (AP-50): Engine-Klassen + `Readable/Updateable/Deletable/Countable` gelöscht, `getEngine()` aus `PersistenceResource` gestrichen, JPA + Tests + Doku nachgezogen. | ✅ Done |
| 1 | **Codec-Gap-Analyse** (emf.codec) | Punkt-8-Liste verifizieren/nachrüsten, mit Testfällen: `_type`/`_superTypes`, `_eProxyURI` (Proxy-URIs beim Laden!), id↔`_id`-Option, ObjectId-Write-back, Extended-Metadata, Enum-Literal, FeatureMap/EMap, Default-Werte. Deliverable: dokumentiertes Options-Set für Mongo. | ⬜ Offen |
| 2 | **Mongo v1** (`org.eclipse.fennec.persistence.mongo`) | (a) Client/DB-Konfiguration (gecko-Muster: Client-PID + DB-PID, Heartbeat-Status, Alias-Binding); (b) `MongoResourceFactory` (Scheme `mongodb`, bindet `MongoDatabase`); (c) `MongoResourceImpl` — `PersistenceResource`, BsonDocument-direct (FormatDelegate-Brücke), `doSave` als **bulkWrite-Upsert**, count/exist/delete, Pagination (`Options.getPageSize`), `_id`-Strategie (EID↔`_id`, ObjectId-Generierung + Write-back); (d) **`getEObject`-Fragment-Resolution** (Proxy-Modell) + Misch-ResourceSet-Test JPA↔Mongo; (e) Optimierungen: ID-only-Projektion für Lazy-Option, `batchSize`; (f) Tests via TCK (Docker/Podman-Container oder `MONGO_URI`, keine `localhost:27017`-Annahme). | 🟨 Weitgehend done — **Misch-ResourceSet JPA↔Mongo umgesetzt und getestet** (`MixedBackendResourceSetTest`: Mongo-Dokumente referenzieren JPA-Objekte, Auflösung über den gemeinsamen ResourceSet; JPA schreibt/löst id-basierte Fragmente; Richtung JPA→Mongo ist storage-bedingt unmöglich — FK-Spalten. Cross-Resource-URIs werden beim Mongo-Encode nachgezogen, da der Codec-Format-Delegate-Pfad ohne `CodecWriteContext` keine Cross-Document-Erkennung hat — Codec-Verbesserung als Gap-Analyse-Punkt notiert). Offen: ID-only-Lazy-Projektion (e), BOM/Workspace-Library-Aufnahme |
| 3 | **Mongo v1.5 — Streaming** | (a) API-Design: erstklassige Streaming-API (`java.util.Stream<EObject>` im Kern; PushStream-Interface im Add-on) inkl. Cleanup-Contract (Close/Cancel/Error schließt Cursor + Ressourcen — gecko-Lücke nicht erben); (b) Add-on-Bundle `org.eclipse.fennec.persistence.pushstreams`: generischer EventSource/Runnable-Teil (Backpressure-Semantik wie gecko: accept-Rückgabewert honorieren, error/close-Events, sync/async + Executor-Option) + Mongo-Impl über `MongoCursor`; (c) **JPA umgesetzt**: `JPAResourceImpl` streamt über EclipseLink `ScrollableCursor`; derselbe generische PushStream-Adapter bedient beide Backends. | ✅ Done |
| 3b | **Persistence-TCK** | Backend-agnostische Suite `org.eclipse.fennec.persistence.tck` (Attribute, Containment, Non-Containment single/multi als Proxies, Bidi, count/exist/delete, Stream, PushStream) mit JPA- und Mongo-Binding — beide 9/9 grün. Gefundene Backlog-Punkte: ~~eorm-Mapper reihenfolge-sensitiv bei eOpposite-Paaren~~ (behoben: Stage-5-Normalisierung in `MappingProcessor.createOppositeMapping` + gehärtete Opposite-Pfade, JPA-TCK läuft als Regression mit der vormals kaputten Reihenfolge); ~~String-IDs im JPA-Mapper nicht unterstützt~~ (Fehldiagnose — die Fehler stammten vom Reihenfolge-Bug; mit explizit gesetzten String-IDs läuft der JPA-TCK grün, verifiziert 2026-07-16. ID-Generierung ist inzwischen vollständig getestet: der TCK läuft in vier Bindings — JPA/Mongo × int-/String-IDs — inkl. `idGenerationOnSaveAssignsAndWritesBackId`. Ergebnisse: JPA-Sequencing schreibt auch in String-PK-Felder (EclipseLink konvertiert), generierte IDs werden nach `save()` in das EObject zurückgeschrieben (Write-back in `JPAResourceImpl` nachgerüstet, Mongo hatte es bereits); Mongo lehnt Generierung für numerische IDs mit klarer Fehlermeldung ab — dokumentierter Contract); Codec-`_type` nutzt die Metamodell-Resource-URI (nsURI-Registrierung empfohlen). | ✅ Done |
| 4 | **Query v1 (nativ)** | Portierung der `NativeQueryEngine`-Semantik: JSON-Query (URI-Query-Part/Options) mit `filter`/`projection`/`sort`/`skip`/`limit`; EClass-Typ-Filter (`_type`/`_superTypes`); `ProjectionHelper`-Augmentation; delete-by-filter, count-by-filter. | ⬜ Offen |
| 5 | **Zeitreihen (Mongo)** | Time-Series-Collections: Erzeugung mit `timeField`/`metaField`/`granularity` (Config/Options), Zuordnung Zeitreihen-EClass → TS-Collection, Range-Query-Pfad, Tests. Analyse-Anhang: TimescaleDB-Ansatz für JPA. | ⬜ Offen |
| 6 | **Query-Modell strategisch** (`fennec.common.models`) | Modell-Fixes (Operand-Baum für And/Or/Not, generisches `Eq`, `In`/`Exists`/Regex, `FeaturePath`-Sort, typisierte Werte, Zeit-Bucketing + Aggregat-Set), fluent Builder, Übersetzer BSON → dann JPQL/Criteria; ersetzt Query v1 schrittweise als gemeinsame Schicht. | ⬜ Offen |
| 7 | **Repo-Rename** `emf.persistence-jpa` → `emf.persistence` | `github_repository` in `gradle.properties`, Workflow-Referenzen, Docs-Basispfad, EF/otterdog-Org-Settings; GitHub-Redirects greifen automatisch. Danach ggf. Workspace-Library umbenennen. | ⬜ Offen |
| 8 | **PostgreSQL-Perspektive** | (a) FerretDB-Kompatibilitätstest des Mongo-Backends (nur connectionString); (b) Analyse natives DocumentDB-SQL-Transport-Bundle; (c) TimescaleDB für JPA (aus Paket 5-Analyse). | ⬜ Offen |

## Open questions (updated)

1. ~~**Codec hook**~~ — **answered**: `FormatDelegateGenerator`/`FormatDelegateParser` +
   `BsonFormatDelegate(bsonDoc)`; remaining detail = mapper access (extend `CodecResource` vs
   extract mapper construction) → decide in package 2.
2. **`_id` types** — strategy decided (EID ↔ `_id`, ObjectId generation + write-back, raw values
   accepted); remaining detail = typed conversion table for non-String EIDs → package 2.
3. **Collection resolution precedence** — prototype order adopted: option (`EClass`/`String`) else
   URI segment; mixed contents per resource: single collection in v1 → revisit with query work.
4. **Save semantics** — decided: `bulkWrite(ReplaceOneModel(upsert))` batches; multi-doc
   transactions out of v1 (needs replica set); **document that `doSave` is not atomic** (unlike
   JPA's single tx) in getting-started when the backend lands.
5. **Rename mechanics** — package 7.
6. **BOM / workspace library** — part of package 2(f).
7. **Streaming API shape** — first-class API decided; exact interface (core `Stream` accessor +
   add-on PushStream interface) designed in package 3(a).
8. **Test infrastructure** — Testcontainers vs Flapdoodle for unit-level; OSGi tests (bndrun) need
   container availability in CI → decide in package 2(f).

## Cross-references

- API bundle: `org.eclipse.fennec.persistence` (`PersistenceResource`, `Options`, `Keywords`,
  `PersistenceException`; the former `PersistenceEngine` machinery was removed pre-release, AP-50).
- JPA blueprint: `org.eclipse.fennec.persistence.eclipselink.resource.JPAResourceImpl` /
  `JPAResourceFactory`; proxy model: `getting-started.md` ("How references are loaded"), AP-46/47/48
  in `REVIEW.md`.
- bson codec: `org.eclipse.fennec.codec.bson` (`BsonFormatDelegate`, `BsonFormatReaderDelegate`,
  `BsonFormatProvider`, `BsonResourceFactoryComponent`) in the `emf.codec` repo; stream-free bridge:
  `org.eclipse.fennec.codec.format.impl.FormatDelegateGenerator`/`FormatDelegateParser`.
- Old gecko impl (reference; branch **`snapshot`**): `org.gecko.emf.persistence/…persistence.mongo`,
  `…persistence.pushstreams`, `…persistence.jdbc` (pushstream pair), `org.gecko.persistence.mongo`
  (client/db config).
- Old codec-mongo prototype (reference only): `fennec-codec/org.eclipse.fennec.codec.mongo`.
- Query model (strategic): `fennec.common.models/org.eclipse.fennec.query.model`.
