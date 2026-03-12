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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.persistence.orm.ORMConstants;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.framework.BundleContext;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.Property.Type;
import org.osgi.test.common.annotation.Property.ValueSource;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Test for EclipseLink integration with composite primary keys.
 * 
 * This test verifies that our enhanced EDynamicTypeBuilder and composite ID
 * processing correctly configures EclipseLink for multiple primary key scenarios.
 * 
 * @author Mark Hoffmann
 * @since 28.01.2025
 */
public class CompositeIdEclipseLinkTest extends EPersistenceModelBase {

    private static final String DATASOURCE_PID = "org.eclipse.daanse.jdbc.datasource.metatype.h2.DataSource";
    private static final String MULTI_PK_CLASS = "MultiPKClass";
    
    @InjectBundleContext
    BundleContext bctx;
    
    @Override
    void beforeModelRegistered(EPackage modelPackage) {
        // Implementation for abstract method
    }
    
    @Override 
    void afterModelRegistered(EPackage modelPackage) {
        // Implementation for abstract method
    }
    
    @BeforeEach
    public void before() throws Exception {
        super.before(bctx);
    }
    
    /**
     * Setup database with test data for composite ID testing.
     */
    public void setupDatabase(ServiceAware<DataSource> dataSourceAware) throws Exception {
        if (!dataSourceAware.isEmpty()) {
            DataSource ds = dataSourceAware.getService();
            try (Connection conn = ds.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                // Clean up any existing tables
                try {
                    stmt.execute("DROP SCHEMA IF EXISTS PUBLIC CASCADE");
                    stmt.execute("CREATE SCHEMA PUBLIC");
                } catch (Exception e) {
                    // Fallback cleanup
                    try {
                        stmt.execute("DROP TABLE IF EXISTS MULTIPKCLASS");
                    } catch (Exception e2) {
                        // Ignore
                    }
                }
                
                // Create test tables for our composite ID test classes
                // MultiPKClass table with composite primary key (matches ECore model)
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS MULTIPKCLASS (
                        id VARCHAR(255) NOT NULL,
                        timestamp TIMESTAMP NOT NULL,
                        name VARCHAR(255),
                        PRIMARY KEY (id, timestamp)
                    )
                    """);
                
                
                // Insert sample test data
                stmt.execute("DELETE FROM MULTIPKCLASS");
                
                stmt.execute("INSERT INTO MULTIPKCLASS (id, timestamp, name) VALUES " +
                    "('key1', '2025-01-01 10:00:00', 'First test record')");
                stmt.execute("INSERT INTO MULTIPKCLASS (id, timestamp, name) VALUES " +
                    "('key2', '2025-01-01 11:00:00', 'Second test record')");
                
            }
        }
    }

    /**
     * Test EclipseLink persistence with composite IDs - MultiPKClass scenario.
     */
    @Test
    @WithFactoryConfiguration(factoryPid = DATASOURCE_PID, name = "composite-multi", location = "?", properties = {
        @Property(key="name", value = "h2composite"),
        @Property(key = "identifier", value = "./data/composite/multi_test")
    })
    @WithFactoryConfiguration(factoryPid = ORMConstants.ORM_MAPPING_SERVICE_PID, name = "composite-multi", properties = {
        @Property(key = ORMConstants.PROPERTY_PREFIX + "eClasses", type = Type.Array, source = ValueSource.Value, value = {"MultiPKClass"}),
        @Property(key = ORMConstants.PROPERTY_PREFIX + "mappingName", value = "composite-multi"),
        @Property(key = ORMConstants.PROPERTY_PREFIX + "model.target", value = "(emf.name=fennec.persistence.model)"),
        @Property(key = ORMConstants.PROPERTY_PREFIX + "strict", value = "false")
    })
    @WithFactoryConfiguration(factoryPid = "fennec.jpa.EMPersistenceUnit", name = "composite-multi", properties = {
    		@Property(key = "fennec.jpa.dataSource.target", value = "(name=h2composite)"),
        @Property(key = "fennec.jpa.mapping.target", value = "(fennec.jpa.eorm.mapping=composite-multi)"),
        @Property(key = "fennec.jpa.persistenceUnitName", value = "composite-multi"),
        @Property(key = "fennec.jpa.ext.eclipselink.target-database", value = "org.eclipse.persistence.platform.database.H2Platform"),
        @Property(key = "fennec.jpa.ext.eclipselink.ddl-generation", value = "create-or-extend-tables"),
        @Property(key = "fennec.jpa.ext.eclipselink.logging.level.sql", value = "FINE"),
        @Property(key = "fennec.jpa.ext.eclipselink.logging.parameters", value = "true")
    })
    public void testMultiPKClassEclipseLinkIntegration(
            @InjectService(timeout = 5000) ServiceAware<DataSource> dataSourceAware,
            @InjectService(filter = "(emf.name=fennec.persistence.model)", timeout = 5000) ServiceAware<EPackage> modelPackageAware,
            @InjectService(timeout = 10000) ServiceAware<EntityManagerFactory> emfAware) throws Exception {

        assertFalse(dataSourceAware.isEmpty(), "DataSource should be available");
        setupDatabase(dataSourceAware);
        assertFalse(modelPackageAware.isEmpty(), "Model package should be available");
        assertFalse(emfAware.isEmpty(), "EntityManagerFactory should be available");

        EPackage modelPackage = modelPackageAware.getService();
        EntityManagerFactory emf = emfAware.getService();

        // Get MultiPKClass from model
        EClass multiPKClass = (EClass) modelPackage.getEClassifier(MULTI_PK_CLASS);
        assertNotNull(multiPKClass, "MultiPKClass should exist");

        // Get EclipseLink descriptors
        Server server = JpaHelper.getServerSession(emf);
        ClassDescriptor descriptor = server.getDescriptorForAlias(multiPKClass.getName());
        assertNotNull(descriptor, "MultiPKClass descriptor should exist");

        System.out.println("\n=== Testing MultiPKClass Composite ID Persistence ===");
        System.out.printf("Primary key fields: %s\n", descriptor.getPrimaryKeyFields());

        // Test reading existing records with composite keys
        try (EntityManager em = emf.createEntityManager()) {
            // Test finding by composite key - this will verify our EclipseLink integration
            // Note: We can't easily construct the composite key object here, so we'll
            // use a query to find records and verify the composite key handling
            
            var query = em.createQuery("SELECT m FROM " + multiPKClass.getName() + " m");
            List<?> results = query.getResultList();
            
            System.out.printf("Found %d MultiPKClass records\n", results.size());
            assertTrue(results.size() >= 2, "Should find at least 2 test records");
            
            // Verify each result is properly loaded with composite key
            for (Object result : results) {
                assertNotNull(result, "Record should not be null");
                assertTrue(result instanceof EObject, "Should be EObject instance");
                
                EObject record = (EObject) result;
                assertEquals(multiPKClass, record.eClass(), "Should be MultiPKClass instance");
                
                // Verify composite key attributes are properly loaded
                EStructuralFeature idFeature = multiPKClass.getEStructuralFeature("id");
                EStructuralFeature timestampFeature = multiPKClass.getEStructuralFeature("timestamp");
                
                Object id = record.eGet(idFeature);
                Object timestamp = record.eGet(timestampFeature);
                
                assertNotNull(id, "ID component should not be null");
                assertNotNull(timestamp, "Timestamp component should not be null");
                
                System.out.printf("  Record: id=%s, timestamp=%s\n", id, timestamp);
            }
        }
    }

}