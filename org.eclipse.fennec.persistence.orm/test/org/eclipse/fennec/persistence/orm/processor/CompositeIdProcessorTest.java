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

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.persistence.eorm.GenerationType;
import org.eclipse.fennec.persistence.eorm.Id;
import org.eclipse.fennec.persistence.orm.MappingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CompositeIdProcessor}
 */
public class CompositeIdProcessorTest {

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
	class SingleIdTests {

		@Test
		void testSingleStringId() {
			EClass eClass = createEClass("Person");
			addIdAttribute(eClass, "id", EcorePackage.Literals.ESTRING);

			CompositeIdProcessor processor = new CompositeIdProcessor(false);
			List<Id> ids = processor.createIds(eClass, context);

			assertThat(ids).hasSize(1);
			assertThat(ids.get(0).getName()).isEqualTo("id");
		}

		@Test
		void testSingleStringIdHasUuidGeneration() {
			EClass eClass = createEClass("Person");
			addIdAttribute(eClass, "id", EcorePackage.Literals.ESTRING);

			CompositeIdProcessor processor = new CompositeIdProcessor(false);
			List<Id> ids = processor.createIds(eClass, context);

			assertThat(ids.get(0).getGeneratedValue()).isNotNull();
			assertThat(ids.get(0).getGeneratedValue().getStrategy()).isEqualTo(GenerationType.UUID);
		}

		@Test
		void testSingleLongIdHasSequenceGenerator() {
			EClass eClass = createEClass("Item");
			addIdAttribute(eClass, "itemId", EcorePackage.Literals.ELONG_OBJECT);

			CompositeIdProcessor processor = new CompositeIdProcessor(false);
			List<Id> ids = processor.createIds(eClass, context);

			assertThat(ids.get(0).getSequenceGenerator()).isNotNull();
			assertThat(ids.get(0).getSequenceGenerator().getName()).contains("ITEMID");
		}

		@Test
		void testSingleIdStrictModeNoGeneration() {
			EClass eClass = createEClass("Strict");
			addIdAttribute(eClass, "id", EcorePackage.Literals.ESTRING);

			CompositeIdProcessor processor = new CompositeIdProcessor(true);
			List<Id> ids = processor.createIds(eClass, context);

			assertThat(ids).hasSize(1);
			assertThat(ids.get(0).getGeneratedValue()).isNull();
			assertThat(ids.get(0).getSequenceGenerator()).isNull();
		}
	}

	@Nested
	class EmbeddedIdTests {

		@Test
		void testTwoIdAttributes() {
			EClass eClass = createEClass("MultiPK");
			addIdAttribute(eClass, "id", EcorePackage.Literals.ESTRING);
			addIdAttribute(eClass, "timestamp", EcorePackage.Literals.ELONG);

			CompositeIdProcessor processor = new CompositeIdProcessor(false);
			List<Id> ids = processor.createIds(eClass, context);

			assertThat(ids).hasSize(2);
			assertThat(ids).extracting("name").contains("id", "timestamp");
		}

		@Test
		void testThreeIdAttributes() {
			EClass eClass = createEClass("TriplePK");
			addIdAttribute(eClass, "a", EcorePackage.Literals.ESTRING);
			addIdAttribute(eClass, "b", EcorePackage.Literals.EINT);
			addIdAttribute(eClass, "c", EcorePackage.Literals.ELONG);

			CompositeIdProcessor processor = new CompositeIdProcessor(false);
			List<Id> ids = processor.createIds(eClass, context);

			assertThat(ids).hasSize(3);
		}

		@Test
		void testEmbeddedIdStrictModeNoGeneration() {
			EClass eClass = createEClass("StrictMulti");
			addIdAttribute(eClass, "id", EcorePackage.Literals.ESTRING);
			addIdAttribute(eClass, "ts", EcorePackage.Literals.ELONG);

			CompositeIdProcessor processor = new CompositeIdProcessor(true);
			List<Id> ids = processor.createIds(eClass, context);

			assertThat(ids).hasSize(2);
			for (Id id : ids) {
				assertThat(id.getGeneratedValue()).isNull();
				assertThat(id.getSequenceGenerator()).isNull();
			}
		}

		@Test
		void testEmbeddedIdComponentsGetNoDefaultGeneration() {
			// issue #111: generating the halves of a composite key independently is
			// not a meaningful identity — composite keys are natural/assigned keys,
			// generation must be an explicit eorm declaration
			EClass eClass = createEClass("MixedPK");
			addIdAttribute(eClass, "strId", EcorePackage.Literals.ESTRING);
			addIdAttribute(eClass, "numId", EcorePackage.Literals.ELONG_OBJECT);

			CompositeIdProcessor processor = new CompositeIdProcessor(false);
			List<Id> ids = processor.createIds(eClass, context);

			assertThat(ids).hasSize(2);
			for (Id id : ids) {
				assertThat(id.getGeneratedValue()).as("no default generation on '%s'", id.getName()).isNull();
				assertThat(id.getSequenceGenerator()).as("no default sequence on '%s'", id.getName()).isNull();
			}
		}
	}

	@Nested
	class IdClassTests {

		@Test
		void testIdClassFromReferenceAnnotation() {
			EClass keyClass = createEClass("CompositeKey");
			addIdAttribute(keyClass, "part1", EcorePackage.Literals.ESTRING);
			addIdAttribute(keyClass, "part2", EcorePackage.Literals.ELONG);

			EClass eClass = createEClass("WithIdClass");
			EReference ref = createContainmentReference(eClass, "key", keyClass);
			addExtendedMetadataIdAnnotation(ref);

			CompositeIdProcessor processor = new CompositeIdProcessor(false);
			List<Id> ids = processor.createIds(eClass, context);

			assertThat(ids).hasSize(2);
			// Names are flattened: reference_name + "_" + attribute_name
			assertThat(ids).extracting("name").contains("key_part1", "key_part2");
		}

		@Test
		void testIdClassColumnNaming() {
			EClass keyClass = createEClass("Key");
			addIdAttribute(keyClass, "field1", EcorePackage.Literals.ESTRING);

			EClass eClass = createEClass("Owner");
			EReference ref = createContainmentReference(eClass, "pk", keyClass);
			addExtendedMetadataIdAnnotation(ref);

			CompositeIdProcessor processor = new CompositeIdProcessor(false);
			List<Id> ids = processor.createIds(eClass, context);

			assertThat(ids.get(0).getColumn()).isNotNull();
			assertThat(ids.get(0).getColumn().getName()).isEqualTo("pk_field1");
			assertThat(ids.get(0).getColumn().isNullable()).isFalse();
		}

		@Test
		void testIdClassStrictMode() {
			EClass keyClass = createEClass("StrictKey");
			addIdAttribute(keyClass, "k", EcorePackage.Literals.ESTRING);

			EClass eClass = createEClass("StrictOwner");
			EReference ref = createContainmentReference(eClass, "key", keyClass);
			addExtendedMetadataIdAnnotation(ref);

			CompositeIdProcessor processor = new CompositeIdProcessor(true);
			List<Id> ids = processor.createIds(eClass, context);

			assertThat(ids).hasSize(1);
			assertThat(ids.get(0).getGeneratedValue()).isNull();
			assertThat(ids.get(0).getSequenceGenerator()).isNull();
		}
	}

	@Nested
	class SyntheticIdTests {

		@Test
		void testSyntheticIdCreated() {
			EClass eClass = createEClass("NoId");
			addAttribute(eClass, "name", EcorePackage.Literals.ESTRING);

			CompositeIdProcessor processor = new CompositeIdProcessor(false);
			List<Id> ids = processor.createIds(eClass, context);

			assertThat(ids).hasSize(1);
			assertThat(ids.get(0).getName()).isEqualTo("pk_NoId");
		}

		@Test
		void testSyntheticIdColumnSetup() {
			EClass eClass = createEClass("NoId2");

			CompositeIdProcessor processor = new CompositeIdProcessor(false);
			List<Id> ids = processor.createIds(eClass, context);

			Id id = ids.get(0);
			assertThat(id.getColumn()).isNotNull();
			assertThat(id.getColumn().getName()).isEqualTo("PK_NOID2");
			assertThat(id.getColumn().isNullable()).isFalse();
			assertThat(id.getColumn().isUnique()).isTrue();
		}

		@Test
		void testSyntheticIdHasSequenceGenerator() {
			EClass eClass = createEClass("AutoId");

			CompositeIdProcessor processor = new CompositeIdProcessor(false);
			List<Id> ids = processor.createIds(eClass, context);

			assertThat(ids.get(0).getSequenceGenerator()).isNotNull();
			assertThat(ids.get(0).getSequenceGenerator().getName()).contains("AUTOID");
		}

		@Test
		void testSyntheticIdStrictModeNoSequence() {
			EClass eClass = createEClass("StrictNoId");

			CompositeIdProcessor processor = new CompositeIdProcessor(true);
			List<Id> ids = processor.createIds(eClass, context);

			assertThat(ids).hasSize(1);
			assertThat(ids.get(0).getSequenceGenerator()).isNull();
		}
	}

	@Nested
	class EntityProcessorIntegrationTests {

		@Test
		void testEntityProcessorUsesSingleId() {
			EClass eClass = createEClass("SingleIdEntity");
			addIdAttribute(eClass, "myId", EcorePackage.Literals.ESTRING);

			EntityProcessor ep = new EntityProcessor(eClass, context);
			ep.process();

			assertThat(ep.isProcessed()).isTrue();
			assertThat(ep.getTarget().getAttributes().getId()).hasSize(1);
			assertThat(ep.getTarget().getAttributes().getId().get(0).getName()).isEqualTo("myId");
		}

		@Test
		void testEntityProcessorUsesCompositeId() {
			EClass eClass = createEClass("CompositeIdEntity");
			addIdAttribute(eClass, "pk1", EcorePackage.Literals.ESTRING);
			addIdAttribute(eClass, "pk2", EcorePackage.Literals.ELONG);

			EntityProcessor ep = new EntityProcessor(eClass, context);
			ep.process();

			assertThat(ep.isProcessed()).isTrue();
			assertThat(ep.getTarget().getAttributes().getId()).hasSize(2);
			assertThat(ep.getTarget().getAttributes().getId()).extracting("name").contains("pk1", "pk2");
		}

		@Test
		void testEntityProcessorUsesSyntheticId() {
			EClass eClass = createEClass("NoIdEntity");
			addAttribute(eClass, "data", EcorePackage.Literals.ESTRING);

			EntityProcessor ep = new EntityProcessor(eClass, context);
			ep.process();

			assertThat(ep.isProcessed()).isTrue();
			assertThat(ep.getTarget().getAttributes().getId()).hasSize(1);
			assertThat(ep.getTarget().getAttributes().getId().get(0).getName()).startsWith("pk_");
		}
	}

	// --- Helper methods ---

	private EClass createEClass(String name) {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(name);
		testPackage.getEClassifiers().add(eClass);
		return eClass;
	}

	private EAttribute addIdAttribute(EClass owner, String name, org.eclipse.emf.ecore.EClassifier type) {
		EAttribute attr = EcoreFactory.eINSTANCE.createEAttribute();
		attr.setName(name);
		attr.setEType(type);
		attr.setID(true);
		owner.getEStructuralFeatures().add(attr);
		return attr;
	}

	private EAttribute addAttribute(EClass owner, String name, org.eclipse.emf.ecore.EClassifier type) {
		EAttribute attr = EcoreFactory.eINSTANCE.createEAttribute();
		attr.setName(name);
		attr.setEType(type);
		owner.getEStructuralFeatures().add(attr);
		return attr;
	}

	private EReference createContainmentReference(EClass owner, String name, EClass type) {
		EReference ref = EcoreFactory.eINSTANCE.createEReference();
		ref.setName(name);
		ref.setEType(type);
		ref.setContainment(true);
		owner.getEStructuralFeatures().add(ref);
		return ref;
	}

	private void addExtendedMetadataIdAnnotation(org.eclipse.emf.ecore.EModelElement element) {
		EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
		annotation.setSource("http:///org/eclipse/emf/ecore/util/ExtendedMetaData");
		annotation.getDetails().put("id", "true");
		element.getEAnnotations().add(annotation);
	}
}
