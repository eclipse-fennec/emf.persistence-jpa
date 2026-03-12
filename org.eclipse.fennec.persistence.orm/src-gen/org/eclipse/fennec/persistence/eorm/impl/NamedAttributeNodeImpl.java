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

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.NamedAttributeNode;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Named Attribute Node</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.NamedAttributeNodeImpl#getKeySubgraph <em>Key Subgraph</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.NamedAttributeNodeImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.NamedAttributeNodeImpl#getSubgraph <em>Subgraph</em>}</li>
 * </ul>
 *
 * @generated
 */
public class NamedAttributeNodeImpl extends MinimalEObjectImpl.Container implements NamedAttributeNode {
	/**
	 * The default value of the '{@link #getKeySubgraph() <em>Key Subgraph</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getKeySubgraph()
	 * @generated
	 * @ordered
	 */
	protected static final String KEY_SUBGRAPH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getKeySubgraph() <em>Key Subgraph</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getKeySubgraph()
	 * @generated
	 * @ordered
	 */
	protected String keySubgraph = KEY_SUBGRAPH_EDEFAULT;

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
	 * The default value of the '{@link #getSubgraph() <em>Subgraph</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubgraph()
	 * @generated
	 * @ordered
	 */
	protected static final String SUBGRAPH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSubgraph() <em>Subgraph</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubgraph()
	 * @generated
	 * @ordered
	 */
	protected String subgraph = SUBGRAPH_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NamedAttributeNodeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EORMPackage.eINSTANCE.getNamedAttributeNode();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getKeySubgraph() {
		return keySubgraph;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setKeySubgraph(String newKeySubgraph) {
		String oldKeySubgraph = keySubgraph;
		keySubgraph = newKeySubgraph;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.NAMED_ATTRIBUTE_NODE__KEY_SUBGRAPH, oldKeySubgraph, keySubgraph));
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.NAMED_ATTRIBUTE_NODE__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getSubgraph() {
		return subgraph;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSubgraph(String newSubgraph) {
		String oldSubgraph = subgraph;
		subgraph = newSubgraph;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.NAMED_ATTRIBUTE_NODE__SUBGRAPH, oldSubgraph, subgraph));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case EORMPackage.NAMED_ATTRIBUTE_NODE__KEY_SUBGRAPH:
				return getKeySubgraph();
			case EORMPackage.NAMED_ATTRIBUTE_NODE__NAME:
				return getName();
			case EORMPackage.NAMED_ATTRIBUTE_NODE__SUBGRAPH:
				return getSubgraph();
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
			case EORMPackage.NAMED_ATTRIBUTE_NODE__KEY_SUBGRAPH:
				setKeySubgraph((String)newValue);
				return;
			case EORMPackage.NAMED_ATTRIBUTE_NODE__NAME:
				setName((String)newValue);
				return;
			case EORMPackage.NAMED_ATTRIBUTE_NODE__SUBGRAPH:
				setSubgraph((String)newValue);
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
			case EORMPackage.NAMED_ATTRIBUTE_NODE__KEY_SUBGRAPH:
				setKeySubgraph(KEY_SUBGRAPH_EDEFAULT);
				return;
			case EORMPackage.NAMED_ATTRIBUTE_NODE__NAME:
				setName(NAME_EDEFAULT);
				return;
			case EORMPackage.NAMED_ATTRIBUTE_NODE__SUBGRAPH:
				setSubgraph(SUBGRAPH_EDEFAULT);
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
			case EORMPackage.NAMED_ATTRIBUTE_NODE__KEY_SUBGRAPH:
				return KEY_SUBGRAPH_EDEFAULT == null ? keySubgraph != null : !KEY_SUBGRAPH_EDEFAULT.equals(keySubgraph);
			case EORMPackage.NAMED_ATTRIBUTE_NODE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case EORMPackage.NAMED_ATTRIBUTE_NODE__SUBGRAPH:
				return SUBGRAPH_EDEFAULT == null ? subgraph != null : !SUBGRAPH_EDEFAULT.equals(subgraph);
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
		result.append(" (keySubgraph: ");
		result.append(keySubgraph);
		result.append(", name: ");
		result.append(name);
		result.append(", subgraph: ");
		result.append(subgraph);
		result.append(')');
		return result.toString();
	}

} //NamedAttributeNodeImpl
