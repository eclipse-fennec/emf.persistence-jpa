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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
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
import org.eclipse.fennec.model.expression.IntervalMatch;
import org.eclipse.fennec.model.expression.IntervalSubject;
import org.eclipse.fennec.model.expression.IsNull;
import org.eclipse.fennec.model.expression.Junction;
import org.eclipse.fennec.model.expression.Literal;
import org.eclipse.fennec.model.expression.MapValue;
import org.eclipse.fennec.model.expression.Negate;
import org.eclipse.fennec.model.expression.Not;
import org.eclipse.fennec.model.expression.NumericFunction;
import org.eclipse.fennec.model.expression.ParameterRef;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.model.expression.RootReference;
import org.eclipse.fennec.model.expression.Quantifier;
import org.eclipse.fennec.model.expression.RealLiteral;
import org.eclipse.fennec.model.expression.Score;
import org.eclipse.fennec.model.expression.StringFunction;
import org.eclipse.fennec.model.expression.StringMatch;
import org.eclipse.fennec.model.expression.StringMatchKind;
import org.eclipse.fennec.model.expression.Substring;
import org.eclipse.fennec.model.expression.TemporalFunction;
import org.eclipse.fennec.model.expression.TemporalLiteral;
import org.eclipse.fennec.model.expression.TypeCheck;
import org.eclipse.fennec.model.query.Aggregate;
import org.eclipse.fennec.model.query.AggregateMethod;
import org.eclipse.fennec.model.query.Computation;
import org.eclipse.fennec.model.query.ComputeStage;
import org.eclipse.fennec.model.query.Expand;
import org.eclipse.fennec.model.query.FilterStage;
import org.eclipse.fennec.model.query.GroupByStage;
import org.eclipse.fennec.model.query.GroupKey;
import org.eclipse.fennec.model.query.OrderBy;
import org.eclipse.fennec.model.query.Pipeline;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.RepresentativeSpec;
import org.eclipse.fennec.model.query.Selection;
import org.eclipse.fennec.model.query.Stage;
import org.eclipse.fennec.persistence.capabilities.QueryFeature;
import org.eclipse.fennec.persistence.helper.EMaps;
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
		String[] invalidProjection = { null };
		for (Selection selection : query.getSelect()) {
			features.add(QueryFeature.PROJECTION);
			boolean hasPath = selection.getPath() != null;
			boolean hasKey = selection.getKey() != null;
			if (hasPath == hasKey) {
				// exactly one of the two, like OrderBy (issue #189)
				invalidProjection[0] = hasPath
						? "Projection '" + selection.getAlias() + "' sets both path and key"
						: "A projection sets neither path nor key";
			} else if (hasKey) {
				// projecting an arbitrary value expression (issue #189)
				features.add(QueryFeature.PROJECTION_EXPRESSION);
				if (selection.getAlias() == null || selection.getAlias().isBlank()) {
					// an expression has no derivable column name — the alias is the name
					invalidProjection[0] = "An expression projection needs an alias";
				}
				walk(selection.getKey(), features, maxDepth, zeroDivision);
			} else {
				int depth = depthOf(selection.getPath());
				track(depth, features, maxDepth);
				if (depth > 1) {
					features.add(QueryFeature.PROJECTION_NESTED);
				}
			}
		}
		String[] invalidRepresentatives = { null };
		boolean aggregating = analyzePipeline(query.getApply(), features, maxDepth, zeroDivision,
				invalidAggregate, invalidRepresentatives);
		if (!query.getExpand().isEmpty()) {
			features.add(QueryFeature.EXPAND);
			query.getExpand().forEach(expand -> expansion(expand, features, maxDepth, zeroDivision));
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
		String invalidMapValue = features.contains(QueryFeature.MAP_VALUE) ? scanMapValues(query) : null;
		String invalidInterval = features.contains(QueryFeature.INTERVAL_MATCH)
				? scanIntervalStructure(query) : null;
		return new QueryAnalysis(features, maxDepth[0], shape, zeroDivision[0], invalidAggregate[0],
				invalidSort[0], invalidGeo, invalidStringMatch, invalidMapValue, invalidProjection[0],
				invalidInterval, invalidRepresentatives[0], scanPipelineShape(query.getApply()),
				scanExpandShape(query.getExpand()));
	}

	/**
	 * Structural findings for map access (issue #186). Two rules, both contract rather than
	 * capability — no backend declares its way out of either:
	 * <ul>
	 * <li>the path must end in a map, otherwise there is no entry to address;</li>
	 * <li>the key must be constant — a {@code Literal} or a {@code ParameterRef}. Mongo and
	 * Lucene turn a map key into a field name, so it has to be knowable when the query is
	 * translated; a computed key would be a construct only the relational backend could
	 * serve.</li>
	 * </ul>
	 */
	private static String scanMapValues(Query query) {
		Iterator<EObject> contents = query.eAllContents();
		while (contents.hasNext()) {
			if (contents.next() instanceof MapValue mapValue) {
				String finding = checkMapValue(mapValue);
				if (finding != null) {
					return finding;
				}
			}
		}
		return null;
	}

	private static String checkMapValue(MapValue mapValue) {
		PropertyPath map = mapValue.getMap();
		if (map == null || map.getSegments().isEmpty()) {
			return "MapValue carries no map path";
		}
		EStructuralFeature last = map.getSegments().get(map.getSegments().size() - 1);
		if (!EMaps.isMap(last)) {
			return "MapValue addresses '" + last.getName() + "', which is not a map"
					+ " (a map is a containment-many reference to a Map.Entry class)";
		}
		Expression key = mapValue.getKey();
		if (!(key instanceof Literal) && !(key instanceof ParameterRef)) {
			return "MapValue key must be a literal or a parameter, was "
					+ (key == null ? "absent" : key.eClass().getName())
					+ " — a map key becomes a field name on document and search backends,"
					+ " so it has to be known when the query is translated";
		}
		return null;
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

	/**
	 * Pipeline shapes that are expressible but that no backend serves (issue #239).
	 * <p>
	 * A second {@code GroupByStage} is the case that forced this: the model is a list of stages,
	 * so two groupings in sequence parse and validate, and the three backends then disagree —
	 * JPA throws while translating, the memory engine silently drops the second grouping (its
	 * row-stage loop handles filter/compute/paging and no grouping at all), and mongo appends a
	 * second {@code $group} whose output columns nothing has registered. Two of those three
	 * answer <em>plausibly and wrongly</em>, which §5 forbids more strongly than a refusal.
	 * <p>
	 * Reported as unsupported rather than invalid, and without a capability literal: the query
	 * is well-formed, and per the #207 rule a literal appears when an implementation declares
	 * it. When a backend learns to serve this — mongo's {@code $group} twice, JPA's grouping
	 * subquery — the literal arrives with that work and this check becomes conditional.
	 *
	 * @param pipeline the pipeline to inspect; may be {@code null}
	 * @return the finding, or {@code null} when the shape is servable
	 */
	private static String scanPipelineShape(Pipeline pipeline) {
		if (pipeline == null) {
			return null;
		}
		long groupings = pipeline.getStages().stream().filter(GroupByStage.class::isInstance).count();
		if (groupings > 1) {
			return "The pipeline groups " + groupings + " times; no backend serves more than one"
					+ " GroupBy stage — the second grouping would aggregate the first one's output";
		}
		return null;
	}

	private static boolean analyzePipeline(Pipeline pipeline, Set<QueryFeature> features, int[] maxDepth,
			boolean[] zeroDivision, String[] invalidAggregate, String[] invalidRepresentatives) {
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
				RepresentativeSpec representatives = groupBy.getRepresentatives();
				if (representatives != null) {
					// the group's own documents next to its aggregates (issue #214)
					features.add(QueryFeature.GROUP_REPRESENTATIVES);
					walk(representatives.getCount(), features, maxDepth, zeroDivision);
					if (representatives.getOffset() != null) {
						walk(representatives.getOffset(), features, maxDepth, zeroDivision);
					}
					for (OrderBy within : representatives.getOrderBy()) {
						if (within.getPath() != null) {
							path(within.getPath(), features, maxDepth);
						}
						if (within.getKey() != null) {
							features.add(QueryFeature.SORT_EXPRESSION);
							walk(within.getKey(), features, maxDepth, zeroDivision);
						}
					}
					if (invalidRepresentatives[0] == null) {
						invalidRepresentatives[0] = scanRepresentatives(representatives);
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
		} else if (expression instanceof MapValue mapValue) {
			// addressing one entry of a map by key (issue #186) — the map navigation counts
			// as an ordinary path, the key is walked so a ParameterRef in it is declared
			features.add(QueryFeature.MAP_VALUE);
			path(mapValue.getMap(), features, maxDepth);
			walk(mapValue.getKey(), features, maxDepth, zeroDivision);
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
		} else if (expression instanceof IntervalMatch intervalMatch) {
			features.add(QueryFeature.INTERVAL_MATCH);
			subjectPaths(intervalMatch.getSubject(), features, maxDepth);
			walk(intervalMatch.getLower(), features, maxDepth, zeroDivision);
			walk(intervalMatch.getUpper(), features, maxDepth, zeroDivision);
		} else if (expression instanceof RootReference rootReference) {
			features.add(QueryFeature.ROOT_REFERENCE);
			// the key is walked for its features but NOT for paths: those address the
			// referenced type, not the query's own root, so they must not widen the
			// path-depth budget or the root's feature set with a foreign traversal
			walk(rootReference.getKey(), features, maxDepth, zeroDivision);
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

	/** Registers the subject's two bound paths (issue #215). */
	private static void subjectPaths(IntervalSubject subject, Set<QueryFeature> features, int[] maxDepth) {
		if (subject == null) {
			return;
		}
		if (subject.getPathLower() != null) {
			path(subject.getPathLower(), features, maxDepth);
		}
		if (subject.getPathUpper() != null) {
			path(subject.getPathUpper(), features, maxDepth);
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

	/**
	 * Structural interval validation (issue #215): a subject binds both bound paths, both
	 * end in an attribute of the same domain, and a query interval given by two literals is
	 * not inverted. First finding wins.
	 * <p>
	 * The inversion check is deliberately static-only. An inverted subject <em>row</em> is
	 * data, not shape — it is the empty interval and matches no relation (concept §A.5.3),
	 * which the evaluators enforce, not the analyzer.
	 */
	private static String scanIntervalStructure(Query query) {
		var iterator = query.eAllContents();
		while (iterator.hasNext()) {
			Object content = iterator.next();
			if (content instanceof IntervalSubject subject) {
				String finding = scanIntervalSubject(subject);
				if (finding != null) {
					return finding;
				}
			} else if (content instanceof IntervalMatch match
					&& isInverted(match.getLower(), match.getUpper())) {
				return "IntervalMatch query interval is inverted — its lower bound is greater "
						+ "than its upper bound, so it can never match";
			}
		}
		return null;
	}

	/** The three subject rules of the interval scan. */
	private static String scanIntervalSubject(IntervalSubject subject) {
		if (subject.getPathLower() == null || subject.getPathUpper() == null) {
			return "IntervalSubject must bind both bound paths (pathLower and pathUpper)";
		}
		EStructuralFeature lower = lastSegment(subject.getPathLower());
		EStructuralFeature upper = lastSegment(subject.getPathUpper());
		if (!(lower instanceof EAttribute lowerAttribute) || !(upper instanceof EAttribute upperAttribute)) {
			return "IntervalSubject bound paths must end in an attribute — a reference is not an "
					+ "ordered bound";
		}
		String lowerDomain = domainOf(lowerAttribute);
		String upperDomain = domainOf(upperAttribute);
		if (lowerDomain != null && upperDomain != null && !lowerDomain.equals(upperDomain)) {
			return "IntervalSubject bounds are of different domains: " + lowerAttribute.getName() + " is "
					+ lowerDomain + ", " + upperAttribute.getName() + " is " + upperDomain;
		}
		return null;
	}

	private static EStructuralFeature lastSegment(PropertyPath path) {
		var segments = path.getSegments();
		return segments.isEmpty() ? null : segments.get(segments.size() - 1);
	}

	/**
	 * The comparable domain of a bound attribute — {@code null} when the instance class is
	 * unknown (dynamic EDataTypes), in which case the pair is not judged rather than
	 * refused on a guess.
	 */
	private static String domainOf(EAttribute attribute) {
		Class<?> instanceClass = attribute.getEAttributeType() == null
				? null
				: attribute.getEAttributeType().getInstanceClass();
		if (instanceClass == null) {
			return null;
		}
		if (Number.class.isAssignableFrom(instanceClass) || instanceClass.isPrimitive()) {
			return instanceClass == boolean.class || instanceClass == char.class ? null : "numeric";
		}
		if (Temporal.class.isAssignableFrom(instanceClass) || Date.class.isAssignableFrom(instanceClass)) {
			return "temporal";
		}
		if (CharSequence.class.isAssignableFrom(instanceClass)) {
			return "textual";
		}
		return null;
	}

	/** Whether two literal bounds are the wrong way round (numeric or same-kind temporal). */
	private static boolean isInverted(Expression lower, Expression upper) {
		Double lowerNumber = numberOf(lower);
		Double upperNumber = numberOf(upper);
		if (lowerNumber != null && upperNumber != null) {
			return lowerNumber > upperNumber;
		}
		if (lower instanceof TemporalLiteral lowerTemporal && upper instanceof TemporalLiteral upperTemporal
				&& lowerTemporal.getKind() == upperTemporal.getKind()) {
			return isInvertedTemporal(lowerTemporal, upperTemporal);
		}
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static boolean isInvertedTemporal(TemporalLiteral lower, TemporalLiteral upper) {
		Object lowerValue = temporalOf(lower);
		Object upperValue = temporalOf(upper);
		if (lowerValue == null || upperValue == null || !lowerValue.getClass().equals(upperValue.getClass())) {
			return false;
		}
		return ((Comparable) lowerValue).compareTo(upperValue) > 0;
	}

	private static Double numberOf(Expression expression) {
		if (expression instanceof IntegerLiteral integer) {
			return (double) integer.getValue();
		}
		if (expression instanceof RealLiteral real) {
			return real.getValue();
		}
		return null;
	}

	/**
	 * The parsed temporal value, or {@code null} when the text does not parse — a malformed
	 * literal is the translation layer's diagnostic, not this one's.
	 */
	private static Object temporalOf(TemporalLiteral literal) {
		try {
			return switch (literal.getKind()) {
				case DATE -> LocalDate.parse(literal.getValue());
				case TIME -> LocalTime.parse(literal.getValue());
				case DATE_TIME -> LocalDateTime.parse(literal.getValue());
				case INSTANT -> Instant.parse(literal.getValue());
			};
		} catch (RuntimeException e) {
			return null;
		}
	}

	/**
	 * Structural validation of a representative window (issue #214). The window bounds have
	 * to be constants — a literal or a bound parameter — because a backend that serves this
	 * natively constructs its search with the number known (Lucene's grouping search does),
	 * the same rule {@code MapValue}'s key carries for the same reason. A literal count of
	 * zero or less asks for nothing and is refused rather than silently answered with an
	 * empty cell.
	 */
	private static String scanRepresentatives(RepresentativeSpec representatives) {
		if (representatives.getAlias() == null || representatives.getAlias().isBlank()) {
			return "Representatives need an alias — it names the result cell that holds them";
		}
		String countFinding = constantWindowBound(representatives.getCount(), "count", 1);
		if (countFinding != null) {
			return countFinding;
		}
		return representatives.getOffset() == null ? null
				: constantWindowBound(representatives.getOffset(), "offset", 0);
	}

	private static String constantWindowBound(Expression bound, String name, long minimum) {
		if (bound instanceof ParameterRef) {
			return null;
		}
		if (!(bound instanceof IntegerLiteral literal)) {
			return "Representative " + name + " must be an integer literal or a parameter, not "
					+ (bound == null ? "nothing" : bound.eClass().getName())
					+ " — a backend has to know it when the query is translated";
		}
		return literal.getValue() < minimum
				? "Representative " + name + " is " + literal.getValue() + ", which asks for no rows"
				: null;
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

	/**
	 * Structural findings for expansions (issue #238), recursing into nested ones.
	 * <p>
	 * One rule, and it is contract rather than capability — no backend declares its way out:
	 * an {@code orderBy} is served only together with {@code top} or {@code skip}. Under the
	 * resolution semantics an expansion selects which proxies of a reference get resolved and
	 * never reorders the feature, whose order belongs to the store; ordering is meaningful
	 * only as the selector that decides <em>which</em> children paging picks. Standing alone it
	 * could promise nothing anyone delivers, so it is refused rather than silently ignored —
	 * the line of #239 and #233.
	 *
	 * @param expansions the expansions of the envelope
	 * @return the finding, or {@code null} if every expansion is well-formed
	 */
	private static String scanExpandShape(List<Expand> expansions) {
		for (Expand expand : expansions) {
			if (!expand.getOrderBy().isEmpty() && expand.getTop() <= 0 && expand.getSkip() <= 0) {
				return "Expansion '" + pathName(expand.getPath())
						+ "' orders without top or skip — an expansion selects which children are"
						+ " resolved and never reorders the feature, so orderBy is served only as"
						+ " the selector for paging";
			}
			String nested = scanExpandShape(expand.getExpand());
			if (nested != null) {
				return nested;
			}
		}
		return null;
	}

	/** Dotted rendering of an expansion path, for diagnostics. */
	private static String pathName(PropertyPath path) {
		if (path == null || path.getSegments().isEmpty()) {
			return "<empty>";
		}
		return path.getSegments().stream().map(EStructuralFeature::getName)
				.collect(Collectors.joining("."));
	}

	/**
	 * Flags one expansion and its options (issue #238), recursing into nested ones.
	 * <p>
	 * A bare path is the plain fetch hint and flags nothing beyond {@code EXPAND}. A filter
	 * adds {@code EXPAND_FILTER}; {@code top}/{@code skip} add {@code EXPAND_PAGE}. Its
	 * {@code orderBy} rides on {@code EXPAND_PAGE} too — under the resolution semantics it is
	 * the selector that makes paging meaningful and is never a delivered order, so it is
	 * flagged whether or not paging accompanies it. Whether standing alone is legal at all is
	 * the validator's call, not the analyzer's.
	 * <p>
	 * The filter is walked like any other predicate so the features it uses (and a division by
	 * a literal zero) are flagged as they would be in {@code where}.
	 */
	private static void expansion(Expand expand, Set<QueryFeature> features, int[] maxDepth,
			boolean[] zeroDivision) {
		path(expand.getPath(), features, maxDepth);
		if (expand.getFilter() != null) {
			features.add(QueryFeature.EXPAND_FILTER);
			walk(expand.getFilter(), features, maxDepth, zeroDivision);
		}
		if (expand.getTop() > 0 || expand.getSkip() > 0 || !expand.getOrderBy().isEmpty()) {
			features.add(QueryFeature.EXPAND_PAGE);
		}
		expand.getOrderBy().forEach(orderBy -> {
			if (orderBy.getPath() != null) {
				path(orderBy.getPath(), features, maxDepth);
			}
			if (orderBy.getKey() != null) {
				walk(orderBy.getKey(), features, maxDepth, zeroDivision);
			}
		});
		expand.getExpand().forEach(nested -> expansion(nested, features, maxDepth, zeroDivision));
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
