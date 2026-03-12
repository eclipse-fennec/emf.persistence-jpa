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
import org.eclipse.fennec.persistence.eorm.EntityResult;
import org.eclipse.fennec.persistence.eorm.FieldResult;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Entity Result</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityResultImpl#getFieldResult <em>Field Result</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityResultImpl#getDiscriminatorColumn <em>Discriminator Column</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityResultImpl#getEntityClass <em>Entity Class</em>}</li>
 * </ul>
 *
 * @generated
 */
public class EntityResultImpl extends MinimalEObjectImpl.Container implements EntityResult {
	/**
	 * The cached value of the '{@link #getFieldResult() <em>Field Result</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFieldResult()
	 * @generated
	 * @ordered
	 */
	protected EList<FieldResult> fieldResult;

	/**
	 * The default value of the '{@link #getDiscriminatorColumn() <em>Discriminator Column</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDiscriminatorColumn()
	 * @generated
	 * @ordered
	 */
	protected static final String DISCRIMINATOR_COLUMN_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDiscriminatorColumn() <em>Discriminator Column</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDiscriminatorColumn()
	 * @generated
	 * @ordered
	 */
	protected String discriminatorColumn = DISCRIMINATOR_COLUMN_EDEFAULT;

	/**
	 * The default value of the '{@link #getEntityClass() <em>Entity Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEntityClass()
	 * @generated
	 * @ordered
	 */
	protected static final String ENTITY_CLASS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getEntityClass() <em>Entity Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEntityClass()
	 * @generated
	 * @ordered
	 */
	protected String entityClass = ENTITY_CLASS_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EntityResultImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EORMPackage.eINSTANCE.getEntityResult();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<FieldResult> getFieldResult() {
		if (fieldResult == null) {
			fieldResult = new EObjectContainmentEList<FieldResult>(FieldResult.class, this, EORMPackage.ENTITY_RESULT__FIELD_RESULT);
		}
		return fieldResult;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDiscriminatorColumn() {
		return discriminatorColumn;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDiscriminatorColumn(String newDiscriminatorColumn) {
		String oldDiscriminatorColumn = discriminatorColumn;
		discriminatorColumn = newDiscriminatorColumn;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_RESULT__DISCRIMINATOR_COLUMN, oldDiscriminatorColumn, discriminatorColumn));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getEntityClass() {
		return entityClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setEntityClass(String newEntityClass) {
		String oldEntityClass = entityClass;
		entityClass = newEntityClass;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_RESULT__ENTITY_CLASS, oldEntityClass, entityClass));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EORMPackage.ENTITY_RESULT__FIELD_RESULT:
				return ((InternalEList<?>)getFieldResult()).basicRemove(otherEnd, msgs);
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
			case EORMPackage.ENTITY_RESULT__FIELD_RESULT:
				return getFieldResult();
			case EORMPackage.ENTITY_RESULT__DISCRIMINATOR_COLUMN:
				return getDiscriminatorColumn();
			case EORMPackage.ENTITY_RESULT__ENTITY_CLASS:
				return getEntityClass();
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
			case EORMPackage.ENTITY_RESULT__FIELD_RESULT:
				getFieldResult().clear();
				getFieldResult().addAll((Collection<? extends FieldResult>)newValue);
				return;
			case EORMPackage.ENTITY_RESULT__DISCRIMINATOR_COLUMN:
				setDiscriminatorColumn((String)newValue);
				return;
			case EORMPackage.ENTITY_RESULT__ENTITY_CLASS:
				setEntityClass((String)newValue);
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
			case EORMPackage.ENTITY_RESULT__FIELD_RESULT:
				getFieldResult().clear();
				return;
			case EORMPackage.ENTITY_RESULT__DISCRIMINATOR_COLUMN:
				setDiscriminatorColumn(DISCRIMINATOR_COLUMN_EDEFAULT);
				return;
			case EORMPackage.ENTITY_RESULT__ENTITY_CLASS:
				setEntityClass(ENTITY_CLASS_EDEFAULT);
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
			case EORMPackage.ENTITY_RESULT__FIELD_RESULT:
				return fieldResult != null && !fieldResult.isEmpty();
			case EORMPackage.ENTITY_RESULT__DISCRIMINATOR_COLUMN:
				return DISCRIMINATOR_COLUMN_EDEFAULT == null ? discriminatorColumn != null : !DISCRIMINATOR_COLUMN_EDEFAULT.equals(discriminatorColumn);
			case EORMPackage.ENTITY_RESULT__ENTITY_CLASS:
				return ENTITY_CLASS_EDEFAULT == null ? entityClass != null : !ENTITY_CLASS_EDEFAULT.equals(entityClass);
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
		result.append(" (discriminatorColumn: ");
		result.append(discriminatorColumn);
		result.append(", entityClass: ");
		result.append(entityClass);
		result.append(')');
		return result.toString();
	}

} //EntityResultImpl
