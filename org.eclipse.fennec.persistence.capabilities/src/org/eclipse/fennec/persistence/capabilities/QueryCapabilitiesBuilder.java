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
package org.eclipse.fennec.persistence.capabilities;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;


/**
 * Builds immutable {@link QueryCapabilities} declarations for a backend.
 * <p>
 * The default {@code maxFeaturePathDepth} is {@code 1} (local features only) — a backend
 * must opt in to joins explicitly.
 *
 * @author Mark Hoffmann
 * @since 23.07.2026
 */
public final class QueryCapabilitiesBuilder {

	private final EnumSet<QueryFeature> features = EnumSet.noneOf(QueryFeature.class);
	private int maxFeaturePathDepth = 1;

	private QueryCapabilitiesBuilder() {
	}

	/**
	 * @return a fresh builder with no supported features and depth {@code 1}
	 */
	public static QueryCapabilitiesBuilder create() {
		return new QueryCapabilitiesBuilder();
	}

	/**
	 * Derives a builder from an existing declaration — everything {@code base} supports,
	 * including its {@code maxFeaturePathDepth}.
	 * <p>
	 * Intended for variants of one backend that differ only by what they <em>lack</em>
	 * (issue #118: the same Mongo translation served by MongoDB, FerretDB or the DocumentDB
	 * gateway). Restating a full set per variant would drift: a newly supported feature
	 * would have to be added in every list, and a forgotten entry is invisible. Derived
	 * plus {@link #exclude(QueryFeature...)} makes a new feature available everywhere by
	 * default, so a genuine gap has to be discovered — and declared — deliberately.
	 *
	 * @param base the declaration to derive from
	 * @return a builder pre-filled from {@code base}
	 */
	public static QueryCapabilitiesBuilder from(QueryCapabilities base) {
		QueryCapabilitiesBuilder builder = new QueryCapabilitiesBuilder();
		builder.features.addAll(base.supported());
		builder.maxFeaturePathDepth = base.maxFeaturePathDepth();
		return builder;
	}

	/**
	 * Removes features from this declaration; unsupported entries are ignored.
	 *
	 * @param toExclude the features this variant does not serve natively
	 * @return this builder
	 */
	public QueryCapabilitiesBuilder exclude(QueryFeature... toExclude) {
		for (QueryFeature feature : toExclude) {
			features.remove(feature);
		}
		return this;
	}

	/**
	 * Removes features from this declaration; unsupported entries are ignored.
	 *
	 * @param toExclude the features this variant does not serve natively
	 * @return this builder
	 */
	public QueryCapabilitiesBuilder excludeAll(Collection<QueryFeature> toExclude) {
		features.removeAll(toExclude);
		return this;
	}

	/**
	 * Declares the given features as natively supported.
	 *
	 * @param toSupport the features to add
	 * @return this builder
	 */
	public QueryCapabilitiesBuilder support(QueryFeature... toSupport) {
		for (QueryFeature feature : toSupport) {
			features.add(feature);
		}
		return this;
	}

	/**
	 * Declares the given features as natively supported.
	 *
	 * @param toSupport the features to add
	 * @return this builder
	 */
	public QueryCapabilitiesBuilder supportAll(Collection<QueryFeature> toSupport) {
		features.addAll(toSupport);
		return this;
	}

	/**
	 * Declares the maximum {@code FeaturePath} depth the backend can traverse.
	 *
	 * @param depth the depth; {@code 1} = local features only, {@code -1} = unlimited
	 * @return this builder
	 */
	public QueryCapabilitiesBuilder maxFeaturePathDepth(int depth) {
		if (depth < -1 || depth == 0) {
			throw new IllegalArgumentException("maxFeaturePathDepth must be -1 (unlimited) or >= 1, was " + depth);
		}
		this.maxFeaturePathDepth = depth;
		return this;
	}

	/**
	 * @return the immutable capabilities declaration
	 */
	public QueryCapabilities build() {
		return new ImmutableCapabilities(features, maxFeaturePathDepth);
	}

	private static final class ImmutableCapabilities implements QueryCapabilities {

		private final Set<QueryFeature> supported;
		private final int maxFeaturePathDepth;

		private ImmutableCapabilities(EnumSet<QueryFeature> features, int maxFeaturePathDepth) {
			this.supported = Collections.unmodifiableSet(features.isEmpty()
					? EnumSet.noneOf(QueryFeature.class)
					: EnumSet.copyOf(features));
			this.maxFeaturePathDepth = maxFeaturePathDepth;
		}

		@Override
		public boolean supports(QueryFeature feature) {
			return supported.contains(feature);
		}

		@Override
		public Set<QueryFeature> supported() {
			return supported;
		}

		@Override
		public int maxFeaturePathDepth() {
			return maxFeaturePathDepth;
		}

		@Override
		public String toString() {
			return "QueryCapabilities[maxDepth=" + maxFeaturePathDepth + ", supported=" + supported + "]";
		}
	}
}
