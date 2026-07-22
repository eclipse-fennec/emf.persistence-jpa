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

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Filter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * The filter definition
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.utilities.Filter#getIndex <em>Index</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.Filter#getField <em>Field</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.Filter#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.utilities.UtilitiesPackage#getFilter()
 * @model
 * @generated
 */
@ProviderType
public interface Filter extends EObject {
	/**
	 * Returns the value of the '<em><b>Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Index for the right ordering
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Index</em>' attribute.
	 * @see #setIndex(int)
	 * @see org.eclipse.fennec.model.utilities.UtilitiesPackage#getFilter_Index()
	 * @model required="true"
	 * @generated
	 */
	int getIndex();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.utilities.Filter#getIndex <em>Index</em>}' attribute.
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
	 * @see org.eclipse.fennec.model.utilities.UtilitiesPackage#getFilter_Field()
	 * @model required="true"
	 * @generated
	 */
	String getField();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.utilities.Filter#getField <em>Field</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Field</em>' attribute.
	 * @see #getField()
	 * @generated
	 */
	void setField(String value);

	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The values to filter against
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Value</em>' attribute list.
	 * @see org.eclipse.fennec.model.utilities.UtilitiesPackage#getFilter_Value()
	 * @model required="true"
	 * @generated
	 */
	EList<String> getValue();

} // Filter
