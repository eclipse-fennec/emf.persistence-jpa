# Getting Started

This guide walks through a complete example: modelling a domain in Ecore,
persisting instances to a relational database with Fennec Persistence JPA,
and reading them back through the EMF Resource API.

## Contents

1. [Prerequisites](#prerequisites)
2. [The example domain](#the-example-domain)
3. [Bootstrap in Non-OSGi code](#bootstrap-in-non-osgi-code)
4. [Bootstrap via OSGi Declarative Services](#bootstrap-via-osgi-declarative-services)
5. [CRUD through an EMF Resource](#crud-through-an-emf-resource)
6. [How references are loaded (lazy by default)](#how-references-are-loaded-lazy-by-default)
7. [Next steps](#next-steps)

## Prerequisites

- Java 17 or newer
- A relational database (the examples use in-memory H2)
- An Ecore model
- For the OSGi path: a runtime with Declarative Services (Felix, Equinox, bnd-gradle)

## The example domain

Create `library.ecore` with two EClasses and a non-containment reference.
Every persistent EClass must have exactly one EID attribute — it becomes
the primary key column.

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

## Bootstrap in Non-OSGi code

This is the shortest path: a plain Java main method that builds an
`EntityManagerFactory` using Fennec's dynamic-type pipeline.

```java
import java.util.List;
import java.util.Map;
import java.util.HashMap;
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
import org.eclipse.fennec.persistence.eclipselink.resource.JPAResourceFactory;
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

public class LibraryExample {

    public static void main(String[] args) throws Exception {
        // 1. Load the Ecore model
        ResourceSet rs = new ResourceSetImpl();
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put("*", new XMIResourceFactoryImpl());
        Resource ecoreResource = rs.createResource(URI.createFileURI("library.ecore"));
        ecoreResource.load(null);
        EPackage pkg = (EPackage) ecoreResource.getContents().get(0);
        rs.getPackageRegistry().put(pkg.getNsURI(), pkg);

        EClass author = (EClass) pkg.getEClassifier("Author");
        EClass book = (EClass) pkg.getEClassifier("Book");

        // 2. Build an EntityMappings model from the EClasses
        EntityMapper mapper = new EntityMapper();
        EntityMappings mappings = mapper.createMappings(List.of(author, book));

        // 3. Configure H2 and create a PersistenceUnit
        DynamicClassLoader dcl = new DynamicClassLoader(LibraryExample.class.getClassLoader());
        Map<String, Object> props = new HashMap<>();
        props.put(PersistenceUnitProperties.DDL_GENERATION, "create-or-extend-tables");
        props.put(PersistenceUnitProperties.DDL_GENERATION_MODE, "database");
        props.put(PersistenceUnitProperties.JDBC_DRIVER, "org.h2.Driver");
        props.put(PersistenceUnitProperties.JDBC_URL, "jdbc:h2:mem:library_" + UUID.randomUUID());
        props.put(PersistenceUnitProperties.JDBC_USER, "sa");
        props.put(PersistenceUnitProperties.JDBC_PASSWORD, "");
        props.put(PersistenceUnitProperties.TARGET_DATABASE, "Auto");
        props.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");
        props.put(PersistenceUnitProperties.WEAVING, "false");
        props.put(PersistenceUnitProperties.CLASSLOADER, dcl);

        PersistenceUnit pu = EPersistenceFactory.eINSTANCE.createPersistenceUnit();
        pu.setName("library");
        pu.setProperties(EPersistenceFactory.eINSTANCE.createProperties());
        EDynamicPersistenceUnitInfo pui = new EDynamicPersistenceUnitInfo(pu,
                LibraryExample.class.getProtectionDomain().getCodeSource().getLocation(),
                props);

        // 4. Boot EclipseLink and register the dynamic types
        EntityManagerFactory emf = new PersistenceProvider()
                .createContainerEntityManagerFactory(pui, props);
        Server session = JpaHelper.getServerSession(emf);
        ConverterService converter = new DefaultConverterService() {};
        EDynamicTypeGenerator generator =
                new EDynamicTypeGenerator(dcl, session, "library", converter);
        List<EDynamicType> types = generator.createFromMappings(List.of(mappings));
        new EDynamicHelper(emf, dcl).addETypes(true, true, types);

        // 5. EMF-Resource setup
        ResourceSet persistenceRs = new ResourceSetImpl();
        persistenceRs.getResourceFactoryRegistry().getProtocolToFactoryMap()
                .put("jpa", new JPAResourceFactory(emf));

        // ... persist, query, etc. (see CRUD section below) ...

        emf.close();
    }
}
```

The full working copy of this setup — including the `bootstrapPersistence`
helper used in the test suite — lives in
[`NonOsgiPersistenceTestBase`](../org.eclipse.fennec.persistence.test/test/org/eclipse/fennec/persistence/test/NonOsgiPersistenceTestBase.java).
You can copy that class verbatim as a starting point.

## Bootstrap via OSGi Declarative Services

In an OSGi runtime the usual path is to let the `EntityMappingPersistenceUnitConfigurator`
or `PersistenceUnitConfigurator` component do the heavy lifting.

### Required services

- A `javax.sql.DataSource` registered with your database
- A `ConverterService` — the default `DefaultConverterService` is registered
  automatically
- An `EntityMappings` OSGi service (for `EntityMappingPersistenceUnitConfigurator`)
  or a persistence-unit file on disk (for `PersistenceUnitConfigurator`)

### Factory configuration

Create a factory configuration for the PID `fennec.jpa.EMPersistenceUnit`:

```properties
# .cfg file for fennec.jpa.EMPersistenceUnit~library
persistenceUnitName=library
batchWriting=JDBC
batchSize=500

# Any fennec.jpa.ext.* property is forwarded verbatim to EclipseLink.
fennec.jpa.ext.eclipselink.ddl-generation=create-or-extend-tables
fennec.jpa.ext.eclipselink.logging.level=INFO
```

See [`configuration-reference.md`](configuration-reference.md) for the full list of
OCD properties and forwarded EclipseLink keys.

### Consuming the EntityManagerFactory

```java
@Component
public class LibraryService {

    @Reference(target = "(osgi.unit.name=library)")
    EntityManagerFactory emf;

    public void readAll() throws Exception {
        ResourceSet rs = new ResourceSetImpl();
        rs.getResourceFactoryRegistry().getProtocolToFactoryMap()
                .put("jpa", new JPAResourceFactory(emf));
        Resource books = rs.createResource(URI.createURI("jpa://library/Book"));
        books.load(null);
        for (EObject b : books.getContents()) {
            // ...
        }
    }
}
```

## CRUD through an EMF Resource

Fennec exposes the database through EMF's standard `Resource` API with the
`jpa://` URI scheme:

```
jpa://<persistenceUnitName>/<EClassName>
```

Example for our model: `jpa://library/Book`, `jpa://library/Author`.

### Create

```java
// Build instances via the descriptor's instantiation policy so the class loader
// produces EclipseLink-managed dynamic entities — not plain DynamicEObjectImpl.
Server session = JpaHelper.getServerSession(emf);
ClassDescriptor authorDesc = session.getDescriptorForAlias("Author");
ClassDescriptor bookDesc = session.getDescriptorForAlias("Book");

EObject tolkien = (EObject) authorDesc.getInstantiationPolicy().buildNewInstance();
tolkien.eSet(author.getEStructuralFeature("name"), "J.R.R. Tolkien");

EObject hobbit = (EObject) bookDesc.getInstantiationPolicy().buildNewInstance();
hobbit.eSet(book.getEStructuralFeature("title"), "The Hobbit");
hobbit.eSet(book.getEStructuralFeature("author"), tolkien);

try (EntityManager em = emf.createEntityManager()) {
    em.getTransaction().begin();
    em.persist(tolkien);
    em.persist(hobbit);
    em.getTransaction().commit();
}
```

### Read

```java
ResourceSet rs = new ResourceSetImpl();
rs.getResourceFactoryRegistry().getProtocolToFactoryMap()
        .put("jpa", new JPAResourceFactory(emf));
Resource books = rs.createResource(URI.createURI("jpa://library/Book"));
books.load(null);

for (EObject b : books.getContents()) {
    String title = (String) b.eGet(book.getEStructuralFeature("title"));
    EObject a = (EObject) b.eGet(book.getEStructuralFeature("author"));
    // The author reference is a proxy until accessed.
    String name = (String) a.eGet(author.getEStructuralFeature("name"));
    System.out.println(title + " -- " + name);
}
```

### Update

```java
Resource books = rs.createResource(URI.createURI("jpa://library/Book"));
books.load(null);
EObject hobbit = books.getContents().get(0);
hobbit.eSet(book.getEStructuralFeature("title"), "The Hobbit (revised)");
books.save(null);
```

`Resource.save()` performs an **upsert**: it calls `em.find(type, id)` for
every entity in the resource contents and copies the current state into
the managed instance. New entities are persisted; existing rows are updated.

### Pagination

Pass `Options.OPTION_PAGE_SIZE` to `load` to stream large result sets in
fixed-size chunks instead of holding the full resultset in memory:

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

Subsequent accesses on the same reference are free — no further SQL runs.

### Important: resolution requires a ResourceSet

Entities fetched directly with `em.find(Book.class, id)` are **not** loaded
through a `ResourceSet`. Their proxy references cannot be resolved by EMF's
standard machinery and `proxy.eGet(anyFeature)` will return `null`.

**Use the `Resource` API for any code that navigates non-containment
references**, not `em.find` directly. The framework's positioning is
*"EMF Resource, backed by JPA"* — `em.find` is a low-level escape hatch.

If you need to resolve a proxy explicitly (e.g. for references reached
through an EclipseLink `IndirectList`), use the standard EMF helper:

```java
EObject resolved = EcoreUtil.resolve((EObject) proxy, resourceSet);
```

### Containment references are still eager

Containment references follow EMF's composition semantics — the target is
materialised with its owner, not via a proxy. This is the correct behaviour
for ownership; only association-style references (non-containment) are lazy.

## Next steps

- [`configuration-reference.md`](configuration-reference.md) — full property catalogue
- [`REVIEW.md`](REVIEW.md) — structured review and status of every work package
- [`development-guide.md`](development-guide.md) — internals, session continuity, contribution notes
- [`AP-46_Lazy-Loading-Analysis.md`](AP-46_Lazy-Loading-Analysis.md) — deep dive into the lazy-loading architecture
