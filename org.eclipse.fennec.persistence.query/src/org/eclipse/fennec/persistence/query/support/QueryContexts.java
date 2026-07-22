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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.query.api.QueryContext;

/**
 * Factory for immutable {@link QueryContext} instances — the carrier of everything a
 * {@code QueryProcessor} needs besides the query itself. Used by the backend resources
 * when executing {@code QueryableResource.query(...)} and by tests.
 *
 * @author Mark Hoffmann
 * @since 23.07.2026
 */
public final class QueryContexts {

	private QueryContexts() {
	}

	/**
	 * Creates a context without parameters and options.
	 *
	 * @param rootEClass the root type the query selects from, must not be {@code null}
	 * @param converter the shared converter service; may be {@code null}
	 * @return the immutable context
	 */
	public static QueryContext of(EClass rootEClass, ConverterService converter) {
		return of(rootEClass, converter, null, null);
	}

	/**
	 * Creates a context.
	 *
	 * @param rootEClass the root type the query selects from, must not be {@code null}
	 * @param converter the shared converter service; may be {@code null}
	 * @param parameters bound placeholder values; may be {@code null}
	 * @param options backend options; may be {@code null}
	 * @return the immutable context
	 */
	public static QueryContext of(EClass rootEClass, ConverterService converter, Map<String, Object> parameters,
			Map<?, ?> options) {
		if (rootEClass == null) {
			throw new IllegalArgumentException("rootEClass must not be null");
		}
		Map<String, Object> boundParameters = parameters == null || parameters.isEmpty()
				? Collections.emptyMap()
				: Collections.unmodifiableMap(new LinkedHashMap<>(parameters));
		Map<?, ?> boundOptions = options == null || options.isEmpty()
				? Collections.emptyMap()
				: Collections.unmodifiableMap(new LinkedHashMap<>(options));
		return new ImmutableContext(rootEClass, converter, boundParameters, boundOptions);
	}

	private static final class ImmutableContext implements QueryContext {

		private final EClass rootEClass;
		private final ConverterService converter;
		private final Map<String, Object> parameters;
		private final Map<?, ?> options;

		private ImmutableContext(EClass rootEClass, ConverterService converter, Map<String, Object> parameters,
				Map<?, ?> options) {
			this.rootEClass = rootEClass;
			this.converter = converter;
			this.parameters = parameters;
			this.options = options;
		}

		@Override
		public EClass rootEClass() {
			return rootEClass;
		}

		@Override
		public ConverterService converter() {
			return converter;
		}

		@Override
		public Map<String, Object> parameters() {
			return parameters;
		}

		@Override
		public Map<?, ?> options() {
			return options;
		}

		@Override
		public String toString() {
			return "QueryContext[root=" + rootEClass.getName() + ", parameters=" + parameters.keySet() + "]";
		}
	}
}
