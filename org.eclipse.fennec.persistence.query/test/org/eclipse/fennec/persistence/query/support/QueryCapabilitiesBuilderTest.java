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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.EnumSet;
import java.util.List;

import org.eclipse.fennec.persistence.query.api.QueryCapabilities;
import org.eclipse.fennec.persistence.query.api.QueryFeature;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link QueryCapabilitiesBuilder}.
 *
 * @author Mark Hoffmann
 */
class QueryCapabilitiesBuilderTest {

	@Test
	void emptyBuilderSupportsNothingWithLocalDepth() {
		QueryCapabilities capabilities = QueryCapabilitiesBuilder.create().build();
		assertThat(capabilities.supported()).isEmpty();
		assertThat(capabilities.supports(QueryFeature.WHERE_EQ)).isFalse();
		assertThat(capabilities.maxFeaturePathDepth()).isEqualTo(1);
	}

	@Test
	void supportedFeaturesAreReported() {
		QueryCapabilities capabilities = QueryCapabilitiesBuilder.create()
				.support(QueryFeature.WHERE_EQ, QueryFeature.SORT)
				.supportAll(EnumSet.of(QueryFeature.LIMIT, QueryFeature.SKIP))
				.build();

		assertThat(capabilities.supports(QueryFeature.WHERE_EQ)).isTrue();
		assertThat(capabilities.supports(QueryFeature.SORT)).isTrue();
		assertThat(capabilities.supports(QueryFeature.LIMIT)).isTrue();
		assertThat(capabilities.supports(QueryFeature.SKIP)).isTrue();
		assertThat(capabilities.supports(QueryFeature.DISTINCT)).isFalse();
		assertThat(capabilities.supported()).containsExactlyInAnyOrder(QueryFeature.WHERE_EQ, QueryFeature.SORT,
				QueryFeature.LIMIT, QueryFeature.SKIP);
	}

	@Test
	void builtCapabilitiesAreImmutableSnapshots() {
		QueryCapabilitiesBuilder builder = QueryCapabilitiesBuilder.create().support(QueryFeature.WHERE_EQ);
		QueryCapabilities first = builder.build();
		builder.support(QueryFeature.SORT);
		QueryCapabilities second = builder.build();

		assertThat(first.supports(QueryFeature.SORT)).isFalse();
		assertThat(second.supports(QueryFeature.SORT)).isTrue();
		assertThatThrownBy(() -> first.supported().add(QueryFeature.DISTINCT))
				.isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	void derivesFromABaselineIncludingDepth() {
		QueryCapabilities baseline = QueryCapabilitiesBuilder.create()
				.support(QueryFeature.WHERE_EQ, QueryFeature.SORT, QueryFeature.LIMIT)
				.maxFeaturePathDepth(-1)
				.build();

		QueryCapabilities derived = QueryCapabilitiesBuilder.from(baseline).build();

		assertThat(derived.supported()).isEqualTo(baseline.supported());
		assertThat(derived.maxFeaturePathDepth()).isEqualTo(-1);
	}

	@Test
	void excludesFeaturesFromADerivedDeclaration() {
		QueryCapabilities baseline = QueryCapabilitiesBuilder.create()
				.support(QueryFeature.WHERE_EQ, QueryFeature.SORT, QueryFeature.LIMIT, QueryFeature.GEO_WITHIN)
				.build();

		QueryCapabilities derived = QueryCapabilitiesBuilder.from(baseline)
				.exclude(QueryFeature.GEO_WITHIN)
				.excludeAll(List.of(QueryFeature.LIMIT))
				// excluding something never supported is a no-op, not an error: a variant
				// should be able to name a gap without knowing the baseline by heart
				.exclude(QueryFeature.DISTINCT)
				.build();

		assertThat(derived.supported()).containsExactlyInAnyOrder(QueryFeature.WHERE_EQ, QueryFeature.SORT);
		// the baseline is untouched — deriving must not mutate what it derives from
		assertThat(baseline.supported()).contains(QueryFeature.GEO_WITHIN, QueryFeature.LIMIT);
	}

	@Test
	void depthValidation() {
		assertThat(QueryCapabilitiesBuilder.create().maxFeaturePathDepth(-1).build().maxFeaturePathDepth())
				.isEqualTo(-1);
		assertThat(QueryCapabilitiesBuilder.create().maxFeaturePathDepth(3).build().maxFeaturePathDepth())
				.isEqualTo(3);
		assertThatIllegalArgumentException()
				.isThrownBy(() -> QueryCapabilitiesBuilder.create().maxFeaturePathDepth(0));
		assertThatIllegalArgumentException()
				.isThrownBy(() -> QueryCapabilitiesBuilder.create().maxFeaturePathDepth(-2));
	}
}
