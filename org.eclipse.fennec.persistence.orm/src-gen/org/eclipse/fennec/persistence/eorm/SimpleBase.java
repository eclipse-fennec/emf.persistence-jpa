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
package org.eclipse.fennec.persistence.eorm;

import org.eclipse.emf.ecore.EEnum;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Simple Base</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.SimpleBase#getLob <em>Lob</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.SimpleBase#getConvert <em>Convert</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.SimpleBase#getFetch <em>Fetch</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.SimpleBase#getEnumerated <em>Enumerated</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getSimpleBase()
 * @model
 * @generated
 */
@ProviderType
public interface SimpleBase extends Base {
	/**
	 * Returns the value of the '<em><b>Lob</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Lob</em>' containment reference.
	 * @see #setLob(Lob)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getSimpleBase_Lob()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='lob' namespace='##targetNamespace'"
	 * @generated
	 */
	Lob getLob();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.SimpleBase#getLob <em>Lob</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Lob</em>' containment reference.
	 * @see #getLob()
	 * @generated
	 */
	void setLob(Lob value);

	/**
	 * Returns the value of the '<em><b>Convert</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Convert</em>' containment reference.
	 * @see #setConvert(Convert)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getSimpleBase_Convert()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='convert' namespace='##targetNamespace'"
	 * @generated
	 */
	Convert getConvert();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.SimpleBase#getConvert <em>Convert</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Convert</em>' containment reference.
	 * @see #getConvert()
	 * @generated
	 */
	void setConvert(Convert value);

	/**
	 * Returns the value of the '<em><b>Fetch</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.persistence.eorm.FetchType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Fetch</em>' attribute.
	 * @see org.eclipse.fennec.persistence.eorm.FetchType
	 * @see #isSetFetch()
	 * @see #unsetFetch()
	 * @see #setFetch(FetchType)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getSimpleBase_Fetch()
	 * @model unsettable="true"
	 *        extendedMetaData="kind='attribute' name='fetch'"
	 * @generated
	 */
	FetchType getFetch();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.SimpleBase#getFetch <em>Fetch</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Fetch</em>' attribute.
	 * @see org.eclipse.fennec.persistence.eorm.FetchType
	 * @see #isSetFetch()
	 * @see #unsetFetch()
	 * @see #getFetch()
	 * @generated
	 */
	void setFetch(FetchType value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.persistence.eorm.SimpleBase#getFetch <em>Fetch</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetFetch()
	 * @see #getFetch()
	 * @see #setFetch(FetchType)
	 * @generated
	 */
	void unsetFetch();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.persistence.eorm.SimpleBase#getFetch <em>Fetch</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Fetch</em>' attribute is set.
	 * @see #unsetFetch()
	 * @see #getFetch()
	 * @see #setFetch(FetchType)
	 * @generated
	 */
	boolean isSetFetch();

	/**
	 * Returns the value of the '<em><b>Enumerated</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Enumerated</em>' reference.
	 * @see #setEnumerated(EEnum)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getSimpleBase_Enumerated()
	 * @model
	 * @generated
	 */
	EEnum getEnumerated();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.SimpleBase#getEnumerated <em>Enumerated</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Enumerated</em>' reference.
	 * @see #getEnumerated()
	 * @generated
	 */
	void setEnumerated(EEnum value);

} // SimpleBase
