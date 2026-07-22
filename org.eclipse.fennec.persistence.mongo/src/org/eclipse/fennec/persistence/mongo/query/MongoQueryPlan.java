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

	MongoQueryPlan(Query source, QueryShape shape, Bson filter, Bson sort, int skip, int limit) {
		this.source = Objects.requireNonNull(source, "source must not be null");
		this.shape = Objects.requireNonNull(shape, "shape must not be null");
		this.filter = filter;
		this.sort = sort;
		this.skip = skip;
		this.limit = limit;
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

	@Override
	public String toString() {
		return "MongoQueryPlan[shape=" + shape + ", filter=" + filter + ", sort=" + sort + ", skip=" + skip
				+ ", limit=" + limit + "]";
	}
}
