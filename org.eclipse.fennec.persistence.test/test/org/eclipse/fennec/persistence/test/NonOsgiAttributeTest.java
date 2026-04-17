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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

/**
 * Non-OSGi port of {@code EPersistenceAttributeTest}. Exercises attribute
 * persistence (simple types, enums, collections, byte arrays) against an
 * in-memory H2 using the shared {@link NonOsgiPersistenceTestBase}.
 */
class NonOsgiAttributeTest extends NonOsgiPersistenceTestBase {

	private EPackage modelPackage;
	private EClass personEClass;
	private EClass classOneEClass;

	@BeforeEach
	void setUp() {
		modelPackage = loadEcore("data/model.ecore");
		personEClass = (EClass) modelPackage.getEClassifier("Person");
		classOneEClass = (EClass) modelPackage.getEClassifier("ClassOne");
		assertNotNull(personEClass);
		assertNotNull(classOneEClass);
		bootstrapPersistence("person", List.of(personEClass, classOneEClass));
	}

	@Test
	void testPersonRoundtripWithDefaultString() {
		ClassDescriptor personDescriptor = serverSession.getDescriptorForAlias(personEClass.getName());
		ClassDescriptor classOneDescriptor = serverSession.getDescriptorForAlias(classOneEClass.getName());
		assertNotNull(personDescriptor);
		assertNotNull(classOneDescriptor);

		EObject personEO = (EObject) personDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classOne01EO = (EObject) classOneDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classOne02EO = (EObject) classOneDescriptor.getInstantiationPolicy().buildNewInstance();
		EObject classOne03EO = (EObject) classOneDescriptor.getInstantiationPolicy().buildNewInstance();

		EStructuralFeature attributeOneFeature = classOneEClass.getEStructuralFeature("attributeOne");
		classOne01EO.eSet(attributeOneFeature, "The First!");
		classOne02EO.eSet(attributeOneFeature, "The Second!");
		classOne03EO.eSet(attributeOneFeature, "The Third!");

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
			fail("Failed to persist Person+ClassOne", e);
		}

		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(personEO);
			assertNotNull(id);
			findEO = em.find(personDescriptor.getJavaClass(), id);
		} catch (Exception e) {
			fail("Failed to find Person", e);
		}

		assertNotNull(findEO);
		assertNotEquals(personEO, findEO);
		assertEquals("Hello World", findEO.eGet(stringDefaultFeature));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testManyAttributes() {
		ClassDescriptor descriptor = serverSession.getDescriptorForAlias(personEClass.getName());
		assertNotNull(descriptor);

		EObject eObject = (EObject) descriptor.getInstantiationPolicy().buildNewInstance();

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
			fail("Failed roundtrip for many attributes", e);
		}

		assertNotNull(findEO);
		assertNotEquals(eObject, findEO);
		assertThat((List<String>) findEO.eGet(stringTagsRequiredFeature))
				.hasSize(2)
				.contains("tag1", "tag-heuer");
		assertThat((List<Integer>) findEO.eGet(intListOptionalFeature))
				.hasSize(3)
				.contains(24, 42, 2442);
	}

	@Test
	void testSimpleAttributesAndEnum() {
		ClassDescriptor descriptor = serverSession.getDescriptorForAlias(personEClass.getName());
		assertNotNull(descriptor);

		EObject eObject = (EObject) descriptor.getInstantiationPolicy().buildNewInstance();

		EStructuralFeature stringDefaultFeature = eObject.eClass().getEStructuralFeature("stringDefault");
		EStructuralFeature stringTagsRequiredFeature = eObject.eClass().getEStructuralFeature("stringTagsRequired");
		EStructuralFeature birthDateFeature = eObject.eClass().getEStructuralFeature("birthDate");
		EStructuralFeature cmDatatypeWithClassFeature = eObject.eClass().getEStructuralFeature("cmDatatypeWithClass");
		EStructuralFeature typeFeature = eObject.eClass().getEStructuralFeature("type");
		EStructuralFeature localDateFeature = eObject.eClass().getEStructuralFeature("localDateNoTypeConverter");
		EStructuralFeature dataFeature = eObject.eClass().getEStructuralFeature("data");
		assertNotNull(stringDefaultFeature);
		assertNotNull(stringTagsRequiredFeature);
		assertNotNull(birthDateFeature);
		assertNotNull(cmDatatypeWithClassFeature);
		assertNotNull(typeFeature);
		assertNotNull(localDateFeature);
		assertNotNull(dataFeature);

		long timestamp = System.currentTimeMillis();
		eObject.eSet(birthDateFeature, new Date(timestamp));

		EEnum personTypeEnum = (EEnum) modelPackage.getEClassifier("PersonType");
		assertNotNull(personTypeEnum);
		EEnumLiteral businessLiteral = personTypeEnum.getEEnumLiteral(1);
		assertNotNull(businessLiteral);
		eObject.eSet(typeFeature, businessLiteral);

		LocalDate localDateNow = LocalDate.now();
		eObject.eSet(localDateFeature, localDateNow);

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
			fail("Failed to persist with enum and simple attributes", e);
		}

		assertNotNull(findEO);
		assertNotEquals(eObject, findEO);
		assertEquals(stringDefaultFeature.getDefaultValueLiteral(), findEO.eGet(stringDefaultFeature));
		Object birthDateObject = findEO.eGet(birthDateFeature);
		assertThat(birthDateObject).isInstanceOf(Date.class);
		assertEquals(timestamp, ((Date) birthDateObject).getTime());
		assertEquals(3, findEO.eGet(cmDatatypeWithClassFeature));
		assertEquals(businessLiteral, findEO.eGet(typeFeature));
		assertEquals(localDateNow, findEO.eGet(localDateFeature));
		assertArrayEquals(hwa, (byte[]) findEO.eGet(dataFeature));
	}
}
