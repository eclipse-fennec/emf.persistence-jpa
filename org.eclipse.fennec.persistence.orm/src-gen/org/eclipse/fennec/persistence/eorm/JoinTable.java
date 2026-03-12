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
 * A representation of the model object '<em><b>Join Table</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 
 *         @Target({METHOD, FIELD}) @Retention(RUNTIME)
 *         public @interface JoinTable {
 *           String name() default "";
 *           String catalog() default "";
 *           String schema() default "";
 *           JoinColumn[] joinColumns() default {};
 *           JoinColumn[] inverseJoinColumns() default {};
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
 *   <li>{@link org.eclipse.fennec.persistence.eorm.JoinTable#getJoinColumn <em>Join Column</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.JoinTable#getForeignKey <em>Foreign Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.JoinTable#getInverseJoinColumn <em>Inverse Join Column</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.JoinTable#getInverseForeignKey <em>Inverse Foreign Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.JoinTable#getUniqueConstraint <em>Unique Constraint</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.JoinTable#getIndex <em>Index</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.JoinTable#getCatalog <em>Catalog</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.JoinTable#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.JoinTable#getSchema <em>Schema</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getJoinTable()
 * @model extendedMetaData="name='join-table' kind='elementOnly'"
 * @generated
 */
@ProviderType
public interface JoinTable extends EObject {
	/**
	 * Returns the value of the '<em><b>Join Column</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.JoinColumn}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Join Column</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getJoinTable_JoinColumn()
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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getJoinTable_ForeignKey()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='foreign-key' namespace='##targetNamespace'"
	 * @generated
	 */
	ForeignKey getForeignKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.JoinTable#getForeignKey <em>Foreign Key</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Foreign Key</em>' containment reference.
	 * @see #getForeignKey()
	 * @generated
	 */
	void setForeignKey(ForeignKey value);

	/**
	 * Returns the value of the '<em><b>Inverse Join Column</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.JoinColumn}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Inverse Join Column</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getJoinTable_InverseJoinColumn()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='inverse-join-column' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<JoinColumn> getInverseJoinColumn();

	/**
	 * Returns the value of the '<em><b>Inverse Foreign Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Inverse Foreign Key</em>' containment reference.
	 * @see #setInverseForeignKey(ForeignKey)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getJoinTable_InverseForeignKey()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='inverse-foreign-key' namespace='##targetNamespace'"
	 * @generated
	 */
	ForeignKey getInverseForeignKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.JoinTable#getInverseForeignKey <em>Inverse Foreign Key</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Inverse Foreign Key</em>' containment reference.
	 * @see #getInverseForeignKey()
	 * @generated
	 */
	void setInverseForeignKey(ForeignKey value);

	/**
	 * Returns the value of the '<em><b>Unique Constraint</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.UniqueConstraint}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Unique Constraint</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getJoinTable_UniqueConstraint()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='unique-constraint' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<UniqueConstraint> getUniqueConstraint();

	/**
	 * Returns the value of the '<em><b>Index</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.Index}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Index</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getJoinTable_Index()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='index' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<Index> getIndex();

	/**
	 * Returns the value of the '<em><b>Catalog</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Catalog</em>' attribute.
	 * @see #setCatalog(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getJoinTable_Catalog()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='catalog'"
	 * @generated
	 */
	String getCatalog();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.JoinTable#getCatalog <em>Catalog</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Catalog</em>' attribute.
	 * @see #getCatalog()
	 * @generated
	 */
	void setCatalog(String value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getJoinTable_Name()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='name'"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.JoinTable#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Schema</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Schema</em>' attribute.
	 * @see #setSchema(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getJoinTable_Schema()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='schema'"
	 * @generated
	 */
	String getSchema();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.JoinTable#getSchema <em>Schema</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Schema</em>' attribute.
	 * @see #getSchema()
	 * @generated
	 */
	void setSchema(String value);

} // JoinTable
