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

import java.time.Duration;

/**
 * Backend-specific connectivity check used by a {@link LivenessGate}. A successful,
 * exception-free return means the connection is considered alive; any exception counts
 * as a probe failure.
 * <p>
 * Implementations must be time-bounded: honor the given timeout as far as the underlying
 * driver allows (e.g. {@code Connection.isValid(seconds)} for JDBC). A probe that blocks
 * indefinitely wedges its gate's probe thread.
 *
 * @author Mark Hoffmann
 * @since 20.07.2026
 */
@FunctionalInterface
public interface ConnectionProbe {

	/**
	 * Checks the connection, returning normally if it is alive.
	 *
	 * @param timeout the maximum time the probe should take
	 * @throws Exception if the connection is not functional
	 */
	void ping(Duration timeout) throws Exception;
}
