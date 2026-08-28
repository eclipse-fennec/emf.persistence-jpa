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
import static org.eclipse.fennec.model.query.builder.Expressions.aliasRef;
import static org.eclipse.fennec.model.query.builder.Expressions.all;
import static org.eclipse.fennec.model.query.builder.Expressions.and;
import static org.eclipse.fennec.model.query.builder.Expressions.any;
import static org.eclipse.fennec.model.query.builder.Expressions.not;
import static org.eclipse.fennec.model.query.builder.Expressions.or;
import static org.eclipse.fennec.model.query.builder.Expressions.param;
import static org.eclipse.fennec.model.query.builder.Expressions.path;
import static org.eclipse.fennec.model.query.builder.Expressions.propertyPath;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.persistence.converter.DefaultConverterService;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.builder.Expands;
import org.eclipse.fennec.model.query.builder.Expressions;
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
	private EAttribute personId;
	private EAttribute name;
	private EAttribute age;
	private EReference addresses;
	private EReference friend;
	private EReference mentor;
	private EReference owner;
	private EAttribute street;
	private EReference attributes;

	@BeforeEach
	void setUp() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		person = ecore.createEClass();
		person.setName("Person");
		// a filtered expand keys its second query by the root id (issue #238)
		personId = ecore.createEAttribute();
		personId.setName("id");
		personId.setEType(EcorePackage.Literals.ESTRING);
		personId.setID(true);
		person.getEStructuralFeatures().add(personId);
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

		friend = ecore.createEReference();
		friend.setName("friend");
		friend.setEType(person);
		person.getEStructuralFeatures().add(friend);

		mentor = ecore.createEReference();
		mentor.setName("mentor");
		mentor.setEType(person);
		person.getEStructuralFeatures().add(mentor);

		owner = ecore.createEReference();
		owner.setName("owner");
		owner.setEType(person);
		address.getEStructuralFeatures().add(owner);

		// an EMap (issue #186): containment-many onto a Map.Entry class
		EClass entry = ecore.createEClass();
		entry.setName("StringToStringMapEntry");
		entry.setInstanceClassName("java.util.Map$Entry");
		EAttribute key = ecore.createEAttribute();
		key.setName("key");
		key.setEType(EcorePackage.Literals.ESTRING);
		entry.getEStructuralFeatures().add(key);
		EAttribute value = ecore.createEAttribute();
		value.setName("value");
		value.setEType(EcorePackage.Literals.ESTRING);
		entry.getEStructuralFeatures().add(value);
		attributes = ecore.createEReference();
		attributes.setName("attributes");
		attributes.setEType(entry);
		attributes.setUpperBound(-1);
		attributes.setContainment(true);
		person.getEStructuralFeatures().add(attributes);
	}

	/**
	 * A map is an entry table on a relational store (contract §9.2), so one entry is a
	 * correlated subselect keyed on the entry's key column (issue #186).
	 */
	@Test
	void mapAccessRendersACorrelatedSubselect() throws QueryException {
		JpaQueryPlan plan = translate(QueryBuilder.from(person)
				.where(Expressions.mapValue(attributes, "color").eq("red"))
				.build());
		assertThat(plan.jpql()).isEqualTo("SELECT e FROM Person e WHERE "
				+ "(SELECT me0.value FROM e.attributes me0 WHERE me0.key = :p0) = :p1");
		assertThat(plan.parameters()).containsEntry("p0", "color").containsEntry("p1", "red");
	}

	@Test
	void mapAccessSortsAndBindsTheKeyOncePerAccess() throws QueryException {
		JpaQueryPlan plan = translate(QueryBuilder.from(person)
				.orderByAsc(Expressions.mapValue(attributes, "size").toExpression())
				.build());
		assertThat(plan.jpql()).contains("ORDER BY (SELECT me0.value FROM e.attributes me0"
				+ " WHERE me0.key = :p0) ASC");
		assertThat(plan.parameters()).containsEntry("p0", "size");
	}

	/**
	 * Inside a grouping the same access joins instead (issue #190): a correlated subselect
	 * references the grouped row and is illegal in {@code GROUP BY}. The join is outer with
	 * the key in {@code ON}, so owners without the key group under null.
	 */
	@Test
	void mapAccessInAGroupingRendersAJoin() throws QueryException {
		JpaQueryPlan plan = translate(QueryBuilder.from(person)
				.groupByAs("colour", Expressions.mapValue(attributes, "color").toExpression())
				.countOf("total")
				.build());
		assertThat(plan.jpql()).isEqualTo("SELECT mj0.value AS colour, COUNT(e) AS total"
				+ " FROM Person e LEFT JOIN e.attributes mj0 ON mj0.key = :p0"
				+ " GROUP BY mj0.value");
		assertThat(plan.parameters()).containsEntry("p0", "color");
	}

	/**
	 * One join per distinct map-and-key pair (issue #190) — grouping and aggregating over the
	 * same entry must address the same alias, or GROUP BY and SELECT would disagree.
	 */
	@Test
	void mapAccessJoinsOncePerKey() throws QueryException {
		JpaQueryPlan plan = translate(QueryBuilder.from(person)
				.groupByAs("colour", Expressions.mapValue(attributes, "color").toExpression())
				.countDistinct("colours", Expressions.mapValue(attributes, "color").toExpression())
				.max("largest", Expressions.mapValue(attributes, "size").toExpression())
				.build());
		assertThat(plan.jpql()).contains("LEFT JOIN e.attributes mj0 ON mj0.key = :p0")
				.contains("LEFT JOIN e.attributes mj1 ON mj1.key = :p1")
				.contains("COUNT(DISTINCT (mj0.value)) AS colours")
				.contains("MAX((mj1.value)) AS largest");
		assertThat(plan.parameters()).containsEntry("p0", "color").containsEntry("p1", "size");
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

	/**
	 * With a {@code ConverterService} in the context (issue #164), a parameter over a
	 * converter-claimed type is bound in its persistence form — the same form the mapping
	 * layer writes to the column — while plain types stay untouched.
	 */
	@Test
	void parameterOverConvertedTypeBindsThePersistenceForm() throws QueryException {
		EDataType uuidType = EcoreFactory.eINSTANCE.createEDataType();
		uuidType.setName("UUID");
		uuidType.setInstanceClass(UUID.class);
		EAttribute uid = EcoreFactory.eINSTANCE.createEAttribute();
		uid.setName("uid");
		uid.setEType(uuidType);
		person.getEStructuralFeatures().add(uid);

		UUID wanted = UUID.randomUUID();
		Query query = QueryBuilder.from(person).where(path(uid).eq(param("wanted"))).build();
		JpaQueryPlan plan = translate(query, QueryContexts.of(person,
				new DefaultConverterService(), Map.of("wanted", wanted), null));
		assertThat(plan.parameters()).containsEntry("p0", wanted.toString());
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

	/**
	 * Issue #189: a projection can name an expression, not only a path — the counterpart
	 * of the issue-#84 sort seam. The alias is the column name; JPQL renders inline.
	 */
	@Test
	void projectionOfAnExpression() throws QueryException {
		Query query = QueryBuilder.from(person)
				.selectAs("n", name)
				.selectAs("nextAge", path(age).plus(1).toExpression())
				.build();
		JpaQueryPlan plan = translate(query);
		assertThat(plan.shape()).isEqualTo(QueryShape.PROJECTION);
		assertThat(plan.jpql()).isEqualTo(
				"SELECT e.name AS n, (e.age + :p0) AS nextAge FROM Person e");
		assertThat(plan.rowKeys()).containsExactly("n", "nextAge");
		assertThat(plan.rowAliases()).containsExactly("n", "nextAge");
	}

	/**
	 * Issue #189 with #186: the map access that could not be projected before. It is the
	 * same correlated subselect the predicate uses, now in the select list.
	 */
	@Test
	void projectionOfAMapValue() throws QueryException {
		Query query = QueryBuilder.from(person)
				.selectAs("colour", Expressions.mapValue(attributes, "color").toExpression())
				.build();
		JpaQueryPlan plan = translate(query);
		assertThat(plan.jpql()).isEqualTo("SELECT (SELECT me0.value FROM e.attributes me0"
				+ " WHERE me0.key = :p0) AS colour FROM Person e");
		assertThat(plan.rowKeys()).containsExactly("colour");
	}

	/**
	 * Issue #155: substring offsets must be bound as {@code Integer}. The IR carries them as longs,
	 * and PostgreSQL declares only {@code substr(text, int, int)} — it refuses to narrow
	 * {@code bigint} during function resolution, while H2 narrows silently.
	 */
	@Test
	void substringOffsetsAreBoundAsIntegers() throws QueryException {
		Query query = QueryBuilder.from(person)
				.where(Expressions.path(name).substring(1, 3).eq("lic"))
				.build();

		JpaQueryPlan plan = translate(query);

		assertThat(plan.jpql()).contains("SUBSTRING(");
		assertThat(plan.parameters().values()).as("no offset may travel as a long")
				.noneMatch(Long.class::isInstance);
		assertThat(plan.parameters().values()).contains(1, 3);
	}

	/**
	 * A substring offset bound to {@code null} renders as the {@code NULL} literal for the whole
	 * expression, never as a bound parameter. Two things break otherwise: the parameter carries no
	 * SQL type, so PostgreSQL refuses what h2 and MariaDB accept (the shape of #228, #240 and
	 * #241); and the {@code CASE} would fall through to {@code ELSE 1} and substring from the
	 * front, where the in-memory reference yields null.
	 */
	@Test
	void aNullSubstringOffsetRendersAsTheNullLiteral() throws QueryException {
		Query query = QueryBuilder.from(person)
				.where(Expressions.path(name).substring(param("from")).eq("lic"))
				.build();

		JpaQueryPlan plan = translate(query,
				QueryContexts.of(person, null, Collections.singletonMap("from", null), null));

		assertThat(plan.jpql()).as("the whole substring collapses to NULL")
				.doesNotContain("SUBSTRING(").contains("NULL");
		assertThat(plan.parameters().values()).as("no untyped null may be bound")
				.doesNotContainNull();
	}

	/** The same for the length operand, which is optional and rendered separately. */
	@Test
	void aNullSubstringLengthRendersAsTheNullLiteral() throws QueryException {
		Query query = QueryBuilder.from(person)
				.where(Expressions.path(name).substring(1, param("len")).eq("lic"))
				.build();

		JpaQueryPlan plan = translate(query,
				QueryContexts.of(person, null, Collections.singletonMap("len", null), null));

		assertThat(plan.jpql()).doesNotContain("SUBSTRING(").contains("NULL");
		assertThat(plan.parameters().values()).doesNotContainNull();
	}

	/**
	 * Issue #156: an expression-valued group key is rendered twice — select list and GROUP BY —
	 * and EclipseLink expands one named parameter into a separate {@code ?} per occurrence, so
	 * PostgreSQL cannot see the two as the same expression. The plan therefore asks for binding to
	 * be switched off; a plain column group key does not need that.
	 */
	@Test
	void anExpressionGroupKeyAsksForInlineLiterals() throws QueryException {
		Query withExpressionKey = QueryBuilder.from(person)
				.groupByAs("band", Expressions.path(age).dividedBy(25).floor().toExpression())
				.avg("avgAge", age)
				.build();

		assertThat(translate(withExpressionKey).inlineLiterals()).isTrue();

		Query withColumnKey = QueryBuilder.from(person)
				.groupBy(name)
				.avg("avgAge", age)
				.build();

		assertThat(translate(withColumnKey).inlineLiterals())
				.as("a plain column key carries no parameters, so binding stays on")
				.isFalse();
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
	void aggregationAliasSortRendersTheResultVariable() throws QueryException {
		// a bare AliasRef sort key addresses the output column directly (issue #102)
		Query query = QueryBuilder.from(person)
				.groupBy(name)
				.sum("total", age)
				.orderByDesc(aliasRef("total").toExpression())
				.build();
		JpaQueryPlan plan = translate(query);
		assertThat(plan.jpql()).isEqualTo("SELECT e.name AS name, SUM(e.age) AS total"
				+ " FROM Person e GROUP BY e.name ORDER BY total DESC");

		Query unknown = QueryBuilder.from(person)
				.groupBy(name)
				.sum("total", age)
				.orderByDesc(aliasRef("nope").toExpression())
				.build();
		assertThatThrownBy(() -> translate(unknown)).isInstanceOf(QueryException.class)
				.hasMessageContaining("nope");
	}

	@Test
	void wholeSetAggregation() throws QueryException {
		JpaQueryPlan plan = translate(QueryBuilder.from(person).max("maxAge", age).build());
		assertThat(plan.jpql()).isEqualTo("SELECT MAX(e.age) AS maxAge FROM Person e");
	}

	@Test
	void expandSingleValuedBecomesAliasedFetchJoinChain() throws QueryException {
		// single-valued segments chain as aliased LEFT JOIN FETCH (issue #95)
		JpaQueryPlan plan = translate(QueryBuilder.from(person).expand(friend).build());
		assertThat(plan.jpql()).isEqualTo("SELECT e FROM Person e LEFT JOIN FETCH e.friend f0");
		assertThat(plan.batchFetchPaths()).isEmpty();

		JpaQueryPlan nested = translate(QueryBuilder.from(person).expand(friend, mentor).build());
		assertThat(nested.jpql()).isEqualTo(
				"SELECT e FROM Person e LEFT JOIN FETCH e.friend f0 LEFT JOIN FETCH f0.mentor f1");

		// shared prefixes reuse their alias — e.friend joins once
		JpaQueryPlan shared = translate(QueryBuilder.from(person)
				.expand(friend, mentor)
				.expand(friend)
				.build());
		assertThat(shared.jpql()).isEqualTo(
				"SELECT e FROM Person e LEFT JOIN FETCH e.friend f0 LEFT JOIN FETCH f0.mentor f1");
	}

	@Test
	void expandToManyBecomesBatchFetchPath() throws QueryException {
		// a collection fetch join would multiply rows and break setMaxResults (issue #95)
		JpaQueryPlan plan = translate(QueryBuilder.from(person).expand(addresses).build());
		assertThat(plan.jpql()).isEqualTo("SELECT e FROM Person e");
		assertThat(plan.batchFetchPaths()).containsExactly("e.addresses");

		// single-valued prefix fetch-joins, the to-many tail batches from the root path
		JpaQueryPlan mixed = translate(QueryBuilder.from(person).expand(friend, addresses).build());
		assertThat(mixed.jpql()).isEqualTo("SELECT e FROM Person e LEFT JOIN FETCH e.friend f0");
		assertThat(mixed.batchFetchPaths()).containsExactly("e.friend.addresses");

		// everything behind a to-many segment batches too — one hint per level
		JpaQueryPlan behind = translate(QueryBuilder.from(person).expand(addresses, owner).build());
		assertThat(behind.batchFetchPaths()).containsExactly("e.addresses", "e.addresses.owner");
	}

	@Test
	void expandRefusesNonReferenceSegments() {
		Query attribute = QueryBuilder.from(person).expand(addresses, street).build();
		assertThatThrownBy(() -> translate(attribute)).isInstanceOf(QueryException.class)
				.hasMessageContaining("not a reference");
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

	/**
	 * Per-parent paging is slice 3. A caller bypassing {@code validate()} — which refuses it
	 * against the undeclared EXPAND_PAGE — must still get a refusal here, never a plan that
	 * silently drops the option and returns the wrong children.
	 */
	@Test
	void expandPagingIsRefusedByTheTranslatorToo() {
		Query paged = QueryBuilder.from(person).expand(Expands.of(addresses).top(5).build()).build();

		assertThatThrownBy(() -> translate(paged))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("expand paging");
	}

	/**
	 * Issue #238: a filtered expansion becomes a keyed second query. The target carries the
	 * {@code e} alias so the filter — which addresses the expanded type — translates through the
	 * ordinary path; the root is joined in under its own alias and keys the query.
	 */
	@Test
	void aFilteredExpansionBecomesAKeyedSecondQuery() throws QueryException {
		Query filtered = QueryBuilder.from(person)
				.expand(Expands.of(addresses).filter(Expressions.path(street).eq("Main")).build())
				.build();

		JpaQueryPlan plan = translate(filtered);

		assertThat(plan.expandPlans()).hasSize(1);
		JpaExpandPlan expand = plan.expandPlans().get(0);
		assertThat(expand.jpql()).contains("FROM Person p JOIN p.addresses e")
				.contains("WHERE p.")
				.contains("IN :expandKeys AND (e.street = :p0)");
		assertThat(expand.parameters()).containsEntry("p0", "Main");
		assertThat(expand.target()).isEqualTo(addresses);
		assertThat(plan.jpql())
				.as("the main query is untouched — the filter never belongs to the root")
				.doesNotContain("street");
	}

	/** A plain expansion produces no second query: the fetch joins of #95 already serve it. */
	@Test
	void aPlainExpansionProducesNoSecondQuery() throws QueryException {
		assertThat(translate(QueryBuilder.from(person).expand(addresses).build()).expandPlans())
				.isEmpty();
	}

	/** The plain fetch hint is untouched by #238 — same JPQL as before the type changed. */
	@Test
	void aPlainExpandStillRendersTheFetchJoin() throws QueryException {
		JpaQueryPlan plan = translate(QueryBuilder.from(person).expand(friend).build());

		assertThat(plan.jpql()).contains("LEFT JOIN FETCH e.friend");
	}
}
