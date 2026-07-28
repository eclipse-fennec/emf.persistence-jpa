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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.model.expression.And;
import org.eclipse.fennec.model.expression.Between;
import org.eclipse.fennec.model.expression.BooleanLiteral;
import org.eclipse.fennec.model.expression.Comparison;
import org.eclipse.fennec.model.expression.ComparisonOperator;
import org.eclipse.fennec.model.expression.EnumLiteral;
import org.eclipse.fennec.model.expression.Exists;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.ExpressionFactory;
import org.eclipse.fennec.model.expression.ForAll;
import org.eclipse.fennec.model.expression.In;
import org.eclipse.fennec.model.expression.IntegerLiteral;
import org.eclipse.fennec.model.expression.IsNull;
import org.eclipse.fennec.model.expression.Literal;
import org.eclipse.fennec.model.expression.Not;
import org.eclipse.fennec.model.expression.Or;
import org.eclipse.fennec.model.expression.ParameterRef;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.model.expression.Quantifier;
import org.eclipse.fennec.model.expression.RealLiteral;
import org.eclipse.fennec.model.expression.StringFunction;
import org.eclipse.fennec.model.expression.StringFunctionKind;
import org.eclipse.fennec.model.expression.StringLiteral;
import org.eclipse.fennec.model.expression.StringMatch;
import org.eclipse.fennec.model.expression.StringMatchKind;
import org.eclipse.fennec.model.expression.TemporalKind;
import org.eclipse.fennec.model.expression.TemporalLiteral;
import org.eclipse.fennec.model.expression.Variable;

/**
 * Static factory for expression trees — the composable half of builder v2. Designed for
 * static import; every method returns model objects ready for containment:
 *
 * <pre>
 * import static org.eclipse.fennec.model.query.builder.Expressions.*;
 *
 * Expression predicate = and(
 *     or(path(name).eq("smith"), path(name).containsIgnoreCase("x")),
 *     path(age).gte(18),
 *     path(age).isNotNull(),
 *     any(path(addresses), a -> a.path(street).startsWith("Main")));
 * </pre>
 *
 * Values passed to the comparison methods are auto-boxed into typed literals
 * ({@link #literal(Object)}); {@link #param(String)} references a named parameter.
 *
 * @author Mark Hoffmann
 * @since 24.07.2026
 */
public final class Expressions {

	private static final ExpressionFactory FACTORY = ExpressionFactory.eINSTANCE;

	private Expressions() {
	}

	// ==================== paths ====================

	/**
	 * Starts a predicate on a navigation path from the query root.
	 *
	 * @param segments the path segments, root feature first
	 * @return the path step exposing the comparison methods
	 */
	public static PathStep path(EStructuralFeature... segments) {
		return new PathStep(propertyPath(null, segments));
	}

	/**
	 * Creates a bare {@link PropertyPath} from the query root.
	 *
	 * @param segments the path segments, root feature first
	 * @return the property path
	 */
	public static PropertyPath propertyPath(EStructuralFeature... segments) {
		return propertyPath(null, segments);
	}

	private static PropertyPath propertyPath(Variable base, EStructuralFeature... segments) {
		if (segments.length == 0) {
			throw new IllegalArgumentException("a path needs at least one segment");
		}
		PropertyPath path = FACTORY.createPropertyPath();
		for (EStructuralFeature segment : segments) {
			Objects.requireNonNull(segment, "path segment must not be null");
			path.getSegments().add(segment);
		}
		if (base != null) {
			path.setBase(base);
		}
		return path;
	}

	// ==================== logic ====================

	/**
	 * @param operands at least two boolean operands
	 * @return the conjunction
	 */
	public static And and(Expression... operands) {
		And and = FACTORY.createAnd();
		junction(and.getOperands(), operands);
		return and;
	}

	/**
	 * @param operands at least two boolean operands
	 * @return the disjunction
	 */
	public static Or or(Expression... operands) {
		Or or = FACTORY.createOr();
		junction(or.getOperands(), operands);
		return or;
	}

	private static void junction(java.util.List<Expression> target, Expression... operands) {
		if (operands.length < 2) {
			throw new IllegalArgumentException("a junction needs at least two operands");
		}
		for (Expression operand : operands) {
			target.add(Objects.requireNonNull(operand, "operand must not be null"));
		}
	}

	/**
	 * @param operand the boolean operand to negate
	 * @return the negation
	 */
	public static Not not(Expression operand) {
		Not not = FACTORY.createNot();
		not.setOperand(Objects.requireNonNull(operand, "operand must not be null"));
		return not;
	}

	// ==================== quantifiers ====================

	/**
	 * Existential quantifier: at least one element of the multi-valued navigation
	 * satisfies the body.
	 *
	 * @param source the multi-valued navigation (see {@link #propertyPath(EStructuralFeature...)})
	 * @param body builds the predicate over the iterator variable
	 * @return the quantifier
	 */
	public static Exists any(PropertyPath source, Function<It, Expression> body) {
		Exists exists = FACTORY.createExists();
		quantify(exists, source, body);
		return exists;
	}

	/**
	 * Universal quantifier: every element of the multi-valued navigation satisfies the
	 * body (vacuously true on empty collections).
	 *
	 * @param source the multi-valued navigation
	 * @param body builds the predicate over the iterator variable
	 * @return the quantifier
	 */
	public static ForAll all(PropertyPath source, Function<It, Expression> body) {
		ForAll forAll = FACTORY.createForAll();
		quantify(forAll, source, body);
		return forAll;
	}

	private static void quantify(Quantifier quantifier, PropertyPath source, Function<It, Expression> body) {
		quantifier.setSource(Objects.requireNonNull(source, "source path must not be null"));
		Variable variable = FACTORY.createVariable();
		variable.setName("it");
		quantifier.setVariable(variable);
		quantifier.setPredicate(Objects.requireNonNull(body.apply(new It(variable)), "quantifier body must not be null"));
	}

	/** The iterator variable inside a quantifier body. */
	public static final class It {

		private final Variable variable;

		private It(Variable variable) {
			this.variable = variable;
		}

		/**
		 * Starts a predicate on a path from the iterator element.
		 *
		 * @param segments the path segments relative to the element
		 * @return the path step
		 */
		public PathStep path(EStructuralFeature... segments) {
			return new PathStep(propertyPath(variable, segments));
		}
	}

	// ==================== values ====================

	/**
	 * @param name the parameter name
	 * @return a first-class named parameter reference
	 */
	public static ParameterRef param(String name) {
		ParameterRef ref = FACTORY.createParameterRef();
		ref.setName(Objects.requireNonNull(name, "parameter name must not be null"));
		return ref;
	}

	/**
	 * Boxes a Java value into its typed literal: strings, integral and floating numbers,
	 * booleans, {@link Enumerator}s (by literal name), {@code java.time} temporals and
	 * {@link Date} (as INSTANT), {@code null}.
	 *
	 * @param value the value to box
	 * @return the literal
	 * @throws IllegalArgumentException for unsupported value types
	 */
	public static Literal literal(Object value) {
		if (value == null) {
			return FACTORY.createNullLiteral();
		}
		if (value instanceof Literal alreadyBoxed) {
			return alreadyBoxed;
		}
		if (value instanceof String string) {
			StringLiteral literal = FACTORY.createStringLiteral();
			literal.setValue(string);
			return literal;
		}
		if (value instanceof Integer || value instanceof Long || value instanceof Short || value instanceof Byte) {
			IntegerLiteral literal = FACTORY.createIntegerLiteral();
			literal.setValue(((Number) value).longValue());
			return literal;
		}
		if (value instanceof Double || value instanceof Float) {
			RealLiteral literal = FACTORY.createRealLiteral();
			literal.setValue(((Number) value).doubleValue());
			return literal;
		}
		if (value instanceof Boolean bool) {
			BooleanLiteral literal = FACTORY.createBooleanLiteral();
			literal.setValue(bool);
			return literal;
		}
		if (value instanceof Enumerator enumerator) {
			EnumLiteral literal = FACTORY.createEnumLiteral();
			literal.setLiteralName(enumerator.getName());
			return literal;
		}
		if (value instanceof Instant instant) {
			return temporal(TemporalKind.INSTANT, instant.toString());
		}
		if (value instanceof Date date) {
			return temporal(TemporalKind.INSTANT, date.toInstant().toString());
		}
		if (value instanceof LocalDate localDate) {
			return temporal(TemporalKind.DATE, localDate.toString());
		}
		if (value instanceof LocalDateTime localDateTime) {
			return temporal(TemporalKind.DATE_TIME, localDateTime.toString());
		}
		if (value instanceof LocalTime localTime) {
			return temporal(TemporalKind.TIME, localTime.toString());
		}
		throw new IllegalArgumentException("Cannot box value of type " + value.getClass().getName()
				+ " into a literal — pass a Literal or ParameterRef explicitly");
	}

	private static TemporalLiteral temporal(TemporalKind kind, String iso) {
		TemporalLiteral literal = FACTORY.createTemporalLiteral();
		literal.setKind(kind);
		literal.setValue(iso);
		return literal;
	}

	private static Expression value(Object value) {
		if (value instanceof PathStep step) {
			return step.toPath();
		}
		if (value instanceof FunctionStep step) {
			return step.toExpression();
		}
		return value instanceof Expression expression ? expression : literal(value);
	}

	private static Comparison compare(Expression left, ComparisonOperator operator, Object right) {
		Comparison comparison = FACTORY.createComparison();
		comparison.setOperator(operator);
		comparison.setLeft(left);
		comparison.setRight(value(right));
		return comparison;
	}

	private static StringFunction function(StringFunctionKind kind, Expression source) {
		StringFunction function = FACTORY.createStringFunction();
		function.setKind(kind);
		function.setSource(source);
		return function;
	}

	// ==================== the path step ====================

	/**
	 * A navigation path with the comparison vocabulary. Every method returns a finished
	 * expression node; values are auto-boxed ({@link Expressions#literal(Object)}) —
	 * pass a {@link ParameterRef} (via {@link Expressions#param(String)}) for prepared
	 * queries.
	 */
	public static final class PathStep {

		private final PropertyPath path;

		private PathStep(PropertyPath path) {
			this.path = path;
		}

		/**
		 * @return the underlying property path (e.g. for orderBy/select/quantifier sources)
		 */
		public PropertyPath toPath() {
			return path;
		}

		// --- comparisons ---

		/** @param value the value to compare against
		 *  @return the comparison */
		public Comparison eq(Object value) {
			return compare(ComparisonOperator.EQ, value);
		}

		/** @param value the value to compare against
		 *  @return the comparison */
		public Comparison ne(Object value) {
			return compare(ComparisonOperator.NE, value);
		}

		/** @param value the value to compare against
		 *  @return the comparison */
		public Comparison lt(Object value) {
			return compare(ComparisonOperator.LT, value);
		}

		/** @param value the value to compare against
		 *  @return the comparison */
		public Comparison le(Object value) {
			return compare(ComparisonOperator.LE, value);
		}

		/** @param value the value to compare against
		 *  @return the comparison */
		public Comparison gt(Object value) {
			return compare(ComparisonOperator.GT, value);
		}

		/** @param value the value to compare against
		 *  @return the comparison */
		public Comparison ge(Object value) {
			return compare(ComparisonOperator.GE, value);
		}

		/** Alias for {@link #ge(Object)}.
		 *  @param value the value to compare against
		 *  @return the comparison */
		public Comparison gte(Object value) {
			return ge(value);
		}

		/** Alias for {@link #le(Object)}.
		 *  @param value the value to compare against
		 *  @return the comparison */
		public Comparison lte(Object value) {
			return le(value);
		}

		private Comparison compare(ComparisonOperator operator, Object right) {
			return Expressions.compare(path, operator, right);
		}

		// --- string functions ---

		/** @return a comparable step over {@code LOWER(path)} */
		public FunctionStep toLower() {
			return new FunctionStep(function(StringFunctionKind.TO_LOWER, path));
		}

		/** @return a comparable step over {@code UPPER(path)} */
		public FunctionStep toUpper() {
			return new FunctionStep(function(StringFunctionKind.TO_UPPER, path));
		}

		/** @return a comparable step over {@code TRIM(path)} */
		public FunctionStep trim() {
			return new FunctionStep(function(StringFunctionKind.TRIM, path));
		}

		/** @return a comparable step over {@code LENGTH(path)} — compares numerically */
		public FunctionStep length() {
			return new FunctionStep(function(StringFunctionKind.LENGTH, path));
		}

		// --- null / range / membership ---

		/** @return the IS NULL check */
		public IsNull isNull() {
			return isNull(false);
		}

		/** @return the IS NOT NULL check */
		public IsNull isNotNull() {
			return isNull(true);
		}

		private IsNull isNull(boolean negated) {
			IsNull isNull = FACTORY.createIsNull();
			isNull.setNegated(negated);
			isNull.setSource(path);
			return isNull;
		}

		/**
		 * Range check with inclusive bounds.
		 *
		 * @param lower the lower bound
		 * @param upper the upper bound
		 * @return the range check
		 */
		public Between between(Object lower, Object upper) {
			return between(lower, upper, true, true);
		}

		/**
		 * Range check.
		 *
		 * @param lower the lower bound
		 * @param upper the upper bound
		 * @param lowerIncluded whether the lower bound is inclusive
		 * @param upperIncluded whether the upper bound is inclusive
		 * @return the range check
		 */
		public Between between(Object lower, Object upper, boolean lowerIncluded, boolean upperIncluded) {
			Between between = FACTORY.createBetween();
			between.setSource(path);
			between.setLower(value(lower));
			between.setUpper(value(upper));
			between.setLowerIncluded(lowerIncluded);
			between.setUpperIncluded(upperIncluded);
			return between;
		}

		/**
		 * @param values the candidate values (auto-boxed; parameters allowed)
		 * @return the membership test
		 */
		public In in(Object... values) {
			if (values.length == 0) {
				throw new IllegalArgumentException("in() needs at least one value");
			}
			In in = FACTORY.createIn();
			in.setSource(path);
			for (Object candidate : values) {
				in.getValues().add(value(candidate));
			}
			return in;
		}

		// --- string matching ---

		/** @param value the substring to match
		 *  @return the match */
		public StringMatch contains(Object value) {
			return match(StringMatchKind.CONTAINS, value, false);
		}

		/** Case-insensitive {@link #contains(Object)}.
		 *  @param value the substring to match
		 *  @return the match */
		public StringMatch containsIgnoreCase(Object value) {
			return match(StringMatchKind.CONTAINS, value, true);
		}

		/** @param value the prefix to match
		 *  @return the match */
		public StringMatch startsWith(Object value) {
			return match(StringMatchKind.STARTS_WITH, value, false);
		}

		/** Case-insensitive {@link #startsWith(Object)}.
		 *  @param value the prefix to match
		 *  @return the match */
		public StringMatch startsWithIgnoreCase(Object value) {
			return match(StringMatchKind.STARTS_WITH, value, true);
		}

		/** @param value the suffix to match
		 *  @return the match */
		public StringMatch endsWith(Object value) {
			return match(StringMatchKind.ENDS_WITH, value, false);
		}

		/** Case-insensitive {@link #endsWith(Object)}.
		 *  @param value the suffix to match
		 *  @return the match */
		public StringMatch endsWithIgnoreCase(Object value) {
			return match(StringMatchKind.ENDS_WITH, value, true);
		}

		/** @param pattern the LIKE pattern ({@code %}/{@code _})
		 *  @return the match */
		public StringMatch like(Object pattern) {
			return match(StringMatchKind.LIKE, pattern, false);
		}

		/** Case-insensitive {@link #like(Object)}.
		 *  @param pattern the LIKE pattern
		 *  @return the match */
		public StringMatch likeIgnoreCase(Object pattern) {
			return match(StringMatchKind.LIKE, pattern, true);
		}

		private StringMatch match(StringMatchKind kind, Object pattern, boolean caseInsensitive) {
			StringMatch match = FACTORY.createStringMatch();
			match.setKind(kind);
			match.setCaseInsensitive(caseInsensitive);
			match.setSource(path);
			match.setPattern(value(pattern));
			return match;
		}
	}

	/**
	 * A string-function application with the comparison vocabulary — created via
	 * {@link PathStep#toLower()}, {@link PathStep#toUpper()}, {@link PathStep#trim()} and
	 * {@link PathStep#length()}; functions chain ({@code path(name).trim().toLower()}).
	 * Values are auto-boxed like on {@link PathStep}.
	 */
	public static final class FunctionStep {

		private final StringFunction function;

		private FunctionStep(StringFunction function) {
			this.function = function;
		}

		/** @return the underlying function expression */
		public StringFunction toExpression() {
			return function;
		}

		// --- chaining ---

		/** @return a step over {@code LOWER(this)} */
		public FunctionStep toLower() {
			return new FunctionStep(function(StringFunctionKind.TO_LOWER, function));
		}

		/** @return a step over {@code UPPER(this)} */
		public FunctionStep toUpper() {
			return new FunctionStep(function(StringFunctionKind.TO_UPPER, function));
		}

		/** @return a step over {@code TRIM(this)} */
		public FunctionStep trim() {
			return new FunctionStep(function(StringFunctionKind.TRIM, function));
		}

		/** @return a step over {@code LENGTH(this)} — compares numerically */
		public FunctionStep length() {
			return new FunctionStep(function(StringFunctionKind.LENGTH, function));
		}

		// --- comparisons ---

		/** @param value the value to compare against
		 *  @return the comparison */
		public Comparison eq(Object value) {
			return compare(function, ComparisonOperator.EQ, value);
		}

		/** @param value the value to compare against
		 *  @return the comparison */
		public Comparison ne(Object value) {
			return compare(function, ComparisonOperator.NE, value);
		}

		/** @param value the value to compare against
		 *  @return the comparison */
		public Comparison lt(Object value) {
			return compare(function, ComparisonOperator.LT, value);
		}

		/** @param value the value to compare against
		 *  @return the comparison */
		public Comparison le(Object value) {
			return compare(function, ComparisonOperator.LE, value);
		}

		/** @param value the value to compare against
		 *  @return the comparison */
		public Comparison gt(Object value) {
			return compare(function, ComparisonOperator.GT, value);
		}

		/** @param value the value to compare against
		 *  @return the comparison */
		public Comparison ge(Object value) {
			return compare(function, ComparisonOperator.GE, value);
		}
	}
}
