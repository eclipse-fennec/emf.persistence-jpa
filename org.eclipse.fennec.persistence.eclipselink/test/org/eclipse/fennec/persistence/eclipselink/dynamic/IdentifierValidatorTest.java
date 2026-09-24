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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Table and column names that are no SQL identifier are reported as ERROR diagnostics before any
 * DDL runs, naming entity and attribute (issue #314) — instead of a {@code CREATE TABLE} that
 * {@code create-or-extend-tables} swallows, and a missing table at the first query.
 */
class IdentifierValidatorTest {

	private EntityManagerFactory emf;
	private Server serverSession;
	private DynamicClassLoader dcl;
	private EPackage ePackage;
	private EClass cityFeature;

	@BeforeEach
	void setUp() {
		ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("idtest");
		ePackage.setNsURI("http://test/idtest/" + UUID.randomUUID());
		ePackage.setNsPrefix("idtest");
		cityFeature = EcoreFactory.eINSTANCE.createEClass();
		cityFeature.setName("CityFeature");
		ePackage.getEClassifiers().add(cityFeature);
		EAttribute id = EcoreFactory.eINSTANCE.createEAttribute();
		id.setName("id");
		id.setEType(EcorePackage.Literals.ELONG);
		id.setID(true);
		cityFeature.getEStructuralFeatures().add(id);
		EAttribute color = EcoreFactory.eINSTANCE.createEAttribute();
		color.setName("color");
		color.setEType(EcorePackage.Literals.ESTRING);
		annotate(color, "marker-color");
		cityFeature.getEStructuralFeatures().add(color);
	}

	@AfterEach
	void tearDown() {
		if (emf != null && emf.isOpen()) {
			emf.close();
		}
	}

	@ParameterizedTest
	@ValueSource(strings = { "color", "_x", "marker_color", "A1", "price$", "straße" })
	void regularIdentifiers(String name) {
		assertThat(IdentifierValidator.isRegularIdentifier(name)).isTrue();
	}

	@ParameterizedTest
	@ValueSource(strings = { "marker-color", "1st", "has space", "a.b", "-x" })
	void noIdentifiers(String name) {
		assertThat(IdentifierValidator.isRegularIdentifier(name)).isFalse();
	}

	@Test
	void defaultMappingProducesNoError() {
		List<Diagnostic> diagnostics = generate(new EntityMapper().createMappings(List.of(cityFeature)));

		assertThat(errors(diagnostics)).isEmpty();
	}

	@Test
	void columnNameThatIsNoIdentifierIsAnErrorNamingTheAttribute() {
		List<Diagnostic> diagnostics = generate(extendedMetaDataMapping());

		assertThat(errors(diagnostics)).singleElement().satisfies(error -> {
			assertThat(error.getSource()).isEqualTo(EDynamicTypeContext.DIAGNOSTIC_SOURCE);
			assertThat(error.getMessage()).contains("'marker-color'", "attribute 'color'", "entity 'CityFeature'");
			assertThat(error.getData()).first().isEqualTo(cityFeature);
		});
	}

	@Test
	void tableNameThatIsNoIdentifierIsAnError() {
		annotate(cityFeature, "city-feature");

		List<Diagnostic> diagnostics = generate(extendedMetaDataMapping());

		assertThat(errors(diagnostics)).extracting(Diagnostic::getMessage)
				.anySatisfy(m -> assertThat(m).contains("Table name 'CITY-FEATURE'", "entity 'CityFeature'"));
	}

	@Test
	void delimitedIdentifiersAcceptTheNameAndRoundTrip() {
		EntityMappings mappings = extendedMetaDataMapping();
		PersistenceUnitMetadata metadata = EORMFactory.eINSTANCE.createPersistenceUnitMetadata();
		PersistenceUnitDefaults defaults = EORMFactory.eINSTANCE.createPersistenceUnitDefaults();
		defaults.setDelimitedIdentifiers(EORMFactory.eINSTANCE.createEmptyType());
		metadata.setPersistenceUnitDefaults(defaults);
		mappings.setPersistenceUnitMetadata(metadata);

		EDynamicTypeGenerator generator = generator();
		List<EDynamicType> types = generator.createFromMappings(List.of(mappings));
		assertThat(errors(generator.getDiagnostics())).isEmpty();
		new EDynamicHelper(emf, dcl).addETypes(true, true, types);

		ClassDescriptor descriptor = serverSession.getDescriptorForAlias("CityFeature");
		EObject object = (EObject) descriptor.getInstantiationPolicy().buildNewInstance();
		object.eSet(cityFeature.getEStructuralFeature("id"), 1L);
		object.eSet(cityFeature.getEStructuralFeature("color"), "#ff0000");
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(object);
			em.getTransaction().commit();
		}
		emf.getCache().evictAll();
		try (EntityManager em = emf.createEntityManager()) {
			EObject found = (EObject) em.find(descriptor.getJavaClass(), 1L);
			assertThat(found.eGet(cityFeature.getEStructuralFeature("color"))).isEqualTo("#ff0000");
		}
	}

	// ── Helpers ─────────────────────────────────────────────────────────────

	private static void annotate(EModelElement element, String name) {
		EcoreUtil.setAnnotation(element, ExtendedMetaData.ANNOTATION_URI, "name", name);
	}

	private EntityMappings extendedMetaDataMapping() {
		EntityMapper mapper = new EntityMapper();
		mapper.setUseNamesFromExtendedMetaData(true);
		return mapper.createMappings(List.of(cityFeature));
	}

	private static List<Diagnostic> errors(List<Diagnostic> diagnostics) {
		return diagnostics.stream().filter(d -> d.getSeverity() >= Diagnostic.ERROR).toList();
	}

	private List<Diagnostic> generate(EntityMappings mappings) {
		EDynamicTypeGenerator generator = generator();
		generator.createFromMappings(List.of(mappings));
		return generator.getDiagnostics();
	}

	private EDynamicTypeGenerator generator() {
		dcl = new DynamicClassLoader(getClass().getClassLoader());
		Map<String, Object> props = new HashMap<>();
		props.put(PersistenceUnitProperties.CLASSLOADER, dcl);
		props.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.NONE);
		props.put(PersistenceUnitProperties.JDBC_DRIVER, "org.h2.Driver");
		props.put(PersistenceUnitProperties.JDBC_URL, "jdbc:h2:mem:idtest_" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
		props.put(PersistenceUnitProperties.JDBC_USER, "sa");
		props.put(PersistenceUnitProperties.JDBC_PASSWORD, "");
		props.put(PersistenceUnitProperties.LOGGING_LEVEL, "WARNING");
		props.put(PersistenceUnitProperties.WEAVING, "false");
		props.put(PersistenceUnitProperties.TARGET_DATABASE, "Auto");
		props.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");

		PersistenceUnit pu = EPersistenceFactory.eINSTANCE.createPersistenceUnit();
		pu.setName("idtest");
		pu.setProperties(EPersistenceFactory.eINSTANCE.createProperties());
		URL puRoot = getClass().getProtectionDomain().getCodeSource().getLocation();
		EDynamicPersistenceUnitInfo pui = new EDynamicPersistenceUnitInfo(pu, puRoot, props);

		emf = new PersistenceProvider().createContainerEntityManagerFactory(pui, props);
		serverSession = JpaHelper.getServerSession(emf);
		return new EDynamicTypeGenerator(dcl, serverSession, "idtest");
	}
}
