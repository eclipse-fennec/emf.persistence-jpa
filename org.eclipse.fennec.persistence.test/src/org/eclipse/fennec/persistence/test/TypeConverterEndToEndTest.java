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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.sql.DataSource;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.orm.ORMConstants;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.jupiter.api.Test;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.Property.Type;
import org.osgi.test.common.annotation.Property.ValueSource;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * End-to-end test for type converters using EORMMappingProvider to automatically
 * generate mappings for TypeConverterTestEntity. This test verifies the complete
 * pipeline: EObject → Database → EObject with all supported type conversions.
 * 
 * @author Mark Hoffmann
 * @since 14.01.2025
 */
public class TypeConverterEndToEndTest extends EPersistenceModelBase {

    private static final String DATASOURCE_PID = "daanse.jdbc.datasource.h2.DataSource";

    @Override
    void beforeModelRegistered(EPackage modelPackage) {
        // Setup can go here if needed
    }

    @Override
    void afterModelRegistered(EPackage modelPackage) {
        // Additional setup after model registration
    }

    @WithFactoryConfiguration(factoryPid = DATASOURCE_PID, name = "typeconverter-test", location = "?", properties = {
            @Property(key = "name", value = "h2typeconverter"),
            @Property(key = "identifier", value = "./data/typeconverter_test")
    })
    @WithFactoryConfiguration(factoryPid = ORMConstants.ORM_MAPPING_SERVICE_PID, name = "typeconverter-test", properties = {
            @Property(key = ORMConstants.PROPERTY_PREFIX + "eClasses", type = Type.Array, source = ValueSource.Value, value = {"TypeConverterTestEntity"}),
            @Property(key = ORMConstants.PROPERTY_PREFIX + "mappingName", value = "typeConverterModel"),
            @Property(key = ORMConstants.PROPERTY_PREFIX + "model.target", value = "(emf.name=fennec.persistence.model)"),
            @Property(key = ORMConstants.PROPERTY_PREFIX + "strict", value = "false")
    })
    @WithFactoryConfiguration(factoryPid = "fennec.jpa.EMPersistenceUnit", name = "typeconverter-test", properties = {
            @Property(key = "fennec.jpa.dataSource.target", value = "(identifier=./data/typeconverter_test)"),
            @Property(key = "fennec.jpa.mapping.target", value = "(fennec.jpa.eorm.mapping=typeConverterModel)"),
            @Property(key = "fennec.jpa.persistenceUnitName", value = "typeConverter"),
            @Property(key = "fennec.jpa.ext.eclipselink.target-database", value = "org.eclipse.persistence.platform.database.H2Platform"),
            @Property(key = "fennec.jpa.ext.eclipselink.ddl-generation", value = "create-or-extend-tables"),
            @Property(key = "fennec.jpa.ext.eclipselink.logging.level.sql", value = "FINE"),
            @Property(key = "fennec.jpa.ext.eclipselink.logging.parameters", value = "true")
    })
    @Test
    public void testTypeConverterEndToEndPipeline(
            @InjectService(timeout = 5000, filter = "(fennec.jpa.eorm.mapping=typeConverterModel)") ServiceAware<EntityMappings> mappingAware,
            @InjectService(timeout = 10000, filter = "(identifier=./data/typeconverter_test)") ServiceAware<DataSource> dataSourceAware,
            @InjectService(timeout = 15000, filter = "(osgi.unit.name=typeConverter)") ServiceAware<EntityManagerFactory> emfAware)
            throws Exception {

        // Verify services are available
        assertNotNull(mappingAware.getService(), "EntityMappings should be available");
        assertNotNull(dataSourceAware.getService(), "DataSource should be available");
        assertNotNull(emfAware.getService(), "EntityManagerFactory should be available");

        // Get the model package and test entity class
        EPackage modelPackage = rs.getPackageRegistry().getEPackage("https://projects.eclipse.org/projects/modeling.fennec/fpm");
        assertNotNull(modelPackage, "Model package should be available");
        
        EClass testEntityClass = (EClass) modelPackage.getEClassifier("TypeConverterTestEntity");
        assertNotNull(testEntityClass, "TypeConverterTestEntity should be available in model");
        
     // Persist to database
        EntityManagerFactory emf = emfAware.getService();
        Server server = JpaHelper.getServerSession(emf);
		ClassDescriptor testEntityDescriptor = server.getDescriptorForAlias(testEntityClass.getName());
		assertNotNull(testEntityDescriptor, "TestEntity descriptor should exist");

        // Create test data with all supported types
        LocalDateTime testDateTime = LocalDateTime.of(2025, 1, 14, 15, 30, 45);
        Instant testInstant = Instant.now();
        ZonedDateTime testZonedDateTime = ZonedDateTime.of(2025, 1, 14, 16, 45, 0, 0, ZoneId.of("UTC"));
        Duration testDuration = Duration.ofHours(2).plusMinutes(30);
        UUID testUuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        int[] testIntArray = {10, 20, 30, 40, 50};
        double[] testDoubleArray = {1.1, 2.2, 3.3, 4.4, 5.5};
        boolean[] testBooleanArray = {true, false, true, false, true};

        // Create and populate EObject
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

        // Clean up any existing data from previous test runs
        try (EntityManager cleanEm = emf.createEntityManager()) {
            cleanEm.getTransaction().begin();
            cleanEm.createQuery("DELETE FROM " + testEntityClass.getName()).executeUpdate();
            cleanEm.getTransaction().commit();
        }

        try (EntityManager entityManager = emf.createEntityManager();){
            entityManager.getTransaction().begin();
            entityManager.persist(testEntity);
            entityManager.getTransaction().commit();
            
            // Clear the persistence context to ensure we're reading from database
            entityManager.clear();

            // Verify data was written correctly to database
            verifyDatabaseContent(dataSourceAware.getService(), testDateTime, testInstant, 
                                testZonedDateTime, testDuration, testUuid, 
                                testIntArray, testDoubleArray, testBooleanArray);
    		
            // Read back from database
            EObject loadedEntity = entityManager.find(testEntityDescriptor.getJavaClass(), 1);
            assertNotNull(loadedEntity, "Should be able to load entity from database");
            assertNotEquals(testEntity, loadedEntity);

            // Verify all type conversions worked correctly
            assertEquals(1, getEObjectValue(loadedEntity, "id"));
            assertEquals("Type Converter Test Entity", getEObjectValue(loadedEntity, "name"));
            
            // Verify Java 8+ Time API conversions
            assertEquals(testDateTime, getEObjectValue(loadedEntity, "createdDateTime"));
            assertEquals(testInstant, getEObjectValue(loadedEntity, "lastAccessInstant"));
            assertEquals(testZonedDateTime, getEObjectValue(loadedEntity, "scheduledTime"));
            assertEquals(testDuration, getEObjectValue(loadedEntity, "processingDuration"));
            
            // Verify UUID conversion
            assertEquals(testUuid, getEObjectValue(loadedEntity, "entityUuid"));
            
            // Verify array conversions
            assertArrayEquals(testIntArray, (int[]) getEObjectValue(loadedEntity, "scores"));
            assertArrayEquals(testDoubleArray, (double[]) getEObjectValue(loadedEntity, "weights"), 0.001);
            assertArrayEquals(testBooleanArray, (boolean[]) getEObjectValue(loadedEntity, "flags"));

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

    private void verifyDatabaseContent(DataSource dataSource, LocalDateTime testDateTime, 
                                     Instant testInstant, ZonedDateTime testZonedDateTime,
                                     Duration testDuration, UUID testUuid,
                                     int[] testIntArray, double[] testDoubleArray, 
                                     boolean[] testBooleanArray) throws Exception {
        
        try (Connection conn = dataSource.getConnection()) {
            // First check if the table exists and has data
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM TYPECONVERTERTESTENTITY");
                assertTrue(rs.next(), "Should have result");
                assertEquals(1, rs.getInt(1), "Should have exactly one record");
            }

            // Verify the actual data values
            String sql = """
                SELECT ID, NAME, CREATEDDATETIME, LASTACCESSINSTANT, SCHEDULEDTIME, 
                       PROCESSINGDURATION, ENTITYUUID, SCORES, WEIGHTS, FLAGS 
                FROM TYPECONVERTERTESTENTITY WHERE ID = ?
                """;
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, 1);
                ResultSet rs = pstmt.executeQuery();
                
                assertTrue(rs.next(), "Should find record with ID 1");
                
                // Verify basic fields
                assertEquals(1, rs.getInt("ID"));
                assertEquals("Type Converter Test Entity", rs.getString("NAME"));
                
                // Verify time fields - these should be stored as database time types
                assertNotNull(rs.getTimestamp("CREATEDDATETIME"), "DateTime should be stored");
                assertNotNull(rs.getTimestamp("LASTACCESSINSTANT"), "Instant should be stored");
                assertNotNull(rs.getString("SCHEDULEDTIME"), "ZonedDateTime should be stored as ISO-8601 string");
                assertNotNull(rs.getLong("PROCESSINGDURATION"), "Duration should be stored as millis");
                
                // Verify UUID is stored as string
                assertEquals(testUuid.toString(), rs.getString("ENTITYUUID"));

                // Verify arrays are stored as BLOB (serialized byte[])
                assertNotNull(rs.getBytes("SCORES"), "Int array should be stored as BLOB");
                assertNotNull(rs.getBytes("WEIGHTS"), "Double array should be stored as BLOB");
                assertNotNull(rs.getBytes("FLAGS"), "Boolean array should be stored as BLOB");
            }
        }
    }
}