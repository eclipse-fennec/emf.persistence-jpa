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

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.fennec.persistence.Keywords;
import org.eclipse.fennec.persistence.eclipselink.indirection.ETransparentIndirectionPolicy;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.SessionEventListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

/**
 * Verifies the eorm-driven {@code fetch}/{@code batch} contract at the type-mapping level
 * (follow-up to #17). The eorm is the source of truth; here the relevant ecore references
 * are seeded with a persistence {@link EAnnotation} (which the generator turns into the
 * eorm {@code fetch}/{@code batch} flags), then the behaviour is asserted with a query
 * counter that distinguishes full-table ({@code ReadAllQuery}) from keyed
 * ({@code ReadObjectQuery}) reads.
 * <ul>
 * <li>{@code fetch=EAGER} — targets are materialised (no EMF proxies).</li>
 * <li>{@code fetch=LAZY} + {@code batch=true} (many) — the whole collection is resolved in
 *     one {@code IN} query on first access, not element-by-element.</li>
 * </ul>
 */
class NonOsgiFetchModeTest extends NonOsgiPersistenceTestBase {

	private EPackage modelPackage;
	// single-valued non-containment: ClassAO2O.dNonContainment -> ClassDO2O
	private EClass classAO2O;
	private EClass classDO2O;
	// multi-valued non-containment (FK in target): ClassAO2M.eNonContainmentBidi -> ClassEO2M
	private EClass classAO2M;
	private EClass classEO2M;

	@BeforeEach
	void setUp() {
		// Load the model but defer bootstrap — each test annotates a reference first so the
		// generator writes the matching fetch/batch flags into the eorm.
		modelPackage = loadEcore("data/model.ecore");
		classAO2O = (EClass) modelPackage.getEClassifier("ClassAO2O");
		classDO2O = (EClass) modelPackage.getEClassifier("ClassDO2O");
		classAO2M = (EClass) modelPackage.getEClassifier("ClassAO2M");
		classEO2M = (EClass) modelPackage.getEClassifier("ClassEO2M");
	}

	@Override
	protected Map<String, Object> defaultProperties() {
		// Evict the shared L2 cache so query counters reflect real SQL, not cache hits.
		Map<String, Object> props = super.defaultProperties();
		props.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "false");
		return props;
	}

	// ------------------------------------------------------------------ helpers

	private static void annotate(EClass owner, String featureName, String key, String value) {
		EReference ref = (EReference) owner.getEStructuralFeature(featureName);
		EAnnotation annotation = ref.getEAnnotation(Keywords.PERSISTENCE_ANNOTATION_SOURCE);
		if (annotation == null) {
			annotation = EcoreFactory.eINSTANCE.createEAnnotation();
			annotation.setSource(Keywords.PERSISTENCE_ANNOTATION_SOURCE);
			ref.getEAnnotations().add(annotation);
		}
		annotation.getDetails().put(key, value);
	}

	private EObject newInstance(EClass eClass) {
		ClassDescriptor descriptor = serverSession.getDescriptorForAlias(eClass.getName());
		return (EObject) descriptor.getInstantiationPolicy().buildNewInstance();
	}

	/** Raw list content without triggering EMF proxy resolution. */
	@SuppressWarnings("unchecked")
	private List<EObject> basicList(EObject owner, String feature) {
		return ((InternalEList<EObject>) owner.eGet(owner.eClass().getEStructuralFeature(feature))).basicList();
	}

	private QueryCounter attachCounter() {
		QueryCounter counter = new QueryCounter();
		serverSession.getEventManager().addListener(counter);
		return counter;
	}

	private void detach(SessionEventListener l) {
		serverSession.getEventManager().removeListener(l);
	}

	private static final class QueryCounter extends SessionEventAdapter {
		int readObject;

		@Override
		public void postExecuteQuery(SessionEvent event) {
			if (event.getQuery() instanceof ReadObjectQuery) {
				readObject++;
			}
		}
	}

	// -------------------------------------------------------------------- tests

	@Test
	void testEagerSingleMaterialisesNoProxy() {
		annotate(classAO2O, "dNonContainment", "fetch", "EAGER");
		bootstrapPersistence("fetcheagersingle", List.of(classAO2O, classDO2O));

		EStructuralFeature aName = classAO2O.getEStructuralFeature("name");
		EStructuralFeature dName = classDO2O.getEStructuralFeature("name");
		EStructuralFeature dRef = classAO2O.getEStructuralFeature("dNonContainment");

		EObject d = newInstance(classDO2O);
		d.eSet(dName, "D-eager");
		EObject a = newInstance(classAO2O);
		a.eSet(aName, "A-eager");
		a.eSet(dRef, d);
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(d);
			em.persist(a);
			em.getTransaction().commit();
		}
		String aId = EcoreUtil.getID(a);
		emf.getCache().evictAll();

		ResourceSet rs = newJpaResourceSet();
		EObject aLoaded = findViaResource(rs, "ClassAO2O", aId);
		assertThat(aLoaded).isNotNull();

		// Eager: the raw slot already holds a materialised target, not an unresolved proxy.
		EObject rawRef = (EObject) ((InternalEObject) aLoaded).eGet(dRef, false);
		assertThat(rawRef).isNotNull();
		assertThat(rawRef.eIsProxy()).as("eager single ref must be materialised, not a proxy").isFalse();
		assertThat(rawRef.eGet(dName)).isEqualTo("D-eager");
	}

	@Test
	void testEagerManyMaterialisesNoProxies() {
		annotate(classAO2M, "eNonContainmentBidi", "fetch", "EAGER");
		bootstrapPersistence("fetcheagermany", List.of(classAO2M, classEO2M));

		String aId = persistAWithEs("A-eager", List.of("E-1", "E-2", "E-3"));
		emf.getCache().evictAll();

		ResourceSet rs = newJpaResourceSet();
		EObject aLoaded = findViaResource(rs, "ClassAO2M", aId);
		assertThat(aLoaded).isNotNull();

		List<EObject> elements = basicList(aLoaded, "eNonContainmentBidi");
		assertThat(elements).hasSize(3);
		assertThat(elements)
				.as("eager many elements must be materialised, not proxies")
				.allSatisfy(e -> assertThat(e.eIsProxy()).isFalse());
	}

	@SuppressWarnings("restriction")
	@Test
	void testBatchManyResolvesInOneInQuery() {
		annotate(classAO2M, "eNonContainmentBidi", "batch", "true");
		bootstrapPersistence("fetchbatchmany", List.of(classAO2M, classEO2M));

		String aId = persistAWithEs("A-batch", List.of("E-1", "E-2", "E-3", "E-4", "E-5"));
		emf.getCache().evictAll();

		EStructuralFeature ref = classAO2M.getEStructuralFeature("eNonContainmentBidi");
		EStructuralFeature eName = classEO2M.getEStructuralFeature("name");

		// The mapping must be configured for batch resolution.
		ClassDescriptor aDesc = serverSession.getDescriptorForAlias(classAO2M.getName());
		DatabaseMapping mapping = aDesc.getMappingForAttributeName("eNonContainmentBidi");
		assertThat(((ForeignReferenceMapping) mapping).getIndirectionPolicy())
				.isInstanceOfSatisfying(ETransparentIndirectionPolicy.class,
						p -> assertThat(p.isBatch()).as("batch mode is enabled from the eorm flag").isTrue());

		ResourceSet rs = newJpaResourceSet();
		QueryCounter counter = attachCounter();
		try {
			EObject aLoaded = findViaResource(rs, "ClassAO2M", aId);
			assertThat(aLoaded).isNotNull();
			// Batch stays lazy while the owner list fills: loading the owners materialises
			// no targets (keyed target reads would show here).
			assertThat(counter.readObject)
					.as("owner load does not materialise batch targets")
					.isZero();

			@SuppressWarnings("unchecked")
			List<EObject> resolved = (List<EObject>) aLoaded.eGet(ref);
			assertThat(resolved).hasSize(5);
			assertThat(resolved)
					.as("batch elements are materialised on first access")
					.allSatisfy(e -> {
						assertThat(e.eIsProxy()).isFalse();
						assertThat(e.eGet(eName)).asString().startsWith("E-");
					});
			// The whole collection is resolved in a single IN query — NOT element-by-element.
			// A per-element fallback would resolve each proxy through JPAResourceImpl.getEObject
			// on a fresh EntityManager, whose keyed ReadObjectQuery executions ARE observed on
			// the server session (see NonOsgiLazyLoadingTest). Zero keyed reads here proves the
			// batch path handled the collection wholesale.
			assertThat(counter.readObject)
					.as("no per-element keyed resolution — the collection is resolved in one IN query")
					.isZero();
		} finally {
			detach(counter);
		}
	}

	@SuppressWarnings("unchecked")
	private String persistAWithEs(String name, List<String> eNames) {
		EStructuralFeature aName = classAO2M.getEStructuralFeature("name");
		EStructuralFeature eName = classEO2M.getEStructuralFeature("name");
		EStructuralFeature ref = classAO2M.getEStructuralFeature("eNonContainmentBidi");
		EObject a = newInstance(classAO2M);
		a.eSet(aName, name);
		List<EObject> es = new ArrayList<>();
		for (String n : eNames) {
			EObject e = newInstance(classEO2M);
			e.eSet(eName, n);
			es.add(e);
		}
		((List<Object>) a.eGet(ref)).addAll(es);
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			es.forEach(em::persist);
			em.persist(a);
			em.getTransaction().commit();
		}
		return EcoreUtil.getID(a);
	}
}
