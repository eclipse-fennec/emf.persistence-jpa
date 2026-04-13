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
package org.eclipse.fennec.persistence.orm.processor;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.eorm.InheritanceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for EClass inheritance → JPA inheritance mapping in the processor pipeline.
 */
class InheritanceProcessorTest {

	private EPackage testPackage;

	@BeforeEach
	void setUp() {
		testPackage = EcoreFactory.eINSTANCE.createEPackage();
		testPackage.setName("testpkg");
		testPackage.setNsURI("http://test/pkg");
		testPackage.setNsPrefix("test");
	}

	@Nested
	class RootEntityTests {

		@Test
		void testAbstractRootWithSubclassGetsInheritance() {
			EClass vehicle = createClassWithId("Vehicle");
			vehicle.setAbstract(true);
			addAttribute(vehicle, "name", EcorePackage.Literals.ESTRING);

			EClass car = createEClass("Car");
			car.getESuperTypes().add(vehicle);
			addAttribute(car, "doors", EcorePackage.Literals.EINT);

			MappingProcessor processor = MappingProcessor.create(List.of(vehicle, car));
			processor.process();

			Entity vehicleEntity = findEntity(processor.getTarget(), "Vehicle");
			assertThat(vehicleEntity.getInheritance()).isNotNull();
			assertThat(vehicleEntity.getInheritance().getStrategy()).isEqualTo(InheritanceType.SINGLETABLE);
			assertThat(vehicleEntity.getDiscriminatorColumn()).isNotNull();
			assertThat(vehicleEntity.getDiscriminatorColumn().getName()).isEqualTo("DTYPE");
			assertThat(vehicleEntity.getDiscriminatorValue()).isEqualTo("Vehicle");
		}

		@Test
		void testConcreteRootWithSubclassGetsInheritance() {
			EClass animal = createClassWithId("Animal");
			addAttribute(animal, "name", EcorePackage.Literals.ESTRING);

			EClass dog = createEClass("Dog");
			dog.getESuperTypes().add(animal);
			addAttribute(dog, "breed", EcorePackage.Literals.ESTRING);

			MappingProcessor processor = MappingProcessor.create(List.of(animal, dog));
			processor.process();

			Entity animalEntity = findEntity(processor.getTarget(), "Animal");
			assertThat(animalEntity.getInheritance()).isNotNull();
			assertThat(animalEntity.getDiscriminatorValue()).isEqualTo("Animal");
		}

		@Test
		void testStandaloneClassNoInheritance() {
			EClass person = createClassWithId("Person");
			addAttribute(person, "name", EcorePackage.Literals.ESTRING);

			MappingProcessor processor = MappingProcessor.create(person);
			processor.process();

			Entity personEntity = findEntity(processor.getTarget(), "Person");
			assertThat(personEntity.getInheritance()).isNull();
			assertThat(personEntity.getDiscriminatorColumn()).isNull();
			assertThat(personEntity.getDiscriminatorValue()).isNull();
		}

		@Test
		void testAbstractWithoutSubclassNoInheritance() {
			EClass base = createClassWithId("Base");
			base.setAbstract(true);

			MappingProcessor processor = MappingProcessor.create(base);
			processor.process();

			Entity baseEntity = findEntity(processor.getTarget(), "Base");
			// No subclasses in the list → no inheritance config
			assertThat(baseEntity.getInheritance()).isNull();
		}
	}

	@Nested
	class ChildEntityTests {

		@Test
		void testChildHasDiscriminatorValue() {
			EClass vehicle = createClassWithId("Vehicle");
			vehicle.setAbstract(true);

			EClass car = createEClass("Car");
			car.getESuperTypes().add(vehicle);

			MappingProcessor processor = MappingProcessor.create(List.of(vehicle, car));
			processor.process();

			Entity carEntity = findEntity(processor.getTarget(), "Car");
			assertThat(carEntity.getDiscriminatorValue()).isEqualTo("Car");
			assertThat(carEntity.getInheritance()).isNull();
			assertThat(carEntity.getDiscriminatorColumn()).isNull();
		}

		@Test
		void testChildHasNoId() {
			EClass vehicle = createClassWithId("Vehicle");
			vehicle.setAbstract(true);

			EClass car = createEClass("Car");
			car.getESuperTypes().add(vehicle);
			addAttribute(car, "doors", EcorePackage.Literals.EINT);

			MappingProcessor processor = MappingProcessor.create(List.of(vehicle, car));
			processor.process();

			Entity carEntity = findEntity(processor.getTarget(), "Car");
			// ID is on root entity, not on child
			assertThat(carEntity.getAttributes().getId()).isEmpty();

			Entity vehicleEntity = findEntity(processor.getTarget(), "Vehicle");
			assertThat(vehicleEntity.getAttributes().getId()).isNotEmpty();
		}

		@Test
		void testChildOnlyLocalAttributes() {
			EClass vehicle = createClassWithId("Vehicle");
			vehicle.setAbstract(true);
			addAttribute(vehicle, "name", EcorePackage.Literals.ESTRING);
			addAttribute(vehicle, "year", EcorePackage.Literals.EINT);

			EClass car = createEClass("Car");
			car.getESuperTypes().add(vehicle);
			addAttribute(car, "doors", EcorePackage.Literals.EINT);

			MappingProcessor processor = MappingProcessor.create(List.of(vehicle, car));
			processor.process();

			// Root has its own attributes
			Entity vehicleEntity = findEntity(processor.getTarget(), "Vehicle");
			assertThat(vehicleEntity.getAttributes().getBasic())
				.extracting("name")
				.contains("name", "year");

			// Child has only LOCAL attributes, not inherited ones
			Entity carEntity = findEntity(processor.getTarget(), "Car");
			assertThat(carEntity.getAttributes().getBasic())
				.extracting("name")
				.contains("doors")
				.doesNotContain("name", "year");
		}

		@Test
		void testChildOnlyLocalReferences() {
			EClass vehicle = createClassWithId("Vehicle");
			vehicle.setAbstract(true);
			EClass owner = createClassWithId("Owner");
			addReference(vehicle, "owner", owner, false, false);

			EClass car = createEClass("Car");
			car.getESuperTypes().add(vehicle);
			EClass garage = createClassWithId("Garage");
			addReference(car, "garage", garage, false, false);

			MappingProcessor processor = MappingProcessor.create(List.of(vehicle, car, owner, garage));
			processor.process();

			// Root has its reference
			Entity vehicleEntity = findEntity(processor.getTarget(), "Vehicle");
			assertThat(vehicleEntity.getAttributes().getOneToOne())
				.extracting("name")
				.contains("owner");

			// Child has only local reference, not inherited
			Entity carEntity = findEntity(processor.getTarget(), "Car");
			assertThat(carEntity.getAttributes().getOneToOne())
				.extracting("name")
				.contains("garage")
				.doesNotContain("owner");
		}
	}

	@Nested
	class MultiLevelInheritanceTests {

		@Test
		void testThreeLevelHierarchy() {
			EClass vehicle = createClassWithId("Vehicle");
			vehicle.setAbstract(true);
			addAttribute(vehicle, "name", EcorePackage.Literals.ESTRING);

			EClass car = createEClass("Car");
			car.setAbstract(true);
			car.getESuperTypes().add(vehicle);
			addAttribute(car, "doors", EcorePackage.Literals.EINT);

			EClass sportsCar = createEClass("SportsCar");
			sportsCar.getESuperTypes().add(car);
			addAttribute(sportsCar, "hp", EcorePackage.Literals.EINT);

			MappingProcessor processor = MappingProcessor.create(List.of(vehicle, car, sportsCar));
			processor.process();

			assertThat(processor.getTarget().getEntity()).hasSize(3);

			// Root has Inheritance
			Entity vehicleEntity = findEntity(processor.getTarget(), "Vehicle");
			assertThat(vehicleEntity.getInheritance()).isNotNull();
			assertThat(vehicleEntity.getAttributes().getBasic()).extracting("name").contains("name");

			// Mid-level has DiscriminatorValue, only local attrs
			Entity carEntity = findEntity(processor.getTarget(), "Car");
			assertThat(carEntity.getDiscriminatorValue()).isEqualTo("Car");
			assertThat(carEntity.getInheritance()).isNull();
			assertThat(carEntity.getAttributes().getBasic())
				.extracting("name")
				.contains("doors")
				.doesNotContain("name");

			// Leaf has DiscriminatorValue, only local attrs
			Entity sportsCarEntity = findEntity(processor.getTarget(), "SportsCar");
			assertThat(sportsCarEntity.getDiscriminatorValue()).isEqualTo("SportsCar");
			assertThat(sportsCarEntity.getAttributes().getBasic())
				.extracting("name")
				.contains("hp")
				.doesNotContain("name", "doors");
		}
	}

	@Nested
	class MultipleSiblingsTests {

		@Test
		void testMultipleChildClasses() {
			EClass vehicle = createClassWithId("Vehicle");
			vehicle.setAbstract(true);
			addAttribute(vehicle, "name", EcorePackage.Literals.ESTRING);

			EClass car = createEClass("Car");
			car.getESuperTypes().add(vehicle);
			addAttribute(car, "doors", EcorePackage.Literals.EINT);

			EClass motorcycle = createEClass("Motorcycle");
			motorcycle.getESuperTypes().add(vehicle);
			addAttribute(motorcycle, "cc", EcorePackage.Literals.EINT);

			EClass truck = createEClass("Truck");
			truck.getESuperTypes().add(vehicle);
			addAttribute(truck, "payload", EcorePackage.Literals.EDOUBLE);

			MappingProcessor processor = MappingProcessor.create(List.of(vehicle, car, motorcycle, truck));
			processor.process();

			assertThat(processor.getTarget().getEntity()).hasSize(4);

			Entity vehicleEntity = findEntity(processor.getTarget(), "Vehicle");
			assertThat(vehicleEntity.getInheritance()).isNotNull();

			// Each child has its own discriminator value
			assertThat(findEntity(processor.getTarget(), "Car").getDiscriminatorValue()).isEqualTo("Car");
			assertThat(findEntity(processor.getTarget(), "Motorcycle").getDiscriminatorValue()).isEqualTo("Motorcycle");
			assertThat(findEntity(processor.getTarget(), "Truck").getDiscriminatorValue()).isEqualTo("Truck");
		}
	}

	@Nested
	class MixedHierarchyTests {

		@Test
		void testInheritanceAndStandaloneCoexist() {
			// Hierarchy
			EClass vehicle = createClassWithId("Vehicle");
			vehicle.setAbstract(true);
			EClass car = createEClass("Car");
			car.getESuperTypes().add(vehicle);

			// Standalone
			EClass person = createClassWithId("Person");
			addAttribute(person, "name", EcorePackage.Literals.ESTRING);

			MappingProcessor processor = MappingProcessor.create(List.of(vehicle, car, person));
			processor.process();

			assertThat(processor.getTarget().getEntity()).hasSize(3);

			// Vehicle has inheritance
			assertThat(findEntity(processor.getTarget(), "Vehicle").getInheritance()).isNotNull();
			// Person does not
			assertThat(findEntity(processor.getTarget(), "Person").getInheritance()).isNull();
			assertThat(findEntity(processor.getTarget(), "Person").getDiscriminatorValue()).isNull();
		}
	}

	// --- Helper methods ---

	private EClass createEClass(String name) {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(name);
		testPackage.getEClassifiers().add(eClass);
		return eClass;
	}

	private EClass createClassWithId(String name) {
		EClass eClass = createEClass(name);
		EAttribute id = EcoreFactory.eINSTANCE.createEAttribute();
		id.setName("id");
		id.setEType(EcorePackage.Literals.ESTRING);
		id.setID(true);
		eClass.getEStructuralFeatures().add(id);
		return eClass;
	}

	private EAttribute addAttribute(EClass owner, String name, org.eclipse.emf.ecore.EClassifier type) {
		EAttribute attr = EcoreFactory.eINSTANCE.createEAttribute();
		attr.setName(name);
		attr.setEType(type);
		owner.getEStructuralFeatures().add(attr);
		return attr;
	}

	private EReference addReference(EClass owner, String name, EClass type, boolean many, boolean containment) {
		EReference ref = EcoreFactory.eINSTANCE.createEReference();
		ref.setName(name);
		ref.setEType(type);
		ref.setContainment(containment);
		if (many) {
			ref.setUpperBound(-1);
		}
		owner.getEStructuralFeatures().add(ref);
		return ref;
	}

	private Entity findEntity(EntityMappings em, String name) {
		return em.getEntity().stream()
			.filter(e -> name.equals(e.getName()))
			.findFirst()
			.orElseThrow(() -> new AssertionError("Entity not found: " + name));
	}
}
