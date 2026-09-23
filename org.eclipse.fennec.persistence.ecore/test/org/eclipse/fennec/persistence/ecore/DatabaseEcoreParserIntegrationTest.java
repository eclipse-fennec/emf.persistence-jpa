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
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.fennec.persistence.helper.CompositeIds;
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

	private static void assertValid(EPackage pkg) {
		Diagnostic validation = Diagnostician.INSTANCE.validate(pkg);
		assertThat(validation.getSeverity()).as("Ecore validation: %s", validation.getChildren())
				.isLessThan(Diagnostic.WARNING);
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

			// several isID attributes are invalid Ecore — composite identity is idFeatures (#115, #294)
			assertThat(oi.getEAttributes()).noneMatch(EAttribute::isID);
			assertThat(oi.getEAnnotation(CompositeIds.ANNOTATION_SOURCE).getDetails().get(CompositeIds.ID_FEATURES))
					.isEqualTo("orderId,itemSeq");
			assertThat(CompositeIds.idAttributes(oi)).extracting(EAttribute::getName).containsExactly("orderId", "itemSeq");
			assertValid(pkg);
		}

		@Test void testCompositePkFollowsKeyOrderNotColumnOrder() throws SQLException {
			executeSql("CREATE TABLE READING (VAL INTEGER, TS TIMESTAMP, SENSOR INTEGER, PRIMARY KEY (SENSOR, TS))");
			activateDefault();
			EClass reading = findClass(parser.parse(), "Reading");

			assertThat(reading.getEAnnotation(CompositeIds.ANNOTATION_SOURCE).getDetails().get(CompositeIds.ID_FEATURES))
					.isEqualTo("sensor,ts");
		}

		@Test void testSinglePkStaysEid() throws SQLException {
			executeSql("CREATE TABLE PERSON (ID INTEGER PRIMARY KEY, NAME VARCHAR(10))");
			activateDefault();
			EClass person = findClass(parser.parse(), "Person");

			assertThat(person.getEIDAttribute().getName()).isEqualTo("id");
			assertThat(person.getEAnnotation(CompositeIds.ANNOTATION_SOURCE)).isNull();
		}
	}

	// --- Schema patterns that produced invalid Ecore (issue #294) ---

	@Nested
	@DisplayName("Valid Ecore for common schema patterns (#294)")
	class SchemaPatternTests {

		@Test void testTwoForeignKeysToTheSameTable() throws SQLException {
			executeSql("CREATE TABLE CUSTOMER (ID BIGINT PRIMARY KEY)",
					"CREATE TABLE ORDERS (ID BIGINT PRIMARY KEY, "
							+ "BILLING_CUSTOMER_ID BIGINT REFERENCES CUSTOMER(ID), "
							+ "SHIPPING_CUSTOMER_ID BIGINT REFERENCES CUSTOMER(ID))");
			activateDefault();
			ParseResult result = parser.parseAllWithDiagnostics();
			EPackage pkg = result.ePackages().get(0);
			EClass customer = findClass(pkg, "Customer");
			EClass orders = findClass(pkg, "Orders");

			EReference billing = findReference(orders, "billingCustomerId");
			EReference shipping = findReference(orders, "shippingCustomerId");
			assertThat(billing.getEOpposite()).isNotNull().isNotSameAs(shipping.getEOpposite());
			assertThat(customer.getEReferences()).extracting(EReference::getName)
					.doesNotHaveDuplicates().hasSize(2)
					.containsExactlyInAnyOrder(billing.getEOpposite().getName(), shipping.getEOpposite().getName());
			assertThat(customer.getEReferences()).allSatisfy(r -> assertThat(r.getEOpposite().getEOpposite()).isSameAs(r));
			// the fallback name is reported, not silent
			assertThat(result.diagnostics()).anySatisfy(d -> {
				assertThat(d.getSeverity()).isEqualTo(Diagnostic.INFO);
				assertThat(d.getMessage()).contains("already taken on 'Customer'");
			});
			assertValid(pkg);
		}

		@Test void testSelfReferencingJunctionTable() throws SQLException {
			executeSql("CREATE TABLE PERSON (ID BIGINT PRIMARY KEY)",
					"CREATE TABLE FRIENDS (A_ID BIGINT REFERENCES PERSON(ID), B_ID BIGINT REFERENCES PERSON(ID), "
							+ "PRIMARY KEY (A_ID, B_ID))");
			activateDefault();
			EPackage pkg = parser.parse();
			EClass person = findClass(pkg, "Person");

			assertThat(findClass(pkg, "Friends")).isNull();
			assertThat(person.getEReferences()).hasSize(2).extracting(EReference::getName).doesNotHaveDuplicates();
			EReference first = person.getEReferences().get(0);
			EReference second = person.getEReferences().get(1);
			assertThat(first.getEOpposite()).isSameAs(second);
			assertThat(second.getEOpposite()).isSameAs(first);
			assertThat(first.isMany()).isTrue();
			assertThat(second.isMany()).isTrue();
			assertValid(pkg);
		}

		@Test void testCompositeForeignKeyIsOneReference() throws SQLException {
			executeSql("CREATE TABLE ORDER_LINE (ORDER_NO INTEGER, LINE_NO INTEGER, PRIMARY KEY (ORDER_NO, LINE_NO))",
					"CREATE TABLE SHIPMENT (ID BIGINT PRIMARY KEY, ORDER_NO INTEGER, LINE_NO INTEGER, "
							+ "FOREIGN KEY (ORDER_NO, LINE_NO) REFERENCES ORDER_LINE(ORDER_NO, LINE_NO))");
			activateDefault();
			EPackage pkg = parser.parse();
			EClass shipment = findClass(pkg, "Shipment");
			EClass orderLine = findClass(pkg, "OrderLine");

			assertThat(shipment.getEReferences()).hasSize(1);
			EReference toLine = shipment.getEReferences().get(0);
			assertThat(toLine.getName()).isEqualTo("orderLine");
			assertThat(toLine.getEReferenceType()).isSameAs(orderLine);
			// the FK columns are carried by the reference, not duplicated as attributes
			assertThat(shipment.getEAttributes()).extracting(EAttribute::getName).containsExactly("id");
			assertThat(orderLine.getEReferences()).hasSize(1);
			assertThat(orderLine.getEReferences().get(0).getEOpposite()).isSameAs(toLine);
			assertValid(pkg);
		}

		@Test void testIdentifyingForeignKeyKeepsItsColumnInTheId() throws SQLException {
			executeSql("CREATE TABLE ORDERS (ID BIGINT PRIMARY KEY)",
					"CREATE TABLE ORDER_LINE (ORDER_ID BIGINT NOT NULL REFERENCES ORDERS(ID) ON DELETE CASCADE, "
							+ "LINE_NO INTEGER NOT NULL, QTY INTEGER, PRIMARY KEY (ORDER_ID, LINE_NO))");
			activateDefault();
			EPackage pkg = parser.parse();
			EClass orders = findClass(pkg, "Orders");
			EClass orderLine = findClass(pkg, "OrderLine");

			assertThat(CompositeIds.idAttributes(orderLine)).extracting(EAttribute::getName)
					.containsExactly("orderId", "lineNo");
			// the relationship is still there, named after its target since the column name is the id's
			EReference toOrder = findReference(orderLine, "orders");
			assertThat(toOrder).isNotNull();
			assertThat(toOrder.getEReferenceType()).isSameAs(orders);
			EReference lines = toOrder.getEOpposite();
			assertThat(lines.getEContainingClass()).isSameAs(orders);
			assertThat(lines.isContainment()).isTrue();
			assertValid(pkg);
		}

		@Test void testUnderscoreInTableNameIsNotAWildcard() throws SQLException {
			executeSql("CREATE TABLE USER_ACCOUNT (ID INTEGER PRIMARY KEY)",
					"CREATE TABLE USERXACCOUNT (X INTEGER PRIMARY KEY)");
			activateDefault();
			EPackage pkg = parser.parse();

			assertThat(findClass(pkg, "UserAccount").getEStructuralFeatures())
					.extracting(f -> f.getName()).containsExactly("id");
			assertThat(findClass(pkg, "Userxaccount").getEStructuralFeatures())
					.extracting(f -> f.getName()).containsExactly("x");
			assertValid(pkg);
		}

		@Test void testUnderscoreInSchemaNameIsNotAWildcard() throws SQLException {
			executeSql("CREATE SCHEMA MY_SCHEMA", "CREATE SCHEMA MYXSCHEMA",
					"CREATE TABLE MY_SCHEMA.ITEM (ID INTEGER PRIMARY KEY)",
					"CREATE TABLE MYXSCHEMA.ITEM (ID INTEGER PRIMARY KEY, OTHER INTEGER)",
					"CREATE TABLE MYXSCHEMA.GADGET (ID INTEGER PRIMARY KEY)");
			activate(false, true, "MY_SCHEMA");
			EPackage pkg = parser.parse();

			assertThat(pkg.getEClassifiers()).extracting(c -> c.getName()).containsExactly("Item");
			assertThat(findClass(pkg, "Item").getEStructuralFeatures()).extracting(f -> f.getName())
					.containsExactly("id");
		}

		@Test void testTableWithThreeForeignKeysIsAnEntity() throws SQLException {
			executeSql("CREATE TABLE A (ID INTEGER PRIMARY KEY)",
					"CREATE TABLE B (ID INTEGER PRIMARY KEY)",
					"CREATE TABLE C (ID INTEGER PRIMARY KEY)",
					"CREATE TABLE ABC (A_ID INTEGER REFERENCES A(ID), B_ID INTEGER REFERENCES B(ID), "
							+ "C_ID INTEGER REFERENCES C(ID), PRIMARY KEY (A_ID, B_ID, C_ID))");
			activateDefault();
			EPackage pkg = parser.parse();
			EClass abc = findClass(pkg, "Abc");

			assertThat(abc).isNotNull();
			assertThat(abc.getEReferences()).extracting(EReference::getEReferenceType)
					.containsExactly(findClass(pkg, "A"), findClass(pkg, "B"), findClass(pkg, "C"));
			assertThat(CompositeIds.idAttributes(abc)).extracting(EAttribute::getName)
					.containsExactly("aId", "bId", "cId");
			assertValid(pkg);
		}

		@Test void testSelfReference() throws SQLException {
			executeSql("CREATE TABLE EMPLOYEE (ID BIGINT PRIMARY KEY, MANAGER_ID BIGINT REFERENCES EMPLOYEE(ID))");
			activateDefault();
			EPackage pkg = parser.parse();
			EClass employee = findClass(pkg, "Employee");

			EReference manager = findReference(employee, "managerId");
			assertThat(manager.getEReferenceType()).isSameAs(employee);
			assertThat(manager.getEOpposite().getEContainingClass()).isSameAs(employee);
			assertThat(manager.getEOpposite().isMany()).isTrue();
			assertValid(pkg);
		}

		/** The whole probe schema of #294 at once, parsed twice: the result is valid and stable. */
		@Test void testProbeSchemaIsValidAndDeterministic() throws SQLException {
			executeSql("CREATE TABLE CUSTOMER (ID BIGINT PRIMARY KEY, EMAIL VARCHAR(120) NOT NULL)",
					"CREATE TABLE EMPLOYEE (ID BIGINT PRIMARY KEY, MANAGER_ID BIGINT REFERENCES EMPLOYEE(ID))",
					"CREATE TABLE ORDERS (ID BIGINT PRIMARY KEY, BILLING_CUSTOMER_ID BIGINT REFERENCES CUSTOMER(ID), "
							+ "SHIPPING_CUSTOMER_ID BIGINT REFERENCES CUSTOMER(ID))",
					"CREATE TABLE ORDER_LINE (ORDER_ID BIGINT NOT NULL REFERENCES ORDERS(ID) ON DELETE CASCADE, "
							+ "LINE_NO INT NOT NULL, QTY INT, PRIMARY KEY (ORDER_ID, LINE_NO))",
					"CREATE TABLE SHIPMENT (ID BIGINT PRIMARY KEY, ORDER_ID BIGINT, LINE_NO INT, "
							+ "FOREIGN KEY (ORDER_ID, LINE_NO) REFERENCES ORDER_LINE(ORDER_ID, LINE_NO))",
					"CREATE TABLE FRIENDS (A_ID BIGINT REFERENCES CUSTOMER(ID), B_ID BIGINT REFERENCES CUSTOMER(ID), "
							+ "PRIMARY KEY (A_ID, B_ID))",
					"CREATE TABLE USER_ACCOUNT (ID INT PRIMARY KEY)",
					"CREATE TABLE USERXACCOUNT (X INT PRIMARY KEY)");
			activateDefault();
			EPackage first = parser.parse();
			EPackage second = parser.parse();

			assertValid(first);
			assertThat(describe(second)).isEqualTo(describe(first));
		}

		private String describe(EPackage pkg) {
			StringBuilder sb = new StringBuilder();
			for (var classifier : pkg.getEClassifiers()) {
				EClass eClass = (EClass) classifier;
				sb.append(eClass.getName()).append('{');
				eClass.getEStructuralFeatures().forEach(f -> sb.append(f.getName()).append(':')
						.append(f.getEType().getName()).append(f instanceof EReference r && r.getEOpposite() != null
								? "<->" + r.getEOpposite().getName() : "").append(' '));
				sb.append("} ");
			}
			return sb.toString();
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
