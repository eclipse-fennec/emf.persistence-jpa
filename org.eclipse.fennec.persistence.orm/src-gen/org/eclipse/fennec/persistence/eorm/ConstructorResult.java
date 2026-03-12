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
 * A representation of the model object '<em><b>Constructor Result</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 
 *         @Target({}) @Retention(RUNTIME)
 *         public @interface ConstructorResult {
 *           Class targetClass();
 *           ColumnResult[] columns();
 *         }
 * 
 *       
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.ConstructorResult#getColumn <em>Column</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.ConstructorResult#getTargetClass <em>Target Class</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getConstructorResult()
 * @model extendedMetaData="name='constructor-result' kind='elementOnly'"
 * @generated
 */
@ProviderType
public interface ConstructorResult extends EObject {
	/**
	 * Returns the value of the '<em><b>Column</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.ColumnResult}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Column</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getConstructorResult_Column()
	 * @model containment="true" required="true"
	 *        extendedMetaData="kind='element' name='column' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<ColumnResult> getColumn();

	/**
	 * Returns the value of the '<em><b>Target Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target Class</em>' attribute.
	 * @see #setTargetClass(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getConstructorResult_TargetClass()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='attribute' name='target-class'"
	 * @generated
	 */
	String getTargetClass();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.ConstructorResult#getTargetClass <em>Target Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target Class</em>' attribute.
	 * @see #getTargetClass()
	 * @generated
	 */
	void setTargetClass(String value);

} // ConstructorResult
