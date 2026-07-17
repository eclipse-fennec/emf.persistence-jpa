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

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.persistence.Keywords;
import org.eclipse.fennec.persistence.eorm.CascadeType;
import org.eclipse.fennec.persistence.eorm.FetchType;
import org.eclipse.fennec.persistence.eorm.ManyToMany;
import org.eclipse.fennec.persistence.eorm.ManyToOne;
import org.eclipse.fennec.persistence.eorm.OneToMany;
import org.eclipse.fennec.persistence.eorm.OneToOne;
import org.eclipse.fennec.persistence.orm.MappingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for relationship processors: {@link OneToOneProcessor}, {@link OneToManyProcessor},
 * {@link ManyToOneProcessor}, {@link ManyToManyProcessor}
 */
public class ReferenceProcessorTest {

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
	class OneToOneTests {

		@Test
		void testContainmentOneToOne() {
			EClass parent = createClassWithId("Parent");
			EClass child = createClassWithId("Child");
			EReference ref = createReference("child", parent, child, false, true);

			OneToOneProcessor processor = new OneToOneProcessor(ref, context);
			processor.process();

			assertThat(processor.isProcessed()).isTrue();
			OneToOne result = processor.getMapping();
			assertThat(result).isNotNull();
			assertThat(result.getName()).isEqualTo("child");
			// containment → orphanRemoval=true (child is owned, removed when detached from parent)
			assertThat(result.isOrphanRemoval()).isTrue();
		}

		@Test
		void testNonContainmentOneToOne() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference ref = createReference("bRef", a, b, false, false);

			OneToOneProcessor processor = new OneToOneProcessor(ref, context);
			processor.process();

			assertThat(processor.isProcessed()).isTrue();
			OneToOne result = processor.getMapping();
			assertThat(result).isNotNull();
			// non-containment → orphanRemoval=false (referenced objects survive removal)
			assertThat(result.isOrphanRemoval()).isFalse();
		}

		@Test
		void testOneToOneJoinColumn() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference ref = createReference("bRef", a, b, false, false);

			OneToOneProcessor processor = new OneToOneProcessor(ref, context);
			processor.process();

			OneToOne result = processor.getMapping();
			assertThat(result.getJoinColumn()).isNotEmpty();
			assertThat(result.getForeignKey()).isNotNull();
		}

		@Test
		void testCannotProcessManyValued() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference ref = createReference("bs", a, b, true, false);

			OneToOneProcessor processor = new OneToOneProcessor(ref, context);
			processor.process();

			assertThat(processor.isProcessed()).isFalse();
		}

		@Test
		void testCannotProcessWithManyOpposite() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference refAB = createReference("b", a, b, false, false);
			EReference refBA = createReference("as", b, a, true, false);
			refAB.setEOpposite(refBA);
			refBA.setEOpposite(refAB);

			OneToOneProcessor processor = new OneToOneProcessor(refAB, context);
			processor.process();

			assertThat(processor.isProcessed()).isFalse();
		}

		@Test
		void testOptionalAttribute() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference ref = createReference("b", a, b, false, false);
			ref.setLowerBound(0); // not required

			OneToOneProcessor processor = new OneToOneProcessor(ref, context);
			processor.process();

			assertThat(processor.getMapping().isOptional()).isTrue();
		}

		@Test
		void testRequiredAttribute() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference ref = createReference("b", a, b, false, false);
			ref.setLowerBound(1); // required

			OneToOneProcessor processor = new OneToOneProcessor(ref, context);
			processor.process();

			assertThat(processor.getMapping().isOptional()).isFalse();
		}
	}

	@Nested
	class OneToManyTests {

		@Test
		void testContainmentOneToManyUsesJoinColumn() {
			EClass parent = createClassWithId("Parent");
			EClass child = createClassWithId("Child");
			EReference ref = createReference("children", parent, child, true, true);

			OneToManyProcessor processor = new OneToManyProcessor(ref, context);
			processor.process();

			assertThat(processor.isProcessed()).isTrue();
			OneToMany result = processor.getMapping();
			assertThat(result).isNotNull();
			assertThat(result.getJoinColumn()).isNotEmpty();
			assertThat(result.getJoinTable()).isNull();
			// containment → orphanRemoval=true (children are owned, deleted when removed from parent)
			assertThat(result.isOrphanRemoval()).isTrue();
		}

		@Test
		void testContainmentJoinColumnNaming() {
			EClass parent = createClassWithId("Parent");
			EClass child = createClassWithId("Child");
			EReference ref = createReference("children", parent, child, true, true);

			OneToManyProcessor processor = new OneToManyProcessor(ref, context);
			processor.process();

			OneToMany result = processor.getMapping();
			assertThat(result.getJoinColumn().get(0).getName()).isEqualTo("parent_id");
			assertThat(result.getJoinColumn().get(0).isNullable()).isFalse();
		}

		@Test
		void testNonContainmentOneToManyUsesJoinTable() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference ref = createReference("items", a, b, true, false);

			OneToManyProcessor processor = new OneToManyProcessor(ref, context);
			processor.process();

			assertThat(processor.isProcessed()).isTrue();
			OneToMany result = processor.getMapping();
			assertThat(result.getJoinTable()).isNotNull();
			assertThat(result.getJoinColumn()).isEmpty();
			// non-containment → orphanRemoval=false (referenced objects survive removal)
			assertThat(result.isOrphanRemoval()).isFalse();
		}

		@Test
		void testNonContainmentJoinTableNaming() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference ref = createReference("items", a, b, true, false);

			OneToManyProcessor processor = new OneToManyProcessor(ref, context);
			processor.process();

			OneToMany result = processor.getMapping();
			assertThat(result.getJoinTable().getName()).isEqualTo("A__B");
		}

		@Test
		void testCannotProcessSingleValued() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference ref = createReference("item", a, b, false, false);

			OneToManyProcessor processor = new OneToManyProcessor(ref, context);
			processor.process();

			assertThat(processor.isProcessed()).isFalse();
		}

		@Test
		void testCannotProcessWhenBothSidesMany() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference refAB = createReference("bs", a, b, true, false);
			EReference refBA = createReference("as", b, a, true, false);
			refAB.setEOpposite(refBA);
			refBA.setEOpposite(refAB);

			OneToManyProcessor processor = new OneToManyProcessor(refAB, context);
			processor.process();

			assertThat(processor.isProcessed()).isFalse();
		}
	}

	@Nested
	class ManyToOneTests {

		@Test
		void testManyToOneWithOpposite() {
			EClass parent = createClassWithId("Parent");
			EClass child = createClassWithId("Child");
			// parent has many children
			EReference parentToChild = createReference("children", parent, child, true, false);
			// child has single ref to parent
			EReference childToParent = createReference("parent", child, parent, false, false);
			parentToChild.setEOpposite(childToParent);
			childToParent.setEOpposite(parentToChild);

			ManyToOneProcessor processor = new ManyToOneProcessor(childToParent, context);
			processor.process();

			assertThat(processor.isProcessed()).isTrue();
			ManyToOne result = processor.getMapping();
			assertThat(result).isNotNull();
			assertThat(result.getName()).isEqualTo("parent");
			assertThat(result.getJoinColumn()).isNotEmpty();
			assertThat(result.getForeignKey()).isNotNull();
		}

		@Test
		void testManyToOneOptional() {
			EClass parent = createClassWithId("Parent");
			EClass child = createClassWithId("Child");
			EReference parentToChild = createReference("children", parent, child, true, false);
			EReference childToParent = createReference("parent", child, parent, false, false);
			childToParent.setLowerBound(0);
			parentToChild.setEOpposite(childToParent);
			childToParent.setEOpposite(parentToChild);

			ManyToOneProcessor processor = new ManyToOneProcessor(childToParent, context);
			processor.process();

			assertThat(processor.getMapping().isOptional()).isTrue();
		}

		@Test
		void testCannotProcessWithoutOpposite() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference ref = createReference("b", a, b, false, false);

			ManyToOneProcessor processor = new ManyToOneProcessor(ref, context);
			processor.process();

			assertThat(processor.isProcessed()).isFalse();
		}

		@Test
		void testCannotProcessWhenSelfIsMany() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference refAB = createReference("bs", a, b, true, false);
			EReference refBA = createReference("as", b, a, true, false);
			refAB.setEOpposite(refBA);
			refBA.setEOpposite(refAB);

			ManyToOneProcessor processor = new ManyToOneProcessor(refAB, context);
			processor.process();

			assertThat(processor.isProcessed()).isFalse();
		}

		@Test
		void testCannotProcessContainmentOnly() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference refAB = createReference("b", a, b, true, false);
			EReference refBA = createReference("a", b, a, false, false);
			refAB.setEOpposite(refBA);
			refBA.setEOpposite(refAB);

			ManyToOneProcessor processor = new ManyToOneProcessor(refBA, context);
			processor.withContaintmentOnly();
			processor.process();

			assertThat(processor.isProcessed()).isFalse();
		}
	}

	@Nested
	class ManyToManyTests {

		@Test
		void testManyToManyWithJoinTable() {
			EClass a = createClassWithId("Student");
			EClass b = createClassWithId("Course");
			EReference ref = createReference("courses", a, b, true, false);

			ManyToManyProcessor processor = new ManyToManyProcessor(ref, context);
			processor.process();

			assertThat(processor.isProcessed()).isTrue();
			ManyToMany result = processor.getMapping();
			assertThat(result).isNotNull();
			assertThat(result.getJoinTable()).isNotNull();
			assertThat(result.getJoinTable().getName()).isEqualTo("STUDENT__COURSE");
		}

		@Test
		void testManyToManyJoinTableColumns() {
			EClass a = createClassWithId("Student");
			EClass b = createClassWithId("Course");
			EReference ref = createReference("courses", a, b, true, false);

			ManyToManyProcessor processor = new ManyToManyProcessor(ref, context);
			processor.process();

			ManyToMany result = processor.getMapping();
			assertThat(result.getJoinTable().getJoinColumn()).isNotEmpty();
			assertThat(result.getJoinTable().getInverseJoinColumn()).isNotEmpty();
			assertThat(result.getJoinTable().getForeignKey()).isNotNull();
			assertThat(result.getJoinTable().getInverseForeignKey()).isNotNull();
		}

		@Test
		void testCannotProcessSingleValued() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference ref = createReference("b", a, b, false, false);

			ManyToManyProcessor processor = new ManyToManyProcessor(ref, context);
			processor.process();

			assertThat(processor.isProcessed()).isFalse();
		}

		@Test
		void testCannotProcessContainmentOnly() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference ref = createReference("bs", a, b, true, true);

			ManyToManyProcessor processor = new ManyToManyProcessor(ref, context);
			processor.withContaintmentOnly();
			processor.process();

			assertThat(processor.isProcessed()).isFalse();
		}

		@Test
		void testCannotProcessAlreadyMapped() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference ref = createReference("bs", a, b, true, false);

			// register a mapping first
			context.registerRefMapping(ref, context.createMapping(ref));

			ManyToManyProcessor processor = new ManyToManyProcessor(ref, context);
			processor.process();

			assertThat(processor.isProcessed()).isFalse();
		}
	}

	@Nested
	class CascadeTypeTests {

		@Test
		void testContainmentHasCascadeAll() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference ref = createReference("child", a, b, false, true);

			OneToOneProcessor processor = new OneToOneProcessor(ref, context);
			processor.process();

			CascadeType cascade = processor.getMapping().getCascade();
			assertThat(cascade).isNotNull();
			assertThat(cascade.getCascadeAll()).isNotNull();
		}

		@Test
		void testNonContainmentHasPersistDetachRefreshButNoRemove() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference ref = createReference("b", a, b, false, false);

			OneToOneProcessor processor = new OneToOneProcessor(ref, context);
			processor.process();

			CascadeType cascade = processor.getMapping().getCascade();
			assertThat(cascade).isNotNull();
			assertThat(cascade.getCascadeAll()).isNull();
			// Non-containment: persist, detach, refresh — but NO remove
			assertThat(cascade.getCascadePersist()).isNotNull();
			assertThat(cascade.getCascadeDetach()).isNotNull();
			assertThat(cascade.getCascadeRefresh()).isNotNull();
			// Referenced objects must survive parent deletion
			assertThat(cascade.getCascadeRemove()).isNull();
			assertThat(cascade.getCascadeMerge()).isNull();
		}

		@Test
		void testNonContainmentOneToManyHasNoCascadeRemove() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference ref = createReference("items", a, b, true, false);

			OneToManyProcessor processor = new OneToManyProcessor(ref, context);
			processor.process();

			CascadeType cascade = processor.getMapping().getCascade();
			assertThat(cascade).isNotNull();
			assertThat(cascade.getCascadeAll()).isNull();
			assertThat(cascade.getCascadePersist()).isNotNull();
			assertThat(cascade.getCascadeDetach()).isNotNull();
			assertThat(cascade.getCascadeRefresh()).isNotNull();
			assertThat(cascade.getCascadeRemove()).isNull();
		}

		@Test
		void testContainmentOneToManyCascadeAll() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference ref = createReference("children", a, b, true, true);

			OneToManyProcessor processor = new OneToManyProcessor(ref, context);
			processor.process();

			CascadeType cascade = processor.getMapping().getCascade();
			assertThat(cascade.getCascadeAll()).isNotNull();
		}
	}

	@Nested
	class FetchTests {

		@Test
		void testNonContainmentSingleDefaultsLazy() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference ref = createReference("bRef", a, b, false, false);

			OneToOneProcessor processor = new OneToOneProcessor(ref, context);
			processor.process();

			OneToOne result = processor.getMapping();
			assertThat(result.getFetch()).isEqualTo(FetchType.LAZY);
			assertThat(result.isBatch()).isFalse();
		}

		@Test
		void testContainmentSingleIsEager() {
			EClass parent = createClassWithId("Parent");
			EClass child = createClassWithId("Child");
			EReference ref = createReference("child", parent, child, false, true);

			OneToOneProcessor processor = new OneToOneProcessor(ref, context);
			processor.process();

			assertThat(processor.getMapping().getFetch()).isEqualTo(FetchType.EAGER);
		}

		@Test
		void testNonContainmentManyDefaultsLazy() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference ref = createReference("items", a, b, true, false);

			OneToManyProcessor processor = new OneToManyProcessor(ref, context);
			processor.process();

			assertThat(processor.getMapping().getFetch()).isEqualTo(FetchType.LAZY);
		}

		@Test
		void testContainmentManyIsEager() {
			EClass parent = createClassWithId("Parent");
			EClass child = createClassWithId("Child");
			EReference ref = createReference("children", parent, child, true, true);

			OneToManyProcessor processor = new OneToManyProcessor(ref, context);
			processor.process();

			assertThat(processor.getMapping().getFetch()).isEqualTo(FetchType.EAGER);
		}

		@Test
		void testAnnotationOverridesFetchToEager() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference ref = createReference("bRef", a, b, false, false);
			annotate(ref, "fetch", "EAGER");

			OneToOneProcessor processor = new OneToOneProcessor(ref, context);
			processor.process();

			assertThat(processor.getMapping().getFetch()).isEqualTo(FetchType.EAGER);
		}

		@Test
		void testAnnotationEnablesBatch() {
			EClass a = createClassWithId("A");
			EClass b = createClassWithId("B");
			EReference ref = createReference("items", a, b, true, false);
			annotate(ref, "batch", "true");

			OneToManyProcessor processor = new OneToManyProcessor(ref, context);
			processor.process();

			OneToMany result = processor.getMapping();
			assertThat(result.getFetch()).as("batch keeps the LAZY default").isEqualTo(FetchType.LAZY);
			assertThat(result.isBatch()).isTrue();
		}

		private void annotate(EReference ref, String key, String value) {
			EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
			annotation.setSource(Keywords.PERSISTENCE_ANNOTATION_SOURCE);
			annotation.getDetails().put(key, value);
			ref.getEAnnotations().add(annotation);
		}
	}

	// --- Helper methods ---

	private EClass createClassWithId(String name) {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(name);
		testPackage.getEClassifiers().add(eClass);

		EAttribute id = EcoreFactory.eINSTANCE.createEAttribute();
		id.setName("id");
		id.setEType(EcorePackage.Literals.ESTRING);
		id.setID(true);
		eClass.getEStructuralFeatures().add(id);

		// Register entity in context
		EntityProcessor ep = new EntityProcessor(eClass, context);
		ep.process();

		return eClass;
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
