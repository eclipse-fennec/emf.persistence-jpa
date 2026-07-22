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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.model.query.Chaining;
import org.eclipse.fennec.model.query.Comparator;
import org.eclipse.fennec.model.query.IsInRange;
import org.eclipse.fennec.model.query.Operation;
import org.eclipse.fennec.model.query.QObject;
import org.eclipse.fennec.model.query.QSubject;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.QueryFactory;
import org.eclipse.fennec.model.query.SimpleValueComparator;
import org.eclipse.fennec.model.query.SortEntity;
import org.eclipse.fennec.model.query.SortOrder;
import org.eclipse.fennec.model.utilities.FeaturePath;
import org.eclipse.fennec.model.utilities.UtilitiesFactory;

/**
 * Fluent builder producing {@link Query} EObjects — hand-assembling the containment
 * trees of the query model is impractical; this is the intended construction API.
 * <p>
 * Chaining semantics match the analyzer contract of the persistence query SPI: the first
 * {@code where} entry is the base predicate, every further entry chains with the
 * semantics of its type ({@code and}/{@code or}); {@code not} negates its own predicate
 * and chains conjunctively. Comparator values are the query model's string literals — use
 * {@link WhereStep#eqParam(String)} style methods or {@code ":name"} values for prepared
 * queries, and {@code "::"} to escape a literal leading colon.
 *
 * <pre>
 * Query query = QueryBuilder.create()
 *     .from(personClass)
 *     .where(namePath).toLower().contains("smith")
 *     .and(agePath).gte(18)
 *     .sortBy(ageFeature, SortOrder.ASC)
 *     .limit(10)
 *     .build();
 * </pre>
 *
 * @author Mark Hoffmann
 * @since 23.07.2026
 */
public final class QueryBuilder {

	private final QueryFactory factory = QueryFactory.eINSTANCE;
	private final Query query = QueryFactory.eINSTANCE.createQuery();

	private QueryBuilder() {
	}

	/**
	 * @return a fresh builder for an empty query (OBJECTS-shaped until subjects,
	 *         aggregates or {@link #count()} are added)
	 */
	public static QueryBuilder create() {
		return new QueryBuilder();
	}

	/**
	 * Creates a {@link FeaturePath} from path segments — a convenience for callers that
	 * do not want to touch the utilities factory.
	 *
	 * @param segments the path segments, root feature first
	 * @return the feature path
	 */
	public static FeaturePath path(EStructuralFeature... segments) {
		FeaturePath path = UtilitiesFactory.eINSTANCE.createFeaturePath();
		for (EStructuralFeature segment : segments) {
			Objects.requireNonNull(segment, "path segment must not be null");
			path.getFeature().add(segment);
		}
		return path;
	}

	// ==================== from / type filter ====================

	/**
	 * Adds a type filter: the query selects from the given root type.
	 *
	 * @param rootEClass the root type
	 * @return this builder
	 */
	public QueryBuilder from(EClass rootEClass) {
		Objects.requireNonNull(rootEClass, "rootEClass must not be null");
		QObject from = factory.createQObject();
		from.setRootEClass(rootEClass);
		query.getFrom().add(from);
		return this;
	}

	// ==================== where ====================

	/**
	 * Starts the base predicate (first {@code where} entry).
	 *
	 * @param segments the feature path the predicate addresses, root feature first
	 * @return the predicate step; its comparator call returns this builder
	 */
	public WhereStep where(EStructuralFeature... segments) {
		return new WhereStep(this, factory.createAnd(), path(segments));
	}

	/**
	 * Chains a further predicate conjunctively.
	 *
	 * @param segments the feature path the predicate addresses, root feature first
	 * @return the predicate step; its comparator call returns this builder
	 */
	public WhereStep and(EStructuralFeature... segments) {
		return new WhereStep(this, factory.createAnd(), path(segments));
	}

	/**
	 * Chains a further predicate disjunctively.
	 *
	 * @param segments the feature path the predicate addresses, root feature first
	 * @return the predicate step; its comparator call returns this builder
	 */
	public WhereStep or(EStructuralFeature... segments) {
		return new WhereStep(this, factory.createOr(), path(segments));
	}

	/**
	 * Chains a negated predicate (conjunctively).
	 *
	 * @param segments the feature path the predicate addresses, root feature first
	 * @return the predicate step; its comparator call returns this builder
	 */
	public WhereStep not(EStructuralFeature... segments) {
		return new WhereStep(this, factory.createNot(), path(segments));
	}

	// ==================== subjects / projection ====================

	/**
	 * Adds a plain projection subject.
	 *
	 * @param segments the feature path to project, root feature first
	 * @return this builder
	 */
	public QueryBuilder select(EStructuralFeature... segments) {
		return subject(null, null, segments);
	}

	/**
	 * Adds a plain projection subject with an alias.
	 *
	 * @param alias the result alias
	 * @param segments the feature path to project, root feature first
	 * @return this builder
	 */
	public QueryBuilder selectAs(String alias, EStructuralFeature... segments) {
		return subject(alias, null, segments);
	}

	/**
	 * Adds an average aggregate subject.
	 *
	 * @param alias the result alias; may be {@code null}
	 * @param segments the feature path to aggregate, root feature first
	 * @return this builder
	 */
	public QueryBuilder avg(String alias, EStructuralFeature... segments) {
		return subject(alias, factory.createAverage(), segments);
	}

	/**
	 * Adds a minimum aggregate subject.
	 *
	 * @param alias the result alias; may be {@code null}
	 * @param segments the feature path to aggregate, root feature first
	 * @return this builder
	 */
	public QueryBuilder min(String alias, EStructuralFeature... segments) {
		return subject(alias, factory.createMin(), segments);
	}

	/**
	 * Adds a maximum aggregate subject.
	 *
	 * @param alias the result alias; may be {@code null}
	 * @param segments the feature path to aggregate, root feature first
	 * @return this builder
	 */
	public QueryBuilder max(String alias, EStructuralFeature... segments) {
		return subject(alias, factory.createMax(), segments);
	}

	/**
	 * Adds a sum aggregate subject.
	 *
	 * @param alias the result alias; may be {@code null}
	 * @param segments the feature path to aggregate, root feature first
	 * @return this builder
	 */
	public QueryBuilder sum(String alias, EStructuralFeature... segments) {
		return subject(alias, factory.createSum(), segments);
	}

	/**
	 * Adds a count aggregate subject (per group, or whole result set without groupBy).
	 *
	 * @param alias the result alias; may be {@code null}
	 * @param segments the feature path to count, root feature first
	 * @return this builder
	 */
	public QueryBuilder countOf(String alias, EStructuralFeature... segments) {
		return subject(alias, factory.createCountOperation(), segments);
	}

	/**
	 * Adds a lower-cased projection subject.
	 *
	 * @param alias the result alias; may be {@code null}
	 * @param segments the feature path to project, root feature first
	 * @return this builder
	 */
	public QueryBuilder toLower(String alias, EStructuralFeature... segments) {
		return subject(alias, factory.createToLowerCase(), segments);
	}

	/**
	 * Adds an upper-cased projection subject.
	 *
	 * @param alias the result alias; may be {@code null}
	 * @param segments the feature path to project, root feature first
	 * @return this builder
	 */
	public QueryBuilder toUpper(String alias, EStructuralFeature... segments) {
		return subject(alias, factory.createToUpperCase(), segments);
	}

	private QueryBuilder subject(String alias, Operation operation, EStructuralFeature... segments) {
		if (segments.length == 0) {
			throw new IllegalArgumentException("a subject needs at least one path segment");
		}
		QSubject subject = factory.createQSubject();
		subject.setFeaturePath(path(segments));
		if (alias != null) {
			subject.setAlias(alias);
		}
		if (operation != null) {
			subject.setOperation(operation);
		}
		query.getSubject().add(subject);
		return this;
	}

	// ==================== grouping / shaping ====================

	/**
	 * Adds grouping paths.
	 *
	 * @param segments the feature path to group by, root feature first
	 * @return this builder
	 */
	public QueryBuilder groupBy(EStructuralFeature... segments) {
		query.getGroupBy().add(path(segments));
		return this;
	}

	/**
	 * Adds a sort entry.
	 *
	 * @param feature the feature to sort by
	 * @param order the sort order
	 * @return this builder
	 */
	public QueryBuilder sortBy(EStructuralFeature feature, SortOrder order) {
		Objects.requireNonNull(feature, "sort feature must not be null");
		SortEntity sort = factory.createSortEntity();
		sort.setSortFeature(feature);
		sort.setSortOrder(order == null ? SortOrder.DESC : order);
		query.getSortBy().add(sort);
		return this;
	}

	/**
	 * Caps the result cardinality.
	 *
	 * @param limit the maximum number of results, must be positive
	 * @return this builder
	 */
	public QueryBuilder limit(int limit) {
		if (limit <= 0) {
			throw new IllegalArgumentException("limit must be positive, was " + limit);
		}
		query.setLimit(limit);
		return this;
	}

	/**
	 * Skips leading results.
	 *
	 * @param skip the offset, must not be negative
	 * @return this builder
	 */
	public QueryBuilder skip(int skip) {
		if (skip < 0) {
			throw new IllegalArgumentException("skip must not be negative, was " + skip);
		}
		query.setSkip(skip);
		return this;
	}

	/**
	 * Requests duplicate elimination.
	 *
	 * @return this builder
	 */
	public QueryBuilder distinct() {
		query.setDistinct(true);
		return this;
	}

	/**
	 * Requests a count-only result.
	 *
	 * @return this builder
	 */
	public QueryBuilder count() {
		query.setCount(true);
		return this;
	}

	/**
	 * Names the query and marks it to be saved for later reuse.
	 *
	 * @param name the query name
	 * @return this builder
	 */
	public QueryBuilder named(String name) {
		Objects.requireNonNull(name, "query name must not be null");
		query.setName(name);
		query.setSaveQuery(true);
		return this;
	}

	/**
	 * @return the built query
	 */
	public Query build() {
		return query;
	}

	/**
	 * One predicate under construction: the feature path is fixed, an optional value
	 * operation may be applied, and exactly one comparator call finishes the step and
	 * returns the owning builder.
	 */
	public static final class WhereStep {

		private final QueryBuilder builder;
		private final Chaining entry;
		private final FeaturePath featurePath;
		private Operation operation;

		private WhereStep(QueryBuilder builder, Chaining entry, FeaturePath featurePath) {
			if (featurePath.getFeature().isEmpty()) {
				throw new IllegalArgumentException("a predicate needs at least one path segment");
			}
			this.builder = builder;
			this.entry = entry;
			this.featurePath = featurePath;
		}

		/**
		 * Applies {@code toLowerCase} to the path value before comparison.
		 *
		 * @return this step
		 */
		public WhereStep toLower() {
			operation = builder.factory.createToLowerCase();
			return this;
		}

		/**
		 * Applies {@code toUpperCase} to the path value before comparison.
		 *
		 * @return this step
		 */
		public WhereStep toUpper() {
			operation = builder.factory.createToUpperCase();
			return this;
		}

		// --- equality / ordering ---

		/**
		 * @param value the literal to compare against
		 * @return the owning builder
		 */
		public QueryBuilder eq(Object value) {
			return finish(builder.factory.createEq(), value);
		}

		/**
		 * Equality against a named parameter ({@code :name}).
		 *
		 * @param parameterName the parameter name without prefix
		 * @return the owning builder
		 */
		public QueryBuilder eqParam(String parameterName) {
			return finish(builder.factory.createEq(), ":" + parameterName);
		}

		/**
		 * @param value the literal to compare against
		 * @return the owning builder
		 */
		public QueryBuilder lt(Object value) {
			return finish(builder.factory.createLt(), value);
		}

		/**
		 * @param value the literal to compare against
		 * @return the owning builder
		 */
		public QueryBuilder lte(Object value) {
			return finish(builder.factory.createLte(), value);
		}

		/**
		 * @param value the literal to compare against
		 * @return the owning builder
		 */
		public QueryBuilder gt(Object value) {
			return finish(builder.factory.createGt(), value);
		}

		/**
		 * @param value the literal to compare against
		 * @return the owning builder
		 */
		public QueryBuilder gte(Object value) {
			return finish(builder.factory.createGte(), value);
		}

		// --- strings ---

		/**
		 * @param value the substring to match
		 * @return the owning builder
		 */
		public QueryBuilder contains(String value) {
			return finish(builder.factory.createContains(), value);
		}

		/**
		 * @param value the prefix to match
		 * @return the owning builder
		 */
		public QueryBuilder startsWith(String value) {
			return finish(builder.factory.createStartWith(), value);
		}

		/**
		 * @param value the suffix to match
		 * @return the owning builder
		 */
		public QueryBuilder endsWith(String value) {
			return finish(builder.factory.createEndsWith(), value);
		}

		/**
		 * @param pattern the like pattern
		 * @return the owning builder
		 */
		public QueryBuilder like(String pattern) {
			return finish(builder.factory.createLike(), pattern);
		}

		// --- dates ---

		/**
		 * @param value the date literal
		 * @return the owning builder
		 */
		public QueryBuilder isBefore(Object value) {
			return finish(builder.factory.createIsBefore(), value);
		}

		/**
		 * @param value the date literal
		 * @return the owning builder
		 */
		public QueryBuilder isAfter(Object value) {
			return finish(builder.factory.createIsAfter(), value);
		}

		/**
		 * @param value the date literal
		 * @return the owning builder
		 */
		public QueryBuilder isBeforeOrEqual(Object value) {
			return finish(builder.factory.createIsBeforeOrEqual(), value);
		}

		/**
		 * @param value the date literal
		 * @return the owning builder
		 */
		public QueryBuilder isAfterOrEqual(Object value) {
			return finish(builder.factory.createIsAfterOrEqual(), value);
		}

		// --- range / enum / boolean ---

		/**
		 * Range comparison with inclusive bounds.
		 *
		 * @param start the lower bound literal
		 * @param end the upper bound literal
		 * @return the owning builder
		 */
		public QueryBuilder inRange(Object start, Object end) {
			return inRange(start, end, true, true);
		}

		/**
		 * Range comparison.
		 *
		 * @param start the lower bound literal
		 * @param end the upper bound literal
		 * @param startIncluded whether the lower bound is inclusive
		 * @param endIncluded whether the upper bound is inclusive
		 * @return the owning builder
		 */
		public QueryBuilder inRange(Object start, Object end, boolean startIncluded, boolean endIncluded) {
			IsInRange range = builder.factory.createIsInRange();
			range.setStartValue(literal(start));
			range.setEndValue(literal(end));
			range.setStartIncluded(startIncluded);
			range.setEndIncluded(endIncluded);
			return attach(range);
		}

		/**
		 * @param literalName the enum literal name
		 * @return the owning builder
		 */
		public QueryBuilder isLiteral(String literalName) {
			return finish(builder.factory.createIsLiteral(), literalName);
		}

		/**
		 * @param value the boolean value to match
		 * @return the owning builder
		 */
		public QueryBuilder isBool(boolean value) {
			return finish(builder.factory.createIsBool(), Boolean.toString(value));
		}

		private QueryBuilder finish(SimpleValueComparator comparator, Object value) {
			comparator.setValue(literal(value));
			return attach(comparator);
		}

		private QueryBuilder attach(Comparator comparator) {
			entry.setFeaturePath(featurePath);
			entry.setComparator(comparator);
			if (operation != null) {
				entry.setOperation(operation);
			}
			builder.query.getWhere().add(entry);
			return builder;
		}

		private static String literal(Object value) {
			return value == null ? null : String.valueOf(value);
		}
	}
}
