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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/**
 * Covers {@link StoreCapabilitiesBuilder} and the narrowing shape a deployment probe uses
 * (issue #134).
 *
 * @author Mark Hoffmann
 * @since 14.08.2026
 */
class StoreCapabilitiesBuilderTest {

	@Test
	void anEmptyDeclarationSupportsNothing() {
		StoreCapabilities capabilities = StoreCapabilitiesBuilder.create().build();
		assertThat(capabilities.supports(StoreFeature.TRANSACTION_BRACKET)).isFalse();
		assertThat(capabilities.supported()).isEmpty();
	}

	@Test
	void declaredFeaturesAreReported() {
		StoreCapabilities capabilities = StoreCapabilitiesBuilder.create()
				.support(StoreFeature.TRANSACTION_BRACKET)
				.build();
		assertThat(capabilities.supports(StoreFeature.TRANSACTION_BRACKET)).isTrue();
		assertThat(capabilities.supported()).containsExactly(StoreFeature.TRANSACTION_BRACKET);
	}

	/**
	 * The probe shape: start from the flavor's declaration and take away what this deployment
	 * cannot serve — a standalone mongod against a flavor that declares transactions.
	 */
	@Test
	void aProbeNarrowsTheDeclarationItStartedFrom() {
		StoreCapabilities declared = StoreCapabilitiesBuilder.create()
				.support(StoreFeature.TRANSACTION_BRACKET)
				.build();

		StoreCapabilities effective = StoreCapabilitiesBuilder.from(declared)
				.exclude(StoreFeature.TRANSACTION_BRACKET)
				.build();

		assertThat(effective.supports(StoreFeature.TRANSACTION_BRACKET)).isFalse();
		// the declaration is untouched — narrowing produces a second, smaller answer
		assertThat(declared.supports(StoreFeature.TRANSACTION_BRACKET)).isTrue();
	}

	@Test
	void excludingSomethingUndeclaredChangesNothing() {
		StoreCapabilities effective = StoreCapabilitiesBuilder.from(
						StoreCapabilitiesBuilder.create().build())
				.exclude(StoreFeature.TRANSACTION_BRACKET)
				.build();
		assertThat(effective.supported()).isEmpty();
	}

	@Test
	void supportedSetIsImmutable() {
		StoreCapabilities capabilities = StoreCapabilitiesBuilder.create()
				.support(StoreFeature.TRANSACTION_BRACKET)
				.build();
		assertThatThrownBy(() -> capabilities.supported().add(StoreFeature.TRANSACTION_BRACKET))
				.isInstanceOf(UnsupportedOperationException.class);
	}
}
