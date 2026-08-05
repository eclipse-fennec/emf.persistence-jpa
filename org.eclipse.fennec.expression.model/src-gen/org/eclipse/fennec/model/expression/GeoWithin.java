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
 * A representation of the model object '<em><b>Geo Within</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Containment of the subject's position in a box or polygon (issue #101). Null/unset coordinates make the predicate UNKNOWN per the 3VL discipline (issue #94). Capability GEO_WITHIN.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.GeoWithin#getSubject <em>Subject</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.GeoWithin#getShape <em>Shape</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getGeoWithin()
 * @model
 * @generated
 */
@ProviderType
public interface GeoWithin extends Expression {
	/**
	 * Returns the value of the '<em><b>Subject</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Subject</em>' containment reference.
	 * @see #setSubject(GeoSubject)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getGeoWithin_Subject()
	 * @model containment="true" required="true"
	 * @generated
	 */
	GeoSubject getSubject();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.GeoWithin#getSubject <em>Subject</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Subject</em>' containment reference.
	 * @see #getSubject()
	 * @generated
	 */
	void setSubject(GeoSubject value);

	/**
	 * Returns the value of the '<em><b>Shape</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Shape</em>' containment reference.
	 * @see #setShape(GeoShape)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getGeoWithin_Shape()
	 * @model containment="true" required="true"
	 * @generated
	 */
	GeoShape getShape();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.GeoWithin#getShape <em>Shape</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Shape</em>' containment reference.
	 * @see #getShape()
	 * @generated
	 */
	void setShape(GeoShape value);

} // GeoWithin
