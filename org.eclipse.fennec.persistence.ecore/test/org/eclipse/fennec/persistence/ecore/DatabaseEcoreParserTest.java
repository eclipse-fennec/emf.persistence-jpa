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

import java.sql.Types;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DatabaseEcoreParserTest {

	@Nested
	@DisplayName("convertType — JDBC type code to EDataType")
	class ConvertTypeTests {

		@Test void testIntegerTypes() {
			assertThat(DatabaseEcoreParser.convertType(Types.TINYINT)).isEqualTo(EcorePackage.Literals.ESHORT_OBJECT);
			assertThat(DatabaseEcoreParser.convertType(Types.SMALLINT)).isEqualTo(EcorePackage.Literals.ESHORT_OBJECT);
			assertThat(DatabaseEcoreParser.convertType(Types.INTEGER)).isEqualTo(EcorePackage.Literals.EINTEGER_OBJECT);
			assertThat(DatabaseEcoreParser.convertType(Types.BIGINT)).isEqualTo(EcorePackage.Literals.ELONG_OBJECT);
		}

		@Test void testFloatingPointTypes() {
			assertThat(DatabaseEcoreParser.convertType(Types.FLOAT)).isEqualTo(EcorePackage.Literals.EFLOAT_OBJECT);
			assertThat(DatabaseEcoreParser.convertType(Types.REAL)).isEqualTo(EcorePackage.Literals.EFLOAT_OBJECT);
			assertThat(DatabaseEcoreParser.convertType(Types.DOUBLE)).isEqualTo(EcorePackage.Literals.EDOUBLE_OBJECT);
		}

		@Test void testExactNumeric() {
			assertThat(DatabaseEcoreParser.convertType(Types.DECIMAL)).isEqualTo(EcorePackage.Literals.EBIG_DECIMAL);
			assertThat(DatabaseEcoreParser.convertType(Types.NUMERIC)).isEqualTo(EcorePackage.Literals.EBIG_DECIMAL);
		}

		@Test void testBoolean() {
			assertThat(DatabaseEcoreParser.convertType(Types.BOOLEAN)).isEqualTo(EcorePackage.Literals.EBOOLEAN_OBJECT);
			assertThat(DatabaseEcoreParser.convertType(Types.BIT)).isEqualTo(EcorePackage.Literals.EBOOLEAN_OBJECT);
		}

		@Test void testStringTypes() {
			assertThat(DatabaseEcoreParser.convertType(Types.VARCHAR)).isEqualTo(EcorePackage.Literals.ESTRING);
			assertThat(DatabaseEcoreParser.convertType(Types.CHAR)).isEqualTo(EcorePackage.Literals.ESTRING);
			assertThat(DatabaseEcoreParser.convertType(Types.CLOB)).isEqualTo(EcorePackage.Literals.ESTRING);
		}

		@Test void testDateTimeTypes() {
			assertThat(DatabaseEcoreParser.convertType(Types.DATE)).isEqualTo(EcorePackage.Literals.EDATE);
			assertThat(DatabaseEcoreParser.convertType(Types.TIMESTAMP)).isEqualTo(EcorePackage.Literals.EDATE);
		}

		@Test void testBinaryTypes() {
			assertThat(DatabaseEcoreParser.convertType(Types.BLOB)).isEqualTo(EcorePackage.Literals.EBYTE_ARRAY);
			assertThat(DatabaseEcoreParser.convertType(Types.BINARY)).isEqualTo(EcorePackage.Literals.EBYTE_ARRAY);
		}

		@Test void testUnknownFallback() {
			assertThat(DatabaseEcoreParser.convertType(Types.OTHER)).isEqualTo(EcorePackage.Literals.ESTRING);
			assertThat(DatabaseEcoreParser.convertType(99999)).isEqualTo(EcorePackage.Literals.ESTRING);
		}
	}

	@Nested
	@DisplayName("mapType — fallback reporting (issue #19)")
	class MapTypeTests {

		@Test void testCleanMappingHasNoProblem() {
			DatabaseEcoreParser.TypeMapping mapping = DatabaseEcoreParser.mapType(Types.INTEGER);
			assertThat(mapping.type()).isEqualTo(EcorePackage.Literals.EINTEGER_OBJECT);
			assertThat(mapping.problem()).isNull();
		}

		@Test void testUnmappedJdbcTypeReportsProblem() {
			DatabaseEcoreParser.TypeMapping mapping = DatabaseEcoreParser.mapType(Types.OTHER);
			assertThat(mapping.type()).isEqualTo(EcorePackage.Literals.ESTRING);
			assertThat(mapping.problem()).contains("Unmapped JDBC type OTHER");
		}

		@Test void testUnknownTypeCodeReportsProblem() {
			DatabaseEcoreParser.TypeMapping mapping = DatabaseEcoreParser.mapType(99999);
			assertThat(mapping.type()).isEqualTo(EcorePackage.Literals.ESTRING);
			assertThat(mapping.problem()).contains("Unknown JDBC type code 99999");
		}
	}

	@Nested
	@DisplayName("Naming transformation")
	class NamingTests {

		@Test void testSnakeToPascalCase() {
			assertThat(DatabaseEcoreParser.snakeToPascalCase("USER_ACCOUNT")).isEqualTo("UserAccount");
			assertThat(DatabaseEcoreParser.snakeToPascalCase("first_name")).isEqualTo("FirstName");
			assertThat(DatabaseEcoreParser.snakeToPascalCase("ID")).isEqualTo("Id");
			assertThat(DatabaseEcoreParser.snakeToPascalCase("a")).isEqualTo("A");
			assertThat(DatabaseEcoreParser.snakeToPascalCase("")).isEqualTo("");
			assertThat(DatabaseEcoreParser.snakeToPascalCase(null)).isNull();
		}

		@Test void testSnakeToCamelCase() {
			assertThat(DatabaseEcoreParser.snakeToCamelCase("USER_ACCOUNT")).isEqualTo("userAccount");
			assertThat(DatabaseEcoreParser.snakeToCamelCase("FIRST_NAME")).isEqualTo("firstName");
			assertThat(DatabaseEcoreParser.snakeToCamelCase("ID")).isEqualTo("id");
			assertThat(DatabaseEcoreParser.snakeToCamelCase("a")).isEqualTo("a");
		}
	}

	@Nested
	@DisplayName("Static helpers")
	class HelperTests {

		@Test void testAddAttributeRequired() {
			EClass ec = EcoreFactory.eINSTANCE.createEClass();
			DatabaseEcoreParser.addAttribute(ec, "name", EcorePackage.Literals.ESTRING, false, true);
			EAttribute attr = (EAttribute) ec.getEStructuralFeatures().get(0);
			assertThat(attr.getLowerBound()).isEqualTo(1);
		}

		@Test void testCreatePackage() {
			EPackage pkg = DatabaseEcoreParser.createPackage("test", "t", "http://test/1.0");
			assertThat(pkg.getName()).isEqualTo("test");
			assertThat(pkg.getNsURI()).isEqualTo("http://test/1.0");
		}

		@Test void testSetOpposite() {
			EClass a = EcoreFactory.eINSTANCE.createEClass();
			EClass b = EcoreFactory.eINSTANCE.createEClass();
			var ref1 = DatabaseEcoreParser.addReference(a, "b", b);
			var ref2 = DatabaseEcoreParser.addManyReference(b, "as", a);
			DatabaseEcoreParser.setOpposite(ref1, ref2);
			assertThat(ref1.getEOpposite()).isSameAs(ref2);
			assertThat(ref2.getEOpposite()).isSameAs(ref1);
		}
	}
}
