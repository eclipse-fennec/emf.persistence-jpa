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
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.SessionEventListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

/**
 * Verifies AP-47 element-level lazy loading for OneToMany non-containment references
 * with the foreign key in the <em>target</em> table (bidirectional via EOpposite):
 * the owner's list is filled with lightweight EMF proxies from an ID-only query that
 * selects nothing but the target's primary-key column — targets are never materialised
 * until a proxy is resolved through a {@link ResourceSet}.
 */
class NonOsgiOneToManyProxyTest extends NonOsgiPersistenceTestBase {

	private EPackage modelPackage;
	private EClass classAEClass;
	private EClass classEEClass;

	private EStructuralFeature aName;
	private EStructuralFeature aENonContainmentBidi;
	private EStructuralFeature eName;
	private EStructuralFeature eClassA;

	@BeforeEach
	void setUp() {
		modelPackage = loadEcore("data/model.ecore");
		classAEClass = (EClass) modelPackage.getEClassifier("ClassAO2M");
		classEEClass = (EClass) modelPackage.getEClassifier("ClassEO2M");
		aName = classAEClass.getEStructuralFeature("name");
		aENonContainmentBidi = classAEClass.getEStructuralFeature("eNonContainmentBidi");
		eName = classEEClass.getEStructuralFeature("name");
		eClassA = classEEClass.getEStructuralFeature("eClassA");
		bootstrapPersistence("o2mproxy", List.of(classAEClass, classEEClass));
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

	@SuppressWarnings("unchecked")
	private EObject persistAWithEs(String name, List<EObject> es) {
		EObject a = newInstance(classAEClass);
		a.eSet(aName, name);
		((List<Object>) a.eGet(aENonContainmentBidi)).addAll(es);
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			es.forEach(em::persist);
			em.persist(a);
			em.getTransaction().commit();
		}
		return a;
	}

	private EObject newE(String name) {
		EObject e = newInstance(classEEClass);
		e.eSet(eName, name);
		return e;
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
		DatabaseMapping mapping = aDesc.getMappingForAttributeName("eNonContainmentBidi");
		assertThat(mapping).isInstanceOf(OneToManyMapping.class);
		assertThat(((ForeignReferenceMapping) mapping).getIndirectionPolicy())
				.isInstanceOf(ETransparentIndirectionPolicy.class);
	}

	@Test
	void testListElementsAreLazyProxiesWithIdAndUri() {
		EObject e1 = newE("First E");
		EObject e2 = newE("Second E");
		EObject a = persistAWithEs("Owner A", List.of(e1, e2));
		String e1Id = EcoreUtil.getID(e1);
		String e2Id = EcoreUtil.getID(e2);
		emf.getCache().evictAll();

		EObject aLoaded;
		try (EntityManager em = emf.createEntityManager()) {
			ClassDescriptor aDesc = serverSession.getDescriptorForAlias(classAEClass.getName());
			aLoaded = (EObject) em.find(aDesc.getJavaClass(), Integer.valueOf(EcoreUtil.getID(a)));
		}
		assertThat(aLoaded).isNotNull();

		List<EObject> elements = basicList(aLoaded, aENonContainmentBidi);
		assertThat(elements).hasSize(2);
		for (EObject element : elements) {
			assertThat(element.eIsProxy())
					.as("list element must be an unresolved EMF proxy")
					.isTrue();
			String id = EcoreUtil.getID(element);
			assertThat(id).isIn(e1Id, e2Id);
			URI proxyURI = ((InternalEObject) element).eProxyURI();
			assertThat(proxyURI.toString()).isEqualTo(
					"jpa://o2mproxy/ClassEO2M#//eNonContainmentBidi/idE/" + id);
		}
	}

	@Test
	void testLoadDoesNotMaterializeTargets() throws Exception {
		// 20 A's, each with 3 own E's (FK in the E table). Loading all A's must issue
		// exactly one object-building query (the A ReadAll) — the per-A id reads select
		// only the E primary-key column and never materialise an E.
		int totalA = 20;
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			for (int i = 0; i < totalA; i++) {
				EObject a = newInstance(classAEClass);
				a.eSet(aName, "A-" + i);
				@SuppressWarnings("unchecked")
				List<Object> list = (List<Object>) a.eGet(aENonContainmentBidi);
				for (int j = 0; j < 3; j++) {
					EObject e = newE("E-" + i + "-" + j);
					list.add(e);
					em.persist(e);
				}
				em.persist(a);
			}
			em.getTransaction().commit();
		}
		emf.getCache().evictAll();

		QueryCounter counter = attachCounter();
		try {
			ResourceSet resourceSet = newJpaResourceSet();
			Resource aResource = resourceSet.createResource(
					URI.createURI("jpa://o2mproxy/ClassAO2M"));
			aResource.load(null);
			assertThat(aResource.getContents()).hasSize(totalA);
			for (EObject a : aResource.getContents()) {
				assertThat(basicList(a, aENonContainmentBidi)).hasSize(3);
			}
			assertThat(counter.count)
					.as("loading all A's must NOT materialise any E target")
					.isEqualTo(1);
		} finally {
			detach(counter);
		}
	}

	@Test
	void testProxyResolutionViaResourceSet() throws Exception {
		EObject e1 = newE("First E");
		EObject e2 = newE("Second E");
		EObject a = persistAWithEs("Owner A", List.of(e1, e2));
		String aId = EcoreUtil.getID(a);
		emf.getCache().evictAll();

		ResourceSet resourceSet = newJpaResourceSet();
		EObject aLoaded = findViaResource(resourceSet, "ClassAO2M", aId);
		assertThat(aLoaded).isNotNull();

		@SuppressWarnings("unchecked")
		List<EObject> resolved = (List<EObject>) aLoaded.eGet(aENonContainmentBidi);
		assertThat(resolved).hasSize(2);
		assertThat(resolved)
				.allSatisfy(eTarget -> assertThat(eTarget.eIsProxy()).isFalse())
				.extracting(eTarget -> eTarget.eGet(eName))
				.containsExactlyInAnyOrder("First E", "Second E");
		// The resolved E's back reference points to A — resolution may materialise a
		// separate instance, so compare by EMF id.
		for (EObject eTarget : resolved) {
			EObject back = (EObject) EcoreUtil.resolve((EObject) eTarget.eGet(eClassA), resourceSet);
			assertThat(EcoreUtil.getID(back)).isEqualTo(aId);
		}
	}

	@Test
	void testSaveOwnerWithProxyListLeavesTargetsUntouched() throws Exception {
		EObject e1 = newE("E-original");
		EObject a = persistAWithEs("A-original", List.of(e1));
		String aId = EcoreUtil.getID(a);
		String e1Id = EcoreUtil.getID(e1);
		emf.getCache().evictAll();

		ResourceSet resourceSet = newJpaResourceSet();
		Resource aResource = resourceSet.createResource(
				URI.createURI("jpa://o2mproxy/ClassAO2M"));
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
			ClassDescriptor eDesc = serverSession.getDescriptorForAlias(classEEClass.getName());
			EObject aRefreshed = (EObject) em.find(aDesc.getJavaClass(), Integer.valueOf(aId));
			assertThat(aRefreshed.eGet(aName)).isEqualTo("A-updated");
			// Exactly the one original E row — nothing inserted, nothing modified.
			Long eCount = em.createQuery("SELECT COUNT(e) FROM " + eDesc.getAlias() + " e", Long.class)
					.getSingleResult();
			assertThat(eCount).isEqualTo(1L);
			EObject eRefreshed = (EObject) em.find(eDesc.getJavaClass(), Integer.valueOf(e1Id));
			assertThat(eRefreshed.eGet(eName)).isEqualTo("E-original");
			// The FK relation survived the owner update.
			List<EObject> eList = basicList(aRefreshed, aENonContainmentBidi);
			assertThat(eList).hasSize(1);
			assertThat(EcoreUtil.getID(eList.get(0))).isEqualTo(e1Id);
		}
	}
}
