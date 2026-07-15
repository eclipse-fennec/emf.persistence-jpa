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
package org.eclipse.fennec.persistence.eclipselink.mappings;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DirectReadQuery;

/**
 * Implemented by collection mappings that can provide an ID-only query returning the
 * primary keys of their targets for one source row (AP-47). The query is the mapping's
 * relational knowledge — where the target ids live (relation table, target table) —
 * while {@code ETransparentIndirectionPolicy} owns the EMF proxy semantics built on
 * top of the returned ids.
 *
 * @author Mark Hoffmann
 * @since 15.07.2026
 */
public interface ETargetIdQuerySupport {

	/**
	 * Builds the ID-only query returning the target primary-key values for one source
	 * row. Returns {@code null} when this mapping configuration cannot be served (e.g.
	 * composite keys) — the caller must then fall back to full materialisation.
	 *
	 * @param session the initialized session used to normalize the query
	 * @return the query or {@code null}
	 */
	DirectReadQuery buildTargetIdQuery(AbstractSession session);
}
