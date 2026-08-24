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

import org.eclipse.fennec.persistence.capabilities.QueryFeature;
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
	private final String invalidStringMatch;
	private final String invalidMapValue;
	private final String invalidProjection;
	private final String invalidInterval;
	private final String invalidRepresentatives;
	private final String unsupportedPipeline;

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
		this(features, maxFeaturePathDepth, shape, divisionByLiteralZero, invalidAggregate, invalidSort,
				invalidGeo, null);
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
	 * @param invalidStringMatch the malformed-string-match finding (issue #167), or {@code null}
	 */
	public QueryAnalysis(Set<QueryFeature> features, int maxFeaturePathDepth, QueryShape shape,
			boolean divisionByLiteralZero, String invalidAggregate, String invalidSort, String invalidGeo,
			String invalidStringMatch) {
		this(features, maxFeaturePathDepth, shape, divisionByLiteralZero, invalidAggregate, invalidSort,
				invalidGeo, invalidStringMatch, null);
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
	 * @param invalidStringMatch the malformed-string-match finding (issue #167), or {@code null}
	 * @param invalidMapValue the malformed-map-access finding (issue #186), or {@code null}
	 */
	public QueryAnalysis(Set<QueryFeature> features, int maxFeaturePathDepth, QueryShape shape,
			boolean divisionByLiteralZero, String invalidAggregate, String invalidSort, String invalidGeo,
			String invalidStringMatch, String invalidMapValue) {
		this(features, maxFeaturePathDepth, shape, divisionByLiteralZero, invalidAggregate, invalidSort,
				invalidGeo, invalidStringMatch, invalidMapValue, null);
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
	 * @param invalidStringMatch the malformed-string-match finding (issue #167), or {@code null}
	 * @param invalidMapValue the malformed-map-access finding (issue #186), or {@code null}
	 * @param invalidProjection the malformed-projection finding (issue #189), or {@code null}
	 */
	public QueryAnalysis(Set<QueryFeature> features, int maxFeaturePathDepth, QueryShape shape,
			boolean divisionByLiteralZero, String invalidAggregate, String invalidSort, String invalidGeo,
			String invalidStringMatch, String invalidMapValue, String invalidProjection) {
		this(features, maxFeaturePathDepth, shape, divisionByLiteralZero, invalidAggregate, invalidSort,
				invalidGeo, invalidStringMatch, invalidMapValue, invalidProjection, null);
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
	 * @param invalidStringMatch the malformed-string-match finding (issue #167), or {@code null}
	 * @param invalidMapValue the malformed-map-access finding (issue #186), or {@code null}
	 * @param invalidProjection the malformed-projection finding (issue #189), or {@code null}
	 * @param invalidInterval the malformed-interval finding (issue #215), or {@code null}
	 */
	public QueryAnalysis(Set<QueryFeature> features, int maxFeaturePathDepth, QueryShape shape,
			boolean divisionByLiteralZero, String invalidAggregate, String invalidSort, String invalidGeo,
			String invalidStringMatch, String invalidMapValue, String invalidProjection,
			String invalidInterval) {
		this(features, maxFeaturePathDepth, shape, divisionByLiteralZero, invalidAggregate, invalidSort,
				invalidGeo, invalidStringMatch, invalidMapValue, invalidProjection, invalidInterval, null,
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
	 * @param invalidStringMatch the malformed-string-match finding (issue #167), or {@code null}
	 * @param invalidMapValue the malformed-map-access finding (issue #186), or {@code null}
	 * @param invalidProjection the malformed-projection finding (issue #189), or {@code null}
	 * @param invalidInterval the malformed-interval finding (issue #215), or {@code null}
	 * @param invalidRepresentatives the malformed-representatives finding (issue #214), or
	 *        {@code null}
	 * @param unsupportedPipeline a pipeline shape no backend serves (issue #239), or
	 *        {@code null}. Unlike the {@code invalid*} findings this is not malformed — the
	 *        query is well-formed and simply cannot be executed anywhere, so it is reported as
	 *        an unsupported feature rather than as a structural error.
	 */
	public QueryAnalysis(Set<QueryFeature> features, int maxFeaturePathDepth, QueryShape shape,
			boolean divisionByLiteralZero, String invalidAggregate, String invalidSort, String invalidGeo,
			String invalidStringMatch, String invalidMapValue, String invalidProjection,
			String invalidInterval, String invalidRepresentatives, String unsupportedPipeline) {
		this.features = Collections.unmodifiableSet(features.isEmpty()
				? EnumSet.noneOf(QueryFeature.class)
				: EnumSet.copyOf(features));
		this.maxFeaturePathDepth = maxFeaturePathDepth;
		this.shape = shape;
		this.divisionByLiteralZero = divisionByLiteralZero;
		this.invalidAggregate = invalidAggregate;
		this.invalidSort = invalidSort;
		this.invalidGeo = invalidGeo;
		this.invalidStringMatch = invalidStringMatch;
		this.invalidMapValue = invalidMapValue;
		this.invalidProjection = invalidProjection;
		this.invalidInterval = invalidInterval;
		this.invalidRepresentatives = invalidRepresentatives;
		this.unsupportedPipeline = unsupportedPipeline;
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

	/**
	 * @return the malformed-string-match finding — fuzzy parameters on a non-FUZZY kind, or
	 *         an out-of-range edit budget (issue #167) — or {@code null} if all string
	 *         matches are well-formed
	 */
	public String invalidStringMatch() {
		return invalidStringMatch;
	}

	/**
	 * @return the malformed-map-access finding — a {@code MapValue} whose path does not end
	 *         in a map, or whose key is not constant (issue #186) — or {@code null} if every
	 *         map access is well-formed
	 */
	public String invalidMapValue() {
		return invalidMapValue;
	}

	/**
	 * @return the malformed-projection finding — a {@code Selection} setting both or neither
	 *         of {@code path}/{@code key}, or an expression projection without an alias
	 *         (issue #189) — or {@code null} if every projection is well-formed
	 */
	public String invalidProjection() {
		return invalidProjection;
	}

	/**
	 * @return the malformed-interval finding — an {@code IntervalSubject} missing a bound
	 *         path or an {@code IntervalMatch} whose two literal bounds are inverted
	 *         (issue #215) — or {@code null} if every interval is well-formed
	 */
	public String invalidInterval() {
		return invalidInterval;
	}

	/**
	 * @return the malformed-representatives finding — a window without an alias, or with a
	 *         non-constant or non-positive bound (issue #214) — or {@code null} if the
	 *         representative window is well-formed
	 */
	public String invalidRepresentatives() {
		return invalidRepresentatives;
	}

	/**
	 * A pipeline shape that is expressible but that no backend serves (issue #239).
	 *
	 * @return the finding, or {@code null} when the pipeline shape is servable
	 */
	public String unsupportedPipeline() {
		return unsupportedPipeline;
	}

	@Override
	public String toString() {
		return "QueryAnalysis[shape=" + shape + ", maxDepth=" + maxFeaturePathDepth + ", features=" + features
				+ (divisionByLiteralZero ? ", divisionByLiteralZero" : "")
				+ (invalidAggregate != null ? ", invalidAggregate=" + invalidAggregate : "")
				+ (invalidSort != null ? ", invalidSort=" + invalidSort : "") + "]";
	}
}
