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

import java.util.Map;

import org.eclipse.emf.common.util.URI;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.fennec.persistence.capabilities.PersistenceCapabilities;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Repository Service</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Base contract of every repository: identity, lifecycle, capabilities and the URI/resource machinery (formerly EMFRepositoryHelper). A repository owns a ResourceSet configured for its backend; all URIs it creates are rooted at its base URI. Registered as an OSGi service; the configuration surface (repository id, base URI, default options) lives in the hand-written RepositoryConstants. AutoCloseable: close() is equivalent to dispose().
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.persistence.repository.api.RepositoryApiPackage#getRepositoryService()
 * @model interface="true" abstract="true" superTypes="org.eclipse.fennec.persistence.query.api.AutoCloseable"
 * @generated
 */
@ProviderType
public interface RepositoryService extends AutoCloseable {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The stable configured id of this repository, unique per runtime. Also published as the service property named by RepositoryConstants.REPOSITORY_ID.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	String id();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The base URI this repository is bound to, without a trailing separator. Every URI this repository creates or resolves is rooted here.
	 * <!-- end-model-doc -->
	 * @model dataType="org.eclipse.fennec.persistence.repository.api.Uri"
	 * @generated
	 */
	URI baseUri();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The effective capabilities of the bound backend — narrowed by deployment probes exactly like PersistenceResource.capabilities(). Never null.
	 * <!-- end-model-doc -->
	 * @model dataType="org.eclipse.fennec.persistence.repository.api.PersistenceCapabilities"
	 * @generated
	 */
	PersistenceCapabilities capabilities();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Whether this repository has been disposed. A disposed repository refuses all data access with IllegalStateException.
	 * <!-- end-model-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	boolean isDisposed();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Releases the owned ResourceSet and all backend resources. Idempotent; close() delegates here.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	void dispose();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The repository-owned ResourceSet, fully configured for the bound backend. Shared by all repository operations — treat as read-mostly plumbing; prefer the repository operations over touching it directly.
	 * <!-- end-model-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	ResourceSet getResourceSet();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Creates a fresh ResourceSet configured like the repository-owned one but independent of it — for callers that need isolation (e.g. concurrent loads with conflicting states).
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	ResourceSet createResourceSet();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Creates the URI for the given EObject against the base URI, derived from its EClass and id. Returns null if no id can be determined.
	 * <!-- end-model-doc -->
	 * @model dataType="org.eclipse.fennec.persistence.repository.api.Uri" objectRequired="true"
	 * @generated
	 */
	URI createUri(EObject object);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Creates the URI for the given EObject against the base URI, honoring the given options (e.g. an id override or type hints).
	 * <!-- end-model-doc -->
	 * @model dataType="org.eclipse.fennec.persistence.repository.api.Uri" objectRequired="true" optionsDataType="org.eclipse.fennec.persistence.query.api.OptionsMap"
	 * @generated
	 */
	URI createUri(EObject object, Map<?, ?> options);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Creates the URI for an object of the given EClass with the given id against the base URI — without needing an instance.
	 * <!-- end-model-doc -->
	 * @model dataType="org.eclipse.fennec.persistence.repository.api.Uri" eClassRequired="true" idRequired="true"
	 * @generated
	 */
	URI createUri(EClass eClass, Object id);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Creates an unresolved proxy of the given EClass whose proxy URI points at the object with the given id — for wiring non-containment references without loading the target.
	 * <!-- end-model-doc -->
	 * @model eClassRequired="true" idRequired="true"
	 * @generated
	 */
	EObject createProxy(EClass eClass, Object id);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Turns the given loaded EObject back into a proxy (formerly proxiefyEObject): sets its proxy URI, detaches it from its Resource and unloads that resource. Non-containment references are left untouched.
	 * <!-- end-model-doc -->
	 * @model objectRequired="true"
	 * @generated
	 */
	void proxify(EObject object);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Attaches the given EObject to a Resource with its proper repository URI without saving it. If the object already has a resource, that resource is returned unchanged.
	 * <!-- end-model-doc -->
	 * @model objectRequired="true"
	 * @generated
	 */
	Resource attach(EObject object);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Attaches the given EObject to a Resource with its proper repository URI without saving it, honoring the given options.
	 * <!-- end-model-doc -->
	 * @model objectRequired="true" optionsDataType="org.eclipse.fennec.persistence.query.api.OptionsMap"
	 * @generated
	 */
	Resource attach(EObject object, Map<?, ?> options);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Detaches the given EObject from its Resource and unloads that resource, returning the now standalone object.
	 * <!-- end-model-doc -->
	 * @model objectRequired="true"
	 * @generated
	 */
	EObject detach(EObject object);

} // RepositoryService
