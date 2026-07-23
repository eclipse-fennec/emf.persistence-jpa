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
 * A representation of the model object '<em><b>Order By</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * One ordering entry. For row-shaped queries the path must address an output key (alias).
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.query.OrderBy#getPath <em>Path</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.OrderBy#getDirection <em>Direction</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.query.QueryPackage#getOrderBy()
 * @model
 * @generated
 */
@ProviderType
public interface OrderBy extends EObject {
	/**
	 * Returns the value of the '<em><b>Path</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Path</em>' containment reference.
	 * @see #setPath(PropertyPath)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getOrderBy_Path()
	 * @model containment="true" required="true"
	 * @generated
	 */
	PropertyPath getPath();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.OrderBy#getPath <em>Path</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Path</em>' containment reference.
	 * @see #getPath()
	 * @generated
	 */
	void setPath(PropertyPath value);

	/**
	 * Returns the value of the '<em><b>Direction</b></em>' attribute.
	 * The default value is <code>"ASC"</code>.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.query.SortDirection}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Direction</em>' attribute.
	 * @see org.eclipse.fennec.model.query.SortDirection
	 * @see #setDirection(SortDirection)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getOrderBy_Direction()
	 * @model default="ASC" required="true"
	 * @generated
	 */
	SortDirection getDirection();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.OrderBy#getDirection <em>Direction</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Direction</em>' attribute.
	 * @see org.eclipse.fennec.model.query.SortDirection
	 * @see #getDirection()
	 * @generated
	 */
	void setDirection(SortDirection value);

} // OrderBy
