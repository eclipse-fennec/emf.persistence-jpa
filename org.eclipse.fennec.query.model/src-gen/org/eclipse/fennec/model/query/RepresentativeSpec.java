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

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Representative Spec</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * How many documents of each group to hand out, and in which order (issue #214). The alias names one result cell that holds them as a list of EObjects — the shape decision R1: QueryResultRow cells are typed Object already, so this needs no SPI change and keeps grouped answers in the same row space as everything else. There is deliberately no total-count field: the group's full size is an ordinary COUNT aggregate on the same stage, so a truncated group shows as count greater than the number of representatives.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.query.RepresentativeSpec#getAlias <em>Alias</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.RepresentativeSpec#getCount <em>Count</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.RepresentativeSpec#getOffset <em>Offset</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.RepresentativeSpec#getOrderBy <em>Order By</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.query.QueryPackage#getRepresentativeSpec()
 * @model
 * @generated
 */
@ProviderType
public interface RepresentativeSpec extends EObject {
	/**
	 * Returns the value of the '<em><b>Alias</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Alias</em>' attribute.
	 * @see #setAlias(String)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getRepresentativeSpec_Alias()
	 * @model required="true"
	 * @generated
	 */
	String getAlias();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.RepresentativeSpec#getAlias <em>Alias</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Alias</em>' attribute.
	 * @see #getAlias()
	 * @generated
	 */
	void setAlias(String value);

	/**
	 * Returns the value of the '<em><b>Count</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * How many per group — a literal or a bound parameter, never a computed expression: an inverted index has to construct its search with the number known, the same rule MapValue's key carries. A count of zero or less is a validation error.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Count</em>' containment reference.
	 * @see #setCount(Expression)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getRepresentativeSpec_Count()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Expression getCount();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.RepresentativeSpec#getCount <em>Count</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Count</em>' containment reference.
	 * @see #getCount()
	 * @generated
	 */
	void setCount(Expression value);

	/**
	 * Returns the value of the '<em><b>Offset</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Where to start within the group, same constant rule as count. An offset past the end of a group yields an empty cell — the group's row, keys and aggregates still appear.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Offset</em>' containment reference.
	 * @see #setOffset(Expression)
	 * @see org.eclipse.fennec.model.query.QueryPackage#getRepresentativeSpec_Offset()
	 * @model containment="true"
	 * @generated
	 */
	Expression getOffset();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.query.RepresentativeSpec#getOffset <em>Offset</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Offset</em>' containment reference.
	 * @see #getOffset()
	 * @generated
	 */
	void setOffset(Expression value);

	/**
	 * Returns the value of the '<em><b>Order By</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.query.OrderBy}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The order WITHIN a group, kept apart from the order BETWEEN groups (which is the envelope's ordinary row ordering). Deliberately without a fallback to the envelope's orderBy: a query with representatives is grouped, so its envelope ordering addresses output columns rather than the documents inside a group. Left empty, the window is unspecified. Ordering groups by their best representative needs no vocabulary: a MIN/MAX aggregate over the sort key plus orderBy on that alias — a backend may recognise the pattern and use its native group sort, as optimisation, not semantics.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Order By</em>' containment reference list.
	 * @see org.eclipse.fennec.model.query.QueryPackage#getRepresentativeSpec_OrderBy()
	 * @model containment="true"
	 * @generated
	 */
	EList<OrderBy> getOrderBy();

} // RepresentativeSpec
