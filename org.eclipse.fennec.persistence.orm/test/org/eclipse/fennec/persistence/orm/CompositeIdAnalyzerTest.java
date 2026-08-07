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

import java.util.List;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.persistence.helper.CompositeIds;
import org.eclipse.fennec.persistence.orm.IdConfiguration.IdStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CompositeIdAnalyzer}
 */
public class CompositeIdAnalyzerTest {

	private CompositeIdAnalyzer analyzer;
	private EPackage testPackage;

	@BeforeEach
	void setUp() {
		analyzer = new CompositeIdAnalyzer();
		testPackage = EcoreFactory.eINSTANCE.createEPackage();
		testPackage.setName("test");
		testPackage.setNsURI("http://test");
	}

	@Nested
	class AnalyzeIdStructureTests {

		@Test
		void testNullThrows() {
			assertThatThrownBy(() -> analyzer.analyzeIdStructure(null))
				.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		void testSingleIdAttribute() {
			EClass eClass = createEClass("Person");
			addIdAttribute(eClass, "id", EcorePackage.Literals.ESTRING);

			IdConfiguration config = analyzer.analyzeIdStructure(eClass);

			assertThat(config.getStrategy()).isEqualTo(IdStrategy.SINGLE_ID);
			assertThat(config.isSingleId()).isTrue();
			assertThat(config.isComposite()).isFalse();
			assertThat(config.getIdAttributes()).hasSize(1);
			assertThat(config.getIdAttributes().get(0).getName()).isEqualTo("id");
		}

		@Test
		void testMultipleIdAttributesEmbeddedId() {
			EClass eClass = createEClass("MultiPK");
			addIdAttribute(eClass, "id", EcorePackage.Literals.ESTRING);
			addAttribute(eClass, "timestamp", EcorePackage.Literals.ELONG);
			declareIdFeatures(eClass, "id,timestamp");

			IdConfiguration config = analyzer.analyzeIdStructure(eClass);

			assertThat(config.getStrategy()).isEqualTo(IdStrategy.EMBEDDED_ID);
			assertThat(config.isEmbeddedId()).isTrue();
			assertThat(config.isComposite()).isTrue();
			assertThat(config.getIdAttributes()).hasSize(2);
			assertThat(config.getIdAttributes()).extracting("name").contains("id", "timestamp");
		}

		@Test
		void testNoIdAttributesSyntheticId() {
			EClass eClass = createEClass("NoId");
			addAttribute(eClass, "name", EcorePackage.Literals.ESTRING);

			IdConfiguration config = analyzer.analyzeIdStructure(eClass);

			assertThat(config.getStrategy()).isEqualTo(IdStrategy.SYNTHETIC_ID);
			assertThat(config.isSynthetic()).isTrue();
			assertThat(config.getSyntheticIdName()).isEqualTo("pk_NoId");
		}

		@Test
		void testIdClassReferenceHighestPriority() {
			EClass eClass = createEClass("WithIdClass");
			EClass keyClass = createEClass("KeyClass");
			addIdAttribute(keyClass, "k1", EcorePackage.Literals.ESTRING);

			// Add a containment reference with id annotation
			EReference ref = createContainmentReference(eClass, "key", keyClass);
			addExtendedMetadataIdAnnotation(ref);

			// Also add a direct ID attribute — should be ignored because IdClass has priority
			addIdAttribute(eClass, "directId", EcorePackage.Literals.ESTRING);

			IdConfiguration config = analyzer.analyzeIdStructure(eClass);

			assertThat(config.getStrategy()).isEqualTo(IdStrategy.ID_CLASS);
			assertThat(config.isIdClass()).isTrue();
			assertThat(config.getIdClassReference()).isSameAs(ref);
		}

		@Test
		void testIdClassViaClassLevelAnnotation() {
			EClass eClass = createEClass("ClassLevelId");
			EClass keyClass = createEClass("KeyClass2");
			addIdAttribute(keyClass, "k1", EcorePackage.Literals.ESTRING);

			// Containment reference (target for IdClass)
			createContainmentReference(eClass, "compositeKey", keyClass);

			// Class-level annotation with id=true
			addExtendedMetadataIdAnnotation(eClass);

			IdConfiguration config = analyzer.analyzeIdStructure(eClass);

			assertThat(config.getStrategy()).isEqualTo(IdStrategy.ID_CLASS);
			assertThat(config.getIdClassReference().getName()).isEqualTo("compositeKey");
		}

		@Test
		void testThreeIdAttributesEmbeddedId() {
			EClass eClass = createEClass("TriplePK");
			addIdAttribute(eClass, "a", EcorePackage.Literals.ESTRING);
			addAttribute(eClass, "b", EcorePackage.Literals.EINT);
			addAttribute(eClass, "c", EcorePackage.Literals.ELONG);
			declareIdFeatures(eClass, "a,b,c");

			IdConfiguration config = analyzer.analyzeIdStructure(eClass);

			assertThat(config.getStrategy()).isEqualTo(IdStrategy.EMBEDDED_ID);
			assertThat(config.getIdAttributes()).hasSize(3);
		}
	}

	@Nested
	class FindDirectIdAttributesTests {

		@Test
		void testFindsIdAttributes() {
			EClass eClass = createEClass("Test");
			addIdAttribute(eClass, "pk", EcorePackage.Literals.ESTRING);
			addAttribute(eClass, "name", EcorePackage.Literals.ESTRING);

			List<EAttribute> ids = analyzer.findDirectIdAttributes(eClass);

			assertThat(ids).hasSize(1);
			assertThat(ids.get(0).getName()).isEqualTo("pk");
		}

		@Test
		void testNoIdAttributes() {
			EClass eClass = createEClass("NoIds");
			addAttribute(eClass, "name", EcorePackage.Literals.ESTRING);

			List<EAttribute> ids = analyzer.findDirectIdAttributes(eClass);

			assertThat(ids).isEmpty();
		}

		@Test
		void testMultipleIdAttributes() {
			EClass eClass = createEClass("MultiId");
			addIdAttribute(eClass, "id1", EcorePackage.Literals.ESTRING);
			addAttribute(eClass, "id2", EcorePackage.Literals.ELONG);
			addAttribute(eClass, "data", EcorePackage.Literals.ESTRING);
			declareIdFeatures(eClass, "id1,id2");

			List<EAttribute> ids = analyzer.findDirectIdAttributes(eClass);

			assertThat(ids).hasSize(2);
			assertThat(ids).extracting("name").contains("id1", "id2");
		}
	}

	@Nested
	class FindIdClassReferenceTests {

		@Test
		void testNoIdClassReference() {
			EClass eClass = createEClass("Simple");
			addAttribute(eClass, "name", EcorePackage.Literals.ESTRING);

			EReference result = analyzer.findIdClassReference(eClass);

			assertThat(result).isNull();
		}

		@Test
		void testReferenceLevelAnnotation() {
			EClass eClass = createEClass("WithRef");
			EClass keyClass = createEClass("Key");
			EReference ref = createContainmentReference(eClass, "key", keyClass);
			addExtendedMetadataIdAnnotation(ref);

			EReference result = analyzer.findIdClassReference(eClass);

			assertThat(result).isSameAs(ref);
		}

		@Test
		void testClassLevelAnnotationUsesFirstContainment() {
			EClass eClass = createEClass("WithClassAnnotation");
			EClass keyClass = createEClass("Key2");
			EReference ref = createContainmentReference(eClass, "myKey", keyClass);
			addExtendedMetadataIdAnnotation(eClass);

			EReference result = analyzer.findIdClassReference(eClass);

			assertThat(result).isSameAs(ref);
		}

		@Test
		void testClassLevelAnnotationWithoutContainmentReturnsNull() {
			EClass eClass = createEClass("NoContainment");
			addExtendedMetadataIdAnnotation(eClass);

			EReference result = analyzer.findIdClassReference(eClass);

			assertThat(result).isNull();
		}
	}

	@Nested
	class AnalyzeIdClassComponentsTests {

		@Test
		void testNullReference() {
			List<EAttribute> result = analyzer.analyzeIdClassComponents(null);
			assertThat(result).isEmpty();
		}

		@Test
		void testComponentsFromIdAttributes() {
			EClass keyClass = createEClass("CompositeKey");
			addIdAttribute(keyClass, "part1", EcorePackage.Literals.ESTRING);
			addAttribute(keyClass, "part2", EcorePackage.Literals.ELONG);
			addAttribute(keyClass, "notId", EcorePackage.Literals.ESTRING);
			declareIdFeatures(keyClass, "part1,part2");

			EClass owner = createEClass("Owner");
			EReference ref = createContainmentReference(owner, "key", keyClass);

			List<EAttribute> components = analyzer.analyzeIdClassComponents(ref);

			assertThat(components).hasSize(2);
			assertThat(components).extracting("name").contains("part1", "part2");
		}

		@Test
		void testComponentsFromEKeys() {
			EClass keyClass = createEClass("KeyWithEKeys");
			EAttribute k1 = addAttribute(keyClass, "k1", EcorePackage.Literals.ESTRING);
			EAttribute k2 = addAttribute(keyClass, "k2", EcorePackage.Literals.ESTRING);
			addAttribute(keyClass, "other", EcorePackage.Literals.ESTRING);

			EClass owner = createEClass("OwnerWithEKeys");
			EReference ref = createContainmentReference(owner, "key", keyClass);
			ref.getEKeys().add(k1);
			ref.getEKeys().add(k2);

			List<EAttribute> components = analyzer.analyzeIdClassComponents(ref);

			assertThat(components).hasSize(2);
			assertThat(components).extracting("name").containsExactly("k1", "k2");
		}
	}

	@Nested
	class IdConfigurationTests {

		@Test
		void testSingleIdUtilities() {
			EClass eClass = createEClass("SingleId");
			EAttribute id = addIdAttribute(eClass, "id", EcorePackage.Literals.ESTRING);

			IdConfiguration config = IdConfiguration.createSingleId(id);

			assertThat(config.isSingleId()).isTrue();
			assertThat(config.isComposite()).isFalse();
			assertThat(config.isEmbeddedId()).isFalse();
			assertThat(config.isIdClass()).isFalse();
			assertThat(config.isSynthetic()).isFalse();
			assertThat(config.getIdAttributeCount()).isEqualTo(1);
		}

		@Test
		void testEmbeddedIdRequiresMultipleAttributes() {
			EClass eClass = createEClass("Test");
			EAttribute id = addIdAttribute(eClass, "id", EcorePackage.Literals.ESTRING);

			assertThatThrownBy(() -> IdConfiguration.createEmbeddedId(List.of(id)))
				.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		void testSyntheticIdName() {
			IdConfiguration config = IdConfiguration.createSyntheticId("pk_Test");

			assertThat(config.isSynthetic()).isTrue();
			assertThat(config.getSyntheticIdName()).isEqualTo("pk_Test");
			assertThat(config.getIdAttributes()).isEmpty();
		}

		@Test
		void testToString() {
			EClass eClass = createEClass("Test2");
			EAttribute id = addIdAttribute(eClass, "myId", EcorePackage.Literals.ESTRING);

			IdConfiguration config = IdConfiguration.createSingleId(id);

			assertThat(config.toString()).contains("SINGLE_ID").contains("myId");
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

	/** The canonical composite declaration (issue #115): explicit idFeatures annotation. */
	private void declareIdFeatures(EClass owner, String idFeatures) {
		org.eclipse.emf.ecore.EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
		annotation.setSource(CompositeIds.ANNOTATION_SOURCE);
		annotation.getDetails().put(CompositeIds.ID_FEATURES, idFeatures);
		owner.getEAnnotations().add(annotation);
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
