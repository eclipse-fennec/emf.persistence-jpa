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

import java.util.List;
import java.util.Set;

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
 * Shared roundtrip tests for the three JPA inheritance strategies
 * (SINGLE_TABLE / JOINED / TABLE_PER_CLASS). Subclasses pick the
 * concrete EClass names; the test logic here is identical since the
 * strategies must all yield the same externally-observable behaviour
 * for persist, find, polymorphic query, and attribute roundtrip.
 */
abstract class NonOsgiInheritanceRoundtripBase extends NonOsgiPersistenceTestBase {

	protected EClass vehicleEClass;
	protected EClass carEClass;
	protected EClass motorcycleEClass;

	protected abstract String vehicleClassName();
	protected abstract String carClassName();
	protected abstract String motorcycleClassName();

	@BeforeEach
	void setUpHierarchy() {
		EPackage modelPackage = loadEcore("data/model.ecore");
		vehicleEClass = (EClass) modelPackage.getEClassifier(vehicleClassName());
		carEClass = (EClass) modelPackage.getEClassifier(carClassName());
		motorcycleEClass = (EClass) modelPackage.getEClassifier(motorcycleClassName());
		assertNotNull(vehicleEClass, vehicleClassName() + " should exist");
		assertTrue(vehicleEClass.isAbstract(), vehicleClassName() + " must be abstract");
		assertNotNull(carEClass, carClassName() + " should exist");
		assertNotNull(motorcycleEClass, motorcycleClassName() + " should exist");
		bootstrapPersistence("vehicles", List.of(vehicleEClass, carEClass, motorcycleEClass));
	}

	private EObject newCar(int id, String name, int year, int doors) {
		ClassDescriptor d = serverSession.getDescriptorForAlias(carEClass.getName());
		EObject car = (EObject) d.getInstantiationPolicy().buildNewInstance();
		car.eSet(vehicleEClass.getEStructuralFeature("vehicleId"), id);
		car.eSet(vehicleEClass.getEStructuralFeature("name"), name);
		car.eSet(vehicleEClass.getEStructuralFeature("modelYear"), year);
		car.eSet(carEClass.getEStructuralFeature("doors"), doors);
		return car;
	}

	private EObject newMotorcycle(int id, String name, int year, int cc) {
		ClassDescriptor d = serverSession.getDescriptorForAlias(motorcycleEClass.getName());
		EObject moto = (EObject) d.getInstantiationPolicy().buildNewInstance();
		moto.eSet(vehicleEClass.getEStructuralFeature("vehicleId"), id);
		moto.eSet(vehicleEClass.getEStructuralFeature("name"), name);
		moto.eSet(vehicleEClass.getEStructuralFeature("modelYear"), year);
		moto.eSet(motorcycleEClass.getEStructuralFeature("cc"), cc);
		return moto;
	}

	private void persistAll(EObject... objects) {
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			for (EObject o : objects) {
				em.persist(o);
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			fail("Failed to persist vehicles", e);
		}
	}

	@Test
	void testPersistAndFindBySubclass() {
		EObject car = newCar(100, "Tesla Model 3", 2024, 4);
		EObject moto = newMotorcycle(200, "BMW R1250GS", 2023, 1254);
		persistAll(car, moto);

		ClassDescriptor carDesc = serverSession.getDescriptorForAlias(carEClass.getName());
		ClassDescriptor motoDesc = serverSession.getDescriptorForAlias(motorcycleEClass.getName());

		try (EntityManager em = emf.createEntityManager()) {
			EObject foundCar = em.find(carDesc.getJavaClass(), 100);
			assertNotNull(foundCar);
			assertEquals(carEClass, foundCar.eClass(), "Found object must be Car subtype");
			assertEquals("Tesla Model 3", foundCar.eGet(vehicleEClass.getEStructuralFeature("name")));
			assertEquals(2024, foundCar.eGet(vehicleEClass.getEStructuralFeature("modelYear")));
			assertEquals(4, foundCar.eGet(carEClass.getEStructuralFeature("doors")));

			EObject foundMoto = em.find(motoDesc.getJavaClass(), 200);
			assertNotNull(foundMoto);
			assertEquals(motorcycleEClass, foundMoto.eClass(), "Found object must be Motorcycle subtype");
			assertEquals("BMW R1250GS", foundMoto.eGet(vehicleEClass.getEStructuralFeature("name")));
			assertEquals(1254, foundMoto.eGet(motorcycleEClass.getEStructuralFeature("cc")));
		}
	}

	@Test
	void testFindViaAbstractBaseReturnsConcreteSubtype() {
		EObject car = newCar(300, "Volvo XC60", 2022, 5);
		EObject moto = newMotorcycle(400, "Ducati Panigale", 2024, 1100);
		persistAll(car, moto);

		ClassDescriptor vehicleDesc = serverSession.getDescriptorForAlias(vehicleEClass.getName());
		assertNotNull(vehicleDesc, "Abstract base must have a descriptor");

		try (EntityManager em = emf.createEntityManager()) {
			EObject foundAsVehicle = em.find(vehicleDesc.getJavaClass(), 300);
			assertNotNull(foundAsVehicle, "Find on abstract base must resolve");
			assertEquals(carEClass, foundAsVehicle.eClass(),
					"Find over abstract base must materialise the concrete subtype (Car)");
			assertEquals(5, foundAsVehicle.eGet(carEClass.getEStructuralFeature("doors")));

			EObject foundAsVehicle2 = em.find(vehicleDesc.getJavaClass(), 400);
			assertNotNull(foundAsVehicle2);
			assertEquals(motorcycleEClass, foundAsVehicle2.eClass(),
					"Find over abstract base must materialise the concrete subtype (Motorcycle)");
			assertEquals(1100, foundAsVehicle2.eGet(motorcycleEClass.getEStructuralFeature("cc")));
		}
	}

	@Test
	void testPolymorphicQueryOverBase() {
		persistAll(
				newCar(500, "Porsche 911", 2021, 2),
				newCar(501, "Skoda Octavia", 2020, 5),
				newMotorcycle(600, "Honda CBR", 2022, 600),
				newMotorcycle(601, "Yamaha MT-07", 2023, 689));

		ClassDescriptor vehicleDesc = serverSession.getDescriptorForAlias(vehicleEClass.getName());
		assertNotNull(vehicleDesc);

		try (EntityManager em = emf.createEntityManager()) {
			List<?> all = em.createQuery(
					"SELECT v FROM " + vehicleEClass.getName() + " v",
					vehicleDesc.getJavaClass()).getResultList();
			assertEquals(4, all.size(), "Polymorphic query must return all subtype instances");

			int cars = 0, motos = 0;
			for (Object o : all) {
				EObject eo = (EObject) o;
				if (eo.eClass() == carEClass) cars++;
				else if (eo.eClass() == motorcycleEClass) motos++;
				else fail("Unexpected eClass in polymorphic result: " + eo.eClass().getName());
			}
			assertEquals(2, cars);
			assertEquals(2, motos);
		}
	}

	@Test
	void testPolymorphicQueryWithWhereOnInheritedAttribute() {
		persistAll(
				newCar(700, "Car-2024", 2024, 4),
				newCar(701, "Car-2020", 2020, 5),
				newMotorcycle(800, "Moto-2024", 2024, 750),
				newMotorcycle(801, "Moto-2019", 2019, 500));

		ClassDescriptor vehicleDesc = serverSession.getDescriptorForAlias(vehicleEClass.getName());

		try (EntityManager em = emf.createEntityManager()) {
			List<?> recent = em.createQuery(
					"SELECT v FROM " + vehicleEClass.getName() + " v WHERE v.modelYear = 2024",
					vehicleDesc.getJavaClass()).getResultList();
			assertEquals(2, recent.size(), "Filter on inherited attribute must work across subtypes");
			EStructuralFeature nameFeature = vehicleEClass.getEStructuralFeature("name");
			Set<String> names = Set.of(
					(String) ((EObject) recent.get(0)).eGet(nameFeature),
					(String) ((EObject) recent.get(1)).eGet(nameFeature));
			assertEquals(Set.of("Car-2024", "Moto-2024"), names);
		}
	}

	@Test
	void testAttributeRoundtripAllFields() {
		EObject car = newCar(900, "Full Roundtrip Car", 2025, 3);
		EObject moto = newMotorcycle(901, "Full Roundtrip Moto", 2026, 999);
		persistAll(car, moto);

		ClassDescriptor carDesc = serverSession.getDescriptorForAlias(carEClass.getName());
		ClassDescriptor motoDesc = serverSession.getDescriptorForAlias(motorcycleEClass.getName());

		try (EntityManager em = emf.createEntityManager()) {
			EObject rbCar = em.find(carDesc.getJavaClass(), 900);
			assertEquals(EcoreUtil.getID(car), EcoreUtil.getID(rbCar));
			for (EStructuralFeature f : carEClass.getEAllStructuralFeatures()) {
				assertEquals(car.eGet(f), rbCar.eGet(f),
						"Attribute " + f.getName() + " must round-trip on Car");
			}

			EObject rbMoto = em.find(motoDesc.getJavaClass(), 901);
			for (EStructuralFeature f : motorcycleEClass.getEAllStructuralFeatures()) {
				assertEquals(moto.eGet(f), rbMoto.eGet(f),
						"Attribute " + f.getName() + " must round-trip on Motorcycle");
			}
		}
	}
}
