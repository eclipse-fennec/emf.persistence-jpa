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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.model.stream.ContextSnapshot;
import org.eclipse.fennec.model.stream.StreamPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Context Snapshot</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ContextSnapshotImpl#getFingerprint <em>Fingerprint</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ContextSnapshotImpl#getPredecessorFingerprint <em>Predecessor Fingerprint</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ContextSnapshotImpl#getCreated <em>Created</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ContextSnapshotImpl#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ContextSnapshotImpl#getLabels <em>Labels</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ContextSnapshotImpl#getPackageUri <em>Package Uri</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ContextSnapshotImpl#getContent <em>Content</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ContextSnapshotImpl extends MinimalEObjectImpl.Container implements ContextSnapshot {
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
	 * The default value of the '{@link #getAuthor() <em>Author</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAuthor()
	 * @generated
	 * @ordered
	 */
	protected static final String AUTHOR_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAuthor() <em>Author</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAuthor()
	 * @generated
	 * @ordered
	 */
	protected String author = AUTHOR_EDEFAULT;

	/**
	 * The cached value of the '{@link #getLabels() <em>Labels</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLabels()
	 * @generated
	 * @ordered
	 */
	protected EList<String> labels;

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
	 * The cached value of the '{@link #getContent() <em>Content</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContent()
	 * @generated
	 * @ordered
	 */
	protected EList<EObject> content;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ContextSnapshotImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return StreamPackage.Literals.CONTEXT_SNAPSHOT;
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
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.CONTEXT_SNAPSHOT__FINGERPRINT, oldFingerprint, fingerprint));
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
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.CONTEXT_SNAPSHOT__PREDECESSOR_FINGERPRINT, oldPredecessorFingerprint, predecessorFingerprint));
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
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.CONTEXT_SNAPSHOT__CREATED, oldCreated, created));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getAuthor() {
		return author;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAuthor(String newAuthor) {
		String oldAuthor = author;
		author = newAuthor;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.CONTEXT_SNAPSHOT__AUTHOR, oldAuthor, author));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<String> getLabels() {
		if (labels == null) {
			labels = new EDataTypeUniqueEList<String>(String.class, this, StreamPackage.CONTEXT_SNAPSHOT__LABELS);
		}
		return labels;
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
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.CONTEXT_SNAPSHOT__PACKAGE_URI, oldPackageUri, packageUri));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<EObject> getContent() {
		if (content == null) {
			content = new EObjectContainmentEList<EObject>(EObject.class, this, StreamPackage.CONTEXT_SNAPSHOT__CONTENT);
		}
		return content;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case StreamPackage.CONTEXT_SNAPSHOT__CONTENT:
				return ((InternalEList<?>)getContent()).basicRemove(otherEnd, msgs);
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
			case StreamPackage.CONTEXT_SNAPSHOT__FINGERPRINT:
				return getFingerprint();
			case StreamPackage.CONTEXT_SNAPSHOT__PREDECESSOR_FINGERPRINT:
				return getPredecessorFingerprint();
			case StreamPackage.CONTEXT_SNAPSHOT__CREATED:
				return getCreated();
			case StreamPackage.CONTEXT_SNAPSHOT__AUTHOR:
				return getAuthor();
			case StreamPackage.CONTEXT_SNAPSHOT__LABELS:
				return getLabels();
			case StreamPackage.CONTEXT_SNAPSHOT__PACKAGE_URI:
				return getPackageUri();
			case StreamPackage.CONTEXT_SNAPSHOT__CONTENT:
				return getContent();
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
			case StreamPackage.CONTEXT_SNAPSHOT__FINGERPRINT:
				setFingerprint((String)newValue);
				return;
			case StreamPackage.CONTEXT_SNAPSHOT__PREDECESSOR_FINGERPRINT:
				setPredecessorFingerprint((String)newValue);
				return;
			case StreamPackage.CONTEXT_SNAPSHOT__CREATED:
				setCreated((Long)newValue);
				return;
			case StreamPackage.CONTEXT_SNAPSHOT__AUTHOR:
				setAuthor((String)newValue);
				return;
			case StreamPackage.CONTEXT_SNAPSHOT__LABELS:
				getLabels().clear();
				getLabels().addAll((Collection<? extends String>)newValue);
				return;
			case StreamPackage.CONTEXT_SNAPSHOT__PACKAGE_URI:
				setPackageUri((String)newValue);
				return;
			case StreamPackage.CONTEXT_SNAPSHOT__CONTENT:
				getContent().clear();
				getContent().addAll((Collection<? extends EObject>)newValue);
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
			case StreamPackage.CONTEXT_SNAPSHOT__FINGERPRINT:
				setFingerprint(FINGERPRINT_EDEFAULT);
				return;
			case StreamPackage.CONTEXT_SNAPSHOT__PREDECESSOR_FINGERPRINT:
				setPredecessorFingerprint(PREDECESSOR_FINGERPRINT_EDEFAULT);
				return;
			case StreamPackage.CONTEXT_SNAPSHOT__CREATED:
				setCreated(CREATED_EDEFAULT);
				return;
			case StreamPackage.CONTEXT_SNAPSHOT__AUTHOR:
				setAuthor(AUTHOR_EDEFAULT);
				return;
			case StreamPackage.CONTEXT_SNAPSHOT__LABELS:
				getLabels().clear();
				return;
			case StreamPackage.CONTEXT_SNAPSHOT__PACKAGE_URI:
				setPackageUri(PACKAGE_URI_EDEFAULT);
				return;
			case StreamPackage.CONTEXT_SNAPSHOT__CONTENT:
				getContent().clear();
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
			case StreamPackage.CONTEXT_SNAPSHOT__FINGERPRINT:
				return FINGERPRINT_EDEFAULT == null ? fingerprint != null : !FINGERPRINT_EDEFAULT.equals(fingerprint);
			case StreamPackage.CONTEXT_SNAPSHOT__PREDECESSOR_FINGERPRINT:
				return PREDECESSOR_FINGERPRINT_EDEFAULT == null ? predecessorFingerprint != null : !PREDECESSOR_FINGERPRINT_EDEFAULT.equals(predecessorFingerprint);
			case StreamPackage.CONTEXT_SNAPSHOT__CREATED:
				return created != CREATED_EDEFAULT;
			case StreamPackage.CONTEXT_SNAPSHOT__AUTHOR:
				return AUTHOR_EDEFAULT == null ? author != null : !AUTHOR_EDEFAULT.equals(author);
			case StreamPackage.CONTEXT_SNAPSHOT__LABELS:
				return labels != null && !labels.isEmpty();
			case StreamPackage.CONTEXT_SNAPSHOT__PACKAGE_URI:
				return PACKAGE_URI_EDEFAULT == null ? packageUri != null : !PACKAGE_URI_EDEFAULT.equals(packageUri);
			case StreamPackage.CONTEXT_SNAPSHOT__CONTENT:
				return content != null && !content.isEmpty();
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
		result.append(", author: ");
		result.append(author);
		result.append(", labels: ");
		result.append(labels);
		result.append(", packageUri: ");
		result.append(packageUri);
		result.append(')');
		return result.toString();
	}

} //ContextSnapshotImpl
