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

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Comparator</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.query.Comparator#getSuitableForType <em>Suitable For Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.Comparator#getWhere <em>Where</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.query.QueryPackage#getComparator()
 * @model abstract="true"
 * @generated
 */
@ProviderType
public interface Comparator extends EObject {
	/**
	 * Returns the value of the '<em><b>Suitable For Type</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.query.SuitableType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Suitable For Type</em>' attribute.
	 * @see org.eclipse.fennec.model.query.SuitableType
	 * @see #setSuitableForType(SuitableType)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getComparator_SuitableForType()
	 * @model required="true"
	 * @generated
	 */
	SuitableType getSuitableForType();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.Comparator#getSuitableForType <em>Suitable For Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Suitable For Type</em>' attribute.
	 * @see org.eclipse.fennec.model.query.SuitableType
	 * @see #getSuitableForType()
	 * @generated
	 */
	void setSuitableForType(SuitableType value);

	/**
	 * Returns the value of the '<em><b>Where</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.eclipse.fennec.model.query.QWhere#getComparator <em>Comparator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Where</em>' container reference.
	 * @see #setWhere(QWhere)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getComparator_Where()
	 * @see org.eclipse.fennec.model.query.QWhere#getComparator
	 * @model opposite="comparator" transient="false"
	 * @generated
	 */
	QWhere getWhere();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.Comparator#getWhere <em>Where</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Where</em>' container reference.
	 * @see #getWhere()
	 * @generated
	 */
	void setWhere(QWhere value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	boolean compare();

} // Comparator
