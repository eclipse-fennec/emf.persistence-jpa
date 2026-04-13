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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.fennec.persistence.test.annotations.TestAnnotations;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.jupiter.api.Test;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * OSGi integration test for EClass inheritance → JPA SINGLE_TABLE inheritance.
 * Tests Vehicle (abstract) → Car, Motorcycle hierarchy with H2 database.
 */
public class EPersistenceInheritanceTest extends EPersistenceBase {

	protected EClass vehicleEClass;
	protected EClass carEClass;
	protected EClass motorcycleEClass;

	@Override
	protected EntityMappings setupMappings(EntityMapper mapper, EPackage ePackage) {
		vehicleEClass = (EClass) ePackage.getEClassifier("Vehicle");
		assertNotNull(vehicleEClass);
		assertTrue(vehicleEClass.isAbstract());
		carEClass = (EClass) ePackage.getEClassifier("Car");
		assertNotNull(carEClass);
		motorcycleEClass = (EClass) ePackage.getEClassifier("Motorcycle");
		assertNotNull(motorcycleEClass);
		EntityMappings mapping = mapper.createMappings(List.of(vehicleEClass, carEClass, motorcycleEClass));
		return mapping;
	}

	@Test
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testInheritancePersistAndFind(
			@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 7000) ServiceAware<EntityManagerFactory> emfAware)
			throws InterruptedException, IOException {

		assertFalse(modelPackageAware.isEmpty());
		assertFalse(dataSourceAware.isEmpty());
		assertFalse(emfAware.isEmpty());

		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);

		ClassDescriptor carDescriptor = server.getDescriptorForAlias(carEClass.getName());
		assertNotNull(carDescriptor, "Car descriptor should exist");
		ClassDescriptor motorcycleDescriptor = server.getDescriptorForAlias(motorcycleEClass.getName());
		assertNotNull(motorcycleDescriptor, "Motorcycle descriptor should exist");

		// Create Car — inherited features are accessed via the child EClass (getEStructuralFeature traverses supertypes)
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

		// Create Motorcycle — use vehicleEClass for inherited features
		EObject motoEO = (EObject) motorcycleDescriptor.getInstantiationPolicy().buildNewInstance();
		EStructuralFeature ccFeature = motorcycleEClass.getEStructuralFeature("cc");
		assertNotNull(ccFeature);
		motoEO.eSet(nameFeature, "BMW R1250GS");
		motoEO.eSet(yearFeature, Integer.valueOf(2023));
		motoEO.eSet(ccFeature, 1254);

		// Persist
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(carEO);
			em.persist(motoEO);
			em.getTransaction().commit();
			em.clear();
		} catch (Exception e) {
			fail("Failed to persist inheritance entities", e);
		}

		// Find Car
		EObject foundCar = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(carEO);
			assertNotNull(id);
			foundCar = em.find(carDescriptor.getJavaClass(), Integer.valueOf(id));
		} catch (Exception e) {
			fail("Failed to find Car", e);
		}

		assertNotNull(foundCar);
		assertNotEquals(carEO, foundCar);
		assertEquals("Tesla Model 3", foundCar.eGet(nameFeature));
		assertEquals(2024, foundCar.eGet(yearFeature));
		assertEquals(4, foundCar.eGet(doorsFeature));
		// Verify it's actually a Car (correct EClass)
		assertEquals(carEClass, foundCar.eClass());

		// Find Motorcycle
		EObject foundMoto = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(motoEO);
			assertNotNull(id);
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
