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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.emf.osgi.metadata.MetadataServices;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.eclipse.fennec.persistence.mongo.MongoResourceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import jakarta.persistence.EntityManagerFactory;

/**
 * Cross-backend test: one {@link ResourceSet} carries JPA-backed <b>and</b>
 * MongoDB-backed resources of the same TCK model.
 * <p>
 * Directionality is asymmetric by construction: JPA stores non-containment references
 * as foreign keys and can therefore only reference rows of its own database — but the
 * Mongo backend stores references as URIs, so a <b>Mongo document can reference a
 * JPA-persisted object</b>. Resolution runs through the shared ResourceSet:
 * proxy URI {@code jpa://…#<id>} → {@code JPAResourceImpl.getEObject}.
 *
 * @author Mark Hoffmann
 * @since 16.07.2026
 */
class MixedBackendResourceSetTest {

	static {
		// Same doctrine as AbstractPersistenceTCK (issue #79): H2 caches the JVM zone
		// statically at first use, and this class does not extend the abstract TCK —
		// when it runs first in the module it must pin UTC itself.
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static final String PU_NAME = "mixed";

	private EPackage tckPackage;
	private EClass personClass;
	private EStructuralFeature personName;
	private EStructuralFeature personBestFriend;
	private EStructuralFeature personFriends;

	private EntityManagerFactory emf;
	private MongoClient client;
	private MongoDatabase database;
	private MetadataWhiteboard metadataService;
	private String databaseName;

	@BeforeEach
	void setUp() throws IOException {
		String connectionString = MongoTestSupport.connectionString();
		assumeTrue(nonNull(connectionString),
				"No MongoDB available (set -Dmongo.uri or provide docker/podman)");

		tckPackage = loadModel();
		personClass = (EClass) tckPackage.getEClassifier("Person");
		personName = personClass.getEStructuralFeature("name");
		personBestFriend = personClass.getEStructuralFeature("bestFriend");
		personFriends = personClass.getEStructuralFeature("friends");

		List<EClassifier> eClasses = new ArrayList<>();
		eClasses.add(tckPackage.getEClassifier("Person"));
		eClasses.add(tckPackage.getEClassifier("Address"));
		eClasses.add(tckPackage.getEClassifier("Company"));
		emf = JpaTckSupport.bootstrap(PU_NAME, eClasses);

		metadataService = MetadataServices.createWhiteboard();
		metadataService.registerPackage(tckPackage);
		client = MongoClients.create(connectionString);
		databaseName = "mixed_" + UUID.randomUUID().toString().replace("-", "");
		database = client.getDatabase(databaseName);
	}

	@AfterEach
	void tearDown() {
		if (nonNull(database)) {
			database.drop();
			database = null;
		}
		if (nonNull(client)) {
			client.close();
			client = null;
		}
		if (nonNull(emf)) {
			emf.close();
			emf = null;
		}
	}

	private EPackage loadModel() throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new XMIResourceFactoryImpl());
		// the TCK model ships as a resource next to the abstract TCK (issue #99)
		Resource resource = resourceSet.createResource(URI.createURI("tck.ecore"));
		try (InputStream stream = AbstractPersistenceTCK.class.getResourceAsStream("tck.ecore")) {
			resource.load(stream, null);
		}
		EPackage ePackage = (EPackage) resource.getContents().get(0);
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		return ePackage;
	}

	/** One ResourceSet with BOTH backend factories mounted. */
	private ResourceSet createMixedResourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(tckPackage.getNsURI(), tckPackage);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("jpa", new JPAResourceFactory(emf));
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("mongodb", new MongoResourceFactory(database, metadataService, null));
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new XMIResourceFactoryImpl());
		return resourceSet;
	}

	private URI jpaUri(String typeName) {
		return URI.createURI("jpa://" + PU_NAME + "/" + typeName);
	}

	private URI mongoUri(String typeName) {
		return URI.createURI("mongodb://" + databaseName + "/" + typeName);
	}

	private EObject newPerson(int id, String name) {
		EObject person = EcoreUtil.create(personClass);
		person.eSet(personClass.getEStructuralFeature("pid"), id);
		person.eSet(personName, name);
		return person;
	}

	private EObject findById(Resource resource, String id) {
		for (EObject candidate : resource.getContents()) {
			if (id.equals(EcoreUtil.getID(candidate))) {
				return candidate;
			}
		}
		return null;
	}

	@Test
	void mongoDocumentReferencesJpaObject() throws Exception {
		// friend lives in JPA, person (referencing the friend) lives in Mongo —
		// both mounted in ONE ResourceSet before saving so the codec can write the
		// cross-backend reference URI (standard EMF cross-resource semantics).
		ResourceSet writeSet = createMixedResourceSet();
		Resource jpaResource = writeSet.createResource(jpaUri("Person"));
		Resource mongoResource = writeSet.createResource(mongoUri("Person"));

		EObject friendJpa = newPerson(1, "JPA Friend");
		EObject personMongo = newPerson(2, "Mongo Person");
		personMongo.eSet(personBestFriend, friendJpa);

		jpaResource.getContents().add(friendJpa);
		mongoResource.getContents().add(personMongo);
		jpaResource.save(null);
		mongoResource.save(null);

		// Fresh mixed ResourceSet: load the Mongo person, resolve the proxy — the
		// ResourceSet must route jpa://…#1 to the JPA backend.
		ResourceSet readSet = createMixedResourceSet();
		Resource loadedMongo = readSet.createResource(mongoUri("Person"));
		loadedMongo.load(null);
		EObject loadedPerson = findById(loadedMongo, "2");
		assertThat(loadedPerson).isNotNull();

		EObject bestFriend = EcoreUtil.resolve((EObject) loadedPerson.eGet(personBestFriend), readSet);
		assertThat(bestFriend.eIsProxy())
				.as("cross-backend proxy must resolve through the shared ResourceSet")
				.isFalse();
		assertThat(bestFriend.eGet(personName)).isEqualTo("JPA Friend");
		assertThat(bestFriend.eResource()).isNotNull();
		assertThat(bestFriend.eResource().getURI().scheme())
				.as("the resolved object must come from the JPA backend")
				.isEqualTo("jpa");
	}

	@Test
	void mongoListMixesJpaAndMongoTargets() throws Exception {
		ResourceSet writeSet = createMixedResourceSet();
		Resource jpaResource = writeSet.createResource(jpaUri("Person"));
		Resource mongoResource = writeSet.createResource(mongoUri("Person"));

		EObject jpaFriend = newPerson(1, "JPA Friend");
		EObject mongoFriend = newPerson(2, "Mongo Friend");
		EObject person = newPerson(3, "Owner");
		@SuppressWarnings("unchecked")
		List<EObject> friends = (List<EObject>) person.eGet(personFriends);
		friends.add(jpaFriend);
		friends.add(mongoFriend);

		jpaResource.getContents().add(jpaFriend);
		mongoResource.getContents().add(mongoFriend);
		mongoResource.getContents().add(person);
		jpaResource.save(null);
		mongoResource.save(null);

		ResourceSet readSet = createMixedResourceSet();
		Resource loadedMongo = readSet.createResource(mongoUri("Person"));
		loadedMongo.load(null);
		EObject loadedPerson = findById(loadedMongo, "3");
		assertThat(loadedPerson).isNotNull();

		@SuppressWarnings("unchecked")
		List<EObject> loadedFriends = (List<EObject>) loadedPerson.eGet(personFriends);
		assertThat(loadedFriends).hasSize(2);
		List<String> names = new ArrayList<>();
		List<String> schemes = new ArrayList<>();
		for (EObject friend : loadedFriends) {
			EObject resolved = EcoreUtil.resolve(friend, readSet);
			assertThat(resolved.eIsProxy()).isFalse();
			names.add((String) resolved.eGet(personName));
			schemes.add(resolved.eResource().getURI().scheme());
		}
		assertThat(names).containsExactlyInAnyOrder("JPA Friend", "Mongo Friend");
		assertThat(schemes).containsExactlyInAnyOrder("jpa", "mongodb");
	}

	@Test
	void bothBackendsResolveIndependentlySideBySide() throws Exception {
		// Same-backend references in both backends, mounted in one ResourceSet.
		ResourceSet writeSet = createMixedResourceSet();
		Resource jpaResource = writeSet.createResource(jpaUri("Person"));
		Resource mongoResource = writeSet.createResource(mongoUri("Person"));

		EObject jpaFriend = newPerson(1, "JPA Friend");
		EObject jpaPerson = newPerson(2, "JPA Person");
		jpaPerson.eSet(personBestFriend, jpaFriend);
		EObject mongoFriend = newPerson(11, "Mongo Friend");
		EObject mongoPerson = newPerson(12, "Mongo Person");
		mongoPerson.eSet(personBestFriend, mongoFriend);

		jpaResource.getContents().add(jpaFriend);
		jpaResource.getContents().add(jpaPerson);
		mongoResource.getContents().add(mongoFriend);
		mongoResource.getContents().add(mongoPerson);
		jpaResource.save(null);
		mongoResource.save(null);

		ResourceSet readSet = createMixedResourceSet();
		Resource loadedJpa = readSet.createResource(jpaUri("Person"));
		loadedJpa.load(null);
		Resource loadedMongo = readSet.createResource(mongoUri("Person"));
		loadedMongo.load(null);

		EObject jpaLoaded = findById(loadedJpa, "2");
		EObject jpaResolved = EcoreUtil.resolve((EObject) jpaLoaded.eGet(personBestFriend), readSet);
		assertThat(jpaResolved.eIsProxy()).isFalse();
		assertThat(jpaResolved.eGet(personName)).isEqualTo("JPA Friend");

		EObject mongoLoaded = findById(loadedMongo, "12");
		EObject mongoResolved = EcoreUtil.resolve((EObject) mongoLoaded.eGet(personBestFriend), readSet);
		assertThat(mongoResolved.eIsProxy()).isFalse();
		assertThat(mongoResolved.eGet(personName)).isEqualTo("Mongo Friend");
	}
}
