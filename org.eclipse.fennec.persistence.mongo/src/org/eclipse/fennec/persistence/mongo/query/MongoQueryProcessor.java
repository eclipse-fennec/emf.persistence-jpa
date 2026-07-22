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

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonNull;
import org.bson.BsonString;

import org.bson.conversions.Bson;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.model.query.Average;
import org.eclipse.fennec.model.query.Comparator;
import org.eclipse.fennec.model.query.Contains;
import org.eclipse.fennec.model.query.CountOperation;
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
import org.eclipse.fennec.model.query.Max;
import org.eclipse.fennec.model.query.Min;
import org.eclipse.fennec.model.query.Not;
import org.eclipse.fennec.model.query.Or;
import org.eclipse.fennec.model.query.Operation;
import org.eclipse.fennec.model.query.QSubject;
import org.eclipse.fennec.model.query.QWhere;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.SimpleValueComparator;
import org.eclipse.fennec.model.query.SortEntity;
import org.eclipse.fennec.model.query.SortOrder;
import org.eclipse.fennec.model.query.StartWith;
import org.eclipse.fennec.model.query.StringOperation;
import org.eclipse.fennec.model.query.Sum;
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

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

/**
 * {@link QueryProcessor} for MongoDB.
 * <p>
 * {@code OBJECTS}/{@code COUNT} queries translate to the find path of the
 * {@link MongoQueryPlan} (filter + sort + skip/limit, executed via
 * {@code collection.find(...)} / {@code countDocuments(...)}); {@code PROJECTION}/
 * {@code AGGREGATION} queries translate to an aggregation pipeline ($match → $group/
 * $project → $sort → $skip/$limit). Translation is pure — no database access.
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
 * Aggregation notes: DISTINCT requires a projection (whole documents cannot be
 * deduplicated) and is served by a $group over the projected keys; plain subjects in an
 * AGGREGATION query must match a groupBy path; sorting in PROJECTION/AGGREGATION shapes
 * addresses output keys.
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

	/** Diagnostic code: {@code distinct} without a subject projection. */
	public static final int CODE_DISTINCT_WITHOUT_PROJECTION = 101;

	private static final QueryCapabilities CAPABILITIES = QueryCapabilitiesBuilder.create()
			.support(QueryFeature.WHERE_EQ, QueryFeature.WHERE_COMPARISON, QueryFeature.WHERE_STRING_MATCH,
					QueryFeature.WHERE_RANGE, QueryFeature.WHERE_DATE, QueryFeature.WHERE_ENUM,
					QueryFeature.WHERE_BOOL, QueryFeature.LOGICAL_AND, QueryFeature.LOGICAL_OR,
					QueryFeature.LOGICAL_NOT, QueryFeature.SORT, QueryFeature.LIMIT, QueryFeature.SKIP,
					QueryFeature.COUNT, QueryFeature.TYPE_FILTER, QueryFeature.TYPE_FILTER_STRICT,
					QueryFeature.PARAMETERS, QueryFeature.OP_TO_LOWER, QueryFeature.OP_TO_UPPER,
					QueryFeature.FEATUREPATH_NESTED, QueryFeature.DISTINCT, QueryFeature.PROJECTION,
					QueryFeature.PROJECTION_NESTED, QueryFeature.GROUP_BY, QueryFeature.AGG_AVG,
					QueryFeature.AGG_MIN, QueryFeature.AGG_MAX, QueryFeature.AGG_SUM, QueryFeature.AGG_COUNT)
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
		if (query.isDistinct() && query.getSubject().isEmpty()) {
			result.add(new BasicDiagnostic(Diagnostic.ERROR, QueryValidator.DIAGNOSTIC_SOURCE,
					CODE_DISTINCT_WITHOUT_PROJECTION,
					"DISTINCT requires a subject projection under Mongo — whole documents cannot be deduplicated",
					new Object[] { query }));
		}
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
		Bson filter = buildFilter(query, context);
		if (shape == QueryShape.PROJECTION || shape == QueryShape.AGGREGATION) {
			return buildPipelinePlan(query, shape, filter);
		}
		Bson sort = buildSort(query);
		return new MongoQueryPlan(query, shape, filter, sort, Math.max(0, query.getSkip()),
				Math.max(0, query.getLimit()));
	}

	// -------------------------------------------------- aggregation pipeline

	private MongoQueryPlan buildPipelinePlan(Query query, QueryShape shape, Bson filter) throws QueryException {
		List<Bson> pipeline = new ArrayList<>();
		if (filter != null) {
			pipeline.add(Aggregates.match(filter));
		}
		// output key per subject (alias, or the path with dots flattened) in subject order
		Map<String, QSubject> outputs = new LinkedHashMap<>();
		List<String> rowKeys = new ArrayList<>();
		List<String> rowAliases = new ArrayList<>();
		for (QSubject subject : query.getSubject()) {
			String key = outputKey(subject);
			if (outputs.put(key, subject) != null) {
				throw new QueryException("Duplicate result key '" + key + "' — use distinct aliases");
			}
			rowKeys.add(key);
			rowAliases.add(subject.getAlias());
		}

		if (shape == QueryShape.AGGREGATION) {
			pipeline.addAll(groupStages(query, outputs));
		} else if (query.isDistinct()) {
			pipeline.addAll(distinctStages(outputs));
		} else {
			BsonDocument project = new BsonDocument("_id", new BsonInt32(0));
			outputs.forEach((key, subject) -> project.put(key,
					new BsonString("$" + MongoFieldNames.render(subject.getFeaturePath()))));
			pipeline.add(Aggregates.project(project));
		}

		Bson sort = buildOutputSort(query, outputs.keySet());
		if (sort != null) {
			pipeline.add(Aggregates.sort(sort));
		}
		if (query.getSkip() > 0) {
			pipeline.add(Aggregates.skip(query.getSkip()));
		}
		if (query.getLimit() > 0) {
			pipeline.add(Aggregates.limit(query.getLimit()));
		}
		return new MongoQueryPlan(query, shape, filter, sort, Math.max(0, query.getSkip()),
				Math.max(0, query.getLimit()), pipeline, rowKeys, rowAliases);
	}

	/** $group over group keys + accumulators, then $project flattening _id.* back to keys. */
	private List<Bson> groupStages(Query query, Map<String, QSubject> outputs) throws QueryException {
		// group key: the groupBy paths, keyed by their flattened field name
		Map<String, String> groupKeys = new LinkedHashMap<>();
		for (FeaturePath path : query.getGroupBy()) {
			String field = MongoFieldNames.render(path);
			groupKeys.put(flatten(field), field);
		}
		BsonDocument id = new BsonDocument();
		groupKeys.forEach((key, field) -> id.put(key, new BsonString("$" + field)));

		BsonDocument group = new BsonDocument("_id", groupKeys.isEmpty() ? BsonNull.VALUE : id);
		BsonDocument project = new BsonDocument("_id", new BsonInt32(0));
		for (Map.Entry<String, QSubject> output : outputs.entrySet()) {
			String key = output.getKey();
			QSubject subject = output.getValue();
			String field = MongoFieldNames.render(subject.getFeaturePath());
			Operation operation = subject.getOperation();
			String accumulator = accumulator(operation);
			if (accumulator == null) {
				// plain subject in an aggregation: must be a group key, projected from _id
				String groupKey = flatten(field);
				if (!groupKeys.containsKey(groupKey)) {
					throw new QueryException("Subject '" + field
							+ "' in an aggregation query is neither aggregated nor part of groupBy");
				}
				project.put(key, new BsonString("$_id." + groupKey));
				continue;
			}
			if (operation instanceof CountOperation) {
				group.put(key, new BsonDocument("$sum", new BsonInt32(1)));
			} else {
				group.put(key, new BsonDocument(accumulator, new BsonString("$" + field)));
			}
			project.put(key, new BsonInt32(1));
		}
		return List.of(new BsonDocument("$group", group), Aggregates.project(project));
	}

	/** DISTINCT projection: $group over the projected values, then $project from _id. */
	private List<Bson> distinctStages(Map<String, QSubject> outputs) {
		BsonDocument id = new BsonDocument();
		BsonDocument project = new BsonDocument("_id", new BsonInt32(0));
		outputs.forEach((key, subject) -> {
			id.put(key, new BsonString("$" + MongoFieldNames.render(subject.getFeaturePath())));
			project.put(key, new BsonString("$_id." + key));
		});
		return List.of(new BsonDocument("$group", new BsonDocument("_id", id)), Aggregates.project(project));
	}

	private static String accumulator(Operation operation) {
		if (operation instanceof Average) {
			return "$avg";
		}
		if (operation instanceof Min) {
			return "$min";
		}
		if (operation instanceof Max) {
			return "$max";
		}
		if (operation instanceof Sum) {
			return "$sum";
		}
		if (operation instanceof CountOperation) {
			return "$sum"; // rendered as {$sum: 1}
		}
		return null;
	}

	private static String outputKey(QSubject subject) {
		if (subject.getAlias() != null && !subject.getAlias().isBlank()) {
			return subject.getAlias();
		}
		return flatten(MongoFieldNames.render(subject.getFeaturePath()));
	}

	/** $project/$group output keys must not contain dots. */
	private static String flatten(String field) {
		return field.replace('.', '_');
	}

	/** Sort for pipeline output: sort features must address an output key. */
	private Bson buildOutputSort(Query query, java.util.Set<String> outputKeys) throws QueryException {
		if (query.getSortBy().isEmpty()) {
			return null;
		}
		Bson[] entries = new Bson[query.getSortBy().size()];
		for (int i = 0; i < query.getSortBy().size(); i++) {
			SortEntity sort = query.getSortBy().get(i);
			String key = sort.getSortFeature().getName();
			if (!outputKeys.contains(key)) {
				throw new QueryException("Sort feature '" + key
						+ "' does not address an output key of the projection/aggregation (keys: " + outputKeys
						+ ") — alias the subject accordingly");
			}
			entries[i] = sort.getSortOrder() == SortOrder.ASC ? Sorts.ascending(key) : Sorts.descending(key);
		}
		return entries.length == 1 ? entries[0] : Sorts.orderBy(entries);
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
