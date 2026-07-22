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

import java.util.Date;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.model.utilities.FeaturePath;
import org.eclipse.fennec.model.utilities.UtilitiesFactory;
import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.api.TypeConverter;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link QueryValues} typed-value conversion.
 *
 * @author Mark Hoffmann
 */
class QueryValuesTest {

	private EAttribute attribute(String name, EClassifier type) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(type);
		return attribute;
	}

	@Test
	void stringLiteralPassesThrough() {
		EAttribute name = attribute("name", EcorePackage.Literals.ESTRING);
		assertThat(QueryValues.toEmfValue("smith", name)).isEqualTo("smith");
	}

	@Test
	void numericLiteralsAreParsed() {
		assertThat(QueryValues.toEmfValue("42", attribute("age", EcorePackage.Literals.EINT))).isEqualTo(42);
		assertThat(QueryValues.toEmfValue("42", attribute("id", EcorePackage.Literals.ELONG))).isEqualTo(42L);
		assertThat(QueryValues.toEmfValue("3.14", attribute("pi", EcorePackage.Literals.EDOUBLE))).isEqualTo(3.14d);
	}

	@Test
	void booleanLiteralIsParsed() {
		assertThat(QueryValues.toEmfValue("true", attribute("active", EcorePackage.Literals.EBOOLEAN)))
				.isEqualTo(Boolean.TRUE);
	}

	@Test
	void dateLiteralIsParsed() {
		Object value = QueryValues.toEmfValue("2026-07-23T10:15:30.000+0000",
				attribute("created", EcorePackage.Literals.EDATE));
		assertThat(value).isInstanceOf(Date.class);
	}

	@Test
	void enumLiteralIsParsed() {
		EEnum colors = EcoreFactory.eINSTANCE.createEEnum();
		colors.setName("Color");
		EEnumLiteral red = EcoreFactory.eINSTANCE.createEEnumLiteral();
		red.setName("RED");
		red.setValue(0);
		colors.getELiterals().add(red);
		// dynamic enums need a containing package for EFactory-based literal parsing
		EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
		pkg.setName("test");
		pkg.setNsURI("http://test/1.0");
		pkg.getEClassifiers().add(colors);

		Object value = QueryValues.toEmfValue("RED", attribute("color", colors));
		assertThat(value).isEqualTo(red);
	}

	@Test
	void nullLiteralStaysNull() {
		assertThat(QueryValues.toEmfValue(null, attribute("name", EcorePackage.Literals.ESTRING))).isNull();
	}

	@Test
	void unparsableLiteralIsRejectedWithContext() {
		EAttribute age = attribute("age", EcorePackage.Literals.EINT);
		assertThatIllegalArgumentException()
				.isThrownBy(() -> QueryValues.toEmfValue("not-a-number", age))
				.withMessageContaining("not-a-number")
				.withMessageContaining("age");
	}

	@Test
	void referenceTypedFeatureIsRejected() {
		EClass target = EcoreFactory.eINSTANCE.createEClass();
		target.setName("Address");
		EReference reference = EcoreFactory.eINSTANCE.createEReference();
		reference.setName("address");
		reference.setEType(target);

		assertThatIllegalArgumentException()
				.isThrownBy(() -> QueryValues.toEmfValue("x", reference))
				.withMessageContaining("address");
	}

	@Test
	void persistenceConversionWithoutConverterIsIdentity() {
		EAttribute age = attribute("age", EcorePackage.Literals.EINT);
		assertThat(QueryValues.toPersistenceValue(42, age, null)).isEqualTo(42);
	}

	@Test
	void persistenceConversionUsesConverterService() {
		EAttribute age = attribute("age", EcorePackage.Literals.EINT);
		ConverterService converter = converterReturning(new TypeConverter() {
			@Override
			public String getName() {
				return "test";
			}

			@Override
			public boolean isConverterForType(EClassifier eDataType) {
				return true;
			}

			@Override
			public Object convertValueToEMF(EClassifier eDataType, Object value) {
				return value;
			}

			@Override
			public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
				return "converted:" + emfValue;
			}
		});

		assertThat(QueryValues.toPersistenceValue(42, age, converter)).isEqualTo("converted:42");
	}

	@Test
	void persistenceConversionWithoutMatchingConverterIsIdentity() {
		EAttribute age = attribute("age", EcorePackage.Literals.EINT);
		assertThat(QueryValues.toPersistenceValue(42, age, converterReturning(null))).isEqualTo(42);
	}

	@Test
	void convertChainsBothSteps() {
		EAttribute age = attribute("age", EcorePackage.Literals.EINT);
		ConverterService converter = converterReturning(new TypeConverter() {
			@Override
			public String getName() {
				return "test";
			}

			@Override
			public boolean isConverterForType(EClassifier eDataType) {
				return true;
			}

			@Override
			public Object convertValueToEMF(EClassifier eDataType, Object value) {
				return value;
			}

			@Override
			public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
				return ((Integer) emfValue) * 2;
			}
		});

		assertThat(QueryValues.convert("21", age, converter)).isEqualTo(42);
	}

	@Test
	void targetFeatureIsLastPathSegment() {
		EAttribute name = attribute("name", EcorePackage.Literals.ESTRING);
		EAttribute street = attribute("street", EcorePackage.Literals.ESTRING);
		FeaturePath path = UtilitiesFactory.eINSTANCE.createFeaturePath();
		path.getFeature().add(name);
		path.getFeature().add(street);

		assertThat(QueryValues.targetFeature(path)).isSameAs(street);
		assertThat(QueryValues.targetFeature(UtilitiesFactory.eINSTANCE.createFeaturePath())).isNull();
		assertThat(QueryValues.targetFeature(null)).isNull();
	}

	private ConverterService converterReturning(TypeConverter typeConverter) {
		return new ConverterService() {
			@Override
			public TypeConverter getConverter(EClassifier eDataType) {
				return typeConverter;
			}

			@Override
			public TypeConverter getConverter(String name) {
				return typeConverter;
			}
		};
	}
}
