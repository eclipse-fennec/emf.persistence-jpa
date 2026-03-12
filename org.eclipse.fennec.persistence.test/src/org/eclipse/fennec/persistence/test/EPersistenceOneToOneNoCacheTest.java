/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.persistence.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Map;

import javax.sql.DataSource;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.test.annotations.TestAnnotations;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.sessions.server.Server;
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
 * Same test like the super class but with a no-cache configuration. This should lead to same results
 * @author Mark Hoffmann
 * @since 21.01.2025
 */
public class EPersistenceOneToOneNoCacheTest extends EPersistenceOneToOneTest{

	@Test
	@TestAnnotations.DefaultEPersistenceSetup
	public void testEMFAvailableDebugOrig(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
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
						"fennec.jpa.mappingFile", getModelFile().toURI().toString(), 
						"fennec.jpa.persistenceUnitName", "person",
						"fennec.jpa.ext.eclipselink.cache.shared.default", "false",
						"fennec.jpa.ext.eclipselink.ddl-generation", "create-or-extend-tables")));
//		Thread.sleep(50000);
		assertNotNull(emfAware.waitForService(7000l));

		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);

		ClassDescriptor classADescriptor = server.getDescriptorForAlias(classAEClass.getName());
		assertNotNull(classADescriptor);
		ClassDescriptor classCDescriptor = server.getDescriptorForAlias(classCEClass.getName());
		assertNotNull(classCDescriptor);

		EObject classAEO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classCEO = (EObject) classCDescriptor.getInstantiationPolicy().buildNewInstance();
		//

		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		assertNotNull(aNameFeature);
		classAEO.eSet(aNameFeature, "The A Class!");
		EStructuralFeature aCContainmentFeature = classAEClass.getEStructuralFeature("cContainmentBidi");
		assertNotNull(aCContainmentFeature);

		EStructuralFeature cNameFeature = classCEClass.getEStructuralFeature("name");
		assertNotNull(cNameFeature);
		classCEO.eSet(cNameFeature, "The C Class!");
		EStructuralFeature cClassAFeature = classCEClass.getEStructuralFeature("cClassA");
		assertNotNull(cClassAFeature);

		assertNull(classAEO.eGet(aCContainmentFeature));
		assertNull(classCEO.eGet(cClassAFeature));
		classAEO.eSet(aCContainmentFeature, classCEO);
		assertNotNull(classCEO.eGet(cClassAFeature));
		assertEquals(classAEO, classCEO.eGet(cClassAFeature));

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classCEO);
			em.persist(classAEO);
			em.getTransaction().commit();
			em.clear();

		} catch (Exception e) {
			fail("Fail test One-to-One mapping persist", e);
		};

		EObject findAEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classAEO);
			assertNotNull(id);
			findAEO = em.find(classADescriptor.getJavaClass(), Integer.valueOf(id));

		} catch (Exception e) {
			fail("Fail test One-to-One containment bidi-mapping find", e);
		}

		assertNotNull(findAEO);
		assertNotEquals(classAEO, findAEO);
		Object classANameObject = findAEO.eGet(aNameFeature);
		assertNotNull(classANameObject);
		assertEquals("The A Class!", classANameObject);

		Object classCObject = findAEO.eGet(aCContainmentFeature);
		assertNotNull(classCObject);
		assertInstanceOf(EObject.class, classCObject);
		EObject classCFEO = (EObject) classCObject;
		assertEquals(classCEClass, classCFEO.eClass());
		Object classCNameObject = classCFEO.eGet(cNameFeature);
		assertNotNull(classCNameObject);
		assertEquals("The C Class!", classCNameObject);

		Object cClassAObject = classCFEO.eGet(cClassAFeature);
		assertNotNull(cClassAObject);
		assertInstanceOf(EObject.class, cClassAObject);
		EObject classAFEO = (EObject) cClassAObject;
		assertEquals("The A Class!", classAFEO.eGet(aNameFeature));
		assertEquals(findAEO, classAFEO);

		EObject findCEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classCEO);
			assertNotNull(id);
			findCEO = em.find(classCDescriptor.getJavaClass(), Integer.valueOf(id));

		} catch (Exception e) {
			fail("Fail test One-to-One non-containment mapping find", e);
		}
		assertNotNull(findCEO);
		assertNotEquals(classCEO, findCEO);
		classCNameObject = findCEO.eGet(cNameFeature);
		assertNotNull(classCNameObject);
		assertEquals("The C Class!", classCNameObject);

		cClassAObject = findCEO.eGet(cClassAFeature);
		assertNotNull(cClassAObject);
		assertInstanceOf(EObject.class, cClassAObject);
		classAFEO = (EObject) cClassAObject;
		assertEquals("The A Class!", classAFEO.eGet(aNameFeature));
		// both return values must be different, because they are from different entity managers
		assertNotEquals(findAEO, classAFEO);

		classCObject = classAFEO.eGet(aCContainmentFeature);
		assertNotNull(classCObject);
		assertInstanceOf(EObject.class, classCObject);
		assertEquals(findCEO, classCObject);
	}

	/**
	 * Tests an one-to-one uni-directional containment setting
	 */
	@Test
	@TestAnnotations.NoCacheEPersistenceConfiguration
	public void testOneToOneContainmentUni(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 5000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		super.testOneToOneContainmentUni(dataSourceAware, modelPackageAware, emfAware);
	}

	/**
	 * Test ono-to-one containment relations with opposites defined. 
	 * It tests loading both sides: parent and children for having the correct bi-directional setting
	 * Loading children is not really possible in EMF but in relational databases.
	 */
	@Test
	@TestAnnotations.NoCacheEPersistenceConfiguration
	public void testOneToOneContainmentBiDiEOpposite(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 5000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		super.testOneToOneContainmentBiDiEOpposite(dataSourceAware, modelPackageAware, emfAware);
	}

	/**
	 * Test one-to-one uni-directional non-containment setup
	 */
	@Test
	@TestAnnotations.NoCacheEPersistenceConfiguration
	public void testOneToOneNonContainmentUni(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 5000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		super.testOneToOneNonContainmentUni(dataSourceAware, modelPackageAware, emfAware);
	}

	/**
	 * Test one-to-one bi-directional non-containment setup
	 */
	@Test
	@TestAnnotations.NoCacheEPersistenceConfiguration
	public void testOneToOneNonContainmentBiDiEOpposite(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 5000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		super.testOneToOneNonContainmentBiDiEOpposite(dataSourceAware, modelPackageAware, emfAware);
	}

	/**
	 * Test one-to-one non-containment relations with no opposites defined. 
	 * It tests loading both sides: parent and children for having the correct bi-directional setting
	 * Loading children is not really possible in EMF but in relational databases.
	 */
	@Test
	@TestAnnotations.NoCacheEPersistenceConfiguration
	public void testOneToOneNonContainmentBiDiNoEOpposite(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 5000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		super.testOneToOneNonContainmentBiDiNoEOpposite(dataSourceAware, modelPackageAware, emfAware);
	}

}
