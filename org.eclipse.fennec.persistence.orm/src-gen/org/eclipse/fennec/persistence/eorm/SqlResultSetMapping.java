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
 * A representation of the model object '<em><b>Sql Result Set Mapping</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 
 *         @Target({TYPE}) @Retention(RUNTIME)
 *         public @interface SqlResultSetMapping {
 *           String name();
 *           EntityResult[] entities() default {};
 *           ConstructorResult[] classes() default{};
 *           ColumnResult[] columns() default {};
 *         }
 * 
 *       
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.SqlResultSetMapping#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.SqlResultSetMapping#getEntityResult <em>Entity Result</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.SqlResultSetMapping#getConstructorResult <em>Constructor Result</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.SqlResultSetMapping#getColumnResult <em>Column Result</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.SqlResultSetMapping#getName <em>Name</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getSqlResultSetMapping()
 * @model extendedMetaData="name='sql-result-set-mapping' kind='elementOnly'"
 * @generated
 */
@ProviderType
public interface SqlResultSetMapping extends EObject {
	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getSqlResultSetMapping_Description()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='description' namespace='##targetNamespace'"
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.SqlResultSetMapping#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Entity Result</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.EntityResult}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Entity Result</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getSqlResultSetMapping_EntityResult()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='entity-result' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<EntityResult> getEntityResult();

	/**
	 * Returns the value of the '<em><b>Constructor Result</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.ConstructorResult}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Constructor Result</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getSqlResultSetMapping_ConstructorResult()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='constructor-result' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<ConstructorResult> getConstructorResult();

	/**
	 * Returns the value of the '<em><b>Column Result</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.ColumnResult}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Column Result</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getSqlResultSetMapping_ColumnResult()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='column-result' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<ColumnResult> getColumnResult();

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getSqlResultSetMapping_Name()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='attribute' name='name'"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.SqlResultSetMapping#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

} // SqlResultSetMapping
