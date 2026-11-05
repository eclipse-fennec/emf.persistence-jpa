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

import java.util.List;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.persistence.eorm.Basic;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.GenerationType;
import org.eclipse.fennec.persistence.eorm.Id;
import org.eclipse.fennec.persistence.eorm.JoinColumn;
import org.eclipse.fennec.persistence.eorm.JoinTable;
import org.eclipse.fennec.persistence.eorm.ManyToMany;
import org.eclipse.fennec.persistence.eorm.ManyToOne;
import org.eclipse.fennec.persistence.eorm.OneToOne;
import org.eclipse.fennec.persistence.helper.CompositeIds;
import org.eclipse.fennec.persistence.helper.DatabaseAnnotations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The database names of an existing schema ({@link DatabaseAnnotations}, issue #296) are taken
 * literally by the mapping: tables, columns, join columns and join tables as declared, and a key
 * generated only as the schema says. Elements without them keep the derived names.
 */
class DatabaseNamesTest {

	private EPackage ePackage;

	@BeforeEach
	void setUp() {
		ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("dbnames");
		ePackage.setNsPrefix("dbnames");
		ePackage.setNsURI("http://fennec.eclipse.org/dbnames");
	}

	@Test
	void tableAndColumnsAreNamedExactly() {
		EClass person = eClass("Person", "person_tbl");
		EAttribute id = attribute(person, "id", EcorePackage.Literals.ELONG, "person_id");
		id.setID(true);
		attribute(person, "firstName", EcorePackage.Literals.ESTRING, "first_name");
		// a reserved word declared by the schema is its name, no escape and no warning
		attribute(person, "order", EcorePackage.Literals.ESTRING, "order");

		MappingResult result = map();

		Entity entity = entity(result, "Person");
		assertThat(entity.getTable().getName()).isEqualTo("person_tbl");
		assertThat(id(entity).getColumn().getName()).isEqualTo("person_id");
		assertThat(basic(entity, "firstName").getColumn().getName()).isEqualTo("first_name");
		assertThat(basic(entity, "order").getColumn().getName()).isEqualTo("order");
		assertThat(result.diagnostics()).noneMatch(d -> d.getMessage().startsWith("Column name 'order'"));
	}

	@Test
	void elementsWithoutNamesKeepTheDerivedOnes() {
		EClass tag = eClass("Tag", null);
		EAttribute id = attribute(tag, "id", EcorePackage.Literals.ELONG, null);
		id.setID(true);
		attribute(tag, "label", EcorePackage.Literals.ESTRING, null);

		Entity entity = entity(map(), "Tag");

		assertThat(entity.getTable().getName()).isEqualTo("TAG");
		assertThat(basic(entity, "label").getColumn().getName()).isEqualTo("label");
		// a numeric id without a declaration still gets its sequence
		assertThat(id(entity).getSequenceGenerator()).isNotNull();
	}

	@Test
	void identityKeyIsGeneratedByTheDatabase() {
		EClass counter = eClass("Counter", "COUNTER");
		EAttribute id = attribute(counter, "id", EcorePackage.Literals.ELONG, "ID");
		id.setID(true);
		DatabaseAnnotations.set(id, DatabaseAnnotations.GENERATED, DatabaseAnnotations.GENERATED_IDENTITY);

		Id mapped = id(entity(map(), "Counter"));

		assertThat(mapped.getGeneratedValue().getStrategy()).isEqualTo(GenerationType.IDENTITY);
		assertThat(mapped.getSequenceGenerator()).isNull();
	}

	@Test
	void assignedKeyGetsNoGenerator() {
		EClass tag = eClass("Tag", "TAG");
		EAttribute id = attribute(tag, "id", EcorePackage.Literals.ELONG, "ID");
		id.setID(true);
		DatabaseAnnotations.set(id, DatabaseAnnotations.GENERATED, DatabaseAnnotations.GENERATED_NONE);

		Id mapped = id(entity(map(), "Tag"));

		assertThat(mapped.getGeneratedValue()).isNull();
		assertThat(mapped.getSequenceGenerator()).isNull();
	}

	@Test
	void unknownGenerationIsAnError() {
		EClass tag = eClass("Tag", "TAG");
		EAttribute id = attribute(tag, "id", EcorePackage.Literals.ELONG, "ID");
		id.setID(true);
		DatabaseAnnotations.set(id, DatabaseAnnotations.GENERATED, "sequence");

		MappingResult result = map();

		assertThat(result.getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThat(result.diagnostics()).anyMatch(d -> d.getMessage().contains("'sequence'"));
	}

	@Test
	void twoReferencesToOneTypeKeepTheirOwnColumns() {
		EClass account = keyed("Account", "ACCOUNT");
		EClass transfer = keyed("Transfer", "TRANSFER");
		reference(transfer, "source", account, "SOURCE_ID");
		reference(transfer, "target", account, "TARGET_ID");

		Entity entity = entity(map(), "Transfer");

		assertThat(joinColumns(entity, "source")).singleElement().satisfies(jc -> {
			assertThat(jc.getName()).isEqualTo("SOURCE_ID");
			assertThat(jc.getReferencedColumnName()).isEqualTo("ACCOUNT.ID");
			assertThat(jc.isInsertable()).isTrue();
		});
		assertThat(joinColumns(entity, "target")).extracting(JoinColumn::getName)
				.containsExactly("TARGET_ID");
	}

	@Test
	void compositeKeyIsReferencedColumnByColumn() {
		EClass lot = eClass("Lot", "LOT");
		attribute(lot, "plant", EcorePackage.Literals.EINT, "PLANT");
		attribute(lot, "lotno", EcorePackage.Literals.EINT, "LOTNO");
		lot.getEAnnotations().add(compositeId("plant,lotno"));
		EClass sample = keyed("Sample", "SAMPLE");
		EReference ref = reference(sample, "lot", lot, "S_PLANT,S_LOTNO");
		DatabaseAnnotations.set(ref, DatabaseAnnotations.REFERENCED_COLUMNS, "PLANT,LOTNO");

		List<JoinColumn> joinColumns = joinColumns(entity(map(), "Sample"), "lot");

		assertThat(joinColumns).extracting(JoinColumn::getName).containsExactly("S_PLANT", "S_LOTNO");
		assertThat(joinColumns).extracting(JoinColumn::getReferencedColumnName)
				.containsExactly("LOT.PLANT", "LOT.LOTNO");
	}

	@Test
	void joinColumnsThatDoNotCoverTheKeyAreAnError() {
		EClass account = keyed("Account", "ACCOUNT");
		EClass transfer = keyed("Transfer", "TRANSFER");
		reference(transfer, "source", account, "SOURCE_ID,SOURCE_BRANCH");

		MappingResult result = map();

		assertThat(result.getSeverity()).isEqualTo(Diagnostic.ERROR);
		assertThat(result.diagnostics()).anyMatch(d -> d.getMessage().contains("SOURCE_BRANCH")
				&& d.getMessage().contains("'source'"));
		assertThat(joinColumns(entity(result, "Transfer"), "source")).isEmpty();
	}

	@Test
	void keyColumnOfAnIdentifyingRelationshipIsOnlyRead() {
		keyed("Invoice", "INVOICE");
		EClass line = eClass("InvoiceLine", "INVOICE_LINE");
		attribute(line, "invoiceId", EcorePackage.Literals.ELONG, "INVOICE_ID");
		attribute(line, "lineNo", EcorePackage.Literals.EINT, "LINE_NO");
		line.getEAnnotations().add(compositeId("invoiceId,lineNo"));
		reference(line, "invoice", (EClass) ePackage.getEClassifier("Invoice"), "INVOICE_ID");

		JoinColumn jc = joinColumns(entity(map(), "InvoiceLine"), "invoice").get(0);

		assertThat(jc.getName()).isEqualTo("INVOICE_ID");
		assertThat(jc.isInsertable()).isFalse();
		assertThat(jc.isUpdatable()).isFalse();
	}

	@Test
	void junctionTableIsNamedWithItsColumns() {
		EClass users = keyed("Users", "USERS");
		EClass roles = keyed("Roles", "ROLES");
		EReference userRoles = many(users, "roles", roles);
		EReference roleUsers = many(roles, "users", users);
		userRoles.setEOpposite(roleUsers);
		roleUsers.setEOpposite(userRoles);
		junction(userRoles, "user_roles", "USER_ID", "ROLE_ID");
		junction(roleUsers, "user_roles", "ROLE_ID", "USER_ID");

		MappingResult result = map();

		JoinTable jt = ownerSide(result, "Users", "roles", "Roles", "users");
		assertThat(jt.getName()).isEqualTo("user_roles");
		boolean usersOwns = manyToMany(entity(result, "Users"), "roles").getMappedBy() == null;
		assertThat(jt.getJoinColumn()).extracting(JoinColumn::getName)
				.containsExactly(usersOwns ? "USER_ID" : "ROLE_ID");
		assertThat(jt.getInverseJoinColumn()).extracting(JoinColumn::getName)
				.containsExactly(usersOwns ? "ROLE_ID" : "USER_ID");
		assertThat(jt.getJoinColumn().get(0).getReferencedColumnName())
				.isEqualTo(usersOwns ? "USERS.ID" : "ROLES.ID");
	}

	// --- model building ---

	private EClass eClass(String name, String table) {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(name);
		if (table != null) {
			DatabaseAnnotations.set(eClass, DatabaseAnnotations.TABLE_NAME, table);
		}
		ePackage.getEClassifiers().add(eClass);
		return eClass;
	}

	/** A class with the single id {@code id} in column {@code ID}. */
	private EClass keyed(String name, String table) {
		EClass eClass = eClass(name, table);
		EAttribute id = attribute(eClass, "id", EcorePackage.Literals.ELONG, "ID");
		id.setID(true);
		DatabaseAnnotations.set(id, DatabaseAnnotations.GENERATED, DatabaseAnnotations.GENERATED_NONE);
		return eClass;
	}

	private static EAttribute attribute(EClass owner, String name, EClassifier type, String column) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(type);
		if (column != null) {
			DatabaseAnnotations.set(attribute, DatabaseAnnotations.COLUMN_NAME, column);
		}
		owner.getEStructuralFeatures().add(attribute);
		return attribute;
	}

	private static EReference reference(EClass owner, String name, EClass target, String joinColumns) {
		EReference reference = EcoreFactory.eINSTANCE.createEReference();
		reference.setName(name);
		reference.setEType(target);
		DatabaseAnnotations.set(reference, DatabaseAnnotations.JOIN_COLUMNS, joinColumns);
		owner.getEStructuralFeatures().add(reference);
		return reference;
	}

	private static EReference many(EClass owner, String name, EClass target) {
		EReference reference = EcoreFactory.eINSTANCE.createEReference();
		reference.setName(name);
		reference.setEType(target);
		reference.setUpperBound(-1);
		owner.getEStructuralFeatures().add(reference);
		return reference;
	}

	private static void junction(EReference reference, String table, String toOwner, String toTarget) {
		DatabaseAnnotations.set(reference, DatabaseAnnotations.JOIN_TABLE, table);
		DatabaseAnnotations.set(reference, DatabaseAnnotations.JOIN_COLUMNS, toOwner);
		DatabaseAnnotations.set(reference, DatabaseAnnotations.INVERSE_JOIN_COLUMNS, toTarget);
	}

	private static EAnnotation compositeId(String features) {
		EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
		annotation.setSource(CompositeIds.ANNOTATION_SOURCE);
		annotation.getDetails().put(CompositeIds.ID_FEATURES, features);
		return annotation;
	}

	// --- mapping access ---

	private MappingResult map() {
		return new EntityMapper().createMappingsFromEPackageWithDiagnostics(ePackage);
	}

	private static Entity entity(MappingResult result, String name) {
		return result.mappings().getEntity().stream().filter(e -> name.equals(e.getName())).findFirst().orElseThrow();
	}

	private static Id id(Entity entity) {
		return entity.getAttributes().getId().get(0);
	}

	private static Basic basic(Entity entity, String name) {
		return entity.getAttributes().getBasic().stream().filter(b -> name.equals(b.getName())).findFirst()
				.orElseThrow();
	}

	/** The join columns of a single-valued reference — a one-to-one without a reverse side. */
	private static List<JoinColumn> joinColumns(Entity entity, String name) {
		return entity.getAttributes().getManyToOne().stream().filter(m -> name.equals(m.getName()))
				.map(ManyToOne::getJoinColumn).findFirst()
				.or(() -> entity.getAttributes().getOneToOne().stream().filter(m -> name.equals(m.getName()))
						.map(OneToOne::getJoinColumn).findFirst())
				.map(List::copyOf).orElseThrow();
	}

	private static ManyToMany manyToMany(Entity entity, String name) {
		return entity.getAttributes().getManyToMany().stream().filter(m -> name.equals(m.getName())).findFirst()
				.orElseThrow();
	}

	/** The join table of whichever side owns the pair; the other side is mapped by it. */
	private static JoinTable ownerSide(MappingResult result, String a, String refA, String b, String refB) {
		ManyToMany sideA = manyToMany(entity(result, a), refA);
		ManyToMany sideB = manyToMany(entity(result, b), refB);
		ManyToMany owner = sideA.getMappedBy() == null ? sideA : sideB;
		assertThat(owner.getJoinTable()).isNotNull();
		return owner.getJoinTable();
	}
}
