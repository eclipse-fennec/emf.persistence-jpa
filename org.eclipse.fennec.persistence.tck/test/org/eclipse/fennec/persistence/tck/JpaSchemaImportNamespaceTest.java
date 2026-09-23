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

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.fennec.persistence.ecore.DatabaseEcoreParser;
import org.eclipse.fennec.persistence.ecore.ParseResult;
import org.eclipse.fennec.persistence.ecore.ParserSettings;
import org.eclipse.fennec.persistence.ecore.TableFacts;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The schema import reads exactly its namespace — on every flavor (issue #305).
 * <p>
 * On MariaDB the namespace is the database, which JDBC calls catalog; the schema level does
 * not exist there. A parser working on schemas only passed {@code catalog = null} and read
 * every database of the server, the system databases included. A second namespace with a
 * table of the same name makes such a leak visible on every flavor: its columns, its tables
 * and its foreign keys must stay out of the parsed package.
 *
 * @author Mark Hoffmann
 */
class JpaSchemaImportNamespaceTest {

	private String url;
	private String user;
	private String password;
	private String home;
	private String other;

	@BeforeEach
	void setUp() throws SQLException {
		Map<String, Object> props = JpaTestSupport.jdbcProperties("schemaimport");
		url = (String) props.get(PersistenceUnitProperties.JDBC_URL);
		user = (String) props.get(PersistenceUnitProperties.JDBC_USER);
		password = (String) props.get(PersistenceUnitProperties.JDBC_PASSWORD);
		if (JpaTestSupport.isMariaDb()) {
			// the TCK's EclipseLink workaround for exactly this leak — the parser must not
			// depend on it, so it is taken out here
			url = url.replace("?nullDatabaseMeansCurrent=true", "");
		} else if (!JpaTestSupport.isPostgres()) {
			// H2 in memory: keep the database across the parser's own connections
			url = url + ";DB_CLOSE_DELAY=-1";
		}
		String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
		other = JpaTestSupport.isPostgres() || JpaTestSupport.isMariaDb() ? "other_" + suffix : "OTHER_" + suffix.toUpperCase();

		try (Connection con = connection(); Statement stmt = con.createStatement()) {
			home = JpaTestSupport.isMariaDb() ? con.getCatalog() : con.getSchema();
			stmt.execute("CREATE TABLE CUSTOMER (ID BIGINT PRIMARY KEY, EMAIL VARCHAR(50))");
			stmt.execute("CREATE TABLE ORDERS (ID BIGINT PRIMARY KEY, CUSTOMER_ID BIGINT, "
					+ "FOREIGN KEY (CUSTOMER_ID) REFERENCES CUSTOMER(ID))");
			stmt.execute((JpaTestSupport.isMariaDb() ? "CREATE DATABASE " : "CREATE SCHEMA ") + other);
			stmt.execute("CREATE TABLE " + other + ".CUSTOMER (ID BIGINT PRIMARY KEY, SECRET VARCHAR(50))");
			stmt.execute("CREATE TABLE " + other + ".PAYROLL (ID BIGINT PRIMARY KEY, SALARY INT)");
			stmt.execute("CREATE TABLE " + other + ".INVOICE (ID BIGINT PRIMARY KEY, CUSTOMER_ID BIGINT, "
					+ "FOREIGN KEY (CUSTOMER_ID) REFERENCES " + home + ".CUSTOMER(ID))");
		}
	}

	@AfterEach
	void tearDown() throws SQLException {
		try (Connection con = connection(); Statement stmt = con.createStatement()) {
			stmt.execute(JpaTestSupport.isMariaDb() ? "DROP DATABASE " + other : "DROP SCHEMA " + other + " CASCADE");
			stmt.execute("DROP TABLE ORDERS");
			stmt.execute("DROP TABLE CUSTOMER");
		}
	}

	@Test
	void theCurrentNamespaceIsParsedAndNothingElse() throws SQLException {
		ParseResult result = DatabaseEcoreParser.parse(dataSource(), ParserSettings.of("shop", "http://test"));

		assertThat(result.ePackages()).hasSize(1);
		EPackage shop = result.ePackages().get(0);
		// no table of another namespace, no system database
		assertThat(classNames(shop)).containsExactlyInAnyOrder("Customer", "Orders");
		// no column of other.CUSTOMER
		assertThat(featureNames(shop, "Customer")).containsExactlyInAnyOrder("id", "email", "orders");
		assertThat(featureNames(shop, "Orders")).containsExactlyInAnyOrder("id", "customer");
		assertThat(result.isSuccess()).isTrue();
		assertThat(Diagnostician.INSTANCE.validate(shop).getSeverity()).isLessThan(Diagnostic.WARNING);

		// the facts name the namespace on the level the database addresses tables with
		TableFacts customer = result.table((EClass) shop.getEClassifier("Customer"));
		if (JpaTestSupport.isMariaDb()) {
			assertThat(customer.catalog()).isEqualTo(home);
			assertThat(customer.schema()).isNull();
		} else {
			assertThat(customer.schema()).isEqualTo(home);
			assertThat(customer.catalog()).isNull();
		}
	}

	@Test
	void aConfiguredSecondNamespaceIsItsOwnPackage() throws SQLException {
		ParseResult result = DatabaseEcoreParser.parse(dataSource(),
				ParserSettings.of("shop", "http://test").withNamespaces(home, other));

		assertThat(result.ePackages()).hasSize(2);
		EPackage homePackage = result.ePackages().get(0);
		EPackage otherPackage = result.ePackages().get(1);
		assertThat(classNames(homePackage)).containsExactlyInAnyOrder("Customer", "Orders");
		assertThat(featureNames(homePackage, "Customer")).containsExactlyInAnyOrder("id", "email", "orders");
		assertThat(classNames(otherPackage)).containsExactlyInAnyOrder("Customer", "Payroll", "Invoice");
		assertThat(featureNames(otherPackage, "Customer")).containsExactlyInAnyOrder("id", "secret");
		// the FK into the first namespace is cross-namespace: reported, the column stays an attribute
		assertThat(otherPackage.getEClassifier("Invoice")).isInstanceOf(EClass.class);
		EStructuralFeature customerId = ((EClass) otherPackage.getEClassifier("Invoice"))
				.getEStructuralFeature("customerId");
		assertThat(customerId).isNotNull();
		assertThat(result.diagnostics()).anySatisfy(d -> {
			assertThat(d.getSeverity()).isEqualTo(Diagnostic.WARNING);
			assertThat(d.getMessage()).containsIgnoringCase("FK target 'CUSTOMER' not found");
		});
	}

	private List<String> classNames(EPackage ePackage) {
		return ePackage.getEClassifiers().stream().filter(EClass.class::isInstance).map(EClassifier::getName).toList();
	}

	private List<String> featureNames(EPackage ePackage, String className) {
		return ((EClass) ePackage.getEClassifier(className)).getEStructuralFeatures().stream()
				.map(EStructuralFeature::getName).toList();
	}

	private Connection connection() throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}

	/** A plain DriverManager data source — the parser only asks for connections. */
	private DataSource dataSource() {
		return (DataSource) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { DataSource.class },
				(proxy, method, args) -> {
					if ("getConnection".equals(method.getName())) {
						return connection();
					}
					throw new UnsupportedOperationException(method.getName());
				});
	}
}
