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
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.persistence.eorm.Converter;
import org.eclipse.fennec.persistence.eorm.EORMPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Converter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ConverterImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ConverterImpl#isAutoApply <em>Auto Apply</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ConverterImpl#getClass_ <em>Class</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ConverterImpl extends MinimalEObjectImpl.Container implements Converter {
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
	 * The default value of the '{@link #isAutoApply() <em>Auto Apply</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isAutoApply()
	 * @generated
	 * @ordered
	 */
	protected static final boolean AUTO_APPLY_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isAutoApply() <em>Auto Apply</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isAutoApply()
	 * @generated
	 * @ordered
	 */
	protected boolean autoApply = AUTO_APPLY_EDEFAULT;

	/**
	 * This is true if the Auto Apply attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean autoApplyESet;

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
	protected ConverterImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EORMPackage.eINSTANCE.getConverter();
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.CONVERTER__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isAutoApply() {
		return autoApply;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAutoApply(boolean newAutoApply) {
		boolean oldAutoApply = autoApply;
		autoApply = newAutoApply;
		boolean oldAutoApplyESet = autoApplyESet;
		autoApplyESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.CONVERTER__AUTO_APPLY, oldAutoApply, autoApply, !oldAutoApplyESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetAutoApply() {
		boolean oldAutoApply = autoApply;
		boolean oldAutoApplyESet = autoApplyESet;
		autoApply = AUTO_APPLY_EDEFAULT;
		autoApplyESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EORMPackage.CONVERTER__AUTO_APPLY, oldAutoApply, AUTO_APPLY_EDEFAULT, oldAutoApplyESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetAutoApply() {
		return autoApplyESet;
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
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, EORMPackage.CONVERTER__CLASS, oldClass, class_));
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.CONVERTER__CLASS, oldClass, class_));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case EORMPackage.CONVERTER__DESCRIPTION:
				return getDescription();
			case EORMPackage.CONVERTER__AUTO_APPLY:
				return isAutoApply();
			case EORMPackage.CONVERTER__CLASS:
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
			case EORMPackage.CONVERTER__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case EORMPackage.CONVERTER__AUTO_APPLY:
				setAutoApply((Boolean)newValue);
				return;
			case EORMPackage.CONVERTER__CLASS:
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
			case EORMPackage.CONVERTER__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case EORMPackage.CONVERTER__AUTO_APPLY:
				unsetAutoApply();
				return;
			case EORMPackage.CONVERTER__CLASS:
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
			case EORMPackage.CONVERTER__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case EORMPackage.CONVERTER__AUTO_APPLY:
				return isSetAutoApply();
			case EORMPackage.CONVERTER__CLASS:
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
		result.append(", autoApply: ");
		if (autoApplyESet) result.append(autoApply); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //ConverterImpl
