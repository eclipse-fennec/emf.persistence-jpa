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
package org.eclipse.fennec.persistence.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.fennec.persistence.eclipselink.indirection.ETransparentIndirectionPolicy;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.SessionEventListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

/**
 * Verifies AP-47 element-level lazy loading for ManyToMany non-containment
 * references: the owner's list is filled with lightweight EMF proxies built from
 * an ID-only relation-table query — the target table is never touched until a
 * proxy is resolved through a {@link ResourceSet}.
 */
class NonOsgiManyToManyProxyTest extends NonOsgiPersistenceTestBase {

	private EPackage modelPackage;
	private EClass classAEClass;
	private EClass classBEClass;
	private EClass classCEClass;

	private EStructuralFeature aName;
	private EStructuralFeature aBNonContainment;
	private EStructuralFeature aCNonContainmentBidi;
	private EStructuralFeature bName;
	private EStructuralFeature cName;
	private EStructuralFeature cClassA;

	@BeforeEach
	void setUp() {
		modelPackage = loadEcore("data/model.ecore");
		classAEClass = (EClass) modelPackage.getEClassifier("ClassAM2M");
		classBEClass = (EClass) modelPackage.getEClassifier("ClassBM2M");
		classCEClass = (EClass) modelPackage.getEClassifier("ClassCM2M");
		aName = classAEClass.getEStructuralFeature("name");
		aBNonContainment = classAEClass.getEStructuralFeature("bNonContainment");
		aCNonContainmentBidi = classAEClass.getEStructuralFeature("cNonContainmentBidi");
		bName = classBEClass.getEStructuralFeature("name");
		cName = classCEClass.getEStructuralFeature("name");
		cClassA = classCEClass.getEStructuralFeature("cClassA");
		bootstrapPersistence("m2mproxy", List.of(classAEClass, classBEClass, classCEClass));
	}

	@Override
	protected Map<String, Object> defaultProperties() {
		// Disable the shared L2-cache so every read hits the DB and the query
		// counters below reflect actual SQL cost, not cache hits.
		Map<String, Object> props = super.defaultProperties();
		props.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "false");
		return props;
	}

	// ------------------------------------------------------------------ helpers

	private EObject newInstance(EClass eClass) {
		ClassDescriptor descriptor = serverSession.getDescriptorForAlias(eClass.getName());
		return (EObject) descriptor.getInstantiationPolicy().buildNewInstance();
	}

	/** Persists an A with the given name and B targets on bNonContainment. */
	@SuppressWarnings("unchecked")
	private EObject persistAWithBs(String name, List<EObject> bs) {
		EObject a = newInstance(classAEClass);
		a.eSet(aName, name);
		((List<Object>) a.eGet(aBNonContainment)).addAll(bs);
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			bs.forEach(em::persist);
			em.persist(a);
			em.getTransaction().commit();
		}
		return a;
	}

	private EObject newB(String name) {
		EObject b = newInstance(classBEClass);
		b.eSet(bName, name);
		return b;
	}

	/** Returns the raw list content without triggering EMF proxy resolution. */
	@SuppressWarnings("unchecked")
	private List<EObject> basicList(EObject owner, EStructuralFeature feature) {
		return ((InternalEList<EObject>) owner.eGet(feature)).basicList();
	}

	private QueryCounter attachCounter() {
		QueryCounter counter = new QueryCounter();
		serverSession.getEventManager().addListener(counter);
		return counter;
	}

	private void detach(SessionEventListener l) {
		serverSession.getEventManager().removeListener(l);
	}

	/** Counts object-building queries — any materialisation of a target shows up here. */
	private static final class QueryCounter extends SessionEventAdapter {
		int count;

		@Override
		public void postExecuteQuery(SessionEvent event) {
			if (event.getQuery() instanceof ReadObjectQuery
					|| event.getQuery() instanceof ReadAllQuery) {
				count++;
			}
		}
	}

	// -------------------------------------------------------------------- tests

	@SuppressWarnings("restriction")
	@Test
	void testIndirectionPolicyInstalled() {
		ClassDescriptor aDesc = serverSession.getDescriptorForAlias(classAEClass.getName());
		DatabaseMapping mapping = aDesc.getMappingForAttributeName("bNonContainment");
		assertThat(mapping).isInstanceOf(ManyToManyMapping.class);
		assertThat(((ForeignReferenceMapping) mapping).getIndirectionPolicy())
				.isInstanceOf(ETransparentIndirectionPolicy.class);
	}

	@Test
	void testListElementsAreLazyProxiesWithIdAndUri() {
		EObject b1 = newB("First B");
		EObject b2 = newB("Second B");
		EObject a = persistAWithBs("Owner A", List.of(b1, b2));
		String b1Id = EcoreUtil.getID(b1);
		String b2Id = EcoreUtil.getID(b2);
		emf.getCache().evictAll();

		EObject aLoaded;
		try (EntityManager em = emf.createEntityManager()) {
			ClassDescriptor aDesc = serverSession.getDescriptorForAlias(classAEClass.getName());
			aLoaded = (EObject) em.find(aDesc.getJavaClass(), Integer.valueOf(EcoreUtil.getID(a)));
		}
		assertThat(aLoaded).isNotNull();

		List<EObject> elements = basicList(aLoaded, aBNonContainment);
		assertThat(elements).hasSize(2);
		for (EObject element : elements) {
			assertThat(element.eIsProxy())
					.as("list element must be an unresolved EMF proxy")
					.isTrue();
			String id = EcoreUtil.getID(element);
			assertThat(id).isIn(b1Id, b2Id);
			URI proxyURI = ((InternalEObject) element).eProxyURI();
			assertThat(proxyURI.toString()).isEqualTo(
					"jpa://m2mproxy/ClassBM2M#//bNonContainment/idB/" + id);
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	void testLoadDoesNotMaterializeTargets() throws Exception {
		// 20 A's, each referencing 3 B's. Loading all A's must issue exactly one
		// object-building query (the A ReadAll) — the per-A relation-table id reads
		// are DataReadQueries and must never materialise a B.
		int totalA = 20;
		EObject b1 = newB("B-1");
		EObject b2 = newB("B-2");
		EObject b3 = newB("B-3");
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(b1);
			em.persist(b2);
			em.persist(b3);
			for (int i = 0; i < totalA; i++) {
				EObject a = newInstance(classAEClass);
				a.eSet(aName, "A-" + i);
				((List<Object>) a.eGet(aBNonContainment)).addAll(List.of(b1, b2, b3));
				em.persist(a);
			}
			em.getTransaction().commit();
		}
		emf.getCache().evictAll();

		QueryCounter counter = attachCounter();
		try {
			ResourceSet resourceSet = newJpaResourceSet();
			Resource aResource = resourceSet.createResource(
					URI.createURI("jpa://m2mproxy/ClassAM2M"));
			aResource.load(null);
			assertThat(aResource.getContents()).hasSize(totalA);
			for (EObject a : aResource.getContents()) {
				assertThat(basicList(a, aBNonContainment)).hasSize(3);
			}
			assertThat(counter.count)
					.as("loading all A's must NOT materialise any B target")
					.isEqualTo(1);
		} finally {
			detach(counter);
		}
	}

	@Test
	void testProxyResolutionViaResourceSet() throws Exception {
		EObject b1 = newB("First B");
		EObject b2 = newB("Second B");
		EObject a = persistAWithBs("Owner A", List.of(b1, b2));
		String aId = EcoreUtil.getID(a);
		emf.getCache().evictAll();

		ResourceSet resourceSet = newJpaResourceSet();
		EObject aLoaded = findViaResource(resourceSet, "ClassAM2M", aId);
		assertThat(aLoaded).isNotNull();

		// Normal (resolving) list access — EMF resolves each proxy through the
		// ResourceSet → JPAResource → em.find on first touch.
		@SuppressWarnings("unchecked")
		List<EObject> resolved = (List<EObject>) aLoaded.eGet(aBNonContainment);
		assertThat(resolved).hasSize(2);
		assertThat(resolved)
				.allSatisfy(bTarget -> assertThat(bTarget.eIsProxy()).isFalse())
				.extracting(bTarget -> bTarget.eGet(bName))
				.containsExactlyInAnyOrder("First B", "Second B");
	}

	@SuppressWarnings("unchecked")
	@Test
	void testBidirectionalProxiesResolveBothSides() throws Exception {
		EObject c1 = newInstance(classCEClass);
		c1.eSet(cName, "First C");
		EObject a = newInstance(classAEClass);
		a.eSet(aName, "Owner A");
		((List<Object>) a.eGet(aCNonContainmentBidi)).add(c1);

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(c1);
			em.persist(a);
			em.getTransaction().commit();
		}
		String aId = EcoreUtil.getID(a);
		String cId = EcoreUtil.getID(c1);
		emf.getCache().evictAll();

		ResourceSet resourceSet = newJpaResourceSet();
		EObject aLoaded = findViaResource(resourceSet, "ClassAM2M", aId);
		List<EObject> cList = (List<EObject>) aLoaded.eGet(aCNonContainmentBidi);
		assertThat(cList).hasSize(1);
		assertThat(cList.get(0).eGet(cName)).isEqualTo("First C");

		ResourceSet resourceSet2 = newJpaResourceSet();
		EObject cLoaded = findViaResource(resourceSet2, "ClassCM2M", cId);
		List<EObject> aList = (List<EObject>) cLoaded.eGet(cClassA);
		assertThat(aList).hasSize(1);
		assertThat(aList.get(0).eGet(aName)).isEqualTo("Owner A");
	}

	@Test
	void testSaveOwnerWithProxyListLeavesTargetsUntouched() throws Exception {
		EObject b1 = newB("B-original");
		EObject a = persistAWithBs("A-original", List.of(b1));
		String aId = EcoreUtil.getID(a);
		String b1Id = EcoreUtil.getID(b1);
		emf.getCache().evictAll();

		// Load A via resource (list stays proxies), mutate an A attribute, save.
		ResourceSet resourceSet = newJpaResourceSet();
		Resource aResource = resourceSet.createResource(
				URI.createURI("jpa://m2mproxy/ClassAM2M"));
		aResource.load(null);
		EObject aLoaded = null;
		for (EObject eo : aResource.getContents()) {
			if (aId.equals(EcoreUtil.getID(eo))) {
				aLoaded = eo;
				break;
			}
		}
		assertThat(aLoaded).isNotNull();
		aLoaded.eSet(aName, "A-updated");
		aResource.save(null);

		emf.getCache().evictAll();
		try (EntityManager em = emf.createEntityManager()) {
			ClassDescriptor aDesc = serverSession.getDescriptorForAlias(classAEClass.getName());
			ClassDescriptor bDesc = serverSession.getDescriptorForAlias(classBEClass.getName());
			EObject aRefreshed = (EObject) em.find(aDesc.getJavaClass(), Integer.valueOf(aId));
			assertThat(aRefreshed.eGet(aName)).isEqualTo("A-updated");
			// Exactly the one original B row — nothing inserted, nothing modified.
			Long bCount = em.createQuery("SELECT COUNT(b) FROM " + bDesc.getAlias() + " b", Long.class)
					.getSingleResult();
			assertThat(bCount).isEqualTo(1L);
			EObject bRefreshed = (EObject) em.find(bDesc.getJavaClass(), Integer.valueOf(b1Id));
			assertThat(bRefreshed.eGet(bName)).isEqualTo("B-original");
			// The relation row survived the owner update.
			List<EObject> bList = basicList(aRefreshed, aBNonContainment);
			assertThat(bList).hasSize(1);
			assertThat(EcoreUtil.getID(bList.get(0))).isEqualTo(b1Id);
		}
	}
}
