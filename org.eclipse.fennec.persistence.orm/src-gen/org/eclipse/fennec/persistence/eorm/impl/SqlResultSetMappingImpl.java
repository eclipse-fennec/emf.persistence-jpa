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

import org.eclipse.fennec.persistence.eorm.ColumnResult;
import org.eclipse.fennec.persistence.eorm.ConstructorResult;
import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.EntityResult;
import org.eclipse.fennec.persistence.eorm.SqlResultSetMapping;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Sql Result Set Mapping</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.SqlResultSetMappingImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.SqlResultSetMappingImpl#getEntityResult <em>Entity Result</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.SqlResultSetMappingImpl#getConstructorResult <em>Constructor Result</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.SqlResultSetMappingImpl#getColumnResult <em>Column Result</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.SqlResultSetMappingImpl#getName <em>Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SqlResultSetMappingImpl extends MinimalEObjectImpl.Container implements SqlResultSetMapping {
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
	 * The cached value of the '{@link #getEntityResult() <em>Entity Result</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEntityResult()
	 * @generated
	 * @ordered
	 */
	protected EList<EntityResult> entityResult;

	/**
	 * The cached value of the '{@link #getConstructorResult() <em>Constructor Result</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConstructorResult()
	 * @generated
	 * @ordered
	 */
	protected EList<ConstructorResult> constructorResult;

	/**
	 * The cached value of the '{@link #getColumnResult() <em>Column Result</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getColumnResult()
	 * @generated
	 * @ordered
	 */
	protected EList<ColumnResult> columnResult;

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
	protected SqlResultSetMappingImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EORMPackage.eINSTANCE.getSqlResultSetMapping();
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.SQL_RESULT_SET_MAPPING__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<EntityResult> getEntityResult() {
		if (entityResult == null) {
			entityResult = new EObjectContainmentEList<EntityResult>(EntityResult.class, this, EORMPackage.SQL_RESULT_SET_MAPPING__ENTITY_RESULT);
		}
		return entityResult;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ConstructorResult> getConstructorResult() {
		if (constructorResult == null) {
			constructorResult = new EObjectContainmentEList<ConstructorResult>(ConstructorResult.class, this, EORMPackage.SQL_RESULT_SET_MAPPING__CONSTRUCTOR_RESULT);
		}
		return constructorResult;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ColumnResult> getColumnResult() {
		if (columnResult == null) {
			columnResult = new EObjectContainmentEList<ColumnResult>(ColumnResult.class, this, EORMPackage.SQL_RESULT_SET_MAPPING__COLUMN_RESULT);
		}
		return columnResult;
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.SQL_RESULT_SET_MAPPING__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EORMPackage.SQL_RESULT_SET_MAPPING__ENTITY_RESULT:
				return ((InternalEList<?>)getEntityResult()).basicRemove(otherEnd, msgs);
			case EORMPackage.SQL_RESULT_SET_MAPPING__CONSTRUCTOR_RESULT:
				return ((InternalEList<?>)getConstructorResult()).basicRemove(otherEnd, msgs);
			case EORMPackage.SQL_RESULT_SET_MAPPING__COLUMN_RESULT:
				return ((InternalEList<?>)getColumnResult()).basicRemove(otherEnd, msgs);
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
			case EORMPackage.SQL_RESULT_SET_MAPPING__DESCRIPTION:
				return getDescription();
			case EORMPackage.SQL_RESULT_SET_MAPPING__ENTITY_RESULT:
				return getEntityResult();
			case EORMPackage.SQL_RESULT_SET_MAPPING__CONSTRUCTOR_RESULT:
				return getConstructorResult();
			case EORMPackage.SQL_RESULT_SET_MAPPING__COLUMN_RESULT:
				return getColumnResult();
			case EORMPackage.SQL_RESULT_SET_MAPPING__NAME:
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
			case EORMPackage.SQL_RESULT_SET_MAPPING__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case EORMPackage.SQL_RESULT_SET_MAPPING__ENTITY_RESULT:
				getEntityResult().clear();
				getEntityResult().addAll((Collection<? extends EntityResult>)newValue);
				return;
			case EORMPackage.SQL_RESULT_SET_MAPPING__CONSTRUCTOR_RESULT:
				getConstructorResult().clear();
				getConstructorResult().addAll((Collection<? extends ConstructorResult>)newValue);
				return;
			case EORMPackage.SQL_RESULT_SET_MAPPING__COLUMN_RESULT:
				getColumnResult().clear();
				getColumnResult().addAll((Collection<? extends ColumnResult>)newValue);
				return;
			case EORMPackage.SQL_RESULT_SET_MAPPING__NAME:
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
			case EORMPackage.SQL_RESULT_SET_MAPPING__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case EORMPackage.SQL_RESULT_SET_MAPPING__ENTITY_RESULT:
				getEntityResult().clear();
				return;
			case EORMPackage.SQL_RESULT_SET_MAPPING__CONSTRUCTOR_RESULT:
				getConstructorResult().clear();
				return;
			case EORMPackage.SQL_RESULT_SET_MAPPING__COLUMN_RESULT:
				getColumnResult().clear();
				return;
			case EORMPackage.SQL_RESULT_SET_MAPPING__NAME:
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
			case EORMPackage.SQL_RESULT_SET_MAPPING__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case EORMPackage.SQL_RESULT_SET_MAPPING__ENTITY_RESULT:
				return entityResult != null && !entityResult.isEmpty();
			case EORMPackage.SQL_RESULT_SET_MAPPING__CONSTRUCTOR_RESULT:
				return constructorResult != null && !constructorResult.isEmpty();
			case EORMPackage.SQL_RESULT_SET_MAPPING__COLUMN_RESULT:
				return columnResult != null && !columnResult.isEmpty();
			case EORMPackage.SQL_RESULT_SET_MAPPING__NAME:
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
		result.append(", name: ");
		result.append(name);
		result.append(')');
		return result.toString();
	}

} //SqlResultSetMappingImpl
