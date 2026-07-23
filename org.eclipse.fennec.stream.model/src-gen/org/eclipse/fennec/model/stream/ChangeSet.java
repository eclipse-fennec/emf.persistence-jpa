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
 * A representation of the model object '<em><b>Change Set</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Atomic batch of change entries: one commit / one editor save / one ingest message (concept §5.1, §5.5). Applied all-or-nothing, in two phases (§5.4f): first all CREATE entries, then all feature deltas — forward references within the batch are legal. COALESCED by definition (§5.5): at most one entry per (objectId, featureId, address) — valueOld = before the batch, valueNew = after it; net-zero changes are not written. Exception: ordered-list features keep their ADD/REMOVE/MOVE sequences as an ordered operation list (index ops do not commute).
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.stream.ChangeSet#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ChangeSet#getStreamId <em>Stream Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ChangeSet#getSequence <em>Sequence</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ChangeSet#getTimestamp <em>Timestamp</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ChangeSet#getCommitTime <em>Commit Time</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ChangeSet#getContextFingerprint <em>Context Fingerprint</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ChangeSet#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ChangeSet#getCause <em>Cause</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ChangeSet#getTransactionId <em>Transaction Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ChangeSet#isManifest <em>Manifest</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ChangeSet#getEntries <em>Entries</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.stream.StreamPackage#getChangeSet()
 * @model
 * @generated
 */
@ProviderType
public interface ChangeSet extends EObject {
	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Globally unique id of this batch (UUID). Supports transport idempotency.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getChangeSet_Id()
	 * @model id="true" required="true"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.ChangeSet#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Stream Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The aggregate-root stream this batch belongs to (concept §5.4 base rule 3). Containment children of the root write into this same stream.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Stream Id</em>' attribute.
	 * @see #setStreamId(String)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getChangeSet_StreamId()
	 * @model required="true"
	 * @generated
	 */
	String getStreamId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.ChangeSet#getStreamId <em>Stream Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Stream Id</em>' attribute.
	 * @see #getStreamId()
	 * @generated
	 */
	void setStreamId(String value);

	/**
	 * Returns the value of the '<em><b>Sequence</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Per-stream contiguous monotonic sequence — the authoritative order (§4.3). Used for gap detection, replay, undo, idempotent apply.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Sequence</em>' attribute.
	 * @see #setSequence(long)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getChangeSet_Sequence()
	 * @model required="true"
	 * @generated
	 */
	long getSequence();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.ChangeSet#getSequence <em>Sequence</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sequence</em>' attribute.
	 * @see #getSequence()
	 * @generated
	 */
	void setSequence(long value);

	/**
	 * Returns the value of the '<em><b>Timestamp</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Event time (epoch millis) — domain time, may arrive late/out of order (§4.3). Range queries use this; ordering uses sequence.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Timestamp</em>' attribute.
	 * @see #setTimestamp(long)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getChangeSet_Timestamp()
	 * @model required="true"
	 * @generated
	 */
	long getTimestamp();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.ChangeSet#getTimestamp <em>Timestamp</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Timestamp</em>' attribute.
	 * @see #getTimestamp()
	 * @generated
	 */
	void setTimestamp(long value);

	/**
	 * Returns the value of the '<em><b>Commit Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Ingest/commit time (epoch millis): when the entry landed in the log.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Commit Time</em>' attribute.
	 * @see #setCommitTime(long)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getChangeSet_CommitTime()
	 * @model
	 * @generated
	 */
	long getCommitTime();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.ChangeSet#getCommitTime <em>Commit Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Commit Time</em>' attribute.
	 * @see #getCommitTime()
	 * @generated
	 */
	void setCommitTime(long value);

	/**
	 * Returns the value of the '<em><b>Context Fingerprint</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Fingerprint of the interpretation context this batch was written under: the COMPOSITE root (§6.4, §22 decision #2) — one batch may touch classes from several packages, and the composite stamp keeps every entry resolvable via (compositeFp, classId). LOGICALLY present on every batch; storage formats may run-length/delta-encode it (§6.3). Resolvable against the snapshot registry: CompositeSnapshot → per-package ContextSnapshots.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Context Fingerprint</em>' attribute.
	 * @see #setContextFingerprint(String)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getChangeSet_ContextFingerprint()
	 * @model required="true"
	 * @generated
	 */
	String getContextFingerprint();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.ChangeSet#getContextFingerprint <em>Context Fingerprint</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Context Fingerprint</em>' attribute.
	 * @see #getContextFingerprint()
	 * @generated
	 */
	void setContextFingerprint(String value);

	/**
	 * Returns the value of the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Provenance: user, device, import job, ...
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Author</em>' attribute.
	 * @see #setAuthor(String)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getChangeSet_Author()
	 * @model
	 * @generated
	 */
	String getAuthor();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.ChangeSet#getAuthor <em>Author</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Author</em>' attribute.
	 * @see #getAuthor()
	 * @generated
	 */
	void setAuthor(String value);

	/**
	 * Returns the value of the '<em><b>Cause</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Optional cause/correlation info (e.g. the capture source, an ingest message id).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Cause</em>' attribute.
	 * @see #setCause(String)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getChangeSet_Cause()
	 * @model
	 * @generated
	 */
	String getCause();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.ChangeSet#getCause <em>Cause</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Cause</em>' attribute.
	 * @see #getCause()
	 * @generated
	 */
	void setCause(String value);

	/**
	 * Returns the value of the '<em><b>Transaction Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Links batches that were split across several streams by one logical commit (cross-aggregate save, §5.4 base rule 3). Atomicity across streams is a backend transaction concern, not a log-format concern.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Transaction Id</em>' attribute.
	 * @see #setTransactionId(String)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getChangeSet_TransactionId()
	 * @model
	 * @generated
	 */
	String getTransactionId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.ChangeSet#getTransactionId <em>Transaction Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Transaction Id</em>' attribute.
	 * @see #getTransactionId()
	 * @generated
	 */
	void setTransactionId(String value);

	/**
	 * Returns the value of the '<em><b>Manifest</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * True = this batch is a STREAM MANIFEST (concept §10, §22 decision #5): it carries KEYFRAME entries for EVERY live object of the aggregate at this sequence. Absence from the manifest = deleted (makes older tombstones purgeable). Retention cuts only at manifests; resync serves 'latest manifest + deltas since'. Explicit by design — completeness IS the semantics, a keyframe-only batch without this flag is NOT a manifest.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Manifest</em>' attribute.
	 * @see #setManifest(boolean)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getChangeSet_Manifest()
	 * @model default="false"
	 * @generated
	 */
	boolean isManifest();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.ChangeSet#isManifest <em>Manifest</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Manifest</em>' attribute.
	 * @see #isManifest()
	 * @generated
	 */
	void setManifest(boolean value);

	/**
	 * Returns the value of the '<em><b>Entries</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.stream.ChangeEntry}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Ordered entries of this batch.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Entries</em>' containment reference list.
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getChangeSet_Entries()
	 * @model containment="true"
	 * @generated
	 */
	EList<ChangeEntry> getEntries();

} // ChangeSet
