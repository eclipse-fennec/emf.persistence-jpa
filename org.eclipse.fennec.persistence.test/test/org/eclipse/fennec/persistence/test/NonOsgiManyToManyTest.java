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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

/**
 * Non-OSGi port of {@code EPersistenceManyToManyTest}. Covers M2M uni,
 * M2M no-opposite (explicit inverse set), and M2M with EOpposite.
 */
class NonOsgiManyToManyTest extends NonOsgiPersistenceTestBase {

	private EPackage modelPackage;
	private EClass classAEClass;
	private EClass classBEClass;
	private EClass classCEClass;

	@BeforeEach
	void setUp() {
		modelPackage = loadEcore("data/model.ecore");
		classAEClass = (EClass) modelPackage.getEClassifier("ClassAM2M");
		classBEClass = (EClass) modelPackage.getEClassifier("ClassBM2M");
		classCEClass = (EClass) modelPackage.getEClassifier("ClassCM2M");
		assertNotNull(classAEClass);
		assertNotNull(classBEClass);
		assertNotNull(classCEClass);
		bootstrapPersistence("person", List.of(classAEClass, classBEClass, classCEClass));
	}

	@Test
	void testManyToManyUni() {
		ClassDescriptor classADescriptor = serverSession.getDescriptorForAlias(classAEClass.getName());
		ClassDescriptor classBDescriptor = serverSession.getDescriptorForAlias(classBEClass.getName());

		EObject classA01EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classA02EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classA03EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classB01EO = (EObject) classBDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classB02EO = (EObject) classBDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classB03EO = (EObject) classBDescriptor.getInstantiationPolicy().buildNewInstance();

		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		EStructuralFeature aBNonContainmentFeature = classAEClass.getEStructuralFeature("bNonContainment");
		EStructuralFeature bNameFeature = classBEClass.getEStructuralFeature("name");
		EStructuralFeature bClassAFeature = classBEClass.getEStructuralFeature("bClassA");

		classA01EO.eSet(aNameFeature, "Emil Tester");
		classA02EO.eSet(aNameFeature, "Emilia Testerova");
		classA03EO.eSet(aNameFeature, "Amalia Toaster");
		classB01EO.eSet(bNameFeature, "First B");
		classB02EO.eSet(bNameFeature, "Second B");
		classB03EO.eSet(bNameFeature, "Third B");

		classA01EO.eSet(aBNonContainmentFeature, List.of(classB01EO, classB02EO));
		classA02EO.eSet(aBNonContainmentFeature, List.of(classB01EO, classB03EO));
		classA03EO.eSet(aBNonContainmentFeature, List.of(classB03EO));

		// Inverses not set on B — no EOpposite
		assertTrue(((Collection<?>) classB01EO.eGet(bClassAFeature)).isEmpty());
		assertTrue(((Collection<?>) classB02EO.eGet(bClassAFeature)).isEmpty());
		assertTrue(((Collection<?>) classB03EO.eGet(bClassAFeature)).isEmpty());

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classB01EO);
			em.persist(classB02EO);
			em.persist(classB03EO);
			em.persist(classA01EO);
			em.persist(classA02EO);
			em.persist(classA03EO);
			em.getTransaction().commit();
			em.clear();
		} catch (Exception e) {
			fail("Fail test Many-to-Many uni persist", e);
		}

		// AP-47: many-valued non-containment lists hold lazy EMF proxies — read back
		// through a ResourceSet so element access resolves them transparently.
		ResourceSet resourceSet = newJpaResourceSet();
		EObject findAEO = findViaResource(resourceSet, classAEClass.getName(), EcoreUtil.getID(classA01EO));
		assertNotNull(findAEO);
		assertEquals("Emil Tester", findAEO.eGet(aNameFeature));

		List<?> bResultList = (List<?>) findAEO.eGet(aBNonContainmentFeature);
		assertEquals(2, bResultList.size());
		Set<String> values = new HashSet<>(Set.of("First B", "Second B", "Third B"));
		for (Object o : bResultList) {
			assertTrue(values.remove(((EObject) o).eGet(bNameFeature)));
		}
		assertEquals(1, values.size());
		assertTrue(values.contains("Third B"));

		EObject findBEO = findViaResource(resourceSet, classBEClass.getName(), EcoreUtil.getID(classB03EO));
		assertNotNull(findBEO);
		assertEquals("Third B", findBEO.eGet(bNameFeature));
		List<?> aResultList = (List<?>) findBEO.eGet(bClassAFeature);
		assertTrue(aResultList.isEmpty());
	}

	@Test
	void testManyToManyNoEOpposite() {
		ClassDescriptor classADescriptor = serverSession.getDescriptorForAlias(classAEClass.getName());
		ClassDescriptor classBDescriptor = serverSession.getDescriptorForAlias(classBEClass.getName());

		EObject classA01EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classA02EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classA03EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classB01EO = (EObject) classBDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classB02EO = (EObject) classBDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classB03EO = (EObject) classBDescriptor.getInstantiationPolicy().buildNewInstance();

		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		EStructuralFeature aBNonContainmentFeature = classAEClass.getEStructuralFeature("bNonContainment");
		EStructuralFeature bNameFeature = classBEClass.getEStructuralFeature("name");
		EStructuralFeature bClassAFeature = classBEClass.getEStructuralFeature("bClassA");

		classA01EO.eSet(aNameFeature, "Emil Tester");
		classA02EO.eSet(aNameFeature, "Emilia Testerova");
		classA03EO.eSet(aNameFeature, "Amalia Toaster");
		classB01EO.eSet(bNameFeature, "First B");
		classB02EO.eSet(bNameFeature, "Second B");
		classB03EO.eSet(bNameFeature, "Third B");

		classA01EO.eSet(aBNonContainmentFeature, List.of(classB01EO, classB02EO));
		classA02EO.eSet(aBNonContainmentFeature, List.of(classB01EO, classB03EO));
		classA03EO.eSet(aBNonContainmentFeature, List.of(classB03EO));

		// Explicit inverse on B03 without an EOpposite
		classB03EO.eSet(bClassAFeature, List.of(classA02EO, classA03EO));

		assertTrue(((Collection<?>) classB01EO.eGet(bClassAFeature)).isEmpty());
		assertTrue(((Collection<?>) classB02EO.eGet(bClassAFeature)).isEmpty());
		assertEquals(2, ((Collection<?>) classB03EO.eGet(bClassAFeature)).size());

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classB01EO);
			em.persist(classB02EO);
			em.persist(classB03EO);
			em.persist(classA01EO);
			em.persist(classA02EO);
			em.persist(classA03EO);
			em.getTransaction().commit();
			em.clear();
		} catch (Exception e) {
			fail("Fail test Many-to-Many no-opposite persist", e);
		}

		// AP-47: read back via ResourceSet so the proxy elements resolve on access.
		ResourceSet resourceSet = newJpaResourceSet();
		EObject findBEO = findViaResource(resourceSet, classBEClass.getName(), EcoreUtil.getID(classB03EO));
		assertNotNull(findBEO);
		assertEquals("Third B", findBEO.eGet(bNameFeature));

		List<?> aResultList = (List<?>) findBEO.eGet(bClassAFeature);
		assertEquals(2, aResultList.size());
		Set<String> aValues = new HashSet<>(Set.of("Emilia Testerova", "Amalia Toaster"));
		for (Object o : aResultList) {
			assertTrue(aValues.remove(((EObject) o).eGet(aNameFeature)));
		}
		assertTrue(aValues.isEmpty());
	}

	@Test
	void testManyToManyEOpposite() {
		ClassDescriptor classADescriptor = serverSession.getDescriptorForAlias(classAEClass.getName());
		ClassDescriptor classCDescriptor = serverSession.getDescriptorForAlias(classCEClass.getName());

		EObject classA01EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classA02EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classA03EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classC01EO = (EObject) classCDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classC02EO = (EObject) classCDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classC03EO = (EObject) classCDescriptor.getInstantiationPolicy().buildNewInstance();

		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		EStructuralFeature aCNonContainmentFeature = classAEClass.getEStructuralFeature("cNonContainmentBidi");
		EStructuralFeature cNameFeature = classCEClass.getEStructuralFeature("name");
		EStructuralFeature cClassAFeature = classCEClass.getEStructuralFeature("cClassA");

		classA01EO.eSet(aNameFeature, "Emil Tester");
		classA02EO.eSet(aNameFeature, "Emilia Testerova");
		classA03EO.eSet(aNameFeature, "Amalia Toaster");
		classC01EO.eSet(cNameFeature, "First C");
		classC02EO.eSet(cNameFeature, "Second C");
		classC03EO.eSet(cNameFeature, "Third C");

		classA01EO.eSet(aCNonContainmentFeature, List.of(classC01EO, classC02EO));
		classA02EO.eSet(aCNonContainmentFeature, List.of(classC01EO, classC03EO));
		classA03EO.eSet(aCNonContainmentFeature, List.of(classC03EO));

		// EOpposite auto-populates the inverse
		assertEquals(2, ((Collection<?>) classC01EO.eGet(cClassAFeature)).size());
		assertEquals(1, ((Collection<?>) classC02EO.eGet(cClassAFeature)).size());
		assertEquals(2, ((Collection<?>) classC03EO.eGet(cClassAFeature)).size());

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classC01EO);
			em.persist(classC02EO);
			em.persist(classC03EO);
			em.persist(classA01EO);
			em.persist(classA02EO);
			em.persist(classA03EO);
			em.getTransaction().commit();
			em.clear();
		} catch (Exception e) {
			fail("Fail test Many-to-Many EOpposite persist", e);
		}

		// AP-47: read back via ResourceSet so the proxy elements resolve on access.
		ResourceSet resourceSet = newJpaResourceSet();
		String a01Id = EcoreUtil.getID(classA01EO);
		EObject findAEO = findViaResource(resourceSet, classAEClass.getName(), a01Id);
		assertNotNull(findAEO);
		assertEquals("Emil Tester", findAEO.eGet(aNameFeature));

		List<?> cResultList = (List<?>) findAEO.eGet(aCNonContainmentFeature);
		assertEquals(2, cResultList.size());
		Set<String> values = new HashSet<>(Set.of("First C", "Second C", "Third C"));
		for (Object o : cResultList) {
			EObject ceo = (EObject) o;
			assertTrue(values.remove(ceo.eGet(cNameFeature)));
			// Inverse side must point back to A01 — resolution may materialise a
			// separate instance, so compare by EMF id instead of object identity.
			List<?> cToA = (List<?>) ceo.eGet(cClassAFeature);
			assertTrue(cToA.stream().anyMatch(a -> a01Id.equals(EcoreUtil.getID((EObject) a))));
		}
		assertEquals(1, values.size());
		assertTrue(values.contains("Third C"));

		EObject findCEO = findViaResource(resourceSet, classCEClass.getName(), EcoreUtil.getID(classC01EO));
		assertNotNull(findCEO);
		assertEquals("First C", findCEO.eGet(cNameFeature));

		List<?> aResultList = (List<?>) findCEO.eGet(cClassAFeature);
		assertEquals(2, aResultList.size());
		Set<String> aValues = new HashSet<>(Set.of("Emil Tester", "Emilia Testerova"));
		for (Object o : aResultList) {
			assertTrue(aValues.remove(((EObject) o).eGet(aNameFeature)));
		}
		assertTrue(aValues.isEmpty());
	}
}
