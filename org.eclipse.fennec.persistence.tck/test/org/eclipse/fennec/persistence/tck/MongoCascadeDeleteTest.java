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
import org.eclipse.fennec.persistence.mongo.OwnershipMaintenance;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
	void deletingTheRootDeletesTheCrossDocumentChildDocument() throws Exception {
		ResourceSet writeSet = saveNestedFixture();
		Resource libraryResource = writeSet.getResource(uriFor("Library"), false);
		libraryResource.delete(null);

		assertThat(documentCount("Library")).isZero();
		assertThat(documentCount("Archive"))
				.as("the owned grandchild document must be deleted with its root")
				.isZero();
	}

	// ------------------------------------------------------------------ ownership edges

	/**
	 * The case that justifies ownership records over a read-before-write diff (issue #139):
	 * the child is <b>re-parented</b>, not dropped. The former owner's save must leave it
	 * alone.
	 * <p>
	 * A diff of the stored document would see "no longer mine" and delete a child that now
	 * belongs elsewhere. The record is keyed by the child, so the new owner's save rewrites the
	 * owner and the old owner simply no longer sees it as its own.
	 */
	@Test
	void reParentedChildSurvivesTheFormerOwnersSave() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject first = create(libraryClass, "lid", "l1", "name", "First");
		EObject second = create(libraryClass, "lid", "l2", "name", "Second");
		EObject archive = create(archiveClass, "arid", "a1", "label", "Vault");
		first.eSet(libraryAnnex, archive);

		Resource archiveResource = writeSet.createResource(uriFor("Archive"));
		archiveResource.getContents().add(archive);
		Resource libraryResource = writeSet.createResource(uriFor("Library"));
		libraryResource.getContents().add(first);
		libraryResource.getContents().add(second);
		libraryResource.save(null);
		archiveResource.save(null);
		assertThat(documentCount("Archive")).isEqualTo(1);

		// hand the archive over: EMF moves it, so first.annex becomes empty by itself
		second.eSet(libraryAnnex, archive);
		assertThat(first.eGet(libraryAnnex)).as("EMF moved the child").isNull();
		libraryResource.save(null);

		assertThat(documentCount("Archive"))
				.as("re-parented, not orphaned — the new owner keeps it alive")
				.isEqualTo(1);

		ResourceSet readSet = resourceSet();
		Resource loaded = readSet.createResource(uriFor("Library"));
		loaded.load(null);
		EObject reloadedSecond = loaded.getEObject("l2");
		EObject annex = (EObject) reloadedSecond.eGet(libraryAnnex);
		assertThat(annex).as("the child now hangs off the second library").isNotNull();
		assertThat(value(annex, "label")).isEqualTo("Vault");
	}

	/**
	 * The cost guard: a root whose type cannot own containment at all must not touch the
	 * ownership bookkeeping. Verified through the wire, since the collection would otherwise be
	 * created lazily and silently on every save.
	 */
	@Test
	void aTypeWithoutContainmentNeverTouchesTheOwnershipCollection() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject archive = create(archiveClass, "arid", "solo", "label", "Standalone");
		Resource archiveResource = writeSet.createResource(uriFor("Archive"));
		archiveResource.getContents().add(archive);
		archiveResource.save(null);

		assertThat(database.listCollectionNames())
				.as("Archive owns no containment — no ownership bookkeeping may appear")
				.doesNotContain("_fennec_ownership");
	}

	/**
	 * The other re-parenting direction: the hand-over is saved <em>before</em> the former owner
	 * is. This one is correct by construction rather than by the union above — the new owner's
	 * save rewrote the record, so the former owner's reconciliation queries by its own id and
	 * does not see the child at all.
	 */
	@Test
	void reParentedChildSurvivesWhenTheNewOwnerIsSavedFirst() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject first = create(libraryClass, "lid", "l1", "name", "First");
		EObject archive = create(archiveClass, "arid", "a1", "label", "Vault");
		first.eSet(libraryAnnex, archive);
		Resource archiveResource = writeSet.createResource(uriFor("Archive"));
		archiveResource.getContents().add(archive);
		Resource firstResource = writeSet.createResource(uriFor("Library"));
		firstResource.getContents().add(first);
		firstResource.save(null);
		archiveResource.save(null);

		// second library in its OWN resource, saved first after taking the child over
		ResourceSet secondSet = resourceSet();
		Resource secondResource = secondSet.createResource(uriFor("Library"));
		secondResource.load(null);
		EObject second = create(libraryClass, "lid", "l2", "name", "Second");
		EObject movedArchive = create(archiveClass, "arid", "a1", "label", "Vault");
		second.eSet(libraryAnnex, movedArchive);
		Resource movedArchiveResource = secondSet.createResource(uriFor("Archive"));
		movedArchiveResource.getContents().add(movedArchive);
		secondResource.getContents().add(second);
		secondResource.save(null);
		assertThat(documentCount("Archive")).isEqualTo(1);

		// now the former owner drops it and saves — the record already names l2 as owner
		first.eSet(libraryAnnex, null);
		firstResource.save(null);

		assertThat(documentCount("Archive"))
				.as("the record's owner is already the new one, so the old save must not delete")
				.isEqualTo(1);
	}

	// ------------------------------------------------------------------ sweep (#140)

	/**
	 * The crash window where transactions are unavailable: the root was written (or removed)
	 * but the child deletion never ran. Simulated with the raw driver, so the interruption is
	 * real rather than mocked — the resource layer is not asked to leave an inconsistency
	 * behind, the store is simply put into the state a crash would leave.
	 * <p>
	 * Owner still there but no longer referencing the child.
	 */
	@Test
	void sweepReclaimsAChildTheOwnerNoLongerReferences() throws Exception {
		saveNestedFixture();
		assertThat(documentCount("Archive")).isEqualTo(1);

		// the state a crash between the root write and the child delete leaves: the library
		// document no longer carries the section subtree, the archive document is still there
		database.getCollection("Library", BsonDocument.class).updateOne(
				new BsonDocument("_id", new BsonString("l1")),
				new BsonDocument("$unset", new BsonDocument("section", new BsonString(""))));
		assertThat(documentCount("Archive")).as("orphan present, as after a crash").isEqualTo(1);

		ResourceSet set = resourceSet();
		Resource resource = set.createResource(uriFor("Library"));
		long reclaimed = ((OwnershipMaintenance) resource).sweepOwnership();

		assertThat(reclaimed).isEqualTo(1);
		assertThat(documentCount("Archive")).as("the orphan is reclaimed").isZero();
		assertThat(database.getCollection("_fennec_ownership", BsonDocument.class).countDocuments())
				.as("its bookkeeping goes with it")
				.isZero();
	}

	/** The other shape: the owner document is gone entirely. */
	@Test
	void sweepReclaimsChildrenOfAVanishedOwner() throws Exception {
		saveNestedFixture();
		database.getCollection("Library", BsonDocument.class)
				.deleteOne(new BsonDocument("_id", new BsonString("l1")));
		assertThat(documentCount("Archive")).isEqualTo(1);

		ResourceSet set = resourceSet();
		Resource resource = set.createResource(uriFor("Library"));
		long reclaimed = ((OwnershipMaintenance) resource).sweepOwnership();

		assertThat(reclaimed).isEqualTo(1);
		assertThat(documentCount("Archive")).isZero();
	}

	/**
	 * Idempotence, and the guarantee that matters most: a sweep must never touch a child that
	 * is still owned. Running it on a healthy store has to be a no-op, twice.
	 */
	@Test
	void sweepLeavesAHealthyStoreAlone() throws Exception {
		saveNestedFixture();

		ResourceSet set = resourceSet();
		Resource resource = set.createResource(uriFor("Library"));
		assertThat(((OwnershipMaintenance) resource).sweepOwnership()).isZero();
		assertThat(((OwnershipMaintenance) resource).sweepOwnership()).isZero();

		assertThat(documentCount("Archive")).as("the owned child is untouched").isEqualTo(1);
		assertThat(documentCount("Library")).isEqualTo(1);
	}
}
