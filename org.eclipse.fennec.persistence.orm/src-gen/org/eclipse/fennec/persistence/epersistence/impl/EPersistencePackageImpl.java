/**
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
 */
package org.eclipse.fennec.persistence.epersistence.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EValidator;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

import org.eclipse.fennec.persistence.eorm.EORMPackage;

import org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl;

import org.eclipse.fennec.persistence.epersistence.EPersistenceFactory;
import org.eclipse.fennec.persistence.epersistence.EPersistencePackage;
import org.eclipse.fennec.persistence.epersistence.Persistence;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnitCachingType;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnitTransactionType;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnitValidationMode;
import org.eclipse.fennec.persistence.epersistence.Properties;
import org.eclipse.fennec.persistence.epersistence.Property;

import org.eclipse.fennec.persistence.epersistence.util.EPersistenceValidator;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class EPersistencePackageImpl extends EPackageImpl implements EPersistencePackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass persistenceUnitEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass propertiesEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass propertyEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass persistenceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum persistenceUnitValidationModeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum persistenceUnitCachingTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum persistenceUnitTransactionTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType versionEDataType = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.fennec.persistence.epersistence.EPersistencePackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private EPersistencePackageImpl() {
		super(eNS_URI, EPersistenceFactory.eINSTANCE);
	}
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link EPersistencePackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static EPersistencePackage init() {
		if (isInited) return (EPersistencePackage)EPackage.Registry.INSTANCE.getEPackage(EPersistencePackage.eNS_URI);

		// Obtain or create and register package
		Object registeredEPersistencePackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		EPersistencePackageImpl theEPersistencePackage = registeredEPersistencePackage instanceof EPersistencePackageImpl ? (EPersistencePackageImpl)registeredEPersistencePackage : new EPersistencePackageImpl();

		isInited = true;

		// Initialize simple dependencies
		XMLTypePackage.eINSTANCE.eClass();

		// Obtain or create and register interdependencies
		Object registeredPackage = EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI);
		EORMPackageImpl theEORMPackage = (EORMPackageImpl)(registeredPackage instanceof EORMPackageImpl ? registeredPackage : EORMPackage.eINSTANCE);

		// Load packages
		theEORMPackage.loadPackage();

		// Create package meta-data objects
		theEPersistencePackage.createPackageContents();

		// Initialize created meta-data
		theEPersistencePackage.initializePackageContents();

		// Fix loaded packages
		theEORMPackage.fixPackageContents();

		// Register package validator
		EValidator.Registry.INSTANCE.put
			(theEPersistencePackage,
			 new EValidator.Descriptor() {
				 @Override
				 public EValidator getEValidator() {
					 return EPersistenceValidator.INSTANCE;
				 }
			 });

		// Mark meta-data to indicate it can't be changed
		theEPersistencePackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(EPersistencePackage.eNS_URI, theEPersistencePackage);
		return theEPersistencePackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPersistenceUnit() {
		return persistenceUnitEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPersistenceUnit_Description() {
		return (EAttribute)persistenceUnitEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPersistenceUnit_Provider() {
		return (EAttribute)persistenceUnitEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPersistenceUnit_Qualifier() {
		return (EAttribute)persistenceUnitEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPersistenceUnit_Scope() {
		return (EAttribute)persistenceUnitEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPersistenceUnit_JtaDataSource() {
		return (EAttribute)persistenceUnitEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPersistenceUnit_NonJtaDataSource() {
		return (EAttribute)persistenceUnitEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPersistenceUnit_MappingFile() {
		return (EAttribute)persistenceUnitEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPersistenceUnit_JarFile() {
		return (EAttribute)persistenceUnitEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPersistenceUnit_EntityClass() {
		return (EAttribute)persistenceUnitEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPersistenceUnit_ExcludeUnlistedClasses() {
		return (EAttribute)persistenceUnitEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPersistenceUnit_SharedCacheMode() {
		return (EAttribute)persistenceUnitEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPersistenceUnit_ValidationMode() {
		return (EAttribute)persistenceUnitEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPersistenceUnit_Properties() {
		return (EReference)persistenceUnitEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPersistenceUnit_Any() {
		return (EAttribute)persistenceUnitEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPersistenceUnit_Name() {
		return (EAttribute)persistenceUnitEClass.getEStructuralFeatures().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPersistenceUnit_TransactionType() {
		return (EAttribute)persistenceUnitEClass.getEStructuralFeatures().get(15);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPersistenceUnit_EntityMappings() {
		return (EReference)persistenceUnitEClass.getEStructuralFeatures().get(16);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getProperties() {
		return propertiesEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getProperties_Property() {
		return (EReference)propertiesEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getProperty() {
		return propertyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getProperty_Name() {
		return (EAttribute)propertyEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getProperty_Value() {
		return (EAttribute)propertyEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPersistence() {
		return persistenceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPersistence_PersistenceUnit() {
		return (EReference)persistenceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPersistence_Version() {
		return (EAttribute)persistenceEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getPersistenceUnitValidationMode() {
		return persistenceUnitValidationModeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getPersistenceUnitCachingType() {
		return persistenceUnitCachingTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getPersistenceUnitTransactionType() {
		return persistenceUnitTransactionTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getVersion() {
		return versionEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EPersistenceFactory getEPersistenceFactory() {
		return (EPersistenceFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		persistenceUnitEClass = createEClass(PERSISTENCE_UNIT);
		createEAttribute(persistenceUnitEClass, PERSISTENCE_UNIT__DESCRIPTION);
		createEAttribute(persistenceUnitEClass, PERSISTENCE_UNIT__PROVIDER);
		createEAttribute(persistenceUnitEClass, PERSISTENCE_UNIT__QUALIFIER);
		createEAttribute(persistenceUnitEClass, PERSISTENCE_UNIT__SCOPE);
		createEAttribute(persistenceUnitEClass, PERSISTENCE_UNIT__JTA_DATA_SOURCE);
		createEAttribute(persistenceUnitEClass, PERSISTENCE_UNIT__NON_JTA_DATA_SOURCE);
		createEAttribute(persistenceUnitEClass, PERSISTENCE_UNIT__MAPPING_FILE);
		createEAttribute(persistenceUnitEClass, PERSISTENCE_UNIT__JAR_FILE);
		createEAttribute(persistenceUnitEClass, PERSISTENCE_UNIT__ENTITY_CLASS);
		createEAttribute(persistenceUnitEClass, PERSISTENCE_UNIT__EXCLUDE_UNLISTED_CLASSES);
		createEAttribute(persistenceUnitEClass, PERSISTENCE_UNIT__SHARED_CACHE_MODE);
		createEAttribute(persistenceUnitEClass, PERSISTENCE_UNIT__VALIDATION_MODE);
		createEReference(persistenceUnitEClass, PERSISTENCE_UNIT__PROPERTIES);
		createEAttribute(persistenceUnitEClass, PERSISTENCE_UNIT__ANY);
		createEAttribute(persistenceUnitEClass, PERSISTENCE_UNIT__NAME);
		createEAttribute(persistenceUnitEClass, PERSISTENCE_UNIT__TRANSACTION_TYPE);
		createEReference(persistenceUnitEClass, PERSISTENCE_UNIT__ENTITY_MAPPINGS);

		propertiesEClass = createEClass(PROPERTIES);
		createEReference(propertiesEClass, PROPERTIES__PROPERTY);

		propertyEClass = createEClass(PROPERTY);
		createEAttribute(propertyEClass, PROPERTY__NAME);
		createEAttribute(propertyEClass, PROPERTY__VALUE);

		persistenceEClass = createEClass(PERSISTENCE);
		createEReference(persistenceEClass, PERSISTENCE__PERSISTENCE_UNIT);
		createEAttribute(persistenceEClass, PERSISTENCE__VERSION);

		// Create enums
		persistenceUnitValidationModeEEnum = createEEnum(PERSISTENCE_UNIT_VALIDATION_MODE);
		persistenceUnitCachingTypeEEnum = createEEnum(PERSISTENCE_UNIT_CACHING_TYPE);
		persistenceUnitTransactionTypeEEnum = createEEnum(PERSISTENCE_UNIT_TRANSACTION_TYPE);

		// Create data types
		versionEDataType = createEDataType(VERSION);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		XMLTypePackage theXMLTypePackage = (XMLTypePackage)EPackage.Registry.INSTANCE.getEPackage(XMLTypePackage.eNS_URI);
		EORMPackage theEORMPackage = (EORMPackage)EPackage.Registry.INSTANCE.getEPackage(EORMPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes, features, and operations; add parameters
		initEClass(persistenceUnitEClass, PersistenceUnit.class, "PersistenceUnit", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPersistenceUnit_Description(), theXMLTypePackage.getString(), "description", null, 0, 1, PersistenceUnit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPersistenceUnit_Provider(), theXMLTypePackage.getString(), "provider", null, 0, 1, PersistenceUnit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPersistenceUnit_Qualifier(), theXMLTypePackage.getString(), "qualifier", null, 0, -1, PersistenceUnit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPersistenceUnit_Scope(), theXMLTypePackage.getString(), "scope", null, 0, 1, PersistenceUnit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPersistenceUnit_JtaDataSource(), theXMLTypePackage.getString(), "jtaDataSource", null, 0, 1, PersistenceUnit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPersistenceUnit_NonJtaDataSource(), theXMLTypePackage.getString(), "nonJtaDataSource", null, 0, 1, PersistenceUnit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPersistenceUnit_MappingFile(), theXMLTypePackage.getString(), "mappingFile", null, 0, -1, PersistenceUnit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPersistenceUnit_JarFile(), theXMLTypePackage.getString(), "jarFile", null, 0, -1, PersistenceUnit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPersistenceUnit_EntityClass(), theXMLTypePackage.getString(), "entityClass", null, 0, -1, PersistenceUnit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPersistenceUnit_ExcludeUnlistedClasses(), theXMLTypePackage.getBoolean(), "excludeUnlistedClasses", "true", 0, 1, PersistenceUnit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPersistenceUnit_SharedCacheMode(), this.getPersistenceUnitCachingType(), "sharedCacheMode", null, 0, 1, PersistenceUnit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPersistenceUnit_ValidationMode(), this.getPersistenceUnitValidationMode(), "validationMode", null, 0, 1, PersistenceUnit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPersistenceUnit_Properties(), this.getProperties(), null, "properties", null, 0, 1, PersistenceUnit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPersistenceUnit_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, PersistenceUnit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPersistenceUnit_Name(), theXMLTypePackage.getString(), "name", null, 1, 1, PersistenceUnit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPersistenceUnit_TransactionType(), this.getPersistenceUnitTransactionType(), "transactionType", null, 0, 1, PersistenceUnit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPersistenceUnit_EntityMappings(), theEORMPackage.getEntityMappings(), null, "entityMappings", null, 0, -1, PersistenceUnit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(propertiesEClass, Properties.class, "Properties", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getProperties_Property(), this.getProperty(), null, "property", null, 0, -1, Properties.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(propertyEClass, Property.class, "Property", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getProperty_Name(), theXMLTypePackage.getString(), "name", null, 1, 1, Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getProperty_Value(), theXMLTypePackage.getString(), "value", null, 1, 1, Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(persistenceEClass, Persistence.class, "Persistence", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getPersistence_PersistenceUnit(), this.getPersistenceUnit(), null, "persistenceUnit", null, 1, -1, Persistence.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPersistence_Version(), this.getVersion(), "version", "3.2", 1, 1, Persistence.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(persistenceUnitValidationModeEEnum, PersistenceUnitValidationMode.class, "PersistenceUnitValidationMode");
		addEEnumLiteral(persistenceUnitValidationModeEEnum, PersistenceUnitValidationMode.AUTO);
		addEEnumLiteral(persistenceUnitValidationModeEEnum, PersistenceUnitValidationMode.CALLBACK);
		addEEnumLiteral(persistenceUnitValidationModeEEnum, PersistenceUnitValidationMode.NONE);

		initEEnum(persistenceUnitCachingTypeEEnum, PersistenceUnitCachingType.class, "PersistenceUnitCachingType");
		addEEnumLiteral(persistenceUnitCachingTypeEEnum, PersistenceUnitCachingType.ALL);
		addEEnumLiteral(persistenceUnitCachingTypeEEnum, PersistenceUnitCachingType.NONE);
		addEEnumLiteral(persistenceUnitCachingTypeEEnum, PersistenceUnitCachingType.ENABLESELECTIVE);
		addEEnumLiteral(persistenceUnitCachingTypeEEnum, PersistenceUnitCachingType.DISABLESELECTIVE);
		addEEnumLiteral(persistenceUnitCachingTypeEEnum, PersistenceUnitCachingType.UNSPECIFIED);

		initEEnum(persistenceUnitTransactionTypeEEnum, PersistenceUnitTransactionType.class, "PersistenceUnitTransactionType");
		addEEnumLiteral(persistenceUnitTransactionTypeEEnum, PersistenceUnitTransactionType.JTA);
		addEEnumLiteral(persistenceUnitTransactionTypeEEnum, PersistenceUnitTransactionType.RESOURCELOCAL);

		// Initialize data types
		initEDataType(versionEDataType, String.class, "Version", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http:///org/eclipse/emf/ecore/util/ExtendedMetaData
		createExtendedMetaDataAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createExtendedMetaDataAnnotations() {
		String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";
		addAnnotation
		  (versionEDataType,
		   source,
		   new String[] {
			   "name", "versionType",
			   "baseType", "http://www.eclipse.org/emf/2003/XMLType#token",
			   "pattern", "[0-9]+(\\.[0-9]+)*"
		   });
		addAnnotation
		  (getPersistenceUnit_Description(),
		   source,
		   new String[] {
			   "kind", "element",
			   "name", "description",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getPersistenceUnit_Provider(),
		   source,
		   new String[] {
			   "kind", "element",
			   "name", "provider",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getPersistenceUnit_Qualifier(),
		   source,
		   new String[] {
			   "kind", "element",
			   "name", "qualifier",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getPersistenceUnit_Scope(),
		   source,
		   new String[] {
			   "kind", "element",
			   "name", "scope",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getPersistenceUnit_JtaDataSource(),
		   source,
		   new String[] {
			   "kind", "element",
			   "name", "jta-data-source",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getPersistenceUnit_NonJtaDataSource(),
		   source,
		   new String[] {
			   "kind", "element",
			   "name", "non-jta-data-source",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getPersistenceUnit_MappingFile(),
		   source,
		   new String[] {
			   "kind", "element",
			   "name", "mapping-file",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getPersistenceUnit_JarFile(),
		   source,
		   new String[] {
			   "kind", "element",
			   "name", "jar-file",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getPersistenceUnit_EntityClass(),
		   source,
		   new String[] {
			   "kind", "element",
			   "name", "class",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getPersistenceUnit_ExcludeUnlistedClasses(),
		   source,
		   new String[] {
			   "kind", "element",
			   "name", "exclude-unlisted-classes",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getPersistenceUnit_SharedCacheMode(),
		   source,
		   new String[] {
			   "kind", "element",
			   "name", "shared-cache-mode",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getPersistenceUnit_ValidationMode(),
		   source,
		   new String[] {
			   "kind", "element",
			   "name", "validation-mode",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getPersistenceUnit_Properties(),
		   source,
		   new String[] {
			   "kind", "element",
			   "name", "properties",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getPersistenceUnit_Any(),
		   source,
		   new String[] {
			   "kind", "elementWildcard",
			   "wildcards", "##other",
			   "name", ":13",
			   "processing", "lax"
		   });
		addAnnotation
		  (getPersistenceUnit_Name(),
		   source,
		   new String[] {
			   "kind", "attribute",
			   "name", "name"
		   });
		addAnnotation
		  (getPersistenceUnit_TransactionType(),
		   source,
		   new String[] {
			   "kind", "attribute",
			   "name", "transaction-type"
		   });
		addAnnotation
		  (getProperties_Property(),
		   source,
		   new String[] {
			   "kind", "element",
			   "name", "property",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getProperty_Name(),
		   source,
		   new String[] {
			   "kind", "attribute",
			   "name", "name"
		   });
		addAnnotation
		  (getProperty_Value(),
		   source,
		   new String[] {
			   "kind", "attribute",
			   "name", "value"
		   });
		addAnnotation
		  (getPersistence_PersistenceUnit(),
		   source,
		   new String[] {
			   "kind", "element",
			   "name", "persistence-unit",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getPersistence_Version(),
		   source,
		   new String[] {
			   "kind", "attribute",
			   "name", "version"
		   });
	}

} //EPersistencePackageImpl
