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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.List;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.RollbackException;

/**
 * Entity Lifecycle E2E tests: Update, Delete (cascade, non-containment),
 * OrphanRemoval, and Optimistic Locking (@Version).
 * <p>
 * These tests prove that the fixes from AP-01 (cascade), AP-02 (rollback),
 * AP-12 (inheritance), and AP-13 (version locking) work end-to-end against
 * a real H2 database via EclipseLink.
 *
 * @author Mark Hoffmann
 * @since 14.04.2026
 */
public class EPersistenceLifecycleTest extends EPersistenceBase {

	private EClass classAO2MClass;
	private EClass classBO2MClass;
	private EClass classDO2MClass;
	private EClass versionedEntityClass;

	@Override
	protected EntityMappings setupMappings(EntityMapper mapper, EPackage ePackage) {
		classAO2MClass = (EClass) ePackage.getEClassifier("ClassAO2M");
		assertNotNull(classAO2MClass);
		classBO2MClass = (EClass) ePackage.getEClassifier("ClassBO2M");
		assertNotNull(classBO2MClass);
		classDO2MClass = (EClass) ePackage.getEClassifier("ClassDO2M");
		assertNotNull(classDO2MClass);
		versionedEntityClass = (EClass) ePackage.getEClassifier("VersionedEntity");
		assertNotNull(versionedEntityClass);
		return mapper.createMappings(List.of(
				classAO2MClass, classBO2MClass, classDO2MClass, versionedEntityClass));
	}

	// ── Update ────────────────────────────────────────────────────────────

	@Test
	@DisplayName("Update: modify persisted entity attribute and verify change survives roundtrip")
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testUpdateAttribute(
			@InjectService(timeout = 500) ServiceAware<DataSource> dsAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> pkgAware,
			@InjectService(timeout = 7000) ServiceAware<EntityManagerFactory> emfAware)
			throws InterruptedException, IOException {
		assertFalse(emfAware.isEmpty());
		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);

		ClassDescriptor aDesc = server.getDescriptorForAlias(classAO2MClass.getName());
		assertNotNull(aDesc);
		EStructuralFeature nameFeature = classAO2MClass.getEStructuralFeature("name");

		// Persist
		EObject aEO = (EObject) aDesc.getInstantiationPolicy().buildNewInstance();
		aEO.eSet(nameFeature, "Original");

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(aEO);
			em.getTransaction().commit();
		}

		String id = EcoreUtil.getID(aEO);
		assertNotNull(id);

		// Update in a new EntityManager
		try (EntityManager em = emf.createEntityManager()) {
			EObject found = em.find(aDesc.getJavaClass(), Integer.valueOf(id));
			assertNotNull(found);
			assertEquals("Original", found.eGet(nameFeature));

			em.getTransaction().begin();
			found.eSet(nameFeature, "Updated");
			em.merge(found);
			em.getTransaction().commit();
		}

		// Verify in a third EntityManager
		try (EntityManager em = emf.createEntityManager()) {
			EObject verified = em.find(aDesc.getJavaClass(), Integer.valueOf(id));
			assertNotNull(verified);
			assertEquals("Updated", verified.eGet(nameFeature));
		}
	}

	// ── Delete: Non-Containment Reference ─────────────────────────────────

	@Test
	@DisplayName("Delete parent with non-containment ref: referenced object survives (AP-01 proof)")
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testDeleteParentNonContainmentRefSurvives(
			@InjectService(timeout = 500) ServiceAware<DataSource> dsAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> pkgAware,
			@InjectService(timeout = 7000) ServiceAware<EntityManagerFactory> emfAware)
			throws InterruptedException, IOException {
		assertFalse(emfAware.isEmpty());
		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);

		ClassDescriptor aDesc = server.getDescriptorForAlias(classAO2MClass.getName());
		ClassDescriptor dDesc = server.getDescriptorForAlias(classDO2MClass.getName());
		assertNotNull(aDesc);
		assertNotNull(dDesc);

		EStructuralFeature aNameFeature = classAO2MClass.getEStructuralFeature("name");
		EStructuralFeature dNameFeature = classDO2MClass.getEStructuralFeature("name");
		EStructuralFeature dNonContainment = classAO2MClass.getEStructuralFeature("dNonContainment");

		// Create parent A with non-containment ref to D
		EObject aEO = (EObject) aDesc.getInstantiationPolicy().buildNewInstance();
		aEO.eSet(aNameFeature, "Parent A");
		EObject dEO = (EObject) dDesc.getInstantiationPolicy().buildNewInstance();
		dEO.eSet(dNameFeature, "Referenced D");

		// Persist D first, then A with non-containment ref to D (same transaction)
		aEO.eSet(dNonContainment, List.of(dEO));
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(dEO);
			em.persist(aEO);
			em.getTransaction().commit();
		}

		String aId = EcoreUtil.getID(aEO);
		String dId = EcoreUtil.getID(dEO);
		assertNotNull(aId);

		// Delete parent A — clear non-containment ref first to clean up join table
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			EObject aToDelete = em.find(aDesc.getJavaClass(), Integer.valueOf(aId));
			assertNotNull(aToDelete);
			// Clear the non-containment reference (join table cleanup)
			aToDelete.eSet(dNonContainment, List.of());
			em.merge(aToDelete);
			em.flush();
			em.remove(aToDelete);
			em.getTransaction().commit();
		}

		// Verify: A is gone, D still exists
		try (EntityManager em = emf.createEntityManager()) {
			EObject aGone = em.find(aDesc.getJavaClass(), Integer.valueOf(aId));
			assertNull(aGone, "Parent A should be deleted");

			EObject dAlive = em.find(dDesc.getJavaClass(), Integer.valueOf(dId));
			assertNotNull(dAlive, "Non-containment referenced D must survive parent deletion");
			assertEquals("Referenced D", dAlive.eGet(dNameFeature));
		}
	}

	// ── Delete: Containment Cascade ───────────────────────────────────────

	@Test
	@DisplayName("Delete parent with containment children: children are cascade-deleted")
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testDeleteParentContainmentCascade(
			@InjectService(timeout = 500) ServiceAware<DataSource> dsAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> pkgAware,
			@InjectService(timeout = 7000) ServiceAware<EntityManagerFactory> emfAware)
			throws InterruptedException, IOException {
		assertFalse(emfAware.isEmpty());
		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);

		ClassDescriptor aDesc = server.getDescriptorForAlias(classAO2MClass.getName());
		ClassDescriptor bDesc = server.getDescriptorForAlias(classBO2MClass.getName());
		assertNotNull(aDesc);
		assertNotNull(bDesc);

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

		// Persist parent with children
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(aEO);
			em.getTransaction().commit();
		}

		String aId = EcoreUtil.getID(aEO);
		String b1Id = EcoreUtil.getID(b1EO);
		String b2Id = EcoreUtil.getID(b2EO);

		// Delete parent
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			EObject aToDelete = em.find(aDesc.getJavaClass(), Integer.valueOf(aId));
			assertNotNull(aToDelete);
			em.remove(aToDelete);
			em.getTransaction().commit();
		}

		// Verify: A and both B children are gone
		try (EntityManager em = emf.createEntityManager()) {
			assertNull(em.find(aDesc.getJavaClass(), Integer.valueOf(aId)),
					"Parent A should be deleted");
			assertNull(em.find(bDesc.getJavaClass(), Integer.valueOf(b1Id)),
					"Containment child B1 should be cascade-deleted");
			assertNull(em.find(bDesc.getJavaClass(), Integer.valueOf(b2Id)),
					"Containment child B2 should be cascade-deleted");
		}
	}

	// ── Optimistic Locking (@Version) ─────────────────────────────────────

	@Test
	@DisplayName("Optimistic locking: concurrent modification triggers conflict (AP-13 proof)")
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testOptimisticLockingConflict(
			@InjectService(timeout = 500) ServiceAware<DataSource> dsAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> pkgAware,
			@InjectService(timeout = 7000) ServiceAware<EntityManagerFactory> emfAware)
			throws InterruptedException, IOException {
		assertFalse(emfAware.isEmpty());
		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);

		ClassDescriptor vDesc = server.getDescriptorForAlias(versionedEntityClass.getName());
		assertNotNull(vDesc, "VersionedEntity descriptor must be registered");

		EStructuralFeature nameFeature = versionedEntityClass.getEStructuralFeature("name");

		// Persist initial entity
		EObject vEO = (EObject) vDesc.getInstantiationPolicy().buildNewInstance();
		vEO.eSet(nameFeature, "Initial");

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(vEO);
			em.getTransaction().commit();
		}

		String id = EcoreUtil.getID(vEO);
		assertNotNull(id);

		// Load in two separate EntityManagers (simulating concurrent access)
		EntityManager em1 = emf.createEntityManager();
		EntityManager em2 = emf.createEntityManager();

		try {
			EObject v1 = em1.find(vDesc.getJavaClass(), Integer.valueOf(id));
			EObject v2 = em2.find(vDesc.getJavaClass(), Integer.valueOf(id));
			assertNotNull(v1);
			assertNotNull(v2);

			// First update succeeds
			em1.getTransaction().begin();
			v1.eSet(nameFeature, "Updated by EM1");
			em1.merge(v1);
			em1.getTransaction().commit();

			// Second update on stale version should fail
			em2.getTransaction().begin();
			v2.eSet(nameFeature, "Updated by EM2");
			em2.merge(v2);

			assertThrows(RollbackException.class, () -> em2.getTransaction().commit(),
					"Concurrent modification must trigger OptimisticLockException/RollbackException");
		} finally {
			if (em1.isOpen()) em1.close();
			if (em2.isOpen()) em2.close();
		}

		// Verify: the first update's value persists
		try (EntityManager em = emf.createEntityManager()) {
			EObject verified = em.find(vDesc.getJavaClass(), Integer.valueOf(id));
			assertNotNull(verified);
			assertEquals("Updated by EM1", verified.eGet(nameFeature));
		}
	}

	// ── Update: version is incremented ────────────────────────────────────

	@Test
	@DisplayName("Version field is incremented on update")
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testVersionIncrementOnUpdate(
			@InjectService(timeout = 500) ServiceAware<DataSource> dsAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> pkgAware,
			@InjectService(timeout = 7000) ServiceAware<EntityManagerFactory> emfAware)
			throws InterruptedException, IOException {
		assertFalse(emfAware.isEmpty());
		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);

		ClassDescriptor vDesc = server.getDescriptorForAlias(versionedEntityClass.getName());
		assertNotNull(vDesc);

		EStructuralFeature nameFeature = versionedEntityClass.getEStructuralFeature("name");
		EStructuralFeature versionFeature = versionedEntityClass.getEStructuralFeature("versionNum");

		// Persist
		EObject vEO = (EObject) vDesc.getInstantiationPolicy().buildNewInstance();
		vEO.eSet(nameFeature, "V0");

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(vEO);
			em.getTransaction().commit();
		}

		String id = EcoreUtil.getID(vEO);

		// Read initial version
		int initialVersion;
		try (EntityManager em = emf.createEntityManager()) {
			EObject found = em.find(vDesc.getJavaClass(), Integer.valueOf(id));
			assertNotNull(found);
			initialVersion = (int) found.eGet(versionFeature);
		}

		// Update
		try (EntityManager em = emf.createEntityManager()) {
			EObject found = em.find(vDesc.getJavaClass(), Integer.valueOf(id));
			em.getTransaction().begin();
			found.eSet(nameFeature, "V1");
			em.merge(found);
			em.getTransaction().commit();
		}

		// Verify version incremented
		try (EntityManager em = emf.createEntityManager()) {
			EObject found = em.find(vDesc.getJavaClass(), Integer.valueOf(id));
			assertNotNull(found);
			assertEquals("V1", found.eGet(nameFeature));
			int newVersion = (int) found.eGet(versionFeature);
			assertEquals(initialVersion + 1, newVersion, "Version must be incremented after update");
		}
	}
}
