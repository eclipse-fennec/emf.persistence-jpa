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

import java.util.ArrayList;
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
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.emf.osgi.metadata.MetadataServices;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.model.command.CommandFactory;
import org.eclipse.fennec.model.command.DeleteCommand;
import org.eclipse.fennec.model.query.builder.Expressions;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.mongo.MongoResourceFactory;
import org.eclipse.fennec.persistence.query.api.CommandResource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.event.CommandListener;
import com.mongodb.event.CommandStartedEvent;

/**
 * Measures what issue #137 asks: the I/O cost of discovering a root's owned children by
 * walking {@code eContents()}, which is the mechanism {@code JPAResourceImpl
 * .removeChildrenFirst} already uses and the cheapest candidate fix for the delete half of
 * #133.
 * <p>
 * The walk is free for embedded children — they are materialised with the root document —
 * but a cross-document child sits in its own document, so resolving it is a query the
 * delete would otherwise never issue. The question is the shape of that cost, and the
 * answer decides whether the delete path can be fixed standalone or has to wait for the
 * ownership records of #139.
 * <p>
 * Result, counter to the expectation in #137: the cost is <b>O(distinct child collections)</b>
 * and constant in the child count, because resolving the first cross-document child
 * demand-loads its whole resource and the rest are then found in memory. The price sits in the
 * data volume instead — that load is unfiltered, which is its own defect (#146).
 * <p>
 * Counting happens on the driver itself through a {@link CommandListener}, so it measures
 * what actually goes over the wire rather than what the code appears to do.
 *
 * @author Mark Hoffmann
 * @since 13.08.2026
 */
class MongoDeleteWalkCostTest {

	/** The pathological fan-out — kept modest so the case stays in the normal build. */
	private static final int MANY = 20;

	private EPackage cascadePackage;
	private EClass libraryClass;
	private EClass sectionClass;
	private EClass archiveClass;
	private EReference librarySections;
	private EReference sectionArchive;

	private MongoClient client;
	private MongoDatabase database;
	private MetadataWhiteboard metadataService;
	private String databaseName;
	/** One entry per {@code find} that went over the wire: collection and filter shape. */
	private final List<String> wireFinds = new ArrayList<>();

	@BeforeEach
	void setUp() {
		String connectionString = MongoTestSupport.connectionString();
		assumeTrue(nonNull(connectionString), MongoTestSupport.unavailableMessage());
		buildModel();
		metadataService = MetadataServices.createWhiteboard();
		metadataService.registerPackage(cascadePackage);
		client = MongoClients.create(MongoClientSettings.builder()
				.applyConnectionString(new ConnectionString(connectionString))
				.addCommandListener(new CommandListener() {
					@Override
					public void commandStarted(CommandStartedEvent event) {
						if (!"find".equals(event.getCommandName())) {
							return;
						}
						BsonDocument command = event.getCommand();
						BsonDocument filter = command.getDocument("filter", new BsonDocument());
						synchronized (wireFinds) {
							wireFinds.add(command.getString("find").getValue()
									+ (filter.isEmpty() ? " UNFILTERED" : " filter=" + filter.toJson()));
						}
					}
				})
				.build());
		databaseName = "walkcost_" + UUID.randomUUID().toString().replace("-", "");
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
		sectionArchive = reference("archive", archiveClass, 1);
		sectionClass.getEStructuralFeatures().add(sectionArchive);

		libraryClass = ecore.createEClass();
		libraryClass.setName("Library");
		addId(libraryClass, "lid");
		addString(libraryClass, "name");
		librarySections = reference("sections", sectionClass, -1);
		libraryClass.getEStructuralFeatures().add(librarySections);

		cascadePackage = ecore.createEPackage();
		cascadePackage.setName("walkcost");
		cascadePackage.setNsURI("urn:walkcost:test/1.0");
		cascadePackage.setNsPrefix("wc");
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

	private static EReference reference(String name, EClass type, int upper) {
		EReference reference = EcoreFactory.eINSTANCE.createEReference();
		reference.setName(name);
		reference.setEType(type);
		reference.setContainment(true);
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

	@SuppressWarnings("unchecked")
	private List<EObject> sectionsOf(EObject library) {
		return (List<EObject>) library.eGet(librarySections);
	}

	/**
	 * Builds one library with {@code sections} embedded children; {@code crossDocument} of
	 * them additionally own an archive that is a root of its own resource.
	 */
	private void saveFixture(int sections, int crossDocument) throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject library = create(libraryClass, "lid", "l1", "name", "Central");
		Resource archiveResource = writeSet.createResource(uriFor("Archive"));
		for (int i = 0; i < sections; i++) {
			EObject section = create(sectionClass, "sid", "s" + i, "title", "Section " + i);
			sectionsOf(library).add(section);
			if (i < crossDocument) {
				EObject archive = create(archiveClass, "arid", "a" + i, "label", "Vault " + i);
				section.eSet(sectionArchive, archive);
				archiveResource.getContents().add(archive);
			}
		}
		Resource libraryResource = writeSet.createResource(uriFor("Library"));
		libraryResource.getContents().add(library);
		libraryResource.save(null);
		if (!archiveResource.getContents().isEmpty()) {
			archiveResource.save(null);
		}
	}

	/**
	 * The walk under measurement: exactly what a delete would have to do to discover the
	 * owned tree — the same shape as {@code JPAResourceImpl.removeChildrenFirst}, which
	 * recurses over {@code eContents()}.
	 *
	 * @return the objects found, and the number of {@code find} commands it took
	 */
	private WalkResult walk(EObject root) {
		synchronized (wireFinds) {
			wireFinds.clear();
		}
		List<EObject> found = new ArrayList<>();
		collect(root, found);
		synchronized (wireFinds) {
			return new WalkResult(found, new ArrayList<>(wireFinds));
		}
	}

	private void collect(EObject object, List<EObject> found) {
		for (EObject child : object.eContents()) {
			found.add(child);
			collect(child, found);
		}
	}

	private record WalkResult(List<EObject> found, List<String> finds) {
	}

	private EObject loadRoot() throws Exception {
		ResourceSet readSet = resourceSet();
		Resource resource = readSet.createResource(uriFor("Library"));
		resource.load(null);
		return resource.getContents().get(0);
	}

	// -------------------------------------------------------------------- the cases

	/**
	 * The common case: everything embedded. The walk must be pure in-memory traversal — if it
	 * cost a query here, the approach would be off the table, since every delete of an
	 * ordinary tree would pay for it.
	 */
	@Test
	void embeddedOnlyTreeCostsNoQueries() throws Exception {
		saveFixture(5, 0);
		EObject root = loadRoot();

		WalkResult result = walk(root);

		System.out.printf("### embedded-only(5 sections): found=%d finds=%s%n",
				result.found().size(), result.finds());
		assertThat(result.found()).as("all five embedded children discovered").hasSize(5);
		assertThat(result.finds())
				.as("embedded children come with the root document — the walk must not query")
				.isEmpty();
	}

	/**
	 * One cross-document child under an embedded child — the nested shape of #133. The walk
	 * discovers it, which a delete scoped to its own collection never would, and pays exactly
	 * one keyed query for it.
	 */
	@Test
	void oneCrossDocumentChildCostsOneKeyedQuery() throws Exception {
		saveFixture(5, 1);
		EObject root = loadRoot();

		WalkResult result = walk(root);

		System.out.printf("### one-cross-document(5 sections, 1 archive): found=%d finds=%s%n",
				result.found().size(), result.finds());
		assertThat(result.found()).as("five sections plus the resolved archive").hasSize(6);
		assertThat(result.finds())
				.as("keyed, not a collection scan (#146)")
				.containsExactly("Archive filter={\"_id\": \"a0\"}");
	}

	/**
	 * The fan-out, and the number that decides the design of #138: resolving proxy by proxy
	 * costs one keyed query per cross-document child. Bounded in volume, but linear in round
	 * trips — which is why a caller that knows the whole set (a delete walking the tree)
	 * should collect the ids and issue a single {@code $in} per collection instead, the
	 * counterpart of the JPA indirection policy's batch mode.
	 */
	@Test
	void fanOutCostsOneKeyedQueryPerCrossDocumentChild() throws Exception {
		saveFixture(MANY, MANY);
		EObject root = loadRoot();

		WalkResult result = walk(root);

		System.out.printf("### fan-out(%d sections, %d archives): found=%d finds=%s%n",
				MANY, MANY, result.found().size(), result.finds());
		assertThat(result.found()).as("sections plus their archives").hasSize(2 * MANY);
		assertThat(result.finds())
				.as("one keyed find per child — bounded in volume, linear in round trips")
				.hasSize(MANY)
				.allSatisfy(find -> assertThat(find).startsWith("Archive filter="));
	}

	/**
	 * The limit of the keyed fast path, pinned so nobody optimises the fallback away: an
	 * <b>embedded</b> containment child has no document of its own, so its id can never be
	 * found by a keyed {@code _id} lookup. Resolving a reference to one therefore still needs
	 * the documents that might contain it — and that is exactly where {@code getEObject} falls
	 * back to the collection-wide population, once (issue #146, preserving #116).
	 * <p>
	 * Measured through the wire: the keyed attempt happens first and misses, then the
	 * unfiltered load follows, and the child comes back resolved rather than as a dead proxy.
	 */
	@Test
	void embeddedChildFragmentFallsBackToACollectionLoad() throws Exception {
		saveFixture(3, 0);

		ResourceSet readSet = resourceSet();
		Resource resource = readSet.createResource(uriFor("Library"));
		resource.load(null);
		synchronized (wireFinds) {
			wireFinds.clear();
		}

		// resolve by fragment WITHOUT touching the contents first, so the resource is still
		// unpopulated — "s1" is embedded in the library document, not a root of its own
		EObject embedded = resource.getEObject("s1");

		List<String> finds;
		synchronized (wireFinds) {
			finds = new ArrayList<>(wireFinds);
		}
		System.out.printf("### embedded-fragment(s1): resolved=%s finds=%s%n",
				embedded != null, finds);
		assertThat(embedded).as("the embedded child must still be resolvable by fragment").isNotNull();
		assertThat(embedded.eGet(sectionClass.getEStructuralFeature("title"))).isEqualTo("Section 1");
		assertThat(finds)
				.as("keyed attempt first, then the fallback load that alone can see embedded children")
				.containsExactly("Library filter={\"_id\": \"s1\"}", "Library UNFILTERED");
	}

	/**
	 * What the command-path delete of issue #223 costs: <b>one</b> resolve for the whole
	 * selector, however many documents it matches and however many children they own.
	 * <p>
	 * The fix had to add a read — the ownership bookkeeping and the #219 refusal both need to
	 * know which documents the filter hits before anything is removed. This case pins the budget
	 * that made that acceptable: the resolve is a single {@code find}, and collecting the owned
	 * children costs nothing on top, because a leaf child type is answered from the proxy URI
	 * without ever being read. A regression to one query per match, or to a decode of every
	 * matched document, shows up here as extra entries rather than as a slow build.
	 */
	@Test
	void aCommandDeleteResolvesOnceForTheWholeSelector() throws Exception {
		saveFixture(MANY, MANY);
		synchronized (wireFinds) {
			wireFinds.clear();
		}

		DeleteCommand delete = CommandFactory.eINSTANCE.createDeleteCommand();
		delete.setSelector(QueryBuilder.from(libraryClass)
				.where(Expressions.path((EAttribute) libraryClass.getEStructuralFeature("lid")).eq("l1"))
				.build());
		Resource resource = resourceSet().createResource(uriFor("Library"));
		long affected = ((CommandResource) resource).execute(delete);

		List<String> finds;
		synchronized (wireFinds) {
			finds = new ArrayList<>(wireFinds);
		}
		System.out.printf("### command-delete(%d sections, %d archives): affected=%d finds=%s%n",
				MANY, MANY, affected, finds);

		assertThat(affected).isEqualTo(1);
		assertThat(finds)
				.as("one resolve for the selector — not one per match, and not one per owned child")
				.hasSize(1);
		assertThat(finds.get(0)).startsWith("Library filter=");
		assertThat(database.getCollection("Archive", BsonDocument.class).countDocuments())
				.as("and the owned children went with the root").isZero();
	}
}
