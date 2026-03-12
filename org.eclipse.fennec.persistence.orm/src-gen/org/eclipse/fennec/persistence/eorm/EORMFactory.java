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

import org.eclipse.emf.ecore.EFactory;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage
 * @generated
 */
@ProviderType
public interface EORMFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	EORMFactory eINSTANCE = org.eclipse.fennec.persistence.eorm.impl.EORMFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Association Override</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Association Override</em>'.
	 * @generated
	 */
	AssociationOverride createAssociationOverride();

	/**
	 * Returns a new object of class '<em>Attribute Override</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Attribute Override</em>'.
	 * @generated
	 */
	AttributeOverride createAttributeOverride();

	/**
	 * Returns a new object of class '<em>Attributes</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Attributes</em>'.
	 * @generated
	 */
	Attributes createAttributes();

	/**
	 * Returns a new object of class '<em>Basic</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Basic</em>'.
	 * @generated
	 */
	Basic createBasic();

	/**
	 * Returns a new object of class '<em>Cascade Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Cascade Type</em>'.
	 * @generated
	 */
	CascadeType createCascadeType();

	/**
	 * Returns a new object of class '<em>Collection Table</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Collection Table</em>'.
	 * @generated
	 */
	CollectionTable createCollectionTable();

	/**
	 * Returns a new object of class '<em>Column</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Column</em>'.
	 * @generated
	 */
	Column createColumn();

	/**
	 * Returns a new object of class '<em>Column Result</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Column Result</em>'.
	 * @generated
	 */
	ColumnResult createColumnResult();

	/**
	 * Returns a new object of class '<em>Constructor Result</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Constructor Result</em>'.
	 * @generated
	 */
	ConstructorResult createConstructorResult();

	/**
	 * Returns a new object of class '<em>Convert</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Convert</em>'.
	 * @generated
	 */
	Convert createConvert();

	/**
	 * Returns a new object of class '<em>Converter</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Converter</em>'.
	 * @generated
	 */
	Converter createConverter();

	/**
	 * Returns a new object of class '<em>Discriminator Column</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Discriminator Column</em>'.
	 * @generated
	 */
	DiscriminatorColumn createDiscriminatorColumn();

	/**
	 * Returns a new object of class '<em>Document Root</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Document Root</em>'.
	 * @generated
	 */
	DocumentRoot createDocumentRoot();

	/**
	 * Returns a new object of class '<em>Element Collection</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Element Collection</em>'.
	 * @generated
	 */
	ElementCollection createElementCollection();

	/**
	 * Returns a new object of class '<em>Embeddable</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Embeddable</em>'.
	 * @generated
	 */
	Embeddable createEmbeddable();

	/**
	 * Returns a new object of class '<em>Embeddable Attributes</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Embeddable Attributes</em>'.
	 * @generated
	 */
	EmbeddableAttributes createEmbeddableAttributes();

	/**
	 * Returns a new object of class '<em>Embedded</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Embedded</em>'.
	 * @generated
	 */
	Embedded createEmbedded();

	/**
	 * Returns a new object of class '<em>Embedded Id</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Embedded Id</em>'.
	 * @generated
	 */
	EmbeddedId createEmbeddedId();

	/**
	 * Returns a new object of class '<em>Empty Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Empty Type</em>'.
	 * @generated
	 */
	EmptyType createEmptyType();

	/**
	 * Returns a new object of class '<em>Entity</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Entity</em>'.
	 * @generated
	 */
	Entity createEntity();

	/**
	 * Returns a new object of class '<em>Entity Listener</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Entity Listener</em>'.
	 * @generated
	 */
	EntityListener createEntityListener();

	/**
	 * Returns a new object of class '<em>Entity Listeners</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Entity Listeners</em>'.
	 * @generated
	 */
	EntityListeners createEntityListeners();

	/**
	 * Returns a new object of class '<em>Entity Mappings</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Entity Mappings</em>'.
	 * @generated
	 */
	EntityMappings createEntityMappings();

	/**
	 * Returns a new object of class '<em>Entity Result</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Entity Result</em>'.
	 * @generated
	 */
	EntityResult createEntityResult();

	/**
	 * Returns a new object of class '<em>Field Result</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Field Result</em>'.
	 * @generated
	 */
	FieldResult createFieldResult();

	/**
	 * Returns a new object of class '<em>Foreign Key</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Foreign Key</em>'.
	 * @generated
	 */
	ForeignKey createForeignKey();

	/**
	 * Returns a new object of class '<em>Generated Value</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Generated Value</em>'.
	 * @generated
	 */
	GeneratedValue createGeneratedValue();

	/**
	 * Returns a new object of class '<em>ENamed Base</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>ENamed Base</em>'.
	 * @generated
	 */
	ENamedBase createENamedBase();

	/**
	 * Returns a new object of class '<em>Base</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Base</em>'.
	 * @generated
	 */
	Base createBase();

	/**
	 * Returns a new object of class '<em>Base Ref</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Base Ref</em>'.
	 * @generated
	 */
	BaseRef createBaseRef();

	/**
	 * Returns a new object of class '<em>Id</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Id</em>'.
	 * @generated
	 */
	Id createId();

	/**
	 * Returns a new object of class '<em>Id Class</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Id Class</em>'.
	 * @generated
	 */
	IdClass createIdClass();

	/**
	 * Returns a new object of class '<em>Index</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Index</em>'.
	 * @generated
	 */
	Index createIndex();

	/**
	 * Returns a new object of class '<em>Inheritance</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Inheritance</em>'.
	 * @generated
	 */
	Inheritance createInheritance();

	/**
	 * Returns a new object of class '<em>Join Column</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Join Column</em>'.
	 * @generated
	 */
	JoinColumn createJoinColumn();

	/**
	 * Returns a new object of class '<em>Join Table</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Join Table</em>'.
	 * @generated
	 */
	JoinTable createJoinTable();

	/**
	 * Returns a new object of class '<em>Lob</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Lob</em>'.
	 * @generated
	 */
	Lob createLob();

	/**
	 * Returns a new object of class '<em>Many To Many</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Many To Many</em>'.
	 * @generated
	 */
	ManyToMany createManyToMany();

	/**
	 * Returns a new object of class '<em>Many To One</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Many To One</em>'.
	 * @generated
	 */
	ManyToOne createManyToOne();

	/**
	 * Returns a new object of class '<em>Map Key</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Map Key</em>'.
	 * @generated
	 */
	MapKey createMapKey();

	/**
	 * Returns a new object of class '<em>Map Key Class</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Map Key Class</em>'.
	 * @generated
	 */
	MapKeyClass createMapKeyClass();

	/**
	 * Returns a new object of class '<em>Map Key Column</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Map Key Column</em>'.
	 * @generated
	 */
	MapKeyColumn createMapKeyColumn();

	/**
	 * Returns a new object of class '<em>Map Key Join Column</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Map Key Join Column</em>'.
	 * @generated
	 */
	MapKeyJoinColumn createMapKeyJoinColumn();

	/**
	 * Returns a new object of class '<em>Mapped Superclass</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Mapped Superclass</em>'.
	 * @generated
	 */
	MappedSuperclass createMappedSuperclass();

	/**
	 * Returns a new object of class '<em>Named Attribute Node</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Named Attribute Node</em>'.
	 * @generated
	 */
	NamedAttributeNode createNamedAttributeNode();

	/**
	 * Returns a new object of class '<em>Named Entity Graph</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Named Entity Graph</em>'.
	 * @generated
	 */
	NamedEntityGraph createNamedEntityGraph();

	/**
	 * Returns a new object of class '<em>Named Native Query</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Named Native Query</em>'.
	 * @generated
	 */
	NamedNativeQuery createNamedNativeQuery();

	/**
	 * Returns a new object of class '<em>Named Query</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Named Query</em>'.
	 * @generated
	 */
	NamedQuery createNamedQuery();

	/**
	 * Returns a new object of class '<em>Named Stored Procedure Query</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Named Stored Procedure Query</em>'.
	 * @generated
	 */
	NamedStoredProcedureQuery createNamedStoredProcedureQuery();

	/**
	 * Returns a new object of class '<em>Named Subgraph</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Named Subgraph</em>'.
	 * @generated
	 */
	NamedSubgraph createNamedSubgraph();

	/**
	 * Returns a new object of class '<em>One To Many</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>One To Many</em>'.
	 * @generated
	 */
	OneToMany createOneToMany();

	/**
	 * Returns a new object of class '<em>One To One</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>One To One</em>'.
	 * @generated
	 */
	OneToOne createOneToOne();

	/**
	 * Returns a new object of class '<em>Order Column</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Order Column</em>'.
	 * @generated
	 */
	OrderColumn createOrderColumn();

	/**
	 * Returns a new object of class '<em>Persistence Unit Defaults</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Persistence Unit Defaults</em>'.
	 * @generated
	 */
	PersistenceUnitDefaults createPersistenceUnitDefaults();

	/**
	 * Returns a new object of class '<em>Persistence Unit Metadata</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Persistence Unit Metadata</em>'.
	 * @generated
	 */
	PersistenceUnitMetadata createPersistenceUnitMetadata();

	/**
	 * Returns a new object of class '<em>Post Load</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Post Load</em>'.
	 * @generated
	 */
	PostLoad createPostLoad();

	/**
	 * Returns a new object of class '<em>Post Persist</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Post Persist</em>'.
	 * @generated
	 */
	PostPersist createPostPersist();

	/**
	 * Returns a new object of class '<em>Post Remove</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Post Remove</em>'.
	 * @generated
	 */
	PostRemove createPostRemove();

	/**
	 * Returns a new object of class '<em>Post Update</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Post Update</em>'.
	 * @generated
	 */
	PostUpdate createPostUpdate();

	/**
	 * Returns a new object of class '<em>Pre Persist</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Pre Persist</em>'.
	 * @generated
	 */
	PrePersist createPrePersist();

	/**
	 * Returns a new object of class '<em>Pre Remove</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Pre Remove</em>'.
	 * @generated
	 */
	PreRemove createPreRemove();

	/**
	 * Returns a new object of class '<em>Pre Update</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Pre Update</em>'.
	 * @generated
	 */
	PreUpdate createPreUpdate();

	/**
	 * Returns a new object of class '<em>Primary Key Join Column</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Primary Key Join Column</em>'.
	 * @generated
	 */
	PrimaryKeyJoinColumn createPrimaryKeyJoinColumn();

	/**
	 * Returns a new object of class '<em>Query Hint</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Query Hint</em>'.
	 * @generated
	 */
	QueryHint createQueryHint();

	/**
	 * Returns a new object of class '<em>Secondary Table</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Secondary Table</em>'.
	 * @generated
	 */
	SecondaryTable createSecondaryTable();

	/**
	 * Returns a new object of class '<em>Sequence Generator</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Sequence Generator</em>'.
	 * @generated
	 */
	SequenceGenerator createSequenceGenerator();

	/**
	 * Returns a new object of class '<em>Simple Base</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Simple Base</em>'.
	 * @generated
	 */
	SimpleBase createSimpleBase();

	/**
	 * Returns a new object of class '<em>Sql Result Set Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Sql Result Set Mapping</em>'.
	 * @generated
	 */
	SqlResultSetMapping createSqlResultSetMapping();

	/**
	 * Returns a new object of class '<em>Stored Procedure Parameter</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Stored Procedure Parameter</em>'.
	 * @generated
	 */
	StoredProcedureParameter createStoredProcedureParameter();

	/**
	 * Returns a new object of class '<em>Table</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Table</em>'.
	 * @generated
	 */
	Table createTable();

	/**
	 * Returns a new object of class '<em>Table Generator</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Table Generator</em>'.
	 * @generated
	 */
	TableGenerator createTableGenerator();

	/**
	 * Returns a new object of class '<em>Transient</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Transient</em>'.
	 * @generated
	 */
	Transient createTransient();

	/**
	 * Returns a new object of class '<em>Unique Constraint</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Unique Constraint</em>'.
	 * @generated
	 */
	UniqueConstraint createUniqueConstraint();

	/**
	 * Returns a new object of class '<em>Version</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Version</em>'.
	 * @generated
	 */
	Version createVersion();

	/**
	 * Returns a new object of class '<em>EAccessor</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>EAccessor</em>'.
	 * @generated
	 */
	EAccessor createEAccessor();

	/**
	 * Returns a new object of class '<em>EFeature Object</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>EFeature Object</em>'.
	 * @generated
	 */
	EFeatureObject createEFeatureObject();

	/**
	 * Returns a new object of class '<em>EClass Object</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>EClass Object</em>'.
	 * @generated
	 */
	EClassObject createEClassObject();

	/**
	 * Returns a new object of class '<em>Element</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Element</em>'.
	 * @generated
	 */
	EORMElement createEORMElement();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	EORMPackage getEORMPackage();

} //EORMFactory
