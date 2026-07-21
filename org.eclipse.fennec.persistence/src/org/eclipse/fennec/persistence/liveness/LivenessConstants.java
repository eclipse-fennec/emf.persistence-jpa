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

/**
 * Constants of the connection-liveness support.
 *
 * @author Mark Hoffmann
 * @since 20.07.2026
 */
public interface LivenessConstants {

	/** Prefix of all liveness configuration keys. */
	String PREFIX = "liveness.";

	/** {@code false} disables probing: the service is registered immediately. Default {@code true}. */
	String ENABLED = PREFIX + "enabled";

	/** Probe period in seconds while UP; {@code 0} = no periodic re-check. Default {@code 30}. */
	String CHECK_INTERVAL = PREFIX + "checkInterval";

	/** Timeout per probe in seconds. Default {@code 5}. */
	String CHECK_TIMEOUT = PREFIX + "checkTimeout";

	/** Consecutive probe failures before the service is unregistered. Default {@code 3}. */
	String FAILURE_THRESHOLD = PREFIX + "failureThreshold";

	/** Lower bound of the retry backoff in seconds while DOWN. Default {@code 1}. */
	String RETRY_MIN = PREFIX + "retryMin";

	/** Upper bound of the retry backoff in seconds while DOWN. Default {@code 30}. */
	String RETRY_MAX = PREFIX + "retryMax";

	/**
	 * Prefix of the {@code osgi.condition.id} a gate registers while UP; the gate ident
	 * is appended.
	 */
	String CONDITION_ID_PREFIX = "fennec.liveness.";

	/** Marker service property carried by liveness-gated re-registrations. */
	String CHECKED_PROPERTY = "fennec.liveness";

	/** Value of {@link #CHECKED_PROPERTY}. */
	String CHECKED_VALUE = "checked";

	/** Factory PID of the gated {@code DataSource} re-publisher. */
	String JDBC_GATE_PID = "persistence.jdbc.gate";

	/** Backend type reported for JDBC gates. */
	String BACKEND_JDBC = "jdbc";

	/** Backend type reported for MongoDB gates. */
	String BACKEND_MONGO = "mongo";

	/** {@link org.eclipse.fennec.persistence.liveness.dto.GateDTO#state} while the connection is verified. */
	String STATE_UP = "UP";

	/** {@link org.eclipse.fennec.persistence.liveness.dto.GateDTO#state} while the connection is not verified. */
	String STATE_DOWN = "DOWN";
}
