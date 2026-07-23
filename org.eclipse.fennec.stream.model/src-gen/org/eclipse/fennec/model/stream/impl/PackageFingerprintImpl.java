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

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.model.stream.PackageFingerprint;
import org.eclipse.fennec.model.stream.StreamPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Package Fingerprint</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.PackageFingerprintImpl#getPackageUri <em>Package Uri</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.PackageFingerprintImpl#getPackageFingerprint <em>Package Fingerprint</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PackageFingerprintImpl extends MinimalEObjectImpl.Container implements PackageFingerprint {
	/**
	 * The default value of the '{@link #getPackageUri() <em>Package Uri</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPackageUri()
	 * @generated
	 * @ordered
	 */
	protected static final String PACKAGE_URI_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPackageUri() <em>Package Uri</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPackageUri()
	 * @generated
	 * @ordered
	 */
	protected String packageUri = PACKAGE_URI_EDEFAULT;

	/**
	 * The default value of the '{@link #getPackageFingerprint() <em>Package Fingerprint</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPackageFingerprint()
	 * @generated
	 * @ordered
	 */
	protected static final String PACKAGE_FINGERPRINT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPackageFingerprint() <em>Package Fingerprint</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPackageFingerprint()
	 * @generated
	 * @ordered
	 */
	protected String packageFingerprint = PACKAGE_FINGERPRINT_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PackageFingerprintImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return StreamPackage.Literals.PACKAGE_FINGERPRINT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getPackageUri() {
		return packageUri;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPackageUri(String newPackageUri) {
		String oldPackageUri = packageUri;
		packageUri = newPackageUri;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.PACKAGE_FINGERPRINT__PACKAGE_URI, oldPackageUri, packageUri));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getPackageFingerprint() {
		return packageFingerprint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPackageFingerprint(String newPackageFingerprint) {
		String oldPackageFingerprint = packageFingerprint;
		packageFingerprint = newPackageFingerprint;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.PACKAGE_FINGERPRINT__PACKAGE_FINGERPRINT, oldPackageFingerprint, packageFingerprint));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case StreamPackage.PACKAGE_FINGERPRINT__PACKAGE_URI:
				return getPackageUri();
			case StreamPackage.PACKAGE_FINGERPRINT__PACKAGE_FINGERPRINT:
				return getPackageFingerprint();
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
			case StreamPackage.PACKAGE_FINGERPRINT__PACKAGE_URI:
				setPackageUri((String)newValue);
				return;
			case StreamPackage.PACKAGE_FINGERPRINT__PACKAGE_FINGERPRINT:
				setPackageFingerprint((String)newValue);
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
			case StreamPackage.PACKAGE_FINGERPRINT__PACKAGE_URI:
				setPackageUri(PACKAGE_URI_EDEFAULT);
				return;
			case StreamPackage.PACKAGE_FINGERPRINT__PACKAGE_FINGERPRINT:
				setPackageFingerprint(PACKAGE_FINGERPRINT_EDEFAULT);
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
			case StreamPackage.PACKAGE_FINGERPRINT__PACKAGE_URI:
				return PACKAGE_URI_EDEFAULT == null ? packageUri != null : !PACKAGE_URI_EDEFAULT.equals(packageUri);
			case StreamPackage.PACKAGE_FINGERPRINT__PACKAGE_FINGERPRINT:
				return PACKAGE_FINGERPRINT_EDEFAULT == null ? packageFingerprint != null : !PACKAGE_FINGERPRINT_EDEFAULT.equals(packageFingerprint);
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
		result.append(" (packageUri: ");
		result.append(packageUri);
		result.append(", packageFingerprint: ");
		result.append(packageFingerprint);
		result.append(')');
		return result.toString();
	}

} //PackageFingerprintImpl
