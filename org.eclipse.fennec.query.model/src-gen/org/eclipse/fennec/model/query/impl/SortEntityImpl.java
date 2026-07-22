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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.model.query.QueryPackage;
import org.eclipse.fennec.model.query.SortEntity;
import org.eclipse.fennec.model.query.SortOrder;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Sort Entity</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.query.impl.SortEntityImpl#getSortOrder <em>Sort Order</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.SortEntityImpl#getSortFeature <em>Sort Feature</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SortEntityImpl extends MinimalEObjectImpl.Container implements SortEntity {
	/**
	 * The default value of the '{@link #getSortOrder() <em>Sort Order</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSortOrder()
	 * @generated
	 * @ordered
	 */
	protected static final SortOrder SORT_ORDER_EDEFAULT = SortOrder.DESC;

	/**
	 * The cached value of the '{@link #getSortOrder() <em>Sort Order</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSortOrder()
	 * @generated
	 * @ordered
	 */
	protected SortOrder sortOrder = SORT_ORDER_EDEFAULT;

	/**
	 * The cached value of the '{@link #getSortFeature() <em>Sort Feature</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSortFeature()
	 * @generated
	 * @ordered
	 */
	protected EStructuralFeature sortFeature;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SortEntityImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return QueryPackage.Literals.SORT_ENTITY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SortOrder getSortOrder() {
		return sortOrder;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSortOrder(SortOrder newSortOrder) {
		SortOrder oldSortOrder = sortOrder;
		sortOrder = newSortOrder == null ? SORT_ORDER_EDEFAULT : newSortOrder;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.SORT_ENTITY__SORT_ORDER, oldSortOrder, sortOrder));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EStructuralFeature getSortFeature() {
		if (sortFeature != null && sortFeature.eIsProxy()) {
			InternalEObject oldSortFeature = (InternalEObject)sortFeature;
			sortFeature = (EStructuralFeature)eResolveProxy(oldSortFeature);
			if (sortFeature != oldSortFeature) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, QueryPackage.SORT_ENTITY__SORT_FEATURE, oldSortFeature, sortFeature));
			}
		}
		return sortFeature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EStructuralFeature basicGetSortFeature() {
		return sortFeature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSortFeature(EStructuralFeature newSortFeature) {
		EStructuralFeature oldSortFeature = sortFeature;
		sortFeature = newSortFeature;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.SORT_ENTITY__SORT_FEATURE, oldSortFeature, sortFeature));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case QueryPackage.SORT_ENTITY__SORT_ORDER:
				return getSortOrder();
			case QueryPackage.SORT_ENTITY__SORT_FEATURE:
				if (resolve) return getSortFeature();
				return basicGetSortFeature();
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
			case QueryPackage.SORT_ENTITY__SORT_ORDER:
				setSortOrder((SortOrder)newValue);
				return;
			case QueryPackage.SORT_ENTITY__SORT_FEATURE:
				setSortFeature((EStructuralFeature)newValue);
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
			case QueryPackage.SORT_ENTITY__SORT_ORDER:
				setSortOrder(SORT_ORDER_EDEFAULT);
				return;
			case QueryPackage.SORT_ENTITY__SORT_FEATURE:
				setSortFeature((EStructuralFeature)null);
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
			case QueryPackage.SORT_ENTITY__SORT_ORDER:
				return sortOrder != SORT_ORDER_EDEFAULT;
			case QueryPackage.SORT_ENTITY__SORT_FEATURE:
				return sortFeature != null;
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
		result.append(" (sortOrder: ");
		result.append(sortOrder);
		result.append(')');
		return result.toString();
	}

} //SortEntityImpl
