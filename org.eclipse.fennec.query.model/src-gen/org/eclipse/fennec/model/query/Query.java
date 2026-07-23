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
package org.eclipse.fennec.model.query;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.PropertyPath;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Query</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * A backend-neutral read query. The shape of its result follows the content: countOnly → COUNT; apply with aggregation → AGGREGATION rows; select → PROJECTION rows; otherwise whole OBJECTS.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.query.Query#getFrom <em>From</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.Query#getPredicate <em>Predicate</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.Query#getOrderBy <em>Order By</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.Query#getSelect <em>Select</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.Query#getApply <em>Apply</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.Query#getExpand <em>Expand</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.Query#getTop <em>Top</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.Query#getSkip <em>Skip</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.Query#isDistinct <em>Distinct</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.Query#isCountOnly <em>Count Only</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.Query#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.Query#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.Query#isSaveQuery <em>Save Query</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.query.QueryPackage#getQuery()
 * @model
 * @generated
 */
@ProviderType
public interface Query extends EObject {
	/**
	 * Returns the value of the '<em><b>From</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The root type the query selects from — the type filter.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>From</em>' reference.
	 * @see #setFrom(EClass)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getQuery_From()
	 * @model required="true"
	 * @generated
	 */
	EClass getFrom();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.Query#getFrom <em>From</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>From</em>' reference.
	 * @see #getFrom()
	 * @generated
	 */
	void setFrom(EClass value);

	/**
	 * Returns the value of the '<em><b>Predicate</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The boolean filter expression; absent = match all.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Predicate</em>' containment reference.
	 * @see #setPredicate(Expression)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getQuery_Predicate()
	 * @model containment="true"
	 * @generated
	 */
	Expression getPredicate();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.Query#getPredicate <em>Predicate</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Predicate</em>' containment reference.
	 * @see #getPredicate()
	 * @generated
	 */
	void setPredicate(Expression value);

	/**
	 * Returns the value of the '<em><b>Order By</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.query.OrderBy}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Order By</em>' containment reference list.
	 * @see org.eclipse.fennec.model.query.QueryPackage#getQuery_OrderBy()
	 * @model containment="true"
	 * @generated
	 */
	EList<OrderBy> getOrderBy();

	/**
	 * Returns the value of the '<em><b>Select</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.query.Selection}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Projection subjects; non-empty select makes the query row-shaped.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Select</em>' containment reference list.
	 * @see org.eclipse.fennec.model.query.QueryPackage#getQuery_Select()
	 * @model containment="true"
	 * @generated
	 */
	EList<Selection> getSelect();

	/**
	 * Returns the value of the '<em><b>Apply</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Optional aggregation pipeline, modelled after the OData $apply composition.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Apply</em>' containment reference.
	 * @see #setApply(Pipeline)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getQuery_Apply()
	 * @model containment="true"
	 * @generated
	 */
	Pipeline getApply();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.Query#getApply <em>Apply</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Apply</em>' containment reference.
	 * @see #getApply()
	 * @generated
	 */
	void setApply(Pipeline value);

	/**
	 * Returns the value of the '<em><b>Expand</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.expression.PropertyPath}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Fetch hints: reference paths to materialise eagerly with the result (decision D5 — envelope-level).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Expand</em>' containment reference list.
	 * @see org.eclipse.fennec.model.query.QueryPackage#getQuery_Expand()
	 * @model containment="true"
	 * @generated
	 */
	EList<PropertyPath> getExpand();

	/**
	 * Returns the value of the '<em><b>Top</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Result cap; 0 = unlimited.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Top</em>' attribute.
	 * @see #setTop(int)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getQuery_Top()
	 * @model
	 * @generated
	 */
	int getTop();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.Query#getTop <em>Top</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Top</em>' attribute.
	 * @see #getTop()
	 * @generated
	 */
	void setTop(int value);

	/**
	 * Returns the value of the '<em><b>Skip</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Skip</em>' attribute.
	 * @see #setSkip(int)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getQuery_Skip()
	 * @model
	 * @generated
	 */
	int getSkip();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.Query#getSkip <em>Skip</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Skip</em>' attribute.
	 * @see #getSkip()
	 * @generated
	 */
	void setSkip(int value);

	/**
	 * Returns the value of the '<em><b>Distinct</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Distinct</em>' attribute.
	 * @see #setDistinct(boolean)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getQuery_Distinct()
	 * @model
	 * @generated
	 */
	boolean isDistinct();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.Query#isDistinct <em>Distinct</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Distinct</em>' attribute.
	 * @see #isDistinct()
	 * @generated
	 */
	void setDistinct(boolean value);

	/**
	 * Returns the value of the '<em><b>Count Only</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Count Only</em>' attribute.
	 * @see #setCountOnly(boolean)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getQuery_CountOnly()
	 * @model
	 * @generated
	 */
	boolean isCountOnly();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.Query#isCountOnly <em>Count Only</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Count Only</em>' attribute.
	 * @see #isCountOnly()
	 * @generated
	 */
	void setCountOnly(boolean value);

	/**
	 * Returns the value of the '<em><b>Parameters</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.query.ParameterDecl}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Declared parameters of a prepared query. Every ParameterRef in the predicate should be declared; binding happens at execution.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Parameters</em>' containment reference list.
	 * @see org.eclipse.fennec.model.query.QueryPackage#getQuery_Parameters()
	 * @model containment="true"
	 * @generated
	 */
	EList<ParameterDecl> getParameters();

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Query name; required when saveQuery is set.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getQuery_Name()
	 * @model id="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.Query#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Save Query</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Marks the query to be persisted for later reuse (dogfooding — queries are EMF objects).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Save Query</em>' attribute.
	 * @see #setSaveQuery(boolean)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getQuery_SaveQuery()
	 * @model
	 * @generated
	 */
	boolean isSaveQuery();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.Query#isSaveQuery <em>Save Query</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Save Query</em>' attribute.
	 * @see #isSaveQuery()
	 * @generated
	 */
	void setSaveQuery(boolean value);

} // Query
