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
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.List;

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
		yearEClass = (EClass) ePackage.getEClassifier("YearEntity");
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
		assertNotNull(yearDescriptor, "YearEntity descriptor missing");
		ClassDescriptor statBezDescriptor = server.getDescriptorForAlias(statbezEClass.getName());
		assertNotNull(statBezDescriptor, "statbez descriptor missing");
		ClassDescriptor townDescriptor = server.getDescriptorForAlias(townEClass.getName());
		assertNotNull(townDescriptor, "Town descriptor missing");

		// Persist/find roundtrip for statbez
		statBezNameFeature = statbezEClass.getEStructuralFeature("statbez_name");
		assertNotNull(statBezNameFeature);

		EObject statBezEO = (EObject) statBezDescriptor.getInstantiationPolicy().buildNewInstance();
		EStructuralFeature gidFeature = statbezEClass.getEStructuralFeature("gid");
		assertNotNull(gidFeature);
		statBezEO.eSet(gidFeature, 1);
		statBezEO.eSet(statBezNameFeature, "Test-Bezirk");

		EObject findEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(statBezEO);
			em.getTransaction().commit();
			em.clear();

			findEO = em.find(statBezDescriptor.getJavaClass(), 1);
		} catch (Exception e) {
			fail("Fail statbez persist/find roundtrip", e);
		}

		assertNotNull(findEO);
		assertEquals(statbezEClass, findEO.eClass());
		assertEquals("Test-Bezirk", findEO.eGet(statBezNameFeature));
	}

	@Override
	@Disabled("Does not work with the citizen model — requires different PU configuration")
	public void testEMFAvailable(ServiceAware<DataSource> dataSourceAware, ServiceAware<EPackage> modelPackageAware,
			ServiceAware<EntityManagerFactory> emfAware, Configuration emfConfig)
			throws InterruptedException, IOException {
		super.testEMFAvailable(dataSourceAware, modelPackageAware, emfAware, emfConfig);
	}

}
