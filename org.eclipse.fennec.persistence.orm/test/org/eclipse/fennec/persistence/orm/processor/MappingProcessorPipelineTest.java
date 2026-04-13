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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the full 5-stage {@link MappingProcessor} pipeline.
 */
public class MappingProcessorPipelineTest {

	private EPackage testPackage;

	@BeforeEach
	void setUp() {
		testPackage = EcoreFactory.eINSTANCE.createEPackage();
		testPackage.setName("testpkg");
		testPackage.setNsURI("http://test/pkg");
	}

	@Nested
	class SingleEntityTests {

		@Test
		void testSimpleClassWithAttributes() {
			EClass person = createEClass("Person");
			addAttribute(person, "name", EcorePackage.Literals.ESTRING);
			addAttribute(person, "age", EcorePackage.Literals.EINT);

			MappingProcessor processor = MappingProcessor.create(person);
			processor.process();

			assertThat(processor.isProcessed()).isTrue();
			EntityMappings em = processor.getTarget();
			assertThat(em.getEntity()).hasSize(1);

			Entity entity = em.getEntity().get(0);
			assertThat(entity.getName()).isEqualTo("Person");
			assertThat(entity.getTable().getName()).isEqualTo("PERSON");
			assertThat(entity.getAttributes().getBasic())
				.extracting("name")
				.contains("name", "age");
		}

		@Test
		void testAbstractClassIncludedWithInheritance() {
			EClass abstractClass = createEClass("Base");
			abstractClass.setAbstract(true);
			EClass concrete = createClassWithId("Concrete");
			concrete.getESuperTypes().add(abstractClass);

			MappingProcessor processor = MappingProcessor.create(List.of(abstractClass, concrete));
			processor.process();

			// Both abstract and concrete classes are now included
			assertThat(processor.getTarget().getEntity()).hasSize(2);
			Entity baseEntity = findEntity(processor.getTarget(), "Base");
			Entity concreteEntity = findEntity(processor.getTarget(), "Concrete");
			assertThat(baseEntity).isNotNull();
			assertThat(concreteEntity).isNotNull();
			// Root should have Inheritance + DiscriminatorColumn
			assertThat(baseEntity.getInheritance()).isNotNull();
			assertThat(baseEntity.getDiscriminatorColumn()).isNotNull();
			assertThat(baseEntity.getDiscriminatorColumn().getName()).isEqualTo("DTYPE");
			// Child should have DiscriminatorValue, no Inheritance
			assertThat(concreteEntity.getDiscriminatorValue()).isEqualTo("Concrete");
			assertThat(concreteEntity.getInheritance()).isNull();
		}

		@Test
		void testClassWithIdAttribute() {
			EClass entity = createEClass("Item");
			EAttribute id = addAttribute(entity, "itemId", EcorePackage.Literals.ESTRING);
			id.setID(true);

			MappingProcessor processor = MappingProcessor.create(entity);
			processor.process();

			Entity e = processor.getTarget().getEntity().get(0);
			assertThat(e.getAttributes().getId()).isNotEmpty();
			assertThat(e.getAttributes().getId().get(0).getName()).isEqualTo("itemId");
		}

		@Test
		void testClassWithoutIdGetsSyntheticId() {
			EClass entity = createEClass("NoId");
			addAttribute(entity, "name", EcorePackage.Literals.ESTRING);

			MappingProcessor processor = MappingProcessor.create(entity);
			processor.process();

			Entity e = processor.getTarget().getEntity().get(0);
			assertThat(e.getAttributes().getId()).isNotEmpty();
			// synthetic ID starts with "pk_"
			assertThat(e.getAttributes().getId().get(0).getName()).startsWith("pk_");
		}

		@Test
		void testMultiValuedAttributeBecomesElementCollection() {
			EClass entity = createEClass("Tagged");
			EAttribute tags = addAttribute(entity, "tags", EcorePackage.Literals.ESTRING);
			tags.setUpperBound(-1);

			MappingProcessor processor = MappingProcessor.create(entity);
			processor.process();

			Entity e = processor.getTarget().getEntity().get(0);
			assertThat(e.getAttributes().getElementCollection())
				.extracting("name")
				.contains("tags");
			// single-valued attributes should not include multi-valued ones
			assertThat(e.getAttributes().getBasic())
				.extracting("name")
				.doesNotContain("tags");
		}
	}

	@Nested
	class ContainmentReferenceTests {

		@Test
		void testOneToOneContainment() {
			EClass parent = createClassWithId("Parent");
			EClass child = createClassWithId("Child");
			addReference(parent, "child", child, false, true);

			MappingProcessor processor = MappingProcessor.create(List.of(parent, child));
			processor.process();

			Entity parentEntity = findEntity(processor.getTarget(), "Parent");
			assertThat(parentEntity.getAttributes().getOneToOne())
				.extracting("name")
				.contains("child");
		}

		@Test
		void testOneToManyContainment() {
			EClass parent = createClassWithId("Parent");
			EClass child = createClassWithId("Child");
			addReference(parent, "children", child, true, true);

			MappingProcessor processor = MappingProcessor.create(List.of(parent, child));
			processor.process();

			Entity parentEntity = findEntity(processor.getTarget(), "Parent");
			assertThat(parentEntity.getAttributes().getOneToMany())
				.extracting("name")
				.contains("children");
		}
	}

	@Nested
	class NonContainmentReferenceTests {

		@Test
		void testNonContainmentSingleValuedCreatesOneToOne() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			addReference(a, "b", b, false, false);

			MappingProcessor processor = MappingProcessor.create(List.of(a, b));
			processor.process();

			Entity entityA = findEntity(processor.getTarget(), "A");
			assertThat(entityA.getAttributes().getOneToOne())
				.extracting("name")
				.contains("b");
		}

		@Test
		void testNonContainmentMultiValuedCreatesOneToMany() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			addReference(a, "bs", b, true, false);

			MappingProcessor processor = MappingProcessor.create(List.of(a, b));
			processor.process();

			Entity entityA = findEntity(processor.getTarget(), "A");
			assertThat(entityA.getAttributes().getOneToMany())
				.extracting("name")
				.contains("bs");
		}
	}

	@Nested
	class BidirectionalReferenceTests {

		@Test
		void testBidirectionalContainmentCreatesOppositeMapping() {
			EClass parent = createClassWithId("Parent");
			EClass child = createClassWithId("Child");
			EReference parentToChild = addReference(parent, "children", child, true, true);
			EReference childToParent = addReference(child, "parent", parent, false, false);
			parentToChild.setEOpposite(childToParent);
			childToParent.setEOpposite(parentToChild);

			MappingProcessor processor = MappingProcessor.create(List.of(parent, child));
			processor.process();

			// Parent should have OneToMany
			Entity parentEntity = findEntity(processor.getTarget(), "Parent");
			assertThat(parentEntity.getAttributes().getOneToMany())
				.extracting("name")
				.contains("children");

			// Child should get a ManyToOne via opposite processing (Stage 5)
			Entity childEntity = findEntity(processor.getTarget(), "Child");
			assertThat(childEntity.getAttributes().getManyToOne())
				.extracting("name")
				.contains("parent");
		}

		@Test
		void testBidirectionalNonContainmentManyToMany() {
			EClass student = createClassWithId("Student");
			EClass course = createClassWithId("Course");
			EReference studentToCourse = addReference(student, "courses", course, true, false);
			EReference courseToStudent = addReference(course, "students", student, true, false);
			studentToCourse.setEOpposite(courseToStudent);
			courseToStudent.setEOpposite(studentToCourse);

			MappingProcessor processor = MappingProcessor.create(List.of(student, course));
			processor.process();

			// One side should have a ManyToMany with JoinTable
			EntityMappings em = processor.getTarget();
			long m2mCount = em.getEntity().stream()
				.flatMap(e -> e.getAttributes().getManyToMany().stream())
				.count();
			// At least one side has M2M mapping
			assertThat(m2mCount).isGreaterThanOrEqualTo(1);
		}
	}

	@Nested
	class TransientFeatureTests {

		@Test
		void testTransientAttributeExcluded() {
			EClass entity = createClassWithId("WithTransient");
			addAttribute(entity, "name", EcorePackage.Literals.ESTRING);
			EAttribute transientAttr = addAttribute(entity, "computed", EcorePackage.Literals.ESTRING);
			transientAttr.setTransient(true);

			MappingProcessor processor = MappingProcessor.create(entity);
			processor.process();

			Entity e = processor.getTarget().getEntity().get(0);
			assertThat(e.getAttributes().getBasic())
				.extracting("name")
				.contains("name")
				.doesNotContain("computed");
		}

		@Test
		void testTransientReferenceExcluded() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference ref = addReference(a, "temp", b, false, true);
			ref.setTransient(true);

			MappingProcessor processor = MappingProcessor.create(List.of(a, b));
			processor.process();

			Entity entityA = findEntity(processor.getTarget(), "A");
			assertThat(entityA.getAttributes().getOneToOne())
				.extracting("name")
				.doesNotContain("temp");
		}
	}

	@Nested
	class PackageMetadataTests {

		@Test
		void testEntityMappingsPackageInfo() {
			EClass entity = createEClass("Foo");

			MappingProcessor processor = MappingProcessor.create(entity);
			processor.process();

			EntityMappings em = processor.getTarget();
			assertThat(em.getName()).isEqualTo("testpkg");
			assertThat(em.getPackage()).isEqualTo("http://test/pkg");
			assertThat(em.getSchema()).isEqualTo("TESTPKG");
		}
	}

	@Nested
	class StrictModeTests {

		@Test
		void testStrictModeNoSequenceGenerator() {
			EClass entity = createEClass("StrictEntity");
			EAttribute id = addAttribute(entity, "myId", EcorePackage.Literals.ESTRING);
			id.setID(true);

			MappingProcessor processor = MappingProcessor.createStrict(List.of(entity));
			processor.process();

			Entity e = processor.getTarget().getEntity().get(0);
			assertThat(e.getAttributes().getId()).isNotEmpty();
			// strict mode: no sequence generator
			assertThat(e.getAttributes().getId().get(0).getSequenceGenerator()).isNull();
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
