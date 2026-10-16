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

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.eorm.Index;
import org.eclipse.fennec.persistence.eorm.JoinColumn;
import org.eclipse.fennec.persistence.eorm.OneToOne;
import org.eclipse.fennec.persistence.eorm.TableGenerator;
import org.eclipse.fennec.persistence.epersistence.EPersistenceFactory;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManagerFactory;

/**
 * What the provider does not read of an eorm mapping is reported, not ignored (issue #297):
 * an embedded id or an id class changes what the key is and is an ERROR, an index or a table
 * generator leaves the stored values alone and is a WARNING. Join columns that do not cover
 * the key they refer to would store a partial key, so they are an ERROR too.
 */
class MappingDiagnosticsTest {

	private static final String ID_FEATURES = "http://eclipse.org/fennec/persistence/1.0";

	private EClass stationClass;
	private EClass lotClass;
	private EClass visitClass;
	private EntityManagerFactory emf;

	@BeforeEach
	void setUp() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		stationClass = ecore.createEClass();
		stationClass.setName("Station");
		attribute(stationClass, "id", true);
		attribute(stationClass, "name", false);

		lotClass = ecore.createEClass();
		lotClass.setName("Lot");
		attribute(lotClass, "plant", false);
		attribute(lotClass, "lotNo", false);
		attribute(lotClass, "grade", false);
		EcoreUtil.setAnnotation(lotClass, ID_FEATURES, "idFeatures", "plant,lotNo");

		visitClass = ecore.createEClass();
		visitClass.setName("Visit");
		attribute(visitClass, "id", true);
		EReference lot = ecore.createEReference();
		lot.setName("lot");
		lot.setEType(lotClass);
		visitClass.getEStructuralFeatures().add(lot);

		EPackage ePackage = ecore.createEPackage();
		ePackage.setName("diag");
		ePackage.setNsURI("urn:diag:" + UUID.randomUUID());
		ePackage.setNsPrefix("diag");
		ePackage.getEClassifiers().addAll(List.of(stationClass, lotClass, visitClass));
	}

	@AfterEach
	void tearDown() {
		if (nonNull(emf)) {
			emf.close();
		}
	}

	@Test
	void aGeneratedMappingReportsNothing() {
		List<Diagnostic> diagnostics = generate(mappings());

		assertThat(diagnostics).filteredOn(d -> d.getSeverity() >= Diagnostic.WARNING).isEmpty();
	}

	@Test
	void anEmbeddedIdIsAnError() {
		EntityMappings mappings = mappings();
		entity(mappings, "Station").getAttributes().setEmbeddedId(EORMFactory.eINSTANCE.createEmbeddedId());

		assertThat(errors(generate(mappings))).singleElement().satisfies(error -> {
			assertThat(error.getMessage()).contains("Entity 'Station'", "embedded id");
			assertThat(error.getData()).first().isEqualTo(stationClass);
		});
	}

	@Test
	void anIdClassIsAnError() {
		EntityMappings mappings = mappings();
		entity(mappings, "Station").setIdClass(EORMFactory.eINSTANCE.createIdClass());

		assertThat(errors(generate(mappings))).singleElement()
				.satisfies(error -> assertThat(error.getMessage()).contains("Entity 'Station'", "id class"));
	}

	@Test
	void anIndexIsAWarning() {
		EntityMappings mappings = mappings();
		Index index = EORMFactory.eINSTANCE.createIndex();
		index.setName("IDX_NAME");
		index.setColumnList("name");
		entity(mappings, "Station").getTable().getIndex().add(index);

		List<Diagnostic> diagnostics = generate(mappings);

		assertThat(errors(diagnostics)).isEmpty();
		assertThat(warnings(diagnostics)).singleElement()
				.satisfies(warning -> assertThat(warning.getMessage()).contains("Entity 'Station'", "indexes"));
	}

	@Test
	void aTableGeneratorIsAWarningWhereverItIsDeclared() {
		EntityMappings mappings = mappings();
		mappings.getTableGenerator().add(tableGenerator("GLOBAL_GEN"));
		entity(mappings, "Station").setTableGenerator(tableGenerator("STATION_GEN"));
		entity(mappings, "Visit").getAttributes().getId().get(0).setTableGenerator(tableGenerator("VISIT_GEN"));

		List<Diagnostic> diagnostics = generate(mappings);

		assertThat(errors(diagnostics)).isEmpty();
		assertThat(warnings(diagnostics)).extracting(Diagnostic::getMessage)
				.anySatisfy(m -> assertThat(m).contains("GLOBAL_GEN"))
				.anySatisfy(m -> assertThat(m).contains("Entity 'Station'", "table generator"))
				.anySatisfy(m -> assertThat(m).contains("Id 'id' of entity 'Visit'", "table generator"));
	}

	/** One join column for a two-column key would store half the key. */
	@Test
	void joinColumnsCoveringPartOfACompositeKeyAreAnError() {
		EntityMappings mappings = mappings();
		List<JoinColumn> joinColumns = oneToOne(mappings, "Visit", "lot").getJoinColumn();
		assertThat(joinColumns).hasSize(2);
		joinColumns.remove(1);

		assertThat(errors(generate(mappings))).singleElement().satisfies(error -> {
			assertThat(error.getMessage()).contains("Reference 'lot' of 'Visit'", "1 join column(s)",
					"key of 2 column(s)");
			assertThat(error.getData()).first().isEqualTo(visitClass.getEStructuralFeature("lot"));
		});
	}

	@Test
	void aJoinColumnReferringToNoKeyColumnIsAnError() {
		EntityMappings mappings = mappings();
		oneToOne(mappings, "Visit", "lot").getJoinColumn().get(1).setReferencedColumnName("LOT.grade");

		assertThat(errors(generate(mappings))).singleElement()
				.satisfies(error -> assertThat(error.getMessage()).contains("refers to 'LOT.grade'",
						"no key column"));
	}

	// ── Helpers ─────────────────────────────────────────────────────────────

	private EntityMappings mappings() {
		return new EntityMapper().createMappings(List.of(stationClass, lotClass, visitClass));
	}

	private static Entity entity(EntityMappings mappings, String name) {
		return mappings.getEntity().stream().filter(e -> name.equals(e.getName())).findFirst().orElseThrow();
	}

	private static OneToOne oneToOne(EntityMappings mappings, String entity, String name) {
		return entity(mappings, entity).getAttributes().getOneToOne().stream()
				.filter(m -> name.equals(m.getName())).findFirst().orElseThrow();
	}

	private static TableGenerator tableGenerator(String name) {
		TableGenerator generator = EORMFactory.eINSTANCE.createTableGenerator();
		generator.setName(name);
		return generator;
	}

	private static void attribute(EClass eClass, String name, boolean id) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(EcorePackage.Literals.ESTRING);
		attribute.setID(id);
		eClass.getEStructuralFeatures().add(attribute);
	}

	private static List<Diagnostic> errors(List<Diagnostic> diagnostics) {
		return diagnostics.stream().filter(d -> d.getSeverity() >= Diagnostic.ERROR).toList();
	}

	private static List<Diagnostic> warnings(List<Diagnostic> diagnostics) {
		return diagnostics.stream().filter(d -> d.getSeverity() == Diagnostic.WARNING).toList();
	}

	private List<Diagnostic> generate(EntityMappings mappings) {
		DynamicClassLoader dcl = new DynamicClassLoader(getClass().getClassLoader());
		Map<String, Object> props = new HashMap<>();
		props.put(PersistenceUnitProperties.CLASSLOADER, dcl);
		props.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.NONE);
		props.put(PersistenceUnitProperties.JDBC_DRIVER, "org.h2.Driver");
		props.put(PersistenceUnitProperties.JDBC_URL, "jdbc:h2:mem:diag_" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
		props.put(PersistenceUnitProperties.JDBC_USER, "sa");
		props.put(PersistenceUnitProperties.JDBC_PASSWORD, "");
		props.put(PersistenceUnitProperties.LOGGING_LEVEL, "WARNING");
		props.put(PersistenceUnitProperties.WEAVING, "false");
		props.put(PersistenceUnitProperties.TARGET_DATABASE, "Auto");
		props.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");

		PersistenceUnit pu = EPersistenceFactory.eINSTANCE.createPersistenceUnit();
		pu.setName("diag");
		pu.setProperties(EPersistenceFactory.eINSTANCE.createProperties());
		URL puRoot = getClass().getProtectionDomain().getCodeSource().getLocation();
		EDynamicPersistenceUnitInfo pui = new EDynamicPersistenceUnitInfo(pu, puRoot, props);
		emf = new PersistenceProvider().createContainerEntityManagerFactory(pui, props);

		EDynamicTypeGenerator generator = new EDynamicTypeGenerator(dcl, JpaHelper.getServerSession(emf), "diag");
		generator.createFromMappings(List.of(mappings));
		return generator.getDiagnostics();
	}
}
