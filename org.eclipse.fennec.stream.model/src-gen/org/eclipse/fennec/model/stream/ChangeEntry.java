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
 * A representation of the model object '<em><b>Change Entry</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * One delta with the coordinates (objectId, featureId, address, valueOld, valueNew) — concept §5.1. Every entry is invertible: undo swaps valueOld/valueNew and reverses ADD/REMOVE, PUT/REMOVE_KEY etc. (§5.2). valueOld doubles as an optimistic-concurrency guard on apply (§13).
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.stream.ChangeEntry#getObjectId <em>Object Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ChangeEntry#getClassId <em>Class Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ChangeEntry#getKind <em>Kind</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ChangeEntry#getFeatureId <em>Feature Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ChangeEntry#getIndex <em>Index</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ChangeEntry#getToIndex <em>To Index</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ChangeEntry#getKey <em>Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ChangeEntry#getCoords <em>Coords</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ChangeEntry#getValueOld <em>Value Old</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ChangeEntry#getValueNew <em>Value New</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.ChangeEntry#getState <em>State</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.stream.StreamPackage#getChangeEntry()
 * @model
 * @generated
 */
@ProviderType
public interface ChangeEntry extends EObject {
	/**
	 * Returns the value of the '<em><b>Object Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Stable object id (§4.1). Containment children carry their own id but live in the root's stream.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Object Id</em>' attribute.
	 * @see #setObjectId(String)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getChangeEntry_ObjectId()
	 * @model required="true"
	 * @generated
	 */
	String getObjectId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.ChangeEntry#getObjectId <em>Object Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Object Id</em>' attribute.
	 * @see #getObjectId()
	 * @generated
	 */
	void setObjectId(String value);

	/**
	 * Returns the value of the '<em><b>Class Id</b></em>' attribute.
	 * The default value is <code>"-1"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Stable class id, resolved via (contextFingerprint, classId) — assigned in the tracking aspect (tracking.ecore). Required for CREATE; redundant otherwise.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Class Id</em>' attribute.
	 * @see #setClassId(int)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getChangeEntry_ClassId()
	 * @model default="-1"
	 * @generated
	 */
	int getClassId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.ChangeEntry#getClassId <em>Class Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Class Id</em>' attribute.
	 * @see #getClassId()
	 * @generated
	 */
	void setClassId(int value);

	/**
	 * Returns the value of the '<em><b>Kind</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.stream.DeltaKind}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The delta kind (concept §5.2). Determines which address and value fields of this entry apply.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Kind</em>' attribute.
	 * @see org.eclipse.fennec.model.stream.DeltaKind
	 * @see #setKind(DeltaKind)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getChangeEntry_Kind()
	 * @model required="true"
	 * @generated
	 */
	DeltaKind getKind();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.ChangeEntry#getKind <em>Kind</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Kind</em>' attribute.
	 * @see org.eclipse.fennec.model.stream.DeltaKind
	 * @see #getKind()
	 * @generated
	 */
	void setKind(DeltaKind value);

	/**
	 * Returns the value of the '<em><b>Feature Id</b></em>' attribute.
	 * The default value is <code>"-1"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Stable feature id (§4.2, protobuf discipline: bound to semantics, never renamed away, never reused). Absent (-1) for CREATE/DELETE/KEYFRAME.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Feature Id</em>' attribute.
	 * @see #setFeatureId(int)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getChangeEntry_FeatureId()
	 * @model default="-1"
	 * @generated
	 */
	int getFeatureId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.ChangeEntry#getFeatureId <em>Feature Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Feature Id</em>' attribute.
	 * @see #getFeatureId()
	 * @generated
	 */
	void setFeatureId(int value);

	/**
	 * Returns the value of the '<em><b>Index</b></em>' attribute.
	 * The default value is <code>"-1"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Address for ordered lists (ADD/REMOVE/MOVE). -1 = not applicable (§5.4 addressing modes).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Index</em>' attribute.
	 * @see #setIndex(int)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getChangeEntry_Index()
	 * @model default="-1"
	 * @generated
	 */
	int getIndex();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.ChangeEntry#getIndex <em>Index</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Index</em>' attribute.
	 * @see #getIndex()
	 * @generated
	 */
	void setIndex(int value);

	/**
	 * Returns the value of the '<em><b>To Index</b></em>' attribute.
	 * The default value is <code>"-1"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Target index, MOVE only.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>To Index</em>' attribute.
	 * @see #setToIndex(int)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getChangeEntry_ToIndex()
	 * @model default="-1"
	 * @generated
	 */
	int getToIndex();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.ChangeEntry#getToIndex <em>To Index</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>To Index</em>' attribute.
	 * @see #getToIndex()
	 * @generated
	 */
	void setToIndex(int value);

	/**
	 * Returns the value of the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Address for maps (PUT/REMOVE_KEY): encoded literal or objectId for object keys (§5.4h).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Key</em>' attribute.
	 * @see #setKey(String)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getChangeEntry_Key()
	 * @model
	 * @generated
	 */
	String getKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.ChangeEntry#getKey <em>Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Key</em>' attribute.
	 * @see #getKey()
	 * @generated
	 */
	void setKey(String value);

	/**
	 * Returns the value of the '<em><b>Coords</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.Integer}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Address for arrays (SET_AT): n-dimensional coordinate vector (§5.4i).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Coords</em>' attribute list.
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getChangeEntry_Coords()
	 * @model
	 * @generated
	 */
	EList<Integer> getCoords();

	/**
	 * Returns the value of the '<em><b>Value Old</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Previous value: encoded literal, or objectId for references. Enables inversion, conflict detection and the apply-time guard. For RESHAPE: the old dimension vector, encoded.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Value Old</em>' attribute.
	 * @see #setValueOld(String)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getChangeEntry_ValueOld()
	 * @model
	 * @generated
	 */
	String getValueOld();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.ChangeEntry#getValueOld <em>Value Old</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value Old</em>' attribute.
	 * @see #getValueOld()
	 * @generated
	 */
	void setValueOld(String value);

	/**
	 * Returns the value of the '<em><b>Value New</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * New value: encoded literal, or objectId for references. For RESHAPE: the new dimension vector, encoded.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Value New</em>' attribute.
	 * @see #setValueNew(String)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getChangeEntry_ValueNew()
	 * @model
	 * @generated
	 */
	String getValueNew();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.ChangeEntry#getValueNew <em>Value New</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value New</em>' attribute.
	 * @see #getValueNew()
	 * @generated
	 */
	void setValueNew(String value);

	/**
	 * Returns the value of the '<em><b>State</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.stream.SlotValue}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * KEYFRAME only: the full tracked state of the object as a flat list of addressed slots (§10).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>State</em>' containment reference list.
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getChangeEntry_State()
	 * @model containment="true"
	 * @generated
	 */
	EList<SlotValue> getState();

} // ChangeEntry
