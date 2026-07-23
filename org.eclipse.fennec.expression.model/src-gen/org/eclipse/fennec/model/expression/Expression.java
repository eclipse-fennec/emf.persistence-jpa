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

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Expression</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Abstract root of all expression-tree nodes. An expression evaluates against a context object (the query root, or an iterator variable) to a value; predicates evaluate to boolean.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getExpression()
 * @model abstract="true"
 * @generated
 */
@ProviderType
public interface Expression extends EObject {
} // Expression
