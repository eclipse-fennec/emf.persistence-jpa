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

	QueryAnalysis(Set<QueryFeature> features, int maxFeaturePathDepth, QueryShape shape) {
		this.features = Collections.unmodifiableSet(features.isEmpty()
				? EnumSet.noneOf(QueryFeature.class)
				: EnumSet.copyOf(features));
		this.maxFeaturePathDepth = maxFeaturePathDepth;
		this.shape = shape;
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

	@Override
	public String toString() {
		return "QueryAnalysis[shape=" + shape + ", maxDepth=" + maxFeaturePathDepth + ", features=" + features + "]";
	}
}
