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
package org.eclipse.fennec.persistence.eclipselink.spi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.converter.DefaultConverterService;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import jakarta.persistence.EntityManagerFactory;

/**
 * Demonstrates that {@link EntityManagerFactoryConfigurator#configure()} currently
 * generates tables via {@code EDynamicHelper.addETypes} even when the user requests
 * {@code eclipselink.ddl-generation=none}. See GitHub issue #6.
 */
class EntityManagerFactoryConfiguratorDdlTest {

	private EntityManagerFactory emf;

	@AfterEach
	void tearDown() {
		if (emf != null && emf.isOpen()) {
			emf.close();
		}
	}

	@SuppressWarnings("restriction")
	@Test
	@DisplayName("ddl-generation=none must not create tables via EDynamicHelper")
	void ddlGenerationNone_doesNotCreateTables() throws Exception {
		EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
		pkg.setName("ddltest");
		pkg.setNsURI("http://test/ddltest");
		pkg.setNsPrefix("ddl");
		EClass thing = EcoreFactory.eINSTANCE.createEClass();
		thing.setName("Thing");
		EAttribute idAttr = EcoreFactory.eINSTANCE.createEAttribute();
		idAttr.setName("id");
		idAttr.setEType(EcorePackage.Literals.ELONG);
		idAttr.setID(true);
		thing.getEStructuralFeatures().add(idAttr);
		EAttribute labelAttr = EcoreFactory.eINSTANCE.createEAttribute();
		labelAttr.setName("label");
		labelAttr.setEType(EcorePackage.Literals.ESTRING);
		thing.getEStructuralFeatures().add(labelAttr);
		pkg.getEClassifiers().add(thing);

		EntityMapper mapper = new EntityMapper();
		EntityMappings mappings = mapper.createMappings(List.<EClassifier>of(thing));

		EPersistenceContextImpl pctx = new EPersistenceContextImpl("ddl_none_pu", List.of(mappings));
		URL metadataUrl = getClass().getProtectionDomain().getCodeSource().getLocation();
		pctx.setMetadataURL(metadataUrl);

		Bundle bundle = mock(Bundle.class);
		when(bundle.getEntry(anyString())).thenReturn(null);
		lenient().when(bundle.getResources(anyString()))
				.thenReturn(Collections.enumeration(Collections.<URL>emptyList()));
		BundleContext bctx = mock(BundleContext.class);
		when(bctx.getBundle()).thenReturn(bundle);

		ResourceSet rs = new ResourceSetImpl();
		rs.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);

		String jdbcUrl = "jdbc:h2:mem:ddl_none_" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1";
		Map<String, Object> props = new HashMap<>();
		props.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.NONE);
		props.put(PersistenceUnitProperties.JDBC_DRIVER, "org.h2.Driver");
		props.put(PersistenceUnitProperties.JDBC_URL, jdbcUrl);
		props.put(PersistenceUnitProperties.JDBC_USER, "sa");
		props.put(PersistenceUnitProperties.JDBC_PASSWORD, "");
		props.put(PersistenceUnitProperties.LOGGING_LEVEL, "WARNING");
		props.put(PersistenceUnitProperties.WEAVING, "false");
		props.put(PersistenceUnitProperties.TARGET_DATABASE, "Auto");
		props.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");

		ConverterService converter = new DefaultConverterService() { /* concrete */ };

		emf = EntityManagerFactoryConfigurator.Builder.create(bctx, rs)
				.context(pctx)
				.converter(converter)
				.properties(props)
				.build()
				.configure();

		try (Connection conn = DriverManager.getConnection(jdbcUrl, "sa", "")) {
			DatabaseMetaData md = conn.getMetaData();
			try (ResultSet tables = md.getTables(null, null, "THING", new String[] { "TABLE" })) {
				assertThat(tables.next())
						.as("With eclipselink.ddl-generation=none, the THING table must not be created by EDynamicHelper")
						.isFalse();
			}
		}
	}
}
