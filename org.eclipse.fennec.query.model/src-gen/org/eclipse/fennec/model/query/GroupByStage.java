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

import org.eclipse.emf.common.util.EList;

import org.eclipse.fennec.model.expression.PropertyPath;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Group By Stage</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Grouping over the given paths with aggregate outputs. Without paths the aggregates apply to the whole input (single row).
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.query.GroupByStage#getPaths <em>Paths</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.GroupByStage#getAggregates <em>Aggregates</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.query.QueryPackage#getGroupByStage()
 * @model
 * @generated
 */
@ProviderType
public interface GroupByStage extends Stage {
	/**
	 * Returns the value of the '<em><b>Paths</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.expression.PropertyPath}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Paths</em>' containment reference list.
	 * @see org.eclipse.fennec.model.query.QueryPackage#getGroupByStage_Paths()
	 * @model containment="true"
	 * @generated
	 */
	EList<PropertyPath> getPaths();

	/**
	 * Returns the value of the '<em><b>Aggregates</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.query.Aggregate}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Aggregates</em>' containment reference list.
	 * @see org.eclipse.fennec.model.query.QueryPackage#getGroupByStage_Aggregates()
	 * @model containment="true" required="true"
	 * @generated
	 */
	EList<Aggregate> getAggregates();

} // GroupByStage
