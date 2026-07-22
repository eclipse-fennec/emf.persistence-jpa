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

import java.util.Set;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Query Capabilities</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * The set of QueryFeatures a QueryProcessor serves natively, plus structural limits. Used to validate a query before translation so unsupported constructs are rejected with a Diagnostic rather than silently post-filtered.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.persistence.query.api.QueryApiPackage#getQueryCapabilities()
 * @model interface="true" abstract="true"
 * @generated
 */
@ProviderType
public interface QueryCapabilities {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Returns true if this backend serves the feature natively.
	 * <!-- end-model-doc -->
	 * @model featureRequired="true"
	 * @generated
	 */
	boolean supports(QueryFeature feature);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Returns the immutable set of natively supported features.
	 * <!-- end-model-doc -->
	 * @model dataType="org.eclipse.fennec.persistence.query.api.QueryFeatureSet"
	 * @generated
	 */
	Set<QueryFeature> supported();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The maximum FeaturePath depth this backend can traverse in where/sort/projection (each segment beyond the first is a join). 1 = local features only, -1 = unlimited.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	int maxFeaturePathDepth();

} // QueryCapabilities
