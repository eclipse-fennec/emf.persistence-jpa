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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.function.Function;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.eclipselink.copying.ECopier;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicTypeContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for ECopier behavior with EDynamicTypeContext and factory functions.
 * 
 * This test class demonstrates the priority order of object creation mechanisms:
 * 1. EDynamicTypeContext (highest priority - inside EclipseLink)
 * 2. Factory Function (fallback - outside EclipseLink, e.g., JPARepository)
 * 3. Standard EMF copying (lowest priority)
 * 
 * Note: Full context testing with mocks is complex due to EclipseLink's EInstantiationPolicy requirements.
 * These tests focus on verifiable aspects of the priority behavior.
 * 
 * @author Data In Motion
 * @since 01.10.2025
 */
@ExtendWith(MockitoExtension.class)
public class ECopierContextTest {

    private EPackage testPackage;
    private EClass personClass;
    private EAttribute personNameAttribute;

    @BeforeEach
    void setUp() {
        testPackage = ECopierTestHelper.createTestModel();
        personClass = ECopierTestHelper.getPersonClass(testPackage);
        
        personNameAttribute = ECopierTestHelper.getPersonNameAttribute(testPackage);
    }

    @Test
    @DisplayName("Context empty falls back to factory function")
    void testContextEmptyFallbackToFactory(@Mock Function<EObject, EObject> factoryFunction, 
                                         @Mock EDynamicTypeContext mockContext) {
        EObject source = ECopierTestHelper.createPersonWithAddress(testPackage, "source-person", "Source Name", "Source Street");
        
        // Mock EDynamicTypeContext that returns empty Optional (simpler than full chain)
        when(mockContext.getOptionalETypeBuilder(any(EClass.class))).thenReturn(Optional.empty());
        
        // Factory function that should be called as fallback
        EObject factoryCreatedObject = EcoreUtil.create(personClass);
        factoryCreatedObject.eSet(personNameAttribute, "Factory Created");
        when(factoryFunction.apply(any())).thenReturn(factoryCreatedObject);
        
        // Create ECopier with context that returns empty and a factory function
        ECopier copier = new ECopier(null, mockContext);
        copier.setCopyFunction(factoryFunction);
        
        // Copy the source object
        EObject result = assertDoesNotThrow(() -> {
            EObject res = copier.copy(source);
            copier.copyReferences();
            return res;
        });
        
        // Verify factory function was used as fallback
        assertNotNull(result);
        assertSame(factoryCreatedObject, result, "Factory-created object should be returned");
        assertEquals("Source Name", result.eGet(personNameAttribute), "Source attributes should be copied to factory-created object");
        
        // Verify context was tried first, then factory function called
        verify(mockContext).getOptionalETypeBuilder(personClass);
        verify(factoryFunction).apply(source);
        
        System.out.println("✓ Context empty → factory function called as fallback");
        System.out.println("  - Context.getOptionalETypeBuilder() returned Optional.empty()");
        System.out.println("  - Factory function called and object created successfully");
    }

    @Test
    @DisplayName("Factory function priority when no context provided")
    void testFactoryFunctionWithoutContext(@Mock Function<EObject, EObject> factoryFunction) {
        EObject source = ECopierTestHelper.createPersonWithAddress(testPackage, "source-person", "Source Name", "Source Street");
        
        // Factory function that should be called (no context provided)
        EObject factoryCreatedObject = EcoreUtil.create(personClass);
        factoryCreatedObject.eSet(personNameAttribute, "Factory Created");
        when(factoryFunction.apply(any())).thenReturn(factoryCreatedObject);
        
        // Create ECopier with null context and factory function
        ECopier copier = new ECopier(null, null);  // null context
        copier.setCopyFunction(factoryFunction);
        
        // Copy the source object
        EObject result = assertDoesNotThrow(() -> {
            EObject res = copier.copy(source);
            copier.copyReferences();
            return res;
        });
        
        // Verify factory function was used
        assertNotNull(result);
        assertSame(factoryCreatedObject, result, "Factory-created object should be returned");
        assertEquals("Source Name", result.eGet(personNameAttribute), "Source attributes should be copied to factory-created object");
        
        // Verify factory function was called
        verify(factoryFunction).apply(source);
        
        System.out.println("✓ Factory function used when no context provided");
        System.out.println("  - No EDynamicTypeContext provided (null)");
        System.out.println("  - Factory function called directly as expected");
    }

    @Test
    @DisplayName("Standard EMF copying when both context and factory are null")
    void testStandardEMFCopyingFallback() {
        EObject source = ECopierTestHelper.createPersonWithAddress(testPackage, "source-person", "Source Name", "Source Street");
        
        // Create ECopier with null context and no factory function
        ECopier copier = new ECopier(null, null);
        // Don't set factory function - should fall back to standard EMF copying
        
        // Copy the source object
        EObject result = assertDoesNotThrow(() -> {
            EObject res = copier.copy(source);
            copier.copyReferences();
            return res;
        });
        
        // Verify standard EMF copying was used
        assertNotNull(result);
        assertEquals(personClass, result.eClass(), "Result should be same EClass as source");
        assertEquals("Source Name", result.eGet(personNameAttribute), "Attributes should be copied");
        
        // Verify it's a different object (copied, not same instance)
        assertTrue(source != result, "Result should be a copy, not the same object");
        
        System.out.println("✓ Standard EMF copying used when both context and factory are null");
        System.out.println("  - No context and no factory function provided");
        System.out.println("  - Fell back to standard EMF Copier.createCopy() behavior");
    }

}