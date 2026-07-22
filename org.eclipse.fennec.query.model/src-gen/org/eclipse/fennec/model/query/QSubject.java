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

import org.eclipse.fennec.model.utilities.FeaturePath;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>QSubject</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.query.QSubject#getFeaturePath <em>Feature Path</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.QSubject#isIsExclude <em>Is Exclude</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.QSubject#getAlias <em>Alias</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.QSubject#getAliasFeature <em>Alias Feature</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.QSubject#getOperation <em>Operation</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.query.QueryPackage#getQSubject()
 * @model
 * @generated
 */
@ProviderType
public interface QSubject extends EObject {
	/**
	 * Returns the value of the '<em><b>Feature Path</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * This is the path for the subject like SELECT [Person.address, Address.street] to select an Address Street out of a Person object
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Feature Path</em>' containment reference.
	 * @see #setFeaturePath(FeaturePath)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getQSubject_FeaturePath()
	 * @model containment="true" required="true"
	 * @generated
	 */
	FeaturePath getFeaturePath();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.QSubject#getFeaturePath <em>Feature Path</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Feature Path</em>' containment reference.
	 * @see #getFeaturePath()
	 * @generated
	 */
	void setFeaturePath(FeaturePath value);

	/**
	 * Returns the value of the '<em><b>Is Exclude</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * If set to true, all feature path of the subject are excluded from the SELECT  query
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Is Exclude</em>' attribute.
	 * @see #setIsExclude(boolean)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getQSubject_IsExclude()
	 * @model default="false"
	 * @generated
	 */
	boolean isIsExclude();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.QSubject#isIsExclude <em>Is Exclude</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is Exclude</em>' attribute.
	 * @see #isIsExclude()
	 * @generated
	 */
	void setIsExclude(boolean value);

	/**
	 * Returns the value of the '<em><b>Alias</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * This is something like SELECT person AS <alias>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Alias</em>' attribute.
	 * @see #setAlias(String)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getQSubject_Alias()
	 * @model
	 * @generated
	 */
	String getAlias();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.QSubject#getAlias <em>Alias</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Alias</em>' attribute.
	 * @see #getAlias()
	 * @generated
	 */
	void setAlias(String value);

	/**
	 * Returns the value of the '<em><b>Alias Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * This is a optional feature the resulting value can be mapped to
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Alias Feature</em>' reference.
	 * @see #setAliasFeature(EStructuralFeature)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getQSubject_AliasFeature()
	 * @model
	 * @generated
	 */
	EStructuralFeature getAliasFeature();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.QSubject#getAliasFeature <em>Alias Feature</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Alias Feature</em>' reference.
	 * @see #getAliasFeature()
	 * @generated
	 */
	void setAliasFeature(EStructuralFeature value);

	/**
	 * Returns the value of the '<em><b>Operation</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * An operation defines additional functions, executed on the returning value/values like toUpper, avg, count
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Operation</em>' containment reference.
	 * @see #setOperation(Operation)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getQSubject_Operation()
	 * @model containment="true"
	 * @generated
	 */
	Operation getOperation();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.QSubject#getOperation <em>Operation</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Operation</em>' containment reference.
	 * @see #getOperation()
	 * @generated
	 */
	void setOperation(Operation value);

} // QSubject
