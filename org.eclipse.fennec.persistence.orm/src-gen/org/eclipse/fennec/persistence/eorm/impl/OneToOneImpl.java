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
import org.eclipse.fennec.persistence.eorm.ForeignKey;
import org.eclipse.fennec.persistence.eorm.JoinColumn;
import org.eclipse.fennec.persistence.eorm.OneToOne;
import org.eclipse.fennec.persistence.eorm.PrimaryKeyJoinColumn;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>One To One</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.OneToOneImpl#getMappedBy <em>Mapped By</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.OneToOneImpl#getPrimaryKeyJoinColumn <em>Primary Key Join Column</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.OneToOneImpl#getPrimaryKeyForeignKey <em>Primary Key Foreign Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.OneToOneImpl#getJoinColumn <em>Join Column</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.OneToOneImpl#isId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.OneToOneImpl#getMapsId <em>Maps Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.OneToOneImpl#isOrphanRemoval <em>Orphan Removal</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.OneToOneImpl#getTargetEntity <em>Target Entity</em>}</li>
 * </ul>
 *
 * @generated
 */
public class OneToOneImpl extends BaseRefImpl implements OneToOne {
	/**
	 * The default value of the '{@link #getMappedBy() <em>Mapped By</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMappedBy()
	 * @generated
	 * @ordered
	 */
	protected static final String MAPPED_BY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMappedBy() <em>Mapped By</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMappedBy()
	 * @generated
	 * @ordered
	 */
	protected String mappedBy = MAPPED_BY_EDEFAULT;

	/**
	 * The cached value of the '{@link #getPrimaryKeyJoinColumn() <em>Primary Key Join Column</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrimaryKeyJoinColumn()
	 * @generated
	 * @ordered
	 */
	protected EList<PrimaryKeyJoinColumn> primaryKeyJoinColumn;

	/**
	 * The cached value of the '{@link #getPrimaryKeyForeignKey() <em>Primary Key Foreign Key</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrimaryKeyForeignKey()
	 * @generated
	 * @ordered
	 */
	protected ForeignKey primaryKeyForeignKey;

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
	 * The default value of the '{@link #isOrphanRemoval() <em>Orphan Removal</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isOrphanRemoval()
	 * @generated
	 * @ordered
	 */
	protected static final boolean ORPHAN_REMOVAL_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isOrphanRemoval() <em>Orphan Removal</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isOrphanRemoval()
	 * @generated
	 * @ordered
	 */
	protected boolean orphanRemoval = ORPHAN_REMOVAL_EDEFAULT;

	/**
	 * This is true if the Orphan Removal attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean orphanRemovalESet;

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
	protected OneToOneImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EORMPackage.eINSTANCE.getOneToOne();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getMappedBy() {
		return mappedBy;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMappedBy(String newMappedBy) {
		String oldMappedBy = mappedBy;
		mappedBy = newMappedBy;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ONE_TO_ONE__MAPPED_BY, oldMappedBy, mappedBy));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<PrimaryKeyJoinColumn> getPrimaryKeyJoinColumn() {
		if (primaryKeyJoinColumn == null) {
			primaryKeyJoinColumn = new EObjectContainmentEList<PrimaryKeyJoinColumn>(PrimaryKeyJoinColumn.class, this, EORMPackage.ONE_TO_ONE__PRIMARY_KEY_JOIN_COLUMN);
		}
		return primaryKeyJoinColumn;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ForeignKey getPrimaryKeyForeignKey() {
		return primaryKeyForeignKey;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPrimaryKeyForeignKey(ForeignKey newPrimaryKeyForeignKey, NotificationChain msgs) {
		ForeignKey oldPrimaryKeyForeignKey = primaryKeyForeignKey;
		primaryKeyForeignKey = newPrimaryKeyForeignKey;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.ONE_TO_ONE__PRIMARY_KEY_FOREIGN_KEY, oldPrimaryKeyForeignKey, newPrimaryKeyForeignKey);
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
	public void setPrimaryKeyForeignKey(ForeignKey newPrimaryKeyForeignKey) {
		if (newPrimaryKeyForeignKey != primaryKeyForeignKey) {
			NotificationChain msgs = null;
			if (primaryKeyForeignKey != null)
				msgs = ((InternalEObject)primaryKeyForeignKey).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.ONE_TO_ONE__PRIMARY_KEY_FOREIGN_KEY, null, msgs);
			if (newPrimaryKeyForeignKey != null)
				msgs = ((InternalEObject)newPrimaryKeyForeignKey).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.ONE_TO_ONE__PRIMARY_KEY_FOREIGN_KEY, null, msgs);
			msgs = basicSetPrimaryKeyForeignKey(newPrimaryKeyForeignKey, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ONE_TO_ONE__PRIMARY_KEY_FOREIGN_KEY, newPrimaryKeyForeignKey, newPrimaryKeyForeignKey));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<JoinColumn> getJoinColumn() {
		if (joinColumn == null) {
			joinColumn = new EObjectContainmentEList<JoinColumn>(JoinColumn.class, this, EORMPackage.ONE_TO_ONE__JOIN_COLUMN);
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ONE_TO_ONE__ID, oldId, id, !oldIdESet));
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
			eNotify(new ENotificationImpl(this, Notification.UNSET, EORMPackage.ONE_TO_ONE__ID, oldId, ID_EDEFAULT, oldIdESet));
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ONE_TO_ONE__MAPS_ID, oldMapsId, mapsId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isOrphanRemoval() {
		return orphanRemoval;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setOrphanRemoval(boolean newOrphanRemoval) {
		boolean oldOrphanRemoval = orphanRemoval;
		orphanRemoval = newOrphanRemoval;
		boolean oldOrphanRemovalESet = orphanRemovalESet;
		orphanRemovalESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ONE_TO_ONE__ORPHAN_REMOVAL, oldOrphanRemoval, orphanRemoval, !oldOrphanRemovalESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetOrphanRemoval() {
		boolean oldOrphanRemoval = orphanRemoval;
		boolean oldOrphanRemovalESet = orphanRemovalESet;
		orphanRemoval = ORPHAN_REMOVAL_EDEFAULT;
		orphanRemovalESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EORMPackage.ONE_TO_ONE__ORPHAN_REMOVAL, oldOrphanRemoval, ORPHAN_REMOVAL_EDEFAULT, oldOrphanRemovalESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetOrphanRemoval() {
		return orphanRemovalESet;
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ONE_TO_ONE__TARGET_ENTITY, oldTargetEntity, targetEntity));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EORMPackage.ONE_TO_ONE__PRIMARY_KEY_JOIN_COLUMN:
				return ((InternalEList<?>)getPrimaryKeyJoinColumn()).basicRemove(otherEnd, msgs);
			case EORMPackage.ONE_TO_ONE__PRIMARY_KEY_FOREIGN_KEY:
				return basicSetPrimaryKeyForeignKey(null, msgs);
			case EORMPackage.ONE_TO_ONE__JOIN_COLUMN:
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
			case EORMPackage.ONE_TO_ONE__MAPPED_BY:
				return getMappedBy();
			case EORMPackage.ONE_TO_ONE__PRIMARY_KEY_JOIN_COLUMN:
				return getPrimaryKeyJoinColumn();
			case EORMPackage.ONE_TO_ONE__PRIMARY_KEY_FOREIGN_KEY:
				return getPrimaryKeyForeignKey();
			case EORMPackage.ONE_TO_ONE__JOIN_COLUMN:
				return getJoinColumn();
			case EORMPackage.ONE_TO_ONE__ID:
				return isId();
			case EORMPackage.ONE_TO_ONE__MAPS_ID:
				return getMapsId();
			case EORMPackage.ONE_TO_ONE__ORPHAN_REMOVAL:
				return isOrphanRemoval();
			case EORMPackage.ONE_TO_ONE__TARGET_ENTITY:
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
			case EORMPackage.ONE_TO_ONE__MAPPED_BY:
				setMappedBy((String)newValue);
				return;
			case EORMPackage.ONE_TO_ONE__PRIMARY_KEY_JOIN_COLUMN:
				getPrimaryKeyJoinColumn().clear();
				getPrimaryKeyJoinColumn().addAll((Collection<? extends PrimaryKeyJoinColumn>)newValue);
				return;
			case EORMPackage.ONE_TO_ONE__PRIMARY_KEY_FOREIGN_KEY:
				setPrimaryKeyForeignKey((ForeignKey)newValue);
				return;
			case EORMPackage.ONE_TO_ONE__JOIN_COLUMN:
				getJoinColumn().clear();
				getJoinColumn().addAll((Collection<? extends JoinColumn>)newValue);
				return;
			case EORMPackage.ONE_TO_ONE__ID:
				setId((Boolean)newValue);
				return;
			case EORMPackage.ONE_TO_ONE__MAPS_ID:
				setMapsId((String)newValue);
				return;
			case EORMPackage.ONE_TO_ONE__ORPHAN_REMOVAL:
				setOrphanRemoval((Boolean)newValue);
				return;
			case EORMPackage.ONE_TO_ONE__TARGET_ENTITY:
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
			case EORMPackage.ONE_TO_ONE__MAPPED_BY:
				setMappedBy(MAPPED_BY_EDEFAULT);
				return;
			case EORMPackage.ONE_TO_ONE__PRIMARY_KEY_JOIN_COLUMN:
				getPrimaryKeyJoinColumn().clear();
				return;
			case EORMPackage.ONE_TO_ONE__PRIMARY_KEY_FOREIGN_KEY:
				setPrimaryKeyForeignKey((ForeignKey)null);
				return;
			case EORMPackage.ONE_TO_ONE__JOIN_COLUMN:
				getJoinColumn().clear();
				return;
			case EORMPackage.ONE_TO_ONE__ID:
				unsetId();
				return;
			case EORMPackage.ONE_TO_ONE__MAPS_ID:
				setMapsId(MAPS_ID_EDEFAULT);
				return;
			case EORMPackage.ONE_TO_ONE__ORPHAN_REMOVAL:
				unsetOrphanRemoval();
				return;
			case EORMPackage.ONE_TO_ONE__TARGET_ENTITY:
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
			case EORMPackage.ONE_TO_ONE__MAPPED_BY:
				return MAPPED_BY_EDEFAULT == null ? mappedBy != null : !MAPPED_BY_EDEFAULT.equals(mappedBy);
			case EORMPackage.ONE_TO_ONE__PRIMARY_KEY_JOIN_COLUMN:
				return primaryKeyJoinColumn != null && !primaryKeyJoinColumn.isEmpty();
			case EORMPackage.ONE_TO_ONE__PRIMARY_KEY_FOREIGN_KEY:
				return primaryKeyForeignKey != null;
			case EORMPackage.ONE_TO_ONE__JOIN_COLUMN:
				return joinColumn != null && !joinColumn.isEmpty();
			case EORMPackage.ONE_TO_ONE__ID:
				return isSetId();
			case EORMPackage.ONE_TO_ONE__MAPS_ID:
				return MAPS_ID_EDEFAULT == null ? mapsId != null : !MAPS_ID_EDEFAULT.equals(mapsId);
			case EORMPackage.ONE_TO_ONE__ORPHAN_REMOVAL:
				return isSetOrphanRemoval();
			case EORMPackage.ONE_TO_ONE__TARGET_ENTITY:
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
		result.append(" (mappedBy: ");
		result.append(mappedBy);
		result.append(", id: ");
		if (idESet) result.append(id); else result.append("<unset>");
		result.append(", mapsId: ");
		result.append(mapsId);
		result.append(", orphanRemoval: ");
		if (orphanRemovalESet) result.append(orphanRemoval); else result.append("<unset>");
		result.append(", targetEntity: ");
		result.append(targetEntity);
		result.append(')');
		return result.toString();
	}

} //OneToOneImpl
