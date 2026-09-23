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
			// java.time types, backed by the core converters (#295)
			assertThat(DatabaseEcoreParser.mapType(Types.DATE).javaType()).isEqualTo("java.time.LocalDate");
			assertThat(DatabaseEcoreParser.mapType(Types.TIME).javaType()).isEqualTo("java.time.LocalTime");
			assertThat(DatabaseEcoreParser.mapType(Types.TIMESTAMP).javaType()).isEqualTo("java.time.LocalDateTime");
			assertThat(DatabaseEcoreParser.mapType(Types.TIMESTAMP_WITH_TIMEZONE).javaType())
					.isEqualTo("java.time.OffsetDateTime");
			// no converter for an offset time: EDate as before, but reported
			DatabaseEcoreParser.TypeMapping timeTz = DatabaseEcoreParser.mapType(Types.TIME_WITH_TIMEZONE);
			assertThat(timeTz.type()).isEqualTo(EcorePackage.Literals.EDATE);
			assertThat(timeTz.problem()).contains("TIME WITH TIME ZONE");
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
	@DisplayName("mapType — precision, scale and type name (issue #295)")
	class RefinedTypeTests {

		private DatabaseEcoreParser.TypeMapping map(int jdbcType, String typeName, int size, int digits) {
			return DatabaseEcoreParser.mapType(new DatabaseEcoreParser.ColumnType(jdbcType, typeName, size, digits));
		}

		@Test void testUuidByTypeName() {
			// PostgreSQL reports OTHER, H2 BINARY — both are uuid by name
			assertThat(map(Types.OTHER, "uuid", 2147483647, 0).javaType()).isEqualTo("java.util.UUID");
			assertThat(map(Types.BINARY, "UUID", 16, 0).javaType()).isEqualTo("java.util.UUID");
			assertThat(map(Types.BINARY, "BINARY", 16, 0).type()).isEqualTo(EcorePackage.Literals.EBYTE_ARRAY);
		}

		@Test void testUnsignedIntegersWiden() {
			assertThat(map(Types.BIGINT, "BIGINT UNSIGNED", 20, 0).type()).isEqualTo(EcorePackage.Literals.EBIG_INTEGER);
			assertThat(map(Types.INTEGER, "INT UNSIGNED", 10, 0).type()).isEqualTo(EcorePackage.Literals.ELONG_OBJECT);
			assertThat(map(Types.SMALLINT, "SMALLINT UNSIGNED", 5, 0).type())
					.isEqualTo(EcorePackage.Literals.EINTEGER_OBJECT);
			assertThat(map(Types.BIGINT, "BIGINT", 19, 0).type()).isEqualTo(EcorePackage.Literals.ELONG_OBJECT);
		}

		@Test void testDecimalWithoutScaleIsIntegral() {
			assertThat(map(Types.DECIMAL, "DECIMAL", 9, 0).type()).isEqualTo(EcorePackage.Literals.ELONG_OBJECT);
			assertThat(map(Types.NUMERIC, "NUMERIC", 18, 0).type()).isEqualTo(EcorePackage.Literals.ELONG_OBJECT);
			assertThat(map(Types.NUMERIC, "NUMERIC", 30, 0).type()).isEqualTo(EcorePackage.Literals.EBIG_INTEGER);
			assertThat(map(Types.DECIMAL, "DECIMAL", 10, 2).type()).isEqualTo(EcorePackage.Literals.EBIG_DECIMAL);
		}

		@Test void testUnconstrainedDecimalKeepsBigDecimal() {
			// H2 DECIMAL without precision reports 100000, PostgreSQL numeric 131089 — neither is integral
			assertThat(map(Types.DECIMAL, "DECIMAL", 100000, 0).type()).isEqualTo(EcorePackage.Literals.EBIG_DECIMAL);
			assertThat(map(Types.NUMERIC, "numeric", 131089, 0).type()).isEqualTo(EcorePackage.Literals.EBIG_DECIMAL);
			assertThat(map(Types.NUMERIC, "NUMBER", 0, 0).type()).isEqualTo(EcorePackage.Literals.EBIG_DECIMAL);
		}
	}

	@Nested
	@DisplayName("Column defaults (issue #295)")
	class DefaultLiteralTests {

		@Test void testStringLiterals() {
			assertThat(DatabaseEcoreParser.defaultLiteral("'NEW'", EcorePackage.Literals.ESTRING)).isEqualTo("NEW");
			assertThat(DatabaseEcoreParser.defaultLiteral("'it''s'", EcorePackage.Literals.ESTRING)).isEqualTo("it's");
			assertThat(DatabaseEcoreParser.defaultLiteral("'NEW'::character varying", EcorePackage.Literals.ESTRING))
					.isEqualTo("NEW");
			assertThat(DatabaseEcoreParser.defaultLiteral("('NEW'::text)", EcorePackage.Literals.ESTRING))
					.isEqualTo("NEW");
		}

		@Test void testNumericAndBooleanLiterals() {
			assertThat(DatabaseEcoreParser.defaultLiteral("0", EcorePackage.Literals.EINTEGER_OBJECT)).isEqualTo("0");
			assertThat(DatabaseEcoreParser.defaultLiteral("-1", EcorePackage.Literals.ELONG_OBJECT)).isEqualTo("-1");
			assertThat(DatabaseEcoreParser.defaultLiteral("((-1))", EcorePackage.Literals.ELONG_OBJECT)).isEqualTo("-1");
			assertThat(DatabaseEcoreParser.defaultLiteral("1.5", EcorePackage.Literals.EBIG_DECIMAL)).isEqualTo("1.5");
			assertThat(DatabaseEcoreParser.defaultLiteral("TRUE", EcorePackage.Literals.EBOOLEAN_OBJECT)).isEqualTo("true");
		}

		@Test void testLiteralOfTheWrongTypeIsNoDefault() {
			assertThat(DatabaseEcoreParser.defaultLiteral("1.5", EcorePackage.Literals.EINTEGER_OBJECT)).isNull();
			assertThat(DatabaseEcoreParser.defaultLiteral("'7'", EcorePackage.Literals.EINTEGER_OBJECT)).isNull();
			assertThat(DatabaseEcoreParser.defaultLiteral("0", EcorePackage.Literals.ESTRING)).isNull();
		}

		@Test void testExpressionsAreNoLiterals() {
			assertThat(DatabaseEcoreParser.defaultLiteral("CURRENT_TIMESTAMP", EcorePackage.Literals.EDATE)).isNull();
			assertThat(DatabaseEcoreParser.defaultLiteral("nextval('seq'::regclass)", EcorePackage.Literals.ELONG_OBJECT))
					.isNull();
			assertThat(DatabaseEcoreParser.defaultLiteral("now()", EcorePackage.Literals.ESTRING)).isNull();
		}
	}

	@Nested
	@DisplayName("Plural and enum names (issue #295)")
	class NameRuleTests {

		@Test void testPluralize() {
			assertThat(DatabaseEcoreParser.pluralize("order")).isEqualTo("orders");
			assertThat(DatabaseEcoreParser.pluralize("orders")).isEqualTo("orders");
			assertThat(DatabaseEcoreParser.pluralize("address")).isEqualTo("addresses");
			assertThat(DatabaseEcoreParser.pluralize("status")).isEqualTo("statuses");
			assertThat(DatabaseEcoreParser.pluralize("category")).isEqualTo("categories");
			assertThat(DatabaseEcoreParser.pluralize("day")).isEqualTo("days");
			assertThat(DatabaseEcoreParser.pluralize("box")).isEqualTo("boxes");
			assertThat(DatabaseEcoreParser.pluralize("branch")).isEqualTo("branches");
			assertThat(DatabaseEcoreParser.pluralize("analysis")).isEqualTo("analyses");
			assertThat(DatabaseEcoreParser.pluralize("orderLine")).isEqualTo("orderLines");
		}

		@Test void testInlineEnumValues() {
			assertThat(DatabaseEcoreParser.inlineEnumValues("ENUM('A', 'B')")).containsExactly("A", "B");
			assertThat(DatabaseEcoreParser.inlineEnumValues("enum('new','it''s')")).containsExactly("new", "it's");
			assertThat(DatabaseEcoreParser.inlineEnumValues("CHARACTER VARYING")).isNull();
			assertThat(DatabaseEcoreParser.inlineEnumValues(null)).isNull();
		}

		@Test void testEnumLiteralName() {
			assertThat(DatabaseEcoreParser.enumLiteralName("in progress")).isEqualTo("IN_PROGRESS");
			assertThat(DatabaseEcoreParser.enumLiteralName("1st")).isEqualTo("_1ST");
			assertThat(DatabaseEcoreParser.enumLiteralName("")).isEqualTo("_");
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
