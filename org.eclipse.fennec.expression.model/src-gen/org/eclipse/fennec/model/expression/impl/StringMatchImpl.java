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
import org.eclipse.fennec.model.expression.StringMatch;
import org.eclipse.fennec.model.expression.StringMatchKind;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>String Match</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.StringMatchImpl#getKind <em>Kind</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.StringMatchImpl#isCaseInsensitive <em>Case Insensitive</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.StringMatchImpl#getMaxEdits <em>Max Edits</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.StringMatchImpl#getPrefixLength <em>Prefix Length</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.StringMatchImpl#getSource <em>Source</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.expression.impl.StringMatchImpl#getPattern <em>Pattern</em>}</li>
 * </ul>
 *
 * @generated
 */
public class StringMatchImpl extends ExpressionImpl implements StringMatch {
	/**
	 * The default value of the '{@link #getKind() <em>Kind</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getKind()
	 * @generated
	 * @ordered
	 */
	protected static final StringMatchKind KIND_EDEFAULT = StringMatchKind.CONTAINS;

	/**
	 * The cached value of the '{@link #getKind() <em>Kind</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getKind()
	 * @generated
	 * @ordered
	 */
	protected StringMatchKind kind = KIND_EDEFAULT;

	/**
	 * The default value of the '{@link #isCaseInsensitive() <em>Case Insensitive</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isCaseInsensitive()
	 * @generated
	 * @ordered
	 */
	protected static final boolean CASE_INSENSITIVE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isCaseInsensitive() <em>Case Insensitive</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isCaseInsensitive()
	 * @generated
	 * @ordered
	 */
	protected boolean caseInsensitive = CASE_INSENSITIVE_EDEFAULT;

	/**
	 * The default value of the '{@link #getMaxEdits() <em>Max Edits</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaxEdits()
	 * @generated
	 * @ordered
	 */
	protected static final int MAX_EDITS_EDEFAULT = 2;

	/**
	 * The cached value of the '{@link #getMaxEdits() <em>Max Edits</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaxEdits()
	 * @generated
	 * @ordered
	 */
	protected int maxEdits = MAX_EDITS_EDEFAULT;

	/**
	 * This is true if the Max Edits attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean maxEditsESet;

	/**
	 * The default value of the '{@link #getPrefixLength() <em>Prefix Length</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrefixLength()
	 * @generated
	 * @ordered
	 */
	protected static final int PREFIX_LENGTH_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getPrefixLength() <em>Prefix Length</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrefixLength()
	 * @generated
	 * @ordered
	 */
	protected int prefixLength = PREFIX_LENGTH_EDEFAULT;

	/**
	 * This is true if the Prefix Length attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean prefixLengthESet;

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
	 * The cached value of the '{@link #getPattern() <em>Pattern</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPattern()
	 * @generated
	 * @ordered
	 */
	protected Expression pattern;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected StringMatchImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ExpressionPackage.Literals.STRING_MATCH;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public StringMatchKind getKind() {
		return kind;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setKind(StringMatchKind newKind) {
		StringMatchKind oldKind = kind;
		kind = newKind == null ? KIND_EDEFAULT : newKind;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.STRING_MATCH__KIND, oldKind, kind));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isCaseInsensitive() {
		return caseInsensitive;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setCaseInsensitive(boolean newCaseInsensitive) {
		boolean oldCaseInsensitive = caseInsensitive;
		caseInsensitive = newCaseInsensitive;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.STRING_MATCH__CASE_INSENSITIVE, oldCaseInsensitive, caseInsensitive));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getMaxEdits() {
		return maxEdits;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMaxEdits(int newMaxEdits) {
		int oldMaxEdits = maxEdits;
		maxEdits = newMaxEdits;
		boolean oldMaxEditsESet = maxEditsESet;
		maxEditsESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.STRING_MATCH__MAX_EDITS, oldMaxEdits, maxEdits, !oldMaxEditsESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetMaxEdits() {
		int oldMaxEdits = maxEdits;
		boolean oldMaxEditsESet = maxEditsESet;
		maxEdits = MAX_EDITS_EDEFAULT;
		maxEditsESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, ExpressionPackage.STRING_MATCH__MAX_EDITS, oldMaxEdits, MAX_EDITS_EDEFAULT, oldMaxEditsESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetMaxEdits() {
		return maxEditsESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getPrefixLength() {
		return prefixLength;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPrefixLength(int newPrefixLength) {
		int oldPrefixLength = prefixLength;
		prefixLength = newPrefixLength;
		boolean oldPrefixLengthESet = prefixLengthESet;
		prefixLengthESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.STRING_MATCH__PREFIX_LENGTH, oldPrefixLength, prefixLength, !oldPrefixLengthESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetPrefixLength() {
		int oldPrefixLength = prefixLength;
		boolean oldPrefixLengthESet = prefixLengthESet;
		prefixLength = PREFIX_LENGTH_EDEFAULT;
		prefixLengthESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, ExpressionPackage.STRING_MATCH__PREFIX_LENGTH, oldPrefixLength, PREFIX_LENGTH_EDEFAULT, oldPrefixLengthESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetPrefixLength() {
		return prefixLengthESet;
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ExpressionPackage.STRING_MATCH__SOURCE, oldSource, newSource);
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
				msgs = ((InternalEObject)source).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.STRING_MATCH__SOURCE, null, msgs);
			if (newSource != null)
				msgs = ((InternalEObject)newSource).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.STRING_MATCH__SOURCE, null, msgs);
			msgs = basicSetSource(newSource, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.STRING_MATCH__SOURCE, newSource, newSource));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Expression getPattern() {
		return pattern;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPattern(Expression newPattern, NotificationChain msgs) {
		Expression oldPattern = pattern;
		pattern = newPattern;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ExpressionPackage.STRING_MATCH__PATTERN, oldPattern, newPattern);
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
	public void setPattern(Expression newPattern) {
		if (newPattern != pattern) {
			NotificationChain msgs = null;
			if (pattern != null)
				msgs = ((InternalEObject)pattern).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.STRING_MATCH__PATTERN, null, msgs);
			if (newPattern != null)
				msgs = ((InternalEObject)newPattern).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ExpressionPackage.STRING_MATCH__PATTERN, null, msgs);
			msgs = basicSetPattern(newPattern, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ExpressionPackage.STRING_MATCH__PATTERN, newPattern, newPattern));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ExpressionPackage.STRING_MATCH__SOURCE:
				return basicSetSource(null, msgs);
			case ExpressionPackage.STRING_MATCH__PATTERN:
				return basicSetPattern(null, msgs);
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
			case ExpressionPackage.STRING_MATCH__KIND:
				return getKind();
			case ExpressionPackage.STRING_MATCH__CASE_INSENSITIVE:
				return isCaseInsensitive();
			case ExpressionPackage.STRING_MATCH__MAX_EDITS:
				return getMaxEdits();
			case ExpressionPackage.STRING_MATCH__PREFIX_LENGTH:
				return getPrefixLength();
			case ExpressionPackage.STRING_MATCH__SOURCE:
				return getSource();
			case ExpressionPackage.STRING_MATCH__PATTERN:
				return getPattern();
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
			case ExpressionPackage.STRING_MATCH__KIND:
				setKind((StringMatchKind)newValue);
				return;
			case ExpressionPackage.STRING_MATCH__CASE_INSENSITIVE:
				setCaseInsensitive((Boolean)newValue);
				return;
			case ExpressionPackage.STRING_MATCH__MAX_EDITS:
				setMaxEdits((Integer)newValue);
				return;
			case ExpressionPackage.STRING_MATCH__PREFIX_LENGTH:
				setPrefixLength((Integer)newValue);
				return;
			case ExpressionPackage.STRING_MATCH__SOURCE:
				setSource((Expression)newValue);
				return;
			case ExpressionPackage.STRING_MATCH__PATTERN:
				setPattern((Expression)newValue);
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
			case ExpressionPackage.STRING_MATCH__KIND:
				setKind(KIND_EDEFAULT);
				return;
			case ExpressionPackage.STRING_MATCH__CASE_INSENSITIVE:
				setCaseInsensitive(CASE_INSENSITIVE_EDEFAULT);
				return;
			case ExpressionPackage.STRING_MATCH__MAX_EDITS:
				unsetMaxEdits();
				return;
			case ExpressionPackage.STRING_MATCH__PREFIX_LENGTH:
				unsetPrefixLength();
				return;
			case ExpressionPackage.STRING_MATCH__SOURCE:
				setSource((Expression)null);
				return;
			case ExpressionPackage.STRING_MATCH__PATTERN:
				setPattern((Expression)null);
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
			case ExpressionPackage.STRING_MATCH__KIND:
				return kind != KIND_EDEFAULT;
			case ExpressionPackage.STRING_MATCH__CASE_INSENSITIVE:
				return caseInsensitive != CASE_INSENSITIVE_EDEFAULT;
			case ExpressionPackage.STRING_MATCH__MAX_EDITS:
				return isSetMaxEdits();
			case ExpressionPackage.STRING_MATCH__PREFIX_LENGTH:
				return isSetPrefixLength();
			case ExpressionPackage.STRING_MATCH__SOURCE:
				return source != null;
			case ExpressionPackage.STRING_MATCH__PATTERN:
				return pattern != null;
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
		result.append(" (kind: ");
		result.append(kind);
		result.append(", caseInsensitive: ");
		result.append(caseInsensitive);
		result.append(", maxEdits: ");
		if (maxEditsESet) result.append(maxEdits); else result.append("<unset>");
		result.append(", prefixLength: ");
		if (prefixLengthESet) result.append(prefixLength); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //StringMatchImpl
