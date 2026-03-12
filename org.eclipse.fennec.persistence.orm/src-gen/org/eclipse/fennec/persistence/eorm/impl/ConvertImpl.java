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

import org.eclipse.fennec.persistence.eorm.Convert;
import org.eclipse.fennec.persistence.eorm.EORMPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Convert</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ConvertImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ConvertImpl#getAttributeName <em>Attribute Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ConvertImpl#getConverter <em>Converter</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ConvertImpl#isDisableConversion <em>Disable Conversion</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ConvertImpl extends MinimalEObjectImpl.Container implements Convert {
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
	 * The default value of the '{@link #getAttributeName() <em>Attribute Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAttributeName()
	 * @generated
	 * @ordered
	 */
	protected static final String ATTRIBUTE_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAttributeName() <em>Attribute Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAttributeName()
	 * @generated
	 * @ordered
	 */
	protected String attributeName = ATTRIBUTE_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getConverter() <em>Converter</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConverter()
	 * @generated
	 * @ordered
	 */
	protected static final String CONVERTER_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getConverter() <em>Converter</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConverter()
	 * @generated
	 * @ordered
	 */
	protected String converter = CONVERTER_EDEFAULT;

	/**
	 * The default value of the '{@link #isDisableConversion() <em>Disable Conversion</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDisableConversion()
	 * @generated
	 * @ordered
	 */
	protected static final boolean DISABLE_CONVERSION_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isDisableConversion() <em>Disable Conversion</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDisableConversion()
	 * @generated
	 * @ordered
	 */
	protected boolean disableConversion = DISABLE_CONVERSION_EDEFAULT;

	/**
	 * This is true if the Disable Conversion attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean disableConversionESet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ConvertImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EORMPackage.eINSTANCE.getConvert();
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.CONVERT__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getAttributeName() {
		return attributeName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAttributeName(String newAttributeName) {
		String oldAttributeName = attributeName;
		attributeName = newAttributeName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.CONVERT__ATTRIBUTE_NAME, oldAttributeName, attributeName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getConverter() {
		return converter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setConverter(String newConverter) {
		String oldConverter = converter;
		converter = newConverter;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.CONVERT__CONVERTER, oldConverter, converter));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isDisableConversion() {
		return disableConversion;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDisableConversion(boolean newDisableConversion) {
		boolean oldDisableConversion = disableConversion;
		disableConversion = newDisableConversion;
		boolean oldDisableConversionESet = disableConversionESet;
		disableConversionESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.CONVERT__DISABLE_CONVERSION, oldDisableConversion, disableConversion, !oldDisableConversionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetDisableConversion() {
		boolean oldDisableConversion = disableConversion;
		boolean oldDisableConversionESet = disableConversionESet;
		disableConversion = DISABLE_CONVERSION_EDEFAULT;
		disableConversionESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EORMPackage.CONVERT__DISABLE_CONVERSION, oldDisableConversion, DISABLE_CONVERSION_EDEFAULT, oldDisableConversionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetDisableConversion() {
		return disableConversionESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case EORMPackage.CONVERT__DESCRIPTION:
				return getDescription();
			case EORMPackage.CONVERT__ATTRIBUTE_NAME:
				return getAttributeName();
			case EORMPackage.CONVERT__CONVERTER:
				return getConverter();
			case EORMPackage.CONVERT__DISABLE_CONVERSION:
				return isDisableConversion();
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
			case EORMPackage.CONVERT__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case EORMPackage.CONVERT__ATTRIBUTE_NAME:
				setAttributeName((String)newValue);
				return;
			case EORMPackage.CONVERT__CONVERTER:
				setConverter((String)newValue);
				return;
			case EORMPackage.CONVERT__DISABLE_CONVERSION:
				setDisableConversion((Boolean)newValue);
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
			case EORMPackage.CONVERT__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case EORMPackage.CONVERT__ATTRIBUTE_NAME:
				setAttributeName(ATTRIBUTE_NAME_EDEFAULT);
				return;
			case EORMPackage.CONVERT__CONVERTER:
				setConverter(CONVERTER_EDEFAULT);
				return;
			case EORMPackage.CONVERT__DISABLE_CONVERSION:
				unsetDisableConversion();
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
			case EORMPackage.CONVERT__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case EORMPackage.CONVERT__ATTRIBUTE_NAME:
				return ATTRIBUTE_NAME_EDEFAULT == null ? attributeName != null : !ATTRIBUTE_NAME_EDEFAULT.equals(attributeName);
			case EORMPackage.CONVERT__CONVERTER:
				return CONVERTER_EDEFAULT == null ? converter != null : !CONVERTER_EDEFAULT.equals(converter);
			case EORMPackage.CONVERT__DISABLE_CONVERSION:
				return isSetDisableConversion();
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
		result.append(", attributeName: ");
		result.append(attributeName);
		result.append(", converter: ");
		result.append(converter);
		result.append(", disableConversion: ");
		if (disableConversionESet) result.append(disableConversion); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //ConvertImpl
