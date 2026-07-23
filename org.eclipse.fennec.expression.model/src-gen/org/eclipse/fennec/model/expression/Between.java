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
 * A representation of the model object '<em><b>Between</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Range check with configurable bound inclusion. Replaces the v1 IsInRange.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.Between#isLowerIncluded <em>Lower Included</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.Between#isUpperIncluded <em>Upper Included</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.Between#getSource <em>Source</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.Between#getLower <em>Lower</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.Between#getUpper <em>Upper</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getBetween()
 * @model
 * @generated
 */
@ProviderType
public interface Between extends Expression {
	/**
	 * Returns the value of the '<em><b>Lower Included</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Lower Included</em>' attribute.
	 * @see #setLowerIncluded(boolean)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getBetween_LowerIncluded()
	 * @model default="true"
	 * @generated
	 */
	boolean isLowerIncluded();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.Between#isLowerIncluded <em>Lower Included</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Lower Included</em>' attribute.
	 * @see #isLowerIncluded()
	 * @generated
	 */
	void setLowerIncluded(boolean value);

	/**
	 * Returns the value of the '<em><b>Upper Included</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Upper Included</em>' attribute.
	 * @see #setUpperIncluded(boolean)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getBetween_UpperIncluded()
	 * @model default="true"
	 * @generated
	 */
	boolean isUpperIncluded();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.Between#isUpperIncluded <em>Upper Included</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Upper Included</em>' attribute.
	 * @see #isUpperIncluded()
	 * @generated
	 */
	void setUpperIncluded(boolean value);

	/**
	 * Returns the value of the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Source</em>' containment reference.
	 * @see #setSource(Expression)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getBetween_Source()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Expression getSource();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.Between#getSource <em>Source</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source</em>' containment reference.
	 * @see #getSource()
	 * @generated
	 */
	void setSource(Expression value);

	/**
	 * Returns the value of the '<em><b>Lower</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Lower</em>' containment reference.
	 * @see #setLower(Expression)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getBetween_Lower()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Expression getLower();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.Between#getLower <em>Lower</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Lower</em>' containment reference.
	 * @see #getLower()
	 * @generated
	 */
	void setLower(Expression value);

	/**
	 * Returns the value of the '<em><b>Upper</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Upper</em>' containment reference.
	 * @see #setUpper(Expression)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getBetween_Upper()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Expression getUpper();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.Between#getUpper <em>Upper</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Upper</em>' containment reference.
	 * @see #getUpper()
	 * @generated
	 */
	void setUpper(Expression value);

} // Between
