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
package org.eclipse.fennec.persistence.query;

/**
 * Constants of the query SPI that cannot be carried by the generated API model
 * (EMF does not model interface constants).
 *
 * @author Mark Hoffmann
 * @since 22.07.2026
 */
public final class QueryConstants {

	private QueryConstants() {
	}

	/**
	 * OSGi service property of a {@code QueryProcessor} carrying the backend id,
	 * e.g. {@code "mongo"} or {@code "jpa"}. A {@code QueryableResource} resolves
	 * the processor matching its backend via this property.
	 */
	public static final String BACKEND_PROPERTY = "persistence.query.backend";
}
