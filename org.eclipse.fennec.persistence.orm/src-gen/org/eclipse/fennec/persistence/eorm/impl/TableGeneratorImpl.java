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

import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.Index;
import org.eclipse.fennec.persistence.eorm.TableGenerator;
import org.eclipse.fennec.persistence.eorm.UniqueConstraint;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Table Generator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.TableGeneratorImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.TableGeneratorImpl#getUniqueConstraint <em>Unique Constraint</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.TableGeneratorImpl#getIndex <em>Index</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.TableGeneratorImpl#getAllocationSize <em>Allocation Size</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.TableGeneratorImpl#getCatalog <em>Catalog</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.TableGeneratorImpl#getInitialValue <em>Initial Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.TableGeneratorImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.TableGeneratorImpl#getPkColumnName <em>Pk Column Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.TableGeneratorImpl#getPkColumnValue <em>Pk Column Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.TableGeneratorImpl#getSchema <em>Schema</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.TableGeneratorImpl#getTable <em>Table</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.TableGeneratorImpl#getValueColumnName <em>Value Column Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TableGeneratorImpl extends MinimalEObjectImpl.Container implements TableGenerator {
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
	 * The cached value of the '{@link #getUniqueConstraint() <em>Unique Constraint</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUniqueConstraint()
	 * @generated
	 * @ordered
	 */
	protected EList<UniqueConstraint> uniqueConstraint;

	/**
	 * The cached value of the '{@link #getIndex() <em>Index</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIndex()
	 * @generated
	 * @ordered
	 */
	protected EList<Index> index;

	/**
	 * The default value of the '{@link #getAllocationSize() <em>Allocation Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAllocationSize()
	 * @generated
	 * @ordered
	 */
	protected static final int ALLOCATION_SIZE_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getAllocationSize() <em>Allocation Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAllocationSize()
	 * @generated
	 * @ordered
	 */
	protected int allocationSize = ALLOCATION_SIZE_EDEFAULT;

	/**
	 * This is true if the Allocation Size attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean allocationSizeESet;

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
	 * The default value of the '{@link #getInitialValue() <em>Initial Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInitialValue()
	 * @generated
	 * @ordered
	 */
	protected static final int INITIAL_VALUE_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getInitialValue() <em>Initial Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInitialValue()
	 * @generated
	 * @ordered
	 */
	protected int initialValue = INITIAL_VALUE_EDEFAULT;

	/**
	 * This is true if the Initial Value attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean initialValueESet;

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
	 * The default value of the '{@link #getPkColumnName() <em>Pk Column Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPkColumnName()
	 * @generated
	 * @ordered
	 */
	protected static final String PK_COLUMN_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPkColumnName() <em>Pk Column Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPkColumnName()
	 * @generated
	 * @ordered
	 */
	protected String pkColumnName = PK_COLUMN_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getPkColumnValue() <em>Pk Column Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPkColumnValue()
	 * @generated
	 * @ordered
	 */
	protected static final String PK_COLUMN_VALUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPkColumnValue() <em>Pk Column Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPkColumnValue()
	 * @generated
	 * @ordered
	 */
	protected String pkColumnValue = PK_COLUMN_VALUE_EDEFAULT;

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
	 * The default value of the '{@link #getTable() <em>Table</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTable()
	 * @generated
	 * @ordered
	 */
	protected static final String TABLE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTable() <em>Table</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTable()
	 * @generated
	 * @ordered
	 */
	protected String table = TABLE_EDEFAULT;

	/**
	 * The default value of the '{@link #getValueColumnName() <em>Value Column Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValueColumnName()
	 * @generated
	 * @ordered
	 */
	protected static final String VALUE_COLUMN_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getValueColumnName() <em>Value Column Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValueColumnName()
	 * @generated
	 * @ordered
	 */
	protected String valueColumnName = VALUE_COLUMN_NAME_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TableGeneratorImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EORMPackage.eINSTANCE.getTableGenerator();
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.TABLE_GENERATOR__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<UniqueConstraint> getUniqueConstraint() {
		if (uniqueConstraint == null) {
			uniqueConstraint = new EObjectContainmentEList<UniqueConstraint>(UniqueConstraint.class, this, EORMPackage.TABLE_GENERATOR__UNIQUE_CONSTRAINT);
		}
		return uniqueConstraint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Index> getIndex() {
		if (index == null) {
			index = new EObjectContainmentEList<Index>(Index.class, this, EORMPackage.TABLE_GENERATOR__INDEX);
		}
		return index;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getAllocationSize() {
		return allocationSize;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAllocationSize(int newAllocationSize) {
		int oldAllocationSize = allocationSize;
		allocationSize = newAllocationSize;
		boolean oldAllocationSizeESet = allocationSizeESet;
		allocationSizeESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.TABLE_GENERATOR__ALLOCATION_SIZE, oldAllocationSize, allocationSize, !oldAllocationSizeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetAllocationSize() {
		int oldAllocationSize = allocationSize;
		boolean oldAllocationSizeESet = allocationSizeESet;
		allocationSize = ALLOCATION_SIZE_EDEFAULT;
		allocationSizeESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EORMPackage.TABLE_GENERATOR__ALLOCATION_SIZE, oldAllocationSize, ALLOCATION_SIZE_EDEFAULT, oldAllocationSizeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetAllocationSize() {
		return allocationSizeESet;
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.TABLE_GENERATOR__CATALOG, oldCatalog, catalog));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getInitialValue() {
		return initialValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setInitialValue(int newInitialValue) {
		int oldInitialValue = initialValue;
		initialValue = newInitialValue;
		boolean oldInitialValueESet = initialValueESet;
		initialValueESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.TABLE_GENERATOR__INITIAL_VALUE, oldInitialValue, initialValue, !oldInitialValueESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetInitialValue() {
		int oldInitialValue = initialValue;
		boolean oldInitialValueESet = initialValueESet;
		initialValue = INITIAL_VALUE_EDEFAULT;
		initialValueESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EORMPackage.TABLE_GENERATOR__INITIAL_VALUE, oldInitialValue, INITIAL_VALUE_EDEFAULT, oldInitialValueESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetInitialValue() {
		return initialValueESet;
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.TABLE_GENERATOR__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getPkColumnName() {
		return pkColumnName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPkColumnName(String newPkColumnName) {
		String oldPkColumnName = pkColumnName;
		pkColumnName = newPkColumnName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.TABLE_GENERATOR__PK_COLUMN_NAME, oldPkColumnName, pkColumnName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getPkColumnValue() {
		return pkColumnValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPkColumnValue(String newPkColumnValue) {
		String oldPkColumnValue = pkColumnValue;
		pkColumnValue = newPkColumnValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.TABLE_GENERATOR__PK_COLUMN_VALUE, oldPkColumnValue, pkColumnValue));
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.TABLE_GENERATOR__SCHEMA, oldSchema, schema));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getTable() {
		return table;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTable(String newTable) {
		String oldTable = table;
		table = newTable;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.TABLE_GENERATOR__TABLE, oldTable, table));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getValueColumnName() {
		return valueColumnName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setValueColumnName(String newValueColumnName) {
		String oldValueColumnName = valueColumnName;
		valueColumnName = newValueColumnName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.TABLE_GENERATOR__VALUE_COLUMN_NAME, oldValueColumnName, valueColumnName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EORMPackage.TABLE_GENERATOR__UNIQUE_CONSTRAINT:
				return ((InternalEList<?>)getUniqueConstraint()).basicRemove(otherEnd, msgs);
			case EORMPackage.TABLE_GENERATOR__INDEX:
				return ((InternalEList<?>)getIndex()).basicRemove(otherEnd, msgs);
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
			case EORMPackage.TABLE_GENERATOR__DESCRIPTION:
				return getDescription();
			case EORMPackage.TABLE_GENERATOR__UNIQUE_CONSTRAINT:
				return getUniqueConstraint();
			case EORMPackage.TABLE_GENERATOR__INDEX:
				return getIndex();
			case EORMPackage.TABLE_GENERATOR__ALLOCATION_SIZE:
				return getAllocationSize();
			case EORMPackage.TABLE_GENERATOR__CATALOG:
				return getCatalog();
			case EORMPackage.TABLE_GENERATOR__INITIAL_VALUE:
				return getInitialValue();
			case EORMPackage.TABLE_GENERATOR__NAME:
				return getName();
			case EORMPackage.TABLE_GENERATOR__PK_COLUMN_NAME:
				return getPkColumnName();
			case EORMPackage.TABLE_GENERATOR__PK_COLUMN_VALUE:
				return getPkColumnValue();
			case EORMPackage.TABLE_GENERATOR__SCHEMA:
				return getSchema();
			case EORMPackage.TABLE_GENERATOR__TABLE:
				return getTable();
			case EORMPackage.TABLE_GENERATOR__VALUE_COLUMN_NAME:
				return getValueColumnName();
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
			case EORMPackage.TABLE_GENERATOR__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case EORMPackage.TABLE_GENERATOR__UNIQUE_CONSTRAINT:
				getUniqueConstraint().clear();
				getUniqueConstraint().addAll((Collection<? extends UniqueConstraint>)newValue);
				return;
			case EORMPackage.TABLE_GENERATOR__INDEX:
				getIndex().clear();
				getIndex().addAll((Collection<? extends Index>)newValue);
				return;
			case EORMPackage.TABLE_GENERATOR__ALLOCATION_SIZE:
				setAllocationSize((Integer)newValue);
				return;
			case EORMPackage.TABLE_GENERATOR__CATALOG:
				setCatalog((String)newValue);
				return;
			case EORMPackage.TABLE_GENERATOR__INITIAL_VALUE:
				setInitialValue((Integer)newValue);
				return;
			case EORMPackage.TABLE_GENERATOR__NAME:
				setName((String)newValue);
				return;
			case EORMPackage.TABLE_GENERATOR__PK_COLUMN_NAME:
				setPkColumnName((String)newValue);
				return;
			case EORMPackage.TABLE_GENERATOR__PK_COLUMN_VALUE:
				setPkColumnValue((String)newValue);
				return;
			case EORMPackage.TABLE_GENERATOR__SCHEMA:
				setSchema((String)newValue);
				return;
			case EORMPackage.TABLE_GENERATOR__TABLE:
				setTable((String)newValue);
				return;
			case EORMPackage.TABLE_GENERATOR__VALUE_COLUMN_NAME:
				setValueColumnName((String)newValue);
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
			case EORMPackage.TABLE_GENERATOR__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case EORMPackage.TABLE_GENERATOR__UNIQUE_CONSTRAINT:
				getUniqueConstraint().clear();
				return;
			case EORMPackage.TABLE_GENERATOR__INDEX:
				getIndex().clear();
				return;
			case EORMPackage.TABLE_GENERATOR__ALLOCATION_SIZE:
				unsetAllocationSize();
				return;
			case EORMPackage.TABLE_GENERATOR__CATALOG:
				setCatalog(CATALOG_EDEFAULT);
				return;
			case EORMPackage.TABLE_GENERATOR__INITIAL_VALUE:
				unsetInitialValue();
				return;
			case EORMPackage.TABLE_GENERATOR__NAME:
				setName(NAME_EDEFAULT);
				return;
			case EORMPackage.TABLE_GENERATOR__PK_COLUMN_NAME:
				setPkColumnName(PK_COLUMN_NAME_EDEFAULT);
				return;
			case EORMPackage.TABLE_GENERATOR__PK_COLUMN_VALUE:
				setPkColumnValue(PK_COLUMN_VALUE_EDEFAULT);
				return;
			case EORMPackage.TABLE_GENERATOR__SCHEMA:
				setSchema(SCHEMA_EDEFAULT);
				return;
			case EORMPackage.TABLE_GENERATOR__TABLE:
				setTable(TABLE_EDEFAULT);
				return;
			case EORMPackage.TABLE_GENERATOR__VALUE_COLUMN_NAME:
				setValueColumnName(VALUE_COLUMN_NAME_EDEFAULT);
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
			case EORMPackage.TABLE_GENERATOR__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case EORMPackage.TABLE_GENERATOR__UNIQUE_CONSTRAINT:
				return uniqueConstraint != null && !uniqueConstraint.isEmpty();
			case EORMPackage.TABLE_GENERATOR__INDEX:
				return index != null && !index.isEmpty();
			case EORMPackage.TABLE_GENERATOR__ALLOCATION_SIZE:
				return isSetAllocationSize();
			case EORMPackage.TABLE_GENERATOR__CATALOG:
				return CATALOG_EDEFAULT == null ? catalog != null : !CATALOG_EDEFAULT.equals(catalog);
			case EORMPackage.TABLE_GENERATOR__INITIAL_VALUE:
				return isSetInitialValue();
			case EORMPackage.TABLE_GENERATOR__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case EORMPackage.TABLE_GENERATOR__PK_COLUMN_NAME:
				return PK_COLUMN_NAME_EDEFAULT == null ? pkColumnName != null : !PK_COLUMN_NAME_EDEFAULT.equals(pkColumnName);
			case EORMPackage.TABLE_GENERATOR__PK_COLUMN_VALUE:
				return PK_COLUMN_VALUE_EDEFAULT == null ? pkColumnValue != null : !PK_COLUMN_VALUE_EDEFAULT.equals(pkColumnValue);
			case EORMPackage.TABLE_GENERATOR__SCHEMA:
				return SCHEMA_EDEFAULT == null ? schema != null : !SCHEMA_EDEFAULT.equals(schema);
			case EORMPackage.TABLE_GENERATOR__TABLE:
				return TABLE_EDEFAULT == null ? table != null : !TABLE_EDEFAULT.equals(table);
			case EORMPackage.TABLE_GENERATOR__VALUE_COLUMN_NAME:
				return VALUE_COLUMN_NAME_EDEFAULT == null ? valueColumnName != null : !VALUE_COLUMN_NAME_EDEFAULT.equals(valueColumnName);
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
		result.append(", allocationSize: ");
		if (allocationSizeESet) result.append(allocationSize); else result.append("<unset>");
		result.append(", catalog: ");
		result.append(catalog);
		result.append(", initialValue: ");
		if (initialValueESet) result.append(initialValue); else result.append("<unset>");
		result.append(", name: ");
		result.append(name);
		result.append(", pkColumnName: ");
		result.append(pkColumnName);
		result.append(", pkColumnValue: ");
		result.append(pkColumnValue);
		result.append(", schema: ");
		result.append(schema);
		result.append(", table: ");
		result.append(table);
		result.append(", valueColumnName: ");
		result.append(valueColumnName);
		result.append(')');
		return result.toString();
	}

} //TableGeneratorImpl
