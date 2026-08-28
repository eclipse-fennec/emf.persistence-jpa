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

import org.eclipse.emf.ecore.EObject;

import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.PropertyPath;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Expand</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * One expansion: a non-containment reference path to resolve eagerly and batched, optionally narrowed by query options (issue #238, docs/unified-persistence/expand-query-options.md).
 * 
 * Both backends already deliver non-containment references as EMF proxies, so an expansion resolves proxies rather than shaping a result. The options therefore select WHICH proxies get resolved, never which entries the feature holds (D1): the collection keeps everything the store has, so the object cannot misreport it and writing it back loses nothing. What is delivered is exactly the selected set (D1b) — eIsProxy() discriminates the expansion from the untouched remainder.
 * 
 * An Expand carrying only a path is the plain fetch hint of issue #95, unchanged.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.query.Expand#getPath <em>Path</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.Expand#getFilter <em>Filter</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.Expand#getOrderBy <em>Order By</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.Expand#getTop <em>Top</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.Expand#getSkip <em>Skip</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.Expand#getExpand <em>Expand</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.query.QueryPackage#getExpand()
 * @model
 * @generated
 */
@ProviderType
public interface Expand extends EObject {
	/**
	 * Returns the value of the '<em><b>Path</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The reference path to expand; every segment must be an EReference.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Path</em>' containment reference.
	 * @see #setPath(PropertyPath)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getExpand_Path()
	 * @model containment="true" required="true"
	 * @generated
	 */
	PropertyPath getPath();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.Expand#getPath <em>Path</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Path</em>' containment reference.
	 * @see #getPath()
	 * @generated
	 */
	void setPath(PropertyPath value);

	/**
	 * Returns the value of the '<em><b>Filter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Narrows which children are resolved; addresses the expanded type, not the root. Requires the EXPAND_FILTER capability.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Filter</em>' containment reference.
	 * @see #setFilter(Expression)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getExpand_Filter()
	 * @model containment="true"
	 * @generated
	 */
	Expression getFilter();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.Expand#getFilter <em>Filter</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Filter</em>' containment reference.
	 * @see #getFilter()
	 * @generated
	 */
	void setFilter(Expression value);

	/**
	 * Returns the value of the '<em><b>Order By</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.query.OrderBy}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Selector input for top/skip — 'the five newest' is orderBy plus top. It is NOT a delivered order: under D1 the list order belongs to the store, and an expansion cannot reorder a feature it does not own. Therefore served only together with top or skip, and refused standing alone (D3). Requires EXPAND_PAGE.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Order By</em>' containment reference list.
	 * @see org.eclipse.fennec.model.query.QueryPackage#getExpand_OrderBy()
	 * @model containment="true"
	 * @generated
	 */
	EList<OrderBy> getOrderBy();

	/**
	 * Returns the value of the '<em><b>Top</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Resolve at most this many children per expanded parent; 0 = all. Requires EXPAND_PAGE.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Top</em>' attribute.
	 * @see #setTop(int)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getExpand_Top()
	 * @model
	 * @generated
	 */
	int getTop();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.Expand#getTop <em>Top</em>}' attribute.
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
	 * <!-- begin-model-doc -->
	 * Skip this many children per expanded parent before resolving. Requires EXPAND_PAGE.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Skip</em>' attribute.
	 * @see #setSkip(int)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getExpand_Skip()
	 * @model
	 * @generated
	 */
	int getSkip();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.Expand#getSkip <em>Skip</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Skip</em>' attribute.
	 * @see #getSkip()
	 * @generated
	 */
	void setSkip(int value);

	/**
	 * Returns the value of the '<em><b>Expand</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.query.Expand}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Nested expansions, relative to the expanded type. OData allows arbitrary depth.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Expand</em>' containment reference list.
	 * @see org.eclipse.fennec.model.query.QueryPackage#getExpand_Expand()
	 * @model containment="true"
	 * @generated
	 */
	EList<Expand> getExpand();

} // Expand
