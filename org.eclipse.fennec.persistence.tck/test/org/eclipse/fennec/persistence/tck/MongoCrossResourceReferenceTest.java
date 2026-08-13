/********************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 ********************************************************************/
package org.eclipse.fennec.persistence.tck;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bson.BsonDocument;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.emf.osgi.metadata.MetadataServices;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.persistence.mongo.MongoResourceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * Cross-resource reference round trips on the mongo backend (issue #116) — the
 * emf.codec {@code CrossResourceReferenceTest} matrix (emf.codec#113/#123/#124)
 * adapted to Mongo. Every load runs against a FRESH {@link ResourceSet}: with the
 * saving one the target is already in memory and nothing really resolves.
 *
 * @author Mark Hoffmann
 * @since 06.08.2026
 */
class MongoCrossResourceReferenceTest {

	private EPackage xrefPackage;
	private EClass authorClass;
	private EClass bookClass;
	private EClass chapterClass;
	private EReference bookAuthor;
	private EReference bookRelated;
	private EReference bookSequel;
	private EReference bookChapters;
	private EReference bookAppendix;
	private EReference authorFavorite;

	private MongoClient client;
	private MongoDatabase database;
	private MetadataWhiteboard metadataService;
	private String databaseName;

	@BeforeEach
	void setUp() {
		String connectionString = MongoTestSupport.connectionString();
		assumeTrue(nonNull(connectionString),
				MongoTestSupport.unavailableMessage());
		buildModel();
		metadataService = MetadataServices.createWhiteboard();
		metadataService.registerPackage(xrefPackage);
		client = MongoClients.create(connectionString);
		databaseName = "xref_" + UUID.randomUUID().toString().replace("-", "");
		database = client.getDatabase(databaseName);
	}

	@AfterEach
	void tearDown() {
		if (nonNull(database)) {
			database.drop();
		}
		if (nonNull(client)) {
			client.close();
		}
	}

	private void buildModel() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		authorClass = ecore.createEClass();
		authorClass.setName("Author");
		addId(authorClass, "aid");
		addString(authorClass, "name");

		chapterClass = ecore.createEClass();
		chapterClass.setName("Chapter");
		addId(chapterClass, "cid");
		addString(chapterClass, "title");

		bookClass = ecore.createEClass();
		bookClass.setName("Book");
		addId(bookClass, "bid");
		addString(bookClass, "title");
		bookAuthor = reference("author", authorClass, false, 1);
		bookClass.getEStructuralFeatures().add(bookAuthor);
		bookRelated = reference("related", bookClass, false, -1);
		bookClass.getEStructuralFeatures().add(bookRelated);
		bookSequel = reference("sequel", bookClass, false, 1);
		bookClass.getEStructuralFeatures().add(bookSequel);
		bookChapters = reference("chapters", chapterClass, true, -1);
		bookClass.getEStructuralFeatures().add(bookChapters);
		bookAppendix = reference("appendix", chapterClass, true, 1);
		bookClass.getEStructuralFeatures().add(bookAppendix);

		authorFavorite = reference("favorite", chapterClass, false, 1);
		authorClass.getEStructuralFeatures().add(authorFavorite);

		xrefPackage = ecore.createEPackage();
		xrefPackage.setName("xref");
		xrefPackage.setNsURI("urn:xref:test/1.0");
		xrefPackage.setNsPrefix("xref");
		xrefPackage.getEClassifiers().add(authorClass);
		xrefPackage.getEClassifiers().add(bookClass);
		xrefPackage.getEClassifiers().add(chapterClass);
	}

	private static void addId(EClass eClass, String name) {
		EAttribute id = EcoreFactory.eINSTANCE.createEAttribute();
		id.setName(name);
		id.setEType(EcorePackage.Literals.ESTRING);
		id.setID(true);
		eClass.getEStructuralFeatures().add(id);
	}

	private static void addString(EClass eClass, String name) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(EcorePackage.Literals.ESTRING);
		eClass.getEStructuralFeatures().add(attribute);
	}

	private static EReference reference(String name, EClass type, boolean containment, int upper) {
		EReference reference = EcoreFactory.eINSTANCE.createEReference();
		reference.setName(name);
		reference.setEType(type);
		reference.setContainment(containment);
		reference.setUpperBound(upper);
		return reference;
	}

	// ------------------------------------------------------------------ helpers

	private ResourceSet resourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(xrefPackage.getNsURI(), xrefPackage);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("mongodb", new MongoResourceFactory(database, metadataService, null, null, client));
		return resourceSet;
	}

	private URI uriFor(String typeName) {
		return URI.createURI("mongodb://" + databaseName + "/" + typeName);
	}

	private EObject create(EClass eClass, String idFeature, String id, String nameFeature, String value) {
		EObject object = EcoreUtil.create(eClass);
		object.eSet(eClass.getEStructuralFeature(idFeature), id);
		object.eSet(eClass.getEStructuralFeature(nameFeature), value);
		return object;
	}

	private EObject author(String id, String name) {
		return create(authorClass, "aid", id, "name", name);
	}

	private EObject book(String id, String title) {
		return create(bookClass, "bid", id, "title", title);
	}

	private EObject chapter(String id, String title) {
		return create(chapterClass, "cid", id, "title", title);
	}

	private void save(ResourceSet resourceSet, String typeName, EObject... objects) throws Exception {
		Resource resource = resourceSet.createResource(uriFor(typeName));
		for (EObject object : objects) {
			resource.getContents().add(object);
		}
		resource.save(null);
	}

	private Resource load(ResourceSet resourceSet, String typeName) throws Exception {
		Resource resource = resourceSet.createResource(uriFor(typeName));
		resource.load(null);
		return resource;
	}

	private BsonDocument rawDocument(String collection, String id) {
		return database.getCollection(collection, BsonDocument.class)
				.find(new BsonDocument("_id", new org.bson.BsonString(id))).first();
	}

	private static Object value(EObject object, String feature) {
		return object.eGet(object.eClass().getEStructuralFeature(feature));
	}

	/** The stored reference target — the codec writes {@code {$ref: <uri>}} documents. */
	private static String refString(org.bson.BsonValue stored) {
		return stored.isDocument()
				? stored.asDocument().getString("$ref").getValue()
				: stored.asString().getValue();
	}

	// -------------------------------------------------------------------- matrix

	@Test
	void crossResourceReferenceStoresTheTargetUriAndResolves() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject tolkien = author("a1", "Tolkien");
		EObject hobbit = book("b1", "The Hobbit");
		hobbit.eSet(bookAuthor, tolkien);
		save(writeSet, "Author", tolkien);
		save(writeSet, "Book", hobbit);

		// the stored URI must name the target resource, not a document-internal
		// fragment (the emf.codec#113 failure mode) — the codec writes it deresolved
		// against the source resource, so resolve it back before comparing
		String stored = refString(rawDocument("Book", "b1").get("author"));
		URI resolved = URI.createURI(stored).resolve(uriFor("Book"));
		assertThat(resolved).isEqualTo(URI.createURI("mongodb://" + databaseName + "/Author#a1"));

		ResourceSet readSet = resourceSet();
		EObject loaded = load(readSet, "Book").getContents().get(0);
		EObject resolvedAuthor = (EObject) loaded.eGet(bookAuthor);
		assertThat(resolvedAuthor.eIsProxy()).isFalse();
		assertThat(value(resolvedAuthor, "aid")).isEqualTo("a1");
		assertThat(value(resolvedAuthor, "name")).isEqualTo("Tolkien");
	}

	@Test
	void deepContainmentTargetResolvesByIdentityNotPosition() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject book = book("b1", "The Hobbit");
		EObject decoy = chapter("c1", "An Unexpected Party");
		EObject target = chapter("c2", "Roast Mutton");
		@SuppressWarnings("unchecked")
		List<EObject> chapters = (List<EObject>) book.eGet(bookChapters);
		chapters.add(decoy);
		chapters.add(target);
		EObject reader = author("a1", "Reader");
		reader.eSet(authorFavorite, target);
		save(writeSet, "Book", book);
		save(writeSet, "Author", reader);

		ResourceSet readSet = resourceSet();
		EObject loadedReader = load(readSet, "Author").getContents().get(0);
		EObject favorite = (EObject) loadedReader.eGet(authorFavorite);
		// identity, not position: the wrong-but-plausible answer is chapters[0]
		assertThat(favorite.eIsProxy()).isFalse();
		assertThat(value(favorite, "cid")).isEqualTo("c2");
		assertThat(value(favorite, "title")).isEqualTo("Roast Mutton");
		assertThat(favorite.eContainer()).isNotNull();
		assertThat(value(favorite.eContainer(), "bid")).isEqualTo("b1");
	}

	@Test
	void sameResourceReferenceStaysResourceInternal() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject first = book("b1", "The Fellowship");
		EObject second = book("b2", "The Two Towers");
		first.eSet(bookSequel, second);
		save(writeSet, "Book", first, second);

		// resource-internal: the plain id fragment, no absolute URI
		String stored = refString(rawDocument("Book", "b1").get("sequel"));
		assertThat(stored).doesNotStartWith("mongodb://");

		ResourceSet readSet = resourceSet();
		Resource loaded = load(readSet, "Book");
		EObject loadedFirst = loaded.getEObject("b1");
		EObject sequel = (EObject) loadedFirst.eGet(bookSequel);
		assertThat(value(sequel, "bid")).isEqualTo("b2");
		// the very object of this resource, not a re-decoded twin
		assertThat(sequel).isSameAs(loaded.getEObject("b2"));
	}

	@Test
	void multiValuedReferencePreservesOrderAndIdentity() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject first = book("b1", "One");
		EObject second = book("b2", "Two");
		EObject third = book("b3", "Three");
		@SuppressWarnings("unchecked")
		List<EObject> related = (List<EObject>) first.eGet(bookRelated);
		related.add(third); // deliberately not id order
		related.add(second);
		save(writeSet, "Book", first, second, third);

		ResourceSet readSet = resourceSet();
		Resource loaded = load(readSet, "Book");
		@SuppressWarnings("unchecked")
		List<EObject> loadedRelated = (List<EObject>) loaded.getEObject("b1").eGet(bookRelated);
		assertThat(loadedRelated).extracting(object -> value(object, "bid"))
				.containsExactly("b3", "b2");
		assertThat(loadedRelated.get(0)).isSameAs(loaded.getEObject("b3"));
		assertThat(loadedRelated.get(1)).isSameAs(loaded.getEObject("b2"));
	}

	@Test
	void unresolvedProxySurvivesTheRoundTripWithItsUri() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject book = book("b1", "Ghost Story");
		EObject ghost = EcoreUtil.create(authorClass);
		URI ghostUri = URI.createURI("mongodb://" + databaseName + "/Author#ghost");
		((InternalEObject) ghost).eSetProxyURI(ghostUri);
		book.eSet(bookAuthor, ghost);
		save(writeSet, "Book", book);

		ResourceSet readSet = resourceSet();
		EObject loaded = load(readSet, "Book").getContents().get(0);
		Object rawAuthor = ((InternalEObject) loaded).eGet(bookAuthor, false);
		assertThat(rawAuthor).isInstanceOf(EObject.class);
		EObject proxy = (EObject) rawAuthor;
		assertThat(proxy.eIsProxy()).isTrue();
		assertThat(((InternalEObject) proxy).eProxyURI()).isEqualTo(ghostUri);
	}

	@Test
	void crossDocumentContainmentResolvesToTheContainedChild() throws Exception {
		// the child is contained by the book AND a root of its own resource
		// (eDirectResource) — the emf.codec#123 shape; pinned as SUPPORTED.
		// The $ref marker and the proxy behind it are storage internals: what the contract
		// requires is that eGet hands back the resolved child, never a proxy.
		ResourceSet writeSet = resourceSet();
		EObject book = book("b1", "Anthology");
		EObject chapter = chapter("c1", "Standalone");
		book.eSet(bookAppendix, chapter);
		Resource chapterResource = writeSet.createResource(uriFor("Chapter"));
		chapterResource.getContents().add(chapter);
		assertThat(((InternalEObject) chapter).eDirectResource()).isSameAs(chapterResource);
		assertThat(chapter.eContainer()).isSameAs(book);
		chapterResource.save(null);
		save(writeSet, "Book", book);

		// stored as a reference marker, not an inlined value copy
		assertThat(rawDocument("Book", "b1").get("appendix").isDocument()).isTrue();
		assertThat(rawDocument("Book", "b1").get("appendix").asDocument().containsKey("$ref")).isTrue();

		ResourceSet readSet = resourceSet();
		EObject loadedBook = load(readSet, "Book").getContents().get(0);

		// The fetch is lazy — until something resolves it, the raw slot holds a proxy.
		// That is implementation freedom and deliberately not asserted: what must hold is
		// that no consumer-facing API ever hands out an unresolved object. Generic tree
		// walks are what would leak it, since EcoreUtil.copy, validation and serialization
		// all traverse eContents.
		assertThat(loadedBook.eContents())
				.as("eContents resolves instead of exposing the proxy")
				.allSatisfy(child -> assertThat(child.eIsProxy()).isFalse())
				.extracting(child -> child.eClass().getName())
				.containsExactly("Chapter");
		Iterator<EObject> all = EcoreUtil.getAllContents(loadedBook, false);
		while (all.hasNext()) {
			assertThat(all.next().eIsProxy()).as("getAllContents resolves too").isFalse();
		}

		EObject resolved = (EObject) loadedBook.eGet(bookAppendix);
		// resolved via its own resource — a value copy or empty object fails here
		assertThat(resolved.eIsProxy()).isFalse();
		assertThat(value(resolved, "cid")).isEqualTo("c1");
		assertThat(value(resolved, "title")).isEqualTo("Standalone");
		// the full cross-document shape, restored: owned by the book, resident in its own
		// resource. This is the part a backend can get wrong while still returning correct
		// data — collapsing the child into the parent's resource loses its residency.
		assertThat(resolved.eContainer()).as("still owned by the book").isSameAs(loadedBook);
		assertThat(resolved.eResource()).as("resident in its own resource").isNotNull();
		assertThat(resolved.eResource().getURI()).isEqualTo(uriFor("Chapter"));
	}

	/** The multi-valued analogue — the {@code $ref} check per array element (emf.codec#128). */
	@Test
	void crossDocumentContainmentInAManyValuedReference() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject book = book("b1", "Anthology");
		EObject chapter = chapter("c1", "Standalone");
		@SuppressWarnings("unchecked")
		List<EObject> chapters = (List<EObject>) book.eGet(bookChapters);
		chapters.add(chapter);
		Resource chapterResource = writeSet.createResource(uriFor("Chapter"));
		chapterResource.getContents().add(chapter);
		chapterResource.save(null);
		save(writeSet, "Book", book);

		ResourceSet readSet = resourceSet();
		EObject loadedBook = load(readSet, "Book").getContents().get(0);
		@SuppressWarnings("unchecked")
		List<EObject> loadedChapters = (List<EObject>) loadedBook.eGet(bookChapters);
		assertThat(loadedChapters).hasSize(1);
		EObject resolved = loadedChapters.get(0);
		assertThat(resolved.eIsProxy()).isFalse();
		assertThat(value(resolved, "cid")).isEqualTo("c1");
		assertThat(value(resolved, "title")).isEqualTo("Standalone");
	}
}
