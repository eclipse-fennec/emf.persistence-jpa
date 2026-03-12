/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
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
package org.eclipse.fennec.persistence.orm.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.junit.jupiter.api.Test;

/**
 * 
 * @author mark
 * @since 03.01.2025
 */
public class MappingHelperTest {
	
	@Test
	public void testUnwrapEObject() {
		assertNotNull(MappingHelper.unwrapEObject(null));
		assertTrue(MappingHelper.unwrapEObject(null).isEmpty());
		assertTrue(MappingHelper.unwrapEObject(42).isEmpty());
		assertTrue(MappingHelper.unwrapEObject(List.of(42, "24")).isEmpty());
		EObject eo1 = new DynamicEObjectImpl(EcorePackage.Literals.ECLASS);
		assertEquals(1, MappingHelper.unwrapEObject(eo1).size());
		assertEquals(1, MappingHelper.unwrapEObject(List.of(42, "24", eo1)).size());
		EObject eo2 = new DynamicEObjectImpl(EcorePackage.Literals.EREFERENCE);
		assertEquals(2, MappingHelper.unwrapEObject(List.of(42, "24", eo1, eo2)).size());
	}
	
	@Test
	public void testUnwrap() {
		assertNotNull(MappingHelper.unwrapObject(null));
		assertTrue(MappingHelper.unwrapObject(null).isEmpty());
		assertEquals(1, MappingHelper.unwrapObject(42).size());
		assertEquals(2, MappingHelper.unwrapObject(List.of(42, "24")).size());
		EObject eo1 = new DynamicEObjectImpl(EcorePackage.Literals.ECLASS);
		assertEquals(1, MappingHelper.unwrapObject(eo1).size());
		assertEquals(3, MappingHelper.unwrapObject(List.of(42, "24", eo1)).size());
		EObject eo2 = new DynamicEObjectImpl(EcorePackage.Literals.EREFERENCE);
		assertEquals(4, MappingHelper.unwrapObject(List.of(42, "24", eo1, eo2)).size());
	}
	
	@Test
	public void testRefValueEquals() {
		assertFalse(MappingHelper.refValueEquals(null, null, null));
		EObject eo1 = new DynamicEObjectImpl(EcorePackage.Literals.ECLASS);
		assertFalse(MappingHelper.refValueEquals(42, null, null));
		assertFalse(MappingHelper.refValueEquals(null, eo1, null));
		assertFalse(MappingHelper.refValueEquals(null, null, EcorePackage.Literals.ECLASS__EALL_ATTRIBUTES));
		
		eo1.eSet(EcorePackage.Literals.ENAMED_ELEMENT__NAME, "Foo");
		
		assertFalse(MappingHelper.refValueEquals(Integer.valueOf(42), eo1, EcorePackage.Literals.ENAMED_ELEMENT__NAME));
		assertTrue(MappingHelper.refValueEquals("Foo", eo1, EcorePackage.Literals.ENAMED_ELEMENT__NAME));
		
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		EReference ref01 = EcoreFactory.eINSTANCE.createEReference(); 
		ref01.setName("ref01");
		EReference ref02 = EcoreFactory.eINSTANCE.createEReference(); 
		ref02.setName("ref02");
		eClass.eSet(EcorePackage.Literals.ECLASS__ESTRUCTURAL_FEATURES, List.of(ref01, ref02));
		EReference ref03 = EcoreFactory.eINSTANCE.createEReference(); 
		ref03.setName("ref03");
		assertTrue(MappingHelper.refValueEquals(ref01, eClass, EcorePackage.Literals.ECLASS__EREFERENCES));
		assertTrue(MappingHelper.refValueEquals(ref02, eClass, EcorePackage.Literals.ECLASS__EREFERENCES));
		
		assertFalse(MappingHelper.refValueEquals(ref03, eClass, EcorePackage.Literals.ECLASS__EREFERENCES));
		
		assertTrue(MappingHelper.refValueEquals(List.of(ref01, ref02), eClass, EcorePackage.Literals.ECLASS__EREFERENCES));
		assertFalse(MappingHelper.refValueEquals(List.of(ref01, ref02, ref03), eClass, EcorePackage.Literals.ECLASS__EREFERENCES));
	}
	
	@Test
	public void testRefValueContains() {
		assertFalse(MappingHelper.refValueContains(null, null, null));
		EObject eo1 = new DynamicEObjectImpl(EcorePackage.Literals.ECLASS);
		assertFalse(MappingHelper.refValueContains(42, null, null));
		assertFalse(MappingHelper.refValueContains(null, eo1, null));
		assertFalse(MappingHelper.refValueContains(null, null, EcorePackage.Literals.ECLASS__EALL_ATTRIBUTES));
		
		eo1.eSet(EcorePackage.Literals.ENAMED_ELEMENT__NAME, "Foo");
		
		assertFalse(MappingHelper.refValueContains(Integer.valueOf(42), eo1, EcorePackage.Literals.ENAMED_ELEMENT__NAME));
		assertFalse(MappingHelper.refValueContains("Foo", eo1, EcorePackage.Literals.ENAMED_ELEMENT__NAME));
		
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		EReference ref01 = EcoreFactory.eINSTANCE.createEReference();
		ref01.setName("ref01");
		EReference ref02 = EcoreFactory.eINSTANCE.createEReference(); 
		ref02.setName("ref02");
		eClass.eSet(EcorePackage.Literals.ECLASS__ESTRUCTURAL_FEATURES, List.of(ref01, ref02));
		
		EReference ref03 = EcoreFactory.eINSTANCE.createEReference(); 
		ref03.setName("ref01");
		assertTrue(MappingHelper.refValueContains(ref03, eClass, EcorePackage.Literals.ECLASS__ESTRUCTURAL_FEATURES));
//		assertTrue(MappingHelper.refValueEquals(ref02, eClass, EcorePackage.Literals.ECLASS__EREFERENCES));
//		
//		assertFalse(MappingHelper.refValueEquals(ref03, eClass, EcorePackage.Literals.ECLASS__EREFERENCES));
//		
//		assertTrue(MappingHelper.refValueEquals(List.of(ref01, ref02), eClass, EcorePackage.Literals.ECLASS__EREFERENCES));
//		assertFalse(MappingHelper.refValueEquals(List.of(ref01, ref02, ref03), eClass, EcorePackage.Literals.ECLASS__EREFERENCES));
	}
	
	@Test
	public void testIsContainerParentChild() {
		assertFalse(MappingHelper.isContainmentParentChild(null, null));
		assertFalse(MappingHelper.isContainmentParentChild(null, null));
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		EReference ref01 = EcoreFactory.eINSTANCE.createEReference(); 
		assertFalse(MappingHelper.isContainmentParentChild(eClass, "42"));
		assertFalse(MappingHelper.isContainmentParentChild(eClass, ref01));
		// create a containment
		eClass.getEStructuralFeatures().add(ref01);
		assertTrue(MappingHelper.isContainmentParentChild(eClass, ref01));
	}
	
	@Test
	public void testIsContainmentChild() {
		assertFalse(MappingHelper.isContainmentChild(null));
		EReference ref01 = EcoreFactory.eINSTANCE.createEReference(); 
		assertFalse(MappingHelper.isContainmentChild(ref01));
		EReference ref02 = EcoreFactory.eINSTANCE.createEReference();
		ref01.setEOpposite(ref02);
		ref02.setEOpposite(ref01);
		assertFalse(MappingHelper.isContainmentChild(ref01));
		assertFalse(MappingHelper.isContainmentChild(ref02));
		ref01.setContainment(true);
		assertFalse(MappingHelper.isContainmentChild(ref01));
		assertTrue(MappingHelper.isContainmentChild(ref02));
	}
	
	@Test
	public void testIsNonContainmentOppositerelation() {
		assertFalse(MappingHelper.isNonContainmentOppositeRelation(null));
		EReference ref01 = EcoreFactory.eINSTANCE.createEReference(); 
		assertFalse(MappingHelper.isNonContainmentOppositeRelation(ref01));
		EReference ref02 = EcoreFactory.eINSTANCE.createEReference();
		ref01.setEOpposite(ref02);
		ref02.setEOpposite(ref01);
		assertTrue(MappingHelper.isNonContainmentOppositeRelation(ref01));
		assertTrue(MappingHelper.isNonContainmentOppositeRelation(ref02));
		
		ref02.setContainment(true);
		assertFalse(MappingHelper.isNonContainmentOppositeRelation(ref01));
		assertFalse(MappingHelper.isNonContainmentOppositeRelation(ref02));
	}
	
	@Test
	public void testIsOppositeRelation() {
		assertFalse(MappingHelper.isOppositeRelation(null));
		EReference ref01 = EcoreFactory.eINSTANCE.createEReference(); 
		assertFalse(MappingHelper.isOppositeRelation(ref01));
		EReference ref02 = EcoreFactory.eINSTANCE.createEReference();
		ref01.setEOpposite(ref02);
		assertTrue(MappingHelper.isOppositeRelation(ref01));
		
	}

}
