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

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.common.util.URI;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.fennec.model.command.Command;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Write Repository</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Write side of the repository: save and delete EObjects — the repository takes care of attaching them to properly-URI'd Resources — and execute canonical write commands (command.ecore) first-class, delegating to the backend's CommandResource. All operations throw IOException on backend failure. A deployment may withhold this interface for read-only views.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.persistence.repository.api.RepositoryApiPackage#getWriteRepository()
 * @model interface="true" abstract="true"
 * @generated
 */
@ProviderType
public interface WriteRepository extends RepositoryService {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Saves exactly the given EObject under its repository URI — the old one-resource-per-object save semantics on top of the collection-resource mechanic. Objects sharing a loaded collection resource are not written: the repository isolates the object for the save and restores its attachment afterwards; an unattached object is attached to its collection resource after saving.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" objectRequired="true"
	 * @generated
	 */
	void save(EObject object) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Saves exactly the given EObject using the given save options — same isolation contract as save(object).
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" objectRequired="true" optionsDataType="org.eclipse.fennec.persistence.query.api.OptionsMap"
	 * @generated
	 */
	void save(EObject object, Map<?, ?> options) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Saves the given EObject at the given explicit URI instead of its derived repository URI.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" objectRequired="true" uriDataType="org.eclipse.fennec.persistence.repository.api.Uri" uriRequired="true"
	 * @generated
	 */
	void save(EObject object, URI uri) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Saves the given EObject at the given explicit URI using the given save options.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" objectRequired="true" uriDataType="org.eclipse.fennec.persistence.repository.api.Uri" uriRequired="true" optionsDataType="org.eclipse.fennec.persistence.query.api.OptionsMap"
	 * @generated
	 */
	void save(EObject object, URI uri, Map<?, ?> options) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Saves exactly the given EObjects — one backend save per type, replacing the old save(Collection)/save(EObject...) overload family. Unrelated objects sharing a loaded collection resource are not written; the same isolation contract as save(object) applies.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" objectsDataType="org.eclipse.fennec.persistence.repository.api.EObjectCollection" objectsRequired="true"
	 * @generated
	 */
	void saveAll(Collection<EObject> objects) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Saves all given EObjects using the given save options.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" objectsDataType="org.eclipse.fennec.persistence.repository.api.EObjectCollection" objectsRequired="true" optionsDataType="org.eclipse.fennec.persistence.query.api.OptionsMap"
	 * @generated
	 */
	void saveAll(Collection<EObject> objects, Map<?, ?> options) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Deletes the given EObject from the backend and detaches it from its Resource.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" objectRequired="true"
	 * @generated
	 */
	void delete(EObject object) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Deletes the given EObject using the given options.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" objectRequired="true" optionsDataType="org.eclipse.fennec.persistence.query.api.OptionsMap"
	 * @generated
	 */
	void delete(EObject object, Map<?, ?> options) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Deletes the object at the given URI without loading it first.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" uriDataType="org.eclipse.fennec.persistence.repository.api.Uri" uriRequired="true"
	 * @generated
	 */
	void delete(URI uri) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Deletes the object at the given URI without loading it first, using the given options.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" uriDataType="org.eclipse.fennec.persistence.repository.api.Uri" uriRequired="true" optionsDataType="org.eclipse.fennec.persistence.query.api.OptionsMap"
	 * @generated
	 */
	void delete(URI uri, Map<?, ?> options) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Executes a canonical write command (Insert/Delete/Update), delegating to the backend's CommandResource, and answers the number of affected objects. Same refusal contract as CommandResource.execute.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" commandRequired="true"
	 * @generated
	 */
	long execute(Command command) throws IOException;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Executes a write command with bound parameters (issue #202) - the write-side counterpart of find(query, parameters, options). A selector may use ParameterRef nodes; this is where their values come from. Both maps may be null.
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" commandRequired="true" parametersDataType="org.eclipse.fennec.persistence.query.api.ParameterMap" optionsDataType="org.eclipse.fennec.persistence.query.api.OptionsMap"
	 * @generated
	 */
	long execute(Command command, Map<String, Object> parameters, Map<?, ?> options) throws IOException;

} // WriteRepository
