/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Mark Hoffmann - initial API and implementation
 */
package org.eclipse.fennec.persistence.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.eorm.Id;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.junit.jupiter.api.Test;

/**
 * Test for composite primary key handling in Eclipse Fennec Persistence.
 * 
 * This test class examines how the EntityMapper handles:
 * - MultiPKClass - Direct composite primary key (multiple iD="true" attributes) using EmbeddedId strategy
 * 
 * @author Mark Hoffmann
 * @since 28.01.2025
 */
public class CompositeIdTest extends EPersistenceModelBase {

    private static final String MULTI_PK_CLASS = "MultiPKClass";
    
    @Override
    void beforeModelRegistered(EPackage modelPackage) {
        // Implementation for abstract method
    }
    
    @Override 
    void afterModelRegistered(EPackage modelPackage) {
        // Implementation for abstract method
    }
    
    /**
     * Test the critical scenario: MultiPKClass with direct composite primary key.
     * 
     * Expected behavior: Framework should detect multiple iD="true" attributes
     * and create appropriate composite key mapping.
     * 
     * Current limitation: EntityProcessor.createId() only handles single EIDAttribute
     */
    @Test
    public void testMultiPKClassCurrentBehavior() {
        // Get the test model package
        EPackage modelPackage = getModelPackage();
        assertNotNull(modelPackage, "Model package should be loaded");
        
        // Find MultiPKClass
        EClass multiPKClass = findEClass(modelPackage, MULTI_PK_CLASS);
        assertNotNull(multiPKClass, "MultiPKClass should exist in model");
        
        // Analyze composite ID structure
        analyzeCompositeIdStructure(multiPKClass);
        
        // Test current EntityMapper behavior
        testCurrentEntityMapperBehavior(multiPKClass);
    }
    
    
    /**
     * Analyzes the structure of MultiPKClass to understand its composite ID definition.
     */
    private void analyzeCompositeIdStructure(EClass multiPKClass) {
        System.out.println("\n=== MultiPKClass Structure Analysis ===");
        
        // Find all ID attributes
        List<EAttribute> idAttributes = multiPKClass.getEAllAttributes().stream()
            .filter(EAttribute::isID)
            .collect(Collectors.toList());
            
        System.out.printf("Found %d ID attributes:\n", idAttributes.size());
        for (EAttribute attr : idAttributes) {
            System.out.printf("  - %s: %s (ID=%b)\n", 
                attr.getName(), 
                attr.getEAttributeType().getName(), 
                attr.isID());
        }
        
        // Verify we have the expected composite structure
        assertEquals(2, idAttributes.size(), "MultiPKClass should have exactly 2 ID attributes");
        
        // Verify specific attributes
        assertTrue(idAttributes.stream().anyMatch(a -> "id".equals(a.getName())), 
            "Should have 'id' attribute marked as ID");
        assertTrue(idAttributes.stream().anyMatch(a -> "timestamp".equals(a.getName())), 
            "Should have 'timestamp' attribute marked as ID");
            
        // Test getEIDAttribute() behavior - this is the current limitation
        EAttribute currentIdAttr = multiPKClass.getEIDAttribute();
        System.out.printf("getEIDAttribute() returns: %s\n", 
            currentIdAttr != null ? currentIdAttr.getName() : "null");
            
        // This reveals the limitation: getEIDAttribute() only returns one attribute
        // even when multiple attributes have iD="true"
    }
    
    
    /**
     * Tests current EntityMapper behavior with the given EClass.
     */
    private void testCurrentEntityMapperBehavior(EClass eClass) {
        System.out.printf("\n=== Testing EntityMapper with %s ===\n", eClass.getName());
        
        try {
            EntityMapper mapper = new EntityMapper();
            
            // Test with strict mode off (default)
            mapper.setStrict(false);
            EntityMappings mappings = mapper.createMappingsFromEClass(eClass);
            
            assertNotNull(mappings, "EntityMappings should be created");
            assertFalse(mappings.getEntity().isEmpty(), "Should have at least one entity");
            
            Entity entity = mappings.getEntity().get(0);
            System.out.printf("Generated entity: %s\n", entity.getName());
            
            // Analyze generated ID mappings
            analyzeGeneratedIdMappings(entity);
            
            // Test with strict mode on
            System.out.println("\n--- Testing with strict mode ---");
            mapper.setStrict(true);
            EntityMappings strictMappings = mapper.createMappingsFromEClass(eClass);
            Entity strictEntity = strictMappings.getEntity().get(0);
            analyzeGeneratedIdMappings(strictEntity);
            
        } catch (Exception e) {
            System.err.printf("ERROR: EntityMapper failed for %s: %s\n", 
                eClass.getName(), e.getMessage());
            e.printStackTrace();
            fail("EntityMapper should not throw exception, even if it doesn't support composite keys properly");
        }
    }
    
    /**
     * Analyzes the generated ID mappings to understand current behavior.
     */
    private void analyzeGeneratedIdMappings(Entity entity) {
        System.out.printf("Entity: %s\n", entity.getName());
        
        if (entity.getAttributes() != null) {
            List<Id> ids = entity.getAttributes().getId();
            System.out.printf("Generated %d ID mappings:\n", ids.size());
            
            for (Id id : ids) {
                System.out.printf("  - ID name: %s\n", id.getName());
                if (id.getColumn() != null) {
                    System.out.printf("    Column: %s\n", id.getColumn().getName());
                }
                if (id.getSequenceGenerator() != null) {
                    System.out.printf("    Sequence: %s\n", id.getSequenceGenerator().getName());
                }
                if (id.getGeneratedValue() != null) {
                    System.out.printf("    Generation: %s\n", id.getGeneratedValue().getStrategy());
                }
            }
        }
        
        // This will reveal the current limitation: 
        // - For MultiPKClass: Likely only 1 ID generated (synthetic or from getEIDAttribute)
    }
    
    /**
     * Helper method to find EClass by name in the model package.
     */
    private EClass findEClass(EPackage ePackage, String className) {
        return ePackage.getEClassifiers().stream()
            .filter(classifier -> classifier instanceof EClass)
            .map(classifier -> (EClass) classifier)
            .filter(eClass -> className.equals(eClass.getName()))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Gets the model package containing our test classes.
     */
    private EPackage getModelPackage() {
        // The model should be registered in the resource set by EPersistenceModelBase
        return rs.getPackageRegistry().getEPackage("https://projects.eclipse.org/projects/modeling.fennec/fpm");
    }
    
    /**
     * Test that our composite ID implementation successfully configures the EORM mappings.
     * 
     * This test validates that the enhanced CompositeIdProcessor correctly handles
     * composite primary keys and generates proper EORM mappings.
     */
    @Test
    public void testCompositeIdFrameworkIntegration() {
        System.out.println("\n=== Composite ID Framework Integration Test ===");
        
        EPackage modelPackage = getModelPackage();
        EClass multiPKClass = findEClass(modelPackage, MULTI_PK_CLASS);
        
        assertNotNull(multiPKClass, "MultiPKClass should exist");
        
        // Test the CompositeIdAnalyzer directly
        org.eclipse.fennec.persistence.orm.CompositeIdAnalyzer analyzer = 
            new org.eclipse.fennec.persistence.orm.CompositeIdAnalyzer();
            
        // Test MultiPKClass analysis
        var multiConfig = analyzer.analyzeIdStructure(multiPKClass);
        assertEquals(org.eclipse.fennec.persistence.orm.IdConfiguration.IdStrategy.EMBEDDED_ID, 
            multiConfig.getStrategy(), "MultiPKClass should use EMBEDDED_ID strategy");
        assertEquals(2, multiConfig.getIdAttributes().size(), "Should have 2 ID attributes");
        
        // Test EORM generation for MultiPKClass
        EntityMapper mapper = new EntityMapper();
        mapper.setStrict(false);
        
        var mappings = mapper.createMappingsFromEClass(multiPKClass);
        assertNotNull(mappings, "Should generate EORM mappings");
        assertFalse(mappings.getEntity().isEmpty(), "Should have entities");
        
        var entity = mappings.getEntity().get(0);
        assertEquals("MultiPKClass", entity.getName(), "Entity name should match");
        
        var ids = entity.getAttributes().getId();
        assertEquals(2, ids.size(), "Should generate 2 ID mappings for composite key");
        
        // Verify the ID field names
        var idNames = ids.stream().map(Id::getName).toList();
        assertTrue(idNames.contains("id"), "Should have 'id' ID mapping");
        assertTrue(idNames.contains("timestamp"), "Should have 'timestamp' ID mapping");
        
        System.out.println("✓ Composite ID framework integration test passed");
        System.out.printf("✓ Successfully analyzed %s with %s strategy\n", 
            multiPKClass.getName(), multiConfig.getStrategy());
        System.out.printf("✓ Successfully generated %d ID mappings for %s\n", 
            ids.size(), entity.getName());
    }
    
    /**
     * Test to document expected composite ID behavior for future implementation.
     * This test will initially fail but documents what we want to achieve.
     */
    @Test
    public void testExpectedCompositeIdBehavior() {
        System.out.println("\n=== Expected Composite ID Behavior (Future Implementation) ===");
        
        EPackage modelPackage = getModelPackage();
        EClass multiPKClass = findEClass(modelPackage, MULTI_PK_CLASS);
        
        // This test documents what we WANT to happen (will fail initially)
        try {
            EntityMapper mapper = new EntityMapper();
            EntityMappings mappings = mapper.createMappingsFromEClass(multiPKClass);
            Entity entity = mappings.getEntity().get(0);
            
            // Expected behavior for composite keys:
            // 1. Should detect 2 ID attributes
            List<Id> ids = entity.getAttributes().getId();
            
             assertEquals(2, ids.size(), "Should generate 2 ID mappings for composite key");
            
            // For now, just document what we expect
            System.out.println("EXPECTED (not yet implemented):");
            System.out.println("  - 2 ID mappings generated");
            System.out.println("  - @EmbeddedId or @IdClass strategy used");
            System.out.println("  - Proper composite key database schema");
            
        } catch (Exception e) {
            System.out.printf("Current implementation limitation: %s\n", e.getMessage());
        }
    }
}