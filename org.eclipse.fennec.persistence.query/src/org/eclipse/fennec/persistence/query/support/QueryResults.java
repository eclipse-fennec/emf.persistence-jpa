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
package org.eclipse.fennec.persistence.query.support;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.eclipse.fennec.persistence.query.api.QueryResultRow;
import org.eclipse.fennec.persistence.query.api.QueryShape;

/**
 * Factories for {@link QueryResult}s. Backends wrap their cursors/streams here so shape
 * guarding, idempotent closing and resource release behave identically everywhere.
 * <p>
 * The accessor matching the result's shape returns the underlying stream — repeated calls
 * return the <em>same</em> stream instance (a stream is consumable once). Accessors not
 * matching the shape throw {@link IllegalStateException}. {@link QueryResult#close()} is
 * idempotent, closes the stream (triggering its {@code onClose} chain) and never throws.
 *
 * @author Mark Hoffmann
 * @since 23.07.2026
 */
public final class QueryResults {

	private static final Logger LOGGER = Logger.getLogger(QueryResults.class.getName());

	private QueryResults() {
	}

	/**
	 * Creates an {@link QueryShape#OBJECTS} result over an entity stream.
	 *
	 * @param objects the entity stream (backend cursor release belongs in its
	 *        {@code onClose} chain), must not be {@code null}
	 * @return the result; must be closed by the caller
	 */
	public static QueryResult objects(Stream<EObject> objects) {
		Objects.requireNonNull(objects, "objects stream must not be null");
		return new StreamResult(QueryShape.OBJECTS, objects, null);
	}

	/**
	 * Creates a {@link QueryShape#PROJECTION} or {@link QueryShape#AGGREGATION} result
	 * over a row stream.
	 *
	 * @param shape the row shape, must be {@code PROJECTION} or {@code AGGREGATION}
	 * @param rows the row stream (backend cursor release belongs in its {@code onClose}
	 *        chain), must not be {@code null}
	 * @return the result; must be closed by the caller
	 */
	public static QueryResult rows(QueryShape shape, Stream<QueryResultRow> rows) {
		Objects.requireNonNull(shape, "shape must not be null");
		Objects.requireNonNull(rows, "row stream must not be null");
		if (shape != QueryShape.PROJECTION && shape != QueryShape.AGGREGATION) {
			throw new IllegalArgumentException("Row results require PROJECTION or AGGREGATION, was " + shape);
		}
		return new StreamResult(shape, null, rows);
	}

	/**
	 * Creates a {@link QueryShape#COUNT} result.
	 *
	 * @param count the cardinality
	 * @return the result; closing is a no-op
	 */
	public static QueryResult count(long count) {
		return new CountResult(count);
	}

	private static final class StreamResult implements QueryResult {

		private final QueryShape shape;
		private final Stream<EObject> objects;
		private final Stream<QueryResultRow> rows;
		private boolean closed;

		private StreamResult(QueryShape shape, Stream<EObject> objects, Stream<QueryResultRow> rows) {
			this.shape = shape;
			this.objects = objects;
			this.rows = rows;
		}

		@Override
		public QueryShape shape() {
			return shape;
		}

		@Override
		public Stream<EObject> objects() {
			if (shape != QueryShape.OBJECTS) {
				throw new IllegalStateException("objects() is only valid for OBJECTS results, this is " + shape);
			}
			return objects;
		}

		@Override
		public Stream<QueryResultRow> rows() {
			if (shape != QueryShape.PROJECTION && shape != QueryShape.AGGREGATION) {
				throw new IllegalStateException(
						"rows() is only valid for PROJECTION/AGGREGATION results, this is " + shape);
			}
			return rows;
		}

		@Override
		public long count() {
			throw new IllegalStateException("count() is only valid for COUNT results, this is " + shape);
		}

		@Override
		public void close() {
			if (closed) {
				return;
			}
			closed = true;
			try {
				if (objects != null) {
					objects.close();
				}
				if (rows != null) {
					rows.close();
				}
			} catch (RuntimeException e) {
				// close() never throws by contract — backend release failures are logged
				LOGGER.log(Level.WARNING, "Closing a query result failed", e);
			}
		}
	}

	private static final class CountResult implements QueryResult {

		private final long count;

		private CountResult(long count) {
			this.count = count;
		}

		@Override
		public QueryShape shape() {
			return QueryShape.COUNT;
		}

		@Override
		public Stream<EObject> objects() {
			throw new IllegalStateException("objects() is only valid for OBJECTS results, this is COUNT");
		}

		@Override
		public Stream<QueryResultRow> rows() {
			throw new IllegalStateException("rows() is only valid for PROJECTION/AGGREGATION results, this is COUNT");
		}

		@Override
		public long count() {
			return count;
		}

		@Override
		public void close() {
			// nothing to release
		}
	}
}
