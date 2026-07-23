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
package org.eclipse.fennec.model.stream;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Package Fingerprint</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * One (packageUri, packageFingerprint) pair of a CompositeSnapshot, pointing at a per-package ContextSnapshot.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.stream.PackageFingerprint#getPackageUri <em>Package Uri</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.PackageFingerprint#getPackageFingerprint <em>Package Fingerprint</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.stream.StreamPackage#getPackageFingerprint()
 * @model
 * @generated
 */
@ProviderType
public interface PackageFingerprint extends EObject {
	/**
	 * Returns the value of the '<em><b>Package Uri</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * nsURI of the EPackage.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Package Uri</em>' attribute.
	 * @see #setPackageUri(String)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getPackageFingerprint_PackageUri()
	 * @model required="true"
	 * @generated
	 */
	String getPackageUri();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.PackageFingerprint#getPackageUri <em>Package Uri</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Package Uri</em>' attribute.
	 * @see #getPackageUri()
	 * @generated
	 */
	void setPackageUri(String value);

	/**
	 * Returns the value of the '<em><b>Package Fingerprint</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Fingerprint of the package's ContextSnapshot in effect.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Package Fingerprint</em>' attribute.
	 * @see #setPackageFingerprint(String)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getPackageFingerprint_PackageFingerprint()
	 * @model required="true"
	 * @generated
	 */
	String getPackageFingerprint();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.PackageFingerprint#getPackageFingerprint <em>Package Fingerprint</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Package Fingerprint</em>' attribute.
	 * @see #getPackageFingerprint()
	 * @generated
	 */
	void setPackageFingerprint(String value);

} // PackageFingerprint
