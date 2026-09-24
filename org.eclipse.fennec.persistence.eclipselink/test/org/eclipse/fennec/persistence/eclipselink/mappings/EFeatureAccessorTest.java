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
package org.eclipse.fennec.persistence.eclipselink.mappings;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.api.TypeConverter;
import org.eclipse.fennec.persistence.converter.DefaultConverterService;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Values read from the database reach the model through the attribute's converter first
 * (issue #308): a dynamic data type has no generated factory, and {@code createFromString} cannot
 * build types without a String constructor or {@code valueOf(String)} — {@code UUID} has only
 * {@code fromString}.
 */
class EFeatureAccessorTest {

	private EClass thing;
	private EDataType uuidType;

	@BeforeEach
	void setUp() {
		EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
		pkg.setName("accessor");
		pkg.setNsURI("http://test/accessor/" + UUID.randomUUID());
		pkg.setNsPrefix("accessor");
		uuidType = EcoreFactory.eINSTANCE.createEDataType();
		uuidType.setName("EUUID");
		uuidType.setInstanceClassName(UUID.class.getName());
		pkg.getEClassifiers().add(uuidType);
		thing = EcoreFactory.eINSTANCE.createEClass();
		thing.setName("Thing");
		pkg.getEClassifiers().add(thing);
		attribute("key", uuidType);
		attribute("name", EcorePackage.Literals.ESTRING);
		attribute("count", EcorePackage.Literals.EINT);
	}

	@Test
	void aDynamicUuidTypeIsReadThroughItsConverter() {
		UUID uuid = UUID.fromString("0f8fad5b-d9cb-469f-a165-70867728950e");
		TypeConverter converter = new DefaultConverterService().getConverter(uuidType);
		EObject object = EcoreUtil.create(thing);

		accessor("key", converter).setAttributeValueInObject(object, uuid.toString());

		assertThat(object.eGet(thing.getEStructuralFeature("key"))).isEqualTo(uuid);
	}

	@Test
	void aStringOfAnEStringAttributeStaysAsIs() {
		EObject object = EcoreUtil.create(thing);

		accessor("name", null).setAttributeValueInObject(object, "0f8fad5b");

		assertThat(object.eGet(thing.getEStructuralFeature("name"))).isEqualTo("0f8fad5b");
	}

	@Test
	void withoutAConverterAStringIsCreatedFromString() {
		EObject object = EcoreUtil.create(thing);

		accessor("count", null).setAttributeValueInObject(object, "42");

		assertThat(object.eGet(thing.getEStructuralFeature("count"))).isEqualTo(42);
	}

	private void attribute(String name, EDataType type) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(type);
		thing.getEStructuralFeatures().add(attribute);
	}

	private EFeatureAccessor accessor(String name, TypeConverter converter) {
		DirectToFieldMapping mapping = new DirectToFieldMapping();
		mapping.setAttributeName(name);
		return EFeatureAccessor.create(mapping, thing.getEStructuralFeature(name), converter);
	}
}
