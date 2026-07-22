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
package org.eclipse.fennec.persistence.query.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Query Shape</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * The shape of a QueryPlan and, correspondingly, of the QueryResult a backend produces when executing it.
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.persistence.query.api.QueryApiPackage#getQueryShape()
 * @model
 * @generated
 */
@ProviderType
public enum QueryShape implements Enumerator {
	/**
	 * The '<em><b>OBJECTS</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Whole entities: the result exposes a Stream of EObjects.
	 * <!-- end-model-doc -->
	 * @see #OBJECTS_VALUE
	 * @generated
	 * @ordered
	 */
	OBJECTS(0, "OBJECTS", "OBJECTS"),

	/**
	 * The '<em><b>PROJECTION</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Selected scalar fields (subject projection): the result exposes rows.
	 * <!-- end-model-doc -->
	 * @see #PROJECTION_VALUE
	 * @generated
	 * @ordered
	 */
	PROJECTION(1, "PROJECTION", "PROJECTION"),

	/**
	 * The '<em><b>AGGREGATION</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Grouped/aggregated values (groupBy + aggregate functions): the result exposes rows.
	 * <!-- end-model-doc -->
	 * @see #AGGREGATION_VALUE
	 * @generated
	 * @ordered
	 */
	AGGREGATION(2, "AGGREGATION", "AGGREGATION"),

	/**
	 * The '<em><b>COUNT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * A single cardinality: the result exposes a long count.
	 * <!-- end-model-doc -->
	 * @see #COUNT_VALUE
	 * @generated
	 * @ordered
	 */
	COUNT(3, "COUNT", "COUNT");

	/**
	 * The '<em><b>OBJECTS</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Whole entities: the result exposes a Stream of EObjects.
	 * <!-- end-model-doc -->
	 * @see #OBJECTS
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int OBJECTS_VALUE = 0;

	/**
	 * The '<em><b>PROJECTION</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Selected scalar fields (subject projection): the result exposes rows.
	 * <!-- end-model-doc -->
	 * @see #PROJECTION
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int PROJECTION_VALUE = 1;

	/**
	 * The '<em><b>AGGREGATION</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Grouped/aggregated values (groupBy + aggregate functions): the result exposes rows.
	 * <!-- end-model-doc -->
	 * @see #AGGREGATION
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int AGGREGATION_VALUE = 2;

	/**
	 * The '<em><b>COUNT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * A single cardinality: the result exposes a long count.
	 * <!-- end-model-doc -->
	 * @see #COUNT
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int COUNT_VALUE = 3;

	/**
	 * An array of all the '<em><b>Query Shape</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final QueryShape[] VALUES_ARRAY =
		new QueryShape[] {
			OBJECTS,
			PROJECTION,
			AGGREGATION,
			COUNT,
		};

	/**
	 * A public read-only list of all the '<em><b>Query Shape</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<QueryShape> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Query Shape</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static QueryShape get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			QueryShape result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Query Shape</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static QueryShape getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			QueryShape result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Query Shape</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static QueryShape get(int value) {
		switch (value) {
			case OBJECTS_VALUE: return OBJECTS;
			case PROJECTION_VALUE: return PROJECTION;
			case AGGREGATION_VALUE: return AGGREGATION;
			case COUNT_VALUE: return COUNT;
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
	private QueryShape(int value, String name, String literal) {
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
	
} //QueryShape
