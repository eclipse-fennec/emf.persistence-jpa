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
package org.eclipse.fennec.persistence.eclipselink.dynamic;

import java.util.Optional;

import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 * Schema-generation action performed by {@link EDynamicHelper#addETypes(DdlAction, boolean, java.util.List)}
 * for dynamic types. Mirrors the values accepted by EclipseLink's
 * {@code eclipselink.ddl-generation} property.
 */
public enum DdlAction {

	NONE(PersistenceUnitProperties.NONE),
	CREATE_TABLES(PersistenceUnitProperties.CREATE_ONLY),
	DROP_AND_CREATE_TABLES(PersistenceUnitProperties.DROP_AND_CREATE),
	CREATE_OR_EXTEND_TABLES(PersistenceUnitProperties.CREATE_OR_EXTEND);

	private final String eclipseLinkValue;

	DdlAction(String eclipseLinkValue) {
		this.eclipseLinkValue = eclipseLinkValue;
	}

	/**
	 * @return the corresponding {@code eclipselink.ddl-generation} string value.
	 */
	public String getEclipseLinkValue() {
		return eclipseLinkValue;
	}

	/**
	 * Resolve the action for the given {@code eclipselink.ddl-generation} property value.
	 *
	 * @param value the raw property value (may be {@code null})
	 * @return the matching action, or {@link Optional#empty()} if {@code value} is non-null and
	 *         does not match any known EclipseLink ddl-generation value. A {@code null} value
	 *         resolves to {@link #NONE}.
	 */
	public static Optional<DdlAction> fromEclipseLinkValue(String value) {
		if (value == null) {
			return Optional.of(NONE);
		}
		for (DdlAction action : values()) {
			if (action.eclipseLinkValue.equals(value)) {
				return Optional.of(action);
			}
		}
		return Optional.empty();
	}
}
