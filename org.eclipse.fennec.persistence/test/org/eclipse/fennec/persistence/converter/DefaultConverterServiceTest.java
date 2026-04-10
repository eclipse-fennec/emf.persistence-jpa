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
package org.eclipse.fennec.persistence.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.persistence.api.TypeConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link DefaultConverterService} and converter ordering/lookup.
 */
public class DefaultConverterServiceTest {

	private TestConverterService service;

	@BeforeEach
	void setUp() {
		service = new TestConverterService();
	}

	/**
	 * Concrete subclass for testing since DefaultConverterService is abstract.
	 */
	static class TestConverterService extends DefaultConverterService {
	}

	@Nested
	class GetConverterByTypeTests {

		@Test
		void testNullTypeThrows() {
			assertThatThrownBy(() -> service.getConverter((EClassifier) null))
				.isInstanceOf(NullPointerException.class);
		}

		@Test
		void testUuidTypeReturnsComprehensiveConverter() {
			EDataType uuidType = EcoreFactory.eINSTANCE.createEDataType();
			uuidType.setName("UUID");
			uuidType.setInstanceClass(UUID.class);

			TypeConverter converter = service.getConverter(uuidType);
			assertThat(converter).isNotNull();
			assertThat(converter).isInstanceOf(ComprehensiveTypeConverter.class);
		}

		@Test
		void testLocalDateTypeReturnsComprehensiveConverter() {
			EDataType ldType = EcoreFactory.eINSTANCE.createEDataType();
			ldType.setName("LocalDate");
			ldType.setInstanceClass(LocalDate.class);

			TypeConverter converter = service.getConverter(ldType);
			assertThat(converter).isNotNull();
			assertThat(converter).isInstanceOf(ComprehensiveTypeConverter.class);
		}

		@Test
		void testBigDecimalTypeReturnsComprehensiveConverter() {
			EDataType bdType = EcoreFactory.eINSTANCE.createEDataType();
			bdType.setName("BigDecimal");
			bdType.setInstanceClass(BigDecimal.class);

			TypeConverter converter = service.getConverter(bdType);
			assertThat(converter).isNotNull();
			assertThat(converter).isInstanceOf(ComprehensiveTypeConverter.class);
		}

		@Test
		void testInstantTypeReturnsComprehensiveConverter() {
			EDataType instantType = EcoreFactory.eINSTANCE.createEDataType();
			instantType.setName("Instant");
			instantType.setInstanceClass(Instant.class);

			TypeConverter converter = service.getConverter(instantType);
			assertThat(converter).isNotNull();
			assertThat(converter).isInstanceOf(ComprehensiveTypeConverter.class);
		}

		@Test
		void testDurationTypeReturnsComprehensiveConverter() {
			EDataType durType = EcoreFactory.eINSTANCE.createEDataType();
			durType.setName("Duration");
			durType.setInstanceClass(Duration.class);

			TypeConverter converter = service.getConverter(durType);
			assertThat(converter).isNotNull();
			assertThat(converter).isInstanceOf(ComprehensiveTypeConverter.class);
		}
	}

	@Nested
	class GetConverterByNameTests {

		@Test
		void testNullNameThrows() {
			assertThatThrownBy(() -> service.getConverter((String) null))
				.isInstanceOf(NullPointerException.class);
		}

		@Test
		void testUnknownNameThrows() {
			assertThatThrownBy(() -> service.getConverter("nonExistentConverter"))
				.isInstanceOf(IllegalStateException.class);
		}

		@Test
		void testFindComprehensiveConverterByName() {
			TypeConverter converter = service.getConverter("comprehensive");
			assertThat(converter).isNotNull();
			assertThat(converter).isInstanceOf(ComprehensiveTypeConverter.class);
		}

		@Test
		void testFindDefaultConverterByName() {
			TypeConverter converter = service.getConverter("default");
			assertThat(converter).isNotNull();
			assertThat(converter).isInstanceOf(DefaultConverter.class);
		}

		@Test
		void testFindArrayConverterByName() {
			TypeConverter converter = service.getConverter("array");
			assertThat(converter).isNotNull();
			assertThat(converter).isInstanceOf(ArrayConverter.class);
		}
	}

	@Nested
	class ConverterPriorityTests {

		@Test
		void testComprehensiveConverterIsFirst() {
			// The first converter should be ComprehensiveTypeConverter
			assertThat(service.converters.getFirst()).isInstanceOf(ComprehensiveTypeConverter.class);
		}

		@Test
		void testDefaultConverterOrder() {
			// Verify the expected converter order
			assertThat(service.converters.get(0)).isInstanceOf(ComprehensiveTypeConverter.class);
			assertThat(service.converters.get(1)).isInstanceOf(ArrayConverter.class);
			assertThat(service.converters.get(2)).isInstanceOf(DefaultConverter.class);
			assertThat(service.converters.get(3)).isInstanceOf(XMLGregorianCalendarConverter.class);
			assertThat(service.converters.get(4)).isInstanceOf(BigDecimalConverter.class);
			assertThat(service.converters.get(5)).isInstanceOf(BigIntegerConverter.class);
			assertThat(service.converters.get(6)).isInstanceOf(NonContainmentConverter.class);
		}

		@Test
		void testTotalDefaultConverterCount() {
			assertThat(service.converters).hasSize(7);
		}
	}

	@Nested
	class DynamicRegistrationTests {

		@Test
		void testAddConverterIncreasesCount() {
			int before = service.converters.size();

			TypeConverter custom = new TypeConverter() {
				@Override
				public String getName() { return "custom"; }
				@Override
				public Object convertValueToEMF(EClassifier eDataType, Object value) { return value; }
				@Override
				public Object convertEMFToValue(EClassifier eDataType, Object emfValue) { return emfValue; }
				@Override
				public boolean isConverterForType(EClassifier eDataType) { return false; }
			};

			synchronized (service.converters) {
				service.converters.add(custom);
			}

			assertThat(service.converters).hasSize(before + 1);
		}

		@Test
		void testRemoveConverterDecreasesCount() {
			TypeConverter last;
			synchronized (service.converters) {
				last = service.converters.getLast();
				int before = service.converters.size();
				service.converters.remove(last);
				assertThat(service.converters).hasSize(before - 1);
				// restore
				service.converters.add(last);
			}
		}

		@Test
		void testAddedConverterCanBeFoundByName() {
			TypeConverter custom = new TypeConverter() {
				@Override
				public String getName() { return "mySpecialConverter"; }
				@Override
				public Object convertValueToEMF(EClassifier eDataType, Object value) { return value; }
				@Override
				public Object convertEMFToValue(EClassifier eDataType, Object emfValue) { return emfValue; }
				@Override
				public boolean isConverterForType(EClassifier eDataType) { return false; }
			};

			synchronized (service.converters) {
				service.converters.add(custom);
			}

			TypeConverter found = service.getConverter("mySpecialConverter");
			assertThat(found).isSameAs(custom);
		}
	}
}
