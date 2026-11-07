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

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.model.query.builder.Expressions;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.eclipse.fennec.persistence.helper.ReservedWords;
import org.eclipse.fennec.persistence.helper.ReservedWords.Dialect;
import org.eclipse.fennec.persistence.helper.ReservedWords.Kind;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.eclipse.fennec.persistence.query.api.QueryableResource;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManagerFactory;

/**
 * Class and attribute names that are reserved words on the database under test (issue #399):
 * the derived mapping escapes them, so a model needs no persistence annotation for
 * {@code value}, {@code key}, {@code index} or a class {@code Order} — on every flavor.
 * <p>
 * The second half measures the {@link ReservedWords} list of the flavor's dialect against the
 * running database: every listed word is refused, every word only another dialect lists is
 * accepted. A database upgrade that reserves or frees a word fails here, not in a user's DDL.
 *
 * @author Mark Hoffmann
 * @since 07.11.2026
 */
class JpaReservedWordsTest {

	static {
		// H2 caches the JVM zone statically at first use (issue #79)
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private EntityManagerFactory emf;
	private String puName;
	private Map<String, Object> props;
	private String url;

	private EPackage ePackage;
	private EClass userClass;
	private EClass orderClass;
	private EClass itemClass;

	@AfterEach
	void tearDown() {
		if (nonNull(emf)) {
			emf.close();
			emf = null;
		}
	}

	// ------------------------------------------------------------------ derived mapping

	/** The reserved names are stored under their escaped form; the others stay as they are. */
	@Test
	void reservedNamesAreEscapedInTheStore() throws Exception {
		start();

		Map<String, TreeSet<String>> schema = schema();

		assertThat(schema).containsEntry("ORDER_",
				columns("GROUP_", "ID", "INDEX_", "KEY_", "POSITION", "USER_ID", "VALUE_"));
		assertThat(schema).containsEntry("USER_", columns("ID", "KEY_", "NAME"));
		assertThat(schema).containsEntry("ITEM", columns("ID", "ITEMS_ORDER", "ORDER_", "ORDER_ID", "VALUE_"));
	}

	/** Write, reload and query through the escaped columns, references and containment included. */
	@Test
	void reservedNamesRoundTripAndQuery() throws Exception {
		start();
		EObject alice = create(userClass, "u1", "name", "Alice");
		alice.eSet(feature(userClass, "key"), "key-u1");
		save("User", alice);
		save("Order",
				order("o1", "beta", 2, alice, item("i1", "first", 1), item("i2", "second", 2)),
				order("o2", "alpha", 1, alice),
				order("o3", "gamma", 3, null));

		emf.getCache().evictAll();
		EObject reloaded = load("Order").stream()
				.filter(o -> "o1".equals(o.eGet(feature(orderClass, "id"))))
				.findFirst().orElseThrow();
		assertThat(reloaded.eGet(feature(orderClass, "value"))).isEqualTo("beta");
		assertThat(reloaded.eGet(feature(orderClass, "index"))).isEqualTo(2);
		assertThat(reloaded.eGet(feature(orderClass, "position"))).isEqualTo(20);
		assertThat(reloaded.eGet(feature(orderClass, "group"))).isEqualTo("group-o1");
		assertThat(reloaded.eGet(feature(orderClass, "key"))).isEqualTo("key-o1");
		EObject owner = (EObject) reloaded.eGet(orderClass.getEStructuralFeature("owner"));
		assertThat(owner.eGet(feature(userClass, "name"))).isEqualTo("Alice");
		assertThat(owner.eGet(feature(userClass, "key"))).isEqualTo("key-u1");
		@SuppressWarnings("unchecked")
		EList<EObject> items = (EList<EObject>) reloaded.eGet(orderClass.getEStructuralFeature("items"));
		assertThat(items).extracting(i -> i.eGet(feature(itemClass, "value"))).containsExactly("first", "second");
		assertThat(items).extracting(i -> i.eGet(feature(itemClass, "order"))).containsExactly(1, 2);

		emf.getCache().evictAll();
		try (QueryResult result = queryable("Order").query(QueryBuilder.from(orderClass)
				.where(Expressions.path(feature(orderClass, "value")).ne("gamma"))
				.orderByAsc(feature(orderClass, "index"))
				.build())) {
			assertThat(result.objects().map(o -> o.eGet(feature(orderClass, "id")))).containsExactly("o2", "o1");
		}
	}

	// ------------------------------------------------------------------ the measured lists

	/**
	 * The list of the flavor's dialect is exact against the running database, measured with the
	 * statements {@link ReservedWords} documents. The candidates are every word any dialect lists.
	 */
	@Test
	void theWordListOfThisDatabaseIsExact() throws Exception {
		Dialect dialect = dialect();
		prepare("reserved_words");
		try (Connection con = connection()) {
			con.setAutoCommit(true);
			for (Kind kind : Kind.values()) {
				Set<String> candidates = new TreeSet<>();
				for (Dialect any : Dialect.values()) {
					candidates.addAll(ReservedWords.words(any, kind));
				}
				List<String> refusedButNotListed = new ArrayList<>();
				List<String> listedButAccepted = new ArrayList<>();
				for (String word : candidates) {
					boolean accepted = kind == Kind.COLUMN ? acceptsColumn(con, word) : acceptsTable(con, word);
					boolean listed = ReservedWords.isReserved(word, kind, dialect);
					if (!accepted && !listed) {
						refusedButNotListed.add(word);
					} else if (accepted && listed) {
						listedButAccepted.add(word);
					}
				}
				assertThat(refusedButNotListed).as("%s %s names refused but not listed", dialect, kind).isEmpty();
				assertThat(listedButAccepted).as("%s %s names listed but accepted", dialect, kind).isEmpty();
			}
		}
	}

	private static boolean acceptsColumn(Connection con, String w) {
		boolean accepted = run(con, "CREATE TABLE rw_probe (" + w + " INTEGER)",
				"INSERT INTO rw_probe (" + w + ") VALUES (1)",
				"SELECT t0." + w + " FROM rw_probe t0 WHERE t0." + w + " = 1 ORDER BY t0." + w,
				"UPDATE rw_probe SET " + w + " = 2 WHERE " + w + " = 1",
				"SELECT " + w + " FROM rw_probe WHERE " + w + " IS NOT NULL",
				"DELETE FROM rw_probe WHERE " + w + " = 2");
		run(con, "DROP TABLE IF EXISTS rw_probe");
		return accepted;
	}

	private static boolean acceptsTable(Connection con, String w) {
		boolean accepted = run(con, "CREATE TABLE " + w + " (a INTEGER)",
				"INSERT INTO " + w + " (a) VALUES (1)",
				"SELECT t0.a FROM " + w + " t0 WHERE t0.a = 1",
				"UPDATE " + w + " SET a = 2",
				"DELETE FROM " + w + " WHERE a = 2");
		run(con, "DROP TABLE IF EXISTS " + w);
		return accepted;
	}

	private static boolean run(Connection con, String... statements) {
		for (String statement : statements) {
			try (Statement stmt = con.createStatement()) {
				stmt.execute(statement);
			} catch (SQLException e) {
				return false;
			}
		}
		return true;
	}

	private static Dialect dialect() {
		if (JpaTestSupport.isPostgres()) {
			return Dialect.POSTGRESQL;
		}
		return JpaTestSupport.isMariaDb() ? Dialect.MARIADB : Dialect.H2;
	}

	// ------------------------------------------------------------------ model

	/**
	 * {@code User} and {@code Order} are reserved table names, {@code value}, {@code key},
	 * {@code index}, {@code group} and {@code order} reserved column names somewhere;
	 * {@code position} is reserved only as a MariaDB table name and stays a column as it is.
	 * {@code Order} has both {@code key} and {@code value} and is still no map entry (issue #401).
	 */
	private void model() {
		ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("reserved");
		ePackage.setNsURI("urn:reserved:test/1.0");
		ePackage.setNsPrefix("reserved");

		userClass = eClass("User");
		attribute(userClass, "id", EcorePackage.Literals.ESTRING).setID(true);
		attribute(userClass, "name", EcorePackage.Literals.ESTRING);
		attribute(userClass, "key", EcorePackage.Literals.ESTRING);

		itemClass = eClass("Item");
		attribute(itemClass, "id", EcorePackage.Literals.ESTRING).setID(true);
		attribute(itemClass, "value", EcorePackage.Literals.ESTRING);
		attribute(itemClass, "order", EcorePackage.Literals.EINT);

		orderClass = eClass("Order");
		attribute(orderClass, "id", EcorePackage.Literals.ESTRING).setID(true);
		attribute(orderClass, "value", EcorePackage.Literals.ESTRING);
		attribute(orderClass, "key", EcorePackage.Literals.ESTRING);
		attribute(orderClass, "index", EcorePackage.Literals.EINT);
		attribute(orderClass, "position", EcorePackage.Literals.EINT);
		attribute(orderClass, "group", EcorePackage.Literals.ESTRING);
		EReference owner = EcoreFactory.eINSTANCE.createEReference();
		owner.setName("owner");
		owner.setEType(userClass);
		orderClass.getEStructuralFeatures().add(owner);
		EReference items = EcoreFactory.eINSTANCE.createEReference();
		items.setName("items");
		items.setEType(itemClass);
		items.setContainment(true);
		items.setUpperBound(-1);
		orderClass.getEStructuralFeatures().add(items);
	}

	private EClass eClass(String name) {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(name);
		ePackage.getEClassifiers().add(eClass);
		return eClass;
	}

	private static EAttribute attribute(EClass owner, String name, EDataType type) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(type);
		owner.getEStructuralFeatures().add(attribute);
		return attribute;
	}

	private static EAttribute feature(EClass eClass, String name) {
		return (EAttribute) eClass.getEStructuralFeature(name);
	}

	private static EObject create(EClass eClass, String id, String feature, Object value) {
		EObject object = EcoreUtil.create(eClass);
		object.eSet(eClass.getEStructuralFeature("id"), id);
		object.eSet(eClass.getEStructuralFeature(feature), value);
		return object;
	}

	@SuppressWarnings("unchecked")
	private EObject order(String id, String value, int index, EObject owner, EObject... items) {
		EObject order = create(orderClass, id, "value", value);
		order.eSet(feature(orderClass, "index"), index);
		order.eSet(feature(orderClass, "position"), index * 10);
		order.eSet(feature(orderClass, "group"), "group-" + id);
		order.eSet(feature(orderClass, "key"), "key-" + id);
		order.eSet(orderClass.getEStructuralFeature("owner"), owner);
		((EList<EObject>) order.eGet(orderClass.getEStructuralFeature("items"))).addAll(List.of(items));
		return order;
	}

	private EObject item(String id, String value, int order) {
		EObject item = create(itemClass, id, "value", value);
		item.eSet(feature(itemClass, "order"), order);
		return item;
	}

	// ------------------------------------------------------------------ unit and store

	private void start() {
		model();
		prepare("reserved_names");
		emf = JpaTckSupport.bootstrap(puName,
				new EntityMapper().createMappings(new ArrayList<EClassifier>(ePackage.getEClassifiers())), props,
				"create-or-extend-tables");
		// DDL runs when the first entity manager is created
		emf.createEntityManager().close();
	}

	private void prepare(String name) {
		puName = name;
		props = new HashMap<>(JpaTestSupport.jdbcProperties(name));
		url = (String) props.get(PersistenceUnitProperties.JDBC_URL);
		if (!JpaTestSupport.isPostgres() && !JpaTestSupport.isMariaDb()) {
			// H2 in memory: keep the database while no connection is open
			url = url + ";DB_CLOSE_DELAY=-1";
			props.put(PersistenceUnitProperties.JDBC_URL, url);
		}
	}

	private Connection connection() throws SQLException {
		return DriverManager.getConnection(url, (String) props.get(PersistenceUnitProperties.JDBC_USER),
				(String) props.get(PersistenceUnitProperties.JDBC_PASSWORD));
	}

	/** Table → columns of the unit's own schema, upper-cased: each flavor folds names its own way. */
	private Map<String, TreeSet<String>> schema() throws SQLException {
		Map<String, TreeSet<String>> schema = new TreeMap<>();
		try (Connection con = connection()) {
			DatabaseMetaData md = con.getMetaData();
			try (ResultSet rs = md.getColumns(con.getCatalog(), con.getSchema(), "%", "%")) {
				while (rs.next()) {
					schema.computeIfAbsent(rs.getString("TABLE_NAME").toUpperCase(), k -> new TreeSet<>())
							.add(rs.getString("COLUMN_NAME").toUpperCase());
				}
			}
		}
		return schema;
	}

	private static TreeSet<String> columns(String... names) {
		return new TreeSet<>(List.of(names));
	}

	private void save(String type, EObject... objects) throws Exception {
		Resource resource = resourceSet().createResource(uriFor(type));
		resource.getContents().addAll(List.of(objects));
		resource.save(null);
	}

	private List<EObject> load(String type) throws Exception {
		Resource resource = resourceSet().createResource(uriFor(type));
		resource.load(null);
		return resource.getContents();
	}

	private QueryableResource queryable(String type) {
		return (QueryableResource) resourceSet().createResource(uriFor(type));
	}

	private ResourceSet resourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("jpa", new JPAResourceFactory(emf));
		return resourceSet;
	}

	private URI uriFor(String type) {
		return URI.createURI("jpa://" + puName + "/" + type);
	}
}
