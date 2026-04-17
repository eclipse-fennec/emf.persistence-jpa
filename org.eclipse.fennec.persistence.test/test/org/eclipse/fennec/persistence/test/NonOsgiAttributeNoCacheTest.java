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

import java.util.Map;

import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 * Same suite as {@link NonOsgiAttributeTest} with the shared L2 cache disabled.
 */
class NonOsgiAttributeNoCacheTest extends NonOsgiAttributeTest {

	@Override
	protected Map<String, Object> defaultProperties() {
		Map<String, Object> props = super.defaultProperties();
		props.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "false");
		return props;
	}
}
