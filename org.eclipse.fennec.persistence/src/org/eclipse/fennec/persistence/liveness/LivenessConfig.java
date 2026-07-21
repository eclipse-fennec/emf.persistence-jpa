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

import static java.util.Objects.isNull;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Immutable configuration of a {@link LivenessGate}. All durations are configured in
 * seconds, matching the {@code liveness.*} keys documented in {@link LivenessConstants}.
 *
 * @author Mark Hoffmann
 * @since 20.07.2026
 */
public final class LivenessConfig {

	public static final boolean DEFAULT_ENABLED = true;
	public static final long DEFAULT_CHECK_INTERVAL_SECONDS = 30L;
	public static final long DEFAULT_CHECK_TIMEOUT_SECONDS = 5L;
	public static final int DEFAULT_FAILURE_THRESHOLD = 3;
	public static final long DEFAULT_RETRY_MIN_SECONDS = 1L;
	public static final long DEFAULT_RETRY_MAX_SECONDS = 30L;

	private final boolean enabled;
	private final long checkIntervalSeconds;
	private final long checkTimeoutSeconds;
	private final int failureThreshold;
	private final long retryMinSeconds;
	private final long retryMaxSeconds;

	private LivenessConfig(boolean enabled, long checkIntervalSeconds, long checkTimeoutSeconds,
			int failureThreshold, long retryMinSeconds, long retryMaxSeconds) {
		this.enabled = enabled;
		this.checkIntervalSeconds = Math.max(0, checkIntervalSeconds);
		this.checkTimeoutSeconds = Math.max(1, checkTimeoutSeconds);
		this.failureThreshold = Math.max(1, failureThreshold);
		this.retryMinSeconds = Math.max(0, retryMinSeconds);
		this.retryMaxSeconds = Math.max(this.retryMinSeconds, retryMaxSeconds);
	}

	/**
	 * Returns the configuration with all default values.
	 */
	public static LivenessConfig defaults() {
		return of(DEFAULT_ENABLED, DEFAULT_CHECK_INTERVAL_SECONDS, DEFAULT_CHECK_TIMEOUT_SECONDS,
				DEFAULT_FAILURE_THRESHOLD, DEFAULT_RETRY_MIN_SECONDS, DEFAULT_RETRY_MAX_SECONDS);
	}

	/**
	 * Creates a configuration from explicit values. Out-of-range values are clamped to
	 * sane bounds (interval &gt;= 0, timeout &gt;= 1, threshold &gt;= 1, retryMax &gt;= retryMin).
	 */
	public static LivenessConfig of(boolean enabled, long checkIntervalSeconds, long checkTimeoutSeconds,
			int failureThreshold, long retryMinSeconds, long retryMaxSeconds) {
		return new LivenessConfig(enabled, checkIntervalSeconds, checkTimeoutSeconds,
				failureThreshold, retryMinSeconds, retryMaxSeconds);
	}

	/**
	 * Creates a configuration from component properties using the {@code liveness.*}
	 * keys of {@link LivenessConstants}. Missing or unparsable values fall back to the
	 * defaults; values may be given as {@link Number}, {@link Boolean} or {@link String}.
	 */
	public static LivenessConfig fromProperties(Map<String, Object> properties) {
		return of(asBoolean(properties.get(LivenessConstants.ENABLED), DEFAULT_ENABLED),
				asLong(properties.get(LivenessConstants.CHECK_INTERVAL), DEFAULT_CHECK_INTERVAL_SECONDS),
				asLong(properties.get(LivenessConstants.CHECK_TIMEOUT), DEFAULT_CHECK_TIMEOUT_SECONDS),
				(int) asLong(properties.get(LivenessConstants.FAILURE_THRESHOLD), DEFAULT_FAILURE_THRESHOLD),
				asLong(properties.get(LivenessConstants.RETRY_MIN), DEFAULT_RETRY_MIN_SECONDS),
				asLong(properties.get(LivenessConstants.RETRY_MAX), DEFAULT_RETRY_MAX_SECONDS));
	}

	public boolean enabled() {
		return enabled;
	}

	public long checkIntervalSeconds() {
		return checkIntervalSeconds;
	}

	public Duration checkTimeout() {
		return Duration.ofSeconds(checkTimeoutSeconds);
	}

	public long checkTimeoutSeconds() {
		return checkTimeoutSeconds;
	}

	public int failureThreshold() {
		return failureThreshold;
	}

	public long retryMinSeconds() {
		return retryMinSeconds;
	}

	public long retryMaxSeconds() {
		return retryMaxSeconds;
	}

	/**
	 * Returns the effective settings as string map, used in the runtime DTO.
	 */
	public Map<String, String> asMap() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put(LivenessConstants.ENABLED, Boolean.toString(enabled));
		map.put(LivenessConstants.CHECK_INTERVAL, Long.toString(checkIntervalSeconds));
		map.put(LivenessConstants.CHECK_TIMEOUT, Long.toString(checkTimeoutSeconds));
		map.put(LivenessConstants.FAILURE_THRESHOLD, Integer.toString(failureThreshold));
		map.put(LivenessConstants.RETRY_MIN, Long.toString(retryMinSeconds));
		map.put(LivenessConstants.RETRY_MAX, Long.toString(retryMaxSeconds));
		return map;
	}

	private static boolean asBoolean(Object value, boolean defaultValue) {
		if (value instanceof Boolean b) {
			return b;
		}
		if (value instanceof String s && !s.isBlank()) {
			return Boolean.parseBoolean(s.trim());
		}
		return defaultValue;
	}

	private static long asLong(Object value, long defaultValue) {
		if (isNull(value)) {
			return defaultValue;
		}
		if (value instanceof Number n) {
			return n.longValue();
		}
		if (value instanceof String s && !s.isBlank()) {
			try {
				return Long.parseLong(s.trim());
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
		return defaultValue;
	}
}
