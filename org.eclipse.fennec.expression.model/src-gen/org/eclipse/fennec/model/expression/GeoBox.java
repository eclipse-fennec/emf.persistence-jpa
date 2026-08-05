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
 * A representation of the model object '<em><b>Geo Box</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * An axis-aligned box between two corners. May cross the antimeridian (southWest.lon > northEast.lon is the wrap-around box, §5.3).
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.GeoBox#getSouthWest <em>South West</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.GeoBox#getNorthEast <em>North East</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getGeoBox()
 * @model
 * @generated
 */
@ProviderType
public interface GeoBox extends GeoShape {
	/**
	 * Returns the value of the '<em><b>South West</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>South West</em>' containment reference.
	 * @see #setSouthWest(GeoPointLiteral)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getGeoBox_SouthWest()
	 * @model containment="true" required="true"
	 * @generated
	 */
	GeoPointLiteral getSouthWest();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.GeoBox#getSouthWest <em>South West</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>South West</em>' containment reference.
	 * @see #getSouthWest()
	 * @generated
	 */
	void setSouthWest(GeoPointLiteral value);

	/**
	 * Returns the value of the '<em><b>North East</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>North East</em>' containment reference.
	 * @see #setNorthEast(GeoPointLiteral)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getGeoBox_NorthEast()
	 * @model containment="true" required="true"
	 * @generated
	 */
	GeoPointLiteral getNorthEast();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.GeoBox#getNorthEast <em>North East</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>North East</em>' containment reference.
	 * @see #getNorthEast()
	 * @generated
	 */
	void setNorthEast(GeoPointLiteral value);

} // GeoBox
