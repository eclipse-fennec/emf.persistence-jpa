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

import java.time.Duration;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.fennec.persistence.api.TypeConverter;

/**
 * Converter for Duration to/from Long (milliseconds).
 * Handles conversion between Java 8+ Duration and database BIGINT columns.
 * 
 * @author Mark Hoffmann
 * @since 14.01.2025
 */
// @Component - Now handled by ComprehensiveTypeConverter
public class DurationConverter implements TypeConverter {

    @Override
    public String getName() {
        return "duration";
    }

    @Override
    public Object convertValueToEMF(EClassifier eDataType, Object value) {
        if (value == null) {
            return null;
        }
        
        if (value instanceof Long millis) {
            return Duration.ofMillis(millis);
        }
        
        if (value instanceof Integer millis) {
            return Duration.ofMillis(millis.longValue());
        }
        
        if (value instanceof String durationStr) {
            return Duration.parse(durationStr);
        }
        
        return value;
    }

    @Override
    public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
        if (emfValue == null) {
            return null;
        }
        
        if (emfValue instanceof Duration duration) {
            return duration.toMillis();
        }
        
        return emfValue;
    }

    @Override
    public boolean isConverterForType(EClassifier eDataType) {
        if (eDataType instanceof EDataType dataType) {
            String instanceTypeName = dataType.getInstanceTypeName();
            return "java.time.Duration".equals(instanceTypeName);
        }
        return false;
    }
}