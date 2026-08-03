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

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Index Of</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Position of the first occurrence of search inside source — 0-based, -1 when absent (issue #77; OData/Java semantics — the 1-based JPQL LOCATE translation compensates, Mongo $indexOfCP matches natively).
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.IndexOf#getSource <em>Source</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.IndexOf#getSearch <em>Search</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getIndexOf()
 * @model
 * @generated
 */
@ProviderType
public interface IndexOf extends Expression {
	/**
	 * Returns the value of the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Source</em>' containment reference.
	 * @see #setSource(Expression)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getIndexOf_Source()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Expression getSource();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.IndexOf#getSource <em>Source</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source</em>' containment reference.
	 * @see #getSource()
	 * @generated
	 */
	void setSource(Expression value);

	/**
	 * Returns the value of the '<em><b>Search</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Search</em>' containment reference.
	 * @see #setSearch(Expression)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getIndexOf_Search()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Expression getSearch();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.IndexOf#getSearch <em>Search</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Search</em>' containment reference.
	 * @see #getSearch()
	 * @generated
	 */
	void setSearch(Expression value);

} // IndexOf
