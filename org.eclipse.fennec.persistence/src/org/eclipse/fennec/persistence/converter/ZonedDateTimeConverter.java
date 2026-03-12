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

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.fennec.persistence.api.TypeConverter;

/**
 * Converter for ZonedDateTime to/from SQL Timestamp.
 * Handles conversion between Java 8+ ZonedDateTime and database TIMESTAMP columns.
 * Note: Timezone information is lost during conversion to database timestamp.
 * 
 * @author Mark Hoffmann
 * @since 14.01.2025
 */
// @Component - Now handled by ComprehensiveTypeConverter
public class ZonedDateTimeConverter implements TypeConverter {

    @Override
    public String getName() {
        return "zonedDateTime";
    }

    @Override
    public Object convertValueToEMF(EClassifier eDataType, Object value) {
        if (value == null) {
            return null;
        }
        
        if (value instanceof Timestamp timestamp) {
            // Convert to system default timezone - this is a limitation as timezone info is lost in DB
            return timestamp.toInstant().atZone(ZoneId.systemDefault());
        }
        
        if (value instanceof java.util.Date utilDate) {
            return utilDate.toInstant().atZone(ZoneId.systemDefault());
        }
        
        if (value instanceof String dateTimeStr) {
            return ZonedDateTime.parse(dateTimeStr);
        }
        
        return value;
    }

    @Override
    public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
        if (emfValue == null) {
            return null;
        }
        
        if (emfValue instanceof ZonedDateTime zonedDateTime) {
            return Timestamp.from(zonedDateTime.toInstant());
        }
        
        return emfValue;
    }

    @Override
    public boolean isConverterForType(EClassifier eDataType) {
        if (eDataType instanceof EDataType dataType) {
            String instanceTypeName = dataType.getInstanceTypeName();
            return "java.time.ZonedDateTime".equals(instanceTypeName);
        }
        return false;
    }
}