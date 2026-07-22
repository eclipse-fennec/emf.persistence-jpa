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
package org.eclipse.fennec.persistence.query.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.model.query.And;
import org.eclipse.fennec.model.query.Contains;
import org.eclipse.fennec.model.query.Eq;
import org.eclipse.fennec.model.query.IsInRange;
import org.eclipse.fennec.model.query.Like;
import org.eclipse.fennec.model.query.Not;
import org.eclipse.fennec.model.query.Or;
import org.eclipse.fennec.model.query.QObject;
import org.eclipse.fennec.model.query.QSubject;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.QueryFactory;
import org.eclipse.fennec.model.query.SortEntity;
import org.eclipse.fennec.model.utilities.FeaturePath;
import org.eclipse.fennec.model.utilities.UtilitiesFactory;
import org.eclipse.fennec.persistence.query.api.QueryFeature;
import org.eclipse.fennec.persistence.query.api.QueryShape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link QueryAnalyzer} feature detection, depth tracking and shape derivation.
 *
 * @author Mark Hoffmann
 */
class QueryAnalyzerTest {

	private EClass person;
	private EAttribute name;
	private EAttribute age;
	private EReference address;
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
	}

	private FeaturePath path(EStructuralFeature... features) {
		FeaturePath path = UtilitiesFactory.eINSTANCE.createFeaturePath();
		for (EStructuralFeature feature : features) {
			path.getFeature().add(feature);
		}
		return path;
	}

	private And eqWhere(EStructuralFeature feature, String value) {
		QueryFactory factory = QueryFactory.eINSTANCE;
		And where = factory.createAnd();
		where.setFeaturePath(path(feature));
		Eq eq = factory.createEq();
		eq.setValue(value);
		where.setComparator(eq);
		return where;
	}

	@Test
	void nullQueryIsRejected() {
		assertThatIllegalArgumentException().isThrownBy(() -> QueryAnalyzer.analyze(null));
	}

	@Test
	void emptyQueryIsObjectsShapedWithoutFeatures() {
		QueryAnalysis analysis = QueryAnalyzer.analyze(QueryFactory.eINSTANCE.createQuery());
		assertThat(analysis.features()).isEmpty();
		assertThat(analysis.shape()).isEqualTo(QueryShape.OBJECTS);
		assertThat(analysis.maxFeaturePathDepth()).isZero();
	}

	@Test
	void simpleEqPredicate() {
		Query query = QueryFactory.eINSTANCE.createQuery();
		query.getWhere().add(eqWhere(age, "42"));

		QueryAnalysis analysis = QueryAnalyzer.analyze(query);
		assertThat(analysis.features()).containsExactlyInAnyOrder(QueryFeature.WHERE_EQ);
		assertThat(analysis.shape()).isEqualTo(QueryShape.OBJECTS);
		assertThat(analysis.maxFeaturePathDepth()).isEqualTo(1);
	}

	@Test
	void firstWhereEntryDoesNotCountAsChaining() {
		Query query = QueryFactory.eINSTANCE.createQuery();
		query.getWhere().add(eqWhere(age, "42"));

		assertThat(QueryAnalyzer.analyze(query).features()).doesNotContain(QueryFeature.LOGICAL_AND);
	}

	@Test
	void chainedOrAndNotAreDetected() {
		QueryFactory factory = QueryFactory.eINSTANCE;
		Query query = factory.createQuery();
		query.getWhere().add(eqWhere(age, "42"));

		Or or = factory.createOr();
		or.setFeaturePath(path(name));
		Contains contains = factory.createContains();
		contains.setValue("smith");
		or.setComparator(contains);
		query.getWhere().add(or);

		Not not = factory.createNot();
		not.setFeaturePath(path(name));
		Like like = factory.createLike();
		like.setValue("%test%");
		not.setComparator(like);
		query.getWhere().add(not);

		QueryAnalysis analysis = QueryAnalyzer.analyze(query);
		assertThat(analysis.features()).contains(QueryFeature.LOGICAL_OR, QueryFeature.LOGICAL_NOT,
				QueryFeature.WHERE_EQ, QueryFeature.WHERE_STRING_MATCH);
	}

	@Test
	void comparatorKindsMapToFeatures() {
		QueryFactory factory = QueryFactory.eINSTANCE;

		Query query = factory.createQuery();
		And lt = factory.createAnd();
		lt.setFeaturePath(path(age));
		lt.setComparator(factory.createLt());
		query.getWhere().add(lt);

		And date = factory.createAnd();
		date.setFeaturePath(path(age));
		date.setComparator(factory.createIsBefore());
		query.getWhere().add(date);

		And range = factory.createAnd();
		range.setFeaturePath(path(age));
		range.setComparator(factory.createIsInRange());
		query.getWhere().add(range);

		And literal = factory.createAnd();
		literal.setFeaturePath(path(name));
		literal.setComparator(factory.createIsLiteral());
		query.getWhere().add(literal);

		And bool = factory.createAnd();
		bool.setFeaturePath(path(name));
		bool.setComparator(factory.createIsBool());
		query.getWhere().add(bool);

		QueryAnalysis analysis = QueryAnalyzer.analyze(query);
		assertThat(analysis.features()).contains(QueryFeature.WHERE_COMPARISON, QueryFeature.WHERE_DATE,
				QueryFeature.WHERE_RANGE, QueryFeature.WHERE_ENUM, QueryFeature.WHERE_BOOL);
		assertThat(analysis.features()).doesNotContain(QueryFeature.WHERE_EQ);
	}

	@Test
	void parameterPlaceholdersAreDetected() {
		Query query = QueryFactory.eINSTANCE.createQuery();
		query.getWhere().add(eqWhere(name, ":name"));

		assertThat(QueryAnalyzer.analyze(query).features()).contains(QueryFeature.PARAMETERS);
	}

	@Test
	void rangeParameterPlaceholdersAreDetected() {
		QueryFactory factory = QueryFactory.eINSTANCE;
		Query query = factory.createQuery();
		And where = factory.createAnd();
		where.setFeaturePath(path(age));
		IsInRange range = factory.createIsInRange();
		range.setStartValue(":from");
		range.setEndValue("100");
		where.setComparator(range);
		query.getWhere().add(where);

		assertThat(QueryAnalyzer.analyze(query).features()).contains(QueryFeature.WHERE_RANGE,
				QueryFeature.PARAMETERS);
	}

	@Test
	void nestedFeaturePathInWhereIsDetected() {
		Query query = QueryFactory.eINSTANCE.createQuery();
		And where = eqWhere(street, "Main St");
		where.getFeaturePath().getFeature().add(0, address);
		query.getWhere().add(where);

		QueryAnalysis analysis = QueryAnalyzer.analyze(query);
		assertThat(analysis.features()).contains(QueryFeature.FEATUREPATH_NESTED);
		assertThat(analysis.maxFeaturePathDepth()).isEqualTo(2);
	}

	@Test
	void shapingFlagsAreDetected() {
		Query query = QueryFactory.eINSTANCE.createQuery();
		query.setLimit(10);
		query.setSkip(5);
		query.setDistinct(true);
		SortEntity sort = QueryFactory.eINSTANCE.createSortEntity();
		sort.setSortFeature(name);
		query.getSortBy().add(sort);

		QueryAnalysis analysis = QueryAnalyzer.analyze(query);
		assertThat(analysis.features()).contains(QueryFeature.SORT, QueryFeature.LIMIT, QueryFeature.SKIP,
				QueryFeature.DISTINCT);
	}

	@Test
	void countQueryHasCountShape() {
		Query query = QueryFactory.eINSTANCE.createQuery();
		query.setCount(true);

		QueryAnalysis analysis = QueryAnalyzer.analyze(query);
		assertThat(analysis.features()).contains(QueryFeature.COUNT);
		assertThat(analysis.shape()).isEqualTo(QueryShape.COUNT);
	}

	@Test
	void subjectProjectionHasProjectionShape() {
		QueryFactory factory = QueryFactory.eINSTANCE;
		Query query = factory.createQuery();
		QSubject subject = factory.createQSubject();
		subject.setFeaturePath(path(name));
		query.getSubject().add(subject);

		QueryAnalysis analysis = QueryAnalyzer.analyze(query);
		assertThat(analysis.features()).contains(QueryFeature.PROJECTION);
		assertThat(analysis.shape()).isEqualTo(QueryShape.PROJECTION);
	}

	@Test
	void nestedSubjectProjectionIsDetected() {
		QueryFactory factory = QueryFactory.eINSTANCE;
		Query query = factory.createQuery();
		QSubject subject = factory.createQSubject();
		subject.setFeaturePath(path(address, street));
		query.getSubject().add(subject);

		QueryAnalysis analysis = QueryAnalyzer.analyze(query);
		assertThat(analysis.features()).contains(QueryFeature.PROJECTION, QueryFeature.PROJECTION_NESTED);
		assertThat(analysis.maxFeaturePathDepth()).isEqualTo(2);
	}

	@Test
	void groupByWithAverageIsAggregation() {
		QueryFactory factory = QueryFactory.eINSTANCE;
		Query query = factory.createQuery();
		query.getGroupBy().add(path(name));
		QSubject subject = factory.createQSubject();
		subject.setFeaturePath(path(age));
		subject.setOperation(factory.createAverage());
		query.getSubject().add(subject);

		QueryAnalysis analysis = QueryAnalyzer.analyze(query);
		assertThat(analysis.features()).contains(QueryFeature.GROUP_BY, QueryFeature.AGG_AVG,
				QueryFeature.PROJECTION);
		assertThat(analysis.features()).doesNotContain(QueryFeature.OP_AVERAGE);
		assertThat(analysis.shape()).isEqualTo(QueryShape.AGGREGATION);
	}

	@Test
	void ungroupedAggregateIsAWholeSetAggregation() {
		// SQL semantics: an aggregate without groupBy aggregates the whole result set
		QueryFactory factory = QueryFactory.eINSTANCE;
		Query query = factory.createQuery();
		QSubject subject = factory.createQSubject();
		subject.setFeaturePath(path(age));
		subject.setOperation(factory.createAverage());
		query.getSubject().add(subject);

		QueryAnalysis analysis = QueryAnalyzer.analyze(query);
		assertThat(analysis.features()).contains(QueryFeature.AGG_AVG);
		assertThat(analysis.features()).doesNotContain(QueryFeature.OP_AVERAGE, QueryFeature.GROUP_BY);
		assertThat(analysis.shape()).isEqualTo(QueryShape.AGGREGATION);
	}

	@Test
	void allAggregateFunctionsMapToTheirFeatures() {
		QueryFactory factory = QueryFactory.eINSTANCE;
		Query query = factory.createQuery();
		query.getGroupBy().add(path(name));
		record Pair(org.eclipse.fennec.model.query.Operation op, QueryFeature feature) {
		}
		java.util.List<Pair> pairs = java.util.List.of(
				new Pair(factory.createMin(), QueryFeature.AGG_MIN),
				new Pair(factory.createMax(), QueryFeature.AGG_MAX),
				new Pair(factory.createSum(), QueryFeature.AGG_SUM),
				new Pair(factory.createCountOperation(), QueryFeature.AGG_COUNT));
		for (Pair pair : pairs) {
			QSubject subject = factory.createQSubject();
			subject.setFeaturePath(path(age));
			subject.setOperation(pair.op());
			query.getSubject().add(subject);
		}

		QueryAnalysis analysis = QueryAnalyzer.analyze(query);
		assertThat(analysis.features()).contains(QueryFeature.GROUP_BY, QueryFeature.AGG_MIN, QueryFeature.AGG_MAX,
				QueryFeature.AGG_SUM, QueryFeature.AGG_COUNT);
		assertThat(analysis.shape()).isEqualTo(QueryShape.AGGREGATION);
	}

	@Test
	void stringOperationsAreDetected() {
		QueryFactory factory = QueryFactory.eINSTANCE;
		Query query = factory.createQuery();
		And where = eqWhere(name, "smith");
		where.setOperation(factory.createToLowerCase());
		query.getWhere().add(where);

		QSubject subject = factory.createQSubject();
		subject.setFeaturePath(path(name));
		subject.setOperation(factory.createToUpperCase());
		query.getSubject().add(subject);

		assertThat(QueryAnalyzer.analyze(query).features()).contains(QueryFeature.OP_TO_LOWER,
				QueryFeature.OP_TO_UPPER);
	}

	@Test
	void typeFilterIsDetected() {
		QueryFactory factory = QueryFactory.eINSTANCE;
		Query query = factory.createQuery();
		QObject from = factory.createQObject();
		from.setRootEClass(person);
		query.getFrom().add(from);

		assertThat(QueryAnalyzer.analyze(query).features()).contains(QueryFeature.TYPE_FILTER);
	}
}
