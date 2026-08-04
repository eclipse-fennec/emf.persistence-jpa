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

import org.eclipse.emf.ecore.EFactory;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.model.query.QueryPackage
 * @generated
 */
@ProviderType
public interface QueryFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	QueryFactory eINSTANCE = org.eclipse.fennec.model.query.impl.QueryFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Query</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Query</em>'.
	 * @generated
	 */
	Query createQuery();

	/**
	 * Returns a new object of class '<em>Order By</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Order By</em>'.
	 * @generated
	 */
	OrderBy createOrderBy();

	/**
	 * Returns a new object of class '<em>Selection</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Selection</em>'.
	 * @generated
	 */
	Selection createSelection();

	/**
	 * Returns a new object of class '<em>Parameter Decl</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Parameter Decl</em>'.
	 * @generated
	 */
	ParameterDecl createParameterDecl();

	/**
	 * Returns a new object of class '<em>Pipeline</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Pipeline</em>'.
	 * @generated
	 */
	Pipeline createPipeline();

	/**
	 * Returns a new object of class '<em>Filter Stage</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Filter Stage</em>'.
	 * @generated
	 */
	FilterStage createFilterStage();

	/**
	 * Returns a new object of class '<em>Group By Stage</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Group By Stage</em>'.
	 * @generated
	 */
	GroupByStage createGroupByStage();

	/**
	 * Returns a new object of class '<em>Group Key</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Group Key</em>'.
	 * @generated
	 */
	GroupKey createGroupKey();

	/**
	 * Returns a new object of class '<em>Aggregate</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Aggregate</em>'.
	 * @generated
	 */
	Aggregate createAggregate();

	/**
	 * Returns a new object of class '<em>Top Stage</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Top Stage</em>'.
	 * @generated
	 */
	TopStage createTopStage();

	/**
	 * Returns a new object of class '<em>Skip Stage</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Skip Stage</em>'.
	 * @generated
	 */
	SkipStage createSkipStage();

	/**
	 * Returns a new object of class '<em>Compute Stage</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Compute Stage</em>'.
	 * @generated
	 */
	ComputeStage createComputeStage();

	/**
	 * Returns a new object of class '<em>Computation</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Computation</em>'.
	 * @generated
	 */
	Computation createComputation();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	QueryPackage getQueryPackage();

} //QueryFactory
