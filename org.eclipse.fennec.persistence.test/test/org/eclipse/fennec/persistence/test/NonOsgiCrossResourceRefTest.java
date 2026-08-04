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

import java.io.File;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jakarta.persistence.EntityManager;

/**
 * Tests cross-resource reference resolution for AP-30:
 * JPA↔JPA (two separate {@code jpa://} Resources in one ResourceSet) and
 * JPA↔XMI (one JPA Resource + one XMI file Resource in one ResourceSet).
 */
class NonOsgiCrossResourceRefTest extends NonOsgiPersistenceTestBase {

	private EPackage modelPackage;
	private EClass classAEClass;
	private EClass classDEClass;
	private EClass classEEClass;

	@BeforeEach
	void setUp() {
		modelPackage = loadEcore("data/model.ecore");
		classAEClass = (EClass) modelPackage.getEClassifier("ClassAO2O");
		classDEClass = (EClass) modelPackage.getEClassifier("ClassDO2O");
		classEEClass = (EClass) modelPackage.getEClassifier("ClassEO2O");
		bootstrapPersistence("xref",
				List.of(classAEClass, classDEClass, classEEClass));
	}

	@Test
	void testJpaToJpaUnidirectionalCrossResource() throws Exception {
		ClassDescriptor aDescriptor = serverSession.getDescriptorForAlias(classAEClass.getName());
		ClassDescriptor dDescriptor = serverSession.getDescriptorForAlias(classDEClass.getName());

		EObject a = (EObject) aDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject d = (EObject) dDescriptor.getInstantiationPolicy().buildNewInstance();
		a.eSet(classAEClass.getEStructuralFeature("name"), "A");
		d.eSet(classDEClass.getEStructuralFeature("name"), "D");
		a.eSet(classAEClass.getEStructuralFeature("dNonContainment"), d);

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(d);
			em.persist(a);
			em.getTransaction().commit();
		}

		String aId = EcoreUtil.getID(a);
		String dId = EcoreUtil.getID(d);

		ResourceSet rs = newMixedResourceSet();
		Resource aResource = rs.createResource(URI.createURI("jpa://xref/ClassAO2O"));
		Resource dResource = rs.createResource(URI.createURI("jpa://xref/ClassDO2O"));
		aResource.load(null);
		dResource.load(null);

		EObject aLoaded = findById(aResource, aId);
		EObject dLoaded = findById(dResource, dId);
		assertThat(aLoaded).as("A loaded").isNotNull();
		assertThat(dLoaded).as("D loaded").isNotNull();

		EObject aRef = (EObject) aLoaded.eGet(classAEClass.getEStructuralFeature("dNonContainment"));
		assertThat(aRef).as("cross-resource ref resolved").isNotNull();
		assertThat(aRef.eGet(classDEClass.getEStructuralFeature("name"))).isEqualTo("D");
	}

	@Test
	void testJpaToJpaBidirectionalCrossResource() throws Exception {
		ClassDescriptor aDescriptor = serverSession.getDescriptorForAlias(classAEClass.getName());
		ClassDescriptor eDescriptor = serverSession.getDescriptorForAlias(classEEClass.getName());

		EObject a = (EObject) aDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject e = (EObject) eDescriptor.getInstantiationPolicy().buildNewInstance();
		a.eSet(classAEClass.getEStructuralFeature("name"), "A");
		e.eSet(classEEClass.getEStructuralFeature("name"), "E");
		// Setting A.eNonContainmentBidi = E also sets E.eClassA = A via EOpposite
		a.eSet(classAEClass.getEStructuralFeature("eNonContainmentBidi"), e);

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(e);
			em.persist(a);
			em.getTransaction().commit();
		}

		String aId = EcoreUtil.getID(a);
		String eId = EcoreUtil.getID(e);

		ResourceSet rs = newMixedResourceSet();
		Resource aResource = rs.createResource(URI.createURI("jpa://xref/ClassAO2O"));
		Resource eResource = rs.createResource(URI.createURI("jpa://xref/ClassEO2O"));
		aResource.load(null);
		eResource.load(null);

		EObject aLoaded = findById(aResource, aId);
		EObject eLoaded = findById(eResource, eId);
		EObject aForward = (EObject) aLoaded.eGet(classAEClass.getEStructuralFeature("eNonContainmentBidi"));
		EObject eBack = (EObject) eLoaded.eGet(classEEClass.getEStructuralFeature("eClassA"));
		assertThat(aForward).as("A→E resolves").isNotNull();
		assertThat(aForward.eGet(classEEClass.getEStructuralFeature("name"))).isEqualTo("E");
		assertThat(eBack).as("E→A resolves").isNotNull();
		assertThat(eBack.eGet(classAEClass.getEStructuralFeature("name"))).isEqualTo("A");
	}

	@Test
	void testResourceSetGetEObjectResolvesJpaFragment() throws Exception {
		ClassDescriptor dDescriptor = serverSession.getDescriptorForAlias(classDEClass.getName());
		EObject d = (EObject) dDescriptor.getInstantiationPolicy().buildNewInstance();
		d.eSet(classDEClass.getEStructuralFeature("name"), "D-direct");

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(d);
			em.getTransaction().commit();
		}

		String dId = EcoreUtil.getID(d);
		ResourceSet rs = newMixedResourceSet();
		rs.createResource(URI.createURI("jpa://xref/ClassDO2O"));

		// Direct fragment resolution via ResourceSet — this is the public entry point
		// EMF uses when resolving proxy URIs across resources.
		URI proxyURI = URI.createURI("jpa://xref/ClassDO2O#//dNonContainment/id/" + dId);
		EObject resolved = rs.getEObject(proxyURI, true);

		assertThat(resolved).as("ResourceSet resolves JPA proxy URI").isNotNull();
		assertThat(resolved.eGet(classDEClass.getEStructuralFeature("name"))).isEqualTo("D-direct");
	}

	@Test
	void testUnresolvableJpaFragmentReturnsNull() {
		ResourceSet rs = newMixedResourceSet();
		rs.createResource(URI.createURI("jpa://xref/ClassDO2O"));

		URI unknownURI = URI.createURI("jpa://xref/ClassDO2O#//ref/id/99999");
		EObject resolved = rs.getEObject(unknownURI, true);

		assertThat(resolved).as("unknown ID yields null, not an exception").isNull();
	}

	@Test
	void testMixedResourceSetWithJpaAndXmi(@TempDir File tempDir) throws Exception {
		// Persist a ClassAO2O in JPA.
		ClassDescriptor aDescriptor = serverSession.getDescriptorForAlias(classAEClass.getName());
		EObject aJpa = (EObject) aDescriptor.getInstantiationPolicy().buildNewInstance();
		aJpa.eSet(classAEClass.getEStructuralFeature("name"), "A-JPA");

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(aJpa);
			em.getTransaction().commit();
		}
		String aId = EcoreUtil.getID(aJpa);

		// Write a standalone ClassDO2O into an XMI file — this object is NOT JPA-managed.
		File xmiFile = new File(tempDir, "classD.xmi");
		ResourceSet writeRs = newMixedResourceSet();
		Resource xmiResource = writeRs.createResource(URI.createFileURI(xmiFile.getAbsolutePath()));
		EObject dXmi = EcoreUtil.create(classDEClass);
		dXmi.eSet(classDEClass.getEStructuralFeature("id"), 42);
		dXmi.eSet(classDEClass.getEStructuralFeature("name"), "D-XMI");
		xmiResource.getContents().add(dXmi);
		xmiResource.save(null);

		// In a fresh ResourceSet, mount both JPA and XMI resources side by side
		// and verify each resolves independently.
		ResourceSet rs = newMixedResourceSet();
		Resource aResource = rs.createResource(URI.createURI("jpa://xref/ClassAO2O"));
		Resource xmiReloaded = rs.createResource(URI.createFileURI(xmiFile.getAbsolutePath()));
		aResource.load(null);
		xmiReloaded.load(null);

		EObject aLoaded = findById(aResource, aId);
		assertThat(aLoaded).as("JPA A loaded").isNotNull();
		assertThat(aLoaded.eGet(classAEClass.getEStructuralFeature("name"))).isEqualTo("A-JPA");

		assertThat(xmiReloaded.getContents()).hasSize(1);
		EObject dLoaded = xmiReloaded.getContents().get(0);
		assertThat(dLoaded.eGet(classDEClass.getEStructuralFeature("name"))).isEqualTo("D-XMI");

		// Cross-resource fragment resolution: proxy URI pointing at the XMI file resolves,
		// and proxy URI pointing at the JPA resource also resolves — both from the same RS.
		URI xmiProxyURI = URI.createFileURI(xmiFile.getAbsolutePath())
				.appendFragment("/"); // first root object
		assertThat(rs.getEObject(xmiProxyURI, true)).isNotNull();

		URI jpaProxyURI = URI.createURI("jpa://xref/ClassAO2O#//ref/id/" + aId);
		EObject resolvedFromJpa = rs.getEObject(jpaProxyURI, true);
		assertThat(resolvedFromJpa).as("JPA fragment resolves in mixed RS").isNotNull();
		assertThat(resolvedFromJpa.eGet(classAEClass.getEStructuralFeature("name"))).isEqualTo("A-JPA");
	}

	@Test
	void testJpaNonContainmentRefIsProxyUntilAccessed() throws Exception {
		ClassDescriptor aDescriptor = serverSession.getDescriptorForAlias(classAEClass.getName());
		ClassDescriptor dDescriptor = serverSession.getDescriptorForAlias(classDEClass.getName());

		EObject a = (EObject) aDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject d = (EObject) dDescriptor.getInstantiationPolicy().buildNewInstance();
		a.eSet(classAEClass.getEStructuralFeature("name"), "A-parent");
		d.eSet(classDEClass.getEStructuralFeature("name"), "D-target");
		a.eSet(classAEClass.getEStructuralFeature("dNonContainment"), d);

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(d);
			em.persist(a);
			em.getTransaction().commit();
		}
		String aId = EcoreUtil.getID(a);

		emf.getCache().evictAll();

		ResourceSet rs = newMixedResourceSet();
		Resource aResource = rs.createResource(URI.createURI("jpa://xref/ClassAO2O"));
		// D-resource only registered; deliberately NOT loaded — forces proxy resolution
		// to flow through the ResourceSet → JPAResourceImpl route on first access.
		rs.createResource(URI.createURI("jpa://xref/ClassDO2O"));
		aResource.load(null);

		EObject aLoaded = findById(aResource, aId);
		assertThat(aLoaded).as("A loaded from JPA").isNotNull();

		EStructuralFeature dNonContainmentFeature = classAEClass.getEStructuralFeature("dNonContainment");

		// Raw peek — eGet(feature, /*resolve*/ false) skips proxy resolution. The
		// non-containment ref is stored as an EclipseLink-managed dynamic proxy with
		// eProxyURI targeting the ClassDO2O resource, so eIsProxy() == true.
		EObject beforeAccess = (EObject) ((InternalEObject) aLoaded)
				.eGet(dNonContainmentFeature, false);
		assertThat(beforeAccess).as("raw non-containment ref").isNotNull();
		assertThat(beforeAccess.eIsProxy())
				.as("non-containment ref is a proxy before default eGet")
				.isTrue();
		assertThat(((InternalEObject) beforeAccess).eProxyURI().toString())
				.as("proxy URI points at ClassDO2O resource")
				.startsWith("jpa://xref/ClassDO2O#");

		// Default-resolving eGet → ResourceSet → JPAResourceImpl.getEObject → em.find →
		// fully initialised D entity with the correct content.
		EObject resolvedRef = (EObject) aLoaded.eGet(dNonContainmentFeature);
		assertThat(resolvedRef).as("resolved ref").isNotNull();
		assertThat(resolvedRef.eIsProxy()).as("proxy resolved after default eGet").isFalse();
		assertThat(resolvedRef.eClass()).isEqualTo(classDEClass);
		assertThat(resolvedRef.eGet(classDEClass.getEStructuralFeature("name"))).isEqualTo("D-target");
		assertThat(EcoreUtil.getID(resolvedRef)).isEqualTo(EcoreUtil.getID(d));
	}

	@Test
	void testXmiReferenceResolvesToJpaEntity(@TempDir File tempDir) throws Exception {
		// 1. Persist a ClassAO2O in JPA — this is the target of the cross-resource ref.
		ClassDescriptor aDescriptor = serverSession.getDescriptorForAlias(classAEClass.getName());
		EObject a = (EObject) aDescriptor.getInstantiationPolicy().buildNewInstance();
		a.eSet(classAEClass.getEStructuralFeature("name"), "A-in-JPA");

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(a);
			em.getTransaction().commit();
		}
		String aId = EcoreUtil.getID(a);

		// 2. Write an XMI file with a ClassDO2O whose dClassA is a *proxy* referencing the
		//    JPA-persisted A via the jpa://-URI fragment format expected by JPAResourceImpl.
		File xmiFile = new File(tempDir, "classD-with-jpa-ref.xmi");
		ResourceSet writeRs = newMixedResourceSet();
		Resource xmiResource = writeRs.createResource(URI.createFileURI(xmiFile.getAbsolutePath()));

		EObject aProxy = EcoreUtil.create(classAEClass);
		((InternalEObject) aProxy).eSetProxyURI(
				URI.createURI("jpa://xref/ClassAO2O#//dClassA/id/" + aId));

		EObject dXmi = EcoreUtil.create(classDEClass);
		dXmi.eSet(classDEClass.getEStructuralFeature("id"), 777);
		dXmi.eSet(classDEClass.getEStructuralFeature("name"), "D-xref-to-A");
		dXmi.eSet(classDEClass.getEStructuralFeature("dClassA"), aProxy);

		xmiResource.getContents().add(dXmi);
		xmiResource.save(null);

		// 3. Load the XMI file in a fresh ResourceSet that ALSO has the JPA factory,
		//    so proxy resolution can cross from XMI into the JPA-backed resource.
		ResourceSet readRs = newMixedResourceSet();
		Resource xmiReloaded = readRs.createResource(URI.createFileURI(xmiFile.getAbsolutePath()));
		xmiReloaded.load(null);

		EObject dLoaded = xmiReloaded.getContents().get(0);
		assertThat(dLoaded.eGet(classDEClass.getEStructuralFeature("name"))).isEqualTo("D-xref-to-A");

		// 4. Access the cross-resource reference — EMF resolves the proxy via the
		//    ResourceSet, which routes to JPAResourceImpl.getEObject() and produces
		//    a fully initialised A entity from the database.
		EObject resolved = (EObject) dLoaded.eGet(classDEClass.getEStructuralFeature("dClassA"));
		assertThat(resolved).as("dClassA resolves across XMI→JPA").isNotNull();
		assertThat(resolved.eIsProxy()).as("proxy should be resolved after access").isFalse();
		assertThat(resolved.eClass()).isEqualTo(classAEClass);
		assertThat(resolved.eGet(classAEClass.getEStructuralFeature("name"))).isEqualTo("A-in-JPA");
	}

	@Test
	void testLazyNonContainmentDefersTargetQuery() throws Exception {
		ClassDescriptor aDescriptor = serverSession.getDescriptorForAlias(classAEClass.getName());
		ClassDescriptor dDescriptor = serverSession.getDescriptorForAlias(classDEClass.getName());

		EObject a = (EObject) aDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject d = (EObject) dDescriptor.getInstantiationPolicy().buildNewInstance();
		a.eSet(classAEClass.getEStructuralFeature("name"), "A-lazy");
		d.eSet(classDEClass.getEStructuralFeature("name"), "D-lazy");
		a.eSet(classAEClass.getEStructuralFeature("dNonContainment"), d);

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(d);
			em.persist(a);
			em.getTransaction().commit();
		}
		String aId = EcoreUtil.getID(a);
		emf.getCache().evictAll();

		// Count EclipseLink ReadObject/ReadAll queries. Two deferrals are asserted:
		// (1) the resource itself is lazy — load() runs no query; the ClassAO2O ReadAll
		//     fires only when the contents are first iterated (issue #17), and
		// (2) the target-side (ClassDO2O) SELECT is deferred until eGet triggers proxy
		//     resolution.
		int[] queryCount = { 0 };
		SessionEventAdapter counter = new SessionEventAdapter() {
			@Override
			public void postExecuteQuery(SessionEvent event) {
				if (event.getQuery() instanceof ReadObjectQuery
						|| event.getQuery() instanceof ReadAllQuery) {
					queryCount[0]++;
				}
			}
		};
		serverSession.getEventManager().addListener(counter);
		try {
			ResourceSet rs = newMixedResourceSet();
			Resource aResource = rs.createResource(URI.createURI("jpa://xref/ClassAO2O"));
			rs.createResource(URI.createURI("jpa://xref/ClassDO2O"));

			aResource.load(null);
			assertThat(queryCount[0])
					.as("lazy resource: load() runs no query until contents are iterated (#17)")
					.isZero();

			EObject aLoaded = findById(aResource, aId);
			assertThat(aLoaded).isNotNull();
			int afterLoad = queryCount[0];
			assertThat(afterLoad)
					.as("iterating contents runs only the ClassAO2O query, no target query")
					.isEqualTo(1);

			EStructuralFeature dNonContainmentFeature = classAEClass.getEStructuralFeature("dNonContainment");
			EObject resolved = (EObject) aLoaded.eGet(dNonContainmentFeature);
			assertThat(resolved).isNotNull();
			assertThat(resolved.eIsProxy()).isFalse();
			assertThat(resolved.eGet(classDEClass.getEStructuralFeature("name"))).isEqualTo("D-lazy");

			int afterResolve = queryCount[0];
			assertThat(afterResolve - afterLoad)
					.as("target query fires only after eGet (lazy), not during load")
					.isGreaterThanOrEqualTo(1);

			// Second access must NOT issue another query — proxy is resolved.
			aLoaded.eGet(dNonContainmentFeature);
			assertThat(queryCount[0]).as("second eGet reuses the resolved object")
					.isEqualTo(afterResolve);
		} finally {
			serverSession.getEventManager().removeListener(counter);
		}
	}

	/**
	 * Build a ResourceSet that can handle both {@code jpa://} and XMI file URIs.
	 */
	private ResourceSet newMixedResourceSet() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getPackageRegistry().put(modelPackage.getNsURI(), modelPackage);
		rs.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("jpa", new JPAResourceFactory(emf));
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new XMIResourceFactoryImpl());
		return rs;
	}

	/**
	 * Returns the first EObject in the resource whose ID matches {@code expectedId},
	 * or {@code null}.
	 */
	private EObject findById(Resource resource, String expectedId) {
		for (EObject eo : resource.getContents()) {
			if (expectedId.equals(EcoreUtil.getID(eo))) {
				return eo;
			}
		}
		return null;
	}
}
