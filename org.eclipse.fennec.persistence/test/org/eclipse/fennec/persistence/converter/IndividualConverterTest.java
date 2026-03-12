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
package org.eclipse.fennec.persistence.converter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcoreFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for individual converter implementations.
 * Tests standalone converter behavior for specific type conversions.
 * 
 * @author Mark Hoffmann
 * @since 14.01.2025
 */
class IndividualConverterTest {

    private final EcoreFactory ecoreFactory = EcoreFactory.eINSTANCE;

    @Nested
    @DisplayName("LocalDateConverter Tests")
    class LocalDateConverterTests {

        @Test
        @DisplayName("LocalDate converter standalone functionality")
        void testLocalDateConverter() {
            LocalDateConverter converter = new LocalDateConverter();
            EDataType dataType = createDataType("java.time.LocalDate");
            
            assertEquals("localDate", converter.getName());
            assertTrue(converter.isConverterForType(dataType));
            
            // Test conversion
            Date sqlDate = Date.valueOf("2025-01-15");
            Object emfValue = converter.convertValueToEMF(dataType, sqlDate);
            assertEquals(LocalDate.of(2025, 1, 15), emfValue);
            
            LocalDate localDate = LocalDate.of(2025, 1, 15);
            Object dbValue = converter.convertEMFToValue(dataType, localDate);
            assertEquals(sqlDate, dbValue);
        }
    }

    @Nested
    @DisplayName("LocalDateTimeConverter Tests")
    class LocalDateTimeConverterTests {

        @Test
        @DisplayName("LocalDateTime converter standalone functionality")
        void testLocalDateTimeConverter() {
            LocalDateTimeConverter converter = new LocalDateTimeConverter();
            EDataType dataType = createDataType("java.time.LocalDateTime");
            
            assertEquals("localDateTime", converter.getName());
            assertTrue(converter.isConverterForType(dataType));
            
            // Test conversion
            Timestamp timestamp = Timestamp.valueOf("2025-01-15 14:30:00");
            Object emfValue = converter.convertValueToEMF(dataType, timestamp);
            assertEquals(LocalDateTime.of(2025, 1, 15, 14, 30, 0), emfValue);
        }
    }

    @Nested
    @DisplayName("InstantConverter Tests")
    class InstantConverterTests {

        @Test
        @DisplayName("Instant converter standalone functionality")
        void testInstantConverter() {
            InstantConverter converter = new InstantConverter();
            EDataType dataType = createDataType("java.time.Instant");
            
            assertEquals("instant", converter.getName());
            assertTrue(converter.isConverterForType(dataType));
            
            // Test conversion
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Object emfValue = converter.convertValueToEMF(dataType, timestamp);
            assertEquals(timestamp.toInstant(), emfValue);
        }
    }

    @Nested
    @DisplayName("UUIDConverter Tests")
    class UUIDConverterTests {

        @Test
        @DisplayName("UUID converter standalone functionality")
        void testUUIDConverter() {
            UUIDConverter converter = new UUIDConverter();
            EDataType dataType = createDataType("java.util.UUID");
            
            assertEquals("uuid", converter.getName());
            assertTrue(converter.isConverterForType(dataType));
            
            // Test conversion
            String uuidString = "123e4567-e89b-12d3-a456-426614174000";
            Object emfValue = converter.convertValueToEMF(dataType, uuidString);
            assertEquals(UUID.fromString(uuidString), emfValue);
            
            UUID uuid = UUID.randomUUID();
            Object dbValue = converter.convertEMFToValue(dataType, uuid);
            assertEquals(uuid.toString(), dbValue);
        }
    }

    @Nested
    @DisplayName("PrimitiveArrayConverter Tests")
    class PrimitiveArrayConverterTests {

        @Test
        @DisplayName("PrimitiveArray converter standalone functionality")
        void testPrimitiveArrayConverter() {
            PrimitiveArrayConverter converter = new PrimitiveArrayConverter();
            EDataType dataType = createDataType("int[]");
            
            assertEquals("primitiveArray", converter.getName());
            assertTrue(converter.isConverterForType(dataType));
            
            // Test conversion
            Object[] objArray = {1, 2, 3, 4, 5};
            Object emfValue = converter.convertValueToEMF(dataType, objArray);
            assertArrayEquals(new int[]{1, 2, 3, 4, 5}, (int[]) emfValue);
            
            int[] intArray = {10, 20, 30};
            Object dbValue = converter.convertEMFToValue(dataType, intArray);
            assertArrayEquals(new Object[]{10, 20, 30}, (Object[]) dbValue);
        }

        @Test
        @DisplayName("All primitive array types supported")
        void testAllPrimitiveArrayTypes() {
            PrimitiveArrayConverter converter = new PrimitiveArrayConverter();
            
            assertTrue(converter.isConverterForType(createDataType("int[]")));
            assertTrue(converter.isConverterForType(createDataType("double[]")));
            assertTrue(converter.isConverterForType(createDataType("float[]")));
            assertTrue(converter.isConverterForType(createDataType("long[]")));
            assertTrue(converter.isConverterForType(createDataType("boolean[]")));
            assertTrue(converter.isConverterForType(createDataType("byte[]")));
            assertTrue(converter.isConverterForType(createDataType("char[]")));
            assertTrue(converter.isConverterForType(createDataType("short[]")));
            
            // Should not support wrapper arrays
            assertFalse(converter.isConverterForType(createDataType("java.lang.Integer[]")));
        }
    }

    @Nested
    @DisplayName("NonContainmentConverter Tests")
    class NonContainmentConverterTests {

        @Test
        @DisplayName("NonContainment converter standalone functionality")
        void testNonContainmentConverter() {
            NonContainmentConverter converter = new NonContainmentConverter();
            
            assertEquals("nonContainment", converter.getName());
            
            // Test with concrete EClass
            org.eclipse.emf.ecore.EClass eClass = ecoreFactory.createEClass();
            eClass.setName("TestClass");
            assertTrue(converter.isConverterForType(eClass));
            
            // Test conversion
            String uriString = "test://example/resource#fragment";
            Object emfValue = converter.convertValueToEMF(eClass, uriString);
            assertNotNull(emfValue);
        }
    }

    @Nested
    @DisplayName("ArrayConverter Tests") 
    class ArrayConverterTests {

        @Test
        @DisplayName("ArrayConverter supports wrapper arrays")
        void testArrayConverterSupportsWrapperArrays() {
            ArrayConverter converter = new ArrayConverter();
            
            assertEquals("array", converter.getName());
            
            // Test type detection - ArrayConverter checks for array types ending with []
            assertTrue(converter.isConverterForType(createDataType("java.lang.Double[]")));
            assertTrue(converter.isConverterForType(createDataType("java.lang.Integer[]")));
            assertTrue(converter.isConverterForType(createDataType("java.lang.String[]")));
            
            // ArrayConverter actually supports any array type ending with [], including primitive arrays
            // The logic in ArrayConverter.isConverterForType() just checks for className.endsWith("[]")
            assertTrue(converter.isConverterForType(createDataType("int[]")));
            assertTrue(converter.isConverterForType(createDataType("double[]")));
            
            // Should not support non-array types
            assertFalse(converter.isConverterForType(createDataType("java.lang.String")));
            assertFalse(converter.isConverterForType(createDataType("int")));
        }
    }

    @Nested
    @DisplayName("Converter Naming and Identity")
    class ConverterIdentityTests {

        @Test
        @DisplayName("All converters have unique names")
        void testUniqueConverterNames() {
            
            // Verify each converter has its expected name
            assertEquals("localDate", new LocalDateConverter().getName());
            assertEquals("localDateTime", new LocalDateTimeConverter().getName());
            assertEquals("localTime", new LocalTimeConverter().getName());
            assertEquals("instant", new InstantConverter().getName());
            assertEquals("zonedDateTime", new ZonedDateTimeConverter().getName());
            assertEquals("duration", new DurationConverter().getName());
            assertEquals("uuid", new UUIDConverter().getName());
            assertEquals("primitiveArray", new PrimitiveArrayConverter().getName());
            assertEquals("nonContainment", new NonContainmentConverter().getName());
            assertEquals("array", new ArrayConverter().getName());
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCaseTests {

        @Test
        @DisplayName("Null handling in individual converters")
        void testNullHandling() {
            LocalDateConverter converter = new LocalDateConverter();
            EDataType dataType = createDataType("java.time.LocalDate");
            
            assertNull(converter.convertValueToEMF(dataType, null));
            assertNull(converter.convertEMFToValue(dataType, null));
        }

        @Test
        @DisplayName("Invalid type detection")
        void testInvalidTypeDetection() {
            LocalDateConverter converter = new LocalDateConverter();
            EDataType wrongType = createDataType("java.lang.String");
            
            assertFalse(converter.isConverterForType(wrongType));
        }
    }

    // Helper method
    private EDataType createDataType(String instanceClassName) {
        EDataType dataType = ecoreFactory.createEDataType();
        dataType.setInstanceClassName(instanceClassName);
        return dataType;
    }
}