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
	 * Locates an appropriate converter for a given {@link EClassifier}.
	 * <p>
	 * "No converter" is an answer, not an error (issue #164): most classifiers — all the
	 * plain Ecore data types among them — are persisted as they are, and callers treat a
	 * {@code null} as "use the value unconverted" (see
	 * {@code ExpressionValues.toPersistenceValue}).
	 *
	 * @param eDataType the data type needing conversion
	 * @return the converter for the specified data type, or {@code null} if no registered
	 *         converter claims the type
	 */
	TypeConverter getConverter(EClassifier eDataType);

	/**
	 * Locates an appropriate converter for a given name.
	 * <p>
	 * Nullable like the type-based lookup (issue #164). A caller resolving an
	 * <em>explicitly configured</em> name (e.g. from an eorm {@code Convert}) should treat
	 * {@code null} as a configuration error and fail loudly with the name — this service
	 * cannot know whether the absence was expected.
	 *
	 * @param name the converter name
	 * @return the converter registered under the name, or {@code null} if there is none
	 */
	TypeConverter getConverter(String name);

}