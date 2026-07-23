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

import org.eclipse.fennec.model.stream.ChangeSet;

import org.eclipse.fennec.model2.query.Query;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Update Command</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Updates every object the selector matches by applying the ChangeSet template (concept §14: Update = selector + patch; the query language needs no update vocabulary of its own). Executable once the patch-apply engine exists — until then backends refuse it with a diagnostic.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.command.UpdateCommand#getSelector <em>Selector</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.command.UpdateCommand#getTemplate <em>Template</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.command.CommandPackage#getUpdateCommand()
 * @model
 * @generated
 */
@ProviderType
public interface UpdateCommand extends Command {
	/**
	 * Returns the value of the '<em><b>Selector</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Selector</em>' containment reference.
	 * @see #setSelector(Query)
	 * @see org.eclipse.fennec.model.command.CommandPackage#getUpdateCommand_Selector()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Query getSelector();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.command.UpdateCommand#getSelector <em>Selector</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Selector</em>' containment reference.
	 * @see #getSelector()
	 * @generated
	 */
	void setSelector(Query value);

	/**
	 * Returns the value of the '<em><b>Template</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The change template applied per matched object. Entry coordinates address features of the selector's root type; objectId is resolved per match.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Template</em>' containment reference.
	 * @see #setTemplate(ChangeSet)
	 * @see org.eclipse.fennec.model.command.CommandPackage#getUpdateCommand_Template()
	 * @model containment="true" required="true"
	 * @generated
	 */
	ChangeSet getTemplate();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.command.UpdateCommand#getTemplate <em>Template</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Template</em>' containment reference.
	 * @see #getTemplate()
	 * @generated
	 */
	void setTemplate(ChangeSet value);

} // UpdateCommand
