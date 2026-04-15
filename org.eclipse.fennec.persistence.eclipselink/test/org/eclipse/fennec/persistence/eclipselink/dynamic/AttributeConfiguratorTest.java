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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.UUID;

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
import org.eclipse.fennec.persistence.eorm.Id;
import org.eclipse.fennec.persistence.eorm.Table;
import org.eclipse.fennec.persistence.eorm.Version;
import org.eclipse.fennec.persistence.orm.helper.MappingHelper;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link AttributeConfigurator}.
 * Tests type mapping, version attributes, and converter detection.
 */
class AttributeConfiguratorTest {

	private EPackage testPackage;
	private EDynamicTypeContext context;

	@BeforeEach
	void setUp() {
		testPackage = EcoreFactory.eINSTANCE.createEPackage();
		testPackage.setName("test");
		testPackage.setNsURI("http://test/attr");
		testPackage.setNsPrefix("t");
		context = new EDynamicTypeContext();
		context.setClassloader(getClass().getClassLoader());
	}

	@Nested
	@DisplayName("Type mapping: isStandardDatabaseType")
	class StandardDatabaseTypeTests {

		private final AttributeConfigurator configurator = new AttributeConfigurator(null, null);

		@Test
		void testPrimitivesAreStandard() {
			assertThat(configurator.isStandardDatabaseType(int.class)).isTrue();
			assertThat(configurator.isStandardDatabaseType(long.class)).isTrue();
			assertThat(configurator.isStandardDatabaseType(boolean.class)).isTrue();
			assertThat(configurator.isStandardDatabaseType(double.class)).isTrue();
		}

		@Test
		void testWrappersAreStandard() {
			assertThat(configurator.isStandardDatabaseType(Integer.class)).isTrue();
			assertThat(configurator.isStandardDatabaseType(Long.class)).isTrue();
			assertThat(configurator.isStandardDatabaseType(Boolean.class)).isTrue();
			assertThat(configurator.isStandardDatabaseType(String.class)).isTrue();
		}

		@Test
		void testSqlTypesAreStandard() {
			assertThat(configurator.isStandardDatabaseType(java.sql.Date.class)).isTrue();
			assertThat(configurator.isStandardDatabaseType(java.sql.Timestamp.class)).isTrue();
			assertThat(configurator.isStandardDatabaseType(java.sql.Time.class)).isTrue();
			assertThat(configurator.isStandardDatabaseType(byte[].class)).isTrue();
		}

		@Test
		void testTemporalTypesAreNotStandard() {
			assertThat(configurator.isStandardDatabaseType(Instant.class)).isFalse();
			assertThat(configurator.isStandardDatabaseType(LocalDate.class)).isFalse();
			assertThat(configurator.isStandardDatabaseType(LocalDateTime.class)).isFalse();
			assertThat(configurator.isStandardDatabaseType(ZonedDateTime.class)).isFalse();
			assertThat(configurator.isStandardDatabaseType(java.time.OffsetDateTime.class)).isFalse();
			assertThat(configurator.isStandardDatabaseType(Duration.class)).isFalse();
		}

		@Test
		void testUuidIsNotStandard() {
			assertThat(configurator.isStandardDatabaseType(UUID.class)).isFalse();
		}

		@Test
		void testBigDecimalIsNotStandard() {
			assertThat(configurator.isStandardDatabaseType(BigDecimal.class)).isFalse();
			assertThat(configurator.isStandardDatabaseType(BigInteger.class)).isFalse();
		}

		@Test
		void testNullIsStandard() {
			assertThat(configurator.isStandardDatabaseType(null)).isTrue();
		}
	}

	@Nested
	@DisplayName("Type mapping: mapToDbFriendlyType")
	class DbFriendlyTypeTests {

		private final AttributeConfigurator configurator = new AttributeConfigurator(null, null);

		@Test
		void testTemporalTypesToTimestamp() {
			assertThat(configurator.mapToDbFriendlyType(Instant.class)).isEqualTo(java.sql.Timestamp.class);
			assertThat(configurator.mapToDbFriendlyType(LocalDateTime.class)).isEqualTo(java.sql.Timestamp.class);
			assertThat(configurator.mapToDbFriendlyType(LocalDate.class)).isEqualTo(java.sql.Timestamp.class);
			assertThat(configurator.mapToDbFriendlyType(LocalTime.class)).isEqualTo(java.sql.Timestamp.class);
		}

		@Test
		void testZonedDateTimeToString() {
			assertThat(configurator.mapToDbFriendlyType(ZonedDateTime.class)).isEqualTo(String.class);
		}

		@Test
		void testOffsetDateTimeToString() {
			assertThat(configurator.mapToDbFriendlyType(java.time.OffsetDateTime.class)).isEqualTo(String.class);
		}

		@Test
		void testDurationToLong() {
			assertThat(configurator.mapToDbFriendlyType(Duration.class)).isEqualTo(Long.class);
		}

		@Test
		void testUuidToString() {
			assertThat(configurator.mapToDbFriendlyType(UUID.class)).isEqualTo(String.class);
		}

		@Test
		void testBigDecimalPassthrough() {
			assertThat(configurator.mapToDbFriendlyType(BigDecimal.class)).isEqualTo(BigDecimal.class);
			assertThat(configurator.mapToDbFriendlyType(BigInteger.class)).isEqualTo(BigInteger.class);
		}

		@Test
		void testUriToString() {
			assertThat(configurator.mapToDbFriendlyType(java.net.URI.class)).isEqualTo(String.class);
			assertThat(configurator.mapToDbFriendlyType(java.net.URL.class)).isEqualTo(String.class);
		}
	}

	@Nested
	@DisplayName("Version attribute mapping")
	class VersionAttributeTests {

		@Test
		void testVersionAttributeCreatesDirectMapping() {
			Entity entity = createEntityWithVersionAttribute("Versioned", "versionNum", EcorePackage.Literals.EINT);
			EDynamicTypeBuilder builder = new EDynamicTypeBuilder(entity, context);

			DatabaseMapping mapping = builder.getType().getDescriptor().getMappingForAttributeName("versionNum");
			assertThat(mapping).isNotNull();
			assertThat(mapping).isInstanceOf(DirectToFieldMapping.class);
		}

		@Test
		void testVersionAttributeConfiguresLocking() {
			Entity entity = createEntityWithVersionAttribute("Versioned", "versionNum", EcorePackage.Literals.EINT);
			EDynamicTypeBuilder builder = new EDynamicTypeBuilder(entity, context);

			ClassDescriptor descriptor = builder.getType().getDescriptor();
			assertThat(descriptor.getOptimisticLockingPolicy()).isNotNull();
		}

		@Test
		void testVersionAccessorIsEFeatureAccessor() {
			Entity entity = createEntityWithVersionAttribute("Versioned", "versionNum", EcorePackage.Literals.EINT);
			EDynamicTypeBuilder builder = new EDynamicTypeBuilder(entity, context);

			DatabaseMapping mapping = builder.getType().getDescriptor().getMappingForAttributeName("versionNum");
			assertThat(mapping.getAttributeAccessor())
				.isInstanceOf(org.eclipse.fennec.persistence.eclipselink.mappings.EFeatureAccessor.class);
		}

		@Test
		void testVersionTypeDerivedFromEStructuralFeature() {
			// EInt → int, not Long
			Entity entity = createEntityWithVersionAttribute("Versioned", "versionNum", EcorePackage.Literals.EINT);
			EDynamicTypeBuilder builder = new EDynamicTypeBuilder(entity, context);

			DirectToFieldMapping mapping = (DirectToFieldMapping) builder.getType().getDescriptor()
					.getMappingForAttributeName("versionNum");
			assertThat(mapping.getAttributeClassification()).isEqualTo(int.class);
		}

		@Test
		void testVersionWithLongType() {
			Entity entity = createEntityWithVersionAttribute("Versioned", "versionNum", EcorePackage.Literals.ELONG);
			EDynamicTypeBuilder builder = new EDynamicTypeBuilder(entity, context);

			DirectToFieldMapping mapping = (DirectToFieldMapping) builder.getType().getDescriptor()
					.getMappingForAttributeName("versionNum");
			assertThat(mapping.getAttributeClassification()).isEqualTo(long.class);
		}
	}

	// ===== Helper Methods =====

	private Entity createEntityWithVersionAttribute(String entityName, String versionAttrName,
			org.eclipse.emf.ecore.EClassifier versionType) {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(entityName);
		eClass.setInstanceClass(DynamicEObjectImpl.class);
		testPackage.getEClassifiers().add(eClass);

		// ID attribute
		EAttribute idAttr = EcoreFactory.eINSTANCE.createEAttribute();
		idAttr.setName("id");
		idAttr.setEType(EcorePackage.Literals.ESTRING);
		idAttr.setID(true);
		eClass.getEStructuralFeatures().add(idAttr);

		// Version attribute
		EAttribute versionAttr = EcoreFactory.eINSTANCE.createEAttribute();
		versionAttr.setName(versionAttrName);
		versionAttr.setEType(versionType);
		eClass.getEStructuralFeatures().add(versionAttr);

		// EORM Entity
		Entity entity = EORMFactory.eINSTANCE.createEntity();
		entity.setName(entityName);
		entity.setClass(eClass);
		entity.setAccess(AccessType.FIELD);
		Table table = EORMFactory.eINSTANCE.createTable();
		table.setName(entityName.toUpperCase());
		entity.setTable(table);
		entity.setAttributes(EORMFactory.eINSTANCE.createAttributes());

		EClassObject eco = EORMFactory.eINSTANCE.createEClassObject();
		eco.setEclass(eClass);
		eco.setName("test." + entityName);
		entity.setAccessibleObject(eco);

		// ID mapping
		Id id = EORMFactory.eINSTANCE.createId();
		MappingHelper.createBase(id, idAttr, true);
		entity.getAttributes().getId().add(id);

		// Version mapping
		Version version = EORMFactory.eINSTANCE.createVersion();
		version.setName(versionAttrName);
		Column col = EORMFactory.eINSTANCE.createColumn();
		col.setName(versionAttrName.toUpperCase());
		version.setColumn(col);
		entity.getAttributes().getVersion().add(version);

		return entity;
	}
}
