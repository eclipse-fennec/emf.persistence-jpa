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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.persistence.pushstreams.PersistencePushStreams;
import org.eclipse.fennec.persistence.resource.PersistenceResource;
import org.eclipse.fennec.persistence.resource.StreamingResource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.util.pushstream.PushStream;

/**
 * Backend-agnostic compatibility suite for EMF persistence backends.
 * <p>
 * Every backend binding provides bootstrap, a configured {@link ResourceSet} and the
 * URI shape; the suite then verifies the shared persistence contract: attribute
 * round trips, containment (single document/row ownership), non-containment references
 * (single- and multi-valued) resolving as EMF proxies through the ResourceSet,
 * bidirectional references, {@link PersistenceResource} operations and the
 * {@link StreamingResource} / PushStream behavior.
 *
 * @author Mark Hoffmann
 * @since 16.07.2026
 */
public abstract class AbstractPersistenceTCK {

	protected EPackage tckPackage;
	protected EClass personClass;
	protected EClass addressClass;
	protected EClass companyClass;

	protected EStructuralFeature personName;
	protected EStructuralFeature personAge;
	protected EStructuralFeature personAddresses;
	protected EStructuralFeature personBestFriend;
	protected EStructuralFeature personFriends;
	protected EStructuralFeature personEmployer;
	protected EStructuralFeature addressStreet;
	protected EStructuralFeature companyName;
	protected EStructuralFeature companyEmployees;

	// ------------------------------------------------------------ backend SPI

	/** Bootstraps the backend for the (already loaded) TCK package. */
	protected abstract void setUpBackend(EPackage tckPackage) throws Exception;

	/** Releases all backend resources. */
	protected abstract void tearDownBackend() throws Exception;

	/** Creates a ResourceSet wired with the backend's {@link Resource.Factory}. */
	protected abstract ResourceSet createBackendResourceSet();

	/** Returns the backend resource URI for the given type, e.g. {@code jpa://tck/Person}. */
	protected abstract URI uriFor(String typeName);

	// ----------------------------------------------------------------- set up

	@BeforeEach
	void setUpTck() throws Exception {
		tckPackage = loadTckModel();
		personClass = (EClass) tckPackage.getEClassifier("Person");
		addressClass = (EClass) tckPackage.getEClassifier("Address");
		companyClass = (EClass) tckPackage.getEClassifier("Company");
		personName = personClass.getEStructuralFeature("name");
		personAge = personClass.getEStructuralFeature("age");
		personAddresses = personClass.getEStructuralFeature("addresses");
		personBestFriend = personClass.getEStructuralFeature("bestFriend");
		personFriends = personClass.getEStructuralFeature("friends");
		personEmployer = personClass.getEStructuralFeature("employer");
		addressStreet = addressClass.getEStructuralFeature("street");
		companyName = companyClass.getEStructuralFeature("name");
		companyEmployees = companyClass.getEStructuralFeature("employees");
		setUpBackend(tckPackage);
	}

	@AfterEach
	void tearDownTck() throws Exception {
		tearDownBackend();
	}

	/**
	 * The TCK model to run against. The default model uses int-typed EMF ids; the
	 * String-id bindings override this with {@code data/tck-string.ecore}.
	 */
	protected String tckModelPath() {
		return "data/tck.ecore";
	}

	protected EPackage loadTckModel() throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new XMIResourceFactoryImpl());
		File ecoreFile = new File(tckModelPath());
		assertThat(ecoreFile).as("TCK ecore must exist").exists();
		Resource resource = resourceSet.createResource(URI.createFileURI(ecoreFile.getAbsolutePath()));
		resource.load(null);
		EPackage ePackage = (EPackage) resource.getContents().get(0);
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		return ePackage;
	}

	// ----------------------------------------------------------------- helpers

	protected EObject newPerson(int id, String name, int age) {
		EObject person = EcoreUtil.create(personClass);
		person.eSet(personClass.getEStructuralFeature("pid"), idValue(personClass, id));
		person.eSet(personName, name);
		person.eSet(personAge, age);
		return person;
	}

	/** Creates a person WITHOUT an id — for the id-generation contract test. */
	protected EObject newPersonWithoutId(String name, int age) {
		EObject person = EcoreUtil.create(personClass);
		person.eSet(personName, name);
		person.eSet(personAge, age);
		return person;
	}

	protected EObject newAddress(int id, String street, String city) {
		EObject address = EcoreUtil.create(addressClass);
		address.eSet(addressClass.getEStructuralFeature("aid"), idValue(addressClass, id));
		address.eSet(addressStreet, street);
		address.eSet(addressClass.getEStructuralFeature("city"), city);
		return address;
	}

	protected EObject newCompany(int id, String name) {
		EObject company = EcoreUtil.create(companyClass);
		company.eSet(companyClass.getEStructuralFeature("cid"), idValue(companyClass, id));
		company.eSet(companyName, name);
		return company;
	}

	/**
	 * Converts the numeric test id to the model's id attribute type — the same test
	 * bodies run against the int-id and the String-id TCK model
	 * ({@code EcoreUtil.getID} yields the identical string form for both).
	 */
	private Object idValue(EClass eClass, int id) {
		Class<?> instanceClass = eClass.getEIDAttribute().getEAttributeType().getInstanceClass();
		return instanceClass == String.class ? String.valueOf(id) : id;
	}

	/** Saves the given objects as contents of the type's backend resource. */
	protected void save(ResourceSet resourceSet, String typeName, EObject... objects) throws IOException {
		Resource resource = resourceSet.createResource(uriFor(typeName));
		for (EObject object : objects) {
			resource.getContents().add(object);
		}
		resource.save(null);
	}

	/** Loads all objects of the type into a resource of the given ResourceSet. */
	protected Resource loadAll(ResourceSet resourceSet, String typeName) throws IOException {
		Resource resource = resourceSet.getResource(uriFor(typeName), false);
		if (resource == null) {
			resource = resourceSet.createResource(uriFor(typeName));
		}
		resource.load(null);
		return resource;
	}

	protected EObject findById(Resource resource, String id) {
		for (EObject candidate : resource.getContents()) {
			if (id.equals(EcoreUtil.getID(candidate))) {
				return candidate;
			}
		}
		return null;
	}

	protected EObject resolved(EObject maybeProxy, ResourceSet resourceSet) {
		return EcoreUtil.resolve(maybeProxy, resourceSet);
	}

	@SuppressWarnings("unchecked")
	protected List<EObject> listOf(EObject owner, EStructuralFeature feature) {
		return (List<EObject>) owner.eGet(feature);
	}

	// -------------------------------------------------------------------- tests

	@Test
	public void saveAndLoadAttributes() throws Exception {
		ResourceSet writeSet = createBackendResourceSet();
		save(writeSet, "Person", newPerson(1, "Emil Tester", 42));

		ResourceSet readSet = createBackendResourceSet();
		EObject loaded = findById(loadAll(readSet, "Person"), "1");
		assertThat(loaded).isNotNull();
		assertThat(loaded.eGet(personName)).isEqualTo("Emil Tester");
		assertThat(loaded.eGet(personAge)).isEqualTo(42);
	}

	@Test
	public void containmentManyRoundtrip() throws Exception {
		EObject person = newPerson(1, "Emil", 30);
		listOf(person, personAddresses).add(newAddress(11, "Main Street 1", "Jena"));
		listOf(person, personAddresses).add(newAddress(12, "Second Street 2", "Gera"));

		ResourceSet writeSet = createBackendResourceSet();
		save(writeSet, "Person", person);

		ResourceSet readSet = createBackendResourceSet();
		EObject loaded = findById(loadAll(readSet, "Person"), "1");
		assertThat(loaded).isNotNull();
		List<EObject> addresses = listOf(loaded, personAddresses);
		assertThat(addresses).hasSize(2);
		assertThat(addresses).allSatisfy(address -> {
			assertThat(address.eIsProxy()).as("containment children are materialised").isFalse();
			assertThat(address.eContainer()).isSameAs(loaded);
		});
		assertThat(addresses).extracting(a -> a.eGet(addressStreet))
				.containsExactlyInAnyOrder("Main Street 1", "Second Street 2");
	}

	@Test
	public void nonContainmentSingleResolvesViaResourceSet() throws Exception {
		EObject friend = newPerson(2, "Friend", 25);
		EObject person = newPerson(1, "Emil", 30);
		person.eSet(personBestFriend, friend);

		ResourceSet writeSet = createBackendResourceSet();
		save(writeSet, "Person", friend, person);

		ResourceSet readSet = createBackendResourceSet();
		EObject loaded = findById(loadAll(readSet, "Person"), "1");
		assertThat(loaded).isNotNull();
		EObject bestFriend = resolved((EObject) loaded.eGet(personBestFriend), readSet);
		assertThat(bestFriend).isNotNull();
		assertThat(bestFriend.eIsProxy())
				.as("bestFriend must resolve through the ResourceSet")
				.isFalse();
		assertThat(bestFriend.eGet(personName)).isEqualTo("Friend");
	}

	@Test
	public void nonContainmentManyResolvesViaResourceSet() throws Exception {
		EObject f1 = newPerson(2, "First Friend", 25);
		EObject f2 = newPerson(3, "Second Friend", 26);
		EObject person = newPerson(1, "Emil", 30);
		listOf(person, personFriends).add(f1);
		listOf(person, personFriends).add(f2);

		ResourceSet writeSet = createBackendResourceSet();
		save(writeSet, "Person", f1, f2, person);

		ResourceSet readSet = createBackendResourceSet();
		EObject loaded = findById(loadAll(readSet, "Person"), "1");
		assertThat(loaded).isNotNull();
		List<String> names = new ArrayList<>();
		for (EObject friend : listOf(loaded, personFriends)) {
			EObject resolvedFriend = resolved(friend, readSet);
			assertThat(resolvedFriend.eIsProxy()).isFalse();
			names.add((String) resolvedFriend.eGet(personName));
		}
		assertThat(names).containsExactlyInAnyOrder("First Friend", "Second Friend");
	}

	@Test
	public void bidirectionalReferenceBothSidesResolve() throws Exception {
		EObject company = newCompany(21, "Data In Motion");
		EObject person = newPerson(1, "Emil", 30);
		person.eSet(personEmployer, company);
		assertThat(listOf(company, companyEmployees)).contains(person);

		// Cross-resource references require resource membership before saving —
		// standard EMF semantics (identical to XMI cross-document references).
		ResourceSet writeSet = createBackendResourceSet();
		Resource companyResource = writeSet.createResource(uriFor("Company"));
		Resource personResource = writeSet.createResource(uriFor("Person"));
		companyResource.getContents().add(company);
		personResource.getContents().add(person);
		companyResource.save(null);
		personResource.save(null);

		ResourceSet readSet = createBackendResourceSet();
		EObject loadedPerson = findById(loadAll(readSet, "Person"), "1");
		assertThat(loadedPerson).isNotNull();
		EObject employer = resolved((EObject) loadedPerson.eGet(personEmployer), readSet);
		assertThat(employer.eIsProxy()).isFalse();
		assertThat(employer.eGet(companyName)).isEqualTo("Data In Motion");

		ResourceSet readSet2 = createBackendResourceSet();
		EObject loadedCompany = findById(loadAll(readSet2, "Company"), "21");
		assertThat(loadedCompany).isNotNull();
		List<EObject> employees = listOf(loadedCompany, companyEmployees);
		assertThat(employees).hasSize(1);
		EObject employee = resolved(employees.get(0), readSet2);
		assertThat(employee.eIsProxy()).isFalse();
		assertThat(employee.eGet(personName)).isEqualTo("Emil");
	}

	@Test
	public void countAndExist() throws Exception {
		ResourceSet writeSet = createBackendResourceSet();
		Resource resource = writeSet.createResource(uriFor("Person"));
		assertThat(resource).isInstanceOf(PersistenceResource.class);
		try (PersistenceResource persistence = (PersistenceResource) resource) {
			assertThat(persistence.exist()).isFalse();
			assertThat(persistence.count()).isZero();

			resource.getContents().add(newPerson(1, "Emil", 30));
			resource.getContents().add(newPerson(2, "Emilia", 31));
			resource.save(null);

			assertThat(persistence.count()).isEqualTo(2);
			assertThat(persistence.exist()).isTrue();
		}
	}

	@Test
	public void deleteRemovesPersistedObjects() throws Exception {
		ResourceSet writeSet = createBackendResourceSet();
		save(writeSet, "Person", newPerson(1, "Emil", 30));

		ResourceSet workSet = createBackendResourceSet();
		Resource resource = loadAll(workSet, "Person");
		try (PersistenceResource persistence = (PersistenceResource) resource) {
			assertThat(persistence.count()).isEqualTo(1);
			persistence.delete(null);
			assertThat(persistence.count()).isZero();
		}
	}

	@Test
	public void idGenerationOnSaveAssignsAndWritesBackId() throws Exception {
		EObject person = newPersonWithoutId("Generated", 33);

		ResourceSet writeSet = createBackendResourceSet();
		save(writeSet, "Person", person);

		// Contract: after save the backend has assigned an id AND written it back
		// into the saved EObject (Mongo: generated ObjectId hex; JPA: sequence value).
		String generatedId = EcoreUtil.getID(person);
		assertThat(generatedId)
				.as("save must write the generated id back into the EObject")
				.isNotNull()
				.isNotEmpty()
				.isNotEqualTo("0");

		ResourceSet readSet = createBackendResourceSet();
		Resource resource = loadAll(readSet, "Person");
		assertThat(resource.getContents()).hasSize(1);
		EObject loaded = findById(resource, generatedId);
		assertThat(loaded).as("object must be loadable under the generated id").isNotNull();
		assertThat(loaded.eGet(personName)).isEqualTo("Generated");
	}

	@Test
	public void streamAllObjects() throws Exception {
		int total = 25;
		ResourceSet writeSet = createBackendResourceSet();
		Resource resource = writeSet.createResource(uriFor("Person"));
		for (int i = 0; i < total; i++) {
			resource.getContents().add(newPerson(100 + i, "Person-" + i, i));
		}
		resource.save(null);

		ResourceSet readSet = createBackendResourceSet();
		Resource readResource = readSet.createResource(uriFor("Person"));
		assertThat(readResource).isInstanceOf(StreamingResource.class);
		List<String> names = new ArrayList<>();
		try (Stream<EObject> stream = ((StreamingResource) readResource).stream()) {
			stream.forEach(person -> names.add((String) person.eGet(personName)));
		}
		assertThat(names).hasSize(total);
		assertThat(names).contains("Person-0", "Person-13", "Person-24");
	}

	@Test
	public void pushStreamDeliversAllObjects() throws Exception {
		int total = 25;
		ResourceSet writeSet = createBackendResourceSet();
		Resource resource = writeSet.createResource(uriFor("Person"));
		for (int i = 0; i < total; i++) {
			resource.getContents().add(newPerson(100 + i, "Person-" + i, i));
		}
		resource.save(null);

		ResourceSet readSet = createBackendResourceSet();
		Resource readResource = readSet.createResource(uriFor("Person"));
		List<String> names = new ArrayList<>();
		CountDownLatch closed = new CountDownLatch(1);
		AtomicReference<Throwable> failure = new AtomicReference<>();
		try (PushStream<EObject> stream = PersistencePushStreams.createPushStream(
				(StreamingResource) readResource, null, null, null)) {
			stream.onClose(closed::countDown)
					.onError(failure::set)
					.forEach(person -> {
						synchronized (names) {
							names.add((String) person.eGet(personName));
						}
					});
			assertThat(closed.await(30, TimeUnit.SECONDS)).as("stream must terminate").isTrue();
		}
		assertThat(failure.get()).isNull();
		synchronized (names) {
			assertThat(names).hasSize(total);
		}
	}
}
