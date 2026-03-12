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
 * A representation of the literals of the enumeration '<em><b>Inheritance Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * 
 * 
 *         public enum InheritanceType
 *           { SINGLE_TABLE, JOINED, TABLE_PER_CLASS};
 * 
 *       
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.persistence.eorm.EORMPackage#getInheritanceType()
 * @model extendedMetaData="name='inheritance-type'"
 * @generated
 */
@ProviderType
public enum InheritanceType implements Enumerator {
	/**
	 * The '<em><b>SINGLETABLE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SINGLETABLE_VALUE
	 * @generated
	 * @ordered
	 */
	SINGLETABLE(0, "SINGLETABLE", "SINGLE_TABLE"),

	/**
	 * The '<em><b>JOINED</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #JOINED_VALUE
	 * @generated
	 * @ordered
	 */
	JOINED(1, "JOINED", "JOINED"),

	/**
	 * The '<em><b>TABLEPERCLASS</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #TABLEPERCLASS_VALUE
	 * @generated
	 * @ordered
	 */
	TABLEPERCLASS(2, "TABLEPERCLASS", "TABLE_PER_CLASS");

	/**
	 * The '<em><b>SINGLETABLE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SINGLETABLE
	 * @model literal="SINGLE_TABLE"
	 * @generated
	 * @ordered
	 */
	public static final int SINGLETABLE_VALUE = 0;

	/**
	 * The '<em><b>JOINED</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #JOINED
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int JOINED_VALUE = 1;

	/**
	 * The '<em><b>TABLEPERCLASS</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #TABLEPERCLASS
	 * @model literal="TABLE_PER_CLASS"
	 * @generated
	 * @ordered
	 */
	public static final int TABLEPERCLASS_VALUE = 2;

	/**
	 * An array of all the '<em><b>Inheritance Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final InheritanceType[] VALUES_ARRAY =
		new InheritanceType[] {
			SINGLETABLE,
			JOINED,
			TABLEPERCLASS,
		};

	/**
	 * A public read-only list of all the '<em><b>Inheritance Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<InheritanceType> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Inheritance Type</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static InheritanceType get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			InheritanceType result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Inheritance Type</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static InheritanceType getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			InheritanceType result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Inheritance Type</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static InheritanceType get(int value) {
		switch (value) {
			case SINGLETABLE_VALUE: return SINGLETABLE;
			case JOINED_VALUE: return JOINED;
			case TABLEPERCLASS_VALUE: return TABLEPERCLASS;
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
	private InheritanceType(int value, String name, String literal) {
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
	
} //InheritanceType
