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

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Composite Snapshot</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Deployment-level chain link (concept §6.4, §22 decision #2): the ordered list of (packageUri, packageFingerprint) pairs of all packages in scope, content-addressed under its fingerprint (the composite root) and predecessor-chained. THIS is what ChangeSet.contextFingerprint references. Per-package ContextSnapshots remain the units of evolution; the composite merely references their states. Single-package deployments degenerate to one entry.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.stream.CompositeSnapshot#getFingerprint <em>Fingerprint</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.CompositeSnapshot#getPredecessorFingerprint <em>Predecessor Fingerprint</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.CompositeSnapshot#getCreated <em>Created</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.CompositeSnapshot#getPackages <em>Packages</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.stream.StreamPackage#getCompositeSnapshot()
 * @model
 * @generated
 */
@ProviderType
public interface CompositeSnapshot extends EObject {
	/**
	 * Returns the value of the '<em><b>Fingerprint</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The composite root: reproducible hash over the canonical pair list; also the storage key.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Fingerprint</em>' attribute.
	 * @see #setFingerprint(String)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getCompositeSnapshot_Fingerprint()
	 * @model id="true" required="true"
	 * @generated
	 */
	String getFingerprint();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.CompositeSnapshot#getFingerprint <em>Fingerprint</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Fingerprint</em>' attribute.
	 * @see #getFingerprint()
	 * @generated
	 */
	void setFingerprint(String value);

	/**
	 * Returns the value of the '<em><b>Predecessor Fingerprint</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Fingerprint of the predecessor composite state. Absent only on the genesis composite.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Predecessor Fingerprint</em>' attribute.
	 * @see #setPredecessorFingerprint(String)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getCompositeSnapshot_PredecessorFingerprint()
	 * @model
	 * @generated
	 */
	String getPredecessorFingerprint();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.CompositeSnapshot#getPredecessorFingerprint <em>Predecessor Fingerprint</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Predecessor Fingerprint</em>' attribute.
	 * @see #getPredecessorFingerprint()
	 * @generated
	 */
	void setPredecessorFingerprint(String value);

	/**
	 * Returns the value of the '<em><b>Created</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Creation time (epoch millis). Informational — ordering comes from the predecessor chain.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Created</em>' attribute.
	 * @see #setCreated(long)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getCompositeSnapshot_Created()
	 * @model
	 * @generated
	 */
	long getCreated();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.CompositeSnapshot#getCreated <em>Created</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Created</em>' attribute.
	 * @see #getCreated()
	 * @generated
	 */
	void setCreated(long value);

	/**
	 * Returns the value of the '<em><b>Packages</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.stream.PackageFingerprint}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The per-package states this composite references, in canonical order (part of the hashed content).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Packages</em>' containment reference list.
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getCompositeSnapshot_Packages()
	 * @model containment="true"
	 * @generated
	 */
	EList<PackageFingerprint> getPackages();

} // CompositeSnapshot
