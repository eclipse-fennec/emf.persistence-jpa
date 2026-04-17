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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

/**
 * Non-OSGi port of {@code EPersistenceCitizenTest}. Loads the citizen_geojson
 * Ecore, maps the 7 entity classes with {@code strict=true} (so column names
 * are taken as-is) and verifies descriptor registration plus a persist/find
 * roundtrip on {@code statbez}.
 */
class NonOsgiCitizenTest extends NonOsgiPersistenceTestBase {

	private EPackage modelPackage;
	private EClass ageGroupsEClass;
	private EClass einwohnerEClass;
	private EClass genderEClass;
	private EClass statbezEClass;
	private EClass plraumEClass;
	private EClass townEClass;
	private EClass yearEClass;

	@BeforeEach
	void setUp() {
		modelPackage = loadEcore("data/citizen_geojson.ecore");
		ageGroupsEClass = (EClass) modelPackage.getEClassifier("AgeGroups");
		einwohnerEClass = (EClass) modelPackage.getEClassifier("einwohner");
		genderEClass = (EClass) modelPackage.getEClassifier("gender");
		statbezEClass = (EClass) modelPackage.getEClassifier("statbez");
		plraumEClass = (EClass) modelPackage.getEClassifier("plraum");
		townEClass = (EClass) modelPackage.getEClassifier("Town");
		yearEClass = (EClass) modelPackage.getEClassifier("YearEntity");
		assertNotNull(ageGroupsEClass);
		assertNotNull(einwohnerEClass);
		assertNotNull(genderEClass);
		assertNotNull(statbezEClass);
		assertNotNull(plraumEClass);
		assertNotNull(townEClass);
		assertNotNull(yearEClass);

		bootstrapPersistence("citizen", List.of(
				statbezEClass, townEClass, yearEClass,
				ageGroupsEClass, genderEClass, plraumEClass, einwohnerEClass));
	}

	@Override
	protected void configureMapper(EntityMapper mapper) {
		mapper.setStrict(true);
	}

	@Test
	void testCitizenDescriptorsRegistered() {
		assertNotNull(serverSession.getDescriptorForAlias(ageGroupsEClass.getName()), "AgeGroups descriptor missing");
		assertNotNull(serverSession.getDescriptorForAlias(einwohnerEClass.getName()), "einwohner descriptor missing");
		assertNotNull(serverSession.getDescriptorForAlias(plraumEClass.getName()), "plraum descriptor missing");
		assertNotNull(serverSession.getDescriptorForAlias(genderEClass.getName()), "gender descriptor missing");
		assertNotNull(serverSession.getDescriptorForAlias(yearEClass.getName()), "YearEntity descriptor missing");
		ClassDescriptor statBezDescriptor = serverSession.getDescriptorForAlias(statbezEClass.getName());
		assertNotNull(statBezDescriptor, "statbez descriptor missing");
		assertNotNull(serverSession.getDescriptorForAlias(townEClass.getName()), "Town descriptor missing");

		EStructuralFeature gidFeature = statbezEClass.getEStructuralFeature("gid");
		EStructuralFeature statBezNameFeature = statbezEClass.getEStructuralFeature("statbez_name");
		assertNotNull(gidFeature);
		assertNotNull(statBezNameFeature);

		EObject statBezEO = (EObject) statBezDescriptor.getInstantiationPolicy().buildNewInstance();
		statBezEO.eSet(gidFeature, 1);
		statBezEO.eSet(statBezNameFeature, "Test-Bezirk");

		EObject findEO = null;
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(statBezEO);
			em.getTransaction().commit();
			em.clear();

			findEO = em.find(statBezDescriptor.getJavaClass(), 1);
		} catch (Exception e) {
			fail("Fail statbez persist/find roundtrip", e);
		}

		assertNotNull(findEO);
		assertEquals(statbezEClass, findEO.eClass());
		assertEquals("Test-Bezirk", findEO.eGet(statBezNameFeature));
	}
}
