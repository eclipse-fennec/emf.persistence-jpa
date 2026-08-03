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
package org.eclipse.fennec.model.expression;

import org.eclipse.emf.ecore.EClass;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Type Check</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Type test with kind-of semantics (issue #80; OData isof, OCL oclIsKindOf): true when the subject's EClass is the given type or one of its subtypes. Unset source = the query root. JPA renders TYPE(x) IN (concrete subtypes by entity name) — the dynamic Java classes are deliberately flat, entity names sidestep Java assignability; Mongo refuses via capability until documents carry a type discriminator.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.TypeCheck#getSource <em>Source</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.TypeCheck#getType <em>Type</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getTypeCheck()
 * @model
 * @generated
 */
@ProviderType
public interface TypeCheck extends Expression {
	/**
	 * Returns the value of the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The navigated subject; unset tests the query root itself.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Source</em>' containment reference.
	 * @see #setSource(PropertyPath)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getTypeCheck_Source()
	 * @model containment="true"
	 * @generated
	 */
	PropertyPath getSource();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.TypeCheck#getSource <em>Source</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source</em>' containment reference.
	 * @see #getSource()
	 * @generated
	 */
	void setSource(PropertyPath value);

	/**
	 * Returns the value of the '<em><b>Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' reference.
	 * @see #setType(EClass)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getTypeCheck_Type()
	 * @model required="true"
	 * @generated
	 */
	EClass getType();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.TypeCheck#getType <em>Type</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' reference.
	 * @see #getType()
	 * @generated
	 */
	void setType(EClass value);

} // TypeCheck
