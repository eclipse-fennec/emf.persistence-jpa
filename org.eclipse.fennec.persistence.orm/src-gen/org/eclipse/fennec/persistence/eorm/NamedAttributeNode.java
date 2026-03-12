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
 * A representation of the model object '<em><b>Named Attribute Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 
 *         @Target({}) @Retention(RUNTIME)
 *         public @interface NamedAttributeNode {
 *           String value();
 *           String subgraph() default "";
 *           String keySubgraph() default "";
 *         }
 *  
 *       
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.NamedAttributeNode#getKeySubgraph <em>Key Subgraph</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.NamedAttributeNode#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.NamedAttributeNode#getSubgraph <em>Subgraph</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getNamedAttributeNode()
 * @model extendedMetaData="name='named-attribute-node' kind='empty'"
 * @generated
 */
@ProviderType
public interface NamedAttributeNode extends EObject {
	/**
	 * Returns the value of the '<em><b>Key Subgraph</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Key Subgraph</em>' attribute.
	 * @see #setKeySubgraph(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getNamedAttributeNode_KeySubgraph()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='key-subgraph'"
	 * @generated
	 */
	String getKeySubgraph();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.NamedAttributeNode#getKeySubgraph <em>Key Subgraph</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Key Subgraph</em>' attribute.
	 * @see #getKeySubgraph()
	 * @generated
	 */
	void setKeySubgraph(String value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getNamedAttributeNode_Name()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='attribute' name='name'"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.NamedAttributeNode#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Subgraph</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Subgraph</em>' attribute.
	 * @see #setSubgraph(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getNamedAttributeNode_Subgraph()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='subgraph'"
	 * @generated
	 */
	String getSubgraph();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.NamedAttributeNode#getSubgraph <em>Subgraph</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Subgraph</em>' attribute.
	 * @see #getSubgraph()
	 * @generated
	 */
	void setSubgraph(String value);

} // NamedAttributeNode
