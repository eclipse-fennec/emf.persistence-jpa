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

import java.util.Date;
import java.util.regex.Pattern;

import org.bson.conversions.Bson;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.model.query.Comparator;
import org.eclipse.fennec.model.query.Contains;
import org.eclipse.fennec.model.query.DateComparator;
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
import org.eclipse.emf.common.util.Enumerator;
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

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

/**
 * {@link QueryProcessor} for MongoDB — the find path.
 * <p>
 * Translates {@code OBJECTS} and {@code COUNT} shaped queries into a {@link MongoQueryPlan}
 * (filter + sort + skip/limit) executed via {@code collection.find(...)} /
 * {@code countDocuments(...)}. Translation is pure — no database access.
 * <p>
 * Mongo-specific semantics:
 * <ul>
 * <li><b>Type filter</b>: collections are per-EClass (name-based mapping), so
 * {@code QObject.rootEClass} is naturally satisfied and translates to no filter —
 * declared supported because the storage layout guarantees it, not by post-filtering.</li>
 * <li><b>Nested paths</b> traverse embedded (containment) documents in dot notation.
 * Cross-document references hold proxy URIs — no join; {@link #validate} refuses such
 * paths with a diagnostic.</li>
 * <li><b>{@code toLower}/{@code toUpper}</b> on a predicate translate to case-insensitive
 * matching (regex {@code i} option) — valid for string comparators and {@code Eq} only.</li>
 * <li><b>Enum values</b> compare against the literal's name, matching the codec's
 * document encoding.</li>
 * </ul>
 * PROJECTION/AGGREGATION shapes are refused here and served by the aggregation-pipeline
 * stage (issue #41).
 *
 * @author Mark Hoffmann
 * @since 23.07.2026
 */
@Component(service = QueryProcessor.class, property = QueryConstants.BACKEND_PROPERTY + "=" + MongoQueryProcessor.BACKEND)
public class MongoQueryProcessor implements QueryProcessor {

	/** The backend id of this processor. */
	public static final String BACKEND = "mongo";

	/** Diagnostic code: a nested path traverses a non-containment reference. */
	public static final int CODE_NON_EMBEDDED_PATH = 100;

	private static final QueryCapabilities CAPABILITIES = QueryCapabilitiesBuilder.create()
			.support(QueryFeature.WHERE_EQ, QueryFeature.WHERE_COMPARISON, QueryFeature.WHERE_STRING_MATCH,
					QueryFeature.WHERE_RANGE, QueryFeature.WHERE_DATE, QueryFeature.WHERE_ENUM,
					QueryFeature.WHERE_BOOL, QueryFeature.LOGICAL_AND, QueryFeature.LOGICAL_OR,
					QueryFeature.LOGICAL_NOT, QueryFeature.SORT, QueryFeature.LIMIT, QueryFeature.SKIP,
					QueryFeature.COUNT, QueryFeature.TYPE_FILTER, QueryFeature.TYPE_FILTER_STRICT,
					QueryFeature.PARAMETERS, QueryFeature.OP_TO_LOWER, QueryFeature.OP_TO_UPPER,
					QueryFeature.FEATUREPATH_NESTED)
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
		Diagnostic base = QueryValidator.validate(query, rootEClass, CAPABILITIES);
		BasicDiagnostic result = new BasicDiagnostic(QueryValidator.DIAGNOSTIC_SOURCE, 0,
				"Mongo query validation", new Object[] { query });
		if (base.getSeverity() != Diagnostic.OK) {
			base.getChildren().forEach(result::add);
		}
		// Mongo cannot join: nested paths must stay inside the embedded document
		for (QWhere where : query.getWhere()) {
			checkEmbedded(where.getFeaturePath(), result);
		}
		query.getGroupBy().forEach(path -> checkEmbedded(path, result));
		query.getSubject().forEach(subject -> checkEmbedded(subject.getFeaturePath(), result));
		return result.getSeverity() == Diagnostic.OK ? Diagnostic.OK_INSTANCE : result;
	}

	private void checkEmbedded(FeaturePath path, BasicDiagnostic result) {
		if (path != null && !MongoFieldNames.isEmbeddedPath(path)) {
			result.add(new BasicDiagnostic(Diagnostic.ERROR, QueryValidator.DIAGNOSTIC_SOURCE,
					CODE_NON_EMBEDDED_PATH,
					"Feature path '" + pathText(path)
							+ "' traverses a non-containment reference — Mongo cannot join across documents",
					new Object[] { path }));
		}
	}

	private static String pathText(FeaturePath path) {
		StringBuilder text = new StringBuilder();
		path.getFeature().forEach(feature -> {
			if (text.length() > 0) {
				text.append('.');
			}
			text.append(feature.getName());
		});
		return text.toString();
	}

	@Override
	public QueryPlan translate(Query query, QueryContext context) throws QueryException {
		QueryAnalysis analysis = QueryAnalyzer.analyze(query);
		QueryShape shape = analysis.shape();
		if (shape == QueryShape.PROJECTION || shape == QueryShape.AGGREGATION) {
			throw new QueryException(
					"PROJECTION/AGGREGATION queries are not yet served by the Mongo find path (see issue #41)");
		}
		Bson filter = buildFilter(query, context);
		Bson sort = buildSort(query);
		return new MongoQueryPlan(query, shape, filter, sort, Math.max(0, query.getSkip()),
				Math.max(0, query.getLimit()));
	}

	// -------------------------------------------------- filter

	private Bson buildFilter(Query query, QueryContext context) throws QueryException {
		Bson filter = null;
		for (QWhere where : query.getWhere()) {
			Bson predicate = predicate(where, context);
			if (where instanceof Not) {
				predicate = Filters.nor(predicate);
			}
			if (filter == null) {
				filter = predicate;
			} else if (where instanceof Or) {
				filter = Filters.or(filter, predicate);
			} else {
				filter = Filters.and(filter, predicate);
			}
		}
		return filter;
	}

	private Bson predicate(QWhere where, QueryContext context) throws QueryException {
		FeaturePath path = where.getFeaturePath();
		String field = MongoFieldNames.render(path);
		EStructuralFeature target = QueryValues.targetFeature(path);
		Comparator comparator = where.getComparator();
		boolean caseInsensitive = where.getOperation() instanceof ToLowerCase
				|| where.getOperation() instanceof ToUpperCase;
		if (where.getOperation() != null && !(where.getOperation() instanceof StringOperation)) {
			throw new QueryException("Only toLower/toUpper operations are supported in Mongo predicates, was "
					+ where.getOperation().eClass().getName());
		}

		if (comparator instanceof Eq eq) {
			Object value = resolve(eq, target, context);
			if (caseInsensitive) {
				return regex(field, "^" + Pattern.quote(text(value)) + "$", true);
			}
			return Filters.eq(field, value);
		}
		if (comparator instanceof Lt lt) {
			return Filters.lt(field, resolve(lt, target, context));
		}
		if (comparator instanceof Lte lte) {
			return Filters.lte(field, resolve(lte, target, context));
		}
		if (comparator instanceof Gt gt) {
			return Filters.gt(field, resolve(gt, target, context));
		}
		if (comparator instanceof Gte gte) {
			return Filters.gte(field, resolve(gte, target, context));
		}
		if (comparator instanceof Contains contains) {
			return regex(field, Pattern.quote(rawText(contains, context)), caseInsensitive);
		}
		if (comparator instanceof StartWith startWith) {
			return regex(field, "^" + Pattern.quote(rawText(startWith, context)), caseInsensitive);
		}
		if (comparator instanceof EndsWith endsWith) {
			return regex(field, Pattern.quote(rawText(endsWith, context)) + "$", caseInsensitive);
		}
		if (comparator instanceof Like like) {
			return regex(field, likeToRegex(rawText(like, context)), caseInsensitive);
		}
		if (comparator instanceof DateComparator date) {
			Object value = resolve(date, target, context);
			if (date instanceof IsBefore) {
				return Filters.lt(field, value);
			}
			if (date instanceof IsBeforeOrEqual) {
				return Filters.lte(field, value);
			}
			if (date instanceof IsAfter) {
				return Filters.gt(field, value);
			}
			if (date instanceof IsAfterOrEqual) {
				return Filters.gte(field, value);
			}
		}
		if (comparator instanceof IsInRange range) {
			Object start = value(range.getStartValue(), target, context);
			Object end = value(range.getEndValue(), target, context);
			Bson lower = range.isStartIncluded() ? Filters.gte(field, start) : Filters.gt(field, start);
			Bson upper = range.isEndIncluded() ? Filters.lte(field, end) : Filters.lt(field, end);
			return Filters.and(lower, upper);
		}
		if (comparator instanceof IsLiteral literal) {
			// enums are stored under their literal name
			return Filters.eq(field, text(resolve(literal, target, context)));
		}
		if (comparator instanceof IsBool bool) {
			return Filters.eq(field, Boolean.valueOf(bool.getValue()));
		}
		throw new QueryException("Unsupported comparator "
				+ (comparator == null ? "null" : comparator.eClass().getName()) + " for field '" + field + "'");
	}

	private Object resolve(SimpleValueComparator comparator, EStructuralFeature target, QueryContext context)
			throws QueryException {
		return value(comparator.getValue(), target, context);
	}

	private Object value(String raw, EStructuralFeature target, QueryContext context) throws QueryException {
		Object value = QueryParameters.resolve(raw, target, context.parameters(), context.converter());
		return mongoValue(value);
	}

	/** Normalises EMF-typed values to their document encoding. */
	private static Object mongoValue(Object value) {
		if (value instanceof Enumerator enumerator) {
			return enumerator.getName();
		}
		if (value instanceof Date date) {
			return date;
		}
		return value;
	}

	/** Raw string for regex-based comparators: placeholder-resolved, no type parsing. */
	private String rawText(SimpleValueComparator comparator, QueryContext context) throws QueryException {
		String raw = comparator.getValue();
		if (QueryParameters.isPlaceholder(raw)) {
			Object bound = QueryParameters.resolve(raw, null, context.parameters(), null);
			return text(bound);
		}
		if (raw != null && raw.startsWith(QueryParameters.ESCAPE_PREFIX)) {
			return raw.substring(1);
		}
		return raw;
	}

	private static String text(Object value) {
		return value == null ? null : String.valueOf(value);
	}

	private static Bson regex(String field, String pattern, boolean caseInsensitive) {
		return caseInsensitive ? Filters.regex(field, pattern, "i") : Filters.regex(field, pattern);
	}

	/** Translates a SQL-like pattern ({@code %}, {@code _}) to an anchored regex. */
	static String likeToRegex(String like) {
		StringBuilder regex = new StringBuilder("^");
		for (int i = 0; i < like.length(); i++) {
			char c = like.charAt(i);
			switch (c) {
			case '%' -> regex.append(".*");
			case '_' -> regex.append('.');
			default -> {
				if ("\\.[]{}()*+-?^$|".indexOf(c) >= 0) {
					regex.append('\\');
				}
				regex.append(c);
			}
			}
		}
		return regex.append('$').toString();
	}

	// -------------------------------------------------- sort

	private Bson buildSort(Query query) {
		if (query.getSortBy().isEmpty()) {
			return null;
		}
		Bson[] entries = new Bson[query.getSortBy().size()];
		for (int i = 0; i < query.getSortBy().size(); i++) {
			SortEntity sort = query.getSortBy().get(i);
			String field = MongoFieldNames.render(sort.getSortFeature());
			entries[i] = sort.getSortOrder() == SortOrder.ASC ? Sorts.ascending(field) : Sorts.descending(field);
		}
		return entries.length == 1 ? entries[0] : Sorts.orderBy(entries);
	}
}
