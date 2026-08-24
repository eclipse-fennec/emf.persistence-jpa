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

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
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
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.IntervalSubject;
import org.eclipse.fennec.model.query.GroupByStage;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.SortDirection;
import org.eclipse.fennec.model.query.QueryFactory;
import org.eclipse.fennec.model.query.TopStage;
import org.eclipse.fennec.model.query.builder.Expressions;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.model.stream.ChangeEntry;
import org.eclipse.fennec.model.stream.ChangeSet;
import org.eclipse.fennec.model.stream.DeltaKind;
import org.eclipse.fennec.model.stream.StreamFactory;
import org.eclipse.fennec.persistence.capabilities.CommandCapabilities;
import org.eclipse.fennec.persistence.Options;
import org.eclipse.fennec.persistence.diagnostic.PersistenceDiagnostic;
import org.eclipse.fennec.persistence.capabilities.CommandFeature;
import org.eclipse.fennec.persistence.capabilities.PersistenceCapabilities;
import org.eclipse.fennec.persistence.capabilities.QueryFeature;
import org.eclipse.fennec.persistence.capabilities.StoreFeature;
import org.eclipse.fennec.persistence.pushstreams.PersistencePushStreams;
import org.eclipse.fennec.persistence.query.api.CommandResource;
import org.eclipse.fennec.persistence.query.QueryException;
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
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
 * <p>
 * Capability variance is declarative (issue #160): the binding answers
 * {@link #declaredCapabilities()}, every non-core case carries a {@link RequiresCapabilities}
 * annotation, and {@link CapabilityGate} skips a case whose required features the backend does
 * not declare — the skip reason names them. The other direction of contract §2B is
 * {@link #undeclaredFeaturesAreRefusedWithADiagnostic()}: an undeclared feature must be
 * refused with a Diagnostic, never silently post-filtered.
 *
 * @author Mark Hoffmann
 * @since 16.07.2026
 */
@ExtendWith(CapabilityGate.class)
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
	protected EStructuralFeature personAttributes;
	protected EStructuralFeature personCounts;
	protected EStructuralFeature personBestFriend;
	protected EStructuralFeature personFriends;
	protected EStructuralFeature personEmployer;
	protected EStructuralFeature addressStreet;
	/** {@code Profile}, the §8 EMF-semantics fixture (issue #174). */
	protected EClass profileClass;
	protected EStructuralFeature profileNicknames;
	protected EStructuralFeature profileMotto;
	protected EStructuralFeature profileRank;
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

	/**
	 * The backend's capability declaration (issue #160): what this backend and flavor can do
	 * at all, per contract §5a answerable <em>without opening a connection</em> — the gate
	 * evaluates it before {@link #setUpBackend(EPackage)} has run. The live resource's
	 * effective set may be narrower (a probe took something away), never wider;
	 * {@link #effectiveCapabilitiesNeverExceedTheDeclaration()} holds the binding to that.
	 */
	protected abstract PersistenceCapabilities declaredCapabilities();

	/**
	 * Provokes a load that the backend cannot fully answer, and returns the resource it was
	 * attempted on (issue #197).
	 * <p>
	 * The <em>contract</em> is portable — a load that could not do what was asked says so on
	 * the resource — but nothing that triggers it is: JPA rejects an unknown entity because it
	 * has a schema, while mongo will happily read a collection nobody wrote. So the binding
	 * supplies the trigger and the core case asserts the contract.
	 *
	 * @return the resource the load was attempted on, carrying its diagnostics
	 * @throws Exception when the backend signals the failure by throwing — also conforming,
	 *             as long as the diagnostics are on the resource
	 */
	protected abstract Resource provokeLoadDiagnostic() throws Exception;

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
		personAttributes = personClass.getEStructuralFeature("attributes");
		personCounts = personClass.getEStructuralFeature("counts");
		personBestFriend = personClass.getEStructuralFeature("bestFriend");
		personFriends = personClass.getEStructuralFeature("friends");
		personEmployer = personClass.getEStructuralFeature("employer");
		addressStreet = addressClass.getEStructuralFeature("street");
		profileClass = (EClass) tckPackage.getEClassifier("Profile");
		profileNicknames = profileClass.getEStructuralFeature("nicknames");
		profileMotto = profileClass.getEStructuralFeature("motto");
		profileRank = profileClass.getEStructuralFeature("rank");
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
		// Re-home the resource on the package's nsURI. EcoreUtil.getURI derives a classifier's
		// identity from its resource, so a model loaded under its file path hands out
		// "tck.ecore#//Car" — which a reader resolving against the package registry cannot
		// find, and which a backend then stores as the type of every document it writes.
		// Registered EPackages are addressed by nsURI, and the fixture has to look like one
		// (issue #174: this is what made polymorphic documents unreadable on mongo).
		resource.setURI(URI.createURI(ePackage.getNsURI()));
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

	/** The many-valued <em>attribute</em> counterpart of {@link #listOf} (issue #174). */
	@SuppressWarnings("unchecked")
	protected List<Object> listOfValues(EObject owner, EStructuralFeature feature) {
		return (List<Object>) owner.eGet(feature);
	}

	/** A {@code Profile} with nothing but its id — the §8 fixture (issue #174). */
	protected EObject newProfile(int id) {
		EObject profile = EcoreUtil.create(profileClass);
		profile.eSet(profileClass.getEStructuralFeature("prid"), idValue(profileClass, id));
		return profile;
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

	// ------------------------------------------------ EMF semantics (§8, issue #174)
	//
	// The conformance core the TCK was thinnest on: plain EMF behaviour every backend owes,
	// with no capability to gate it — "skip this core case" cannot be spelled. Each of these
	// closes a gap §8 verified was untested; a failure here is a defect in that backend, not
	// a capability it may decline.

	/**
	 * A load that cannot do what was asked reports it <b>on the resource</b> (issue #197).
	 * <p>
	 * {@code Resource#getErrors()} / {@code getWarnings()} is the EMF way to hand a caller what
	 * went wrong during a load, and it is where this framework puts its diagnostics (see
	 * {@code docs/diagnostics.md}). This case exists because that forwarding is easy to lose:
	 * a codec collects into its own collector, a resource forgets to transfer it, and from the
	 * outside the load looks like a success that simply returned less. Nothing about the value
	 * of the diagnostics is asserted here — only that they arrive where a caller can read them.
	 * <p>
	 * <b>Both backends populate lazily</b>, and that is the trap this case walks into on
	 * purpose: {@code load(...)} only records the request, while the work — and everything it
	 * has to report — happens on first contents access. Reading the diagnostics before
	 * touching the contents finds them empty and makes a working backend look silent. The
	 * bindings therefore trigger the population in their trigger, and anyone debugging a
	 * "missing" diagnostic should check that first.
	 */
	@Test
	public void aFailedLoadReportsItsDiagnosticsOnTheResource() throws Exception {
		Resource resource;
		try {
			resource = provokeLoadDiagnostic();
		} catch (IOException signalled) {
			// throwing is conforming too — the diagnostics still have to be on the resource,
			// which the binding hands back through the exception-free path below
			resource = null;
		}
		if (resource == null) {
			return;
		}
		assertThat(resource.getErrors().isEmpty() && resource.getWarnings().isEmpty())
				.as("a load that could not answer must leave a diagnostic on the resource —"
						+ " otherwise the caller cannot tell it apart from an empty result")
				.isFalse();
		Stream.concat(resource.getErrors().stream(), resource.getWarnings().stream())
				.forEach(diagnostic -> assertThat(diagnostic.toString())
						.as("a diagnostic without a message helps nobody").isNotBlank());
	}

	/**
	 * A multi-valued attribute round-trips (§8). Only {@code GeoPoint.coordinates} existed
	 * before, exercised solely by geo tests that mongo alone runs — so on JPA, where a
	 * many-valued attribute needs a collection table of its own, this had never been written
	 * or read at all.
	 */
	@Test
	public void multiValuedAttributeRoundTrips() throws Exception {
		EObject profile = newProfile(1);
		listOfValues(profile, profileNicknames).addAll(List.of("Ada", "Bee", "Cy"));

		save(createBackendResourceSet(), "Profile", profile);

		EObject loaded = findById(loadAll(createBackendResourceSet(), "Profile"), "1");
		assertThat(loaded).isNotNull();
		assertThat(listOfValues(loaded, profileNicknames))
				.as("a multi-valued attribute keeps its values, in order")
				.containsExactly("Ada", "Bee", "Cy");
	}

	/**
	 * An empty multi-valued attribute stays empty rather than coming back as one null element
	 * — the failure mode a collection table invites.
	 */
	@Test
	public void emptyMultiValuedAttributeStaysEmpty() throws Exception {
		save(createBackendResourceSet(), "Profile", newProfile(2));

		EObject loaded = findById(loadAll(createBackendResourceSet(), "Profile"), "2");
		assertThat(listOfValues(loaded, profileNicknames)).isEmpty();
	}

	/**
	 * Single-valued containment round-trips (§8) — {@code Place.location}. Untested on JPA for
	 * the same reason as the multi-valued attribute: the type was not in its bootstrap.
	 */
	@Test
	public void singleValuedContainmentRoundTrips() throws Exception {
		EClass placeClass = (EClass) tckPackage.getEClassifier("Place");
		EClass geoPointClass = (EClass) tckPackage.getEClassifier("GeoPoint");
		EStructuralFeature location = placeClass.getEStructuralFeature("location");
		EObject place = EcoreUtil.create(placeClass);
		place.eSet(placeClass.getEStructuralFeature("plid"), idValue(placeClass, 1));
		place.eSet(placeClass.getEStructuralFeature("name"), "Market Square");
		EObject point = EcoreUtil.create(geoPointClass);
		point.eSet(geoPointClass.getEStructuralFeature("gid"), idValue(geoPointClass, 1));
		point.eSet(geoPointClass.getEStructuralFeature("type"), "Point");
		place.eSet(location, point);

		save(createBackendResourceSet(), "Place", place);

		EObject loaded = findById(loadAll(createBackendResourceSet(), "Place"), "1");
		assertThat(loaded).isNotNull();
		EObject loadedPoint = (EObject) loaded.eGet(location);
		assertThat(loadedPoint).as("the contained object comes back").isNotNull();
		assertThat(loadedPoint.eIsProxy()).as("containment children are materialised").isFalse();
		assertThat(loadedPoint.eContainer()).isSameAs(loaded);
		assertThat(loadedPoint.eGet(geoPointClass.getEStructuralFeature("type"))).isEqualTo("Point");
	}

	/**
	 * A subtype written and read through its supertype's resource comes back as that subtype
	 * (§8). The hierarchy was exercised thirteen times before this — every time through a
	 * capability-gated type predicate, so the plain round trip, which no backend may decline,
	 * was never asserted.
	 * <p>
	 * The root is <b>abstract</b>, which makes this the §8 abstract-type case as well: nothing
	 * can fall back to instantiating the root, so a backend that loses the stored type fails
	 * here instead of quietly answering with a supertype instance.
	 */
	@Test
	public void polymorphicRoundTrip() throws Exception {
		EClass vehicleClass = (EClass) tckPackage.getEClassifier("Vehicle");
		assertThat(vehicleClass.isAbstract())
				.as("the fixture root is abstract — that is what makes this the abstract-type case")
				.isTrue();
		EClass carClass = (EClass) tckPackage.getEClassifier("Car");
		EClass motorcycleClass = (EClass) tckPackage.getEClassifier("Motorcycle");
		EObject car = newVehicle(carClass, 1, "Beetle");
		car.eSet(carClass.getEStructuralFeature("horsepower"), 50);
		EObject motorcycle = newVehicle(motorcycleClass, 2, "Bonnie");
		motorcycle.eSet(motorcycleClass.getEStructuralFeature("cc"), 900);

		save(createBackendResourceSet(), "Vehicle", car, motorcycle);

		Resource loaded = loadAll(createBackendResourceSet(), "Vehicle");
		EObject loadedCar = findById(loaded, "1");
		EObject loadedMotorcycle = findById(loaded, "2");
		assertThat(loadedCar).isNotNull();
		assertThat(loadedMotorcycle).isNotNull();
		assertThat(loadedCar.eClass().getName())
				.as("a subtype read through its supertype keeps its type").isEqualTo("Car");
		assertThat(loadedMotorcycle.eClass().getName()).isEqualTo("Motorcycle");
		assertThat(loadedCar.eGet(carClass.getEStructuralFeature("horsepower"))).isEqualTo(50);
		assertThat(loadedMotorcycle.eGet(motorcycleClass.getEStructuralFeature("cc"))).isEqualTo(900);
	}

	/**
	 * {@code eUnset} on the EMF write path (§8). Only the command path covered unset before
	 * ({@code commandUpdateUnsetClearsTheValue}), so the ordinary "clear it and save" route
	 * was untested.
	 */
	@Test
	public void unsetOnTheWritePathClearsTheValue() throws Exception {
		EObject profile = newProfile(3);
		profile.eSet(profileMotto, "carpe diem");
		save(createBackendResourceSet(), "Profile", profile);

		EObject stored = findById(loadAll(createBackendResourceSet(), "Profile"), "3");
		assertThat(stored.eGet(profileMotto)).isEqualTo("carpe diem");
		stored.eUnset(profileMotto);
		stored.eResource().save(null);

		EObject reloaded = findById(loadAll(createBackendResourceSet(), "Profile"), "3");
		assertThat(reloaded.eGet(profileMotto)).as("the unset value is gone after reload").isNull();
	}

	/**
	 * A feature never written comes back as its declared default (§8) — {@code Profile.rank}
	 * defaults to 7, so a backend that stores a physical null must still answer the default.
	 */
	@Test
	public void defaultValueSurvivesTheRoundTrip() throws Exception {
		save(createBackendResourceSet(), "Profile", newProfile(4));

		EObject loaded = findById(loadAll(createBackendResourceSet(), "Profile"), "4");
		assertThat(loaded.eGet(profileRank)).as("an unwritten feature answers its default")
				.isEqualTo(7);
	}

	/**
	 * Containment order is part of the value (§8): a many-valued containment reference comes
	 * back in the order it was written, not in whatever order the store returns rows.
	 */
	@Test
	public void containmentOrderIsPreserved() throws Exception {
		EObject person = newPerson(1, "Emil", 30);
		listOf(person, personAddresses).add(newAddress(31, "First", "Jena"));
		listOf(person, personAddresses).add(newAddress(32, "Second", "Jena"));
		listOf(person, personAddresses).add(newAddress(33, "Third", "Jena"));

		save(createBackendResourceSet(), "Person", person);

		EObject loaded = findById(loadAll(createBackendResourceSet(), "Person"), "1");
		assertThat(listOf(loaded, personAddresses)).extracting(a -> a.eGet(addressStreet))
				.as("containment keeps insertion order")
				.containsExactly("First", "Second", "Third");
	}

	/**
	 * Two reads through one ResourceSet yield the same instance (§8): the collection resource
	 * is the identity anchor, so a second load must dedup against it rather than produce a
	 * second copy of the same object.
	 */
	@Test
	public void objectIdentityIsStableAcrossRepeatedLoads() throws Exception {
		save(createBackendResourceSet(), "Person", newPerson(1, "Emil", 30));

		ResourceSet readSet = createBackendResourceSet();
		EObject first = findById(loadAll(readSet, "Person"), "1");
		EObject second = findById(loadAll(readSet, "Person"), "1");
		assertThat(second).as("one ResourceSet answers one identity per object").isSameAs(first);
	}

	/**
	 * Deleting an object that something still points at is <b>refused</b> (§8, issue #195).
	 * <p>
	 * The reference in question is a plain, non-containment one — containment is ownership and
	 * cascades (issues #142/#143), which is a different question with a settled answer. What
	 * this pins is the other direction: a store must not leave a reference pointing at
	 * something that is gone, and it must not decide that quietly. JPA arrives here through
	 * its foreign key, mongo has to look before it deletes; both must refuse, and say why.
	 */
	@Test
	public void deletingAReferencedObjectIsRefused() throws Exception {
		EObject alice = newPerson(1, "Alice", 30);
		EObject bob = newPerson(2, "Bob", 40);
		bob.eSet(personBestFriend, alice);
		save(createBackendResourceSet(), "Person", alice, bob);

		EObject storedAlice = findById(loadAll(createBackendResourceSet(), "Person"), "1");
		assertThat(storedAlice).isNotNull();
		Resource holder = createBackendResourceSet().createResource(uriFor("Person"));
		holder.getContents().add(storedAlice);

		assertThatThrownBy(() -> holder.delete(null))
				.as("an object something still references must not be deleted silently")
				.isInstanceOf(IOException.class);
		assertThat(holder.getErrors()).as("the refusal has to say why").isNotEmpty();
		assertRefusalIsClassified(holder);

		Resource reloaded = loadAll(createBackendResourceSet(), "Person");
		assertThat(reloaded.getContents()).as("the refused delete changed nothing").hasSize(2);
		EObject reloadedBob = findById(reloaded, "2");
		assertThat(reloadedBob.eGet(personBestFriend, false))
				.as("the reference still points at something that exists").isNotNull();
	}

	/**
	 * A containment reference declaring {@code resolveProxies="false"} keeps its child at home
	 * — and nothing in the store may undo that (§8, issue #195).
	 * <p>
	 * Cross-document containment is core and both backends serve it (§4b), but a model can
	 * decline it per reference: {@code resolveProxies="false"} states that this reference
	 * never yields a proxy, which only holds while the child lives with its owner.
	 * <p>
	 * <b>EMF enforces that itself</b>, which is the finding this case records: making such a
	 * child a root of its own resource does not produce the forbidden shape, it dissolves the
	 * containment — the reference goes empty and the child loses its container, before any
	 * backend is involved. So there is nothing for a store to refuse, and a check for it would
	 * be code that can never run. What a store still owes is not to reconstruct the shape on
	 * the way back, which is what the round trip below asserts.
	 */
	@Test
	public void containmentThatForbidsProxiesKeepsItsChildAtHome() throws Exception {
		EStructuralFeature badge = profileClass.getEStructuralFeature("badge");
		assertThat(((EReference) badge).isResolveProxies())
				.as("the fixture reference is the one that forbids proxies").isFalse();

		EObject profile = newProfile(9);
		EObject child = newAddress(91, "Badge Street 1", "Jena");
		profile.eSet(badge, child);
		assertThat(child.eContainer()).as("contained to begin with").isSameAs(profile);

		ResourceSet writeSet = createBackendResourceSet();
		Resource own = writeSet.createResource(uriFor("Address"));
		own.getContents().add(child);

		// EMF dissolves the containment rather than allowing a proxy-bearing one
		assertThat(profile.eGet(badge))
				.as("EMF empties a resolveProxies=false reference instead of letting the child leave")
				.isNull();
		assertThat(child.eContainer()).as("and the child keeps no container").isNull();

		Resource holder = writeSet.createResource(uriFor("Profile"));
		holder.getContents().add(profile);
		own.save(null);
		holder.save(null);

		EObject reloaded = findById(loadAll(createBackendResourceSet(), "Profile"), "9");
		assertThat(reloaded).isNotNull();
		assertThat(reloaded.eGet(badge, false))
				.as("the store must not hand back a proxy the model ruled out")
				.isNull();
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

	/**
	 * A map round-trips: entries come back, keys keep their type, and a second put replaces
	 * rather than appends (contract §9.2, issue #185). Core, so no capability gate — the two
	 * backends realise it very differently (a keyed sub-document on mongo, rows in an entry
	 * table with a unique constraint on JPA) and that is form, not semantics.
	 * <p>
	 * The int-keyed map is here deliberately: a key becomes a field name on document stores, so
	 * a non-string key is exactly where the shape leaks (emf.codec#154 lost every entry of such
	 * a map on read until it was fixed).
	 */
	@Test
	public void mapRoundTrip() throws Exception {
		ResourceSet writeSet = createBackendResourceSet();
		EObject person = newPerson(1, "Alice", 30);
		mapOf(person, personAttributes).put("color", "red");
		mapOf(person, personAttributes).put("size", "L");
		mapOf(person, personCounts).put(1, "one");
		mapOf(person, personCounts).put(42, "answer");
		Resource resource = writeSet.createResource(uriFor("Person"));
		resource.getContents().add(person);
		resource.save(null);

		evictBackendCaches();
		EObject loaded = loadAll(createBackendResourceSet(), "Person").getContents().get(0);
		EMap<Object, Object> attributes = mapOf(loaded, personAttributes);
		assertThat(attributes).as("both entries come back").hasSize(2);
		assertThat(attributes.get("color")).isEqualTo("red");
		assertThat(attributes.get("size")).isEqualTo("L");
		EMap<Object, Object> counts = mapOf(loaded, personCounts);
		assertThat(counts).hasSize(2);
		// Integer.valueOf, deliberately: EMap extends EList, so get(int) is the list index
		// overload and would read the second entry instead of key 1
		assertThat(counts.get(Integer.valueOf(1))).as("an int key must stay an int key")
				.isEqualTo("one");
		assertThat(counts.get(Integer.valueOf(42))).isEqualTo("answer");
	}

	@Test
	public void mapKeysAreUnique() throws Exception {
		ResourceSet writeSet = createBackendResourceSet();
		EObject person = newPerson(2, "Bob", 40);
		mapOf(person, personAttributes).put("color", "red");
		mapOf(person, personAttributes).put("color", "blue");
		Resource resource = writeSet.createResource(uriFor("Person"));
		resource.getContents().add(person);
		resource.save(null);

		evictBackendCaches();
		EObject loaded = loadAll(createBackendResourceSet(), "Person").getContents().get(0);
		EMap<Object, Object> attributes = mapOf(loaded, personAttributes);
		assertThat(attributes).as("one key, one entry").hasSize(1);
		assertThat(attributes.get("color")).isEqualTo("blue");
	}

	@SuppressWarnings("unchecked")
	private EMap<Object, Object> mapOf(EObject owner, EStructuralFeature feature) {
		Object value = owner.eGet(feature);
		assertThat(value).as("'%s' must be an EMap", feature.getName()).isInstanceOf(EMap.class);
		return (EMap<Object, Object>) value;
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
	@RequiresCapabilities(query = { QueryFeature.WHERE_EQ, QueryFeature.WHERE_NE,
			QueryFeature.WHERE_COMPARISON, QueryFeature.LOGICAL_AND, QueryFeature.LOGICAL_OR })
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
	@RequiresCapabilities(query = { QueryFeature.IN, QueryFeature.WHERE_NE, QueryFeature.IS_NULL,
			QueryFeature.LOGICAL_AND })
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
	@RequiresCapabilities(query = { QueryFeature.WHERE_EQ, QueryFeature.WHERE_NE,
			QueryFeature.LOGICAL_NOT })
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
	@RequiresCapabilities(query = { QueryFeature.WHERE_EQ, QueryFeature.WHERE_COMPARISON,
			QueryFeature.LOGICAL_AND, QueryFeature.LOGICAL_OR, QueryFeature.LOGICAL_NOT })
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
	@RequiresCapabilities(query = QueryFeature.WHERE_EQ)
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
	@RequiresCapabilities(query = QueryFeature.WHERE_EQ)
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
	@RequiresCapabilities(query = QueryFeature.WHERE_EQ)
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

	/**
	 * Java string semantics are the reference (§2): {@code 'alice'} is not {@code 'Alice'}.
	 * A store whose <em>default collation</em> compares case-insensitively — MariaDB's
	 * utf8mb4 CI default (#158) — must not leak that into EMF equality; case-insensitivity
	 * is what {@link QueryFeature#STRING_MATCH_CASE_INSENSITIVE} exists for, opt-in per
	 * predicate, never ambient.
	 */
	@Test
	@RequiresCapabilities(query = QueryFeature.WHERE_EQ)
	public void queryStringEqualityIsCaseSensitive() throws Exception {
		saveQueryFixture();
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.path(personName).eq("alice"))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			assertThat(result.objects())
					.as("case-sensitive equality must not match a differently-cased value")
					.isEmpty();
		}
	}

	@Test
	@RequiresCapabilities(query = { QueryFeature.WHERE_STRING_MATCH,
			QueryFeature.STRING_MATCH_CASE_INSENSITIVE })
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

	/**
	 * Edit-distance matching (issue #167): a declared-STRING_MATCH_FUZZY backend must agree
	 * with the memory oracle's whole-value optimal-string-alignment semantics — the edit
	 * budget, adjacent transpositions and the exact-prefix requirement included.
	 */
	@Test
	@RequiresCapabilities(query = { QueryFeature.WHERE_STRING_MATCH, QueryFeature.STRING_MATCH_FUZZY })
	public void queryFuzzyMatchingAgreesWithTheMemoryOracle() throws Exception {
		saveQueryFixture();
		List<EObject> oracle = List.of(
				newPerson(1, "Alice", 30), newPerson(2, "Bob", 40), newPerson(3, "Carol", 50));
		Map<String, Query> corpus = new LinkedHashMap<>();
		corpus.put("default budget", QueryBuilder.from(personClass)
				.where(Expressions.path(personName).fuzzy("Alicia")).build());
		corpus.put("budget of one", QueryBuilder.from(personClass)
				.where(Expressions.path(personName).fuzzy("Alicia", 1)).build());
		corpus.put("adjacent transposition", QueryBuilder.from(personClass)
				.where(Expressions.path(personName).fuzzy("oBb", 1)).build());
		corpus.put("exact prefix excludes", QueryBuilder.from(personClass)
				.where(Expressions.path(personName).fuzzy("Karol", 2, 1)).build());
		for (Map.Entry<String, Query> entry : corpus.entrySet()) {
			List<Object> backendNames;
			try (QueryResult result = queryable(createBackendResourceSet()).query(entry.getValue())) {
				backendNames = result.objects().map(person -> person.eGet(personName))
						.map(Object.class::cast).toList();
			}
			try (QueryResult memory = MemoryQueries.execute(entry.getValue(), oracle, null)) {
				assertThat(backendNames)
						.as("backend and memory oracle must agree on '%s'", entry.getKey())
						.containsExactlyInAnyOrderElementsOf(memory.objects()
								.map(person -> person.eGet(personName)).map(Object.class::cast).toList());
			}
		}
	}

	@Test
	@RequiresCapabilities(query = { QueryFeature.EXISTS, QueryFeature.WHERE_STRING_MATCH })
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
	@RequiresCapabilities(query = { QueryFeature.FOR_ALL, QueryFeature.WHERE_STRING_MATCH })
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
	@RequiresCapabilities(query = { QueryFeature.STRING_FUNCTIONS, QueryFeature.WHERE_EQ,
			QueryFeature.WHERE_COMPARISON })
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
	@RequiresCapabilities(query = { QueryFeature.ARITHMETIC, QueryFeature.WHERE_EQ,
			QueryFeature.WHERE_COMPARISON })
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
	@RequiresCapabilities(query = { QueryFeature.ARITHMETIC, QueryFeature.WHERE_COMPARISON })
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
	@RequiresCapabilities(query = { QueryFeature.STRING_FUNCTIONS_EXTENDED, QueryFeature.WHERE_EQ })
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
	@RequiresCapabilities(query = { QueryFeature.NUMERIC_FUNCTIONS, QueryFeature.ARITHMETIC,
			QueryFeature.WHERE_EQ })
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
	@RequiresCapabilities(query = { QueryFeature.TEMPORAL_FUNCTIONS, QueryFeature.WHERE_EQ })
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

	@Test
	@RequiresCapabilities(query = { QueryFeature.TYPE_CHECK, QueryFeature.TYPE_CAST,
			QueryFeature.WHERE_COMPARISON, QueryFeature.LOGICAL_NOT })
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
	@RequiresCapabilities(query = { QueryFeature.COLLECTION_COUNT, QueryFeature.WHERE_EQ,
			QueryFeature.WHERE_COMPARISON })
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
	}

	@Test
	@RequiresCapabilities(query = { QueryFeature.COLLECTION_COUNT_FILTERED, QueryFeature.WHERE_EQ,
			QueryFeature.WHERE_STRING_MATCH })
	public void queryFilteredCollectionCount() throws Exception {
		saveQueryFixture();
		// exactly one Main-Street address — only Bob (issue #81)
		Query filtered = QueryBuilder.from(personClass)
				.where(Expressions.count(Expressions.propertyPath(personAddresses),
						a -> a.path(addressStreet).startsWith("Main")).eq(1))
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(filtered)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Bob");
		}
	}

	@Test
	@RequiresCapabilities(query = { QueryFeature.GROUP_BY, QueryFeature.AGG_SUM,
			QueryFeature.AGG_COUNT, QueryFeature.PIPELINE, QueryFeature.PIPELINE_COMPUTE,
			QueryFeature.ARITHMETIC, QueryFeature.WHERE_COMPARISON })
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
	@RequiresCapabilities(query = { QueryFeature.PIPELINE, QueryFeature.PIPELINE_COMPUTE,
			QueryFeature.GROUP_BY, QueryFeature.GROUP_EXPRESSION, QueryFeature.AGG_SUM,
			QueryFeature.AGG_COUNT, QueryFeature.ARITHMETIC, QueryFeature.NUMERIC_FUNCTIONS })
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
	@RequiresCapabilities(query = { QueryFeature.GROUP_BY, QueryFeature.GROUP_EXPRESSION,
			QueryFeature.AGG_AVG, QueryFeature.ARITHMETIC, QueryFeature.NUMERIC_FUNCTIONS })
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
	@RequiresCapabilities(query = { QueryFeature.SORT, QueryFeature.SORT_EXPRESSION,
			QueryFeature.ARITHMETIC })
	public void querySortByExpression() throws Exception {
		saveQueryFixture();
		// -age ascending = age descending: Carol, Bob, Alice (issue #84)
		Query query = QueryBuilder.from(personClass)
				.orderByAsc(Expressions.neg(Expressions.path(personAge)).toExpression())
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.containsExactly("Carol", "Bob", "Alice");
		}
	}

	@Test
	@RequiresCapabilities(query = { QueryFeature.GROUP_BY, QueryFeature.AGG_AVG, QueryFeature.SORT })
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
	@RequiresCapabilities(query = { QueryFeature.EXPAND, QueryFeature.WHERE_EQ })
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
	@RequiresCapabilities(query = { QueryFeature.EXPAND, QueryFeature.SORT, QueryFeature.LIMIT })
	public void queryExpandToManyKeepsMaxResultsCounting() throws Exception {
		// a collection fetch join would count joined SQL rows against setMaxResults and
		// truncate Bob's addresses — the to-many expand must batch instead (issue #95)
		saveQueryFixture();
		Query query = QueryBuilder.from(personClass)
				.orderByAsc(personName)
				.top(2)
				.expand(personAddresses)
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			List<EObject> persons = result.objects().toList();
			assertThat(persons.stream().map(person -> person.eGet(personName)))
					.containsExactly("Alice", "Bob");
			assertThat(listOf(persons.get(1), personAddresses))
					.as("the expanded to-many reference must stay complete under top()")
					.hasSize(2);
		}
	}

	@Test
	@RequiresCapabilities(query = { QueryFeature.GROUP_BY, QueryFeature.AGG_COUNT,
			QueryFeature.SORT, QueryFeature.PIPELINE })
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
	@RequiresCapabilities(query = { QueryFeature.ARITHMETIC, QueryFeature.PARAMETERS,
			QueryFeature.WHERE_COMPARISON })
	public void queryRuntimeZeroDivisorErrorsOrExcludes() throws Exception {
		saveQueryFixture();
		// a literal zero is refused statically; a zero bound at runtime is backend-defined
		// (#158): H2 and PostgreSQL raise the division error, MariaDB and the memory oracle
		// evaluate the division to NULL and exclude every row (3VL). Both are conforming —
		// what no backend may do is return rows as if the predicate had held.
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).dividedBy(Expressions.param("divisor")).gt(1))
				.build();
		QueryableResource resource = queryable(createBackendResourceSet());
		try (QueryResult result = resource.query(query, Map.of("divisor", 0), null)) {
			assertThat(result.objects()).as("a NULL-evaluating division must exclude, never match")
					.isEmpty();
		} catch (IOException expected) {
			// the backend's division error — the other conforming shape
		}
	}

	@Test
	@RequiresCapabilities(query = { QueryFeature.FIELD_TO_FIELD, QueryFeature.WHERE_EQ,
			QueryFeature.WHERE_NE })
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
	@RequiresCapabilities(query = { QueryFeature.SORT, QueryFeature.SKIP, QueryFeature.LIMIT,
			QueryFeature.COUNT, QueryFeature.WHERE_COMPARISON })
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
	@RequiresCapabilities(query = { QueryFeature.PARAMETERS, QueryFeature.WHERE_EQ })
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
	@RequiresCapabilities(query = { QueryFeature.PROJECTION, QueryFeature.WHERE_COMPARISON })
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

	/**
	 * Issue #189: a projection column that is computed rather than navigated. Both
	 * database backends serve it, and the memory oracle defines the expected values —
	 * the point of the case is that all three agree on the computed column.
	 */
	@Test
	@RequiresCapabilities(query = { QueryFeature.PROJECTION, QueryFeature.PROJECTION_EXPRESSION,
			QueryFeature.ARITHMETIC, QueryFeature.WHERE_COMPARISON })
	public void queryProjectionOfAnExpression() throws Exception {
		saveQueryFixture();
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).ge(40))
				.selectAs("n", personName)
				.selectAs("nextAge", Expressions.path(personAge).plus(1).toExpression())
				.build();
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			assertThat(result.shape()).isEqualTo(QueryShape.PROJECTION);
			List<QueryResultRow> rows = result.rows().toList();
			assertThat(rows).extracting(row -> row.get("n"))
					.containsExactlyInAnyOrder("Bob", "Carol");
			assertThat(rows).extracting(row -> ((Number) row.get("nextAge")).longValue())
					.containsExactlyInAnyOrder(41L, 51L);
		}
	}

	@Test
	@RequiresCapabilities(query = { QueryFeature.GROUP_BY, QueryFeature.AGG_AVG,
			QueryFeature.AGG_COUNT })
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
	@RequiresCapabilities(query = { QueryFeature.GROUP_BY, QueryFeature.AGG_AVG,
			QueryFeature.AGG_COUNT })
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
	@RequiresCapabilities(query = { QueryFeature.PARAMETERS, QueryFeature.WHERE_COMPARISON })
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
	@RequiresCapabilities(query = { QueryFeature.WHERE_EQ, QueryFeature.WHERE_COMPARISON })
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
	@RequiresCapabilities(query = { QueryFeature.IN, QueryFeature.LOGICAL_AND,
			QueryFeature.WHERE_COMPARISON })
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
	@RequiresCapabilities(query = { QueryFeature.IN, QueryFeature.LOGICAL_AND,
			QueryFeature.WHERE_COMPARISON })
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
	@RequiresCapabilities(query = { QueryFeature.GROUP_BY, QueryFeature.AGG_AVG, QueryFeature.SORT })
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
	@RequiresCapabilities(query = { QueryFeature.WHERE_EQ, QueryFeature.WHERE_NE,
			QueryFeature.WHERE_COMPARISON, QueryFeature.WHERE_RANGE, QueryFeature.IS_NULL,
			QueryFeature.IN, QueryFeature.LOGICAL_AND, QueryFeature.LOGICAL_OR,
			QueryFeature.WHERE_STRING_MATCH, QueryFeature.STRING_MATCH_CASE_INSENSITIVE,
			QueryFeature.EXISTS, QueryFeature.FOR_ALL, QueryFeature.SORT, QueryFeature.SKIP,
			QueryFeature.LIMIT, QueryFeature.COUNT, QueryFeature.STRING_FUNCTIONS,
			QueryFeature.STRING_FUNCTIONS_EXTENDED, QueryFeature.ARITHMETIC,
			QueryFeature.NUMERIC_FUNCTIONS, QueryFeature.TEMPORAL_FUNCTIONS,
			QueryFeature.FIELD_TO_FIELD, QueryFeature.COLLECTION_COUNT,
			QueryFeature.COLLECTION_COUNT_FILTERED })
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
	@RequiresCapabilities(command = CommandFeature.INSERT)
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
	@RequiresCapabilities(command = CommandFeature.DELETE_BY_SELECTOR,
			query = QueryFeature.WHERE_COMPARISON)
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

	/**
	 * The §4c refusal holds on the command path too (issue #219).
	 * <p>
	 * {@link #deletingAReferencedObjectIsRefused()} pins the same contract for
	 * {@code Resource.delete()}. It passed on both backends while this one would have failed on
	 * mongo, and that gap is the whole point of the case: JPA arrives at the refusal through its
	 * foreign key no matter which path removes the row, while an application-level guard only
	 * holds on the path it sits on. A consumer that writes exclusively through commands — as
	 * {@code emf.odata} does — met the contract on one backend and not on the other.
	 */
	@Test
	@RequiresCapabilities(command = CommandFeature.DELETE_BY_SELECTOR,
			query = QueryFeature.WHERE_COMPARISON)
	public void commandDeleteOfAReferencedObjectIsRefused() throws Exception {
		EObject alice = newPerson(1, "Alice", 30);
		EObject bob = newPerson(2, "Bob", 40);
		bob.eSet(personBestFriend, alice);
		save(createBackendResourceSet(), "Person", alice, bob);

		DeleteCommand delete = CommandFactory.eINSTANCE.createDeleteCommand();
		delete.setSelector(QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).lt(40))
				.build());

		CommandResource resource = commands(createBackendResourceSet());
		assertThatThrownBy(() -> resource.execute(delete))
				.as("a selector must not delete what the resource path refuses to delete")
				.isInstanceOf(IOException.class);
		assertThat(((Resource) resource).getErrors())
				.as("the refusal has to say why, on this path as well").isNotEmpty();
		assertRefusalIsClassified((Resource) resource);

		Resource reloaded = loadAll(createBackendResourceSet(), "Person");
		assertThat(reloaded.getContents()).as("the refused delete changed nothing").hasSize(2);
		EObject reloadedBob = findById(reloaded, "2");
		assertThat(reloadedBob.eGet(personBestFriend, false))
				.as("the reference still points at something that exists").isNotNull();
	}

	/**
	 * A command selector that matches nothing referenced still deletes (issue #219).
	 * <p>
	 * The companion to the case above, and the one that keeps the guard from being satisfied by
	 * refusing everything: Bob points at Alice, so deleting <em>Bob</em> is nobody's problem.
	 */
	@Test
	@RequiresCapabilities(command = CommandFeature.DELETE_BY_SELECTOR,
			query = QueryFeature.WHERE_COMPARISON)
	public void commandDeleteOfAnUnreferencedObjectStillWorks() throws Exception {
		EObject alice = newPerson(1, "Alice", 30);
		EObject bob = newPerson(2, "Bob", 40);
		bob.eSet(personBestFriend, alice);
		save(createBackendResourceSet(), "Person", alice, bob);

		DeleteCommand delete = CommandFactory.eINSTANCE.createDeleteCommand();
		delete.setSelector(QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).ge(40))
				.build());

		assertThat(commands(createBackendResourceSet()).execute(delete))
				.as("the referring object itself may go").isEqualTo(1);
		assertThat(loadAll(createBackendResourceSet(), "Person").getContents()).hasSize(1);
	}

	/**
	 * A command selector can use parameters, and the values come with the call (issue #202).
	 * <p>
	 * Until now {@code execute(Command)} took no bindings, so a selector using
	 * {@code param(...)} had nowhere to get its values from and failed on every backend —
	 * measured in #175, which had to leave `PARAMETERS` out of its selector corpus for exactly
	 * that reason. This is the write-side counterpart of {@code find(query, parameters, …)},
	 * and the thing a prepared command is built on.
	 */
	@Test
	@RequiresCapabilities(command = CommandFeature.DELETE_BY_SELECTOR,
			query = { QueryFeature.PARAMETERS, QueryFeature.WHERE_COMPARISON })
	public void commandSelectorTakesParameterBindings() throws Exception {
		saveQueryFixture();
		DeleteCommand delete = CommandFactory.eINSTANCE.createDeleteCommand();
		delete.setSelector(QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).ge(Expressions.param("minAge")))
				.parameter("minAge", null)
				.build());

		long affected = commands(createBackendResourceSet()).execute(delete, Map.of("minAge", 40), null);

		assertThat(affected).as("the binding decides what the selector matches").isEqualTo(2);
		Resource remaining = loadAll(createBackendResourceSet(), "Person");
		assertThat(remaining.getContents()).hasSize(1);
		assertThat(remaining.getContents().get(0).eGet(personName)).isEqualTo("Alice");
	}

	/**
	 * The same selector without bindings is still refused — the parameter has no value, and
	 * inventing one would be worse than failing (issue #202).
	 */
	@Test
	@RequiresCapabilities(command = CommandFeature.DELETE_BY_SELECTOR,
			query = { QueryFeature.PARAMETERS, QueryFeature.WHERE_COMPARISON })
	public void anUnboundCommandParameterIsRefused() throws Exception {
		saveQueryFixture();
		DeleteCommand delete = CommandFactory.eINSTANCE.createDeleteCommand();
		delete.setSelector(QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).ge(Expressions.param("minAge")))
				.parameter("minAge", null)
				.build());

		CommandResource resource = commands(createBackendResourceSet());
		assertThatThrownBy(() -> resource.execute(delete))
				.as("an unbound parameter must fail rather than match something arbitrary")
				.isInstanceOf(IOException.class);
		assertThat(loadAll(createBackendResourceSet(), "Person").getContents())
				.as("and nothing may have been deleted").hasSize(3);
	}

	/**
	 * A command DELETE keeps its count and its completeness across chunk boundaries
	 * (issue #227).
	 * <p>
	 * The chunk size is set below the match count on purpose, so the flush/clear boundary is
	 * crossed several times. What this guards is not the memory — that needs volumes no suite
	 * should carry — but everything the chunking could break while saving it: a lost match, a
	 * count that reports the last chunk instead of the sum, or work discarded by the clear.
	 */
	@Test
	@RequiresCapabilities(command = CommandFeature.DELETE_BY_SELECTOR,
			query = QueryFeature.WHERE_COMPARISON)
	public void commandDeleteChunksWithoutLosingMatches() throws Exception {
		EObject[] people = new EObject[5];
		for (int i = 0; i < people.length; i++) {
			people[i] = newPerson(i + 1, "P" + i, 20 + i);
		}
		save(createBackendResourceSet(), "Person", people);

		DeleteCommand delete = CommandFactory.eINSTANCE.createDeleteCommand();
		delete.setSelector(QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).ge(20))
				.build());

		long affected = commands(createBackendResourceSet())
				.execute(delete, null, Map.of(Options.OPTION_WRITE_CHUNK_SIZE, 2));

		assertThat(affected).as("the count sums across chunks rather than reporting the last one")
				.isEqualTo(5);
		assertThat(loadAll(createBackendResourceSet(), "Person").getContents())
				.as("every chunk was actually written").isEmpty();
	}

	/**
	 * A command UPDATE whose template moves the matches <em>out of</em> the selector still
	 * patches each of them exactly once (issue #227).
	 * <p>
	 * This is the case that decides how chunking may be implemented at all. Offset paging is
	 * wrong here — after the first chunk is patched those rows no longer match, everything
	 * shifts up, and an offset skips the same number of rows it just wrote. Re-running "the
	 * first n" would be wrong for the opposite template, the one that leaves its matches
	 * matching: that never terminates. Only a cursor is right for both, and this case fails
	 * loudly if someone replaces it with paging.
	 */
	@Test
	@RequiresCapabilities(command = CommandFeature.UPDATE_BY_SELECTOR,
			query = QueryFeature.WHERE_COMPARISON)
	public void commandUpdateChunksEvenWhenThePatchLeavesTheSelector() throws Exception {
		EObject[] people = new EObject[5];
		for (int i = 0; i < people.length; i++) {
			people[i] = newPerson(i + 1, "P" + i, 20 + i);
		}
		save(createBackendResourceSet(), "Person", people);

		ChangeEntry setAge = changeEntry(DeltaKind.SET, personAge);
		setAge.setValueNew("99");
		UpdateCommand update = updateCommand(
				QueryBuilder.from(personClass)
						.where(Expressions.path(personAge).lt(50))
						.build(),
				setAge);

		long affected = commands(createBackendResourceSet())
				.execute(update, null, Map.of(Options.OPTION_WRITE_CHUNK_SIZE, 2));

		assertThat(affected).as("every match is patched exactly once").isEqualTo(5);
		Resource loaded = loadAll(createBackendResourceSet(), "Person");
		assertThat(loaded.getContents()).hasSize(5);
		assertThat(loaded.getContents())
				.as("no match was skipped by the chunk boundary")
				.allSatisfy(person -> assertThat(((Number) person.eGet(personAge)).intValue()).isEqualTo(99));
	}

	/**
	 * The refusal carries the machine-readable code, not just prose (issue #229).
	 * <p>
	 * One contract, three wordings: JPA can only say the delete failed and leaves the constraint
	 * in a nested SQLException, while the two mongo paths phrase it per object and per selector.
	 * A consumer mapping this onto a protocol — {@code emf.odata} onto 409 rather than 500 —
	 * must not have to match message text, and a fourth wording must not break it.
	 */
	private void assertRefusalIsClassified(Resource resource) {
		assertThat(resource.getErrors())
				.as("a referential refusal is classified, on every backend and every path")
				.anySatisfy(diagnostic -> assertThat(diagnostic)
						.asInstanceOf(org.assertj.core.api.InstanceOfAssertFactories
								.type(PersistenceDiagnostic.class))
						.extracting(PersistenceDiagnostic::code)
						.isEqualTo(PersistenceDiagnostic.CODE_REFERENTIAL_INTEGRITY));
	}

	/**
	 * A template of plain attribute assignments produces the same result as the load path
	 * (issue #228).
	 * <p>
	 * Such a template now runs as one statement — {@code UPDATE … SET} / {@code updateMany} —
	 * instead of decoding every match, patching it and writing it back. The promise is that the
	 * caller cannot tell: same values, same count, same everything a caller may rely on. §191 is
	 * what makes the change legitimate (update-by-selector never carried EMF change semantics),
	 * but the stored result must be identical either way.
	 */
	@Test
	@RequiresCapabilities(command = CommandFeature.UPDATE_BY_SELECTOR,
			query = QueryFeature.WHERE_COMPARISON)
	public void aSetBasedTemplateStoresWhatTheLoadPathWouldStore() throws Exception {
		saveQueryFixture();
		ChangeEntry setName = changeEntry(DeltaKind.SET, personName);
		setName.setValueNew("Renamed");
		ChangeEntry setAge = changeEntry(DeltaKind.SET, personAge);
		setAge.setValueNew("77");
		UpdateCommand update = updateCommand(
				QueryBuilder.from(personClass)
						.where(Expressions.path(personAge).ge(40))
						.build(),
				setName, setAge);

		long affected = commands(createBackendResourceSet()).execute(update);

		assertThat(affected).as("the count is the number of matches").isEqualTo(2);
		Resource loaded = loadAll(createBackendResourceSet(), "Person");
		assertThat(loaded.getContents()).hasSize(3);
		assertThat(loaded.getContents())
				.filteredOn(person -> "Renamed".equals(person.eGet(personName)))
				.as("both matches carry both assignments")
				.hasSize(2)
				.allSatisfy(person -> assertThat(((Number) person.eGet(personAge)).intValue()).isEqualTo(77));
		assertThat(loaded.getContents())
				.filteredOn(person -> "Alice".equals(person.eGet(personName)))
				.as("and the non-match is untouched")
				.singleElement()
				.satisfies(person -> assertThat(((Number) person.eGet(personAge)).intValue()).isEqualTo(30));
	}

	/**
	 * The count is matches, not changed rows (issue #228).
	 * <p>
	 * The one place a set-based statement could quietly disagree with the load path, and with
	 * itself across flavors: assigning a value a row already holds changes nothing, and MariaDB
	 * reports rows <em>changed</em> where H2 and PostgreSQL report rows matched. The load path
	 * counts what it applied the template to, so this must too — on every binding.
	 */
	@Test
	@RequiresCapabilities(command = CommandFeature.UPDATE_BY_SELECTOR, query = QueryFeature.WHERE_EQ)
	public void aSetBasedUpdateCountsMatchesEvenWhenNothingChanges() throws Exception {
		saveQueryFixture();
		ChangeEntry setName = changeEntry(DeltaKind.SET, personName);
		setName.setValueNew("Alice");
		UpdateCommand update = updateCommand(
				QueryBuilder.from(personClass)
						.where(Expressions.path(personName).eq("Alice"))
						.build(),
				setName);

		assertThat(commands(createBackendResourceSet()).execute(update))
				.as("a no-op assignment still matched one object")
				.isEqualTo(1);
	}

	/**
	 * {@code UNSET} on a primitively typed attribute leaves the type default, exactly as
	 * {@code eUnset} does (issue #228).
	 * <p>
	 * Pinned as an equality, not as "null or zero": {@code age} is an {@code EInt}, so the load
	 * path leaves {@code 0} there and the statement must leave the same thing. Writing SQL
	 * {@code NULL} instead was the first implementation, it passed on h2, and PostgreSQL
	 * rejected it — the strict database catching a divergence the lenient one hid, which is what
	 * the flavor matrix exists for.
	 */
	@Test
	@RequiresCapabilities(command = CommandFeature.UPDATE_BY_SELECTOR, query = QueryFeature.WHERE_EQ)
	public void aSetBasedUnsetLeavesThePrimitiveDefault() throws Exception {
		saveQueryFixture();
		UpdateCommand update = updateCommand(
				QueryBuilder.from(personClass)
						.where(Expressions.path(personName).eq("Alice"))
						.build(),
				changeEntry(DeltaKind.UNSET, personAge));

		assertThat(commands(createBackendResourceSet()).execute(update)).isEqualTo(1);

		EObject alice = findById(loadAll(createBackendResourceSet(), "Person"), "1");
		assertThat(((Number) alice.eGet(personAge)).intValue())
				.as("the EInt default, which is what eUnset leaves — not 30, and not null")
				.isZero();
	}

	/**
	 * {@code UNSET} on a reference-typed attribute really does store nothing (issue #228).
	 * <p>
	 * The companion of the case above, and the one that exercises the other branch: {@code name}
	 * is an {@code EString}, whose default is {@code null}, so here the statement genuinely has
	 * to write nothing at all. On JPA that is the JPQL literal {@code NULL} rather than a bound
	 * parameter, because a null parameter carries no SQL type for PostgreSQL to use.
	 */
	@Test
	@RequiresCapabilities(command = CommandFeature.UPDATE_BY_SELECTOR,
			query = QueryFeature.WHERE_COMPARISON)
	public void aSetBasedUnsetClearsANullableAttribute() throws Exception {
		saveQueryFixture();
		UpdateCommand update = updateCommand(
				QueryBuilder.from(personClass)
						.where(Expressions.path(personAge).ge(50))
						.build(),
				changeEntry(DeltaKind.UNSET, personName));

		assertThat(commands(createBackendResourceSet()).execute(update)).isEqualTo(1);

		Resource loaded = loadAll(createBackendResourceSet(), "Person");
		assertThat(loaded.getContents())
				.filteredOn(person -> ((Number) person.eGet(personAge)).intValue() == 50)
				.singleElement()
				.satisfies(person -> assertThat(person.eGet(personName))
						.as("an EString default is null, so the attribute is empty")
						.isNull());
		assertThat(loaded.getContents())
				.filteredOn(person -> "Alice".equals(person.eGet(personName)))
				.as("the non-matches keep their names")
				.hasSize(1);
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
	@RequiresCapabilities(command = CommandFeature.UPDATE_BY_SELECTOR, query = QueryFeature.WHERE_EQ)
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
	@RequiresCapabilities(command = CommandFeature.UPDATE_BY_SELECTOR)
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
	@RequiresCapabilities(command = CommandFeature.UPDATE_BY_SELECTOR, query = QueryFeature.WHERE_EQ)
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
	@RequiresCapabilities(command = CommandFeature.UPDATE_BY_SELECTOR)
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
	@RequiresCapabilities(command = CommandFeature.UPDATE_BY_SELECTOR, query = QueryFeature.WHERE_EQ)
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
	@RequiresCapabilities(command = CommandFeature.UPDATE_BY_SELECTOR, query = QueryFeature.WHERE_EQ)
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
	@RequiresCapabilities(command = CommandFeature.INSERT)
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

	// the geo vocabulary is capability-gated declaratively (issue #160): declared →
	// the differential corpus below runs; undeclared → the generic refusal test
	// asserts the GEO_WITHIN/GEO_DISTANCE Diagnostic instead

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
	@RequiresCapabilities(query = QueryFeature.GEO_WITHIN)
	public void geoBoxOverSplitAndPackedSubjects() throws Exception {
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
	@RequiresCapabilities(query = QueryFeature.GEO_WITHIN)
	public void geoWrapAroundBoxCrossesTheAntimeridian() throws Exception {
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
	@RequiresCapabilities(query = QueryFeature.GEO_WITHIN)
	public void geoPolygonOverThePackedSubject() throws Exception {
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
	@RequiresCapabilities(query = { QueryFeature.GEO_DISTANCE, QueryFeature.WHERE_COMPARISON })
	public void geoDistanceThresholdsOnBothBindings() throws Exception {
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
	@RequiresCapabilities(query = { QueryFeature.GEO_WITHIN, QueryFeature.LOGICAL_NOT })
	public void geoNegationExcludesUnknownSubjects() throws Exception {
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

	// --------------------------------------------- group representatives (issue #214)

	/**
	 * Two groups, one of them bigger than the window — which is what makes the difference
	 * between "three of many" and "three in all" observable.
	 */
	private List<EObject> representativeCorpus() {
		return List.of(
				newPerson(11, "Ada", 30),
				newPerson(12, "Ben", 30),
				newPerson(13, "Cleo", 30),
				newPerson(14, "Dan", 40),
				newPerson(15, "Eve", 40));
	}

	/** The rows of a representative query, keyed by group, from the oracle and the backend. */
	private Map<Integer, List<String>> representativeNames(Query query, boolean backend) throws Exception {
		Map<Integer, List<String>> byAge = new LinkedHashMap<>();
		if (backend) {
			save(createBackendResourceSet(), "Person", representativeCorpus().toArray(EObject[]::new));
			try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
				result.rows().forEach(row -> byAge.put(((Number) row.get("age")).intValue(),
						namesOf(row.get("top"))));
			}
			return byAge;
		}
		try (QueryResult oracle = MemoryQueries.execute(query, representativeCorpus(), null)) {
			oracle.rows().forEach(row -> byAge.put(((Number) row.get("age")).intValue(),
					namesOf(row.get("top"))));
		}
		return byAge;
	}

	/**
	 * The cell contract of decision R1: a representative column holds EObjects, never null,
	 * in the declared order.
	 */
	@SuppressWarnings("unchecked")
	private List<String> namesOf(Object cell) {
		assertThat(cell).as("a representative cell is a list, never null").isInstanceOf(List.class);
		return ((List<EObject>) cell).stream().map(object -> (String) object.eGet(personName)).toList();
	}

	@Test
	@RequiresCapabilities(query = { QueryFeature.GROUP_REPRESENTATIVES, QueryFeature.GROUP_BY,
			QueryFeature.AGG_COUNT, QueryFeature.SORT })
	public void groupRepresentativesHandOutTheGroupsOwnDocuments() throws Exception {
		Query query = QueryBuilder.from(personClass)
				.groupBy(personAge)
				.countOf("cnt")
				.representativesOrderedBy("top", 2, SortDirection.ASC, personName)
				.build();
		Map<Integer, List<String>> expected = Map.of(30, List.of("Ada", "Ben"), 40, List.of("Dan", "Eve"));
		assertThat(representativeNames(query, false)).isEqualTo(expected);
		assertThat(representativeNames(query, true)).isEqualTo(expected);

		// the group's full size is an ordinary COUNT aggregate — that is what makes the
		// truncation of the first group visible, rather than a field of its own
		try (QueryResult result = queryable(createBackendResourceSet()).query(query)) {
			Map<Integer, Long> counts = result.rows().collect(Collectors.toMap(
					row -> ((Number) row.get("age")).intValue(),
					row -> ((Number) row.get("cnt")).longValue()));
			assertThat(counts).containsEntry(30, 3L).containsEntry(40, 2L);
		}
	}

	@Test
	@RequiresCapabilities(query = { QueryFeature.GROUP_REPRESENTATIVES, QueryFeature.GROUP_BY,
			QueryFeature.AGG_COUNT, QueryFeature.SORT })
	public void groupRepresentativeWindowSkipsWithinTheGroup() throws Exception {
		Query offsetQuery = QueryBuilder.from(personClass)
				.groupBy(personAge)
				.countOf("cnt")
				.representativesOrderedBy("top", 2, SortDirection.ASC, personName)
				.build();
		((GroupByStage) offsetQuery.getApply().getStages().get(0))
				.getRepresentatives().setOffset(Expressions.literal(1));
		Map<Integer, List<String>> windowed = Map.of(30, List.of("Ben", "Cleo"), 40, List.of("Eve"));
		assertThat(representativeNames(offsetQuery, false)).isEqualTo(windowed);
		assertThat(representativeNames(offsetQuery, true)).isEqualTo(windowed);
	}

	@Test
	@RequiresCapabilities(query = { QueryFeature.GROUP_REPRESENTATIVES, QueryFeature.GROUP_BY,
			QueryFeature.AGG_COUNT, QueryFeature.SORT })
	public void groupSurvivesAnEmptyRepresentativeWindow() throws Exception {
		// an offset past the end of every group: empty cells, but the rows and their
		// aggregates still appear
		Query query = QueryBuilder.from(personClass)
				.groupBy(personAge)
				.countOf("cnt")
				.representativesOrderedBy("top", 2, SortDirection.ASC, personName)
				.build();
		((GroupByStage) query.getApply().getStages().get(0))
				.getRepresentatives().setOffset(Expressions.literal(9));
		Map<Integer, List<String>> empty = Map.of(30, List.of(), 40, List.of());
		assertThat(representativeNames(query, false)).isEqualTo(empty);
		assertThat(representativeNames(query, true)).isEqualTo(empty);
	}

	// ------------------------------------------------------ intervals (issue #215)

	/**
	 * The interval fixture. Five bookings on a small integer axis, plus one with an open end
	 * and one whose bounds are inverted — the two rows that separate the readings the
	 * concept had to decide (§A.4, §A.5.3).
	 */
	private List<EObject> bookings() {
		return List.of(
				booking(1, "early", 1, 5),
				booking(2, "adjacent", 5, 9),
				booking(3, "late", 20, 30),
				booking(4, "open", 40, null),
				booking(5, "broken", 60, 50));
	}

	private EObject booking(int id, String label, Integer from, Integer to) {
		EClass bookingClass = (EClass) tckPackage.getEClassifier("Booking");
		EObject booking = EcoreUtil.create(bookingClass);
		booking.eSet(bookingClass.getEStructuralFeature("bid"), idValue(bookingClass, id));
		booking.eSet(bookingClass.getEStructuralFeature("label"), label);
		booking.eSet(bookingClass.getEStructuralFeature("validFrom"), from);
		if (to != null) {
			booking.eSet(bookingClass.getEStructuralFeature("validTo"), to);
		}
		return booking;
	}

	/**
	 * The closed-closed reading of the fixture — the default a measurement range wants: both
	 * stored bounds belong to the interval and an unset one leaves the answer UNKNOWN.
	 */
	private IntervalSubject closedSubject() {
		EClass bookingClass = (EClass) tckPackage.getEClassifier("Booking");
		return Expressions.intervalSubject(
				Expressions.propertyPath(bookingClass.getEStructuralFeature("validFrom")),
				Expressions.propertyPath(bookingClass.getEStructuralFeature("validTo")));
	}

	/** The temporal reading: half-open {@code [from, to)}, an unset end means "still valid". */
	private IntervalSubject halfOpenSubject() {
		EClass bookingClass = (EClass) tckPackage.getEClassifier("Booking");
		return Expressions.intervalSubject(
				Expressions.propertyPath(bookingClass.getEStructuralFeature("validFrom")),
				Expressions.propertyPath(bookingClass.getEStructuralFeature("validTo")),
				true, false, true);
	}

	/** The differential contract, as for geo: oracle first, then the backend against it. */
	private void assertIntervalDifferential(Query query, String... expectedLabels) throws Exception {
		EClass bookingClass = (EClass) tckPackage.getEClassifier("Booking");
		EStructuralFeature label = bookingClass.getEStructuralFeature("label");
		try (QueryResult oracle = MemoryQueries.execute(query, bookings(), null)) {
			assertThat(oracle.objects().map(found -> found.eGet(label)))
					.containsExactlyInAnyOrder((Object[]) expectedLabels);
		}
		save(createBackendResourceSet(), "Booking", bookings().toArray(EObject[]::new));
		QueryableResource resource = (QueryableResource) createBackendResourceSet()
				.createResource(uriFor("Booking"));
		try (QueryResult result = resource.query(query)) {
			assertThat(result.objects().map(found -> found.eGet(label)))
					.containsExactlyInAnyOrder((Object[]) expectedLabels);
		}
	}

	private Query intervalQuery(Expression predicate) {
		return QueryBuilder.from((EClass) tckPackage.getEClassifier("Booking"))
				.where(predicate).build();
	}

	@Test
	@RequiresCapabilities(query = QueryFeature.INTERVAL_MATCH)
	public void intervalRelationsOverAClosedSubject() throws Exception {
		// [4, 6] touches "early" (ends at 5) and "adjacent" (starts at 5)
		assertIntervalDifferential(intervalQuery(Expressions.intersects(closedSubject(), 4, 6)),
				"early", "adjacent");
		// only "early" fits completely inside [0, 6]
		assertIntervalDifferential(intervalQuery(Expressions.intervalWithin(closedSubject(), 0, 6)),
				"early");
		// "late" [20, 30] is the only one covering [22, 25]
		assertIntervalDifferential(intervalQuery(Expressions.intervalContains(closedSubject(), 22, 25)),
				"late");
	}

	@Test
	@RequiresCapabilities(query = QueryFeature.INTERVAL_MATCH)
	public void intervalAtIsContainmentOfASinglePoint() throws Exception {
		// the degenerate query interval — "valid at t", and 5 belongs to both neighbours
		assertIntervalDifferential(intervalQuery(Expressions.intervalAt(closedSubject(), 5)),
				"early", "adjacent");
		assertIntervalDifferential(intervalQuery(Expressions.intervalAt(closedSubject(), 25)),
				"late");
	}

	/**
	 * The reason the subject carries its own boundary convention (§A.4): under the half-open
	 * reading the same point does <em>not</em> belong to two adjacent periods, and no bound
	 * on the query side can express that.
	 */
	@Test
	@RequiresCapabilities(query = QueryFeature.INTERVAL_MATCH)
	public void halfOpenSubjectsDoNotOverlapAtTheirSeam() throws Exception {
		assertIntervalDifferential(intervalQuery(Expressions.intervalAt(halfOpenSubject(), 5)),
				"adjacent");
		assertIntervalDifferential(intervalQuery(Expressions.intersects(halfOpenSubject(), 5, 5)),
				"adjacent");
	}

	/**
	 * The other half of §A.4: an unset upper bound is "still valid" only where the subject
	 * says so. Undeclared, it makes that one bound comparison UNKNOWN — and the conjunction
	 * then decides under ordinary 3VL, which is the rule the implementation round settled on
	 * (§A.5.4): UNKNOWN survives only where the missing bound actually matters.
	 */
	@Test
	@RequiresCapabilities(query = { QueryFeature.INTERVAL_MATCH, QueryFeature.LOGICAL_NOT })
	public void unsetBoundsAreUnboundedOnlyWhenDeclared() throws Exception {
		// declared unbounded: "open" runs from 40 into the future
		assertIntervalDifferential(intervalQuery(Expressions.intervalAt(halfOpenSubject(), 500)),
				"open");
		// undeclared: the unknown end leaves the answer UNKNOWN, so the row stays out
		assertIntervalDifferential(intervalQuery(Expressions.intervalAt(closedSubject(), 500)));
		// and it stays out under negation too, because there the unknown end still decides:
		// 45 is past the start of "open", so only its missing end could answer
		assertIntervalDifferential(
				intervalQuery(Expressions.not(Expressions.intervalAt(closedSubject(), 45))),
				"early", "adjacent", "late", "broken");
		// where the known bound already answers, the conjunction is FALSE rather than
		// UNKNOWN — "open" starts at 40, so it cannot be valid at 25, and its negation holds
		assertIntervalDifferential(
				intervalQuery(Expressions.not(Expressions.intervalAt(closedSubject(), 25))),
				"early", "adjacent", "open", "broken");
	}

	/**
	 * An empty subject row matches nothing, {@code WITHIN} included (§A.5.3) — vacuous truth
	 * is what a naive pair of comparisons would return, and the row "broken" [60, 50] exists
	 * in the fixture to pin that it does not.
	 */
	@Test
	@RequiresCapabilities(query = QueryFeature.INTERVAL_MATCH)
	public void invertedSubjectRowsMatchNoRelation() throws Exception {
		// [45, 65] reaches into "open" and straddles the bounds of "broken" — only one matches
		assertIntervalDifferential(intervalQuery(Expressions.intersects(halfOpenSubject(), 45, 65)),
				"open");
		// WITHIN over everything: the empty row stays out, and so does the unbounded one
		assertIntervalDifferential(intervalQuery(Expressions.intervalWithin(halfOpenSubject(), 0, 100)),
				"early", "adjacent", "late");
	}

	// -------------------------------------------------- command transactions (issue #108)

	/**
	 * The issue-#114 contract: the live resource matches the binding's declaration. Every
	 * declared feature executes (the command/transaction cases above cover that), an
	 * undeclared one refuses before any work with a Diagnostic naming the feature, and the
	 * per-EClass answer defaults to the backend-wide one. The undeclared direction for the
	 * transaction bracket is asserted here, because the bracket has no query surface the
	 * generic refusal probes could reach.
	 */
	@Test
	@RequiresCapabilities(command = { CommandFeature.INSERT, CommandFeature.DELETE_BY_SELECTOR,
			CommandFeature.UPDATE_BY_SELECTOR })
	public void commandCapabilitiesMatchDeclaredBehaviour() throws Exception {
		saveQueryFixture();
		CommandResource resource = commands(createBackendResourceSet());
		PersistenceCapabilities effective = ((PersistenceResource) resource).capabilities();
		assertThat(effective).isNotNull();
		CommandCapabilities capabilities = effective.command();
		assertThat(capabilities).isNotNull();
		for (CommandFeature feature : List.of(CommandFeature.INSERT,
				CommandFeature.DELETE_BY_SELECTOR, CommandFeature.UPDATE_BY_SELECTOR)) {
			assertThat(capabilities.supports(feature)).isTrue();
			// unnarrowed features answer per EClass exactly like backend-wide
			assertThat(capabilities.supports(feature, personClass)).isTrue();
			assertThat(capabilities.supported()).contains(feature);
		}
		// the transaction bracket is a store feature now, not a command verb (issue #134, §5a)
		boolean bracketDeclared = declaredCapabilities().store()
				.supports(StoreFeature.TRANSACTION_BRACKET);
		assertThat(effective.store().supports(StoreFeature.TRANSACTION_BRACKET))
				.isEqualTo(bracketDeclared);
		assertThat(capabilities.supported())
				.noneMatch(feature -> "TRANSACTION_BRACKET".equals(feature.getName()));
		if (!bracketDeclared) {
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
	@RequiresCapabilities(store = StoreFeature.TRANSACTION_BRACKET,
			command = { CommandFeature.INSERT, CommandFeature.UPDATE_BY_SELECTOR },
			query = QueryFeature.WHERE_EQ)
	public void commandTransactionCommitsAtomically() throws Exception {
		saveQueryFixture();
		CommandResource resource = commands(createBackendResourceSet());
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
	@RequiresCapabilities(store = StoreFeature.TRANSACTION_BRACKET,
			command = { CommandFeature.INSERT, CommandFeature.UPDATE_BY_SELECTOR })
	public void commandTransactionRollbackLeavesNoTrace() throws Exception {
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

	// composite ids are conformance core (issue #134): every store can key on a
	// concatenation, so there is no declarable value to gate on and no refusal shape

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
	@RequiresCapabilities(query = { QueryFeature.WHERE_EQ, QueryFeature.LOGICAL_AND })
	public void compositeIdSelectorResolvesASingleObject() throws Exception {
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
	@RequiresCapabilities(command = CommandFeature.DELETE_BY_SELECTOR,
			query = QueryFeature.WHERE_COMPARISON)
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

	// -------------------------------------------- capability declaration (issue #160)

	/**
	 * The refusal direction of contract §2B, mechanically: every probeable query feature the
	 * backend does <em>not</em> declare must be refused with an {@link IOException} whose
	 * message names the feature — never silently post-filtered. The declared direction is
	 * the gated cases themselves: declared means they run and must pass.
	 */
	@Test
	public void undeclaredFeaturesAreRefusedWithADiagnostic() throws Exception {
		Set<QueryFeature> declared = declaredCapabilities().query().supported();
		for (Map.Entry<QueryFeature, Query> probe : featureProbes().entrySet()) {
			if (declared.contains(probe.getKey())) {
				continue;
			}
			QueryableResource resource = queryable(createBackendResourceSet());
			assertThatThrownBy(() -> resource.query(probe.getValue()).close())
					.as("undeclared feature %s must be refused with a Diagnostic",
							probe.getKey().getName())
					.isInstanceOf(IOException.class)
					.hasMessageContaining(probe.getKey().getName());
		}
	}

	/**
	 * Contract §4, the derived cross product (issue #175): command <b>verbs</b> are core
	 * vocabulary, the selector vocabulary is a capability, and their combination is derived —
	 * never declared. If a backend declares verb V and expression capability X, then V over a
	 * selector using X must work; otherwise the flag set explodes into
	 * {@code DELETE_WITH_GEO_SELECTOR} and friends.
	 * <p>
	 * Before this, the command cases exercised a handful of hand-picked selectors, so a
	 * backend could declare {@code DELETE_BY_SELECTOR} and {@code EXISTS} and break on the
	 * combination without any test noticing. Here <b>the case count follows the
	 * declaration</b>: every declared selector-shaped verb is paired with every plain-filter
	 * probe from the same corpus the refusal test uses, and each pair asserts the consistency
	 * the contract promises — a declared feature executes, an undeclared one is refused with a
	 * Diagnostic naming it.
	 * <p>
	 * The effect asserted is the one that holds for every selector regardless of what it
	 * matches: the command reports exactly as many affected objects as the same selector finds
	 * as a query. A probe that matches nothing is still worth running — a broken join or
	 * subselect fails when the statement executes, not when it matches.
	 * <p>
	 * A declared capability may still be refused for a <em>concrete</em> feature, and that is
	 * contract, not a loophole: declaration is backend-wide (§5a), while whether one feature is
	 * served follows from its mapping and is {@code validate()}'s answer — Mongo declares
	 * nested paths and refuses one that crosses a document boundary. So a declared cell must
	 * either execute with the right reach or refuse with a {@code Diagnostic}; what it must
	 * never do is answer wrongly. To keep that from degenerating into "refuse everything and
	 * stay green", each verb must additionally have executed at least one cell for real.
	 *
	 * @return one dynamic case per declared verb × plain-filter selector, plus one case per
	 *         verb asserting that the verb really ran somewhere
	 */
	@TestFactory
	public Stream<DynamicTest> commandSelectorCrossProductFollowsTheDeclaration() {
		Set<CommandFeature> verbs = declaredCapabilities().command().supported();
		Set<QueryFeature> declaredQuery = declaredCapabilities().query().supported();
		Map<QueryFeature, Query> selectors = plainFilterProbes();
		List<DynamicTest> cases = new ArrayList<>();
		for (CommandFeature verb : List.of(CommandFeature.DELETE_BY_SELECTOR, CommandFeature.UPDATE_BY_SELECTOR)) {
			if (!verbs.contains(verb)) {
				continue;
			}
			AtomicInteger executed = new AtomicInteger();
			for (Map.Entry<QueryFeature, Query> selector : selectors.entrySet()) {
				QueryFeature feature = selector.getKey();
				cases.add(DynamicTest.dynamicTest(verb.getName() + " × " + feature.getName(),
						() -> assertCommandOverSelector(verb, feature, selector.getValue(),
								declaredQuery.contains(feature), executed)));
			}
			cases.add(DynamicTest.dynamicTest(verb.getName() + " executed at least one selector",
					() -> assertThat(executed.get())
							.as("%s refused every selector in the corpus — a verb that never runs"
									+ " makes the cross product vacuous", verb.getName())
							.isPositive()));
		}
		return cases.stream();
	}

	/**
	 * One cell of the cross product: the command either executes with the same reach as its
	 * selector, or is refused with a Diagnostic naming the undeclared feature.
	 */
	/**
	 * Values for whatever parameters a probe declares. The corpus is generated, so a selector
	 * may carry a {@code ParameterRef} — since #202 the command side can bind them, which is
	 * what let `PARAMETERS` rejoin the corpus rather than being excluded from it.
	 */
	private static Map<String, Object> bindingsFor(Query selector) {
		Map<String, Object> bindings = new LinkedHashMap<>();
		selector.getParameters().forEach(declaration -> bindings.put(declaration.getName(), 1));
		return bindings;
	}

	private void assertCommandOverSelector(CommandFeature verb, QueryFeature feature, Query selector,
			boolean declared, AtomicInteger executed) throws Exception {
		// every cell restores the fixture: the dynamic cases of one factory share the
		// per-method setup, and a delete would otherwise empty the store for its successors
		saveQueryFixture();

		if (!declared) {
			assertThatThrownBy(() -> execute(command(verb, EcoreUtil.copy(selector)), bindingsFor(selector)))
					.as("%s over an undeclared %s selector must be refused with a Diagnostic",
							verb.getName(), feature.getName())
					.isInstanceOf(IOException.class)
					.hasMessageContaining(feature.getName());
			return;
		}

		long matched;
		try {
			try (QueryResult result = queryable(createBackendResourceSet())
					.query(EcoreUtil.copy(selector), bindingsFor(selector), null)) {
				matched = result.objects().count();
			}
		} catch (IOException refused) {
			assertRefusedWithADiagnostic(verb, feature, refused);
			return;
		}
		long affected;
		try {
			affected = execute(command(verb, EcoreUtil.copy(selector)), bindingsFor(selector));
		} catch (IOException refused) {
			assertRefusedWithADiagnostic(verb, feature, refused);
			return;
		}
		executed.incrementAndGet();
		assertThat(affected)
				.as("%s must affect exactly what its %s selector matches", verb.getName(), feature.getName())
				.isEqualTo(matched);
	}

	/**
	 * A declared capability refused for one concrete feature is conforming — but only as a
	 * <em>refusal</em>: the failure must carry a {@link Diagnostic}, which is what separates
	 * "this mapping is not served" from a backend that broke while executing.
	 */
	private static void assertRefusedWithADiagnostic(CommandFeature verb, QueryFeature feature,
			IOException failure) {
		for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
			if (cause instanceof QueryException queryException && queryException.getDiagnostic() != null) {
				return;
			}
		}
		throw new AssertionError(String.format(
				"%s over a %s selector failed without a Diagnostic — a declared capability may be"
						+ " refused for a concrete feature, but not fail while executing: %s",
				verb.getName(), feature.getName(), failure), failure);
	}

	/** The command for a verb over {@code selector} — the selector is the only variable here. */
	private Object command(CommandFeature verb, Query selector) {
		if (verb == CommandFeature.DELETE_BY_SELECTOR) {
			DeleteCommand delete = CommandFactory.eINSTANCE.createDeleteCommand();
			delete.setSelector(selector);
			return delete;
		}
		ChangeEntry setName = changeEntry(DeltaKind.SET, personName);
		setName.setValueNew("patched");
		return updateCommand(selector, setName);
	}

	private long execute(Object command, Map<String, Object> bindings) throws IOException {
		CommandResource resource = commands(createBackendResourceSet());
		return command instanceof DeleteCommand delete
				? resource.execute(delete, bindings, null)
				: resource.execute((UpdateCommand) command, bindings, null);
	}

	/**
	 * The selector corpus: every {@link #featureProbes() probe} that is a plain filter, which
	 * is what a command selector must be ({@code commandSelectorMustBeAPlainFilter}). Derived
	 * mechanically rather than listed, so a probe added for a new feature joins the cross
	 * product by itself.
	 */
	private Map<QueryFeature, Query> plainFilterProbes() {
		Map<QueryFeature, Query> filters = new LinkedHashMap<>();
		featureProbes().forEach((feature, query) -> {
			if (query.getPredicate() != null && query.getSelect().isEmpty() && query.getApply() == null
					&& query.getOrderBy().isEmpty() && query.getExpand().isEmpty()
					&& query.getTop() == 0 && query.getSkip() == 0
					&& !query.isDistinct() && !query.isCountOnly() && !query.isWithScores()) {
				filters.put(feature, query);
			}
		});
		return filters;
	}

	/**
	 * One minimal Person-rooted query per probeable {@link QueryFeature}. A probe may use
	 * auxiliary features beyond the one it targets; the validator names every unsupported
	 * feature in the Diagnostic, so the targeted name always appears in the refusal.
	 */
	private Map<QueryFeature, Query> featureProbes() {
		Map<QueryFeature, Query> probes = new LinkedHashMap<>();
		probes.put(QueryFeature.WHERE_EQ, QueryBuilder.from(personClass)
				.where(Expressions.path(personName).eq("x")).build());
		probes.put(QueryFeature.WHERE_NE, QueryBuilder.from(personClass)
				.where(Expressions.path(personName).ne("x")).build());
		probes.put(QueryFeature.WHERE_COMPARISON, QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).ge(1)).build());
		probes.put(QueryFeature.WHERE_RANGE, QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).between(1, 2)).build());
		probes.put(QueryFeature.IS_NULL, QueryBuilder.from(personClass)
				.where(Expressions.path(personName).isNotNull()).build());
		probes.put(QueryFeature.IN, QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).in(1, 2)).build());
		probes.put(QueryFeature.WHERE_STRING_MATCH, QueryBuilder.from(personClass)
				.where(Expressions.path(personName).startsWith("x")).build());
		probes.put(QueryFeature.STRING_MATCH_CASE_INSENSITIVE, QueryBuilder.from(personClass)
				.where(Expressions.path(personName).containsIgnoreCase("x")).build());
		probes.put(QueryFeature.STRING_MATCH_FUZZY, QueryBuilder.from(personClass)
				.where(Expressions.path(personName).fuzzy("x")).build());
		probes.put(QueryFeature.LOGICAL_AND, QueryBuilder.from(personClass)
				.where(Expressions.and(Expressions.path(personName).eq("x"),
						Expressions.path(personName).eq("y"))).build());
		probes.put(QueryFeature.LOGICAL_OR, QueryBuilder.from(personClass)
				.where(Expressions.or(Expressions.path(personName).eq("x"),
						Expressions.path(personName).eq("y"))).build());
		probes.put(QueryFeature.LOGICAL_NOT, QueryBuilder.from(personClass)
				.where(Expressions.not(Expressions.path(personName).eq("x"))).build());
		probes.put(QueryFeature.EXISTS, QueryBuilder.from(personClass)
				.where(Expressions.any(Expressions.propertyPath(personAddresses),
						a -> a.path(addressStreet).startsWith("x"))).build());
		probes.put(QueryFeature.FOR_ALL, QueryBuilder.from(personClass)
				.where(Expressions.all(Expressions.propertyPath(personAddresses),
						a -> a.path(addressStreet).startsWith("x"))).build());
		probes.put(QueryFeature.FIELD_TO_FIELD, QueryBuilder.from(personClass)
				.where(Expressions.path(personName).eq(Expressions.path(personName))).build());
		probes.put(QueryFeature.ARITHMETIC, QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).plus(1).gt(1)).build());
		probes.put(QueryFeature.STRING_FUNCTIONS, QueryBuilder.from(personClass)
				.where(Expressions.path(personName).toLower().eq("x")).build());
		probes.put(QueryFeature.STRING_FUNCTIONS_EXTENDED, QueryBuilder.from(personClass)
				.where(Expressions.path(personName).indexOf("x").eq(1)).build());
		probes.put(QueryFeature.NUMERIC_FUNCTIONS, QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).dividedBy(4).floor().eq(1)).build());
		probes.put(QueryFeature.TEMPORAL_FUNCTIONS, QueryBuilder.from(personClass)
				.where(Expressions.path(personBirthday).year().eq(2000)).build());
		probes.put(QueryFeature.TYPE_CHECK, QueryBuilder.from(personClass)
				.where(Expressions.isOf(personClass)).build());
		probes.put(QueryFeature.TYPE_CAST, QueryBuilder.from(personClass)
				.where(Expressions.pathAs(personClass, personAge).gt(1)).build());
		probes.put(QueryFeature.COLLECTION_COUNT, QueryBuilder.from(personClass)
				.where(Expressions.count(Expressions.propertyPath(personAddresses)).ge(1)).build());
		probes.put(QueryFeature.COLLECTION_COUNT_FILTERED, QueryBuilder.from(personClass)
				.where(Expressions.count(Expressions.propertyPath(personAddresses),
						a -> a.path(addressStreet).startsWith("x")).ge(1)).build());
		probes.put(QueryFeature.FEATUREPATH_NESTED, QueryBuilder.from(personClass)
				.where(Expressions.path(personBestFriend, personName).eq("x")).build());
		probes.put(QueryFeature.SORT, QueryBuilder.from(personClass)
				.orderByAsc(personName).build());
		probes.put(QueryFeature.SORT_EXPRESSION, QueryBuilder.from(personClass)
				.orderByAsc(Expressions.neg(Expressions.path(personAge)).toExpression()).build());
		probes.put(QueryFeature.LIMIT, QueryBuilder.from(personClass).top(1).build());
		probes.put(QueryFeature.SKIP, QueryBuilder.from(personClass).skip(1).build());
		probes.put(QueryFeature.DISTINCT, QueryBuilder.from(personClass).distinct().build());
		probes.put(QueryFeature.COUNT, QueryBuilder.from(personClass).countOnly().build());
		probes.put(QueryFeature.PROJECTION, QueryBuilder.from(personClass)
				.selectAs("n", personName).build());
		probes.put(QueryFeature.PROJECTION_NESTED, QueryBuilder.from(personClass)
				.selectAs("s", personBestFriend, personName).build());
		probes.put(QueryFeature.PROJECTION_EXPRESSION, QueryBuilder.from(personClass)
				.selectAs("c", Expressions.path(personAge).plus(1).toExpression()).build());
		probes.put(QueryFeature.EXPAND, QueryBuilder.from(personClass)
				.expand(personBestFriend).build());
		// declared, not just referenced: a ParameterRef without its ParameterDecl is an
		// incomplete query, and the cross product needs the declaration to know what to bind
		probes.put(QueryFeature.PARAMETERS, QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).eq(Expressions.param("p")))
				.parameter("p", null).build());
		probes.put(QueryFeature.GROUP_BY, QueryBuilder.from(personClass)
				.groupBy(personAge).countOf("cnt").build());
		probes.put(QueryFeature.AGG_AVG, QueryBuilder.from(personClass)
				.groupBy(personAge).avg("a", personAge).build());
		probes.put(QueryFeature.AGG_MIN, QueryBuilder.from(personClass)
				.groupBy(personAge).min("m", personAge).build());
		probes.put(QueryFeature.AGG_MAX, QueryBuilder.from(personClass)
				.groupBy(personAge).max("m", personAge).build());
		probes.put(QueryFeature.AGG_SUM, QueryBuilder.from(personClass)
				.groupBy(personAge).sum("s", personAge).build());
		probes.put(QueryFeature.AGG_COUNT, QueryBuilder.from(personClass)
				.groupBy(personAge).countOf("cnt").build());
		probes.put(QueryFeature.AGG_COUNT_DISTINCT, QueryBuilder.from(personClass)
				.groupBy(personAge).countDistinct("cd", personName).build());
		probes.put(QueryFeature.GROUP_EXPRESSION, QueryBuilder.from(personClass)
				.groupByAs("k", Expressions.neg(Expressions.path(personAge)).toExpression())
				.countOf("cnt").build());
		probes.put(QueryFeature.PIPELINE, QueryBuilder.from(personClass)
				.groupBy(personAge).countOf("cnt")
				.having(Expressions.aliasRef("cnt").ge(1)).build());
		probes.put(QueryFeature.PIPELINE_COMPUTE, QueryBuilder.from(personClass)
				.groupBy(personAge).countOf("cnt")
				.computeAs("c", Expressions.aliasRef("cnt").toExpression()).build());
		probes.put(QueryFeature.SCORE, QueryBuilder.from(personClass).withScores().build());
		probes.put(QueryFeature.GEO_WITHIN, QueryBuilder.from(personClass)
				.where(Expressions.geoWithin(
						Expressions.geoSubject(Expressions.propertyPath(personAge),
								Expressions.propertyPath(personAge)),
						Expressions.geoBox(Expressions.geoPoint(10, 50),
								Expressions.geoPoint(13, 52))))
				.build());
		probes.put(QueryFeature.GROUP_REPRESENTATIVES, QueryBuilder.from(personClass)
				.groupBy(personAge).countOf("cnt").representatives("top", 1).build());
		probes.put(QueryFeature.INTERVAL_MATCH, QueryBuilder.from(personClass)
				.where(Expressions.intersects(
						Expressions.intervalSubject(Expressions.propertyPath(personAge),
								Expressions.propertyPath(personAge)),
						20, 40))
				.build());
		probes.put(QueryFeature.GEO_DISTANCE, QueryBuilder.from(personClass)
				.where(Expressions.geoDistance(
						Expressions.geoSubject(Expressions.propertyPath(personAge),
								Expressions.propertyPath(personAge)),
						Expressions.geoPoint(11.5, 50.9)).le(1000))
				.build());
		return probes;
	}

	/**
	 * Declaration and live resource must agree (issue #160). The effective set is the
	 * declaration narrowed by a runtime probe — never widened. And the gate's premise is
	 * that nothing declared is missing live, otherwise a gated case would run against a
	 * resource that refuses it: an under-declaring binding hides passing tests as skips,
	 * an over-declaring one is caught here before its gated cases fail confusingly.
	 */
	@Test
	public void effectiveCapabilitiesNeverExceedTheDeclaration() throws Exception {
		PersistenceCapabilities declared = declaredCapabilities();
		PersistenceCapabilities effective = ((PersistenceResource) createBackendResourceSet()
				.createResource(uriFor("Person"))).capabilities();
		assertThat(effective.query().supported()).isSubsetOf(declared.query().supported());
		assertThat(effective.command().supported()).isSubsetOf(declared.command().supported());
		assertThat(effective.store().supported()).isSubsetOf(declared.store().supported());
		assertThat(declared.query().supported())
				.as("a declared query feature must be served by the live resource")
				.isSubsetOf(effective.query().supported());
		assertThat(declared.command().supported())
				.as("a declared command verb must be served by the live resource")
				.isSubsetOf(effective.command().supported());
	}
}
