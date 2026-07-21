# MongoDB User Guide

This guide shows how to persist EMF models in MongoDB with Eclipse Fennec
Persistence. The MongoDB backend lives in the
`org.eclipse.fennec.persistence.mongo` bundle and follows the same design as
the JPA backend: you read and write EObjects through EMF's standard
`Resource` API — here over a `mongodb://` URI scheme.

The big difference to the JPA backend: **there is no ORM mapping step**.
EObjects are (de)serialized to BSON documents by the Fennec codec framework
(`org.eclipse.fennec.codec.bson`), directly from the Ecore metadata. No
`.eorm` file, no `EntityMapper`, no DDL generation — a registered `EPackage`
is all the mapping the backend needs.

## Contents

1. [Prerequisites](#prerequisites)
2. [Configuring the backend (OSGi)](#configuring-the-backend-osgi)
3. [Connection liveness](#connection-liveness)
4. [Plain-Java setup (non-OSGi)](#plain-java-setup-non-osgi)
5. [The `mongodb://` URI scheme](#the-mongodb-uri-scheme)
6. [CRUD through an EMF Resource](#crud-through-an-emf-resource)
7. [Identity: EMF ids and `_id`](#identity-emf-ids-and-_id)
8. [How the codec maps EObjects to BSON](#how-the-codec-maps-eobjects-to-bson)
9. [References and proxies](#references-and-proxies)
10. [Mixing JPA and MongoDB in one ResourceSet](#mixing-jpa-and-mongodb-in-one-resourceset)
11. [Error handling and diagnostics](#error-handling-and-diagnostics)
12. [What is not (yet) supported](#what-is-not-yet-supported)

## Prerequisites

- Java 17 or newer
- A reachable MongoDB server (the tests use a plain `mongo:7` container)
- An Ecore model whose persistent EClasses each have exactly one EID
  attribute (`iD="true"`) — it becomes the document `_id`
- Bundles: `org.eclipse.fennec.persistence.mongo` plus its codec
  dependencies (`org.eclipse.fennec.codec`, `org.eclipse.fennec.codec.bson`,
  `org.eclipse.fennec.model.metadata`) and the MongoDB sync driver

If you are new to the framework as a whole, read
[Getting Started](getting-started.md) first — the EMF `Resource` usage
patterns are the same for both backends.

## Configuring the backend (OSGi)

Two factory PIDs configure the connection layer. Each configuration produces
one OSGi service.

### 1. The client — factory PID `persistence.mongo.client`

Creates a `com.mongodb.client.MongoClient` service. The service carries the
property `mongo.client.ident=<ident>` so database configurations can select
it.

```properties
# filename: persistence.mongo.client~main.cfg
ident=main
connectionString=mongodb://localhost:27017
```

| Key | Required | Purpose |
|-----|----------|---------|
| `ident` | yes | Unique client identifier; becomes the service property `mongo.client.ident` and the liveness condition id `fennec.liveness.<ident>` |
| `connectionString` | yes | Standard MongoDB connection string, e.g. `mongodb://user:pass@host:27017` |
| `liveness.*` | no | Probe tuning, see [Connection liveness](#connection-liveness) |

### 2. The database — factory PID `persistence.mongo.database`

Resolves a named database from a bound client and registers it as a
`com.mongodb.client.MongoDatabase` service with the property
`mongo.database.alias=<alias>`.

```properties
# filename: persistence.mongo.database~library.cfg
alias=library
database=library
client.target=(mongo.client.ident=main)
```

| Key | Required | Purpose |
|-----|----------|---------|
| `alias` | yes | Logical name; becomes the service property `mongo.database.alias` |
| `database` | yes | Name of the MongoDB database |
| `client.target` | no | OSGi filter selecting the `MongoClient` service (recommended when more than one client exists) |

### 3. Consuming the database service

The Mongo backend does not (yet) auto-mount its resource factory — you bind
the `MongoDatabase` service and wire the factory into your `ResourceSet`
yourself:

```java
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.model.metadata.api.MetadataService;
import org.eclipse.fennec.persistence.mongo.resource.MongoResourceFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.mongodb.client.MongoDatabase;

@Component
public class LibraryMongoService {

    private final ResourceSet resourceSet = new ResourceSetImpl();

    @Activate
    public LibraryMongoService(
            @Reference(target = "(mongo.database.alias=library)") MongoDatabase database,
            @Reference MetadataService metadataService) {
        resourceSet.getPackageRegistry()
                .put(LibraryPackage.eNS_URI, LibraryPackage.eINSTANCE);
        resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
                .put("mongodb", new MongoResourceFactory(database, metadataService, null));
    }
}
```

The third `MongoResourceFactory` argument is an optional
`CodecValueRegistry` for custom value (de)serialization — pass `null` to use
the codec defaults. The `MetadataService` must know every `EPackage` you
persist.

Because the component `@Reference`s the `MongoDatabase` service, standard DS
lifecycle applies: your component only activates while a verified connection
exists, and deactivates when it goes away — see the next section.

## Connection liveness

Creating a MongoDB client performs no I/O — the driver connects lazily in
the background. The backend therefore does **not** register the
`MongoClient` service just because a configuration exists. Instead the
service is registered through a liveness gate: it appears only after a
`ping` probe verifies the connection, and it disappears again (together with
all dependent `MongoDatabase` services) when the connection breaks. Presence
of the service means *the connection works* — see
[Connection Liveness](concept-connection-liveness.md) for the full concept.

Consequences for you as a user:

- **Startup order does not matter.** If MongoDB comes up after the OSGi
  framework, the services appear as soon as the first probe succeeds
  (retries run with exponential backoff).
- **Recovery is automatic.** A driver topology listener triggers immediate
  probes on reachability changes, so both failure detection and recovery are
  faster than the polling interval.
- While the gate is UP it additionally registers an
  `org.osgi.service.condition.Condition` with
  `osgi.condition.id=fennec.liveness.<ident>` — useful to gate whole
  subsystems on database availability.

The probe is tuned with `liveness.*` keys on the client configuration:

```properties
# filename: persistence.mongo.client~main.cfg
ident=main
connectionString=mongodb://localhost:27017
liveness.checkInterval=10
liveness.failureThreshold=3
```

See the [Configuration Reference](configuration-reference.md#connection-liveness)
for the full key table and defaults (`liveness.enabled`,
`liveness.checkInterval`, `liveness.checkTimeout`,
`liveness.failureThreshold`, `liveness.retryMin`/`liveness.retryMax`).

## Plain-Java setup (non-OSGi)

Outside OSGi you create the driver objects and the metadata service
yourself. This is exactly what the compatibility test suite
(`MongoPersistenceTckTest`) does:

```java
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.model.metadata.service.MetadataServiceImpl;
import org.eclipse.fennec.persistence.mongo.resource.MongoResourceFactory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

MongoClient client = MongoClients.create("mongodb://localhost:27017");
MongoDatabase database = client.getDatabase("library");

MetadataServiceImpl metadataService = new MetadataServiceImpl();
metadataService.registerPackage(libraryPackage);

ResourceSet resourceSet = new ResourceSetImpl();
resourceSet.getPackageRegistry().put(libraryPackage.getNsURI(), libraryPackage);
resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
        .put("mongodb", new MongoResourceFactory(database, metadataService, null));
```

Note that no liveness gate exists in this mode — you own the client
lifecycle, including error handling for an unreachable server.

## The `mongodb://` URI scheme

```
mongodb://<db>/<collection>[/<id>]
```

- `<db>` — the URI authority. It is **addressing only**: the actual
  connection is the `MongoDatabase` injected into the factory. Use a stable
  name (the database alias is a good choice) and keep it consistent within a
  ResourceSet — reference URIs written into documents are built from it.
- `<collection>` — the MongoDB collection, by convention the EClass name.
  The backend resolves the EClass for decoding by looking the collection
  name up as a classifier in the ResourceSet's package registry (falling
  back to the global registry). To use a different collection name, pass
  `Options.OPTION_TABLE_NAME` with an EClass value in the load/save options
  — it overrides both the collection name and the type hint.
- `<id>` — optional. With an id segment, `load` fetches exactly the one
  document with that `_id`; without it, the whole collection is loaded.

Examples: `mongodb://library/Book` (all books),
`mongodb://library/Book/42` (one book).

## CRUD through an EMF Resource

The examples below mirror the verified TCK tests
(`AbstractPersistenceTCK` in `org.eclipse.fennec.persistence.tck`).

### Create / update (upsert)

```java
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

Resource resource = resourceSet.createResource(URI.createURI("mongodb://library/Book"));
resource.getContents().add(book);
resource.save(null);
```

`save` performs a **bulk upsert**: every root object in `getContents()` is
encoded to a BSON document and written with a replace-by-`_id` (upsert)
model in one `bulkWrite`. New documents are inserted, existing ones
replaced.

### Read

```java
Resource books = resourceSet.createResource(URI.createURI("mongodb://library/Book"));
books.load(null);
for (EObject book : books.getContents()) {
    // ...
}
```

For large collections, load in pages:

```java
import org.eclipse.fennec.persistence.Options;

Map<Object, Object> options = new HashMap<>();
options.put(Options.OPTION_PAGE_SIZE, 1000);
books.load(options);
```

### Delete, count, exist

Mongo resources implement `PersistenceResource` (like the JPA backend):

```java
import org.eclipse.fennec.persistence.resource.PersistenceResource;

PersistenceResource persistence = (PersistenceResource) resource;
persistence.exist();      // true if the collection has any document
persistence.count();      // number of documents in the collection
persistence.delete(null); // deletes each object currently in contents by _id
```

Note: `count()` and `exist()` operate on the **whole collection** — they
ignore an id segment in the URI. `delete` removes exactly the documents
whose ids match the resource contents, then clears the contents.

### Streaming

Mongo resources implement `StreamingResource`; documents are decoded one by
one from a server cursor instead of materialising the whole collection:

```java
import java.util.stream.Stream;
import org.eclipse.fennec.persistence.resource.StreamingResource;

Resource resource = resourceSet.createResource(URI.createURI("mongodb://library/Book"));
try (Stream<EObject> stream = ((StreamingResource) resource).stream()) {
    stream.forEach(book -> process(book));
}
```

`Options.OPTION_PAGE_SIZE` sets the cursor batch size. An OSGi
`PushStream` view is available through the shared helper:

```java
import org.eclipse.fennec.persistence.pushstreams.PersistencePushStreams;
import org.osgi.util.pushstream.PushStream;

try (PushStream<EObject> stream = PersistencePushStreams.createPushStream(
        (StreamingResource) resource, null, null, null)) {
    stream.forEach(book -> process(book));
}
```

## Identity: EMF ids and `_id`

The EMF ID attribute (`eClass().getEIDAttribute()`) maps to the MongoDB
`_id` field:

- `int`/`Integer` and `long`/`Long` ids are stored as native BSON int32 /
  int64; everything else is stored as a string.
- An EClass **without** an EID attribute cannot be saved — `save` fails with
  an `IOException`.
- **Id generation** works for String-typed id attributes only: when the id
  is unset at save time, the backend generates a MongoDB `ObjectId` hex
  string and **writes it back** into the EObject. For numeric id attributes
  an unset id fails the save with a clear error (`cannot generate an
  ObjectId`) instead of inventing values — assign numeric ids yourself.

```java
EObject person = EcoreUtil.create(personClass);   // String-typed id, unset
person.eSet(personName, "Generated");
resource.getContents().add(person);
resource.save(null);
String id = EcoreUtil.getID(person);              // ObjectId hex, written back
```

## How the codec maps EObjects to BSON

Serialization is *BsonDocument-direct*: each root EObject is written into /
read from a `BsonDocument` through the Fennec codec's BSON format delegates
— no intermediate byte stream, no reflection-generated entity classes.

What this means for you:

- **No mapping artefact.** Unlike the JPA backend there is no
  `EntityMappings`/`.eorm` model, no processors, no DDL. The document shape
  follows the Ecore structure directly: attributes become fields,
  containment children become embedded sub-documents.
- **The `MetadataService` is the type source.** It supplies the codec with
  the registered `EPackage`s so documents can be decoded back into the right
  EClasses. Register every persisted package (in OSGi, bind the
  `MetadataService` from `org.eclipse.fennec.model.metadata`; in plain Java,
  call `MetadataServiceImpl.registerPackage`).
- **Custom value handling** is possible via an optional
  `CodecValueRegistry` passed to the factory (each resource gets its own
  copy).

Because there is no eorm model, the eorm-driven tuning of the JPA backend
(fetch modes, batch hints, column overrides) does not apply here.

## References and proxies

The backend follows standard EMF cross-document semantics — the same
contract as the JPA backend and XMI:

- **Containment references** are embedded: children are stored inside the
  owner's document and materialised eagerly with it (single-document
  ownership).
- **Non-containment references** are stored as reference values (ids or EMF
  URIs) and loaded as **EMF proxies**. The first `eGet` resolves the proxy
  through the ResourceSet: the target resource's `getEObject` runs a
  find-by-`_id`, decodes the document, and replaces the proxy. As with the
  JPA backend, resolution requires the resource to live in a `ResourceSet`.
- Cross-resource references must follow standard EMF rules at save time:
  the target object must already be in a resource of the same ResourceSet,
  otherwise no reference URI can be written.

```java
EObject loaded = findByIdInContents(books, "1");
EObject author = EcoreUtil.resolve((EObject) loaded.eGet(bookAuthor), resourceSet);
```

**In-flux caveat:** references from a Mongo document to objects in *other*
resources are currently rewritten to absolute EMF URIs by a workaround for
[emf.codec#50](https://github.com/eclipse-fennec/emf.codec/issues/50). The
rewrite covers only the references of the **root** object — cross-resource
references held by nested containment children are not yet rewritten. This
limitation disappears once the codec writes absolute URIs itself.

## Mixing JPA and MongoDB in one ResourceSet

Both backends can be mounted into one `ResourceSet` — this is verified by
`MixedBackendResourceSetTest` in the TCK:

```java
resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
        .put("jpa", new JPAResourceFactory(entityManagerFactory));
resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
        .put("mongodb", new MongoResourceFactory(database, metadataService, null));
```

Directionality is asymmetric by construction:

- A **Mongo document can reference a JPA-persisted object** — Mongo stores
  references as URIs, so a `jpa://…#<id>` proxy resolves through the shared
  ResourceSet into the JPA backend. A single many-valued reference may even
  mix JPA and Mongo targets.
- A **JPA row cannot reference a Mongo document** — JPA stores
  non-containment references as foreign keys and can only point at rows of
  its own database.

## Error handling and diagnostics

Identical contract to the JPA backend (see
[Getting Started — Error handling](getting-started.md#error-handling-and-diagnostics)):
failures in `load`/`save`/`delete` surface as `java.io.IOException` with the
driver exception as cause, and the resource populates EMF's standard
channels — `getErrors()` for fatal problems, `getWarnings()` for non-fatal
anomalies (e.g. a URI without a collection segment, or a proxy fragment that
could not be resolved). Both lists are cleared at the start of every
operation.

## What is not (yet) supported

Honest gap list compared to the JPA backend, based on what the TCK actually
verifies:

- **No query support.** A resource can load a whole collection or a single
  document by id — there is no filter/query API yet (the JPA backend's
  `READ_FILTER_ECLASS` option does not apply, and there is no equivalent of
  JPQL access). A query model is on the roadmap.
- **No transactions.** Each `save` is one `bulkWrite` of upserts; there is
  no unit-of-work or rollback spanning multiple resources.
- **Id generation only for String ids** (`ObjectId` hex). Numeric ids must
  be assigned by the application.
- **No automatic resource-factory registration.** You mount
  `MongoResourceFactory` into each `ResourceSet` yourself; there is no
  whiteboard that dispatches `mongodb://` URIs to configured databases yet.
- **`count()`/`exist()` are collection-wide** and ignore the URI id segment.
- **No eorm-style tuning** — fetch/batch/column configuration from the JPA
  backend has no Mongo counterpart (page size for load/stream is the only
  knob).
- **Cross-resource references of nested children** are not yet rewritten to
  absolute URIs (see the caveat in
  [References and proxies](#references-and-proxies)).
- `updateDefaultOptions` on the resource is a no-op (as in the JPA backend).

Both backends pass the same backend-agnostic TCK
(`AbstractPersistenceTCK`): attribute round trips, containment, single- and
many-valued non-containment references, bidirectional references,
`PersistenceResource` operations, id generation write-back, Java `Stream`
and `PushStream` — so within the supported feature set the behaviour is
interchangeable.

## Next steps

- [Getting Started](getting-started.md) — the JPA paths and general Resource usage
- [Configuration Reference](configuration-reference.md) — full property catalogue, liveness keys, load/save options
- [Connection Liveness](concept-connection-liveness.md) — the "registered means working" concept
