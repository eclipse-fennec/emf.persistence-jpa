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
package org.eclipse.fennec.model.query.analysis;

/**
 * The role a feature path plays in a query, derived from the query-model slot it sits under
 * (issue #292).
 *
 * @author Mark Hoffmann
 * @since 23.09.2026
 */
public enum FeatureRole {

	/** Restricts which objects qualify — {@code Query.predicate}, a filter stage (also HAVING), an expand filter. */
	FILTER,
	/** Orders the result, an expansion window or the representatives of a group. */
	SORT,
	/** A projected column — {@code Selection.path} or {@code Selection.key}. */
	PROJECTION,
	/** A grouping key — {@code GroupByStage.paths} or {@code GroupKey.expression}. */
	GROUP,
	/** The input of an aggregate. */
	AGGREGATE,
	/** The input of a computed column. */
	COMPUTE,
	/** The reference path of an expansion. */
	EXPAND,
	/**
	 * The bounds of a representatives window. By contract a literal or a parameter, so a path
	 * here is an invalid query — the role exists so that no expression slot is left unmapped.
	 */
	PAGE

}
