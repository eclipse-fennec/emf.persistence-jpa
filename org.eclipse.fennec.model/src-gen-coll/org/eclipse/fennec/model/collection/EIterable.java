/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 * 
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 */
package org.eclipse.fennec.model.collection;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EIterable</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.collection.EIterable#getDelegate <em>Delegate</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.collection.CollectionPackage#getEIterable()
 * @model superTypes="org.eclipse.fennec.model.collection.EIterableInterface"
 * @generated
 */
@ProviderType
public interface EIterable extends EObject, Iterable<EObject> {
	/**
	 * Returns the value of the '<em><b>Delegate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Delegate</em>' attribute.
	 * @see #setDelegate(Iterable)
	 * @see org.eclipse.fennec.model.collection.CollectionPackage#getEIterable_Delegate()
	 * @model dataType="org.eclipse.fennec.model.collection.Iterable"
	 * @generated
	 */
	Iterable<EObject> getDelegate();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.collection.EIterable#getDelegate <em>Delegate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Delegate</em>' attribute.
	 * @see #getDelegate()
	 * @generated
	 */
	void setDelegate(Iterable<EObject> value);

} // EIterable
