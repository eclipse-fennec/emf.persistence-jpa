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
 * A representation of the model object '<em><b>Map Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Value of one map entry, addressed by its key (issue #186). map addresses an EMap feature - in Ecore a containment-many reference to an entry class with key and value features - and key selects the entry. The key must be a Literal or a ParameterRef, never a computed expression: Mongo and Lucene turn a map key into a field name, so it has to be knowable when the query is translated, and an IR construct only one backend could serve would be a capability trap. JPA renders a correlated subselect over the entry table (SELECT e.value ... WHERE e.key = ?); Mongo the field path map.key into the stored sub-document. Refused with a diagnostic when the path does not end in a map or the key is not constant.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.MapValue#getMap <em>Map</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.MapValue#getKey <em>Key</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getMapValue()
 * @model
 * @generated
 */
@ProviderType
public interface MapValue extends Expression {
	/**
	 * Returns the value of the '<em><b>Map</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Navigation to the map feature; the last segment must be an EMap.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Map</em>' containment reference.
	 * @see #setMap(PropertyPath)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getMapValue_Map()
	 * @model containment="true" required="true"
	 * @generated
	 */
	PropertyPath getMap();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.MapValue#getMap <em>Map</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Map</em>' containment reference.
	 * @see #getMap()
	 * @generated
	 */
	void setMap(PropertyPath value);

	/**
	 * Returns the value of the '<em><b>Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The entry key - a Literal or a ParameterRef, so the key is known at translation time.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Key</em>' containment reference.
	 * @see #setKey(Expression)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getMapValue_Key()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Expression getKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.MapValue#getKey <em>Key</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Key</em>' containment reference.
	 * @see #getKey()
	 * @generated
	 */
	void setKey(Expression value);

} // MapValue
