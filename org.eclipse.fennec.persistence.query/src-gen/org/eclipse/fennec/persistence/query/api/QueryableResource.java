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

import java.io.IOException;

import java.util.Map;

import org.eclipse.fennec.model.query.Query;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Queryable Resource</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Optional capability of a PersistenceResource: execute a canonical Query against the backend the resource is bound to (analogous to StreamingResource). The resource resolves its QueryProcessor, validates and translates the query, then executes the resulting QueryPlan and returns a QueryResult. A query using constructs the backend cannot serve natively fails with an IOException carrying the validation Diagnostic — never a silent in-memory fallback. Lives in the query bundle rather than the core persistence API so the core does not depend on the query model.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.persistence.query.api.QueryApiPackage#getQueryableResource()
 * @model interface="true" abstract="true"
 * @generated
 */
@ProviderType
public interface QueryableResource {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Executes a query with no parameters and no extra options. The result must be closed by the caller.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" queryRequired="true"
	 * @generated
	 */
	QueryResult query(Query query) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Executes a query with bound parameters and backend options (both may be null). The result must be closed by the caller.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" queryRequired="true" parametersDataType="org.eclipse.fennec.persistence.query.api.ParameterMap" optionsDataType="org.eclipse.fennec.persistence.query.api.OptionsMap"
	 * @generated
	 */
	QueryResult query(Query query, Map<String, Object> parameters, Map<?, ?> options) throws IOException;

} // QueryableResource
