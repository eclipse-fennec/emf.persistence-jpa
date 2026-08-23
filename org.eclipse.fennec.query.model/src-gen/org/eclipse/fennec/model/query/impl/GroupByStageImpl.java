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
package org.eclipse.fennec.model.query.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.model.expression.PropertyPath;

import org.eclipse.fennec.model.query.Aggregate;
import org.eclipse.fennec.model.query.GroupByStage;
import org.eclipse.fennec.model.query.GroupKey;
import org.eclipse.fennec.model.query.QueryPackage;
import org.eclipse.fennec.model.query.RepresentativeSpec;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Group By Stage</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.query.impl.GroupByStageImpl#getPaths <em>Paths</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.GroupByStageImpl#getKeys <em>Keys</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.GroupByStageImpl#getAggregates <em>Aggregates</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.GroupByStageImpl#getRepresentatives <em>Representatives</em>}</li>
 * </ul>
 *
 * @generated
 */
public class GroupByStageImpl extends StageImpl implements GroupByStage {
	/**
	 * The cached value of the '{@link #getPaths() <em>Paths</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPaths()
	 * @generated
	 * @ordered
	 */
	protected EList<PropertyPath> paths;

	/**
	 * The cached value of the '{@link #getKeys() <em>Keys</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getKeys()
	 * @generated
	 * @ordered
	 */
	protected EList<GroupKey> keys;

	/**
	 * The cached value of the '{@link #getAggregates() <em>Aggregates</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAggregates()
	 * @generated
	 * @ordered
	 */
	protected EList<Aggregate> aggregates;

	/**
	 * The cached value of the '{@link #getRepresentatives() <em>Representatives</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRepresentatives()
	 * @generated
	 * @ordered
	 */
	protected RepresentativeSpec representatives;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected GroupByStageImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return QueryPackage.Literals.GROUP_BY_STAGE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<PropertyPath> getPaths() {
		if (paths == null) {
			paths = new EObjectContainmentEList<PropertyPath>(PropertyPath.class, this, QueryPackage.GROUP_BY_STAGE__PATHS);
		}
		return paths;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<GroupKey> getKeys() {
		if (keys == null) {
			keys = new EObjectContainmentEList<GroupKey>(GroupKey.class, this, QueryPackage.GROUP_BY_STAGE__KEYS);
		}
		return keys;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Aggregate> getAggregates() {
		if (aggregates == null) {
			aggregates = new EObjectContainmentEList<Aggregate>(Aggregate.class, this, QueryPackage.GROUP_BY_STAGE__AGGREGATES);
		}
		return aggregates;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public RepresentativeSpec getRepresentatives() {
		return representatives;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetRepresentatives(RepresentativeSpec newRepresentatives, NotificationChain msgs) {
		RepresentativeSpec oldRepresentatives = representatives;
		representatives = newRepresentatives;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QueryPackage.GROUP_BY_STAGE__REPRESENTATIVES, oldRepresentatives, newRepresentatives);
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
	public void setRepresentatives(RepresentativeSpec newRepresentatives) {
		if (newRepresentatives != representatives) {
			NotificationChain msgs = null;
			if (representatives != null)
				msgs = ((InternalEObject)representatives).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QueryPackage.GROUP_BY_STAGE__REPRESENTATIVES, null, msgs);
			if (newRepresentatives != null)
				msgs = ((InternalEObject)newRepresentatives).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - QueryPackage.GROUP_BY_STAGE__REPRESENTATIVES, null, msgs);
			msgs = basicSetRepresentatives(newRepresentatives, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.GROUP_BY_STAGE__REPRESENTATIVES, newRepresentatives, newRepresentatives));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case QueryPackage.GROUP_BY_STAGE__PATHS:
				return ((InternalEList<?>)getPaths()).basicRemove(otherEnd, msgs);
			case QueryPackage.GROUP_BY_STAGE__KEYS:
				return ((InternalEList<?>)getKeys()).basicRemove(otherEnd, msgs);
			case QueryPackage.GROUP_BY_STAGE__AGGREGATES:
				return ((InternalEList<?>)getAggregates()).basicRemove(otherEnd, msgs);
			case QueryPackage.GROUP_BY_STAGE__REPRESENTATIVES:
				return basicSetRepresentatives(null, msgs);
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
			case QueryPackage.GROUP_BY_STAGE__PATHS:
				return getPaths();
			case QueryPackage.GROUP_BY_STAGE__KEYS:
				return getKeys();
			case QueryPackage.GROUP_BY_STAGE__AGGREGATES:
				return getAggregates();
			case QueryPackage.GROUP_BY_STAGE__REPRESENTATIVES:
				return getRepresentatives();
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
			case QueryPackage.GROUP_BY_STAGE__PATHS:
				getPaths().clear();
				getPaths().addAll((Collection<? extends PropertyPath>)newValue);
				return;
			case QueryPackage.GROUP_BY_STAGE__KEYS:
				getKeys().clear();
				getKeys().addAll((Collection<? extends GroupKey>)newValue);
				return;
			case QueryPackage.GROUP_BY_STAGE__AGGREGATES:
				getAggregates().clear();
				getAggregates().addAll((Collection<? extends Aggregate>)newValue);
				return;
			case QueryPackage.GROUP_BY_STAGE__REPRESENTATIVES:
				setRepresentatives((RepresentativeSpec)newValue);
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
			case QueryPackage.GROUP_BY_STAGE__PATHS:
				getPaths().clear();
				return;
			case QueryPackage.GROUP_BY_STAGE__KEYS:
				getKeys().clear();
				return;
			case QueryPackage.GROUP_BY_STAGE__AGGREGATES:
				getAggregates().clear();
				return;
			case QueryPackage.GROUP_BY_STAGE__REPRESENTATIVES:
				setRepresentatives((RepresentativeSpec)null);
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
			case QueryPackage.GROUP_BY_STAGE__PATHS:
				return paths != null && !paths.isEmpty();
			case QueryPackage.GROUP_BY_STAGE__KEYS:
				return keys != null && !keys.isEmpty();
			case QueryPackage.GROUP_BY_STAGE__AGGREGATES:
				return aggregates != null && !aggregates.isEmpty();
			case QueryPackage.GROUP_BY_STAGE__REPRESENTATIVES:
				return representatives != null;
		}
		return super.eIsSet(featureID);
	}

} //GroupByStageImpl
