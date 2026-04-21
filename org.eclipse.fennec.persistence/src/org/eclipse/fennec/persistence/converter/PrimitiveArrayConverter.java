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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.lang.reflect.Array;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.fennec.persistence.api.TypeConverter;

/**
 * Converter for primitive arrays (int[], double[], boolean[], etc.).
 * Handles conversion between primitive arrays and database array columns.
 * 
 * @author Mark Hoffmann
 * @since 14.01.2025
 */
// @Component - Now handled by ComprehensiveTypeConverter
public class PrimitiveArrayConverter implements TypeConverter {

    private static final Logger logger = Logger.getLogger(PrimitiveArrayConverter.class.getName());

    @Override
    public String getName() {
        return "primitiveArray";
    }

    @Override
    public Object convertValueToEMF(EClassifier eDataType, Object databaseValue) {
        if (isNull(databaseValue)) {
            return null;
        }
        
        if (databaseValue instanceof Object[] objArray) {
            String className = eDataType.getInstanceClassName();
            if (isNull(className)) {
                logger.warning("EDataType has null instance class name. Cannot convert to primitive array.");
                return databaseValue;
            }
            return convertToPrimitiveArray(objArray, className);
        }
        
        logger.warning("Database value is not an Object array, cannot convert to primitive array");
        return databaseValue;
    }

    @Override
    public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
        if (isNull(emfValue)) {
            return null;
        }
        
        if (emfValue.getClass().isArray() && emfValue.getClass().getComponentType().isPrimitive()) {
            return convertToObjectArray(emfValue);
        }
        
        return emfValue;
    }

    @Override
    public boolean isConverterForType(EClassifier eDataType) {
        if (eDataType instanceof EDataType dataType) {
            String className = dataType.getInstanceClassName();
            return nonNull(className) && isPrimitiveArrayType(className);
        }
        return false;
    }

    private boolean isPrimitiveArrayType(String className) {
        return className.equals("int[]") ||
               className.equals("double[]") ||
               className.equals("float[]") ||
               className.equals("long[]") ||
               className.equals("boolean[]") ||
               className.equals("byte[]") ||
               className.equals("char[]") ||
               className.equals("short[]");
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
                
            case "float[]":
                float[] floatArray = new float[objArray.length];
                for (int i = 0; i < objArray.length; i++) {
                    floatArray[i] = objArray[i] instanceof Number ? ((Number) objArray[i]).floatValue() : 0.0f;
                }
                return floatArray;
                
            case "long[]":
                long[] longArray = new long[objArray.length];
                for (int i = 0; i < objArray.length; i++) {
                    longArray[i] = objArray[i] instanceof Number ? ((Number) objArray[i]).longValue() : 0L;
                }
                return longArray;
                
            case "boolean[]":
                boolean[] boolArray = new boolean[objArray.length];
                for (int i = 0; i < objArray.length; i++) {
                    boolArray[i] = objArray[i] instanceof Boolean ? (Boolean) objArray[i] : false;
                }
                return boolArray;
                
            case "byte[]":
                byte[] byteArray = new byte[objArray.length];
                for (int i = 0; i < objArray.length; i++) {
                    byteArray[i] = objArray[i] instanceof Number ? ((Number) objArray[i]).byteValue() : 0;
                }
                return byteArray;
                
            case "char[]":
                char[] charArray = new char[objArray.length];
                for (int i = 0; i < objArray.length; i++) {
                    if (objArray[i] instanceof Character charValue) {
                        charArray[i] = charValue;
                    } else if (objArray[i] instanceof String strValue && !strValue.isEmpty()) {
                        charArray[i] = strValue.charAt(0);
                    } else {
                        charArray[i] = '\0';
                    }
                }
                return charArray;
                
            case "short[]":
                short[] shortArray = new short[objArray.length];
                for (int i = 0; i < objArray.length; i++) {
                    shortArray[i] = objArray[i] instanceof Number ? ((Number) objArray[i]).shortValue() : 0;
                }
                return shortArray;
                
            default:
                logger.warning("Unsupported primitive array type: " + className);
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