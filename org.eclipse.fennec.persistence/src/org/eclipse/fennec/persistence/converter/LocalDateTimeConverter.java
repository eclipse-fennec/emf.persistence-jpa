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

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.fennec.persistence.api.TypeConverter;

/**
 * Converter for LocalDateTime to/from SQL Timestamp.
 * Handles conversion between Java 8+ LocalDateTime and database TIMESTAMP columns.
 * 
 * @author Mark Hoffmann
 * @since 14.01.2025
 */
// @Component - Now handled by ComprehensiveTypeConverter
public class LocalDateTimeConverter implements TypeConverter {

    @Override
    public String getName() {
        return "localDateTime";
    }

    @Override
    public Object convertValueToEMF(EClassifier eDataType, Object value) {
        if (value == null) {
            return null;
        }
        
        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }
        
        if (value instanceof java.util.Date utilDate) {
            return new Timestamp(utilDate.getTime()).toLocalDateTime();
        }
        
        if (value instanceof String dateTimeStr) {
            return LocalDateTime.parse(dateTimeStr);
        }
        
        return value;
    }

    @Override
    public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
        if (emfValue == null) {
            return null;
        }
        
        if (emfValue instanceof LocalDateTime localDateTime) {
            return Timestamp.valueOf(localDateTime);
        }
        
        return emfValue;
    }

    @Override
    public boolean isConverterForType(EClassifier eDataType) {
        if (eDataType instanceof EDataType dataType) {
            String instanceTypeName = dataType.getInstanceTypeName();
            return "java.time.LocalDateTime".equals(instanceTypeName);
        }
        return false;
    }
}