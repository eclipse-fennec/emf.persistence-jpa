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
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.BsonInt32;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.conversions.Bson;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.model.expression.And;
import org.eclipse.fennec.model.expression.AliasRef;
import org.eclipse.fennec.model.expression.Arithmetic;
import org.eclipse.fennec.model.expression.Between;
import org.eclipse.fennec.model.expression.CollectionCount;
import org.eclipse.fennec.model.expression.Comparison;
import org.eclipse.fennec.model.expression.Concat;
import org.eclipse.fennec.model.expression.Exists;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.In;
import org.eclipse.fennec.model.expression.IndexOf;
import org.eclipse.fennec.model.expression.IsNull;
import org.eclipse.fennec.model.expression.Junction;
import org.eclipse.fennec.model.expression.Literal;
import org.eclipse.fennec.model.expression.Negate;
import org.eclipse.fennec.model.expression.Not;
import org.eclipse.fennec.model.expression.NumericFunction;
import org.eclipse.fennec.model.expression.ParameterRef;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.model.expression.Quantifier;
import org.eclipse.fennec.model.expression.StringFunction;
import org.eclipse.fennec.model.expression.StringFunctionKind;
import org.eclipse.fennec.model.expression.StringMatch;
import org.eclipse.fennec.model.expression.Substring;
import org.eclipse.fennec.model.expression.TemporalFunction;
import org.eclipse.fennec.model.expression.TypeCheck;
import org.eclipse.fennec.model.query.Aggregate;
import org.eclipse.fennec.model.query.AggregateMethod;
import org.eclipse.fennec.model.query.Computation;
import org.eclipse.fennec.model.query.ComputeStage;
import org.eclipse.fennec.model.query.FilterStage;
import org.eclipse.fennec.model.query.GroupByStage;
import org.eclipse.fennec.model.query.OrderBy;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.Selection;
import org.eclipse.fennec.model.query.SkipStage;
import org.eclipse.fennec.model.query.SortDirection;
import org.eclipse.fennec.model.query.Stage;
import org.eclipse.fennec.model.query.TopStage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.persistence.mongo.MongoPersistenceConstants;
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

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

/**
 * {@link QueryProcessor} for MongoDB over the expression IR.
 * <p>
 * OBJECTS/COUNT queries translate to the find path (filter + sort + skip/limit);
 * PROJECTION/AGGREGATION queries — and multi-stage pipelines, which Mongo serves
 * natively ({@link QueryFeature#PIPELINE}) — translate to an aggregation pipeline.
 * Translation is pure, no database access.
 * <p>
 * Mongo semantics:
 * <ul>
 * <li>Root EMF ID attribute → {@code _id}; nested paths in dot notation through
 * containment embedding only — cross-document paths are refused (code 100).</li>
 * <li>{@code IsNull} matches missing-or-null ({@code {field: null}}); negated →
 * {@code {$ne: null}}.</li>
 * <li>{@code Exists}/{@code ForAll} over <em>embedded</em> collections via
 * {@code $elemMatch} (ForAll = {@code $nor} around an $elemMatch of the negated
 * predicate); non-embedded quantifier sources are refused (code 100).</li>
 * <li>Case-insensitive matching via the regex {@code i} option; enum values compare by
 * literal name; DISTINCT requires a projection (code 101).</li>
 * <li>Field-to-field comparisons, string functions and arithmetic translate to
 * {@code $expr} aggregation expressions; every referenced field carries a
 * {@code $ne null} guard so comparisons with missing/null values are false — mirroring
 * the SQL and in-memory semantics. All are limited to root-based paths (not available
 * inside quantifier predicates).</li>
 * </ul>
 *
 * @author Mark Hoffmann
 * @since 24.07.2026
 */
@Component(service = QueryProcessor.class, property = QueryConstants.BACKEND_PROPERTY + "=" + MongoQueryProcessor.BACKEND)
public class MongoQueryProcessor implements QueryProcessor {

	/** The backend id of this processor. */
	public static final String BACKEND = "mongo";

	/** Diagnostic code: a path traverses a non-containment reference (no joins). */
	public static final int CODE_NON_EMBEDDED_PATH = 100;

	/** Diagnostic code: {@code distinct} without a projection. */
	public static final int CODE_DISTINCT_WITHOUT_PROJECTION = 101;

	private static final QueryCapabilities CAPABILITIES = QueryCapabilitiesBuilder.create()
			.support(QueryFeature.WHERE_EQ, QueryFeature.WHERE_NE, QueryFeature.WHERE_COMPARISON,
					QueryFeature.WHERE_RANGE, QueryFeature.IS_NULL, QueryFeature.IN,
					QueryFeature.WHERE_STRING_MATCH, QueryFeature.STRING_MATCH_CASE_INSENSITIVE,
					QueryFeature.STRING_FUNCTIONS, QueryFeature.STRING_FUNCTIONS_EXTENDED,
					QueryFeature.ARITHMETIC, QueryFeature.NUMERIC_FUNCTIONS,
					QueryFeature.TEMPORAL_FUNCTIONS, QueryFeature.COLLECTION_COUNT,
					QueryFeature.PIPELINE_COMPUTE, QueryFeature.TYPE_CAST, QueryFeature.TYPE_CHECK,
					QueryFeature.FIELD_TO_FIELD,
					QueryFeature.LOGICAL_AND, QueryFeature.LOGICAL_OR, QueryFeature.LOGICAL_NOT,
					QueryFeature.EXISTS, QueryFeature.FOR_ALL, QueryFeature.SORT, QueryFeature.LIMIT,
					QueryFeature.SKIP, QueryFeature.DISTINCT, QueryFeature.COUNT, QueryFeature.PROJECTION,
					QueryFeature.PROJECTION_NESTED, QueryFeature.GROUP_BY, QueryFeature.PIPELINE,
					QueryFeature.AGG_AVG, QueryFeature.AGG_MIN, QueryFeature.AGG_MAX, QueryFeature.AGG_SUM,
					QueryFeature.AGG_COUNT, QueryFeature.AGG_COUNT_DISTINCT, QueryFeature.TYPE_FILTER,
					QueryFeature.PARAMETERS, QueryFeature.FEATUREPATH_NESTED)
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
		Diagnostic base = QueryValidator.validate(ExpressionAnalyzer.analyze(query), rootEClass, CAPABILITIES);
		BasicDiagnostic result = new BasicDiagnostic(QueryValidator.DIAGNOSTIC_SOURCE, 0,
				"Mongo query validation", new Object[] { query });
		if (base.getSeverity() != Diagnostic.OK) {
			base.getChildren().forEach(result::add);
		}
		query.eAllContents().forEachRemaining(content -> {
			if (content instanceof PropertyPath path && !MongoFieldNames.isEmbeddedPath(path)) {
				result.add(new BasicDiagnostic(Diagnostic.ERROR, QueryValidator.DIAGNOSTIC_SOURCE,
						CODE_NON_EMBEDDED_PATH,
						"Path '" + MongoFieldNames.render(path)
								+ "' traverses a non-containment reference — Mongo cannot join across documents",
						new Object[] { path }));
			}
			if (content instanceof Quantifier quantifier
					&& !MongoFieldNames.isEmbeddedCollection(quantifier.getSource())) {
				result.add(new BasicDiagnostic(Diagnostic.ERROR, QueryValidator.DIAGNOSTIC_SOURCE,
						CODE_NON_EMBEDDED_PATH,
						"Quantifier source '" + MongoFieldNames.render(quantifier.getSource())
								+ "' is not an embedded (containment) collection — $elemMatch cannot apply",
						new Object[] { quantifier }));
			}
		});
		if (query.isDistinct() && query.getSelect().isEmpty()) {
			result.add(new BasicDiagnostic(Diagnostic.ERROR, QueryValidator.DIAGNOSTIC_SOURCE,
					CODE_DISTINCT_WITHOUT_PROJECTION,
					"DISTINCT requires a subject projection under Mongo — whole documents cannot be deduplicated",
					new Object[] { query }));
		}
		return result.getSeverity() == Diagnostic.OK ? Diagnostic.OK_INSTANCE : result;
	}

	@Override
	public QueryPlan translate(Query query, QueryContext context) throws QueryException {
		QueryAnalysis analysis = ExpressionAnalyzer.analyze(query);
		QueryShape shape = analysis.shape();
		if (query.getApply() != null && !query.getSelect().isEmpty()) {
			throw new QueryException("select and apply are mutually exclusive — aggregation defines its own columns");
		}
		if (!query.getExpand().isEmpty()) {
			throw new QueryException("expand is not supported by the mongo backend");
		}
		Bson filter = query.getPredicate() == null ? null : filter(query.getPredicate(), context);
		if (shape == QueryShape.PROJECTION || shape == QueryShape.AGGREGATION) {
			return pipelinePlan(query, shape, filter, context);
		}
		Bson sort = objectSort(query);
		return new MongoQueryPlan(query, shape, filter, sort, Math.max(0, query.getSkip()),
				Math.max(0, query.getTop()));
	}

	// -------------------------------------------------- find filter

	private Bson filter(Expression expression, QueryContext context) throws QueryException {
		return predicate(expression, context);
	}

	private Bson predicate(Expression expression, QueryContext context) throws QueryException {
		if (expression instanceof Junction junction) {
			List<Bson> operands = new ArrayList<>(junction.getOperands().size());
			for (Expression operand : junction.getOperands()) {
				operands.add(predicate(operand, context));
			}
			return junction instanceof And ? Filters.and(operands) : Filters.or(operands);
		}
		if (expression instanceof Not not) {
			return Filters.nor(predicate(not.getOperand(), context));
		}
		if (expression instanceof Comparison comparison) {
			boolean rightIsValue = comparison.getRight() instanceof Literal
					|| comparison.getRight() instanceof ParameterRef;
			if (comparison.getLeft() instanceof AliasRef aliasRef && rightIsValue) {
				// pipeline output columns are plain top-level fields after the flatten
				Object bound = value(comparison.getRight(), null, context);
				return switch (comparison.getOperator()) {
				case EQ -> Filters.eq(aliasRef.getAlias(), bound);
				case NE -> Filters.ne(aliasRef.getAlias(), bound);
				case LT -> Filters.lt(aliasRef.getAlias(), bound);
				case LE -> Filters.lte(aliasRef.getAlias(), bound);
				case GT -> Filters.gt(aliasRef.getAlias(), bound);
				case GE -> Filters.gte(aliasRef.getAlias(), bound);
				};
			}
			boolean plain = comparison.getLeft() instanceof PropertyPath && rightIsValue;
			if (!plain) {
				return exprComparison(comparison, context);
			}
			String field = field(comparison.getLeft());
			EStructuralFeature target = targetOf(comparison.getLeft());
			Object value = value(comparison.getRight(), target, context);
			Bson filter = switch (comparison.getOperator()) {
			case EQ -> Filters.eq(field, value);
			case NE -> Filters.ne(field, value);
			case LT -> Filters.lt(field, value);
			case LE -> Filters.lte(field, value);
			case GT -> Filters.gt(field, value);
			case GE -> Filters.gte(field, value);
			};
			return guarded(filter, comparison.getLeft(), context);
		}
		if (expression instanceof IsNull isNull) {
			String field = field(isNull.getSource());
			return guarded(isNull.isNegated() ? Filters.ne(field, null) : Filters.eq(field, null),
					isNull.getSource(), context);
		}
		if (expression instanceof Between between) {
			String field = field(between.getSource());
			EStructuralFeature target = targetOf(between.getSource());
			Object lower = value(between.getLower(), target, context);
			Object upper = value(between.getUpper(), target, context);
			return guarded(Filters.and(
					between.isLowerIncluded() ? Filters.gte(field, lower) : Filters.gt(field, lower),
					between.isUpperIncluded() ? Filters.lte(field, upper) : Filters.lt(field, upper)),
					between.getSource(), context);
		}
		if (expression instanceof In in) {
			String field = field(in.getSource());
			EStructuralFeature target = targetOf(in.getSource());
			List<Object> values = new ArrayList<>(in.getValues().size());
			for (Expression candidate : in.getValues()) {
				values.add(value(candidate, target, context));
			}
			return guarded(Filters.in(field, values), in.getSource(), context);
		}
		if (expression instanceof StringMatch match) {
			return guarded(match(match, context), match.getSource(), context);
		}
		if (expression instanceof Quantifier quantifier) {
			return quantifier(quantifier, context);
		}
		if (expression instanceof TypeCheck typeCheck) {
			// against the codec type discriminator, config-driven (issue #88)
			return MongoTypePredicates.typeCheck(typeCheck, codecResolver(context));
		}
		throw new QueryException("Unsupported predicate " + expression.eClass().getName()
				+ " for the mongo backend");
	}

	/** ANDs the treat guard when the operand path downcasts the root (castBase, issue #88). */
	private Bson guarded(Bson filter, Expression operand, QueryContext context) throws QueryException {
		if (operand instanceof PropertyPath path && path.getCastBase() != null) {
			return Filters.and(MongoTypePredicates.castGuard(path, codecResolver(context)), filter);
		}
		return filter;
	}

	/**
	 * The codec configuration the documents were written with — passed by the resource
	 * through the query options; defaults when queried standalone (issue #88).
	 */
	private ConfigurationResolver codecResolver(QueryContext context) {
		Object resolver = context.options() == null ? null
				: context.options().get(MongoPersistenceConstants.OPTION_CODEC_RESOLVER);
		return resolver instanceof ConfigurationResolver configured ? configured
				: ConfigurationResolver.defaults();
	}

	private Bson match(StringMatch match, QueryContext context) throws QueryException {
		String field = field(match.getSource());
		Object raw = ExpressionValues.resolve(match.getPattern(), null, context.parameters(), null);
		String text = raw == null ? "" : String.valueOf(raw);
		String pattern = switch (match.getKind()) {
		case CONTAINS -> Pattern.quote(text);
		case STARTS_WITH -> "^" + Pattern.quote(text);
		case ENDS_WITH -> Pattern.quote(text) + "$";
		case LIKE -> likeToRegex(text);
		};
		return match.isCaseInsensitive() ? Filters.regex(field, pattern, "i") : Filters.regex(field, pattern);
	}

	/**
	 * Translates a comparison that the find vocabulary cannot express — a string
	 * function on either side or field-to-field — into an {@code $expr} aggregation
	 * expression. Every referenced field is guarded with {@code $ne null} so
	 * comparisons involving missing/null values are false (SQL and in-memory
	 * semantics; Mongo itself treats missing as equal to null).
	 */
	private Bson exprComparison(Comparison comparison, QueryContext context) throws QueryException {
		EStructuralFeature target = exprTarget(comparison.getLeft(), comparison.getRight());
		List<Object> guards = new ArrayList<>();
		Object left = exprOperand(comparison.getLeft(), target, context, guards);
		Object right = exprOperand(comparison.getRight(), target, context, guards);
		String operator = switch (comparison.getOperator()) {
		case EQ -> "$eq";
		case NE -> "$ne";
		case LT -> "$lt";
		case LE -> "$lte";
		case GT -> "$gt";
		case GE -> "$gte";
		};
		Document compare = new Document(operator, Arrays.asList(left, right));
		if (guards.isEmpty()) {
			return Filters.expr(compare);
		}
		List<Object> operands = new ArrayList<>(guards);
		operands.add(compare);
		return Filters.expr(new Document("$and", operands));
	}

	/** Renders one {@code $expr} operand; collects a {@code $ne null} guard per referenced field. */
	private Object exprOperand(Expression expression, EStructuralFeature target, QueryContext context,
			List<Object> guards) throws QueryException {
		if (expression instanceof PropertyPath path) {
			if (path.getBase() != null) {
				throw new QueryException("String functions, arithmetic and field-to-field comparisons are not"
						+ " supported inside quantifier predicates on the mongo backend ($expr cannot address"
						+ " $elemMatch elements)");
			}
			if (path.getCastBase() != null) {
				throw new QueryException("Cast paths (castBase) are not supported inside $expr operands"
						+ " on the mongo backend — use them in plain filter positions");
			}
			String reference = "$" + MongoFieldNames.render(path);
			Document guard = new Document("$ne", Arrays.asList(reference, null));
			if (!guards.contains(guard)) {
				guards.add(guard);
			}
			return reference;
		}
		if (expression instanceof StringFunction function) {
			Object inner = exprOperand(function.getSource(), target, context, guards);
			return switch (function.getKind()) {
			case TO_LOWER -> new Document("$toLower", inner);
			case TO_UPPER -> new Document("$toUpper", inner);
			case TRIM -> new Document("$trim", new Document("input", inner));
			case LENGTH -> new Document("$strLenCP", inner);
			};
		}
		if (expression instanceof Arithmetic arithmetic) {
			Object left = exprOperand(arithmetic.getLeft(), target, context, guards);
			Object right = exprOperand(arithmetic.getRight(), target, context, guards);
			return switch (arithmetic.getOperator()) {
			case ADD -> new Document("$add", Arrays.asList(left, right));
			case SUB -> new Document("$subtract", Arrays.asList(left, right));
			case MUL -> new Document("$multiply", Arrays.asList(left, right));
			case DIV -> new Document("$divide", Arrays.asList(left, right));
			case MOD -> new Document("$mod", Arrays.asList(left, right));
			};
		}
		if (expression instanceof Negate negate) {
			Object operand = exprOperand(negate.getOperand(), target, context, guards);
			return new Document("$multiply", Arrays.asList(-1, operand));
		}
		if (expression instanceof NumericFunction numericFunction) {
			Object inner = exprOperand(numericFunction.getSource(), target, context, guards);
			return switch (numericFunction.getKind()) {
			// $round rounds half to even — emulate the contracted half-away-from-zero
			case ROUND -> new Document("$cond", Arrays.asList(
					new Document("$gte", Arrays.asList(inner, 0)),
					new Document("$floor", new Document("$add", Arrays.asList(inner, 0.5d))),
					new Document("$ceil", new Document("$subtract", Arrays.asList(inner, 0.5d)))));
			case FLOOR -> new Document("$floor", inner);
			case CEILING -> new Document("$ceil", inner);
			};
		}
		if (expression instanceof AliasRef aliasRef) {
			// a pipeline output column — plain field, no null guard (issue #82)
			return "$" + aliasRef.getAlias();
		}
		if (expression instanceof CollectionCount count) {
			if (count.getPredicate() != null) {
				throw new QueryException("Filtered collection counts are not supported by the mongo"
						+ " backend yet — declare COLLECTION_COUNT_FILTERED once $filter rendering lands");
			}
			if (count.getSource().getBase() != null) {
				throw new QueryException("Collection counts inside quantifier predicates are not"
						+ " supported by the mongo backend");
			}
			// a missing array is an empty EMF list (smart compression omits it) — count 0,
			// so no $ne-null guard and an $ifNull fallback instead
			String reference = "$" + MongoFieldNames.render(count.getSource());
			return new Document("$size", new Document("$ifNull", Arrays.asList(reference, List.of())));
		}
		if (expression instanceof TemporalFunction temporalFunction) {
			// BSON dates are UTC instants — the operators extract UTC parts natively,
			// matching the UTC-normative contract (issue #79)
			Object inner = exprOperand(temporalFunction.getSource(), target, context, guards);
			return switch (temporalFunction.getKind()) {
			case YEAR -> new Document("$year", inner);
			case MONTH -> new Document("$month", inner);
			case DAY -> new Document("$dayOfMonth", inner);
			case HOUR -> new Document("$hour", inner);
			case MINUTE -> new Document("$minute", inner);
			case SECOND -> new Document("$second", inner);
			};
		}
		if (expression instanceof Concat concatenation) {
			List<Object> parts = new ArrayList<>(concatenation.getParts().size());
			for (Expression part : concatenation.getParts()) {
				parts.add(exprOperand(part, target, context, guards));
			}
			return new Document("$concat", parts);
		}
		if (expression instanceof IndexOf indexOf) {
			// $indexOfCP is 0-based with -1 for absent — the IR semantics natively
			return new Document("$indexOfCP", Arrays.asList(
					exprOperand(indexOf.getSource(), target, context, guards),
					exprOperand(indexOf.getSearch(), target, context, guards)));
		}
		if (expression instanceof Substring substring) {
			return exprSubstring(substring, target, context, guards);
		}
		// $literal keeps values (notably strings starting with '$') out of path interpretation
		Object value = mongoValue(ExpressionValues.resolve(expression, target, context.parameters(),
				context.converter()));
		return new Document("$literal", value);
	}

	/**
	 * Substring per [OData-URL] 5.1.1.7 over {@code $substrCP}: the effective start is
	 * {@code start < 0 ? max(0, strlen + start) : min(start, strlen)}, the effective
	 * length is clamped into {@code [0, strlen - effectiveStart]} ({@code $substrCP}
	 * refuses negative arguments). Mirrors the memory oracle exactly.
	 */
	private Object exprSubstring(Substring substring, EStructuralFeature target, QueryContext context,
			List<Object> guards) throws QueryException {
		Object source = exprOperand(substring.getSource(), target, context, guards);
		Object start = exprOperand(substring.getStart(), target, context, guards);
		Object strlen = new Document("$strLenCP", source);
		Object effectiveStart = new Document("$cond", Arrays.asList(
				new Document("$lt", Arrays.asList(start, 0)),
				new Document("$max", Arrays.asList(0, new Document("$add", Arrays.asList(strlen, start)))),
				new Document("$min", Arrays.asList(start, strlen))));
		Object remaining = new Document("$subtract", Arrays.asList(strlen, effectiveStart));
		Object effectiveLength = remaining;
		if (substring.getLength() != null) {
			Object length = exprOperand(substring.getLength(), target, context, guards);
			effectiveLength = new Document("$min", Arrays.asList(
					new Document("$max", Arrays.asList(0, length)), remaining));
		}
		return new Document("$substrCP", Arrays.asList(source, effectiveStart, effectiveLength));
	}

	/** The feature that types literal operands: the path side's target — unless LENGTH shifts the domain to numbers. */
	private EStructuralFeature exprTarget(Expression left, Expression right) {
		EStructuralFeature target = pathTarget(left);
		return target != null ? target : pathTarget(right);
	}

	private EStructuralFeature pathTarget(Expression expression) {
		if (expression instanceof PropertyPath path) {
			return ExpressionValues.targetFeature(path);
		}
		if (expression instanceof StringFunction function && function.getKind() != StringFunctionKind.LENGTH) {
			return pathTarget(function.getSource());
		}
		return null;
	}

	private Bson quantifier(Quantifier quantifier, QueryContext context) throws QueryException {
		String collection = MongoFieldNames.render(quantifier.getSource());
		Bson inner = predicate(quantifier.getPredicate(), context);
		if (quantifier instanceof Exists) {
			return Filters.elemMatch(collection, inner);
		}
		// forAll: no element violates the predicate (vacuously true on empty)
		return Filters.nor(Filters.elemMatch(collection, Filters.nor(inner)));
	}

	private String field(Expression expression) throws QueryException {
		if (!(expression instanceof PropertyPath path)) {
			throw new QueryException("The mongo backend requires a property path on the comparison's left side, was "
					+ expression.eClass().getName());
		}
		return MongoFieldNames.render(path);
	}

	private EStructuralFeature targetOf(Expression expression) {
		return expression instanceof PropertyPath path ? ExpressionValues.targetFeature(path) : null;
	}

	private Object value(Expression expression, EStructuralFeature target, QueryContext context)
			throws QueryException {
		if (!(expression instanceof Literal) && !(expression instanceof ParameterRef)) {
			throw new QueryException("The mongo backend requires literal or parameter comparison values, was "
					+ expression.eClass().getName());
		}
		return mongoValue(ExpressionValues.resolve(expression, target, context.parameters(), context.converter()));
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

	// -------------------------------------------------- sorting

	private Bson objectSort(Query query) throws QueryException {
		if (query.getOrderBy().isEmpty()) {
			return null;
		}
		Bson[] entries = new Bson[query.getOrderBy().size()];
		for (int i = 0; i < query.getOrderBy().size(); i++) {
			OrderBy orderBy = query.getOrderBy().get(i);
			if (orderBy.getKey() != null) {
				// backstop — SORT_EXPRESSION is not declared, validation refuses first
				throw new QueryException("Sort expressions are not supported by the mongo backend"
						+ " (feature SORT_EXPRESSION)");
			}
			String field = MongoFieldNames.render(orderBy.getPath());
			entries[i] = orderBy.getDirection() == SortDirection.DESC ? Sorts.descending(field)
					: Sorts.ascending(field);
		}
		return entries.length == 1 ? entries[0] : Sorts.orderBy(entries);
	}

	// -------------------------------------------------- pipeline

	private MongoQueryPlan pipelinePlan(Query query, QueryShape shape, Bson filter, QueryContext context)
			throws QueryException {
		List<Bson> pipeline = new ArrayList<>();
		if (filter != null) {
			pipeline.add(Aggregates.match(filter));
		}
		List<String> rowKeys = new ArrayList<>();
		List<String> rowAliases = new ArrayList<>();

		List<Bson> deferredPaging = new ArrayList<>();
		if (shape == QueryShape.PROJECTION) {
			projectionStages(query, pipeline, rowKeys, rowAliases);
		} else {
			aggregationStages(query, pipeline, rowKeys, rowAliases, deferredPaging, context);
		}

		Bson sort = rowSort(query, rowKeys);
		if (sort != null) {
			pipeline.add(Aggregates.sort(sort));
		}
		// row-space pipeline paging is sort-then-limit — behind the sort, before the
		// envelope paging, so all backends page the same window
		pipeline.addAll(deferredPaging);
		if (query.getSkip() > 0) {
			pipeline.add(Aggregates.skip(query.getSkip()));
		}
		if (query.getTop() > 0) {
			pipeline.add(Aggregates.limit(query.getTop()));
		}
		return new MongoQueryPlan(query, shape, filter, sort, Math.max(0, query.getSkip()),
				Math.max(0, query.getTop()), pipeline, rowKeys, rowAliases);
	}

	private void projectionStages(Query query, List<Bson> pipeline, List<String> rowKeys, List<String> rowAliases)
			throws QueryException {
		Map<String, String> columns = new LinkedHashMap<>();
		for (Selection selection : query.getSelect()) {
			String key = outputKey(selection.getAlias(), selection.getPath());
			if (columns.put(key, MongoFieldNames.render(selection.getPath())) != null) {
				throw new QueryException("Duplicate result key '" + key + "' — use distinct aliases");
			}
			rowKeys.add(key);
			rowAliases.add(selection.getAlias());
		}
		if (query.isDistinct()) {
			BsonDocument id = new BsonDocument();
			BsonDocument flatten = new BsonDocument("_id", new BsonInt32(0));
			columns.forEach((key, fieldName) -> {
				id.put(key, new BsonString("$" + fieldName));
				flatten.put(key, new BsonString("$_id." + key));
			});
			pipeline.add(new BsonDocument("$group", new BsonDocument("_id", id)));
			pipeline.add(Aggregates.project(flatten));
			return;
		}
		BsonDocument project = new BsonDocument("_id", new BsonInt32(0));
		columns.forEach((key, fieldName) -> project.put(key, new BsonString("$" + fieldName)));
		pipeline.add(Aggregates.project(project));
	}

	private void aggregationStages(Query query, List<Bson> pipeline, List<String> rowKeys, List<String> rowAliases,
			List<Bson> deferredPaging, QueryContext context) throws QueryException {
		boolean groupsSomewhere = query.getApply().getStages().stream()
				.anyMatch(GroupByStage.class::isInstance);
		boolean rowSpace = false;
		for (Stage stage : query.getApply().getStages()) {
			if (stage instanceof FilterStage filterStage) {
				pipeline.add(Aggregates.match(filter(filterStage.getPredicate(), context)));
			} else if (stage instanceof GroupByStage groupBy) {
				groupStage(groupBy, pipeline, rowKeys, rowAliases);
				rowSpace = true;
			} else if (stage instanceof ComputeStage compute) {
				// computed columns via $set (issue #82); after $group the flatten
				// $project lifted every alias to a plain top-level field
				if (!rowSpace && groupsSomewhere) {
					throw new QueryException("A compute before GroupBy cannot be addressed by"
							+ " group paths or aggregate sources yet — move it after the"
							+ " grouping (issue #82)");
				}
				if (!rowSpace) {
					// terminal computes: one row per entity, single-valued attributes first
					for (EAttribute attribute : query.getFrom().getEAllAttributes()) {
						if (!attribute.isMany()) {
							register(attribute.getName(), attribute.getName(), rowKeys, rowAliases);
						}
					}
					rowSpace = true;
				}
				Document fields = new Document();
				for (Computation computation : compute.getComputations()) {
					fields.put(computation.getAlias(),
							exprOperand(computation.getExpression(), null, context, new ArrayList<>()));
					register(computation.getAlias(), computation.getAlias(), rowKeys, rowAliases);
				}
				pipeline.add(new Document("$set", fields));
			} else if (stage instanceof TopStage top) {
				// object-space paging stays in stage order; row-space paging defers
				// behind the sort (sort-then-limit)
				(rowSpace ? deferredPaging : pipeline).add(Aggregates.limit(top.getCount()));
			} else if (stage instanceof SkipStage skip) {
				(rowSpace ? deferredPaging : pipeline).add(Aggregates.skip(skip.getCount()));
			} else {
				throw new QueryException("Unsupported pipeline stage " + stage.eClass().getName());
			}
		}
	}

	private void groupStage(GroupByStage groupBy, List<Bson> pipeline, List<String> rowKeys, List<String> rowAliases)
			throws QueryException {
		Map<String, String> groupKeys = new LinkedHashMap<>();
		for (PropertyPath path : groupBy.getPaths()) {
			String field = MongoFieldNames.render(path);
			groupKeys.put(field.replace('.', '_'), field);
		}
		BsonDocument id = new BsonDocument();
		groupKeys.forEach((key, field) -> id.put(key, new BsonString("$" + field)));
		BsonDocument group = new BsonDocument("_id", groupKeys.isEmpty() ? BsonNull.VALUE : id);
		BsonDocument flatten = new BsonDocument("_id", new BsonInt32(0));
		groupKeys.keySet().forEach(key -> {
			// group keys are alias-addressable under their derived name
			register(key, key, rowKeys, rowAliases);
			flatten.put(key, new BsonString("$_id." + key));
		});
		for (Aggregate aggregate : groupBy.getAggregates()) {
			String alias = aggregate.getAlias();
			register(alias, alias, rowKeys, rowAliases);
			String field = aggregate.getPath() == null ? null : MongoFieldNames.render(aggregate.getPath());
			switch (aggregate.getMethod()) {
			case SUM -> group.put(alias, new BsonDocument("$sum", new BsonString("$" + field)));
			case MIN -> group.put(alias, new BsonDocument("$min", new BsonString("$" + field)));
			case MAX -> group.put(alias, new BsonDocument("$max", new BsonString("$" + field)));
			case AVG -> group.put(alias, new BsonDocument("$avg", new BsonString("$" + field)));
			case COUNT -> group.put(alias, new BsonDocument("$sum", new BsonInt32(1)));
			case COUNT_DISTINCT -> {
				if (field == null) {
					throw new QueryException("COUNT_DISTINCT requires a path (aggregate '" + alias + "')");
				}
				group.put(alias, new BsonDocument("$addToSet", new BsonString("$" + field)));
			}
			}
			if (aggregate.getMethod() == AggregateMethod.COUNT_DISTINCT) {
				flatten.put(alias, new BsonDocument("$size", new BsonString("$" + alias)));
			} else {
				flatten.put(alias, new BsonInt32(1));
			}
		}
		pipeline.add(new BsonDocument("$group", group));
		pipeline.add(Aggregates.project(flatten));
	}

	private static void register(String key, String alias, List<String> rowKeys, List<String> rowAliases) {
		rowKeys.add(key);
		rowAliases.add(alias);
	}

	private Bson rowSort(Query query, List<String> rowKeys) throws QueryException {
		if (query.getOrderBy().isEmpty()) {
			return null;
		}
		Set<String> keys = Set.copyOf(rowKeys);
		Bson[] entries = new Bson[query.getOrderBy().size()];
		for (int i = 0; i < query.getOrderBy().size(); i++) {
			OrderBy orderBy = query.getOrderBy().get(i);
			if (orderBy.getKey() != null) {
				// backstop — SORT_EXPRESSION is not declared, validation refuses first
				throw new QueryException("Sort expressions are not supported by the mongo backend"
						+ " (feature SORT_EXPRESSION)");
			}
			String candidate = MongoFieldNames.render(orderBy.getPath()).replace('.', '_');
			if (!keys.contains(candidate)) {
				throw new QueryException("Sort path '" + candidate
						+ "' does not address an output key of the projection/aggregation (keys: " + rowKeys
						+ ") — alias the column accordingly");
			}
			entries[i] = orderBy.getDirection() == SortDirection.DESC ? Sorts.descending(candidate)
					: Sorts.ascending(candidate);
		}
		return entries.length == 1 ? entries[0] : Sorts.orderBy(entries);
	}

	private static String outputKey(String alias, PropertyPath path) {
		if (alias != null && !alias.isBlank()) {
			return alias;
		}
		return MongoFieldNames.render(path).replace('.', '_');
	}
}
