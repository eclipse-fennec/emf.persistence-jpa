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

import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.ExpressionPackage;
import org.eclipse.fennec.model.expression.IntervalMatch;
import org.eclipse.fennec.model.expression.IntervalRelation;
import org.eclipse.fennec.model.expression.IntervalSubject;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Interval Match</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.IntervalMatchImpl#getRelation <em>Relation</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.IntervalMatchImpl#isLowerIncluded <em>Lower Included</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.IntervalMatchImpl#isUpperIncluded <em>Upper Included</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.IntervalMatchImpl#getSubject <em>Subject</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.IntervalMatchImpl#getLower <em>Lower</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.IntervalMatchImpl#getUpper <em>Upper</em>}</li>
 * </ul>
 *
 * @generated
 */
public class IntervalMatchImpl extends ExpressionImpl implements IntervalMatch {
	/**
	 * The default value of the '{@link #getRelation() <em>Relation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRelation()
	 * @generated
	 * @ordered
	 */
	protected static final IntervalRelation RELATION_EDEFAULT = IntervalRelation.INTERSECTS;

	/**
	 * The cached value of the '{@link #getRelation() <em>Relation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRelation()
	 * @generated
	 * @ordered
	 */
	protected IntervalRelation relation = RELATION_EDEFAULT;

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
	 * The cached value of the '{@link #getSubject() <em>Subject</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubject()
	 * @generated
	 * @ordered
	 */
	protected IntervalSubject subject;

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
	protected IntervalMatchImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ExpressionPackage.Literals.INTERVAL_MATCH;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public IntervalRelation getRelation() {
		return relation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRelation(IntervalRelation newRelation) {
		IntervalRelation oldRelation = relation;
		relation = newRelation == null ? RELATION_EDEFAULT : newRelation;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.INTERVAL_MATCH__RELATION, oldRelation, relation));
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
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.INTERVAL_MATCH__LOWER_INCLUDED, oldLowerIncluded, lowerIncluded));
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
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.INTERVAL_MATCH__UPPER_INCLUDED, oldUpperIncluded, upperIncluded));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public IntervalSubject getSubject() {
		return subject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSubject(IntervalSubject newSubject, NotificationChain msgs) {
		IntervalSubject oldSubject = subject;
		subject = newSubject;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ExpressionPackage.INTERVAL_MATCH__SUBJECT, oldSubject, newSubject);
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
	public void setSubject(IntervalSubject newSubject) {
		if (newSubject != subject) {
			NotificationChain msgs = null;
			if (subject != null)
				msgs = ((InternalEObject)subject).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.INTERVAL_MATCH__SUBJECT, null, msgs);
			if (newSubject != null)
				msgs = ((InternalEObject)newSubject).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.INTERVAL_MATCH__SUBJECT, null, msgs);
			msgs = basicSetSubject(newSubject, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.INTERVAL_MATCH__SUBJECT, newSubject, newSubject));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ExpressionPackage.INTERVAL_MATCH__LOWER, oldLower, newLower);
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
				msgs = ((InternalEObject)lower).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.INTERVAL_MATCH__LOWER, null, msgs);
			if (newLower != null)
				msgs = ((InternalEObject)newLower).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.INTERVAL_MATCH__LOWER, null, msgs);
			msgs = basicSetLower(newLower, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.INTERVAL_MATCH__LOWER, newLower, newLower));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ExpressionPackage.INTERVAL_MATCH__UPPER, oldUpper, newUpper);
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
				msgs = ((InternalEObject)upper).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.INTERVAL_MATCH__UPPER, null, msgs);
			if (newUpper != null)
				msgs = ((InternalEObject)newUpper).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.INTERVAL_MATCH__UPPER, null, msgs);
			msgs = basicSetUpper(newUpper, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.INTERVAL_MATCH__UPPER, newUpper, newUpper));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ExpressionPackage.INTERVAL_MATCH__SUBJECT:
				return basicSetSubject(null, msgs);
			case ExpressionPackage.INTERVAL_MATCH__LOWER:
				return basicSetLower(null, msgs);
			case ExpressionPackage.INTERVAL_MATCH__UPPER:
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
			case ExpressionPackage.INTERVAL_MATCH__RELATION:
				return getRelation();
			case ExpressionPackage.INTERVAL_MATCH__LOWER_INCLUDED:
				return isLowerIncluded();
			case ExpressionPackage.INTERVAL_MATCH__UPPER_INCLUDED:
				return isUpperIncluded();
			case ExpressionPackage.INTERVAL_MATCH__SUBJECT:
				return getSubject();
			case ExpressionPackage.INTERVAL_MATCH__LOWER:
				return getLower();
			case ExpressionPackage.INTERVAL_MATCH__UPPER:
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
			case ExpressionPackage.INTERVAL_MATCH__RELATION:
				setRelation((IntervalRelation)newValue);
				return;
			case ExpressionPackage.INTERVAL_MATCH__LOWER_INCLUDED:
				setLowerIncluded((Boolean)newValue);
				return;
			case ExpressionPackage.INTERVAL_MATCH__UPPER_INCLUDED:
				setUpperIncluded((Boolean)newValue);
				return;
			case ExpressionPackage.INTERVAL_MATCH__SUBJECT:
				setSubject((IntervalSubject)newValue);
				return;
			case ExpressionPackage.INTERVAL_MATCH__LOWER:
				setLower((Expression)newValue);
				return;
			case ExpressionPackage.INTERVAL_MATCH__UPPER:
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
			case ExpressionPackage.INTERVAL_MATCH__RELATION:
				setRelation(RELATION_EDEFAULT);
				return;
			case ExpressionPackage.INTERVAL_MATCH__LOWER_INCLUDED:
				setLowerIncluded(LOWER_INCLUDED_EDEFAULT);
				return;
			case ExpressionPackage.INTERVAL_MATCH__UPPER_INCLUDED:
				setUpperIncluded(UPPER_INCLUDED_EDEFAULT);
				return;
			case ExpressionPackage.INTERVAL_MATCH__SUBJECT:
				setSubject((IntervalSubject)null);
				return;
			case ExpressionPackage.INTERVAL_MATCH__LOWER:
				setLower((Expression)null);
				return;
			case ExpressionPackage.INTERVAL_MATCH__UPPER:
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
			case ExpressionPackage.INTERVAL_MATCH__RELATION:
				return relation != RELATION_EDEFAULT;
			case ExpressionPackage.INTERVAL_MATCH__LOWER_INCLUDED:
				return lowerIncluded != LOWER_INCLUDED_EDEFAULT;
			case ExpressionPackage.INTERVAL_MATCH__UPPER_INCLUDED:
				return upperIncluded != UPPER_INCLUDED_EDEFAULT;
			case ExpressionPackage.INTERVAL_MATCH__SUBJECT:
				return subject != null;
			case ExpressionPackage.INTERVAL_MATCH__LOWER:
				return lower != null;
			case ExpressionPackage.INTERVAL_MATCH__UPPER:
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
		result.append(" (relation: ");
		result.append(relation);
		result.append(", lowerIncluded: ");
		result.append(lowerIncluded);
		result.append(", upperIncluded: ");
		result.append(upperIncluded);
		result.append(')');
		return result.toString();
	}

} //IntervalMatchImpl
