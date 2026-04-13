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
package org.eclipse.fennec.persistence.ecore;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for {@link DatabaseEcoreParser#convertType(String)} — JDBC type to ECore type mapping.
 */
class DatabaseEcoreParserConvertTypeTest {

	@Nested
	class NumericTypes {

		@Test
		void testInt4MapsToEIntegerObject() {
			EDataType result = DatabaseEcoreParser.convertType("int4");
			assertThat(result).isSameAs(EcorePackage.Literals.EINTEGER_OBJECT);
		}

		@Test
		void testFloat8MapsToEBigDecimal() {
			EDataType result = DatabaseEcoreParser.convertType("float8");
			assertThat(result).isSameAs(EcorePackage.Literals.EBIG_DECIMAL);
		}
	}

	@Nested
	class StringTypes {

		@ParameterizedTest
		@ValueSource(strings = {"text", "varchar", "bpchar", "geometry", "xml"})
		void testStringTypesMapToEString(String jdbcType) {
			EDataType result = DatabaseEcoreParser.convertType(jdbcType);
			assertThat(result).isSameAs(EcorePackage.Literals.ESTRING);
		}

		@Test
		void testSerialMapsToEString() {
			EDataType result = DatabaseEcoreParser.convertType("serial");
			assertThat(result).isSameAs(EcorePackage.Literals.ESTRING);
		}
	}

	@Nested
	class BooleanTypes {

		@Test
		void testBoolMapsToEBoolean() {
			EDataType result = DatabaseEcoreParser.convertType("bool");
			assertThat(result).isSameAs(EcorePackage.Literals.EBOOLEAN);
		}
	}

	@Nested
	class FallbackBehavior {

		@Test
		void testUnknownTypeFallsBackToEString() {
			EDataType result = DatabaseEcoreParser.convertType("unknownType");
			assertThat(result).isSameAs(EcorePackage.Literals.ESTRING);
		}

		@Test
		void testEmptyStringFallsBackToEString() {
			EDataType result = DatabaseEcoreParser.convertType("");
			assertThat(result).isSameAs(EcorePackage.Literals.ESTRING);
		}
	}
}
