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

import java.lang.reflect.Array;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.api.TypeConverter;

/**
 * Comprehensive type converter that delegates to specific converter implementations.
 * This converter is automatically registered by DefaultConverterService and provides
 * all common type conversions internally.
 * 
 * @author Mark Hoffmann
 * @since 14.01.2025
 */
public class ComprehensiveTypeConverter implements TypeConverter {

    private static final Logger logger = Logger.getLogger(ComprehensiveTypeConverter.class.getName());

    // Delegate converters - instantiated locally to avoid service overhead
    private final Map<String, InternalConverter> converters = new HashMap<>();
    
    public ComprehensiveTypeConverter() {
        initializeConverters();
    }

    private void initializeConverters() {
        // Java 8+ Time API
        converters.put("java.time.LocalDate", new LocalDateInternalConverter());
        converters.put("java.time.LocalDateTime", new LocalDateTimeInternalConverter());
        converters.put("java.time.LocalTime", new LocalTimeInternalConverter());
        converters.put("java.time.Instant", new InstantInternalConverter());
        converters.put("java.time.ZonedDateTime", new ZonedDateTimeInternalConverter());
        converters.put("java.time.Duration", new DurationInternalConverter());
        
        // Common types
        converters.put("java.util.UUID", new UUIDInternalConverter());
        converters.put("UUID", new UUIDInternalConverter());
        converters.put("java.math.BigDecimal", new BigDecimalInternalConverter());
        converters.put("java.math.BigInteger", new BigIntegerInternalConverter());
        
        // Array types - handled by type pattern matching
        converters.put("array", new ArrayInternalConverter());
        converters.put("primitiveArray", new PrimitiveArrayInternalConverter());
        
        // EObject references
        converters.put("eobject", new NonContainmentInternalConverter());
    }

    @Override
    public String getName() {
        return "comprehensive";
    }

    @Override
    public Object convertValueToEMF(EClassifier eDataType, Object value) {
        InternalConverter converter = findConverter(eDataType);
        if (converter != null) {
            return converter.convertValueToEMF(eDataType, value);
        }
        return value;
    }

    @Override
    public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
        InternalConverter converter = findConverter(eDataType);
        if (converter != null) {
            return converter.convertEMFToValue(eDataType, emfValue);
        }
        return emfValue;
    }

    @Override
    public boolean isConverterForType(EClassifier eDataType) {
        return findConverter(eDataType) != null;
    }

    private InternalConverter findConverter(EClassifier eDataType) {
        if (eDataType instanceof EDataType dataType) {
            String instanceTypeName = dataType.getInstanceTypeName();
            
            // Direct lookup
            InternalConverter converter = converters.get(instanceTypeName);
            if (converter != null) {
                return converter;
            }
            
            // Array type patterns
            if (instanceTypeName != null) {
                if (instanceTypeName.endsWith("[]")) {
                    if (isPrimitiveArrayType(instanceTypeName)) {
                        return converters.get("primitiveArray");
                    } else {
                        return converters.get("array");
                    }
                }
            }
        }
        
        // EClass (EObject) handling
        if (eDataType instanceof EClass eClass && !eClass.isAbstract() && !eClass.isInterface()) {
            return converters.get("eobject");
        }
        
        // EEnum handling
        if (eDataType instanceof EEnum) {
            return converters.get("enum");
        }
        
        return null;
    }

    private boolean isPrimitiveArrayType(String className) {
        return className.equals("int[]") || className.equals("double[]") || className.equals("float[]") ||
               className.equals("long[]") || className.equals("boolean[]") || className.equals("byte[]") ||
               className.equals("char[]") || className.equals("short[]");
    }

    // Internal converter interface to avoid TypeConverter interface conflicts
    private interface InternalConverter {
        Object convertValueToEMF(EClassifier eDataType, Object value);
        Object convertEMFToValue(EClassifier eDataType, Object emfValue);
    }

    // Internal converter implementations
    private static class LocalDateInternalConverter implements InternalConverter {
        @Override
        public Object convertValueToEMF(EClassifier eDataType, Object value) {
            if (value == null) return null;
            if (value instanceof Date sqlDate) return sqlDate.toLocalDate();
            if (value instanceof java.util.Date utilDate) return new Date(utilDate.getTime()).toLocalDate();
            if (value instanceof String dateStr) return LocalDate.parse(dateStr);
            return value;
        }

        @Override
        public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
            if (emfValue == null) return null;
            if (emfValue instanceof LocalDate localDate) return Date.valueOf(localDate);
            return emfValue;
        }
    }

    private static class LocalDateTimeInternalConverter implements InternalConverter {
        @Override
        public Object convertValueToEMF(EClassifier eDataType, Object value) {
            if (value == null) return null;
            if (value instanceof Timestamp timestamp) return timestamp.toLocalDateTime();
            if (value instanceof java.util.Date utilDate) return new Timestamp(utilDate.getTime()).toLocalDateTime();
            if (value instanceof String dateTimeStr) return LocalDateTime.parse(dateTimeStr);
            return value;
        }

        @Override
        public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
            if (emfValue == null) return null;
            if (emfValue instanceof LocalDateTime localDateTime) return Timestamp.valueOf(localDateTime);
            return emfValue;
        }
    }

    private static class LocalTimeInternalConverter implements InternalConverter {
        @Override
        public Object convertValueToEMF(EClassifier eDataType, Object value) {
            if (value == null) return null;
            if (value instanceof Time sqlTime) return sqlTime.toLocalTime();
            if (value instanceof java.util.Date utilDate) return new Time(utilDate.getTime()).toLocalTime();
            if (value instanceof String timeStr) return LocalTime.parse(timeStr);
            return value;
        }

        @Override
        public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
            if (emfValue == null) return null;
            if (emfValue instanceof LocalTime localTime) return Time.valueOf(localTime);
            return emfValue;
        }
    }

    private static class InstantInternalConverter implements InternalConverter {
        @Override
        public Object convertValueToEMF(EClassifier eDataType, Object value) {
            if (value == null) return null;
            if (value instanceof Timestamp timestamp) return timestamp.toInstant();
            if (value instanceof java.util.Date utilDate) return utilDate.toInstant();
            if (value instanceof Date sqlDate) return sqlDate.toInstant();
            if (value instanceof Long epochMilli) return Instant.ofEpochMilli(epochMilli);
            if (value instanceof String instantStr) return Instant.parse(instantStr);
            return value;
        }

        @Override
        public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
            if (emfValue == null) return null;
            if (emfValue instanceof Instant instant) return Timestamp.from(instant);
            return emfValue;
        }
    }

    private static class ZonedDateTimeInternalConverter implements InternalConverter {
        @Override
        public Object convertValueToEMF(EClassifier eDataType, Object value) {
            if (value == null) return null;
            if (value instanceof Timestamp timestamp) return timestamp.toInstant().atZone(ZoneId.systemDefault());
            if (value instanceof java.util.Date utilDate) return utilDate.toInstant().atZone(ZoneId.systemDefault());
            if (value instanceof String dateTimeStr) return ZonedDateTime.parse(dateTimeStr);
            return value;
        }

        @Override
        public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
            if (emfValue == null) return null;
            if (emfValue instanceof ZonedDateTime zonedDateTime) return Timestamp.from(zonedDateTime.toInstant());
            return emfValue;
        }
    }

    private static class DurationInternalConverter implements InternalConverter {
        @Override
        public Object convertValueToEMF(EClassifier eDataType, Object value) {
            if (value == null) return null;
            if (value instanceof Long millis) return Duration.ofMillis(millis);
            if (value instanceof Integer millis) return Duration.ofMillis(millis.longValue());
            if (value instanceof String durationStr) return Duration.parse(durationStr);
            return value;
        }

        @Override
        public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
            if (emfValue == null) return null;
            if (emfValue instanceof Duration duration) return duration.toMillis();
            return emfValue;
        }
    }

    private static class UUIDInternalConverter implements InternalConverter {
        @Override
        public Object convertValueToEMF(EClassifier eDataType, Object value) {
            if (value == null) return null;
            if (value instanceof String uuidStr) return UUID.fromString(uuidStr);
            if (value instanceof UUID) return value;
            return value;
        }

        @Override
        public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
            if (emfValue == null) return null;
            if (emfValue instanceof UUID uuid) return uuid.toString();
            return emfValue;
        }
    }

    private static class BigDecimalInternalConverter implements InternalConverter {
        @Override
        public Object convertValueToEMF(EClassifier eDataType, Object value) {
            if (value == null) return null;
            if (value instanceof Number number) return BigDecimal.valueOf(number.doubleValue());
            if (value instanceof String numberStr) return new BigDecimal(numberStr);
            return value;
        }

        @Override
        public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
            if (emfValue == null) return null;
            if (emfValue instanceof BigDecimal bigDecimal) return bigDecimal.doubleValue();
            return emfValue;
        }
    }

    private static class BigIntegerInternalConverter implements InternalConverter {
        @Override
        public Object convertValueToEMF(EClassifier eDataType, Object value) {
            if (value == null) return null;
            if (value instanceof Number number) return BigInteger.valueOf(number.longValue());
            if (value instanceof String numberStr) return new BigInteger(numberStr);
            return value;
        }

        @Override
        public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
            if (emfValue == null) return null;
            if (emfValue instanceof BigInteger bigInteger) return bigInteger.intValue();
            return emfValue;
        }
    }

    private static class NonContainmentInternalConverter implements InternalConverter {
        @Override
        public Object convertValueToEMF(EClassifier eDataType, Object value) {
            if (value instanceof String) {
                URI proxyURI = URI.createURI(value.toString());
                return proxyURI;
            }
            return value;
        }

        @Override
        public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
            if (emfValue instanceof EObject eo) {
                URI uri = EcoreUtil.getURI(eo);
                return uri.toString();
            }
            return null;
        }
    }

    private class ArrayInternalConverter implements InternalConverter {
        @Override
        public Object convertValueToEMF(EClassifier eDataType, Object value) {
            if (value instanceof Object[] objArray) {
                String instanceClassName = eDataType.getInstanceClassName();
                if (instanceClassName == null) {
                    logger.warning("EDataType has null instance class name. Not supported by array converter!");
                    return null;
                }
                String[] splitName = instanceClassName.split("(?<=\\[)]");
                int arrayDim = splitName.length;
                return createArray(objArray, arrayDim, instanceClassName);
            }
            logger.warning("Database object is not of type Object[]. Not supported by array converter!");
            return null;
        }

        @Override
        public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
            if (emfValue instanceof Object[]) {
                return emfValue;
            }
            return null;
        }

        private Object createArray(Object[] objArray, int arrayDim, String className) {
            return doCreateArray(objArray, arrayDim, className);
        }

        private Object[] doCreateArray(Object[] objArray, int arrayDim, String className) {
            Object[] array = getStartingArray(arrayDim, objArray.length, className);
            for (int i = 0; i < objArray.length; i++) {
                if (objArray[i] instanceof Object[]) {
                    array[i] = createArray((Object[]) objArray[i], arrayDim - 1, className);
                } else {
                    array[i] = objArray[i];
                }
            }
            return array;
        }

        private Object[] getStartingArray(int arrayDim, int firstDimSize, String type) {
            switch (arrayDim) {
                case 1:
                    if (type.startsWith("java.lang.Double")) return new Double[firstDimSize];
                    else if (type.startsWith("java.lang.Integer")) return new Integer[firstDimSize];
                    else if (type.startsWith("java.lang.Long")) return new Long[firstDimSize];
                    else if (type.startsWith("java.lang.Float")) return new Float[firstDimSize];
                    else if (type.startsWith("java.lang.String")) return new String[firstDimSize];
                    break;
                case 2:
                    if (type.startsWith("java.lang.Double")) return new Double[firstDimSize][];
                    else if (type.startsWith("java.lang.Integer")) return new Integer[firstDimSize][];
                    else if (type.startsWith("java.lang.Long")) return new Long[firstDimSize][];
                    else if (type.startsWith("java.lang.Float")) return new Float[firstDimSize][];
                    else if (type.startsWith("java.lang.String")) return new String[firstDimSize][];
                    break;
                // Add more dimensions as needed
            }
            logger.warning(String.format("Array Type %s or Dimension %s not yet supported!", type, arrayDim));
            return null;
        }
    }

    private static class PrimitiveArrayInternalConverter implements InternalConverter {
        @Override
        public Object convertValueToEMF(EClassifier eDataType, Object value) {
            if (value instanceof Object[] objArray) {
                String className = eDataType.getInstanceClassName();
                if (className == null) {
                    logger.warning("EDataType has null instance class name. Not supported by primitive array converter!");
                    return value;
                }
                return convertToPrimitiveArray(objArray, className);
            }
            return value;
        }

        @Override
        public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
            if (emfValue != null && emfValue.getClass().isArray() && emfValue.getClass().getComponentType().isPrimitive()) {
                return convertToObjectArray(emfValue);
            }
            return emfValue;
        }

        private Object convertToPrimitiveArray(Object[] objArray, String className) {
            switch (className) {
                case "int[]":
                    int[] intArray = new int[objArray.length];
                    for (int i = 0; i < objArray.length; i++) {
                        intArray[i] = objArray[i] instanceof Number ? ((Number) objArray[i]).intValue() : 0;
                    }
                    return intArray;
                case "double[]":
                    double[] doubleArray = new double[objArray.length];
                    for (int i = 0; i < objArray.length; i++) {
                        doubleArray[i] = objArray[i] instanceof Number ? ((Number) objArray[i]).doubleValue() : 0.0;
                    }
                    return doubleArray;
                case "boolean[]":
                    boolean[] boolArray = new boolean[objArray.length];
                    for (int i = 0; i < objArray.length; i++) {
                        boolArray[i] = objArray[i] instanceof Boolean ? (Boolean) objArray[i] : false;
                    }
                    return boolArray;
                // Add other primitive types as needed
                default:
                    return null;
            }
        }

        private Object[] convertToObjectArray(Object primitiveArray) {
            int length = Array.getLength(primitiveArray);
            Object[] objArray = new Object[length];
            for (int i = 0; i < length; i++) {
                objArray[i] = Array.get(primitiveArray, i);
            }
            return objArray;
        }
    }
}