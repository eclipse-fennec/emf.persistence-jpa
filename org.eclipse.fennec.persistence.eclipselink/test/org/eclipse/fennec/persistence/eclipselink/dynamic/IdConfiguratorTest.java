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
package org.eclipse.fennec.persistence.eclipselink.dynamic;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.fennec.persistence.eorm.AccessType;
import org.eclipse.fennec.persistence.eorm.Column;
import org.eclipse.fennec.persistence.eorm.EClassObject;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.GeneratedValue;
import org.eclipse.fennec.persistence.eorm.GenerationType;
import org.eclipse.fennec.persistence.eorm.Id;
import org.eclipse.fennec.persistence.eorm.SequenceGenerator;
import org.eclipse.fennec.persistence.eorm.Table;
import org.eclipse.fennec.persistence.orm.helper.MappingHelper;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link IdConfigurator}.
 * Tests single IDs, composite IDs, UUID generation, and sequence generators.
 */
class IdConfiguratorTest {

	private EPackage testPackage;
	private EDynamicTypeContext context;

	@BeforeEach
	void setUp() {
		testPackage = EcoreFactory.eINSTANCE.createEPackage();
		testPackage.setName("test");
		testPackage.setNsURI("http://test/id");
		testPackage.setNsPrefix("t");
		context = new EDynamicTypeContext();
		context.setClassloader(getClass().getClassLoader());
	}

	@Test
	@DisplayName("Single String ID sets primary key field")
	void testSingleStringId() {
		Entity entity = createEntityWithId("Person", "id", EcorePackage.Literals.ESTRING);
		EDynamicTypeBuilder builder = new EDynamicTypeBuilder(entity, context);

		ClassDescriptor descriptor = builder.getType().getDescriptor();
		assertThat(descriptor.getPrimaryKeyFieldNames()).containsExactly("id");
	}

	@Test
	@DisplayName("Single Int ID sets primary key field")
	void testSingleIntId() {
		Entity entity = createEntityWithId("Item", "itemId", EcorePackage.Literals.EINT);
		EDynamicTypeBuilder builder = new EDynamicTypeBuilder(entity, context);

		ClassDescriptor descriptor = builder.getType().getDescriptor();
		assertThat(descriptor.getPrimaryKeyFieldNames()).containsExactly("itemId");
	}

	@Test
	@DisplayName("ID with explicit column name uses column name as PK field")
	void testIdWithExplicitColumn() {
		Entity entity = createEntityWithId("Person", "id", EcorePackage.Literals.ESTRING);
		Id id = entity.getAttributes().getId().get(0);
		Column col = EORMFactory.eINSTANCE.createColumn();
		col.setName("PERSON_ID");
		id.setColumn(col);

		EDynamicTypeBuilder builder = new EDynamicTypeBuilder(entity, context);

		ClassDescriptor descriptor = builder.getType().getDescriptor();
		assertThat(descriptor.getPrimaryKeyFieldNames()).containsExactly("PERSON_ID");
	}

	@Test
	@DisplayName("Composite IDs set multiple primary key fields")
	void testCompositeIds() {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName("CompositeEntity");
		eClass.setInstanceClass(DynamicEObjectImpl.class);
		testPackage.getEClassifiers().add(eClass);

		EAttribute pk1 = EcoreFactory.eINSTANCE.createEAttribute();
		pk1.setName("tenantId");
		pk1.setEType(EcorePackage.Literals.ESTRING);
		pk1.setID(true);
		eClass.getEStructuralFeatures().add(pk1);

		EAttribute pk2 = EcoreFactory.eINSTANCE.createEAttribute();
		pk2.setName("userId");
		pk2.setEType(EcorePackage.Literals.ESTRING);
		pk2.setID(true);
		eClass.getEStructuralFeatures().add(pk2);

		Entity entity = createEntityForEClass(eClass);
		Id id1 = EORMFactory.eINSTANCE.createId();
		MappingHelper.createBase(id1, pk1, true);
		entity.getAttributes().getId().add(id1);

		Id id2 = EORMFactory.eINSTANCE.createId();
		MappingHelper.createBase(id2, pk2, true);
		entity.getAttributes().getId().add(id2);

		EDynamicTypeBuilder builder = new EDynamicTypeBuilder(entity, context);

		ClassDescriptor descriptor = builder.getType().getDescriptor();
		assertThat(descriptor.getPrimaryKeyFieldNames())
			.containsExactly("tenantId", "userId");
	}

	@Test
	@DisplayName("UUID generation strategy configures UUIDSequence")
	void testUuidGeneration() {
		Entity entity = createEntityWithId("UuidEntity", "id", EcorePackage.Literals.ESTRING);
		Id id = entity.getAttributes().getId().get(0);
		GeneratedValue gv = EORMFactory.eINSTANCE.createGeneratedValue();
		gv.setStrategy(GenerationType.UUID);
		id.setGeneratedValue(gv);
		SequenceGenerator sg = EORMFactory.eINSTANCE.createSequenceGenerator();
		sg.setName("UUID_SEQ");
		sg.setSequenceName("UUID_SEQ");
		id.setSequenceGenerator(sg);

		EDynamicTypeBuilder builder = new EDynamicTypeBuilder(entity, context);

		ClassDescriptor descriptor = builder.getType().getDescriptor();
		assertThat(descriptor.getPrimaryKeyFieldNames()).containsExactly("id");
		assertThat(descriptor.getSequenceNumberName()).isNotNull();
	}

	@Test
	@DisplayName("Entity without IDs does not throw")
	void testNoIdNoException() {
		Entity entity = createEntityForEClass(createEClass("EmptyEntity"));

		EDynamicTypeBuilder builder = new EDynamicTypeBuilder(entity, context);
		assertThat(builder.getType()).isNotNull();
		assertThat(builder.getType().getDescriptor().getPrimaryKeyFieldNames()).isEmpty();
	}

	// ===== Helper Methods =====

	private EClass createEClass(String name) {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(name);
		eClass.setInstanceClass(DynamicEObjectImpl.class);
		testPackage.getEClassifiers().add(eClass);
		return eClass;
	}

	private Entity createEntityWithId(String name, String idName, org.eclipse.emf.ecore.EClassifier idType) {
		EClass eClass = createEClass(name);

		EAttribute idAttr = EcoreFactory.eINSTANCE.createEAttribute();
		idAttr.setName(idName);
		idAttr.setEType(idType);
		idAttr.setID(true);
		eClass.getEStructuralFeatures().add(idAttr);

		Entity entity = createEntityForEClass(eClass);
		Id id = EORMFactory.eINSTANCE.createId();
		MappingHelper.createBase(id, idAttr, true);
		entity.getAttributes().getId().add(id);
		return entity;
	}

	private Entity createEntityForEClass(EClass eClass) {
		Entity entity = EORMFactory.eINSTANCE.createEntity();
		entity.setName(eClass.getName());
		entity.setClass(eClass);
		entity.setAccess(AccessType.FIELD);
		Table table = EORMFactory.eINSTANCE.createTable();
		table.setName(eClass.getName().toUpperCase());
		entity.setTable(table);
		entity.setAttributes(EORMFactory.eINSTANCE.createAttributes());

		EClassObject eco = EORMFactory.eINSTANCE.createEClassObject();
		eco.setEclass(eClass);
		eco.setName("test." + eClass.getName());
		entity.setAccessibleObject(eco);
		return entity;
	}
}
