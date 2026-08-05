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
 * A representation of the model object '<em><b>Geo Distance</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * The spherical WGS84 distance in meters between the subject's position and a point (issue #101, decision G3): a VALUE expression — composes with comparisons (geoDistance <= 500) and the sort seam of issue #84 (nearest first, k-NN = sort + limit); backends may recognise the pattern as a native near-query, as optimisation, not semantics. Reference formula: haversine over the mean earth radius 6371008.8 m; conformance is banded per decision G5. Capability GEO_DISTANCE.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.GeoDistance#getSubject <em>Subject</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.GeoDistance#getPoint <em>Point</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getGeoDistance()
 * @model
 * @generated
 */
@ProviderType
public interface GeoDistance extends Expression {
	/**
	 * Returns the value of the '<em><b>Subject</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Subject</em>' containment reference.
	 * @see #setSubject(GeoSubject)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getGeoDistance_Subject()
	 * @model containment="true" required="true"
	 * @generated
	 */
	GeoSubject getSubject();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.GeoDistance#getSubject <em>Subject</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Subject</em>' containment reference.
	 * @see #getSubject()
	 * @generated
	 */
	void setSubject(GeoSubject value);

	/**
	 * Returns the value of the '<em><b>Point</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Point</em>' containment reference.
	 * @see #setPoint(GeoPointLiteral)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getGeoDistance_Point()
	 * @model containment="true" required="true"
	 * @generated
	 */
	GeoPointLiteral getPoint();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.GeoDistance#getPoint <em>Point</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Point</em>' containment reference.
	 * @see #getPoint()
	 * @generated
	 */
	void setPoint(GeoPointLiteral value);

} // GeoDistance
