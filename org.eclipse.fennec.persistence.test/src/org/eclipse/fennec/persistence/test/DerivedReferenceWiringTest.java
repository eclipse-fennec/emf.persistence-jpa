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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.query.derived.DerivedReferenceCompiler;
import org.junit.jupiter.api.Test;
import org.osgi.framework.ServiceReference;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;

/**
 * OSGi integration test for the query-backed derived-reference wiring (issue #71):
 * the delegate factory is a DS component on the emf.osgi whiteboard (same delegate URI
 * as the m2x OCL factory, higher ranking), and its delegates evaluate a derivation
 * end-to-end inside the OSGi runtime — proving the m2x parser/ANTLR bundle wiring.
 */
public class DerivedReferenceWiringTest {

	@Test
	public void factoryIsRegisteredWithWhiteboardProperties(
			@InjectService(filter = "(emf.name=fennec-query-derived)", timeout = 5000)
			ServiceAware<EStructuralFeature.Internal.SettingDelegate.Factory> factoryAware) {
		assertNotNull(factoryAware.getService(), "The derived-reference delegate factory must be registered");
		ServiceReference<EStructuralFeature.Internal.SettingDelegate.Factory> reference =
				factoryAware.getServiceReference();
		assertEquals("SETTING_DELEGATE_FACTORY", reference.getProperty("emf.configuratorType"));
		assertEquals(DerivedReferenceCompiler.DELEGATE_URI, reference.getProperty("emf.configuratorName"));
		assertEquals(100, reference.getProperty("service.ranking"));
	}

	/**
	 * End-to-end through EMF's delegate mechanism with the DS-created factory: an
	 * annotated dynamic model evaluates its derivation locally (unattached owner) —
	 * exercising the OCL parse (m2x parser + ANTLR runtime) and the expression bridge
	 * inside the OSGi runtime.
	 */
	@Test
	public void annotatedDerivationEvaluatesInsideTheRuntime(
			@InjectService(filter = "(emf.name=fennec-query-derived)", timeout = 5000)
			ServiceAware<EStructuralFeature.Internal.SettingDelegate.Factory> factoryAware) {
		EStructuralFeature.Internal.SettingDelegate.Factory.Registry.INSTANCE
				.put(DerivedReferenceCompiler.DELEGATE_URI, factoryAware.getService());
		try {
			EcoreFactory ecore = EcoreFactory.eINSTANCE;
			EPackage ePackage = ecore.createEPackage();
			ePackage.setName("wiring");
			ePackage.setNsPrefix("wiring");
			ePackage.setNsURI("https://eclipse.org/fennec/test/derived-wiring");
			EAnnotation packageAnnotation = ecore.createEAnnotation();
			packageAnnotation.setSource(EcorePackage.eNS_URI);
			packageAnnotation.getDetails().put("settingDelegates", DerivedReferenceCompiler.DELEGATE_URI);
			ePackage.getEAnnotations().add(packageAnnotation);

			EClass person = ecore.createEClass();
			person.setName("Person");
			EAttribute age = ecore.createEAttribute();
			age.setName("age");
			age.setEType(EcorePackage.Literals.EINT);
			person.getEStructuralFeatures().add(age);
			EReference friends = ecore.createEReference();
			friends.setName("friends");
			friends.setEType(person);
			friends.setUpperBound(-1);
			person.getEStructuralFeatures().add(friends);

			EReference adultFriends = ecore.createEReference();
			adultFriends.setName("adultFriends");
			adultFriends.setEType(person);
			adultFriends.setUpperBound(-1);
			adultFriends.setTransient(true);
			adultFriends.setVolatile(true);
			adultFriends.setDerived(true);
			adultFriends.setChangeable(false);
			EAnnotation derivation = ecore.createEAnnotation();
			derivation.setSource(DerivedReferenceCompiler.DELEGATE_URI);
			derivation.getDetails().put(DerivedReferenceCompiler.DETAIL_DERIVATION,
					"self.friends->select(f | f.age >= 18)");
			adultFriends.getEAnnotations().add(derivation);
			person.getEStructuralFeatures().add(adultFriends);
			ePackage.getEClassifiers().add(person);

			EObject adult = EcoreUtil.create(person);
			adult.eSet(age, 30);
			EObject kid = EcoreUtil.create(person);
			kid.eSet(age, 8);
			EObject owner = EcoreUtil.create(person);
			owner.eSet(age, 40);
			@SuppressWarnings("unchecked")
			List<EObject> ownerFriends = (List<EObject>) owner.eGet(friends);
			ownerFriends.add(adult);
			ownerFriends.add(kid);

			@SuppressWarnings("unchecked")
			List<EObject> result = (List<EObject>) owner.eGet(adultFriends);
			assertEquals(1, result.size(), "only the adult friend must pass the derivation");
			assertTrue(result.contains(adult));
		} finally {
			EStructuralFeature.Internal.SettingDelegate.Factory.Registry.INSTANCE
					.remove(DerivedReferenceCompiler.DELEGATE_URI);
		}
	}
}
