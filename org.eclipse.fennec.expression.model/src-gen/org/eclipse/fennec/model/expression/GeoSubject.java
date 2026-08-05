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

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Geo Subject</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * The coordinate binding of a geo predicate (issue #101, decision G1: both forms) — EITHER the split feature pair (pathLat and pathLon, the dominant Ecore shape and the #96 enrichment-tag shape) OR a single packed point path (pathPoint, the Mongo 2dsphere index shape; its canonical value representation is defined with the Mongo translation, G-P2). Exactly one binding must be present — validated structurally. Deliberately structural, not a model aspect (decision G2): an aspect can later derive the paths.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.GeoSubject#getPathLat <em>Path Lat</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.GeoSubject#getPathLon <em>Path Lon</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.GeoSubject#getPathPoint <em>Path Point</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getGeoSubject()
 * @model
 * @generated
 */
@ProviderType
public interface GeoSubject extends EObject {
	/**
	 * Returns the value of the '<em><b>Path Lat</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Path Lat</em>' containment reference.
	 * @see #setPathLat(PropertyPath)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getGeoSubject_PathLat()
	 * @model containment="true"
	 * @generated
	 */
	PropertyPath getPathLat();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.GeoSubject#getPathLat <em>Path Lat</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Path Lat</em>' containment reference.
	 * @see #getPathLat()
	 * @generated
	 */
	void setPathLat(PropertyPath value);

	/**
	 * Returns the value of the '<em><b>Path Lon</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Path Lon</em>' containment reference.
	 * @see #setPathLon(PropertyPath)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getGeoSubject_PathLon()
	 * @model containment="true"
	 * @generated
	 */
	PropertyPath getPathLon();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.GeoSubject#getPathLon <em>Path Lon</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Path Lon</em>' containment reference.
	 * @see #getPathLon()
	 * @generated
	 */
	void setPathLon(PropertyPath value);

	/**
	 * Returns the value of the '<em><b>Path Point</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Path Point</em>' containment reference.
	 * @see #setPathPoint(PropertyPath)
	 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getGeoSubject_PathPoint()
	 * @model containment="true"
	 * @generated
	 */
	PropertyPath getPathPoint();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.expression.GeoSubject#getPathPoint <em>Path Point</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Path Point</em>' containment reference.
	 * @see #getPathPoint()
	 * @generated
	 */
	void setPathPoint(PropertyPath value);

} // GeoSubject
