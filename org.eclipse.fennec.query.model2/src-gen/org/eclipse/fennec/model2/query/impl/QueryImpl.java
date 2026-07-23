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
package org.eclipse.fennec.model2.query.impl;

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

import org.eclipse.fennec.model2.query.OrderBy;
import org.eclipse.fennec.model2.query.ParameterDecl;
import org.eclipse.fennec.model2.query.Pipeline;
import org.eclipse.fennec.model2.query.Query;
import org.eclipse.fennec.model2.query.QueryPackage;
import org.eclipse.fennec.model2.query.Selection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Query</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model2.query.impl.QueryImpl#getFrom <em>From</em>}</li>
 *   <li>{@link org.eclipse.fennec.model2.query.impl.QueryImpl#getPredicate <em>Predicate</em>}</li>
 *   <li>{@link org.eclipse.fennec.model2.query.impl.QueryImpl#getOrderBy <em>Order By</em>}</li>
 *   <li>{@link org.eclipse.fennec.model2.query.impl.QueryImpl#getSelect <em>Select</em>}</li>
 *   <li>{@link org.eclipse.fennec.model2.query.impl.QueryImpl#getApply <em>Apply</em>}</li>
 *   <li>{@link org.eclipse.fennec.model2.query.impl.QueryImpl#getExpand <em>Expand</em>}</li>
 *   <li>{@link org.eclipse.fennec.model2.query.impl.QueryImpl#getTop <em>Top</em>}</li>
 *   <li>{@link org.eclipse.fennec.model2.query.impl.QueryImpl#getSkip <em>Skip</em>}</li>
 *   <li>{@link org.eclipse.fennec.model2.query.impl.QueryImpl#isDistinct <em>Distinct</em>}</li>
 *   <li>{@link org.eclipse.fennec.model2.query.impl.QueryImpl#isCountOnly <em>Count Only</em>}</li>
 *   <li>{@link org.eclipse.fennec.model2.query.impl.QueryImpl#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.eclipse.fennec.model2.query.impl.QueryImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.model2.query.impl.QueryImpl#isSaveQuery <em>Save Query</em>}</li>
 * </ul>
 *
 * @generated
 */
public class QueryImpl extends MinimalEObjectImpl.Container implements Query {
	/**
	 * The cached value of the '{@link #getFrom() <em>From</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFrom()
	 * @generated
	 * @ordered
	 */
	protected EClass from;

	/**
	 * The cached value of the '{@link #getPredicate() <em>Predicate</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPredicate()
	 * @generated
	 * @ordered
	 */
	protected Expression predicate;

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
	 * The cached value of the '{@link #getSelect() <em>Select</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSelect()
	 * @generated
	 * @ordered
	 */
	protected EList<Selection> select;

	/**
	 * The cached value of the '{@link #getApply() <em>Apply</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getApply()
	 * @generated
	 * @ordered
	 */
	protected Pipeline apply;

	/**
	 * The cached value of the '{@link #getExpand() <em>Expand</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpand()
	 * @generated
	 * @ordered
	 */
	protected EList<PropertyPath> expand;

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
	 * The default value of the '{@link #isDistinct() <em>Distinct</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDistinct()
	 * @generated
	 * @ordered
	 */
	protected static final boolean DISTINCT_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isDistinct() <em>Distinct</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDistinct()
	 * @generated
	 * @ordered
	 */
	protected boolean distinct = DISTINCT_EDEFAULT;

	/**
	 * The default value of the '{@link #isCountOnly() <em>Count Only</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isCountOnly()
	 * @generated
	 * @ordered
	 */
	protected static final boolean COUNT_ONLY_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isCountOnly() <em>Count Only</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isCountOnly()
	 * @generated
	 * @ordered
	 */
	protected boolean countOnly = COUNT_ONLY_EDEFAULT;

	/**
	 * The cached value of the '{@link #getParameters() <em>Parameters</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParameters()
	 * @generated
	 * @ordered
	 */
	protected EList<ParameterDecl> parameters;

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #isSaveQuery() <em>Save Query</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSaveQuery()
	 * @generated
	 * @ordered
	 */
	protected static final boolean SAVE_QUERY_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isSaveQuery() <em>Save Query</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSaveQuery()
	 * @generated
	 * @ordered
	 */
	protected boolean saveQuery = SAVE_QUERY_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected QueryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return QueryPackage.Literals.QUERY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getFrom() {
		if (from != null && from.eIsProxy()) {
			InternalEObject oldFrom = (InternalEObject)from;
			from = (EClass)eResolveProxy(oldFrom);
			if (from != oldFrom) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, QueryPackage.QUERY__FROM, oldFrom, from));
			}
		}
		return from;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass basicGetFrom() {
		return from;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setFrom(EClass newFrom) {
		EClass oldFrom = from;
		from = newFrom;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.QUERY__FROM, oldFrom, from));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Expression getPredicate() {
		return predicate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPredicate(Expression newPredicate, NotificationChain msgs) {
		Expression oldPredicate = predicate;
		predicate = newPredicate;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QueryPackage.QUERY__PREDICATE, oldPredicate, newPredicate);
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
	public void setPredicate(Expression newPredicate) {
		if (newPredicate != predicate) {
			NotificationChain msgs = null;
			if (predicate != null)
				msgs = ((InternalEObject)predicate).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QueryPackage.QUERY__PREDICATE, null, msgs);
			if (newPredicate != null)
				msgs = ((InternalEObject)newPredicate).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - QueryPackage.QUERY__PREDICATE, null, msgs);
			msgs = basicSetPredicate(newPredicate, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.QUERY__PREDICATE, newPredicate, newPredicate));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<OrderBy> getOrderBy() {
		if (orderBy == null) {
			orderBy = new EObjectContainmentEList<OrderBy>(OrderBy.class, this, QueryPackage.QUERY__ORDER_BY);
		}
		return orderBy;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Selection> getSelect() {
		if (select == null) {
			select = new EObjectContainmentEList<Selection>(Selection.class, this, QueryPackage.QUERY__SELECT);
		}
		return select;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Pipeline getApply() {
		return apply;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetApply(Pipeline newApply, NotificationChain msgs) {
		Pipeline oldApply = apply;
		apply = newApply;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QueryPackage.QUERY__APPLY, oldApply, newApply);
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
	public void setApply(Pipeline newApply) {
		if (newApply != apply) {
			NotificationChain msgs = null;
			if (apply != null)
				msgs = ((InternalEObject)apply).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QueryPackage.QUERY__APPLY, null, msgs);
			if (newApply != null)
				msgs = ((InternalEObject)newApply).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - QueryPackage.QUERY__APPLY, null, msgs);
			msgs = basicSetApply(newApply, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.QUERY__APPLY, newApply, newApply));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<PropertyPath> getExpand() {
		if (expand == null) {
			expand = new EObjectContainmentEList<PropertyPath>(PropertyPath.class, this, QueryPackage.QUERY__EXPAND);
		}
		return expand;
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
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.QUERY__TOP, oldTop, top));
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
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.QUERY__SKIP, oldSkip, skip));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isDistinct() {
		return distinct;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDistinct(boolean newDistinct) {
		boolean oldDistinct = distinct;
		distinct = newDistinct;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.QUERY__DISTINCT, oldDistinct, distinct));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isCountOnly() {
		return countOnly;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setCountOnly(boolean newCountOnly) {
		boolean oldCountOnly = countOnly;
		countOnly = newCountOnly;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.QUERY__COUNT_ONLY, oldCountOnly, countOnly));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ParameterDecl> getParameters() {
		if (parameters == null) {
			parameters = new EObjectContainmentEList<ParameterDecl>(ParameterDecl.class, this, QueryPackage.QUERY__PARAMETERS);
		}
		return parameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.QUERY__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSaveQuery() {
		return saveQuery;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSaveQuery(boolean newSaveQuery) {
		boolean oldSaveQuery = saveQuery;
		saveQuery = newSaveQuery;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.QUERY__SAVE_QUERY, oldSaveQuery, saveQuery));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case QueryPackage.QUERY__PREDICATE:
				return basicSetPredicate(null, msgs);
			case QueryPackage.QUERY__ORDER_BY:
				return ((InternalEList<?>)getOrderBy()).basicRemove(otherEnd, msgs);
			case QueryPackage.QUERY__SELECT:
				return ((InternalEList<?>)getSelect()).basicRemove(otherEnd, msgs);
			case QueryPackage.QUERY__APPLY:
				return basicSetApply(null, msgs);
			case QueryPackage.QUERY__EXPAND:
				return ((InternalEList<?>)getExpand()).basicRemove(otherEnd, msgs);
			case QueryPackage.QUERY__PARAMETERS:
				return ((InternalEList<?>)getParameters()).basicRemove(otherEnd, msgs);
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
			case QueryPackage.QUERY__FROM:
				if (resolve) return getFrom();
				return basicGetFrom();
			case QueryPackage.QUERY__PREDICATE:
				return getPredicate();
			case QueryPackage.QUERY__ORDER_BY:
				return getOrderBy();
			case QueryPackage.QUERY__SELECT:
				return getSelect();
			case QueryPackage.QUERY__APPLY:
				return getApply();
			case QueryPackage.QUERY__EXPAND:
				return getExpand();
			case QueryPackage.QUERY__TOP:
				return getTop();
			case QueryPackage.QUERY__SKIP:
				return getSkip();
			case QueryPackage.QUERY__DISTINCT:
				return isDistinct();
			case QueryPackage.QUERY__COUNT_ONLY:
				return isCountOnly();
			case QueryPackage.QUERY__PARAMETERS:
				return getParameters();
			case QueryPackage.QUERY__NAME:
				return getName();
			case QueryPackage.QUERY__SAVE_QUERY:
				return isSaveQuery();
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
			case QueryPackage.QUERY__FROM:
				setFrom((EClass)newValue);
				return;
			case QueryPackage.QUERY__PREDICATE:
				setPredicate((Expression)newValue);
				return;
			case QueryPackage.QUERY__ORDER_BY:
				getOrderBy().clear();
				getOrderBy().addAll((Collection<? extends OrderBy>)newValue);
				return;
			case QueryPackage.QUERY__SELECT:
				getSelect().clear();
				getSelect().addAll((Collection<? extends Selection>)newValue);
				return;
			case QueryPackage.QUERY__APPLY:
				setApply((Pipeline)newValue);
				return;
			case QueryPackage.QUERY__EXPAND:
				getExpand().clear();
				getExpand().addAll((Collection<? extends PropertyPath>)newValue);
				return;
			case QueryPackage.QUERY__TOP:
				setTop((Integer)newValue);
				return;
			case QueryPackage.QUERY__SKIP:
				setSkip((Integer)newValue);
				return;
			case QueryPackage.QUERY__DISTINCT:
				setDistinct((Boolean)newValue);
				return;
			case QueryPackage.QUERY__COUNT_ONLY:
				setCountOnly((Boolean)newValue);
				return;
			case QueryPackage.QUERY__PARAMETERS:
				getParameters().clear();
				getParameters().addAll((Collection<? extends ParameterDecl>)newValue);
				return;
			case QueryPackage.QUERY__NAME:
				setName((String)newValue);
				return;
			case QueryPackage.QUERY__SAVE_QUERY:
				setSaveQuery((Boolean)newValue);
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
			case QueryPackage.QUERY__FROM:
				setFrom((EClass)null);
				return;
			case QueryPackage.QUERY__PREDICATE:
				setPredicate((Expression)null);
				return;
			case QueryPackage.QUERY__ORDER_BY:
				getOrderBy().clear();
				return;
			case QueryPackage.QUERY__SELECT:
				getSelect().clear();
				return;
			case QueryPackage.QUERY__APPLY:
				setApply((Pipeline)null);
				return;
			case QueryPackage.QUERY__EXPAND:
				getExpand().clear();
				return;
			case QueryPackage.QUERY__TOP:
				setTop(TOP_EDEFAULT);
				return;
			case QueryPackage.QUERY__SKIP:
				setSkip(SKIP_EDEFAULT);
				return;
			case QueryPackage.QUERY__DISTINCT:
				setDistinct(DISTINCT_EDEFAULT);
				return;
			case QueryPackage.QUERY__COUNT_ONLY:
				setCountOnly(COUNT_ONLY_EDEFAULT);
				return;
			case QueryPackage.QUERY__PARAMETERS:
				getParameters().clear();
				return;
			case QueryPackage.QUERY__NAME:
				setName(NAME_EDEFAULT);
				return;
			case QueryPackage.QUERY__SAVE_QUERY:
				setSaveQuery(SAVE_QUERY_EDEFAULT);
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
			case QueryPackage.QUERY__FROM:
				return from != null;
			case QueryPackage.QUERY__PREDICATE:
				return predicate != null;
			case QueryPackage.QUERY__ORDER_BY:
				return orderBy != null && !orderBy.isEmpty();
			case QueryPackage.QUERY__SELECT:
				return select != null && !select.isEmpty();
			case QueryPackage.QUERY__APPLY:
				return apply != null;
			case QueryPackage.QUERY__EXPAND:
				return expand != null && !expand.isEmpty();
			case QueryPackage.QUERY__TOP:
				return top != TOP_EDEFAULT;
			case QueryPackage.QUERY__SKIP:
				return skip != SKIP_EDEFAULT;
			case QueryPackage.QUERY__DISTINCT:
				return distinct != DISTINCT_EDEFAULT;
			case QueryPackage.QUERY__COUNT_ONLY:
				return countOnly != COUNT_ONLY_EDEFAULT;
			case QueryPackage.QUERY__PARAMETERS:
				return parameters != null && !parameters.isEmpty();
			case QueryPackage.QUERY__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case QueryPackage.QUERY__SAVE_QUERY:
				return saveQuery != SAVE_QUERY_EDEFAULT;
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
		result.append(", distinct: ");
		result.append(distinct);
		result.append(", countOnly: ");
		result.append(countOnly);
		result.append(", name: ");
		result.append(name);
		result.append(", saveQuery: ");
		result.append(saveQuery);
		result.append(')');
		return result.toString();
	}

} //QueryImpl
