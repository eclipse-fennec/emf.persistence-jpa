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
 * A representation of the model object '<em><b>Entity Result</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 
 *         @Target({}) @Retention(RUNTIME)
 *         public @interface EntityResult {
 *           Class entityClass();
 *           FieldResult[] fields() default {};
 *           String discriminatorColumn() default "";
 *         }
 * 
 *       
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EntityResult#getFieldResult <em>Field Result</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EntityResult#getDiscriminatorColumn <em>Discriminator Column</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EntityResult#getEntityClass <em>Entity Class</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityResult()
 * @model extendedMetaData="name='entity-result' kind='elementOnly'"
 * @generated
 */
@ProviderType
public interface EntityResult extends EObject {
	/**
	 * Returns the value of the '<em><b>Field Result</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.FieldResult}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Field Result</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityResult_FieldResult()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='field-result' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<FieldResult> getFieldResult();

	/**
	 * Returns the value of the '<em><b>Discriminator Column</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Discriminator Column</em>' attribute.
	 * @see #setDiscriminatorColumn(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityResult_DiscriminatorColumn()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='discriminator-column'"
	 * @generated
	 */
	String getDiscriminatorColumn();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.EntityResult#getDiscriminatorColumn <em>Discriminator Column</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Discriminator Column</em>' attribute.
	 * @see #getDiscriminatorColumn()
	 * @generated
	 */
	void setDiscriminatorColumn(String value);

	/**
	 * Returns the value of the '<em><b>Entity Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Entity Class</em>' attribute.
	 * @see #setEntityClass(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityResult_EntityClass()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='attribute' name='entity-class'"
	 * @generated
	 */
	String getEntityClass();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.EntityResult#getEntityClass <em>Entity Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Entity Class</em>' attribute.
	 * @see #getEntityClass()
	 * @generated
	 */
	void setEntityClass(String value);

} // EntityResult
