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

import java.sql.Date;
import java.time.LocalDate;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.fennec.persistence.api.TypeConverter;

/**
 * Converter for LocalDate to/from SQL Date.
 * Handles conversion between Java 8+ LocalDate and database DATE columns.
 * 
 * @author Mark Hoffmann
 * @since 14.01.2025
 */
// @Component - Now handled by ComprehensiveTypeConverter
public class LocalDateConverter implements TypeConverter {

    @Override
    public String getName() {
        return "localDate";
    }

    @Override
    public Object convertValueToEMF(EClassifier eDataType, Object value) {
        if (value == null) {
            return null;
        }
        
        if (value instanceof Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        
        if (value instanceof java.util.Date utilDate) {
            return new Date(utilDate.getTime()).toLocalDate();
        }
        
        if (value instanceof String dateStr) {
            return LocalDate.parse(dateStr);
        }
        
        return value;
    }

    @Override
    public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
        if (emfValue == null) {
            return null;
        }
        
        if (emfValue instanceof LocalDate localDate) {
            return Date.valueOf(localDate);
        }
        
        return emfValue;
    }

    @Override
    public boolean isConverterForType(EClassifier eDataType) {
        if (eDataType instanceof EDataType dataType) {
            String instanceTypeName = dataType.getInstanceTypeName();
            return "java.time.LocalDate".equals(instanceTypeName);
        }
        return false;
    }
}