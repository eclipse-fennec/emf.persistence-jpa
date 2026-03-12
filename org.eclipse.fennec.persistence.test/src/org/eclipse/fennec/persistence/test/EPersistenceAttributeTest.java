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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
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
public class EPersistenceAttributeTest extends EPersistenceBase{

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.test.EPersistenceBase#setupMappings(org.eclipse.fennec.persistence.orm.helper.EntityMapper)
	 */
	@Override
	protected EntityMappings setupMappings(EntityMapper mapper, EPackage ePackage) {
		EClass personEClass = (EClass) ePackage.getEClassifier("Person");
		assertNotNull(personEClass);
		EClass classOneEClass = (EClass) ePackage.getEClassifier("ClassOne");
		assertNotNull(classOneEClass);
		EntityMappings mapping = mapper.createMappings(List.of(personEClass, classOneEClass));
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
		
		assertNotNull(emfAware.waitForService(7000l));
		
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
//		List<EObject> classOneList = List.of(classOne01EO, classOne02EO, classOne03EO); 
//		personEO.eSet(classOnes, classOneList);
		
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
//		
//		Object classOneListObject = findEO.eGet(classOnes);
//		assertNotNull(classOneListObject);
//		assertInstanceOf(List.class, classOneListObject);
//		List<?> classOneResultList = (List<?>) classOneListObject;
//		assertEquals(3, classOneResultList.size());
//		final Set<String> values = new HashSet<>(Set.of("The First!", "The Second!", "The Third!"));
//		classOneResultList.forEach(o->{
//			assertInstanceOf(EObject.class, o);
//			EObject eo = (EObject) o;
//			assertEquals(classOneEClass, eo.eClass());
//			Object attributeOneObject = eo.eGet(attributeOneFeature);
//			assertNotNull(attributeOneObject);
//			assertTrue(values.remove(attributeOneObject));
//			assertFalse(classOneList.contains(eo));
//		});
//		assertTrue(values.isEmpty());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testManyAttributes(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 5000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		assertFalse(modelPackageAware.isEmpty());
		assertFalse(dataSourceAware.isEmpty());
		assertFalse(emfAware.isEmpty());

		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);
		//
		EPackage modelPackage = modelPackageAware.getService();
		EClass personEClass = (EClass) modelPackage.getEClassifier("Person");
		assertNotNull(personEClass);
		ClassDescriptor descriptor = server.getDescriptorForAlias(personEClass.getName());
		assertNotNull(descriptor);

		EObject eObject = (EObject) descriptor.getInstantiationPolicy().buildNewInstance();
		//		EObject eObject = (EObject) modelPackage.getEFactoryInstance().create(personEClass);
		//
		EStructuralFeature stringTagsRequiredFeature = eObject.eClass().getEStructuralFeature("stringTagsRequired");
		assertNotNull(stringTagsRequiredFeature);
		List<String> tags = List.of("tag1", "tag-heuer");
		eObject.eSet(stringTagsRequiredFeature, ECollections.asEList(tags));
		EStructuralFeature intListOptionalFeature = eObject.eClass().getEStructuralFeature("intListOptional");
		assertNotNull(intListOptionalFeature);
		List<Integer> ints = List.of(42, 24, 2442);
		eObject.eSet(intListOptionalFeature, ECollections.asEList(ints));

		EObject findEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(eObject);
			em.flush();
			em.getTransaction().commit();
			em.clear();

			String id = EcoreUtil.getID(eObject);
			assertNotNull(id);
			findEO = em.find(descriptor.getJavaClass(), id);
		} catch (Exception e) {
			fail("Fail test Enum and simple attributes", e);
		};

		assertNotNull(findEO);
		assertNotEquals(eObject, findEO);
		Object tagListObject = findEO.eGet(stringTagsRequiredFeature);
		assertThat(tagListObject).
			isNotNull().
			isInstanceOf(List.class);
		List<String> tagList = (List<String>) tagListObject;
		assertThat(tagList).
			isNotEmpty().
			hasSize(2).
			contains("tag1", "tag-heuer");
		Object intListObject = findEO.eGet(intListOptionalFeature);
		assertThat(intListObject).
			isNotNull().
			isInstanceOf(List.class);
		List<Integer> intList = (List<Integer>) intListObject;
		assertThat(intList).
			isNotEmpty().
			hasSize(3).
			contains(24, 42, 2442);
	}

	@Test
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testSimpleAttributesAndEnum(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 5000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		assertFalse(modelPackageAware.isEmpty());
		assertFalse(dataSourceAware.isEmpty());
		assertFalse(emfAware.isEmpty());

		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);
		//
		EPackage modelPackage = modelPackageAware.getService();
		//			EObject eObject = (EObject) descriptor.getInstantiationPolicy().buildNewInstance();
		EClass personEClass = (EClass) modelPackage.getEClassifier("Person");
		assertNotNull(personEClass);
		ClassDescriptor descriptor = server.getDescriptorForAlias(personEClass.getName());
		assertNotNull(descriptor);
		EObject eObject = (EObject) descriptor.getInstantiationPolicy().buildNewInstance();
		//		EObject eObject = (EObject) modelPackage.getEFactoryInstance().create(personEClass);
		//
		EStructuralFeature stringDefaultFeature = eObject.eClass().getEStructuralFeature("stringDefault");
		assertNotNull(stringDefaultFeature);
		EStructuralFeature stringTagsRequiredFeature = eObject.eClass().getEStructuralFeature("stringTagsRequired");
		assertNotNull(stringTagsRequiredFeature);
		EStructuralFeature birthDateFeature = eObject.eClass().getEStructuralFeature("birthDate");
		assertNotNull(birthDateFeature);
		long timestamp = System.currentTimeMillis(); 
		eObject.eSet(birthDateFeature, new Date(timestamp));
		EStructuralFeature cmDatatypeWithClassFeature = eObject.eClass().getEStructuralFeature("cmDatatypeWithClass");
		assertNotNull(cmDatatypeWithClassFeature);
		EStructuralFeature typeFeature = eObject.eClass().getEStructuralFeature("type");
		assertNotNull(typeFeature);
		EEnum personTypeEnum = (EEnum) modelPackage.getEClassifier("PersonType");
		assertNotNull(personTypeEnum);
		EEnumLiteral businessLiteral = personTypeEnum.getEEnumLiteral(1);
		assertNotNull(businessLiteral);
		eObject.eSet(typeFeature, businessLiteral);
		EStructuralFeature localDateFeature = eObject.eClass().getEStructuralFeature("localDateNoTypeConverter");
		assertNotNull(localDateFeature);
		LocalDate localDateNow = LocalDate.now();
		eObject.eSet(localDateFeature, localDateNow);
		EStructuralFeature dataFeature = eObject.eClass().getEStructuralFeature("data");
		assertNotNull(dataFeature);
		byte[] hwa = "Hello World".getBytes();
		eObject.eSet(dataFeature, hwa);


		List<String> tags = List.of("tag1", "tag-heuer");
		eObject.eSet(stringTagsRequiredFeature, ECollections.asEList(tags));

		EObject findEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(eObject);
			em.flush();
			em.getTransaction().commit();
			em.clear();

			String id = EcoreUtil.getID(eObject);
			assertNotNull(id);
			findEO = em.find(descriptor.getJavaClass(), id);
		} catch (Exception e) {
			fail("Fail test Enum and simple attributes", e);
		};
		assertNotNull(findEO);
		assertNotEquals(eObject, findEO);
		assertEquals(stringDefaultFeature.getDefaultValueLiteral(), findEO.eGet(stringDefaultFeature));
		Object birthDateObject = findEO.eGet(birthDateFeature);
		assertNotNull(birthDateObject);
		assertInstanceOf(Date.class, birthDateObject);
		assertEquals(timestamp, ((Date) birthDateObject).getTime());
		assertEquals(3, findEO.eGet(cmDatatypeWithClassFeature));
		assertEquals(businessLiteral, findEO.eGet(typeFeature));
		assertEquals(localDateNow, findEO.eGet(localDateFeature));
		assertArrayEquals(hwa, (byte[])findEO.eGet(dataFeature));
	}

}
