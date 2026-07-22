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

import org.eclipse.fennec.model.utilities.FeaturePath;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>QWhere</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.query.QWhere#getFeaturePath <em>Feature Path</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.QWhere#getComparator <em>Comparator</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.QWhere#getOperation <em>Operation</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.query.QueryPackage#getQWhere()
 * @model abstract="true"
 * @generated
 */
@ProviderType
public interface QWhere extends EObject {
	/**
	 * Returns the value of the '<em><b>Feature Path</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * This is the feature path on which the (optional) operation and the comparator have to be applied (like Person.name -> toLowerCase -> startsWith("something"))
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Feature Path</em>' containment reference.
	 * @see #setFeaturePath(FeaturePath)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getQWhere_FeaturePath()
	 * @model containment="true" required="true"
	 * @generated
	 */
	FeaturePath getFeaturePath();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.QWhere#getFeaturePath <em>Feature Path</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Feature Path</em>' containment reference.
	 * @see #getFeaturePath()
	 * @generated
	 */
	void setFeaturePath(FeaturePath value);

	/**
	 * Returns the value of the '<em><b>Comparator</b></em>' containment reference.
	 * It is bidirectional and its opposite is '{@link org.eclipse.fennec.model.query.Comparator#getWhere <em>Where</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * This is the comparator to be applied to the feature path (this is the actual predicate of the query)
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Comparator</em>' containment reference.
	 * @see #setComparator(Comparator)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getQWhere_Comparator()
	 * @see org.eclipse.fennec.model.query.Comparator#getWhere
	 * @model opposite="where" containment="true" required="true"
	 * @generated
	 */
	Comparator getComparator();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.QWhere#getComparator <em>Comparator</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Comparator</em>' containment reference.
	 * @see #getComparator()
	 * @generated
	 */
	void setComparator(Comparator value);

	/**
	 * Returns the value of the '<em><b>Operation</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * This is an optional function to be applied to the feature path before the comparator 
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Operation</em>' containment reference.
	 * @see #setOperation(Operation)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getQWhere_Operation()
	 * @model containment="true"
	 * @generated
	 */
	Operation getOperation();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.QWhere#getOperation <em>Operation</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Operation</em>' containment reference.
	 * @see #getOperation()
	 * @generated
	 */
	void setOperation(Operation value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	boolean execute();

} // QWhere
