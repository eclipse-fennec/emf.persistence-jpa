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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.Test;

/**
 * Only a class whose instance class name is {@code java.util.Map$Entry} is a map entry, as in EMF
 * and in the codec (issue #401).
 */
class EMapsTest {

	@Test
	void aMapEntryClassIsAMapEntry() {
		EClass entry = keyValueClass("StringToStringMapEntry");
		entry.setInstanceClassName(EMaps.MAP_ENTRY_CLASS_NAME);

		assertThat(EMaps.isMapEntry(entry)).isTrue();
		assertThat(EMaps.isMap(containmentMany(entry))).isTrue();
	}

	@Test
	void keyAndValueFeaturesAloneMakeNoMapEntry() {
		EClass setting = keyValueClass("Setting");

		assertThat(EMaps.isMapEntry(setting)).isFalse();
		assertThat(EMaps.isMap(containmentMany(setting))).isFalse();
	}

	@Test
	void nullIsNoMapEntry() {
		assertThat(EMaps.isMapEntry(null)).isFalse();
		assertThat(EMaps.isMap(null)).isFalse();
	}

	private static EClass keyValueClass(String name) {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(name);
		for (String feature : new String[] { EMaps.KEY_FEATURE, EMaps.VALUE_FEATURE }) {
			EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
			attribute.setName(feature);
			attribute.setEType(EcorePackage.Literals.ESTRING);
			eClass.getEStructuralFeatures().add(attribute);
		}
		return eClass;
	}

	private static EReference containmentMany(EClass type) {
		EReference reference = EcoreFactory.eINSTANCE.createEReference();
		reference.setName("entries");
		reference.setEType(type);
		reference.setContainment(true);
		reference.setUpperBound(-1);
		return reference;
	}
}
