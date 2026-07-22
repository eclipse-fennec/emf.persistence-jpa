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

import java.util.Date;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Response</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Response wrapper object. Usually used in combination with the request object
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.utilities.Response#getTimestamp <em>Timestamp</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.Response#getResultSize <em>Result Size</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.Response#getResponseCode <em>Response Code</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.Response#getResponseMessage <em>Response Message</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.Response#getData <em>Data</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.utilities.UtilitiesPackage#getResponse()
 * @model
 * @generated
 */
@ProviderType
public interface Response extends EObject {
	/**
	 * Returns the value of the '<em><b>Timestamp</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Response timestamp
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Timestamp</em>' attribute.
	 * @see #setTimestamp(Date)
	 * @see org.eclipse.fennec.model.utilities.UtilitiesPackage#getResponse_Timestamp()
	 * @model
	 * @generated
	 */
	Date getTimestamp();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.utilities.Response#getTimestamp <em>Timestamp</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Timestamp</em>' attribute.
	 * @see #getTimestamp()
	 * @generated
	 */
	void setTimestamp(Date value);

	/**
	 * Returns the value of the '<em><b>Result Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Returns the whole query return size, if 'returnResultSize' was set to true in the request
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Result Size</em>' attribute.
	 * @see #setResultSize(int)
	 * @see org.eclipse.fennec.model.utilities.UtilitiesPackage#getResponse_ResultSize()
	 * @model
	 * @generated
	 */
	int getResultSize();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.utilities.Response#getResultSize <em>Result Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Result Size</em>' attribute.
	 * @see #getResultSize()
	 * @generated
	 */
	void setResultSize(int value);

	/**
	 * Returns the value of the '<em><b>Response Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Can be used to return a response code, when working outside protocols like HTTP
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Response Code</em>' attribute.
	 * @see #setResponseCode(String)
	 * @see org.eclipse.fennec.model.utilities.UtilitiesPackage#getResponse_ResponseCode()
	 * @model
	 * @generated
	 */
	String getResponseCode();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.utilities.Response#getResponseCode <em>Response Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Response Code</em>' attribute.
	 * @see #getResponseCode()
	 * @generated
	 */
	void setResponseCode(String value);

	/**
	 * Returns the value of the '<em><b>Response Message</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Can be used for an error text
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Response Message</em>' attribute.
	 * @see #setResponseMessage(String)
	 * @see org.eclipse.fennec.model.utilities.UtilitiesPackage#getResponse_ResponseMessage()
	 * @model
	 * @generated
	 */
	String getResponseMessage();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.utilities.Response#getResponseMessage <em>Response Message</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Response Message</em>' attribute.
	 * @see #getResponseMessage()
	 * @generated
	 */
	void setResponseMessage(String value);

	/**
	 * Returns the value of the '<em><b>Data</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.EObject}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * General purpose data, depending on the request
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Data</em>' containment reference list.
	 * @see org.eclipse.fennec.model.utilities.UtilitiesPackage#getResponse_Data()
	 * @model containment="true"
	 * @generated
	 */
	EList<EObject> getData();

} // Response
