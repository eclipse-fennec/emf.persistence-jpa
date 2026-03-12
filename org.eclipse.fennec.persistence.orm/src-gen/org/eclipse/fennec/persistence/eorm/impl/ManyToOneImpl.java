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

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.JoinColumn;
import org.eclipse.fennec.persistence.eorm.ManyToOne;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Many To One</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ManyToOneImpl#getJoinColumn <em>Join Column</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ManyToOneImpl#isId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ManyToOneImpl#getMapsId <em>Maps Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ManyToOneImpl#getTargetEntity <em>Target Entity</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ManyToOneImpl extends BaseRefImpl implements ManyToOne {
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
	 * The default value of the '{@link #isId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isId()
	 * @generated
	 * @ordered
	 */
	protected static final boolean ID_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isId()
	 * @generated
	 * @ordered
	 */
	protected boolean id = ID_EDEFAULT;

	/**
	 * This is true if the Id attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean idESet;

	/**
	 * The default value of the '{@link #getMapsId() <em>Maps Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMapsId()
	 * @generated
	 * @ordered
	 */
	protected static final String MAPS_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMapsId() <em>Maps Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMapsId()
	 * @generated
	 * @ordered
	 */
	protected String mapsId = MAPS_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getTargetEntity() <em>Target Entity</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTargetEntity()
	 * @generated
	 * @ordered
	 */
	protected static final String TARGET_ENTITY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTargetEntity() <em>Target Entity</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTargetEntity()
	 * @generated
	 * @ordered
	 */
	protected String targetEntity = TARGET_ENTITY_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ManyToOneImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EORMPackage.eINSTANCE.getManyToOne();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<JoinColumn> getJoinColumn() {
		if (joinColumn == null) {
			joinColumn = new EObjectContainmentEList<JoinColumn>(JoinColumn.class, this, EORMPackage.MANY_TO_ONE__JOIN_COLUMN);
		}
		return joinColumn;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setId(boolean newId) {
		boolean oldId = id;
		id = newId;
		boolean oldIdESet = idESet;
		idESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MANY_TO_ONE__ID, oldId, id, !oldIdESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetId() {
		boolean oldId = id;
		boolean oldIdESet = idESet;
		id = ID_EDEFAULT;
		idESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EORMPackage.MANY_TO_ONE__ID, oldId, ID_EDEFAULT, oldIdESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetId() {
		return idESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getMapsId() {
		return mapsId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMapsId(String newMapsId) {
		String oldMapsId = mapsId;
		mapsId = newMapsId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MANY_TO_ONE__MAPS_ID, oldMapsId, mapsId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getTargetEntity() {
		return targetEntity;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTargetEntity(String newTargetEntity) {
		String oldTargetEntity = targetEntity;
		targetEntity = newTargetEntity;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MANY_TO_ONE__TARGET_ENTITY, oldTargetEntity, targetEntity));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EORMPackage.MANY_TO_ONE__JOIN_COLUMN:
				return ((InternalEList<?>)getJoinColumn()).basicRemove(otherEnd, msgs);
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
			case EORMPackage.MANY_TO_ONE__JOIN_COLUMN:
				return getJoinColumn();
			case EORMPackage.MANY_TO_ONE__ID:
				return isId();
			case EORMPackage.MANY_TO_ONE__MAPS_ID:
				return getMapsId();
			case EORMPackage.MANY_TO_ONE__TARGET_ENTITY:
				return getTargetEntity();
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
			case EORMPackage.MANY_TO_ONE__JOIN_COLUMN:
				getJoinColumn().clear();
				getJoinColumn().addAll((Collection<? extends JoinColumn>)newValue);
				return;
			case EORMPackage.MANY_TO_ONE__ID:
				setId((Boolean)newValue);
				return;
			case EORMPackage.MANY_TO_ONE__MAPS_ID:
				setMapsId((String)newValue);
				return;
			case EORMPackage.MANY_TO_ONE__TARGET_ENTITY:
				setTargetEntity((String)newValue);
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
			case EORMPackage.MANY_TO_ONE__JOIN_COLUMN:
				getJoinColumn().clear();
				return;
			case EORMPackage.MANY_TO_ONE__ID:
				unsetId();
				return;
			case EORMPackage.MANY_TO_ONE__MAPS_ID:
				setMapsId(MAPS_ID_EDEFAULT);
				return;
			case EORMPackage.MANY_TO_ONE__TARGET_ENTITY:
				setTargetEntity(TARGET_ENTITY_EDEFAULT);
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
			case EORMPackage.MANY_TO_ONE__JOIN_COLUMN:
				return joinColumn != null && !joinColumn.isEmpty();
			case EORMPackage.MANY_TO_ONE__ID:
				return isSetId();
			case EORMPackage.MANY_TO_ONE__MAPS_ID:
				return MAPS_ID_EDEFAULT == null ? mapsId != null : !MAPS_ID_EDEFAULT.equals(mapsId);
			case EORMPackage.MANY_TO_ONE__TARGET_ENTITY:
				return TARGET_ENTITY_EDEFAULT == null ? targetEntity != null : !TARGET_ENTITY_EDEFAULT.equals(targetEntity);
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
		result.append(" (id: ");
		if (idESet) result.append(id); else result.append("<unset>");
		result.append(", mapsId: ");
		result.append(mapsId);
		result.append(", targetEntity: ");
		result.append(targetEntity);
		result.append(')');
		return result.toString();
	}

} //ManyToOneImpl
