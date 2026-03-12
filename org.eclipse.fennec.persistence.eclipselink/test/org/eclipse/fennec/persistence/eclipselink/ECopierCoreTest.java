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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
 * Test class for ECopier core functionality - ID and attribute copying.
 * 
 * This test verifies that ECopier correctly handles:
 * 1. ID copying from source to target objects
 * 2. Attribute copying with correct values
 * 3. Different ECopier configuration modes
 * 4. Integration scenarios matching real-world usage
 * 
 * Uses Person domain objects with clear, intuitive attributes for better readability.
 * 
 * @author Data In Motion
 * @since 01.10.2025
 */
public class ECopierCoreTest {

    private EPackage testPackage;
    private EClass personClass;
    private EClass addressClass;
    private EAttribute nameAttribute;
    private EAttribute ageAttribute;
    private EAttribute streetAttribute;
    private EReference addressReference;

    @BeforeEach
    void setUp() {
        // Use shared test model helper for consistent testing across ECopier tests
        testPackage = ECopierTestHelper.createTestModel();
        personClass = ECopierTestHelper.getPersonClass(testPackage);
        addressClass = ECopierTestHelper.getAddressClass(testPackage);
        nameAttribute = ECopierTestHelper.getPersonNameAttribute(testPackage);
        ageAttribute = ECopierTestHelper.getPersonAgeAttribute(testPackage);
        streetAttribute = ECopierTestHelper.getAddressStreetAttribute(testPackage);
        addressReference = ECopierTestHelper.getPersonAddressReference(testPackage);
    }

    @Test
    @DisplayName("Static copyInto method correctly copies ID and attributes")
    void testStaticCopyIntoSuccess() {
        // Create source person with specific values (simulating DynamicEObjectImpl)
        EObject source = EcoreUtil.create(personClass);
        EcoreUtil.setID(source, "person-123");
        source.eSet(nameAttribute, "Alice Johnson");
        source.eSet(ageAttribute, 25);

        // Verify source has correct values
        assertEquals("person-123", EcoreUtil.getID(source));
        assertEquals("Alice Johnson", (String) source.eGet(nameAttribute));
        assertEquals(25, (Integer) source.eGet(ageAttribute));

        // Create target person (simulating EclipseLink entity)
        EObject target = EcoreUtil.create(personClass);

        // Use ECopier.copyInto() to copy data from source to target
        EObject result = ECopier.copyInto(source, target);

        // Verify the successful copying behavior
        System.out.println("=== ECopier.copyInto() Results ===");
        System.out.println("Source ID: " + EcoreUtil.getID(source));
        System.out.println("Result ID: " + EcoreUtil.getID(result));
        System.out.println("Source name: " + source.eGet(nameAttribute));
        System.out.println("Result name: " + result.eGet(nameAttribute));
        System.out.println("Source age: " + source.eGet(ageAttribute));
        System.out.println("Result age: " + result.eGet(ageAttribute));

        // Verify that ECopier correctly copies all data
        assertEquals("person-123", EcoreUtil.getID(result), "ID should be copied from source to target");
        assertEquals("Alice Johnson", (String) result.eGet(nameAttribute), 
                    "name should be copied from source");
        assertEquals(25, (Integer) result.eGet(ageAttribute), 
                    "age should be copied from source");
        
        System.out.println("✓ ECopier.copyInto() works correctly - all values copied successfully");
    }

    @Test
    @DisplayName("Direct ECopier usage with default configuration (copyContainments=false)")
    void testDirectECopierDefaultConfiguration() {
        // Create source person with address (containment reference)
        EObject source = EcoreUtil.create(personClass);
        EcoreUtil.setID(source, "person-456");
        source.eSet(nameAttribute, "Bob Smith");
        source.eSet(ageAttribute, 30);
        
        // Create address for source person
        EObject sourceAddress = EcoreUtil.create(addressClass);
        sourceAddress.eSet(streetAttribute, "456 Oak Avenue");
        source.eSet(addressReference, sourceAddress);

        // Create target person
        EObject target = EcoreUtil.create(personClass);

        // Use ECopier directly with default configuration (copyContainments=false by default)
        ECopier copier = new ECopier(target, null);
        EObject result = copier.copy(source);
        copier.copyReferences();

        System.out.println("=== Direct ECopier with Default Configuration ===");
        System.out.println("Source ID: " + EcoreUtil.getID(source));
        System.out.println("Result ID: " + EcoreUtil.getID(result));
        System.out.println("Source name: " + source.eGet(nameAttribute));
        System.out.println("Result name: " + result.eGet(nameAttribute));
        System.out.println("Source age: " + source.eGet(ageAttribute));
        System.out.println("Result age: " + result.eGet(ageAttribute));
        
        // Check containment copying behavior with default configuration
        EObject resultAddress = (EObject) result.eGet(addressReference);
        System.out.println("Source address: " + (sourceAddress != null ? sourceAddress.eGet(streetAttribute) : "null"));
        System.out.println("Result address: " + (resultAddress != null ? resultAddress.eGet(streetAttribute) : "null"));

        // Verify basic copying works with default ECopier configuration
        assertEquals("person-456", EcoreUtil.getID(result), "ID should be copied with default ECopier configuration");
        assertEquals("Bob Smith", (String) result.eGet(nameAttribute), "name should be copied");
        assertEquals(30, (Integer) result.eGet(ageAttribute), "age should be copied");
        
        // Verify default behavior: containment references are NOT copied (copyContainments=false by default)
        assertNull(resultAddress, "Address containment should NOT be copied with default configuration (copyContainments=false)");
        
        System.out.println("✓ Direct ECopier with default configuration works correctly (excludes containments)");
    }

    @Test
    @DisplayName("ECopier with copyContainments=true - containments are copied")
    void testECopierWithCopyContainments() {
        // Create source person with address (containment reference)
        EObject source = EcoreUtil.create(personClass);
        EcoreUtil.setID(source, "person-789");
        source.eSet(nameAttribute, "Carol Davis");
        source.eSet(ageAttribute, 35);
        
        // Create address for source person
        EObject sourceAddress = EcoreUtil.create(addressClass);
        sourceAddress.eSet(streetAttribute, "123 Main Street");
        source.eSet(addressReference, sourceAddress);

        // Create target person
        EObject target = EcoreUtil.create(personClass);

        // Use ECopier with copyContainments = true
        ECopier copier = new ECopier(target, null);
        copier.setCopyContainments(true);
        EObject result = copier.copy(source);
        copier.copyReferences();

        System.out.println("=== ECopier with copyContainments=true ===");
        System.out.println("Source ID: " + EcoreUtil.getID(source));
        System.out.println("Result ID: " + EcoreUtil.getID(result));
        System.out.println("Source name: " + source.eGet(nameAttribute));
        System.out.println("Result name: " + result.eGet(nameAttribute));
        System.out.println("Source age: " + source.eGet(ageAttribute));
        System.out.println("Result age: " + result.eGet(ageAttribute));
        
        // Check containment copying
        EObject resultAddress = (EObject) result.eGet(addressReference);
        System.out.println("Source address: " + (sourceAddress != null ? sourceAddress.eGet(streetAttribute) : "null"));
        System.out.println("Result address: " + (resultAddress != null ? resultAddress.eGet(streetAttribute) : "null"));

        // Verify copying works with containments flag
        assertEquals("person-789", EcoreUtil.getID(result), "ID should be copied");
        assertEquals("Carol Davis", (String) result.eGet(nameAttribute), "name should be copied");
        assertEquals(35, (Integer) result.eGet(ageAttribute), "age should be copied");
        
        // Verify containment reference is copied
        assertNotNull(resultAddress, "Address containment should be copied");
        assertEquals("123 Main Street", (String) resultAddress.eGet(streetAttribute), "Address street should be copied");
        
        System.out.println("✓ ECopier with copyContainments flag works correctly");
    }


}