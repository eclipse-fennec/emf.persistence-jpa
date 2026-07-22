/*
 * Copyright (c) 2012 - 2026 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.model.query;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Is In Range</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.query.IsInRange#getStartValue <em>Start Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.IsInRange#getEndValue <em>End Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.IsInRange#isStartIncluded <em>Start Included</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.IsInRange#isEndIncluded <em>End Included</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.query.QueryPackage#getIsInRange()
 * @model
 * @generated
 */
@ProviderType
public interface IsInRange extends Comparator {
	/**
	 * Returns the value of the '<em><b>Start Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Start Value</em>' attribute.
	 * @see #setStartValue(String)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getIsInRange_StartValue()
	 * @model
	 * @generated
	 */
	String getStartValue();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.IsInRange#getStartValue <em>Start Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Start Value</em>' attribute.
	 * @see #getStartValue()
	 * @generated
	 */
	void setStartValue(String value);

	/**
	 * Returns the value of the '<em><b>End Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>End Value</em>' attribute.
	 * @see #setEndValue(String)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getIsInRange_EndValue()
	 * @model
	 * @generated
	 */
	String getEndValue();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.IsInRange#getEndValue <em>End Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>End Value</em>' attribute.
	 * @see #getEndValue()
	 * @generated
	 */
	void setEndValue(String value);

	/**
	 * Returns the value of the '<em><b>Start Included</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Start Included</em>' attribute.
	 * @see #setStartIncluded(boolean)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getIsInRange_StartIncluded()
	 * @model
	 * @generated
	 */
	boolean isStartIncluded();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.IsInRange#isStartIncluded <em>Start Included</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Start Included</em>' attribute.
	 * @see #isStartIncluded()
	 * @generated
	 */
	void setStartIncluded(boolean value);

	/**
	 * Returns the value of the '<em><b>End Included</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>End Included</em>' attribute.
	 * @see #setEndIncluded(boolean)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getIsInRange_EndIncluded()
	 * @model
	 * @generated
	 */
	boolean isEndIncluded();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.IsInRange#isEndIncluded <em>End Included</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>End Included</em>' attribute.
	 * @see #isEndIncluded()
	 * @generated
	 */
	void setEndIncluded(boolean value);

} // IsInRange
