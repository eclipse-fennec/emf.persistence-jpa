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

import org.eclipse.fennec.persistence.eorm.AttributeOverride;
import org.eclipse.fennec.persistence.eorm.Convert;
import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.EnumType;
import org.eclipse.fennec.persistence.eorm.ForeignKey;
import org.eclipse.fennec.persistence.eorm.ManyToMany;
import org.eclipse.fennec.persistence.eorm.MapKey;
import org.eclipse.fennec.persistence.eorm.MapKeyClass;
import org.eclipse.fennec.persistence.eorm.MapKeyColumn;
import org.eclipse.fennec.persistence.eorm.MapKeyJoinColumn;
import org.eclipse.fennec.persistence.eorm.OrderColumn;
import org.eclipse.fennec.persistence.eorm.TemporalType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Many To Many</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ManyToManyImpl#getMappedBy <em>Mapped By</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ManyToManyImpl#getOrderBy <em>Order By</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ManyToManyImpl#getOrderColumn <em>Order Column</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ManyToManyImpl#getMapKey <em>Map Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ManyToManyImpl#getMapKeyClass <em>Map Key Class</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ManyToManyImpl#getMapKeyTemporal <em>Map Key Temporal</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ManyToManyImpl#getMapKeyEnumerated <em>Map Key Enumerated</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ManyToManyImpl#getMapKeyAttributeOverride <em>Map Key Attribute Override</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ManyToManyImpl#getMapKeyConvert <em>Map Key Convert</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ManyToManyImpl#getMapKeyColumn <em>Map Key Column</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ManyToManyImpl#getMapKeyJoinColumn <em>Map Key Join Column</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ManyToManyImpl#getMapKeyForeignKey <em>Map Key Foreign Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ManyToManyImpl#getTargetEntity <em>Target Entity</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ManyToManyImpl extends BaseRefImpl implements ManyToMany {
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
	 * The default value of the '{@link #getOrderBy() <em>Order By</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOrderBy()
	 * @generated
	 * @ordered
	 */
	protected static final String ORDER_BY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getOrderBy() <em>Order By</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOrderBy()
	 * @generated
	 * @ordered
	 */
	protected String orderBy = ORDER_BY_EDEFAULT;

	/**
	 * The cached value of the '{@link #getOrderColumn() <em>Order Column</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOrderColumn()
	 * @generated
	 * @ordered
	 */
	protected OrderColumn orderColumn;

	/**
	 * The cached value of the '{@link #getMapKey() <em>Map Key</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMapKey()
	 * @generated
	 * @ordered
	 */
	protected MapKey mapKey;

	/**
	 * The cached value of the '{@link #getMapKeyClass() <em>Map Key Class</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMapKeyClass()
	 * @generated
	 * @ordered
	 */
	protected MapKeyClass mapKeyClass;

	/**
	 * The default value of the '{@link #getMapKeyTemporal() <em>Map Key Temporal</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMapKeyTemporal()
	 * @generated
	 * @ordered
	 */
	protected static final TemporalType MAP_KEY_TEMPORAL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMapKeyTemporal() <em>Map Key Temporal</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMapKeyTemporal()
	 * @generated
	 * @ordered
	 */
	protected TemporalType mapKeyTemporal = MAP_KEY_TEMPORAL_EDEFAULT;

	/**
	 * The default value of the '{@link #getMapKeyEnumerated() <em>Map Key Enumerated</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMapKeyEnumerated()
	 * @generated
	 * @ordered
	 */
	protected static final EnumType MAP_KEY_ENUMERATED_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMapKeyEnumerated() <em>Map Key Enumerated</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMapKeyEnumerated()
	 * @generated
	 * @ordered
	 */
	protected EnumType mapKeyEnumerated = MAP_KEY_ENUMERATED_EDEFAULT;

	/**
	 * The cached value of the '{@link #getMapKeyAttributeOverride() <em>Map Key Attribute Override</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMapKeyAttributeOverride()
	 * @generated
	 * @ordered
	 */
	protected EList<AttributeOverride> mapKeyAttributeOverride;

	/**
	 * The cached value of the '{@link #getMapKeyConvert() <em>Map Key Convert</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMapKeyConvert()
	 * @generated
	 * @ordered
	 */
	protected EList<Convert> mapKeyConvert;

	/**
	 * The cached value of the '{@link #getMapKeyColumn() <em>Map Key Column</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMapKeyColumn()
	 * @generated
	 * @ordered
	 */
	protected MapKeyColumn mapKeyColumn;

	/**
	 * The cached value of the '{@link #getMapKeyJoinColumn() <em>Map Key Join Column</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMapKeyJoinColumn()
	 * @generated
	 * @ordered
	 */
	protected EList<MapKeyJoinColumn> mapKeyJoinColumn;

	/**
	 * The cached value of the '{@link #getMapKeyForeignKey() <em>Map Key Foreign Key</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMapKeyForeignKey()
	 * @generated
	 * @ordered
	 */
	protected ForeignKey mapKeyForeignKey;

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
	protected ManyToManyImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EORMPackage.eINSTANCE.getManyToMany();
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MANY_TO_MANY__MAPPED_BY, oldMappedBy, mappedBy));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getOrderBy() {
		return orderBy;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setOrderBy(String newOrderBy) {
		String oldOrderBy = orderBy;
		orderBy = newOrderBy;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MANY_TO_MANY__ORDER_BY, oldOrderBy, orderBy));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public OrderColumn getOrderColumn() {
		return orderColumn;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetOrderColumn(OrderColumn newOrderColumn, NotificationChain msgs) {
		OrderColumn oldOrderColumn = orderColumn;
		orderColumn = newOrderColumn;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.MANY_TO_MANY__ORDER_COLUMN, oldOrderColumn, newOrderColumn);
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
	public void setOrderColumn(OrderColumn newOrderColumn) {
		if (newOrderColumn != orderColumn) {
			NotificationChain msgs = null;
			if (orderColumn != null)
				msgs = ((InternalEObject)orderColumn).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MANY_TO_MANY__ORDER_COLUMN, null, msgs);
			if (newOrderColumn != null)
				msgs = ((InternalEObject)newOrderColumn).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MANY_TO_MANY__ORDER_COLUMN, null, msgs);
			msgs = basicSetOrderColumn(newOrderColumn, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MANY_TO_MANY__ORDER_COLUMN, newOrderColumn, newOrderColumn));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MapKey getMapKey() {
		return mapKey;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetMapKey(MapKey newMapKey, NotificationChain msgs) {
		MapKey oldMapKey = mapKey;
		mapKey = newMapKey;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.MANY_TO_MANY__MAP_KEY, oldMapKey, newMapKey);
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
	public void setMapKey(MapKey newMapKey) {
		if (newMapKey != mapKey) {
			NotificationChain msgs = null;
			if (mapKey != null)
				msgs = ((InternalEObject)mapKey).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MANY_TO_MANY__MAP_KEY, null, msgs);
			if (newMapKey != null)
				msgs = ((InternalEObject)newMapKey).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MANY_TO_MANY__MAP_KEY, null, msgs);
			msgs = basicSetMapKey(newMapKey, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MANY_TO_MANY__MAP_KEY, newMapKey, newMapKey));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MapKeyClass getMapKeyClass() {
		return mapKeyClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetMapKeyClass(MapKeyClass newMapKeyClass, NotificationChain msgs) {
		MapKeyClass oldMapKeyClass = mapKeyClass;
		mapKeyClass = newMapKeyClass;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.MANY_TO_MANY__MAP_KEY_CLASS, oldMapKeyClass, newMapKeyClass);
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
	public void setMapKeyClass(MapKeyClass newMapKeyClass) {
		if (newMapKeyClass != mapKeyClass) {
			NotificationChain msgs = null;
			if (mapKeyClass != null)
				msgs = ((InternalEObject)mapKeyClass).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MANY_TO_MANY__MAP_KEY_CLASS, null, msgs);
			if (newMapKeyClass != null)
				msgs = ((InternalEObject)newMapKeyClass).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MANY_TO_MANY__MAP_KEY_CLASS, null, msgs);
			msgs = basicSetMapKeyClass(newMapKeyClass, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MANY_TO_MANY__MAP_KEY_CLASS, newMapKeyClass, newMapKeyClass));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TemporalType getMapKeyTemporal() {
		return mapKeyTemporal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMapKeyTemporal(TemporalType newMapKeyTemporal) {
		TemporalType oldMapKeyTemporal = mapKeyTemporal;
		mapKeyTemporal = newMapKeyTemporal;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MANY_TO_MANY__MAP_KEY_TEMPORAL, oldMapKeyTemporal, mapKeyTemporal));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EnumType getMapKeyEnumerated() {
		return mapKeyEnumerated;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMapKeyEnumerated(EnumType newMapKeyEnumerated) {
		EnumType oldMapKeyEnumerated = mapKeyEnumerated;
		mapKeyEnumerated = newMapKeyEnumerated;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MANY_TO_MANY__MAP_KEY_ENUMERATED, oldMapKeyEnumerated, mapKeyEnumerated));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<AttributeOverride> getMapKeyAttributeOverride() {
		if (mapKeyAttributeOverride == null) {
			mapKeyAttributeOverride = new EObjectContainmentEList<AttributeOverride>(AttributeOverride.class, this, EORMPackage.MANY_TO_MANY__MAP_KEY_ATTRIBUTE_OVERRIDE);
		}
		return mapKeyAttributeOverride;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Convert> getMapKeyConvert() {
		if (mapKeyConvert == null) {
			mapKeyConvert = new EObjectContainmentEList<Convert>(Convert.class, this, EORMPackage.MANY_TO_MANY__MAP_KEY_CONVERT);
		}
		return mapKeyConvert;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MapKeyColumn getMapKeyColumn() {
		return mapKeyColumn;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetMapKeyColumn(MapKeyColumn newMapKeyColumn, NotificationChain msgs) {
		MapKeyColumn oldMapKeyColumn = mapKeyColumn;
		mapKeyColumn = newMapKeyColumn;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.MANY_TO_MANY__MAP_KEY_COLUMN, oldMapKeyColumn, newMapKeyColumn);
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
	public void setMapKeyColumn(MapKeyColumn newMapKeyColumn) {
		if (newMapKeyColumn != mapKeyColumn) {
			NotificationChain msgs = null;
			if (mapKeyColumn != null)
				msgs = ((InternalEObject)mapKeyColumn).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MANY_TO_MANY__MAP_KEY_COLUMN, null, msgs);
			if (newMapKeyColumn != null)
				msgs = ((InternalEObject)newMapKeyColumn).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MANY_TO_MANY__MAP_KEY_COLUMN, null, msgs);
			msgs = basicSetMapKeyColumn(newMapKeyColumn, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MANY_TO_MANY__MAP_KEY_COLUMN, newMapKeyColumn, newMapKeyColumn));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<MapKeyJoinColumn> getMapKeyJoinColumn() {
		if (mapKeyJoinColumn == null) {
			mapKeyJoinColumn = new EObjectContainmentEList<MapKeyJoinColumn>(MapKeyJoinColumn.class, this, EORMPackage.MANY_TO_MANY__MAP_KEY_JOIN_COLUMN);
		}
		return mapKeyJoinColumn;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ForeignKey getMapKeyForeignKey() {
		return mapKeyForeignKey;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetMapKeyForeignKey(ForeignKey newMapKeyForeignKey, NotificationChain msgs) {
		ForeignKey oldMapKeyForeignKey = mapKeyForeignKey;
		mapKeyForeignKey = newMapKeyForeignKey;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.MANY_TO_MANY__MAP_KEY_FOREIGN_KEY, oldMapKeyForeignKey, newMapKeyForeignKey);
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
	public void setMapKeyForeignKey(ForeignKey newMapKeyForeignKey) {
		if (newMapKeyForeignKey != mapKeyForeignKey) {
			NotificationChain msgs = null;
			if (mapKeyForeignKey != null)
				msgs = ((InternalEObject)mapKeyForeignKey).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MANY_TO_MANY__MAP_KEY_FOREIGN_KEY, null, msgs);
			if (newMapKeyForeignKey != null)
				msgs = ((InternalEObject)newMapKeyForeignKey).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MANY_TO_MANY__MAP_KEY_FOREIGN_KEY, null, msgs);
			msgs = basicSetMapKeyForeignKey(newMapKeyForeignKey, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MANY_TO_MANY__MAP_KEY_FOREIGN_KEY, newMapKeyForeignKey, newMapKeyForeignKey));
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MANY_TO_MANY__TARGET_ENTITY, oldTargetEntity, targetEntity));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EORMPackage.MANY_TO_MANY__ORDER_COLUMN:
				return basicSetOrderColumn(null, msgs);
			case EORMPackage.MANY_TO_MANY__MAP_KEY:
				return basicSetMapKey(null, msgs);
			case EORMPackage.MANY_TO_MANY__MAP_KEY_CLASS:
				return basicSetMapKeyClass(null, msgs);
			case EORMPackage.MANY_TO_MANY__MAP_KEY_ATTRIBUTE_OVERRIDE:
				return ((InternalEList<?>)getMapKeyAttributeOverride()).basicRemove(otherEnd, msgs);
			case EORMPackage.MANY_TO_MANY__MAP_KEY_CONVERT:
				return ((InternalEList<?>)getMapKeyConvert()).basicRemove(otherEnd, msgs);
			case EORMPackage.MANY_TO_MANY__MAP_KEY_COLUMN:
				return basicSetMapKeyColumn(null, msgs);
			case EORMPackage.MANY_TO_MANY__MAP_KEY_JOIN_COLUMN:
				return ((InternalEList<?>)getMapKeyJoinColumn()).basicRemove(otherEnd, msgs);
			case EORMPackage.MANY_TO_MANY__MAP_KEY_FOREIGN_KEY:
				return basicSetMapKeyForeignKey(null, msgs);
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
			case EORMPackage.MANY_TO_MANY__MAPPED_BY:
				return getMappedBy();
			case EORMPackage.MANY_TO_MANY__ORDER_BY:
				return getOrderBy();
			case EORMPackage.MANY_TO_MANY__ORDER_COLUMN:
				return getOrderColumn();
			case EORMPackage.MANY_TO_MANY__MAP_KEY:
				return getMapKey();
			case EORMPackage.MANY_TO_MANY__MAP_KEY_CLASS:
				return getMapKeyClass();
			case EORMPackage.MANY_TO_MANY__MAP_KEY_TEMPORAL:
				return getMapKeyTemporal();
			case EORMPackage.MANY_TO_MANY__MAP_KEY_ENUMERATED:
				return getMapKeyEnumerated();
			case EORMPackage.MANY_TO_MANY__MAP_KEY_ATTRIBUTE_OVERRIDE:
				return getMapKeyAttributeOverride();
			case EORMPackage.MANY_TO_MANY__MAP_KEY_CONVERT:
				return getMapKeyConvert();
			case EORMPackage.MANY_TO_MANY__MAP_KEY_COLUMN:
				return getMapKeyColumn();
			case EORMPackage.MANY_TO_MANY__MAP_KEY_JOIN_COLUMN:
				return getMapKeyJoinColumn();
			case EORMPackage.MANY_TO_MANY__MAP_KEY_FOREIGN_KEY:
				return getMapKeyForeignKey();
			case EORMPackage.MANY_TO_MANY__TARGET_ENTITY:
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
			case EORMPackage.MANY_TO_MANY__MAPPED_BY:
				setMappedBy((String)newValue);
				return;
			case EORMPackage.MANY_TO_MANY__ORDER_BY:
				setOrderBy((String)newValue);
				return;
			case EORMPackage.MANY_TO_MANY__ORDER_COLUMN:
				setOrderColumn((OrderColumn)newValue);
				return;
			case EORMPackage.MANY_TO_MANY__MAP_KEY:
				setMapKey((MapKey)newValue);
				return;
			case EORMPackage.MANY_TO_MANY__MAP_KEY_CLASS:
				setMapKeyClass((MapKeyClass)newValue);
				return;
			case EORMPackage.MANY_TO_MANY__MAP_KEY_TEMPORAL:
				setMapKeyTemporal((TemporalType)newValue);
				return;
			case EORMPackage.MANY_TO_MANY__MAP_KEY_ENUMERATED:
				setMapKeyEnumerated((EnumType)newValue);
				return;
			case EORMPackage.MANY_TO_MANY__MAP_KEY_ATTRIBUTE_OVERRIDE:
				getMapKeyAttributeOverride().clear();
				getMapKeyAttributeOverride().addAll((Collection<? extends AttributeOverride>)newValue);
				return;
			case EORMPackage.MANY_TO_MANY__MAP_KEY_CONVERT:
				getMapKeyConvert().clear();
				getMapKeyConvert().addAll((Collection<? extends Convert>)newValue);
				return;
			case EORMPackage.MANY_TO_MANY__MAP_KEY_COLUMN:
				setMapKeyColumn((MapKeyColumn)newValue);
				return;
			case EORMPackage.MANY_TO_MANY__MAP_KEY_JOIN_COLUMN:
				getMapKeyJoinColumn().clear();
				getMapKeyJoinColumn().addAll((Collection<? extends MapKeyJoinColumn>)newValue);
				return;
			case EORMPackage.MANY_TO_MANY__MAP_KEY_FOREIGN_KEY:
				setMapKeyForeignKey((ForeignKey)newValue);
				return;
			case EORMPackage.MANY_TO_MANY__TARGET_ENTITY:
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
			case EORMPackage.MANY_TO_MANY__MAPPED_BY:
				setMappedBy(MAPPED_BY_EDEFAULT);
				return;
			case EORMPackage.MANY_TO_MANY__ORDER_BY:
				setOrderBy(ORDER_BY_EDEFAULT);
				return;
			case EORMPackage.MANY_TO_MANY__ORDER_COLUMN:
				setOrderColumn((OrderColumn)null);
				return;
			case EORMPackage.MANY_TO_MANY__MAP_KEY:
				setMapKey((MapKey)null);
				return;
			case EORMPackage.MANY_TO_MANY__MAP_KEY_CLASS:
				setMapKeyClass((MapKeyClass)null);
				return;
			case EORMPackage.MANY_TO_MANY__MAP_KEY_TEMPORAL:
				setMapKeyTemporal(MAP_KEY_TEMPORAL_EDEFAULT);
				return;
			case EORMPackage.MANY_TO_MANY__MAP_KEY_ENUMERATED:
				setMapKeyEnumerated(MAP_KEY_ENUMERATED_EDEFAULT);
				return;
			case EORMPackage.MANY_TO_MANY__MAP_KEY_ATTRIBUTE_OVERRIDE:
				getMapKeyAttributeOverride().clear();
				return;
			case EORMPackage.MANY_TO_MANY__MAP_KEY_CONVERT:
				getMapKeyConvert().clear();
				return;
			case EORMPackage.MANY_TO_MANY__MAP_KEY_COLUMN:
				setMapKeyColumn((MapKeyColumn)null);
				return;
			case EORMPackage.MANY_TO_MANY__MAP_KEY_JOIN_COLUMN:
				getMapKeyJoinColumn().clear();
				return;
			case EORMPackage.MANY_TO_MANY__MAP_KEY_FOREIGN_KEY:
				setMapKeyForeignKey((ForeignKey)null);
				return;
			case EORMPackage.MANY_TO_MANY__TARGET_ENTITY:
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
			case EORMPackage.MANY_TO_MANY__MAPPED_BY:
				return MAPPED_BY_EDEFAULT == null ? mappedBy != null : !MAPPED_BY_EDEFAULT.equals(mappedBy);
			case EORMPackage.MANY_TO_MANY__ORDER_BY:
				return ORDER_BY_EDEFAULT == null ? orderBy != null : !ORDER_BY_EDEFAULT.equals(orderBy);
			case EORMPackage.MANY_TO_MANY__ORDER_COLUMN:
				return orderColumn != null;
			case EORMPackage.MANY_TO_MANY__MAP_KEY:
				return mapKey != null;
			case EORMPackage.MANY_TO_MANY__MAP_KEY_CLASS:
				return mapKeyClass != null;
			case EORMPackage.MANY_TO_MANY__MAP_KEY_TEMPORAL:
				return MAP_KEY_TEMPORAL_EDEFAULT == null ? mapKeyTemporal != null : !MAP_KEY_TEMPORAL_EDEFAULT.equals(mapKeyTemporal);
			case EORMPackage.MANY_TO_MANY__MAP_KEY_ENUMERATED:
				return MAP_KEY_ENUMERATED_EDEFAULT == null ? mapKeyEnumerated != null : !MAP_KEY_ENUMERATED_EDEFAULT.equals(mapKeyEnumerated);
			case EORMPackage.MANY_TO_MANY__MAP_KEY_ATTRIBUTE_OVERRIDE:
				return mapKeyAttributeOverride != null && !mapKeyAttributeOverride.isEmpty();
			case EORMPackage.MANY_TO_MANY__MAP_KEY_CONVERT:
				return mapKeyConvert != null && !mapKeyConvert.isEmpty();
			case EORMPackage.MANY_TO_MANY__MAP_KEY_COLUMN:
				return mapKeyColumn != null;
			case EORMPackage.MANY_TO_MANY__MAP_KEY_JOIN_COLUMN:
				return mapKeyJoinColumn != null && !mapKeyJoinColumn.isEmpty();
			case EORMPackage.MANY_TO_MANY__MAP_KEY_FOREIGN_KEY:
				return mapKeyForeignKey != null;
			case EORMPackage.MANY_TO_MANY__TARGET_ENTITY:
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
		result.append(", orderBy: ");
		result.append(orderBy);
		result.append(", mapKeyTemporal: ");
		result.append(mapKeyTemporal);
		result.append(", mapKeyEnumerated: ");
		result.append(mapKeyEnumerated);
		result.append(", targetEntity: ");
		result.append(targetEntity);
		result.append(')');
		return result.toString();
	}

} //ManyToManyImpl
