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
package org.eclipse.fennec.persistence.mongo.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.bson.conversions.Bson;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.persistence.query.api.QueryPlan;
import org.eclipse.fennec.persistence.query.api.QueryShape;

/**
 * The Mongo translation of a canonical {@link Query} — the find path: a filter document
 * plus sort/skip/limit, executed by the Mongo resource via {@code collection.find(...)}
 * (or {@code countDocuments(filter)} for {@link QueryShape#COUNT}).
 * <p>
 * PROJECTION and AGGREGATION shapes translate to an aggregation pipeline instead and are
 * delivered by the aggregation stage of the processor.
 *
 * @author Mark Hoffmann
 * @since 23.07.2026
 */
public final class MongoQueryPlan implements QueryPlan {

	private final Query source;
	private final QueryShape shape;
	private final Bson filter;
	private final Bson sort;
	private final int skip;
	private final int limit;
	private final List<Bson> pipeline;
	private final List<String> rowKeys;
	private final List<String> rowAliases;

	MongoQueryPlan(Query source, QueryShape shape, Bson filter, Bson sort, int skip, int limit) {
		this(source, shape, filter, sort, skip, limit, null, null, null);
	}

	MongoQueryPlan(Query source, QueryShape shape, Bson filter, Bson sort, int skip, int limit,
			List<Bson> pipeline, List<String> rowKeys, List<String> rowAliases) {
		this.source = Objects.requireNonNull(source, "source must not be null");
		this.shape = Objects.requireNonNull(shape, "shape must not be null");
		this.filter = filter;
		this.sort = sort;
		this.skip = skip;
		this.limit = limit;
		this.pipeline = pipeline == null ? null : List.copyOf(pipeline);
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
	 * @return the filter document; {@code null} = no filter (match all)
	 */
	public Bson filter() {
		return filter;
	}

	/**
	 * @return the sort document; {@code null} = no ordering
	 */
	public Bson sort() {
		return sort;
	}

	/**
	 * @return the number of leading results to skip; {@code 0} = none
	 */
	public int skip() {
		return skip;
	}

	/**
	 * @return the result cap; {@code 0} = unlimited
	 */
	public int limit() {
		return limit;
	}

	/**
	 * @return {@code true} if this plan executes as an aggregation pipeline
	 *         ({@code collection.aggregate(pipeline())}) instead of a find
	 */
	public boolean aggregation() {
		return pipeline != null;
	}

	/**
	 * @return the aggregation pipeline stages; {@code null} for find-path plans
	 */
	public List<Bson> pipeline() {
		return pipeline;
	}

	/**
	 * The output document keys of a PROJECTION/AGGREGATION plan, in subject order —
	 * used to map result documents to {@code QueryResultRow} cells.
	 *
	 * @return the row keys; empty for find-path plans
	 */
	public List<String> rowKeys() {
		return rowKeys;
	}

	/**
	 * The subject aliases in subject order; entries may be {@code null} for subjects
	 * without an alias (ordinal access only).
	 *
	 * @return the row aliases; empty for find-path plans
	 */
	public List<String> rowAliases() {
		return rowAliases;
	}

	@Override
	public String toString() {
		return "MongoQueryPlan[shape=" + shape + (aggregation() ? ", pipeline=" + pipeline : ", filter=" + filter)
				+ ", sort=" + sort + ", skip=" + skip + ", limit=" + limit + "]";
	}
}
