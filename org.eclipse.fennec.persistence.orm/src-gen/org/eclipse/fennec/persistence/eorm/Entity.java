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

import org.eclipse.emf.ecore.EClassifier;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Entity</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 
 *         Defines the settings and mappings for an entity. Is allowed to be
 *         sparsely populated and used in conjunction with the annotations.
 *         Alternatively, the metadata-complete attribute can be used to 
 *         indicate that no annotations on the entity class (and its fields
 *         or properties) are to be processed. If this is the case then 
 *         the defaulting rules for the entity and its subelements will 
 *         be recursively applied.
 * 
 *         @Target(TYPE) @Retention(RUNTIME)
 *           public @interface Entity {
 *           String name() default "";
 *         }
 * 
 *       
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getTable <em>Table</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getSecondaryTable <em>Secondary Table</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getPrimaryKeyJoinColumn <em>Primary Key Join Column</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getPrimaryKeyForeignKey <em>Primary Key Foreign Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getIdClass <em>Id Class</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getInheritance <em>Inheritance</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getDiscriminatorValue <em>Discriminator Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getDiscriminatorColumn <em>Discriminator Column</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getSequenceGenerator <em>Sequence Generator</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getTableGenerator <em>Table Generator</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getNamedQuery <em>Named Query</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getNamedNativeQuery <em>Named Native Query</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getNamedStoredProcedureQuery <em>Named Stored Procedure Query</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getSqlResultSetMapping <em>Sql Result Set Mapping</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getExcludeDefaultListeners <em>Exclude Default Listeners</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getExcludeSuperclassListeners <em>Exclude Superclass Listeners</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getEntityListeners <em>Entity Listeners</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getPrePersist <em>Pre Persist</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getPostPersist <em>Post Persist</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getPreRemove <em>Pre Remove</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getPostRemove <em>Post Remove</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getPreUpdate <em>Pre Update</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getPostUpdate <em>Post Update</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getPostLoad <em>Post Load</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getAttributeOverride <em>Attribute Override</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getAssociationOverride <em>Association Override</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getConvert <em>Convert</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getNamedEntityGraph <em>Named Entity Graph</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getAttributes <em>Attributes</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getAccess <em>Access</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#isCacheable <em>Cacheable</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#isMetadataComplete <em>Metadata Complete</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.Entity#getClass_ <em>Class</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity()
 * @model extendedMetaData="name='entity' kind='elementOnly'"
 * @generated
 */
@ProviderType
public interface Entity extends EORMElement {
	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_Description()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='description' namespace='##targetNamespace'"
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Table</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Table</em>' containment reference.
	 * @see #setTable(Table)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_Table()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='table' namespace='##targetNamespace'"
	 * @generated
	 */
	Table getTable();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getTable <em>Table</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Table</em>' containment reference.
	 * @see #getTable()
	 * @generated
	 */
	void setTable(Table value);

	/**
	 * Returns the value of the '<em><b>Secondary Table</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.SecondaryTable}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Secondary Table</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_SecondaryTable()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='secondary-table' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<SecondaryTable> getSecondaryTable();

	/**
	 * Returns the value of the '<em><b>Primary Key Join Column</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.PrimaryKeyJoinColumn}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Primary Key Join Column</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_PrimaryKeyJoinColumn()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='primary-key-join-column' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<PrimaryKeyJoinColumn> getPrimaryKeyJoinColumn();

	/**
	 * Returns the value of the '<em><b>Primary Key Foreign Key</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Primary Key Foreign Key</em>' containment reference.
	 * @see #setPrimaryKeyForeignKey(ForeignKey)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_PrimaryKeyForeignKey()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='primary-key-foreign-key' namespace='##targetNamespace'"
	 * @generated
	 */
	ForeignKey getPrimaryKeyForeignKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getPrimaryKeyForeignKey <em>Primary Key Foreign Key</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Primary Key Foreign Key</em>' containment reference.
	 * @see #getPrimaryKeyForeignKey()
	 * @generated
	 */
	void setPrimaryKeyForeignKey(ForeignKey value);

	/**
	 * Returns the value of the '<em><b>Id Class</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id Class</em>' containment reference.
	 * @see #setIdClass(IdClass)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_IdClass()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='id-class' namespace='##targetNamespace'"
	 * @generated
	 */
	IdClass getIdClass();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getIdClass <em>Id Class</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id Class</em>' containment reference.
	 * @see #getIdClass()
	 * @generated
	 */
	void setIdClass(IdClass value);

	/**
	 * Returns the value of the '<em><b>Inheritance</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Inheritance</em>' containment reference.
	 * @see #setInheritance(Inheritance)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_Inheritance()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='inheritance' namespace='##targetNamespace'"
	 * @generated
	 */
	Inheritance getInheritance();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getInheritance <em>Inheritance</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Inheritance</em>' containment reference.
	 * @see #getInheritance()
	 * @generated
	 */
	void setInheritance(Inheritance value);

	/**
	 * Returns the value of the '<em><b>Discriminator Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Discriminator Value</em>' attribute.
	 * @see #setDiscriminatorValue(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_DiscriminatorValue()
	 * @model dataType="org.eclipse.fennec.persistence.eorm.DiscriminatorValue"
	 *        extendedMetaData="kind='element' name='discriminator-value' namespace='##targetNamespace'"
	 * @generated
	 */
	String getDiscriminatorValue();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getDiscriminatorValue <em>Discriminator Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Discriminator Value</em>' attribute.
	 * @see #getDiscriminatorValue()
	 * @generated
	 */
	void setDiscriminatorValue(String value);

	/**
	 * Returns the value of the '<em><b>Discriminator Column</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Discriminator Column</em>' containment reference.
	 * @see #setDiscriminatorColumn(DiscriminatorColumn)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_DiscriminatorColumn()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='discriminator-column' namespace='##targetNamespace'"
	 * @generated
	 */
	DiscriminatorColumn getDiscriminatorColumn();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getDiscriminatorColumn <em>Discriminator Column</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Discriminator Column</em>' containment reference.
	 * @see #getDiscriminatorColumn()
	 * @generated
	 */
	void setDiscriminatorColumn(DiscriminatorColumn value);

	/**
	 * Returns the value of the '<em><b>Sequence Generator</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sequence Generator</em>' containment reference.
	 * @see #setSequenceGenerator(SequenceGenerator)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_SequenceGenerator()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='sequence-generator' namespace='##targetNamespace'"
	 * @generated
	 */
	SequenceGenerator getSequenceGenerator();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getSequenceGenerator <em>Sequence Generator</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sequence Generator</em>' containment reference.
	 * @see #getSequenceGenerator()
	 * @generated
	 */
	void setSequenceGenerator(SequenceGenerator value);

	/**
	 * Returns the value of the '<em><b>Table Generator</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Table Generator</em>' containment reference.
	 * @see #setTableGenerator(TableGenerator)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_TableGenerator()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='table-generator' namespace='##targetNamespace'"
	 * @generated
	 */
	TableGenerator getTableGenerator();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getTableGenerator <em>Table Generator</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Table Generator</em>' containment reference.
	 * @see #getTableGenerator()
	 * @generated
	 */
	void setTableGenerator(TableGenerator value);

	/**
	 * Returns the value of the '<em><b>Named Query</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.NamedQuery}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Named Query</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_NamedQuery()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='named-query' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<NamedQuery> getNamedQuery();

	/**
	 * Returns the value of the '<em><b>Named Native Query</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.NamedNativeQuery}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Named Native Query</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_NamedNativeQuery()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='named-native-query' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<NamedNativeQuery> getNamedNativeQuery();

	/**
	 * Returns the value of the '<em><b>Named Stored Procedure Query</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Named Stored Procedure Query</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_NamedStoredProcedureQuery()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='named-stored-procedure-query' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<NamedStoredProcedureQuery> getNamedStoredProcedureQuery();

	/**
	 * Returns the value of the '<em><b>Sql Result Set Mapping</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.SqlResultSetMapping}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sql Result Set Mapping</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_SqlResultSetMapping()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='sql-result-set-mapping' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<SqlResultSetMapping> getSqlResultSetMapping();

	/**
	 * Returns the value of the '<em><b>Exclude Default Listeners</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Exclude Default Listeners</em>' containment reference.
	 * @see #setExcludeDefaultListeners(EmptyType)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_ExcludeDefaultListeners()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='exclude-default-listeners' namespace='##targetNamespace'"
	 * @generated
	 */
	EmptyType getExcludeDefaultListeners();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getExcludeDefaultListeners <em>Exclude Default Listeners</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Exclude Default Listeners</em>' containment reference.
	 * @see #getExcludeDefaultListeners()
	 * @generated
	 */
	void setExcludeDefaultListeners(EmptyType value);

	/**
	 * Returns the value of the '<em><b>Exclude Superclass Listeners</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Exclude Superclass Listeners</em>' containment reference.
	 * @see #setExcludeSuperclassListeners(EmptyType)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_ExcludeSuperclassListeners()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='exclude-superclass-listeners' namespace='##targetNamespace'"
	 * @generated
	 */
	EmptyType getExcludeSuperclassListeners();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getExcludeSuperclassListeners <em>Exclude Superclass Listeners</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Exclude Superclass Listeners</em>' containment reference.
	 * @see #getExcludeSuperclassListeners()
	 * @generated
	 */
	void setExcludeSuperclassListeners(EmptyType value);

	/**
	 * Returns the value of the '<em><b>Entity Listeners</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Entity Listeners</em>' containment reference.
	 * @see #setEntityListeners(EntityListeners)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_EntityListeners()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='entity-listeners' namespace='##targetNamespace'"
	 * @generated
	 */
	EntityListeners getEntityListeners();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getEntityListeners <em>Entity Listeners</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Entity Listeners</em>' containment reference.
	 * @see #getEntityListeners()
	 * @generated
	 */
	void setEntityListeners(EntityListeners value);

	/**
	 * Returns the value of the '<em><b>Pre Persist</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Pre Persist</em>' containment reference.
	 * @see #setPrePersist(PrePersist)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_PrePersist()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='pre-persist' namespace='##targetNamespace'"
	 * @generated
	 */
	PrePersist getPrePersist();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getPrePersist <em>Pre Persist</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Pre Persist</em>' containment reference.
	 * @see #getPrePersist()
	 * @generated
	 */
	void setPrePersist(PrePersist value);

	/**
	 * Returns the value of the '<em><b>Post Persist</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Post Persist</em>' containment reference.
	 * @see #setPostPersist(PostPersist)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_PostPersist()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='post-persist' namespace='##targetNamespace'"
	 * @generated
	 */
	PostPersist getPostPersist();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getPostPersist <em>Post Persist</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Post Persist</em>' containment reference.
	 * @see #getPostPersist()
	 * @generated
	 */
	void setPostPersist(PostPersist value);

	/**
	 * Returns the value of the '<em><b>Pre Remove</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Pre Remove</em>' containment reference.
	 * @see #setPreRemove(PreRemove)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_PreRemove()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='pre-remove' namespace='##targetNamespace'"
	 * @generated
	 */
	PreRemove getPreRemove();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getPreRemove <em>Pre Remove</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Pre Remove</em>' containment reference.
	 * @see #getPreRemove()
	 * @generated
	 */
	void setPreRemove(PreRemove value);

	/**
	 * Returns the value of the '<em><b>Post Remove</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Post Remove</em>' containment reference.
	 * @see #setPostRemove(PostRemove)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_PostRemove()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='post-remove' namespace='##targetNamespace'"
	 * @generated
	 */
	PostRemove getPostRemove();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getPostRemove <em>Post Remove</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Post Remove</em>' containment reference.
	 * @see #getPostRemove()
	 * @generated
	 */
	void setPostRemove(PostRemove value);

	/**
	 * Returns the value of the '<em><b>Pre Update</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Pre Update</em>' containment reference.
	 * @see #setPreUpdate(PreUpdate)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_PreUpdate()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='pre-update' namespace='##targetNamespace'"
	 * @generated
	 */
	PreUpdate getPreUpdate();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getPreUpdate <em>Pre Update</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Pre Update</em>' containment reference.
	 * @see #getPreUpdate()
	 * @generated
	 */
	void setPreUpdate(PreUpdate value);

	/**
	 * Returns the value of the '<em><b>Post Update</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Post Update</em>' containment reference.
	 * @see #setPostUpdate(PostUpdate)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_PostUpdate()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='post-update' namespace='##targetNamespace'"
	 * @generated
	 */
	PostUpdate getPostUpdate();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getPostUpdate <em>Post Update</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Post Update</em>' containment reference.
	 * @see #getPostUpdate()
	 * @generated
	 */
	void setPostUpdate(PostUpdate value);

	/**
	 * Returns the value of the '<em><b>Post Load</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Post Load</em>' containment reference.
	 * @see #setPostLoad(PostLoad)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_PostLoad()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='post-load' namespace='##targetNamespace'"
	 * @generated
	 */
	PostLoad getPostLoad();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getPostLoad <em>Post Load</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Post Load</em>' containment reference.
	 * @see #getPostLoad()
	 * @generated
	 */
	void setPostLoad(PostLoad value);

	/**
	 * Returns the value of the '<em><b>Attribute Override</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.AttributeOverride}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Attribute Override</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_AttributeOverride()
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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_AssociationOverride()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='association-override' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<AssociationOverride> getAssociationOverride();

	/**
	 * Returns the value of the '<em><b>Convert</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.Convert}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Convert</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_Convert()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='convert' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<Convert> getConvert();

	/**
	 * Returns the value of the '<em><b>Named Entity Graph</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.NamedEntityGraph}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Named Entity Graph</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_NamedEntityGraph()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='named-entity-graph' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<NamedEntityGraph> getNamedEntityGraph();

	/**
	 * Returns the value of the '<em><b>Attributes</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Attributes</em>' containment reference.
	 * @see #setAttributes(Attributes)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_Attributes()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='attributes' namespace='##targetNamespace'"
	 * @generated
	 */
	Attributes getAttributes();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getAttributes <em>Attributes</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Attributes</em>' containment reference.
	 * @see #getAttributes()
	 * @generated
	 */
	void setAttributes(Attributes value);

	/**
	 * Returns the value of the '<em><b>Access</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.persistence.eorm.AccessType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Access</em>' attribute.
	 * @see org.eclipse.fennec.persistence.eorm.AccessType
	 * @see #isSetAccess()
	 * @see #unsetAccess()
	 * @see #setAccess(AccessType)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_Access()
	 * @model unsettable="true"
	 *        extendedMetaData="kind='attribute' name='access'"
	 * @generated
	 */
	AccessType getAccess();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getAccess <em>Access</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Access</em>' attribute.
	 * @see org.eclipse.fennec.persistence.eorm.AccessType
	 * @see #isSetAccess()
	 * @see #unsetAccess()
	 * @see #getAccess()
	 * @generated
	 */
	void setAccess(AccessType value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getAccess <em>Access</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetAccess()
	 * @see #getAccess()
	 * @see #setAccess(AccessType)
	 * @generated
	 */
	void unsetAccess();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getAccess <em>Access</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Access</em>' attribute is set.
	 * @see #unsetAccess()
	 * @see #getAccess()
	 * @see #setAccess(AccessType)
	 * @generated
	 */
	boolean isSetAccess();

	/**
	 * Returns the value of the '<em><b>Cacheable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Cacheable</em>' attribute.
	 * @see #isSetCacheable()
	 * @see #unsetCacheable()
	 * @see #setCacheable(boolean)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_Cacheable()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='cacheable'"
	 * @generated
	 */
	boolean isCacheable();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#isCacheable <em>Cacheable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Cacheable</em>' attribute.
	 * @see #isSetCacheable()
	 * @see #unsetCacheable()
	 * @see #isCacheable()
	 * @generated
	 */
	void setCacheable(boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#isCacheable <em>Cacheable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetCacheable()
	 * @see #isCacheable()
	 * @see #setCacheable(boolean)
	 * @generated
	 */
	void unsetCacheable();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#isCacheable <em>Cacheable</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Cacheable</em>' attribute is set.
	 * @see #unsetCacheable()
	 * @see #isCacheable()
	 * @see #setCacheable(boolean)
	 * @generated
	 */
	boolean isSetCacheable();

	/**
	 * Returns the value of the '<em><b>Metadata Complete</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Metadata Complete</em>' attribute.
	 * @see #isSetMetadataComplete()
	 * @see #unsetMetadataComplete()
	 * @see #setMetadataComplete(boolean)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_MetadataComplete()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='metadata-complete'"
	 * @generated
	 */
	boolean isMetadataComplete();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#isMetadataComplete <em>Metadata Complete</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Metadata Complete</em>' attribute.
	 * @see #isSetMetadataComplete()
	 * @see #unsetMetadataComplete()
	 * @see #isMetadataComplete()
	 * @generated
	 */
	void setMetadataComplete(boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#isMetadataComplete <em>Metadata Complete</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetMetadataComplete()
	 * @see #isMetadataComplete()
	 * @see #setMetadataComplete(boolean)
	 * @generated
	 */
	void unsetMetadataComplete();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#isMetadataComplete <em>Metadata Complete</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Metadata Complete</em>' attribute is set.
	 * @see #unsetMetadataComplete()
	 * @see #isMetadataComplete()
	 * @see #setMetadataComplete(boolean)
	 * @generated
	 */
	boolean isSetMetadataComplete();

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_Name()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='name'"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Class</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Class</em>' reference.
	 * @see #setClass(EClassifier)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntity_Class()
	 * @model required="true"
	 *        extendedMetaData="kind='attribute' name='class'"
	 * @generated
	 */
	EClassifier getClass_();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.Entity#getClass_ <em>Class</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Class</em>' reference.
	 * @see #getClass_()
	 * @generated
	 */
	void setClass(EClassifier value);

} // Entity
