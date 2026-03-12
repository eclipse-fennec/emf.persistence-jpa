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

import java.sql.Time;
import java.time.LocalTime;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.fennec.persistence.api.TypeConverter;

/**
 * Converter for LocalTime to/from SQL Time.
 * Handles conversion between Java 8+ LocalTime and database TIME columns.
 * 
 * @author Mark Hoffmann
 * @since 14.01.2025
 */
// @Component - Now handled by ComprehensiveTypeConverter
public class LocalTimeConverter implements TypeConverter {

    @Override
    public String getName() {
        return "localTime";
    }

    @Override
    public Object convertValueToEMF(EClassifier eDataType, Object value) {
        if (value == null) {
            return null;
        }
        
        if (value instanceof Time sqlTime) {
            return sqlTime.toLocalTime();
        }
        
        if (value instanceof java.util.Date utilDate) {
            return new Time(utilDate.getTime()).toLocalTime();
        }
        
        if (value instanceof String timeStr) {
            return LocalTime.parse(timeStr);
        }
        
        return value;
    }

    @Override
    public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
        if (emfValue == null) {
            return null;
        }
        
        if (emfValue instanceof LocalTime localTime) {
            return Time.valueOf(localTime);
        }
        
        return emfValue;
    }

    @Override
    public boolean isConverterForType(EClassifier eDataType) {
        if (eDataType instanceof EDataType dataType) {
            String instanceTypeName = dataType.getInstanceTypeName();
            return "java.time.LocalTime".equals(instanceTypeName);
        }
        return false;
    }
}