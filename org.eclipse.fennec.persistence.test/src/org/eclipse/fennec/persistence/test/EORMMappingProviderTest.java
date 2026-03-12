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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Objects;

import javax.sql.DataSource;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.eorm.util.EORMResourceFactoryImpl;
import org.eclipse.fennec.persistence.epersistence.EPersistencePackage;
import org.eclipse.fennec.persistence.orm.EORMMappingCustomizer;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.fennec.persistence.orm.ORMConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.Property.Type;
import org.osgi.test.common.annotation.Property.ValueSource;
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
public class EORMMappingProviderTest extends EPersistenceBase {
	
	@BeforeEach
	public void before(@InjectBundleContext BundleContext ctx) {
		// initialize ResourceSet
		if (Objects.isNull(rs)) {
			rs = initResourceSet();
		}
		// load local ecore model
		EPackage modelPackage = loadModelPackage(ctx);

		registerEPackageService(ctx, modelPackage);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.test.EPersistenceBase#setupMappings(org.eclipse.fennec.persistence.orm.EntityMapper, org.eclipse.emf.ecore.EPackage)
	 */
	@Override
	protected EntityMappings setupMappings(EntityMapper mapper, EPackage ePackage) {
		return null;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.test.EPersistenceBase#testEMFAvailable(org.osgi.test.common.service.ServiceAware, org.osgi.test.common.service.ServiceAware, org.osgi.test.common.service.ServiceAware, org.osgi.service.cm.Configuration)
	 */
	@Disabled
	@Override
	public void testEMFAvailable(ServiceAware<DataSource> dataSourceAware, ServiceAware<EPackage> modelPackageAware,
			ServiceAware<EntityManagerFactory> emfAware, Configuration emfConfig)
			throws InterruptedException, IOException {
		super.testEMFAvailable(dataSourceAware, modelPackageAware, emfAware, emfConfig);
	}
	
	@WithFactoryConfiguration(factoryPid = ORMConstants.ORM_MAPPING_SERVICE_PID, name = "test", location = "?", properties = {
			@Property(key = ORMConstants.PROPERTY_PREFIX + "eClasses", type = Type.Array, source = ValueSource.Value, value = {"ClassAM2M", "ClassBM2M", "ClassCM2M"}),
			@Property(key = ORMConstants.PROPERTY_PREFIX + "mappingName", value = "testModel"),
			@Property(key = ORMConstants.PROPERTY_PREFIX + "model.target", value = "(emf.name=fennec.persistence.model)"),
	})
	@Test
	public void simpleEORMConfigurationTest(@InjectService(timeout = 1000, filter = "(fennec.jpa.eorm.mapping=testModel)") ServiceAware<EntityMappings> mappingAware)
					throws Exception {
		
		assertFalse(mappingAware.isEmpty());
		EntityMappings mapping = mappingAware.getService();
		assertEquals("fennec.persistence.model", mapping.getName());
		assertEquals(3, mapping.getEntity().size());
		List<String> entities = new ArrayList<>(List.of("ClassAM2M", "ClassBM2M", "ClassCM2M"));
		mapping.getEntity().forEach(e->entities.remove(e.getName()));
		assertTrue(entities.isEmpty());
		
	}
	
	@WithFactoryConfiguration(factoryPid = ORMConstants.ORM_MAPPING_SERVICE_PID, name = "test", location = "?", properties = {
			@Property(key = ORMConstants.PROPERTY_PREFIX + "eClasses", type = Type.Array, source = ValueSource.Value, value = {"ClassAM2M", "ClassBM2M", "ClassCM2M"}),
			@Property(key = ORMConstants.PROPERTY_PREFIX + "mappingName", value = "testModel"),
			@Property(key = ORMConstants.PROPERTY_PREFIX + "customizer.target", value = "(name=test)"),
			@Property(key = ORMConstants.PROPERTY_PREFIX + "model.target", value = "(emf.name=fennec.persistence.model)"),
	})
	@Test
	public void simpleEORMConfigurationCustomizerTest(@InjectService(cardinality = 0, filter = "(fennec.jpa.eorm.mapping=testModel)") ServiceAware<EntityMappings> mappingAware, @InjectBundleContext BundleContext ctx)
			throws Exception {
		
		assertTrue(mappingAware.isEmpty());
		
		Dictionary<String, Object> props = Dictionaries.dictionaryOf("name", "test");
		ServiceRegistration<EORMMappingCustomizer> registration = ctx.registerService(EORMMappingCustomizer.class, new MyCustomizer(), props);
		
		EntityMappings mapping = mappingAware.waitForService(5000l);
		
		assertEquals("fennec.persistence.model", mapping.getName());
		assertEquals(2, mapping.getEntity().size());
		List<String> entities = new ArrayList<>(List.of("ClassAM2M", "ClassBM2M"));
		mapping.getEntity().forEach(e->entities.remove(e.getName()));
		assertTrue(entities.isEmpty());
		registration.unregister();
		
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
	
	private static class MyCustomizer implements EORMMappingCustomizer {
		/* 
		 * (non-Javadoc)
		 * @see org.eclipse.fennec.persistence.orm.EORMMappingCustomizer#customizeMapping(org.eclipse.fennec.persistence.eorm.EntityMappings)
		 */
		@Override
		public EntityMappings customizeMapping(EntityMappings mapping) {
			mapping.getEntity().remove(2);
			return mapping;
		}
	}
}
