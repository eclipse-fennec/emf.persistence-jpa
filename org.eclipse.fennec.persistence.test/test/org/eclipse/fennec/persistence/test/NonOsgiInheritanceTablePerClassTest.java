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
 * Inheritance roundtrip tests for the {@code TABLE_PER_CLASS} strategy —
 * VehicleTpc (abstract) → CarTpc / MotorcycleTpc. Each concrete subclass gets
 * its own table containing all inherited columns; no shared root table, no
 * discriminator.
 * <p>
 * Currently @Disabled — the existing TPC implementation (table-name routing,
 * InheritancePolicy→TablePerClassPolicy, child ID mapping, cross-descriptor
 * mapping cloning) is fundamentally incomplete for EclipseLink + Dynamic
 * entities. See AP-44 for the dedicated fix.
 */
// @Disabled("TABLE_PER_CLASS not yet functional with EclipseLink Dynamic — see AP-44")
class NonOsgiInheritanceTablePerClassTest extends NonOsgiInheritanceRoundtripBase {

	@Override
	protected String vehicleClassName() {
		return "VehicleTpc";
	}

	@Override
	protected String carClassName() {
		return "CarTpc";
	}

	@Override
	protected String motorcycleClassName() {
		return "MotorcycleTpc";
	}
}
