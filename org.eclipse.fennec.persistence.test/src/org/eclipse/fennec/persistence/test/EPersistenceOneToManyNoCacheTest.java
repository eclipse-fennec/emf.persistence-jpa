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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class EPersistenceOneToManyNoCacheTest extends EPersistenceOneToManyTest{

//	@Test
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
		assertNotNull(emfAware.waitForService(7000l));
		
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
//		classA01EO.eSet(dNonContainmentFeature, dNonContainmentList);
		
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
	 * Test uni-directional one-to-many containment relations
	 */
	@Test
	@TestAnnotations.NoCacheEPersistenceConfiguration
	public void testOneToManyContainmentUni(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 5000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		super.testOneToManyContainmentUni(dataSourceAware, modelPackageAware, emfAware);
	}
	
	/**
	 * Test bi-directional one-to-many containment relations, iwth many-to-one on the other side
	 */
	@Test
	@TestAnnotations.NoCacheEPersistenceConfiguration
	public void testOneToManyContainmentBidi(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 5000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		super.testOneToManyContainmentBidi(dataSourceAware, modelPackageAware, emfAware);
	}

	/**
	 * Test uni-directional one-to-many non-containment relations 
	 */
	@Test
	@TestAnnotations.NoCacheEPersistenceConfiguration
	public void testOneToManyNonContainmentUni(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 5000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		super.testOneToManyNonContainmentUni(dataSourceAware, modelPackageAware, emfAware);
	}
	
	/**
	 * Test bi-directional non-containment relation without an opposite defined
	 */
	@Test
	@TestAnnotations.NoCacheEPersistenceConfiguration
	public void testOneToManyNonContainmentBidiNoEOpposite(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 5000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		super.testOneToManyNonContainmentBidiNoEOpposite(dataSourceAware, modelPackageAware, emfAware);
	}

	/**
	 * Test bi-directional non-containment relation with opposite defined
	 */
	@Test
	@TestAnnotations.NoCacheEPersistenceConfiguration
	public void testOneToManyNonContainmentBidiEOpposite(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 5000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		super.testOneToManyNonContainmentBidiEOpposite(dataSourceAware, modelPackageAware, emfAware);
	}

}
