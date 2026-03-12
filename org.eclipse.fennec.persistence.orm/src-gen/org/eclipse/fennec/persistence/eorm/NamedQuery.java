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

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Named Query</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 
 *         @Target({TYPE}) @Retention(RUNTIME)
 *         public @interface NamedQuery {
 *           String name();
 *           String query();
 *           LockModeType lockMode() default NONE;
 *           QueryHint[] hints() default {};
 *         }
 * 
 *       
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.NamedQuery#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.NamedQuery#getQuery <em>Query</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.NamedQuery#getLockMode <em>Lock Mode</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.NamedQuery#getHint <em>Hint</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.NamedQuery#getName <em>Name</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getNamedQuery()
 * @model extendedMetaData="name='named-query' kind='elementOnly'"
 * @generated
 */
@ProviderType
public interface NamedQuery extends EObject {
	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getNamedQuery_Description()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='description' namespace='##targetNamespace'"
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.NamedQuery#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Query</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Query</em>' attribute.
	 * @see #setQuery(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getNamedQuery_Query()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='element' name='query' namespace='##targetNamespace'"
	 * @generated
	 */
	String getQuery();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.NamedQuery#getQuery <em>Query</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Query</em>' attribute.
	 * @see #getQuery()
	 * @generated
	 */
	void setQuery(String value);

	/**
	 * Returns the value of the '<em><b>Lock Mode</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.persistence.eorm.LockModeType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Lock Mode</em>' attribute.
	 * @see org.eclipse.fennec.persistence.eorm.LockModeType
	 * @see #isSetLockMode()
	 * @see #unsetLockMode()
	 * @see #setLockMode(LockModeType)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getNamedQuery_LockMode()
	 * @model unsettable="true"
	 *        extendedMetaData="kind='element' name='lock-mode' namespace='##targetNamespace'"
	 * @generated
	 */
	LockModeType getLockMode();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.NamedQuery#getLockMode <em>Lock Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Lock Mode</em>' attribute.
	 * @see org.eclipse.fennec.persistence.eorm.LockModeType
	 * @see #isSetLockMode()
	 * @see #unsetLockMode()
	 * @see #getLockMode()
	 * @generated
	 */
	void setLockMode(LockModeType value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.persistence.eorm.NamedQuery#getLockMode <em>Lock Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetLockMode()
	 * @see #getLockMode()
	 * @see #setLockMode(LockModeType)
	 * @generated
	 */
	void unsetLockMode();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.persistence.eorm.NamedQuery#getLockMode <em>Lock Mode</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Lock Mode</em>' attribute is set.
	 * @see #unsetLockMode()
	 * @see #getLockMode()
	 * @see #setLockMode(LockModeType)
	 * @generated
	 */
	boolean isSetLockMode();

	/**
	 * Returns the value of the '<em><b>Hint</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.QueryHint}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Hint</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getNamedQuery_Hint()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='hint' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<QueryHint> getHint();

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getNamedQuery_Name()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='attribute' name='name'"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.NamedQuery#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

} // NamedQuery
