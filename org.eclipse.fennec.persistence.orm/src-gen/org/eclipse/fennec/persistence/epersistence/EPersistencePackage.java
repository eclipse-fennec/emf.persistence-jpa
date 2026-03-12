/*
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
package org.eclipse.fennec.persistence.epersistence;


import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.fennec.emf.osgi.annotation.provide.EPackage;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * 
 * 
 *        This is the XML Schema for the persistence object/relational 
 *        mapping file.
 *        The file may be named "META-INF/orm.xml" in the persistence 
 *        archive or it may be named some other name which would be 
 *        used to locate the file as resource on the classpath.
 * 
 *        Object/relational mapping files must indicate the object/relational
 *        mapping file schema by using the persistence namespace:
 * 
 *        https://jakarta.ee/xml/ns/persistence/orm
 * 
 *        and indicate the version of the schema by
 *        using the version element as shown below:
 * 
 *       <entity-mappings xmlns="https://jakarta.ee/xml/ns/persistence/orm"
 *         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *         xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence/orm
 *           https://jakarta.ee/xml/ns/persistence/orm/orm_3_1.xsd"
 *         version="3.1">
 *           ...
 *       </entity-mappings>
 * 
 * 
 *      
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.persistence.epersistence.EPersistenceFactory
 * @model kind="package"
 * @generated
 */
@ProviderType
@EPackage(uri = EPersistencePackage.eNS_URI, genModel = "/model/epersistence.genmodel", genModelSourceLocations = {"model/epersistence.genmodel","org.eclipse.fennec.persistence.orm/model/epersistence.genmodel"}, ecore = "/model/epersistence.ecore", ecoreSourceLocations = "/model/epersistence.ecore")
public interface EPersistencePackage extends org.eclipse.emf.ecore.EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "epersistence";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://eclipse.org/fennec/persistence/epersistence/3.2.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "epersistence";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	EPersistencePackage eINSTANCE = org.eclipse.fennec.persistence.epersistence.impl.EPersistencePackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.epersistence.impl.PersistenceUnitImpl <em>Persistence Unit</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.epersistence.impl.PersistenceUnitImpl
	 * @see org.eclipse.fennec.persistence.epersistence.impl.EPersistencePackageImpl#getPersistenceUnit()
	 * @generated
	 */
	int PERSISTENCE_UNIT = 0;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Provider</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT__PROVIDER = 1;

	/**
	 * The feature id for the '<em><b>Qualifier</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT__QUALIFIER = 2;

	/**
	 * The feature id for the '<em><b>Scope</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT__SCOPE = 3;

	/**
	 * The feature id for the '<em><b>Jta Data Source</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT__JTA_DATA_SOURCE = 4;

	/**
	 * The feature id for the '<em><b>Non Jta Data Source</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT__NON_JTA_DATA_SOURCE = 5;

	/**
	 * The feature id for the '<em><b>Mapping File</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT__MAPPING_FILE = 6;

	/**
	 * The feature id for the '<em><b>Jar File</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT__JAR_FILE = 7;

	/**
	 * The feature id for the '<em><b>Entity Class</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT__ENTITY_CLASS = 8;

	/**
	 * The feature id for the '<em><b>Exclude Unlisted Classes</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT__EXCLUDE_UNLISTED_CLASSES = 9;

	/**
	 * The feature id for the '<em><b>Shared Cache Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT__SHARED_CACHE_MODE = 10;

	/**
	 * The feature id for the '<em><b>Validation Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT__VALIDATION_MODE = 11;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT__PROPERTIES = 12;

	/**
	 * The feature id for the '<em><b>Any</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT__ANY = 13;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT__NAME = 14;

	/**
	 * The feature id for the '<em><b>Transaction Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT__TRANSACTION_TYPE = 15;

	/**
	 * The feature id for the '<em><b>Entity Mappings</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT__ENTITY_MAPPINGS = 16;

	/**
	 * The number of structural features of the '<em>Persistence Unit</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT_FEATURE_COUNT = 17;

	/**
	 * The number of operations of the '<em>Persistence Unit</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.epersistence.impl.PropertiesImpl <em>Properties</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.epersistence.impl.PropertiesImpl
	 * @see org.eclipse.fennec.persistence.epersistence.impl.EPersistencePackageImpl#getProperties()
	 * @generated
	 */
	int PROPERTIES = 1;

	/**
	 * The feature id for the '<em><b>Property</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTIES__PROPERTY = 0;

	/**
	 * The number of structural features of the '<em>Properties</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTIES_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Properties</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTIES_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.epersistence.impl.PropertyImpl <em>Property</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.epersistence.impl.PropertyImpl
	 * @see org.eclipse.fennec.persistence.epersistence.impl.EPersistencePackageImpl#getProperty()
	 * @generated
	 */
	int PROPERTY = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY__NAME = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Property</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Property</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.epersistence.impl.PersistenceImpl <em>Persistence</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.epersistence.impl.PersistenceImpl
	 * @see org.eclipse.fennec.persistence.epersistence.impl.EPersistencePackageImpl#getPersistence()
	 * @generated
	 */
	int PERSISTENCE = 3;

	/**
	 * The feature id for the '<em><b>Persistence Unit</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE__PERSISTENCE_UNIT = 0;

	/**
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE__VERSION = 1;

	/**
	 * The number of structural features of the '<em>Persistence</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Persistence</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnitValidationMode <em>Persistence Unit Validation Mode</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnitValidationMode
	 * @see org.eclipse.fennec.persistence.epersistence.impl.EPersistencePackageImpl#getPersistenceUnitValidationMode()
	 * @generated
	 */
	int PERSISTENCE_UNIT_VALIDATION_MODE = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnitCachingType <em>Persistence Unit Caching Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnitCachingType
	 * @see org.eclipse.fennec.persistence.epersistence.impl.EPersistencePackageImpl#getPersistenceUnitCachingType()
	 * @generated
	 */
	int PERSISTENCE_UNIT_CACHING_TYPE = 5;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnitTransactionType <em>Persistence Unit Transaction Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnitTransactionType
	 * @see org.eclipse.fennec.persistence.epersistence.impl.EPersistencePackageImpl#getPersistenceUnitTransactionType()
	 * @generated
	 */
	int PERSISTENCE_UNIT_TRANSACTION_TYPE = 6;

	/**
	 * The meta object id for the '<em>Version</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.lang.String
	 * @see org.eclipse.fennec.persistence.epersistence.impl.EPersistencePackageImpl#getVersion()
	 * @generated
	 */
	int VERSION = 7;


	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnit <em>Persistence Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Persistence Unit</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnit
	 * @generated
	 */
	EClass getPersistenceUnit();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getDescription()
	 * @see #getPersistenceUnit()
	 * @generated
	 */
	EAttribute getPersistenceUnit_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getProvider <em>Provider</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Provider</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getProvider()
	 * @see #getPersistenceUnit()
	 * @generated
	 */
	EAttribute getPersistenceUnit_Provider();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getQualifier <em>Qualifier</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Qualifier</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getQualifier()
	 * @see #getPersistenceUnit()
	 * @generated
	 */
	EAttribute getPersistenceUnit_Qualifier();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getScope <em>Scope</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Scope</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getScope()
	 * @see #getPersistenceUnit()
	 * @generated
	 */
	EAttribute getPersistenceUnit_Scope();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getJtaDataSource <em>Jta Data Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Jta Data Source</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getJtaDataSource()
	 * @see #getPersistenceUnit()
	 * @generated
	 */
	EAttribute getPersistenceUnit_JtaDataSource();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getNonJtaDataSource <em>Non Jta Data Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Non Jta Data Source</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getNonJtaDataSource()
	 * @see #getPersistenceUnit()
	 * @generated
	 */
	EAttribute getPersistenceUnit_NonJtaDataSource();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getMappingFile <em>Mapping File</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mapping File</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getMappingFile()
	 * @see #getPersistenceUnit()
	 * @generated
	 */
	EAttribute getPersistenceUnit_MappingFile();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getJarFile <em>Jar File</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Jar File</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getJarFile()
	 * @see #getPersistenceUnit()
	 * @generated
	 */
	EAttribute getPersistenceUnit_JarFile();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getEntityClass <em>Entity Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Entity Class</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getEntityClass()
	 * @see #getPersistenceUnit()
	 * @generated
	 */
	EAttribute getPersistenceUnit_EntityClass();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnit#isExcludeUnlistedClasses <em>Exclude Unlisted Classes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Exclude Unlisted Classes</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnit#isExcludeUnlistedClasses()
	 * @see #getPersistenceUnit()
	 * @generated
	 */
	EAttribute getPersistenceUnit_ExcludeUnlistedClasses();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getSharedCacheMode <em>Shared Cache Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Shared Cache Mode</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getSharedCacheMode()
	 * @see #getPersistenceUnit()
	 * @generated
	 */
	EAttribute getPersistenceUnit_SharedCacheMode();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getValidationMode <em>Validation Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Validation Mode</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getValidationMode()
	 * @see #getPersistenceUnit()
	 * @generated
	 */
	EAttribute getPersistenceUnit_ValidationMode();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getProperties <em>Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Properties</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getProperties()
	 * @see #getPersistenceUnit()
	 * @generated
	 */
	EReference getPersistenceUnit_Properties();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getAny <em>Any</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Any</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getAny()
	 * @see #getPersistenceUnit()
	 * @generated
	 */
	EAttribute getPersistenceUnit_Any();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getName()
	 * @see #getPersistenceUnit()
	 * @generated
	 */
	EAttribute getPersistenceUnit_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getTransactionType <em>Transaction Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Transaction Type</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getTransactionType()
	 * @see #getPersistenceUnit()
	 * @generated
	 */
	EAttribute getPersistenceUnit_TransactionType();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getEntityMappings <em>Entity Mappings</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Entity Mappings</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnit#getEntityMappings()
	 * @see #getPersistenceUnit()
	 * @generated
	 */
	EReference getPersistenceUnit_EntityMappings();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.epersistence.Properties <em>Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Properties</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.Properties
	 * @generated
	 */
	EClass getProperties();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.epersistence.Properties#getProperty <em>Property</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Property</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.Properties#getProperty()
	 * @see #getProperties()
	 * @generated
	 */
	EReference getProperties_Property();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.epersistence.Property <em>Property</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Property</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.Property
	 * @generated
	 */
	EClass getProperty();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.epersistence.Property#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.Property#getName()
	 * @see #getProperty()
	 * @generated
	 */
	EAttribute getProperty_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.epersistence.Property#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.Property#getValue()
	 * @see #getProperty()
	 * @generated
	 */
	EAttribute getProperty_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.epersistence.Persistence <em>Persistence</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Persistence</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.Persistence
	 * @generated
	 */
	EClass getPersistence();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.epersistence.Persistence#getPersistenceUnit <em>Persistence Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Persistence Unit</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.Persistence#getPersistenceUnit()
	 * @see #getPersistence()
	 * @generated
	 */
	EReference getPersistence_PersistenceUnit();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.epersistence.Persistence#getVersion <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.Persistence#getVersion()
	 * @see #getPersistence()
	 * @generated
	 */
	EAttribute getPersistence_Version();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnitValidationMode <em>Persistence Unit Validation Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Persistence Unit Validation Mode</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnitValidationMode
	 * @generated
	 */
	EEnum getPersistenceUnitValidationMode();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnitCachingType <em>Persistence Unit Caching Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Persistence Unit Caching Type</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnitCachingType
	 * @generated
	 */
	EEnum getPersistenceUnitCachingType();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnitTransactionType <em>Persistence Unit Transaction Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Persistence Unit Transaction Type</em>'.
	 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnitTransactionType
	 * @generated
	 */
	EEnum getPersistenceUnitTransactionType();

	/**
	 * Returns the meta object for data type '{@link java.lang.String <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Version</em>'.
	 * @see java.lang.String
	 * @model instanceClass="java.lang.String"
	 *        extendedMetaData="name='versionType' baseType='http://www.eclipse.org/emf/2003/XMLType#token' pattern='[0-9]+(\\.[0-9]+)*'"
	 * @generated
	 */
	EDataType getVersion();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	EPersistenceFactory getEPersistenceFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.persistence.epersistence.impl.PersistenceUnitImpl <em>Persistence Unit</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.epersistence.impl.PersistenceUnitImpl
		 * @see org.eclipse.fennec.persistence.epersistence.impl.EPersistencePackageImpl#getPersistenceUnit()
		 * @generated
		 */
		EClass PERSISTENCE_UNIT = eINSTANCE.getPersistenceUnit();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PERSISTENCE_UNIT__DESCRIPTION = eINSTANCE.getPersistenceUnit_Description();

		/**
		 * The meta object literal for the '<em><b>Provider</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PERSISTENCE_UNIT__PROVIDER = eINSTANCE.getPersistenceUnit_Provider();

		/**
		 * The meta object literal for the '<em><b>Qualifier</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PERSISTENCE_UNIT__QUALIFIER = eINSTANCE.getPersistenceUnit_Qualifier();

		/**
		 * The meta object literal for the '<em><b>Scope</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PERSISTENCE_UNIT__SCOPE = eINSTANCE.getPersistenceUnit_Scope();

		/**
		 * The meta object literal for the '<em><b>Jta Data Source</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PERSISTENCE_UNIT__JTA_DATA_SOURCE = eINSTANCE.getPersistenceUnit_JtaDataSource();

		/**
		 * The meta object literal for the '<em><b>Non Jta Data Source</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PERSISTENCE_UNIT__NON_JTA_DATA_SOURCE = eINSTANCE.getPersistenceUnit_NonJtaDataSource();

		/**
		 * The meta object literal for the '<em><b>Mapping File</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PERSISTENCE_UNIT__MAPPING_FILE = eINSTANCE.getPersistenceUnit_MappingFile();

		/**
		 * The meta object literal for the '<em><b>Jar File</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PERSISTENCE_UNIT__JAR_FILE = eINSTANCE.getPersistenceUnit_JarFile();

		/**
		 * The meta object literal for the '<em><b>Entity Class</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PERSISTENCE_UNIT__ENTITY_CLASS = eINSTANCE.getPersistenceUnit_EntityClass();

		/**
		 * The meta object literal for the '<em><b>Exclude Unlisted Classes</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PERSISTENCE_UNIT__EXCLUDE_UNLISTED_CLASSES = eINSTANCE.getPersistenceUnit_ExcludeUnlistedClasses();

		/**
		 * The meta object literal for the '<em><b>Shared Cache Mode</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PERSISTENCE_UNIT__SHARED_CACHE_MODE = eINSTANCE.getPersistenceUnit_SharedCacheMode();

		/**
		 * The meta object literal for the '<em><b>Validation Mode</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PERSISTENCE_UNIT__VALIDATION_MODE = eINSTANCE.getPersistenceUnit_ValidationMode();

		/**
		 * The meta object literal for the '<em><b>Properties</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PERSISTENCE_UNIT__PROPERTIES = eINSTANCE.getPersistenceUnit_Properties();

		/**
		 * The meta object literal for the '<em><b>Any</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PERSISTENCE_UNIT__ANY = eINSTANCE.getPersistenceUnit_Any();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PERSISTENCE_UNIT__NAME = eINSTANCE.getPersistenceUnit_Name();

		/**
		 * The meta object literal for the '<em><b>Transaction Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PERSISTENCE_UNIT__TRANSACTION_TYPE = eINSTANCE.getPersistenceUnit_TransactionType();

		/**
		 * The meta object literal for the '<em><b>Entity Mappings</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PERSISTENCE_UNIT__ENTITY_MAPPINGS = eINSTANCE.getPersistenceUnit_EntityMappings();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.persistence.epersistence.impl.PropertiesImpl <em>Properties</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.epersistence.impl.PropertiesImpl
		 * @see org.eclipse.fennec.persistence.epersistence.impl.EPersistencePackageImpl#getProperties()
		 * @generated
		 */
		EClass PROPERTIES = eINSTANCE.getProperties();

		/**
		 * The meta object literal for the '<em><b>Property</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROPERTIES__PROPERTY = eINSTANCE.getProperties_Property();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.persistence.epersistence.impl.PropertyImpl <em>Property</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.epersistence.impl.PropertyImpl
		 * @see org.eclipse.fennec.persistence.epersistence.impl.EPersistencePackageImpl#getProperty()
		 * @generated
		 */
		EClass PROPERTY = eINSTANCE.getProperty();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROPERTY__NAME = eINSTANCE.getProperty_Name();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROPERTY__VALUE = eINSTANCE.getProperty_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.persistence.epersistence.impl.PersistenceImpl <em>Persistence</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.epersistence.impl.PersistenceImpl
		 * @see org.eclipse.fennec.persistence.epersistence.impl.EPersistencePackageImpl#getPersistence()
		 * @generated
		 */
		EClass PERSISTENCE = eINSTANCE.getPersistence();

		/**
		 * The meta object literal for the '<em><b>Persistence Unit</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PERSISTENCE__PERSISTENCE_UNIT = eINSTANCE.getPersistence_PersistenceUnit();

		/**
		 * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PERSISTENCE__VERSION = eINSTANCE.getPersistence_Version();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnitValidationMode <em>Persistence Unit Validation Mode</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnitValidationMode
		 * @see org.eclipse.fennec.persistence.epersistence.impl.EPersistencePackageImpl#getPersistenceUnitValidationMode()
		 * @generated
		 */
		EEnum PERSISTENCE_UNIT_VALIDATION_MODE = eINSTANCE.getPersistenceUnitValidationMode();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnitCachingType <em>Persistence Unit Caching Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnitCachingType
		 * @see org.eclipse.fennec.persistence.epersistence.impl.EPersistencePackageImpl#getPersistenceUnitCachingType()
		 * @generated
		 */
		EEnum PERSISTENCE_UNIT_CACHING_TYPE = eINSTANCE.getPersistenceUnitCachingType();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.persistence.epersistence.PersistenceUnitTransactionType <em>Persistence Unit Transaction Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.epersistence.PersistenceUnitTransactionType
		 * @see org.eclipse.fennec.persistence.epersistence.impl.EPersistencePackageImpl#getPersistenceUnitTransactionType()
		 * @generated
		 */
		EEnum PERSISTENCE_UNIT_TRANSACTION_TYPE = eINSTANCE.getPersistenceUnitTransactionType();

		/**
		 * The meta object literal for the '<em>Version</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.lang.String
		 * @see org.eclipse.fennec.persistence.epersistence.impl.EPersistencePackageImpl#getVersion()
		 * @generated
		 */
		EDataType VERSION = eINSTANCE.getVersion();

	}

} //EPersistencePackage
