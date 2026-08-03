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
 * A representation of the model object '<em><b>Numeric Function</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Numeric rounding function over a value expression (issue #78; OData round/floor/ceiling). ROUND rounds half away from zero (OData semantics; Mongo's half-to-even $round is emulated). The result is integral.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.NumericFunction#getKind <em>Kind</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.NumericFunction#getSource <em>Source</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getNumericFunction()
 * @model
 * @generated
 */
@ProviderType
public interface NumericFunction extends Expression {
	/**
	 * Returns the value of the '<em><b>Kind</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.expression.NumericFunctionKind}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Kind</em>' attribute.
	 * @see org.eclipse.fennec.model.expression.NumericFunctionKind
	 * @see #setKind(NumericFunctionKind)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getNumericFunction_Kind()
	 * @model required="true"
	 * @generated
	 */
	NumericFunctionKind getKind();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.NumericFunction#getKind <em>Kind</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Kind</em>' attribute.
	 * @see org.eclipse.fennec.model.expression.NumericFunctionKind
	 * @see #getKind()
	 * @generated
	 */
	void setKind(NumericFunctionKind value);

	/**
	 * Returns the value of the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Source</em>' containment reference.
	 * @see #setSource(Expression)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getNumericFunction_Source()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Expression getSource();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.NumericFunction#getSource <em>Source</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source</em>' containment reference.
	 * @see #getSource()
	 * @generated
	 */
	void setSource(Expression value);

} // NumericFunction
