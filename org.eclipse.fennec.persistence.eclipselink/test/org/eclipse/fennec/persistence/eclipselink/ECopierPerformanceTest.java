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

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Comprehensive performance test for ECopier with scaling object counts and different relationship types.
 * 
 * <p>This test class measures ECopier performance across different scenarios:</p>
 * <ul>
 * <li><strong>Simple copying</strong> - Objects with attributes only</li>
 * <li><strong>Containment copying</strong> - Objects with containment relationships</li>
 * <li><strong>Bidirectional copying</strong> - Objects with non-containment bidirectional relationships</li>
 * <li><strong>Collection copying</strong> - Multiple objects with cross-references using ECopier.copyCollection()</li>
 * </ul>
 * 
 * <p>Each scenario is tested with different object counts: 10, 100, 1000, 10000 objects.</p>
 * 
 * @author Data In Motion
 * @since 02.10.2025
 */
public class ECopierPerformanceTest {

    private EPackage testPackage;
    private EClass personClass;
    private EAttribute personNameAttribute;
    private EReference personFriendsReference;

    @BeforeEach
    void setUp() {
        testPackage = ECopierTestHelper.createTestModel();
        personClass = ECopierTestHelper.getPersonClass(testPackage);
        personNameAttribute = ECopierTestHelper.getPersonNameAttribute(testPackage);
        personFriendsReference = ECopierTestHelper.getPersonFriendsReference(testPackage);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 100, 1000, 10000, 100000})
    @DisplayName("Simple copying performance")
    void testSimpleCopyingPerformance(int count) {
        System.out.println("=== Simple Copying with " + count + " objects ===");
        testSimpleCopying(count);
    }

    @ParameterizedTest  
    @ValueSource(ints = {10, 100, 1000, 10000, 100000})
    @DisplayName("Containment copying performance")
    void testContainmentCopyingPerformance(int count) {
        System.out.println("=== Containment Copying with " + count + " objects ===");
        testContainmentCopying(count);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 100, 1000, 10000, 100000})
    @DisplayName("Bidirectional copying performance")
    void testBidirectionalCopyingPerformance(int count) {
        System.out.println("=== Bidirectional Copying with " + count + " objects ===");
        testBidirectionalCopying(count);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 100, 1000, 10000, 100000})
    @DisplayName("Collection copying performance")
    void testCollectionCopyingPerformance(int count) {
        System.out.println("=== Collection Copying with " + count + " objects ===");
        testCollectionCopying(count);
    }
    
    private void testSimpleCopying(int count) {
        // Create objects with attributes only (no relationships)
        List<EObject> sources = new ArrayList<>(count);
        
        for (int i = 0; i < count; i++) {
            EObject person = EcoreUtil.create(personClass);
            EcoreUtil.setID(person, "person-" + i);
            person.eSet(personNameAttribute, "Person " + i);
            sources.add(person);
        }
        
        // Measure individual copying
        long startTime = System.nanoTime();
        for (EObject source : sources) {
            EObject target = EcoreUtil.create(personClass);
            EObject result = ECopier.copyInto(source, target);
            assertNotNull(result); // Ensure copying worked
        }
        long endTime = System.nanoTime();
        
        long durationMs = (endTime - startTime) / 1_000_000;
        double avgPerObject = (double) durationMs / count;
        System.out.printf("  Simple copying: %d ms total, %.3f ms/object%n", durationMs, avgPerObject);
    }
    
    private void testContainmentCopying(int count) {
        // Create objects with containment relationships (Person -> Address)
        List<EObject> sources = new ArrayList<>(count);
        
        for (int i = 0; i < count; i++) {
            EObject person = ECopierTestHelper.createPersonWithAddress(
                testPackage, 
                "person-containment-" + i, 
                "Person with Address " + i, 
                "Street " + i
            );
            sources.add(person);
        }
        
        // Measure copying with containment
        long startTime = System.nanoTime();
        for (EObject source : sources) {
            EObject target = EcoreUtil.create(personClass);
            ECopier copier = new ECopier(target, null);
            copier.setCopyContainments(true);
            EObject result = copier.copy(source);
            copier.copyReferences();
            assertNotNull(result); // Ensure copying worked
        }
        long endTime = System.nanoTime();
        
        long durationMs = (endTime - startTime) / 1_000_000;
        double avgPerObject = (double) durationMs / count;
        System.out.printf("  Containment copying: %d ms total, %.3f ms/object%n", durationMs, avgPerObject);
    }
    
    private void testBidirectionalCopying(int count) {
        // Create objects with bidirectional non-containment relationships
        // Each person is friends with the next person (chain of friendships)
        List<EObject> sources = new ArrayList<>(count);
        
        for (int i = 0; i < count; i++) {
            EObject person = EcoreUtil.create(personClass);
            EcoreUtil.setID(person, "person-bidi-" + i);
            person.eSet(personNameAttribute, "Friend " + i);
            sources.add(person);
        }
        
        // Create friendship chain: 0->1, 1->2, 2->3, etc. (bidirectional)
        for (int i = 0; i < count - 1; i++) {
            EObject person = sources.get(i);
            EObject nextPerson = sources.get(i + 1);
            
            @SuppressWarnings("unchecked")
            List<EObject> friends = (List<EObject>) person.eGet(personFriendsReference);
            friends.add(nextPerson);
        }
        
        // Measure copying with bidirectional references
        long startTime = System.nanoTime();
        for (EObject source : sources) {
            EObject target = EcoreUtil.create(personClass);
            ECopier copier = new ECopier(target, null);
            EObject result = copier.copy(source);
            copier.copyReferences();
            assertNotNull(result); // Ensure copying worked
        }
        long endTime = System.nanoTime();
        
        long durationMs = (endTime - startTime) / 1_000_000;
        double avgPerObject = (double) durationMs / count;
        System.out.printf("  Bidirectional copying: %d ms total, %.3f ms/object%n", durationMs, avgPerObject);
    }
    
    private void testCollectionCopying(int count) {
        // Create a fixed-size collection (100 objects) to avoid quadratic growth
        // This tests the ECopier.copyCollection() method performance with consistent complexity
        List<EObject> sources = new ArrayList<>(count);
        
        for (int i = 0; i < count; i++) {
            EObject person = ECopierTestHelper.createPersonWithAddress(
                testPackage,
                "person-collection-" + i,
                "Collection Person " + i,
                "Collection Street " + i
            );
            sources.add(person);
        }
        
        // Create a fixed number of cross-references (every 10th person is friends with the next)
        for (int i = 0; i < count - 1; i += 10) {
            EObject person = sources.get(i);
            EObject nextPerson = sources.get(i + 1);
            
            @SuppressWarnings("unchecked")
            List<EObject> friends = (List<EObject>) person.eGet(personFriendsReference);
            friends.add(nextPerson);
        }
        
        // Measure collection copying performance
        Function<EObject, EObject> factoryFunction = source -> EcoreUtil.create(source.eClass());
        
        long startTime = System.nanoTime();
        Map<EObject, EObject> mappings = ECopier.copyCollection(sources, factoryFunction);
        long endTime = System.nanoTime();
        
        assertNotNull(mappings); // Ensure copying worked
        
        long durationMs = (endTime - startTime) / 1_000_000;
        double avgPerObject = (double) durationMs / count;
        System.out.printf("  Collection copying: %d ms total, %.3f ms/object, %d objects copied%n", 
                         durationMs, avgPerObject, mappings.size());
    }
}