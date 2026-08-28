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
import org.eclipse.fennec.model.expression.PropertyPath;

import org.eclipse.fennec.model.query.Expand;
import org.eclipse.fennec.model.query.OrderBy;
import org.eclipse.fennec.model.query.QueryPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Expand</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.query.impl.ExpandImpl#getPath <em>Path</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.ExpandImpl#getFilter <em>Filter</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.ExpandImpl#getOrderBy <em>Order By</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.ExpandImpl#getTop <em>Top</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.ExpandImpl#getSkip <em>Skip</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.ExpandImpl#getExpand <em>Expand</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ExpandImpl extends MinimalEObjectImpl.Container implements Expand {
	/**
	 * The cached value of the '{@link #getPath() <em>Path</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPath()
	 * @generated
	 * @ordered
	 */
	protected PropertyPath path;

	/**
	 * The cached value of the '{@link #getFilter() <em>Filter</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFilter()
	 * @generated
	 * @ordered
	 */
	protected Expression filter;

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
	 * The default value of the '{@link #getTop() <em>Top</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTop()
	 * @generated
	 * @ordered
	 */
	protected static final int TOP_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getTop() <em>Top</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTop()
	 * @generated
	 * @ordered
	 */
	protected int top = TOP_EDEFAULT;

	/**
	 * The default value of the '{@link #getSkip() <em>Skip</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSkip()
	 * @generated
	 * @ordered
	 */
	protected static final int SKIP_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getSkip() <em>Skip</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSkip()
	 * @generated
	 * @ordered
	 */
	protected int skip = SKIP_EDEFAULT;

	/**
	 * The cached value of the '{@link #getExpand() <em>Expand</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpand()
	 * @generated
	 * @ordered
	 */
	protected EList<Expand> expand;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ExpandImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return QueryPackage.Literals.EXPAND;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PropertyPath getPath() {
		return path;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPath(PropertyPath newPath, NotificationChain msgs) {
		PropertyPath oldPath = path;
		path = newPath;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QueryPackage.EXPAND__PATH, oldPath, newPath);
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
	public void setPath(PropertyPath newPath) {
		if (newPath != path) {
			NotificationChain msgs = null;
			if (path != null)
				msgs = ((InternalEObject)path).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QueryPackage.EXPAND__PATH, null, msgs);
			if (newPath != null)
				msgs = ((InternalEObject)newPath).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - QueryPackage.EXPAND__PATH, null, msgs);
			msgs = basicSetPath(newPath, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.EXPAND__PATH, newPath, newPath));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Expression getFilter() {
		return filter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetFilter(Expression newFilter, NotificationChain msgs) {
		Expression oldFilter = filter;
		filter = newFilter;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QueryPackage.EXPAND__FILTER, oldFilter, newFilter);
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
	public void setFilter(Expression newFilter) {
		if (newFilter != filter) {
			NotificationChain msgs = null;
			if (filter != null)
				msgs = ((InternalEObject)filter).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QueryPackage.EXPAND__FILTER, null, msgs);
			if (newFilter != null)
				msgs = ((InternalEObject)newFilter).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - QueryPackage.EXPAND__FILTER, null, msgs);
			msgs = basicSetFilter(newFilter, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.EXPAND__FILTER, newFilter, newFilter));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<OrderBy> getOrderBy() {
		if (orderBy == null) {
			orderBy = new EObjectContainmentEList<OrderBy>(OrderBy.class, this, QueryPackage.EXPAND__ORDER_BY);
		}
		return orderBy;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getTop() {
		return top;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTop(int newTop) {
		int oldTop = top;
		top = newTop;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.EXPAND__TOP, oldTop, top));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getSkip() {
		return skip;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSkip(int newSkip) {
		int oldSkip = skip;
		skip = newSkip;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.EXPAND__SKIP, oldSkip, skip));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Expand> getExpand() {
		if (expand == null) {
			expand = new EObjectContainmentEList<Expand>(Expand.class, this, QueryPackage.EXPAND__EXPAND);
		}
		return expand;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case QueryPackage.EXPAND__PATH:
				return basicSetPath(null, msgs);
			case QueryPackage.EXPAND__FILTER:
				return basicSetFilter(null, msgs);
			case QueryPackage.EXPAND__ORDER_BY:
				return ((InternalEList<?>)getOrderBy()).basicRemove(otherEnd, msgs);
			case QueryPackage.EXPAND__EXPAND:
				return ((InternalEList<?>)getExpand()).basicRemove(otherEnd, msgs);
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
			case QueryPackage.EXPAND__PATH:
				return getPath();
			case QueryPackage.EXPAND__FILTER:
				return getFilter();
			case QueryPackage.EXPAND__ORDER_BY:
				return getOrderBy();
			case QueryPackage.EXPAND__TOP:
				return getTop();
			case QueryPackage.EXPAND__SKIP:
				return getSkip();
			case QueryPackage.EXPAND__EXPAND:
				return getExpand();
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
			case QueryPackage.EXPAND__PATH:
				setPath((PropertyPath)newValue);
				return;
			case QueryPackage.EXPAND__FILTER:
				setFilter((Expression)newValue);
				return;
			case QueryPackage.EXPAND__ORDER_BY:
				getOrderBy().clear();
				getOrderBy().addAll((Collection<? extends OrderBy>)newValue);
				return;
			case QueryPackage.EXPAND__TOP:
				setTop((Integer)newValue);
				return;
			case QueryPackage.EXPAND__SKIP:
				setSkip((Integer)newValue);
				return;
			case QueryPackage.EXPAND__EXPAND:
				getExpand().clear();
				getExpand().addAll((Collection<? extends Expand>)newValue);
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
			case QueryPackage.EXPAND__PATH:
				setPath((PropertyPath)null);
				return;
			case QueryPackage.EXPAND__FILTER:
				setFilter((Expression)null);
				return;
			case QueryPackage.EXPAND__ORDER_BY:
				getOrderBy().clear();
				return;
			case QueryPackage.EXPAND__TOP:
				setTop(TOP_EDEFAULT);
				return;
			case QueryPackage.EXPAND__SKIP:
				setSkip(SKIP_EDEFAULT);
				return;
			case QueryPackage.EXPAND__EXPAND:
				getExpand().clear();
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
			case QueryPackage.EXPAND__PATH:
				return path != null;
			case QueryPackage.EXPAND__FILTER:
				return filter != null;
			case QueryPackage.EXPAND__ORDER_BY:
				return orderBy != null && !orderBy.isEmpty();
			case QueryPackage.EXPAND__TOP:
				return top != TOP_EDEFAULT;
			case QueryPackage.EXPAND__SKIP:
				return skip != SKIP_EDEFAULT;
			case QueryPackage.EXPAND__EXPAND:
				return expand != null && !expand.isEmpty();
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
		result.append(" (top: ");
		result.append(top);
		result.append(", skip: ");
		result.append(skip);
		result.append(')');
		return result.toString();
	}

} //ExpandImpl
