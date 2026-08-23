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
package org.eclipse.fennec.model.query.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.model.expression.Expression;

import org.eclipse.fennec.model.query.OrderBy;
import org.eclipse.fennec.model.query.QueryPackage;
import org.eclipse.fennec.model.query.RepresentativeSpec;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Representative Spec</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.query.impl.RepresentativeSpecImpl#getAlias <em>Alias</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.RepresentativeSpecImpl#getCount <em>Count</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.RepresentativeSpecImpl#getOffset <em>Offset</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.RepresentativeSpecImpl#getOrderBy <em>Order By</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RepresentativeSpecImpl extends MinimalEObjectImpl.Container implements RepresentativeSpec {
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
	 * The cached value of the '{@link #getCount() <em>Count</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCount()
	 * @generated
	 * @ordered
	 */
	protected Expression count;

	/**
	 * The cached value of the '{@link #getOffset() <em>Offset</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOffset()
	 * @generated
	 * @ordered
	 */
	protected Expression offset;

	/**
	 * The cached value of the '{@link #getOrderBy() <em>Order By</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOrderBy()
	 * @generated
	 * @ordered
	 */
	protected EList<OrderBy> orderBy;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RepresentativeSpecImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return QueryPackage.Literals.REPRESENTATIVE_SPEC;
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
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.REPRESENTATIVE_SPEC__ALIAS, oldAlias, alias));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Expression getCount() {
		return count;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetCount(Expression newCount, NotificationChain msgs) {
		Expression oldCount = count;
		count = newCount;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QueryPackage.REPRESENTATIVE_SPEC__COUNT, oldCount, newCount);
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
	public void setCount(Expression newCount) {
		if (newCount != count) {
			NotificationChain msgs = null;
			if (count != null)
				msgs = ((InternalEObject)count).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QueryPackage.REPRESENTATIVE_SPEC__COUNT, null, msgs);
			if (newCount != null)
				msgs = ((InternalEObject)newCount).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - QueryPackage.REPRESENTATIVE_SPEC__COUNT, null, msgs);
			msgs = basicSetCount(newCount, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.REPRESENTATIVE_SPEC__COUNT, newCount, newCount));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Expression getOffset() {
		return offset;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetOffset(Expression newOffset, NotificationChain msgs) {
		Expression oldOffset = offset;
		offset = newOffset;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QueryPackage.REPRESENTATIVE_SPEC__OFFSET, oldOffset, newOffset);
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
	public void setOffset(Expression newOffset) {
		if (newOffset != offset) {
			NotificationChain msgs = null;
			if (offset != null)
				msgs = ((InternalEObject)offset).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QueryPackage.REPRESENTATIVE_SPEC__OFFSET, null, msgs);
			if (newOffset != null)
				msgs = ((InternalEObject)newOffset).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - QueryPackage.REPRESENTATIVE_SPEC__OFFSET, null, msgs);
			msgs = basicSetOffset(newOffset, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.REPRESENTATIVE_SPEC__OFFSET, newOffset, newOffset));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<OrderBy> getOrderBy() {
		if (orderBy == null) {
			orderBy = new EObjectContainmentEList<OrderBy>(OrderBy.class, this, QueryPackage.REPRESENTATIVE_SPEC__ORDER_BY);
		}
		return orderBy;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case QueryPackage.REPRESENTATIVE_SPEC__COUNT:
				return basicSetCount(null, msgs);
			case QueryPackage.REPRESENTATIVE_SPEC__OFFSET:
				return basicSetOffset(null, msgs);
			case QueryPackage.REPRESENTATIVE_SPEC__ORDER_BY:
				return ((InternalEList<?>)getOrderBy()).basicRemove(otherEnd, msgs);
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
			case QueryPackage.REPRESENTATIVE_SPEC__ALIAS:
				return getAlias();
			case QueryPackage.REPRESENTATIVE_SPEC__COUNT:
				return getCount();
			case QueryPackage.REPRESENTATIVE_SPEC__OFFSET:
				return getOffset();
			case QueryPackage.REPRESENTATIVE_SPEC__ORDER_BY:
				return getOrderBy();
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
			case QueryPackage.REPRESENTATIVE_SPEC__ALIAS:
				setAlias((String)newValue);
				return;
			case QueryPackage.REPRESENTATIVE_SPEC__COUNT:
				setCount((Expression)newValue);
				return;
			case QueryPackage.REPRESENTATIVE_SPEC__OFFSET:
				setOffset((Expression)newValue);
				return;
			case QueryPackage.REPRESENTATIVE_SPEC__ORDER_BY:
				getOrderBy().clear();
				getOrderBy().addAll((Collection<? extends OrderBy>)newValue);
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
			case QueryPackage.REPRESENTATIVE_SPEC__ALIAS:
				setAlias(ALIAS_EDEFAULT);
				return;
			case QueryPackage.REPRESENTATIVE_SPEC__COUNT:
				setCount((Expression)null);
				return;
			case QueryPackage.REPRESENTATIVE_SPEC__OFFSET:
				setOffset((Expression)null);
				return;
			case QueryPackage.REPRESENTATIVE_SPEC__ORDER_BY:
				getOrderBy().clear();
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
			case QueryPackage.REPRESENTATIVE_SPEC__ALIAS:
				return ALIAS_EDEFAULT == null ? alias != null : !ALIAS_EDEFAULT.equals(alias);
			case QueryPackage.REPRESENTATIVE_SPEC__COUNT:
				return count != null;
			case QueryPackage.REPRESENTATIVE_SPEC__OFFSET:
				return offset != null;
			case QueryPackage.REPRESENTATIVE_SPEC__ORDER_BY:
				return orderBy != null && !orderBy.isEmpty();
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
		result.append(" (alias: ");
		result.append(alias);
		result.append(')');
		return result.toString();
	}

} //RepresentativeSpecImpl
