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
 * A representation of the model object '<em><b>Insert Command</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Inserts the payload objects (save semantics of the target resource).
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.command.InsertCommand#getObjects <em>Objects</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.command.CommandPackage#getInsertCommand()
 * @model
 * @generated
 */
@ProviderType
public interface InsertCommand extends Command {
	/**
	 * Returns the value of the '<em><b>Objects</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.EObject}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The payload; contained so a persisted command is self-contained.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Objects</em>' containment reference list.
	 * @see org.eclipse.fennec.model.command.CommandPackage#getInsertCommand_Objects()
	 * @model containment="true" required="true"
	 * @generated
	 */
	EList<EObject> getObjects();

} // InsertCommand
