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
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EReference;

/**
 * One filtered expansion, translated into a keyed query of its own (issue #238).
 * <p>
 * A plain expansion needs no plan: it rides on the fetch joins and batch-fetch hints of issue
 * #95, which fetch everything the reference holds. A <em>filtered</em> one cannot — the hint has
 * no room for a predicate — so it becomes a second query, run once per chunk of roots:
 *
 * <pre>
 * SELECT e FROM Person p JOIN p.addresses e WHERE p.id IN :expandKeys AND (e.street = :p0)
 * </pre>
 *
 * The target carries the {@code e} alias, not the root. That is deliberate: the expression
 * translator renders every path against {@code ALIAS}, so making the target the alias lets the
 * filter — which addresses the expanded type — translate with no change to the translator at
 * all. The root only appears as the join source and in the key predicate, which this plan
 * builds by hand.
 * <p>
 * Executing it resolves nothing by itself; it registers the matching targets in the persistence
 * context, which is what makes the subsequent proxy resolution free (the mechanism issue #226
 * introduced for the save path). The ids it returns say which proxies belong to the expansion,
 * per decision D1: the collection keeps everything the store has, and exactly the selected
 * targets are resolved.
 *
 * @param path the reference path this expansion addresses, root feature first
 * @param jpql the keyed query; binds {@value #KEY_PARAMETER} to the root keys
 * @param parameters the filter's bound values, by name
 * @author Mark Hoffmann
 */
public record JpaExpandPlan(List<EReference> path, String jpql, Map<String, Object> parameters) {

	/** The parameter the root keys of one chunk are bound to. */
	public static final String KEY_PARAMETER = "expandKeys";

	/**
	 * Creates a plan.
	 *
	 * @param path the reference path, root feature first
	 * @param jpql the keyed query
	 * @param parameters the filter's bound values
	 */
	public JpaExpandPlan {
		path = List.copyOf(path);
		parameters = Collections.unmodifiableMap(new java.util.LinkedHashMap<>(parameters));
	}

	/** @return the last segment — the reference whose proxies this expansion resolves */
	public EReference target() {
		return path.get(path.size() - 1);
	}
}
