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
package org.eclipse.fennec.model.collection.impl;

import java.lang.reflect.InvocationTargetException;

import java.util.Iterator;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.model.collection.CollectionPackage;
import org.eclipse.fennec.model.collection.EIterable;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EIterable</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.collection.impl.EIterableImpl#getDelegate <em>Delegate</em>}</li>
 * </ul>
 *
 * @generated
 */
public class EIterableImpl extends MinimalEObjectImpl.Container implements EIterable {
	/**
	 * The cached value of the '{@link #getDelegate() <em>Delegate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDelegate()
	 * @generated
	 * @ordered
	 */
	protected Iterable<EObject> delegate;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EIterableImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CollectionPackage.Literals.EITERABLE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Iterable<EObject> getDelegate() {
		return delegate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDelegate(Iterable<EObject> newDelegate) {
		Iterable<EObject> oldDelegate = delegate;
		delegate = newDelegate;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CollectionPackage.EITERABLE__DELEGATE, oldDelegate, delegate));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Iterator<EObject> iterator() {
		return getDelegate() != null ? getDelegate().iterator() : null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CollectionPackage.EITERABLE__DELEGATE:
				return getDelegate();
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
			case CollectionPackage.EITERABLE__DELEGATE:
				setDelegate((Iterable<EObject>)newValue);
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
			case CollectionPackage.EITERABLE__DELEGATE:
				setDelegate((Iterable<EObject>)null);
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
			case CollectionPackage.EITERABLE__DELEGATE:
				return delegate != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		switch (operationID) {
			case CollectionPackage.EITERABLE___ITERATOR:
				return iterator();
		}
		return super.eInvoke(operationID, arguments);
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
		result.append(" (delegate: ");
		result.append(delegate);
		result.append(')');
		return result.toString();
	}

} //EIterableImpl
