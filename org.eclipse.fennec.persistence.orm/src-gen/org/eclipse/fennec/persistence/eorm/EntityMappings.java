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
 * A representation of the model object '<em><b>Entity Mappings</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 
 *         The entity-mappings element is the root element of a mapping
 *         file. It contains the following four types of elements:
 * 
 *         1. The persistence-unit-metadata element contains metadata
 *         for the entire persistence unit. It is undefined if this element
 *         occurs in multiple mapping files within the same persistence unit.
 *         
 *         2. The package, schema, catalog and access elements apply to all of
 *         the entity, mapped-superclass and embeddable elements defined in
 *         the same file in which they occur.
 * 
 *         3. The sequence-generator, table-generator, converter, named-query,
 *         named-native-query, named-stored-procedure-query, and 
 *         sql-result-set-mapping elements are global to the persistence
 *         unit. It is undefined to have more than one sequence-generator
 *         or table-generator of the same name in the same or different
 *         mapping files in a persistence unit. It is undefined to have
 *         more than one named-query, named-native-query, sql-result-set-mapping,
 *         or named-stored-procedure-query of the same name in the same 
 *         or different mapping files in a persistence unit.  It is also
 *         undefined to have more than one converter for the same target
 *         type in the same or different mapping files in a persistence unit.
 * 
 *         4. The entity, mapped-superclass and embeddable elements each define
 *         the mapping information for a managed persistent class. The mapping
 *         information contained in these elements may be complete or it may
 *         be partial.
 * 
 *         
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getPersistenceUnitMetadata <em>Persistence Unit Metadata</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getPackage <em>Package</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getSchema <em>Schema</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getCatalog <em>Catalog</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getAccess <em>Access</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getSequenceGenerator <em>Sequence Generator</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getTableGenerator <em>Table Generator</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getNamedQuery <em>Named Query</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getNamedNativeQuery <em>Named Native Query</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getNamedStoredProcedureQuery <em>Named Stored Procedure Query</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getSqlResultSetMapping <em>Sql Result Set Mapping</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getMappedSuperclass <em>Mapped Superclass</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getEntity <em>Entity</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getEmbeddable <em>Embeddable</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getConverter <em>Converter</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getVersion <em>Version</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getName <em>Name</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityMappings()
 * @model extendedMetaData="name='entity-mappings_._type' kind='elementOnly'"
 * @generated
 */
@ProviderType
public interface EntityMappings extends EObject {
	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityMappings_Description()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='description' namespace='##targetNamespace'"
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Persistence Unit Metadata</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Persistence Unit Metadata</em>' containment reference.
	 * @see #setPersistenceUnitMetadata(PersistenceUnitMetadata)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityMappings_PersistenceUnitMetadata()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='persistence-unit-metadata' namespace='##targetNamespace'"
	 * @generated
	 */
	PersistenceUnitMetadata getPersistenceUnitMetadata();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getPersistenceUnitMetadata <em>Persistence Unit Metadata</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Persistence Unit Metadata</em>' containment reference.
	 * @see #getPersistenceUnitMetadata()
	 * @generated
	 */
	void setPersistenceUnitMetadata(PersistenceUnitMetadata value);

	/**
	 * Returns the value of the '<em><b>Package</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Package</em>' attribute.
	 * @see #setPackage(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityMappings_Package()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='package' namespace='##targetNamespace'"
	 * @generated
	 */
	String getPackage();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getPackage <em>Package</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Package</em>' attribute.
	 * @see #getPackage()
	 * @generated
	 */
	void setPackage(String value);

	/**
	 * Returns the value of the '<em><b>Schema</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Schema</em>' attribute.
	 * @see #setSchema(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityMappings_Schema()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='schema' namespace='##targetNamespace'"
	 * @generated
	 */
	String getSchema();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getSchema <em>Schema</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Schema</em>' attribute.
	 * @see #getSchema()
	 * @generated
	 */
	void setSchema(String value);

	/**
	 * Returns the value of the '<em><b>Catalog</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Catalog</em>' attribute.
	 * @see #setCatalog(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityMappings_Catalog()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='catalog' namespace='##targetNamespace'"
	 * @generated
	 */
	String getCatalog();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getCatalog <em>Catalog</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Catalog</em>' attribute.
	 * @see #getCatalog()
	 * @generated
	 */
	void setCatalog(String value);

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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityMappings_Access()
	 * @model unsettable="true"
	 *        extendedMetaData="kind='element' name='access' namespace='##targetNamespace'"
	 * @generated
	 */
	AccessType getAccess();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getAccess <em>Access</em>}' attribute.
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
	 * Unsets the value of the '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getAccess <em>Access</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetAccess()
	 * @see #getAccess()
	 * @see #setAccess(AccessType)
	 * @generated
	 */
	void unsetAccess();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getAccess <em>Access</em>}' attribute is set.
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
	 * Returns the value of the '<em><b>Sequence Generator</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.SequenceGenerator}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sequence Generator</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityMappings_SequenceGenerator()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='sequence-generator' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<SequenceGenerator> getSequenceGenerator();

	/**
	 * Returns the value of the '<em><b>Table Generator</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.TableGenerator}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Table Generator</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityMappings_TableGenerator()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='table-generator' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<TableGenerator> getTableGenerator();

	/**
	 * Returns the value of the '<em><b>Named Query</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.NamedQuery}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Named Query</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityMappings_NamedQuery()
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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityMappings_NamedNativeQuery()
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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityMappings_NamedStoredProcedureQuery()
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
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityMappings_SqlResultSetMapping()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='sql-result-set-mapping' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<SqlResultSetMapping> getSqlResultSetMapping();

	/**
	 * Returns the value of the '<em><b>Mapped Superclass</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.MappedSuperclass}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mapped Superclass</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityMappings_MappedSuperclass()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='mapped-superclass' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<MappedSuperclass> getMappedSuperclass();

	/**
	 * Returns the value of the '<em><b>Entity</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.Entity}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Entity</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityMappings_Entity()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='entity' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<Entity> getEntity();

	/**
	 * Returns the value of the '<em><b>Embeddable</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.Embeddable}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Embeddable</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityMappings_Embeddable()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='embeddable' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<Embeddable> getEmbeddable();

	/**
	 * Returns the value of the '<em><b>Converter</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.persistence.eorm.Converter}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Converter</em>' containment reference list.
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityMappings_Converter()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='converter' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<Converter> getConverter();

	/**
	 * Returns the value of the '<em><b>Version</b></em>' attribute.
	 * The default value is <code>"3.1"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Version</em>' attribute.
	 * @see #isSetVersion()
	 * @see #unsetVersion()
	 * @see #setVersion(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityMappings_Version()
	 * @model default="3.1" unsettable="true" dataType="org.eclipse.fennec.persistence.eorm.VersionType" required="true"
	 *        extendedMetaData="kind='attribute' name='version'"
	 * @generated
	 */
	String getVersion();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getVersion <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Version</em>' attribute.
	 * @see #isSetVersion()
	 * @see #unsetVersion()
	 * @see #getVersion()
	 * @generated
	 */
	void setVersion(String value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getVersion <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetVersion()
	 * @see #getVersion()
	 * @see #setVersion(String)
	 * @generated
	 */
	void unsetVersion();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getVersion <em>Version</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Version</em>' attribute is set.
	 * @see #unsetVersion()
	 * @see #getVersion()
	 * @see #setVersion(String)
	 * @generated
	 */
	boolean isSetVersion();

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getEntityMappings_Name()
	 * @model id="true" required="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.persistence.eorm.EntityMappings#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

} // EntityMappings
