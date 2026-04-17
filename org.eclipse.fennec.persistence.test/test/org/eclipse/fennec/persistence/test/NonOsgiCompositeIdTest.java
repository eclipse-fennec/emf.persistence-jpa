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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.eorm.Id;
import org.eclipse.fennec.persistence.orm.CompositeIdAnalyzer;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.fennec.persistence.orm.IdConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Non-OSGi port of {@code CompositeIdTest}. Validates that the {@link EntityMapper}
 * and {@link CompositeIdAnalyzer} correctly handle an EClass with multiple
 * {@code iD="true"} attributes ({@code MultiPKClass}).
 * <p>
 * This test operates purely on the EMF metamodel and the EORM mapping output —
 * no database is involved.
 */
class NonOsgiCompositeIdTest {

	private static final String MULTI_PK_CLASS = "MultiPKClass";

	private EPackage modelPackage;

	@BeforeEach
	void setUp() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		try {
			var resource = rs.createResource(URI.createFileURI(
					new java.io.File("data/model.ecore").getAbsolutePath()));
			resource.load(null);
			modelPackage = (EPackage) resource.getContents().get(0);
		} catch (Exception e) {
			throw new IllegalStateException("Failed to load model.ecore", e);
		}
	}

	@Test
	void testMultiPKClassStructureAndMapping() {
		EClass multiPKClass = (EClass) modelPackage.getEClassifier(MULTI_PK_CLASS);
		assertNotNull(multiPKClass, "MultiPKClass should exist in model");

		List<EAttribute> idAttributes = multiPKClass.getEAllAttributes().stream()
				.filter(EAttribute::isID)
				.toList();
		assertEquals(2, idAttributes.size(), "MultiPKClass should have exactly 2 ID attributes");
		assertThat(idAttributes).extracting(EAttribute::getName).contains("id", "timestamp");

		EntityMapper mapper = new EntityMapper();
		mapper.setStrict(false);
		EntityMappings mappings = mapper.createMappingsFromEClass(multiPKClass);
		assertNotNull(mappings);
		assertFalse(mappings.getEntity().isEmpty(), "Should have at least one entity");

		Entity entity = mappings.getEntity().get(0);
		assertEquals(MULTI_PK_CLASS, entity.getName());

		List<Id> ids = entity.getAttributes().getId();
		assertEquals(2, ids.size(), "Should generate 2 ID mappings for composite key");
		assertThat(ids).extracting(Id::getName).contains("id", "timestamp");
	}

	@Test
	void testCompositeIdAnalyzerReportsEmbeddedIdStrategy() {
		EClass multiPKClass = (EClass) modelPackage.getEClassifier(MULTI_PK_CLASS);
		assertNotNull(multiPKClass);

		CompositeIdAnalyzer analyzer = new CompositeIdAnalyzer();
		IdConfiguration config = analyzer.analyzeIdStructure(multiPKClass);
		assertEquals(IdConfiguration.IdStrategy.EMBEDDED_ID, config.getStrategy(),
				"MultiPKClass should use EMBEDDED_ID strategy");
		assertEquals(2, config.getIdAttributes().size(), "Should have 2 ID attributes");
	}
}
