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

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Id</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 
 *         @Target({METHOD, FIELD}) @Retention(RUNTIME)
 *         public @interface Id {}
 * 
 *       
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Id#getGeneratedValue <em>Generated Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Id#getTableGenerator <em>Table Generator</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Id#getSequenceGenerator <em>Sequence Generator</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getId()
 * @model extendedMetaData="name='id' kind='elementOnly'"
 * @generated
 */
@ProviderType
public interface Id extends Base {
	/**
	 * Returns the value of the '<em><b>Generated Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Generated Value</em>' containment reference.
	 * @see #setGeneratedValue(GeneratedValue)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getId_GeneratedValue()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='generated-value' namespace='##targetNamespace'"
	 * @generated
	 */
	GeneratedValue getGeneratedValue();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Id#getGeneratedValue <em>Generated Value</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Generated Value</em>' containment reference.
	 * @see #getGeneratedValue()
	 * @generated
	 */
	void setGeneratedValue(GeneratedValue value);

	/**
	 * Returns the value of the '<em><b>Table Generator</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Table Generator</em>' containment reference.
	 * @see #setTableGenerator(TableGenerator)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getId_TableGenerator()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='table-generator' namespace='##targetNamespace'"
	 * @generated
	 */
	TableGenerator getTableGenerator();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Id#getTableGenerator <em>Table Generator</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Table Generator</em>' containment reference.
	 * @see #getTableGenerator()
	 * @generated
	 */
	void setTableGenerator(TableGenerator value);

	/**
	 * Returns the value of the '<em><b>Sequence Generator</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sequence Generator</em>' containment reference.
	 * @see #setSequenceGenerator(SequenceGenerator)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getId_SequenceGenerator()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='sequence-generator' namespace='##targetNamespace'"
	 * @generated
	 */
	SequenceGenerator getSequenceGenerator();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Id#getSequenceGenerator <em>Sequence Generator</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sequence Generator</em>' containment reference.
	 * @see #getSequenceGenerator()
	 * @generated
	 */
	void setSequenceGenerator(SequenceGenerator value);

} // Id
