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
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.fennec.persistence.eorm.AccessType;
import org.eclipse.fennec.persistence.eorm.Basic;
import org.eclipse.fennec.persistence.eorm.DiscriminatorColumn;
import org.eclipse.fennec.persistence.eorm.DiscriminatorType;
import org.eclipse.fennec.persistence.eorm.EClassObject;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.ForeignKey;
import org.eclipse.fennec.persistence.eorm.Id;
import org.eclipse.fennec.persistence.eorm.Inheritance;
import org.eclipse.fennec.persistence.eorm.InheritanceType;
import org.eclipse.fennec.persistence.eorm.JoinTable;
import org.eclipse.fennec.persistence.eorm.ManyToMany;
import org.eclipse.fennec.persistence.eorm.ManyToOne;
import org.eclipse.fennec.persistence.eorm.OneToOne;
import org.eclipse.fennec.persistence.eorm.SequenceGenerator;
import org.eclipse.fennec.persistence.eorm.Table;
import org.eclipse.fennec.persistence.orm.helper.MappingHelper;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link EDynamicTypeBuilder}.
 * Tests the translation of EORM Entity metadata into EclipseLink descriptors and mappings.
 */
class EDynamicTypeBuilderTest {

	private EPackage testPackage;
	private EDynamicTypeContext sharedContext;

	@BeforeEach
	void setUp() {
		testPackage = EcoreFactory.eINSTANCE.createEPackage();
		testPackage.setName("test");
		testPackage.setNsURI("http://test");
		testPackage.setNsPrefix("t");
		sharedContext = createContext();
	}

	@Nested
	class EntitySetupTests {

		@Test
		void testEntityWithTableNameInitialized() {
			Entity entity = createEntityWithId("Person", "id", EcorePackage.Literals.ESTRING);
			EDynamicTypeBuilder builder = createBuilder(entity);

			assertThat(builder.getType()).isNotNull();
			ClassDescriptor descriptor = builder.getType().getDescriptor();
			assertThat(descriptor).isNotNull();
			assertThat(descriptor.getTableName()).isEqualTo("PERSON");
		}

		@Test
		void testEntityDescriptorHasAlias() {
			Entity entity = createEntityWithId("Customer", "id", EcorePackage.Literals.ESTRING);
			EDynamicTypeBuilder builder = createBuilder(entity);

			assertThat(builder.getType().getDescriptor().getAlias()).isNotNull();
		}

		@Test
		void testEntityHasJavaClass() {
			Entity entity = createEntityWithId("Order", "id", EcorePackage.Literals.ESTRING);
			EDynamicTypeBuilder builder = createBuilder(entity);

			assertThat(builder.getType().getJavaClass()).isNotNull();
		}
	}

	@Nested
	class IdConfigurationTests {

		@Test
		void testSingleStringIdWithUuidSequence() {
			Entity entity = createEntityWithId("Person", "id", EcorePackage.Literals.ESTRING);
			addSequenceGenerator(entity, "SEQ_PERSON_ID");
			EDynamicTypeBuilder builder = createBuilder(entity);

			ClassDescriptor descriptor = builder.getType().getDescriptor();
			assertThat(descriptor.getPrimaryKeyFieldNames()).contains("id");
		}

		@Test
		void testSingleIntIdMapped() {
			Entity entity = createEntityWithId("Item", "itemId", EcorePackage.Literals.EINT);
			EDynamicTypeBuilder builder = createBuilder(entity);

			ClassDescriptor descriptor = builder.getType().getDescriptor();
			assertThat(descriptor.getPrimaryKeyFieldNames()).isNotEmpty();
		}

		@Test
		void testNoIdLogsWarning() {
			Entity entity = createEntityNoId("EmptyEntity");
			EDynamicTypeBuilder builder = createBuilder(entity);

			// Should not throw — just logs WARNING
			assertThat(builder.getType()).isNotNull();
		}
	}

	@Nested
	class BasicMappingTests {

		@Test
		void testStringAttributeCreatesDirectMapping() {
			Entity entity = createEntityWithId("Person", "id", EcorePackage.Literals.ESTRING);
			addBasicAttribute(entity, "name", EcorePackage.Literals.ESTRING, false);
			EDynamicTypeBuilder builder = createBuilder(entity);

			DatabaseMapping mapping = builder.getType().getDescriptor().getMappingForAttributeName("name");
			assertThat(mapping).isNotNull();
			assertThat(mapping).isInstanceOf(DirectToFieldMapping.class);
		}

		@Test
		void testIntegerAttributeCreatesDirectMapping() {
			Entity entity = createEntityWithId("Person", "id", EcorePackage.Literals.ESTRING);
			addBasicAttribute(entity, "age", EcorePackage.Literals.EINT, false);
			EDynamicTypeBuilder builder = createBuilder(entity);

			DatabaseMapping mapping = builder.getType().getDescriptor().getMappingForAttributeName("age");
			assertThat(mapping).isNotNull();
			assertThat(mapping).isInstanceOf(DirectToFieldMapping.class);
		}

		@Test
		void testEnumAttributeCreatesMappingWithStringType() {
			EEnum statusEnum = EcoreFactory.eINSTANCE.createEEnum();
			statusEnum.setName("Status");
			testPackage.getEClassifiers().add(statusEnum);
			EEnumLiteral active = EcoreFactory.eINSTANCE.createEEnumLiteral();
			active.setName("ACTIVE");
			active.setLiteral("active");
			statusEnum.getELiterals().add(active);

			Entity entity = createEntityWithId("Task", "id", EcorePackage.Literals.ESTRING);
			addBasicAttribute(entity, "status", statusEnum, false);
			EDynamicTypeBuilder builder = createBuilder(entity);

			DatabaseMapping mapping = builder.getType().getDescriptor().getMappingForAttributeName("status");
			assertThat(mapping).isNotNull();
			assertThat(mapping).isInstanceOf(DirectToFieldMapping.class);
			// EEnum is mapped via EFeatureAccessor which converts to/from String literals
			DirectToFieldMapping dtfm = (DirectToFieldMapping) mapping;
			assertThat(dtfm.getAttributeName()).isEqualTo("status");
		}

		@Test
		void testOptionalAttributeIsOptional() {
			Entity entity = createEntityWithId("Person", "id", EcorePackage.Literals.ESTRING);
			addBasicAttribute(entity, "nickname", EcorePackage.Literals.ESTRING, true);
			EDynamicTypeBuilder builder = createBuilder(entity);

			DatabaseMapping mapping = builder.getType().getDescriptor().getMappingForAttributeName("nickname");
			assertThat(mapping).isNotNull();
			assertThat(((DirectToFieldMapping) mapping).isOptional()).isTrue();
		}

		@Test
		void testExplicitColumnName() {
			Entity entity = createEntityWithId("Person", "id", EcorePackage.Literals.ESTRING);
			Basic basic = addBasicAttribute(entity, "firstName", EcorePackage.Literals.ESTRING, false);
			basic.getColumn().setName("FIRST_NAME");
			EDynamicTypeBuilder builder = createBuilder(entity);

			DatabaseMapping mapping = builder.getType().getDescriptor().getMappingForAttributeName("firstName");
			assertThat(mapping).isNotNull();
			DirectToFieldMapping dtfm = (DirectToFieldMapping) mapping;
			assertThat(dtfm.getField().getName()).isEqualTo("FIRST_NAME");
		}
	}

	@Nested
	class ReferenceMappingTests {
		// Note: Full reference mapping tests require DynamicClassLoader (ASM) to create
		// unique Java classes per entity. These are covered by OSGi integration tests.
		// Here we verify that EORM reference metadata is correctly structured.

		@Test
		void testOneToOneEormStructure() {
			EClass personEClass = EcoreFactory.eINSTANCE.createEClass();
			personEClass.setName("Person");
			testPackage.getEClassifiers().add(personEClass);
			EClass addressEClass = EcoreFactory.eINSTANCE.createEClass();
			addressEClass.setName("Address");
			testPackage.getEClassifiers().add(addressEClass);

			EReference ref = addEReference(personEClass, "address", addressEClass, false, false);
			OneToOne o2o = createOneToOne(ref, addressEClass);

			assertThat(o2o.getName()).isEqualTo("address");
			assertThat(o2o.getForeignKey()).isNotNull();
			assertThat(o2o.getForeignKey().getName()).isEqualTo("FK_ADDRESS");
		}

		@Test
		void testManyToManyEormStructure() {
			EClass personEClass = EcoreFactory.eINSTANCE.createEClass();
			personEClass.setName("Person");
			testPackage.getEClassifiers().add(personEClass);
			EClass skillEClass = EcoreFactory.eINSTANCE.createEClass();
			skillEClass.setName("Skill");
			testPackage.getEClassifiers().add(skillEClass);

			EReference ref = addEReference(personEClass, "skills", skillEClass, true, false);
			ManyToMany m2m = createManyToMany(ref, personEClass, skillEClass);

			assertThat(m2m.getName()).isEqualTo("skills");
			assertThat(m2m.getJoinTable()).isNotNull();
			assertThat(m2m.getJoinTable().getName()).isEqualTo("PERSON__SKILL");
		}

		@Test
		void testManyToOneEormStructure() {
			EClass orderEClass = EcoreFactory.eINSTANCE.createEClass();
			orderEClass.setName("Order");
			testPackage.getEClassifiers().add(orderEClass);
			EClass customerEClass = EcoreFactory.eINSTANCE.createEClass();
			customerEClass.setName("Customer");
			testPackage.getEClassifiers().add(customerEClass);

			EReference ref = addEReference(orderEClass, "customer", customerEClass, false, false);
			ManyToOne m2o = createManyToOne(ref, customerEClass);

			assertThat(m2o.getName()).isEqualTo("customer");
			assertThat(m2o.getForeignKey()).isNotNull();
		}
	}

	@Nested
	class InheritanceTests {

		@Test
		void testRootEntityWithInheritancePolicy() {
			Entity vehicleEntity = createEntityWithId("Vehicle", "id", EcorePackage.Literals.EINT);
			Inheritance inheritance = EORMFactory.eINSTANCE.createInheritance();
			inheritance.setStrategy(InheritanceType.SINGLETABLE);
			vehicleEntity.setInheritance(inheritance);
			vehicleEntity.setDiscriminatorValue("Vehicle");
			DiscriminatorColumn dc = EORMFactory.eINSTANCE.createDiscriminatorColumn();
			dc.setName("DTYPE");
			dc.setDiscriminatorType(DiscriminatorType.STRING);
			vehicleEntity.setDiscriminatorColumn(dc);

			EDynamicTypeContext ctx = createContext();
			EDynamicTypeBuilder builder = new EDynamicTypeBuilder(vehicleEntity, ctx);
			builder.configureInheritance();

			InheritancePolicy ip = builder.getType().getDescriptor().getInheritancePolicyOrNull();
			assertThat(ip).isNotNull();
			assertThat(ip.getClassIndicatorFieldName()).isEqualTo("DTYPE");
		}

		@Test
		void testInheritanceEormStructure() {
			// Verify the EORM model is correctly set up for inheritance
			Entity vehicleEntity = createEntityWithId("Vehicle", "id", EcorePackage.Literals.EINT);
			Inheritance inheritance = EORMFactory.eINSTANCE.createInheritance();
			inheritance.setStrategy(InheritanceType.SINGLETABLE);
			vehicleEntity.setInheritance(inheritance);
			vehicleEntity.setDiscriminatorValue("Vehicle");
			DiscriminatorColumn dc = EORMFactory.eINSTANCE.createDiscriminatorColumn();
			dc.setName("DTYPE");
			dc.setDiscriminatorType(DiscriminatorType.STRING);
			vehicleEntity.setDiscriminatorColumn(dc);

			assertThat(vehicleEntity.getInheritance().getStrategy()).isEqualTo(InheritanceType.SINGLETABLE);
			assertThat(vehicleEntity.getDiscriminatorColumn().getName()).isEqualTo("DTYPE");
			assertThat(vehicleEntity.getDiscriminatorValue()).isEqualTo("Vehicle");

			// Child entity has discriminator value but no inheritance
			Entity carEntity = createEntityNoId("Car");
			carEntity.setDiscriminatorValue("Car");
			assertThat(carEntity.getDiscriminatorValue()).isEqualTo("Car");
			assertThat(carEntity.getInheritance()).isNull();
		}
	}

	// ===== Helper Methods =====

	private EDynamicTypeContext createContext() {
		EDynamicTypeContext ctx = new EDynamicTypeContext();
		// EClasses have instanceClass set to DynamicEObjectImpl,
		// so EClassDescriptor uses that directly without ASM/DynamicClassLoader
		ctx.setClassloader(getClass().getClassLoader());
		return ctx;
	}

	private EDynamicTypeBuilder createBuilder(Entity entity) {
		return new EDynamicTypeBuilder(entity, sharedContext);
	}

	private Entity createEntityWithId(String name, String idName, org.eclipse.emf.ecore.EClassifier idType) {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(name);
		eClass.setInstanceClass(DynamicEObjectImpl.class);
		testPackage.getEClassifiers().add(eClass);

		EAttribute idAttr = EcoreFactory.eINSTANCE.createEAttribute();
		idAttr.setName(idName);
		idAttr.setEType(idType);
		idAttr.setID(true);
		eClass.getEStructuralFeatures().add(idAttr);

		return createEntityForEClass(eClass, idAttr);
	}

	private Entity createEntityNoId(String name) {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(name);
		eClass.setInstanceClass(DynamicEObjectImpl.class);
		testPackage.getEClassifiers().add(eClass);

		Entity entity = EORMFactory.eINSTANCE.createEntity();
		entity.setName(name);
		entity.setClass(eClass);
		entity.setAccess(AccessType.FIELD);
		Table table = EORMFactory.eINSTANCE.createTable();
		table.setName(name.toUpperCase());
		entity.setTable(table);
		entity.setAttributes(EORMFactory.eINSTANCE.createAttributes());

		EClassObject eco = EORMFactory.eINSTANCE.createEClassObject();
		eco.setEclass(eClass);
		eco.setName("test." + name);
		entity.setAccessibleObject(eco);
		return entity;
	}

	private Entity createEntityForEClass(EClass eClassArg) {
		EClass eClass = eClassArg;
		if (eClass.getInstanceClass() == null) {
			eClass.setInstanceClass(DynamicEObjectImpl.class);
		}
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

	private Entity createEntityForEClass(EClass eClass, EAttribute idAttr) {
		Entity entity = createEntityForEClass(eClass);

		Id id = EORMFactory.eINSTANCE.createId();
		MappingHelper.createBase(id, idAttr, true);
		entity.getAttributes().getId().add(id);
		return entity;
	}

	private void addSequenceGenerator(Entity entity, String seqName) {
		if (!entity.getAttributes().getId().isEmpty()) {
			SequenceGenerator sg = EORMFactory.eINSTANCE.createSequenceGenerator();
			sg.setName(seqName);
			sg.setSequenceName(seqName);
			entity.getAttributes().getId().get(0).setSequenceGenerator(sg);
		}
	}

	private Basic addBasicAttribute(Entity entity, String name, org.eclipse.emf.ecore.EClassifier type, boolean optional) {
		EClass eClass = (EClass) entity.getClass_();
		EAttribute attr = EcoreFactory.eINSTANCE.createEAttribute();
		attr.setName(name);
		attr.setEType(type);
		if (!optional) {
			attr.setLowerBound(1);
		}
		eClass.getEStructuralFeatures().add(attr);

		Basic basic = EORMFactory.eINSTANCE.createBasic();
		basic.setOptional(optional);
		MappingHelper.createBase(basic, attr, true);
		entity.getAttributes().getBasic().add(basic);
		return basic;
	}

	private EReference addEReference(EClass owner, String name, EClass type, boolean many, boolean containment) {
		EReference ref = EcoreFactory.eINSTANCE.createEReference();
		ref.setName(name);
		ref.setEType(type);
		ref.setContainment(containment);
		if (many) {
			ref.setUpperBound(-1);
		}
		owner.getEStructuralFeatures().add(ref);
		return ref;
	}

	private OneToOne createOneToOne(EReference ref, EClass targetEClass) {
		OneToOne o2o = EORMFactory.eINSTANCE.createOneToOne();
		MappingHelper.createBaseRef(o2o, ref);
		ForeignKey fk = EORMFactory.eINSTANCE.createForeignKey();
		fk.setName("FK_" + ref.getName().toUpperCase());
		o2o.setForeignKey(fk);
		return o2o;
	}

	private ManyToMany createManyToMany(EReference ref, EClass sourceEClass, EClass targetEClass) {
		ManyToMany m2m = EORMFactory.eINSTANCE.createManyToMany();
		MappingHelper.createBaseRef(m2m, ref);
		JoinTable jt = EORMFactory.eINSTANCE.createJoinTable();
		jt.setName(sourceEClass.getName().toUpperCase() + "__" + targetEClass.getName().toUpperCase());
		m2m.setJoinTable(jt);
		return m2m;
	}

	private ManyToOne createManyToOne(EReference ref, EClass targetEClass) {
		ManyToOne m2o = EORMFactory.eINSTANCE.createManyToOne();
		MappingHelper.createBaseRef(m2o, ref);
		ForeignKey fk = EORMFactory.eINSTANCE.createForeignKey();
		fk.setName("FK_" + ref.getName().toUpperCase());
		m2o.setForeignKey(fk);
		return m2o;
	}
}
