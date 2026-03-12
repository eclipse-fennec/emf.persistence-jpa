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
package org.eclipse.fennec.persistence.eorm.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.persistence.eorm.AccessType;
import org.eclipse.fennec.persistence.eorm.Converter;
import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.Embeddable;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.eorm.MappedSuperclass;
import org.eclipse.fennec.persistence.eorm.NamedNativeQuery;
import org.eclipse.fennec.persistence.eorm.NamedQuery;
import org.eclipse.fennec.persistence.eorm.NamedStoredProcedureQuery;
import org.eclipse.fennec.persistence.eorm.PersistenceUnitMetadata;
import org.eclipse.fennec.persistence.eorm.SequenceGenerator;
import org.eclipse.fennec.persistence.eorm.SqlResultSetMapping;
import org.eclipse.fennec.persistence.eorm.TableGenerator;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Entity Mappings</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityMappingsImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityMappingsImpl#getPersistenceUnitMetadata <em>Persistence Unit Metadata</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityMappingsImpl#getPackage <em>Package</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityMappingsImpl#getSchema <em>Schema</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityMappingsImpl#getCatalog <em>Catalog</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityMappingsImpl#getAccess <em>Access</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityMappingsImpl#getSequenceGenerator <em>Sequence Generator</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityMappingsImpl#getTableGenerator <em>Table Generator</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityMappingsImpl#getNamedQuery <em>Named Query</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityMappingsImpl#getNamedNativeQuery <em>Named Native Query</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityMappingsImpl#getNamedStoredProcedureQuery <em>Named Stored Procedure Query</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityMappingsImpl#getSqlResultSetMapping <em>Sql Result Set Mapping</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityMappingsImpl#getMappedSuperclass <em>Mapped Superclass</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityMappingsImpl#getEntity <em>Entity</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityMappingsImpl#getEmbeddable <em>Embeddable</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityMappingsImpl#getConverter <em>Converter</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityMappingsImpl#getVersion <em>Version</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityMappingsImpl#getName <em>Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class EntityMappingsImpl extends MinimalEObjectImpl.Container implements EntityMappings {
	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected String description = DESCRIPTION_EDEFAULT;

	/**
	 * The cached value of the '{@link #getPersistenceUnitMetadata() <em>Persistence Unit Metadata</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPersistenceUnitMetadata()
	 * @generated
	 * @ordered
	 */
	protected PersistenceUnitMetadata persistenceUnitMetadata;

	/**
	 * The default value of the '{@link #getPackage() <em>Package</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPackage()
	 * @generated
	 * @ordered
	 */
	protected static final String PACKAGE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPackage() <em>Package</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPackage()
	 * @generated
	 * @ordered
	 */
	protected String package_ = PACKAGE_EDEFAULT;

	/**
	 * The default value of the '{@link #getSchema() <em>Schema</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSchema()
	 * @generated
	 * @ordered
	 */
	protected static final String SCHEMA_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSchema() <em>Schema</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSchema()
	 * @generated
	 * @ordered
	 */
	protected String schema = SCHEMA_EDEFAULT;

	/**
	 * The default value of the '{@link #getCatalog() <em>Catalog</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCatalog()
	 * @generated
	 * @ordered
	 */
	protected static final String CATALOG_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCatalog() <em>Catalog</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCatalog()
	 * @generated
	 * @ordered
	 */
	protected String catalog = CATALOG_EDEFAULT;

	/**
	 * The default value of the '{@link #getAccess() <em>Access</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAccess()
	 * @generated
	 * @ordered
	 */
	protected static final AccessType ACCESS_EDEFAULT = AccessType.PROPERTY;

	/**
	 * The cached value of the '{@link #getAccess() <em>Access</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAccess()
	 * @generated
	 * @ordered
	 */
	protected AccessType access = ACCESS_EDEFAULT;

	/**
	 * This is true if the Access attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean accessESet;

	/**
	 * The cached value of the '{@link #getSequenceGenerator() <em>Sequence Generator</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSequenceGenerator()
	 * @generated
	 * @ordered
	 */
	protected EList<SequenceGenerator> sequenceGenerator;

	/**
	 * The cached value of the '{@link #getTableGenerator() <em>Table Generator</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTableGenerator()
	 * @generated
	 * @ordered
	 */
	protected EList<TableGenerator> tableGenerator;

	/**
	 * The cached value of the '{@link #getNamedQuery() <em>Named Query</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNamedQuery()
	 * @generated
	 * @ordered
	 */
	protected EList<NamedQuery> namedQuery;

	/**
	 * The cached value of the '{@link #getNamedNativeQuery() <em>Named Native Query</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNamedNativeQuery()
	 * @generated
	 * @ordered
	 */
	protected EList<NamedNativeQuery> namedNativeQuery;

	/**
	 * The cached value of the '{@link #getNamedStoredProcedureQuery() <em>Named Stored Procedure Query</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNamedStoredProcedureQuery()
	 * @generated
	 * @ordered
	 */
	protected EList<NamedStoredProcedureQuery> namedStoredProcedureQuery;

	/**
	 * The cached value of the '{@link #getSqlResultSetMapping() <em>Sql Result Set Mapping</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSqlResultSetMapping()
	 * @generated
	 * @ordered
	 */
	protected EList<SqlResultSetMapping> sqlResultSetMapping;

	/**
	 * The cached value of the '{@link #getMappedSuperclass() <em>Mapped Superclass</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMappedSuperclass()
	 * @generated
	 * @ordered
	 */
	protected EList<MappedSuperclass> mappedSuperclass;

	/**
	 * The cached value of the '{@link #getEntity() <em>Entity</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEntity()
	 * @generated
	 * @ordered
	 */
	protected EList<Entity> entity;

	/**
	 * The cached value of the '{@link #getEmbeddable() <em>Embeddable</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEmbeddable()
	 * @generated
	 * @ordered
	 */
	protected EList<Embeddable> embeddable;

	/**
	 * The cached value of the '{@link #getConverter() <em>Converter</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConverter()
	 * @generated
	 * @ordered
	 */
	protected EList<Converter> converter;

	/**
	 * The default value of the '{@link #getVersion() <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVersion()
	 * @generated
	 * @ordered
	 */
	protected static final String VERSION_EDEFAULT = "3.1";

	/**
	 * The cached value of the '{@link #getVersion() <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVersion()
	 * @generated
	 * @ordered
	 */
	protected String version = VERSION_EDEFAULT;

	/**
	 * This is true if the Version attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean versionESet;

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EntityMappingsImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EORMPackage.eINSTANCE.getEntityMappings();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDescription(String newDescription) {
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_MAPPINGS__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PersistenceUnitMetadata getPersistenceUnitMetadata() {
		return persistenceUnitMetadata;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPersistenceUnitMetadata(PersistenceUnitMetadata newPersistenceUnitMetadata, NotificationChain msgs) {
		PersistenceUnitMetadata oldPersistenceUnitMetadata = persistenceUnitMetadata;
		persistenceUnitMetadata = newPersistenceUnitMetadata;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_MAPPINGS__PERSISTENCE_UNIT_METADATA, oldPersistenceUnitMetadata, newPersistenceUnitMetadata);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPersistenceUnitMetadata(PersistenceUnitMetadata newPersistenceUnitMetadata) {
		if (newPersistenceUnitMetadata != persistenceUnitMetadata) {
			NotificationChain msgs = null;
			if (persistenceUnitMetadata != null)
				msgs = ((InternalEObject)persistenceUnitMetadata).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.ENTITY_MAPPINGS__PERSISTENCE_UNIT_METADATA, null, msgs);
			if (newPersistenceUnitMetadata != null)
				msgs = ((InternalEObject)newPersistenceUnitMetadata).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.ENTITY_MAPPINGS__PERSISTENCE_UNIT_METADATA, null, msgs);
			msgs = basicSetPersistenceUnitMetadata(newPersistenceUnitMetadata, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_MAPPINGS__PERSISTENCE_UNIT_METADATA, newPersistenceUnitMetadata, newPersistenceUnitMetadata));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getPackage() {
		return package_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPackage(String newPackage) {
		String oldPackage = package_;
		package_ = newPackage;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_MAPPINGS__PACKAGE, oldPackage, package_));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getSchema() {
		return schema;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSchema(String newSchema) {
		String oldSchema = schema;
		schema = newSchema;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_MAPPINGS__SCHEMA, oldSchema, schema));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getCatalog() {
		return catalog;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setCatalog(String newCatalog) {
		String oldCatalog = catalog;
		catalog = newCatalog;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_MAPPINGS__CATALOG, oldCatalog, catalog));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AccessType getAccess() {
		return access;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAccess(AccessType newAccess) {
		AccessType oldAccess = access;
		access = newAccess == null ? ACCESS_EDEFAULT : newAccess;
		boolean oldAccessESet = accessESet;
		accessESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_MAPPINGS__ACCESS, oldAccess, access, !oldAccessESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetAccess() {
		AccessType oldAccess = access;
		boolean oldAccessESet = accessESet;
		access = ACCESS_EDEFAULT;
		accessESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EORMPackage.ENTITY_MAPPINGS__ACCESS, oldAccess, ACCESS_EDEFAULT, oldAccessESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetAccess() {
		return accessESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<SequenceGenerator> getSequenceGenerator() {
		if (sequenceGenerator == null) {
			sequenceGenerator = new EObjectContainmentEList<SequenceGenerator>(SequenceGenerator.class, this, EORMPackage.ENTITY_MAPPINGS__SEQUENCE_GENERATOR);
		}
		return sequenceGenerator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<TableGenerator> getTableGenerator() {
		if (tableGenerator == null) {
			tableGenerator = new EObjectContainmentEList<TableGenerator>(TableGenerator.class, this, EORMPackage.ENTITY_MAPPINGS__TABLE_GENERATOR);
		}
		return tableGenerator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<NamedQuery> getNamedQuery() {
		if (namedQuery == null) {
			namedQuery = new EObjectContainmentEList<NamedQuery>(NamedQuery.class, this, EORMPackage.ENTITY_MAPPINGS__NAMED_QUERY);
		}
		return namedQuery;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<NamedNativeQuery> getNamedNativeQuery() {
		if (namedNativeQuery == null) {
			namedNativeQuery = new EObjectContainmentEList<NamedNativeQuery>(NamedNativeQuery.class, this, EORMPackage.ENTITY_MAPPINGS__NAMED_NATIVE_QUERY);
		}
		return namedNativeQuery;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<NamedStoredProcedureQuery> getNamedStoredProcedureQuery() {
		if (namedStoredProcedureQuery == null) {
			namedStoredProcedureQuery = new EObjectContainmentEList<NamedStoredProcedureQuery>(NamedStoredProcedureQuery.class, this, EORMPackage.ENTITY_MAPPINGS__NAMED_STORED_PROCEDURE_QUERY);
		}
		return namedStoredProcedureQuery;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<SqlResultSetMapping> getSqlResultSetMapping() {
		if (sqlResultSetMapping == null) {
			sqlResultSetMapping = new EObjectContainmentEList<SqlResultSetMapping>(SqlResultSetMapping.class, this, EORMPackage.ENTITY_MAPPINGS__SQL_RESULT_SET_MAPPING);
		}
		return sqlResultSetMapping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<MappedSuperclass> getMappedSuperclass() {
		if (mappedSuperclass == null) {
			mappedSuperclass = new EObjectContainmentEList<MappedSuperclass>(MappedSuperclass.class, this, EORMPackage.ENTITY_MAPPINGS__MAPPED_SUPERCLASS);
		}
		return mappedSuperclass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Entity> getEntity() {
		if (entity == null) {
			entity = new EObjectContainmentEList<Entity>(Entity.class, this, EORMPackage.ENTITY_MAPPINGS__ENTITY);
		}
		return entity;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Embeddable> getEmbeddable() {
		if (embeddable == null) {
			embeddable = new EObjectContainmentEList<Embeddable>(Embeddable.class, this, EORMPackage.ENTITY_MAPPINGS__EMBEDDABLE);
		}
		return embeddable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Converter> getConverter() {
		if (converter == null) {
			converter = new EObjectContainmentEList<Converter>(Converter.class, this, EORMPackage.ENTITY_MAPPINGS__CONVERTER);
		}
		return converter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getVersion() {
		return version;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setVersion(String newVersion) {
		String oldVersion = version;
		version = newVersion;
		boolean oldVersionESet = versionESet;
		versionESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_MAPPINGS__VERSION, oldVersion, version, !oldVersionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetVersion() {
		String oldVersion = version;
		boolean oldVersionESet = versionESet;
		version = VERSION_EDEFAULT;
		versionESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EORMPackage.ENTITY_MAPPINGS__VERSION, oldVersion, VERSION_EDEFAULT, oldVersionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetVersion() {
		return versionESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_MAPPINGS__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EORMPackage.ENTITY_MAPPINGS__PERSISTENCE_UNIT_METADATA:
				return basicSetPersistenceUnitMetadata(null, msgs);
			case EORMPackage.ENTITY_MAPPINGS__SEQUENCE_GENERATOR:
				return ((InternalEList<?>)getSequenceGenerator()).basicRemove(otherEnd, msgs);
			case EORMPackage.ENTITY_MAPPINGS__TABLE_GENERATOR:
				return ((InternalEList<?>)getTableGenerator()).basicRemove(otherEnd, msgs);
			case EORMPackage.ENTITY_MAPPINGS__NAMED_QUERY:
				return ((InternalEList<?>)getNamedQuery()).basicRemove(otherEnd, msgs);
			case EORMPackage.ENTITY_MAPPINGS__NAMED_NATIVE_QUERY:
				return ((InternalEList<?>)getNamedNativeQuery()).basicRemove(otherEnd, msgs);
			case EORMPackage.ENTITY_MAPPINGS__NAMED_STORED_PROCEDURE_QUERY:
				return ((InternalEList<?>)getNamedStoredProcedureQuery()).basicRemove(otherEnd, msgs);
			case EORMPackage.ENTITY_MAPPINGS__SQL_RESULT_SET_MAPPING:
				return ((InternalEList<?>)getSqlResultSetMapping()).basicRemove(otherEnd, msgs);
			case EORMPackage.ENTITY_MAPPINGS__MAPPED_SUPERCLASS:
				return ((InternalEList<?>)getMappedSuperclass()).basicRemove(otherEnd, msgs);
			case EORMPackage.ENTITY_MAPPINGS__ENTITY:
				return ((InternalEList<?>)getEntity()).basicRemove(otherEnd, msgs);
			case EORMPackage.ENTITY_MAPPINGS__EMBEDDABLE:
				return ((InternalEList<?>)getEmbeddable()).basicRemove(otherEnd, msgs);
			case EORMPackage.ENTITY_MAPPINGS__CONVERTER:
				return ((InternalEList<?>)getConverter()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case EORMPackage.ENTITY_MAPPINGS__DESCRIPTION:
				return getDescription();
			case EORMPackage.ENTITY_MAPPINGS__PERSISTENCE_UNIT_METADATA:
				return getPersistenceUnitMetadata();
			case EORMPackage.ENTITY_MAPPINGS__PACKAGE:
				return getPackage();
			case EORMPackage.ENTITY_MAPPINGS__SCHEMA:
				return getSchema();
			case EORMPackage.ENTITY_MAPPINGS__CATALOG:
				return getCatalog();
			case EORMPackage.ENTITY_MAPPINGS__ACCESS:
				return getAccess();
			case EORMPackage.ENTITY_MAPPINGS__SEQUENCE_GENERATOR:
				return getSequenceGenerator();
			case EORMPackage.ENTITY_MAPPINGS__TABLE_GENERATOR:
				return getTableGenerator();
			case EORMPackage.ENTITY_MAPPINGS__NAMED_QUERY:
				return getNamedQuery();
			case EORMPackage.ENTITY_MAPPINGS__NAMED_NATIVE_QUERY:
				return getNamedNativeQuery();
			case EORMPackage.ENTITY_MAPPINGS__NAMED_STORED_PROCEDURE_QUERY:
				return getNamedStoredProcedureQuery();
			case EORMPackage.ENTITY_MAPPINGS__SQL_RESULT_SET_MAPPING:
				return getSqlResultSetMapping();
			case EORMPackage.ENTITY_MAPPINGS__MAPPED_SUPERCLASS:
				return getMappedSuperclass();
			case EORMPackage.ENTITY_MAPPINGS__ENTITY:
				return getEntity();
			case EORMPackage.ENTITY_MAPPINGS__EMBEDDABLE:
				return getEmbeddable();
			case EORMPackage.ENTITY_MAPPINGS__CONVERTER:
				return getConverter();
			case EORMPackage.ENTITY_MAPPINGS__VERSION:
				return getVersion();
			case EORMPackage.ENTITY_MAPPINGS__NAME:
				return getName();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case EORMPackage.ENTITY_MAPPINGS__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case EORMPackage.ENTITY_MAPPINGS__PERSISTENCE_UNIT_METADATA:
				setPersistenceUnitMetadata((PersistenceUnitMetadata)newValue);
				return;
			case EORMPackage.ENTITY_MAPPINGS__PACKAGE:
				setPackage((String)newValue);
				return;
			case EORMPackage.ENTITY_MAPPINGS__SCHEMA:
				setSchema((String)newValue);
				return;
			case EORMPackage.ENTITY_MAPPINGS__CATALOG:
				setCatalog((String)newValue);
				return;
			case EORMPackage.ENTITY_MAPPINGS__ACCESS:
				setAccess((AccessType)newValue);
				return;
			case EORMPackage.ENTITY_MAPPINGS__SEQUENCE_GENERATOR:
				getSequenceGenerator().clear();
				getSequenceGenerator().addAll((Collection<? extends SequenceGenerator>)newValue);
				return;
			case EORMPackage.ENTITY_MAPPINGS__TABLE_GENERATOR:
				getTableGenerator().clear();
				getTableGenerator().addAll((Collection<? extends TableGenerator>)newValue);
				return;
			case EORMPackage.ENTITY_MAPPINGS__NAMED_QUERY:
				getNamedQuery().clear();
				getNamedQuery().addAll((Collection<? extends NamedQuery>)newValue);
				return;
			case EORMPackage.ENTITY_MAPPINGS__NAMED_NATIVE_QUERY:
				getNamedNativeQuery().clear();
				getNamedNativeQuery().addAll((Collection<? extends NamedNativeQuery>)newValue);
				return;
			case EORMPackage.ENTITY_MAPPINGS__NAMED_STORED_PROCEDURE_QUERY:
				getNamedStoredProcedureQuery().clear();
				getNamedStoredProcedureQuery().addAll((Collection<? extends NamedStoredProcedureQuery>)newValue);
				return;
			case EORMPackage.ENTITY_MAPPINGS__SQL_RESULT_SET_MAPPING:
				getSqlResultSetMapping().clear();
				getSqlResultSetMapping().addAll((Collection<? extends SqlResultSetMapping>)newValue);
				return;
			case EORMPackage.ENTITY_MAPPINGS__MAPPED_SUPERCLASS:
				getMappedSuperclass().clear();
				getMappedSuperclass().addAll((Collection<? extends MappedSuperclass>)newValue);
				return;
			case EORMPackage.ENTITY_MAPPINGS__ENTITY:
				getEntity().clear();
				getEntity().addAll((Collection<? extends Entity>)newValue);
				return;
			case EORMPackage.ENTITY_MAPPINGS__EMBEDDABLE:
				getEmbeddable().clear();
				getEmbeddable().addAll((Collection<? extends Embeddable>)newValue);
				return;
			case EORMPackage.ENTITY_MAPPINGS__CONVERTER:
				getConverter().clear();
				getConverter().addAll((Collection<? extends Converter>)newValue);
				return;
			case EORMPackage.ENTITY_MAPPINGS__VERSION:
				setVersion((String)newValue);
				return;
			case EORMPackage.ENTITY_MAPPINGS__NAME:
				setName((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case EORMPackage.ENTITY_MAPPINGS__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case EORMPackage.ENTITY_MAPPINGS__PERSISTENCE_UNIT_METADATA:
				setPersistenceUnitMetadata((PersistenceUnitMetadata)null);
				return;
			case EORMPackage.ENTITY_MAPPINGS__PACKAGE:
				setPackage(PACKAGE_EDEFAULT);
				return;
			case EORMPackage.ENTITY_MAPPINGS__SCHEMA:
				setSchema(SCHEMA_EDEFAULT);
				return;
			case EORMPackage.ENTITY_MAPPINGS__CATALOG:
				setCatalog(CATALOG_EDEFAULT);
				return;
			case EORMPackage.ENTITY_MAPPINGS__ACCESS:
				unsetAccess();
				return;
			case EORMPackage.ENTITY_MAPPINGS__SEQUENCE_GENERATOR:
				getSequenceGenerator().clear();
				return;
			case EORMPackage.ENTITY_MAPPINGS__TABLE_GENERATOR:
				getTableGenerator().clear();
				return;
			case EORMPackage.ENTITY_MAPPINGS__NAMED_QUERY:
				getNamedQuery().clear();
				return;
			case EORMPackage.ENTITY_MAPPINGS__NAMED_NATIVE_QUERY:
				getNamedNativeQuery().clear();
				return;
			case EORMPackage.ENTITY_MAPPINGS__NAMED_STORED_PROCEDURE_QUERY:
				getNamedStoredProcedureQuery().clear();
				return;
			case EORMPackage.ENTITY_MAPPINGS__SQL_RESULT_SET_MAPPING:
				getSqlResultSetMapping().clear();
				return;
			case EORMPackage.ENTITY_MAPPINGS__MAPPED_SUPERCLASS:
				getMappedSuperclass().clear();
				return;
			case EORMPackage.ENTITY_MAPPINGS__ENTITY:
				getEntity().clear();
				return;
			case EORMPackage.ENTITY_MAPPINGS__EMBEDDABLE:
				getEmbeddable().clear();
				return;
			case EORMPackage.ENTITY_MAPPINGS__CONVERTER:
				getConverter().clear();
				return;
			case EORMPackage.ENTITY_MAPPINGS__VERSION:
				unsetVersion();
				return;
			case EORMPackage.ENTITY_MAPPINGS__NAME:
				setName(NAME_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case EORMPackage.ENTITY_MAPPINGS__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case EORMPackage.ENTITY_MAPPINGS__PERSISTENCE_UNIT_METADATA:
				return persistenceUnitMetadata != null;
			case EORMPackage.ENTITY_MAPPINGS__PACKAGE:
				return PACKAGE_EDEFAULT == null ? package_ != null : !PACKAGE_EDEFAULT.equals(package_);
			case EORMPackage.ENTITY_MAPPINGS__SCHEMA:
				return SCHEMA_EDEFAULT == null ? schema != null : !SCHEMA_EDEFAULT.equals(schema);
			case EORMPackage.ENTITY_MAPPINGS__CATALOG:
				return CATALOG_EDEFAULT == null ? catalog != null : !CATALOG_EDEFAULT.equals(catalog);
			case EORMPackage.ENTITY_MAPPINGS__ACCESS:
				return isSetAccess();
			case EORMPackage.ENTITY_MAPPINGS__SEQUENCE_GENERATOR:
				return sequenceGenerator != null && !sequenceGenerator.isEmpty();
			case EORMPackage.ENTITY_MAPPINGS__TABLE_GENERATOR:
				return tableGenerator != null && !tableGenerator.isEmpty();
			case EORMPackage.ENTITY_MAPPINGS__NAMED_QUERY:
				return namedQuery != null && !namedQuery.isEmpty();
			case EORMPackage.ENTITY_MAPPINGS__NAMED_NATIVE_QUERY:
				return namedNativeQuery != null && !namedNativeQuery.isEmpty();
			case EORMPackage.ENTITY_MAPPINGS__NAMED_STORED_PROCEDURE_QUERY:
				return namedStoredProcedureQuery != null && !namedStoredProcedureQuery.isEmpty();
			case EORMPackage.ENTITY_MAPPINGS__SQL_RESULT_SET_MAPPING:
				return sqlResultSetMapping != null && !sqlResultSetMapping.isEmpty();
			case EORMPackage.ENTITY_MAPPINGS__MAPPED_SUPERCLASS:
				return mappedSuperclass != null && !mappedSuperclass.isEmpty();
			case EORMPackage.ENTITY_MAPPINGS__ENTITY:
				return entity != null && !entity.isEmpty();
			case EORMPackage.ENTITY_MAPPINGS__EMBEDDABLE:
				return embeddable != null && !embeddable.isEmpty();
			case EORMPackage.ENTITY_MAPPINGS__CONVERTER:
				return converter != null && !converter.isEmpty();
			case EORMPackage.ENTITY_MAPPINGS__VERSION:
				return isSetVersion();
			case EORMPackage.ENTITY_MAPPINGS__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (description: ");
		result.append(description);
		result.append(", package: ");
		result.append(package_);
		result.append(", schema: ");
		result.append(schema);
		result.append(", catalog: ");
		result.append(catalog);
		result.append(", access: ");
		if (accessESet) result.append(access); else result.append("<unset>");
		result.append(", version: ");
		if (versionESet) result.append(version); else result.append("<unset>");
		result.append(", name: ");
		result.append(name);
		result.append(')');
		return result.toString();
	}

} //EntityMappingsImpl
