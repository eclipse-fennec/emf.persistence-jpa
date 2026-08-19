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

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * The shared model behind {@code JpaEMapRoundTripTest} and {@code MongoEMapRoundTripTest}:
 * one {@code Catalog} carrying three maps, so both backends are measured against exactly the
 * same shape.
 *
 * <pre>
 * Catalog
 *   cid        EString, id
 *   name       EString
 *   attributes EMap&lt;EString, EString&gt;   — the ordinary case
 *   counts     EMap&lt;EInt, EString&gt;      — a non-string key
 *   parts      EMap&lt;EString, Part&gt;      — an EObject value, containment
 * </pre>
 *
 * An EMap is not a distinct construct in Ecore: it is a containment-many {@link EReference}
 * whose type is an {@link EClass} with {@code key} and {@code value} features and the instance
 * class name {@code java.util.Map$Entry}. That last part is what makes EMF hand out an
 * {@code EcoreEMap} instead of a plain list — {@code EStructuralFeatureImpl} selects the
 * {@code EMAP} setting delegate on {@code dataClass == Map.Entry.class}, so the entry class must
 * carry the instance class name even in a dynamic model.
 * <p>
 * Built in code rather than added to {@code tck.ecore} for the same reason as
 * {@code JpaCascadeDeleteTest}: the shared model has no map, and adding one would touch every
 * binding's bootstrap list before it is decided where maps belong in the contract (issue #171,
 * §9.2).
 *
 * @author Mark Hoffmann
 * @since 19.08.2026
 */
final class EMapTestModel {

	static final String NS_URI = "urn:emap:test/1.0";

	final EPackage ePackage;
	final EClass catalogClass;
	final EClass partClass;
	final EClass stringEntryClass;
	final EClass intEntryClass;
	final EClass partEntryClass;
	final EReference attributes;
	final EReference counts;
	final EReference parts;

	EMapTestModel() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;

		partClass = ecore.createEClass();
		partClass.setName("Part");
		addId(partClass, "pid");
		addString(partClass, "label");

		stringEntryClass = mapEntry("StringToStringMapEntry", EcorePackage.Literals.ESTRING,
				EcorePackage.Literals.ESTRING);
		intEntryClass = mapEntry("IntToStringMapEntry", EcorePackage.Literals.EINT,
				EcorePackage.Literals.ESTRING);
		partEntryClass = mapEntryWithReferenceValue("StringToPartMapEntry",
				EcorePackage.Literals.ESTRING, partClass);

		catalogClass = ecore.createEClass();
		catalogClass.setName("Catalog");
		addId(catalogClass, "cid");
		addString(catalogClass, "name");
		attributes = mapFeature("attributes", stringEntryClass);
		catalogClass.getEStructuralFeatures().add(attributes);
		counts = mapFeature("counts", intEntryClass);
		catalogClass.getEStructuralFeatures().add(counts);
		parts = mapFeature("parts", partEntryClass);
		catalogClass.getEStructuralFeatures().add(parts);

		ePackage = ecore.createEPackage();
		ePackage.setName("emap");
		ePackage.setNsURI(NS_URI);
		ePackage.setNsPrefix("emap");
		ePackage.getEClassifiers().add(catalogClass);
		ePackage.getEClassifiers().add(partClass);
		ePackage.getEClassifiers().add(stringEntryClass);
		ePackage.getEClassifiers().add(intEntryClass);
		ePackage.getEClassifiers().add(partEntryClass);
	}

	/** The classifiers a JPA persistence unit has to be bootstrapped with. */
	List<EClassifier> classifiers() {
		return List.of(catalogClass, partClass, stringEntryClass, intEntryClass, partEntryClass);
	}

	EObject newCatalog(String id, String name) {
		EObject catalog = EcoreUtil.create(catalogClass);
		catalog.eSet(catalogClass.getEStructuralFeature("cid"), id);
		catalog.eSet(catalogClass.getEStructuralFeature("name"), name);
		return catalog;
	}

	EObject newPart(String id, String label) {
		EObject part = EcoreUtil.create(partClass);
		part.eSet(partClass.getEStructuralFeature("pid"), id);
		part.eSet(partClass.getEStructuralFeature("label"), label);
		return part;
	}

	private static EClass mapEntry(String name, EDataType keyType, EDataType valueType) {
		EClass entry = baseEntry(name, keyType);
		EAttribute value = EcoreFactory.eINSTANCE.createEAttribute();
		value.setName("value");
		value.setEType(valueType);
		entry.getEStructuralFeatures().add(value);
		return entry;
	}

	private static EClass mapEntryWithReferenceValue(String name, EDataType keyType, EClass valueType) {
		EClass entry = baseEntry(name, keyType);
		EReference value = EcoreFactory.eINSTANCE.createEReference();
		value.setName("value");
		value.setEType(valueType);
		value.setContainment(true);
		entry.getEStructuralFeatures().add(value);
		return entry;
	}

	private static EClass baseEntry(String name, EDataType keyType) {
		EClass entry = EcoreFactory.eINSTANCE.createEClass();
		entry.setName(name);
		// what turns the containment reference below into an EMap rather than a plain list
		entry.setInstanceClassName("java.util.Map$Entry");
		EAttribute key = EcoreFactory.eINSTANCE.createEAttribute();
		key.setName("key");
		key.setEType(keyType);
		entry.getEStructuralFeatures().add(key);
		return entry;
	}

	private static EReference mapFeature(String name, EClass entryClass) {
		EReference reference = EcoreFactory.eINSTANCE.createEReference();
		reference.setName(name);
		reference.setEType(entryClass);
		reference.setContainment(true);
		reference.setUpperBound(-1);
		return reference;
	}

	private static void addId(EClass eClass, String name) {
		EAttribute id = EcoreFactory.eINSTANCE.createEAttribute();
		id.setName(name);
		id.setEType(EcorePackage.Literals.ESTRING);
		id.setID(true);
		eClass.getEStructuralFeatures().add(id);
	}

	private static void addString(EClass eClass, String name) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(EcorePackage.Literals.ESTRING);
		eClass.getEStructuralFeatures().add(attribute);
	}
}
