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
package org.eclipse.fennec.persistence.test;

/**
 * Inheritance roundtrip tests for the {@code JOINED} strategy —
 * VehicleJ (abstract) → CarJ / MotorcycleJ, root has its own table and each
 * subclass has a separate table joined via the shared PK.
 */
class NonOsgiInheritanceJoinedTest extends NonOsgiInheritanceRoundtripBase {

	@Override
	protected String vehicleClassName() {
		return "VehicleJ";
	}

	@Override
	protected String carClassName() {
		return "CarJ";
	}

	@Override
	protected String motorcycleClassName() {
		return "MotorcycleJ";
	}
}
