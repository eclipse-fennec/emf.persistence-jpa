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
package org.eclipse.fennec.persistence.eorm;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EORMElement#getAccessibleObject <em>Accessible Object</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEORMElement()
 * @model
 * @generated
 */
@ProviderType
public interface EORMElement extends EObject {
	/**
	 * Returns the value of the '<em><b>Accessible Object</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Accessible Object</em>' containment reference.
	 * @see #setAccessibleObject(EAccessor)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEORMElement_AccessibleObject()
	 * @model containment="true" keys="name"
	 * @generated
	 */
	EAccessor getAccessibleObject();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.EORMElement#getAccessibleObject <em>Accessible Object</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Accessible Object</em>' containment reference.
	 * @see #getAccessibleObject()
	 * @generated
	 */
	void setAccessibleObject(EAccessor value);

} // EORMElement
