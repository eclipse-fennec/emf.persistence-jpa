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
 * A representation of the model object '<em><b>Named Stored Procedure Query</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 
 *         @Target({TYPE}) @Retention(RUNTIME)
 *         public @interface NamedStoredProcedureQuery {
 *           String name();
 *           String procedureName();
 *           StoredProcedureParameter[] parameters() default {};
 *           Class[] resultClasses() default {};
 *           String[] resultSetMappings() default{};
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
 *   <li>{@link org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery#getParameter <em>Parameter</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery#getResultClass <em>Result Class</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery#getResultSetMapping <em>Result Set Mapping</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery#getHint <em>Hint</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery#getProcedureName <em>Procedure Name</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getNamedStoredProcedureQuery()
 * @model extendedMetaData="name='named-stored-procedure-query' kind='elementOnly'"
 * @generated
 */
@ProviderType
public interface NamedStoredProcedureQuery extends EObject {
	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getNamedStoredProcedureQuery_Description()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='description' namespace='##targetNamespace'"
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Parameter</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.StoredProcedureParameter}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameter</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getNamedStoredProcedureQuery_Parameter()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='parameter' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<StoredProcedureParameter> getParameter();

	/**
	 * Returns the value of the '<em><b>Result Class</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Result Class</em>' attribute list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getNamedStoredProcedureQuery_ResultClass()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='result-class' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<String> getResultClass();

	/**
	 * Returns the value of the '<em><b>Result Set Mapping</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Result Set Mapping</em>' attribute list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getNamedStoredProcedureQuery_ResultSetMapping()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='result-set-mapping' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<String> getResultSetMapping();

	/**
	 * Returns the value of the '<em><b>Hint</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.QueryHint}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Hint</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getNamedStoredProcedureQuery_Hint()
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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getNamedStoredProcedureQuery_Name()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='attribute' name='name'"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Procedure Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Procedure Name</em>' attribute.
	 * @see #setProcedureName(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getNamedStoredProcedureQuery_ProcedureName()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='attribute' name='procedure-name'"
	 * @generated
	 */
	String getProcedureName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery#getProcedureName <em>Procedure Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Procedure Name</em>' attribute.
	 * @see #getProcedureName()
	 * @generated
	 */
	void setProcedureName(String value);

} // NamedStoredProcedureQuery
