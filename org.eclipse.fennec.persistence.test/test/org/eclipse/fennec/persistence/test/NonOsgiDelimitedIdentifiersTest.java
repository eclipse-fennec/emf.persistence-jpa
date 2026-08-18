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
package org.eclipse.fennec.persistence.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.converter.DefaultConverterService;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicHelper;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicPersistenceUnitInfo;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicType;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicTypeGenerator;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.eorm.PersistenceUnitDefaults;
import org.eclipse.fennec.persistence.eorm.PersistenceUnitMetadata;
import org.eclipse.fennec.persistence.epersistence.EPersistenceFactory;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Non-OSGi integration test for the {@code delimitedIdentifiers} flag from
 * {@code persistenceUnitMetadata.persistenceUnitDefaults}. When the flag is set,
 * column and table names that match SQL reserved words must be emitted with
 * delimiters so that DDL and queries succeed against H2. See GitHub issue #8.
 */
@SuppressWarnings("restriction")
class NonOsgiDelimitedIdentifiersTest {

	@TempDir
	File tempDir;

	private EPackage pkg;
	private EClass bookClass;
	private EntityManagerFactory emf;
	private Server serverSession;

	@BeforeEach
	void setUp() {
		pkg = EcoreFactory.eINSTANCE.createEPackage();
		pkg.setName("delim_test");
		pkg.setNsURI("http://test/delimited");
		pkg.setNsPrefix("dlm");

		bookClass = EcoreFactory.eINSTANCE.createEClass();
		bookClass.setName("Book");
		EAttribute idAttr = EcoreFactory.eINSTANCE.createEAttribute();
		idAttr.setName("id");
		idAttr.setEType(EcorePackage.Literals.ELONG);
		idAttr.setID(true);
		bookClass.getEStructuralFeatures().add(idAttr);
		// "order" is a SQL reserved word — without delimited identifiers,
		// EclipseLink will emit unquoted ORDER in DDL and SELECT, both of which
		// H2 rejects.
		EAttribute orderAttr = EcoreFactory.eINSTANCE.createEAttribute();
		orderAttr.setName("order");
		orderAttr.setEType(EcorePackage.Literals.EINT);
		bookClass.getEStructuralFeatures().add(orderAttr);
		pkg.getEClassifiers().add(bookClass);
	}

	@AfterEach
	void tearDown() {
		if (emf != null && emf.isOpen()) {
			emf.close();
		}
	}

	@Test
	@DisplayName("delimitedIdentifiers=set → reserved-word column names are quoted in DDL and SQL")
	void delimitedIdentifiers_quotesReservedColumnNames() {
		assertThatNoException()
				.as("With delimitedIdentifiers set, DDL and SELECT for a column named 'order' must succeed")
				.isThrownBy(() -> {
					initEclipseLink();

					ClassDescriptor desc = serverSession.getDescriptorForAlias("Book");
					EObject book = (EObject) desc.getInstantiationPolicy().buildNewInstance();
					book.eSet(bookClass.getEStructuralFeature("id"), 1L);
					book.eSet(bookClass.getEStructuralFeature("order"), 42);

					try (EntityManager em = emf.createEntityManager()) {
						em.getTransaction().begin();
						em.persist(book);
						em.getTransaction().commit();
					}

					try (EntityManager em = emf.createEntityManager()) {
						Object found = em.find(desc.getJavaClass(), 1L);
						assertThat(found).isNotNull();
						assertThat(((EObject) found).eGet(bookClass.getEStructuralFeature("order")))
								.isEqualTo(42);
					}
				});
	}

	private void initEclipseLink() {
		EntityMapper mapper = new EntityMapper();
		EntityMappings mappings = mapper.createMappings(List.<EClassifier>of(bookClass));

		// Enable the delimitedIdentifiers flag in persistenceUnitMetadata.
		PersistenceUnitMetadata pum = EORMFactory.eINSTANCE.createPersistenceUnitMetadata();
		PersistenceUnitDefaults pud = EORMFactory.eINSTANCE.createPersistenceUnitDefaults();
		pud.setDelimitedIdentifiers(EORMFactory.eINSTANCE.createEmptyType());
		pum.setPersistenceUnitDefaults(pud);
		mappings.setPersistenceUnitMetadata(pum);

		DynamicClassLoader dcl = new DynamicClassLoader(getClass().getClassLoader());
		Map<String, Object> props = new HashMap<>();
		props.put(PersistenceUnitProperties.CLASSLOADER, dcl);
		props.put(PersistenceUnitProperties.DDL_GENERATION, "create-or-extend-tables");
		props.put(PersistenceUnitProperties.DDL_GENERATION_MODE, "database");
		props.put(PersistenceUnitProperties.JDBC_DRIVER, "org.h2.Driver");
		props.put(PersistenceUnitProperties.JDBC_URL,
				"jdbc:h2:" + tempDir.getAbsolutePath() + "/delimtest;AUTO_SERVER=FALSE");
		props.put(PersistenceUnitProperties.JDBC_USER, "sa");
		props.put(PersistenceUnitProperties.JDBC_PASSWORD, "");
		props.put(PersistenceUnitProperties.LOGGING_LEVEL, "WARNING");
		props.put(PersistenceUnitProperties.WEAVING, "false");
		props.put(PersistenceUnitProperties.TARGET_DATABASE, "Auto");
		props.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");

		PersistenceUnit pu = EPersistenceFactory.eINSTANCE.createPersistenceUnit();
		pu.setName("delim_test");
		pu.setProperties(EPersistenceFactory.eINSTANCE.createProperties());
		URL puRoot = getClass().getProtectionDomain().getCodeSource().getLocation();
		EDynamicPersistenceUnitInfo pui = new EDynamicPersistenceUnitInfo(pu, puRoot, props);

		PersistenceProvider provider = new PersistenceProvider();
		emf = provider.createContainerEntityManagerFactory(pui, props);
		serverSession = JpaHelper.getServerSession(emf);

		ConverterService converter = new DefaultConverterService();
		EDynamicTypeGenerator generator = new EDynamicTypeGenerator(dcl, serverSession, "delim_test", converter);
		List<EDynamicType> types = generator.createFromMappings(List.of(mappings));

		EDynamicHelper helper = new EDynamicHelper(emf, dcl);
		helper.addETypes(true, true, types);
	}
}
