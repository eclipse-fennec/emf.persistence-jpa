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
import static org.assertj.core.api.Assertions.tuple;

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
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EModelElement;
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
			assertThat(((EAttribute) tc.getEStructuralFeature("fTimestamp")).getEType().getInstanceClassName())
					.isEqualTo("java.time.LocalDateTime");
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
			EReference empToDept = findReference(emp, "dept");
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
			EReference usersToRoles = findReference(users, "roles");
			assertThat(usersToRoles).isNotNull();
			assertThat(usersToRoles.getEType()).isEqualTo(roles);
			assertThat(usersToRoles.isMany()).isTrue();

			// Roles → Users (ManyToMany reverse)
			EReference rolesToUsers = findReference(roles, "users");
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

			EReference billing = findReference(orders, "billingCustomer");
			EReference shipping = findReference(orders, "shippingCustomer");
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

			EReference manager = findReference(employee, "manager");
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

	// --- Schema facts, defaults, types and names (issue #295) ---

	@Nested
	@DisplayName("Schema facts, defaults, types and names (#295)")
	class SchemaFactsTests {

		@Test void testColumnFactsAreLinkedToTheirFeatures() throws SQLException {
			executeSql("CREATE TABLE CUSTOMER (ID BIGINT AUTO_INCREMENT PRIMARY KEY, EMAIL VARCHAR(120) NOT NULL, "
					+ "PRICE DECIMAL(10,2), DOUBLED INT GENERATED ALWAYS AS (ID * 2))");
			activateDefault();
			ParseResult result = parser.parseAllWithDiagnostics();
			EClass customer = findClass(result.ePackages().get(0), "Customer");
			TableFacts table = result.table(customer);

			assertThat(table.name()).isEqualTo("CUSTOMER");
			assertThat(table.schema()).isEqualTo("PUBLIC");
			assertThat(table.primaryKey()).containsExactly("ID");
			TableFacts.Column id = table.column("ID");
			assertThat(id.autoIncrement()).isTrue();
			assertThat(id.feature()).isSameAs(customer.getEStructuralFeature("id"));
			TableFacts.Column email = table.column("EMAIL");
			assertThat(email.size()).isEqualTo(120);
			assertThat(email.nullable()).isFalse();
			assertThat(email.feature()).isSameAs(customer.getEStructuralFeature("email"));
			TableFacts.Column price = table.column("PRICE");
			assertThat(price.size()).isEqualTo(10);
			assertThat(price.decimalDigits()).isEqualTo(2);
			assertThat(table.column("DOUBLED").generated()).isTrue();
			// the original name also travels with the serialized model
			assertThat(customer.getEStructuralFeature("email").getEAnnotation(DatabaseEcoreParser.ANNOTATION_SOURCE)
					.getDetails().get(DatabaseEcoreParser.ANNOTATION_COLUMN_NAME)).isEqualTo("EMAIL");
		}

		@Test void testForeignKeyAndJunctionFacts() throws SQLException {
			executeSql("CREATE TABLE ORDERS (ID BIGINT PRIMARY KEY)",
					"CREATE TABLE ORDER_LINE (ORDER_NO BIGINT, LINE_NO INT, PRIMARY KEY (ORDER_NO, LINE_NO))",
					"CREATE TABLE SHIPMENT (ID BIGINT PRIMARY KEY, ORDER_NO BIGINT, LINE_NO INT, "
							+ "CONSTRAINT FK_SHIP_LINE FOREIGN KEY (ORDER_NO, LINE_NO) REFERENCES ORDER_LINE(ORDER_NO, LINE_NO))",
					"CREATE TABLE TAG (ID BIGINT PRIMARY KEY)",
					"CREATE TABLE ORDER_TAG (ORDER_ID BIGINT REFERENCES ORDERS(ID), TAG_ID BIGINT REFERENCES TAG(ID), "
							+ "PRIMARY KEY (ORDER_ID, TAG_ID))");
			activateDefault();
			ParseResult result = parser.parseAllWithDiagnostics();
			EPackage pkg = result.ePackages().get(0);
			EClass shipment = findClass(pkg, "Shipment");
			TableFacts table = result.table(shipment);

			assertThat(table.foreignKeys()).hasSize(1);
			TableFacts.ForeignKey fk = table.foreignKeys().get(0);
			assertThat(fk.name()).isEqualTo("FK_SHIP_LINE");
			assertThat(fk.targetTable()).isEqualTo("ORDER_LINE");
			assertThat(fk.columns()).containsExactly("ORDER_NO", "LINE_NO");
			assertThat(fk.targetColumns()).containsExactly("ORDER_NO", "LINE_NO");
			assertThat(fk.reference()).isSameAs(findReference(shipment, "orderLine"));
			// both FK columns are carried by the one reference
			assertThat(table.column("ORDER_NO").feature()).isSameAs(fk.reference());
			assertThat(table.column("LINE_NO").feature()).isSameAs(fk.reference());
			assertThat(fk.reference().getEAnnotation(DatabaseEcoreParser.ANNOTATION_SOURCE).getDetails()
					.get(DatabaseEcoreParser.ANNOTATION_JOIN_COLUMNS)).isEqualTo("ORDER_NO,LINE_NO");

			assertThat(result.junctions()).hasSize(1);
			JunctionFacts junction = result.junctions().get(0);
			assertThat(junction.name()).isEqualTo("ORDER_TAG");
			assertThat(junction.sourceKey().targetTable()).isEqualTo("ORDERS");
			assertThat(junction.sourceKey().columns()).containsExactly("ORDER_ID");
			assertThat(junction.targetKey().targetTable()).isEqualTo("TAG");
			assertThat(junction.reference()).isSameAs(findReference(findClass(pkg, "Orders"), "tags"));
			assertThat(junction.opposite()).isSameAs(junction.reference().getEOpposite());
			assertThat(result.table(findClass(pkg, "Orders"))).isNotNull();
			assertThat(result.tables()).noneMatch(t -> t.name().equals("ORDER_TAG"));
		}

		@Test void testUniqueConstraintsAndIndexes() throws SQLException {
			executeSql("CREATE TABLE ACCOUNT (ID BIGINT PRIMARY KEY, EMAIL VARCHAR(50) UNIQUE, X INT, Y INT, "
					+ "STATE VARCHAR(10), CONSTRAINT UQ_XY UNIQUE (X, Y))",
					"CREATE INDEX IDX_STATE ON ACCOUNT(STATE)");
			activateDefault();
			ParseResult result = parser.parseAllWithDiagnostics();
			TableFacts table = result.tables().get(0);

			assertThat(table.indexes()).extracting(TableFacts.Index::columns, TableFacts.Index::unique)
					.containsExactlyInAnyOrder(
							tuple(List.of("EMAIL"), true),
							tuple(List.of("X", "Y"), true),
							tuple(List.of("STATE"), false));
			// the primary key's own index is the primary key, not an index fact
			assertThat(table.indexes()).noneMatch(i -> i.columns().equals(List.of("ID")));
			assertThat(table.indexes()).filteredOn(i -> i.columns().equals(List.of("STATE")))
					.extracting(TableFacts.Index::name).containsExactly("IDX_STATE");
		}

		@Test void testCommentsBecomeDocumentation() throws SQLException {
			executeSql("CREATE TABLE PERSON (ID BIGINT PRIMARY KEY, EMAIL VARCHAR(50))",
					"COMMENT ON TABLE PERSON IS 'natural persons'",
					"COMMENT ON COLUMN PERSON.EMAIL IS 'personal contact'");
			activateDefault();
			ParseResult result = parser.parseAllWithDiagnostics();
			EClass person = findClass(result.ePackages().get(0), "Person");

			assertThat(documentation(person)).isEqualTo("natural persons");
			assertThat(documentation(person.getEStructuralFeature("email"))).isEqualTo("personal contact");
			assertThat(documentation(person.getEStructuralFeature("id"))).isNull();
			assertThat(result.table(person).remarks()).isEqualTo("natural persons");
			assertThat(result.table(person).column("EMAIL").remarks()).isEqualTo("personal contact");
		}

		@Test void testLiteralDefaultsAndExpressionDefaults() throws SQLException {
			executeSql("CREATE TABLE TICKET (ID BIGINT AUTO_INCREMENT PRIMARY KEY, STATE VARCHAR(10) DEFAULT 'NEW', "
					+ "PRIO INT DEFAULT 0, NEG INT DEFAULT -1, OPEN BOOLEAN DEFAULT TRUE, NOTE VARCHAR(10) DEFAULT 'it''s', "
					+ "CREATED TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
			activateDefault();
			ParseResult result = parser.parseAllWithDiagnostics();
			EClass ticket = findClass(result.ePackages().get(0), "Ticket");

			assertThat(attribute(ticket, "state").getDefaultValueLiteral()).isEqualTo("NEW");
			assertThat(attribute(ticket, "prio").getDefaultValueLiteral()).isEqualTo("0");
			assertThat(attribute(ticket, "neg").getDefaultValueLiteral()).isEqualTo("-1");
			assertThat(attribute(ticket, "open").getDefaultValueLiteral()).isEqualTo("true");
			assertThat(attribute(ticket, "note").getDefaultValueLiteral()).isEqualTo("it's");
			assertThat(attribute(ticket, "state").getDefaultValue()).isEqualTo("NEW");
			assertThat(attribute(ticket, "prio").getDefaultValue()).isEqualTo(0);
			// an expression is no Ecore literal: fact and diagnostic, never a string default
			assertThat(attribute(ticket, "created").getDefaultValueLiteral()).isNull();
			assertThat(result.table(ticket).column("CREATED").defaultValue()).isEqualTo("CURRENT_TIMESTAMP");
			assertThat(result.diagnostics()).filteredOn(d -> d.getMessage().contains("TICKET.CREATED"))
					.singleElement().satisfies(d -> {
						assertThat(d.getSeverity()).isEqualTo(Diagnostic.INFO);
						assertThat(d.getMessage()).contains("kept as schema fact");
					});
			// the auto-increment id reports no default at all
			assertThat(result.diagnostics()).noneMatch(d -> d.getMessage().contains("TICKET.ID"));
			assertValid(result.ePackages().get(0));
		}

		@Test void testTemporalTypesAndUuid() throws SQLException {
			executeSql("CREATE TABLE EVENT (ID UUID PRIMARY KEY, ON_DAY DATE, DUE_DAY DATE, AT_TIME TIME, "
					+ "STAMP TIMESTAMP, ZONED TIMESTAMP WITH TIME ZONE)");
			activateDefault();
			EPackage pkg = parser.parse();
			EClass event = findClass(pkg, "Event");

			assertThat(attribute(event, "id").getEType().getInstanceClassName()).isEqualTo("java.util.UUID");
			assertThat(attribute(event, "onDay").getEType().getInstanceClassName()).isEqualTo("java.time.LocalDate");
			assertThat(attribute(event, "atTime").getEType().getInstanceClassName()).isEqualTo("java.time.LocalTime");
			assertThat(attribute(event, "stamp").getEType().getInstanceClassName())
					.isEqualTo("java.time.LocalDateTime");
			assertThat(attribute(event, "zoned").getEType().getInstanceClassName())
					.isEqualTo("java.time.OffsetDateTime");
			// one data type per Java type, declared in the package and shared
			assertThat(attribute(event, "dueDay").getEType()).isSameAs(attribute(event, "onDay").getEType());
			assertThat(attribute(event, "onDay").getEType().getEPackage()).isSameAs(pkg);
			assertThat(attribute(event, "onDay").getEType().getName()).isEqualTo("ELocalDate");
			assertValid(pkg);
		}

		@Test void testIntegralDecimals() throws SQLException {
			executeSql("CREATE TABLE AMOUNTS (ID BIGINT PRIMARY KEY, SMALL DECIMAL(9,0), BIG NUMERIC(30,0), "
					+ "MONEY DECIMAL(10,2), ANY_NUM DECIMAL)");
			activateDefault();
			EClass amounts = findClass(parser.parse(), "Amounts");

			assertThat(attribute(amounts, "small").getEType()).isEqualTo(EcorePackage.Literals.ELONG_OBJECT);
			assertThat(attribute(amounts, "big").getEType()).isEqualTo(EcorePackage.Literals.EBIG_INTEGER);
			assertThat(attribute(amounts, "money").getEType()).isEqualTo(EcorePackage.Literals.EBIG_DECIMAL);
			assertThat(attribute(amounts, "anyNum").getEType()).isEqualTo(EcorePackage.Literals.EBIG_DECIMAL);
		}

		@Test void testInlineEnumBecomesEEnum() throws SQLException {
			executeSql("CREATE TABLE TASK (ID BIGINT PRIMARY KEY, "
					+ "STATE ENUM('open', 'in progress', 'done') DEFAULT 'open')");
			activateDefault();
			EPackage pkg = parser.parse();
			EClass task = findClass(pkg, "Task");

			EAttribute state = attribute(task, "state");
			assertThat(state.getEType()).isInstanceOf(EEnum.class);
			EEnum eEnum = (EEnum) state.getEType();
			assertThat(eEnum.getName()).isEqualTo("TaskState");
			assertThat(eEnum.getELiterals()).extracting(EEnumLiteral::getLiteral)
					.containsExactly("open", "in progress", "done");
			assertThat(eEnum.getELiterals()).extracting(EEnumLiteral::getName)
					.containsExactly("OPEN", "IN_PROGRESS", "DONE");
			assertThat(state.getDefaultValueLiteral()).isEqualTo("open");
			assertThat(state.getDefaultValue()).isSameAs(eEnum.getEEnumLiteral("OPEN").getInstance());
			assertValid(pkg);
		}

		@Test void testTableWithoutPrimaryKeyIsReported() throws SQLException {
			executeSql("CREATE TABLE LOGBOOK (MSG VARCHAR(100))");
			activateDefault();
			ParseResult result = parser.parseAllWithDiagnostics();
			EClass logbook = findClass(result.ePackages().get(0), "Logbook");

			assertThat(logbook.getEIDAttribute()).isNull();
			assertThat(result.diagnostics()).singleElement().satisfies(d -> {
				assertThat(d.getSeverity()).isEqualTo(Diagnostic.WARNING);
				assertThat(d.getMessage()).contains("'LOGBOOK' has no primary key");
				assertThat(d.getData()).first().isSameAs(logbook);
			});
			assertThat(result.table(logbook).primaryKey()).isEmpty();
		}

		@Test void testReferenceAndPluralNames() throws SQLException {
			executeSql("CREATE TABLE CATEGORY (ID BIGINT PRIMARY KEY, PARENT_ID BIGINT REFERENCES CATEGORY(ID))",
					"CREATE TABLE ADDRESS (ID BIGINT PRIMARY KEY)",
					"CREATE TABLE PERSON (ID BIGINT PRIMARY KEY, HOME_ADDRESS_ID BIGINT REFERENCES ADDRESS(ID), "
							+ "CATEGORY_ID BIGINT REFERENCES CATEGORY(ID))");
			activateDefault();
			EPackage pkg = parser.parse();
			EClass category = findClass(pkg, "Category");
			EClass person = findClass(pkg, "Person");

			assertThat(findReference(category, "parent").getEReferenceType()).isSameAs(category);
			assertThat(findReference(category, "categories").getEOpposite()).isSameAs(findReference(category, "parent"));
			assertThat(findReference(person, "homeAddress")).isNotNull();
			assertThat(findReference(person, "category")).isNotNull();
			assertThat(findReference(findClass(pkg, "Address"), "persons")).isNotNull();
			assertThat(findReference(category, "persons")).isNotNull();
			assertValid(pkg);
		}

		@Test void testReservedJavaNamesAreEscaped() throws SQLException {
			executeSql("CREATE TABLE \"CLASS\" (ID BIGINT PRIMARY KEY, \"PACKAGE\" VARCHAR(10), \"DEFAULT\" INT)");
			activateDefault();
			ParseResult result = parser.parseAllWithDiagnostics();
			EPackage pkg = result.ePackages().get(0);
			EClass clazz = findClass(pkg, "Class_");

			assertThat(clazz).isNotNull();
			assertThat(clazz.getEStructuralFeature("package_")).isNotNull();
			assertThat(clazz.getEStructuralFeature("default_")).isNotNull();
			assertThat(result.diagnostics()).filteredOn(d -> d.getSeverity() == Diagnostic.INFO).hasSize(3);
			// the original names survive for the mapping
			assertThat(result.table(clazz).name()).isEqualTo("CLASS");
			assertThat(result.table(clazz).column("PACKAGE").feature()).isSameAs(clazz.getEStructuralFeature("package_"));
			assertValid(pkg);
		}

		@Test void testUntransformedNamesStayRaw() throws SQLException {
			executeSql("CREATE TABLE EMPLOYEE (ID BIGINT PRIMARY KEY, MANAGER_ID BIGINT REFERENCES EMPLOYEE(ID))");
			activate(false, false);
			EClass employee = findClass(parser.parse(), "EMPLOYEE");

			assertThat(findReference(employee, "MANAGER_ID")).isNotNull();
			assertThat(findReference(employee, "EMPLOYEE")).isNotNull();
		}

		private EAttribute attribute(EClass eClass, String name) {
			return (EAttribute) eClass.getEStructuralFeature(name);
		}

		private String documentation(EModelElement element) {
			var annotation = element.getEAnnotation(DatabaseEcoreParser.GENMODEL_SOURCE);
			return annotation == null ? null : annotation.getDetails().get(DatabaseEcoreParser.GENMODEL_DOCUMENTATION);
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
