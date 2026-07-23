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
 * A representation of the model object '<em><b>Context Snapshot</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * One PER-PACKAGE state of the interpretation context, stored content-addressed under its fingerprint and linking its predecessor — the chain IS the model stream (§6.4). Travels in-band in the same envelope as ChangeSets (§12.2). Merkle internals are FingerprintService implementation, not wire model. Referenced from CompositeSnapshot; per-package chains remain the units of evolution and governance.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.stream.ContextSnapshot#getFingerprint <em>Fingerprint</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ContextSnapshot#getPredecessorFingerprint <em>Predecessor Fingerprint</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ContextSnapshot#getCreated <em>Created</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ContextSnapshot#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ContextSnapshot#getLabels <em>Labels</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ContextSnapshot#getPackageUri <em>Package Uri</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ContextSnapshot#getContent <em>Content</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.stream.StreamPackage#getContextSnapshot()
 * @model
 * @generated
 */
@ProviderType
public interface ContextSnapshot extends EObject {
	/**
	 * Returns the value of the '<em><b>Fingerprint</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The fingerprint of this context state: reproducible, identifying, resolvable (§6.2). Computed by the FingerprintService from the canonical form of 'content'; also the storage key (content-addressed, §6.4).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Fingerprint</em>' attribute.
	 * @see #setFingerprint(String)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getContextSnapshot_Fingerprint()
	 * @model id="true" required="true"
	 * @generated
	 */
	String getFingerprint();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.ContextSnapshot#getFingerprint <em>Fingerprint</em>}' attribute.
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
	 * Fingerprint of the predecessor state (git-commit principle). Absent only on the genesis snapshot (§6.4).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Predecessor Fingerprint</em>' attribute.
	 * @see #setPredecessorFingerprint(String)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getContextSnapshot_PredecessorFingerprint()
	 * @model
	 * @generated
	 */
	String getPredecessorFingerprint();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.ContextSnapshot#getPredecessorFingerprint <em>Predecessor Fingerprint</em>}' attribute.
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
	 * Creation time of this snapshot (epoch millis). Informational — ordering comes from the predecessor chain, never from timestamps.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Created</em>' attribute.
	 * @see #setCreated(long)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getContextSnapshot_Created()
	 * @model
	 * @generated
	 */
	long getCreated();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.ContextSnapshot#getCreated <em>Created</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Created</em>' attribute.
	 * @see #getCreated()
	 * @generated
	 */
	void setCreated(long value);

	/**
	 * Returns the value of the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Provenance: who/what produced this context change (modeler, migration tool, import).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Author</em>' attribute.
	 * @see #setAuthor(String)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getContextSnapshot_Author()
	 * @model
	 * @generated
	 */
	String getAuthor();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.ContextSnapshot#getAuthor <em>Author</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Author</em>' attribute.
	 * @see #getAuthor()
	 * @generated
	 */
	void setAuthor(String value);

	/**
	 * Returns the value of the '<em><b>Labels</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Human-readable tags on this chain position (release names like '1.2') — git-tag principle (§6.4).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Labels</em>' attribute list.
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getContextSnapshot_Labels()
	 * @model
	 * @generated
	 */
	EList<String> getLabels();

	/**
	 * Returns the value of the '<em><b>Package Uri</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * nsURI of the EPackage this context describes (chain granularity is per EPackage, §6.4/§15).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Package Uri</em>' attribute.
	 * @see #setPackageUri(String)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getContextSnapshot_PackageUri()
	 * @model required="true"
	 * @generated
	 */
	String getPackageUri();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.ContextSnapshot#getPackageUri <em>Package Uri</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Package Uri</em>' attribute.
	 * @see #getPackageUri()
	 * @generated
	 */
	void setPackageUri(String value);

	/**
	 * Returns the value of the '<em><b>Content</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.EObject}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The interpretation context itself: the Ecore model plus all log-relevant aspect models (tracking incl. id bindings, units, value-affecting mappings — §17.3). Arbitrary EMF content, canonicalized by the FingerprintService.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Content</em>' containment reference list.
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getContextSnapshot_Content()
	 * @model containment="true"
	 * @generated
	 */
	EList<EObject> getContent();

} // ContextSnapshot
