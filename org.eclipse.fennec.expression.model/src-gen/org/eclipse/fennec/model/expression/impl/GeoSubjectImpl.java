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
package org.eclipse.fennec.model.expression.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.model.expression.ExpressionPackage;
import org.eclipse.fennec.model.expression.GeoSubject;
import org.eclipse.fennec.model.expression.PropertyPath;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Geo Subject</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.GeoSubjectImpl#getPathLat <em>Path Lat</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.GeoSubjectImpl#getPathLon <em>Path Lon</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.GeoSubjectImpl#getPathPoint <em>Path Point</em>}</li>
 * </ul>
 *
 * @generated
 */
public class GeoSubjectImpl extends MinimalEObjectImpl.Container implements GeoSubject {
	/**
	 * The cached value of the '{@link #getPathLat() <em>Path Lat</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPathLat()
	 * @generated
	 * @ordered
	 */
	protected PropertyPath pathLat;

	/**
	 * The cached value of the '{@link #getPathLon() <em>Path Lon</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPathLon()
	 * @generated
	 * @ordered
	 */
	protected PropertyPath pathLon;

	/**
	 * The cached value of the '{@link #getPathPoint() <em>Path Point</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPathPoint()
	 * @generated
	 * @ordered
	 */
	protected PropertyPath pathPoint;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected GeoSubjectImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ExpressionPackage.Literals.GEO_SUBJECT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PropertyPath getPathLat() {
		return pathLat;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPathLat(PropertyPath newPathLat, NotificationChain msgs) {
		PropertyPath oldPathLat = pathLat;
		pathLat = newPathLat;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ExpressionPackage.GEO_SUBJECT__PATH_LAT, oldPathLat, newPathLat);
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
	public void setPathLat(PropertyPath newPathLat) {
		if (newPathLat != pathLat) {
			NotificationChain msgs = null;
			if (pathLat != null)
				msgs = ((InternalEObject)pathLat).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.GEO_SUBJECT__PATH_LAT, null, msgs);
			if (newPathLat != null)
				msgs = ((InternalEObject)newPathLat).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.GEO_SUBJECT__PATH_LAT, null, msgs);
			msgs = basicSetPathLat(newPathLat, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.GEO_SUBJECT__PATH_LAT, newPathLat, newPathLat));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PropertyPath getPathLon() {
		return pathLon;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPathLon(PropertyPath newPathLon, NotificationChain msgs) {
		PropertyPath oldPathLon = pathLon;
		pathLon = newPathLon;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ExpressionPackage.GEO_SUBJECT__PATH_LON, oldPathLon, newPathLon);
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
	public void setPathLon(PropertyPath newPathLon) {
		if (newPathLon != pathLon) {
			NotificationChain msgs = null;
			if (pathLon != null)
				msgs = ((InternalEObject)pathLon).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.GEO_SUBJECT__PATH_LON, null, msgs);
			if (newPathLon != null)
				msgs = ((InternalEObject)newPathLon).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.GEO_SUBJECT__PATH_LON, null, msgs);
			msgs = basicSetPathLon(newPathLon, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.GEO_SUBJECT__PATH_LON, newPathLon, newPathLon));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PropertyPath getPathPoint() {
		return pathPoint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPathPoint(PropertyPath newPathPoint, NotificationChain msgs) {
		PropertyPath oldPathPoint = pathPoint;
		pathPoint = newPathPoint;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ExpressionPackage.GEO_SUBJECT__PATH_POINT, oldPathPoint, newPathPoint);
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
	public void setPathPoint(PropertyPath newPathPoint) {
		if (newPathPoint != pathPoint) {
			NotificationChain msgs = null;
			if (pathPoint != null)
				msgs = ((InternalEObject)pathPoint).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.GEO_SUBJECT__PATH_POINT, null, msgs);
			if (newPathPoint != null)
				msgs = ((InternalEObject)newPathPoint).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.GEO_SUBJECT__PATH_POINT, null, msgs);
			msgs = basicSetPathPoint(newPathPoint, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.GEO_SUBJECT__PATH_POINT, newPathPoint, newPathPoint));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ExpressionPackage.GEO_SUBJECT__PATH_LAT:
				return basicSetPathLat(null, msgs);
			case ExpressionPackage.GEO_SUBJECT__PATH_LON:
				return basicSetPathLon(null, msgs);
			case ExpressionPackage.GEO_SUBJECT__PATH_POINT:
				return basicSetPathPoint(null, msgs);
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
			case ExpressionPackage.GEO_SUBJECT__PATH_LAT:
				return getPathLat();
			case ExpressionPackage.GEO_SUBJECT__PATH_LON:
				return getPathLon();
			case ExpressionPackage.GEO_SUBJECT__PATH_POINT:
				return getPathPoint();
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
			case ExpressionPackage.GEO_SUBJECT__PATH_LAT:
				setPathLat((PropertyPath)newValue);
				return;
			case ExpressionPackage.GEO_SUBJECT__PATH_LON:
				setPathLon((PropertyPath)newValue);
				return;
			case ExpressionPackage.GEO_SUBJECT__PATH_POINT:
				setPathPoint((PropertyPath)newValue);
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
			case ExpressionPackage.GEO_SUBJECT__PATH_LAT:
				setPathLat((PropertyPath)null);
				return;
			case ExpressionPackage.GEO_SUBJECT__PATH_LON:
				setPathLon((PropertyPath)null);
				return;
			case ExpressionPackage.GEO_SUBJECT__PATH_POINT:
				setPathPoint((PropertyPath)null);
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
			case ExpressionPackage.GEO_SUBJECT__PATH_LAT:
				return pathLat != null;
			case ExpressionPackage.GEO_SUBJECT__PATH_LON:
				return pathLon != null;
			case ExpressionPackage.GEO_SUBJECT__PATH_POINT:
				return pathPoint != null;
		}
		return super.eIsSet(featureID);
	}

} //GeoSubjectImpl
