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
package org.eclipse.fennec.persistence.eorm.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.fennec.persistence.eorm.Convert;
import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.FetchType;
import org.eclipse.fennec.persistence.eorm.Lob;
import org.eclipse.fennec.persistence.eorm.SimpleBase;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Simple Base</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.SimpleBaseImpl#getLob <em>Lob</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.SimpleBaseImpl#getConvert <em>Convert</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.SimpleBaseImpl#getFetch <em>Fetch</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.SimpleBaseImpl#getEnumerated <em>Enumerated</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SimpleBaseImpl extends BaseImpl implements SimpleBase {
	/**
	 * The cached value of the '{@link #getLob() <em>Lob</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLob()
	 * @generated
	 * @ordered
	 */
	protected Lob lob;

	/**
	 * The cached value of the '{@link #getConvert() <em>Convert</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConvert()
	 * @generated
	 * @ordered
	 */
	protected Convert convert;

	/**
	 * The default value of the '{@link #getFetch() <em>Fetch</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFetch()
	 * @generated
	 * @ordered
	 */
	protected static final FetchType FETCH_EDEFAULT = FetchType.LAZY;

	/**
	 * The cached value of the '{@link #getFetch() <em>Fetch</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFetch()
	 * @generated
	 * @ordered
	 */
	protected FetchType fetch = FETCH_EDEFAULT;

	/**
	 * This is true if the Fetch attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean fetchESet;

	/**
	 * The cached value of the '{@link #getEnumerated() <em>Enumerated</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEnumerated()
	 * @generated
	 * @ordered
	 */
	protected EEnum enumerated;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SimpleBaseImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EORMPackage.eINSTANCE.getSimpleBase();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Lob getLob() {
		return lob;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetLob(Lob newLob, NotificationChain msgs) {
		Lob oldLob = lob;
		lob = newLob;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.SIMPLE_BASE__LOB, oldLob, newLob);
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
	public void setLob(Lob newLob) {
		if (newLob != lob) {
			NotificationChain msgs = null;
			if (lob != null)
				msgs = ((InternalEObject)lob).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.SIMPLE_BASE__LOB, null, msgs);
			if (newLob != null)
				msgs = ((InternalEObject)newLob).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.SIMPLE_BASE__LOB, null, msgs);
			msgs = basicSetLob(newLob, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.SIMPLE_BASE__LOB, newLob, newLob));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Convert getConvert() {
		return convert;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetConvert(Convert newConvert, NotificationChain msgs) {
		Convert oldConvert = convert;
		convert = newConvert;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.SIMPLE_BASE__CONVERT, oldConvert, newConvert);
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
	public void setConvert(Convert newConvert) {
		if (newConvert != convert) {
			NotificationChain msgs = null;
			if (convert != null)
				msgs = ((InternalEObject)convert).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.SIMPLE_BASE__CONVERT, null, msgs);
			if (newConvert != null)
				msgs = ((InternalEObject)newConvert).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.SIMPLE_BASE__CONVERT, null, msgs);
			msgs = basicSetConvert(newConvert, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.SIMPLE_BASE__CONVERT, newConvert, newConvert));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public FetchType getFetch() {
		return fetch;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setFetch(FetchType newFetch) {
		FetchType oldFetch = fetch;
		fetch = newFetch == null ? FETCH_EDEFAULT : newFetch;
		boolean oldFetchESet = fetchESet;
		fetchESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.SIMPLE_BASE__FETCH, oldFetch, fetch, !oldFetchESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetFetch() {
		FetchType oldFetch = fetch;
		boolean oldFetchESet = fetchESet;
		fetch = FETCH_EDEFAULT;
		fetchESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EORMPackage.SIMPLE_BASE__FETCH, oldFetch, FETCH_EDEFAULT, oldFetchESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetFetch() {
		return fetchESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getEnumerated() {
		if (enumerated != null && enumerated.eIsProxy()) {
			InternalEObject oldEnumerated = (InternalEObject)enumerated;
			enumerated = (EEnum)eResolveProxy(oldEnumerated);
			if (enumerated != oldEnumerated) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, EORMPackage.SIMPLE_BASE__ENUMERATED, oldEnumerated, enumerated));
			}
		}
		return enumerated;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum basicGetEnumerated() {
		return enumerated;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setEnumerated(EEnum newEnumerated) {
		EEnum oldEnumerated = enumerated;
		enumerated = newEnumerated;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.SIMPLE_BASE__ENUMERATED, oldEnumerated, enumerated));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EORMPackage.SIMPLE_BASE__LOB:
				return basicSetLob(null, msgs);
			case EORMPackage.SIMPLE_BASE__CONVERT:
				return basicSetConvert(null, msgs);
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
			case EORMPackage.SIMPLE_BASE__LOB:
				return getLob();
			case EORMPackage.SIMPLE_BASE__CONVERT:
				return getConvert();
			case EORMPackage.SIMPLE_BASE__FETCH:
				return getFetch();
			case EORMPackage.SIMPLE_BASE__ENUMERATED:
				if (resolve) return getEnumerated();
				return basicGetEnumerated();
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
			case EORMPackage.SIMPLE_BASE__LOB:
				setLob((Lob)newValue);
				return;
			case EORMPackage.SIMPLE_BASE__CONVERT:
				setConvert((Convert)newValue);
				return;
			case EORMPackage.SIMPLE_BASE__FETCH:
				setFetch((FetchType)newValue);
				return;
			case EORMPackage.SIMPLE_BASE__ENUMERATED:
				setEnumerated((EEnum)newValue);
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
			case EORMPackage.SIMPLE_BASE__LOB:
				setLob((Lob)null);
				return;
			case EORMPackage.SIMPLE_BASE__CONVERT:
				setConvert((Convert)null);
				return;
			case EORMPackage.SIMPLE_BASE__FETCH:
				unsetFetch();
				return;
			case EORMPackage.SIMPLE_BASE__ENUMERATED:
				setEnumerated((EEnum)null);
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
			case EORMPackage.SIMPLE_BASE__LOB:
				return lob != null;
			case EORMPackage.SIMPLE_BASE__CONVERT:
				return convert != null;
			case EORMPackage.SIMPLE_BASE__FETCH:
				return isSetFetch();
			case EORMPackage.SIMPLE_BASE__ENUMERATED:
				return enumerated != null;
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
		result.append(" (fetch: ");
		if (fetchESet) result.append(fetch); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //SimpleBaseImpl
