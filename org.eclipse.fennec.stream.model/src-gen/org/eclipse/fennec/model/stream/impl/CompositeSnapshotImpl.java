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
package org.eclipse.fennec.model.stream.impl;

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

import org.eclipse.fennec.model.stream.CompositeSnapshot;
import org.eclipse.fennec.model.stream.PackageFingerprint;
import org.eclipse.fennec.model.stream.StreamPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Composite Snapshot</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.CompositeSnapshotImpl#getFingerprint <em>Fingerprint</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.CompositeSnapshotImpl#getPredecessorFingerprint <em>Predecessor Fingerprint</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.CompositeSnapshotImpl#getCreated <em>Created</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.CompositeSnapshotImpl#getPackages <em>Packages</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CompositeSnapshotImpl extends MinimalEObjectImpl.Container implements CompositeSnapshot {
	/**
	 * The default value of the '{@link #getFingerprint() <em>Fingerprint</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFingerprint()
	 * @generated
	 * @ordered
	 */
	protected static final String FINGERPRINT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFingerprint() <em>Fingerprint</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFingerprint()
	 * @generated
	 * @ordered
	 */
	protected String fingerprint = FINGERPRINT_EDEFAULT;

	/**
	 * The default value of the '{@link #getPredecessorFingerprint() <em>Predecessor Fingerprint</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPredecessorFingerprint()
	 * @generated
	 * @ordered
	 */
	protected static final String PREDECESSOR_FINGERPRINT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPredecessorFingerprint() <em>Predecessor Fingerprint</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPredecessorFingerprint()
	 * @generated
	 * @ordered
	 */
	protected String predecessorFingerprint = PREDECESSOR_FINGERPRINT_EDEFAULT;

	/**
	 * The default value of the '{@link #getCreated() <em>Created</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCreated()
	 * @generated
	 * @ordered
	 */
	protected static final long CREATED_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getCreated() <em>Created</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCreated()
	 * @generated
	 * @ordered
	 */
	protected long created = CREATED_EDEFAULT;

	/**
	 * The cached value of the '{@link #getPackages() <em>Packages</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPackages()
	 * @generated
	 * @ordered
	 */
	protected EList<PackageFingerprint> packages;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CompositeSnapshotImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return StreamPackage.Literals.COMPOSITE_SNAPSHOT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getFingerprint() {
		return fingerprint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setFingerprint(String newFingerprint) {
		String oldFingerprint = fingerprint;
		fingerprint = newFingerprint;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.COMPOSITE_SNAPSHOT__FINGERPRINT, oldFingerprint, fingerprint));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getPredecessorFingerprint() {
		return predecessorFingerprint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPredecessorFingerprint(String newPredecessorFingerprint) {
		String oldPredecessorFingerprint = predecessorFingerprint;
		predecessorFingerprint = newPredecessorFingerprint;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.COMPOSITE_SNAPSHOT__PREDECESSOR_FINGERPRINT, oldPredecessorFingerprint, predecessorFingerprint));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public long getCreated() {
		return created;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setCreated(long newCreated) {
		long oldCreated = created;
		created = newCreated;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.COMPOSITE_SNAPSHOT__CREATED, oldCreated, created));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<PackageFingerprint> getPackages() {
		if (packages == null) {
			packages = new EObjectContainmentEList<PackageFingerprint>(PackageFingerprint.class, this, StreamPackage.COMPOSITE_SNAPSHOT__PACKAGES);
		}
		return packages;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case StreamPackage.COMPOSITE_SNAPSHOT__PACKAGES:
				return ((InternalEList<?>)getPackages()).basicRemove(otherEnd, msgs);
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
			case StreamPackage.COMPOSITE_SNAPSHOT__FINGERPRINT:
				return getFingerprint();
			case StreamPackage.COMPOSITE_SNAPSHOT__PREDECESSOR_FINGERPRINT:
				return getPredecessorFingerprint();
			case StreamPackage.COMPOSITE_SNAPSHOT__CREATED:
				return getCreated();
			case StreamPackage.COMPOSITE_SNAPSHOT__PACKAGES:
				return getPackages();
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
			case StreamPackage.COMPOSITE_SNAPSHOT__FINGERPRINT:
				setFingerprint((String)newValue);
				return;
			case StreamPackage.COMPOSITE_SNAPSHOT__PREDECESSOR_FINGERPRINT:
				setPredecessorFingerprint((String)newValue);
				return;
			case StreamPackage.COMPOSITE_SNAPSHOT__CREATED:
				setCreated((Long)newValue);
				return;
			case StreamPackage.COMPOSITE_SNAPSHOT__PACKAGES:
				getPackages().clear();
				getPackages().addAll((Collection<? extends PackageFingerprint>)newValue);
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
			case StreamPackage.COMPOSITE_SNAPSHOT__FINGERPRINT:
				setFingerprint(FINGERPRINT_EDEFAULT);
				return;
			case StreamPackage.COMPOSITE_SNAPSHOT__PREDECESSOR_FINGERPRINT:
				setPredecessorFingerprint(PREDECESSOR_FINGERPRINT_EDEFAULT);
				return;
			case StreamPackage.COMPOSITE_SNAPSHOT__CREATED:
				setCreated(CREATED_EDEFAULT);
				return;
			case StreamPackage.COMPOSITE_SNAPSHOT__PACKAGES:
				getPackages().clear();
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
			case StreamPackage.COMPOSITE_SNAPSHOT__FINGERPRINT:
				return FINGERPRINT_EDEFAULT == null ? fingerprint != null : !FINGERPRINT_EDEFAULT.equals(fingerprint);
			case StreamPackage.COMPOSITE_SNAPSHOT__PREDECESSOR_FINGERPRINT:
				return PREDECESSOR_FINGERPRINT_EDEFAULT == null ? predecessorFingerprint != null : !PREDECESSOR_FINGERPRINT_EDEFAULT.equals(predecessorFingerprint);
			case StreamPackage.COMPOSITE_SNAPSHOT__CREATED:
				return created != CREATED_EDEFAULT;
			case StreamPackage.COMPOSITE_SNAPSHOT__PACKAGES:
				return packages != null && !packages.isEmpty();
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
		result.append(" (fingerprint: ");
		result.append(fingerprint);
		result.append(", predecessorFingerprint: ");
		result.append(predecessorFingerprint);
		result.append(", created: ");
		result.append(created);
		result.append(')');
		return result.toString();
	}

} //CompositeSnapshotImpl
