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

import org.eclipse.fennec.persistence.eorm.AccessType;
import org.eclipse.fennec.persistence.eorm.Attributes;
import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.EmptyType;
import org.eclipse.fennec.persistence.eorm.EntityListeners;
import org.eclipse.fennec.persistence.eorm.IdClass;
import org.eclipse.fennec.persistence.eorm.MappedSuperclass;
import org.eclipse.fennec.persistence.eorm.PostLoad;
import org.eclipse.fennec.persistence.eorm.PostPersist;
import org.eclipse.fennec.persistence.eorm.PostRemove;
import org.eclipse.fennec.persistence.eorm.PostUpdate;
import org.eclipse.fennec.persistence.eorm.PrePersist;
import org.eclipse.fennec.persistence.eorm.PreRemove;
import org.eclipse.fennec.persistence.eorm.PreUpdate;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Mapped Superclass</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MappedSuperclassImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MappedSuperclassImpl#getIdClass <em>Id Class</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MappedSuperclassImpl#getExcludeDefaultListeners <em>Exclude Default Listeners</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MappedSuperclassImpl#getExcludeSuperclassListeners <em>Exclude Superclass Listeners</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MappedSuperclassImpl#getEntityListeners <em>Entity Listeners</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MappedSuperclassImpl#getPrePersist <em>Pre Persist</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MappedSuperclassImpl#getPostPersist <em>Post Persist</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MappedSuperclassImpl#getPreRemove <em>Pre Remove</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MappedSuperclassImpl#getPostRemove <em>Post Remove</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MappedSuperclassImpl#getPreUpdate <em>Pre Update</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MappedSuperclassImpl#getPostUpdate <em>Post Update</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MappedSuperclassImpl#getPostLoad <em>Post Load</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MappedSuperclassImpl#getAttributes <em>Attributes</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MappedSuperclassImpl#getAccess <em>Access</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MappedSuperclassImpl#getClass_ <em>Class</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.MappedSuperclassImpl#isMetadataComplete <em>Metadata Complete</em>}</li>
 * </ul>
 *
 * @generated
 */
public class MappedSuperclassImpl extends MinimalEObjectImpl.Container implements MappedSuperclass {
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
	 * The cached value of the '{@link #getIdClass() <em>Id Class</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdClass()
	 * @generated
	 * @ordered
	 */
	protected IdClass idClass;

	/**
	 * The cached value of the '{@link #getExcludeDefaultListeners() <em>Exclude Default Listeners</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExcludeDefaultListeners()
	 * @generated
	 * @ordered
	 */
	protected EmptyType excludeDefaultListeners;

	/**
	 * The cached value of the '{@link #getExcludeSuperclassListeners() <em>Exclude Superclass Listeners</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExcludeSuperclassListeners()
	 * @generated
	 * @ordered
	 */
	protected EmptyType excludeSuperclassListeners;

	/**
	 * The cached value of the '{@link #getEntityListeners() <em>Entity Listeners</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEntityListeners()
	 * @generated
	 * @ordered
	 */
	protected EntityListeners entityListeners;

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
	 * The cached value of the '{@link #getAttributes() <em>Attributes</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAttributes()
	 * @generated
	 * @ordered
	 */
	protected Attributes attributes;

	/**
	 * The default value of the '{@link #getAccess() <em>Access</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAccess()
	 * @generated
	 * @ordered
	 */
	protected static final AccessType ACCESS_EDEFAULT = AccessType.PROPERTY;

	/**
	 * The cached value of the '{@link #getAccess() <em>Access</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAccess()
	 * @generated
	 * @ordered
	 */
	protected AccessType access = ACCESS_EDEFAULT;

	/**
	 * This is true if the Access attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean accessESet;

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
	 * The default value of the '{@link #isMetadataComplete() <em>Metadata Complete</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isMetadataComplete()
	 * @generated
	 * @ordered
	 */
	protected static final boolean METADATA_COMPLETE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isMetadataComplete() <em>Metadata Complete</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isMetadataComplete()
	 * @generated
	 * @ordered
	 */
	protected boolean metadataComplete = METADATA_COMPLETE_EDEFAULT;

	/**
	 * This is true if the Metadata Complete attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean metadataCompleteESet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MappedSuperclassImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EORMPackage.eINSTANCE.getMappedSuperclass();
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public IdClass getIdClass() {
		return idClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetIdClass(IdClass newIdClass, NotificationChain msgs) {
		IdClass oldIdClass = idClass;
		idClass = newIdClass;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__ID_CLASS, oldIdClass, newIdClass);
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
	public void setIdClass(IdClass newIdClass) {
		if (newIdClass != idClass) {
			NotificationChain msgs = null;
			if (idClass != null)
				msgs = ((InternalEObject)idClass).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MAPPED_SUPERCLASS__ID_CLASS, null, msgs);
			if (newIdClass != null)
				msgs = ((InternalEObject)newIdClass).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MAPPED_SUPERCLASS__ID_CLASS, null, msgs);
			msgs = basicSetIdClass(newIdClass, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__ID_CLASS, newIdClass, newIdClass));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EmptyType getExcludeDefaultListeners() {
		return excludeDefaultListeners;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetExcludeDefaultListeners(EmptyType newExcludeDefaultListeners, NotificationChain msgs) {
		EmptyType oldExcludeDefaultListeners = excludeDefaultListeners;
		excludeDefaultListeners = newExcludeDefaultListeners;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__EXCLUDE_DEFAULT_LISTENERS, oldExcludeDefaultListeners, newExcludeDefaultListeners);
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
	public void setExcludeDefaultListeners(EmptyType newExcludeDefaultListeners) {
		if (newExcludeDefaultListeners != excludeDefaultListeners) {
			NotificationChain msgs = null;
			if (excludeDefaultListeners != null)
				msgs = ((InternalEObject)excludeDefaultListeners).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MAPPED_SUPERCLASS__EXCLUDE_DEFAULT_LISTENERS, null, msgs);
			if (newExcludeDefaultListeners != null)
				msgs = ((InternalEObject)newExcludeDefaultListeners).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MAPPED_SUPERCLASS__EXCLUDE_DEFAULT_LISTENERS, null, msgs);
			msgs = basicSetExcludeDefaultListeners(newExcludeDefaultListeners, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__EXCLUDE_DEFAULT_LISTENERS, newExcludeDefaultListeners, newExcludeDefaultListeners));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EmptyType getExcludeSuperclassListeners() {
		return excludeSuperclassListeners;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetExcludeSuperclassListeners(EmptyType newExcludeSuperclassListeners, NotificationChain msgs) {
		EmptyType oldExcludeSuperclassListeners = excludeSuperclassListeners;
		excludeSuperclassListeners = newExcludeSuperclassListeners;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__EXCLUDE_SUPERCLASS_LISTENERS, oldExcludeSuperclassListeners, newExcludeSuperclassListeners);
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
	public void setExcludeSuperclassListeners(EmptyType newExcludeSuperclassListeners) {
		if (newExcludeSuperclassListeners != excludeSuperclassListeners) {
			NotificationChain msgs = null;
			if (excludeSuperclassListeners != null)
				msgs = ((InternalEObject)excludeSuperclassListeners).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MAPPED_SUPERCLASS__EXCLUDE_SUPERCLASS_LISTENERS, null, msgs);
			if (newExcludeSuperclassListeners != null)
				msgs = ((InternalEObject)newExcludeSuperclassListeners).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MAPPED_SUPERCLASS__EXCLUDE_SUPERCLASS_LISTENERS, null, msgs);
			msgs = basicSetExcludeSuperclassListeners(newExcludeSuperclassListeners, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__EXCLUDE_SUPERCLASS_LISTENERS, newExcludeSuperclassListeners, newExcludeSuperclassListeners));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EntityListeners getEntityListeners() {
		return entityListeners;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetEntityListeners(EntityListeners newEntityListeners, NotificationChain msgs) {
		EntityListeners oldEntityListeners = entityListeners;
		entityListeners = newEntityListeners;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__ENTITY_LISTENERS, oldEntityListeners, newEntityListeners);
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
	public void setEntityListeners(EntityListeners newEntityListeners) {
		if (newEntityListeners != entityListeners) {
			NotificationChain msgs = null;
			if (entityListeners != null)
				msgs = ((InternalEObject)entityListeners).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MAPPED_SUPERCLASS__ENTITY_LISTENERS, null, msgs);
			if (newEntityListeners != null)
				msgs = ((InternalEObject)newEntityListeners).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MAPPED_SUPERCLASS__ENTITY_LISTENERS, null, msgs);
			msgs = basicSetEntityListeners(newEntityListeners, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__ENTITY_LISTENERS, newEntityListeners, newEntityListeners));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__PRE_PERSIST, oldPrePersist, newPrePersist);
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
				msgs = ((InternalEObject)prePersist).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MAPPED_SUPERCLASS__PRE_PERSIST, null, msgs);
			if (newPrePersist != null)
				msgs = ((InternalEObject)newPrePersist).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MAPPED_SUPERCLASS__PRE_PERSIST, null, msgs);
			msgs = basicSetPrePersist(newPrePersist, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__PRE_PERSIST, newPrePersist, newPrePersist));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__POST_PERSIST, oldPostPersist, newPostPersist);
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
				msgs = ((InternalEObject)postPersist).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MAPPED_SUPERCLASS__POST_PERSIST, null, msgs);
			if (newPostPersist != null)
				msgs = ((InternalEObject)newPostPersist).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MAPPED_SUPERCLASS__POST_PERSIST, null, msgs);
			msgs = basicSetPostPersist(newPostPersist, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__POST_PERSIST, newPostPersist, newPostPersist));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__PRE_REMOVE, oldPreRemove, newPreRemove);
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
				msgs = ((InternalEObject)preRemove).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MAPPED_SUPERCLASS__PRE_REMOVE, null, msgs);
			if (newPreRemove != null)
				msgs = ((InternalEObject)newPreRemove).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MAPPED_SUPERCLASS__PRE_REMOVE, null, msgs);
			msgs = basicSetPreRemove(newPreRemove, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__PRE_REMOVE, newPreRemove, newPreRemove));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__POST_REMOVE, oldPostRemove, newPostRemove);
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
				msgs = ((InternalEObject)postRemove).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MAPPED_SUPERCLASS__POST_REMOVE, null, msgs);
			if (newPostRemove != null)
				msgs = ((InternalEObject)newPostRemove).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MAPPED_SUPERCLASS__POST_REMOVE, null, msgs);
			msgs = basicSetPostRemove(newPostRemove, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__POST_REMOVE, newPostRemove, newPostRemove));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__PRE_UPDATE, oldPreUpdate, newPreUpdate);
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
				msgs = ((InternalEObject)preUpdate).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MAPPED_SUPERCLASS__PRE_UPDATE, null, msgs);
			if (newPreUpdate != null)
				msgs = ((InternalEObject)newPreUpdate).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MAPPED_SUPERCLASS__PRE_UPDATE, null, msgs);
			msgs = basicSetPreUpdate(newPreUpdate, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__PRE_UPDATE, newPreUpdate, newPreUpdate));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__POST_UPDATE, oldPostUpdate, newPostUpdate);
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
				msgs = ((InternalEObject)postUpdate).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MAPPED_SUPERCLASS__POST_UPDATE, null, msgs);
			if (newPostUpdate != null)
				msgs = ((InternalEObject)newPostUpdate).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MAPPED_SUPERCLASS__POST_UPDATE, null, msgs);
			msgs = basicSetPostUpdate(newPostUpdate, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__POST_UPDATE, newPostUpdate, newPostUpdate));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__POST_LOAD, oldPostLoad, newPostLoad);
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
				msgs = ((InternalEObject)postLoad).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MAPPED_SUPERCLASS__POST_LOAD, null, msgs);
			if (newPostLoad != null)
				msgs = ((InternalEObject)newPostLoad).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MAPPED_SUPERCLASS__POST_LOAD, null, msgs);
			msgs = basicSetPostLoad(newPostLoad, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__POST_LOAD, newPostLoad, newPostLoad));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Attributes getAttributes() {
		return attributes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetAttributes(Attributes newAttributes, NotificationChain msgs) {
		Attributes oldAttributes = attributes;
		attributes = newAttributes;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__ATTRIBUTES, oldAttributes, newAttributes);
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
	public void setAttributes(Attributes newAttributes) {
		if (newAttributes != attributes) {
			NotificationChain msgs = null;
			if (attributes != null)
				msgs = ((InternalEObject)attributes).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MAPPED_SUPERCLASS__ATTRIBUTES, null, msgs);
			if (newAttributes != null)
				msgs = ((InternalEObject)newAttributes).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EORMPackage.MAPPED_SUPERCLASS__ATTRIBUTES, null, msgs);
			msgs = basicSetAttributes(newAttributes, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__ATTRIBUTES, newAttributes, newAttributes));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AccessType getAccess() {
		return access;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAccess(AccessType newAccess) {
		AccessType oldAccess = access;
		access = newAccess == null ? ACCESS_EDEFAULT : newAccess;
		boolean oldAccessESet = accessESet;
		accessESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__ACCESS, oldAccess, access, !oldAccessESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetAccess() {
		AccessType oldAccess = access;
		boolean oldAccessESet = accessESet;
		access = ACCESS_EDEFAULT;
		accessESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EORMPackage.MAPPED_SUPERCLASS__ACCESS, oldAccess, ACCESS_EDEFAULT, oldAccessESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetAccess() {
		return accessESet;
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
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, EORMPackage.MAPPED_SUPERCLASS__CLASS, oldClass, class_));
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__CLASS, oldClass, class_));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isMetadataComplete() {
		return metadataComplete;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMetadataComplete(boolean newMetadataComplete) {
		boolean oldMetadataComplete = metadataComplete;
		metadataComplete = newMetadataComplete;
		boolean oldMetadataCompleteESet = metadataCompleteESet;
		metadataCompleteESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.MAPPED_SUPERCLASS__METADATA_COMPLETE, oldMetadataComplete, metadataComplete, !oldMetadataCompleteESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetMetadataComplete() {
		boolean oldMetadataComplete = metadataComplete;
		boolean oldMetadataCompleteESet = metadataCompleteESet;
		metadataComplete = METADATA_COMPLETE_EDEFAULT;
		metadataCompleteESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EORMPackage.MAPPED_SUPERCLASS__METADATA_COMPLETE, oldMetadataComplete, METADATA_COMPLETE_EDEFAULT, oldMetadataCompleteESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetMetadataComplete() {
		return metadataCompleteESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EORMPackage.MAPPED_SUPERCLASS__ID_CLASS:
				return basicSetIdClass(null, msgs);
			case EORMPackage.MAPPED_SUPERCLASS__EXCLUDE_DEFAULT_LISTENERS:
				return basicSetExcludeDefaultListeners(null, msgs);
			case EORMPackage.MAPPED_SUPERCLASS__EXCLUDE_SUPERCLASS_LISTENERS:
				return basicSetExcludeSuperclassListeners(null, msgs);
			case EORMPackage.MAPPED_SUPERCLASS__ENTITY_LISTENERS:
				return basicSetEntityListeners(null, msgs);
			case EORMPackage.MAPPED_SUPERCLASS__PRE_PERSIST:
				return basicSetPrePersist(null, msgs);
			case EORMPackage.MAPPED_SUPERCLASS__POST_PERSIST:
				return basicSetPostPersist(null, msgs);
			case EORMPackage.MAPPED_SUPERCLASS__PRE_REMOVE:
				return basicSetPreRemove(null, msgs);
			case EORMPackage.MAPPED_SUPERCLASS__POST_REMOVE:
				return basicSetPostRemove(null, msgs);
			case EORMPackage.MAPPED_SUPERCLASS__PRE_UPDATE:
				return basicSetPreUpdate(null, msgs);
			case EORMPackage.MAPPED_SUPERCLASS__POST_UPDATE:
				return basicSetPostUpdate(null, msgs);
			case EORMPackage.MAPPED_SUPERCLASS__POST_LOAD:
				return basicSetPostLoad(null, msgs);
			case EORMPackage.MAPPED_SUPERCLASS__ATTRIBUTES:
				return basicSetAttributes(null, msgs);
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
			case EORMPackage.MAPPED_SUPERCLASS__DESCRIPTION:
				return getDescription();
			case EORMPackage.MAPPED_SUPERCLASS__ID_CLASS:
				return getIdClass();
			case EORMPackage.MAPPED_SUPERCLASS__EXCLUDE_DEFAULT_LISTENERS:
				return getExcludeDefaultListeners();
			case EORMPackage.MAPPED_SUPERCLASS__EXCLUDE_SUPERCLASS_LISTENERS:
				return getExcludeSuperclassListeners();
			case EORMPackage.MAPPED_SUPERCLASS__ENTITY_LISTENERS:
				return getEntityListeners();
			case EORMPackage.MAPPED_SUPERCLASS__PRE_PERSIST:
				return getPrePersist();
			case EORMPackage.MAPPED_SUPERCLASS__POST_PERSIST:
				return getPostPersist();
			case EORMPackage.MAPPED_SUPERCLASS__PRE_REMOVE:
				return getPreRemove();
			case EORMPackage.MAPPED_SUPERCLASS__POST_REMOVE:
				return getPostRemove();
			case EORMPackage.MAPPED_SUPERCLASS__PRE_UPDATE:
				return getPreUpdate();
			case EORMPackage.MAPPED_SUPERCLASS__POST_UPDATE:
				return getPostUpdate();
			case EORMPackage.MAPPED_SUPERCLASS__POST_LOAD:
				return getPostLoad();
			case EORMPackage.MAPPED_SUPERCLASS__ATTRIBUTES:
				return getAttributes();
			case EORMPackage.MAPPED_SUPERCLASS__ACCESS:
				return getAccess();
			case EORMPackage.MAPPED_SUPERCLASS__CLASS:
				if (resolve) return getClass_();
				return basicGetClass();
			case EORMPackage.MAPPED_SUPERCLASS__METADATA_COMPLETE:
				return isMetadataComplete();
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
			case EORMPackage.MAPPED_SUPERCLASS__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__ID_CLASS:
				setIdClass((IdClass)newValue);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__EXCLUDE_DEFAULT_LISTENERS:
				setExcludeDefaultListeners((EmptyType)newValue);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__EXCLUDE_SUPERCLASS_LISTENERS:
				setExcludeSuperclassListeners((EmptyType)newValue);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__ENTITY_LISTENERS:
				setEntityListeners((EntityListeners)newValue);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__PRE_PERSIST:
				setPrePersist((PrePersist)newValue);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__POST_PERSIST:
				setPostPersist((PostPersist)newValue);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__PRE_REMOVE:
				setPreRemove((PreRemove)newValue);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__POST_REMOVE:
				setPostRemove((PostRemove)newValue);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__PRE_UPDATE:
				setPreUpdate((PreUpdate)newValue);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__POST_UPDATE:
				setPostUpdate((PostUpdate)newValue);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__POST_LOAD:
				setPostLoad((PostLoad)newValue);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__ATTRIBUTES:
				setAttributes((Attributes)newValue);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__ACCESS:
				setAccess((AccessType)newValue);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__CLASS:
				setClass((EClass)newValue);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__METADATA_COMPLETE:
				setMetadataComplete((Boolean)newValue);
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
			case EORMPackage.MAPPED_SUPERCLASS__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__ID_CLASS:
				setIdClass((IdClass)null);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__EXCLUDE_DEFAULT_LISTENERS:
				setExcludeDefaultListeners((EmptyType)null);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__EXCLUDE_SUPERCLASS_LISTENERS:
				setExcludeSuperclassListeners((EmptyType)null);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__ENTITY_LISTENERS:
				setEntityListeners((EntityListeners)null);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__PRE_PERSIST:
				setPrePersist((PrePersist)null);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__POST_PERSIST:
				setPostPersist((PostPersist)null);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__PRE_REMOVE:
				setPreRemove((PreRemove)null);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__POST_REMOVE:
				setPostRemove((PostRemove)null);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__PRE_UPDATE:
				setPreUpdate((PreUpdate)null);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__POST_UPDATE:
				setPostUpdate((PostUpdate)null);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__POST_LOAD:
				setPostLoad((PostLoad)null);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__ATTRIBUTES:
				setAttributes((Attributes)null);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__ACCESS:
				unsetAccess();
				return;
			case EORMPackage.MAPPED_SUPERCLASS__CLASS:
				setClass((EClass)null);
				return;
			case EORMPackage.MAPPED_SUPERCLASS__METADATA_COMPLETE:
				unsetMetadataComplete();
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
			case EORMPackage.MAPPED_SUPERCLASS__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case EORMPackage.MAPPED_SUPERCLASS__ID_CLASS:
				return idClass != null;
			case EORMPackage.MAPPED_SUPERCLASS__EXCLUDE_DEFAULT_LISTENERS:
				return excludeDefaultListeners != null;
			case EORMPackage.MAPPED_SUPERCLASS__EXCLUDE_SUPERCLASS_LISTENERS:
				return excludeSuperclassListeners != null;
			case EORMPackage.MAPPED_SUPERCLASS__ENTITY_LISTENERS:
				return entityListeners != null;
			case EORMPackage.MAPPED_SUPERCLASS__PRE_PERSIST:
				return prePersist != null;
			case EORMPackage.MAPPED_SUPERCLASS__POST_PERSIST:
				return postPersist != null;
			case EORMPackage.MAPPED_SUPERCLASS__PRE_REMOVE:
				return preRemove != null;
			case EORMPackage.MAPPED_SUPERCLASS__POST_REMOVE:
				return postRemove != null;
			case EORMPackage.MAPPED_SUPERCLASS__PRE_UPDATE:
				return preUpdate != null;
			case EORMPackage.MAPPED_SUPERCLASS__POST_UPDATE:
				return postUpdate != null;
			case EORMPackage.MAPPED_SUPERCLASS__POST_LOAD:
				return postLoad != null;
			case EORMPackage.MAPPED_SUPERCLASS__ATTRIBUTES:
				return attributes != null;
			case EORMPackage.MAPPED_SUPERCLASS__ACCESS:
				return isSetAccess();
			case EORMPackage.MAPPED_SUPERCLASS__CLASS:
				return class_ != null;
			case EORMPackage.MAPPED_SUPERCLASS__METADATA_COMPLETE:
				return isSetMetadataComplete();
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
		result.append(", access: ");
		if (accessESet) result.append(access); else result.append("<unset>");
		result.append(", metadataComplete: ");
		if (metadataCompleteESet) result.append(metadataComplete); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //MappedSuperclassImpl
