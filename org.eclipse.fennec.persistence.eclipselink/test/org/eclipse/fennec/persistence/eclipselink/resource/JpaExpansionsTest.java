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
package org.eclipse.fennec.persistence.eclipselink.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.fennec.persistence.eclipselink.query.JpaExpandPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

/**
 * The in-place resolution of {@link JpaExpansions} on a path that comes back over a
 * bidirectional reference: {@code Datastream.sensor} / {@code Sensor.datastreams}, to-one (issue
 * #413), and {@code HistoricalLocation.locations} / {@code Location.historicalLocations},
 * many-to-many (issue #415).
 */
class JpaExpansionsTest {

	private EClass sensorClass;
	private EClass datastreamClass;
	private EAttribute sensorId;
	private EAttribute datastreamId;
	private EReference sensorDatastreams;
	private EReference datastreamSensor;
	private ResourceSet resourceSet;
	private Resource datastreams;

	@BeforeEach
	void setUp() {
		EcoreFactory factory = EcoreFactory.eINSTANCE;
		EPackage pkg = factory.createEPackage();
		pkg.setName("sta413");
		pkg.setNsURI("urn:test:sta413");
		pkg.setNsPrefix("sta413");
		sensorClass = factory.createEClass();
		sensorClass.setName("Sensor");
		datastreamClass = factory.createEClass();
		datastreamClass.setName("Datastream");
		sensorId = idAttribute(sensorClass);
		datastreamId = idAttribute(datastreamClass);
		sensorDatastreams = factory.createEReference();
		sensorDatastreams.setName("datastreams");
		sensorDatastreams.setEType(datastreamClass);
		sensorDatastreams.setUpperBound(-1);
		datastreamSensor = factory.createEReference();
		datastreamSensor.setName("sensor");
		datastreamSensor.setEType(sensorClass);
		sensorDatastreams.setEOpposite(datastreamSensor);
		datastreamSensor.setEOpposite(sensorDatastreams);
		sensorClass.getEStructuralFeatures().add(sensorDatastreams);
		datastreamClass.getEStructuralFeatures().add(datastreamSensor);
		pkg.getEClassifiers().addAll(List.of(sensorClass, datastreamClass));

		resourceSet = new ResourceSetImpl();
		datastreams = new ResourceImpl(URI.createURI("jpa://sta/Datastream"));
		resourceSet.getResources().add(datastreams);
	}

	private static EAttribute idAttribute(EClass owner) {
		EAttribute id = EcoreFactory.eINSTANCE.createEAttribute();
		id.setName("id");
		id.setEType(EcorePackage.Literals.ESTRING);
		id.setID(true);
		owner.getEStructuralFeatures().add(id);
		return id;
	}

	private EObject datastream(String id) {
		EObject datastream = EcoreUtil.create(datastreamClass);
		datastream.eSet(datastreamId, id);
		return datastream;
	}

	/** A proxy of a datastream of {@link #datastreams}, the way the lazy list holds it. */
	private EObject proxyOf(EObject datastream) {
		EObject proxy = EcoreUtil.create(datastreamClass);
		((InternalEObject) proxy).eSetProxyURI(EcoreUtil.getURI(datastream));
		return proxy;
	}

	private List<EObject> expand(EObject root) {
		JpaExpandPlan plan = new JpaExpandPlan(List.of(datastreamSensor, sensorDatastreams), null,
				Map.of());
		JpaExpansions.resolve(List.of(root), List.of(plan), mock(EntityManager.class), resourceSet);
		@SuppressWarnings("unchecked")
		InternalEList<EObject> list = (InternalEList<EObject>) ((EObject) root.eGet(datastreamSensor))
				.eGet(sensorDatastreams);
		return list.basicList();
	}

	@Test
	void aProxyOfTheRootItselfIsDroppedInsteadOfReplaced() {
		// the root lives in its resource, so its proxy resolves to the very same instance:
		// replacing the proxy threw "The 'no duplicates' constraint is violated"
		EObject root = datastream("D");
		EObject other = datastream("E");
		datastreams.getContents().addAll(List.of(root, other));
		EObject sensor = EcoreUtil.create(sensorClass);
		sensor.eSet(sensorId, "S");
		@SuppressWarnings("unchecked")
		List<EObject> built = (List<EObject>) sensor.eGet(sensorDatastreams);
		built.add(proxyOf(root));
		built.add(proxyOf(other));
		// resolving the level before: the opposite adds the root next to its proxy
		root.eSet(datastreamSensor, sensor);

		List<EObject> entries = expand(root);

		assertThat(entries).containsExactly(other, root);
		assertThat(entries).noneMatch(EObject::eIsProxy);
	}

	@Test
	void aDetachedRootStaysAndTheCopyTheProxyResolvedToGoes() {
		// a query root outside any resource: its proxy resolves to a second instance
		EObject root = datastream("D");
		EObject stored = datastream("D");
		datastreams.getContents().add(stored);
		EObject sensor = EcoreUtil.create(sensorClass);
		sensor.eSet(sensorId, "S");
		@SuppressWarnings("unchecked")
		List<EObject> built = (List<EObject>) sensor.eGet(sensorDatastreams);
		built.add(proxyOf(stored));
		root.eSet(datastreamSensor, sensor);

		List<EObject> entries = expand(root);

		assertThat(entries).containsExactly(root);
	}

	@Test
	void entriesOfOtherIdsResolveAsBefore() {
		// the root is an anchor, but the proxy names another datastream: it resolves in place
		EObject stored = datastream("E");
		datastreams.getContents().add(stored);
		EObject sensor = EcoreUtil.create(sensorClass);
		sensor.eSet(sensorId, "S");
		@SuppressWarnings("unchecked")
		List<EObject> built = (List<EObject>) sensor.eGet(sensorDatastreams);
		built.add(proxyOf(stored));
		EObject root = datastream("D");
		root.eSet(datastreamSensor, sensor);

		List<EObject> entries = expand(root);

		assertThat(entries).containsExactly(stored, root);
	}

	@Test
	void aManyToManyPathBackHoldsTheRootOnce() {
		// issue #415: HistoricalLocation(H) -> locations -> historicalLocations
		EcoreFactory factory = EcoreFactory.eINSTANCE;
		EPackage pkg = factory.createEPackage();
		pkg.setName("sta415");
		pkg.setNsURI("urn:test:sta415");
		pkg.setNsPrefix("sta415");
		EClass locationClass = factory.createEClass();
		locationClass.setName("Location");
		EClass historicalClass = factory.createEClass();
		historicalClass.setName("HistoricalLocation");
		EAttribute locationId = idAttribute(locationClass);
		EAttribute historicalId = idAttribute(historicalClass);
		EReference historicalLocations = factory.createEReference();
		historicalLocations.setName("historicalLocations");
		historicalLocations.setEType(historicalClass);
		historicalLocations.setUpperBound(-1);
		EReference locations = factory.createEReference();
		locations.setName("locations");
		locations.setEType(locationClass);
		locations.setUpperBound(-1);
		historicalLocations.setEOpposite(locations);
		locations.setEOpposite(historicalLocations);
		locationClass.getEStructuralFeatures().add(historicalLocations);
		historicalClass.getEStructuralFeatures().add(locations);
		pkg.getEClassifiers().addAll(List.of(locationClass, historicalClass));

		Resource historical = new ResourceImpl(URI.createURI("jpa://sta/HistoricalLocation"));
		resourceSet.getResources().add(historical);
		EObject root = EcoreUtil.create(historicalClass);
		root.eSet(historicalId, "H");
		EObject other = EcoreUtil.create(historicalClass);
		other.eSet(historicalId, "I");
		historical.getContents().addAll(List.of(root, other));
		EObject location = EcoreUtil.create(locationClass);
		location.eSet(locationId, "L");
		@SuppressWarnings("unchecked")
		List<EObject> built = (List<EObject>) location.eGet(historicalLocations);
		for (EObject stored : List.of(root, other)) {
			EObject proxy = EcoreUtil.create(historicalClass);
			((InternalEObject) proxy).eSetProxyURI(EcoreUtil.getURI(stored));
			built.add(proxy);
		}
		// resolving the level before: the opposite adds the root next to its proxy
		@SuppressWarnings("unchecked")
		List<EObject> rootLocations = (List<EObject>) root.eGet(locations);
		rootLocations.add(location);

		JpaExpandPlan plan = new JpaExpandPlan(List.of(locations, historicalLocations), null, Map.of());
		JpaExpansions.resolve(List.of(root), List.of(plan), mock(EntityManager.class), resourceSet);

		@SuppressWarnings("unchecked")
		List<EObject> entries = ((InternalEList<EObject>) location.eGet(historicalLocations)).basicList();
		assertThat(entries).containsExactly(other, root);
	}
}
