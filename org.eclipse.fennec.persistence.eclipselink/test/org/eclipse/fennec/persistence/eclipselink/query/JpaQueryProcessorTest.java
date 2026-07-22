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

import java.util.Map;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.SortOrder;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.query.QueryException;
import org.eclipse.fennec.persistence.query.api.QueryContext;
import org.eclipse.fennec.persistence.query.api.QueryShape;
import org.eclipse.fennec.persistence.query.support.QueryContexts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Translation tests for the {@link JpaQueryProcessor} — pure JPQL assertions, no database.
 *
 * @author Mark Hoffmann
 */
class JpaQueryProcessorTest {

	private final JpaQueryProcessor processor = new JpaQueryProcessor();

	private EClass person;
	private EAttribute name;
	private EAttribute age;
	private EReference address;
	private EAttribute street;

	@BeforeEach
	void setUp() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		EPackage pkg = ecore.createEPackage();
		pkg.setName("test");
		pkg.setNsURI("http://test/jpa/query/1.0");

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

		EClass addressClass = ecore.createEClass();
		addressClass.setName("Address");
		street = ecore.createEAttribute();
		street.setName("street");
		street.setEType(EcorePackage.Literals.ESTRING);
		addressClass.getEStructuralFeatures().add(street);

		address = ecore.createEReference();
		address.setName("address");
		address.setEType(addressClass);
		person.getEStructuralFeatures().add(address);

		pkg.getEClassifiers().add(person);
		pkg.getEClassifiers().add(addressClass);
	}

	private JpaQueryPlan translate(Query query) throws QueryException {
		return translate(query, QueryContexts.of(person, null));
	}

	private JpaQueryPlan translate(Query query, QueryContext ctx) throws QueryException {
		return (JpaQueryPlan) processor.translate(query, ctx);
	}

	@Test
	void backendAndCapabilities() {
		assertThat(processor.backend()).isEqualTo("jpa");
		assertThat(processor.capabilities().maxFeaturePathDepth()).isEqualTo(-1);
	}

	@Test
	void emptyQuerySelectsAll() throws QueryException {
		JpaQueryPlan plan = translate(QueryBuilder.create().build());
		assertThat(plan.shape()).isEqualTo(QueryShape.OBJECTS);
		assertThat(plan.jpql()).isEqualTo("SELECT e FROM Person e");
		assertThat(plan.parameters()).isEmpty();
	}

	@Test
	void eqBindsNamedParameter() throws QueryException {
		JpaQueryPlan plan = translate(QueryBuilder.create().where(age).eq(42).build());
		assertThat(plan.jpql()).isEqualTo("SELECT e FROM Person e WHERE e.age = :p0");
		assertThat(plan.parameters()).containsEntry("p0", 42);
	}

	@Test
	void chainedAndOrNotWithParentheses() throws QueryException {
		Query query = QueryBuilder.create()
				.where(age).gte(18)
				.and(age).lt(65)
				.or(name).eq("smith")
				.not(name).eq("test")
				.build();
		JpaQueryPlan plan = translate(query);
		assertThat(plan.jpql()).isEqualTo("SELECT e FROM Person e WHERE "
				+ "(((e.age >= :p0 AND e.age < :p1) OR e.name = :p2) AND NOT (e.name = :p3))");
		assertThat(plan.parameters()).containsEntry("p0", 18).containsEntry("p1", 65)
				.containsEntry("p2", "smith").containsEntry("p3", "test");
	}

	@Test
	void stringMatchersUseLikeWithEscapedWildcards() throws QueryException {
		JpaQueryPlan contains = translate(QueryBuilder.create().where(name).contains("50%").build());
		assertThat(contains.jpql()).contains("e.name LIKE :p0 ESCAPE '\\'");
		assertThat(contains.parameters()).containsEntry("p0", "%50\\%%");

		JpaQueryPlan starts = translate(QueryBuilder.create().where(name).startsWith("sm_").build());
		assertThat(starts.parameters()).containsEntry("p0", "sm\\_%");

		JpaQueryPlan like = translate(QueryBuilder.create().where(name).like("sm_th%").build());
		assertThat(like.parameters()).containsEntry("p0", "sm_th%");
	}

	@Test
	void caseInsensitiveMatchingWrapsBothSides() throws QueryException {
		JpaQueryPlan plan = translate(QueryBuilder.create().where(name).toLower().eq("Smith").build());
		assertThat(plan.jpql()).contains("LOWER(e.name) = LOWER(:p0)");
		assertThat(plan.parameters()).containsEntry("p0", "Smith");

		JpaQueryPlan likePlan = translate(QueryBuilder.create().where(name).toUpper().contains("x").build());
		assertThat(likePlan.jpql()).contains("UPPER(e.name) LIKE UPPER(:p0) ESCAPE '\\'");
	}

	@Test
	void rangeWithBoundFlags() throws QueryException {
		JpaQueryPlan plan = translate(QueryBuilder.create().where(age).inRange(18, 65, true, false).build());
		assertThat(plan.jpql()).contains("(e.age >= :p0 AND e.age < :p1)");
		assertThat(plan.parameters()).containsEntry("p0", 18).containsEntry("p1", 65);
	}

	@Test
	void nestedPathBecomesPathExpression() throws QueryException {
		JpaQueryPlan plan = translate(QueryBuilder.create().where(address, street).eq("Main St").build());
		assertThat(plan.jpql()).contains("e.address.street = :p0");
	}

	@Test
	void parameterPlaceholderIsResolvedAndBound() throws QueryException {
		Query query = QueryBuilder.create().where(age).eqParam("minAge").build();
		JpaQueryPlan plan = translate(query, QueryContexts.of(person, null, Map.of("minAge", 21), null));
		assertThat(plan.parameters()).containsEntry("p0", 21);

		assertThatThrownBy(() -> translate(query)).isInstanceOf(QueryException.class)
				.hasMessageContaining(":minAge");
	}

	@Test
	void distinctSortSkipLimit() throws QueryException {
		Query query = QueryBuilder.create()
				.where(age).gte(18)
				.distinct()
				.sortBy(age, SortOrder.ASC)
				.sortBy(name, SortOrder.DESC)
				.skip(5)
				.limit(10)
				.build();
		JpaQueryPlan plan = translate(query);
		assertThat(plan.jpql()).startsWith("SELECT DISTINCT e FROM Person e WHERE ")
				.endsWith(" ORDER BY e.age ASC, e.name DESC");
		assertThat(plan.skip()).isEqualTo(5);
		assertThat(plan.limit()).isEqualTo(10);
	}

	@Test
	void countQuery() throws QueryException {
		JpaQueryPlan plan = translate(QueryBuilder.create().where(age).gte(18).count().build());
		assertThat(plan.shape()).isEqualTo(QueryShape.COUNT);
		assertThat(plan.jpql()).isEqualTo("SELECT COUNT(e) FROM Person e WHERE e.age >= :p0");
	}

	@Test
	void typeFilterIsTheFromClause() throws QueryException {
		Query query = QueryBuilder.create().from(person).where(age).eq(42).build();
		assertThat(processor.validate(query, person).getSeverity()).isEqualTo(Diagnostic.OK);
		assertThat(translate(query).jpql()).startsWith("SELECT e FROM Person e");
	}

	@Test
	void projectionSelectsPathsWithResultVariables() throws QueryException {
		Query query = QueryBuilder.create()
				.where(age).gte(18)
				.selectAs("personName", name)
				.select(address, street)
				.build();
		JpaQueryPlan plan = translate(query);

		assertThat(plan.shape()).isEqualTo(QueryShape.PROJECTION);
		assertThat(plan.jpql()).isEqualTo(
				"SELECT e.name AS personName, e.address.street AS address_street FROM Person e WHERE e.age >= :p0");
		assertThat(plan.rowKeys()).containsExactly("personName", "address_street");
		assertThat(plan.rowAliases()).containsExactly("personName", null);
	}

	@Test
	void distinctProjection() throws QueryException {
		JpaQueryPlan plan = translate(QueryBuilder.create().selectAs("n", name).distinct().build());
		assertThat(plan.jpql()).isEqualTo("SELECT DISTINCT e.name AS n FROM Person e");
	}

	@Test
	void groupedAggregation() throws QueryException {
		Query query = QueryBuilder.create()
				.select(name)
				.avg("avgAge", age)
				.countOf("cnt", age)
				.groupBy(name)
				.build();
		JpaQueryPlan plan = translate(query);

		assertThat(plan.shape()).isEqualTo(QueryShape.AGGREGATION);
		assertThat(plan.jpql()).isEqualTo("SELECT e.name AS name, AVG(e.age) AS avgAge, COUNT(e.age) AS cnt"
				+ " FROM Person e GROUP BY e.name");
	}

	@Test
	void ungroupedAggregateIsWholeSet() throws QueryException {
		JpaQueryPlan plan = translate(QueryBuilder.create().max("maxAge", age).build());
		assertThat(plan.jpql()).isEqualTo("SELECT MAX(e.age) AS maxAge FROM Person e");
	}

	@Test
	void rowSortingAddressesResultVariables() throws QueryException {
		EAttribute avgAge = EcoreFactory.eINSTANCE.createEAttribute();
		avgAge.setName("avgAge");
		avgAge.setEType(EcorePackage.Literals.EDOUBLE);

		Query query = QueryBuilder.create()
				.select(name)
				.avg("avgAge", age)
				.groupBy(name)
				.sortBy(avgAge, SortOrder.DESC)
				.build();
		assertThat(translate(query).jpql()).endsWith(" GROUP BY e.name ORDER BY avgAge DESC");

		Query bad = QueryBuilder.create().avg("avgAge", age).sortBy(name, SortOrder.ASC).build();
		assertThatThrownBy(() -> translate(bad)).isInstanceOf(QueryException.class)
				.hasMessageContaining("name");
	}

	@Test
	void plainSubjectOutsideGroupByIsRefused() {
		Query query = QueryBuilder.create()
				.select(name)
				.avg("avgAge", age)
				.groupBy(age)
				.build();
		assertThatThrownBy(() -> translate(query)).isInstanceOf(QueryException.class)
				.hasMessageContaining("e.name");
	}

	@Test
	void duplicateResultKeysAreRefused() {
		Query query = QueryBuilder.create().selectAs("x", name).selectAs("x", age).build();
		assertThatThrownBy(() -> translate(query)).isInstanceOf(QueryException.class)
				.hasMessageContaining("Duplicate result key");
	}

	@Test
	void likeEscaping() {
		assertThat(JpaQueryProcessor.escapeLike("a%b_c\\d")).isEqualTo("a\\%b\\_c\\\\d");
	}
}
