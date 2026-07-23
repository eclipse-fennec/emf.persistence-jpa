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

import org.eclipse.fennec.model.command.Command;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Command Resource</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Optional capability of a PersistenceResource: execute write commands (concept.md §14 — Insert with payload, Delete = query selector, Update = selector + ChangeSet template). Insert and Delete execute in v1; Update is refused with a diagnostic until the patch-apply engine exists. Selectors must be plain filters — projection, aggregation, ordering and paging on a command selector are refused.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.persistence.query.api.QueryApiPackage#getCommandResource()
 * @model interface="true" abstract="true"
 * @generated
 */
@ProviderType
public interface CommandResource {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Executes the command and returns the number of affected objects (inserted or deleted).
	 * <!-- end-model-doc -->
	 * @model exceptions="org.eclipse.fennec.persistence.query.api.IOException" commandRequired="true"
	 * @generated
	 */
	long execute(Command command) throws IOException;

} // CommandResource
