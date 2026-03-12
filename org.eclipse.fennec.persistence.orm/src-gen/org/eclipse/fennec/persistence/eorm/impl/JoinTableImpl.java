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
import org.eclipse.fennec.persistence.eorm.ForeignKey;
import org.eclipse.fennec.persistence.eorm.Index;
import org.eclipse.fennec.persistence.eorm.JoinColumn;
import org.eclipse.fennec.persistence.eorm.JoinTable;
import org.eclipse.fennec.persistence.eorm.UniqueConstraint;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Join Table</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.JoinTableImpl#getJoinColumn <em>Join Column</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.JoinTableImpl#getForeignKey <em>Foreign Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.JoinTableImpl#getInverseJoinColumn <em>Inverse Join Column</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.JoinTableImpl#getInverseForeignKey <em>Inverse Foreign Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.JoinTableImpl#getUniqueConstraint <em>Unique Constraint</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.JoinTableImpl#getIndex <em>Index</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.JoinTableImpl#getCatalog <em>Catalog</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.JoinTableImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.JoinTableImpl#getSchema <em>Schema</em>}</li>
 * </ul>
 *
 * @generated
 */
public class JoinTableImpl extends MinimalEObjectImpl.Container implements JoinTable {
	/**
	 * The cached value of the '{@link #getJoinColumn() <em>Join Column</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getJoinColumn()
	 * @generated
	 * @ordered
	 */
	protected EList<JoinColumn> joinColumn;

	/**
	 * The cached value of the '{@link #getForeignKey() <em>Foreign Key</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getForeignKey()
	 * @generated
	 * @ordered
	 */
	protected ForeignKey foreignKey;

	/**
	 * The cached value of the '{@link #getInverseJoinColumn() <em>Inverse Join Column</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInverseJoinColumn()
	 * @generated
	 * @ordered
	 */
	protected EList<JoinColumn> inverseJoinColumn;

	/**
	 * The cached value of the '{@link #getInverseForeignKey() <em>Inverse Foreign Key</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInverseForeignKey()
	 * @generated
	 * @ordered
	 */
	protected ForeignKey inverseForeignKey;

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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected JoinTableImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EORMPackage.eINSTANCE.getJoinTable();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<JoinColumn> getJoinColumn() {
		if (joinColumn == null) {
			joinColumn = new EObjectContainmentEList<JoinColumn>(JoinColumn.class, this, EORMPackage.JOIN_TABLE__JOIN_COLUMN);
		}
		return joinColumn;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ForeignKey getForeignKey() {
		return foreignKey;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetForeignKey(ForeignKey newForeignKey, NotificationChain msgs) {
		ForeignKey oldForeignKey = foreignKey;
		foreignKey = newForeignKey;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.JOIN_TABLE__FOREIGN_KEY, oldForeignKey, newForeignKey);
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
	public void setForeignKey(ForeignKey newForeignKey) {
		if (newForeignKey != foreignKey) {
			NotificationChain msgs = null;
			if (foreignKey != null)
				msgs = ((InternalEObject)foreignKey).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.JOIN_TABLE__FOREIGN_KEY, null, msgs);
			if (newForeignKey != null)
				msgs = ((InternalEObject)newForeignKey).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.JOIN_TABLE__FOREIGN_KEY, null, msgs);
			msgs = basicSetForeignKey(newForeignKey, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.JOIN_TABLE__FOREIGN_KEY, newForeignKey, newForeignKey));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<JoinColumn> getInverseJoinColumn() {
		if (inverseJoinColumn == null) {
			inverseJoinColumn = new EObjectContainmentEList<JoinColumn>(JoinColumn.class, this, EORMPackage.JOIN_TABLE__INVERSE_JOIN_COLUMN);
		}
		return inverseJoinColumn;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ForeignKey getInverseForeignKey() {
		return inverseForeignKey;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetInverseForeignKey(ForeignKey newInverseForeignKey, NotificationChain msgs) {
		ForeignKey oldInverseForeignKey = inverseForeignKey;
		inverseForeignKey = newInverseForeignKey;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.JOIN_TABLE__INVERSE_FOREIGN_KEY, oldInverseForeignKey, newInverseForeignKey);
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
	public void setInverseForeignKey(ForeignKey newInverseForeignKey) {
		if (newInverseForeignKey != inverseForeignKey) {
			NotificationChain msgs = null;
			if (inverseForeignKey != null)
				msgs = ((InternalEObject)inverseForeignKey).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.JOIN_TABLE__INVERSE_FOREIGN_KEY, null, msgs);
			if (newInverseForeignKey != null)
				msgs = ((InternalEObject)newInverseForeignKey).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.JOIN_TABLE__INVERSE_FOREIGN_KEY, null, msgs);
			msgs = basicSetInverseForeignKey(newInverseForeignKey, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.JOIN_TABLE__INVERSE_FOREIGN_KEY, newInverseForeignKey, newInverseForeignKey));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<UniqueConstraint> getUniqueConstraint() {
		if (uniqueConstraint == null) {
			uniqueConstraint = new EObjectContainmentEList<UniqueConstraint>(UniqueConstraint.class, this, EORMPackage.JOIN_TABLE__UNIQUE_CONSTRAINT);
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
			index = new EObjectContainmentEList<Index>(Index.class, this, EORMPackage.JOIN_TABLE__INDEX);
		}
		return index;
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.JOIN_TABLE__CATALOG, oldCatalog, catalog));
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.JOIN_TABLE__NAME, oldName, name));
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.JOIN_TABLE__SCHEMA, oldSchema, schema));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EORMPackage.JOIN_TABLE__JOIN_COLUMN:
				return ((InternalEList<?>)getJoinColumn()).basicRemove(otherEnd, msgs);
			case EORMPackage.JOIN_TABLE__FOREIGN_KEY:
				return basicSetForeignKey(null, msgs);
			case EORMPackage.JOIN_TABLE__INVERSE_JOIN_COLUMN:
				return ((InternalEList<?>)getInverseJoinColumn()).basicRemove(otherEnd, msgs);
			case EORMPackage.JOIN_TABLE__INVERSE_FOREIGN_KEY:
				return basicSetInverseForeignKey(null, msgs);
			case EORMPackage.JOIN_TABLE__UNIQUE_CONSTRAINT:
				return ((InternalEList<?>)getUniqueConstraint()).basicRemove(otherEnd, msgs);
			case EORMPackage.JOIN_TABLE__INDEX:
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
			case EORMPackage.JOIN_TABLE__JOIN_COLUMN:
				return getJoinColumn();
			case EORMPackage.JOIN_TABLE__FOREIGN_KEY:
				return getForeignKey();
			case EORMPackage.JOIN_TABLE__INVERSE_JOIN_COLUMN:
				return getInverseJoinColumn();
			case EORMPackage.JOIN_TABLE__INVERSE_FOREIGN_KEY:
				return getInverseForeignKey();
			case EORMPackage.JOIN_TABLE__UNIQUE_CONSTRAINT:
				return getUniqueConstraint();
			case EORMPackage.JOIN_TABLE__INDEX:
				return getIndex();
			case EORMPackage.JOIN_TABLE__CATALOG:
				return getCatalog();
			case EORMPackage.JOIN_TABLE__NAME:
				return getName();
			case EORMPackage.JOIN_TABLE__SCHEMA:
				return getSchema();
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
			case EORMPackage.JOIN_TABLE__JOIN_COLUMN:
				getJoinColumn().clear();
				getJoinColumn().addAll((Collection<? extends JoinColumn>)newValue);
				return;
			case EORMPackage.JOIN_TABLE__FOREIGN_KEY:
				setForeignKey((ForeignKey)newValue);
				return;
			case EORMPackage.JOIN_TABLE__INVERSE_JOIN_COLUMN:
				getInverseJoinColumn().clear();
				getInverseJoinColumn().addAll((Collection<? extends JoinColumn>)newValue);
				return;
			case EORMPackage.JOIN_TABLE__INVERSE_FOREIGN_KEY:
				setInverseForeignKey((ForeignKey)newValue);
				return;
			case EORMPackage.JOIN_TABLE__UNIQUE_CONSTRAINT:
				getUniqueConstraint().clear();
				getUniqueConstraint().addAll((Collection<? extends UniqueConstraint>)newValue);
				return;
			case EORMPackage.JOIN_TABLE__INDEX:
				getIndex().clear();
				getIndex().addAll((Collection<? extends Index>)newValue);
				return;
			case EORMPackage.JOIN_TABLE__CATALOG:
				setCatalog((String)newValue);
				return;
			case EORMPackage.JOIN_TABLE__NAME:
				setName((String)newValue);
				return;
			case EORMPackage.JOIN_TABLE__SCHEMA:
				setSchema((String)newValue);
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
			case EORMPackage.JOIN_TABLE__JOIN_COLUMN:
				getJoinColumn().clear();
				return;
			case EORMPackage.JOIN_TABLE__FOREIGN_KEY:
				setForeignKey((ForeignKey)null);
				return;
			case EORMPackage.JOIN_TABLE__INVERSE_JOIN_COLUMN:
				getInverseJoinColumn().clear();
				return;
			case EORMPackage.JOIN_TABLE__INVERSE_FOREIGN_KEY:
				setInverseForeignKey((ForeignKey)null);
				return;
			case EORMPackage.JOIN_TABLE__UNIQUE_CONSTRAINT:
				getUniqueConstraint().clear();
				return;
			case EORMPackage.JOIN_TABLE__INDEX:
				getIndex().clear();
				return;
			case EORMPackage.JOIN_TABLE__CATALOG:
				setCatalog(CATALOG_EDEFAULT);
				return;
			case EORMPackage.JOIN_TABLE__NAME:
				setName(NAME_EDEFAULT);
				return;
			case EORMPackage.JOIN_TABLE__SCHEMA:
				setSchema(SCHEMA_EDEFAULT);
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
			case EORMPackage.JOIN_TABLE__JOIN_COLUMN:
				return joinColumn != null && !joinColumn.isEmpty();
			case EORMPackage.JOIN_TABLE__FOREIGN_KEY:
				return foreignKey != null;
			case EORMPackage.JOIN_TABLE__INVERSE_JOIN_COLUMN:
				return inverseJoinColumn != null && !inverseJoinColumn.isEmpty();
			case EORMPackage.JOIN_TABLE__INVERSE_FOREIGN_KEY:
				return inverseForeignKey != null;
			case EORMPackage.JOIN_TABLE__UNIQUE_CONSTRAINT:
				return uniqueConstraint != null && !uniqueConstraint.isEmpty();
			case EORMPackage.JOIN_TABLE__INDEX:
				return index != null && !index.isEmpty();
			case EORMPackage.JOIN_TABLE__CATALOG:
				return CATALOG_EDEFAULT == null ? catalog != null : !CATALOG_EDEFAULT.equals(catalog);
			case EORMPackage.JOIN_TABLE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case EORMPackage.JOIN_TABLE__SCHEMA:
				return SCHEMA_EDEFAULT == null ? schema != null : !SCHEMA_EDEFAULT.equals(schema);
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
		result.append(" (catalog: ");
		result.append(catalog);
		result.append(", name: ");
		result.append(name);
		result.append(", schema: ");
		result.append(schema);
		result.append(')');
		return result.toString();
	}

} //JoinTableImpl
