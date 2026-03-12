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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.orm.EntityMapper;
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
 * See documentation here: 
 * 	https://github.com/osgi/osgi-test
 * 	https://github.com/osgi/osgi-test/wiki
 * Examples: https://github.com/osgi/osgi-test/tree/main/examples
 */
public class EPersistenceOneToOneTest extends EPersistenceBase{

	protected EClass classAEClass;
	protected EClass classBEClass;
	protected EClass classCEClass;
	protected EClass classDEClass;
	protected EClass classEEClass;

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.test.EPersistenceBase#setupMappings(org.eclipse.fennec.persistence.orm.helper.EntityMapper, org.eclipse.emf.ecore.EPackage)
	 */
	@Override
	protected EntityMappings setupMappings(EntityMapper mapper, EPackage ePackage) {
		classAEClass = (EClass) ePackage.getEClassifier("ClassAO2O");
		assertNotNull(classAEClass);
		classBEClass = (EClass) ePackage.getEClassifier("ClassBO2O");
		assertNotNull(classBEClass);
		classCEClass = (EClass) ePackage.getEClassifier("ClassCO2O");
		assertNotNull(classCEClass);
		classDEClass = (EClass) ePackage.getEClassifier("ClassDO2O");
		assertNotNull(classDEClass);
		classEEClass = (EClass) ePackage.getEClassifier("ClassEO2O");
		assertNotNull(classEEClass);
		EntityMappings mapping = mapper.createMappings(List.of(classAEClass, classBEClass, classCEClass, classDEClass, classEEClass));
		return mapping;
	}

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
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testOneToOneContainmentUni(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 5000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		assertFalse(modelPackageAware.isEmpty());
		assertFalse(dataSourceAware.isEmpty());
		assertFalse(emfAware.isEmpty());

		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);
		//
		ClassDescriptor classADescriptor = server.getDescriptorForAlias(classAEClass.getName());
		assertNotNull(classADescriptor);
		ClassDescriptor classBDescriptor = server.getDescriptorForAlias(classBEClass.getName());
		assertNotNull(classBDescriptor);

		EObject classAEO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classBEO = (EObject) classBDescriptor.getInstantiationPolicy().buildNewInstance();

		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		assertNotNull(aNameFeature);
		classAEO.eSet(aNameFeature, "The A Class!");
		EStructuralFeature aBContainmentFeature = classAEClass.getEStructuralFeature("bContainment");
		assertNotNull(aBContainmentFeature);

		EStructuralFeature bNameFeature = classBEClass.getEStructuralFeature("name");
		assertNotNull(bNameFeature);
		classBEO.eSet(bNameFeature, "The B Class!");

		classAEO.eSet(aBContainmentFeature, classBEO);

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classBEO);
			em.persist(classAEO);
			em.getTransaction().commit();
			em.clear();

		} catch (Exception e) {
			fail("Fail test One-to-One mapping persist", e);
		};

		EObject findEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classAEO);
			assertNotNull(id);
			findEO = em.find(classADescriptor.getJavaClass(), Integer.valueOf(id));

		} catch (Exception e) {
			fail("Fail test One-to-Many mapping find", e);
		}

		assertNotNull(findEO);
		assertNotEquals(classAEO, findEO);
		Object classANameObject = findEO.eGet(aNameFeature);
		assertNotNull(classANameObject);
		assertEquals("The A Class!", classANameObject);

		Object classBObject = findEO.eGet(aBContainmentFeature);
		assertNotNull(classBObject);
		assertInstanceOf(EObject.class, classBObject);
		EObject classBFEO = (EObject) classBObject;
		assertEquals(classBEClass, classBFEO.eClass());
		Object classBNameObject = classBFEO.eGet(bNameFeature);
		assertNotNull(classBNameObject);
		assertEquals("The B Class!", classBNameObject);

	}

	/**
	 * Test ono-to-one containment relations with opposites defined. 
	 * It tests loading both sides: parent and children for having the correct bi-directional setting
	 * Loading children is not really possible in EMF but in relational databases.
	 */
	@Test
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testOneToOneContainmentBiDiEOpposite(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 5000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		assertFalse(modelPackageAware.isEmpty());
		assertFalse(dataSourceAware.isEmpty());
		assertFalse(emfAware.isEmpty());

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
	 * Test one-to-one uni-directional non-containment setup
	 */
	@Test
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testOneToOneNonContainmentUni(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 5000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		assertFalse(modelPackageAware.isEmpty());
		assertFalse(dataSourceAware.isEmpty());
		assertFalse(emfAware.isEmpty());

		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);
		//
		ClassDescriptor classADescriptor = server.getDescriptorForAlias(classAEClass.getName());
		assertNotNull(classADescriptor);
		ClassDescriptor classDDescriptor = server.getDescriptorForAlias(classDEClass.getName());
		assertNotNull(classDDescriptor);

		EObject classAEO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classDEO = (EObject) classDDescriptor.getInstantiationPolicy().buildNewInstance();

		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		assertNotNull(aNameFeature);
		classAEO.eSet(aNameFeature, "The A Class!");
		EStructuralFeature aDNonContainmentFeature = classAEClass.getEStructuralFeature("dNonContainment");
		assertNotNull(aDNonContainmentFeature);

		EStructuralFeature dNameFeature = classDEClass.getEStructuralFeature("name");
		assertNotNull(dNameFeature);
		classDEO.eSet(dNameFeature, "The D Class!");

		classAEO.eSet(aDNonContainmentFeature, classDEO);

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classDEO);
			em.persist(classAEO);
			em.getTransaction().commit();
			em.clear();

		} catch (Exception e) {
			fail("Fail test One-to-One non-containment mapping persist", e);
		};

		EObject findEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classAEO);
			assertNotNull(id);
			findEO = em.find(classADescriptor.getJavaClass(), Integer.valueOf(id));

		} catch (Exception e) {
			fail("Fail test One-to-One non-containment mapping find", e);
		}

		assertNotNull(findEO);
		assertNotEquals(classAEO, findEO);
		Object classANameObject = findEO.eGet(aNameFeature);
		assertNotNull(classANameObject);
		assertEquals("The A Class!", classANameObject);

		Object classDObject = findEO.eGet(aDNonContainmentFeature);
		assertNotNull(classDObject);
		assertInstanceOf(EObject.class, classDObject);
		EObject classDFEO = (EObject) classDObject;
		assertEquals(classDEClass, classDFEO.eClass());
		Object classDNameObject = classDFEO.eGet(dNameFeature);
		assertNotNull(classDNameObject);
		assertEquals("The D Class!", classDNameObject);

	}

	/**
	 * Test one-to-one bi-directional non-containment setup
	 */
	@Test
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testOneToOneNonContainmentBiDiEOpposite(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 5000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		assertFalse(modelPackageAware.isEmpty());
		assertFalse(dataSourceAware.isEmpty());
		assertFalse(emfAware.isEmpty());

		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);
		//
		ClassDescriptor classADescriptor = server.getDescriptorForAlias(classAEClass.getName());
		assertNotNull(classADescriptor);
		ClassDescriptor classEDescriptor = server.getDescriptorForAlias(classEEClass.getName());
		assertNotNull(classEDescriptor);

		EObject classAEO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classEEO = (EObject) classEDescriptor.getInstantiationPolicy().buildNewInstance();

		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		assertNotNull(aNameFeature);
		classAEO.eSet(aNameFeature, "The A Class!");
		EStructuralFeature aENonContainmentFeature = classAEClass.getEStructuralFeature("eNonContainmentBidi");
		assertNotNull(aENonContainmentFeature);

		EStructuralFeature eNameFeature = classEEClass.getEStructuralFeature("name");
		assertNotNull(eNameFeature);
		classEEO.eSet(eNameFeature, "The E Class!");
		EStructuralFeature eClassAFeature = classEEClass.getEStructuralFeature("eClassA");
		assertNotNull(eClassAFeature);

		assertNull(classEEO.eGet(eClassAFeature));
		classAEO.eSet(aENonContainmentFeature, classEEO);
		assertEquals(classAEO, classEEO.eGet(eClassAFeature));

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classEEO);
			em.persist(classAEO);
			em.getTransaction().commit();
			em.clear();

		} catch (Exception e) {
			fail("Fail test One-to-One non-containment mapping persist", e);
		};

		EObject findAEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classAEO);
			assertNotNull(id);
			findAEO = em.find(classADescriptor.getJavaClass(), Integer.valueOf(id));

		} catch (Exception e) {
			fail("Fail test One-to-One non-containment mapping find", e);
		}

		assertNotNull(findAEO);
		assertNotEquals(classAEO, findAEO);
		Object classANameObject = findAEO.eGet(aNameFeature);
		assertNotNull(classANameObject);
		assertEquals("The A Class!", classANameObject);

		Object classEObject = findAEO.eGet(aENonContainmentFeature);
		assertNotNull(classEObject);
		assertInstanceOf(EObject.class, classEObject);
		EObject classEFEO = (EObject) classEObject;
		assertEquals(classEEClass, classEFEO.eClass());
		Object classENameObject = classEFEO.eGet(eNameFeature);
		assertNotNull(classENameObject);
		assertEquals("The E Class!", classENameObject);
		Object classAObject = classEFEO.eGet(eClassAFeature);
		assertNotNull(classAObject);
		assertEquals(findAEO, classAObject);

		EObject findEEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classEEO);
			assertNotNull(id);
			findEEO = em.find(classEDescriptor.getJavaClass(), Integer.valueOf(id));

		} catch (Exception e) {
			fail("Fail test One-to-One non-containment mapping find", e);
		}

		assertNotNull(findEEO);
		assertNotEquals(classEEO, findEEO);
		classENameObject = findEEO.eGet(eNameFeature);
		assertNotNull(classENameObject);
		assertEquals("The E Class!", classENameObject);

		classAObject = findEEO.eGet(eClassAFeature);
		assertNotNull(classAObject);
		assertInstanceOf(EObject.class, classAObject);
		EObject classAFEO = (EObject) classAObject;
		assertEquals(classAEClass, classAFEO.eClass());
		classANameObject = classAFEO.eGet(aNameFeature);
		assertNotNull(classANameObject);
		assertEquals("The A Class!", classANameObject);
		classEObject = classAFEO.eGet(aENonContainmentFeature);
		assertNotNull(classEObject);
		assertEquals(findEEO, classEObject);
	}

	/**
	 * Test one-to-one non-containment relations with no opposites defined. 
	 * It tests loading both sides: parent and children for having the correct bi-directional setting
	 * Loading children is not really possible in EMF but in relational databases.
	 */
	@Test
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testOneToOneNonContainmentBiDiNoEOpposite(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 5000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		assertFalse(modelPackageAware.isEmpty());
		assertFalse(dataSourceAware.isEmpty());
		assertFalse(emfAware.isEmpty());

		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);
		//
		ClassDescriptor classADescriptor = server.getDescriptorForAlias(classAEClass.getName());
		assertNotNull(classADescriptor);
		ClassDescriptor classDDescriptor = server.getDescriptorForAlias(classDEClass.getName());
		assertNotNull(classDDescriptor);

		EObject classAEO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classDEO = (EObject) classDDescriptor.getInstantiationPolicy().buildNewInstance();

		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		assertNotNull(aNameFeature);
		classAEO.eSet(aNameFeature, "The A Class!");
		EStructuralFeature aDNonContainmentFeature = classAEClass.getEStructuralFeature("dNonContainment");
		assertNotNull(aDNonContainmentFeature);

		EStructuralFeature dNameFeature = classDEClass.getEStructuralFeature("name");
		assertNotNull(dNameFeature);
		classDEO.eSet(dNameFeature, "The D Class!");
		EStructuralFeature dClassAFeature = classDEClass.getEStructuralFeature("dClassA");
		assertNotNull(dClassAFeature);
		assertNull(classDEO.eGet(dClassAFeature));

		classAEO.eSet(aDNonContainmentFeature, classDEO);
		classDEO.eSet(dClassAFeature, classAEO);

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classDEO);
			em.persist(classAEO);
			em.getTransaction().commit();
			em.clear();

		} catch (Exception e) {
			fail("Fail test One-to-One non-containment mapping persist", e);
		};

		EObject findAEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classAEO);
			assertNotNull(id);
			findAEO = em.find(classADescriptor.getJavaClass(), Integer.valueOf(id));

		} catch (Exception e) {
			fail("Fail test One-to-One non-containment mapping find", e);
		}

		assertNotNull(findAEO);
		assertNotEquals(classAEO, findAEO);
		Object classANameObject = findAEO.eGet(aNameFeature);
		assertNotNull(classANameObject);
		assertEquals("The A Class!", classANameObject);

		Object classDObject = findAEO.eGet(aDNonContainmentFeature);
		assertNotNull(classDObject);
		assertInstanceOf(EObject.class, classDObject);
		EObject classDFEO = (EObject) classDObject;
		assertEquals(classDEClass, classDFEO.eClass());
		Object classDNameObject = classDFEO.eGet(dNameFeature);
		assertNotNull(classDNameObject);
		assertEquals("The D Class!", classDNameObject);

		Object dClassAObject = classDFEO.eGet(dClassAFeature);
		assertNotNull(dClassAObject);
		assertInstanceOf(EObject.class, dClassAObject);
		EObject classAFEO = (EObject) dClassAObject;
		assertEquals("The A Class!", classAFEO.eGet(aNameFeature));
		assertEquals(findAEO, classAFEO);

		EObject findDEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classDEO);
			assertNotNull(id);
			findDEO = em.find(classDDescriptor.getJavaClass(), Integer.valueOf(id));

		} catch (Exception e) {
			fail("Fail test One-to-One non-containment mapping find", e);
		}
		assertNotNull(findDEO);
		assertNotEquals(classDEO, findDEO);
		classDNameObject = findDEO.eGet(dNameFeature);
		assertNotNull(classDNameObject);
		assertEquals("The D Class!", classDNameObject);

		dClassAObject = findDEO.eGet(dClassAFeature);
		assertNotNull(dClassAObject);
		assertInstanceOf(EObject.class, dClassAObject);
		classAFEO = (EObject) dClassAObject;
		assertEquals("The A Class!", classAFEO.eGet(aNameFeature));
		// both return values must be different, because they are from different entity managers
		assertNotEquals(findAEO, classAFEO);

		classDObject = classAFEO.eGet(aDNonContainmentFeature);
		assertNotNull(classDObject);
		assertInstanceOf(EObject.class, classDObject);
		assertEquals(findDEO, classDObject);
	}

}
