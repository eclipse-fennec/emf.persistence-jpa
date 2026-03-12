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
package org.eclipse.fennec.persistence.eclipselink;

import java.util.function.Function;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.eclipselink.copying.ECopier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test class demonstrating the different ECopier usage scenarios as documented
 * in ECOPIER_DOCUMENTATION.md.
 * 
 * Tests the three main use cases:
 * 1. JPARepository initial entity conversion (doCopy method)
 * 2. JPARepository update merge (save method for existing entities)
 * 3. General EMF object copying (static copyInto method)
 * 
 * @author Data In Motion
 * @since 01.10.2025
 */
public class ECopierScenarioTest {

    private EPackage testPackage;
    private EClass parentClass;
    private EClass childClass;
    private EAttribute parentNameAttribute;
    private EAttribute childValueAttribute;
    private EReference parentChildrenReference;

    @BeforeEach
    void setUp() {
        testPackage = ECopierTestHelper.createParentChildTestModel();
        parentClass = (EClass) testPackage.getEClassifier("Parent");
        childClass = (EClass) testPackage.getEClassifier("Child");
        
        parentNameAttribute = (EAttribute) parentClass.getEStructuralFeature("name");
        childValueAttribute = (EAttribute) childClass.getEStructuralFeature("value");
        
        parentChildrenReference = (EReference) parentClass.getEStructuralFeature("children");
    }

    @Test
    @DisplayName("Scenario 1: JPARepository Initial Entity Conversion (doCopy)")
    void testInitialEntityConversion() {
        System.out.println("=== Scenario 1: Initial Entity Conversion ===");
        
        // Create source DynamicEObjectImpl with containment children
        EObject sourceParent = ECopierTestHelper.createParentWithChildren(testPackage, "parent-1", "Test Parent", 
                                                        new String[]{"child-1", "child-2"}, 
                                                        new Double[]{10.5, 20.7});

        // Simulate createEclipselinkEObject() - creates target EclipseLink entity
        EObject targetParent = EcoreUtil.create(parentClass);

        // Simulate the factory function that creates EclipseLink entities for children
        Function<EObject, EObject> factoryFunction = (source) -> {
            EClass sourceClass = source.eClass();
            return EcoreUtil.create(sourceClass);
        };

        // Configure ECopier as in JPARepository.doCopy()
        ECopier copier = new ECopier(targetParent, null);
        copier.setCopyContainments(true);  // Copy containment children
        copier.setCopyFunction(factoryFunction);  // Convert children to EclipseLink entities
        
        EObject result = copier.copy(sourceParent);
        copier.copyReferences();

        // Verify the conversion
        System.out.println("Source parent ID: " + EcoreUtil.getID(sourceParent));
        System.out.println("Result parent ID: " + EcoreUtil.getID(result));
        System.out.println("Source parent name: " + sourceParent.eGet(parentNameAttribute));
        System.out.println("Result parent name: " + result.eGet(parentNameAttribute));

        @SuppressWarnings("unchecked")
        var sourceChildren = (java.util.List<EObject>) sourceParent.eGet(parentChildrenReference);
        @SuppressWarnings("unchecked")
        var resultChildren = (java.util.List<EObject>) result.eGet(parentChildrenReference);

        System.out.println("Source children count: " + sourceChildren.size());
        System.out.println("Result children count: " + resultChildren.size());

        for (int i = 0; i < Math.min(sourceChildren.size(), resultChildren.size()); i++) {
            EObject sourceChild = sourceChildren.get(i);
            EObject resultChild = resultChildren.get(i);
            System.out.println("Source child[" + i + "] ID: " + EcoreUtil.getID(sourceChild));
            System.out.println("Result child[" + i + "] ID: " + EcoreUtil.getID(resultChild));
            System.out.println("Source child[" + i + "] value: " + sourceChild.eGet(childValueAttribute));
            System.out.println("Result child[" + i + "] value: " + resultChild.eGet(childValueAttribute));
        }

        // Document what should happen vs what actually happens
        System.out.println("Expected: All IDs and attributes should be copied to EclipseLink entities");
        System.out.println("Actual: Check the output above to see if this works correctly");
    }

    @Test
    @DisplayName("Scenario 2: JPARepository Update Merge (existing entities)")
    void testUpdateMerge() {
        System.out.println("=== Scenario 2: Update Merge ===");
        
        // Create "existing" managed entity (from entityManager.find())
        EObject existingEntity = ECopierTestHelper.createParentWithChildren(testPackage, "existing-1", "Original Name", 
                                                          new String[]{"existing-child-1"}, 
                                                          new Double[]{100.0});

        // Create modified copy with changes
        EObject modifiedCopy = ECopierTestHelper.createParentWithChildren(testPackage, "existing-1", "Updated Name", 
                                                       new String[]{"existing-child-1", "new-child-2"}, 
                                                       new Double[]{150.0, 200.0});

        // Simulate factory function for creating new EclipseLink entities
        Function<EObject, EObject> factoryFunction = (source) -> {
            return EcoreUtil.create(source.eClass());
        };

        // Configure ECopier as in JPARepository.save() for existing entities
        ECopier copier = new ECopier(existingEntity, null);
        copier.setCopyContainments(false);  // Don't copy containments normally
        copier.setMergeContainments(true);  // Use special merge logic
        copier.setCopyFunction(factoryFunction);
        
        EObject result = copier.copy(modifiedCopy);
        copier.copyReferences();

        System.out.println("Existing entity name before merge: " + existingEntity.eGet(parentNameAttribute));
        System.out.println("Modified copy name: " + modifiedCopy.eGet(parentNameAttribute));
        System.out.println("Result name after merge: " + result.eGet(parentNameAttribute));

        @SuppressWarnings("unchecked")
        var existingChildren = (java.util.List<EObject>) existingEntity.eGet(parentChildrenReference);
        @SuppressWarnings("unchecked")
        var modifiedChildren = (java.util.List<EObject>) modifiedCopy.eGet(parentChildrenReference);
        @SuppressWarnings("unchecked")
        var resultChildren = (java.util.List<EObject>) result.eGet(parentChildrenReference);

        System.out.println("Existing children count: " + existingChildren.size());
        System.out.println("Modified children count: " + modifiedChildren.size());
        System.out.println("Result children count: " + resultChildren.size());

        // This tests the mergeContainments logic specifically
        System.out.println("Expected: mergeContainments should update existing + add new children");
        System.out.println("Actual: Check if new child with correct values was added");
        
        // Check if the second child (new one) has correct values
        if (resultChildren.size() > 1) {
            EObject newChild = resultChildren.get(1);
            System.out.println("New child ID: " + EcoreUtil.getID(newChild));
            System.out.println("New child value: " + newChild.eGet(childValueAttribute));
            System.out.println("Expected new child value: 200.0");
        }
    }

    @Test
    @DisplayName("Scenario 3: General EMF Object Copying (static copyInto)")
    void testGeneralEMFCopying() {
        System.out.println("=== Scenario 3: General EMF Object Copying ===");
        
        // Create source EMF object
        EObject source = EcoreUtil.create(parentClass);
        EcoreUtil.setID(source, "general-copy-source");
        source.eSet(parentNameAttribute, "Source Object");

        // Create target EMF object  
        EObject target = EcoreUtil.create(parentClass);
        EcoreUtil.setID(target, "general-copy-target");
        target.eSet(parentNameAttribute, "Target Object");

        System.out.println("Before copy:");
        System.out.println("Source ID: " + EcoreUtil.getID(source) + ", name: " + source.eGet(parentNameAttribute));
        System.out.println("Target ID: " + EcoreUtil.getID(target) + ", name: " + target.eGet(parentNameAttribute));

        // Use static copyInto method
        EObject result = ECopier.copyInto(source, target);

        System.out.println("After copy:");
        System.out.println("Result ID: " + EcoreUtil.getID(result) + ", name: " + result.eGet(parentNameAttribute));
        System.out.println("Target ID: " + EcoreUtil.getID(target) + ", name: " + target.eGet(parentNameAttribute));
        
        // Check if result is the same object as target
        System.out.println("Result is same object as target: " + (result == target));
        
        // This should demonstrate the general copying behavior
        System.out.println("Expected: Attributes copied from source to target, target object returned");
        System.out.println("Actual: Check if name was copied correctly");
    }

    @Test
    @DisplayName("Test createCopy method behavior")
    void testCreateCopyBehavior() {
        System.out.println("=== createCopy Method Behavior ===");
        
        // Test case 1: No source object set OR source.eClass != target.eClass
        EObject source1 = EcoreUtil.create(childClass);
        EObject target1 = EcoreUtil.create(parentClass);  // Different class
        
        ECopier copier1 = new ECopier(target1, null);
        EObject result1 = copier1.copy(source1);
        
        System.out.println("Test 1 - Different classes:");
        System.out.println("Source class: " + source1.eClass().getName());
        System.out.println("Target class: " + target1.eClass().getName());
        System.out.println("Result class: " + result1.eClass().getName());
        
        // Test case 2: source.eClass == target.eClass
        EObject source2 = EcoreUtil.create(parentClass);
        EObject target2 = EcoreUtil.create(parentClass);  // Same class
        
        ECopier copier2 = new ECopier(target2, null);
        EObject result2 = copier2.copy(source2);
        
        System.out.println("Test 2 - Same classes:");
        System.out.println("Source class: " + source2.eClass().getName());
        System.out.println("Target class: " + target2.eClass().getName());
        System.out.println("Result class: " + result2.eClass().getName());
        System.out.println("Result is source object: " + (result2 == source2));
        System.out.println("Result is target object: " + (result2 == target2));
    }

}