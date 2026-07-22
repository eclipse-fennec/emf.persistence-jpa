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

import java.util.Map;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.fennec.persistence.api.ConverterService;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Query Context</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Everything a QueryProcessor needs for translation that is not carried by the Query itself: the resolved root type, the shared value converter, bound parameters and backend options.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.persistence.query.api.QueryApiPackage#getQueryContext()
 * @model interface="true" abstract="true"
 * @generated
 */
@ProviderType
public interface QueryContext {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The resolved root EClass the query selects from.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	EClass rootEClass();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The shared converter used to turn the query's EString comparator values into the target feature's typed value — done once here, not per backend. Never null.
	 * <!-- end-model-doc -->
	 * @model dataType="org.eclipse.fennec.persistence.query.api.ConverterService"
	 * @generated
	 */
	ConverterService converter();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Bound values for placeholders used by prepared queries. Empty if none.
	 * <!-- end-model-doc -->
	 * @model dataType="org.eclipse.fennec.persistence.query.api.ParameterMap"
	 * @generated
	 */
	Map<String, Object> parameters();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Backend options for the translation/execution (e.g. page size, batch size, read options). Empty if none.
	 * <!-- end-model-doc -->
	 * @model dataType="org.eclipse.fennec.persistence.query.api.OptionsMap"
	 * @generated
	 */
	Map<?, ?> options();

} // QueryContext
