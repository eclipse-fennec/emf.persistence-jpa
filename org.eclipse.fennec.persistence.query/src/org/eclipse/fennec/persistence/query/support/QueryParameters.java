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

import java.util.Map;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.query.QueryException;

/**
 * Resolution of comparator values during translation, implementing the placeholder
 * convention for prepared queries (see {@code QueryFeature.PARAMETERS}):
 * <ul>
 * <li>{@code ":name"} — a named parameter, resolved from the bound parameter map. The
 * bound value is already typed (no string parsing) and only runs through the
 * {@code ConverterService} for the backend representation.</li>
 * <li>{@code "::rest"} — an escaped literal starting with a single {@code ":"}.</li>
 * <li>anything else — a literal, parsed and converted via
 * {@link QueryValues#convert(String, EStructuralFeature, ConverterService)}.</li>
 * </ul>
 * An unbound placeholder is a translation error ({@link QueryException}) — prepared
 * queries either bind all their parameters or fail loudly.
 *
 * @author Mark Hoffmann
 * @since 23.07.2026
 */
public final class QueryParameters {

	/** Escape prefix: a value starting with {@code "::"} is a literal starting with {@code ":"}. */
	public static final String ESCAPE_PREFIX = QueryAnalyzer.PARAMETER_PREFIX + QueryAnalyzer.PARAMETER_PREFIX;

	private QueryParameters() {
	}

	/**
	 * @param raw the comparator value
	 * @return {@code true} if the value is a (non-escaped) named parameter placeholder
	 */
	public static boolean isPlaceholder(String raw) {
		return raw != null && raw.startsWith(QueryAnalyzer.PARAMETER_PREFIX) && !raw.startsWith(ESCAPE_PREFIX);
	}

	/**
	 * @param raw a placeholder value (see {@link #isPlaceholder(String)})
	 * @return the parameter name without the prefix
	 * @throws IllegalArgumentException if the value is not a placeholder or names nothing
	 */
	public static String parameterName(String raw) {
		if (!isPlaceholder(raw)) {
			throw new IllegalArgumentException("'" + raw + "' is not a parameter placeholder");
		}
		String name = raw.substring(QueryAnalyzer.PARAMETER_PREFIX.length());
		if (name.isBlank()) {
			throw new IllegalArgumentException("Parameter placeholder without a name: '" + raw + "'");
		}
		return name;
	}

	/**
	 * Resolves a comparator value to the typed backend value: placeholders from the bound
	 * parameters, escaped and plain literals via {@link QueryValues}.
	 *
	 * @param raw the comparator value; {@code null} stays {@code null}
	 * @param feature the target feature the value is compared against
	 * @param parameters the bound parameters; may be {@code null} when the query has none
	 * @param converter the shared converter service; may be {@code null}
	 * @return the typed backend value
	 * @throws QueryException if the value is a placeholder with no bound parameter
	 * @throws IllegalArgumentException if a literal cannot be parsed as the feature's type
	 */
	public static Object resolve(String raw, EStructuralFeature feature, Map<String, Object> parameters,
			ConverterService converter) throws QueryException {
		if (raw == null) {
			return null;
		}
		if (isPlaceholder(raw)) {
			String name = parameterName(raw);
			if (parameters == null || !parameters.containsKey(name)) {
				throw new QueryException("Unbound query parameter '" + raw + "' for feature '"
						+ (feature == null ? "<unknown>" : feature.getName())
						+ "' — bind it via the parameters map");
			}
			return QueryValues.toPersistenceValue(parameters.get(name), feature, converter);
		}
		String literal = raw.startsWith(ESCAPE_PREFIX) ? raw.substring(QueryAnalyzer.PARAMETER_PREFIX.length()) : raw;
		return QueryValues.convert(literal, feature, converter);
	}
}
