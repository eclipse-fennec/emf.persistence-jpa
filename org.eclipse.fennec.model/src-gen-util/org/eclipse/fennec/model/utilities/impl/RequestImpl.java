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
package org.eclipse.fennec.model.utilities.impl;

import java.util.Collection;
import java.util.Date;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.model.utilities.Filter;
import org.eclipse.fennec.model.utilities.Request;
import org.eclipse.fennec.model.utilities.Sort;
import org.eclipse.fennec.model.utilities.UtilitiesPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Request</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.utilities.impl.RequestImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.impl.RequestImpl#getObjectId <em>Object Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.impl.RequestImpl#getFrom <em>From</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.impl.RequestImpl#getTo <em>To</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.impl.RequestImpl#getPage <em>Page</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.impl.RequestImpl#getPageSize <em>Page Size</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.impl.RequestImpl#isReturnResultSize <em>Return Result Size</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.impl.RequestImpl#getSorting <em>Sorting</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.impl.RequestImpl#getFiltering <em>Filtering</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.impl.RequestImpl#getProjection <em>Projection</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.impl.RequestImpl#getQuery <em>Query</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RequestImpl extends MinimalEObjectImpl.Container implements Request {
	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * The cached value of the '{@link #getObjectId() <em>Object Id</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getObjectId()
	 * @generated
	 * @ordered
	 */
	protected EList<String> objectId;

	/**
	 * The default value of the '{@link #getFrom() <em>From</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFrom()
	 * @generated
	 * @ordered
	 */
	protected static final Date FROM_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFrom() <em>From</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFrom()
	 * @generated
	 * @ordered
	 */
	protected Date from = FROM_EDEFAULT;

	/**
	 * The default value of the '{@link #getTo() <em>To</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTo()
	 * @generated
	 * @ordered
	 */
	protected static final Date TO_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTo() <em>To</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTo()
	 * @generated
	 * @ordered
	 */
	protected Date to = TO_EDEFAULT;

	/**
	 * The default value of the '{@link #getPage() <em>Page</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPage()
	 * @generated
	 * @ordered
	 */
	protected static final int PAGE_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getPage() <em>Page</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPage()
	 * @generated
	 * @ordered
	 */
	protected int page = PAGE_EDEFAULT;

	/**
	 * The default value of the '{@link #getPageSize() <em>Page Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPageSize()
	 * @generated
	 * @ordered
	 */
	protected static final int PAGE_SIZE_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getPageSize() <em>Page Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPageSize()
	 * @generated
	 * @ordered
	 */
	protected int pageSize = PAGE_SIZE_EDEFAULT;

	/**
	 * The default value of the '{@link #isReturnResultSize() <em>Return Result Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isReturnResultSize()
	 * @generated
	 * @ordered
	 */
	protected static final boolean RETURN_RESULT_SIZE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isReturnResultSize() <em>Return Result Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isReturnResultSize()
	 * @generated
	 * @ordered
	 */
	protected boolean returnResultSize = RETURN_RESULT_SIZE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getSorting() <em>Sorting</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSorting()
	 * @generated
	 * @ordered
	 */
	protected EList<Sort> sorting;

	/**
	 * The cached value of the '{@link #getFiltering() <em>Filtering</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFiltering()
	 * @generated
	 * @ordered
	 */
	protected EList<Filter> filtering;

	/**
	 * The cached value of the '{@link #getProjection() <em>Projection</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProjection()
	 * @generated
	 * @ordered
	 */
	protected EList<String> projection;

	/**
	 * The default value of the '{@link #getQuery() <em>Query</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getQuery()
	 * @generated
	 * @ordered
	 */
	protected static final String QUERY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getQuery() <em>Query</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getQuery()
	 * @generated
	 * @ordered
	 */
	protected String query = QUERY_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RequestImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return UtilitiesPackage.Literals.REQUEST;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UtilitiesPackage.REQUEST__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<String> getObjectId() {
		if (objectId == null) {
			objectId = new EDataTypeUniqueEList<String>(String.class, this, UtilitiesPackage.REQUEST__OBJECT_ID);
		}
		return objectId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Date getFrom() {
		return from;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setFrom(Date newFrom) {
		Date oldFrom = from;
		from = newFrom;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UtilitiesPackage.REQUEST__FROM, oldFrom, from));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Date getTo() {
		return to;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTo(Date newTo) {
		Date oldTo = to;
		to = newTo;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UtilitiesPackage.REQUEST__TO, oldTo, to));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getPage() {
		return page;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPage(int newPage) {
		int oldPage = page;
		page = newPage;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UtilitiesPackage.REQUEST__PAGE, oldPage, page));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPageSize(int newPageSize) {
		int oldPageSize = pageSize;
		pageSize = newPageSize;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UtilitiesPackage.REQUEST__PAGE_SIZE, oldPageSize, pageSize));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isReturnResultSize() {
		return returnResultSize;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setReturnResultSize(boolean newReturnResultSize) {
		boolean oldReturnResultSize = returnResultSize;
		returnResultSize = newReturnResultSize;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UtilitiesPackage.REQUEST__RETURN_RESULT_SIZE, oldReturnResultSize, returnResultSize));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Sort> getSorting() {
		if (sorting == null) {
			sorting = new EObjectContainmentEList<Sort>(Sort.class, this, UtilitiesPackage.REQUEST__SORTING);
		}
		return sorting;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Filter> getFiltering() {
		if (filtering == null) {
			filtering = new EObjectContainmentEList<Filter>(Filter.class, this, UtilitiesPackage.REQUEST__FILTERING);
		}
		return filtering;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<String> getProjection() {
		if (projection == null) {
			projection = new EDataTypeUniqueEList<String>(String.class, this, UtilitiesPackage.REQUEST__PROJECTION);
		}
		return projection;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getQuery() {
		return query;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setQuery(String newQuery) {
		String oldQuery = query;
		query = newQuery;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UtilitiesPackage.REQUEST__QUERY, oldQuery, query));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case UtilitiesPackage.REQUEST__SORTING:
				return ((InternalEList<?>)getSorting()).basicRemove(otherEnd, msgs);
			case UtilitiesPackage.REQUEST__FILTERING:
				return ((InternalEList<?>)getFiltering()).basicRemove(otherEnd, msgs);
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
			case UtilitiesPackage.REQUEST__ID:
				return getId();
			case UtilitiesPackage.REQUEST__OBJECT_ID:
				return getObjectId();
			case UtilitiesPackage.REQUEST__FROM:
				return getFrom();
			case UtilitiesPackage.REQUEST__TO:
				return getTo();
			case UtilitiesPackage.REQUEST__PAGE:
				return getPage();
			case UtilitiesPackage.REQUEST__PAGE_SIZE:
				return getPageSize();
			case UtilitiesPackage.REQUEST__RETURN_RESULT_SIZE:
				return isReturnResultSize();
			case UtilitiesPackage.REQUEST__SORTING:
				return getSorting();
			case UtilitiesPackage.REQUEST__FILTERING:
				return getFiltering();
			case UtilitiesPackage.REQUEST__PROJECTION:
				return getProjection();
			case UtilitiesPackage.REQUEST__QUERY:
				return getQuery();
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
			case UtilitiesPackage.REQUEST__ID:
				setId((String)newValue);
				return;
			case UtilitiesPackage.REQUEST__OBJECT_ID:
				getObjectId().clear();
				getObjectId().addAll((Collection<? extends String>)newValue);
				return;
			case UtilitiesPackage.REQUEST__FROM:
				setFrom((Date)newValue);
				return;
			case UtilitiesPackage.REQUEST__TO:
				setTo((Date)newValue);
				return;
			case UtilitiesPackage.REQUEST__PAGE:
				setPage((Integer)newValue);
				return;
			case UtilitiesPackage.REQUEST__PAGE_SIZE:
				setPageSize((Integer)newValue);
				return;
			case UtilitiesPackage.REQUEST__RETURN_RESULT_SIZE:
				setReturnResultSize((Boolean)newValue);
				return;
			case UtilitiesPackage.REQUEST__SORTING:
				getSorting().clear();
				getSorting().addAll((Collection<? extends Sort>)newValue);
				return;
			case UtilitiesPackage.REQUEST__FILTERING:
				getFiltering().clear();
				getFiltering().addAll((Collection<? extends Filter>)newValue);
				return;
			case UtilitiesPackage.REQUEST__PROJECTION:
				getProjection().clear();
				getProjection().addAll((Collection<? extends String>)newValue);
				return;
			case UtilitiesPackage.REQUEST__QUERY:
				setQuery((String)newValue);
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
			case UtilitiesPackage.REQUEST__ID:
				setId(ID_EDEFAULT);
				return;
			case UtilitiesPackage.REQUEST__OBJECT_ID:
				getObjectId().clear();
				return;
			case UtilitiesPackage.REQUEST__FROM:
				setFrom(FROM_EDEFAULT);
				return;
			case UtilitiesPackage.REQUEST__TO:
				setTo(TO_EDEFAULT);
				return;
			case UtilitiesPackage.REQUEST__PAGE:
				setPage(PAGE_EDEFAULT);
				return;
			case UtilitiesPackage.REQUEST__PAGE_SIZE:
				setPageSize(PAGE_SIZE_EDEFAULT);
				return;
			case UtilitiesPackage.REQUEST__RETURN_RESULT_SIZE:
				setReturnResultSize(RETURN_RESULT_SIZE_EDEFAULT);
				return;
			case UtilitiesPackage.REQUEST__SORTING:
				getSorting().clear();
				return;
			case UtilitiesPackage.REQUEST__FILTERING:
				getFiltering().clear();
				return;
			case UtilitiesPackage.REQUEST__PROJECTION:
				getProjection().clear();
				return;
			case UtilitiesPackage.REQUEST__QUERY:
				setQuery(QUERY_EDEFAULT);
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
			case UtilitiesPackage.REQUEST__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case UtilitiesPackage.REQUEST__OBJECT_ID:
				return objectId != null && !objectId.isEmpty();
			case UtilitiesPackage.REQUEST__FROM:
				return FROM_EDEFAULT == null ? from != null : !FROM_EDEFAULT.equals(from);
			case UtilitiesPackage.REQUEST__TO:
				return TO_EDEFAULT == null ? to != null : !TO_EDEFAULT.equals(to);
			case UtilitiesPackage.REQUEST__PAGE:
				return page != PAGE_EDEFAULT;
			case UtilitiesPackage.REQUEST__PAGE_SIZE:
				return pageSize != PAGE_SIZE_EDEFAULT;
			case UtilitiesPackage.REQUEST__RETURN_RESULT_SIZE:
				return returnResultSize != RETURN_RESULT_SIZE_EDEFAULT;
			case UtilitiesPackage.REQUEST__SORTING:
				return sorting != null && !sorting.isEmpty();
			case UtilitiesPackage.REQUEST__FILTERING:
				return filtering != null && !filtering.isEmpty();
			case UtilitiesPackage.REQUEST__PROJECTION:
				return projection != null && !projection.isEmpty();
			case UtilitiesPackage.REQUEST__QUERY:
				return QUERY_EDEFAULT == null ? query != null : !QUERY_EDEFAULT.equals(query);
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
		result.append(" (id: ");
		result.append(id);
		result.append(", objectId: ");
		result.append(objectId);
		result.append(", from: ");
		result.append(from);
		result.append(", to: ");
		result.append(to);
		result.append(", page: ");
		result.append(page);
		result.append(", pageSize: ");
		result.append(pageSize);
		result.append(", returnResultSize: ");
		result.append(returnResultSize);
		result.append(", projection: ");
		result.append(projection);
		result.append(", query: ");
		result.append(query);
		result.append(')');
		return result.toString();
	}

} //RequestImpl
