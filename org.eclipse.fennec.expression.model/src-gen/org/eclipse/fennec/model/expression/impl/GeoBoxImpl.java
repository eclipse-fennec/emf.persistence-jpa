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

import org.eclipse.fennec.model.expression.ExpressionPackage;
import org.eclipse.fennec.model.expression.GeoBox;
import org.eclipse.fennec.model.expression.GeoPointLiteral;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Geo Box</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.GeoBoxImpl#getSouthWest <em>South West</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.GeoBoxImpl#getNorthEast <em>North East</em>}</li>
 * </ul>
 *
 * @generated
 */
public class GeoBoxImpl extends GeoShapeImpl implements GeoBox {
	/**
	 * The cached value of the '{@link #getSouthWest() <em>South West</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSouthWest()
	 * @generated
	 * @ordered
	 */
	protected GeoPointLiteral southWest;

	/**
	 * The cached value of the '{@link #getNorthEast() <em>North East</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNorthEast()
	 * @generated
	 * @ordered
	 */
	protected GeoPointLiteral northEast;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected GeoBoxImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ExpressionPackage.Literals.GEO_BOX;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public GeoPointLiteral getSouthWest() {
		return southWest;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSouthWest(GeoPointLiteral newSouthWest, NotificationChain msgs) {
		GeoPointLiteral oldSouthWest = southWest;
		southWest = newSouthWest;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ExpressionPackage.GEO_BOX__SOUTH_WEST, oldSouthWest, newSouthWest);
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
	public void setSouthWest(GeoPointLiteral newSouthWest) {
		if (newSouthWest != southWest) {
			NotificationChain msgs = null;
			if (southWest != null)
				msgs = ((InternalEObject)southWest).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.GEO_BOX__SOUTH_WEST, null, msgs);
			if (newSouthWest != null)
				msgs = ((InternalEObject)newSouthWest).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.GEO_BOX__SOUTH_WEST, null, msgs);
			msgs = basicSetSouthWest(newSouthWest, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.GEO_BOX__SOUTH_WEST, newSouthWest, newSouthWest));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public GeoPointLiteral getNorthEast() {
		return northEast;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetNorthEast(GeoPointLiteral newNorthEast, NotificationChain msgs) {
		GeoPointLiteral oldNorthEast = northEast;
		northEast = newNorthEast;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ExpressionPackage.GEO_BOX__NORTH_EAST, oldNorthEast, newNorthEast);
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
	public void setNorthEast(GeoPointLiteral newNorthEast) {
		if (newNorthEast != northEast) {
			NotificationChain msgs = null;
			if (northEast != null)
				msgs = ((InternalEObject)northEast).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.GEO_BOX__NORTH_EAST, null, msgs);
			if (newNorthEast != null)
				msgs = ((InternalEObject)newNorthEast).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.GEO_BOX__NORTH_EAST, null, msgs);
			msgs = basicSetNorthEast(newNorthEast, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.GEO_BOX__NORTH_EAST, newNorthEast, newNorthEast));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ExpressionPackage.GEO_BOX__SOUTH_WEST:
				return basicSetSouthWest(null, msgs);
			case ExpressionPackage.GEO_BOX__NORTH_EAST:
				return basicSetNorthEast(null, msgs);
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
			case ExpressionPackage.GEO_BOX__SOUTH_WEST:
				return getSouthWest();
			case ExpressionPackage.GEO_BOX__NORTH_EAST:
				return getNorthEast();
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
			case ExpressionPackage.GEO_BOX__SOUTH_WEST:
				setSouthWest((GeoPointLiteral)newValue);
				return;
			case ExpressionPackage.GEO_BOX__NORTH_EAST:
				setNorthEast((GeoPointLiteral)newValue);
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
			case ExpressionPackage.GEO_BOX__SOUTH_WEST:
				setSouthWest((GeoPointLiteral)null);
				return;
			case ExpressionPackage.GEO_BOX__NORTH_EAST:
				setNorthEast((GeoPointLiteral)null);
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
			case ExpressionPackage.GEO_BOX__SOUTH_WEST:
				return southWest != null;
			case ExpressionPackage.GEO_BOX__NORTH_EAST:
				return northEast != null;
		}
		return super.eIsSet(featureID);
	}

} //GeoBoxImpl
