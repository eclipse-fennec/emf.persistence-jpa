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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.persistence.eorm.ElementCollection;
import org.eclipse.fennec.persistence.orm.MappingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ElementCollectionProcessor}
 */
public class ElementCollectionProcessorTest {

	private MappingContext context;
	private EPackage testPackage;
	private EClass testClass;

	@BeforeEach
	void setUp() {
		context = new MappingContext();
		testPackage = EcoreFactory.eINSTANCE.createEPackage();
		testPackage.setName("test");
		testPackage.setNsURI("http://test");
		testClass = EcoreFactory.eINSTANCE.createEClass();
		testClass.setName("Person");
		testPackage.getEClassifiers().add(testClass);

		EntityProcessor ep = new EntityProcessor(testClass, context);
		ep.process();
	}

	@Test
	void testMultiValuedStringAttribute() {
		EAttribute attr = createManyAttribute("tags", EcorePackage.Literals.ESTRING);
		ElementCollectionProcessor processor = new ElementCollectionProcessor(attr, context);
		processor.process();

		assertThat(processor.isProcessed()).isTrue();
		ElementCollection ec = processor.getMapping();
		assertThat(ec).isNotNull();
		assertThat(ec.getName()).isEqualTo("tags");
	}

	@Test
	void testCollectionTableNaming() {
		EAttribute attr = createManyAttribute("skills", EcorePackage.Literals.ESTRING);
		ElementCollectionProcessor processor = new ElementCollectionProcessor(attr, context);
		processor.process();

		ElementCollection ec = processor.getMapping();
		assertThat(ec.getCollectionTable()).isNotNull();
		assertThat(ec.getCollectionTable().getName()).isEqualTo("PERSON__SKILLS");
	}

	@Test
	void testValueColumnNaming() {
		EAttribute attr = createManyAttribute("skills", EcorePackage.Literals.ESTRING);
		ElementCollectionProcessor processor = new ElementCollectionProcessor(attr, context);
		processor.process();

		ElementCollection ec = processor.getMapping();
		assertThat(ec.getColumn()).isNotNull();
		assertThat(ec.getColumn().getName()).isEqualTo("VAL_SKILLS");
	}

	@Test
	void testJoinColumnCreated() {
		EAttribute attr = createManyAttribute("emails", EcorePackage.Literals.ESTRING);
		ElementCollectionProcessor processor = new ElementCollectionProcessor(attr, context);
		processor.process();

		ElementCollection ec = processor.getMapping();
		assertThat(ec.getCollectionTable().getJoinColumn()).isNotEmpty();
		assertThat(ec.getCollectionTable().getForeignKey()).isNotNull();
	}

	@Test
	void testRegisteredToEntity() {
		EAttribute attr = createManyAttribute("roles", EcorePackage.Literals.ESTRING);
		ElementCollectionProcessor processor = new ElementCollectionProcessor(attr, context);
		processor.process();

		assertThat(context.getEntity(testClass).getAttributes().getElementCollection())
			.anyMatch(ec -> "roles".equals(ec.getName()));
	}

	// --- Helper ---

	private EAttribute createManyAttribute(String name, org.eclipse.emf.ecore.EClassifier type) {
		EAttribute attr = EcoreFactory.eINSTANCE.createEAttribute();
		attr.setName(name);
		attr.setEType(type);
		attr.setUpperBound(-1);
		testClass.getEStructuralFeatures().add(attr);
		return attr;
	}
}
