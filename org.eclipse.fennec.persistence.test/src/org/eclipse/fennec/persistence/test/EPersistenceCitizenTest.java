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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.fennec.persistence.test.annotations.TestAnnotations;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.osgi.service.cm.Configuration;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.config.InjectConfiguration;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.dictionary.Dictionaries;
import org.osgi.test.common.service.ServiceAware;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * See documentation here: 
 * 	https://github.com/osgi/osgi-test
 * 	https://github.com/osgi/osgi-test/wiki
 * Examples: https://github.com/osgi/osgi-test/tree/main/examples
 */
public class EPersistenceCitizenTest extends EPersistenceBase{

	private EClass ageGroupsEClass;
	private EClass einwohnerEClass;
	private EClass genderEClass;
	private EClass statbezEClass;
	private EClass plraumEClass;
	private EClass townEClass;
	private EClass yearEClass;
	private EStructuralFeature statBezNameFeature;

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.test.EPersistenceBase#getModelEntryPath()
	 */
	@Override
	protected String getModelEntryPath() {
		return "/data/citizen_geojson.ecore";
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.test.EPersistenceBase#setupMappings(org.eclipse.fennec.persistence.orm.helper.EntityMapper, org.eclipse.emf.ecore.EPackage)
	 */
	@Override
	protected EntityMappings setupMappings(EntityMapper mapper, EPackage ePackage) {
		ageGroupsEClass = (EClass) ePackage.getEClassifier("AgeGroups");
		assertNotNull(ageGroupsEClass);
		einwohnerEClass = (EClass) ePackage.getEClassifier("einwohner");
		assertNotNull(einwohnerEClass);
		genderEClass = (EClass) ePackage.getEClassifier("gender");
		assertNotNull(genderEClass);
		statbezEClass = (EClass) ePackage.getEClassifier("statbez");
		assertNotNull(statbezEClass);
		plraumEClass = (EClass) ePackage.getEClassifier("plraum");
		assertNotNull(plraumEClass);
		townEClass = (EClass) ePackage.getEClassifier("Town");
		assertNotNull(townEClass);
		yearEClass = (EClass) ePackage.getEClassifier("Year");
		assertNotNull(yearEClass);
		mapper.setStrict(true);
		EntityMappings mapping = mapper.createMappings(List.of(statbezEClass, townEClass, yearEClass, ageGroupsEClass, genderEClass, plraumEClass, einwohnerEClass));
		return mapping;
	}
	
    @Test
    @TestAnnotations.CitizenEPersistenceSetup
    void serviceWithConfigurationTest(@InjectService(timeout = 500) ServiceAware<DataSource> serviceAwareDataSource, //
            @InjectService(timeout = 500) ServiceAware<XADataSource> serviceAwareXaDataSource, //
            @InjectService(timeout = 500) ServiceAware<ConnectionPoolDataSource> serviceAwareCpDataSource)
            throws Exception {

        assertThat(serviceAwareDataSource.getServices()).hasSize(1);
        assertThat(serviceAwareXaDataSource.getServices()).hasSize(1);
        assertThat(serviceAwareCpDataSource.getServices()).hasSize(1);
    }

	@Test
	@TestAnnotations.CitizenEPersistenceConfiguration
	public void testCitizenDescriptorsRegistered(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=citizen)") ServiceAware<EPackage> citizenPackageAware,
			@InjectService(timeout = 7000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		assertFalse(citizenPackageAware.isEmpty());
		assertFalse(dataSourceAware.isEmpty());
		assertFalse(emfAware.isEmpty());

		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);

		// Verify all 7 entity descriptors are registered
		ClassDescriptor ageGroupsDescriptor = server.getDescriptorForAlias(ageGroupsEClass.getName());
		assertNotNull(ageGroupsDescriptor, "AgeGroups descriptor missing");
		ClassDescriptor einwohnerDescriptor = server.getDescriptorForAlias(einwohnerEClass.getName());
		assertNotNull(einwohnerDescriptor, "einwohner descriptor missing");
		ClassDescriptor plraumDescriptor = server.getDescriptorForAlias(plraumEClass.getName());
		assertNotNull(plraumDescriptor, "plraum descriptor missing");
		ClassDescriptor genderDescriptor = server.getDescriptorForAlias(genderEClass.getName());
		assertNotNull(genderDescriptor, "gender descriptor missing");
		ClassDescriptor yearDescriptor = server.getDescriptorForAlias(yearEClass.getName());
		assertNotNull(yearDescriptor, "Year descriptor missing");
		ClassDescriptor statBezDescriptor = server.getDescriptorForAlias(statbezEClass.getName());
		assertNotNull(statBezDescriptor, "statbez descriptor missing");
		ClassDescriptor townDescriptor = server.getDescriptorForAlias(townEClass.getName());
		assertNotNull(townDescriptor, "Town descriptor missing");

		// Verify statbez persist/find roundtrip (uses no reserved SQL words)
		EObject statBezEO = (EObject) statBezDescriptor.getInstantiationPolicy().buildNewInstance();
		statBezNameFeature = statbezEClass.getEStructuralFeature("statbez_name");
		assertNotNull(statBezNameFeature);
		EStructuralFeature gidFeature = statbezEClass.getEStructuralFeature("gid");
		assertNotNull(gidFeature);
		statBezEO.eSet(gidFeature, 999);
		statBezEO.eSet(statBezNameFeature, "Test-Bezirk");

		EObject findEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(statBezEO);
			em.getTransaction().commit();
			em.clear();

			findEO = em.find(statBezDescriptor.getJavaClass(), 999);
		} catch (Exception e) {
			fail("Fail statbez persist/find roundtrip", e);
		}

		assertNotNull(findEO);
		assertEquals(statbezEClass, findEO.eClass());
		assertEquals("Test-Bezirk", findEO.eGet(statBezNameFeature));
	}
	
//	@Test
	@TestAnnotations.CitizenEPersistenceSetup
	public void testConverterDebugOrig(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=citizen)") ServiceAware<EPackage> citizenPackageAware,
			@InjectService(cardinality = 0) ServiceAware<EntityManagerFactory> emfAware,
			@InjectConfiguration(withFactoryConfig = @WithFactoryConfiguration(factoryPid = "fennec.jpa.PersistenceUnit", name = "test")) Configuration emfConfig) throws InterruptedException, IOException {
		assertFalse(citizenPackageAware.isEmpty());
		assertFalse(dataSourceAware.isEmpty());
		assertTrue(emfAware.isEmpty());
		assertNull(emfConfig.getProperties());
		
		emfConfig.update(Dictionaries.asDictionary(
				Map.of(
						"fennec.jpa.model.target", "(emf.name=citizen)", 
						"fennec.jpa.converter.target", "(fennec.persistence.converter=geojson)", 
						"fennec.jpa.mappingFile", System.getProperty("rootPathConverter"), 
						"fennec.jpa.persistenceUnitName", "citizen")));
		
//		Thread.sleep(50000);
		assertNotNull(emfAware.waitForService(5000l));
		
		
		
		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);
		
		
		ClassDescriptor ageGroupsDescriptor = server.getDescriptorForAlias(ageGroupsEClass.getName());
		assertNotNull(ageGroupsDescriptor);
		ClassDescriptor einwohnerDescriptor = server.getDescriptorForAlias(einwohnerEClass.getName());
		assertNotNull(einwohnerDescriptor);
		ClassDescriptor plraumDescriptor = server.getDescriptorForAlias(plraumEClass.getName());
		assertNotNull(plraumDescriptor);
		ClassDescriptor genderDescriptor = server.getDescriptorForAlias(genderEClass.getName());
		assertNotNull(genderDescriptor);
		ClassDescriptor yearDescriptor = server.getDescriptorForAlias(yearEClass.getName());
		assertNotNull(yearDescriptor);
		ClassDescriptor statBezDescriptor = server.getDescriptorForAlias(statbezEClass.getName());
		assertNotNull(statBezDescriptor);
		ClassDescriptor townDescriptor = server.getDescriptorForAlias(townEClass.getName());
		assertNotNull(townDescriptor);
		
		EObject findSBEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			findSBEO = em.find(statBezDescriptor.getJavaClass(), 62);
			
		} catch (Exception e) {
			fail("Fail test One-to-One containment bidi-mapping find", e);
		}
		
		assertNotNull(findSBEO);
		assertEquals(statbezEClass, findSBEO.eClass());
		
		statBezNameFeature = statbezEClass.getEStructuralFeature("statbez_name");
		assertNotNull(statBezNameFeature);
		EStructuralFeature geojsonFeature = statbezEClass.getEStructuralFeature("geojsonO");
		assertNotNull(geojsonFeature);
		
		Object statBezNameObject = findSBEO.eGet(statBezNameFeature);
		assertNotNull(statBezNameObject);
		assertEquals("Lobeda-West", statBezNameObject);
		Object geojsonObject = findSBEO.eGet(geojsonFeature);
		assertNotNull(geojsonObject);
		
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.test.EPersistenceBase#testEMFAvailable(org.osgi.test.common.service.ServiceAware, org.osgi.test.common.service.ServiceAware, org.osgi.test.common.service.ServiceAware, org.osgi.service.cm.Configuration)
	 */
	@Override
	@Disabled
	public void testEMFAvailable(ServiceAware<DataSource> dataSourceAware, ServiceAware<EPackage> modelPackageAware,
			ServiceAware<EntityManagerFactory> emfAware, Configuration emfConfig)
			throws InterruptedException, IOException {
		// Does not work with the citizen model
		super.testEMFAvailable(dataSourceAware, modelPackageAware, emfAware, emfConfig);
	}

}
