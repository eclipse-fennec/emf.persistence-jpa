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

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Base Ref</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.BaseRef#getCascade <em>Cascade</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.BaseRef#getFetch <em>Fetch</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.BaseRef#getForeignKey <em>Foreign Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.BaseRef#getJoinTable <em>Join Table</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.BaseRef#isOptional <em>Optional</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getBaseRef()
 * @model
 * @generated
 */
@ProviderType
public interface BaseRef extends ENamedBase {
	/**
	 * Returns the value of the '<em><b>Cascade</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Cascade</em>' containment reference.
	 * @see #setCascade(CascadeType)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getBaseRef_Cascade()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='cascade' namespace='##targetNamespace'"
	 * @generated
	 */
	CascadeType getCascade();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.BaseRef#getCascade <em>Cascade</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Cascade</em>' containment reference.
	 * @see #getCascade()
	 * @generated
	 */
	void setCascade(CascadeType value);

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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getBaseRef_Fetch()
	 * @model unsettable="true"
	 *        extendedMetaData="kind='attribute' name='fetch'"
	 * @generated
	 */
	FetchType getFetch();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.BaseRef#getFetch <em>Fetch</em>}' attribute.
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
	 * Unsets the value of the '{@link org.eclipse.fennec.persistence.eorm.BaseRef#getFetch <em>Fetch</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetFetch()
	 * @see #getFetch()
	 * @see #setFetch(FetchType)
	 * @generated
	 */
	void unsetFetch();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.persistence.eorm.BaseRef#getFetch <em>Fetch</em>}' attribute is set.
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
	 * Returns the value of the '<em><b>Foreign Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Foreign Key</em>' containment reference.
	 * @see #setForeignKey(ForeignKey)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getBaseRef_ForeignKey()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='foreign-key' namespace='##targetNamespace'"
	 * @generated
	 */
	ForeignKey getForeignKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.BaseRef#getForeignKey <em>Foreign Key</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Foreign Key</em>' containment reference.
	 * @see #getForeignKey()
	 * @generated
	 */
	void setForeignKey(ForeignKey value);

	/**
	 * Returns the value of the '<em><b>Join Table</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Join Table</em>' containment reference.
	 * @see #setJoinTable(JoinTable)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getBaseRef_JoinTable()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='join-table' namespace='##targetNamespace'"
	 * @generated
	 */
	JoinTable getJoinTable();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.BaseRef#getJoinTable <em>Join Table</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Join Table</em>' containment reference.
	 * @see #getJoinTable()
	 * @generated
	 */
	void setJoinTable(JoinTable value);

	/**
	 * Returns the value of the '<em><b>Optional</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Optional</em>' attribute.
	 * @see #isSetOptional()
	 * @see #unsetOptional()
	 * @see #setOptional(boolean)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getBaseRef_Optional()
	 * @model default="false" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='optional'"
	 * @generated
	 */
	boolean isOptional();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.BaseRef#isOptional <em>Optional</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Optional</em>' attribute.
	 * @see #isSetOptional()
	 * @see #unsetOptional()
	 * @see #isOptional()
	 * @generated
	 */
	void setOptional(boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.persistence.eorm.BaseRef#isOptional <em>Optional</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetOptional()
	 * @see #isOptional()
	 * @see #setOptional(boolean)
	 * @generated
	 */
	void unsetOptional();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.persistence.eorm.BaseRef#isOptional <em>Optional</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Optional</em>' attribute is set.
	 * @see #unsetOptional()
	 * @see #isOptional()
	 * @see #setOptional(boolean)
	 * @generated
	 */
	boolean isSetOptional();

} // BaseRef
