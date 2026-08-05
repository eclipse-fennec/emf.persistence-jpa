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
import java.util.EnumSet;
import java.util.Set;

import org.eclipse.fennec.persistence.query.api.QueryFeature;
import org.eclipse.fennec.persistence.query.api.QueryShape;

/**
 * The immutable outcome of a {@link QueryAnalyzer} run: which {@link QueryFeature}s a
 * query uses, the maximum {@code FeaturePath} depth it traverses and the {@link QueryShape}
 * its result will have.
 *
 * @author Mark Hoffmann
 * @since 23.07.2026
 */
public final class QueryAnalysis {

	private final Set<QueryFeature> features;
	private final int maxFeaturePathDepth;
	private final QueryShape shape;
	private final boolean divisionByLiteralZero;
	private final String invalidAggregate;
	private final String invalidSort;
	private final String invalidGeo;

	/**
	 * Creates an analysis result — used by the analyzers ({@code QueryAnalyzer} for the
	 * v1 model, {@code ExpressionAnalyzer} for the v2 expression IR).
	 *
	 * @param features the used features
	 * @param maxFeaturePathDepth the maximum navigation depth
	 * @param shape the result shape
	 */
	public QueryAnalysis(Set<QueryFeature> features, int maxFeaturePathDepth, QueryShape shape) {
		this(features, maxFeaturePathDepth, shape, false);
	}

	/**
	 * Creates an analysis result including the static division-by-zero verdict.
	 *
	 * @param features the used features
	 * @param maxFeaturePathDepth the maximum navigation depth
	 * @param shape the result shape
	 * @param divisionByLiteralZero whether any DIV/MOD divides by a literal zero
	 */
	public QueryAnalysis(Set<QueryFeature> features, int maxFeaturePathDepth, QueryShape shape,
			boolean divisionByLiteralZero) {
		this(features, maxFeaturePathDepth, shape, divisionByLiteralZero, null);
	}

	/**
	 * Creates an analysis result including the static structural verdicts.
	 *
	 * @param features the used features
	 * @param maxFeaturePathDepth the maximum navigation depth
	 * @param shape the result shape
	 * @param divisionByLiteralZero whether any DIV/MOD divides by a literal zero
	 * @param invalidAggregate the malformed-aggregate finding (issue #87), or {@code null}
	 */
	public QueryAnalysis(Set<QueryFeature> features, int maxFeaturePathDepth, QueryShape shape,
			boolean divisionByLiteralZero, String invalidAggregate) {
		this(features, maxFeaturePathDepth, shape, divisionByLiteralZero, invalidAggregate, null);
	}

	/**
	 * Creates an analysis result including the static structural verdicts.
	 *
	 * @param features the used features
	 * @param maxFeaturePathDepth the maximum navigation depth
	 * @param shape the result shape
	 * @param divisionByLiteralZero whether any DIV/MOD divides by a literal zero
	 * @param invalidAggregate the malformed-aggregate finding (issue #87), or {@code null}
	 * @param invalidSort the malformed-sort finding (issue #102), or {@code null}
	 */
	public QueryAnalysis(Set<QueryFeature> features, int maxFeaturePathDepth, QueryShape shape,
			boolean divisionByLiteralZero, String invalidAggregate, String invalidSort) {
		this(features, maxFeaturePathDepth, shape, divisionByLiteralZero, invalidAggregate, invalidSort,
				null);
	}

	/**
	 * Creates an analysis result including the static structural verdicts.
	 *
	 * @param features the used features
	 * @param maxFeaturePathDepth the maximum navigation depth
	 * @param shape the result shape
	 * @param divisionByLiteralZero whether any DIV/MOD divides by a literal zero
	 * @param invalidAggregate the malformed-aggregate finding (issue #87), or {@code null}
	 * @param invalidSort the malformed-sort finding (issue #102), or {@code null}
	 * @param invalidGeo the malformed-geo finding (issue #101), or {@code null}
	 */
	public QueryAnalysis(Set<QueryFeature> features, int maxFeaturePathDepth, QueryShape shape,
			boolean divisionByLiteralZero, String invalidAggregate, String invalidSort, String invalidGeo) {
		this.features = Collections.unmodifiableSet(features.isEmpty()
				? EnumSet.noneOf(QueryFeature.class)
				: EnumSet.copyOf(features));
		this.maxFeaturePathDepth = maxFeaturePathDepth;
		this.shape = shape;
		this.divisionByLiteralZero = divisionByLiteralZero;
		this.invalidAggregate = invalidAggregate;
		this.invalidSort = invalidSort;
		this.invalidGeo = invalidGeo;
	}

	/**
	 * @return the features the analyzed query uses, never {@code null}
	 */
	public Set<QueryFeature> features() {
		return features;
	}

	/**
	 * @param feature the feature to test
	 * @return {@code true} if the analyzed query uses the feature
	 */
	public boolean uses(QueryFeature feature) {
		return features.contains(feature);
	}

	/**
	 * @return the maximum number of segments of any {@code FeaturePath} in the query;
	 *         {@code 0} if the query holds no feature paths
	 */
	public int maxFeaturePathDepth() {
		return maxFeaturePathDepth;
	}

	/**
	 * @return the shape the query's result will have
	 */
	public QueryShape shape() {
		return shape;
	}

	/**
	 * @return {@code true} if any arithmetic DIV/MOD in the query divides by a literal
	 *         zero — statically refusable, see issue #76
	 */
	public boolean divisionByLiteralZero() {
		return divisionByLiteralZero;
	}

	/**
	 * @return the malformed-aggregate finding — an {@code Aggregate} setting both or
	 *         (except COUNT) neither of {@code path}/{@code source} (issue #87) —
	 *         or {@code null} if all aggregates are well-formed
	 */
	public String invalidAggregate() {
		return invalidAggregate;
	}

	/**
	 * @return the malformed-sort finding — a bare {@code AliasRef} sort key on a query
	 *         that is not row-shaped (issue #102) — or {@code null} if all sorts are
	 *         well-formed
	 */
	public String invalidSort() {
		return invalidSort;
	}

	/**
	 * @return the malformed-geo finding — a {@code GeoSubject} without exactly one
	 *         binding, out-of-range coordinates or a degenerate/antimeridian-crossing
	 *         polygon (issue #101) — or {@code null} if the geo structure is well-formed
	 */
	public String invalidGeo() {
		return invalidGeo;
	}

	@Override
	public String toString() {
		return "QueryAnalysis[shape=" + shape + ", maxDepth=" + maxFeaturePathDepth + ", features=" + features
				+ (divisionByLiteralZero ? ", divisionByLiteralZero" : "")
				+ (invalidAggregate != null ? ", invalidAggregate=" + invalidAggregate : "")
				+ (invalidSort != null ? ", invalidSort=" + invalidSort : "") + "]";
	}
}
