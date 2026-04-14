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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class EPersistenceManyToManyTest extends EPersistenceBase {
	
	protected EClass classAEClass;
	protected EClass classBEClass;
	protected EClass classCEClass;
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.test.EPersistenceBase#setupMappings(org.eclipse.fennec.persistence.orm.helper.EntityMapper, org.eclipse.emf.ecore.EPackage)
	 */
	@Override
	protected EntityMappings setupMappings(EntityMapper mapper, EPackage ePackage) {
		classAEClass = (EClass) ePackage.getEClassifier("ClassAM2M");
		assertNotNull(classAEClass);
		classBEClass = (EClass) ePackage.getEClassifier("ClassBM2M");
		assertNotNull(classBEClass);
		classCEClass = (EClass) ePackage.getEClassifier("ClassCM2M");
		assertNotNull(classCEClass);
		EntityMappings mapping = mapper.createMappings(List.of(classAEClass, classBEClass, classCEClass));
		return mapping;
	}

	@Test
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testManyToManyUni(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 7000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		assertFalse(modelPackageAware.isEmpty());
		assertFalse(dataSourceAware.isEmpty());
		assertFalse(emfAware.isEmpty());
		
		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);
		
		ClassDescriptor classADescriptor = server.getDescriptorForAlias(classAEClass.getName());
		assertNotNull(classADescriptor);
		ClassDescriptor classBDescriptor = server.getDescriptorForAlias(classBEClass.getName());
		assertNotNull(classBDescriptor);
		
		EObject classA01EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classA02EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classA03EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classB01EO = (EObject) classBDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classB02EO = (EObject) classBDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classB03EO = (EObject) classBDescriptor.getInstantiationPolicy().buildNewInstance();
		//
		
		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		assertNotNull(aNameFeature);
		EStructuralFeature aBNonContainmentFeature = classAEClass.getEStructuralFeature("bNonContainment");
		assertNotNull(aBNonContainmentFeature);
		
		classA01EO.eSet(aNameFeature, "Emil Tester");
		classA02EO.eSet(aNameFeature, "Emilia Testerova");
		classA03EO.eSet(aNameFeature, "Amalia Toaster");

		EStructuralFeature bNameFeature = classBEClass.getEStructuralFeature("name");
		assertNotNull(bNameFeature);
		EStructuralFeature bClassAFeature = classBEClass.getEStructuralFeature("bClassA");
		assertNotNull(bClassAFeature);
		
		classB01EO.eSet(bNameFeature, "First B");
		classB02EO.eSet(bNameFeature, "Second B");
		classB03EO.eSet(bNameFeature, "Third B");
		
		classA01EO.eSet(aBNonContainmentFeature, List.of(classB01EO, classB02EO));
		classA02EO.eSet(aBNonContainmentFeature, List.of(classB01EO, classB03EO));
		classA03EO.eSet(aBNonContainmentFeature, List.of(classB03EO));
		
		Object b01AListObject = classB01EO.eGet(bClassAFeature);
		assertNotNull(b01AListObject);
		assertInstanceOf(Collection.class, b01AListObject);
		assertTrue(((Collection<?>)b01AListObject).isEmpty());
//		assertEquals(2, ((Collection<?>)b01AListObject).size());
//		assertTrue(((Collection<?>)b01AListObject).contains(classA01EO));
//		assertTrue(((Collection<?>)b01AListObject).contains(classA02EO));
		
		Object b02AListObject = classB02EO.eGet(bClassAFeature);
		assertNotNull(b02AListObject);
		assertInstanceOf(Collection.class, b02AListObject);
		assertTrue(((Collection<?>)b02AListObject).isEmpty());
//		assertEquals(1, ((Collection<?>)b02AListObject).size());
		Object b03AListObject = classB03EO.eGet(bClassAFeature);
		assertNotNull(b03AListObject);
		assertInstanceOf(Collection.class, b03AListObject);
		assertTrue(((Collection<?>)b03AListObject).isEmpty());
//		assertEquals(2, ((Collection<?>)b03AListObject).size());
		
		
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classB01EO);
			em.persist(classB02EO);
			em.persist(classB03EO);
			em.persist(classA01EO);
			em.persist(classA02EO);
			em.persist(classA03EO);
			em.getTransaction().commit();
			em.clear();
			
		} catch (Exception e) {
			fail("Fail test Many-to-Many mapping persist", e);
		};
		
		EObject findAEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classA01EO);
			assertNotNull(id);
			findAEO = em.find(classADescriptor.getJavaClass(), Integer.valueOf(id));
			
		} catch (Exception e) {
			fail("Fail test Many-to-Many mapping find", e);
		}
		
		assertNotNull(findAEO);
		assertNotEquals(classA01EO, findAEO);
		Object aNameObject = findAEO.eGet(aNameFeature);
		assertNotNull(aNameObject);
		assertEquals("Emil Tester", aNameObject);
		
		Object bListObject = findAEO.eGet(aBNonContainmentFeature);
		assertNotNull(bListObject);
		assertInstanceOf(List.class, bListObject);
		List<?> bResultList = (List<?>) bListObject;
		assertEquals(2, bResultList.size());
		final Set<String> values = new HashSet<>(Set.of("First B", "Second B", "Third B"));
		for (Object o : bResultList) {
			assertInstanceOf(EObject.class, o);
			EObject beo = (EObject) o;
			assertEquals(classBEClass, beo.eClass());
			Object bNameObject = beo.eGet(bNameFeature);
			assertNotNull(bNameObject);
			assertTrue(values.remove(bNameObject));
//			if ("First Street".equals(bNameObject)) {
//				Object addressPersonsObject = beo.eGet(aBNonContainmentFeature);
//				assertNotNull(addressPersonsObject);
//				assertInstanceOf(List.class, addressPersonsObject);
//				List<?> addressPersonsResultList = (List<?>) addressPersonsObject;
//				assertEquals(2, addressPersonsResultList.size());
//				assertTrue(addressPersonsResultList.contains(findAEO));
////				Set<String> personNames = new HashSet<>(Set.of("Emil Tester", "Emilia Testerova"));
//				for (Object po : addressPersonsResultList) {
//					assertInstanceOf(EObject.class, po);
//					EObject peo = (EObject) po;
//					assertEquals(citizenEClass, peo.eClass());
//					Object personNameObject = peo.eGet(citizenNameFeature);
//					assertNotNull(personNameObject);
////					assertTrue(personNames.remove(personNameObject));
//				}
////				assertTrue(personNames.isEmpty());
//			}
		};
		assertEquals(1, values.size());
		assertTrue(values.contains("Third B"));
		
		EObject findBEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classB03EO);
			assertNotNull(id);
			findBEO = em.find(classBDescriptor.getJavaClass(), Integer.valueOf(id));
			
		} catch (Exception e) {
			fail("Fail test Many-to-Many mapping find", e);
		}
		
		assertNotNull(findBEO);
		assertNotEquals(classB03EO, findBEO);
		Object bNameObject = findBEO.eGet(bNameFeature);
		assertNotNull(bNameObject);
		assertEquals("Third B", bNameObject);
		
		Object aListObject = findBEO.eGet(bClassAFeature);
		assertNotNull(aListObject);
		assertInstanceOf(List.class, aListObject);
		List<?> aResultList = (List<?>) aListObject;
		// we have not set any A in one of the B
		assertTrue(aResultList.isEmpty());
	}
	
	@Test
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testManyToManyNoEOpposite(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 7000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		assertFalse(modelPackageAware.isEmpty());
		assertFalse(dataSourceAware.isEmpty());
		assertFalse(emfAware.isEmpty());
		
		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);
		
		ClassDescriptor classADescriptor = server.getDescriptorForAlias(classAEClass.getName());
		assertNotNull(classADescriptor);
		ClassDescriptor classBDescriptor = server.getDescriptorForAlias(classBEClass.getName());
		assertNotNull(classBDescriptor);
		
		EObject classA01EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classA02EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classA03EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classB01EO = (EObject) classBDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classB02EO = (EObject) classBDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classB03EO = (EObject) classBDescriptor.getInstantiationPolicy().buildNewInstance();
		//
		
		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		assertNotNull(aNameFeature);
		EStructuralFeature aBNonContainmentFeature = classAEClass.getEStructuralFeature("bNonContainment");
		assertNotNull(aBNonContainmentFeature);
		
		classA01EO.eSet(aNameFeature, "Emil Tester");
		classA02EO.eSet(aNameFeature, "Emilia Testerova");
		classA03EO.eSet(aNameFeature, "Amalia Toaster");
		
		EStructuralFeature bNameFeature = classBEClass.getEStructuralFeature("name");
		assertNotNull(bNameFeature);
		EStructuralFeature bClassAFeature = classBEClass.getEStructuralFeature("bClassA");
		assertNotNull(bClassAFeature);
		
		classB01EO.eSet(bNameFeature, "First B");
		classB02EO.eSet(bNameFeature, "Second B");
		classB03EO.eSet(bNameFeature, "Third B");
		
		classA01EO.eSet(aBNonContainmentFeature, List.of(classB01EO, classB02EO));
		classA02EO.eSet(aBNonContainmentFeature, List.of(classB01EO, classB03EO));
		classA03EO.eSet(aBNonContainmentFeature, List.of(classB03EO));
		
		// now set the inverses for B03
		classB03EO.eSet(bClassAFeature, List.of(classA02EO, classA03EO));
		
		Object b01AListObject = classB01EO.eGet(bClassAFeature);
		assertNotNull(b01AListObject);
		assertInstanceOf(Collection.class, b01AListObject);
		assertTrue(((Collection<?>)b01AListObject).isEmpty());
		
		Object b02AListObject = classB02EO.eGet(bClassAFeature);
		assertNotNull(b02AListObject);
		assertInstanceOf(Collection.class, b02AListObject);
		assertTrue(((Collection<?>)b02AListObject).isEmpty());
		Object b03AListObject = classB03EO.eGet(bClassAFeature);
		assertNotNull(b03AListObject);
		assertInstanceOf(Collection.class, b03AListObject);
		assertEquals(2, ((Collection<?>)b03AListObject).size());
		
		
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classB01EO);
			em.persist(classB02EO);
			em.persist(classB03EO);
			em.persist(classA01EO);
			em.persist(classA02EO);
			em.persist(classA03EO);
			em.getTransaction().commit();
			em.clear();
			
		} catch (Exception e) {
			fail("Fail test Many-to-Many mapping persist", e);
		};
		
		EObject findAEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classA01EO);
			assertNotNull(id);
			findAEO = em.find(classADescriptor.getJavaClass(), Integer.valueOf(id));
			
		} catch (Exception e) {
			fail("Fail test Many-to-Many mapping find", e);
		}
		
		assertNotNull(findAEO);
		assertNotEquals(classA01EO, findAEO);
		Object aNameObject = findAEO.eGet(aNameFeature);
		assertNotNull(aNameObject);
		assertEquals("Emil Tester", aNameObject);
		
		Object bListObject = findAEO.eGet(aBNonContainmentFeature);
		assertNotNull(bListObject);
		assertInstanceOf(List.class, bListObject);
		List<?> bResultList = (List<?>) bListObject;
		assertEquals(2, bResultList.size());
		final Set<String> values = new HashSet<>(Set.of("First B", "Second B", "Third B"));
		for (Object o : bResultList) {
			assertInstanceOf(EObject.class, o);
			EObject beo = (EObject) o;
			assertEquals(classBEClass, beo.eClass());
			Object bNameObject = beo.eGet(bNameFeature);
			assertNotNull(bNameObject);
			assertTrue(values.remove(bNameObject));
		};
		assertEquals(1, values.size());
		assertTrue(values.contains("Third B"));
		
		EObject findBEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classB03EO);
			assertNotNull(id);
			findBEO = em.find(classBDescriptor.getJavaClass(), Integer.valueOf(id));
			
		} catch (Exception e) {
			fail("Fail test Many-to-Many mapping find", e);
		}
		
		assertNotNull(findBEO);
		assertNotEquals(classB03EO, findBEO);
		Object bNameObject = findBEO.eGet(bNameFeature);
		assertNotNull(bNameObject);
		assertEquals("Third B", bNameObject);
		
		Object aListObject = findBEO.eGet(bClassAFeature);
		assertNotNull(aListObject);
		assertInstanceOf(List.class, aListObject);
		List<?> aResultList = (List<?>) aListObject;
		assertEquals(2, aResultList.size());
		Set<String> aValues = new HashSet<>(Set.of("Emilia Testerova", "Amalia Toaster"));
		for (Object o : aResultList) {
			assertInstanceOf(EObject.class, o);
			EObject aeo = (EObject) o;
			assertEquals(classAEClass, aeo.eClass());
			aNameObject = aeo.eGet(aNameFeature);
			assertNotNull(aNameObject);
			assertTrue(aValues.remove(aNameObject));
		};
		assertTrue(aValues.isEmpty());
		
	}
	
	@Test
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testManyToManyEOpposite(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 7000) ServiceAware<EntityManagerFactory> emfAware) throws InterruptedException, IOException {
		assertFalse(modelPackageAware.isEmpty());
		assertFalse(dataSourceAware.isEmpty());
		assertFalse(emfAware.isEmpty());
		
		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);
		
		ClassDescriptor classADescriptor = server.getDescriptorForAlias(classAEClass.getName());
		assertNotNull(classADescriptor);
		ClassDescriptor classCDescriptor = server.getDescriptorForAlias(classCEClass.getName());
		assertNotNull(classCDescriptor);
		
		EObject classA01EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classA02EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classA03EO = (EObject) classADescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classC01EO = (EObject) classCDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classC02EO = (EObject) classCDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classC03EO = (EObject) classCDescriptor.getInstantiationPolicy().buildNewInstance();
		//
		
		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
		assertNotNull(aNameFeature);
		EStructuralFeature aCNonContainmentFeature = classAEClass.getEStructuralFeature("cNonContainmentBidi");
		assertNotNull(aCNonContainmentFeature);
		
		classA01EO.eSet(aNameFeature, "Emil Tester");
		classA02EO.eSet(aNameFeature, "Emilia Testerova");
		classA03EO.eSet(aNameFeature, "Amalia Toaster");
		
		EStructuralFeature cNameFeature = classCEClass.getEStructuralFeature("name");
		assertNotNull(cNameFeature);
		EStructuralFeature cClassAFeature = classCEClass.getEStructuralFeature("cClassA");
		assertNotNull(cClassAFeature);
		
		classC01EO.eSet(cNameFeature, "First C");
		classC02EO.eSet(cNameFeature, "Second C");
		classC03EO.eSet(cNameFeature, "Third C");
		
//		classA01EO.eSet(aCNonContainmentFeature, List.of(classC01EO));
//		classA02EO.eSet(aCNonContainmentFeature, List.of(classC01EO));
		classA01EO.eSet(aCNonContainmentFeature, List.of(classC01EO, classC02EO));
		classA02EO.eSet(aCNonContainmentFeature, List.of(classC01EO, classC03EO));
		classA03EO.eSet(aCNonContainmentFeature, List.of(classC03EO));
		
		Object c01AListObject = classC01EO.eGet(cClassAFeature);
		assertNotNull(c01AListObject);
		assertInstanceOf(Collection.class, c01AListObject);
		assertEquals(2, ((Collection<?>)c01AListObject).size());
		assertTrue(((Collection<?>)c01AListObject).contains(classA01EO));
		assertTrue(((Collection<?>)c01AListObject).contains(classA02EO));
		
		Object c02AListObject = classC02EO.eGet(cClassAFeature);
		assertNotNull(c02AListObject);
		assertInstanceOf(Collection.class, c02AListObject);
		assertEquals(1, ((Collection<?>)c02AListObject).size());
		assertTrue(((Collection<?>)c02AListObject).contains(classA01EO));
		
		Object c03AListObject = classC03EO.eGet(cClassAFeature);
		assertNotNull(c03AListObject);
		assertInstanceOf(Collection.class, c03AListObject);
		assertEquals(2, ((Collection<?>)c03AListObject).size());
		assertTrue(((Collection<?>)c03AListObject).contains(classA02EO));
		assertTrue(((Collection<?>)c03AListObject).contains(classA03EO));
		
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(classC01EO);
			em.persist(classC02EO);
			em.persist(classC03EO);
			em.persist(classA01EO);
			em.persist(classA02EO);
			em.persist(classA03EO);
			em.getTransaction().commit();
			em.clear();
			
		} catch (Exception e) {
			fail("Fail test Many-to-Many mapping persist", e);
		};
		
		EObject findAEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classA01EO);
			assertNotNull(id);
			findAEO = em.find(classADescriptor.getJavaClass(), Integer.valueOf(id));
			
		} catch (Exception e) {
			fail("Fail test Many-to-Many mapping find", e);
		}
		
		assertNotNull(findAEO);
		assertNotEquals(classA01EO, findAEO);
		Object aNameObject = findAEO.eGet(aNameFeature);
		assertNotNull(aNameObject);
		assertEquals("Emil Tester", aNameObject);
		
		Object cListObject = findAEO.eGet(aCNonContainmentFeature);
		assertNotNull(cListObject);
		assertInstanceOf(List.class, cListObject);
		List<?> cResultList = (List<?>) cListObject;
		assertEquals(2, cResultList.size());
		final Set<String> values = new HashSet<>(Set.of("First C", "Second C", "Third C"));
		for (Object o : cResultList) {
			assertInstanceOf(EObject.class, o);
			EObject ceo = (EObject) o;
			assertEquals(classCEClass, ceo.eClass());
			Object cNameObject = ceo.eGet(cNameFeature);
			assertNotNull(cNameObject);
			assertTrue(values.remove(cNameObject));
			Object cAClassListObject = ceo.eGet(cClassAFeature);
			assertNotNull(cAClassListObject);
			assertInstanceOf(List.class, cAClassListObject);
			List<?> cAClassResultList = (List<?>) cAClassListObject;
			assertTrue(cAClassResultList.contains(findAEO));
		};
		assertEquals(1, values.size());
		assertTrue(values.contains("Third C"));
		
		EObject findCEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(classC01EO);
			assertNotNull(id);
			findCEO = em.find(classCDescriptor.getJavaClass(), Integer.valueOf(id));
			
		} catch (Exception e) {
			fail("Fail test Many-to-Many mapping find", e);
		}
		
		assertNotNull(findCEO);
		assertNotEquals(classC01EO, findCEO);
		Object cNameObject = findCEO.eGet(cNameFeature);
		assertNotNull(cNameObject);
		assertEquals("First C", cNameObject);
		
		Object aListObject = findCEO.eGet(cClassAFeature);
		assertNotNull(aListObject);
		assertInstanceOf(List.class, aListObject);
		List<?> aResultList = (List<?>) aListObject;
		assertEquals(2, aResultList.size());
		Set<String> aValues = new HashSet<>(Set.of("Emilia Testerova", "Emil Tester"));
		for (Object o : aResultList) {
			assertInstanceOf(EObject.class, o);
			EObject aeo = (EObject) o;
			assertEquals(classAEClass, aeo.eClass());
			aNameObject = aeo.eGet(aNameFeature);
			assertNotNull(aNameObject);
			assertTrue(aValues.remove(aNameObject));
		};
		assertTrue(aValues.isEmpty());
		
	}
	

}
