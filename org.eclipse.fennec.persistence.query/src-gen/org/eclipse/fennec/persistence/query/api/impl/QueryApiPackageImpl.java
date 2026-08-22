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
package org.eclipse.fennec.persistence.query.api.impl;

import java.io.IOException;

import java.lang.AutoCloseable;

import java.util.List;
import java.util.Map;

import java.util.stream.Stream;

import org.eclipse.emf.common.util.Diagnostic;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.fennec.model.command.CommandPackage;

import org.eclipse.fennec.model.expression.ExpressionPackage;

import org.eclipse.fennec.model.query.QueryPackage;

import org.eclipse.fennec.model.stream.StreamPackage;

import org.eclipse.fennec.persistence.api.ConverterService;

import org.eclipse.fennec.persistence.capabilities.QueryCapabilities;

import org.eclipse.fennec.persistence.query.QueryException;

import org.eclipse.fennec.persistence.query.api.CommandResource;
import org.eclipse.fennec.persistence.query.api.Hit;
import org.eclipse.fennec.persistence.query.api.QueryApiFactory;
import org.eclipse.fennec.persistence.query.api.QueryApiPackage;
import org.eclipse.fennec.persistence.query.api.QueryContext;
import org.eclipse.fennec.persistence.query.api.QueryPlan;
import org.eclipse.fennec.persistence.query.api.QueryProcessor;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.eclipse.fennec.persistence.query.api.QueryResultRow;
import org.eclipse.fennec.persistence.query.api.QueryShape;
import org.eclipse.fennec.persistence.query.api.QueryableResource;

import org.eclipse.fennec.persistence.query.support.CommandTransaction;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class QueryApiPackageImpl extends EPackageImpl implements QueryApiPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass autoCloseableEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass queryPlanEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass queryContextEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass queryProcessorEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass queryResultRowEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass queryResultEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass hitEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass queryableResourceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass commandResourceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum queryShapeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType diagnosticEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType converterServiceEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType queryExceptionEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType commandTransactionEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType ioExceptionEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType eObjectStreamEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType rowStreamEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType objectListEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType queryCapabilitiesEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType parameterMapEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType scoreMapEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType hitStreamEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType optionsMapEDataType = null;

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
	 * @see org.eclipse.fennec.persistence.query.api.QueryApiPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private QueryApiPackageImpl() {
		super(eNS_URI, QueryApiFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link QueryApiPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static QueryApiPackage init() {
		if (isInited) return (QueryApiPackage)EPackage.Registry.INSTANCE.getEPackage(QueryApiPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredQueryApiPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		QueryApiPackageImpl theQueryApiPackage = registeredQueryApiPackage instanceof QueryApiPackageImpl ? (QueryApiPackageImpl)registeredQueryApiPackage : new QueryApiPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		QueryPackage.eINSTANCE.eClass();
		ExpressionPackage.eINSTANCE.eClass();
		CommandPackage.eINSTANCE.eClass();
		StreamPackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theQueryApiPackage.createPackageContents();

		// Initialize created meta-data
		theQueryApiPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theQueryApiPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(QueryApiPackage.eNS_URI, theQueryApiPackage);
		return theQueryApiPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAutoCloseable() {
		return autoCloseableEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getQueryPlan() {
		return queryPlanEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getQueryPlan__Source() {
		return queryPlanEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getQueryPlan__Shape() {
		return queryPlanEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getQueryContext() {
		return queryContextEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getQueryContext__RootEClass() {
		return queryContextEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getQueryContext__Converter() {
		return queryContextEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getQueryContext__Parameters() {
		return queryContextEClass.getEOperations().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getQueryContext__Options() {
		return queryContextEClass.getEOperations().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getQueryProcessor() {
		return queryProcessorEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getQueryProcessor__Backend() {
		return queryProcessorEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getQueryProcessor__Capabilities() {
		return queryProcessorEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getQueryProcessor__Validate__Query_EClass() {
		return queryProcessorEClass.getEOperations().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getQueryProcessor__Translate__Query_QueryContext() {
		return queryProcessorEClass.getEOperations().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getQueryResultRow() {
		return queryResultRowEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getQueryResultRow__Get__String() {
		return queryResultRowEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getQueryResultRow__Get__int() {
		return queryResultRowEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getQueryResultRow__Values() {
		return queryResultRowEClass.getEOperations().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getQueryResult() {
		return queryResultEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getQueryResult__Shape() {
		return queryResultEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getQueryResult__Objects() {
		return queryResultEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getQueryResult__Rows() {
		return queryResultEClass.getEOperations().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getQueryResult__Count() {
		return queryResultEClass.getEOperations().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getQueryResult__Hits() {
		return queryResultEClass.getEOperations().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getQueryResult__Scores() {
		return queryResultEClass.getEOperations().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getQueryResult__Close() {
		return queryResultEClass.getEOperations().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getHit() {
		return hitEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getHit__Object() {
		return hitEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getHit__Score() {
		return hitEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getQueryableResource() {
		return queryableResourceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getQueryableResource__Query__Query() {
		return queryableResourceEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getQueryableResource__Query__Query_Map_Map() {
		return queryableResourceEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getQueryableResource__Query__String_Map_Map() {
		return queryableResourceEClass.getEOperations().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCommandResource() {
		return commandResourceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getCommandResource__Execute__Command() {
		return commandResourceEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getCommandResource__Execute__Command_Map_Map() {
		return commandResourceEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getCommandResource__Begin() {
		return commandResourceEClass.getEOperations().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getQueryShape() {
		return queryShapeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getDiagnostic() {
		return diagnosticEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getConverterService() {
		return converterServiceEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getQueryException() {
		return queryExceptionEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getCommandTransaction() {
		return commandTransactionEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getIOException() {
		return ioExceptionEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getEObjectStream() {
		return eObjectStreamEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getRowStream() {
		return rowStreamEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getObjectList() {
		return objectListEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getQueryCapabilities() {
		return queryCapabilitiesEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getParameterMap() {
		return parameterMapEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getScoreMap() {
		return scoreMapEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getHitStream() {
		return hitStreamEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getOptionsMap() {
		return optionsMapEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public QueryApiFactory getQueryApiFactory() {
		return (QueryApiFactory)getEFactoryInstance();
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
		autoCloseableEClass = createEClass(AUTO_CLOSEABLE);

		queryPlanEClass = createEClass(QUERY_PLAN);
		createEOperation(queryPlanEClass, QUERY_PLAN___SOURCE);
		createEOperation(queryPlanEClass, QUERY_PLAN___SHAPE);

		queryContextEClass = createEClass(QUERY_CONTEXT);
		createEOperation(queryContextEClass, QUERY_CONTEXT___ROOT_ECLASS);
		createEOperation(queryContextEClass, QUERY_CONTEXT___CONVERTER);
		createEOperation(queryContextEClass, QUERY_CONTEXT___PARAMETERS);
		createEOperation(queryContextEClass, QUERY_CONTEXT___OPTIONS);

		queryProcessorEClass = createEClass(QUERY_PROCESSOR);
		createEOperation(queryProcessorEClass, QUERY_PROCESSOR___BACKEND);
		createEOperation(queryProcessorEClass, QUERY_PROCESSOR___CAPABILITIES);
		createEOperation(queryProcessorEClass, QUERY_PROCESSOR___VALIDATE__QUERY_ECLASS);
		createEOperation(queryProcessorEClass, QUERY_PROCESSOR___TRANSLATE__QUERY_QUERYCONTEXT);

		queryResultRowEClass = createEClass(QUERY_RESULT_ROW);
		createEOperation(queryResultRowEClass, QUERY_RESULT_ROW___GET__STRING);
		createEOperation(queryResultRowEClass, QUERY_RESULT_ROW___GET__INT);
		createEOperation(queryResultRowEClass, QUERY_RESULT_ROW___VALUES);

		queryResultEClass = createEClass(QUERY_RESULT);
		createEOperation(queryResultEClass, QUERY_RESULT___SHAPE);
		createEOperation(queryResultEClass, QUERY_RESULT___OBJECTS);
		createEOperation(queryResultEClass, QUERY_RESULT___ROWS);
		createEOperation(queryResultEClass, QUERY_RESULT___COUNT);
		createEOperation(queryResultEClass, QUERY_RESULT___HITS);
		createEOperation(queryResultEClass, QUERY_RESULT___SCORES);
		createEOperation(queryResultEClass, QUERY_RESULT___CLOSE);

		hitEClass = createEClass(HIT);
		createEOperation(hitEClass, HIT___OBJECT);
		createEOperation(hitEClass, HIT___SCORE);

		queryableResourceEClass = createEClass(QUERYABLE_RESOURCE);
		createEOperation(queryableResourceEClass, QUERYABLE_RESOURCE___QUERY__QUERY);
		createEOperation(queryableResourceEClass, QUERYABLE_RESOURCE___QUERY__QUERY_MAP_MAP);
		createEOperation(queryableResourceEClass, QUERYABLE_RESOURCE___QUERY__STRING_MAP_MAP);

		commandResourceEClass = createEClass(COMMAND_RESOURCE);
		createEOperation(commandResourceEClass, COMMAND_RESOURCE___EXECUTE__COMMAND);
		createEOperation(commandResourceEClass, COMMAND_RESOURCE___EXECUTE__COMMAND_MAP_MAP);
		createEOperation(commandResourceEClass, COMMAND_RESOURCE___BEGIN);

		// Create enums
		queryShapeEEnum = createEEnum(QUERY_SHAPE);

		// Create data types
		diagnosticEDataType = createEDataType(DIAGNOSTIC);
		converterServiceEDataType = createEDataType(CONVERTER_SERVICE);
		queryExceptionEDataType = createEDataType(QUERY_EXCEPTION);
		commandTransactionEDataType = createEDataType(COMMAND_TRANSACTION);
		ioExceptionEDataType = createEDataType(IO_EXCEPTION);
		eObjectStreamEDataType = createEDataType(EOBJECT_STREAM);
		rowStreamEDataType = createEDataType(ROW_STREAM);
		objectListEDataType = createEDataType(OBJECT_LIST);
		queryCapabilitiesEDataType = createEDataType(QUERY_CAPABILITIES);
		parameterMapEDataType = createEDataType(PARAMETER_MAP);
		scoreMapEDataType = createEDataType(SCORE_MAP);
		hitStreamEDataType = createEDataType(HIT_STREAM);
		optionsMapEDataType = createEDataType(OPTIONS_MAP);
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
		QueryPackage theQueryPackage = (QueryPackage)EPackage.Registry.INSTANCE.getEPackage(QueryPackage.eNS_URI);
		CommandPackage theCommandPackage = (CommandPackage)EPackage.Registry.INSTANCE.getEPackage(CommandPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		queryResultEClass.getESuperTypes().add(this.getAutoCloseable());

		// Initialize classes, features, and operations; add parameters
		initEClass(autoCloseableEClass, AutoCloseable.class, "AutoCloseable", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);

		initEClass(queryPlanEClass, QueryPlan.class, "QueryPlan", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEOperation(getQueryPlan__Source(), theQueryPackage.getQuery(), "source", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getQueryPlan__Shape(), this.getQueryShape(), "shape", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(queryContextEClass, QueryContext.class, "QueryContext", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEOperation(getQueryContext__RootEClass(), ecorePackage.getEClass(), "rootEClass", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getQueryContext__Converter(), this.getConverterService(), "converter", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getQueryContext__Parameters(), this.getParameterMap(), "parameters", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getQueryContext__Options(), this.getOptionsMap(), "options", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(queryProcessorEClass, QueryProcessor.class, "QueryProcessor", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEOperation(getQueryProcessor__Backend(), ecorePackage.getEString(), "backend", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getQueryProcessor__Capabilities(), this.getQueryCapabilities(), "capabilities", 0, 1, IS_UNIQUE, IS_ORDERED);

		EOperation op = initEOperation(getQueryProcessor__Validate__Query_EClass(), this.getDiagnostic(), "validate", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryPackage.getQuery(), "query", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEClass(), "rootEClass", 1, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getQueryProcessor__Translate__Query_QueryContext(), this.getQueryPlan(), "translate", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryPackage.getQuery(), "query", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getQueryContext(), "context", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, this.getQueryException());

		initEClass(queryResultRowEClass, QueryResultRow.class, "QueryResultRow", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		op = initEOperation(getQueryResultRow__Get__String(), ecorePackage.getEJavaObject(), "get", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "alias", 1, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getQueryResultRow__Get__int(), ecorePackage.getEJavaObject(), "get", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEInt(), "index", 1, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getQueryResultRow__Values(), this.getObjectList(), "values", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(queryResultEClass, QueryResult.class, "QueryResult", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEOperation(getQueryResult__Shape(), this.getQueryShape(), "shape", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getQueryResult__Objects(), this.getEObjectStream(), "objects", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getQueryResult__Rows(), this.getRowStream(), "rows", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getQueryResult__Count(), ecorePackage.getELong(), "count", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getQueryResult__Hits(), this.getHitStream(), "hits", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getQueryResult__Scores(), this.getScoreMap(), "scores", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getQueryResult__Close(), null, "close", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(hitEClass, Hit.class, "Hit", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEOperation(getHit__Object(), ecorePackage.getEObject(), "object", 1, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getHit__Score(), ecorePackage.getEDouble(), "score", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(queryableResourceEClass, QueryableResource.class, "QueryableResource", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		op = initEOperation(getQueryableResource__Query__Query(), this.getQueryResult(), "query", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryPackage.getQuery(), "query", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, this.getIOException());

		op = initEOperation(getQueryableResource__Query__Query_Map_Map(), this.getQueryResult(), "query", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theQueryPackage.getQuery(), "query", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getParameterMap(), "parameters", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getOptionsMap(), "options", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, this.getIOException());

		op = initEOperation(getQueryableResource__Query__String_Map_Map(), this.getQueryResult(), "query", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "name", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getParameterMap(), "parameters", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getOptionsMap(), "options", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, this.getIOException());

		initEClass(commandResourceEClass, CommandResource.class, "CommandResource", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		op = initEOperation(getCommandResource__Execute__Command(), ecorePackage.getELong(), "execute", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theCommandPackage.getCommand(), "command", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, this.getIOException());

		op = initEOperation(getCommandResource__Execute__Command_Map_Map(), ecorePackage.getELong(), "execute", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theCommandPackage.getCommand(), "command", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getParameterMap(), "parameters", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getOptionsMap(), "options", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, this.getIOException());

		op = initEOperation(getCommandResource__Begin(), this.getCommandTransaction(), "begin", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, this.getIOException());

		// Initialize enums and add enum literals
		initEEnum(queryShapeEEnum, QueryShape.class, "QueryShape");
		addEEnumLiteral(queryShapeEEnum, QueryShape.OBJECTS);
		addEEnumLiteral(queryShapeEEnum, QueryShape.PROJECTION);
		addEEnumLiteral(queryShapeEEnum, QueryShape.AGGREGATION);
		addEEnumLiteral(queryShapeEEnum, QueryShape.COUNT);

		// Initialize data types
		initEDataType(diagnosticEDataType, Diagnostic.class, "Diagnostic", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(converterServiceEDataType, ConverterService.class, "ConverterService", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(queryExceptionEDataType, QueryException.class, "QueryException", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(commandTransactionEDataType, CommandTransaction.class, "CommandTransaction", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(ioExceptionEDataType, IOException.class, "IOException", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(eObjectStreamEDataType, Stream.class, "EObjectStream", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS, "java.util.stream.Stream<org.eclipse.emf.ecore.EObject>");
		initEDataType(rowStreamEDataType, Stream.class, "RowStream", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS, "java.util.stream.Stream<org.eclipse.fennec.persistence.query.api.QueryResultRow>");
		initEDataType(objectListEDataType, List.class, "ObjectList", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS, "java.util.List<java.lang.Object>");
		initEDataType(queryCapabilitiesEDataType, QueryCapabilities.class, "QueryCapabilities", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(parameterMapEDataType, Map.class, "ParameterMap", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS, "java.util.Map<java.lang.String, java.lang.Object>");
		initEDataType(scoreMapEDataType, Map.class, "ScoreMap", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS, "java.util.Map<java.lang.String, java.lang.Double>");
		initEDataType(hitStreamEDataType, Stream.class, "HitStream", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS, "java.util.stream.Stream<org.eclipse.fennec.persistence.query.api.Hit>");
		initEDataType(optionsMapEDataType, Map.class, "OptionsMap", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS, "java.util.Map<?, ?>");

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// Version
		createVersionAnnotations();
	}

	/**
	 * Initializes the annotations for <b>Version</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createVersionAnnotations() {
		String source = "Version";
		addAnnotation
		  (this,
		   source,
		   new String[] {
			   "value", "1.0"
		   });
	}

} //QueryApiPackageImpl
