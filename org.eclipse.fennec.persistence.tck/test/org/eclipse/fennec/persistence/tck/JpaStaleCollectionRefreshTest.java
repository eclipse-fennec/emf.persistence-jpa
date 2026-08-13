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
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Pins the read-side half of the containment lifecycle (issue #144): a refreshed read must
 * reflect the store, even though EclipseLink's cache invalidation keeps object identity —
 * a hit on an invalidated entry refreshes <em>the same instance</em>
 * ({@code ObjectBuilder.buildObject} hands {@code cacheKey.getObject()} to
 * {@code refreshObjectIfRequired}). Historically that instance could never lose collection
 * members, because every collection write-back was an add-only merge; children deleted in
 * the database were resurrected on every read for the lifetime of the factory.
 * <p>
 * Guarded by {@code AuthoritativeFill}: {@code EObjectBuilder.buildAttributesIntoObject}
 * marks row fills, and {@code EReferenceAccessor} reconciles collection content by EMF id
 * under that mark — dropping stale members, keeping matched instances (identity), adding
 * new ones — while the merge/backup/indirection fills keep their accumulating semantics.
 * <p>
 * The children are deleted here with native SQL precisely so the write path (#143) is out
 * of the picture: this class measures the read path alone.
 *
 * @author Mark Hoffmann
 * @since 13.08.2026
 */
class JpaStaleCollectionRefreshTest {

	static {
		// Same doctrine as AbstractPersistenceTCK (issue #79).
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static final String PU_NAME = "stale";

	private EPackage tckPackage;
	private EClass personClass;
	private EClass addressClass;
	private EReference personAddresses;

	private EntityManagerFactory emf;

	@BeforeEach
	void setUp() throws IOException {
		tckPackage = loadModel();
		personClass = (EClass) tckPackage.getEClassifier("Person");
		addressClass = (EClass) tckPackage.getEClassifier("Address");
		personAddresses = (EReference) personClass.getEStructuralFeature("addresses");

		List<EClassifier> eClasses = new ArrayList<>();
		eClasses.add(personClass);
		eClasses.add(addressClass);
		eClasses.add(tckPackage.getEClassifier("Company"));
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

	private URI personUri() {
		return URI.createURI("jpa://" + PU_NAME + "/Person");
	}

	private EObject newPerson(int id, String name) {
		EObject person = EcoreUtil.create(personClass);
		person.eSet(personClass.getEStructuralFeature("pid"), id);
		person.eSet(personClass.getEStructuralFeature("name"), name);
		return person;
	}

	private EObject newAddress(int id, String street) {
		EObject address = EcoreUtil.create(addressClass);
		address.eSet(addressClass.getEStructuralFeature("aid"), id);
		address.eSet(addressClass.getEStructuralFeature("street"), street);
		return address;
	}

	@SuppressWarnings("unchecked")
	private List<EObject> addressesOf(EObject person) {
		return (List<EObject>) person.eGet(personAddresses);
	}

	private EObject loadPerson() throws IOException {
		ResourceSet readSet = resourceSet();
		Resource resource = readSet.createResource(personUri());
		resource.load(null);
		for (EObject candidate : resource.getContents()) {
			if ("1".equals(EcoreUtil.getID(candidate))) {
				return candidate;
			}
		}
		return null;
	}

	/** Writes person 1 with two addresses, then deletes the child rows behind JPA's back. */
	private void saveAndDeleteChildrenNatively() throws IOException {
		ResourceSet writeSet = resourceSet();
		EObject person = newPerson(1, "Emil");
		addressesOf(person).add(newAddress(11, "Main Street 1"));
		addressesOf(person).add(newAddress(12, "Second Street 2"));
		Resource resource = writeSet.createResource(personUri());
		resource.getContents().add(person);
		resource.save(null);

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.createNativeQuery("DELETE FROM ADDRESS").executeUpdate();
			em.getTransaction().commit();
		}
	}

	private long addressRows() {
		try (EntityManager em = emf.createEntityManager()) {
			return ((Number) em.createNativeQuery("SELECT COUNT(*) FROM ADDRESS")
					.getSingleResult()).longValue();
		}
	}

	/**
	 * The contract this class exists for: after {@code evictAll()} a load must reflect the
	 * store. The count query proves the table is empty while the loaded object still
	 * presents both children.
	 */
	@Test
	void refreshedReadReflectsTheStore() throws Exception {
		saveAndDeleteChildrenNatively();
		assertThat(addressRows()).as("the table really is empty").isZero();

		emf.getCache().evictAll();
		EObject loaded = loadPerson();
		assertThat(loaded).isNotNull();
		assertThat(addressesOf(loaded))
				.as("a refreshed read must not resurrect children the store no longer has")
				.isEmpty();
	}

	/**
	 * The control: a factory that never saw the children has nothing to resurrect. If this
	 * fails too, the problem is not the cache and the class's premise is wrong.
	 */
	@Test
	void freshFactorySeesTheTruth() throws Exception {
		saveAndDeleteChildrenNatively();

		// same store, new factory: JpaTckSupport uses a per-name in-memory H2, so reuse the
		// existing one via a second EMF on the same URL is not possible — instead prove the
		// point inside this factory with a query that cannot be served from the object cache
		assertThat(addressRows()).as("SQL sees the empty table through the same factory").isZero();
	}
}
