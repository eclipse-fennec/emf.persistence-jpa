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
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
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
	private EEnum colorType;

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
		colorType = EcoreFactory.eINSTANCE.createEEnum();
		colorType.setName("Color");
		colorType.getELiterals().add(literal("GREEN", 0, "green"));
		colorType.getELiterals().add(literal("RED", 1, "red"));
		pkg.getEClassifiers().add(colorType);
		thing = EcoreFactory.eINSTANCE.createEClass();
		thing.setName("Thing");
		pkg.getEClassifiers().add(thing);
		attribute("key", uuidType);
		attribute("name", EcorePackage.Literals.ESTRING);
		attribute("count", EcorePackage.Literals.EINT);
		attribute("color", colorType);
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

	/**
	 * The eorm named this converter for this one attribute (issue #373), so it is authoritative
	 * for it; the converter claims no {@code EDataType} at all, which is the right shape for a
	 * transform that is a property of a column rather than of a type — claiming {@code EString}
	 * would convert the query literals of every String attribute in the unit.
	 */
	@Test
	void anAttributeConverterNamedByTheMappingIsAppliedOnRead() {
		EObject object = EcoreUtil.create(thing);

		accessor("name", new MarkerConverter()).setAttributeValueInObject(object, "enc:secret");

		assertThat(object.eGet(thing.getEStructuralFeature("name"))).isEqualTo("secret");
	}

	@Test
	void anAttributeConverterRoundTripsTheValueItWrote() {
		EObject object = EcoreUtil.create(thing);
		object.eSet(thing.getEStructuralFeature("name"), "secret");

		Object stored = accessor("name", new MarkerConverter()).getAttributeValueFromObject(object);
		EObject read = EcoreUtil.create(thing);
		accessor("name", new MarkerConverter()).setAttributeValueInObject(read, stored);

		assertThat(stored).isEqualTo("enc:secret");
		assertThat(read.eGet(thing.getEStructuralFeature("name"))).isEqualTo("secret");
	}

	@Test
	void anEnumValueReachesTheConverterAsItsLiteral() {
		EObject object = EcoreUtil.create(thing);
		object.eSet(thing.getEStructuralFeature("color"), colorType.getEEnumLiteral("GREEN").getInstance());

		Object stored = accessor("color", new MarkerConverter()).getAttributeValueFromObject(object);

		assertThat(stored).isEqualTo("enc:green");
	}

	@Test
	void anEnumAttributeIsReadBackThroughTheConverter() {
		EObject object = EcoreUtil.create(thing);

		accessor("color", new MarkerConverter()).setAttributeValueInObject(object, "enc:green");

		assertThat(object.eGet(thing.getEStructuralFeature("color")))
			.isEqualTo(colorType.getEEnumLiteral("GREEN").getInstance());
	}

	@Test
	void anEnumWithoutAConverterStillReadsAndWritesItsLiteral() {
		EObject object = EcoreUtil.create(thing);
		object.eSet(thing.getEStructuralFeature("color"), colorType.getEEnumLiteral("RED").getInstance());

		assertThat(accessor("color", null).getAttributeValueFromObject(object)).isEqualTo("red");

		EObject read = EcoreUtil.create(thing);
		accessor("color", null).setAttributeValueInObject(read, "red");
		assertThat(read.eGet(thing.getEStructuralFeature("color")))
			.isEqualTo(colorType.getEEnumLiteral("RED").getInstance());
	}

	private EEnumLiteral literal(String name, int value, String literal) {
		EEnumLiteral eLiteral = EcoreFactory.eINSTANCE.createEEnumLiteral();
		eLiteral.setName(name);
		eLiteral.setValue(value);
		eLiteral.setLiteral(literal);
		return eLiteral;
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

	/**
	 * A converter bound to one attribute by name and to no type at all: it marks the value it
	 * writes and strips the marker from the value it reads, so the two directions are inverses.
	 */
	private static class MarkerConverter implements TypeConverter {

		private static final String MARKER = "enc:";

		@Override
		public String getName() {
			return "test-marker";
		}

		@Override
		public boolean isConverterForType(EClassifier eDataType) {
			return false;
		}

		@Override
		public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
			return emfValue instanceof String value ? MARKER + value : emfValue;
		}

		@Override
		public Object convertValueToEMF(EClassifier eDataType, Object value) {
			return value instanceof String string && string.startsWith(MARKER)
					? string.substring(MARKER.length())
					: value;
		}
	}
}
