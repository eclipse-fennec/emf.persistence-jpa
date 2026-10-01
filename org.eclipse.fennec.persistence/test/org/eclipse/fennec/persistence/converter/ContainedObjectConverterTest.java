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
package org.eclipse.fennec.persistence.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

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
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ContainedObjectConverter} (issue #363): a containment subtree stored as one
 * XMI value of its parent.
 * <p>
 * The model is dynamic and registered nowhere globally — the case of a package deployed as a
 * dynamic schema, which the decode must still resolve. A volatile feature cannot hold a value in a
 * dynamic model without a setting delegate, so the volatile persisted form is covered end to end
 * by the GeoJSON round trips of the TCK instead.
 */
class ContainedObjectConverterTest {

	private EClass shape;
	private EClass geometry;
	private EClass point;
	private EAttribute label;
	private EAttribute data;
	private EAttribute transientCache;
	private EReference parts;
	private EReference link;

	private final ContainedObjectConverter single = new ContainedObjectConverter(false);
	private final ContainedObjectConverter many = new ContainedObjectConverter(true);

	@BeforeEach
	void setUp() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		EPackage ePackage = ecore.createEPackage();
		ePackage.setName("contained");
		ePackage.setNsPrefix("contained");
		ePackage.setNsURI("urn:contained:test:" + System.nanoTime());

		geometry = ecore.createEClass();
		geometry.setName("Geometry");
		geometry.setAbstract(true);
		label = ecore.createEAttribute();
		label.setName("label");
		label.setEType(EcorePackage.Literals.ESTRING);
		geometry.getEStructuralFeatures().add(label);

		point = ecore.createEClass();
		point.setName("Point");
		point.getESuperTypes().add(geometry);
		data = ecore.createEAttribute();
		data.setName("data");
		data.setEType(EcorePackage.Literals.ESTRING);
		transientCache = ecore.createEAttribute();
		transientCache.setName("cache");
		transientCache.setEType(EcorePackage.Literals.ESTRING);
		transientCache.setTransient(true);
		parts = ecore.createEReference();
		parts.setName("parts");
		parts.setEType(geometry);
		parts.setContainment(true);
		parts.setUpperBound(-1);
		point.getEStructuralFeatures().addAll(List.of(data, transientCache, parts));

		shape = ecore.createEClass();
		shape.setName("Shape");
		link = ecore.createEReference();
		link.setName("link");
		link.setEType(shape);
		point.getEStructuralFeatures().add(link);

		ePackage.getEClassifiers().addAll(List.of(geometry, point, shape));
	}

	@Test
	void namesOneConverterPerCardinalityAndClaimsNoType() {
		assertThat(single.getName()).isEqualTo(ContainedObjectConverter.NAME);
		assertThat(many.getName()).isEqualTo(ContainedObjectConverter.NAME_MANY);
		assertThat(single.isConverterForType(geometry)).isFalse();
		assertThat(single.isConverterForType(EcorePackage.Literals.ESTRING)).isFalse();
		assertThat(single.isLargeValue(geometry)).isTrue();
	}

	@Test
	void aSubtreeRoundTripsWithItsPersistedFeaturesOnly() {
		EObject written = newPoint("outer", "1,2");
		written.eSet(transientCache, "not stored");
		@SuppressWarnings("unchecked")
		List<EObject> children = (List<EObject>) written.eGet(parts);
		children.add(newPoint("inner", "3,4"));

		Object stored = single.convertEMFToValue(geometry, written);
		assertThat(stored).isInstanceOf(String.class);
		EObject read = (EObject) single.convertValueToEMF(geometry, stored);

		assertThat(read.eClass()).as("the subtype comes back, not the declared type").isSameAs(point);
		assertThat(read.eGet(label)).isEqualTo("outer");
		assertThat(read.eGet(data)).isEqualTo("1,2");
		assertThat(read.eGet(transientCache)).as("a transient feature is not part of the value").isNull();
		@SuppressWarnings("unchecked")
		List<EObject> readChildren = (List<EObject>) read.eGet(parts);
		assertThat(readChildren).hasSize(1);
		assertThat(readChildren.get(0).eGet(label)).isEqualTo("inner");
		assertThat(read.eResource()).as("the child is free to go into its parent").isNull();
	}

	@Test
	void encodingLeavesTheChildWithItsContainer() {
		EObject owner = newPoint("owner", "0,0");
		EObject child = newPoint("child", "5,5");
		@SuppressWarnings("unchecked")
		List<EObject> children = (List<EObject>) owner.eGet(parts);
		children.add(child);

		single.convertEMFToValue(geometry, child);

		assertThat(child.eContainer()).isSameAs(owner);
		assertThat(children).containsExactly(child);
	}

	@Test
	void aChildListRoundTripsInOrder() {
		List<EObject> written = List.of(newPoint("a", "1,1"), newPoint("b", "2,2"), newPoint("c", "3,3"));

		Object stored = many.convertEMFToValue(geometry, written);
		@SuppressWarnings("unchecked")
		List<EObject> read = (List<EObject>) many.convertValueToEMF(geometry, stored);

		assertThat(read).extracting(object -> object.eGet(label)).containsExactly("a", "b", "c");
	}

	@Test
	void noValueIsStoredForNoChild() {
		assertThat(single.convertEMFToValue(geometry, null)).isNull();
		assertThat(many.convertEMFToValue(geometry, List.of())).isNull();
		assertThat(single.convertValueToEMF(geometry, null)).isNull();
		assertThat(many.convertValueToEMF(geometry, null)).isEqualTo(List.of());
		assertThat(many.convertValueToEMF(geometry, "")).isEqualTo(List.of());
	}

	/**
	 * A cross-reference out of the subtree is written as an absolute href — against the opaque
	 * scratch URI nothing is deresolved — and comes back as a proxy to the same object.
	 */
	@Test
	void aCrossReferenceOutOfTheSubtreeComesBackAsAProxy() {
		Resource elsewhere = new ResourceImpl(URI.createURI("jpa://unit/Shape"));
		EObject target = EcoreUtil.create(shape);
		elsewhere.getContents().add(target);
		EObject written = newPoint("linked", "9,9");
		written.eSet(link, target);

		Object stored = single.convertEMFToValue(geometry, written);
		EObject read = (EObject) single.convertValueToEMF(geometry, stored);

		EObject proxy = (EObject) read.eGet(link, false);
		assertThat(proxy.eIsProxy()).isTrue();
		assertThat(((InternalEObject) proxy).eProxyURI()).isEqualTo(EcoreUtil.getURI(target));
	}

	private EObject newPoint(String pointLabel, String value) {
		EObject object = EcoreUtil.create(point);
		object.eSet(label, pointLabel);
		object.eSet(data, value);
		return object;
	}
}
