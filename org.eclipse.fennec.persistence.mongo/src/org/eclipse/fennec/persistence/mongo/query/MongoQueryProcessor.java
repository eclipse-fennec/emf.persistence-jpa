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
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.model.expression.AliasRef;
import org.eclipse.fennec.model.expression.And;
import org.eclipse.fennec.model.expression.Arithmetic;
import org.eclipse.fennec.model.expression.Between;
import org.eclipse.fennec.model.expression.CollectionCount;
import org.eclipse.fennec.model.expression.Comparison;
import org.eclipse.fennec.model.expression.ComparisonOperator;
import org.eclipse.fennec.model.expression.Concat;
import org.eclipse.fennec.model.expression.Exists;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.GeoDistance;
import org.eclipse.fennec.model.expression.GeoWithin;
import org.eclipse.fennec.model.expression.In;
import org.eclipse.fennec.model.expression.IndexOf;
import org.eclipse.fennec.model.expression.IsNull;
import org.eclipse.fennec.model.expression.Junction;
import org.eclipse.fennec.model.expression.Literal;
import org.eclipse.fennec.model.expression.MapValue;
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
import org.eclipse.fennec.model.expression.Variable;
import org.eclipse.fennec.model.query.Aggregate;
import org.eclipse.fennec.model.query.AggregateMethod;
import org.eclipse.fennec.model.query.Computation;
import org.eclipse.fennec.model.query.ComputeStage;
import org.eclipse.fennec.model.query.FilterStage;
import org.eclipse.fennec.model.query.GroupByStage;
import org.eclipse.fennec.model.query.GroupKey;
import org.eclipse.fennec.model.query.OrderBy;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.Selection;
import org.eclipse.fennec.model.query.SkipStage;
import org.eclipse.fennec.model.query.SortDirection;
import org.eclipse.fennec.model.query.Stage;
import org.eclipse.fennec.model.query.TopStage;
import org.eclipse.fennec.persistence.capabilities.QueryCapabilities;
import org.eclipse.fennec.persistence.capabilities.QueryFeature;
import org.eclipse.fennec.persistence.mongo.MongoFlavor;
import org.eclipse.fennec.persistence.mongo.MongoFlavorCapabilities;
import org.eclipse.fennec.persistence.mongo.MongoPersistenceConstants;
import org.eclipse.fennec.persistence.query.QueryConstants;
import org.eclipse.fennec.persistence.helper.EMaps;
import org.eclipse.fennec.persistence.query.QueryException;
import org.eclipse.fennec.persistence.query.api.QueryContext;
import org.eclipse.fennec.persistence.query.api.QueryPlan;
import org.eclipse.fennec.persistence.query.api.QueryProcessor;
import org.eclipse.fennec.persistence.query.api.QueryShape;
import org.eclipse.fennec.persistence.query.expr.ExpressionAnalyzer;
import org.eclipse.fennec.persistence.query.expr.ExpressionValues;
import org.eclipse.fennec.persistence.query.support.QueryAnalysis;
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
 * <li>Null comparisons follow SQL's three-valued logic (issue #97): {@code NE} and
 * negated IN/string matches carry {@code $ne null} guards, {@code Not} translates by
 * negation push-down (De Morgan, operator inversion, quantifier duality) instead of the
 * two-valued {@code $nor} — a null-poisoned comparison never matches, negated or
 * not.</li>
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

	/**
	 * Diagnostic code: a map key that cannot be a BSON field name (issue #186). A map is
	 * stored as a sub-document keyed by the map key, so {@code .} and a leading {@code $}
	 * are field-name syntax — storing them would need an escaping scheme the contract does
	 * not define (§9.2), and querying them silently addresses something else.
	 */
	public static final int CODE_INVALID_MAP_KEY = 102;

	private final MongoFlavor flavor;
	private final QueryCapabilities capabilities;

	/** Creates a processor for {@link MongoFlavor#MONGO} — the DS component default. */
	public MongoQueryProcessor() {
		this(MongoFlavor.MONGO);
	}

	/**
	 * Creates a processor whose capability declaration matches {@code flavor}. Translation
	 * itself is flavor-independent — every flavor speaks the same wire protocol, so the
	 * generated filters and pipelines are identical; only what may be asked for differs.
	 *
	 * @param flavor the server flavor; {@code null} is treated as {@link MongoFlavor#MONGO}
	 */
	public MongoQueryProcessor(MongoFlavor flavor) {
		this.flavor = flavor == null ? MongoFlavor.MONGO : flavor;
		this.capabilities = MongoFlavorCapabilities.of(this.flavor);
	}

	@Override
	public String backend() {
		return BACKEND;
	}

	/** @return the server flavor this processor declares capabilities for */
	public MongoFlavor flavor() {
		return flavor;
	}

	@Override
	public QueryCapabilities capabilities() {
		return capabilities;
	}

	@Override
	public Diagnostic validate(Query query, EClass rootEClass) {
		Diagnostic base = QueryValidator.validate(ExpressionAnalyzer.analyze(query), rootEClass, capabilities);
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
			if (content instanceof MapValue mapValue) {
				String key = mapKeyOrNull(mapValue, null);
				if (key != null && (key.indexOf('.') >= 0 || key.startsWith("$"))) {
					result.add(new BasicDiagnostic(Diagnostic.ERROR, QueryValidator.DIAGNOSTIC_SOURCE,
							CODE_INVALID_MAP_KEY,
							"Map key '" + key + "' cannot be a BSON field name — '.' and a leading '$'"
									+ " are field-name syntax on this backend",
							new Object[] { mapValue }));
				}
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
			return negated(not.getOperand(), context);
		}
		if (expression instanceof Comparison comparison) {
			return comparison(comparison, comparison.getOperator(), context);
		}
		if (expression instanceof IsNull isNull) {
			String field = field(isNull.getSource(), context);
			return guarded(isNull.isNegated() ? Filters.ne(field, null) : Filters.eq(field, null),
					isNull.getSource(), context);
		}
		if (expression instanceof Between between) {
			String field = field(between.getSource(), context);
			EStructuralFeature target = targetOf(between.getSource());
			Object lower = value(between.getLower(), target, context);
			Object upper = value(between.getUpper(), target, context);
			return guarded(Filters.and(
					between.isLowerIncluded() ? Filters.gte(field, lower) : Filters.gt(field, lower),
					between.isUpperIncluded() ? Filters.lte(field, upper) : Filters.lt(field, upper)),
					between.getSource(), context);
		}
		if (expression instanceof In in) {
			String field = field(in.getSource(), context);
			EStructuralFeature target = targetOf(in.getSource());
			List<Object> values = new ArrayList<>(in.getValues().size());
			for (Expression candidate : in.getValues()) {
				Object resolved = value(candidate, target, context);
				if (resolved != null) {
					// SQL: a null option never matches ($in would match null docs)
					values.add(resolved);
				}
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
		if (expression instanceof GeoWithin geoWithin) {
			return MongoGeoPredicates.geoWithin(geoWithin, false);
		}
		throw new QueryException("Unsupported predicate " + expression.eClass().getName()
				+ " for the mongo backend");
	}

	/**
	 * Translates a comparison with an explicit effective operator — the negation
	 * rewrite (issue #97) passes the inverse. NE carries a {@code $ne null} guard:
	 * a bare {@code $ne} matches null and missing fields where SQL's NE over null
	 * is UNKNOWN and excludes the row.
	 */
	private Bson comparison(Comparison comparison, ComparisonOperator operator, QueryContext context)
			throws QueryException {
		boolean rightIsValue = comparison.getRight() instanceof Literal
				|| comparison.getRight() instanceof ParameterRef;
		if (comparison.getLeft() instanceof GeoDistance distance && rightIsValue) {
			return MongoGeoPredicates.geoDistance(distance, operator,
					value(comparison.getRight(), null, context));
		}
		if (comparison.getRight() instanceof GeoDistance distance
				&& (comparison.getLeft() instanceof Literal || comparison.getLeft() instanceof ParameterRef)) {
			// r <op> distance ≡ distance <mirrored op> r
			return MongoGeoPredicates.geoDistance(distance, mirrored(operator),
					value(comparison.getLeft(), null, context));
		}
		if (comparison.getLeft() instanceof AliasRef aliasRef && rightIsValue) {
			// pipeline output columns are plain top-level fields after the flatten
			Object bound = value(comparison.getRight(), null, context);
			return fieldComparison(aliasRef.getAlias(), operator, bound);
		}
		boolean plain = (comparison.getLeft() instanceof PropertyPath
				|| comparison.getLeft() instanceof MapValue) && rightIsValue;
		if (!plain) {
			return exprComparison(comparison, operator, context);
		}
		String field = field(comparison.getLeft(), context);
		EStructuralFeature target = targetOf(comparison.getLeft());
		Object value = value(comparison.getRight(), target, context);
		return guarded(fieldComparison(field, operator, value), comparison.getLeft(), context);
	}

	private static Bson fieldComparison(String field, ComparisonOperator operator, Object value) {
		return switch (operator) {
		case EQ -> Filters.eq(field, value);
		case NE -> Filters.and(Filters.ne(field, value), Filters.ne(field, null));
		case LT -> Filters.lt(field, value);
		case LE -> Filters.lte(field, value);
		case GT -> Filters.gt(field, value);
		case GE -> Filters.gte(field, value);
		};
	}

	/**
	 * SQL-3VL negation by push-down (issue #97): a {@code $nor} is the two-valued
	 * complement and would select rows whose predicate is UNKNOWN (a null operand).
	 * Junctions flip by De Morgan, comparisons by operator inversion — both exact
	 * under Kleene 3VL — quantifiers by duality (¬∃p = ∀¬p, ¬∀p = ∃¬p); negated
	 * string matches and IN carry explicit non-null guards.
	 */
	private Bson negated(Expression expression, QueryContext context) throws QueryException {
		if (expression instanceof Not not) {
			return predicate(not.getOperand(), context);
		}
		if (expression instanceof Junction junction) {
			List<Bson> operands = new ArrayList<>(junction.getOperands().size());
			for (Expression operand : junction.getOperands()) {
				operands.add(negated(operand, context));
			}
			return junction instanceof And ? Filters.or(operands) : Filters.and(operands);
		}
		if (expression instanceof Comparison comparison) {
			return comparison(comparison, inverse(comparison.getOperator()), context);
		}
		if (expression instanceof IsNull isNull) {
			// the null probe is two-valued — negation just flips it
			String field = field(isNull.getSource(), context);
			return guarded(isNull.isNegated() ? Filters.eq(field, null) : Filters.ne(field, null),
					isNull.getSource(), context);
		}
		if (expression instanceof Between between) {
			// ¬(l ≤ x ≤ u) → x < l OR x > u — positive operators exclude nulls natively
			String field = field(between.getSource(), context);
			EStructuralFeature target = targetOf(between.getSource());
			Object lower = value(between.getLower(), target, context);
			Object upper = value(between.getUpper(), target, context);
			return guarded(Filters.or(
					between.isLowerIncluded() ? Filters.lt(field, lower) : Filters.lte(field, lower),
					between.isUpperIncluded() ? Filters.gt(field, upper) : Filters.gte(field, upper)),
					between.getSource(), context);
		}
		if (expression instanceof In in) {
			String field = field(in.getSource(), context);
			EStructuralFeature target = targetOf(in.getSource());
			List<Object> values = new ArrayList<>(in.getValues().size());
			for (Expression candidate : in.getValues()) {
				Object resolved = value(candidate, target, context);
				if (resolved == null) {
					// SQL: NOT IN over a null option can never be TRUE
					return Filters.expr(false);
				}
				values.add(resolved);
			}
			return guarded(Filters.and(Filters.nin(field, values), Filters.ne(field, null)),
					in.getSource(), context);
		}
		if (expression instanceof StringMatch match) {
			// $not over a regex matches null/missing — SQL's NOT LIKE over null is UNKNOWN
			return guarded(Filters.and(Filters.not(match(match, context)),
					Filters.ne(field(match.getSource(), context), null)), match.getSource(), context);
		}
		if (expression instanceof Quantifier quantifier) {
			String collection = MongoFieldNames.render(quantifier.getSource());
			Bson inner = negated(quantifier.getPredicate(), context);
			if (quantifier instanceof Exists) {
				// ¬∃p = ∀¬p: no element where p is TRUE or UNKNOWN
				return Filters.nor(Filters.elemMatch(collection, Filters.nor(inner)));
			}
			// ¬∀p = ∃¬p: some element where p is plainly FALSE
			return Filters.elemMatch(collection, inner);
		}
		if (expression instanceof TypeCheck typeCheck) {
			// the discriminator is always present — the two-valued complement is exact
			return Filters.nor(MongoTypePredicates.typeCheck(typeCheck, codecResolver(context)));
		}
		if (expression instanceof GeoWithin geoWithin) {
			// $geoWithin excludes missing coordinates natively; the negated form adds
			// explicit guards so an UNKNOWN subject stays excluded (§5.5)
			return MongoGeoPredicates.geoWithin(geoWithin, true);
		}
		throw new QueryException("Unsupported negated predicate " + expression.eClass().getName()
				+ " for the mongo backend");
	}

	private static ComparisonOperator inverse(ComparisonOperator operator) {
		return switch (operator) {
		case EQ -> ComparisonOperator.NE;
		case NE -> ComparisonOperator.EQ;
		case LT -> ComparisonOperator.GE;
		case LE -> ComparisonOperator.GT;
		case GT -> ComparisonOperator.LE;
		case GE -> ComparisonOperator.LT;
		};
	}

	/** Operand-swap mirror: {@code r <op> x ≡ x <mirror(op)> r}. */
	private static ComparisonOperator mirrored(ComparisonOperator operator) {
		return switch (operator) {
		case LT -> ComparisonOperator.GT;
		case LE -> ComparisonOperator.GE;
		case GT -> ComparisonOperator.LT;
		case GE -> ComparisonOperator.LE;
		default -> operator;
		};
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
		String field = field(match.getSource(), context);
		String pattern = regexPattern(match, context);
		return match.isCaseInsensitive() ? Filters.regex(field, pattern, "i") : Filters.regex(field, pattern);
	}

	private String regexPattern(StringMatch match, QueryContext context) throws QueryException {
		Object raw = ExpressionValues.resolve(match.getPattern(), null, context.parameters(), null);
		String text = raw == null ? "" : String.valueOf(raw);
		return switch (match.getKind()) {
		case CONTAINS -> Pattern.quote(text);
		case STARTS_WITH -> "^" + Pattern.quote(text);
		case ENDS_WITH -> Pattern.quote(text) + "$";
		case LIKE -> likeToRegex(text);
		// unreachable: STRING_MATCH_FUZZY is undeclared, validation refused already (issue #167)
		case FUZZY -> throw new QueryException("FUZZY matching is not served by the mongo backend");
		};
	}

	/**
	 * Translates a comparison that the find vocabulary cannot express — a string
	 * function on either side or field-to-field — into an {@code $expr} aggregation
	 * expression. Every referenced field is guarded with {@code $ne null} so
	 * comparisons involving missing/null values are false (SQL and in-memory
	 * semantics; Mongo itself treats missing as equal to null).
	 */
	private Bson exprComparison(Comparison comparison, ComparisonOperator operator, QueryContext context)
			throws QueryException {
		EStructuralFeature target = exprTarget(comparison.getLeft(), comparison.getRight());
		List<Object> guards = new ArrayList<>();
		Object left = exprOperand(comparison.getLeft(), target, context, guards);
		Object right = exprOperand(comparison.getRight(), target, context, guards);
		Document compare = new Document(mongoOperator(operator), Arrays.asList(left, right));
		if (guards.isEmpty()) {
			return Filters.expr(compare);
		}
		List<Object> operands = new ArrayList<>(guards);
		operands.add(compare);
		return Filters.expr(new Document("$and", operands));
	}

	private static String mongoOperator(ComparisonOperator operator) {
		return switch (operator) {
		case EQ -> "$eq";
		case NE -> "$ne";
		case LT -> "$lt";
		case LE -> "$lte";
		case GT -> "$gt";
		case GE -> "$gte";
		};
	}

	/**
	 * Renders a boolean aggregation-language condition for a {@code $filter} cond
	 * (issue #86): paths based on the scoped variable address the element as
	 * {@code $$<var>.<field>}, carrying the usual {@code $ne null} guards. The v1
	 * vocabulary is comparisons, junctions, not, isNull, between, in and string
	 * matches over element fields and literals — nested functions are refused.
	 */
	private Object exprCondition(Expression expression, Variable variable, String name,
			QueryContext context) throws QueryException {
		if (expression instanceof Junction junction) {
			List<Object> operands = new ArrayList<>(junction.getOperands().size());
			for (Expression operand : junction.getOperands()) {
				operands.add(exprCondition(operand, variable, name, context));
			}
			return new Document(junction instanceof And ? "$and" : "$or", operands);
		}
		if (expression instanceof Not not) {
			return negatedCondition(not.getOperand(), variable, name, context);
		}
		if (expression instanceof Comparison comparison) {
			return condComparison(comparison, comparison.getOperator(), variable, name, context);
		}
		if (expression instanceof IsNull isNull) {
			Object value = condOperand(isNull.getSource(), variable, name, null, context, new ArrayList<>());
			return new Document(isNull.isNegated() ? "$ne" : "$eq", Arrays.asList(value, null));
		}
		if (expression instanceof Between between) {
			EStructuralFeature target = condTarget(between.getSource(), null);
			List<Object> guards = new ArrayList<>();
			Object value = condOperand(between.getSource(), variable, name, target, context, guards);
			Object lower = condOperand(between.getLower(), variable, name, target, context, guards);
			Object upper = condOperand(between.getUpper(), variable, name, target, context, guards);
			return guardedCondition(new Document("$and", Arrays.asList(
					new Document(between.isLowerIncluded() ? "$gte" : "$gt", Arrays.asList(value, lower)),
					new Document(between.isUpperIncluded() ? "$lte" : "$lt", Arrays.asList(value, upper)))),
					guards);
		}
		if (expression instanceof In in) {
			EStructuralFeature target = condTarget(in.getSource(), null);
			List<Object> guards = new ArrayList<>();
			Object value = condOperand(in.getSource(), variable, name, target, context, guards);
			List<Object> options = new ArrayList<>(in.getValues().size());
			for (Expression option : in.getValues()) {
				options.add(mongoValue(ExpressionValues.resolve(option, target, context.parameters(),
						context.converter())));
			}
			return guardedCondition(new Document("$in", Arrays.asList(value, options)), guards);
		}
		if (expression instanceof StringMatch match) {
			List<Object> guards = new ArrayList<>();
			Object input = condOperand(match.getSource(), variable, name, null, context, guards);
			Document regex = new Document("input", input)
					.append("regex", regexPattern(match, context));
			if (match.isCaseInsensitive()) {
				regex.append("options", "i");
			}
			return guardedCondition(new Document("$regexMatch", regex), guards);
		}
		throw new QueryException("Unsupported condition " + expression.eClass().getName()
				+ " inside a filtered collection count on the mongo backend");
	}

	private Object condComparison(Comparison comparison, ComparisonOperator operator, Variable variable,
			String name, QueryContext context) throws QueryException {
		EStructuralFeature target = condTarget(comparison.getLeft(), comparison.getRight());
		List<Object> guards = new ArrayList<>();
		Object left = condOperand(comparison.getLeft(), variable, name, target, context, guards);
		Object right = condOperand(comparison.getRight(), variable, name, target, context, guards);
		return guardedCondition(new Document(mongoOperator(operator), Arrays.asList(left, right)), guards);
	}

	/**
	 * The cond-language mirror of {@link #negated} (issue #97): a bare {@code $not}
	 * would turn a guard-collapsed false (null element field) back into true. The
	 * guards stay ANDed outside the pushed-down negation, so a null-poisoned
	 * element condition remains false — the element does not count.
	 */
	private Object negatedCondition(Expression expression, Variable variable, String name,
			QueryContext context) throws QueryException {
		if (expression instanceof Not not) {
			return exprCondition(not.getOperand(), variable, name, context);
		}
		if (expression instanceof Junction junction) {
			List<Object> operands = new ArrayList<>(junction.getOperands().size());
			for (Expression operand : junction.getOperands()) {
				operands.add(negatedCondition(operand, variable, name, context));
			}
			return new Document(junction instanceof And ? "$or" : "$and", operands);
		}
		if (expression instanceof Comparison comparison) {
			return condComparison(comparison, inverse(comparison.getOperator()), variable, name, context);
		}
		if (expression instanceof IsNull isNull) {
			Object value = condOperand(isNull.getSource(), variable, name, null, context, new ArrayList<>());
			return new Document(isNull.isNegated() ? "$eq" : "$ne", Arrays.asList(value, null));
		}
		if (expression instanceof Between between) {
			EStructuralFeature target = condTarget(between.getSource(), null);
			List<Object> guards = new ArrayList<>();
			Object value = condOperand(between.getSource(), variable, name, target, context, guards);
			Object lower = condOperand(between.getLower(), variable, name, target, context, guards);
			Object upper = condOperand(between.getUpper(), variable, name, target, context, guards);
			return guardedCondition(new Document("$or", Arrays.asList(
					new Document(between.isLowerIncluded() ? "$lt" : "$lte", Arrays.asList(value, lower)),
					new Document(between.isUpperIncluded() ? "$gt" : "$gte", Arrays.asList(value, upper)))),
					guards);
		}
		if (expression instanceof In in) {
			EStructuralFeature target = condTarget(in.getSource(), null);
			List<Object> guards = new ArrayList<>();
			Object value = condOperand(in.getSource(), variable, name, target, context, guards);
			List<Object> options = new ArrayList<>(in.getValues().size());
			for (Expression option : in.getValues()) {
				Object resolved = mongoValue(ExpressionValues.resolve(option, target, context.parameters(),
						context.converter()));
				if (resolved == null) {
					// SQL: NOT IN over a null option can never be TRUE
					return Boolean.FALSE;
				}
				options.add(resolved);
			}
			return guardedCondition(new Document("$not",
					List.of(new Document("$in", Arrays.asList(value, options)))), guards);
		}
		if (expression instanceof StringMatch match) {
			List<Object> guards = new ArrayList<>();
			Object input = condOperand(match.getSource(), variable, name, null, context, guards);
			Document regex = new Document("input", input)
					.append("regex", regexPattern(match, context));
			if (match.isCaseInsensitive()) {
				regex.append("options", "i");
			}
			return guardedCondition(new Document("$not", List.of(new Document("$regexMatch", regex))), guards);
		}
		throw new QueryException("Unsupported negated condition " + expression.eClass().getName()
				+ " inside a filtered collection count on the mongo backend");
	}

	private static Object guardedCondition(Document condition, List<Object> guards) {
		if (guards.isEmpty()) {
			return condition;
		}
		List<Object> operands = new ArrayList<>(guards);
		operands.add(condition);
		return new Document("$and", operands);
	}

	/** The element field typing literal peers inside a {@code $filter} cond. */
	private EStructuralFeature condTarget(Expression left, Expression right) {
		if (left instanceof PropertyPath path) {
			return ExpressionValues.targetFeature(path);
		}
		return right instanceof PropertyPath path ? ExpressionValues.targetFeature(path) : null;
	}

	/** One cond operand: the element field ({@code $$<var>.<field>}) or a bound value. */
	private Object condOperand(Expression expression, Variable variable, String name,
			EStructuralFeature target, QueryContext context, List<Object> guards) throws QueryException {
		if (expression instanceof PropertyPath path) {
			if (path.getBase() != variable) {
				throw new QueryException("Filtered collection counts address the element variable"
						+ " only — root paths and foreign variables are not supported in the cond");
			}
			String reference = "$$" + name + "." + MongoFieldNames.render(path);
			Document guard = new Document("$ne", Arrays.asList(reference, null));
			if (!guards.contains(guard)) {
				guards.add(guard);
			}
			return reference;
		}
		if (expression instanceof Literal || expression instanceof ParameterRef) {
			Object value = mongoValue(ExpressionValues.resolve(expression, target, context.parameters(),
					context.converter()));
			return new Document("$literal", value);
		}
		throw new QueryException("Unsupported operand " + expression.eClass().getName()
				+ " inside a filtered collection count on the mongo backend");
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
		if (expression instanceof MapValue mapValue) {
			// in an aggregation the same field path, as a field reference
			return "$" + mapField(mapValue, context);
		}
		if (expression instanceof CollectionCount count) {
			if (count.getSource().getBase() != null) {
				throw new QueryException("Collection counts inside quantifier predicates are not"
						+ " supported by the mongo backend");
			}
			// a missing array is an empty EMF list (smart compression omits it) — count 0,
			// so no $ne-null guard and an $ifNull fallback instead
			String reference = "$" + MongoFieldNames.render(count.getSource());
			Object input = new Document("$ifNull", Arrays.asList(reference, List.of()));
			if (count.getPredicate() == null) {
				return new Document("$size", input);
			}
			// predicated count: $size over $filter — the cond addresses the element
			// through the $$variable (issue #86)
			String name = count.getVariable().getName();
			Object cond = exprCondition(count.getPredicate(), count.getVariable(), name, context);
			return new Document("$size", new Document("$filter",
					new Document("input", input).append("as", name).append("cond", cond)));
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

	private String field(Expression expression, QueryContext context) throws QueryException {
		if (expression instanceof MapValue mapValue) {
			return mapField(mapValue, context);
		}
		if (!(expression instanceof PropertyPath path)) {
			throw new QueryException("The mongo backend requires a property path on the comparison's left side, was "
					+ expression.eClass().getName());
		}
		return MongoFieldNames.render(path);
	}

	/**
	 * {@code attributes.color} — a map is a sub-document keyed by the map key here (contract
	 * §9.2), so addressing one entry is a field path and nothing else. That is why the key has
	 * to be constant (issue #186): it is part of the field name, not a runtime value.
	 */
	private String mapField(MapValue mapValue, QueryContext context) throws QueryException {
		String key = mapKeyOrNull(mapValue, context);
		if (key == null) {
			throw new QueryException("MapValue key does not resolve to a field name");
		}
		if (key.indexOf('.') >= 0 || key.startsWith("$")) {
			throw new QueryException("Map key '" + key + "' cannot be a BSON field name —"
					+ " '.' and a leading '$' are field-name syntax on this backend");
		}
		return MongoFieldNames.render(mapValue.getMap()) + "." + key;
	}

	/**
	 * The stored form of a map key, or {@code null} when it cannot be determined — an unbound
	 * parameter during validation, or a malformed map access the shared analyzer already
	 * reports (issue #186).
	 */
	private String mapKeyOrNull(MapValue mapValue, QueryContext context) {
		EClass entryClass = EMaps.entryClass(ExpressionValues.targetFeature(mapValue.getMap()));
		if (entryClass == null) {
			return null;
		}
		try {
			Object key = ExpressionValues.resolve(mapValue.getKey(), EMaps.keyFeature(entryClass),
					context == null ? null : context.parameters(), null);
			return EMaps.renderKey(entryClass, key);
		} catch (QueryException e) {
			return null;
		}
	}

	private EStructuralFeature targetOf(Expression expression) {
		return ExpressionValues.targetFeature(expression);
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
				groupStage(groupBy, pipeline, rowKeys, rowAliases, context);
				rowSpace = true;
			} else if (stage instanceof ComputeStage compute) {
				// computed columns via $set (issue #82); after $group the flatten
				// $project lifted every alias to a plain top-level field
				boolean preGroup = !rowSpace && groupsSomewhere;
				if (!rowSpace && !groupsSomewhere) {
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
					if (!preGroup) {
						register(computation.getAlias(), computation.getAlias(), rowKeys, rowAliases);
					}
					// pre-group aliases (issue #87) stay intermediate $set fields for
					// group keys and aggregate sources — not result columns
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

	private void groupStage(GroupByStage groupBy, List<Bson> pipeline, List<String> rowKeys, List<String> rowAliases,
			QueryContext context) throws QueryException {
		Document id = new Document();
		for (PropertyPath path : groupBy.getPaths()) {
			String field = MongoFieldNames.render(path);
			id.put(field.replace('.', '_'), "$" + field);
		}
		for (GroupKey key : groupBy.getKeys()) {
			// expression-valued group key (issue #87) — evaluated inside _id, e.g.
			// over the $set fields of a pre-group compute (AliasRef → "$alias")
			id.put(key.getAlias(), exprOperand(key.getExpression(), null, context, new ArrayList<>()));
		}
		Document group = new Document("_id", id.isEmpty() ? null : id);
		Document flatten = new Document("_id", 0);
		id.keySet().forEach(key -> {
			// group keys are alias-addressable under their derived name
			register(key, key, rowKeys, rowAliases);
			flatten.put(key, "$_id." + key);
		});
		for (Aggregate aggregate : groupBy.getAggregates()) {
			String alias = aggregate.getAlias();
			register(alias, alias, rowKeys, rowAliases);
			Object argument = aggregateArgument(aggregate, context);
			switch (aggregate.getMethod()) {
			case SUM -> group.put(alias, new Document("$sum", argument));
			case MIN -> group.put(alias, new Document("$min", argument));
			case MAX -> group.put(alias, new Document("$max", argument));
			case AVG -> group.put(alias, new Document("$avg", argument));
			case COUNT -> group.put(alias, new Document("$sum", 1));
			case COUNT_DISTINCT -> {
				if (argument == null) {
					throw new QueryException("COUNT_DISTINCT requires a path or source (aggregate '"
							+ alias + "')");
				}
				group.put(alias, new Document("$addToSet", argument));
			}
			}
			if (aggregate.getMethod() == AggregateMethod.COUNT_DISTINCT) {
				flatten.put(alias, new Document("$size", "$" + alias));
			} else {
				flatten.put(alias, 1);
			}
		}
		pipeline.add(new Document("$group", group));
		pipeline.add(Aggregates.project(flatten));
	}

	/**
	 * The accumulator argument of an aggregate: an expression-valued source (issue
	 * #87, e.g. an AliasRef to a pre-group $set field), a rendered document path, or
	 * {@code null} for the bare COUNT.
	 */
	private Object aggregateArgument(Aggregate aggregate, QueryContext context) throws QueryException {
		if (aggregate.getSource() != null) {
			return exprOperand(aggregate.getSource(), null, context, new ArrayList<>());
		}
		return aggregate.getPath() == null ? null : "$" + MongoFieldNames.render(aggregate.getPath());
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
			String candidate;
			if (orderBy.getKey() instanceof AliasRef aliasRef) {
				// a bare AliasRef is a plain output-column sort (issue #102) —
				// $sort on the flattened field, native after $group
				candidate = aliasRef.getAlias();
			} else if (orderBy.getKey() != null) {
				// backstop — SORT_EXPRESSION is not declared, validation refuses first
				throw new QueryException("Sort expressions are not supported by the mongo backend"
						+ " (feature SORT_EXPRESSION)");
			} else {
				candidate = MongoFieldNames.render(orderBy.getPath()).replace('.', '_');
			}
			if (!keys.contains(candidate)) {
				throw new QueryException("Sort key '" + candidate
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
