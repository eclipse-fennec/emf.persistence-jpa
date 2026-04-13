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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import javax.sql.DataSource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.eclipselink.resource.JPAResourceFactory;
import org.eclipse.fennec.persistence.eclipselink.resource.JPAResourceImpl;
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
 * OSGi integration test for {@link JPAResourceImpl}.
 * Tests load, save, count, exist, and proxy resolution via EMF Resource API.
 */
public class JPAResourceIntegrationTest extends EPersistenceBase {

	protected EClass personEClass;

	@Override
	protected EntityMappings setupMappings(EntityMapper mapper, EPackage ePackage) {
		personEClass = (EClass) ePackage.getEClassifier("Person");
		assertNotNull(personEClass);
		EntityMappings mapping = mapper.createMappings(List.of(personEClass));
		return mapping;
	}

	@Test
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testLoadEntitiesViaResource(
			@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 7000) ServiceAware<EntityManagerFactory> emfAware)
			throws Exception {

		assertFalse(emfAware.isEmpty());
		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);

		ClassDescriptor personDescriptor = server.getDescriptorForAlias(personEClass.getName());
		assertNotNull(personDescriptor);

		// Persist test data via EntityManager
		EStructuralFeature nameFeature = personEClass.getEStructuralFeature("stringDefault");
		assertNotNull(nameFeature);

		EObject person1 = (EObject) personDescriptor.getInstantiationPolicy().buildNewInstance();
		person1.eSet(nameFeature, "Alice");
		EObject person2 = (EObject) personDescriptor.getInstantiationPolicy().buildNewInstance();
		person2.eSet(nameFeature, "Bob");

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(person1);
			em.persist(person2);
			em.getTransaction().commit();
			em.clear();
		} catch (Exception e) {
			fail("Failed to persist test data", e);
		}

		// Load via JPAResource
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getProtocolToFactoryMap().put("jpa", new JPAResourceFactory(emf));

		Resource resource = rs.createResource(URI.createURI("jpa://test/Person"));
		assertNotNull(resource);
		assertTrue(resource instanceof JPAResourceImpl);

		resource.load(null);
		assertFalse(resource.getContents().isEmpty());
		assertEquals(2, resource.getContents().size());
	}

	@Test
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testCountAndExist(
			@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 7000) ServiceAware<EntityManagerFactory> emfAware)
			throws Exception {

		assertFalse(emfAware.isEmpty());
		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);
		ClassDescriptor personDescriptor = server.getDescriptorForAlias(personEClass.getName());
		assertNotNull(personDescriptor);

		try (JPAResourceImpl resource = new JPAResourceImpl(URI.createURI("jpa://test/Person"), emf)) {
			// Initially empty
			assertEquals(0, resource.count());
			assertFalse(resource.exist());

			// Persist one entity
			EObject person = (EObject) personDescriptor.getInstantiationPolicy().buildNewInstance();
			person.eSet(personEClass.getEStructuralFeature("stringDefault"), "Charlie");

			try (EntityManager em = emf.createEntityManager()) {
				em.getTransaction().begin();
				em.persist(person);
				em.getTransaction().commit();
			}

			// Now count and exist should reflect the data
			assertEquals(1, resource.count());
			assertTrue(resource.exist());
		}
	}

	@Test
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testGetEObjectResolvesFragment(
			@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 7000) ServiceAware<EntityManagerFactory> emfAware)
			throws Exception {

		assertFalse(emfAware.isEmpty());
		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);
		ClassDescriptor personDescriptor = server.getDescriptorForAlias(personEClass.getName());
		assertNotNull(personDescriptor);

		// Persist entity
		EObject person = (EObject) personDescriptor.getInstantiationPolicy().buildNewInstance();
		person.eSet(personEClass.getEStructuralFeature("stringDefault"), "Diana");

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(person);
			em.getTransaction().commit();
		}

		String id = EcoreUtil.getID(person);
		assertNotNull(id, "Person should have an ID after persist");

		// Resolve via fragment
		try (JPAResourceImpl resource = new JPAResourceImpl(URI.createURI("jpa://test/Person"), emf)) {
			String fragment = "//address/id/" + id;
			EObject resolved = resource.getEObject(fragment);

			assertNotNull(resolved, "Should resolve entity from fragment");
			assertEquals("Diana", resolved.eGet(personEClass.getEStructuralFeature("stringDefault")));
		}
	}

	@Test
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testSaveEntitiesViaResource(
			@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 7000) ServiceAware<EntityManagerFactory> emfAware)
			throws Exception {

		assertFalse(emfAware.isEmpty());
		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);
		ClassDescriptor personDescriptor = server.getDescriptorForAlias(personEClass.getName());
		assertNotNull(personDescriptor);

		// Create entities via descriptor
		EObject person1 = (EObject) personDescriptor.getInstantiationPolicy().buildNewInstance();
		person1.eSet(personEClass.getEStructuralFeature("stringDefault"), "SaveTest1");
		EObject person2 = (EObject) personDescriptor.getInstantiationPolicy().buildNewInstance();
		person2.eSet(personEClass.getEStructuralFeature("stringDefault"), "SaveTest2");

		// Persist via EntityManager first (to get IDs assigned)
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(person1);
			em.persist(person2);
			em.getTransaction().commit();
		}

		// Modify detached entity and save via Resource (tests merge)
		person1.eSet(personEClass.getEStructuralFeature("stringDefault"), "SaveTest1_Updated");
		try (JPAResourceImpl resource = new JPAResourceImpl(URI.createURI("jpa://test/Person"), emf)) {
			resource.getContents().add(person1);
			resource.save(null);
		}

		// Verify the update was persisted
		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(person1);
			EObject found = (EObject) em.find(personDescriptor.getJavaClass(), id);
			assertNotNull(found);
			assertEquals("SaveTest1_Updated", found.eGet(personEClass.getEStructuralFeature("stringDefault")));
		}
	}

	@Test
	@TestAnnotations.DefaultEPersistenceConfiguration
	public void testDeleteEntitiesViaResource(
			@InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
			@InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
			@InjectService(timeout = 7000) ServiceAware<EntityManagerFactory> emfAware)
			throws Exception {

		assertFalse(emfAware.isEmpty());
		EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);
		ClassDescriptor personDescriptor = server.getDescriptorForAlias(personEClass.getName());
		assertNotNull(personDescriptor);

		// Persist test data
		EObject person = (EObject) personDescriptor.getInstantiationPolicy().buildNewInstance();
		person.eSet(personEClass.getEStructuralFeature("stringDefault"), "ToDelete");

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(person);
			em.getTransaction().commit();
		}

		String id = EcoreUtil.getID(person);
		assertNotNull(id);

		// Verify it exists and delete via Resource
		try (JPAResourceImpl resource = new JPAResourceImpl(URI.createURI("jpa://test/Person"), emf)) {
			assertTrue(resource.exist());

			resource.getContents().add(person);
			resource.delete(null);

			assertTrue(resource.getContents().isEmpty(), "Resource contents should be empty after delete");
		}

		// Verify it's gone
		try (EntityManager em = emf.createEntityManager()) {
			EObject found = (EObject) em.find(personDescriptor.getJavaClass(), id);
			assertTrue(found == null, "Entity should be deleted from database");
		}
	}
}
