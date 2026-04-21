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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.api.TypeConverter;

/**
 * Converter for non-containment references (cross-references).
 * Handles conversion between EObject references and their string URI representations
 * for persistence in relational databases.
 * 
 * @author Mark Hoffmann
 * @since 14.01.2025
 */
public class NonContainmentConverter implements TypeConverter {

	/** Target EObject to be reshaped into a proxy during DB→EMF conversion. */
	private EObject toEObject;

	/**
	 * Registers the EObject that should receive the {@code eProxyURI} when the
	 * next {@link #convertValueToEMF(EClassifier, Object)} call parses a URI
	 * string. Callers typically pass a freshly instantiated target EObject just
	 * before feeding it the persisted URI value.
	 *
	 * @param toEObject the target EObject to be turned into a proxy
	 */
	public void setToEMFEObject(EObject toEObject) {
		this.toEObject = toEObject;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.api.TypeConverter#getName()
	 */
	@Override
	public String getName() {
		return "nonContainment";
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.api.TypeConverter#convertValueToEMF(org.eclipse.emf.ecore.EClassifier, java.lang.Object)
	 */
	@Override
	public Object convertValueToEMF(EClassifier eDataType, Object value) {
		if (value instanceof String) {
			URI proxyURI = URI.createURI(value.toString());
			if (nonNull(toEObject)) {
				((InternalEObject)toEObject).eSetProxyURI(proxyURI);
				return toEObject;
			}
			return URI.createURI(value.toString());
		}
		return value;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.api.TypeConverter#convertEMFToValue(org.eclipse.emf.ecore.EClassifier, java.lang.Object)
	 */
	@Override
	public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
		if (emfValue instanceof EObject eo) {
			URI uri = EcoreUtil.getURI(eo);
			return uri.toString();
		}
		return null;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.api.TypeConverter#isConverterForType(org.eclipse.emf.ecore.EClassifier)
	 */
	@Override
	public boolean isConverterForType(EClassifier eDataType) {
		if (eDataType instanceof EClass eClass) {
			return !eClass.isAbstract() && !eClass.isInterface();
		}
		return false;
	}

}
