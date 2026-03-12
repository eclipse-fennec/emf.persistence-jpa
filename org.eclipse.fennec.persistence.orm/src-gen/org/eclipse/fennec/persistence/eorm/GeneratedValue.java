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
 * A representation of the model object '<em><b>Generated Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 
 *         @Target({METHOD, FIELD}) @Retention(RUNTIME)
 *         public @interface GeneratedValue {
 *           GenerationType strategy() default AUTO;
 *           String generator() default "";
 *         }
 * 
 *       
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.GeneratedValue#getGenerator <em>Generator</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.GeneratedValue#getStrategy <em>Strategy</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getGeneratedValue()
 * @model extendedMetaData="name='generated-value' kind='empty'"
 * @generated
 */
@ProviderType
public interface GeneratedValue extends EObject {
	/**
	 * Returns the value of the '<em><b>Generator</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Generator</em>' attribute.
	 * @see #setGenerator(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getGeneratedValue_Generator()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='generator'"
	 * @generated
	 */
	String getGenerator();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.GeneratedValue#getGenerator <em>Generator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Generator</em>' attribute.
	 * @see #getGenerator()
	 * @generated
	 */
	void setGenerator(String value);

	/**
	 * Returns the value of the '<em><b>Strategy</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.persistence.eorm.GenerationType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Strategy</em>' attribute.
	 * @see org.eclipse.fennec.persistence.eorm.GenerationType
	 * @see #isSetStrategy()
	 * @see #unsetStrategy()
	 * @see #setStrategy(GenerationType)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getGeneratedValue_Strategy()
	 * @model unsettable="true"
	 *        extendedMetaData="kind='attribute' name='strategy'"
	 * @generated
	 */
	GenerationType getStrategy();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.GeneratedValue#getStrategy <em>Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Strategy</em>' attribute.
	 * @see org.eclipse.fennec.persistence.eorm.GenerationType
	 * @see #isSetStrategy()
	 * @see #unsetStrategy()
	 * @see #getStrategy()
	 * @generated
	 */
	void setStrategy(GenerationType value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.persistence.eorm.GeneratedValue#getStrategy <em>Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetStrategy()
	 * @see #getStrategy()
	 * @see #setStrategy(GenerationType)
	 * @generated
	 */
	void unsetStrategy();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.persistence.eorm.GeneratedValue#getStrategy <em>Strategy</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Strategy</em>' attribute is set.
	 * @see #unsetStrategy()
	 * @see #getStrategy()
	 * @see #setStrategy(GenerationType)
	 * @generated
	 */
	boolean isSetStrategy();

} // GeneratedValue
