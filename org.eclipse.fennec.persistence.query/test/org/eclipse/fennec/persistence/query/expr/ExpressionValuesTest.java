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
package org.eclipse.fennec.persistence.query.expr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Date;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.model.expression.EnumLiteral;
import org.eclipse.fennec.model.expression.ExpressionFactory;
import org.eclipse.fennec.model.expression.IntegerLiteral;
import org.eclipse.fennec.model.expression.ParameterRef;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.model.expression.TemporalKind;
import org.eclipse.fennec.model.expression.TemporalLiteral;
import org.eclipse.fennec.persistence.query.QueryException;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link ExpressionValues} typed resolution.
 *
 * @author Mark Hoffmann
 */
class ExpressionValuesTest {

	private final ExpressionFactory expr = ExpressionFactory.eINSTANCE;

	private EAttribute attribute(String name, EClassifier type) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(type);
		return attribute;
	}

	@Test
	void integerLiteralNarrowsToTargetType() throws QueryException {
		IntegerLiteral literal = expr.createIntegerLiteral();
		literal.setValue(42L);
		assertThat(ExpressionValues.resolve(literal, attribute("age", EcorePackage.Literals.EINT), null, null))
				.isEqualTo(42);
		assertThat(ExpressionValues.resolve(literal, attribute("id", EcorePackage.Literals.ELONG), null, null))
				.isEqualTo(42L);
		assertThat(ExpressionValues.resolve(literal, attribute("x", EcorePackage.Literals.EDOUBLE), null, null))
				.isEqualTo(42.0d);
	}

	@Test
	void enumLiteralResolvesAgainstTargetEnum() throws QueryException {
		EEnum colors = EcoreFactory.eINSTANCE.createEEnum();
		colors.setName("Color");
		EEnumLiteral red = EcoreFactory.eINSTANCE.createEEnumLiteral();
		red.setName("RED");
		colors.getELiterals().add(red);

		EnumLiteral literal = expr.createEnumLiteral();
		literal.setLiteralName("RED");
		Object resolved = ExpressionValues.resolve(literal, attribute("color", colors), null, null);
		assertThat(resolved).isEqualTo(red);

		EnumLiteral unknown = expr.createEnumLiteral();
		unknown.setLiteralName("PURPLE");
		assertThatThrownBy(() -> ExpressionValues.resolve(unknown, attribute("color", colors), null, null))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("PURPLE");
	}

	@Test
	void temporalLiteralAdaptsToTargetClass() throws QueryException {
		TemporalLiteral instant = expr.createTemporalLiteral();
		instant.setKind(TemporalKind.INSTANT);
		instant.setValue("2026-07-23T10:15:30Z");
		Object asDate = ExpressionValues.resolve(instant, attribute("created", EcorePackage.Literals.EDATE),
				null, null);
		assertThat(asDate).isInstanceOf(Date.class);

		TemporalLiteral bad = expr.createTemporalLiteral();
		bad.setKind(TemporalKind.DATE);
		bad.setValue("not-a-date");
		assertThatThrownBy(() -> ExpressionValues.resolve(bad, null, null, null))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("not-a-date");
	}

	@Test
	void parameterRefResolvesFromBindings() throws QueryException {
		ParameterRef ref = expr.createParameterRef();
		ref.setName("who");
		assertThat(ExpressionValues.resolve(ref, attribute("name", EcorePackage.Literals.ESTRING),
				Map.of("who", "smith"), null)).isEqualTo("smith");

		assertThatThrownBy(() -> ExpressionValues.resolve(ref, null, Map.of(), null))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("who");
	}

	@Test
	void nonValueExpressionIsRejected() {
		PropertyPath path = expr.createPropertyPath();
		path.getSegments().add(attribute("name", EcorePackage.Literals.ESTRING));
		assertThatThrownBy(() -> ExpressionValues.resolve(path, null, null, null))
				.isInstanceOf(QueryException.class);
	}

	@Test
	void nullLiteralAndTargetFeature() throws QueryException {
		assertThat(ExpressionValues.resolve(expr.createNullLiteral(), null, null, null)).isNull();

		PropertyPath path = expr.createPropertyPath();
		EAttribute name = attribute("name", EcorePackage.Literals.ESTRING);
		path.getSegments().add(name);
		assertThat(ExpressionValues.targetFeature(path)).isSameAs(name);
		assertThat(ExpressionValues.targetFeature(null)).isNull();
	}
}
