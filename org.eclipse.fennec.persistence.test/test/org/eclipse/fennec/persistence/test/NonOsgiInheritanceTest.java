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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

/**
 * Non-OSGi port of {@code EPersistenceInheritanceTest}. Covers Vehicle (abstract)
 * → Car / Motorcycle SINGLE_TABLE mapping against in-memory H2.
 */
class NonOsgiInheritanceTest extends NonOsgiPersistenceTestBase {

	private EPackage modelPackage;
	private EClass vehicleEClass;
	private EClass carEClass;
	private EClass motorcycleEClass;

	@BeforeEach
	void setUp() {
		modelPackage = loadEcore("data/model.ecore");
		vehicleEClass = (EClass) modelPackage.getEClassifier("Vehicle");
		carEClass = (EClass) modelPackage.getEClassifier("Car");
		motorcycleEClass = (EClass) modelPackage.getEClassifier("Motorcycle");
		assertNotNull(vehicleEClass);
		assertTrue(vehicleEClass.isAbstract());
		assertNotNull(carEClass);
		assertNotNull(motorcycleEClass);
		bootstrapPersistence("person", List.of(vehicleEClass, carEClass, motorcycleEClass));
	}

	@Test
	void testInheritancePersistAndFind() {
		ClassDescriptor carDescriptor = serverSession.getDescriptorForAlias(carEClass.getName());
		ClassDescriptor motorcycleDescriptor = serverSession.getDescriptorForAlias(motorcycleEClass.getName());
		assertNotNull(carDescriptor, "Car descriptor should exist");
		assertNotNull(motorcycleDescriptor, "Motorcycle descriptor should exist");

		EObject carEO = (EObject) carDescriptor.getInstantiationPolicy().buildNewInstance();
		EStructuralFeature nameFeature = vehicleEClass.getEStructuralFeature("name");
		EStructuralFeature yearFeature = vehicleEClass.getEStructuralFeature("modelYear");
		EStructuralFeature doorsFeature = carEClass.getEStructuralFeature("doors");
		assertNotNull(nameFeature);
		assertNotNull(yearFeature);
		assertNotNull(doorsFeature);
		carEO.eSet(nameFeature, "Tesla Model 3");
		carEO.eSet(yearFeature, Integer.valueOf(2024));
		carEO.eSet(doorsFeature, 4);

		EObject motoEO = (EObject) motorcycleDescriptor.getInstantiationPolicy().buildNewInstance();
		EStructuralFeature ccFeature = motorcycleEClass.getEStructuralFeature("cc");
		assertNotNull(ccFeature);
		motoEO.eSet(nameFeature, "BMW R1250GS");
		motoEO.eSet(yearFeature, Integer.valueOf(2023));
		motoEO.eSet(ccFeature, 1254);

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(carEO);
			em.persist(motoEO);
			em.getTransaction().commit();
			em.clear();
		} catch (Exception e) {
			fail("Failed to persist inheritance entities", e);
		}

		EObject foundCar = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(carEO);
			foundCar = em.find(carDescriptor.getJavaClass(), Integer.valueOf(id));
		} catch (Exception e) {
			fail("Failed to find Car", e);
		}

		assertNotNull(foundCar);
		assertNotEquals(carEO, foundCar);
		assertEquals("Tesla Model 3", foundCar.eGet(nameFeature));
		assertEquals(2024, foundCar.eGet(yearFeature));
		assertEquals(4, foundCar.eGet(doorsFeature));
		assertEquals(carEClass, foundCar.eClass());

		EObject foundMoto = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(motoEO);
			foundMoto = em.find(motorcycleDescriptor.getJavaClass(), Integer.valueOf(id));
		} catch (Exception e) {
			fail("Failed to find Motorcycle", e);
		}

		assertNotNull(foundMoto);
		assertEquals("BMW R1250GS", foundMoto.eGet(nameFeature));
		assertEquals(1254, foundMoto.eGet(ccFeature));
		assertEquals(motorcycleEClass, foundMoto.eClass());
	}
}
