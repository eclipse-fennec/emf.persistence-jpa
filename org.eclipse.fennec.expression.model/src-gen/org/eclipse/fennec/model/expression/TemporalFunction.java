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
 * A representation of the model object '<em><b>Temporal Function</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Temporal part extraction over a date/time value expression (issue #79; OData year..second). Normative semantics: the part is extracted from the value interpreted as a UTC instant; SECOND is integral (0-59, fractional seconds are floored — EclipseLink types EXTRACT(SECOND) as Double); time parts of date-only values are 0. Deliberately without date()/time() (ISO-string representation, divergence risk — OData does not push those either).
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.TemporalFunction#getKind <em>Kind</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.TemporalFunction#getSource <em>Source</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getTemporalFunction()
 * @model
 * @generated
 */
@ProviderType
public interface TemporalFunction extends Expression {
	/**
	 * Returns the value of the '<em><b>Kind</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.expression.TemporalFunctionKind}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Kind</em>' attribute.
	 * @see org.eclipse.fennec.model.expression.TemporalFunctionKind
	 * @see #setKind(TemporalFunctionKind)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getTemporalFunction_Kind()
	 * @model required="true"
	 * @generated
	 */
	TemporalFunctionKind getKind();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.TemporalFunction#getKind <em>Kind</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Kind</em>' attribute.
	 * @see org.eclipse.fennec.model.expression.TemporalFunctionKind
	 * @see #getKind()
	 * @generated
	 */
	void setKind(TemporalFunctionKind value);

	/**
	 * Returns the value of the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Source</em>' containment reference.
	 * @see #setSource(Expression)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getTemporalFunction_Source()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Expression getSource();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.TemporalFunction#getSource <em>Source</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source</em>' containment reference.
	 * @see #getSource()
	 * @generated
	 */
	void setSource(Expression value);

} // TemporalFunction
