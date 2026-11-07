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
package org.eclipse.fennec.model.command;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Update Entry</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * One patch step on one feature of the matched object. Values travel as EMF string literals, decoded through the attribute type's factory; on a non-containment reference they carry the target's id, resolved through the backend's keyed find (issue #107).
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.command.UpdateEntry#getKind <em>Kind</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.command.UpdateEntry#getFeature <em>Feature</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.command.UpdateEntry#getIndex <em>Index</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.command.UpdateEntry#getToIndex <em>To Index</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.command.UpdateEntry#getValue <em>Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.command.UpdateEntry#getOldValue <em>Old Value</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.command.CommandPackage#getUpdateEntry()
 * @model
 * @generated
 */
@ProviderType
public interface UpdateEntry extends EObject {
	/**
	 * Returns the value of the '<em><b>Kind</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.command.UpdateKind}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Kind</em>' attribute.
	 * @see org.eclipse.fennec.model.command.UpdateKind
	 * @see #setKind(UpdateKind)
	 * @see org.eclipse.fennec.model.command.CommandPackage#getUpdateEntry_Kind()
	 * @model required="true"
	 * @generated
	 */
	UpdateKind getKind();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.command.UpdateEntry#getKind <em>Kind</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Kind</em>' attribute.
	 * @see org.eclipse.fennec.model.command.UpdateKind
	 * @see #getKind()
	 * @generated
	 */
	void setKind(UpdateKind value);

	/**
	 * Returns the value of the '<em><b>Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The patched feature. It must be a feature of the selector's root type (own or inherited); anything else is refused. Containment references are object lifecycle and refused as well.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Feature</em>' reference.
	 * @see #setFeature(EStructuralFeature)
	 * @see org.eclipse.fennec.model.command.CommandPackage#getUpdateEntry_Feature()
	 * @model required="true"
	 * @generated
	 */
	EStructuralFeature getFeature();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.command.UpdateEntry#getFeature <em>Feature</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Feature</em>' reference.
	 * @see #getFeature()
	 * @generated
	 */
	void setFeature(EStructuralFeature value);

	/**
	 * Returns the value of the '<em><b>Index</b></em>' attribute.
	 * The default value is <code>"-1"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Position for ADD (insert at), REMOVE (remove at, attributes only) and MOVE (from). -1 = not given: ADD appends, REMOVE removes by oldValue.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Index</em>' attribute.
	 * @see #setIndex(int)
	 * @see org.eclipse.fennec.model.command.CommandPackage#getUpdateEntry_Index()
	 * @model default="-1"
	 * @generated
	 */
	int getIndex();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.command.UpdateEntry#getIndex <em>Index</em>}' attribute.
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
	 * Target position, MOVE only.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>To Index</em>' attribute.
	 * @see #setToIndex(int)
	 * @see org.eclipse.fennec.model.command.CommandPackage#getUpdateEntry_ToIndex()
	 * @model default="-1"
	 * @generated
	 */
	int getToIndex();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.command.UpdateEntry#getToIndex <em>To Index</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>To Index</em>' attribute.
	 * @see #getToIndex()
	 * @generated
	 */
	void setToIndex(int value);

	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The new value for SET and ADD: an encoded literal, or the target's id on a reference. null on SET clears the feature.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Value</em>' attribute.
	 * @see #setValue(String)
	 * @see org.eclipse.fennec.model.command.CommandPackage#getUpdateEntry_Value()
	 * @model
	 * @generated
	 */
	String getValue();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.command.UpdateEntry#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(String value);

	/**
	 * Returns the value of the '<em><b>Old Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The member REMOVE takes out when no index is given: an encoded literal, or the member's id on a reference (references are removed by id only). Not an optimistic guard: a template applies to many matches whose current values differ by design.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Old Value</em>' attribute.
	 * @see #setOldValue(String)
	 * @see org.eclipse.fennec.model.command.CommandPackage#getUpdateEntry_OldValue()
	 * @model
	 * @generated
	 */
	String getOldValue();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.command.UpdateEntry#getOldValue <em>Old Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Old Value</em>' attribute.
	 * @see #getOldValue()
	 * @generated
	 */
	void setOldValue(String value);

} // UpdateEntry
