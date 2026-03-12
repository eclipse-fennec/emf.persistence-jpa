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

import org.eclipse.emf.ecore.EClass;
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
public class EPersistenceManyToManyNoCacheTest extends EPersistenceManyToManyTest {
	

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
		
		assertNotNull(emfAware.waitForService(5000l));
		
		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);
		//
		EPackage modelPackage = modelPackageAware.getService();
		
		EClass personEClass = (EClass) modelPackage.getEClassifier("Person");
		EClass classOneEClass = (EClass) modelPackage.getEClassifier("ClassOne");
		assertNotNull(personEClass);
		assertNotNull(classOneEClass);
		
		ClassDescriptor personDescriptor = server.getDescriptorForAlias(personEClass.getName());
		assertNotNull(personDescriptor);
		ClassDescriptor classOneDescriptor = server.getDescriptorForAlias(classOneEClass.getName());
		assertNotNull(classOneDescriptor);
		
		EObject personEO = (EObject) personDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classOne01EO = (EObject) classOneDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classOne02EO = (EObject) classOneDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classOne03EO = (EObject) classOneDescriptor.getInstantiationPolicy().buildNewInstance();
		//
		
		EStructuralFeature attributeOneFeature = classOneEClass.getEStructuralFeature("attributeOne");
		classOne01EO.eSet(attributeOneFeature, "The First!");
		classOne02EO.eSet(attributeOneFeature, "The Second!");
		classOne03EO.eSet(attributeOneFeature, "The Third!");
		
		EStructuralFeature classOnes = personEClass.getEStructuralFeature("classOnes");
		assertNotNull(classOnes);
		List<EObject> classOneList = List.of(classOne01EO, classOne02EO, classOne03EO); 
		personEO.eSet(classOnes, classOneList);
		
		EStructuralFeature stringDefaultFeature = personEClass.getEStructuralFeature("stringDefault");
		assertNotNull(stringDefaultFeature);
		personEO.eSet(stringDefaultFeature, "Hello World");
		
		EObject findEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classOne01EO);
			em.persist(classOne02EO);
			em.persist(classOne03EO);
			em.persist(personEO);
			em.flush();
			em.getTransaction().commit();
			em.clear();
			
		} catch (Exception e) {
			fail("Fail test One-to-Many mapping persist", e);
		};
		
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(personEO);
			assertNotNull(id);
			findEO = em.find(personDescriptor.getJavaClass(), id);
			
		} catch (Exception e) {
			fail("Fail test One-to-Many mapping find", e);
		}
		
		assertNotNull(findEO);
		assertNotEquals(personEO, findEO);
		Object stringDefaultObject = findEO.eGet(stringDefaultFeature);
		assertNotNull(stringDefaultObject);
		assertEquals("Hello World", stringDefaultObject);
		
		Object classOneListObject = findEO.eGet(classOnes);
		assertNotNull(classOneListObject);
		assertInstanceOf(List.class, classOneListObject);
		List<?> classOneResultList = (List<?>) classOneListObject;
		assertEquals(3, classOneResultList.size());
		final Set<String> values = new HashSet<>(Set.of("The First!", "The Second!", "The Third!"));
		classOneResultList.forEach(o->{
			assertInstanceOf(EObject.class, o);
			EObject eo = (EObject) o;
			assertEquals(classOneEClass, eo.eClass());
			Object attributeOneObject = eo.eGet(attributeOneFeature);
			assertNotNull(attributeOneObject);
			assertTrue(values.remove(attributeOneObject));
			assertFalse(classOneList.contains(eo));
		});
		assertTrue(values.isEmpty());
	}
	
	@Test
	@TestAnnotations.NoCacheEPersistenceConfiguration
	public void testManyToManyUni(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 7000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		super.testManyToManyUni(dataSourceAware, modelPackageAware, emfAware);
	}
	
	@Test
	@TestAnnotations.NoCacheEPersistenceConfiguration
	public void testManyToManyNoEOpposite(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 7000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		super.testManyToManyNoEOpposite(dataSourceAware, modelPackageAware, emfAware);
	}
	
	@Test
	@TestAnnotations.NoCacheEPersistenceConfiguration
	public void testManyToManyEOpposite(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 7000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		super.testManyToManyEOpposite(dataSourceAware, modelPackageAware, emfAware);
	}

}
