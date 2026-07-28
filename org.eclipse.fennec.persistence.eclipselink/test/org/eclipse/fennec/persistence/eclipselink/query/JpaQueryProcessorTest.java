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
package org.eclipse.fennec.persistence.eclipselink.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.eclipse.fennec.model.query.builder.Expressions.all;
import static org.eclipse.fennec.model.query.builder.Expressions.and;
import static org.eclipse.fennec.model.query.builder.Expressions.any;
import static org.eclipse.fennec.model.query.builder.Expressions.not;
import static org.eclipse.fennec.model.query.builder.Expressions.or;
import static org.eclipse.fennec.model.query.builder.Expressions.param;
import static org.eclipse.fennec.model.query.builder.Expressions.path;
import static org.eclipse.fennec.model.query.builder.Expressions.propertyPath;

import java.util.Map;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.query.QueryException;
import org.eclipse.fennec.persistence.query.api.QueryContext;
import org.eclipse.fennec.persistence.query.api.QueryShape;
import org.eclipse.fennec.persistence.query.support.QueryContexts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Translation tests for the {@link JpaQueryProcessor} over the expression IR — pure
 * JPQL assertions, no database.
 *
 * @author Mark Hoffmann
 */
class JpaQueryProcessorTest {

	private final JpaQueryProcessor processor = new JpaQueryProcessor();

	private EClass person;
	private EAttribute name;
	private EAttribute age;
	private EReference addresses;
	private EAttribute street;

	@BeforeEach
	void setUp() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		person = ecore.createEClass();
		person.setName("Person");
		name = ecore.createEAttribute();
		name.setName("name");
		name.setEType(EcorePackage.Literals.ESTRING);
		age = ecore.createEAttribute();
		age.setName("age");
		age.setEType(EcorePackage.Literals.EINT);
		person.getEStructuralFeatures().add(name);
		person.getEStructuralFeatures().add(age);

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

	private JpaQueryPlan translate(Query query) throws QueryException {
		return translate(query, QueryContexts.of(person, null));
	}

	private JpaQueryPlan translate(Query query, QueryContext context) throws QueryException {
		return (JpaQueryPlan) processor.translate(query, context);
	}

	@Test
	void emptyQuerySelectsAll() throws QueryException {
		JpaQueryPlan plan = translate(QueryBuilder.from(person).build());
		assertThat(plan.shape()).isEqualTo(QueryShape.OBJECTS);
		assertThat(plan.jpql()).isEqualTo("SELECT e FROM Person e");
	}

	@Test
	void groupedTreeRendersParenthesised() throws QueryException {
		Query query = QueryBuilder.from(person)
				.where(and(
						or(path(age).ge(18), path(age).ne(65)),
						not(path(name).isNull())))
				.build();
		JpaQueryPlan plan = translate(query);
		assertThat(plan.jpql()).isEqualTo("SELECT e FROM Person e WHERE "
				+ "((e.age >= :p0 OR e.age <> :p1) AND NOT (e.name IS NULL))");
		assertThat(plan.parameters()).containsEntry("p0", 18).containsEntry("p1", 65);
	}

	@Test
	void isNotNullAndBetweenAndIn() throws QueryException {
		Query query = QueryBuilder.from(person)
				.where(and(
						path(name).isNotNull(),
						path(age).between(18, 65, true, false),
						path(age).in(1, 2, 3)))
				.build();
		JpaQueryPlan plan = translate(query);
		assertThat(plan.jpql()).contains("e.name IS NOT NULL")
				.contains("(e.age >= :p0 AND e.age < :p1)")
				.contains("e.age IN (:p2, :p3, :p4)");
	}

	@Test
	void stringMatchersEscapeAndFold() throws QueryException {
		JpaQueryPlan contains = translate(QueryBuilder.from(person)
				.where(path(name).contains("50%")).build());
		assertThat(contains.jpql()).contains("e.name LIKE :p0 ESCAPE '\\'");
		assertThat(contains.parameters()).containsEntry("p0", "%50\\%%");

		JpaQueryPlan ci = translate(QueryBuilder.from(person)
				.where(path(name).startsWithIgnoreCase("Sm")).build());
		assertThat(ci.jpql()).contains("LOWER(e.name) LIKE LOWER(:p0) ESCAPE '\\'");
		assertThat(ci.parameters()).containsEntry("p0", "Sm%");
	}

	@Test
	void stringFunctionsRenderAsJpqlFunctions() throws QueryException {
		JpaQueryPlan lower = translate(QueryBuilder.from(person)
				.where(path(name).toLower().eq("bob")).build());
		assertThat(lower.jpql()).contains("LOWER(e.name) = :p0");
		assertThat(lower.parameters()).containsEntry("p0", "bob");

		JpaQueryPlan length = translate(QueryBuilder.from(person)
				.where(path(name).length().gt(3)).build());
		assertThat(length.jpql()).contains("LENGTH(e.name) > :p0");

		JpaQueryPlan chained = translate(QueryBuilder.from(person)
				.where(path(name).trim().toUpper().eq("BOB")).build());
		assertThat(chained.jpql()).contains("UPPER(TRIM(e.name)) = :p0");
	}

	@Test
	void fieldToFieldComparisonRendersBothPaths() throws QueryException {
		JpaQueryPlan plan = translate(QueryBuilder.from(person)
				.where(path(name).ne(path(name))).build());
		assertThat(plan.jpql()).contains("e.name <> e.name");
		assertThat(plan.parameters()).isEmpty();
	}

	@Test
	void quantifiersBecomeCorrelatedExists() throws QueryException {
		Query query = QueryBuilder.from(person)
				.where(any(propertyPath(addresses), a -> a.path(street).startsWith("Main")))
				.build();
		JpaQueryPlan plan = translate(query);
		assertThat(plan.jpql()).isEqualTo("SELECT e FROM Person e WHERE "
				+ "EXISTS (SELECT it0 FROM e.addresses it0 WHERE it0.street LIKE :p0 ESCAPE '\\')");

		Query forAll = QueryBuilder.from(person)
				.where(all(propertyPath(addresses), a -> a.path(street).isNotNull()))
				.build();
		assertThat(translate(forAll).jpql()).contains(
				"NOT EXISTS (SELECT it0 FROM e.addresses it0 WHERE NOT (it0.street IS NOT NULL))");
	}

	@Test
	void nestedQuantifiersGetUniqueAliases() throws QueryException {
		Query query = QueryBuilder.from(person)
				.where(any(propertyPath(addresses),
						a -> and(a.path(street).isNotNull(),
								any(propertyPath(addresses), b -> b.path(street).eq("x")))))
				.build();
		String jpql = translate(query).jpql();
		assertThat(jpql).contains("it0").contains("it1");
	}

	@Test
	void parameterBindingAndRefusal() throws QueryException {
		Query query = QueryBuilder.from(person).where(path(age).eq(param("minAge"))).build();
		JpaQueryPlan plan = translate(query, QueryContexts.of(person, null, Map.of("minAge", 21), null));
		assertThat(plan.parameters()).containsEntry("p0", 21);

		assertThatThrownBy(() -> translate(query))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("minAge");
	}

	@Test
	void sortPagingDistinctCount() throws QueryException {
		Query query = QueryBuilder.from(person)
				.where(path(age).ge(18))
				.orderByAsc(age)
				.orderByDesc(name)
				.distinct()
				.top(10)
				.skip(5)
				.build();
		JpaQueryPlan plan = translate(query);
		assertThat(plan.jpql()).startsWith("SELECT DISTINCT e FROM Person e WHERE ")
				.endsWith(" ORDER BY e.age ASC, e.name DESC");
		assertThat(plan.skip()).isEqualTo(5);
		assertThat(plan.limit()).isEqualTo(10);

		JpaQueryPlan count = translate(QueryBuilder.from(person).where(path(age).ge(18)).countOnly().build());
		assertThat(count.shape()).isEqualTo(QueryShape.COUNT);
		assertThat(count.jpql()).isEqualTo("SELECT COUNT(e) FROM Person e WHERE e.age >= :p0");
	}

	@Test
	void projectionWithAliasesAndNestedPaths() throws QueryException {
		Query query = QueryBuilder.from(person)
				.where(path(age).ge(18))
				.selectAs("n", name)
				.select(addresses, street)
				.build();
		JpaQueryPlan plan = translate(query);
		assertThat(plan.shape()).isEqualTo(QueryShape.PROJECTION);
		assertThat(plan.jpql()).isEqualTo(
				"SELECT e.name AS n, e.addresses.street AS addresses_street FROM Person e WHERE e.age >= :p0");
		assertThat(plan.rowKeys()).containsExactly("n", "addresses_street");
		assertThat(plan.rowAliases()).containsExactly("n", null);
	}

	@Test
	void aggregationWithGroupByAndRowSort() throws QueryException {
		// row sorting addresses output keys — a synthetic feature named like the alias
		EAttribute avgAge = EcoreFactory.eINSTANCE.createEAttribute();
		avgAge.setName("avgAge");
		avgAge.setEType(EcorePackage.Literals.EDOUBLE);
		Query query = QueryBuilder.from(person)
				.groupBy(name)
				.avg("avgAge", age)
				.countOf("cnt")
				.countDistinct("streets", addresses, street)
				.orderByDesc(avgAge)
				.build();

		JpaQueryPlan plan = translate(query);
		assertThat(plan.shape()).isEqualTo(QueryShape.AGGREGATION);
		assertThat(plan.jpql()).isEqualTo("SELECT e.name AS name, AVG(e.age) AS avgAge, COUNT(e) AS cnt,"
				+ " COUNT(DISTINCT e.addresses.street) AS streets"
				+ " FROM Person e GROUP BY e.name ORDER BY avgAge DESC");
	}

	@Test
	void wholeSetAggregation() throws QueryException {
		JpaQueryPlan plan = translate(QueryBuilder.from(person).max("maxAge", age).build());
		assertThat(plan.jpql()).isEqualTo("SELECT MAX(e.age) AS maxAge FROM Person e");
	}

	@Test
	void expandBecomesFetchJoin() throws QueryException {
		JpaQueryPlan plan = translate(QueryBuilder.from(person).expand(addresses).build());
		assertThat(plan.jpql()).isEqualTo("SELECT e FROM Person e LEFT JOIN FETCH e.addresses");

		Query nested = QueryBuilder.from(person).expand(addresses, street).build();
		assertThatThrownBy(() -> translate(nested)).isInstanceOf(QueryException.class)
				.hasMessageContaining("depth");
	}

	@Test
	void refusals() {
		Query sortOnNonOutput = QueryBuilder.from(person)
				.avg("avgAge", age)
				.orderByAsc(name)
				.build();
		assertThatThrownBy(() -> translate(sortOnNonOutput)).isInstanceOf(QueryException.class)
				.hasMessageContaining("output key");

		Query duplicateKeys = QueryBuilder.from(person).selectAs("x", name).selectAs("x", age).build();
		assertThatThrownBy(() -> translate(duplicateKeys)).isInstanceOf(QueryException.class)
				.hasMessageContaining("Duplicate result key");
	}

	@Test
	void validateUsesTheDeclaredCapabilities() {
		Query query = QueryBuilder.from(person).where(path(age).eq(1)).build();
		assertThat(processor.validate(query, person).getSeverity()).isEqualTo(Diagnostic.OK);
		assertThat(processor.backend()).isEqualTo("jpa");
		assertThat(processor.capabilities().maxFeaturePathDepth()).isEqualTo(-1);
	}

	@Test
	void likeEscaping() {
		assertThat(JpaQueryProcessor.escapeLike("a%b_c\\d")).isEqualTo("a\\%b\\_c\\\\d");
	}
}
