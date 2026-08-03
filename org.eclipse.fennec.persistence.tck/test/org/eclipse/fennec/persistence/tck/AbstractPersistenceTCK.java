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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.builder.Expressions;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.pushstreams.PersistencePushStreams;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.eclipse.fennec.persistence.query.api.QueryResultRow;
import org.eclipse.fennec.persistence.query.api.QueryShape;
import org.eclipse.fennec.model.command.CommandFactory;
import org.eclipse.fennec.model.command.DeleteCommand;
import org.eclipse.fennec.model.command.InsertCommand;
import org.eclipse.fennec.model.command.UpdateCommand;
import org.eclipse.fennec.model.stream.ChangeEntry;
import org.eclipse.fennec.model.stream.ChangeSet;
import org.eclipse.fennec.model.stream.DeltaKind;
import org.eclipse.fennec.model.stream.StreamFactory;
import org.eclipse.fennec.persistence.query.api.CommandResource;
import org.eclipse.fennec.persistence.query.api.QueryableResource;
import org.eclipse.fennec.persistence.query.derived.DerivedReferenceCompiler;
import org.eclipse.fennec.persistence.query.derived.QueryBackedSettingDelegateFactory;
import org.eclipse.fennec.persistence.query.memory.MemoryQueries;
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

	// ------------------------------------------------------------ query TCK (v2 IR)

	/** Saves the standard query fixture: three persons, Bob with two addresses. */
	private void saveQueryFixture() throws Exception {
		EObject bob = newPerson(2, "Bob", 40);
		listOf(bob, personAddresses).add(newAddress(21, "Main Street 5", "Jena"));
		listOf(bob, personAddresses).add(newAddress(22, "Side Road 9", "Gera"));
		ResourceSet writeSet = createBackendResourceSet();
		save(writeSet, "Person",
				newPerson(1, "Alice", 30),
				bob,
				newPerson(3, "Carol", 50));
	}

	private QueryableResource queryable(ResourceSet resourceSet) {
		return (QueryableResource) resourceSet.createResource(uriFor("Person"));
	}

	@Test
	public void queryGroupedPredicateTree() throws Exception {
		saveQueryFixture();
		// (age >= 40 OR name = "Alice") AND age <> 50 — inexpressible in the v1 IR
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.and(
						Expressions.or(
								Expressions.path(personAge).ge(40),
								Expressions.path(personName).eq("Alice")),
						Expressions.path(personAge).ne(50)))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactlyInAnyOrder("Alice", "Bob");
		}
	}

	@Test
	public void queryNeInAndIsNotNull() throws Exception {
		saveQueryFixture();
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.and(
						Expressions.path(personAge).in(30, 40, 99),
						Expressions.path(personName).ne("Alice"),
						Expressions.path(personName).isNotNull()))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Bob");
		}
	}

	@Test
	public void queryCaseInsensitiveMatching() throws Exception {
		saveQueryFixture();
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.path(personName).containsIgnoreCase("ARO"))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Carol");
		}
	}

	@Test
	public void queryExistsOverContainment() throws Exception {
		saveQueryFixture();
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.any(Expressions.propertyPath(personAddresses),
						a -> a.path(addressStreet).startsWith("Main")))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Bob");
		}
	}

	@Test
	public void queryForAllIsVacuouslyTrueOnEmpty() throws Exception {
		saveQueryFixture();
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.all(Expressions.propertyPath(personAddresses),
						a -> a.path(addressStreet).startsWith("Main")))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			// Alice and Carol have no addresses (vacuously true); Bob has a non-Main address
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactlyInAnyOrder("Alice", "Carol");
		}
	}

	@Test
	public void queryStringFunctions() throws Exception {
		saveQueryFixture();
		Query lower = QueryBuilder.from(personClass)
				.where(Expressions.path(personName).toLower().eq("bob"))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(lower)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Bob");
		}
		Query length = QueryBuilder.from(personClass)
				.where(Expressions.path(personName).length().gt(3))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(length)) {
			// Alice and Carol have 5 letters, Bob only 3
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactlyInAnyOrder("Alice", "Carol");
		}
	}

	@Test
	public void queryArithmetic() throws Exception {
		saveQueryFixture();
		// (age + 10) * 2 > 90 — Alice 80, Bob 100, Carol 120
		Query addMul = QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).plus(10).times(2).gt(90))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(addMul)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactlyInAnyOrder("Bob", "Carol");
		}
		// DIV is floating-point: 30 / 4 = 7.5 — integer truncation would find nobody
		Query fpDivision = QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).dividedBy(4).eq(7.5))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(fpDivision)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Alice");
		}
		// age mod 20 = 0 — only Bob (40); 30 and 50 leave 10
		Query modulo = QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).mod(20).eq(0))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(modulo)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Bob");
		}
		// -age < -45 — only Carol (50)
		Query negated = QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).negated().lt(-45))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(negated)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Carol");
		}
	}

	@Test
	public void queryDivisionByLiteralZeroIsRefused() throws Exception {
		saveQueryFixture();
		Query zeroDivision = QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).dividedBy(0).gt(1))
				.build();
		QueryableResource resource = queryable(createBackendResourceSet());
		assertThatThrownBy(() -> resource.query(zeroDivision).close())
				.isInstanceOf(IOException.class)
				.hasMessageContaining("zero");
	}

	@Test
	public void queryExtendedStringFunctions() throws Exception {
		saveQueryFixture();
		// CONCAT — "Bob" + "!" = "Bob!"
		Query concatenated = QueryBuilder.from(personClass)
				.where(Expressions.concat(Expressions.path(personName), "!").eq("Bob!"))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(concatenated)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Bob");
		}
		// INDEX_OF is 0-based with -1 when absent: "Bob"=1, "Carol"=3, "Alice"=-1
		Query position = QueryBuilder.from(personClass)
				.where(Expressions.path(personName).indexOf("o").eq(1))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(position)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Bob");
		}
		Query absent = QueryBuilder.from(personClass)
				.where(Expressions.path(personName).indexOf("o").eq(-1))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(absent)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Alice");
		}
		// SUBSTRING is 0-based; a negative start counts from the end of the string
		Query window = QueryBuilder.from(personClass)
				.where(Expressions.path(personName).substring(1, 3).eq("aro"))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(window)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Carol");
		}
		Query fromEnd = QueryBuilder.from(personClass)
				.where(Expressions.path(personName).substring(-2).eq("ce"))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(fromEnd)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Alice");
		}
	}

	@Test
	public void queryRuntimeZeroDivisorSurfacesBackendError() throws Exception {
		saveQueryFixture();
		// a literal zero is refused statically; a zero bound at runtime is the backend's
		// division error (the memory oracle yields null/no match instead — see its tests)
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).dividedBy(Expressions.param("divisor")).gt(1))
				.build();
		QueryableResource resource = queryable(createBackendResourceSet());
		assertThatThrownBy(() -> resource.query(query, Map.of("divisor", 0), null).close())
				.isInstanceOf(IOException.class);
	}

	@Test
	public void queryFieldToFieldComparison() throws Exception {
		saveQueryFixture();
		Query same = QueryBuilder.from(personClass)
				.where(Expressions.path(personName).eq(Expressions.path(personName)))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(same)) {
			assertThat(result.objects()).hasSize(3);
		}
		Query none = QueryBuilder.from(personClass)
				.where(Expressions.path(personName).ne(Expressions.path(personName)))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(none)) {
			assertThat(result.objects()).isEmpty();
		}
	}

	@Test
	public void querySortSkipTopAndCount() throws Exception {
		saveQueryFixture();
		Query query = QueryBuilder.from(personClass)
				.orderByDesc(personAge)
				.skip(1)
				.top(1)
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Bob");
		}

		Query count = QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).gt(30))
				.countOnly()
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(count)) {
			assertThat(result.shape()).isEqualTo(QueryShape.COUNT);
			assertThat(result.count()).isEqualTo(2);
		}
	}

	@Test
	public void queryParameterBinding() throws Exception {
		saveQueryFixture();
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).eq(Expressions.param("wanted")))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet())
				.query(query, Map.of("wanted", 50), null)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Carol");
		}
	}

	@Test
	public void queryProjectionRows() throws Exception {
		saveQueryFixture();
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).ge(40))
				.selectAs("n", personName)
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			assertThat(result.shape()).isEqualTo(QueryShape.PROJECTION);
			List<QueryResultRow> rows = result.rows().toList();
			assertThat(rows).extracting(row -> row.get("n"))
					.containsExactlyInAnyOrder("Bob", "Carol");
			assertThat(rows).extracting(row -> row.get(0))
					.containsExactlyInAnyOrder("Bob", "Carol");
		}
	}

	@Test
	public void queryWholeSetAggregation() throws Exception {
		saveQueryFixture();
		Query query = QueryBuilder.from(personClass)
				.avg("avgAge", personAge)
				.countOf("cnt")
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			assertThat(result.shape()).isEqualTo(QueryShape.AGGREGATION);
			List<QueryResultRow> rows = result.rows().toList();
			assertThat(rows).hasSize(1);
			assertThat(((Number) rows.get(0).get("avgAge")).doubleValue()).isEqualTo(40.0);
			assertThat(((Number) rows.get(0).get("cnt")).longValue()).isEqualTo(3L);
		}
	}

	@Test
	public void queryGroupedAggregation() throws Exception {
		EObject bob2 = newPerson(4, "Bob", 20);
		ResourceSet writeSet = createBackendResourceSet();
		save(writeSet, "Person",
				newPerson(1, "Alice", 30),
				newPerson(2, "Bob", 40),
				bob2);
		Query query = QueryBuilder.from(personClass)
				.groupBy(personName)
				.avg("avgAge", personAge)
				.countOf("cnt")
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			List<QueryResultRow> rows = result.rows().toList();
			assertThat(rows).hasSize(2);
			QueryResultRow bobRow = rows.stream()
					.filter(row -> "Bob".equals(row.get("name")))
					.findFirst().orElseThrow();
			assertThat(((Number) bobRow.get("avgAge")).doubleValue()).isEqualTo(30.0);
			assertThat(((Number) bobRow.get("cnt")).longValue()).isEqualTo(2L);
		}
	}

	@Test
	public void querySaveAndExecuteByName() throws Exception {
		saveQueryFixture();
		Query named = QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).ge(Expressions.param("minAge")))
				.parameter("minAge", null)
				.named("adults")
				.build();
		// executing a saveQuery-marked query persists it (upsert by name) and runs it
		try (QueryResult result = queryable(createBackendResourceSet())
				.query(named, Map.of("minAge", 40), null)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactlyInAnyOrder("Bob", "Carol");
		}
		// a fresh resource executes it by name, with different parameter bindings
		try (QueryResult result = queryable(createBackendResourceSet())
				.query("adults", Map.of("minAge", 50), null)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Carol");
		}
	}

	@Test
	public void querySavedQueryUpsertsAndUnknownNameIsRefused() throws Exception {
		saveQueryFixture();
		QueryableResource resource = queryable(createBackendResourceSet());
		assertThatThrownBy(() -> resource.query("no-such-query", null, null))
				.isInstanceOf(IOException.class)
				.hasMessageContaining("no-such-query");

		Query first = QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).ge(40))
				.named("current").build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(first)) {
			assertThat(result.objects()).hasSize(2);
		}
		// same name, new definition — last write wins
		Query second = QueryBuilder.from(personClass)
				.where(Expressions.path(personName).eq("Alice"))
				.named("current").build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(second)) {
			assertThat(result.objects()).hasSize(1);
		}
		try (QueryResult result = queryable(createBackendResourceSet()).query("current", null, null)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Alice");
		}
	}

	// -------------------------------------------- derived references (query-backed)

	/** Registers the query-backed delegate factory for the duration of one test. */
	private AutoCloseable derivedDelegateRegistration() {
		EStructuralFeature.Internal.SettingDelegate.Factory.Registry.INSTANCE.put(
				DerivedReferenceCompiler.DELEGATE_URI, new QueryBackedSettingDelegateFactory());
		return () -> EStructuralFeature.Internal.SettingDelegate.Factory.Registry.INSTANCE
				.remove(DerivedReferenceCompiler.DELEGATE_URI);
	}

	private void saveFriendsFixture() throws Exception {
		EObject adult = newPerson(2, "Adult", 30);
		EObject kid = newPerson(3, "Kid", 8);
		EObject owner = newPerson(1, "Owner", 40);
		listOf(owner, personFriends).add(adult);
		listOf(owner, personFriends).add(kid);
		save(createBackendResourceSet(), "Person", adult, kid, owner);
	}

	@Test
	public void derivedReferenceComputesViaBackendQuery() throws Exception {
		try (AutoCloseable registration = derivedDelegateRegistration()) {
			saveFriendsFixture();
			ResourceSet readSet = createBackendResourceSet();
			EObject loaded = findById(loadAll(readSet, "Person"), "1");
			assertThat(loaded).isNotNull();
			EStructuralFeature adultFriends = personClass.getEStructuralFeature("adultFriends");
			@SuppressWarnings("unchecked")
			List<EObject> result = (List<EObject>) loaded.eGet(adultFriends);
			assertThat(result).extracting(person -> person.eGet(personName)).containsExactly("Adult");
		}
	}

	@Test
	public void derivedReferenceDifferentialLocalVsBackend() throws Exception {
		try (AutoCloseable registration = derivedDelegateRegistration()) {
			saveFriendsFixture();
			EStructuralFeature adultFriends = personClass.getEStructuralFeature("adultFriends");

			// unattached objects — the same annotation evaluates locally (concept P5)
			EObject localAdult = newPerson(2, "Adult", 30);
			EObject localOwner = newPerson(1, "Owner", 40);
			listOf(localOwner, personFriends).add(localAdult);
			listOf(localOwner, personFriends).add(newPerson(3, "Kid", 8));
			@SuppressWarnings("unchecked")
			List<EObject> local = (List<EObject>) localOwner.eGet(adultFriends);

			// attached object — the backend answers the same annotation (concept P7)
			EObject loaded = findById(loadAll(createBackendResourceSet(), "Person"), "1");
			@SuppressWarnings("unchecked")
			List<EObject> pushed = (List<EObject>) loaded.eGet(adultFriends);

			assertThat(pushed).extracting(person -> person.eGet(personName))
					.as("pushdown and local evaluation must agree")
					.containsExactlyElementsOf(
							local.stream().map(person -> person.eGet(personName)).toList());
		}
	}

	@Test
	public void queryRefusalIsAnIOExceptionWithDiagnostics() throws Exception {
		saveQueryFixture();
		Query bad = QueryBuilder.from(personClass)
				.avg("avgAge", personAge)
				.orderByAsc(personName)
				.build();
		QueryableResource resource = queryable(createBackendResourceSet());
		assertThatThrownBy(() -> resource.query(bad).close())
				.isInstanceOf(IOException.class)
				.hasMessageContaining("output key");
	}

	/**
	 * Differential mode (#62): the {@code memory} backend is the reference oracle — the
	 * same conformance queries run against the database backend and against the fixture
	 * objects in memory, and the result sets must agree.
	 */
	@Test
	public void queryDifferentialAgainstMemoryOracle() throws Exception {
		saveQueryFixture();
		List<EObject> oracle = new ArrayList<>();
		oracle.add(newPerson(1, "Alice", 30));
		EObject bob = newPerson(2, "Bob", 40);
		listOf(bob, personAddresses).add(newAddress(21, "Main Street 5", "Jena"));
		listOf(bob, personAddresses).add(newAddress(22, "Side Road 9", "Gera"));
		oracle.add(bob);
		oracle.add(newPerson(3, "Carol", 50));

		Map<String, Query> corpus = new LinkedHashMap<>();
		corpus.put("grouped tree", QueryBuilder.from(personClass)
				.where(Expressions.and(
						Expressions.or(
								Expressions.path(personAge).ge(40),
								Expressions.path(personName).eq("Alice")),
						Expressions.path(personAge).ne(50)))
				.build());
		corpus.put("in + ne + isNotNull", QueryBuilder.from(personClass)
				.where(Expressions.and(
						Expressions.path(personAge).in(30, 40, 99),
						Expressions.path(personName).ne("Alice"),
						Expressions.path(personName).isNotNull()))
				.build());
		corpus.put("ci contains", QueryBuilder.from(personClass)
				.where(Expressions.path(personName).containsIgnoreCase("ARO"))
				.build());
		corpus.put("exists", QueryBuilder.from(personClass)
				.where(Expressions.any(Expressions.propertyPath(personAddresses),
						a -> a.path(addressStreet).startsWith("Main")))
				.build());
		corpus.put("forAll", QueryBuilder.from(personClass)
				.where(Expressions.all(Expressions.propertyPath(personAddresses),
						a -> a.path(addressStreet).startsWith("Main")))
				.build());
		corpus.put("sort/skip/top", QueryBuilder.from(personClass)
				.orderByDesc(personAge)
				.skip(1)
				.top(1)
				.build());
		corpus.put("between", QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).between(30, 40))
				.build());
		corpus.put("string function", QueryBuilder.from(personClass)
				.where(Expressions.path(personName).toLower().eq("bob"))
				.build());
		corpus.put("length", QueryBuilder.from(personClass)
				.where(Expressions.path(personName).length().gt(3))
				.build());
		corpus.put("field-to-field", QueryBuilder.from(personClass)
				.where(Expressions.path(personName).eq(Expressions.path(personName)))
				.build());
		corpus.put("arithmetic add/mul", QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).plus(10).times(2).gt(90))
				.build());
		corpus.put("arithmetic fp division", QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).dividedBy(4).eq(7.5))
				.build());
		corpus.put("arithmetic mod", QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).mod(20).eq(0))
				.build());
		corpus.put("arithmetic negate", QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).negated().lt(-45))
				.build());
		corpus.put("concat", QueryBuilder.from(personClass)
				.where(Expressions.concat(Expressions.path(personName), "!").eq("Bob!"))
				.build());
		corpus.put("indexOf found", QueryBuilder.from(personClass)
				.where(Expressions.path(personName).indexOf("o").eq(1))
				.build());
		corpus.put("indexOf absent", QueryBuilder.from(personClass)
				.where(Expressions.path(personName).indexOf("o").eq(-1))
				.build());
		corpus.put("substring window", QueryBuilder.from(personClass)
				.where(Expressions.path(personName).substring(1, 3).eq("aro"))
				.build());
		corpus.put("substring negative start", QueryBuilder.from(personClass)
				.where(Expressions.path(personName).substring(-2).eq("ce"))
				.build());
		corpus.put("substring beyond end", QueryBuilder.from(personClass)
				.where(Expressions.path(personName).substring(99).eq(""))
				.build());

		for (Map.Entry<String, Query> entry : corpus.entrySet()) {
			List<Object> backendNames;
			try (QueryResult result = queryable(createBackendResourceSet()).query(entry.getValue())) {
				backendNames = result.objects().map(person -> person.eGet(personName))
						.map(Object.class::cast).toList();
			}
			List<Object> memoryNames;
			try (QueryResult result = MemoryQueries.execute(entry.getValue(), oracle, null)) {
				memoryNames = result.objects().map(person -> person.eGet(personName))
						.map(Object.class::cast).toList();
			}
			assertThat(backendNames)
					.as("backend and memory oracle must agree on '%s'", entry.getKey())
					.containsExactlyInAnyOrderElementsOf(memoryNames);
		}

		Query count = QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).gt(30))
				.countOnly()
				.build();
		try (QueryResult backend = queryable(createBackendResourceSet()).query(count);
				QueryResult memory = MemoryQueries.execute(count, oracle, null)) {
			assertThat(backend.count()).as("count must agree with the memory oracle")
					.isEqualTo(memory.count());
		}
	}

	// ------------------------------------------------------------ command TCK (CUD v1)

	private CommandResource commands(ResourceSet resourceSet) {
		return (CommandResource) resourceSet.createResource(uriFor("Person"));
	}

	@Test
	public void commandInsertPersistsThePayload() throws Exception {
		InsertCommand insert = CommandFactory.eINSTANCE.createInsertCommand();
		insert.getObjects().add(newPerson(1, "Alice", 30));
		insert.getObjects().add(newPerson(2, "Bob", 40));

		long affected = commands(createBackendResourceSet()).execute(insert);
		assertThat(affected).isEqualTo(2);
		// the command still owns its payload (execution works on copies)
		assertThat(insert.getObjects()).hasSize(2);

		Resource loaded = loadAll(createBackendResourceSet(), "Person");
		assertThat(loaded.getContents()).hasSize(2);
	}

	@Test
	public void commandDeleteBySelector() throws Exception {
		saveQueryFixture();
		DeleteCommand delete = CommandFactory.eINSTANCE.createDeleteCommand();
		delete.setSelector(QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).ge(40))
				.build());

		long affected = commands(createBackendResourceSet()).execute(delete);
		assertThat(affected).isEqualTo(2);

		Resource remaining = loadAll(createBackendResourceSet(), "Person");
		assertThat(remaining.getContents()).hasSize(1);
		assertThat(remaining.getContents().get(0).eGet(personName)).isEqualTo("Alice");
	}

	private ChangeEntry changeEntry(DeltaKind kind, EStructuralFeature feature) {
		ChangeEntry entry = StreamFactory.eINSTANCE.createChangeEntry();
		entry.setKind(kind);
		entry.setFeatureId(personClass.getFeatureID(feature));
		return entry;
	}

	private UpdateCommand updateCommand(Query selector, ChangeEntry... entries) {
		UpdateCommand update = CommandFactory.eINSTANCE.createUpdateCommand();
		update.setSelector(selector);
		ChangeSet template = StreamFactory.eINSTANCE.createChangeSet();
		for (ChangeEntry entry : entries) {
			template.getEntries().add(entry);
		}
		update.setTemplate(template);
		return update;
	}

	@Test
	public void commandUpdateAppliesTheTemplatePerMatch() throws Exception {
		saveQueryFixture();
		ChangeEntry setName = changeEntry(DeltaKind.SET, personName);
		setName.setValueNew("Robert");
		ChangeEntry setAge = changeEntry(DeltaKind.SET, personAge);
		setAge.setValueNew("41");
		UpdateCommand update = updateCommand(
				QueryBuilder.from(personClass)
						.where(Expressions.path(personName).eq("Bob"))
						.build(),
				setName, setAge);

		long affected = commands(createBackendResourceSet()).execute(update);
		assertThat(affected).isEqualTo(1);

		Resource loaded = loadAll(createBackendResourceSet(), "Person");
		Map<Object, Object> byName = new HashMap<>();
		loaded.getContents().forEach(person -> byName.put(person.eGet(personName), person.eGet(personAge)));
		assertThat(byName).containsOnlyKeys("Alice", "Robert", "Carol");
		assertThat(((Number) byName.get("Robert")).intValue()).isEqualTo(41);
		assertThat(((Number) byName.get("Alice")).intValue()).isEqualTo(30);
	}

	@Test
	public void commandUpdateMatchesEveryObjectOnEmptySelector() throws Exception {
		saveQueryFixture();
		ChangeEntry setAge = changeEntry(DeltaKind.SET, personAge);
		setAge.setValueNew("18");
		UpdateCommand update = updateCommand(QueryBuilder.from(personClass).build(), setAge);

		long affected = commands(createBackendResourceSet()).execute(update);
		assertThat(affected).isEqualTo(3);

		Resource loaded = loadAll(createBackendResourceSet(), "Person");
		assertThat(loaded.getContents())
				.allSatisfy(person -> assertThat(((Number) person.eGet(personAge)).intValue()).isEqualTo(18));
	}

	@Test
	public void commandUpdateUnsetClearsTheValue() throws Exception {
		saveQueryFixture();
		UpdateCommand update = updateCommand(
				QueryBuilder.from(personClass)
						.where(Expressions.path(personAge).eq(50))
						.build(),
				changeEntry(DeltaKind.UNSET, personName));

		long affected = commands(createBackendResourceSet()).execute(update);
		assertThat(affected).isEqualTo(1);

		Resource loaded = loadAll(createBackendResourceSet(), "Person");
		List<Object> names = loaded.getContents().stream().map(person -> person.eGet(personName)).toList();
		assertThat(names).contains("Alice", "Bob");
		assertThat(names).filteredOn(java.util.Objects::isNull).hasSize(1);
	}

	@Test
	public void commandUpdateRefusesBadTemplates() throws Exception {
		saveQueryFixture();
		Query all = QueryBuilder.from(personClass).build();
		CommandResource resource = commands(createBackendResourceSet());

		// lifecycle kinds belong to Insert/Delete commands, not templates
		ChangeEntry create = changeEntry(DeltaKind.CREATE, personName);
		assertThatThrownBy(() -> resource.execute(updateCommand(all, create)))
				.isInstanceOf(IOException.class)
				.hasMessageContaining("Unsupported template kind");

		// reference patching is not part of the v1 engine
		ChangeEntry onReference = changeEntry(DeltaKind.SET, personAddresses);
		assertThatThrownBy(() -> resource.execute(updateCommand(all, onReference)))
				.isInstanceOf(IOException.class)
				.hasMessageContaining("reference");

		// an empty template patches nothing — refused, not a silent no-op
		assertThatThrownBy(() -> resource.execute(updateCommand(all)))
				.isInstanceOf(IOException.class)
				.hasMessageContaining("no entries");

		// nothing may have been changed by the refused commands
		Resource loaded = loadAll(createBackendResourceSet(), "Person");
		assertThat(loaded.getContents()).hasSize(3);
	}

	@Test
	public void commandSelectorMustBeAPlainFilter() throws Exception {
		saveQueryFixture();
		DeleteCommand delete = CommandFactory.eINSTANCE.createDeleteCommand();
		delete.setSelector(QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).ge(40))
				.top(1)
				.build());

		CommandResource resource = commands(createBackendResourceSet());
		assertThatThrownBy(() -> resource.execute(delete))
				.isInstanceOf(IOException.class)
				.hasMessageContaining("plain filters");
	}
}
