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
package org.eclipse.fennec.persistence.repository.api;

import java.io.IOException;

import java.util.Map;

import org.eclipse.emf.common.util.EList;

import org.eclipse.fennec.model.query.ParameterDecl;
import org.eclipse.fennec.model.query.Query;

import org.eclipse.fennec.persistence.query.api.QueryResult;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Prepared Query</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * A canonical Query bound to one repository and prepared for repeated execution — the prepared-statement analogue. Obtained programmatically via ReadRepository.prepare(...) or registered as a configured OSGi service carrying the repository id and query name as service properties. The query was validated against the backend at preparation time; execute only binds the declared parameters. Whether the translated plan is cached between executions is an implementation detail. Deliberately a minimal standalone handle, not a RepositoryService.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.persistence.repository.api.RepositoryApiPackage#getPreparedQuery()
 * @model interface="true" abstract="true"
 * @generated
 */
@ProviderType
public interface PreparedQuery {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The query's name (Query.name), or null for an unnamed ad-hoc prepared query.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	String name();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The canonical query this handle was prepared from. Treat as read-only — mutating it does not affect this handle.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	Query query();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The declared parameters of the prepared query (Query.parameters) — name and optional type hint, for callers and tooling to know what execute expects.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	EList<ParameterDecl> parameterDeclarations();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Executes the prepared query with the given parameter bindings. A declared ParameterRef without a binding fails translation with a diagnostic. The result must be closed by the caller.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" parametersDataType="org.eclipse.fennec.persistence.query.api.ParameterMap"
	 * @generated
	 */
	QueryResult execute(Map<String, Object> parameters) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Executes the prepared query with the given parameter bindings and backend options (both may be null). The result must be closed by the caller.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" parametersDataType="org.eclipse.fennec.persistence.query.api.ParameterMap" optionsDataType="org.eclipse.fennec.persistence.query.api.OptionsMap"
	 * @generated
	 */
	QueryResult execute(Map<String, Object> parameters, Map<?, ?> options) throws IOException;

} // PreparedQuery
