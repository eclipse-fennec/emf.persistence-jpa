# JPA User Guide

Day-to-day reference for working with Fennec Persistence JPA once your
persistence unit is up and running. It covers the `jpa://` URI scheme, CRUD and
query patterns through the EMF `Resource` API, load/save options, the eorm
mapping semantics, type converters, and what the lazy persistence-unit
lifecycle means for consuming code. Setup and bootstrapping are covered in
[Getting Started](getting-started.md) and are not repeated here.

## Contents

1. [The `jpa://` URI scheme](#the-jpa-uri-scheme)
2. [Working with persistence resources](#working-with-persistence-resources)
3. [Load and save options](#load-and-save-options)
4. [eorm mappings — how Ecore becomes a schema](#eorm-mappings--how-ecore-becomes-a-schema)
5. [Type converters](#type-converters)
6. [The persistence-unit lifecycle from a consumer's view](#the-persistence-unit-lifecycle-from-a-consumers-view)
7. [Liveness-gated DataSources](#liveness-gated-datasources)

## The `jpa://` URI scheme

Every JPA-backed resource is addressed by a URI of the form:

```
jpa://<unitName>/<EntityName>[#<id>]
```

| Part | Meaning |
|------|---------|
| authority (`<unitName>`) | The persistence-unit name — matched against the `osgi.unit.name` service property of the unit (the `persistenceUnitName` you configured) |
| last segment (`<EntityName>`) | The entity alias — the name of the mapped EClass |
| fragment (`#<id>`) | Optional: a single object's EMF id (the value of its EID attribute) |

Examples:

```
jpa://library/Book          all books (one resource per entity type)
jpa://library/Book#42       the book with id 42
jpa://crm/Contact           a different unit, a different database
```

In OSGi, one single whiteboard `Resource.Factory` serves the entire `jpa`
scheme and dispatches by the URI authority to the matching persistence unit —
you never register a factory yourself, you only need a `ResourceSet` (see
[OSGi architecture](osgi-architecture.md)). A URI naming an unknown unit still
yields a resource, but every operation on it fails with a diagnostic naming
the missing unit — there is no silent fallback.

In the non-OSGi path you wire the factory once, as shown in
[Getting Started](getting-started.md):

```java
resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
        .put("jpa", new JPAResourceFactory(emf));
```

Everything below is identical for both paths.

## Working with persistence resources

### Loading all instances of a type

```java
Resource books = resourceSet.createResource(URI.createURI("jpa://library/Book"));
books.load(null);
for (EObject book : books.getContents()) {
    // ...
}
```

`load(...)` itself is deliberately cheap: it only marks the resource loaded and
remembers the options. The actual `SELECT e FROM Book e` runs on the first
`getContents()` access. This matters for proxy resolution — resolving a single
cross-reference into a `jpa://` resource never materialises the whole target
table (issue #17). A load failure on deferred population surfaces as an EMF
`WrappedException` from `getContents()` and is recorded in
`resource.getErrors()`.

### Loading a single object by id

Use the id as the URI fragment and let the `ResourceSet` resolve it — this
issues exactly one `em.find`:

```java
EObject book = resourceSet.getEObject(
        URI.createURI("jpa://library/Book#42"), true);
```

The resolved object is cached in the resource's contents, so repeated access
does not hit the database again. The same mechanism resolves the proxy URIs
that lazy non-containment references carry
(`jpa://library/Book#//author/id/42` — see
[How references are loaded](getting-started.md#how-references-are-loaded-lazy-by-default)).

### Saving — upsert semantics

```java
Resource books = resourceSet.getResource(URI.createURI("jpa://library/Book"), true);
EObject book = books.getContents().get(0);
book.eSet(titleFeature, "New title");
books.save(null);
```

`save` runs one transaction over the resource contents. Per object:

- **Existing row** (the object carries a non-default id and a row with that id
  exists): the current attribute and containment-reference state is copied
  onto the managed instance — an UPDATE.
- **New object** (no id, or id is `0`/empty): `em.persist` — an INSERT.
  Sequence-generated ids are written back onto your EObject after commit, so
  the saved object always carries its id.
- Non-containment references that are still unresolved proxies are left
  alone — a proxy always represents an existing row and is never re-persisted.

Plain `DynamicEObjectImpl` objects (e.g. loaded from XMI) are converted to
managed dynamic entities transparently during save.

### Deleting

```java
books.load(null);
books.delete(null);   // removes every entity currently in getContents()
```

To delete a subset, load, remove the objects you want to keep from
`getContents()`, then call `delete`. To delete a single object, resolve it via
its id fragment into a fresh resource and delete that resource.

### Count, exist and streaming

`JPAResourceImpl` implements `PersistenceResource` and `StreamingResource`
from `org.eclipse.fennec.persistence.resource`, adding queries beyond the
plain `Resource` contract:

```java
import org.eclipse.fennec.persistence.resource.PersistenceResource;
import org.eclipse.fennec.persistence.resource.StreamingResource;

PersistenceResource pr = (PersistenceResource) books;
long total   = pr.count();     // SELECT COUNT(e) — no rows materialised
boolean any  = pr.exist();     // count() > 0

StreamingResource sr = (StreamingResource) books;
try (Stream<EObject> stream = sr.stream()) {
    stream.filter(b -> /* ... */ true).forEach(this::process);
}
```

`stream()` reads rows one by one from an open scrollable JDBC cursor instead
of materialising the full result list — suitable for tables that do not fit in
memory. The cursor, `EntityManager` and unit lease stay open until the stream
is closed, so always use try-with-resources. `Options.OPTION_PAGE_SIZE` is
applied as the JDBC fetch size.

### Error handling

All failures in `load`/`save`/`delete`/`count` surface as `IOException` with
the causing `PersistenceException`, plus per-problem detail in
`resource.getErrors()` and `resource.getWarnings()`. See
[Error handling and diagnostics](getting-started.md#error-handling-and-diagnostics)
for the pattern.

## Load and save options

Options are passed through the standard EMF `options` map — per call
(`resource.load(options)`) or globally
(`resourceSet.getLoadOptions()` / `getSaveOptions()`). Constants live on
`org.eclipse.fennec.persistence.Options`:

```java
import org.eclipse.fennec.persistence.Options;

Map<Object, Object> options = new HashMap<>();
options.put(Options.OPTION_PAGE_SIZE, 1000);              // load: paginate the SELECT
options.put(Options.READ_FILTER_ECLASS, myEClass);        // load: restrict to an EClass
options.put(Options.OPTION_TABLE_NAME, "MY_TABLE");       // load/save: override table
options.put(Options.OPTION_CACHE_NEW_OBJECTS, Boolean.FALSE); // save: skip L2 cache
resource.load(options);
```

The full table with value types and semantics is in the
[Configuration Reference](configuration-reference.md#resource-load-and-save-options).
Two of them deserve a note here:

- `OPTION_PAGE_SIZE` keeps memory flat for large tables by iterating
  `setFirstResult`/`setMaxResults` pages; for truly unbounded data prefer
  `stream()` (above).
- `OPTION_CACHE_NEW_OBJECTS = FALSE` is the bulk-insert knob: freshly
  persisted objects are not registered in EclipseLink's shared identity map,
  which avoids growing the second-level cache during mass writes.

## eorm mappings — how Ecore becomes a schema

The `EntityMappings` (eorm) model is the single source of truth for how each
EClass maps to the database — whether it was auto-generated by `EntityMapper`
or loaded from a pre-serialized `.eorm` file (see
[Mapping strategies](getting-started.md#mapping-strategies--generate-or-pre-serialize)).
The generator walks the EClasses through a processor chain and emits:

| Ecore element | eorm mapping | Relational result |
|---------------|--------------|-------------------|
| `EClass` | `Entity` | One table per entity (subject to the inheritance strategy) |
| single-valued `EAttribute` | `Basic` + `Column` | A column, type-converted via the `ConverterService` |
| many-valued `EAttribute` | `ElementCollection` | A collection table |
| single-valued `EReference` | `OneToOne` (or `ManyToOne` for the single side of a bidirectional pair) | FK column |
| many-valued `EReference` | `OneToMany` / `ManyToMany` | FK in the target table or a join table |

### Id handling

Every persistent EClass needs a primary key. The generator resolves it in this
order:

- **EID attribute** (`iD="true"` in the Ecore) — becomes the PK column. In
  non-strict mode (the default), numeric id types get a `SequenceGenerator`
  and `String` ids get a UUID `GeneratedValue`, so unset ids are generated on
  insert. An id value of `0` (numeric) or `""` (String) counts as "unset".
- **Composite ids** — multiple id attributes or a reference-based id class are
  detected and mapped as composite keys.
- **No id at all** — a synthetic `pk_<ClassName>` column with a sequence
  generator is added.

In `strict` mode no generators are invented — names and ids are taken as-is
(useful when mapping onto an existing schema).

### Containment vs. non-containment

The EMF reference kind drives both cascade and fetch semantics:

| | Containment | Non-containment |
|---|---|---|
| Semantics | Composition — the target is owned | Association — the target is shared |
| Cascade | `ALL` (deleting the parent deletes the children) | `PERSIST`/`DETACH`/`REFRESH`, deliberately **no** `REMOVE` — referenced objects survive parent deletion |
| Fetch default | `EAGER` — children are materialised with the owner | `LAZY` — the slot is filled with an EMF proxy carrying only the id |

The proxy contract for lazy references (single- and many-valued) is described
in [How references are loaded](getting-started.md#how-references-are-loaded-lazy-by-default).

### Cross-document containment, and its one limitation

EMF lets a containment child be a root of its own `Resource` while staying owned by its
parent — containment references have `resolveProxies=true`, so attaching the child
elsewhere keeps the container link. The JPA backend supports that shape:

```java
EObject place = …, point = …;
place.eSet(location, point);

Resource points = resourceSet.createResource(URI.createURI("jpa://app/GeoPoint"));
points.getContents().add(point);          // owned by place AND a root here
Resource places = resourceSet.createResource(URI.createURI("jpa://app/Place"));
places.getContents().add(place);

places.save(null);
points.save(null);                        // either order works
```

What holds:

- **Save order does not matter.** Whichever resource is saved first, the other's save
  updates the existing row instead of trying to insert it again.
- **The child is addressable.** A reference to it resolves, because its URI fragment is
  qualified with the containing reference, which names the target type.
- **Ownership cascades.** Dropping the child from its parent deletes it, transitively.
- **The child's own resource is a first-class entry point.** Loading `jpa://app/GeoPoint`
  hands the child over as a root there.

**The limitation:** after loading the parent alone, `child.eResource()` is the *parent's*
resource, not the child's. A JPA row is identical whether its object was an ordinary
containment child or additionally a resource root — the foreign key is the same — so
nothing on load can tell the two apart. The Mongo backend can, because the parent document
records it with a `{"$ref": …}` marker; JPA has nowhere for that fact to live, and
persisting it in a side table was judged not worth schema the model does not describe.

Practical consequences:

- `child.eResource().save(null)` saves the **parent's** resource. The child is included, but
  so is everything else in it.
- The shape is not persistent: load the parent, save it again, and the child is an ordinary
  containment child. Re-attach it to its own resource to re-establish it.
- Code meant to behave identically on Mongo and JPA should not ask a containment child which
  resource it belongs to — ask its container, or address it by reference.

Recorded as a deliberate divergence in
[Conformance and capabilities](unified-persistence/conformance-and-capabilities.md) §4b.

### Overriding fetch and batch per reference

Fetch behaviour is recorded on each eorm reference as two attributes — `fetch`
(`EAGER`/`LAZY`) and `batch` — and the type mapping reads exactly these
(issue #17). You control them either by editing a pre-serialized `.eorm`
directly, or via a persistence `EAnnotation` on the Ecore reference that the
generator translates:

```xml
<eStructuralFeatures xsi:type="ecore:EReference" name="tags"
    eType="#//Tag" upperBound="-1">
  <eAnnotations source="https://eclipse.org/fennec/persistence">
    <details key="fetch" value="LAZY"/>
    <details key="batch" value="true"/>
  </eAnnotations>
</eStructuralFeatures>
```

Programmatically (e.g. before calling `EntityMapper.createMappings`):

```java
import org.eclipse.fennec.persistence.Keywords;

EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
annotation.setSource(Keywords.PERSISTENCE_ANNOTATION_SOURCE);
annotation.getDetails().put("fetch", "LAZY");
annotation.getDetails().put("batch", "true");
reference.getEAnnotations().add(annotation);
```

Semantics:

- `fetch=EAGER` on a non-containment reference materialises the target(s)
  together with the owner — no proxies. Use it for references you always
  navigate.
- `fetch=LAZY` (the structural default for non-containment) fills the slot
  with lightweight proxies; each element is resolved individually on first
  access.
- `batch=true` on a **lazy many-valued** reference changes the resolution
  strategy: on first access of the collection, all elements are resolved in a
  single `IN` query instead of one `em.find` per element. Use it when you
  usually iterate the whole collection; leave it off when you mostly read ids
  or sizes.
- Inconsistent combinations (e.g. a containment marked `LAZY`) are corrected
  with a diagnostic during type mapping — containment stays eager.

The same annotation source carries further generator hints on EClasses and
attributes: `name` (alias for table/column names), `version` (optimistic-lock
column), `mappedSuperclass="true"`, and `inheritance` =
`SINGLE_TABLE` (default) | `JOINED` | `TABLE_PER_CLASS` on the hierarchy root.

## Type converters

EMF attribute types that have no direct JDBC representation are converted by
the `ConverterService` (`org.eclipse.fennec.persistence.api`). The default
service handles, out of the box:

| Category | Types |
|----------|-------|
| Time API | `LocalDate`, `LocalDateTime`, `LocalTime`, `Instant`, `ZonedDateTime`, `OffsetDateTime`, `Duration` |
| Numbers | `BigDecimal`, `BigInteger` |
| Identifiers / net | `UUID`, `java.net.URI`, `java.net.URL` |
| Arrays | object arrays and primitive arrays (`int[]`, `byte[]`, …) |
| Legacy | `XMLGregorianCalendar` |
| EMF | enums, non-containment EObject values |

Conversion is symmetric: `convertEMFToValue` on write,
`convertValueToEMF` on read. Round-trips preserve the value — an `Instant` or
`UUID` written through a resource comes back `equals()` to the original.
Converters are matched by the attribute's `EDataType` instance type name; the
first converter whose `isConverterForType` returns `true` wins.

### Custom converters

Implement `TypeConverter` and register it:

```java
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.fennec.persistence.api.TypeConverter;
import org.osgi.service.component.annotations.Component;

@Component
public class MoneyConverter implements TypeConverter {

    @Override
    public String getName() { return "money"; }

    @Override
    public boolean isConverterForType(EClassifier eDataType) {
        return "org.example.Money".equals(eDataType.getInstanceTypeName());
    }

    @Override
    public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
        return emfValue == null ? null : emfValue.toString();
    }

    @Override
    public Object convertValueToEMF(EClassifier eDataType, Object value) {
        return value == null ? null : Money.parse(value.toString());
    }
}
```

In OSGi the `ConverterWhiteboard` picks up every `TypeConverter` service
dynamically and lists all converter names in its `fennec.persistence.converter`
service property. In the non-OSGi path, extend `DefaultConverterService` and
add your converter to the protected `converters` list before wiring it into
the bootstrap.

## The persistence-unit lifecycle from a consumer's view

In OSGi a configured persistence unit follows a strict pay-per-use lifecycle
(issue #20, details in [OSGi architecture](osgi-architecture.md)): the
heavyweight EclipseLink factory is built on the **first** operation, kept warm
while in use, and closed again after an idle period (default 60 s,
`emfIdleTimeout`). As a resource user you get this for free — every
`load`/`save`/`delete`/`count` internally opens a short lease on the unit and
closes it when the operation ends. Practical consequences:

- The first operation on a cold unit pays the EclipseLink deploy (and DDL, if
  enabled) — expect it to be slower. Subsequent operations run on the warm
  factory.
- After the idle timeout the unit holds no connections, sessions or caches;
  the next operation rebuilds everything transparently. Nothing in your code
  changes — at most you see the deploy cost again.
- A `stream()` holds its lease until the stream is closed — a forgotten
  `close()` keeps the factory open (and its connections allocated)
  indefinitely.

If you consume the `EntityManagerFactory` service directly (plain JPA interop),
the get/unget window of the service is your lease: do not cache the instance
beyond it. For lower-level code the narrow `JPAUnit` service offers explicit
leases:

```java
import org.eclipse.fennec.persistence.eclipselink.spi.JPAUnit;

try (JPAUnit.Lease lease = unit.lease()) {
    EntityManager em = lease.createEntityManager(); // fresh, per operation
    // ...
}
```

## Liveness-gated DataSources

When the database behind a unit can come and go (network storage, failover),
put the gated-DataSource factory (`persistence.jdbc.gate`) between the raw
`DataSource` and the persistence unit, and target the marker property in the
unit configuration:

```properties
fennec.jpa.dataSource.target=(&(fennec.liveness=checked)(name=mydb))
```

The gate re-registers the upstream `DataSource` only while a probe verifies
the connection. From the consumer perspective: while the database is down, the
gated `DataSource` — and with it the persistence unit and its `JPAUnit`
service — is absent, so `jpa://` operations fail fast with the clear
missing-unit diagnostic instead of hanging on a dead connection. When the
probe succeeds again, the unit reappears and the same URIs work again without
any code change. Probe intervals, thresholds and backoff are configured on the
gate — see [Configuration Reference](configuration-reference.md#connection-liveness).
