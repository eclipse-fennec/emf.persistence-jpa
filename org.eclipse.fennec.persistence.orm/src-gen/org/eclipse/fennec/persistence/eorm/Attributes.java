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
 * A representation of the model object '<em><b>Attributes</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 
 *         This element contains the entity field or property mappings.
 *         It may be sparsely populated to include only a subset of the
 *         fields or properties. If metadata-complete for the entity is true
 *         then the remainder of the attributes will be defaulted according
 *         to the default rules.
 * 
 *       
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Attributes#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Attributes#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Attributes#getEmbeddedId <em>Embedded Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Attributes#getBasic <em>Basic</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Attributes#getVersion <em>Version</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Attributes#getManyToOne <em>Many To One</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Attributes#getOneToMany <em>One To Many</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Attributes#getOneToOne <em>One To One</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Attributes#getManyToMany <em>Many To Many</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Attributes#getElementCollection <em>Element Collection</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Attributes#getEmbedded <em>Embedded</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Attributes#getTransient <em>Transient</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getAttributes()
 * @model extendedMetaData="name='attributes' kind='elementOnly'"
 * @generated
 */
@ProviderType
public interface Attributes extends EObject {
	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getAttributes_Description()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='description' namespace='##targetNamespace'"
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Attributes#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Id</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.Id}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getAttributes_Id()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='id' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<Id> getId();

	/**
	 * Returns the value of the '<em><b>Embedded Id</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Embedded Id</em>' containment reference.
	 * @see #setEmbeddedId(EmbeddedId)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getAttributes_EmbeddedId()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='embedded-id' namespace='##targetNamespace'"
	 * @generated
	 */
	EmbeddedId getEmbeddedId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Attributes#getEmbeddedId <em>Embedded Id</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Embedded Id</em>' containment reference.
	 * @see #getEmbeddedId()
	 * @generated
	 */
	void setEmbeddedId(EmbeddedId value);

	/**
	 * Returns the value of the '<em><b>Basic</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.Basic}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Basic</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getAttributes_Basic()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='basic' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<Basic> getBasic();

	/**
	 * Returns the value of the '<em><b>Version</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.Version}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Version</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getAttributes_Version()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='version' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<Version> getVersion();

	/**
	 * Returns the value of the '<em><b>Many To One</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.ManyToOne}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Many To One</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getAttributes_ManyToOne()
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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getAttributes_OneToMany()
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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getAttributes_OneToOne()
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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getAttributes_ManyToMany()
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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getAttributes_ElementCollection()
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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getAttributes_Embedded()
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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getAttributes_Transient()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='transient' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<Transient> getTransient();

} // Attributes
