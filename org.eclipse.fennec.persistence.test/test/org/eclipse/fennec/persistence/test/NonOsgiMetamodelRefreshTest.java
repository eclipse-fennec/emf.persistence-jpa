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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.EntityType;

/**
 * Dynamically registered entity types must surface in the JPA {@link jakarta.persistence.metamodel.Metamodel}.
 * <p>
 * The metamodel is created and initialized during persistence-unit deployment — BEFORE
 * {@code EDynamicHelper.addETypes(...)} adds the dynamic descriptors to the session. Without
 * an explicit refresh the metamodel stays empty, which breaks every metamodel consumer, most
 * importantly the Jakarta Criteria API ({@code CriteriaQuery.from(...)} throws for types the
 * metamodel does not know).
 */
class NonOsgiMetamodelRefreshTest extends NonOsgiPersistenceTestBase {

	private EClass personEClass;

	@BeforeEach
	void setUp() {
		EPackage modelPackage = loadEcore("data/model.ecore");
		personEClass = (EClass) modelPackage.getEClassifier("Person");
		assertNotNull(personEClass);
		bootstrapPersistence("metamodel-refresh", List.of(personEClass));
	}

	@Test
	@DisplayName("addETypes surfaces dynamic types in the JPA metamodel")
	void metamodelContainsDynamicTypes() {
		assertThat(emf.getMetamodel().getEntities())
				.as("dynamic entity types must be visible in the JPA metamodel")
				.extracting(EntityType::getName)
				.contains(personEClass.getName());
	}

	@Test
	@DisplayName("Criteria API works against dynamic types (from/where/getResultList)")
	void criteriaQueryOverDynamicType() {
		ClassDescriptor descriptor = serverSession.getDescriptorForAlias(personEClass.getName());
		EObject person = (EObject) descriptor.getInstantiationPolicy().buildNewInstance();
		person.eSet(personEClass.getEStructuralFeature("id"), "p-4711");
		person.eSet(personEClass.getEStructuralFeature("stringDefault"), "Ada");

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(person);
			em.getTransaction().commit();
		}

		try (EntityManager em = emf.createEntityManager()) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Object> query = cb.createQuery(Object.class);
			Root<?> root = query.from(em.getMetamodel().entity(descriptor.getJavaClass()));
			query.select(root).where(cb.equal(root.get("stringDefault"), "Ada"));
			List<Object> result = em.createQuery(query).getResultList();

			assertThat(result).hasSize(1);
			assertThat(((EObject) result.get(0))
					.eGet(personEClass.getEStructuralFeature("stringDefault"))).isEqualTo("Ada");
		}
	}
}
