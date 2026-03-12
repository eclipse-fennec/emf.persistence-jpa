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
 * A representation of the model object '<em><b>Many To One</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 
 *         @Target({METHOD, FIELD}) @Retention(RUNTIME)
 *         public @interface ManyToOne {
 *           Class targetEntity() default void.class;
 *           CascadeType[] cascade() default {};
 *           FetchType fetch() default EAGER;
 *           boolean optional() default true;
 *         }
 * 
 *       
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.ManyToOne#getJoinColumn <em>Join Column</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.ManyToOne#isId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.ManyToOne#getMapsId <em>Maps Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.ManyToOne#getTargetEntity <em>Target Entity</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getManyToOne()
 * @model extendedMetaData="name='many-to-one' kind='elementOnly'"
 * @generated
 */
@ProviderType
public interface ManyToOne extends BaseRef {
	/**
	 * Returns the value of the '<em><b>Join Column</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.JoinColumn}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Join Column</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getManyToOne_JoinColumn()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='join-column' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<JoinColumn> getJoinColumn();

	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #isSetId()
	 * @see #unsetId()
	 * @see #setId(boolean)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getManyToOne_Id()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='id'"
	 * @generated
	 */
	boolean isId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.ManyToOne#isId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #isSetId()
	 * @see #unsetId()
	 * @see #isId()
	 * @generated
	 */
	void setId(boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.persistence.eorm.ManyToOne#isId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetId()
	 * @see #isId()
	 * @see #setId(boolean)
	 * @generated
	 */
	void unsetId();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.persistence.eorm.ManyToOne#isId <em>Id</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Id</em>' attribute is set.
	 * @see #unsetId()
	 * @see #isId()
	 * @see #setId(boolean)
	 * @generated
	 */
	boolean isSetId();

	/**
	 * Returns the value of the '<em><b>Maps Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Maps Id</em>' attribute.
	 * @see #setMapsId(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getManyToOne_MapsId()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='maps-id'"
	 * @generated
	 */
	String getMapsId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.ManyToOne#getMapsId <em>Maps Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Maps Id</em>' attribute.
	 * @see #getMapsId()
	 * @generated
	 */
	void setMapsId(String value);

	/**
	 * Returns the value of the '<em><b>Target Entity</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target Entity</em>' attribute.
	 * @see #setTargetEntity(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getManyToOne_TargetEntity()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='target-entity'"
	 * @generated
	 */
	String getTargetEntity();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.ManyToOne#getTargetEntity <em>Target Entity</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target Entity</em>' attribute.
	 * @see #getTargetEntity()
	 * @generated
	 */
	void setTargetEntity(String value);

} // ManyToOne
