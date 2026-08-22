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
package org.eclipse.fennec.persistence.repository.api;


import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;

import org.eclipse.fennec.emf.osgi.annotation.provide.EPackage;

import org.eclipse.fennec.persistence.query.api.QueryApiPackage;

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
 * The user-facing repository facade over the unified persistence layer. A repository is an OSGi service bound to one backend location (base URI); it owns a ResourceSet and takes care of all URI construction and Resource lifecycle so callers only handle EObjects. Successor of the Gecko EMFRepository (geckoprojects-org/org.gecko.emf.persistence), continuing its read/write split: RepositoryService carries identity and the URI/resource machinery of the old EMFRepositoryHelper, ReadRepository and WriteRepository carry the data access, Repository combines both. Query and command execution are first-class here and delegate to the QueryableResource and CommandResource roles of the underlying PersistenceResources; capability answers always come from PersistenceResource.capabilities(), the aggregate carrier (issue #134).
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.persistence.repository.api.RepositoryApiFactory
 * @model kind="package"
 *        annotation="Version value='1.0'"
 * @generated
 */
@ProviderType
@EPackage(uri = RepositoryApiPackage.eNS_URI, fingerprint = "fp1:b31abcbdc87b4c1c611fbe3d277f714838111325a3510a82f70d967a8c6fe8e1", genModel = "/model/repository-api.genmodel", genModelSourceLocations = {"model/repository-api.genmodel","org.eclipse.fennec.persistence.repository/model/repository-api.genmodel"}, ecore = "/model/repository-api.ecore", ecoreSourceLocations = "/model/repository-api.ecore")
public interface RepositoryApiPackage extends org.eclipse.emf.ecore.EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "api";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://eclipse.org/fennec/persistence/repository/api/1.0.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "repository.api";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	RepositoryApiPackage eINSTANCE = org.eclipse.fennec.persistence.repository.api.impl.RepositoryApiPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.repository.api.RepositoryService <em>Repository Service</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.repository.api.RepositoryService
	 * @see org.eclipse.fennec.persistence.repository.api.impl.RepositoryApiPackageImpl#getRepositoryService()
	 * @generated
	 */
	int REPOSITORY_SERVICE = 0;

	/**
	 * The number of structural features of the '<em>Repository Service</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY_SERVICE_FEATURE_COUNT = QueryApiPackage.AUTO_CLOSEABLE_FEATURE_COUNT + 0;

	/**
	 * The operation id for the '<em>Id</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY_SERVICE___ID = QueryApiPackage.AUTO_CLOSEABLE_OPERATION_COUNT + 0;

	/**
	 * The operation id for the '<em>Base Uri</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY_SERVICE___BASE_URI = QueryApiPackage.AUTO_CLOSEABLE_OPERATION_COUNT + 1;

	/**
	 * The operation id for the '<em>Capabilities</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY_SERVICE___CAPABILITIES = QueryApiPackage.AUTO_CLOSEABLE_OPERATION_COUNT + 2;

	/**
	 * The operation id for the '<em>Is Disposed</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY_SERVICE___IS_DISPOSED = QueryApiPackage.AUTO_CLOSEABLE_OPERATION_COUNT + 3;

	/**
	 * The operation id for the '<em>Dispose</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY_SERVICE___DISPOSE = QueryApiPackage.AUTO_CLOSEABLE_OPERATION_COUNT + 4;

	/**
	 * The operation id for the '<em>Get Resource Set</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY_SERVICE___GET_RESOURCE_SET = QueryApiPackage.AUTO_CLOSEABLE_OPERATION_COUNT + 5;

	/**
	 * The operation id for the '<em>Create Resource Set</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY_SERVICE___CREATE_RESOURCE_SET = QueryApiPackage.AUTO_CLOSEABLE_OPERATION_COUNT + 6;

	/**
	 * The operation id for the '<em>Create Uri</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY_SERVICE___CREATE_URI__EOBJECT = QueryApiPackage.AUTO_CLOSEABLE_OPERATION_COUNT + 7;

	/**
	 * The operation id for the '<em>Create Uri</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY_SERVICE___CREATE_URI__EOBJECT_MAP = QueryApiPackage.AUTO_CLOSEABLE_OPERATION_COUNT + 8;

	/**
	 * The operation id for the '<em>Create Uri</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY_SERVICE___CREATE_URI__ECLASS_OBJECT = QueryApiPackage.AUTO_CLOSEABLE_OPERATION_COUNT + 9;

	/**
	 * The operation id for the '<em>Create Proxy</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY_SERVICE___CREATE_PROXY__ECLASS_OBJECT = QueryApiPackage.AUTO_CLOSEABLE_OPERATION_COUNT + 10;

	/**
	 * The operation id for the '<em>Proxify</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY_SERVICE___PROXIFY__EOBJECT = QueryApiPackage.AUTO_CLOSEABLE_OPERATION_COUNT + 11;

	/**
	 * The operation id for the '<em>Attach</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY_SERVICE___ATTACH__EOBJECT = QueryApiPackage.AUTO_CLOSEABLE_OPERATION_COUNT + 12;

	/**
	 * The operation id for the '<em>Attach</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY_SERVICE___ATTACH__EOBJECT_MAP = QueryApiPackage.AUTO_CLOSEABLE_OPERATION_COUNT + 13;

	/**
	 * The operation id for the '<em>Detach</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY_SERVICE___DETACH__EOBJECT = QueryApiPackage.AUTO_CLOSEABLE_OPERATION_COUNT + 14;

	/**
	 * The number of operations of the '<em>Repository Service</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY_SERVICE_OPERATION_COUNT = QueryApiPackage.AUTO_CLOSEABLE_OPERATION_COUNT + 15;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.repository.api.ReadRepository <em>Read Repository</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.repository.api.ReadRepository
	 * @see org.eclipse.fennec.persistence.repository.api.impl.RepositoryApiPackageImpl#getReadRepository()
	 * @generated
	 */
	int READ_REPOSITORY = 1;

	/**
	 * The number of structural features of the '<em>Read Repository</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY_FEATURE_COUNT = REPOSITORY_SERVICE_FEATURE_COUNT + 0;

	/**
	 * The operation id for the '<em>Id</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___ID = REPOSITORY_SERVICE___ID;

	/**
	 * The operation id for the '<em>Base Uri</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___BASE_URI = REPOSITORY_SERVICE___BASE_URI;

	/**
	 * The operation id for the '<em>Capabilities</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___CAPABILITIES = REPOSITORY_SERVICE___CAPABILITIES;

	/**
	 * The operation id for the '<em>Is Disposed</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___IS_DISPOSED = REPOSITORY_SERVICE___IS_DISPOSED;

	/**
	 * The operation id for the '<em>Dispose</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___DISPOSE = REPOSITORY_SERVICE___DISPOSE;

	/**
	 * The operation id for the '<em>Get Resource Set</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___GET_RESOURCE_SET = REPOSITORY_SERVICE___GET_RESOURCE_SET;

	/**
	 * The operation id for the '<em>Create Resource Set</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___CREATE_RESOURCE_SET = REPOSITORY_SERVICE___CREATE_RESOURCE_SET;

	/**
	 * The operation id for the '<em>Create Uri</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___CREATE_URI__EOBJECT = REPOSITORY_SERVICE___CREATE_URI__EOBJECT;

	/**
	 * The operation id for the '<em>Create Uri</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___CREATE_URI__EOBJECT_MAP = REPOSITORY_SERVICE___CREATE_URI__EOBJECT_MAP;

	/**
	 * The operation id for the '<em>Create Uri</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___CREATE_URI__ECLASS_OBJECT = REPOSITORY_SERVICE___CREATE_URI__ECLASS_OBJECT;

	/**
	 * The operation id for the '<em>Create Proxy</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___CREATE_PROXY__ECLASS_OBJECT = REPOSITORY_SERVICE___CREATE_PROXY__ECLASS_OBJECT;

	/**
	 * The operation id for the '<em>Proxify</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___PROXIFY__EOBJECT = REPOSITORY_SERVICE___PROXIFY__EOBJECT;

	/**
	 * The operation id for the '<em>Attach</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___ATTACH__EOBJECT = REPOSITORY_SERVICE___ATTACH__EOBJECT;

	/**
	 * The operation id for the '<em>Attach</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___ATTACH__EOBJECT_MAP = REPOSITORY_SERVICE___ATTACH__EOBJECT_MAP;

	/**
	 * The operation id for the '<em>Detach</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___DETACH__EOBJECT = REPOSITORY_SERVICE___DETACH__EOBJECT;

	/**
	 * The operation id for the '<em>Get Resource</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___GET_RESOURCE__URI_BOOLEAN = REPOSITORY_SERVICE_OPERATION_COUNT + 0;

	/**
	 * The operation id for the '<em>Get EObject</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___GET_EOBJECT__URI = REPOSITORY_SERVICE_OPERATION_COUNT + 1;

	/**
	 * The operation id for the '<em>Get EObject</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___GET_EOBJECT__URI_MAP = REPOSITORY_SERVICE_OPERATION_COUNT + 2;

	/**
	 * The operation id for the '<em>Get EObject</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___GET_EOBJECT__ECLASS_OBJECT = REPOSITORY_SERVICE_OPERATION_COUNT + 3;

	/**
	 * The operation id for the '<em>Get EObject</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___GET_EOBJECT__ECLASS_OBJECT_MAP = REPOSITORY_SERVICE_OPERATION_COUNT + 4;

	/**
	 * The operation id for the '<em>Get All EObjects</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___GET_ALL_EOBJECTS__ECLASS = REPOSITORY_SERVICE_OPERATION_COUNT + 5;

	/**
	 * The operation id for the '<em>Get All EObjects</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___GET_ALL_EOBJECTS__ECLASS_MAP = REPOSITORY_SERVICE_OPERATION_COUNT + 6;

	/**
	 * The operation id for the '<em>Count</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___COUNT__ECLASS = REPOSITORY_SERVICE_OPERATION_COUNT + 7;

	/**
	 * The operation id for the '<em>Count</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___COUNT__ECLASS_MAP = REPOSITORY_SERVICE_OPERATION_COUNT + 8;

	/**
	 * The operation id for the '<em>Exist</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___EXIST__URI = REPOSITORY_SERVICE_OPERATION_COUNT + 9;

	/**
	 * The operation id for the '<em>Exist</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___EXIST__ECLASS_OBJECT = REPOSITORY_SERVICE_OPERATION_COUNT + 10;

	/**
	 * The operation id for the '<em>Reload</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___RELOAD__EOBJECT = REPOSITORY_SERVICE_OPERATION_COUNT + 11;

	/**
	 * The operation id for the '<em>Find</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___FIND__QUERY = REPOSITORY_SERVICE_OPERATION_COUNT + 12;

	/**
	 * The operation id for the '<em>Find</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___FIND__QUERY_MAP_MAP = REPOSITORY_SERVICE_OPERATION_COUNT + 13;

	/**
	 * The operation id for the '<em>Find</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___FIND__STRING_MAP_MAP = REPOSITORY_SERVICE_OPERATION_COUNT + 14;

	/**
	 * The operation id for the '<em>Count</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___COUNT__QUERY = REPOSITORY_SERVICE_OPERATION_COUNT + 15;

	/**
	 * The operation id for the '<em>Prepare</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___PREPARE__QUERY = REPOSITORY_SERVICE_OPERATION_COUNT + 16;

	/**
	 * The operation id for the '<em>Prepare</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY___PREPARE__STRING = REPOSITORY_SERVICE_OPERATION_COUNT + 17;

	/**
	 * The number of operations of the '<em>Read Repository</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int READ_REPOSITORY_OPERATION_COUNT = REPOSITORY_SERVICE_OPERATION_COUNT + 18;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.repository.api.PreparedQuery <em>Prepared Query</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.repository.api.PreparedQuery
	 * @see org.eclipse.fennec.persistence.repository.api.impl.RepositoryApiPackageImpl#getPreparedQuery()
	 * @generated
	 */
	int PREPARED_QUERY = 2;

	/**
	 * The number of structural features of the '<em>Prepared Query</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PREPARED_QUERY_FEATURE_COUNT = 0;

	/**
	 * The operation id for the '<em>Name</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PREPARED_QUERY___NAME = 0;

	/**
	 * The operation id for the '<em>Query</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PREPARED_QUERY___QUERY = 1;

	/**
	 * The operation id for the '<em>Parameter Declarations</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PREPARED_QUERY___PARAMETER_DECLARATIONS = 2;

	/**
	 * The operation id for the '<em>Execute</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PREPARED_QUERY___EXECUTE__MAP = 3;

	/**
	 * The operation id for the '<em>Execute</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PREPARED_QUERY___EXECUTE__MAP_MAP = 4;

	/**
	 * The number of operations of the '<em>Prepared Query</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PREPARED_QUERY_OPERATION_COUNT = 5;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.repository.api.WriteRepository <em>Write Repository</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.repository.api.WriteRepository
	 * @see org.eclipse.fennec.persistence.repository.api.impl.RepositoryApiPackageImpl#getWriteRepository()
	 * @generated
	 */
	int WRITE_REPOSITORY = 3;

	/**
	 * The number of structural features of the '<em>Write Repository</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY_FEATURE_COUNT = REPOSITORY_SERVICE_FEATURE_COUNT + 0;

	/**
	 * The operation id for the '<em>Id</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___ID = REPOSITORY_SERVICE___ID;

	/**
	 * The operation id for the '<em>Base Uri</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___BASE_URI = REPOSITORY_SERVICE___BASE_URI;

	/**
	 * The operation id for the '<em>Capabilities</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___CAPABILITIES = REPOSITORY_SERVICE___CAPABILITIES;

	/**
	 * The operation id for the '<em>Is Disposed</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___IS_DISPOSED = REPOSITORY_SERVICE___IS_DISPOSED;

	/**
	 * The operation id for the '<em>Dispose</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___DISPOSE = REPOSITORY_SERVICE___DISPOSE;

	/**
	 * The operation id for the '<em>Get Resource Set</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___GET_RESOURCE_SET = REPOSITORY_SERVICE___GET_RESOURCE_SET;

	/**
	 * The operation id for the '<em>Create Resource Set</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___CREATE_RESOURCE_SET = REPOSITORY_SERVICE___CREATE_RESOURCE_SET;

	/**
	 * The operation id for the '<em>Create Uri</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___CREATE_URI__EOBJECT = REPOSITORY_SERVICE___CREATE_URI__EOBJECT;

	/**
	 * The operation id for the '<em>Create Uri</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___CREATE_URI__EOBJECT_MAP = REPOSITORY_SERVICE___CREATE_URI__EOBJECT_MAP;

	/**
	 * The operation id for the '<em>Create Uri</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___CREATE_URI__ECLASS_OBJECT = REPOSITORY_SERVICE___CREATE_URI__ECLASS_OBJECT;

	/**
	 * The operation id for the '<em>Create Proxy</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___CREATE_PROXY__ECLASS_OBJECT = REPOSITORY_SERVICE___CREATE_PROXY__ECLASS_OBJECT;

	/**
	 * The operation id for the '<em>Proxify</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___PROXIFY__EOBJECT = REPOSITORY_SERVICE___PROXIFY__EOBJECT;

	/**
	 * The operation id for the '<em>Attach</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___ATTACH__EOBJECT = REPOSITORY_SERVICE___ATTACH__EOBJECT;

	/**
	 * The operation id for the '<em>Attach</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___ATTACH__EOBJECT_MAP = REPOSITORY_SERVICE___ATTACH__EOBJECT_MAP;

	/**
	 * The operation id for the '<em>Detach</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___DETACH__EOBJECT = REPOSITORY_SERVICE___DETACH__EOBJECT;

	/**
	 * The operation id for the '<em>Save</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___SAVE__EOBJECT = REPOSITORY_SERVICE_OPERATION_COUNT + 0;

	/**
	 * The operation id for the '<em>Save</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___SAVE__EOBJECT_MAP = REPOSITORY_SERVICE_OPERATION_COUNT + 1;

	/**
	 * The operation id for the '<em>Save</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___SAVE__EOBJECT_URI = REPOSITORY_SERVICE_OPERATION_COUNT + 2;

	/**
	 * The operation id for the '<em>Save</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___SAVE__EOBJECT_URI_MAP = REPOSITORY_SERVICE_OPERATION_COUNT + 3;

	/**
	 * The operation id for the '<em>Save All</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___SAVE_ALL__COLLECTION = REPOSITORY_SERVICE_OPERATION_COUNT + 4;

	/**
	 * The operation id for the '<em>Save All</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___SAVE_ALL__COLLECTION_MAP = REPOSITORY_SERVICE_OPERATION_COUNT + 5;

	/**
	 * The operation id for the '<em>Delete</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___DELETE__EOBJECT = REPOSITORY_SERVICE_OPERATION_COUNT + 6;

	/**
	 * The operation id for the '<em>Delete</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___DELETE__EOBJECT_MAP = REPOSITORY_SERVICE_OPERATION_COUNT + 7;

	/**
	 * The operation id for the '<em>Delete</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___DELETE__URI = REPOSITORY_SERVICE_OPERATION_COUNT + 8;

	/**
	 * The operation id for the '<em>Delete</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___DELETE__URI_MAP = REPOSITORY_SERVICE_OPERATION_COUNT + 9;

	/**
	 * The operation id for the '<em>Execute</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___EXECUTE__COMMAND = REPOSITORY_SERVICE_OPERATION_COUNT + 10;

	/**
	 * The operation id for the '<em>Execute</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY___EXECUTE__COMMAND_MAP_MAP = REPOSITORY_SERVICE_OPERATION_COUNT + 11;

	/**
	 * The number of operations of the '<em>Write Repository</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WRITE_REPOSITORY_OPERATION_COUNT = REPOSITORY_SERVICE_OPERATION_COUNT + 12;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.repository.api.Repository <em>Repository</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.repository.api.Repository
	 * @see org.eclipse.fennec.persistence.repository.api.impl.RepositoryApiPackageImpl#getRepository()
	 * @generated
	 */
	int REPOSITORY = 4;

	/**
	 * The number of structural features of the '<em>Repository</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY_FEATURE_COUNT = READ_REPOSITORY_FEATURE_COUNT + 0;

	/**
	 * The operation id for the '<em>Id</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___ID = READ_REPOSITORY___ID;

	/**
	 * The operation id for the '<em>Base Uri</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___BASE_URI = READ_REPOSITORY___BASE_URI;

	/**
	 * The operation id for the '<em>Capabilities</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___CAPABILITIES = READ_REPOSITORY___CAPABILITIES;

	/**
	 * The operation id for the '<em>Is Disposed</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___IS_DISPOSED = READ_REPOSITORY___IS_DISPOSED;

	/**
	 * The operation id for the '<em>Dispose</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___DISPOSE = READ_REPOSITORY___DISPOSE;

	/**
	 * The operation id for the '<em>Get Resource Set</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___GET_RESOURCE_SET = READ_REPOSITORY___GET_RESOURCE_SET;

	/**
	 * The operation id for the '<em>Create Resource Set</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___CREATE_RESOURCE_SET = READ_REPOSITORY___CREATE_RESOURCE_SET;

	/**
	 * The operation id for the '<em>Create Uri</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___CREATE_URI__EOBJECT = READ_REPOSITORY___CREATE_URI__EOBJECT;

	/**
	 * The operation id for the '<em>Create Uri</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___CREATE_URI__EOBJECT_MAP = READ_REPOSITORY___CREATE_URI__EOBJECT_MAP;

	/**
	 * The operation id for the '<em>Create Uri</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___CREATE_URI__ECLASS_OBJECT = READ_REPOSITORY___CREATE_URI__ECLASS_OBJECT;

	/**
	 * The operation id for the '<em>Create Proxy</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___CREATE_PROXY__ECLASS_OBJECT = READ_REPOSITORY___CREATE_PROXY__ECLASS_OBJECT;

	/**
	 * The operation id for the '<em>Proxify</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___PROXIFY__EOBJECT = READ_REPOSITORY___PROXIFY__EOBJECT;

	/**
	 * The operation id for the '<em>Attach</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___ATTACH__EOBJECT = READ_REPOSITORY___ATTACH__EOBJECT;

	/**
	 * The operation id for the '<em>Attach</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___ATTACH__EOBJECT_MAP = READ_REPOSITORY___ATTACH__EOBJECT_MAP;

	/**
	 * The operation id for the '<em>Detach</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___DETACH__EOBJECT = READ_REPOSITORY___DETACH__EOBJECT;

	/**
	 * The operation id for the '<em>Get Resource</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___GET_RESOURCE__URI_BOOLEAN = READ_REPOSITORY___GET_RESOURCE__URI_BOOLEAN;

	/**
	 * The operation id for the '<em>Get EObject</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___GET_EOBJECT__URI = READ_REPOSITORY___GET_EOBJECT__URI;

	/**
	 * The operation id for the '<em>Get EObject</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___GET_EOBJECT__URI_MAP = READ_REPOSITORY___GET_EOBJECT__URI_MAP;

	/**
	 * The operation id for the '<em>Get EObject</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___GET_EOBJECT__ECLASS_OBJECT = READ_REPOSITORY___GET_EOBJECT__ECLASS_OBJECT;

	/**
	 * The operation id for the '<em>Get EObject</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___GET_EOBJECT__ECLASS_OBJECT_MAP = READ_REPOSITORY___GET_EOBJECT__ECLASS_OBJECT_MAP;

	/**
	 * The operation id for the '<em>Get All EObjects</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___GET_ALL_EOBJECTS__ECLASS = READ_REPOSITORY___GET_ALL_EOBJECTS__ECLASS;

	/**
	 * The operation id for the '<em>Get All EObjects</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___GET_ALL_EOBJECTS__ECLASS_MAP = READ_REPOSITORY___GET_ALL_EOBJECTS__ECLASS_MAP;

	/**
	 * The operation id for the '<em>Count</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___COUNT__ECLASS = READ_REPOSITORY___COUNT__ECLASS;

	/**
	 * The operation id for the '<em>Count</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___COUNT__ECLASS_MAP = READ_REPOSITORY___COUNT__ECLASS_MAP;

	/**
	 * The operation id for the '<em>Exist</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___EXIST__URI = READ_REPOSITORY___EXIST__URI;

	/**
	 * The operation id for the '<em>Exist</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___EXIST__ECLASS_OBJECT = READ_REPOSITORY___EXIST__ECLASS_OBJECT;

	/**
	 * The operation id for the '<em>Reload</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___RELOAD__EOBJECT = READ_REPOSITORY___RELOAD__EOBJECT;

	/**
	 * The operation id for the '<em>Find</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___FIND__QUERY = READ_REPOSITORY___FIND__QUERY;

	/**
	 * The operation id for the '<em>Find</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___FIND__QUERY_MAP_MAP = READ_REPOSITORY___FIND__QUERY_MAP_MAP;

	/**
	 * The operation id for the '<em>Find</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___FIND__STRING_MAP_MAP = READ_REPOSITORY___FIND__STRING_MAP_MAP;

	/**
	 * The operation id for the '<em>Count</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___COUNT__QUERY = READ_REPOSITORY___COUNT__QUERY;

	/**
	 * The operation id for the '<em>Prepare</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___PREPARE__QUERY = READ_REPOSITORY___PREPARE__QUERY;

	/**
	 * The operation id for the '<em>Prepare</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___PREPARE__STRING = READ_REPOSITORY___PREPARE__STRING;

	/**
	 * The operation id for the '<em>Save</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___SAVE__EOBJECT = READ_REPOSITORY_OPERATION_COUNT + 0;

	/**
	 * The operation id for the '<em>Save</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___SAVE__EOBJECT_MAP = READ_REPOSITORY_OPERATION_COUNT + 1;

	/**
	 * The operation id for the '<em>Save</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___SAVE__EOBJECT_URI = READ_REPOSITORY_OPERATION_COUNT + 2;

	/**
	 * The operation id for the '<em>Save</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___SAVE__EOBJECT_URI_MAP = READ_REPOSITORY_OPERATION_COUNT + 3;

	/**
	 * The operation id for the '<em>Save All</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___SAVE_ALL__COLLECTION = READ_REPOSITORY_OPERATION_COUNT + 4;

	/**
	 * The operation id for the '<em>Save All</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___SAVE_ALL__COLLECTION_MAP = READ_REPOSITORY_OPERATION_COUNT + 5;

	/**
	 * The operation id for the '<em>Delete</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___DELETE__EOBJECT = READ_REPOSITORY_OPERATION_COUNT + 6;

	/**
	 * The operation id for the '<em>Delete</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___DELETE__EOBJECT_MAP = READ_REPOSITORY_OPERATION_COUNT + 7;

	/**
	 * The operation id for the '<em>Delete</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___DELETE__URI = READ_REPOSITORY_OPERATION_COUNT + 8;

	/**
	 * The operation id for the '<em>Delete</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___DELETE__URI_MAP = READ_REPOSITORY_OPERATION_COUNT + 9;

	/**
	 * The operation id for the '<em>Execute</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___EXECUTE__COMMAND = READ_REPOSITORY_OPERATION_COUNT + 10;

	/**
	 * The operation id for the '<em>Execute</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY___EXECUTE__COMMAND_MAP_MAP = READ_REPOSITORY_OPERATION_COUNT + 11;

	/**
	 * The number of operations of the '<em>Repository</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY_OPERATION_COUNT = READ_REPOSITORY_OPERATION_COUNT + 12;

	/**
	 * The meta object id for the '<em>Uri</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.common.util.URI
	 * @see org.eclipse.fennec.persistence.repository.api.impl.RepositoryApiPackageImpl#getUri()
	 * @generated
	 */
	int URI = 5;

	/**
	 * The meta object id for the '<em>Persistence Capabilities</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.capabilities.PersistenceCapabilities
	 * @see org.eclipse.fennec.persistence.repository.api.impl.RepositoryApiPackageImpl#getPersistenceCapabilities()
	 * @generated
	 */
	int PERSISTENCE_CAPABILITIES = 6;

	/**
	 * The meta object id for the '<em>EObject Collection</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.Collection
	 * @see org.eclipse.fennec.persistence.repository.api.impl.RepositoryApiPackageImpl#getEObjectCollection()
	 * @generated
	 */
	int EOBJECT_COLLECTION = 7;


	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.repository.api.RepositoryService <em>Repository Service</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Repository Service</em>'.
	 * @see org.eclipse.fennec.persistence.repository.api.RepositoryService
	 * @generated
	 */
	EClass getRepositoryService();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.RepositoryService#id() <em>Id</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Id</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.RepositoryService#id()
	 * @generated
	 */
	EOperation getRepositoryService__Id();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.RepositoryService#baseUri() <em>Base Uri</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Base Uri</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.RepositoryService#baseUri()
	 * @generated
	 */
	EOperation getRepositoryService__BaseUri();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.RepositoryService#capabilities() <em>Capabilities</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Capabilities</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.RepositoryService#capabilities()
	 * @generated
	 */
	EOperation getRepositoryService__Capabilities();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.RepositoryService#isDisposed() <em>Is Disposed</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Is Disposed</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.RepositoryService#isDisposed()
	 * @generated
	 */
	EOperation getRepositoryService__IsDisposed();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.RepositoryService#dispose() <em>Dispose</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Dispose</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.RepositoryService#dispose()
	 * @generated
	 */
	EOperation getRepositoryService__Dispose();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.RepositoryService#getResourceSet() <em>Get Resource Set</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Resource Set</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.RepositoryService#getResourceSet()
	 * @generated
	 */
	EOperation getRepositoryService__GetResourceSet();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.RepositoryService#createResourceSet() <em>Create Resource Set</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Create Resource Set</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.RepositoryService#createResourceSet()
	 * @generated
	 */
	EOperation getRepositoryService__CreateResourceSet();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.RepositoryService#createUri(org.eclipse.emf.ecore.EObject) <em>Create Uri</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Create Uri</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.RepositoryService#createUri(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	EOperation getRepositoryService__CreateUri__EObject();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.RepositoryService#createUri(org.eclipse.emf.ecore.EObject, java.util.Map) <em>Create Uri</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Create Uri</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.RepositoryService#createUri(org.eclipse.emf.ecore.EObject, java.util.Map)
	 * @generated
	 */
	EOperation getRepositoryService__CreateUri__EObject_Map();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.RepositoryService#createUri(org.eclipse.emf.ecore.EClass, java.lang.Object) <em>Create Uri</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Create Uri</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.RepositoryService#createUri(org.eclipse.emf.ecore.EClass, java.lang.Object)
	 * @generated
	 */
	EOperation getRepositoryService__CreateUri__EClass_Object();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.RepositoryService#createProxy(org.eclipse.emf.ecore.EClass, java.lang.Object) <em>Create Proxy</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Create Proxy</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.RepositoryService#createProxy(org.eclipse.emf.ecore.EClass, java.lang.Object)
	 * @generated
	 */
	EOperation getRepositoryService__CreateProxy__EClass_Object();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.RepositoryService#proxify(org.eclipse.emf.ecore.EObject) <em>Proxify</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Proxify</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.RepositoryService#proxify(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	EOperation getRepositoryService__Proxify__EObject();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.RepositoryService#attach(org.eclipse.emf.ecore.EObject) <em>Attach</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Attach</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.RepositoryService#attach(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	EOperation getRepositoryService__Attach__EObject();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.RepositoryService#attach(org.eclipse.emf.ecore.EObject, java.util.Map) <em>Attach</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Attach</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.RepositoryService#attach(org.eclipse.emf.ecore.EObject, java.util.Map)
	 * @generated
	 */
	EOperation getRepositoryService__Attach__EObject_Map();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.RepositoryService#detach(org.eclipse.emf.ecore.EObject) <em>Detach</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Detach</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.RepositoryService#detach(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	EOperation getRepositoryService__Detach__EObject();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.repository.api.ReadRepository <em>Read Repository</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Read Repository</em>'.
	 * @see org.eclipse.fennec.persistence.repository.api.ReadRepository
	 * @generated
	 */
	EClass getReadRepository();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.ReadRepository#getResource(org.eclipse.emf.common.util.URI, boolean) <em>Get Resource</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Resource</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.ReadRepository#getResource(org.eclipse.emf.common.util.URI, boolean)
	 * @generated
	 */
	EOperation getReadRepository__GetResource__URI_boolean();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.ReadRepository#getEObject(org.eclipse.emf.common.util.URI) <em>Get EObject</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get EObject</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.ReadRepository#getEObject(org.eclipse.emf.common.util.URI)
	 * @generated
	 */
	EOperation getReadRepository__GetEObject__URI();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.ReadRepository#getEObject(org.eclipse.emf.common.util.URI, java.util.Map) <em>Get EObject</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get EObject</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.ReadRepository#getEObject(org.eclipse.emf.common.util.URI, java.util.Map)
	 * @generated
	 */
	EOperation getReadRepository__GetEObject__URI_Map();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.ReadRepository#getEObject(org.eclipse.emf.ecore.EClass, java.lang.Object) <em>Get EObject</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get EObject</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.ReadRepository#getEObject(org.eclipse.emf.ecore.EClass, java.lang.Object)
	 * @generated
	 */
	EOperation getReadRepository__GetEObject__EClass_Object();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.ReadRepository#getEObject(org.eclipse.emf.ecore.EClass, java.lang.Object, java.util.Map) <em>Get EObject</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get EObject</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.ReadRepository#getEObject(org.eclipse.emf.ecore.EClass, java.lang.Object, java.util.Map)
	 * @generated
	 */
	EOperation getReadRepository__GetEObject__EClass_Object_Map();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.ReadRepository#getAllEObjects(org.eclipse.emf.ecore.EClass) <em>Get All EObjects</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get All EObjects</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.ReadRepository#getAllEObjects(org.eclipse.emf.ecore.EClass)
	 * @generated
	 */
	EOperation getReadRepository__GetAllEObjects__EClass();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.ReadRepository#getAllEObjects(org.eclipse.emf.ecore.EClass, java.util.Map) <em>Get All EObjects</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get All EObjects</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.ReadRepository#getAllEObjects(org.eclipse.emf.ecore.EClass, java.util.Map)
	 * @generated
	 */
	EOperation getReadRepository__GetAllEObjects__EClass_Map();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.ReadRepository#count(org.eclipse.emf.ecore.EClass) <em>Count</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Count</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.ReadRepository#count(org.eclipse.emf.ecore.EClass)
	 * @generated
	 */
	EOperation getReadRepository__Count__EClass();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.ReadRepository#count(org.eclipse.emf.ecore.EClass, java.util.Map) <em>Count</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Count</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.ReadRepository#count(org.eclipse.emf.ecore.EClass, java.util.Map)
	 * @generated
	 */
	EOperation getReadRepository__Count__EClass_Map();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.ReadRepository#exist(org.eclipse.emf.common.util.URI) <em>Exist</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Exist</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.ReadRepository#exist(org.eclipse.emf.common.util.URI)
	 * @generated
	 */
	EOperation getReadRepository__Exist__URI();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.ReadRepository#exist(org.eclipse.emf.ecore.EClass, java.lang.Object) <em>Exist</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Exist</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.ReadRepository#exist(org.eclipse.emf.ecore.EClass, java.lang.Object)
	 * @generated
	 */
	EOperation getReadRepository__Exist__EClass_Object();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.ReadRepository#reload(org.eclipse.emf.ecore.EObject) <em>Reload</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Reload</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.ReadRepository#reload(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	EOperation getReadRepository__Reload__EObject();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.ReadRepository#find(org.eclipse.fennec.model.query.Query) <em>Find</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Find</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.ReadRepository#find(org.eclipse.fennec.model.query.Query)
	 * @generated
	 */
	EOperation getReadRepository__Find__Query();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.ReadRepository#find(org.eclipse.fennec.model.query.Query, java.util.Map, java.util.Map) <em>Find</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Find</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.ReadRepository#find(org.eclipse.fennec.model.query.Query, java.util.Map, java.util.Map)
	 * @generated
	 */
	EOperation getReadRepository__Find__Query_Map_Map();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.ReadRepository#find(java.lang.String, java.util.Map, java.util.Map) <em>Find</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Find</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.ReadRepository#find(java.lang.String, java.util.Map, java.util.Map)
	 * @generated
	 */
	EOperation getReadRepository__Find__String_Map_Map();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.ReadRepository#count(org.eclipse.fennec.model.query.Query) <em>Count</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Count</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.ReadRepository#count(org.eclipse.fennec.model.query.Query)
	 * @generated
	 */
	EOperation getReadRepository__Count__Query();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.ReadRepository#prepare(org.eclipse.fennec.model.query.Query) <em>Prepare</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Prepare</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.ReadRepository#prepare(org.eclipse.fennec.model.query.Query)
	 * @generated
	 */
	EOperation getReadRepository__Prepare__Query();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.ReadRepository#prepare(java.lang.String) <em>Prepare</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Prepare</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.ReadRepository#prepare(java.lang.String)
	 * @generated
	 */
	EOperation getReadRepository__Prepare__String();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.repository.api.PreparedQuery <em>Prepared Query</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Prepared Query</em>'.
	 * @see org.eclipse.fennec.persistence.repository.api.PreparedQuery
	 * @generated
	 */
	EClass getPreparedQuery();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.PreparedQuery#name() <em>Name</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Name</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.PreparedQuery#name()
	 * @generated
	 */
	EOperation getPreparedQuery__Name();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.PreparedQuery#query() <em>Query</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Query</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.PreparedQuery#query()
	 * @generated
	 */
	EOperation getPreparedQuery__Query();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.PreparedQuery#parameterDeclarations() <em>Parameter Declarations</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Parameter Declarations</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.PreparedQuery#parameterDeclarations()
	 * @generated
	 */
	EOperation getPreparedQuery__ParameterDeclarations();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.PreparedQuery#execute(java.util.Map) <em>Execute</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Execute</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.PreparedQuery#execute(java.util.Map)
	 * @generated
	 */
	EOperation getPreparedQuery__Execute__Map();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.PreparedQuery#execute(java.util.Map, java.util.Map) <em>Execute</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Execute</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.PreparedQuery#execute(java.util.Map, java.util.Map)
	 * @generated
	 */
	EOperation getPreparedQuery__Execute__Map_Map();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.repository.api.WriteRepository <em>Write Repository</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Write Repository</em>'.
	 * @see org.eclipse.fennec.persistence.repository.api.WriteRepository
	 * @generated
	 */
	EClass getWriteRepository();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.WriteRepository#save(org.eclipse.emf.ecore.EObject) <em>Save</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Save</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.WriteRepository#save(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	EOperation getWriteRepository__Save__EObject();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.WriteRepository#save(org.eclipse.emf.ecore.EObject, java.util.Map) <em>Save</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Save</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.WriteRepository#save(org.eclipse.emf.ecore.EObject, java.util.Map)
	 * @generated
	 */
	EOperation getWriteRepository__Save__EObject_Map();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.WriteRepository#save(org.eclipse.emf.ecore.EObject, org.eclipse.emf.common.util.URI) <em>Save</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Save</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.WriteRepository#save(org.eclipse.emf.ecore.EObject, org.eclipse.emf.common.util.URI)
	 * @generated
	 */
	EOperation getWriteRepository__Save__EObject_URI();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.WriteRepository#save(org.eclipse.emf.ecore.EObject, org.eclipse.emf.common.util.URI, java.util.Map) <em>Save</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Save</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.WriteRepository#save(org.eclipse.emf.ecore.EObject, org.eclipse.emf.common.util.URI, java.util.Map)
	 * @generated
	 */
	EOperation getWriteRepository__Save__EObject_URI_Map();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.WriteRepository#saveAll(java.util.Collection) <em>Save All</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Save All</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.WriteRepository#saveAll(java.util.Collection)
	 * @generated
	 */
	EOperation getWriteRepository__SaveAll__Collection();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.WriteRepository#saveAll(java.util.Collection, java.util.Map) <em>Save All</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Save All</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.WriteRepository#saveAll(java.util.Collection, java.util.Map)
	 * @generated
	 */
	EOperation getWriteRepository__SaveAll__Collection_Map();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.WriteRepository#delete(org.eclipse.emf.ecore.EObject) <em>Delete</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Delete</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.WriteRepository#delete(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	EOperation getWriteRepository__Delete__EObject();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.WriteRepository#delete(org.eclipse.emf.ecore.EObject, java.util.Map) <em>Delete</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Delete</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.WriteRepository#delete(org.eclipse.emf.ecore.EObject, java.util.Map)
	 * @generated
	 */
	EOperation getWriteRepository__Delete__EObject_Map();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.WriteRepository#delete(org.eclipse.emf.common.util.URI) <em>Delete</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Delete</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.WriteRepository#delete(org.eclipse.emf.common.util.URI)
	 * @generated
	 */
	EOperation getWriteRepository__Delete__URI();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.WriteRepository#delete(org.eclipse.emf.common.util.URI, java.util.Map) <em>Delete</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Delete</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.WriteRepository#delete(org.eclipse.emf.common.util.URI, java.util.Map)
	 * @generated
	 */
	EOperation getWriteRepository__Delete__URI_Map();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.WriteRepository#execute(org.eclipse.fennec.model.command.Command) <em>Execute</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Execute</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.WriteRepository#execute(org.eclipse.fennec.model.command.Command)
	 * @generated
	 */
	EOperation getWriteRepository__Execute__Command();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.repository.api.WriteRepository#execute(org.eclipse.fennec.model.command.Command, java.util.Map, java.util.Map) <em>Execute</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Execute</em>' operation.
	 * @see org.eclipse.fennec.persistence.repository.api.WriteRepository#execute(org.eclipse.fennec.model.command.Command, java.util.Map, java.util.Map)
	 * @generated
	 */
	EOperation getWriteRepository__Execute__Command_Map_Map();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.repository.api.Repository <em>Repository</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Repository</em>'.
	 * @see org.eclipse.fennec.persistence.repository.api.Repository
	 * @generated
	 */
	EClass getRepository();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.emf.common.util.URI <em>Uri</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * org.eclipse.emf.common.util.URI — the EMF URI addressing an object or resource inside the repository's backend, always rooted at the repository's base URI.
     * <!-- end-model-doc -->
	 * @return the meta object for data type '<em>Uri</em>'.
	 * @see org.eclipse.emf.common.util.URI
	 * @model instanceClass="org.eclipse.emf.common.util.URI"
	 * @generated
	 */
	EDataType getUri();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.fennec.persistence.capabilities.PersistenceCapabilities <em>Persistence Capabilities</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The effective capabilities of the backend this repository is bound to — same contract as PersistenceResource.capabilities() (issue #134, contract §5a).
     * <!-- end-model-doc -->
	 * @return the meta object for data type '<em>Persistence Capabilities</em>'.
	 * @see org.eclipse.fennec.persistence.capabilities.PersistenceCapabilities
	 * @model instanceClass="org.eclipse.fennec.persistence.capabilities.PersistenceCapabilities"
	 * @generated
	 */
	EDataType getPersistenceCapabilities();

	/**
	 * Returns the meta object for data type '{@link java.util.Collection <em>EObject Collection</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * A plain java.util.Collection of EObjects, used for bulk save — callers pass any collection type without adapting to EList.
     * <!-- end-model-doc -->
	 * @return the meta object for data type '<em>EObject Collection</em>'.
	 * @see java.util.Collection
	 * @model instanceClass="java.util.Collection&lt;org.eclipse.emf.ecore.EObject&gt;"
	 * @generated
	 */
	EDataType getEObjectCollection();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	RepositoryApiFactory getRepositoryApiFactory();

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
		 * The meta object literal for the '{@link org.eclipse.fennec.persistence.repository.api.RepositoryService <em>Repository Service</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.repository.api.RepositoryService
		 * @see org.eclipse.fennec.persistence.repository.api.impl.RepositoryApiPackageImpl#getRepositoryService()
		 * @generated
		 */
		EClass REPOSITORY_SERVICE = eINSTANCE.getRepositoryService();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation REPOSITORY_SERVICE___ID = eINSTANCE.getRepositoryService__Id();

		/**
		 * The meta object literal for the '<em><b>Base Uri</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation REPOSITORY_SERVICE___BASE_URI = eINSTANCE.getRepositoryService__BaseUri();

		/**
		 * The meta object literal for the '<em><b>Capabilities</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation REPOSITORY_SERVICE___CAPABILITIES = eINSTANCE.getRepositoryService__Capabilities();

		/**
		 * The meta object literal for the '<em><b>Is Disposed</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation REPOSITORY_SERVICE___IS_DISPOSED = eINSTANCE.getRepositoryService__IsDisposed();

		/**
		 * The meta object literal for the '<em><b>Dispose</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation REPOSITORY_SERVICE___DISPOSE = eINSTANCE.getRepositoryService__Dispose();

		/**
		 * The meta object literal for the '<em><b>Get Resource Set</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation REPOSITORY_SERVICE___GET_RESOURCE_SET = eINSTANCE.getRepositoryService__GetResourceSet();

		/**
		 * The meta object literal for the '<em><b>Create Resource Set</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation REPOSITORY_SERVICE___CREATE_RESOURCE_SET = eINSTANCE.getRepositoryService__CreateResourceSet();

		/**
		 * The meta object literal for the '<em><b>Create Uri</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation REPOSITORY_SERVICE___CREATE_URI__EOBJECT = eINSTANCE.getRepositoryService__CreateUri__EObject();

		/**
		 * The meta object literal for the '<em><b>Create Uri</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation REPOSITORY_SERVICE___CREATE_URI__EOBJECT_MAP = eINSTANCE.getRepositoryService__CreateUri__EObject_Map();

		/**
		 * The meta object literal for the '<em><b>Create Uri</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation REPOSITORY_SERVICE___CREATE_URI__ECLASS_OBJECT = eINSTANCE.getRepositoryService__CreateUri__EClass_Object();

		/**
		 * The meta object literal for the '<em><b>Create Proxy</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation REPOSITORY_SERVICE___CREATE_PROXY__ECLASS_OBJECT = eINSTANCE.getRepositoryService__CreateProxy__EClass_Object();

		/**
		 * The meta object literal for the '<em><b>Proxify</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation REPOSITORY_SERVICE___PROXIFY__EOBJECT = eINSTANCE.getRepositoryService__Proxify__EObject();

		/**
		 * The meta object literal for the '<em><b>Attach</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation REPOSITORY_SERVICE___ATTACH__EOBJECT = eINSTANCE.getRepositoryService__Attach__EObject();

		/**
		 * The meta object literal for the '<em><b>Attach</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation REPOSITORY_SERVICE___ATTACH__EOBJECT_MAP = eINSTANCE.getRepositoryService__Attach__EObject_Map();

		/**
		 * The meta object literal for the '<em><b>Detach</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation REPOSITORY_SERVICE___DETACH__EOBJECT = eINSTANCE.getRepositoryService__Detach__EObject();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.persistence.repository.api.ReadRepository <em>Read Repository</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.repository.api.ReadRepository
		 * @see org.eclipse.fennec.persistence.repository.api.impl.RepositoryApiPackageImpl#getReadRepository()
		 * @generated
		 */
		EClass READ_REPOSITORY = eINSTANCE.getReadRepository();

		/**
		 * The meta object literal for the '<em><b>Get Resource</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation READ_REPOSITORY___GET_RESOURCE__URI_BOOLEAN = eINSTANCE.getReadRepository__GetResource__URI_boolean();

		/**
		 * The meta object literal for the '<em><b>Get EObject</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation READ_REPOSITORY___GET_EOBJECT__URI = eINSTANCE.getReadRepository__GetEObject__URI();

		/**
		 * The meta object literal for the '<em><b>Get EObject</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation READ_REPOSITORY___GET_EOBJECT__URI_MAP = eINSTANCE.getReadRepository__GetEObject__URI_Map();

		/**
		 * The meta object literal for the '<em><b>Get EObject</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation READ_REPOSITORY___GET_EOBJECT__ECLASS_OBJECT = eINSTANCE.getReadRepository__GetEObject__EClass_Object();

		/**
		 * The meta object literal for the '<em><b>Get EObject</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation READ_REPOSITORY___GET_EOBJECT__ECLASS_OBJECT_MAP = eINSTANCE.getReadRepository__GetEObject__EClass_Object_Map();

		/**
		 * The meta object literal for the '<em><b>Get All EObjects</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation READ_REPOSITORY___GET_ALL_EOBJECTS__ECLASS = eINSTANCE.getReadRepository__GetAllEObjects__EClass();

		/**
		 * The meta object literal for the '<em><b>Get All EObjects</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation READ_REPOSITORY___GET_ALL_EOBJECTS__ECLASS_MAP = eINSTANCE.getReadRepository__GetAllEObjects__EClass_Map();

		/**
		 * The meta object literal for the '<em><b>Count</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation READ_REPOSITORY___COUNT__ECLASS = eINSTANCE.getReadRepository__Count__EClass();

		/**
		 * The meta object literal for the '<em><b>Count</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation READ_REPOSITORY___COUNT__ECLASS_MAP = eINSTANCE.getReadRepository__Count__EClass_Map();

		/**
		 * The meta object literal for the '<em><b>Exist</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation READ_REPOSITORY___EXIST__URI = eINSTANCE.getReadRepository__Exist__URI();

		/**
		 * The meta object literal for the '<em><b>Exist</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation READ_REPOSITORY___EXIST__ECLASS_OBJECT = eINSTANCE.getReadRepository__Exist__EClass_Object();

		/**
		 * The meta object literal for the '<em><b>Reload</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation READ_REPOSITORY___RELOAD__EOBJECT = eINSTANCE.getReadRepository__Reload__EObject();

		/**
		 * The meta object literal for the '<em><b>Find</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation READ_REPOSITORY___FIND__QUERY = eINSTANCE.getReadRepository__Find__Query();

		/**
		 * The meta object literal for the '<em><b>Find</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation READ_REPOSITORY___FIND__QUERY_MAP_MAP = eINSTANCE.getReadRepository__Find__Query_Map_Map();

		/**
		 * The meta object literal for the '<em><b>Find</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation READ_REPOSITORY___FIND__STRING_MAP_MAP = eINSTANCE.getReadRepository__Find__String_Map_Map();

		/**
		 * The meta object literal for the '<em><b>Count</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation READ_REPOSITORY___COUNT__QUERY = eINSTANCE.getReadRepository__Count__Query();

		/**
		 * The meta object literal for the '<em><b>Prepare</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation READ_REPOSITORY___PREPARE__QUERY = eINSTANCE.getReadRepository__Prepare__Query();

		/**
		 * The meta object literal for the '<em><b>Prepare</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation READ_REPOSITORY___PREPARE__STRING = eINSTANCE.getReadRepository__Prepare__String();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.persistence.repository.api.PreparedQuery <em>Prepared Query</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.repository.api.PreparedQuery
		 * @see org.eclipse.fennec.persistence.repository.api.impl.RepositoryApiPackageImpl#getPreparedQuery()
		 * @generated
		 */
		EClass PREPARED_QUERY = eINSTANCE.getPreparedQuery();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation PREPARED_QUERY___NAME = eINSTANCE.getPreparedQuery__Name();

		/**
		 * The meta object literal for the '<em><b>Query</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation PREPARED_QUERY___QUERY = eINSTANCE.getPreparedQuery__Query();

		/**
		 * The meta object literal for the '<em><b>Parameter Declarations</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation PREPARED_QUERY___PARAMETER_DECLARATIONS = eINSTANCE.getPreparedQuery__ParameterDeclarations();

		/**
		 * The meta object literal for the '<em><b>Execute</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation PREPARED_QUERY___EXECUTE__MAP = eINSTANCE.getPreparedQuery__Execute__Map();

		/**
		 * The meta object literal for the '<em><b>Execute</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation PREPARED_QUERY___EXECUTE__MAP_MAP = eINSTANCE.getPreparedQuery__Execute__Map_Map();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.persistence.repository.api.WriteRepository <em>Write Repository</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.repository.api.WriteRepository
		 * @see org.eclipse.fennec.persistence.repository.api.impl.RepositoryApiPackageImpl#getWriteRepository()
		 * @generated
		 */
		EClass WRITE_REPOSITORY = eINSTANCE.getWriteRepository();

		/**
		 * The meta object literal for the '<em><b>Save</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WRITE_REPOSITORY___SAVE__EOBJECT = eINSTANCE.getWriteRepository__Save__EObject();

		/**
		 * The meta object literal for the '<em><b>Save</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WRITE_REPOSITORY___SAVE__EOBJECT_MAP = eINSTANCE.getWriteRepository__Save__EObject_Map();

		/**
		 * The meta object literal for the '<em><b>Save</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WRITE_REPOSITORY___SAVE__EOBJECT_URI = eINSTANCE.getWriteRepository__Save__EObject_URI();

		/**
		 * The meta object literal for the '<em><b>Save</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WRITE_REPOSITORY___SAVE__EOBJECT_URI_MAP = eINSTANCE.getWriteRepository__Save__EObject_URI_Map();

		/**
		 * The meta object literal for the '<em><b>Save All</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WRITE_REPOSITORY___SAVE_ALL__COLLECTION = eINSTANCE.getWriteRepository__SaveAll__Collection();

		/**
		 * The meta object literal for the '<em><b>Save All</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WRITE_REPOSITORY___SAVE_ALL__COLLECTION_MAP = eINSTANCE.getWriteRepository__SaveAll__Collection_Map();

		/**
		 * The meta object literal for the '<em><b>Delete</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WRITE_REPOSITORY___DELETE__EOBJECT = eINSTANCE.getWriteRepository__Delete__EObject();

		/**
		 * The meta object literal for the '<em><b>Delete</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WRITE_REPOSITORY___DELETE__EOBJECT_MAP = eINSTANCE.getWriteRepository__Delete__EObject_Map();

		/**
		 * The meta object literal for the '<em><b>Delete</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WRITE_REPOSITORY___DELETE__URI = eINSTANCE.getWriteRepository__Delete__URI();

		/**
		 * The meta object literal for the '<em><b>Delete</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WRITE_REPOSITORY___DELETE__URI_MAP = eINSTANCE.getWriteRepository__Delete__URI_Map();

		/**
		 * The meta object literal for the '<em><b>Execute</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WRITE_REPOSITORY___EXECUTE__COMMAND = eINSTANCE.getWriteRepository__Execute__Command();

		/**
		 * The meta object literal for the '<em><b>Execute</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WRITE_REPOSITORY___EXECUTE__COMMAND_MAP_MAP = eINSTANCE.getWriteRepository__Execute__Command_Map_Map();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.persistence.repository.api.Repository <em>Repository</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.repository.api.Repository
		 * @see org.eclipse.fennec.persistence.repository.api.impl.RepositoryApiPackageImpl#getRepository()
		 * @generated
		 */
		EClass REPOSITORY = eINSTANCE.getRepository();

		/**
		 * The meta object literal for the '<em>Uri</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.common.util.URI
		 * @see org.eclipse.fennec.persistence.repository.api.impl.RepositoryApiPackageImpl#getUri()
		 * @generated
		 */
		EDataType URI = eINSTANCE.getUri();

		/**
		 * The meta object literal for the '<em>Persistence Capabilities</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.capabilities.PersistenceCapabilities
		 * @see org.eclipse.fennec.persistence.repository.api.impl.RepositoryApiPackageImpl#getPersistenceCapabilities()
		 * @generated
		 */
		EDataType PERSISTENCE_CAPABILITIES = eINSTANCE.getPersistenceCapabilities();

		/**
		 * The meta object literal for the '<em>EObject Collection</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.util.Collection
		 * @see org.eclipse.fennec.persistence.repository.api.impl.RepositoryApiPackageImpl#getEObjectCollection()
		 * @generated
		 */
		EDataType EOBJECT_COLLECTION = eINSTANCE.getEObjectCollection();

	}

} //RepositoryApiPackage
