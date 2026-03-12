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
 * A representation of the model object '<em><b>Element Collection</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 
 *         @Target({METHOD, FIELD}) @Retention(RUNTIME)
 *         public @interface ElementCollection {
 *           Class targetClass() default void.class;
 *           FetchType fetch() default LAZY;
 *         }
 * 
 *       
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getOrderBy <em>Order By</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getOrderColumn <em>Order Column</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKey <em>Map Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyClass <em>Map Key Class</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyTemporal <em>Map Key Temporal</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyEnumerated <em>Map Key Enumerated</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyAttributeOverride <em>Map Key Attribute Override</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyConvert <em>Map Key Convert</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyColumn <em>Map Key Column</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyJoinColumn <em>Map Key Join Column</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyForeignKey <em>Map Key Foreign Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getAttributeOverride <em>Attribute Override</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getAssociationOverride <em>Association Override</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getCollectionTable <em>Collection Table</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getTargetClass <em>Target Class</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getElementCollection()
 * @model extendedMetaData="name='element-collection' kind='elementOnly'"
 * @generated
 */
@ProviderType
public interface ElementCollection extends SimpleBase {
	/**
	 * Returns the value of the '<em><b>Order By</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Order By</em>' attribute.
	 * @see #setOrderBy(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getElementCollection_OrderBy()
	 * @model dataType="org.eclipse.fennec.persistence.eorm.OrderBy"
	 *        extendedMetaData="kind='element' name='order-by' namespace='##targetNamespace'"
	 * @generated
	 */
	String getOrderBy();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getOrderBy <em>Order By</em>}' attribute.
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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getElementCollection_OrderColumn()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='order-column' namespace='##targetNamespace'"
	 * @generated
	 */
	OrderColumn getOrderColumn();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getOrderColumn <em>Order Column</em>}' containment reference.
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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getElementCollection_MapKey()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='map-key' namespace='##targetNamespace'"
	 * @generated
	 */
	MapKey getMapKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKey <em>Map Key</em>}' containment reference.
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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getElementCollection_MapKeyClass()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='map-key-class' namespace='##targetNamespace'"
	 * @generated
	 */
	MapKeyClass getMapKeyClass();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyClass <em>Map Key Class</em>}' containment reference.
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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getElementCollection_MapKeyTemporal()
	 * @model dataType="org.eclipse.fennec.persistence.eorm.Temporal"
	 *        extendedMetaData="kind='element' name='map-key-temporal' namespace='##targetNamespace'"
	 * @generated
	 */
	TemporalType getMapKeyTemporal();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyTemporal <em>Map Key Temporal</em>}' attribute.
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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getElementCollection_MapKeyEnumerated()
	 * @model dataType="org.eclipse.fennec.persistence.eorm.Enumerated"
	 *        extendedMetaData="kind='element' name='map-key-enumerated' namespace='##targetNamespace'"
	 * @generated
	 */
	EnumType getMapKeyEnumerated();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyEnumerated <em>Map Key Enumerated</em>}' attribute.
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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getElementCollection_MapKeyAttributeOverride()
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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getElementCollection_MapKeyConvert()
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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getElementCollection_MapKeyColumn()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='map-key-column' namespace='##targetNamespace'"
	 * @generated
	 */
	MapKeyColumn getMapKeyColumn();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyColumn <em>Map Key Column</em>}' containment reference.
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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getElementCollection_MapKeyJoinColumn()
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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getElementCollection_MapKeyForeignKey()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='map-key-foreign-key' namespace='##targetNamespace'"
	 * @generated
	 */
	ForeignKey getMapKeyForeignKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getMapKeyForeignKey <em>Map Key Foreign Key</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Map Key Foreign Key</em>' containment reference.
	 * @see #getMapKeyForeignKey()
	 * @generated
	 */
	void setMapKeyForeignKey(ForeignKey value);

	/**
	 * Returns the value of the '<em><b>Attribute Override</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.AttributeOverride}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Attribute Override</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getElementCollection_AttributeOverride()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='attribute-override' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<AttributeOverride> getAttributeOverride();

	/**
	 * Returns the value of the '<em><b>Association Override</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.AssociationOverride}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Association Override</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getElementCollection_AssociationOverride()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='association-override' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<AssociationOverride> getAssociationOverride();

	/**
	 * Returns the value of the '<em><b>Collection Table</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Collection Table</em>' containment reference.
	 * @see #setCollectionTable(CollectionTable)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getElementCollection_CollectionTable()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='collection-table' namespace='##targetNamespace'"
	 * @generated
	 */
	CollectionTable getCollectionTable();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getCollectionTable <em>Collection Table</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Collection Table</em>' containment reference.
	 * @see #getCollectionTable()
	 * @generated
	 */
	void setCollectionTable(CollectionTable value);

	/**
	 * Returns the value of the '<em><b>Target Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target Class</em>' attribute.
	 * @see #setTargetClass(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getElementCollection_TargetClass()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='target-class'"
	 * @generated
	 */
	String getTargetClass();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.ElementCollection#getTargetClass <em>Target Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target Class</em>' attribute.
	 * @see #getTargetClass()
	 * @generated
	 */
	void setTargetClass(String value);

} // ElementCollection
