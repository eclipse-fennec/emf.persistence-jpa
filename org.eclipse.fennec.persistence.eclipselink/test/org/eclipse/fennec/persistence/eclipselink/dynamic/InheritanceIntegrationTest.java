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
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
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
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.epersistence.EPersistenceFactory;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Non-OSGi integration test for SINGLE_TABLE inheritance with a real H2 database.
 * Tests the full pipeline: Ecore → MappingProcessor → EDynamicTypeGenerator →
 * EclipseLink session initialization → DDL generation → persist/find.
 *
 * @author Mark Hoffmann
 * @since 16.04.2026
 */
class InheritanceIntegrationTest {

	@TempDir
	File tempDir;

	private EPackage pkg;
	private EClass rootClass;
	private EClass midClass;
	private EClass leafAClass;
	private EClass leafBClass;
	private EntityManagerFactory emf;
	private Server serverSession;

	@BeforeEach
	void setUp() {
		pkg = EcoreFactory.eINSTANCE.createEPackage();
		pkg.setName("inh_test");
		pkg.setNsURI("http://test/inheritance");
		pkg.setNsPrefix("inh");

		rootClass = createEClass("GeoFeature", true);
		EAttribute idAttr = addAttribute(rootClass, "id", EcorePackage.Literals.ELONG);
		idAttr.setID(true);
		addAttribute(rootClass, "name", EcorePackage.Literals.ESTRING);

		midClass = createEClass("AmenityFeature", true);
		midClass.getESuperTypes().add(rootClass);
		addAttribute(midClass, "cuisine", EcorePackage.Literals.ESTRING);

		leafAClass = createEClass("Restaurant", false);
		leafAClass.getESuperTypes().add(midClass);
		addAttribute(leafAClass, "stars", EcorePackage.Literals.EINT);

		leafBClass = createEClass("Bench", false);
		leafBClass.getESuperTypes().add(rootClass);
		addAttribute(leafBClass, "material", EcorePackage.Literals.ESTRING);
	}

	@AfterEach
	void tearDown() {
		if (emf != null && emf.isOpen()) {
			emf.close();
		}
	}

	private void initEclipseLink() {
		EntityMapper mapper = new EntityMapper();
		List<EClassifier> allClasses = new ArrayList<>(List.of(rootClass, midClass, leafAClass, leafBClass));
		EntityMappings mappings = mapper.createMappings(allClasses);

		DynamicClassLoader dcl = new DynamicClassLoader(getClass().getClassLoader());
		Map<String, Object> props = new HashMap<>();
		props.put(PersistenceUnitProperties.CLASSLOADER, dcl);
		props.put(PersistenceUnitProperties.DDL_GENERATION, "create-or-extend-tables");
		props.put(PersistenceUnitProperties.DDL_GENERATION_MODE, "database");
		props.put(PersistenceUnitProperties.JDBC_DRIVER, "org.h2.Driver");
		props.put(PersistenceUnitProperties.JDBC_URL,
				"jdbc:h2:" + tempDir.getAbsolutePath() + "/testdb;AUTO_SERVER=FALSE");
		props.put(PersistenceUnitProperties.JDBC_USER, "sa");
		props.put(PersistenceUnitProperties.JDBC_PASSWORD, "");
		props.put(PersistenceUnitProperties.LOGGING_LEVEL, "WARNING");
		props.put(PersistenceUnitProperties.WEAVING, "false");
		props.put(PersistenceUnitProperties.TARGET_DATABASE, "Auto");
		props.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");

		PersistenceUnit pu = EPersistenceFactory.eINSTANCE.createPersistenceUnit();
		pu.setName("inh_test");
		pu.setProperties(EPersistenceFactory.eINSTANCE.createProperties());
		URL puRoot = getClass().getProtectionDomain().getCodeSource().getLocation();
		EDynamicPersistenceUnitInfo pui = new EDynamicPersistenceUnitInfo(pu, puRoot, props);

		PersistenceProvider provider = new PersistenceProvider();
		emf = provider.createContainerEntityManagerFactory(pui, props);
		serverSession = JpaHelper.getServerSession(emf);

		EDynamicTypeGenerator generator = new EDynamicTypeGenerator(dcl, serverSession, "inh_test");
		List<EDynamicType> types = generator.createFromMapping(mappings);
		EDynamicHelper helper = new EDynamicHelper(emf, dcl);
		helper.addETypes(true, true, types);
	}

	@Test
	@DisplayName("Session initialization succeeds — no sequence number field errors")
	void testSessionInitializationSucceeds() {
		assertThatNoException()
				.as("Full pipeline should not throw for SINGLE_TABLE with 3-level hierarchy")
				.isThrownBy(this::initEclipseLink);
	}

	@Test
	@DisplayName("All entity types are registered as descriptors")
	void testAllDescriptorsRegistered() {
		initEclipseLink();

		assertThat(serverSession.getDescriptorForAlias("GeoFeature")).isNotNull();
		assertThat(serverSession.getDescriptorForAlias("AmenityFeature")).isNotNull();
		assertThat(serverSession.getDescriptorForAlias("Restaurant")).isNotNull();
		assertThat(serverSession.getDescriptorForAlias("Bench")).isNotNull();
	}

	@Test
	@DisplayName("All descriptors have inherited 'id' mapping after initialization")
	void testAllDescriptorsHaveIdMapping() {
		initEclipseLink();

		for (String alias : List.of("GeoFeature", "AmenityFeature", "Restaurant", "Bench")) {
			ClassDescriptor desc = serverSession.getDescriptorForAlias(alias);
			assertThat(desc.getMappingForAttributeName("id"))
					.as("Descriptor %s should have 'id' mapping", alias)
					.isNotNull()
					.isInstanceOf(DirectToFieldMapping.class);
		}
	}

	@Test
	@DisplayName("All descriptors have PK fields")
	void testAllDescriptorsHavePkFields() {
		initEclipseLink();

		for (String alias : List.of("GeoFeature", "AmenityFeature", "Restaurant", "Bench")) {
			ClassDescriptor desc = serverSession.getDescriptorForAlias(alias);
			assertThat(desc.getPrimaryKeyFields())
					.as("Descriptor %s should have PK fields", alias)
					.isNotEmpty();
		}
	}

	@Test
	@DisplayName("Leaf Restaurant has own + inherited attribute mappings")
	void testLeafHasAllMappings() {
		initEclipseLink();

		ClassDescriptor desc = serverSession.getDescriptorForAlias("Restaurant");
		assertThat(desc.getMappingForAttributeName("id")).isNotNull();      // from GeoFeature
		assertThat(desc.getMappingForAttributeName("name")).isNotNull();    // from GeoFeature
		assertThat(desc.getMappingForAttributeName("cuisine")).isNotNull(); // from AmenityFeature
		assertThat(desc.getMappingForAttributeName("stars")).isNotNull();   // own
	}

	@Test
	@DisplayName("Persist and find: Restaurant roundtrip via EntityManager")
	void testPersistAndFind() {
		initEclipseLink();

		ClassDescriptor restaurantDesc = serverSession.getDescriptorForAlias("Restaurant");
		EObject restaurant = (EObject) restaurantDesc.getInstantiationPolicy().buildNewInstance();
		restaurant.eSet(leafAClass.getEStructuralFeature("stars"), 3);
		restaurant.eSet(rootClass.getEStructuralFeature("name"), "Test Restaurant");
		restaurant.eSet(rootClass.getEStructuralFeature("id"), 42L);

		// Persist
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(restaurant);
			em.getTransaction().commit();
		}

		// Find
		try (EntityManager em = emf.createEntityManager()) {
			Object found = em.find(restaurantDesc.getJavaClass(), 42L);
			assertThat(found).isNotNull();
			assertThat(found).isInstanceOf(EObject.class);
			EObject foundEo = (EObject) found;
			assertThat(foundEo.eGet(rootClass.getEStructuralFeature("name"))).isEqualTo("Test Restaurant");
			assertThat(foundEo.eGet(leafAClass.getEStructuralFeature("stars"))).isEqualTo(3);
		}
	}

	@Test
	@DisplayName("Persist and find: Bench (direct child of root) roundtrip")
	void testPersistAndFindDirectChild() {
		initEclipseLink();

		ClassDescriptor benchDesc = serverSession.getDescriptorForAlias("Bench");
		EObject bench = (EObject) benchDesc.getInstantiationPolicy().buildNewInstance();
		bench.eSet(rootClass.getEStructuralFeature("id"), 99L);
		bench.eSet(rootClass.getEStructuralFeature("name"), "Park Bench");
		bench.eSet(leafBClass.getEStructuralFeature("material"), "Wood");

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(bench);
			em.getTransaction().commit();
		}

		try (EntityManager em = emf.createEntityManager()) {
			Object found = em.find(benchDesc.getJavaClass(), 99L);
			assertThat(found).isNotNull();
			EObject foundEo = (EObject) found;
			assertThat(foundEo.eGet(leafBClass.getEStructuralFeature("material"))).isEqualTo("Wood");
		}
	}

	// ── Ecore helpers ────────────────────────────────────────────────────

	private EClass createEClass(String name, boolean isAbstract) {
		EClass ec = EcoreFactory.eINSTANCE.createEClass();
		ec.setName(name);
		ec.setAbstract(isAbstract);
		pkg.getEClassifiers().add(ec);
		return ec;
	}

	private EAttribute addAttribute(EClass owner, String name, EClassifier type) {
		EAttribute attr = EcoreFactory.eINSTANCE.createEAttribute();
		attr.setName(name);
		attr.setEType(type);
		owner.getEStructuralFeatures().add(attr);
		return attr;
	}
}
