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
package org.eclipse.fennec.model.query;


import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
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
 * The v2 query envelope around the Fennec Expression Model: root type, predicate, ordering, projection, aggregation pipeline, fetch hints, paging and declared parameters. Read-only by design — CUD lives in the command model (concept.md §14). See docs/unified-persistence/query-ir-redesign.md §4.
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.model.query.QueryFactory
 * @model kind="package"
 *        annotation="Version value='2.0'"
 * @generated
 */
@ProviderType
@EPackage(uri = QueryPackage.eNS_URI, fingerprint = "fp1:a21e4bafdd607b9e3e4b6a45989f9b188b2a8a0abebdf8b92b9f83b2a28e0303", genModel = "/model/query.genmodel", genModelSourceLocations = {"model/query.genmodel","org.eclipse.fennec.query.model/model/query.genmodel"}, ecore = "/model/query.ecore", ecoreSourceLocations = "/model/query.ecore")
public interface QueryPackage extends org.eclipse.emf.ecore.EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "query";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://eclipse.org/fennec/query/2.0.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "query";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	QueryPackage eINSTANCE = org.eclipse.fennec.model.query.impl.QueryPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.query.impl.QueryImpl <em>Query</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.query.impl.QueryImpl
	 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getQuery()
	 * @generated
	 */
	int QUERY = 0;

	/**
	 * The feature id for the '<em><b>From</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY__FROM = 0;

	/**
	 * The feature id for the '<em><b>Predicate</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY__PREDICATE = 1;

	/**
	 * The feature id for the '<em><b>Order By</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY__ORDER_BY = 2;

	/**
	 * The feature id for the '<em><b>Select</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY__SELECT = 3;

	/**
	 * The feature id for the '<em><b>Apply</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY__APPLY = 4;

	/**
	 * The feature id for the '<em><b>Expand</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY__EXPAND = 5;

	/**
	 * The feature id for the '<em><b>Top</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY__TOP = 6;

	/**
	 * The feature id for the '<em><b>Skip</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY__SKIP = 7;

	/**
	 * The feature id for the '<em><b>Distinct</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY__DISTINCT = 8;

	/**
	 * The feature id for the '<em><b>Count Only</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY__COUNT_ONLY = 9;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY__PARAMETERS = 10;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY__NAME = 11;

	/**
	 * The feature id for the '<em><b>Save Query</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY__SAVE_QUERY = 12;

	/**
	 * The number of structural features of the '<em>Query</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_FEATURE_COUNT = 13;

	/**
	 * The number of operations of the '<em>Query</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.query.impl.OrderByImpl <em>Order By</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.query.impl.OrderByImpl
	 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getOrderBy()
	 * @generated
	 */
	int ORDER_BY = 1;

	/**
	 * The feature id for the '<em><b>Path</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ORDER_BY__PATH = 0;

	/**
	 * The feature id for the '<em><b>Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ORDER_BY__KEY = 1;

	/**
	 * The feature id for the '<em><b>Direction</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ORDER_BY__DIRECTION = 2;

	/**
	 * The number of structural features of the '<em>Order By</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ORDER_BY_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Order By</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ORDER_BY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.query.impl.SelectionImpl <em>Selection</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.query.impl.SelectionImpl
	 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getSelection()
	 * @generated
	 */
	int SELECTION = 2;

	/**
	 * The feature id for the '<em><b>Path</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SELECTION__PATH = 0;

	/**
	 * The feature id for the '<em><b>Alias</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SELECTION__ALIAS = 1;

	/**
	 * The number of structural features of the '<em>Selection</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SELECTION_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Selection</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SELECTION_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.query.impl.ParameterDeclImpl <em>Parameter Decl</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.query.impl.ParameterDeclImpl
	 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getParameterDecl()
	 * @generated
	 */
	int PARAMETER_DECL = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_DECL__NAME = 0;

	/**
	 * The feature id for the '<em><b>Type Hint</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_DECL__TYPE_HINT = 1;

	/**
	 * The number of structural features of the '<em>Parameter Decl</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_DECL_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Parameter Decl</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_DECL_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.query.impl.PipelineImpl <em>Pipeline</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.query.impl.PipelineImpl
	 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getPipeline()
	 * @generated
	 */
	int PIPELINE = 4;

	/**
	 * The feature id for the '<em><b>Stages</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PIPELINE__STAGES = 0;

	/**
	 * The number of structural features of the '<em>Pipeline</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PIPELINE_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Pipeline</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PIPELINE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.query.impl.StageImpl <em>Stage</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.query.impl.StageImpl
	 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getStage()
	 * @generated
	 */
	int STAGE = 5;

	/**
	 * The number of structural features of the '<em>Stage</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STAGE_FEATURE_COUNT = 0;

	/**
	 * The number of operations of the '<em>Stage</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STAGE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.query.impl.FilterStageImpl <em>Filter Stage</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.query.impl.FilterStageImpl
	 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getFilterStage()
	 * @generated
	 */
	int FILTER_STAGE = 6;

	/**
	 * The feature id for the '<em><b>Predicate</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILTER_STAGE__PREDICATE = STAGE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Filter Stage</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILTER_STAGE_FEATURE_COUNT = STAGE_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Filter Stage</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILTER_STAGE_OPERATION_COUNT = STAGE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.query.impl.GroupByStageImpl <em>Group By Stage</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.query.impl.GroupByStageImpl
	 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getGroupByStage()
	 * @generated
	 */
	int GROUP_BY_STAGE = 7;

	/**
	 * The feature id for the '<em><b>Paths</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROUP_BY_STAGE__PATHS = STAGE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Aggregates</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROUP_BY_STAGE__AGGREGATES = STAGE_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Group By Stage</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROUP_BY_STAGE_FEATURE_COUNT = STAGE_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Group By Stage</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROUP_BY_STAGE_OPERATION_COUNT = STAGE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.query.impl.AggregateImpl <em>Aggregate</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.query.impl.AggregateImpl
	 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getAggregate()
	 * @generated
	 */
	int AGGREGATE = 8;

	/**
	 * The feature id for the '<em><b>Path</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AGGREGATE__PATH = 0;

	/**
	 * The feature id for the '<em><b>Method</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AGGREGATE__METHOD = 1;

	/**
	 * The feature id for the '<em><b>Alias</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AGGREGATE__ALIAS = 2;

	/**
	 * The number of structural features of the '<em>Aggregate</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AGGREGATE_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Aggregate</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AGGREGATE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.query.impl.TopStageImpl <em>Top Stage</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.query.impl.TopStageImpl
	 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getTopStage()
	 * @generated
	 */
	int TOP_STAGE = 9;

	/**
	 * The feature id for the '<em><b>Count</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TOP_STAGE__COUNT = STAGE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Top Stage</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TOP_STAGE_FEATURE_COUNT = STAGE_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Top Stage</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TOP_STAGE_OPERATION_COUNT = STAGE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.query.impl.SkipStageImpl <em>Skip Stage</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.query.impl.SkipStageImpl
	 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getSkipStage()
	 * @generated
	 */
	int SKIP_STAGE = 10;

	/**
	 * The feature id for the '<em><b>Count</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SKIP_STAGE__COUNT = STAGE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Skip Stage</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SKIP_STAGE_FEATURE_COUNT = STAGE_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Skip Stage</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SKIP_STAGE_OPERATION_COUNT = STAGE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.query.impl.ComputeStageImpl <em>Compute Stage</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.query.impl.ComputeStageImpl
	 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getComputeStage()
	 * @generated
	 */
	int COMPUTE_STAGE = 11;

	/**
	 * The feature id for the '<em><b>Computations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPUTE_STAGE__COMPUTATIONS = STAGE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Compute Stage</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPUTE_STAGE_FEATURE_COUNT = STAGE_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Compute Stage</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPUTE_STAGE_OPERATION_COUNT = STAGE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.query.impl.ComputationImpl <em>Computation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.query.impl.ComputationImpl
	 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getComputation()
	 * @generated
	 */
	int COMPUTATION = 12;

	/**
	 * The feature id for the '<em><b>Expression</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPUTATION__EXPRESSION = 0;

	/**
	 * The feature id for the '<em><b>Alias</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPUTATION__ALIAS = 1;

	/**
	 * The number of structural features of the '<em>Computation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPUTATION_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Computation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPUTATION_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.query.SortDirection <em>Sort Direction</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.query.SortDirection
	 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getSortDirection()
	 * @generated
	 */
	int SORT_DIRECTION = 13;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.query.AggregateMethod <em>Aggregate Method</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.query.AggregateMethod
	 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getAggregateMethod()
	 * @generated
	 */
	int AGGREGATE_METHOD = 14;


	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.query.Query <em>Query</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Query</em>'.
	 * @see org.eclipse.fennec.model.query.Query
	 * @generated
	 */
	EClass getQuery();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.query.Query#getFrom <em>From</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>From</em>'.
	 * @see org.eclipse.fennec.model.query.Query#getFrom()
	 * @see #getQuery()
	 * @generated
	 */
	EReference getQuery_From();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.query.Query#getPredicate <em>Predicate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Predicate</em>'.
	 * @see org.eclipse.fennec.model.query.Query#getPredicate()
	 * @see #getQuery()
	 * @generated
	 */
	EReference getQuery_Predicate();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.query.Query#getOrderBy <em>Order By</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Order By</em>'.
	 * @see org.eclipse.fennec.model.query.Query#getOrderBy()
	 * @see #getQuery()
	 * @generated
	 */
	EReference getQuery_OrderBy();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.query.Query#getSelect <em>Select</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Select</em>'.
	 * @see org.eclipse.fennec.model.query.Query#getSelect()
	 * @see #getQuery()
	 * @generated
	 */
	EReference getQuery_Select();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.query.Query#getApply <em>Apply</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Apply</em>'.
	 * @see org.eclipse.fennec.model.query.Query#getApply()
	 * @see #getQuery()
	 * @generated
	 */
	EReference getQuery_Apply();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.query.Query#getExpand <em>Expand</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Expand</em>'.
	 * @see org.eclipse.fennec.model.query.Query#getExpand()
	 * @see #getQuery()
	 * @generated
	 */
	EReference getQuery_Expand();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.query.Query#getTop <em>Top</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Top</em>'.
	 * @see org.eclipse.fennec.model.query.Query#getTop()
	 * @see #getQuery()
	 * @generated
	 */
	EAttribute getQuery_Top();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.query.Query#getSkip <em>Skip</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Skip</em>'.
	 * @see org.eclipse.fennec.model.query.Query#getSkip()
	 * @see #getQuery()
	 * @generated
	 */
	EAttribute getQuery_Skip();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.query.Query#isDistinct <em>Distinct</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Distinct</em>'.
	 * @see org.eclipse.fennec.model.query.Query#isDistinct()
	 * @see #getQuery()
	 * @generated
	 */
	EAttribute getQuery_Distinct();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.query.Query#isCountOnly <em>Count Only</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Count Only</em>'.
	 * @see org.eclipse.fennec.model.query.Query#isCountOnly()
	 * @see #getQuery()
	 * @generated
	 */
	EAttribute getQuery_CountOnly();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.query.Query#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameters</em>'.
	 * @see org.eclipse.fennec.model.query.Query#getParameters()
	 * @see #getQuery()
	 * @generated
	 */
	EReference getQuery_Parameters();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.query.Query#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.model.query.Query#getName()
	 * @see #getQuery()
	 * @generated
	 */
	EAttribute getQuery_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.query.Query#isSaveQuery <em>Save Query</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Save Query</em>'.
	 * @see org.eclipse.fennec.model.query.Query#isSaveQuery()
	 * @see #getQuery()
	 * @generated
	 */
	EAttribute getQuery_SaveQuery();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.query.OrderBy <em>Order By</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Order By</em>'.
	 * @see org.eclipse.fennec.model.query.OrderBy
	 * @generated
	 */
	EClass getOrderBy();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.query.OrderBy#getPath <em>Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Path</em>'.
	 * @see org.eclipse.fennec.model.query.OrderBy#getPath()
	 * @see #getOrderBy()
	 * @generated
	 */
	EReference getOrderBy_Path();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.query.OrderBy#getKey <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Key</em>'.
	 * @see org.eclipse.fennec.model.query.OrderBy#getKey()
	 * @see #getOrderBy()
	 * @generated
	 */
	EReference getOrderBy_Key();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.query.OrderBy#getDirection <em>Direction</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Direction</em>'.
	 * @see org.eclipse.fennec.model.query.OrderBy#getDirection()
	 * @see #getOrderBy()
	 * @generated
	 */
	EAttribute getOrderBy_Direction();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.query.Selection <em>Selection</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Selection</em>'.
	 * @see org.eclipse.fennec.model.query.Selection
	 * @generated
	 */
	EClass getSelection();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.query.Selection#getPath <em>Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Path</em>'.
	 * @see org.eclipse.fennec.model.query.Selection#getPath()
	 * @see #getSelection()
	 * @generated
	 */
	EReference getSelection_Path();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.query.Selection#getAlias <em>Alias</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Alias</em>'.
	 * @see org.eclipse.fennec.model.query.Selection#getAlias()
	 * @see #getSelection()
	 * @generated
	 */
	EAttribute getSelection_Alias();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.query.ParameterDecl <em>Parameter Decl</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameter Decl</em>'.
	 * @see org.eclipse.fennec.model.query.ParameterDecl
	 * @generated
	 */
	EClass getParameterDecl();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.query.ParameterDecl#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.model.query.ParameterDecl#getName()
	 * @see #getParameterDecl()
	 * @generated
	 */
	EAttribute getParameterDecl_Name();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.query.ParameterDecl#getTypeHint <em>Type Hint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Type Hint</em>'.
	 * @see org.eclipse.fennec.model.query.ParameterDecl#getTypeHint()
	 * @see #getParameterDecl()
	 * @generated
	 */
	EReference getParameterDecl_TypeHint();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.query.Pipeline <em>Pipeline</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Pipeline</em>'.
	 * @see org.eclipse.fennec.model.query.Pipeline
	 * @generated
	 */
	EClass getPipeline();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.query.Pipeline#getStages <em>Stages</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Stages</em>'.
	 * @see org.eclipse.fennec.model.query.Pipeline#getStages()
	 * @see #getPipeline()
	 * @generated
	 */
	EReference getPipeline_Stages();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.query.Stage <em>Stage</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Stage</em>'.
	 * @see org.eclipse.fennec.model.query.Stage
	 * @generated
	 */
	EClass getStage();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.query.FilterStage <em>Filter Stage</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Filter Stage</em>'.
	 * @see org.eclipse.fennec.model.query.FilterStage
	 * @generated
	 */
	EClass getFilterStage();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.query.FilterStage#getPredicate <em>Predicate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Predicate</em>'.
	 * @see org.eclipse.fennec.model.query.FilterStage#getPredicate()
	 * @see #getFilterStage()
	 * @generated
	 */
	EReference getFilterStage_Predicate();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.query.GroupByStage <em>Group By Stage</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Group By Stage</em>'.
	 * @see org.eclipse.fennec.model.query.GroupByStage
	 * @generated
	 */
	EClass getGroupByStage();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.query.GroupByStage#getPaths <em>Paths</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Paths</em>'.
	 * @see org.eclipse.fennec.model.query.GroupByStage#getPaths()
	 * @see #getGroupByStage()
	 * @generated
	 */
	EReference getGroupByStage_Paths();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.query.GroupByStage#getAggregates <em>Aggregates</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Aggregates</em>'.
	 * @see org.eclipse.fennec.model.query.GroupByStage#getAggregates()
	 * @see #getGroupByStage()
	 * @generated
	 */
	EReference getGroupByStage_Aggregates();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.query.Aggregate <em>Aggregate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Aggregate</em>'.
	 * @see org.eclipse.fennec.model.query.Aggregate
	 * @generated
	 */
	EClass getAggregate();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.query.Aggregate#getPath <em>Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Path</em>'.
	 * @see org.eclipse.fennec.model.query.Aggregate#getPath()
	 * @see #getAggregate()
	 * @generated
	 */
	EReference getAggregate_Path();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.query.Aggregate#getMethod <em>Method</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Method</em>'.
	 * @see org.eclipse.fennec.model.query.Aggregate#getMethod()
	 * @see #getAggregate()
	 * @generated
	 */
	EAttribute getAggregate_Method();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.query.Aggregate#getAlias <em>Alias</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Alias</em>'.
	 * @see org.eclipse.fennec.model.query.Aggregate#getAlias()
	 * @see #getAggregate()
	 * @generated
	 */
	EAttribute getAggregate_Alias();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.query.TopStage <em>Top Stage</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Top Stage</em>'.
	 * @see org.eclipse.fennec.model.query.TopStage
	 * @generated
	 */
	EClass getTopStage();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.query.TopStage#getCount <em>Count</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Count</em>'.
	 * @see org.eclipse.fennec.model.query.TopStage#getCount()
	 * @see #getTopStage()
	 * @generated
	 */
	EAttribute getTopStage_Count();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.query.SkipStage <em>Skip Stage</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Skip Stage</em>'.
	 * @see org.eclipse.fennec.model.query.SkipStage
	 * @generated
	 */
	EClass getSkipStage();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.query.SkipStage#getCount <em>Count</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Count</em>'.
	 * @see org.eclipse.fennec.model.query.SkipStage#getCount()
	 * @see #getSkipStage()
	 * @generated
	 */
	EAttribute getSkipStage_Count();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.query.ComputeStage <em>Compute Stage</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Compute Stage</em>'.
	 * @see org.eclipse.fennec.model.query.ComputeStage
	 * @generated
	 */
	EClass getComputeStage();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.query.ComputeStage#getComputations <em>Computations</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Computations</em>'.
	 * @see org.eclipse.fennec.model.query.ComputeStage#getComputations()
	 * @see #getComputeStage()
	 * @generated
	 */
	EReference getComputeStage_Computations();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.query.Computation <em>Computation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Computation</em>'.
	 * @see org.eclipse.fennec.model.query.Computation
	 * @generated
	 */
	EClass getComputation();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.query.Computation#getExpression <em>Expression</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Expression</em>'.
	 * @see org.eclipse.fennec.model.query.Computation#getExpression()
	 * @see #getComputation()
	 * @generated
	 */
	EReference getComputation_Expression();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.query.Computation#getAlias <em>Alias</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Alias</em>'.
	 * @see org.eclipse.fennec.model.query.Computation#getAlias()
	 * @see #getComputation()
	 * @generated
	 */
	EAttribute getComputation_Alias();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.query.SortDirection <em>Sort Direction</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Sort Direction</em>'.
	 * @see org.eclipse.fennec.model.query.SortDirection
	 * @generated
	 */
	EEnum getSortDirection();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.query.AggregateMethod <em>Aggregate Method</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Aggregate Method</em>'.
	 * @see org.eclipse.fennec.model.query.AggregateMethod
	 * @generated
	 */
	EEnum getAggregateMethod();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	QueryFactory getQueryFactory();

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
		 * The meta object literal for the '{@link org.eclipse.fennec.model.query.impl.QueryImpl <em>Query</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.query.impl.QueryImpl
		 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getQuery()
		 * @generated
		 */
		EClass QUERY = eINSTANCE.getQuery();

		/**
		 * The meta object literal for the '<em><b>From</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference QUERY__FROM = eINSTANCE.getQuery_From();

		/**
		 * The meta object literal for the '<em><b>Predicate</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference QUERY__PREDICATE = eINSTANCE.getQuery_Predicate();

		/**
		 * The meta object literal for the '<em><b>Order By</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference QUERY__ORDER_BY = eINSTANCE.getQuery_OrderBy();

		/**
		 * The meta object literal for the '<em><b>Select</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference QUERY__SELECT = eINSTANCE.getQuery_Select();

		/**
		 * The meta object literal for the '<em><b>Apply</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference QUERY__APPLY = eINSTANCE.getQuery_Apply();

		/**
		 * The meta object literal for the '<em><b>Expand</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference QUERY__EXPAND = eINSTANCE.getQuery_Expand();

		/**
		 * The meta object literal for the '<em><b>Top</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute QUERY__TOP = eINSTANCE.getQuery_Top();

		/**
		 * The meta object literal for the '<em><b>Skip</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute QUERY__SKIP = eINSTANCE.getQuery_Skip();

		/**
		 * The meta object literal for the '<em><b>Distinct</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute QUERY__DISTINCT = eINSTANCE.getQuery_Distinct();

		/**
		 * The meta object literal for the '<em><b>Count Only</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute QUERY__COUNT_ONLY = eINSTANCE.getQuery_CountOnly();

		/**
		 * The meta object literal for the '<em><b>Parameters</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference QUERY__PARAMETERS = eINSTANCE.getQuery_Parameters();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute QUERY__NAME = eINSTANCE.getQuery_Name();

		/**
		 * The meta object literal for the '<em><b>Save Query</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute QUERY__SAVE_QUERY = eINSTANCE.getQuery_SaveQuery();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.query.impl.OrderByImpl <em>Order By</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.query.impl.OrderByImpl
		 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getOrderBy()
		 * @generated
		 */
		EClass ORDER_BY = eINSTANCE.getOrderBy();

		/**
		 * The meta object literal for the '<em><b>Path</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ORDER_BY__PATH = eINSTANCE.getOrderBy_Path();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ORDER_BY__KEY = eINSTANCE.getOrderBy_Key();

		/**
		 * The meta object literal for the '<em><b>Direction</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ORDER_BY__DIRECTION = eINSTANCE.getOrderBy_Direction();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.query.impl.SelectionImpl <em>Selection</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.query.impl.SelectionImpl
		 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getSelection()
		 * @generated
		 */
		EClass SELECTION = eINSTANCE.getSelection();

		/**
		 * The meta object literal for the '<em><b>Path</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SELECTION__PATH = eINSTANCE.getSelection_Path();

		/**
		 * The meta object literal for the '<em><b>Alias</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SELECTION__ALIAS = eINSTANCE.getSelection_Alias();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.query.impl.ParameterDeclImpl <em>Parameter Decl</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.query.impl.ParameterDeclImpl
		 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getParameterDecl()
		 * @generated
		 */
		EClass PARAMETER_DECL = eINSTANCE.getParameterDecl();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER_DECL__NAME = eINSTANCE.getParameterDecl_Name();

		/**
		 * The meta object literal for the '<em><b>Type Hint</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PARAMETER_DECL__TYPE_HINT = eINSTANCE.getParameterDecl_TypeHint();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.query.impl.PipelineImpl <em>Pipeline</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.query.impl.PipelineImpl
		 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getPipeline()
		 * @generated
		 */
		EClass PIPELINE = eINSTANCE.getPipeline();

		/**
		 * The meta object literal for the '<em><b>Stages</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PIPELINE__STAGES = eINSTANCE.getPipeline_Stages();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.query.impl.StageImpl <em>Stage</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.query.impl.StageImpl
		 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getStage()
		 * @generated
		 */
		EClass STAGE = eINSTANCE.getStage();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.query.impl.FilterStageImpl <em>Filter Stage</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.query.impl.FilterStageImpl
		 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getFilterStage()
		 * @generated
		 */
		EClass FILTER_STAGE = eINSTANCE.getFilterStage();

		/**
		 * The meta object literal for the '<em><b>Predicate</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FILTER_STAGE__PREDICATE = eINSTANCE.getFilterStage_Predicate();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.query.impl.GroupByStageImpl <em>Group By Stage</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.query.impl.GroupByStageImpl
		 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getGroupByStage()
		 * @generated
		 */
		EClass GROUP_BY_STAGE = eINSTANCE.getGroupByStage();

		/**
		 * The meta object literal for the '<em><b>Paths</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GROUP_BY_STAGE__PATHS = eINSTANCE.getGroupByStage_Paths();

		/**
		 * The meta object literal for the '<em><b>Aggregates</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GROUP_BY_STAGE__AGGREGATES = eINSTANCE.getGroupByStage_Aggregates();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.query.impl.AggregateImpl <em>Aggregate</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.query.impl.AggregateImpl
		 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getAggregate()
		 * @generated
		 */
		EClass AGGREGATE = eINSTANCE.getAggregate();

		/**
		 * The meta object literal for the '<em><b>Path</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference AGGREGATE__PATH = eINSTANCE.getAggregate_Path();

		/**
		 * The meta object literal for the '<em><b>Method</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute AGGREGATE__METHOD = eINSTANCE.getAggregate_Method();

		/**
		 * The meta object literal for the '<em><b>Alias</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute AGGREGATE__ALIAS = eINSTANCE.getAggregate_Alias();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.query.impl.TopStageImpl <em>Top Stage</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.query.impl.TopStageImpl
		 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getTopStage()
		 * @generated
		 */
		EClass TOP_STAGE = eINSTANCE.getTopStage();

		/**
		 * The meta object literal for the '<em><b>Count</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TOP_STAGE__COUNT = eINSTANCE.getTopStage_Count();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.query.impl.SkipStageImpl <em>Skip Stage</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.query.impl.SkipStageImpl
		 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getSkipStage()
		 * @generated
		 */
		EClass SKIP_STAGE = eINSTANCE.getSkipStage();

		/**
		 * The meta object literal for the '<em><b>Count</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SKIP_STAGE__COUNT = eINSTANCE.getSkipStage_Count();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.query.impl.ComputeStageImpl <em>Compute Stage</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.query.impl.ComputeStageImpl
		 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getComputeStage()
		 * @generated
		 */
		EClass COMPUTE_STAGE = eINSTANCE.getComputeStage();

		/**
		 * The meta object literal for the '<em><b>Computations</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMPUTE_STAGE__COMPUTATIONS = eINSTANCE.getComputeStage_Computations();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.query.impl.ComputationImpl <em>Computation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.query.impl.ComputationImpl
		 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getComputation()
		 * @generated
		 */
		EClass COMPUTATION = eINSTANCE.getComputation();

		/**
		 * The meta object literal for the '<em><b>Expression</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMPUTATION__EXPRESSION = eINSTANCE.getComputation_Expression();

		/**
		 * The meta object literal for the '<em><b>Alias</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COMPUTATION__ALIAS = eINSTANCE.getComputation_Alias();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.query.SortDirection <em>Sort Direction</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.query.SortDirection
		 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getSortDirection()
		 * @generated
		 */
		EEnum SORT_DIRECTION = eINSTANCE.getSortDirection();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.query.AggregateMethod <em>Aggregate Method</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.query.AggregateMethod
		 * @see org.eclipse.fennec.model.query.impl.QueryPackageImpl#getAggregateMethod()
		 * @generated
		 */
		EEnum AGGREGATE_METHOD = eINSTANCE.getAggregateMethod();

	}

} //QueryPackage
