/**
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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.fennec.model.stream.ChangeEntry;
import org.eclipse.fennec.model.stream.ChangeSet;
import org.eclipse.fennec.model.stream.CompositeSnapshot;
import org.eclipse.fennec.model.stream.ContextSnapshot;
import org.eclipse.fennec.model.stream.DeltaKind;
import org.eclipse.fennec.model.stream.PackageFingerprint;
import org.eclipse.fennec.model.stream.SlotValue;
import org.eclipse.fennec.model.stream.StreamFactory;
import org.eclipse.fennec.model.stream.StreamPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class StreamPackageImpl extends EPackageImpl implements StreamPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass changeSetEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass changeEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass slotValueEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass contextSnapshotEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass compositeSnapshotEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass packageFingerprintEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum deltaKindEEnum = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.fennec.model.stream.StreamPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private StreamPackageImpl() {
		super(eNS_URI, StreamFactory.eINSTANCE);
	}
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link StreamPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static StreamPackage init() {
		if (isInited) return (StreamPackage)EPackage.Registry.INSTANCE.getEPackage(StreamPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredStreamPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		StreamPackageImpl theStreamPackage = registeredStreamPackage instanceof StreamPackageImpl ? (StreamPackageImpl)registeredStreamPackage : new StreamPackageImpl();

		isInited = true;

		// Create package meta-data objects
		theStreamPackage.createPackageContents();

		// Initialize created meta-data
		theStreamPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theStreamPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(StreamPackage.eNS_URI, theStreamPackage);
		return theStreamPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getChangeSet() {
		return changeSetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getChangeSet_Id() {
		return (EAttribute)changeSetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getChangeSet_StreamId() {
		return (EAttribute)changeSetEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getChangeSet_Sequence() {
		return (EAttribute)changeSetEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getChangeSet_Timestamp() {
		return (EAttribute)changeSetEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getChangeSet_CommitTime() {
		return (EAttribute)changeSetEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getChangeSet_ContextFingerprint() {
		return (EAttribute)changeSetEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getChangeSet_Author() {
		return (EAttribute)changeSetEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getChangeSet_Cause() {
		return (EAttribute)changeSetEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getChangeSet_TransactionId() {
		return (EAttribute)changeSetEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getChangeSet_Manifest() {
		return (EAttribute)changeSetEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getChangeSet_Entries() {
		return (EReference)changeSetEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getChangeEntry() {
		return changeEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getChangeEntry_ObjectId() {
		return (EAttribute)changeEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getChangeEntry_ClassId() {
		return (EAttribute)changeEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getChangeEntry_Kind() {
		return (EAttribute)changeEntryEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getChangeEntry_FeatureId() {
		return (EAttribute)changeEntryEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getChangeEntry_Index() {
		return (EAttribute)changeEntryEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getChangeEntry_ToIndex() {
		return (EAttribute)changeEntryEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getChangeEntry_Key() {
		return (EAttribute)changeEntryEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getChangeEntry_Coords() {
		return (EAttribute)changeEntryEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getChangeEntry_ValueOld() {
		return (EAttribute)changeEntryEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getChangeEntry_ValueNew() {
		return (EAttribute)changeEntryEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getChangeEntry_State() {
		return (EReference)changeEntryEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSlotValue() {
		return slotValueEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSlotValue_FeatureId() {
		return (EAttribute)slotValueEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSlotValue_Index() {
		return (EAttribute)slotValueEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSlotValue_Key() {
		return (EAttribute)slotValueEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSlotValue_Coords() {
		return (EAttribute)slotValueEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSlotValue_Value() {
		return (EAttribute)slotValueEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getContextSnapshot() {
		return contextSnapshotEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getContextSnapshot_Fingerprint() {
		return (EAttribute)contextSnapshotEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getContextSnapshot_PredecessorFingerprint() {
		return (EAttribute)contextSnapshotEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getContextSnapshot_Created() {
		return (EAttribute)contextSnapshotEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getContextSnapshot_Author() {
		return (EAttribute)contextSnapshotEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getContextSnapshot_Labels() {
		return (EAttribute)contextSnapshotEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getContextSnapshot_PackageUri() {
		return (EAttribute)contextSnapshotEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getContextSnapshot_Content() {
		return (EReference)contextSnapshotEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCompositeSnapshot() {
		return compositeSnapshotEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCompositeSnapshot_Fingerprint() {
		return (EAttribute)compositeSnapshotEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCompositeSnapshot_PredecessorFingerprint() {
		return (EAttribute)compositeSnapshotEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCompositeSnapshot_Created() {
		return (EAttribute)compositeSnapshotEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCompositeSnapshot_Packages() {
		return (EReference)compositeSnapshotEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPackageFingerprint() {
		return packageFingerprintEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPackageFingerprint_PackageUri() {
		return (EAttribute)packageFingerprintEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPackageFingerprint_PackageFingerprint() {
		return (EAttribute)packageFingerprintEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getDeltaKind() {
		return deltaKindEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public StreamFactory getStreamFactory() {
		return (StreamFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		changeSetEClass = createEClass(CHANGE_SET);
		createEAttribute(changeSetEClass, CHANGE_SET__ID);
		createEAttribute(changeSetEClass, CHANGE_SET__STREAM_ID);
		createEAttribute(changeSetEClass, CHANGE_SET__SEQUENCE);
		createEAttribute(changeSetEClass, CHANGE_SET__TIMESTAMP);
		createEAttribute(changeSetEClass, CHANGE_SET__COMMIT_TIME);
		createEAttribute(changeSetEClass, CHANGE_SET__CONTEXT_FINGERPRINT);
		createEAttribute(changeSetEClass, CHANGE_SET__AUTHOR);
		createEAttribute(changeSetEClass, CHANGE_SET__CAUSE);
		createEAttribute(changeSetEClass, CHANGE_SET__TRANSACTION_ID);
		createEAttribute(changeSetEClass, CHANGE_SET__MANIFEST);
		createEReference(changeSetEClass, CHANGE_SET__ENTRIES);

		changeEntryEClass = createEClass(CHANGE_ENTRY);
		createEAttribute(changeEntryEClass, CHANGE_ENTRY__OBJECT_ID);
		createEAttribute(changeEntryEClass, CHANGE_ENTRY__CLASS_ID);
		createEAttribute(changeEntryEClass, CHANGE_ENTRY__KIND);
		createEAttribute(changeEntryEClass, CHANGE_ENTRY__FEATURE_ID);
		createEAttribute(changeEntryEClass, CHANGE_ENTRY__INDEX);
		createEAttribute(changeEntryEClass, CHANGE_ENTRY__TO_INDEX);
		createEAttribute(changeEntryEClass, CHANGE_ENTRY__KEY);
		createEAttribute(changeEntryEClass, CHANGE_ENTRY__COORDS);
		createEAttribute(changeEntryEClass, CHANGE_ENTRY__VALUE_OLD);
		createEAttribute(changeEntryEClass, CHANGE_ENTRY__VALUE_NEW);
		createEReference(changeEntryEClass, CHANGE_ENTRY__STATE);

		slotValueEClass = createEClass(SLOT_VALUE);
		createEAttribute(slotValueEClass, SLOT_VALUE__FEATURE_ID);
		createEAttribute(slotValueEClass, SLOT_VALUE__INDEX);
		createEAttribute(slotValueEClass, SLOT_VALUE__KEY);
		createEAttribute(slotValueEClass, SLOT_VALUE__COORDS);
		createEAttribute(slotValueEClass, SLOT_VALUE__VALUE);

		contextSnapshotEClass = createEClass(CONTEXT_SNAPSHOT);
		createEAttribute(contextSnapshotEClass, CONTEXT_SNAPSHOT__FINGERPRINT);
		createEAttribute(contextSnapshotEClass, CONTEXT_SNAPSHOT__PREDECESSOR_FINGERPRINT);
		createEAttribute(contextSnapshotEClass, CONTEXT_SNAPSHOT__CREATED);
		createEAttribute(contextSnapshotEClass, CONTEXT_SNAPSHOT__AUTHOR);
		createEAttribute(contextSnapshotEClass, CONTEXT_SNAPSHOT__LABELS);
		createEAttribute(contextSnapshotEClass, CONTEXT_SNAPSHOT__PACKAGE_URI);
		createEReference(contextSnapshotEClass, CONTEXT_SNAPSHOT__CONTENT);

		compositeSnapshotEClass = createEClass(COMPOSITE_SNAPSHOT);
		createEAttribute(compositeSnapshotEClass, COMPOSITE_SNAPSHOT__FINGERPRINT);
		createEAttribute(compositeSnapshotEClass, COMPOSITE_SNAPSHOT__PREDECESSOR_FINGERPRINT);
		createEAttribute(compositeSnapshotEClass, COMPOSITE_SNAPSHOT__CREATED);
		createEReference(compositeSnapshotEClass, COMPOSITE_SNAPSHOT__PACKAGES);

		packageFingerprintEClass = createEClass(PACKAGE_FINGERPRINT);
		createEAttribute(packageFingerprintEClass, PACKAGE_FINGERPRINT__PACKAGE_URI);
		createEAttribute(packageFingerprintEClass, PACKAGE_FINGERPRINT__PACKAGE_FINGERPRINT);

		// Create enums
		deltaKindEEnum = createEEnum(DELTA_KIND);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes, features, and operations; add parameters
		initEClass(changeSetEClass, ChangeSet.class, "ChangeSet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getChangeSet_Id(), ecorePackage.getEString(), "id", null, 1, 1, ChangeSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getChangeSet_StreamId(), ecorePackage.getEString(), "streamId", null, 1, 1, ChangeSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getChangeSet_Sequence(), ecorePackage.getELong(), "sequence", null, 1, 1, ChangeSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getChangeSet_Timestamp(), ecorePackage.getELong(), "timestamp", null, 1, 1, ChangeSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getChangeSet_CommitTime(), ecorePackage.getELong(), "commitTime", null, 0, 1, ChangeSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getChangeSet_ContextFingerprint(), ecorePackage.getEString(), "contextFingerprint", null, 1, 1, ChangeSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getChangeSet_Author(), ecorePackage.getEString(), "author", null, 0, 1, ChangeSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getChangeSet_Cause(), ecorePackage.getEString(), "cause", null, 0, 1, ChangeSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getChangeSet_TransactionId(), ecorePackage.getEString(), "transactionId", null, 0, 1, ChangeSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getChangeSet_Manifest(), ecorePackage.getEBoolean(), "manifest", "false", 0, 1, ChangeSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getChangeSet_Entries(), this.getChangeEntry(), null, "entries", null, 0, -1, ChangeSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(changeEntryEClass, ChangeEntry.class, "ChangeEntry", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getChangeEntry_ObjectId(), ecorePackage.getEString(), "objectId", null, 1, 1, ChangeEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getChangeEntry_ClassId(), ecorePackage.getEInt(), "classId", "-1", 0, 1, ChangeEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getChangeEntry_Kind(), this.getDeltaKind(), "kind", null, 1, 1, ChangeEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getChangeEntry_FeatureId(), ecorePackage.getEInt(), "featureId", "-1", 0, 1, ChangeEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getChangeEntry_Index(), ecorePackage.getEInt(), "index", "-1", 0, 1, ChangeEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getChangeEntry_ToIndex(), ecorePackage.getEInt(), "toIndex", "-1", 0, 1, ChangeEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getChangeEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, ChangeEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getChangeEntry_Coords(), ecorePackage.getEInt(), "coords", null, 0, -1, ChangeEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getChangeEntry_ValueOld(), ecorePackage.getEString(), "valueOld", null, 0, 1, ChangeEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getChangeEntry_ValueNew(), ecorePackage.getEString(), "valueNew", null, 0, 1, ChangeEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getChangeEntry_State(), this.getSlotValue(), null, "state", null, 0, -1, ChangeEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(slotValueEClass, SlotValue.class, "SlotValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSlotValue_FeatureId(), ecorePackage.getEInt(), "featureId", null, 1, 1, SlotValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSlotValue_Index(), ecorePackage.getEInt(), "index", "-1", 0, 1, SlotValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSlotValue_Key(), ecorePackage.getEString(), "key", null, 0, 1, SlotValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSlotValue_Coords(), ecorePackage.getEInt(), "coords", null, 0, -1, SlotValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSlotValue_Value(), ecorePackage.getEString(), "value", null, 0, 1, SlotValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(contextSnapshotEClass, ContextSnapshot.class, "ContextSnapshot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getContextSnapshot_Fingerprint(), ecorePackage.getEString(), "fingerprint", null, 1, 1, ContextSnapshot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getContextSnapshot_PredecessorFingerprint(), ecorePackage.getEString(), "predecessorFingerprint", null, 0, 1, ContextSnapshot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getContextSnapshot_Created(), ecorePackage.getELong(), "created", null, 0, 1, ContextSnapshot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getContextSnapshot_Author(), ecorePackage.getEString(), "author", null, 0, 1, ContextSnapshot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getContextSnapshot_Labels(), ecorePackage.getEString(), "labels", null, 0, -1, ContextSnapshot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getContextSnapshot_PackageUri(), ecorePackage.getEString(), "packageUri", null, 1, 1, ContextSnapshot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getContextSnapshot_Content(), ecorePackage.getEObject(), null, "content", null, 0, -1, ContextSnapshot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(compositeSnapshotEClass, CompositeSnapshot.class, "CompositeSnapshot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getCompositeSnapshot_Fingerprint(), ecorePackage.getEString(), "fingerprint", null, 1, 1, CompositeSnapshot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCompositeSnapshot_PredecessorFingerprint(), ecorePackage.getEString(), "predecessorFingerprint", null, 0, 1, CompositeSnapshot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCompositeSnapshot_Created(), ecorePackage.getELong(), "created", null, 0, 1, CompositeSnapshot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCompositeSnapshot_Packages(), this.getPackageFingerprint(), null, "packages", null, 0, -1, CompositeSnapshot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(packageFingerprintEClass, PackageFingerprint.class, "PackageFingerprint", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPackageFingerprint_PackageUri(), ecorePackage.getEString(), "packageUri", null, 1, 1, PackageFingerprint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPackageFingerprint_PackageFingerprint(), ecorePackage.getEString(), "packageFingerprint", null, 1, 1, PackageFingerprint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(deltaKindEEnum, DeltaKind.class, "DeltaKind");
		addEEnumLiteral(deltaKindEEnum, DeltaKind.CREATE);
		addEEnumLiteral(deltaKindEEnum, DeltaKind.SET);
		addEEnumLiteral(deltaKindEEnum, DeltaKind.UNSET);
		addEEnumLiteral(deltaKindEEnum, DeltaKind.ADD);
		addEEnumLiteral(deltaKindEEnum, DeltaKind.REMOVE);
		addEEnumLiteral(deltaKindEEnum, DeltaKind.MOVE);
		addEEnumLiteral(deltaKindEEnum, DeltaKind.PUT);
		addEEnumLiteral(deltaKindEEnum, DeltaKind.REMOVE_KEY);
		addEEnumLiteral(deltaKindEEnum, DeltaKind.SET_AT);
		addEEnumLiteral(deltaKindEEnum, DeltaKind.RESHAPE);
		addEEnumLiteral(deltaKindEEnum, DeltaKind.DELETE);
		addEEnumLiteral(deltaKindEEnum, DeltaKind.KEYFRAME);
		addEEnumLiteral(deltaKindEEnum, DeltaKind.TOUCH);
		addEEnumLiteral(deltaKindEEnum, DeltaKind.MIGRATE_OUT);
		addEEnumLiteral(deltaKindEEnum, DeltaKind.MIGRATE_IN);

		// Create resource
		createResource(eNS_URI);
	}

} //StreamPackageImpl
