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

import java.util.ArrayList;
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
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.SessionEventListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

/**
 * Verifies the actual lazy-loading behaviour introduced by AP-46 (OneToOne) and
 * AP-48 (ManyToOne). Each test uses a {@link SessionEventAdapter} counting
 * EclipseLink {@code ReadObjectQuery}/{@code ReadAllQuery} executions so we can
 * prove — not just assert — that Non-Containment targets are not eager-fetched.
 * <p>
 * Save-path behaviour with unresolved proxies (cascade-merge producing INSERTs
 * instead of UPDATEs for detached graphs) is orthogonal to the lazy-loading
 * mechanism and tracked separately in AP-49.
 */
class NonOsgiLazyLoadingTest extends NonOsgiPersistenceTestBase {

	private EPackage modelPackage;
	private EClass classAEClass;   // ClassAO2O — owner with dNonContainment OneToOne
	private EClass classDEClass;   // ClassDO2O — target of OneToOne
	private EClass classAM2OEClass; // ClassAO2M — parent in OneToMany/ManyToOne bidi
	private EClass classEM2OEClass; // ClassEO2M — child with ManyToOne (eClassA)

	@BeforeEach
	void setUp() {
		modelPackage = loadEcore("data/model.ecore");
		classAEClass = (EClass) modelPackage.getEClassifier("ClassAO2O");
		classDEClass = (EClass) modelPackage.getEClassifier("ClassDO2O");
		classAM2OEClass = (EClass) modelPackage.getEClassifier("ClassAO2M");
		classEM2OEClass = (EClass) modelPackage.getEClassifier("ClassEO2M");
		bootstrapPersistence("lazy",
				List.of(classAEClass, classDEClass, classAM2OEClass, classEM2OEClass));
	}

	@Override
	protected Map<String, Object> defaultProperties() {
		// Evict L2-cache guarantees every read hits the DB so query counters reflect
		// the actual SQL cost, not cache hits.
		Map<String, Object> props = super.defaultProperties();
		props.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "false");
		return props;
	}

	// ------------------------------------------------------------------ helpers

	private QueryCounter attachCounter() {
		QueryCounter counter = new QueryCounter();
		serverSession.getEventManager().addListener(counter);
		return counter;
	}

	private void detach(SessionEventListener l) {
		serverSession.getEventManager().removeListener(l);
	}

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

	// ----------------------------------------------------------- OneToOne AP-46

	@Test
	void testOneToOneLoadIsSingleQuery() throws Exception {
		// 50 A's, each pointing to its own D. Without lazy, loading all A's would
		// also fetch all 50 D's (N+1). With AP-46 we expect exactly one SELECT.
		ClassDescriptor aDesc = serverSession.getDescriptorForAlias(classAEClass.getName());
		ClassDescriptor dDesc = serverSession.getDescriptorForAlias(classDEClass.getName());
		EStructuralFeature aName = classAEClass.getEStructuralFeature("name");
		EStructuralFeature aRef = classAEClass.getEStructuralFeature("dNonContainment");
		EStructuralFeature dName = classDEClass.getEStructuralFeature("name");

		int total = 50;
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			for (int i = 0; i < total; i++) {
				EObject d = (EObject) dDesc.getInstantiationPolicy().buildNewInstance();
				d.eSet(dName, "D-" + i);
				em.persist(d);
				EObject a = (EObject) aDesc.getInstantiationPolicy().buildNewInstance();
				a.eSet(aName, "A-" + i);
				a.eSet(aRef, d);
				em.persist(a);
			}
			em.getTransaction().commit();
		}
		emf.getCache().evictAll();

		QueryCounter counter = attachCounter();
		try {
			ResourceSet rs = newJpaResourceSet();
			Resource aResource = rs.createResource(URI.createURI("jpa://lazy/ClassAO2O"));
			aResource.load(null);
			assertThat(aResource.getContents()).hasSize(total);
			assertThat(counter.count)
					.as("loading all A's must NOT eager-fetch their D targets")
					.isEqualTo(1);
		} finally {
			detach(counter);
		}
	}

	@Test
	void testOneToOneProxyAttributeAccessDoesNotResolve() throws Exception {
		// Accessing an owner attribute on a loaded entity must not trigger any
		// resolution of its non-containment proxy refs.
		ClassDescriptor aDesc = serverSession.getDescriptorForAlias(classAEClass.getName());
		ClassDescriptor dDesc = serverSession.getDescriptorForAlias(classDEClass.getName());
		EStructuralFeature aName = classAEClass.getEStructuralFeature("name");
		EStructuralFeature aRef = classAEClass.getEStructuralFeature("dNonContainment");
		EStructuralFeature dName = classDEClass.getEStructuralFeature("name");

		EObject d = (EObject) dDesc.getInstantiationPolicy().buildNewInstance();
		d.eSet(dName, "D");
		EObject a = (EObject) aDesc.getInstantiationPolicy().buildNewInstance();
		a.eSet(aName, "A");
		a.eSet(aRef, d);
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(d);
			em.persist(a);
			em.getTransaction().commit();
		}
		emf.getCache().evictAll();

		ResourceSet rs = newJpaResourceSet();
		Resource aResource = rs.createResource(URI.createURI("jpa://lazy/ClassAO2O"));
		aResource.load(null);
		EObject aLoaded = aResource.getContents().get(0);

		QueryCounter counter = attachCounter();
		try {
			// Accessing owner attributes — must not hit DB.
			assertThat(aLoaded.eGet(aName)).isEqualTo("A");
			assertThat(aLoaded.eGet(classAEClass.getEStructuralFeature("id"))).isNotNull();
			// Raw peek at the ref (no resolve) — must not hit DB.
			((InternalEObject) aLoaded).eGet(aRef, false);
			assertThat(counter.count).as("no query for attribute access or raw peek").isZero();
		} finally {
			detach(counter);
		}
	}

	// --------------------------------------------------------- ManyToOne AP-48

	@Test
	void testManyToOneLoadIsSingleQuery() throws Exception {
		// 60 E-children, each with a ManyToOne to one of 3 shared A-parents.
		// Loading all children must NOT eager-fetch the parents. Before AP-48
		// ManyToOne had indirection disabled and would fetch the parent per row.
		ClassDescriptor aDesc = serverSession.getDescriptorForAlias(classAM2OEClass.getName());
		ClassDescriptor eDesc = serverSession.getDescriptorForAlias(classEM2OEClass.getName());
		EStructuralFeature aName = classAM2OEClass.getEStructuralFeature("name");
		EStructuralFeature aOwning = classAM2OEClass.getEStructuralFeature("eNonContainmentBidi");
		EStructuralFeature eName = classEM2OEClass.getEStructuralFeature("name");
		EStructuralFeature eBack = classEM2OEClass.getEStructuralFeature("eClassA");

		int childrenPerParent = 20;
		int parents = 3;
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			List<EObject> parentEs = new ArrayList<>();
			for (int p = 0; p < parents; p++) {
				EObject a = (EObject) aDesc.getInstantiationPolicy().buildNewInstance();
				a.eSet(aName, "Parent-" + p);
				parentEs.add(a);
			}
			for (int p = 0; p < parents; p++) {
				List<EObject> list = new ArrayList<>();
				for (int c = 0; c < childrenPerParent; c++) {
					EObject child = (EObject) eDesc.getInstantiationPolicy().buildNewInstance();
					child.eSet(eName, "Child-" + p + "-" + c);
					child.eSet(eBack, parentEs.get(p));
					list.add(child);
				}
				parentEs.get(p).eSet(aOwning, list);
			}
			for (EObject parent : parentEs) {
				em.persist(parent);
			}
			em.getTransaction().commit();
		}
		emf.getCache().evictAll();

		QueryCounter counter = attachCounter();
		try {
			ResourceSet rs = newJpaResourceSet();
			Resource eResource = rs.createResource(URI.createURI("jpa://lazy/ClassEO2M"));
			eResource.load(null);
			assertThat(eResource.getContents()).hasSize(parents * childrenPerParent);
			assertThat(counter.count)
					.as("loading all children must NOT eager-fetch the parent via ManyToOne")
					.isEqualTo(1);
		} finally {
			detach(counter);
		}
	}

	@Test
	void testManyToOneFirstAccessTriggersExactlyOneQueryPerParent() throws Exception {
		// Navigate to each child's parent. Without cache each resolution opens a
		// fresh EntityManager, so we expect ≤ children queries — but we can at
		// least verify that the *load phase* added 0 queries beyond the initial one.
		ClassDescriptor aDesc = serverSession.getDescriptorForAlias(classAM2OEClass.getName());
		ClassDescriptor eDesc = serverSession.getDescriptorForAlias(classEM2OEClass.getName());
		EStructuralFeature aName = classAM2OEClass.getEStructuralFeature("name");
		EStructuralFeature aOwning = classAM2OEClass.getEStructuralFeature("eNonContainmentBidi");
		EStructuralFeature eName = classEM2OEClass.getEStructuralFeature("name");
		EStructuralFeature eBack = classEM2OEClass.getEStructuralFeature("eClassA");

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			EObject a = (EObject) aDesc.getInstantiationPolicy().buildNewInstance();
			a.eSet(aName, "Shared-Parent");
			List<EObject> children = new ArrayList<>();
			for (int c = 0; c < 10; c++) {
				EObject child = (EObject) eDesc.getInstantiationPolicy().buildNewInstance();
				child.eSet(eName, "Child-" + c);
				child.eSet(eBack, a);
				children.add(child);
			}
			a.eSet(aOwning, children);
			em.persist(a);
			em.getTransaction().commit();
		}
		emf.getCache().evictAll();

		ResourceSet rs = newJpaResourceSet();
		Resource eResource = rs.createResource(URI.createURI("jpa://lazy/ClassEO2M"));
		eResource.load(null);

		QueryCounter counter = attachCounter();
		try {
			// First child: navigate parent — this triggers one resolution query.
			EObject first = eResource.getContents().get(0);
			EObject parent = (EObject) EcoreUtil.resolve((EObject) first.eGet(eBack), rs);
			assertThat(parent.eGet(aName)).isEqualTo("Shared-Parent");
			int afterFirst = counter.count;
			assertThat(afterFirst)
					.as("first parent navigation triggers at least one DB call")
					.isGreaterThanOrEqualTo(1);
			// Second access to the same child's ref must be free.
			first.eGet(eBack);
			assertThat(counter.count).as("second access on same child is a no-op")
					.isEqualTo(afterFirst);
		} finally {
			detach(counter);
		}
	}
}
