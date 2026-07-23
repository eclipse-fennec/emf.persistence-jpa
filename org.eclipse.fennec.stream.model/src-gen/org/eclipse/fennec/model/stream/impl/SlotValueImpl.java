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
package org.eclipse.fennec.model.stream.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;

import org.eclipse.fennec.model.stream.SlotValue;
import org.eclipse.fennec.model.stream.StreamPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Slot Value</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.SlotValueImpl#getFeatureId <em>Feature Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.SlotValueImpl#getIndex <em>Index</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.SlotValueImpl#getKey <em>Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.SlotValueImpl#getCoords <em>Coords</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.SlotValueImpl#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SlotValueImpl extends MinimalEObjectImpl.Container implements SlotValue {
	/**
	 * The default value of the '{@link #getFeatureId() <em>Feature Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFeatureId()
	 * @generated
	 * @ordered
	 */
	protected static final int FEATURE_ID_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getFeatureId() <em>Feature Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFeatureId()
	 * @generated
	 * @ordered
	 */
	protected int featureId = FEATURE_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getIndex() <em>Index</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIndex()
	 * @generated
	 * @ordered
	 */
	protected static final int INDEX_EDEFAULT = -1;

	/**
	 * The cached value of the '{@link #getIndex() <em>Index</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIndex()
	 * @generated
	 * @ordered
	 */
	protected int index = INDEX_EDEFAULT;

	/**
	 * The default value of the '{@link #getKey() <em>Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getKey()
	 * @generated
	 * @ordered
	 */
	protected static final String KEY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getKey() <em>Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getKey()
	 * @generated
	 * @ordered
	 */
	protected String key = KEY_EDEFAULT;

	/**
	 * The cached value of the '{@link #getCoords() <em>Coords</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCoords()
	 * @generated
	 * @ordered
	 */
	protected EList<Integer> coords;

	/**
	 * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected static final String VALUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getValue() <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected String value = VALUE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SlotValueImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return StreamPackage.Literals.SLOT_VALUE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getFeatureId() {
		return featureId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setFeatureId(int newFeatureId) {
		int oldFeatureId = featureId;
		featureId = newFeatureId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.SLOT_VALUE__FEATURE_ID, oldFeatureId, featureId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getIndex() {
		return index;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIndex(int newIndex) {
		int oldIndex = index;
		index = newIndex;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.SLOT_VALUE__INDEX, oldIndex, index));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getKey() {
		return key;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setKey(String newKey) {
		String oldKey = key;
		key = newKey;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.SLOT_VALUE__KEY, oldKey, key));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Integer> getCoords() {
		if (coords == null) {
			coords = new EDataTypeUniqueEList<Integer>(Integer.class, this, StreamPackage.SLOT_VALUE__COORDS);
		}
		return coords;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getValue() {
		return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setValue(String newValue) {
		String oldValue = value;
		value = newValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.SLOT_VALUE__VALUE, oldValue, value));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case StreamPackage.SLOT_VALUE__FEATURE_ID:
				return getFeatureId();
			case StreamPackage.SLOT_VALUE__INDEX:
				return getIndex();
			case StreamPackage.SLOT_VALUE__KEY:
				return getKey();
			case StreamPackage.SLOT_VALUE__COORDS:
				return getCoords();
			case StreamPackage.SLOT_VALUE__VALUE:
				return getValue();
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
			case StreamPackage.SLOT_VALUE__FEATURE_ID:
				setFeatureId((Integer)newValue);
				return;
			case StreamPackage.SLOT_VALUE__INDEX:
				setIndex((Integer)newValue);
				return;
			case StreamPackage.SLOT_VALUE__KEY:
				setKey((String)newValue);
				return;
			case StreamPackage.SLOT_VALUE__COORDS:
				getCoords().clear();
				getCoords().addAll((Collection<? extends Integer>)newValue);
				return;
			case StreamPackage.SLOT_VALUE__VALUE:
				setValue((String)newValue);
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
			case StreamPackage.SLOT_VALUE__FEATURE_ID:
				setFeatureId(FEATURE_ID_EDEFAULT);
				return;
			case StreamPackage.SLOT_VALUE__INDEX:
				setIndex(INDEX_EDEFAULT);
				return;
			case StreamPackage.SLOT_VALUE__KEY:
				setKey(KEY_EDEFAULT);
				return;
			case StreamPackage.SLOT_VALUE__COORDS:
				getCoords().clear();
				return;
			case StreamPackage.SLOT_VALUE__VALUE:
				setValue(VALUE_EDEFAULT);
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
			case StreamPackage.SLOT_VALUE__FEATURE_ID:
				return featureId != FEATURE_ID_EDEFAULT;
			case StreamPackage.SLOT_VALUE__INDEX:
				return index != INDEX_EDEFAULT;
			case StreamPackage.SLOT_VALUE__KEY:
				return KEY_EDEFAULT == null ? key != null : !KEY_EDEFAULT.equals(key);
			case StreamPackage.SLOT_VALUE__COORDS:
				return coords != null && !coords.isEmpty();
			case StreamPackage.SLOT_VALUE__VALUE:
				return VALUE_EDEFAULT == null ? value != null : !VALUE_EDEFAULT.equals(value);
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
		result.append(" (featureId: ");
		result.append(featureId);
		result.append(", index: ");
		result.append(index);
		result.append(", key: ");
		result.append(key);
		result.append(", coords: ");
		result.append(coords);
		result.append(", value: ");
		result.append(value);
		result.append(')');
		return result.toString();
	}

} //SlotValueImpl
