# Getting Started

This guide shows two complete, end-to-end paths for using Fennec Persistence JPA:

- A **Non-OSGi** path — plain Java, you assemble the `EntityManagerFactory` yourself.
- An **OSGi** path — Declarative Services assemble the `EntityManagerFactory` from factory configuration.

Both paths end in the same place: you read and write entities through EMF's
standard `Resource` API over a `jpa://` URI scheme.

## Contents

1. [Prerequisites](#prerequisites)
2. [The example domain](#the-example-domain)
3. [Path A — Non-OSGi end-to-end](#path-a--non-osgi-end-to-end)
4. [Path B — OSGi end-to-end](#path-b--osgi-end-to-end)
5. [Mapping strategies — generate or pre-serialize](#mapping-strategies--generate-or-pre-serialize)
6. [CRUD through an EMF Resource](#crud-through-an-emf-resource)
7. [How references are loaded (lazy by default)](#how-references-are-loaded-lazy-by-default)
8. [Error handling and diagnostics](#error-handling-and-diagnostics)
9. [Next steps](#next-steps)

## Prerequisites

- Java 21 or newer
- A relational database (examples use in-memory H2)
- An Ecore model (shown below)
- For the OSGi path: a runtime with Declarative Services and Configuration Admin
  (Felix, Equinox, bnd-gradle)

## The example domain

Create `library.ecore` with two EClasses and a non-containment reference.
Every persistent EClass must have exactly one EID attribute — it becomes the
primary-key column.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<ecore:EPackage xmi:version="2.0"
        xmlns:xmi="http://www.omg.org/XMI"
        xmlns:ecore="http://www.eclipse.org/emf/2002/Ecore"
        name="library" nsURI="http://example.org/library" nsPrefix="lib">

  <eClassifiers xsi:type="ecore:EClass" name="Author">
    <eStructuralFeatures xsi:type="ecore:EAttribute" name="id"
        eType="ecore:EDataType http://www.eclipse.org/emf/2002/Ecore#//EInt"
        iD="true"/>
    <eStructuralFeatures xsi:type="ecore:EAttribute" name="name"
        eType="ecore:EDataType http://www.eclipse.org/emf/2002/Ecore#//EString"/>
  </eClassifiers>

  <eClassifiers xsi:type="ecore:EClass" name="Book">
    <eStructuralFeatures xsi:type="ecore:EAttribute" name="id"
        eType="ecore:EDataType http://www.eclipse.org/emf/2002/Ecore#//EInt"
        iD="true"/>
    <eStructuralFeatures xsi:type="ecore:EAttribute" name="title"
        eType="ecore:EDataType http://www.eclipse.org/emf/2002/Ecore#//EString"/>
    <!-- Non-containment reference: Book has one Author; the Author is not owned by Book. -->
    <eStructuralFeatures xsi:type="ecore:EReference" name="author"
        eType="#//Author"/>
  </eClassifiers>
</ecore:EPackage>
```

Key points:

- `iD="true"` on the `id` attribute marks it as the primary key.
- The `author` reference is **non-containment** (no `containment="true"`).
  Non-containment references are loaded lazily — see
  [How references are loaded](#how-references-are-loaded-lazy-by-default).

---

## Path A — Non-OSGi end-to-end

Plain Java. You load the Ecore, build an `EntityMappings`, boot EclipseLink,
and wire a `ResourceSet` by hand.

### A.1 — Bootstrap the EntityManagerFactory

```java
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.converter.DefaultConverterService;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicHelper;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicPersistenceUnitInfo;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicType;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicTypeGenerator;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.epersistence.EPersistenceFactory;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.sessions.server.Server;

import jakarta.persistence.EntityManagerFactory;

// 1. Load the Ecore model
ResourceSet rs = new ResourceSetImpl();
rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
        .put("*", new XMIResourceFactoryImpl());
Resource ecoreResource = rs.createResource(URI.createFileURI("library.ecore"));
ecoreResource.load(null);
EPackage pkg = (EPackage) ecoreResource.getContents().get(0);
rs.getPackageRegistry().put(pkg.getNsURI(), pkg);

EClass authorCls = (EClass) pkg.getEClassifier("Author");
EClass bookCls   = (EClass) pkg.getEClassifier("Book");

// 2. Build an EntityMappings model from the EClasses
EntityMappings mappings = new EntityMapper()
        .createMappings(List.of(authorCls, bookCls));

// 3. Configure the JDBC connection and JPA provider
DynamicClassLoader dcl = new DynamicClassLoader(
        Thread.currentThread().getContextClassLoader());

Map<String, Object> props = new HashMap<>();
props.put(PersistenceUnitProperties.DDL_GENERATION,       "create-or-extend-tables");
props.put(PersistenceUnitProperties.DDL_GENERATION_MODE,  "database");
props.put(PersistenceUnitProperties.JDBC_DRIVER,          "org.h2.Driver");
props.put(PersistenceUnitProperties.JDBC_URL,
          "jdbc:h2:mem:library_" + UUID.randomUUID());
props.put(PersistenceUnitProperties.JDBC_USER,            "sa");
props.put(PersistenceUnitProperties.JDBC_PASSWORD,        "");
props.put(PersistenceUnitProperties.TARGET_DATABASE,      "Auto");
props.put(PersistenceUnitProperties.TRANSACTION_TYPE,     "RESOURCE_LOCAL");
props.put(PersistenceUnitProperties.WEAVING,              "false");
props.put(PersistenceUnitProperties.CLASSLOADER,          dcl);

// 4. Boot EclipseLink and register the dynamic types
PersistenceUnit pu = EPersistenceFactory.eINSTANCE.createPersistenceUnit();
pu.setName("library");
pu.setProperties(EPersistenceFactory.eINSTANCE.createProperties());
EDynamicPersistenceUnitInfo pui = new EDynamicPersistenceUnitInfo(pu,
        MyApp.class.getProtectionDomain().getCodeSource().getLocation(), props);

EntityManagerFactory emf = new PersistenceProvider()
        .createContainerEntityManagerFactory(pui, props);
Server session = JpaHelper.getServerSession(emf);

ConverterService converter = new DefaultConverterService() { };
List<EDynamicType> types = new EDynamicTypeGenerator(dcl, session, "library", converter)
        .createFromMappings(List.of(mappings));
new EDynamicHelper(emf, dcl).addETypes(true, true, types);
```

After step 4 the database has `AUTHOR` and `BOOK` tables and the EclipseLink
session knows how to map the EClasses to them.

### A.2 — Wire the EMF Resource layer

```java
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;

ResourceSet persistenceRs = new ResourceSetImpl();
persistenceRs.getResourceFactoryRegistry()
        .getProtocolToFactoryMap()
        .put("jpa", new JPAResourceFactory(emf));
```

`persistenceRs` now resolves any URI of the form
`jpa://library/<EntityName>` through the `EntityManagerFactory` created above.
Jump to [CRUD through an EMF Resource](#crud-through-an-emf-resource) to read
and write entities.

The full working copy of this setup lives in
[`NonOsgiPersistenceTestBase`](../org.eclipse.fennec.persistence.test/test/org/eclipse/fennec/persistence/test/NonOsgiPersistenceTestBase.java)
— copy it verbatim as a starting point.

---

## Path B — OSGi end-to-end

In OSGi the `EntityManagerFactory` is produced by a configured
`EntityMappingPersistenceUnitConfigurator` (PID `fennec.jpa.EMPersistenceUnit`).
You provide three inputs as services and one as factory configuration.

### B.1 — Register a DataSource

Any `javax.sql.DataSource` OSGi service works. For H2 you can use the
standard `DataSourceFactory` config — this example uses a named data source
so the persistence unit can filter it:

```properties
# osgi.cm config — factory PID org.osgi.service.jdbc.DataSourceFactory
# filename: org.osgi.service.jdbc.DataSourceFactory~library.cfg
osgi.jdbc.driver.class=org.h2.Driver
url=jdbc:h2:mem:library
dataSourceName=libraryDs
```

### B.2 — Register the EntityMappings

An `EntityMappings` OSGi service is the contract — how it is produced is
your choice. There are two standard producers; pick the one that fits:

- **Auto-generate at startup** (`fennec.jpa.EORMMappingService`) — the
  mapping is derived from the `EPackage` every time the bundle starts.
- **Load a pre-serialized `.eorm` file** (`fennec.jpa.EORMLoader`) — you
  saved the mapping once, the runtime just reads it back.

The example below shows the pre-serialized variant; the auto-generate
variant and the trade-off between them are described in
[Mapping strategies](#mapping-strategies--generate-or-pre-serialize).

```properties
# filename: fennec.jpa.EORMLoader~library.cfg
fennec.jpa.eorm.model.target=(emf.name=library)
fennec.jpa.eorm.name=library
fennec.jpa.eorm.uri=file:/var/lib/myapp/library.eorm
```

- `fennec.jpa.eorm.model.target` — OSGi filter for the `EPackage` service
  representing the Ecore model. The library's `EPackage` must already be
  registered (e.g. via a generated `EPackageConfigurator`).
- `fennec.jpa.eorm.uri` — path to the serialised `EntityMappings` XMI file.
  You generate this once offline via `EntityMapper.createMappings(...)` and
  save the result.

After the configuration is applied, an `EntityMappings` service becomes
available with property `fennec.jpa.orm.mapping.name=library`.

### B.3 — Create the persistence unit

```properties
# filename: fennec.jpa.EMPersistenceUnit~library.cfg
# every configurator key carries the fennec.jpa. prefix
fennec.jpa.persistenceUnitName=library
fennec.jpa.batchWriting=JDBC
fennec.jpa.batchSize=500

# service filters — bind the right DataSource, EntityMappings, ConverterService.
# The mapping filter below matches the EORMLoader variant. If you use
# EORMMappingService (auto-generated), use (fennec.jpa.eorm.mapping=library) instead.
fennec.jpa.dataSource.target=(dataSourceName=libraryDs)
fennec.jpa.mapping.target=(fennec.jpa.orm.mapping.name=library)
fennec.jpa.converter.target=(component.name=org.eclipse.fennec.persistence.converter.DefaultConverterService)

# pass-through EclipseLink properties — any key prefixed with fennec.jpa.ext.
fennec.jpa.ext.eclipselink.ddl-generation=create-or-extend-tables
fennec.jpa.ext.eclipselink.logging.level=INFO
```

When this configuration is applied, the configurator registers an
`EntityManagerFactory` service with property `osgi.unit.name=library`.

The `fennec.jpa.` prefix is not optional: `persistenceUnitName` without it is
not seen, and activation fails with
`ConfigurationException: No persistence unit name was provided`.

See [`configuration-reference.md`](configuration-reference.md) for the full
OCD property list and forwarded EclipseLink keys.

### B.4 — Consume the EntityManagerFactory

```java
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.persistence.EntityManagerFactory;

@Component
public class LibraryService {

    private final ResourceSet rs = new ResourceSetImpl();

    @Activate
    public LibraryService(
            @Reference(target = "(osgi.unit.name=library)") EntityManagerFactory emf) {
        rs.getResourceFactoryRegistry()
                .getProtocolToFactoryMap()
                .put("jpa", new JPAResourceFactory(emf));
    }

    public void listAllBooks() throws java.io.IOException {
        Resource books = rs.createResource(URI.createURI("jpa://library/Book"));
        books.load(null);
        for (EObject b : books.getContents()) {
            // ...
        }
    }
}
```

The OSGi reference filter `(osgi.unit.name=library)` picks exactly the
`EntityManagerFactory` produced by the `fennec.jpa.EMPersistenceUnit~library`
configuration. From here the CRUD usage is identical to the Non-OSGi path.

### B.5 — Or let the repository facade do it

Building the `ResourceSet` yourself, as B.4 does, is the low-level route. In
OSGi you normally do not need it: add one more configuration and you get a
`Repository` service that reads and writes EObjects directly.

```properties
# filename: fennec.repository.jpa~library.cfg
# note: repository keys are unprefixed
repositoryId=library
unit.target=(osgi.unit.name=library)
readOnly=false
```

```java
import java.io.IOException;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.persistence.repository.api.Repository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component
public class LibraryService {

    @Reference(target = "(persistence.repository.id=library)")
    private Repository repository;

    public EObject findBook(EClass bookEClass, String id) throws IOException {
        return repository.getEObject(bookEClass, id);
    }
}
```

`readOnly=true` withholds the write interfaces from the service registry
entirely, so a read-only consumer cannot even bind them. Queries, prepared
queries and the full interface set are described in the
[Repository User Guide](repository-user-guide.md).

### B.6 — The whole chain without an .eorm file

Putting B.1–B.5 together, the shortest OSGi setup that never touches a
hand-written mapping file is four configurations:

```properties
# 1. the DataSource — see B.1
# filename: org.osgi.service.jdbc.DataSourceFactory~library.cfg
osgi.jdbc.driver.class=org.h2.Driver
url=jdbc:h2:mem:library
dataSourceName=libraryDs

# 2. derive the mapping from the registered EPackage
# filename: fennec.jpa.EORMMappingService~library.cfg
fennec.jpa.eorm.model.target=(emf.nsURI=http://example.org/library/1.0)
fennec.jpa.eorm.mappingName=library
# fennec.jpa.eorm.eClasses=Author,Book   # omit to map the whole EPackage

# 3. the persistence unit over that mapping
# filename: fennec.jpa.EMPersistenceUnit~library.cfg
fennec.jpa.persistenceUnitName=library
fennec.jpa.dataSource.target=(dataSourceName=libraryDs)
fennec.jpa.mapping.target=(fennec.jpa.eorm.mapping=library)
fennec.jpa.ext.eclipselink.ddl-generation=create-or-extend-tables

# 4. the repository over that unit
# filename: fennec.repository.jpa~library.cfg
repositoryId=library
unit.target=(osgi.unit.name=library)
readOnly=true
```

Watch the three property namespaces — they are different on purpose and are
the most common source of a silently missing service:

| Configuration | Key prefix | Service property it publishes |
|---------------|-----------|-------------------------------|
| `fennec.jpa.EORMMappingService` | `fennec.jpa.eorm.` | `fennec.jpa.eorm.mapping=<mappingName>` |
| `fennec.jpa.EORMLoader` | `fennec.jpa.eorm.` | `fennec.jpa.orm.mapping.name=<name>` |
| `fennec.jpa.EMPersistenceUnit` | `fennec.jpa.` | `osgi.unit.name=<persistenceUnitName>` |
| `fennec.repository.jpa` | *(none)* | `persistence.repository.id=<repositoryId>` |

Each step binds the previous one by the property in the right-hand column. If
a service never appears, `scr:list` in the Gogo shell tells you which
reference is unsatisfied.

---

## Mapping strategies — generate or pre-serialize

The `EntityMappings` (`.eorm`) model is what tells EclipseLink how each
EClass maps to a table. You have two ways to produce it, and they
interchange — `fennec.jpa.EMPersistenceUnit` doesn't care how the service
was registered, only that the filter resolves.

| When to choose | Strategy | Producer |
|----------------|----------|----------|
| Model still changing; you want the framework to re-derive the mapping on every start | **Auto-generate** | `EntityMapper` (non-OSGi) / `fennec.jpa.EORMMappingService` (OSGi) |
| Model is stable; you want a versionable artefact, startup savings, and/or hand-tuned overrides the generator would never emit | **Pre-serialize** | save once via `EORMResourceFactoryImpl`, reload via `EORMModelHelper` (non-OSGi) or `fennec.jpa.EORMLoader` (OSGi) |

Auto-generation is what Path A and the first half of Path B use. The
sections below cover pre-serialization — generate once, commit the `.eorm`
alongside the Ecore, and reuse it on every boot.

### Why pre-serialize?

- **Reproducibility.** A `.eorm` is plain XMI you can commit, diff, and
  review. Two starts of the same code with the same Ecore are guaranteed
  to mount the same database mapping.
- **Startup cost.** For large EPackages the generator walks every feature,
  ref, and opposite. A loaded file short-circuits that.
- **Hand tuning.** You can open the `.eorm` and adjust table names, column
  types, fetch modes, or discriminator strategies that the generator
  picked differently. The runtime does not care where the decision came
  from.
- **Audit/immutability.** Schema migrations become easier to reason about
  when the mapping is an artefact, not a derivation.

### Generate and save once (non-OSGi)

You run this once offline — a build step, a one-off main method, or a
migration task — and check the resulting file into your repo or drop it
into `/var/lib/myapp/`.

```java
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.eorm.util.EORMResourceFactoryImpl;
import org.eclipse.fennec.persistence.epersistence.EPersistencePackage;
import org.eclipse.fennec.persistence.orm.EntityMapper;

// 1. Generate in memory
EntityMappings mappings = new EntityMapper()
        .createMappings(List.of(authorCls, bookCls));

// 2. Prepare a ResourceSet that understands .eorm
ResourceSet rs = new ResourceSetImpl();
rs.getPackageRegistry().put(EcorePackage.eNS_URI,        EcorePackage.eINSTANCE);
rs.getPackageRegistry().put(EORMPackage.eNS_URI,         EORMPackage.eINSTANCE);
rs.getPackageRegistry().put(EPersistencePackage.eNS_URI, EPersistencePackage.eINSTANCE);
rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
        .put("*",    new XMIResourceFactoryImpl());
rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
        .put("eorm", new EORMResourceFactoryImpl());

// 3. Write the file
Resource out = rs.createResource(URI.createFileURI("library.eorm"));
out.getContents().add(mappings);
out.save(null);
```

`library.eorm` is now a plain XMI document. `xmlns:eorm` is
`https://eclipse.org/fennec/persistence/eorm/1.0.0`; the Ecore cross
references use relative `href`s that resolve against your Ecore file.

### Load and use the saved file (non-OSGi)

Replace the `EntityMapper.createMappings(...)` line in
[Path A.1](#path-a--non-osgi-end-to-end) with a load:

```java
import org.eclipse.fennec.persistence.orm.helper.EORMModelHelper;

ResourceSet rs = new ResourceSetImpl();
rs.getPackageRegistry().put(EcorePackage.eNS_URI,        EcorePackage.eINSTANCE);
rs.getPackageRegistry().put(EORMPackage.eNS_URI,         EORMPackage.eINSTANCE);
rs.getPackageRegistry().put(EPersistencePackage.eNS_URI, EPersistencePackage.eINSTANCE);
rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
        .put("eorm", new EORMResourceFactoryImpl());
rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
        .put("*",    new XMIResourceFactoryImpl());

EntityMappings mappings = new EORMModelHelper(rs)
        .loadMapping("file:/var/lib/myapp/library.eorm");
```

From here the rest of the bootstrap — `EDynamicTypeGenerator`,
`EDynamicHelper`, `JPAResourceFactory` — is identical to Path A.

### Auto-generation in OSGi

If you want the runtime to generate the mapping from a registered
`EPackage` at every start, use `fennec.jpa.EORMMappingService` instead of
`fennec.jpa.EORMLoader`:

```properties
# filename: fennec.jpa.EORMMappingService~library.cfg
fennec.jpa.eorm.model.target=(emf.name=library)
fennec.jpa.eorm.eClasses=Author,Book
fennec.jpa.eorm.mappingName=library
fennec.jpa.eorm.strict=false
```

**Every key carries the `fennec.jpa.eorm.` prefix** — the component declares it
as its object class definition prefix. An unprefixed `eClasses=…` is not an
error; it is simply ignored, and you end up with an empty mapping.

- `eClasses` — names of EClasses in the referenced `EPackage` to map. Omit the
  key to map every EClass of the package. A configured name that is not an
  EClass of that package (a typo, or an EEnum) is skipped with a warning in
  the log, not an activation failure.
- `strict` — when `true` the generator takes EClass/attribute names as
  authoritative and skips column-name guessing. Default `false`.

The registered `EntityMappings` service carries the property
`fennec.jpa.eorm.mapping=library`, so the `fennec.jpa.EMPersistenceUnit`
configuration from [Path B.3](#b3--create-the-persistence-unit) must use:

```properties
fennec.jpa.mapping.target=(fennec.jpa.eorm.mapping=library)
```

### Pre-serialized eorm in OSGi

Use `fennec.jpa.EORMLoader` (shown in
[Path B.2](#b2--register-the-entitymappings)). The filter
for `EMPersistenceUnit` is then:

```properties
fennec.jpa.mapping.target=(fennec.jpa.orm.mapping.name=library)
```

Note the different service property key — `fennec.jpa.orm.mapping.name`
for the loader, `fennec.jpa.eorm.mapping` for the auto-generator. That's
the only thing the downstream config cares about.

### Customising a generated mapping (OSGi)

If auto-generation is almost right but you need a small tweak, register an
`EORMMappingCustomizer` service. `fennec.jpa.EORMMappingService`
runs your customizer on the generated model before publishing the
`EntityMappings` service — useful for overriding one column name without
maintaining a full `.eorm` file.

You do not have to register one: the reference is satisfied by the built-in
`EmptyMappingCustomizer`. But it is a **mandatory static reference**, so a
`fennec.jpa.eorm.customizer.target` filter that matches nothing leaves the
component unsatisfied — no `EntityMappings` service appears, and nothing in the
log says why. If your mapping never shows up, check that filter first
(`scr:list` / `scr:info` in the Gogo shell shows the unsatisfied reference).

---

## CRUD through an EMF Resource

Both paths end with a `ResourceSet` that resolves `jpa://` URIs. The URI
scheme is:

```
jpa://<persistenceUnitName>/<EClassName>
```

Example for our model: `jpa://library/Book`, `jpa://library/Author`.

### Create

Build instances via the descriptor's instantiation policy so the class loader
produces EclipseLink-managed dynamic entities — not plain `DynamicEObjectImpl`:

```java
import jakarta.persistence.EntityManager;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.sessions.server.Server;

Server session = JpaHelper.getServerSession(emf);
ClassDescriptor authorDesc = session.getDescriptorForAlias("Author");
ClassDescriptor bookDesc   = session.getDescriptorForAlias("Book");

EObject tolkien = (EObject) authorDesc.getInstantiationPolicy().buildNewInstance();
tolkien.eSet(authorCls.getEStructuralFeature("name"), "J.R.R. Tolkien");

EObject hobbit = (EObject) bookDesc.getInstantiationPolicy().buildNewInstance();
hobbit.eSet(bookCls.getEStructuralFeature("title"),  "The Hobbit");
hobbit.eSet(bookCls.getEStructuralFeature("author"), tolkien);

try (EntityManager em = emf.createEntityManager()) {
    em.getTransaction().begin();
    em.persist(tolkien);
    em.persist(hobbit);
    em.getTransaction().commit();
}
```

### Read

```java
Resource books = rs.createResource(URI.createURI("jpa://library/Book"));
books.load(null);

for (EObject b : books.getContents()) {
    String title = (String) b.eGet(bookCls.getEStructuralFeature("title"));
    EObject a    = (EObject) b.eGet(bookCls.getEStructuralFeature("author"));
    // The author reference is a proxy until accessed.
    String name  = (String) a.eGet(authorCls.getEStructuralFeature("name"));
    System.out.println(title + " -- " + name);
}
```

### Update

```java
Resource books = rs.createResource(URI.createURI("jpa://library/Book"));
books.load(null);
EObject hobbit = books.getContents().get(0);
hobbit.eSet(bookCls.getEStructuralFeature("title"), "The Hobbit (revised)");
books.save(null);
```

`Resource.save()` performs an **upsert**: for every entity it calls
`em.find(type, id)` and copies the current state into the managed instance.
New entities are persisted; existing rows are updated.

### Pagination

Pass `Options.OPTION_PAGE_SIZE` to `load` to stream large result sets:

```java
import org.eclipse.fennec.persistence.Options;

Map<Object, Object> opts = new HashMap<>();
opts.put(Options.OPTION_PAGE_SIZE, 1000);
books.load(opts);
```

### Delete

```java
books.load(null);
books.delete(null); // removes all entities currently in contents
```

---

## How references are loaded (lazy by default)

Non-containment references are lazy. Loading `jpa://library/Book` fetches
exactly one `SELECT` against the `Book` table. The `author` slot on every
book is filled with an EMF proxy carrying the foreign-key value and an
`eProxyURI` of the form `jpa://library/Author#//author/id/42`.

The first time you call `book.eGet(author)`:

1. EMF notices `eIsProxy() == true`
2. It asks the containing `ResourceSet` to resolve the proxy URI
3. The `ResourceSet` routes to the `Author` JPA resource
4. `JPAResourceImpl.getEObject` parses the fragment and calls `em.find(Author.class, 42)`
5. The fully-loaded `Author` replaces the proxy in the field

Subsequent accesses are free — no further SQL runs.

### Many-valued references: proxy elements from an ID-only query

Many-valued non-containment references follow the same proxy contract, for
both storage layouts. Loading the owner issues one lightweight ID-only query:

- **Relation (join) table** (`ManyToMany`, unidirectional `OneToMany` with a
  join table): `SELECT target_id FROM join_table WHERE source_id = ?` — the
  target table is never touched.
- **Foreign key in the target table** (bidirectional `OneToMany`): `SELECT
  target_pk FROM target_table WHERE fk = ?` — only the primary-key column is
  read, **no target object is materialised**.

The list is filled with EMF proxies, one per id, each carrying the id
attribute and an `eProxyURI`:

```java
List<EObject> tags = (List<EObject>) book.eGet(bookCls.getEStructuralFeature("tags"));
tags.size();                    // relation-table ids only — no Tag row was read
EObject first = tags.get(0);    // iteration/access resolves each element on demand
```

Element access resolves each proxy individually through the `ResourceSet`
(one `em.find` per element on first touch). This is standard EMF behaviour:
non-containment references hold proxies until accessed — exactly like
cross-document references in XMI.

Two consequences worth knowing:

- Reading only the list size or the target ids (`EcoreUtil.getID(element)`)
  never materialises a target row.
- Writing an owner back (`resource.save()`) with an untouched proxy list
  leaves the targets and the relation table alone — a proxy always represents
  an existing row and is never re-persisted.

### Important: resolution requires a ResourceSet

Entities fetched directly with `em.find(Book.class, id)` are **not** loaded
through a `ResourceSet`. Their proxy references cannot be resolved by EMF's
standard machinery and `proxy.eGet(anyFeature)` will return `null`.

**Use the `Resource` API for any code that navigates non-containment
references** — `em.find` is a low-level escape hatch.

If you need to resolve a proxy explicitly (e.g. for references reached
through an EclipseLink `IndirectList`), use the standard EMF helper:

```java
EObject resolved = EcoreUtil.resolve((EObject) proxy, resourceSet);
```

### Containment references are still eager

Containment references follow EMF's composition semantics — the target is
materialised with its owner, not via a proxy. This is correct for ownership;
only association-style references (non-containment) are lazy.

---

## Error handling and diagnostics

`JPAResourceImpl` fulfils the EMF `Resource` contract: any failure in `load`,
`save`, `delete` or `count` is surfaced as a plain `java.io.IOException`
(with the originating `PersistenceException` as the cause).

It also populates EMF's standard diagnostic channels:

- `resource.getErrors()` — fatal problems (load/save/delete/count failures,
  `em.find` failures during proxy resolution).
- `resource.getWarnings()` — non-fatal anomalies (missing entity segment in
  URI, unknown descriptor, un-parseable id in a proxy fragment).

Both lists are cleared at the start of every `doLoad`/`doSave`/`delete` so
stale diagnostics from a previous call do not leak in.

```java
try {
    books.load(null);
} catch (IOException e) {
    // e is always an IOException — the cause is the underlying PersistenceException
    LOG.log(Level.SEVERE, "Load failed", e);

    // Richer per-problem detail is available here:
    books.getErrors().forEach(d ->
            LOG.severe(d.getMessage() + " at " + d.getLocation()));
}

books.getWarnings().forEach(d -> LOG.warning(d.getMessage()));
```

This matches the behaviour of EMF's own XMI resource and keeps JPA-backed
resources interchangeable with file-backed ones from the caller's point of
view.

---

## Next steps

- [`repository-user-guide.md`](repository-user-guide.md) — the repository facade in full: interfaces, queries, prepared queries, both flavours
- [`configuration-reference.md`](configuration-reference.md) — full property catalogue (both modes)
- `REVIEW.md` — structured review and status of every work package
- [`development-guide.md`](development-guide.md) — internals, session continuity, contribution notes
