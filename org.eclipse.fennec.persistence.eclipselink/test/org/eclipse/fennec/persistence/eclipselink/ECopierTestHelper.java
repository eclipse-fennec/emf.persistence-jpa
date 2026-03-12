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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * Helper class for ECopier tests providing reusable test model creation and test data setup.
 * 
 * <p>This helper creates a test model with Person and Address classes that includes:</p>
 * <ul>
 * <li><strong>Person class</strong> - with id, name attributes</li>
 * <li><strong>Address class</strong> - with street attribute</li>
 * <li><strong>Containment relationship</strong> - Person -> Address (bidirectional)</li>
 * <li><strong>Non-containment relationship</strong> - Person -> Person friends (many-to-many, bidirectional)</li>
 * </ul>
 * 
 * @author Data In Motion
 * @since 02.10.2025
 */
public class ECopierTestHelper {

    /**
     * Creates a comprehensive test model for ECopier testing with various relationship types.
     * 
     * <p>The model includes:</p>
     * <ul>
     * <li><strong>Person class</strong> with id (EString, ID=true) and name (EString) attributes</li>
     * <li><strong>Address class</strong> with street (EString) attribute</li>
     * <li><strong>Person.address</strong> - containment reference to Address (0..1)</li>
     * <li><strong>Address.owner</strong> - opposite non-containment reference to Person (0..1)</li>
     * <li><strong>Person.friends</strong> - self-referencing non-containment reference (0..*), bidirectional</li>
     * </ul>
     * 
     * <p>The package is automatically registered in the EPackage.Registry for EMF usage.</p>
     * 
     * @return the created test EPackage with Person and Address classes
     */
    public static EPackage createTestModel() {
        EPackage pkg = org.eclipse.emf.ecore.EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName("ecopierTestModel");
        pkg.setNsURI("http://ecopier.test.model/1.0");
        pkg.setNsPrefix("ecopier");

        // Create Person class
        EClass person = org.eclipse.emf.ecore.EcoreFactory.eINSTANCE.createEClass();
        person.setName("Person");

        EAttribute personId = org.eclipse.emf.ecore.EcoreFactory.eINSTANCE.createEAttribute();
        personId.setName("id");
        personId.setEType(org.eclipse.emf.ecore.EcorePackage.Literals.ESTRING);
        personId.setID(true);
        person.getEStructuralFeatures().add(personId);

        EAttribute personName = org.eclipse.emf.ecore.EcoreFactory.eINSTANCE.createEAttribute();
        personName.setName("name");
        personName.setEType(org.eclipse.emf.ecore.EcorePackage.Literals.ESTRING);
        person.getEStructuralFeatures().add(personName);

        EAttribute personAge = org.eclipse.emf.ecore.EcoreFactory.eINSTANCE.createEAttribute();
        personAge.setName("age");
        personAge.setEType(org.eclipse.emf.ecore.EcorePackage.Literals.EINT);
        person.getEStructuralFeatures().add(personAge);

        // Create Address class
        EClass address = org.eclipse.emf.ecore.EcoreFactory.eINSTANCE.createEClass();
        address.setName("Address");

        EAttribute addressStreet = org.eclipse.emf.ecore.EcoreFactory.eINSTANCE.createEAttribute();
        addressStreet.setName("street");
        addressStreet.setEType(org.eclipse.emf.ecore.EcorePackage.Literals.ESTRING);
        address.getEStructuralFeatures().add(addressStreet);

        // Create Person -> Address reference (containment)
        EReference personAddress = org.eclipse.emf.ecore.EcoreFactory.eINSTANCE.createEReference();
        personAddress.setName("address");
        personAddress.setEType(address);
        personAddress.setContainment(true);
        personAddress.setUpperBound(1);
        person.getEStructuralFeatures().add(personAddress);

        // Create Address -> Person reference (opposite, non-containment)
        EReference addressOwner = org.eclipse.emf.ecore.EcoreFactory.eINSTANCE.createEReference();
        addressOwner.setName("owner");
        addressOwner.setEType(person);
        addressOwner.setContainment(false);
        addressOwner.setUpperBound(1);
        address.getEStructuralFeatures().add(addressOwner);

        // Set bidirectional relationship for Person <-> Address
        personAddress.setEOpposite(addressOwner);
        addressOwner.setEOpposite(personAddress);

        // Create non-containment bidirectional references between persons
        // Person -> Person reference (friends, non-containment, many-to-many)
        EReference personFriends = org.eclipse.emf.ecore.EcoreFactory.eINSTANCE.createEReference();
        personFriends.setName("friends");
        personFriends.setEType(person);
        personFriends.setContainment(false);
        personFriends.setUpperBound(-1); // many
        person.getEStructuralFeatures().add(personFriends);

        // Set self-referencing bidirectional relationship (friends are mutual)
        personFriends.setEOpposite(personFriends);

        pkg.getEClassifiers().add(person);
        pkg.getEClassifiers().add(address);

        // Register the package
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);

        return pkg;
    }

    /**
     * Creates a Person object with an associated Address using the test model.
     * 
     * <p>This helper method creates a complete Person-Address relationship with:</p>
     * <ul>
     * <li>Person with the specified ID and name</li>
     * <li>Address with the specified street</li>
     * <li>Bidirectional containment relationship properly established</li>
     * </ul>
     * 
     * @param testPackage the EPackage containing the Person and Address classes (from createTestModel())
     * @param personId the ID to set on the person (using EcoreUtil.setID)
     * @param personName the name attribute value for the person
     * @param street the street attribute value for the address
     * @return the created Person EObject with contained Address
     */
    public static EObject createPersonWithAddress(EPackage testPackage, String personId, String personName, String street) {
        EClass personClass = (EClass) testPackage.getEClassifier("Person");
        EClass addressClass = (EClass) testPackage.getEClassifier("Address");
        
        EAttribute personNameAttribute = (EAttribute) personClass.getEStructuralFeature("name");
        EAttribute addressStreetAttribute = (EAttribute) addressClass.getEStructuralFeature("street");
        EReference personAddressReference = (EReference) personClass.getEStructuralFeature("address");

        EObject person = EcoreUtil.create(personClass);
        EcoreUtil.setID(person, personId);
        person.eSet(personNameAttribute, personName);

        EObject address = EcoreUtil.create(addressClass);
        address.eSet(addressStreetAttribute, street);
        person.eSet(personAddressReference, address);

        return person;
    }

    /**
     * Creates a Person object with name, age, and an associated Address using the test model.
     * 
     * <p>This helper method creates a complete Person-Address relationship with:</p>
     * <ul>
     * <li>Person with the specified ID, name, and age</li>
     * <li>Address with the specified street</li>
     * <li>Bidirectional containment relationship properly established</li>
     * </ul>
     * 
     * @param testPackage the EPackage containing the Person and Address classes (from createTestModel())
     * @param personId the ID to set on the person (using EcoreUtil.setID)
     * @param personName the name attribute value for the person
     * @param personAge the age attribute value for the person
     * @param street the street attribute value for the address
     * @return the created Person EObject with contained Address
     */
    public static EObject createPersonWithAddressAndAge(EPackage testPackage, String personId, String personName, Integer personAge, String street) {
        EClass personClass = (EClass) testPackage.getEClassifier("Person");
        EClass addressClass = (EClass) testPackage.getEClassifier("Address");
        
        EAttribute personNameAttribute = (EAttribute) personClass.getEStructuralFeature("name");
        EAttribute personAgeAttribute = (EAttribute) personClass.getEStructuralFeature("age");
        EAttribute addressStreetAttribute = (EAttribute) addressClass.getEStructuralFeature("street");
        EReference personAddressReference = (EReference) personClass.getEStructuralFeature("address");

        EObject person = EcoreUtil.create(personClass);
        EcoreUtil.setID(person, personId);
        person.eSet(personNameAttribute, personName);
        person.eSet(personAgeAttribute, personAge);

        EObject address = EcoreUtil.create(addressClass);
        address.eSet(addressStreetAttribute, street);
        person.eSet(personAddressReference, address);

        return person;
    }

    /**
     * Gets the Person EClass from the test model package.
     * 
     * @param testPackage the EPackage from createTestModel()
     * @return the Person EClass
     */
    public static EClass getPersonClass(EPackage testPackage) {
        return (EClass) testPackage.getEClassifier("Person");
    }

    /**
     * Gets the Address EClass from the test model package.
     * 
     * @param testPackage the EPackage from createTestModel()
     * @return the Address EClass
     */
    public static EClass getAddressClass(EPackage testPackage) {
        return (EClass) testPackage.getEClassifier("Address");
    }

    /**
     * Gets the name attribute from the Person EClass.
     * 
     * @param testPackage the EPackage from createTestModel()
     * @return the Person.name EAttribute
     */
    public static EAttribute getPersonNameAttribute(EPackage testPackage) {
        EClass personClass = getPersonClass(testPackage);
        return (EAttribute) personClass.getEStructuralFeature("name");
    }

    /**
     * Gets the age attribute from the Person EClass.
     * 
     * @param testPackage the EPackage from createTestModel()
     * @return the Person.age EAttribute
     */
    public static EAttribute getPersonAgeAttribute(EPackage testPackage) {
        EClass personClass = getPersonClass(testPackage);
        return (EAttribute) personClass.getEStructuralFeature("age");
    }

    /**
     * Gets the street attribute from the Address EClass.
     * 
     * @param testPackage the EPackage from createTestModel()
     * @return the Address.street EAttribute
     */
    public static EAttribute getAddressStreetAttribute(EPackage testPackage) {
        EClass addressClass = getAddressClass(testPackage);
        return (EAttribute) addressClass.getEStructuralFeature("street");
    }

    /**
     * Gets the address containment reference from the Person EClass.
     * 
     * @param testPackage the EPackage from createTestModel()
     * @return the Person.address EReference (containment)
     */
    public static EReference getPersonAddressReference(EPackage testPackage) {
        EClass personClass = getPersonClass(testPackage);
        return (EReference) personClass.getEStructuralFeature("address");
    }

    /**
     * Gets the owner reference from the Address EClass.
     * 
     * @param testPackage the EPackage from createTestModel()
     * @return the Address.owner EReference (opposite of Person.address)
     */
    public static EReference getAddressOwnerReference(EPackage testPackage) {
        EClass addressClass = getAddressClass(testPackage);
        return (EReference) addressClass.getEStructuralFeature("owner");
    }

    /**
     * Gets the friends reference from the Person EClass.
     * 
     * @param testPackage the EPackage from createTestModel()
     * @return the Person.friends EReference (many-to-many, bidirectional)
     */
    public static EReference getPersonFriendsReference(EPackage testPackage) {
        EClass personClass = getPersonClass(testPackage);
        return (EReference) personClass.getEStructuralFeature("friends");
    }

    /**
     * Creates a specialized test model for containment scenarios with Parent/Child classes.
     * 
     * <p>This model is designed for testing containment relationships and includes:</p>
     * <ul>
     * <li><strong>Parent class</strong> - with id, name attributes</li>
     * <li><strong>Child class</strong> - with id, value (double) attributes</li>
     * <li><strong>Containment relationship</strong> - Parent -> Child[] (many containment)</li>
     * </ul>
     * 
     * <p>This is used by ECopierScenarioTest for testing specific containment scenarios.</p>
     * 
     * @return the created test EPackage with Parent and Child classes
     */
    public static EPackage createParentChildTestModel() {
        EPackage pkg = org.eclipse.emf.ecore.EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName("containmentTestModel");
        pkg.setNsURI("http://containment.test.model/1.0");
        pkg.setNsPrefix("ctest");

        // Create Parent class
        EClass parent = org.eclipse.emf.ecore.EcoreFactory.eINSTANCE.createEClass();
        parent.setName("Parent");

        EAttribute parentId = org.eclipse.emf.ecore.EcoreFactory.eINSTANCE.createEAttribute();
        parentId.setName("id");
        parentId.setEType(org.eclipse.emf.ecore.EcorePackage.Literals.ESTRING);
        parentId.setID(true);
        parent.getEStructuralFeatures().add(parentId);

        EAttribute parentName = org.eclipse.emf.ecore.EcoreFactory.eINSTANCE.createEAttribute();
        parentName.setName("name");
        parentName.setEType(org.eclipse.emf.ecore.EcorePackage.Literals.ESTRING);
        parent.getEStructuralFeatures().add(parentName);

        // Create Child class
        EClass child = org.eclipse.emf.ecore.EcoreFactory.eINSTANCE.createEClass();
        child.setName("Child");

        EAttribute childId = org.eclipse.emf.ecore.EcoreFactory.eINSTANCE.createEAttribute();
        childId.setName("id");
        childId.setEType(org.eclipse.emf.ecore.EcorePackage.Literals.ESTRING);
        childId.setID(true);
        child.getEStructuralFeatures().add(childId);

        EAttribute childValue = org.eclipse.emf.ecore.EcoreFactory.eINSTANCE.createEAttribute();
        childValue.setName("value");
        childValue.setEType(org.eclipse.emf.ecore.EcorePackage.Literals.EDOUBLE);
        child.getEStructuralFeatures().add(childValue);

        // Create containment reference Parent -> Child (many)
        EReference parentChildren = org.eclipse.emf.ecore.EcoreFactory.eINSTANCE.createEReference();
        parentChildren.setName("children");
        parentChildren.setEType(child);
        parentChildren.setContainment(true);
        parentChildren.setUpperBound(-1); // many
        parent.getEStructuralFeatures().add(parentChildren);

        pkg.getEClassifiers().add(parent);
        pkg.getEClassifiers().add(child);

        // Register the package
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);

        return pkg;
    }

    /**
     * Creates a Parent object with multiple Child objects for containment testing.
     * 
     * @param testPackage the EPackage from createParentChildTestModel()
     * @param parentId the ID for the parent
     * @param parentName the name for the parent
     * @param childIds array of IDs for the children
     * @param childValues array of values (double) for the children
     * @return the created Parent EObject with contained Children
     */
    public static EObject createParentWithChildren(EPackage testPackage, String parentId, String parentName, 
                                                  String[] childIds, Double[] childValues) {
        EClass parentClass = (EClass) testPackage.getEClassifier("Parent");
        EClass childClass = (EClass) testPackage.getEClassifier("Child");
        
        EAttribute parentNameAttribute = (EAttribute) parentClass.getEStructuralFeature("name");
        EAttribute childValueAttribute = (EAttribute) childClass.getEStructuralFeature("value");
        EReference parentChildrenReference = (EReference) parentClass.getEStructuralFeature("children");

        EObject parent = EcoreUtil.create(parentClass);
        EcoreUtil.setID(parent, parentId);
        parent.eSet(parentNameAttribute, parentName);

        @SuppressWarnings("unchecked")
        java.util.List<EObject> children = (java.util.List<EObject>) parent.eGet(parentChildrenReference);
        
        for (int i = 0; i < childIds.length; i++) {
            EObject child = EcoreUtil.create(childClass);
            EcoreUtil.setID(child, childIds[i]);
            child.eSet(childValueAttribute, childValues[i]);
            children.add(child);
        }

        return parent;
    }
}