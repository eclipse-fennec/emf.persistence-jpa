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

import org.eclipse.fennec.model.query.Query;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Query Plan</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * A backend-typed, executable translation of a canonical Query. Concrete plans live in the backend bundles and carry the native payload (e.g. a MongoDB aggregation pipeline or a JPA CriteriaQuery). Only the backend that produced a plan handles its concrete type; callers stay on this generic surface.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.persistence.query.api.QueryApiPackage#getQueryPlan()
 * @model interface="true" abstract="true"
 * @generated
 */
@ProviderType
public interface QueryPlan {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The canonical query this plan was translated from.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	Query source();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The shape of the result this plan produces when executed.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	QueryShape shape();

} // QueryPlan
