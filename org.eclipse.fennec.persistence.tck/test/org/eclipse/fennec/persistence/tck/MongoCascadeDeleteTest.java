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

import java.util.UUID;

import org.bson.BsonDocument;
import org.bson.BsonString;
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * Cascade-delete of cross-document containment children on the mongo backend (issue #133).
 * <p>
 * Containment means lifecycle ownership, so dropping a containment subtree has to delete
 * what that subtree owned. For an embedded child that is automatic — it is physically part
 * of the root document. A cross-document containment child (emf.codec#123/#128) lives in
 * its own document in its own collection, and nothing in the write path deletes it.
 * <p>
 * The model deliberately nests two levels so the transitive case is covered:
 *
 * <pre>
 * Library                       own document in collection "Library"
 *  └─ section    containment    embedded in the Library document
 *      └─ archive containment   own Resource → own document in collection "Archive"
 * </pre>
 *
 * Removing {@code section} from the library is an ordinary root update; the archive is
 * owned transitively by the removed subtree and must go with it.
 *
 * @author Mark Hoffmann
 * @since 13.08.2026
 */
class MongoCascadeDeleteTest {

	private EPackage cascadePackage;
	private EClass libraryClass;
	private EClass sectionClass;
	private EClass archiveClass;
	private EReference librarySection;
	private EReference libraryAnnex;
	private EReference sectionArchive;

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
		metadataService.registerPackage(cascadePackage);
		client = MongoClients.create(connectionString);
		databaseName = "cascade_" + UUID.randomUUID().toString().replace("-", "");
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
		archiveClass = ecore.createEClass();
		archiveClass.setName("Archive");
		addId(archiveClass, "arid");
		addString(archiveClass, "label");

		sectionClass = ecore.createEClass();
		sectionClass.setName("Section");
		addId(sectionClass, "sid");
		addString(sectionClass, "title");
		sectionArchive = reference("archive", archiveClass, true, 1);
		sectionClass.getEStructuralFeatures().add(sectionArchive);

		libraryClass = ecore.createEClass();
		libraryClass.setName("Library");
		addId(libraryClass, "lid");
		addString(libraryClass, "name");
		librarySection = reference("section", sectionClass, true, 1);
		libraryClass.getEStructuralFeatures().add(librarySection);
		libraryAnnex = reference("annex", archiveClass, true, 1);
		libraryClass.getEStructuralFeatures().add(libraryAnnex);

		cascadePackage = ecore.createEPackage();
		cascadePackage.setName("cascade");
		cascadePackage.setNsURI("urn:cascade:test/1.0");
		cascadePackage.setNsPrefix("cascade");
		cascadePackage.getEClassifiers().add(libraryClass);
		cascadePackage.getEClassifiers().add(sectionClass);
		cascadePackage.getEClassifiers().add(archiveClass);
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
		resourceSet.getPackageRegistry().put(cascadePackage.getNsURI(), cascadePackage);
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

	private BsonDocument rawDocument(String collection, String id) {
		return database.getCollection(collection, BsonDocument.class)
				.find(new BsonDocument("_id", new BsonString(id))).first();
	}

	private long documentCount(String collection) {
		return database.getCollection(collection, BsonDocument.class).countDocuments();
	}

	private static Object value(EObject object, String feature) {
		return object.eGet(object.eClass().getEStructuralFeature(feature));
	}

	/**
	 * Builds {@code Library → section → archive} with the archive as a root of its own
	 * resource, and writes both documents. Returns the write ResourceSet so a caller can
	 * keep mutating the very objects that were saved.
	 */
	private ResourceSet saveNestedFixture() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject library = create(libraryClass, "lid", "l1", "name", "Central");
		EObject section = create(sectionClass, "sid", "s1", "title", "Rare Books");
		EObject archive = create(archiveClass, "arid", "a1", "label", "Vault");
		library.eSet(librarySection, section);
		section.eSet(sectionArchive, archive);

		Resource archiveResource = writeSet.createResource(uriFor("Archive"));
		archiveResource.getContents().add(archive);
		// the cross-document shape: owned by the section, resident in its own resource
		assertThat(((InternalEObject) archive).eDirectResource()).isSameAs(archiveResource);
		assertThat(archive.eContainer()).isSameAs(section);

		Resource libraryResource = writeSet.createResource(uriFor("Library"));
		libraryResource.getContents().add(library);

		archiveResource.save(null);
		libraryResource.save(null);
		return writeSet;
	}

	// -------------------------------------------------------------------- the shape

	/**
	 * The fixture itself, so the disabled cases below cannot be dismissed as a broken
	 * setup: the section really is embedded, the archive really is a separate document
	 * referenced by {@code $ref}, and the whole thing reloads.
	 */
	@Test
	void nestedCrossDocumentContainmentRoundTrips() throws Exception {
		saveNestedFixture();

		BsonDocument stored = rawDocument("Library", "l1");
		assertThat(stored.get("section").isDocument()).as("section is embedded").isTrue();
		BsonDocument storedSection = stored.get("section").asDocument();
		assertThat(storedSection.getString("title").getValue()).isEqualTo("Rare Books");
		assertThat(storedSection.get("archive").asDocument().containsKey("$ref"))
				.as("the grandchild is a reference marker, not an inlined copy")
				.isTrue();
		assertThat(documentCount("Archive")).as("grandchild has its own document").isEqualTo(1);

		ResourceSet readSet = resourceSet();
		Resource loaded = readSet.createResource(uriFor("Library"));
		loaded.load(null);
		EObject library = loaded.getContents().get(0);
		EObject section = (EObject) library.eGet(librarySection);
		assertThat(value(section, "sid")).isEqualTo("s1");
		EObject archive = (EObject) section.eGet(sectionArchive);
		assertThat(archive.eIsProxy()).isFalse();
		assertThat(value(archive, "label")).isEqualTo("Vault");
	}

	// ------------------------------------------------------------------ cascade delete

	/**
	 * The transitive case of issue #133: the archive is owned by the section, and the
	 * section is dropped from the library. Nothing else references the archive afterwards,
	 * so its document must go.
	 */
	@Test
	@Disabled("issue #133 — the dropped subtree's cross-document grandchild is left as an orphan document")
	void droppingIntermediateContainmentDeletesTheCrossDocumentGrandchild() throws Exception {
		ResourceSet writeSet = saveNestedFixture();
		assertThat(documentCount("Archive")).isEqualTo(1);

		Resource libraryResource = writeSet.getResource(uriFor("Library"), false);
		EObject library = libraryResource.getContents().get(0);
		library.eSet(librarySection, null);
		libraryResource.save(null);

		assertThat(rawDocument("Library", "l1").containsKey("section"))
				.as("the embedded subtree is gone from the root document")
				.isFalse();
		assertThat(documentCount("Archive"))
				.as("the transitively owned grandchild document must be deleted with the subtree")
				.isZero();
	}

	/**
	 * The flat variant: a cross-document containment child hanging directly off the root.
	 */
	@Test
	@Disabled("issue #133 — the dropped cross-document child is left as an orphan document")
	void droppingCrossDocumentContainmentChildDeletesItsDocument() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject library = create(libraryClass, "lid", "l2", "name", "Annex Holder");
		EObject archive = create(archiveClass, "arid", "a2", "label", "Annex");
		library.eSet(libraryAnnex, archive);

		Resource archiveResource = writeSet.createResource(uriFor("Archive"));
		archiveResource.getContents().add(archive);
		Resource libraryResource = writeSet.createResource(uriFor("Library"));
		libraryResource.getContents().add(library);
		archiveResource.save(null);
		libraryResource.save(null);
		assertThat(documentCount("Archive")).isEqualTo(1);

		library.eSet(libraryAnnex, null);
		libraryResource.save(null);

		assertThat(documentCount("Archive"))
				.as("the dropped containment child's document must be deleted")
				.isZero();
	}

	/**
	 * Deleting the root has to take the owned cross-document child with it too — the same
	 * ownership rule, triggered through {@code Resource.delete} rather than an update.
	 */
	@Test
	@Disabled("issue #133 — Resource.delete is scoped to its own collection and leaves the child document")
	void deletingTheRootDeletesTheCrossDocumentChildDocument() throws Exception {
		ResourceSet writeSet = saveNestedFixture();
		Resource libraryResource = writeSet.getResource(uriFor("Library"), false);
		libraryResource.delete(null);

		assertThat(documentCount("Library")).isZero();
		assertThat(documentCount("Archive"))
				.as("the owned grandchild document must be deleted with its root")
				.isZero();
	}
}
