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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcoreFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ComprehensiveTypeConverter.
 * Tests all supported type conversions to ensure proper EMF ↔ database value conversion.
 * 
 * @author Mark Hoffmann
 * @since 14.01.2025
 */
class ComprehensiveTypeConverterTest {

    private ComprehensiveTypeConverter converter;
    private EcoreFactory ecoreFactory;

    @BeforeEach
    void setUp() {
        converter = new ComprehensiveTypeConverter();
        ecoreFactory = EcoreFactory.eINSTANCE;
    }

    @Nested
    @DisplayName("Java 8+ Time API Conversions")
    class TimeApiTests {

        @Test
        @DisplayName("LocalDate conversion")
        void testLocalDateConversion() {
            EDataType dataType = createDataType("java.time.LocalDate");
            
            // Test database to EMF conversion
            Date sqlDate = Date.valueOf("2025-01-15");
            Object emfValue = converter.convertValueToEMF(dataType, sqlDate);
            assertEquals(LocalDate.of(2025, 1, 15), emfValue);
            
            // Test EMF to database conversion
            LocalDate localDate = LocalDate.of(2025, 1, 15);
            Object dbValue = converter.convertEMFToValue(dataType, localDate);
            assertEquals(sqlDate, dbValue);
            
            // Test string parsing
            Object parsedValue = converter.convertValueToEMF(dataType, "2025-01-15");
            assertEquals(LocalDate.of(2025, 1, 15), parsedValue);
        }

        @Test
        @DisplayName("LocalDateTime conversion")
        void testLocalDateTimeConversion() {
            EDataType dataType = createDataType("java.time.LocalDateTime");
            
            // Test database to EMF conversion
            Timestamp timestamp = Timestamp.valueOf("2025-01-15 14:30:00");
            Object emfValue = converter.convertValueToEMF(dataType, timestamp);
            assertEquals(LocalDateTime.of(2025, 1, 15, 14, 30, 0), emfValue);
            
            // Test EMF to database conversion
            LocalDateTime localDateTime = LocalDateTime.of(2025, 1, 15, 14, 30, 0);
            Object dbValue = converter.convertEMFToValue(dataType, localDateTime);
            assertEquals(timestamp, dbValue);
        }

        @Test
        @DisplayName("LocalTime conversion")
        void testLocalTimeConversion() {
            EDataType dataType = createDataType("java.time.LocalTime");
            
            // Test database to EMF conversion
            Time sqlTime = Time.valueOf("14:30:00");
            Object emfValue = converter.convertValueToEMF(dataType, sqlTime);
            assertEquals(LocalTime.of(14, 30, 0), emfValue);
            
            // Test EMF to database conversion
            LocalTime localTime = LocalTime.of(14, 30, 0);
            Object dbValue = converter.convertEMFToValue(dataType, localTime);
            assertEquals(sqlTime, dbValue);
        }

        @Test
        @DisplayName("Instant conversion")
        void testInstantConversion() {
            EDataType dataType = createDataType("java.time.Instant");
            
            // Test database to EMF conversion
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Object emfValue = converter.convertValueToEMF(dataType, timestamp);
            assertEquals(timestamp.toInstant(), emfValue);
            
            // Test EMF to database conversion
            Instant instant = Instant.now();
            Object dbValue = converter.convertEMFToValue(dataType, instant);
            assertEquals(Timestamp.from(instant), dbValue);
            
            // Test epoch millis conversion
            long epochMilli = System.currentTimeMillis();
            Object instantFromEpoch = converter.convertValueToEMF(dataType, epochMilli);
            assertEquals(Instant.ofEpochMilli(epochMilli), instantFromEpoch);
        }

        @Test
        @DisplayName("ZonedDateTime conversion from Timestamp uses UTC")
        void testZonedDateTimeFromTimestamp() {
            EDataType dataType = createDataType("java.time.ZonedDateTime");

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Object emfValue = converter.convertValueToEMF(dataType, timestamp);
            assertInstanceOf(ZonedDateTime.class, emfValue);
            // Timestamp has no timezone — converter uses UTC as convention
            assertEquals("UTC", ((ZonedDateTime) emfValue).getZone().getId());
        }

        @Test
        @DisplayName("ZonedDateTime EMF→DB stores as ISO-8601 string")
        void testZonedDateTimeToISOString() {
            EDataType dataType = createDataType("java.time.ZonedDateTime");

            ZonedDateTime zdt = ZonedDateTime.of(2026, 4, 14, 10, 30, 0, 0,
                    java.time.ZoneId.of("Europe/Berlin"));
            Object dbValue = converter.convertEMFToValue(dataType, zdt);
            assertInstanceOf(String.class, dbValue);
            assertTrue(((String) dbValue).contains("Europe/Berlin"));
        }

        @Test
        @DisplayName("ZonedDateTime round-trip preserves timezone")
        void testZonedDateTimeRoundTripPreservesTimezone() {
            EDataType dataType = createDataType("java.time.ZonedDateTime");

            ZonedDateTime original = ZonedDateTime.of(2026, 1, 15, 14, 30, 0, 0,
                    java.time.ZoneId.of("America/New_York"));

            // EMF → DB (ISO string)
            Object dbValue = converter.convertEMFToValue(dataType, original);
            // DB → EMF (parse ISO string)
            Object roundTripped = converter.convertValueToEMF(dataType, dbValue);

            assertInstanceOf(ZonedDateTime.class, roundTripped);
            ZonedDateTime result = (ZonedDateTime) roundTripped;
            assertEquals(original, result);
            assertEquals("America/New_York", result.getZone().getId());
        }

        @Test
        @DisplayName("OffsetDateTime conversion from String")
        void testOffsetDateTimeFromString() {
            EDataType dataType = createDataType("java.time.OffsetDateTime");

            String isoString = "2026-04-15T10:30:00+02:00";
            Object emfValue = converter.convertValueToEMF(dataType, isoString);
            assertInstanceOf(OffsetDateTime.class, emfValue);
            OffsetDateTime odt = (OffsetDateTime) emfValue;
            assertEquals(10, odt.getHour());
            assertEquals(ZoneOffset.ofHours(2), odt.getOffset());
        }

        @Test
        @DisplayName("OffsetDateTime EMF→DB stores as ISO-8601 string")
        void testOffsetDateTimeToISOString() {
            EDataType dataType = createDataType("java.time.OffsetDateTime");

            OffsetDateTime odt = OffsetDateTime.of(2026, 4, 15, 10, 30, 0, 0, ZoneOffset.ofHours(2));
            Object dbValue = converter.convertEMFToValue(dataType, odt);
            assertInstanceOf(String.class, dbValue);
            assertTrue(((String) dbValue).contains("+02:00"));
        }

        @Test
        @DisplayName("OffsetDateTime round-trip preserves offset")
        void testOffsetDateTimeRoundTrip() {
            EDataType dataType = createDataType("java.time.OffsetDateTime");

            OffsetDateTime original = OffsetDateTime.of(2026, 1, 15, 14, 30, 0, 0, ZoneOffset.ofHours(-5));
            Object dbValue = converter.convertEMFToValue(dataType, original);
            Object roundTripped = converter.convertValueToEMF(dataType, dbValue);

            assertEquals(original, roundTripped);
        }

        @Test
        @DisplayName("OffsetDateTime from Timestamp uses UTC")
        void testOffsetDateTimeFromTimestamp() {
            EDataType dataType = createDataType("java.time.OffsetDateTime");

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Object emfValue = converter.convertValueToEMF(dataType, timestamp);
            assertInstanceOf(OffsetDateTime.class, emfValue);
            assertEquals(ZoneOffset.UTC, ((OffsetDateTime) emfValue).getOffset());
        }

        @Test
        @DisplayName("Duration conversion")
        void testDurationConversion() {
            EDataType dataType = createDataType("java.time.Duration");
            
            // Test database to EMF conversion
            Long millis = 5000L; // 5 seconds
            Object emfValue = converter.convertValueToEMF(dataType, millis);
            assertEquals(Duration.ofMillis(5000), emfValue);
            
            // Test EMF to database conversion
            Duration duration = Duration.ofMinutes(2);
            Object dbValue = converter.convertEMFToValue(dataType, duration);
            assertEquals(120000L, dbValue); // 2 minutes in milliseconds
        }
    }

    @Nested
    @DisplayName("Common Type Conversions")
    class CommonTypeTests {

        @Test
        @DisplayName("URI conversion")
        void testURIConversion() {
            EDataType dataType = createDataType("java.net.URI");

            // DB → EMF
            Object emfValue = converter.convertValueToEMF(dataType, "https://example.com/path?q=1");
            assertInstanceOf(java.net.URI.class, emfValue);
            assertEquals(java.net.URI.create("https://example.com/path?q=1"), emfValue);

            // EMF → DB
            java.net.URI uri = java.net.URI.create("file:///tmp/test.txt");
            Object dbValue = converter.convertEMFToValue(dataType, uri);
            assertEquals("file:///tmp/test.txt", dbValue);
        }

        @Test
        @DisplayName("URI round-trip preserves all components")
        void testURIRoundTrip() {
            EDataType dataType = createDataType("java.net.URI");

            java.net.URI original = java.net.URI.create("https://user:pass@host:8080/path?q=v#frag");
            Object dbValue = converter.convertEMFToValue(dataType, original);
            Object roundTripped = converter.convertValueToEMF(dataType, dbValue);
            assertEquals(original, roundTripped);
        }

        @Test
        @DisplayName("URL conversion")
        void testURLConversion() throws Exception {
            EDataType dataType = createDataType("java.net.URL");

            // DB → EMF
            Object emfValue = converter.convertValueToEMF(dataType, "https://example.com/api");
            assertInstanceOf(java.net.URL.class, emfValue);

            // EMF → DB
            java.net.URL url = java.net.URI.create("https://example.com/api").toURL();
            Object dbValue = converter.convertEMFToValue(dataType, url);
            assertEquals("https://example.com/api", dbValue);
        }

        @Test
        @DisplayName("URL with malformed string returns null")
        void testURLMalformed() {
            EDataType dataType = createDataType("java.net.URL");

            Object result = converter.convertValueToEMF(dataType, "not a valid url %%%");
            assertNull(result);
        }

        @Test
        @DisplayName("UUID conversion")
        void testUUIDConversion() {
            EDataType dataType = createDataType("java.util.UUID");
            
            // Test database to EMF conversion
            String uuidString = "123e4567-e89b-12d3-a456-426614174000";
            Object emfValue = converter.convertValueToEMF(dataType, uuidString);
            assertEquals(UUID.fromString(uuidString), emfValue);
            
            // Test EMF to database conversion
            UUID uuid = UUID.randomUUID();
            Object dbValue = converter.convertEMFToValue(dataType, uuid);
            assertEquals(uuid.toString(), dbValue);
            
            // Test short name variant
            EDataType shortNameDataType = createDataType("UUID");
            assertTrue(converter.isConverterForType(shortNameDataType));
        }

        @Test
        @DisplayName("BigDecimal conversion")
        void testBigDecimalConversion() {
            EDataType dataType = createDataType("java.math.BigDecimal");
            
            // Test database to EMF conversion
            Double doubleValue = 123.45;
            Object emfValue = converter.convertValueToEMF(dataType, doubleValue);
            assertEquals(BigDecimal.valueOf(123.45), emfValue);
            
            // Test string conversion
            Object stringValue = converter.convertValueToEMF(dataType, "456.78");
            assertEquals(new BigDecimal("456.78"), stringValue);
            
            // Test EMF to database conversion — BigDecimal passes through natively
            BigDecimal bigDecimal = new BigDecimal("789.12");
            Object dbValue = converter.convertEMFToValue(dataType, bigDecimal);
            assertEquals(new BigDecimal("789.12"), dbValue);
        }

        @Test
        @DisplayName("BigInteger conversion")
        void testBigIntegerConversion() {
            EDataType dataType = createDataType("java.math.BigInteger");
            
            // Test database to EMF conversion
            Long longValue = 123456789L;
            Object emfValue = converter.convertValueToEMF(dataType, longValue);
            assertEquals(BigInteger.valueOf(123456789L), emfValue);
            
            // Test EMF to database conversion — BigInteger passes through natively
            BigInteger bigInteger = new BigInteger("987654321");
            Object dbValue = converter.convertEMFToValue(dataType, bigInteger);
            assertEquals(new BigInteger("987654321"), dbValue);
        }

        @Test
        @DisplayName("BigDecimal precision is preserved for large values")
        void testBigDecimalPrecisionPreserved() {
            EDataType dataType = createDataType("java.math.BigDecimal");

            // High-precision value that would lose precision via doubleValue()
            String preciseValue = "12345678901234567890.12345678901234567890";
            Object emfValue = converter.convertValueToEMF(dataType, preciseValue);
            assertEquals(new BigDecimal(preciseValue), emfValue);

            // Round-trip: EMF → DB → EMF should be identical
            Object dbValue = converter.convertEMFToValue(dataType, emfValue);
            assertInstanceOf(BigDecimal.class, dbValue);
            assertEquals(new BigDecimal(preciseValue), dbValue);
        }

        @Test
        @DisplayName("BigInteger handles values beyond int range")
        void testBigIntegerBeyondIntRange() {
            EDataType dataType = createDataType("java.math.BigInteger");

            // Value exceeding Integer.MAX_VALUE — would overflow with intValue()
            BigInteger largeValue = new BigInteger("99999999999999999999");
            Object dbValue = converter.convertEMFToValue(dataType, largeValue);
            assertInstanceOf(BigInteger.class, dbValue);
            assertEquals(largeValue, dbValue);

            // Round-trip from Long
            Long longVal = Long.MAX_VALUE;
            Object emfValue = converter.convertValueToEMF(dataType, longVal);
            assertEquals(BigInteger.valueOf(Long.MAX_VALUE), emfValue);
        }

        @Test
        @DisplayName("BigDecimal null handling")
        void testBigDecimalNullHandling() {
            EDataType dataType = createDataType("java.math.BigDecimal");
            assertNull(converter.convertValueToEMF(dataType, null));
            assertNull(converter.convertEMFToValue(dataType, null));
        }

        @Test
        @DisplayName("BigInteger null handling")
        void testBigIntegerNullHandling() {
            EDataType dataType = createDataType("java.math.BigInteger");
            assertNull(converter.convertValueToEMF(dataType, null));
            assertNull(converter.convertEMFToValue(dataType, null));
        }
    }

    @Nested
    @DisplayName("Array Conversions")
    class ArrayTests {

        @Test
        @DisplayName("Primitive int[] round-trip via BLOB encoding")
        void testPrimitiveIntArrayRoundTrip() {
            EDataType dataType = createDataType("int[]");

            // EMF → DB: int[] → byte[] (encoded)
            int[] original = {1, 2, 3, 4, 5};
            Object dbValue = converter.convertEMFToValue(dataType, original);
            assertInstanceOf(byte[].class, dbValue);

            // DB → EMF: byte[] → int[] (decoded)
            Object emfValue = converter.convertValueToEMF(dataType, dbValue);
            assertArrayEquals(original, (int[]) emfValue);
        }

        @Test
        @DisplayName("Primitive double[] round-trip via BLOB encoding")
        void testPrimitiveDoubleArrayRoundTrip() {
            EDataType dataType = createDataType("double[]");

            double[] original = {1.1, 2.2, 3.3};
            Object dbValue = converter.convertEMFToValue(dataType, original);
            assertInstanceOf(byte[].class, dbValue);

            Object emfValue = converter.convertValueToEMF(dataType, dbValue);
            assertArrayEquals(original, (double[]) emfValue, 0.001);
        }

        @Test
        @DisplayName("Primitive boolean[] round-trip via BLOB encoding")
        void testPrimitiveBooleanArrayRoundTrip() {
            EDataType dataType = createDataType("boolean[]");

            boolean[] original = {true, false, true};
            Object dbValue = converter.convertEMFToValue(dataType, original);
            assertInstanceOf(byte[].class, dbValue);

            Object emfValue = converter.convertValueToEMF(dataType, dbValue);
            assertArrayEquals(original, (boolean[]) emfValue);
        }

        @Test
        @DisplayName("All eight primitive array kinds round-trip through the BLOB encoding")
        void testAllPrimitiveArrayKindsRoundTrip() {
            assertArrayEquals(new int[]{Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE},
                    (int[]) roundTrip("int[]", new int[]{Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE}));
            assertArrayEquals(new double[]{Double.NaN, -0.0, Double.MAX_VALUE, Double.MIN_VALUE},
                    (double[]) roundTrip("double[]", new double[]{Double.NaN, -0.0, Double.MAX_VALUE, Double.MIN_VALUE}));
            assertArrayEquals(new float[]{1.5f, Float.NEGATIVE_INFINITY, Float.MIN_VALUE},
                    (float[]) roundTrip("float[]", new float[]{1.5f, Float.NEGATIVE_INFINITY, Float.MIN_VALUE}));
            assertArrayEquals(new long[]{Long.MIN_VALUE, 0L, Long.MAX_VALUE},
                    (long[]) roundTrip("long[]", new long[]{Long.MIN_VALUE, 0L, Long.MAX_VALUE}));
            assertArrayEquals(new boolean[]{true, false, false, true},
                    (boolean[]) roundTrip("boolean[]", new boolean[]{true, false, false, true}));
            assertArrayEquals(new byte[]{Byte.MIN_VALUE, 0, Byte.MAX_VALUE},
                    (byte[]) roundTrip("byte[]", new byte[]{Byte.MIN_VALUE, 0, Byte.MAX_VALUE}));
            assertArrayEquals(new char[]{'a', '\u00e4', Character.MAX_VALUE, '\0'},
                    (char[]) roundTrip("char[]", new char[]{'a', '\u00e4', Character.MAX_VALUE, '\0'}));
            assertArrayEquals(new short[]{Short.MIN_VALUE, 7, Short.MAX_VALUE},
                    (short[]) roundTrip("short[]", new short[]{Short.MIN_VALUE, 7, Short.MAX_VALUE}));
        }

        @Test
        @DisplayName("Empty primitive arrays round-trip and stay distinguishable from null")
        void testEmptyPrimitiveArrayRoundTrip() {
            Object result = roundTrip("int[]", new int[0]);
            assertInstanceOf(int[].class, result);
            assertEquals(0, ((int[]) result).length);
            assertNull(converter.convertValueToEMF(createDataType("int[]"), null));
        }

        @Test
        @DisplayName("BLOB encoding is a tag, a length and the raw elements - no Java serialization stream")
        void testBlobEncodingLayout() {
            byte[] blob = (byte[]) converter.convertEMFToValue(createDataType("int[]"), new int[]{1, 2});
            // tag(1) + length(4) + 2 * int(4)
            assertEquals(13, blob.length);
            assertEquals(1, blob[0]);
            assertArrayEquals(new byte[]{0, 0, 0, 2}, Arrays.copyOfRange(blob, 1, 5));
            assertArrayEquals(new byte[]{0, 0, 0, 1, 0, 0, 0, 2}, Arrays.copyOfRange(blob, 5, 13));
            // java.io.ObjectOutputStream always starts with the STREAM_MAGIC 0xACED
            assertFalse((blob[0] & 0xFF) == 0xAC && (blob[1] & 0xFF) == 0xED,
                    "BLOB must not be a Java serialization stream");
        }

        @Test
        @DisplayName("A Java-serialized object in the BLOB column is rejected without being deserialized")
        void testJavaSerializedPayloadIsRejected() throws IOException {
            EDataType dataType = createDataType("int[]");
            ReadObjectProbe.FIRED.set(false);

            assertNull(converter.convertValueToEMF(dataType, javaSerialize(new ReadObjectProbe())));
            assertFalse(ReadObjectProbe.FIRED.get(), "readObject() of a column-supplied class must never run");

            Map<String, String> map = new HashMap<>();
            map.put("pwned", "yes");
            assertNull(converter.convertValueToEMF(dataType, javaSerialize(map)));

            // even a Java-serialized int[] is not the storage format and is refused
            assertNull(converter.convertValueToEMF(dataType, javaSerialize(new int[]{1, 2, 3})));
        }

        @Test
        @DisplayName("A payload whose type tag does not match the declared EDataType is rejected")
        void testMismatchedTypeTagIsRejected() {
            byte[] doubleBlob = (byte[]) converter.convertEMFToValue(createDataType("double[]"), new double[]{1.0});
            assertNull(converter.convertValueToEMF(createDataType("int[]"), doubleBlob));

            byte[] unknownTag = doubleBlob.clone();
            unknownTag[0] = 42;
            assertNull(converter.convertValueToEMF(createDataType("double[]"), unknownTag));
            unknownTag[0] = 0;
            assertNull(converter.convertValueToEMF(createDataType("double[]"), unknownTag));
        }

        @Test
        @DisplayName("A payload whose length header does not fit the bytes is rejected")
        void testInconsistentLengthIsRejected() {
            EDataType dataType = createDataType("long[]");
            byte[] blob = (byte[]) converter.convertEMFToValue(dataType, new long[]{1L, 2L});

            byte[] truncated = Arrays.copyOf(blob, blob.length - 3);
            assertNull(converter.convertValueToEMF(dataType, truncated));

            byte[] padded = Arrays.copyOf(blob, blob.length + 1);
            assertNull(converter.convertValueToEMF(dataType, padded));

            byte[] hugeLength = blob.clone();
            hugeLength[1] = 0x7F; hugeLength[2] = (byte) 0xFF; hugeLength[3] = (byte) 0xFF; hugeLength[4] = (byte) 0xFF;
            assertNull(converter.convertValueToEMF(dataType, hugeLength));

            byte[] negativeLength = blob.clone();
            negativeLength[1] = (byte) 0xFF;
            assertNull(converter.convertValueToEMF(dataType, negativeLength));

            assertNull(converter.convertValueToEMF(dataType, new byte[0]));
            assertNull(converter.convertValueToEMF(dataType, new byte[]{4, 0, 0}));
        }

        private Object roundTrip(String instanceClassName, Object original) {
            EDataType dataType = createDataType(instanceClassName);
            Object dbValue = converter.convertEMFToValue(dataType, original);
            assertInstanceOf(byte[].class, dbValue);
            return converter.convertValueToEMF(dataType, dbValue);
        }

        private byte[] javaSerialize(Object o) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(o);
            }
            return baos.toByteArray();
        }

        @Test
        @DisplayName("Legacy Object[] input still works for backward compatibility")
        void testLegacyObjectArrayInput() {
            EDataType dataType = createDataType("int[]");

            Object[] objArray = {1, 2, 3};
            Object emfValue = converter.convertValueToEMF(dataType, objArray);
            assertArrayEquals(new int[]{1, 2, 3}, (int[]) emfValue);
        }

        @Test
        @DisplayName("Wrapper array conversion - Double[]")
        void testWrapperDoubleArrayConversion() {
            EDataType dataType = createDataType("java.lang.Double[]");
            
            // Test database to EMF conversion - delegates to existing ArrayConverter logic
            Object[] objArray = {1.1, 2.2, 3.3};
            Object emfValue = converter.convertValueToEMF(dataType, objArray);
            assertNotNull(emfValue);
            
            // Test EMF to database conversion
            Double[] doubleArray = {4.4, 5.5, 6.6};
            Object dbValue = converter.convertEMFToValue(dataType, doubleArray);
            assertEquals(doubleArray, dbValue);
        }
    }

    @Nested
    @DisplayName("EObject Reference Conversions")
    class EObjectTests {

        @Test
        @DisplayName("Non-containment EObject reference conversion")
        void testEObjectReferenceConversion() {
            EClass eClass = createEClass("TestClass");
            
            // Test database to EMF conversion (URI string)
            String uriString = "test://example/resource#fragment";
            Object emfValue = converter.convertValueToEMF(eClass, uriString);
            // Should return a URI object for cross-references
            assertNotNull(emfValue);
            
            // Test converter type detection
            assertTrue(converter.isConverterForType(eClass));
        }
    }

    @Nested
    @DisplayName("Converter Type Detection")
    class TypeDetectionTests {

        @Test
        @DisplayName("Time API type detection")
        void testTimeApiTypeDetection() {
            assertTrue(converter.isConverterForType(createDataType("java.time.LocalDate")));
            assertTrue(converter.isConverterForType(createDataType("java.time.LocalDateTime")));
            assertTrue(converter.isConverterForType(createDataType("java.time.LocalTime")));
            assertTrue(converter.isConverterForType(createDataType("java.time.Instant")));
            assertTrue(converter.isConverterForType(createDataType("java.time.ZonedDateTime")));
            assertTrue(converter.isConverterForType(createDataType("java.time.Duration")));
            assertTrue(converter.isConverterForType(createDataType("java.time.OffsetDateTime")));
        }

        @Test
        @DisplayName("Common type detection")
        void testCommonTypeDetection() {
            assertTrue(converter.isConverterForType(createDataType("java.net.URI")));
            assertTrue(converter.isConverterForType(createDataType("java.net.URL")));
            assertTrue(converter.isConverterForType(createDataType("java.util.UUID")));
            assertTrue(converter.isConverterForType(createDataType("UUID")));
            assertTrue(converter.isConverterForType(createDataType("java.math.BigDecimal")));
            assertTrue(converter.isConverterForType(createDataType("java.math.BigInteger")));
        }

        @Test
        @DisplayName("Array type detection")
        void testArrayTypeDetection() {
            // Primitive arrays
            assertTrue(converter.isConverterForType(createDataType("int[]")));
            assertTrue(converter.isConverterForType(createDataType("double[]")));
            assertTrue(converter.isConverterForType(createDataType("boolean[]")));
            
            // Wrapper arrays
            assertTrue(converter.isConverterForType(createDataType("java.lang.Double[]")));
            assertTrue(converter.isConverterForType(createDataType("java.lang.Integer[]")));
        }

        @Test
        @DisplayName("Unsupported type detection")
        void testUnsupportedTypeDetection() {
            assertFalse(converter.isConverterForType(createDataType("java.lang.UnsupportedType")));
            assertFalse(converter.isConverterForType(createDataType("some.unknown.Type")));
            
            // Abstract EClass should not be supported
            EClass abstractClass = createEClass("AbstractClass");
            abstractClass.setAbstract(true);
            assertFalse(converter.isConverterForType(abstractClass));
        }
    }

    @Nested
    @DisplayName("Null Value Handling")
    class NullValueTests {

        @Test
        @DisplayName("Null database values")
        void testNullDatabaseValues() {
            EDataType dataType = createDataType("java.time.LocalDate");
            
            Object result = converter.convertValueToEMF(dataType, null);
            assertNull(result);
        }

        @Test
        @DisplayName("Null EMF values")
        void testNullEMFValues() {
            EDataType dataType = createDataType("java.time.LocalDate");
            
            Object result = converter.convertEMFToValue(dataType, null);
            assertNull(result);
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Invalid string parsing")
        void testInvalidStringParsing() {
            EDataType dataType = createDataType("java.time.LocalDate");
            
            // Invalid date string should either throw exception or return original value
            // Let's test that it behaves consistently
            try {
                Object result = converter.convertValueToEMF(dataType, "invalid-date-string");
                // If no exception, should return original string
                assertEquals("invalid-date-string", result);
            } catch (Exception e) {
                // Exception is also acceptable behavior for invalid input
                assertTrue(e instanceof RuntimeException);
            }
        }

        @Test
        @DisplayName("Unsupported conversion")
        void testUnsupportedConversion() {
            EDataType dataType = createDataType("java.lang.UnsupportedType");
            
            String originalValue = "test-value";
            Object result = converter.convertValueToEMF(dataType, originalValue);
            assertEquals(originalValue, result); // Should return original value
        }
    }

    // Helper methods
    private EDataType createDataType(String instanceClassName) {
        EDataType dataType = ecoreFactory.createEDataType();
        dataType.setInstanceClassName(instanceClassName);
        return dataType;
    }

    private EClass createEClass(String name) {
        EClass eClass = ecoreFactory.createEClass();
        eClass.setName(name);
        return eClass;
    }
    /** Records whether Java deserialization ever instantiated it. */
    static class ReadObjectProbe implements Serializable {
        private static final long serialVersionUID = 1L;
        static final AtomicBoolean FIRED = new AtomicBoolean(false);

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            FIRED.set(true);
        }
    }
}
