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
package org.eclipse.fennec.persistence.orm;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.fennec.persistence.eorm.Basic;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code ExtendedMetaData} names become table and column names only when asked to (issue #314).
 * They describe an XML/JSON serialization — {@code marker-color} is a GeoJSON member name and no
 * SQL identifier — so by default the EClass and feature names are taken, and the unused names are
 * reported once per EClass. The JPA attribute name is the feature name either way.
 */
class ExtendedMetaDataNamesTest {

	private EPackage ePackage;
	private EClass feature;
	private EAttribute color;
	private EAttribute id;

	@BeforeEach
	void setUp() {
		ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("emdtest");
		ePackage.setNsPrefix("emdtest");
		ePackage.setNsURI("http://fennec.eclipse.org/emdtest");

		feature = EcoreFactory.eINSTANCE.createEClass();
		feature.setName("CityFeature");
		annotate(feature, "city-feature");
		ePackage.getEClassifiers().add(feature);

		id = EcoreFactory.eINSTANCE.createEAttribute();
		id.setName("id");
		id.setID(true);
		id.setEType(EcorePackage.Literals.ESTRING);
		annotate(id, "feature_id");
		feature.getEStructuralFeatures().add(id);

		color = EcoreFactory.eINSTANCE.createEAttribute();
		color.setName("color");
		color.setEType(EcorePackage.Literals.ESTRING);
		annotate(color, "marker-color");
		feature.getEStructuralFeatures().add(color);
	}

	@Test
	void namesComeFromTheModelByDefault() {
		MappingResult result = new EntityMapper().createMappingsFromEPackageWithDiagnostics(ePackage);

		Entity entity = result.mappings().getEntity().get(0);
		assertThat(entity.getTable().getName()).isEqualTo("CITYFEATURE");
		assertThat(basic(entity, "color").getColumn().getName()).isEqualTo("color");
		assertThat(id(entity).getColumn().getName()).isEqualTo("id");
	}

	@Test
	void unusedNamesAreReportedOncePerEClass() {
		MappingResult result = new EntityMapper().createMappingsFromEPackageWithDiagnostics(ePackage);

		List<Diagnostic> infos = result.diagnostics().stream()
				.filter(d -> d.getSeverity() == Diagnostic.INFO && d.getMessage().contains("ExtendedMetaData"))
				.toList();
		assertThat(infos).hasSize(1);
		assertThat(infos.get(0).getMessage())
				.contains("CityFeature=city-feature", "color=marker-color", "id=feature_id");
		assertThat(infos.get(0).getData()).first().isEqualTo(feature);
		// info only — the run succeeds without warnings of its own
		assertThat(result.getSeverity()).isEqualTo(Diagnostic.INFO);
	}

	@Test
	void unusedNameEscapingAReservedWordIsAWarning() {
		EAttribute user = EcoreFactory.eINSTANCE.createEAttribute();
		user.setName("user");
		user.setEType(EcorePackage.Literals.ESTRING);
		annotate(user, "userName");
		feature.getEStructuralFeatures().add(user);

		MappingResult result = new EntityMapper().createMappingsFromEPackageWithDiagnostics(ePackage);

		assertThat(result.diagnostics())
				.filteredOn(d -> d.getMessage().contains("ExtendedMetaData names of"))
				.singleElement()
				.satisfies(d -> {
					assertThat(d.getSeverity()).isEqualTo(Diagnostic.WARNING);
					assertThat(d.getMessage()).contains("user=userName");
				});
	}

	@Test
	void namesComeFromExtendedMetaDataWhenAskedTo() {
		EntityMapper mapper = new EntityMapper();
		mapper.setUseNamesFromExtendedMetaData(true);
		MappingResult result = mapper.createMappingsFromEPackageWithDiagnostics(ePackage);

		Entity entity = result.mappings().getEntity().get(0);
		assertThat(entity.getTable().getName()).isEqualTo("CITY-FEATURE");
		assertThat(basic(entity, "color").getColumn().getName()).isEqualTo("marker-color");
		assertThat(id(entity).getColumn().getName()).isEqualTo("feature_id");
		assertThat(result.diagnostics()).noneMatch(d -> d.getMessage().contains("ExtendedMetaData"));
	}

	@Test
	void attributeNameIsTheFeatureNameEitherWay() {
		for (boolean useExtendedMetaData : new boolean[] { false, true }) {
			EntityMapper mapper = new EntityMapper();
			mapper.setUseNamesFromExtendedMetaData(useExtendedMetaData);
			Entity entity = mapper.createMappingsFromEPackage(ePackage).getEntity().get(0);

			assertThat(entity.getAttributes().getBasic()).extracting(Basic::getName)
					.contains("color")
					.doesNotContain("marker-color", "feature_id");
			assertThat(id(entity).getName()).isEqualTo("id");
		}
	}

	private static void annotate(EModelElement element, String name) {
		EcoreUtil.setAnnotation(element, ExtendedMetaData.ANNOTATION_URI, "name", name);
	}

	private static Basic basic(Entity entity, String name) {
		return entity.getAttributes().getBasic().stream()
				.filter(b -> name.equals(b.getName()))
				.findFirst()
				.orElseThrow();
	}

	private static Id id(Entity entity) {
		return entity.getAttributes().getId().get(0);
	}
}
