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
package org.eclipse.fennec.persistence.eorm.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.persistence.eorm.EAccessor;
import org.eclipse.fennec.persistence.eorm.EORMElement;
import org.eclipse.fennec.persistence.eorm.EORMPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Element</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EORMElementImpl#getAccessibleObject <em>Accessible Object</em>}</li>
 * </ul>
 *
 * @generated
 */
public class EORMElementImpl extends MinimalEObjectImpl.Container implements EORMElement {
	/**
	 * The cached value of the '{@link #getAccessibleObject() <em>Accessible Object</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAccessibleObject()
	 * @generated
	 * @ordered
	 */
	protected EAccessor accessibleObject;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EORMElementImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EORMPackage.eINSTANCE.getEORMElement();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAccessor getAccessibleObject() {
		return accessibleObject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetAccessibleObject(EAccessor newAccessibleObject, NotificationChain msgs) {
		EAccessor oldAccessibleObject = accessibleObject;
		accessibleObject = newAccessibleObject;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.EORM_ELEMENT__ACCESSIBLE_OBJECT, oldAccessibleObject, newAccessibleObject);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAccessibleObject(EAccessor newAccessibleObject) {
		if (newAccessibleObject != accessibleObject) {
			NotificationChain msgs = null;
			if (accessibleObject != null)
				msgs = ((InternalEObject)accessibleObject).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.EORM_ELEMENT__ACCESSIBLE_OBJECT, null, msgs);
			if (newAccessibleObject != null)
				msgs = ((InternalEObject)newAccessibleObject).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.EORM_ELEMENT__ACCESSIBLE_OBJECT, null, msgs);
			msgs = basicSetAccessibleObject(newAccessibleObject, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.EORM_ELEMENT__ACCESSIBLE_OBJECT, newAccessibleObject, newAccessibleObject));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EORMPackage.EORM_ELEMENT__ACCESSIBLE_OBJECT:
				return basicSetAccessibleObject(null, msgs);
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
			case EORMPackage.EORM_ELEMENT__ACCESSIBLE_OBJECT:
				return getAccessibleObject();
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
			case EORMPackage.EORM_ELEMENT__ACCESSIBLE_OBJECT:
				setAccessibleObject((EAccessor)newValue);
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
			case EORMPackage.EORM_ELEMENT__ACCESSIBLE_OBJECT:
				setAccessibleObject((EAccessor)null);
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
			case EORMPackage.EORM_ELEMENT__ACCESSIBLE_OBJECT:
				return accessibleObject != null;
		}
		return super.eIsSet(featureID);
	}

} //EORMElementImpl
