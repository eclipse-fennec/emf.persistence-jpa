/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 * 
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 */
package org.eclipse.fennec.model.utilities;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Sort</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Sort definition
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.utilities.Sort#getIndex <em>Index</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.Sort#getField <em>Field</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.Sort#getType <em>Type</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.utilities.UtilitiesPackage#getSort()
 * @model
 * @generated
 */
@ProviderType
public interface Sort extends EObject {
	/**
	 * Returns the value of the '<em><b>Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Index for the right ordering
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Index</em>' attribute.
	 * @see #setIndex(int)
	 * @see org.eclipse.fennec.model.utilities.UtilitiesPackage#getSort_Index()
	 * @model required="true"
	 * @generated
	 */
	int getIndex();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.utilities.Sort#getIndex <em>Index</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Index</em>' attribute.
	 * @see #getIndex()
	 * @generated
	 */
	void setIndex(int value);

	/**
	 * Returns the value of the '<em><b>Field</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The field name to sort against
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Field</em>' attribute.
	 * @see #setField(String)
	 * @see org.eclipse.fennec.model.utilities.UtilitiesPackage#getSort_Field()
	 * @model required="true"
	 * @generated
	 */
	String getField();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.utilities.Sort#getField <em>Field</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Field</em>' attribute.
	 * @see #getField()
	 * @generated
	 */
	void setField(String value);

	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.utilities.SortType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see org.eclipse.fennec.model.utilities.SortType
	 * @see #setType(SortType)
	 * @see org.eclipse.fennec.model.utilities.UtilitiesPackage#getSort_Type()
	 * @model
	 * @generated
	 */
	SortType getType();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.utilities.Sort#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see org.eclipse.fennec.model.utilities.SortType
	 * @see #getType()
	 * @generated
	 */
	void setType(SortType value);

} // Sort
