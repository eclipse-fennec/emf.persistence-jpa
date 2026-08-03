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

import org.eclipse.emf.common.util.EList;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Concat</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * String concatenation of all parts, in order (issue #77; OData concat). Deliberately n-ary — the binary OCL concat chains flatten on the bridge like the junctions.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.Concat#getParts <em>Parts</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getConcat()
 * @model
 * @generated
 */
@ProviderType
public interface Concat extends Expression {
	/**
	 * Returns the value of the '<em><b>Parts</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.expression.Expression}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parts</em>' containment reference list.
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getConcat_Parts()
	 * @model containment="true" lower="2"
	 * @generated
	 */
	EList<Expression> getParts();

} // Concat
