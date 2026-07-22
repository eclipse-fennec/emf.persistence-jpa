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

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.model.utilities.Converter;
import org.eclipse.fennec.model.utilities.UtilitiesPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Converter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.utilities.impl.ConverterImpl#getConverterId <em>Converter Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.impl.ConverterImpl#getFromType <em>From Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.impl.ConverterImpl#getToType <em>To Type</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ConverterImpl extends MinimalEObjectImpl.Container implements Converter {
	/**
	 * The default value of the '{@link #getConverterId() <em>Converter Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConverterId()
	 * @generated
	 * @ordered
	 */
	protected static final String CONVERTER_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getConverterId() <em>Converter Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConverterId()
	 * @generated
	 * @ordered
	 */
	protected String converterId = CONVERTER_ID_EDEFAULT;

	/**
	 * The cached value of the '{@link #getFromType() <em>From Type</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFromType()
	 * @generated
	 * @ordered
	 */
	protected EClassifier fromType;

	/**
	 * The cached value of the '{@link #getToType() <em>To Type</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getToType()
	 * @generated
	 * @ordered
	 */
	protected EClassifier toType;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ConverterImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return UtilitiesPackage.Literals.CONVERTER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getConverterId() {
		return converterId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setConverterId(String newConverterId) {
		String oldConverterId = converterId;
		converterId = newConverterId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UtilitiesPackage.CONVERTER__CONVERTER_ID, oldConverterId, converterId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClassifier getFromType() {
		if (fromType != null && fromType.eIsProxy()) {
			InternalEObject oldFromType = (InternalEObject)fromType;
			fromType = (EClassifier)eResolveProxy(oldFromType);
			if (fromType != oldFromType) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, UtilitiesPackage.CONVERTER__FROM_TYPE, oldFromType, fromType));
			}
		}
		return fromType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClassifier basicGetFromType() {
		return fromType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setFromType(EClassifier newFromType) {
		EClassifier oldFromType = fromType;
		fromType = newFromType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UtilitiesPackage.CONVERTER__FROM_TYPE, oldFromType, fromType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClassifier getToType() {
		if (toType != null && toType.eIsProxy()) {
			InternalEObject oldToType = (InternalEObject)toType;
			toType = (EClassifier)eResolveProxy(oldToType);
			if (toType != oldToType) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, UtilitiesPackage.CONVERTER__TO_TYPE, oldToType, toType));
			}
		}
		return toType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClassifier basicGetToType() {
		return toType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setToType(EClassifier newToType) {
		EClassifier oldToType = toType;
		toType = newToType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UtilitiesPackage.CONVERTER__TO_TYPE, oldToType, toType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case UtilitiesPackage.CONVERTER__CONVERTER_ID:
				return getConverterId();
			case UtilitiesPackage.CONVERTER__FROM_TYPE:
				if (resolve) return getFromType();
				return basicGetFromType();
			case UtilitiesPackage.CONVERTER__TO_TYPE:
				if (resolve) return getToType();
				return basicGetToType();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case UtilitiesPackage.CONVERTER__CONVERTER_ID:
				setConverterId((String)newValue);
				return;
			case UtilitiesPackage.CONVERTER__FROM_TYPE:
				setFromType((EClassifier)newValue);
				return;
			case UtilitiesPackage.CONVERTER__TO_TYPE:
				setToType((EClassifier)newValue);
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
			case UtilitiesPackage.CONVERTER__CONVERTER_ID:
				setConverterId(CONVERTER_ID_EDEFAULT);
				return;
			case UtilitiesPackage.CONVERTER__FROM_TYPE:
				setFromType((EClassifier)null);
				return;
			case UtilitiesPackage.CONVERTER__TO_TYPE:
				setToType((EClassifier)null);
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
			case UtilitiesPackage.CONVERTER__CONVERTER_ID:
				return CONVERTER_ID_EDEFAULT == null ? converterId != null : !CONVERTER_ID_EDEFAULT.equals(converterId);
			case UtilitiesPackage.CONVERTER__FROM_TYPE:
				return fromType != null;
			case UtilitiesPackage.CONVERTER__TO_TYPE:
				return toType != null;
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
		result.append(" (converterId: ");
		result.append(converterId);
		result.append(')');
		return result.toString();
	}

} //ConverterImpl
