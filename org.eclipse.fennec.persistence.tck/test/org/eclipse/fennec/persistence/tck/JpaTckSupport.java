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

		DynamicClassLoader dcl = new DynamicClassLoader(JpaTckSupport.class.getClassLoader());
		Map<String, Object> props = new HashMap<>();
		props.put(PersistenceUnitProperties.DDL_GENERATION, "create-or-extend-tables");
		props.put(PersistenceUnitProperties.DDL_GENERATION_MODE, "database");
		props.put(PersistenceUnitProperties.LOGGING_LEVEL, "WARNING");
		props.put(PersistenceUnitProperties.WEAVING, "false");
		props.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");
		props.put(PersistenceUnitProperties.CLASSLOADER, dcl);
		// driver, url, credentials and dialect come from the flavor under test (issue #134, §6);
		// DDL generation deliberately stays in the picture — it is where dialects differ
		props.putAll(JpaTestSupport.jdbcProperties(puName));

		PersistenceUnit persistenceUnit = EPersistenceFactory.eINSTANCE.createPersistenceUnit();
		persistenceUnit.setName(puName);
		persistenceUnit.setProperties(EPersistenceFactory.eINSTANCE.createProperties());
		URL puRoot = JpaTckSupport.class.getProtectionDomain().getCodeSource().getLocation();
		EDynamicPersistenceUnitInfo unitInfo = new EDynamicPersistenceUnitInfo(persistenceUnit, puRoot, props);

		PersistenceProvider provider = new PersistenceProvider();
		EntityManagerFactory emf = provider.createContainerEntityManagerFactory(unitInfo, props);

		ConverterService converter = new DefaultConverterService();
		EDynamicTypeGenerator generator = new EDynamicTypeGenerator(dcl,
				JpaHelper.getServerSession(emf), puName, converter);
		List<EDynamicType> types = generator.createFromMappings(List.of(mappings));

		EDynamicHelper helper = new EDynamicHelper(emf, dcl);
		helper.addETypes(true, true, types);
		return emf;
	}
}
