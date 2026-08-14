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
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.model.command.CommandFactory;
import org.eclipse.fennec.model.command.DeleteCommand;
import org.eclipse.fennec.model.command.InsertCommand;
import org.eclipse.fennec.model.command.UpdateCommand;
import org.eclipse.fennec.model.expression.GeoBox;
import org.eclipse.fennec.model.expression.GeoPointLiteral;
import org.eclipse.fennec.model.expression.GeoSubject;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.QueryFactory;
import org.eclipse.fennec.model.query.TopStage;
import org.eclipse.fennec.model.query.builder.Expressions;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.model.stream.ChangeEntry;
import org.eclipse.fennec.model.stream.ChangeSet;
import org.eclipse.fennec.model.stream.DeltaKind;
import org.eclipse.fennec.model.stream.StreamFactory;
import org.eclipse.fennec.persistence.capabilities.CommandCapabilities;
import org.eclipse.fennec.persistence.capabilities.CommandFeature;
import org.eclipse.fennec.persistence.capabilities.PersistenceCapabilities;
import org.eclipse.fennec.persistence.capabilities.StoreFeature;
import org.eclipse.fennec.persistence.pushstreams.PersistencePushStreams;
import org.eclipse.fennec.persistence.query.api.CommandResource;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.eclipse.fennec.persistence.query.api.QueryResultRow;
import org.eclipse.fennec.persistence.query.api.QueryShape;
import org.eclipse.fennec.persistence.query.api.QueryableResource;
import org.eclipse.fennec.persistence.query.derived.DerivedReferenceCompiler;
import org.eclipse.fennec.persistence.query.derived.QueryBackedSettingDelegateFactory;
import org.eclipse.fennec.persistence.query.memory.MemoryQueries;
import org.eclipse.fennec.persistence.query.support.CommandTransaction;
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
	protected EStructuralFeature personBirthday;
	protected EStructuralFeature personFavoriteColor;
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
		personBirthday = personClass.getEStructuralFeature("birthday");
		personFavoriteColor = personClass.getEStructuralFeature("favoriteColor");
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
	 * The TCK model to run against, as a resource next to this class. The default model
	 * uses int-typed EMF ids; the String-id bindings override this with
	 * {@code tck-string.ecore}.
	 */
	protected String tckModelPath() {
		return "tck.ecore";
	}

	/** Loads the TCK model from the bundle/classpath (issue #99 — no source checkout needed). */
	protected EPackage loadTckModel() throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new XMIResourceFactoryImpl());
		Resource resource = resourceSet.createResource(URI.createURI(tckModelPath()));
		try (InputStream stream = AbstractPersistenceTCK.class.getResourceAsStream(tckModelPath())) {
			assertThat(stream).as("TCK model '%s' must be a classpath resource next to %s",
					tckModelPath(), AbstractPersistenceTCK.class.getSimpleName()).isNotNull();
			resource.load(stream, null);
		}
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

	/**
	 * Drops whatever second-level cache the backend keeps, so a read really goes to the store.
	 * <p>
	 * A fresh {@link ResourceSet} is not enough on its own: EclipseLink's shared object cache
	 * lives on the {@code EntityManagerFactory} and outlives every ResourceSet, so a "round
	 * trip" could be answered entirely from memory and prove nothing about what was written.
	 * Bindings whose backend caches must override this; the default is a no-op because Mongo
	 * keeps no such cache.
	 */
	protected void evictBackendCaches() {
		// no-op by default
	}

	/**
	 * Loads all objects of the type into a resource of the given ResourceSet, after dropping
	 * backend caches — so this is the only read path the TCK offers, and it cannot
	 * accidentally be served from a cache (see {@link #evictBackendCaches()}).
	 */
	protected Resource loadAll(ResourceSet resourceSet, String typeName) throws IOException {
		evictBackendCaches();
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

	/**
	 * Containment is ownership, so removing a child and saving the parent must destroy the
	 * child — the plain, single-document case that every backend owes (issue #142).
	 * <p>
	 * Asserted through the reloaded parent rather than by counting rows or documents, because
	 * that is the only backend-neutral observation: JPA gives the child a row of its own,
	 * Mongo embeds it in the parent's document, so there is no shared place to count. The
	 * storage-level proof belongs in the backend suites.
	 */
	@Test
	public void containmentOrphanIsRemovedOnSave() throws Exception {
		EObject person = newPerson(1, "Emil", 30);
		listOf(person, personAddresses).add(newAddress(11, "Main Street 1", "Jena"));
		listOf(person, personAddresses).add(newAddress(12, "Second Street 2", "Gera"));

		ResourceSet writeSet = createBackendResourceSet();
		save(writeSet, "Person", person);

		// drop one child through the very resource that wrote it, then save again
		Resource written = writeSet.getResource(uriFor("Person"), false);
		EObject owner = findById(written, "1");
		assertThat(owner).as("the saved person is addressable in its resource").isNotNull();
		List<EObject> owned = listOf(owner, personAddresses);
		owned.removeIf(address -> "Main Street 1".equals(address.eGet(addressStreet)));
		assertThat(owned).hasSize(1);
		written.save(null);

		ResourceSet readSet = createBackendResourceSet();
		EObject loaded = findById(loadAll(readSet, "Person"), "1");
		assertThat(loaded).isNotNull();
		List<EObject> addresses = listOf(loaded, personAddresses);
		assertThat(addresses).as("the dropped child must be gone, not merely unreferenced")
				.hasSize(1);
		assertThat(addresses.get(0).eGet(addressStreet)).isEqualTo("Second Street 2");
	}

	/**
	 * The inverse guard of the orphan cases: updating a parent while <em>keeping</em> its
	 * children untouched must not disturb them. This is the case a lifecycle fix can break
	 * silently — a backend that treats "collection differs by instance identity" as
	 * "collection changed" would delete and re-insert (or worse, just delete) children on
	 * every parent update.
	 */
	@Test
	public void updatingParentKeepsUntouchedContainmentChildren() throws Exception {
		EObject person = newPerson(1, "Emil", 30);
		listOf(person, personAddresses).add(newAddress(11, "Main Street 1", "Jena"));
		listOf(person, personAddresses).add(newAddress(12, "Second Street 2", "Gera"));

		ResourceSet writeSet = createBackendResourceSet();
		save(writeSet, "Person", person);

		// touch only the parent's own attribute, children stay exactly as they are
		Resource written = writeSet.getResource(uriFor("Person"), false);
		EObject owner = findById(written, "1");
		owner.eSet(personName, "Emil Renamed");
		written.save(null);

		ResourceSet readSet = createBackendResourceSet();
		EObject loaded = findById(loadAll(readSet, "Person"), "1");
		assertThat(loaded).isNotNull();
		assertThat(loaded.eGet(personName)).isEqualTo("Emil Renamed");
		assertThat(listOf(loaded, personAddresses))
				.as("children untouched by the update must survive it")
				.extracting(a -> a.eGet(addressStreet))
				.containsExactlyInAnyOrder("Main Street 1", "Second Street 2");
	}

	/**
	 * The counterpart of the removal case: a child <em>added</em> to an already persisted
	 * parent has to reach the store. Same code path, opposite direction — if collection
	 * changes on an existing parent are dropped, this fails too, and the severity is not
	 * "orphans linger" but "containment updates are lost".
	 */
	@Test
	public void containmentChildAddedToAnExistingParentIsPersisted() throws Exception {
		EObject person = newPerson(1, "Emil", 30);
		listOf(person, personAddresses).add(newAddress(11, "Main Street 1", "Jena"));

		ResourceSet writeSet = createBackendResourceSet();
		save(writeSet, "Person", person);

		Resource written = writeSet.getResource(uriFor("Person"), false);
		EObject owner = findById(written, "1");
		listOf(owner, personAddresses).add(newAddress(12, "Second Street 2", "Gera"));
		written.save(null);

		ResourceSet readSet = createBackendResourceSet();
		EObject loaded = findById(loadAll(readSet, "Person"), "1");
		assertThat(loaded).isNotNull();
		assertThat(listOf(loaded, personAddresses))
				.as("a child added to an existing parent must be persisted")
				.extracting(a -> a.eGet(addressStreet))
				.containsExactlyInAnyOrder("Main Street 1", "Second Street 2");
	}

	/**
	 * Clearing the containment feature entirely — the same ownership rule with nothing left
	 * to keep.
	 */
	@Test
	public void clearingContainmentDestroysAllChildren() throws Exception {
		EObject person = newPerson(1, "Emil", 30);
		listOf(person, personAddresses).add(newAddress(11, "Main Street 1", "Jena"));

		ResourceSet writeSet = createBackendResourceSet();
		save(writeSet, "Person", person);

		Resource written = writeSet.getResource(uriFor("Person"), false);
		EObject owner = findById(written, "1");
		listOf(owner, personAddresses).clear();
		written.save(null);

		ResourceSet readSet = createBackendResourceSet();
		EObject loaded = findById(loadAll(readSet, "Person"), "1");
		assertThat(loaded).isNotNull();
		assertThat(listOf(loaded, personAddresses))
				.as("clearing the containment feature must destroy the children")
				.isEmpty();
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

	/** The fixture birthdays — fixed UTC instants (see the temporal contract, issue #79). */
	static final Instant ALICE_BIRTHDAY = Instant.parse("1996-03-15T10:30:45Z");
	static final Instant BOB_BIRTHDAY = Instant.parse("1986-07-01T23:59:59Z");
	static final Instant CAROL_BIRTHDAY = Instant.parse("1976-12-31T00:00:05Z");

	static {
		// The temporal contract (issue #79) is UTC-normative. The zone-less H2
		// TIMESTAMP carries the wall-clock of the writing session, and H2 fixes the
		// session zone when the connection opens — so the whole TCK JVM is pinned to
		// UTC before any backend connects (CI runners are UTC anyway; production
		// deployments should run UTC or accept local-wall-clock extraction on SQL).
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	/**
	 * Saves the standard query fixture: three persons with UTC birthdays, Bob with two
	 * addresses, Alice and Bob with favourite colors (Carol's stays unset).
	 */
	private void saveQueryFixture() throws Exception {
		EObject alice = newPerson(1, "Alice", 30);
		alice.eSet(personBirthday, Date.from(ALICE_BIRTHDAY));
		alice.eSet(personFavoriteColor, colorLiteral("GREEN"));
		EObject bob = newPerson(2, "Bob", 40);
		bob.eSet(personBirthday, Date.from(BOB_BIRTHDAY));
		bob.eSet(personFavoriteColor, colorLiteral("BLUE"));
		listOf(bob, personAddresses).add(newAddress(21, "Main Street 5", "Jena"));
		listOf(bob, personAddresses).add(newAddress(22, "Side Road 9", "Gera"));
		EObject carol = newPerson(3, "Carol", 50);
		carol.eSet(personBirthday, Date.from(CAROL_BIRTHDAY));
		ResourceSet writeSet = createBackendResourceSet();
		save(writeSet, "Person", alice, bob, carol);
	}

	/** The dynamic-EMF instance value of a Color literal. */
	private Object colorLiteral(String name) {
		EEnum colors = (EEnum) tckPackage.getEClassifier("Color");
		return colors.getEEnumLiteral(name).getInstance();
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
	public void queryNotOverNullableComparisonExcludesNullRows() throws Exception {
		// SQL 3VL pinned by issue #94 (Mongo aligned by #97): not(birthday = X) over a
		// NULL birthday is NOT UNKNOWN = UNKNOWN — excluded, it does not flip to a match
		EObject alice = newPerson(1, "Alice", 30);
		alice.eSet(personBirthday, Date.from(ALICE_BIRTHDAY));
		EObject dave = newPerson(4, "Dave", 25); // no birthday — the nullable column
		save(createBackendResourceSet(), "Person", alice, dave);

		Query query = QueryBuilder.from(personClass)
				.where(Expressions.not(
						Expressions.path(personBirthday).eq(Date.from(BOB_BIRTHDAY))))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Alice");
		}

		// the divergence exists without Not too: a plain NE over null is UNKNOWN
		Query ne = QueryBuilder.from(personClass)
				.where(Expressions.path(personBirthday).ne(Date.from(BOB_BIRTHDAY)))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(ne)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Alice");
		}
	}

	@Test
	public void queryNegationDistributesThreeValuedOverJunctions() throws Exception {
		EObject alice = newPerson(1, "Alice", 30);
		alice.eSet(personBirthday, Date.from(ALICE_BIRTHDAY));
		EObject dave = newPerson(4, "Dave", 25); // no birthday
		save(createBackendResourceSet(), "Person", alice, dave);

		// FALSE dominates AND over UNKNOWN: Dave's (UNKNOWN and false) is FALSE, so
		// not(...) is TRUE — a blanket non-null guard around the Not would lose him
		Query notOverAnd = QueryBuilder.from(personClass)
				.where(Expressions.not(Expressions.and(
						Expressions.path(personBirthday).eq(Date.from(BOB_BIRTHDAY)),
						Expressions.path(personAge).ge(40))))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(notOverAnd)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactlyInAnyOrder("Alice", "Dave");
		}

		// UNKNOWN survives OR against FALSE: Dave's not(UNKNOWN or false) stays UNKNOWN
		Query notOverOr = QueryBuilder.from(personClass)
				.where(Expressions.not(Expressions.or(
						Expressions.path(personBirthday).eq(Date.from(BOB_BIRTHDAY)),
						Expressions.path(personAge).ge(40))))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(notOverOr)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Alice");
		}
	}

	@Test
	public void queryStringLiteralCoercesAgainstEnumFeature() throws Exception {
		saveQueryFixture();
		// OData transports enum values as quoted strings (issue #93): a StringLiteral
		// compared against an EEnum-typed feature must resolve like an EnumLiteral
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.path(personFavoriteColor).eq("GREEN"))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Alice");
		}

		// an unknown literal must be refused, not silently match nothing
		Query unknown = QueryBuilder.from(personClass)
				.where(Expressions.path(personFavoriteColor).eq("PURPLE"))
				.build();
		assertThatThrownBy(() -> queryable(createBackendResourceSet()).query(unknown))
				.hasMessageContaining("PURPLE");
	}

	@Test
	public void queryPlainFilterOnIdAttribute() throws Exception {
		saveQueryFixture();
		// ID equality compiles to an EclipseLink ReadObjectQuery, which supports no
		// scrollable cursor (issue #91) — must behave like any other predicate.
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.path(personClass.getEIDAttribute()).eq(idValue(personClass, 2)))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Bob");
		}
	}

	@Test
	public void queryMissOnIdAttributeIsEmpty() throws Exception {
		saveQueryFixture();
		// the no-match branch of the ReadObjectQuery path (issue #91): empty, not null
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.path(personClass.getEIDAttribute()).eq(idValue(personClass, 99)))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			assertThat(result.objects()).isEmpty();
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
	public void queryNumericFunctions() throws Exception {
		saveQueryFixture();
		// age / 4 → Alice 7.5, Bob 10, Carol 12.5. ROUND is half away from zero:
		// 12.5 → 13 — the banker's rounding of Mongo's $round would yield 12
		Query round = QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).dividedBy(4).round().eq(13))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(round)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Carol");
		}
		Query floor = QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).dividedBy(4).floor().eq(7))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(floor)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Alice");
		}
		Query ceiling = QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).dividedBy(4).ceiling().eq(8))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(ceiling)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Alice");
		}
	}

	@Test
	public void queryTemporalFunctions() throws Exception {
		saveQueryFixture();
		// Alice 1996-03-15T10:30:45Z, Bob 1986-07-01T23:59:59Z, Carol 1976-12-31T00:00:05Z
		Query year = QueryBuilder.from(personClass)
				.where(Expressions.path(personBirthday).year().eq(1996))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(year)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Alice");
		}
		Query month = QueryBuilder.from(personClass)
				.where(Expressions.path(personBirthday).month().eq(7))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(month)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Bob");
		}
		Query day = QueryBuilder.from(personClass)
				.where(Expressions.path(personBirthday).day().eq(31))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(day)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Carol");
		}
		// time parts are UTC-normative: 23h only holds in UTC wall-clock
		Query hour = QueryBuilder.from(personClass)
				.where(Expressions.path(personBirthday).hour().eq(23))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(hour)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Bob");
		}
		Query minute = QueryBuilder.from(personClass)
				.where(Expressions.path(personBirthday).minute().eq(30))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(minute)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Alice");
		}
		Query second = QueryBuilder.from(personClass)
				.where(Expressions.path(personBirthday).second().eq(5))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(second)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Carol");
		}
	}

	/** Whether the backend supports type predicates (TYPE_CHECK/TYPE_CAST, issues #80/#88). */
	protected boolean supportsTypePredicates() {
		return true;
	}

	@Test
	public void queryTypeCheckAndTreat() throws Exception {
		EClass vehicleClass = (EClass) tckPackage.getEClassifier("Vehicle");
		EClass carClass = (EClass) tckPackage.getEClassifier("Car");
		EClass motorcycleClass = (EClass) tckPackage.getEClassifier("Motorcycle");
		EStructuralFeature horsepower = carClass.getEStructuralFeature("horsepower");
		EStructuralFeature label = vehicleClass.getEStructuralFeature("label");

		Query isCar = QueryBuilder.from(vehicleClass)
				.where(Expressions.isOf(carClass))
				.build();
		Query strongCar = QueryBuilder.from(vehicleClass)
				.where(Expressions.pathAs(carClass, horsepower).gt(100))
				.build();

		if (!supportsTypePredicates()) {
			// documented capability refusal — never silent (issue #80)
			ResourceSet refusalSet = createBackendResourceSet();
			QueryableResource resource = (QueryableResource) refusalSet.createResource(uriFor("Vehicle"));
			assertThatThrownBy(() -> resource.query(isCar).close())
					.isInstanceOf(IOException.class)
					.hasMessageContaining("TYPE_CHECK");
			assertThatThrownBy(() -> resource.query(strongCar).close())
					.isInstanceOf(IOException.class)
					.hasMessageContaining("TYPE_CAST");
			return;
		}

		EObject beetle = newVehicle(carClass, 1, "Beetle");
		beetle.eSet(horsepower, 50);
		EObject brutus = newVehicle(carClass, 2, "Brutus");
		brutus.eSet(horsepower, 300);
		EObject bonnie = newVehicle(motorcycleClass, 3, "Bonnie");
		bonnie.eSet(motorcycleClass.getEStructuralFeature("cc"), 900);
		// one hierarchy container: single table on JPA, one collection on Mongo —
		// the codec type discriminator resolves the concrete subtype on decode (#88)
		ResourceSet writeSet = createBackendResourceSet();
		save(writeSet, "Vehicle", beetle, brutus, bonnie);

		ResourceSet readSet = createBackendResourceSet();
		QueryableResource vehicles = (QueryableResource) readSet.createResource(uriFor("Vehicle"));
		try (QueryResult result = vehicles.query(isCar)) {
			assertThat(result.objects().map(vehicle -> vehicle.eGet(label)))
					.containsExactlyInAnyOrder("Beetle", "Brutus");
		}
		// treat: motorcycles yield null on the cast path — excluded, not an error
		try (QueryResult result = vehicles.query(strongCar)) {
			assertThat(result.objects().map(vehicle -> vehicle.eGet(label)))
					.containsExactly("Brutus");
		}
		Query notCar = QueryBuilder.from(vehicleClass)
				.where(Expressions.not(Expressions.isOf(carClass)))
				.build();
		try (QueryResult result = vehicles.query(notCar)) {
			assertThat(result.objects().map(vehicle -> vehicle.eGet(label)))
					.containsExactly("Bonnie");
		}

		// differential against the memory oracle (kind-of semantics is normative)
		List<EObject> oracle = List.of(
				copyVehicle(carClass, 1, "Beetle", horsepower, 50),
				copyVehicle(carClass, 2, "Brutus", horsepower, 300),
				copyVehicle(motorcycleClass, 3, "Bonnie", motorcycleClass.getEStructuralFeature("cc"), 900));
		for (Query query : List.of(isCar, strongCar, notCar)) {
			List<Object> backendLabels;
			try (QueryResult result = vehicles.query(query)) {
				backendLabels = result.objects().map(vehicle -> vehicle.eGet(label))
						.map(Object.class::cast).toList();
			}
			try (QueryResult result = MemoryQueries.execute(query, oracle, null)) {
				assertThat(backendLabels)
						.as("backend and memory oracle must agree on type predicates")
						.containsExactlyInAnyOrderElementsOf(result.objects()
								.map(vehicle -> vehicle.eGet(label)).map(Object.class::cast).toList());
			}
		}
	}

	private EObject newVehicle(EClass type, int id, String label) {
		EObject vehicle = EcoreUtil.create(type);
		vehicle.eSet(type.getEStructuralFeature("vid"), idValue(type, id));
		vehicle.eSet(type.getEStructuralFeature("label"), label);
		return vehicle;
	}

	private EObject copyVehicle(EClass type, int id, String label, EStructuralFeature extra, int value) {
		EObject vehicle = newVehicle(type, id, label);
		vehicle.eSet(extra, value);
		return vehicle;
	}

	@Test
	public void queryCollectionCounts() throws Exception {
		saveQueryFixture();
		// plain: Bob has two addresses, Alice and Carol none
		Query plain = QueryBuilder.from(personClass)
				.where(Expressions.count(Expressions.propertyPath(personAddresses)).ge(2))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(plain)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Bob");
		}
		Query empty = QueryBuilder.from(personClass)
				.where(Expressions.count(Expressions.propertyPath(personAddresses)).eq(0))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(empty)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactlyInAnyOrder("Alice", "Carol");
		}
		// filtered: exactly one Main-Street address — supported on JPA, refused on Mongo
		Query filtered = QueryBuilder.from(personClass)
				.where(Expressions.count(Expressions.propertyPath(personAddresses),
						a -> a.path(addressStreet).startsWith("Main")).eq(1))
				.build();
		if (supportsFilteredCollectionCounts()) {
			try (QueryResult result = queryable(createBackendResourceSet()).query(filtered)) {
				assertThat(result.objects().map(person -> person.eGet(personName)))
						.containsExactly("Bob");
			}
		} else {
			QueryableResource resource = queryable(createBackendResourceSet());
			assertThatThrownBy(() -> resource.query(filtered).close())
					.isInstanceOf(IOException.class)
					.hasMessageContaining("COLLECTION_COUNT_FILTERED");
		}
	}

	/** Whether the backend supports predicate-filtered collection counts (issue #81). */
	protected boolean supportsFilteredCollectionCounts() {
		return true;
	}

	@Test
	public void queryPipelineComputeAndHaving() throws Exception {
		saveQueryFixture();
		// Dora shares age 30 with Alice — the HAVING keeps only that group
		ResourceSet extraSet = createBackendResourceSet();
		save(extraSet, "Person", newPerson(4, "Dora", 30));

		Query query = QueryBuilder.from(personClass)
				.groupBy(personAge)
				.sum("total", personAge)
				.countOf("cnt")
				.computeAs("avgAge", Expressions.div(Expressions.aliasRef("total"),
						Expressions.aliasRef("cnt")).toExpression())
				.having(Expressions.aliasRef("cnt").ge(2))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			List<QueryResultRow> rows = result.rows().toList();
			assertThat(rows).hasSize(1);
			QueryResultRow row = rows.get(0);
			assertThat(((Number) row.get("age")).intValue()).isEqualTo(30);
			assertThat(((Number) row.get("total")).longValue()).isEqualTo(60);
			assertThat(((Number) row.get("cnt")).longValue()).isEqualTo(2);
			assertThat(((Number) row.get("avgAge")).doubleValue()).isEqualTo(30.0);
		}

		// differential: the memory oracle computes the same row
		List<EObject> oracle = List.of(
				newPerson(1, "Alice", 30), newPerson(2, "Bob", 40),
				newPerson(3, "Carol", 50), newPerson(4, "Dora", 30));
		try (QueryResult memory = MemoryQueries.execute(query, oracle, null)) {
			List<QueryResultRow> rows = memory.rows().toList();
			assertThat(rows).hasSize(1);
			assertThat(((Number) rows.get(0).get("avgAge")).doubleValue()).isEqualTo(30.0);
			assertThat(((Number) rows.get(0).get("cnt")).longValue()).isEqualTo(2);
		}
	}

	@Test
	public void queryPreGroupComputeFeedsGroupKeysAndSources() throws Exception {
		saveQueryFixture();
		// Dora(35) shares the floor(age/10)=3 decade with Alice(30) — the pre-group
		// compute alias feeds both the group key and an aggregate source (issue #87)
		save(createBackendResourceSet(), "Person", newPerson(4, "Dora", 35));

		Query query = QueryBuilder.from(personClass)
				.computeAs("dec", Expressions.path(personAge).dividedBy(10).floor().toExpression())
				.groupByAs("decade", Expressions.aliasRef("dec").toExpression())
				.sum("decSum", Expressions.aliasRef("dec").toExpression())
				.countOf("cnt")
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			Map<Integer, QueryResultRow> byDecade = result.rows()
					.collect(Collectors.toMap(row -> ((Number) row.get("decade")).intValue(), row -> row));
			assertThat(byDecade).containsOnlyKeys(3, 4, 5);
			assertThat(((Number) byDecade.get(3).get("cnt")).longValue()).isEqualTo(2);
			assertThat(((Number) byDecade.get(3).get("decSum")).intValue()).isEqualTo(6);
			assertThat(((Number) byDecade.get(4).get("cnt")).longValue()).isEqualTo(1);
			assertThat(((Number) byDecade.get(5).get("cnt")).longValue()).isEqualTo(1);
		}

		// differential: the memory oracle computes the same buckets
		List<EObject> oracle = List.of(
				newPerson(1, "Alice", 30), newPerson(2, "Bob", 40),
				newPerson(3, "Carol", 50), newPerson(4, "Dora", 35));
		try (QueryResult memory = MemoryQueries.execute(query, oracle, null)) {
			Map<Integer, QueryResultRow> byDecade = memory.rows()
					.collect(Collectors.toMap(row -> ((Number) row.get("decade")).intValue(), row -> row));
			assertThat(byDecade).containsOnlyKeys(3, 4, 5);
			assertThat(((Number) byDecade.get(3).get("cnt")).longValue()).isEqualTo(2);
			assertThat(((Number) byDecade.get(3).get("decSum")).intValue()).isEqualTo(6);
		}
	}

	@Test
	public void queryGroupByExpressionKeyWithoutCompute() throws Exception {
		saveQueryFixture();
		// the expression key needs no pipeline compute (issue #87): floor(age/25)
		// buckets Alice(30)/Bob(40) together, avg aggregates over an expression source
		Query query = QueryBuilder.from(personClass)
				.groupByAs("band", Expressions.path(personAge).dividedBy(25).floor().toExpression())
				.avg("halfAvg", Expressions.path(personAge).dividedBy(2).toExpression())
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			Map<Integer, QueryResultRow> byBand = result.rows()
					.collect(Collectors.toMap(row -> ((Number) row.get("band")).intValue(), row -> row));
			assertThat(byBand).containsOnlyKeys(1, 2);
			// band 1 = {30, 40}: avg(15, 20) = 17.5; band 2 = {50}: avg(25) = 25
			assertThat(((Number) byBand.get(1).get("halfAvg")).doubleValue()).isEqualTo(17.5);
			assertThat(((Number) byBand.get(2).get("halfAvg")).doubleValue()).isEqualTo(25.0);
		}

		List<EObject> oracle = List.of(
				newPerson(1, "Alice", 30), newPerson(2, "Bob", 40), newPerson(3, "Carol", 50));
		try (QueryResult memory = MemoryQueries.execute(query, oracle, null)) {
			Map<Integer, QueryResultRow> byBand = memory.rows()
					.collect(Collectors.toMap(row -> ((Number) row.get("band")).intValue(), row -> row));
			assertThat(byBand).containsOnlyKeys(1, 2);
			assertThat(((Number) byBand.get(1).get("halfAvg")).doubleValue()).isEqualTo(17.5);
			assertThat(((Number) byBand.get(2).get("halfAvg")).doubleValue()).isEqualTo(25.0);
		}
	}

	@Test
	public void querySortByExpression() throws Exception {
		saveQueryFixture();
		// -age ascending = age descending: Carol, Bob, Alice (issue #84)
		Query query = QueryBuilder.from(personClass)
				.orderByAsc(Expressions.neg(Expressions.path(personAge)).toExpression())
				.build();
		if (!supportsSortExpressions()) {
			QueryableResource resource = queryable(createBackendResourceSet());
			assertThatThrownBy(() -> resource.query(query).close())
					.isInstanceOf(IOException.class)
					.hasMessageContaining("SORT_EXPRESSION");
			return;
		}
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Carol", "Bob", "Alice");
		}
	}

	/** Whether the backend supports ordering by arbitrary value expressions (issue #84). */
	protected boolean supportsSortExpressions() {
		return true;
	}

	@Test
	public void queryAggregationSortsByOutputAlias() throws Exception {
		saveQueryFixture();
		// OData: $apply=groupby((name),aggregate(age with average as avgAge))&$orderby=avgAge desc
		// — a bare AliasRef key is a plain output-column sort on EVERY backend (issue
		// #102), no SORT_EXPRESSION involved: $sort after $group is native on Mongo
		Query query = QueryBuilder.from(personClass)
				.groupBy(personName)
				.avg("avgAge", personAge)
				.orderByDesc(Expressions.aliasRef("avgAge").toExpression())
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			assertThat(result.rows().map(row -> row.get("name")))
					.containsExactly("Carol", "Bob", "Alice");
		}
	}

	@Test
	public void queryExpandPrefetchesMultiSegmentPaths() throws Exception {
		// multi-segment expand (issue #95): single-valued segments fetch-join as an
		// aliased chain — the result must stay correct and fully navigable
		EObject company = newCompany(21, "Data In Motion");
		EObject alice = newPerson(1, "Alice", 30);
		EObject bob = newPerson(2, "Bob", 40);
		bob.eSet(personBestFriend, alice);
		ResourceSet writeSet = createBackendResourceSet();
		Resource companyResource = writeSet.createResource(uriFor("Company"));
		companyResource.getContents().add(company);
		alice.eSet(personEmployer, company);
		companyResource.save(null);
		save(writeSet, "Person", alice, bob);

		Query query = QueryBuilder.from(personClass)
				.where(Expressions.path(personName).eq("Bob"))
				.expand(personBestFriend, personEmployer)
				.build();
		if (!supportsExpand()) {
			QueryableResource resource = queryable(createBackendResourceSet());
			assertThatThrownBy(() -> resource.query(query).close())
					.isInstanceOf(IOException.class)
					.hasMessageContaining("EXPAND");
			return;
		}
		ResourceSet readSet = createBackendResourceSet();
		try (QueryResult result = queryable(readSet).query(query)) {
			List<EObject> persons = result.objects().toList();
			assertThat(persons).hasSize(1);
			EObject bestFriend = resolved((EObject) persons.get(0).eGet(personBestFriend), readSet);
			assertThat(bestFriend.eGet(personName)).isEqualTo("Alice");
			EObject employer = resolved((EObject) bestFriend.eGet(personEmployer), readSet);
			assertThat(employer.eGet(companyName)).isEqualTo("Data In Motion");
		}
	}

	@Test
	public void queryExpandToManyKeepsMaxResultsCounting() throws Exception {
		// a collection fetch join would count joined SQL rows against setMaxResults and
		// truncate Bob's addresses — the to-many expand must batch instead (issue #95)
		saveQueryFixture();
		Query query = QueryBuilder.from(personClass)
				.orderByAsc(personName)
				.top(2)
				.expand(personAddresses)
				.build();
		if (!supportsExpand()) {
			QueryableResource resource = queryable(createBackendResourceSet());
			assertThatThrownBy(() -> resource.query(query).close())
					.isInstanceOf(IOException.class)
					.hasMessageContaining("EXPAND");
			return;
		}
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			List<EObject> persons = result.objects().toList();
			assertThat(persons.stream().map(person -> person.eGet(personName)))
					.containsExactly("Alice", "Bob");
			assertThat(listOf(persons.get(1), personAddresses))
					.as("the expanded to-many reference must stay complete under top()")
					.hasSize(2);
		}
	}

	/** Whether the backend serves {@code expand} prefetch hints (issue #95). */
	protected boolean supportsExpand() {
		return true;
	}

	@Test
	public void queryPipelinePagingIsSortThenLimit() throws Exception {
		saveQueryFixture();
		save(createBackendResourceSet(), "Person", newPerson(4, "Dora", 30));
		// groups: age 30 (cnt 2), 40, 50 — sort DESC first, then the pipeline top
		// takes the window: [50, 40] on every backend (the normative order)
		Query query = QueryBuilder.from(personClass)
				.groupBy(personAge)
				.countOf("cnt")
				.orderByDesc(personAge)
				.build();
		TopStage top = QueryFactory.eINSTANCE.createTopStage();
		top.setCount(2);
		query.getApply().getStages().add(top);

		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			assertThat(result.rows().map(row -> ((Number) row.get("age")).intValue()))
					.containsExactly(50, 40);
		}
		List<EObject> oracle = List.of(
				newPerson(1, "Alice", 30), newPerson(2, "Bob", 40),
				newPerson(3, "Carol", 50), newPerson(4, "Dora", 30));
		try (QueryResult memory = MemoryQueries.execute(query, oracle, null)) {
			assertThat(memory.rows().map(row -> ((Number) row.get("age")).intValue()))
					.containsExactly(50, 40);
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
		EObject alice = newPerson(1, "Alice", 30);
		alice.eSet(personBirthday, Date.from(ALICE_BIRTHDAY));
		oracle.add(alice);
		EObject bob = newPerson(2, "Bob", 40);
		bob.eSet(personBirthday, Date.from(BOB_BIRTHDAY));
		listOf(bob, personAddresses).add(newAddress(21, "Main Street 5", "Jena"));
		listOf(bob, personAddresses).add(newAddress(22, "Side Road 9", "Gera"));
		oracle.add(bob);
		EObject carol = newPerson(3, "Carol", 50);
		carol.eSet(personBirthday, Date.from(CAROL_BIRTHDAY));
		oracle.add(carol);

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
		corpus.put("round half away from zero", QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).dividedBy(4).round().eq(13))
				.build());
		corpus.put("floor", QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).dividedBy(4).floor().eq(7))
				.build());
		corpus.put("ceiling", QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).dividedBy(4).ceiling().eq(8))
				.build());
		corpus.put("temporal year", QueryBuilder.from(personClass)
				.where(Expressions.path(personBirthday).year().eq(1986))
				.build());
		corpus.put("temporal hour utc", QueryBuilder.from(personClass)
				.where(Expressions.path(personBirthday).hour().eq(23))
				.build());
		corpus.put("temporal second", QueryBuilder.from(personClass)
				.where(Expressions.path(personBirthday).second().eq(45))
				.build());
		corpus.put("collection count plain", QueryBuilder.from(personClass)
				.where(Expressions.count(Expressions.propertyPath(personAddresses)).ge(2))
				.build());
		corpus.put("collection count filtered", QueryBuilder.from(personClass)
				.where(Expressions.count(Expressions.propertyPath(personAddresses),
						a -> a.path(addressStreet).startsWith("Main")).eq(1))
				.build());
		corpus.put("collection count empty", QueryBuilder.from(personClass)
				.where(Expressions.count(Expressions.propertyPath(personAddresses)).eq(0))
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

		// containment references are object lifecycle, not patchable state (issue #107)
		ChangeEntry onContainment = changeEntry(DeltaKind.SET, personAddresses);
		assertThatThrownBy(() -> resource.execute(updateCommand(all, onContainment)))
				.isInstanceOf(IOException.class)
				.hasMessageContaining("containment");

		// an empty template patches nothing — refused, not a silent no-op
		assertThatThrownBy(() -> resource.execute(updateCommand(all)))
				.isInstanceOf(IOException.class)
				.hasMessageContaining("no entries");

		// nothing may have been changed by the refused commands
		Resource loaded = loadAll(createBackendResourceSet(), "Person");
		assertThat(loaded.getContents()).hasSize(3);
	}

	@Test
	public void commandUpdateSetsAndUnsetsSingleReferencesById() throws Exception {
		saveQueryFixture();
		// OData link semantics (issue #107): SET binds the target resolved by id
		ChangeEntry link = changeEntry(DeltaKind.SET, personBestFriend);
		link.setValueNew(id(1));
		long affected = commands(createBackendResourceSet()).execute(updateCommand(
				QueryBuilder.from(personClass).where(Expressions.path(personName).eq("Bob")).build(),
				link));
		assertThat(affected).isEqualTo(1);

		ResourceSet readSet = createBackendResourceSet();
		EObject bob = findById(loadAll(readSet, "Person"), "2");
		EObject bestFriend = resolved((EObject) bob.eGet(personBestFriend), readSet);
		assertThat(bestFriend.eGet(personName)).isEqualTo("Alice");

		// unlink = UNSET
		long cleared = commands(createBackendResourceSet()).execute(updateCommand(
				QueryBuilder.from(personClass).where(Expressions.path(personName).eq("Bob")).build(),
				changeEntry(DeltaKind.UNSET, personBestFriend)));
		assertThat(cleared).isEqualTo(1);
		EObject reloaded = findById(loadAll(createBackendResourceSet(), "Person"), "2");
		assertThat(reloaded.eGet(personBestFriend)).isNull();

		// dangling target → refusal, not a silent null bind
		ChangeEntry dangling = changeEntry(DeltaKind.SET, personBestFriend);
		dangling.setValueNew("99");
		CommandResource resource = commands(createBackendResourceSet());
		UpdateCommand broken = updateCommand(
				QueryBuilder.from(personClass).where(Expressions.path(personName).eq("Bob")).build(),
				dangling);
		assertThatThrownBy(() -> resource.execute(broken))
				.isInstanceOf(IOException.class)
				.hasMessageContaining("99");
	}

	@Test
	public void commandUpdateAddsAndRemovesManyReferenceMembersById() throws Exception {
		saveQueryFixture();
		ChangeEntry addAlice = changeEntry(DeltaKind.ADD, personFriends);
		addAlice.setValueNew(id(1));
		ChangeEntry addCarol = changeEntry(DeltaKind.ADD, personFriends);
		addCarol.setValueNew(id(3));
		long added = commands(createBackendResourceSet()).execute(updateCommand(
				QueryBuilder.from(personClass).where(Expressions.path(personName).eq("Bob")).build(),
				addAlice, addCarol));
		assertThat(added).isEqualTo(1);

		ResourceSet readSet = createBackendResourceSet();
		EObject bob = findById(loadAll(readSet, "Person"), "2");
		// membership, not order — JPA join tables carry no order column
		assertThat(listOf(bob, personFriends).stream()
				.map(friend -> resolved(friend, readSet).eGet(personName)))
				.containsExactlyInAnyOrder("Alice", "Carol");

		// REMOVE is by member id (valueOld), robust against reordering (issue #107)
		ChangeEntry removeAlice = changeEntry(DeltaKind.REMOVE, personFriends);
		removeAlice.setValueOld(id(1));
		long removed = commands(createBackendResourceSet()).execute(updateCommand(
				QueryBuilder.from(personClass).where(Expressions.path(personName).eq("Bob")).build(),
				removeAlice));
		assertThat(removed).isEqualTo(1);

		ResourceSet verifySet = createBackendResourceSet();
		EObject reloaded = findById(loadAll(verifySet, "Person"), "2");
		assertThat(listOf(reloaded, personFriends).stream()
				.map(friend -> resolved(friend, verifySet).eGet(personName)))
				.containsExactly("Carol");
	}

	@Test
	public void commandInsertBindsExistingReferenceTargetsById() throws Exception {
		// existing world: Alice (Person) and a company
		EObject company = newCompany(21, "Data In Motion");
		ResourceSet writeSet = createBackendResourceSet();
		Resource companyResource = writeSet.createResource(uriFor("Company"));
		companyResource.getContents().add(company);
		companyResource.save(null);
		save(writeSet, "Person", newPerson(1, "Alice", 30));

		// the insert payload references BOTH by detached id-stubs (@odata.bind shape);
		// employer is bidirectional — exactly what the EMF copier would drop (issue #107)
		EObject bob = newPerson(2, "Bob", 40);
		bob.eSet(personBestFriend, newPerson(1, "ignored", 0));
		bob.eSet(personEmployer, newCompany(21, "ignored"));
		InsertCommand insert = CommandFactory.eINSTANCE.createInsertCommand();
		insert.getObjects().add(bob);
		long affected = commands(createBackendResourceSet()).execute(insert);
		assertThat(affected).isEqualTo(1);

		ResourceSet readSet = createBackendResourceSet();
		EObject loaded = findById(loadAll(readSet, "Person"), "2");
		assertThat(resolved((EObject) loaded.eGet(personBestFriend), readSet).eGet(personName))
				.isEqualTo("Alice");
		assertThat(resolved((EObject) loaded.eGet(personEmployer), readSet).eGet(companyName))
				.isEqualTo("Data In Motion");

		// a dangling stub refuses the whole insert
		EObject dave = newPerson(4, "Dave", 25);
		dave.eSet(personBestFriend, newPerson(99, "nope", 0));
		InsertCommand broken = CommandFactory.eINSTANCE.createInsertCommand();
		broken.getObjects().add(dave);
		CommandResource resource = commands(createBackendResourceSet());
		assertThatThrownBy(() -> resource.execute(broken))
				.isInstanceOf(IOException.class)
				.hasMessageContaining("99");
		assertThat(findById(loadAll(createBackendResourceSet(), "Person"), "4")).isNull();
	}

	/** The string form of a person id, matching the model's id type via {@code EcoreUtil.getID}. */
	private String id(int value) {
		return String.valueOf(value);
	}

	// --------------------------------------------------------------- geo (issue #101)

	/**
	 * Whether the backend serves the geo vocabulary. The memory engine is the reference;
	 * JPA refuses until a PostGIS dialect story exists, Mongo until the 2dsphere
	 * translation lands (G-P2) — geo-capable bindings flip this and run real semantics.
	 */
	protected boolean supportsGeo() {
		return false;
	}

	@Test
	public void queryGeoVocabularyIsCapabilityGated() throws Exception {
		saveQueryFixture();
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.geoWithin(
						Expressions.geoSubject(
								Expressions.propertyPath(personAge), Expressions.propertyPath(personAge)),
						Expressions.geoBox(Expressions.geoPoint(10, 50), Expressions.geoPoint(13, 52))))
				.build();
		if (!supportsGeo()) {
			QueryableResource resource = queryable(createBackendResourceSet());
			assertThatThrownBy(() -> resource.query(query).close())
					.isInstanceOf(IOException.class)
					.hasMessageContaining("GEO_WITHIN");
			return;
		}
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			assertThat(result.objects()).isNotNull();
		}
	}

	// geo differential corpus (issue #113, G-P2): every case pins the reference
	// semantics on the memory oracle AND asserts the backend returns the same set —
	// thresholds and shape edges stay clear of boundaries (tolerance band G5)

	@SuppressWarnings("unchecked")
	private EObject place(int id, String name, Double lonValue, Double latValue) {
		EClass placeClass = (EClass) tckPackage.getEClassifier("Place");
		EObject place = EcoreUtil.create(placeClass);
		place.eSet(placeClass.getEStructuralFeature("plid"), idValue(placeClass, id));
		place.eSet(placeClass.getEStructuralFeature("name"), name);
		if (latValue != null) {
			place.eSet(placeClass.getEStructuralFeature("lat"), latValue);
			place.eSet(placeClass.getEStructuralFeature("lon"), lonValue);
			// canonical PACKED shape: GeoJSON point, coordinates [lon, lat]
			EClass geoPointClass = (EClass) tckPackage.getEClassifier("GeoPoint");
			EObject point = EcoreUtil.create(geoPointClass);
			point.eSet(geoPointClass.getEStructuralFeature("gid"), idValue(geoPointClass, id));
			point.eSet(geoPointClass.getEStructuralFeature("type"), "Point");
			((List<Double>) point.eGet(geoPointClass.getEStructuralFeature("coordinates")))
					.addAll(List.of(lonValue, latValue));
			place.eSet(placeClass.getEStructuralFeature("location"), point);
		}
		return place;
	}

	/** Thuringian triple + a null-coordinate row + an antimeridian pair (Fiji/Samoa). */
	private List<EObject> geoPlaces() {
		return List.of(
				place(1, "Jena", 11.586, 50.927),
				place(2, "Gera", 12.083, 50.880),
				place(3, "Erfurt", 11.029, 50.984),
				place(4, "Nowhere", null, null),
				place(5, "Suva", 178.442, -18.141),
				place(6, "Apia", -171.760, -13.833));
	}

	private GeoSubject splitSubject() {
		EClass placeClass = (EClass) tckPackage.getEClassifier("Place");
		return Expressions.geoSubject(
				Expressions.propertyPath(placeClass.getEStructuralFeature("lat")),
				Expressions.propertyPath(placeClass.getEStructuralFeature("lon")));
	}

	private GeoSubject packedSubject() {
		EClass placeClass = (EClass) tckPackage.getEClassifier("Place");
		return Expressions.geoSubject(
				Expressions.propertyPath(placeClass.getEStructuralFeature("location")));
	}

	/**
	 * The differential contract: the memory oracle over the fixture must yield
	 * {@code expectedNames}, and the backend must agree with the oracle.
	 */
	private void assertGeoDifferential(Query query, String... expectedNames) throws Exception {
		EClass placeClass = (EClass) tckPackage.getEClassifier("Place");
		EStructuralFeature placeName = placeClass.getEStructuralFeature("name");
		try (QueryResult oracle = MemoryQueries.execute(query, geoPlaces(), null)) {
			assertThat(oracle.objects().map(found -> found.eGet(placeName)))
					.containsExactlyInAnyOrder((Object[]) expectedNames);
		}
		save(createBackendResourceSet(), "Place", geoPlaces().toArray(EObject[]::new));
		QueryableResource resource = (QueryableResource) createBackendResourceSet()
				.createResource(uriFor("Place"));
		try (QueryResult result = resource.query(query)) {
			assertThat(result.objects().map(found -> found.eGet(placeName)))
					.containsExactlyInAnyOrder((Object[]) expectedNames);
		}
	}

	@Test
	public void geoBoxOverSplitAndPackedSubjects() throws Exception {
		assumeTrue(supportsGeo(), "geo vocabulary pending on this backend — issue #101");
		GeoBox thuringia = Expressions.geoBox(
				Expressions.geoPoint(11.3, 50.5), Expressions.geoPoint(12.5, 51.5));
		assertGeoDifferential(QueryBuilder.from((EClass) tckPackage.getEClassifier("Place"))
				.where(Expressions.geoWithin(splitSubject(), thuringia)).build(),
				"Jena", "Gera");
		assertGeoDifferential(QueryBuilder.from((EClass) tckPackage.getEClassifier("Place"))
				.where(Expressions.geoWithin(packedSubject(), thuringia)).build(),
				"Jena", "Gera");
	}

	@Test
	public void geoWrapAroundBoxCrossesTheAntimeridian() throws Exception {
		assumeTrue(supportsGeo(), "geo vocabulary pending on this backend — issue #101");
		// west > east is the legal wrap-around box (§5.3) — catches Fiji AND Samoa
		GeoBox pacific = Expressions.geoBox(
				Expressions.geoPoint(170.0, -25.0), Expressions.geoPoint(-165.0, -10.0));
		assertGeoDifferential(QueryBuilder.from((EClass) tckPackage.getEClassifier("Place"))
				.where(Expressions.geoWithin(splitSubject(), pacific)).build(),
				"Suva", "Apia");
		assertGeoDifferential(QueryBuilder.from((EClass) tckPackage.getEClassifier("Place"))
				.where(Expressions.geoWithin(packedSubject(), pacific)).build(),
				"Suva", "Apia");
	}

	@Test
	public void geoPolygonOverThePackedSubject() throws Exception {
		assumeTrue(supportsGeo(), "geo vocabulary pending on this backend — issue #101");
		// triangle around Jena and Gera, Erfurt stays west of it
		assertGeoDifferential(QueryBuilder.from((EClass) tckPackage.getEClassifier("Place"))
				.where(Expressions.geoWithin(packedSubject(), Expressions.geoPolygon(
						Expressions.geoPoint(11.3, 50.6),
						Expressions.geoPoint(12.5, 50.6),
						Expressions.geoPoint(11.9, 51.4))))
				.build(),
				"Jena", "Gera");
	}

	@Test
	public void geoDistanceThresholdsOnBothBindings() throws Exception {
		assumeTrue(supportsGeo(), "geo vocabulary pending on this backend — issue #101");
		// Jena↔Gera ≈ 35 km, Jena↔Erfurt ≈ 39 km — 37 km around Jena splits the two
		GeoPointLiteral jena = Expressions.geoPoint(11.586, 50.927);
		assertGeoDifferential(QueryBuilder.from((EClass) tckPackage.getEClassifier("Place"))
				.where(Expressions.geoDistance(packedSubject(), jena).le(37_000)).build(),
				"Jena", "Gera");
		assertGeoDifferential(QueryBuilder.from((EClass) tckPackage.getEClassifier("Place"))
				.where(Expressions.geoDistance(splitSubject(), jena).le(37_000)).build(),
				"Jena", "Gera");
		// the outside band excludes the null-coordinate row on both bindings (3VL)
		assertGeoDifferential(QueryBuilder.from((EClass) tckPackage.getEClassifier("Place"))
				.where(Expressions.geoDistance(packedSubject(), jena).gt(37_000)).build(),
				"Erfurt", "Suva", "Apia");
		assertGeoDifferential(QueryBuilder.from((EClass) tckPackage.getEClassifier("Place"))
				.where(Expressions.geoDistance(splitSubject(), jena).gt(37_000)).build(),
				"Erfurt", "Suva", "Apia");
	}

	@Test
	public void geoNegationExcludesUnknownSubjects() throws Exception {
		assumeTrue(supportsGeo(), "geo vocabulary pending on this backend — issue #101");
		GeoBox thuringia = Expressions.geoBox(
				Expressions.geoPoint(11.3, 50.5), Expressions.geoPoint(12.5, 51.5));
		// NOT within must not surface the null-coordinate row (§5.5, issue-#97 discipline)
		assertGeoDifferential(QueryBuilder.from((EClass) tckPackage.getEClassifier("Place"))
				.where(Expressions.not(Expressions.geoWithin(splitSubject(), thuringia))).build(),
				"Erfurt", "Suva", "Apia");
		assertGeoDifferential(QueryBuilder.from((EClass) tckPackage.getEClassifier("Place"))
				.where(Expressions.not(Expressions.geoWithin(packedSubject(), thuringia))).build(),
				"Erfurt", "Suva", "Apia");
	}

	// -------------------------------------------------- command transactions (issue #108)

	/**
	 * Whether the backend serves cross-command transaction brackets. The mongo backend
	 * refuses (multi-document transactions need a session-capable client and a replica
	 * set) — its bindings return {@code false} and assert the refusal shape instead.
	 */
	protected boolean supportsCommandTransactions() {
		return true;
	}

	/**
	 * The issue-#114 contract: the declaration matches behaviour. Every declared
	 * feature executes (the command/transaction cases above cover that), an undeclared
	 * one refuses before any work with a Diagnostic naming the {@link CommandFeature},
	 * and the per-EClass answer defaults to the backend-wide one.
	 */
	@Test
	public void commandCapabilitiesMatchDeclaredBehaviour() throws Exception {
		saveQueryFixture();
		CommandResource resource = commands(createBackendResourceSet());
		PersistenceCapabilities declared = ((PersistenceResource) resource).capabilities();
		assertThat(declared).isNotNull();
		CommandCapabilities capabilities = declared.command();
		assertThat(capabilities).isNotNull();
		for (CommandFeature feature : List.of(CommandFeature.INSERT,
				CommandFeature.DELETE_BY_SELECTOR, CommandFeature.UPDATE_BY_SELECTOR)) {
			assertThat(capabilities.supports(feature)).isTrue();
			// unnarrowed features answer per EClass exactly like backend-wide
			assertThat(capabilities.supports(feature, personClass)).isTrue();
			assertThat(capabilities.supported()).contains(feature);
		}
		// the transaction bracket is a store feature now, not a command verb (issue #134, §5a)
		assertThat(declared.store().supports(StoreFeature.TRANSACTION_BRACKET))
				.isEqualTo(supportsCommandTransactions());
		assertThat(capabilities.supported())
				.noneMatch(feature -> "TRANSACTION_BRACKET".equals(feature.getName()));
		if (!supportsCommandTransactions()) {
			assertThatThrownBy(resource::begin)
					.isInstanceOf(IOException.class)
					.hasMessageContaining("TRANSACTION_BRACKET");
			assertThat(((Resource) resource).getErrors()).anySatisfy(diagnostic ->
					assertThat(diagnostic.getMessage()).contains("TRANSACTION_BRACKET"));
			// the refusal changed nothing
			assertThat(loadAll(createBackendResourceSet(), "Person").getContents()).hasSize(3);
		}
	}

	@Test
	public void commandTransactionCommitsAtomically() throws Exception {
		saveQueryFixture();
		CommandResource resource = commands(createBackendResourceSet());
		if (!supportsCommandTransactions()) {
			assertThatThrownBy(resource::begin)
					.isInstanceOf(IOException.class)
					.hasMessageContaining("not supported");
			return;
		}
		InsertCommand insert = CommandFactory.eINSTANCE.createInsertCommand();
		insert.getObjects().add(newPerson(4, "Dave", 25));
		ChangeEntry setAge = changeEntry(DeltaKind.SET, personAge);
		setAge.setValueNew("26");
		UpdateCommand update = updateCommand(
				QueryBuilder.from(personClass).where(Expressions.path(personName).eq("Dave")).build(),
				setAge);

		try (CommandTransaction transaction = resource.begin()) {
			assertThat(resource.execute(insert)).isEqualTo(1);
			// the update sees the bracket's own uncommitted insert (OData $batch:
			// later requests in a changeset see earlier ones)
			assertThat(resource.execute(update)).isEqualTo(1);
			transaction.commit();
		}

		Resource loaded = loadAll(createBackendResourceSet(), "Person");
		assertThat(loaded.getContents()).hasSize(4);
		EObject dave = findById(loaded, "4");
		assertThat(((Number) dave.eGet(personAge)).intValue()).isEqualTo(26);
	}

	@Test
	public void commandTransactionRollbackLeavesNoTrace() throws Exception {
		assumeTrue(supportsCommandTransactions(), "no command transactions — issue #108");
		saveQueryFixture();
		CommandResource resource = commands(createBackendResourceSet());

		InsertCommand insert = CommandFactory.eINSTANCE.createInsertCommand();
		insert.getObjects().add(newPerson(4, "Dave", 25));
		ChangeEntry setAge = changeEntry(DeltaKind.SET, personAge);
		setAge.setValueNew("99");
		UpdateCommand update = updateCommand(QueryBuilder.from(personClass).build(), setAge);

		// close() without commit rolls back — the try-with-resources contract
		try (CommandTransaction transaction = resource.begin()) {
			assertThat(resource.execute(insert)).isEqualTo(1);
			assertThat(resource.execute(update)).isEqualTo(4);
		}

		Resource loaded = loadAll(createBackendResourceSet(), "Person");
		assertThat(loaded.getContents()).hasSize(3);
		assertThat(loaded.getContents())
				.allSatisfy(person -> assertThat(((Number) person.eGet(personAge)).intValue()).isNotEqualTo(99));

		// only one bracket at a time; a closed bracket stays closed
		try (CommandTransaction transaction = resource.begin()) {
			assertThatThrownBy(resource::begin)
					.isInstanceOf(IOException.class)
					.hasMessageContaining("already open");
			transaction.rollback();
			assertThatThrownBy(transaction::commit)
					.isInstanceOf(IOException.class)
					.hasMessageContaining("closed");
		}
	}

	// ------------------------------------------------------- composite ids (issue #109)

	/**
	 * Whether the backend persists composite-id EClasses. A backend without support
	 * returns {@code false} and the keyed test asserts the honest refusal instead.
	 */
	protected boolean supportsCompositeIds() {
		return true;
	}

	private EObject orderLine(String order, int line, int quantity) {
		EClass orderLineClass = (EClass) tckPackage.getEClassifier("OrderLine");
		EObject orderLine = EcoreUtil.create(orderLineClass);
		orderLine.eSet(orderLineClass.getEStructuralFeature("orderId"), order);
		orderLine.eSet(orderLineClass.getEStructuralFeature("lineNo"), line);
		orderLine.eSet(orderLineClass.getEStructuralFeature("quantity"), quantity);
		return orderLine;
	}

	@Test
	public void compositeIdKeyedResolutionUsesTheFragmentContract() throws Exception {
		EClass orderLineClass = (EClass) tckPackage.getEClassifier("OrderLine");
		ResourceSet writeSet = createBackendResourceSet();
		if (!supportsCompositeIds()) {
			Resource resource = writeSet.createResource(uriFor("OrderLine"));
			resource.getContents().add(orderLine("A", 1, 10));
			assertThatThrownBy(() -> resource.save(null))
					.isInstanceOf(IOException.class)
					.hasMessageContaining("composite id");
			return;
		}
		// two lines sharing the first key component — distinguishable only composite
		save(writeSet, "OrderLine", orderLine("A", 1, 10), orderLine("A", 2, 20), orderLine("B", 1, 30));

		ResourceSet readSet = createBackendResourceSet();
		Resource loaded = loadAll(readSet, "OrderLine");
		assertThat(loaded.getContents()).hasSize(3);

		// the keyed-access contract (issue #109): k1=v1,k2=v2 in declaration order
		EObject line = loaded.getEObject("orderId=A,lineNo=2");
		assertThat(line).isNotNull();
		assertThat(line.eGet(orderLineClass.getEStructuralFeature("quantity"))).isEqualTo(20);

		// pair order in the fragment is free; the produced fragment is canonical
		assertThat(loaded.getEObject("lineNo=1,orderId=B")).isNotNull();
		assertThat(loaded.getURIFragment(line)).isEqualTo("orderId=A,lineNo=2");
		assertThat(loaded.getEObject("orderId=A,lineNo=99")).isNull();
	}

	@Test
	public void compositeIdSelectorResolvesASingleObject() throws Exception {
		assumeTrue(supportsCompositeIds(), "composite ids pending on this backend — issue #109");
		EClass orderLineClass = (EClass) tckPackage.getEClassifier("OrderLine");
		save(createBackendResourceSet(), "OrderLine",
				orderLine("A", 1, 10), orderLine("A", 2, 20), orderLine("B", 1, 30));

		// the selector guarantee (issue #109 variant 2): an AND over the full composite
		// key addresses exactly one row on the primary-key path
		Query query = QueryBuilder.from(orderLineClass)
				.where(Expressions.and(
						Expressions.path(orderLineClass.getEStructuralFeature("orderId")).eq("A"),
						Expressions.path(orderLineClass.getEStructuralFeature("lineNo")).eq(2)))
				.build();
		ResourceSet readSet = createBackendResourceSet();
		QueryableResource resource = (QueryableResource) readSet.createResource(uriFor("OrderLine"));
		try (QueryResult result = resource.query(query)) {
			assertThat(result.objects().map(line ->
					line.eGet(orderLineClass.getEStructuralFeature("quantity"))))
					.containsExactly(20);
		}
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
