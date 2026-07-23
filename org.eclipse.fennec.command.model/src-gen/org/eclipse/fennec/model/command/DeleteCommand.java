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

import org.eclipse.fennec.model2.query.Query;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Delete Command</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Deletes every object the selector matches (concept §14: Delete = selector). Backends translate the selector's predicate to a native scoped delete.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.command.DeleteCommand#getSelector <em>Selector</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.command.CommandPackage#getDeleteCommand()
 * @model
 * @generated
 */
@ProviderType
public interface DeleteCommand extends Command {
	/**
	 * Returns the value of the '<em><b>Selector</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Selector</em>' containment reference.
	 * @see #setSelector(Query)
	 * @see org.eclipse.fennec.model.command.CommandPackage#getDeleteCommand_Selector()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Query getSelector();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.command.DeleteCommand#getSelector <em>Selector</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Selector</em>' containment reference.
	 * @see #getSelector()
	 * @generated
	 */
	void setSelector(Query value);

} // DeleteCommand
