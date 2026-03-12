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

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.persistence.eorm.Basic;
import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.ElementCollection;
import org.eclipse.fennec.persistence.eorm.EmbeddableAttributes;
import org.eclipse.fennec.persistence.eorm.Embedded;
import org.eclipse.fennec.persistence.eorm.ManyToMany;
import org.eclipse.fennec.persistence.eorm.ManyToOne;
import org.eclipse.fennec.persistence.eorm.OneToMany;
import org.eclipse.fennec.persistence.eorm.OneToOne;
import org.eclipse.fennec.persistence.eorm.Transient;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Embeddable Attributes</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EmbeddableAttributesImpl#getBasic <em>Basic</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EmbeddableAttributesImpl#getManyToOne <em>Many To One</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EmbeddableAttributesImpl#getOneToMany <em>One To Many</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EmbeddableAttributesImpl#getOneToOne <em>One To One</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EmbeddableAttributesImpl#getManyToMany <em>Many To Many</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EmbeddableAttributesImpl#getElementCollection <em>Element Collection</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EmbeddableAttributesImpl#getEmbedded <em>Embedded</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.EmbeddableAttributesImpl#getTransient <em>Transient</em>}</li>
 * </ul>
 *
 * @generated
 */
public class EmbeddableAttributesImpl extends MinimalEObjectImpl.Container implements EmbeddableAttributes {
	/**
	 * The cached value of the '{@link #getBasic() <em>Basic</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBasic()
	 * @generated
	 * @ordered
	 */
	protected EList<Basic> basic;

	/**
	 * The cached value of the '{@link #getManyToOne() <em>Many To One</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getManyToOne()
	 * @generated
	 * @ordered
	 */
	protected EList<ManyToOne> manyToOne;

	/**
	 * The cached value of the '{@link #getOneToMany() <em>One To Many</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOneToMany()
	 * @generated
	 * @ordered
	 */
	protected EList<OneToMany> oneToMany;

	/**
	 * The cached value of the '{@link #getOneToOne() <em>One To One</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOneToOne()
	 * @generated
	 * @ordered
	 */
	protected EList<OneToOne> oneToOne;

	/**
	 * The cached value of the '{@link #getManyToMany() <em>Many To Many</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getManyToMany()
	 * @generated
	 * @ordered
	 */
	protected EList<ManyToMany> manyToMany;

	/**
	 * The cached value of the '{@link #getElementCollection() <em>Element Collection</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getElementCollection()
	 * @generated
	 * @ordered
	 */
	protected EList<ElementCollection> elementCollection;

	/**
	 * The cached value of the '{@link #getEmbedded() <em>Embedded</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEmbedded()
	 * @generated
	 * @ordered
	 */
	protected EList<Embedded> embedded;

	/**
	 * The cached value of the '{@link #getTransient() <em>Transient</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransient()
	 * @generated
	 * @ordered
	 */
	protected EList<Transient> transient_;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EmbeddableAttributesImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EORMPackage.eINSTANCE.getEmbeddableAttributes();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Basic> getBasic() {
		if (basic == null) {
			basic = new EObjectContainmentEList<Basic>(Basic.class, this, EORMPackage.EMBEDDABLE_ATTRIBUTES__BASIC);
		}
		return basic;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ManyToOne> getManyToOne() {
		if (manyToOne == null) {
			manyToOne = new EObjectContainmentEList<ManyToOne>(ManyToOne.class, this, EORMPackage.EMBEDDABLE_ATTRIBUTES__MANY_TO_ONE);
		}
		return manyToOne;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<OneToMany> getOneToMany() {
		if (oneToMany == null) {
			oneToMany = new EObjectContainmentEList<OneToMany>(OneToMany.class, this, EORMPackage.EMBEDDABLE_ATTRIBUTES__ONE_TO_MANY);
		}
		return oneToMany;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<OneToOne> getOneToOne() {
		if (oneToOne == null) {
			oneToOne = new EObjectContainmentEList<OneToOne>(OneToOne.class, this, EORMPackage.EMBEDDABLE_ATTRIBUTES__ONE_TO_ONE);
		}
		return oneToOne;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ManyToMany> getManyToMany() {
		if (manyToMany == null) {
			manyToMany = new EObjectContainmentEList<ManyToMany>(ManyToMany.class, this, EORMPackage.EMBEDDABLE_ATTRIBUTES__MANY_TO_MANY);
		}
		return manyToMany;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ElementCollection> getElementCollection() {
		if (elementCollection == null) {
			elementCollection = new EObjectContainmentEList<ElementCollection>(ElementCollection.class, this, EORMPackage.EMBEDDABLE_ATTRIBUTES__ELEMENT_COLLECTION);
		}
		return elementCollection;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Embedded> getEmbedded() {
		if (embedded == null) {
			embedded = new EObjectContainmentEList<Embedded>(Embedded.class, this, EORMPackage.EMBEDDABLE_ATTRIBUTES__EMBEDDED);
		}
		return embedded;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Transient> getTransient() {
		if (transient_ == null) {
			transient_ = new EObjectContainmentEList<Transient>(Transient.class, this, EORMPackage.EMBEDDABLE_ATTRIBUTES__TRANSIENT);
		}
		return transient_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__BASIC:
				return ((InternalEList<?>)getBasic()).basicRemove(otherEnd, msgs);
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__MANY_TO_ONE:
				return ((InternalEList<?>)getManyToOne()).basicRemove(otherEnd, msgs);
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__ONE_TO_MANY:
				return ((InternalEList<?>)getOneToMany()).basicRemove(otherEnd, msgs);
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__ONE_TO_ONE:
				return ((InternalEList<?>)getOneToOne()).basicRemove(otherEnd, msgs);
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__MANY_TO_MANY:
				return ((InternalEList<?>)getManyToMany()).basicRemove(otherEnd, msgs);
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__ELEMENT_COLLECTION:
				return ((InternalEList<?>)getElementCollection()).basicRemove(otherEnd, msgs);
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__EMBEDDED:
				return ((InternalEList<?>)getEmbedded()).basicRemove(otherEnd, msgs);
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__TRANSIENT:
				return ((InternalEList<?>)getTransient()).basicRemove(otherEnd, msgs);
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
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__BASIC:
				return getBasic();
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__MANY_TO_ONE:
				return getManyToOne();
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__ONE_TO_MANY:
				return getOneToMany();
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__ONE_TO_ONE:
				return getOneToOne();
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__MANY_TO_MANY:
				return getManyToMany();
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__ELEMENT_COLLECTION:
				return getElementCollection();
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__EMBEDDED:
				return getEmbedded();
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__TRANSIENT:
				return getTransient();
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
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__BASIC:
				getBasic().clear();
				getBasic().addAll((Collection<? extends Basic>)newValue);
				return;
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__MANY_TO_ONE:
				getManyToOne().clear();
				getManyToOne().addAll((Collection<? extends ManyToOne>)newValue);
				return;
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__ONE_TO_MANY:
				getOneToMany().clear();
				getOneToMany().addAll((Collection<? extends OneToMany>)newValue);
				return;
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__ONE_TO_ONE:
				getOneToOne().clear();
				getOneToOne().addAll((Collection<? extends OneToOne>)newValue);
				return;
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__MANY_TO_MANY:
				getManyToMany().clear();
				getManyToMany().addAll((Collection<? extends ManyToMany>)newValue);
				return;
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__ELEMENT_COLLECTION:
				getElementCollection().clear();
				getElementCollection().addAll((Collection<? extends ElementCollection>)newValue);
				return;
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__EMBEDDED:
				getEmbedded().clear();
				getEmbedded().addAll((Collection<? extends Embedded>)newValue);
				return;
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__TRANSIENT:
				getTransient().clear();
				getTransient().addAll((Collection<? extends Transient>)newValue);
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
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__BASIC:
				getBasic().clear();
				return;
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__MANY_TO_ONE:
				getManyToOne().clear();
				return;
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__ONE_TO_MANY:
				getOneToMany().clear();
				return;
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__ONE_TO_ONE:
				getOneToOne().clear();
				return;
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__MANY_TO_MANY:
				getManyToMany().clear();
				return;
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__ELEMENT_COLLECTION:
				getElementCollection().clear();
				return;
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__EMBEDDED:
				getEmbedded().clear();
				return;
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__TRANSIENT:
				getTransient().clear();
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
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__BASIC:
				return basic != null && !basic.isEmpty();
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__MANY_TO_ONE:
				return manyToOne != null && !manyToOne.isEmpty();
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__ONE_TO_MANY:
				return oneToMany != null && !oneToMany.isEmpty();
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__ONE_TO_ONE:
				return oneToOne != null && !oneToOne.isEmpty();
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__MANY_TO_MANY:
				return manyToMany != null && !manyToMany.isEmpty();
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__ELEMENT_COLLECTION:
				return elementCollection != null && !elementCollection.isEmpty();
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__EMBEDDED:
				return embedded != null && !embedded.isEmpty();
			case EORMPackage.EMBEDDABLE_ATTRIBUTES__TRANSIENT:
				return transient_ != null && !transient_.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //EmbeddableAttributesImpl
