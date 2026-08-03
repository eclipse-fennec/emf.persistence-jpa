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
 * A representation of the model object '<em><b>Duration Literal</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Duration as ISO-8601 text (issue #83, analogous to D1) — resolved to java.time.Duration, epoch-milliseconds Long or kept as String against the target feature's type at translation.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.DurationLiteral#getIso8601 <em>Iso8601</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getDurationLiteral()
 * @model
 * @generated
 */
@ProviderType
public interface DurationLiteral extends Literal {
	/**
	 * Returns the value of the '<em><b>Iso8601</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Iso8601</em>' attribute.
	 * @see #setIso8601(String)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getDurationLiteral_Iso8601()
	 * @model required="true"
	 * @generated
	 */
	String getIso8601();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.DurationLiteral#getIso8601 <em>Iso8601</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Iso8601</em>' attribute.
	 * @see #getIso8601()
	 * @generated
	 */
	void setIso8601(String value);

} // DurationLiteral
