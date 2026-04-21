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

import java.sql.Timestamp;
import java.time.Instant;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.fennec.persistence.api.TypeConverter;

/**
 * Converter for Instant to/from SQL Timestamp.
 * Handles conversion between Java 8+ Instant and database TIMESTAMP columns.
 * 
 * @author Mark Hoffmann
 * @since 14.01.2025
 */
// @Component - Now handled by ComprehensiveTypeConverter
public class InstantConverter implements TypeConverter {

    @Override
    public String getName() {
        return "instant";
    }

    @Override
    public Object convertValueToEMF(EClassifier eDataType, Object value) {
        if (isNull(value)) {
            return null;
        }
        
        if (value instanceof Timestamp timestamp) {
            return timestamp.toInstant();
        }
        
        if (value instanceof java.util.Date utilDate) {
            return utilDate.toInstant();
        }
        
        if (value instanceof java.sql.Date sqlDate) {
            return sqlDate.toInstant();
        }
        
        if (value instanceof Long epochMilli) {
            return Instant.ofEpochMilli(epochMilli);
        }
        
        if (value instanceof String instantStr) {
            return Instant.parse(instantStr);
        }
        
        return value;
    }

    @Override
    public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
        if (isNull(emfValue)) {
            return null;
        }
        
        if (emfValue instanceof Instant instant) {
            return Timestamp.from(instant);
        }
        
        return emfValue;
    }

    @Override
    public boolean isConverterForType(EClassifier eDataType) {
        if (eDataType instanceof EDataType dataType) {
            String instanceTypeName = dataType.getInstanceTypeName();
            return "java.time.Instant".equals(instanceTypeName);
        }
        return false;
    }
}