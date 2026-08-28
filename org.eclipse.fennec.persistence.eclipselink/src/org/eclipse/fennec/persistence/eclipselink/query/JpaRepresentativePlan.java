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
package org.eclipse.fennec.persistence.eclipselink.query;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The representatives of a grouped query, translated into a windowed query of its own
 * (issues #214, #259).
 * <p>
 * A grouped answer is a row of key plus aggregates, and decision R1 puts the representatives in
 * one cell of that row as a list of EObjects. No single SQL result can carry a nested list, so
 * they come from a second query and are stitched onto the groups by key. That is assembly rather
 * than post-filtering — the group membership and the window are both decided in the database.
 *
 * <pre>
 * SELECT t.age, t FROM Person t, (
 *     SELECT e.pid AS pid,
 *            SQL('ROW_NUMBER() OVER (PARTITION BY ? ORDER BY ? ASC)', e.age, e.name) AS rn
 *     FROM Person e)
 *   sub
 *  WHERE t.pid = sub.pid AND sub.rn &gt; :repSkip AND sub.rn &lt;= :repUpper
 * </pre>
 *
 * The entity being grouped anchors the FROM clause itself — unlike an expansion there is no
 * parent to correlate against, and the derived table may not lead. Each row is the group key
 * values followed by the representative, so the stitching key is exactly what the main query
 * puts in its own key cells.
 *
 * @param jpql the windowed query
 * @param parameters the bound values, including the window bounds
 * @param alias the result cell the representatives go into
 * @param groupKeyAliases the group key cells, in the order the query selects them
 * @author Mark Hoffmann
 */
public record JpaRepresentativePlan(String jpql, Map<String, Object> parameters, String alias,
		List<String> groupKeyAliases) {

	/** The lower bound of the per-group window — rows with a higher row number are kept. */
	public static final String SKIP_PARAMETER = "repSkip";

	/** The upper bound of the per-group window: {@code offset + count}. */
	public static final String UPPER_PARAMETER = "repUpper";

	/**
	 * Creates a plan.
	 *
	 * @param jpql the windowed query
	 * @param parameters the bound values
	 * @param alias the result cell the representatives go into
	 * @param groupKeyAliases the group key cells the rows are keyed by
	 */
	public JpaRepresentativePlan {
		parameters = Collections.unmodifiableMap(new LinkedHashMap<>(parameters));
		groupKeyAliases = List.copyOf(groupKeyAliases);
	}
}
