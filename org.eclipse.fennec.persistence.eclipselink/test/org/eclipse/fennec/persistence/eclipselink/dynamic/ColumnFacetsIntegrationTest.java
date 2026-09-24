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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.persistence.eorm.Basic;
import org.eclipse.fennec.persistence.eorm.Column;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.epersistence.EPersistenceFactory;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * The eorm column facets {@code length}, {@code columnDefinition} and {@code Lob} of a basic
 * attribute reach the generated DDL and the stored values (issue #312).
 */
class ColumnFacetsIntegrationTest {

	private EntityManagerFactory emf;
	private Server serverSession;
	private EClass noteClass;

	@BeforeEach
	void setUp() {
		EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
		pkg.setName("facets");
		pkg.setNsURI("http://test/facets/" + UUID.randomUUID());
		pkg.setNsPrefix("facets");
		noteClass = EcoreFactory.eINSTANCE.createEClass();
		noteClass.setName("Note");
		pkg.getEClassifiers().add(noteClass);
		addAttribute("id", EcorePackage.Literals.ELONG).setID(true);
		addAttribute("code", EcorePackage.Literals.ESTRING);
		addAttribute("summary", EcorePackage.Literals.ESTRING);
		addAttribute("body", EcorePackage.Literals.ESTRING);
		addAttribute("payload", EcorePackage.Literals.EBYTE_ARRAY);
		addAttribute("plain", EcorePackage.Literals.ESTRING);
		addAttribute("count", EcorePackage.Literals.EINT);
	}

	@AfterEach
	void tearDown() {
		if (emf != null && emf.isOpen()) {
			emf.close();
		}
	}

	@Test
	@DisplayName("Column.length becomes the VARCHAR length")
	void lengthReachesDdl() {
		EntityMappings mappings = mappings();
		column(mappings, "code").setLength(10);
		initEclipseLink(mappings);

		assertThat(columnInfo("code")).containsExactly("CHARACTER VARYING", 10L);
	}

	@Test
	@DisplayName("Column.length is enforced by the database")
	void lengthIsEnforced() {
		EntityMappings mappings = mappings();
		column(mappings, "code").setLength(10);
		initEclipseLink(mappings);

		EObject note = newNote(1L);
		note.eSet(noteClass.getEStructuralFeature("code"), "12345678901");

		assertThatThrownBy(() -> persist(note)).isNotNull();
	}

	@Test
	@DisplayName("Column.columnDefinition replaces the derived column type")
	void columnDefinitionReachesDdl() {
		EntityMappings mappings = mappings();
		column(mappings, "summary").setColumnDefinition("VARCHAR(2000)");
		initEclipseLink(mappings);

		assertThat(columnInfo("summary")).containsExactly("CHARACTER VARYING", 2000L);
	}

	/*
	 * A Lob is mapped the way EclipseLink's own LobMetadata maps @Lob: a Clob/Blob field
	 * classification plus a TypeConversionConverter. DDL generation then resets the column to
	 * Character[]/Byte[] and lets the platform choose — TEXT/BYTEA on PostgreSQL, while H2 2.x
	 * answers LONGVARCHAR/LONGVARBINARY with an unbounded CHARACTER/BINARY VARYING. So on H2
	 * the contract is asserted on the mapping and by the round trip, not by the type name.
	 */

	@Test
	@DisplayName("A Lob String is mapped as CLOB and round-trips a long value")
	void stringLobRoundTrips() {
		EntityMappings mappings = mappings();
		basic(mappings, "body").setLob(EORMFactory.eINSTANCE.createLob());
		initEclipseLink(mappings);

		assertLobMapping("body", Clob.class);

		String text = "x".repeat(100_000);
		EObject note = newNote(1L);
		note.eSet(noteClass.getEStructuralFeature("body"), text);
		persist(note);

		assertThat(load(1L).eGet(noteClass.getEStructuralFeature("body"))).isEqualTo(text);
	}

	@Test
	@DisplayName("A Lob byte[] is mapped as BLOB and round-trips its bytes")
	void binaryLobRoundTrips() {
		EntityMappings mappings = mappings();
		basic(mappings, "payload").setLob(EORMFactory.eINSTANCE.createLob());
		initEclipseLink(mappings);

		assertLobMapping("payload", Blob.class);

		byte[] bytes = new byte[50_000];
		Arrays.fill(bytes, (byte) 7);
		EObject note = newNote(1L);
		note.eSet(noteClass.getEStructuralFeature("payload"), bytes);
		persist(note);

		assertThat((byte[]) load(1L).eGet(noteClass.getEStructuralFeature("payload"))).isEqualTo(bytes);
	}

	@Test
	@DisplayName("Without facets a String column keeps the platform default and no converter")
	void noFacetsKeepDefault() {
		initEclipseLink(mappings());

		assertThat(columnInfo("plain").get(0)).isEqualTo("CHARACTER VARYING");
		DirectToFieldMapping mapping = (DirectToFieldMapping) descriptor().getMappingForAttributeName("plain");
		assertThat(mapping.getConverter()).isNull();
		assertThat(mapping.getField().getLength()).isZero();
	}

	@Test
	@DisplayName("A Lob on a non-character, non-binary attribute is refused, naming the attribute")
	void lobOnNumberIsRefused() {
		EntityMappings mappings = mappings();
		basic(mappings, "count").setLob(EORMFactory.eINSTANCE.createLob());

		assertThatThrownBy(() -> initEclipseLink(mappings))
				.hasStackTraceContaining("'count' declares a Lob");
	}

	// ── Helpers ─────────────────────────────────────────────────────────────

	private EAttribute addAttribute(String name, EDataType type) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(type);
		noteClass.getEStructuralFeatures().add(attribute);
		return attribute;
	}

	private EntityMappings mappings() {
		return new EntityMapper().createMappings(List.of(noteClass));
	}

	private Basic basic(EntityMappings mappings, String name) {
		Entity entity = mappings.getEntity().get(0);
		return entity.getAttributes().getBasic().stream()
				.filter(b -> name.equals(b.getName()))
				.findFirst()
				.orElseThrow();
	}

	private Column column(EntityMappings mappings, String name) {
		Basic basic = basic(mappings, name);
		if (basic.getColumn() == null) {
			Column column = EORMFactory.eINSTANCE.createColumn();
			column.setName(name);
			basic.setColumn(column);
		}
		return basic.getColumn();
	}

	private void assertLobMapping(String attribute, Class<?> dataClass) {
		DirectToFieldMapping mapping = (DirectToFieldMapping) descriptor().getMappingForAttributeName(attribute);
		assertThat(mapping.getFieldClassification()).isEqualTo(dataClass);
		assertThat(mapping.getConverter()).isInstanceOfSatisfying(TypeConversionConverter.class,
				c -> assertThat(c.getDataClass()).isEqualTo(dataClass));
	}

	private ClassDescriptor descriptor() {
		return serverSession.getDescriptorForAlias("Note");
	}

	private EObject newNote(long id) {
		EObject note = (EObject) descriptor().getInstantiationPolicy().buildNewInstance();
		note.eSet(noteClass.getEStructuralFeature("id"), id);
		return note;
	}

	private void persist(EObject object) {
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(object);
			em.getTransaction().commit();
		}
	}

	private EObject load(long id) {
		try (EntityManager em = emf.createEntityManager()) {
			return (EObject) em.find(descriptor().getJavaClass(), id);
		}
	}

	/**
	 * @return the column's data type and character length, as H2 reports them
	 */
	private List<Object> columnInfo(String columnName) {
		try (EntityManager em = emf.createEntityManager()) {
			Object[] row = (Object[]) em.createNativeQuery(
					"SELECT DATA_TYPE, CHARACTER_MAXIMUM_LENGTH FROM INFORMATION_SCHEMA.COLUMNS"
					+ " WHERE UPPER(TABLE_NAME) = 'NOTE' AND UPPER(COLUMN_NAME) = ?1")
					.setParameter(1, columnName.toUpperCase())
					.getSingleResult();
			return Arrays.asList(row[0], row[1] == null ? null : ((Number) row[1]).longValue());
		}
	}

	private void initEclipseLink(EntityMappings mappings) {
		DynamicClassLoader dcl = new DynamicClassLoader(getClass().getClassLoader());

		Map<String, Object> props = new HashMap<>();
		props.put(PersistenceUnitProperties.CLASSLOADER, dcl);
		props.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.NONE);
		props.put(PersistenceUnitProperties.JDBC_DRIVER, "org.h2.Driver");
		props.put(PersistenceUnitProperties.JDBC_URL, "jdbc:h2:mem:facets_" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
		props.put(PersistenceUnitProperties.JDBC_USER, "sa");
		props.put(PersistenceUnitProperties.JDBC_PASSWORD, "");
		props.put(PersistenceUnitProperties.LOGGING_LEVEL, "WARNING");
		props.put(PersistenceUnitProperties.WEAVING, "false");
		props.put(PersistenceUnitProperties.TARGET_DATABASE, "Auto");
		props.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");

		PersistenceUnit pu = EPersistenceFactory.eINSTANCE.createPersistenceUnit();
		pu.setName("facets_test");
		pu.setProperties(EPersistenceFactory.eINSTANCE.createProperties());
		URL puRoot = getClass().getProtectionDomain().getCodeSource().getLocation();
		EDynamicPersistenceUnitInfo pui = new EDynamicPersistenceUnitInfo(pu, puRoot, props);

		PersistenceProvider provider = new PersistenceProvider();
		emf = provider.createContainerEntityManagerFactory(pui, props);
		serverSession = JpaHelper.getServerSession(emf);

		EDynamicTypeGenerator generator = new EDynamicTypeGenerator(dcl, serverSession, "facets_test");
		List<EDynamicType> types = generator.createFromMapping(mappings);

		EDynamicHelper helper = new EDynamicHelper(emf, dcl);
		helper.addETypes(true, true, types);
	}
}
