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
package org.eclipse.fennec.model.expression;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Numeric Function Kind</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * ROUND is half away from zero.
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getNumericFunctionKind()
 * @model
 * @generated
 */
@ProviderType
public enum NumericFunctionKind implements Enumerator {
	/**
	 * The '<em><b>ROUND</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ROUND_VALUE
	 * @generated
	 * @ordered
	 */
	ROUND(0, "ROUND", "ROUND"),

	/**
	 * The '<em><b>FLOOR</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #FLOOR_VALUE
	 * @generated
	 * @ordered
	 */
	FLOOR(1, "FLOOR", "FLOOR"),

	/**
	 * The '<em><b>CEILING</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CEILING_VALUE
	 * @generated
	 * @ordered
	 */
	CEILING(2, "CEILING", "CEILING");

	/**
	 * The '<em><b>ROUND</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ROUND
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int ROUND_VALUE = 0;

	/**
	 * The '<em><b>FLOOR</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #FLOOR
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int FLOOR_VALUE = 1;

	/**
	 * The '<em><b>CEILING</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CEILING
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int CEILING_VALUE = 2;

	/**
	 * An array of all the '<em><b>Numeric Function Kind</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final NumericFunctionKind[] VALUES_ARRAY =
		new NumericFunctionKind[] {
			ROUND,
			FLOOR,
			CEILING,
		};

	/**
	 * A public read-only list of all the '<em><b>Numeric Function Kind</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<NumericFunctionKind> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Numeric Function Kind</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static NumericFunctionKind get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			NumericFunctionKind result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Numeric Function Kind</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static NumericFunctionKind getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			NumericFunctionKind result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Numeric Function Kind</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static NumericFunctionKind get(int value) {
		switch (value) {
			case ROUND_VALUE: return ROUND;
			case FLOOR_VALUE: return FLOOR;
			case CEILING_VALUE: return CEILING;
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
	private NumericFunctionKind(int value, String name, String literal) {
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
	
} //NumericFunctionKind
