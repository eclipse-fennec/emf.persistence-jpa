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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.m2x.model.ocl.BooleanLiteralExp;
import org.eclipse.fennec.m2x.model.ocl.IntegerLiteralExp;
import org.eclipse.fennec.m2x.model.ocl.IteratorExp;
import org.eclipse.fennec.m2x.model.ocl.OclExpression;
import org.eclipse.fennec.m2x.model.ocl.OclFactory;
import org.eclipse.fennec.m2x.model.ocl.OperationCallExp;
import org.eclipse.fennec.m2x.model.ocl.PrimitiveType;
import org.eclipse.fennec.m2x.model.ocl.PropertyCallExp;
import org.eclipse.fennec.m2x.model.ocl.RealLiteralExp;
import org.eclipse.fennec.m2x.model.ocl.StringLiteralExp;
import org.eclipse.fennec.m2x.model.ocl.TypeExp;
import org.eclipse.fennec.m2x.model.ocl.VariableExp;
import org.eclipse.fennec.model.expression.And;
import org.eclipse.fennec.model.expression.Arithmetic;
import org.eclipse.fennec.model.expression.Between;
import org.eclipse.fennec.model.expression.BooleanLiteral;
import org.eclipse.fennec.model.expression.AliasRef;
import org.eclipse.fennec.model.expression.CollectionCount;
import org.eclipse.fennec.model.expression.GeoBox;
import org.eclipse.fennec.model.expression.GeoDistance;
import org.eclipse.fennec.model.expression.GeoPointLiteral;
import org.eclipse.fennec.model.expression.GeoPolygon;
import org.eclipse.fennec.model.expression.GeoShape;
import org.eclipse.fennec.model.expression.GeoSubject;
import org.eclipse.fennec.model.expression.GeoWithin;
import org.eclipse.fennec.model.expression.Score;
import org.eclipse.fennec.model.expression.Comparison;
import org.eclipse.fennec.model.expression.Concat;
import org.eclipse.fennec.model.expression.DurationLiteral;
import org.eclipse.fennec.model.expression.EnumLiteral;
import org.eclipse.fennec.model.expression.Exists;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.GuidLiteral;
import org.eclipse.fennec.model.expression.In;
import org.eclipse.fennec.model.expression.IndexOf;
import org.eclipse.fennec.model.expression.IntegerLiteral;
import org.eclipse.fennec.model.expression.IntervalMatch;
import org.eclipse.fennec.model.expression.IntervalSubject;
import org.eclipse.fennec.model.expression.IsNull;
import org.eclipse.fennec.model.expression.Junction;
import org.eclipse.fennec.model.expression.Literal;
import org.eclipse.fennec.model.expression.Negate;
import org.eclipse.fennec.model.expression.Not;
import org.eclipse.fennec.model.expression.NullLiteral;
import org.eclipse.fennec.model.expression.NumericFunction;
import org.eclipse.fennec.model.expression.ParameterRef;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.model.expression.Quantifier;
import org.eclipse.fennec.model.expression.RealLiteral;
import org.eclipse.fennec.model.expression.StringFunction;
import org.eclipse.fennec.model.expression.StringLiteral;
import org.eclipse.fennec.model.expression.StringMatch;
import org.eclipse.fennec.model.expression.Substring;
import org.eclipse.fennec.model.expression.TemporalFunction;
import org.eclipse.fennec.model.expression.TemporalLiteral;
import org.eclipse.fennec.model.expression.TypeCheck;
import org.eclipse.fennec.model.expression.Variable;
import org.eclipse.fennec.persistence.query.QueryException;

/**
 * Maps Fennec expressions onto the m2x Essential-OCL AST — the <b>total</b> direction
 * of the bridge (decision R6): every blessed construct has an OCL form.
 * <p>
 * Mapping notes:
 * <ul>
 * <li>Operation calls are name-based ({@code =}, {@code <>}, {@code and}, {@code or},
 * {@code not}, {@code oclIsUndefined}, {@code includes}), matching the construction
 * style of the OData $filter builder. N-ary junctions nest as left-deep binary calls.</li>
 * <li>String matching uses the extended operation names {@code contains},
 * {@code startsWith}, {@code endsWith}, {@code like}; case-insensitive matches wrap
 * both sides in {@code toLowerCase}.</li>
 * <li>Quantifiers become {@code exists}/{@code forAll} {@code IteratorExp}s.</li>
 * <li>{@link ParameterRef}s must be <b>bound before mapping</b> (OCL has no parameter
 * concept) — pass the bindings; unbound parameters are refused.</li>
 * <li>{@link EnumLiteral}s resolve against the comparison target's {@code EEnum} where
 * derivable, otherwise they fall back to their literal name as a string.
 * {@link TemporalLiteral}s, {@link GuidLiteral}s and {@link DurationLiteral}s map to
 * their canonical text (OCL has no such literals), typed with their OData primitive-type
 * name so {@link OclToExpr} restores the literal on the way back (issue #263).</li>
 * </ul>
 *
 * @author Mark Hoffmann
 * @since 24.07.2026
 */
public final class ExprToOcl {

	private static final OclFactory OCL = OclFactory.eINSTANCE;

	private ExprToOcl() {
	}

	/**
	 * Maps an expression tree to OCL without parameter bindings.
	 *
	 * @param expression the expression to map
	 * @return the OCL expression
	 * @throws QueryException if the tree contains unbound parameters
	 */
	public static OclExpression toOcl(Expression expression) throws QueryException {
		return toOcl(expression, Map.of());
	}

	/**
	 * Maps an expression tree to OCL, inlining bound parameter values.
	 *
	 * @param expression the expression to map
	 * @param parameters bound parameter values; may be empty
	 * @return the OCL expression
	 * @throws QueryException if a parameter is unbound or a value cannot be represented
	 */
	public static OclExpression toOcl(Expression expression, Map<String, Object> parameters) throws QueryException {
		return new Mapper(parameters).map(expression, null);
	}

	private static final class Mapper {

		private final Map<String, Object> parameters;
		private final Map<Variable, org.eclipse.fennec.m2x.model.ocl.Variable> variables = new HashMap<>();

		private Mapper(Map<String, Object> parameters) {
			this.parameters = parameters == null ? Map.of() : parameters;
		}

		private OclExpression map(Expression expression, EStructuralFeature target) throws QueryException {
			if (expression instanceof Junction junction) {
				String operation = junction instanceof And ? "and" : "or";
				List<org.eclipse.fennec.model.expression.Expression> operands = junction.getOperands();
				OclExpression result = map(operands.get(0), null);
				for (int i = 1; i < operands.size(); i++) {
					result = call(operation, result, map(operands.get(i), null));
				}
				return result;
			}
			if (expression instanceof Not not) {
				return call("not", map(not.getOperand(), null));
			}
			if (expression instanceof Comparison comparison) {
				String operation = switch (comparison.getOperator()) {
				case EQ -> "=";
				case NE -> "<>";
				case LT -> "<";
				case LE -> "<=";
				case GT -> ">";
				case GE -> ">=";
				};
				EStructuralFeature comparisonTarget = targetOf(comparison.getLeft(), comparison.getRight());
				return call(operation, map(comparison.getLeft(), comparisonTarget),
						map(comparison.getRight(), comparisonTarget));
			}
			if (expression instanceof IsNull isNull) {
				OclExpression undefined = call("oclIsUndefined", map(isNull.getSource(), null));
				return isNull.isNegated() ? call("not", undefined) : undefined;
			}
			if (expression instanceof Between between) {
				EStructuralFeature target2 = targetOf(between.getSource(), null);
				OclExpression lower = call(between.isLowerIncluded() ? ">=" : ">",
						map(between.getSource(), target2), map(between.getLower(), target2));
				OclExpression upper = call(between.isUpperIncluded() ? "<=" : "<",
						map(between.getSource(), target2), map(between.getUpper(), target2));
				return call("and", lower, upper);
			}
			if (expression instanceof In in) {
				EStructuralFeature target2 = targetOf(in.getSource(), null);
				org.eclipse.fennec.m2x.model.ocl.CollectionLiteralExp collection = OCL.createCollectionLiteralExp();
				for (Expression candidate : in.getValues()) {
					org.eclipse.fennec.m2x.model.ocl.CollectionItem item = OCL.createCollectionItem();
					item.setOwnedItem(map(candidate, target2));
					collection.getOwnedParts().add(item);
				}
				return call("includes", collection, map(in.getSource(), target2));
			}
			if (expression instanceof StringMatch match) {
				String operation = switch (match.getKind()) {
				case CONTAINS -> "contains";
				case STARTS_WITH -> "startsWith";
				case ENDS_WITH -> "endsWith";
				case LIKE -> "like";
				case FUZZY -> throw new QueryException("FUZZY string matching has no OCL form"
						+ " — edit distance is not part of the OCL string library (issue #167)");
				};
				OclExpression source = map(match.getSource(), null);
				OclExpression pattern = map(match.getPattern(), null);
				if (match.isCaseInsensitive()) {
					source = call("toLowerCase", source);
					pattern = call("toLowerCase", pattern);
				}
				return call(operation, source, pattern);
			}
			if (expression instanceof Quantifier quantifier) {
				IteratorExp iterator = OCL.createIteratorExp();
				iterator.setName(quantifier instanceof Exists ? "exists" : "forAll");
				iterator.setOwnedSource(map(quantifier.getSource(), null));
				org.eclipse.fennec.m2x.model.ocl.Variable variable = OCL.createVariable();
				variable.setName(quantifier.getVariable().getName());
				iterator.getOwnedIterators().add(variable);
				variables.put(quantifier.getVariable(), variable);
				iterator.setOwnedBody(map(quantifier.getPredicate(), null));
				variables.remove(quantifier.getVariable());
				return iterator;
			}
			if (expression instanceof StringFunction function) {
				String operation = switch (function.getKind()) {
				case TO_LOWER -> "toLowerCase";
				case TO_UPPER -> "toUpperCase";
				case TRIM -> "trim";
				case LENGTH -> "size";
				};
				return call(operation, map(function.getSource(), null));
			}
			if (expression instanceof Arithmetic arithmetic) {
				String operation = switch (arithmetic.getOperator()) {
				case ADD -> "+";
				case SUB -> "-";
				case MUL -> "*";
				case DIV -> "/";
				case MOD -> "mod";
				};
				return call(operation, map(arithmetic.getLeft(), null), map(arithmetic.getRight(), null));
			}
			if (expression instanceof Negate negate) {
				// source-only '-' is the OCL unary minus — OclToExpr recognizes it back
				return call("-", map(negate.getOperand(), null));
			}
			if (expression instanceof TemporalFunction temporalFunction) {
				String operation = switch (temporalFunction.getKind()) {
				case YEAR -> "year";
				case MONTH -> "month";
				case DAY -> "day";
				case HOUR -> "hour";
				case MINUTE -> "minute";
				case SECOND -> "second";
				// OData spells these exactly so, and the bridge already reads named functions
				// as vocabulary (issue #240)
				case DATE -> "date";
				case TIME -> "time";
				};
				return call(operation, map(temporalFunction.getSource(), null));
			}
			if (expression instanceof NumericFunction numericFunction) {
				String operation = switch (numericFunction.getKind()) {
				case ROUND -> "round";
				case FLOOR -> "floor";
				case CEILING -> "ceiling";
				};
				return call(operation, map(numericFunction.getSource(), null));
			}
			if (expression instanceof Concat concatenation) {
				// the n-ary Concat unrolls into a left-deep binary chain; the partial
				// direction flattens it back
				List<Expression> parts = concatenation.getParts();
				OclExpression result = map(parts.get(0), null);
				for (int i = 1; i < parts.size(); i++) {
					result = call("concat", result, map(parts.get(i), null));
				}
				return result;
			}
			if (expression instanceof IndexOf indexOf) {
				return call("indexOf", map(indexOf.getSource(), null), map(indexOf.getSearch(), null));
			}
			if (expression instanceof Substring substring) {
				if (substring.getLength() != null) {
					return call("substring", map(substring.getSource(), null),
							map(substring.getStart(), null), map(substring.getLength(), null));
				}
				return call("substring", map(substring.getSource(), null), map(substring.getStart(), null));
			}
			if (expression instanceof AliasRef aliasRef) {
				// the documented totality exception (issue #82): pipeline alias references
				// address query-envelope output columns — OCL has no pipeline concept
				throw new QueryException("Alias reference '" + aliasRef.getAlias()
						+ "' has no OCL form — the bridge covers predicate expressions, not pipeline stages");
			}
			if (expression instanceof Score) {
				// the second totality exception (issue #100): the relevance score is an
				// execution-time backend value — OCL has no ranking concept
				throw new QueryException("Score has no OCL form — relevance is a ranking-backend"
						+ " execution value, not a model expression");
			}
			if (expression instanceof IntervalMatch interval) {
				return interval(interval);
			}
			if (expression instanceof GeoWithin within) {
				return geoWithin(within);
			}
			if (expression instanceof GeoDistance distance) {
				return geoDistance(distance);
			}
			if (expression instanceof CollectionCount count) {
				// plain: path->size(); filtered: path->select(v | pred)->size() (issue #81)
				OclExpression source = propertyChain(count.getSource());
				if (count.getPredicate() == null) {
					return call("size", source);
				}
				IteratorExp select = OCL.createIteratorExp();
				select.setName("select");
				select.setOwnedSource(source);
				org.eclipse.fennec.m2x.model.ocl.Variable iterator = OCL.createVariable();
				iterator.setName(count.getVariable().getName());
				select.getOwnedIterators().add(iterator);
				variables.put(count.getVariable(), iterator);
				select.setOwnedBody(map(count.getPredicate(), null));
				variables.remove(count.getVariable());
				return call("size", select);
			}
			if (expression instanceof TypeCheck typeCheck) {
				// kind-of test — source-less form tests the implicit self (issue #80)
				OclExpression subject = typeCheck.getSource() == null ? null
						: propertyChain(typeCheck.getSource());
				return call("oclIsKindOf", subject, typeExp(typeCheck.getType()));
			}
			if (expression instanceof PropertyPath path) {
				return propertyChain(path);
			}
			if (expression instanceof ParameterRef parameter) {
				if (!parameters.containsKey(parameter.getName())) {
					throw new QueryException("Unbound query parameter '" + parameter.getName()
							+ "' — OCL has no parameter concept, bind it before mapping");
				}
				return boxValue(parameters.get(parameter.getName()));
			}
			if (expression instanceof Literal literal) {
				return literal(literal, target);
			}
			if (expression instanceof org.eclipse.fennec.model.expression.VariableRef ref) {
				VariableExp variableExp = OCL.createVariableExp();
				variableExp.setReferredVariable(scoped(ref.getVariable()));
				return variableExp;
			}
			throw new QueryException("Unsupported expression " + expression.eClass().getName());
		}

		private OclExpression propertyChain(PropertyPath path) throws QueryException {
			OclExpression source = null;
			if (path.getBase() != null) {
				VariableExp variableExp = OCL.createVariableExp();
				variableExp.setReferredVariable(scoped(path.getBase()));
				source = variableExp;
			}
			if (path.getCastBase() != null) {
				// the origin downcast is the OCL oclAsType on the (implicit) self (issue #80)
				source = call("oclAsType", source, typeExp(path.getCastBase()));
			}
			for (EStructuralFeature segment : path.getSegments()) {
				PropertyCallExp property = OCL.createPropertyCallExp();
				property.setReferredProperty(segment);
				if (source != null) {
					property.setOwnedSource(source);
				}
				source = property;
			}
			return source;
		}

		private TypeExp typeExp(EClass type) {
			TypeExp typeExp = OCL.createTypeExp();
			typeExp.setReferredType(type);
			return typeExp;
		}

		private org.eclipse.fennec.m2x.model.ocl.Variable scoped(Variable variable) throws QueryException {
			org.eclipse.fennec.m2x.model.ocl.Variable scoped = variables.get(variable);
			if (scoped == null) {
				throw new QueryException("Variable '" + variable.getName() + "' is not in scope");
			}
			return scoped;
		}

		private OclExpression literal(Literal literal, EStructuralFeature target) throws QueryException {
			if (literal instanceof NullLiteral) {
				return OCL.createNullLiteralExp();
			}
			if (literal instanceof StringLiteral string) {
				return stringLiteral(string.getValue());
			}
			if (literal instanceof IntegerLiteral integer) {
				IntegerLiteralExp exp = OCL.createIntegerLiteralExp();
				exp.setIntegerSymbol(integer.getValue());
				return exp;
			}
			if (literal instanceof RealLiteral real) {
				RealLiteralExp exp = OCL.createRealLiteralExp();
				exp.setRealSymbol(real.getValue());
				return exp;
			}
			if (literal instanceof BooleanLiteral bool) {
				BooleanLiteralExp exp = OCL.createBooleanLiteralExp();
				exp.setBooleanSymbol(bool.isValue());
				return exp;
			}
			if (literal instanceof EnumLiteral enumLiteral) {
				if (target != null && target.getEType() instanceof EEnum eEnum) {
					EEnumLiteral resolved = eEnum.getEEnumLiteral(enumLiteral.getLiteralName());
					if (resolved != null) {
						org.eclipse.fennec.m2x.model.ocl.EnumLiteralExp exp = OCL.createEnumLiteralExp();
						exp.setReferredLiteral(resolved);
						return exp;
					}
				}
				return stringLiteral(enumLiteral.getLiteralName());
			}
			if (literal instanceof TemporalLiteral temporal) {
				// OCL has no temporal literals — the ISO text, typed so OclToExpr can
				// restore the literal instead of guessing a plain string (issue #263)
				return typedString(temporal.getValue(), switch (temporal.getKind()) {
				case DATE -> "Date";
				case TIME -> "TimeOfDay";
				case DATE_TIME -> "DateTime";
				case INSTANT -> "DateTimeOffset";
				});
			}
			if (literal instanceof GuidLiteral guid) {
				// no OCL guid literal — the canonical text form (issue #83), typed (issue #263)
				return typedString(guid.getValue(), "Guid");
			}
			if (literal instanceof DurationLiteral duration) {
				// no OCL duration literal — the ISO-8601 text form (issue #83), typed (issue #263)
				return typedString(duration.getIso8601(), "Duration");
			}
			throw new QueryException("Unsupported literal " + literal.eClass().getName());
		}

		private OclExpression boxValue(Object value) throws QueryException {
			if (value == null) {
				return OCL.createNullLiteralExp();
			}
			if (value instanceof String string) {
				return stringLiteral(string);
			}
			if (value instanceof Integer || value instanceof Long || value instanceof Short
					|| value instanceof Byte) {
				IntegerLiteralExp exp = OCL.createIntegerLiteralExp();
				exp.setIntegerSymbol(((Number) value).longValue());
				return exp;
			}
			if (value instanceof Double || value instanceof Float) {
				RealLiteralExp exp = OCL.createRealLiteralExp();
				exp.setRealSymbol(((Number) value).doubleValue());
				return exp;
			}
			if (value instanceof Boolean bool) {
				BooleanLiteralExp exp = OCL.createBooleanLiteralExp();
				exp.setBooleanSymbol(bool);
				return exp;
			}
			throw new QueryException("Cannot represent bound value of type " + value.getClass().getName()
					+ " as an OCL literal");
		}

		private StringLiteralExp stringLiteral(String value) {
			StringLiteralExp exp = OCL.createStringLiteralExp();
			exp.setStringSymbol(value);
			return exp;
		}

		/**
		 * A string literal carrying its primitive type by name — the same shape emf.odata's
		 * {@code $filter} builder emits for its pre-typed literals, using the OData spellings
		 * ({@code Date}, {@code TimeOfDay}, {@code DateTimeOffset}, {@code Guid},
		 * {@code Duration}; {@code DateTime} is the bridge's own name for the local
		 * date-time OData does not have). The type reference is what keeps the round trip
		 * lossless (issue #263).
		 */
		private StringLiteralExp typedString(String value, String typeName) {
			StringLiteralExp exp = stringLiteral(value);
			PrimitiveType type = OCL.createPrimitiveType();
			type.setName(typeName);
			exp.setType(type);
			return exp;
		}

		/**
		 * Interval predicates (issue #215) keep the bridge total: their meaning <em>is</em> the
		 * conjunction of bound comparisons (concept §A.5.1), so there is a faithful OCL form —
		 * unlike geo, which has no OCL vocabulary at all. The rendering mirrors the reference
		 * engine down to the empty-subject guard and the unbounded disjunctions, which is what
		 * makes the OclEvaluator usable as a third opinion on these predicates.
		 */
		/**
		 * {@code geoWithin(lonPath, [latPath,] shape)} — the dialect form of the geo vocabulary
		 * (issue #232, lifting the third totality exception of issue #101).
		 * <p>
		 * OCL defines no geo operators, but the bridge has never been limited to operators OCL
		 * defines: {@code toLower}/{@code toUpper} are read as an evaluator dialect for named
		 * functions, and {@code IntervalMatch} (issue #215) got a form because it decomposes
		 * into things OCL can spell. Geo is the same case — the arguments are property paths and
		 * numbers, all of which OCL has. What it is <em>not</em> is the case of {@code AliasRef}
		 * (#82) and {@code Score} (#100): those have no model-expression meaning at all, while a
		 * geo predicate is an ordinary predicate over stored coordinates.
		 * <p>
		 * The binding is told apart by arity rather than by a second function name: a split
		 * subject contributes two path arguments, a packed one contributes a single path.
		 * <p>
		 * <b>Coordinate order is longitude first, everywhere</b> — in the subject arguments and
		 * inside every shape — matching {@code GeoPointLiteral}'s GeoJSON order. One rule for
		 * the whole vocabulary is worth more than agreeing with the order the model happens to
		 * declare its features in.
		 */
		private OclExpression geoWithin(GeoWithin within) throws QueryException {
			List<OclExpression> arguments = new ArrayList<>();
			OclExpression source = geoSubject(within.getSubject(), arguments);
			arguments.add(geoShape(within.getShape()));
			return call("geoWithin", source, arguments.toArray(OclExpression[]::new));
		}

		/** {@code geoDistance(lonPath, [latPath,] geoPoint(lon, lat))} — the value form (issue #232). */
		private OclExpression geoDistance(GeoDistance distance) throws QueryException {
			List<OclExpression> arguments = new ArrayList<>();
			OclExpression source = geoSubject(distance.getSubject(), arguments);
			arguments.add(geoPoint(distance.getPoint()));
			return call("geoDistance", source, arguments.toArray(OclExpression[]::new));
		}

		/**
		 * Renders the coordinate binding, returning the call source and appending any further
		 * path argument: longitude then latitude for the split pair, one path for the packed
		 * form (decision G1).
		 */
		private OclExpression geoSubject(GeoSubject subject, List<OclExpression> arguments)
				throws QueryException {
			if (subject.getPathPoint() != null) {
				return propertyChain(subject.getPathPoint());
			}
			if (subject.getPathLon() == null || subject.getPathLat() == null) {
				throw new QueryException("GeoSubject has neither a packed point path nor a"
						+ " complete lat/lon pair — exactly one binding must be present");
			}
			OclExpression longitude = propertyChain(subject.getPathLon());
			arguments.add(propertyChain(subject.getPathLat()));
			return longitude;
		}

		/** {@code geoBox(swLon, swLat, neLon, neLat)} / {@code geoPolygon(lon, lat, lon, lat, …)}. */
		private OclExpression geoShape(GeoShape shape) throws QueryException {
			if (shape instanceof GeoBox box) {
				return call("geoBox", real(box.getSouthWest().getLon()),
						real(box.getSouthWest().getLat()),
						real(box.getNorthEast().getLon()),
						real(box.getNorthEast().getLat()));
			}
			if (shape instanceof GeoPolygon polygon) {
				List<OclExpression> coordinates = new ArrayList<>();
				for (GeoPointLiteral point : polygon.getPoints()) {
					coordinates.add(real(point.getLon()));
					coordinates.add(real(point.getLat()));
				}
				return call("geoPolygon", coordinates.get(0),
						coordinates.subList(1, coordinates.size()).toArray(OclExpression[]::new));
			}
			throw new QueryException("Unsupported geo shape " + shape.eClass().getName());
		}

		/** {@code geoPoint(lon, lat)} — the point argument of a distance. */
		private OclExpression geoPoint(GeoPointLiteral point) {
			return call("geoPoint", real(point.getLon()), real(point.getLat()));
		}

		/** A coordinate as an OCL real literal; degrees are never integers by convention. */
		private OclExpression real(double value) {
			RealLiteralExp exp = OCL.createRealLiteralExp();
			exp.setRealSymbol(value);
			return exp;
		}

		private OclExpression interval(IntervalMatch interval) throws QueryException {
			IntervalSubject subject = interval.getSubject();
			EStructuralFeature target = targetOf(subject.getPathLower(), null);
			OclExpression lowerBound = map(subject.getPathLower(), null);
			OclExpression upperBound = map(subject.getPathUpper(), null);
			OclExpression queryLower = map(interval.getLower(), target);
			OclExpression queryUpper = map(interval.getUpper(), target);
			boolean subjectLowerIncluded = subject.isLowerIncluded();
			boolean subjectUpperIncluded = subject.isUpperIncluded();
			boolean queryLowerIncluded = interval.isLowerIncluded();
			boolean queryUpperIncluded = interval.isUpperIncluded();
			boolean unbounded = subject.isNullMeansUnbounded();

			OclExpression nonEmpty = call(subjectLowerIncluded && subjectUpperIncluded ? "<=" : "<",
					map(subject.getPathLower(), null), map(subject.getPathUpper(), null));
			if (unbounded) {
				nonEmpty = call("or", call("or", undefined(subject.getPathLower()),
						undefined(subject.getPathUpper())), nonEmpty);
			}

			OclExpression first;
			OclExpression second;
			switch (interval.getRelation()) {
			case INTERSECTS -> {
				first = bound(lowerBound, subjectLowerIncluded && queryUpperIncluded ? "<=" : "<",
						queryUpper, subject.getPathLower(), unbounded, true);
				second = bound(upperBound, subjectUpperIncluded && queryLowerIncluded ? ">=" : ">",
						queryLower, subject.getPathUpper(), unbounded, true);
			}
			case WITHIN -> {
				first = bound(lowerBound, queryLowerIncluded || !subjectLowerIncluded ? ">=" : ">",
						queryLower, subject.getPathLower(), unbounded, false);
				second = bound(upperBound, queryUpperIncluded || !subjectUpperIncluded ? "<=" : "<",
						queryUpper, subject.getPathUpper(), unbounded, false);
			}
			default -> {
				first = bound(lowerBound, subjectLowerIncluded || !queryLowerIncluded ? "<=" : "<",
						queryLower, subject.getPathLower(), unbounded, true);
				second = bound(upperBound, subjectUpperIncluded || !queryUpperIncluded ? ">=" : ">",
						queryUpper, subject.getPathUpper(), unbounded, true);
			}
			}
			return call("and", call("and", nonEmpty, first), second);
		}

		/** One bound comparison, wrapped in the declared infinity where the subject asks for it. */
		private OclExpression bound(OclExpression boundValue, String operator, OclExpression limit,
				PropertyPath boundPath, boolean unbounded,
				boolean absentSatisfies) throws QueryException {
			OclExpression comparison = call(operator, boundValue, limit);
			if (!unbounded) {
				return comparison;
			}
			return absentSatisfies
					? call("or", undefined(boundPath), comparison)
					: call("and", call("not", undefined(boundPath)), comparison);
		}

		private OclExpression undefined(PropertyPath path)
				throws QueryException {
			return call("oclIsUndefined", map(path, null));
		}

		private OperationCallExp call(String operation, OclExpression source, OclExpression... arguments) {
			OperationCallExp exp = OCL.createOperationCallExp();
			exp.setName(operation);
			exp.setOwnedSource(source);
			for (OclExpression argument : arguments) {
				exp.getOwnedArguments().add(argument);
			}
			return exp;
		}

		private EStructuralFeature targetOf(Expression left, Expression right) {
			if (left instanceof PropertyPath path && !path.getSegments().isEmpty()) {
				return path.getSegments().get(path.getSegments().size() - 1);
			}
			if (right instanceof PropertyPath path && !path.getSegments().isEmpty()) {
				return path.getSegments().get(path.getSegments().size() - 1);
			}
			return null;
		}
	}
}
