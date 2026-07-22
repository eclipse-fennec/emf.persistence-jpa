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
package org.eclipse.fennec.persistence.query.support;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.model.utilities.FeaturePath;
import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.api.TypeConverter;

/**
 * Central typed-value conversion for query translation — the one place where the query
 * model's {@code EString} comparator values become typed values, so no backend processor
 * re-implements parsing.
 * <p>
 * Two steps, composable:
 * <ol>
 * <li>{@link #toEmfValue(String, EStructuralFeature)} parses the query literal into the
 * EMF-typed value of the target feature's {@code EDataType} (numbers, booleans, dates,
 * enum literals — everything a registered EMF data type can read).</li>
 * <li>{@link #toPersistenceValue(Object, EStructuralFeature, ConverterService)} converts
 * the EMF value into the backend representation via the shared {@link ConverterService}
 * (e.g. {@code Instant} → {@code long}), falling back to the EMF value when no converter
 * is registered.</li>
 * </ol>
 * {@link #convert(String, EStructuralFeature, ConverterService)} chains both.
 *
 * @author Mark Hoffmann
 * @since 23.07.2026
 */
public final class QueryValues {

	private QueryValues() {
	}

	/**
	 * Returns the feature a {@code FeaturePath} finally addresses — its last segment.
	 *
	 * @param path the path; may be {@code null} or empty
	 * @return the last segment, or {@code null} if the path is {@code null} or empty
	 */
	public static EStructuralFeature targetFeature(FeaturePath path) {
		if (path == null || path.getFeature().isEmpty()) {
			return null;
		}
		return path.getFeature().get(path.getFeature().size() - 1);
	}

	/**
	 * Parses a query literal into the EMF-typed value of the target feature.
	 *
	 * @param raw the query literal; {@code null} stays {@code null}
	 * @param feature the target feature, must have an {@link EDataType} type
	 * @return the typed EMF value
	 * @throws IllegalArgumentException if the feature has no data type, or the literal
	 *         cannot be parsed as the feature's type
	 */
	public static Object toEmfValue(String raw, EStructuralFeature feature) {
		if (raw == null) {
			return null;
		}
		if (feature == null) {
			throw new IllegalArgumentException("feature must not be null");
		}
		EClassifier type = feature.getEType();
		if (!(type instanceof EDataType dataType)) {
			throw new IllegalArgumentException("Feature '" + feature.getName()
					+ "' is not typed with an EDataType (was: " + (type == null ? "null" : type.getName())
					+ ") — comparator values can only address data-typed features");
		}
		try {
			return EcoreUtil.createFromString(dataType, raw);
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("Cannot convert '" + raw + "' to " + dataType.getName()
					+ " for feature '" + feature.getName() + "': " + e.getMessage(), e);
		}
	}

	/**
	 * Converts an EMF-typed value into its backend representation via the shared
	 * {@link ConverterService}. Without a converter (or when the service has none
	 * registered for the type) the EMF value is returned unchanged.
	 *
	 * @param emfValue the EMF-typed value; {@code null} stays {@code null}
	 * @param feature the target feature the value belongs to
	 * @param converter the shared converter service; may be {@code null}
	 * @return the persistence-side value
	 */
	public static Object toPersistenceValue(Object emfValue, EStructuralFeature feature, ConverterService converter) {
		if (emfValue == null || converter == null) {
			return emfValue;
		}
		EClassifier type = feature.getEType();
		TypeConverter typeConverter = converter.getConverter(type);
		if (typeConverter == null) {
			return emfValue;
		}
		return typeConverter.convertEMFToValue(type, emfValue);
	}

	/**
	 * Chains {@link #toEmfValue(String, EStructuralFeature)} and
	 * {@link #toPersistenceValue(Object, EStructuralFeature, ConverterService)}: query
	 * literal → typed backend value in one step.
	 *
	 * @param raw the query literal; {@code null} stays {@code null}
	 * @param feature the target feature, must have an {@link EDataType} type
	 * @param converter the shared converter service; may be {@code null}
	 * @return the persistence-side typed value
	 * @throws IllegalArgumentException if the literal cannot be parsed as the feature's type
	 */
	public static Object convert(String raw, EStructuralFeature feature, ConverterService converter) {
		return toPersistenceValue(toEmfValue(raw, feature), feature, converter);
	}
}
