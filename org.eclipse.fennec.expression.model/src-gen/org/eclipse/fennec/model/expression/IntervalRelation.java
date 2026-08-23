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
 * A representation of the literals of the enumeration '<em><b>Interval Relation</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * The three relations every range implementation answers directly (issue #215) — Lucene's newIntersectsQuery/newWithinQuery/newContainsQuery, PostgreSQL's range operators. Allen's remaining ten interval relations are expressible from the bounds and have no consumer. In the definitions below each comparison is strict where the corresponding bound is excluded by the four inclusion flags.
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getIntervalRelation()
 * @model
 * @generated
 */
@ProviderType
public enum IntervalRelation implements Enumerator {
	/**
	 * The '<em><b>INTERSECTS</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The two intervals overlap at all: subject.lower <= query.upper AND subject.upper >= query.lower. The common case — "valid at some point in [a, b]".
	 * <!-- end-model-doc -->
	 * @see #INTERSECTS_VALUE
	 * @generated
	 * @ordered
	 */
	INTERSECTS(0, "INTERSECTS", "INTERSECTS"),

	/**
	 * The '<em><b>WITHIN</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The subject lies inside the query interval: subject.lower >= query.lower AND subject.upper <= query.upper.
	 * <!-- end-model-doc -->
	 * @see #WITHIN_VALUE
	 * @generated
	 * @ordered
	 */
	WITHIN(1, "WITHIN", "WITHIN"),

	/**
	 * The '<em><b>CONTAINS</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The subject covers the query interval: subject.lower <= query.lower AND subject.upper >= query.upper. With a degenerate query interval (lower == upper) this is the "valid at t" question.
	 * <!-- end-model-doc -->
	 * @see #CONTAINS_VALUE
	 * @generated
	 * @ordered
	 */
	CONTAINS(2, "CONTAINS", "CONTAINS");

	/**
	 * The '<em><b>INTERSECTS</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The two intervals overlap at all: subject.lower <= query.upper AND subject.upper >= query.lower. The common case — "valid at some point in [a, b]".
	 * <!-- end-model-doc -->
	 * @see #INTERSECTS
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int INTERSECTS_VALUE = 0;

	/**
	 * The '<em><b>WITHIN</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The subject lies inside the query interval: subject.lower >= query.lower AND subject.upper <= query.upper.
	 * <!-- end-model-doc -->
	 * @see #WITHIN
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int WITHIN_VALUE = 1;

	/**
	 * The '<em><b>CONTAINS</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The subject covers the query interval: subject.lower <= query.lower AND subject.upper >= query.upper. With a degenerate query interval (lower == upper) this is the "valid at t" question.
	 * <!-- end-model-doc -->
	 * @see #CONTAINS
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int CONTAINS_VALUE = 2;

	/**
	 * An array of all the '<em><b>Interval Relation</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final IntervalRelation[] VALUES_ARRAY =
		new IntervalRelation[] {
			INTERSECTS,
			WITHIN,
			CONTAINS,
		};

	/**
	 * A public read-only list of all the '<em><b>Interval Relation</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<IntervalRelation> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Interval Relation</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static IntervalRelation get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			IntervalRelation result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Interval Relation</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static IntervalRelation getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			IntervalRelation result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Interval Relation</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static IntervalRelation get(int value) {
		switch (value) {
			case INTERSECTS_VALUE: return INTERSECTS;
			case WITHIN_VALUE: return WITHIN;
			case CONTAINS_VALUE: return CONTAINS;
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
	private IntervalRelation(int value, String name, String literal) {
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
	
} //IntervalRelation
