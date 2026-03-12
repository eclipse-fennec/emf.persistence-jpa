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
package org.eclipse.fennec.persistence.api;

import org.eclipse.emf.ecore.EClassifier;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * Value converter interface to enable customization on type level
 * @author Mark Hoffmann
 * @since 26.03.2022
 */
@ConsumerType
public interface TypeConverter {
	
	/**
	 * The converter name
	 * @return converter name
	 */
	String getName();
	
	/**
	 * Convert a value from a persistence to a value used by EMF of the specified type.
	 * 
	 * An example might be converting a long read from DB into an EDataType of
	 * java.util.Calendar.
	 * 
	 * @param eDataType the EMF type that the value needs to be converted to
	 * @param value the value read from 3rd party persistence
	 * @return the value that will be set in the EMF object being built.
	 */
	Object convertValueToEMF(EClassifier eDataType, Object value);

	/**
	 * Convert a value from EMF of the specified type to a value stored in a persistence.
	 * 
	 * An example might be converting an EDataType of java.util.Calendar to a long
	 * that is stored in a database
	 * 
	 * @param eDataType the EMF type that the value needs to be converted from
	 * @param emfValue the value from the EMF object
	 * @return the value that will be stored in the 3rd party persistence
	 */
	Object convertEMFToValue(EClassifier eDataType, Object emfValue);

	/**
	 * Determines whether or not this converter can convert a value of a specific type.
	 * 
	 * @param eDataType the type of the value that needs to be converted
	 * @return <code>true</code> if this converter can handle values of the specified type; <code>false</code> otherwise
	 */
	boolean isConverterForType(EClassifier eDataType);

}
