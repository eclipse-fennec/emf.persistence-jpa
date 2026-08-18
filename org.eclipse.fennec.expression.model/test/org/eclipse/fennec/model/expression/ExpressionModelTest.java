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
package org.eclipse.fennec.model.expression;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Test;

/**
 * Smoke test for the generated expression model: trees build, containment holds,
 * defaults apply.
 *
 * @author Mark Hoffmann
 */
class ExpressionModelTest {

	private final ExpressionFactory factory = ExpressionFactory.eINSTANCE;

	private EAttribute attribute(String name) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(EcorePackage.Literals.ESTRING);
		return attribute;
	}

	@Test
	void groupedPredicateTreeBuildsWithContainment() {
		// (name = :who OR name CONTAINS-CI "smith") AND age IS NOT NULL — inexpressible in the v1 IR
		Comparison eq = factory.createComparison();
		eq.setOperator(ComparisonOperator.EQ);
		PropertyPath namePath = factory.createPropertyPath();
		namePath.getSegments().add(attribute("name"));
		eq.setLeft(namePath);
		ParameterRef who = factory.createParameterRef();
		who.setName("who");
		eq.setRight(who);

		StringMatch contains = factory.createStringMatch();
		contains.setKind(StringMatchKind.CONTAINS);
		contains.setCaseInsensitive(true);
		PropertyPath namePath2 = factory.createPropertyPath();
		namePath2.getSegments().add(attribute("name"));
		contains.setSource(namePath2);
		StringLiteral smith = factory.createStringLiteral();
		smith.setValue("smith");
		contains.setPattern(smith);

		Or or = factory.createOr();
		or.getOperands().add(eq);
		or.getOperands().add(contains);

		IsNull notNull = factory.createIsNull();
		notNull.setNegated(true);
		PropertyPath agePath = factory.createPropertyPath();
		agePath.getSegments().add(attribute("age"));
		notNull.setSource(agePath);

		And root = factory.createAnd();
		root.getOperands().add(or);
		root.getOperands().add(notNull);

		assertThat(root.getOperands()).hasSize(2);
		assertThat(or.eContainer()).isSameAs(root);
		assertThat(eq.eContainer()).isSameAs(or);
		assertThat(who.eContainer()).isSameAs(eq);
		assertThat(EcoreUtil.isAncestor(root, smith)).isTrue();
	}

	@Test
	void quantifierScopesItsVariable() {
		EClass address = EcoreFactory.eINSTANCE.createEClass();
		address.setName("Address");
		EReference addresses = EcoreFactory.eINSTANCE.createEReference();
		addresses.setName("addresses");
		addresses.setEType(address);
		addresses.setUpperBound(-1);

		Exists exists = factory.createExists();
		PropertyPath source = factory.createPropertyPath();
		source.getSegments().add(addresses);
		exists.setSource(source);
		Variable a = factory.createVariable();
		a.setName("a");
		exists.setVariable(a);

		Comparison predicate = factory.createComparison();
		predicate.setOperator(ComparisonOperator.EQ);
		PropertyPath street = factory.createPropertyPath();
		street.getSegments().add(attribute("street"));
		street.setBase(a);
		predicate.setLeft(street);
		StringLiteral main = factory.createStringLiteral();
		main.setValue("Main St");
		predicate.setRight(main);
		exists.setPredicate(predicate);

		assertThat(exists.getVariable().getName()).isEqualTo("a");
		assertThat(street.getBase()).isSameAs(a);
		assertThat(a.eContainer()).isSameAs(exists);
		assertThat(predicate.eContainer()).isSameAs(exists);
	}

	@Test
	void defaultsAndEnums() {
		assertThat(factory.createIsNull().isNegated()).isFalse();
		assertThat(factory.createStringMatch().isCaseInsensitive()).isFalse();
		// the fuzzy parameters (issue #167) default to Lucene's budget and no exact prefix,
		// and are unsettable so "explicitly configured" stays distinguishable
		assertThat(factory.createStringMatch().getMaxEdits()).isEqualTo(2);
		assertThat(factory.createStringMatch().getPrefixLength()).isZero();
		assertThat(factory.createStringMatch().isSetMaxEdits()).isFalse();
		assertThat(factory.createStringMatch().isSetPrefixLength()).isFalse();
		Between between = factory.createBetween();
		assertThat(between.isLowerIncluded()).isTrue();
		assertThat(between.isUpperIncluded()).isTrue();
		assertThat(ComparisonOperator.values()).hasSize(6);
		assertThat(StringMatchKind.values()).hasSize(5);
		assertThat(TemporalKind.values()).hasSize(4);
	}

	@Test
	void literalsCarryTypedValues() {
		IntegerLiteral i = factory.createIntegerLiteral();
		i.setValue(42L);
		RealLiteral r = factory.createRealLiteral();
		r.setValue(3.14d);
		BooleanLiteral b = factory.createBooleanLiteral();
		b.setValue(true);
		TemporalLiteral t = factory.createTemporalLiteral();
		t.setValue("2026-07-23T10:15:30Z");
		t.setKind(TemporalKind.INSTANT);
		EnumLiteral e = factory.createEnumLiteral();
		e.setLiteralName("RED");

		assertThat(i.getValue()).isEqualTo(42L);
		assertThat(r.getValue()).isEqualTo(3.14d);
		assertThat(b.isValue()).isTrue();
		assertThat(t.getKind()).isEqualTo(TemporalKind.INSTANT);
		assertThat(e.getLiteralName()).isEqualTo("RED");
		assertThat(factory.createNullLiteral()).isInstanceOf(Literal.class);
	}

	@Test
	void packageMetadata() {
		ExpressionPackage pkg = ExpressionPackage.eINSTANCE;
		assertThat(pkg.getNsURI()).isEqualTo("https://eclipse.org/fennec/expression/1.0.0");
		assertThat(pkg.getEClassifiers().size()).isGreaterThanOrEqualTo(27);
	}
}
