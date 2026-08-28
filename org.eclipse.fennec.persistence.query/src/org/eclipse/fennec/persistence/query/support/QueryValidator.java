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
import org.eclipse.fennec.persistence.capabilities.QueryCapabilities;
import org.eclipse.fennec.persistence.capabilities.QueryFeature;
import org.eclipse.fennec.persistence.query.expr.ExpressionAnalyzer;

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

	/**
	 * Diagnostic code: an {@code Aggregate} sets both or (except COUNT) neither of
	 * {@code path}/{@code source} (issue #87).
	 */
	public static final int CODE_INVALID_AGGREGATE = 4;

	/**
	 * Diagnostic code: a bare {@code AliasRef} sort key on a query that is not
	 * row-shaped — output-column sorts need a projection or aggregation (issue #102).
	 */
	public static final int CODE_INVALID_SORT = 5;

	/**
	 * Diagnostic code: malformed geo structure — a {@code GeoSubject} without exactly
	 * one binding, out-of-range coordinates, or a degenerate/antimeridian-crossing
	 * polygon (issue #101).
	 */
	public static final int CODE_INVALID_GEO = 6;

	/**
	 * Diagnostic code: malformed string match — fuzzy parameters on a non-FUZZY kind, or
	 * an out-of-range edit budget (issue #167).
	 */
	public static final int CODE_INVALID_STRING_MATCH = 7;

	/**
	 * Diagnostic code: a {@code MapValue} whose path does not end in a map, or whose key is
	 * not a literal or parameter (issue #186).
	 */
	public static final int CODE_INVALID_MAP_VALUE = 8;

	/**
	 * Diagnostic code: a {@code Selection} that sets both or neither of {@code path}/{@code key},
	 * or projects an expression without the mandatory alias (issue #189).
	 */
	public static final int CODE_INVALID_PROJECTION = 9;

	/**
	 * Diagnostic code: an {@code IntervalSubject} that does not bind both bound paths, or an
	 * {@code IntervalMatch} whose query interval is inverted over two literals (issue #215).
	 */
	public static final int CODE_INVALID_INTERVAL = 10;

	/**
	 * Diagnostic code: a representative window without an alias, or with a bound that is not a
	 * positive constant (issue #214).
	 */
	public static final int CODE_INVALID_REPRESENTATIVES = 11;

	/**
	 * An expansion whose options do not form a meaningful request (issue #238) — currently an
	 * {@code orderBy} without {@code top}/{@code skip}. Structural, not a capability gap: under
	 * the resolution semantics the list order belongs to the store, so a standing-alone
	 * {@code orderBy} could only promise an order no backend ever delivers.
	 */
	public static final int CODE_INVALID_EXPAND = 12;

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

		if (analysis.invalidAggregate() != null) {
			result.add(new BasicDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_INVALID_AGGREGATE,
					analysis.invalidAggregate() + " (root type '" + rootName + "')",
					new Object[] { analysis }));
		}

		if (analysis.invalidSort() != null) {
			result.add(new BasicDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_INVALID_SORT,
					analysis.invalidSort() + " (root type '" + rootName + "')",
					new Object[] { analysis }));
		}

		if (analysis.invalidGeo() != null) {
			result.add(new BasicDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_INVALID_GEO,
					analysis.invalidGeo() + " (root type '" + rootName + "')",
					new Object[] { analysis }));
		}

		if (analysis.invalidStringMatch() != null) {
			result.add(new BasicDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_INVALID_STRING_MATCH,
					analysis.invalidStringMatch() + " (root type '" + rootName + "')",
					new Object[] { analysis }));
		}

		if (analysis.invalidMapValue() != null) {
			result.add(new BasicDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_INVALID_MAP_VALUE,
					analysis.invalidMapValue() + " (root type '" + rootName + "')",
					new Object[] { analysis }));
		}
		if (analysis.invalidProjection() != null) {
			result.add(new BasicDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_INVALID_PROJECTION,
					analysis.invalidProjection() + " (root type '" + rootName + "')",
					new Object[] { analysis }));
		}
		if (analysis.invalidInterval() != null) {
			result.add(new BasicDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_INVALID_INTERVAL,
					analysis.invalidInterval() + " (root type '" + rootName + "')",
					new Object[] { analysis }));
		}
		if (analysis.invalidRepresentatives() != null) {
			result.add(new BasicDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE,
					CODE_INVALID_REPRESENTATIVES,
					analysis.invalidRepresentatives() + " (root type '" + rootName + "')",
					new Object[] { analysis }));
		}
		if (analysis.invalidExpand() != null) {
			result.add(new BasicDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_INVALID_EXPAND,
					analysis.invalidExpand() + " (root type '" + rootName + "')",
					new Object[] { analysis }));
		}
		if (analysis.unsupportedPipeline() != null) {
			// UNSUPPORTED rather than INVALID (issue #239): the query is well-formed, it just
			// cannot be executed anywhere. A consumer routing on the code should answer "this
			// service cannot" (501), not "your request is malformed" (400).
			result.add(new BasicDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE,
					CODE_UNSUPPORTED_FEATURE,
					analysis.unsupportedPipeline() + " (root type '" + rootName + "')",
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
