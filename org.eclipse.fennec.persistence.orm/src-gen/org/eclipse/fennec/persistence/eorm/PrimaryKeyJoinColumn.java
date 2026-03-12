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

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Primary Key Join Column</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 
 *         @Target({TYPE, METHOD, FIELD}) @Retention(RUNTIME)
 *         public @interface PrimaryKeyJoinColumn {
 *           String name() default "";
 *           String referencedColumnName() default "";
 *           String columnDefinition() default "";
 *         }
 * 
 *       
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.PrimaryKeyJoinColumn#getColumnDefinition <em>Column Definition</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.PrimaryKeyJoinColumn#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.PrimaryKeyJoinColumn#getReferencedColumnName <em>Referenced Column Name</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getPrimaryKeyJoinColumn()
 * @model extendedMetaData="name='primary-key-join-column' kind='empty'"
 * @generated
 */
@ProviderType
public interface PrimaryKeyJoinColumn extends EObject {
	/**
	 * Returns the value of the '<em><b>Column Definition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Column Definition</em>' attribute.
	 * @see #setColumnDefinition(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getPrimaryKeyJoinColumn_ColumnDefinition()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='column-definition'"
	 * @generated
	 */
	String getColumnDefinition();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.PrimaryKeyJoinColumn#getColumnDefinition <em>Column Definition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Column Definition</em>' attribute.
	 * @see #getColumnDefinition()
	 * @generated
	 */
	void setColumnDefinition(String value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getPrimaryKeyJoinColumn_Name()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='name'"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.PrimaryKeyJoinColumn#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Referenced Column Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Referenced Column Name</em>' attribute.
	 * @see #setReferencedColumnName(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getPrimaryKeyJoinColumn_ReferencedColumnName()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='referenced-column-name'"
	 * @generated
	 */
	String getReferencedColumnName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.PrimaryKeyJoinColumn#getReferencedColumnName <em>Referenced Column Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Referenced Column Name</em>' attribute.
	 * @see #getReferencedColumnName()
	 * @generated
	 */
	void setReferencedColumnName(String value);

} // PrimaryKeyJoinColumn
