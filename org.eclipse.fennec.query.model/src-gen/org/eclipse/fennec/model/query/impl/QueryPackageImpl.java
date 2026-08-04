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
package org.eclipse.fennec.model.query.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.fennec.model.expression.ExpressionPackage;

import org.eclipse.fennec.model.query.Aggregate;
import org.eclipse.fennec.model.query.AggregateMethod;
import org.eclipse.fennec.model.query.Computation;
import org.eclipse.fennec.model.query.ComputeStage;
import org.eclipse.fennec.model.query.FilterStage;
import org.eclipse.fennec.model.query.GroupByStage;
import org.eclipse.fennec.model.query.GroupKey;
import org.eclipse.fennec.model.query.OrderBy;
import org.eclipse.fennec.model.query.ParameterDecl;
import org.eclipse.fennec.model.query.Pipeline;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.QueryFactory;
import org.eclipse.fennec.model.query.QueryPackage;
import org.eclipse.fennec.model.query.Selection;
import org.eclipse.fennec.model.query.SkipStage;
import org.eclipse.fennec.model.query.SortDirection;
import org.eclipse.fennec.model.query.Stage;
import org.eclipse.fennec.model.query.TopStage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class QueryPackageImpl extends EPackageImpl implements QueryPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass queryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass orderByEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass selectionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass parameterDeclEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass pipelineEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass stageEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass filterStageEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass groupByStageEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass groupKeyEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass aggregateEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass topStageEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass skipStageEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass computeStageEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass computationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum sortDirectionEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum aggregateMethodEEnum = null;

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
	 * @see org.eclipse.fennec.model.query.QueryPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private QueryPackageImpl() {
		super(eNS_URI, QueryFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link QueryPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static QueryPackage init() {
		if (isInited) return (QueryPackage)EPackage.Registry.INSTANCE.getEPackage(QueryPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredQueryPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		QueryPackageImpl theQueryPackage = registeredQueryPackage instanceof QueryPackageImpl ? (QueryPackageImpl)registeredQueryPackage : new QueryPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		ExpressionPackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theQueryPackage.createPackageContents();

		// Initialize created meta-data
		theQueryPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theQueryPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(QueryPackage.eNS_URI, theQueryPackage);
		return theQueryPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getQuery() {
		return queryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getQuery_From() {
		return (EReference)queryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getQuery_Predicate() {
		return (EReference)queryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getQuery_OrderBy() {
		return (EReference)queryEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getQuery_Select() {
		return (EReference)queryEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getQuery_Apply() {
		return (EReference)queryEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getQuery_Expand() {
		return (EReference)queryEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getQuery_Top() {
		return (EAttribute)queryEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getQuery_Skip() {
		return (EAttribute)queryEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getQuery_Distinct() {
		return (EAttribute)queryEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getQuery_CountOnly() {
		return (EAttribute)queryEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getQuery_Parameters() {
		return (EReference)queryEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getQuery_Name() {
		return (EAttribute)queryEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getQuery_SaveQuery() {
		return (EAttribute)queryEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getOrderBy() {
		return orderByEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOrderBy_Path() {
		return (EReference)orderByEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOrderBy_Key() {
		return (EReference)orderByEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOrderBy_Direction() {
		return (EAttribute)orderByEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSelection() {
		return selectionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSelection_Path() {
		return (EReference)selectionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSelection_Alias() {
		return (EAttribute)selectionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getParameterDecl() {
		return parameterDeclEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getParameterDecl_Name() {
		return (EAttribute)parameterDeclEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getParameterDecl_TypeHint() {
		return (EReference)parameterDeclEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPipeline() {
		return pipelineEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPipeline_Stages() {
		return (EReference)pipelineEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getStage() {
		return stageEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getFilterStage() {
		return filterStageEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getFilterStage_Predicate() {
		return (EReference)filterStageEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getGroupByStage() {
		return groupByStageEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getGroupByStage_Paths() {
		return (EReference)groupByStageEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getGroupByStage_Keys() {
		return (EReference)groupByStageEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getGroupByStage_Aggregates() {
		return (EReference)groupByStageEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getGroupKey() {
		return groupKeyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getGroupKey_Expression() {
		return (EReference)groupKeyEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getGroupKey_Alias() {
		return (EAttribute)groupKeyEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAggregate() {
		return aggregateEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAggregate_Path() {
		return (EReference)aggregateEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAggregate_Source() {
		return (EReference)aggregateEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAggregate_Method() {
		return (EAttribute)aggregateEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAggregate_Alias() {
		return (EAttribute)aggregateEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTopStage() {
		return topStageEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTopStage_Count() {
		return (EAttribute)topStageEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSkipStage() {
		return skipStageEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSkipStage_Count() {
		return (EAttribute)skipStageEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getComputeStage() {
		return computeStageEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getComputeStage_Computations() {
		return (EReference)computeStageEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getComputation() {
		return computationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getComputation_Expression() {
		return (EReference)computationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getComputation_Alias() {
		return (EAttribute)computationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getSortDirection() {
		return sortDirectionEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getAggregateMethod() {
		return aggregateMethodEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public QueryFactory getQueryFactory() {
		return (QueryFactory)getEFactoryInstance();
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
		queryEClass = createEClass(QUERY);
		createEReference(queryEClass, QUERY__FROM);
		createEReference(queryEClass, QUERY__PREDICATE);
		createEReference(queryEClass, QUERY__ORDER_BY);
		createEReference(queryEClass, QUERY__SELECT);
		createEReference(queryEClass, QUERY__APPLY);
		createEReference(queryEClass, QUERY__EXPAND);
		createEAttribute(queryEClass, QUERY__TOP);
		createEAttribute(queryEClass, QUERY__SKIP);
		createEAttribute(queryEClass, QUERY__DISTINCT);
		createEAttribute(queryEClass, QUERY__COUNT_ONLY);
		createEReference(queryEClass, QUERY__PARAMETERS);
		createEAttribute(queryEClass, QUERY__NAME);
		createEAttribute(queryEClass, QUERY__SAVE_QUERY);

		orderByEClass = createEClass(ORDER_BY);
		createEReference(orderByEClass, ORDER_BY__PATH);
		createEReference(orderByEClass, ORDER_BY__KEY);
		createEAttribute(orderByEClass, ORDER_BY__DIRECTION);

		selectionEClass = createEClass(SELECTION);
		createEReference(selectionEClass, SELECTION__PATH);
		createEAttribute(selectionEClass, SELECTION__ALIAS);

		parameterDeclEClass = createEClass(PARAMETER_DECL);
		createEAttribute(parameterDeclEClass, PARAMETER_DECL__NAME);
		createEReference(parameterDeclEClass, PARAMETER_DECL__TYPE_HINT);

		pipelineEClass = createEClass(PIPELINE);
		createEReference(pipelineEClass, PIPELINE__STAGES);

		stageEClass = createEClass(STAGE);

		filterStageEClass = createEClass(FILTER_STAGE);
		createEReference(filterStageEClass, FILTER_STAGE__PREDICATE);

		groupByStageEClass = createEClass(GROUP_BY_STAGE);
		createEReference(groupByStageEClass, GROUP_BY_STAGE__PATHS);
		createEReference(groupByStageEClass, GROUP_BY_STAGE__KEYS);
		createEReference(groupByStageEClass, GROUP_BY_STAGE__AGGREGATES);

		groupKeyEClass = createEClass(GROUP_KEY);
		createEReference(groupKeyEClass, GROUP_KEY__EXPRESSION);
		createEAttribute(groupKeyEClass, GROUP_KEY__ALIAS);

		aggregateEClass = createEClass(AGGREGATE);
		createEReference(aggregateEClass, AGGREGATE__PATH);
		createEReference(aggregateEClass, AGGREGATE__SOURCE);
		createEAttribute(aggregateEClass, AGGREGATE__METHOD);
		createEAttribute(aggregateEClass, AGGREGATE__ALIAS);

		topStageEClass = createEClass(TOP_STAGE);
		createEAttribute(topStageEClass, TOP_STAGE__COUNT);

		skipStageEClass = createEClass(SKIP_STAGE);
		createEAttribute(skipStageEClass, SKIP_STAGE__COUNT);

		computeStageEClass = createEClass(COMPUTE_STAGE);
		createEReference(computeStageEClass, COMPUTE_STAGE__COMPUTATIONS);

		computationEClass = createEClass(COMPUTATION);
		createEReference(computationEClass, COMPUTATION__EXPRESSION);
		createEAttribute(computationEClass, COMPUTATION__ALIAS);

		// Create enums
		sortDirectionEEnum = createEEnum(SORT_DIRECTION);
		aggregateMethodEEnum = createEEnum(AGGREGATE_METHOD);
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
		ExpressionPackage theExpressionPackage = (ExpressionPackage)EPackage.Registry.INSTANCE.getEPackage(ExpressionPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		filterStageEClass.getESuperTypes().add(this.getStage());
		groupByStageEClass.getESuperTypes().add(this.getStage());
		topStageEClass.getESuperTypes().add(this.getStage());
		skipStageEClass.getESuperTypes().add(this.getStage());
		computeStageEClass.getESuperTypes().add(this.getStage());

		// Initialize classes, features, and operations; add parameters
		initEClass(queryEClass, Query.class, "Query", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getQuery_From(), ecorePackage.getEClass(), null, "from", null, 1, 1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getQuery_Predicate(), theExpressionPackage.getExpression(), null, "predicate", null, 0, 1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getQuery_OrderBy(), this.getOrderBy(), null, "orderBy", null, 0, -1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getQuery_Select(), this.getSelection(), null, "select", null, 0, -1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getQuery_Apply(), this.getPipeline(), null, "apply", null, 0, 1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getQuery_Expand(), theExpressionPackage.getPropertyPath(), null, "expand", null, 0, -1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getQuery_Top(), ecorePackage.getEInt(), "top", null, 0, 1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getQuery_Skip(), ecorePackage.getEInt(), "skip", null, 0, 1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getQuery_Distinct(), ecorePackage.getEBoolean(), "distinct", null, 0, 1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getQuery_CountOnly(), ecorePackage.getEBoolean(), "countOnly", null, 0, 1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getQuery_Parameters(), this.getParameterDecl(), null, "parameters", null, 0, -1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getQuery_Name(), ecorePackage.getEString(), "name", null, 0, 1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getQuery_SaveQuery(), ecorePackage.getEBoolean(), "saveQuery", null, 0, 1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(orderByEClass, OrderBy.class, "OrderBy", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getOrderBy_Path(), theExpressionPackage.getPropertyPath(), null, "path", null, 0, 1, OrderBy.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOrderBy_Key(), theExpressionPackage.getExpression(), null, "key", null, 0, 1, OrderBy.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getOrderBy_Direction(), this.getSortDirection(), "direction", "ASC", 1, 1, OrderBy.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(selectionEClass, Selection.class, "Selection", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getSelection_Path(), theExpressionPackage.getPropertyPath(), null, "path", null, 1, 1, Selection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSelection_Alias(), ecorePackage.getEString(), "alias", null, 0, 1, Selection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(parameterDeclEClass, ParameterDecl.class, "ParameterDecl", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getParameterDecl_Name(), ecorePackage.getEString(), "name", null, 1, 1, ParameterDecl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getParameterDecl_TypeHint(), ecorePackage.getEClassifier(), null, "typeHint", null, 0, 1, ParameterDecl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(pipelineEClass, Pipeline.class, "Pipeline", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getPipeline_Stages(), this.getStage(), null, "stages", null, 1, -1, Pipeline.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(stageEClass, Stage.class, "Stage", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(filterStageEClass, FilterStage.class, "FilterStage", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getFilterStage_Predicate(), theExpressionPackage.getExpression(), null, "predicate", null, 1, 1, FilterStage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(groupByStageEClass, GroupByStage.class, "GroupByStage", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getGroupByStage_Paths(), theExpressionPackage.getPropertyPath(), null, "paths", null, 0, -1, GroupByStage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getGroupByStage_Keys(), this.getGroupKey(), null, "keys", null, 0, -1, GroupByStage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getGroupByStage_Aggregates(), this.getAggregate(), null, "aggregates", null, 1, -1, GroupByStage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(groupKeyEClass, GroupKey.class, "GroupKey", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getGroupKey_Expression(), theExpressionPackage.getExpression(), null, "expression", null, 1, 1, GroupKey.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGroupKey_Alias(), ecorePackage.getEString(), "alias", null, 1, 1, GroupKey.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(aggregateEClass, Aggregate.class, "Aggregate", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getAggregate_Path(), theExpressionPackage.getPropertyPath(), null, "path", null, 0, 1, Aggregate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAggregate_Source(), theExpressionPackage.getExpression(), null, "source", null, 0, 1, Aggregate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAggregate_Method(), this.getAggregateMethod(), "method", null, 1, 1, Aggregate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAggregate_Alias(), ecorePackage.getEString(), "alias", null, 1, 1, Aggregate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(topStageEClass, TopStage.class, "TopStage", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getTopStage_Count(), ecorePackage.getEInt(), "count", null, 1, 1, TopStage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(skipStageEClass, SkipStage.class, "SkipStage", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSkipStage_Count(), ecorePackage.getEInt(), "count", null, 1, 1, SkipStage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(computeStageEClass, ComputeStage.class, "ComputeStage", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getComputeStage_Computations(), this.getComputation(), null, "computations", null, 1, -1, ComputeStage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(computationEClass, Computation.class, "Computation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getComputation_Expression(), theExpressionPackage.getExpression(), null, "expression", null, 1, 1, Computation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getComputation_Alias(), ecorePackage.getEString(), "alias", null, 1, 1, Computation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(sortDirectionEEnum, SortDirection.class, "SortDirection");
		addEEnumLiteral(sortDirectionEEnum, SortDirection.ASC);
		addEEnumLiteral(sortDirectionEEnum, SortDirection.DESC);

		initEEnum(aggregateMethodEEnum, AggregateMethod.class, "AggregateMethod");
		addEEnumLiteral(aggregateMethodEEnum, AggregateMethod.SUM);
		addEEnumLiteral(aggregateMethodEEnum, AggregateMethod.MIN);
		addEEnumLiteral(aggregateMethodEEnum, AggregateMethod.MAX);
		addEEnumLiteral(aggregateMethodEEnum, AggregateMethod.AVG);
		addEEnumLiteral(aggregateMethodEEnum, AggregateMethod.COUNT);
		addEEnumLiteral(aggregateMethodEEnum, AggregateMethod.COUNT_DISTINCT);

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
			   "value", "2.0"
		   });
	}

} //QueryPackageImpl
