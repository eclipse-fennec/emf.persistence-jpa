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
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.persistence.eorm.Basic;
import org.eclipse.fennec.persistence.eorm.FetchType;
import org.eclipse.fennec.persistence.eorm.TemporalType;
import org.eclipse.fennec.persistence.orm.MappingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link BasicProcessor}
 */
public class BasicProcessorTest {

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
		testClass.setName("TestClass");
		testPackage.getEClassifiers().add(testClass);

		// Register entity so registerMapping works
		EntityProcessor ep = new EntityProcessor(testClass, context);
		ep.process();
	}

	@Nested
	class BasicAttributeTests {

		@Test
		void testStringAttribute() {
			EAttribute attr = createAttribute("name", EcorePackage.Literals.ESTRING, false);
			BasicProcessor processor = new BasicProcessor(attr, context);
			processor.process();

			assertThat(processor.isProcessed()).isTrue();
			Basic result = processor.getMapping();
			assertThat(result).isNotNull();
			assertThat(result.getName()).isEqualTo("name");
			assertThat(result.getFetch()).isEqualTo(FetchType.EAGER);
		}

		@Test
		void testOptionalAttribute() {
			EAttribute attr = createAttribute("optional", EcorePackage.Literals.ESTRING, false);
			// not required → optional=true
			attr.setLowerBound(0);
			BasicProcessor processor = new BasicProcessor(attr, context);
			processor.process();

			assertThat(processor.isProcessed()).isTrue();
			assertThat(processor.getMapping().isOptional()).isTrue();
		}

		@Test
		void testRequiredAttribute() {
			EAttribute attr = createAttribute("required", EcorePackage.Literals.ESTRING, false);
			attr.setLowerBound(1);
			BasicProcessor processor = new BasicProcessor(attr, context);
			processor.process();

			assertThat(processor.isProcessed()).isTrue();
			assertThat(processor.getMapping().isOptional()).isFalse();
		}

		@Test
		void testIntAttribute() {
			EAttribute attr = createAttribute("count", EcorePackage.Literals.EINT, false);
			BasicProcessor processor = new BasicProcessor(attr, context);
			processor.process();

			assertThat(processor.isProcessed()).isTrue();
			assertThat(processor.getMapping().getName()).isEqualTo("count");
		}
	}

	@Nested
	class TemporalTypeTests {

		@Test
		void testDateAttributeIsTimestamp() {
			EDataType dateType = createDataType("MyDate", java.util.Date.class);
			EAttribute attr = createAttribute("createdAt", dateType, false);
			BasicProcessor processor = new BasicProcessor(attr, context);
			processor.process();

			assertThat(processor.isProcessed()).isTrue();
			assertThat(processor.getMapping().getTemporal()).isEqualTo(TemporalType.TIMESTAMP);
		}

		@Test
		void testLocalDateTimeIsTimestamp() {
			EDataType ldtType = createDataType("LDT", java.time.LocalDateTime.class);
			EAttribute attr = createAttribute("updatedAt", ldtType, false);
			BasicProcessor processor = new BasicProcessor(attr, context);
			processor.process();

			assertThat(processor.isProcessed()).isTrue();
			assertThat(processor.getMapping().getTemporal()).isEqualTo(TemporalType.TIMESTAMP);
		}

		@Test
		void testInstantIsTimestamp() {
			EDataType instantType = createDataType("Instant", java.time.Instant.class);
			EAttribute attr = createAttribute("timestamp", instantType, false);
			BasicProcessor processor = new BasicProcessor(attr, context);
			processor.process();

			assertThat(processor.isProcessed()).isTrue();
			assertThat(processor.getMapping().getTemporal()).isEqualTo(TemporalType.TIMESTAMP);
		}

		@Test
		void testLocalDateIsDate() {
			EDataType ldType = createDataType("LD", java.time.LocalDate.class);
			EAttribute attr = createAttribute("birthday", ldType, false);
			BasicProcessor processor = new BasicProcessor(attr, context);
			processor.process();

			assertThat(processor.isProcessed()).isTrue();
			assertThat(processor.getMapping().getTemporal()).isEqualTo(TemporalType.DATE);
		}

		@Test
		void testLocalTimeIsTime() {
			EDataType ltType = createDataType("LT", java.time.LocalTime.class);
			EAttribute attr = createAttribute("startTime", ltType, false);
			BasicProcessor processor = new BasicProcessor(attr, context);
			processor.process();

			assertThat(processor.isProcessed()).isTrue();
			assertThat(processor.getMapping().getTemporal()).isEqualTo(TemporalType.TIME);
		}

		@Test
		void testCalendarIsDate() {
			EDataType calType = createDataType("Cal", java.util.Calendar.class);
			EAttribute attr = createAttribute("cal", calType, false);
			BasicProcessor processor = new BasicProcessor(attr, context);
			processor.process();

			assertThat(processor.isProcessed()).isTrue();
			assertThat(processor.getMapping().getTemporal()).isEqualTo(TemporalType.DATE);
		}

		@Test
		void testStringHasNoTemporal() {
			EAttribute attr = createAttribute("name", EcorePackage.Literals.ESTRING, false);
			BasicProcessor processor = new BasicProcessor(attr, context);
			processor.process();

			assertThat(processor.isProcessed()).isTrue();
			assertThat(processor.getMapping().getTemporal()).isNull();
		}
	}

	@Nested
	class EnumTests {

		@Test
		void testEnumAttribute() {
			org.eclipse.emf.ecore.EEnum myEnum = EcoreFactory.eINSTANCE.createEEnum();
			myEnum.setName("Status");
			testPackage.getEClassifiers().add(myEnum);

			EAttribute attr = createAttribute("status", myEnum, false);
			BasicProcessor processor = new BasicProcessor(attr, context);
			processor.process();

			assertThat(processor.isProcessed()).isTrue();
			assertThat(processor.getMapping().getEnumerated()).isSameAs(myEnum);
		}
	}

	@Nested
	class RegistrationTests {

		@Test
		void testMappingRegisteredToEntity() {
			EAttribute attr = createAttribute("name", EcorePackage.Literals.ESTRING, false);
			BasicProcessor processor = new BasicProcessor(attr, context);
			processor.process();

			assertThat(processor.isProcessed()).isTrue();
			// Entity should now contain the basic mapping
			assertThat(context.getEntity(testClass).getAttributes().getBasic())
				.anyMatch(b -> "name".equals(b.getName()));
		}
	}

	// --- Helper methods ---

	private EAttribute createAttribute(String name, org.eclipse.emf.ecore.EClassifier type, boolean many) {
		EAttribute attr = EcoreFactory.eINSTANCE.createEAttribute();
		attr.setName(name);
		attr.setEType(type);
		if (many) {
			attr.setUpperBound(-1);
		}
		testClass.getEStructuralFeatures().add(attr);
		return attr;
	}

	private EDataType createDataType(String name, Class<?> instanceClass) {
		EDataType dt = EcoreFactory.eINSTANCE.createEDataType();
		dt.setName(name);
		dt.setInstanceClass(instanceClass);
		testPackage.getEClassifiers().add(dt);
		return dt;
	}
}
