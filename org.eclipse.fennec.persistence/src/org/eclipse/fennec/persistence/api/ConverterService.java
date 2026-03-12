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
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.persistence.api;

import org.eclipse.emf.ecore.EClassifier;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Converter whiteboard
 * @author Mark Hoffmann
 * @since 14.01.2025
 */
@ProviderType
public interface ConverterService {
	
	public static final String PROP_CONVERTER_NAME = "fennec.persistence.converter";

	/**
	 * Locates an appropriate converter for a given {@link EClassifier}
	 * 
	 * @param eDataType the data type needing conversion
	 * @return the converter for the specified data type
	 */
	TypeConverter getConverter(EClassifier eDataType);
	
	/**
	 * Locates an appropriate converter for a given name
	 * 
	 * @param name the converter name
	 * @return the converter for the specified data type
	 */
	TypeConverter getConverter(String name);

}