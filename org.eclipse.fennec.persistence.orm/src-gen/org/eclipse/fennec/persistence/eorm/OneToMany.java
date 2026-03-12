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
 * A representation of the model object '<em><b>One To Many</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 
 *         @Target({METHOD, FIELD}) @Retention(RUNTIME)
 *         public @interface OneToMany {
 *           Class targetEntity() default void.class;
 *           CascadeType[] cascade() default {};
 *           FetchType fetch() default LAZY;
 *           String mappedBy() default "";
 *         }
 * 
 *       
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.OneToMany#getOrderBy <em>Order By</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.OneToMany#getOrderColumn <em>Order Column</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.OneToMany#getMapKey <em>Map Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyClass <em>Map Key Class</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyTemporal <em>Map Key Temporal</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyEnumerated <em>Map Key Enumerated</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyAttributeOverride <em>Map Key Attribute Override</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyConvert <em>Map Key Convert</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyColumn <em>Map Key Column</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyJoinColumn <em>Map Key Join Column</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyForeignKey <em>Map Key Foreign Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.OneToMany#getJoinColumn <em>Join Column</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.OneToMany#isOrphanRemoval <em>Orphan Removal</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.OneToMany#getTargetEntity <em>Target Entity</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getOneToMany()
 * @model extendedMetaData="name='one-to-many' kind='elementOnly'"
 * @generated
 */
@ProviderType
public interface OneToMany extends MappedByRef {
	/**
	 * Returns the value of the '<em><b>Order By</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Order By</em>' attribute.
	 * @see #setOrderBy(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getOneToMany_OrderBy()
	 * @model dataType="org.eclipse.fennec.persistence.eorm.OrderBy"
	 *        extendedMetaData="kind='element' name='order-by' namespace='##targetNamespace'"
	 * @generated
	 */
	String getOrderBy();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.OneToMany#getOrderBy <em>Order By</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Order By</em>' attribute.
	 * @see #getOrderBy()
	 * @generated
	 */
	void setOrderBy(String value);

	/**
	 * Returns the value of the '<em><b>Order Column</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Order Column</em>' containment reference.
	 * @see #setOrderColumn(OrderColumn)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getOneToMany_OrderColumn()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='order-column' namespace='##targetNamespace'"
	 * @generated
	 */
	OrderColumn getOrderColumn();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.OneToMany#getOrderColumn <em>Order Column</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Order Column</em>' containment reference.
	 * @see #getOrderColumn()
	 * @generated
	 */
	void setOrderColumn(OrderColumn value);

	/**
	 * Returns the value of the '<em><b>Map Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Map Key</em>' containment reference.
	 * @see #setMapKey(MapKey)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getOneToMany_MapKey()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='map-key' namespace='##targetNamespace'"
	 * @generated
	 */
	MapKey getMapKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.OneToMany#getMapKey <em>Map Key</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Map Key</em>' containment reference.
	 * @see #getMapKey()
	 * @generated
	 */
	void setMapKey(MapKey value);

	/**
	 * Returns the value of the '<em><b>Map Key Class</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Map Key Class</em>' containment reference.
	 * @see #setMapKeyClass(MapKeyClass)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getOneToMany_MapKeyClass()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='map-key-class' namespace='##targetNamespace'"
	 * @generated
	 */
	MapKeyClass getMapKeyClass();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyClass <em>Map Key Class</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Map Key Class</em>' containment reference.
	 * @see #getMapKeyClass()
	 * @generated
	 */
	void setMapKeyClass(MapKeyClass value);

	/**
	 * Returns the value of the '<em><b>Map Key Temporal</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Map Key Temporal</em>' attribute.
	 * @see #setMapKeyTemporal(TemporalType)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getOneToMany_MapKeyTemporal()
	 * @model dataType="org.eclipse.fennec.persistence.eorm.Temporal"
	 *        extendedMetaData="kind='element' name='map-key-temporal' namespace='##targetNamespace'"
	 * @generated
	 */
	TemporalType getMapKeyTemporal();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyTemporal <em>Map Key Temporal</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Map Key Temporal</em>' attribute.
	 * @see #getMapKeyTemporal()
	 * @generated
	 */
	void setMapKeyTemporal(TemporalType value);

	/**
	 * Returns the value of the '<em><b>Map Key Enumerated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Map Key Enumerated</em>' attribute.
	 * @see #setMapKeyEnumerated(EnumType)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getOneToMany_MapKeyEnumerated()
	 * @model dataType="org.eclipse.fennec.persistence.eorm.Enumerated"
	 *        extendedMetaData="kind='element' name='map-key-enumerated' namespace='##targetNamespace'"
	 * @generated
	 */
	EnumType getMapKeyEnumerated();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyEnumerated <em>Map Key Enumerated</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Map Key Enumerated</em>' attribute.
	 * @see #getMapKeyEnumerated()
	 * @generated
	 */
	void setMapKeyEnumerated(EnumType value);

	/**
	 * Returns the value of the '<em><b>Map Key Attribute Override</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.AttributeOverride}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Map Key Attribute Override</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getOneToMany_MapKeyAttributeOverride()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='map-key-attribute-override' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<AttributeOverride> getMapKeyAttributeOverride();

	/**
	 * Returns the value of the '<em><b>Map Key Convert</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.Convert}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Map Key Convert</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getOneToMany_MapKeyConvert()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='map-key-convert' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<Convert> getMapKeyConvert();

	/**
	 * Returns the value of the '<em><b>Map Key Column</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Map Key Column</em>' containment reference.
	 * @see #setMapKeyColumn(MapKeyColumn)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getOneToMany_MapKeyColumn()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='map-key-column' namespace='##targetNamespace'"
	 * @generated
	 */
	MapKeyColumn getMapKeyColumn();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyColumn <em>Map Key Column</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Map Key Column</em>' containment reference.
	 * @see #getMapKeyColumn()
	 * @generated
	 */
	void setMapKeyColumn(MapKeyColumn value);

	/**
	 * Returns the value of the '<em><b>Map Key Join Column</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.MapKeyJoinColumn}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Map Key Join Column</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getOneToMany_MapKeyJoinColumn()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='map-key-join-column' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<MapKeyJoinColumn> getMapKeyJoinColumn();

	/**
	 * Returns the value of the '<em><b>Map Key Foreign Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Map Key Foreign Key</em>' containment reference.
	 * @see #setMapKeyForeignKey(ForeignKey)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getOneToMany_MapKeyForeignKey()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='map-key-foreign-key' namespace='##targetNamespace'"
	 * @generated
	 */
	ForeignKey getMapKeyForeignKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.OneToMany#getMapKeyForeignKey <em>Map Key Foreign Key</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Map Key Foreign Key</em>' containment reference.
	 * @see #getMapKeyForeignKey()
	 * @generated
	 */
	void setMapKeyForeignKey(ForeignKey value);

	/**
	 * Returns the value of the '<em><b>Join Column</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.JoinColumn}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Join Column</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getOneToMany_JoinColumn()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='join-column' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<JoinColumn> getJoinColumn();

	/**
	 * Returns the value of the '<em><b>Orphan Removal</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Orphan Removal</em>' attribute.
	 * @see #isSetOrphanRemoval()
	 * @see #unsetOrphanRemoval()
	 * @see #setOrphanRemoval(boolean)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getOneToMany_OrphanRemoval()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='orphan-removal'"
	 * @generated
	 */
	boolean isOrphanRemoval();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.OneToMany#isOrphanRemoval <em>Orphan Removal</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Orphan Removal</em>' attribute.
	 * @see #isSetOrphanRemoval()
	 * @see #unsetOrphanRemoval()
	 * @see #isOrphanRemoval()
	 * @generated
	 */
	void setOrphanRemoval(boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.persistence.eorm.OneToMany#isOrphanRemoval <em>Orphan Removal</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetOrphanRemoval()
	 * @see #isOrphanRemoval()
	 * @see #setOrphanRemoval(boolean)
	 * @generated
	 */
	void unsetOrphanRemoval();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.persistence.eorm.OneToMany#isOrphanRemoval <em>Orphan Removal</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Orphan Removal</em>' attribute is set.
	 * @see #unsetOrphanRemoval()
	 * @see #isOrphanRemoval()
	 * @see #setOrphanRemoval(boolean)
	 * @generated
	 */
	boolean isSetOrphanRemoval();

	/**
	 * Returns the value of the '<em><b>Target Entity</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target Entity</em>' attribute.
	 * @see #setTargetEntity(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getOneToMany_TargetEntity()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='target-entity'"
	 * @generated
	 */
	String getTargetEntity();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.OneToMany#getTargetEntity <em>Target Entity</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target Entity</em>' attribute.
	 * @see #getTargetEntity()
	 * @generated
	 */
	void setTargetEntity(String value);

} // OneToMany
