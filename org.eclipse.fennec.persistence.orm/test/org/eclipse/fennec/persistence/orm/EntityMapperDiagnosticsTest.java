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
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.eorm.Basic;
import org.eclipse.fennec.persistence.helper.DatabaseAnnotations;
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
	void reservedDerivedColumnIsEscapedAndReportedAsInfo() {
		EPackage ePackage = createEPackage();
		EClass person = createEClass(ePackage, "Person");
		EAttribute select = createAttribute(person, "select");

		MappingResult result = new EntityMapper().createMappingsFromEPackageWithDiagnostics(ePackage);

		// issue #399: the derived column is escaped instead of breaking the DDL
		assertThat(result.isSuccess()).isTrue();
		assertThat(basic(result, "select").getColumn().getName()).isEqualTo("select_");
		assertThat(basic(result, "select").getName()).as("the attribute keeps its name").isEqualTo("select");
		assertThat(result.diagnostics()).noneMatch(d -> d.getSeverity() >= Diagnostic.WARNING);
		assertThat(result.diagnostics()).filteredOn(d -> d.getSeverity() == Diagnostic.INFO)
				.anySatisfy(info -> {
					assertThat(info.getSource()).isEqualTo(MappingContext.DIAGNOSTIC_SOURCE);
					assertThat(info.getMessage()).contains("Column name 'select'", "'select_'", "H2", "POSTGRESQL",
							"MARIADB");
					assertThat(info.getData()).first().isEqualTo(select);
				});
	}

	@Test
	void reservedDerivedTableIsEscapedAndReportedAsInfo() {
		EPackage ePackage = createEPackage();
		EClass order = createEClass(ePackage, "Order");

		MappingResult result = new EntityMapper().createMappingsFromEPackageWithDiagnostics(ePackage);

		assertThat(result.mappings().getEntity()).singleElement().satisfies(entity -> {
			assertThat(entity.getTable().getName()).isEqualTo("ORDER_");
			assertThat(entity.getName()).as("the entity keeps its name").isEqualTo("Order");
		});
		assertThat(result.diagnostics()).filteredOn(d -> d.getSeverity() == Diagnostic.INFO)
				.anySatisfy(info -> {
					assertThat(info.getMessage()).contains("Table name 'ORDER'", "'ORDER_'");
					assertThat(info.getData()).first().isEqualTo(order);
				});
	}

	/** A word only some kind of name has reserved is escaped only there: MariaDB reads {@code position (} as a call. */
	@Test
	void aWordReservedOnlyAsTableNameStaysAColumnName() {
		EPackage ePackage = createEPackage();
		EClass position = createEClass(ePackage, "Position");
		createAttribute(position, "position");

		MappingResult result = new EntityMapper().createMappingsFromEPackageWithDiagnostics(ePackage);

		assertThat(result.mappings().getEntity().get(0).getTable().getName()).isEqualTo("POSITION_");
		assertThat(basic(result, "position").getColumn().getName()).isEqualTo("position");
	}

	@Test
	void ordinaryNamesAreNotEscaped() {
		EPackage ePackage = createEPackage();
		EClass person = createEClass(ePackage, "Person");
		createAttribute(person, "timestamp");
		createAttribute(person, "type");

		MappingResult result = new EntityMapper().createMappingsFromEPackageWithDiagnostics(ePackage);

		assertThat(result.mappings().getEntity().get(0).getTable().getName()).isEqualTo("PERSON");
		assertThat(basic(result, "timestamp").getColumn().getName()).isEqualTo("timestamp");
		assertThat(basic(result, "type").getColumn().getName()).isEqualTo("type");
		assertThat(result.diagnostics()).noneMatch(d -> d.getMessage().contains("reserved"));
	}

	/** A database name is the name of an existing column, taken literally (issue #296). */
	@Test
	void declaredDatabaseNamesAreNotEscaped() {
		EPackage ePackage = createEPackage();
		EClass order = createEClass(ePackage, "Order");
		EcoreUtil.setAnnotation(order, DatabaseAnnotations.ANNOTATION_SOURCE, DatabaseAnnotations.TABLE_NAME, "ORDER");
		EAttribute value = createAttribute(order, "value");
		EcoreUtil.setAnnotation(value, DatabaseAnnotations.ANNOTATION_SOURCE, DatabaseAnnotations.COLUMN_NAME, "value");

		MappingResult result = new EntityMapper().createMappingsFromEPackageWithDiagnostics(ePackage);

		assertThat(result.mappings().getEntity().get(0).getTable().getName()).isEqualTo("ORDER");
		assertThat(basic(result, "value").getColumn().getName()).isEqualTo("value");
		assertThat(result.diagnostics()).noneMatch(d -> d.getMessage().contains("reserved"));
	}

	/** A strict mapping guesses nothing, so it takes the derived names as they are. */
	@Test
	void aStrictMappingDoesNotEscape() {
		EPackage ePackage = createEPackage();
		EClass order = createEClass(ePackage, "Order");
		createAttribute(order, "value");
		EntityMapper mapper = new EntityMapper();
		mapper.setStrict(true);

		MappingResult result = mapper.createMappingsFromEPackageWithDiagnostics(ePackage);

		assertThat(result.mappings().getEntity().get(0).getTable().getName()).isEqualTo("ORDER");
		assertThat(basic(result, "value").getColumn().getName()).isEqualTo("value");
	}

	private static EAttribute createAttribute(EClass owner, String name) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(EcorePackage.Literals.ESTRING);
		owner.getEStructuralFeatures().add(attribute);
		return attribute;
	}

	private static Basic basic(MappingResult result, String name) {
		return result.mappings().getEntity().get(0).getAttributes().getBasic().stream()
				.filter(b -> name.equals(b.getName()))
				.findFirst()
				.orElseThrow();
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
