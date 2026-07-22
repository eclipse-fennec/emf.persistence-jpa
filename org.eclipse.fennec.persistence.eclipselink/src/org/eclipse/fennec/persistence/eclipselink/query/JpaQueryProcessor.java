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

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.model.query.Comparator;
import org.eclipse.fennec.model.query.Contains;
import org.eclipse.fennec.model.query.EndsWith;
import org.eclipse.fennec.model.query.Eq;
import org.eclipse.fennec.model.query.Gt;
import org.eclipse.fennec.model.query.Gte;
import org.eclipse.fennec.model.query.IsAfter;
import org.eclipse.fennec.model.query.IsAfterOrEqual;
import org.eclipse.fennec.model.query.IsBefore;
import org.eclipse.fennec.model.query.IsBeforeOrEqual;
import org.eclipse.fennec.model.query.IsBool;
import org.eclipse.fennec.model.query.IsInRange;
import org.eclipse.fennec.model.query.IsLiteral;
import org.eclipse.fennec.model.query.Like;
import org.eclipse.fennec.model.query.Lt;
import org.eclipse.fennec.model.query.Lte;
import org.eclipse.fennec.model.query.Not;
import org.eclipse.fennec.model.query.Or;
import org.eclipse.fennec.model.query.QWhere;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.SimpleValueComparator;
import org.eclipse.fennec.model.query.SortEntity;
import org.eclipse.fennec.model.query.SortOrder;
import org.eclipse.fennec.model.query.StartWith;
import org.eclipse.fennec.model.query.StringOperation;
import org.eclipse.fennec.model.query.ToLowerCase;
import org.eclipse.fennec.model.query.ToUpperCase;
import org.eclipse.fennec.model.utilities.FeaturePath;
import org.eclipse.fennec.persistence.query.QueryConstants;
import org.eclipse.fennec.persistence.query.QueryException;
import org.eclipse.fennec.persistence.query.api.QueryCapabilities;
import org.eclipse.fennec.persistence.query.api.QueryContext;
import org.eclipse.fennec.persistence.query.api.QueryFeature;
import org.eclipse.fennec.persistence.query.api.QueryPlan;
import org.eclipse.fennec.persistence.query.api.QueryProcessor;
import org.eclipse.fennec.persistence.query.api.QueryShape;
import org.eclipse.fennec.persistence.query.support.QueryAnalysis;
import org.eclipse.fennec.persistence.query.support.QueryAnalyzer;
import org.eclipse.fennec.persistence.query.support.QueryCapabilitiesBuilder;
import org.eclipse.fennec.persistence.query.support.QueryParameters;
import org.eclipse.fennec.persistence.query.support.QueryValidator;
import org.eclipse.fennec.persistence.query.support.QueryValues;
import org.osgi.service.component.annotations.Component;

/**
 * {@link QueryProcessor} for JPA/EclipseLink.
 * <p>
 * Translates canonical queries into <b>JPQL</b> — deliberately not Criteria: a Criteria
 * tree requires a live {@code CriteriaBuilder} (i.e. an EntityManager), which would make
 * translation impure and untestable without a database. JPQL is a string, covers the full
 * feature set (implicit joins over path expressions, aggregates, grouping) and matches how
 * {@code JPAResourceImpl} already executes queries.
 * <p>
 * Safety: identifiers in the JPQL are model names (the entity name is the root EClass
 * name, path segments are feature names — valid Java identifiers by EMF construction);
 * every comparator value travels as a named JPQL parameter, never inlined.
 * <p>
 * {@code toLower}/{@code toUpper} predicates wrap both sides ({@code LOWER(e.f) = LOWER(:p)}),
 * mirroring the case-insensitive semantics of the Mongo processor. String matchers
 * ({@code Contains}/{@code StartWith}/{@code EndsWith}) become {@code LIKE} with escaped
 * wildcard characters in the bound value ({@code ESCAPE '\'}).
 * <p>
 * OBJECTS/COUNT shapes are served here; PROJECTION/AGGREGATION shapes are added by the
 * aggregation stage (issue #43).
 *
 * @author Mark Hoffmann
 * @since 23.07.2026
 */
@Component(service = QueryProcessor.class, property = QueryConstants.BACKEND_PROPERTY + "=" + JpaQueryProcessor.BACKEND)
public class JpaQueryProcessor implements QueryProcessor {

	/** The backend id of this processor. */
	public static final String BACKEND = "jpa";

	/** The JPQL root alias. */
	static final String ALIAS = "e";

	private static final QueryCapabilities CAPABILITIES = QueryCapabilitiesBuilder.create()
			.support(QueryFeature.WHERE_EQ, QueryFeature.WHERE_COMPARISON, QueryFeature.WHERE_STRING_MATCH,
					QueryFeature.WHERE_RANGE, QueryFeature.WHERE_DATE, QueryFeature.WHERE_ENUM,
					QueryFeature.WHERE_BOOL, QueryFeature.LOGICAL_AND, QueryFeature.LOGICAL_OR,
					QueryFeature.LOGICAL_NOT, QueryFeature.SORT, QueryFeature.LIMIT, QueryFeature.SKIP,
					QueryFeature.DISTINCT, QueryFeature.COUNT, QueryFeature.TYPE_FILTER,
					QueryFeature.TYPE_FILTER_STRICT, QueryFeature.PARAMETERS, QueryFeature.OP_TO_LOWER,
					QueryFeature.OP_TO_UPPER, QueryFeature.FEATUREPATH_NESTED)
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
		return QueryValidator.validate(query, rootEClass, CAPABILITIES);
	}

	@Override
	public QueryPlan translate(Query query, QueryContext context) throws QueryException {
		QueryAnalysis analysis = QueryAnalyzer.analyze(query);
		QueryShape shape = analysis.shape();
		if (shape == QueryShape.PROJECTION || shape == QueryShape.AGGREGATION) {
			throw new QueryException(
					"PROJECTION/AGGREGATION queries are not yet served by the JPA processor (see issue #43)");
		}
		String entity = entityName(context.rootEClass());
		Map<String, Object> parameters = new LinkedHashMap<>();
		String where = buildWhere(query, context, parameters);

		StringBuilder jpql = new StringBuilder("SELECT ");
		if (shape == QueryShape.COUNT) {
			jpql.append("COUNT(").append(ALIAS).append(')');
		} else {
			if (query.isDistinct()) {
				jpql.append("DISTINCT ");
			}
			jpql.append(ALIAS);
		}
		jpql.append(" FROM ").append(entity).append(' ').append(ALIAS);
		if (!where.isEmpty()) {
			jpql.append(" WHERE ").append(where);
		}
		if (shape != QueryShape.COUNT) {
			appendOrderBy(jpql, query);
		}
		return new JpaQueryPlan(query, shape, jpql.toString(), parameters, Math.max(0, query.getSkip()),
				Math.max(0, query.getLimit()), null, null);
	}

	private static String entityName(EClass rootEClass) throws QueryException {
		if (rootEClass == null) {
			throw new QueryException("The query context carries no root EClass — cannot derive the entity name");
		}
		return rootEClass.getName();
	}

	// -------------------------------------------------- where

	private String buildWhere(Query query, QueryContext context, Map<String, Object> parameters)
			throws QueryException {
		String where = "";
		for (QWhere entry : query.getWhere()) {
			String predicate = predicate(entry, context, parameters);
			if (entry instanceof Not) {
				predicate = "NOT (" + predicate + ")";
			}
			if (where.isEmpty()) {
				where = predicate;
			} else if (entry instanceof Or) {
				where = "(" + where + " OR " + predicate + ")";
			} else {
				where = "(" + where + " AND " + predicate + ")";
			}
		}
		return where;
	}

	private String predicate(QWhere entry, QueryContext context, Map<String, Object> parameters)
			throws QueryException {
		FeaturePath featurePath = entry.getFeaturePath();
		String path = pathExpression(featurePath);
		EStructuralFeature target = QueryValues.targetFeature(featurePath);
		Comparator comparator = entry.getComparator();
		boolean lower = entry.getOperation() instanceof ToLowerCase;
		boolean upper = entry.getOperation() instanceof ToUpperCase;
		if (entry.getOperation() != null && !(entry.getOperation() instanceof StringOperation)) {
			throw new QueryException("Only toLower/toUpper operations are supported in JPA predicates, was "
					+ entry.getOperation().eClass().getName());
		}
		String lhs = lower ? "LOWER(" + path + ")" : upper ? "UPPER(" + path + ")" : path;

		if (comparator instanceof Eq eq) {
			return lhs + " = " + rhs(eq.getValue(), target, context, parameters, lower, upper);
		}
		if (comparator instanceof Lt lt) {
			return lhs + " < " + rhs(lt.getValue(), target, context, parameters, lower, upper);
		}
		if (comparator instanceof Lte lte) {
			return lhs + " <= " + rhs(lte.getValue(), target, context, parameters, lower, upper);
		}
		if (comparator instanceof Gt gt) {
			return lhs + " > " + rhs(gt.getValue(), target, context, parameters, lower, upper);
		}
		if (comparator instanceof Gte gte) {
			return lhs + " >= " + rhs(gte.getValue(), target, context, parameters, lower, upper);
		}
		if (comparator instanceof Contains contains) {
			return like(lhs, "%" + escapeLike(rawText(contains, context)) + "%", parameters, lower, upper);
		}
		if (comparator instanceof StartWith startWith) {
			return like(lhs, escapeLike(rawText(startWith, context)) + "%", parameters, lower, upper);
		}
		if (comparator instanceof EndsWith endsWith) {
			return like(lhs, "%" + escapeLike(rawText(endsWith, context)), parameters, lower, upper);
		}
		if (comparator instanceof Like likeComparator) {
			return like(lhs, rawText(likeComparator, context), parameters, lower, upper);
		}
		if (comparator instanceof IsBefore before) {
			return lhs + " < " + rhs(before.getValue(), target, context, parameters, false, false);
		}
		if (comparator instanceof IsBeforeOrEqual beforeOrEqual) {
			return lhs + " <= " + rhs(beforeOrEqual.getValue(), target, context, parameters, false, false);
		}
		if (comparator instanceof IsAfter after) {
			return lhs + " > " + rhs(after.getValue(), target, context, parameters, false, false);
		}
		if (comparator instanceof IsAfterOrEqual afterOrEqual) {
			return lhs + " >= " + rhs(afterOrEqual.getValue(), target, context, parameters, false, false);
		}
		if (comparator instanceof IsInRange range) {
			String lowerBound = rhs(range.getStartValue(), target, context, parameters, false, false);
			String upperBound = rhs(range.getEndValue(), target, context, parameters, false, false);
			String lowerOp = range.isStartIncluded() ? " >= " : " > ";
			String upperOp = range.isEndIncluded() ? " <= " : " < ";
			return "(" + lhs + lowerOp + lowerBound + " AND " + lhs + upperOp + upperBound + ")";
		}
		if (comparator instanceof IsLiteral literal) {
			return lhs + " = " + rhs(literal.getValue(), target, context, parameters, false, false);
		}
		if (comparator instanceof IsBool bool) {
			return lhs + " = " + bind(parameters, Boolean.valueOf(bool.getValue()));
		}
		throw new QueryException("Unsupported comparator "
				+ (comparator == null ? "null" : comparator.eClass().getName()) + " for path '" + path + "'");
	}

	/** Renders a comparison right-hand side: resolve, bind, optionally case-fold. */
	private String rhs(String raw, EStructuralFeature target, QueryContext context, Map<String, Object> parameters,
			boolean lower, boolean upper) throws QueryException {
		Object value = QueryParameters.resolve(raw, target, context.parameters(), context.converter());
		String parameter = bind(parameters, value);
		return lower ? "LOWER(" + parameter + ")" : upper ? "UPPER(" + parameter + ")" : parameter;
	}

	private String like(String lhs, String pattern, Map<String, Object> parameters, boolean lower, boolean upper) {
		String parameter = bind(parameters, pattern);
		String rhsExpr = lower ? "LOWER(" + parameter + ")" : upper ? "UPPER(" + parameter + ")" : parameter;
		return lhs + " LIKE " + rhsExpr + " ESCAPE '\\'";
	}

	/** Raw string for LIKE-based comparators: placeholder-resolved, no type parsing. */
	private String rawText(SimpleValueComparator comparator, QueryContext context) throws QueryException {
		String raw = comparator.getValue();
		if (QueryParameters.isPlaceholder(raw)) {
			Object bound = QueryParameters.resolve(raw, null, context.parameters(), null);
			return bound == null ? null : String.valueOf(bound);
		}
		if (raw != null && raw.startsWith(QueryParameters.ESCAPE_PREFIX)) {
			return raw.substring(1);
		}
		return raw;
	}

	/** Escapes LIKE wildcards in a literal so Contains/StartWith/EndsWith match verbatim. */
	static String escapeLike(String value) {
		return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
	}

	private static String bind(Map<String, Object> parameters, Object value) {
		String name = "p" + parameters.size();
		parameters.put(name, value);
		return ":" + name;
	}

	// -------------------------------------------------- path / order

	private static String pathExpression(FeaturePath featurePath) throws QueryException {
		if (featurePath == null || featurePath.getFeature().isEmpty()) {
			throw new QueryException("A predicate requires a non-empty feature path");
		}
		StringBuilder path = new StringBuilder(ALIAS);
		for (EStructuralFeature segment : featurePath.getFeature()) {
			path.append('.').append(segment.getName());
		}
		return path.toString();
	}

	private static void appendOrderBy(StringBuilder jpql, Query query) {
		if (query.getSortBy().isEmpty()) {
			return;
		}
		jpql.append(" ORDER BY ");
		for (int i = 0; i < query.getSortBy().size(); i++) {
			SortEntity sort = query.getSortBy().get(i);
			if (i > 0) {
				jpql.append(", ");
			}
			jpql.append(ALIAS).append('.').append(sort.getSortFeature().getName())
					.append(sort.getSortOrder() == SortOrder.ASC ? " ASC" : " DESC");
		}
	}
}
