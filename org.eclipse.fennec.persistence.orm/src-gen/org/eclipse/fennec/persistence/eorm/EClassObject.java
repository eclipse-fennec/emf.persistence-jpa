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

import org.eclipse.emf.ecore.EClass;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EClass Object</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EClassObject#getEclass <em>Eclass</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEClassObject()
 * @model
 * @generated
 */
@ProviderType
public interface EClassObject extends EAccessor {
	/**
	 * Returns the value of the '<em><b>Eclass</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Eclass</em>' reference.
	 * @see #setEclass(EClass)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEClassObject_Eclass()
	 * @model
	 * @generated
	 */
	EClass getEclass();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.EClassObject#getEclass <em>Eclass</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Eclass</em>' reference.
	 * @see #getEclass()
	 * @generated
	 */
	void setEclass(EClass value);

} // EClassObject
