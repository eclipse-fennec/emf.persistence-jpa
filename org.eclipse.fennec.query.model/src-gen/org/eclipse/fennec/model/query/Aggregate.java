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
package org.eclipse.fennec.model.query;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.fennec.model.expression.PropertyPath;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Aggregate</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * One aggregate output. path is optional for COUNT (count of group members).
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.query.Aggregate#getPath <em>Path</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.Aggregate#getMethod <em>Method</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.Aggregate#getAlias <em>Alias</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.query.QueryPackage#getAggregate()
 * @model
 * @generated
 */
@ProviderType
public interface Aggregate extends EObject {
	/**
	 * Returns the value of the '<em><b>Path</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Path</em>' containment reference.
	 * @see #setPath(PropertyPath)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getAggregate_Path()
	 * @model containment="true"
	 * @generated
	 */
	PropertyPath getPath();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.Aggregate#getPath <em>Path</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Path</em>' containment reference.
	 * @see #getPath()
	 * @generated
	 */
	void setPath(PropertyPath value);

	/**
	 * Returns the value of the '<em><b>Method</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.query.AggregateMethod}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Method</em>' attribute.
	 * @see org.eclipse.fennec.model.query.AggregateMethod
	 * @see #setMethod(AggregateMethod)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getAggregate_Method()
	 * @model required="true"
	 * @generated
	 */
	AggregateMethod getMethod();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.Aggregate#getMethod <em>Method</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Method</em>' attribute.
	 * @see org.eclipse.fennec.model.query.AggregateMethod
	 * @see #getMethod()
	 * @generated
	 */
	void setMethod(AggregateMethod value);

	/**
	 * Returns the value of the '<em><b>Alias</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Alias</em>' attribute.
	 * @see #setAlias(String)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getAggregate_Alias()
	 * @model required="true"
	 * @generated
	 */
	String getAlias();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.Aggregate#getAlias <em>Alias</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Alias</em>' attribute.
	 * @see #getAlias()
	 * @generated
	 */
	void setAlias(String value);

} // Aggregate
