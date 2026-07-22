/*
 * Copyright (c) 2012 - 2026 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.model.query.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.fennec.model.query.IsInRange;
import org.eclipse.fennec.model.query.QueryPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Is In Range</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.query.impl.IsInRangeImpl#getStartValue <em>Start Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.IsInRangeImpl#getEndValue <em>End Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.IsInRangeImpl#isStartIncluded <em>Start Included</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.query.impl.IsInRangeImpl#isEndIncluded <em>End Included</em>}</li>
 * </ul>
 *
 * @generated
 */
public class IsInRangeImpl extends ComparatorImpl implements IsInRange {
	/**
	 * The default value of the '{@link #getStartValue() <em>Start Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStartValue()
	 * @generated
	 * @ordered
	 */
	protected static final String START_VALUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getStartValue() <em>Start Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStartValue()
	 * @generated
	 * @ordered
	 */
	protected String startValue = START_VALUE_EDEFAULT;

	/**
	 * The default value of the '{@link #getEndValue() <em>End Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEndValue()
	 * @generated
	 * @ordered
	 */
	protected static final String END_VALUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getEndValue() <em>End Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEndValue()
	 * @generated
	 * @ordered
	 */
	protected String endValue = END_VALUE_EDEFAULT;

	/**
	 * The default value of the '{@link #isStartIncluded() <em>Start Included</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isStartIncluded()
	 * @generated
	 * @ordered
	 */
	protected static final boolean START_INCLUDED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isStartIncluded() <em>Start Included</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isStartIncluded()
	 * @generated
	 * @ordered
	 */
	protected boolean startIncluded = START_INCLUDED_EDEFAULT;

	/**
	 * The default value of the '{@link #isEndIncluded() <em>End Included</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isEndIncluded()
	 * @generated
	 * @ordered
	 */
	protected static final boolean END_INCLUDED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isEndIncluded() <em>End Included</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isEndIncluded()
	 * @generated
	 * @ordered
	 */
	protected boolean endIncluded = END_INCLUDED_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected IsInRangeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return QueryPackage.Literals.IS_IN_RANGE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getStartValue() {
		return startValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setStartValue(String newStartValue) {
		String oldStartValue = startValue;
		startValue = newStartValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.IS_IN_RANGE__START_VALUE, oldStartValue, startValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getEndValue() {
		return endValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setEndValue(String newEndValue) {
		String oldEndValue = endValue;
		endValue = newEndValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.IS_IN_RANGE__END_VALUE, oldEndValue, endValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isStartIncluded() {
		return startIncluded;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setStartIncluded(boolean newStartIncluded) {
		boolean oldStartIncluded = startIncluded;
		startIncluded = newStartIncluded;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.IS_IN_RANGE__START_INCLUDED, oldStartIncluded, startIncluded));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isEndIncluded() {
		return endIncluded;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setEndIncluded(boolean newEndIncluded) {
		boolean oldEndIncluded = endIncluded;
		endIncluded = newEndIncluded;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QueryPackage.IS_IN_RANGE__END_INCLUDED, oldEndIncluded, endIncluded));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case QueryPackage.IS_IN_RANGE__START_VALUE:
				return getStartValue();
			case QueryPackage.IS_IN_RANGE__END_VALUE:
				return getEndValue();
			case QueryPackage.IS_IN_RANGE__START_INCLUDED:
				return isStartIncluded();
			case QueryPackage.IS_IN_RANGE__END_INCLUDED:
				return isEndIncluded();
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
			case QueryPackage.IS_IN_RANGE__START_VALUE:
				setStartValue((String)newValue);
				return;
			case QueryPackage.IS_IN_RANGE__END_VALUE:
				setEndValue((String)newValue);
				return;
			case QueryPackage.IS_IN_RANGE__START_INCLUDED:
				setStartIncluded((Boolean)newValue);
				return;
			case QueryPackage.IS_IN_RANGE__END_INCLUDED:
				setEndIncluded((Boolean)newValue);
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
			case QueryPackage.IS_IN_RANGE__START_VALUE:
				setStartValue(START_VALUE_EDEFAULT);
				return;
			case QueryPackage.IS_IN_RANGE__END_VALUE:
				setEndValue(END_VALUE_EDEFAULT);
				return;
			case QueryPackage.IS_IN_RANGE__START_INCLUDED:
				setStartIncluded(START_INCLUDED_EDEFAULT);
				return;
			case QueryPackage.IS_IN_RANGE__END_INCLUDED:
				setEndIncluded(END_INCLUDED_EDEFAULT);
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
			case QueryPackage.IS_IN_RANGE__START_VALUE:
				return START_VALUE_EDEFAULT == null ? startValue != null : !START_VALUE_EDEFAULT.equals(startValue);
			case QueryPackage.IS_IN_RANGE__END_VALUE:
				return END_VALUE_EDEFAULT == null ? endValue != null : !END_VALUE_EDEFAULT.equals(endValue);
			case QueryPackage.IS_IN_RANGE__START_INCLUDED:
				return startIncluded != START_INCLUDED_EDEFAULT;
			case QueryPackage.IS_IN_RANGE__END_INCLUDED:
				return endIncluded != END_INCLUDED_EDEFAULT;
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
		result.append(" (startValue: ");
		result.append(startValue);
		result.append(", endValue: ");
		result.append(endValue);
		result.append(", startIncluded: ");
		result.append(startIncluded);
		result.append(", endIncluded: ");
		result.append(endIncluded);
		result.append(')');
		return result.toString();
	}

} //IsInRangeImpl
