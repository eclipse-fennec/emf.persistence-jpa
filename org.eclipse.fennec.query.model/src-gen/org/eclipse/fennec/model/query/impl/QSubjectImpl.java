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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.model.query.Operation;
import org.eclipse.fennec.model.query.QSubject;
import org.eclipse.fennec.model.query.QueryPackage;

import org.eclipse.fennec.model.utilities.FeaturePath;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>QSubject</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.query.impl.QSubjectImpl#getFeaturePath <em>Feature Path</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.QSubjectImpl#isIsExclude <em>Is Exclude</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.QSubjectImpl#getAlias <em>Alias</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.QSubjectImpl#getAliasFeature <em>Alias Feature</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.QSubjectImpl#getOperation <em>Operation</em>}</li>
 * </ul>
 *
 * @generated
 */
public class QSubjectImpl extends MinimalEObjectImpl.Container implements QSubject {
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
	 * The default value of the '{@link #isIsExclude() <em>Is Exclude</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIsExclude()
	 * @generated
	 * @ordered
	 */
	protected static final boolean IS_EXCLUDE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isIsExclude() <em>Is Exclude</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIsExclude()
	 * @generated
	 * @ordered
	 */
	protected boolean isExclude = IS_EXCLUDE_EDEFAULT;

	/**
	 * The default value of the '{@link #getAlias() <em>Alias</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAlias()
	 * @generated
	 * @ordered
	 */
	protected static final String ALIAS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAlias() <em>Alias</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAlias()
	 * @generated
	 * @ordered
	 */
	protected String alias = ALIAS_EDEFAULT;

	/**
	 * The cached value of the '{@link #getAliasFeature() <em>Alias Feature</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAliasFeature()
	 * @generated
	 * @ordered
	 */
	protected EStructuralFeature aliasFeature;

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
	protected QSubjectImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return QueryPackage.Literals.QSUBJECT;
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QueryPackage.QSUBJECT__FEATURE_PATH, oldFeaturePath, newFeaturePath);
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
				msgs = ((InternalEObject)featurePath).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QueryPackage.QSUBJECT__FEATURE_PATH, null, msgs);
			if (newFeaturePath != null)
				msgs = ((InternalEObject)newFeaturePath).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - QueryPackage.QSUBJECT__FEATURE_PATH, null, msgs);
			msgs = basicSetFeaturePath(newFeaturePath, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.QSUBJECT__FEATURE_PATH, newFeaturePath, newFeaturePath));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isIsExclude() {
		return isExclude;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIsExclude(boolean newIsExclude) {
		boolean oldIsExclude = isExclude;
		isExclude = newIsExclude;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.QSUBJECT__IS_EXCLUDE, oldIsExclude, isExclude));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getAlias() {
		return alias;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAlias(String newAlias) {
		String oldAlias = alias;
		alias = newAlias;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.QSUBJECT__ALIAS, oldAlias, alias));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EStructuralFeature getAliasFeature() {
		if (aliasFeature != null && aliasFeature.eIsProxy()) {
			InternalEObject oldAliasFeature = (InternalEObject)aliasFeature;
			aliasFeature = (EStructuralFeature)eResolveProxy(oldAliasFeature);
			if (aliasFeature != oldAliasFeature) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, QueryPackage.QSUBJECT__ALIAS_FEATURE, oldAliasFeature, aliasFeature));
			}
		}
		return aliasFeature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EStructuralFeature basicGetAliasFeature() {
		return aliasFeature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAliasFeature(EStructuralFeature newAliasFeature) {
		EStructuralFeature oldAliasFeature = aliasFeature;
		aliasFeature = newAliasFeature;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.QSUBJECT__ALIAS_FEATURE, oldAliasFeature, aliasFeature));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QueryPackage.QSUBJECT__OPERATION, oldOperation, newOperation);
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
				msgs = ((InternalEObject)operation).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QueryPackage.QSUBJECT__OPERATION, null, msgs);
			if (newOperation != null)
				msgs = ((InternalEObject)newOperation).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - QueryPackage.QSUBJECT__OPERATION, null, msgs);
			msgs = basicSetOperation(newOperation, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.QSUBJECT__OPERATION, newOperation, newOperation));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case QueryPackage.QSUBJECT__FEATURE_PATH:
				return basicSetFeaturePath(null, msgs);
			case QueryPackage.QSUBJECT__OPERATION:
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
			case QueryPackage.QSUBJECT__FEATURE_PATH:
				return getFeaturePath();
			case QueryPackage.QSUBJECT__IS_EXCLUDE:
				return isIsExclude();
			case QueryPackage.QSUBJECT__ALIAS:
				return getAlias();
			case QueryPackage.QSUBJECT__ALIAS_FEATURE:
				if (resolve) return getAliasFeature();
				return basicGetAliasFeature();
			case QueryPackage.QSUBJECT__OPERATION:
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
			case QueryPackage.QSUBJECT__FEATURE_PATH:
				setFeaturePath((FeaturePath)newValue);
				return;
			case QueryPackage.QSUBJECT__IS_EXCLUDE:
				setIsExclude((Boolean)newValue);
				return;
			case QueryPackage.QSUBJECT__ALIAS:
				setAlias((String)newValue);
				return;
			case QueryPackage.QSUBJECT__ALIAS_FEATURE:
				setAliasFeature((EStructuralFeature)newValue);
				return;
			case QueryPackage.QSUBJECT__OPERATION:
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
			case QueryPackage.QSUBJECT__FEATURE_PATH:
				setFeaturePath((FeaturePath)null);
				return;
			case QueryPackage.QSUBJECT__IS_EXCLUDE:
				setIsExclude(IS_EXCLUDE_EDEFAULT);
				return;
			case QueryPackage.QSUBJECT__ALIAS:
				setAlias(ALIAS_EDEFAULT);
				return;
			case QueryPackage.QSUBJECT__ALIAS_FEATURE:
				setAliasFeature((EStructuralFeature)null);
				return;
			case QueryPackage.QSUBJECT__OPERATION:
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
			case QueryPackage.QSUBJECT__FEATURE_PATH:
				return featurePath != null;
			case QueryPackage.QSUBJECT__IS_EXCLUDE:
				return isExclude != IS_EXCLUDE_EDEFAULT;
			case QueryPackage.QSUBJECT__ALIAS:
				return ALIAS_EDEFAULT == null ? alias != null : !ALIAS_EDEFAULT.equals(alias);
			case QueryPackage.QSUBJECT__ALIAS_FEATURE:
				return aliasFeature != null;
			case QueryPackage.QSUBJECT__OPERATION:
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
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (isExclude: ");
		result.append(isExclude);
		result.append(", alias: ");
		result.append(alias);
		result.append(')');
		return result.toString();
	}

} //QSubjectImpl
