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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.converter.DefaultConverterService;
import org.eclipse.fennec.persistence.eclipselink.dynamic.DdlAction;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicHelper;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicPersistenceUnitInfo;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicType;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicTypeGenerator;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.epersistence.EPersistenceFactory;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.PersistenceProvider;

import jakarta.persistence.EntityManagerFactory;

/**
 * Non-OSGi bootstrap of the JPA/EclipseLink backend for TCK tests (H2 in-memory,
 * dynamic types generated from the given EClasses) — mirrors
 * {@code NonOsgiPersistenceTestBase} in {@code org.eclipse.fennec.persistence.test}.
 *
 * @author Mark Hoffmann
 * @since 16.07.2026
 */
final class JpaTckSupport {

	private JpaTckSupport() {
	}

	static EntityManagerFactory bootstrap(String puName, List<EClassifier> eClasses) {
		EntityMapper mapper = new EntityMapper();
		EntityMappings mappings = mapper.createMappings(new ArrayList<>(eClasses));
		// driver, url, credentials and dialect come from the flavor under test (issue #134, §6);
		// DDL generation deliberately stays in the picture — it is where dialects differ
		return bootstrap(puName, mappings, JpaTestSupport.jdbcProperties(puName), "create-or-extend-tables");
	}

	/**
	 * Bootstraps a unit from a given mapping onto given JDBC properties — the schema-import
	 * round trip (issue #298) maps onto a schema it created itself, with DDL generation
	 * {@code none}.
	 *
	 * @param puName the persistence unit name
	 * @param mappings the eorm mapping
	 * @param jdbcProperties driver, url, credentials and dialect
	 * @param ddlGeneration the EclipseLink DDL generation mode
	 * @return the entity manager factory
	 */
	static EntityManagerFactory bootstrap(String puName, EntityMappings mappings, Map<String, Object> jdbcProperties,
			String ddlGeneration) {
		DynamicClassLoader dcl = new DynamicClassLoader(JpaTckSupport.class.getClassLoader());
		Map<String, Object> props = new HashMap<>();
		props.put(PersistenceUnitProperties.DDL_GENERATION, ddlGeneration);
		props.put(PersistenceUnitProperties.DDL_GENERATION_MODE, "database");
		// -Djpa.test.logging=FINE surfaces the DDL a flavor rejects — create-or-extend
		// swallows DDL failures, so a missing table is otherwise diagnosed at first SELECT
		props.put(PersistenceUnitProperties.LOGGING_LEVEL,
				System.getProperty("jpa.test.logging", "WARNING"));
		props.put(PersistenceUnitProperties.WEAVING, "false");
		props.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");
		props.put(PersistenceUnitProperties.CLASSLOADER, dcl);
		props.putAll(jdbcProperties);

		PersistenceUnit persistenceUnit = EPersistenceFactory.eINSTANCE.createPersistenceUnit();
		persistenceUnit.setName(puName);
		persistenceUnit.setProperties(EPersistenceFactory.eINSTANCE.createProperties());
		URL puRoot = JpaTckSupport.class.getProtectionDomain().getCodeSource().getLocation();
		EDynamicPersistenceUnitInfo unitInfo = new EDynamicPersistenceUnitInfo(persistenceUnit, puRoot, props);

		PersistenceProvider provider = new PersistenceProvider();
		EntityManagerFactory emf = provider.createContainerEntityManagerFactory(unitInfo, props);
		verifyPlatform(emf);

		ConverterService converter = new DefaultConverterService();
		EDynamicTypeGenerator generator = new EDynamicTypeGenerator(dcl,
				JpaHelper.getServerSession(emf), puName, converter);
		List<EDynamicType> types = generator.createFromMappings(List.of(mappings));

		EDynamicHelper helper = new EDynamicHelper(emf, dcl);
		// "none" maps onto an existing schema (#298): no table, no sequence table, no FK — the
		// helper's boolean overload would create missing tables whatever the unit says
		boolean ddl = !"none".equals(ddlGeneration);
		helper.addETypes(ddl ? DdlAction.CREATE_TABLES : DdlAction.NONE, ddl, types);
		return emf;
	}

	/**
	 * The dialect is the point of a container flavor (#158): assert the platform EclipseLink
	 * actually chose instead of trusting the {@code TARGET_DATABASE} name — a typo or a
	 * platform-resolution surprise would otherwise run every test against the wrong dialect
	 * and report green for something never measured.
	 */
	private static void verifyPlatform(EntityManagerFactory emf) {
		Platform platform = JpaHelper.getServerSession(emf).getDatasourcePlatform();
		if (JpaTestSupport.isPostgres() && !platform.isPostgreSQL()) {
			throw new IllegalStateException("flavor postgres, but EclipseLink chose "
					+ platform.getClass().getName());
		}
		if (JpaTestSupport.isMariaDb() && !platform.isMariaDB()) {
			throw new IllegalStateException("flavor mariadb, but EclipseLink chose "
					+ platform.getClass().getName());
		}
	}
}
