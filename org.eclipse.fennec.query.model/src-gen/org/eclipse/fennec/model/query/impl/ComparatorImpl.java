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
package org.eclipse.fennec.model.query.impl;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.fennec.model.query.Comparator;
import org.eclipse.fennec.model.query.QWhere;
import org.eclipse.fennec.model.query.QueryPackage;
import org.eclipse.fennec.model.query.SuitableType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Comparator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.query.impl.ComparatorImpl#getSuitableForType <em>Suitable For Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.ComparatorImpl#getWhere <em>Where</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class ComparatorImpl extends MinimalEObjectImpl.Container implements Comparator {
	/**
	 * The default value of the '{@link #getSuitableForType() <em>Suitable For Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSuitableForType()
	 * @generated
	 * @ordered
	 */
	protected static final SuitableType SUITABLE_FOR_TYPE_EDEFAULT = SuitableType.NUMERIC;

	/**
	 * The cached value of the '{@link #getSuitableForType() <em>Suitable For Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSuitableForType()
	 * @generated
	 * @ordered
	 */
	protected SuitableType suitableForType = SUITABLE_FOR_TYPE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ComparatorImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return QueryPackage.Literals.COMPARATOR;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SuitableType getSuitableForType() {
		return suitableForType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSuitableForType(SuitableType newSuitableForType) {
		SuitableType oldSuitableForType = suitableForType;
		suitableForType = newSuitableForType == null ? SUITABLE_FOR_TYPE_EDEFAULT : newSuitableForType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.COMPARATOR__SUITABLE_FOR_TYPE, oldSuitableForType, suitableForType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public QWhere getWhere() {
		if (eContainerFeatureID() != QueryPackage.COMPARATOR__WHERE) return null;
		return (QWhere)eInternalContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetWhere(QWhere newWhere, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newWhere, QueryPackage.COMPARATOR__WHERE, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setWhere(QWhere newWhere) {
		if (newWhere != eInternalContainer() || (eContainerFeatureID() != QueryPackage.COMPARATOR__WHERE && newWhere != null)) {
			if (EcoreUtil.isAncestor(this, newWhere))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newWhere != null)
				msgs = ((InternalEObject)newWhere).eInverseAdd(this, QueryPackage.QWHERE__COMPARATOR, QWhere.class, msgs);
			msgs = basicSetWhere(newWhere, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.COMPARATOR__WHERE, newWhere, newWhere));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean compare() {
		// TODO: implement this method
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case QueryPackage.COMPARATOR__WHERE:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetWhere((QWhere)otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case QueryPackage.COMPARATOR__WHERE:
				return basicSetWhere(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID()) {
			case QueryPackage.COMPARATOR__WHERE:
				return eInternalContainer().eInverseRemove(this, QueryPackage.QWHERE__COMPARATOR, QWhere.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case QueryPackage.COMPARATOR__SUITABLE_FOR_TYPE:
				return getSuitableForType();
			case QueryPackage.COMPARATOR__WHERE:
				return getWhere();
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
			case QueryPackage.COMPARATOR__SUITABLE_FOR_TYPE:
				setSuitableForType((SuitableType)newValue);
				return;
			case QueryPackage.COMPARATOR__WHERE:
				setWhere((QWhere)newValue);
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
			case QueryPackage.COMPARATOR__SUITABLE_FOR_TYPE:
				setSuitableForType(SUITABLE_FOR_TYPE_EDEFAULT);
				return;
			case QueryPackage.COMPARATOR__WHERE:
				setWhere((QWhere)null);
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
			case QueryPackage.COMPARATOR__SUITABLE_FOR_TYPE:
				return suitableForType != SUITABLE_FOR_TYPE_EDEFAULT;
			case QueryPackage.COMPARATOR__WHERE:
				return getWhere() != null;
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
			case QueryPackage.COMPARATOR___COMPARE:
				return compare();
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
		result.append(" (suitableForType: ");
		result.append(suitableForType);
		result.append(')');
		return result.toString();
	}

} //ComparatorImpl
