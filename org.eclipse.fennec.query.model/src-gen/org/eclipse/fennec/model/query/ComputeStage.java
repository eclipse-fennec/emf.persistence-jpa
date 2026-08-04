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

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Compute Stage</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Alias-bound computed columns (issue #82, revisits decision D3; OData $apply compute). Three positions: terminal (no GroupBy in the pipeline — one row per entity: single-valued attributes plus the computed columns), after grouping (computations over aggregate aliases/group keys via AliasRef, e.g. Total div Cnt as Avg — visible to a trailing HAVING filter and orderBy), and before grouping (issue #87): the aliases bind a named scope for expression-valued group keys (GroupKey) and aggregate sources (Aggregate.source) — they are no result columns.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.query.ComputeStage#getComputations <em>Computations</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.query.QueryPackage#getComputeStage()
 * @model
 * @generated
 */
@ProviderType
public interface ComputeStage extends Stage {
	/**
	 * Returns the value of the '<em><b>Computations</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.query.Computation}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Computations</em>' containment reference list.
	 * @see org.eclipse.fennec.model.query.QueryPackage#getComputeStage_Computations()
	 * @model containment="true" required="true"
	 * @generated
	 */
	EList<Computation> getComputations();

} // ComputeStage
