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

import static org.eclipse.fennec.persistence.test.annotations.TestAnnotations.PROP_MODEL_FILE;
import static org.eclipse.fennec.persistence.test.annotations.TestAnnotations.PROP_MODEL_FILE_PATH;
import static org.eclipse.fennec.persistence.test.annotations.TestAnnotations.PROP_MODEL_PATH;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.sql.DataSource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.fennec.persistence.test.annotations.TestAnnotations;
import org.junit.jupiter.api.Test;
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
public abstract class EPersistenceBase extends EPersistenceModelBase {

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.test.EPersistenceModelBase#beforeModelRegistered(org.eclipse.emf.ecore.EPackage)
	 */
	@Override
	void beforeModelRegistered(EPackage modelPackage) {
		Resource resource = createMappingModelResource();

		EntityMapper mapper = new EntityMapper();
		EntityMappings mapping = setupMappings(mapper, modelPackage);
		assertNotNull(mapping);
		resource.getContents().add(mapping);
		assertDoesNotThrow(()->resource.save(null));
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.test.EPersistenceModelBase#afterModelRegistered(org.eclipse.emf.ecore.EPackage)
	 */
	@Override
	void afterModelRegistered(EPackage modelPackage) {
	}

	abstract protected EntityMappings setupMappings(EntityMapper mapper, EPackage ePackage);
	
	@Test
	@TestAnnotations.DefaultEPersistenceSetup
	public void testEMFAvailable(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(cardinality = 0) ServiceAware<EntityManagerFactory> emfAware,
			@InjectConfiguration(withFactoryConfig = @WithFactoryConfiguration(factoryPid = "fennec.jpa.PersistenceUnit", name = "test")) Configuration emfConfig) throws InterruptedException, IOException {
		assertFalse(modelPackageAware.isEmpty());
		assertFalse(dataSourceAware.isEmpty());
		assertTrue(emfAware.isEmpty());
		assertNull(emfConfig.getProperties());
	
		emfConfig.update(Dictionaries.asDictionary(
				Map.of(
						"fennec.jpa.model", "(emf.name=fennec.persistence.model)", 
						"fennec.jpa.mappingFile", modelFile.toURI().toString(), 
						"fennec.jpa.persistenceUnitName", "person")));
	
		assertNotNull(emfAware.waitForService(5000l));
	}

	protected Resource createMappingModelResource() {
		modelFile = new File(modelPath, PROP_MODEL_FILE);
		System.setProperty(PROP_MODEL_FILE_PATH, modelFile.toURI().toString());
		System.setProperty(PROP_MODEL_PATH, modelPath.getAbsolutePath());
		Resource resource = rs.createResource(URI.createURI(modelFile.toURI().toString()));
		assertNotNull(resource);
		return resource;
	}

}
