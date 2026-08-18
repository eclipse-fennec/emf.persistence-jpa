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

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Hit</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * One scored result of a withScores query (issue #165): the object paired with its per-hit metadata, in stream order. This is the extensible carrier — further per-hit payloads (highlight fragments, rank signals) grow here rather than as parallel side channels.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.persistence.query.api.QueryApiPackage#getHit()
 * @model interface="true" abstract="true"
 * @generated
 */
@ProviderType
public interface Hit {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The matched entity.
	 * <!-- end-model-doc -->
	 * @model required="true"
	 * @generated
	 */
	EObject object();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The relevance of this hit under the executed query. Document-level: one value per hit; what the score of a hit without a scoring predicate is stays backend-defined.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	double score();

} // Hit
