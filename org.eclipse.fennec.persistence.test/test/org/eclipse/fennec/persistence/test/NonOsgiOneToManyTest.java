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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
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
 * Non-OSGi port of {@code EPersistenceOneToManyTest}. Covers all OneToMany
 * permutations (containment uni/bi, non-containment uni/bi with/without
 * opposite) against in-memory H2 via {@link NonOsgiPersistenceTestBase}.
 */
class NonOsgiOneToManyTest extends NonOsgiPersistenceTestBase {

	private EPackage modelPackage;
	private EClass classAEClass;
	private EClass classBEClass;
	private EClass classCEClass;
	private EClass classDEClass;
	private EClass classEEClass;

	@BeforeEach
	void setUp() {
		modelPackage = loadEcore("data/model.ecore");
		classAEClass = (EClass) modelPackage.getEClassifier("ClassAO2M");
		classBEClass = (EClass) modelPackage.getEClassifier("ClassBO2M");
		classCEClass = (EClass) modelPackage.getEClassifier("ClassCO2M");
		classDEClass = (EClass) modelPackage.getEClassifier("ClassDO2M");
		classEEClass = (EClass) modelPackage.getEClassifier("ClassEO2M");
		assertNotNull(classAEClass);
		assertNotNull(classBEClass);
		assertNotNull(classCEClass);
		assertNotNull(classDEClass);
		assertNotNull(classEEClass);
		bootstrapPersistence("person",
				List.of(classAEClass, classBEClass, classCEClass, classDEClass, classEEClass));
	}

	@Test
	void testOneToManyContainmentUni() {
		ClassDescriptor classADescriptor = serverSession.getDescriptorForAlias(classAEClass.getName());
		ClassDescriptor classBDescriptor = serverSession.getDescriptorForAlias(classBEClass.getName());
		assertNotNull(classADescriptor);
		assertNotNull(classBDescriptor);

		EObject classAEO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classB01EO = (EObject) classBDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classB02EO = (EObject) classBDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classB03EO = (EObject) classBDescriptor.getInstantiationPolicy().buildNewInstance();

		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		EStructuralFeature bContainmentFeature = classAEClass.getEStructuralFeature("bContainment");
		EStructuralFeature bNameFeature = classBEClass.getEStructuralFeature("name");
		assertNotNull(aNameFeature);
		assertNotNull(bContainmentFeature);
		assertNotNull(bNameFeature);

		classAEO.eSet(aNameFeature, "The A-Class!");
		classB01EO.eSet(bNameFeature, "The First B-Class!");
		classB02EO.eSet(bNameFeature, "The Second B-Class!");
		classB03EO.eSet(bNameFeature, "The Third B-Class!");

		List<EObject> bContainmentList = List.of(classB01EO, classB02EO, classB03EO);
		classAEO.eSet(bContainmentFeature, bContainmentList);

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classAEO);
			em.getTransaction().commit();
			em.clear();
		} catch (Exception e) {
			fail("Fail test One-to-Many containment uni persist", e);
		}

		EObject findEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classAEO);
			findEO = em.find(classADescriptor.getJavaClass(), Integer.valueOf(id));
		} catch (Exception e) {
			fail("Fail test One-to-Many containment uni find", e);
		}

		assertNotNull(findEO);
		assertEquals("The A-Class!", findEO.eGet(aNameFeature));

		List<?> resultList = (List<?>) findEO.eGet(bContainmentFeature);
		assertNotNull(resultList);
		assertEquals(3, resultList.size());
		Set<String> values = new HashSet<>(Set.of("The First B-Class!", "The Second B-Class!", "The Third B-Class!"));
		for (Object o : resultList) {
			EObject eo = (EObject) o;
			assertEquals(classBEClass, eo.eClass());
			assertTrue(values.remove(eo.eGet(bNameFeature)));
			assertFalse(bContainmentList.contains(eo));
		}
		assertTrue(values.isEmpty());
	}

	@Test
	void testOneToManyContainmentBidi() {
		ClassDescriptor classADescriptor = serverSession.getDescriptorForAlias(classAEClass.getName());
		ClassDescriptor classCDescriptor = serverSession.getDescriptorForAlias(classCEClass.getName());
		assertNotNull(classADescriptor);
		assertNotNull(classCDescriptor);

		EObject classAEO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classC01EO = (EObject) classCDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classC02EO = (EObject) classCDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classC03EO = (EObject) classCDescriptor.getInstantiationPolicy().buildNewInstance();

		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		EStructuralFeature aCContainmentFeature = classAEClass.getEStructuralFeature("cContainmentBidi");
		EStructuralFeature cNameFeature = classCEClass.getEStructuralFeature("name");
		EStructuralFeature cClassAFeature = classCEClass.getEStructuralFeature("cClassA");
		assertNotNull(aNameFeature);
		assertNotNull(aCContainmentFeature);
		assertNotNull(cNameFeature);
		assertNotNull(cClassAFeature);

		classAEO.eSet(aNameFeature, "The A-Class!");
		classC01EO.eSet(cNameFeature, "The First C-Class!");
		classC02EO.eSet(cNameFeature, "The Second C-Class!");
		classC03EO.eSet(cNameFeature, "The Third C-Class!");

		List<EObject> cContainmentList = List.of(classC01EO, classC02EO, classC03EO);
		classAEO.eSet(aCContainmentFeature, cContainmentList);

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classAEO);
			em.getTransaction().commit();
			em.clear();
		} catch (Exception e) {
			fail("Fail test One-to-Many containment bidi persist", e);
		}

		EObject findAEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classAEO);
			findAEO = em.find(classADescriptor.getJavaClass(), Integer.valueOf(id));
		} catch (Exception e) {
			fail("Fail test One-to-Many containment bidi find A", e);
		}

		assertNotNull(findAEO);
		assertEquals("The A-Class!", findAEO.eGet(aNameFeature));
		List<?> resultList = (List<?>) findAEO.eGet(aCContainmentFeature);
		assertEquals(3, resultList.size());
		Set<String> values = new HashSet<>(Set.of("The First C-Class!", "The Second C-Class!", "The Third C-Class!"));
		for (Object o : resultList) {
			EObject eo = (EObject) o;
			assertTrue(values.remove(eo.eGet(cNameFeature)));
			assertEquals(findAEO, eo.eGet(cClassAFeature));
		}
		assertTrue(values.isEmpty());

		EObject findCEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classC02EO);
			findCEO = em.find(classCDescriptor.getJavaClass(), Integer.valueOf(id));
		} catch (Exception e) {
			fail("Fail test One-to-Many containment bidi find C", e);
		}

		assertNotNull(findCEO);
		assertEquals("The Second C-Class!", findCEO.eGet(cNameFeature));
		EObject classAFEO = (EObject) findCEO.eGet(cClassAFeature);
		assertNotNull(classAFEO);
		assertNotEquals(findAEO, classAFEO);
		List<?> containmentFromC = (List<?>) classAFEO.eGet(aCContainmentFeature);
		assertEquals(3, containmentFromC.size());
		assertTrue(containmentFromC.contains(findCEO));
	}

	@Test
	void testOneToManyNonContainmentUni() {
		ClassDescriptor classADescriptor = serverSession.getDescriptorForAlias(classAEClass.getName());
		ClassDescriptor classDDescriptor = serverSession.getDescriptorForAlias(classDEClass.getName());
		assertNotNull(classADescriptor);
		assertNotNull(classDDescriptor);

		EObject classA01EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classA02EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classD01EO = (EObject) classDDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classD02EO = (EObject) classDDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classD03EO = (EObject) classDDescriptor.getInstantiationPolicy().buildNewInstance();

		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		EStructuralFeature dNonContainmentFeature = classAEClass.getEStructuralFeature("dNonContainment");
		EStructuralFeature dNameFeature = classDEClass.getEStructuralFeature("name");
		EStructuralFeature dClassAFeature = classDEClass.getEStructuralFeature("dClassA");
		assertNotNull(aNameFeature);
		assertNotNull(dNonContainmentFeature);
		assertNotNull(dNameFeature);
		assertNotNull(dClassAFeature);

		classA01EO.eSet(aNameFeature, "The First A-Class!");
		classA02EO.eSet(aNameFeature, "The Second A-Class!");
		classD01EO.eSet(dNameFeature, "The First D-Class!");
		classD02EO.eSet(dNameFeature, "The Second D-Class!");
		classD03EO.eSet(dNameFeature, "The Third D-Class!");

		List<EObject> dList = List.of(classD01EO, classD02EO, classD03EO);
		classA01EO.eSet(dNonContainmentFeature, dList);
		classD01EO.eSet(dClassAFeature, classA01EO);
		classD02EO.eSet(dClassAFeature, classA01EO);
		classD03EO.eSet(dClassAFeature, classA02EO);

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classD01EO);
			em.persist(classD02EO);
			em.persist(classD03EO);
			em.persist(classA01EO);
			em.persist(classA02EO);
			em.getTransaction().commit();
			em.clear();
		} catch (Exception e) {
			fail("Fail test One-to-Many non-containment uni persist", e);
		}

		ResourceSet rs = newJpaResourceSet();
		EObject findAEO = findViaResource(rs, classAEClass.getName(), EcoreUtil.getID(classA01EO));

		assertNotNull(findAEO);
		assertEquals("The First A-Class!", findAEO.eGet(aNameFeature));
		List<?> resultList = (List<?>) findAEO.eGet(dNonContainmentFeature);
		assertEquals(3, resultList.size());
		Set<String> dValues = new HashSet<>(Set.of("The First D-Class!", "The Second D-Class!", "The Third D-Class!"));
		List<String> aValuesList = new ArrayList<>(List.of(
				"The First A-Class!", "The First A-Class!", "The Second A-Class!"));
		for (Object o : resultList) {
			EObject eo = (EObject) o;
			assertTrue(dValues.remove(eo.eGet(dNameFeature)));
			// D's from the IndirectList have no eResource; resolve the back-reference
			// explicitly through our ResourceSet so the proxy can route to the A-resource.
			EObject aeo = (EObject) EcoreUtil.resolve((EObject) eo.eGet(dClassAFeature), rs);
			Object aeoName = aeo.eGet(aNameFeature);
			aValuesList.remove(aeoName);
		}
		assertTrue(dValues.isEmpty());
		assertTrue(aValuesList.isEmpty());
	}

	@Test
	void testOneToManyNonContainmentBidiNoEOpposite() {
		ClassDescriptor classADescriptor = serverSession.getDescriptorForAlias(classAEClass.getName());
		ClassDescriptor classDDescriptor = serverSession.getDescriptorForAlias(classDEClass.getName());
		assertNotNull(classADescriptor);
		assertNotNull(classDDescriptor);

		EObject classA01EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classA02EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classD01EO = (EObject) classDDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classD02EO = (EObject) classDDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classD03EO = (EObject) classDDescriptor.getInstantiationPolicy().buildNewInstance();

		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		EStructuralFeature aDNonContainmentFeature = classAEClass.getEStructuralFeature("dNonContainment");
		EStructuralFeature dNameFeature = classDEClass.getEStructuralFeature("name");
		EStructuralFeature dClassAFeature = classDEClass.getEStructuralFeature("dClassA");

		classA01EO.eSet(aNameFeature, "The First A-Class!");
		classA02EO.eSet(aNameFeature, "The Second A-Class!");
		classD01EO.eSet(dNameFeature, "The First D-Class!");
		classD02EO.eSet(dNameFeature, "The Second D-Class!");
		classD03EO.eSet(dNameFeature, "The Third D-Class!");

		List<EObject> dList = List.of(classD01EO, classD02EO, classD03EO);
		classA01EO.eSet(aDNonContainmentFeature, dList);
		classD01EO.eSet(dClassAFeature, classA01EO);
		classD02EO.eSet(dClassAFeature, classA01EO);
		classD03EO.eSet(dClassAFeature, classA01EO);

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classD01EO);
			em.persist(classD02EO);
			em.persist(classD03EO);
			em.persist(classA01EO);
			em.persist(classA02EO);
			em.getTransaction().commit();
			em.clear();
		} catch (Exception e) {
			fail("Fail test One-to-Many non-containment bidi-no-opposite persist", e);
		}

		ResourceSet rs = newJpaResourceSet();
		EObject findAEO = findViaResource(rs, classAEClass.getName(), EcoreUtil.getID(classA01EO));
		EObject findDEO = findViaResource(rs, classDEClass.getName(), EcoreUtil.getID(classD03EO));

		assertNotNull(findAEO);
		assertEquals("The First A-Class!", findAEO.eGet(aNameFeature));
		List<?> dResult = (List<?>) findAEO.eGet(aDNonContainmentFeature);
		assertEquals(3, dResult.size());
		Set<String> dValues = new HashSet<>(Set.of("The First D-Class!", "The Second D-Class!", "The Third D-Class!"));
		for (Object o : dResult) {
			EObject eo = (EObject) o;
			assertTrue(dValues.remove(eo.eGet(dNameFeature)));
			EObject resolvedA = (EObject) EcoreUtil.resolve((EObject) eo.eGet(dClassAFeature), rs);
			assertEquals("The First A-Class!", resolvedA.eGet(aNameFeature));
		}
		assertTrue(dValues.isEmpty());

		assertNotNull(findDEO);
		assertEquals("The Third D-Class!", findDEO.eGet(dNameFeature));
		EObject backRefA = (EObject) findDEO.eGet(dClassAFeature);
		assertNotNull(backRefA);
		assertEquals("The First A-Class!", backRefA.eGet(aNameFeature));

		List<?> backRefList = (List<?>) backRefA.eGet(aDNonContainmentFeature);
		assertEquals(3, backRefList.size());
		Set<String> again = new HashSet<>(Set.of("The First D-Class!", "The Second D-Class!", "The Third D-Class!"));
		backRefList.forEach(o -> {
			EObject eo = (EObject) o;
			assertTrue(again.remove(eo.eGet(dNameFeature)));
		});
		assertTrue(again.isEmpty());
	}

	@Test
	void testOneToManyNonContainmentBidiEOpposite() {
		ClassDescriptor classADescriptor = serverSession.getDescriptorForAlias(classAEClass.getName());
		ClassDescriptor classEDescriptor = serverSession.getDescriptorForAlias(classEEClass.getName());
		assertNotNull(classADescriptor);
		assertNotNull(classEDescriptor);

		EObject classAEO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classE01EO = (EObject) classEDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classE02EO = (EObject) classEDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classE03EO = (EObject) classEDescriptor.getInstantiationPolicy().buildNewInstance();

		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		EStructuralFeature aEContainmentFeature = classAEClass.getEStructuralFeature("eNonContainmentBidi");
		EStructuralFeature eNameFeature = classEEClass.getEStructuralFeature("name");
		EStructuralFeature eClassAFeature = classEEClass.getEStructuralFeature("eClassA");

		classAEO.eSet(aNameFeature, "The A-Class!");
		classE01EO.eSet(eNameFeature, "The First E-Class!");
		classE02EO.eSet(eNameFeature, "The Second E-Class!");
		classE03EO.eSet(eNameFeature, "The Third E-Class!");

		List<EObject> eContainmentList = List.of(classE01EO, classE02EO, classE03EO);
		classAEO.eSet(aEContainmentFeature, eContainmentList);

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classE01EO);
			em.persist(classE02EO);
			em.persist(classE03EO);
			em.persist(classAEO);
			em.getTransaction().commit();
			em.clear();
		} catch (Exception e) {
			fail("Fail test One-to-Many non-containment bidi-opposite persist", e);
		}

		EObject findAEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classAEO);
			findAEO = em.find(classADescriptor.getJavaClass(), Integer.valueOf(id));
		} catch (Exception e) {
			fail("Fail test One-to-Many non-containment bidi-opposite find A", e);
		}

		assertNotNull(findAEO);
		assertEquals("The A-Class!", findAEO.eGet(aNameFeature));
		List<?> eContainmentResultList = (List<?>) findAEO.eGet(aEContainmentFeature);
		assertEquals(3, eContainmentResultList.size());
		Set<String> values = new HashSet<>(Set.of("The First E-Class!", "The Second E-Class!", "The Third E-Class!"));
		for (Object o : eContainmentResultList) {
			EObject eo = (EObject) o;
			assertEquals(classEEClass, eo.eClass());
			assertTrue(values.remove(eo.eGet(eNameFeature)));
			assertEquals(findAEO, eo.eGet(eClassAFeature));
		}
		assertTrue(values.isEmpty());

		EObject findEEO = findViaResource(classEEClass.getName(), EcoreUtil.getID(classE02EO));

		assertNotNull(findEEO);
		assertEquals("The Second E-Class!", findEEO.eGet(eNameFeature));
		// eClassA is a ManyToOne lazy proxy — resolve explicitly via the E-resource's RS.
		EObject classAFEO = (EObject) EcoreUtil.resolve(
				(EObject) findEEO.eGet(eClassAFeature), findEEO.eResource().getResourceSet());
		assertEquals("The A-Class!", classAFEO.eGet(aNameFeature));
		List<?> backList = (List<?>) classAFEO.eGet(aEContainmentFeature);
		assertEquals(3, backList.size());
		assertInstanceOf(EObject.class, backList.get(0));
	}
}
