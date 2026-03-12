/**
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
package org.eclipse.fennec.persistence.eorm.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.emf.ecore.xml.type.XMLTypeFactory;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

import org.eclipse.fennec.persistence.eorm.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class EORMFactoryImpl extends EFactoryImpl implements EORMFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static EORMFactory init() {
		try {
			EORMFactory theEORMFactory = (EORMFactory)EPackage.Registry.INSTANCE.getEFactory(EORMPackage.eNS_URI);
			if (theEORMFactory != null) {
				return theEORMFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new EORMFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EORMFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case EORMPackage.ASSOCIATION_OVERRIDE: return createAssociationOverride();
			case EORMPackage.ATTRIBUTE_OVERRIDE: return createAttributeOverride();
			case EORMPackage.ATTRIBUTES: return createAttributes();
			case EORMPackage.BASIC: return createBasic();
			case EORMPackage.CASCADE_TYPE: return createCascadeType();
			case EORMPackage.COLLECTION_TABLE: return createCollectionTable();
			case EORMPackage.COLUMN: return createColumn();
			case EORMPackage.COLUMN_RESULT: return createColumnResult();
			case EORMPackage.CONSTRUCTOR_RESULT: return createConstructorResult();
			case EORMPackage.CONVERT: return createConvert();
			case EORMPackage.CONVERTER: return createConverter();
			case EORMPackage.DISCRIMINATOR_COLUMN: return createDiscriminatorColumn();
			case EORMPackage.DOCUMENT_ROOT: return createDocumentRoot();
			case EORMPackage.ELEMENT_COLLECTION: return createElementCollection();
			case EORMPackage.EMBEDDABLE: return createEmbeddable();
			case EORMPackage.EMBEDDABLE_ATTRIBUTES: return createEmbeddableAttributes();
			case EORMPackage.EMBEDDED: return createEmbedded();
			case EORMPackage.EMBEDDED_ID: return createEmbeddedId();
			case EORMPackage.EMPTY_TYPE: return createEmptyType();
			case EORMPackage.ENTITY: return createEntity();
			case EORMPackage.ENTITY_LISTENER: return createEntityListener();
			case EORMPackage.ENTITY_LISTENERS: return createEntityListeners();
			case EORMPackage.ENTITY_MAPPINGS: return createEntityMappings();
			case EORMPackage.ENTITY_RESULT: return createEntityResult();
			case EORMPackage.FIELD_RESULT: return createFieldResult();
			case EORMPackage.FOREIGN_KEY: return createForeignKey();
			case EORMPackage.GENERATED_VALUE: return createGeneratedValue();
			case EORMPackage.ENAMED_BASE: return createENamedBase();
			case EORMPackage.BASE: return createBase();
			case EORMPackage.BASE_REF: return createBaseRef();
			case EORMPackage.ID: return createId();
			case EORMPackage.ID_CLASS: return createIdClass();
			case EORMPackage.INDEX: return createIndex();
			case EORMPackage.INHERITANCE: return createInheritance();
			case EORMPackage.JOIN_COLUMN: return createJoinColumn();
			case EORMPackage.JOIN_TABLE: return createJoinTable();
			case EORMPackage.LOB: return createLob();
			case EORMPackage.MANY_TO_MANY: return createManyToMany();
			case EORMPackage.MANY_TO_ONE: return createManyToOne();
			case EORMPackage.MAP_KEY: return createMapKey();
			case EORMPackage.MAP_KEY_CLASS: return createMapKeyClass();
			case EORMPackage.MAP_KEY_COLUMN: return createMapKeyColumn();
			case EORMPackage.MAP_KEY_JOIN_COLUMN: return createMapKeyJoinColumn();
			case EORMPackage.MAPPED_SUPERCLASS: return createMappedSuperclass();
			case EORMPackage.NAMED_ATTRIBUTE_NODE: return createNamedAttributeNode();
			case EORMPackage.NAMED_ENTITY_GRAPH: return createNamedEntityGraph();
			case EORMPackage.NAMED_NATIVE_QUERY: return createNamedNativeQuery();
			case EORMPackage.NAMED_QUERY: return createNamedQuery();
			case EORMPackage.NAMED_STORED_PROCEDURE_QUERY: return createNamedStoredProcedureQuery();
			case EORMPackage.NAMED_SUBGRAPH: return createNamedSubgraph();
			case EORMPackage.ONE_TO_MANY: return createOneToMany();
			case EORMPackage.ONE_TO_ONE: return createOneToOne();
			case EORMPackage.ORDER_COLUMN: return createOrderColumn();
			case EORMPackage.PERSISTENCE_UNIT_DEFAULTS: return createPersistenceUnitDefaults();
			case EORMPackage.PERSISTENCE_UNIT_METADATA: return createPersistenceUnitMetadata();
			case EORMPackage.POST_LOAD: return createPostLoad();
			case EORMPackage.POST_PERSIST: return createPostPersist();
			case EORMPackage.POST_REMOVE: return createPostRemove();
			case EORMPackage.POST_UPDATE: return createPostUpdate();
			case EORMPackage.PRE_PERSIST: return createPrePersist();
			case EORMPackage.PRE_REMOVE: return createPreRemove();
			case EORMPackage.PRE_UPDATE: return createPreUpdate();
			case EORMPackage.PRIMARY_KEY_JOIN_COLUMN: return createPrimaryKeyJoinColumn();
			case EORMPackage.QUERY_HINT: return createQueryHint();
			case EORMPackage.SECONDARY_TABLE: return createSecondaryTable();
			case EORMPackage.SEQUENCE_GENERATOR: return createSequenceGenerator();
			case EORMPackage.SIMPLE_BASE: return createSimpleBase();
			case EORMPackage.SQL_RESULT_SET_MAPPING: return createSqlResultSetMapping();
			case EORMPackage.STORED_PROCEDURE_PARAMETER: return createStoredProcedureParameter();
			case EORMPackage.TABLE: return createTable();
			case EORMPackage.TABLE_GENERATOR: return createTableGenerator();
			case EORMPackage.TRANSIENT: return createTransient();
			case EORMPackage.UNIQUE_CONSTRAINT: return createUniqueConstraint();
			case EORMPackage.VERSION: return createVersion();
			case EORMPackage.EACCESSOR: return createEAccessor();
			case EORMPackage.EFEATURE_OBJECT: return createEFeatureObject();
			case EORMPackage.ECLASS_OBJECT: return createEClassObject();
			case EORMPackage.EORM_ELEMENT: return createEORMElement();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case EORMPackage.ACCESS_TYPE:
				return createAccessTypeFromString(eDataType, initialValue);
			case EORMPackage.CONSTRAINT_MODE:
				return createConstraintModeFromString(eDataType, initialValue);
			case EORMPackage.DISCRIMINATOR_TYPE:
				return createDiscriminatorTypeFromString(eDataType, initialValue);
			case EORMPackage.ENUM_TYPE:
				return createEnumTypeFromString(eDataType, initialValue);
			case EORMPackage.FETCH_TYPE:
				return createFetchTypeFromString(eDataType, initialValue);
			case EORMPackage.GENERATION_TYPE:
				return createGenerationTypeFromString(eDataType, initialValue);
			case EORMPackage.INHERITANCE_TYPE:
				return createInheritanceTypeFromString(eDataType, initialValue);
			case EORMPackage.LOCK_MODE_TYPE:
				return createLockModeTypeFromString(eDataType, initialValue);
			case EORMPackage.PARAMETER_MODE:
				return createParameterModeFromString(eDataType, initialValue);
			case EORMPackage.TEMPORAL_TYPE:
				return createTemporalTypeFromString(eDataType, initialValue);
			case EORMPackage.ACCESS_TYPE_OBJECT:
				return createAccessTypeObjectFromString(eDataType, initialValue);
			case EORMPackage.CONSTRAINT_MODE_OBJECT:
				return createConstraintModeObjectFromString(eDataType, initialValue);
			case EORMPackage.DISCRIMINATOR_TYPE_OBJECT:
				return createDiscriminatorTypeObjectFromString(eDataType, initialValue);
			case EORMPackage.DISCRIMINATOR_VALUE:
				return createDiscriminatorValueFromString(eDataType, initialValue);
			case EORMPackage.ENUMERATED:
				return createEnumeratedFromString(eDataType, initialValue);
			case EORMPackage.ENUM_TYPE_OBJECT:
				return createEnumTypeObjectFromString(eDataType, initialValue);
			case EORMPackage.FETCH_TYPE_OBJECT:
				return createFetchTypeObjectFromString(eDataType, initialValue);
			case EORMPackage.GENERATION_TYPE_OBJECT:
				return createGenerationTypeObjectFromString(eDataType, initialValue);
			case EORMPackage.INHERITANCE_TYPE_OBJECT:
				return createInheritanceTypeObjectFromString(eDataType, initialValue);
			case EORMPackage.LOCK_MODE_TYPE_OBJECT:
				return createLockModeTypeObjectFromString(eDataType, initialValue);
			case EORMPackage.ORDER_BY:
				return createOrderByFromString(eDataType, initialValue);
			case EORMPackage.PARAMETER_MODE_OBJECT:
				return createParameterModeObjectFromString(eDataType, initialValue);
			case EORMPackage.TEMPORAL:
				return createTemporalFromString(eDataType, initialValue);
			case EORMPackage.TEMPORAL_TYPE_OBJECT:
				return createTemporalTypeObjectFromString(eDataType, initialValue);
			case EORMPackage.VERSION_TYPE:
				return createVersionTypeFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case EORMPackage.ACCESS_TYPE:
				return convertAccessTypeToString(eDataType, instanceValue);
			case EORMPackage.CONSTRAINT_MODE:
				return convertConstraintModeToString(eDataType, instanceValue);
			case EORMPackage.DISCRIMINATOR_TYPE:
				return convertDiscriminatorTypeToString(eDataType, instanceValue);
			case EORMPackage.ENUM_TYPE:
				return convertEnumTypeToString(eDataType, instanceValue);
			case EORMPackage.FETCH_TYPE:
				return convertFetchTypeToString(eDataType, instanceValue);
			case EORMPackage.GENERATION_TYPE:
				return convertGenerationTypeToString(eDataType, instanceValue);
			case EORMPackage.INHERITANCE_TYPE:
				return convertInheritanceTypeToString(eDataType, instanceValue);
			case EORMPackage.LOCK_MODE_TYPE:
				return convertLockModeTypeToString(eDataType, instanceValue);
			case EORMPackage.PARAMETER_MODE:
				return convertParameterModeToString(eDataType, instanceValue);
			case EORMPackage.TEMPORAL_TYPE:
				return convertTemporalTypeToString(eDataType, instanceValue);
			case EORMPackage.ACCESS_TYPE_OBJECT:
				return convertAccessTypeObjectToString(eDataType, instanceValue);
			case EORMPackage.CONSTRAINT_MODE_OBJECT:
				return convertConstraintModeObjectToString(eDataType, instanceValue);
			case EORMPackage.DISCRIMINATOR_TYPE_OBJECT:
				return convertDiscriminatorTypeObjectToString(eDataType, instanceValue);
			case EORMPackage.DISCRIMINATOR_VALUE:
				return convertDiscriminatorValueToString(eDataType, instanceValue);
			case EORMPackage.ENUMERATED:
				return convertEnumeratedToString(eDataType, instanceValue);
			case EORMPackage.ENUM_TYPE_OBJECT:
				return convertEnumTypeObjectToString(eDataType, instanceValue);
			case EORMPackage.FETCH_TYPE_OBJECT:
				return convertFetchTypeObjectToString(eDataType, instanceValue);
			case EORMPackage.GENERATION_TYPE_OBJECT:
				return convertGenerationTypeObjectToString(eDataType, instanceValue);
			case EORMPackage.INHERITANCE_TYPE_OBJECT:
				return convertInheritanceTypeObjectToString(eDataType, instanceValue);
			case EORMPackage.LOCK_MODE_TYPE_OBJECT:
				return convertLockModeTypeObjectToString(eDataType, instanceValue);
			case EORMPackage.ORDER_BY:
				return convertOrderByToString(eDataType, instanceValue);
			case EORMPackage.PARAMETER_MODE_OBJECT:
				return convertParameterModeObjectToString(eDataType, instanceValue);
			case EORMPackage.TEMPORAL:
				return convertTemporalToString(eDataType, instanceValue);
			case EORMPackage.TEMPORAL_TYPE_OBJECT:
				return convertTemporalTypeObjectToString(eDataType, instanceValue);
			case EORMPackage.VERSION_TYPE:
				return convertVersionTypeToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AssociationOverride createAssociationOverride() {
		AssociationOverrideImpl associationOverride = new AssociationOverrideImpl();
		return associationOverride;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AttributeOverride createAttributeOverride() {
		AttributeOverrideImpl attributeOverride = new AttributeOverrideImpl();
		return attributeOverride;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Attributes createAttributes() {
		AttributesImpl attributes = new AttributesImpl();
		return attributes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Basic createBasic() {
		BasicImpl basic = new BasicImpl();
		return basic;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public CascadeType createCascadeType() {
		CascadeTypeImpl cascadeType = new CascadeTypeImpl();
		return cascadeType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public CollectionTable createCollectionTable() {
		CollectionTableImpl collectionTable = new CollectionTableImpl();
		return collectionTable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Column createColumn() {
		ColumnImpl column = new ColumnImpl();
		return column;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ColumnResult createColumnResult() {
		ColumnResultImpl columnResult = new ColumnResultImpl();
		return columnResult;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ConstructorResult createConstructorResult() {
		ConstructorResultImpl constructorResult = new ConstructorResultImpl();
		return constructorResult;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Convert createConvert() {
		ConvertImpl convert = new ConvertImpl();
		return convert;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Converter createConverter() {
		ConverterImpl converter = new ConverterImpl();
		return converter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DiscriminatorColumn createDiscriminatorColumn() {
		DiscriminatorColumnImpl discriminatorColumn = new DiscriminatorColumnImpl();
		return discriminatorColumn;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DocumentRoot createDocumentRoot() {
		DocumentRootImpl documentRoot = new DocumentRootImpl();
		return documentRoot;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ElementCollection createElementCollection() {
		ElementCollectionImpl elementCollection = new ElementCollectionImpl();
		return elementCollection;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Embeddable createEmbeddable() {
		EmbeddableImpl embeddable = new EmbeddableImpl();
		return embeddable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EmbeddableAttributes createEmbeddableAttributes() {
		EmbeddableAttributesImpl embeddableAttributes = new EmbeddableAttributesImpl();
		return embeddableAttributes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Embedded createEmbedded() {
		EmbeddedImpl embedded = new EmbeddedImpl();
		return embedded;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EmbeddedId createEmbeddedId() {
		EmbeddedIdImpl embeddedId = new EmbeddedIdImpl();
		return embeddedId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EmptyType createEmptyType() {
		EmptyTypeImpl emptyType = new EmptyTypeImpl();
		return emptyType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Entity createEntity() {
		EntityImpl entity = new EntityImpl();
		return entity;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EntityListener createEntityListener() {
		EntityListenerImpl entityListener = new EntityListenerImpl();
		return entityListener;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EntityListeners createEntityListeners() {
		EntityListenersImpl entityListeners = new EntityListenersImpl();
		return entityListeners;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EntityMappings createEntityMappings() {
		EntityMappingsImpl entityMappings = new EntityMappingsImpl();
		return entityMappings;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EntityResult createEntityResult() {
		EntityResultImpl entityResult = new EntityResultImpl();
		return entityResult;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public FieldResult createFieldResult() {
		FieldResultImpl fieldResult = new FieldResultImpl();
		return fieldResult;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ForeignKey createForeignKey() {
		ForeignKeyImpl foreignKey = new ForeignKeyImpl();
		return foreignKey;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public GeneratedValue createGeneratedValue() {
		GeneratedValueImpl generatedValue = new GeneratedValueImpl();
		return generatedValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ENamedBase createENamedBase() {
		ENamedBaseImpl eNamedBase = new ENamedBaseImpl();
		return eNamedBase;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Base createBase() {
		BaseImpl base = new BaseImpl();
		return base;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public BaseRef createBaseRef() {
		BaseRefImpl baseRef = new BaseRefImpl();
		return baseRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Id createId() {
		IdImpl id = new IdImpl();
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public IdClass createIdClass() {
		IdClassImpl idClass = new IdClassImpl();
		return idClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Index createIndex() {
		IndexImpl index = new IndexImpl();
		return index;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Inheritance createInheritance() {
		InheritanceImpl inheritance = new InheritanceImpl();
		return inheritance;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public JoinColumn createJoinColumn() {
		JoinColumnImpl joinColumn = new JoinColumnImpl();
		return joinColumn;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public JoinTable createJoinTable() {
		JoinTableImpl joinTable = new JoinTableImpl();
		return joinTable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Lob createLob() {
		LobImpl lob = new LobImpl();
		return lob;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ManyToMany createManyToMany() {
		ManyToManyImpl manyToMany = new ManyToManyImpl();
		return manyToMany;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ManyToOne createManyToOne() {
		ManyToOneImpl manyToOne = new ManyToOneImpl();
		return manyToOne;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MapKey createMapKey() {
		MapKeyImpl mapKey = new MapKeyImpl();
		return mapKey;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MapKeyClass createMapKeyClass() {
		MapKeyClassImpl mapKeyClass = new MapKeyClassImpl();
		return mapKeyClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MapKeyColumn createMapKeyColumn() {
		MapKeyColumnImpl mapKeyColumn = new MapKeyColumnImpl();
		return mapKeyColumn;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MapKeyJoinColumn createMapKeyJoinColumn() {
		MapKeyJoinColumnImpl mapKeyJoinColumn = new MapKeyJoinColumnImpl();
		return mapKeyJoinColumn;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MappedSuperclass createMappedSuperclass() {
		MappedSuperclassImpl mappedSuperclass = new MappedSuperclassImpl();
		return mappedSuperclass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NamedAttributeNode createNamedAttributeNode() {
		NamedAttributeNodeImpl namedAttributeNode = new NamedAttributeNodeImpl();
		return namedAttributeNode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NamedEntityGraph createNamedEntityGraph() {
		NamedEntityGraphImpl namedEntityGraph = new NamedEntityGraphImpl();
		return namedEntityGraph;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NamedNativeQuery createNamedNativeQuery() {
		NamedNativeQueryImpl namedNativeQuery = new NamedNativeQueryImpl();
		return namedNativeQuery;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NamedQuery createNamedQuery() {
		NamedQueryImpl namedQuery = new NamedQueryImpl();
		return namedQuery;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NamedStoredProcedureQuery createNamedStoredProcedureQuery() {
		NamedStoredProcedureQueryImpl namedStoredProcedureQuery = new NamedStoredProcedureQueryImpl();
		return namedStoredProcedureQuery;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NamedSubgraph createNamedSubgraph() {
		NamedSubgraphImpl namedSubgraph = new NamedSubgraphImpl();
		return namedSubgraph;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public OneToMany createOneToMany() {
		OneToManyImpl oneToMany = new OneToManyImpl();
		return oneToMany;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public OneToOne createOneToOne() {
		OneToOneImpl oneToOne = new OneToOneImpl();
		return oneToOne;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public OrderColumn createOrderColumn() {
		OrderColumnImpl orderColumn = new OrderColumnImpl();
		return orderColumn;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PersistenceUnitDefaults createPersistenceUnitDefaults() {
		PersistenceUnitDefaultsImpl persistenceUnitDefaults = new PersistenceUnitDefaultsImpl();
		return persistenceUnitDefaults;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PersistenceUnitMetadata createPersistenceUnitMetadata() {
		PersistenceUnitMetadataImpl persistenceUnitMetadata = new PersistenceUnitMetadataImpl();
		return persistenceUnitMetadata;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PostLoad createPostLoad() {
		PostLoadImpl postLoad = new PostLoadImpl();
		return postLoad;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PostPersist createPostPersist() {
		PostPersistImpl postPersist = new PostPersistImpl();
		return postPersist;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PostRemove createPostRemove() {
		PostRemoveImpl postRemove = new PostRemoveImpl();
		return postRemove;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PostUpdate createPostUpdate() {
		PostUpdateImpl postUpdate = new PostUpdateImpl();
		return postUpdate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PrePersist createPrePersist() {
		PrePersistImpl prePersist = new PrePersistImpl();
		return prePersist;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PreRemove createPreRemove() {
		PreRemoveImpl preRemove = new PreRemoveImpl();
		return preRemove;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PreUpdate createPreUpdate() {
		PreUpdateImpl preUpdate = new PreUpdateImpl();
		return preUpdate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PrimaryKeyJoinColumn createPrimaryKeyJoinColumn() {
		PrimaryKeyJoinColumnImpl primaryKeyJoinColumn = new PrimaryKeyJoinColumnImpl();
		return primaryKeyJoinColumn;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public QueryHint createQueryHint() {
		QueryHintImpl queryHint = new QueryHintImpl();
		return queryHint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SecondaryTable createSecondaryTable() {
		SecondaryTableImpl secondaryTable = new SecondaryTableImpl();
		return secondaryTable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SequenceGenerator createSequenceGenerator() {
		SequenceGeneratorImpl sequenceGenerator = new SequenceGeneratorImpl();
		return sequenceGenerator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SimpleBase createSimpleBase() {
		SimpleBaseImpl simpleBase = new SimpleBaseImpl();
		return simpleBase;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SqlResultSetMapping createSqlResultSetMapping() {
		SqlResultSetMappingImpl sqlResultSetMapping = new SqlResultSetMappingImpl();
		return sqlResultSetMapping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public StoredProcedureParameter createStoredProcedureParameter() {
		StoredProcedureParameterImpl storedProcedureParameter = new StoredProcedureParameterImpl();
		return storedProcedureParameter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Table createTable() {
		TableImpl table = new TableImpl();
		return table;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TableGenerator createTableGenerator() {
		TableGeneratorImpl tableGenerator = new TableGeneratorImpl();
		return tableGenerator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Transient createTransient() {
		TransientImpl transient_ = new TransientImpl();
		return transient_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public UniqueConstraint createUniqueConstraint() {
		UniqueConstraintImpl uniqueConstraint = new UniqueConstraintImpl();
		return uniqueConstraint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Version createVersion() {
		VersionImpl version = new VersionImpl();
		return version;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAccessor createEAccessor() {
		EAccessorImpl eAccessor = new EAccessorImpl();
		return eAccessor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EFeatureObject createEFeatureObject() {
		EFeatureObjectImpl eFeatureObject = new EFeatureObjectImpl();
		return eFeatureObject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClassObject createEClassObject() {
		EClassObjectImpl eClassObject = new EClassObjectImpl();
		return eClassObject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EORMElement createEORMElement() {
		EORMElementImpl eormElement = new EORMElementImpl();
		return eormElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AccessType createAccessTypeFromString(EDataType eDataType, String initialValue) {
		AccessType result = AccessType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertAccessTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConstraintMode createConstraintModeFromString(EDataType eDataType, String initialValue) {
		ConstraintMode result = ConstraintMode.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertConstraintModeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DiscriminatorType createDiscriminatorTypeFromString(EDataType eDataType, String initialValue) {
		DiscriminatorType result = DiscriminatorType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDiscriminatorTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EnumType createEnumTypeFromString(EDataType eDataType, String initialValue) {
		EnumType result = EnumType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertEnumTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FetchType createFetchTypeFromString(EDataType eDataType, String initialValue) {
		FetchType result = FetchType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertFetchTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GenerationType createGenerationTypeFromString(EDataType eDataType, String initialValue) {
		GenerationType result = GenerationType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertGenerationTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InheritanceType createInheritanceTypeFromString(EDataType eDataType, String initialValue) {
		InheritanceType result = InheritanceType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertInheritanceTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LockModeType createLockModeTypeFromString(EDataType eDataType, String initialValue) {
		LockModeType result = LockModeType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertLockModeTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ParameterMode createParameterModeFromString(EDataType eDataType, String initialValue) {
		ParameterMode result = ParameterMode.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertParameterModeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TemporalType createTemporalTypeFromString(EDataType eDataType, String initialValue) {
		TemporalType result = TemporalType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTemporalTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AccessType createAccessTypeObjectFromString(EDataType eDataType, String initialValue) {
		return createAccessTypeFromString(EORMPackage.eINSTANCE.getAccessType(), initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertAccessTypeObjectToString(EDataType eDataType, Object instanceValue) {
		return convertAccessTypeToString(EORMPackage.eINSTANCE.getAccessType(), instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConstraintMode createConstraintModeObjectFromString(EDataType eDataType, String initialValue) {
		return createConstraintModeFromString(EORMPackage.eINSTANCE.getConstraintMode(), initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertConstraintModeObjectToString(EDataType eDataType, Object instanceValue) {
		return convertConstraintModeToString(EORMPackage.eINSTANCE.getConstraintMode(), instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DiscriminatorType createDiscriminatorTypeObjectFromString(EDataType eDataType, String initialValue) {
		return createDiscriminatorTypeFromString(EORMPackage.eINSTANCE.getDiscriminatorType(), initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDiscriminatorTypeObjectToString(EDataType eDataType, Object instanceValue) {
		return convertDiscriminatorTypeToString(EORMPackage.eINSTANCE.getDiscriminatorType(), instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String createDiscriminatorValueFromString(EDataType eDataType, String initialValue) {
		return (String)XMLTypeFactory.eINSTANCE.createFromString(XMLTypePackage.Literals.STRING, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDiscriminatorValueToString(EDataType eDataType, Object instanceValue) {
		return XMLTypeFactory.eINSTANCE.convertToString(XMLTypePackage.Literals.STRING, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EnumType createEnumeratedFromString(EDataType eDataType, String initialValue) {
		return createEnumTypeFromString(EORMPackage.eINSTANCE.getEnumType(), initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertEnumeratedToString(EDataType eDataType, Object instanceValue) {
		return convertEnumTypeToString(EORMPackage.eINSTANCE.getEnumType(), instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EnumType createEnumTypeObjectFromString(EDataType eDataType, String initialValue) {
		return createEnumTypeFromString(EORMPackage.eINSTANCE.getEnumType(), initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertEnumTypeObjectToString(EDataType eDataType, Object instanceValue) {
		return convertEnumTypeToString(EORMPackage.eINSTANCE.getEnumType(), instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FetchType createFetchTypeObjectFromString(EDataType eDataType, String initialValue) {
		return createFetchTypeFromString(EORMPackage.eINSTANCE.getFetchType(), initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertFetchTypeObjectToString(EDataType eDataType, Object instanceValue) {
		return convertFetchTypeToString(EORMPackage.eINSTANCE.getFetchType(), instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GenerationType createGenerationTypeObjectFromString(EDataType eDataType, String initialValue) {
		return createGenerationTypeFromString(EORMPackage.eINSTANCE.getGenerationType(), initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertGenerationTypeObjectToString(EDataType eDataType, Object instanceValue) {
		return convertGenerationTypeToString(EORMPackage.eINSTANCE.getGenerationType(), instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InheritanceType createInheritanceTypeObjectFromString(EDataType eDataType, String initialValue) {
		return createInheritanceTypeFromString(EORMPackage.eINSTANCE.getInheritanceType(), initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertInheritanceTypeObjectToString(EDataType eDataType, Object instanceValue) {
		return convertInheritanceTypeToString(EORMPackage.eINSTANCE.getInheritanceType(), instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LockModeType createLockModeTypeObjectFromString(EDataType eDataType, String initialValue) {
		return createLockModeTypeFromString(EORMPackage.eINSTANCE.getLockModeType(), initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertLockModeTypeObjectToString(EDataType eDataType, Object instanceValue) {
		return convertLockModeTypeToString(EORMPackage.eINSTANCE.getLockModeType(), instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String createOrderByFromString(EDataType eDataType, String initialValue) {
		return (String)XMLTypeFactory.eINSTANCE.createFromString(XMLTypePackage.Literals.STRING, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertOrderByToString(EDataType eDataType, Object instanceValue) {
		return XMLTypeFactory.eINSTANCE.convertToString(XMLTypePackage.Literals.STRING, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ParameterMode createParameterModeObjectFromString(EDataType eDataType, String initialValue) {
		return createParameterModeFromString(EORMPackage.eINSTANCE.getParameterMode(), initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertParameterModeObjectToString(EDataType eDataType, Object instanceValue) {
		return convertParameterModeToString(EORMPackage.eINSTANCE.getParameterMode(), instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TemporalType createTemporalFromString(EDataType eDataType, String initialValue) {
		return createTemporalTypeFromString(EORMPackage.eINSTANCE.getTemporalType(), initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTemporalToString(EDataType eDataType, Object instanceValue) {
		return convertTemporalTypeToString(EORMPackage.eINSTANCE.getTemporalType(), instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TemporalType createTemporalTypeObjectFromString(EDataType eDataType, String initialValue) {
		return createTemporalTypeFromString(EORMPackage.eINSTANCE.getTemporalType(), initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTemporalTypeObjectToString(EDataType eDataType, Object instanceValue) {
		return convertTemporalTypeToString(EORMPackage.eINSTANCE.getTemporalType(), instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String createVersionTypeFromString(EDataType eDataType, String initialValue) {
		return (String)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertVersionTypeToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EORMPackage getEORMPackage() {
		return (EORMPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static EORMPackage getPackage() {
		return EORMPackage.eINSTANCE;
	}

} //EORMFactoryImpl
