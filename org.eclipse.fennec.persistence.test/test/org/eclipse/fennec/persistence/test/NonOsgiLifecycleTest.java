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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.RollbackException;

/**
 * Non-OSGi port of {@code EPersistenceLifecycleTest}. Update, delete cascade,
 * non-containment survival, optimistic locking (AP-01, AP-02, AP-13).
 */
class NonOsgiLifecycleTest extends NonOsgiPersistenceTestBase {

	private EPackage modelPackage;
	private EClass classAO2MClass;
	private EClass classBO2MClass;
	private EClass classDO2MClass;
	private EClass versionedEntityClass;

	@BeforeEach
	void setUp() {
		modelPackage = loadEcore("data/model.ecore");
		classAO2MClass = (EClass) modelPackage.getEClassifier("ClassAO2M");
		classBO2MClass = (EClass) modelPackage.getEClassifier("ClassBO2M");
		classDO2MClass = (EClass) modelPackage.getEClassifier("ClassDO2M");
		versionedEntityClass = (EClass) modelPackage.getEClassifier("VersionedEntity");
		assertNotNull(classAO2MClass);
		assertNotNull(classBO2MClass);
		assertNotNull(classDO2MClass);
		assertNotNull(versionedEntityClass);
		bootstrapPersistence("person",
				List.of(classAO2MClass, classBO2MClass, classDO2MClass, versionedEntityClass));
	}

	@Test
	@DisplayName("Update: modify persisted entity attribute and verify change survives roundtrip")
	void testUpdateAttribute() {
		ClassDescriptor aDesc = serverSession.getDescriptorForAlias(classAO2MClass.getName());
		EStructuralFeature nameFeature = classAO2MClass.getEStructuralFeature("name");

		EObject aEO = (EObject) aDesc.getInstantiationPolicy().buildNewInstance();
		aEO.eSet(nameFeature, "Original");

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(aEO);
			em.getTransaction().commit();
		}

		String id = EcoreUtil.getID(aEO);
		assertNotNull(id);

		try (EntityManager em = emf.createEntityManager()) {
			EObject found = em.find(aDesc.getJavaClass(), Integer.valueOf(id));
			assertNotNull(found);
			assertEquals("Original", found.eGet(nameFeature));
			em.getTransaction().begin();
			found.eSet(nameFeature, "Updated");
			em.merge(found);
			em.getTransaction().commit();
		}

		try (EntityManager em = emf.createEntityManager()) {
			EObject verified = em.find(aDesc.getJavaClass(), Integer.valueOf(id));
			assertNotNull(verified);
			assertEquals("Updated", verified.eGet(nameFeature));
		}
	}

	@Test
	@DisplayName("Delete parent with non-containment ref: referenced object survives (AP-01 proof)")
	void testDeleteParentNonContainmentRefSurvives() {
		ClassDescriptor aDesc = serverSession.getDescriptorForAlias(classAO2MClass.getName());
		ClassDescriptor dDesc = serverSession.getDescriptorForAlias(classDO2MClass.getName());

		EStructuralFeature aNameFeature = classAO2MClass.getEStructuralFeature("name");
		EStructuralFeature dNameFeature = classDO2MClass.getEStructuralFeature("name");
		EStructuralFeature dNonContainment = classAO2MClass.getEStructuralFeature("dNonContainment");

		EObject aEO = (EObject) aDesc.getInstantiationPolicy().buildNewInstance();
		aEO.eSet(aNameFeature, "Parent A");
		EObject dEO = (EObject) dDesc.getInstantiationPolicy().buildNewInstance();
		dEO.eSet(dNameFeature, "Referenced D");

		aEO.eSet(dNonContainment, List.of(dEO));
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(dEO);
			em.persist(aEO);
			em.getTransaction().commit();
		}

		String aId = EcoreUtil.getID(aEO);
		String dId = EcoreUtil.getID(dEO);

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			EObject aToDelete = em.find(aDesc.getJavaClass(), Integer.valueOf(aId));
			assertNotNull(aToDelete);
			aToDelete.eSet(dNonContainment, List.of());
			em.merge(aToDelete);
			em.flush();
			em.remove(aToDelete);
			em.getTransaction().commit();
		}

		try (EntityManager em = emf.createEntityManager()) {
			assertNull(em.find(aDesc.getJavaClass(), Integer.valueOf(aId)), "Parent A should be deleted");
			EObject dAlive = em.find(dDesc.getJavaClass(), Integer.valueOf(dId));
			assertNotNull(dAlive, "Non-containment referenced D must survive parent deletion");
			assertEquals("Referenced D", dAlive.eGet(dNameFeature));
		}
	}

	@Test
	@DisplayName("Delete parent with containment children: children are cascade-deleted")
	void testDeleteParentContainmentCascade() {
		ClassDescriptor aDesc = serverSession.getDescriptorForAlias(classAO2MClass.getName());
		ClassDescriptor bDesc = serverSession.getDescriptorForAlias(classBO2MClass.getName());

		EStructuralFeature aNameFeature = classAO2MClass.getEStructuralFeature("name");
		EStructuralFeature bNameFeature = classBO2MClass.getEStructuralFeature("name");
		EStructuralFeature bContainment = classAO2MClass.getEStructuralFeature("bContainment");

		EObject aEO = (EObject) aDesc.getInstantiationPolicy().buildNewInstance();
		aEO.eSet(aNameFeature, "Parent A");

		EObject b1EO = (EObject) bDesc.getInstantiationPolicy().buildNewInstance();
		b1EO.eSet(bNameFeature, "Child B1");
		EObject b2EO = (EObject) bDesc.getInstantiationPolicy().buildNewInstance();
		b2EO.eSet(bNameFeature, "Child B2");

		aEO.eSet(bContainment, List.of(b1EO, b2EO));

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(aEO);
			em.getTransaction().commit();
		}

		String aId = EcoreUtil.getID(aEO);
		String b1Id = EcoreUtil.getID(b1EO);
		String b2Id = EcoreUtil.getID(b2EO);

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			EObject aToDelete = em.find(aDesc.getJavaClass(), Integer.valueOf(aId));
			em.remove(aToDelete);
			em.getTransaction().commit();
		}

		try (EntityManager em = emf.createEntityManager()) {
			assertNull(em.find(aDesc.getJavaClass(), Integer.valueOf(aId)), "Parent A should be deleted");
			assertNull(em.find(bDesc.getJavaClass(), Integer.valueOf(b1Id)), "Containment child B1 should be cascade-deleted");
			assertNull(em.find(bDesc.getJavaClass(), Integer.valueOf(b2Id)), "Containment child B2 should be cascade-deleted");
		}
	}

	@Test
	@DisplayName("Optimistic locking: concurrent modification triggers conflict (AP-13 proof)")
	void testOptimisticLockingConflict() {
		ClassDescriptor vDesc = serverSession.getDescriptorForAlias(versionedEntityClass.getName());
		assertNotNull(vDesc, "VersionedEntity descriptor must be registered");

		EStructuralFeature nameFeature = versionedEntityClass.getEStructuralFeature("name");

		EObject vEO = (EObject) vDesc.getInstantiationPolicy().buildNewInstance();
		vEO.eSet(nameFeature, "Initial");

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(vEO);
			em.getTransaction().commit();
		}

		String id = EcoreUtil.getID(vEO);
		assertNotNull(id);

		EntityManager em1 = emf.createEntityManager();
		EntityManager em2 = emf.createEntityManager();

		try {
			EObject v1 = em1.find(vDesc.getJavaClass(), Integer.valueOf(id));
			EObject v2 = em2.find(vDesc.getJavaClass(), Integer.valueOf(id));
			assertNotNull(v1);
			assertNotNull(v2);

			em1.getTransaction().begin();
			v1.eSet(nameFeature, "Updated by EM1");
			em1.merge(v1);
			em1.getTransaction().commit();

			em2.getTransaction().begin();
			v2.eSet(nameFeature, "Updated by EM2");
			em2.merge(v2);

			assertThrows(RollbackException.class, () -> em2.getTransaction().commit(),
					"Concurrent modification must trigger OptimisticLockException/RollbackException");
		} finally {
			if (em1.isOpen()) em1.close();
			if (em2.isOpen()) em2.close();
		}

		try (EntityManager em = emf.createEntityManager()) {
			EObject verified = em.find(vDesc.getJavaClass(), Integer.valueOf(id));
			assertEquals("Updated by EM1", verified.eGet(nameFeature));
		}
	}

	@Test
	@DisplayName("Version field is incremented on update")
	void testVersionIncrementOnUpdate() {
		ClassDescriptor vDesc = serverSession.getDescriptorForAlias(versionedEntityClass.getName());
		assertNotNull(vDesc);

		EStructuralFeature nameFeature = versionedEntityClass.getEStructuralFeature("name");
		EStructuralFeature versionFeature = versionedEntityClass.getEStructuralFeature("versionNum");

		EObject vEO = (EObject) vDesc.getInstantiationPolicy().buildNewInstance();
		vEO.eSet(nameFeature, "V0");

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(vEO);
			em.getTransaction().commit();
		}

		String id = EcoreUtil.getID(vEO);

		int initialVersion;
		try (EntityManager em = emf.createEntityManager()) {
			EObject found = em.find(vDesc.getJavaClass(), Integer.valueOf(id));
			initialVersion = (int) found.eGet(versionFeature);
		}

		try (EntityManager em = emf.createEntityManager()) {
			EObject found = em.find(vDesc.getJavaClass(), Integer.valueOf(id));
			em.getTransaction().begin();
			found.eSet(nameFeature, "V1");
			em.merge(found);
			em.getTransaction().commit();
		}

		try (EntityManager em = emf.createEntityManager()) {
			EObject found = em.find(vDesc.getJavaClass(), Integer.valueOf(id));
			assertEquals("V1", found.eGet(nameFeature));
			int newVersion = (int) found.eGet(versionFeature);
			assertEquals(initialVersion + 1, newVersion, "Version must be incremented after update");
		}
	}
}
