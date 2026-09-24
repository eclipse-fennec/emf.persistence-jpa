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

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.persistence.api.TypeConverter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;

/**
 * The {@link DefaultConverter} claims only Ecore's own data types (issue #324). It compared the
 * classifier id alone, so a data type of another package that happens to sit at the index of
 * {@code EChar} (27) — the {@code GeoJsonGeometry} of emf.ogc.features — was taken for an
 * {@code EChar}, and the converter registered for it was never asked.
 */
class DefaultConverterTest {

	private final DefaultConverter converter = new DefaultConverter();

	@ParameterizedTest
	@ValueSource(ints = { EcorePackage.EBYTE_ARRAY, EcorePackage.EBYTE_OBJECT, EcorePackage.ECHAR,
			EcorePackage.ECHARACTER_OBJECT, EcorePackage.EJAVA_CLASS, EcorePackage.EJAVA_OBJECT })
	void aForeignDataTypeAtAnEcoreIndexIsNotClaimed(int classifierId) {
		EDataType foreign = dataTypeAt(classifierId);

		assertThat(foreign.getClassifierID()).isEqualTo(classifierId);
		assertThat(converter.isConverterForType(foreign)).isFalse();
	}

	@Test
	void ecoreDataTypesAreClaimed() {
		assertThat(converter.isConverterForType(EcorePackage.Literals.ECHAR)).isTrue();
		assertThat(converter.isConverterForType(EcorePackage.Literals.EBYTE_ARRAY)).isTrue();
		assertThat(converter.isConverterForType(EcorePackage.Literals.EJAVA_OBJECT)).isTrue();
		assertThat(converter.isConverterForType(EcorePackage.Literals.ESTRING)).isFalse();
	}

	@Test
	void theServiceHandsAForeignTypeToItsOwnConverter() {
		EDataType foreign = dataTypeAt(EcorePackage.ECHAR);
		DefaultConverterService service = new DefaultConverterService();
		TypeConverter own = new TypeConverter() {
			@Override
			public String getName() {
				return "own";
			}

			@Override
			public Object convertValueToEMF(EClassifier eDataType, Object value) {
				return value;
			}

			@Override
			public Object convertEMFToValue(EClassifier eDataType, Object emfValue) {
				return emfValue;
			}

			@Override
			public boolean isConverterForType(EClassifier eDataType) {
				return eDataType == foreign;
			}
		};
		service.converters.add(own);

		assertThat(service.getConverter(foreign)).isSameAs(own);
	}

	/** A data type of a package other than Ecore whose classifier id is {@code classifierId}. */
	private static EDataType dataTypeAt(int classifierId) {
		EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
		pkg.setName("foreign");
		pkg.setNsURI("http://test/foreign/" + classifierId);
		pkg.setNsPrefix("foreign");
		for (int i = 0; i < classifierId; i++) {
			EDataType filler = EcoreFactory.eINSTANCE.createEDataType();
			filler.setName("Filler" + i);
			filler.setInstanceClassName("java.lang.String");
			pkg.getEClassifiers().add(filler);
		}
		EDataType type = EcoreFactory.eINSTANCE.createEDataType();
		type.setName("Geometry");
		type.setInstanceClassName("org.example.Geometry");
		pkg.getEClassifiers().add(type);
		return type;
	}
}
