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
import org.eclipse.fennec.persistence.eorm.NamedAttributeNode;
import org.eclipse.fennec.persistence.eorm.NamedSubgraph;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Named Subgraph</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.NamedSubgraphImpl#getNamedAttributeNode <em>Named Attribute Node</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.NamedSubgraphImpl#getClass_ <em>Class</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.NamedSubgraphImpl#getName <em>Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class NamedSubgraphImpl extends MinimalEObjectImpl.Container implements NamedSubgraph {
	/**
	 * The cached value of the '{@link #getNamedAttributeNode() <em>Named Attribute Node</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNamedAttributeNode()
	 * @generated
	 * @ordered
	 */
	protected EList<NamedAttributeNode> namedAttributeNode;

	/**
	 * The default value of the '{@link #getClass_() <em>Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getClass_()
	 * @generated
	 * @ordered
	 */
	protected static final String CLASS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getClass_() <em>Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getClass_()
	 * @generated
	 * @ordered
	 */
	protected String class_ = CLASS_EDEFAULT;

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
	protected NamedSubgraphImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EORMPackage.eINSTANCE.getNamedSubgraph();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<NamedAttributeNode> getNamedAttributeNode() {
		if (namedAttributeNode == null) {
			namedAttributeNode = new EObjectContainmentEList<NamedAttributeNode>(NamedAttributeNode.class, this, EORMPackage.NAMED_SUBGRAPH__NAMED_ATTRIBUTE_NODE);
		}
		return namedAttributeNode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getClass_() {
		return class_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setClass(String newClass) {
		String oldClass = class_;
		class_ = newClass;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.NAMED_SUBGRAPH__CLASS, oldClass, class_));
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.NAMED_SUBGRAPH__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EORMPackage.NAMED_SUBGRAPH__NAMED_ATTRIBUTE_NODE:
				return ((InternalEList<?>)getNamedAttributeNode()).basicRemove(otherEnd, msgs);
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
			case EORMPackage.NAMED_SUBGRAPH__NAMED_ATTRIBUTE_NODE:
				return getNamedAttributeNode();
			case EORMPackage.NAMED_SUBGRAPH__CLASS:
				return getClass_();
			case EORMPackage.NAMED_SUBGRAPH__NAME:
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
			case EORMPackage.NAMED_SUBGRAPH__NAMED_ATTRIBUTE_NODE:
				getNamedAttributeNode().clear();
				getNamedAttributeNode().addAll((Collection<? extends NamedAttributeNode>)newValue);
				return;
			case EORMPackage.NAMED_SUBGRAPH__CLASS:
				setClass((String)newValue);
				return;
			case EORMPackage.NAMED_SUBGRAPH__NAME:
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
			case EORMPackage.NAMED_SUBGRAPH__NAMED_ATTRIBUTE_NODE:
				getNamedAttributeNode().clear();
				return;
			case EORMPackage.NAMED_SUBGRAPH__CLASS:
				setClass(CLASS_EDEFAULT);
				return;
			case EORMPackage.NAMED_SUBGRAPH__NAME:
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
			case EORMPackage.NAMED_SUBGRAPH__NAMED_ATTRIBUTE_NODE:
				return namedAttributeNode != null && !namedAttributeNode.isEmpty();
			case EORMPackage.NAMED_SUBGRAPH__CLASS:
				return CLASS_EDEFAULT == null ? class_ != null : !CLASS_EDEFAULT.equals(class_);
			case EORMPackage.NAMED_SUBGRAPH__NAME:
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
		result.append(" (class: ");
		result.append(class_);
		result.append(", name: ");
		result.append(name);
		result.append(')');
		return result.toString();
	}

} //NamedSubgraphImpl
