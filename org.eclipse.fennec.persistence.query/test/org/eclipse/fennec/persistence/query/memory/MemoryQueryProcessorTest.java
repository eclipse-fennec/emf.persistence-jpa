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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.model.expression.Comparison;
import org.eclipse.fennec.model.expression.ComparisonOperator;
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
		personAddresses = ecore.createEReference();
		personAddresses.setName("addresses");
		personAddresses.setEType(addressClass);
		personAddresses.setUpperBound(-1);
		personAddresses.setContainment(true);
		personClass.getEStructuralFeatures().add(personName);
		personClass.getEStructuralFeatures().add(personNickname);
		personClass.getEStructuralFeatures().add(personAge);
		personClass.getEStructuralFeatures().add(personAddresses);

		ePackage = ecore.createEPackage();
		ePackage.setName("memtest");
		ePackage.setNsURI("urn:memoryquery:test");
		ePackage.setNsPrefix("memtest");
		ePackage.getEClassifiers().add(addressClass);
		ePackage.getEClassifiers().add(personClass);

		// the TCK query fixture: Alice 30, Bob 40 with two addresses, Carol 50
		persons.clear();
		persons.add(person("Alice", 30));
		EObject bob = person("Bob", 40);
		address(bob, "Main Street 5");
		address(bob, "Side Road 9");
		persons.add(bob);
		persons.add(person("Carol", 50));
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
	void filterStageAfterGroupByIsRefused() {
		Query query = QueryBuilder.from(personClass)
				.groupBy(personName)
				.countOf("cnt")
				.build();
		FilterStage filter = QueryFactory.eINSTANCE.createFilterStage();
		Comparison always = ExpressionFactory.eINSTANCE.createComparison();
		always.setOperator(ComparisonOperator.GE);
		always.setLeft(Expressions.propertyPath(personAge));
		always.setRight(Expressions.literal(0));
		filter.setPredicate(always);
		query.getApply().getStages().add(filter);

		assertThatThrownBy(() -> MemoryQueries.execute(query, persons, null))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("after GroupBy");
	}
}
