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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.model.expression.Comparison;
import org.eclipse.fennec.model.expression.ComparisonOperator;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.ExpressionFactory;
import org.eclipse.fennec.model.expression.StringFunction;
import org.eclipse.fennec.model.expression.StringFunctionKind;
import org.eclipse.fennec.model.query.FilterStage;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.QueryFactory;
import org.eclipse.fennec.model.query.SkipStage;
import org.eclipse.fennec.model.query.TopStage;
import org.eclipse.fennec.model.query.builder.Expressions;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.query.QueryException;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.eclipse.fennec.persistence.query.api.QueryResultRow;
import org.eclipse.fennec.persistence.query.api.QueryShape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Conformance tests of the {@code memory} backend — the queries mirror the TCK query
 * suite so in-memory semantics stay aligned with the database backends.
 *
 * @author Mark Hoffmann
 */
class MemoryQueryProcessorTest {

	private EClass personClass;
	private EClass addressClass;
	private EAttribute personName;
	private EAttribute personNickname;
	private EAttribute personAge;
	private EAttribute personScore;
	private EAttribute personSalary;
	private EAttribute personRank;
	private EAttribute personHired;
	private EAttribute lat;
	private EAttribute lon;
	private EEnum colorEnum;
	private EAttribute personFavoriteColor;
	private EReference personAddresses;
	private EAttribute addressStreet;
	private EPackage ePackage;

	private final List<EObject> persons = new ArrayList<>();

	@BeforeEach
	void setUp() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		addressClass = ecore.createEClass();
		addressClass.setName("Address");
		addressStreet = ecore.createEAttribute();
		addressStreet.setName("street");
		addressStreet.setEType(EcorePackage.Literals.ESTRING);
		addressClass.getEStructuralFeatures().add(addressStreet);

		personClass = ecore.createEClass();
		personClass.setName("Person");
		personName = ecore.createEAttribute();
		personName.setName("name");
		personName.setEType(EcorePackage.Literals.ESTRING);
		personNickname = ecore.createEAttribute();
		personNickname.setName("nickname");
		personNickname.setEType(EcorePackage.Literals.ESTRING);
		personAge = ecore.createEAttribute();
		personAge.setName("age");
		personAge.setEType(EcorePackage.Literals.EINT);
		personScore = ecore.createEAttribute();
		personScore.setName("score");
		personScore.setEType(EcorePackage.Literals.EDOUBLE);
		personSalary = ecore.createEAttribute();
		personSalary.setName("salary");
		personSalary.setEType(EcorePackage.Literals.EBIG_DECIMAL);
		personRank = ecore.createEAttribute();
		personRank.setName("rank");
		personRank.setEType(EcorePackage.Literals.EINTEGER_OBJECT);
		personHired = ecore.createEAttribute();
		personHired.setName("hired");
		personHired.setEType(EcorePackage.Literals.EDATE);
		lat = ecore.createEAttribute();
		lat.setName("lat");
		lat.setEType(EcorePackage.Literals.EDOUBLE_OBJECT);
		lon = ecore.createEAttribute();
		lon.setName("lon");
		lon.setEType(EcorePackage.Literals.EDOUBLE_OBJECT);
		colorEnum = ecore.createEEnum();
		colorEnum.setName("Color");
		// RED first — the first literal is the dynamic-EMF default for unset values
		EEnumLiteral red = ecore.createEEnumLiteral();
		red.setName("RED");
		colorEnum.getELiterals().add(red);
		EEnumLiteral green = ecore.createEEnumLiteral();
		green.setName("GREEN");
		green.setValue(1);
		colorEnum.getELiterals().add(green);
		personFavoriteColor = ecore.createEAttribute();
		personFavoriteColor.setName("favoriteColor");
		personFavoriteColor.setEType(colorEnum);
		personAddresses = ecore.createEReference();
		personAddresses.setName("addresses");
		personAddresses.setEType(addressClass);
		personAddresses.setUpperBound(-1);
		personAddresses.setContainment(true);
		personClass.getEStructuralFeatures().add(personName);
		personClass.getEStructuralFeatures().add(personNickname);
		personClass.getEStructuralFeatures().add(personAge);
		personClass.getEStructuralFeatures().add(personScore);
		personClass.getEStructuralFeatures().add(personSalary);
		personClass.getEStructuralFeatures().add(personRank);
		personClass.getEStructuralFeatures().add(personHired);
		personClass.getEStructuralFeatures().add(personFavoriteColor);
		personClass.getEStructuralFeatures().add(lat);
		personClass.getEStructuralFeatures().add(lon);
		personClass.getEStructuralFeatures().add(personAddresses);

		ePackage = ecore.createEPackage();
		ePackage.setName("memtest");
		ePackage.setNsURI("urn:memoryquery:test");
		ePackage.setNsPrefix("memtest");
		ePackage.getEClassifiers().add(addressClass);
		ePackage.getEClassifiers().add(personClass);
		ePackage.getEClassifiers().add(colorEnum);

		// the TCK query fixture: Alice 30, Bob 40 with two addresses, Carol 50 —
		// extended with score (double), salary (BigDecimal) and the nullable rank
		// for the arithmetic semantics tests
		persons.clear();
		EObject alice = person("Alice", 30);
		alice.eSet(personScore, 7.5d);
		alice.eSet(personSalary, new BigDecimal("1000.10"));
		alice.eSet(personFavoriteColor, green.getInstance());
		alice.eSet(lat, 50.927d); // Jena
		alice.eSet(lon, 11.586d);
		persons.add(alice);
		EObject bob = person("Bob", 40);
		address(bob, "Main Street 5");
		address(bob, "Side Road 9");
		bob.eSet(personScore, 3.0d);
		bob.eSet(personSalary, new BigDecimal("2000.20"));
		bob.eSet(personRank, 1);
		bob.eSet(lat, 50.880d); // Gera
		bob.eSet(lon, 12.083d);
		persons.add(bob);
		EObject carol = person("Carol", 50);
		carol.eSet(personScore, 12.5d);
		carol.eSet(personSalary, new BigDecimal("3000.30"));
		persons.add(carol);
	}

	private EObject person(String name, int age) {
		EObject person = ePackage.getEFactoryInstance().create(personClass);
		person.eSet(personName, name);
		person.eSet(personAge, age);
		return person;
	}

	@SuppressWarnings("unchecked")
	private void address(EObject person, String street) {
		EObject address = ePackage.getEFactoryInstance().create(addressClass);
		address.eSet(addressStreet, street);
		((List<EObject>) person.eGet(personAddresses)).add(address);
	}

	private List<Object> names(QueryResult result) {
		return result.objects().map(person -> person.eGet(personName)).map(Object.class::cast).toList();
	}

	@Test
	void groupedPredicateTree() throws QueryException {
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.and(
						Expressions.or(
								Expressions.path(personAge).ge(40),
								Expressions.path(personName).eq("Alice")),
						Expressions.path(personAge).ne(50)))
				.build();
		try (QueryResult result = MemoryQueries.execute(query, persons, null)) {
			assertThat(names(result)).containsExactlyInAnyOrder("Alice", "Bob");
		}
	}

	@Test
	void neInAndIsNotNull() throws QueryException {
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.and(
						Expressions.path(personAge).in(30, 40, 99),
						Expressions.path(personName).ne("Alice"),
						Expressions.path(personName).isNotNull()))
				.build();
		try (QueryResult result = MemoryQueries.execute(query, persons, null)) {
			assertThat(names(result)).containsExactly("Bob");
		}
	}

	@Test
	void stringLiteralCoercesAgainstEnumFeature() throws QueryException {
		// OData transports enum values as quoted strings (issue #93): 'GREEN' against
		// the enum-typed feature must match the Enumerator value, not stay a String
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.path(personFavoriteColor).eq("GREEN"))
				.build();
		try (QueryResult result = MemoryQueries.execute(query, persons, null)) {
			assertThat(names(result)).containsExactly("Alice");
		}

		Query unknown = QueryBuilder.from(personClass)
				.where(Expressions.path(personFavoriteColor).eq("PURPLE"))
				.build();
		assertThatThrownBy(() -> MemoryQueries.execute(unknown, persons, null))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("PURPLE");
	}

	@Test
	void notOverNullableComparisonIsThreeValued() throws QueryException {
		// Kleene 3VL (issue #94): not(rank = 2) over the null ranks of Alice/Carol is
		// UNKNOWN and excludes the row — matching SQL's NOT UNKNOWN, not Java's !false
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.not(Expressions.path(personRank).eq(2)))
				.build();
		try (QueryResult result = MemoryQueries.execute(query, persons, null)) {
			assertThat(names(result)).containsExactly("Bob");
		}

		// not(rank = 1): Bob is plainly false, the null ranks stay UNKNOWN — nobody
		Query nobody = QueryBuilder.from(personClass)
				.where(Expressions.not(Expressions.path(personRank).eq(1)))
				.build();
		try (QueryResult result = MemoryQueries.execute(nobody, persons, null)) {
			assertThat(names(result)).isEmpty();
		}
	}

	@Test
	void unknownPropagatesThroughJunctions() throws QueryException {
		// Carol: UNKNOWN or TRUE = TRUE (in), Alice: UNKNOWN or FALSE = UNKNOWN (out)
		Query orQuery = QueryBuilder.from(personClass)
				.where(Expressions.or(
						Expressions.not(Expressions.path(personRank).eq(2)),
						Expressions.path(personAge).ge(50)))
				.build();
		try (QueryResult result = MemoryQueries.execute(orQuery, persons, null)) {
			assertThat(names(result)).containsExactlyInAnyOrder("Bob", "Carol");
		}

		// FALSE dominates AND over UNKNOWN: Alice's (UNKNOWN and false) is FALSE, so
		// not(...) is TRUE — a blanket "operand must be non-null" guard would be wrong
		Query notOverAnd = QueryBuilder.from(personClass)
				.where(Expressions.not(Expressions.and(
						Expressions.path(personRank).eq(2),
						Expressions.path(personAge).ge(40))))
				.build();
		try (QueryResult result = MemoryQueries.execute(notOverAnd, persons, null)) {
			assertThat(names(result)).containsExactlyInAnyOrder("Alice", "Bob");
		}
	}

	@Test
	void notOverNullPoisonedOperandsIsUnknown() throws QueryException {
		// nickname is null everywhere: LIKE/contains over null is UNKNOWN, not false
		Query match = QueryBuilder.from(personClass)
				.where(Expressions.not(Expressions.path(personNickname).contains("x")))
				.build();
		try (QueryResult result = MemoryQueries.execute(match, persons, null)) {
			assertThat(names(result)).isEmpty();
		}

		// a null option keeps an IN miss UNKNOWN: 1 in (2, null) is UNKNOWN for Bob too
		Query inWithNull = QueryBuilder.from(personClass)
				.where(Expressions.not(Expressions.path(personRank).in(2, null)))
				.build();
		try (QueryResult result = MemoryQueries.execute(inWithNull, persons, null)) {
			assertThat(names(result)).isEmpty();
		}

		// IsNull stays two-valued — not(rank isNull) is exactly isNotNull
		Query notIsNull = QueryBuilder.from(personClass)
				.where(Expressions.not(Expressions.path(personRank).isNull()))
				.build();
		try (QueryResult result = MemoryQueries.execute(notIsNull, persons, null)) {
			assertThat(names(result)).containsExactly("Bob");
		}
	}

	@Test
	void scoreIsRefusedByTheMemoryBackend() {
		// issue #100: a score has no reference semantics — the memory engine does not
		// declare SCORE and validation refuses the query wholesale
		Query query = QueryBuilder.from(personClass)
				.orderByDesc(Expressions.score().toExpression())
				.build();
		assertThatThrownBy(() -> MemoryQueries.execute(query, persons, null))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("SCORE");
	}

	@Test
	void geoWithinBoxAndPolygon() throws QueryException {
		// Thuringia-ish box contains Jena (Alice) and Gera (Bob); Carol has no coords
		// — UNKNOWN excludes her (issue #101, 3VL per issue #94)
		Query box = QueryBuilder.from(personClass)
				.where(Expressions.geoWithin(
						Expressions.geoSubject(Expressions.propertyPath(lat), Expressions.propertyPath(lon)),
						Expressions.geoBox(Expressions.geoPoint(10.0, 50.0), Expressions.geoPoint(13.0, 51.5))))
				.build();
		try (QueryResult result = MemoryQueries.execute(box, persons, null)) {
			assertThat(names(result)).containsExactlyInAnyOrder("Alice", "Bob");
		}

		// triangle around Jena only
		Query polygon = QueryBuilder.from(personClass)
				.where(Expressions.geoWithin(
						Expressions.geoSubject(Expressions.propertyPath(lat), Expressions.propertyPath(lon)),
						Expressions.geoPolygon(
								Expressions.geoPoint(11.0, 50.5),
								Expressions.geoPoint(12.0, 50.5),
								Expressions.geoPoint(11.5, 51.5))))
				.build();
		try (QueryResult result = MemoryQueries.execute(polygon, persons, null)) {
			assertThat(names(result)).containsExactly("Alice");
		}

		// the antimeridian wrap-around box (west > east) contains Fiji-ish, not Jena
		Query wrap = QueryBuilder.from(personClass)
				.where(Expressions.geoWithin(
						Expressions.geoSubject(Expressions.propertyPath(lat), Expressions.propertyPath(lon)),
						Expressions.geoBox(Expressions.geoPoint(170.0, -30.0), Expressions.geoPoint(-170.0, 0.0))))
				.build();
		try (QueryResult result = MemoryQueries.execute(wrap, persons, null)) {
			assertThat(names(result)).isEmpty();
		}

		// 3VL: not(within) must not flip Carol's UNKNOWN into a match
		Query notWithin = QueryBuilder.from(personClass)
				.where(Expressions.not(Expressions.geoWithin(
						Expressions.geoSubject(Expressions.propertyPath(lat), Expressions.propertyPath(lon)),
						Expressions.geoPolygon(
								Expressions.geoPoint(11.0, 50.5),
								Expressions.geoPoint(12.0, 50.5),
								Expressions.geoPoint(11.5, 51.5)))))
				.build();
		try (QueryResult result = MemoryQueries.execute(notWithin, persons, null)) {
			assertThat(names(result)).containsExactly("Bob");
		}
	}

	@Test
	void geoDistanceComparesAndSortsNearestFirst() throws QueryException {
		// Jena↔Gera is ~35 km — 10 km around Jena keeps only Alice
		Query near = QueryBuilder.from(personClass)
				.where(Expressions.geoDistance(
						Expressions.geoSubject(Expressions.propertyPath(lat), Expressions.propertyPath(lon)),
						Expressions.geoPoint(11.586, 50.927)).le(10_000))
				.build();
		try (QueryResult result = MemoryQueries.execute(near, persons, null)) {
			assertThat(names(result)).containsExactly("Alice");
		}

		// nearest first via the issue-#84 sort seam (Carol's null coords sort last)
		Query nearest = QueryBuilder.from(personClass)
				.orderByAsc(Expressions.geoDistance(
						Expressions.geoSubject(Expressions.propertyPath(lat), Expressions.propertyPath(lon)),
						Expressions.geoPoint(12.083, 50.880)).toExpression())
				.build();
		try (QueryResult result = MemoryQueries.execute(nearest, persons, null)) {
			assertThat(names(result)).containsExactly("Bob", "Alice", "Carol");
		}
	}

	@Test
	void geoPackedSubjectIsRefusedByTheMemoryEngine() {
		Query packed = QueryBuilder.from(personClass)
				.where(Expressions.geoWithin(
						Expressions.geoSubject(Expressions.propertyPath(lat)),
						Expressions.geoBox(Expressions.geoPoint(10.0, 50.0), Expressions.geoPoint(13.0, 51.5))))
				.build();
		assertThatThrownBy(() -> MemoryQueries.execute(packed, persons, null))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("Packed geo subjects");
	}

	@Test
	void caseInsensitiveMatching() throws QueryException {
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.path(personName).containsIgnoreCase("ARO"))
				.build();
		try (QueryResult result = MemoryQueries.execute(query, persons, null)) {
			assertThat(names(result)).containsExactly("Carol");
		}
	}

	@Test
	void likePatternWithWildcards() throws QueryException {
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.path(personName).like("_ob"))
				.build();
		try (QueryResult result = MemoryQueries.execute(query, persons, null)) {
			assertThat(names(result)).containsExactly("Bob");
		}
	}

	@Test
	void existsAndForAllOverContainment() throws QueryException {
		Query exists = QueryBuilder.from(personClass)
				.where(Expressions.any(Expressions.propertyPath(personAddresses),
						a -> a.path(addressStreet).startsWith("Main")))
				.build();
		try (QueryResult result = MemoryQueries.execute(exists, persons, null)) {
			assertThat(names(result)).containsExactly("Bob");
		}

		Query forAll = QueryBuilder.from(personClass)
				.where(Expressions.all(Expressions.propertyPath(personAddresses),
						a -> a.path(addressStreet).startsWith("Main")))
				.build();
		try (QueryResult result = MemoryQueries.execute(forAll, persons, null)) {
			// Alice and Carol have no addresses (vacuously true); Bob has a non-Main address
			assertThat(names(result)).containsExactlyInAnyOrder("Alice", "Carol");
		}
	}

	@Test
	void sortSkipTopAndCount() throws QueryException {
		Query query = QueryBuilder.from(personClass)
				.orderByDesc(personAge)
				.skip(1)
				.top(1)
				.build();
		try (QueryResult result = MemoryQueries.execute(query, persons, null)) {
			assertThat(names(result)).containsExactly("Bob");
		}

		Query count = QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).gt(30))
				.countOnly()
				.build();
		try (QueryResult result = MemoryQueries.execute(count, persons, null)) {
			assertThat(result.shape()).isEqualTo(QueryShape.COUNT);
			assertThat(result.count()).isEqualTo(2);
		}
	}

	@Test
	void parameterBindingAndMissingParameterRefusal() throws QueryException {
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).eq(Expressions.param("wanted")))
				.build();
		try (QueryResult result = MemoryQueries.execute(query, persons, Map.of("wanted", 50))) {
			assertThat(names(result)).containsExactly("Carol");
		}
		assertThatThrownBy(() -> MemoryQueries.execute(query, persons, null))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("wanted");
	}

	@Test
	void arithmeticDivisionIsFloatingPointOnIntegers() throws QueryException {
		// 30 / 4 = 7.5 — integer truncation (7) would match nobody
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).dividedBy(4).eq(7.5))
				.build();
		try (QueryResult result = MemoryQueries.execute(query, persons, null)) {
			assertThat(names(result)).containsExactly("Alice");
		}
	}

	@Test
	void arithmeticWidensMixedIntAndDoubleOperands() throws QueryException {
		// score(double) + age(int): Alice 37.5, Bob 43.0, Carol 62.5
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.path(personScore).plus(Expressions.path(personAge)).eq(37.5))
				.build();
		try (QueryResult result = MemoryQueries.execute(query, persons, null)) {
			assertThat(names(result)).containsExactly("Alice");
		}
	}

	@Test
	void arithmeticComputesOnBigDecimal() throws QueryException {
		// salary(BigDecimal) * 2 = 2000.20 — the decimal branch computes, comparisons
		// are numeric across boxed types (the IR has no decimal literal yet, see #83)
		Query multiply = QueryBuilder.from(personClass)
				.where(Expressions.path(personSalary).times(2).eq(2000.20))
				.build();
		try (QueryResult result = MemoryQueries.execute(multiply, persons, null)) {
			assertThat(names(result)).containsExactly("Alice");
		}
		// salary / 3 divides with DECIMAL64 precision: only Alice stays below 334
		Query divide = QueryBuilder.from(personClass)
				.where(Expressions.path(personSalary).dividedBy(3).lt(334))
				.build();
		try (QueryResult result = MemoryQueries.execute(divide, persons, null)) {
			assertThat(names(result)).containsExactly("Alice");
		}
	}

	@Test
	void arithmeticRuntimeZeroDivisorMatchesNothing() throws QueryException {
		// a zero divisor bound at runtime yields null — every comparison is false
		// (the database backends surface their own division error instead, see the TCK)
		Query division = QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).dividedBy(Expressions.param("divisor")).gt(-1000))
				.build();
		try (QueryResult result = MemoryQueries.execute(division, persons, Map.of("divisor", 0))) {
			assertThat(result.objects()).isEmpty();
		}
		try (QueryResult result = MemoryQueries.execute(division, persons, Map.of("divisor", 4))) {
			assertThat(result.objects()).hasSize(3);
		}
		Query modulo = QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).mod(Expressions.param("divisor")).ge(0))
				.build();
		try (QueryResult result = MemoryQueries.execute(modulo, persons, Map.of("divisor", 0))) {
			assertThat(result.objects()).isEmpty();
		}
	}

	@Test
	void arithmeticNullOperandMatchesNothing() throws QueryException {
		// rank is null for Alice and Carol — null propagates, the comparison is false
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.path(personRank).plus(1).gt(0))
				.build();
		try (QueryResult result = MemoryQueries.execute(query, persons, null)) {
			assertThat(names(result)).containsExactly("Bob");
		}
	}

	@Test
	void concatJoinsPartsAndNullPoisons() throws QueryException {
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.concat(Expressions.path(personName), "!").eq("Bob!"))
				.build();
		try (QueryResult result = MemoryQueries.execute(query, persons, null)) {
			assertThat(names(result)).containsExactly("Bob");
		}
		// nickname is null everywhere — a null part poisons the concatenation,
		// so even NE against an unrelated value matches nothing
		Query poisoned = QueryBuilder.from(personClass)
				.where(Expressions.concat(Expressions.path(personName), Expressions.path(personNickname))
						.ne("nonexistent"))
				.build();
		try (QueryResult result = MemoryQueries.execute(poisoned, persons, null)) {
			assertThat(result.objects()).isEmpty();
		}
	}

	@Test
	void indexOfIsZeroBasedWithMinusOneWhenAbsent() throws QueryException {
		// "Bob".indexOf("o") = 1, "Carol" = 3, "Alice" = -1
		Query found = QueryBuilder.from(personClass)
				.where(Expressions.path(personName).indexOf("o").eq(1))
				.build();
		try (QueryResult result = MemoryQueries.execute(found, persons, null)) {
			assertThat(names(result)).containsExactly("Bob");
		}
		Query absent = QueryBuilder.from(personClass)
				.where(Expressions.path(personName).indexOf("o").eq(-1))
				.build();
		try (QueryResult result = MemoryQueries.execute(absent, persons, null)) {
			assertThat(names(result)).containsExactly("Alice");
		}
	}

	@Test
	void substringFollowsODataClamping() throws QueryException {
		// plain 0-based window
		Query window = QueryBuilder.from(personClass)
				.where(Expressions.path(personName).substring(0, 3).eq("Bob"))
				.build();
		try (QueryResult result = MemoryQueries.execute(window, persons, null)) {
			assertThat(names(result)).containsExactly("Bob");
		}
		// negative start counts from the end of the string
		Query fromEnd = QueryBuilder.from(personClass)
				.where(Expressions.path(personName).substring(-2).eq("ol"))
				.build();
		try (QueryResult result = MemoryQueries.execute(fromEnd, persons, null)) {
			assertThat(names(result)).containsExactly("Carol");
		}
		// start beyond the end yields the empty string
		Query beyond = QueryBuilder.from(personClass)
				.where(Expressions.path(personName).substring(99).eq(""))
				.build();
		try (QueryResult result = MemoryQueries.execute(beyond, persons, null)) {
			assertThat(result.objects()).hasSize(3);
		}
		// negative length yields the empty string
		Query negativeLength = QueryBuilder.from(personClass)
				.where(Expressions.path(personName).substring(1, -1).eq(""))
				.build();
		try (QueryResult result = MemoryQueries.execute(negativeLength, persons, null)) {
			assertThat(result.objects()).hasSize(3);
		}
	}

	@Test
	void roundIsHalfAwayFromZero() throws QueryException {
		// score: Alice 7.5 → 8, Bob 3.0 → 3, Carol 12.5 → 13 (banker's would give 12)
		Query round = QueryBuilder.from(personClass)
				.where(Expressions.path(personScore).round().eq(13))
				.build();
		try (QueryResult result = MemoryQueries.execute(round, persons, null)) {
			assertThat(names(result)).containsExactly("Carol");
		}
		// negative values round away from zero too: -7.5 → -8
		Query negative = QueryBuilder.from(personClass)
				.where(Expressions.path(personScore).negated().round().eq(-8))
				.build();
		try (QueryResult result = MemoryQueries.execute(negative, persons, null)) {
			assertThat(names(result)).containsExactly("Alice");
		}
	}

	@Test
	void floorAndCeilingClampToIntegral() throws QueryException {
		Query floor = QueryBuilder.from(personClass)
				.where(Expressions.path(personScore).floor().eq(7))
				.build();
		try (QueryResult result = MemoryQueries.execute(floor, persons, null)) {
			assertThat(names(result)).containsExactly("Alice");
		}
		Query ceiling = QueryBuilder.from(personClass)
				.where(Expressions.path(personScore).ceiling().eq(8))
				.build();
		try (QueryResult result = MemoryQueries.execute(ceiling, persons, null)) {
			assertThat(names(result)).containsExactly("Alice");
		}
		// null operand (rank is null for Alice/Carol) propagates — no match
		Query nullSource = QueryBuilder.from(personClass)
				.where(Expressions.path(personRank).round().ge(0))
				.build();
		try (QueryResult result = MemoryQueries.execute(nullSource, persons, null)) {
			assertThat(names(result)).containsExactly("Bob");
		}
	}

	@Test
	void temporalPartsExtractInUtc() throws QueryException {
		// hired: 2020-06-15T22:45:30Z — set only for Bob; extraction is UTC-normative
		persons.get(1).eSet(personHired, Date.from(Instant.parse("2020-06-15T22:45:30Z")));

		assertTemporal(Expressions.path(personHired).year().eq(2020), "Bob");
		assertTemporal(Expressions.path(personHired).month().eq(6), "Bob");
		assertTemporal(Expressions.path(personHired).day().eq(15), "Bob");
		assertTemporal(Expressions.path(personHired).hour().eq(22), "Bob");
		assertTemporal(Expressions.path(personHired).minute().eq(45), "Bob");
		assertTemporal(Expressions.path(personHired).second().eq(30), "Bob");
	}

	private void assertTemporal(Expression predicate, String expected) throws QueryException {
		Query query = QueryBuilder.from(personClass).where(predicate).build();
		try (QueryResult result = MemoryQueries.execute(query, persons, null)) {
			assertThat(names(result)).containsExactly(expected);
		}
	}

	@Test
	void temporalNullSourceMatchesNothing() throws QueryException {
		// hired is unset everywhere — null propagates, no person matches
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.path(personHired).year().ge(0))
				.build();
		try (QueryResult result = MemoryQueries.execute(query, persons, null)) {
			assertThat(result.objects()).isEmpty();
		}
	}

	@Test
	void typeCheckUsesKindOfSemanticsAndTreatYieldsNull() throws QueryException {
		// VipPerson extends Person, nickname reused as its own attribute domain
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		EClass vipClass = ecore.createEClass();
		vipClass.setName("VipPerson");
		vipClass.getESuperTypes().add(personClass);
		EAttribute vipLevel = ecore.createEAttribute();
		vipLevel.setName("level");
		vipLevel.setEType(EcorePackage.Literals.EINT);
		vipClass.getEStructuralFeatures().add(vipLevel);
		ePackage.getEClassifiers().add(vipClass);

		EObject vip = ePackage.getEFactoryInstance().create(vipClass);
		vip.eSet(personName, "Vera");
		vip.eSet(personAge, 35);
		vip.eSet(vipLevel, 3);
		persons.add(vip);

		// kind-of: every person (including the subtype) matches Person
		Query kindOf = QueryBuilder.from(personClass)
				.where(Expressions.isOf(personClass))
				.build();
		try (QueryResult result = MemoryQueries.execute(kindOf, persons, null)) {
			assertThat(result.objects()).hasSize(4);
		}
		Query onlyVip = QueryBuilder.from(personClass)
				.where(Expressions.isOf(vipClass))
				.build();
		try (QueryResult result = MemoryQueries.execute(onlyVip, persons, null)) {
			assertThat(names(result)).containsExactly("Vera");
		}
		// treat: non-instances yield null on the cast path — excluded, not an error
		Query treat = QueryBuilder.from(personClass)
				.where(Expressions.pathAs(vipClass, vipLevel).ge(1))
				.build();
		try (QueryResult result = MemoryQueries.execute(treat, persons, null)) {
			assertThat(names(result)).containsExactly("Vera");
		}
	}

	@Test
	void collectionCountsPlainAndFiltered() throws QueryException {
		// Bob has two addresses, one on "Main Street 5"
		Query plain = QueryBuilder.from(personClass)
				.where(Expressions.count(Expressions.propertyPath(personAddresses)).ge(2))
				.build();
		try (QueryResult result = MemoryQueries.execute(plain, persons, null)) {
			assertThat(names(result)).containsExactly("Bob");
		}
		Query filtered = QueryBuilder.from(personClass)
				.where(Expressions.count(Expressions.propertyPath(personAddresses),
						a -> a.path(addressStreet).startsWith("Main")).eq(1))
				.build();
		try (QueryResult result = MemoryQueries.execute(filtered, persons, null)) {
			assertThat(names(result)).containsExactly("Bob");
		}
		// empty collections count 0 — Alice and Carol have no addresses
		Query none = QueryBuilder.from(personClass)
				.where(Expressions.count(Expressions.propertyPath(personAddresses)).eq(0))
				.build();
		try (QueryResult result = MemoryQueries.execute(none, persons, null)) {
			assertThat(names(result)).containsExactlyInAnyOrder("Alice", "Carol");
		}
	}

	@Test
	void sortByExpressionOrdersObjectsAndRows() throws QueryException {
		// -age ascending = age descending: Carol, Bob, Alice
		Query objects = QueryBuilder.from(personClass)
				.orderByAsc(Expressions.neg(Expressions.path(personAge)).toExpression())
				.build();
		try (QueryResult result = MemoryQueries.execute(objects, persons, null)) {
			assertThat(names(result)).containsExactly("Carol", "Bob", "Alice");
		}
		// row space: sort grouped rows by a computed expression over the aggregate alias
		Query rows = QueryBuilder.from(personClass)
				.groupBy(personAge)
				.countOf("cnt")
				.orderByDesc(Expressions.aliasRef("age").toExpression())
				.build();
		try (QueryResult result = MemoryQueries.execute(rows, persons, null)) {
			assertThat(result.rows().map(row -> ((Number) row.get("age")).intValue()))
					.containsExactly(50, 40, 30);
		}
	}

	@Test
	void negateWorksOnFloatingValues() throws QueryException {
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.path(personScore).negated().lt(-10))
				.build();
		try (QueryResult result = MemoryQueries.execute(query, persons, null)) {
			assertThat(names(result)).containsExactly("Carol");
		}
	}

	@Test
	void projectionRows() throws QueryException {
		Query query = QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).ge(40))
				.selectAs("n", personName)
				.build();
		try (QueryResult result = MemoryQueries.execute(query, persons, null)) {
			assertThat(result.shape()).isEqualTo(QueryShape.PROJECTION);
			List<QueryResultRow> rows = result.rows().toList();
			assertThat(rows).extracting(row -> row.get("n")).containsExactlyInAnyOrder("Bob", "Carol");
			assertThat(rows).extracting(row -> row.get(0)).containsExactlyInAnyOrder("Bob", "Carol");
		}
	}

	@Test
	void wholeSetAggregation() throws QueryException {
		Query query = QueryBuilder.from(personClass)
				.avg("avgAge", personAge)
				.countOf("cnt")
				.build();
		try (QueryResult result = MemoryQueries.execute(query, persons, null)) {
			assertThat(result.shape()).isEqualTo(QueryShape.AGGREGATION);
			List<QueryResultRow> rows = result.rows().toList();
			assertThat(rows).hasSize(1);
			assertThat(((Number) rows.get(0).get("avgAge")).doubleValue()).isEqualTo(40.0);
			assertThat(((Number) rows.get(0).get("cnt")).longValue()).isEqualTo(3L);
		}
	}

	@Test
	void groupedAggregationWithRowSort() throws QueryException {
		persons.add(person("Bob", 20));
		Query query = QueryBuilder.from(personClass)
				.groupBy(personName)
				.avg("avgAge", personAge)
				.countOf("cnt")
				.orderByAsc(personName)
				.build();
		try (QueryResult result = MemoryQueries.execute(query, persons, null)) {
			List<QueryResultRow> rows = result.rows().toList();
			assertThat(rows).extracting(row -> row.get("name"))
					.containsExactly("Alice", "Bob", "Carol");
			QueryResultRow bobRow = rows.get(1);
			assertThat(((Number) bobRow.get("avgAge")).doubleValue()).isEqualTo(30.0);
			assertThat(((Number) bobRow.get("cnt")).longValue()).isEqualTo(2L);
		}
	}

	@Test
	void minMaxSumAndCountDistinct() throws QueryException {
		Query query = QueryBuilder.from(personClass)
				.min("youngest", personAge)
				.max("oldest", personAge)
				.sum("total", personAge)
				.countDistinct("distinctNames", personName)
				.build();
		try (QueryResult result = MemoryQueries.execute(query, persons, null)) {
			QueryResultRow row = result.rows().toList().get(0);
			assertThat(row.get("youngest")).isEqualTo(30);
			assertThat(row.get("oldest")).isEqualTo(50);
			assertThat(((Number) row.get("total")).longValue()).isEqualTo(120L);
			assertThat(((Number) row.get("distinctNames")).longValue()).isEqualTo(3L);
		}
	}

	@Test
	void multiStagePipelineStaysInObjectSpace() throws QueryException {
		// filter -> skip -> top without grouping: the Mongo-native showcase, in memory
		Query query = QueryBuilder.from(personClass)
				.orderByAsc(personAge)
				.build();
		QueryFactory factory = QueryFactory.eINSTANCE;
		FilterStage filter = factory.createFilterStage();
		Comparison adult = ExpressionFactory.eINSTANCE.createComparison();
		adult.setOperator(ComparisonOperator.GE);
		adult.setLeft(Expressions.propertyPath(personAge));
		adult.setRight(Expressions.literal(40));
		filter.setPredicate(adult);
		SkipStage skip = factory.createSkipStage();
		skip.setCount(1);
		TopStage top = factory.createTopStage();
		top.setCount(1);
		org.eclipse.fennec.model.query.Pipeline pipeline = factory.createPipeline();
		pipeline.getStages().add(filter);
		pipeline.getStages().add(skip);
		pipeline.getStages().add(top);
		query.setApply(pipeline);

		try (QueryResult result = MemoryQueries.execute(query, persons, null)) {
			assertThat(names(result)).containsExactly("Carol");
		}
	}

	@Test
	void pipelineStagesAroundGroupBy() throws QueryException {
		persons.add(person("Bob", 20));
		Query query = QueryBuilder.from(personClass)
				.groupBy(personName)
				.countOf("cnt")
				.orderByAsc(personName)
				.build();
		QueryFactory factory = QueryFactory.eINSTANCE;
		FilterStage filter = factory.createFilterStage();
		Comparison adult = ExpressionFactory.eINSTANCE.createComparison();
		adult.setOperator(ComparisonOperator.GE);
		adult.setLeft(Expressions.propertyPath(personAge));
		adult.setRight(Expressions.literal(30));
		filter.setPredicate(adult);
		query.getApply().getStages().add(0, filter);
		TopStage top = factory.createTopStage();
		top.setCount(2);
		query.getApply().getStages().add(top);

		try (QueryResult result = MemoryQueries.execute(query, persons, null)) {
			List<QueryResultRow> rows = result.rows().toList();
			// Bob 20 filtered before grouping; top 2 of the sorted rows
			assertThat(rows).extracting(row -> row.get("name")).containsExactly("Alice", "Bob");
			assertThat(((Number) rows.get(1).get("cnt")).longValue()).isEqualTo(1L);
		}
	}

	@Test
	void fieldToFieldComparisonAndStringFunctions() throws QueryException {
		persons.get(0).eSet(personNickname, "Alice"); // nickname == name
		Query fieldEq = QueryBuilder.from(personClass)
				.where(Expressions.path(personName).eq(Expressions.propertyPath(personNickname)))
				.build();
		try (QueryResult result = MemoryQueries.execute(fieldEq, persons, null)) {
			assertThat(names(result)).containsExactly("Alice");
		}

		StringFunction toLower = ExpressionFactory.eINSTANCE.createStringFunction();
		toLower.setKind(StringFunctionKind.TO_LOWER);
		toLower.setSource(Expressions.propertyPath(personName));
		Comparison lowered = ExpressionFactory.eINSTANCE.createComparison();
		lowered.setOperator(ComparisonOperator.EQ);
		lowered.setLeft(toLower);
		lowered.setRight(Expressions.literal("carol"));
		Query lower = QueryBuilder.from(personClass).where(lowered).build();
		try (QueryResult result = MemoryQueries.execute(lower, persons, null)) {
			assertThat(names(result)).containsExactly("Carol");
		}
	}

	@Test
	void betweenAndDistinct() throws QueryException {
		Query between = QueryBuilder.from(personClass)
				.where(Expressions.path(personAge).between(30, 40))
				.build();
		try (QueryResult result = MemoryQueries.execute(between, persons, null)) {
			assertThat(names(result)).containsExactlyInAnyOrder("Alice", "Bob");
		}

		persons.add(person("Alice", 60));
		Query distinctNames = QueryBuilder.from(personClass)
				.selectAs("n", personName)
				.distinct()
				.build();
		try (QueryResult result = MemoryQueries.execute(distinctNames, persons, null)) {
			assertThat(result.rows().map(row -> row.get("n")).toList())
					.containsExactlyInAnyOrder("Alice", "Bob", "Carol");
		}
	}

	@Test
	void typeFilterIgnoresForeignObjects() throws QueryException {
		List<EObject> mixed = new ArrayList<>(persons);
		EObject address = ePackage.getEFactoryInstance().create(addressClass);
		address.eSet(addressStreet, "Main Street 5");
		mixed.add(address);
		Query query = QueryBuilder.from(personClass).countOnly().build();
		try (QueryResult result = MemoryQueries.execute(query, mixed, null)) {
			assertThat(result.count()).isEqualTo(3);
		}
	}

	@Test
	void sortOnNonOutputKeyIsRefused() {
		Query bad = QueryBuilder.from(personClass)
				.avg("avgAge", personAge)
				.orderByAsc(personName)
				.build();
		assertThatThrownBy(() -> MemoryQueries.execute(bad, persons, null))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("output key");
	}

	@Test
	void preGroupComputeFeedsGroupKeysAndSources() throws QueryException {
		// floor(age/25) buckets Alice(30)/Bob(40) as 1, Carol(50) as 2 (issue #87)
		Query query = QueryBuilder.from(personClass)
				.computeAs("band", Expressions.path(personAge).dividedBy(25).floor().toExpression())
				.groupByAs("bucket", Expressions.aliasRef("band").toExpression())
				.sum("bandSum", Expressions.aliasRef("band").toExpression())
				.countOf("cnt")
				.build();
		try (QueryResult result = MemoryQueries.execute(query, persons, null)) {
			List<QueryResultRow> rows = result.rows().toList();
			assertThat(rows).hasSize(2);
			Map<Integer, QueryResultRow> byBucket = rows.stream()
					.collect(Collectors.toMap(row -> ((Number) row.get("bucket")).intValue(), row -> row));
			assertThat(((Number) byBucket.get(1).get("cnt")).longValue()).isEqualTo(2);
			assertThat(((Number) byBucket.get(1).get("bandSum")).intValue()).isEqualTo(2);
			assertThat(((Number) byBucket.get(2).get("cnt")).longValue()).isEqualTo(1);
			assertThat(((Number) byBucket.get(2).get("bandSum")).intValue()).isEqualTo(2);
		}
	}

	@Test
	void unknownAliasInGroupKeyIsRefused() {
		Query query = QueryBuilder.from(personClass)
				.groupByAs("bucket", Expressions.aliasRef("ghost").toExpression())
				.countOf("cnt")
				.build();
		assertThatThrownBy(() -> MemoryQueries.execute(query, persons, null))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("ghost");
	}

	@Test
	void havingFiltersGroupedRows() throws QueryException {
		// two persons share age 30 once Dave joins — HAVING keeps only that group
		persons.add(person("Dave", 30));
		Query query = QueryBuilder.from(personClass)
				.groupBy(personAge)
				.countOf("cnt")
				.sum("total", personAge)
				.having(Expressions.aliasRef("cnt").ge(2))
				.build();
		try (QueryResult result = MemoryQueries.execute(query, persons, null)) {
			List<QueryResultRow> rows = result.rows().toList();
			assertThat(rows).hasSize(1);
			assertThat(((Number) rows.get(0).get("age")).intValue()).isEqualTo(30);
			assertThat(((Number) rows.get(0).get("cnt")).longValue()).isEqualTo(2);
			assertThat(((Number) rows.get(0).get("total")).longValue()).isEqualTo(60);
		}
	}

	@Test
	void postGroupComputeDerivesColumns() throws QueryException {
		// whole-set: total 120, cnt 3 → avg = total / cnt = 40.0 (FP division)
		Query query = QueryBuilder.from(personClass)
				.sum("total", personAge)
				.countOf("cnt")
				.computeAs("avgAge", Expressions.div(Expressions.aliasRef("total"),
						Expressions.aliasRef("cnt")).toExpression())
				.having(Expressions.aliasRef("avgAge").ge(30))
				.build();
		try (QueryResult result = MemoryQueries.execute(query, persons, null)) {
			List<QueryResultRow> rows = result.rows().toList();
			assertThat(rows).hasSize(1);
			assertThat(((Number) rows.get(0).get("avgAge")).doubleValue()).isEqualTo(40.0);
		}
	}

	@Test
	void terminalComputeYieldsOneRowPerEntity() throws QueryException {
		Query query = QueryBuilder.from(personClass)
				.computeAs("doubled", Expressions.path(personAge).times(2).toExpression())
				.build();
		try (QueryResult result = MemoryQueries.execute(query, persons, null)) {
			List<QueryResultRow> rows = result.rows().toList();
			assertThat(rows).hasSize(3);
			assertThat(rows.stream().map(row -> ((Number) row.get("doubled")).longValue()))
					.containsExactlyInAnyOrder(60L, 80L, 100L);
			// single-valued attributes ride along
			assertThat(rows.stream().map(row -> row.get("name")))
					.containsExactlyInAnyOrder("Alice", "Bob", "Carol");
		}
	}
}
