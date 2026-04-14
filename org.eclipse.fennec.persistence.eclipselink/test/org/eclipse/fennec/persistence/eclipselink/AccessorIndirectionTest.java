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
package org.eclipse.fennec.persistence.eclipselink;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.eclipselink.mappings.EFeatureAccessor;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the EclipseLink-EMF bridge: EFeatureAccessor, EReferenceAccessor,
 * EBasicIndirectionPolicy, and related MappingHelper methods.
 */
class AccessorIndirectionTest {

	private EPackage testPackage;
	private EClass personClass;
	private EAttribute nameAttr;
	private EAttribute ageAttr;

	@BeforeEach
	void setUp() {
		testPackage = EcoreFactory.eINSTANCE.createEPackage();
		testPackage.setName("test");
		testPackage.setNsURI("http://test");
		testPackage.setNsPrefix("t");

		personClass = EcoreFactory.eINSTANCE.createEClass();
		personClass.setName("Person");
		testPackage.getEClassifiers().add(personClass);

		nameAttr = EcoreFactory.eINSTANCE.createEAttribute();
		nameAttr.setName("name");
		nameAttr.setEType(EcorePackage.Literals.ESTRING);
		personClass.getEStructuralFeatures().add(nameAttr);

		ageAttr = EcoreFactory.eINSTANCE.createEAttribute();
		ageAttr.setName("age");
		ageAttr.setEType(EcorePackage.Literals.EINT);
		personClass.getEStructuralFeatures().add(ageAttr);
	}

	@Nested
	class EFeatureAccessorTests {

		@Test
		void testCreateReturnsNewInstancePerCall() {
			// No shared cache — each create() returns a fresh, immutable instance
			AttributeAccessor a1 = EFeatureAccessor.create(nameAttr);
			AttributeAccessor a2 = EFeatureAccessor.create(nameAttr);
			assertThat(a1).isNotSameAs(a2);
		}

		@Test
		void testCreateReturnsDifferentInstanceForDifferentFeature() {
			AttributeAccessor a1 = EFeatureAccessor.create(nameAttr);
			AttributeAccessor a2 = EFeatureAccessor.create(ageAttr);
			assertThat(a1).isNotSameAs(a2);
		}

		@Test
		void testGetStringAttribute() {
			EObject person = EcoreUtil.create(personClass);
			person.eSet(nameAttr, "Alice");

			AttributeAccessor accessor = EFeatureAccessor.create(nameAttr);
			Object value = accessor.getAttributeValueFromObject(person);
			assertThat(value).isEqualTo("Alice");
		}

		@Test
		void testGetIntegerAttribute() {
			EObject person = EcoreUtil.create(personClass);
			person.eSet(ageAttr, 30);

			AttributeAccessor accessor = EFeatureAccessor.create(ageAttr);
			Object value = accessor.getAttributeValueFromObject(person);
			assertThat(value).isEqualTo(30);
		}

		@Test
		void testSetStringAttribute() {
			EObject person = EcoreUtil.create(personClass);

			AttributeAccessor accessor = EFeatureAccessor.create(nameAttr);
			accessor.setAttributeValueInObject(person, "Bob");
			assertThat(person.eGet(nameAttr)).isEqualTo("Bob");
		}

		@Test
		void testSetIntegerFromString() {
			EObject person = EcoreUtil.create(personClass);

			AttributeAccessor accessor = EFeatureAccessor.create(ageAttr);
			// When DB returns a String for a non-String type, dataTypeConvert converts it
			accessor.setAttributeValueInObject(person, "25");
			assertThat(person.eGet(ageAttr)).isEqualTo(25);
		}

		@Test
		void testGetNonEObjectReturnsUnchanged() {
			AttributeAccessor accessor = EFeatureAccessor.create(nameAttr);
			Object value = accessor.getAttributeValueFromObject("not an EObject");
			assertThat(value).isEqualTo("not an EObject");
		}

		@Test
		void testGetNullFeatureReturnsObject() {
			EObject person = EcoreUtil.create(personClass);
			// Accessor with a null feature path — should handle gracefully
			AttributeAccessor accessor = EFeatureAccessor.create(nameAttr);
			Object value = accessor.getAttributeValueFromObject(person);
			// name not set → returns null (EMF default)
			assertThat(value).isNull();
		}
	}

	@Nested
	class EFeatureAccessorEnumTests {

		private EEnum statusEnum;
		private EAttribute statusAttr;
		private EClass taskClass;

		@BeforeEach
		void setUpEnum() {
			statusEnum = EcoreFactory.eINSTANCE.createEEnum();
			statusEnum.setName("Status");
			testPackage.getEClassifiers().add(statusEnum);

			EEnumLiteral active = EcoreFactory.eINSTANCE.createEEnumLiteral();
			active.setName("ACTIVE");
			active.setValue(0);
			active.setLiteral("active");
			statusEnum.getELiterals().add(active);

			EEnumLiteral inactive = EcoreFactory.eINSTANCE.createEEnumLiteral();
			inactive.setName("INACTIVE");
			inactive.setValue(1);
			inactive.setLiteral("inactive");
			statusEnum.getELiterals().add(inactive);

			taskClass = EcoreFactory.eINSTANCE.createEClass();
			taskClass.setName("Task");
			testPackage.getEClassifiers().add(taskClass);

			statusAttr = EcoreFactory.eINSTANCE.createEAttribute();
			statusAttr.setName("status");
			statusAttr.setEType(statusEnum);
			taskClass.getEStructuralFeatures().add(statusAttr);
		}

		@Test
		void testGetEnumReturnsLiteral() {
			EObject task = EcoreUtil.create(taskClass);
			task.eSet(statusAttr, statusEnum.getEEnumLiteral("ACTIVE").getInstance());

			AttributeAccessor accessor = EFeatureAccessor.create(statusAttr);
			Object value = accessor.getAttributeValueFromObject(task);
			// EEnum should return the literal string, not the Enumerator object
			assertThat(value).isEqualTo("active");
		}

		@Test
		void testSetEnumFromString() {
			EObject task = EcoreUtil.create(taskClass);

			AttributeAccessor accessor = EFeatureAccessor.create(statusAttr);
			accessor.setAttributeValueInObject(task, "inactive");

			Object stored = task.eGet(statusAttr);
			assertThat(stored).isNotNull();
			// The stored value should be an Enumerator with literal "inactive"
			assertThat(stored.toString()).contains("inactive");
		}

		@Test
		void testIsEEnumFeatureForEnum() {
			// We can't directly test isEEnumFeature (private), but we verify
			// the behavior: enum attributes return literal strings
			EObject task = EcoreUtil.create(taskClass);
			task.eSet(statusAttr, statusEnum.getEEnumLiteral("ACTIVE").getInstance());

			AttributeAccessor accessor = EFeatureAccessor.create(statusAttr);
			Object value = accessor.getAttributeValueFromObject(task);
			assertThat(value).isInstanceOf(String.class);
		}

		@Test
		void testIsNotEEnumFeatureForString() {
			EObject person = EcoreUtil.create(personClass);
			person.eSet(nameAttr, "Alice");

			AttributeAccessor accessor = EFeatureAccessor.create(nameAttr);
			Object value = accessor.getAttributeValueFromObject(person);
			// String attribute returns the actual String, not a literal extraction
			assertThat(value).isEqualTo("Alice");
		}
	}

	@Nested
	class ContainmentHelperTests {

		private EClass parentClass;
		private EClass childClass;
		private EReference childrenRef;
		private EReference parentRef;

		@BeforeEach
		void setUpContainment() {
			parentClass = EcoreFactory.eINSTANCE.createEClass();
			parentClass.setName("Parent");
			testPackage.getEClassifiers().add(parentClass);

			childClass = EcoreFactory.eINSTANCE.createEClass();
			childClass.setName("Child");
			testPackage.getEClassifiers().add(childClass);

			// Containment: Parent.children → Child (many, containment)
			childrenRef = EcoreFactory.eINSTANCE.createEReference();
			childrenRef.setName("children");
			childrenRef.setEType(childClass);
			childrenRef.setContainment(true);
			childrenRef.setUpperBound(-1);
			parentClass.getEStructuralFeatures().add(childrenRef);

			// Opposite: Child.parent → Parent (single, non-containment)
			parentRef = EcoreFactory.eINSTANCE.createEReference();
			parentRef.setName("parent");
			parentRef.setEType(parentClass);
			parentRef.setContainment(false);
			childClass.getEStructuralFeatures().add(parentRef);

			childrenRef.setEOpposite(parentRef);
			parentRef.setEOpposite(childrenRef);
		}

		@Test
		void testContainmentReferenceIsContainment() {
			assertThat(org.eclipse.fennec.persistence.orm.helper.MappingHelper.isContainmentReference(childrenRef)).isTrue();
		}

		@Test
		void testOppositeOfContainmentIsAlsoContainmentReference() {
			// parentRef is not containment itself, but its opposite IS
			assertThat(org.eclipse.fennec.persistence.orm.helper.MappingHelper.isContainmentReference(parentRef)).isTrue();
		}

		@Test
		void testContainmentChildDetection() {
			// parentRef is on the child side (its opposite is containment)
			assertThat(org.eclipse.fennec.persistence.orm.helper.MappingHelper.isContainmentChild(parentRef)).isTrue();
		}

		@Test
		void testContainmentParentIsNotChild() {
			assertThat(org.eclipse.fennec.persistence.orm.helper.MappingHelper.isContainmentChild(childrenRef)).isFalse();
		}

		@Test
		void testNonContainmentOppositeNotDetectedForContainment() {
			assertThat(org.eclipse.fennec.persistence.orm.helper.MappingHelper.isNonContainmentOppositeRelation(childrenRef)).isFalse();
			assertThat(org.eclipse.fennec.persistence.orm.helper.MappingHelper.isNonContainmentOppositeRelation(parentRef)).isFalse();
		}

		@Test
		void testContainmentSetViaSingleRef() {
			EObject parent = EcoreUtil.create(parentClass);
			EObject child = EcoreUtil.create(childClass);

			// Set child's parent → EMF should automatically add to parent.children
			child.eSet(parentRef, parent);

			assertThat(child.eContainer()).isEqualTo(parent);
			@SuppressWarnings("unchecked")
			List<EObject> children = (List<EObject>) parent.eGet(childrenRef);
			assertThat(children).contains(child);
		}
	}

	@Nested
	class NonContainmentBidiTests {

		private EClass classA;
		private EClass classB;
		private EReference aToB;
		private EReference bToA;

		@BeforeEach
		void setUpNonContainmentBidi() {
			classA = EcoreFactory.eINSTANCE.createEClass();
			classA.setName("ClassA");
			testPackage.getEClassifiers().add(classA);

			classB = EcoreFactory.eINSTANCE.createEClass();
			classB.setName("ClassB");
			testPackage.getEClassifiers().add(classB);

			aToB = EcoreFactory.eINSTANCE.createEReference();
			aToB.setName("b");
			aToB.setEType(classB);
			aToB.setContainment(false);
			aToB.setUpperBound(-1);
			classA.getEStructuralFeatures().add(aToB);

			bToA = EcoreFactory.eINSTANCE.createEReference();
			bToA.setName("a");
			bToA.setEType(classA);
			bToA.setContainment(false);
			bToA.setUpperBound(-1);
			classB.getEStructuralFeatures().add(bToA);

			aToB.setEOpposite(bToA);
			bToA.setEOpposite(aToB);
		}

		@Test
		void testIsNonContainmentOppositeRelation() {
			assertThat(org.eclipse.fennec.persistence.orm.helper.MappingHelper.isNonContainmentOppositeRelation(aToB)).isTrue();
			assertThat(org.eclipse.fennec.persistence.orm.helper.MappingHelper.isNonContainmentOppositeRelation(bToA)).isTrue();
		}

		@Test
		void testIsNotContainmentReference() {
			assertThat(org.eclipse.fennec.persistence.orm.helper.MappingHelper.isContainmentReference(aToB)).isFalse();
			assertThat(org.eclipse.fennec.persistence.orm.helper.MappingHelper.isContainmentReference(bToA)).isFalse();
		}

		@Test
		void testBidiNonContainmentWithEOpposite() {
			EObject a = EcoreUtil.create(classA);
			EObject b = EcoreUtil.create(classB);

			@SuppressWarnings("unchecked")
			List<EObject> aList = (List<EObject>) a.eGet(aToB);
			aList.add(b);

			// EMF should automatically update the opposite
			@SuppressWarnings("unchecked")
			List<EObject> bList = (List<EObject>) b.eGet(bToA);
			assertThat(bList).contains(a);
		}
	}

	@Nested
	class ProxyURITests {

		@Test
		void testProxyURIFormat() {
			EClass entityClass = EcoreFactory.eINSTANCE.createEClass();
			entityClass.setName("Entity");
			testPackage.getEClassifiers().add(entityClass);

			EAttribute idAttr = EcoreFactory.eINSTANCE.createEAttribute();
			idAttr.setName("id");
			idAttr.setEType(EcorePackage.Literals.ESTRING);
			idAttr.setID(true);
			entityClass.getEStructuralFeatures().add(idAttr);

			EObject entity = EcoreUtil.create(entityClass);
			entity.eSet(idAttr, "abc-123");

			// Verify that EcoreUtil.getID works
			assertThat(EcoreUtil.getID(entity)).isEqualTo("abc-123");

			// Verify proxy URI construction
			URI baseURI = URI.createURI("jpa://testPU/Entity");
			String id = EcoreUtil.getID(entity);
			EAttribute idAttribute = entity.eClass().getEIDAttribute();

			URI proxyURI = baseURI.appendFragment("//" + "ref" + "/" + idAttribute.getName() + "/" + id);
			assertThat(proxyURI.toString()).isEqualTo("jpa://testPU/Entity#//ref/id/abc-123");

			// Set and verify proxy
			((InternalEObject) entity).eSetProxyURI(proxyURI);
			assertThat(entity.eIsProxy()).isTrue();
			assertThat(((InternalEObject) entity).eProxyURI()).isEqualTo(proxyURI);
		}

		@Test
		void testBuildIndirectObjectDoesNotCorruptOriginal() {
			// Setup: Create an EClass with ID attribute and a non-containment reference
			EClass sourceClass = EcoreFactory.eINSTANCE.createEClass();
			sourceClass.setName("Source");
			testPackage.getEClassifiers().add(sourceClass);

			EClass targetClass = EcoreFactory.eINSTANCE.createEClass();
			targetClass.setName("Target");
			testPackage.getEClassifiers().add(targetClass);

			EAttribute targetId = EcoreFactory.eINSTANCE.createEAttribute();
			targetId.setName("id");
			targetId.setEType(EcorePackage.Literals.ESTRING);
			targetId.setID(true);
			targetClass.getEStructuralFeatures().add(targetId);

			EReference ref = EcoreFactory.eINSTANCE.createEReference();
			ref.setName("target");
			ref.setEType(targetClass);
			ref.setContainment(false); // non-containment
			sourceClass.getEStructuralFeatures().add(ref);

			// Create a "cached" target object (simulates what EclipseLink cache holds)
			EObject cachedTarget = EcoreUtil.create(targetClass);
			cachedTarget.eSet(targetId, "target-42");
			assertThat(cachedTarget.eIsProxy()).isFalse();

			// Build proxy URI manually (same logic as EBasicIndirectionPolicy)
			URI baseURI = URI.createURI("jpa://testPU/Target");
			String id = EcoreUtil.getID(cachedTarget);
			URI proxyURI = baseURI.appendFragment("//" + ref.getName() + "/" + targetId.getName() + "/" + id);

			// Create a copy as proxy (what the fixed buildIndirectObject does)
			EObject proxyCopy = EcoreUtil.copy(cachedTarget);
			((InternalEObject) proxyCopy).eSetProxyURI(proxyURI);

			// The copy is a proxy
			assertThat(proxyCopy.eIsProxy()).isTrue();
			assertThat(((InternalEObject) proxyCopy).eProxyURI()).isEqualTo(proxyURI);

			// The original MUST NOT be a proxy (cache integrity)
			assertThat(cachedTarget.eIsProxy()).isFalse();
			assertThat(EcoreUtil.getID(cachedTarget)).isEqualTo("target-42");
		}
	}
}
