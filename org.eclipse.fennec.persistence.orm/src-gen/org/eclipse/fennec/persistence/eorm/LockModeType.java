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
 * A representation of the literals of the enumeration '<em><b>Lock Mode Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * 
 * 
 *         public enum LockModeType { READ, WRITE, OPTIMISTIC, OPTIMISTIC_FORCE_INCREMENT, PESSIMISTIC_READ, PESSIMISTIC_WRITE, PESSIMISTIC_FORCE_INCREMENT, NONE};
 * 
 *       
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getLockModeType()
 * @model extendedMetaData="name='lock-mode-type'"
 * @generated
 */
@ProviderType
public enum LockModeType implements Enumerator {
	/**
	 * The '<em><b>READ</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #READ_VALUE
	 * @generated
	 * @ordered
	 */
	READ(0, "READ", "READ"),

	/**
	 * The '<em><b>WRITE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #WRITE_VALUE
	 * @generated
	 * @ordered
	 */
	WRITE(1, "WRITE", "WRITE"),

	/**
	 * The '<em><b>OPTIMISTIC</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #OPTIMISTIC_VALUE
	 * @generated
	 * @ordered
	 */
	OPTIMISTIC(2, "OPTIMISTIC", "OPTIMISTIC"),

	/**
	 * The '<em><b>OPTIMISTICFORCEINCREMENT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #OPTIMISTICFORCEINCREMENT_VALUE
	 * @generated
	 * @ordered
	 */
	OPTIMISTICFORCEINCREMENT(3, "OPTIMISTICFORCEINCREMENT", "OPTIMISTIC_FORCE_INCREMENT"),

	/**
	 * The '<em><b>PESSIMISTICREAD</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PESSIMISTICREAD_VALUE
	 * @generated
	 * @ordered
	 */
	PESSIMISTICREAD(4, "PESSIMISTICREAD", "PESSIMISTIC_READ"),

	/**
	 * The '<em><b>PESSIMISTICWRITE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PESSIMISTICWRITE_VALUE
	 * @generated
	 * @ordered
	 */
	PESSIMISTICWRITE(5, "PESSIMISTICWRITE", "PESSIMISTIC_WRITE"),

	/**
	 * The '<em><b>PESSIMISTICFORCEINCREMENT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PESSIMISTICFORCEINCREMENT_VALUE
	 * @generated
	 * @ordered
	 */
	PESSIMISTICFORCEINCREMENT(6, "PESSIMISTICFORCEINCREMENT", "PESSIMISTIC_FORCE_INCREMENT"),

	/**
	 * The '<em><b>NONE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #NONE_VALUE
	 * @generated
	 * @ordered
	 */
	NONE(7, "NONE", "NONE");

	/**
	 * The '<em><b>READ</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #READ
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int READ_VALUE = 0;

	/**
	 * The '<em><b>WRITE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #WRITE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int WRITE_VALUE = 1;

	/**
	 * The '<em><b>OPTIMISTIC</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #OPTIMISTIC
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int OPTIMISTIC_VALUE = 2;

	/**
	 * The '<em><b>OPTIMISTICFORCEINCREMENT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #OPTIMISTICFORCEINCREMENT
	 * @model literal="OPTIMISTIC_FORCE_INCREMENT"
	 * @generated
	 * @ordered
	 */
	public static final int OPTIMISTICFORCEINCREMENT_VALUE = 3;

	/**
	 * The '<em><b>PESSIMISTICREAD</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PESSIMISTICREAD
	 * @model literal="PESSIMISTIC_READ"
	 * @generated
	 * @ordered
	 */
	public static final int PESSIMISTICREAD_VALUE = 4;

	/**
	 * The '<em><b>PESSIMISTICWRITE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PESSIMISTICWRITE
	 * @model literal="PESSIMISTIC_WRITE"
	 * @generated
	 * @ordered
	 */
	public static final int PESSIMISTICWRITE_VALUE = 5;

	/**
	 * The '<em><b>PESSIMISTICFORCEINCREMENT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PESSIMISTICFORCEINCREMENT
	 * @model literal="PESSIMISTIC_FORCE_INCREMENT"
	 * @generated
	 * @ordered
	 */
	public static final int PESSIMISTICFORCEINCREMENT_VALUE = 6;

	/**
	 * The '<em><b>NONE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #NONE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int NONE_VALUE = 7;

	/**
	 * An array of all the '<em><b>Lock Mode Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final LockModeType[] VALUES_ARRAY =
		new LockModeType[] {
			READ,
			WRITE,
			OPTIMISTIC,
			OPTIMISTICFORCEINCREMENT,
			PESSIMISTICREAD,
			PESSIMISTICWRITE,
			PESSIMISTICFORCEINCREMENT,
			NONE,
		};

	/**
	 * A public read-only list of all the '<em><b>Lock Mode Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<LockModeType> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Lock Mode Type</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static LockModeType get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			LockModeType result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Lock Mode Type</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static LockModeType getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			LockModeType result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Lock Mode Type</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static LockModeType get(int value) {
		switch (value) {
			case READ_VALUE: return READ;
			case WRITE_VALUE: return WRITE;
			case OPTIMISTIC_VALUE: return OPTIMISTIC;
			case OPTIMISTICFORCEINCREMENT_VALUE: return OPTIMISTICFORCEINCREMENT;
			case PESSIMISTICREAD_VALUE: return PESSIMISTICREAD;
			case PESSIMISTICWRITE_VALUE: return PESSIMISTICWRITE;
			case PESSIMISTICFORCEINCREMENT_VALUE: return PESSIMISTICFORCEINCREMENT;
			case NONE_VALUE: return NONE;
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
	private LockModeType(int value, String name, String literal) {
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
	
} //LockModeType
