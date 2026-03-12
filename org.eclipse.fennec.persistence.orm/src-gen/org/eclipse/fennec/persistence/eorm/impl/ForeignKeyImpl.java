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

import org.eclipse.fennec.persistence.eorm.ConstraintMode;
import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.ForeignKey;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Foreign Key</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ForeignKeyImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ForeignKeyImpl#getConstraintMode <em>Constraint Mode</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ForeignKeyImpl#getForeignKeyDefinition <em>Foreign Key Definition</em>}</li>
 *   <li>{@link org.eclipse.fennec.persistence.eorm.impl.ForeignKeyImpl#getName <em>Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ForeignKeyImpl extends MinimalEObjectImpl.Container implements ForeignKey {
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
	 * The default value of the '{@link #getConstraintMode() <em>Constraint Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConstraintMode()
	 * @generated
	 * @ordered
	 */
	protected static final ConstraintMode CONSTRAINT_MODE_EDEFAULT = ConstraintMode.CONSTRAINT;

	/**
	 * The cached value of the '{@link #getConstraintMode() <em>Constraint Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConstraintMode()
	 * @generated
	 * @ordered
	 */
	protected ConstraintMode constraintMode = CONSTRAINT_MODE_EDEFAULT;

	/**
	 * This is true if the Constraint Mode attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean constraintModeESet;

	/**
	 * The default value of the '{@link #getForeignKeyDefinition() <em>Foreign Key Definition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getForeignKeyDefinition()
	 * @generated
	 * @ordered
	 */
	protected static final String FOREIGN_KEY_DEFINITION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getForeignKeyDefinition() <em>Foreign Key Definition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getForeignKeyDefinition()
	 * @generated
	 * @ordered
	 */
	protected String foreignKeyDefinition = FOREIGN_KEY_DEFINITION_EDEFAULT;

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
	protected ForeignKeyImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EORMPackage.eINSTANCE.getForeignKey();
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.FOREIGN_KEY__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ConstraintMode getConstraintMode() {
		return constraintMode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setConstraintMode(ConstraintMode newConstraintMode) {
		ConstraintMode oldConstraintMode = constraintMode;
		constraintMode = newConstraintMode == null ? CONSTRAINT_MODE_EDEFAULT : newConstraintMode;
		boolean oldConstraintModeESet = constraintModeESet;
		constraintModeESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.FOREIGN_KEY__CONSTRAINT_MODE, oldConstraintMode, constraintMode, !oldConstraintModeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetConstraintMode() {
		ConstraintMode oldConstraintMode = constraintMode;
		boolean oldConstraintModeESet = constraintModeESet;
		constraintMode = CONSTRAINT_MODE_EDEFAULT;
		constraintModeESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EORMPackage.FOREIGN_KEY__CONSTRAINT_MODE, oldConstraintMode, CONSTRAINT_MODE_EDEFAULT, oldConstraintModeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetConstraintMode() {
		return constraintModeESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getForeignKeyDefinition() {
		return foreignKeyDefinition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setForeignKeyDefinition(String newForeignKeyDefinition) {
		String oldForeignKeyDefinition = foreignKeyDefinition;
		foreignKeyDefinition = newForeignKeyDefinition;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.FOREIGN_KEY__FOREIGN_KEY_DEFINITION, oldForeignKeyDefinition, foreignKeyDefinition));
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
			eNotify(new ENotificationImpl(this, Notification.SET, EORMPackage.FOREIGN_KEY__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case EORMPackage.FOREIGN_KEY__DESCRIPTION:
				return getDescription();
			case EORMPackage.FOREIGN_KEY__CONSTRAINT_MODE:
				return getConstraintMode();
			case EORMPackage.FOREIGN_KEY__FOREIGN_KEY_DEFINITION:
				return getForeignKeyDefinition();
			case EORMPackage.FOREIGN_KEY__NAME:
				return getName();
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
			case EORMPackage.FOREIGN_KEY__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case EORMPackage.FOREIGN_KEY__CONSTRAINT_MODE:
				setConstraintMode((ConstraintMode)newValue);
				return;
			case EORMPackage.FOREIGN_KEY__FOREIGN_KEY_DEFINITION:
				setForeignKeyDefinition((String)newValue);
				return;
			case EORMPackage.FOREIGN_KEY__NAME:
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
			case EORMPackage.FOREIGN_KEY__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case EORMPackage.FOREIGN_KEY__CONSTRAINT_MODE:
				unsetConstraintMode();
				return;
			case EORMPackage.FOREIGN_KEY__FOREIGN_KEY_DEFINITION:
				setForeignKeyDefinition(FOREIGN_KEY_DEFINITION_EDEFAULT);
				return;
			case EORMPackage.FOREIGN_KEY__NAME:
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
			case EORMPackage.FOREIGN_KEY__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case EORMPackage.FOREIGN_KEY__CONSTRAINT_MODE:
				return isSetConstraintMode();
			case EORMPackage.FOREIGN_KEY__FOREIGN_KEY_DEFINITION:
				return FOREIGN_KEY_DEFINITION_EDEFAULT == null ? foreignKeyDefinition != null : !FOREIGN_KEY_DEFINITION_EDEFAULT.equals(foreignKeyDefinition);
			case EORMPackage.FOREIGN_KEY__NAME:
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
		result.append(" (description: ");
		result.append(description);
		result.append(", constraintMode: ");
		if (constraintModeESet) result.append(constraintMode); else result.append("<unset>");
		result.append(", foreignKeyDefinition: ");
		result.append(foreignKeyDefinition);
		result.append(", name: ");
		result.append(name);
		result.append(')');
		return result.toString();
	}

} //ForeignKeyImpl
