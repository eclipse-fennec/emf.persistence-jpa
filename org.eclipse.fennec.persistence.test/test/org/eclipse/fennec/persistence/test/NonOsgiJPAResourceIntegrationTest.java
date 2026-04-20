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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.Options;
import org.eclipse.fennec.persistence.eclipselink.resource.JPAResourceFactory;
import org.eclipse.fennec.persistence.eclipselink.resource.JPAResourceImpl;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

/**
 * Non-OSGi port of {@code JPAResourceIntegrationTest}. Exercises the EMF
 * {@link JPAResourceImpl} API (load, save, delete, count/exist, fragment
 * resolution) against in-memory H2 via {@link NonOsgiPersistenceTestBase}.
 */
class NonOsgiJPAResourceIntegrationTest extends NonOsgiPersistenceTestBase {

	private EPackage modelPackage;
	private EClass personEClass;

	@BeforeEach
	void setUp() {
		modelPackage = loadEcore("data/model.ecore");
		personEClass = (EClass) modelPackage.getEClassifier("Person");
		assertNotNull(personEClass);
		bootstrapPersistence("person", List.of(personEClass));
	}

	@Test
	void testLoadEntitiesViaResource() throws Exception {
		ClassDescriptor personDescriptor = serverSession.getDescriptorForAlias(personEClass.getName());
		EStructuralFeature nameFeature = personEClass.getEStructuralFeature("stringDefault");

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

		ResourceSet localRs = new ResourceSetImpl();
		localRs.getResourceFactoryRegistry().getProtocolToFactoryMap().put("jpa", new JPAResourceFactory(emf));

		Resource resource = localRs.createResource(URI.createURI("jpa://person/Person"));
		assertNotNull(resource);
		assertTrue(resource instanceof JPAResourceImpl);

		resource.load(null);
		assertFalse(resource.getContents().isEmpty());
		assertEquals(2, resource.getContents().size());
	}

	@Test
	void testCountAndExist() throws Exception {
		ClassDescriptor personDescriptor = serverSession.getDescriptorForAlias(personEClass.getName());

		try (JPAResourceImpl resource = new JPAResourceImpl(URI.createURI("jpa://person/Person"), emf)) {
			assertEquals(0, resource.count());
			assertFalse(resource.exist());

			EObject person = (EObject) personDescriptor.getInstantiationPolicy().buildNewInstance();
			person.eSet(personEClass.getEStructuralFeature("stringDefault"), "Charlie");

			try (EntityManager em = emf.createEntityManager()) {
				em.getTransaction().begin();
				em.persist(person);
				em.getTransaction().commit();
			}

			assertEquals(1, resource.count());
			assertTrue(resource.exist());
		}
	}

	@Test
	void testGetEObjectResolvesFragment() throws Exception {
		ClassDescriptor personDescriptor = serverSession.getDescriptorForAlias(personEClass.getName());

		EObject person = (EObject) personDescriptor.getInstantiationPolicy().buildNewInstance();
		person.eSet(personEClass.getEStructuralFeature("stringDefault"), "Diana");

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(person);
			em.getTransaction().commit();
		}

		String id = EcoreUtil.getID(person);
		assertNotNull(id, "Person should have an ID after persist");

		try (JPAResourceImpl resource = new JPAResourceImpl(URI.createURI("jpa://person/Person"), emf)) {
			String fragment = "//address/id/" + id;
			EObject resolved = resource.getEObject(fragment);
			assertNotNull(resolved, "Should resolve entity from fragment");
			assertEquals("Diana", resolved.eGet(personEClass.getEStructuralFeature("stringDefault")));
		}
	}

	@Test
	void testSaveEntitiesViaResource() throws Exception {
		ClassDescriptor personDescriptor = serverSession.getDescriptorForAlias(personEClass.getName());

		EObject person1 = (EObject) personDescriptor.getInstantiationPolicy().buildNewInstance();
		person1.eSet(personEClass.getEStructuralFeature("stringDefault"), "SaveTest1");
		EObject person2 = (EObject) personDescriptor.getInstantiationPolicy().buildNewInstance();
		person2.eSet(personEClass.getEStructuralFeature("stringDefault"), "SaveTest2");

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(person1);
			em.persist(person2);
			em.getTransaction().commit();
		}

		person1.eSet(personEClass.getEStructuralFeature("stringDefault"), "SaveTest1_Updated");
		try (JPAResourceImpl resource = new JPAResourceImpl(URI.createURI("jpa://person/Person"), emf)) {
			resource.getContents().add(person1);
			resource.save(null);
		}

		try (EntityManager em = emf.createEntityManager()) {
			String id = EcoreUtil.getID(person1);
			EObject found = em.find(personDescriptor.getJavaClass(), id);
			assertNotNull(found);
			assertEquals("SaveTest1_Updated", found.eGet(personEClass.getEStructuralFeature("stringDefault")));
		}
	}

	@Test
	void testLoadPaginated() throws Exception {
		ClassDescriptor personDescriptor = serverSession.getDescriptorForAlias(personEClass.getName());
		EStructuralFeature nameFeature = personEClass.getEStructuralFeature("stringDefault");

		int totalCount = 25;
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			for (int i = 0; i < totalCount; i++) {
				EObject person = (EObject) personDescriptor.getInstantiationPolicy().buildNewInstance();
				person.eSet(nameFeature, "Paginated_" + i);
				em.persist(person);
			}
			em.getTransaction().commit();
		}

		try (JPAResourceImpl resource = new JPAResourceImpl(URI.createURI("jpa://person/Person"), emf)) {
			Map<Object, Object> options = new HashMap<>();
			options.put(Options.OPTION_PAGE_SIZE, 10);
			resource.load(options);
			assertEquals(totalCount, resource.getContents().size(),
					"Paginated load should return all entities in total");
		}
	}

	@Test
	void testLoadPaginatedPageSizeEqualsTotal() throws Exception {
		ClassDescriptor personDescriptor = serverSession.getDescriptorForAlias(personEClass.getName());
		EStructuralFeature nameFeature = personEClass.getEStructuralFeature("stringDefault");

		int totalCount = 10;
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			for (int i = 0; i < totalCount; i++) {
				EObject person = (EObject) personDescriptor.getInstantiationPolicy().buildNewInstance();
				person.eSet(nameFeature, "Exact_" + i);
				em.persist(person);
			}
			em.getTransaction().commit();
		}

		try (JPAResourceImpl resource = new JPAResourceImpl(URI.createURI("jpa://person/Person"), emf)) {
			Map<Object, Object> options = new HashMap<>();
			options.put(Options.OPTION_PAGE_SIZE, totalCount);
			resource.load(options);
			assertEquals(totalCount, resource.getContents().size());
		}
	}

	@Test
	void testLoadPaginationZeroDisablesPagination() throws Exception {
		ClassDescriptor personDescriptor = serverSession.getDescriptorForAlias(personEClass.getName());
		EStructuralFeature nameFeature = personEClass.getEStructuralFeature("stringDefault");

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			for (int i = 0; i < 5; i++) {
				EObject person = (EObject) personDescriptor.getInstantiationPolicy().buildNewInstance();
				person.eSet(nameFeature, "NoPaging_" + i);
				em.persist(person);
			}
			em.getTransaction().commit();
		}

		try (JPAResourceImpl resource = new JPAResourceImpl(URI.createURI("jpa://person/Person"), emf)) {
			Map<Object, Object> options = new HashMap<>();
			options.put(Options.OPTION_PAGE_SIZE, 0);
			resource.load(options);
			assertEquals(5, resource.getContents().size());
		}
	}

	@Test
	void testSaveWithCacheNewObjectsFalse() throws Exception {
		ClassDescriptor personDescriptor = serverSession.getDescriptorForAlias(personEClass.getName());
		EStructuralFeature nameFeature = personEClass.getEStructuralFeature("stringDefault");

		EObject person = (EObject) personDescriptor.getInstantiationPolicy().buildNewInstance();
		person.eSet(nameFeature, "CacheOff");

		try (JPAResourceImpl resource = new JPAResourceImpl(URI.createURI("jpa://person/Person"), emf)) {
			resource.getContents().add(person);
			Map<Object, Object> options = new HashMap<>();
			options.put(Options.OPTION_CACHE_NEW_OBJECTS, Boolean.FALSE);
			resource.save(options);
		}

		try (EntityManager em = emf.createEntityManager()) {
			long count = em.createQuery(
					"SELECT COUNT(e) FROM " + personDescriptor.getAlias() + " e", Long.class)
					.getSingleResult();
			assertEquals(1L, count, "Entity should still be persisted when cache-new-objects=false");
		}
	}

	@Test
	void testDeleteEntitiesViaResource() throws Exception {
		ClassDescriptor personDescriptor = serverSession.getDescriptorForAlias(personEClass.getName());

		EObject person = (EObject) personDescriptor.getInstantiationPolicy().buildNewInstance();
		person.eSet(personEClass.getEStructuralFeature("stringDefault"), "ToDelete");

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(person);
			em.getTransaction().commit();
		}

		String id = EcoreUtil.getID(person);

		try (JPAResourceImpl resource = new JPAResourceImpl(URI.createURI("jpa://person/Person"), emf)) {
			assertTrue(resource.exist());
			resource.getContents().add(person);
			resource.delete(null);
			assertTrue(resource.getContents().isEmpty(), "Resource contents should be empty after delete");
		}

		try (EntityManager em = emf.createEntityManager()) {
			EObject found = em.find(personDescriptor.getJavaClass(), id);
			assertTrue(found == null, "Entity should be deleted from database");
		}
	}
}
