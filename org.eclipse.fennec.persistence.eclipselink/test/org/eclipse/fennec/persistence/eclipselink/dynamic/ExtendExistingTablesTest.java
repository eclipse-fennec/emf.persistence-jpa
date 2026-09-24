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
package org.eclipse.fennec.persistence.eclipselink.dynamic;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.persistence.epersistence.EPersistenceFactory;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManagerFactory;

/**
 * {@code create-or-extend-tables} on an existing schema adds only the columns that are missing
 * (issue #320). On H2, which folds identifiers to upper case, the extend step used to look up
 * {@code id} among the existing {@code ID} case-sensitively and issued an {@code ALTER TABLE ADD}
 * for every column, each failing with a duplicate column that EclipseLink swallows.
 */
class ExtendExistingTablesTest {

	private final List<EntityManagerFactory> factories = new ArrayList<>();
	private String url;
	private EClass person;

	@BeforeEach
	void setUp() {
		url = "jdbc:h2:mem:extend_" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1";
		EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
		pkg.setName("extend");
		pkg.setNsURI("http://test/extend/" + UUID.randomUUID());
		pkg.setNsPrefix("extend");
		person = EcoreFactory.eINSTANCE.createEClass();
		person.setName("Person");
		pkg.getEClassifiers().add(person);
		addAttribute("id", EcorePackage.Literals.ESTRING).setID(true);
		addAttribute("name", EcorePackage.Literals.ESTRING);
		addAttribute("birthDate", EcorePackage.Literals.EDATE);
	}

	@AfterEach
	void tearDown() {
		factories.stream().filter(EntityManagerFactory::isOpen).forEach(EntityManagerFactory::close);
	}

	@Test
	void reopeningAnExistingSchemaAltersNothing() {
		deploy(new CapturingLog());

		CapturingLog second = new CapturingLog();
		deploy(second);

		assertThat(second.alterStatements()).isEmpty();
	}

	@Test
	void aColumnTheSchemaLacksIsStillAdded() throws Exception {
		deploy(new CapturingLog());
		addAttribute("nickname", EcorePackage.Literals.ESTRING);

		CapturingLog second = new CapturingLog();
		deploy(second);

		assertThat(second.alterStatements()).singleElement()
				.satisfies(sql -> assertThat(sql).containsIgnoringCase("ADD nickname"));
		assertThat(columns()).contains("ID", "NAME", "BIRTHDATE", "NICKNAME");
	}

	// ── Helpers ─────────────────────────────────────────────────────────────

	private EAttribute addAttribute(String name, EDataType type) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(type);
		person.getEStructuralFeatures().add(attribute);
		return attribute;
	}

	private List<String> columns() throws Exception {
		List<String> names = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection(url, "sa", "");
				ResultSet rs = connection.getMetaData().getColumns(null, null, "PERSON", null)) {
			while (rs.next()) {
				names.add(rs.getString("COLUMN_NAME"));
			}
		}
		return names;
	}

	private void deploy(CapturingLog log) {
		DynamicClassLoader dcl = new DynamicClassLoader(getClass().getClassLoader());
		Map<String, Object> props = new HashMap<>();
		props.put(PersistenceUnitProperties.CLASSLOADER, dcl);
		props.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.NONE);
		props.put(PersistenceUnitProperties.JDBC_DRIVER, "org.h2.Driver");
		props.put(PersistenceUnitProperties.JDBC_URL, url);
		props.put(PersistenceUnitProperties.JDBC_USER, "sa");
		props.put(PersistenceUnitProperties.JDBC_PASSWORD, "");
		props.put(PersistenceUnitProperties.WEAVING, "false");
		props.put(PersistenceUnitProperties.TARGET_DATABASE, "Auto");
		props.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");

		PersistenceUnit pu = EPersistenceFactory.eINSTANCE.createPersistenceUnit();
		pu.setName("extend_" + factories.size());
		pu.setProperties(EPersistenceFactory.eINSTANCE.createProperties());
		URL puRoot = getClass().getProtectionDomain().getCodeSource().getLocation();
		EDynamicPersistenceUnitInfo pui = new EDynamicPersistenceUnitInfo(pu, puRoot, props);

		EntityManagerFactory emf = new PersistenceProvider().createContainerEntityManagerFactory(pui, props);
		factories.add(emf);
		Server session = JpaHelper.getServerSession(emf);
		session.setSessionLog(log);

		EDynamicTypeGenerator generator = new EDynamicTypeGenerator(dcl, session, pu.getName());
		List<EDynamicType> types = generator.createFromMapping(new EntityMapper().createMappings(List.of(person)));
		new EDynamicHelper(emf, dcl).addETypes(DdlAction.CREATE_OR_EXTEND_TABLES, true, types);
	}

	/** Records every SQL statement the session logs. */
	private static final class CapturingLog extends AbstractSessionLog {

		private final List<String> sql = new ArrayList<>();

		CapturingLog() {
			setLevel(SessionLog.ALL);
		}

		@Override
		public synchronized void log(SessionLogEntry entry) {
			if (SessionLog.SQL.equals(entry.getNameSpace()) && entry.getMessage() != null) {
				sql.add(entry.getMessage());
			}
		}

		@Override
		public boolean shouldLog(int level, String category) {
			return SessionLog.SQL.equals(category);
		}

		synchronized List<String> alterStatements() {
			return sql.stream().filter(s -> s.toUpperCase().startsWith("ALTER TABLE PERSON ADD")).toList();
		}
	}
}
