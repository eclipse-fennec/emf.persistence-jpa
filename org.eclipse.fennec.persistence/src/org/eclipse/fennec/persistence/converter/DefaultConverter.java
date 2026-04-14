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

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.api.TypeConverter;

/**
 * Default value converter for EMF 
 * @author bhunt
 */
public class DefaultConverter implements TypeConverter {

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.converter.TypeConverter#convertValueToEMF(org.eclipse.emf.ecore.EClassifier, java.lang.Object)
	 */
	@Override
	public Object convertValueToEMF(EClassifier eDataType, Object databaseValue) {
		return EcoreUtil.createFromString((EDataType) eDataType, (String) databaseValue);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.converter.TypeConverter#convertEMFToValue(org.eclipse.emf.ecore.EClassifier, java.lang.Object)
	 */
	@Override
	public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
		return EcoreUtil.convertToString((EDataType) eDataType, emfValue);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.converter.TypeConverter#isConverterForType(org.eclipse.emf.ecore.EClassifier)
	 */
	@Override
	public boolean isConverterForType(EClassifier classifier) {
		if (!(classifier instanceof EDataType eDataType)) {
			return false;
		}
		int classifierId = eDataType.getClassifierID();
		return classifierId == EcorePackage.EBYTE_ARRAY ||
				classifierId == EcorePackage.EBYTE_OBJECT ||
				classifierId == EcorePackage.ECHAR ||
				classifierId == EcorePackage.ECHARACTER_OBJECT ||
				classifierId == EcorePackage.EJAVA_CLASS ||
				classifierId == EcorePackage.EJAVA_OBJECT;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.api.TypeConverter#getName()
	 */
	@Override
	public String getName() {
		return "default";
	}
}
