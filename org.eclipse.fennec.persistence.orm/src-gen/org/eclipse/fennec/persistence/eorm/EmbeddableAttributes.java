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
 * A representation of the model object '<em><b>Embeddable Attributes</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EmbeddableAttributes#getBasic <em>Basic</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EmbeddableAttributes#getManyToOne <em>Many To One</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EmbeddableAttributes#getOneToMany <em>One To Many</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EmbeddableAttributes#getOneToOne <em>One To One</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EmbeddableAttributes#getManyToMany <em>Many To Many</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EmbeddableAttributes#getElementCollection <em>Element Collection</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EmbeddableAttributes#getEmbedded <em>Embedded</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EmbeddableAttributes#getTransient <em>Transient</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEmbeddableAttributes()
 * @model extendedMetaData="name='embeddable-attributes' kind='elementOnly'"
 * @generated
 */
@ProviderType
public interface EmbeddableAttributes extends EObject {
	/**
	 * Returns the value of the '<em><b>Basic</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.Basic}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Basic</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEmbeddableAttributes_Basic()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='basic' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<Basic> getBasic();

	/**
	 * Returns the value of the '<em><b>Many To One</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.ManyToOne}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Many To One</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEmbeddableAttributes_ManyToOne()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='many-to-one' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<ManyToOne> getManyToOne();

	/**
	 * Returns the value of the '<em><b>One To Many</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.OneToMany}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>One To Many</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEmbeddableAttributes_OneToMany()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='one-to-many' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<OneToMany> getOneToMany();

	/**
	 * Returns the value of the '<em><b>One To One</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.OneToOne}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>One To One</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEmbeddableAttributes_OneToOne()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='one-to-one' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<OneToOne> getOneToOne();

	/**
	 * Returns the value of the '<em><b>Many To Many</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.ManyToMany}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Many To Many</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEmbeddableAttributes_ManyToMany()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='many-to-many' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<ManyToMany> getManyToMany();

	/**
	 * Returns the value of the '<em><b>Element Collection</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.ElementCollection}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Element Collection</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEmbeddableAttributes_ElementCollection()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='element-collection' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<ElementCollection> getElementCollection();

	/**
	 * Returns the value of the '<em><b>Embedded</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.Embedded}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Embedded</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEmbeddableAttributes_Embedded()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='embedded' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<Embedded> getEmbedded();

	/**
	 * Returns the value of the '<em><b>Transient</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.Transient}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Transient</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEmbeddableAttributes_Transient()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='transient' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<Transient> getTransient();

} // EmbeddableAttributes
