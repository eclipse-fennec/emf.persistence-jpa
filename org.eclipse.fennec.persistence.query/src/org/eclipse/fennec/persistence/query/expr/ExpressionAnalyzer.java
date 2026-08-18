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
import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.model.expression.AliasRef;
import org.eclipse.fennec.model.expression.And;
import org.eclipse.fennec.model.expression.Arithmetic;
import org.eclipse.fennec.model.expression.ArithmeticOperator;
import org.eclipse.fennec.model.expression.Between;
import org.eclipse.fennec.model.expression.CollectionCount;
import org.eclipse.fennec.model.expression.Comparison;
import org.eclipse.fennec.model.expression.Concat;
import org.eclipse.fennec.model.expression.Exists;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.GeoDistance;
import org.eclipse.fennec.model.expression.GeoPointLiteral;
import org.eclipse.fennec.model.expression.GeoPolygon;
import org.eclipse.fennec.model.expression.GeoSubject;
import org.eclipse.fennec.model.expression.GeoWithin;
import org.eclipse.fennec.model.expression.In;
import org.eclipse.fennec.model.expression.IndexOf;
import org.eclipse.fennec.model.expression.IntegerLiteral;
import org.eclipse.fennec.model.expression.IsNull;
import org.eclipse.fennec.model.expression.Junction;
import org.eclipse.fennec.model.expression.Negate;
import org.eclipse.fennec.model.expression.Not;
import org.eclipse.fennec.model.expression.NumericFunction;
import org.eclipse.fennec.model.expression.ParameterRef;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.model.expression.Quantifier;
import org.eclipse.fennec.model.expression.RealLiteral;
import org.eclipse.fennec.model.expression.Score;
import org.eclipse.fennec.model.expression.StringFunction;
import org.eclipse.fennec.model.expression.StringMatch;
import org.eclipse.fennec.model.expression.StringMatchKind;
import org.eclipse.fennec.model.expression.Substring;
import org.eclipse.fennec.model.expression.TemporalFunction;
import org.eclipse.fennec.model.expression.TypeCheck;
import org.eclipse.fennec.model.query.Aggregate;
import org.eclipse.fennec.model.query.AggregateMethod;
import org.eclipse.fennec.model.query.Computation;
import org.eclipse.fennec.model.query.ComputeStage;
import org.eclipse.fennec.model.query.FilterStage;
import org.eclipse.fennec.model.query.GroupByStage;
import org.eclipse.fennec.model.query.GroupKey;
import org.eclipse.fennec.model.query.OrderBy;
import org.eclipse.fennec.model.query.Pipeline;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.Selection;
import org.eclipse.fennec.model.query.Stage;
import org.eclipse.fennec.persistence.capabilities.QueryFeature;
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
		boolean[] zeroDivision = { false };
		String[] invalidAggregate = { null };

		if (query.getFrom() != null) {
			features.add(QueryFeature.TYPE_FILTER);
		}
		if (query.getPredicate() != null) {
			walk(query.getPredicate(), features, maxDepth, zeroDivision);
		}
		String[] invalidSort = { null };
		for (OrderBy orderBy : query.getOrderBy()) {
			features.add(QueryFeature.SORT);
			if (orderBy.getKey() instanceof AliasRef aliasRef) {
				// a bare AliasRef key is a plain output-column sort (issue #102): no
				// rendering beyond addressing the column — plain SORT on every backend
				if (query.getSelect().isEmpty() && query.getApply() == null) {
					invalidSort[0] = "Sort key '" + aliasRef.getAlias()
							+ "' addresses an output column, but the query is not row-shaped"
							+ " (no projection or aggregation)";
				}
			} else if (orderBy.getKey() instanceof Score) {
				// a bare Score key is the canonical use of the SCORE capability (issue
				// #165): relevance order asks for SCORE, not for arbitrary expression
				// sorting — only a composed key (score().times(...)) goes the
				// SORT_EXPRESSION route below
				features.add(QueryFeature.SCORE);
			} else if (orderBy.getKey() != null) {
				// ordering by an arbitrary value expression (issue #84)
				features.add(QueryFeature.SORT_EXPRESSION);
				walk(orderBy.getKey(), features, maxDepth, zeroDivision);
			} else {
				path(orderBy.getPath(), features, maxDepth);
			}
		}
		for (Selection selection : query.getSelect()) {
			features.add(QueryFeature.PROJECTION);
			int depth = depthOf(selection.getPath());
			track(depth, features, maxDepth);
			if (depth > 1) {
				features.add(QueryFeature.PROJECTION_NESTED);
			}
		}
		boolean aggregating = analyzePipeline(query.getApply(), features, maxDepth, zeroDivision,
				invalidAggregate);
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
		if (query.isWithScores()) {
			// the envelope flag requests per-hit relevance (issue #165) — an option would
			// bypass this validation, which is why it is IR
			features.add(QueryFeature.SCORE);
		}
		if (query.isCountOnly()) {
			features.add(QueryFeature.COUNT);
		}
		if (!query.getParameters().isEmpty()) {
			features.add(QueryFeature.PARAMETERS);
		}

		QueryShape shape = deriveShape(query, aggregating);
		String invalidGeo = features.contains(QueryFeature.GEO_WITHIN)
				|| features.contains(QueryFeature.GEO_DISTANCE) ? scanGeoStructure(query) : null;
		String invalidStringMatch = features.contains(QueryFeature.WHERE_STRING_MATCH)
				? scanStringMatches(query) : null;
		return new QueryAnalysis(features, maxDepth[0], shape, zeroDivision[0], invalidAggregate[0],
				invalidSort[0], invalidGeo, invalidStringMatch);
	}

	/**
	 * Structural findings for string matches (issue #167): the fuzzy parameters are only
	 * meaningful for {@code kind = FUZZY} — set on any other kind they are refused by shape,
	 * never silently ignored — and the edit budget is 1 or 2.
	 */
	private static String scanStringMatches(Query query) {
		Iterator<EObject> contents = query.eAllContents();
		while (contents.hasNext()) {
			if (contents.next() instanceof StringMatch match) {
				boolean fuzzy = match.getKind() == StringMatchKind.FUZZY;
				if (!fuzzy && (match.isSetMaxEdits() || match.isSetPrefixLength())) {
					return "maxEdits/prefixLength are only meaningful for StringMatch kind FUZZY, not "
							+ match.getKind();
				}
				if (fuzzy && (match.getMaxEdits() < 1 || match.getMaxEdits() > 2)) {
					return "StringMatch maxEdits must be 1 or 2, was " + match.getMaxEdits();
				}
				if (fuzzy && match.getPrefixLength() < 0) {
					return "StringMatch prefixLength must not be negative, was " + match.getPrefixLength();
				}
			}
		}
		return null;
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

	private static boolean analyzePipeline(Pipeline pipeline, Set<QueryFeature> features, int[] maxDepth,
			boolean[] zeroDivision, String[] invalidAggregate) {
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
				for (GroupKey key : groupBy.getKeys()) {
					// expression-valued group keys (issue #87)
					features.add(QueryFeature.GROUP_EXPRESSION);
					walk(key.getExpression(), features, maxDepth, zeroDivision);
				}
				for (Aggregate aggregate : groupBy.getAggregates()) {
					features.add(aggregateFeature(aggregate));
					if (aggregate.getPath() != null && aggregate.getSource() != null) {
						invalidAggregate[0] = "Aggregate '" + aggregate.getAlias()
								+ "' sets both path and source — exactly one is allowed";
					} else if (aggregate.getPath() == null && aggregate.getSource() == null
							&& aggregate.getMethod() != AggregateMethod.COUNT) {
						invalidAggregate[0] = "Aggregate '" + aggregate.getAlias() + "' ("
								+ aggregate.getMethod() + ") needs a path or a source"
								+ " — only COUNT aggregates the bare group members";
					}
					if (aggregate.getPath() != null) {
						path(aggregate.getPath(), features, maxDepth);
					}
					if (aggregate.getSource() != null) {
						// expression-valued aggregate sources (issue #87)
						features.add(QueryFeature.GROUP_EXPRESSION);
						walk(aggregate.getSource(), features, maxDepth, zeroDivision);
					}
				}
			} else if (stage instanceof FilterStage filter) {
				beyondSingleGroupBy = true;
				walk(filter.getPredicate(), features, maxDepth, zeroDivision);
			} else if (stage instanceof ComputeStage compute) {
				// computed columns produce rows like an aggregation (issue #82)
				aggregating = true;
				beyondSingleGroupBy = true;
				features.add(QueryFeature.PIPELINE_COMPUTE);
				for (Computation computation : compute.getComputations()) {
					walk(computation.getExpression(), features, maxDepth, zeroDivision);
				}
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

	private static void walk(Expression expression, Set<QueryFeature> features, int[] maxDepth,
			boolean[] zeroDivision) {
		if (expression == null) {
			return;
		}
		if (expression instanceof Junction junction) {
			features.add(junction instanceof And ? QueryFeature.LOGICAL_AND : QueryFeature.LOGICAL_OR);
			junction.getOperands().forEach(operand -> walk(operand, features, maxDepth, zeroDivision));
		} else if (expression instanceof Not not) {
			features.add(QueryFeature.LOGICAL_NOT);
			walk(not.getOperand(), features, maxDepth, zeroDivision);
		} else if (expression instanceof Comparison comparison) {
			features.add(switch (comparison.getOperator()) {
			case EQ -> QueryFeature.WHERE_EQ;
			case NE -> QueryFeature.WHERE_NE;
			default -> QueryFeature.WHERE_COMPARISON;
			});
			if (navigates(comparison.getLeft()) && navigates(comparison.getRight())) {
				features.add(QueryFeature.FIELD_TO_FIELD);
			}
			walk(comparison.getLeft(), features, maxDepth, zeroDivision);
			walk(comparison.getRight(), features, maxDepth, zeroDivision);
		} else if (expression instanceof IsNull isNull) {
			features.add(QueryFeature.IS_NULL);
			walk(isNull.getSource(), features, maxDepth, zeroDivision);
		} else if (expression instanceof Between between) {
			features.add(QueryFeature.WHERE_RANGE);
			walk(between.getSource(), features, maxDepth, zeroDivision);
			walk(between.getLower(), features, maxDepth, zeroDivision);
			walk(between.getUpper(), features, maxDepth, zeroDivision);
		} else if (expression instanceof In in) {
			features.add(QueryFeature.IN);
			walk(in.getSource(), features, maxDepth, zeroDivision);
			in.getValues().forEach(value -> walk(value, features, maxDepth, zeroDivision));
		} else if (expression instanceof StringMatch match) {
			features.add(QueryFeature.WHERE_STRING_MATCH);
			if (match.isCaseInsensitive()) {
				features.add(QueryFeature.STRING_MATCH_CASE_INSENSITIVE);
			}
			if (match.getKind() == StringMatchKind.FUZZY) {
				// edit-distance matching is a refinement like case-insensitivity (issue #167)
				features.add(QueryFeature.STRING_MATCH_FUZZY);
			}
			walk(match.getSource(), features, maxDepth, zeroDivision);
			walk(match.getPattern(), features, maxDepth, zeroDivision);
		} else if (expression instanceof Quantifier quantifier) {
			features.add(quantifier instanceof Exists ? QueryFeature.EXISTS : QueryFeature.FOR_ALL);
			path(quantifier.getSource(), features, maxDepth);
			walk(quantifier.getPredicate(), features, maxDepth, zeroDivision);
		} else if (expression instanceof StringFunction function) {
			features.add(QueryFeature.STRING_FUNCTIONS);
			walk(function.getSource(), features, maxDepth, zeroDivision);
		} else if (expression instanceof Concat concatenation) {
			features.add(QueryFeature.STRING_FUNCTIONS_EXTENDED);
			concatenation.getParts().forEach(part -> walk(part, features, maxDepth, zeroDivision));
		} else if (expression instanceof IndexOf indexOf) {
			features.add(QueryFeature.STRING_FUNCTIONS_EXTENDED);
			walk(indexOf.getSource(), features, maxDepth, zeroDivision);
			walk(indexOf.getSearch(), features, maxDepth, zeroDivision);
		} else if (expression instanceof Substring substring) {
			features.add(QueryFeature.STRING_FUNCTIONS_EXTENDED);
			walk(substring.getSource(), features, maxDepth, zeroDivision);
			walk(substring.getStart(), features, maxDepth, zeroDivision);
			walk(substring.getLength(), features, maxDepth, zeroDivision);
		} else if (expression instanceof Arithmetic arithmetic) {
			features.add(QueryFeature.ARITHMETIC);
			if ((arithmetic.getOperator() == ArithmeticOperator.DIV
					|| arithmetic.getOperator() == ArithmeticOperator.MOD)
					&& isLiteralZero(arithmetic.getRight())) {
				zeroDivision[0] = true;
			}
			walk(arithmetic.getLeft(), features, maxDepth, zeroDivision);
			walk(arithmetic.getRight(), features, maxDepth, zeroDivision);
		} else if (expression instanceof Negate negate) {
			features.add(QueryFeature.ARITHMETIC);
			walk(negate.getOperand(), features, maxDepth, zeroDivision);
		} else if (expression instanceof NumericFunction numericFunction) {
			features.add(QueryFeature.NUMERIC_FUNCTIONS);
			walk(numericFunction.getSource(), features, maxDepth, zeroDivision);
		} else if (expression instanceof TemporalFunction temporalFunction) {
			features.add(QueryFeature.TEMPORAL_FUNCTIONS);
			walk(temporalFunction.getSource(), features, maxDepth, zeroDivision);
		} else if (expression instanceof CollectionCount count) {
			features.add(count.getPredicate() == null ? QueryFeature.COLLECTION_COUNT
					: QueryFeature.COLLECTION_COUNT_FILTERED);
			path(count.getSource(), features, maxDepth);
			walk(count.getPredicate(), features, maxDepth, zeroDivision);
		} else if (expression instanceof TypeCheck typeCheck) {
			features.add(QueryFeature.TYPE_CHECK);
			if (typeCheck.getSource() != null) {
				walk(typeCheck.getSource(), features, maxDepth, zeroDivision);
			}
		} else if (expression instanceof ParameterRef) {
			features.add(QueryFeature.PARAMETERS);
		} else if (expression instanceof Score) {
			features.add(QueryFeature.SCORE);
		} else if (expression instanceof GeoWithin geoWithin) {
			features.add(QueryFeature.GEO_WITHIN);
			subjectPaths(geoWithin.getSubject(), features, maxDepth);
		} else if (expression instanceof GeoDistance geoDistance) {
			features.add(QueryFeature.GEO_DISTANCE);
			subjectPaths(geoDistance.getSubject(), features, maxDepth);
		} else if (expression instanceof PropertyPath propertyPath) {
			if (propertyPath.getCastBase() != null) {
				features.add(QueryFeature.TYPE_CAST);
			}
			path(propertyPath, features, maxDepth);
		}
		// literals and variable refs carry no features
	}

	/** Registers the subject's coordinate paths (either binding form — issue #101). */
	private static void subjectPaths(GeoSubject subject, Set<QueryFeature> features, int[] maxDepth) {
		if (subject == null) {
			return;
		}
		if (subject.getPathLat() != null) {
			path(subject.getPathLat(), features, maxDepth);
		}
		if (subject.getPathLon() != null) {
			path(subject.getPathLon(), features, maxDepth);
		}
		if (subject.getPathPoint() != null) {
			path(subject.getPathPoint(), features, maxDepth);
		}
	}

	/**
	 * Structural geo validation (issue #101, §5 rules): every subject carries exactly
	 * one binding, coordinates are in range, polygons have three distinct vertices and
	 * do not cross the antimeridian. First finding wins.
	 */
	private static String scanGeoStructure(Query query) {
		var iterator = query.eAllContents();
		while (iterator.hasNext()) {
			Object content = iterator.next();
			if (content instanceof GeoSubject subject) {
				boolean split = subject.getPathLat() != null && subject.getPathLon() != null;
				boolean packed = subject.getPathPoint() != null;
				if (split == packed || (subject.getPathLat() == null) != (subject.getPathLon() == null)) {
					return "GeoSubject must bind either the lat/lon feature pair or a single point path";
				}
			} else if (content instanceof GeoPointLiteral point) {
				if (Math.abs(point.getLat()) > 90.0 || Math.abs(point.getLon()) > 180.0) {
					return "Geo coordinate out of range: lon=" + point.getLon() + ", lat=" + point.getLat()
							+ " (|lat| <= 90, |lon| <= 180)";
				}
			} else if (content instanceof GeoPolygon polygon) {
				long distinct = polygon.getPoints().stream()
						.map(p -> p.getLon() + "/" + p.getLat())
						.distinct().count();
				if (distinct < 3) {
					return "GeoPolygon needs at least three distinct vertices, got " + distinct;
				}
				for (int i = 1; i < polygon.getPoints().size(); i++) {
					if (Math.abs(polygon.getPoints().get(i).getLon()
							- polygon.getPoints().get(i - 1).getLon()) > 180.0) {
						return "GeoPolygon must not cross the antimeridian (§5.3) — split it";
					}
				}
			}
		}
		return null;
	}

	/** Whether the divisor is a literal zero (statically refusable, issue #76). */
	private static boolean isLiteralZero(Expression divisor) {
		if (divisor instanceof IntegerLiteral literal) {
			return literal.getValue() == 0L;
		}
		if (divisor instanceof RealLiteral literal) {
			return literal.getValue() == 0.0d;
		}
		return false;
	}

	/** Whether the operand navigates a feature — directly or through string functions. */
	private static boolean navigates(Expression expression) {
		if (expression instanceof PropertyPath) {
			return true;
		}
		if (expression instanceof StringFunction function) {
			return navigates(function.getSource());
		}
		if (expression instanceof Substring substring) {
			return navigates(substring.getSource());
		}
		if (expression instanceof Concat concatenation) {
			return concatenation.getParts().stream().anyMatch(ExpressionAnalyzer::navigates);
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
