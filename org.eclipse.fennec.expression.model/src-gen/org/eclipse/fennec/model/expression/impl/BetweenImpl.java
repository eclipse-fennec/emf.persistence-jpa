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
package org.eclipse.fennec.model.expression.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.fennec.model.expression.Between;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.ExpressionPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Between</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.BetweenImpl#isLowerIncluded <em>Lower Included</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.BetweenImpl#isUpperIncluded <em>Upper Included</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.BetweenImpl#getSource <em>Source</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.BetweenImpl#getLower <em>Lower</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.BetweenImpl#getUpper <em>Upper</em>}</li>
 * </ul>
 *
 * @generated
 */
public class BetweenImpl extends ExpressionImpl implements Between {
	/**
	 * The default value of the '{@link #isLowerIncluded() <em>Lower Included</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isLowerIncluded()
	 * @generated
	 * @ordered
	 */
	protected static final boolean LOWER_INCLUDED_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isLowerIncluded() <em>Lower Included</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isLowerIncluded()
	 * @generated
	 * @ordered
	 */
	protected boolean lowerIncluded = LOWER_INCLUDED_EDEFAULT;

	/**
	 * The default value of the '{@link #isUpperIncluded() <em>Upper Included</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isUpperIncluded()
	 * @generated
	 * @ordered
	 */
	protected static final boolean UPPER_INCLUDED_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isUpperIncluded() <em>Upper Included</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isUpperIncluded()
	 * @generated
	 * @ordered
	 */
	protected boolean upperIncluded = UPPER_INCLUDED_EDEFAULT;

	/**
	 * The cached value of the '{@link #getSource() <em>Source</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSource()
	 * @generated
	 * @ordered
	 */
	protected Expression source;

	/**
	 * The cached value of the '{@link #getLower() <em>Lower</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLower()
	 * @generated
	 * @ordered
	 */
	protected Expression lower;

	/**
	 * The cached value of the '{@link #getUpper() <em>Upper</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUpper()
	 * @generated
	 * @ordered
	 */
	protected Expression upper;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected BetweenImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ExpressionPackage.Literals.BETWEEN;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isLowerIncluded() {
		return lowerIncluded;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setLowerIncluded(boolean newLowerIncluded) {
		boolean oldLowerIncluded = lowerIncluded;
		lowerIncluded = newLowerIncluded;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.BETWEEN__LOWER_INCLUDED, oldLowerIncluded, lowerIncluded));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isUpperIncluded() {
		return upperIncluded;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setUpperIncluded(boolean newUpperIncluded) {
		boolean oldUpperIncluded = upperIncluded;
		upperIncluded = newUpperIncluded;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.BETWEEN__UPPER_INCLUDED, oldUpperIncluded, upperIncluded));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Expression getSource() {
		return source;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSource(Expression newSource, NotificationChain msgs) {
		Expression oldSource = source;
		source = newSource;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ExpressionPackage.BETWEEN__SOURCE, oldSource, newSource);
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
	public void setSource(Expression newSource) {
		if (newSource != source) {
			NotificationChain msgs = null;
			if (source != null)
				msgs = ((InternalEObject)source).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.BETWEEN__SOURCE, null, msgs);
			if (newSource != null)
				msgs = ((InternalEObject)newSource).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.BETWEEN__SOURCE, null, msgs);
			msgs = basicSetSource(newSource, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.BETWEEN__SOURCE, newSource, newSource));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Expression getLower() {
		return lower;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetLower(Expression newLower, NotificationChain msgs) {
		Expression oldLower = lower;
		lower = newLower;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ExpressionPackage.BETWEEN__LOWER, oldLower, newLower);
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
	public void setLower(Expression newLower) {
		if (newLower != lower) {
			NotificationChain msgs = null;
			if (lower != null)
				msgs = ((InternalEObject)lower).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.BETWEEN__LOWER, null, msgs);
			if (newLower != null)
				msgs = ((InternalEObject)newLower).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.BETWEEN__LOWER, null, msgs);
			msgs = basicSetLower(newLower, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.BETWEEN__LOWER, newLower, newLower));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Expression getUpper() {
		return upper;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetUpper(Expression newUpper, NotificationChain msgs) {
		Expression oldUpper = upper;
		upper = newUpper;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ExpressionPackage.BETWEEN__UPPER, oldUpper, newUpper);
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
	public void setUpper(Expression newUpper) {
		if (newUpper != upper) {
			NotificationChain msgs = null;
			if (upper != null)
				msgs = ((InternalEObject)upper).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.BETWEEN__UPPER, null, msgs);
			if (newUpper != null)
				msgs = ((InternalEObject)newUpper).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.BETWEEN__UPPER, null, msgs);
			msgs = basicSetUpper(newUpper, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.BETWEEN__UPPER, newUpper, newUpper));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ExpressionPackage.BETWEEN__SOURCE:
				return basicSetSource(null, msgs);
			case ExpressionPackage.BETWEEN__LOWER:
				return basicSetLower(null, msgs);
			case ExpressionPackage.BETWEEN__UPPER:
				return basicSetUpper(null, msgs);
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
			case ExpressionPackage.BETWEEN__LOWER_INCLUDED:
				return isLowerIncluded();
			case ExpressionPackage.BETWEEN__UPPER_INCLUDED:
				return isUpperIncluded();
			case ExpressionPackage.BETWEEN__SOURCE:
				return getSource();
			case ExpressionPackage.BETWEEN__LOWER:
				return getLower();
			case ExpressionPackage.BETWEEN__UPPER:
				return getUpper();
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
			case ExpressionPackage.BETWEEN__LOWER_INCLUDED:
				setLowerIncluded((Boolean)newValue);
				return;
			case ExpressionPackage.BETWEEN__UPPER_INCLUDED:
				setUpperIncluded((Boolean)newValue);
				return;
			case ExpressionPackage.BETWEEN__SOURCE:
				setSource((Expression)newValue);
				return;
			case ExpressionPackage.BETWEEN__LOWER:
				setLower((Expression)newValue);
				return;
			case ExpressionPackage.BETWEEN__UPPER:
				setUpper((Expression)newValue);
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
			case ExpressionPackage.BETWEEN__LOWER_INCLUDED:
				setLowerIncluded(LOWER_INCLUDED_EDEFAULT);
				return;
			case ExpressionPackage.BETWEEN__UPPER_INCLUDED:
				setUpperIncluded(UPPER_INCLUDED_EDEFAULT);
				return;
			case ExpressionPackage.BETWEEN__SOURCE:
				setSource((Expression)null);
				return;
			case ExpressionPackage.BETWEEN__LOWER:
				setLower((Expression)null);
				return;
			case ExpressionPackage.BETWEEN__UPPER:
				setUpper((Expression)null);
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
			case ExpressionPackage.BETWEEN__LOWER_INCLUDED:
				return lowerIncluded != LOWER_INCLUDED_EDEFAULT;
			case ExpressionPackage.BETWEEN__UPPER_INCLUDED:
				return upperIncluded != UPPER_INCLUDED_EDEFAULT;
			case ExpressionPackage.BETWEEN__SOURCE:
				return source != null;
			case ExpressionPackage.BETWEEN__LOWER:
				return lower != null;
			case ExpressionPackage.BETWEEN__UPPER:
				return upper != null;
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
		result.append(" (lowerIncluded: ");
		result.append(lowerIncluded);
		result.append(", upperIncluded: ");
		result.append(upperIncluded);
		result.append(')');
		return result.toString();
	}

} //BetweenImpl
