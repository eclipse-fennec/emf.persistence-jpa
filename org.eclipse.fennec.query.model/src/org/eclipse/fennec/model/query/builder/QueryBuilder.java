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

import java.util.List;
import java.util.Objects;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.query.Aggregate;
import org.eclipse.fennec.model.query.AggregateMethod;
import org.eclipse.fennec.model.query.Computation;
import org.eclipse.fennec.model.query.ComputeStage;
import org.eclipse.fennec.model.query.FilterStage;
import org.eclipse.fennec.model.query.GroupByStage;
import org.eclipse.fennec.model.query.GroupKey;
import org.eclipse.fennec.model.query.OrderBy;
import org.eclipse.fennec.model.query.ParameterDecl;
import org.eclipse.fennec.model.query.Pipeline;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.QueryFactory;
import org.eclipse.fennec.model.query.Selection;
import org.eclipse.fennec.model.query.SortDirection;
import org.eclipse.fennec.model.query.Stage;

/**
 * Fluent builder for the query envelope; predicates come from the composable
 * {@link Expressions} factory:
 *
 * <pre>
 * import static org.eclipse.fennec.model.query.builder.Expressions.*;
 *
 * Query query = QueryBuilder.from(personClass)
 *     .where(and(
 *         or(path(name).eq("smith"), path(name).containsIgnoreCase("x")),
 *         path(age).ge(18),
 *         any(propertyPath(addresses), a -> a.path(street).startsWith("Main"))))
 *     .orderByAsc(age)
 *     .top(10).skip(5)
 *     .build();
 * </pre>
 *
 * Aggregation helpers create a single {@code GroupByStage} pipeline; richer pipelines
 * (filter/top/skip stages) can be composed via the model directly.
 *
 * @author Mark Hoffmann
 * @since 24.07.2026
 */
public final class QueryBuilder {

	private final QueryFactory factory = QueryFactory.eINSTANCE;
	private final Query query = QueryFactory.eINSTANCE.createQuery();
	private GroupByStage groupByStage;

	private QueryBuilder(EClass from) {
		query.setFrom(Objects.requireNonNull(from, "from must not be null"));
	}

	/**
	 * Starts a query on the given root type.
	 *
	 * @param from the root type the query selects from
	 * @return a fresh builder
	 */
	public static QueryBuilder from(EClass from) {
		return new QueryBuilder(from);
	}

	/**
	 * Sets the filter predicate (compose it via {@link Expressions}).
	 *
	 * @param predicate the boolean expression
	 * @return this builder
	 */
	public QueryBuilder where(Expression predicate) {
		query.setPredicate(Objects.requireNonNull(predicate, "predicate must not be null"));
		return this;
	}

	// ==================== ordering / projection / expand ====================

	/**
	 * Adds an ascending ordering over the given path.
	 *
	 * @param segments the path segments, root feature first
	 * @return this builder
	 */
	public QueryBuilder orderByAsc(EStructuralFeature... segments) {
		return orderBy(SortDirection.ASC, segments);
	}

	/**
	 * Adds a descending ordering over the given path.
	 *
	 * @param segments the path segments, root feature first
	 * @return this builder
	 */
	public QueryBuilder orderByDesc(EStructuralFeature... segments) {
		return orderBy(SortDirection.DESC, segments);
	}

	private QueryBuilder orderBy(SortDirection direction, EStructuralFeature... segments) {
		OrderBy orderBy = factory.createOrderBy();
		orderBy.setPath(Expressions.propertyPath(segments));
		orderBy.setDirection(direction);
		query.getOrderBy().add(orderBy);
		return this;
	}

	/**
	 * Adds an ascending ordering over an arbitrary value expression (issue #84).
	 *
	 * @param key the sort expression
	 * @return this builder
	 */
	public QueryBuilder orderByAsc(Expression key) {
		return orderByKey(SortDirection.ASC, key);
	}

	/**
	 * Adds a descending ordering over an arbitrary value expression (issue #84).
	 *
	 * @param key the sort expression
	 * @return this builder
	 */
	public QueryBuilder orderByDesc(Expression key) {
		return orderByKey(SortDirection.DESC, key);
	}

	private QueryBuilder orderByKey(SortDirection direction, Expression key) {
		OrderBy orderBy = factory.createOrderBy();
		orderBy.setKey(Objects.requireNonNull(key, "sort expression must not be null"));
		orderBy.setDirection(direction);
		query.getOrderBy().add(orderBy);
		return this;
	}

	/**
	 * Adds a projection subject (ordinal access only).
	 *
	 * @param segments the path segments, root feature first
	 * @return this builder
	 */
	public QueryBuilder select(EStructuralFeature... segments) {
		return selectAs(null, segments);
	}

	/**
	 * Adds a projection subject with an alias.
	 *
	 * @param alias the result column alias; may be {@code null}
	 * @param segments the path segments, root feature first
	 * @return this builder
	 */
	public QueryBuilder selectAs(String alias, EStructuralFeature... segments) {
		Selection selection = factory.createSelection();
		selection.setPath(Expressions.propertyPath(segments));
		if (alias != null) {
			selection.setAlias(alias);
		}
		query.getSelect().add(selection);
		return this;
	}

	/**
	 * Adds an eager-fetch hint.
	 *
	 * @param segments the reference path to materialise, root feature first
	 * @return this builder
	 */
	public QueryBuilder expand(EStructuralFeature... segments) {
		query.getExpand().add(Expressions.propertyPath(segments));
		return this;
	}

	// ==================== aggregation ====================

	/**
	 * Declares the grouping paths of the (single) group-by stage.
	 *
	 * @param segments the path to group by, root feature first
	 * @return this builder
	 */
	public QueryBuilder groupBy(EStructuralFeature... segments) {
		groupByStage().getPaths().add(Expressions.propertyPath(segments));
		return this;
	}

	/**
	 * Declares an expression-valued group key (issue #87). The mandatory alias names
	 * the key in the result rows; the expression may be any value expression,
	 * including an {@link Expressions#aliasRef(String) AliasRef} to a preceding
	 * {@link #computeAs(String, Expression) compute} alias.
	 *
	 * @param alias the result column alias of this key
	 * @param key the key expression
	 * @return this builder
	 */
	public QueryBuilder groupByAs(String alias, Expression key) {
		GroupKey groupKey = factory.createGroupKey();
		groupKey.setAlias(Objects.requireNonNull(alias, "group key alias must not be null"));
		groupKey.setExpression(Objects.requireNonNull(key, "group key expression must not be null"));
		groupByStage().getKeys().add(groupKey);
		return this;
	}

	/**
	 * Adds an average aggregate output.
	 *
	 * @param alias the result column alias
	 * @param segments the path to aggregate
	 * @return this builder
	 */
	public QueryBuilder avg(String alias, EStructuralFeature... segments) {
		return aggregate(AggregateMethod.AVG, alias, segments);
	}

	/**
	 * Adds a minimum aggregate output.
	 *
	 * @param alias the result column alias
	 * @param segments the path to aggregate
	 * @return this builder
	 */
	public QueryBuilder min(String alias, EStructuralFeature... segments) {
		return aggregate(AggregateMethod.MIN, alias, segments);
	}

	/**
	 * Adds a maximum aggregate output.
	 *
	 * @param alias the result column alias
	 * @param segments the path to aggregate
	 * @return this builder
	 */
	public QueryBuilder max(String alias, EStructuralFeature... segments) {
		return aggregate(AggregateMethod.MAX, alias, segments);
	}

	/**
	 * Adds a sum aggregate output.
	 *
	 * @param alias the result column alias
	 * @param segments the path to aggregate
	 * @return this builder
	 */
	public QueryBuilder sum(String alias, EStructuralFeature... segments) {
		return aggregate(AggregateMethod.SUM, alias, segments);
	}

	/**
	 * Adds a count aggregate output (group members; no path).
	 *
	 * @param alias the result column alias
	 * @return this builder
	 */
	public QueryBuilder countOf(String alias) {
		return aggregate(AggregateMethod.COUNT, alias);
	}

	/**
	 * Adds a count-distinct aggregate output.
	 *
	 * @param alias the result column alias
	 * @param segments the path whose distinct values are counted
	 * @return this builder
	 */
	public QueryBuilder countDistinct(String alias, EStructuralFeature... segments) {
		return aggregate(AggregateMethod.COUNT_DISTINCT, alias, segments);
	}

	/**
	 * Adds an average aggregate over a value expression (issue #87), e.g. an
	 * {@link Expressions#aliasRef(String) AliasRef} to a pre-group compute alias.
	 *
	 * @param alias the result column alias
	 * @param source the aggregated expression
	 * @return this builder
	 */
	public QueryBuilder avg(String alias, Expression source) {
		return aggregate(AggregateMethod.AVG, alias, source);
	}

	/** Expression-source variant of {@link #min(String, EStructuralFeature...)} (issue #87). */
	public QueryBuilder min(String alias, Expression source) {
		return aggregate(AggregateMethod.MIN, alias, source);
	}

	/** Expression-source variant of {@link #max(String, EStructuralFeature...)} (issue #87). */
	public QueryBuilder max(String alias, Expression source) {
		return aggregate(AggregateMethod.MAX, alias, source);
	}

	/** Expression-source variant of {@link #sum(String, EStructuralFeature...)} (issue #87). */
	public QueryBuilder sum(String alias, Expression source) {
		return aggregate(AggregateMethod.SUM, alias, source);
	}

	/** Expression-source variant of {@link #countDistinct(String, EStructuralFeature...)} (issue #87). */
	public QueryBuilder countDistinct(String alias, Expression source) {
		return aggregate(AggregateMethod.COUNT_DISTINCT, alias, source);
	}

	private QueryBuilder aggregate(AggregateMethod method, String alias, EStructuralFeature... segments) {
		Aggregate aggregate = factory.createAggregate();
		aggregate.setMethod(method);
		aggregate.setAlias(Objects.requireNonNull(alias, "aggregate alias must not be null"));
		if (segments.length > 0) {
			aggregate.setPath(Expressions.propertyPath(segments));
		}
		groupByStage().getAggregates().add(aggregate);
		return this;
	}

	private QueryBuilder aggregate(AggregateMethod method, String alias, Expression source) {
		Aggregate aggregate = factory.createAggregate();
		aggregate.setMethod(method);
		aggregate.setAlias(Objects.requireNonNull(alias, "aggregate alias must not be null"));
		aggregate.setSource(Objects.requireNonNull(source, "aggregate source must not be null"));
		groupByStage().getAggregates().add(aggregate);
		return this;
	}

	private GroupByStage groupByStage() {
		if (groupByStage == null) {
			groupByStage = factory.createGroupByStage();
			pipeline().getStages().add(groupByStage);
		}
		return groupByStage;
	}

	private Pipeline pipeline() {
		if (query.getApply() == null) {
			query.setApply(factory.createPipeline());
		}
		return query.getApply();
	}

	/**
	 * Appends an alias-bound computed column to the pipeline (issue #82): after the
	 * grouping it computes over aggregate aliases and group keys
	 * ({@link Expressions#aliasRef(String)}); without a grouping it is terminal — one
	 * row per entity, single-valued attributes plus the computed columns.
	 *
	 * @param alias the column alias
	 * @param expression the computation
	 * @return this builder
	 */
	public QueryBuilder computeAs(String alias, Expression expression) {
		ComputeStage stage;
		List<Stage> stages = pipeline().getStages();
		if (!stages.isEmpty() && stages.get(stages.size() - 1) instanceof ComputeStage last) {
			stage = last;
		} else {
			stage = factory.createComputeStage();
			stages.add(stage);
		}
		Computation computation = factory.createComputation();
		computation.setAlias(Objects.requireNonNull(alias, "compute alias must not be null"));
		computation.setExpression(Objects.requireNonNull(expression, "computation must not be null"));
		stage.getComputations().add(computation);
		return this;
	}

	/**
	 * Appends a post-grouping filter (HAVING, issue #82) — the predicate addresses
	 * pipeline output columns via {@link Expressions#aliasRef(String)} or group-key
	 * paths.
	 *
	 * @param predicate the row predicate
	 * @return this builder
	 */
	public QueryBuilder having(Expression predicate) {
		FilterStage stage = factory.createFilterStage();
		stage.setPredicate(Objects.requireNonNull(predicate, "having predicate must not be null"));
		pipeline().getStages().add(stage);
		return this;
	}

	// ==================== shaping / parameters / persistence ====================

	/**
	 * @param top the result cap, must be positive
	 * @return this builder
	 */
	public QueryBuilder top(int top) {
		if (top <= 0) {
			throw new IllegalArgumentException("top must be positive, was " + top);
		}
		query.setTop(top);
		return this;
	}

	/**
	 * @param skip the result offset, must not be negative
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
	 * @return this builder, with duplicate elimination requested
	 */
	public QueryBuilder distinct() {
		query.setDistinct(true);
		return this;
	}

	/**
	 * @return this builder, requesting a count-only result
	 */
	public QueryBuilder countOnly() {
		query.setCountOnly(true);
		return this;
	}

	/**
	 * Requests the per-hit relevance score alongside the results (issue #165) — delivered on
	 * {@code QueryResult.scores()}, keyed by object id. An envelope flag rather than an
	 * option, so the SCORE capability is validated: a backend not declaring it refuses the
	 * query with a Diagnostic.
	 *
	 * @return this builder, requesting per-hit scores
	 */
	public QueryBuilder withScores() {
		query.setWithScores(true);
		return this;
	}

	/**
	 * Declares a parameter of this (prepared) query.
	 *
	 * @param name the parameter name (referenced via {@link Expressions#param(String)})
	 * @param typeHint the expected value type; may be {@code null}
	 * @return this builder
	 */
	public QueryBuilder parameter(String name, EClassifier typeHint) {
		ParameterDecl declaration = factory.createParameterDecl();
		declaration.setName(Objects.requireNonNull(name, "parameter name must not be null"));
		if (typeHint != null) {
			declaration.setTypeHint(typeHint);
		}
		query.getParameters().add(declaration);
		return this;
	}

	/**
	 * Names the query and marks it to be saved for later reuse.
	 *
	 * @param name the query name
	 * @return this builder
	 */
	public QueryBuilder named(String name) {
		query.setName(Objects.requireNonNull(name, "query name must not be null"));
		query.setSaveQuery(true);
		return this;
	}

	/**
	 * @return the built query
	 */
	public Query build() {
		return query;
	}
}
