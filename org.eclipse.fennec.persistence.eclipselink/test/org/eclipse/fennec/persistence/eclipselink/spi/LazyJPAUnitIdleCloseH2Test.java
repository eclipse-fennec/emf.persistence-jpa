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
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.persistence.converter.DefaultConverterService;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAUnit.Lease;
import org.eclipse.fennec.persistence.eclipselink.spi.impl.EPersistenceContextImpl;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import jakarta.persistence.EntityManager;

/**
 * Committed rows survive the idle close of a lazy unit on an embedded, file-based H2 without
 * {@code DB_CLOSE_DELAY} (issue #316): the idle close releases the last connection, H2 closes the
 * database, the next lease opens it again.
 */
class LazyJPAUnitIdleCloseH2Test {

	@TempDir
	Path dir;

	@Test
	void committedRowsSurviveIdleCloseAndReopen() throws Exception {
		String url = "jdbc:h2:file:" + dir.resolve("bath").toAbsolutePath();
		JdbcDataSource dataSource = new JdbcDataSource();
		dataSource.setURL(url);
		dataSource.setUser("sa");
		dataSource.setPassword("");

		EClass thing = thingClass();
		LazyJPAUnit unit = new LazyJPAUnit("bath", () -> buildFactory(thing, dataSource), 100);
		int batches = 7;
		int perBatch = 300;
		try {
			for (int b = 0; b < batches; b++) {
				try (Lease lease = unit.lease(); EntityManager em = lease.createEntityManager()) {
					ClassDescriptor descriptor = lease.getServerSession().getDescriptorForAlias("Thing");
					em.getTransaction().begin();
					for (int i = 0; i < perBatch; i++) {
						EObject object = (EObject) descriptor.getInstantiationPolicy().buildNewInstance();
						object.eSet(thing.getEStructuralFeature("id"), "b" + b + "-" + i);
						object.eSet(thing.getEStructuralFeature("batch"), b);
						em.persist(object);
					}
					em.getTransaction().commit();
				}
			}
			assertThat(count(unit)).isEqualTo(batches * perBatch);

			awaitIdleClose(unit);

			assertThat(count(unit)).as("rows after idle close and reopen").isEqualTo(batches * perBatch);
		} finally {
			unit.dispose();
		}
		try (Connection connection = DriverManager.getConnection(url, "sa", "");
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM THING")) {
			rs.next();
			assertThat(rs.getLong(1)).as("rows in the file").isEqualTo(batches * perBatch);
		}
	}

	private static long count(LazyJPAUnit unit) {
		try (Lease lease = unit.lease(); EntityManager em = lease.createEntityManager()) {
			return ((Number) em.createNativeQuery("SELECT COUNT(*) FROM THING").getSingleResult()).longValue();
		}
	}

	private static void awaitIdleClose(LazyJPAUnit unit) throws InterruptedException {
		long deadline = System.currentTimeMillis() + 10_000;
		while (unit.isFactoryOpen() && System.currentTimeMillis() < deadline) {
			Thread.sleep(20);
		}
		assertThat(unit.isFactoryOpen()).as("idle close happened").isFalse();
	}

	private static EClass thingClass() {
		EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
		pkg.setName("idle");
		pkg.setNsURI("http://test/idle/" + UUID.randomUUID());
		pkg.setNsPrefix("idle");
		EClass thing = EcoreFactory.eINSTANCE.createEClass();
		thing.setName("Thing");
		EAttribute id = EcoreFactory.eINSTANCE.createEAttribute();
		id.setName("id");
		id.setEType(EcorePackage.Literals.ESTRING);
		id.setID(true);
		thing.getEStructuralFeatures().add(id);
		EAttribute batch = EcoreFactory.eINSTANCE.createEAttribute();
		batch.setName("batch");
		batch.setEType(EcorePackage.Literals.EINT);
		thing.getEStructuralFeatures().add(batch);
		pkg.getEClassifiers().add(thing);
		return thing;
	}

	private static jakarta.persistence.EntityManagerFactory buildFactory(EClass thing, JdbcDataSource dataSource) {
		EntityMappings mappings = new EntityMapper().createMappings(new ArrayList<EClassifier>(List.of(thing)));
		EPersistenceContextImpl pctx = new EPersistenceContextImpl("bath", List.of(mappings));
		URL metadataUrl = LazyJPAUnitIdleCloseH2Test.class.getProtectionDomain().getCodeSource().getLocation();
		pctx.setMetadataURL(metadataUrl);

		Bundle bundle = mock(Bundle.class);
		when(bundle.getEntry(anyString())).thenReturn(null);
		try {
			lenient().when(bundle.getResources(anyString()))
					.thenReturn(Collections.enumeration(Collections.<URL>emptyList()));
		} catch (java.io.IOException e) {
			throw new IllegalStateException(e);
		}
		BundleContext bctx = mock(BundleContext.class);
		when(bctx.getBundle()).thenReturn(bundle);

		ResourceSet rs = new ResourceSetImpl();
		rs.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);

		Map<String, Object> props = new HashMap<>();
		props.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_OR_EXTEND);
		props.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, dataSource);
		props.put(PersistenceUnitProperties.LOGGING_LEVEL, "WARNING");
		props.put(PersistenceUnitProperties.WEAVING, "false");
		props.put(PersistenceUnitProperties.TARGET_DATABASE, "Auto");
		props.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");
		try {
			return EntityManagerFactoryConfigurator.Builder.create(bctx, rs)
					.context(pctx)
					.converter(new DefaultConverterService())
					.properties(props)
					.build()
					.configure();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
}
