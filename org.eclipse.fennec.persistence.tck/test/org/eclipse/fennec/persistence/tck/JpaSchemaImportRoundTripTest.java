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
package org.eclipse.fennec.persistence.tck;

import static java.util.Map.entry;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.resource.PersistenceResource;
import org.eclipse.fennec.persistence.ecore.DatabaseEcoreParser;
import org.eclipse.fennec.persistence.ecore.JunctionFacts;
import org.eclipse.fennec.persistence.ecore.ParseResult;
import org.eclipse.fennec.persistence.ecore.ParserSettings;
import org.eclipse.fennec.persistence.ecore.TableFacts;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import jakarta.persistence.EntityManagerFactory;

/**
 * Schema import round trip (issue #298): a pre-existing schema with rows is parsed into Ecore
 * and mapped back onto itself with DDL generation {@code none}; then every row is read,
 * every reference navigated and writes are verified — all compared with plain SQL on the
 * same tables. The schema must be unchanged afterwards.
 * <p>
 * <b>Test-first.</b> The suite comes before the eorm generation (#296) and the backend fixes
 * (#297). A case that cannot pass yet is listed in {@link #KNOWN_GAPS} with the issue that
 * closes it: it still runs, a failure is reported as skipped with that reason, and a gap
 * that starts passing <em>fails</em> — so closing a gap is a visible change to this file,
 * never a silent one. A rising number of skipped cases is a gap, not a pass.
 * <p>
 * Each case is an own schema (an own database on MariaDB) and an own persistence unit, so a
 * case the mapping cannot express does not take the others down with it.
 *
 * @author Mark Hoffmann
 */
class JpaSchemaImportRoundTripTest {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static final String TWO_FKS = "#296: both references get the default join column ACCOUNT_ID"
			+ " — the unit refuses to start (multiple writable mappings)";
	private static final String SELF_REFERENCE = "#296: default join column EMPLOYEE_ID instead of MANAGER_ID";
	private static final String COMPOSITE_KEY = "#296/#297: default join columns LOT_PLANT, LOT_LOTNO instead of"
			+ " PLANT, LOTNO; a composite many-to-one binds only the first key field";
	private static final String IDENTIFYING_FK = "#296: table INVOICELINE instead of INVOICE_LINE";
	private static final String JUNCTION = "#296/#297: default join table <OWNER>__<TARGET> instead of the"
			+ " junction table; join and inverse join columns are ignored";

	/** Case aspect ({@code case/aspect}) → the issue and the concrete gap that keeps it red. */
	private static final Map<String, String> KNOWN_GAPS = Map.ofEntries(
			entry("naming/read", "#296: columns are named after the features (FIRSTNAME), not FIRST_NAME"),
			entry("two-fks/parse", TWO_FKS), entry("two-fks/read", TWO_FKS), entry("two-fks/navigate", TWO_FKS),
			entry("two-fks/schema-unchanged", TWO_FKS),
			entry("self-reference/read", SELF_REFERENCE), entry("self-reference/navigate", SELF_REFERENCE),
			entry("composite-key/read", COMPOSITE_KEY), entry("composite-key/navigate", COMPOSITE_KEY),
			entry("identifying-fk/read", IDENTIFYING_FK), entry("identifying-fk/navigate", IDENTIFYING_FK),
			entry("junction/read", JUNCTION), entry("junction/navigate", JUNCTION), entry("junction/write", JUNCTION),
			entry("self-junction/read", JUNCTION), entry("self-junction/navigate", JUNCTION),
			entry("auto-increment/write", "#296: a sequence generator (table SEQUENCE) instead of IDENTITY"),
			entry("no-primary-key/read", "#296: synthetic id PK_LOGBOOK — decide the mapping of a table without key"),
			entry("view/read", "#296: table CHEAPITEM instead of CHEAP_ITEM; a view has no primary key"));



	/** Flavor-specific gaps, keyed {@code flavor:case/aspect}. */
	private static final Map<String, String> KNOWN_FLAVOR_GAPS = Map.of();

	private static final List<RoundTrip> OPEN = new ArrayList<>();

	@AfterAll
	static void closeUnits() {
		OPEN.forEach(RoundTrip::close);
		OPEN.clear();
	}

	@TestFactory
	Stream<DynamicNode> roundTrip() {
		return cases().stream().map(this::container);
	}

	private DynamicContainer container(Case c) {
		RoundTrip trip = new RoundTrip(c);
		OPEN.add(trip);
		List<DynamicTest> tests = new ArrayList<>();
		tests.add(aspect(c, trip, "parse", trip::verifyParse));
		tests.add(aspect(c, trip, "read", trip::verifyRead));
		tests.add(aspect(c, trip, "navigate", trip::verifyNavigate));
		if (nonNull(c.write())) {
			tests.add(aspect(c, trip, "write", () -> c.write().accept(trip)));
		}
		tests.add(aspect(c, trip, "schema-unchanged", trip::verifySchemaUnchanged));
		return DynamicContainer.dynamicContainer(c.name(), tests);
	}

	private DynamicTest aspect(Case c, RoundTrip trip, String aspect, ThrowingRunnable body) {
		String key = c.name() + "/" + aspect;
		String gap = KNOWN_FLAVOR_GAPS.getOrDefault(JpaTestSupport.flavor() + ":" + key, KNOWN_GAPS.get(key));
		return DynamicTest.dynamicTest(key, () -> {
			trip.fresh();
			if (isNull(gap)) {
				body.run();
				return;
			}
			try {
				body.run();
			} catch (Throwable failure) {
				Assumptions.abort("known gap " + gap + " — " + firstLine(failure));
			}
			fail("Known gap '" + key + "' (" + gap + ") passes now — remove it from KNOWN_GAPS");
		});
	}

	// --- Cases ---

	private static List<Case> cases() {
		List<Case> cases = new ArrayList<>();
		cases.add(new Case("plain", List.of(
				"CREATE TABLE TAG (ID BIGINT PRIMARY KEY, NAME VARCHAR(40) NOT NULL)",
				"INSERT INTO TAG VALUES (1, 'red'), (2, 'blue')"), false, JpaSchemaImportRoundTripTest::writePlain));
		cases.add(new Case("naming", List.of(
				"CREATE TABLE PERSON (ID BIGINT PRIMARY KEY, FIRST_NAME VARCHAR(40), LAST_NAME VARCHAR(40))",
				"INSERT INTO PERSON VALUES (1, 'Ada', 'Lovelace'), (2, 'Alan', 'Turing')"), false, null));
		cases.add(new Case("types", List.of(
				"CREATE TABLE MEASURE (ID BIGINT PRIMARY KEY, ONDAY DATE, ATTIME TIME, STAMP TIMESTAMP, "
						+ "AMOUNT DECIMAL(10,2), QTY DECIMAL(9,0), FLAG BOOLEAN)",
				"INSERT INTO MEASURE VALUES (1, DATE '2026-09-23', TIME '12:34:56', TIMESTAMP '2026-09-23 12:34:56', "
						+ "12.34, 7, TRUE)",
				"INSERT INTO MEASURE VALUES (2, NULL, NULL, NULL, NULL, NULL, NULL)"), false, null));
		cases.add(new Case("uuid", List.of(
				"CREATE TABLE TOKEN (ID UUID PRIMARY KEY, LABEL VARCHAR(20))",
				"INSERT INTO TOKEN VALUES ('0f8fad5b-d9cb-469f-a165-70867728950e', 'first')"), false, null));
		cases.add(new Case("fk", List.of(
				"CREATE TABLE CUSTOMER (ID BIGINT PRIMARY KEY, EMAIL VARCHAR(50))",
				"CREATE TABLE ORDERS (ID BIGINT PRIMARY KEY, CUSTOMER_ID BIGINT REFERENCES CUSTOMER(ID))",
				"INSERT INTO CUSTOMER VALUES (1, 'a@x'), (2, 'b@x')",
				"INSERT INTO ORDERS VALUES (10, 1), (11, 1), (12, NULL)"), false, JpaSchemaImportRoundTripTest::writeFk));
		cases.add(new Case("two-fks", List.of(
				"CREATE TABLE ACCOUNT (ID BIGINT PRIMARY KEY)",
				"CREATE TABLE TRANSFER (ID BIGINT PRIMARY KEY, SOURCE_ID BIGINT REFERENCES ACCOUNT(ID), "
						+ "TARGET_ID BIGINT REFERENCES ACCOUNT(ID))",
				"INSERT INTO ACCOUNT VALUES (1), (2)",
				"INSERT INTO TRANSFER VALUES (100, 1, 2), (101, 2, 1)"), false, null));
		cases.add(new Case("self-reference", List.of(
				"CREATE TABLE EMPLOYEE (ID BIGINT PRIMARY KEY, MANAGER_ID BIGINT REFERENCES EMPLOYEE(ID))",
				"INSERT INTO EMPLOYEE VALUES (1, NULL)",
				"INSERT INTO EMPLOYEE VALUES (2, 1), (3, 1)"), false, null));
		cases.add(new Case("composite-key", List.of(
				"CREATE TABLE LOT (PLANT INT, LOTNO INT, PRIMARY KEY (PLANT, LOTNO))",
				"CREATE TABLE SAMPLE (ID BIGINT PRIMARY KEY, PLANT INT, LOTNO INT, "
						+ "FOREIGN KEY (PLANT, LOTNO) REFERENCES LOT(PLANT, LOTNO))",
				"INSERT INTO LOT VALUES (1, 1), (1, 2)",
				"INSERT INTO SAMPLE VALUES (7, 1, 2), (8, 1, 1)"), false, null));
		cases.add(new Case("identifying-fk", List.of(
				"CREATE TABLE INVOICE (ID BIGINT PRIMARY KEY)",
				"CREATE TABLE INVOICE_LINE (INVOICE_ID BIGINT NOT NULL, LINE_NO INT NOT NULL, AMOUNT DECIMAL(10,2), "
						+ "PRIMARY KEY (INVOICE_ID, LINE_NO), "
						+ "FOREIGN KEY (INVOICE_ID) REFERENCES INVOICE(ID) ON DELETE CASCADE)",
				"INSERT INTO INVOICE VALUES (1), (2)",
				"INSERT INTO INVOICE_LINE VALUES (1, 1, 10.00), (1, 2, 5.50), (2, 1, 1.00)"), false, null));
		cases.add(new Case("junction", List.of(
				"CREATE TABLE USERS (ID BIGINT PRIMARY KEY, NAME VARCHAR(20))",
				"CREATE TABLE ROLES (ID BIGINT PRIMARY KEY, NAME VARCHAR(20))",
				"CREATE TABLE USER_ROLES (USER_ID BIGINT REFERENCES USERS(ID), ROLE_ID BIGINT REFERENCES ROLES(ID), "
						+ "PRIMARY KEY (USER_ID, ROLE_ID))",
				"INSERT INTO USERS VALUES (1, 'ada'), (2, 'alan')",
				"INSERT INTO ROLES VALUES (1, 'admin'), (2, 'dev')",
				"INSERT INTO USER_ROLES VALUES (1, 1), (1, 2), (2, 2)"), false, JpaSchemaImportRoundTripTest::writeJunction));
		cases.add(new Case("self-junction", List.of(
				"CREATE TABLE PEER (ID BIGINT PRIMARY KEY)",
				"CREATE TABLE PEER_LINK (A_ID BIGINT REFERENCES PEER(ID), B_ID BIGINT REFERENCES PEER(ID), "
						+ "PRIMARY KEY (A_ID, B_ID))",
				"INSERT INTO PEER VALUES (1), (2), (3)",
				"INSERT INTO PEER_LINK VALUES (1, 2), (1, 3), (2, 3)"), false, null));
		cases.add(new Case("auto-increment", List.of(
				"CREATE TABLE COUNTER (ID BIGINT " + identity() + " PRIMARY KEY, NAME VARCHAR(20))",
				"INSERT INTO COUNTER (NAME) VALUES ('first')"), false, JpaSchemaImportRoundTripTest::writeAutoIncrement));
		cases.add(new Case("defaults", List.of(
				"CREATE TABLE SETTING (ID BIGINT PRIMARY KEY, MODE VARCHAR(10) DEFAULT 'auto', PRIO INT DEFAULT 3)",
				"INSERT INTO SETTING (ID) VALUES (1)"), false, JpaSchemaImportRoundTripTest::writeDefaults));
		cases.add(new Case("enum", enumFixture(), false, null));
		cases.add(new Case("no-primary-key", List.of(
				"CREATE TABLE LOGBOOK (MSG VARCHAR(100))",
				"INSERT INTO LOGBOOK VALUES ('started'), ('stopped')"), false, null));
		cases.add(new Case("view", List.of(
				"CREATE TABLE ITEM (ID BIGINT PRIMARY KEY, NAME VARCHAR(20), PRICE DECIMAL(10,2))",
				"CREATE VIEW CHEAP_ITEM AS SELECT ID, NAME FROM ITEM WHERE PRICE < 10",
				"INSERT INTO ITEM VALUES (1, 'pen', 2.00), (2, 'desk', 200.00)"), true, null));
		return cases;
	}

	private static String identity() {
		return JpaTestSupport.isPostgres() ? "GENERATED BY DEFAULT AS IDENTITY" : "AUTO_INCREMENT";
	}

	private static List<String> enumFixture() {
		if (JpaTestSupport.isPostgres()) {
			return List.of("CREATE TYPE TICKET_STATE AS ENUM ('open', 'done')",
					"CREATE TABLE TICKET (ID BIGINT PRIMARY KEY, STATE TICKET_STATE)",
					"INSERT INTO TICKET VALUES (1, 'open'), (2, 'done')");
		}
		return List.of("CREATE TABLE TICKET (ID BIGINT PRIMARY KEY, STATE ENUM('open', 'done'))",
				"INSERT INTO TICKET VALUES (1, 'open'), (2, 'done')");
	}

	// --- Case-specific writes ---

	private static void writePlain(RoundTrip trip) throws Exception {
		EClass tag = trip.eClass("Tag");
		EObject green = EcoreUtil.create(tag);
		green.eSet(tag.getEStructuralFeature("id"), 3L);
		green.eSet(tag.getEStructuralFeature("name"), "green");
		trip.insert(green);
		assertThat(trip.sql("SELECT NAME FROM TAG WHERE ID = 3")).containsExactly(List.of("green"));

		trip.fresh();
		Resource all = trip.load("Tag");
		EObject red = all.getContents().stream()
				.filter(o -> Long.valueOf(1).equals(o.eGet(tag.getEStructuralFeature("id")))).findFirst().orElseThrow();
		red.eSet(tag.getEStructuralFeature("name"), "crimson");
		all.save(null);
		assertThat(trip.sql("SELECT NAME FROM TAG WHERE ID = 1")).containsExactly(List.of("crimson"));

		trip.delete("Tag", "2");
		assertThat(trip.sql("SELECT ID FROM TAG ORDER BY ID")).containsExactly(List.of("1"), List.of("3"));
	}

	private static void writeFk(RoundTrip trip) throws Exception {
		// update: an existing order without customer gets one
		Resource orders = trip.load("Orders");
		EClass ordersClass = trip.eClass("Orders");
		EObject open = orders.getContents().stream()
				.filter(o -> Long.valueOf(12).equals(o.eGet(ordersClass.getEStructuralFeature("id")))).findFirst()
				.orElseThrow();
		open.eSet(ordersClass.getEStructuralFeature("customer"), trip.byId("Customer", "2"));
		orders.save(null);
		assertThat(trip.sql("SELECT CUSTOMER_ID FROM ORDERS WHERE ID = 12")).as("update").containsExactly(List.of("2"));

		// insert: a new order for an existing customer
		trip.fresh();
		EObject order = EcoreUtil.create(ordersClass);
		order.eSet(ordersClass.getEStructuralFeature("id"), 13L);
		order.eSet(ordersClass.getEStructuralFeature("customer"), trip.byId("Customer", "2"));
		trip.insert(order);
		assertThat(trip.sql("SELECT CUSTOMER_ID FROM ORDERS WHERE ID = 13")).as("insert").containsExactly(List.of("2"));
	}

	private static void writeJunction(RoundTrip trip) throws Exception {
		Resource users = trip.load("Users");
		EClass usersClass = trip.eClass("Users");
		EObject alan = users.getContents().stream()
				.filter(o -> Long.valueOf(2).equals(o.eGet(usersClass.getEStructuralFeature("id")))).findFirst()
				.orElseThrow();
		EObject admin = trip.byId("Roles", "1");
		@SuppressWarnings("unchecked")
		List<EObject> roles = (List<EObject>) alan.eGet(usersClass.getEStructuralFeature("roles"));
		roles.add(admin);
		users.save(null);
		assertThat(trip.sql("SELECT ROLE_ID FROM USER_ROLES WHERE USER_ID = 2 ORDER BY ROLE_ID"))
				.containsExactly(List.of("1"), List.of("2"));
	}

	private static void writeAutoIncrement(RoundTrip trip) throws Exception {
		EClass counter = trip.eClass("Counter");
		EObject second = EcoreUtil.create(counter);
		second.eSet(counter.getEStructuralFeature("name"), "second");
		trip.insert(second);
		List<List<String>> rows = trip.sql("SELECT ID, NAME FROM COUNTER WHERE NAME = 'second'");
		assertThat(rows).hasSize(1);
		assertThat(rows.get(0).get(0)).isNotNull();
		// the generated id is handed back to the object
		assertThat(second.eGet(counter.getEStructuralFeature("id"))).hasToString(rows.get(0).get(0));
	}

	private static void writeDefaults(RoundTrip trip) throws Exception {
		EClass setting = trip.eClass("Setting");
		EObject fresh = EcoreUtil.create(setting);
		fresh.eSet(setting.getEStructuralFeature("id"), 2L);
		trip.insert(fresh);
		assertThat(trip.sql("SELECT MODE, PRIO FROM SETTING WHERE ID = 2")).containsExactly(List.of("auto", "3"));
	}

	// --- One case: schema, parse, unit ---

	record Case(String name, List<String> fixture, boolean views, ThrowingConsumer<RoundTrip> write) {
	}

	@FunctionalInterface
	interface ThrowingRunnable {
		void run() throws Exception;
	}

	@FunctionalInterface
	interface ThrowingConsumer<T> {
		void accept(T value) throws Exception;
	}

	/**
	 * The state of one case, set up on first use: a failing setup fails every aspect of the
	 * case with the same cause instead of aborting the whole suite.
	 */
	static final class RoundTrip {

		private final Case c;
		private final String puName;
		private final Supplier<Setup> setup;
		private Setup state;
		private RuntimeException setupFailure;
		private ResourceSet current;

		RoundTrip(Case c) {
			this.c = c;
			this.puName = "rt_" + c.name().replace('-', '_');
			this.setup = this::createSetup;
		}

		private Setup state() {
			if (isNull(state) && isNull(setupFailure)) {
				try {
					state = setup.get();
				} catch (RuntimeException e) {
					setupFailure = e;
				}
			}
			if (nonNull(setupFailure)) {
				throw setupFailure;
			}
			return state;
		}

		private Setup createSetup() {
			Map<String, Object> props = new LinkedHashMap<>(JpaTestSupport.jdbcProperties(puName));
			String url = (String) props.get(PersistenceUnitProperties.JDBC_URL);
			if (!JpaTestSupport.isPostgres() && !JpaTestSupport.isMariaDb()) {
				// H2 in memory: keep the database across connections
				url = url + ";DB_CLOSE_DELAY=-1";
				props.put(PersistenceUnitProperties.JDBC_URL, url);
			}
			Setup s = new Setup(url, (String) props.get(PersistenceUnitProperties.JDBC_USER),
					(String) props.get(PersistenceUnitProperties.JDBC_PASSWORD));
			try (Connection con = s.connection(); Statement stmt = con.createStatement()) {
				for (String sql : c.fixture()) {
					stmt.execute(sql);
				}
				s.schemaBefore = schemaSnapshot(con);
			} catch (SQLException e) {
				throw new IllegalStateException("fixture of case '" + c.name() + "' failed: " + e.getMessage(), e);
			}
			try {
				ParserSettings settings = ParserSettings.of(puName, "http://fennec.test/roundtrip");
				if (c.views()) {
					settings = new ParserSettings(settings.packageName(), settings.uriPrefix(), settings.version(),
							List.of(), true, true);
				}
				s.result = DatabaseEcoreParser.parse(s.dataSource(), settings);
			} catch (SQLException e) {
				throw new IllegalStateException("parsing case '" + c.name() + "' failed: " + e.getMessage(), e);
			}
			s.ePackage = s.result.ePackages().get(0);
			s.emf = JpaTckSupport.bootstrap(puName, mappings(s.ePackage), props, "none");
			return s;
		}

		/** The mapping onto the existing schema. Until #296: the default ORM derivation. */
		private static EntityMappings mappings(EPackage ePackage) {
			return new EntityMapper().createMappingsFromEPackage(ePackage);
		}

		void close() {
			if (nonNull(state) && nonNull(state.emf)) {
				state.emf.close();
			}
		}

		// --- aspects ---

		void verifyParse() throws SQLException {
			Setup s = state();
			assertThat(s.result.isSuccess()).as("parse diagnostics: %s", s.result.diagnostics()).isTrue();
			Diagnostic validation = Diagnostician.INSTANCE.validate(s.ePackage);
			assertThat(validation.getSeverity()).as("Ecore validation: %s", validation.getChildren())
					.isLessThan(Diagnostic.ERROR);
		}

		/** Every row of every table, compared column by column with plain SQL. */
		void verifyRead() throws Exception {
			Setup s = state();
			for (TableFacts table : s.result.tables()) {
				List<List<String>> expected = sqlRows(s, table);
				List<List<String>> actual = new ArrayList<>();
				for (EObject object : load(table.eClass().getName()).getContents()) {
					actual.add(modelRow(s, table, object));
				}
				assertThat(sorted(actual)).as("rows of %s", table.name()).isEqualTo(sorted(expected));
			}
		}

		/** Every reverse reference and every ManyToMany pair, compared with the SQL join. */
		void verifyNavigate() throws Exception {
			Setup s = state();
			for (TableFacts table : s.result.tables()) {
				for (TableFacts.ForeignKey fk : table.foreignKeys()) {
					EReference forward = fk.reference();
					if (isNull(forward) || isNull(forward.getEOpposite())) {
						continue;
					}
					EReference reverse = forward.getEOpposite();
					TableFacts parent = s.result.table(forward.getEReferenceType());
					Map<String, Integer> expected = new TreeMap<>();
					// children per parent key: the FK columns carry the parent's key values
					String sql = "SELECT " + String.join(", ", fk.columns()) + ", COUNT(*) FROM " + qualified(table)
							+ " WHERE " + fk.columns().get(0) + " IS NOT NULL GROUP BY " + String.join(", ", fk.columns());
					for (List<String> row : sql(sql)) {
						expected.put(String.join("|", row.subList(0, row.size() - 1)), Integer.valueOf(row.get(row.size() - 1)));
					}
					Map<String, Integer> actual = new TreeMap<>();
					for (EObject owner : load(parent.eClass().getName()).getContents()) {
						int size = ((Collection<?>) owner.eGet(reverse)).size();
						if (size > 0) {
							actual.put(key(s, parent, owner, fk.targetColumns()), size);
						}
					}
					assertThat(actual).as("%s.%s", parent.eClass().getName(), reverse.getName()).isEqualTo(expected);
				}
			}
			for (JunctionFacts junction : s.result.junctions()) {
				List<String> expected = new ArrayList<>();
				for (List<String> row : sql("SELECT " + String.join(", ", junction.sourceKey().columns()) + ", "
						+ String.join(", ", junction.targetKey().columns()) + " FROM " + qualified(junction))) {
					int split = junction.sourceKey().columns().size();
					expected.add(String.join("|", row.subList(0, split)) + " -> "
							+ String.join("|", row.subList(split, row.size())));
				}
				TableFacts owner = s.result.table(junction.reference().getEContainingClass());
				TableFacts target = s.result.table(junction.reference().getEReferenceType());
				List<String> actual = new ArrayList<>();
				for (EObject a : load(owner.eClass().getName()).getContents()) {
					for (Object b : (Collection<?>) a.eGet(junction.reference())) {
						actual.add(key(s, owner, a, junction.sourceKey().targetColumns()) + " -> "
								+ key(s, target, (EObject) b, junction.targetKey().targetColumns()));
					}
				}
				assertThat(sorted1(actual)).as("pairs of %s", junction.name()).isEqualTo(sorted1(expected));
			}
		}

		void verifySchemaUnchanged() throws SQLException {
			Setup s = state();
			try (Connection con = s.connection()) {
				assertThat(schemaSnapshot(con)).isEqualTo(s.schemaBefore);
			}
		}

		// --- helpers for aspects and writes ---

		EClass eClass(String name) {
			return (EClass) state().ePackage.getEClassifier(name);
		}

		Resource load(String typeName) throws Exception {
			Resource resource = resourceSet().getResource(uri(typeName), true);
			return resource;
		}

		/** One object by id — one {@code em.find} through the resource set. */
		EObject byId(String typeName, String id) throws Exception {
			EObject object = resourceSet().getEObject(uri(typeName).appendFragment(id), true);
			assertThat(object).as("%s#%s", typeName, id).isNotNull();
			return object;
		}

		void insert(EObject object) throws Exception {
			Resource resource = resourceSet().createResource(uri(object.eClass().getName()));
			resource.getContents().add(object);
			resource.save(null);
		}

		/** Deletes one object: resolved into a fresh resource, which then holds only that object. */
		void delete(String typeName, String id) throws Exception {
			fresh();
			Resource resource = byId(typeName, id).eResource();
			((PersistenceResource) resource).delete(null);
		}

		List<List<String>> sql(String query) throws SQLException {
			try (Connection con = state().connection(); Statement stmt = con.createStatement();
					ResultSet rs = stmt.executeQuery(query)) {
				List<List<String>> rows = new ArrayList<>();
				int count = rs.getMetaData().getColumnCount();
				while (rs.next()) {
					List<String> row = new ArrayList<>();
					for (int i = 1; i <= count; i++) {
						row.add(normalize(rs.getObject(i)));
					}
					rows.add(row);
				}
				return rows;
			}
		}

		/**
		 * The resource set of the current step. Each aspect starts from a fresh one
		 * ({@link #fresh()}), so a read sees the database and not an earlier step's cache,
		 * while the operations of one write step share it — as an application would.
		 */
		private ResourceSet resourceSet() {
			if (isNull(current)) {
				current = newResourceSet();
			}
			return current;
		}

		void fresh() {
			current = null;
		}

		private ResourceSet newResourceSet() {
			Setup s = state();
			ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet.getPackageRegistry().put(s.ePackage.getNsURI(), s.ePackage);
			resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("jpa", new JPAResourceFactory(s.emf));
			return resourceSet;
		}

		private URI uri(String typeName) {
			return URI.createURI("jpa://" + puName + "/" + typeName);
		}

		private List<List<String>> sqlRows(Setup s, TableFacts table) throws SQLException {
			List<String> columns = table.columns().stream().map(TableFacts.Column::name).toList();
			return sql("SELECT " + String.join(", ", columns) + " FROM " + qualified(table));
		}

		/** The row an object represents: attributes directly, FK columns through the referenced object. */
		private List<String> modelRow(Setup s, TableFacts table, EObject object) {
			List<String> row = new ArrayList<>();
			for (TableFacts.Column column : table.columns()) {
				EStructuralFeature feature = column.feature();
				if (feature instanceof EAttribute) {
					row.add(normalize(object.eGet(feature)));
				} else if (feature instanceof EReference reference) {
					EObject target = (EObject) object.eGet(reference);
					TableFacts.ForeignKey fk = table.foreignKeys().stream().filter(f -> f.reference() == reference)
							.findFirst().orElseThrow();
					String targetColumn = fk.targetColumns().get(fk.columns().indexOf(column.name()));
					row.add(isNull(target) ? null
							: normalize(target.eGet(s.result.table(reference.getEReferenceType()).column(targetColumn)
									.feature())));
				} else {
					row.add("<unmapped " + column.name() + ">");
				}
			}
			return row;
		}

		private String key(Setup s, TableFacts table, EObject object, List<String> columns) {
			return columns.stream().map(c -> normalize(object.eGet(table.column(c).feature())))
					.collect(Collectors.joining("|"));
		}

		private String qualified(TableFacts table) {
			return table.name();
		}

		private String qualified(JunctionFacts junction) {
			return junction.name();
		}

		private static List<String> sorted(List<List<String>> rows) {
			return rows.stream().map(r -> String.join(" | ", r.stream().map(String::valueOf).toList())).sorted().toList();
		}

		private static List<String> sorted1(List<String> rows) {
			return rows.stream().sorted().toList();
		}

		/** Tables and columns with their types — what DDL generation {@code none} must not touch. */
		private static List<String> schemaSnapshot(Connection con) throws SQLException {
			DatabaseMetaData md = con.getMetaData();
			String catalog = JpaTestSupport.isMariaDb() ? con.getCatalog() : null;
			String schema = JpaTestSupport.isMariaDb() ? null : con.getSchema();
			List<String> snapshot = new ArrayList<>();
			try (ResultSet rs = md.getColumns(catalog, schema, "%", "%")) {
				while (rs.next()) {
					snapshot.add(rs.getString("TABLE_NAME") + "." + rs.getString("COLUMN_NAME") + ":"
							+ rs.getString("TYPE_NAME") + ":" + rs.getInt("NULLABLE"));
				}
			}
			try (ResultSet rs = md.getTables(catalog, schema, "%", null)) {
				while (rs.next()) {
					snapshot.add("table " + rs.getString("TABLE_NAME") + ":" + rs.getString("TABLE_TYPE"));
				}
			}
			return snapshot.stream().sorted().toList();
		}
	}

	/** The mutable state of one case after setup. */
	static final class Setup {

		private final String url;
		private final String user;
		private final String password;
		private List<String> schemaBefore;
		private ParseResult result;
		private EPackage ePackage;
		private EntityManagerFactory emf;

		Setup(String url, String user, String password) {
			this.url = url;
			this.user = user;
			this.password = password;
		}

		Connection connection() throws SQLException {
			return DriverManager.getConnection(url, user, password);
		}

		DataSource dataSource() {
			return (DataSource) Proxy.newProxyInstance(Setup.class.getClassLoader(), new Class<?>[] { DataSource.class },
					(proxy, method, args) -> {
						if ("getConnection".equals(method.getName())) {
							return connection();
						}
						throw new UnsupportedOperationException(method.getName());
					});
		}
	}

	/**
	 * One comparable form for a value from JDBC and from the model: numbers without trailing
	 * zeros, java.sql temporals as java.time, enum literals as their literal, binary as hex.
	 */
	static String normalize(Object value) {
		if (isNull(value)) {
			return null;
		}
		if (value instanceof java.sql.Date date) {
			return date.toLocalDate().toString();
		}
		if (value instanceof java.sql.Time time) {
			return time.toLocalTime().toString();
		}
		if (value instanceof java.sql.Timestamp timestamp) {
			return timestamp.toLocalDateTime().toString();
		}
		if (value instanceof Boolean bool) {
			return bool.toString();
		}
		if (value instanceof Number number) {
			return new BigDecimal(number.toString()).stripTrailingZeros().toPlainString();
		}
		if (value instanceof Enumerator enumerator) {
			return enumerator.getLiteral();
		}
		if (value instanceof byte[] bytes) {
			return HexFormat.of().formatHex(bytes);
		}
		return value.toString();
	}

	private static String firstLine(Throwable failure) {
		String message = String.valueOf(failure.getMessage());
		String line = message.lines().filter(l -> !l.isBlank()).findFirst().orElse(message);
		return failure.getClass().getSimpleName() + ": " + (line.length() > 300 ? line.substring(0, 300) + "…" : line);
	}
}
