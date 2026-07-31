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
package org.eclipse.fennec.persistence.query.memory;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.model.expression.And;
import org.eclipse.fennec.model.expression.Between;
import org.eclipse.fennec.model.expression.Comparison;
import org.eclipse.fennec.model.expression.Exists;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.In;
import org.eclipse.fennec.model.expression.IsNull;
import org.eclipse.fennec.model.expression.Junction;
import org.eclipse.fennec.model.expression.StringMatchKind;
import org.eclipse.fennec.model.expression.Not;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.model.expression.Quantifier;
import org.eclipse.fennec.model.expression.StringFunction;
import org.eclipse.fennec.model.expression.StringMatch;
import org.eclipse.fennec.model.expression.Variable;

/**
 * Interprets an expression tree against candidate {@link EObject}s. All values of
 * literals and bound parameters were resolved at translation time (see
 * {@link MemoryQueryProcessor}) — evaluation itself never throws.
 * <p>
 * Comparison semantics follow the database backends: any comparison with {@code null}
 * is <em>false</em> (SQL three-valued logic collapsed), {@code IsNull} is the explicit
 * null probe, {@code ForAll} is vacuously true on empty collections. Numbers compare
 * numerically across their boxed types, enums by their model value.
 *
 * @author Mark Hoffmann
 * @since 24.07.2026
 */
final class MemoryPredicate {

	/** Total order used for sorting and MIN/MAX: nulls last, numbers numeric. */
	static final Comparator<Object> VALUE_ORDER = (left, right) -> {
		if (left == null && right == null) {
			return 0;
		}
		if (left == null) {
			return 1;
		}
		if (right == null) {
			return -1;
		}
		Integer compared = tryCompare(left, right);
		return compared == null ? 0 : compared;
	};

	private final Expression root;
	/** Literal/parameter operand → resolved value, filled at translation time. */
	private final Map<Expression, Object> values;

	MemoryPredicate(Expression root, Map<Expression, Object> values) {
		this.root = root;
		this.values = values;
	}

	/** Evaluates the query's root predicate ({@code true} if the query has none). */
	boolean test(EObject candidate) {
		return root == null || test(root, candidate);
	}

	/** Evaluates any translated expression (e.g. a pipeline FilterStage predicate). */
	boolean test(Expression expression, EObject candidate) {
		return eval(expression, candidate, new HashMap<>());
	}

	private boolean eval(Expression expression, EObject candidate, Map<Variable, Object> bindings) {
		if (expression instanceof Junction junction) {
			boolean and = junction instanceof And;
			for (Expression operand : junction.getOperands()) {
				if (eval(operand, candidate, bindings) != and) {
					return !and;
				}
			}
			return and;
		}
		if (expression instanceof Not not) {
			return !eval(not.getOperand(), candidate, bindings);
		}
		if (expression instanceof Comparison comparison) {
			Object left = operand(comparison.getLeft(), candidate, bindings);
			Object right = operand(comparison.getRight(), candidate, bindings);
			if (left == null || right == null) {
				return false;
			}
			return switch (comparison.getOperator()) {
			case EQ -> equal(left, right);
			case NE -> !equal(left, right);
			case LT -> lessThan(left, right, false);
			case LE -> lessThan(left, right, true);
			case GT -> lessThan(right, left, false);
			case GE -> lessThan(right, left, true);
			};
		}
		if (expression instanceof IsNull isNull) {
			Object value = operand(isNull.getSource(), candidate, bindings);
			return isNull.isNegated() ? value != null : value == null;
		}
		if (expression instanceof Between between) {
			Object value = operand(between.getSource(), candidate, bindings);
			Object lower = operand(between.getLower(), candidate, bindings);
			Object upper = operand(between.getUpper(), candidate, bindings);
			if (value == null || lower == null || upper == null) {
				return false;
			}
			return lessThan(lower, value, between.isLowerIncluded())
					&& lessThan(value, upper, between.isUpperIncluded());
		}
		if (expression instanceof In in) {
			Object value = operand(in.getSource(), candidate, bindings);
			if (value == null) {
				return false;
			}
			return in.getValues().stream()
					.map(option -> operand(option, candidate, bindings))
					.anyMatch(option -> option != null && equal(value, option));
		}
		if (expression instanceof StringMatch match) {
			return evalMatch(match, candidate, bindings);
		}
		if (expression instanceof Quantifier quantifier) {
			return evalQuantifier(quantifier, candidate, bindings);
		}
		// unreachable: translation refused everything else
		return false;
	}

	private boolean evalMatch(StringMatch match, EObject candidate, Map<Variable, Object> bindings) {
		Object source = operand(match.getSource(), candidate, bindings);
		Object pattern = values.get(match.getPattern());
		if (!(source instanceof String text) || pattern == null) {
			return false;
		}
		String raw = String.valueOf(pattern);
		if (match.isCaseInsensitive() && match.getKind() != StringMatchKind.LIKE) {
			text = text.toLowerCase();
			raw = raw.toLowerCase();
		}
		return switch (match.getKind()) {
		case CONTAINS -> text.contains(raw);
		case STARTS_WITH -> text.startsWith(raw);
		case ENDS_WITH -> text.endsWith(raw);
		case LIKE -> likePattern(raw, match.isCaseInsensitive()).matcher(text).matches();
		};
	}

	private boolean evalQuantifier(Quantifier quantifier, EObject candidate, Map<Variable, Object> bindings) {
		Object collection = pathValue(quantifier.getSource(), candidate, bindings);
		boolean exists = quantifier instanceof Exists;
		if (!(collection instanceof Collection<?> elements) || elements.isEmpty()) {
			return !exists; // exists: false on empty, forAll: vacuously true
		}
		for (Object element : elements) {
			bindings.put(quantifier.getVariable(), element);
			boolean matches = eval(quantifier.getPredicate(), candidate, bindings);
			bindings.remove(quantifier.getVariable());
			if (matches == exists) {
				return exists;
			}
		}
		return !exists;
	}

	/** Resolves a comparison operand: navigations and functions per candidate, values from the map. */
	private Object operand(Expression expression, EObject candidate, Map<Variable, Object> bindings) {
		if (expression instanceof PropertyPath path) {
			return pathValue(path, candidate, bindings);
		}
		if (expression instanceof StringFunction function) {
			Object inner = operand(function.getSource(), candidate, bindings);
			if (inner == null) {
				return null;
			}
			String text = String.valueOf(inner);
			return switch (function.getKind()) {
			case TO_LOWER -> text.toLowerCase();
			case TO_UPPER -> text.toUpperCase();
			case TRIM -> text.trim();
			case LENGTH -> text.length();
			};
		}
		return values.get(expression);
	}

	/** Navigates a root-based path (used by the plan for sort/projection/grouping). */
	Object pathValue(PropertyPath path, EObject candidate) {
		return pathValue(path, candidate, Map.of());
	}

	private Object pathValue(PropertyPath path, EObject candidate, Map<Variable, Object> bindings) {
		Object current = path.getBase() == null ? candidate : bindings.get(path.getBase());
		for (EStructuralFeature segment : path.getSegments()) {
			if (!(current instanceof EObject object)) {
				return null;
			}
			EStructuralFeature actual = object.eClass().getEStructuralFeature(segment.getName());
			if (actual == null) {
				return null;
			}
			current = object.eGet(actual);
		}
		return current;
	}

	// ------------------------------------------------------------- value logic

	private static boolean equal(Object left, Object right) {
		if (left instanceof Number a && right instanceof Number b) {
			return Double.compare(a.doubleValue(), b.doubleValue()) == 0;
		}
		if (left instanceof Enumerator a && right instanceof Enumerator b) {
			return a.getValue() == b.getValue();
		}
		return Objects.equals(left, right);
	}

	private static boolean lessThan(Object left, Object right, boolean orEqual) {
		Integer compared = tryCompare(left, right);
		if (compared == null) {
			return false;
		}
		return orEqual ? compared <= 0 : compared < 0;
	}

	@SuppressWarnings("unchecked")
	private static Integer tryCompare(Object left, Object right) {
		if (left instanceof Number a && right instanceof Number b) {
			return Double.compare(a.doubleValue(), b.doubleValue());
		}
		if (left instanceof Enumerator a && right instanceof Enumerator b) {
			return Integer.compare(a.getValue(), b.getValue());
		}
		if (left instanceof Comparable comparable && left.getClass().isInstance(right)) {
			return comparable.compareTo(right);
		}
		return null;
	}

	/** Translates a LIKE pattern ({@code %}, {@code _}, {@code \} escape) into a regex. */
	private static Pattern likePattern(String like, boolean caseInsensitive) {
		StringBuilder regex = new StringBuilder();
		boolean escaped = false;
		for (int i = 0; i < like.length(); i++) {
			char c = like.charAt(i);
			if (escaped) {
				regex.append(Pattern.quote(String.valueOf(c)));
				escaped = false;
			} else if (c == '\\') {
				escaped = true;
			} else if (c == '%') {
				regex.append(".*");
			} else if (c == '_') {
				regex.append('.');
			} else {
				regex.append(Pattern.quote(String.valueOf(c)));
			}
		}
		return Pattern.compile(regex.toString(),
				caseInsensitive ? Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE : 0);
	}
}
