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
 * A representation of the model object '<em><b>Substring</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Substring of source from start (0-based), optionally length-bounded (issue #77; [OData-URL] 5.1.1.7): a negative start counts from the end of the string (clamped to position 0), a start beyond the end yields the empty string, a negative length yields the empty string.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.Substring#getSource <em>Source</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.Substring#getStart <em>Start</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.Substring#getLength <em>Length</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getSubstring()
 * @model
 * @generated
 */
@ProviderType
public interface Substring extends Expression {
	/**
	 * Returns the value of the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Source</em>' containment reference.
	 * @see #setSource(Expression)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getSubstring_Source()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Expression getSource();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.Substring#getSource <em>Source</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source</em>' containment reference.
	 * @see #getSource()
	 * @generated
	 */
	void setSource(Expression value);

	/**
	 * Returns the value of the '<em><b>Start</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Start</em>' containment reference.
	 * @see #setStart(Expression)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getSubstring_Start()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Expression getStart();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.Substring#getStart <em>Start</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Start</em>' containment reference.
	 * @see #getStart()
	 * @generated
	 */
	void setStart(Expression value);

	/**
	 * Returns the value of the '<em><b>Length</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Length</em>' containment reference.
	 * @see #setLength(Expression)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getSubstring_Length()
	 * @model containment="true"
	 * @generated
	 */
	Expression getLength();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.Substring#getLength <em>Length</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Length</em>' containment reference.
	 * @see #getLength()
	 * @generated
	 */
	void setLength(Expression value);

} // Substring
