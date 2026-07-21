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

import org.eclipse.fennec.persistence.liveness.dto.LivenessRuntimeDTO;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Introspection service for connection liveness, following the whiteboard-runtime
 * pattern (cf. {@code HttpServiceRuntime}). The service is always registered —
 * independent of any gate state — and carries the standard
 * {@code service.changecount} property, incremented on every gate state transition,
 * so tooling can watch for changes instead of polling DTOs.
 *
 * @author Mark Hoffmann
 * @since 20.07.2026
 */
@ProviderType
public interface PersistenceLivenessRuntime {

	/**
	 * Returns a consistent snapshot of all liveness gates.
	 */
	LivenessRuntimeDTO getRuntimeDTO();
}
