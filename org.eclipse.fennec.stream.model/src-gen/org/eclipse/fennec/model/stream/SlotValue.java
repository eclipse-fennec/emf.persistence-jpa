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
 * A representation of the model object '<em><b>Slot Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * One addressed slot within a keyframe: featureId plus the same addressing modes as ChangeEntry (index for ordered lists, key for maps, coords for arrays; none for single-valued).
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.stream.SlotValue#getFeatureId <em>Feature Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.SlotValue#getIndex <em>Index</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.SlotValue#getKey <em>Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.SlotValue#getCoords <em>Coords</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.stream.SlotValue#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.stream.StreamPackage#getSlotValue()
 * @model
 * @generated
 */
@ProviderType
public interface SlotValue extends EObject {
	/**
	 * Returns the value of the '<em><b>Feature Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Stable feature id of the slot's feature (§4.2).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Feature Id</em>' attribute.
	 * @see #setFeatureId(int)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getSlotValue_FeatureId()
	 * @model required="true"
	 * @generated
	 */
	int getFeatureId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.SlotValue#getFeatureId <em>Feature Id</em>}' attribute.
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
	 * Slot address for ordered lists; -1 = not applicable.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Index</em>' attribute.
	 * @see #setIndex(int)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getSlotValue_Index()
	 * @model default="-1"
	 * @generated
	 */
	int getIndex();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.SlotValue#getIndex <em>Index</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Index</em>' attribute.
	 * @see #getIndex()
	 * @generated
	 */
	void setIndex(int value);

	/**
	 * Returns the value of the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Slot address for maps: encoded literal or objectId for object keys.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Key</em>' attribute.
	 * @see #setKey(String)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getSlotValue_Key()
	 * @model
	 * @generated
	 */
	String getKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.SlotValue#getKey <em>Key</em>}' attribute.
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
	 * Slot address for arrays: n-dimensional coordinate vector.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Coords</em>' attribute list.
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getSlotValue_Coords()
	 * @model
	 * @generated
	 */
	EList<Integer> getCoords();

	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The slot's value: encoded literal, or objectId for references — typing via (contextFingerprint, featureId), like ChangeEntry values.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Value</em>' attribute.
	 * @see #setValue(String)
	 * @see org.eclipse.fennec.model.stream.StreamPackage#getSlotValue_Value()
	 * @model
	 * @generated
	 */
	String getValue();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.stream.SlotValue#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(String value);

} // SlotValue
