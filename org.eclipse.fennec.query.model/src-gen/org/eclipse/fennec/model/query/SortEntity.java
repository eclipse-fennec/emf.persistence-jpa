/*
 * Copyright (c) 2012 - 2026 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.model.query;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Sort Entity</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.query.SortEntity#getSortOrder <em>Sort Order</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.SortEntity#getSortFeature <em>Sort Feature</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.query.QueryPackage#getSortEntity()
 * @model
 * @generated
 */
@ProviderType
public interface SortEntity extends EObject {
	/**
	 * Returns the value of the '<em><b>Sort Order</b></em>' attribute.
	 * The default value is <code>"DESC"</code>.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.query.SortOrder}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sort Order</em>' attribute.
	 * @see org.eclipse.fennec.model.query.SortOrder
	 * @see #setSortOrder(SortOrder)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getSortEntity_SortOrder()
	 * @model default="DESC" required="true"
	 * @generated
	 */
	SortOrder getSortOrder();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.SortEntity#getSortOrder <em>Sort Order</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sort Order</em>' attribute.
	 * @see org.eclipse.fennec.model.query.SortOrder
	 * @see #getSortOrder()
	 * @generated
	 */
	void setSortOrder(SortOrder value);

	/**
	 * Returns the value of the '<em><b>Sort Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sort Feature</em>' reference.
	 * @see #setSortFeature(EStructuralFeature)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getSortEntity_SortFeature()
	 * @model required="true"
	 * @generated
	 */
	EStructuralFeature getSortFeature();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.SortEntity#getSortFeature <em>Sort Feature</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sort Feature</em>' reference.
	 * @see #getSortFeature()
	 * @generated
	 */
	void setSortFeature(EStructuralFeature value);

} // SortEntity
