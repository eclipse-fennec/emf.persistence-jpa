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
import org.eclipse.fennec.persistence.resource.PersistenceResource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Deleting objects whose many-to-one points at a target with the opposite one-to-many removes
 * their rows (issue #326). The merge in {@code delete} made the target managed with its opposite
 * collection still holding the object, and the persist cascade of non-containment references
 * re-persisted the removed object at flush — the delete was silently undone.
 *
 * @author Mark Hoffmann
 * @since 24.09.2026
 */
class JpaDeleteBidirectionalTest {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static final String PU_NAME = "delbidi";

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
		customerClass.setName("DbCustomer");
		customerId = id(customerClass);
		orderClass = ecore.createEClass();
		orderClass.setName("DbOrder");
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
		ePackage.setName("delbidi");
		ePackage.setNsURI("urn:delbidi:test/1.0");
		ePackage.setNsPrefix("delbidi");
		ePackage.getEClassifiers().add(customerClass);
		ePackage.getEClassifiers().add(orderClass);

		emf = JpaTckSupport.bootstrap(PU_NAME, List.<EClassifier>of(customerClass, orderClass));
		clearWithSql();

		ResourceSet resourceSet = resourceSet();
		Resource customers = resourceSet.createResource(uri("DbCustomer"));
		EObject customer = EcoreUtil.create(customerClass);
		customer.eSet(customerId, 1L);
		customers.getContents().add(customer);
		customers.save(null);

		Resource orders = resourceSet.createResource(uri("DbOrder"));
		for (long id = 11; id <= 12; id++) {
			EObject order = EcoreUtil.create(orderClass);
			order.eSet(orderId, id);
			order.eSet(orderCustomer, customer);
			orders.getContents().add(order);
		}
		orders.save(null);
	}

	@AfterEach
	void tearDown() {
		if (nonNull(emf)) {
			clearWithSql();
			emf.close();
			emf = null;
		}
	}

	@Test
	void deletingTheOrdersRemovesTheirRows() throws Exception {
		assertThat(count("DBORDER")).isEqualTo(2);

		deleteAll("DbOrder");

		assertThat(count("DBORDER")).isZero();
	}

	@Test
	void afterTheOrdersTheirCustomerCanBeDeleted() throws Exception {
		deleteAll("DbOrder");

		deleteAll("DbCustomer");

		assertThat(count("DBORDER")).isZero();
		assertThat(count("DBCUSTOMER")).isZero();
	}

	@Test
	void deletingOneFragmentResolvedOrderKeepsTheOther() throws Exception {
		EObject order = resourceSet().getEObject(URI.createURI(uri("DbOrder") + "#11"), true);

		((PersistenceResource) order.eResource()).delete(null);

		assertThat(count("DBORDER")).isEqualTo(1);
	}

	private void deleteAll(String type) throws Exception {
		Resource resource = resourceSet().createResource(uri(type));
		resource.load(null);
		((PersistenceResource) resource).delete(null);
	}

	private long count(String table) {
		emf.getCache().evictAll();
		try (EntityManager em = emf.createEntityManager()) {
			return ((Number) em.createNativeQuery("SELECT COUNT(*) FROM " + table).getSingleResult()).longValue();
		}
	}

	/** Plain SQL, so setup and cleanup do not depend on the delete path under test. */
	private void clearWithSql() {
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.createNativeQuery("DELETE FROM DBORDER").executeUpdate();
			em.createNativeQuery("DELETE FROM DBCUSTOMER").executeUpdate();
			em.getTransaction().commit();
		}
		emf.getCache().evictAll();
	}

	private EAttribute id(EClass eClass) {
		EAttribute id = EcoreFactory.eINSTANCE.createEAttribute();
		id.setName("id");
		id.setEType(EcorePackage.Literals.ELONG);
		id.setID(true);
		eClass.getEStructuralFeatures().add(id);
		return id;
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
