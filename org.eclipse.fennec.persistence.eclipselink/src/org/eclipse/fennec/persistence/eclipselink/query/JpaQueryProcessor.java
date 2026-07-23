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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.model.expression.And;
import org.eclipse.fennec.model.expression.Between;
import org.eclipse.fennec.model.expression.Comparison;
import org.eclipse.fennec.model.expression.Exists;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.In;
import org.eclipse.fennec.model.expression.IsNull;
import org.eclipse.fennec.model.expression.Junction;
import org.eclipse.fennec.model.expression.Literal;
import org.eclipse.fennec.model.expression.Not;
import org.eclipse.fennec.model.expression.ParameterRef;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.model.expression.Quantifier;
import org.eclipse.fennec.model.expression.StringFunction;
import org.eclipse.fennec.model.expression.StringMatch;
import org.eclipse.fennec.model.expression.Variable;
import org.eclipse.fennec.model.query.Aggregate;
import org.eclipse.fennec.model.query.GroupByStage;
import org.eclipse.fennec.model.query.OrderBy;
import org.eclipse.fennec.model.query.Pipeline;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.Selection;
import org.eclipse.fennec.model.query.SortDirection;
import org.eclipse.fennec.model.query.Stage;
import org.eclipse.fennec.persistence.query.QueryConstants;
import org.eclipse.fennec.persistence.query.QueryException;
import org.eclipse.fennec.persistence.query.api.QueryCapabilities;
import org.eclipse.fennec.persistence.query.api.QueryContext;
import org.eclipse.fennec.persistence.query.api.QueryFeature;
import org.eclipse.fennec.persistence.query.api.QueryPlan;
import org.eclipse.fennec.persistence.query.api.QueryProcessor;
import org.eclipse.fennec.persistence.query.api.QueryShape;
import org.eclipse.fennec.persistence.query.expr.ExpressionAnalyzer;
import org.eclipse.fennec.persistence.query.expr.ExpressionValues;
import org.eclipse.fennec.persistence.query.support.QueryAnalysis;
import org.eclipse.fennec.persistence.query.support.QueryCapabilitiesBuilder;
import org.eclipse.fennec.persistence.query.support.QueryValidator;
import org.osgi.service.component.annotations.Component;

/**
 * {@link QueryProcessor} for JPA/EclipseLink over the expression IR.
 * <p>
 * Translates to <b>JPQL</b> (pure string translation, injection-safe: identifiers are
 * model names, every value is a named parameter). Grouped logic renders parenthesised;
 * {@code Exists}/{@code ForAll} become correlated {@code [NOT] EXISTS} subqueries over
 * the collection path with a unique alias per quantifier; case-insensitive matching
 * wraps both sides in {@code LOWER}; string functions map to
 * {@code LOWER/UPPER/TRIM/LENGTH}.
 * <p>
 * Aggregation: a single {@code GroupByStage} pipeline translates to
 * {@code GROUP BY} + aggregate functions; multi-stage pipelines
 * ({@link QueryFeature#PIPELINE}) are not yet served. Sorting in row shapes addresses
 * the result variables. {@code expand} hints translate to {@code LEFT JOIN FETCH}
 * (single-segment paths).
 *
 * @author Mark Hoffmann
 * @since 24.07.2026
 */
@Component(service = QueryProcessor.class, property = QueryConstants.BACKEND_PROPERTY + "=" + JpaQueryProcessor.BACKEND)
public class JpaQueryProcessor implements QueryProcessor {

	/** The backend id of this processor. */
	public static final String BACKEND = "jpa";

	/** The JPQL root alias. */
	static final String ALIAS = "e";

	private static final QueryCapabilities CAPABILITIES = QueryCapabilitiesBuilder.create()
			.support(QueryFeature.WHERE_EQ, QueryFeature.WHERE_NE, QueryFeature.WHERE_COMPARISON,
					QueryFeature.WHERE_RANGE, QueryFeature.IS_NULL, QueryFeature.IN,
					QueryFeature.WHERE_STRING_MATCH, QueryFeature.STRING_MATCH_CASE_INSENSITIVE,
					QueryFeature.STRING_FUNCTIONS, QueryFeature.LOGICAL_AND, QueryFeature.LOGICAL_OR,
					QueryFeature.LOGICAL_NOT, QueryFeature.EXISTS, QueryFeature.FOR_ALL, QueryFeature.SORT,
					QueryFeature.LIMIT, QueryFeature.SKIP, QueryFeature.DISTINCT, QueryFeature.COUNT,
					QueryFeature.PROJECTION, QueryFeature.PROJECTION_NESTED, QueryFeature.GROUP_BY,
					QueryFeature.AGG_AVG, QueryFeature.AGG_MIN, QueryFeature.AGG_MAX, QueryFeature.AGG_SUM,
					QueryFeature.AGG_COUNT, QueryFeature.AGG_COUNT_DISTINCT, QueryFeature.TYPE_FILTER,
					QueryFeature.TYPE_FILTER_STRICT, QueryFeature.PARAMETERS, QueryFeature.FEATUREPATH_NESTED,
					QueryFeature.EXPAND)
			.maxFeaturePathDepth(-1)
			.build();

	@Override
	public String backend() {
		return BACKEND;
	}

	@Override
	public QueryCapabilities capabilities() {
		return CAPABILITIES;
	}

	@Override
	public Diagnostic validate(Query query, EClass rootEClass) {
		return QueryValidator.validate(ExpressionAnalyzer.analyze(query), rootEClass, CAPABILITIES);
	}

	@Override
	public QueryPlan translate(Query query, QueryContext context) throws QueryException {
		QueryAnalysis analysis = ExpressionAnalyzer.analyze(query);
		QueryShape shape = analysis.shape();
		if (query.getFrom() == null) {
			throw new QueryException("The query carries no root type (from) — cannot derive the entity name");
		}
		if (query.getApply() != null && !query.getSelect().isEmpty()) {
			throw new QueryException("select and apply are mutually exclusive — aggregation defines its own columns");
		}
		Translation translation = new Translation(context);
		String where = query.getPredicate() == null ? "" : translation.render(query.getPredicate());

		StringBuilder jpql = new StringBuilder("SELECT ");
		List<String> rowKeys = new ArrayList<>();
		List<String> rowAliases = new ArrayList<>();
		String entity = query.getFrom().getName();

		switch (shape) {
		case COUNT -> jpql.append("COUNT(").append(ALIAS).append(')');
		case OBJECTS -> {
			if (query.isDistinct()) {
				jpql.append("DISTINCT ");
			}
			jpql.append(ALIAS);
		}
		case PROJECTION -> {
			if (query.isDistinct()) {
				jpql.append("DISTINCT ");
			}
			jpql.append(projectionColumns(query, rowKeys, rowAliases));
		}
		case AGGREGATION -> jpql.append(aggregationColumns(query, rowKeys, rowAliases));
		}
		jpql.append(" FROM ").append(entity).append(' ').append(ALIAS);
		appendFetchJoins(jpql, query);
		if (!where.isEmpty()) {
			jpql.append(" WHERE ").append(where);
		}
		if (shape == QueryShape.AGGREGATION) {
			appendGroupBy(jpql, query);
		}
		if (shape != QueryShape.COUNT) {
			appendOrderBy(jpql, query, shape, rowKeys);
		}
		return new JpaQueryPlan(query, shape, jpql.toString(), translation.parameters,
				Math.max(0, query.getSkip()), Math.max(0, query.getTop()), rowKeys, rowAliases);
	}

	// -------------------------------------------------- select / group / order

	private String projectionColumns(Query query, List<String> rowKeys, List<String> rowAliases)
			throws QueryException {
		StringBuilder columns = new StringBuilder();
		for (Selection selection : query.getSelect()) {
			String key = outputKey(selection.getAlias(), selection.getPath());
			registerKey(key, selection.getAlias(), rowKeys, rowAliases);
			if (columns.length() > 0) {
				columns.append(", ");
			}
			columns.append(rootPath(selection.getPath())).append(" AS ").append(key);
		}
		return columns.toString();
	}

	private String aggregationColumns(Query query, List<String> rowKeys, List<String> rowAliases)
			throws QueryException {
		GroupByStage stage = singleGroupByStage(query.getApply());
		StringBuilder columns = new StringBuilder();
		for (PropertyPath path : stage.getPaths()) {
			String key = outputKey(null, path);
			registerKey(key, null, rowKeys, rowAliases);
			if (columns.length() > 0) {
				columns.append(", ");
			}
			columns.append(rootPath(path)).append(" AS ").append(key);
		}
		for (Aggregate aggregate : stage.getAggregates()) {
			String key = aggregate.getAlias();
			registerKey(key, key, rowKeys, rowAliases);
			if (columns.length() > 0) {
				columns.append(", ");
			}
			String argument = aggregate.getPath() == null ? ALIAS : rootPath(aggregate.getPath());
			String function = switch (aggregate.getMethod()) {
			case SUM -> "SUM(" + argument + ")";
			case MIN -> "MIN(" + argument + ")";
			case MAX -> "MAX(" + argument + ")";
			case AVG -> "AVG(" + argument + ")";
			case COUNT -> "COUNT(" + argument + ")";
			case COUNT_DISTINCT -> "COUNT(DISTINCT " + argument + ")";
			};
			columns.append(function).append(" AS ").append(key);
		}
		return columns.toString();
	}

	private GroupByStage singleGroupByStage(Pipeline pipeline) throws QueryException {
		GroupByStage found = null;
		for (Stage stage : pipeline.getStages()) {
			if (stage instanceof GroupByStage groupBy && found == null) {
				found = groupBy;
			} else {
				throw new QueryException(
						"Multi-stage pipelines are not yet served by the JPA processor (feature PIPELINE)");
			}
		}
		if (found == null) {
			throw new QueryException("The pipeline carries no GroupBy stage");
		}
		return found;
	}

	private void appendGroupBy(StringBuilder jpql, Query query) throws QueryException {
		GroupByStage stage = singleGroupByStage(query.getApply());
		if (stage.getPaths().isEmpty()) {
			return; // whole-set aggregation
		}
		jpql.append(" GROUP BY ");
		for (int i = 0; i < stage.getPaths().size(); i++) {
			if (i > 0) {
				jpql.append(", ");
			}
			jpql.append(rootPath(stage.getPaths().get(i)));
		}
	}

	private void appendFetchJoins(StringBuilder jpql, Query query) throws QueryException {
		for (PropertyPath expand : query.getExpand()) {
			if (expand.getSegments().size() != 1) {
				throw new QueryException("expand supports single-segment reference paths on JPA, got depth "
						+ expand.getSegments().size());
			}
			jpql.append(" LEFT JOIN FETCH ").append(ALIAS).append('.')
					.append(expand.getSegments().get(0).getName());
		}
	}

	private void appendOrderBy(StringBuilder jpql, Query query, QueryShape shape, List<String> rowKeys)
			throws QueryException {
		if (query.getOrderBy().isEmpty()) {
			return;
		}
		jpql.append(" ORDER BY ");
		for (int i = 0; i < query.getOrderBy().size(); i++) {
			OrderBy orderBy = query.getOrderBy().get(i);
			if (i > 0) {
				jpql.append(", ");
			}
			if (shape == QueryShape.OBJECTS) {
				jpql.append(rootPath(orderBy.getPath()));
			} else {
				jpql.append(rowKey(orderBy.getPath(), rowKeys));
			}
			jpql.append(orderBy.getDirection() == SortDirection.DESC ? " DESC" : " ASC");
		}
	}

	private String rowKey(PropertyPath path, List<String> rowKeys) throws QueryException {
		String flattened = outputKey(null, path);
		if (rowKeys.contains(flattened)) {
			return flattened;
		}
		if (path.getSegments().size() == 1 && rowKeys.contains(path.getSegments().get(0).getName())) {
			return path.getSegments().get(0).getName();
		}
		throw new QueryException("Sort path '" + flattened
				+ "' does not address an output key of the projection/aggregation (keys: " + rowKeys
				+ ") — alias the column accordingly");
	}

	private static void registerKey(String key, String alias, List<String> rowKeys, List<String> rowAliases)
			throws QueryException {
		if (rowKeys.contains(key)) {
			throw new QueryException("Duplicate result key '" + key + "' — use distinct aliases");
		}
		rowKeys.add(key);
		rowAliases.add(alias);
	}

	private static String outputKey(String alias, PropertyPath path) {
		if (alias != null && !alias.isBlank()) {
			return alias;
		}
		StringBuilder key = new StringBuilder();
		path.getSegments().forEach(segment -> {
			if (key.length() > 0) {
				key.append('_');
			}
			key.append(segment.getName());
		});
		return key.toString();
	}

	private static String rootPath(PropertyPath path) throws QueryException {
		if (path.getBase() != null) {
			throw new QueryException("Variable-based paths are only valid inside quantifier predicates");
		}
		return pathFrom(ALIAS, path);
	}

	private static String pathFrom(String alias, PropertyPath path) {
		StringBuilder rendered = new StringBuilder(alias);
		path.getSegments().forEach(segment -> rendered.append('.').append(segment.getName()));
		return rendered.toString();
	}

	// -------------------------------------------------- predicate rendering

	/** Carries the parameter map and quantifier alias scope through one translation. */
	private final class Translation {

		private final QueryContext context;
		private final Map<String, Object> parameters = new LinkedHashMap<>();
		private final Map<Variable, String> aliases = new HashMap<>();
		private int aliasCounter = 0;

		private Translation(QueryContext context) {
			this.context = context;
		}

		private String render(Expression expression) throws QueryException {
			if (expression instanceof Junction junction) {
				StringBuilder rendered = new StringBuilder("(");
				String operator = junction instanceof And ? " AND " : " OR ";
				for (int i = 0; i < junction.getOperands().size(); i++) {
					if (i > 0) {
						rendered.append(operator);
					}
					rendered.append(render(junction.getOperands().get(i)));
				}
				return rendered.append(')').toString();
			}
			if (expression instanceof Not not) {
				return "NOT (" + render(not.getOperand()) + ")";
			}
			if (expression instanceof Comparison comparison) {
				String operator = switch (comparison.getOperator()) {
				case EQ -> " = ";
				case NE -> " <> ";
				case LT -> " < ";
				case LE -> " <= ";
				case GT -> " > ";
				case GE -> " >= ";
				};
				EStructuralFeature target = targetOf(comparison.getLeft(), comparison.getRight());
				return operand(comparison.getLeft(), target) + operator + operand(comparison.getRight(), target);
			}
			if (expression instanceof IsNull isNull) {
				return operand(isNull.getSource(), null) + (isNull.isNegated() ? " IS NOT NULL" : " IS NULL");
			}
			if (expression instanceof Between between) {
				EStructuralFeature target = targetOf(between.getSource(), null);
				String source = operand(between.getSource(), target);
				String lower = operand(between.getLower(), target);
				String upper = operand(between.getUpper(), target);
				return "(" + source + (between.isLowerIncluded() ? " >= " : " > ") + lower + " AND " + source
						+ (between.isUpperIncluded() ? " <= " : " < ") + upper + ")";
			}
			if (expression instanceof In in) {
				EStructuralFeature target = targetOf(in.getSource(), null);
				StringBuilder rendered = new StringBuilder(operand(in.getSource(), target)).append(" IN (");
				for (int i = 0; i < in.getValues().size(); i++) {
					if (i > 0) {
						rendered.append(", ");
					}
					rendered.append(operand(in.getValues().get(i), target));
				}
				return rendered.append(')').toString();
			}
			if (expression instanceof StringMatch match) {
				return renderMatch(match);
			}
			if (expression instanceof Quantifier quantifier) {
				return renderQuantifier(quantifier);
			}
			throw new QueryException("Unsupported predicate " + expression.eClass().getName());
		}

		private String renderMatch(StringMatch match) throws QueryException {
			String source = operand(match.getSource(), null);
			String raw = text(match.getPattern());
			String pattern = switch (match.getKind()) {
			case CONTAINS -> "%" + escapeLike(raw) + "%";
			case STARTS_WITH -> escapeLike(raw) + "%";
			case ENDS_WITH -> "%" + escapeLike(raw);
			case LIKE -> raw;
			};
			String parameter = bind(pattern);
			if (match.isCaseInsensitive()) {
				return "LOWER(" + source + ") LIKE LOWER(" + parameter + ") ESCAPE '\\'";
			}
			return source + " LIKE " + parameter + " ESCAPE '\\'";
		}

		private String renderQuantifier(Quantifier quantifier) throws QueryException {
			String alias = "it" + aliasCounter++;
			aliases.put(quantifier.getVariable(), alias);
			String collection = pathFrom(ALIAS, quantifier.getSource());
			String predicate = render(quantifier.getPredicate());
			aliases.remove(quantifier.getVariable());
			if (quantifier instanceof Exists) {
				return "EXISTS (SELECT " + alias + " FROM " + collection + " " + alias + " WHERE " + predicate + ")";
			}
			return "NOT EXISTS (SELECT " + alias + " FROM " + collection + " " + alias + " WHERE NOT (" + predicate
					+ "))";
		}

		/** Renders a comparison operand: navigations/functions as expressions, values as parameters. */
		private String operand(Expression expression, EStructuralFeature target) throws QueryException {
			if (expression instanceof PropertyPath path) {
				String base = path.getBase() == null ? ALIAS : alias(path.getBase());
				return pathFrom(base, path);
			}
			if (expression instanceof StringFunction function) {
				String inner = operand(function.getSource(), target);
				return switch (function.getKind()) {
				case TO_LOWER -> "LOWER(" + inner + ")";
				case TO_UPPER -> "UPPER(" + inner + ")";
				case TRIM -> "TRIM(" + inner + ")";
				case LENGTH -> "LENGTH(" + inner + ")";
				};
			}
			return bind(ExpressionValues.resolve(expression, target, context.parameters(), context.converter()));
		}

		private String alias(Variable variable) throws QueryException {
			String alias = aliases.get(variable);
			if (alias == null) {
				throw new QueryException("Variable '" + variable.getName() + "' is not in scope");
			}
			return alias;
		}

		private EStructuralFeature targetOf(Expression left, Expression right) {
			if (left instanceof PropertyPath path) {
				return ExpressionValues.targetFeature(path);
			}
			if (right instanceof PropertyPath path) {
				return ExpressionValues.targetFeature(path);
			}
			if (left instanceof StringFunction function && function.getSource() instanceof PropertyPath path) {
				return ExpressionValues.targetFeature(path);
			}
			return null;
		}

		private String text(Expression pattern) throws QueryException {
			Object value = pattern instanceof Literal || pattern instanceof ParameterRef
					? ExpressionValues.resolve(pattern, null, context.parameters(), null)
					: null;
			if (value == null && !(pattern instanceof Literal)) {
				throw new QueryException("String match patterns must be literals or bound parameters");
			}
			return value == null ? "" : String.valueOf(value);
		}

		private String bind(Object value) {
			String name = "p" + parameters.size();
			parameters.put(name, value);
			return ":" + name;
		}
	}

	/** Escapes LIKE wildcards so CONTAINS/STARTS_WITH/ENDS_WITH match verbatim. */
	static String escapeLike(String value) {
		return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
	}
}
