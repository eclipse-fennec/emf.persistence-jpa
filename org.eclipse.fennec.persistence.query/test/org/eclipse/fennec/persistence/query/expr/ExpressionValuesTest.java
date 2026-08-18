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

import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.converter.DefaultConverterService;
import org.eclipse.fennec.model.expression.DurationLiteral;
import org.eclipse.fennec.model.expression.EnumLiteral;
import org.eclipse.fennec.model.expression.ExpressionFactory;
import org.eclipse.fennec.model.expression.GuidLiteral;
import org.eclipse.fennec.model.expression.IntegerLiteral;
import org.eclipse.fennec.model.expression.ParameterRef;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.model.expression.StringLiteral;
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
	void stringLiteralCoercesAgainstEnumTypedFeature() throws QueryException {
		// OData transports enum values as quoted strings (issue #93)
		EEnum colors = EcoreFactory.eINSTANCE.createEEnum();
		colors.setName("Color");
		EEnumLiteral green = EcoreFactory.eINSTANCE.createEEnumLiteral();
		green.setName("GREEN");
		colors.getELiterals().add(green);

		StringLiteral literal = expr.createStringLiteral();
		literal.setValue("GREEN");
		assertThat(ExpressionValues.resolve(literal, attribute("color", colors), null, null))
				.isEqualTo(green);
		// without an enum-typed target the string stays a string
		assertThat(ExpressionValues.resolve(literal, attribute("name", EcorePackage.Literals.ESTRING), null, null))
				.isEqualTo("GREEN");
		assertThat(ExpressionValues.resolve(literal, null, null, null)).isEqualTo("GREEN");

		StringLiteral unknown = expr.createStringLiteral();
		unknown.setValue("PURPLE");
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
	void guidLiteralResolvesAgainstTargetType() throws QueryException {
		GuidLiteral guid = expr.createGuidLiteral();
		guid.setValue("123e4567-e89b-12d3-a456-426614174000");

		// no/typed target → UUID (the OData coercion)
		Object asUuid = ExpressionValues.resolve(guid, null, null, null);
		assertThat(asUuid).isEqualTo(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));

		// String-typed feature → canonical text
		Object asText = ExpressionValues.resolve(guid, attribute("id", EcorePackage.Literals.ESTRING),
				null, null);
		assertThat(asText).isEqualTo("123e4567-e89b-12d3-a456-426614174000");

		GuidLiteral bad = expr.createGuidLiteral();
		bad.setValue("not-a-guid");
		assertThatThrownBy(() -> ExpressionValues.resolve(bad, null, null, null))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("not-a-guid");
	}

	@Test
	void durationLiteralResolvesAgainstTargetType() throws QueryException {
		DurationLiteral duration = expr.createDurationLiteral();
		duration.setIso8601("PT1H30M");

		Object asDuration = ExpressionValues.resolve(duration, null, null, null);
		assertThat(asDuration).isEqualTo(Duration.ofMinutes(90));

		// Long-typed feature → milliseconds (the JPA column form)
		Object asMillis = ExpressionValues.resolve(duration,
				attribute("timeout", EcorePackage.Literals.ELONG_OBJECT), null, null);
		assertThat(asMillis).isEqualTo(5_400_000L);

		Object asText = ExpressionValues.resolve(duration,
				attribute("timeout", EcorePackage.Literals.ESTRING), null, null);
		assertThat(asText).isEqualTo("PT1H30M");

		DurationLiteral bad = expr.createDurationLiteral();
		bad.setIso8601("90 minutes");
		assertThatThrownBy(() -> ExpressionValues.resolve(bad, null, null, null))
				.isInstanceOf(QueryException.class)
				.hasMessageContaining("90 minutes");
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

	private EDataType uuidType() {
		EDataType uuidType = EcoreFactory.eINSTANCE.createEDataType();
		uuidType.setName("UUID");
		uuidType.setInstanceClass(UUID.class);
		return uuidType;
	}

	/**
	 * The conversion path over a real {@link ConverterService} (issue #164 — dead until the
	 * backends handed one in): a claimed type converts to its persistence form, an unclaimed
	 * plain type passes through as identity. The nullable {@code getConverter} contract is
	 * exactly this distinction — absence means identity, never an exception.
	 */
	@Test
	void converterServiceConvertsClaimedTypesAndPassesPlainOnes() {
		ConverterService converters = new DefaultConverterService();
		UUID id = UUID.randomUUID();
		assertThat(ExpressionValues.toPersistenceValue(id, attribute("uid", uuidType()), converters))
				.isEqualTo(id.toString());
		assertThat(ExpressionValues.toPersistenceValue("smith",
				attribute("name", EcorePackage.Literals.ESTRING), converters)).isEqualTo("smith");
		assertThat(ExpressionValues.toPersistenceValue(42,
				attribute("age", EcorePackage.Literals.EINT), converters)).isEqualTo(42);
	}

	/** A bound parameter takes the same conversion road as a literal (issue #164). */
	@Test
	void parameterValueIsConvertedThroughTheService() throws QueryException {
		ParameterRef ref = expr.createParameterRef();
		ref.setName("id");
		UUID id = UUID.randomUUID();
		assertThat(ExpressionValues.resolve(ref, attribute("uid", uuidType()),
				Map.of("id", id), new DefaultConverterService())).isEqualTo(id.toString());
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
