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
 * Grouping over the given paths and expression keys with aggregate outputs. The effective group keys are paths plus keys; without either the aggregates apply to the whole input (single row). Expression keys (issue #87) accept arbitrary expressions including AliasRef to a pre-group compute alias.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.query.GroupByStage#getPaths <em>Paths</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.GroupByStage#getKeys <em>Keys</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.GroupByStage#getAggregates <em>Aggregates</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.GroupByStage#getRepresentatives <em>Representatives</em>}</li>
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
	 * Returns the value of the '<em><b>Keys</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.query.GroupKey}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Keys</em>' containment reference list.
	 * @see org.eclipse.fennec.model.query.QueryPackage#getGroupByStage_Keys()
	 * @model containment="true"
	 * @generated
	 */
	EList<GroupKey> getKeys();

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

	/**
	 * Returns the value of the '<em><b>Representatives</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The top-N documents of each group next to its aggregates (issue #214) — what SQL spells with a windowed ROW_NUMBER() OVER (PARTITION BY ...) and Lucene serves natively. Set here rather than as its own stage because it is a property of the grouping, and deliberately not the reserved BottomTop slot, which covers OData's topcount family over ROWS (the best groups by an aggregate) and is expressible today. Capability GROUP_REPRESENTATIVES.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Representatives</em>' containment reference.
	 * @see #setRepresentatives(RepresentativeSpec)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getGroupByStage_Representatives()
	 * @model containment="true"
	 * @generated
	 */
	RepresentativeSpec getRepresentatives();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.GroupByStage#getRepresentatives <em>Representatives</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Representatives</em>' containment reference.
	 * @see #getRepresentatives()
	 * @generated
	 */
	void setRepresentatives(RepresentativeSpec value);

} // GroupByStage
