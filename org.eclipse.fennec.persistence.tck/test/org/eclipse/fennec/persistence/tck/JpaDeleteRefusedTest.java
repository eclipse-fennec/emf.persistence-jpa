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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.diagnostic.PersistenceDiagnostic;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.fennec.persistence.repository.spi.AbstractRepository;
import org.eclipse.fennec.persistence.resource.PersistenceResource;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.JpaHelper;
import org.h2.api.Trigger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManagerFactory;

/**
 * A delete the store suppresses without an error (issue #371): the principal may read the row
 * but not delete it. The {@code DELETE} affects no row, and without a version attribute
 * EclipseLink does not look at the row count — the delete used to report success.
 * <p>
 * Built per flavor, since a store-side refusal is store-specific:
 * <ul>
 * <li><b>PostgreSQL</b> — the real case: row level security with a {@code SELECT} policy and
 * none for {@code DELETE}, used through a role that is not the owner (an owner and a superuser
 * bypass the policies).</li>
 * <li><b>h2</b> — the table becomes a view whose {@code INSTEAD OF DELETE} trigger does
 * nothing: the row stays, no error.</li>
 * <li><b>MariaDB</b> — skipped: a trigger can only fail a delete there, not suppress it.</li>
 * </ul>
 *
 * @author Mark Hoffmann
 * @since 12.10.2026
 */
class JpaDeleteRefusedTest {

	static {
		// Same doctrine as AbstractPersistenceTCK (issue #79): H2 caches the JVM zone
		// statically at first use, and this class does not extend the abstract TCK.
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static final String PU_NAME = "refused";
	private static final String RLS_ROLE = "fennec_rls_reader";
	private static final String RLS_PASSWORD = "rls";

	private EPackage ledgerPackage;
	private EClass ledgerClass;

	private EntityManagerFactory ownerUnit;
	private EntityManagerFactory restrictedUnit;
	private JpaStoreProbe probe;

	@BeforeEach
	void setUp() throws Exception {
		assumeFalse(JpaTestSupport.isMariaDb(), "MariaDB cannot suppress a delete without an error");
		buildModel();
		List<EClassifier> eClasses = List.of(ledgerClass);
		EntityMappings mappings = new EntityMapper().createMappings(eClasses);
		Map<String, Object> jdbc = JpaTestSupport.jdbcProperties(PU_NAME);
		ownerUnit = JpaTckSupport.bootstrap(PU_NAME, mappings, jdbc, "create-or-extend-tables");
		probe = new JpaStoreProbe(JpaHelper.getServerSession(ownerUnit), jdbc);

		save(ownerUnit, ledger("l1"), ledger("l2"));
		String table = JpaHelper.getServerSession(ownerUnit).getDescriptorForAlias("Ledger").getTables().get(0)
				.getQualifiedName();
		if (JpaTestSupport.isPostgres()) {
			restrictedUnit = JpaTckSupport.bootstrap(PU_NAME, mappings, rowLevelSecurity(jdbc, table), "none");
		} else {
			suppressDeletes(jdbc, table);
			restrictedUnit = ownerUnit;
		}
	}

	@AfterEach
	void tearDown() {
		if (nonNull(restrictedUnit) && restrictedUnit != ownerUnit) {
			restrictedUnit.close();
		}
		if (nonNull(ownerUnit)) {
			ownerUnit.close();
		}
		restrictedUnit = null;
		ownerUnit = null;
	}

	private void buildModel() {
		ledgerClass = EcoreFactory.eINSTANCE.createEClass();
		ledgerClass.setName("Ledger");
		addAttribute(ledgerClass, "lid", true);
		addAttribute(ledgerClass, "entry", false);

		ledgerPackage = EcoreFactory.eINSTANCE.createEPackage();
		ledgerPackage.setName("refused");
		ledgerPackage.setNsURI("urn:refused:jpa/1.0");
		ledgerPackage.setNsPrefix("refused");
		ledgerPackage.getEClassifiers().add(ledgerClass);
	}

	private static void addAttribute(EClass eClass, String name, boolean id) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(EcorePackage.Literals.ESTRING);
		attribute.setID(id);
		eClass.getEStructuralFeatures().add(attribute);
	}

	// ------------------------------------------------------------------ the shape

	/** The setup itself: the restricted principal sees the rows. */
	@Test
	void theRestrictedPrincipalReadsTheRows() throws Exception {
		Resource resource = resourceSet(restrictedUnit).getResource(uriFor("Ledger"), true);

		assertThat(resource.getContents()).hasSize(2);
	}

	// ------------------------------------------------------------------ refusal

	/** The consumer's flow: read through the repository, delete through the repository. */
	@Test
	void aSuppressedDeleteIsRefusedNotReportedAsSuccess() throws Exception {
		AbstractRepository repository = repository(restrictedUnit);
		EObject ledger = repository.getEObject(ledgerClass, "l1");
		assertThat(ledger).isNotNull();

		assertThatThrownBy(() -> repository.delete(ledger))
				.isInstanceOf(IOException.class);

		assertThat(probe.ids(ledgerClass)).as("the store kept the row").containsExactlyInAnyOrder("l1", "l2");
	}

	/** At resource level the refusal is classified, and nothing of the delete is applied. */
	@Test
	void theRefusalIsClassifiedAndNothingIsApplied() throws Exception {
		Resource resource = resourceSet(restrictedUnit).getResource(uriFor("Ledger"), true);

		assertThatThrownBy(() -> ((PersistenceResource) resource).delete(null))
				.isInstanceOf(IOException.class);

		assertThat(resource.getErrors())
				.singleElement()
				.satisfies(error -> {
					assertThat(error).isInstanceOf(PersistenceDiagnostic.class);
					assertThat(((PersistenceDiagnostic) error).code())
							.isEqualTo(PersistenceDiagnostic.CODE_DELETE_REFUSED);
					assertThat(error.getMessage()).contains("Ledger");
				});
		assertThat(resource.getContents()).as("the contents stay on a refused delete").hasSize(2);
		assertThat(probe.ids(ledgerClass)).containsExactlyInAnyOrder("l1", "l2");
	}

	// ------------------------------------------------------------------ store setup

	/**
	 * PostgreSQL: a role that may read and write but not delete, through row level security.
	 * The policies bind every role but the owner and superusers, so the restricted unit logs in
	 * as a role of its own.
	 */
	private Map<String, Object> rowLevelSecurity(Map<String, Object> jdbc, String table) throws SQLException {
		try (Connection connection = connect(jdbc); Statement statement = connection.createStatement()) {
			String schema;
			try (ResultSet rs = statement.executeQuery("SELECT current_schema()")) {
				rs.next();
				schema = rs.getString(1);
			}
			statement.execute("DO $$ BEGIN IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = '" + RLS_ROLE
					+ "') THEN CREATE ROLE " + RLS_ROLE + " LOGIN PASSWORD '" + RLS_PASSWORD + "'; END IF; END $$");
			statement.execute("GRANT USAGE ON SCHEMA \"" + schema + "\" TO " + RLS_ROLE);
			statement.execute("GRANT SELECT, INSERT, UPDATE, DELETE ON " + table + " TO " + RLS_ROLE);
			statement.execute("ALTER TABLE " + table + " ENABLE ROW LEVEL SECURITY");
			statement.execute("DROP POLICY IF EXISTS ledger_read ON " + table);
			statement.execute("CREATE POLICY ledger_read ON " + table + " FOR SELECT USING (true)");
		}
		Map<String, Object> restricted = new HashMap<>(jdbc);
		restricted.put(PersistenceUnitProperties.JDBC_USER, RLS_ROLE);
		restricted.put(PersistenceUnitProperties.JDBC_PASSWORD, RLS_PASSWORD);
		return restricted;
	}

	/** h2: the table behind a view whose delete trigger does nothing. */
	private void suppressDeletes(Map<String, Object> jdbc, String table) throws SQLException {
		try (Connection connection = connect(jdbc); Statement statement = connection.createStatement()) {
			statement.execute("ALTER TABLE " + table + " RENAME TO " + table + "_DATA");
			statement.execute("CREATE VIEW " + table + " AS SELECT * FROM " + table + "_DATA");
			statement.execute("CREATE TRIGGER " + table + "_KEEP INSTEAD OF DELETE ON " + table
					+ " FOR EACH ROW CALL \"" + KeepRows.class.getName() + "\"");
		}
	}

	/** The h2 trigger that answers a delete with nothing. */
	public static final class KeepRows implements Trigger {

		@Override
		public void fire(Connection connection, Object[] oldRow, Object[] newRow) {
			// the row stays — like a DELETE a row level security policy filters out
		}
	}

	private static Connection connect(Map<String, Object> jdbc) throws SQLException {
		return DriverManager.getConnection((String) jdbc.get(PersistenceUnitProperties.JDBC_URL),
				(String) jdbc.get(PersistenceUnitProperties.JDBC_USER),
				(String) jdbc.get(PersistenceUnitProperties.JDBC_PASSWORD));
	}

	// ------------------------------------------------------------------ helpers

	private AbstractRepository repository(EntityManagerFactory unit) {
		return new AbstractRepository("refused", uriFor("Ledger").trimSegments(1), () -> resourceSet(unit), null,
				null, null) {
			// all behaviour is generic
		};
	}

	private ResourceSet resourceSet(EntityManagerFactory unit) {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(ledgerPackage.getNsURI(), ledgerPackage);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("jpa", new JPAResourceFactory(unit));
		return resourceSet;
	}

	private URI uriFor(String typeName) {
		return URI.createURI("jpa://" + PU_NAME + "/" + typeName);
	}

	private EObject ledger(String id) {
		EObject ledger = EcoreUtil.create(ledgerClass);
		ledger.eSet(ledgerClass.getEStructuralFeature("lid"), id);
		ledger.eSet(ledgerClass.getEStructuralFeature("entry"), "entry " + id);
		return ledger;
	}

	private void save(EntityManagerFactory unit, EObject... ledgers) throws Exception {
		Resource resource = resourceSet(unit).createResource(uriFor("Ledger"));
		resource.getContents().addAll(List.of(ledgers));
		resource.save(null);
	}
}
