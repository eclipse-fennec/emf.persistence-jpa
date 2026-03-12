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
package org.eclipse.fennec.persistence.eorm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Constraint Mode</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * 
 * 
 *        public enum ConstraintMode {CONSTRAINT, NO_CONSTRAINT, PROVIDER_DEFAULT};
 * 
 *       
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getConstraintMode()
 * @model extendedMetaData="name='constraint-mode'"
 * @generated
 */
@ProviderType
public enum ConstraintMode implements Enumerator {
	/**
	 * The '<em><b>CONSTRAINT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CONSTRAINT_VALUE
	 * @generated
	 * @ordered
	 */
	CONSTRAINT(0, "CONSTRAINT", "CONSTRAINT"),

	/**
	 * The '<em><b>NOCONSTRAINT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #NOCONSTRAINT_VALUE
	 * @generated
	 * @ordered
	 */
	NOCONSTRAINT(1, "NOCONSTRAINT", "NO_CONSTRAINT"),

	/**
	 * The '<em><b>PROVIDERDEFAULT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PROVIDERDEFAULT_VALUE
	 * @generated
	 * @ordered
	 */
	PROVIDERDEFAULT(2, "PROVIDERDEFAULT", "PROVIDER_DEFAULT");

	/**
	 * The '<em><b>CONSTRAINT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CONSTRAINT
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int CONSTRAINT_VALUE = 0;

	/**
	 * The '<em><b>NOCONSTRAINT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #NOCONSTRAINT
	 * @model literal="NO_CONSTRAINT"
	 * @generated
	 * @ordered
	 */
	public static final int NOCONSTRAINT_VALUE = 1;

	/**
	 * The '<em><b>PROVIDERDEFAULT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PROVIDERDEFAULT
	 * @model literal="PROVIDER_DEFAULT"
	 * @generated
	 * @ordered
	 */
	public static final int PROVIDERDEFAULT_VALUE = 2;

	/**
	 * An array of all the '<em><b>Constraint Mode</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final ConstraintMode[] VALUES_ARRAY =
		new ConstraintMode[] {
			CONSTRAINT,
			NOCONSTRAINT,
			PROVIDERDEFAULT,
		};

	/**
	 * A public read-only list of all the '<em><b>Constraint Mode</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<ConstraintMode> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Constraint Mode</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static ConstraintMode get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			ConstraintMode result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Constraint Mode</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static ConstraintMode getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			ConstraintMode result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Constraint Mode</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static ConstraintMode get(int value) {
		switch (value) {
			case CONSTRAINT_VALUE: return CONSTRAINT;
			case NOCONSTRAINT_VALUE: return NOCONSTRAINT;
			case PROVIDERDEFAULT_VALUE: return PROVIDERDEFAULT;
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final int value;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String name;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String literal;

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private ConstraintMode(int value, String name, String literal) {
		this.value = value;
		this.name = name;
		this.literal = literal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getValue() {
	  return value;
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
	public String getLiteral() {
	  return literal;
	}

	/**
	 * Returns the literal value of the enumerator, which is its string representation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		return literal;
	}
	
} //ConstraintMode
