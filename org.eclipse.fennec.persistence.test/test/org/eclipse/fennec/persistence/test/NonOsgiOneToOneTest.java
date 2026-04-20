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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

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
 * Non-OSGi port of {@code EPersistenceOneToOneTest}. Exercises all OneToOne
 * permutations (containment uni/bi, non-containment uni/bi with/without
 * opposite) against in-memory H2 via {@link NonOsgiPersistenceTestBase}.
 */
class NonOsgiOneToOneTest extends NonOsgiPersistenceTestBase {

	private EPackage modelPackage;
	private EClass classAEClass;
	private EClass classBEClass;
	private EClass classCEClass;
	private EClass classDEClass;
	private EClass classEEClass;

	@BeforeEach
	void setUp() {
		modelPackage = loadEcore("data/model.ecore");
		classAEClass = (EClass) modelPackage.getEClassifier("ClassAO2O");
		classBEClass = (EClass) modelPackage.getEClassifier("ClassBO2O");
		classCEClass = (EClass) modelPackage.getEClassifier("ClassCO2O");
		classDEClass = (EClass) modelPackage.getEClassifier("ClassDO2O");
		classEEClass = (EClass) modelPackage.getEClassifier("ClassEO2O");
		assertNotNull(classAEClass);
		assertNotNull(classBEClass);
		assertNotNull(classCEClass);
		assertNotNull(classDEClass);
		assertNotNull(classEEClass);
		bootstrapPersistence("person",
				List.of(classAEClass, classBEClass, classCEClass, classDEClass, classEEClass));
	}

	@Test
	void testOneToOneContainmentUni() {
		ClassDescriptor classADescriptor = serverSession.getDescriptorForAlias(classAEClass.getName());
		ClassDescriptor classBDescriptor = serverSession.getDescriptorForAlias(classBEClass.getName());
		assertNotNull(classADescriptor);
		assertNotNull(classBDescriptor);

		EObject classAEO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classBEO = (EObject) classBDescriptor.getInstantiationPolicy().buildNewInstance();

		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		EStructuralFeature aBContainmentFeature = classAEClass.getEStructuralFeature("bContainment");
		EStructuralFeature bNameFeature = classBEClass.getEStructuralFeature("name");
		assertNotNull(aNameFeature);
		assertNotNull(aBContainmentFeature);
		assertNotNull(bNameFeature);

		classAEO.eSet(aNameFeature, "The A Class!");
		classBEO.eSet(bNameFeature, "The B Class!");
		classAEO.eSet(aBContainmentFeature, classBEO);

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classBEO);
			em.persist(classAEO);
			em.getTransaction().commit();
			em.clear();
		} catch (Exception e) {
			fail("Fail test One-to-One mapping persist", e);
		}

		EObject findEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classAEO);
			assertNotNull(id);
			findEO = em.find(classADescriptor.getJavaClass(), Integer.valueOf(id));
		} catch (Exception e) {
			fail("Fail test One-to-One mapping find", e);
		}

		assertNotNull(findEO);
		assertNotEquals(classAEO, findEO);
		assertEquals("The A Class!", findEO.eGet(aNameFeature));

		EObject classBFEO = (EObject) findEO.eGet(aBContainmentFeature);
		assertNotNull(classBFEO);
		assertEquals(classBEClass, classBFEO.eClass());
		assertEquals("The B Class!", classBFEO.eGet(bNameFeature));
	}

	@Test
	void testOneToOneContainmentBiDiEOpposite() {
		ClassDescriptor classADescriptor = serverSession.getDescriptorForAlias(classAEClass.getName());
		ClassDescriptor classCDescriptor = serverSession.getDescriptorForAlias(classCEClass.getName());
		assertNotNull(classADescriptor);
		assertNotNull(classCDescriptor);

		EObject classAEO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classCEO = (EObject) classCDescriptor.getInstantiationPolicy().buildNewInstance();

		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		EStructuralFeature aCContainmentFeature = classAEClass.getEStructuralFeature("cContainmentBidi");
		EStructuralFeature cNameFeature = classCEClass.getEStructuralFeature("name");
		EStructuralFeature cClassAFeature = classCEClass.getEStructuralFeature("cClassA");
		assertNotNull(aNameFeature);
		assertNotNull(aCContainmentFeature);
		assertNotNull(cNameFeature);
		assertNotNull(cClassAFeature);

		classAEO.eSet(aNameFeature, "The A Class!");
		classCEO.eSet(cNameFeature, "The C Class!");

		assertNull(classAEO.eGet(aCContainmentFeature));
		assertNull(classCEO.eGet(cClassAFeature));
		classAEO.eSet(aCContainmentFeature, classCEO);
		assertEquals(classAEO, classCEO.eGet(cClassAFeature));

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classCEO);
			em.persist(classAEO);
			em.getTransaction().commit();
			em.clear();
		} catch (Exception e) {
			fail("Fail test One-to-One mapping persist", e);
		}

		EObject findAEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classAEO);
			findAEO = em.find(classADescriptor.getJavaClass(), Integer.valueOf(id));
		} catch (Exception e) {
			fail("Fail test One-to-One containment bidi-mapping find A", e);
		}

		assertNotNull(findAEO);
		assertNotEquals(classAEO, findAEO);
		assertEquals("The A Class!", findAEO.eGet(aNameFeature));

		EObject classCFEO = (EObject) findAEO.eGet(aCContainmentFeature);
		assertNotNull(classCFEO);
		assertEquals(classCEClass, classCFEO.eClass());
		assertEquals("The C Class!", classCFEO.eGet(cNameFeature));
		assertEquals(findAEO, classCFEO.eGet(cClassAFeature));

		EObject findCEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classCEO);
			findCEO = em.find(classCDescriptor.getJavaClass(), Integer.valueOf(id));
		} catch (Exception e) {
			fail("Fail test One-to-One containment bidi-mapping find C", e);
		}
		assertNotNull(findCEO);
		assertEquals("The C Class!", findCEO.eGet(cNameFeature));
		EObject classAFEO = (EObject) findCEO.eGet(cClassAFeature);
		assertNotNull(classAFEO);
		assertEquals("The A Class!", classAFEO.eGet(aNameFeature));
		// different EMs — not identical references
		assertNotEquals(findAEO, classAFEO);
		assertEquals(findCEO, classAFEO.eGet(aCContainmentFeature));
	}

	@Test
	void testOneToOneNonContainmentUni() {
		ClassDescriptor classADescriptor = serverSession.getDescriptorForAlias(classAEClass.getName());
		ClassDescriptor classDDescriptor = serverSession.getDescriptorForAlias(classDEClass.getName());
		assertNotNull(classADescriptor);
		assertNotNull(classDDescriptor);

		EObject classAEO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classDEO = (EObject) classDDescriptor.getInstantiationPolicy().buildNewInstance();

		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		EStructuralFeature aDNonContainmentFeature = classAEClass.getEStructuralFeature("dNonContainment");
		EStructuralFeature dNameFeature = classDEClass.getEStructuralFeature("name");
		assertNotNull(aNameFeature);
		assertNotNull(aDNonContainmentFeature);
		assertNotNull(dNameFeature);

		classAEO.eSet(aNameFeature, "The A Class!");
		classDEO.eSet(dNameFeature, "The D Class!");
		classAEO.eSet(aDNonContainmentFeature, classDEO);

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classDEO);
			em.persist(classAEO);
			em.getTransaction().commit();
			em.clear();
		} catch (Exception e) {
			fail("Fail test One-to-One non-containment mapping persist", e);
		}

		// Non-containment refs are now lazy EMF proxies. Load via Resource so that
		// eGet can resolve the proxy through the ResourceSet.
		EObject findEO = findViaResource(classAEClass.getName(), EcoreUtil.getID(classAEO));

		assertNotNull(findEO);
		assertEquals("The A Class!", findEO.eGet(aNameFeature));
		EObject classDFEO = (EObject) findEO.eGet(aDNonContainmentFeature);
		assertNotNull(classDFEO);
		assertEquals(classDEClass, classDFEO.eClass());
		assertEquals("The D Class!", classDFEO.eGet(dNameFeature));
	}

	@Test
	void testOneToOneNonContainmentBiDiEOpposite() {
		ClassDescriptor classADescriptor = serverSession.getDescriptorForAlias(classAEClass.getName());
		ClassDescriptor classEDescriptor = serverSession.getDescriptorForAlias(classEEClass.getName());
		assertNotNull(classADescriptor);
		assertNotNull(classEDescriptor);

		EObject classAEO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classEEO = (EObject) classEDescriptor.getInstantiationPolicy().buildNewInstance();

		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		EStructuralFeature aENonContainmentFeature = classAEClass.getEStructuralFeature("eNonContainmentBidi");
		EStructuralFeature eNameFeature = classEEClass.getEStructuralFeature("name");
		EStructuralFeature eClassAFeature = classEEClass.getEStructuralFeature("eClassA");
		assertNotNull(aNameFeature);
		assertNotNull(aENonContainmentFeature);
		assertNotNull(eNameFeature);
		assertNotNull(eClassAFeature);

		classAEO.eSet(aNameFeature, "The A Class!");
		classEEO.eSet(eNameFeature, "The E Class!");

		assertNull(classEEO.eGet(eClassAFeature));
		classAEO.eSet(aENonContainmentFeature, classEEO);
		assertEquals(classAEO, classEEO.eGet(eClassAFeature));

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classEEO);
			em.persist(classAEO);
			em.getTransaction().commit();
			em.clear();
		} catch (Exception e) {
			fail("Fail test One-to-One non-containment mapping persist", e);
		}

		// Shared ResourceSet — proxy resolution of the bidi opposite resolves to the
		// same loaded EObject (cross-resource identity).
		ResourceSet rs = newJpaResourceSet();
		EObject findAEO = findViaResource(rs, classAEClass.getName(), EcoreUtil.getID(classAEO));
		EObject findEEO = findViaResource(rs, classEEClass.getName(), EcoreUtil.getID(classEEO));

		assertNotNull(findAEO);
		assertEquals("The A Class!", findAEO.eGet(aNameFeature));
		EObject classEFEO = (EObject) findAEO.eGet(aENonContainmentFeature);
		assertNotNull(classEFEO);
		assertEquals("The E Class!", classEFEO.eGet(eNameFeature));
		// Bidi opposite points back to A: compare by ID, not Java identity — each lazy
		// resolution may open a fresh EntityManager without shared cache.
		assertEquals(EcoreUtil.getID(findAEO), EcoreUtil.getID((EObject) classEFEO.eGet(eClassAFeature)));

		assertNotNull(findEEO);
		assertEquals("The E Class!", findEEO.eGet(eNameFeature));
		EObject classAFEO = (EObject) findEEO.eGet(eClassAFeature);
		assertNotNull(classAFEO);
		assertEquals("The A Class!", classAFEO.eGet(aNameFeature));
		assertEquals(EcoreUtil.getID(findEEO), EcoreUtil.getID((EObject) classAFEO.eGet(aENonContainmentFeature)));
	}

	@Test
	void testOneToOneNonContainmentBiDiNoEOpposite() {
		ClassDescriptor classADescriptor = serverSession.getDescriptorForAlias(classAEClass.getName());
		ClassDescriptor classDDescriptor = serverSession.getDescriptorForAlias(classDEClass.getName());
		assertNotNull(classADescriptor);
		assertNotNull(classDDescriptor);

		EObject classAEO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classDEO = (EObject) classDDescriptor.getInstantiationPolicy().buildNewInstance();

		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		EStructuralFeature aDNonContainmentFeature = classAEClass.getEStructuralFeature("dNonContainment");
		EStructuralFeature dNameFeature = classDEClass.getEStructuralFeature("name");
		EStructuralFeature dClassAFeature = classDEClass.getEStructuralFeature("dClassA");
		assertNotNull(aNameFeature);
		assertNotNull(aDNonContainmentFeature);
		assertNotNull(dNameFeature);
		assertNotNull(dClassAFeature);

		classAEO.eSet(aNameFeature, "The A Class!");
		classDEO.eSet(dNameFeature, "The D Class!");
		assertNull(classDEO.eGet(dClassAFeature));

		classAEO.eSet(aDNonContainmentFeature, classDEO);
		classDEO.eSet(dClassAFeature, classAEO);

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classDEO);
			em.persist(classAEO);
			em.getTransaction().commit();
			em.clear();
		} catch (Exception e) {
			fail("Fail test One-to-One non-containment mapping persist", e);
		}

		ResourceSet rs = newJpaResourceSet();
		EObject findAEO = findViaResource(rs, classAEClass.getName(), EcoreUtil.getID(classAEO));
		EObject findDEO = findViaResource(rs, classDEClass.getName(), EcoreUtil.getID(classDEO));

		assertNotNull(findAEO);
		assertEquals("The A Class!", findAEO.eGet(aNameFeature));
		EObject classDFEO = (EObject) findAEO.eGet(aDNonContainmentFeature);
		assertNotNull(classDFEO);
		assertEquals("The D Class!", classDFEO.eGet(dNameFeature));
		Object dClassAObj = classDFEO.eGet(dClassAFeature);
		assertInstanceOf(EObject.class, dClassAObj);
		assertEquals(EcoreUtil.getID(findAEO), EcoreUtil.getID((EObject) dClassAObj));

		assertNotNull(findDEO);
		assertEquals("The D Class!", findDEO.eGet(dNameFeature));
		EObject classAFEO = (EObject) findDEO.eGet(dClassAFeature);
		assertNotNull(classAFEO);
		assertEquals("The A Class!", classAFEO.eGet(aNameFeature));
		assertEquals(EcoreUtil.getID(findDEO), EcoreUtil.getID((EObject) classAFEO.eGet(aDNonContainmentFeature)));
	}
}
