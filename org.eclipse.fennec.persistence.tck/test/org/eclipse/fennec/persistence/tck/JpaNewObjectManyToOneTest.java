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
package org.eclipse.fennec.persistence.tck;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.TimeZone;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * A new plain object whose bidirectional many-to-one points at an existing row writes the
 * foreign key (issue #309). The update path — setting the reference on an existing object —
 * already did; the insert path wrote {@code NULL}.
 *
 * @author Mark Hoffmann
 * @since 24.09.2026
 */
class JpaNewObjectManyToOneTest {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static final String PU_NAME = "newm2o";

	private EPackage ePackage;
	private EClass customerClass;
	private EClass orderClass;
	private EAttribute customerId;
	private EAttribute orderId;
	private EReference orderCustomer;
	private EntityManagerFactory emf;

	@BeforeEach
	void setUp() throws Exception {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		customerClass = ecore.createEClass();
		customerClass.setName("M2oCustomer");
		customerId = id(customerClass);
		orderClass = ecore.createEClass();
		orderClass.setName("M2oOrder");
		orderId = id(orderClass);

		orderCustomer = ecore.createEReference();
		orderCustomer.setName("customer");
		orderCustomer.setEType(customerClass);
		orderClass.getEStructuralFeatures().add(orderCustomer);
		EReference customerOrders = ecore.createEReference();
		customerOrders.setName("orders");
		customerOrders.setEType(orderClass);
		customerOrders.setUpperBound(-1);
		customerClass.getEStructuralFeatures().add(customerOrders);
		orderCustomer.setEOpposite(customerOrders);
		customerOrders.setEOpposite(orderCustomer);

		ePackage = ecore.createEPackage();
		ePackage.setName("newm2o");
		ePackage.setNsURI("urn:newm2o:test/1.0");
		ePackage.setNsPrefix("newm2o");
		ePackage.getEClassifiers().add(customerClass);
		ePackage.getEClassifiers().add(orderClass);

		emf = JpaTckSupport.bootstrap(PU_NAME, List.<EClassifier>of(customerClass, orderClass));
		clear();
		Resource customers = resourceSet().createResource(uri("M2oCustomer"));
		for (long id = 1; id <= 2; id++) {
			EObject customer = EcoreUtil.create(customerClass);
			customer.eSet(customerId, id);
			customers.getContents().add(customer);
		}
		customers.save(null);
	}

	@AfterEach
	void tearDown() throws Exception {
		if (nonNull(emf)) {
			clear();
			emf.close();
			emf = null;
		}
	}

	@Test
	void aNewOrderPointingAtAnExistingCustomerWritesTheForeignKey() throws Exception {
		ResourceSet resourceSet = resourceSet();
		EObject customer = resourceSet.getEObject(URI.createURI(uri("M2oCustomer") + "#2"), true);
		EObject order = EcoreUtil.create(orderClass);
		order.eSet(orderId, 13L);
		order.eSet(orderCustomer, customer);
		Resource orders = resourceSet.createResource(uri("M2oOrder"));
		orders.getContents().add(order);

		orders.save(null);

		assertThat(customerIdOfOrder(13L)).isEqualTo(2L);
	}

	@Test
	void settingTheReferenceOnAnExistingOrderWritesTheForeignKey() throws Exception {
		Resource orders = resourceSet().createResource(uri("M2oOrder"));
		EObject order = EcoreUtil.create(orderClass);
		order.eSet(orderId, 12L);
		orders.getContents().add(order);
		orders.save(null);

		ResourceSet resourceSet = resourceSet();
		EObject customer = resourceSet.getEObject(URI.createURI(uri("M2oCustomer") + "#2"), true);
		EObject stored = resourceSet.getEObject(URI.createURI(uri("M2oOrder") + "#12"), true);
		stored.eSet(orderCustomer, customer);
		stored.eResource().save(null);

		assertThat(customerIdOfOrder(12L)).isEqualTo(2L);
	}

	private Long customerIdOfOrder(long id) {
		emf.getCache().evictAll();
		EObject order = resourceSet().getEObject(URI.createURI(uri("M2oOrder") + "#" + id), true);
		assertThat(order).as("order %s is stored", id).isNotNull();
		EObject customer = (EObject) order.eGet(orderCustomer);
		return customer == null ? null : (Long) customer.eGet(customerId);
	}

	private EAttribute id(EClass eClass) {
		EAttribute id = EcoreFactory.eINSTANCE.createEAttribute();
		id.setName("id");
		id.setEType(EcorePackage.Literals.ELONG);
		id.setID(true);
		eClass.getEStructuralFeatures().add(id);
		return id;
	}

	/** Plain SQL, so the cleanup does not depend on the delete path under test elsewhere. */
	private void clear() {
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.createNativeQuery("DELETE FROM M2OORDER").executeUpdate();
			em.createNativeQuery("DELETE FROM M2OCUSTOMER").executeUpdate();
			em.getTransaction().commit();
		}
		emf.getCache().evictAll();
	}

	private ResourceSet resourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("jpa", new JPAResourceFactory(emf));
		return resourceSet;
	}

	private URI uri(String type) {
		return URI.createURI("jpa://" + PU_NAME + "/" + type);
	}
}
