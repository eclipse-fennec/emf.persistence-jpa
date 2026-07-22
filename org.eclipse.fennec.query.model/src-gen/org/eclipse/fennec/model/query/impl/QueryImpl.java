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

import org.eclipse.fennec.model.query.QObject;
import org.eclipse.fennec.model.query.QSubject;
import org.eclipse.fennec.model.query.QWhere;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.QueryPackage;
import org.eclipse.fennec.model.query.SortEntity;

import org.eclipse.fennec.model.utilities.FeaturePath;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Query</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.query.impl.QueryImpl#getSubject <em>Subject</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.QueryImpl#getFrom <em>From</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.QueryImpl#getWhere <em>Where</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.QueryImpl#getGroupBy <em>Group By</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.QueryImpl#getSortBy <em>Sort By</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.QueryImpl#isCount <em>Count</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.QueryImpl#isDistinct <em>Distinct</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.QueryImpl#getLimit <em>Limit</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.QueryImpl#getSkip <em>Skip</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.QueryImpl#isSaveQuery <em>Save Query</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.QueryImpl#getName <em>Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class QueryImpl extends MinimalEObjectImpl.Container implements Query {
	/**
	 * The cached value of the '{@link #getSubject() <em>Subject</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubject()
	 * @generated
	 * @ordered
	 */
	protected EList<QSubject> subject;

	/**
	 * The cached value of the '{@link #getFrom() <em>From</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFrom()
	 * @generated
	 * @ordered
	 */
	protected EList<QObject> from;

	/**
	 * The cached value of the '{@link #getWhere() <em>Where</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWhere()
	 * @generated
	 * @ordered
	 */
	protected EList<QWhere> where;

	/**
	 * The cached value of the '{@link #getGroupBy() <em>Group By</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGroupBy()
	 * @generated
	 * @ordered
	 */
	protected EList<FeaturePath> groupBy;

	/**
	 * The cached value of the '{@link #getSortBy() <em>Sort By</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSortBy()
	 * @generated
	 * @ordered
	 */
	protected EList<SortEntity> sortBy;

	/**
	 * The default value of the '{@link #isCount() <em>Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isCount()
	 * @generated
	 * @ordered
	 */
	protected static final boolean COUNT_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isCount() <em>Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isCount()
	 * @generated
	 * @ordered
	 */
	protected boolean count = COUNT_EDEFAULT;

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
	 * The default value of the '{@link #getLimit() <em>Limit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLimit()
	 * @generated
	 * @ordered
	 */
	protected static final int LIMIT_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getLimit() <em>Limit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLimit()
	 * @generated
	 * @ordered
	 */
	protected int limit = LIMIT_EDEFAULT;

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
	public EList<QSubject> getSubject() {
		if (subject == null) {
			subject = new EObjectContainmentEList<QSubject>(QSubject.class, this, QueryPackage.QUERY__SUBJECT);
		}
		return subject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<QObject> getFrom() {
		if (from == null) {
			from = new EObjectContainmentEList<QObject>(QObject.class, this, QueryPackage.QUERY__FROM);
		}
		return from;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<QWhere> getWhere() {
		if (where == null) {
			where = new EObjectContainmentEList<QWhere>(QWhere.class, this, QueryPackage.QUERY__WHERE);
		}
		return where;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<FeaturePath> getGroupBy() {
		if (groupBy == null) {
			groupBy = new EObjectContainmentEList<FeaturePath>(FeaturePath.class, this, QueryPackage.QUERY__GROUP_BY);
		}
		return groupBy;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<SortEntity> getSortBy() {
		if (sortBy == null) {
			sortBy = new EObjectContainmentEList<SortEntity>(SortEntity.class, this, QueryPackage.QUERY__SORT_BY);
		}
		return sortBy;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isCount() {
		return count;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setCount(boolean newCount) {
		boolean oldCount = count;
		count = newCount;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.QUERY__COUNT, oldCount, count));
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
	public int getLimit() {
		return limit;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setLimit(int newLimit) {
		int oldLimit = limit;
		limit = newLimit;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.QUERY__LIMIT, oldLimit, limit));
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
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case QueryPackage.QUERY__SUBJECT:
				return ((InternalEList<?>)getSubject()).basicRemove(otherEnd, msgs);
			case QueryPackage.QUERY__FROM:
				return ((InternalEList<?>)getFrom()).basicRemove(otherEnd, msgs);
			case QueryPackage.QUERY__WHERE:
				return ((InternalEList<?>)getWhere()).basicRemove(otherEnd, msgs);
			case QueryPackage.QUERY__GROUP_BY:
				return ((InternalEList<?>)getGroupBy()).basicRemove(otherEnd, msgs);
			case QueryPackage.QUERY__SORT_BY:
				return ((InternalEList<?>)getSortBy()).basicRemove(otherEnd, msgs);
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
			case QueryPackage.QUERY__SUBJECT:
				return getSubject();
			case QueryPackage.QUERY__FROM:
				return getFrom();
			case QueryPackage.QUERY__WHERE:
				return getWhere();
			case QueryPackage.QUERY__GROUP_BY:
				return getGroupBy();
			case QueryPackage.QUERY__SORT_BY:
				return getSortBy();
			case QueryPackage.QUERY__COUNT:
				return isCount();
			case QueryPackage.QUERY__DISTINCT:
				return isDistinct();
			case QueryPackage.QUERY__LIMIT:
				return getLimit();
			case QueryPackage.QUERY__SKIP:
				return getSkip();
			case QueryPackage.QUERY__SAVE_QUERY:
				return isSaveQuery();
			case QueryPackage.QUERY__NAME:
				return getName();
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
			case QueryPackage.QUERY__SUBJECT:
				getSubject().clear();
				getSubject().addAll((Collection<? extends QSubject>)newValue);
				return;
			case QueryPackage.QUERY__FROM:
				getFrom().clear();
				getFrom().addAll((Collection<? extends QObject>)newValue);
				return;
			case QueryPackage.QUERY__WHERE:
				getWhere().clear();
				getWhere().addAll((Collection<? extends QWhere>)newValue);
				return;
			case QueryPackage.QUERY__GROUP_BY:
				getGroupBy().clear();
				getGroupBy().addAll((Collection<? extends FeaturePath>)newValue);
				return;
			case QueryPackage.QUERY__SORT_BY:
				getSortBy().clear();
				getSortBy().addAll((Collection<? extends SortEntity>)newValue);
				return;
			case QueryPackage.QUERY__COUNT:
				setCount((Boolean)newValue);
				return;
			case QueryPackage.QUERY__DISTINCT:
				setDistinct((Boolean)newValue);
				return;
			case QueryPackage.QUERY__LIMIT:
				setLimit((Integer)newValue);
				return;
			case QueryPackage.QUERY__SKIP:
				setSkip((Integer)newValue);
				return;
			case QueryPackage.QUERY__SAVE_QUERY:
				setSaveQuery((Boolean)newValue);
				return;
			case QueryPackage.QUERY__NAME:
				setName((String)newValue);
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
			case QueryPackage.QUERY__SUBJECT:
				getSubject().clear();
				return;
			case QueryPackage.QUERY__FROM:
				getFrom().clear();
				return;
			case QueryPackage.QUERY__WHERE:
				getWhere().clear();
				return;
			case QueryPackage.QUERY__GROUP_BY:
				getGroupBy().clear();
				return;
			case QueryPackage.QUERY__SORT_BY:
				getSortBy().clear();
				return;
			case QueryPackage.QUERY__COUNT:
				setCount(COUNT_EDEFAULT);
				return;
			case QueryPackage.QUERY__DISTINCT:
				setDistinct(DISTINCT_EDEFAULT);
				return;
			case QueryPackage.QUERY__LIMIT:
				setLimit(LIMIT_EDEFAULT);
				return;
			case QueryPackage.QUERY__SKIP:
				setSkip(SKIP_EDEFAULT);
				return;
			case QueryPackage.QUERY__SAVE_QUERY:
				setSaveQuery(SAVE_QUERY_EDEFAULT);
				return;
			case QueryPackage.QUERY__NAME:
				setName(NAME_EDEFAULT);
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
			case QueryPackage.QUERY__SUBJECT:
				return subject != null && !subject.isEmpty();
			case QueryPackage.QUERY__FROM:
				return from != null && !from.isEmpty();
			case QueryPackage.QUERY__WHERE:
				return where != null && !where.isEmpty();
			case QueryPackage.QUERY__GROUP_BY:
				return groupBy != null && !groupBy.isEmpty();
			case QueryPackage.QUERY__SORT_BY:
				return sortBy != null && !sortBy.isEmpty();
			case QueryPackage.QUERY__COUNT:
				return count != COUNT_EDEFAULT;
			case QueryPackage.QUERY__DISTINCT:
				return distinct != DISTINCT_EDEFAULT;
			case QueryPackage.QUERY__LIMIT:
				return limit != LIMIT_EDEFAULT;
			case QueryPackage.QUERY__SKIP:
				return skip != SKIP_EDEFAULT;
			case QueryPackage.QUERY__SAVE_QUERY:
				return saveQuery != SAVE_QUERY_EDEFAULT;
			case QueryPackage.QUERY__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
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
		result.append(" (count: ");
		result.append(count);
		result.append(", distinct: ");
		result.append(distinct);
		result.append(", limit: ");
		result.append(limit);
		result.append(", skip: ");
		result.append(skip);
		result.append(", saveQuery: ");
		result.append(saveQuery);
		result.append(", name: ");
		result.append(name);
		result.append(')');
		return result.toString();
	}

} //QueryImpl
