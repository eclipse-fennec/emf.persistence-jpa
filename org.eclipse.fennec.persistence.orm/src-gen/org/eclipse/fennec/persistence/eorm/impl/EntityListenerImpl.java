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

import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.EntityListener;
import org.eclipse.fennec.persistence.eorm.PostLoad;
import org.eclipse.fennec.persistence.eorm.PostPersist;
import org.eclipse.fennec.persistence.eorm.PostRemove;
import org.eclipse.fennec.persistence.eorm.PostUpdate;
import org.eclipse.fennec.persistence.eorm.PrePersist;
import org.eclipse.fennec.persistence.eorm.PreRemove;
import org.eclipse.fennec.persistence.eorm.PreUpdate;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Entity Listener</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityListenerImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityListenerImpl#getPrePersist <em>Pre Persist</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityListenerImpl#getPostPersist <em>Post Persist</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityListenerImpl#getPreRemove <em>Pre Remove</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityListenerImpl#getPostRemove <em>Post Remove</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityListenerImpl#getPreUpdate <em>Pre Update</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityListenerImpl#getPostUpdate <em>Post Update</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityListenerImpl#getPostLoad <em>Post Load</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EntityListenerImpl#getClass_ <em>Class</em>}</li>
 * </ul>
 *
 * @generated
 */
public class EntityListenerImpl extends MinimalEObjectImpl.Container implements EntityListener {
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
	 * The cached value of the '{@link #getPrePersist() <em>Pre Persist</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrePersist()
	 * @generated
	 * @ordered
	 */
	protected PrePersist prePersist;

	/**
	 * The cached value of the '{@link #getPostPersist() <em>Post Persist</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPostPersist()
	 * @generated
	 * @ordered
	 */
	protected PostPersist postPersist;

	/**
	 * The cached value of the '{@link #getPreRemove() <em>Pre Remove</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPreRemove()
	 * @generated
	 * @ordered
	 */
	protected PreRemove preRemove;

	/**
	 * The cached value of the '{@link #getPostRemove() <em>Post Remove</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPostRemove()
	 * @generated
	 * @ordered
	 */
	protected PostRemove postRemove;

	/**
	 * The cached value of the '{@link #getPreUpdate() <em>Pre Update</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPreUpdate()
	 * @generated
	 * @ordered
	 */
	protected PreUpdate preUpdate;

	/**
	 * The cached value of the '{@link #getPostUpdate() <em>Post Update</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPostUpdate()
	 * @generated
	 * @ordered
	 */
	protected PostUpdate postUpdate;

	/**
	 * The cached value of the '{@link #getPostLoad() <em>Post Load</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPostLoad()
	 * @generated
	 * @ordered
	 */
	protected PostLoad postLoad;

	/**
	 * The cached value of the '{@link #getClass_() <em>Class</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getClass_()
	 * @generated
	 * @ordered
	 */
	protected EClass class_;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EntityListenerImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EORMPackage.eINSTANCE.getEntityListener();
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_LISTENER__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PrePersist getPrePersist() {
		return prePersist;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPrePersist(PrePersist newPrePersist, NotificationChain msgs) {
		PrePersist oldPrePersist = prePersist;
		prePersist = newPrePersist;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_LISTENER__PRE_PERSIST, oldPrePersist, newPrePersist);
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
	public void setPrePersist(PrePersist newPrePersist) {
		if (newPrePersist != prePersist) {
			NotificationChain msgs = null;
			if (prePersist != null)
				msgs = ((InternalEObject)prePersist).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.ENTITY_LISTENER__PRE_PERSIST, null, msgs);
			if (newPrePersist != null)
				msgs = ((InternalEObject)newPrePersist).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.ENTITY_LISTENER__PRE_PERSIST, null, msgs);
			msgs = basicSetPrePersist(newPrePersist, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_LISTENER__PRE_PERSIST, newPrePersist, newPrePersist));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PostPersist getPostPersist() {
		return postPersist;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPostPersist(PostPersist newPostPersist, NotificationChain msgs) {
		PostPersist oldPostPersist = postPersist;
		postPersist = newPostPersist;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_LISTENER__POST_PERSIST, oldPostPersist, newPostPersist);
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
	public void setPostPersist(PostPersist newPostPersist) {
		if (newPostPersist != postPersist) {
			NotificationChain msgs = null;
			if (postPersist != null)
				msgs = ((InternalEObject)postPersist).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.ENTITY_LISTENER__POST_PERSIST, null, msgs);
			if (newPostPersist != null)
				msgs = ((InternalEObject)newPostPersist).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.ENTITY_LISTENER__POST_PERSIST, null, msgs);
			msgs = basicSetPostPersist(newPostPersist, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_LISTENER__POST_PERSIST, newPostPersist, newPostPersist));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PreRemove getPreRemove() {
		return preRemove;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPreRemove(PreRemove newPreRemove, NotificationChain msgs) {
		PreRemove oldPreRemove = preRemove;
		preRemove = newPreRemove;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_LISTENER__PRE_REMOVE, oldPreRemove, newPreRemove);
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
	public void setPreRemove(PreRemove newPreRemove) {
		if (newPreRemove != preRemove) {
			NotificationChain msgs = null;
			if (preRemove != null)
				msgs = ((InternalEObject)preRemove).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.ENTITY_LISTENER__PRE_REMOVE, null, msgs);
			if (newPreRemove != null)
				msgs = ((InternalEObject)newPreRemove).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.ENTITY_LISTENER__PRE_REMOVE, null, msgs);
			msgs = basicSetPreRemove(newPreRemove, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_LISTENER__PRE_REMOVE, newPreRemove, newPreRemove));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PostRemove getPostRemove() {
		return postRemove;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPostRemove(PostRemove newPostRemove, NotificationChain msgs) {
		PostRemove oldPostRemove = postRemove;
		postRemove = newPostRemove;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_LISTENER__POST_REMOVE, oldPostRemove, newPostRemove);
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
	public void setPostRemove(PostRemove newPostRemove) {
		if (newPostRemove != postRemove) {
			NotificationChain msgs = null;
			if (postRemove != null)
				msgs = ((InternalEObject)postRemove).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.ENTITY_LISTENER__POST_REMOVE, null, msgs);
			if (newPostRemove != null)
				msgs = ((InternalEObject)newPostRemove).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.ENTITY_LISTENER__POST_REMOVE, null, msgs);
			msgs = basicSetPostRemove(newPostRemove, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_LISTENER__POST_REMOVE, newPostRemove, newPostRemove));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PreUpdate getPreUpdate() {
		return preUpdate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPreUpdate(PreUpdate newPreUpdate, NotificationChain msgs) {
		PreUpdate oldPreUpdate = preUpdate;
		preUpdate = newPreUpdate;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_LISTENER__PRE_UPDATE, oldPreUpdate, newPreUpdate);
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
	public void setPreUpdate(PreUpdate newPreUpdate) {
		if (newPreUpdate != preUpdate) {
			NotificationChain msgs = null;
			if (preUpdate != null)
				msgs = ((InternalEObject)preUpdate).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.ENTITY_LISTENER__PRE_UPDATE, null, msgs);
			if (newPreUpdate != null)
				msgs = ((InternalEObject)newPreUpdate).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.ENTITY_LISTENER__PRE_UPDATE, null, msgs);
			msgs = basicSetPreUpdate(newPreUpdate, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_LISTENER__PRE_UPDATE, newPreUpdate, newPreUpdate));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PostUpdate getPostUpdate() {
		return postUpdate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPostUpdate(PostUpdate newPostUpdate, NotificationChain msgs) {
		PostUpdate oldPostUpdate = postUpdate;
		postUpdate = newPostUpdate;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_LISTENER__POST_UPDATE, oldPostUpdate, newPostUpdate);
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
	public void setPostUpdate(PostUpdate newPostUpdate) {
		if (newPostUpdate != postUpdate) {
			NotificationChain msgs = null;
			if (postUpdate != null)
				msgs = ((InternalEObject)postUpdate).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.ENTITY_LISTENER__POST_UPDATE, null, msgs);
			if (newPostUpdate != null)
				msgs = ((InternalEObject)newPostUpdate).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.ENTITY_LISTENER__POST_UPDATE, null, msgs);
			msgs = basicSetPostUpdate(newPostUpdate, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_LISTENER__POST_UPDATE, newPostUpdate, newPostUpdate));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PostLoad getPostLoad() {
		return postLoad;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPostLoad(PostLoad newPostLoad, NotificationChain msgs) {
		PostLoad oldPostLoad = postLoad;
		postLoad = newPostLoad;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_LISTENER__POST_LOAD, oldPostLoad, newPostLoad);
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
	public void setPostLoad(PostLoad newPostLoad) {
		if (newPostLoad != postLoad) {
			NotificationChain msgs = null;
			if (postLoad != null)
				msgs = ((InternalEObject)postLoad).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.ENTITY_LISTENER__POST_LOAD, null, msgs);
			if (newPostLoad != null)
				msgs = ((InternalEObject)newPostLoad).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.ENTITY_LISTENER__POST_LOAD, null, msgs);
			msgs = basicSetPostLoad(newPostLoad, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_LISTENER__POST_LOAD, newPostLoad, newPostLoad));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getClass_() {
		if (class_ != null && class_.eIsProxy()) {
			InternalEObject oldClass = (InternalEObject)class_;
			class_ = (EClass)eResolveProxy(oldClass);
			if (class_ != oldClass) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, EORMPackage.ENTITY_LISTENER__CLASS, oldClass, class_));
			}
		}
		return class_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass basicGetClass() {
		return class_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setClass(EClass newClass) {
		EClass oldClass = class_;
		class_ = newClass;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.ENTITY_LISTENER__CLASS, oldClass, class_));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EORMPackage.ENTITY_LISTENER__PRE_PERSIST:
				return basicSetPrePersist(null, msgs);
			case EORMPackage.ENTITY_LISTENER__POST_PERSIST:
				return basicSetPostPersist(null, msgs);
			case EORMPackage.ENTITY_LISTENER__PRE_REMOVE:
				return basicSetPreRemove(null, msgs);
			case EORMPackage.ENTITY_LISTENER__POST_REMOVE:
				return basicSetPostRemove(null, msgs);
			case EORMPackage.ENTITY_LISTENER__PRE_UPDATE:
				return basicSetPreUpdate(null, msgs);
			case EORMPackage.ENTITY_LISTENER__POST_UPDATE:
				return basicSetPostUpdate(null, msgs);
			case EORMPackage.ENTITY_LISTENER__POST_LOAD:
				return basicSetPostLoad(null, msgs);
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
			case EORMPackage.ENTITY_LISTENER__DESCRIPTION:
				return getDescription();
			case EORMPackage.ENTITY_LISTENER__PRE_PERSIST:
				return getPrePersist();
			case EORMPackage.ENTITY_LISTENER__POST_PERSIST:
				return getPostPersist();
			case EORMPackage.ENTITY_LISTENER__PRE_REMOVE:
				return getPreRemove();
			case EORMPackage.ENTITY_LISTENER__POST_REMOVE:
				return getPostRemove();
			case EORMPackage.ENTITY_LISTENER__PRE_UPDATE:
				return getPreUpdate();
			case EORMPackage.ENTITY_LISTENER__POST_UPDATE:
				return getPostUpdate();
			case EORMPackage.ENTITY_LISTENER__POST_LOAD:
				return getPostLoad();
			case EORMPackage.ENTITY_LISTENER__CLASS:
				if (resolve) return getClass_();
				return basicGetClass();
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
			case EORMPackage.ENTITY_LISTENER__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case EORMPackage.ENTITY_LISTENER__PRE_PERSIST:
				setPrePersist((PrePersist)newValue);
				return;
			case EORMPackage.ENTITY_LISTENER__POST_PERSIST:
				setPostPersist((PostPersist)newValue);
				return;
			case EORMPackage.ENTITY_LISTENER__PRE_REMOVE:
				setPreRemove((PreRemove)newValue);
				return;
			case EORMPackage.ENTITY_LISTENER__POST_REMOVE:
				setPostRemove((PostRemove)newValue);
				return;
			case EORMPackage.ENTITY_LISTENER__PRE_UPDATE:
				setPreUpdate((PreUpdate)newValue);
				return;
			case EORMPackage.ENTITY_LISTENER__POST_UPDATE:
				setPostUpdate((PostUpdate)newValue);
				return;
			case EORMPackage.ENTITY_LISTENER__POST_LOAD:
				setPostLoad((PostLoad)newValue);
				return;
			case EORMPackage.ENTITY_LISTENER__CLASS:
				setClass((EClass)newValue);
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
			case EORMPackage.ENTITY_LISTENER__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case EORMPackage.ENTITY_LISTENER__PRE_PERSIST:
				setPrePersist((PrePersist)null);
				return;
			case EORMPackage.ENTITY_LISTENER__POST_PERSIST:
				setPostPersist((PostPersist)null);
				return;
			case EORMPackage.ENTITY_LISTENER__PRE_REMOVE:
				setPreRemove((PreRemove)null);
				return;
			case EORMPackage.ENTITY_LISTENER__POST_REMOVE:
				setPostRemove((PostRemove)null);
				return;
			case EORMPackage.ENTITY_LISTENER__PRE_UPDATE:
				setPreUpdate((PreUpdate)null);
				return;
			case EORMPackage.ENTITY_LISTENER__POST_UPDATE:
				setPostUpdate((PostUpdate)null);
				return;
			case EORMPackage.ENTITY_LISTENER__POST_LOAD:
				setPostLoad((PostLoad)null);
				return;
			case EORMPackage.ENTITY_LISTENER__CLASS:
				setClass((EClass)null);
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
			case EORMPackage.ENTITY_LISTENER__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case EORMPackage.ENTITY_LISTENER__PRE_PERSIST:
				return prePersist != null;
			case EORMPackage.ENTITY_LISTENER__POST_PERSIST:
				return postPersist != null;
			case EORMPackage.ENTITY_LISTENER__PRE_REMOVE:
				return preRemove != null;
			case EORMPackage.ENTITY_LISTENER__POST_REMOVE:
				return postRemove != null;
			case EORMPackage.ENTITY_LISTENER__PRE_UPDATE:
				return preUpdate != null;
			case EORMPackage.ENTITY_LISTENER__POST_UPDATE:
				return postUpdate != null;
			case EORMPackage.ENTITY_LISTENER__POST_LOAD:
				return postLoad != null;
			case EORMPackage.ENTITY_LISTENER__CLASS:
				return class_ != null;
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
		result.append(')');
		return result.toString();
	}

} //EntityListenerImpl
