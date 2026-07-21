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
package org.eclipse.fennec.persistence.liveness;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link LivenessConfig} parsing and clamping.
 */
class LivenessConfigTest {

	@Test
	void defaultsAreApplied() {
		LivenessConfig config = LivenessConfig.defaults();
		assertThat(config.enabled()).isTrue();
		assertThat(config.checkIntervalSeconds()).isEqualTo(30);
		assertThat(config.checkTimeoutSeconds()).isEqualTo(5);
		assertThat(config.failureThreshold()).isEqualTo(3);
		assertThat(config.retryMinSeconds()).isEqualTo(1);
		assertThat(config.retryMaxSeconds()).isEqualTo(30);
	}

	@Test
	void fromPropertiesReadsAllSupportedTypes() {
		LivenessConfig config = LivenessConfig.fromProperties(Map.of(
				LivenessConstants.ENABLED, "false",
				LivenessConstants.CHECK_INTERVAL, 60,
				LivenessConstants.CHECK_TIMEOUT, "10",
				LivenessConstants.FAILURE_THRESHOLD, 5L,
				LivenessConstants.RETRY_MIN, " 2 ",
				LivenessConstants.RETRY_MAX, 120));
		assertThat(config.enabled()).isFalse();
		assertThat(config.checkIntervalSeconds()).isEqualTo(60);
		assertThat(config.checkTimeoutSeconds()).isEqualTo(10);
		assertThat(config.failureThreshold()).isEqualTo(5);
		assertThat(config.retryMinSeconds()).isEqualTo(2);
		assertThat(config.retryMaxSeconds()).isEqualTo(120);
	}

	@Test
	void unparsableValuesFallBackToDefaults() {
		LivenessConfig config = LivenessConfig.fromProperties(Map.of(
				LivenessConstants.CHECK_INTERVAL, "not-a-number",
				LivenessConstants.FAILURE_THRESHOLD, ""));
		assertThat(config.checkIntervalSeconds()).isEqualTo(LivenessConfig.DEFAULT_CHECK_INTERVAL_SECONDS);
		assertThat(config.failureThreshold()).isEqualTo(LivenessConfig.DEFAULT_FAILURE_THRESHOLD);
	}

	@Test
	void outOfRangeValuesAreClamped() {
		LivenessConfig config = LivenessConfig.of(true, -5, 0, 0, -1, -10);
		assertThat(config.checkIntervalSeconds()).isZero();
		assertThat(config.checkTimeoutSeconds()).isEqualTo(1);
		assertThat(config.failureThreshold()).isEqualTo(1);
		assertThat(config.retryMinSeconds()).isZero();
		assertThat(config.retryMaxSeconds()).isZero();

		LivenessConfig inverted = LivenessConfig.of(true, 30, 5, 3, 20, 10);
		assertThat(inverted.retryMaxSeconds()).isEqualTo(20);
	}

	@Test
	void asMapExposesEffectiveSettings() {
		assertThat(LivenessConfig.defaults().asMap())
				.containsEntry(LivenessConstants.ENABLED, "true")
				.containsEntry(LivenessConstants.CHECK_INTERVAL, "30")
				.containsEntry(LivenessConstants.CHECK_TIMEOUT, "5")
				.containsEntry(LivenessConstants.FAILURE_THRESHOLD, "3")
				.containsEntry(LivenessConstants.RETRY_MIN, "1")
				.containsEntry(LivenessConstants.RETRY_MAX, "30");
	}
}
