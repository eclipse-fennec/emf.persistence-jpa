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
package org.eclipse.fennec.model.query.builder;

import java.util.Objects;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.query.Expand;
import org.eclipse.fennec.model.query.OrderBy;
import org.eclipse.fennec.model.query.QueryFactory;
import org.eclipse.fennec.model.query.SortDirection;

/**
 * Fluent construction of {@link Expand} — a reference path to resolve eagerly, optionally
 * narrowed by query options (issue #238).
 * <p>
 * An expansion resolves proxies; both backends already deliver non-containment references as
 * such. The options therefore select <em>which</em> proxies get resolved, never which entries
 * the feature holds: the collection keeps everything the store has, and what is delivered is
 * exactly the selected set. See {@code docs/unified-persistence/expand-query-options.md}.
 * <p>
 * Built without options this is the plain fetch hint of issue #95:
 *
 * <pre>{@code
 * Expands.of(orders).build()
 * Expands.of(orders).filter(path(amount).gt(100)).top(5).orderByDesc(date).build()
 * }</pre>
 *
 * The builder does not judge which options a backend can serve — an unserved combination is
 * refused by {@code validate()} against {@code EXPAND_FILTER} / {@code EXPAND_PAGE}, not here.
 *
 * @author Mark Hoffmann
 */
public final class Expands {

	private static final QueryFactory FACTORY = QueryFactory.eINSTANCE;

	private Expands() {
		// no instances
	}

	/**
	 * Starts an expansion over the given reference path.
	 *
	 * @param segments the reference path to expand, root feature first
	 * @return a builder for the expansion
	 */
	public static Builder of(EStructuralFeature... segments) {
		return new Builder(Expressions.propertyPath(segments));
	}

	/** Fluent builder for one {@link Expand}. */
	public static final class Builder {

		private final Expand expand = FACTORY.createExpand();

		private Builder(org.eclipse.fennec.model.expression.PropertyPath path) {
			expand.setPath(path);
		}

		/**
		 * Narrows which children are resolved. The expression addresses the expanded type,
		 * not the query root. Requires {@code EXPAND_FILTER}.
		 *
		 * @param filter the filter expression
		 * @return this builder
		 */
		public Builder filter(Expression filter) {
			expand.setFilter(Objects.requireNonNull(filter, "expand filter must not be null"));
			return this;
		}

		/**
		 * Resolves at most {@code top} children per expanded parent. Requires
		 * {@code EXPAND_PAGE}.
		 *
		 * @param top the per-parent cap; 0 = all
		 * @return this builder
		 */
		public Builder top(int top) {
			expand.setTop(top);
			return this;
		}

		/**
		 * Skips this many children per expanded parent before resolving. Requires
		 * {@code EXPAND_PAGE}.
		 *
		 * @param skip the per-parent offset
		 * @return this builder
		 */
		public Builder skip(int skip) {
			expand.setSkip(skip);
			return this;
		}

		/**
		 * Orders the children before {@code top}/{@code skip} pick from them, ascending.
		 * <p>
		 * This is a <em>selector</em>, not a delivered order — the list order belongs to the
		 * store. It is served only together with {@code top} or {@code skip}; standing alone
		 * it is refused whatever a backend declares.
		 *
		 * @param segments the path to order by, relative to the expanded type
		 * @return this builder
		 */
		public Builder orderByAsc(EStructuralFeature... segments) {
			return orderBy(SortDirection.ASC, segments);
		}

		/**
		 * Orders the children before {@code top}/{@code skip} pick from them, descending.
		 *
		 * @param segments the path to order by, relative to the expanded type
		 * @return this builder
		 * @see #orderByAsc(EStructuralFeature...)
		 */
		public Builder orderByDesc(EStructuralFeature... segments) {
			return orderBy(SortDirection.DESC, segments);
		}

		private Builder orderBy(SortDirection direction, EStructuralFeature... segments) {
			OrderBy orderBy = FACTORY.createOrderBy();
			orderBy.setPath(Expressions.propertyPath(segments));
			orderBy.setDirection(direction);
			expand.getOrderBy().add(orderBy);
			return this;
		}

		/**
		 * Adds a nested expansion, relative to the expanded type.
		 *
		 * @param nested the nested expansion
		 * @return this builder
		 */
		public Builder expand(Expand nested) {
			expand.getExpand().add(Objects.requireNonNull(nested, "nested expand must not be null"));
			return this;
		}

		/** @return the built expansion */
		public Expand build() {
			return expand;
		}
	}
}
