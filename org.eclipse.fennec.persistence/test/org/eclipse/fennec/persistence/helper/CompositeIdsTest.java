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
package org.eclipse.fennec.persistence.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the composite-id fragment contract (issue #109).
 *
 * @author Mark Hoffmann
 */
class CompositeIdsTest {

	private EClass orderLine;
	private EAttribute orderId;
	private EAttribute lineNo;
	private EClass single;
	private EAttribute sid;
	private EPackage ePackage;

	@BeforeEach
	void setUp() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		orderLine = ecore.createEClass();
		orderLine.setName("OrderLine");
		orderId = ecore.createEAttribute();
		orderId.setName("orderId");
		orderId.setEType(EcorePackage.Literals.ESTRING);
		orderId.setID(true);
		lineNo = ecore.createEAttribute();
		lineNo.setName("lineNo");
		lineNo.setEType(EcorePackage.Literals.EINT);
		orderLine.getEStructuralFeatures().add(orderId);
		orderLine.getEStructuralFeatures().add(lineNo);
		// the canonical composite declaration (issue #115): explicit idFeatures,
		// at most one isID — valid Ecore (validateEClass_AtMostOneID)
		annotate(orderLine, "orderId,lineNo");

		single = ecore.createEClass();
		single.setName("Single");
		sid = ecore.createEAttribute();
		sid.setName("sid");
		sid.setEType(EcorePackage.Literals.ESTRING);
		sid.setID(true);
		single.getEStructuralFeatures().add(sid);

		ePackage = ecore.createEPackage();
		ePackage.setName("cid");
		ePackage.setNsURI("urn:compositeids:test");
		ePackage.setNsPrefix("cid");
		ePackage.getEClassifiers().add(orderLine);
		ePackage.getEClassifiers().add(single);
	}

	private static void annotate(EClass eClass, String idFeatures) {
		EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
		annotation.setSource(CompositeIds.ANNOTATION_SOURCE);
		annotation.getDetails().put(CompositeIds.ID_FEATURES, idFeatures);
		eClass.getEAnnotations().add(annotation);
	}

	private EObject line(String order, int no) {
		EObject line = ePackage.getEFactoryInstance().create(orderLine);
		line.eSet(orderId, order);
		line.eSet(lineNo, no);
		return line;
	}

	@Test
	void fragmentRoundTripsInCanonicalOrder() {
		EObject line = line("A-1", 2);
		assertThat(CompositeIds.isComposite(orderLine)).isTrue();
		String fragment = CompositeIds.fragment(line);
		assertThat(fragment).isEqualTo("orderId=A-1,lineNo=2");

		assertThat(CompositeIds.parse(orderLine, fragment)).containsExactly("A-1", "2");
		// pairs may arrive in any order — parse normalises to canonical order
		assertThat(CompositeIds.parse(orderLine, "lineNo=2,orderId=A-1")).containsExactly("A-1", "2");

		EObject stub = ePackage.getEFactoryInstance().create(orderLine);
		CompositeIds.setId(stub, fragment);
		assertThat(stub.eGet(orderId)).isEqualTo("A-1");
		assertThat(stub.eGet(lineNo)).isEqualTo(2);
	}

	@Test
	void singleIdClassesKeepTheBareValueContract() {
		EObject one = ePackage.getEFactoryInstance().create(single);
		one.eSet(sid, "x");
		assertThat(CompositeIds.isComposite(single)).isFalse();
		assertThat(CompositeIds.fragment(one)).isEqualTo("x");
		assertThat(CompositeIds.isCompositeFragment("x")).isFalse();
		assertThat(CompositeIds.isCompositeFragment("orderId=A,lineNo=1")).isTrue();
	}

	@Test
	void reservedCharactersAreEscaped() {
		EObject line = line("A=1,x%", 7);
		String fragment = CompositeIds.fragment(line);
		assertThat(fragment).isEqualTo("orderId=A%3D1%2Cx%25,lineNo=7");
		assertThat(CompositeIds.parse(orderLine, fragment)).containsExactly("A=1,x%", "7");
	}

	@Test
	void nullComponentYieldsNoFragment() {
		// like EcoreUtil.getID, primitive defaults count as values — only null kills it
		EObject partial = ePackage.getEFactoryInstance().create(orderLine);
		partial.eSet(lineNo, 4);
		assertThat(partial.eGet(orderId)).isNull();
		assertThat(CompositeIds.fragment(partial)).isNull();
	}

	@Test
	void severalIsIdAttributesWithoutTheAnnotationAreRefused() {
		// invalid Ecore (validateEClass_AtMostOneID) — never a composite declaration
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		EClass invalid = ecore.createEClass();
		invalid.setName("Invalid");
		for (String name : new String[] { "k1", "k2" }) {
			EAttribute key = ecore.createEAttribute();
			key.setName(name);
			key.setEType(EcorePackage.Literals.ESTRING);
			key.setID(true);
			invalid.getEStructuralFeatures().add(key);
		}
		ePackage.getEClassifiers().add(invalid);
		assertThatThrownBy(() -> CompositeIds.idAttributes(invalid))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("validateEClass_AtMostOneID")
				.hasMessageContaining(CompositeIds.ID_FEATURES);
	}

	@Test
	void annotationNamingAnUnknownFeatureIsRefused() {
		EClass broken = EcoreFactory.eINSTANCE.createEClass();
		broken.setName("Broken");
		annotate(broken, "nope");
		ePackage.getEClassifiers().add(broken);
		assertThatThrownBy(() -> CompositeIds.idAttributes(broken))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("'nope'");
	}

	@Test
	void singleFeatureAnnotationWorksWithoutAnEIdAttribute() {
		// the annotation may declare an identity EMF itself does not know about
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		EClass keyed = ecore.createEClass();
		keyed.setName("Keyed");
		EAttribute code = ecore.createEAttribute();
		code.setName("code");
		code.setEType(EcorePackage.Literals.ESTRING);
		keyed.getEStructuralFeatures().add(code);
		annotate(keyed, "code");
		ePackage.getEClassifiers().add(keyed);

		assertThat(CompositeIds.isComposite(keyed)).isFalse();
		EObject object = ePackage.getEFactoryInstance().create(keyed);
		object.eSet(code, "k-7");
		assertThat(CompositeIds.fragment(object)).isEqualTo("k-7");
		EObject stub = ePackage.getEFactoryInstance().create(keyed);
		CompositeIds.setId(stub, "k-7");
		assertThat(stub.eGet(code)).isEqualTo("k-7");
	}

	@Test
	void malformedFragmentsAreRefused() {
		assertThatThrownBy(() -> CompositeIds.parse(orderLine, "orderId=A-1"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("misses id attribute 'lineNo'");
		assertThatThrownBy(() -> CompositeIds.parse(orderLine, "orderId=A,nope=1"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("unknown id attribute 'nope'");
		assertThatThrownBy(() -> CompositeIds.parse(orderLine, "orderId=A,orderId=B"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("repeats");
		assertThatThrownBy(() -> CompositeIds.parse(orderLine, "garbage"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Malformed");
		assertThatThrownBy(() -> CompositeIds.parse(single, "sid=x"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("no composite id");
	}
}
