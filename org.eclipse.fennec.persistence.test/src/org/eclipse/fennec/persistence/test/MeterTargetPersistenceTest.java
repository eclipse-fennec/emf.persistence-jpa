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
//package org.eclipse.fennec.persistence.test;
//
//import static org.eclipse.emf.ecore.util.EcoreUtil.copy;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.junit.jupiter.api.Assertions.fail;
//
//import java.io.IOException;
//import java.net.URL;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.UUID;
//
//import javax.sql.DataSource;
//
//import org.eclipse.emf.common.util.URI;
//import org.eclipse.emf.ecore.EClass;
//import org.eclipse.emf.ecore.EObject;
//import org.eclipse.emf.ecore.EPackage;
//import org.eclipse.emf.ecore.EStructuralFeature;
//import org.eclipse.emf.ecore.resource.Resource;
//import org.eclipse.emf.ecore.util.EcoreUtil;
//import org.eclipse.fennec.persistence.orm.ORMConstants;
//import org.junit.jupiter.api.Test;
//import org.osgi.test.common.annotation.InjectService;
//import org.osgi.test.common.annotation.Property;
//import org.osgi.test.common.annotation.Property.Type;
//import org.osgi.test.common.annotation.Property.ValueSource;
//import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
//import org.osgi.test.common.service.ServiceAware;
//
///**
// * End-to-end test for type converters using EORMMappingProvider to automatically
// * generate mappings for TypeConverterTestEntity. This test verifies the complete
// * pipeline: EObject → Database → EObject with all supported type conversions.
// * 
// * @author Mark Hoffmann
// * @since 14.01.2025
// */
//public class MeterTargetPersistenceTest extends EPersistenceModelBase {
//
//    private static final String DATASOURCE_PID = "org.eclipse.daanse.jdbc.datasource.metatype.h2.DataSource";
//
//    @Override
//    void beforeModelRegistered(EPackage modelPackage) {
//        // Setup can go here if needed
//    }
//
//    @Override
//    void afterModelRegistered(EPackage modelPackage) {
//        // Additional setup after model registration
//    }
//    
//    /* 
//     * (non-Javadoc)
//     * @see org.eclipse.fennec.persistence.test.EPersistenceModelBase#getModelEntryPath()
//     */
//    @Override
//    protected String getModelEntryPath() {
//    	return "/data/meter/meter-target.ecore";
//    }
//
//    @WithFactoryConfiguration(factoryPid = DATASOURCE_PID, name = "metertarget", location = "?", properties = {
//            @Property(key = "name", value = "h2metertarget"),
//            @Property(key = "identifier", value = "./data/meter/target_test")
//    })
//    @WithFactoryConfiguration(factoryPid = ORMConstants.ORM_MAPPING_SERVICE_PID, name = "metertarget-test", properties = {
//            @Property(key = ORMConstants.PROPERTY_PREFIX + "eClasses", type = Type.Array, source = ValueSource.Value, value = {"Plant", "Parts", "Net", "OperatingData", "MeteringPoint", "MeterReading", "MeterHistory", "Meter"}),
//            @Property(key = ORMConstants.PROPERTY_PREFIX + "mappingName", value = "meterTarget"),
//            @Property(key = ORMConstants.PROPERTY_PREFIX + "model.target", value = "(emf.name=target)"),
//            @Property(key = ORMConstants.PROPERTY_PREFIX + "strict", value = "false")
//    })
//    @WithFactoryConfiguration(factoryPid = "fennec.jpa.EMPersistenceUnit", name = "metertarget-test", properties = {
//            @Property(key = "fennec.jpa.dataSource.target", value = "(name=h2metertarget)"),
//            @Property(key = "fennec.jpa.mapping.target", value = "(fennec.jpa.eorm.mapping=meterTarget)"),
//            @Property(key = "fennec.jpa.persistenceUnitName", value = "meterTarget"),
//            @Property(key = "fennec.jpa.ext.eclipselink.target-database", value = "org.eclipse.persistence.platform.database.H2Platform"),
//            @Property(key = "fennec.jpa.ext.eclipselink.ddl-generation", value = "create-or-extend-tables"),
//            @Property(key = "fennec.jpa.ext.eclipselink.logging.level.sql", value = "FINE"),
//            @Property(key = "fennec.jpa.ext.eclipselink.logging.parameters", value = "true")
//    })
//    @WithFactoryConfiguration(factoryPid = "fennec.jpa.JPARepository", name = "metertarget-test", properties = {
//			@Property(key = "entityManager.target", value = "(osgi.unit.name=meterTarget)"),
//			@Property(key = "sharedEM", value = "true"),
//			@Property(key = "repo_id", value = "test"),
//			@Property(key = "base_uri", value = "test")
//	})
//    @Test
//    public void testPersistenceMeterTarget(
//    		@InjectService(timeout = 7000, filter ="(repo_id=test)") ServiceAware<EMFRepository> repoAware,
//    		@InjectService(timeout = 7000, filter = "(name=h2metertarget)") ServiceAware<DataSource> dataSourceAware)
//            throws Exception {
//
//        // Verify services are available
//        assertNotNull(repoAware.getService(), "JPA Repository should be available");
//        assertNotNull(dataSourceAware.getService(), "DataSource should be available");
//        
//        // Clean tables before test to ensure clean state
//        cleanMeterTargetTables(dataSourceAware.getService());
//
//        URL xmi = bctx.getBundle().getEntry("data/meter/Plant.xmi");
//		Resource resource = rs.createResource(URI.createURI(UUID.randomUUID().toString() + ".xmi"));
//		assertNotNull(resource);
//		try {
//			resource.load(xmi.openStream(), null);
//			assertFalse(resource.getContents().isEmpty());
//			
//		} catch (IOException e) {
//			fail("Cannot load plant xmi", e);
//		}
//		EObject plant = resource.getContents().get(0);
//		assertNotNull(plant);
//		
//        // Get the model package and test entity class
//        EPackage targetPackage = rs.getPackageRegistry().getEPackage("https://civitas.org/meter/target/1.0.0");
//        assertNotNull(targetPackage, "Model package should be available");
//        
//        EClass plantClass = (EClass) targetPackage.getEClassifier("Plant");
//        assertNotNull(plantClass, "Plant should be available in model");
//        
//     // Persist to database
//        EMFRepository repo = repoAware.getService();
//
//		assertEquals(8, plant.eContents().size());
//		List<EObject> content = new ArrayList<>(plant.eContents());
//		content.add(plant);
//		repo.save(plant, Collections.emptyMap());
//		
//		// Verify persistence with SQL queries
//		verifyPersistenceWithSQL(dataSourceAware.getService());
//		
//		// Test updating by loading the entity from the database first, then modifying it
//		EObject loadedPlant = repo.getEObject(plantClass, "42");
//		assertNotNull(loadedPlant, "Should be able to load plant with ID 42");
//		
//		EObject copy2 = copy(loadedPlant);
//		// Verify loaded plant has correct city initially
//		EStructuralFeature cityFeature = plantClass.getEStructuralFeature("city");
//		EStructuralFeature odFeature = plantClass.getEStructuralFeature("operatingData");
//		assertEquals("Jena", copy2.eGet(cityFeature), "Loaded plant should have city Jena");
//		List<EObject> ods = (List<EObject>) copy2.eGet(odFeature);
//		assertEquals(3, ods.size());
//		assertNotNull(ods.remove(2));
//		
//		// Add a new operating data to test adding new containment elements
//		EClass operatingDataClass = (EClass) targetPackage.getEClassifier("OperatingData");
//		EObject newOd = targetPackage.getEFactoryInstance().create(operatingDataClass);
//		EcoreUtil.setID(newOd, "1003");
//		newOd.eSet(operatingDataClass.getEStructuralFeature("operatingHours"), 15.5);
//		newOd.eSet(operatingDataClass.getEStructuralFeature("starts"), 7);
//		ods.add(newOd);
//		
//		// Update the city and save - this should now work with merge()
//		copy2.eSet(cityFeature, "Berlin");
//		repo.save(copy2);
//		
//		// Verify the update was persisted
//		EObject updatedPlant = repo.getEObject(plantClass, "42");
//		assertEquals("Berlin", updatedPlant.eGet(cityFeature), "Updated plant should have city Berlin");
//		List<EObject> ods2 = (List<EObject>) updatedPlant.eGet(odFeature);
//		assertEquals(3, ods2.size(), "Should have 3 operating data records (2 existing + 1 new)");
//		
//		// Verify the new operating data was added (EclipseLink generates new ID)
//		EObject newOdFromDb = ods2.stream()
//			.filter(od -> {
//				String id = EcoreUtil.getID(od);
//				return !"100".equals(id) && !"101".equals(id) && !"102".equals(id); // Not one of the original IDs
//			})
//			.findFirst()
//			.orElse(null);
//		assertNotNull(newOdFromDb, "New operating data should exist");
//		assertEquals(15.5, (double)newOdFromDb.eGet(operatingDataClass.getEStructuralFeature("operatingHours")), 0.001);
//		assertEquals(7, newOdFromDb.eGet(operatingDataClass.getEStructuralFeature("starts")));
//		
//		System.out.println("Successfully demonstrated INSERT, UPDATE and ADD containment operations!");
//		
//    }
//    
//    /**
//     * Verifies that the Plant.xmi data was correctly persisted to the database by executing
//     * SQL queries and checking the expected counts and values.
//     */
//    private void verifyPersistenceWithSQL(DataSource dataSource) throws Exception {
//        try (Connection conn = dataSource.getConnection()) {
//            
//            // Verify Plant table - should have 1 record with id="42"
//            try (PreparedStatement stmt = conn.prepareStatement("SELECT id, number, city, postalcode, street, houseNumber FROM PLANT WHERE id = ?")) {
//                stmt.setString(1, "42");
//                try (ResultSet rs = stmt.executeQuery()) {
//                    assertTrue(rs.next(), "Plant with id='42' should exist");
//                    assertEquals("24", rs.getString("number"));
//                    assertEquals("Jena", rs.getString("city"));
//                    assertEquals("07745", rs.getString("postalcode"));
//                    assertEquals("Kahlaische Straße", rs.getString("street"));
//                    assertEquals("4", rs.getString("houseNumber"));
//                    assertFalse(rs.next(), "Should only have one plant record");
//                }
//            }
//            
//            // Verify OperatingData table - should have 3 records (ids: 100, 101, 102)
//            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM OPERATINGDATA WHERE plant_id = ?")) {
//                stmt.setString(1, "42");
//                try (ResultSet rs = stmt.executeQuery()) {
//                    assertTrue(rs.next());
//                    assertEquals(3, rs.getInt(1), "Should have 3 operating data records");
//                }
//            }
//            
//            // Verify specific OperatingData values
//            try (PreparedStatement stmt = conn.prepareStatement("SELECT id, operatingHours, starts FROM OPERATINGDATA WHERE plant_id = ? ORDER BY id")) {
//                stmt.setString(1, "42");
//                try (ResultSet rs = stmt.executeQuery()) {
//                    // First record (id=100)
//                    assertTrue(rs.next());
//                    assertEquals("100", rs.getString("id"));
//                    assertEquals(12.0, rs.getDouble("operatingHours"), 0.001);
//                    assertEquals(5, rs.getInt("starts"));
//                    
//                    // Second record (id=101)
//                    assertTrue(rs.next());
//                    assertEquals("101", rs.getString("id"));
//                    assertEquals(23.0, rs.getDouble("operatingHours"), 0.001);
//                    assertEquals(4, rs.getInt("starts"));
//                    
//                    // Third record (id=102)
//                    assertTrue(rs.next());
//                    assertEquals("102", rs.getString("id"));
//                    assertEquals(21.0, rs.getDouble("operatingHours"), 0.001);
//                    assertEquals(2, rs.getInt("starts"));
//                    
//                    assertFalse(rs.next(), "Should only have 3 operating data records");
//                }
//            }
//            
//            // Verify Parts table - should have 2 records (ids: 10, 11)
//            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM PARTS WHERE plant_id = ?")) {
//                stmt.setString(1, "42");
//                try (ResultSet rs = stmt.executeQuery()) {
//                    assertTrue(rs.next());
//                    assertEquals(2, rs.getInt(1), "Should have 2 parts records");
//                }
//            }
//            
//            // Verify specific Parts values
//            try (PreparedStatement stmt = conn.prepareStatement("SELECT id, type, performance, constructionYear FROM PARTS WHERE plant_id = ? ORDER BY id")) {
//                stmt.setString(1, "42");
//                try (ResultSet rs = stmt.executeQuery()) {
//                    // First part (id=10)
//                    assertTrue(rs.next());
//                    assertEquals("10", rs.getString("id"));
//                    assertEquals(" WATER-POWER", rs.getString("type")); // Note: space is in the original data
//                    assertEquals("234", rs.getString("performance"));
//                    assertEquals(1930, rs.getInt("constructionYear"));
//                    
//                    // Second part (id=11)
//                    assertTrue(rs.next());
//                    assertEquals("11", rs.getString("id"));
//                    assertEquals("PV", rs.getString("type"));
//                    assertEquals("123MW", rs.getString("performance"));
//                    assertEquals(2020, rs.getInt("constructionYear"));
//                    
//                    assertFalse(rs.next(), "Should only have 2 parts records");
//                }
//            }
//            
//            // Verify MeteringPoint table - should have 3 records (ids: 50, 51, 52)
//            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM METERINGPOINT WHERE plant_id = ?")) {
//                stmt.setString(1, "42");
//                try (ResultSet rs = stmt.executeQuery()) {
//                    assertTrue(rs.next());
//                    assertEquals(3, rs.getInt(1), "Should have 3 metering point records");
//                }
//            }
//            
//            // Verify MeterReading counts for each metering point
//            // MeteringPoint id=50 should have 4 readings (ids: 2000, 2001, 2002, 2003)
//            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM METERREADING WHERE METERINGPOINT_ID = ?")) {
//                stmt.setString(1, "50");
//                try (ResultSet rs = stmt.executeQuery()) {
//                    assertTrue(rs.next());
//                    assertEquals(4, rs.getInt(1), "MeteringPoint 50 should have 4 readings");
//                }
//            }
//            
//            // MeteringPoint id=51 should have 3 readings (ids: 2010, 2011, 2012)
//            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM METERREADING WHERE METERINGPOINT_ID = ?")) {
//                stmt.setString(1, "51");
//                try (ResultSet rs = stmt.executeQuery()) {
//                    assertTrue(rs.next());
//                    assertEquals(3, rs.getInt(1), "MeteringPoint 51 should have 3 readings");
//                }
//            }
//            
//            // MeteringPoint id=52 should have 5 readings (ids: 2020, 2021, 2022, 2023, 2024)
//            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM METERREADING WHERE METERINGPOINT_ID = ?")) {
//                stmt.setString(1, "52");
//                try (ResultSet rs = stmt.executeQuery()) {
//                    assertTrue(rs.next());
//                    assertEquals(5, rs.getInt(1), "MeteringPoint 52 should have 5 readings");
//                }
//            }
//            
//            // Verify total MeterReading count
//            try (Statement stmt = conn.createStatement();
//                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM METERREADING")) {
//                assertTrue(rs.next());
//                assertEquals(12, rs.getInt(1), "Should have 12 total meter readings (4+3+5)");
//            }
//            
//            // Verify a specific meter reading value
//            try (PreparedStatement stmt = conn.prepareStatement("SELECT _value, timestamp FROM METERREADING WHERE id = ?")) {
//                stmt.setString(1, "2000");
//                try (ResultSet rs = stmt.executeQuery()) {
//                    assertTrue(rs.next());
//                    assertEquals(12.0, rs.getDouble("_value"), 0.001);
//                    assertNotNull(rs.getTimestamp("timestamp"));
//                }
//            }
//            
//            System.out.println("✓ All SQL verification checks passed - data persisted correctly!");
//        }
//    }
//    
//    /**
//     * Cleans all meter-target related tables to ensure clean test state.
//     * Tables are cleaned in dependency order to avoid foreign key constraint violations.
//     */
//    private void cleanMeterTargetTables(DataSource dataSource) throws Exception {
//        try (Connection conn = dataSource.getConnection();
//             Statement stmt = conn.createStatement()) {
//            
//            // Disable foreign key checks for H2 to allow truncation
//            stmt.executeUpdate("SET REFERENTIAL_INTEGRITY FALSE");
//            
//            // Clean tables (truncate is faster than delete for large datasets)
//            String[] tablesToClean = {
//                "METERREADING",     // Child of METERINGPOINT
//                "METERINGPOINT",    // Child of PLANT
//                "OPERATINGDATA",    // Child of PLANT
//                "PARTS",            // Child of PLANT
//                "METER",            // Standalone table
//                "NET",              // Standalone table
//                "PLANT"             // Root table
//            };
//            
//            for (String tableName : tablesToClean) {
//                try {
//                    stmt.executeUpdate("TRUNCATE TABLE " + tableName);
//                    System.out.println("Cleaned table: " + tableName);
//                } catch (Exception e) {
//                    // Fallback to DELETE if TRUNCATE fails
//                    try {
//                        stmt.executeUpdate("DELETE FROM " + tableName);
//                        System.out.println("Cleaned table with DELETE: " + tableName);
//                    } catch (Exception e2) {
//                        System.out.println("Table " + tableName + " could not be cleaned: " + e2.getMessage());
//                    }
//                }
//            }
//            
//            // Re-enable foreign key checks
//            stmt.executeUpdate("SET REFERENTIAL_INTEGRITY TRUE");
//        }
//    }
//
//}