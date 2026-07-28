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
package org.eclipse.fennec.persistence.query.expr;

import java.util.EnumSet;
import java.util.Set;

import org.eclipse.fennec.model.expression.And;
import org.eclipse.fennec.model.expression.Between;
import org.eclipse.fennec.model.expression.Comparison;
import org.eclipse.fennec.model.expression.Exists;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.In;
import org.eclipse.fennec.model.expression.IsNull;
import org.eclipse.fennec.model.expression.Junction;
import org.eclipse.fennec.model.expression.Not;
import org.eclipse.fennec.model.expression.ParameterRef;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.model.expression.Quantifier;
import org.eclipse.fennec.model.expression.StringFunction;
import org.eclipse.fennec.model.expression.StringMatch;
import org.eclipse.fennec.model.query.Aggregate;
import org.eclipse.fennec.model.query.GroupByStage;
import org.eclipse.fennec.model.query.FilterStage;
import org.eclipse.fennec.model.query.OrderBy;
import org.eclipse.fennec.model.query.Pipeline;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.Selection;
import org.eclipse.fennec.model.query.Stage;
import org.eclipse.fennec.persistence.query.api.QueryFeature;
import org.eclipse.fennec.persistence.query.api.QueryShape;
import org.eclipse.fennec.persistence.query.support.QueryAnalysis;

/**
 * Analyzes a v2 {@link Query} envelope (expression IR) and reports the used
 * {@link QueryFeature}s, the maximum navigation depth and the result {@link QueryShape}.
 * Pure function — no backend access. The result feeds the unchanged
 * {@code QueryValidator}/{@code QueryCapabilities} mechanism.
 *
 * @author Mark Hoffmann
 * @since 24.07.2026
 */
public final class ExpressionAnalyzer {

	private ExpressionAnalyzer() {
	}

	/**
	 * Analyzes the given query envelope.
	 *
	 * @param query the query to analyze, must not be {@code null}
	 * @return the analysis result
	 */
	public static QueryAnalysis analyze(Query query) {
		if (query == null) {
			throw new IllegalArgumentException("query must not be null");
		}
		Set<QueryFeature> features = EnumSet.noneOf(QueryFeature.class);
		int[] maxDepth = { 0 };

		if (query.getFrom() != null) {
			features.add(QueryFeature.TYPE_FILTER);
		}
		if (query.getPredicate() != null) {
			walk(query.getPredicate(), features, maxDepth);
		}
		for (OrderBy orderBy : query.getOrderBy()) {
			features.add(QueryFeature.SORT);
			path(orderBy.getPath(), features, maxDepth);
		}
		for (Selection selection : query.getSelect()) {
			features.add(QueryFeature.PROJECTION);
			int depth = depthOf(selection.getPath());
			track(depth, features, maxDepth);
			if (depth > 1) {
				features.add(QueryFeature.PROJECTION_NESTED);
			}
		}
		boolean aggregating = analyzePipeline(query.getApply(), features, maxDepth);
		if (!query.getExpand().isEmpty()) {
			features.add(QueryFeature.EXPAND);
			query.getExpand().forEach(expand -> path(expand, features, maxDepth));
		}
		if (query.getTop() > 0) {
			features.add(QueryFeature.LIMIT);
		}
		if (query.getSkip() > 0) {
			features.add(QueryFeature.SKIP);
		}
		if (query.isDistinct()) {
			features.add(QueryFeature.DISTINCT);
		}
		if (query.isCountOnly()) {
			features.add(QueryFeature.COUNT);
		}
		if (!query.getParameters().isEmpty()) {
			features.add(QueryFeature.PARAMETERS);
		}

		QueryShape shape = deriveShape(query, aggregating);
		return new QueryAnalysis(features, maxDepth[0], shape);
	}

	private static QueryShape deriveShape(Query query, boolean aggregating) {
		if (query.isCountOnly()) {
			return QueryShape.COUNT;
		}
		if (aggregating) {
			return QueryShape.AGGREGATION;
		}
		if (!query.getSelect().isEmpty()) {
			return QueryShape.PROJECTION;
		}
		return QueryShape.OBJECTS;
	}

	private static boolean analyzePipeline(Pipeline pipeline, Set<QueryFeature> features, int[] maxDepth) {
		if (pipeline == null) {
			return false;
		}
		boolean aggregating = false;
		boolean beyondSingleGroupBy = pipeline.getStages().size() > 1;
		for (Stage stage : pipeline.getStages()) {
			if (stage instanceof GroupByStage groupBy) {
				aggregating = true;
				features.add(QueryFeature.GROUP_BY);
				groupBy.getPaths().forEach(p -> path(p, features, maxDepth));
				for (Aggregate aggregate : groupBy.getAggregates()) {
					features.add(aggregateFeature(aggregate));
					if (aggregate.getPath() != null) {
						path(aggregate.getPath(), features, maxDepth);
					}
				}
			} else if (stage instanceof FilterStage filter) {
				beyondSingleGroupBy = true;
				walk(filter.getPredicate(), features, maxDepth);
			} else {
				beyondSingleGroupBy = true;
			}
		}
		if (beyondSingleGroupBy) {
			features.add(QueryFeature.PIPELINE);
		}
		return aggregating;
	}

	private static QueryFeature aggregateFeature(Aggregate aggregate) {
		return switch (aggregate.getMethod()) {
		case SUM -> QueryFeature.AGG_SUM;
		case MIN -> QueryFeature.AGG_MIN;
		case MAX -> QueryFeature.AGG_MAX;
		case AVG -> QueryFeature.AGG_AVG;
		case COUNT -> QueryFeature.AGG_COUNT;
		case COUNT_DISTINCT -> QueryFeature.AGG_COUNT_DISTINCT;
		};
	}

	private static void walk(Expression expression, Set<QueryFeature> features, int[] maxDepth) {
		if (expression == null) {
			return;
		}
		if (expression instanceof Junction junction) {
			features.add(junction instanceof And ? QueryFeature.LOGICAL_AND : QueryFeature.LOGICAL_OR);
			junction.getOperands().forEach(operand -> walk(operand, features, maxDepth));
		} else if (expression instanceof Not not) {
			features.add(QueryFeature.LOGICAL_NOT);
			walk(not.getOperand(), features, maxDepth);
		} else if (expression instanceof Comparison comparison) {
			features.add(switch (comparison.getOperator()) {
			case EQ -> QueryFeature.WHERE_EQ;
			case NE -> QueryFeature.WHERE_NE;
			default -> QueryFeature.WHERE_COMPARISON;
			});
			if (navigates(comparison.getLeft()) && navigates(comparison.getRight())) {
				features.add(QueryFeature.FIELD_TO_FIELD);
			}
			walk(comparison.getLeft(), features, maxDepth);
			walk(comparison.getRight(), features, maxDepth);
		} else if (expression instanceof IsNull isNull) {
			features.add(QueryFeature.IS_NULL);
			walk(isNull.getSource(), features, maxDepth);
		} else if (expression instanceof Between between) {
			features.add(QueryFeature.WHERE_RANGE);
			walk(between.getSource(), features, maxDepth);
			walk(between.getLower(), features, maxDepth);
			walk(between.getUpper(), features, maxDepth);
		} else if (expression instanceof In in) {
			features.add(QueryFeature.IN);
			walk(in.getSource(), features, maxDepth);
			in.getValues().forEach(value -> walk(value, features, maxDepth));
		} else if (expression instanceof StringMatch match) {
			features.add(QueryFeature.WHERE_STRING_MATCH);
			if (match.isCaseInsensitive()) {
				features.add(QueryFeature.STRING_MATCH_CASE_INSENSITIVE);
			}
			walk(match.getSource(), features, maxDepth);
			walk(match.getPattern(), features, maxDepth);
		} else if (expression instanceof Quantifier quantifier) {
			features.add(quantifier instanceof Exists ? QueryFeature.EXISTS : QueryFeature.FOR_ALL);
			path(quantifier.getSource(), features, maxDepth);
			walk(quantifier.getPredicate(), features, maxDepth);
		} else if (expression instanceof StringFunction function) {
			features.add(QueryFeature.STRING_FUNCTIONS);
			walk(function.getSource(), features, maxDepth);
		} else if (expression instanceof ParameterRef) {
			features.add(QueryFeature.PARAMETERS);
		} else if (expression instanceof PropertyPath propertyPath) {
			path(propertyPath, features, maxDepth);
		}
		// literals and variable refs carry no features
	}

	/** Whether the operand navigates a feature — directly or through string functions. */
	private static boolean navigates(Expression expression) {
		if (expression instanceof PropertyPath) {
			return true;
		}
		if (expression instanceof StringFunction function) {
			return navigates(function.getSource());
		}
		return false;
	}

	private static void path(PropertyPath path, Set<QueryFeature> features, int[] maxDepth) {
		track(depthOf(path), features, maxDepth);
	}

	private static void track(int depth, Set<QueryFeature> features, int[] maxDepth) {
		maxDepth[0] = Math.max(maxDepth[0], depth);
		if (depth > 1) {
			features.add(QueryFeature.FEATUREPATH_NESTED);
		}
	}

	private static int depthOf(PropertyPath path) {
		return path == null ? 0 : path.getSegments().size();
	}
}
