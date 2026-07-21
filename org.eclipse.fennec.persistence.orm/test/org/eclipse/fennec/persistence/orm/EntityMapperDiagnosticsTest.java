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
package org.eclipse.fennec.persistence.orm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.Test;

/**
 * Tests for the diagnostics of the eorm generation run (issue #19): problems and
 * silent corrections are reported as EMF diagnostics on the {@link MappingResult},
 * not only logged.
 */
class EntityMapperDiagnosticsTest {

	private EAttribute createId() {
		EAttribute id = EcoreFactory.eINSTANCE.createEAttribute();
		id.setName("id");
		id.setID(true);
		id.setEType(EcorePackage.Literals.ELONG);
		return id;
	}

	private EClass createEClass(EPackage ePackage, String name) {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(name);
		eClass.getEStructuralFeatures().add(createId());
		ePackage.getEClassifiers().add(eClass);
		return eClass;
	}

	private EPackage createEPackage() {
		EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("diagtest");
		ePackage.setNsPrefix("diagtest");
		ePackage.setNsURI("http://fennec.eclipse.org/diagtest");
		return ePackage;
	}

	@Test
	void reservedSqlNameIsReportedAsWarningWithTheAffectedElement() {
		EPackage ePackage = createEPackage();
		EClass person = createEClass(ePackage, "Person");
		EAttribute select = EcoreFactory.eINSTANCE.createEAttribute();
		select.setName("select");
		select.setEType(EcorePackage.Literals.ESTRING);
		person.getEStructuralFeatures().add(select);

		MappingResult result = new EntityMapper().createMappingsFromEPackageWithDiagnostics(ePackage);

		assertThat(result.isSuccess()).isTrue();
		assertThat(result.getSeverity()).isEqualTo(Diagnostic.WARNING);
		List<Diagnostic> warnings = result.diagnostics().stream()
				.filter(diagnostic -> diagnostic.getSeverity() == Diagnostic.WARNING)
				.toList();
		assertThat(warnings).isNotEmpty();
		Diagnostic warning = warnings.get(0);
		assertThat(warning.getSource()).isEqualTo(MappingContext.DIAGNOSTIC_SOURCE);
		assertThat(warning.getMessage()).contains("reserved word").contains("select");
		assertThat(warning.getData()).first().isEqualTo(select);
		// the mapping itself is still produced - warnings do not abort the run
		assertThat(result.mappings().getEntity()).hasSize(1);
	}

	@Test
	void manyValuedReferenceWithContainmentOppositeIsReportedAsError() {
		EPackage ePackage = createEPackage();
		EClass parent = createEClass(ePackage, "Parent");
		EClass child = createEClass(ePackage, "Child");

		EReference childRef = EcoreFactory.eINSTANCE.createEReference();
		childRef.setName("child");
		childRef.setContainment(true);
		childRef.setEType(child);
		parent.getEStructuralFeatures().add(childRef);

		// invalid EMF shape: a many-valued opposite of a containment reference
		EReference parentsRef = EcoreFactory.eINSTANCE.createEReference();
		parentsRef.setName("parents");
		parentsRef.setUpperBound(-1);
		parentsRef.setEType(parent);
		child.getEStructuralFeatures().add(parentsRef);
		childRef.setEOpposite(parentsRef);
		parentsRef.setEOpposite(childRef);

		MappingResult result = new EntityMapper().createMappingsFromEPackageWithDiagnostics(ePackage);

		// the run aborts internally; the result API converts that into an ERROR
		// diagnostic carrying the exception instead of throwing (m2x result semantics)
		assertThat(result.isSuccess()).isFalse();
		assertThat(result.getSeverity()).isEqualTo(Diagnostic.ERROR);
		Diagnostic error = result.diagnostics().stream()
				.filter(diagnostic -> diagnostic.getSeverity() == Diagnostic.ERROR)
				.findFirst().orElseThrow();
		assertThat(error.getSource()).isEqualTo(MappingContext.DIAGNOSTIC_SOURCE);
		assertThat(error.getMessage()).contains("containment");
		assertThat(error.getException()).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void legacyApiKeepsThrowingOnAbortingModelProblems() {
		EPackage ePackage = createEPackage();
		EClass parent = createEClass(ePackage, "Parent");
		EClass child = createEClass(ePackage, "Child");
		EReference childRef = EcoreFactory.eINSTANCE.createEReference();
		childRef.setName("child");
		childRef.setContainment(true);
		childRef.setEType(child);
		parent.getEStructuralFeatures().add(childRef);
		EReference parentsRef = EcoreFactory.eINSTANCE.createEReference();
		parentsRef.setName("parents");
		parentsRef.setUpperBound(-1);
		parentsRef.setEType(parent);
		child.getEStructuralFeatures().add(parentsRef);
		childRef.setEOpposite(parentsRef);
		parentsRef.setEOpposite(childRef);

		assertThrows(IllegalStateException.class,
				() -> new EntityMapper().createMappingsFromEPackage(ePackage));
	}

	@Test
	void cleanModelProducesNoDiagnostics() {
		EPackage ePackage = createEPackage();
		EClass person = createEClass(ePackage, "Person");
		EAttribute name = EcoreFactory.eINSTANCE.createEAttribute();
		name.setName("personName");
		name.setEType(EcorePackage.Literals.ESTRING);
		person.getEStructuralFeatures().add(name);

		MappingResult result = new EntityMapper().createMappingsFromEPackageWithDiagnostics(ePackage);

		assertThat(result.diagnostics()).isEmpty();
		assertThat(result.isSuccess()).isTrue();
		assertThat(result.getSeverity()).isEqualTo(Diagnostic.OK);
	}

	@Test
	void legacyApiStillReturnsTheMappings() {
		EPackage ePackage = createEPackage();
		EClass person = createEClass(ePackage, "Person");
		EAttribute select = EcoreFactory.eINSTANCE.createEAttribute();
		select.setName("select");
		select.setEType(EcorePackage.Literals.ESTRING);
		person.getEStructuralFeatures().add(select);

		// diagnostics are bridged to JUL at this boundary, the result is unchanged
		assertThat(new EntityMapper().createMappingsFromEPackage(ePackage).getEntity()).hasSize(1);
	}
}
