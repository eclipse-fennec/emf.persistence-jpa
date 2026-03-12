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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.sql.DataSource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.emf.osgi.example.model.basic.BasicPackage;
import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.eorm.util.EORMResourceFactoryImpl;
import org.eclipse.fennec.persistence.epersistence.EPersistencePackage;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.fennec.persistence.test.annotations.TestAnnotations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.osgi.service.cm.Configuration;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.config.InjectConfiguration;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.dictionary.Dictionaries;
import org.osgi.test.common.service.ServiceAware;

import jakarta.persistence.EntityManagerFactory;

/**
 * See documentation here: 
 * 	https://github.com/osgi/osgi-test
 * 	https://github.com/osgi/osgi-test/wiki
 * Examples: https://github.com/osgi/osgi-test/tree/main/examples
 */
public class EORMLoaderTest {
	
	@TempDir
	private File modelPath;
	private File modelFile;
	
	@BeforeEach
	public void before() {
		EntityMapper mapper = new EntityMapper();
		EntityMappings mapping = mapper.createMappingsFromEClass(BasicPackage.Literals.PERSON);
		modelFile = new File(modelPath, "person.eorm");
		ResourceSet rs = initResourceSet();
		String fileUri = modelFile.toURI().toString();
		System.setProperty(TestAnnotations.PROP_MODEL_PATH, modelPath.toURI().toString());
		System.setProperty(TestAnnotations.PROP_MODEL_FILE_PATH, fileUri);
		Resource resource = rs.createResource(URI.createURI(fileUri));
		assertNotNull(resource);
		resource.getContents().add(mapping);
		try {
			resource.save(null);
		} catch (IOException e) {
			fail("Failed save mapping in temp file", e);
		}
	}
	
	@Test
	public void manualEORMConfigurationTest(@InjectService(cardinality = 0, filter = "(fennec.jpa.orm.mapping.name=person)") ServiceAware<EntityMappings> mappingAware, 
			@InjectConfiguration(withFactoryConfig = @WithFactoryConfiguration(factoryPid = "fennec.jpa.EORMLoader", name = "test")) Configuration emfConfig)
					throws Exception {
		
		assertTrue(mappingAware.isEmpty());
		
		emfConfig.update(Dictionaries.asDictionary(
				Map.of(
						"fennec.jpa.eorm.model.target", "(emf.name=basic)", 
						"fennec.jpa.eorm.name", "person", 
						"fennec.jpa.eorm.uri", modelFile.toURI().toString())));
		assertNotNull(mappingAware.waitForService(1000l));
		EntityMappings mapping = mappingAware.getService();
		assertEquals("basic", mapping.getName());
		assertEquals(1, mapping.getEntity().size());
		assertEquals("Person", mapping.getEntity().get(0).getName());
	}
	
	@Test
	@TestAnnotations.EORMLoaderConfiguration
	public void annotationEORMConfigurationTest(@InjectService(filter = "(fennec.jpa.orm.mapping.name=person)") ServiceAware<EntityMappings> mappingAware)
					throws Exception {
		
		assertFalse(mappingAware.isEmpty());
		EntityMappings mapping = mappingAware.getService();
		assertEquals("basic", mapping.getName());
		assertEquals(1, mapping.getEntity().size());
		assertEquals("Person", mapping.getEntity().get(0).getName());
	}
	
	@Test
	@TestAnnotations.EORMLoaderConfiguration
	@TestAnnotations.DefaultEPersistenceSetup
	public void testEORMPersistenceAvailable(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(cardinality = 0) ServiceAware<EntityManagerFactory> emfAware,
			@InjectConfiguration(withFactoryConfig = @WithFactoryConfiguration(factoryPid = "fennec.jpa.EMPersistenceUnit", name = "test")) Configuration emfConfig) throws InterruptedException, IOException {
		assertFalse(dataSourceAware.isEmpty());
		assertTrue(emfAware.isEmpty());
		assertNull(emfConfig.getProperties());
	
		emfConfig.update(Dictionaries.asDictionary(
				Map.of(
						"fennec.jpa.mapping.target", "(fennec.jpa.orm.mapping.name=person)", 
						"fennec.jpa.persistenceUnitName", "person")));
	
		assertNotNull(emfAware.waitForService(5000l));
	}
	
	@Test
	@TestAnnotations.EORMLoaderEPersistenceConfiguration
	public void testEORMPersistenceAnnotationAvailable(@InjectService(timeout = 5000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		assertFalse(emfAware.isEmpty());
	}

	protected ResourceSet initResourceSet() {
	
		ResourceSet rs = new ResourceSetImpl();
		rs.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		rs.getPackageRegistry().put(EORMPackage.eNS_URI, EORMPackage.eINSTANCE);
		rs.getPackageRegistry().put(EPersistencePackage.eNS_URI, EPersistencePackage.eINSTANCE);
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("eorm", new EORMResourceFactoryImpl());
		return rs;
	}
}
