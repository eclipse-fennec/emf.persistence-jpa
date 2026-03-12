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
package org.eclipse.fennec.persistence.eorm.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

import org.eclipse.fennec.persistence.eorm.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage
 * @generated
 */
public class EORMSwitch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static EORMPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EORMSwitch() {
		if (modelPackage == null) {
			modelPackage = EORMPackage.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		return ePackage == modelPackage;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case EORMPackage.ASSOCIATION_OVERRIDE: {
				AssociationOverride associationOverride = (AssociationOverride)theEObject;
				T result = caseAssociationOverride(associationOverride);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.ATTRIBUTE_OVERRIDE: {
				AttributeOverride attributeOverride = (AttributeOverride)theEObject;
				T result = caseAttributeOverride(attributeOverride);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.ATTRIBUTES: {
				Attributes attributes = (Attributes)theEObject;
				T result = caseAttributes(attributes);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.BASIC: {
				Basic basic = (Basic)theEObject;
				T result = caseBasic(basic);
				if (result == null) result = caseSimpleBase(basic);
				if (result == null) result = caseBase(basic);
				if (result == null) result = caseENamedBase(basic);
				if (result == null) result = caseEORMElement(basic);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.CASCADE_TYPE: {
				CascadeType cascadeType = (CascadeType)theEObject;
				T result = caseCascadeType(cascadeType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.COLLECTION_TABLE: {
				CollectionTable collectionTable = (CollectionTable)theEObject;
				T result = caseCollectionTable(collectionTable);
				if (result == null) result = caseTable(collectionTable);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.COLUMN: {
				Column column = (Column)theEObject;
				T result = caseColumn(column);
				if (result == null) result = caseBaseColumn(column);
				if (result == null) result = caseEORMElement(column);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.COLUMN_RESULT: {
				ColumnResult columnResult = (ColumnResult)theEObject;
				T result = caseColumnResult(columnResult);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.CONSTRUCTOR_RESULT: {
				ConstructorResult constructorResult = (ConstructorResult)theEObject;
				T result = caseConstructorResult(constructorResult);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.CONVERT: {
				Convert convert = (Convert)theEObject;
				T result = caseConvert(convert);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.CONVERTER: {
				Converter converter = (Converter)theEObject;
				T result = caseConverter(converter);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.DISCRIMINATOR_COLUMN: {
				DiscriminatorColumn discriminatorColumn = (DiscriminatorColumn)theEObject;
				T result = caseDiscriminatorColumn(discriminatorColumn);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.DOCUMENT_ROOT: {
				DocumentRoot documentRoot = (DocumentRoot)theEObject;
				T result = caseDocumentRoot(documentRoot);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.ELEMENT_COLLECTION: {
				ElementCollection elementCollection = (ElementCollection)theEObject;
				T result = caseElementCollection(elementCollection);
				if (result == null) result = caseSimpleBase(elementCollection);
				if (result == null) result = caseBase(elementCollection);
				if (result == null) result = caseENamedBase(elementCollection);
				if (result == null) result = caseEORMElement(elementCollection);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.EMBEDDABLE: {
				Embeddable embeddable = (Embeddable)theEObject;
				T result = caseEmbeddable(embeddable);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.EMBEDDABLE_ATTRIBUTES: {
				EmbeddableAttributes embeddableAttributes = (EmbeddableAttributes)theEObject;
				T result = caseEmbeddableAttributes(embeddableAttributes);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.EMBEDDED: {
				Embedded embedded = (Embedded)theEObject;
				T result = caseEmbedded(embedded);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.EMBEDDED_ID: {
				EmbeddedId embeddedId = (EmbeddedId)theEObject;
				T result = caseEmbeddedId(embeddedId);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.EMPTY_TYPE: {
				EmptyType emptyType = (EmptyType)theEObject;
				T result = caseEmptyType(emptyType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.ENTITY: {
				Entity entity = (Entity)theEObject;
				T result = caseEntity(entity);
				if (result == null) result = caseEORMElement(entity);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.ENTITY_LISTENER: {
				EntityListener entityListener = (EntityListener)theEObject;
				T result = caseEntityListener(entityListener);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.ENTITY_LISTENERS: {
				EntityListeners entityListeners = (EntityListeners)theEObject;
				T result = caseEntityListeners(entityListeners);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.ENTITY_MAPPINGS: {
				EntityMappings entityMappings = (EntityMappings)theEObject;
				T result = caseEntityMappings(entityMappings);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.ENTITY_RESULT: {
				EntityResult entityResult = (EntityResult)theEObject;
				T result = caseEntityResult(entityResult);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.FIELD_RESULT: {
				FieldResult fieldResult = (FieldResult)theEObject;
				T result = caseFieldResult(fieldResult);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.FOREIGN_KEY: {
				ForeignKey foreignKey = (ForeignKey)theEObject;
				T result = caseForeignKey(foreignKey);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.GENERATED_VALUE: {
				GeneratedValue generatedValue = (GeneratedValue)theEObject;
				T result = caseGeneratedValue(generatedValue);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.ENAMED_BASE: {
				ENamedBase eNamedBase = (ENamedBase)theEObject;
				T result = caseENamedBase(eNamedBase);
				if (result == null) result = caseEORMElement(eNamedBase);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.BASE: {
				Base base = (Base)theEObject;
				T result = caseBase(base);
				if (result == null) result = caseENamedBase(base);
				if (result == null) result = caseEORMElement(base);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.BASE_REF: {
				BaseRef baseRef = (BaseRef)theEObject;
				T result = caseBaseRef(baseRef);
				if (result == null) result = caseENamedBase(baseRef);
				if (result == null) result = caseEORMElement(baseRef);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.ID: {
				Id id = (Id)theEObject;
				T result = caseId(id);
				if (result == null) result = caseBase(id);
				if (result == null) result = caseENamedBase(id);
				if (result == null) result = caseEORMElement(id);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.ID_CLASS: {
				IdClass idClass = (IdClass)theEObject;
				T result = caseIdClass(idClass);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.INDEX: {
				Index index = (Index)theEObject;
				T result = caseIndex(index);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.INHERITANCE: {
				Inheritance inheritance = (Inheritance)theEObject;
				T result = caseInheritance(inheritance);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.JOIN_COLUMN: {
				JoinColumn joinColumn = (JoinColumn)theEObject;
				T result = caseJoinColumn(joinColumn);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.JOIN_TABLE: {
				JoinTable joinTable = (JoinTable)theEObject;
				T result = caseJoinTable(joinTable);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.LOB: {
				Lob lob = (Lob)theEObject;
				T result = caseLob(lob);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.MANY_TO_MANY: {
				ManyToMany manyToMany = (ManyToMany)theEObject;
				T result = caseManyToMany(manyToMany);
				if (result == null) result = caseMappedByRef(manyToMany);
				if (result == null) result = caseBaseRef(manyToMany);
				if (result == null) result = caseENamedBase(manyToMany);
				if (result == null) result = caseEORMElement(manyToMany);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.MANY_TO_ONE: {
				ManyToOne manyToOne = (ManyToOne)theEObject;
				T result = caseManyToOne(manyToOne);
				if (result == null) result = caseBaseRef(manyToOne);
				if (result == null) result = caseENamedBase(manyToOne);
				if (result == null) result = caseEORMElement(manyToOne);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.MAP_KEY: {
				MapKey mapKey = (MapKey)theEObject;
				T result = caseMapKey(mapKey);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.MAP_KEY_CLASS: {
				MapKeyClass mapKeyClass = (MapKeyClass)theEObject;
				T result = caseMapKeyClass(mapKeyClass);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.MAP_KEY_COLUMN: {
				MapKeyColumn mapKeyColumn = (MapKeyColumn)theEObject;
				T result = caseMapKeyColumn(mapKeyColumn);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.MAP_KEY_JOIN_COLUMN: {
				MapKeyJoinColumn mapKeyJoinColumn = (MapKeyJoinColumn)theEObject;
				T result = caseMapKeyJoinColumn(mapKeyJoinColumn);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.MAPPED_SUPERCLASS: {
				MappedSuperclass mappedSuperclass = (MappedSuperclass)theEObject;
				T result = caseMappedSuperclass(mappedSuperclass);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.NAMED_ATTRIBUTE_NODE: {
				NamedAttributeNode namedAttributeNode = (NamedAttributeNode)theEObject;
				T result = caseNamedAttributeNode(namedAttributeNode);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.NAMED_ENTITY_GRAPH: {
				NamedEntityGraph namedEntityGraph = (NamedEntityGraph)theEObject;
				T result = caseNamedEntityGraph(namedEntityGraph);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.NAMED_NATIVE_QUERY: {
				NamedNativeQuery namedNativeQuery = (NamedNativeQuery)theEObject;
				T result = caseNamedNativeQuery(namedNativeQuery);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.NAMED_QUERY: {
				NamedQuery namedQuery = (NamedQuery)theEObject;
				T result = caseNamedQuery(namedQuery);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.NAMED_STORED_PROCEDURE_QUERY: {
				NamedStoredProcedureQuery namedStoredProcedureQuery = (NamedStoredProcedureQuery)theEObject;
				T result = caseNamedStoredProcedureQuery(namedStoredProcedureQuery);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.NAMED_SUBGRAPH: {
				NamedSubgraph namedSubgraph = (NamedSubgraph)theEObject;
				T result = caseNamedSubgraph(namedSubgraph);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.ONE_TO_MANY: {
				OneToMany oneToMany = (OneToMany)theEObject;
				T result = caseOneToMany(oneToMany);
				if (result == null) result = caseMappedByRef(oneToMany);
				if (result == null) result = caseBaseRef(oneToMany);
				if (result == null) result = caseENamedBase(oneToMany);
				if (result == null) result = caseEORMElement(oneToMany);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.ONE_TO_ONE: {
				OneToOne oneToOne = (OneToOne)theEObject;
				T result = caseOneToOne(oneToOne);
				if (result == null) result = caseMappedByRef(oneToOne);
				if (result == null) result = caseBaseRef(oneToOne);
				if (result == null) result = caseENamedBase(oneToOne);
				if (result == null) result = caseEORMElement(oneToOne);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.ORDER_COLUMN: {
				OrderColumn orderColumn = (OrderColumn)theEObject;
				T result = caseOrderColumn(orderColumn);
				if (result == null) result = caseBaseColumn(orderColumn);
				if (result == null) result = caseEORMElement(orderColumn);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.PERSISTENCE_UNIT_DEFAULTS: {
				PersistenceUnitDefaults persistenceUnitDefaults = (PersistenceUnitDefaults)theEObject;
				T result = casePersistenceUnitDefaults(persistenceUnitDefaults);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.PERSISTENCE_UNIT_METADATA: {
				PersistenceUnitMetadata persistenceUnitMetadata = (PersistenceUnitMetadata)theEObject;
				T result = casePersistenceUnitMetadata(persistenceUnitMetadata);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.POST_LOAD: {
				PostLoad postLoad = (PostLoad)theEObject;
				T result = casePostLoad(postLoad);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.POST_PERSIST: {
				PostPersist postPersist = (PostPersist)theEObject;
				T result = casePostPersist(postPersist);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.POST_REMOVE: {
				PostRemove postRemove = (PostRemove)theEObject;
				T result = casePostRemove(postRemove);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.POST_UPDATE: {
				PostUpdate postUpdate = (PostUpdate)theEObject;
				T result = casePostUpdate(postUpdate);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.PRE_PERSIST: {
				PrePersist prePersist = (PrePersist)theEObject;
				T result = casePrePersist(prePersist);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.PRE_REMOVE: {
				PreRemove preRemove = (PreRemove)theEObject;
				T result = casePreRemove(preRemove);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.PRE_UPDATE: {
				PreUpdate preUpdate = (PreUpdate)theEObject;
				T result = casePreUpdate(preUpdate);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.PRIMARY_KEY_JOIN_COLUMN: {
				PrimaryKeyJoinColumn primaryKeyJoinColumn = (PrimaryKeyJoinColumn)theEObject;
				T result = casePrimaryKeyJoinColumn(primaryKeyJoinColumn);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.QUERY_HINT: {
				QueryHint queryHint = (QueryHint)theEObject;
				T result = caseQueryHint(queryHint);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.SECONDARY_TABLE: {
				SecondaryTable secondaryTable = (SecondaryTable)theEObject;
				T result = caseSecondaryTable(secondaryTable);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.SEQUENCE_GENERATOR: {
				SequenceGenerator sequenceGenerator = (SequenceGenerator)theEObject;
				T result = caseSequenceGenerator(sequenceGenerator);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.SIMPLE_BASE: {
				SimpleBase simpleBase = (SimpleBase)theEObject;
				T result = caseSimpleBase(simpleBase);
				if (result == null) result = caseBase(simpleBase);
				if (result == null) result = caseENamedBase(simpleBase);
				if (result == null) result = caseEORMElement(simpleBase);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.SQL_RESULT_SET_MAPPING: {
				SqlResultSetMapping sqlResultSetMapping = (SqlResultSetMapping)theEObject;
				T result = caseSqlResultSetMapping(sqlResultSetMapping);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.STORED_PROCEDURE_PARAMETER: {
				StoredProcedureParameter storedProcedureParameter = (StoredProcedureParameter)theEObject;
				T result = caseStoredProcedureParameter(storedProcedureParameter);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.TABLE: {
				Table table = (Table)theEObject;
				T result = caseTable(table);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.TABLE_GENERATOR: {
				TableGenerator tableGenerator = (TableGenerator)theEObject;
				T result = caseTableGenerator(tableGenerator);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.TRANSIENT: {
				Transient transient_ = (Transient)theEObject;
				T result = caseTransient(transient_);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.UNIQUE_CONSTRAINT: {
				UniqueConstraint uniqueConstraint = (UniqueConstraint)theEObject;
				T result = caseUniqueConstraint(uniqueConstraint);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.VERSION: {
				Version version = (Version)theEObject;
				T result = caseVersion(version);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.MAPPED_BY_REF: {
				MappedByRef mappedByRef = (MappedByRef)theEObject;
				T result = caseMappedByRef(mappedByRef);
				if (result == null) result = caseBaseRef(mappedByRef);
				if (result == null) result = caseENamedBase(mappedByRef);
				if (result == null) result = caseEORMElement(mappedByRef);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.BASE_COLUMN: {
				BaseColumn baseColumn = (BaseColumn)theEObject;
				T result = caseBaseColumn(baseColumn);
				if (result == null) result = caseEORMElement(baseColumn);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.EACCESSOR: {
				EAccessor eAccessor = (EAccessor)theEObject;
				T result = caseEAccessor(eAccessor);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.EFEATURE_OBJECT: {
				EFeatureObject eFeatureObject = (EFeatureObject)theEObject;
				T result = caseEFeatureObject(eFeatureObject);
				if (result == null) result = caseEAccessor(eFeatureObject);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.ECLASS_OBJECT: {
				EClassObject eClassObject = (EClassObject)theEObject;
				T result = caseEClassObject(eClassObject);
				if (result == null) result = caseEAccessor(eClassObject);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EORMPackage.EORM_ELEMENT: {
				EORMElement eormElement = (EORMElement)theEObject;
				T result = caseEORMElement(eormElement);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Association Override</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Association Override</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAssociationOverride(AssociationOverride object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Attribute Override</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Attribute Override</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAttributeOverride(AttributeOverride object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Attributes</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Attributes</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAttributes(Attributes object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Basic</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Basic</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBasic(Basic object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Cascade Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Cascade Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCascadeType(CascadeType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Collection Table</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Collection Table</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCollectionTable(CollectionTable object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Column</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Column</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseColumn(Column object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Column Result</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Column Result</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseColumnResult(ColumnResult object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Constructor Result</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Constructor Result</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseConstructorResult(ConstructorResult object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Convert</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Convert</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseConvert(Convert object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Converter</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Converter</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseConverter(Converter object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Discriminator Column</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Discriminator Column</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDiscriminatorColumn(DiscriminatorColumn object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Document Root</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Document Root</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDocumentRoot(DocumentRoot object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Element Collection</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Element Collection</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseElementCollection(ElementCollection object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Embeddable</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Embeddable</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEmbeddable(Embeddable object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Embeddable Attributes</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Embeddable Attributes</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEmbeddableAttributes(EmbeddableAttributes object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Embedded</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Embedded</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEmbedded(Embedded object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Embedded Id</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Embedded Id</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEmbeddedId(EmbeddedId object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Empty Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Empty Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEmptyType(EmptyType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Entity</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Entity</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEntity(Entity object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Entity Listener</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Entity Listener</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEntityListener(EntityListener object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Entity Listeners</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Entity Listeners</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEntityListeners(EntityListeners object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Entity Mappings</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Entity Mappings</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEntityMappings(EntityMappings object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Entity Result</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Entity Result</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEntityResult(EntityResult object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Field Result</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Field Result</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseFieldResult(FieldResult object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Foreign Key</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Foreign Key</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseForeignKey(ForeignKey object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Generated Value</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Generated Value</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseGeneratedValue(GeneratedValue object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>ENamed Base</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>ENamed Base</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseENamedBase(ENamedBase object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Base</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Base</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBase(Base object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Base Ref</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Base Ref</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBaseRef(BaseRef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Id</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Id</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseId(Id object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Id Class</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Id Class</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIdClass(IdClass object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Index</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Index</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIndex(Index object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Inheritance</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Inheritance</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseInheritance(Inheritance object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Join Column</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Join Column</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseJoinColumn(JoinColumn object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Join Table</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Join Table</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseJoinTable(JoinTable object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Lob</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Lob</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseLob(Lob object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Many To Many</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Many To Many</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseManyToMany(ManyToMany object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Many To One</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Many To One</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseManyToOne(ManyToOne object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Map Key</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Map Key</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMapKey(MapKey object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Map Key Class</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Map Key Class</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMapKeyClass(MapKeyClass object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Map Key Column</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Map Key Column</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMapKeyColumn(MapKeyColumn object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Map Key Join Column</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Map Key Join Column</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMapKeyJoinColumn(MapKeyJoinColumn object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Mapped Superclass</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Mapped Superclass</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMappedSuperclass(MappedSuperclass object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Named Attribute Node</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Named Attribute Node</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNamedAttributeNode(NamedAttributeNode object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Named Entity Graph</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Named Entity Graph</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNamedEntityGraph(NamedEntityGraph object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Named Native Query</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Named Native Query</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNamedNativeQuery(NamedNativeQuery object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Named Query</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Named Query</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNamedQuery(NamedQuery object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Named Stored Procedure Query</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Named Stored Procedure Query</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNamedStoredProcedureQuery(NamedStoredProcedureQuery object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Named Subgraph</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Named Subgraph</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNamedSubgraph(NamedSubgraph object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>One To Many</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>One To Many</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseOneToMany(OneToMany object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>One To One</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>One To One</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseOneToOne(OneToOne object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Order Column</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Order Column</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseOrderColumn(OrderColumn object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Persistence Unit Defaults</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Persistence Unit Defaults</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePersistenceUnitDefaults(PersistenceUnitDefaults object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Persistence Unit Metadata</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Persistence Unit Metadata</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePersistenceUnitMetadata(PersistenceUnitMetadata object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Post Load</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Post Load</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePostLoad(PostLoad object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Post Persist</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Post Persist</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePostPersist(PostPersist object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Post Remove</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Post Remove</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePostRemove(PostRemove object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Post Update</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Post Update</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePostUpdate(PostUpdate object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Pre Persist</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Pre Persist</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePrePersist(PrePersist object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Pre Remove</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Pre Remove</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePreRemove(PreRemove object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Pre Update</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Pre Update</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePreUpdate(PreUpdate object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Primary Key Join Column</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Primary Key Join Column</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePrimaryKeyJoinColumn(PrimaryKeyJoinColumn object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Query Hint</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Query Hint</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseQueryHint(QueryHint object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Secondary Table</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Secondary Table</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSecondaryTable(SecondaryTable object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Sequence Generator</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Sequence Generator</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSequenceGenerator(SequenceGenerator object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Simple Base</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Simple Base</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSimpleBase(SimpleBase object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Sql Result Set Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Sql Result Set Mapping</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSqlResultSetMapping(SqlResultSetMapping object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Stored Procedure Parameter</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Stored Procedure Parameter</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseStoredProcedureParameter(StoredProcedureParameter object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Table</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Table</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTable(Table object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Table Generator</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Table Generator</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTableGenerator(TableGenerator object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Transient</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Transient</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTransient(Transient object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Unique Constraint</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Unique Constraint</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseUniqueConstraint(UniqueConstraint object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Version</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Version</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseVersion(Version object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Mapped By Ref</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Mapped By Ref</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMappedByRef(MappedByRef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Base Column</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Base Column</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBaseColumn(BaseColumn object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EAccessor</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EAccessor</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEAccessor(EAccessor object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EFeature Object</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EFeature Object</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEFeatureObject(EFeatureObject object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EClass Object</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EClass Object</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEClassObject(EClassObject object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEORMElement(EORMElement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T defaultCase(EObject object) {
		return null;
	}

} //EORMSwitch
