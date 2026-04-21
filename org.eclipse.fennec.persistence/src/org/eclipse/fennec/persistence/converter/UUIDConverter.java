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

import java.util.UUID;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.fennec.persistence.api.TypeConverter;

/**
 * Converter for UUID to/from String.
 * Handles conversion between java.util.UUID and database VARCHAR columns.
 * 
 * @author Mark Hoffmann
 * @since 14.01.2025
 */
// @Component - Now handled by ComprehensiveTypeConverter
public class UUIDConverter implements TypeConverter {

    @Override
    public String getName() {
        return "uuid";
    }

    @Override
    public Object convertValueToEMF(EClassifier eDataType, Object value) {
        if (isNull(value)) {
            return null;
        }
        
        if (value instanceof String uuidStr) {
            return UUID.fromString(uuidStr);
        }
        
        if (value instanceof UUID) {
            return value;
        }
        
        return value;
    }

    @Override
    public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
        if (isNull(emfValue)) {
            return null;
        }
        
        if (emfValue instanceof UUID uuid) {
            return uuid.toString();
        }
        
        return emfValue;
    }

    @Override
    public boolean isConverterForType(EClassifier eDataType) {
        if (eDataType instanceof EDataType dataType) {
            String instanceTypeName = dataType.getInstanceTypeName();
            return "java.util.UUID".equals(instanceTypeName) || 
                   "UUID".equals(instanceTypeName);
        }
        return false;
    }
}