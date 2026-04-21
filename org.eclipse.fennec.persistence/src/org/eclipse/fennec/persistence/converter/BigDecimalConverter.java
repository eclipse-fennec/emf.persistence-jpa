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

import static java.util.Objects.nonNull;

import java.math.BigDecimal;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.api.TypeConverter;

/**
 * Converter for BigInteger.
 * @author Mark Hoffmann
 * @since 19.06.2022
 */
public class BigDecimalConverter implements TypeConverter {

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.converter.TypeConverter#isConverterForType(org.eclipse.emf.ecore.EClassifier)
	 */
	@Override
	public boolean isConverterForType(EClassifier eDataType) {
		if (eDataType instanceof EDataType) {
			Class<?> instanceClass = eDataType.getInstanceClass();
			if (nonNull(instanceClass) && instanceClass.equals(BigDecimal.class)) {
				return true;
			}
		}
		return false;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.converter.TypeConverter#convertValueToEMF(org.eclipse.emf.ecore.EClassifier, java.lang.Object)
	 */
	@Override
	public Object convertValueToEMF(EClassifier eDataType,
			Object databaseValue) {
		if (databaseValue instanceof Double doubleValue) {
			return EcoreUtil.createFromString((EDataType) eDataType, doubleValue.toString());
		}
		if (databaseValue instanceof String stringValue) {
			return EcoreUtil.createFromString((EDataType) eDataType, stringValue);
		}
		return databaseValue.toString();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.converter.TypeConverter#convertEMFToValue(org.eclipse.emf.ecore.EClassifier, java.lang.Object)
	 */
	@Override
	public Object convertEMFToValue(EClassifier eDataType,
			Object emfValue) {
		return EcoreUtil.convertToString((EDataType) eDataType, emfValue);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.api.TypeConverter#getName()
	 */
	@Override
	public String getName() {
		return "bigDecimal";
	}

}
