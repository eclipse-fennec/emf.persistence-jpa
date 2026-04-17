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
package org.eclipse.fennec.persistence.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

/**
 * Non-OSGi port of {@code TypeConverterEndToEndTest}. Verifies the complete
 * conversion pipeline (LocalDateTime, Instant, ZonedDateTime, Duration, UUID,
 * int[], double[], boolean[]) against in-memory H2 via the shared base class.
 */
class NonOsgiTypeConverterEndToEndTest extends NonOsgiPersistenceTestBase {

	private EPackage modelPackage;
	private EClass testEntityClass;
	private String jdbcUrl;

	@BeforeEach
	void setUp() {
		modelPackage = loadEcore("data/model.ecore");
		testEntityClass = (EClass) modelPackage.getEClassifier("TypeConverterTestEntity");
		assertNotNull(testEntityClass, "TypeConverterTestEntity should exist in model");

		jdbcUrl = "jdbc:h2:mem:tc_" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1";
		Map<String, Object> extra = new HashMap<>();
		extra.put(PersistenceUnitProperties.JDBC_URL, jdbcUrl);
		bootstrapPersistence("typeConverter", List.of(testEntityClass), extra);
	}

	@Test
	void testTypeConverterEndToEndPipeline() throws Exception {
		ClassDescriptor testEntityDescriptor = serverSession.getDescriptorForAlias(testEntityClass.getName());
		assertNotNull(testEntityDescriptor, "TestEntity descriptor should exist");

		LocalDateTime testDateTime = LocalDateTime.of(2025, 1, 14, 15, 30, 45);
		Instant testInstant = Instant.now();
		ZonedDateTime testZonedDateTime = ZonedDateTime.of(2025, 1, 14, 16, 45, 0, 0, ZoneId.of("UTC"));
		Duration testDuration = Duration.ofHours(2).plusMinutes(30);
		UUID testUuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
		int[] testIntArray = {10, 20, 30, 40, 50};
		double[] testDoubleArray = {1.1, 2.2, 3.3, 4.4, 5.5};
		boolean[] testBooleanArray = {true, false, true, false, true};

		EObject testEntity = (EObject) testEntityDescriptor.getInstantiationPolicy().buildNewInstance();
		setEObjectValue(testEntity, "id", 1);
		setEObjectValue(testEntity, "name", "Type Converter Test Entity");
		setEObjectValue(testEntity, "createdDateTime", testDateTime);
		setEObjectValue(testEntity, "lastAccessInstant", testInstant);
		setEObjectValue(testEntity, "scheduledTime", testZonedDateTime);
		setEObjectValue(testEntity, "processingDuration", testDuration);
		setEObjectValue(testEntity, "entityUuid", testUuid);
		setEObjectValue(testEntity, "scores", testIntArray);
		setEObjectValue(testEntity, "weights", testDoubleArray);
		setEObjectValue(testEntity, "flags", testBooleanArray);

		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			em.persist(testEntity);
			em.getTransaction().commit();
			em.clear();

			verifyDatabaseContent(testUuid);

			EObject loaded = em.find(testEntityDescriptor.getJavaClass(), 1);
			assertNotNull(loaded);
			assertNotEquals(testEntity, loaded);

			assertEquals(1, getEObjectValue(loaded, "id"));
			assertEquals("Type Converter Test Entity", getEObjectValue(loaded, "name"));
			assertEquals(testDateTime, getEObjectValue(loaded, "createdDateTime"));
			assertEquals(testInstant, getEObjectValue(loaded, "lastAccessInstant"));
			assertEquals(testZonedDateTime, getEObjectValue(loaded, "scheduledTime"));
			assertEquals(testDuration, getEObjectValue(loaded, "processingDuration"));
			assertEquals(testUuid, getEObjectValue(loaded, "entityUuid"));
			assertArrayEquals(testIntArray, (int[]) getEObjectValue(loaded, "scores"));
			assertArrayEquals(testDoubleArray, (double[]) getEObjectValue(loaded, "weights"), 0.001);
			assertArrayEquals(testBooleanArray, (boolean[]) getEObjectValue(loaded, "flags"));
		}
	}

	private void setEObjectValue(EObject eObject, String featureName, Object value) {
		EStructuralFeature feature = eObject.eClass().getEStructuralFeature(featureName);
		assertNotNull(feature, "Feature " + featureName + " should exist");
		eObject.eSet(feature, value);
	}

	private Object getEObjectValue(EObject eObject, String featureName) {
		EStructuralFeature feature = eObject.eClass().getEStructuralFeature(featureName);
		assertNotNull(feature, "Feature " + featureName + " should exist");
		return eObject.eGet(feature);
	}

	private void verifyDatabaseContent(UUID testUuid) throws Exception {
		try (Connection conn = DriverManager.getConnection(jdbcUrl, "sa", "")) {
			try (Statement stmt = conn.createStatement();
				 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM TYPECONVERTERTESTENTITY")) {
				assertTrue(rs.next());
				assertEquals(1, rs.getInt(1));
			}

			String sql = """
					SELECT ID, NAME, CREATEDDATETIME, LASTACCESSINSTANT, SCHEDULEDTIME,
					       PROCESSINGDURATION, ENTITYUUID, SCORES, WEIGHTS, FLAGS
					FROM TYPECONVERTERTESTENTITY WHERE ID = ?
					""";
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setInt(1, 1);
				try (ResultSet rs = ps.executeQuery()) {
					assertTrue(rs.next());
					assertEquals(1, rs.getInt("ID"));
					assertEquals("Type Converter Test Entity", rs.getString("NAME"));
					assertNotNull(rs.getTimestamp("CREATEDDATETIME"));
					assertNotNull(rs.getTimestamp("LASTACCESSINSTANT"));
					assertNotNull(rs.getString("SCHEDULEDTIME"));
					assertNotNull(rs.getLong("PROCESSINGDURATION"));
					assertEquals(testUuid.toString(), rs.getString("ENTITYUUID"));
					assertNotNull(rs.getBytes("SCORES"));
					assertNotNull(rs.getBytes("WEIGHTS"));
					assertNotNull(rs.getBytes("FLAGS"));
				}
			}
		}
	}
}
