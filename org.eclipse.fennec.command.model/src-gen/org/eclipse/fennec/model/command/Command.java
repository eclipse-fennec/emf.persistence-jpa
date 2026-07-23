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

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Command</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Abstract write command. Execution is a backend capability (CommandResource); refusal follows the diagnostics contract of the query SPI.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.model.command.CommandPackage#getCommand()
 * @model abstract="true"
 * @generated
 */
@ProviderType
public interface Command extends EObject {
} // Command
