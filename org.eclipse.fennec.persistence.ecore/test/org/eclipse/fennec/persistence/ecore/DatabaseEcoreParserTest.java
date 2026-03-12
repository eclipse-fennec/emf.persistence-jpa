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
package org.eclipse.fennec.persistence.ecore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.eclipse.daanse.jdbc.db.api.schema.ColumnReference;
import org.eclipse.daanse.jdbc.db.api.schema.ImportedKey;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DatabaseEcoreParserTest {

	@Mock
	EClass eClass;
	@Mock
	EClass eClassRef;

	@Test
	void testAddAttribute() {
		EList<EStructuralFeature> features = new BasicEList<>();
		when(eClass.getEStructuralFeatures()).thenReturn(features);
		DatabaseEcoreParser.addAttribute(eClass, "attrib1", EcorePackage.Literals.ESTRING, false);
		assertThat(features).hasSize(1)
				.element(0) //
				.extracting(EStructuralFeature::getName, EStructuralFeature::getEType) //
				.contains("attrib1", EcorePackage.Literals.ESTRING);
	}

	@Test
	void testAddReference() {
		EList<EStructuralFeature> features = new BasicEList<>();
		when(eClass.getEStructuralFeatures()).thenReturn(features);
		DatabaseEcoreParser.addReference(eClass, "ref1", eClassRef);
		assertThat(features).hasSize(1)
				.element(0) //
				.extracting(EStructuralFeature::getName, EStructuralFeature::getEType) //
				.contains("ref1", eClassRef);
	}

	@Test
	void testCreatePackage() {
		EPackage ePackage = DatabaseEcoreParser.createPackage("name1", "pref1",
				"http://model.example.com/model/name1/1.0");
		assertThat(ePackage) //
				.extracting(p -> p.getName(), p -> p.getNsPrefix(), p -> p.getNsURI()) //
				.contains("name1", "pref1", "http://model.example.com/model/name1/1.0");
	}

	@Test
	void testCreateEClass() {
		EClass eclass = DatabaseEcoreParser.createEClass("name1");
		assertThat(eclass) //
				.extracting(c -> c.getName()) //
				.isEqualTo("name1");
	}

	@Nested
	class IsPK {
		@Mock
		ImportedKey importKey;
		@Mock
		ColumnReference colRef;

		@Test
		void testIsPK() {
			when(importKey.primaryKeyColumn()).thenReturn(colRef);
			when(colRef.name()).thenReturn("col1");
			boolean isPK = DatabaseEcoreParser.isPK(importKey, "col1");
			assertThat(isPK).isTrue();
		}
		@Test
		void testIsNotPK() {
			boolean isPK = DatabaseEcoreParser.isPK(null, "col1");
			assertThat(isPK).isFalse();
		}
		@Test
		void testIsIdCol() {
			boolean isPK = DatabaseEcoreParser.isPK(null, "id");
			assertThat(isPK).isTrue();
		}
	}

}
