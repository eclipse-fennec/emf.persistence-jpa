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
 *      Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.persistence.test.copier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.sql.DataSource;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.eclipselink.copying.ECopier;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicType;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicTypeBuilder;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicTypeContext;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.fennec.persistence.test.EPersistenceBase;
import org.eclipse.fennec.persistence.test.annotations.TestAnnotations;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;

import jakarta.persistence.EntityManagerFactory;

/**
 * Integration test for ECopier using real EclipseLink entities created via EntityManagerFactory.
 * This test verifies that ECopier correctly handles copying between EMF DynamicEObjects
 * and real EclipseLink-managed entities.
 * 
 * @author Data In Motion
 * @since 01.10.2025
 */
@ExtendWith(MockitoExtension.class)
public class ECopierIntegrationTest extends EPersistenceBase {

    @Override
    protected EntityMappings setupMappings(EntityMapper mapper, EPackage ePackage) {
        EClass personEClass = (EClass) ePackage.getEClassifier("Person");
        assertNotNull(personEClass);
        EntityMappings mapping = mapper.createMappings(List.of(personEClass));
        return mapping;
    }

    @Test
    @TestAnnotations.DefaultEPersistenceConfiguration
    public void testECopierWithRealEclipseLinkEntities(
            @InjectService(timeout = 500) ServiceAware<DataSource> dataSourceAware,
            @InjectService(filter = "(emf.name=fennec.persistence.model)") ServiceAware<EPackage> modelPackageAware,
            @InjectService(timeout = 5000) ServiceAware<EntityManagerFactory> emfAware) throws Exception {

        // Verify services are available
        assertNotNull(dataSourceAware.getService(), "DataSource should be available");
        assertNotNull(modelPackageAware.getService(), "Model package should be available");
        assertNotNull(emfAware.getService(), "EntityManagerFactory should be available");

        EntityManagerFactory emf = emfAware.getService();
        Server server = JpaHelper.getServerSession(emf);
        EPackage modelPackage = modelPackageAware.getService();

        EClass personEClass = (EClass) modelPackage.getEClassifier("Person");
        assertNotNull(personEClass, "Person EClass should be available");

        ClassDescriptor personDescriptor = server.getDescriptorForAlias(personEClass.getName());
        assertNotNull(personDescriptor, "Person ClassDescriptor should be available");

        System.out.println("=== ECopier Integration Test with Real EclipseLink Entities ===");
        System.out.println("Person ClassDescriptor: " + personDescriptor.getJavaClass().getSimpleName());

        // Get Person attributes that actually exist in the test model
        EStructuralFeature stringDefaultFeature = personEClass.getEStructuralFeature("stringDefault");
        EStructuralFeature bigIntValueFeature = personEClass.getEStructuralFeature("bigIntValue");
        assertNotNull(stringDefaultFeature, "stringDefault attribute should exist");
        assertNotNull(bigIntValueFeature, "bigIntValue attribute should exist");

        // Create EMF DynamicEObject (source)
        EObject emfDynamicObject = modelPackage.getEFactoryInstance().create(personEClass);
        EcoreUtil.setID(emfDynamicObject, "person-integration-001");
        emfDynamicObject.eSet(stringDefaultFeature, "Integration Test Person");
        emfDynamicObject.eSet(bigIntValueFeature, java.math.BigInteger.valueOf(12345));

        System.out.println("Created EMF DynamicEObject (source):");
        System.out.println("  Class: " + emfDynamicObject.getClass().getSimpleName());
        System.out.println("  ID: " + EcoreUtil.getID(emfDynamicObject));
        System.out.println("  StringDefault: " + emfDynamicObject.eGet(stringDefaultFeature));
        System.out.println("  BigIntValue: " + emfDynamicObject.eGet(bigIntValueFeature));

        // Create EclipseLink entity (target) using EntityManagerFactory
        EObject eclipseLinkEntity = (EObject) personDescriptor.getInstantiationPolicy().buildNewInstance();
        assertNotNull(eclipseLinkEntity, "EclipseLink entity should be created");

        System.out.println("Created EclipseLink entity (target):");
        System.out.println("  Class: " + eclipseLinkEntity.getClass().getSimpleName());
        System.out.println("  ID before copy: " + EcoreUtil.getID(eclipseLinkEntity));
        System.out.println("  StringDefault before copy: " + eclipseLinkEntity.eGet(stringDefaultFeature));
        System.out.println("  BigIntValue before copy: " + eclipseLinkEntity.eGet(bigIntValueFeature));

        // Test ECopier.copyInto() from EMF DynamicEObject to EclipseLink entity
        System.out.println("Testing ECopier.copyInto() from EMF DynamicEObject to EclipseLink entity...");
        EObject result = ECopier.copyInto(emfDynamicObject, eclipseLinkEntity);

        System.out.println("ECopier result:");
        System.out.println("  Class: " + result.getClass().getSimpleName());
        System.out.println("  ID after copy: " + EcoreUtil.getID(result));
        System.out.println("  StringDefault after copy: " + result.eGet(stringDefaultFeature));
        System.out.println("  BigIntValue after copy: " + result.eGet(bigIntValueFeature));

        // Verify ECopier correctly copied all values
        assertEquals("person-integration-001", EcoreUtil.getID(result), 
                    "ID should be copied from EMF DynamicEObject to EclipseLink entity");
        assertEquals("Integration Test Person", (String) result.eGet(stringDefaultFeature), 
                    "StringDefault should be copied from source");
        assertEquals(java.math.BigInteger.valueOf(12345), result.eGet(bigIntValueFeature), 
                    "BigIntValue should be copied from source");

        // Verify the result is the same EclipseLink entity instance (copyInto modifies target)
        assertSame(eclipseLinkEntity, result, "ECopier.copyInto() should return the same target instance");

        System.out.println("✓ ECopier successfully copied from EMF DynamicEObject to EclipseLink entity!");

        // Test reverse direction: EclipseLink entity to EMF DynamicEObject
        System.out.println("Testing reverse direction: EclipseLink entity to EMF DynamicEObject...");
        
        // Modify the EclipseLink entity
        EcoreUtil.setID(eclipseLinkEntity, "person-integration-002");
        eclipseLinkEntity.eSet(stringDefaultFeature, "Reverse Test Person");
        eclipseLinkEntity.eSet(bigIntValueFeature, java.math.BigInteger.valueOf(67890));

        // Create new EMF DynamicEObject as target
        EObject newDynamicObject = modelPackage.getEFactoryInstance().create(personEClass);
        
        // Copy from EclipseLink entity to EMF DynamicEObject
        EObject reverseResult = ECopier.copyInto(eclipseLinkEntity, newDynamicObject);

        System.out.println("Reverse ECopier result:");
        System.out.println("  Class: " + reverseResult.getClass().getSimpleName());
        System.out.println("  ID: " + EcoreUtil.getID(reverseResult));
        System.out.println("  StringDefault: " + reverseResult.eGet(stringDefaultFeature));
        System.out.println("  BigIntValue: " + reverseResult.eGet(bigIntValueFeature));

        // Verify reverse copying works
        assertEquals("person-integration-002", EcoreUtil.getID(reverseResult), 
                    "ID should be copied from EclipseLink entity to EMF DynamicEObject");
        assertEquals("Reverse Test Person", (String) reverseResult.eGet(stringDefaultFeature), 
                    "StringDefault should be copied from EclipseLink entity");
        assertEquals(java.math.BigInteger.valueOf(67890), reverseResult.eGet(bigIntValueFeature), 
                    "BigIntValue should be copied from EclipseLink entity");

        assertSame(newDynamicObject, reverseResult, "ECopier.copyInto() should return the same target instance");

        System.out.println("✓ Reverse ECopier copying also works correctly!");

        // Test that objects are indeed different types
        assertNotEquals(emfDynamicObject.getClass(), eclipseLinkEntity.getClass(), 
                       "EMF DynamicEObject and EclipseLink entity should have different classes");

        System.out.println("✓ Integration test completed successfully:");
        System.out.println("  - ECopier works between EMF DynamicEObject ↔ EclipseLink entity");
        System.out.println("  - ID copying works correctly in both directions");
        System.out.println("  - Attribute copying works correctly in both directions");
        System.out.println("  - copyInto() returns the correct target instance");
    }

    @Test
    @TestAnnotations.DefaultEPersistenceConfiguration
    public void testECopierWithSuccessfulEDynamicTypeContext(@InjectService(timeout = 5000) ServiceAware<EntityManagerFactory> emfAware,
            @InjectService(filter = "(emf.name=fennec.persistence.model)") EPackage modelPackage, @Mock Function<EObject, EObject> factoryFunction) throws Exception {

    	assertFalse(emfAware.isEmpty());

        EClass personEClass = (EClass) modelPackage.getEClassifier("Person");
        assertNotNull(personEClass, "Person EClass should be available");

        // Get the DatabaseSession to access ClassDescriptors and create EDynamicTypeContext
        EntityManagerFactory emf = emfAware.getService();
		Server server = JpaHelper.getServerSession(emf);
        ClassDescriptor personDescriptor = server.getDescriptorForAlias(personEClass.getName());
        assertNotNull(personDescriptor, "Person ClassDescriptor should be available");

        System.out.println("=== Testing ECopier with Successful EDynamicTypeContext ===");

        // Get attributes
        EStructuralFeature stringDefaultFeature = personEClass.getEStructuralFeature("stringDefault");
        EStructuralFeature bigIntValueFeature = personEClass.getEStructuralFeature("bigIntValue");
        assertNotNull(stringDefaultFeature, "stringDefault attribute should exist");
        assertNotNull(bigIntValueFeature, "bigIntValue attribute should exist");

        // Create source EMF DynamicEObject
        EObject source = EcoreUtil.create(personEClass);
        EcoreUtil.setID(source, "context-test-001");
        source.eSet(stringDefaultFeature, "Context Priority Test");
        source.eSet(bigIntValueFeature, java.math.BigInteger.valueOf(99999));

        System.out.println("Created source EMF DynamicEObject:");
        System.out.println("  ID: " + EcoreUtil.getID(source));
        System.out.println("  StringDefault: " + source.eGet(stringDefaultFeature));
        System.out.println("  BigIntValue: " + source.eGet(bigIntValueFeature));

        // Create a real EDynamicTypeContext (this would be available inside EclipseLink)
        // For this integration test, we'll use a mock context that represents successful creation
        EDynamicTypeContext mockContext = mock(EDynamicTypeContext.class);
        EDynamicTypeBuilder mockTypeBuilder = mock(EDynamicTypeBuilder.class);
        EDynamicType mockDynamicType = mock(EDynamicType.class);

        // Mock the context to return the real ClassDescriptor and let EDynamicHelper.createInstance work
        // Use any(EClass.class) because the EClass instance passed at runtime may be different from personEClass
        when(mockContext.getOptionalETypeBuilder(any(EClass.class))).thenReturn(Optional.of(mockTypeBuilder));
        when(mockTypeBuilder.getType()).thenReturn(mockDynamicType);
        when(mockDynamicType.getDescriptor()).thenReturn(personDescriptor);

        // Create a factory function that should NOT be called when context succeeds
        // Note: No stubbing needed since we verify the function is never called

        // Test ECopier with successful context (context should take priority over factory)
        ECopier copier = new ECopier(null, mockContext);
        copier.setCopyFunction(factoryFunction);

        System.out.println("Testing ECopier with EDynamicTypeContext priority...");
        EObject result = assertDoesNotThrow(() -> {
            EObject res = copier.copy(source);
            copier.copyReferences();
            return res;
        });

        System.out.println("ECopier result:");
        System.out.println("  Class: " + result.getClass().getSimpleName());
        System.out.println("  ID: " + EcoreUtil.getID(result));
        System.out.println("  StringDefault: " + result.eGet(stringDefaultFeature));
        System.out.println("  BigIntValue: " + result.eGet(bigIntValueFeature));

        // Verify the result
        assertNotNull(result, "Result should not be null");
        assertEquals("context-test-001", EcoreUtil.getID(result), "ID should be copied from source");
        assertEquals("Context Priority Test", result.eGet(stringDefaultFeature), "Attributes should be copied from source");
        assertEquals(java.math.BigInteger.valueOf(99999), result.eGet(bigIntValueFeature), "BigInt should be copied from source");

        // Verify that context was used (EDynamicHelper.createInstance was called)
        verify(mockContext).getOptionalETypeBuilder(any(EClass.class));
        verify(mockTypeBuilder).getType();
        verify(mockDynamicType).getDescriptor();

        // Verify that factory function was NOT called (context took priority)
        verify(factoryFunction, never()).apply(any());

        // Verify the result is an EclipseLink entity (created via EDynamicHelper.createInstance)
        assertNotEquals(DynamicEntityImpl.class, result.getClass(), "Result class should be an EclipseLink EObject with rela class name created by EDynamicHelper.createInstance");

        System.out.println("✓ EDynamicTypeContext successful creation test completed:");
        System.out.println("  - Context chain was called successfully");
        System.out.println("  - EDynamicHelper.createInstance() created real EclipseLink entity");
        System.out.println("  - Factory function was NOT called (context took priority)");
        System.out.println("  - Source attributes were copied to context-created object");
        System.out.println("  - Priority order verified: Context > Factory > Standard EMF");
    }
}