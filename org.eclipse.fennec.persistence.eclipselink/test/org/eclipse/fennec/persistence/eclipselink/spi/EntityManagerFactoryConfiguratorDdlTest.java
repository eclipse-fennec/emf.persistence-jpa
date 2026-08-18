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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

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
import org.eclipse.fennec.persistence.eclipselink.spi.impl.EPersistenceContextImpl;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import jakarta.persistence.EntityManagerFactory;

/**
 * Verifies that {@link EntityManagerFactoryConfigurator#configure()} honors the
 * {@code eclipselink.ddl-generation} property when delegating to
 * {@code EDynamicHelper.addETypes}. See GitHub issue #6.
 */
class EntityManagerFactoryConfiguratorDdlTest {

	private static final String TABLE_NAME = "THING";

	private EntityManagerFactory emf;

	@AfterEach
	void tearDown() {
		if (emf != null && emf.isOpen()) {
			emf.close();
		}
		emf = null;
	}

	@Test
	@DisplayName("ddl-generation=none must not create tables via EDynamicHelper")
	void ddlGenerationNone_doesNotCreateTables() throws Exception {
		assertThat(bootstrapAndCheckTable(PersistenceUnitProperties.NONE))
				.as("With eclipselink.ddl-generation=none, the %s table must not be created", TABLE_NAME)
				.isFalse();
	}

	@Test
	@DisplayName("missing ddl-generation falls back to NONE — no tables")
	void ddlGenerationMissing_doesNotCreateTables() throws Exception {
		assertThat(bootstrapAndCheckTable(null))
				.as("Without eclipselink.ddl-generation, the %s table must not be created", TABLE_NAME)
				.isFalse();
	}

	@ParameterizedTest(name = "ddl-generation={0} creates tables")
	@ValueSource(strings = {
			PersistenceUnitProperties.CREATE_ONLY,
			PersistenceUnitProperties.DROP_AND_CREATE,
			PersistenceUnitProperties.CREATE_OR_EXTEND })
	void ddlGenerationCreateModes_createTables(String mode) throws Exception {
		assertThat(bootstrapAndCheckTable(mode))
				.as("With eclipselink.ddl-generation=%s, the %s table must be created", mode, TABLE_NAME)
				.isTrue();
	}

	@Test
	@DisplayName("unknown ddl-generation value logs a warning and falls back to NONE")
	void unknownDdlGeneration_logsWarningAndSkipsDdl() throws Exception {
		Logger configuratorLog = Logger.getLogger(EntityManagerFactoryConfigurator.class.getName());
		CapturingLogHandler handler = new CapturingLogHandler();
		configuratorLog.addHandler(handler);
		try {
			boolean tableExists = bootstrapAndCheckTable("bogus-mode");
			assertThat(tableExists)
					.as("Unknown ddl-generation value must not create the %s table", TABLE_NAME)
					.isFalse();
			assertThat(handler.records)
					.as("A WARNING about the unknown ddl-generation value must be logged")
					.anySatisfy(r -> {
						assertThat(r.getLevel()).isEqualTo(Level.WARNING);
						assertThat(r.getParameters()).contains("bogus-mode");
					});
		} finally {
			configuratorLog.removeHandler(handler);
		}
	}

	private boolean bootstrapAndCheckTable(String ddlGenerationValue) throws Exception {
		EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
		pkg.setName("ddltest");
		pkg.setNsURI("http://test/ddltest/" + UUID.randomUUID());
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

		EPersistenceContextImpl pctx = new EPersistenceContextImpl("ddl_pu_" + UUID.randomUUID(),
				List.of(mappings));
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

		String jdbcUrl = "jdbc:h2:mem:ddl_" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1";
		Map<String, Object> props = new HashMap<>();
		if (ddlGenerationValue != null) {
			props.put(PersistenceUnitProperties.DDL_GENERATION, ddlGenerationValue);
		}
		props.put(PersistenceUnitProperties.JDBC_DRIVER, "org.h2.Driver");
		props.put(PersistenceUnitProperties.JDBC_URL, jdbcUrl);
		props.put(PersistenceUnitProperties.JDBC_USER, "sa");
		props.put(PersistenceUnitProperties.JDBC_PASSWORD, "");
		props.put(PersistenceUnitProperties.LOGGING_LEVEL, "WARNING");
		props.put(PersistenceUnitProperties.WEAVING, "false");
		props.put(PersistenceUnitProperties.TARGET_DATABASE, "Auto");
		props.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");

		ConverterService converter = new DefaultConverterService();

		emf = EntityManagerFactoryConfigurator.Builder.create(bctx, rs)
				.context(pctx)
				.converter(converter)
				.properties(props)
				.build()
				.configure();

		try (Connection conn = DriverManager.getConnection(jdbcUrl, "sa", "")) {
			DatabaseMetaData md = conn.getMetaData();
			try (ResultSet tables = md.getTables(null, null, TABLE_NAME, new String[] { "TABLE" })) {
				return tables.next();
			}
		}
	}

	private static final class CapturingLogHandler extends Handler {
		final List<LogRecord> records = new ArrayList<>();

		@Override
		public void publish(LogRecord record) {
			records.add(record);
		}

		@Override
		public void flush() { /* no-op */ }

		@Override
		public void close() { /* no-op */ }
	}
}
