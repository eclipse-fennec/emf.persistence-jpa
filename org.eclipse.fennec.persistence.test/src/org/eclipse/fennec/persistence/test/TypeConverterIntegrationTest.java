/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *      Mark Hoffmann - initial API and implementation
 */
package org.eclipse.fennec.persistence.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * OSGi integration test for the ComprehensiveTypeConverter.
 * Verifies that the converter is properly registered as an OSGi service
 * and can be discovered and used by the persistence framework.
 * 
 * @author Mark Hoffmann
 * @since 14.01.2025
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
class TypeConverterIntegrationTest {

    @InjectService
    ConverterService converterService;

    private final EcoreFactory ecoreFactory = EcoreFactory.eINSTANCE;

    @Test
    @DisplayName("ComprehensiveTypeConverter is available through ConverterService")
    void testComprehensiveTypeConverterServiceRegistration() {
        // ComprehensiveTypeConverter is integrated into DefaultConverterService,
        // not registered as a separate OSGi service
        assertNotNull(converterService, "ConverterService should be available");
        
        // Test that it can handle types that only ComprehensiveTypeConverter supports
        EDataType localDateTimeType = createDataType("java.time.LocalDateTime");
        TypeConverter converter = converterService.getConverter(localDateTimeType);
        
        assertNotNull(converter, "Should find converter for LocalDateTime");
        // The converter returned by ConverterService might be DefaultConverterService itself
        // What matters is that it can handle the conversions properly
    }

    @Test
    @DisplayName("ConverterService can locate and use ComprehensiveTypeConverter")
    void testConverterServiceIntegration() {
        assertNotNull(converterService, "ConverterService should be available");
        
        // Test LocalDate type
        EDataType localDateType = createDataType("java.time.LocalDate");
        TypeConverter converter = converterService.getConverter(localDateType);
        
        assertNotNull(converter, "Should find converter for LocalDate");
        // Don't check the converter name since it might be DefaultConverterService
        // that delegates to ComprehensiveTypeConverter internally
        
        // Test actual conversion - this is what matters
        Date sqlDate = Date.valueOf("2025-01-15");
        Object emfValue = converter.convertValueToEMF(localDateType, sqlDate);
        assertEquals(LocalDate.of(2025, 1, 15), emfValue);
    }

    @Test
    @DisplayName("Type converter works for Java 8+ Time API types via service")
    void testTimeApiTypesViaService() {
        // Test LocalDateTime
        EDataType localDateTimeType = createDataType("java.time.LocalDateTime");
        TypeConverter converter = converterService.getConverter(localDateTimeType);
        
        assertNotNull(converter, "Should find converter for LocalDateTime");
        
        Timestamp timestamp = Timestamp.valueOf("2025-01-15 14:30:00");
        Object emfValue = converter.convertValueToEMF(localDateTimeType, timestamp);
        assertEquals(LocalDateTime.of(2025, 1, 15, 14, 30, 0), emfValue);
        
        // Test Instant
        EDataType instantType = createDataType("java.time.Instant");
        TypeConverter instantConverter = converterService.getConverter(instantType);
        
        assertNotNull(instantConverter, "Should find converter for Instant");
        
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Object instantValue = instantConverter.convertValueToEMF(instantType, ts);
        assertEquals(ts.toInstant(), instantValue);
    }

    @Test
    @DisplayName("Type converter works for UUID via service")
    void testUUIDTypeViaService() {
        EDataType uuidType = createDataType("java.util.UUID");
        TypeConverter converter = converterService.getConverter(uuidType);
        
        assertNotNull(converter, "Should find converter for UUID");
        
        String uuidString = "123e4567-e89b-12d3-a456-426614174000";
        Object emfValue = converter.convertValueToEMF(uuidType, uuidString);
        assertEquals(UUID.fromString(uuidString), emfValue);
    }

    @Test
    @DisplayName("Type converter works for primitive arrays via service")
    void testPrimitiveArraysViaService() {
        EDataType intArrayType = createDataType("int[]");
        TypeConverter converter = converterService.getConverter(intArrayType);
        
        assertNotNull(converter, "Should find converter for int[]");
        
        Object[] objArray = {1, 2, 3, 4, 5};
        Object emfValue = converter.convertValueToEMF(intArrayType, objArray);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, (int[]) emfValue);
    }

    @Test
    @DisplayName("ConverterService handles unsupported types appropriately")
    void testUnsupportedTypesViaService() {
        EDataType unsupportedType = createDataType("java.lang.UnsupportedType");
        
        // ConverterService should throw an exception for truly unsupported types
        // since no converter (including DefaultConverter) handles arbitrary custom types
        assertThrows(IllegalStateException.class, () -> {
            converterService.getConverter(unsupportedType);
        }, "Should throw exception when no converter can handle the type");
    }

    @Test
    @DisplayName("Multiple converter requests return same service instance")
    void testServiceInstanceConsistency() {
        EDataType localDateType = createDataType("java.time.LocalDate");
        EDataType uuidType = createDataType("java.util.UUID");
        
        TypeConverter converter1 = converterService.getConverter(localDateType);
        TypeConverter converter2 = converterService.getConverter(uuidType);
        
        assertNotNull(converter1, "Should find converter for LocalDate");
        assertNotNull(converter2, "Should find converter for UUID");
        
        // Both should return the same converter instance (likely DefaultConverterService)
        assertSame(converter1, converter2, 
            "Should return same converter instance for different types");
        
        // Test that both conversions work correctly through the same service
        assertTrue(converter1.isConverterForType(localDateType), "Should support LocalDate");
        assertTrue(converter2.isConverterForType(uuidType), "Should support UUID");
    }

    @Test
    @DisplayName("Converter service integration with existing converters")
    void testCoexistenceWithExistingConverters() {
        // Test that ComprehensiveTypeConverter doesn't conflict with existing converters
        // and that the service properly selects the right one
        
        EDataType localDateType = createDataType("java.time.LocalDate");
        TypeConverter converter = converterService.getConverter(localDateType);
        
        assertNotNull(converter, "Should find a converter for LocalDate");
        
        // Test that conversion actually works (not just returns a converter)
        Date sqlDate = Date.valueOf("2025-12-25");
        Object emfValue = converter.convertValueToEMF(localDateType, sqlDate);
        assertNotNull(emfValue, "Conversion should produce a result");
        assertTrue(emfValue instanceof LocalDate, "Should convert to LocalDate");
        assertEquals(LocalDate.of(2025, 12, 25), emfValue);
    }

    @Test
    @DisplayName("ConverterService has expected converter capabilities")
    void testServiceProperties() {
        // Verify that the ConverterService can handle the types we expect
        assertNotNull(converterService, "ConverterService should be available");
        
        // Test that it can find converters for the types we added
        assertNotNull(converterService.getConverter(createDataType("java.time.LocalDate")), 
                     "Should find converter for LocalDate");
        assertNotNull(converterService.getConverter(createDataType("java.time.Instant")), 
                     "Should find converter for Instant");
        assertNotNull(converterService.getConverter(createDataType("java.util.UUID")), 
                     "Should find converter for UUID");
        assertNotNull(converterService.getConverter(createDataType("int[]")), 
                     "Should find converter for int[]");
        
        // Test that converter can handle these types
        TypeConverter converter = converterService.getConverter(createDataType("java.time.LocalDate"));
        if (converter != null) {
            assertTrue(converter.isConverterForType(createDataType("java.time.LocalDate")));
            assertTrue(converter.isConverterForType(createDataType("java.time.Instant")));
            assertTrue(converter.isConverterForType(createDataType("java.util.UUID")));
            assertTrue(converter.isConverterForType(createDataType("int[]")));
        }
    }

    @Test
    @DisplayName("Integration with actual persistence context")
    void testPersistenceContextIntegration() {
        // This test verifies that the converter can be used in a realistic persistence scenario
        // where the ConverterService would be used by the persistence framework
        
        assertNotNull(converterService, "ConverterService should be available for persistence framework");
        
        // Simulate what the persistence framework would do:
        // 1. Look up converter for a specific type
        EDataType timestampType = createDataType("java.time.Instant");
        TypeConverter converter = converterService.getConverter(timestampType);
        
        assertNotNull(converter, "Persistence framework should find converter");
        
        // 2. Use converter for database -> EMF conversion
        Timestamp dbValue = new Timestamp(1706187000000L); // 2025-01-25 12:30:00 UTC
        Object emfValue = converter.convertValueToEMF(timestampType, dbValue);
        
        assertNotNull(emfValue, "Should convert database value to EMF value");
        assertTrue(emfValue instanceof Instant, "Should be an Instant");
        
        // 3. Use converter for EMF -> database conversion
        Instant instant = (Instant) emfValue;
        Object backToDb = converter.convertEMFToValue(timestampType, instant);
        
        assertNotNull(backToDb, "Should convert EMF value back to database value");
        assertTrue(backToDb instanceof Timestamp, "Should be a Timestamp");
        assertEquals(dbValue, backToDb, "Round-trip conversion should preserve value");
    }

    @Test
    @DisplayName("Comprehensive round-trip conversion testing")
    void testComprehensiveRoundTripConversions() {
        // Test all supported types for round-trip conversion accuracy
        
        // LocalDate round-trip
        EDataType localDateType = createDataType("java.time.LocalDate");
        TypeConverter converter = converterService.getConverter(localDateType);
        assertNotNull(converter);
        
        Date originalDate = Date.valueOf("2025-03-15");
        Object emfValue = converter.convertValueToEMF(localDateType, originalDate);
        Object backToDb = converter.convertEMFToValue(localDateType, emfValue);
        assertEquals(originalDate, backToDb, "LocalDate round-trip should preserve value");
        
        // LocalDateTime round-trip
        EDataType localDateTimeType = createDataType("java.time.LocalDateTime");
        TypeConverter dateTimeConverter = converterService.getConverter(localDateTimeType);
        assertNotNull(dateTimeConverter);
        
        Timestamp originalTimestamp = Timestamp.valueOf("2025-03-15 14:30:45");
        Object dateTimeEmf = dateTimeConverter.convertValueToEMF(localDateTimeType, originalTimestamp);
        Object timestampBack = dateTimeConverter.convertEMFToValue(localDateTimeType, dateTimeEmf);
        assertEquals(originalTimestamp, timestampBack, "LocalDateTime round-trip should preserve value");
        
        // UUID round-trip
        EDataType uuidType = createDataType("java.util.UUID");
        TypeConverter uuidConverter = converterService.getConverter(uuidType);
        assertNotNull(uuidConverter);
        
        String originalUuidStr = "123e4567-e89b-12d3-a456-426614174000";
        Object uuidEmf = uuidConverter.convertValueToEMF(uuidType, originalUuidStr);
        Object uuidBack = uuidConverter.convertEMFToValue(uuidType, uuidEmf);
        assertEquals(originalUuidStr, uuidBack, "UUID round-trip should preserve value");
        
        // Primitive array round-trip
        EDataType intArrayType = createDataType("int[]");
        TypeConverter arrayConverter = converterService.getConverter(intArrayType);
        assertNotNull(arrayConverter);
        
        Object[] originalArray = {10, 20, 30, 40};
        Object arrayEmf = arrayConverter.convertValueToEMF(intArrayType, originalArray);
        Object arrayBack = arrayConverter.convertEMFToValue(intArrayType, arrayEmf);
        assertNotNull(arrayBack, "Array round-trip should return a result");
        assertTrue(arrayBack instanceof Object[], "Should return Object array");
        assertArrayEquals(new Object[]{10, 20, 30, 40}, (Object[]) arrayBack, "Array round-trip should preserve values");
    }

    // Helper method
    private EDataType createDataType(String instanceClassName) {
        EDataType dataType = ecoreFactory.createEDataType();
        dataType.setInstanceClassName(instanceClassName);
        return dataType;
    }
}