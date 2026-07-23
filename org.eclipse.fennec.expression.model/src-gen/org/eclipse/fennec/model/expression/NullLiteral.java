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
package org.eclipse.fennec.model.expression;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Null Literal</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * The null value — valid as a comparison operand only where the backend defines it; prefer IsNull for null checks.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getNullLiteral()
 * @model
 * @generated
 */
@ProviderType
public interface NullLiteral extends Literal {
} // NullLiteral
