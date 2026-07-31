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


import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.fennec.emf.osgi.annotation.provide.EPackage;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * Fennec change-stream metamodel (the 'diff-Ecore'). Promoted from the unified-persistence concept draft — see docs/unified-persistence/concept.md (§5, §20).
 * 
 * DISCIPLINE (concept §12.3): this package is the ONLY schema sender and receiver must share. It cannot fingerprint-bootstrap itself, so the protobuf doctrine applies manually: evolution is additive-only (append, never remove/reorder), the semantics of an existing feature are never rebound. Keep it small and boring — convenience belongs in projections, not here.
 * 
 * Values (valueOld/valueNew/value) are encoded literals or object IDs as strings; their typing resolves EXCLUSIVELY from (contextFingerprint, featureId) via the snapshot registry. Deliberately no type flags in this model.
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.model.stream.StreamFactory
 * @model kind="package"
 * @generated
 */
@ProviderType
@EPackage(uri = StreamPackage.eNS_URI, fingerprint = "fp1:b88eae543d45edf2b5c2f4aeefa350ca67a578d35551931ca03d206879591ffc", genModel = "/model/stream.genmodel", genModelSourceLocations = {"model/stream.genmodel","org.eclipse.fennec.stream.model/model/stream.genmodel"}, ecore = "/model/stream.ecore", ecoreSourceLocations = "/model/stream.ecore")
public interface StreamPackage extends org.eclipse.emf.ecore.EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "stream";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://org.eclipse/fennec/stream/1.0.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "stream";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	StreamPackage eINSTANCE = org.eclipse.fennec.model.stream.impl.StreamPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.stream.impl.ChangeSetImpl <em>Change Set</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.stream.impl.ChangeSetImpl
	 * @see org.eclipse.fennec.model.stream.impl.StreamPackageImpl#getChangeSet()
	 * @generated
	 */
	int CHANGE_SET = 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_SET__ID = 0;

	/**
	 * The feature id for the '<em><b>Stream Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_SET__STREAM_ID = 1;

	/**
	 * The feature id for the '<em><b>Sequence</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_SET__SEQUENCE = 2;

	/**
	 * The feature id for the '<em><b>Timestamp</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_SET__TIMESTAMP = 3;

	/**
	 * The feature id for the '<em><b>Commit Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_SET__COMMIT_TIME = 4;

	/**
	 * The feature id for the '<em><b>Context Fingerprint</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_SET__CONTEXT_FINGERPRINT = 5;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_SET__AUTHOR = 6;

	/**
	 * The feature id for the '<em><b>Cause</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_SET__CAUSE = 7;

	/**
	 * The feature id for the '<em><b>Transaction Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_SET__TRANSACTION_ID = 8;

	/**
	 * The feature id for the '<em><b>Manifest</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_SET__MANIFEST = 9;

	/**
	 * The feature id for the '<em><b>Entries</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_SET__ENTRIES = 10;

	/**
	 * The number of structural features of the '<em>Change Set</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_SET_FEATURE_COUNT = 11;

	/**
	 * The number of operations of the '<em>Change Set</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_SET_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.stream.impl.ChangeEntryImpl <em>Change Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.stream.impl.ChangeEntryImpl
	 * @see org.eclipse.fennec.model.stream.impl.StreamPackageImpl#getChangeEntry()
	 * @generated
	 */
	int CHANGE_ENTRY = 1;

	/**
	 * The feature id for the '<em><b>Object Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_ENTRY__OBJECT_ID = 0;

	/**
	 * The feature id for the '<em><b>Class Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_ENTRY__CLASS_ID = 1;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_ENTRY__KIND = 2;

	/**
	 * The feature id for the '<em><b>Feature Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_ENTRY__FEATURE_ID = 3;

	/**
	 * The feature id for the '<em><b>Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_ENTRY__INDEX = 4;

	/**
	 * The feature id for the '<em><b>To Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_ENTRY__TO_INDEX = 5;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_ENTRY__KEY = 6;

	/**
	 * The feature id for the '<em><b>Coords</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_ENTRY__COORDS = 7;

	/**
	 * The feature id for the '<em><b>Value Old</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_ENTRY__VALUE_OLD = 8;

	/**
	 * The feature id for the '<em><b>Value New</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_ENTRY__VALUE_NEW = 9;

	/**
	 * The feature id for the '<em><b>State</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_ENTRY__STATE = 10;

	/**
	 * The number of structural features of the '<em>Change Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_ENTRY_FEATURE_COUNT = 11;

	/**
	 * The number of operations of the '<em>Change Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.stream.impl.SlotValueImpl <em>Slot Value</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.stream.impl.SlotValueImpl
	 * @see org.eclipse.fennec.model.stream.impl.StreamPackageImpl#getSlotValue()
	 * @generated
	 */
	int SLOT_VALUE = 2;

	/**
	 * The feature id for the '<em><b>Feature Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SLOT_VALUE__FEATURE_ID = 0;

	/**
	 * The feature id for the '<em><b>Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SLOT_VALUE__INDEX = 1;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SLOT_VALUE__KEY = 2;

	/**
	 * The feature id for the '<em><b>Coords</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SLOT_VALUE__COORDS = 3;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SLOT_VALUE__VALUE = 4;

	/**
	 * The number of structural features of the '<em>Slot Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SLOT_VALUE_FEATURE_COUNT = 5;

	/**
	 * The number of operations of the '<em>Slot Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SLOT_VALUE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.stream.impl.ContextSnapshotImpl <em>Context Snapshot</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.stream.impl.ContextSnapshotImpl
	 * @see org.eclipse.fennec.model.stream.impl.StreamPackageImpl#getContextSnapshot()
	 * @generated
	 */
	int CONTEXT_SNAPSHOT = 3;

	/**
	 * The feature id for the '<em><b>Fingerprint</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_SNAPSHOT__FINGERPRINT = 0;

	/**
	 * The feature id for the '<em><b>Predecessor Fingerprint</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_SNAPSHOT__PREDECESSOR_FINGERPRINT = 1;

	/**
	 * The feature id for the '<em><b>Created</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_SNAPSHOT__CREATED = 2;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_SNAPSHOT__AUTHOR = 3;

	/**
	 * The feature id for the '<em><b>Labels</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_SNAPSHOT__LABELS = 4;

	/**
	 * The feature id for the '<em><b>Package Uri</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_SNAPSHOT__PACKAGE_URI = 5;

	/**
	 * The feature id for the '<em><b>Content</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_SNAPSHOT__CONTENT = 6;

	/**
	 * The number of structural features of the '<em>Context Snapshot</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_SNAPSHOT_FEATURE_COUNT = 7;

	/**
	 * The number of operations of the '<em>Context Snapshot</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_SNAPSHOT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.stream.impl.CompositeSnapshotImpl <em>Composite Snapshot</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.stream.impl.CompositeSnapshotImpl
	 * @see org.eclipse.fennec.model.stream.impl.StreamPackageImpl#getCompositeSnapshot()
	 * @generated
	 */
	int COMPOSITE_SNAPSHOT = 4;

	/**
	 * The feature id for the '<em><b>Fingerprint</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPOSITE_SNAPSHOT__FINGERPRINT = 0;

	/**
	 * The feature id for the '<em><b>Predecessor Fingerprint</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPOSITE_SNAPSHOT__PREDECESSOR_FINGERPRINT = 1;

	/**
	 * The feature id for the '<em><b>Created</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPOSITE_SNAPSHOT__CREATED = 2;

	/**
	 * The feature id for the '<em><b>Packages</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPOSITE_SNAPSHOT__PACKAGES = 3;

	/**
	 * The number of structural features of the '<em>Composite Snapshot</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPOSITE_SNAPSHOT_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Composite Snapshot</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPOSITE_SNAPSHOT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.stream.impl.PackageFingerprintImpl <em>Package Fingerprint</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.stream.impl.PackageFingerprintImpl
	 * @see org.eclipse.fennec.model.stream.impl.StreamPackageImpl#getPackageFingerprint()
	 * @generated
	 */
	int PACKAGE_FINGERPRINT = 5;

	/**
	 * The feature id for the '<em><b>Package Uri</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE_FINGERPRINT__PACKAGE_URI = 0;

	/**
	 * The feature id for the '<em><b>Package Fingerprint</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE_FINGERPRINT__PACKAGE_FINGERPRINT = 1;

	/**
	 * The number of structural features of the '<em>Package Fingerprint</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE_FINGERPRINT_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Package Fingerprint</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE_FINGERPRINT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.stream.DeltaKind <em>Delta Kind</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.stream.DeltaKind
	 * @see org.eclipse.fennec.model.stream.impl.StreamPackageImpl#getDeltaKind()
	 * @generated
	 */
	int DELTA_KIND = 6;


	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.stream.ChangeSet <em>Change Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Change Set</em>'.
	 * @see org.eclipse.fennec.model.stream.ChangeSet
	 * @generated
	 */
	EClass getChangeSet();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.ChangeSet#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.fennec.model.stream.ChangeSet#getId()
	 * @see #getChangeSet()
	 * @generated
	 */
	EAttribute getChangeSet_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.ChangeSet#getStreamId <em>Stream Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Stream Id</em>'.
	 * @see org.eclipse.fennec.model.stream.ChangeSet#getStreamId()
	 * @see #getChangeSet()
	 * @generated
	 */
	EAttribute getChangeSet_StreamId();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.ChangeSet#getSequence <em>Sequence</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Sequence</em>'.
	 * @see org.eclipse.fennec.model.stream.ChangeSet#getSequence()
	 * @see #getChangeSet()
	 * @generated
	 */
	EAttribute getChangeSet_Sequence();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.ChangeSet#getTimestamp <em>Timestamp</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Timestamp</em>'.
	 * @see org.eclipse.fennec.model.stream.ChangeSet#getTimestamp()
	 * @see #getChangeSet()
	 * @generated
	 */
	EAttribute getChangeSet_Timestamp();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.ChangeSet#getCommitTime <em>Commit Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Commit Time</em>'.
	 * @see org.eclipse.fennec.model.stream.ChangeSet#getCommitTime()
	 * @see #getChangeSet()
	 * @generated
	 */
	EAttribute getChangeSet_CommitTime();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.ChangeSet#getContextFingerprint <em>Context Fingerprint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Context Fingerprint</em>'.
	 * @see org.eclipse.fennec.model.stream.ChangeSet#getContextFingerprint()
	 * @see #getChangeSet()
	 * @generated
	 */
	EAttribute getChangeSet_ContextFingerprint();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.ChangeSet#getAuthor <em>Author</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Author</em>'.
	 * @see org.eclipse.fennec.model.stream.ChangeSet#getAuthor()
	 * @see #getChangeSet()
	 * @generated
	 */
	EAttribute getChangeSet_Author();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.ChangeSet#getCause <em>Cause</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Cause</em>'.
	 * @see org.eclipse.fennec.model.stream.ChangeSet#getCause()
	 * @see #getChangeSet()
	 * @generated
	 */
	EAttribute getChangeSet_Cause();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.ChangeSet#getTransactionId <em>Transaction Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Transaction Id</em>'.
	 * @see org.eclipse.fennec.model.stream.ChangeSet#getTransactionId()
	 * @see #getChangeSet()
	 * @generated
	 */
	EAttribute getChangeSet_TransactionId();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.ChangeSet#isManifest <em>Manifest</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Manifest</em>'.
	 * @see org.eclipse.fennec.model.stream.ChangeSet#isManifest()
	 * @see #getChangeSet()
	 * @generated
	 */
	EAttribute getChangeSet_Manifest();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.stream.ChangeSet#getEntries <em>Entries</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Entries</em>'.
	 * @see org.eclipse.fennec.model.stream.ChangeSet#getEntries()
	 * @see #getChangeSet()
	 * @generated
	 */
	EReference getChangeSet_Entries();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.stream.ChangeEntry <em>Change Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Change Entry</em>'.
	 * @see org.eclipse.fennec.model.stream.ChangeEntry
	 * @generated
	 */
	EClass getChangeEntry();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.ChangeEntry#getObjectId <em>Object Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Object Id</em>'.
	 * @see org.eclipse.fennec.model.stream.ChangeEntry#getObjectId()
	 * @see #getChangeEntry()
	 * @generated
	 */
	EAttribute getChangeEntry_ObjectId();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.ChangeEntry#getClassId <em>Class Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Class Id</em>'.
	 * @see org.eclipse.fennec.model.stream.ChangeEntry#getClassId()
	 * @see #getChangeEntry()
	 * @generated
	 */
	EAttribute getChangeEntry_ClassId();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.ChangeEntry#getKind <em>Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Kind</em>'.
	 * @see org.eclipse.fennec.model.stream.ChangeEntry#getKind()
	 * @see #getChangeEntry()
	 * @generated
	 */
	EAttribute getChangeEntry_Kind();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.ChangeEntry#getFeatureId <em>Feature Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Feature Id</em>'.
	 * @see org.eclipse.fennec.model.stream.ChangeEntry#getFeatureId()
	 * @see #getChangeEntry()
	 * @generated
	 */
	EAttribute getChangeEntry_FeatureId();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.ChangeEntry#getIndex <em>Index</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Index</em>'.
	 * @see org.eclipse.fennec.model.stream.ChangeEntry#getIndex()
	 * @see #getChangeEntry()
	 * @generated
	 */
	EAttribute getChangeEntry_Index();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.ChangeEntry#getToIndex <em>To Index</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>To Index</em>'.
	 * @see org.eclipse.fennec.model.stream.ChangeEntry#getToIndex()
	 * @see #getChangeEntry()
	 * @generated
	 */
	EAttribute getChangeEntry_ToIndex();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.ChangeEntry#getKey <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see org.eclipse.fennec.model.stream.ChangeEntry#getKey()
	 * @see #getChangeEntry()
	 * @generated
	 */
	EAttribute getChangeEntry_Key();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.model.stream.ChangeEntry#getCoords <em>Coords</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Coords</em>'.
	 * @see org.eclipse.fennec.model.stream.ChangeEntry#getCoords()
	 * @see #getChangeEntry()
	 * @generated
	 */
	EAttribute getChangeEntry_Coords();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.ChangeEntry#getValueOld <em>Value Old</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value Old</em>'.
	 * @see org.eclipse.fennec.model.stream.ChangeEntry#getValueOld()
	 * @see #getChangeEntry()
	 * @generated
	 */
	EAttribute getChangeEntry_ValueOld();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.ChangeEntry#getValueNew <em>Value New</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value New</em>'.
	 * @see org.eclipse.fennec.model.stream.ChangeEntry#getValueNew()
	 * @see #getChangeEntry()
	 * @generated
	 */
	EAttribute getChangeEntry_ValueNew();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.stream.ChangeEntry#getState <em>State</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>State</em>'.
	 * @see org.eclipse.fennec.model.stream.ChangeEntry#getState()
	 * @see #getChangeEntry()
	 * @generated
	 */
	EReference getChangeEntry_State();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.stream.SlotValue <em>Slot Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Slot Value</em>'.
	 * @see org.eclipse.fennec.model.stream.SlotValue
	 * @generated
	 */
	EClass getSlotValue();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.SlotValue#getFeatureId <em>Feature Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Feature Id</em>'.
	 * @see org.eclipse.fennec.model.stream.SlotValue#getFeatureId()
	 * @see #getSlotValue()
	 * @generated
	 */
	EAttribute getSlotValue_FeatureId();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.SlotValue#getIndex <em>Index</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Index</em>'.
	 * @see org.eclipse.fennec.model.stream.SlotValue#getIndex()
	 * @see #getSlotValue()
	 * @generated
	 */
	EAttribute getSlotValue_Index();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.SlotValue#getKey <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see org.eclipse.fennec.model.stream.SlotValue#getKey()
	 * @see #getSlotValue()
	 * @generated
	 */
	EAttribute getSlotValue_Key();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.model.stream.SlotValue#getCoords <em>Coords</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Coords</em>'.
	 * @see org.eclipse.fennec.model.stream.SlotValue#getCoords()
	 * @see #getSlotValue()
	 * @generated
	 */
	EAttribute getSlotValue_Coords();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.SlotValue#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.fennec.model.stream.SlotValue#getValue()
	 * @see #getSlotValue()
	 * @generated
	 */
	EAttribute getSlotValue_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.stream.ContextSnapshot <em>Context Snapshot</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Context Snapshot</em>'.
	 * @see org.eclipse.fennec.model.stream.ContextSnapshot
	 * @generated
	 */
	EClass getContextSnapshot();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.ContextSnapshot#getFingerprint <em>Fingerprint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Fingerprint</em>'.
	 * @see org.eclipse.fennec.model.stream.ContextSnapshot#getFingerprint()
	 * @see #getContextSnapshot()
	 * @generated
	 */
	EAttribute getContextSnapshot_Fingerprint();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.ContextSnapshot#getPredecessorFingerprint <em>Predecessor Fingerprint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Predecessor Fingerprint</em>'.
	 * @see org.eclipse.fennec.model.stream.ContextSnapshot#getPredecessorFingerprint()
	 * @see #getContextSnapshot()
	 * @generated
	 */
	EAttribute getContextSnapshot_PredecessorFingerprint();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.ContextSnapshot#getCreated <em>Created</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Created</em>'.
	 * @see org.eclipse.fennec.model.stream.ContextSnapshot#getCreated()
	 * @see #getContextSnapshot()
	 * @generated
	 */
	EAttribute getContextSnapshot_Created();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.ContextSnapshot#getAuthor <em>Author</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Author</em>'.
	 * @see org.eclipse.fennec.model.stream.ContextSnapshot#getAuthor()
	 * @see #getContextSnapshot()
	 * @generated
	 */
	EAttribute getContextSnapshot_Author();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.model.stream.ContextSnapshot#getLabels <em>Labels</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Labels</em>'.
	 * @see org.eclipse.fennec.model.stream.ContextSnapshot#getLabels()
	 * @see #getContextSnapshot()
	 * @generated
	 */
	EAttribute getContextSnapshot_Labels();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.ContextSnapshot#getPackageUri <em>Package Uri</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Package Uri</em>'.
	 * @see org.eclipse.fennec.model.stream.ContextSnapshot#getPackageUri()
	 * @see #getContextSnapshot()
	 * @generated
	 */
	EAttribute getContextSnapshot_PackageUri();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.stream.ContextSnapshot#getContent <em>Content</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Content</em>'.
	 * @see org.eclipse.fennec.model.stream.ContextSnapshot#getContent()
	 * @see #getContextSnapshot()
	 * @generated
	 */
	EReference getContextSnapshot_Content();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.stream.CompositeSnapshot <em>Composite Snapshot</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Composite Snapshot</em>'.
	 * @see org.eclipse.fennec.model.stream.CompositeSnapshot
	 * @generated
	 */
	EClass getCompositeSnapshot();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.CompositeSnapshot#getFingerprint <em>Fingerprint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Fingerprint</em>'.
	 * @see org.eclipse.fennec.model.stream.CompositeSnapshot#getFingerprint()
	 * @see #getCompositeSnapshot()
	 * @generated
	 */
	EAttribute getCompositeSnapshot_Fingerprint();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.CompositeSnapshot#getPredecessorFingerprint <em>Predecessor Fingerprint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Predecessor Fingerprint</em>'.
	 * @see org.eclipse.fennec.model.stream.CompositeSnapshot#getPredecessorFingerprint()
	 * @see #getCompositeSnapshot()
	 * @generated
	 */
	EAttribute getCompositeSnapshot_PredecessorFingerprint();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.CompositeSnapshot#getCreated <em>Created</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Created</em>'.
	 * @see org.eclipse.fennec.model.stream.CompositeSnapshot#getCreated()
	 * @see #getCompositeSnapshot()
	 * @generated
	 */
	EAttribute getCompositeSnapshot_Created();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.stream.CompositeSnapshot#getPackages <em>Packages</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Packages</em>'.
	 * @see org.eclipse.fennec.model.stream.CompositeSnapshot#getPackages()
	 * @see #getCompositeSnapshot()
	 * @generated
	 */
	EReference getCompositeSnapshot_Packages();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.stream.PackageFingerprint <em>Package Fingerprint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Package Fingerprint</em>'.
	 * @see org.eclipse.fennec.model.stream.PackageFingerprint
	 * @generated
	 */
	EClass getPackageFingerprint();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.PackageFingerprint#getPackageUri <em>Package Uri</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Package Uri</em>'.
	 * @see org.eclipse.fennec.model.stream.PackageFingerprint#getPackageUri()
	 * @see #getPackageFingerprint()
	 * @generated
	 */
	EAttribute getPackageFingerprint_PackageUri();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.stream.PackageFingerprint#getPackageFingerprint <em>Package Fingerprint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Package Fingerprint</em>'.
	 * @see org.eclipse.fennec.model.stream.PackageFingerprint#getPackageFingerprint()
	 * @see #getPackageFingerprint()
	 * @generated
	 */
	EAttribute getPackageFingerprint_PackageFingerprint();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.stream.DeltaKind <em>Delta Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Delta Kind</em>'.
	 * @see org.eclipse.fennec.model.stream.DeltaKind
	 * @generated
	 */
	EEnum getDeltaKind();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	StreamFactory getStreamFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.stream.impl.ChangeSetImpl <em>Change Set</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.stream.impl.ChangeSetImpl
		 * @see org.eclipse.fennec.model.stream.impl.StreamPackageImpl#getChangeSet()
		 * @generated
		 */
		EClass CHANGE_SET = eINSTANCE.getChangeSet();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CHANGE_SET__ID = eINSTANCE.getChangeSet_Id();

		/**
		 * The meta object literal for the '<em><b>Stream Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CHANGE_SET__STREAM_ID = eINSTANCE.getChangeSet_StreamId();

		/**
		 * The meta object literal for the '<em><b>Sequence</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CHANGE_SET__SEQUENCE = eINSTANCE.getChangeSet_Sequence();

		/**
		 * The meta object literal for the '<em><b>Timestamp</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CHANGE_SET__TIMESTAMP = eINSTANCE.getChangeSet_Timestamp();

		/**
		 * The meta object literal for the '<em><b>Commit Time</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CHANGE_SET__COMMIT_TIME = eINSTANCE.getChangeSet_CommitTime();

		/**
		 * The meta object literal for the '<em><b>Context Fingerprint</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CHANGE_SET__CONTEXT_FINGERPRINT = eINSTANCE.getChangeSet_ContextFingerprint();

		/**
		 * The meta object literal for the '<em><b>Author</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CHANGE_SET__AUTHOR = eINSTANCE.getChangeSet_Author();

		/**
		 * The meta object literal for the '<em><b>Cause</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CHANGE_SET__CAUSE = eINSTANCE.getChangeSet_Cause();

		/**
		 * The meta object literal for the '<em><b>Transaction Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CHANGE_SET__TRANSACTION_ID = eINSTANCE.getChangeSet_TransactionId();

		/**
		 * The meta object literal for the '<em><b>Manifest</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CHANGE_SET__MANIFEST = eINSTANCE.getChangeSet_Manifest();

		/**
		 * The meta object literal for the '<em><b>Entries</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CHANGE_SET__ENTRIES = eINSTANCE.getChangeSet_Entries();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.stream.impl.ChangeEntryImpl <em>Change Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.stream.impl.ChangeEntryImpl
		 * @see org.eclipse.fennec.model.stream.impl.StreamPackageImpl#getChangeEntry()
		 * @generated
		 */
		EClass CHANGE_ENTRY = eINSTANCE.getChangeEntry();

		/**
		 * The meta object literal for the '<em><b>Object Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CHANGE_ENTRY__OBJECT_ID = eINSTANCE.getChangeEntry_ObjectId();

		/**
		 * The meta object literal for the '<em><b>Class Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CHANGE_ENTRY__CLASS_ID = eINSTANCE.getChangeEntry_ClassId();

		/**
		 * The meta object literal for the '<em><b>Kind</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CHANGE_ENTRY__KIND = eINSTANCE.getChangeEntry_Kind();

		/**
		 * The meta object literal for the '<em><b>Feature Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CHANGE_ENTRY__FEATURE_ID = eINSTANCE.getChangeEntry_FeatureId();

		/**
		 * The meta object literal for the '<em><b>Index</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CHANGE_ENTRY__INDEX = eINSTANCE.getChangeEntry_Index();

		/**
		 * The meta object literal for the '<em><b>To Index</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CHANGE_ENTRY__TO_INDEX = eINSTANCE.getChangeEntry_ToIndex();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CHANGE_ENTRY__KEY = eINSTANCE.getChangeEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Coords</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CHANGE_ENTRY__COORDS = eINSTANCE.getChangeEntry_Coords();

		/**
		 * The meta object literal for the '<em><b>Value Old</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CHANGE_ENTRY__VALUE_OLD = eINSTANCE.getChangeEntry_ValueOld();

		/**
		 * The meta object literal for the '<em><b>Value New</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CHANGE_ENTRY__VALUE_NEW = eINSTANCE.getChangeEntry_ValueNew();

		/**
		 * The meta object literal for the '<em><b>State</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CHANGE_ENTRY__STATE = eINSTANCE.getChangeEntry_State();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.stream.impl.SlotValueImpl <em>Slot Value</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.stream.impl.SlotValueImpl
		 * @see org.eclipse.fennec.model.stream.impl.StreamPackageImpl#getSlotValue()
		 * @generated
		 */
		EClass SLOT_VALUE = eINSTANCE.getSlotValue();

		/**
		 * The meta object literal for the '<em><b>Feature Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SLOT_VALUE__FEATURE_ID = eINSTANCE.getSlotValue_FeatureId();

		/**
		 * The meta object literal for the '<em><b>Index</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SLOT_VALUE__INDEX = eINSTANCE.getSlotValue_Index();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SLOT_VALUE__KEY = eINSTANCE.getSlotValue_Key();

		/**
		 * The meta object literal for the '<em><b>Coords</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SLOT_VALUE__COORDS = eINSTANCE.getSlotValue_Coords();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SLOT_VALUE__VALUE = eINSTANCE.getSlotValue_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.stream.impl.ContextSnapshotImpl <em>Context Snapshot</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.stream.impl.ContextSnapshotImpl
		 * @see org.eclipse.fennec.model.stream.impl.StreamPackageImpl#getContextSnapshot()
		 * @generated
		 */
		EClass CONTEXT_SNAPSHOT = eINSTANCE.getContextSnapshot();

		/**
		 * The meta object literal for the '<em><b>Fingerprint</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTEXT_SNAPSHOT__FINGERPRINT = eINSTANCE.getContextSnapshot_Fingerprint();

		/**
		 * The meta object literal for the '<em><b>Predecessor Fingerprint</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTEXT_SNAPSHOT__PREDECESSOR_FINGERPRINT = eINSTANCE.getContextSnapshot_PredecessorFingerprint();

		/**
		 * The meta object literal for the '<em><b>Created</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTEXT_SNAPSHOT__CREATED = eINSTANCE.getContextSnapshot_Created();

		/**
		 * The meta object literal for the '<em><b>Author</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTEXT_SNAPSHOT__AUTHOR = eINSTANCE.getContextSnapshot_Author();

		/**
		 * The meta object literal for the '<em><b>Labels</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTEXT_SNAPSHOT__LABELS = eINSTANCE.getContextSnapshot_Labels();

		/**
		 * The meta object literal for the '<em><b>Package Uri</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTEXT_SNAPSHOT__PACKAGE_URI = eINSTANCE.getContextSnapshot_PackageUri();

		/**
		 * The meta object literal for the '<em><b>Content</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTEXT_SNAPSHOT__CONTENT = eINSTANCE.getContextSnapshot_Content();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.stream.impl.CompositeSnapshotImpl <em>Composite Snapshot</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.stream.impl.CompositeSnapshotImpl
		 * @see org.eclipse.fennec.model.stream.impl.StreamPackageImpl#getCompositeSnapshot()
		 * @generated
		 */
		EClass COMPOSITE_SNAPSHOT = eINSTANCE.getCompositeSnapshot();

		/**
		 * The meta object literal for the '<em><b>Fingerprint</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COMPOSITE_SNAPSHOT__FINGERPRINT = eINSTANCE.getCompositeSnapshot_Fingerprint();

		/**
		 * The meta object literal for the '<em><b>Predecessor Fingerprint</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COMPOSITE_SNAPSHOT__PREDECESSOR_FINGERPRINT = eINSTANCE.getCompositeSnapshot_PredecessorFingerprint();

		/**
		 * The meta object literal for the '<em><b>Created</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COMPOSITE_SNAPSHOT__CREATED = eINSTANCE.getCompositeSnapshot_Created();

		/**
		 * The meta object literal for the '<em><b>Packages</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMPOSITE_SNAPSHOT__PACKAGES = eINSTANCE.getCompositeSnapshot_Packages();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.stream.impl.PackageFingerprintImpl <em>Package Fingerprint</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.stream.impl.PackageFingerprintImpl
		 * @see org.eclipse.fennec.model.stream.impl.StreamPackageImpl#getPackageFingerprint()
		 * @generated
		 */
		EClass PACKAGE_FINGERPRINT = eINSTANCE.getPackageFingerprint();

		/**
		 * The meta object literal for the '<em><b>Package Uri</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PACKAGE_FINGERPRINT__PACKAGE_URI = eINSTANCE.getPackageFingerprint_PackageUri();

		/**
		 * The meta object literal for the '<em><b>Package Fingerprint</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PACKAGE_FINGERPRINT__PACKAGE_FINGERPRINT = eINSTANCE.getPackageFingerprint_PackageFingerprint();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.stream.DeltaKind <em>Delta Kind</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.stream.DeltaKind
		 * @see org.eclipse.fennec.model.stream.impl.StreamPackageImpl#getDeltaKind()
		 * @generated
		 */
		EEnum DELTA_KIND = eINSTANCE.getDeltaKind();

	}

} //StreamPackage
