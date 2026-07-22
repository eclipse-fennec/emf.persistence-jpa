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
package org.eclipse.fennec.model.query.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.model.query.And;
import org.eclipse.fennec.model.query.Average;
import org.eclipse.fennec.model.query.Contains;
import org.eclipse.fennec.model.query.CountOperation;
import org.eclipse.fennec.model.query.Eq;
import org.eclipse.fennec.model.query.Gte;
import org.eclipse.fennec.model.query.IsBool;
import org.eclipse.fennec.model.query.IsInRange;
import org.eclipse.fennec.model.query.IsLiteral;
import org.eclipse.fennec.model.query.Max;
import org.eclipse.fennec.model.query.Min;
import org.eclipse.fennec.model.query.Not;
import org.eclipse.fennec.model.query.Or;
import org.eclipse.fennec.model.query.QWhere;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.SortOrder;
import org.eclipse.fennec.model.query.Sum;
import org.eclipse.fennec.model.query.ToLowerCase;
import org.eclipse.fennec.model.query.ToUpperCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the fluent {@link QueryBuilder}.
 *
 * @author Mark Hoffmann
 */
class QueryBuilderTest {

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

	@Test
	void chainedPredicatesProduceTheChainingTypes() {
		Query query = QueryBuilder.create()
				.from(person)
				.where(name).toLower().contains("smith")
				.and(age).gte(18)
				.or(age).eq(65)
				.not(name).like("%test%")
				.build();

		assertThat(query.getFrom()).hasSize(1);
		assertThat(query.getFrom().get(0).getRootEClass()).isSameAs(person);
		assertThat(query.getWhere()).hasSize(4);

		QWhere first = query.getWhere().get(0);
		assertThat(first).isInstanceOf(And.class);
		assertThat(first.getOperation()).isInstanceOf(ToLowerCase.class);
		assertThat(first.getComparator()).isInstanceOf(Contains.class);
		assertThat(((Contains) first.getComparator()).getValue()).isEqualTo("smith");
		assertThat(first.getFeaturePath().getFeature()).containsExactly(name);

		QWhere second = query.getWhere().get(1);
		assertThat(second).isInstanceOf(And.class);
		assertThat(second.getComparator()).isInstanceOf(Gte.class);
		assertThat(((Gte) second.getComparator()).getValue()).isEqualTo("18");

		assertThat(query.getWhere().get(2)).isInstanceOf(Or.class);
		assertThat(((Eq) query.getWhere().get(2).getComparator()).getValue()).isEqualTo("65");
		assertThat(query.getWhere().get(3)).isInstanceOf(Not.class);
	}

	@Test
	void nestedPathsAreBuiltInOrder() {
		Query query = QueryBuilder.create()
				.where(address, street).eq("Main St")
				.build();

		assertThat(query.getWhere().get(0).getFeaturePath().getFeature()).containsExactly(address, street);
	}

	@Test
	void parameterAndRangeAndEnumAndBool() {
		Query query = QueryBuilder.create()
				.where(name).eqParam("who")
				.and(age).inRange(18, 65, true, false)
				.and(name).isLiteral("RED")
				.and(name).isBool(true)
				.build();

		assertThat(((Eq) query.getWhere().get(0).getComparator()).getValue()).isEqualTo(":who");

		IsInRange range = (IsInRange) query.getWhere().get(1).getComparator();
		assertThat(range.getStartValue()).isEqualTo("18");
		assertThat(range.getEndValue()).isEqualTo("65");
		assertThat(range.isStartIncluded()).isTrue();
		assertThat(range.isEndIncluded()).isFalse();

		assertThat(query.getWhere().get(2).getComparator()).isInstanceOf(IsLiteral.class);
		assertThat(((IsBool) query.getWhere().get(3).getComparator()).getValue()).isEqualTo("true");
	}

	@Test
	void subjectsAliasesAndAggregates() {
		Query query = QueryBuilder.create()
				.selectAs("personName", name)
				.avg("avgAge", age)
				.min("minAge", age)
				.max("maxAge", age)
				.sum("sumAge", age)
				.countOf("cnt", age)
				.toLower(null, name)
				.toUpper(null, name)
				.groupBy(name)
				.build();

		assertThat(query.getSubject()).hasSize(8);
		assertThat(query.getSubject().get(0).getAlias()).isEqualTo("personName");
		assertThat(query.getSubject().get(0).getOperation()).isNull();
		assertThat(query.getSubject().get(1).getOperation()).isInstanceOf(Average.class);
		assertThat(query.getSubject().get(2).getOperation()).isInstanceOf(Min.class);
		assertThat(query.getSubject().get(3).getOperation()).isInstanceOf(Max.class);
		assertThat(query.getSubject().get(4).getOperation()).isInstanceOf(Sum.class);
		assertThat(query.getSubject().get(5).getOperation()).isInstanceOf(CountOperation.class);
		assertThat(query.getSubject().get(6).getOperation()).isInstanceOf(ToLowerCase.class);
		assertThat(query.getSubject().get(7).getOperation()).isInstanceOf(ToUpperCase.class);
		assertThat(query.getGroupBy()).hasSize(1);
		assertThat(query.getGroupBy().get(0).getFeature()).containsExactly(name);
	}

	@Test
	void shapingFlagsAndNaming() {
		Query query = QueryBuilder.create()
				.sortBy(age, SortOrder.ASC)
				.limit(10)
				.skip(5)
				.distinct()
				.count()
				.named("adults")
				.build();

		assertThat(query.getSortBy()).hasSize(1);
		assertThat(query.getSortBy().get(0).getSortFeature()).isSameAs(age);
		assertThat(query.getSortBy().get(0).getSortOrder()).isEqualTo(SortOrder.ASC);
		assertThat(query.getLimit()).isEqualTo(10);
		assertThat(query.getSkip()).isEqualTo(5);
		assertThat(query.isDistinct()).isTrue();
		assertThat(query.isCount()).isTrue();
		assertThat(query.getName()).isEqualTo("adults");
		assertThat(query.isSaveQuery()).isTrue();
	}

	@Test
	void invalidArgumentsAreRejected() {
		assertThatIllegalArgumentException().isThrownBy(() -> QueryBuilder.create().where().eq("x"));
		assertThatIllegalArgumentException().isThrownBy(() -> QueryBuilder.create().select());
		assertThatIllegalArgumentException().isThrownBy(() -> QueryBuilder.create().limit(0));
		assertThatIllegalArgumentException().isThrownBy(() -> QueryBuilder.create().skip(-1));
	}

	@Test
	void defaultSortOrderIsDescending() {
		Query query = QueryBuilder.create().sortBy(age, null).build();
		assertThat(query.getSortBy().get(0).getSortOrder()).isEqualTo(SortOrder.DESC);
	}
}
