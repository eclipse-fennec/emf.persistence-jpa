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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
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
import org.eclipse.fennec.model.query.builder.Expressions;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.capabilities.CommandFeature;
import org.eclipse.fennec.persistence.capabilities.PersistenceCapabilities;
import org.eclipse.fennec.persistence.capabilities.QueryFeature;
import org.eclipse.fennec.persistence.capabilities.StoreFeature;
import org.eclipse.fennec.persistence.query.api.CommandResource;
import org.eclipse.fennec.persistence.repository.spi.AbstractRepository;
import org.eclipse.fennec.persistence.resource.PersistenceResource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Write-path conformance suite (issue #329): create, update and delete across every reference
 * shape — containment single and many, non-containment single and many, bidirectional 1:n and
 * n:m, self reference, inheritance — and off the happy path: repetition, graph surgery, stale
 * views, refused and failing writes, volume, empty values.
 * <p>
 * Every case verifies twice, and never only through the backend under test: through the
 * binding's {@link StoreProbe}, which reads the store natively (JDBC, raw driver documents),
 * and through a reload from a fresh resource set with the backend caches evicted. Each case
 * starts on an empty store; the binding bootstraps a fresh one per case.
 *
 * @author Mark Hoffmann
 * @since 24.09.2026
 */
@ExtendWith(CapabilityGate.class)
public abstract class AbstractWritePathTCK {

	protected EPackage wp;
	protected EClass owner, profile, item, tag, customer, order, student, course, node, asset, lawn, pool;
	protected EReference ownerProfile, ownerItems, ownerFavorite, ownerTags;
	protected EReference customerOrders, orderCustomer, studentCourses, courseStudents;
	protected EReference nodeParent, nodeChildren, assetKeeper;

	private StoreProbe probe;

	// ------------------------------------------------------------ backend SPI

	/** Bootstraps a fresh, empty store for the write-path package. */
	protected abstract void setUpBackend(EPackage writePathPackage) throws Exception;

	/** Releases the store. */
	protected abstract void tearDownBackend() throws Exception;

	/** A resource set wired with the backend's resource factory. */
	protected abstract ResourceSet createBackendResourceSet();

	/** The backend resource URI of a type, e.g. {@code jpa://wp/WpOwner}. */
	protected abstract URI uriFor(String typeName);

	/** Reads the store past the backend, see {@link StoreProbe}. */
	protected abstract StoreProbe storeProbe();

	/** The backend's capability declaration, as for {@link AbstractPersistenceTCK}. */
	protected abstract PersistenceCapabilities declaredCapabilities();

	/** Drops backend caches so a read goes to the store; no-op for a backend without one. */
	protected void evictBackendCaches() {
		// no-op by default
	}

	// ----------------------------------------------------------------- set up

	@BeforeEach
	void setUpWritePath() throws Exception {
		wp = loadModel();
		owner = type("WpOwner");
		profile = type("WpProfile");
		item = type("WpItem");
		tag = type("WpTag");
		customer = type("WpCustomer");
		order = type("WpOrder");
		student = type("WpStudent");
		course = type("WpCourse");
		node = type("WpNode");
		asset = type("WpAsset");
		lawn = type("WpLawn");
		pool = type("WpPool");
		ownerProfile = ref(owner, "profile");
		ownerItems = ref(owner, "items");
		ownerFavorite = ref(owner, "favorite");
		ownerTags = ref(owner, "tags");
		customerOrders = ref(customer, "orders");
		orderCustomer = ref(order, "customer");
		studentCourses = ref(student, "courses");
		courseStudents = ref(course, "students");
		nodeParent = ref(node, "parent");
		nodeChildren = ref(node, "children");
		assetKeeper = ref(asset, "keeper");
		setUpBackend(wp);
		probe = storeProbe();
	}

	@AfterEach
	void tearDownWritePath() throws Exception {
		tearDownBackend();
	}

	/** Loads {@code writepath.ecore}, re-homed on its nsURI like the TCK model. */
	protected EPackage loadModel() throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		Resource resource = resourceSet.createResource(URI.createURI("writepath.ecore"));
		try (InputStream stream = AbstractWritePathTCK.class.getResourceAsStream("writepath.ecore")) {
			assertThat(stream).as("writepath.ecore next to %s", AbstractWritePathTCK.class.getSimpleName()).isNotNull();
			resource.load(stream, null);
		}
		EPackage ePackage = (EPackage) resource.getContents().get(0);
		resource.setURI(URI.createURI(ePackage.getNsURI()));
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		return ePackage;
	}

	// ================================================================= INSERT

	@Test
	public void insertContainmentSingleAndMany() throws Exception {
		EObject o = owner("o1");
		o.eSet(ownerProfile, obj(profile, "p1"));
		items(o).addAll(List.of(obj(item, "i1"), obj(item, "i2"), obj(item, "i3")));
		save("WpOwner", o);

		assertIds(owner, "o1");
		assertRef(owner, "o1", ownerProfile, "p1");
		assertRefsInOrder(owner, "o1", ownerItems, "i1", "i2", "i3");
		assertNoOrphans(profile, "p1");
		assertNoOrphans(item, "i1", "i2", "i3");
	}

	@Test
	public void insertNewObjectReferencingAnExistingTarget() throws Exception {
		save("WpCustomer", obj(customer, "c1"));

		ResourceSet resourceSet = createBackendResourceSet();
		EObject c1 = resolve(resourceSet, "WpCustomer", "c1");
		EObject o1 = obj(order, "o1");
		o1.eSet(orderCustomer, c1);
		resourceSet.createResource(uriFor("WpOrder")).getContents().add(o1);
		// both ends changed, so both resources are saved — EMF's contract, as XMI
		saveResourcesOf(o1, c1);

		assertRef(order, "o1", orderCustomer, "c1");
		assertRefs(customer, "c1", customerOrders, "o1");
	}

	@Test
	public void insertBothSidesOfAOneToManyTogether() throws Exception {
		ResourceSet resourceSet = createBackendResourceSet();
		EObject c1 = obj(customer, "c1");
		EObject o1 = obj(order, "o1");
		EObject o2 = obj(order, "o2");
		list(c1, customerOrders).addAll(List.of(o1, o2));
		// cross-resource references need resource membership before saving (EMF semantics)
		saveAll(resourceSet, "WpCustomer", List.of(c1), "WpOrder", List.of(o1, o2));

		assertRef(order, "o1", orderCustomer, "c1");
		assertRef(order, "o2", orderCustomer, "c1");
		assertRefs(customer, "c1", customerOrders, "o1", "o2");
	}

	@Test
	public void insertManyToMany() throws Exception {
		ResourceSet resourceSet = createBackendResourceSet();
		EObject k1 = obj(course, "k1");
		EObject k2 = obj(course, "k2");
		EObject s1 = obj(student, "s1");
		EObject s2 = obj(student, "s2");
		list(s1, studentCourses).addAll(List.of(k1, k2));
		list(s2, studentCourses).add(k1);
		saveAll(resourceSet, "WpCourse", List.of(k1, k2), "WpStudent", List.of(s1, s2));

		assertRefs(student, "s1", studentCourses, "k1", "k2");
		assertRefs(student, "s2", studentCourses, "k1");
		assertRefs(course, "k1", courseStudents, "s1", "s2");
		assertRefs(course, "k2", courseStudents, "s1");
	}

	@Test
	public void insertUnidirectionalReferencesToExistingTargets() throws Exception {
		save("WpTag", obj(tag, "t1"), obj(tag, "t2"), obj(tag, "t3"));

		ResourceSet resourceSet = createBackendResourceSet();
		EObject o = owner("o1");
		o.eSet(ownerFavorite, resolve(resourceSet, "WpTag", "t2"));
		list(o, ownerTags).addAll(List.of(resolve(resourceSet, "WpTag", "t3"), resolve(resourceSet, "WpTag", "t1")));
		saveIn(resourceSet, "WpOwner", o);

		assertRef(owner, "o1", ownerFavorite, "t2");
		assertRefs(owner, "o1", ownerTags, "t1", "t3");
		assertIds(tag, "t1", "t2", "t3");
	}

	@Test
	public void insertSelfReferencingTree() throws Exception {
		ResourceSet resourceSet = createBackendResourceSet();
		EObject n1 = obj(node, "n1");
		EObject n2 = obj(node, "n2");
		EObject n3 = obj(node, "n3");
		EObject n4 = obj(node, "n4");
		n2.eSet(nodeParent, n1);
		n3.eSet(nodeParent, n1);
		n4.eSet(nodeParent, n2);
		saveIn(resourceSet, "WpNode", n1, n2, n3, n4);

		assertIds(node, "n1", "n2", "n3", "n4");
		assertRef(node, "n1", nodeParent, null);
		assertRef(node, "n2", nodeParent, "n1");
		assertRef(node, "n4", nodeParent, "n2");
		assertRefs(node, "n1", nodeChildren, "n2", "n3");
		assertRefs(node, "n2", nodeChildren, "n4");
	}

	@Test
	public void insertSubtypesOfAHierarchy() throws Exception {
		save("WpCustomer", obj(customer, "c1"));
		ResourceSet resourceSet = createBackendResourceSet();
		EObject l1 = obj(lawn, "l1");
		l1.eSet(attr(lawn, "area"), 120);
		l1.eSet(assetKeeper, resolve(resourceSet, "WpCustomer", "c1"));
		EObject p1 = obj(pool, "p1");
		p1.eSet(attr(pool, "depth"), 3);
		saveIn(resourceSet, "WpLawn", l1);
		saveIn(resourceSet, "WpPool", p1);

		assertIds(asset, "l1", "p1");
		assertIds(lawn, "l1");
		assertIds(pool, "p1");
		assertRef(lawn, "l1", assetKeeper, "c1");
		assertRef(pool, "p1", assetKeeper, null);
		EObject reloadedLawn = fresh("WpLawn", "l1");
		assertThat(reloadedLawn.eClass()).isEqualTo(lawn);
		assertThat(reloadedLawn.eGet(attr(lawn, "area"))).isEqualTo(120);
		assertThat(fresh("WpPool", "p1").eGet(attr(pool, "depth"))).isEqualTo(3);
	}

	@Test
	public void insertEmptyAndUnsetValues() throws Exception {
		save("WpOwner", owner("o1"));

		assertIds(owner, "o1");
		assertRef(owner, "o1", ownerProfile, null);
		assertRef(owner, "o1", ownerFavorite, null);
		assertRefs(owner, "o1", ownerItems);
		assertRefs(owner, "o1", ownerTags);
		EObject reloaded = fresh("WpOwner", "o1");
		assertThat(reloaded.eGet(ownerProfile)).isNull();
		assertThat(list(reloaded, ownerItems)).isEmpty();
	}

	@Test
	public void insertLargeCollections() throws Exception {
		EObject o = owner("o1");
		List<String> itemIds = ids("i", 300);
		itemIds.forEach(id -> items(o).add(obj(item, id)));
		save("WpOwner", o);

		ResourceSet resourceSet = createBackendResourceSet();
		EObject c1 = obj(customer, "c1");
		List<String> orderIds = ids("o", 200);
		List<EObject> orders = orderIds.stream().map(id -> obj(order, id)).toList();
		list(c1, customerOrders).addAll(orders);
		saveAll(resourceSet, "WpCustomer", List.of(c1), "WpOrder", orders);

		assertRefsInOrder(owner, "o1", ownerItems, itemIds.toArray(String[]::new));
		assertRefs(customer, "c1", customerOrders, orderIds.toArray(String[]::new));
		assertIds(order, orderIds.toArray(String[]::new));
	}

	@Test
	public void aReferenceToAnObjectInNoResourceIsNeverStoredCorrupt() throws Exception {
		EObject c1 = obj(customer, "c1");
		list(c1, customerOrders).add(obj(order, "o1"));
		Resource resource = createBackendResourceSet().createResource(uriFor("WpCustomer"));
		resource.getContents().add(c1);
		try {
			resource.save(null);
		} catch (IOException refused) {
			// refusing is fine: the order has no resource to be referenced in
		}

		// whatever the store holds for the reference must name stored objects
		Set<String> orders = probe.ids(order);
		for (String customerId : probe.ids(customer)) {
			assertThat(probe.references(customer, customerId, customerOrders))
					.as("stored references of %s#%s.orders name stored orders", "WpCustomer", customerId)
					.allMatch(orders::contains);
		}
	}

	// ================================================================= UPDATE

	@Test
	public void setAReferenceOnAnExistingObject() throws Exception {
		save("WpCustomer", obj(customer, "c1"));
		save("WpOrder", obj(order, "o1"));

		ResourceSet resourceSet = createBackendResourceSet();
		EObject o1 = resolve(resourceSet, "WpOrder", "o1");
		EObject c1 = resolve(resourceSet, "WpCustomer", "c1");
		o1.eSet(orderCustomer, c1);
		saveResourcesOf(o1, c1);

		assertRef(order, "o1", orderCustomer, "c1");
		assertRefs(customer, "c1", customerOrders, "o1");
	}

	@Test
	public void unsettingAReferenceClearsItAndKeepsTheTarget() throws Exception {
		saveCustomerWithOrders("c1", "o1");

		ResourceSet resourceSet = createBackendResourceSet();
		EObject o1 = resolve(resourceSet, "WpOrder", "o1");
		EObject c1 = (EObject) o1.eGet(orderCustomer);
		o1.eUnset(orderCustomer);
		saveResourcesOf(o1, c1);

		assertRef(order, "o1", orderCustomer, null);
		assertRefs(customer, "c1", customerOrders);
		assertIds(customer, "c1");
	}

	@Test
	public void replacingAReferenceMovesTheObjectBetweenOpposites() throws Exception {
		saveCustomerWithOrders("c1", "o1");
		save("WpCustomer", obj(customer, "c2"));

		ResourceSet resourceSet = createBackendResourceSet();
		EObject o1 = resolve(resourceSet, "WpOrder", "o1");
		// the old end is resolved first: EMF maintains the inverse on what the reference holds,
		// and on a proxy that would miss the loaded customer
		EObject c1 = (EObject) o1.eGet(orderCustomer);
		EObject c2 = resolve(resourceSet, "WpCustomer", "c2");
		o1.eSet(orderCustomer, c2);
		saveResourcesOf(o1, c1, c2);

		assertRef(order, "o1", orderCustomer, "c2");
		assertRefs(customer, "c1", customerOrders);
		assertRefs(customer, "c2", customerOrders, "o1");
	}

	@Test
	public void settingTheReferenceThroughTheManySideWritesIt() throws Exception {
		save("WpCustomer", obj(customer, "c1"));
		save("WpOrder", obj(order, "o1"));

		ResourceSet resourceSet = createBackendResourceSet();
		EObject c1 = resolve(resourceSet, "WpCustomer", "c1");
		EObject o1 = resolve(resourceSet, "WpOrder", "o1");
		list(c1, customerOrders).add(o1);
		c1.eResource().save(null);
		o1.eResource().save(null);

		assertRef(order, "o1", orderCustomer, "c1");
		assertRefs(customer, "c1", customerOrders, "o1");
	}

	@Test
	public void addingAndRemovingUnidirectionalReferencesKeepsTheTargets() throws Exception {
		save("WpTag", obj(tag, "t1"), obj(tag, "t2"), obj(tag, "t3"));
		ResourceSet setup = createBackendResourceSet();
		EObject o = owner("o1");
		list(o, ownerTags).addAll(List.of(resolve(setup, "WpTag", "t1"), resolve(setup, "WpTag", "t2")));
		saveIn(setup, "WpOwner", o);

		ResourceSet resourceSet = createBackendResourceSet();
		EObject stored = resolve(resourceSet, "WpOwner", "o1");
		list(stored, ownerTags).removeIf(t -> "t1".equals(id(t)));
		list(stored, ownerTags).add(resolve(resourceSet, "WpTag", "t3"));
		stored.eResource().save(null);

		assertRefs(owner, "o1", ownerTags, "t2", "t3");
		assertIds(tag, "t1", "t2", "t3");
	}

	@Test
	public void removingAManyToManyLinkKeepsBothSides() throws Exception {
		saveStudentsAndCourses();

		ResourceSet resourceSet = createBackendResourceSet();
		EObject s1 = resolve(resourceSet, "WpStudent", "s1");
		EObject k1 = list(s1, studentCourses).stream().filter(k -> "k1".equals(id(k))).findFirst().orElseThrow();
		assertThat(list(s1, studentCourses).remove(k1)).isTrue();
		saveResourcesOf(s1, k1);

		assertRefs(student, "s1", studentCourses, "k2");
		assertRefs(course, "k1", courseStudents, "s2");
		assertIds(course, "k1", "k2");
		assertIds(student, "s1", "s2");
	}

	@Test
	public void addingAManyToManyLinkFromTheOtherSideWritesIt() throws Exception {
		saveStudentsAndCourses();

		ResourceSet resourceSet = createBackendResourceSet();
		EObject s2 = resolve(resourceSet, "WpStudent", "s2");
		EObject k2 = resolve(resourceSet, "WpCourse", "k2");
		list(s2, studentCourses).add(k2);
		saveResourcesOf(s2, k2);

		assertRefs(student, "s2", studentCourses, "k1", "k2");
		assertRefs(course, "k2", courseStudents, "s1", "s2");
		assertRefs(course, "k1", courseStudents, "s1", "s2");
	}

	@Test
	public void anObjectReachedByReferenceAndByUriIsOneInstance() throws Exception {
		saveCustomerWithOrders("c1", "o1");

		ResourceSet resourceSet = createBackendResourceSet();
		EObject c1 = resolve(resourceSet, "WpCustomer", "c1");
		EObject viaReference = list(c1, customerOrders).get(0);
		EObject viaUri = resolve(resourceSet, "WpOrder", "o1");

		// two instances of one stored object in one resource set make every edit on one of them
		// invisible to the other — and to whichever resource saves the other
		assertThat(viaUri).isSameAs(viaReference);
		assertThat(viaReference.eGet(orderCustomer)).isSameAs(c1);
	}

	@Test
	public void anObjectReachedByUriAndThenByReferenceIsOneInstance() throws Exception {
		saveCustomerWithOrders("c1", "o1");

		ResourceSet resourceSet = createBackendResourceSet();
		EObject c1 = resolve(resourceSet, "WpCustomer", "c1");
		EObject viaUri = resolve(resourceSet, "WpOrder", "o1");
		EObject viaReference = list(c1, customerOrders).get(0);

		assertThat(viaReference).isSameAs(viaUri);
		assertThat(viaUri.eGet(orderCustomer)).isSameAs(c1);
	}

	@Test
	public void removingAnOrderThroughTheManySideUnlinksIt() throws Exception {
		saveCustomerWithOrders("c1", "o1", "o2");

		ResourceSet resourceSet = createBackendResourceSet();
		EObject c1 = resolve(resourceSet, "WpCustomer", "c1");
		EObject o1 = list(c1, customerOrders).stream().filter(o -> "o1".equals(id(o))).findFirst().orElseThrow();
		assertThat(list(c1, customerOrders).remove(o1)).isTrue();
		// the order keeps existing, without a customer
		saveResourcesOf(c1, o1);

		assertRefs(customer, "c1", customerOrders, "o2");
		assertRef(order, "o1", orderCustomer, null);
		assertRef(order, "o2", orderCustomer, "c1");
		assertIds(order, "o1", "o2");
	}

	@Test
	public void replacingAContainedSingleChildRemovesTheOldOne() throws Exception {
		EObject o = owner("o1");
		o.eSet(ownerProfile, obj(profile, "p1"));
		save("WpOwner", o);

		ResourceSet resourceSet = createBackendResourceSet();
		EObject stored = resolve(resourceSet, "WpOwner", "o1");
		stored.eSet(ownerProfile, obj(profile, "p2"));
		stored.eResource().save(null);

		assertRef(owner, "o1", ownerProfile, "p2");
		assertNoOrphans(profile, "p2");
	}

	@Test
	public void removingAContainedChildRemovesItFromTheStore() throws Exception {
		saveOwnerWithItems("o1", "i1", "i2", "i3");

		ResourceSet resourceSet = createBackendResourceSet();
		EObject stored = resolve(resourceSet, "WpOwner", "o1");
		items(stored).removeIf(i -> "i2".equals(id(i)));
		stored.eResource().save(null);

		assertRefsInOrder(owner, "o1", ownerItems, "i1", "i3");
		assertNoOrphans(item, "i1", "i3");
	}

	@Test
	public void reorderingContainedChildrenIsStored() throws Exception {
		saveOwnerWithItems("o1", "i1", "i2", "i3");

		ResourceSet resourceSet = createBackendResourceSet();
		EObject stored = resolve(resourceSet, "WpOwner", "o1");
		items(stored).move(0, 2);
		stored.eResource().save(null);

		assertRefsInOrder(owner, "o1", ownerItems, "i3", "i1", "i2");
	}

	@Test
	public void movingAContainedChildToAnotherOwner() throws Exception {
		saveOwnerWithItems("o1", "i1", "i2", "i3");
		save("WpOwner", owner("o2"));

		ResourceSet resourceSet = createBackendResourceSet();
		EObject o1 = resolve(resourceSet, "WpOwner", "o1");
		EObject o2 = resolve(resourceSet, "WpOwner", "o2");
		EObject i2 = items(o1).stream().filter(i -> "i2".equals(id(i))).findFirst().orElseThrow();
		items(o2).add(i2);
		o1.eResource().save(null);

		assertRefsInOrder(owner, "o1", ownerItems, "i1", "i3");
		assertRefsInOrder(owner, "o2", ownerItems, "i2");
		assertNoOrphans(item, "i1", "i2", "i3");
	}

	@Test
	public void clearingAndRefillingContainedChildrenInOneSave() throws Exception {
		saveOwnerWithItems("o1", "i1", "i2");

		ResourceSet resourceSet = createBackendResourceSet();
		EObject stored = resolve(resourceSet, "WpOwner", "o1");
		items(stored).clear();
		items(stored).addAll(List.of(obj(item, "i4"), obj(item, "i5")));
		stored.eResource().save(null);

		assertRefsInOrder(owner, "o1", ownerItems, "i4", "i5");
		assertNoOrphans(item, "i4", "i5");
	}

	@Test
	public void reAddingARemovedChildInALaterSave() throws Exception {
		saveOwnerWithItems("o1", "i1", "i2");
		ResourceSet first = createBackendResourceSet();
		EObject stored = resolve(first, "WpOwner", "o1");
		items(stored).removeIf(i -> "i2".equals(id(i)));
		stored.eResource().save(null);

		ResourceSet second = createBackendResourceSet();
		EObject again = resolve(second, "WpOwner", "o1");
		items(again).add(obj(item, "i2"));
		again.eResource().save(null);

		assertRefsInOrder(owner, "o1", ownerItems, "i1", "i2");
		assertNoOrphans(item, "i1", "i2");
	}

	@Test
	public void changingAnAttributeKeepsTheReferences() throws Exception {
		saveCustomerWithOrders("c1", "o1", "o2");

		ResourceSet resourceSet = createBackendResourceSet();
		EObject c1 = resolve(resourceSet, "WpCustomer", "c1");
		c1.eSet(attr(customer, "name"), "renamed");
		c1.eResource().save(null);

		assertThat(fresh("WpCustomer", "c1").eGet(attr(customer, "name"))).isEqualTo("renamed");
		assertRefs(customer, "c1", customerOrders, "o1", "o2");
		assertRef(order, "o1", orderCustomer, "c1");
	}

	@Test
	public void savingAnUnchangedGraphAgainChangesNothing() throws Exception {
		EObject o = owner("o1");
		o.eSet(ownerProfile, obj(profile, "p1"));
		items(o).addAll(List.of(obj(item, "i1"), obj(item, "i2")));
		save("WpOwner", o);

		for (int round = 0; round < 3; round++) {
			ResourceSet resourceSet = createBackendResourceSet();
			resolve(resourceSet, "WpOwner", "o1").eResource().save(null);
		}

		assertIds(owner, "o1");
		assertRef(owner, "o1", ownerProfile, "p1");
		assertRefsInOrder(owner, "o1", ownerItems, "i1", "i2");
		assertNoOrphans(item, "i1", "i2");
		assertNoOrphans(profile, "p1");
	}

	@Test
	public void reparentingANode() throws Exception {
		saveTree();

		ResourceSet resourceSet = createBackendResourceSet();
		EObject n4 = resolve(resourceSet, "WpNode", "n4");
		EObject n2 = (EObject) n4.eGet(nodeParent);
		EObject n3 = resolve(resourceSet, "WpNode", "n3");
		n4.eSet(nodeParent, n3);
		saveResourcesOf(n4, n2, n3);

		assertRef(node, "n4", nodeParent, "n3");
		assertRefs(node, "n2", nodeChildren);
		assertRefs(node, "n3", nodeChildren, "n4");
	}

	@Test
	public void aNodeCanBeItsOwnParent() throws Exception {
		EObject n1 = obj(node, "n1");
		n1.eSet(nodeParent, n1);
		save("WpNode", n1);

		assertRef(node, "n1", nodeParent, "n1");
		assertRefs(node, "n1", nodeChildren, "n1");
	}

	@Test
	public void updatingASubtypeKeepsItsTypeAndReferences() throws Exception {
		save("WpCustomer", obj(customer, "c1"));
		ResourceSet setup = createBackendResourceSet();
		EObject l1 = obj(lawn, "l1");
		l1.eSet(attr(lawn, "area"), 10);
		l1.eSet(assetKeeper, resolve(setup, "WpCustomer", "c1"));
		saveIn(setup, "WpLawn", l1);

		ResourceSet resourceSet = createBackendResourceSet();
		EObject stored = resolve(resourceSet, "WpLawn", "l1");
		stored.eSet(attr(lawn, "area"), 99);
		stored.eResource().save(null);

		EObject reloaded = fresh("WpLawn", "l1");
		assertThat(reloaded.eClass()).isEqualTo(lawn);
		assertThat(reloaded.eGet(attr(lawn, "area"))).isEqualTo(99);
		assertRef(lawn, "l1", assetKeeper, "c1");
		assertIds(pool);
	}

	@Test
	public void twoResourceSetsSavingTheSameObjectLeaveOneRow() throws Exception {
		save("WpCustomer", obj(customer, "c1"));

		ResourceSet a = createBackendResourceSet();
		ResourceSet b = createBackendResourceSet();
		EObject inA = resolve(a, "WpCustomer", "c1");
		EObject inB = resolve(b, "WpCustomer", "c1");
		inA.eSet(attr(customer, "name"), "from A");
		inB.eSet(attr(customer, "name"), "from B");
		inA.eResource().save(null);
		inB.eResource().save(null);

		assertIds(customer, "c1");
		assertThat(fresh("WpCustomer", "c1").eGet(attr(customer, "name"))).isEqualTo("from B");
	}

	// ================================================================= DELETE

	@Test
	public void deletingASingleObject() throws Exception {
		save("WpCustomer", obj(customer, "c1"), obj(customer, "c2"));

		delete(resolve(createBackendResourceSet(), "WpCustomer", "c1"));

		assertIds(customer, "c2");
	}

	@Test
	public void deletingAllObjectsOfAType() throws Exception {
		save("WpCustomer", obj(customer, "c1"), obj(customer, "c2"), obj(customer, "c3"));

		deleteAll("WpCustomer");

		assertIds(customer);
	}

	@Test
	public void deletingASubsetTakenThroughTheContents() throws Exception {
		save("WpCustomer", obj(customer, "c1"), obj(customer, "c2"), obj(customer, "c3"));

		Resource resource = load("WpCustomer");
		resource.getContents().removeIf(c -> "c2".equals(id(c)));
		((PersistenceResource) resource).delete(null);

		assertIds(customer, "c2");
	}

	@Test
	public void deletingAnOwnerDeletesItsContainedChildrenButNotItsReferences() throws Exception {
		save("WpTag", obj(tag, "t1"));
		ResourceSet setup = createBackendResourceSet();
		EObject o = owner("o1");
		o.eSet(ownerProfile, obj(profile, "p1"));
		items(o).addAll(List.of(obj(item, "i1"), obj(item, "i2")));
		o.eSet(ownerFavorite, resolve(setup, "WpTag", "t1"));
		saveIn(setup, "WpOwner", o);

		delete(resolve(createBackendResourceSet(), "WpOwner", "o1"));

		assertIds(owner);
		assertNoOrphans(item);
		assertNoOrphans(profile);
		assertIds(tag, "t1");
	}

	@Test
	public void deletingTheOrdersThenTheirCustomer() throws Exception {
		saveCustomerWithOrders("c1", "o1", "o2");

		deleteAll("WpOrder");
		assertIds(order);
		assertRefs(customer, "c1", customerOrders);

		deleteAll("WpCustomer");
		assertIds(customer);
		assertIds(order);
	}

	@Test
	public void deletingOneOrderOfACustomer() throws Exception {
		saveCustomerWithOrders("c1", "o1", "o2");

		delete(resolve(createBackendResourceSet(), "WpOrder", "o1"));

		assertIds(order, "o2");
		assertRefs(customer, "c1", customerOrders, "o2");
	}

	@Test
	public void deletingAReferencedCustomerIsRefusedAndChangesNothing() throws Exception {
		saveCustomerWithOrders("c1", "o1", "o2");

		EObject c1 = resolve(createBackendResourceSet(), "WpCustomer", "c1");
		Resource holder = c1.eResource();
		assertThatThrownBy(() -> holder.delete(null)).isInstanceOf(IOException.class);
		assertThat(holder.getErrors()).as("the refusal says why").isNotEmpty();

		assertIds(customer, "c1");
		assertIds(order, "o1", "o2");
		assertRef(order, "o1", orderCustomer, "c1");
		assertRefs(customer, "c1", customerOrders, "o1", "o2");
	}

	@Test
	public void deletingAReferencedTagIsRefusedAndChangesNothing() throws Exception {
		save("WpTag", obj(tag, "t1"));
		ResourceSet setup = createBackendResourceSet();
		EObject o = owner("o1");
		list(o, ownerTags).add(resolve(setup, "WpTag", "t1"));
		saveIn(setup, "WpOwner", o);

		Resource holder = resolve(createBackendResourceSet(), "WpTag", "t1").eResource();
		assertThatThrownBy(() -> holder.delete(null)).isInstanceOf(IOException.class);

		assertIds(tag, "t1");
		assertRefs(owner, "o1", ownerTags, "t1");
	}

	@Test
	public void deletingAStudentRemovesItsLinksAndKeepsTheCourses() throws Exception {
		saveStudentsAndCourses();

		delete(resolve(createBackendResourceSet(), "WpStudent", "s1"));

		assertIds(student, "s2");
		assertIds(course, "k1", "k2");
		assertRefs(course, "k1", courseStudents, "s2");
		assertRefs(course, "k2", courseStudents);
	}

	@Test
	public void deletingACourseRemovesItsLinksAndKeepsTheStudents() throws Exception {
		saveStudentsAndCourses();

		delete(resolve(createBackendResourceSet(), "WpCourse", "k1"));

		assertIds(course, "k2");
		assertIds(student, "s1", "s2");
		assertRefs(student, "s1", studentCourses, "k2");
		assertRefs(student, "s2", studentCourses);
	}

	@Test
	public void deletingAllStudentsLeavesTheCoursesWithoutLinks() throws Exception {
		saveStudentsAndCourses();

		deleteAll("WpStudent");

		assertIds(student);
		assertIds(course, "k1", "k2");
		assertRefs(course, "k1", courseStudents);
		assertRefs(course, "k2", courseStudents);
	}

	@Test
	public void deletingAllOrdersOfACustomerEmptiesItsOrders() throws Exception {
		saveCustomerWithOrders("c1", "o1", "o2", "o3");

		deleteAll("WpOrder");

		assertIds(order);
		assertIds(customer, "c1");
		assertRefs(customer, "c1", customerOrders);
	}

	/**
	 * A child points at its parent through a single-valued end — a dependency — so the parent's
	 * delete is refused, and the refusal comes before anything is changed: in particular the
	 * link the refused node's own parent keeps to it survives, although that one alone would be
	 * removed by a delete that went through.
	 */
	@Test
	public void deletingANodeWithChildrenIsRefusedAndChangesNothing() throws Exception {
		saveTree();

		Resource holder = resolve(createBackendResourceSet(), "WpNode", "n2").eResource();
		assertThatThrownBy(() -> ((PersistenceResource) holder).delete(null)).isInstanceOf(IOException.class);

		assertIds(node, "n1", "n2", "n3", "n4");
		assertRefs(node, "n1", nodeChildren, "n2", "n3");
		assertRefs(node, "n2", nodeChildren, "n4");
		assertRef(node, "n4", nodeParent, "n2");
		assertRef(node, "n2", nodeParent, "n1");
	}

	@Test
	public void savingBothEndsInEitherOrderGivesTheSameStore() throws Exception {
		saveStudentsAndCourses();

		ResourceSet resourceSet = createBackendResourceSet();
		EObject s2 = resolve(resourceSet, "WpStudent", "s2");
		EObject k2 = resolve(resourceSet, "WpCourse", "k2");
		list(s2, studentCourses).add(k2);
		// the target end first this time
		saveResourcesOf(k2, s2);

		assertRefs(student, "s2", studentCourses, "k1", "k2");
		assertRefs(course, "k2", courseStudents, "s1", "s2");
	}

	@Test
	@RequiresCapabilities(command = CommandFeature.DELETE_BY_SELECTOR, query = QueryFeature.WHERE_EQ)
	public void deletingAStudentByCommandRemovesItsLinks() throws Exception {
		saveStudentsAndCourses();

		DeleteCommand delete = CommandFactory.eINSTANCE.createDeleteCommand();
		delete.setSelector(QueryBuilder.from(student).where(Expressions.path(attr(student, "id")).eq("s1")).build());
		long affected = ((CommandResource) createBackendResourceSet().createResource(uriFor("WpStudent")))
				.execute(delete);

		assertThat(affected).isEqualTo(1);
		assertIds(student, "s2");
		assertRefs(course, "k1", courseStudents, "s2");
		assertRefs(course, "k2", courseStudents);
	}

	@Test
	@RequiresCapabilities(command = CommandFeature.DELETE_BY_SELECTOR, query = QueryFeature.WHERE_EQ)
	public void deletingAReferencedCustomerByCommandIsRefusedAndChangesNothing() throws Exception {
		saveCustomerWithOrders("c1", "o1", "o2");

		DeleteCommand delete = CommandFactory.eINSTANCE.createDeleteCommand();
		delete.setSelector(QueryBuilder.from(customer).where(Expressions.path(attr(customer, "id")).eq("c1")).build());
		CommandResource commands = (CommandResource) createBackendResourceSet().createResource(uriFor("WpCustomer"));
		assertThatThrownBy(() -> commands.execute(delete)).isInstanceOf(IOException.class);

		assertIds(customer, "c1");
		assertRefs(customer, "c1", customerOrders, "o1", "o2");
		assertRef(order, "o1", orderCustomer, "c1");
	}

	/**
	 * Issues #349/#352: the referenced customer lives in a resource of its own that is not saved
	 * yet, so it has no id. The resource is the low-level API and strict, as XMI is with a
	 * dangling href: the save is refused before anything is written — never a reference under a
	 * positional fragment, never a silently dropped one. Once the customer's resource is saved,
	 * the same save goes through and the store names the generated id.
	 */
	@Test
	public void aReferenceToATargetWithoutAnIdIsRefusedAndWritesNothing() throws Exception {
		ResourceSet resourceSet = createBackendResourceSet();
		EObject c = EcoreUtil.create(customer);
		EObject o1 = obj(order, "o1");
		o1.eSet(orderCustomer, c);
		Resource customers = resourceSet.createResource(uriFor("WpCustomer"));
		customers.getContents().add(c);
		Resource orders = resourceSet.createResource(uriFor("WpOrder"));
		orders.getContents().add(o1);

		assertThatThrownBy(() -> orders.save(null)).isInstanceOf(IOException.class);
		assertThat(orders.getErrors()).as("the refusal says why").isNotEmpty();
		assertIds(order);
		assertIds(customer);

		customers.save(null);
		orders.save(null);
		String customerId = EcoreUtil.getID(c);
		assertThat(customerId).isNotNull();
		assertRef(order, "o1", orderCustomer, customerId);
		assertRefs(customer, customerId, customerOrders, "o1");
	}

	@Test
	public void aManyValuedReferenceToATargetWithoutAnIdIsRefusedAndWritesNothing() throws Exception {
		ResourceSet resourceSet = createBackendResourceSet();
		EObject k = EcoreUtil.create(course);
		EObject s1 = obj(student, "s1");
		list(s1, studentCourses).add(obj(course, "k1"));
		list(s1, studentCourses).add(k);
		Resource courses = resourceSet.createResource(uriFor("WpCourse"));
		courses.getContents().addAll(list(s1, studentCourses));
		Resource students = resourceSet.createResource(uriFor("WpStudent"));
		students.getContents().add(s1);

		assertThatThrownBy(() -> students.save(null)).isInstanceOf(IOException.class);
		assertIds(student);
	}

	@Test
	public void aRootReferencingALaterRootOfTheSameResourceWithoutAnIdStoresItsId() throws Exception {
		EObject child = obj(node, "n2");
		EObject parent = EcoreUtil.create(node);
		child.eSet(nodeParent, parent);
		// the child comes first, so it is encoded before the parent would get its id
		save("WpNode", child, parent);

		String parentId = EcoreUtil.getID(parent);
		assertThat(parentId).as("the resource assigns ids to its own objects").isNotNull();
		assertIds(node, "n2", parentId);
		assertRef(node, "n2", nodeParent, parentId);
		assertRefs(node, parentId, nodeChildren, "n2");
	}

	/**
	 * Issue #350: the repository is the convenient API — new objects of several types that
	 * reference each other are saved in one call, in any order, because every id is assigned
	 * before the first resource is saved.
	 */
	@Test
	public void repositorySavesNewObjectsReferencingEachOtherInEitherOrder() throws Exception {
		EObject c = EcoreUtil.create(customer);
		EObject o = EcoreUtil.create(order);
		o.eSet(orderCustomer, c);

		// the order first: saved type by type, it references the customer before that is saved
		repository().saveAll(List.of(o, c));

		String customerId = EcoreUtil.getID(c);
		String orderId = EcoreUtil.getID(o);
		assertThat(customerId).isNotNull();
		assertThat(orderId).isNotNull();
		assertIds(customer, customerId);
		assertIds(order, orderId);
		assertRef(order, orderId, orderCustomer, customerId);
		assertRefs(customer, customerId, customerOrders, orderId);
	}

	@Test
	public void repositorySavesANewManyToManyGraph() throws Exception {
		EObject k1 = EcoreUtil.create(course);
		EObject k2 = EcoreUtil.create(course);
		EObject s1 = EcoreUtil.create(student);
		EObject s2 = EcoreUtil.create(student);
		list(s1, studentCourses).addAll(List.of(k1, k2));
		list(s2, studentCourses).add(k1);

		repository().saveAll(List.of(s1, k1, s2, k2));

		assertRefs(student, EcoreUtil.getID(s1), studentCourses, EcoreUtil.getID(k1), EcoreUtil.getID(k2));
		assertRefs(student, EcoreUtil.getID(s2), studentCourses, EcoreUtil.getID(k1));
		assertRefs(course, EcoreUtil.getID(k1), courseStudents, EcoreUtil.getID(s1), EcoreUtil.getID(s2));
		assertRefs(course, EcoreUtil.getID(k2), courseStudents, EcoreUtil.getID(s1));
	}

	/** The id the repository assigned is the one its URI semantics fetch the object by. */
	@Test
	public void repositoryFetchesASavedObjectByTheUriOfItsAssignedId() throws Exception {
		EObject c = EcoreUtil.create(customer);
		c.eSet(attr(customer, "name"), "by uri");
		repository().saveAll(List.of(c));

		String id = EcoreUtil.getID(c);
		evictBackendCaches();
		EObject viaUri = repository().getEObject(uriFor("WpCustomer").appendFragment(id));
		assertThat(viaUri).isNotNull();
		assertThat(viaUri.eGet(attr(customer, "name"))).isEqualTo("by uri");
		assertThat(repository().getEObject(customer, id)).isNotNull();
	}

	@Test
	public void repositoryRefusesATargetInNoResourceOutsideTheSavedSetAndWritesNothing() throws Exception {
		EObject o1 = obj(order, "o1");
		o1.eSet(orderCustomer, obj(customer, "c1"));

		assertThatThrownBy(() -> repository().saveAll(List.of(o1))).isInstanceOf(IOException.class);

		assertIds(order);
		assertIds(customer);
	}

	@Test
	public void repositoryRefusesATargetWithoutAnIdOutsideTheSavedSetAndWritesNothing() throws Exception {
		EObject c = EcoreUtil.create(customer);
		createBackendResourceSet().createResource(uriFor("WpCustomer")).getContents().add(c);
		EObject o1 = obj(order, "o1");
		EObject k1 = obj(course, "k1");
		o1.eSet(orderCustomer, c);

		// a valid object of another type in the same call is not written either
		assertThatThrownBy(() -> repository().saveAll(List.of(k1, o1))).isInstanceOf(IOException.class);

		assertIds(order);
		assertIds(course);
		assertIds(customer);
	}

	@Test
	public void deletingALeafNodeKeepsItsParent() throws Exception {
		saveTree();

		delete(resolve(createBackendResourceSet(), "WpNode", "n4"));

		assertIds(node, "n1", "n2", "n3");
		assertRefs(node, "n2", nodeChildren);
	}

	@Test
	public void deletingAnAlreadyDeletedObjectDoesNotResurrectIt() throws Exception {
		save("WpCustomer", obj(customer, "c1"));
		EObject stale = resolve(createBackendResourceSet(), "WpCustomer", "c1");
		delete(resolve(createBackendResourceSet(), "WpCustomer", "c1"));

		try {
			((PersistenceResource) stale.eResource()).delete(null);
		} catch (IOException refused) {
			// refusing is fine — resurrecting is not
		}

		assertIds(customer);
	}

	@Test
	public void reInsertingAnIdAfterItsDelete() throws Exception {
		saveCustomerWithOrders("c1", "o1");
		deleteAll("WpOrder");

		ResourceSet resourceSet = createBackendResourceSet();
		EObject c1 = resolve(resourceSet, "WpCustomer", "c1");
		EObject again = obj(order, "o1");
		again.eSet(attr(order, "amount"), 7);
		again.eSet(orderCustomer, c1);
		saveIn(resourceSet, "WpOrder", again);

		assertIds(order, "o1");
		assertRef(order, "o1", orderCustomer, "c1");
		assertThat(fresh("WpOrder", "o1").eGet(attr(order, "amount"))).isEqualTo(7);
	}

	@Test
	public void deletingManyObjectsAtOnce() throws Exception {
		List<String> orderIds = ids("o", 200);
		saveCustomerWithOrders("c1", orderIds.toArray(String[]::new));

		deleteAll("WpOrder");

		assertIds(order);
		assertRefs(customer, "c1", customerOrders);
		deleteAll("WpCustomer");
		assertIds(customer);
	}

	@Test
	public void deletingOneSubtypeObjectKeepsTheOthers() throws Exception {
		save("WpLawn", obj(lawn, "l1"));
		save("WpPool", obj(pool, "p1"));

		delete(resolve(createBackendResourceSet(), "WpLawn", "l1"));

		assertIds(asset, "p1");
		assertIds(lawn);
		assertIds(pool, "p1");
	}

	// ============================================================== ATOMICITY

	@Test
	@RequiresCapabilities(store = StoreFeature.TRANSACTION_BRACKET)
	public void aSaveThatFailsPartWayWritesNothing() throws Exception {
		save("WpCustomer", obj(customer, "c1"));

		ResourceSet resourceSet = createBackendResourceSet();
		EObject o1 = obj(order, "o1");
		EObject clash = obj(order, "o2");
		o1.eSet(orderCustomer, resolve(resourceSet, "WpCustomer", "c1"));
		Resource resource = resourceSet.createResource(uriFor("WpOrder"));
		resource.getContents().addAll(List.of(o1, clash, obj(order, "o2")));
		assertThatThrownBy(() -> resource.save(null)).isInstanceOf(IOException.class);

		assertIds(order);
		assertRefs(customer, "c1", customerOrders);
	}

	// ================================================================ helpers

	private EClass type(String name) {
		return (EClass) wp.getEClassifier(name);
	}

	private static EReference ref(EClass eClass, String name) {
		return (EReference) eClass.getEStructuralFeature(name);
	}

	private static EStructuralFeature attr(EClass eClass, String name) {
		return eClass.getEStructuralFeature(name);
	}

	protected EObject obj(EClass eClass, String id) {
		EObject object = EcoreUtil.create(eClass);
		object.eSet(eClass.getEIDAttribute(), id);
		return object;
	}

	private EObject owner(String id) {
		EObject o = obj(owner, id);
		o.eSet(attr(owner, "name"), "owner " + id);
		return o;
	}

	@SuppressWarnings("unchecked")
	protected static EList<EObject> list(EObject object, EReference reference) {
		return (EList<EObject>) object.eGet(reference);
	}

	private EList<EObject> items(EObject o) {
		return list(o, ownerItems);
	}

	protected static String id(EObject object) {
		return EcoreUtil.getID(object);
	}

	private static List<String> ids(String prefix, int count) {
		return IntStream.rangeClosed(1, count).mapToObj(i -> prefix + i).toList();
	}

	private void save(String typeName, EObject... objects) throws IOException {
		saveIn(createBackendResourceSet(), typeName, objects);
	}

	private void saveIn(ResourceSet resourceSet, String typeName, EObject... objects) throws IOException {
		Resource resource = resourceSet.createResource(uriFor(typeName));
		resource.getContents().addAll(Arrays.asList(objects));
		resource.save(null);
	}

	/**
	 * Saves the resources the objects are in, each once. A change to a bidirectional reference
	 * changes both ends, and EMF's contract — as XMI's — is that each resource saves what its own
	 * objects hold, so every resource an end lives in is saved.
	 */
	private static void saveResourcesOf(EObject... objects) throws IOException {
		Set<Resource> resources = new LinkedHashSet<>();
		for (EObject object : objects) {
			resources.add(object.eResource());
		}
		for (Resource resource : resources) {
			resource.save(null);
		}
	}

	/** A repository over this backend, as its OSGi flavour builds one. */
	private AbstractRepository repository() {
		return new AbstractRepository("writepath", uriFor("WpCustomer").trimSegments(1), this::createBackendResourceSet,
				null, null, null) {
			// all behaviour is generic
		};
	}

	/** Adds both groups to their resources first, then saves both — the order EMF requires. */
	private void saveAll(ResourceSet resourceSet, String firstType, List<EObject> first, String secondType,
			List<EObject> second) throws IOException {
		Resource firstResource = resourceSet.createResource(uriFor(firstType));
		Resource secondResource = resourceSet.createResource(uriFor(secondType));
		firstResource.getContents().addAll(first);
		secondResource.getContents().addAll(second);
		firstResource.save(null);
		secondResource.save(null);
	}

	private void saveOwnerWithItems(String ownerId, String... itemIds) throws IOException {
		EObject o = owner(ownerId);
		for (String itemId : itemIds) {
			items(o).add(obj(item, itemId));
		}
		save("WpOwner", o);
	}

	private void saveCustomerWithOrders(String customerId, String... orderIds) throws IOException {
		ResourceSet resourceSet = createBackendResourceSet();
		EObject c = obj(customer, customerId);
		List<EObject> orders = new ArrayList<>();
		for (String orderId : orderIds) {
			EObject o = obj(order, orderId);
			o.eSet(orderCustomer, c);
			orders.add(o);
		}
		saveAll(resourceSet, "WpCustomer", List.of(c), "WpOrder", orders);
	}

	private void saveStudentsAndCourses() throws IOException {
		ResourceSet resourceSet = createBackendResourceSet();
		EObject k1 = obj(course, "k1");
		EObject k2 = obj(course, "k2");
		EObject s1 = obj(student, "s1");
		EObject s2 = obj(student, "s2");
		list(s1, studentCourses).addAll(List.of(k1, k2));
		list(s2, studentCourses).add(k1);
		saveAll(resourceSet, "WpCourse", List.of(k1, k2), "WpStudent", List.of(s1, s2));
	}

	private void saveTree() throws IOException {
		EObject n1 = obj(node, "n1");
		EObject n2 = obj(node, "n2");
		EObject n3 = obj(node, "n3");
		EObject n4 = obj(node, "n4");
		n2.eSet(nodeParent, n1);
		n3.eSet(nodeParent, n1);
		n4.eSet(nodeParent, n2);
		save("WpNode", n1, n2, n3, n4);
	}

	/** Resolves a stored object by fragment, the way a reference would — after evicting caches. */
	private EObject resolve(ResourceSet resourceSet, String typeName, String id) {
		evictBackendCaches();
		EObject object = resourceSet.getEObject(uriFor(typeName).appendFragment(id), true);
		assertThat(object).as("%s#%s is stored", typeName, id).isNotNull();
		return object;
	}

	/** The object as a fresh resource set with evicted caches sees it. */
	private EObject fresh(String typeName, String id) {
		return resolve(createBackendResourceSet(), typeName, id);
	}

	private Resource load(String typeName) throws IOException {
		evictBackendCaches();
		Resource resource = createBackendResourceSet().createResource(uriFor(typeName));
		resource.load(null);
		return resource;
	}

	private void delete(EObject object) throws IOException {
		((PersistenceResource) object.eResource()).delete(null);
	}

	private void deleteAll(String typeName) throws IOException {
		((PersistenceResource) load(typeName)).delete(null);
	}

	/** The stored ids of a type: in the store, and as a fresh reload sees them. */
	private void assertIds(EClass type, String... expected) throws IOException {
		assertThat(probe.ids(type)).as("stored %s ids (store)", type.getName())
				.containsExactlyInAnyOrder(expected);
		if (!type.isAbstract()) {
			Set<String> reloaded = load(type.getName()).getContents().stream()
					.filter(o -> type.isSuperTypeOf(o.eClass()))
					.map(AbstractWritePathTCK::id).collect(Collectors.toCollection(LinkedHashSet::new));
			assertThat(reloaded).as("stored %s ids (reload)", type.getName()).containsExactlyInAnyOrder(expected);
		}
	}

	private void assertRef(EClass type, String id, EReference reference, String expected) {
		assertThat(probe.reference(type, id, reference)).as("%s#%s.%s (store)", type.getName(), id, reference.getName())
				.isEqualTo(Optional.ofNullable(expected));
		EObject target = (EObject) fresh(storedTypeName(type, id), id).eGet(reference);
		assertThat(target == null ? null : id(target)).as("%s#%s.%s (reload)", type.getName(), id, reference.getName())
				.isEqualTo(expected);
	}

	private void assertRefs(EClass type, String id, EReference reference, String... expected) {
		assertThat(probe.references(type, id, reference)).as("%s#%s.%s (store)", type.getName(), id, reference.getName())
				.containsExactlyInAnyOrder(expected);
		assertThat(list(fresh(storedTypeName(type, id), id), reference).stream().map(AbstractWritePathTCK::id).toList())
				.as("%s#%s.%s (reload)", type.getName(), id, reference.getName()).containsExactlyInAnyOrder(expected);
	}

	private void assertRefsInOrder(EClass type, String id, EReference reference, String... expected) {
		assertThat(probe.references(type, id, reference)).as("%s#%s.%s (store)", type.getName(), id, reference.getName())
				.containsExactly(expected);
		// before the eviction: what a reader served from the backend's cache sees must agree
		// with the store — a merge that left the cache stale shows here
		assertThat(list(cached(storedTypeName(type, id), id), reference).stream().map(AbstractWritePathTCK::id).toList())
				.as("%s#%s.%s (cached read)", type.getName(), id, reference.getName()).containsExactly(expected);
		assertThat(list(fresh(storedTypeName(type, id), id), reference).stream().map(AbstractWritePathTCK::id).toList())
				.as("%s#%s.%s (reload)", type.getName(), id, reference.getName()).containsExactly(expected);
	}

	/** The object as a fresh resource set sees it without evicting caches first. */
	private EObject cached(String typeName, String id) {
		EObject object = createBackendResourceSet().getEObject(uriFor(typeName).appendFragment(id), true);
		assertThat(object).as("%s#%s is stored", typeName, id).isNotNull();
		return object;
	}

	/**
	 * Where contained objects are stored on their own, no stored child may be left that no
	 * container holds — exactly the expected ones remain.
	 */
	private void assertNoOrphans(EClass containedType, String... expected) {
		if (probe.storesContainedObjectsSeparately()) {
			assertThat(probe.ids(containedType)).as("stored %s rows (store, no orphans)", containedType.getName())
					.containsExactlyInAnyOrder(expected);
		}
	}

	/** Objects of an abstract type are addressed through their concrete type's resource. */
	private String storedTypeName(EClass type, String id) {
		if (!type.isAbstract()) {
			return type.getName();
		}
		return probe.ids(lawn).contains(id) ? lawn.getName() : pool.getName();
	}
}
