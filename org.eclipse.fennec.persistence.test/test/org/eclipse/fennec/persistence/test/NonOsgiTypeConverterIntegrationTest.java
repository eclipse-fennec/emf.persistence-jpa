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
package org.eclipse.fennec.persistence.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.api.TypeConverter;
import org.eclipse.fennec.persistence.converter.DefaultConverterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Non-OSGi port of {@code TypeConverterIntegrationTest}. Instantiates a plain
 * {@link DefaultConverterService} and exercises the complete conversion chain
 * (date/time, UUID, primitive arrays, round-trips) without an OSGi container.
 */
@SuppressWarnings("restriction")
class NonOsgiTypeConverterIntegrationTest {

	private final EcoreFactory ecoreFactory = EcoreFactory.eINSTANCE;
	private ConverterService converterService;

	@BeforeEach
	void setUp() {
		converterService = new DefaultConverterService();
	}

	@Test
	@DisplayName("ComprehensiveTypeConverter is available through ConverterService")
	void testComprehensiveTypeConverterServiceRegistration() {
		assertNotNull(converterService);
		EDataType localDateTimeType = createDataType("java.time.LocalDateTime");
		TypeConverter converter = converterService.getConverter(localDateTimeType);
		assertNotNull(converter, "Should find converter for LocalDateTime");
	}

	@Test
	@DisplayName("ConverterService can locate and use ComprehensiveTypeConverter")
	void testConverterServiceIntegration() {
		EDataType localDateType = createDataType("java.time.LocalDate");
		TypeConverter converter = converterService.getConverter(localDateType);
		assertNotNull(converter);

		Date sqlDate = Date.valueOf("2025-01-15");
		Object emfValue = converter.convertValueToEMF(localDateType, sqlDate);
		assertEquals(LocalDate.of(2025, 1, 15), emfValue);
	}

	@Test
	@DisplayName("Type converter works for Java 8+ Time API types via service")
	void testTimeApiTypesViaService() {
		EDataType localDateTimeType = createDataType("java.time.LocalDateTime");
		TypeConverter converter = converterService.getConverter(localDateTimeType);
		assertNotNull(converter);

		Timestamp timestamp = Timestamp.valueOf("2025-01-15 14:30:00");
		assertEquals(LocalDateTime.of(2025, 1, 15, 14, 30, 0),
				converter.convertValueToEMF(localDateTimeType, timestamp));

		EDataType instantType = createDataType("java.time.Instant");
		TypeConverter instantConverter = converterService.getConverter(instantType);
		assertNotNull(instantConverter);

		Timestamp ts = new Timestamp(System.currentTimeMillis());
		assertEquals(ts.toInstant(), instantConverter.convertValueToEMF(instantType, ts));
	}

	@Test
	@DisplayName("Type converter works for UUID via service")
	void testUUIDTypeViaService() {
		EDataType uuidType = createDataType("java.util.UUID");
		TypeConverter converter = converterService.getConverter(uuidType);
		assertNotNull(converter);

		String uuidString = "123e4567-e89b-12d3-a456-426614174000";
		assertEquals(UUID.fromString(uuidString), converter.convertValueToEMF(uuidType, uuidString));
	}

	@Test
	@DisplayName("Type converter works for primitive arrays via service")
	void testPrimitiveArraysViaService() {
		EDataType intArrayType = createDataType("int[]");
		TypeConverter converter = converterService.getConverter(intArrayType);
		assertNotNull(converter);

		Object[] objArray = {1, 2, 3, 4, 5};
		assertArrayEquals(new int[]{1, 2, 3, 4, 5},
				(int[]) converter.convertValueToEMF(intArrayType, objArray));
	}

	@Test
	@DisplayName("ConverterService answers unclaimed types with null (issue #164)")
	void testUnsupportedTypesViaService() {
		EDataType unsupportedType = createDataType("java.lang.UnsupportedType");
		assertNull(converterService.getConverter(unsupportedType));
	}

	@Test
	@DisplayName("Multiple converter requests return same service instance")
	void testServiceInstanceConsistency() {
		EDataType localDateType = createDataType("java.time.LocalDate");
		EDataType uuidType = createDataType("java.util.UUID");

		TypeConverter converter1 = converterService.getConverter(localDateType);
		TypeConverter converter2 = converterService.getConverter(uuidType);
		assertNotNull(converter1);
		assertNotNull(converter2);
		assertSame(converter1, converter2);

		assertTrue(converter1.isConverterForType(localDateType));
		assertTrue(converter2.isConverterForType(uuidType));
	}

	@Test
	@DisplayName("Converter service integration with existing converters")
	void testCoexistenceWithExistingConverters() {
		EDataType localDateType = createDataType("java.time.LocalDate");
		TypeConverter converter = converterService.getConverter(localDateType);
		assertNotNull(converter);

		Date sqlDate = Date.valueOf("2025-12-25");
		Object emfValue = converter.convertValueToEMF(localDateType, sqlDate);
		assertTrue(emfValue instanceof LocalDate);
		assertEquals(LocalDate.of(2025, 12, 25), emfValue);
	}

	@Test
	@DisplayName("ConverterService has expected converter capabilities")
	void testServiceProperties() {
		assertNotNull(converterService.getConverter(createDataType("java.time.LocalDate")));
		assertNotNull(converterService.getConverter(createDataType("java.time.Instant")));
		assertNotNull(converterService.getConverter(createDataType("java.util.UUID")));
		assertNotNull(converterService.getConverter(createDataType("int[]")));

		TypeConverter converter = converterService.getConverter(createDataType("java.time.LocalDate"));
		assertTrue(converter.isConverterForType(createDataType("java.time.LocalDate")));
		assertTrue(converter.isConverterForType(createDataType("java.time.Instant")));
		assertTrue(converter.isConverterForType(createDataType("java.util.UUID")));
		assertTrue(converter.isConverterForType(createDataType("int[]")));
	}

	@Test
	@DisplayName("Integration with actual persistence context")
	void testPersistenceContextIntegration() {
		EDataType timestampType = createDataType("java.time.Instant");
		TypeConverter converter = converterService.getConverter(timestampType);
		assertNotNull(converter);

		Timestamp dbValue = new Timestamp(1706187000000L);
		Object emfValue = converter.convertValueToEMF(timestampType, dbValue);
		assertTrue(emfValue instanceof Instant);

		Instant instant = (Instant) emfValue;
		Object backToDb = converter.convertEMFToValue(timestampType, instant);
		assertTrue(backToDb instanceof Timestamp);
		assertEquals(dbValue, backToDb);
	}

	@Test
	@DisplayName("Comprehensive round-trip conversion testing")
	void testComprehensiveRoundTripConversions() {
		EDataType localDateType = createDataType("java.time.LocalDate");
		TypeConverter converter = converterService.getConverter(localDateType);
		Date originalDate = Date.valueOf("2025-03-15");
		Object emfValue = converter.convertValueToEMF(localDateType, originalDate);
		assertEquals(originalDate, converter.convertEMFToValue(localDateType, emfValue));

		EDataType localDateTimeType = createDataType("java.time.LocalDateTime");
		TypeConverter dateTimeConverter = converterService.getConverter(localDateTimeType);
		Timestamp originalTimestamp = Timestamp.valueOf("2025-03-15 14:30:45");
		Object dateTimeEmf = dateTimeConverter.convertValueToEMF(localDateTimeType, originalTimestamp);
		assertEquals(originalTimestamp, dateTimeConverter.convertEMFToValue(localDateTimeType, dateTimeEmf));

		EDataType uuidType = createDataType("java.util.UUID");
		TypeConverter uuidConverter = converterService.getConverter(uuidType);
		String originalUuidStr = "123e4567-e89b-12d3-a456-426614174000";
		Object uuidEmf = uuidConverter.convertValueToEMF(uuidType, originalUuidStr);
		assertEquals(originalUuidStr, uuidConverter.convertEMFToValue(uuidType, uuidEmf));

		EDataType intArrayType = createDataType("int[]");
		TypeConverter arrayConverter = converterService.getConverter(intArrayType);
		Object[] originalArray = {10, 20, 30, 40};
		Object arrayEmf = arrayConverter.convertValueToEMF(intArrayType, originalArray);
		Object arrayBack = arrayConverter.convertEMFToValue(intArrayType, arrayEmf);
		assertTrue(arrayBack instanceof byte[]);
		Object roundTripped = arrayConverter.convertValueToEMF(intArrayType, arrayBack);
		assertArrayEquals(new int[]{10, 20, 30, 40}, (int[]) roundTripped);
	}

	private EDataType createDataType(String instanceClassName) {
		EDataType dataType = ecoreFactory.createEDataType();
		dataType.setInstanceClassName(instanceClassName);
		return dataType;
	}
}
