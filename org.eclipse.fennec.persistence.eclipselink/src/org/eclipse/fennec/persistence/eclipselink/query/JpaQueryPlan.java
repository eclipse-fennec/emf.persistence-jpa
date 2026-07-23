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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.persistence.query.api.QueryPlan;
import org.eclipse.fennec.persistence.query.api.QueryShape;

/**
 * The JPA translation of an expression-IR {@link Query}: a JPQL string plus its bound
 * parameters, executed via {@code em.createQuery(jpql)} with
 * {@code setFirstResult}/{@code setMaxResults} from {@link #skip()}/{@link #limit()}.
 * <p>
 * Identifiers in the string are model names; every comparison value — literal or
 * parameter — travels as a named JPQL parameter, so the JPQL is injection-safe by
 * construction.
 *
 * @author Mark Hoffmann
 * @since 24.07.2026
 */
public final class JpaQueryPlan implements QueryPlan {

	private final Query source;
	private final QueryShape shape;
	private final String jpql;
	private final Map<String, Object> parameters;
	private final int skip;
	private final int limit;
	private final List<String> rowKeys;
	private final List<String> rowAliases;

	JpaQueryPlan(Query source, QueryShape shape, String jpql, Map<String, Object> parameters, int skip, int limit,
			List<String> rowKeys, List<String> rowAliases) {
		this.source = Objects.requireNonNull(source, "source must not be null");
		this.shape = Objects.requireNonNull(shape, "shape must not be null");
		this.jpql = Objects.requireNonNull(jpql, "jpql must not be null");
		this.parameters = parameters == null || parameters.isEmpty()
				? Collections.emptyMap()
				: Collections.unmodifiableMap(new LinkedHashMap<>(parameters));
		this.skip = skip;
		this.limit = limit;
		this.rowKeys = rowKeys == null ? List.of() : List.copyOf(rowKeys);
		this.rowAliases = rowAliases == null ? List.of()
				: Collections.unmodifiableList(new ArrayList<>(rowAliases));
	}

	@Override
	public Query source() {
		return source;
	}

	@Override
	public QueryShape shape() {
		return shape;
	}

	/**
	 * @return the JPQL query string
	 */
	public String jpql() {
		return jpql;
	}

	/**
	 * @return the named parameters to bind on the created query
	 */
	public Map<String, Object> parameters() {
		return parameters;
	}

	/**
	 * @return the number of leading results to skip ({@code setFirstResult}); {@code 0} = none
	 */
	public int skip() {
		return skip;
	}

	/**
	 * @return the result cap ({@code setMaxResults}); {@code 0} = unlimited
	 */
	public int limit() {
		return limit;
	}

	/**
	 * The result column keys of a row-shaped plan in subject order (the JPQL {@code AS}
	 * result variables) — used to map tuples to {@code QueryResultRow} cells.
	 *
	 * @return the row keys; empty for OBJECTS/COUNT plans
	 */
	public List<String> rowKeys() {
		return rowKeys;
	}

	/**
	 * The column aliases in subject order; {@code null} entries mark ordinal-only cells.
	 *
	 * @return the row aliases; empty for OBJECTS/COUNT plans
	 */
	public List<String> rowAliases() {
		return rowAliases;
	}

	@Override
	public String toString() {
		return "JpaQueryPlan[shape=" + shape + ", jpql=" + jpql + ", parameters=" + parameters.keySet() + ", skip="
				+ skip + ", limit=" + limit + "]";
	}
}
