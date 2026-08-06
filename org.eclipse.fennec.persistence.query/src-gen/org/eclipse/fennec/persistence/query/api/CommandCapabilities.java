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

import org.eclipse.emf.ecore.EClass;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Command Capabilities</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * The set of CommandFeatures a CommandResource serves (issue #114). Two-level contract: supports(feature) is the backend-wide answer — TRUE means the feature exists in this backend/deployment at all; supports(feature, eClass) is the routing truth and may narrow it per type (decision 2026-08-06: a backend that can serve a feature for some classes only declares it backend-wide and narrows per EClass instead of answering conservatively). An undeclared command is refused before any work with a Diagnostic naming the CommandFeature — 'refused' and 'failed' stay distinguishable.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.persistence.query.api.QueryApiPackage#getCommandCapabilities()
 * @model interface="true" abstract="true"
 * @generated
 */
@ProviderType
public interface CommandCapabilities {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The backend-wide answer: true if this resource serves the feature at all.
	 * <!-- end-model-doc -->
	 * @model featureRequired="true"
	 * @generated
	 */
	boolean supports(CommandFeature feature);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The per-type routing answer: true if this resource serves the feature for the given EClass. Defaults to the backend-wide answer; backends narrow it where the mapping decides (e.g. update only for materialized classes).
	 * <!-- end-model-doc -->
	 * @model featureRequired="true" eClassRequired="true"
	 * @generated
	 */
	boolean supports(CommandFeature feature, EClass eClass);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Returns the immutable set of backend-wide supported features.
	 * <!-- end-model-doc -->
	 * @model dataType="org.eclipse.fennec.persistence.query.api.CommandFeatureSet"
	 * @generated
	 */
	Set<CommandFeature> supported();

} // CommandCapabilities
