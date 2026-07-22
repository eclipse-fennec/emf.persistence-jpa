/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 * 
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 */
package org.eclipse.fennec.model.utilities;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Lat Lng</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.utilities.LatLng#getLatitude <em>Latitude</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.utilities.LatLng#getLongitude <em>Longitude</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.utilities.UtilitiesPackage#getLatLng()
 * @model
 * @generated
 */
@ProviderType
public interface LatLng extends EObject {
	/**
	 * Returns the value of the '<em><b>Latitude</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Latitude</em>' attribute.
	 * @see #setLatitude(double)
	 * @see org.eclipse.fennec.model.utilities.UtilitiesPackage#getLatLng_Latitude()
	 * @model
	 * @generated
	 */
	double getLatitude();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.utilities.LatLng#getLatitude <em>Latitude</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Latitude</em>' attribute.
	 * @see #getLatitude()
	 * @generated
	 */
	void setLatitude(double value);

	/**
	 * Returns the value of the '<em><b>Longitude</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Longitude</em>' attribute.
	 * @see #setLongitude(double)
	 * @see org.eclipse.fennec.model.utilities.UtilitiesPackage#getLatLng_Longitude()
	 * @model
	 * @generated
	 */
	double getLongitude();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.utilities.LatLng#getLongitude <em>Longitude</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Longitude</em>' attribute.
	 * @see #getLongitude()
	 * @generated
	 */
	void setLongitude(double value);

} // LatLng
