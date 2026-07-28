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
import static org.eclipse.fennec.model.query.builder.Expressions.all;
import static org.eclipse.fennec.model.query.builder.Expressions.and;
import static org.eclipse.fennec.model.query.builder.Expressions.any;
import static org.eclipse.fennec.model.query.builder.Expressions.literal;
import static org.eclipse.fennec.model.query.builder.Expressions.not;
import static org.eclipse.fennec.model.query.builder.Expressions.or;
import static org.eclipse.fennec.model.query.builder.Expressions.param;
import static org.eclipse.fennec.model.query.builder.Expressions.path;
import static org.eclipse.fennec.model.query.builder.Expressions.propertyPath;

import java.time.Instant;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.model.expression.And;
import org.eclipse.fennec.model.expression.Between;
import org.eclipse.fennec.model.expression.BooleanLiteral;
import org.eclipse.fennec.model.expression.Comparison;
import org.eclipse.fennec.model.expression.ComparisonOperator;
import org.eclipse.fennec.model.expression.Exists;
import org.eclipse.fennec.model.expression.ForAll;
import org.eclipse.fennec.model.expression.In;
import org.eclipse.fennec.model.expression.IntegerLiteral;
import org.eclipse.fennec.model.expression.IsNull;
import org.eclipse.fennec.model.expression.NullLiteral;
import org.eclipse.fennec.model.expression.Or;
import org.eclipse.fennec.model.expression.ParameterRef;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.model.expression.StringFunction;
import org.eclipse.fennec.model.expression.StringFunctionKind;
import org.eclipse.fennec.model.expression.StringMatch;
import org.eclipse.fennec.model.expression.StringMatchKind;
import org.eclipse.fennec.model.expression.TemporalKind;
import org.eclipse.fennec.model.expression.TemporalLiteral;
import org.eclipse.fennec.model.query.AggregateMethod;
import org.eclipse.fennec.model.query.GroupByStage;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.SortDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests builder v2: composable {@link Expressions} + envelope {@link QueryBuilder}.
 *
 * @author Mark Hoffmann
 */
class QueryBuilderV2Test {

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

	@Test
	void groupedTreeWithQuantifierBuilds() {
		Query query = QueryBuilder.from(person)
				.where(and(
						or(path(name).eq("smith"), path(name).containsIgnoreCase("x")),
						path(age).ge(18),
						not(path(age).isNull()),
						any(propertyPath(addresses), a -> a.path(street).startsWith("Main"))))
				.orderByAsc(age)
				.top(10)
				.skip(5)
				.build();

		assertThat(query.getFrom()).isSameAs(person);
		And root = (And) query.getPredicate();
		assertThat(root.getOperands()).hasSize(4);
		assertThat(root.getOperands().get(0)).isInstanceOf(Or.class);
		assertThat(root.getOperands().get(3)).isInstanceOf(Exists.class);
		assertThat(root.eContainer()).isSameAs(query);

		Exists exists = (Exists) root.getOperands().get(3);
		assertThat(exists.getVariable().getName()).isEqualTo("it");
		PropertyPath inner = (PropertyPath) ((StringMatch) exists.getPredicate()).getSource();
		assertThat(inner.getBase()).isSameAs(exists.getVariable());
		assertThat(EcoreUtil.isAncestor(query, exists.getPredicate())).isTrue();

		assertThat(query.getOrderBy()).hasSize(1);
		assertThat(query.getOrderBy().get(0).getDirection()).isEqualTo(SortDirection.ASC);
		assertThat(query.getTop()).isEqualTo(10);
		assertThat(query.getSkip()).isEqualTo(5);
	}

	@Test
	void comparisonVocabulary() {
		Comparison ne = path(age).ne(65);
		assertThat(ne.getOperator()).isEqualTo(ComparisonOperator.NE);
		assertThat(((IntegerLiteral) ne.getRight()).getValue()).isEqualTo(65L);

		IsNull notNull = path(name).isNotNull();
		assertThat(notNull.isNegated()).isTrue();

		Between between = path(age).between(18, 65, true, false);
		assertThat(between.isLowerIncluded()).isTrue();
		assertThat(between.isUpperIncluded()).isFalse();

		In in = path(age).in(1, 2, param("more"));
		assertThat(in.getValues()).hasSize(3);
		assertThat(in.getValues().get(2)).isInstanceOf(ParameterRef.class);

		StringMatch like = path(name).likeIgnoreCase("sm_th%");
		assertThat(like.getKind()).isEqualTo(StringMatchKind.LIKE);
		assertThat(like.isCaseInsensitive()).isTrue();

		ForAll forAll = all(propertyPath(addresses), a -> a.path(street).isNotNull());
		assertThat(forAll.getPredicate()).isInstanceOf(IsNull.class);
	}

	@Test
	void stringFunctionsAndFieldToField() {
		Comparison lower = path(name).toLower().eq("bob");
		StringFunction function = (StringFunction) lower.getLeft();
		assertThat(function.getKind()).isEqualTo(StringFunctionKind.TO_LOWER);
		assertThat(((PropertyPath) function.getSource()).getSegments()).containsExactly(name);

		Comparison chained = path(name).trim().toUpper().length().gt(3);
		StringFunction length = (StringFunction) chained.getLeft();
		assertThat(length.getKind()).isEqualTo(StringFunctionKind.LENGTH);
		StringFunction upper = (StringFunction) length.getSource();
		assertThat(upper.getKind()).isEqualTo(StringFunctionKind.TO_UPPER);
		assertThat(((StringFunction) upper.getSource()).getKind()).isEqualTo(StringFunctionKind.TRIM);

		// a PathStep on the right unwraps into its PropertyPath (field-to-field)
		Comparison fieldToField = path(name).eq(path(name));
		assertThat(fieldToField.getRight()).isInstanceOf(PropertyPath.class);

		// a FunctionStep on the right unwraps into its function expression
		Comparison functionRight = path(name).eq(path(name).toLower());
		assertThat(functionRight.getRight()).isInstanceOf(StringFunction.class);
	}

	@Test
	void literalBoxing() {
		assertThat(literal(null)).isInstanceOf(NullLiteral.class);
		assertThat(((IntegerLiteral) literal(42)).getValue()).isEqualTo(42L);
		assertThat(((BooleanLiteral) literal(true)).isValue()).isTrue();
		TemporalLiteral instant = (TemporalLiteral) literal(Instant.parse("2026-07-24T10:00:00Z"));
		assertThat(instant.getKind()).isEqualTo(TemporalKind.INSTANT);
		assertThatIllegalArgumentException().isThrownBy(() -> literal(new Object()));
	}

	@Test
	void aggregationHelpersBuildASingleGroupByStage() {
		Query query = QueryBuilder.from(person)
				.groupBy(name)
				.avg("avgAge", age)
				.countOf("cnt")
				.countDistinct("streets", addresses, street)
				.build();

		GroupByStage stage = (GroupByStage) query.getApply().getStages().get(0);
		assertThat(query.getApply().getStages()).hasSize(1);
		assertThat(stage.getPaths()).hasSize(1);
		assertThat(stage.getAggregates()).hasSize(3);
		assertThat(stage.getAggregates().get(1).getMethod()).isEqualTo(AggregateMethod.COUNT);
		assertThat(stage.getAggregates().get(1).getPath()).isNull();
		assertThat(stage.getAggregates().get(2).getMethod()).isEqualTo(AggregateMethod.COUNT_DISTINCT);
		assertThat(stage.getAggregates().get(2).getPath().getSegments()).containsExactly(addresses, street);
	}

	@Test
	void envelopeExtras() {
		Query query = QueryBuilder.from(person)
				.selectAs("n", name)
				.expand(addresses)
				.distinct()
				.parameter("who", EcorePackage.Literals.ESTRING)
				.named("adults")
				.build();

		assertThat(query.getSelect()).hasSize(1);
		assertThat(query.getSelect().get(0).getAlias()).isEqualTo("n");
		assertThat(query.getExpand()).hasSize(1);
		assertThat(query.isDistinct()).isTrue();
		assertThat(query.getParameters()).hasSize(1);
		assertThat(query.getName()).isEqualTo("adults");
		assertThat(query.isSaveQuery()).isTrue();
	}

	@Test
	void invalidArguments() {
		assertThatIllegalArgumentException().isThrownBy(() -> QueryBuilder.from(person).top(0));
		assertThatIllegalArgumentException().isThrownBy(() -> QueryBuilder.from(person).skip(-1));
		assertThatIllegalArgumentException().isThrownBy(() -> and(path(age).eq(1)));
		assertThatIllegalArgumentException().isThrownBy(() -> path(age).in());
		assertThatIllegalArgumentException().isThrownBy(Expressions::propertyPath);
	}
}
