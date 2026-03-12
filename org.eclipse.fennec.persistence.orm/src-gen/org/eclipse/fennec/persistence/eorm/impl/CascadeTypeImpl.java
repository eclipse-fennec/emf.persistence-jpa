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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.persistence.eorm.CascadeType;
import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.EmptyType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Cascade Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.CascadeTypeImpl#getCascadeAll <em>Cascade All</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.CascadeTypeImpl#getCascadePersist <em>Cascade Persist</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.CascadeTypeImpl#getCascadeMerge <em>Cascade Merge</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.CascadeTypeImpl#getCascadeRemove <em>Cascade Remove</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.CascadeTypeImpl#getCascadeRefresh <em>Cascade Refresh</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.CascadeTypeImpl#getCascadeDetach <em>Cascade Detach</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CascadeTypeImpl extends MinimalEObjectImpl.Container implements CascadeType {
	/**
	 * The cached value of the '{@link #getCascadeAll() <em>Cascade All</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCascadeAll()
	 * @generated
	 * @ordered
	 */
	protected EmptyType cascadeAll;

	/**
	 * The cached value of the '{@link #getCascadePersist() <em>Cascade Persist</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCascadePersist()
	 * @generated
	 * @ordered
	 */
	protected EmptyType cascadePersist;

	/**
	 * The cached value of the '{@link #getCascadeMerge() <em>Cascade Merge</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCascadeMerge()
	 * @generated
	 * @ordered
	 */
	protected EmptyType cascadeMerge;

	/**
	 * The cached value of the '{@link #getCascadeRemove() <em>Cascade Remove</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCascadeRemove()
	 * @generated
	 * @ordered
	 */
	protected EmptyType cascadeRemove;

	/**
	 * The cached value of the '{@link #getCascadeRefresh() <em>Cascade Refresh</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCascadeRefresh()
	 * @generated
	 * @ordered
	 */
	protected EmptyType cascadeRefresh;

	/**
	 * The cached value of the '{@link #getCascadeDetach() <em>Cascade Detach</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCascadeDetach()
	 * @generated
	 * @ordered
	 */
	protected EmptyType cascadeDetach;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CascadeTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EORMPackage.eINSTANCE.getCascadeType();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EmptyType getCascadeAll() {
		return cascadeAll;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetCascadeAll(EmptyType newCascadeAll, NotificationChain msgs) {
		EmptyType oldCascadeAll = cascadeAll;
		cascadeAll = newCascadeAll;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.CASCADE_TYPE__CASCADE_ALL, oldCascadeAll, newCascadeAll);
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
	public void setCascadeAll(EmptyType newCascadeAll) {
		if (newCascadeAll != cascadeAll) {
			NotificationChain msgs = null;
			if (cascadeAll != null)
				msgs = ((InternalEObject)cascadeAll).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.CASCADE_TYPE__CASCADE_ALL, null, msgs);
			if (newCascadeAll != null)
				msgs = ((InternalEObject)newCascadeAll).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.CASCADE_TYPE__CASCADE_ALL, null, msgs);
			msgs = basicSetCascadeAll(newCascadeAll, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.CASCADE_TYPE__CASCADE_ALL, newCascadeAll, newCascadeAll));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EmptyType getCascadePersist() {
		return cascadePersist;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetCascadePersist(EmptyType newCascadePersist, NotificationChain msgs) {
		EmptyType oldCascadePersist = cascadePersist;
		cascadePersist = newCascadePersist;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.CASCADE_TYPE__CASCADE_PERSIST, oldCascadePersist, newCascadePersist);
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
	public void setCascadePersist(EmptyType newCascadePersist) {
		if (newCascadePersist != cascadePersist) {
			NotificationChain msgs = null;
			if (cascadePersist != null)
				msgs = ((InternalEObject)cascadePersist).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.CASCADE_TYPE__CASCADE_PERSIST, null, msgs);
			if (newCascadePersist != null)
				msgs = ((InternalEObject)newCascadePersist).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.CASCADE_TYPE__CASCADE_PERSIST, null, msgs);
			msgs = basicSetCascadePersist(newCascadePersist, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.CASCADE_TYPE__CASCADE_PERSIST, newCascadePersist, newCascadePersist));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EmptyType getCascadeMerge() {
		return cascadeMerge;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetCascadeMerge(EmptyType newCascadeMerge, NotificationChain msgs) {
		EmptyType oldCascadeMerge = cascadeMerge;
		cascadeMerge = newCascadeMerge;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.CASCADE_TYPE__CASCADE_MERGE, oldCascadeMerge, newCascadeMerge);
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
	public void setCascadeMerge(EmptyType newCascadeMerge) {
		if (newCascadeMerge != cascadeMerge) {
			NotificationChain msgs = null;
			if (cascadeMerge != null)
				msgs = ((InternalEObject)cascadeMerge).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.CASCADE_TYPE__CASCADE_MERGE, null, msgs);
			if (newCascadeMerge != null)
				msgs = ((InternalEObject)newCascadeMerge).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.CASCADE_TYPE__CASCADE_MERGE, null, msgs);
			msgs = basicSetCascadeMerge(newCascadeMerge, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.CASCADE_TYPE__CASCADE_MERGE, newCascadeMerge, newCascadeMerge));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EmptyType getCascadeRemove() {
		return cascadeRemove;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetCascadeRemove(EmptyType newCascadeRemove, NotificationChain msgs) {
		EmptyType oldCascadeRemove = cascadeRemove;
		cascadeRemove = newCascadeRemove;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.CASCADE_TYPE__CASCADE_REMOVE, oldCascadeRemove, newCascadeRemove);
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
	public void setCascadeRemove(EmptyType newCascadeRemove) {
		if (newCascadeRemove != cascadeRemove) {
			NotificationChain msgs = null;
			if (cascadeRemove != null)
				msgs = ((InternalEObject)cascadeRemove).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.CASCADE_TYPE__CASCADE_REMOVE, null, msgs);
			if (newCascadeRemove != null)
				msgs = ((InternalEObject)newCascadeRemove).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.CASCADE_TYPE__CASCADE_REMOVE, null, msgs);
			msgs = basicSetCascadeRemove(newCascadeRemove, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.CASCADE_TYPE__CASCADE_REMOVE, newCascadeRemove, newCascadeRemove));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EmptyType getCascadeRefresh() {
		return cascadeRefresh;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetCascadeRefresh(EmptyType newCascadeRefresh, NotificationChain msgs) {
		EmptyType oldCascadeRefresh = cascadeRefresh;
		cascadeRefresh = newCascadeRefresh;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.CASCADE_TYPE__CASCADE_REFRESH, oldCascadeRefresh, newCascadeRefresh);
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
	public void setCascadeRefresh(EmptyType newCascadeRefresh) {
		if (newCascadeRefresh != cascadeRefresh) {
			NotificationChain msgs = null;
			if (cascadeRefresh != null)
				msgs = ((InternalEObject)cascadeRefresh).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.CASCADE_TYPE__CASCADE_REFRESH, null, msgs);
			if (newCascadeRefresh != null)
				msgs = ((InternalEObject)newCascadeRefresh).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.CASCADE_TYPE__CASCADE_REFRESH, null, msgs);
			msgs = basicSetCascadeRefresh(newCascadeRefresh, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.CASCADE_TYPE__CASCADE_REFRESH, newCascadeRefresh, newCascadeRefresh));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EmptyType getCascadeDetach() {
		return cascadeDetach;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetCascadeDetach(EmptyType newCascadeDetach, NotificationChain msgs) {
		EmptyType oldCascadeDetach = cascadeDetach;
		cascadeDetach = newCascadeDetach;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.CASCADE_TYPE__CASCADE_DETACH, oldCascadeDetach, newCascadeDetach);
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
	public void setCascadeDetach(EmptyType newCascadeDetach) {
		if (newCascadeDetach != cascadeDetach) {
			NotificationChain msgs = null;
			if (cascadeDetach != null)
				msgs = ((InternalEObject)cascadeDetach).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.CASCADE_TYPE__CASCADE_DETACH, null, msgs);
			if (newCascadeDetach != null)
				msgs = ((InternalEObject)newCascadeDetach).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.CASCADE_TYPE__CASCADE_DETACH, null, msgs);
			msgs = basicSetCascadeDetach(newCascadeDetach, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.CASCADE_TYPE__CASCADE_DETACH, newCascadeDetach, newCascadeDetach));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EORMPackage.CASCADE_TYPE__CASCADE_ALL:
				return basicSetCascadeAll(null, msgs);
			case EORMPackage.CASCADE_TYPE__CASCADE_PERSIST:
				return basicSetCascadePersist(null, msgs);
			case EORMPackage.CASCADE_TYPE__CASCADE_MERGE:
				return basicSetCascadeMerge(null, msgs);
			case EORMPackage.CASCADE_TYPE__CASCADE_REMOVE:
				return basicSetCascadeRemove(null, msgs);
			case EORMPackage.CASCADE_TYPE__CASCADE_REFRESH:
				return basicSetCascadeRefresh(null, msgs);
			case EORMPackage.CASCADE_TYPE__CASCADE_DETACH:
				return basicSetCascadeDetach(null, msgs);
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
			case EORMPackage.CASCADE_TYPE__CASCADE_ALL:
				return getCascadeAll();
			case EORMPackage.CASCADE_TYPE__CASCADE_PERSIST:
				return getCascadePersist();
			case EORMPackage.CASCADE_TYPE__CASCADE_MERGE:
				return getCascadeMerge();
			case EORMPackage.CASCADE_TYPE__CASCADE_REMOVE:
				return getCascadeRemove();
			case EORMPackage.CASCADE_TYPE__CASCADE_REFRESH:
				return getCascadeRefresh();
			case EORMPackage.CASCADE_TYPE__CASCADE_DETACH:
				return getCascadeDetach();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case EORMPackage.CASCADE_TYPE__CASCADE_ALL:
				setCascadeAll((EmptyType)newValue);
				return;
			case EORMPackage.CASCADE_TYPE__CASCADE_PERSIST:
				setCascadePersist((EmptyType)newValue);
				return;
			case EORMPackage.CASCADE_TYPE__CASCADE_MERGE:
				setCascadeMerge((EmptyType)newValue);
				return;
			case EORMPackage.CASCADE_TYPE__CASCADE_REMOVE:
				setCascadeRemove((EmptyType)newValue);
				return;
			case EORMPackage.CASCADE_TYPE__CASCADE_REFRESH:
				setCascadeRefresh((EmptyType)newValue);
				return;
			case EORMPackage.CASCADE_TYPE__CASCADE_DETACH:
				setCascadeDetach((EmptyType)newValue);
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
			case EORMPackage.CASCADE_TYPE__CASCADE_ALL:
				setCascadeAll((EmptyType)null);
				return;
			case EORMPackage.CASCADE_TYPE__CASCADE_PERSIST:
				setCascadePersist((EmptyType)null);
				return;
			case EORMPackage.CASCADE_TYPE__CASCADE_MERGE:
				setCascadeMerge((EmptyType)null);
				return;
			case EORMPackage.CASCADE_TYPE__CASCADE_REMOVE:
				setCascadeRemove((EmptyType)null);
				return;
			case EORMPackage.CASCADE_TYPE__CASCADE_REFRESH:
				setCascadeRefresh((EmptyType)null);
				return;
			case EORMPackage.CASCADE_TYPE__CASCADE_DETACH:
				setCascadeDetach((EmptyType)null);
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
			case EORMPackage.CASCADE_TYPE__CASCADE_ALL:
				return cascadeAll != null;
			case EORMPackage.CASCADE_TYPE__CASCADE_PERSIST:
				return cascadePersist != null;
			case EORMPackage.CASCADE_TYPE__CASCADE_MERGE:
				return cascadeMerge != null;
			case EORMPackage.CASCADE_TYPE__CASCADE_REMOVE:
				return cascadeRemove != null;
			case EORMPackage.CASCADE_TYPE__CASCADE_REFRESH:
				return cascadeRefresh != null;
			case EORMPackage.CASCADE_TYPE__CASCADE_DETACH:
				return cascadeDetach != null;
		}
		return super.eIsSet(featureID);
	}

} //CascadeTypeImpl
