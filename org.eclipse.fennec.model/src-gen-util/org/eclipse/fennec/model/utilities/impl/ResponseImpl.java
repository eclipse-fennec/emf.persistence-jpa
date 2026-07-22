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
package org.eclipse.fennec.model.utilities.impl;

import java.util.Collection;
import java.util.Date;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.model.utilities.Response;
import org.eclipse.fennec.model.utilities.UtilitiesPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Response</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.utilities.impl.ResponseImpl#getTimestamp <em>Timestamp</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.impl.ResponseImpl#getResultSize <em>Result Size</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.impl.ResponseImpl#getResponseCode <em>Response Code</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.impl.ResponseImpl#getResponseMessage <em>Response Message</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.impl.ResponseImpl#getData <em>Data</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ResponseImpl extends MinimalEObjectImpl.Container implements Response {
	/**
	 * The default value of the '{@link #getTimestamp() <em>Timestamp</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTimestamp()
	 * @generated
	 * @ordered
	 */
	protected static final Date TIMESTAMP_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTimestamp() <em>Timestamp</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTimestamp()
	 * @generated
	 * @ordered
	 */
	protected Date timestamp = TIMESTAMP_EDEFAULT;

	/**
	 * The default value of the '{@link #getResultSize() <em>Result Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResultSize()
	 * @generated
	 * @ordered
	 */
	protected static final int RESULT_SIZE_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getResultSize() <em>Result Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResultSize()
	 * @generated
	 * @ordered
	 */
	protected int resultSize = RESULT_SIZE_EDEFAULT;

	/**
	 * The default value of the '{@link #getResponseCode() <em>Response Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResponseCode()
	 * @generated
	 * @ordered
	 */
	protected static final String RESPONSE_CODE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getResponseCode() <em>Response Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResponseCode()
	 * @generated
	 * @ordered
	 */
	protected String responseCode = RESPONSE_CODE_EDEFAULT;

	/**
	 * The default value of the '{@link #getResponseMessage() <em>Response Message</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResponseMessage()
	 * @generated
	 * @ordered
	 */
	protected static final String RESPONSE_MESSAGE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getResponseMessage() <em>Response Message</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResponseMessage()
	 * @generated
	 * @ordered
	 */
	protected String responseMessage = RESPONSE_MESSAGE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getData() <em>Data</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getData()
	 * @generated
	 * @ordered
	 */
	protected EList<EObject> data;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ResponseImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return UtilitiesPackage.Literals.RESPONSE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTimestamp(Date newTimestamp) {
		Date oldTimestamp = timestamp;
		timestamp = newTimestamp;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UtilitiesPackage.RESPONSE__TIMESTAMP, oldTimestamp, timestamp));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getResultSize() {
		return resultSize;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setResultSize(int newResultSize) {
		int oldResultSize = resultSize;
		resultSize = newResultSize;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UtilitiesPackage.RESPONSE__RESULT_SIZE, oldResultSize, resultSize));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getResponseCode() {
		return responseCode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setResponseCode(String newResponseCode) {
		String oldResponseCode = responseCode;
		responseCode = newResponseCode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UtilitiesPackage.RESPONSE__RESPONSE_CODE, oldResponseCode, responseCode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getResponseMessage() {
		return responseMessage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setResponseMessage(String newResponseMessage) {
		String oldResponseMessage = responseMessage;
		responseMessage = newResponseMessage;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UtilitiesPackage.RESPONSE__RESPONSE_MESSAGE, oldResponseMessage, responseMessage));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<EObject> getData() {
		if (data == null) {
			data = new EObjectContainmentEList<EObject>(EObject.class, this, UtilitiesPackage.RESPONSE__DATA);
		}
		return data;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case UtilitiesPackage.RESPONSE__DATA:
				return ((InternalEList<?>)getData()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case UtilitiesPackage.RESPONSE__TIMESTAMP:
				return getTimestamp();
			case UtilitiesPackage.RESPONSE__RESULT_SIZE:
				return getResultSize();
			case UtilitiesPackage.RESPONSE__RESPONSE_CODE:
				return getResponseCode();
			case UtilitiesPackage.RESPONSE__RESPONSE_MESSAGE:
				return getResponseMessage();
			case UtilitiesPackage.RESPONSE__DATA:
				return getData();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case UtilitiesPackage.RESPONSE__TIMESTAMP:
				setTimestamp((Date)newValue);
				return;
			case UtilitiesPackage.RESPONSE__RESULT_SIZE:
				setResultSize((Integer)newValue);
				return;
			case UtilitiesPackage.RESPONSE__RESPONSE_CODE:
				setResponseCode((String)newValue);
				return;
			case UtilitiesPackage.RESPONSE__RESPONSE_MESSAGE:
				setResponseMessage((String)newValue);
				return;
			case UtilitiesPackage.RESPONSE__DATA:
				getData().clear();
				getData().addAll((Collection<? extends EObject>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case UtilitiesPackage.RESPONSE__TIMESTAMP:
				setTimestamp(TIMESTAMP_EDEFAULT);
				return;
			case UtilitiesPackage.RESPONSE__RESULT_SIZE:
				setResultSize(RESULT_SIZE_EDEFAULT);
				return;
			case UtilitiesPackage.RESPONSE__RESPONSE_CODE:
				setResponseCode(RESPONSE_CODE_EDEFAULT);
				return;
			case UtilitiesPackage.RESPONSE__RESPONSE_MESSAGE:
				setResponseMessage(RESPONSE_MESSAGE_EDEFAULT);
				return;
			case UtilitiesPackage.RESPONSE__DATA:
				getData().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case UtilitiesPackage.RESPONSE__TIMESTAMP:
				return TIMESTAMP_EDEFAULT == null ? timestamp != null : !TIMESTAMP_EDEFAULT.equals(timestamp);
			case UtilitiesPackage.RESPONSE__RESULT_SIZE:
				return resultSize != RESULT_SIZE_EDEFAULT;
			case UtilitiesPackage.RESPONSE__RESPONSE_CODE:
				return RESPONSE_CODE_EDEFAULT == null ? responseCode != null : !RESPONSE_CODE_EDEFAULT.equals(responseCode);
			case UtilitiesPackage.RESPONSE__RESPONSE_MESSAGE:
				return RESPONSE_MESSAGE_EDEFAULT == null ? responseMessage != null : !RESPONSE_MESSAGE_EDEFAULT.equals(responseMessage);
			case UtilitiesPackage.RESPONSE__DATA:
				return data != null && !data.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (timestamp: ");
		result.append(timestamp);
		result.append(", resultSize: ");
		result.append(resultSize);
		result.append(", responseCode: ");
		result.append(responseCode);
		result.append(", responseMessage: ");
		result.append(responseMessage);
		result.append(')');
		return result.toString();
	}

} //ResponseImpl
