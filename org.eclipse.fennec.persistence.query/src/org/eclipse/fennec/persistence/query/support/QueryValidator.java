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

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.persistence.query.expr.ExpressionAnalyzer;
import org.eclipse.fennec.persistence.query.api.QueryCapabilities;
import org.eclipse.fennec.persistence.query.api.QueryFeature;

/**
 * Validates a canonical {@link Query} against a backend's declared
 * {@link QueryCapabilities}, producing an EMF {@link Diagnostic} tree.
 * <p>
 * This is the shared behaviour behind {@code QueryProcessor.validate(...)}: every
 * {@link QueryFeature} the query uses but the backend does not support yields a
 * {@link Diagnostic#ERROR} child naming the feature — a query is either served natively
 * or refused, never silently post-filtered in memory.
 *
 * @author Mark Hoffmann
 * @since 23.07.2026
 */
public final class QueryValidator {

	/** Diagnostic source of all diagnostics produced by this validator. */
	public static final String DIAGNOSTIC_SOURCE = "org.eclipse.fennec.persistence.query";

	/** Diagnostic code: the query uses a feature the backend does not support. */
	public static final int CODE_UNSUPPORTED_FEATURE = 1;

	/** Diagnostic code: the query traverses feature paths deeper than the backend allows. */
	public static final int CODE_DEPTH_EXCEEDED = 2;

	/** Diagnostic code: an arithmetic DIV/MOD divides by a literal zero (issue #76). */
	public static final int CODE_DIVISION_BY_ZERO = 3;

	private QueryValidator() {
	}

	/**
	 * Analyzes and validates the query in one step.
	 *
	 * @param query the query to validate, must not be {@code null}
	 * @param rootEClass the root type the query selects from; may be {@code null}, used in messages
	 * @param capabilities the backend's declared capabilities, must not be {@code null}
	 * @return {@link Diagnostic#OK} if every used feature is supported, otherwise a
	 *         diagnostic tree with one {@link Diagnostic#ERROR} child per violation
	 */
	public static Diagnostic validate(Query query, EClass rootEClass, QueryCapabilities capabilities) {
		return validate(ExpressionAnalyzer.analyze(query), rootEClass, capabilities);
	}

	/**
	 * Validates a pre-computed analysis, allowing the caller to reuse it for translation.
	 *
	 * @param analysis the analysis to validate, must not be {@code null}
	 * @param rootEClass the root type the query selects from; may be {@code null}, used in messages
	 * @param capabilities the backend's declared capabilities, must not be {@code null}
	 * @return {@link Diagnostic#OK} if every used feature is supported, otherwise a
	 *         diagnostic tree with one {@link Diagnostic#ERROR} child per violation
	 */
	public static Diagnostic validate(QueryAnalysis analysis, EClass rootEClass, QueryCapabilities capabilities) {
		if (analysis == null) {
			throw new IllegalArgumentException("analysis must not be null");
		}
		if (capabilities == null) {
			throw new IllegalArgumentException("capabilities must not be null");
		}
		String rootName = rootEClass == null ? "<unknown>" : rootEClass.getName();
		BasicDiagnostic result = new BasicDiagnostic(DIAGNOSTIC_SOURCE, 0,
				"Query validation for root type '" + rootName + "'", new Object[] { analysis });

		for (QueryFeature feature : analysis.features()) {
			if (!capabilities.supports(feature)) {
				result.add(new BasicDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_UNSUPPORTED_FEATURE,
						"Query feature " + feature.getName() + " is not supported by this backend (root type '"
								+ rootName + "')",
						new Object[] { feature }));
			}
		}

		if (analysis.divisionByLiteralZero()) {
			result.add(new BasicDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_DIVISION_BY_ZERO,
					"Query divides by a literal zero (root type '" + rootName
							+ "') — the expression can never evaluate",
					new Object[] { analysis }));
		}

		int maxDepth = capabilities.maxFeaturePathDepth();
		if (maxDepth != -1 && analysis.maxFeaturePathDepth() > maxDepth) {
			result.add(new BasicDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_DEPTH_EXCEEDED,
					"Query traverses feature paths of depth " + analysis.maxFeaturePathDepth()
							+ " but this backend supports at most " + maxDepth + " (root type '" + rootName + "')",
					new Object[] { analysis.maxFeaturePathDepth(), maxDepth }));
		}

		return result.getSeverity() == Diagnostic.OK ? Diagnostic.OK_INSTANCE : result;
	}
}
