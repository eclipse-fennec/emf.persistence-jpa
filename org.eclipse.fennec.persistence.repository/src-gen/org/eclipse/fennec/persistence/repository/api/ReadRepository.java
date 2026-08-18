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

import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.resource.Resource;

import org.eclipse.fennec.model.query.Query;

import org.eclipse.fennec.persistence.query.api.QueryResult;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Read Repository</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Read side of the repository: resolve objects by URI or EClass+id, enumerate, count, test existence, reload — and execute canonical queries (query.ecore) first-class, delegating to the backend's QueryableResource. All data-touching operations throw IOException on backend failure, consistent with the EMF Resource load/save contract; a plain miss answers null (or false/0) instead. A deployment may expose only this interface for read-only views.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.persistence.repository.api.RepositoryApiPackage#getReadRepository()
 * @model interface="true" abstract="true"
 * @generated
 */
@ProviderType
public interface ReadRepository extends RepositoryService {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The Resource for the given URI from the owned ResourceSet, loaded on demand when the flag is set. Null if it does not exist and loadOnDemand is false.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" uriDataType="org.eclipse.fennec.persistence.repository.api.Uri" uriRequired="true" loadOnDemandRequired="true"
	 * @generated
	 */
	Resource getResource(URI uri, boolean loadOnDemand) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Loads the EObject with the given URI, or null if it does not exist.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" uriDataType="org.eclipse.fennec.persistence.repository.api.Uri" uriRequired="true"
	 * @generated
	 */
	EObject getEObject(URI uri) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Loads the EObject with the given URI using the given load options, or null if it does not exist.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" uriDataType="org.eclipse.fennec.persistence.repository.api.Uri" uriRequired="true" optionsDataType="org.eclipse.fennec.persistence.query.api.OptionsMap"
	 * @generated
	 */
	EObject getEObject(URI uri, Map<?, ?> options) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Loads the EObject of the given EClass with the given id, or null if it does not exist. The URI is built via createUri(eClass, id).
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" eClassRequired="true" idRequired="true"
	 * @generated
	 */
	EObject getEObject(EClass eClass, Object id) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Loads the EObject of the given EClass with the given id using the given load options, or null if it does not exist.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" eClassRequired="true" idRequired="true" optionsDataType="org.eclipse.fennec.persistence.query.api.OptionsMap"
	 * @generated
	 */
	EObject getEObject(EClass eClass, Object id, Map<?, ?> options) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * All objects of the given EClass as a lazy stream — the successor of the old List-returning getAllEObjects. The stream holds backend resources and must be closed by the caller (try-with-resources).
	 * <!-- end-model-doc -->
	 * @model dataType="org.eclipse.fennec.persistence.query.api.EObjectStream" exceptions="org.eclipse.fennec.persistence.query.api.IOException" eClassRequired="true"
	 * @generated
	 */
	Stream<EObject> getAllEObjects(EClass eClass) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * All objects of the given EClass as a lazy stream, using the given load options. The stream must be closed by the caller.
	 * <!-- end-model-doc -->
	 * @model dataType="org.eclipse.fennec.persistence.query.api.EObjectStream" exceptions="org.eclipse.fennec.persistence.query.api.IOException" eClassRequired="true" optionsDataType="org.eclipse.fennec.persistence.query.api.OptionsMap"
	 * @generated
	 */
	Stream<EObject> getAllEObjects(EClass eClass, Map<?, ?> options) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Counts all objects of the given EClass.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" eClassRequired="true"
	 * @generated
	 */
	long count(EClass eClass) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Counts all objects of the given EClass, using the given options.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" eClassRequired="true" optionsDataType="org.eclipse.fennec.persistence.query.api.OptionsMap"
	 * @generated
	 */
	long count(EClass eClass, Map<?, ?> options) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Whether an object exists at the given URI — without loading it.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" uriDataType="org.eclipse.fennec.persistence.repository.api.Uri" uriRequired="true"
	 * @generated
	 */
	boolean exist(URI uri) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Whether an object of the given EClass with the given id exists — without loading it.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" eClassRequired="true" idRequired="true"
	 * @generated
	 */
	boolean exist(EClass eClass, Object id) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Re-reads the given EObject's state from the backend, replacing its in-memory state.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" objectRequired="true"
	 * @generated
	 */
	void reload(EObject object) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Executes a canonical query against the bound backend, delegating to its QueryableResource. Same contract: capability violations fail with an IOException carrying the validation Diagnostic — never a silent in-memory fallback. The result must be closed by the caller.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" queryRequired="true"
	 * @generated
	 */
	QueryResult find(Query query) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Executes a canonical query with bound parameters and backend options (both may be null). The result must be closed by the caller.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" queryRequired="true" parametersDataType="org.eclipse.fennec.persistence.query.api.ParameterMap" optionsDataType="org.eclipse.fennec.persistence.query.api.OptionsMap"
	 * @generated
	 */
	QueryResult find(Query query, Map<String, Object> parameters, Map<?, ?> options) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Loads the query persisted under the given name (saveQuery) and executes it with bound parameters and backend options (both may be null). An unknown name is refused with an IOException carrying a Diagnostic. The result must be closed by the caller.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" nameRequired="true" parametersDataType="org.eclipse.fennec.persistence.query.api.ParameterMap" optionsDataType="org.eclipse.fennec.persistence.query.api.OptionsMap"
	 * @generated
	 */
	QueryResult find(String name, Map<String, Object> parameters, Map<?, ?> options) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Convenience for COUNT-shaped queries: executes the query and answers the cardinality directly, closing the underlying result.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" queryRequired="true"
	 * @generated
	 */
	long count(Query query) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Prepares the given canonical query for repeated parameterized execution — the createNamedQuery analogue. Validation against the backend's QueryProcessor happens here, once: a query the backend cannot serve natively is refused with an IOException carrying the Diagnostic, never at first execute.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" queryRequired="true"
	 * @generated
	 */
	PreparedQuery prepare(Query query) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Loads the query persisted under the given name (saveQuery) from the backend's query catalog and prepares it. An unknown name is refused with an IOException carrying a Diagnostic.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" nameRequired="true"
	 * @generated
	 */
	PreparedQuery prepare(String name) throws IOException;

} // ReadRepository
