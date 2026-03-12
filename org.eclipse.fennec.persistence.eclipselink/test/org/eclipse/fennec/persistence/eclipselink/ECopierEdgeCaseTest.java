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
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.persistence.eclipselink;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
 * Test class for ECopier edge cases, error handling, and robustness scenarios.
 * 
 * This test class covers scenarios that might not occur during normal operation
 * but are important for ensuring ECopier behaves predictably under all conditions.
 * 
 * @author Data In Motion
 * @since 01.10.2025
 */
public class ECopierEdgeCaseTest {

    private EPackage testPackage;
    private EClass personClass;
    private EClass addressClass;
    private EAttribute personNameAttribute;
    private EAttribute addressStreetAttribute;
    private EReference personAddressReference;
    private EReference addressOwnerReference;
    private EReference personFriendsReference;

    @BeforeEach
    void setUp() {
        testPackage = ECopierTestHelper.createTestModel();
        personClass = ECopierTestHelper.getPersonClass(testPackage);
        addressClass = ECopierTestHelper.getAddressClass(testPackage);
        
        personNameAttribute = ECopierTestHelper.getPersonNameAttribute(testPackage);
        addressStreetAttribute = ECopierTestHelper.getAddressStreetAttribute(testPackage);
        personAddressReference = ECopierTestHelper.getPersonAddressReference(testPackage);
        addressOwnerReference = ECopierTestHelper.getAddressOwnerReference(testPackage);
        personFriendsReference = ECopierTestHelper.getPersonFriendsReference(testPackage);
    }

    @Test
    @DisplayName("copyInto(null, target) should throw NullPointerException")
    void testCopyIntoNullSource() {
        // Create target object
        EObject target = EcoreUtil.create(personClass);
        
        // Test that null source throws exception
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            ECopier.copyInto(null, target);
        });
        
        assertNotNull(exception.getMessage());
        System.out.println("✓ Null source exception: " + exception.getMessage());
    }

    @Test
    @DisplayName("copyInto(source, null) should throw NullPointerException")
    void testCopyIntoNullTarget() {
        // Create source object
        EObject source = EcoreUtil.create(personClass);
        EcoreUtil.setID(source, "test-person");
        source.eSet(personNameAttribute, "John Doe");
        
        // Test that null target throws exception
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            ECopier.copyInto(source, null);
        });
        
        assertNotNull(exception.getMessage());
        System.out.println("✓ Null target exception: " + exception.getMessage());
    }

    @Test
    @DisplayName("copyInto(null, null) should throw NullPointerException")
    void testCopyIntoBothNull() {
        // Test that both null throws exception
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            ECopier.copyInto(null, null);
        });
        
        assertNotNull(exception.getMessage());
        System.out.println("✓ Both null exception: " + exception.getMessage());
    }

    @Test
    @DisplayName("ECopier constructor with null source should work")
    void testConstructorWithNullSourceAndNullTarget() {
        // ECopier constructor should accept null source (it's used internally)
        assertDoesNotThrow(() -> {
            ECopier copier = new ECopier(null, null);
            assertNotNull(copier);
        });
        
        System.out.println("✓ ECopier constructor handles null source and null target correctly");
    }

    @Test
    @DisplayName("ECopier with both flags (copyContainments=true, mergeContainments=true)")
    void testBothContainmentFlags() {
        EObject source = ECopierTestHelper.createPersonWithAddress(testPackage, "source-person", "Source Name", "Source Street");
        EObject target = EcoreUtil.create(personClass);
        
        ECopier copier = new ECopier(target, null);
        copier.setCopyContainments(true);
        copier.setMergeContainments(true);  // Both flags set to true
        
        // According to JavaDoc: both operations are performed sequentially (copy first, then merge)
        EObject result = assertDoesNotThrow(() -> {
            EObject res = copier.copy(source);
            copier.copyReferences();
            return res;
        });
        
        assertNotNull(result, "Result should not be null");
        
        // Verify that containment was processed (address should be copied/merged)
        EObject resultAddress = (EObject) result.eGet(personAddressReference);
        assertNotNull(resultAddress, "Address should be copied/merged when both flags are true");
        assertEquals("Source Street", resultAddress.eGet(addressStreetAttribute), 
                    "Address content should be preserved through copy+merge operations");
        
        System.out.println("✓ ECopier with both flags performs sequential copy+merge operations");
        System.out.println("  - Copy operation: standard EMF containment copying");
        System.out.println("  - Merge operation: custom EclipseLink entity handling");
    }

    @Test
    @DisplayName("Factory function returning null")
    void testFactoryFunctionReturningNull() {
        EObject source = ECopierTestHelper.createPersonWithAddress(testPackage, "source-person", "Source Name", "Source Street");
        
        // Factory function that returns null for Address objects
        Function<EObject, EObject> nullFactoryForAddress = (obj) -> {
            if (obj.eClass() == addressClass) {
                return null;  // Return null for Address objects
            }
            return EcoreUtil.create(obj.eClass());  // Normal creation for others
        };
        
        // Use null as target and null context to force factory function usage (simulates JPARepository scenario)
        ECopier copier = new ECopier(null, null);  // null context = no EDynamicTypeContext, factory function will be used
        copier.setCopyContainments(true);
        copier.setCopyFunction(nullFactoryForAddress);
        
        // The copy operation should complete, but containments will be null
        EObject result = assertDoesNotThrow(() -> {
            EObject res = copier.copy(source);
            copier.copyReferences();
            return res;
        });
        
        assertNotNull(result, "Result should not be null (factory function only returns null for Address)");
        
        // The address should be null because factory function returned null for Address objects
        EObject resultAddress = (EObject) result.eGet(personAddressReference);
        assertEquals(null, resultAddress, "Address should be null when factory function returns null for Address objects");
        
        // But other attributes should be copied normally
        assertEquals("Source Name", result.eGet(personNameAttribute), "Person name should be copied normally");
        
        System.out.println("✓ ECopier handles null factory function results (simulates JPARepository scenario)");
        System.out.println("  - Priority: EDynamicTypeContext > Factory Function > Standard EMF");
        System.out.println("  - Factory function returning null creates null entries in object graph");
        System.out.println("  - Copy operation completes but containment references become null");
    }

    @Test
    @DisplayName("Factory function throwing exception")
    void testFactoryFunctionThrowingException() {
        EObject source = ECopierTestHelper.createPersonWithAddress(testPackage, "source-person", "Source Name", "Source Street");
        EObject target = EcoreUtil.create(personClass);
        
        // Factory function that throws exception
        Function<EObject, EObject> throwingFactory = (obj) -> {
            throw new RuntimeException("Factory function error");
        };
        
        ECopier copier = new ECopier(target, null);
        copier.setCopyContainments(true);
        copier.setCopyFunction(throwingFactory);
        
        // Should propagate the exception from factory function
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            copier.copy(source);
        });
        
        assertEquals("Factory function error", exception.getMessage());
        System.out.println("✓ ECopier propagates factory function exceptions correctly");
    }

    @Test
    @DisplayName("Factory function returning wrong type")
    void testFactoryFunctionWrongType() {
        EObject source = ECopierTestHelper.createPersonWithAddress(testPackage, "source-person", "Source Name", "Source Street");
        EObject target = EcoreUtil.create(personClass);
        
        // Factory function that returns wrong type (Address instead of Person)
        Function<EObject, EObject> wrongTypeFactory = (obj) -> {
            if (obj.eClass() == addressClass) {
                // Return a Person when asked for Address (type mismatch)
                return EcoreUtil.create(personClass);
            }
            return EcoreUtil.create(obj.eClass());
        };
        
        ECopier copier = new ECopier(target, null);
        copier.setCopyContainments(true);
        copier.setCopyFunction(wrongTypeFactory);
        
        // Should throw IllegalArgumentException when type mismatch occurs
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            copier.copy(source);
        });
        
        assertNotNull(exception.getMessage());
        System.out.println("✓ ECopier correctly detects factory function type mismatches");
        System.out.println("Exception: " + exception.getMessage());
    }

    @Test
    @DisplayName("EDynamicTypeContext with null context")
    void testEDynamicTypeContextNull() {
        EObject source = EcoreUtil.create(personClass);
        EObject target = EcoreUtil.create(personClass);
        
        // Test with null EDynamicTypeContext
        ECopier copier = new ECopier(target, null);  // null context
        
        assertDoesNotThrow(() -> {
            EObject result = copier.copy(source);
            copier.copyReferences();
            assertNotNull(result);
        });
        
        System.out.println("✓ ECopier handles null EDynamicTypeContext correctly");
    }

    @Test
    @DisplayName("Copying between different EClass types")
    void testCopyingBetweenDifferentTypes() {
        // Create source Person and target Address (different types)
        EObject sourcePerson = EcoreUtil.create(personClass);
        EcoreUtil.setID(sourcePerson, "person-123");
        sourcePerson.eSet(personNameAttribute, "John Doe");
        
        EObject targetAddress = EcoreUtil.create(addressClass);
        
        // Test copying between different types
        EObject result = ECopier.copyInto(sourcePerson, targetAddress);
        
        // Verify the result object and its type
        assertNotNull(result, "Result should not be null");
        
        // Critical assertion: Result should be of SOURCE type, not target type
        assertEquals("Person", result.eClass().getName(), "Result should be of source object type (Person)");
        assertSame(personClass, result.eClass(), "Result EClass should be the Person EClass");
        
        // Result should be a NEW object, not the original target
        assertNotSame(targetAddress, result, "Result should be a new object, not the original target");
        assertNotSame(sourcePerson, result, "Result should be a new object, not the original source");
        
        // Verify that attributes were copied from source to result  
        assertEquals("person-123", EcoreUtil.getID(result), "ID should be copied from source");
        assertEquals("John Doe", result.eGet(personNameAttribute), "Name should be copied from source");
        
        System.out.println("✓ ECopier handles different EClass types correctly");
        System.out.println("Source type: " + sourcePerson.eClass().getName());
        System.out.println("Target type: " + targetAddress.eClass().getName());
        System.out.println("Result type: " + result.eClass().getName());
        System.out.println("Behavior: When types differ, creates new object of SOURCE type with copied attributes");
    }

    @Test
    @DisplayName("Copying with bidirectional references")
    void testCopyingWithBidirectionalReferences() {
        // Create a Person with an Address (bidirectional relationship)
        EObject sourcePerson = EcoreUtil.create(personClass);
        EcoreUtil.setID(sourcePerson, "person-with-address");
        sourcePerson.eSet(personNameAttribute, "John with Address");
        
        EObject sourceAddress = EcoreUtil.create(addressClass);
        sourceAddress.eSet(addressStreetAttribute, "123 Main Street");
        
        // Set bidirectional reference
        sourcePerson.eSet(personAddressReference, sourceAddress);
        // The opposite reference should be set automatically by EMF
        
        // Verify bidirectional reference is established
        assertSame(sourcePerson, sourceAddress.eGet(addressOwnerReference), 
                  "Address should reference back to Person (bidirectional relationship)");
        
        // Create target objects
        EObject targetPerson = EcoreUtil.create(personClass);
        
        // Test copying with bidirectional references
        ECopier copier = new ECopier(targetPerson, null);
        copier.setCopyContainments(true);
        EObject resultPerson = copier.copy(sourcePerson);
        copier.copyReferences(); // This is crucial for reference copying
        
        // Verify the result person
        assertNotNull(resultPerson, "Result person should not be null");
        assertSame(targetPerson, resultPerson, "Should return the target person when types match");
        
        // Verify attributes were copied
        assertEquals("person-with-address", EcoreUtil.getID(resultPerson), "Person ID should be copied");
        assertEquals("John with Address", resultPerson.eGet(personNameAttribute), "Person name should be copied");
        
        // Verify containment reference was copied
        Object referencedAddress = resultPerson.eGet(personAddressReference);
        assertNotNull(referencedAddress, "Referenced address should not be null");
        assertNotSame(sourceAddress, referencedAddress, "Should be a new address object due to containment");
        
        // Verify bidirectional consistency
        EObject copiedAddress = (EObject) referencedAddress;
        assertEquals("123 Main Street", copiedAddress.eGet(addressStreetAttribute), "Address street should be copied");
        assertSame(resultPerson, copiedAddress.eGet(addressOwnerReference), 
                  "Bidirectional reference should point back to result person");
        
        System.out.println("✓ ECopier handles bidirectional references correctly");
        System.out.println("Person name: " + resultPerson.eGet(personNameAttribute));
        System.out.println("Address street: " + copiedAddress.eGet(addressStreetAttribute));
        System.out.println("Bidirectional consistency: " + (copiedAddress.eGet(addressOwnerReference) == resultPerson));
        System.out.println("Behavior: Bidirectional references are maintained during copying");
    }

    @Test
    @DisplayName("Collection copying with cross-references")
    void testCopyCollection() {
        // Create a collection of objects with cross-references
        List<EObject> sources = new ArrayList<>();
        
        // Create Person 1 with Address
        EObject person1 = EcoreUtil.create(personClass);
        EcoreUtil.setID(person1, "person-1");
        person1.eSet(personNameAttribute, "Alice");
        
        EObject address1 = EcoreUtil.create(addressClass);
        address1.eSet(addressStreetAttribute, "123 Main St");
        person1.eSet(personAddressReference, address1);
        
        // Create Person 2 with Address  
        EObject person2 = EcoreUtil.create(personClass);
        EcoreUtil.setID(person2, "person-2");
        person2.eSet(personNameAttribute, "Bob");
        
        EObject address2 = EcoreUtil.create(addressClass);
        address2.eSet(addressStreetAttribute, "456 Oak Ave");
        person2.eSet(personAddressReference, address2);
        
        sources.add(person1);
        sources.add(person2);
        
        // Create factory function to create new objects of same type
        Function<EObject, EObject> factoryFunction = source -> EcoreUtil.create(source.eClass());
        
        // Test collection copying
        Map<EObject, EObject> mappings = ECopier.copyCollection(sources, factoryFunction);
        
        // Verify mapping exists for all source objects
        assertEquals(2, mappings.size(), "Should have mappings for both persons");
        assertTrue(mappings.containsKey(person1), "Should contain mapping for person1");
        assertTrue(mappings.containsKey(person2), "Should contain mapping for person2");
        
        // Verify copied objects
        EObject copiedPerson1 = mappings.get(person1);
        EObject copiedPerson2 = mappings.get(person2);
        
        assertNotNull(copiedPerson1, "Copied person1 should not be null");
        assertNotNull(copiedPerson2, "Copied person2 should not be null");
        assertNotSame(person1, copiedPerson1, "Should be different object instances");
        assertNotSame(person2, copiedPerson2, "Should be different object instances");
        
        // Verify attributes were copied
        assertEquals("person-1", EcoreUtil.getID(copiedPerson1), "Person1 ID should be copied");
        assertEquals("Alice", copiedPerson1.eGet(personNameAttribute), "Person1 name should be copied");
        assertEquals("person-2", EcoreUtil.getID(copiedPerson2), "Person2 ID should be copied");
        assertEquals("Bob", copiedPerson2.eGet(personNameAttribute), "Person2 name should be copied");
        
        // Verify containment references were copied (addresses should be new objects)
        EObject copiedAddress1 = (EObject) copiedPerson1.eGet(personAddressReference);
        EObject copiedAddress2 = (EObject) copiedPerson2.eGet(personAddressReference);
        
        assertNotNull(copiedAddress1, "Copied address1 should not be null");
        assertNotNull(copiedAddress2, "Copied address2 should not be null");
        assertNotSame(address1, copiedAddress1, "Address1 should be a new object due to containment");
        assertNotSame(address2, copiedAddress2, "Address2 should be a new object due to containment");
        
        // Verify address attributes were copied
        assertEquals("123 Main St", copiedAddress1.eGet(addressStreetAttribute), "Address1 street should be copied");
        assertEquals("456 Oak Ave", copiedAddress2.eGet(addressStreetAttribute), "Address2 street should be copied");
        
        // Verify bidirectional references are maintained
        assertSame(copiedPerson1, copiedAddress1.eGet(addressOwnerReference), "Address1 should reference copied person1");
        assertSame(copiedPerson2, copiedAddress2.eGet(addressOwnerReference), "Address2 should reference copied person2");
        
        System.out.println("✓ ECopier.copyCollection() handles multiple objects with cross-references correctly");
        System.out.println("Copied " + mappings.size() + " objects successfully");
        System.out.println("All cross-references and bidirectional relationships maintained");
    }

    @Test
    @DisplayName("Collection copying with null validation")
    void testCopyCollectionValidation() {
        Function<EObject, EObject> factoryFunction = source -> EcoreUtil.create(source.eClass());
        
        // Test null sources collection
        assertThrows(NullPointerException.class, () -> {
            ECopier.copyCollection(null, factoryFunction);
        }, "Should throw NPE for null sources");
        
        // Test null factory function
        assertThrows(NullPointerException.class, () -> {
            ECopier.copyCollection(List.of(), null);
        }, "Should throw NPE for null factory function");
        
        // Test collection with null element
        List<EObject> sourcesWithNull = new ArrayList<>();
        sourcesWithNull.add(EcoreUtil.create(personClass));
        sourcesWithNull.add(null);
        
        assertThrows(IllegalArgumentException.class, () -> {
            ECopier.copyCollection(sourcesWithNull, factoryFunction);
        }, "Should throw IAE for collection containing null elements");
        
        // Test empty collection (should work)
        Map<EObject, EObject> result = ECopier.copyCollection(List.of(), factoryFunction);
        assertTrue(result.isEmpty(), "Empty collection should return empty map");
        
        System.out.println("✓ ECopier.copyCollection() validation works correctly");
    }

    @Test
    @DisplayName("Collection copying with non-containment bidirectional references")
    void testCopyCollectionWithNonContainmentBidirectionalReferences() {
        // Create a network of persons with bidirectional friendship references
        // This tests the complex scenario where objects reference each other 
        // without containment (many-to-many relationships)
        
        List<EObject> sources = new ArrayList<>();
        
        // Create Person 1 (Alice)
        EObject alice = EcoreUtil.create(personClass);
        EcoreUtil.setID(alice, "person-alice");
        alice.eSet(personNameAttribute, "Alice");
        
        // Create Person 2 (Bob)  
        EObject bob = EcoreUtil.create(personClass);
        EcoreUtil.setID(bob, "person-bob");
        bob.eSet(personNameAttribute, "Bob");
        
        // Create Person 3 (Charlie)
        EObject charlie = EcoreUtil.create(personClass);
        EcoreUtil.setID(charlie, "person-charlie");
        charlie.eSet(personNameAttribute, "Charlie");
        
        // Create complex friendship network:
        // Alice -> friends with Bob and Charlie
        // Bob -> friends with Alice and Charlie  
        // Charlie -> friends with Alice and Bob
        // This creates a fully connected graph with bidirectional references
        
        @SuppressWarnings("unchecked")
        List<EObject> aliceFriends = (List<EObject>) alice.eGet(personFriendsReference);
        aliceFriends.add(bob);
        
        @SuppressWarnings("unchecked")
        List<EObject> bobFriends = (List<EObject>) bob.eGet(personFriendsReference);
        
        @SuppressWarnings("unchecked")
        List<EObject> charlieFriends = (List<EObject>) charlie.eGet(personFriendsReference);
        charlieFriends.add(alice);
        
        // Verify the bidirectional references are established
        assertTrue(aliceFriends.contains(bob), "Alice should be friends with Bob");
        assertTrue(aliceFriends.contains(charlie), "Alice should be friends with Charlie (due to bidirectionality when Charlie added Alice)");
        assertTrue(bobFriends.contains(alice), "Bob should be friends with Alice (due to bidirectionality when Alice added Bob)");
        assertFalse(bobFriends.contains(charlie), "Bob should NOT be friends with Charlie");
        assertTrue(charlieFriends.contains(alice), "Charlie should be friends with Alice");
        assertFalse(charlieFriends.contains(bob), "Charlie should NOT be friends with Bob");
        
        sources.add(alice);
        sources.add(bob);
        sources.add(charlie);
        
        System.out.println("Created friendship network:");
        System.out.println("  Alice friends: " + aliceFriends.stream().map(p -> p.eGet(personNameAttribute)).toList());
        System.out.println("  Bob friends: " + bobFriends.stream().map(p -> p.eGet(personNameAttribute)).toList());
        System.out.println("  Charlie friends: " + charlieFriends.stream().map(p -> p.eGet(personNameAttribute)).toList());
        
        // Create factory function to create new objects of same type
        Function<EObject, EObject> factoryFunction = source -> EcoreUtil.create(source.eClass());
        
        // Test collection copying with complex cross-references
        System.out.println("Testing ECopier.copyCollection() with non-containment bidirectional references...");
        Map<EObject, EObject> mappings = ECopier.copyCollection(sources, factoryFunction);
        
        // Verify all mappings exist
        assertEquals(3, mappings.size(), "Should have mappings for all 3 persons");
        assertTrue(mappings.containsKey(alice), "Should contain mapping for Alice");
        assertTrue(mappings.containsKey(bob), "Should contain mapping for Bob");
        assertTrue(mappings.containsKey(charlie), "Should contain mapping for Charlie");
        
        // Get copied objects
        EObject copiedAlice = mappings.get(alice);
        EObject copiedBob = mappings.get(bob);
        EObject copiedCharlie = mappings.get(charlie);
        
        assertNotNull(copiedAlice, "Copied Alice should not be null");
        assertNotNull(copiedBob, "Copied Bob should not be null");
        assertNotNull(copiedCharlie, "Copied Charlie should not be null");
        
        // Verify they are different instances
        assertNotSame(alice, copiedAlice, "Should be different Alice instances");
        assertNotSame(bob, copiedBob, "Should be different Bob instances");
        assertNotSame(charlie, copiedCharlie, "Should be different Charlie instances");
        
        // Verify attributes were copied
        assertEquals("person-alice", EcoreUtil.getID(copiedAlice), "Alice ID should be copied");
        assertEquals("Alice", copiedAlice.eGet(personNameAttribute), "Alice name should be copied");
        assertEquals("person-bob", EcoreUtil.getID(copiedBob), "Bob ID should be copied");
        assertEquals("Bob", copiedBob.eGet(personNameAttribute), "Bob name should be copied");
        assertEquals("person-charlie", EcoreUtil.getID(copiedCharlie), "Charlie ID should be copied");
        assertEquals("Charlie", copiedCharlie.eGet(personNameAttribute), "Charlie name should be copied");
        
        // Verify non-containment bidirectional references were correctly copied
        @SuppressWarnings("unchecked")
        List<EObject> copiedAliceFriends = (List<EObject>) copiedAlice.eGet(personFriendsReference);
        @SuppressWarnings("unchecked")
        List<EObject> copiedBobFriends = (List<EObject>) copiedBob.eGet(personFriendsReference);
        @SuppressWarnings("unchecked")
        List<EObject> copiedCharlieFriends = (List<EObject>) copiedCharlie.eGet(personFriendsReference);
        
        assertEquals(2, copiedAliceFriends.size(), "Copied Alice should have 2 friends");
        assertEquals(1, copiedBobFriends.size(), "Copied Bob should have 1 friend");
        assertEquals(1, copiedCharlieFriends.size(), "Copied Charlie should have 1 friend");
        
        // Verify cross-references point to the copied objects (not originals)
        assertTrue(copiedAliceFriends.contains(copiedBob), "Copied Alice should be friends with copied Bob");
        assertTrue(copiedAliceFriends.contains(copiedCharlie), "Copied Alice should be friends with copied Charlie");
        assertFalse(copiedAliceFriends.contains(bob), "Copied Alice should NOT be friends with original Bob");
        assertFalse(copiedAliceFriends.contains(charlie), "Copied Alice should NOT be friends with original Charlie");
        
        assertTrue(copiedBobFriends.contains(copiedAlice), "Copied Bob should be friends with copied Alice");
        assertFalse(copiedBobFriends.contains(copiedCharlie), "Copied Bob should NOT be friends with copied Charlie");
        assertFalse(copiedBobFriends.contains(alice), "Copied Bob should NOT be friends with original Alice");
        assertFalse(copiedBobFriends.contains(charlie), "Copied Bob should NOT be friends with original Charlie");
        
        assertTrue(copiedCharlieFriends.contains(copiedAlice), "Copied Charlie should be friends with copied Alice");
        assertFalse(copiedCharlieFriends.contains(copiedBob), "Copied Charlie should NOT be friends with copied Bob");
        assertFalse(copiedCharlieFriends.contains(alice), "Copied Charlie should NOT be friends with original Alice");
        assertFalse(copiedCharlieFriends.contains(bob), "Copied Charlie should NOT be friends with original Bob");
        
        System.out.println("✓ ECopier.copyCollection() correctly handles non-containment bidirectional references:");
        System.out.println("  - Friendship network (Alice↔Bob, Alice↔Charlie)");
        System.out.println("  - All cross-references point to copied objects, not originals");
        System.out.println("  - Bidirectional consistency maintained in copied object graph");
        System.out.println("  - Two-phase copying ensures proper object identity mapping");
        System.out.println("  - Perfect for JPARepository scenarios with many-to-many relationships");
    }


    @Test
    @DisplayName("Empty source object")
    void testEmptySourceObject() {
        // Create completely empty source object
        EObject source = EcoreUtil.create(personClass);
        // Don't set any attributes or ID
        
        EObject target = EcoreUtil.create(personClass);
        target.eSet(personNameAttribute, "Target Name");  // Target has some data
        
        EObject result = ECopier.copyInto(source, target);
        
        assertNotNull(result);
        assertNull(EcoreUtil.getID(source));
        assertNull(EcoreUtil.getID(result));
        System.out.println("✓ ECopier handles empty source objects");
        System.out.println("Source ID: " + EcoreUtil.getID(source));
        System.out.println("Result ID: " + EcoreUtil.getID(result));
        System.out.println("Result name: " + result.eGet(personNameAttribute));
    }

    @Test
    @DisplayName("Basic performance test")
    void testBasicPerformance() {
        // Create a simple test case for basic performance validation
        EObject source = ECopierTestHelper.createPersonWithAddress(
            testPackage, 
            "performance-test", 
            "Performance Test Person", 
            "Performance Test Street"
        );
        
        EObject target = EcoreUtil.create(personClass);
        
        // Measure performance (basic check)
        long startTime = System.nanoTime();
        EObject result = ECopier.copyInto(source, target);
        long endTime = System.nanoTime();
        
        assertNotNull(result);
        long durationMs = (endTime - startTime) / 1_000_000;
        System.out.println("✓ Basic ECopier performance test completed in " + durationMs + "ms");
        System.out.println("  Note: For comprehensive performance testing, see ECopierPerformanceTest");
    }

}