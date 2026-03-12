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
 * A representation of the model object '<em><b>Convert</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 
 *         @Target({TYPE, METHOD, FIELD}) @Retention(RUNTIME)
 *         public @interface Convert {
 *           Class converter() default void.class;
 *           String attributeName() default "";
 *           boolean disableConversion() default false;
 *         }
 * 
 *       
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Convert#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Convert#getAttributeName <em>Attribute Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Convert#getConverter <em>Converter</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Convert#isDisableConversion <em>Disable Conversion</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getConvert()
 * @model extendedMetaData="name='convert' kind='elementOnly'"
 * @generated
 */
@ProviderType
public interface Convert extends EObject {
	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getConvert_Description()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='description' namespace='##targetNamespace'"
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Convert#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Attribute Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Attribute Name</em>' attribute.
	 * @see #setAttributeName(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getConvert_AttributeName()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='attribute-name'"
	 * @generated
	 */
	String getAttributeName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Convert#getAttributeName <em>Attribute Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Attribute Name</em>' attribute.
	 * @see #getAttributeName()
	 * @generated
	 */
	void setAttributeName(String value);

	/**
	 * Returns the value of the '<em><b>Converter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Converter</em>' attribute.
	 * @see #setConverter(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getConvert_Converter()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='converter'"
	 * @generated
	 */
	String getConverter();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Convert#getConverter <em>Converter</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Converter</em>' attribute.
	 * @see #getConverter()
	 * @generated
	 */
	void setConverter(String value);

	/**
	 * Returns the value of the '<em><b>Disable Conversion</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Disable Conversion</em>' attribute.
	 * @see #isSetDisableConversion()
	 * @see #unsetDisableConversion()
	 * @see #setDisableConversion(boolean)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getConvert_DisableConversion()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='disable-conversion'"
	 * @generated
	 */
	boolean isDisableConversion();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Convert#isDisableConversion <em>Disable Conversion</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Disable Conversion</em>' attribute.
	 * @see #isSetDisableConversion()
	 * @see #unsetDisableConversion()
	 * @see #isDisableConversion()
	 * @generated
	 */
	void setDisableConversion(boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.persistence.eorm.Convert#isDisableConversion <em>Disable Conversion</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetDisableConversion()
	 * @see #isDisableConversion()
	 * @see #setDisableConversion(boolean)
	 * @generated
	 */
	void unsetDisableConversion();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.persistence.eorm.Convert#isDisableConversion <em>Disable Conversion</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Disable Conversion</em>' attribute is set.
	 * @see #unsetDisableConversion()
	 * @see #isDisableConversion()
	 * @see #setDisableConversion(boolean)
	 * @generated
	 */
	boolean isSetDisableConversion();

} // Convert
