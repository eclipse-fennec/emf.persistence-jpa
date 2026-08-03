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
package org.eclipse.fennec.expression.ocl;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.fennec.m2x.model.ocl.BooleanLiteralExp;
import org.eclipse.fennec.m2x.model.ocl.CollectionItem;
import org.eclipse.fennec.m2x.model.ocl.CollectionLiteralExp;
import org.eclipse.fennec.m2x.model.ocl.EnumLiteralExp;
import org.eclipse.fennec.m2x.model.ocl.IntegerLiteralExp;
import org.eclipse.fennec.m2x.model.ocl.IteratorExp;
import org.eclipse.fennec.m2x.model.ocl.NullLiteralExp;
import org.eclipse.fennec.m2x.model.ocl.OclExpression;
import org.eclipse.fennec.m2x.model.ocl.OperationCallExp;
import org.eclipse.fennec.m2x.model.ocl.PropertyCallExp;
import org.eclipse.fennec.m2x.model.ocl.RealLiteralExp;
import org.eclipse.fennec.m2x.model.ocl.StringLiteralExp;
import org.eclipse.fennec.m2x.model.ocl.VariableExp;
import org.eclipse.fennec.model.expression.Arithmetic;
import org.eclipse.fennec.model.expression.ArithmeticOperator;
import org.eclipse.fennec.model.expression.Comparison;
import org.eclipse.fennec.model.expression.ComparisonOperator;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.ExpressionFactory;
import org.eclipse.fennec.model.expression.In;
import org.eclipse.fennec.model.expression.IsNull;
import org.eclipse.fennec.model.expression.Junction;
import org.eclipse.fennec.model.expression.Negate;
import org.eclipse.fennec.model.expression.Not;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.model.expression.Quantifier;
import org.eclipse.fennec.model.expression.StringFunction;
import org.eclipse.fennec.model.expression.StringFunctionKind;
import org.eclipse.fennec.model.expression.StringMatch;
import org.eclipse.fennec.model.expression.StringMatchKind;
import org.eclipse.fennec.model.expression.Variable;
import org.eclipse.fennec.persistence.query.QueryException;

/**
 * Maps m2x Essential-OCL ASTs onto Fennec expressions — the <b>partial</b> direction of
 * the bridge (decision R6): exactly the blessed subset is accepted, everything else is
 * refused with a {@link QueryException} naming the construct. This is the entry point
 * for OCL-producing frontends (the OData {@code $filter} pipeline in its phase-1
 * migration, m2x-parsed OCL text).
 * <p>
 * Recognised operations: {@code = <> < <= > >=}, {@code and or not},
 * {@code oclIsUndefined} (→ IsNull), {@code includes} over a collection literal (→ In),
 * {@code contains/startsWith/endsWith/like} (→ StringMatch, a {@code toLowerCase} pair
 * on both sides folds into the case-insensitive flag),
 * {@code toLowerCase/toUpperCase/trim/size} (→ StringFunction),
 * {@code + - * / mod} (→ Arithmetic; a source-only {@code -} → Negate; the integer
 * division {@code div} stays refused — truncation is deliberately not modelled),
 * {@code exists/forAll} iterators (→ quantifiers), property-call chains
 * (→ PropertyPath) and the literal set.
 *
 * @author Mark Hoffmann
 * @since 24.07.2026
 */
public final class OclToExpr {

	private static final ExpressionFactory EXPR = ExpressionFactory.eINSTANCE;

	private OclToExpr() {
	}

	/**
	 * Maps an OCL expression to the Fennec expression model.
	 *
	 * @param ocl the OCL expression
	 * @return the expression tree
	 * @throws QueryException if the OCL uses constructs outside the blessed subset
	 */
	public static Expression toExpr(OclExpression ocl) throws QueryException {
		return new Mapper(Map.of()).map(ocl);
	}

	/**
	 * Maps an OCL expression with variables that are already in scope — e.g. the body of
	 * an externally handled iterator, whose iterator variable the caller binds to an
	 * expression-model {@link Variable} of its own (see the derived-references concept).
	 *
	 * @param ocl the OCL expression
	 * @param scope pre-bound variables, OCL variable → expression variable
	 * @return the expression tree
	 * @throws QueryException if the OCL uses constructs outside the blessed subset
	 */
	public static Expression toExpr(OclExpression ocl,
			Map<org.eclipse.fennec.m2x.model.ocl.Variable, Variable> scope) throws QueryException {
		return new Mapper(scope).map(ocl);
	}

	private static final class Mapper {

		private final Map<org.eclipse.fennec.m2x.model.ocl.Variable, Variable> variables = new HashMap<>();

		private Mapper(Map<org.eclipse.fennec.m2x.model.ocl.Variable, Variable> scope) {
			variables.putAll(scope);
		}

		private Expression map(OclExpression ocl) throws QueryException {
			if (ocl instanceof OperationCallExp call) {
				return operation(call);
			}
			if (ocl instanceof IteratorExp iterator) {
				return iterator(iterator);
			}
			if (ocl instanceof PropertyCallExp property) {
				return path(property);
			}
			if (ocl instanceof VariableExp variableExp) {
				org.eclipse.fennec.model.expression.VariableRef ref = EXPR.createVariableRef();
				ref.setVariable(scoped(variableExp));
				return ref;
			}
			if (ocl instanceof StringLiteralExp string) {
				org.eclipse.fennec.model.expression.StringLiteral literal = EXPR.createStringLiteral();
				literal.setValue(string.getStringSymbol());
				return literal;
			}
			if (ocl instanceof IntegerLiteralExp integer) {
				org.eclipse.fennec.model.expression.IntegerLiteral literal = EXPR.createIntegerLiteral();
				literal.setValue(integer.getIntegerSymbol());
				return literal;
			}
			if (ocl instanceof RealLiteralExp real) {
				org.eclipse.fennec.model.expression.RealLiteral literal = EXPR.createRealLiteral();
				literal.setValue(real.getRealSymbol());
				return literal;
			}
			if (ocl instanceof BooleanLiteralExp bool) {
				org.eclipse.fennec.model.expression.BooleanLiteral literal = EXPR.createBooleanLiteral();
				literal.setValue(bool.isBooleanSymbol());
				return literal;
			}
			if (ocl instanceof NullLiteralExp) {
				return EXPR.createNullLiteral();
			}
			if (ocl instanceof EnumLiteralExp enumExp) {
				org.eclipse.fennec.model.expression.EnumLiteral literal = EXPR.createEnumLiteral();
				literal.setLiteralName(enumExp.getReferredLiteral() == null ? null
						: enumExp.getReferredLiteral().getName());
				return literal;
			}
			throw refuse(ocl.eClass().getName());
		}

		private Expression operation(OperationCallExp call) throws QueryException {
			String name = call.getName();
			switch (name == null ? "" : name) {
			case "=", "<>", "<", "<=", ">", ">=" -> {
				Comparison comparison = EXPR.createComparison();
				comparison.setOperator(switch (name) {
				case "=" -> ComparisonOperator.EQ;
				case "<>" -> ComparisonOperator.NE;
				case "<" -> ComparisonOperator.LT;
				case "<=" -> ComparisonOperator.LE;
				case ">" -> ComparisonOperator.GT;
				default -> ComparisonOperator.GE;
				});
				comparison.setLeft(map(call.getOwnedSource()));
				comparison.setRight(map(argument(call, 0)));
				return comparison;
			}
			case "and", "or" -> {
				// left-deep binary OCL chains flatten back into the n-ary junction
				Junction junction = "and".equals(name) ? EXPR.createAnd() : EXPR.createOr();
				addFlattened(junction, map(call.getOwnedSource()));
				addFlattened(junction, map(argument(call, 0)));
				return junction;
			}
			case "not" -> {
				Expression operand = map(call.getOwnedSource());
				if (operand instanceof IsNull isNull && !isNull.isNegated()) {
					isNull.setNegated(true);
					return isNull;
				}
				Not not = EXPR.createNot();
				not.setOperand(operand);
				return not;
			}
			case "oclIsUndefined" -> {
				IsNull isNull = EXPR.createIsNull();
				isNull.setSource(map(call.getOwnedSource()));
				return isNull;
			}
			case "includes" -> {
				if (!(call.getOwnedSource() instanceof CollectionLiteralExp collection)) {
					throw refuse("includes over a non-literal collection");
				}
				In in = EXPR.createIn();
				in.setSource(map(argument(call, 0)));
				for (org.eclipse.fennec.m2x.model.ocl.CollectionLiteralPart part : collection.getOwnedParts()) {
					if (!(part instanceof CollectionItem item)) {
						throw refuse("collection ranges in includes");
					}
					in.getValues().add(map(item.getOwnedItem()));
				}
				return in;
			}
			case "contains", "startsWith", "endsWith", "like" -> {
				StringMatch match = EXPR.createStringMatch();
				match.setKind(switch (name) {
				case "contains" -> StringMatchKind.CONTAINS;
				case "startsWith" -> StringMatchKind.STARTS_WITH;
				case "endsWith" -> StringMatchKind.ENDS_WITH;
				default -> StringMatchKind.LIKE;
				});
				OclExpression source = call.getOwnedSource();
				OclExpression pattern = argument(call, 0);
				if (isToLower(source) && isToLower(pattern)) {
					match.setCaseInsensitive(true);
					source = ((OperationCallExp) source).getOwnedSource();
					pattern = ((OperationCallExp) pattern).getOwnedSource();
				}
				match.setSource(map(source));
				match.setPattern(map(pattern));
				return match;
			}
			case "toLowerCase", "toUpperCase", "trim", "size" -> {
				StringFunction function = EXPR.createStringFunction();
				function.setKind(switch (name) {
				case "toLowerCase" -> StringFunctionKind.TO_LOWER;
				case "toUpperCase" -> StringFunctionKind.TO_UPPER;
				case "trim" -> StringFunctionKind.TRIM;
				default -> StringFunctionKind.LENGTH;
				});
				function.setSource(map(call.getOwnedSource()));
				return function;
			}
			case "+", "-", "*", "/", "mod" -> {
				if (call.getOwnedArguments().isEmpty()) {
					if (!"-".equals(name)) {
						throw refuse("operation '" + name + "' without arguments");
					}
					Negate negate = EXPR.createNegate();
					negate.setOperand(map(call.getOwnedSource()));
					return negate;
				}
				Arithmetic arithmetic = EXPR.createArithmetic();
				arithmetic.setOperator(switch (name) {
				case "+" -> ArithmeticOperator.ADD;
				case "-" -> ArithmeticOperator.SUB;
				case "*" -> ArithmeticOperator.MUL;
				case "/" -> ArithmeticOperator.DIV;
				default -> ArithmeticOperator.MOD;
				});
				arithmetic.setLeft(map(call.getOwnedSource()));
				arithmetic.setRight(map(argument(call, 0)));
				return arithmetic;
			}
			default -> throw refuse("operation '" + name + "'");
			}
		}

		private Expression iterator(IteratorExp iterator) throws QueryException {
			boolean exists = "exists".equals(iterator.getName());
			if (!exists && !"forAll".equals(iterator.getName())) {
				throw refuse("iterator '" + iterator.getName() + "'");
			}
			if (iterator.getOwnedIterators().size() != 1) {
				throw refuse("iterators with " + iterator.getOwnedIterators().size() + " variables");
			}
			Expression source = map(iterator.getOwnedSource());
			if (!(source instanceof PropertyPath sourcePath)) {
				throw refuse("iterator sources other than property paths");
			}
			Quantifier quantifier = exists ? EXPR.createExists() : EXPR.createForAll();
			quantifier.setSource(sourcePath);
			Variable variable = EXPR.createVariable();
			variable.setName(iterator.getOwnedIterators().get(0).getName());
			quantifier.setVariable(variable);
			variables.put(iterator.getOwnedIterators().get(0), variable);
			quantifier.setPredicate(map(iterator.getOwnedBody()));
			variables.remove(iterator.getOwnedIterators().get(0));
			return quantifier;
		}

		private Expression path(PropertyCallExp property) throws QueryException {
			// walk the source chain to the front, collecting segments in order
			java.util.Deque<PropertyCallExp> chain = new java.util.ArrayDeque<>();
			OclExpression current = property;
			while (current instanceof PropertyCallExp link) {
				chain.push(link);
				current = link.getOwnedSource();
			}
			PropertyPath path = EXPR.createPropertyPath();
			if (current instanceof VariableExp variableExp) {
				path.setBase(scoped(variableExp));
			} else if (current != null) {
				throw refuse("property chains rooted in " + current.eClass().getName());
			}
			while (!chain.isEmpty()) {
				PropertyCallExp link = chain.pop();
				if (link.getReferredProperty() == null) {
					throw refuse("property calls without a resolved property");
				}
				path.getSegments().add(link.getReferredProperty());
			}
			return path;
		}

		private Variable scoped(VariableExp variableExp) throws QueryException {
			Variable variable = variables.get(variableExp.getReferredVariable());
			if (variable == null) {
				throw refuse("references to the unbound variable '"
						+ (variableExp.getReferredVariable() == null ? "?"
								: variableExp.getReferredVariable().getName())
						+ "'");
			}
			return variable;
		}

		private static void addFlattened(Junction junction, Expression operand) {
			if (operand.getClass() == junction.getClass() || (operand instanceof Junction nested
					&& nested.eClass() == junction.eClass())) {
				junction.getOperands().addAll(((Junction) operand).getOperands());
			} else {
				junction.getOperands().add(operand);
			}
		}

		private static boolean isToLower(OclExpression expression) {
			return expression instanceof OperationCallExp call && "toLowerCase".equals(call.getName());
		}

		private static OclExpression argument(OperationCallExp call, int index) throws QueryException {
			if (call.getOwnedArguments().size() <= index) {
				throw refuse("operation '" + call.getName() + "' with " + call.getOwnedArguments().size()
						+ " arguments");
			}
			return call.getOwnedArguments().get(index);
		}

		private static QueryException refuse(String what) {
			return new QueryException(
					"The expression bridge does not support " + what + " — outside the blessed subset");
		}
	}
}
