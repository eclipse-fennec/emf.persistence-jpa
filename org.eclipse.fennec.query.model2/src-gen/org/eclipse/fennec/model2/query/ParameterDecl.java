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
package org.eclipse.fennec.model2.query;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Parameter Decl</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model2.query.ParameterDecl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.model2.query.ParameterDecl#getTypeHint <em>Type Hint</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model2.query.QueryPackage#getParameterDecl()
 * @model
 * @generated
 */
@ProviderType
public interface ParameterDecl extends EObject {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.fennec.model2.query.QueryPackage#getParameterDecl_Name()
	 * @model required="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model2.query.ParameterDecl#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Type Hint</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Optional expected type of the bound value.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Type Hint</em>' reference.
	 * @see #setTypeHint(EClassifier)
	 * @see org.eclipse.fennec.model2.query.QueryPackage#getParameterDecl_TypeHint()
	 * @model
	 * @generated
	 */
	EClassifier getTypeHint();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model2.query.ParameterDecl#getTypeHint <em>Type Hint</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type Hint</em>' reference.
	 * @see #getTypeHint()
	 * @generated
	 */
	void setTypeHint(EClassifier value);

} // ParameterDecl
