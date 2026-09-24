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

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.Keywords;
import org.eclipse.fennec.persistence.eorm.Basic;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The persistence annotation of an attribute carries its column facets into the eorm
 * (issue #319): {@code length}, {@code columnDefinition} and {@code lob}.
 */
class AnnotatedColumnFacetsTest {

	private EPackage ePackage;
	private EClass asset;

	@BeforeEach
	void setUp() {
		ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("facettest");
		ePackage.setNsPrefix("facettest");
		ePackage.setNsURI("http://fennec.eclipse.org/facettest");
		asset = EcoreFactory.eINSTANCE.createEClass();
		asset.setName("Asset");
		ePackage.getEClassifiers().add(asset);
		attribute("id").setID(true);
	}

	@Test
	void lengthColumnDefinitionAndLobReachTheEorm() {
		annotate(attribute("code"), "length", "10");
		annotate(attribute("summary"), "columnDefinition", "VARCHAR(2000)");
		annotate(attribute("geometry"), "lob", "true");

		MappingResult result = new EntityMapper().createMappingsFromEPackageWithDiagnostics(ePackage);

		Entity entity = result.mappings().getEntity().get(0);
		assertThat(basic(entity, "code").getColumn().getLength()).isEqualTo(10);
		assertThat(basic(entity, "code").getColumn().isSetLength()).isTrue();
		assertThat(basic(entity, "summary").getColumn().getColumnDefinition()).isEqualTo("VARCHAR(2000)");
		assertThat(basic(entity, "geometry").getLob()).isNotNull();
		assertThat(result.getSeverity()).isLessThan(Diagnostic.WARNING);
	}

	@Test
	void withoutAnnotationNothingIsSet() {
		attribute("plain");

		Entity entity = new EntityMapper().createMappingsFromEPackage(ePackage).getEntity().get(0);

		assertThat(basic(entity, "plain").getColumn().isSetLength()).isFalse();
		assertThat(basic(entity, "plain").getColumn().getColumnDefinition()).isNull();
		assertThat(basic(entity, "plain").getLob()).isNull();
	}

	@Test
	void lobFalseSetsNoLob() {
		annotate(attribute("geometry"), "lob", "false");

		MappingResult result = new EntityMapper().createMappingsFromEPackageWithDiagnostics(ePackage);

		assertThat(basic(result.mappings().getEntity().get(0), "geometry").getLob()).isNull();
		assertThat(result.getSeverity()).isLessThan(Diagnostic.WARNING);
	}

	@Test
	void unusableValuesAreWarningsAndLeftOut() {
		annotate(attribute("code"), "length", "long");
		annotate(attribute("size"), "length", "0");
		annotate(attribute("geometry"), "lob", "yes");

		MappingResult result = new EntityMapper().createMappingsFromEPackageWithDiagnostics(ePackage);

		Entity entity = result.mappings().getEntity().get(0);
		assertThat(basic(entity, "code").getColumn().isSetLength()).isFalse();
		assertThat(basic(entity, "size").getColumn().isSetLength()).isFalse();
		assertThat(basic(entity, "geometry").getLob()).isNull();
		assertThat(result.diagnostics())
				.filteredOn(d -> d.getSeverity() == Diagnostic.WARNING)
				.extracting(Diagnostic::getMessage)
				.anySatisfy(m -> assertThat(m).contains("'length' of 'code'", "'long'"))
				.anySatisfy(m -> assertThat(m).contains("'length' of 'size'", "'0'"))
				.anySatisfy(m -> assertThat(m).contains("'lob' of 'geometry'", "'yes'"));
	}

	private EAttribute attribute(String name) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(EcorePackage.Literals.ESTRING);
		asset.getEStructuralFeatures().add(attribute);
		return attribute;
	}

	private static void annotate(EAttribute attribute, String key, String value) {
		EcoreUtil.setAnnotation(attribute, Keywords.PERSISTENCE_ANNOTATION_SOURCE, key, value);
	}

	private static Basic basic(Entity entity, String name) {
		return entity.getAttributes().getBasic().stream()
				.filter(b -> name.equals(b.getName()))
				.findFirst()
				.orElseThrow();
	}
}
