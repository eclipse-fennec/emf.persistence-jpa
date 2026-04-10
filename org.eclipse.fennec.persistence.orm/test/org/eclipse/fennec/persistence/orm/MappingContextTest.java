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
package org.eclipse.fennec.persistence.orm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.persistence.eorm.BaseRef;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.ManyToMany;
import org.eclipse.fennec.persistence.eorm.OneToMany;
import org.eclipse.fennec.persistence.eorm.OneToOne;
import org.eclipse.fennec.persistence.orm.MappingContext.MappedBy;
import org.eclipse.fennec.persistence.orm.MappingContext.MappingType;
import org.eclipse.fennec.persistence.orm.processor.EntityProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MappingContext}
 */
public class MappingContextTest {

	private MappingContext context;
	private EPackage testPackage;

	@BeforeEach
	void setUp() {
		context = new MappingContext();
		testPackage = EcoreFactory.eINSTANCE.createEPackage();
		testPackage.setName("test");
		testPackage.setNsURI("http://test");
	}

	@Nested
	class EntityMapTests {

		@Test
		void testPutAndGetEntity() {
			EClass eClass = createEClass("Person");
			Entity entity = createEntityForClass(eClass);

			context.putEntity(eClass, entity);

			assertThat(context.containsEntity(eClass)).isTrue();
			assertThat(context.getEntity(eClass)).isSameAs(entity);
		}

		@Test
		void testContainsEntityReturnsFalseForUnknown() {
			EClass eClass = createEClass("Unknown");
			assertThat(context.containsEntity(eClass)).isFalse();
			assertThat(context.getEntity(eClass)).isNull();
		}
	}

	@Nested
	class CreateMappingTests {

		@Test
		void testCreateMappingContainmentSingleValued() {
			EClass parent = createEClass("Parent");
			EClass child = createEClass("Child");
			EReference ref = createReference("child", parent, child, false, true);

			BaseRef mapping = context.createMapping(ref);

			assertThat(mapping).isInstanceOf(OneToOne.class);
			assertThat(mapping.getName()).isEqualTo("child");
		}

		@Test
		void testCreateMappingContainmentMultiValued() {
			EClass parent = createEClass("Parent");
			EClass child = createEClass("Child");
			EReference ref = createReference("children", parent, child, true, true);

			BaseRef mapping = context.createMapping(ref);

			assertThat(mapping).isInstanceOf(OneToMany.class);
		}

		@Test
		void testCreateMappingNonContainment() {
			EClass a = createEClass("A");
			EClass b = createEClass("B");
			EReference ref = createReference("items", a, b, true, false);

			BaseRef mapping = context.createMapping(ref);

			assertThat(mapping).isInstanceOf(ManyToMany.class);
		}

		@Test
		void testCreateMappingNullThrows() {
			assertThatThrownBy(() -> context.createMapping(null))
				.isInstanceOf(NullPointerException.class);
		}
	}

	@Nested
	class RegisterRefMappingTests {

		@Test
		void testRegisterNullReturnsNull() {
			EClass a = createEClass("A");
			EClass b = createEClass("B");
			EReference ref = createReference("ref", a, b, false, false);

			assertThat(context.registerRefMapping(ref, (BaseRef) null)).isNull();
			assertThat(context.registerRefMapping(null, context.createMapping(ref))).isNull();
		}

		@Test
		void testRegisterMappingAndContains() {
			EClass a = createEClass("A");
			EClass b = createEClass("B");
			EReference ref = createReference("ref", a, b, false, false);
			BaseRef mapping = context.createMapping(ref);

			context.registerRefMapping(ref, mapping);

			assertThat(context.containsMapping(ref)).isTrue();
			assertThat(context.<BaseRef>getMapping(ref)).isSameAs(mapping);
		}

		@Test
		void testOppositeTracking() {
			EClass a = createEClass("A");
			EClass b = createEClass("B");
			EReference refAB = createReference("refB", a, b, false, false);
			EReference refBA = createReference("refA", b, a, false, false);
			refAB.setEOpposite(refBA);
			refBA.setEOpposite(refAB);

			BaseRef mappingAB = context.createMapping(refAB);
			context.registerRefMapping(refAB, mappingAB);

			// opposite of refAB is refBA, so refBA should be registered as opposite
			assertThat(context.containsOpposite(refBA)).isTrue();
			assertThat(context.getOppositeReferences()).contains(refBA);
		}

		@Test
		void testMappedByCalculation() {
			EClass a = createEClass("A");
			EClass b = createEClass("B");
			EReference refAB = createReference("bRef", a, b, false, false);
			EReference refBA = createReference("aRef", b, a, false, false);
			refAB.setEOpposite(refBA);
			refBA.setEOpposite(refAB);

			BaseRef mappingAB = context.createMapping(refAB);
			context.registerRefMapping(refAB, mappingAB);

			MappedBy mappedBy = context.getMappedBy(refAB);
			assertThat(mappedBy).isNotNull();
			assertThat(mappedBy.mappingType).isEqualTo(MappingType.ONE_TO_ONE);
		}
	}

	@Nested
	class CalculateMappedByNameTests {

		@Test
		void testCalculateMappedByNameForRegisteredRef() {
			EClass a = createEClass("A");
			EClass b = createEClass("B");
			EReference ref = createReference("myRef", a, b, false, false);
			BaseRef mapping = context.createMapping(ref);
			context.registerRefMapping(ref, mapping);

			String name = context.calculateMappedByName(ref);
			assertThat(name).isEqualTo("myRef");
		}

		@Test
		void testCalculateMappedByNameForUnregisteredRef() {
			EClass a = createEClass("A");
			EClass b = createEClass("B");
			EReference ref = createReference("myRef", a, b, false, false);

			String name = context.calculateMappedByName(ref);
			assertThat(name).isNull();
		}

		@Test
		void testCalculateMappedByNameNullThrows() {
			assertThatThrownBy(() -> context.calculateMappedByName(null))
				.isInstanceOf(NullPointerException.class);
		}
	}

	@Nested
	class MappingTypeCalculationViaCreateMappingTests {

		@Test
		void testContainmentSingleIsOneToOne() {
			EClass a = createEClass("A");
			EClass b = createEClass("B");
			EReference ref = createReference("ref", a, b, false, true);

			BaseRef mapping = context.createMapping(ref);
			assertThat(mapping).isInstanceOf(OneToOne.class);
		}

		@Test
		void testContainmentManyIsOneToMany() {
			EClass a = createEClass("A");
			EClass b = createEClass("B");
			EReference ref = createReference("ref", a, b, true, true);

			BaseRef mapping = context.createMapping(ref);
			assertThat(mapping).isInstanceOf(OneToMany.class);
		}

		@Test
		void testNonContainmentIsManyToMany() {
			EClass a = createEClass("A");
			EClass b = createEClass("B");
			EReference ref = createReference("ref", a, b, true, false);

			BaseRef mapping = context.createMapping(ref);
			assertThat(mapping).isInstanceOf(ManyToMany.class);
		}
	}

	@Nested
	class MappingTypeWithOppositeTests {

		@Test
		void testNonContainmentBidiManyToMany() {
			EClass a = createEClass("A");
			EClass b = createEClass("B");
			EReference refAB = createReference("bs", a, b, true, false);
			EReference refBA = createReference("as", b, a, true, false);
			refAB.setEOpposite(refBA);
			refBA.setEOpposite(refAB);

			// Register first mapping
			BaseRef mappingAB = context.createMapping(refAB);
			context.registerRefMapping(refAB, mappingAB);

			MappedBy mb = context.getMappedBy(refAB);
			assertThat(mb).isNotNull();
			assertThat(mb.mappingType).isEqualTo(MappingType.MANY_TO_MANY);
		}

		@Test
		void testNonContainmentBidiSingleToSingleIsOneToOne() {
			EClass a = createEClass("A");
			EClass b = createEClass("B");
			EReference refAB = createReference("b", a, b, false, false);
			EReference refBA = createReference("a", b, a, false, false);
			refAB.setEOpposite(refBA);
			refBA.setEOpposite(refAB);

			BaseRef mappingBA = context.createMapping(refBA);
			context.registerRefMapping(refBA, mappingBA);
			// now refAB is the opposite side — calculating type for refBA
			MappedBy mb = context.getMappedBy(refBA);
			assertThat(mb).isNotNull();
			// opposite is non-containment single→single
			// getMappingType(refBA) = MANY_TO_MANY (non-containment default)
			// but calculateMappingType should refine: single-single → ONE_TO_ONE
			assertThat(mb.mappingType).isEqualTo(MappingType.ONE_TO_ONE);
		}

		@Test
		void testChildToParentContainmentManyIsM2O() {
			EClass parent = createEClass("Parent");
			EClass child = createEClass("Child");
			// parent contains many children
			EReference parentToChild = createReference("children", parent, child, true, true);
			// child back-ref to parent (non-containment, single)
			EReference childToParent = createReference("parent", child, parent, false, false);
			parentToChild.setEOpposite(childToParent);
			childToParent.setEOpposite(parentToChild);

			BaseRef mappingChild = context.createMapping(childToParent);
			context.registerRefMapping(childToParent, mappingChild);

			MappedBy mb = context.getMappedBy(childToParent);
			assertThat(mb).isNotNull();
			assertThat(mb.mappingType).isEqualTo(MappingType.MANY_TO_ONE);
		}
	}

	// --- Helper methods ---

	private EClass createEClass(String name) {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(name);
		testPackage.getEClassifiers().add(eClass);
		return eClass;
	}

	private Entity createEntityForClass(EClass eClass) {
		EntityProcessor processor = new EntityProcessor(eClass, context);
		processor.process();
		return processor.getTarget();
	}

	private EReference createReference(String name, EClass owner, EClass type, boolean many, boolean containment) {
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
}
