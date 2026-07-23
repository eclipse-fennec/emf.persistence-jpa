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
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.model.stream.ChangeEntry;
import org.eclipse.fennec.model.stream.DeltaKind;
import org.eclipse.fennec.model.stream.SlotValue;
import org.eclipse.fennec.model.stream.StreamPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Change Entry</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ChangeEntryImpl#getObjectId <em>Object Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ChangeEntryImpl#getClassId <em>Class Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ChangeEntryImpl#getKind <em>Kind</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ChangeEntryImpl#getFeatureId <em>Feature Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ChangeEntryImpl#getIndex <em>Index</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ChangeEntryImpl#getToIndex <em>To Index</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ChangeEntryImpl#getKey <em>Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ChangeEntryImpl#getCoords <em>Coords</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ChangeEntryImpl#getValueOld <em>Value Old</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ChangeEntryImpl#getValueNew <em>Value New</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ChangeEntryImpl#getState <em>State</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ChangeEntryImpl extends MinimalEObjectImpl.Container implements ChangeEntry {
	/**
	 * The default value of the '{@link #getObjectId() <em>Object Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getObjectId()
	 * @generated
	 * @ordered
	 */
	protected static final String OBJECT_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getObjectId() <em>Object Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getObjectId()
	 * @generated
	 * @ordered
	 */
	protected String objectId = OBJECT_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getClassId() <em>Class Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getClassId()
	 * @generated
	 * @ordered
	 */
	protected static final int CLASS_ID_EDEFAULT = -1;

	/**
	 * The cached value of the '{@link #getClassId() <em>Class Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getClassId()
	 * @generated
	 * @ordered
	 */
	protected int classId = CLASS_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getKind() <em>Kind</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getKind()
	 * @generated
	 * @ordered
	 */
	protected static final DeltaKind KIND_EDEFAULT = DeltaKind.CREATE;

	/**
	 * The cached value of the '{@link #getKind() <em>Kind</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getKind()
	 * @generated
	 * @ordered
	 */
	protected DeltaKind kind = KIND_EDEFAULT;

	/**
	 * The default value of the '{@link #getFeatureId() <em>Feature Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFeatureId()
	 * @generated
	 * @ordered
	 */
	protected static final int FEATURE_ID_EDEFAULT = -1;

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
	 * The default value of the '{@link #getValueOld() <em>Value Old</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValueOld()
	 * @generated
	 * @ordered
	 */
	protected static final String VALUE_OLD_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getValueOld() <em>Value Old</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValueOld()
	 * @generated
	 * @ordered
	 */
	protected String valueOld = VALUE_OLD_EDEFAULT;

	/**
	 * The default value of the '{@link #getValueNew() <em>Value New</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValueNew()
	 * @generated
	 * @ordered
	 */
	protected static final String VALUE_NEW_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getValueNew() <em>Value New</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValueNew()
	 * @generated
	 * @ordered
	 */
	protected String valueNew = VALUE_NEW_EDEFAULT;

	/**
	 * The cached value of the '{@link #getState() <em>State</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getState()
	 * @generated
	 * @ordered
	 */
	protected EList<SlotValue> state;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ChangeEntryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return StreamPackage.Literals.CHANGE_ENTRY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getObjectId() {
		return objectId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setObjectId(String newObjectId) {
		String oldObjectId = objectId;
		objectId = newObjectId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.CHANGE_ENTRY__OBJECT_ID, oldObjectId, objectId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getClassId() {
		return classId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setClassId(int newClassId) {
		int oldClassId = classId;
		classId = newClassId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.CHANGE_ENTRY__CLASS_ID, oldClassId, classId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DeltaKind getKind() {
		return kind;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setKind(DeltaKind newKind) {
		DeltaKind oldKind = kind;
		kind = newKind == null ? KIND_EDEFAULT : newKind;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.CHANGE_ENTRY__KIND, oldKind, kind));
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
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.CHANGE_ENTRY__FEATURE_ID, oldFeatureId, featureId));
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
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.CHANGE_ENTRY__INDEX, oldIndex, index));
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
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.CHANGE_ENTRY__TO_INDEX, oldToIndex, toIndex));
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
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.CHANGE_ENTRY__KEY, oldKey, key));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Integer> getCoords() {
		if (coords == null) {
			coords = new EDataTypeUniqueEList<Integer>(Integer.class, this, StreamPackage.CHANGE_ENTRY__COORDS);
		}
		return coords;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getValueOld() {
		return valueOld;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setValueOld(String newValueOld) {
		String oldValueOld = valueOld;
		valueOld = newValueOld;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.CHANGE_ENTRY__VALUE_OLD, oldValueOld, valueOld));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getValueNew() {
		return valueNew;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setValueNew(String newValueNew) {
		String oldValueNew = valueNew;
		valueNew = newValueNew;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.CHANGE_ENTRY__VALUE_NEW, oldValueNew, valueNew));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<SlotValue> getState() {
		if (state == null) {
			state = new EObjectContainmentEList<SlotValue>(SlotValue.class, this, StreamPackage.CHANGE_ENTRY__STATE);
		}
		return state;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case StreamPackage.CHANGE_ENTRY__STATE:
				return ((InternalEList<?>)getState()).basicRemove(otherEnd, msgs);
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
			case StreamPackage.CHANGE_ENTRY__OBJECT_ID:
				return getObjectId();
			case StreamPackage.CHANGE_ENTRY__CLASS_ID:
				return getClassId();
			case StreamPackage.CHANGE_ENTRY__KIND:
				return getKind();
			case StreamPackage.CHANGE_ENTRY__FEATURE_ID:
				return getFeatureId();
			case StreamPackage.CHANGE_ENTRY__INDEX:
				return getIndex();
			case StreamPackage.CHANGE_ENTRY__TO_INDEX:
				return getToIndex();
			case StreamPackage.CHANGE_ENTRY__KEY:
				return getKey();
			case StreamPackage.CHANGE_ENTRY__COORDS:
				return getCoords();
			case StreamPackage.CHANGE_ENTRY__VALUE_OLD:
				return getValueOld();
			case StreamPackage.CHANGE_ENTRY__VALUE_NEW:
				return getValueNew();
			case StreamPackage.CHANGE_ENTRY__STATE:
				return getState();
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
			case StreamPackage.CHANGE_ENTRY__OBJECT_ID:
				setObjectId((String)newValue);
				return;
			case StreamPackage.CHANGE_ENTRY__CLASS_ID:
				setClassId((Integer)newValue);
				return;
			case StreamPackage.CHANGE_ENTRY__KIND:
				setKind((DeltaKind)newValue);
				return;
			case StreamPackage.CHANGE_ENTRY__FEATURE_ID:
				setFeatureId((Integer)newValue);
				return;
			case StreamPackage.CHANGE_ENTRY__INDEX:
				setIndex((Integer)newValue);
				return;
			case StreamPackage.CHANGE_ENTRY__TO_INDEX:
				setToIndex((Integer)newValue);
				return;
			case StreamPackage.CHANGE_ENTRY__KEY:
				setKey((String)newValue);
				return;
			case StreamPackage.CHANGE_ENTRY__COORDS:
				getCoords().clear();
				getCoords().addAll((Collection<? extends Integer>)newValue);
				return;
			case StreamPackage.CHANGE_ENTRY__VALUE_OLD:
				setValueOld((String)newValue);
				return;
			case StreamPackage.CHANGE_ENTRY__VALUE_NEW:
				setValueNew((String)newValue);
				return;
			case StreamPackage.CHANGE_ENTRY__STATE:
				getState().clear();
				getState().addAll((Collection<? extends SlotValue>)newValue);
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
			case StreamPackage.CHANGE_ENTRY__OBJECT_ID:
				setObjectId(OBJECT_ID_EDEFAULT);
				return;
			case StreamPackage.CHANGE_ENTRY__CLASS_ID:
				setClassId(CLASS_ID_EDEFAULT);
				return;
			case StreamPackage.CHANGE_ENTRY__KIND:
				setKind(KIND_EDEFAULT);
				return;
			case StreamPackage.CHANGE_ENTRY__FEATURE_ID:
				setFeatureId(FEATURE_ID_EDEFAULT);
				return;
			case StreamPackage.CHANGE_ENTRY__INDEX:
				setIndex(INDEX_EDEFAULT);
				return;
			case StreamPackage.CHANGE_ENTRY__TO_INDEX:
				setToIndex(TO_INDEX_EDEFAULT);
				return;
			case StreamPackage.CHANGE_ENTRY__KEY:
				setKey(KEY_EDEFAULT);
				return;
			case StreamPackage.CHANGE_ENTRY__COORDS:
				getCoords().clear();
				return;
			case StreamPackage.CHANGE_ENTRY__VALUE_OLD:
				setValueOld(VALUE_OLD_EDEFAULT);
				return;
			case StreamPackage.CHANGE_ENTRY__VALUE_NEW:
				setValueNew(VALUE_NEW_EDEFAULT);
				return;
			case StreamPackage.CHANGE_ENTRY__STATE:
				getState().clear();
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
			case StreamPackage.CHANGE_ENTRY__OBJECT_ID:
				return OBJECT_ID_EDEFAULT == null ? objectId != null : !OBJECT_ID_EDEFAULT.equals(objectId);
			case StreamPackage.CHANGE_ENTRY__CLASS_ID:
				return classId != CLASS_ID_EDEFAULT;
			case StreamPackage.CHANGE_ENTRY__KIND:
				return kind != KIND_EDEFAULT;
			case StreamPackage.CHANGE_ENTRY__FEATURE_ID:
				return featureId != FEATURE_ID_EDEFAULT;
			case StreamPackage.CHANGE_ENTRY__INDEX:
				return index != INDEX_EDEFAULT;
			case StreamPackage.CHANGE_ENTRY__TO_INDEX:
				return toIndex != TO_INDEX_EDEFAULT;
			case StreamPackage.CHANGE_ENTRY__KEY:
				return KEY_EDEFAULT == null ? key != null : !KEY_EDEFAULT.equals(key);
			case StreamPackage.CHANGE_ENTRY__COORDS:
				return coords != null && !coords.isEmpty();
			case StreamPackage.CHANGE_ENTRY__VALUE_OLD:
				return VALUE_OLD_EDEFAULT == null ? valueOld != null : !VALUE_OLD_EDEFAULT.equals(valueOld);
			case StreamPackage.CHANGE_ENTRY__VALUE_NEW:
				return VALUE_NEW_EDEFAULT == null ? valueNew != null : !VALUE_NEW_EDEFAULT.equals(valueNew);
			case StreamPackage.CHANGE_ENTRY__STATE:
				return state != null && !state.isEmpty();
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
		result.append(" (objectId: ");
		result.append(objectId);
		result.append(", classId: ");
		result.append(classId);
		result.append(", kind: ");
		result.append(kind);
		result.append(", featureId: ");
		result.append(featureId);
		result.append(", index: ");
		result.append(index);
		result.append(", toIndex: ");
		result.append(toIndex);
		result.append(", key: ");
		result.append(key);
		result.append(", coords: ");
		result.append(coords);
		result.append(", valueOld: ");
		result.append(valueOld);
		result.append(", valueNew: ");
		result.append(valueNew);
		result.append(')');
		return result.toString();
	}

} //ChangeEntryImpl
