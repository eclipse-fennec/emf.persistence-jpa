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
package org.eclipse.fennec.persistence.query.api;


import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;

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
 * Backend-neutral query SPI. A QueryProcessor per backend translates the canonical query model (query.ecore) into a backend-typed QueryPlan; the backend resource executes the plan. Capabilities are declared per backend and violations are diagnosed via EMF Diagnostics — never silently post-filtered in memory. See docs/unified-persistence/query-processor-spi.md.
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.persistence.query.api.QueryApiFactory
 * @model kind="package"
 *        annotation="Version value='1.0'"
 * @generated
 */
@ProviderType
@EPackage(uri = QueryApiPackage.eNS_URI, fingerprint = "fp1:4fdb91b6a1d6b2f9aa44bd91dd50505dbd752878944f6cd0ffbd644c9152c70d", genModel = "/model/query-api.genmodel", genModelSourceLocations = {"model/query-api.genmodel","org.eclipse.fennec.persistence.query/model/query-api.genmodel"}, ecore = "/model/query-api.ecore", ecoreSourceLocations = "/model/query-api.ecore")
public interface QueryApiPackage extends org.eclipse.emf.ecore.EPackage {
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
	String eNS_URI = "https://eclipse.org/fennec/persistence/query/api/1.0.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "query.api";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	QueryApiPackage eINSTANCE = org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl.init();

	/**
	 * The meta object id for the '{@link java.lang.AutoCloseable <em>Auto Closeable</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.lang.AutoCloseable
	 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getAutoCloseable()
	 * @generated
	 */
	int AUTO_CLOSEABLE = 0;

	/**
	 * The number of structural features of the '<em>Auto Closeable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AUTO_CLOSEABLE_FEATURE_COUNT = 0;

	/**
	 * The number of operations of the '<em>Auto Closeable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AUTO_CLOSEABLE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.query.api.QueryPlan <em>Query Plan</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.query.api.QueryPlan
	 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getQueryPlan()
	 * @generated
	 */
	int QUERY_PLAN = 1;

	/**
	 * The number of structural features of the '<em>Query Plan</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_PLAN_FEATURE_COUNT = 0;

	/**
	 * The operation id for the '<em>Source</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_PLAN___SOURCE = 0;

	/**
	 * The operation id for the '<em>Shape</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_PLAN___SHAPE = 1;

	/**
	 * The number of operations of the '<em>Query Plan</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_PLAN_OPERATION_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.query.api.QueryContext <em>Query Context</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.query.api.QueryContext
	 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getQueryContext()
	 * @generated
	 */
	int QUERY_CONTEXT = 2;

	/**
	 * The number of structural features of the '<em>Query Context</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_CONTEXT_FEATURE_COUNT = 0;

	/**
	 * The operation id for the '<em>Root EClass</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_CONTEXT___ROOT_ECLASS = 0;

	/**
	 * The operation id for the '<em>Converter</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_CONTEXT___CONVERTER = 1;

	/**
	 * The operation id for the '<em>Parameters</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_CONTEXT___PARAMETERS = 2;

	/**
	 * The operation id for the '<em>Options</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_CONTEXT___OPTIONS = 3;

	/**
	 * The number of operations of the '<em>Query Context</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_CONTEXT_OPERATION_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.query.api.QueryProcessor <em>Query Processor</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.query.api.QueryProcessor
	 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getQueryProcessor()
	 * @generated
	 */
	int QUERY_PROCESSOR = 3;

	/**
	 * The number of structural features of the '<em>Query Processor</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_PROCESSOR_FEATURE_COUNT = 0;

	/**
	 * The operation id for the '<em>Backend</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_PROCESSOR___BACKEND = 0;

	/**
	 * The operation id for the '<em>Capabilities</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_PROCESSOR___CAPABILITIES = 1;

	/**
	 * The operation id for the '<em>Validate</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_PROCESSOR___VALIDATE__QUERY_ECLASS = 2;

	/**
	 * The operation id for the '<em>Translate</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_PROCESSOR___TRANSLATE__QUERY_QUERYCONTEXT = 3;

	/**
	 * The number of operations of the '<em>Query Processor</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_PROCESSOR_OPERATION_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.query.api.QueryResultRow <em>Query Result Row</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.query.api.QueryResultRow
	 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getQueryResultRow()
	 * @generated
	 */
	int QUERY_RESULT_ROW = 4;

	/**
	 * The number of structural features of the '<em>Query Result Row</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_RESULT_ROW_FEATURE_COUNT = 0;

	/**
	 * The operation id for the '<em>Get</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_RESULT_ROW___GET__STRING = 0;

	/**
	 * The operation id for the '<em>Get</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_RESULT_ROW___GET__INT = 1;

	/**
	 * The operation id for the '<em>Values</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_RESULT_ROW___VALUES = 2;

	/**
	 * The number of operations of the '<em>Query Result Row</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_RESULT_ROW_OPERATION_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.query.api.QueryResult <em>Query Result</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.query.api.QueryResult
	 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getQueryResult()
	 * @generated
	 */
	int QUERY_RESULT = 5;

	/**
	 * The number of structural features of the '<em>Query Result</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_RESULT_FEATURE_COUNT = AUTO_CLOSEABLE_FEATURE_COUNT + 0;

	/**
	 * The operation id for the '<em>Shape</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_RESULT___SHAPE = AUTO_CLOSEABLE_OPERATION_COUNT + 0;

	/**
	 * The operation id for the '<em>Objects</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_RESULT___OBJECTS = AUTO_CLOSEABLE_OPERATION_COUNT + 1;

	/**
	 * The operation id for the '<em>Rows</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_RESULT___ROWS = AUTO_CLOSEABLE_OPERATION_COUNT + 2;

	/**
	 * The operation id for the '<em>Count</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_RESULT___COUNT = AUTO_CLOSEABLE_OPERATION_COUNT + 3;

	/**
	 * The operation id for the '<em>Close</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_RESULT___CLOSE = AUTO_CLOSEABLE_OPERATION_COUNT + 4;

	/**
	 * The number of operations of the '<em>Query Result</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_RESULT_OPERATION_COUNT = AUTO_CLOSEABLE_OPERATION_COUNT + 5;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.query.api.QueryableResource <em>Queryable Resource</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.query.api.QueryableResource
	 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getQueryableResource()
	 * @generated
	 */
	int QUERYABLE_RESOURCE = 6;

	/**
	 * The number of structural features of the '<em>Queryable Resource</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERYABLE_RESOURCE_FEATURE_COUNT = 0;

	/**
	 * The operation id for the '<em>Query</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERYABLE_RESOURCE___QUERY__QUERY = 0;

	/**
	 * The operation id for the '<em>Query</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERYABLE_RESOURCE___QUERY__QUERY_MAP_MAP = 1;

	/**
	 * The operation id for the '<em>Query</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERYABLE_RESOURCE___QUERY__STRING_MAP_MAP = 2;

	/**
	 * The number of operations of the '<em>Queryable Resource</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERYABLE_RESOURCE_OPERATION_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.query.api.CommandResource <em>Command Resource</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.query.api.CommandResource
	 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getCommandResource()
	 * @generated
	 */
	int COMMAND_RESOURCE = 7;

	/**
	 * The number of structural features of the '<em>Command Resource</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMAND_RESOURCE_FEATURE_COUNT = 0;

	/**
	 * The operation id for the '<em>Execute</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMAND_RESOURCE___EXECUTE__COMMAND = 0;

	/**
	 * The operation id for the '<em>Begin</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMAND_RESOURCE___BEGIN = 1;

	/**
	 * The operation id for the '<em>Capabilities</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMAND_RESOURCE___CAPABILITIES = 2;

	/**
	 * The number of operations of the '<em>Command Resource</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMAND_RESOURCE_OPERATION_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.query.api.QueryShape <em>Query Shape</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.query.api.QueryShape
	 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getQueryShape()
	 * @generated
	 */
	int QUERY_SHAPE = 8;

	/**
	 * The meta object id for the '<em>Diagnostic</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.common.util.Diagnostic
	 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getDiagnostic()
	 * @generated
	 */
	int DIAGNOSTIC = 9;

	/**
	 * The meta object id for the '<em>Converter Service</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.api.ConverterService
	 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getConverterService()
	 * @generated
	 */
	int CONVERTER_SERVICE = 10;

	/**
	 * The meta object id for the '<em>Query Exception</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.query.QueryException
	 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getQueryException()
	 * @generated
	 */
	int QUERY_EXCEPTION = 11;

	/**
	 * The meta object id for the '<em>Command Transaction</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.query.support.CommandTransaction
	 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getCommandTransaction()
	 * @generated
	 */
	int COMMAND_TRANSACTION = 12;

	/**
	 * The meta object id for the '<em>IO Exception</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.io.IOException
	 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getIOException()
	 * @generated
	 */
	int IO_EXCEPTION = 13;

	/**
	 * The meta object id for the '<em>EObject Stream</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.stream.Stream
	 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getEObjectStream()
	 * @generated
	 */
	int EOBJECT_STREAM = 14;

	/**
	 * The meta object id for the '<em>Row Stream</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.stream.Stream
	 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getRowStream()
	 * @generated
	 */
	int ROW_STREAM = 15;

	/**
	 * The meta object id for the '<em>Object List</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.List
	 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getObjectList()
	 * @generated
	 */
	int OBJECT_LIST = 16;

	/**
	 * The meta object id for the '<em>Query Capabilities</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.capabilities.QueryCapabilities
	 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getQueryCapabilities()
	 * @generated
	 */
	int QUERY_CAPABILITIES = 17;

	/**
	 * The meta object id for the '<em>Command Capabilities</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.capabilities.CommandCapabilities
	 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getCommandCapabilities()
	 * @generated
	 */
	int COMMAND_CAPABILITIES = 18;

	/**
	 * The meta object id for the '<em>Parameter Map</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.Map
	 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getParameterMap()
	 * @generated
	 */
	int PARAMETER_MAP = 19;

	/**
	 * The meta object id for the '<em>Options Map</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.Map
	 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getOptionsMap()
	 * @generated
	 */
	int OPTIONS_MAP = 20;


	/**
	 * Returns the meta object for class '{@link java.lang.AutoCloseable <em>Auto Closeable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Auto Closeable</em>'.
	 * @see java.lang.AutoCloseable
	 * @model instanceClass="java.lang.AutoCloseable"
	 * @generated
	 */
	EClass getAutoCloseable();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.query.api.QueryPlan <em>Query Plan</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Query Plan</em>'.
	 * @see org.eclipse.fennec.persistence.query.api.QueryPlan
	 * @generated
	 */
	EClass getQueryPlan();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.query.api.QueryPlan#source() <em>Source</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Source</em>' operation.
	 * @see org.eclipse.fennec.persistence.query.api.QueryPlan#source()
	 * @generated
	 */
	EOperation getQueryPlan__Source();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.query.api.QueryPlan#shape() <em>Shape</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Shape</em>' operation.
	 * @see org.eclipse.fennec.persistence.query.api.QueryPlan#shape()
	 * @generated
	 */
	EOperation getQueryPlan__Shape();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.query.api.QueryContext <em>Query Context</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Query Context</em>'.
	 * @see org.eclipse.fennec.persistence.query.api.QueryContext
	 * @generated
	 */
	EClass getQueryContext();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.query.api.QueryContext#rootEClass() <em>Root EClass</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Root EClass</em>' operation.
	 * @see org.eclipse.fennec.persistence.query.api.QueryContext#rootEClass()
	 * @generated
	 */
	EOperation getQueryContext__RootEClass();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.query.api.QueryContext#converter() <em>Converter</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Converter</em>' operation.
	 * @see org.eclipse.fennec.persistence.query.api.QueryContext#converter()
	 * @generated
	 */
	EOperation getQueryContext__Converter();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.query.api.QueryContext#parameters() <em>Parameters</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Parameters</em>' operation.
	 * @see org.eclipse.fennec.persistence.query.api.QueryContext#parameters()
	 * @generated
	 */
	EOperation getQueryContext__Parameters();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.query.api.QueryContext#options() <em>Options</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Options</em>' operation.
	 * @see org.eclipse.fennec.persistence.query.api.QueryContext#options()
	 * @generated
	 */
	EOperation getQueryContext__Options();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.query.api.QueryProcessor <em>Query Processor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Query Processor</em>'.
	 * @see org.eclipse.fennec.persistence.query.api.QueryProcessor
	 * @generated
	 */
	EClass getQueryProcessor();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.query.api.QueryProcessor#backend() <em>Backend</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Backend</em>' operation.
	 * @see org.eclipse.fennec.persistence.query.api.QueryProcessor#backend()
	 * @generated
	 */
	EOperation getQueryProcessor__Backend();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.query.api.QueryProcessor#capabilities() <em>Capabilities</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Capabilities</em>' operation.
	 * @see org.eclipse.fennec.persistence.query.api.QueryProcessor#capabilities()
	 * @generated
	 */
	EOperation getQueryProcessor__Capabilities();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.query.api.QueryProcessor#validate(org.eclipse.fennec.model.query.Query, org.eclipse.emf.ecore.EClass) <em>Validate</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Validate</em>' operation.
	 * @see org.eclipse.fennec.persistence.query.api.QueryProcessor#validate(org.eclipse.fennec.model.query.Query, org.eclipse.emf.ecore.EClass)
	 * @generated
	 */
	EOperation getQueryProcessor__Validate__Query_EClass();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.query.api.QueryProcessor#translate(org.eclipse.fennec.model.query.Query, org.eclipse.fennec.persistence.query.api.QueryContext) <em>Translate</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Translate</em>' operation.
	 * @see org.eclipse.fennec.persistence.query.api.QueryProcessor#translate(org.eclipse.fennec.model.query.Query, org.eclipse.fennec.persistence.query.api.QueryContext)
	 * @generated
	 */
	EOperation getQueryProcessor__Translate__Query_QueryContext();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.query.api.QueryResultRow <em>Query Result Row</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Query Result Row</em>'.
	 * @see org.eclipse.fennec.persistence.query.api.QueryResultRow
	 * @generated
	 */
	EClass getQueryResultRow();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.query.api.QueryResultRow#get(java.lang.String) <em>Get</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get</em>' operation.
	 * @see org.eclipse.fennec.persistence.query.api.QueryResultRow#get(java.lang.String)
	 * @generated
	 */
	EOperation getQueryResultRow__Get__String();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.query.api.QueryResultRow#get(int) <em>Get</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get</em>' operation.
	 * @see org.eclipse.fennec.persistence.query.api.QueryResultRow#get(int)
	 * @generated
	 */
	EOperation getQueryResultRow__Get__int();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.query.api.QueryResultRow#values() <em>Values</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Values</em>' operation.
	 * @see org.eclipse.fennec.persistence.query.api.QueryResultRow#values()
	 * @generated
	 */
	EOperation getQueryResultRow__Values();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.query.api.QueryResult <em>Query Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Query Result</em>'.
	 * @see org.eclipse.fennec.persistence.query.api.QueryResult
	 * @generated
	 */
	EClass getQueryResult();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.query.api.QueryResult#shape() <em>Shape</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Shape</em>' operation.
	 * @see org.eclipse.fennec.persistence.query.api.QueryResult#shape()
	 * @generated
	 */
	EOperation getQueryResult__Shape();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.query.api.QueryResult#objects() <em>Objects</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Objects</em>' operation.
	 * @see org.eclipse.fennec.persistence.query.api.QueryResult#objects()
	 * @generated
	 */
	EOperation getQueryResult__Objects();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.query.api.QueryResult#rows() <em>Rows</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Rows</em>' operation.
	 * @see org.eclipse.fennec.persistence.query.api.QueryResult#rows()
	 * @generated
	 */
	EOperation getQueryResult__Rows();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.query.api.QueryResult#count() <em>Count</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Count</em>' operation.
	 * @see org.eclipse.fennec.persistence.query.api.QueryResult#count()
	 * @generated
	 */
	EOperation getQueryResult__Count();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.query.api.QueryResult#close() <em>Close</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Close</em>' operation.
	 * @see org.eclipse.fennec.persistence.query.api.QueryResult#close()
	 * @generated
	 */
	EOperation getQueryResult__Close();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.query.api.QueryableResource <em>Queryable Resource</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Queryable Resource</em>'.
	 * @see org.eclipse.fennec.persistence.query.api.QueryableResource
	 * @generated
	 */
	EClass getQueryableResource();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.query.api.QueryableResource#query(org.eclipse.fennec.model.query.Query) <em>Query</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Query</em>' operation.
	 * @see org.eclipse.fennec.persistence.query.api.QueryableResource#query(org.eclipse.fennec.model.query.Query)
	 * @generated
	 */
	EOperation getQueryableResource__Query__Query();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.query.api.QueryableResource#query(org.eclipse.fennec.model.query.Query, java.util.Map, java.util.Map) <em>Query</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Query</em>' operation.
	 * @see org.eclipse.fennec.persistence.query.api.QueryableResource#query(org.eclipse.fennec.model.query.Query, java.util.Map, java.util.Map)
	 * @generated
	 */
	EOperation getQueryableResource__Query__Query_Map_Map();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.query.api.QueryableResource#query(java.lang.String, java.util.Map, java.util.Map) <em>Query</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Query</em>' operation.
	 * @see org.eclipse.fennec.persistence.query.api.QueryableResource#query(java.lang.String, java.util.Map, java.util.Map)
	 * @generated
	 */
	EOperation getQueryableResource__Query__String_Map_Map();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.persistence.query.api.CommandResource <em>Command Resource</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Command Resource</em>'.
	 * @see org.eclipse.fennec.persistence.query.api.CommandResource
	 * @generated
	 */
	EClass getCommandResource();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.query.api.CommandResource#execute(org.eclipse.fennec.model.command.Command) <em>Execute</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Execute</em>' operation.
	 * @see org.eclipse.fennec.persistence.query.api.CommandResource#execute(org.eclipse.fennec.model.command.Command)
	 * @generated
	 */
	EOperation getCommandResource__Execute__Command();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.query.api.CommandResource#begin() <em>Begin</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Begin</em>' operation.
	 * @see org.eclipse.fennec.persistence.query.api.CommandResource#begin()
	 * @generated
	 */
	EOperation getCommandResource__Begin();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.persistence.query.api.CommandResource#capabilities() <em>Capabilities</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Capabilities</em>' operation.
	 * @see org.eclipse.fennec.persistence.query.api.CommandResource#capabilities()
	 * @generated
	 */
	EOperation getCommandResource__Capabilities();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.persistence.query.api.QueryShape <em>Query Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Query Shape</em>'.
	 * @see org.eclipse.fennec.persistence.query.api.QueryShape
	 * @generated
	 */
	EEnum getQueryShape();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.emf.common.util.Diagnostic <em>Diagnostic</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * EMF Diagnostic carrying validation results. ERROR entries name the offending QueryFeature and model element and make a query non-executable.
     * <!-- end-model-doc -->
	 * @return the meta object for data type '<em>Diagnostic</em>'.
	 * @see org.eclipse.emf.common.util.Diagnostic
	 * @model instanceClass="org.eclipse.emf.common.util.Diagnostic"
	 * @generated
	 */
	EDataType getDiagnostic();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.fennec.persistence.api.ConverterService <em>Converter Service</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The shared persistence value converter. Comparator values are EString in the query model; conversion to the target feature's EDataType happens once through this service at translate time — never per backend.
     * <!-- end-model-doc -->
	 * @return the meta object for data type '<em>Converter Service</em>'.
	 * @see org.eclipse.fennec.persistence.api.ConverterService
	 * @model instanceClass="org.eclipse.fennec.persistence.api.ConverterService"
	 * @generated
	 */
	EDataType getConverterService();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.fennec.persistence.query.QueryException <em>Query Exception</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Hand-written exception (lives in src, not generated) signalling that a query could not be validated or translated. Carries the validation Diagnostic when it results from a failed validate call.
     * <!-- end-model-doc -->
	 * @return the meta object for data type '<em>Query Exception</em>'.
	 * @see org.eclipse.fennec.persistence.query.QueryException
	 * @model instanceClass="org.eclipse.fennec.persistence.query.QueryException"
	 * @generated
	 */
	EDataType getQueryException();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.fennec.persistence.query.support.CommandTransaction <em>Command Transaction</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * A transaction bracket handle grouping subsequent execute() calls (issue #108) — hand-written interface in the support package (commit/rollback/AutoCloseable).
     * <!-- end-model-doc -->
	 * @return the meta object for data type '<em>Command Transaction</em>'.
	 * @see org.eclipse.fennec.persistence.query.support.CommandTransaction
	 * @model instanceClass="org.eclipse.fennec.persistence.query.support.CommandTransaction"
	 * @generated
	 */
	EDataType getCommandTransaction();

	/**
	 * Returns the meta object for data type '{@link java.io.IOException <em>IO Exception</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * java.io.IOException — thrown by resource-level query execution, consistent with the EMF Resource load/save contract.
     * <!-- end-model-doc -->
	 * @return the meta object for data type '<em>IO Exception</em>'.
	 * @see java.io.IOException
	 * @model instanceClass="java.io.IOException"
	 * @generated
	 */
	EDataType getIOException();

	/**
	 * Returns the meta object for data type '{@link java.util.stream.Stream <em>EObject Stream</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * A lazy java.util.stream.Stream of EObjects. Backend cursors materialise objects one by one while the stream is consumed; closing the owning QueryResult closes the stream and releases the cursor.
     * <!-- end-model-doc -->
	 * @return the meta object for data type '<em>EObject Stream</em>'.
	 * @see java.util.stream.Stream
	 * @model instanceClass="java.util.stream.Stream&lt;org.eclipse.emf.ecore.EObject&gt;"
	 * @generated
	 */
	EDataType getEObjectStream();

	/**
	 * Returns the meta object for data type '{@link java.util.stream.Stream <em>Row Stream</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * A lazy java.util.stream.Stream of QueryResultRows for PROJECTION and AGGREGATION shapes.
     * <!-- end-model-doc -->
	 * @return the meta object for data type '<em>Row Stream</em>'.
	 * @see java.util.stream.Stream
	 * @model instanceClass="java.util.stream.Stream&lt;org.eclipse.fennec.persistence.query.api.QueryResultRow&gt;"
	 * @generated
	 */
	EDataType getRowStream();

	/**
	 * Returns the meta object for data type '{@link java.util.List <em>Object List</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * An immutable java.util.List of row cell values in subject order.
     * <!-- end-model-doc -->
	 * @return the meta object for data type '<em>Object List</em>'.
	 * @see java.util.List
	 * @model instanceClass="java.util.List&lt;java.lang.Object&gt;"
	 * @generated
	 */
	EDataType getObjectList();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.fennec.persistence.capabilities.QueryCapabilities <em>Query Capabilities</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * What a QueryProcessor declares it serves natively, plus its structural limits. Plain Java in the capabilities bundle rather than an EClass here (issue #134, contract §5a): a capability is a value that gets asked, not an EObject that gets loaded, and the declaration surface must not depend on the query model. Wrapped as an EDataType for the same reason ConverterService and Diagnostic are.
     * <!-- end-model-doc -->
	 * @return the meta object for data type '<em>Query Capabilities</em>'.
	 * @see org.eclipse.fennec.persistence.capabilities.QueryCapabilities
	 * @model instanceClass="org.eclipse.fennec.persistence.capabilities.QueryCapabilities"
	 * @generated
	 */
	EDataType getQueryCapabilities();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.fennec.persistence.capabilities.CommandCapabilities <em>Command Capabilities</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * What a CommandResource declares it serves, backend-wide and narrowed per EClass (issue #114). Plain Java in the capabilities bundle - see QueryCapabilities.
     * <!-- end-model-doc -->
	 * @return the meta object for data type '<em>Command Capabilities</em>'.
	 * @see org.eclipse.fennec.persistence.capabilities.CommandCapabilities
	 * @model instanceClass="org.eclipse.fennec.persistence.capabilities.CommandCapabilities"
	 * @generated
	 */
	EDataType getCommandCapabilities();

	/**
	 * Returns the meta object for data type '{@link java.util.Map <em>Parameter Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Bound values for placeholders used by prepared queries (see QueryFeature PARAMETERS).
     * <!-- end-model-doc -->
	 * @return the meta object for data type '<em>Parameter Map</em>'.
	 * @see java.util.Map
	 * @model instanceClass="java.util.Map&lt;java.lang.String, java.lang.Object&gt;"
	 * @generated
	 */
	EDataType getParameterMap();

	/**
	 * Returns the meta object for data type '{@link java.util.Map <em>Options Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Backend options for translation/execution (e.g. page size, batch size, read options) — the same option map style used by the EMF Resource load/save contract.
     * <!-- end-model-doc -->
	 * @return the meta object for data type '<em>Options Map</em>'.
	 * @see java.util.Map
	 * @model instanceClass="java.util.Map&lt;?, ?&gt;"
	 * @generated
	 */
	EDataType getOptionsMap();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	QueryApiFactory getQueryApiFactory();

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
		 * The meta object literal for the '{@link java.lang.AutoCloseable <em>Auto Closeable</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.lang.AutoCloseable
		 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getAutoCloseable()
		 * @generated
		 */
		EClass AUTO_CLOSEABLE = eINSTANCE.getAutoCloseable();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.persistence.query.api.QueryPlan <em>Query Plan</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.query.api.QueryPlan
		 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getQueryPlan()
		 * @generated
		 */
		EClass QUERY_PLAN = eINSTANCE.getQueryPlan();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation QUERY_PLAN___SOURCE = eINSTANCE.getQueryPlan__Source();

		/**
		 * The meta object literal for the '<em><b>Shape</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation QUERY_PLAN___SHAPE = eINSTANCE.getQueryPlan__Shape();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.persistence.query.api.QueryContext <em>Query Context</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.query.api.QueryContext
		 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getQueryContext()
		 * @generated
		 */
		EClass QUERY_CONTEXT = eINSTANCE.getQueryContext();

		/**
		 * The meta object literal for the '<em><b>Root EClass</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation QUERY_CONTEXT___ROOT_ECLASS = eINSTANCE.getQueryContext__RootEClass();

		/**
		 * The meta object literal for the '<em><b>Converter</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation QUERY_CONTEXT___CONVERTER = eINSTANCE.getQueryContext__Converter();

		/**
		 * The meta object literal for the '<em><b>Parameters</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation QUERY_CONTEXT___PARAMETERS = eINSTANCE.getQueryContext__Parameters();

		/**
		 * The meta object literal for the '<em><b>Options</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation QUERY_CONTEXT___OPTIONS = eINSTANCE.getQueryContext__Options();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.persistence.query.api.QueryProcessor <em>Query Processor</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.query.api.QueryProcessor
		 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getQueryProcessor()
		 * @generated
		 */
		EClass QUERY_PROCESSOR = eINSTANCE.getQueryProcessor();

		/**
		 * The meta object literal for the '<em><b>Backend</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation QUERY_PROCESSOR___BACKEND = eINSTANCE.getQueryProcessor__Backend();

		/**
		 * The meta object literal for the '<em><b>Capabilities</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation QUERY_PROCESSOR___CAPABILITIES = eINSTANCE.getQueryProcessor__Capabilities();

		/**
		 * The meta object literal for the '<em><b>Validate</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation QUERY_PROCESSOR___VALIDATE__QUERY_ECLASS = eINSTANCE.getQueryProcessor__Validate__Query_EClass();

		/**
		 * The meta object literal for the '<em><b>Translate</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation QUERY_PROCESSOR___TRANSLATE__QUERY_QUERYCONTEXT = eINSTANCE.getQueryProcessor__Translate__Query_QueryContext();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.persistence.query.api.QueryResultRow <em>Query Result Row</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.query.api.QueryResultRow
		 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getQueryResultRow()
		 * @generated
		 */
		EClass QUERY_RESULT_ROW = eINSTANCE.getQueryResultRow();

		/**
		 * The meta object literal for the '<em><b>Get</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation QUERY_RESULT_ROW___GET__STRING = eINSTANCE.getQueryResultRow__Get__String();

		/**
		 * The meta object literal for the '<em><b>Get</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation QUERY_RESULT_ROW___GET__INT = eINSTANCE.getQueryResultRow__Get__int();

		/**
		 * The meta object literal for the '<em><b>Values</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation QUERY_RESULT_ROW___VALUES = eINSTANCE.getQueryResultRow__Values();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.persistence.query.api.QueryResult <em>Query Result</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.query.api.QueryResult
		 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getQueryResult()
		 * @generated
		 */
		EClass QUERY_RESULT = eINSTANCE.getQueryResult();

		/**
		 * The meta object literal for the '<em><b>Shape</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation QUERY_RESULT___SHAPE = eINSTANCE.getQueryResult__Shape();

		/**
		 * The meta object literal for the '<em><b>Objects</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation QUERY_RESULT___OBJECTS = eINSTANCE.getQueryResult__Objects();

		/**
		 * The meta object literal for the '<em><b>Rows</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation QUERY_RESULT___ROWS = eINSTANCE.getQueryResult__Rows();

		/**
		 * The meta object literal for the '<em><b>Count</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation QUERY_RESULT___COUNT = eINSTANCE.getQueryResult__Count();

		/**
		 * The meta object literal for the '<em><b>Close</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation QUERY_RESULT___CLOSE = eINSTANCE.getQueryResult__Close();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.persistence.query.api.QueryableResource <em>Queryable Resource</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.query.api.QueryableResource
		 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getQueryableResource()
		 * @generated
		 */
		EClass QUERYABLE_RESOURCE = eINSTANCE.getQueryableResource();

		/**
		 * The meta object literal for the '<em><b>Query</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation QUERYABLE_RESOURCE___QUERY__QUERY = eINSTANCE.getQueryableResource__Query__Query();

		/**
		 * The meta object literal for the '<em><b>Query</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation QUERYABLE_RESOURCE___QUERY__QUERY_MAP_MAP = eINSTANCE.getQueryableResource__Query__Query_Map_Map();

		/**
		 * The meta object literal for the '<em><b>Query</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation QUERYABLE_RESOURCE___QUERY__STRING_MAP_MAP = eINSTANCE.getQueryableResource__Query__String_Map_Map();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.persistence.query.api.CommandResource <em>Command Resource</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.query.api.CommandResource
		 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getCommandResource()
		 * @generated
		 */
		EClass COMMAND_RESOURCE = eINSTANCE.getCommandResource();

		/**
		 * The meta object literal for the '<em><b>Execute</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation COMMAND_RESOURCE___EXECUTE__COMMAND = eINSTANCE.getCommandResource__Execute__Command();

		/**
		 * The meta object literal for the '<em><b>Begin</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation COMMAND_RESOURCE___BEGIN = eINSTANCE.getCommandResource__Begin();

		/**
		 * The meta object literal for the '<em><b>Capabilities</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation COMMAND_RESOURCE___CAPABILITIES = eINSTANCE.getCommandResource__Capabilities();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.persistence.query.api.QueryShape <em>Query Shape</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.query.api.QueryShape
		 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getQueryShape()
		 * @generated
		 */
		EEnum QUERY_SHAPE = eINSTANCE.getQueryShape();

		/**
		 * The meta object literal for the '<em>Diagnostic</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.common.util.Diagnostic
		 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getDiagnostic()
		 * @generated
		 */
		EDataType DIAGNOSTIC = eINSTANCE.getDiagnostic();

		/**
		 * The meta object literal for the '<em>Converter Service</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.api.ConverterService
		 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getConverterService()
		 * @generated
		 */
		EDataType CONVERTER_SERVICE = eINSTANCE.getConverterService();

		/**
		 * The meta object literal for the '<em>Query Exception</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.query.QueryException
		 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getQueryException()
		 * @generated
		 */
		EDataType QUERY_EXCEPTION = eINSTANCE.getQueryException();

		/**
		 * The meta object literal for the '<em>Command Transaction</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.query.support.CommandTransaction
		 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getCommandTransaction()
		 * @generated
		 */
		EDataType COMMAND_TRANSACTION = eINSTANCE.getCommandTransaction();

		/**
		 * The meta object literal for the '<em>IO Exception</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.io.IOException
		 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getIOException()
		 * @generated
		 */
		EDataType IO_EXCEPTION = eINSTANCE.getIOException();

		/**
		 * The meta object literal for the '<em>EObject Stream</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.util.stream.Stream
		 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getEObjectStream()
		 * @generated
		 */
		EDataType EOBJECT_STREAM = eINSTANCE.getEObjectStream();

		/**
		 * The meta object literal for the '<em>Row Stream</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.util.stream.Stream
		 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getRowStream()
		 * @generated
		 */
		EDataType ROW_STREAM = eINSTANCE.getRowStream();

		/**
		 * The meta object literal for the '<em>Object List</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.util.List
		 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getObjectList()
		 * @generated
		 */
		EDataType OBJECT_LIST = eINSTANCE.getObjectList();

		/**
		 * The meta object literal for the '<em>Query Capabilities</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.capabilities.QueryCapabilities
		 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getQueryCapabilities()
		 * @generated
		 */
		EDataType QUERY_CAPABILITIES = eINSTANCE.getQueryCapabilities();

		/**
		 * The meta object literal for the '<em>Command Capabilities</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.capabilities.CommandCapabilities
		 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getCommandCapabilities()
		 * @generated
		 */
		EDataType COMMAND_CAPABILITIES = eINSTANCE.getCommandCapabilities();

		/**
		 * The meta object literal for the '<em>Parameter Map</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.util.Map
		 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getParameterMap()
		 * @generated
		 */
		EDataType PARAMETER_MAP = eINSTANCE.getParameterMap();

		/**
		 * The meta object literal for the '<em>Options Map</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.util.Map
		 * @see org.eclipse.fennec.persistence.query.api.impl.QueryApiPackageImpl#getOptionsMap()
		 * @generated
		 */
		EDataType OPTIONS_MAP = eINSTANCE.getOptionsMap();

	}

} //QueryApiPackage
