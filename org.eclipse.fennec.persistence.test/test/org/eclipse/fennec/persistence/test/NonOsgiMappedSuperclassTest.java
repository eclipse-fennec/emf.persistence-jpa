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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

/**
 * Roundtrip tests for the MappedSuperclass strategy —
 * VehicleMs (abstract, @MappedSuperclass) → CarMs / MotorcycleMs.
 * Each concrete subclass is an independent entity with its own table; the
 * abstract base has no EclipseLink descriptor on purpose. Polymorphic
 * queries over the base are explicitly not supported — the two polymorphic
 * tests inherited from the roundtrip base are overridden here to document
 * that limitation and to demonstrate the filtering-by-inherited-attribute
 * per concrete subclass works.
 */
class NonOsgiMappedSuperclassTest extends NonOsgiInheritanceRoundtripBase {

	@Override
	protected String vehicleClassName() {
		return "VehicleMs";
	}

	@Override
	protected String carClassName() {
		return "CarMs";
	}

	@Override
	protected String motorcycleClassName() {
		return "MotorcycleMs";
	}

	@Override
	@Test
	@DisplayName("MappedSuperclass: no descriptor for abstract base — em.find(Base.class, id) is not supported")
	void testFindViaAbstractBaseReturnsConcreteSubtype() {
		ClassDescriptor baseDesc = serverSession.getDescriptorForAlias(vehicleEClass.getName());
		assertNull(baseDesc, "MappedSuperclass base must not have its own EclipseLink descriptor");
	}

	@Override
	@Test
	@DisplayName("MappedSuperclass: no descriptor for abstract base — polymorphic JPQL over base is not supported")
	void testPolymorphicQueryOverBase() {
		ClassDescriptor baseDesc = serverSession.getDescriptorForAlias(vehicleEClass.getName());
		assertNull(baseDesc, "MappedSuperclass base must not have its own EclipseLink descriptor");
	}

	@Override
	@Test
	@DisplayName("MappedSuperclass: filtering on inherited attributes works per concrete subclass")
	void testPolymorphicQueryWithWhereOnInheritedAttribute() {
		persistAll(
				newCar(700, "Car-2024", 2024, 4),
				newCar(701, "Car-2020", 2020, 5),
				newMotorcycle(800, "Moto-2024", 2024, 750),
				newMotorcycle(801, "Moto-2019", 2019, 500));

		ClassDescriptor carDesc = serverSession.getDescriptorForAlias(carEClass.getName());
		ClassDescriptor motoDesc = serverSession.getDescriptorForAlias(motorcycleEClass.getName());
		EStructuralFeature nameFeature = carEClass.getEStructuralFeature("name");

		try (EntityManager em = emf.createEntityManager()) {
			List<?> recentCars = em.createQuery(
					"SELECT c FROM " + carEClass.getName() + " c WHERE c.modelYear = 2024",
					carDesc.getJavaClass()).getResultList();
			assertEquals(1, recentCars.size(), "one 2024 car expected");
			assertEquals("Car-2024", ((EObject) recentCars.get(0)).eGet(nameFeature));

			List<?> recentMotos = em.createQuery(
					"SELECT m FROM " + motorcycleEClass.getName() + " m WHERE m.modelYear = 2024",
					motoDesc.getJavaClass()).getResultList();
			assertEquals(1, recentMotos.size(), "one 2024 motorcycle expected");
			assertEquals("Moto-2024", ((EObject) recentMotos.get(0)).eGet(nameFeature));

			// Inherited attributes really live on each subclass table, so
			// filtering happens on CARMS / MOTORCYCLEMS respectively — not on a
			// shared VEHICLEMS table (which does not exist).
			assertThat(carDesc.getTables()).singleElement().matches(
					t -> "CARMS".equalsIgnoreCase(t.getName()));
			assertThat(motoDesc.getTables()).singleElement().matches(
					t -> "MOTORCYCLEMS".equalsIgnoreCase(t.getName()));
		}
	}

	// Visible shims so the overridden polymorphic test can reuse the roundtrip
	// base's factories (which are private to the base package).
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
		}
	}
}
