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
package org.eclipse.fennec.persistence.epersistence.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.persistence.eorm.EntityMappings;

import org.eclipse.fennec.persistence.epersistence.EPersistencePackage;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnitCachingType;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnitTransactionType;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnitValidationMode;
import org.eclipse.fennec.persistence.epersistence.Properties;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Persistence Unit</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.epersistence.impl.PersistenceUnitImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.epersistence.impl.PersistenceUnitImpl#getProvider <em>Provider</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.epersistence.impl.PersistenceUnitImpl#getQualifier <em>Qualifier</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.epersistence.impl.PersistenceUnitImpl#getScope <em>Scope</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.epersistence.impl.PersistenceUnitImpl#getJtaDataSource <em>Jta Data Source</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.epersistence.impl.PersistenceUnitImpl#getNonJtaDataSource <em>Non Jta Data Source</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.epersistence.impl.PersistenceUnitImpl#getMappingFile <em>Mapping File</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.epersistence.impl.PersistenceUnitImpl#getJarFile <em>Jar File</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.epersistence.impl.PersistenceUnitImpl#getEntityClass <em>Entity Class</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.epersistence.impl.PersistenceUnitImpl#isExcludeUnlistedClasses <em>Exclude Unlisted Classes</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.epersistence.impl.PersistenceUnitImpl#getSharedCacheMode <em>Shared Cache Mode</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.epersistence.impl.PersistenceUnitImpl#getValidationMode <em>Validation Mode</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.epersistence.impl.PersistenceUnitImpl#getProperties <em>Properties</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.epersistence.impl.PersistenceUnitImpl#getAny <em>Any</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.epersistence.impl.PersistenceUnitImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.epersistence.impl.PersistenceUnitImpl#getTransactionType <em>Transaction Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.epersistence.impl.PersistenceUnitImpl#getEntityMappings <em>Entity Mappings</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PersistenceUnitImpl extends MinimalEObjectImpl.Container implements PersistenceUnit {
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
	 * The default value of the '{@link #getProvider() <em>Provider</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProvider()
	 * @generated
	 * @ordered
	 */
	protected static final String PROVIDER_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getProvider() <em>Provider</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProvider()
	 * @generated
	 * @ordered
	 */
	protected String provider = PROVIDER_EDEFAULT;

	/**
	 * The cached value of the '{@link #getQualifier() <em>Qualifier</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getQualifier()
	 * @generated
	 * @ordered
	 */
	protected EList<String> qualifier;

	/**
	 * The default value of the '{@link #getScope() <em>Scope</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScope()
	 * @generated
	 * @ordered
	 */
	protected static final String SCOPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getScope() <em>Scope</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScope()
	 * @generated
	 * @ordered
	 */
	protected String scope = SCOPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getJtaDataSource() <em>Jta Data Source</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getJtaDataSource()
	 * @generated
	 * @ordered
	 */
	protected static final String JTA_DATA_SOURCE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getJtaDataSource() <em>Jta Data Source</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getJtaDataSource()
	 * @generated
	 * @ordered
	 */
	protected String jtaDataSource = JTA_DATA_SOURCE_EDEFAULT;

	/**
	 * The default value of the '{@link #getNonJtaDataSource() <em>Non Jta Data Source</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNonJtaDataSource()
	 * @generated
	 * @ordered
	 */
	protected static final String NON_JTA_DATA_SOURCE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getNonJtaDataSource() <em>Non Jta Data Source</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNonJtaDataSource()
	 * @generated
	 * @ordered
	 */
	protected String nonJtaDataSource = NON_JTA_DATA_SOURCE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getMappingFile() <em>Mapping File</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMappingFile()
	 * @generated
	 * @ordered
	 */
	protected EList<String> mappingFile;

	/**
	 * The cached value of the '{@link #getJarFile() <em>Jar File</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getJarFile()
	 * @generated
	 * @ordered
	 */
	protected EList<String> jarFile;

	/**
	 * The cached value of the '{@link #getEntityClass() <em>Entity Class</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEntityClass()
	 * @generated
	 * @ordered
	 */
	protected EList<String> entityClass;

	/**
	 * The default value of the '{@link #isExcludeUnlistedClasses() <em>Exclude Unlisted Classes</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isExcludeUnlistedClasses()
	 * @generated
	 * @ordered
	 */
	protected static final boolean EXCLUDE_UNLISTED_CLASSES_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isExcludeUnlistedClasses() <em>Exclude Unlisted Classes</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isExcludeUnlistedClasses()
	 * @generated
	 * @ordered
	 */
	protected boolean excludeUnlistedClasses = EXCLUDE_UNLISTED_CLASSES_EDEFAULT;

	/**
	 * This is true if the Exclude Unlisted Classes attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean excludeUnlistedClassesESet;

	/**
	 * The default value of the '{@link #getSharedCacheMode() <em>Shared Cache Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSharedCacheMode()
	 * @generated
	 * @ordered
	 */
	protected static final PersistenceUnitCachingType SHARED_CACHE_MODE_EDEFAULT = PersistenceUnitCachingType.ALL;

	/**
	 * The cached value of the '{@link #getSharedCacheMode() <em>Shared Cache Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSharedCacheMode()
	 * @generated
	 * @ordered
	 */
	protected PersistenceUnitCachingType sharedCacheMode = SHARED_CACHE_MODE_EDEFAULT;

	/**
	 * This is true if the Shared Cache Mode attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean sharedCacheModeESet;

	/**
	 * The default value of the '{@link #getValidationMode() <em>Validation Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValidationMode()
	 * @generated
	 * @ordered
	 */
	protected static final PersistenceUnitValidationMode VALIDATION_MODE_EDEFAULT = PersistenceUnitValidationMode.AUTO;

	/**
	 * The cached value of the '{@link #getValidationMode() <em>Validation Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValidationMode()
	 * @generated
	 * @ordered
	 */
	protected PersistenceUnitValidationMode validationMode = VALIDATION_MODE_EDEFAULT;

	/**
	 * This is true if the Validation Mode attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean validationModeESet;

	/**
	 * The cached value of the '{@link #getProperties() <em>Properties</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProperties()
	 * @generated
	 * @ordered
	 */
	protected Properties properties;

	/**
	 * The cached value of the '{@link #getAny() <em>Any</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAny()
	 * @generated
	 * @ordered
	 */
	protected FeatureMap any;

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
	 * The default value of the '{@link #getTransactionType() <em>Transaction Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransactionType()
	 * @generated
	 * @ordered
	 */
	protected static final PersistenceUnitTransactionType TRANSACTION_TYPE_EDEFAULT = PersistenceUnitTransactionType.JTA;

	/**
	 * The cached value of the '{@link #getTransactionType() <em>Transaction Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransactionType()
	 * @generated
	 * @ordered
	 */
	protected PersistenceUnitTransactionType transactionType = TRANSACTION_TYPE_EDEFAULT;

	/**
	 * This is true if the Transaction Type attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean transactionTypeESet;

	/**
	 * The cached value of the '{@link #getEntityMappings() <em>Entity Mappings</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEntityMappings()
	 * @generated
	 * @ordered
	 */
	protected EList<EntityMappings> entityMappings;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PersistenceUnitImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EPersistencePackage.Literals.PERSISTENCE_UNIT;
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
			eNotify(new ENotificationImpl(this, Notification.SET, EPersistencePackage.PERSISTENCE_UNIT__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getProvider() {
		return provider;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setProvider(String newProvider) {
		String oldProvider = provider;
		provider = newProvider;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EPersistencePackage.PERSISTENCE_UNIT__PROVIDER, oldProvider, provider));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<String> getQualifier() {
		if (qualifier == null) {
			qualifier = new EDataTypeEList<String>(String.class, this, EPersistencePackage.PERSISTENCE_UNIT__QUALIFIER);
		}
		return qualifier;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getScope() {
		return scope;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setScope(String newScope) {
		String oldScope = scope;
		scope = newScope;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EPersistencePackage.PERSISTENCE_UNIT__SCOPE, oldScope, scope));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getJtaDataSource() {
		return jtaDataSource;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setJtaDataSource(String newJtaDataSource) {
		String oldJtaDataSource = jtaDataSource;
		jtaDataSource = newJtaDataSource;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EPersistencePackage.PERSISTENCE_UNIT__JTA_DATA_SOURCE, oldJtaDataSource, jtaDataSource));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getNonJtaDataSource() {
		return nonJtaDataSource;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setNonJtaDataSource(String newNonJtaDataSource) {
		String oldNonJtaDataSource = nonJtaDataSource;
		nonJtaDataSource = newNonJtaDataSource;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EPersistencePackage.PERSISTENCE_UNIT__NON_JTA_DATA_SOURCE, oldNonJtaDataSource, nonJtaDataSource));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<String> getMappingFile() {
		if (mappingFile == null) {
			mappingFile = new EDataTypeEList<String>(String.class, this, EPersistencePackage.PERSISTENCE_UNIT__MAPPING_FILE);
		}
		return mappingFile;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<String> getJarFile() {
		if (jarFile == null) {
			jarFile = new EDataTypeEList<String>(String.class, this, EPersistencePackage.PERSISTENCE_UNIT__JAR_FILE);
		}
		return jarFile;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<String> getEntityClass() {
		if (entityClass == null) {
			entityClass = new EDataTypeEList<String>(String.class, this, EPersistencePackage.PERSISTENCE_UNIT__ENTITY_CLASS);
		}
		return entityClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isExcludeUnlistedClasses() {
		return excludeUnlistedClasses;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setExcludeUnlistedClasses(boolean newExcludeUnlistedClasses) {
		boolean oldExcludeUnlistedClasses = excludeUnlistedClasses;
		excludeUnlistedClasses = newExcludeUnlistedClasses;
		boolean oldExcludeUnlistedClassesESet = excludeUnlistedClassesESet;
		excludeUnlistedClassesESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EPersistencePackage.PERSISTENCE_UNIT__EXCLUDE_UNLISTED_CLASSES, oldExcludeUnlistedClasses, excludeUnlistedClasses, !oldExcludeUnlistedClassesESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetExcludeUnlistedClasses() {
		boolean oldExcludeUnlistedClasses = excludeUnlistedClasses;
		boolean oldExcludeUnlistedClassesESet = excludeUnlistedClassesESet;
		excludeUnlistedClasses = EXCLUDE_UNLISTED_CLASSES_EDEFAULT;
		excludeUnlistedClassesESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EPersistencePackage.PERSISTENCE_UNIT__EXCLUDE_UNLISTED_CLASSES, oldExcludeUnlistedClasses, EXCLUDE_UNLISTED_CLASSES_EDEFAULT, oldExcludeUnlistedClassesESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetExcludeUnlistedClasses() {
		return excludeUnlistedClassesESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PersistenceUnitCachingType getSharedCacheMode() {
		return sharedCacheMode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSharedCacheMode(PersistenceUnitCachingType newSharedCacheMode) {
		PersistenceUnitCachingType oldSharedCacheMode = sharedCacheMode;
		sharedCacheMode = newSharedCacheMode == null ? SHARED_CACHE_MODE_EDEFAULT : newSharedCacheMode;
		boolean oldSharedCacheModeESet = sharedCacheModeESet;
		sharedCacheModeESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EPersistencePackage.PERSISTENCE_UNIT__SHARED_CACHE_MODE, oldSharedCacheMode, sharedCacheMode, !oldSharedCacheModeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetSharedCacheMode() {
		PersistenceUnitCachingType oldSharedCacheMode = sharedCacheMode;
		boolean oldSharedCacheModeESet = sharedCacheModeESet;
		sharedCacheMode = SHARED_CACHE_MODE_EDEFAULT;
		sharedCacheModeESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EPersistencePackage.PERSISTENCE_UNIT__SHARED_CACHE_MODE, oldSharedCacheMode, SHARED_CACHE_MODE_EDEFAULT, oldSharedCacheModeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetSharedCacheMode() {
		return sharedCacheModeESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PersistenceUnitValidationMode getValidationMode() {
		return validationMode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setValidationMode(PersistenceUnitValidationMode newValidationMode) {
		PersistenceUnitValidationMode oldValidationMode = validationMode;
		validationMode = newValidationMode == null ? VALIDATION_MODE_EDEFAULT : newValidationMode;
		boolean oldValidationModeESet = validationModeESet;
		validationModeESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EPersistencePackage.PERSISTENCE_UNIT__VALIDATION_MODE, oldValidationMode, validationMode, !oldValidationModeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetValidationMode() {
		PersistenceUnitValidationMode oldValidationMode = validationMode;
		boolean oldValidationModeESet = validationModeESet;
		validationMode = VALIDATION_MODE_EDEFAULT;
		validationModeESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EPersistencePackage.PERSISTENCE_UNIT__VALIDATION_MODE, oldValidationMode, VALIDATION_MODE_EDEFAULT, oldValidationModeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetValidationMode() {
		return validationModeESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Properties getProperties() {
		return properties;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProperties(Properties newProperties, NotificationChain msgs) {
		Properties oldProperties = properties;
		properties = newProperties;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EPersistencePackage.PERSISTENCE_UNIT__PROPERTIES, oldProperties, newProperties);
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
	public void setProperties(Properties newProperties) {
		if (newProperties != properties) {
			NotificationChain msgs = null;
			if (properties != null)
				msgs = ((InternalEObject)properties).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EPersistencePackage.PERSISTENCE_UNIT__PROPERTIES, null, msgs);
			if (newProperties != null)
				msgs = ((InternalEObject)newProperties).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EPersistencePackage.PERSISTENCE_UNIT__PROPERTIES, null, msgs);
			msgs = basicSetProperties(newProperties, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EPersistencePackage.PERSISTENCE_UNIT__PROPERTIES, newProperties, newProperties));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public FeatureMap getAny() {
		if (any == null) {
			any = new BasicFeatureMap(this, EPersistencePackage.PERSISTENCE_UNIT__ANY);
		}
		return any;
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
			eNotify(new ENotificationImpl(this, Notification.SET, EPersistencePackage.PERSISTENCE_UNIT__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PersistenceUnitTransactionType getTransactionType() {
		return transactionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTransactionType(PersistenceUnitTransactionType newTransactionType) {
		PersistenceUnitTransactionType oldTransactionType = transactionType;
		transactionType = newTransactionType == null ? TRANSACTION_TYPE_EDEFAULT : newTransactionType;
		boolean oldTransactionTypeESet = transactionTypeESet;
		transactionTypeESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EPersistencePackage.PERSISTENCE_UNIT__TRANSACTION_TYPE, oldTransactionType, transactionType, !oldTransactionTypeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetTransactionType() {
		PersistenceUnitTransactionType oldTransactionType = transactionType;
		boolean oldTransactionTypeESet = transactionTypeESet;
		transactionType = TRANSACTION_TYPE_EDEFAULT;
		transactionTypeESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EPersistencePackage.PERSISTENCE_UNIT__TRANSACTION_TYPE, oldTransactionType, TRANSACTION_TYPE_EDEFAULT, oldTransactionTypeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetTransactionType() {
		return transactionTypeESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<EntityMappings> getEntityMappings() {
		if (entityMappings == null) {
			entityMappings = new EObjectResolvingEList<EntityMappings>(EntityMappings.class, this, EPersistencePackage.PERSISTENCE_UNIT__ENTITY_MAPPINGS);
		}
		return entityMappings;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EPersistencePackage.PERSISTENCE_UNIT__PROPERTIES:
				return basicSetProperties(null, msgs);
			case EPersistencePackage.PERSISTENCE_UNIT__ANY:
				return ((InternalEList<?>)getAny()).basicRemove(otherEnd, msgs);
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
			case EPersistencePackage.PERSISTENCE_UNIT__DESCRIPTION:
				return getDescription();
			case EPersistencePackage.PERSISTENCE_UNIT__PROVIDER:
				return getProvider();
			case EPersistencePackage.PERSISTENCE_UNIT__QUALIFIER:
				return getQualifier();
			case EPersistencePackage.PERSISTENCE_UNIT__SCOPE:
				return getScope();
			case EPersistencePackage.PERSISTENCE_UNIT__JTA_DATA_SOURCE:
				return getJtaDataSource();
			case EPersistencePackage.PERSISTENCE_UNIT__NON_JTA_DATA_SOURCE:
				return getNonJtaDataSource();
			case EPersistencePackage.PERSISTENCE_UNIT__MAPPING_FILE:
				return getMappingFile();
			case EPersistencePackage.PERSISTENCE_UNIT__JAR_FILE:
				return getJarFile();
			case EPersistencePackage.PERSISTENCE_UNIT__ENTITY_CLASS:
				return getEntityClass();
			case EPersistencePackage.PERSISTENCE_UNIT__EXCLUDE_UNLISTED_CLASSES:
				return isExcludeUnlistedClasses();
			case EPersistencePackage.PERSISTENCE_UNIT__SHARED_CACHE_MODE:
				return getSharedCacheMode();
			case EPersistencePackage.PERSISTENCE_UNIT__VALIDATION_MODE:
				return getValidationMode();
			case EPersistencePackage.PERSISTENCE_UNIT__PROPERTIES:
				return getProperties();
			case EPersistencePackage.PERSISTENCE_UNIT__ANY:
				if (coreType) return getAny();
				return ((FeatureMap.Internal)getAny()).getWrapper();
			case EPersistencePackage.PERSISTENCE_UNIT__NAME:
				return getName();
			case EPersistencePackage.PERSISTENCE_UNIT__TRANSACTION_TYPE:
				return getTransactionType();
			case EPersistencePackage.PERSISTENCE_UNIT__ENTITY_MAPPINGS:
				return getEntityMappings();
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
			case EPersistencePackage.PERSISTENCE_UNIT__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__PROVIDER:
				setProvider((String)newValue);
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__QUALIFIER:
				getQualifier().clear();
				getQualifier().addAll((Collection<? extends String>)newValue);
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__SCOPE:
				setScope((String)newValue);
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__JTA_DATA_SOURCE:
				setJtaDataSource((String)newValue);
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__NON_JTA_DATA_SOURCE:
				setNonJtaDataSource((String)newValue);
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__MAPPING_FILE:
				getMappingFile().clear();
				getMappingFile().addAll((Collection<? extends String>)newValue);
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__JAR_FILE:
				getJarFile().clear();
				getJarFile().addAll((Collection<? extends String>)newValue);
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__ENTITY_CLASS:
				getEntityClass().clear();
				getEntityClass().addAll((Collection<? extends String>)newValue);
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__EXCLUDE_UNLISTED_CLASSES:
				setExcludeUnlistedClasses((Boolean)newValue);
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__SHARED_CACHE_MODE:
				setSharedCacheMode((PersistenceUnitCachingType)newValue);
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__VALIDATION_MODE:
				setValidationMode((PersistenceUnitValidationMode)newValue);
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__PROPERTIES:
				setProperties((Properties)newValue);
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__ANY:
				((FeatureMap.Internal)getAny()).set(newValue);
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__NAME:
				setName((String)newValue);
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__TRANSACTION_TYPE:
				setTransactionType((PersistenceUnitTransactionType)newValue);
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__ENTITY_MAPPINGS:
				getEntityMappings().clear();
				getEntityMappings().addAll((Collection<? extends EntityMappings>)newValue);
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
			case EPersistencePackage.PERSISTENCE_UNIT__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__PROVIDER:
				setProvider(PROVIDER_EDEFAULT);
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__QUALIFIER:
				getQualifier().clear();
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__SCOPE:
				setScope(SCOPE_EDEFAULT);
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__JTA_DATA_SOURCE:
				setJtaDataSource(JTA_DATA_SOURCE_EDEFAULT);
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__NON_JTA_DATA_SOURCE:
				setNonJtaDataSource(NON_JTA_DATA_SOURCE_EDEFAULT);
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__MAPPING_FILE:
				getMappingFile().clear();
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__JAR_FILE:
				getJarFile().clear();
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__ENTITY_CLASS:
				getEntityClass().clear();
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__EXCLUDE_UNLISTED_CLASSES:
				unsetExcludeUnlistedClasses();
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__SHARED_CACHE_MODE:
				unsetSharedCacheMode();
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__VALIDATION_MODE:
				unsetValidationMode();
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__PROPERTIES:
				setProperties((Properties)null);
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__ANY:
				getAny().clear();
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__NAME:
				setName(NAME_EDEFAULT);
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__TRANSACTION_TYPE:
				unsetTransactionType();
				return;
			case EPersistencePackage.PERSISTENCE_UNIT__ENTITY_MAPPINGS:
				getEntityMappings().clear();
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
			case EPersistencePackage.PERSISTENCE_UNIT__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case EPersistencePackage.PERSISTENCE_UNIT__PROVIDER:
				return PROVIDER_EDEFAULT == null ? provider != null : !PROVIDER_EDEFAULT.equals(provider);
			case EPersistencePackage.PERSISTENCE_UNIT__QUALIFIER:
				return qualifier != null && !qualifier.isEmpty();
			case EPersistencePackage.PERSISTENCE_UNIT__SCOPE:
				return SCOPE_EDEFAULT == null ? scope != null : !SCOPE_EDEFAULT.equals(scope);
			case EPersistencePackage.PERSISTENCE_UNIT__JTA_DATA_SOURCE:
				return JTA_DATA_SOURCE_EDEFAULT == null ? jtaDataSource != null : !JTA_DATA_SOURCE_EDEFAULT.equals(jtaDataSource);
			case EPersistencePackage.PERSISTENCE_UNIT__NON_JTA_DATA_SOURCE:
				return NON_JTA_DATA_SOURCE_EDEFAULT == null ? nonJtaDataSource != null : !NON_JTA_DATA_SOURCE_EDEFAULT.equals(nonJtaDataSource);
			case EPersistencePackage.PERSISTENCE_UNIT__MAPPING_FILE:
				return mappingFile != null && !mappingFile.isEmpty();
			case EPersistencePackage.PERSISTENCE_UNIT__JAR_FILE:
				return jarFile != null && !jarFile.isEmpty();
			case EPersistencePackage.PERSISTENCE_UNIT__ENTITY_CLASS:
				return entityClass != null && !entityClass.isEmpty();
			case EPersistencePackage.PERSISTENCE_UNIT__EXCLUDE_UNLISTED_CLASSES:
				return isSetExcludeUnlistedClasses();
			case EPersistencePackage.PERSISTENCE_UNIT__SHARED_CACHE_MODE:
				return isSetSharedCacheMode();
			case EPersistencePackage.PERSISTENCE_UNIT__VALIDATION_MODE:
				return isSetValidationMode();
			case EPersistencePackage.PERSISTENCE_UNIT__PROPERTIES:
				return properties != null;
			case EPersistencePackage.PERSISTENCE_UNIT__ANY:
				return any != null && !any.isEmpty();
			case EPersistencePackage.PERSISTENCE_UNIT__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case EPersistencePackage.PERSISTENCE_UNIT__TRANSACTION_TYPE:
				return isSetTransactionType();
			case EPersistencePackage.PERSISTENCE_UNIT__ENTITY_MAPPINGS:
				return entityMappings != null && !entityMappings.isEmpty();
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
		result.append(", provider: ");
		result.append(provider);
		result.append(", qualifier: ");
		result.append(qualifier);
		result.append(", scope: ");
		result.append(scope);
		result.append(", jtaDataSource: ");
		result.append(jtaDataSource);
		result.append(", nonJtaDataSource: ");
		result.append(nonJtaDataSource);
		result.append(", mappingFile: ");
		result.append(mappingFile);
		result.append(", jarFile: ");
		result.append(jarFile);
		result.append(", entityClass: ");
		result.append(entityClass);
		result.append(", excludeUnlistedClasses: ");
		if (excludeUnlistedClassesESet) result.append(excludeUnlistedClasses); else result.append("<unset>");
		result.append(", sharedCacheMode: ");
		if (sharedCacheModeESet) result.append(sharedCacheMode); else result.append("<unset>");
		result.append(", validationMode: ");
		if (validationModeESet) result.append(validationMode); else result.append("<unset>");
		result.append(", any: ");
		result.append(any);
		result.append(", name: ");
		result.append(name);
		result.append(", transactionType: ");
		if (transactionTypeESet) result.append(transactionType); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //PersistenceUnitImpl
