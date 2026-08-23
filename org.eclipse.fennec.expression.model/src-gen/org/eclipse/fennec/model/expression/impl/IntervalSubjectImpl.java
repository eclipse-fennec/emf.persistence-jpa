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
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.model.expression.ExpressionPackage;
import org.eclipse.fennec.model.expression.IntervalSubject;
import org.eclipse.fennec.model.expression.PropertyPath;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Interval Subject</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.IntervalSubjectImpl#isLowerIncluded <em>Lower Included</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.IntervalSubjectImpl#isUpperIncluded <em>Upper Included</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.IntervalSubjectImpl#isNullMeansUnbounded <em>Null Means Unbounded</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.IntervalSubjectImpl#getPathLower <em>Path Lower</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.IntervalSubjectImpl#getPathUpper <em>Path Upper</em>}</li>
 * </ul>
 *
 * @generated
 */
public class IntervalSubjectImpl extends MinimalEObjectImpl.Container implements IntervalSubject {
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
	 * The default value of the '{@link #isNullMeansUnbounded() <em>Null Means Unbounded</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isNullMeansUnbounded()
	 * @generated
	 * @ordered
	 */
	protected static final boolean NULL_MEANS_UNBOUNDED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isNullMeansUnbounded() <em>Null Means Unbounded</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isNullMeansUnbounded()
	 * @generated
	 * @ordered
	 */
	protected boolean nullMeansUnbounded = NULL_MEANS_UNBOUNDED_EDEFAULT;

	/**
	 * The cached value of the '{@link #getPathLower() <em>Path Lower</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPathLower()
	 * @generated
	 * @ordered
	 */
	protected PropertyPath pathLower;

	/**
	 * The cached value of the '{@link #getPathUpper() <em>Path Upper</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPathUpper()
	 * @generated
	 * @ordered
	 */
	protected PropertyPath pathUpper;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected IntervalSubjectImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ExpressionPackage.Literals.INTERVAL_SUBJECT;
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
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.INTERVAL_SUBJECT__LOWER_INCLUDED, oldLowerIncluded, lowerIncluded));
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
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.INTERVAL_SUBJECT__UPPER_INCLUDED, oldUpperIncluded, upperIncluded));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isNullMeansUnbounded() {
		return nullMeansUnbounded;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setNullMeansUnbounded(boolean newNullMeansUnbounded) {
		boolean oldNullMeansUnbounded = nullMeansUnbounded;
		nullMeansUnbounded = newNullMeansUnbounded;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.INTERVAL_SUBJECT__NULL_MEANS_UNBOUNDED, oldNullMeansUnbounded, nullMeansUnbounded));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PropertyPath getPathLower() {
		return pathLower;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPathLower(PropertyPath newPathLower, NotificationChain msgs) {
		PropertyPath oldPathLower = pathLower;
		pathLower = newPathLower;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ExpressionPackage.INTERVAL_SUBJECT__PATH_LOWER, oldPathLower, newPathLower);
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
	public void setPathLower(PropertyPath newPathLower) {
		if (newPathLower != pathLower) {
			NotificationChain msgs = null;
			if (pathLower != null)
				msgs = ((InternalEObject)pathLower).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.INTERVAL_SUBJECT__PATH_LOWER, null, msgs);
			if (newPathLower != null)
				msgs = ((InternalEObject)newPathLower).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.INTERVAL_SUBJECT__PATH_LOWER, null, msgs);
			msgs = basicSetPathLower(newPathLower, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.INTERVAL_SUBJECT__PATH_LOWER, newPathLower, newPathLower));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PropertyPath getPathUpper() {
		return pathUpper;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPathUpper(PropertyPath newPathUpper, NotificationChain msgs) {
		PropertyPath oldPathUpper = pathUpper;
		pathUpper = newPathUpper;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ExpressionPackage.INTERVAL_SUBJECT__PATH_UPPER, oldPathUpper, newPathUpper);
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
	public void setPathUpper(PropertyPath newPathUpper) {
		if (newPathUpper != pathUpper) {
			NotificationChain msgs = null;
			if (pathUpper != null)
				msgs = ((InternalEObject)pathUpper).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.INTERVAL_SUBJECT__PATH_UPPER, null, msgs);
			if (newPathUpper != null)
				msgs = ((InternalEObject)newPathUpper).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.INTERVAL_SUBJECT__PATH_UPPER, null, msgs);
			msgs = basicSetPathUpper(newPathUpper, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.INTERVAL_SUBJECT__PATH_UPPER, newPathUpper, newPathUpper));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ExpressionPackage.INTERVAL_SUBJECT__PATH_LOWER:
				return basicSetPathLower(null, msgs);
			case ExpressionPackage.INTERVAL_SUBJECT__PATH_UPPER:
				return basicSetPathUpper(null, msgs);
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
			case ExpressionPackage.INTERVAL_SUBJECT__LOWER_INCLUDED:
				return isLowerIncluded();
			case ExpressionPackage.INTERVAL_SUBJECT__UPPER_INCLUDED:
				return isUpperIncluded();
			case ExpressionPackage.INTERVAL_SUBJECT__NULL_MEANS_UNBOUNDED:
				return isNullMeansUnbounded();
			case ExpressionPackage.INTERVAL_SUBJECT__PATH_LOWER:
				return getPathLower();
			case ExpressionPackage.INTERVAL_SUBJECT__PATH_UPPER:
				return getPathUpper();
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
			case ExpressionPackage.INTERVAL_SUBJECT__LOWER_INCLUDED:
				setLowerIncluded((Boolean)newValue);
				return;
			case ExpressionPackage.INTERVAL_SUBJECT__UPPER_INCLUDED:
				setUpperIncluded((Boolean)newValue);
				return;
			case ExpressionPackage.INTERVAL_SUBJECT__NULL_MEANS_UNBOUNDED:
				setNullMeansUnbounded((Boolean)newValue);
				return;
			case ExpressionPackage.INTERVAL_SUBJECT__PATH_LOWER:
				setPathLower((PropertyPath)newValue);
				return;
			case ExpressionPackage.INTERVAL_SUBJECT__PATH_UPPER:
				setPathUpper((PropertyPath)newValue);
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
			case ExpressionPackage.INTERVAL_SUBJECT__LOWER_INCLUDED:
				setLowerIncluded(LOWER_INCLUDED_EDEFAULT);
				return;
			case ExpressionPackage.INTERVAL_SUBJECT__UPPER_INCLUDED:
				setUpperIncluded(UPPER_INCLUDED_EDEFAULT);
				return;
			case ExpressionPackage.INTERVAL_SUBJECT__NULL_MEANS_UNBOUNDED:
				setNullMeansUnbounded(NULL_MEANS_UNBOUNDED_EDEFAULT);
				return;
			case ExpressionPackage.INTERVAL_SUBJECT__PATH_LOWER:
				setPathLower((PropertyPath)null);
				return;
			case ExpressionPackage.INTERVAL_SUBJECT__PATH_UPPER:
				setPathUpper((PropertyPath)null);
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
			case ExpressionPackage.INTERVAL_SUBJECT__LOWER_INCLUDED:
				return lowerIncluded != LOWER_INCLUDED_EDEFAULT;
			case ExpressionPackage.INTERVAL_SUBJECT__UPPER_INCLUDED:
				return upperIncluded != UPPER_INCLUDED_EDEFAULT;
			case ExpressionPackage.INTERVAL_SUBJECT__NULL_MEANS_UNBOUNDED:
				return nullMeansUnbounded != NULL_MEANS_UNBOUNDED_EDEFAULT;
			case ExpressionPackage.INTERVAL_SUBJECT__PATH_LOWER:
				return pathLower != null;
			case ExpressionPackage.INTERVAL_SUBJECT__PATH_UPPER:
				return pathUpper != null;
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
		result.append(", nullMeansUnbounded: ");
		result.append(nullMeansUnbounded);
		result.append(')');
		return result.toString();
	}

} //IntervalSubjectImpl
