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
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.persistence.Keywords;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for @Version / Optimistic Locking mapping via EAnnotation.
 */
class VersionMappingTest {

	private EPackage testPackage;

	@BeforeEach
	void setUp() {
		testPackage = EcoreFactory.eINSTANCE.createEPackage();
		testPackage.setName("testpkg");
		testPackage.setNsURI("http://test/version");
		testPackage.setNsPrefix("test");
	}

	@Test
	@DisplayName("Attribute with version annotation becomes Version mapping, not Basic")
	void testVersionAttributeNotMappedAsBasic() {
		EClass entity = createClassWithId("MyEntity");
		EAttribute versionAttr = addAttribute(entity, "version", EcorePackage.Literals.ELONG);
		addVersionAnnotation(versionAttr);
		addAttribute(entity, "name", EcorePackage.Literals.ESTRING);

		MappingProcessor processor = MappingProcessor.create(entity);
		processor.process();

		Entity result = findEntity(processor.getTarget(), "MyEntity");
		// Version attribute should be in version list, NOT in basic list
		assertThat(result.getAttributes().getVersion())
				.extracting("name")
				.containsExactly("version");
		assertThat(result.getAttributes().getBasic())
				.extracting("name")
				.contains("name")
				.doesNotContain("version");
	}

	@Test
	@DisplayName("Version mapping has correct column name")
	void testVersionColumnName() {
		EClass entity = createClassWithId("MyEntity");
		EAttribute versionAttr = addAttribute(entity, "optLockVersion", EcorePackage.Literals.ELONG);
		addVersionAnnotation(versionAttr);

		MappingProcessor processor = MappingProcessor.create(entity);
		processor.process();

		Entity result = findEntity(processor.getTarget(), "MyEntity");
		assertThat(result.getAttributes().getVersion()).hasSize(1);
		assertThat(result.getAttributes().getVersion().get(0).getColumn().getName())
				.isEqualTo("OPTLOCKVERSION");
	}

	@Test
	@DisplayName("Attribute without version annotation is mapped as Basic")
	void testNonVersionAttributeStaysBasic() {
		EClass entity = createClassWithId("MyEntity");
		addAttribute(entity, "counter", EcorePackage.Literals.ELONG);

		MappingProcessor processor = MappingProcessor.create(entity);
		processor.process();

		Entity result = findEntity(processor.getTarget(), "MyEntity");
		assertThat(result.getAttributes().getVersion()).isEmpty();
		assertThat(result.getAttributes().getBasic())
				.extracting("name")
				.contains("counter");
	}

	@Test
	@DisplayName("Multiple entities can each have their own version attribute")
	void testMultipleEntitiesWithVersion() {
		EClass entityA = createClassWithId("EntityA");
		EAttribute vA = addAttribute(entityA, "version", EcorePackage.Literals.ELONG);
		addVersionAnnotation(vA);

		EClass entityB = createClassWithId("EntityB");
		EAttribute vB = addAttribute(entityB, "rev", EcorePackage.Literals.EINT);
		addVersionAnnotation(vB);

		MappingProcessor processor = MappingProcessor.create(java.util.List.of(entityA, entityB));
		processor.process();

		assertThat(findEntity(processor.getTarget(), "EntityA").getAttributes().getVersion())
				.extracting("name").containsExactly("version");
		assertThat(findEntity(processor.getTarget(), "EntityB").getAttributes().getVersion())
				.extracting("name").containsExactly("rev");
	}

	// --- Helpers ---

	private EClass createClassWithId(String name) {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(name);
		testPackage.getEClassifiers().add(eClass);
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

	private void addVersionAnnotation(EAttribute attr) {
		EAnnotation ann = EcoreFactory.eINSTANCE.createEAnnotation();
		ann.setSource(Keywords.PERSISTENCE_ANNOTATION_SOURCE);
		ann.getDetails().put("version", "true");
		attr.getEAnnotations().add(ann);
	}

	private Entity findEntity(EntityMappings em, String name) {
		return em.getEntity().stream()
				.filter(e -> name.equals(e.getName()))
				.findFirst()
				.orElseThrow(() -> new AssertionError("Entity not found: " + name));
	}
}
