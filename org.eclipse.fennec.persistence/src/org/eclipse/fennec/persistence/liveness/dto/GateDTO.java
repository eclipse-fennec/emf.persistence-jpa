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
package org.eclipse.fennec.persistence.liveness.dto;

import java.util.Map;

import org.osgi.dto.DTO;

/**
 * Snapshot of one liveness gate.
 *
 * @author Mark Hoffmann
 * @since 20.07.2026
 */
public class GateDTO extends DTO {

	/** Gate identifier (client ident / gate configuration name). */
	public String ident;

	/** Backend type, {@code "mongo"} or {@code "jdbc"}. */
	public String backendType;

	/** Gate state, {@code "UP"} or {@code "DOWN"}. */
	public String state;

	/** Epoch millis of the last successful probe, {@code 0} if never. */
	public long lastSuccess;

	/** Epoch millis of the last failed probe, {@code 0} if never. */
	public long lastFailure;

	/** Message of the most recent probe failure, {@code null} after a success. */
	public String lastFailureMessage;

	/** Number of consecutive probe failures. */
	public int consecutiveFailures;

	/** Total number of probes executed. */
	public long probeCount;

	/** Effective {@code liveness.*} settings. */
	public Map<String, String> config;
}
