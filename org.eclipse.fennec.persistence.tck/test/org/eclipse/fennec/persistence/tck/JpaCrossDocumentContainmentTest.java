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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Cross-document containment on the JPA backend — the shape the Mongo backend pins in
 * {@code MongoCrossResourceReferenceTest}: a child that is <b>contained</b> by a parent
 * (its {@code eContainer}) while at the same time being a <b>root of its own
 * {@link Resource}</b> ({@code eDirectResource}). Standard EMF allows it because
 * containment references have {@code resolveProxies=true}, so attaching the child to
 * another resource keeps the container link intact.
 * <p>
 * Both arities are covered: single-valued ({@code Place.location}) and many-valued
 * ({@code Person.addresses}). Every read runs against a fresh {@link ResourceSet} after
 * {@code emf.getCache().evictAll()}, so nothing is answered from a cache.
 * <p>
 * The contract is not backend-specific: whatever Mongo delivers here, JPA has to deliver
 * too. Mongo returns a fully resolved child that is owned by the parent and resident in
 * its own resource. JPA does not support the shape yet, so the cases demanding it are
 * {@code @Disabled} for issue #130 — the save-order-dependent ones and the residency one.
 * The two enabled tests pin what genuinely holds today: plain containment, and that the
 * data round-trips when the parent is saved first.
 *
 * @author Mark Hoffmann
 * @since 13.08.2026
 */
class JpaCrossDocumentContainmentTest {

	static {
		// Same doctrine as AbstractPersistenceTCK (issue #79): H2 caches the JVM zone
		// statically at first use, and this class does not extend the abstract TCK.
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static final String PU_NAME = "xdoc";

	private EPackage tckPackage;
	private EClass personClass;
	private EClass addressClass;
	private EClass placeClass;
	private EClass geoPointClass;
	private EReference personAddresses;
	private EReference placeLocation;

	private EntityManagerFactory emf;

	@BeforeEach
	void setUp() throws IOException {
		tckPackage = loadModel();
		personClass = (EClass) tckPackage.getEClassifier("Person");
		addressClass = (EClass) tckPackage.getEClassifier("Address");
		placeClass = (EClass) tckPackage.getEClassifier("Place");
		geoPointClass = (EClass) tckPackage.getEClassifier("GeoPoint");
		personAddresses = (EReference) personClass.getEStructuralFeature("addresses");
		placeLocation = (EReference) placeClass.getEStructuralFeature("location");

		List<EClassifier> eClasses = new ArrayList<>();
		eClasses.add(personClass);
		eClasses.add(addressClass);
		eClasses.add(tckPackage.getEClassifier("Company"));
		eClasses.add(placeClass);
		eClasses.add(geoPointClass);
		emf = JpaTckSupport.bootstrap(PU_NAME, eClasses);
	}

	@AfterEach
	void tearDown() {
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
		Resource resource = resourceSet.createResource(URI.createURI("tck.ecore"));
		try (InputStream stream = AbstractPersistenceTCK.class.getResourceAsStream("tck.ecore")) {
			resource.load(stream, null);
		}
		EPackage ePackage = (EPackage) resource.getContents().get(0);
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		return ePackage;
	}

	private ResourceSet resourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(tckPackage.getNsURI(), tckPackage);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("jpa", new JPAResourceFactory(emf));
		return resourceSet;
	}

	private URI uriFor(String typeName) {
		return URI.createURI("jpa://" + PU_NAME + "/" + typeName);
	}

	private EObject create(EClass eClass, String idFeature, int id, String nameFeature, String value) {
		EObject object = EcoreUtil.create(eClass);
		object.eSet(eClass.getEStructuralFeature(idFeature), id);
		object.eSet(eClass.getEStructuralFeature(nameFeature), value);
		return object;
	}

	private EObject findById(Resource resource, String id) {
		for (EObject candidate : resource.getContents()) {
			if (id.equals(EcoreUtil.getID(candidate))) {
				return candidate;
			}
		}
		return null;
	}

	private long countRows(String entityAlias) {
		try (EntityManager em = emf.createEntityManager()) {
			return em.createQuery("SELECT COUNT(e) FROM " + entityAlias + " e", Long.class)
					.getSingleResult();
		}
	}

	private static Object value(EObject object, String feature) {
		return object.eGet(object.eClass().getEStructuralFeature(feature));
	}

	// -------------------------------------------------------------------- fixtures

	/**
	 * Builds the cross-document shape and asserts it really <em>is</em> that shape before
	 * anything is persisted — a silently re-parented child would make the rest vacuous.
	 */
	private void assertCrossDocumentShape(EObject child, EObject parent, Resource childResource) {
		assertThat(((InternalEObject) child).eDirectResource())
				.as("child is a root of its own resource")
				.isSameAs(childResource);
		assertThat(child.eContainer())
				.as("child keeps its container (resolveProxies containment)")
				.isSameAs(parent);
	}

	// --------------------------------------------------------------------- the matrix

	@Test
	@Disabled("issue #130 — the parent's save re-INSERTs the child row written by the child resource")
	void singleValuedCrossDocumentContainmentRoundTrips() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject place = create(placeClass, "plid", 1, "name", "Rivendell");
		EObject point = create(geoPointClass, "gid", 11, "type", "Point");
		place.eSet(placeLocation, point);

		Resource pointResource = writeSet.createResource(uriFor("GeoPoint"));
		pointResource.getContents().add(point);
		assertCrossDocumentShape(point, place, pointResource);

		Resource placeResource = writeSet.createResource(uriFor("Place"));
		placeResource.getContents().add(place);

		pointResource.save(null);
		placeResource.save(null);

		// the child must exist exactly once — a private-owned cascade on top of the
		// child resource's own INSERT would double it (or fail on the PK)
		assertThat(countRows("GeoPoint")).as("child row written once").isEqualTo(1);
		assertThat(countRows("Place")).isEqualTo(1);

		emf.getCache().evictAll();

		ResourceSet readSet = resourceSet();
		EObject loadedPlace = findById(readSet.getResource(uriFor("Place"), true), "1");
		assertThat(loadedPlace).as("place loaded").isNotNull();

		EObject location = (EObject) loadedPlace.eGet(placeLocation);
		assertThat(location).as("containment child resolved").isNotNull();
		assertThat(location.eIsProxy()).isFalse();
		assertThat(value(location, "gid")).isEqualTo(11);
		assertThat(value(location, "type")).isEqualTo("Point");
	}

	@Test
	@Disabled("issue #130 — the parent's save re-INSERTs the child row written by the child resource")
	void manyValuedCrossDocumentContainmentRoundTrips() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject person = create(personClass, "pid", 1, "name", "Bilbo");
		EObject address = create(addressClass, "aid", 21, "street", "Bagshot Row");
		address.eSet(addressClass.getEStructuralFeature("city"), "Hobbiton");
		@SuppressWarnings("unchecked")
		List<EObject> addresses = (List<EObject>) person.eGet(personAddresses);
		addresses.add(address);

		Resource addressResource = writeSet.createResource(uriFor("Address"));
		addressResource.getContents().add(address);
		assertCrossDocumentShape(address, person, addressResource);

		Resource personResource = writeSet.createResource(uriFor("Person"));
		personResource.getContents().add(person);

		addressResource.save(null);
		personResource.save(null);

		assertThat(countRows("Address")).as("child row written once").isEqualTo(1);
		assertThat(countRows("Person")).isEqualTo(1);

		emf.getCache().evictAll();

		ResourceSet readSet = resourceSet();
		EObject loadedPerson = findById(readSet.getResource(uriFor("Person"), true), "1");
		assertThat(loadedPerson).as("person loaded").isNotNull();

		@SuppressWarnings("unchecked")
		List<EObject> loadedAddresses = (List<EObject>) loadedPerson.eGet(personAddresses);
		assertThat(loadedAddresses).as("containment children resolved").hasSize(1);
		EObject loadedAddress = loadedAddresses.get(0);
		assertThat(loadedAddress.eIsProxy()).isFalse();
		assertThat(value(loadedAddress, "aid")).isEqualTo(21);
		assertThat(value(loadedAddress, "street")).isEqualTo("Bagshot Row");
	}

	/**
	 * The child resource is a first-class query entry point: loading it must yield the
	 * child even though the child is owned by a parent living in another resource.
	 */
	@Test
	@Disabled("issue #130 — the parent's save re-INSERTs the child row written by the child resource")
	void childResourceLoadsTheContainedChildOnItsOwn() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject person = create(personClass, "pid", 2, "name", "Frodo");
		EObject address = create(addressClass, "aid", 22, "street", "Bag End");
		@SuppressWarnings("unchecked")
		List<EObject> addresses = (List<EObject>) person.eGet(personAddresses);
		addresses.add(address);

		Resource addressResource = writeSet.createResource(uriFor("Address"));
		addressResource.getContents().add(address);
		Resource personResource = writeSet.createResource(uriFor("Person"));
		personResource.getContents().add(person);
		addressResource.save(null);
		personResource.save(null);

		emf.getCache().evictAll();

		ResourceSet readSet = resourceSet();
		Resource loadedAddresses = readSet.getResource(uriFor("Address"), true);
		EObject loadedAddress = findById(loadedAddresses, "22");
		assertThat(loadedAddress).as("child readable through its own resource").isNotNull();
		assertThat(value(loadedAddress, "street")).isEqualTo("Bag End");
	}

	/**
	 * The same shape with the save order reversed — parent first, then the child's own
	 * resource. The parent's private-owned cascade inserts the child row, so the child
	 * resource's own save should land in {@code upsert}'s UPDATE branch.
	 */
	@Test
	void crossDocumentContainmentWithParentSavedFirst() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject place = create(placeClass, "plid", 4, "name", "Bree");
		EObject point = create(geoPointClass, "gid", 14, "type", "Point");
		place.eSet(placeLocation, point);

		Resource pointResource = writeSet.createResource(uriFor("GeoPoint"));
		pointResource.getContents().add(point);
		Resource placeResource = writeSet.createResource(uriFor("Place"));
		placeResource.getContents().add(place);

		placeResource.save(null);
		pointResource.save(null);

		assertThat(countRows("GeoPoint")).as("child row written once").isEqualTo(1);

		emf.getCache().evictAll();

		ResourceSet readSet = resourceSet();
		EObject loadedPlace = findById(readSet.getResource(uriFor("Place"), true), "4");
		EObject location = (EObject) loadedPlace.eGet(placeLocation);
		assertThat(location).as("containment child resolved").isNotNull();
		assertThat(location.eIsProxy()).isFalse();
		assertThat(value(location, "gid")).isEqualTo(14);
		assertThat(location.eContainer()).as("owned by the place").isSameAs(loadedPlace);
	}

	/**
	 * The residency half of the contract, which the data round-trip above does not cover:
	 * the child must come back resident in <em>its own</em> resource, exactly as the Mongo
	 * backend delivers it ({@code MongoCrossResourceReferenceTest}). The JPA backend
	 * collapses it into the parent's resource instead — it never proxies a containment
	 * child at all ({@code EMappingSupport} calls {@code dontUseIndirection()}), so there
	 * is nothing to resolve through the child's own resource.
	 */
	@Test
	@Disabled("issue #130 — the child is re-attached to the parent's resource, losing its own residency")
	void crossDocumentContainmentKeepsTheChildResidentInItsOwnResource() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject place = create(placeClass, "plid", 5, "name", "Bree");
		EObject point = create(geoPointClass, "gid", 15, "type", "Point");
		place.eSet(placeLocation, point);

		Resource pointResource = writeSet.createResource(uriFor("GeoPoint"));
		pointResource.getContents().add(point);
		Resource placeResource = writeSet.createResource(uriFor("Place"));
		placeResource.getContents().add(place);
		placeResource.save(null);
		pointResource.save(null);

		emf.getCache().evictAll();

		ResourceSet readSet = resourceSet();
		EObject loadedPlace = findById(readSet.getResource(uriFor("Place"), true), "5");
		EObject location = (EObject) loadedPlace.eGet(placeLocation);
		assertThat(location.eIsProxy()).isFalse();
		assertThat(location.eContainer()).as("owned by the place").isSameAs(loadedPlace);
		assertThat(location.eResource()).as("resident in its own resource").isNotNull();
		assertThat(location.eResource().getURI())
				.as("residency must match what Mongo delivers")
				.isEqualTo(uriFor("GeoPoint"));
	}

	/**
	 * Saving the parent alone (the child never attached to a resource of its own) is the
	 * baseline: plain, single-document containment must keep working unchanged.
	 */
	@Test
	void plainContainmentRemainsUnaffected() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject person = create(personClass, "pid", 3, "name", "Sam");
		EObject address = create(addressClass, "aid", 23, "street", "Gamgee Lane");
		@SuppressWarnings("unchecked")
		List<EObject> addresses = (List<EObject>) person.eGet(personAddresses);
		addresses.add(address);

		Resource personResource = writeSet.createResource(uriFor("Person"));
		personResource.getContents().add(person);
		personResource.save(null);

		EStructuralFeature street = addressClass.getEStructuralFeature("street");
		emf.getCache().evictAll();

		ResourceSet readSet = resourceSet();
		EObject loadedPerson = findById(readSet.getResource(uriFor("Person"), true), "3");
		@SuppressWarnings("unchecked")
		List<EObject> loaded = (List<EObject>) loadedPerson.eGet(personAddresses);
		assertThat(loaded).hasSize(1);
		assertThat(loaded.get(0).eGet(street)).isEqualTo("Gamgee Lane");
	}

	// ------------------------------------------------------- orphan removal (issue #136)

	/**
	 * Baseline for the orphan-removal measurement: a plain containment child, never
	 * attached to a resource of its own. Dropping it from the parent and saving must delete
	 * the row — {@code OneToOneProcessor} sets {@code orphanRemoval=true} for containment.
	 * <p>
	 * Without this case a surviving row in the cross-document variants below could equally
	 * well mean that orphan removal does not work at all in this setup.
	 */
	@Test
	void plainContainmentOrphanIsRemoved() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject place = create(placeClass, "plid", 6, "name", "Hobbiton");
		EObject point = create(geoPointClass, "gid", 16, "type", "Point");
		place.eSet(placeLocation, point);

		Resource placeResource = writeSet.createResource(uriFor("Place"));
		placeResource.getContents().add(place);
		placeResource.save(null);
		assertThat(countRows("GeoPoint")).as("child written").isEqualTo(1);

		place.eSet(placeLocation, null);
		placeResource.save(null);

		assertThat(countRows("GeoPoint"))
				.as("orphanRemoval must delete the dropped containment child")
				.isZero();
	}

	/**
	 * The measurement of issue #136: the same drop, but the child is also a root of its own
	 * resource. Does {@code orphanRemoval} still delete the row?
	 * <p>
	 * This is deliberately built parent-first, the order that works today — the
	 * child-resource-first order is blocked by #130 and would fail before reaching the
	 * question. The child stays in its own resource's contents, which is the ambiguous and
	 * therefore interesting case: EMF-wise the object is still a legitimate root of that
	 * resource, so whether ownership or residency wins is exactly what is being measured.
	 */
	@Test
	void crossDocumentContainmentOrphanIsRemovedWhileStillAResourceRoot() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject place = create(placeClass, "plid", 7, "name", "Bywater");
		EObject point = create(geoPointClass, "gid", 17, "type", "Point");
		place.eSet(placeLocation, point);

		Resource pointResource = writeSet.createResource(uriFor("GeoPoint"));
		pointResource.getContents().add(point);
		Resource placeResource = writeSet.createResource(uriFor("Place"));
		placeResource.getContents().add(place);
		placeResource.save(null);
		pointResource.save(null);
		assertThat(countRows("GeoPoint")).as("child written").isEqualTo(1);

		place.eSet(placeLocation, null);
		placeResource.save(null);

		assertThat(countRows("GeoPoint"))
				.as("containment is ownership, so the dropped child must go")
				.isZero();
	}

	/**
	 * The unambiguous variant: the child is dropped from the parent <em>and</em> from its
	 * own resource's contents, so nothing claims it any more. If even this leaves the row,
	 * the JPA backend has no orphan removal for the cross-document shape at all.
	 */
	@Test
	void crossDocumentContainmentOrphanIsRemovedWhenDroppedFromBoth() throws Exception {
		ResourceSet writeSet = resourceSet();
		EObject place = create(placeClass, "plid", 8, "name", "Frogmorton");
		EObject point = create(geoPointClass, "gid", 18, "type", "Point");
		place.eSet(placeLocation, point);

		Resource pointResource = writeSet.createResource(uriFor("GeoPoint"));
		pointResource.getContents().add(point);
		Resource placeResource = writeSet.createResource(uriFor("Place"));
		placeResource.getContents().add(place);
		placeResource.save(null);
		pointResource.save(null);
		assertThat(countRows("GeoPoint")).as("child written").isEqualTo(1);

		place.eSet(placeLocation, null);
		pointResource.getContents().remove(point);
		placeResource.save(null);
		pointResource.save(null);

		assertThat(countRows("GeoPoint"))
				.as("nothing claims the child any more, so the row must go")
				.isZero();
	}
}
