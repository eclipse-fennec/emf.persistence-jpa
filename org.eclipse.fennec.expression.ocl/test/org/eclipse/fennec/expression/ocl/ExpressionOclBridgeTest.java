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
import static org.eclipse.fennec.model.query.builder.Expressions.div;
import static org.eclipse.fennec.model.query.builder.Expressions.mod;
import static org.eclipse.fennec.model.query.builder.Expressions.mul;
import static org.eclipse.fennec.model.query.builder.Expressions.neg;
import static org.eclipse.fennec.model.query.builder.Expressions.or;
import static org.eclipse.fennec.model.query.builder.Expressions.param;
import static org.eclipse.fennec.model.query.builder.Expressions.path;
import static org.eclipse.fennec.model.query.builder.Expressions.propertyPath;

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
import org.eclipse.fennec.m2x.model.ocl.StringLiteralExp;
import org.eclipse.fennec.model.expression.Expression;
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
