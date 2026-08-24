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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * The literal catalogue itself (issue #207) — the same guard {@code StreamModelTest} keeps over
 * {@code DeltaKind}, for the same reason: these values are additive and never renumbered, so a
 * change here is a change nobody can take back after the first release.
 *
 * @author Mark Hoffmann
 * @since 22.08.2026
 */
class QueryFeatureCatalogTest {

	/**
	 * 101 was {@code SERIES_RANGE}, retired in #207 before anything declared it. "Range" was
	 * never the new capability — narrowing a series to a time window is an ordinary predicate
	 * on the time axis, which the {@code WHERE_*} vocabulary already covers. What is new is
	 * that the <em>subject</em> is a series, and that is what the literal will be called when
	 * the IR phase writes it.
	 * <p>
	 * The number stays unused. Reusing a retired value costs nothing today and misleads every
	 * reader of a diff later.
	 */
	@Test
	void theRetiredSeriesRangeValueStaysUnused() {
		assertThat(QueryFeature.get(101))
				.as("101 is retired (SERIES_RANGE, #207) and must not be handed to a successor")
				.isNull();
		assertThat(QueryFeature.get("SERIES_RANGE")).isNull();
	}

	/**
	 * {@code AS_OF} survived the same review because it is not series vocabulary at all: it
	 * reconstructs the <em>current state</em> at a point in time from keyframe plus replay,
	 * which is a CHANGELOG-profile operation. A series query never needs it — every sample is
	 * absolute. Keeping the two apart is what lets a store declare one without the other.
	 */
	@Test
	void asOfIsStillReservedAndStillOrthogonalToSeries() {
		assertThat(QueryFeature.get(100)).isSameAs(QueryFeature.AS_OF);
		assertThat(QueryFeature.AS_OF.getValue()).isEqualTo(100);
	}

	/**
	 * The guard that matters for what comes next.
	 * <p>
	 * {@code MemoryQueryProcessor} declares its capabilities as {@code complementOf(AS_OF,
	 * SCORE)} — every literal except two. That is a fair statement about a reference engine
	 * today, and a trap tomorrow: a literal added for a feature nobody has implemented yet is
	 * <em>immediately</em> claimed by the reference, and the claim is silent. The series
	 * vocabulary (#212) will add exactly such literals.
	 * <p>
	 * So the count is pinned. When this test fails, the literal being added needs a decision
	 * rather than a default: either the memory engine implements it, or it joins the exclusion
	 * set next to {@code AS_OF} and {@code SCORE}.
	 */
	@Test
	void aNewLiteralMustBeADecisionRatherThanADefault() {
		assertThat(QueryFeature.values())
				.as("adding a QueryFeature? MemoryQueryProcessor claims it automatically "
						+ "(complementOf) — implement it there or exclude it, then fix this count")
				// 55 since ROOT_REFERENCE (#241). That one is EXCLUDED from the memory
				// engine rather than implemented there: it needs a store to read the
				// referenced object from, and the engine is handed one collection — the
				// capability belongs to the resource, not to the evaluator. The guard did
				// its job; the literal got a decision instead of a silent claim.
				.hasSize(55);
	}
}
