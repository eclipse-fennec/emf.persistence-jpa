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

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Collection Table</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 
 *         @Target({METHOD, FIELD}) @Retention(RUNTIME)
 *         public @interface CollectionTable {
 *           String name() default "";
 *           String catalog() default "";
 *           String schema() default "";
 *           JoinColumn[] joinColumns() default {};
 *           UniqueConstraint[] uniqueConstraints() default {};
 *           Index[] indexes() default {};
 *         }
 * 
 *       
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.CollectionTable#getJoinColumn <em>Join Column</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.CollectionTable#getForeignKey <em>Foreign Key</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getCollectionTable()
 * @model extendedMetaData="name='collection-table' kind='elementOnly'"
 * @generated
 */
@ProviderType
public interface CollectionTable extends Table {
	/**
	 * Returns the value of the '<em><b>Join Column</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.JoinColumn}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Join Column</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getCollectionTable_JoinColumn()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='join-column' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<JoinColumn> getJoinColumn();

	/**
	 * Returns the value of the '<em><b>Foreign Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Foreign Key</em>' containment reference.
	 * @see #setForeignKey(ForeignKey)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getCollectionTable_ForeignKey()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='foreign-key' namespace='##targetNamespace'"
	 * @generated
	 */
	ForeignKey getForeignKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.CollectionTable#getForeignKey <em>Foreign Key</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Foreign Key</em>' containment reference.
	 * @see #getForeignKey()
	 * @generated
	 */
	void setForeignKey(ForeignKey value);

} // CollectionTable
