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

import org.osgi.dto.DTO;

/**
 * Snapshot of all liveness gates known to the runtime.
 *
 * @author Mark Hoffmann
 * @since 20.07.2026
 */
public class LivenessRuntimeDTO extends DTO {

	/** All gates, in registration order. */
	public GateDTO[] gates;
}
