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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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
import org.osgi.service.cm.Configuration;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.config.InjectConfiguration;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.dictionary.Dictionaries;
import org.osgi.test.common.service.ServiceAware;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

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
	private EStructuralFeature einwohnerAnzahlFeature;
	private EStructuralFeature einwohnerJahrFeature;
	private EStructuralFeature einwohnerStatBezFeature;
	private EStructuralFeature einwohnerGenderFeature;
	private EStructuralFeature einwohnerAgeFeature;
	private EStructuralFeature einwohnerIdFeature;
	private EStructuralFeature genderNameFeature;
	private EStructuralFeature ageGroupH1Feature;
	private EStructuralFeature statBezNameFeature;
	private EStructuralFeature yearOrdinalFeature;

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
	
//    @Test
    @TestAnnotations.CitizenEPersistenceSetup
    void serviceWithConfigurationTest(@InjectService(timeout = 500) ServiceAware<DataSource> serviceAwareDataSource, //
            @InjectService(timeout = 500) ServiceAware<XADataSource> serviceAwareXaDataSource, //
            @InjectService(timeout = 500) ServiceAware<ConnectionPoolDataSource> serviceAwareCpDataSource)
            throws Exception {

        assertThat(serviceAwareDataSource.getServices()).hasSize(1);
        assertThat(serviceAwareXaDataSource.getServices()).hasSize(1);
        assertThat(serviceAwareCpDataSource.getServices()).hasSize(1);
    }

//	@Test
	@TestAnnotations.CitizenEPersistenceSetup
	public void testEMFAvailableDebugOrig(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=citizen)") ServiceAware<EPackage> citizenPackageAware,
			@InjectService(cardinality = 0) ServiceAware<EntityManagerFactory> emfAware,
			@InjectConfiguration(withFactoryConfig = @WithFactoryConfiguration(factoryPid = "fennec.jpa.PersistenceUnit", name = "test")) Configuration emfConfig) throws InterruptedException, IOException {
		assertFalse(citizenPackageAware.isEmpty());
		assertFalse(dataSourceAware.isEmpty());
		assertTrue(emfAware.isEmpty());
		assertNull(emfConfig.getProperties());

		emfConfig.update(Dictionaries.asDictionary(
				Map.of(
						"fennec.jpa.model", "(emf.name=citizen)", 
						"fennec.jpa.mappingFile", System.getProperty("rootPath"), 
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

		EObject findYEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			findYEO = em.find(yearDescriptor.getJavaClass(), 2022);

		} catch (Exception e) {
			fail("Fail test One-to-One containment bidi-mapping find", e);
		}

		assertNotNull(findYEO);
		assertEquals(yearEClass, findYEO.eClass());
		
		yearOrdinalFeature = yearEClass.getEStructuralFeature("ordinal");
		assertNotNull(yearOrdinalFeature);
		
		Object yearOrdinalObject = findYEO.eGet(yearOrdinalFeature);
		assertNotNull(yearOrdinalObject);
		assertEquals(2022, yearOrdinalObject);
		
		EObject findTEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			findTEO = em.find(townDescriptor.getJavaClass(), 1);

		} catch (Exception e) {
			fail("Fail test One-to-One containment bidi-mapping find", e);
		}

		assertNotNull(findTEO);
		assertEquals(townEClass, findTEO.eClass());
		
		EStructuralFeature townNameFeature = townEClass.getEStructuralFeature("name");
		assertNotNull(townNameFeature);
		
		Object townNameObject = findTEO.eGet(townNameFeature);
		assertNotNull(townNameObject);
		assertEquals("Jena", townNameObject);
		
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
		
		Object statBezNameObject = findSBEO.eGet(statBezNameFeature);
		assertNotNull(statBezNameObject);
		assertEquals("Lobeda-West", statBezNameObject);
		
		EObject findAGEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			findAGEO = em.find(ageGroupsDescriptor.getJavaClass(), 10);
			
		} catch (Exception e) {
			fail("Fail test One-to-One containment bidi-mapping find", e);
		}
		
		assertNotNull(findAGEO);
		assertEquals(ageGroupsEClass, findAGEO.eClass());
		
		ageGroupH1Feature = ageGroupsEClass.getEStructuralFeature("H1");
		assertNotNull(ageGroupH1Feature);
		
		Object ageGroupH1Object = findAGEO.eGet(ageGroupH1Feature);
		assertNotNull(ageGroupH1Object);
		assertEquals("10-16", ageGroupH1Object);
		
		EObject findGEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			findGEO = em.find(genderDescriptor.getJavaClass(), "W");
			
		} catch (Exception e) {
			fail("Fail test One-to-One containment bidi-mapping find", e);
		}
		
		assertNotNull(findGEO);
		assertEquals(genderEClass, findGEO.eClass());
		
		genderNameFeature = genderEClass.getEStructuralFeature("name");
		assertNotNull(genderNameFeature);
		
		Object genderNameObject = findGEO.eGet(genderNameFeature);
		assertNotNull(genderNameObject);
		assertEquals("weiblich", genderNameObject);
		
		EObject findPREO = null;
		try (EntityManager em = emf.createEntityManager()) {
			findPREO = em.find(plraumDescriptor.getJavaClass(), 2);
			
		} catch (Exception e) {
			fail("Fail test One-to-One containment bidi-mapping find", e);
		}
		
		assertNotNull(findPREO);
		assertEquals(plraumEClass, findPREO.eClass());
		
		EStructuralFeature plraumFeature = plraumEClass.getEStructuralFeature("plraum");
		assertNotNull(plraumFeature);
		EStructuralFeature townFeature = plraumEClass.getEStructuralFeature("town");
		assertNotNull(townFeature);
		
		Object plraumNameObject = findPREO.eGet(plraumFeature);
		assertNotNull(plraumNameObject);
		assertEquals("Nord", plraumNameObject);
		Object townObject = findPREO.eGet(townFeature);
		assertNotNull(plraumNameObject);
		assertInstanceOf(EObject.class, townObject);
		EObject townEO = (EObject) townObject;
		assertEquals(townEClass, townEO.eClass());
		townNameObject = townEO.eGet(townNameFeature);
		assertNotNull(townNameObject);
		assertEquals("Jena", townNameObject);
		
		
		try (EntityManager em = emf.createEntityManager()) {
			Class<?> plClass = plraumDescriptor.getJavaClass();
			List<?> pls = em.createQuery("SELECT p FROM plraum p", plClass).getResultList();
			assertNotNull(pls);
			assertFalse(pls.isEmpty());
			pls.stream().filter(EObject.class::isInstance).map(EObject.class::cast).forEach(pl->{
				Object nameObject = pl.eGet(plraumFeature);
				System.out.println("Planungsraum " + nameObject);
			});
		} catch (Exception e) {
			fail("Fail test One-to-One containment bidi-mapping find", e);
		}
		
		EObject findEEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			findEEO = em.find(einwohnerDescriptor.getJavaClass(), Long.valueOf(6l));
			
		} catch (Exception e) {
			fail("Fail test One-to-One containment bidi-mapping find", e);
		}
		
		assertNotNull(findEEO);
		assertEquals(einwohnerEClass, findEEO.eClass());
		
		einwohnerAnzahlFeature = einwohnerEClass.getEStructuralFeature("Anzahl");
		assertNotNull(einwohnerAnzahlFeature);
		einwohnerJahrFeature = einwohnerEClass.getEStructuralFeature("JAHR");
		assertNotNull(einwohnerJahrFeature);
		einwohnerStatBezFeature = einwohnerEClass.getEStructuralFeature("STATBEZ");
		assertNotNull(einwohnerStatBezFeature);
		einwohnerGenderFeature = einwohnerEClass.getEStructuralFeature("KER_GESCH");
		assertNotNull(einwohnerGenderFeature);
		einwohnerAgeFeature = einwohnerEClass.getEStructuralFeature("AGE");
		assertNotNull(einwohnerAgeFeature);
		einwohnerIdFeature = einwohnerEClass.getEStructuralFeature("id");
		assertNotNull(einwohnerIdFeature);
		
		Object einwohnerAnzahlObject = findEEO.eGet(einwohnerAnzahlFeature);
		assertNotNull(einwohnerAnzahlObject);
		assertEquals(4, einwohnerAnzahlObject);
		
		Object einwohnerJahrObject = findEEO.eGet(einwohnerJahrFeature);
		assertNotNull(einwohnerJahrObject);
		assertInstanceOf(EObject.class, einwohnerJahrObject);
		EObject yearEO = (EObject) einwohnerJahrObject;
		assertEquals(yearEClass, yearEO.eClass());
		assertEquals(2008, yearEO.eGet(yearOrdinalFeature));
		
		Object einwohnerStatBezObject = findEEO.eGet(einwohnerStatBezFeature);
		assertNotNull(einwohnerStatBezObject);
		assertInstanceOf(EObject.class, einwohnerStatBezObject);
		EObject statBezEO = (EObject) einwohnerStatBezObject;
		assertEquals(statbezEClass, statBezEO.eClass());
		assertEquals("Ammerbach Ort", statBezEO.eGet(statBezNameFeature));
		
		Object einwohnerGenderObject = findEEO.eGet(einwohnerGenderFeature);
		assertNotNull(einwohnerGenderObject);
		assertInstanceOf(EObject.class, einwohnerGenderObject);
		EObject genderEO = (EObject) einwohnerGenderObject;
		assertEquals(genderEClass, genderEO.eClass());
		assertEquals("männlich", genderEO.eGet(genderNameFeature));
		
		Object einwohnerAgeObject = findEEO.eGet(einwohnerAgeFeature);
		assertNotNull(einwohnerAgeObject);
		assertInstanceOf(EObject.class, einwohnerAgeObject);
		EObject ageEO = (EObject) einwohnerAgeObject;
		assertEquals(ageGroupsEClass, ageEO.eClass());
		assertEquals("3-6", ageEO.eGet(ageGroupH1Feature));
		
		try (EntityManager em = emf.createEntityManager()) {
			Class<?> einwohnerClass = einwohnerDescriptor.getJavaClass();
			TypedQuery<?> query = em.createQuery("SELECT e FROM einwohner e", einwohnerClass);
			query.setMaxResults(100);
			List<?> citizens = query.getResultList();
			assertNotNull(citizens);
			assertFalse(citizens.isEmpty());
			citizens.stream().filter(EObject.class::isInstance).map(EObject.class::cast).forEach(this::printEinwohner);
			
			query.setFirstResult(150);
			citizens = query.getResultList();
			assertNotNull(citizens);
			assertFalse(citizens.isEmpty());
			citizens.stream().filter(EObject.class::isInstance).map(EObject.class::cast).forEach(this::printEinwohner);
		} catch (Exception e) {
			fail("Fail test One-to-One containment bidi-mapping find", e);
		}
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

	private void printEinwohner(EObject einwohner) {
		assertNotNull(einwohner);
		Object idO = einwohner.eGet(einwohnerIdFeature);
		Object anzO = einwohner.eGet(einwohnerAnzahlFeature);
		Object yearO = ((EObject)einwohner.eGet(einwohnerJahrFeature)).eGet(yearOrdinalFeature);
		Object ageO = ((EObject)einwohner.eGet(einwohnerAgeFeature)).eGet(ageGroupH1Feature);
		Object genderO = ((EObject)einwohner.eGet(einwohnerGenderFeature)).eGet(genderNameFeature);
		Object sbO = ((EObject)einwohner.eGet(einwohnerStatBezFeature)).eGet(statBezNameFeature);
		System.out.println(String.format("Einwohner[%s] Jahr: %s; Altersgruppe: %s; Geschlecht: %s; stat. Bezirk: %s; Anzahl: %s", idO, yearO, ageO, genderO, sbO, anzO));
	}
}
