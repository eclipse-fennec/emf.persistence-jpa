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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.eclipse.fennec.model.query.builder.Expressions.and;
import static org.eclipse.fennec.model.query.builder.Expressions.any;
import static org.eclipse.fennec.model.query.builder.Expressions.concat;
import static org.eclipse.fennec.model.query.builder.Expressions.count;
import static org.eclipse.fennec.model.query.builder.Expressions.div;
import static org.eclipse.fennec.model.query.builder.Expressions.isOf;
import static org.eclipse.fennec.model.query.builder.Expressions.mod;
import static org.eclipse.fennec.model.query.builder.Expressions.mul;
import static org.eclipse.fennec.model.query.builder.Expressions.neg;
import static org.eclipse.fennec.model.query.builder.Expressions.or;
import static org.eclipse.fennec.model.query.builder.Expressions.param;
import static org.eclipse.fennec.model.query.builder.Expressions.path;
import static org.eclipse.fennec.model.query.builder.Expressions.pathAs;
import static org.eclipse.fennec.model.query.builder.Expressions.propertyPath;
import static org.eclipse.fennec.model.query.builder.Expressions.score;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.m2x.model.ocl.IteratorExp;
import org.eclipse.fennec.m2x.model.ocl.OclExpression;
import org.eclipse.fennec.m2x.model.ocl.OclFactory;
import org.eclipse.fennec.m2x.model.ocl.OperationCallExp;
import org.eclipse.fennec.m2x.model.ocl.PropertyCallExp;
import org.eclipse.fennec.m2x.model.ocl.StringLiteralExp;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.GeoWithin;
import org.eclipse.fennec.model.query.builder.Expressions;
import org.eclipse.fennec.persistence.query.QueryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the bidirectional expression⇄OCL bridge: Expr→OCL total, OCL→Expr partial,
 * structural round trips over the blessed subset.
 *
 * @author Mark Hoffmann
 */
class ExpressionOclBridgeTest {

	private EAttribute name;
	private EAttribute age;
	private EAttribute lat;
	private EAttribute lon;
	private EAttribute position;
	private EReference addresses;
	private EAttribute street;

	@BeforeEach
	void setUp() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		EClass person = ecore.createEClass();
		person.setName("Person");
		name = ecore.createEAttribute();
		name.setName("name");
		name.setEType(EcorePackage.Literals.ESTRING);
		age = ecore.createEAttribute();
		age.setName("age");
		age.setEType(EcorePackage.Literals.EINT);
		lat = ecore.createEAttribute();
		lat.setName("lat");
		lat.setEType(EcorePackage.Literals.EDOUBLE);
		lon = ecore.createEAttribute();
		lon.setName("lon");
		lon.setEType(EcorePackage.Literals.EDOUBLE);
		position = ecore.createEAttribute();
		position.setName("position");
		position.setEType(EcorePackage.Literals.ESTRING);
		person.getEStructuralFeatures().add(name);
		person.getEStructuralFeatures().add(age);
		person.getEStructuralFeatures().add(lat);
		person.getEStructuralFeatures().add(lon);
		person.getEStructuralFeatures().add(position);

		EClass address = ecore.createEClass();
		address.setName("Address");
		street = ecore.createEAttribute();
		street.setName("street");
		street.setEType(EcorePackage.Literals.ESTRING);
		address.getEStructuralFeatures().add(street);

		addresses = ecore.createEReference();
		addresses.setName("addresses");
		addresses.setEType(address);
		addresses.setUpperBound(-1);
		addresses.setContainment(true);
		person.getEStructuralFeatures().add(addresses);
	}

	private Expression roundTrip(Expression expression) throws QueryException {
		return OclToExpr.toExpr(ExprToOcl.toOcl(expression));
	}

	@Test
	void comparisonRoundTripsStructurally() throws QueryException {
		Expression original = and(
				or(path(age).ge(18), path(age).ne(65)),
				path(name).isNotNull(),
				path(age).in(1, 2, 3));
		Expression back = roundTrip(original);
		assertThat(EcoreUtil.equals(original, back))
				.as("expr → ocl → expr must be structurally identical")
				.isTrue();
	}

	@Test
	void stringMatchingRoundTripsWithCaseFolding() throws QueryException {
		Expression original = path(name).containsIgnoreCase("smith");
		OclExpression ocl = ExprToOcl.toOcl(original);
		// CI folds into toLowerCase on both sides in OCL
		OperationCallExp call = (OperationCallExp) ocl;
		assertThat(call.getName()).isEqualTo("contains");
		assertThat(((OperationCallExp) call.getOwnedSource()).getName()).isEqualTo("toLowerCase");

		Expression back = OclToExpr.toExpr(ocl);
		assertThat(EcoreUtil.equals(original, back)).isTrue();
	}

	@Test
	void quantifiersRoundTrip() throws QueryException {
		Expression original = any(propertyPath(addresses), a -> a.path(street).startsWith("Main"));
		OclExpression ocl = ExprToOcl.toOcl(original);
		assertThat(ocl).isInstanceOf(IteratorExp.class);
		assertThat(((IteratorExp) ocl).getName()).isEqualTo("exists");

		Expression back = OclToExpr.toExpr(ocl);
		assertThat(EcoreUtil.equals(original, back)).isTrue();
	}

	@Test
	void betweenExpandsToAndComposition() throws QueryException {
		Expression between = path(age).between(18, 65, true, false);
		OclExpression ocl = ExprToOcl.toOcl(between);
		OperationCallExp andCall = (OperationCallExp) ocl;
		assertThat(andCall.getName()).isEqualTo("and");
		// the round trip yields the equivalent and-composition, not a Between
		Expression back = OclToExpr.toExpr(ocl);
		assertThat(back).isInstanceOf(org.eclipse.fennec.model.expression.And.class);
	}

	@Test
	void parametersMustBeBoundBeforeMapping() throws QueryException {
		Expression withParameter = path(age).eq(param("minAge"));
		assertThatThrownBy(() -> ExprToOcl.toOcl(withParameter))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("minAge");

		OclExpression bound = ExprToOcl.toOcl(withParameter, Map.of("minAge", 21));
		OperationCallExp call = (OperationCallExp) bound;
		assertThat(((org.eclipse.fennec.m2x.model.ocl.IntegerLiteralExp) call.getOwnedArguments().get(0))
				.getIntegerSymbol()).isEqualTo(21L);
	}

	@Test
	void arithmeticRoundTripsStructurally() throws QueryException {
		// (age + 2) * 3 mod 7 > age / 2 — every operator once
		Expression original = mod(mul(path(age).plus(2), 3), 7).gt(div(path(age), 2));
		Expression back = roundTrip(original);
		assertThat(EcoreUtil.equals(original, back))
				.as("arithmetic expr → ocl → expr must be structurally identical")
				.isTrue();
	}

	@Test
	void negateRoundTripsAsSourceOnlyMinus() throws QueryException {
		Expression original = neg(path(age)).lt(0);
		OclExpression ocl = ExprToOcl.toOcl(original);
		OperationCallExp compare = (OperationCallExp) ocl;
		OperationCallExp minus = (OperationCallExp) compare.getOwnedSource();
		assertThat(minus.getName()).isEqualTo("-");
		assertThat(minus.getOwnedArguments()).isEmpty();

		Expression back = OclToExpr.toExpr(ocl);
		assertThat(EcoreUtil.equals(original, back)).isTrue();
	}

	@Test
	void extendedStringFunctionsRoundTripStructurally() throws QueryException {
		Expression original = and(
				path(name).substring(1, 3).eq("lic"),
				path(name).indexOf("o").ge(0));
		Expression back = roundTrip(original);
		assertThat(EcoreUtil.equals(original, back))
				.as("substring/indexOf expr → ocl → expr must be structurally identical")
				.isTrue();
	}

	@Test
	void concatUnrollsBinaryAndFlattensBack() throws QueryException {
		// three parts → left-deep binary concat chain in OCL → flattened n-ary again
		Expression original = concat(path(name), " ", path(street)).eq("x y");
		OclExpression ocl = ExprToOcl.toOcl(original);
		OperationCallExp compare = (OperationCallExp) ocl;
		OperationCallExp outer = (OperationCallExp) compare.getOwnedSource();
		assertThat(outer.getName()).isEqualTo("concat");
		assertThat(((OperationCallExp) outer.getOwnedSource()).getName()).isEqualTo("concat");

		Expression back = OclToExpr.toExpr(ocl);
		assertThat(EcoreUtil.equals(original, back)).isTrue();
	}

	@Test
	void numericFunctionsRoundTripStructurally() throws QueryException {
		Expression original = and(
				path(age).round().eq(8),
				path(age).floor().ge(7),
				path(age).ceiling().le(13));
		Expression back = roundTrip(original);
		assertThat(EcoreUtil.equals(original, back))
				.as("round/floor/ceiling expr → ocl → expr must be structurally identical")
				.isTrue();
	}

	@Test
	void temporalFunctionsRoundTripStructurally() throws QueryException {
		Expression original = and(
				path(age).year().eq(1990),
				path(age).month().le(6),
				path(age).day().ge(1),
				path(age).hour().eq(12),
				path(age).minute().eq(30),
				path(age).second().eq(0));
		Expression back = roundTrip(original);
		assertThat(EcoreUtil.equals(original, back))
				.as("year..second expr → ocl → expr must be structurally identical")
				.isTrue();
	}

	@Test
	void guidAndDurationLiteralsMapToOclStrings() throws QueryException {
		// no OCL guid/duration literals — the canonical text is the total form (issue #83)
		OperationCallExp guid = (OperationCallExp) ExprToOcl.toOcl(
				path(name).eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000")));
		assertThat(((StringLiteralExp) guid.getOwnedArguments().get(0)).getStringSymbol())
				.isEqualTo("123e4567-e89b-12d3-a456-426614174000");

		OperationCallExp duration = (OperationCallExp) ExprToOcl.toOcl(
				path(name).eq(Duration.ofMinutes(90)));
		assertThat(((StringLiteralExp) duration.getOwnedArguments().get(0)).getStringSymbol())
				.isEqualTo("PT1H30M");
	}

	@Test
	void typePredicatesRoundTripStructurally() throws QueryException {
		EClass personClass = name.getEContainingClass();
		Expression original = and(
				isOf(personClass),
				pathAs(personClass, age).gt(18));
		Expression back = roundTrip(original);
		assertThat(EcoreUtil.equals(original, back))
				.as("isOf/castBase expr → ocl → expr must be structurally identical")
				.isTrue();

		// the total direction emits oclIsKindOf / a chain rooted in oclAsType
		OperationCallExp kindOf = (OperationCallExp) ExprToOcl.toOcl(isOf(personClass));
		assertThat(kindOf.getName()).isEqualTo("oclIsKindOf");
	}

	@Test
	void collectionCountsRoundTripStructurally() throws QueryException {
		Expression plain = count(propertyPath(addresses)).ge(2);
		assertThat(EcoreUtil.equals(plain, roundTrip(plain)))
				.as("plain count expr → ocl → expr must be structurally identical")
				.isTrue();

		Expression filtered = count(propertyPath(addresses),
				a -> a.path(street).startsWith("Main")).eq(1);
		assertThat(EcoreUtil.equals(filtered, roundTrip(filtered)))
				.as("filtered count expr → ocl → expr must be structurally identical")
				.isTrue();

		// size over a string path stays the string LENGTH
		Expression length = path(name).length().gt(3);
		assertThat(EcoreUtil.equals(length, roundTrip(length))).isTrue();
	}

	@Test
	void evaluatorDialectCaseAliasesMapToStringFunctions() throws QueryException {
		// toLower/toUpper are the OData evaluator dialect for toLowerCase/toUpperCase (issue #92)
		Expression lower = OclToExpr.toExpr(
				call("=", call("toLower", property(name)), string("smith")));
		assertThat(EcoreUtil.equals(lower, path(name).toLower().eq("smith")))
				.as("toLower must map like toLowerCase")
				.isTrue();

		Expression upper = OclToExpr.toExpr(
				call("=", call("toUpper", property(name)), string("SMITH")));
		assertThat(EcoreUtil.equals(upper, path(name).toUpper().eq("SMITH")))
				.as("toUpper must map like toUpperCase")
				.isTrue();
	}

	@Test
	void evaluatorDialectToLowerPairFoldsIntoCaseInsensitiveMatch() throws QueryException {
		// the caseInsensitive fold must recognise the alias spelling on both sides…
		Expression folded = OclToExpr.toExpr(
				call("contains", call("toLower", property(name)), call("toLower", string("smith"))));
		assertThat(EcoreUtil.equals(folded, path(name).containsIgnoreCase("smith")))
				.as("a toLower pair must fold into the case-insensitive flag")
				.isTrue();

		// …and mixed spellings (one alias, one canonical) still fold
		Expression mixed = OclToExpr.toExpr(
				call("contains", call("toLower", property(name)), call("toLowerCase", string("smith"))));
		assertThat(EcoreUtil.equals(mixed, path(name).containsIgnoreCase("smith")))
				.as("mixed toLower/toLowerCase must fold into the case-insensitive flag")
				.isTrue();
	}

	private static OperationCallExp call(String name, OclExpression source, OclExpression... arguments) {
		OperationCallExp call = OclFactory.eINSTANCE.createOperationCallExp();
		call.setName(name);
		call.setOwnedSource(source);
		for (OclExpression argument : arguments) {
			call.getOwnedArguments().add(argument);
		}
		return call;
	}

	private static PropertyCallExp property(EAttribute attribute) {
		PropertyCallExp property = OclFactory.eINSTANCE.createPropertyCallExp();
		property.setReferredProperty(attribute);
		return property;
	}

	private static StringLiteralExp string(String value) {
		StringLiteralExp literal = OclFactory.eINSTANCE.createStringLiteralExp();
		literal.setStringSymbol(value);
		return literal;
	}

	@Test
	void scoreHasNoOclForm() {
		// documented totality exception (issue #100), like AliasRef for pipelines
		assertThatThrownBy(() -> ExprToOcl.toOcl(score().toExpression()))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("Score");
	}

	/**
	 * The geo vocabulary round-trips through the dialect form (issue #232, lifting the third
	 * totality exception of issue #101).
	 * <p>
	 * It was an exception because OCL defines no geo operators — but the bridge already reads
	 * named functions as vocabulary ({@code toLower}) and already gave a form to a construct
	 * without an operator ({@code IntervalMatch}, #215). Unlike {@code AliasRef} and
	 * {@code Score}, which have no model-expression meaning at all, a geo predicate is an
	 * ordinary predicate over stored coordinates.
	 */
	@Test
	void geoWithinRoundTripsWithABox() throws QueryException {
		Expression within = Expressions.geoWithin(
				Expressions.geoSubject(Expressions.propertyPath(lat), Expressions.propertyPath(lon)),
				Expressions.geoBox(Expressions.geoPoint(10.5, 50.25), Expressions.geoPoint(13.75, 52.5)));

		assertThat(EcoreUtil.equals(roundTrip(within), within))
				.as("a box predicate survives the round trip unchanged")
				.isTrue();
	}

	/** A polygon with more points than a box, and the packed single-path binding (decision G1). */
	@Test
	void geoWithinRoundTripsWithAPolygonAndAPackedSubject() throws QueryException {
		Expression within = Expressions.geoWithin(
				Expressions.geoSubject(Expressions.propertyPath(position)),
				Expressions.geoPolygon(
						Expressions.geoPoint(10.0, 50.0),
						Expressions.geoPoint(11.0, 50.0),
						Expressions.geoPoint(11.0, 51.0),
						Expressions.geoPoint(10.0, 51.0)));

		assertThat(EcoreUtil.equals(roundTrip(within), within))
				.as("four vertices and a packed binding survive the round trip")
				.isTrue();
	}

	/**
	 * The case the consumer actually asks for (issue #232): a distance composed with a
	 * comparison, which is what decision G3 designed the value form for.
	 */
	@Test
	void geoDistanceRoundTripsComposedWithAComparison() throws QueryException {
		Expression predicate = Expressions.geoDistance(
				Expressions.geoSubject(Expressions.propertyPath(lat), Expressions.propertyPath(lon)),
				Expressions.geoPoint(9.99, 53.55))
				.le(500);

		assertThat(EcoreUtil.equals(roundTrip(predicate), predicate))
				.as("geo.distance(...) le 500 survives the round trip")
				.isTrue();
	}

	/**
	 * Longitude comes first everywhere in the dialect form — and this is the case that catches
	 * it, because the builder spells the pair the other way round
	 * ({@code geoSubject(latPath, lonPath)}) while {@code geoPoint(lon, lat)} and every shape are
	 * longitude-first. A round-trip test alone would pass even if both directions swapped the
	 * pair consistently; this one reads the rendered call and checks which path landed where.
	 */
	@Test
	void theDialectFormPutsLongitudeFirst() throws QueryException {
		Expression within = Expressions.geoWithin(
				Expressions.geoSubject(Expressions.propertyPath(lat), Expressions.propertyPath(lon)),
				Expressions.geoBox(Expressions.geoPoint(10.0, 50.0), Expressions.geoPoint(11.0, 51.0)));

		OperationCallExp call = (OperationCallExp) ExprToOcl.toOcl(within);
		assertThat(call.getName()).isEqualTo("geoWithin");
		assertThat(((PropertyCallExp) call.getOwnedSource()).getReferredProperty())
				.as("the call source is the LONGITUDE path")
				.isSameAs(lon);
		assertThat(((PropertyCallExp) call.getOwnedArguments().get(0)).getReferredProperty())
				.as("the first argument is the latitude path")
				.isSameAs(lat);

		GeoWithin readBack = (GeoWithin) OclToExpr.toExpr(call);
		assertThat(readBack.getSubject().getPathLon().getSegments()).containsExactly(lon);
		assertThat(readBack.getSubject().getPathLat().getSegments()).containsExactly(lat);
	}

	/** A shape function is an argument, not a predicate — asking for it alone must be refused. */
	@Test
	void aShapeCallOnItsOwnIsNotAnExpression() {
		OperationCallExp box = OclFactory.eINSTANCE.createOperationCallExp();
		box.setName("geoBox");
		assertThatThrownBy(() -> OclToExpr.toExpr(box))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("shape argument");
	}

	/** A malformed shape is refused by name rather than silently producing a wrong polygon. */
	@Test
	void aPolygonWithTooFewPointsIsRefused() {
		OperationCallExp within = OclFactory.eINSTANCE.createOperationCallExp();
		within.setName("geoWithin");
		within.setOwnedSource(property(lon));
		within.getOwnedArguments().add(property(lat));
		OperationCallExp polygon = OclFactory.eINSTANCE.createOperationCallExp();
		polygon.setName("geoPolygon");
		polygon.setOwnedSource(real(10.0));
		polygon.getOwnedArguments().add(real(50.0));
		polygon.getOwnedArguments().add(real(11.0));
		polygon.getOwnedArguments().add(real(51.0));
		within.getOwnedArguments().add(polygon);

		assertThatThrownBy(() -> OclToExpr.toExpr(within))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("at least three");
	}

	private static OclExpression real(double value) {
		var literal = OclFactory.eINSTANCE.createRealLiteralExp();
		literal.setRealSymbol(value);
		return literal;
	}

	@Test
	void integerDivisionStaysRefused() {
		OperationCallExp div = OclFactory.eINSTANCE.createOperationCallExp();
		div.setName("div");
		assertThatThrownBy(() -> OclToExpr.toExpr(div))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("div");
	}

	@Test
	void oclOutsideTheSubsetIsRefused() {
		OperationCallExp let = OclFactory.eINSTANCE.createOperationCallExp();
		let.setName("oclAsType");
		assertThatThrownBy(() -> OclToExpr.toExpr(let))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("oclAsType");

		IteratorExp collect = OclFactory.eINSTANCE.createIteratorExp();
		collect.setName("collect");
		assertThatThrownBy(() -> OclToExpr.toExpr(collect))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("collect");
	}
}
