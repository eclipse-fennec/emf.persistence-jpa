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
package org.eclipse.fennec.model.command.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.model.command.CommandPackage;
import org.eclipse.fennec.model.command.UpdateEntry;
import org.eclipse.fennec.model.command.UpdateKind;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Update Entry</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.command.impl.UpdateEntryImpl#getKind <em>Kind</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.command.impl.UpdateEntryImpl#getFeature <em>Feature</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.command.impl.UpdateEntryImpl#getIndex <em>Index</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.command.impl.UpdateEntryImpl#getToIndex <em>To Index</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.command.impl.UpdateEntryImpl#getValue <em>Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.command.impl.UpdateEntryImpl#getOldValue <em>Old Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class UpdateEntryImpl extends MinimalEObjectImpl.Container implements UpdateEntry {
	/**
	 * The default value of the '{@link #getKind() <em>Kind</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getKind()
	 * @generated
	 * @ordered
	 */
	protected static final UpdateKind KIND_EDEFAULT = UpdateKind.SET;

	/**
	 * The cached value of the '{@link #getKind() <em>Kind</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getKind()
	 * @generated
	 * @ordered
	 */
	protected UpdateKind kind = KIND_EDEFAULT;

	/**
	 * The cached value of the '{@link #getFeature() <em>Feature</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFeature()
	 * @generated
	 * @ordered
	 */
	protected EStructuralFeature feature;

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
	 * The default value of the '{@link #getToIndex() <em>To Index</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getToIndex()
	 * @generated
	 * @ordered
	 */
	protected static final int TO_INDEX_EDEFAULT = -1;

	/**
	 * The cached value of the '{@link #getToIndex() <em>To Index</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getToIndex()
	 * @generated
	 * @ordered
	 */
	protected int toIndex = TO_INDEX_EDEFAULT;

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
	 * The default value of the '{@link #getOldValue() <em>Old Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOldValue()
	 * @generated
	 * @ordered
	 */
	protected static final String OLD_VALUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getOldValue() <em>Old Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOldValue()
	 * @generated
	 * @ordered
	 */
	protected String oldValue = OLD_VALUE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected UpdateEntryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CommandPackage.Literals.UPDATE_ENTRY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public UpdateKind getKind() {
		return kind;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setKind(UpdateKind newKind) {
		UpdateKind oldKind = kind;
		kind = newKind == null ? KIND_EDEFAULT : newKind;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CommandPackage.UPDATE_ENTRY__KIND, oldKind, kind));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EStructuralFeature getFeature() {
		if (feature != null && feature.eIsProxy()) {
			InternalEObject oldFeature = (InternalEObject)feature;
			feature = (EStructuralFeature)eResolveProxy(oldFeature);
			if (feature != oldFeature) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, CommandPackage.UPDATE_ENTRY__FEATURE, oldFeature, feature));
			}
		}
		return feature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EStructuralFeature basicGetFeature() {
		return feature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setFeature(EStructuralFeature newFeature) {
		EStructuralFeature oldFeature = feature;
		feature = newFeature;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CommandPackage.UPDATE_ENTRY__FEATURE, oldFeature, feature));
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
			eNotify(new ENotificationImpl(this, Notification.SET, CommandPackage.UPDATE_ENTRY__INDEX, oldIndex, index));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getToIndex() {
		return toIndex;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setToIndex(int newToIndex) {
		int oldToIndex = toIndex;
		toIndex = newToIndex;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CommandPackage.UPDATE_ENTRY__TO_INDEX, oldToIndex, toIndex));
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
			eNotify(new ENotificationImpl(this, Notification.SET, CommandPackage.UPDATE_ENTRY__VALUE, oldValue, value));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getOldValue() {
		return oldValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setOldValue(String newOldValue) {
		String oldOldValue = oldValue;
		oldValue = newOldValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CommandPackage.UPDATE_ENTRY__OLD_VALUE, oldOldValue, oldValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CommandPackage.UPDATE_ENTRY__KIND:
				return getKind();
			case CommandPackage.UPDATE_ENTRY__FEATURE:
				if (resolve) return getFeature();
				return basicGetFeature();
			case CommandPackage.UPDATE_ENTRY__INDEX:
				return getIndex();
			case CommandPackage.UPDATE_ENTRY__TO_INDEX:
				return getToIndex();
			case CommandPackage.UPDATE_ENTRY__VALUE:
				return getValue();
			case CommandPackage.UPDATE_ENTRY__OLD_VALUE:
				return getOldValue();
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
			case CommandPackage.UPDATE_ENTRY__KIND:
				setKind((UpdateKind)newValue);
				return;
			case CommandPackage.UPDATE_ENTRY__FEATURE:
				setFeature((EStructuralFeature)newValue);
				return;
			case CommandPackage.UPDATE_ENTRY__INDEX:
				setIndex((Integer)newValue);
				return;
			case CommandPackage.UPDATE_ENTRY__TO_INDEX:
				setToIndex((Integer)newValue);
				return;
			case CommandPackage.UPDATE_ENTRY__VALUE:
				setValue((String)newValue);
				return;
			case CommandPackage.UPDATE_ENTRY__OLD_VALUE:
				setOldValue((String)newValue);
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
			case CommandPackage.UPDATE_ENTRY__KIND:
				setKind(KIND_EDEFAULT);
				return;
			case CommandPackage.UPDATE_ENTRY__FEATURE:
				setFeature((EStructuralFeature)null);
				return;
			case CommandPackage.UPDATE_ENTRY__INDEX:
				setIndex(INDEX_EDEFAULT);
				return;
			case CommandPackage.UPDATE_ENTRY__TO_INDEX:
				setToIndex(TO_INDEX_EDEFAULT);
				return;
			case CommandPackage.UPDATE_ENTRY__VALUE:
				setValue(VALUE_EDEFAULT);
				return;
			case CommandPackage.UPDATE_ENTRY__OLD_VALUE:
				setOldValue(OLD_VALUE_EDEFAULT);
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
			case CommandPackage.UPDATE_ENTRY__KIND:
				return kind != KIND_EDEFAULT;
			case CommandPackage.UPDATE_ENTRY__FEATURE:
				return feature != null;
			case CommandPackage.UPDATE_ENTRY__INDEX:
				return index != INDEX_EDEFAULT;
			case CommandPackage.UPDATE_ENTRY__TO_INDEX:
				return toIndex != TO_INDEX_EDEFAULT;
			case CommandPackage.UPDATE_ENTRY__VALUE:
				return VALUE_EDEFAULT == null ? value != null : !VALUE_EDEFAULT.equals(value);
			case CommandPackage.UPDATE_ENTRY__OLD_VALUE:
				return OLD_VALUE_EDEFAULT == null ? oldValue != null : !OLD_VALUE_EDEFAULT.equals(oldValue);
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
		result.append(" (kind: ");
		result.append(kind);
		result.append(", index: ");
		result.append(index);
		result.append(", toIndex: ");
		result.append(toIndex);
		result.append(", value: ");
		result.append(value);
		result.append(", oldValue: ");
		result.append(oldValue);
		result.append(')');
		return result.toString();
	}

} //UpdateEntryImpl
