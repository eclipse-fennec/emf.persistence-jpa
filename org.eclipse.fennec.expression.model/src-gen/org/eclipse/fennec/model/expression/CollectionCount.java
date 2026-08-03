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
 * A representation of the model object '<em><b>Collection Count</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Element count of a multi-valued navigation, optionally predicate-filtered, usable as a value expression in comparisons (issue #81; OData reviews/$count($filter=…), OCL size over select). JPA: SIZE(path) resp. a correlated SELECT COUNT subquery; Mongo: $size resp. $size($filter) over embedded collections — cross-document counts are refused by the embedded-path validation.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.CollectionCount#getSource <em>Source</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.CollectionCount#getVariable <em>Variable</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.CollectionCount#getPredicate <em>Predicate</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getCollectionCount()
 * @model
 * @generated
 */
@ProviderType
public interface CollectionCount extends Expression {
	/**
	 * Returns the value of the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Source</em>' containment reference.
	 * @see #setSource(PropertyPath)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getCollectionCount_Source()
	 * @model containment="true" required="true"
	 * @generated
	 */
	PropertyPath getSource();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.CollectionCount#getSource <em>Source</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source</em>' containment reference.
	 * @see #getSource()
	 * @generated
	 */
	void setSource(PropertyPath value);

	/**
	 * Returns the value of the '<em><b>Variable</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The element variable scoping the predicate; set exactly when predicate is set.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Variable</em>' containment reference.
	 * @see #setVariable(Variable)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getCollectionCount_Variable()
	 * @model containment="true"
	 * @generated
	 */
	Variable getVariable();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.CollectionCount#getVariable <em>Variable</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Variable</em>' containment reference.
	 * @see #getVariable()
	 * @generated
	 */
	void setVariable(Variable value);

	/**
	 * Returns the value of the '<em><b>Predicate</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Predicate</em>' containment reference.
	 * @see #setPredicate(Expression)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getCollectionCount_Predicate()
	 * @model containment="true"
	 * @generated
	 */
	Expression getPredicate();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.CollectionCount#getPredicate <em>Predicate</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Predicate</em>' containment reference.
	 * @see #getPredicate()
	 * @generated
	 */
	void setPredicate(Expression value);

} // CollectionCount
