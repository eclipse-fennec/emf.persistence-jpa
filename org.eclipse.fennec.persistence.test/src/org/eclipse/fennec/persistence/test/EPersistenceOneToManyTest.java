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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
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
public class EPersistenceOneToManyTest extends EPersistenceBase{

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
		classAEClass = (EClass) ePackage.getEClassifier("ClassAO2M");
		assertNotNull(classAEClass);
		classBEClass = (EClass) ePackage.getEClassifier("ClassBO2M");
		assertNotNull(classBEClass);
		classCEClass = (EClass) ePackage.getEClassifier("ClassCO2M");
		assertNotNull(classCEClass);
		classDEClass = (EClass) ePackage.getEClassifier("ClassDO2M");
		assertNotNull(classDEClass);
		classEEClass = (EClass) ePackage.getEClassifier("ClassEO2M");
		assertNotNull(classEEClass);
		EntityMappings mapping = mapper.createMappings(List.of(classAEClass, classBEClass, classCEClass, classDEClass, classEEClass));
		Resource resource = rs.createResource(URI.createFileURI("/home/mark/test.eorm"));
		resource.getContents().add(mapping);
		try {
			resource.save(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
//		Thread.sleep(50000l);
		assertNotNull(emfAware.waitForService(7000l));
		
		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);
		
		ClassDescriptor classADescriptor = server.getDescriptorForAlias(classAEClass.getName());
		assertNotNull(classADescriptor);
		ClassDescriptor classBDescriptor = server.getDescriptorForAlias(classBEClass.getName());
		assertNotNull(classBDescriptor);
		
		EObject classAEO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classB01EO = (EObject) classBDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classB02EO = (EObject) classBDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classB03EO = (EObject) classBDescriptor.getInstantiationPolicy().buildNewInstance();
		//
		
		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		assertNotNull(aNameFeature);
		classAEO.eSet(aNameFeature, "The A-Class!");
		
		EStructuralFeature bContainmentFeature = classAEClass.getEStructuralFeature("bContainment");
		assertNotNull(bContainmentFeature);

		EStructuralFeature bNameFeature = classBEClass.getEStructuralFeature("name");
		assertNotNull(bNameFeature);
		classB01EO.eSet(bNameFeature, "The First B-Class!");
		classB02EO.eSet(bNameFeature, "The Second B-Class!");
		classB03EO.eSet(bNameFeature, "The Third B-Class!");
		
		List<EObject> bContainmentList = List.of(classB01EO, classB02EO, classB03EO); 
		classAEO.eSet(bContainmentFeature, bContainmentList);
		
		
		EObject findEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classAEO);
			em.getTransaction().commit();
			em.clear();
			
		} catch (Exception e) {
			fail("Fail test One-to-Many mapping persist", e);
		};
		
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classAEO);
			assertNotNull(id);
			findEO = em.find(classADescriptor.getJavaClass(), Integer.valueOf(id));
			
		} catch (Exception e) {
			fail("Fail test One-to-Many mapping find", e);
		}
		
		assertNotNull(findEO);
		assertNotEquals(classAEO, findEO);
		Object aNameObject = findEO.eGet(aNameFeature);
		assertNotNull(aNameObject);
		assertEquals("The A-Class!", aNameObject);
		
		Object bContainmentObject = findEO.eGet(bContainmentFeature);
		assertNotNull(bContainmentObject);
		assertInstanceOf(List.class, bContainmentObject);
		List<?> bContainmentResultList = (List<?>) bContainmentObject;
		assertEquals(3, bContainmentResultList.size());
		final Set<String> values = new HashSet<>(Set.of("The First B-Class!", "The Second B-Class!", "The Third B-Class!"));
		bContainmentResultList.forEach(o->{
			assertInstanceOf(EObject.class, o);
			EObject eo = (EObject) o;
			assertEquals(classBEClass, eo.eClass());
			Object bNameObject = eo.eGet(bNameFeature);
			assertNotNull(bNameObject);
			assertTrue(values.remove(bNameObject));
			assertFalse(bContainmentList.contains(eo));
		});
		assertTrue(values.isEmpty());
	}
	
	/**
	 * Test uni-directional one-to-many containment relations
	 */
	@Test
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testOneToManyContainmentUni(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 5000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		assertFalse(modelPackageAware.isEmpty());
		assertFalse(dataSourceAware.isEmpty());
		assertFalse(emfAware.isEmpty());
		
		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);
		
		ClassDescriptor classADescriptor = server.getDescriptorForAlias(classAEClass.getName());
		assertNotNull(classADescriptor);
		ClassDescriptor classBDescriptor = server.getDescriptorForAlias(classBEClass.getName());
		assertNotNull(classBDescriptor);
		
		EObject classAEO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classB01EO = (EObject) classBDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classB02EO = (EObject) classBDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classB03EO = (EObject) classBDescriptor.getInstantiationPolicy().buildNewInstance();
		//
		
		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		assertNotNull(aNameFeature);
		classAEO.eSet(aNameFeature, "The A-Class!");
		
		EStructuralFeature bContainmentFeature = classAEClass.getEStructuralFeature("bContainment");
		assertNotNull(bContainmentFeature);

		EStructuralFeature bNameFeature = classBEClass.getEStructuralFeature("name");
		assertNotNull(bNameFeature);
		classB01EO.eSet(bNameFeature, "The First B-Class!");
		classB02EO.eSet(bNameFeature, "The Second B-Class!");
		classB03EO.eSet(bNameFeature, "The Third B-Class!");
		
		List<EObject> bContainmentList = List.of(classB01EO, classB02EO, classB03EO); 
		classAEO.eSet(bContainmentFeature, bContainmentList);
		
		
		EObject findEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classAEO);
			em.getTransaction().commit();
			em.clear();
			
		} catch (Exception e) {
			fail("Fail test One-to-Many mapping persist", e);
		};
		
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classAEO);
			assertNotNull(id);
			findEO = em.find(classADescriptor.getJavaClass(), Integer.valueOf(id));
			
		} catch (Exception e) {
			fail("Fail test One-to-Many mapping find", e);
		}
		
		assertNotNull(findEO);
		assertNotEquals(classAEO, findEO);
		Object aNameObject = findEO.eGet(aNameFeature);
		assertNotNull(aNameObject);
		assertEquals("The A-Class!", aNameObject);
		
		Object bContainmentObject = findEO.eGet(bContainmentFeature);
		assertNotNull(bContainmentObject);
		assertInstanceOf(List.class, bContainmentObject);
		List<?> bContainmentResultList = (List<?>) bContainmentObject;
		assertEquals(3, bContainmentResultList.size());
		final Set<String> values = new HashSet<>(Set.of("The First B-Class!", "The Second B-Class!", "The Third B-Class!"));
		bContainmentResultList.forEach(o->{
			assertInstanceOf(EObject.class, o);
			EObject eo = (EObject) o;
			assertEquals(classBEClass, eo.eClass());
			Object bNameObject = eo.eGet(bNameFeature);
			assertNotNull(bNameObject);
			assertTrue(values.remove(bNameObject));
			assertFalse(bContainmentList.contains(eo));
		});
		assertTrue(values.isEmpty());
	}
	
	/**
	 * Test bi-directional one-to-many containment relations, iwth many-to-one on the other side
	 */
	@Test
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testOneToManyContainmentBidi(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
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
		EObject classC01EO = (EObject) classCDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classC02EO = (EObject) classCDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classC03EO = (EObject) classCDescriptor.getInstantiationPolicy().buildNewInstance();
		//
		
		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		assertNotNull(aNameFeature);
		classAEO.eSet(aNameFeature, "The A-Class!");
		
		EStructuralFeature aCContainmentFeature = classAEClass.getEStructuralFeature("cContainmentBidi");
		assertNotNull(aCContainmentFeature);
		
		EStructuralFeature cNameFeature = classCEClass.getEStructuralFeature("name");
		assertNotNull(cNameFeature);
		classC01EO.eSet(cNameFeature, "The First C-Class!");
		classC02EO.eSet(cNameFeature, "The Second C-Class!");
		classC03EO.eSet(cNameFeature, "The Third C-Class!");
		
		EStructuralFeature cClassAFeature = classCEClass.getEStructuralFeature("cClassA");
		assertNotNull(cClassAFeature);
		
		List<EObject> cContainmentList = List.of(classC01EO, classC02EO, classC03EO); 
		classAEO.eSet(aCContainmentFeature, cContainmentList);
		
		
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classAEO);
			em.getTransaction().commit();
			em.clear();
			
		} catch (Exception e) {
			fail("Fail test One-to-Many mapping persist", e);
		};
		
		EObject findAEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classAEO);
			assertNotNull(id);
			findAEO = em.find(classADescriptor.getJavaClass(), Integer.valueOf(id));
			
		} catch (Exception e) {
			fail("Fail test One-to-Many mapping find", e);
		}
		
		assertNotNull(findAEO);
		assertNotEquals(classAEO, findAEO);
		Object aNameObject = findAEO.eGet(aNameFeature);
		assertNotNull(aNameObject);
		assertEquals("The A-Class!", aNameObject);
		
		Object cContainmentObject = findAEO.eGet(aCContainmentFeature);
		assertNotNull(cContainmentObject);
		assertInstanceOf(List.class, cContainmentObject);
		List<?> cContainmentResultList = (List<?>) cContainmentObject;
		assertEquals(3, cContainmentResultList.size());
		final Set<String> values = new HashSet<>(Set.of("The First C-Class!", "The Second C-Class!", "The Third C-Class!"));
		for (Object o : cContainmentResultList) {
			assertInstanceOf(EObject.class, o);
			EObject eo = (EObject) o;
			assertEquals(classCEClass, eo.eClass());
			Object cNameObject = eo.eGet(cNameFeature);
			assertNotNull(cNameObject);
			assertTrue(values.remove(cNameObject));
			assertFalse(cContainmentList.contains(eo));
			Object cClassAObject = eo.eGet(cClassAFeature);
			assertNotNull(cClassAObject);
			EObject classAFEO = (EObject) cClassAObject;
			assertEquals(classAEClass, classAFEO.eClass());
			assertEquals(findAEO, classAFEO);
		}
		assertTrue(values.isEmpty());
		
		EObject findCEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classC02EO);
			assertNotNull(id);
			findCEO = em.find(classCDescriptor.getJavaClass(), Integer.valueOf(id));
			
		} catch (Exception e) {
			fail("Fail test One-to-Many mapping find", e);
		}
		
		assertNotNull(findCEO);
		assertEquals(classCEClass, findCEO.eClass());
		assertNotEquals(classC02EO, findCEO);
		Object cNameObject = findCEO.eGet(cNameFeature);
		assertNotNull(cNameObject);
		assertEquals("The Second C-Class!", cNameObject);
		
		Object cClassAObject = findCEO.eGet(cClassAFeature);
		assertNotNull(cClassAObject);
		EObject classAFEO = (EObject) cClassAObject;
		assertEquals(classAEClass, classAFEO.eClass());
		assertNotEquals(findAEO, classAFEO);
		assertEquals("The A-Class!", classAFEO.eGet(aNameFeature));
		cContainmentObject = classAFEO.eGet(aCContainmentFeature);
		assertNotNull(cContainmentObject);
		assertInstanceOf(List.class, cContainmentObject);
		cContainmentResultList = (List<?>) cContainmentObject;
		assertEquals(3, cContainmentResultList.size());
		assertTrue(cContainmentResultList.contains(findCEO));
	}

	/**
	 * Test uni-directional one-to-many non-containment relations 
	 */
	@Test
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testOneToManyNonContainmentUni(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 5000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		assertFalse(modelPackageAware.isEmpty());
		assertFalse(dataSourceAware.isEmpty());
		assertFalse(emfAware.isEmpty());
		
		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);
		
		ClassDescriptor classADescriptor = server.getDescriptorForAlias(classAEClass.getName());
		assertNotNull(classADescriptor);
		ClassDescriptor classDDescriptor = server.getDescriptorForAlias(classDEClass.getName());
		assertNotNull(classDDescriptor);
		
		EObject classA01EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classA02EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classD01EO = (EObject) classDDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classD02EO = (EObject) classDDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classD03EO = (EObject) classDDescriptor.getInstantiationPolicy().buildNewInstance();
		//
		
		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		assertNotNull(aNameFeature);
		classA01EO.eSet(aNameFeature, "The First A-Class!");
		classA02EO.eSet(aNameFeature, "The Second A-Class!");
		
		EStructuralFeature dNonContainmentFeature = classAEClass.getEStructuralFeature("dNonContainment");
		assertNotNull(dNonContainmentFeature);
		
		EStructuralFeature dNameFeature = classDEClass.getEStructuralFeature("name");
		assertNotNull(dNameFeature);
		classD01EO.eSet(dNameFeature, "The First D-Class!");
		classD02EO.eSet(dNameFeature, "The Second D-Class!");
		classD03EO.eSet(dNameFeature, "The Third D-Class!");
		
		EStructuralFeature dClassAFeature = classDEClass.getEStructuralFeature("dClassA");
		assertNotNull(dClassAFeature);
		
		List<EObject> dNonContainmentList = List.of(classD01EO, classD02EO, classD03EO); 
		classA01EO.eSet(dNonContainmentFeature, dNonContainmentList);
		
		classD01EO.eSet(dClassAFeature, classA01EO);
		classD02EO.eSet(dClassAFeature, classA01EO);
		classD03EO.eSet(dClassAFeature, classA02EO);
		
		
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classD01EO);
			em.persist(classD02EO);
			em.persist(classD03EO);
			em.persist(classA01EO);
			em.persist(classA02EO);
			em.getTransaction().commit();
			em.clear();
		} catch (Exception e) {
			fail("Fail test One-to-Many mapping persist", e);
		};
		
		EObject findAEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classA01EO);
			assertNotNull(id);
			findAEO = em.find(classADescriptor.getJavaClass(), Integer.valueOf(id));
			
		} catch (Exception e) {
			fail("Fail test One-to-Many mapping find", e);
		}
		
		assertNotNull(findAEO);
		assertNotEquals(classA01EO, findAEO);
		Object aNameObject = findAEO.eGet(aNameFeature);
		assertNotNull(aNameObject);
		assertEquals("The First A-Class!", aNameObject);
		
		Object bContainmentObject = findAEO.eGet(dNonContainmentFeature);
		assertNotNull(bContainmentObject);
		assertInstanceOf(List.class, bContainmentObject);
		List<?> bContainmentResultList = (List<?>) bContainmentObject;
		assertEquals(3, bContainmentResultList.size());
		final Set<String> dValues = new HashSet<>(Set.of("The First D-Class!", "The Second D-Class!", "The Third D-Class!"));
		final Set<String> aValues = new HashSet<>(Set.of("The First A-Class!-0", "The First A-Class!-1", "The Second A-Class!"));
		int aCount = 0;
		for (Object o : bContainmentResultList) {
			assertInstanceOf(EObject.class, o);
			EObject eo = (EObject) o;
			assertEquals(classDEClass, eo.eClass());
			Object dNameObject = eo.eGet(dNameFeature);
			assertNotNull(dNameObject);
			assertTrue(dValues.remove(dNameObject));
			assertFalse(dNonContainmentList.contains(eo));
			Object dAClassObject = eo.eGet(dClassAFeature);
			assertNotNull(dAClassObject);
			assertInstanceOf(EObject.class, dAClassObject);
			EObject aeo = (EObject) dAClassObject;
			Object aeoNameObject = aeo.eGet(aNameFeature);
			if ("The First A-Class!".equals(aeoNameObject)) {
				assertTrue(aValues.remove(aeoNameObject.toString() + "-" + aCount));
				aCount++;
			} else {
				assertTrue(aValues.remove(aeoNameObject));
			}
		}
		assertTrue(dValues.isEmpty());
		assertTrue(aValues.isEmpty());
		
	}
	
	/**
	 * Test bi-directional non-containment relation without an opposite defined
	 */
	@Test
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testOneToManyNonContainmentBidiNoEOpposite(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 5000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		assertFalse(modelPackageAware.isEmpty());
		assertFalse(dataSourceAware.isEmpty());
		assertFalse(emfAware.isEmpty());
		
		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);
		
		ClassDescriptor classADescriptor = server.getDescriptorForAlias(classAEClass.getName());
		assertNotNull(classADescriptor);
		ClassDescriptor classDDescriptor = server.getDescriptorForAlias(classDEClass.getName());
		assertNotNull(classDDescriptor);
		
		EObject classA01EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classA02EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classD01EO = (EObject) classDDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classD02EO = (EObject) classDDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classD03EO = (EObject) classDDescriptor.getInstantiationPolicy().buildNewInstance();
		//
		
		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		assertNotNull(aNameFeature);
		classA01EO.eSet(aNameFeature, "The First A-Class!");
		classA02EO.eSet(aNameFeature, "The Second A-Class!");
		
		EStructuralFeature aDNonContainmentFeature = classAEClass.getEStructuralFeature("dNonContainment");
		assertNotNull(aDNonContainmentFeature);
		
		EStructuralFeature dNameFeature = classDEClass.getEStructuralFeature("name");
		assertNotNull(dNameFeature);
		classD01EO.eSet(dNameFeature, "The First D-Class!");
		classD02EO.eSet(dNameFeature, "The Second D-Class!");
		classD03EO.eSet(dNameFeature, "The Third D-Class!");
		
		EStructuralFeature dClassAFeature = classDEClass.getEStructuralFeature("dClassA");
		assertNotNull(dClassAFeature);
		
		List<EObject> dNonContainmentList = List.of(classD01EO, classD02EO, classD03EO); 
		classA01EO.eSet(aDNonContainmentFeature, dNonContainmentList);
		
		classD01EO.eSet(dClassAFeature, classA01EO);
		classD02EO.eSet(dClassAFeature, classA01EO);
		classD03EO.eSet(dClassAFeature, classA01EO);
		
		
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classD01EO);
			em.persist(classD02EO);
			em.persist(classD03EO);
			em.persist(classA01EO);
			em.persist(classA02EO);
			em.getTransaction().commit();
			em.clear();
		} catch (Exception e) {
			fail("Fail test One-to-Many mapping persist", e);
		};
		
		EObject findAEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classA01EO);
			assertNotNull(id);
			findAEO = em.find(classADescriptor.getJavaClass(), Integer.valueOf(id));
			
		} catch (Exception e) {
			fail("Fail test One-to-Many mapping find", e);
		}
		
		assertNotNull(findAEO);
		assertNotEquals(classA01EO, findAEO);
		Object aNameObject = findAEO.eGet(aNameFeature);
		assertNotNull(aNameObject);
		assertEquals("The First A-Class!", aNameObject);
		
		Object dContainmentObject = findAEO.eGet(aDNonContainmentFeature);
		assertNotNull(dContainmentObject);
		assertInstanceOf(List.class, dContainmentObject);
		List<?> bContainmentResultList = (List<?>) dContainmentObject;
		assertEquals(3, bContainmentResultList.size());
		final Set<String> dValues = new HashSet<>(Set.of("The First D-Class!", "The Second D-Class!", "The Third D-Class!"));
		for (Object o : bContainmentResultList) {
			assertInstanceOf(EObject.class, o);
			EObject eo = (EObject) o;
			assertEquals(classDEClass, eo.eClass());
			Object dNameObject = eo.eGet(dNameFeature);
			assertNotNull(dNameObject);
			assertTrue(dValues.remove(dNameObject));
			assertFalse(dNonContainmentList.contains(eo));
			Object dAClassObject = eo.eGet(dClassAFeature);
			assertNotNull(dAClassObject);
			assertInstanceOf(EObject.class, dAClassObject);
			EObject aeo = (EObject) dAClassObject;
			Object aeoNameObject = aeo.eGet(aNameFeature);
			assertEquals("The First A-Class!",aeoNameObject);
		}
		assertTrue(dValues.isEmpty());
		
		EObject findDEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classD03EO);
			assertNotNull(id);
			findDEO = em.find(classDDescriptor.getJavaClass(), Integer.valueOf(id));
			
		} catch (Exception e) {
			fail("Fail test One-to-Many mapping find", e);
		}
		
		assertNotNull(findDEO);
		assertNotEquals(classD03EO, findDEO);
		Object dNameObject = findDEO.eGet(dNameFeature);
		assertNotNull(dNameObject);
		assertEquals("The Third D-Class!", dNameObject);
		
		Object dAClassObject = findDEO.eGet(dClassAFeature);
		assertNotNull(dAClassObject);
		assertInstanceOf(EObject.class, dAClassObject);
		EObject aeo = (EObject) dAClassObject;
		Object aeoNameObject = aeo.eGet(aNameFeature);
		assertNotNull(aeoNameObject);
		assertEquals("The First A-Class!", aeoNameObject);
		
		dContainmentObject = aeo.eGet(aDNonContainmentFeature);
		assertNotNull(dContainmentObject);
		assertInstanceOf(List.class, dContainmentObject);
		bContainmentResultList = (List<?>) dContainmentObject;
		assertEquals(3, bContainmentResultList.size());
		
		final Set<String> values = new HashSet<>(Set.of("The First D-Class!", "The Second D-Class!", "The Third D-Class!"));
		bContainmentResultList.forEach(o->{
			assertInstanceOf(EObject.class, o);
			EObject eo = (EObject) o;
			assertEquals(classDEClass, eo.eClass());
			assertNotNull(eo.eGet(dNameFeature));
			assertTrue(values.remove(eo.eGet(dNameFeature)));
		});
		assertTrue(values.isEmpty());
	}

	/**
	 * Test bi-directional non-containment relation with opposite defined
	 */
	@Test
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testOneToManyNonContainmentBidiEOpposite(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 5000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		assertFalse(modelPackageAware.isEmpty());
		assertFalse(dataSourceAware.isEmpty());
		assertFalse(emfAware.isEmpty());
		
		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);
		
		ClassDescriptor classADescriptor = server.getDescriptorForAlias(classAEClass.getName());
		assertNotNull(classADescriptor);
		ClassDescriptor classEDescriptor = server.getDescriptorForAlias(classEEClass.getName());
		assertNotNull(classEDescriptor);
		
		EObject classAEO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classE01EO = (EObject) classEDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classE02EO = (EObject) classEDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classE03EO = (EObject) classEDescriptor.getInstantiationPolicy().buildNewInstance();
		//
		
		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		assertNotNull(aNameFeature);
		classAEO.eSet(aNameFeature, "The A-Class!");
		
		EStructuralFeature aEContainmentFeature = classAEClass.getEStructuralFeature("eNonContainmentBidi");
		assertNotNull(aEContainmentFeature);
	
		EStructuralFeature eNameFeature = classEEClass.getEStructuralFeature("name");
		assertNotNull(eNameFeature);
		classE01EO.eSet(eNameFeature, "The First E-Class!");
		classE02EO.eSet(eNameFeature, "The Second E-Class!");
		classE03EO.eSet(eNameFeature, "The Third E-Class!");
		
		EStructuralFeature eClassAFeature = classEEClass.getEStructuralFeature("eClassA");
		assertNotNull(eClassAFeature);
		
		List<EObject> eContainmentList = List.of(classE01EO, classE02EO, classE03EO); 
		classAEO.eSet(aEContainmentFeature, eContainmentList);
		
		
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classE01EO);
			em.persist(classE02EO);
			em.persist(classE03EO);
			em.persist(classAEO);
			em.getTransaction().commit();
			em.clear();
			
		} catch (Exception e) {
			fail("Fail test One-to-Many mapping persist", e);
		};
		
		EObject findAEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classAEO);
			assertNotNull(id);
			findAEO = em.find(classADescriptor.getJavaClass(), Integer.valueOf(id));
			
		} catch (Exception e) {
			fail("Fail test One-to-Many mapping find", e);
		}
		
		assertNotNull(findAEO);
		assertNotEquals(classAEO, findAEO);
		Object aNameObject = findAEO.eGet(aNameFeature);
		assertNotNull(aNameObject);
		assertEquals("The A-Class!", aNameObject);
		
		Object eContainmentObject = findAEO.eGet(aEContainmentFeature);
		assertNotNull(eContainmentObject);
		assertInstanceOf(List.class, eContainmentObject);
		List<?> eContainmentResultList = (List<?>) eContainmentObject;
		assertEquals(3, eContainmentResultList.size());
		final Set<String> values = new HashSet<>(Set.of("The First E-Class!", "The Second E-Class!", "The Third E-Class!"));
		for (Object o : eContainmentResultList) {
			assertInstanceOf(EObject.class, o);
			EObject eo = (EObject) o;
			assertEquals(classEEClass, eo.eClass());
			Object eNameObject = eo.eGet(eNameFeature);
			assertNotNull(eNameObject);
			assertTrue(values.remove(eNameObject));
			assertFalse(eContainmentList.contains(eo));
			Object eClassAObject = eo.eGet(eClassAFeature);
			assertNotNull(eClassAObject);
			EObject classAFEO = (EObject) eClassAObject;
			assertEquals(classAEClass, classAFEO.eClass());
			assertEquals(findAEO, classAFEO);
		}
		assertTrue(values.isEmpty());
		
		EObject findEEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classE02EO);
			assertNotNull(id);
			findEEO = em.find(classEDescriptor.getJavaClass(), Integer.valueOf(id));
			
		} catch (Exception e) {
			fail("Fail test One-to-Many mapping find", e);
		}
		
		assertNotNull(findEEO);
		assertEquals(classEEClass, findEEO.eClass());
		assertNotEquals(classE02EO, findEEO);
		Object eNameObject = findEEO.eGet(eNameFeature);
		assertNotNull(eNameObject);
		assertEquals("The Second E-Class!", eNameObject);
		
		Object eClassAObject = findEEO.eGet(eClassAFeature);
		assertNotNull(eClassAObject);
		EObject classAFEO = (EObject) eClassAObject;
		assertEquals(classAEClass, classAFEO.eClass());
		assertNotEquals(findAEO, classAFEO);
		assertEquals("The A-Class!", classAFEO.eGet(aNameFeature));
		eContainmentObject = classAFEO.eGet(aEContainmentFeature);
		assertNotNull(eContainmentObject);
		assertInstanceOf(List.class, eContainmentObject);
		eContainmentResultList = (List<?>) eContainmentObject;
		assertEquals(3, eContainmentResultList.size());
		assertTrue(eContainmentResultList.contains(findEEO));
	}

}
