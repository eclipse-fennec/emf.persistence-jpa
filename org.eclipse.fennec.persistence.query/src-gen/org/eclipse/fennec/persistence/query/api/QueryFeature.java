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
 * A representation of the literals of the enumeration '<em><b>Query Feature</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * A single, backend-neutral query capability. Each QueryProcessor declares the set of features it serves natively (QueryCapabilities); a query using an unsupported feature is rejected with a Diagnostic during validate — never silently post-filtered in memory. Literal values are grouped with gaps: predicates 0+, shaping 20+, aggregation 40+, value/path operations 60+, structural 80+, future 100+.
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.persistence.query.api.QueryApiPackage#getQueryFeature()
 * @model
 * @generated
 */
@ProviderType
public enum QueryFeature implements Enumerator {
	/**
	 * The '<em><b>WHERE EQ</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Equality comparator (Eq).
	 * <!-- end-model-doc -->
	 * @see #WHERE_EQ_VALUE
	 * @generated
	 * @ordered
	 */
	WHERE_EQ(0, "WHERE_EQ", "WHERE_EQ"),

	/**
	 * The '<em><b>WHERE COMPARISON</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Ordering comparators (Lt, Lte, Gt, Gte).
	 * <!-- end-model-doc -->
	 * @see #WHERE_COMPARISON_VALUE
	 * @generated
	 * @ordered
	 */
	WHERE_COMPARISON(1, "WHERE_COMPARISON", "WHERE_COMPARISON"),

	/**
	 * The '<em><b>WHERE STRING MATCH</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * String matching (Contains, StartWith, EndsWith, Like).
	 * <!-- end-model-doc -->
	 * @see #WHERE_STRING_MATCH_VALUE
	 * @generated
	 * @ordered
	 */
	WHERE_STRING_MATCH(2, "WHERE_STRING_MATCH", "WHERE_STRING_MATCH"),

	/**
	 * The '<em><b>WHERE RANGE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Range comparator (IsInRange).
	 * <!-- end-model-doc -->
	 * @see #WHERE_RANGE_VALUE
	 * @generated
	 * @ordered
	 */
	WHERE_RANGE(3, "WHERE_RANGE", "WHERE_RANGE"),

	/**
	 * The '<em><b>WHERE DATE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Date comparators (IsBefore/IsAfter and the OrEqual variants).
	 * <!-- end-model-doc -->
	 * @see #WHERE_DATE_VALUE
	 * @generated
	 * @ordered
	 */
	WHERE_DATE(4, "WHERE_DATE", "WHERE_DATE"),

	/**
	 * The '<em><b>WHERE ENUM</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Enum literal comparator (IsLiteral).
	 * <!-- end-model-doc -->
	 * @see #WHERE_ENUM_VALUE
	 * @generated
	 * @ordered
	 */
	WHERE_ENUM(5, "WHERE_ENUM", "WHERE_ENUM"),

	/**
	 * The '<em><b>WHERE BOOL</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Boolean comparator (IsBool).
	 * <!-- end-model-doc -->
	 * @see #WHERE_BOOL_VALUE
	 * @generated
	 * @ordered
	 */
	WHERE_BOOL(6, "WHERE_BOOL", "WHERE_BOOL"),

	/**
	 * The '<em><b>LOGICAL AND</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Logical conjunction (And).
	 * <!-- end-model-doc -->
	 * @see #LOGICAL_AND_VALUE
	 * @generated
	 * @ordered
	 */
	LOGICAL_AND(7, "LOGICAL_AND", "LOGICAL_AND"),

	/**
	 * The '<em><b>LOGICAL OR</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Logical disjunction (Or).
	 * <!-- end-model-doc -->
	 * @see #LOGICAL_OR_VALUE
	 * @generated
	 * @ordered
	 */
	LOGICAL_OR(8, "LOGICAL_OR", "LOGICAL_OR"),

	/**
	 * The '<em><b>LOGICAL NOT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Logical negation (Not).
	 * <!-- end-model-doc -->
	 * @see #LOGICAL_NOT_VALUE
	 * @generated
	 * @ordered
	 */
	LOGICAL_NOT(9, "LOGICAL_NOT", "LOGICAL_NOT"),

	/**
	 * The '<em><b>SORT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Ordering of results (sortBy).
	 * <!-- end-model-doc -->
	 * @see #SORT_VALUE
	 * @generated
	 * @ordered
	 */
	SORT(20, "SORT", "SORT"),

	/**
	 * The '<em><b>LIMIT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Result cardinality cap (limit).
	 * <!-- end-model-doc -->
	 * @see #LIMIT_VALUE
	 * @generated
	 * @ordered
	 */
	LIMIT(21, "LIMIT", "LIMIT"),

	/**
	 * The '<em><b>SKIP</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Result offset (skip).
	 * <!-- end-model-doc -->
	 * @see #SKIP_VALUE
	 * @generated
	 * @ordered
	 */
	SKIP(22, "SKIP", "SKIP"),

	/**
	 * The '<em><b>DISTINCT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Duplicate elimination (distinct).
	 * <!-- end-model-doc -->
	 * @see #DISTINCT_VALUE
	 * @generated
	 * @ordered
	 */
	DISTINCT(23, "DISTINCT", "DISTINCT"),

	/**
	 * The '<em><b>COUNT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Count-only result (count).
	 * <!-- end-model-doc -->
	 * @see #COUNT_VALUE
	 * @generated
	 * @ordered
	 */
	COUNT(24, "COUNT", "COUNT"),

	/**
	 * The '<em><b>PROJECTION</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Projection of scalar fields via subject/FeaturePath.
	 * <!-- end-model-doc -->
	 * @see #PROJECTION_VALUE
	 * @generated
	 * @ordered
	 */
	PROJECTION(25, "PROJECTION", "PROJECTION"),

	/**
	 * The '<em><b>PROJECTION NESTED</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Projection through references (requires a join).
	 * <!-- end-model-doc -->
	 * @see #PROJECTION_NESTED_VALUE
	 * @generated
	 * @ordered
	 */
	PROJECTION_NESTED(26, "PROJECTION_NESTED", "PROJECTION_NESTED"),

	/**
	 * The '<em><b>GROUP BY</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Grouping (groupBy).
	 * <!-- end-model-doc -->
	 * @see #GROUP_BY_VALUE
	 * @generated
	 * @ordered
	 */
	GROUP_BY(40, "GROUP_BY", "GROUP_BY"),

	/**
	 * The '<em><b>AGG AVG</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Average aggregate.
	 * <!-- end-model-doc -->
	 * @see #AGG_AVG_VALUE
	 * @generated
	 * @ordered
	 */
	AGG_AVG(41, "AGG_AVG", "AGG_AVG"),

	/**
	 * The '<em><b>AGG MIN</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Minimum aggregate.
	 * <!-- end-model-doc -->
	 * @see #AGG_MIN_VALUE
	 * @generated
	 * @ordered
	 */
	AGG_MIN(42, "AGG_MIN", "AGG_MIN"),

	/**
	 * The '<em><b>AGG MAX</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Maximum aggregate.
	 * <!-- end-model-doc -->
	 * @see #AGG_MAX_VALUE
	 * @generated
	 * @ordered
	 */
	AGG_MAX(43, "AGG_MAX", "AGG_MAX"),

	/**
	 * The '<em><b>AGG SUM</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Sum aggregate.
	 * <!-- end-model-doc -->
	 * @see #AGG_SUM_VALUE
	 * @generated
	 * @ordered
	 */
	AGG_SUM(44, "AGG_SUM", "AGG_SUM"),

	/**
	 * The '<em><b>AGG COUNT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Count aggregate (per group).
	 * <!-- end-model-doc -->
	 * @see #AGG_COUNT_VALUE
	 * @generated
	 * @ordered
	 */
	AGG_COUNT(45, "AGG_COUNT", "AGG_COUNT"),

	/**
	 * The '<em><b>OP TO LOWER</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * ToLowerCase string operation.
	 * <!-- end-model-doc -->
	 * @see #OP_TO_LOWER_VALUE
	 * @generated
	 * @ordered
	 */
	OP_TO_LOWER(60, "OP_TO_LOWER", "OP_TO_LOWER"),

	/**
	 * The '<em><b>OP TO UPPER</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * ToUpperCase string operation.
	 * <!-- end-model-doc -->
	 * @see #OP_TO_UPPER_VALUE
	 * @generated
	 * @ordered
	 */
	OP_TO_UPPER(61, "OP_TO_UPPER", "OP_TO_UPPER"),

	/**
	 * The '<em><b>OP AVERAGE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Reserved. Aggregate functions (Average, Min, Max, Sum, CountOperation) always map to their AGG_* feature — without groupBy they aggregate the whole result set (single row, SQL semantics).
	 * <!-- end-model-doc -->
	 * @see #OP_AVERAGE_VALUE
	 * @generated
	 * @ordered
	 */
	OP_AVERAGE(62, "OP_AVERAGE", "OP_AVERAGE"),

	/**
	 * The '<em><b>FEATUREPATH NESTED</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * where/sort/projection over a multi-segment FeaturePath (join).
	 * <!-- end-model-doc -->
	 * @see #FEATUREPATH_NESTED_VALUE
	 * @generated
	 * @ordered
	 */
	FEATUREPATH_NESTED(80, "FEATUREPATH_NESTED", "FEATUREPATH_NESTED"),

	/**
	 * The '<em><b>TYPE FILTER</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Polymorphic type filter via QObject.rootEClass (type plus subtypes).
	 * <!-- end-model-doc -->
	 * @see #TYPE_FILTER_VALUE
	 * @generated
	 * @ordered
	 */
	TYPE_FILTER(81, "TYPE_FILTER", "TYPE_FILTER"),

	/**
	 * The '<em><b>TYPE FILTER STRICT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Exact type filter (no subtypes).
	 * <!-- end-model-doc -->
	 * @see #TYPE_FILTER_STRICT_VALUE
	 * @generated
	 * @ordered
	 */
	TYPE_FILTER_STRICT(82, "TYPE_FILTER_STRICT", "TYPE_FILTER_STRICT"),

	/**
	 * The '<em><b>PARAMETERS</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Bound placeholders for prepared queries.
	 * <!-- end-model-doc -->
	 * @see #PARAMETERS_VALUE
	 * @generated
	 * @ordered
	 */
	PARAMETERS(83, "PARAMETERS", "PARAMETERS"),

	/**
	 * The '<em><b>AS OF</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Point-in-time reads (asOf) — future, see unified-persistence concept §14.
	 * <!-- end-model-doc -->
	 * @see #AS_OF_VALUE
	 * @generated
	 * @ordered
	 */
	AS_OF(100, "AS_OF", "AS_OF"),

	/**
	 * The '<em><b>SERIES RANGE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Range/aggregation over time-series streams — future, see unified-persistence concept §14.
	 * <!-- end-model-doc -->
	 * @see #SERIES_RANGE_VALUE
	 * @generated
	 * @ordered
	 */
	SERIES_RANGE(101, "SERIES_RANGE", "SERIES_RANGE");

	/**
	 * The '<em><b>WHERE EQ</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Equality comparator (Eq).
	 * <!-- end-model-doc -->
	 * @see #WHERE_EQ
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int WHERE_EQ_VALUE = 0;

	/**
	 * The '<em><b>WHERE COMPARISON</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Ordering comparators (Lt, Lte, Gt, Gte).
	 * <!-- end-model-doc -->
	 * @see #WHERE_COMPARISON
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int WHERE_COMPARISON_VALUE = 1;

	/**
	 * The '<em><b>WHERE STRING MATCH</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * String matching (Contains, StartWith, EndsWith, Like).
	 * <!-- end-model-doc -->
	 * @see #WHERE_STRING_MATCH
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int WHERE_STRING_MATCH_VALUE = 2;

	/**
	 * The '<em><b>WHERE RANGE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Range comparator (IsInRange).
	 * <!-- end-model-doc -->
	 * @see #WHERE_RANGE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int WHERE_RANGE_VALUE = 3;

	/**
	 * The '<em><b>WHERE DATE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Date comparators (IsBefore/IsAfter and the OrEqual variants).
	 * <!-- end-model-doc -->
	 * @see #WHERE_DATE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int WHERE_DATE_VALUE = 4;

	/**
	 * The '<em><b>WHERE ENUM</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Enum literal comparator (IsLiteral).
	 * <!-- end-model-doc -->
	 * @see #WHERE_ENUM
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int WHERE_ENUM_VALUE = 5;

	/**
	 * The '<em><b>WHERE BOOL</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Boolean comparator (IsBool).
	 * <!-- end-model-doc -->
	 * @see #WHERE_BOOL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int WHERE_BOOL_VALUE = 6;

	/**
	 * The '<em><b>LOGICAL AND</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Logical conjunction (And).
	 * <!-- end-model-doc -->
	 * @see #LOGICAL_AND
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int LOGICAL_AND_VALUE = 7;

	/**
	 * The '<em><b>LOGICAL OR</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Logical disjunction (Or).
	 * <!-- end-model-doc -->
	 * @see #LOGICAL_OR
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int LOGICAL_OR_VALUE = 8;

	/**
	 * The '<em><b>LOGICAL NOT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Logical negation (Not).
	 * <!-- end-model-doc -->
	 * @see #LOGICAL_NOT
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int LOGICAL_NOT_VALUE = 9;

	/**
	 * The '<em><b>SORT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Ordering of results (sortBy).
	 * <!-- end-model-doc -->
	 * @see #SORT
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int SORT_VALUE = 20;

	/**
	 * The '<em><b>LIMIT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Result cardinality cap (limit).
	 * <!-- end-model-doc -->
	 * @see #LIMIT
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int LIMIT_VALUE = 21;

	/**
	 * The '<em><b>SKIP</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Result offset (skip).
	 * <!-- end-model-doc -->
	 * @see #SKIP
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int SKIP_VALUE = 22;

	/**
	 * The '<em><b>DISTINCT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Duplicate elimination (distinct).
	 * <!-- end-model-doc -->
	 * @see #DISTINCT
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int DISTINCT_VALUE = 23;

	/**
	 * The '<em><b>COUNT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Count-only result (count).
	 * <!-- end-model-doc -->
	 * @see #COUNT
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int COUNT_VALUE = 24;

	/**
	 * The '<em><b>PROJECTION</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Projection of scalar fields via subject/FeaturePath.
	 * <!-- end-model-doc -->
	 * @see #PROJECTION
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int PROJECTION_VALUE = 25;

	/**
	 * The '<em><b>PROJECTION NESTED</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Projection through references (requires a join).
	 * <!-- end-model-doc -->
	 * @see #PROJECTION_NESTED
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int PROJECTION_NESTED_VALUE = 26;

	/**
	 * The '<em><b>GROUP BY</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Grouping (groupBy).
	 * <!-- end-model-doc -->
	 * @see #GROUP_BY
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int GROUP_BY_VALUE = 40;

	/**
	 * The '<em><b>AGG AVG</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Average aggregate.
	 * <!-- end-model-doc -->
	 * @see #AGG_AVG
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int AGG_AVG_VALUE = 41;

	/**
	 * The '<em><b>AGG MIN</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Minimum aggregate.
	 * <!-- end-model-doc -->
	 * @see #AGG_MIN
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int AGG_MIN_VALUE = 42;

	/**
	 * The '<em><b>AGG MAX</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Maximum aggregate.
	 * <!-- end-model-doc -->
	 * @see #AGG_MAX
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int AGG_MAX_VALUE = 43;

	/**
	 * The '<em><b>AGG SUM</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Sum aggregate.
	 * <!-- end-model-doc -->
	 * @see #AGG_SUM
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int AGG_SUM_VALUE = 44;

	/**
	 * The '<em><b>AGG COUNT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Count aggregate (per group).
	 * <!-- end-model-doc -->
	 * @see #AGG_COUNT
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int AGG_COUNT_VALUE = 45;

	/**
	 * The '<em><b>OP TO LOWER</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * ToLowerCase string operation.
	 * <!-- end-model-doc -->
	 * @see #OP_TO_LOWER
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int OP_TO_LOWER_VALUE = 60;

	/**
	 * The '<em><b>OP TO UPPER</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * ToUpperCase string operation.
	 * <!-- end-model-doc -->
	 * @see #OP_TO_UPPER
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int OP_TO_UPPER_VALUE = 61;

	/**
	 * The '<em><b>OP AVERAGE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Reserved. Aggregate functions (Average, Min, Max, Sum, CountOperation) always map to their AGG_* feature — without groupBy they aggregate the whole result set (single row, SQL semantics).
	 * <!-- end-model-doc -->
	 * @see #OP_AVERAGE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int OP_AVERAGE_VALUE = 62;

	/**
	 * The '<em><b>FEATUREPATH NESTED</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * where/sort/projection over a multi-segment FeaturePath (join).
	 * <!-- end-model-doc -->
	 * @see #FEATUREPATH_NESTED
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int FEATUREPATH_NESTED_VALUE = 80;

	/**
	 * The '<em><b>TYPE FILTER</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Polymorphic type filter via QObject.rootEClass (type plus subtypes).
	 * <!-- end-model-doc -->
	 * @see #TYPE_FILTER
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int TYPE_FILTER_VALUE = 81;

	/**
	 * The '<em><b>TYPE FILTER STRICT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Exact type filter (no subtypes).
	 * <!-- end-model-doc -->
	 * @see #TYPE_FILTER_STRICT
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int TYPE_FILTER_STRICT_VALUE = 82;

	/**
	 * The '<em><b>PARAMETERS</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Bound placeholders for prepared queries.
	 * <!-- end-model-doc -->
	 * @see #PARAMETERS
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int PARAMETERS_VALUE = 83;

	/**
	 * The '<em><b>AS OF</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Point-in-time reads (asOf) — future, see unified-persistence concept §14.
	 * <!-- end-model-doc -->
	 * @see #AS_OF
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int AS_OF_VALUE = 100;

	/**
	 * The '<em><b>SERIES RANGE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Range/aggregation over time-series streams — future, see unified-persistence concept §14.
	 * <!-- end-model-doc -->
	 * @see #SERIES_RANGE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int SERIES_RANGE_VALUE = 101;

	/**
	 * An array of all the '<em><b>Query Feature</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final QueryFeature[] VALUES_ARRAY =
		new QueryFeature[] {
			WHERE_EQ,
			WHERE_COMPARISON,
			WHERE_STRING_MATCH,
			WHERE_RANGE,
			WHERE_DATE,
			WHERE_ENUM,
			WHERE_BOOL,
			LOGICAL_AND,
			LOGICAL_OR,
			LOGICAL_NOT,
			SORT,
			LIMIT,
			SKIP,
			DISTINCT,
			COUNT,
			PROJECTION,
			PROJECTION_NESTED,
			GROUP_BY,
			AGG_AVG,
			AGG_MIN,
			AGG_MAX,
			AGG_SUM,
			AGG_COUNT,
			OP_TO_LOWER,
			OP_TO_UPPER,
			OP_AVERAGE,
			FEATUREPATH_NESTED,
			TYPE_FILTER,
			TYPE_FILTER_STRICT,
			PARAMETERS,
			AS_OF,
			SERIES_RANGE,
		};

	/**
	 * A public read-only list of all the '<em><b>Query Feature</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<QueryFeature> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Query Feature</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static QueryFeature get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			QueryFeature result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Query Feature</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static QueryFeature getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			QueryFeature result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Query Feature</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static QueryFeature get(int value) {
		switch (value) {
			case WHERE_EQ_VALUE: return WHERE_EQ;
			case WHERE_COMPARISON_VALUE: return WHERE_COMPARISON;
			case WHERE_STRING_MATCH_VALUE: return WHERE_STRING_MATCH;
			case WHERE_RANGE_VALUE: return WHERE_RANGE;
			case WHERE_DATE_VALUE: return WHERE_DATE;
			case WHERE_ENUM_VALUE: return WHERE_ENUM;
			case WHERE_BOOL_VALUE: return WHERE_BOOL;
			case LOGICAL_AND_VALUE: return LOGICAL_AND;
			case LOGICAL_OR_VALUE: return LOGICAL_OR;
			case LOGICAL_NOT_VALUE: return LOGICAL_NOT;
			case SORT_VALUE: return SORT;
			case LIMIT_VALUE: return LIMIT;
			case SKIP_VALUE: return SKIP;
			case DISTINCT_VALUE: return DISTINCT;
			case COUNT_VALUE: return COUNT;
			case PROJECTION_VALUE: return PROJECTION;
			case PROJECTION_NESTED_VALUE: return PROJECTION_NESTED;
			case GROUP_BY_VALUE: return GROUP_BY;
			case AGG_AVG_VALUE: return AGG_AVG;
			case AGG_MIN_VALUE: return AGG_MIN;
			case AGG_MAX_VALUE: return AGG_MAX;
			case AGG_SUM_VALUE: return AGG_SUM;
			case AGG_COUNT_VALUE: return AGG_COUNT;
			case OP_TO_LOWER_VALUE: return OP_TO_LOWER;
			case OP_TO_UPPER_VALUE: return OP_TO_UPPER;
			case OP_AVERAGE_VALUE: return OP_AVERAGE;
			case FEATUREPATH_NESTED_VALUE: return FEATUREPATH_NESTED;
			case TYPE_FILTER_VALUE: return TYPE_FILTER;
			case TYPE_FILTER_STRICT_VALUE: return TYPE_FILTER_STRICT;
			case PARAMETERS_VALUE: return PARAMETERS;
			case AS_OF_VALUE: return AS_OF;
			case SERIES_RANGE_VALUE: return SERIES_RANGE;
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
	private QueryFeature(int value, String name, String literal) {
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
	
} //QueryFeature
