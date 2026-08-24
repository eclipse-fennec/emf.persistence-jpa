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
 * A representation of the model object '<em><b>Root Reference</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * A value read from ONE fixed object of another entity set (issue #241) — OData's $root ([OData-URL] 4.13): "Price gt $root/Products('reference-sku')/Price". A VALUE expression, not a predicate: it composes with comparisons the way GeoDistance does.
 * 
 * Semantics, fixed here rather than per backend: no match yields NULL and the enclosing comparison goes UNKNOWN under the 3VL discipline (issue #94) — what SQL does with an empty scalar subquery, and what makes it composable without a second error path. MORE than one match is a query error, never a silent first-row pick.
 * 
 * The key is a predicate over the referenced type. When it is CONSTANT — which is all OData's syntax can produce, since $root takes a literal key predicate — a backend may resolve the referenced value once before translating and inline it as a literal, which is what both of ours do. A non-constant key would need a correlated scalar subquery and is not served: declared, refused, not guessed.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.RootReference#getFrom <em>From</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.RootReference#getKey <em>Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.RootReference#getPath <em>Path</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getRootReference()
 * @model
 * @generated
 */
@ProviderType
public interface RootReference extends Expression {
	/**
	 * Returns the value of the '<em><b>From</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>From</em>' reference.
	 * @see #setFrom(EClass)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getRootReference_From()
	 * @model required="true"
	 * @generated
	 */
	EClass getFrom();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.RootReference#getFrom <em>From</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>From</em>' reference.
	 * @see #getFrom()
	 * @generated
	 */
	void setFrom(EClass value);

	/**
	 * Returns the value of the '<em><b>Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Key</em>' containment reference.
	 * @see #setKey(Expression)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getRootReference_Key()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Expression getKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.RootReference#getKey <em>Key</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Key</em>' containment reference.
	 * @see #getKey()
	 * @generated
	 */
	void setKey(Expression value);

	/**
	 * Returns the value of the '<em><b>Path</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Path</em>' containment reference.
	 * @see #setPath(PropertyPath)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getRootReference_Path()
	 * @model containment="true" required="true"
	 * @generated
	 */
	PropertyPath getPath();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.RootReference#getPath <em>Path</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Path</em>' containment reference.
	 * @see #getPath()
	 * @generated
	 */
	void setPath(PropertyPath value);

} // RootReference
