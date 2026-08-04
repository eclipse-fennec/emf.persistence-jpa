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

import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for {@link DatabaseEcoreParser} against H2 in-memory database.
 */
class DatabaseEcoreParserIntegrationTest {

	private JdbcDataSource ds;
	private DatabaseEcoreParser parser;

	@BeforeEach
	void setUp() {
		ds = new JdbcDataSource();
		ds.setURL("jdbc:h2:mem:parsetest;DB_CLOSE_DELAY=-1");
		parser = new DatabaseEcoreParser();
		parser.datasource = ds;
	}

	@AfterEach
	void tearDown() throws SQLException {
		try (Connection con = ds.getConnection(); Statement stmt = con.createStatement()) {
			stmt.execute("DROP ALL OBJECTS");
		}
	}

	private void activate(boolean includeViews, boolean transformNames, String... schemas) {
		parser.activate(new DatabaseEcoreParser.DatabaseParserConfig() {
			@Override public String packageName() { return "testpkg"; }
			@Override public String uriPrefix() { return "http://test"; }
			@Override public String version() { return "1.0"; }
			@Override public String[] schemas() { return schemas; }
			@Override public boolean includeViews() { return includeViews; }
			@Override public boolean transformNames() { return transformNames; }
			@Override public Class<? extends Annotation> annotationType() { return DatabaseEcoreParser.DatabaseParserConfig.class; }
		});
	}

	private void activateDefault() {
		activate(false, true);
	}

	private void executeSql(String... sqls) throws SQLException {
		try (Connection con = ds.getConnection(); Statement stmt = con.createStatement()) {
			for (String sql : sqls) {
				stmt.execute(sql);
			}
		}
	}

	private EClass findClass(EPackage pkg, String name) {
		return pkg.getEClassifiers().stream()
				.filter(EClass.class::isInstance)
				.map(EClass.class::cast)
				.filter(c -> c.getName().equals(name))
				.findFirst().orElse(null);
	}

	private EReference findReference(EClass eClass, String name) {
		return eClass.getEStructuralFeatures().stream()
				.filter(EReference.class::isInstance)
				.map(EReference.class::cast)
				.filter(r -> r.getName().equals(name))
				.findFirst().orElse(null);
	}

	// --- Basic parsing ---

	@Nested
	@DisplayName("Basic table/column parsing")
	class BasicTests {

		@Test void testEmptySchema() throws SQLException {
			activateDefault();
			EPackage pkg = parser.parse();
			assertThat(pkg.getEClassifiers()).isEmpty();
		}

		@Test void testSingleTableWithNaming() throws SQLException {
			executeSql("CREATE TABLE USER_ACCOUNT (ID INTEGER PRIMARY KEY, FIRST_NAME VARCHAR(100) NOT NULL, AGE INTEGER)");
			activateDefault();
			EPackage pkg = parser.parse();

			// Table name → PascalCase
			EClass userAccount = findClass(pkg, "UserAccount");
			assertThat(userAccount).isNotNull();

			// Column names → camelCase
			assertThat(userAccount.getEStructuralFeature("id")).isNotNull();
			assertThat(userAccount.getEStructuralFeature("firstName")).isNotNull();
			assertThat(userAccount.getEStructuralFeature("age")).isNotNull();

			// NOT NULL → required
			assertThat(userAccount.getEStructuralFeature("firstName").getLowerBound()).isEqualTo(1);
			assertThat(userAccount.getEStructuralFeature("age").getLowerBound()).isEqualTo(0);
		}

		@Test void testNamingDisabled() throws SQLException {
			executeSql("CREATE TABLE USER_ACCOUNT (FIRST_NAME VARCHAR(100))");
			activate(false, false);
			EPackage pkg = parser.parse();

			// Names kept as-is
			EClass ua = findClass(pkg, "USER_ACCOUNT");
			assertThat(ua).isNotNull();
			assertThat(ua.getEStructuralFeature("FIRST_NAME")).isNotNull();
		}

		@Test void testTypeMappingEndToEnd() throws SQLException {
			executeSql("CREATE TABLE TYPES_TEST (ID INTEGER PRIMARY KEY, F_BIGINT BIGINT, F_DECIMAL DECIMAL(10,2), F_BOOLEAN BOOLEAN, F_TIMESTAMP TIMESTAMP, F_BLOB BLOB)");
			activateDefault();
			EPackage pkg = parser.parse();
			EClass tc = findClass(pkg, "TypesTest");

			assertThat(((EAttribute) tc.getEStructuralFeature("fBigint")).getEType()).isEqualTo(EcorePackage.Literals.ELONG_OBJECT);
			assertThat(((EAttribute) tc.getEStructuralFeature("fDecimal")).getEType()).isEqualTo(EcorePackage.Literals.EBIG_DECIMAL);
			assertThat(((EAttribute) tc.getEStructuralFeature("fBoolean")).getEType()).isEqualTo(EcorePackage.Literals.EBOOLEAN_OBJECT);
			assertThat(((EAttribute) tc.getEStructuralFeature("fTimestamp")).getEType()).isEqualTo(EcorePackage.Literals.EDATE);
			assertThat(((EAttribute) tc.getEStructuralFeature("fBlob")).getEType()).isEqualTo(EcorePackage.Literals.EBYTE_ARRAY);
		}
	}

	// --- Foreign keys & EOpposite ---

	@Nested
	@DisplayName("FK → EReference with EOpposite")
	class ForeignKeyTests {

		@Test void testFkCreatesOppositeReferences() throws SQLException {
			executeSql(
				"CREATE TABLE DEPARTMENT (ID INTEGER PRIMARY KEY, NAME VARCHAR(100))",
				"CREATE TABLE EMPLOYEE (ID INTEGER PRIMARY KEY, NAME VARCHAR(100), DEPT_ID INTEGER REFERENCES DEPARTMENT(ID))"
			);
			activateDefault();
			EPackage pkg = parser.parse();

			EClass dept = findClass(pkg, "Department");
			EClass emp = findClass(pkg, "Employee");

			// Employee → Department (ManyToOne)
			EReference empToDept = findReference(emp, "deptId");
			assertThat(empToDept).isNotNull();
			assertThat(empToDept.getEType()).isEqualTo(dept);
			assertThat(empToDept.isMany()).isFalse();

			// Department → Employee (OneToMany, reverse)
			EReference deptToEmps = findReference(dept, "employees");
			assertThat(deptToEmps).isNotNull();
			assertThat(deptToEmps.getEType()).isEqualTo(emp);
			assertThat(deptToEmps.isMany()).isTrue();

			// EOpposite set on both
			assertThat(empToDept.getEOpposite()).isSameAs(deptToEmps);
			assertThat(deptToEmps.getEOpposite()).isSameAs(empToDept);
		}
	}

	// --- Containment ---

	@Nested
	@DisplayName("Containment heuristic")
	class ContainmentTests {

		@Test void testNotNullFkWithCascadeDeleteIsContainment() throws SQLException {
			executeSql(
				"CREATE TABLE PARENT_TBL (ID INTEGER PRIMARY KEY)",
				"CREATE TABLE CHILD_TBL (ID INTEGER PRIMARY KEY, PARENT_ID INTEGER NOT NULL REFERENCES PARENT_TBL(ID) ON DELETE CASCADE)"
			);
			activateDefault();
			EPackage pkg = parser.parse();

			EClass parent = findClass(pkg, "ParentTbl");
			findClass(pkg, "ChildTbl");

			// Parent → Children: containment=true
			EReference parentToChildren = findReference(parent, "childTbls");
			assertThat(parentToChildren).isNotNull();
			assertThat(parentToChildren.isContainment()).isTrue();
			assertThat(parentToChildren.isMany()).isTrue();
		}

		@Test void testNullableFkIsNotContainment() throws SQLException {
			executeSql(
				"CREATE TABLE TEAM (ID INTEGER PRIMARY KEY)",
				"CREATE TABLE PLAYER (ID INTEGER PRIMARY KEY, TEAM_ID INTEGER REFERENCES TEAM(ID))"
			);
			activateDefault();
			EPackage pkg = parser.parse();

			EClass team = findClass(pkg, "Team");
			EReference teamToPlayers = findReference(team, "players");
			assertThat(teamToPlayers).isNotNull();
			assertThat(teamToPlayers.isContainment()).isFalse();
		}
	}

	// --- ManyToMany junction table ---

	@Nested
	@DisplayName("Junction table → ManyToMany")
	class ManyToManyTests {

		@Test void testJunctionTableBecomesManyToMany() throws SQLException {
			executeSql(
				"CREATE TABLE USERS (ID INTEGER PRIMARY KEY, NAME VARCHAR(100))",
				"CREATE TABLE ROLES (ID INTEGER PRIMARY KEY, NAME VARCHAR(100))",
				"CREATE TABLE USER_ROLES (USER_ID INTEGER REFERENCES USERS(ID), ROLE_ID INTEGER REFERENCES ROLES(ID), PRIMARY KEY (USER_ID, ROLE_ID))"
			);
			activateDefault();
			EPackage pkg = parser.parse();

			// Junction table should NOT exist as EClass
			assertThat(findClass(pkg, "UserRoles")).isNull();

			EClass users = findClass(pkg, "Users");
			EClass roles = findClass(pkg, "Roles");

			// Users → Roles (ManyToMany)
			EReference usersToRoles = findReference(users, "roless");
			assertThat(usersToRoles).isNotNull();
			assertThat(usersToRoles.getEType()).isEqualTo(roles);
			assertThat(usersToRoles.isMany()).isTrue();

			// Roles → Users (ManyToMany reverse)
			EReference rolesToUsers = findReference(roles, "userss");
			assertThat(rolesToUsers).isNotNull();
			assertThat(rolesToUsers.getEType()).isEqualTo(users);
			assertThat(rolesToUsers.isMany()).isTrue();

			// EOpposite
			assertThat(usersToRoles.getEOpposite()).isSameAs(rolesToUsers);
			assertThat(rolesToUsers.getEOpposite()).isSameAs(usersToRoles);
		}

		@Test void testTableWithExtraColumnsIsNotJunction() throws SQLException {
			executeSql(
				"CREATE TABLE A (ID INTEGER PRIMARY KEY)",
				"CREATE TABLE B (ID INTEGER PRIMARY KEY)",
				"CREATE TABLE AB_LINK (A_ID INTEGER REFERENCES A(ID), B_ID INTEGER REFERENCES B(ID), EXTRA_COL VARCHAR(50), PRIMARY KEY (A_ID, B_ID))"
			);
			activateDefault();
			EPackage pkg = parser.parse();

			// AB_LINK has an extra column → NOT a junction table → still an EClass
			assertThat(findClass(pkg, "AbLink")).isNotNull();
		}
	}

	// --- Views ---

	@Nested
	@DisplayName("Views support")
	class ViewTests {

		@Test void testViewsIncluded() throws SQLException {
			executeSql(
				"CREATE TABLE EMPLOYEE (ID INTEGER PRIMARY KEY, NAME VARCHAR(100), SALARY INTEGER)",
				"CREATE VIEW EMPLOYEE_SUMMARY AS SELECT ID, NAME FROM EMPLOYEE"
			);
			activate(true, true);
			EPackage pkg = parser.parse();

			EClass emp = findClass(pkg, "Employee");
			EClass summary = findClass(pkg, "EmployeeSummary");

			assertThat(emp).isNotNull();
			assertThat(summary).isNotNull();

			// View has readOnly annotation
			assertThat(summary.getEAnnotation(DatabaseEcoreParser.ANNOTATION_SOURCE)
					.getDetails().get(DatabaseEcoreParser.ANNOTATION_READ_ONLY))
				.isEqualTo("true");

			// Table does NOT have readOnly
			assertThat(emp.getEAnnotation(DatabaseEcoreParser.ANNOTATION_SOURCE)
					.getDetails().get(DatabaseEcoreParser.ANNOTATION_READ_ONLY))
				.isNull();
		}

		@Test void testViewsExcludedByDefault() throws SQLException {
			executeSql(
				"CREATE TABLE EMPLOYEE (ID INTEGER PRIMARY KEY)",
				"CREATE VIEW EMP_VIEW AS SELECT ID FROM EMPLOYEE"
			);
			activateDefault(); // includeViews=false
			EPackage pkg = parser.parse();

			assertThat(findClass(pkg, "EmpView")).isNull();
			assertThat(findClass(pkg, "Employee")).isNotNull();
		}
	}

	// --- Multi-schema ---

	@Nested
	@DisplayName("Multi-schema support")
	class MultiSchemaTests {

		@Test void testMultipleSchemasProduceSeparatePackages() throws SQLException {
			executeSql(
				"CREATE SCHEMA SALES",
				"CREATE SCHEMA INVENTORY",
				"CREATE TABLE SALES.ORDERS (ID INTEGER PRIMARY KEY, TOTAL DECIMAL(10,2))",
				"CREATE TABLE INVENTORY.PRODUCTS (ID INTEGER PRIMARY KEY, NAME VARCHAR(100))"
			);
			activate(false, true, "SALES", "INVENTORY");
			List<EPackage> packages = parser.parseAll();

			assertThat(packages).hasSize(2);

			EPackage salesPkg = packages.stream().filter(p -> p.getName().contains("sales")).findFirst().orElse(null);
			EPackage invPkg = packages.stream().filter(p -> p.getName().contains("inventory")).findFirst().orElse(null);

			assertThat(salesPkg).isNotNull();
			assertThat(invPkg).isNotNull();

			assertThat(findClass(salesPkg, "Orders")).isNotNull();
			assertThat(findClass(invPkg, "Products")).isNotNull();

			// Schema annotation
			assertThat(salesPkg.getEAnnotation(DatabaseEcoreParser.ANNOTATION_SOURCE)
					.getDetails().get(DatabaseEcoreParser.ANNOTATION_SCHEMA))
				.isEqualTo("SALES");
		}

		@Test void testSingleSchemaDefaultPackageName() throws SQLException {
			executeSql("CREATE TABLE PERSON (ID INTEGER PRIMARY KEY)");
			activateDefault();
			EPackage pkg = parser.parse();

			// Single schema → package name is just the configured name, no suffix
			assertThat(pkg.getName()).isEqualTo("testpkg");
		}
	}

	// --- Composite PK ---

	@Nested
	@DisplayName("Primary key detection")
	class PrimaryKeyTests {

		@Test void testCompositePk() throws SQLException {
			executeSql("CREATE TABLE ORDER_ITEM (ORDER_ID INTEGER, ITEM_SEQ INTEGER, QTY INTEGER, PRIMARY KEY (ORDER_ID, ITEM_SEQ))");
			activateDefault();
			EPackage pkg = parser.parse();
			EClass oi = findClass(pkg, "OrderItem");

			assertThat(((EAttribute) oi.getEStructuralFeature("orderId")).isID()).isTrue();
			assertThat(((EAttribute) oi.getEStructuralFeature("itemSeq")).isID()).isTrue();
			assertThat(((EAttribute) oi.getEStructuralFeature("qty")).isID()).isFalse();
		}
	}

	// --- Diagnostics (issue #19) ---

	@Nested
	@DisplayName("Diagnostics")
	class DiagnosticsTests {

		@Test void testCleanSchemaHasNoDiagnostics() throws SQLException {
			executeSql("CREATE TABLE PERSON (ID INTEGER PRIMARY KEY, NAME VARCHAR(100))");
			activateDefault();
			ParseResult result = parser.parseAllWithDiagnostics();

			assertThat(result.ePackages()).hasSize(1);
			assertThat(result.diagnostics()).isEmpty();
			assertThat(result.isSuccess()).isTrue();
			assertThat(result.getSeverity()).isEqualTo(Diagnostic.OK);
		}

		@Test void testUnmappedColumnTypeYieldsWarningWithColumnContext() throws SQLException {
			executeSql("CREATE TABLE SENSOR (ID INTEGER PRIMARY KEY, READINGS INTEGER ARRAY)");
			activateDefault();
			ParseResult result = parser.parseAllWithDiagnostics();

			assertThat(result.isSuccess()).isTrue();
			assertThat(result.diagnostics()).hasSize(1);
			Diagnostic diagnostic = result.diagnostics().get(0);
			assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.WARNING);
			assertThat(diagnostic.getSource()).isEqualTo(DatabaseEcoreParser.DIAGNOSTIC_SOURCE);
			assertThat(diagnostic.getMessage())
					.contains("Unmapped JDBC type")
					.contains("SENSOR.READINGS");

			// data[0] = affected model element
			EClass sensor = findClass(result.ePackages().get(0), "Sensor");
			assertThat(diagnostic.getData()).first().isSameAs(sensor);
			// The fallback attribute is still produced
			assertThat(sensor.getEStructuralFeature("readings").getEType())
					.isEqualTo(EcorePackage.Literals.ESTRING);
		}

		@Test void testMissingFkTargetYieldsWarningAndPlainAttribute() throws SQLException {
			executeSql("CREATE SCHEMA OTHER_SCHEMA",
					"CREATE TABLE OTHER_SCHEMA.PARENT (ID INTEGER PRIMARY KEY)",
					"CREATE TABLE CHILD (ID INTEGER PRIMARY KEY, "
							+ "PARENT_ID INTEGER NOT NULL REFERENCES OTHER_SCHEMA.PARENT(ID))");
			activate(false, true, "PUBLIC");
			ParseResult result = parser.parseAllWithDiagnostics();

			assertThat(result.isSuccess()).isTrue();
			assertThat(result.diagnostics()).hasSize(1);
			Diagnostic diagnostic = result.diagnostics().get(0);
			assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.WARNING);
			assertThat(diagnostic.getSource()).isEqualTo(DatabaseEcoreParser.DIAGNOSTIC_SOURCE);
			assertThat(diagnostic.getMessage())
					.contains("FK target 'PARENT' not found")
					.contains("CHILD.PARENT_ID");

			EClass child = findClass(result.ePackages().get(0), "Child");
			assertThat(diagnostic.getData()).first().isSameAs(child);
			// FK falls back to a plain attribute instead of a reference
			assertThat(child.getEStructuralFeature("parentId")).isInstanceOf(EAttribute.class);
		}

		@Test void testLegacyParseAllLogsAndReturnsPackages() throws SQLException {
			executeSql("CREATE TABLE SENSOR (ID INTEGER PRIMARY KEY, READINGS INTEGER ARRAY)");
			activateDefault();

			Logger logger = Logger.getLogger(DatabaseEcoreParser.class.getName());
			List<LogRecord> records = new ArrayList<>();
			Handler handler = new Handler() {
				@Override public void publish(LogRecord logRecord) { records.add(logRecord); }
				@Override public void flush() {}
				@Override public void close() {}
			};
			logger.addHandler(handler);
			try {
				List<EPackage> packages = parser.parseAll();
				assertThat(packages).hasSize(1);
				assertThat(records)
						.anySatisfy(logRecord -> {
							assertThat(logRecord.getLevel()).isEqualTo(Level.WARNING);
							assertThat(logRecord.getMessage()).contains("Unmapped JDBC type");
						});
			} finally {
				logger.removeHandler(handler);
			}
		}
	}
}
