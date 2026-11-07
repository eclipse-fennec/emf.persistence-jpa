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
package org.eclipse.fennec.model.command;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Update Template</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * The patch an UpdateCommand applies to every match, entry by entry in order (issue #397). Deliberately not the stream model's ChangeSet: a template carries no header, no object id and no fingerprint, only what the patch-apply engine (ChangeTemplates) evaluates. An empty template is refused, not treated as a no-op.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.command.UpdateTemplate#getEntries <em>Entries</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.command.CommandPackage#getUpdateTemplate()
 * @model
 * @generated
 */
@ProviderType
public interface UpdateTemplate extends EObject {
	/**
	 * Returns the value of the '<em><b>Entries</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.command.UpdateEntry}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Entries</em>' containment reference list.
	 * @see org.eclipse.fennec.model.command.CommandPackage#getUpdateTemplate_Entries()
	 * @model containment="true"
	 * @generated
	 */
	EList<UpdateEntry> getEntries();

} // UpdateTemplate
