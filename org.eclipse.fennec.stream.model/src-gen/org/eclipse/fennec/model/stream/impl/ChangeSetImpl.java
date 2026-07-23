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

import org.eclipse.fennec.model.stream.ChangeEntry;
import org.eclipse.fennec.model.stream.ChangeSet;
import org.eclipse.fennec.model.stream.StreamPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Change Set</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ChangeSetImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ChangeSetImpl#getStreamId <em>Stream Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ChangeSetImpl#getSequence <em>Sequence</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ChangeSetImpl#getTimestamp <em>Timestamp</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ChangeSetImpl#getCommitTime <em>Commit Time</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ChangeSetImpl#getContextFingerprint <em>Context Fingerprint</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ChangeSetImpl#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ChangeSetImpl#getCause <em>Cause</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ChangeSetImpl#getTransactionId <em>Transaction Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ChangeSetImpl#isManifest <em>Manifest</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.impl.ChangeSetImpl#getEntries <em>Entries</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ChangeSetImpl extends MinimalEObjectImpl.Container implements ChangeSet {
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
	 * The default value of the '{@link #getStreamId() <em>Stream Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStreamId()
	 * @generated
	 * @ordered
	 */
	protected static final String STREAM_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getStreamId() <em>Stream Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStreamId()
	 * @generated
	 * @ordered
	 */
	protected String streamId = STREAM_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getSequence() <em>Sequence</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSequence()
	 * @generated
	 * @ordered
	 */
	protected static final long SEQUENCE_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getSequence() <em>Sequence</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSequence()
	 * @generated
	 * @ordered
	 */
	protected long sequence = SEQUENCE_EDEFAULT;

	/**
	 * The default value of the '{@link #getTimestamp() <em>Timestamp</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTimestamp()
	 * @generated
	 * @ordered
	 */
	protected static final long TIMESTAMP_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getTimestamp() <em>Timestamp</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTimestamp()
	 * @generated
	 * @ordered
	 */
	protected long timestamp = TIMESTAMP_EDEFAULT;

	/**
	 * The default value of the '{@link #getCommitTime() <em>Commit Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCommitTime()
	 * @generated
	 * @ordered
	 */
	protected static final long COMMIT_TIME_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getCommitTime() <em>Commit Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCommitTime()
	 * @generated
	 * @ordered
	 */
	protected long commitTime = COMMIT_TIME_EDEFAULT;

	/**
	 * The default value of the '{@link #getContextFingerprint() <em>Context Fingerprint</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContextFingerprint()
	 * @generated
	 * @ordered
	 */
	protected static final String CONTEXT_FINGERPRINT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getContextFingerprint() <em>Context Fingerprint</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContextFingerprint()
	 * @generated
	 * @ordered
	 */
	protected String contextFingerprint = CONTEXT_FINGERPRINT_EDEFAULT;

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
	 * The default value of the '{@link #getCause() <em>Cause</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCause()
	 * @generated
	 * @ordered
	 */
	protected static final String CAUSE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCause() <em>Cause</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCause()
	 * @generated
	 * @ordered
	 */
	protected String cause = CAUSE_EDEFAULT;

	/**
	 * The default value of the '{@link #getTransactionId() <em>Transaction Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransactionId()
	 * @generated
	 * @ordered
	 */
	protected static final String TRANSACTION_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTransactionId() <em>Transaction Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransactionId()
	 * @generated
	 * @ordered
	 */
	protected String transactionId = TRANSACTION_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #isManifest() <em>Manifest</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isManifest()
	 * @generated
	 * @ordered
	 */
	protected static final boolean MANIFEST_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isManifest() <em>Manifest</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isManifest()
	 * @generated
	 * @ordered
	 */
	protected boolean manifest = MANIFEST_EDEFAULT;

	/**
	 * The cached value of the '{@link #getEntries() <em>Entries</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEntries()
	 * @generated
	 * @ordered
	 */
	protected EList<ChangeEntry> entries;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ChangeSetImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return StreamPackage.Literals.CHANGE_SET;
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
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.CHANGE_SET__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getStreamId() {
		return streamId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setStreamId(String newStreamId) {
		String oldStreamId = streamId;
		streamId = newStreamId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.CHANGE_SET__STREAM_ID, oldStreamId, streamId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public long getSequence() {
		return sequence;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSequence(long newSequence) {
		long oldSequence = sequence;
		sequence = newSequence;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.CHANGE_SET__SEQUENCE, oldSequence, sequence));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTimestamp(long newTimestamp) {
		long oldTimestamp = timestamp;
		timestamp = newTimestamp;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.CHANGE_SET__TIMESTAMP, oldTimestamp, timestamp));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public long getCommitTime() {
		return commitTime;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setCommitTime(long newCommitTime) {
		long oldCommitTime = commitTime;
		commitTime = newCommitTime;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.CHANGE_SET__COMMIT_TIME, oldCommitTime, commitTime));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getContextFingerprint() {
		return contextFingerprint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setContextFingerprint(String newContextFingerprint) {
		String oldContextFingerprint = contextFingerprint;
		contextFingerprint = newContextFingerprint;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.CHANGE_SET__CONTEXT_FINGERPRINT, oldContextFingerprint, contextFingerprint));
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
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.CHANGE_SET__AUTHOR, oldAuthor, author));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getCause() {
		return cause;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setCause(String newCause) {
		String oldCause = cause;
		cause = newCause;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.CHANGE_SET__CAUSE, oldCause, cause));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTransactionId(String newTransactionId) {
		String oldTransactionId = transactionId;
		transactionId = newTransactionId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.CHANGE_SET__TRANSACTION_ID, oldTransactionId, transactionId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isManifest() {
		return manifest;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setManifest(boolean newManifest) {
		boolean oldManifest = manifest;
		manifest = newManifest;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StreamPackage.CHANGE_SET__MANIFEST, oldManifest, manifest));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ChangeEntry> getEntries() {
		if (entries == null) {
			entries = new EObjectContainmentEList<ChangeEntry>(ChangeEntry.class, this, StreamPackage.CHANGE_SET__ENTRIES);
		}
		return entries;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case StreamPackage.CHANGE_SET__ENTRIES:
				return ((InternalEList<?>)getEntries()).basicRemove(otherEnd, msgs);
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
			case StreamPackage.CHANGE_SET__ID:
				return getId();
			case StreamPackage.CHANGE_SET__STREAM_ID:
				return getStreamId();
			case StreamPackage.CHANGE_SET__SEQUENCE:
				return getSequence();
			case StreamPackage.CHANGE_SET__TIMESTAMP:
				return getTimestamp();
			case StreamPackage.CHANGE_SET__COMMIT_TIME:
				return getCommitTime();
			case StreamPackage.CHANGE_SET__CONTEXT_FINGERPRINT:
				return getContextFingerprint();
			case StreamPackage.CHANGE_SET__AUTHOR:
				return getAuthor();
			case StreamPackage.CHANGE_SET__CAUSE:
				return getCause();
			case StreamPackage.CHANGE_SET__TRANSACTION_ID:
				return getTransactionId();
			case StreamPackage.CHANGE_SET__MANIFEST:
				return isManifest();
			case StreamPackage.CHANGE_SET__ENTRIES:
				return getEntries();
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
			case StreamPackage.CHANGE_SET__ID:
				setId((String)newValue);
				return;
			case StreamPackage.CHANGE_SET__STREAM_ID:
				setStreamId((String)newValue);
				return;
			case StreamPackage.CHANGE_SET__SEQUENCE:
				setSequence((Long)newValue);
				return;
			case StreamPackage.CHANGE_SET__TIMESTAMP:
				setTimestamp((Long)newValue);
				return;
			case StreamPackage.CHANGE_SET__COMMIT_TIME:
				setCommitTime((Long)newValue);
				return;
			case StreamPackage.CHANGE_SET__CONTEXT_FINGERPRINT:
				setContextFingerprint((String)newValue);
				return;
			case StreamPackage.CHANGE_SET__AUTHOR:
				setAuthor((String)newValue);
				return;
			case StreamPackage.CHANGE_SET__CAUSE:
				setCause((String)newValue);
				return;
			case StreamPackage.CHANGE_SET__TRANSACTION_ID:
				setTransactionId((String)newValue);
				return;
			case StreamPackage.CHANGE_SET__MANIFEST:
				setManifest((Boolean)newValue);
				return;
			case StreamPackage.CHANGE_SET__ENTRIES:
				getEntries().clear();
				getEntries().addAll((Collection<? extends ChangeEntry>)newValue);
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
			case StreamPackage.CHANGE_SET__ID:
				setId(ID_EDEFAULT);
				return;
			case StreamPackage.CHANGE_SET__STREAM_ID:
				setStreamId(STREAM_ID_EDEFAULT);
				return;
			case StreamPackage.CHANGE_SET__SEQUENCE:
				setSequence(SEQUENCE_EDEFAULT);
				return;
			case StreamPackage.CHANGE_SET__TIMESTAMP:
				setTimestamp(TIMESTAMP_EDEFAULT);
				return;
			case StreamPackage.CHANGE_SET__COMMIT_TIME:
				setCommitTime(COMMIT_TIME_EDEFAULT);
				return;
			case StreamPackage.CHANGE_SET__CONTEXT_FINGERPRINT:
				setContextFingerprint(CONTEXT_FINGERPRINT_EDEFAULT);
				return;
			case StreamPackage.CHANGE_SET__AUTHOR:
				setAuthor(AUTHOR_EDEFAULT);
				return;
			case StreamPackage.CHANGE_SET__CAUSE:
				setCause(CAUSE_EDEFAULT);
				return;
			case StreamPackage.CHANGE_SET__TRANSACTION_ID:
				setTransactionId(TRANSACTION_ID_EDEFAULT);
				return;
			case StreamPackage.CHANGE_SET__MANIFEST:
				setManifest(MANIFEST_EDEFAULT);
				return;
			case StreamPackage.CHANGE_SET__ENTRIES:
				getEntries().clear();
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
			case StreamPackage.CHANGE_SET__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case StreamPackage.CHANGE_SET__STREAM_ID:
				return STREAM_ID_EDEFAULT == null ? streamId != null : !STREAM_ID_EDEFAULT.equals(streamId);
			case StreamPackage.CHANGE_SET__SEQUENCE:
				return sequence != SEQUENCE_EDEFAULT;
			case StreamPackage.CHANGE_SET__TIMESTAMP:
				return timestamp != TIMESTAMP_EDEFAULT;
			case StreamPackage.CHANGE_SET__COMMIT_TIME:
				return commitTime != COMMIT_TIME_EDEFAULT;
			case StreamPackage.CHANGE_SET__CONTEXT_FINGERPRINT:
				return CONTEXT_FINGERPRINT_EDEFAULT == null ? contextFingerprint != null : !CONTEXT_FINGERPRINT_EDEFAULT.equals(contextFingerprint);
			case StreamPackage.CHANGE_SET__AUTHOR:
				return AUTHOR_EDEFAULT == null ? author != null : !AUTHOR_EDEFAULT.equals(author);
			case StreamPackage.CHANGE_SET__CAUSE:
				return CAUSE_EDEFAULT == null ? cause != null : !CAUSE_EDEFAULT.equals(cause);
			case StreamPackage.CHANGE_SET__TRANSACTION_ID:
				return TRANSACTION_ID_EDEFAULT == null ? transactionId != null : !TRANSACTION_ID_EDEFAULT.equals(transactionId);
			case StreamPackage.CHANGE_SET__MANIFEST:
				return manifest != MANIFEST_EDEFAULT;
			case StreamPackage.CHANGE_SET__ENTRIES:
				return entries != null && !entries.isEmpty();
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
		result.append(", streamId: ");
		result.append(streamId);
		result.append(", sequence: ");
		result.append(sequence);
		result.append(", timestamp: ");
		result.append(timestamp);
		result.append(", commitTime: ");
		result.append(commitTime);
		result.append(", contextFingerprint: ");
		result.append(contextFingerprint);
		result.append(", author: ");
		result.append(author);
		result.append(", cause: ");
		result.append(cause);
		result.append(", transactionId: ");
		result.append(transactionId);
		result.append(", manifest: ");
		result.append(manifest);
		result.append(')');
		return result.toString();
	}

} //ChangeSetImpl
