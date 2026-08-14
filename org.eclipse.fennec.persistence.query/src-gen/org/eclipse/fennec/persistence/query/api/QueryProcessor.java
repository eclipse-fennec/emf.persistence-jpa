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

import org.eclipse.emf.common.util.Diagnostic;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.fennec.model.query.Query;

import org.eclipse.fennec.persistence.capabilities.QueryCapabilities;

import org.eclipse.fennec.persistence.query.QueryException;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Query Processor</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Translates a canonical Query into a backend-typed QueryPlan. Registered as an OSGi service with the property persistence.query.backend=<id> (see the hand-written QueryConstants) so a QueryableResource can resolve the processor matching its backend. Translation is a pure function of the query, the root type and the QueryContext; it must not touch the database. Execution of the produced plan is the responsibility of the backend resource, which knows the concrete plan type.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.persistence.query.api.QueryApiPackage#getQueryProcessor()
 * @model interface="true" abstract="true"
 * @generated
 */
@ProviderType
public interface QueryProcessor {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The stable backend id this processor serves, e.g. "mongo" or "jpa".
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	String backend();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The features this processor serves natively.
	 * <!-- end-model-doc -->
	 * @model dataType="org.eclipse.fennec.persistence.query.api.QueryCapabilities"
	 * @generated
	 */
	QueryCapabilities capabilities();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Validates a query against the declared capabilities for the given root type. The result never contains a silent fallback: any construct this backend cannot serve natively yields a Diagnostic ERROR entry naming the offending QueryFeature and model element. Returns Diagnostic OK if the query translates natively.
	 * <!-- end-model-doc -->
	 * @model dataType="org.eclipse.fennec.persistence.query.api.Diagnostic" queryRequired="true" rootEClassRequired="true"
	 * @generated
	 */
	Diagnostic validate(Query query, EClass rootEClass);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Translates a validated query into an executable, backend-typed plan. Callers must validate first; a processor may assume a validated query and throw QueryException when it encounters an unsupported construct.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.QueryException" queryRequired="true" contextRequired="true"
	 * @generated
	 */
	QueryPlan translate(Query query, QueryContext context) throws QueryException;

} // QueryProcessor
