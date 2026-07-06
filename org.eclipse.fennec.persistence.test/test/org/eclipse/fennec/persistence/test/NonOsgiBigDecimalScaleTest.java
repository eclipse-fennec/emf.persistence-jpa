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

import java.math.BigDecimal;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

/**
 * {@code EBigDecimal} attributes must keep their fraction digits through a database
 * round trip.
 * <p>
 * Without an explicit precision/scale the generated DDL falls back to the database default
 * for NUMERIC — scale 0 on H2 (and most databases) — so values like {@code 2.80} were
 * silently rounded to {@code 3} on insert: data corruption, and comparisons such as
 * {@code price < 3.00} returned wrong results.
 */
class NonOsgiBigDecimalScaleTest extends NonOsgiPersistenceTestBase {

	private EClass productClass;
	private EAttribute idAttribute;
	private EAttribute priceAttribute;

	@BeforeEach
	void setUpModel() {
		EPackage shop = EcoreFactory.eINSTANCE.createEPackage();
		shop.setName("decimalshop");
		shop.setNsURI("http://decimalshop");
		shop.setNsPrefix("ds");

		productClass = EcoreFactory.eINSTANCE.createEClass();
		productClass.setName("DecimalProduct");
		shop.getEClassifiers().add(productClass);

		idAttribute = EcoreFactory.eINSTANCE.createEAttribute();
		idAttribute.setName("id");
		idAttribute.setEType(EcorePackage.Literals.ESTRING);
		idAttribute.setID(true);
		idAttribute.setLowerBound(1);
		productClass.getEStructuralFeatures().add(idAttribute);

		priceAttribute = EcoreFactory.eINSTANCE.createEAttribute();
		priceAttribute.setName("price");
		priceAttribute.setEType(EcorePackage.Literals.EBIG_DECIMAL);
		productClass.getEStructuralFeatures().add(priceAttribute);

		bootstrapPersistence("decimalshop", List.of(productClass));
	}

	@Test
	@DisplayName("BigDecimal fraction digits survive the DB round trip (no scale-0 rounding)")
	void bigDecimalRoundTripKeepsFraction() {
		ClassDescriptor descriptor = serverSession.getDescriptorForAlias(productClass.getName());
		EObject bread = (EObject) descriptor.getInstantiationPolicy().buildNewInstance();
		bread.eSet(idAttribute, "p1");
		bread.eSet(priceAttribute, new BigDecimal("2.80"));

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(bread);
			em.getTransaction().commit();
		}

		try (EntityManager em = emf.createEntityManager()) {
			EObject found = (EObject) em.find(descriptor.getJavaClass(), "p1");
			BigDecimal price = (BigDecimal) found.eGet(priceAttribute);
			assertThat(price).isNotNull();
			assertThat(price.compareTo(new BigDecimal("2.80")))
					.as("2.80 must not be rounded to %s by a scale-0 column", price)
					.isZero();

			List<?> cheap = em.createQuery(
					"SELECT p FROM " + productClass.getName() + " p WHERE p.price < 3.0",
					descriptor.getJavaClass()).getResultList();
			assertThat(cheap).as("price 2.80 must match price < 3.0").hasSize(1);
		}
	}
}
