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

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
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
import org.eclipse.fennec.m2x.model.ocl.TypeExp;
import org.eclipse.fennec.m2x.model.ocl.VariableExp;
import org.eclipse.fennec.model.expression.Arithmetic;
import org.eclipse.fennec.model.expression.ArithmeticOperator;
import org.eclipse.fennec.model.expression.CollectionCount;
import org.eclipse.fennec.model.expression.Comparison;
import org.eclipse.fennec.model.expression.ComparisonOperator;
import org.eclipse.fennec.model.expression.Concat;
import org.eclipse.fennec.model.expression.DurationLiteral;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.ExpressionFactory;
import org.eclipse.fennec.model.expression.In;
import org.eclipse.fennec.model.expression.IndexOf;
import org.eclipse.fennec.model.expression.IsNull;
import org.eclipse.fennec.model.expression.Junction;
import org.eclipse.fennec.model.expression.Negate;
import org.eclipse.fennec.model.expression.Not;
import org.eclipse.fennec.model.expression.NumericFunction;
import org.eclipse.fennec.model.expression.NumericFunctionKind;
import org.eclipse.fennec.model.expression.GeoBox;
import org.eclipse.fennec.model.expression.GeoDistance;
import org.eclipse.fennec.model.expression.GeoPointLiteral;
import org.eclipse.fennec.model.expression.GeoPolygon;
import org.eclipse.fennec.model.expression.GeoShape;
import org.eclipse.fennec.model.expression.GeoSubject;
import org.eclipse.fennec.model.expression.GeoWithin;
import org.eclipse.fennec.model.expression.GuidLiteral;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.model.expression.Quantifier;
import org.eclipse.fennec.model.expression.StringFunction;
import org.eclipse.fennec.model.expression.StringFunctionKind;
import org.eclipse.fennec.model.expression.StringLiteral;
import org.eclipse.fennec.model.expression.StringMatch;
import org.eclipse.fennec.model.expression.StringMatchKind;
import org.eclipse.fennec.model.expression.Substring;
import org.eclipse.fennec.model.expression.TemporalFunction;
import org.eclipse.fennec.model.expression.TemporalFunctionKind;
import org.eclipse.fennec.model.expression.TemporalKind;
import org.eclipse.fennec.model.expression.TemporalLiteral;
import org.eclipse.fennec.model.expression.TypeCheck;
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
 * {@code toLowerCase/toUpperCase/trim/size} (→ StringFunction; {@code toLower/toUpper}
 * are accepted as the evaluator-dialect aliases — issue #92),
 * {@code concat/indexOf/substring} (→ Concat/IndexOf/Substring — OData-flavoured
 * 0-based semantics, binary concat chains flatten into the n-ary Concat),
 * {@code round/floor/ceiling} (→ NumericFunction; round is half away from zero),
 * {@code year/month/day/hour/minute/second} (→ TemporalFunction; UTC-normative),
 * {@code oclIsKindOf} (→ TypeCheck) and property chains rooted in {@code oclAsType}
 * on the implicit self (→ PropertyPath.castBase — issue #80),
 * {@code + - * / mod} (→ Arithmetic; a source-only {@code -} → Negate; the integer
 * division {@code div} stays refused — truncation is deliberately not modelled),
 * {@code exists/forAll} iterators (→ quantifiers), property-call chains
 * (→ PropertyPath) and the literal set — a string literal whose OCL type names an OData
 * primitive ({@code Date}, {@code DateTimeOffset}, {@code TimeOfDay}, {@code DateTime},
 * {@code Guid}, {@code Duration}) restores the typed literal it stands for (issue #263).
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
				return stringLiteral(string);
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
			case "geoWithin" -> {
				// the dialect form of issue #232: the last argument is the shape, everything
				// before it is the coordinate binding
				GeoWithin within = EXPR.createGeoWithin();
				within.setSubject(geoSubject(call, call.getOwnedArguments().size() - 1));
				within.setShape(geoShape(argument(call, call.getOwnedArguments().size() - 1)));
				return within;
			}
			case "geoDistance" -> {
				GeoDistance distance = EXPR.createGeoDistance();
				distance.setSubject(geoSubject(call, call.getOwnedArguments().size() - 1));
				distance.setPoint(geoPointOf(argument(call, call.getOwnedArguments().size() - 1)));
				return distance;
			}
			case "geoBox", "geoPolygon", "geoPoint" -> throw new QueryException("'" + name
					+ "' is a shape argument, not an expression — it is only meaningful inside"
					+ " geoWithin/geoDistance");
			case "toLowerCase", "toLower", "toUpperCase", "toUpper", "trim" -> {
				// toLower/toUpper are the OData evaluator dialect for the same
				// operations (issue #92)
				StringFunction function = EXPR.createStringFunction();
				function.setKind(switch (name) {
				case "toLowerCase", "toLower" -> StringFunctionKind.TO_LOWER;
				case "toUpperCase", "toUpper" -> StringFunctionKind.TO_UPPER;
				default -> StringFunctionKind.TRIM;
				});
				function.setSource(map(call.getOwnedSource()));
				return function;
			}
			case "size" -> {
				// AST-shape disambiguation (like the OData translator): size over a
				// select iterator or a many-valued navigation is a CollectionCount
				// (issue #81), anything else is the string LENGTH
				OclExpression source = call.getOwnedSource();
				if (source instanceof IteratorExp select && "select".equals(select.getName())) {
					if (select.getOwnedIterators().size() != 1) {
						throw refuse("select iterators with " + select.getOwnedIterators().size()
								+ " variables");
					}
					if (!(map(select.getOwnedSource()) instanceof PropertyPath collection)) {
						throw refuse("filtered counts over sources other than property paths");
					}
					CollectionCount count = EXPR.createCollectionCount();
					count.setSource(collection);
					Variable variable = EXPR.createVariable();
					variable.setName(select.getOwnedIterators().get(0).getName());
					count.setVariable(variable);
					variables.put(select.getOwnedIterators().get(0), variable);
					count.setPredicate(map(select.getOwnedBody()));
					variables.remove(select.getOwnedIterators().get(0));
					return count;
				}
				if (source instanceof PropertyCallExp property && property.getReferredProperty() != null
						&& property.getReferredProperty().isMany()) {
					CollectionCount count = EXPR.createCollectionCount();
					count.setSource((PropertyPath) map(source));
					return count;
				}
				StringFunction function = EXPR.createStringFunction();
				function.setKind(StringFunctionKind.LENGTH);
				function.setSource(map(source));
				return function;
			}
			case "oclIsKindOf" -> {
				TypeCheck check = EXPR.createTypeCheck();
				check.setType(classifierArgument(call));
				OclExpression subject = call.getOwnedSource();
				if (subject != null) {
					if (!(map(subject) instanceof PropertyPath subjectPath)) {
						throw refuse("oclIsKindOf on subjects other than property paths or the implicit self");
					}
					check.setSource(subjectPath);
				}
				return check;
			}
			case "year", "month", "day", "hour", "minute", "second", "date", "time" -> {
				TemporalFunction function = EXPR.createTemporalFunction();
				function.setKind(switch (name) {
				case "year" -> TemporalFunctionKind.YEAR;
				case "month" -> TemporalFunctionKind.MONTH;
				case "date" -> TemporalFunctionKind.DATE;
				case "time" -> TemporalFunctionKind.TIME;
				case "day" -> TemporalFunctionKind.DAY;
				case "hour" -> TemporalFunctionKind.HOUR;
				case "minute" -> TemporalFunctionKind.MINUTE;
				default -> TemporalFunctionKind.SECOND;
				});
				function.setSource(map(call.getOwnedSource()));
				return function;
			}
			case "round", "floor", "ceiling" -> {
				NumericFunction function = EXPR.createNumericFunction();
				function.setKind(switch (name) {
				case "round" -> NumericFunctionKind.ROUND;
				case "floor" -> NumericFunctionKind.FLOOR;
				default -> NumericFunctionKind.CEILING;
				});
				function.setSource(map(call.getOwnedSource()));
				return function;
			}
			case "concat" -> {
				Concat concat = EXPR.createConcat();
				concatFlattened(concat, map(call.getOwnedSource()));
				concatFlattened(concat, map(argument(call, 0)));
				return concat;
			}
			case "indexOf" -> {
				IndexOf indexOf = EXPR.createIndexOf();
				indexOf.setSource(map(call.getOwnedSource()));
				indexOf.setSearch(map(argument(call, 0)));
				return indexOf;
			}
			case "substring" -> {
				Substring substring = EXPR.createSubstring();
				substring.setSource(map(call.getOwnedSource()));
				substring.setStart(map(argument(call, 0)));
				if (call.getOwnedArguments().size() > 1) {
					substring.setLength(map(call.getOwnedArguments().get(1)));
				}
				return substring;
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

		/**
		 * Reads the coordinate binding of a geo call (issue #232): the call source plus the
		 * arguments before {@code shapeIndex}. One path is the packed binding, two are the split
		 * pair in longitude-then-latitude order — the same order the shapes use, and the order
		 * {@code GeoPointLiteral} declares.
		 */
		private GeoSubject geoSubject(OperationCallExp call, int shapeIndex) throws QueryException {
			GeoSubject subject = EXPR.createGeoSubject();
			PropertyPath first = geoPath(call.getOwnedSource());
			if (shapeIndex == 0) {
				subject.setPathPoint(first);
				return subject;
			}
			if (shapeIndex == 1) {
				subject.setPathLon(first);
				subject.setPathLat(geoPath(argument(call, 0)));
				return subject;
			}
			throw new QueryException("A geo call takes either one packed point path or a"
					+ " longitude/latitude pair before its shape, but had " + (shapeIndex + 1)
					+ " path arguments");
		}

		/** A geo argument that has to be a property path — a coordinate is never computed here. */
		private PropertyPath geoPath(OclExpression expression) throws QueryException {
			if (expression instanceof PropertyCallExp property
					&& path(property) instanceof PropertyPath propertyPath) {
				return propertyPath;
			}
			throw new QueryException("A geo coordinate binding must be a property path, was "
					+ (expression == null ? "nothing" : expression.eClass().getName()));
		}

		/** {@code geoBox(swLon, swLat, neLon, neLat)} or {@code geoPolygon(lon, lat, …)}. */
		private GeoShape geoShape(OclExpression expression) throws QueryException {
			if (!(expression instanceof OperationCallExp shape)) {
				throw new QueryException("A geo shape must be a geoBox or geoPolygon call, was "
						+ (expression == null ? "nothing" : expression.eClass().getName()));
			}
			List<Double> coordinates = coordinates(shape);
			if ("geoBox".equals(shape.getName())) {
				if (coordinates.size() != 4) {
					throw new QueryException("geoBox takes exactly four coordinates"
							+ " (swLon, swLat, neLon, neLat), had " + coordinates.size());
				}
				GeoBox box = EXPR.createGeoBox();
				box.setSouthWest(point(coordinates.get(0), coordinates.get(1)));
				box.setNorthEast(point(coordinates.get(2), coordinates.get(3)));
				return box;
			}
			if ("geoPolygon".equals(shape.getName())) {
				if (coordinates.size() < 6 || coordinates.size() % 2 != 0) {
					throw new QueryException("geoPolygon takes lon/lat pairs for at least three"
							+ " points, had " + coordinates.size() + " coordinates");
				}
				GeoPolygon polygon = EXPR.createGeoPolygon();
				for (int i = 0; i < coordinates.size(); i += 2) {
					polygon.getPoints().add(point(coordinates.get(i), coordinates.get(i + 1)));
				}
				return polygon;
			}
			throw new QueryException("Unknown geo shape '" + shape.getName()
					+ "' — expected geoBox or geoPolygon");
		}

		/** {@code geoPoint(lon, lat)} — the point argument of a distance. */
		private GeoPointLiteral geoPointOf(OclExpression expression) throws QueryException {
			if (!(expression instanceof OperationCallExp call)
					|| !"geoPoint".equals(call.getName())) {
				throw new QueryException("A geo distance needs a geoPoint(lon, lat) argument, was "
						+ (expression == null ? "nothing" : expression.eClass().getName()));
			}
			List<Double> coordinates = coordinates(call);
			if (coordinates.size() != 2) {
				throw new QueryException("geoPoint takes exactly two coordinates (lon, lat), had "
						+ coordinates.size());
			}
			return point(coordinates.get(0), coordinates.get(1));
		}

		/** The source and arguments of a shape call, all of which must be numeric literals. */
		private List<Double> coordinates(OperationCallExp call) throws QueryException {
			List<Double> values = new ArrayList<>();
			values.add(coordinate(call.getOwnedSource(), call.getName()));
			for (OclExpression argument : call.getOwnedArguments()) {
				values.add(coordinate(argument, call.getName()));
			}
			return values;
		}

		private double coordinate(OclExpression expression, String function) throws QueryException {
			if (expression instanceof RealLiteralExp real) {
				return real.getRealSymbol();
			}
			if (expression instanceof IntegerLiteralExp integer) {
				// a whole degree is a legal coordinate and may arrive as an integer literal
				return integer.getIntegerSymbol();
			}
			throw new QueryException("'" + function + "' takes numeric coordinates, was "
					+ (expression == null ? "nothing" : expression.eClass().getName()));
		}

		private GeoPointLiteral point(double lon, double lat) {
			GeoPointLiteral point = EXPR.createGeoPointLiteral();
			point.setLon(lon);
			point.setLat(lat);
			return point;
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
			if (current instanceof OperationCallExp cast && "oclAsType".equals(cast.getName())) {
				// origin downcast Ns.SubType/prop (issue #80) — only on the (implicit) self
				path.setCastBase(classifierArgument(cast));
				current = cast.getOwnedSource();
			}
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

		/**
		 * A string literal may carry its primitive type by name — emf.odata's {@code $filter}
		 * builder stamps {@code Date}/{@code DateTimeOffset}/{@code TimeOfDay}/{@code Guid}/
		 * {@code Duration} on its pre-typed literals, and {@link ExprToOcl} stamps the same
		 * names (plus {@code DateTime} for the local date-time) when it renders the typed
		 * literals as text. Honouring the name restores the literal the text form stands for;
		 * dropping it would compare a string against a date and silently match nothing
		 * (issue #263). An untyped or unknown-typed string stays a plain string.
		 */
		private Expression stringLiteral(StringLiteralExp string) {
			String typeName = string.getType() == null ? "" : string.getType().getName();
			switch (typeName == null ? "" : typeName) {
			case "Date" -> {
				return temporal(TemporalKind.DATE, string.getStringSymbol());
			}
			case "TimeOfDay" -> {
				return temporal(TemporalKind.TIME, string.getStringSymbol());
			}
			case "DateTime" -> {
				return temporal(TemporalKind.DATE_TIME, string.getStringSymbol());
			}
			case "DateTimeOffset" -> {
				return temporal(TemporalKind.INSTANT, string.getStringSymbol());
			}
			case "Guid" -> {
				GuidLiteral literal = EXPR.createGuidLiteral();
				literal.setValue(string.getStringSymbol());
				return literal;
			}
			case "Duration" -> {
				DurationLiteral literal = EXPR.createDurationLiteral();
				literal.setIso8601(string.getStringSymbol());
				return literal;
			}
			default -> {
				StringLiteral literal = EXPR.createStringLiteral();
				literal.setValue(string.getStringSymbol());
				return literal;
			}
			}
		}

		private static TemporalLiteral temporal(TemporalKind kind, String value) {
			TemporalLiteral literal = EXPR.createTemporalLiteral();
			literal.setKind(kind);
			literal.setValue(value);
			return literal;
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

		private static void concatFlattened(Concat concat, Expression part) {
			if (part instanceof Concat nested) {
				concat.getParts().addAll(nested.getParts());
			} else {
				concat.getParts().add(part);
			}
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
			return expression instanceof OperationCallExp call
					&& ("toLowerCase".equals(call.getName()) || "toLower".equals(call.getName()));
		}

		/** Unwraps the type argument: TypeExp → EClass (issue #80). */
		private static EClass classifierArgument(OperationCallExp call) throws QueryException {
			if (argument(call, 0) instanceof TypeExp typeExp
					&& typeExp.getReferredType() instanceof EClass eClass) {
				return eClass;
			}
			throw refuse("operation '" + call.getName() + "' without an EClass type argument");
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
