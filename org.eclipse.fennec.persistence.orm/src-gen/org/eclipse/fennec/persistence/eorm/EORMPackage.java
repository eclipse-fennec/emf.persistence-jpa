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
package org.eclipse.fennec.persistence.eorm;


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
 * @see org.eclipse.fennec.persistence.eorm.EORMFactory
 * @model kind="package"
 * @generated
 */
@ProviderType
@EPackage(uri = EORMPackage.eNS_URI, fingerprint = "fp1:ec55bd4be1f58353a63d143b9056e33c1da5f52cda3cfbc1243f570eeb320d0e", genModel = "/model/epersistence.genmodel", genModelSourceLocations = {"model/epersistence.genmodel","org.eclipse.fennec.persistence.orm/model/epersistence.genmodel"}, ecore = "/model/eorm.ecore", ecoreSourceLocations = "/model/eorm.ecore")
public interface EORMPackage extends org.eclipse.emf.ecore.EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "eorm";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://eclipse.org/fennec/persistence/eorm/1.0.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "eorm";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	EORMPackage eINSTANCE = org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.AssociationOverrideImpl <em>Association Override</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.AssociationOverrideImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getAssociationOverride()
	 * @generated
	 */
	int ASSOCIATION_OVERRIDE = 0;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOCIATION_OVERRIDE__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Join Column</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOCIATION_OVERRIDE__JOIN_COLUMN = 1;

	/**
	 * The feature id for the '<em><b>Foreign Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOCIATION_OVERRIDE__FOREIGN_KEY = 2;

	/**
	 * The feature id for the '<em><b>Join Table</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOCIATION_OVERRIDE__JOIN_TABLE = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOCIATION_OVERRIDE__NAME = 4;

	/**
	 * The number of structural features of the '<em>Association Override</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOCIATION_OVERRIDE_FEATURE_COUNT = 5;

	/**
	 * The number of operations of the '<em>Association Override</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOCIATION_OVERRIDE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.AttributeOverrideImpl <em>Attribute Override</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.AttributeOverrideImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getAttributeOverride()
	 * @generated
	 */
	int ATTRIBUTE_OVERRIDE = 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_OVERRIDE__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Column</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_OVERRIDE__COLUMN = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_OVERRIDE__NAME = 2;

	/**
	 * The number of structural features of the '<em>Attribute Override</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_OVERRIDE_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Attribute Override</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_OVERRIDE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.AttributesImpl <em>Attributes</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.AttributesImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getAttributes()
	 * @generated
	 */
	int ATTRIBUTES = 2;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTES__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTES__ID = 1;

	/**
	 * The feature id for the '<em><b>Embedded Id</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTES__EMBEDDED_ID = 2;

	/**
	 * The feature id for the '<em><b>Basic</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTES__BASIC = 3;

	/**
	 * The feature id for the '<em><b>Version</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTES__VERSION = 4;

	/**
	 * The feature id for the '<em><b>Many To One</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTES__MANY_TO_ONE = 5;

	/**
	 * The feature id for the '<em><b>One To Many</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTES__ONE_TO_MANY = 6;

	/**
	 * The feature id for the '<em><b>One To One</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTES__ONE_TO_ONE = 7;

	/**
	 * The feature id for the '<em><b>Many To Many</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTES__MANY_TO_MANY = 8;

	/**
	 * The feature id for the '<em><b>Element Collection</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTES__ELEMENT_COLLECTION = 9;

	/**
	 * The feature id for the '<em><b>Embedded</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTES__EMBEDDED = 10;

	/**
	 * The feature id for the '<em><b>Transient</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTES__TRANSIENT = 11;

	/**
	 * The number of structural features of the '<em>Attributes</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTES_FEATURE_COUNT = 12;

	/**
	 * The number of operations of the '<em>Attributes</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTES_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.EORMElementImpl <em>Element</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMElementImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getEORMElement()
	 * @generated
	 */
	int EORM_ELEMENT = 79;

	/**
	 * The feature id for the '<em><b>Accessible Object</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EORM_ELEMENT__ACCESSIBLE_OBJECT = 0;

	/**
	 * The number of structural features of the '<em>Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EORM_ELEMENT_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EORM_ELEMENT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.ENamedBaseImpl <em>ENamed Base</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.ENamedBaseImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getENamedBase()
	 * @generated
	 */
	int ENAMED_BASE = 27;

	/**
	 * The feature id for the '<em><b>Accessible Object</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENAMED_BASE__ACCESSIBLE_OBJECT = EORM_ELEMENT__ACCESSIBLE_OBJECT;

	/**
	 * The feature id for the '<em><b>Access</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENAMED_BASE__ACCESS = EORM_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENAMED_BASE__NAME = EORM_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>ENamed Base</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENAMED_BASE_FEATURE_COUNT = EORM_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>ENamed Base</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENAMED_BASE_OPERATION_COUNT = EORM_ELEMENT_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.BaseImpl <em>Base</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.BaseImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getBase()
	 * @generated
	 */
	int BASE = 28;

	/**
	 * The feature id for the '<em><b>Accessible Object</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE__ACCESSIBLE_OBJECT = ENAMED_BASE__ACCESSIBLE_OBJECT;

	/**
	 * The feature id for the '<em><b>Access</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE__ACCESS = ENAMED_BASE__ACCESS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE__NAME = ENAMED_BASE__NAME;

	/**
	 * The feature id for the '<em><b>Column</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE__COLUMN = ENAMED_BASE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Temporal</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE__TEMPORAL = ENAMED_BASE_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Base</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_FEATURE_COUNT = ENAMED_BASE_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Base</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_OPERATION_COUNT = ENAMED_BASE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.SimpleBaseImpl <em>Simple Base</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.SimpleBaseImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getSimpleBase()
	 * @generated
	 */
	int SIMPLE_BASE = 66;

	/**
	 * The feature id for the '<em><b>Accessible Object</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_BASE__ACCESSIBLE_OBJECT = BASE__ACCESSIBLE_OBJECT;

	/**
	 * The feature id for the '<em><b>Access</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_BASE__ACCESS = BASE__ACCESS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_BASE__NAME = BASE__NAME;

	/**
	 * The feature id for the '<em><b>Column</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_BASE__COLUMN = BASE__COLUMN;

	/**
	 * The feature id for the '<em><b>Temporal</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_BASE__TEMPORAL = BASE__TEMPORAL;

	/**
	 * The feature id for the '<em><b>Lob</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_BASE__LOB = BASE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Convert</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_BASE__CONVERT = BASE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Fetch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_BASE__FETCH = BASE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Enumerated</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_BASE__ENUMERATED = BASE_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Simple Base</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_BASE_FEATURE_COUNT = BASE_FEATURE_COUNT + 4;

	/**
	 * The number of operations of the '<em>Simple Base</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_BASE_OPERATION_COUNT = BASE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.BasicImpl <em>Basic</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.BasicImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getBasic()
	 * @generated
	 */
	int BASIC = 3;

	/**
	 * The feature id for the '<em><b>Accessible Object</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASIC__ACCESSIBLE_OBJECT = SIMPLE_BASE__ACCESSIBLE_OBJECT;

	/**
	 * The feature id for the '<em><b>Access</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASIC__ACCESS = SIMPLE_BASE__ACCESS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASIC__NAME = SIMPLE_BASE__NAME;

	/**
	 * The feature id for the '<em><b>Column</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASIC__COLUMN = SIMPLE_BASE__COLUMN;

	/**
	 * The feature id for the '<em><b>Temporal</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASIC__TEMPORAL = SIMPLE_BASE__TEMPORAL;

	/**
	 * The feature id for the '<em><b>Lob</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASIC__LOB = SIMPLE_BASE__LOB;

	/**
	 * The feature id for the '<em><b>Convert</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASIC__CONVERT = SIMPLE_BASE__CONVERT;

	/**
	 * The feature id for the '<em><b>Fetch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASIC__FETCH = SIMPLE_BASE__FETCH;

	/**
	 * The feature id for the '<em><b>Enumerated</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASIC__ENUMERATED = SIMPLE_BASE__ENUMERATED;

	/**
	 * The feature id for the '<em><b>Optional</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASIC__OPTIONAL = SIMPLE_BASE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Basic</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASIC_FEATURE_COUNT = SIMPLE_BASE_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Basic</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASIC_OPERATION_COUNT = SIMPLE_BASE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.CascadeTypeImpl <em>Cascade Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.CascadeTypeImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getCascadeType()
	 * @generated
	 */
	int CASCADE_TYPE = 4;

	/**
	 * The feature id for the '<em><b>Cascade All</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CASCADE_TYPE__CASCADE_ALL = 0;

	/**
	 * The feature id for the '<em><b>Cascade Persist</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CASCADE_TYPE__CASCADE_PERSIST = 1;

	/**
	 * The feature id for the '<em><b>Cascade Merge</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CASCADE_TYPE__CASCADE_MERGE = 2;

	/**
	 * The feature id for the '<em><b>Cascade Remove</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CASCADE_TYPE__CASCADE_REMOVE = 3;

	/**
	 * The feature id for the '<em><b>Cascade Refresh</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CASCADE_TYPE__CASCADE_REFRESH = 4;

	/**
	 * The feature id for the '<em><b>Cascade Detach</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CASCADE_TYPE__CASCADE_DETACH = 5;

	/**
	 * The number of structural features of the '<em>Cascade Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CASCADE_TYPE_FEATURE_COUNT = 6;

	/**
	 * The number of operations of the '<em>Cascade Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CASCADE_TYPE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.TableImpl <em>Table</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.TableImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getTable()
	 * @generated
	 */
	int TABLE = 69;

	/**
	 * The feature id for the '<em><b>Unique Constraint</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE__UNIQUE_CONSTRAINT = 0;

	/**
	 * The feature id for the '<em><b>Index</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE__INDEX = 1;

	/**
	 * The feature id for the '<em><b>Catalog</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE__CATALOG = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE__NAME = 3;

	/**
	 * The feature id for the '<em><b>Schema</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE__SCHEMA = 4;

	/**
	 * The number of structural features of the '<em>Table</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE_FEATURE_COUNT = 5;

	/**
	 * The number of operations of the '<em>Table</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.CollectionTableImpl <em>Collection Table</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.CollectionTableImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getCollectionTable()
	 * @generated
	 */
	int COLLECTION_TABLE = 5;

	/**
	 * The feature id for the '<em><b>Unique Constraint</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLLECTION_TABLE__UNIQUE_CONSTRAINT = TABLE__UNIQUE_CONSTRAINT;

	/**
	 * The feature id for the '<em><b>Index</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLLECTION_TABLE__INDEX = TABLE__INDEX;

	/**
	 * The feature id for the '<em><b>Catalog</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLLECTION_TABLE__CATALOG = TABLE__CATALOG;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLLECTION_TABLE__NAME = TABLE__NAME;

	/**
	 * The feature id for the '<em><b>Schema</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLLECTION_TABLE__SCHEMA = TABLE__SCHEMA;

	/**
	 * The feature id for the '<em><b>Join Column</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLLECTION_TABLE__JOIN_COLUMN = TABLE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Foreign Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLLECTION_TABLE__FOREIGN_KEY = TABLE_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Collection Table</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLLECTION_TABLE_FEATURE_COUNT = TABLE_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Collection Table</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLLECTION_TABLE_OPERATION_COUNT = TABLE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.BaseColumn <em>Base Column</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.BaseColumn
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getBaseColumn()
	 * @generated
	 */
	int BASE_COLUMN = 75;

	/**
	 * The feature id for the '<em><b>Accessible Object</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_COLUMN__ACCESSIBLE_OBJECT = EORM_ELEMENT__ACCESSIBLE_OBJECT;

	/**
	 * The feature id for the '<em><b>Column Definition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_COLUMN__COLUMN_DEFINITION = EORM_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Insertable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_COLUMN__INSERTABLE = EORM_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_COLUMN__NAME = EORM_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Nullable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_COLUMN__NULLABLE = EORM_ELEMENT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Updatable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_COLUMN__UPDATABLE = EORM_ELEMENT_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Base Column</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_COLUMN_FEATURE_COUNT = EORM_ELEMENT_FEATURE_COUNT + 5;

	/**
	 * The number of operations of the '<em>Base Column</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_COLUMN_OPERATION_COUNT = EORM_ELEMENT_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.ColumnImpl <em>Column</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.ColumnImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getColumn()
	 * @generated
	 */
	int COLUMN = 6;

	/**
	 * The feature id for the '<em><b>Accessible Object</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLUMN__ACCESSIBLE_OBJECT = BASE_COLUMN__ACCESSIBLE_OBJECT;

	/**
	 * The feature id for the '<em><b>Column Definition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLUMN__COLUMN_DEFINITION = BASE_COLUMN__COLUMN_DEFINITION;

	/**
	 * The feature id for the '<em><b>Insertable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLUMN__INSERTABLE = BASE_COLUMN__INSERTABLE;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLUMN__NAME = BASE_COLUMN__NAME;

	/**
	 * The feature id for the '<em><b>Nullable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLUMN__NULLABLE = BASE_COLUMN__NULLABLE;

	/**
	 * The feature id for the '<em><b>Updatable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLUMN__UPDATABLE = BASE_COLUMN__UPDATABLE;

	/**
	 * The feature id for the '<em><b>Length</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLUMN__LENGTH = BASE_COLUMN_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Precision</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLUMN__PRECISION = BASE_COLUMN_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Scale</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLUMN__SCALE = BASE_COLUMN_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Table</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLUMN__TABLE = BASE_COLUMN_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Unique</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLUMN__UNIQUE = BASE_COLUMN_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Column</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLUMN_FEATURE_COUNT = BASE_COLUMN_FEATURE_COUNT + 5;

	/**
	 * The number of operations of the '<em>Column</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLUMN_OPERATION_COUNT = BASE_COLUMN_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.ColumnResultImpl <em>Column Result</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.ColumnResultImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getColumnResult()
	 * @generated
	 */
	int COLUMN_RESULT = 7;

	/**
	 * The feature id for the '<em><b>Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLUMN_RESULT__CLASS = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLUMN_RESULT__NAME = 1;

	/**
	 * The number of structural features of the '<em>Column Result</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLUMN_RESULT_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Column Result</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLUMN_RESULT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.ConstructorResultImpl <em>Constructor Result</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.ConstructorResultImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getConstructorResult()
	 * @generated
	 */
	int CONSTRUCTOR_RESULT = 8;

	/**
	 * The feature id for the '<em><b>Column</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONSTRUCTOR_RESULT__COLUMN = 0;

	/**
	 * The feature id for the '<em><b>Target Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONSTRUCTOR_RESULT__TARGET_CLASS = 1;

	/**
	 * The number of structural features of the '<em>Constructor Result</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONSTRUCTOR_RESULT_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Constructor Result</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONSTRUCTOR_RESULT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.ConvertImpl <em>Convert</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.ConvertImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getConvert()
	 * @generated
	 */
	int CONVERT = 9;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONVERT__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Attribute Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONVERT__ATTRIBUTE_NAME = 1;

	/**
	 * The feature id for the '<em><b>Converter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONVERT__CONVERTER = 2;

	/**
	 * The feature id for the '<em><b>Disable Conversion</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONVERT__DISABLE_CONVERSION = 3;

	/**
	 * The number of structural features of the '<em>Convert</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONVERT_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Convert</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONVERT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.ConverterImpl <em>Converter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.ConverterImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getConverter()
	 * @generated
	 */
	int CONVERTER = 10;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONVERTER__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Auto Apply</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONVERTER__AUTO_APPLY = 1;

	/**
	 * The feature id for the '<em><b>Class</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONVERTER__CLASS = 2;

	/**
	 * The number of structural features of the '<em>Converter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONVERTER_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Converter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONVERTER_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.DiscriminatorColumnImpl <em>Discriminator Column</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.DiscriminatorColumnImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getDiscriminatorColumn()
	 * @generated
	 */
	int DISCRIMINATOR_COLUMN = 11;

	/**
	 * The feature id for the '<em><b>Column Definition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISCRIMINATOR_COLUMN__COLUMN_DEFINITION = 0;

	/**
	 * The feature id for the '<em><b>Discriminator Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISCRIMINATOR_COLUMN__DISCRIMINATOR_TYPE = 1;

	/**
	 * The feature id for the '<em><b>Length</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISCRIMINATOR_COLUMN__LENGTH = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISCRIMINATOR_COLUMN__NAME = 3;

	/**
	 * The number of structural features of the '<em>Discriminator Column</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISCRIMINATOR_COLUMN_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Discriminator Column</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISCRIMINATOR_COLUMN_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.DocumentRootImpl <em>Document Root</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.DocumentRootImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getDocumentRoot()
	 * @generated
	 */
	int DOCUMENT_ROOT = 12;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__MIXED = 0;

	/**
	 * The feature id for the '<em><b>XMLNS Prefix Map</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__XMLNS_PREFIX_MAP = 1;

	/**
	 * The feature id for the '<em><b>XSI Schema Location</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__XSI_SCHEMA_LOCATION = 2;

	/**
	 * The feature id for the '<em><b>Entity Mappings</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__ENTITY_MAPPINGS = 3;

	/**
	 * The number of structural features of the '<em>Document Root</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Document Root</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.ElementCollectionImpl <em>Element Collection</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.ElementCollectionImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getElementCollection()
	 * @generated
	 */
	int ELEMENT_COLLECTION = 13;

	/**
	 * The feature id for the '<em><b>Accessible Object</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION__ACCESSIBLE_OBJECT = SIMPLE_BASE__ACCESSIBLE_OBJECT;

	/**
	 * The feature id for the '<em><b>Access</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION__ACCESS = SIMPLE_BASE__ACCESS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION__NAME = SIMPLE_BASE__NAME;

	/**
	 * The feature id for the '<em><b>Column</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION__COLUMN = SIMPLE_BASE__COLUMN;

	/**
	 * The feature id for the '<em><b>Temporal</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION__TEMPORAL = SIMPLE_BASE__TEMPORAL;

	/**
	 * The feature id for the '<em><b>Lob</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION__LOB = SIMPLE_BASE__LOB;

	/**
	 * The feature id for the '<em><b>Convert</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION__CONVERT = SIMPLE_BASE__CONVERT;

	/**
	 * The feature id for the '<em><b>Fetch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION__FETCH = SIMPLE_BASE__FETCH;

	/**
	 * The feature id for the '<em><b>Enumerated</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION__ENUMERATED = SIMPLE_BASE__ENUMERATED;

	/**
	 * The feature id for the '<em><b>Order By</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION__ORDER_BY = SIMPLE_BASE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Order Column</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION__ORDER_COLUMN = SIMPLE_BASE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Map Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION__MAP_KEY = SIMPLE_BASE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Map Key Class</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION__MAP_KEY_CLASS = SIMPLE_BASE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Map Key Temporal</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION__MAP_KEY_TEMPORAL = SIMPLE_BASE_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Map Key Enumerated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION__MAP_KEY_ENUMERATED = SIMPLE_BASE_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Map Key Attribute Override</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION__MAP_KEY_ATTRIBUTE_OVERRIDE = SIMPLE_BASE_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Map Key Convert</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION__MAP_KEY_CONVERT = SIMPLE_BASE_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Map Key Column</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION__MAP_KEY_COLUMN = SIMPLE_BASE_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Map Key Join Column</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION__MAP_KEY_JOIN_COLUMN = SIMPLE_BASE_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Map Key Foreign Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION__MAP_KEY_FOREIGN_KEY = SIMPLE_BASE_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>Attribute Override</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION__ATTRIBUTE_OVERRIDE = SIMPLE_BASE_FEATURE_COUNT + 11;

	/**
	 * The feature id for the '<em><b>Association Override</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION__ASSOCIATION_OVERRIDE = SIMPLE_BASE_FEATURE_COUNT + 12;

	/**
	 * The feature id for the '<em><b>Collection Table</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION__COLLECTION_TABLE = SIMPLE_BASE_FEATURE_COUNT + 13;

	/**
	 * The feature id for the '<em><b>Target Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION__TARGET_CLASS = SIMPLE_BASE_FEATURE_COUNT + 14;

	/**
	 * The number of structural features of the '<em>Element Collection</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION_FEATURE_COUNT = SIMPLE_BASE_FEATURE_COUNT + 15;

	/**
	 * The number of operations of the '<em>Element Collection</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_COLLECTION_OPERATION_COUNT = SIMPLE_BASE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.EmbeddableImpl <em>Embeddable</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.EmbeddableImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getEmbeddable()
	 * @generated
	 */
	int EMBEDDABLE = 14;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDABLE__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDABLE__ATTRIBUTES = 1;

	/**
	 * The feature id for the '<em><b>Access</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDABLE__ACCESS = 2;

	/**
	 * The feature id for the '<em><b>Class</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDABLE__CLASS = 3;

	/**
	 * The feature id for the '<em><b>Metadata Complete</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDABLE__METADATA_COMPLETE = 4;

	/**
	 * The number of structural features of the '<em>Embeddable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDABLE_FEATURE_COUNT = 5;

	/**
	 * The number of operations of the '<em>Embeddable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDABLE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.EmbeddableAttributesImpl <em>Embeddable Attributes</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.EmbeddableAttributesImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getEmbeddableAttributes()
	 * @generated
	 */
	int EMBEDDABLE_ATTRIBUTES = 15;

	/**
	 * The feature id for the '<em><b>Basic</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDABLE_ATTRIBUTES__BASIC = 0;

	/**
	 * The feature id for the '<em><b>Many To One</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDABLE_ATTRIBUTES__MANY_TO_ONE = 1;

	/**
	 * The feature id for the '<em><b>One To Many</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDABLE_ATTRIBUTES__ONE_TO_MANY = 2;

	/**
	 * The feature id for the '<em><b>One To One</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDABLE_ATTRIBUTES__ONE_TO_ONE = 3;

	/**
	 * The feature id for the '<em><b>Many To Many</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDABLE_ATTRIBUTES__MANY_TO_MANY = 4;

	/**
	 * The feature id for the '<em><b>Element Collection</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDABLE_ATTRIBUTES__ELEMENT_COLLECTION = 5;

	/**
	 * The feature id for the '<em><b>Embedded</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDABLE_ATTRIBUTES__EMBEDDED = 6;

	/**
	 * The feature id for the '<em><b>Transient</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDABLE_ATTRIBUTES__TRANSIENT = 7;

	/**
	 * The number of structural features of the '<em>Embeddable Attributes</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDABLE_ATTRIBUTES_FEATURE_COUNT = 8;

	/**
	 * The number of operations of the '<em>Embeddable Attributes</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDABLE_ATTRIBUTES_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.EmbeddedImpl <em>Embedded</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.EmbeddedImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getEmbedded()
	 * @generated
	 */
	int EMBEDDED = 16;

	/**
	 * The feature id for the '<em><b>Attribute Override</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDED__ATTRIBUTE_OVERRIDE = 0;

	/**
	 * The feature id for the '<em><b>Association Override</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDED__ASSOCIATION_OVERRIDE = 1;

	/**
	 * The feature id for the '<em><b>Convert</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDED__CONVERT = 2;

	/**
	 * The feature id for the '<em><b>Access</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDED__ACCESS = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDED__NAME = 4;

	/**
	 * The number of structural features of the '<em>Embedded</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDED_FEATURE_COUNT = 5;

	/**
	 * The number of operations of the '<em>Embedded</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDED_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.EmbeddedIdImpl <em>Embedded Id</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.EmbeddedIdImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getEmbeddedId()
	 * @generated
	 */
	int EMBEDDED_ID = 17;

	/**
	 * The feature id for the '<em><b>Attribute Override</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDED_ID__ATTRIBUTE_OVERRIDE = 0;

	/**
	 * The feature id for the '<em><b>Access</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDED_ID__ACCESS = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDED_ID__NAME = 2;

	/**
	 * The number of structural features of the '<em>Embedded Id</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDED_ID_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Embedded Id</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMBEDDED_ID_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.EmptyTypeImpl <em>Empty Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.EmptyTypeImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getEmptyType()
	 * @generated
	 */
	int EMPTY_TYPE = 18;

	/**
	 * The number of structural features of the '<em>Empty Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMPTY_TYPE_FEATURE_COUNT = 0;

	/**
	 * The number of operations of the '<em>Empty Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMPTY_TYPE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.EntityImpl <em>Entity</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.EntityImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getEntity()
	 * @generated
	 */
	int ENTITY = 19;

	/**
	 * The feature id for the '<em><b>Accessible Object</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__ACCESSIBLE_OBJECT = EORM_ELEMENT__ACCESSIBLE_OBJECT;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__DESCRIPTION = EORM_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Table</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__TABLE = EORM_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Secondary Table</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__SECONDARY_TABLE = EORM_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Primary Key Join Column</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__PRIMARY_KEY_JOIN_COLUMN = EORM_ELEMENT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Primary Key Foreign Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__PRIMARY_KEY_FOREIGN_KEY = EORM_ELEMENT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Id Class</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__ID_CLASS = EORM_ELEMENT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Inheritance</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__INHERITANCE = EORM_ELEMENT_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Discriminator Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__DISCRIMINATOR_VALUE = EORM_ELEMENT_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Discriminator Column</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__DISCRIMINATOR_COLUMN = EORM_ELEMENT_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Sequence Generator</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__SEQUENCE_GENERATOR = EORM_ELEMENT_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Table Generator</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__TABLE_GENERATOR = EORM_ELEMENT_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>Named Query</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__NAMED_QUERY = EORM_ELEMENT_FEATURE_COUNT + 11;

	/**
	 * The feature id for the '<em><b>Named Native Query</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__NAMED_NATIVE_QUERY = EORM_ELEMENT_FEATURE_COUNT + 12;

	/**
	 * The feature id for the '<em><b>Named Stored Procedure Query</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__NAMED_STORED_PROCEDURE_QUERY = EORM_ELEMENT_FEATURE_COUNT + 13;

	/**
	 * The feature id for the '<em><b>Sql Result Set Mapping</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__SQL_RESULT_SET_MAPPING = EORM_ELEMENT_FEATURE_COUNT + 14;

	/**
	 * The feature id for the '<em><b>Exclude Default Listeners</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__EXCLUDE_DEFAULT_LISTENERS = EORM_ELEMENT_FEATURE_COUNT + 15;

	/**
	 * The feature id for the '<em><b>Exclude Superclass Listeners</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__EXCLUDE_SUPERCLASS_LISTENERS = EORM_ELEMENT_FEATURE_COUNT + 16;

	/**
	 * The feature id for the '<em><b>Entity Listeners</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__ENTITY_LISTENERS = EORM_ELEMENT_FEATURE_COUNT + 17;

	/**
	 * The feature id for the '<em><b>Pre Persist</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__PRE_PERSIST = EORM_ELEMENT_FEATURE_COUNT + 18;

	/**
	 * The feature id for the '<em><b>Post Persist</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__POST_PERSIST = EORM_ELEMENT_FEATURE_COUNT + 19;

	/**
	 * The feature id for the '<em><b>Pre Remove</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__PRE_REMOVE = EORM_ELEMENT_FEATURE_COUNT + 20;

	/**
	 * The feature id for the '<em><b>Post Remove</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__POST_REMOVE = EORM_ELEMENT_FEATURE_COUNT + 21;

	/**
	 * The feature id for the '<em><b>Pre Update</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__PRE_UPDATE = EORM_ELEMENT_FEATURE_COUNT + 22;

	/**
	 * The feature id for the '<em><b>Post Update</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__POST_UPDATE = EORM_ELEMENT_FEATURE_COUNT + 23;

	/**
	 * The feature id for the '<em><b>Post Load</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__POST_LOAD = EORM_ELEMENT_FEATURE_COUNT + 24;

	/**
	 * The feature id for the '<em><b>Attribute Override</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__ATTRIBUTE_OVERRIDE = EORM_ELEMENT_FEATURE_COUNT + 25;

	/**
	 * The feature id for the '<em><b>Association Override</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__ASSOCIATION_OVERRIDE = EORM_ELEMENT_FEATURE_COUNT + 26;

	/**
	 * The feature id for the '<em><b>Convert</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__CONVERT = EORM_ELEMENT_FEATURE_COUNT + 27;

	/**
	 * The feature id for the '<em><b>Named Entity Graph</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__NAMED_ENTITY_GRAPH = EORM_ELEMENT_FEATURE_COUNT + 28;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__ATTRIBUTES = EORM_ELEMENT_FEATURE_COUNT + 29;

	/**
	 * The feature id for the '<em><b>Access</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__ACCESS = EORM_ELEMENT_FEATURE_COUNT + 30;

	/**
	 * The feature id for the '<em><b>Cacheable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__CACHEABLE = EORM_ELEMENT_FEATURE_COUNT + 31;

	/**
	 * The feature id for the '<em><b>Metadata Complete</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__METADATA_COMPLETE = EORM_ELEMENT_FEATURE_COUNT + 32;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__NAME = EORM_ELEMENT_FEATURE_COUNT + 33;

	/**
	 * The feature id for the '<em><b>Class</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY__CLASS = EORM_ELEMENT_FEATURE_COUNT + 34;

	/**
	 * The number of structural features of the '<em>Entity</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_FEATURE_COUNT = EORM_ELEMENT_FEATURE_COUNT + 35;

	/**
	 * The number of operations of the '<em>Entity</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_OPERATION_COUNT = EORM_ELEMENT_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.EntityListenerImpl <em>Entity Listener</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.EntityListenerImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getEntityListener()
	 * @generated
	 */
	int ENTITY_LISTENER = 20;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_LISTENER__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Pre Persist</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_LISTENER__PRE_PERSIST = 1;

	/**
	 * The feature id for the '<em><b>Post Persist</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_LISTENER__POST_PERSIST = 2;

	/**
	 * The feature id for the '<em><b>Pre Remove</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_LISTENER__PRE_REMOVE = 3;

	/**
	 * The feature id for the '<em><b>Post Remove</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_LISTENER__POST_REMOVE = 4;

	/**
	 * The feature id for the '<em><b>Pre Update</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_LISTENER__PRE_UPDATE = 5;

	/**
	 * The feature id for the '<em><b>Post Update</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_LISTENER__POST_UPDATE = 6;

	/**
	 * The feature id for the '<em><b>Post Load</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_LISTENER__POST_LOAD = 7;

	/**
	 * The feature id for the '<em><b>Class</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_LISTENER__CLASS = 8;

	/**
	 * The number of structural features of the '<em>Entity Listener</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_LISTENER_FEATURE_COUNT = 9;

	/**
	 * The number of operations of the '<em>Entity Listener</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_LISTENER_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.EntityListenersImpl <em>Entity Listeners</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.EntityListenersImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getEntityListeners()
	 * @generated
	 */
	int ENTITY_LISTENERS = 21;

	/**
	 * The feature id for the '<em><b>Entity Listener</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_LISTENERS__ENTITY_LISTENER = 0;

	/**
	 * The number of structural features of the '<em>Entity Listeners</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_LISTENERS_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Entity Listeners</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_LISTENERS_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.EntityMappingsImpl <em>Entity Mappings</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.EntityMappingsImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getEntityMappings()
	 * @generated
	 */
	int ENTITY_MAPPINGS = 22;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_MAPPINGS__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Persistence Unit Metadata</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_MAPPINGS__PERSISTENCE_UNIT_METADATA = 1;

	/**
	 * The feature id for the '<em><b>Package</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_MAPPINGS__PACKAGE = 2;

	/**
	 * The feature id for the '<em><b>Schema</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_MAPPINGS__SCHEMA = 3;

	/**
	 * The feature id for the '<em><b>Catalog</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_MAPPINGS__CATALOG = 4;

	/**
	 * The feature id for the '<em><b>Access</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_MAPPINGS__ACCESS = 5;

	/**
	 * The feature id for the '<em><b>Sequence Generator</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_MAPPINGS__SEQUENCE_GENERATOR = 6;

	/**
	 * The feature id for the '<em><b>Table Generator</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_MAPPINGS__TABLE_GENERATOR = 7;

	/**
	 * The feature id for the '<em><b>Named Query</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_MAPPINGS__NAMED_QUERY = 8;

	/**
	 * The feature id for the '<em><b>Named Native Query</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_MAPPINGS__NAMED_NATIVE_QUERY = 9;

	/**
	 * The feature id for the '<em><b>Named Stored Procedure Query</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_MAPPINGS__NAMED_STORED_PROCEDURE_QUERY = 10;

	/**
	 * The feature id for the '<em><b>Sql Result Set Mapping</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_MAPPINGS__SQL_RESULT_SET_MAPPING = 11;

	/**
	 * The feature id for the '<em><b>Mapped Superclass</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_MAPPINGS__MAPPED_SUPERCLASS = 12;

	/**
	 * The feature id for the '<em><b>Entity</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_MAPPINGS__ENTITY = 13;

	/**
	 * The feature id for the '<em><b>Embeddable</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_MAPPINGS__EMBEDDABLE = 14;

	/**
	 * The feature id for the '<em><b>Converter</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_MAPPINGS__CONVERTER = 15;

	/**
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_MAPPINGS__VERSION = 16;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_MAPPINGS__NAME = 17;

	/**
	 * The number of structural features of the '<em>Entity Mappings</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_MAPPINGS_FEATURE_COUNT = 18;

	/**
	 * The number of operations of the '<em>Entity Mappings</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_MAPPINGS_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.EntityResultImpl <em>Entity Result</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.EntityResultImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getEntityResult()
	 * @generated
	 */
	int ENTITY_RESULT = 23;

	/**
	 * The feature id for the '<em><b>Field Result</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_RESULT__FIELD_RESULT = 0;

	/**
	 * The feature id for the '<em><b>Discriminator Column</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_RESULT__DISCRIMINATOR_COLUMN = 1;

	/**
	 * The feature id for the '<em><b>Entity Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_RESULT__ENTITY_CLASS = 2;

	/**
	 * The number of structural features of the '<em>Entity Result</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_RESULT_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Entity Result</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENTITY_RESULT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.FieldResultImpl <em>Field Result</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.FieldResultImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getFieldResult()
	 * @generated
	 */
	int FIELD_RESULT = 24;

	/**
	 * The feature id for the '<em><b>Column</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FIELD_RESULT__COLUMN = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FIELD_RESULT__NAME = 1;

	/**
	 * The number of structural features of the '<em>Field Result</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FIELD_RESULT_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Field Result</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FIELD_RESULT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.ForeignKeyImpl <em>Foreign Key</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.ForeignKeyImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getForeignKey()
	 * @generated
	 */
	int FOREIGN_KEY = 25;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FOREIGN_KEY__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Constraint Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FOREIGN_KEY__CONSTRAINT_MODE = 1;

	/**
	 * The feature id for the '<em><b>Foreign Key Definition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FOREIGN_KEY__FOREIGN_KEY_DEFINITION = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FOREIGN_KEY__NAME = 3;

	/**
	 * The number of structural features of the '<em>Foreign Key</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FOREIGN_KEY_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Foreign Key</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FOREIGN_KEY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.GeneratedValueImpl <em>Generated Value</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.GeneratedValueImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getGeneratedValue()
	 * @generated
	 */
	int GENERATED_VALUE = 26;

	/**
	 * The feature id for the '<em><b>Generator</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERATED_VALUE__GENERATOR = 0;

	/**
	 * The feature id for the '<em><b>Strategy</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERATED_VALUE__STRATEGY = 1;

	/**
	 * The number of structural features of the '<em>Generated Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERATED_VALUE_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Generated Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERATED_VALUE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.BaseRefImpl <em>Base Ref</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.BaseRefImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getBaseRef()
	 * @generated
	 */
	int BASE_REF = 29;

	/**
	 * The feature id for the '<em><b>Accessible Object</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_REF__ACCESSIBLE_OBJECT = ENAMED_BASE__ACCESSIBLE_OBJECT;

	/**
	 * The feature id for the '<em><b>Access</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_REF__ACCESS = ENAMED_BASE__ACCESS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_REF__NAME = ENAMED_BASE__NAME;

	/**
	 * The feature id for the '<em><b>Cascade</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_REF__CASCADE = ENAMED_BASE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Fetch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_REF__FETCH = ENAMED_BASE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Foreign Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_REF__FOREIGN_KEY = ENAMED_BASE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Join Table</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_REF__JOIN_TABLE = ENAMED_BASE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Optional</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_REF__OPTIONAL = ENAMED_BASE_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Batch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_REF__BATCH = ENAMED_BASE_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>Base Ref</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_REF_FEATURE_COUNT = ENAMED_BASE_FEATURE_COUNT + 6;

	/**
	 * The number of operations of the '<em>Base Ref</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_REF_OPERATION_COUNT = ENAMED_BASE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.IdImpl <em>Id</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.IdImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getId()
	 * @generated
	 */
	int ID = 30;

	/**
	 * The feature id for the '<em><b>Accessible Object</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID__ACCESSIBLE_OBJECT = BASE__ACCESSIBLE_OBJECT;

	/**
	 * The feature id for the '<em><b>Access</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID__ACCESS = BASE__ACCESS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID__NAME = BASE__NAME;

	/**
	 * The feature id for the '<em><b>Column</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID__COLUMN = BASE__COLUMN;

	/**
	 * The feature id for the '<em><b>Temporal</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID__TEMPORAL = BASE__TEMPORAL;

	/**
	 * The feature id for the '<em><b>Generated Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID__GENERATED_VALUE = BASE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Table Generator</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID__TABLE_GENERATOR = BASE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Sequence Generator</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID__SEQUENCE_GENERATOR = BASE_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Id</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_FEATURE_COUNT = BASE_FEATURE_COUNT + 3;

	/**
	 * The number of operations of the '<em>Id</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_OPERATION_COUNT = BASE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.IdClassImpl <em>Id Class</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.IdClassImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getIdClass()
	 * @generated
	 */
	int ID_CLASS = 31;

	/**
	 * The feature id for the '<em><b>Class</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_CLASS__CLASS = 0;

	/**
	 * The number of structural features of the '<em>Id Class</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_CLASS_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Id Class</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_CLASS_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.IndexImpl <em>Index</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.IndexImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getIndex()
	 * @generated
	 */
	int INDEX = 32;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INDEX__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Column List</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INDEX__COLUMN_LIST = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INDEX__NAME = 2;

	/**
	 * The feature id for the '<em><b>Unique</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INDEX__UNIQUE = 3;

	/**
	 * The number of structural features of the '<em>Index</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INDEX_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Index</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INDEX_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.InheritanceImpl <em>Inheritance</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.InheritanceImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getInheritance()
	 * @generated
	 */
	int INHERITANCE = 33;

	/**
	 * The feature id for the '<em><b>Strategy</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INHERITANCE__STRATEGY = 0;

	/**
	 * The number of structural features of the '<em>Inheritance</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INHERITANCE_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Inheritance</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INHERITANCE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.JoinColumnImpl <em>Join Column</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.JoinColumnImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getJoinColumn()
	 * @generated
	 */
	int JOIN_COLUMN = 34;

	/**
	 * The feature id for the '<em><b>Column Definition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_COLUMN__COLUMN_DEFINITION = 0;

	/**
	 * The feature id for the '<em><b>Insertable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_COLUMN__INSERTABLE = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_COLUMN__NAME = 2;

	/**
	 * The feature id for the '<em><b>Nullable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_COLUMN__NULLABLE = 3;

	/**
	 * The feature id for the '<em><b>Referenced Column Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_COLUMN__REFERENCED_COLUMN_NAME = 4;

	/**
	 * The feature id for the '<em><b>Table</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_COLUMN__TABLE = 5;

	/**
	 * The feature id for the '<em><b>Unique</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_COLUMN__UNIQUE = 6;

	/**
	 * The feature id for the '<em><b>Updatable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_COLUMN__UPDATABLE = 7;

	/**
	 * The number of structural features of the '<em>Join Column</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_COLUMN_FEATURE_COUNT = 8;

	/**
	 * The number of operations of the '<em>Join Column</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_COLUMN_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.JoinTableImpl <em>Join Table</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.JoinTableImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getJoinTable()
	 * @generated
	 */
	int JOIN_TABLE = 35;

	/**
	 * The feature id for the '<em><b>Join Column</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE__JOIN_COLUMN = 0;

	/**
	 * The feature id for the '<em><b>Foreign Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE__FOREIGN_KEY = 1;

	/**
	 * The feature id for the '<em><b>Inverse Join Column</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE__INVERSE_JOIN_COLUMN = 2;

	/**
	 * The feature id for the '<em><b>Inverse Foreign Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE__INVERSE_FOREIGN_KEY = 3;

	/**
	 * The feature id for the '<em><b>Unique Constraint</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE__UNIQUE_CONSTRAINT = 4;

	/**
	 * The feature id for the '<em><b>Index</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE__INDEX = 5;

	/**
	 * The feature id for the '<em><b>Catalog</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE__CATALOG = 6;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE__NAME = 7;

	/**
	 * The feature id for the '<em><b>Schema</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE__SCHEMA = 8;

	/**
	 * The number of structural features of the '<em>Join Table</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE_FEATURE_COUNT = 9;

	/**
	 * The number of operations of the '<em>Join Table</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.LobImpl <em>Lob</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.LobImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getLob()
	 * @generated
	 */
	int LOB = 36;

	/**
	 * The number of structural features of the '<em>Lob</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOB_FEATURE_COUNT = 0;

	/**
	 * The number of operations of the '<em>Lob</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOB_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.MappedByRef <em>Mapped By Ref</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.MappedByRef
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getMappedByRef()
	 * @generated
	 */
	int MAPPED_BY_REF = 74;

	/**
	 * The feature id for the '<em><b>Accessible Object</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_BY_REF__ACCESSIBLE_OBJECT = BASE_REF__ACCESSIBLE_OBJECT;

	/**
	 * The feature id for the '<em><b>Access</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_BY_REF__ACCESS = BASE_REF__ACCESS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_BY_REF__NAME = BASE_REF__NAME;

	/**
	 * The feature id for the '<em><b>Cascade</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_BY_REF__CASCADE = BASE_REF__CASCADE;

	/**
	 * The feature id for the '<em><b>Fetch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_BY_REF__FETCH = BASE_REF__FETCH;

	/**
	 * The feature id for the '<em><b>Foreign Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_BY_REF__FOREIGN_KEY = BASE_REF__FOREIGN_KEY;

	/**
	 * The feature id for the '<em><b>Join Table</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_BY_REF__JOIN_TABLE = BASE_REF__JOIN_TABLE;

	/**
	 * The feature id for the '<em><b>Optional</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_BY_REF__OPTIONAL = BASE_REF__OPTIONAL;

	/**
	 * The feature id for the '<em><b>Batch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_BY_REF__BATCH = BASE_REF__BATCH;

	/**
	 * The feature id for the '<em><b>Mapped By</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_BY_REF__MAPPED_BY = BASE_REF_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Mapped By Ref</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_BY_REF_FEATURE_COUNT = BASE_REF_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Mapped By Ref</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_BY_REF_OPERATION_COUNT = BASE_REF_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.ManyToManyImpl <em>Many To Many</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.ManyToManyImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getManyToMany()
	 * @generated
	 */
	int MANY_TO_MANY = 37;

	/**
	 * The feature id for the '<em><b>Accessible Object</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_MANY__ACCESSIBLE_OBJECT = MAPPED_BY_REF__ACCESSIBLE_OBJECT;

	/**
	 * The feature id for the '<em><b>Access</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_MANY__ACCESS = MAPPED_BY_REF__ACCESS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_MANY__NAME = MAPPED_BY_REF__NAME;

	/**
	 * The feature id for the '<em><b>Cascade</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_MANY__CASCADE = MAPPED_BY_REF__CASCADE;

	/**
	 * The feature id for the '<em><b>Fetch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_MANY__FETCH = MAPPED_BY_REF__FETCH;

	/**
	 * The feature id for the '<em><b>Foreign Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_MANY__FOREIGN_KEY = MAPPED_BY_REF__FOREIGN_KEY;

	/**
	 * The feature id for the '<em><b>Join Table</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_MANY__JOIN_TABLE = MAPPED_BY_REF__JOIN_TABLE;

	/**
	 * The feature id for the '<em><b>Optional</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_MANY__OPTIONAL = MAPPED_BY_REF__OPTIONAL;

	/**
	 * The feature id for the '<em><b>Batch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_MANY__BATCH = MAPPED_BY_REF__BATCH;

	/**
	 * The feature id for the '<em><b>Mapped By</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_MANY__MAPPED_BY = MAPPED_BY_REF__MAPPED_BY;

	/**
	 * The feature id for the '<em><b>Order By</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_MANY__ORDER_BY = MAPPED_BY_REF_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Order Column</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_MANY__ORDER_COLUMN = MAPPED_BY_REF_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Map Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_MANY__MAP_KEY = MAPPED_BY_REF_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Map Key Class</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_MANY__MAP_KEY_CLASS = MAPPED_BY_REF_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Map Key Temporal</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_MANY__MAP_KEY_TEMPORAL = MAPPED_BY_REF_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Map Key Enumerated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_MANY__MAP_KEY_ENUMERATED = MAPPED_BY_REF_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Map Key Attribute Override</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_MANY__MAP_KEY_ATTRIBUTE_OVERRIDE = MAPPED_BY_REF_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Map Key Convert</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_MANY__MAP_KEY_CONVERT = MAPPED_BY_REF_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Map Key Column</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_MANY__MAP_KEY_COLUMN = MAPPED_BY_REF_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Map Key Join Column</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_MANY__MAP_KEY_JOIN_COLUMN = MAPPED_BY_REF_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Map Key Foreign Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_MANY__MAP_KEY_FOREIGN_KEY = MAPPED_BY_REF_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>Target Entity</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_MANY__TARGET_ENTITY = MAPPED_BY_REF_FEATURE_COUNT + 11;

	/**
	 * The number of structural features of the '<em>Many To Many</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_MANY_FEATURE_COUNT = MAPPED_BY_REF_FEATURE_COUNT + 12;

	/**
	 * The number of operations of the '<em>Many To Many</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_MANY_OPERATION_COUNT = MAPPED_BY_REF_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.ManyToOneImpl <em>Many To One</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.ManyToOneImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getManyToOne()
	 * @generated
	 */
	int MANY_TO_ONE = 38;

	/**
	 * The feature id for the '<em><b>Accessible Object</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_ONE__ACCESSIBLE_OBJECT = BASE_REF__ACCESSIBLE_OBJECT;

	/**
	 * The feature id for the '<em><b>Access</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_ONE__ACCESS = BASE_REF__ACCESS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_ONE__NAME = BASE_REF__NAME;

	/**
	 * The feature id for the '<em><b>Cascade</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_ONE__CASCADE = BASE_REF__CASCADE;

	/**
	 * The feature id for the '<em><b>Fetch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_ONE__FETCH = BASE_REF__FETCH;

	/**
	 * The feature id for the '<em><b>Foreign Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_ONE__FOREIGN_KEY = BASE_REF__FOREIGN_KEY;

	/**
	 * The feature id for the '<em><b>Join Table</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_ONE__JOIN_TABLE = BASE_REF__JOIN_TABLE;

	/**
	 * The feature id for the '<em><b>Optional</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_ONE__OPTIONAL = BASE_REF__OPTIONAL;

	/**
	 * The feature id for the '<em><b>Batch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_ONE__BATCH = BASE_REF__BATCH;

	/**
	 * The feature id for the '<em><b>Join Column</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_ONE__JOIN_COLUMN = BASE_REF_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_ONE__ID = BASE_REF_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Maps Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_ONE__MAPS_ID = BASE_REF_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Target Entity</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_ONE__TARGET_ENTITY = BASE_REF_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Many To One</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_ONE_FEATURE_COUNT = BASE_REF_FEATURE_COUNT + 4;

	/**
	 * The number of operations of the '<em>Many To One</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANY_TO_ONE_OPERATION_COUNT = BASE_REF_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.MapKeyImpl <em>Map Key</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.MapKeyImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getMapKey()
	 * @generated
	 */
	int MAP_KEY = 39;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY__NAME = 0;

	/**
	 * The number of structural features of the '<em>Map Key</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Map Key</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.MapKeyClassImpl <em>Map Key Class</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.MapKeyClassImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getMapKeyClass()
	 * @generated
	 */
	int MAP_KEY_CLASS = 40;

	/**
	 * The feature id for the '<em><b>Class</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_CLASS__CLASS = 0;

	/**
	 * The number of structural features of the '<em>Map Key Class</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_CLASS_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Map Key Class</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_CLASS_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.MapKeyColumnImpl <em>Map Key Column</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.MapKeyColumnImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getMapKeyColumn()
	 * @generated
	 */
	int MAP_KEY_COLUMN = 41;

	/**
	 * The feature id for the '<em><b>Column Definition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_COLUMN__COLUMN_DEFINITION = 0;

	/**
	 * The feature id for the '<em><b>Insertable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_COLUMN__INSERTABLE = 1;

	/**
	 * The feature id for the '<em><b>Length</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_COLUMN__LENGTH = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_COLUMN__NAME = 3;

	/**
	 * The feature id for the '<em><b>Nullable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_COLUMN__NULLABLE = 4;

	/**
	 * The feature id for the '<em><b>Precision</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_COLUMN__PRECISION = 5;

	/**
	 * The feature id for the '<em><b>Scale</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_COLUMN__SCALE = 6;

	/**
	 * The feature id for the '<em><b>Table</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_COLUMN__TABLE = 7;

	/**
	 * The feature id for the '<em><b>Unique</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_COLUMN__UNIQUE = 8;

	/**
	 * The feature id for the '<em><b>Updatable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_COLUMN__UPDATABLE = 9;

	/**
	 * The number of structural features of the '<em>Map Key Column</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_COLUMN_FEATURE_COUNT = 10;

	/**
	 * The number of operations of the '<em>Map Key Column</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_COLUMN_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.MapKeyJoinColumnImpl <em>Map Key Join Column</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.MapKeyJoinColumnImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getMapKeyJoinColumn()
	 * @generated
	 */
	int MAP_KEY_JOIN_COLUMN = 42;

	/**
	 * The feature id for the '<em><b>Column Definition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_JOIN_COLUMN__COLUMN_DEFINITION = 0;

	/**
	 * The feature id for the '<em><b>Insertable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_JOIN_COLUMN__INSERTABLE = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_JOIN_COLUMN__NAME = 2;

	/**
	 * The feature id for the '<em><b>Nullable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_JOIN_COLUMN__NULLABLE = 3;

	/**
	 * The feature id for the '<em><b>Referenced Column Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_JOIN_COLUMN__REFERENCED_COLUMN_NAME = 4;

	/**
	 * The feature id for the '<em><b>Table</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_JOIN_COLUMN__TABLE = 5;

	/**
	 * The feature id for the '<em><b>Unique</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_JOIN_COLUMN__UNIQUE = 6;

	/**
	 * The feature id for the '<em><b>Updatable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_JOIN_COLUMN__UPDATABLE = 7;

	/**
	 * The number of structural features of the '<em>Map Key Join Column</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_JOIN_COLUMN_FEATURE_COUNT = 8;

	/**
	 * The number of operations of the '<em>Map Key Join Column</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAP_KEY_JOIN_COLUMN_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.MappedSuperclassImpl <em>Mapped Superclass</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.MappedSuperclassImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getMappedSuperclass()
	 * @generated
	 */
	int MAPPED_SUPERCLASS = 43;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_SUPERCLASS__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Id Class</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_SUPERCLASS__ID_CLASS = 1;

	/**
	 * The feature id for the '<em><b>Exclude Default Listeners</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_SUPERCLASS__EXCLUDE_DEFAULT_LISTENERS = 2;

	/**
	 * The feature id for the '<em><b>Exclude Superclass Listeners</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_SUPERCLASS__EXCLUDE_SUPERCLASS_LISTENERS = 3;

	/**
	 * The feature id for the '<em><b>Entity Listeners</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_SUPERCLASS__ENTITY_LISTENERS = 4;

	/**
	 * The feature id for the '<em><b>Pre Persist</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_SUPERCLASS__PRE_PERSIST = 5;

	/**
	 * The feature id for the '<em><b>Post Persist</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_SUPERCLASS__POST_PERSIST = 6;

	/**
	 * The feature id for the '<em><b>Pre Remove</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_SUPERCLASS__PRE_REMOVE = 7;

	/**
	 * The feature id for the '<em><b>Post Remove</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_SUPERCLASS__POST_REMOVE = 8;

	/**
	 * The feature id for the '<em><b>Pre Update</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_SUPERCLASS__PRE_UPDATE = 9;

	/**
	 * The feature id for the '<em><b>Post Update</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_SUPERCLASS__POST_UPDATE = 10;

	/**
	 * The feature id for the '<em><b>Post Load</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_SUPERCLASS__POST_LOAD = 11;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_SUPERCLASS__ATTRIBUTES = 12;

	/**
	 * The feature id for the '<em><b>Access</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_SUPERCLASS__ACCESS = 13;

	/**
	 * The feature id for the '<em><b>Class</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_SUPERCLASS__CLASS = 14;

	/**
	 * The feature id for the '<em><b>Metadata Complete</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_SUPERCLASS__METADATA_COMPLETE = 15;

	/**
	 * The number of structural features of the '<em>Mapped Superclass</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_SUPERCLASS_FEATURE_COUNT = 16;

	/**
	 * The number of operations of the '<em>Mapped Superclass</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_SUPERCLASS_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.NamedAttributeNodeImpl <em>Named Attribute Node</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.NamedAttributeNodeImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getNamedAttributeNode()
	 * @generated
	 */
	int NAMED_ATTRIBUTE_NODE = 44;

	/**
	 * The feature id for the '<em><b>Key Subgraph</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_ATTRIBUTE_NODE__KEY_SUBGRAPH = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_ATTRIBUTE_NODE__NAME = 1;

	/**
	 * The feature id for the '<em><b>Subgraph</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_ATTRIBUTE_NODE__SUBGRAPH = 2;

	/**
	 * The number of structural features of the '<em>Named Attribute Node</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_ATTRIBUTE_NODE_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Named Attribute Node</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_ATTRIBUTE_NODE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.NamedEntityGraphImpl <em>Named Entity Graph</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.NamedEntityGraphImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getNamedEntityGraph()
	 * @generated
	 */
	int NAMED_ENTITY_GRAPH = 45;

	/**
	 * The feature id for the '<em><b>Named Attribute Node</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_ENTITY_GRAPH__NAMED_ATTRIBUTE_NODE = 0;

	/**
	 * The feature id for the '<em><b>Subgraph</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_ENTITY_GRAPH__SUBGRAPH = 1;

	/**
	 * The feature id for the '<em><b>Subclass Subgraph</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_ENTITY_GRAPH__SUBCLASS_SUBGRAPH = 2;

	/**
	 * The feature id for the '<em><b>Include All Attributes</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_ENTITY_GRAPH__INCLUDE_ALL_ATTRIBUTES = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_ENTITY_GRAPH__NAME = 4;

	/**
	 * The number of structural features of the '<em>Named Entity Graph</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_ENTITY_GRAPH_FEATURE_COUNT = 5;

	/**
	 * The number of operations of the '<em>Named Entity Graph</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_ENTITY_GRAPH_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.NamedNativeQueryImpl <em>Named Native Query</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.NamedNativeQueryImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getNamedNativeQuery()
	 * @generated
	 */
	int NAMED_NATIVE_QUERY = 46;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_NATIVE_QUERY__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Query</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_NATIVE_QUERY__QUERY = 1;

	/**
	 * The feature id for the '<em><b>Hint</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_NATIVE_QUERY__HINT = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_NATIVE_QUERY__NAME = 3;

	/**
	 * The feature id for the '<em><b>Result Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_NATIVE_QUERY__RESULT_CLASS = 4;

	/**
	 * The feature id for the '<em><b>Result Set Mapping</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_NATIVE_QUERY__RESULT_SET_MAPPING = 5;

	/**
	 * The number of structural features of the '<em>Named Native Query</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_NATIVE_QUERY_FEATURE_COUNT = 6;

	/**
	 * The number of operations of the '<em>Named Native Query</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_NATIVE_QUERY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.NamedQueryImpl <em>Named Query</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.NamedQueryImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getNamedQuery()
	 * @generated
	 */
	int NAMED_QUERY = 47;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_QUERY__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Query</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_QUERY__QUERY = 1;

	/**
	 * The feature id for the '<em><b>Lock Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_QUERY__LOCK_MODE = 2;

	/**
	 * The feature id for the '<em><b>Hint</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_QUERY__HINT = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_QUERY__NAME = 4;

	/**
	 * The number of structural features of the '<em>Named Query</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_QUERY_FEATURE_COUNT = 5;

	/**
	 * The number of operations of the '<em>Named Query</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_QUERY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.NamedStoredProcedureQueryImpl <em>Named Stored Procedure Query</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.NamedStoredProcedureQueryImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getNamedStoredProcedureQuery()
	 * @generated
	 */
	int NAMED_STORED_PROCEDURE_QUERY = 48;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_STORED_PROCEDURE_QUERY__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Parameter</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_STORED_PROCEDURE_QUERY__PARAMETER = 1;

	/**
	 * The feature id for the '<em><b>Result Class</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_STORED_PROCEDURE_QUERY__RESULT_CLASS = 2;

	/**
	 * The feature id for the '<em><b>Result Set Mapping</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_STORED_PROCEDURE_QUERY__RESULT_SET_MAPPING = 3;

	/**
	 * The feature id for the '<em><b>Hint</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_STORED_PROCEDURE_QUERY__HINT = 4;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_STORED_PROCEDURE_QUERY__NAME = 5;

	/**
	 * The feature id for the '<em><b>Procedure Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_STORED_PROCEDURE_QUERY__PROCEDURE_NAME = 6;

	/**
	 * The number of structural features of the '<em>Named Stored Procedure Query</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_STORED_PROCEDURE_QUERY_FEATURE_COUNT = 7;

	/**
	 * The number of operations of the '<em>Named Stored Procedure Query</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_STORED_PROCEDURE_QUERY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.NamedSubgraphImpl <em>Named Subgraph</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.NamedSubgraphImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getNamedSubgraph()
	 * @generated
	 */
	int NAMED_SUBGRAPH = 49;

	/**
	 * The feature id for the '<em><b>Named Attribute Node</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_SUBGRAPH__NAMED_ATTRIBUTE_NODE = 0;

	/**
	 * The feature id for the '<em><b>Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_SUBGRAPH__CLASS = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_SUBGRAPH__NAME = 2;

	/**
	 * The number of structural features of the '<em>Named Subgraph</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_SUBGRAPH_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Named Subgraph</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_SUBGRAPH_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.OneToManyImpl <em>One To Many</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.OneToManyImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getOneToMany()
	 * @generated
	 */
	int ONE_TO_MANY = 50;

	/**
	 * The feature id for the '<em><b>Accessible Object</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY__ACCESSIBLE_OBJECT = MAPPED_BY_REF__ACCESSIBLE_OBJECT;

	/**
	 * The feature id for the '<em><b>Access</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY__ACCESS = MAPPED_BY_REF__ACCESS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY__NAME = MAPPED_BY_REF__NAME;

	/**
	 * The feature id for the '<em><b>Cascade</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY__CASCADE = MAPPED_BY_REF__CASCADE;

	/**
	 * The feature id for the '<em><b>Fetch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY__FETCH = MAPPED_BY_REF__FETCH;

	/**
	 * The feature id for the '<em><b>Foreign Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY__FOREIGN_KEY = MAPPED_BY_REF__FOREIGN_KEY;

	/**
	 * The feature id for the '<em><b>Join Table</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY__JOIN_TABLE = MAPPED_BY_REF__JOIN_TABLE;

	/**
	 * The feature id for the '<em><b>Optional</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY__OPTIONAL = MAPPED_BY_REF__OPTIONAL;

	/**
	 * The feature id for the '<em><b>Batch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY__BATCH = MAPPED_BY_REF__BATCH;

	/**
	 * The feature id for the '<em><b>Mapped By</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY__MAPPED_BY = MAPPED_BY_REF__MAPPED_BY;

	/**
	 * The feature id for the '<em><b>Order By</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY__ORDER_BY = MAPPED_BY_REF_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Order Column</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY__ORDER_COLUMN = MAPPED_BY_REF_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Map Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY__MAP_KEY = MAPPED_BY_REF_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Map Key Class</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY__MAP_KEY_CLASS = MAPPED_BY_REF_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Map Key Temporal</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY__MAP_KEY_TEMPORAL = MAPPED_BY_REF_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Map Key Enumerated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY__MAP_KEY_ENUMERATED = MAPPED_BY_REF_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Map Key Attribute Override</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY__MAP_KEY_ATTRIBUTE_OVERRIDE = MAPPED_BY_REF_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Map Key Convert</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY__MAP_KEY_CONVERT = MAPPED_BY_REF_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Map Key Column</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY__MAP_KEY_COLUMN = MAPPED_BY_REF_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Map Key Join Column</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY__MAP_KEY_JOIN_COLUMN = MAPPED_BY_REF_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Map Key Foreign Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY__MAP_KEY_FOREIGN_KEY = MAPPED_BY_REF_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>Join Column</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY__JOIN_COLUMN = MAPPED_BY_REF_FEATURE_COUNT + 11;

	/**
	 * The feature id for the '<em><b>Orphan Removal</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY__ORPHAN_REMOVAL = MAPPED_BY_REF_FEATURE_COUNT + 12;

	/**
	 * The feature id for the '<em><b>Target Entity</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY__TARGET_ENTITY = MAPPED_BY_REF_FEATURE_COUNT + 13;

	/**
	 * The number of structural features of the '<em>One To Many</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY_FEATURE_COUNT = MAPPED_BY_REF_FEATURE_COUNT + 14;

	/**
	 * The number of operations of the '<em>One To Many</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_MANY_OPERATION_COUNT = MAPPED_BY_REF_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.OneToOneImpl <em>One To One</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.OneToOneImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getOneToOne()
	 * @generated
	 */
	int ONE_TO_ONE = 51;

	/**
	 * The feature id for the '<em><b>Accessible Object</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_ONE__ACCESSIBLE_OBJECT = MAPPED_BY_REF__ACCESSIBLE_OBJECT;

	/**
	 * The feature id for the '<em><b>Access</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_ONE__ACCESS = MAPPED_BY_REF__ACCESS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_ONE__NAME = MAPPED_BY_REF__NAME;

	/**
	 * The feature id for the '<em><b>Cascade</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_ONE__CASCADE = MAPPED_BY_REF__CASCADE;

	/**
	 * The feature id for the '<em><b>Fetch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_ONE__FETCH = MAPPED_BY_REF__FETCH;

	/**
	 * The feature id for the '<em><b>Foreign Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_ONE__FOREIGN_KEY = MAPPED_BY_REF__FOREIGN_KEY;

	/**
	 * The feature id for the '<em><b>Join Table</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_ONE__JOIN_TABLE = MAPPED_BY_REF__JOIN_TABLE;

	/**
	 * The feature id for the '<em><b>Optional</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_ONE__OPTIONAL = MAPPED_BY_REF__OPTIONAL;

	/**
	 * The feature id for the '<em><b>Batch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_ONE__BATCH = MAPPED_BY_REF__BATCH;

	/**
	 * The feature id for the '<em><b>Mapped By</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_ONE__MAPPED_BY = MAPPED_BY_REF__MAPPED_BY;

	/**
	 * The feature id for the '<em><b>Primary Key Join Column</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_ONE__PRIMARY_KEY_JOIN_COLUMN = MAPPED_BY_REF_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Primary Key Foreign Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_ONE__PRIMARY_KEY_FOREIGN_KEY = MAPPED_BY_REF_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Join Column</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_ONE__JOIN_COLUMN = MAPPED_BY_REF_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_ONE__ID = MAPPED_BY_REF_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Maps Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_ONE__MAPS_ID = MAPPED_BY_REF_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Orphan Removal</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_ONE__ORPHAN_REMOVAL = MAPPED_BY_REF_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Target Entity</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_ONE__TARGET_ENTITY = MAPPED_BY_REF_FEATURE_COUNT + 6;

	/**
	 * The number of structural features of the '<em>One To One</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_ONE_FEATURE_COUNT = MAPPED_BY_REF_FEATURE_COUNT + 7;

	/**
	 * The number of operations of the '<em>One To One</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ONE_TO_ONE_OPERATION_COUNT = MAPPED_BY_REF_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.OrderColumnImpl <em>Order Column</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.OrderColumnImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getOrderColumn()
	 * @generated
	 */
	int ORDER_COLUMN = 52;

	/**
	 * The feature id for the '<em><b>Accessible Object</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ORDER_COLUMN__ACCESSIBLE_OBJECT = BASE_COLUMN__ACCESSIBLE_OBJECT;

	/**
	 * The feature id for the '<em><b>Column Definition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ORDER_COLUMN__COLUMN_DEFINITION = BASE_COLUMN__COLUMN_DEFINITION;

	/**
	 * The feature id for the '<em><b>Insertable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ORDER_COLUMN__INSERTABLE = BASE_COLUMN__INSERTABLE;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ORDER_COLUMN__NAME = BASE_COLUMN__NAME;

	/**
	 * The feature id for the '<em><b>Nullable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ORDER_COLUMN__NULLABLE = BASE_COLUMN__NULLABLE;

	/**
	 * The feature id for the '<em><b>Updatable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ORDER_COLUMN__UPDATABLE = BASE_COLUMN__UPDATABLE;

	/**
	 * The number of structural features of the '<em>Order Column</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ORDER_COLUMN_FEATURE_COUNT = BASE_COLUMN_FEATURE_COUNT + 0;

	/**
	 * The number of operations of the '<em>Order Column</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ORDER_COLUMN_OPERATION_COUNT = BASE_COLUMN_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.PersistenceUnitDefaultsImpl <em>Persistence Unit Defaults</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.PersistenceUnitDefaultsImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getPersistenceUnitDefaults()
	 * @generated
	 */
	int PERSISTENCE_UNIT_DEFAULTS = 53;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT_DEFAULTS__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Schema</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT_DEFAULTS__SCHEMA = 1;

	/**
	 * The feature id for the '<em><b>Catalog</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT_DEFAULTS__CATALOG = 2;

	/**
	 * The feature id for the '<em><b>Delimited Identifiers</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT_DEFAULTS__DELIMITED_IDENTIFIERS = 3;

	/**
	 * The feature id for the '<em><b>Access</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT_DEFAULTS__ACCESS = 4;

	/**
	 * The feature id for the '<em><b>Cascade Persist</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT_DEFAULTS__CASCADE_PERSIST = 5;

	/**
	 * The feature id for the '<em><b>Entity Listeners</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT_DEFAULTS__ENTITY_LISTENERS = 6;

	/**
	 * The number of structural features of the '<em>Persistence Unit Defaults</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT_DEFAULTS_FEATURE_COUNT = 7;

	/**
	 * The number of operations of the '<em>Persistence Unit Defaults</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT_DEFAULTS_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.PersistenceUnitMetadataImpl <em>Persistence Unit Metadata</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.PersistenceUnitMetadataImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getPersistenceUnitMetadata()
	 * @generated
	 */
	int PERSISTENCE_UNIT_METADATA = 54;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT_METADATA__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Xml Mapping Metadata Complete</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT_METADATA__XML_MAPPING_METADATA_COMPLETE = 1;

	/**
	 * The feature id for the '<em><b>Persistence Unit Defaults</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT_METADATA__PERSISTENCE_UNIT_DEFAULTS = 2;

	/**
	 * The number of structural features of the '<em>Persistence Unit Metadata</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT_METADATA_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Persistence Unit Metadata</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_UNIT_METADATA_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.PostLoadImpl <em>Post Load</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.PostLoadImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getPostLoad()
	 * @generated
	 */
	int POST_LOAD = 55;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST_LOAD__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Method Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST_LOAD__METHOD_NAME = 1;

	/**
	 * The number of structural features of the '<em>Post Load</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST_LOAD_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Post Load</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST_LOAD_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.PostPersistImpl <em>Post Persist</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.PostPersistImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getPostPersist()
	 * @generated
	 */
	int POST_PERSIST = 56;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST_PERSIST__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Method Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST_PERSIST__METHOD_NAME = 1;

	/**
	 * The number of structural features of the '<em>Post Persist</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST_PERSIST_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Post Persist</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST_PERSIST_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.PostRemoveImpl <em>Post Remove</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.PostRemoveImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getPostRemove()
	 * @generated
	 */
	int POST_REMOVE = 57;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST_REMOVE__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Method Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST_REMOVE__METHOD_NAME = 1;

	/**
	 * The number of structural features of the '<em>Post Remove</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST_REMOVE_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Post Remove</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST_REMOVE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.PostUpdateImpl <em>Post Update</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.PostUpdateImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getPostUpdate()
	 * @generated
	 */
	int POST_UPDATE = 58;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST_UPDATE__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Method Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST_UPDATE__METHOD_NAME = 1;

	/**
	 * The number of structural features of the '<em>Post Update</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST_UPDATE_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Post Update</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST_UPDATE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.PrePersistImpl <em>Pre Persist</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.PrePersistImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getPrePersist()
	 * @generated
	 */
	int PRE_PERSIST = 59;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRE_PERSIST__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Method Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRE_PERSIST__METHOD_NAME = 1;

	/**
	 * The number of structural features of the '<em>Pre Persist</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRE_PERSIST_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Pre Persist</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRE_PERSIST_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.PreRemoveImpl <em>Pre Remove</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.PreRemoveImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getPreRemove()
	 * @generated
	 */
	int PRE_REMOVE = 60;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRE_REMOVE__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Method Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRE_REMOVE__METHOD_NAME = 1;

	/**
	 * The number of structural features of the '<em>Pre Remove</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRE_REMOVE_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Pre Remove</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRE_REMOVE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.PreUpdateImpl <em>Pre Update</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.PreUpdateImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getPreUpdate()
	 * @generated
	 */
	int PRE_UPDATE = 61;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRE_UPDATE__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Method Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRE_UPDATE__METHOD_NAME = 1;

	/**
	 * The number of structural features of the '<em>Pre Update</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRE_UPDATE_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Pre Update</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRE_UPDATE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.PrimaryKeyJoinColumnImpl <em>Primary Key Join Column</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.PrimaryKeyJoinColumnImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getPrimaryKeyJoinColumn()
	 * @generated
	 */
	int PRIMARY_KEY_JOIN_COLUMN = 62;

	/**
	 * The feature id for the '<em><b>Column Definition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRIMARY_KEY_JOIN_COLUMN__COLUMN_DEFINITION = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRIMARY_KEY_JOIN_COLUMN__NAME = 1;

	/**
	 * The feature id for the '<em><b>Referenced Column Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRIMARY_KEY_JOIN_COLUMN__REFERENCED_COLUMN_NAME = 2;

	/**
	 * The number of structural features of the '<em>Primary Key Join Column</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRIMARY_KEY_JOIN_COLUMN_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Primary Key Join Column</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRIMARY_KEY_JOIN_COLUMN_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.QueryHintImpl <em>Query Hint</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.QueryHintImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getQueryHint()
	 * @generated
	 */
	int QUERY_HINT = 63;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_HINT__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_HINT__NAME = 1;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_HINT__VALUE = 2;

	/**
	 * The number of structural features of the '<em>Query Hint</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_HINT_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Query Hint</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_HINT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.SecondaryTableImpl <em>Secondary Table</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.SecondaryTableImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getSecondaryTable()
	 * @generated
	 */
	int SECONDARY_TABLE = 64;

	/**
	 * The feature id for the '<em><b>Primary Key Join Column</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECONDARY_TABLE__PRIMARY_KEY_JOIN_COLUMN = 0;

	/**
	 * The feature id for the '<em><b>Primary Key Foreign Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECONDARY_TABLE__PRIMARY_KEY_FOREIGN_KEY = 1;

	/**
	 * The feature id for the '<em><b>Unique Constraint</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECONDARY_TABLE__UNIQUE_CONSTRAINT = 2;

	/**
	 * The feature id for the '<em><b>Index</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECONDARY_TABLE__INDEX = 3;

	/**
	 * The feature id for the '<em><b>Catalog</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECONDARY_TABLE__CATALOG = 4;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECONDARY_TABLE__NAME = 5;

	/**
	 * The feature id for the '<em><b>Schema</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECONDARY_TABLE__SCHEMA = 6;

	/**
	 * The number of structural features of the '<em>Secondary Table</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECONDARY_TABLE_FEATURE_COUNT = 7;

	/**
	 * The number of operations of the '<em>Secondary Table</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECONDARY_TABLE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.SequenceGeneratorImpl <em>Sequence Generator</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.SequenceGeneratorImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getSequenceGenerator()
	 * @generated
	 */
	int SEQUENCE_GENERATOR = 65;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENCE_GENERATOR__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Allocation Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENCE_GENERATOR__ALLOCATION_SIZE = 1;

	/**
	 * The feature id for the '<em><b>Catalog</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENCE_GENERATOR__CATALOG = 2;

	/**
	 * The feature id for the '<em><b>Initial Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENCE_GENERATOR__INITIAL_VALUE = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENCE_GENERATOR__NAME = 4;

	/**
	 * The feature id for the '<em><b>Schema</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENCE_GENERATOR__SCHEMA = 5;

	/**
	 * The feature id for the '<em><b>Sequence Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENCE_GENERATOR__SEQUENCE_NAME = 6;

	/**
	 * The number of structural features of the '<em>Sequence Generator</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENCE_GENERATOR_FEATURE_COUNT = 7;

	/**
	 * The number of operations of the '<em>Sequence Generator</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENCE_GENERATOR_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.SqlResultSetMappingImpl <em>Sql Result Set Mapping</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.SqlResultSetMappingImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getSqlResultSetMapping()
	 * @generated
	 */
	int SQL_RESULT_SET_MAPPING = 67;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SQL_RESULT_SET_MAPPING__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Entity Result</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SQL_RESULT_SET_MAPPING__ENTITY_RESULT = 1;

	/**
	 * The feature id for the '<em><b>Constructor Result</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SQL_RESULT_SET_MAPPING__CONSTRUCTOR_RESULT = 2;

	/**
	 * The feature id for the '<em><b>Column Result</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SQL_RESULT_SET_MAPPING__COLUMN_RESULT = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SQL_RESULT_SET_MAPPING__NAME = 4;

	/**
	 * The number of structural features of the '<em>Sql Result Set Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SQL_RESULT_SET_MAPPING_FEATURE_COUNT = 5;

	/**
	 * The number of operations of the '<em>Sql Result Set Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SQL_RESULT_SET_MAPPING_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.StoredProcedureParameterImpl <em>Stored Procedure Parameter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.StoredProcedureParameterImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getStoredProcedureParameter()
	 * @generated
	 */
	int STORED_PROCEDURE_PARAMETER = 68;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STORED_PROCEDURE_PARAMETER__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Class</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STORED_PROCEDURE_PARAMETER__CLASS = 1;

	/**
	 * The feature id for the '<em><b>Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STORED_PROCEDURE_PARAMETER__MODE = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STORED_PROCEDURE_PARAMETER__NAME = 3;

	/**
	 * The number of structural features of the '<em>Stored Procedure Parameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STORED_PROCEDURE_PARAMETER_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Stored Procedure Parameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STORED_PROCEDURE_PARAMETER_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.TableGeneratorImpl <em>Table Generator</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.TableGeneratorImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getTableGenerator()
	 * @generated
	 */
	int TABLE_GENERATOR = 70;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE_GENERATOR__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Unique Constraint</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE_GENERATOR__UNIQUE_CONSTRAINT = 1;

	/**
	 * The feature id for the '<em><b>Index</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE_GENERATOR__INDEX = 2;

	/**
	 * The feature id for the '<em><b>Allocation Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE_GENERATOR__ALLOCATION_SIZE = 3;

	/**
	 * The feature id for the '<em><b>Catalog</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE_GENERATOR__CATALOG = 4;

	/**
	 * The feature id for the '<em><b>Initial Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE_GENERATOR__INITIAL_VALUE = 5;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE_GENERATOR__NAME = 6;

	/**
	 * The feature id for the '<em><b>Pk Column Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE_GENERATOR__PK_COLUMN_NAME = 7;

	/**
	 * The feature id for the '<em><b>Pk Column Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE_GENERATOR__PK_COLUMN_VALUE = 8;

	/**
	 * The feature id for the '<em><b>Schema</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE_GENERATOR__SCHEMA = 9;

	/**
	 * The feature id for the '<em><b>Table</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE_GENERATOR__TABLE = 10;

	/**
	 * The feature id for the '<em><b>Value Column Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE_GENERATOR__VALUE_COLUMN_NAME = 11;

	/**
	 * The number of structural features of the '<em>Table Generator</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE_GENERATOR_FEATURE_COUNT = 12;

	/**
	 * The number of operations of the '<em>Table Generator</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE_GENERATOR_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.TransientImpl <em>Transient</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.TransientImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getTransient()
	 * @generated
	 */
	int TRANSIENT = 71;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSIENT__NAME = 0;

	/**
	 * The number of structural features of the '<em>Transient</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSIENT_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Transient</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSIENT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.UniqueConstraintImpl <em>Unique Constraint</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.UniqueConstraintImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getUniqueConstraint()
	 * @generated
	 */
	int UNIQUE_CONSTRAINT = 72;

	/**
	 * The feature id for the '<em><b>Column Name</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNIQUE_CONSTRAINT__COLUMN_NAME = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNIQUE_CONSTRAINT__NAME = 1;

	/**
	 * The number of structural features of the '<em>Unique Constraint</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNIQUE_CONSTRAINT_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Unique Constraint</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNIQUE_CONSTRAINT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.VersionImpl <em>Version</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.VersionImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getVersion()
	 * @generated
	 */
	int VERSION = 73;

	/**
	 * The feature id for the '<em><b>Column</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VERSION__COLUMN = 0;

	/**
	 * The feature id for the '<em><b>Temporal</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VERSION__TEMPORAL = 1;

	/**
	 * The feature id for the '<em><b>Access</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VERSION__ACCESS = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VERSION__NAME = 3;

	/**
	 * The number of structural features of the '<em>Version</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VERSION_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Version</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VERSION_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.EAccessorImpl <em>EAccessor</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.EAccessorImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getEAccessor()
	 * @generated
	 */
	int EACCESSOR = 76;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EACCESSOR__NAME = 0;

	/**
	 * The number of structural features of the '<em>EAccessor</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EACCESSOR_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>EAccessor</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EACCESSOR_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.EFeatureObjectImpl <em>EFeature Object</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.EFeatureObjectImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getEFeatureObject()
	 * @generated
	 */
	int EFEATURE_OBJECT = 77;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EFEATURE_OBJECT__NAME = EACCESSOR__NAME;

	/**
	 * The feature id for the '<em><b>Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EFEATURE_OBJECT__FEATURE = EACCESSOR_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>EFeature Object</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EFEATURE_OBJECT_FEATURE_COUNT = EACCESSOR_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>EFeature Object</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EFEATURE_OBJECT_OPERATION_COUNT = EACCESSOR_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.impl.EClassObjectImpl <em>EClass Object</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.impl.EClassObjectImpl
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getEClassObject()
	 * @generated
	 */
	int ECLASS_OBJECT = 78;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECLASS_OBJECT__NAME = EACCESSOR__NAME;

	/**
	 * The feature id for the '<em><b>Eclass</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECLASS_OBJECT__ECLASS = EACCESSOR_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>EClass Object</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECLASS_OBJECT_FEATURE_COUNT = EACCESSOR_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>EClass Object</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECLASS_OBJECT_OPERATION_COUNT = EACCESSOR_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.AccessType <em>Access Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.AccessType
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getAccessType()
	 * @generated
	 */
	int ACCESS_TYPE = 80;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.ConstraintMode <em>Constraint Mode</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.ConstraintMode
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getConstraintMode()
	 * @generated
	 */
	int CONSTRAINT_MODE = 81;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.DiscriminatorType <em>Discriminator Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.DiscriminatorType
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getDiscriminatorType()
	 * @generated
	 */
	int DISCRIMINATOR_TYPE = 82;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.EnumType <em>Enum Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.EnumType
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getEnumType()
	 * @generated
	 */
	int ENUM_TYPE = 83;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.FetchType <em>Fetch Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.FetchType
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getFetchType()
	 * @generated
	 */
	int FETCH_TYPE = 84;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.GenerationType <em>Generation Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.GenerationType
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getGenerationType()
	 * @generated
	 */
	int GENERATION_TYPE = 85;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.InheritanceType <em>Inheritance Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.InheritanceType
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getInheritanceType()
	 * @generated
	 */
	int INHERITANCE_TYPE = 86;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.LockModeType <em>Lock Mode Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.LockModeType
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getLockModeType()
	 * @generated
	 */
	int LOCK_MODE_TYPE = 87;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.ParameterMode <em>Parameter Mode</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.ParameterMode
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getParameterMode()
	 * @generated
	 */
	int PARAMETER_MODE = 88;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.eorm.TemporalType <em>Temporal Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.TemporalType
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getTemporalType()
	 * @generated
	 */
	int TEMPORAL_TYPE = 89;

	/**
	 * The meta object id for the '<em>Access Type Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.AccessType
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getAccessTypeObject()
	 * @generated
	 */
	int ACCESS_TYPE_OBJECT = 90;

	/**
	 * The meta object id for the '<em>Constraint Mode Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.ConstraintMode
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getConstraintModeObject()
	 * @generated
	 */
	int CONSTRAINT_MODE_OBJECT = 91;

	/**
	 * The meta object id for the '<em>Discriminator Type Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.DiscriminatorType
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getDiscriminatorTypeObject()
	 * @generated
	 */
	int DISCRIMINATOR_TYPE_OBJECT = 92;

	/**
	 * The meta object id for the '<em>Discriminator Value</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.lang.String
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getDiscriminatorValue()
	 * @generated
	 */
	int DISCRIMINATOR_VALUE = 93;

	/**
	 * The meta object id for the '<em>Enumerated</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.EnumType
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getEnumerated()
	 * @generated
	 */
	int ENUMERATED = 94;

	/**
	 * The meta object id for the '<em>Enum Type Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.EnumType
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getEnumTypeObject()
	 * @generated
	 */
	int ENUM_TYPE_OBJECT = 95;

	/**
	 * The meta object id for the '<em>Fetch Type Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.FetchType
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getFetchTypeObject()
	 * @generated
	 */
	int FETCH_TYPE_OBJECT = 96;

	/**
	 * The meta object id for the '<em>Generation Type Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.GenerationType
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getGenerationTypeObject()
	 * @generated
	 */
	int GENERATION_TYPE_OBJECT = 97;

	/**
	 * The meta object id for the '<em>Inheritance Type Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.InheritanceType
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getInheritanceTypeObject()
	 * @generated
	 */
	int INHERITANCE_TYPE_OBJECT = 98;

	/**
	 * The meta object id for the '<em>Lock Mode Type Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.LockModeType
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getLockModeTypeObject()
	 * @generated
	 */
	int LOCK_MODE_TYPE_OBJECT = 99;

	/**
	 * The meta object id for the '<em>Order By</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.lang.String
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getOrderBy()
	 * @generated
	 */
	int ORDER_BY = 100;

	/**
	 * The meta object id for the '<em>Parameter Mode Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.ParameterMode
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getParameterModeObject()
	 * @generated
	 */
	int PARAMETER_MODE_OBJECT = 101;

	/**
	 * The meta object id for the '<em>Temporal</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.TemporalType
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getTemporal()
	 * @generated
	 */
	int TEMPORAL = 102;

	/**
	 * The meta object id for the '<em>Temporal Type Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.eorm.TemporalType
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getTemporalTypeObject()
	 * @generated
	 */
	int TEMPORAL_TYPE_OBJECT = 103;

	/**
	 * The meta object id for the '<em>Version Type</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.lang.String
	 * @see org.eclipse.fennec.persistence.eorm.impl.EORMPackageImpl#getVersionType()
	 * @generated
	 */
	int VERSION_TYPE = 104;


	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.AssociationOverride <em>Association Override</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Association Override</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.AssociationOverride
	 * @generated
	 */
	EClass getAssociationOverride();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.AssociationOverride#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.AssociationOverride#getDescription()
	 * @see #getAssociationOverride()
	 * @generated
	 */
	EAttribute getAssociationOverride_Description();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.AssociationOverride#getJoinColumn <em>Join Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Join Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.AssociationOverride#getJoinColumn()
	 * @see #getAssociationOverride()
	 * @generated
	 */
	EReference getAssociationOverride_JoinColumn();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.AssociationOverride#getForeignKey <em>Foreign Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Foreign Key</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.AssociationOverride#getForeignKey()
	 * @see #getAssociationOverride()
	 * @generated
	 */
	EReference getAssociationOverride_ForeignKey();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.AssociationOverride#getJoinTable <em>Join Table</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Join Table</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.AssociationOverride#getJoinTable()
	 * @see #getAssociationOverride()
	 * @generated
	 */
	EReference getAssociationOverride_JoinTable();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.AssociationOverride#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.AssociationOverride#getName()
	 * @see #getAssociationOverride()
	 * @generated
	 */
	EAttribute getAssociationOverride_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.AttributeOverride <em>Attribute Override</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Attribute Override</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.AttributeOverride
	 * @generated
	 */
	EClass getAttributeOverride();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.AttributeOverride#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.AttributeOverride#getDescription()
	 * @see #getAttributeOverride()
	 * @generated
	 */
	EAttribute getAttributeOverride_Description();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.AttributeOverride#getColumn <em>Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.AttributeOverride#getColumn()
	 * @see #getAttributeOverride()
	 * @generated
	 */
	EReference getAttributeOverride_Column();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.AttributeOverride#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.AttributeOverride#getName()
	 * @see #getAttributeOverride()
	 * @generated
	 */
	EAttribute getAttributeOverride_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.Attributes <em>Attributes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Attributes</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Attributes
	 * @generated
	 */
	EClass getAttributes();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Attributes#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Attributes#getDescription()
	 * @see #getAttributes()
	 * @generated
	 */
	EAttribute getAttributes_Description();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Attributes#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Id</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Attributes#getId()
	 * @see #getAttributes()
	 * @generated
	 */
	EReference getAttributes_Id();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Attributes#getEmbeddedId <em>Embedded Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Embedded Id</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Attributes#getEmbeddedId()
	 * @see #getAttributes()
	 * @generated
	 */
	EReference getAttributes_EmbeddedId();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Attributes#getBasic <em>Basic</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Basic</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Attributes#getBasic()
	 * @see #getAttributes()
	 * @generated
	 */
	EReference getAttributes_Basic();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Attributes#getVersion <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Version</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Attributes#getVersion()
	 * @see #getAttributes()
	 * @generated
	 */
	EReference getAttributes_Version();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Attributes#getManyToOne <em>Many To One</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Many To One</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Attributes#getManyToOne()
	 * @see #getAttributes()
	 * @generated
	 */
	EReference getAttributes_ManyToOne();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Attributes#getOneToMany <em>One To Many</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>One To Many</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Attributes#getOneToMany()
	 * @see #getAttributes()
	 * @generated
	 */
	EReference getAttributes_OneToMany();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Attributes#getOneToOne <em>One To One</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>One To One</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Attributes#getOneToOne()
	 * @see #getAttributes()
	 * @generated
	 */
	EReference getAttributes_OneToOne();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Attributes#getManyToMany <em>Many To Many</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Many To Many</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Attributes#getManyToMany()
	 * @see #getAttributes()
	 * @generated
	 */
	EReference getAttributes_ManyToMany();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Attributes#getElementCollection <em>Element Collection</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Element Collection</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Attributes#getElementCollection()
	 * @see #getAttributes()
	 * @generated
	 */
	EReference getAttributes_ElementCollection();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Attributes#getEmbedded <em>Embedded</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Embedded</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Attributes#getEmbedded()
	 * @see #getAttributes()
	 * @generated
	 */
	EReference getAttributes_Embedded();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Attributes#getTransient <em>Transient</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Transient</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Attributes#getTransient()
	 * @see #getAttributes()
	 * @generated
	 */
	EReference getAttributes_Transient();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.Basic <em>Basic</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Basic</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Basic
	 * @generated
	 */
	EClass getBasic();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Basic#isOptional <em>Optional</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Optional</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Basic#isOptional()
	 * @see #getBasic()
	 * @generated
	 */
	EAttribute getBasic_Optional();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.CascadeType <em>Cascade Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Cascade Type</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.CascadeType
	 * @generated
	 */
	EClass getCascadeType();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.CascadeType#getCascadeAll <em>Cascade All</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Cascade All</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.CascadeType#getCascadeAll()
	 * @see #getCascadeType()
	 * @generated
	 */
	EReference getCascadeType_CascadeAll();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.CascadeType#getCascadePersist <em>Cascade Persist</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Cascade Persist</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.CascadeType#getCascadePersist()
	 * @see #getCascadeType()
	 * @generated
	 */
	EReference getCascadeType_CascadePersist();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.CascadeType#getCascadeMerge <em>Cascade Merge</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Cascade Merge</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.CascadeType#getCascadeMerge()
	 * @see #getCascadeType()
	 * @generated
	 */
	EReference getCascadeType_CascadeMerge();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.CascadeType#getCascadeRemove <em>Cascade Remove</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Cascade Remove</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.CascadeType#getCascadeRemove()
	 * @see #getCascadeType()
	 * @generated
	 */
	EReference getCascadeType_CascadeRemove();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.CascadeType#getCascadeRefresh <em>Cascade Refresh</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Cascade Refresh</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.CascadeType#getCascadeRefresh()
	 * @see #getCascadeType()
	 * @generated
	 */
	EReference getCascadeType_CascadeRefresh();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.CascadeType#getCascadeDetach <em>Cascade Detach</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Cascade Detach</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.CascadeType#getCascadeDetach()
	 * @see #getCascadeType()
	 * @generated
	 */
	EReference getCascadeType_CascadeDetach();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.CollectionTable <em>Collection Table</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Collection Table</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.CollectionTable
	 * @generated
	 */
	EClass getCollectionTable();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.CollectionTable#getJoinColumn <em>Join Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Join Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.CollectionTable#getJoinColumn()
	 * @see #getCollectionTable()
	 * @generated
	 */
	EReference getCollectionTable_JoinColumn();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.CollectionTable#getForeignKey <em>Foreign Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Foreign Key</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.CollectionTable#getForeignKey()
	 * @see #getCollectionTable()
	 * @generated
	 */
	EReference getCollectionTable_ForeignKey();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.Column <em>Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Column
	 * @generated
	 */
	EClass getColumn();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Column#getLength <em>Length</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Length</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Column#getLength()
	 * @see #getColumn()
	 * @generated
	 */
	EAttribute getColumn_Length();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Column#getPrecision <em>Precision</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Precision</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Column#getPrecision()
	 * @see #getColumn()
	 * @generated
	 */
	EAttribute getColumn_Precision();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Column#getScale <em>Scale</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Scale</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Column#getScale()
	 * @see #getColumn()
	 * @generated
	 */
	EAttribute getColumn_Scale();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Column#getTable <em>Table</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Table</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Column#getTable()
	 * @see #getColumn()
	 * @generated
	 */
	EAttribute getColumn_Table();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Column#isUnique <em>Unique</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Unique</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Column#isUnique()
	 * @see #getColumn()
	 * @generated
	 */
	EAttribute getColumn_Unique();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.ColumnResult <em>Column Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Column Result</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ColumnResult
	 * @generated
	 */
	EClass getColumnResult();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.ColumnResult#getClass_ <em>Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Class</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ColumnResult#getClass_()
	 * @see #getColumnResult()
	 * @generated
	 */
	EAttribute getColumnResult_Class();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.ColumnResult#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ColumnResult#getName()
	 * @see #getColumnResult()
	 * @generated
	 */
	EAttribute getColumnResult_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.ConstructorResult <em>Constructor Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Constructor Result</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ConstructorResult
	 * @generated
	 */
	EClass getConstructorResult();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.ConstructorResult#getColumn <em>Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ConstructorResult#getColumn()
	 * @see #getConstructorResult()
	 * @generated
	 */
	EReference getConstructorResult_Column();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.ConstructorResult#getTargetClass <em>Target Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Target Class</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ConstructorResult#getTargetClass()
	 * @see #getConstructorResult()
	 * @generated
	 */
	EAttribute getConstructorResult_TargetClass();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.Convert <em>Convert</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Convert</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Convert
	 * @generated
	 */
	EClass getConvert();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Convert#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Convert#getDescription()
	 * @see #getConvert()
	 * @generated
	 */
	EAttribute getConvert_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Convert#getAttributeName <em>Attribute Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Attribute Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Convert#getAttributeName()
	 * @see #getConvert()
	 * @generated
	 */
	EAttribute getConvert_AttributeName();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Convert#getConverter <em>Converter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Converter</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Convert#getConverter()
	 * @see #getConvert()
	 * @generated
	 */
	EAttribute getConvert_Converter();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Convert#isDisableConversion <em>Disable Conversion</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Disable Conversion</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Convert#isDisableConversion()
	 * @see #getConvert()
	 * @generated
	 */
	EAttribute getConvert_DisableConversion();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.Converter <em>Converter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Converter</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Converter
	 * @generated
	 */
	EClass getConverter();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Converter#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Converter#getDescription()
	 * @see #getConverter()
	 * @generated
	 */
	EAttribute getConverter_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Converter#isAutoApply <em>Auto Apply</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Auto Apply</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Converter#isAutoApply()
	 * @see #getConverter()
	 * @generated
	 */
	EAttribute getConverter_AutoApply();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.persistence.eorm.Converter#getClass_ <em>Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Class</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Converter#getClass_()
	 * @see #getConverter()
	 * @generated
	 */
	EReference getConverter_Class();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.DiscriminatorColumn <em>Discriminator Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Discriminator Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.DiscriminatorColumn
	 * @generated
	 */
	EClass getDiscriminatorColumn();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.DiscriminatorColumn#getColumnDefinition <em>Column Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Column Definition</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.DiscriminatorColumn#getColumnDefinition()
	 * @see #getDiscriminatorColumn()
	 * @generated
	 */
	EAttribute getDiscriminatorColumn_ColumnDefinition();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.DiscriminatorColumn#getDiscriminatorType <em>Discriminator Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Discriminator Type</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.DiscriminatorColumn#getDiscriminatorType()
	 * @see #getDiscriminatorColumn()
	 * @generated
	 */
	EAttribute getDiscriminatorColumn_DiscriminatorType();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.DiscriminatorColumn#getLength <em>Length</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Length</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.DiscriminatorColumn#getLength()
	 * @see #getDiscriminatorColumn()
	 * @generated
	 */
	EAttribute getDiscriminatorColumn_Length();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.DiscriminatorColumn#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.DiscriminatorColumn#getName()
	 * @see #getDiscriminatorColumn()
	 * @generated
	 */
	EAttribute getDiscriminatorColumn_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.DocumentRoot <em>Document Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Document Root</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.DocumentRoot
	 * @generated
	 */
	EClass getDocumentRoot();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.persistence.eorm.DocumentRoot#getMixed <em>Mixed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mixed</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.DocumentRoot#getMixed()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EAttribute getDocumentRoot_Mixed();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.persistence.eorm.DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>XMLNS Prefix Map</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.DocumentRoot#getXMLNSPrefixMap()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_XMLNSPrefixMap();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.persistence.eorm.DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>XSI Schema Location</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.DocumentRoot#getXSISchemaLocation()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_XSISchemaLocation();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.DocumentRoot#getEntityMappings <em>Entity Mappings</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Entity Mappings</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.DocumentRoot#getEntityMappings()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_EntityMappings();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.ElementCollection <em>Element Collection</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Element Collection</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ElementCollection
	 * @generated
	 */
	EClass getElementCollection();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getOrderBy <em>Order By</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Order By</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ElementCollection#getOrderBy()
	 * @see #getElementCollection()
	 * @generated
	 */
	EAttribute getElementCollection_OrderBy();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getOrderColumn <em>Order Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Order Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ElementCollection#getOrderColumn()
	 * @see #getElementCollection()
	 * @generated
	 */
	EReference getElementCollection_OrderColumn();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKey <em>Map Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Map Key</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKey()
	 * @see #getElementCollection()
	 * @generated
	 */
	EReference getElementCollection_MapKey();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyClass <em>Map Key Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Map Key Class</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyClass()
	 * @see #getElementCollection()
	 * @generated
	 */
	EReference getElementCollection_MapKeyClass();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyTemporal <em>Map Key Temporal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Map Key Temporal</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyTemporal()
	 * @see #getElementCollection()
	 * @generated
	 */
	EAttribute getElementCollection_MapKeyTemporal();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyEnumerated <em>Map Key Enumerated</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Map Key Enumerated</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyEnumerated()
	 * @see #getElementCollection()
	 * @generated
	 */
	EAttribute getElementCollection_MapKeyEnumerated();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyAttributeOverride <em>Map Key Attribute Override</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Map Key Attribute Override</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyAttributeOverride()
	 * @see #getElementCollection()
	 * @generated
	 */
	EReference getElementCollection_MapKeyAttributeOverride();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyConvert <em>Map Key Convert</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Map Key Convert</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyConvert()
	 * @see #getElementCollection()
	 * @generated
	 */
	EReference getElementCollection_MapKeyConvert();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyColumn <em>Map Key Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Map Key Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyColumn()
	 * @see #getElementCollection()
	 * @generated
	 */
	EReference getElementCollection_MapKeyColumn();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyJoinColumn <em>Map Key Join Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Map Key Join Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyJoinColumn()
	 * @see #getElementCollection()
	 * @generated
	 */
	EReference getElementCollection_MapKeyJoinColumn();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyForeignKey <em>Map Key Foreign Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Map Key Foreign Key</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyForeignKey()
	 * @see #getElementCollection()
	 * @generated
	 */
	EReference getElementCollection_MapKeyForeignKey();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getAttributeOverride <em>Attribute Override</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Attribute Override</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ElementCollection#getAttributeOverride()
	 * @see #getElementCollection()
	 * @generated
	 */
	EReference getElementCollection_AttributeOverride();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getAssociationOverride <em>Association Override</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Association Override</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ElementCollection#getAssociationOverride()
	 * @see #getElementCollection()
	 * @generated
	 */
	EReference getElementCollection_AssociationOverride();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getCollectionTable <em>Collection Table</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Collection Table</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ElementCollection#getCollectionTable()
	 * @see #getElementCollection()
	 * @generated
	 */
	EReference getElementCollection_CollectionTable();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getTargetClass <em>Target Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Target Class</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ElementCollection#getTargetClass()
	 * @see #getElementCollection()
	 * @generated
	 */
	EAttribute getElementCollection_TargetClass();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.Embeddable <em>Embeddable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Embeddable</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Embeddable
	 * @generated
	 */
	EClass getEmbeddable();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Embeddable#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Embeddable#getDescription()
	 * @see #getEmbeddable()
	 * @generated
	 */
	EAttribute getEmbeddable_Description();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Embeddable#getAttributes <em>Attributes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Attributes</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Embeddable#getAttributes()
	 * @see #getEmbeddable()
	 * @generated
	 */
	EReference getEmbeddable_Attributes();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Embeddable#getAccess <em>Access</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Access</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Embeddable#getAccess()
	 * @see #getEmbeddable()
	 * @generated
	 */
	EAttribute getEmbeddable_Access();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.persistence.eorm.Embeddable#getClass_ <em>Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Class</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Embeddable#getClass_()
	 * @see #getEmbeddable()
	 * @generated
	 */
	EReference getEmbeddable_Class();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Embeddable#isMetadataComplete <em>Metadata Complete</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Metadata Complete</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Embeddable#isMetadataComplete()
	 * @see #getEmbeddable()
	 * @generated
	 */
	EAttribute getEmbeddable_MetadataComplete();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.EmbeddableAttributes <em>Embeddable Attributes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Embeddable Attributes</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EmbeddableAttributes
	 * @generated
	 */
	EClass getEmbeddableAttributes();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.EmbeddableAttributes#getBasic <em>Basic</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Basic</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EmbeddableAttributes#getBasic()
	 * @see #getEmbeddableAttributes()
	 * @generated
	 */
	EReference getEmbeddableAttributes_Basic();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.EmbeddableAttributes#getManyToOne <em>Many To One</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Many To One</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EmbeddableAttributes#getManyToOne()
	 * @see #getEmbeddableAttributes()
	 * @generated
	 */
	EReference getEmbeddableAttributes_ManyToOne();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.EmbeddableAttributes#getOneToMany <em>One To Many</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>One To Many</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EmbeddableAttributes#getOneToMany()
	 * @see #getEmbeddableAttributes()
	 * @generated
	 */
	EReference getEmbeddableAttributes_OneToMany();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.EmbeddableAttributes#getOneToOne <em>One To One</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>One To One</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EmbeddableAttributes#getOneToOne()
	 * @see #getEmbeddableAttributes()
	 * @generated
	 */
	EReference getEmbeddableAttributes_OneToOne();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.EmbeddableAttributes#getManyToMany <em>Many To Many</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Many To Many</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EmbeddableAttributes#getManyToMany()
	 * @see #getEmbeddableAttributes()
	 * @generated
	 */
	EReference getEmbeddableAttributes_ManyToMany();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.EmbeddableAttributes#getElementCollection <em>Element Collection</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Element Collection</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EmbeddableAttributes#getElementCollection()
	 * @see #getEmbeddableAttributes()
	 * @generated
	 */
	EReference getEmbeddableAttributes_ElementCollection();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.EmbeddableAttributes#getEmbedded <em>Embedded</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Embedded</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EmbeddableAttributes#getEmbedded()
	 * @see #getEmbeddableAttributes()
	 * @generated
	 */
	EReference getEmbeddableAttributes_Embedded();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.EmbeddableAttributes#getTransient <em>Transient</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Transient</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EmbeddableAttributes#getTransient()
	 * @see #getEmbeddableAttributes()
	 * @generated
	 */
	EReference getEmbeddableAttributes_Transient();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.Embedded <em>Embedded</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Embedded</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Embedded
	 * @generated
	 */
	EClass getEmbedded();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Embedded#getAttributeOverride <em>Attribute Override</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Attribute Override</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Embedded#getAttributeOverride()
	 * @see #getEmbedded()
	 * @generated
	 */
	EReference getEmbedded_AttributeOverride();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Embedded#getAssociationOverride <em>Association Override</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Association Override</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Embedded#getAssociationOverride()
	 * @see #getEmbedded()
	 * @generated
	 */
	EReference getEmbedded_AssociationOverride();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Embedded#getConvert <em>Convert</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Convert</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Embedded#getConvert()
	 * @see #getEmbedded()
	 * @generated
	 */
	EReference getEmbedded_Convert();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Embedded#getAccess <em>Access</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Access</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Embedded#getAccess()
	 * @see #getEmbedded()
	 * @generated
	 */
	EAttribute getEmbedded_Access();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Embedded#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Embedded#getName()
	 * @see #getEmbedded()
	 * @generated
	 */
	EAttribute getEmbedded_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.EmbeddedId <em>Embedded Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Embedded Id</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EmbeddedId
	 * @generated
	 */
	EClass getEmbeddedId();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.EmbeddedId#getAttributeOverride <em>Attribute Override</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Attribute Override</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EmbeddedId#getAttributeOverride()
	 * @see #getEmbeddedId()
	 * @generated
	 */
	EReference getEmbeddedId_AttributeOverride();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.EmbeddedId#getAccess <em>Access</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Access</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EmbeddedId#getAccess()
	 * @see #getEmbeddedId()
	 * @generated
	 */
	EAttribute getEmbeddedId_Access();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.EmbeddedId#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EmbeddedId#getName()
	 * @see #getEmbeddedId()
	 * @generated
	 */
	EAttribute getEmbeddedId_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.EmptyType <em>Empty Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Empty Type</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EmptyType
	 * @generated
	 */
	EClass getEmptyType();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.Entity <em>Entity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Entity</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity
	 * @generated
	 */
	EClass getEntity();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Entity#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getDescription()
	 * @see #getEntity()
	 * @generated
	 */
	EAttribute getEntity_Description();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Entity#getTable <em>Table</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Table</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getTable()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_Table();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Entity#getSecondaryTable <em>Secondary Table</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Secondary Table</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getSecondaryTable()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_SecondaryTable();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Entity#getPrimaryKeyJoinColumn <em>Primary Key Join Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Primary Key Join Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getPrimaryKeyJoinColumn()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_PrimaryKeyJoinColumn();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Entity#getPrimaryKeyForeignKey <em>Primary Key Foreign Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Primary Key Foreign Key</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getPrimaryKeyForeignKey()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_PrimaryKeyForeignKey();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Entity#getIdClass <em>Id Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Id Class</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getIdClass()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_IdClass();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Entity#getInheritance <em>Inheritance</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Inheritance</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getInheritance()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_Inheritance();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Entity#getDiscriminatorValue <em>Discriminator Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Discriminator Value</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getDiscriminatorValue()
	 * @see #getEntity()
	 * @generated
	 */
	EAttribute getEntity_DiscriminatorValue();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Entity#getDiscriminatorColumn <em>Discriminator Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Discriminator Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getDiscriminatorColumn()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_DiscriminatorColumn();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Entity#getSequenceGenerator <em>Sequence Generator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Sequence Generator</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getSequenceGenerator()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_SequenceGenerator();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Entity#getTableGenerator <em>Table Generator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Table Generator</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getTableGenerator()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_TableGenerator();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Entity#getNamedQuery <em>Named Query</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Named Query</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getNamedQuery()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_NamedQuery();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Entity#getNamedNativeQuery <em>Named Native Query</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Named Native Query</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getNamedNativeQuery()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_NamedNativeQuery();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Entity#getNamedStoredProcedureQuery <em>Named Stored Procedure Query</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Named Stored Procedure Query</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getNamedStoredProcedureQuery()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_NamedStoredProcedureQuery();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Entity#getSqlResultSetMapping <em>Sql Result Set Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Sql Result Set Mapping</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getSqlResultSetMapping()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_SqlResultSetMapping();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Entity#getExcludeDefaultListeners <em>Exclude Default Listeners</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Exclude Default Listeners</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getExcludeDefaultListeners()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_ExcludeDefaultListeners();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Entity#getExcludeSuperclassListeners <em>Exclude Superclass Listeners</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Exclude Superclass Listeners</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getExcludeSuperclassListeners()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_ExcludeSuperclassListeners();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Entity#getEntityListeners <em>Entity Listeners</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Entity Listeners</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getEntityListeners()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_EntityListeners();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Entity#getPrePersist <em>Pre Persist</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Pre Persist</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getPrePersist()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_PrePersist();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Entity#getPostPersist <em>Post Persist</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Post Persist</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getPostPersist()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_PostPersist();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Entity#getPreRemove <em>Pre Remove</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Pre Remove</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getPreRemove()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_PreRemove();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Entity#getPostRemove <em>Post Remove</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Post Remove</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getPostRemove()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_PostRemove();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Entity#getPreUpdate <em>Pre Update</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Pre Update</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getPreUpdate()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_PreUpdate();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Entity#getPostUpdate <em>Post Update</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Post Update</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getPostUpdate()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_PostUpdate();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Entity#getPostLoad <em>Post Load</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Post Load</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getPostLoad()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_PostLoad();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Entity#getAttributeOverride <em>Attribute Override</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Attribute Override</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getAttributeOverride()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_AttributeOverride();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Entity#getAssociationOverride <em>Association Override</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Association Override</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getAssociationOverride()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_AssociationOverride();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Entity#getConvert <em>Convert</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Convert</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getConvert()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_Convert();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Entity#getNamedEntityGraph <em>Named Entity Graph</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Named Entity Graph</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getNamedEntityGraph()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_NamedEntityGraph();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Entity#getAttributes <em>Attributes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Attributes</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getAttributes()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_Attributes();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Entity#getAccess <em>Access</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Access</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getAccess()
	 * @see #getEntity()
	 * @generated
	 */
	EAttribute getEntity_Access();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Entity#isCacheable <em>Cacheable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Cacheable</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#isCacheable()
	 * @see #getEntity()
	 * @generated
	 */
	EAttribute getEntity_Cacheable();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Entity#isMetadataComplete <em>Metadata Complete</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Metadata Complete</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#isMetadataComplete()
	 * @see #getEntity()
	 * @generated
	 */
	EAttribute getEntity_MetadataComplete();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Entity#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getName()
	 * @see #getEntity()
	 * @generated
	 */
	EAttribute getEntity_Name();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.persistence.eorm.Entity#getClass_ <em>Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Class</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Entity#getClass_()
	 * @see #getEntity()
	 * @generated
	 */
	EReference getEntity_Class();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.EntityListener <em>Entity Listener</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Entity Listener</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityListener
	 * @generated
	 */
	EClass getEntityListener();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.EntityListener#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityListener#getDescription()
	 * @see #getEntityListener()
	 * @generated
	 */
	EAttribute getEntityListener_Description();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.EntityListener#getPrePersist <em>Pre Persist</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Pre Persist</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityListener#getPrePersist()
	 * @see #getEntityListener()
	 * @generated
	 */
	EReference getEntityListener_PrePersist();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.EntityListener#getPostPersist <em>Post Persist</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Post Persist</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityListener#getPostPersist()
	 * @see #getEntityListener()
	 * @generated
	 */
	EReference getEntityListener_PostPersist();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.EntityListener#getPreRemove <em>Pre Remove</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Pre Remove</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityListener#getPreRemove()
	 * @see #getEntityListener()
	 * @generated
	 */
	EReference getEntityListener_PreRemove();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.EntityListener#getPostRemove <em>Post Remove</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Post Remove</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityListener#getPostRemove()
	 * @see #getEntityListener()
	 * @generated
	 */
	EReference getEntityListener_PostRemove();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.EntityListener#getPreUpdate <em>Pre Update</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Pre Update</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityListener#getPreUpdate()
	 * @see #getEntityListener()
	 * @generated
	 */
	EReference getEntityListener_PreUpdate();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.EntityListener#getPostUpdate <em>Post Update</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Post Update</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityListener#getPostUpdate()
	 * @see #getEntityListener()
	 * @generated
	 */
	EReference getEntityListener_PostUpdate();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.EntityListener#getPostLoad <em>Post Load</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Post Load</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityListener#getPostLoad()
	 * @see #getEntityListener()
	 * @generated
	 */
	EReference getEntityListener_PostLoad();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.persistence.eorm.EntityListener#getClass_ <em>Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Class</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityListener#getClass_()
	 * @see #getEntityListener()
	 * @generated
	 */
	EReference getEntityListener_Class();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.EntityListeners <em>Entity Listeners</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Entity Listeners</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityListeners
	 * @generated
	 */
	EClass getEntityListeners();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.EntityListeners#getEntityListener <em>Entity Listener</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Entity Listener</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityListeners#getEntityListener()
	 * @see #getEntityListeners()
	 * @generated
	 */
	EReference getEntityListeners_EntityListener();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.EntityMappings <em>Entity Mappings</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Entity Mappings</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityMappings
	 * @generated
	 */
	EClass getEntityMappings();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityMappings#getDescription()
	 * @see #getEntityMappings()
	 * @generated
	 */
	EAttribute getEntityMappings_Description();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getPersistenceUnitMetadata <em>Persistence Unit Metadata</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Persistence Unit Metadata</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityMappings#getPersistenceUnitMetadata()
	 * @see #getEntityMappings()
	 * @generated
	 */
	EReference getEntityMappings_PersistenceUnitMetadata();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getPackage <em>Package</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Package</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityMappings#getPackage()
	 * @see #getEntityMappings()
	 * @generated
	 */
	EAttribute getEntityMappings_Package();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getSchema <em>Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Schema</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityMappings#getSchema()
	 * @see #getEntityMappings()
	 * @generated
	 */
	EAttribute getEntityMappings_Schema();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getCatalog <em>Catalog</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Catalog</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityMappings#getCatalog()
	 * @see #getEntityMappings()
	 * @generated
	 */
	EAttribute getEntityMappings_Catalog();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getAccess <em>Access</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Access</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityMappings#getAccess()
	 * @see #getEntityMappings()
	 * @generated
	 */
	EAttribute getEntityMappings_Access();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getSequenceGenerator <em>Sequence Generator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Sequence Generator</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityMappings#getSequenceGenerator()
	 * @see #getEntityMappings()
	 * @generated
	 */
	EReference getEntityMappings_SequenceGenerator();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getTableGenerator <em>Table Generator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Table Generator</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityMappings#getTableGenerator()
	 * @see #getEntityMappings()
	 * @generated
	 */
	EReference getEntityMappings_TableGenerator();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getNamedQuery <em>Named Query</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Named Query</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityMappings#getNamedQuery()
	 * @see #getEntityMappings()
	 * @generated
	 */
	EReference getEntityMappings_NamedQuery();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getNamedNativeQuery <em>Named Native Query</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Named Native Query</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityMappings#getNamedNativeQuery()
	 * @see #getEntityMappings()
	 * @generated
	 */
	EReference getEntityMappings_NamedNativeQuery();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getNamedStoredProcedureQuery <em>Named Stored Procedure Query</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Named Stored Procedure Query</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityMappings#getNamedStoredProcedureQuery()
	 * @see #getEntityMappings()
	 * @generated
	 */
	EReference getEntityMappings_NamedStoredProcedureQuery();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getSqlResultSetMapping <em>Sql Result Set Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Sql Result Set Mapping</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityMappings#getSqlResultSetMapping()
	 * @see #getEntityMappings()
	 * @generated
	 */
	EReference getEntityMappings_SqlResultSetMapping();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getMappedSuperclass <em>Mapped Superclass</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Mapped Superclass</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityMappings#getMappedSuperclass()
	 * @see #getEntityMappings()
	 * @generated
	 */
	EReference getEntityMappings_MappedSuperclass();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getEntity <em>Entity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Entity</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityMappings#getEntity()
	 * @see #getEntityMappings()
	 * @generated
	 */
	EReference getEntityMappings_Entity();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getEmbeddable <em>Embeddable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Embeddable</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityMappings#getEmbeddable()
	 * @see #getEntityMappings()
	 * @generated
	 */
	EReference getEntityMappings_Embeddable();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getConverter <em>Converter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Converter</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityMappings#getConverter()
	 * @see #getEntityMappings()
	 * @generated
	 */
	EReference getEntityMappings_Converter();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getVersion <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityMappings#getVersion()
	 * @see #getEntityMappings()
	 * @generated
	 */
	EAttribute getEntityMappings_Version();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityMappings#getName()
	 * @see #getEntityMappings()
	 * @generated
	 */
	EAttribute getEntityMappings_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.EntityResult <em>Entity Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Entity Result</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityResult
	 * @generated
	 */
	EClass getEntityResult();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.EntityResult#getFieldResult <em>Field Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Field Result</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityResult#getFieldResult()
	 * @see #getEntityResult()
	 * @generated
	 */
	EReference getEntityResult_FieldResult();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.EntityResult#getDiscriminatorColumn <em>Discriminator Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Discriminator Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityResult#getDiscriminatorColumn()
	 * @see #getEntityResult()
	 * @generated
	 */
	EAttribute getEntityResult_DiscriminatorColumn();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.EntityResult#getEntityClass <em>Entity Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Entity Class</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EntityResult#getEntityClass()
	 * @see #getEntityResult()
	 * @generated
	 */
	EAttribute getEntityResult_EntityClass();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.FieldResult <em>Field Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Field Result</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.FieldResult
	 * @generated
	 */
	EClass getFieldResult();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.FieldResult#getColumn <em>Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.FieldResult#getColumn()
	 * @see #getFieldResult()
	 * @generated
	 */
	EAttribute getFieldResult_Column();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.FieldResult#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.FieldResult#getName()
	 * @see #getFieldResult()
	 * @generated
	 */
	EAttribute getFieldResult_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.ForeignKey <em>Foreign Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Foreign Key</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ForeignKey
	 * @generated
	 */
	EClass getForeignKey();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.ForeignKey#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ForeignKey#getDescription()
	 * @see #getForeignKey()
	 * @generated
	 */
	EAttribute getForeignKey_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.ForeignKey#getConstraintMode <em>Constraint Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Constraint Mode</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ForeignKey#getConstraintMode()
	 * @see #getForeignKey()
	 * @generated
	 */
	EAttribute getForeignKey_ConstraintMode();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.ForeignKey#getForeignKeyDefinition <em>Foreign Key Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Foreign Key Definition</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ForeignKey#getForeignKeyDefinition()
	 * @see #getForeignKey()
	 * @generated
	 */
	EAttribute getForeignKey_ForeignKeyDefinition();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.ForeignKey#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ForeignKey#getName()
	 * @see #getForeignKey()
	 * @generated
	 */
	EAttribute getForeignKey_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.GeneratedValue <em>Generated Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Generated Value</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.GeneratedValue
	 * @generated
	 */
	EClass getGeneratedValue();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.GeneratedValue#getGenerator <em>Generator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Generator</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.GeneratedValue#getGenerator()
	 * @see #getGeneratedValue()
	 * @generated
	 */
	EAttribute getGeneratedValue_Generator();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.GeneratedValue#getStrategy <em>Strategy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Strategy</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.GeneratedValue#getStrategy()
	 * @see #getGeneratedValue()
	 * @generated
	 */
	EAttribute getGeneratedValue_Strategy();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.ENamedBase <em>ENamed Base</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>ENamed Base</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ENamedBase
	 * @generated
	 */
	EClass getENamedBase();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.ENamedBase#getAccess <em>Access</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Access</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ENamedBase#getAccess()
	 * @see #getENamedBase()
	 * @generated
	 */
	EAttribute getENamedBase_Access();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.ENamedBase#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ENamedBase#getName()
	 * @see #getENamedBase()
	 * @generated
	 */
	EAttribute getENamedBase_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.Base <em>Base</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Base</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Base
	 * @generated
	 */
	EClass getBase();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Base#getColumn <em>Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Base#getColumn()
	 * @see #getBase()
	 * @generated
	 */
	EReference getBase_Column();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Base#getTemporal <em>Temporal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Temporal</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Base#getTemporal()
	 * @see #getBase()
	 * @generated
	 */
	EAttribute getBase_Temporal();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.BaseRef <em>Base Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Base Ref</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.BaseRef
	 * @generated
	 */
	EClass getBaseRef();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.BaseRef#getCascade <em>Cascade</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Cascade</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.BaseRef#getCascade()
	 * @see #getBaseRef()
	 * @generated
	 */
	EReference getBaseRef_Cascade();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.BaseRef#getFetch <em>Fetch</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Fetch</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.BaseRef#getFetch()
	 * @see #getBaseRef()
	 * @generated
	 */
	EAttribute getBaseRef_Fetch();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.BaseRef#getForeignKey <em>Foreign Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Foreign Key</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.BaseRef#getForeignKey()
	 * @see #getBaseRef()
	 * @generated
	 */
	EReference getBaseRef_ForeignKey();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.BaseRef#getJoinTable <em>Join Table</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Join Table</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.BaseRef#getJoinTable()
	 * @see #getBaseRef()
	 * @generated
	 */
	EReference getBaseRef_JoinTable();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.BaseRef#isOptional <em>Optional</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Optional</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.BaseRef#isOptional()
	 * @see #getBaseRef()
	 * @generated
	 */
	EAttribute getBaseRef_Optional();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.BaseRef#isBatch <em>Batch</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Batch</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.BaseRef#isBatch()
	 * @see #getBaseRef()
	 * @generated
	 */
	EAttribute getBaseRef_Batch();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.Id <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Id</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Id
	 * @generated
	 */
	EClass getId();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Id#getGeneratedValue <em>Generated Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Generated Value</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Id#getGeneratedValue()
	 * @see #getId()
	 * @generated
	 */
	EReference getId_GeneratedValue();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Id#getTableGenerator <em>Table Generator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Table Generator</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Id#getTableGenerator()
	 * @see #getId()
	 * @generated
	 */
	EReference getId_TableGenerator();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Id#getSequenceGenerator <em>Sequence Generator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Sequence Generator</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Id#getSequenceGenerator()
	 * @see #getId()
	 * @generated
	 */
	EReference getId_SequenceGenerator();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.IdClass <em>Id Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Id Class</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.IdClass
	 * @generated
	 */
	EClass getIdClass();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.persistence.eorm.IdClass#getClass_ <em>Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Class</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.IdClass#getClass_()
	 * @see #getIdClass()
	 * @generated
	 */
	EReference getIdClass_Class();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.Index <em>Index</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Index</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Index
	 * @generated
	 */
	EClass getIndex();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Index#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Index#getDescription()
	 * @see #getIndex()
	 * @generated
	 */
	EAttribute getIndex_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Index#getColumnList <em>Column List</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Column List</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Index#getColumnList()
	 * @see #getIndex()
	 * @generated
	 */
	EAttribute getIndex_ColumnList();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Index#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Index#getName()
	 * @see #getIndex()
	 * @generated
	 */
	EAttribute getIndex_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Index#isUnique <em>Unique</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Unique</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Index#isUnique()
	 * @see #getIndex()
	 * @generated
	 */
	EAttribute getIndex_Unique();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.Inheritance <em>Inheritance</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Inheritance</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Inheritance
	 * @generated
	 */
	EClass getInheritance();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Inheritance#getStrategy <em>Strategy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Strategy</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Inheritance#getStrategy()
	 * @see #getInheritance()
	 * @generated
	 */
	EAttribute getInheritance_Strategy();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.JoinColumn <em>Join Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Join Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.JoinColumn
	 * @generated
	 */
	EClass getJoinColumn();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.JoinColumn#getColumnDefinition <em>Column Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Column Definition</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.JoinColumn#getColumnDefinition()
	 * @see #getJoinColumn()
	 * @generated
	 */
	EAttribute getJoinColumn_ColumnDefinition();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.JoinColumn#isInsertable <em>Insertable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Insertable</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.JoinColumn#isInsertable()
	 * @see #getJoinColumn()
	 * @generated
	 */
	EAttribute getJoinColumn_Insertable();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.JoinColumn#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.JoinColumn#getName()
	 * @see #getJoinColumn()
	 * @generated
	 */
	EAttribute getJoinColumn_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.JoinColumn#isNullable <em>Nullable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Nullable</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.JoinColumn#isNullable()
	 * @see #getJoinColumn()
	 * @generated
	 */
	EAttribute getJoinColumn_Nullable();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.JoinColumn#getReferencedColumnName <em>Referenced Column Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Referenced Column Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.JoinColumn#getReferencedColumnName()
	 * @see #getJoinColumn()
	 * @generated
	 */
	EAttribute getJoinColumn_ReferencedColumnName();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.JoinColumn#getTable <em>Table</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Table</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.JoinColumn#getTable()
	 * @see #getJoinColumn()
	 * @generated
	 */
	EAttribute getJoinColumn_Table();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.JoinColumn#isUnique <em>Unique</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Unique</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.JoinColumn#isUnique()
	 * @see #getJoinColumn()
	 * @generated
	 */
	EAttribute getJoinColumn_Unique();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.JoinColumn#isUpdatable <em>Updatable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Updatable</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.JoinColumn#isUpdatable()
	 * @see #getJoinColumn()
	 * @generated
	 */
	EAttribute getJoinColumn_Updatable();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.JoinTable <em>Join Table</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Join Table</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.JoinTable
	 * @generated
	 */
	EClass getJoinTable();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.JoinTable#getJoinColumn <em>Join Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Join Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.JoinTable#getJoinColumn()
	 * @see #getJoinTable()
	 * @generated
	 */
	EReference getJoinTable_JoinColumn();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.JoinTable#getForeignKey <em>Foreign Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Foreign Key</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.JoinTable#getForeignKey()
	 * @see #getJoinTable()
	 * @generated
	 */
	EReference getJoinTable_ForeignKey();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.JoinTable#getInverseJoinColumn <em>Inverse Join Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Inverse Join Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.JoinTable#getInverseJoinColumn()
	 * @see #getJoinTable()
	 * @generated
	 */
	EReference getJoinTable_InverseJoinColumn();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.JoinTable#getInverseForeignKey <em>Inverse Foreign Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Inverse Foreign Key</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.JoinTable#getInverseForeignKey()
	 * @see #getJoinTable()
	 * @generated
	 */
	EReference getJoinTable_InverseForeignKey();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.JoinTable#getUniqueConstraint <em>Unique Constraint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Unique Constraint</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.JoinTable#getUniqueConstraint()
	 * @see #getJoinTable()
	 * @generated
	 */
	EReference getJoinTable_UniqueConstraint();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.JoinTable#getIndex <em>Index</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Index</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.JoinTable#getIndex()
	 * @see #getJoinTable()
	 * @generated
	 */
	EReference getJoinTable_Index();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.JoinTable#getCatalog <em>Catalog</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Catalog</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.JoinTable#getCatalog()
	 * @see #getJoinTable()
	 * @generated
	 */
	EAttribute getJoinTable_Catalog();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.JoinTable#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.JoinTable#getName()
	 * @see #getJoinTable()
	 * @generated
	 */
	EAttribute getJoinTable_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.JoinTable#getSchema <em>Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Schema</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.JoinTable#getSchema()
	 * @see #getJoinTable()
	 * @generated
	 */
	EAttribute getJoinTable_Schema();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.Lob <em>Lob</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Lob</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Lob
	 * @generated
	 */
	EClass getLob();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.ManyToMany <em>Many To Many</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Many To Many</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ManyToMany
	 * @generated
	 */
	EClass getManyToMany();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.ManyToMany#getOrderBy <em>Order By</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Order By</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ManyToMany#getOrderBy()
	 * @see #getManyToMany()
	 * @generated
	 */
	EAttribute getManyToMany_OrderBy();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.ManyToMany#getOrderColumn <em>Order Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Order Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ManyToMany#getOrderColumn()
	 * @see #getManyToMany()
	 * @generated
	 */
	EReference getManyToMany_OrderColumn();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.ManyToMany#getMapKey <em>Map Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Map Key</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ManyToMany#getMapKey()
	 * @see #getManyToMany()
	 * @generated
	 */
	EReference getManyToMany_MapKey();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.ManyToMany#getMapKeyClass <em>Map Key Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Map Key Class</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ManyToMany#getMapKeyClass()
	 * @see #getManyToMany()
	 * @generated
	 */
	EReference getManyToMany_MapKeyClass();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.ManyToMany#getMapKeyTemporal <em>Map Key Temporal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Map Key Temporal</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ManyToMany#getMapKeyTemporal()
	 * @see #getManyToMany()
	 * @generated
	 */
	EAttribute getManyToMany_MapKeyTemporal();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.ManyToMany#getMapKeyEnumerated <em>Map Key Enumerated</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Map Key Enumerated</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ManyToMany#getMapKeyEnumerated()
	 * @see #getManyToMany()
	 * @generated
	 */
	EAttribute getManyToMany_MapKeyEnumerated();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.ManyToMany#getMapKeyAttributeOverride <em>Map Key Attribute Override</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Map Key Attribute Override</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ManyToMany#getMapKeyAttributeOverride()
	 * @see #getManyToMany()
	 * @generated
	 */
	EReference getManyToMany_MapKeyAttributeOverride();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.ManyToMany#getMapKeyConvert <em>Map Key Convert</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Map Key Convert</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ManyToMany#getMapKeyConvert()
	 * @see #getManyToMany()
	 * @generated
	 */
	EReference getManyToMany_MapKeyConvert();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.ManyToMany#getMapKeyColumn <em>Map Key Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Map Key Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ManyToMany#getMapKeyColumn()
	 * @see #getManyToMany()
	 * @generated
	 */
	EReference getManyToMany_MapKeyColumn();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.ManyToMany#getMapKeyJoinColumn <em>Map Key Join Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Map Key Join Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ManyToMany#getMapKeyJoinColumn()
	 * @see #getManyToMany()
	 * @generated
	 */
	EReference getManyToMany_MapKeyJoinColumn();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.ManyToMany#getMapKeyForeignKey <em>Map Key Foreign Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Map Key Foreign Key</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ManyToMany#getMapKeyForeignKey()
	 * @see #getManyToMany()
	 * @generated
	 */
	EReference getManyToMany_MapKeyForeignKey();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.ManyToMany#getTargetEntity <em>Target Entity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Target Entity</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ManyToMany#getTargetEntity()
	 * @see #getManyToMany()
	 * @generated
	 */
	EAttribute getManyToMany_TargetEntity();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.ManyToOne <em>Many To One</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Many To One</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ManyToOne
	 * @generated
	 */
	EClass getManyToOne();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.ManyToOne#getJoinColumn <em>Join Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Join Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ManyToOne#getJoinColumn()
	 * @see #getManyToOne()
	 * @generated
	 */
	EReference getManyToOne_JoinColumn();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.ManyToOne#isId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ManyToOne#isId()
	 * @see #getManyToOne()
	 * @generated
	 */
	EAttribute getManyToOne_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.ManyToOne#getMapsId <em>Maps Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Maps Id</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ManyToOne#getMapsId()
	 * @see #getManyToOne()
	 * @generated
	 */
	EAttribute getManyToOne_MapsId();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.ManyToOne#getTargetEntity <em>Target Entity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Target Entity</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ManyToOne#getTargetEntity()
	 * @see #getManyToOne()
	 * @generated
	 */
	EAttribute getManyToOne_TargetEntity();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.MapKey <em>Map Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Map Key</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MapKey
	 * @generated
	 */
	EClass getMapKey();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.MapKey#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MapKey#getName()
	 * @see #getMapKey()
	 * @generated
	 */
	EAttribute getMapKey_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.MapKeyClass <em>Map Key Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Map Key Class</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MapKeyClass
	 * @generated
	 */
	EClass getMapKeyClass();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.persistence.eorm.MapKeyClass#getClass_ <em>Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Class</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MapKeyClass#getClass_()
	 * @see #getMapKeyClass()
	 * @generated
	 */
	EReference getMapKeyClass_Class();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.MapKeyColumn <em>Map Key Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Map Key Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MapKeyColumn
	 * @generated
	 */
	EClass getMapKeyColumn();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.MapKeyColumn#getColumnDefinition <em>Column Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Column Definition</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MapKeyColumn#getColumnDefinition()
	 * @see #getMapKeyColumn()
	 * @generated
	 */
	EAttribute getMapKeyColumn_ColumnDefinition();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.MapKeyColumn#isInsertable <em>Insertable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Insertable</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MapKeyColumn#isInsertable()
	 * @see #getMapKeyColumn()
	 * @generated
	 */
	EAttribute getMapKeyColumn_Insertable();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.MapKeyColumn#getLength <em>Length</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Length</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MapKeyColumn#getLength()
	 * @see #getMapKeyColumn()
	 * @generated
	 */
	EAttribute getMapKeyColumn_Length();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.MapKeyColumn#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MapKeyColumn#getName()
	 * @see #getMapKeyColumn()
	 * @generated
	 */
	EAttribute getMapKeyColumn_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.MapKeyColumn#isNullable <em>Nullable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Nullable</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MapKeyColumn#isNullable()
	 * @see #getMapKeyColumn()
	 * @generated
	 */
	EAttribute getMapKeyColumn_Nullable();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.MapKeyColumn#getPrecision <em>Precision</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Precision</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MapKeyColumn#getPrecision()
	 * @see #getMapKeyColumn()
	 * @generated
	 */
	EAttribute getMapKeyColumn_Precision();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.MapKeyColumn#getScale <em>Scale</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Scale</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MapKeyColumn#getScale()
	 * @see #getMapKeyColumn()
	 * @generated
	 */
	EAttribute getMapKeyColumn_Scale();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.MapKeyColumn#getTable <em>Table</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Table</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MapKeyColumn#getTable()
	 * @see #getMapKeyColumn()
	 * @generated
	 */
	EAttribute getMapKeyColumn_Table();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.MapKeyColumn#isUnique <em>Unique</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Unique</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MapKeyColumn#isUnique()
	 * @see #getMapKeyColumn()
	 * @generated
	 */
	EAttribute getMapKeyColumn_Unique();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.MapKeyColumn#isUpdatable <em>Updatable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Updatable</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MapKeyColumn#isUpdatable()
	 * @see #getMapKeyColumn()
	 * @generated
	 */
	EAttribute getMapKeyColumn_Updatable();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.MapKeyJoinColumn <em>Map Key Join Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Map Key Join Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MapKeyJoinColumn
	 * @generated
	 */
	EClass getMapKeyJoinColumn();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.MapKeyJoinColumn#getColumnDefinition <em>Column Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Column Definition</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MapKeyJoinColumn#getColumnDefinition()
	 * @see #getMapKeyJoinColumn()
	 * @generated
	 */
	EAttribute getMapKeyJoinColumn_ColumnDefinition();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.MapKeyJoinColumn#isInsertable <em>Insertable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Insertable</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MapKeyJoinColumn#isInsertable()
	 * @see #getMapKeyJoinColumn()
	 * @generated
	 */
	EAttribute getMapKeyJoinColumn_Insertable();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.MapKeyJoinColumn#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MapKeyJoinColumn#getName()
	 * @see #getMapKeyJoinColumn()
	 * @generated
	 */
	EAttribute getMapKeyJoinColumn_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.MapKeyJoinColumn#isNullable <em>Nullable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Nullable</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MapKeyJoinColumn#isNullable()
	 * @see #getMapKeyJoinColumn()
	 * @generated
	 */
	EAttribute getMapKeyJoinColumn_Nullable();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.MapKeyJoinColumn#getReferencedColumnName <em>Referenced Column Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Referenced Column Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MapKeyJoinColumn#getReferencedColumnName()
	 * @see #getMapKeyJoinColumn()
	 * @generated
	 */
	EAttribute getMapKeyJoinColumn_ReferencedColumnName();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.MapKeyJoinColumn#getTable <em>Table</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Table</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MapKeyJoinColumn#getTable()
	 * @see #getMapKeyJoinColumn()
	 * @generated
	 */
	EAttribute getMapKeyJoinColumn_Table();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.MapKeyJoinColumn#isUnique <em>Unique</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Unique</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MapKeyJoinColumn#isUnique()
	 * @see #getMapKeyJoinColumn()
	 * @generated
	 */
	EAttribute getMapKeyJoinColumn_Unique();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.MapKeyJoinColumn#isUpdatable <em>Updatable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Updatable</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MapKeyJoinColumn#isUpdatable()
	 * @see #getMapKeyJoinColumn()
	 * @generated
	 */
	EAttribute getMapKeyJoinColumn_Updatable();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass <em>Mapped Superclass</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Mapped Superclass</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MappedSuperclass
	 * @generated
	 */
	EClass getMappedSuperclass();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MappedSuperclass#getDescription()
	 * @see #getMappedSuperclass()
	 * @generated
	 */
	EAttribute getMappedSuperclass_Description();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getIdClass <em>Id Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Id Class</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MappedSuperclass#getIdClass()
	 * @see #getMappedSuperclass()
	 * @generated
	 */
	EReference getMappedSuperclass_IdClass();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getExcludeDefaultListeners <em>Exclude Default Listeners</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Exclude Default Listeners</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MappedSuperclass#getExcludeDefaultListeners()
	 * @see #getMappedSuperclass()
	 * @generated
	 */
	EReference getMappedSuperclass_ExcludeDefaultListeners();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getExcludeSuperclassListeners <em>Exclude Superclass Listeners</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Exclude Superclass Listeners</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MappedSuperclass#getExcludeSuperclassListeners()
	 * @see #getMappedSuperclass()
	 * @generated
	 */
	EReference getMappedSuperclass_ExcludeSuperclassListeners();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getEntityListeners <em>Entity Listeners</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Entity Listeners</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MappedSuperclass#getEntityListeners()
	 * @see #getMappedSuperclass()
	 * @generated
	 */
	EReference getMappedSuperclass_EntityListeners();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPrePersist <em>Pre Persist</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Pre Persist</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPrePersist()
	 * @see #getMappedSuperclass()
	 * @generated
	 */
	EReference getMappedSuperclass_PrePersist();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPostPersist <em>Post Persist</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Post Persist</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPostPersist()
	 * @see #getMappedSuperclass()
	 * @generated
	 */
	EReference getMappedSuperclass_PostPersist();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPreRemove <em>Pre Remove</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Pre Remove</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPreRemove()
	 * @see #getMappedSuperclass()
	 * @generated
	 */
	EReference getMappedSuperclass_PreRemove();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPostRemove <em>Post Remove</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Post Remove</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPostRemove()
	 * @see #getMappedSuperclass()
	 * @generated
	 */
	EReference getMappedSuperclass_PostRemove();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPreUpdate <em>Pre Update</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Pre Update</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPreUpdate()
	 * @see #getMappedSuperclass()
	 * @generated
	 */
	EReference getMappedSuperclass_PreUpdate();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPostUpdate <em>Post Update</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Post Update</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPostUpdate()
	 * @see #getMappedSuperclass()
	 * @generated
	 */
	EReference getMappedSuperclass_PostUpdate();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPostLoad <em>Post Load</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Post Load</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MappedSuperclass#getPostLoad()
	 * @see #getMappedSuperclass()
	 * @generated
	 */
	EReference getMappedSuperclass_PostLoad();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getAttributes <em>Attributes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Attributes</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MappedSuperclass#getAttributes()
	 * @see #getMappedSuperclass()
	 * @generated
	 */
	EReference getMappedSuperclass_Attributes();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getAccess <em>Access</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Access</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MappedSuperclass#getAccess()
	 * @see #getMappedSuperclass()
	 * @generated
	 */
	EAttribute getMappedSuperclass_Access();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#getClass_ <em>Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Class</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MappedSuperclass#getClass_()
	 * @see #getMappedSuperclass()
	 * @generated
	 */
	EReference getMappedSuperclass_Class();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.MappedSuperclass#isMetadataComplete <em>Metadata Complete</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Metadata Complete</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MappedSuperclass#isMetadataComplete()
	 * @see #getMappedSuperclass()
	 * @generated
	 */
	EAttribute getMappedSuperclass_MetadataComplete();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.NamedAttributeNode <em>Named Attribute Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Named Attribute Node</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedAttributeNode
	 * @generated
	 */
	EClass getNamedAttributeNode();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.NamedAttributeNode#getKeySubgraph <em>Key Subgraph</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key Subgraph</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedAttributeNode#getKeySubgraph()
	 * @see #getNamedAttributeNode()
	 * @generated
	 */
	EAttribute getNamedAttributeNode_KeySubgraph();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.NamedAttributeNode#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedAttributeNode#getName()
	 * @see #getNamedAttributeNode()
	 * @generated
	 */
	EAttribute getNamedAttributeNode_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.NamedAttributeNode#getSubgraph <em>Subgraph</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Subgraph</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedAttributeNode#getSubgraph()
	 * @see #getNamedAttributeNode()
	 * @generated
	 */
	EAttribute getNamedAttributeNode_Subgraph();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.NamedEntityGraph <em>Named Entity Graph</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Named Entity Graph</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedEntityGraph
	 * @generated
	 */
	EClass getNamedEntityGraph();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.NamedEntityGraph#getNamedAttributeNode <em>Named Attribute Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Named Attribute Node</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedEntityGraph#getNamedAttributeNode()
	 * @see #getNamedEntityGraph()
	 * @generated
	 */
	EReference getNamedEntityGraph_NamedAttributeNode();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.NamedEntityGraph#getSubgraph <em>Subgraph</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Subgraph</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedEntityGraph#getSubgraph()
	 * @see #getNamedEntityGraph()
	 * @generated
	 */
	EReference getNamedEntityGraph_Subgraph();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.NamedEntityGraph#getSubclassSubgraph <em>Subclass Subgraph</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Subclass Subgraph</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedEntityGraph#getSubclassSubgraph()
	 * @see #getNamedEntityGraph()
	 * @generated
	 */
	EReference getNamedEntityGraph_SubclassSubgraph();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.NamedEntityGraph#isIncludeAllAttributes <em>Include All Attributes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Include All Attributes</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedEntityGraph#isIncludeAllAttributes()
	 * @see #getNamedEntityGraph()
	 * @generated
	 */
	EAttribute getNamedEntityGraph_IncludeAllAttributes();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.NamedEntityGraph#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedEntityGraph#getName()
	 * @see #getNamedEntityGraph()
	 * @generated
	 */
	EAttribute getNamedEntityGraph_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.NamedNativeQuery <em>Named Native Query</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Named Native Query</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedNativeQuery
	 * @generated
	 */
	EClass getNamedNativeQuery();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.NamedNativeQuery#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedNativeQuery#getDescription()
	 * @see #getNamedNativeQuery()
	 * @generated
	 */
	EAttribute getNamedNativeQuery_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.NamedNativeQuery#getQuery <em>Query</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Query</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedNativeQuery#getQuery()
	 * @see #getNamedNativeQuery()
	 * @generated
	 */
	EAttribute getNamedNativeQuery_Query();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.NamedNativeQuery#getHint <em>Hint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Hint</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedNativeQuery#getHint()
	 * @see #getNamedNativeQuery()
	 * @generated
	 */
	EReference getNamedNativeQuery_Hint();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.NamedNativeQuery#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedNativeQuery#getName()
	 * @see #getNamedNativeQuery()
	 * @generated
	 */
	EAttribute getNamedNativeQuery_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.NamedNativeQuery#getResultClass <em>Result Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Result Class</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedNativeQuery#getResultClass()
	 * @see #getNamedNativeQuery()
	 * @generated
	 */
	EAttribute getNamedNativeQuery_ResultClass();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.NamedNativeQuery#getResultSetMapping <em>Result Set Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Result Set Mapping</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedNativeQuery#getResultSetMapping()
	 * @see #getNamedNativeQuery()
	 * @generated
	 */
	EAttribute getNamedNativeQuery_ResultSetMapping();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.NamedQuery <em>Named Query</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Named Query</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedQuery
	 * @generated
	 */
	EClass getNamedQuery();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.NamedQuery#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedQuery#getDescription()
	 * @see #getNamedQuery()
	 * @generated
	 */
	EAttribute getNamedQuery_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.NamedQuery#getQuery <em>Query</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Query</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedQuery#getQuery()
	 * @see #getNamedQuery()
	 * @generated
	 */
	EAttribute getNamedQuery_Query();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.NamedQuery#getLockMode <em>Lock Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Lock Mode</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedQuery#getLockMode()
	 * @see #getNamedQuery()
	 * @generated
	 */
	EAttribute getNamedQuery_LockMode();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.NamedQuery#getHint <em>Hint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Hint</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedQuery#getHint()
	 * @see #getNamedQuery()
	 * @generated
	 */
	EReference getNamedQuery_Hint();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.NamedQuery#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedQuery#getName()
	 * @see #getNamedQuery()
	 * @generated
	 */
	EAttribute getNamedQuery_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery <em>Named Stored Procedure Query</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Named Stored Procedure Query</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery
	 * @generated
	 */
	EClass getNamedStoredProcedureQuery();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery#getDescription()
	 * @see #getNamedStoredProcedureQuery()
	 * @generated
	 */
	EAttribute getNamedStoredProcedureQuery_Description();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery#getParameter <em>Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameter</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery#getParameter()
	 * @see #getNamedStoredProcedureQuery()
	 * @generated
	 */
	EReference getNamedStoredProcedureQuery_Parameter();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery#getResultClass <em>Result Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Result Class</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery#getResultClass()
	 * @see #getNamedStoredProcedureQuery()
	 * @generated
	 */
	EAttribute getNamedStoredProcedureQuery_ResultClass();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery#getResultSetMapping <em>Result Set Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Result Set Mapping</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery#getResultSetMapping()
	 * @see #getNamedStoredProcedureQuery()
	 * @generated
	 */
	EAttribute getNamedStoredProcedureQuery_ResultSetMapping();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery#getHint <em>Hint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Hint</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery#getHint()
	 * @see #getNamedStoredProcedureQuery()
	 * @generated
	 */
	EReference getNamedStoredProcedureQuery_Hint();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery#getName()
	 * @see #getNamedStoredProcedureQuery()
	 * @generated
	 */
	EAttribute getNamedStoredProcedureQuery_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery#getProcedureName <em>Procedure Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Procedure Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery#getProcedureName()
	 * @see #getNamedStoredProcedureQuery()
	 * @generated
	 */
	EAttribute getNamedStoredProcedureQuery_ProcedureName();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.NamedSubgraph <em>Named Subgraph</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Named Subgraph</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedSubgraph
	 * @generated
	 */
	EClass getNamedSubgraph();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.NamedSubgraph#getNamedAttributeNode <em>Named Attribute Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Named Attribute Node</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedSubgraph#getNamedAttributeNode()
	 * @see #getNamedSubgraph()
	 * @generated
	 */
	EReference getNamedSubgraph_NamedAttributeNode();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.NamedSubgraph#getClass_ <em>Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Class</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedSubgraph#getClass_()
	 * @see #getNamedSubgraph()
	 * @generated
	 */
	EAttribute getNamedSubgraph_Class();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.NamedSubgraph#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.NamedSubgraph#getName()
	 * @see #getNamedSubgraph()
	 * @generated
	 */
	EAttribute getNamedSubgraph_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.OneToMany <em>One To Many</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>One To Many</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.OneToMany
	 * @generated
	 */
	EClass getOneToMany();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.OneToMany#getOrderBy <em>Order By</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Order By</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.OneToMany#getOrderBy()
	 * @see #getOneToMany()
	 * @generated
	 */
	EAttribute getOneToMany_OrderBy();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.OneToMany#getOrderColumn <em>Order Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Order Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.OneToMany#getOrderColumn()
	 * @see #getOneToMany()
	 * @generated
	 */
	EReference getOneToMany_OrderColumn();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.OneToMany#getMapKey <em>Map Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Map Key</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.OneToMany#getMapKey()
	 * @see #getOneToMany()
	 * @generated
	 */
	EReference getOneToMany_MapKey();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyClass <em>Map Key Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Map Key Class</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyClass()
	 * @see #getOneToMany()
	 * @generated
	 */
	EReference getOneToMany_MapKeyClass();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyTemporal <em>Map Key Temporal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Map Key Temporal</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyTemporal()
	 * @see #getOneToMany()
	 * @generated
	 */
	EAttribute getOneToMany_MapKeyTemporal();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyEnumerated <em>Map Key Enumerated</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Map Key Enumerated</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyEnumerated()
	 * @see #getOneToMany()
	 * @generated
	 */
	EAttribute getOneToMany_MapKeyEnumerated();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyAttributeOverride <em>Map Key Attribute Override</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Map Key Attribute Override</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyAttributeOverride()
	 * @see #getOneToMany()
	 * @generated
	 */
	EReference getOneToMany_MapKeyAttributeOverride();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyConvert <em>Map Key Convert</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Map Key Convert</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyConvert()
	 * @see #getOneToMany()
	 * @generated
	 */
	EReference getOneToMany_MapKeyConvert();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyColumn <em>Map Key Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Map Key Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyColumn()
	 * @see #getOneToMany()
	 * @generated
	 */
	EReference getOneToMany_MapKeyColumn();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyJoinColumn <em>Map Key Join Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Map Key Join Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyJoinColumn()
	 * @see #getOneToMany()
	 * @generated
	 */
	EReference getOneToMany_MapKeyJoinColumn();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyForeignKey <em>Map Key Foreign Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Map Key Foreign Key</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyForeignKey()
	 * @see #getOneToMany()
	 * @generated
	 */
	EReference getOneToMany_MapKeyForeignKey();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.OneToMany#getJoinColumn <em>Join Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Join Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.OneToMany#getJoinColumn()
	 * @see #getOneToMany()
	 * @generated
	 */
	EReference getOneToMany_JoinColumn();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.OneToMany#isOrphanRemoval <em>Orphan Removal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Orphan Removal</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.OneToMany#isOrphanRemoval()
	 * @see #getOneToMany()
	 * @generated
	 */
	EAttribute getOneToMany_OrphanRemoval();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.OneToMany#getTargetEntity <em>Target Entity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Target Entity</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.OneToMany#getTargetEntity()
	 * @see #getOneToMany()
	 * @generated
	 */
	EAttribute getOneToMany_TargetEntity();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.OneToOne <em>One To One</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>One To One</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.OneToOne
	 * @generated
	 */
	EClass getOneToOne();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.OneToOne#getPrimaryKeyJoinColumn <em>Primary Key Join Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Primary Key Join Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.OneToOne#getPrimaryKeyJoinColumn()
	 * @see #getOneToOne()
	 * @generated
	 */
	EReference getOneToOne_PrimaryKeyJoinColumn();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.OneToOne#getPrimaryKeyForeignKey <em>Primary Key Foreign Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Primary Key Foreign Key</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.OneToOne#getPrimaryKeyForeignKey()
	 * @see #getOneToOne()
	 * @generated
	 */
	EReference getOneToOne_PrimaryKeyForeignKey();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.OneToOne#getJoinColumn <em>Join Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Join Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.OneToOne#getJoinColumn()
	 * @see #getOneToOne()
	 * @generated
	 */
	EReference getOneToOne_JoinColumn();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.OneToOne#isId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.OneToOne#isId()
	 * @see #getOneToOne()
	 * @generated
	 */
	EAttribute getOneToOne_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.OneToOne#getMapsId <em>Maps Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Maps Id</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.OneToOne#getMapsId()
	 * @see #getOneToOne()
	 * @generated
	 */
	EAttribute getOneToOne_MapsId();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.OneToOne#isOrphanRemoval <em>Orphan Removal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Orphan Removal</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.OneToOne#isOrphanRemoval()
	 * @see #getOneToOne()
	 * @generated
	 */
	EAttribute getOneToOne_OrphanRemoval();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.OneToOne#getTargetEntity <em>Target Entity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Target Entity</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.OneToOne#getTargetEntity()
	 * @see #getOneToOne()
	 * @generated
	 */
	EAttribute getOneToOne_TargetEntity();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.OrderColumn <em>Order Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Order Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.OrderColumn
	 * @generated
	 */
	EClass getOrderColumn();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.PersistenceUnitDefaults <em>Persistence Unit Defaults</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Persistence Unit Defaults</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PersistenceUnitDefaults
	 * @generated
	 */
	EClass getPersistenceUnitDefaults();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.PersistenceUnitDefaults#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PersistenceUnitDefaults#getDescription()
	 * @see #getPersistenceUnitDefaults()
	 * @generated
	 */
	EAttribute getPersistenceUnitDefaults_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.PersistenceUnitDefaults#getSchema <em>Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Schema</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PersistenceUnitDefaults#getSchema()
	 * @see #getPersistenceUnitDefaults()
	 * @generated
	 */
	EAttribute getPersistenceUnitDefaults_Schema();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.PersistenceUnitDefaults#getCatalog <em>Catalog</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Catalog</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PersistenceUnitDefaults#getCatalog()
	 * @see #getPersistenceUnitDefaults()
	 * @generated
	 */
	EAttribute getPersistenceUnitDefaults_Catalog();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.PersistenceUnitDefaults#getDelimitedIdentifiers <em>Delimited Identifiers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Delimited Identifiers</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PersistenceUnitDefaults#getDelimitedIdentifiers()
	 * @see #getPersistenceUnitDefaults()
	 * @generated
	 */
	EReference getPersistenceUnitDefaults_DelimitedIdentifiers();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.PersistenceUnitDefaults#getAccess <em>Access</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Access</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PersistenceUnitDefaults#getAccess()
	 * @see #getPersistenceUnitDefaults()
	 * @generated
	 */
	EAttribute getPersistenceUnitDefaults_Access();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.PersistenceUnitDefaults#getCascadePersist <em>Cascade Persist</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Cascade Persist</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PersistenceUnitDefaults#getCascadePersist()
	 * @see #getPersistenceUnitDefaults()
	 * @generated
	 */
	EReference getPersistenceUnitDefaults_CascadePersist();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.PersistenceUnitDefaults#getEntityListeners <em>Entity Listeners</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Entity Listeners</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PersistenceUnitDefaults#getEntityListeners()
	 * @see #getPersistenceUnitDefaults()
	 * @generated
	 */
	EReference getPersistenceUnitDefaults_EntityListeners();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.PersistenceUnitMetadata <em>Persistence Unit Metadata</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Persistence Unit Metadata</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PersistenceUnitMetadata
	 * @generated
	 */
	EClass getPersistenceUnitMetadata();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.PersistenceUnitMetadata#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PersistenceUnitMetadata#getDescription()
	 * @see #getPersistenceUnitMetadata()
	 * @generated
	 */
	EAttribute getPersistenceUnitMetadata_Description();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.PersistenceUnitMetadata#getXmlMappingMetadataComplete <em>Xml Mapping Metadata Complete</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Xml Mapping Metadata Complete</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PersistenceUnitMetadata#getXmlMappingMetadataComplete()
	 * @see #getPersistenceUnitMetadata()
	 * @generated
	 */
	EReference getPersistenceUnitMetadata_XmlMappingMetadataComplete();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.PersistenceUnitMetadata#getPersistenceUnitDefaults <em>Persistence Unit Defaults</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Persistence Unit Defaults</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PersistenceUnitMetadata#getPersistenceUnitDefaults()
	 * @see #getPersistenceUnitMetadata()
	 * @generated
	 */
	EReference getPersistenceUnitMetadata_PersistenceUnitDefaults();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.PostLoad <em>Post Load</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Post Load</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PostLoad
	 * @generated
	 */
	EClass getPostLoad();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.PostLoad#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PostLoad#getDescription()
	 * @see #getPostLoad()
	 * @generated
	 */
	EAttribute getPostLoad_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.PostLoad#getMethodName <em>Method Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Method Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PostLoad#getMethodName()
	 * @see #getPostLoad()
	 * @generated
	 */
	EAttribute getPostLoad_MethodName();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.PostPersist <em>Post Persist</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Post Persist</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PostPersist
	 * @generated
	 */
	EClass getPostPersist();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.PostPersist#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PostPersist#getDescription()
	 * @see #getPostPersist()
	 * @generated
	 */
	EAttribute getPostPersist_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.PostPersist#getMethodName <em>Method Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Method Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PostPersist#getMethodName()
	 * @see #getPostPersist()
	 * @generated
	 */
	EAttribute getPostPersist_MethodName();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.PostRemove <em>Post Remove</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Post Remove</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PostRemove
	 * @generated
	 */
	EClass getPostRemove();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.PostRemove#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PostRemove#getDescription()
	 * @see #getPostRemove()
	 * @generated
	 */
	EAttribute getPostRemove_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.PostRemove#getMethodName <em>Method Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Method Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PostRemove#getMethodName()
	 * @see #getPostRemove()
	 * @generated
	 */
	EAttribute getPostRemove_MethodName();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.PostUpdate <em>Post Update</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Post Update</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PostUpdate
	 * @generated
	 */
	EClass getPostUpdate();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.PostUpdate#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PostUpdate#getDescription()
	 * @see #getPostUpdate()
	 * @generated
	 */
	EAttribute getPostUpdate_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.PostUpdate#getMethodName <em>Method Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Method Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PostUpdate#getMethodName()
	 * @see #getPostUpdate()
	 * @generated
	 */
	EAttribute getPostUpdate_MethodName();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.PrePersist <em>Pre Persist</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Pre Persist</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PrePersist
	 * @generated
	 */
	EClass getPrePersist();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.PrePersist#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PrePersist#getDescription()
	 * @see #getPrePersist()
	 * @generated
	 */
	EAttribute getPrePersist_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.PrePersist#getMethodName <em>Method Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Method Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PrePersist#getMethodName()
	 * @see #getPrePersist()
	 * @generated
	 */
	EAttribute getPrePersist_MethodName();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.PreRemove <em>Pre Remove</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Pre Remove</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PreRemove
	 * @generated
	 */
	EClass getPreRemove();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.PreRemove#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PreRemove#getDescription()
	 * @see #getPreRemove()
	 * @generated
	 */
	EAttribute getPreRemove_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.PreRemove#getMethodName <em>Method Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Method Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PreRemove#getMethodName()
	 * @see #getPreRemove()
	 * @generated
	 */
	EAttribute getPreRemove_MethodName();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.PreUpdate <em>Pre Update</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Pre Update</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PreUpdate
	 * @generated
	 */
	EClass getPreUpdate();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.PreUpdate#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PreUpdate#getDescription()
	 * @see #getPreUpdate()
	 * @generated
	 */
	EAttribute getPreUpdate_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.PreUpdate#getMethodName <em>Method Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Method Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PreUpdate#getMethodName()
	 * @see #getPreUpdate()
	 * @generated
	 */
	EAttribute getPreUpdate_MethodName();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.PrimaryKeyJoinColumn <em>Primary Key Join Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Primary Key Join Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PrimaryKeyJoinColumn
	 * @generated
	 */
	EClass getPrimaryKeyJoinColumn();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.PrimaryKeyJoinColumn#getColumnDefinition <em>Column Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Column Definition</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PrimaryKeyJoinColumn#getColumnDefinition()
	 * @see #getPrimaryKeyJoinColumn()
	 * @generated
	 */
	EAttribute getPrimaryKeyJoinColumn_ColumnDefinition();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.PrimaryKeyJoinColumn#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PrimaryKeyJoinColumn#getName()
	 * @see #getPrimaryKeyJoinColumn()
	 * @generated
	 */
	EAttribute getPrimaryKeyJoinColumn_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.PrimaryKeyJoinColumn#getReferencedColumnName <em>Referenced Column Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Referenced Column Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.PrimaryKeyJoinColumn#getReferencedColumnName()
	 * @see #getPrimaryKeyJoinColumn()
	 * @generated
	 */
	EAttribute getPrimaryKeyJoinColumn_ReferencedColumnName();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.QueryHint <em>Query Hint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Query Hint</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.QueryHint
	 * @generated
	 */
	EClass getQueryHint();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.QueryHint#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.QueryHint#getDescription()
	 * @see #getQueryHint()
	 * @generated
	 */
	EAttribute getQueryHint_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.QueryHint#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.QueryHint#getName()
	 * @see #getQueryHint()
	 * @generated
	 */
	EAttribute getQueryHint_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.QueryHint#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.QueryHint#getValue()
	 * @see #getQueryHint()
	 * @generated
	 */
	EAttribute getQueryHint_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.SecondaryTable <em>Secondary Table</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Secondary Table</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SecondaryTable
	 * @generated
	 */
	EClass getSecondaryTable();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.SecondaryTable#getPrimaryKeyJoinColumn <em>Primary Key Join Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Primary Key Join Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SecondaryTable#getPrimaryKeyJoinColumn()
	 * @see #getSecondaryTable()
	 * @generated
	 */
	EReference getSecondaryTable_PrimaryKeyJoinColumn();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.SecondaryTable#getPrimaryKeyForeignKey <em>Primary Key Foreign Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Primary Key Foreign Key</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SecondaryTable#getPrimaryKeyForeignKey()
	 * @see #getSecondaryTable()
	 * @generated
	 */
	EReference getSecondaryTable_PrimaryKeyForeignKey();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.SecondaryTable#getUniqueConstraint <em>Unique Constraint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Unique Constraint</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SecondaryTable#getUniqueConstraint()
	 * @see #getSecondaryTable()
	 * @generated
	 */
	EReference getSecondaryTable_UniqueConstraint();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.SecondaryTable#getIndex <em>Index</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Index</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SecondaryTable#getIndex()
	 * @see #getSecondaryTable()
	 * @generated
	 */
	EReference getSecondaryTable_Index();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.SecondaryTable#getCatalog <em>Catalog</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Catalog</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SecondaryTable#getCatalog()
	 * @see #getSecondaryTable()
	 * @generated
	 */
	EAttribute getSecondaryTable_Catalog();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.SecondaryTable#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SecondaryTable#getName()
	 * @see #getSecondaryTable()
	 * @generated
	 */
	EAttribute getSecondaryTable_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.SecondaryTable#getSchema <em>Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Schema</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SecondaryTable#getSchema()
	 * @see #getSecondaryTable()
	 * @generated
	 */
	EAttribute getSecondaryTable_Schema();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.SequenceGenerator <em>Sequence Generator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Sequence Generator</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SequenceGenerator
	 * @generated
	 */
	EClass getSequenceGenerator();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.SequenceGenerator#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SequenceGenerator#getDescription()
	 * @see #getSequenceGenerator()
	 * @generated
	 */
	EAttribute getSequenceGenerator_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.SequenceGenerator#getAllocationSize <em>Allocation Size</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Allocation Size</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SequenceGenerator#getAllocationSize()
	 * @see #getSequenceGenerator()
	 * @generated
	 */
	EAttribute getSequenceGenerator_AllocationSize();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.SequenceGenerator#getCatalog <em>Catalog</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Catalog</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SequenceGenerator#getCatalog()
	 * @see #getSequenceGenerator()
	 * @generated
	 */
	EAttribute getSequenceGenerator_Catalog();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.SequenceGenerator#getInitialValue <em>Initial Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Initial Value</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SequenceGenerator#getInitialValue()
	 * @see #getSequenceGenerator()
	 * @generated
	 */
	EAttribute getSequenceGenerator_InitialValue();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.SequenceGenerator#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SequenceGenerator#getName()
	 * @see #getSequenceGenerator()
	 * @generated
	 */
	EAttribute getSequenceGenerator_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.SequenceGenerator#getSchema <em>Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Schema</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SequenceGenerator#getSchema()
	 * @see #getSequenceGenerator()
	 * @generated
	 */
	EAttribute getSequenceGenerator_Schema();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.SequenceGenerator#getSequenceName <em>Sequence Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Sequence Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SequenceGenerator#getSequenceName()
	 * @see #getSequenceGenerator()
	 * @generated
	 */
	EAttribute getSequenceGenerator_SequenceName();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.SimpleBase <em>Simple Base</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Simple Base</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SimpleBase
	 * @generated
	 */
	EClass getSimpleBase();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.SimpleBase#getLob <em>Lob</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Lob</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SimpleBase#getLob()
	 * @see #getSimpleBase()
	 * @generated
	 */
	EReference getSimpleBase_Lob();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.SimpleBase#getConvert <em>Convert</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Convert</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SimpleBase#getConvert()
	 * @see #getSimpleBase()
	 * @generated
	 */
	EReference getSimpleBase_Convert();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.SimpleBase#getFetch <em>Fetch</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Fetch</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SimpleBase#getFetch()
	 * @see #getSimpleBase()
	 * @generated
	 */
	EAttribute getSimpleBase_Fetch();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.persistence.eorm.SimpleBase#getEnumerated <em>Enumerated</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Enumerated</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SimpleBase#getEnumerated()
	 * @see #getSimpleBase()
	 * @generated
	 */
	EReference getSimpleBase_Enumerated();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.SqlResultSetMapping <em>Sql Result Set Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Sql Result Set Mapping</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SqlResultSetMapping
	 * @generated
	 */
	EClass getSqlResultSetMapping();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.SqlResultSetMapping#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SqlResultSetMapping#getDescription()
	 * @see #getSqlResultSetMapping()
	 * @generated
	 */
	EAttribute getSqlResultSetMapping_Description();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.SqlResultSetMapping#getEntityResult <em>Entity Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Entity Result</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SqlResultSetMapping#getEntityResult()
	 * @see #getSqlResultSetMapping()
	 * @generated
	 */
	EReference getSqlResultSetMapping_EntityResult();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.SqlResultSetMapping#getConstructorResult <em>Constructor Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Constructor Result</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SqlResultSetMapping#getConstructorResult()
	 * @see #getSqlResultSetMapping()
	 * @generated
	 */
	EReference getSqlResultSetMapping_ConstructorResult();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.SqlResultSetMapping#getColumnResult <em>Column Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Column Result</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SqlResultSetMapping#getColumnResult()
	 * @see #getSqlResultSetMapping()
	 * @generated
	 */
	EReference getSqlResultSetMapping_ColumnResult();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.SqlResultSetMapping#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.SqlResultSetMapping#getName()
	 * @see #getSqlResultSetMapping()
	 * @generated
	 */
	EAttribute getSqlResultSetMapping_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.StoredProcedureParameter <em>Stored Procedure Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Stored Procedure Parameter</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.StoredProcedureParameter
	 * @generated
	 */
	EClass getStoredProcedureParameter();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.StoredProcedureParameter#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.StoredProcedureParameter#getDescription()
	 * @see #getStoredProcedureParameter()
	 * @generated
	 */
	EAttribute getStoredProcedureParameter_Description();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.persistence.eorm.StoredProcedureParameter#getClass_ <em>Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Class</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.StoredProcedureParameter#getClass_()
	 * @see #getStoredProcedureParameter()
	 * @generated
	 */
	EReference getStoredProcedureParameter_Class();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.StoredProcedureParameter#getMode <em>Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mode</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.StoredProcedureParameter#getMode()
	 * @see #getStoredProcedureParameter()
	 * @generated
	 */
	EAttribute getStoredProcedureParameter_Mode();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.StoredProcedureParameter#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.StoredProcedureParameter#getName()
	 * @see #getStoredProcedureParameter()
	 * @generated
	 */
	EAttribute getStoredProcedureParameter_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.Table <em>Table</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Table</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Table
	 * @generated
	 */
	EClass getTable();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Table#getUniqueConstraint <em>Unique Constraint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Unique Constraint</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Table#getUniqueConstraint()
	 * @see #getTable()
	 * @generated
	 */
	EReference getTable_UniqueConstraint();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.Table#getIndex <em>Index</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Index</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Table#getIndex()
	 * @see #getTable()
	 * @generated
	 */
	EReference getTable_Index();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Table#getCatalog <em>Catalog</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Catalog</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Table#getCatalog()
	 * @see #getTable()
	 * @generated
	 */
	EAttribute getTable_Catalog();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Table#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Table#getName()
	 * @see #getTable()
	 * @generated
	 */
	EAttribute getTable_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Table#getSchema <em>Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Schema</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Table#getSchema()
	 * @see #getTable()
	 * @generated
	 */
	EAttribute getTable_Schema();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.TableGenerator <em>Table Generator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Table Generator</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.TableGenerator
	 * @generated
	 */
	EClass getTableGenerator();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.TableGenerator#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.TableGenerator#getDescription()
	 * @see #getTableGenerator()
	 * @generated
	 */
	EAttribute getTableGenerator_Description();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.TableGenerator#getUniqueConstraint <em>Unique Constraint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Unique Constraint</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.TableGenerator#getUniqueConstraint()
	 * @see #getTableGenerator()
	 * @generated
	 */
	EReference getTableGenerator_UniqueConstraint();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.persistence.eorm.TableGenerator#getIndex <em>Index</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Index</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.TableGenerator#getIndex()
	 * @see #getTableGenerator()
	 * @generated
	 */
	EReference getTableGenerator_Index();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.TableGenerator#getAllocationSize <em>Allocation Size</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Allocation Size</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.TableGenerator#getAllocationSize()
	 * @see #getTableGenerator()
	 * @generated
	 */
	EAttribute getTableGenerator_AllocationSize();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.TableGenerator#getCatalog <em>Catalog</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Catalog</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.TableGenerator#getCatalog()
	 * @see #getTableGenerator()
	 * @generated
	 */
	EAttribute getTableGenerator_Catalog();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.TableGenerator#getInitialValue <em>Initial Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Initial Value</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.TableGenerator#getInitialValue()
	 * @see #getTableGenerator()
	 * @generated
	 */
	EAttribute getTableGenerator_InitialValue();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.TableGenerator#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.TableGenerator#getName()
	 * @see #getTableGenerator()
	 * @generated
	 */
	EAttribute getTableGenerator_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.TableGenerator#getPkColumnName <em>Pk Column Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Pk Column Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.TableGenerator#getPkColumnName()
	 * @see #getTableGenerator()
	 * @generated
	 */
	EAttribute getTableGenerator_PkColumnName();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.TableGenerator#getPkColumnValue <em>Pk Column Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Pk Column Value</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.TableGenerator#getPkColumnValue()
	 * @see #getTableGenerator()
	 * @generated
	 */
	EAttribute getTableGenerator_PkColumnValue();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.TableGenerator#getSchema <em>Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Schema</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.TableGenerator#getSchema()
	 * @see #getTableGenerator()
	 * @generated
	 */
	EAttribute getTableGenerator_Schema();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.TableGenerator#getTable <em>Table</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Table</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.TableGenerator#getTable()
	 * @see #getTableGenerator()
	 * @generated
	 */
	EAttribute getTableGenerator_Table();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.TableGenerator#getValueColumnName <em>Value Column Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value Column Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.TableGenerator#getValueColumnName()
	 * @see #getTableGenerator()
	 * @generated
	 */
	EAttribute getTableGenerator_ValueColumnName();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.Transient <em>Transient</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Transient</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Transient
	 * @generated
	 */
	EClass getTransient();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Transient#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Transient#getName()
	 * @see #getTransient()
	 * @generated
	 */
	EAttribute getTransient_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.UniqueConstraint <em>Unique Constraint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Unique Constraint</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.UniqueConstraint
	 * @generated
	 */
	EClass getUniqueConstraint();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.persistence.eorm.UniqueConstraint#getColumnName <em>Column Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Column Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.UniqueConstraint#getColumnName()
	 * @see #getUniqueConstraint()
	 * @generated
	 */
	EAttribute getUniqueConstraint_ColumnName();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.UniqueConstraint#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.UniqueConstraint#getName()
	 * @see #getUniqueConstraint()
	 * @generated
	 */
	EAttribute getUniqueConstraint_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.Version <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Version</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Version
	 * @generated
	 */
	EClass getVersion();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.Version#getColumn <em>Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Version#getColumn()
	 * @see #getVersion()
	 * @generated
	 */
	EReference getVersion_Column();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Version#getTemporal <em>Temporal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Temporal</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Version#getTemporal()
	 * @see #getVersion()
	 * @generated
	 */
	EAttribute getVersion_Temporal();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Version#getAccess <em>Access</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Access</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Version#getAccess()
	 * @see #getVersion()
	 * @generated
	 */
	EAttribute getVersion_Access();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.Version#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.Version#getName()
	 * @see #getVersion()
	 * @generated
	 */
	EAttribute getVersion_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.MappedByRef <em>Mapped By Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Mapped By Ref</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MappedByRef
	 * @generated
	 */
	EClass getMappedByRef();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.MappedByRef#getMappedBy <em>Mapped By</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mapped By</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.MappedByRef#getMappedBy()
	 * @see #getMappedByRef()
	 * @generated
	 */
	EAttribute getMappedByRef_MappedBy();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.BaseColumn <em>Base Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Base Column</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.BaseColumn
	 * @generated
	 */
	EClass getBaseColumn();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.BaseColumn#getColumnDefinition <em>Column Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Column Definition</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.BaseColumn#getColumnDefinition()
	 * @see #getBaseColumn()
	 * @generated
	 */
	EAttribute getBaseColumn_ColumnDefinition();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.BaseColumn#isInsertable <em>Insertable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Insertable</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.BaseColumn#isInsertable()
	 * @see #getBaseColumn()
	 * @generated
	 */
	EAttribute getBaseColumn_Insertable();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.BaseColumn#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.BaseColumn#getName()
	 * @see #getBaseColumn()
	 * @generated
	 */
	EAttribute getBaseColumn_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.BaseColumn#isNullable <em>Nullable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Nullable</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.BaseColumn#isNullable()
	 * @see #getBaseColumn()
	 * @generated
	 */
	EAttribute getBaseColumn_Nullable();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.BaseColumn#isUpdatable <em>Updatable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Updatable</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.BaseColumn#isUpdatable()
	 * @see #getBaseColumn()
	 * @generated
	 */
	EAttribute getBaseColumn_Updatable();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.EAccessor <em>EAccessor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EAccessor</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EAccessor
	 * @generated
	 */
	EClass getEAccessor();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.persistence.eorm.EAccessor#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EAccessor#getName()
	 * @see #getEAccessor()
	 * @generated
	 */
	EAttribute getEAccessor_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.EFeatureObject <em>EFeature Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EFeature Object</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EFeatureObject
	 * @generated
	 */
	EClass getEFeatureObject();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.persistence.eorm.EFeatureObject#getFeature <em>Feature</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Feature</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EFeatureObject#getFeature()
	 * @see #getEFeatureObject()
	 * @generated
	 */
	EReference getEFeatureObject_Feature();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.EClassObject <em>EClass Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EClass Object</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EClassObject
	 * @generated
	 */
	EClass getEClassObject();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.persistence.eorm.EClassObject#getEclass <em>Eclass</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Eclass</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EClassObject#getEclass()
	 * @see #getEClassObject()
	 * @generated
	 */
	EReference getEClassObject_Eclass();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.eorm.EORMElement <em>Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Element</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EORMElement
	 * @generated
	 */
	EClass getEORMElement();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.persistence.eorm.EORMElement#getAccessibleObject <em>Accessible Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Accessible Object</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EORMElement#getAccessibleObject()
	 * @see #getEORMElement()
	 * @generated
	 */
	EReference getEORMElement_AccessibleObject();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.persistence.eorm.AccessType <em>Access Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Access Type</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.AccessType
	 * @generated
	 */
	EEnum getAccessType();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.persistence.eorm.ConstraintMode <em>Constraint Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Constraint Mode</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ConstraintMode
	 * @generated
	 */
	EEnum getConstraintMode();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.persistence.eorm.DiscriminatorType <em>Discriminator Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Discriminator Type</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.DiscriminatorType
	 * @generated
	 */
	EEnum getDiscriminatorType();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.persistence.eorm.EnumType <em>Enum Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Enum Type</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EnumType
	 * @generated
	 */
	EEnum getEnumType();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.persistence.eorm.FetchType <em>Fetch Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Fetch Type</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.FetchType
	 * @generated
	 */
	EEnum getFetchType();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.persistence.eorm.GenerationType <em>Generation Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Generation Type</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.GenerationType
	 * @generated
	 */
	EEnum getGenerationType();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.persistence.eorm.InheritanceType <em>Inheritance Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Inheritance Type</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.InheritanceType
	 * @generated
	 */
	EEnum getInheritanceType();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.persistence.eorm.LockModeType <em>Lock Mode Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Lock Mode Type</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.LockModeType
	 * @generated
	 */
	EEnum getLockModeType();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.persistence.eorm.ParameterMode <em>Parameter Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Parameter Mode</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ParameterMode
	 * @generated
	 */
	EEnum getParameterMode();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.persistence.eorm.TemporalType <em>Temporal Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Temporal Type</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.TemporalType
	 * @generated
	 */
	EEnum getTemporalType();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.fennec.persistence.eorm.AccessType <em>Access Type Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Access Type Object</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.AccessType
	 * @model instanceClass="org.eclipse.fennec.persistence.eorm.AccessType"
	 *        extendedMetaData="name='access-type:Object' baseType='access-type'"
	 * @generated
	 */
	EDataType getAccessTypeObject();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.fennec.persistence.eorm.ConstraintMode <em>Constraint Mode Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Constraint Mode Object</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ConstraintMode
	 * @model instanceClass="org.eclipse.fennec.persistence.eorm.ConstraintMode"
	 *        extendedMetaData="name='constraint-mode:Object' baseType='constraint-mode'"
	 * @generated
	 */
	EDataType getConstraintModeObject();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.fennec.persistence.eorm.DiscriminatorType <em>Discriminator Type Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Discriminator Type Object</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.DiscriminatorType
	 * @model instanceClass="org.eclipse.fennec.persistence.eorm.DiscriminatorType"
	 *        extendedMetaData="name='discriminator-type:Object' baseType='discriminator-type'"
	 * @generated
	 */
	EDataType getDiscriminatorTypeObject();

	/**
	 * Returns the meta object for data type '{@link java.lang.String <em>Discriminator Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * 
     * 
     *         @Target({TYPE}) @Retention(RUNTIME)
     *         public @interface DiscriminatorValue {
     *           String value();
     *         }
     * 
     *       
     * <!-- end-model-doc -->
	 * @return the meta object for data type '<em>Discriminator Value</em>'.
	 * @see java.lang.String
	 * @model instanceClass="java.lang.String"
	 *        extendedMetaData="name='discriminator-value' baseType='http://www.eclipse.org/emf/2003/XMLType#string'"
	 * @generated
	 */
	EDataType getDiscriminatorValue();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.fennec.persistence.eorm.EnumType <em>Enumerated</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * 
     * 
     *         @Target({METHOD, FIELD}) @Retention(RUNTIME)
     *         public @interface Enumerated {
     *           EnumType value() default ORDINAL;
     *         }
     * 
     *       
     * <!-- end-model-doc -->
	 * @return the meta object for data type '<em>Enumerated</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EnumType
	 * @model instanceClass="org.eclipse.fennec.persistence.eorm.EnumType"
	 *        extendedMetaData="name='enumerated' baseType='enum-type'"
	 * @generated
	 */
	EDataType getEnumerated();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.fennec.persistence.eorm.EnumType <em>Enum Type Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Enum Type Object</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.EnumType
	 * @model instanceClass="org.eclipse.fennec.persistence.eorm.EnumType"
	 *        extendedMetaData="name='enum-type:Object' baseType='enum-type'"
	 * @generated
	 */
	EDataType getEnumTypeObject();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.fennec.persistence.eorm.FetchType <em>Fetch Type Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Fetch Type Object</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.FetchType
	 * @model instanceClass="org.eclipse.fennec.persistence.eorm.FetchType"
	 *        extendedMetaData="name='fetch-type:Object' baseType='fetch-type'"
	 * @generated
	 */
	EDataType getFetchTypeObject();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.fennec.persistence.eorm.GenerationType <em>Generation Type Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Generation Type Object</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.GenerationType
	 * @model instanceClass="org.eclipse.fennec.persistence.eorm.GenerationType"
	 *        extendedMetaData="name='generation-type:Object' baseType='generation-type'"
	 * @generated
	 */
	EDataType getGenerationTypeObject();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.fennec.persistence.eorm.InheritanceType <em>Inheritance Type Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Inheritance Type Object</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.InheritanceType
	 * @model instanceClass="org.eclipse.fennec.persistence.eorm.InheritanceType"
	 *        extendedMetaData="name='inheritance-type:Object' baseType='inheritance-type'"
	 * @generated
	 */
	EDataType getInheritanceTypeObject();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.fennec.persistence.eorm.LockModeType <em>Lock Mode Type Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Lock Mode Type Object</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.LockModeType
	 * @model instanceClass="org.eclipse.fennec.persistence.eorm.LockModeType"
	 *        extendedMetaData="name='lock-mode-type:Object' baseType='lock-mode-type'"
	 * @generated
	 */
	EDataType getLockModeTypeObject();

	/**
	 * Returns the meta object for data type '{@link java.lang.String <em>Order By</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * 
     * 
     *         @Target({METHOD, FIELD}) @Retention(RUNTIME)
     *         public @interface OrderBy {
     *           String value() default "";
     *         }
     * 
     *       
     * <!-- end-model-doc -->
	 * @return the meta object for data type '<em>Order By</em>'.
	 * @see java.lang.String
	 * @model instanceClass="java.lang.String"
	 *        extendedMetaData="name='order-by' baseType='http://www.eclipse.org/emf/2003/XMLType#string'"
	 * @generated
	 */
	EDataType getOrderBy();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.fennec.persistence.eorm.ParameterMode <em>Parameter Mode Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Parameter Mode Object</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.ParameterMode
	 * @model instanceClass="org.eclipse.fennec.persistence.eorm.ParameterMode"
	 *        extendedMetaData="name='parameter-mode:Object' baseType='parameter-mode'"
	 * @generated
	 */
	EDataType getParameterModeObject();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.fennec.persistence.eorm.TemporalType <em>Temporal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * 
     * 
     *         @Target({METHOD, FIELD}) @Retention(RUNTIME)
     *         public @interface Temporal {
     *           TemporalType value();
     *         }
     * 
     *       
     * <!-- end-model-doc -->
	 * @return the meta object for data type '<em>Temporal</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.TemporalType
	 * @model instanceClass="org.eclipse.fennec.persistence.eorm.TemporalType"
	 *        extendedMetaData="name='temporal' baseType='temporal-type'"
	 * @generated
	 */
	EDataType getTemporal();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.fennec.persistence.eorm.TemporalType <em>Temporal Type Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Temporal Type Object</em>'.
	 * @see org.eclipse.fennec.persistence.eorm.TemporalType
	 * @model instanceClass="org.eclipse.fennec.persistence.eorm.TemporalType"
	 *        extendedMetaData="name='temporal-type:Object' baseType='temporal-type'"
	 * @generated
	 */
	EDataType getTemporalTypeObject();

	/**
	 * Returns the meta object for data type '{@link java.lang.String <em>Version Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Version Type</em>'.
	 * @see java.lang.String
	 * @model instanceClass="java.lang.String"
	 * @generated
	 */
	EDataType getVersionType();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	EORMFactory getEORMFactory();

} //EORMPackage
