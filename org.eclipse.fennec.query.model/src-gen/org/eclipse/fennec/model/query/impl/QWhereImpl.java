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

import org.eclipse.fennec.model.query.Comparator;
import org.eclipse.fennec.model.query.Operation;
import org.eclipse.fennec.model.query.QWhere;
import org.eclipse.fennec.model.query.QueryPackage;

import org.eclipse.fennec.model.utilities.FeaturePath;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>QWhere</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.query.impl.QWhereImpl#getFeaturePath <em>Feature Path</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.QWhereImpl#getComparator <em>Comparator</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.QWhereImpl#getOperation <em>Operation</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class QWhereImpl extends MinimalEObjectImpl.Container implements QWhere {
	/**
	 * The cached value of the '{@link #getFeaturePath() <em>Feature Path</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFeaturePath()
	 * @generated
	 * @ordered
	 */
	protected FeaturePath featurePath;

	/**
	 * The cached value of the '{@link #getComparator() <em>Comparator</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComparator()
	 * @generated
	 * @ordered
	 */
	protected Comparator comparator;

	/**
	 * The cached value of the '{@link #getOperation() <em>Operation</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOperation()
	 * @generated
	 * @ordered
	 */
	protected Operation operation;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected QWhereImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return QueryPackage.Literals.QWHERE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public FeaturePath getFeaturePath() {
		return featurePath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetFeaturePath(FeaturePath newFeaturePath, NotificationChain msgs) {
		FeaturePath oldFeaturePath = featurePath;
		featurePath = newFeaturePath;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QueryPackage.QWHERE__FEATURE_PATH, oldFeaturePath, newFeaturePath);
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
	public void setFeaturePath(FeaturePath newFeaturePath) {
		if (newFeaturePath != featurePath) {
			NotificationChain msgs = null;
			if (featurePath != null)
				msgs = ((InternalEObject)featurePath).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QueryPackage.QWHERE__FEATURE_PATH, null, msgs);
			if (newFeaturePath != null)
				msgs = ((InternalEObject)newFeaturePath).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - QueryPackage.QWHERE__FEATURE_PATH, null, msgs);
			msgs = basicSetFeaturePath(newFeaturePath, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.QWHERE__FEATURE_PATH, newFeaturePath, newFeaturePath));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Comparator getComparator() {
		return comparator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetComparator(Comparator newComparator, NotificationChain msgs) {
		Comparator oldComparator = comparator;
		comparator = newComparator;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QueryPackage.QWHERE__COMPARATOR, oldComparator, newComparator);
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
	public void setComparator(Comparator newComparator) {
		if (newComparator != comparator) {
			NotificationChain msgs = null;
			if (comparator != null)
				msgs = ((InternalEObject)comparator).eInverseRemove(this, QueryPackage.COMPARATOR__WHERE, Comparator.class, msgs);
			if (newComparator != null)
				msgs = ((InternalEObject)newComparator).eInverseAdd(this, QueryPackage.COMPARATOR__WHERE, Comparator.class, msgs);
			msgs = basicSetComparator(newComparator, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.QWHERE__COMPARATOR, newComparator, newComparator));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Operation getOperation() {
		return operation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetOperation(Operation newOperation, NotificationChain msgs) {
		Operation oldOperation = operation;
		operation = newOperation;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QueryPackage.QWHERE__OPERATION, oldOperation, newOperation);
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
	public void setOperation(Operation newOperation) {
		if (newOperation != operation) {
			NotificationChain msgs = null;
			if (operation != null)
				msgs = ((InternalEObject)operation).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QueryPackage.QWHERE__OPERATION, null, msgs);
			if (newOperation != null)
				msgs = ((InternalEObject)newOperation).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - QueryPackage.QWHERE__OPERATION, null, msgs);
			msgs = basicSetOperation(newOperation, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.QWHERE__OPERATION, newOperation, newOperation));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean execute() {
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
			case QueryPackage.QWHERE__COMPARATOR:
				if (comparator != null)
					msgs = ((InternalEObject)comparator).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QueryPackage.QWHERE__COMPARATOR, null, msgs);
				return basicSetComparator((Comparator)otherEnd, msgs);
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
			case QueryPackage.QWHERE__FEATURE_PATH:
				return basicSetFeaturePath(null, msgs);
			case QueryPackage.QWHERE__COMPARATOR:
				return basicSetComparator(null, msgs);
			case QueryPackage.QWHERE__OPERATION:
				return basicSetOperation(null, msgs);
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
			case QueryPackage.QWHERE__FEATURE_PATH:
				return getFeaturePath();
			case QueryPackage.QWHERE__COMPARATOR:
				return getComparator();
			case QueryPackage.QWHERE__OPERATION:
				return getOperation();
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
			case QueryPackage.QWHERE__FEATURE_PATH:
				setFeaturePath((FeaturePath)newValue);
				return;
			case QueryPackage.QWHERE__COMPARATOR:
				setComparator((Comparator)newValue);
				return;
			case QueryPackage.QWHERE__OPERATION:
				setOperation((Operation)newValue);
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
			case QueryPackage.QWHERE__FEATURE_PATH:
				setFeaturePath((FeaturePath)null);
				return;
			case QueryPackage.QWHERE__COMPARATOR:
				setComparator((Comparator)null);
				return;
			case QueryPackage.QWHERE__OPERATION:
				setOperation((Operation)null);
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
			case QueryPackage.QWHERE__FEATURE_PATH:
				return featurePath != null;
			case QueryPackage.QWHERE__COMPARATOR:
				return comparator != null;
			case QueryPackage.QWHERE__OPERATION:
				return operation != null;
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
			case QueryPackage.QWHERE___EXECUTE:
				return execute();
		}
		return super.eInvoke(operationID, arguments);
	}

} //QWhereImpl
