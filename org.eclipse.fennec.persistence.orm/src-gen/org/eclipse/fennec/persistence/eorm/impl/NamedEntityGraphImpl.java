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
import org.eclipse.fennec.persistence.eorm.NamedEntityGraph;
import org.eclipse.fennec.persistence.eorm.NamedSubgraph;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Named Entity Graph</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.NamedEntityGraphImpl#getNamedAttributeNode <em>Named Attribute Node</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.NamedEntityGraphImpl#getSubgraph <em>Subgraph</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.NamedEntityGraphImpl#getSubclassSubgraph <em>Subclass Subgraph</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.NamedEntityGraphImpl#isIncludeAllAttributes <em>Include All Attributes</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.NamedEntityGraphImpl#getName <em>Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class NamedEntityGraphImpl extends MinimalEObjectImpl.Container implements NamedEntityGraph {
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
	 * The cached value of the '{@link #getSubgraph() <em>Subgraph</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubgraph()
	 * @generated
	 * @ordered
	 */
	protected EList<NamedSubgraph> subgraph;

	/**
	 * The cached value of the '{@link #getSubclassSubgraph() <em>Subclass Subgraph</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubclassSubgraph()
	 * @generated
	 * @ordered
	 */
	protected EList<NamedSubgraph> subclassSubgraph;

	/**
	 * The default value of the '{@link #isIncludeAllAttributes() <em>Include All Attributes</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIncludeAllAttributes()
	 * @generated
	 * @ordered
	 */
	protected static final boolean INCLUDE_ALL_ATTRIBUTES_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isIncludeAllAttributes() <em>Include All Attributes</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIncludeAllAttributes()
	 * @generated
	 * @ordered
	 */
	protected boolean includeAllAttributes = INCLUDE_ALL_ATTRIBUTES_EDEFAULT;

	/**
	 * This is true if the Include All Attributes attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean includeAllAttributesESet;

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
	protected NamedEntityGraphImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EORMPackage.eINSTANCE.getNamedEntityGraph();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<NamedAttributeNode> getNamedAttributeNode() {
		if (namedAttributeNode == null) {
			namedAttributeNode = new EObjectContainmentEList<NamedAttributeNode>(NamedAttributeNode.class, this, EORMPackage.NAMED_ENTITY_GRAPH__NAMED_ATTRIBUTE_NODE);
		}
		return namedAttributeNode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<NamedSubgraph> getSubgraph() {
		if (subgraph == null) {
			subgraph = new EObjectContainmentEList<NamedSubgraph>(NamedSubgraph.class, this, EORMPackage.NAMED_ENTITY_GRAPH__SUBGRAPH);
		}
		return subgraph;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<NamedSubgraph> getSubclassSubgraph() {
		if (subclassSubgraph == null) {
			subclassSubgraph = new EObjectContainmentEList<NamedSubgraph>(NamedSubgraph.class, this, EORMPackage.NAMED_ENTITY_GRAPH__SUBCLASS_SUBGRAPH);
		}
		return subclassSubgraph;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isIncludeAllAttributes() {
		return includeAllAttributes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIncludeAllAttributes(boolean newIncludeAllAttributes) {
		boolean oldIncludeAllAttributes = includeAllAttributes;
		includeAllAttributes = newIncludeAllAttributes;
		boolean oldIncludeAllAttributesESet = includeAllAttributesESet;
		includeAllAttributesESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.NAMED_ENTITY_GRAPH__INCLUDE_ALL_ATTRIBUTES, oldIncludeAllAttributes, includeAllAttributes, !oldIncludeAllAttributesESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetIncludeAllAttributes() {
		boolean oldIncludeAllAttributes = includeAllAttributes;
		boolean oldIncludeAllAttributesESet = includeAllAttributesESet;
		includeAllAttributes = INCLUDE_ALL_ATTRIBUTES_EDEFAULT;
		includeAllAttributesESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EORMPackage.NAMED_ENTITY_GRAPH__INCLUDE_ALL_ATTRIBUTES, oldIncludeAllAttributes, INCLUDE_ALL_ATTRIBUTES_EDEFAULT, oldIncludeAllAttributesESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetIncludeAllAttributes() {
		return includeAllAttributesESet;
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.NAMED_ENTITY_GRAPH__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EORMPackage.NAMED_ENTITY_GRAPH__NAMED_ATTRIBUTE_NODE:
				return ((InternalEList<?>)getNamedAttributeNode()).basicRemove(otherEnd, msgs);
			case EORMPackage.NAMED_ENTITY_GRAPH__SUBGRAPH:
				return ((InternalEList<?>)getSubgraph()).basicRemove(otherEnd, msgs);
			case EORMPackage.NAMED_ENTITY_GRAPH__SUBCLASS_SUBGRAPH:
				return ((InternalEList<?>)getSubclassSubgraph()).basicRemove(otherEnd, msgs);
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
			case EORMPackage.NAMED_ENTITY_GRAPH__NAMED_ATTRIBUTE_NODE:
				return getNamedAttributeNode();
			case EORMPackage.NAMED_ENTITY_GRAPH__SUBGRAPH:
				return getSubgraph();
			case EORMPackage.NAMED_ENTITY_GRAPH__SUBCLASS_SUBGRAPH:
				return getSubclassSubgraph();
			case EORMPackage.NAMED_ENTITY_GRAPH__INCLUDE_ALL_ATTRIBUTES:
				return isIncludeAllAttributes();
			case EORMPackage.NAMED_ENTITY_GRAPH__NAME:
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
			case EORMPackage.NAMED_ENTITY_GRAPH__NAMED_ATTRIBUTE_NODE:
				getNamedAttributeNode().clear();
				getNamedAttributeNode().addAll((Collection<? extends NamedAttributeNode>)newValue);
				return;
			case EORMPackage.NAMED_ENTITY_GRAPH__SUBGRAPH:
				getSubgraph().clear();
				getSubgraph().addAll((Collection<? extends NamedSubgraph>)newValue);
				return;
			case EORMPackage.NAMED_ENTITY_GRAPH__SUBCLASS_SUBGRAPH:
				getSubclassSubgraph().clear();
				getSubclassSubgraph().addAll((Collection<? extends NamedSubgraph>)newValue);
				return;
			case EORMPackage.NAMED_ENTITY_GRAPH__INCLUDE_ALL_ATTRIBUTES:
				setIncludeAllAttributes((Boolean)newValue);
				return;
			case EORMPackage.NAMED_ENTITY_GRAPH__NAME:
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
			case EORMPackage.NAMED_ENTITY_GRAPH__NAMED_ATTRIBUTE_NODE:
				getNamedAttributeNode().clear();
				return;
			case EORMPackage.NAMED_ENTITY_GRAPH__SUBGRAPH:
				getSubgraph().clear();
				return;
			case EORMPackage.NAMED_ENTITY_GRAPH__SUBCLASS_SUBGRAPH:
				getSubclassSubgraph().clear();
				return;
			case EORMPackage.NAMED_ENTITY_GRAPH__INCLUDE_ALL_ATTRIBUTES:
				unsetIncludeAllAttributes();
				return;
			case EORMPackage.NAMED_ENTITY_GRAPH__NAME:
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
			case EORMPackage.NAMED_ENTITY_GRAPH__NAMED_ATTRIBUTE_NODE:
				return namedAttributeNode != null && !namedAttributeNode.isEmpty();
			case EORMPackage.NAMED_ENTITY_GRAPH__SUBGRAPH:
				return subgraph != null && !subgraph.isEmpty();
			case EORMPackage.NAMED_ENTITY_GRAPH__SUBCLASS_SUBGRAPH:
				return subclassSubgraph != null && !subclassSubgraph.isEmpty();
			case EORMPackage.NAMED_ENTITY_GRAPH__INCLUDE_ALL_ATTRIBUTES:
				return isSetIncludeAllAttributes();
			case EORMPackage.NAMED_ENTITY_GRAPH__NAME:
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
		result.append(" (includeAllAttributes: ");
		if (includeAllAttributesESet) result.append(includeAllAttributes); else result.append("<unset>");
		result.append(", name: ");
		result.append(name);
		result.append(')');
		return result.toString();
	}

} //NamedEntityGraphImpl
