///**
// * Copyright (c) 2012 - 2023 Data In Motion and others.
// * All rights reserved. 
// * 
// * This program and the accompanying materials are made
// * available under the terms of the Eclipse Public License 2.0
// * which is available at https://www.eclipse.org/legal/epl-2.0/
// *
// * SPDX-License-Identifier: EPL-2.0
// * 
// * Contributors:
// *     Data In Motion - initial API and implementation
// */
//package org.eclipse.fennec.persistence.test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertInstanceOf;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.junit.jupiter.api.Assertions.fail;
//
//import java.io.IOException;
//import java.util.Collection;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import javax.sql.DataSource;
//
//import org.eclipse.emf.ecore.EClass;
//import org.eclipse.emf.ecore.EObject;
//import org.eclipse.emf.ecore.EPackage;
//import org.eclipse.emf.ecore.EStructuralFeature;
//import org.eclipse.emf.ecore.util.EcoreUtil;
//import org.eclipse.fennec.persistence.eorm.EntityMappings;
//import org.eclipse.fennec.persistence.orm.EntityMapper;
//import org.eclipse.fennec.persistence.test.annotations.TestAnnotations;
//import org.gecko.emf.repository.EMFRepository;
//import org.junit.jupiter.api.Test;
//import org.osgi.test.common.annotation.InjectService;
//import org.osgi.test.common.service.ServiceAware;
//
///**
// * See documentation here: 
// * 	https://github.com/osgi/osgi-test
// * 	https://github.com/osgi/osgi-test/wiki
// * Examples: https://github.com/osgi/osgi-test/tree/main/examples
// */
//public class EPersistenceRepositoryTest extends EPersistenceBase {
//	
//	protected EClass classAEClass;
//	protected EClass classBEClass;
//	protected EClass classCEClass;
//	
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.persistence.test.EPersistenceBase#setupMappings(org.eclipse.fennec.persistence.orm.helper.EntityMapper, org.eclipse.emf.ecore.EPackage)
//	 */
//	@Override
//	protected EntityMappings setupMappings(EntityMapper mapper, EPackage ePackage) {
//		classAEClass = (EClass) ePackage.getEClassifier("ClassAM2M");
//		assertNotNull(classAEClass);
//		classBEClass = (EClass) ePackage.getEClassifier("ClassBM2M");
//		assertNotNull(classBEClass);
//		classCEClass = (EClass) ePackage.getEClassifier("ClassCM2M");
//		assertNotNull(classCEClass);
//		EntityMappings mapping = mapper.createMappings(List.of(classAEClass, classBEClass, classCEClass));
//		return mapping;
//	}
//
//	@Test
//	@TestAnnotations.DefaultRepositoryConfiguration
//	public void testManyToManyUni(@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
//			@InjectService(timeout = 7000, filter ="(repo_id=test)") ServiceAware<EMFRepository> repoAware, 
//			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware) throws InterruptedException, IOException {
//		assertFalse(modelPackageAware.isEmpty());
//		assertFalse(dataSourceAware.isEmpty());
//		assertFalse(repoAware.isEmpty());
//		
//		EObject classA01EO = EcoreUtil.create(classAEClass);
////		EObject classA02EO = EcoreUtil.create(classAEClass);
////		EObject classA03EO = EcoreUtil.create(classAEClass);
//		EObject classB01EO = EcoreUtil.create(classBEClass);
//		EObject classB02EO = EcoreUtil.create(classBEClass);
////		EObject classB03EO = EcoreUtil.create(classBEClass);
//		//
//		
//		EStructuralFeature aNameFeature = classAEClass.getEStructuralFeature("name");
//		assertNotNull(aNameFeature);
//		EStructuralFeature aBNonContainmentFeature = classAEClass.getEStructuralFeature("bNonContainment");
//		assertNotNull(aBNonContainmentFeature);
//		
//		classA01EO.eSet(aNameFeature, "Emil Tester");
////		classA02EO.eSet(aNameFeature, "Emilia Testerova");
////		classA03EO.eSet(aNameFeature, "Amalia Toaster");
//
//		EStructuralFeature bNameFeature = classBEClass.getEStructuralFeature("name");
//		assertNotNull(bNameFeature);
//		EStructuralFeature bClassAFeature = classBEClass.getEStructuralFeature("bClassA");
//		assertNotNull(bClassAFeature);
//		
//		classB01EO.eSet(bNameFeature, "First B");
//		classB02EO.eSet(bNameFeature, "Second B");
////		classB03EO.eSet(bNameFeature, "Third B");
//		
//		classA01EO.eSet(aBNonContainmentFeature, List.of(classB01EO, classB02EO));
////		classA02EO.eSet(aBNonContainmentFeature, List.of(classB01EO, classB03EO));
////		classA03EO.eSet(aBNonContainmentFeature, List.of(classB03EO));
//		
//		Object b01AListObject = classB01EO.eGet(bClassAFeature);
//		assertNotNull(b01AListObject);
//		assertInstanceOf(Collection.class, b01AListObject);
//		assertTrue(((Collection<?>)b01AListObject).isEmpty());
////		assertEquals(2, ((Collection<?>)b01AListObject).size());
////		assertTrue(((Collection<?>)b01AListObject).contains(classA01EO));
////		assertTrue(((Collection<?>)b01AListObject).contains(classA02EO));
//		
//		Object b02AListObject = classB02EO.eGet(bClassAFeature);
//		assertNotNull(b02AListObject);
//		assertInstanceOf(Collection.class, b02AListObject);
//		assertTrue(((Collection<?>)b02AListObject).isEmpty());
////		assertEquals(1, ((Collection<?>)b02AListObject).size());
////		Object b03AListObject = classB03EO.eGet(bClassAFeature);
////		assertNotNull(b03AListObject);
////		assertInstanceOf(Collection.class, b03AListObject);
////		assertTrue(((Collection<?>)b03AListObject).isEmpty());
////		assertEquals(2, ((Collection<?>)b03AListObject).size());
//		
//		EMFRepository repo = repoAware.getService();
//		try  {
//			repo.save(classB01EO, classB02EO, classA01EO);
//		} catch (Exception e) {
//			fail("Fail test Many-to-Many mapping persist", e);
//		};
//		
//		EObject findAEO = null;
//		try {
//			String id = EcoreUtil.getID(classA01EO);
//			assertNotNull(id);
//			findAEO = repo.getEObject(classAEClass, Integer.valueOf(id));
//			
//		} catch (Exception e) {
//			fail("Fail test Many-to-Many mapping find", e);
//		}
//		
//		assertNotNull(findAEO);
//		assertNotEquals(classA01EO, findAEO);
//		Object aNameObject = findAEO.eGet(aNameFeature);
//		assertNotNull(aNameObject);
//		assertEquals("Emil Tester", aNameObject);
//		
//		Object bListObject = findAEO.eGet(aBNonContainmentFeature);
//		assertNotNull(bListObject);
//		assertInstanceOf(List.class, bListObject);
//		List<?> bResultList = (List<?>) bListObject;
//		assertEquals(2, bResultList.size());
//		final Set<String> values = new HashSet<>(Set.of("First B", "Second B", "Third B"));
//		for (Object o : bResultList) {
//			assertInstanceOf(EObject.class, o);
//			EObject beo = (EObject) o;
//			assertEquals(classBEClass, beo.eClass());
//			Object bNameObject = beo.eGet(bNameFeature);
//			assertNotNull(bNameObject);
//			assertTrue(values.remove(bNameObject));
////			if ("First Street".equals(bNameObject)) {
////				Object addressPersonsObject = beo.eGet(aBNonContainmentFeature);
////				assertNotNull(addressPersonsObject);
////				assertInstanceOf(List.class, addressPersonsObject);
////				List<?> addressPersonsResultList = (List<?>) addressPersonsObject;
////				assertEquals(2, addressPersonsResultList.size());
////				assertTrue(addressPersonsResultList.contains(findAEO));
//////				Set<String> personNames = new HashSet<>(Set.of("Emil Tester", "Emilia Testerova"));
////				for (Object po : addressPersonsResultList) {
////					assertInstanceOf(EObject.class, po);
////					EObject peo = (EObject) po;
////					assertEquals(citizenEClass, peo.eClass());
////					Object personNameObject = peo.eGet(citizenNameFeature);
////					assertNotNull(personNameObject);
//////					assertTrue(personNames.remove(personNameObject));
////				}
//////				assertTrue(personNames.isEmpty());
////			}
//		};
//		assertEquals(1, values.size());
//		assertTrue(values.contains("Third B"));
//		
////		EObject findBEO = null;
////		try {
////			String id = EcoreUtil.getID(classB03EO);
////			assertNotNull(id);
////			findBEO = repo.getEObject(classBEClass, Integer.valueOf(id));
////			
////		} catch (Exception e) {
////			fail("Fail test Many-to-Many mapping find", e);
////		}
////		
////		assertNotNull(findBEO);
////		assertNotEquals(classB03EO, findBEO);
////		Object bNameObject = findBEO.eGet(bNameFeature);
////		assertNotNull(bNameObject);
////		assertEquals("Third B", bNameObject);
////		
////		Object aListObject = findBEO.eGet(bClassAFeature);
////		assertNotNull(aListObject);
////		assertInstanceOf(List.class, aListObject);
////		List<?> aResultList = (List<?>) aListObject;
////		// we have not set any A in one of the B
////		assertTrue(aResultList.isEmpty());
//	}
//	
//}
