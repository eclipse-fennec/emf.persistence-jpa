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
package org.eclipse.fennec.persistence.capabilities;

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
 * @see org.eclipse.fennec.persistence.capabilities.CapabilitiesPackage#getQueryFeature()
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
	 * Equality comparison (Comparison EQ).
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
	 * Ordering comparisons (Comparison LT, LE, GT, GE).
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
	 * String matching (StringMatch: CONTAINS, STARTS_WITH, ENDS_WITH, LIKE).
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
	 * Range predicate (Between).
	 * <!-- end-model-doc -->
	 * @see #WHERE_RANGE_VALUE
	 * @generated
	 * @ordered
	 */
	WHERE_RANGE(3, "WHERE_RANGE", "WHERE_RANGE"),

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
	 * The '<em><b>WHERE NE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Not-equal comparison (v2 IR Comparison NE).
	 * <!-- end-model-doc -->
	 * @see #WHERE_NE_VALUE
	 * @generated
	 * @ordered
	 */
	WHERE_NE(10, "WHERE_NE", "WHERE_NE"),

	/**
	 * The '<em><b>IS NULL</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Explicit null check (v2 IR IsNull).
	 * <!-- end-model-doc -->
	 * @see #IS_NULL_VALUE
	 * @generated
	 * @ordered
	 */
	IS_NULL(11, "IS_NULL", "IS_NULL"),

	/**
	 * The '<em><b>IN</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Membership test (v2 IR In).
	 * <!-- end-model-doc -->
	 * @see #IN_VALUE
	 * @generated
	 * @ordered
	 */
	IN(12, "IN", "IN"),

	/**
	 * The '<em><b>EXISTS</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Existential quantifier over a multi-valued navigation (v2 IR Exists).
	 * <!-- end-model-doc -->
	 * @see #EXISTS_VALUE
	 * @generated
	 * @ordered
	 */
	EXISTS(13, "EXISTS", "EXISTS"),

	/**
	 * The '<em><b>FOR ALL</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Universal quantifier over a multi-valued navigation (v2 IR ForAll).
	 * <!-- end-model-doc -->
	 * @see #FOR_ALL_VALUE
	 * @generated
	 * @ordered
	 */
	FOR_ALL(14, "FOR_ALL", "FOR_ALL"),

	/**
	 * The '<em><b>STRING MATCH CASE INSENSITIVE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Case-insensitive string matching (v2 IR StringMatch.caseInsensitive).
	 * <!-- end-model-doc -->
	 * @see #STRING_MATCH_CASE_INSENSITIVE_VALUE
	 * @generated
	 * @ordered
	 */
	STRING_MATCH_CASE_INSENSITIVE(15, "STRING_MATCH_CASE_INSENSITIVE", "STRING_MATCH_CASE_INSENSITIVE"),

	/**
	 * The '<em><b>FIELD TO FIELD</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Comparison whose both sides navigate features (field-to-field), directly or through string functions.
	 * <!-- end-model-doc -->
	 * @see #FIELD_TO_FIELD_VALUE
	 * @generated
	 * @ordered
	 */
	FIELD_TO_FIELD(16, "FIELD_TO_FIELD", "FIELD_TO_FIELD"),

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
	 * The '<em><b>EXPAND</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Eager-fetch hints (v2 envelope expand).
	 * <!-- end-model-doc -->
	 * @see #EXPAND_VALUE
	 * @generated
	 * @ordered
	 */
	EXPAND(27, "EXPAND", "EXPAND"),

	/**
	 * The '<em><b>PIPELINE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Aggregation pipeline stages beyond a single groupBy (v2 envelope apply).
	 * <!-- end-model-doc -->
	 * @see #PIPELINE_VALUE
	 * @generated
	 * @ordered
	 */
	PIPELINE(28, "PIPELINE", "PIPELINE"),

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
	 * The '<em><b>AGG COUNT DISTINCT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Count-distinct aggregate (v2 pipeline).
	 * <!-- end-model-doc -->
	 * @see #AGG_COUNT_DISTINCT_VALUE
	 * @generated
	 * @ordered
	 */
	AGG_COUNT_DISTINCT(46, "AGG_COUNT_DISTINCT", "AGG_COUNT_DISTINCT"),

	/**
	 * The '<em><b>STRING FUNCTIONS</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * String functions applied to values (v2 IR StringFunction: toLower/toUpper/trim/length).
	 * <!-- end-model-doc -->
	 * @see #STRING_FUNCTIONS_VALUE
	 * @generated
	 * @ordered
	 */
	STRING_FUNCTIONS(63, "STRING_FUNCTIONS", "STRING_FUNCTIONS"),

	/**
	 * The '<em><b>ARITHMETIC</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Arithmetic value expressions (v2 IR Arithmetic ADD/SUB/MUL/DIV/MOD and Negate). DIV is floating-point division; division by a literal zero is refused by the validator.
	 * <!-- end-model-doc -->
	 * @see #ARITHMETIC_VALUE
	 * @generated
	 * @ordered
	 */
	ARITHMETIC(64, "ARITHMETIC", "ARITHMETIC"),

	/**
	 * The '<em><b>STRING FUNCTIONS EXTENDED</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Extended string vocabulary (v2 IR Concat, IndexOf, Substring — issue #77). Separate from STRING_FUNCTIONS so backends can opt in independently; IndexOf is 0-based/-1, Substring follows [OData-URL] 5.1.1.7.
	 * <!-- end-model-doc -->
	 * @see #STRING_FUNCTIONS_EXTENDED_VALUE
	 * @generated
	 * @ordered
	 */
	STRING_FUNCTIONS_EXTENDED(65, "STRING_FUNCTIONS_EXTENDED", "STRING_FUNCTIONS_EXTENDED"),

	/**
	 * The '<em><b>NUMERIC FUNCTIONS</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Numeric rounding functions (v2 IR NumericFunction ROUND/FLOOR/CEILING — issue #78). ROUND is half away from zero.
	 * <!-- end-model-doc -->
	 * @see #NUMERIC_FUNCTIONS_VALUE
	 * @generated
	 * @ordered
	 */
	NUMERIC_FUNCTIONS(66, "NUMERIC_FUNCTIONS", "NUMERIC_FUNCTIONS"),

	/**
	 * The '<em><b>TEMPORAL FUNCTIONS</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Temporal part extraction (v2 IR TemporalFunction YEAR/MONTH/DAY/HOUR/MINUTE/SECOND — issue #79). UTC-normative; SECOND is integral.
	 * <!-- end-model-doc -->
	 * @see #TEMPORAL_FUNCTIONS_VALUE
	 * @generated
	 * @ordered
	 */
	TEMPORAL_FUNCTIONS(67, "TEMPORAL_FUNCTIONS", "TEMPORAL_FUNCTIONS"),

	/**
	 * The '<em><b>TYPE CAST</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Downcast of a navigation origin (v2 IR PropertyPath.castBase — issue #80). JPA TREAT; non-instances yield null.
	 * <!-- end-model-doc -->
	 * @see #TYPE_CAST_VALUE
	 * @generated
	 * @ordered
	 */
	TYPE_CAST(68, "TYPE_CAST", "TYPE_CAST"),

	/**
	 * The '<em><b>TYPE CHECK</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Kind-of type test (v2 IR TypeCheck — issue #80). JPA TYPE(x) IN (concrete subtypes); Mongo refuses until a type discriminator is stored.
	 * <!-- end-model-doc -->
	 * @see #TYPE_CHECK_VALUE
	 * @generated
	 * @ordered
	 */
	TYPE_CHECK(69, "TYPE_CHECK", "TYPE_CHECK"),

	/**
	 * The '<em><b>COLLECTION COUNT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Plain element count of a multi-valued navigation (v2 IR CollectionCount — issue #81). JPA SIZE, Mongo $size over embedded collections.
	 * <!-- end-model-doc -->
	 * @see #COLLECTION_COUNT_VALUE
	 * @generated
	 * @ordered
	 */
	COLLECTION_COUNT(70, "COLLECTION_COUNT", "COLLECTION_COUNT"),

	/**
	 * The '<em><b>COLLECTION COUNT FILTERED</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Predicate-filtered element count (v2 IR CollectionCount with predicate — issue #81). JPA correlated SELECT COUNT, Mongo $size($filter) over embedded collections.
	 * <!-- end-model-doc -->
	 * @see #COLLECTION_COUNT_FILTERED_VALUE
	 * @generated
	 * @ordered
	 */
	COLLECTION_COUNT_FILTERED(71, "COLLECTION_COUNT_FILTERED", "COLLECTION_COUNT_FILTERED"),

	/**
	 * The '<em><b>PIPELINE COMPUTE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Alias-bound computed pipeline columns (v2 query ComputeStage — issue #82): terminal or after grouping, addressed via AliasRef; a trailing FilterStage after GroupBy is HAVING.
	 * <!-- end-model-doc -->
	 * @see #PIPELINE_COMPUTE_VALUE
	 * @generated
	 * @ordered
	 */
	PIPELINE_COMPUTE(72, "PIPELINE_COMPUTE", "PIPELINE_COMPUTE"),

	/**
	 * The '<em><b>SORT EXPRESSION</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Ordering by an arbitrary value expression (v2 query OrderBy.key — issue #84). JPQL renders the expression inline; Mongo find-sorts refuse it.
	 * <!-- end-model-doc -->
	 * @see #SORT_EXPRESSION_VALUE
	 * @generated
	 * @ordered
	 */
	SORT_EXPRESSION(73, "SORT_EXPRESSION", "SORT_EXPRESSION"),

	/**
	 * The '<em><b>GROUP EXPRESSION</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Expression-valued group keys (GroupKey) and aggregate sources (Aggregate.source), including AliasRef to a pre-group compute alias (issue #87). JPQL re-renders the expression inline in SELECT/GROUP BY/aggregate arguments; Mongo evaluates it in $group/_id and accumulator arguments over $set fields; memory evaluates it per object with the compute alias environment.
	 * <!-- end-model-doc -->
	 * @see #GROUP_EXPRESSION_VALUE
	 * @generated
	 * @ordered
	 */
	GROUP_EXPRESSION(74, "GROUP_EXPRESSION", "GROUP_EXPRESSION"),

	/**
	 * The '<em><b>SCORE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The Score expression — relevance of the row under the query's predicate (issue #100), usable as sort key and computation source. Only ranking backends declare it (first consumer: the Lucene backend); memory/JPA/Mongo refuse. Deliberately no reference semantics — conformance is ordinal at best and lives with the declaring backend.
	 * <!-- end-model-doc -->
	 * @see #SCORE_VALUE
	 * @generated
	 * @ordered
	 */
	SCORE(75, "SCORE", "SCORE"),

	/**
	 * The '<em><b>GEO WITHIN</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * GeoWithin — containment of a WGS84 position in a box or polygon (issue #101). Memory carries the reference semantics (split subject binding); Mongo 2dsphere is G-P2, Lucene follows in emf.search, JPA refuses until a PostGIS dialect story exists.
	 * <!-- end-model-doc -->
	 * @see #GEO_WITHIN_VALUE
	 * @generated
	 * @ordered
	 */
	GEO_WITHIN(76, "GEO_WITHIN", "GEO_WITHIN"),

	/**
	 * The '<em><b>GEO DISTANCE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * GeoDistance — the spherical WGS84 distance in meters as a value expression (issue #101, decision G3): composes with comparisons and the issue-#84 sort seam (nearest first). Reference: haversine over the mean earth radius; conformance banded per decision G5.
	 * <!-- end-model-doc -->
	 * @see #GEO_DISTANCE_VALUE
	 * @generated
	 * @ordered
	 */
	GEO_DISTANCE(77, "GEO_DISTANCE", "GEO_DISTANCE"),

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
	 * Polymorphic type filter via Query.from (root type plus subtypes).
	 * <!-- end-model-doc -->
	 * @see #TYPE_FILTER_VALUE
	 * @generated
	 * @ordered
	 */
	TYPE_FILTER(81, "TYPE_FILTER", "TYPE_FILTER"),

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
	 * Equality comparison (Comparison EQ).
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
	 * Ordering comparisons (Comparison LT, LE, GT, GE).
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
	 * String matching (StringMatch: CONTAINS, STARTS_WITH, ENDS_WITH, LIKE).
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
	 * Range predicate (Between).
	 * <!-- end-model-doc -->
	 * @see #WHERE_RANGE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int WHERE_RANGE_VALUE = 3;

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
	 * The '<em><b>WHERE NE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Not-equal comparison (v2 IR Comparison NE).
	 * <!-- end-model-doc -->
	 * @see #WHERE_NE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int WHERE_NE_VALUE = 10;

	/**
	 * The '<em><b>IS NULL</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Explicit null check (v2 IR IsNull).
	 * <!-- end-model-doc -->
	 * @see #IS_NULL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int IS_NULL_VALUE = 11;

	/**
	 * The '<em><b>IN</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Membership test (v2 IR In).
	 * <!-- end-model-doc -->
	 * @see #IN
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int IN_VALUE = 12;

	/**
	 * The '<em><b>EXISTS</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Existential quantifier over a multi-valued navigation (v2 IR Exists).
	 * <!-- end-model-doc -->
	 * @see #EXISTS
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int EXISTS_VALUE = 13;

	/**
	 * The '<em><b>FOR ALL</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Universal quantifier over a multi-valued navigation (v2 IR ForAll).
	 * <!-- end-model-doc -->
	 * @see #FOR_ALL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int FOR_ALL_VALUE = 14;

	/**
	 * The '<em><b>STRING MATCH CASE INSENSITIVE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Case-insensitive string matching (v2 IR StringMatch.caseInsensitive).
	 * <!-- end-model-doc -->
	 * @see #STRING_MATCH_CASE_INSENSITIVE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int STRING_MATCH_CASE_INSENSITIVE_VALUE = 15;

	/**
	 * The '<em><b>FIELD TO FIELD</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Comparison whose both sides navigate features (field-to-field), directly or through string functions.
	 * <!-- end-model-doc -->
	 * @see #FIELD_TO_FIELD
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int FIELD_TO_FIELD_VALUE = 16;

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
	 * The '<em><b>EXPAND</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Eager-fetch hints (v2 envelope expand).
	 * <!-- end-model-doc -->
	 * @see #EXPAND
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int EXPAND_VALUE = 27;

	/**
	 * The '<em><b>PIPELINE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Aggregation pipeline stages beyond a single groupBy (v2 envelope apply).
	 * <!-- end-model-doc -->
	 * @see #PIPELINE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int PIPELINE_VALUE = 28;

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
	 * The '<em><b>AGG COUNT DISTINCT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Count-distinct aggregate (v2 pipeline).
	 * <!-- end-model-doc -->
	 * @see #AGG_COUNT_DISTINCT
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int AGG_COUNT_DISTINCT_VALUE = 46;

	/**
	 * The '<em><b>STRING FUNCTIONS</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * String functions applied to values (v2 IR StringFunction: toLower/toUpper/trim/length).
	 * <!-- end-model-doc -->
	 * @see #STRING_FUNCTIONS
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int STRING_FUNCTIONS_VALUE = 63;

	/**
	 * The '<em><b>ARITHMETIC</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Arithmetic value expressions (v2 IR Arithmetic ADD/SUB/MUL/DIV/MOD and Negate). DIV is floating-point division; division by a literal zero is refused by the validator.
	 * <!-- end-model-doc -->
	 * @see #ARITHMETIC
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int ARITHMETIC_VALUE = 64;

	/**
	 * The '<em><b>STRING FUNCTIONS EXTENDED</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Extended string vocabulary (v2 IR Concat, IndexOf, Substring — issue #77). Separate from STRING_FUNCTIONS so backends can opt in independently; IndexOf is 0-based/-1, Substring follows [OData-URL] 5.1.1.7.
	 * <!-- end-model-doc -->
	 * @see #STRING_FUNCTIONS_EXTENDED
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int STRING_FUNCTIONS_EXTENDED_VALUE = 65;

	/**
	 * The '<em><b>NUMERIC FUNCTIONS</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Numeric rounding functions (v2 IR NumericFunction ROUND/FLOOR/CEILING — issue #78). ROUND is half away from zero.
	 * <!-- end-model-doc -->
	 * @see #NUMERIC_FUNCTIONS
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int NUMERIC_FUNCTIONS_VALUE = 66;

	/**
	 * The '<em><b>TEMPORAL FUNCTIONS</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Temporal part extraction (v2 IR TemporalFunction YEAR/MONTH/DAY/HOUR/MINUTE/SECOND — issue #79). UTC-normative; SECOND is integral.
	 * <!-- end-model-doc -->
	 * @see #TEMPORAL_FUNCTIONS
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int TEMPORAL_FUNCTIONS_VALUE = 67;

	/**
	 * The '<em><b>TYPE CAST</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Downcast of a navigation origin (v2 IR PropertyPath.castBase — issue #80). JPA TREAT; non-instances yield null.
	 * <!-- end-model-doc -->
	 * @see #TYPE_CAST
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int TYPE_CAST_VALUE = 68;

	/**
	 * The '<em><b>TYPE CHECK</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Kind-of type test (v2 IR TypeCheck — issue #80). JPA TYPE(x) IN (concrete subtypes); Mongo refuses until a type discriminator is stored.
	 * <!-- end-model-doc -->
	 * @see #TYPE_CHECK
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int TYPE_CHECK_VALUE = 69;

	/**
	 * The '<em><b>COLLECTION COUNT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Plain element count of a multi-valued navigation (v2 IR CollectionCount — issue #81). JPA SIZE, Mongo $size over embedded collections.
	 * <!-- end-model-doc -->
	 * @see #COLLECTION_COUNT
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int COLLECTION_COUNT_VALUE = 70;

	/**
	 * The '<em><b>COLLECTION COUNT FILTERED</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Predicate-filtered element count (v2 IR CollectionCount with predicate — issue #81). JPA correlated SELECT COUNT, Mongo $size($filter) over embedded collections.
	 * <!-- end-model-doc -->
	 * @see #COLLECTION_COUNT_FILTERED
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int COLLECTION_COUNT_FILTERED_VALUE = 71;

	/**
	 * The '<em><b>PIPELINE COMPUTE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Alias-bound computed pipeline columns (v2 query ComputeStage — issue #82): terminal or after grouping, addressed via AliasRef; a trailing FilterStage after GroupBy is HAVING.
	 * <!-- end-model-doc -->
	 * @see #PIPELINE_COMPUTE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int PIPELINE_COMPUTE_VALUE = 72;

	/**
	 * The '<em><b>SORT EXPRESSION</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Ordering by an arbitrary value expression (v2 query OrderBy.key — issue #84). JPQL renders the expression inline; Mongo find-sorts refuse it.
	 * <!-- end-model-doc -->
	 * @see #SORT_EXPRESSION
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int SORT_EXPRESSION_VALUE = 73;

	/**
	 * The '<em><b>GROUP EXPRESSION</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Expression-valued group keys (GroupKey) and aggregate sources (Aggregate.source), including AliasRef to a pre-group compute alias (issue #87). JPQL re-renders the expression inline in SELECT/GROUP BY/aggregate arguments; Mongo evaluates it in $group/_id and accumulator arguments over $set fields; memory evaluates it per object with the compute alias environment.
	 * <!-- end-model-doc -->
	 * @see #GROUP_EXPRESSION
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int GROUP_EXPRESSION_VALUE = 74;

	/**
	 * The '<em><b>SCORE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The Score expression — relevance of the row under the query's predicate (issue #100), usable as sort key and computation source. Only ranking backends declare it (first consumer: the Lucene backend); memory/JPA/Mongo refuse. Deliberately no reference semantics — conformance is ordinal at best and lives with the declaring backend.
	 * <!-- end-model-doc -->
	 * @see #SCORE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int SCORE_VALUE = 75;

	/**
	 * The '<em><b>GEO WITHIN</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * GeoWithin — containment of a WGS84 position in a box or polygon (issue #101). Memory carries the reference semantics (split subject binding); Mongo 2dsphere is G-P2, Lucene follows in emf.search, JPA refuses until a PostGIS dialect story exists.
	 * <!-- end-model-doc -->
	 * @see #GEO_WITHIN
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int GEO_WITHIN_VALUE = 76;

	/**
	 * The '<em><b>GEO DISTANCE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * GeoDistance — the spherical WGS84 distance in meters as a value expression (issue #101, decision G3): composes with comparisons and the issue-#84 sort seam (nearest first). Reference: haversine over the mean earth radius; conformance banded per decision G5.
	 * <!-- end-model-doc -->
	 * @see #GEO_DISTANCE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int GEO_DISTANCE_VALUE = 77;

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
	 * Polymorphic type filter via Query.from (root type plus subtypes).
	 * <!-- end-model-doc -->
	 * @see #TYPE_FILTER
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int TYPE_FILTER_VALUE = 81;

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
			LOGICAL_AND,
			LOGICAL_OR,
			LOGICAL_NOT,
			WHERE_NE,
			IS_NULL,
			IN,
			EXISTS,
			FOR_ALL,
			STRING_MATCH_CASE_INSENSITIVE,
			FIELD_TO_FIELD,
			SORT,
			LIMIT,
			SKIP,
			DISTINCT,
			COUNT,
			PROJECTION,
			PROJECTION_NESTED,
			EXPAND,
			PIPELINE,
			GROUP_BY,
			AGG_AVG,
			AGG_MIN,
			AGG_MAX,
			AGG_SUM,
			AGG_COUNT,
			AGG_COUNT_DISTINCT,
			STRING_FUNCTIONS,
			ARITHMETIC,
			STRING_FUNCTIONS_EXTENDED,
			NUMERIC_FUNCTIONS,
			TEMPORAL_FUNCTIONS,
			TYPE_CAST,
			TYPE_CHECK,
			COLLECTION_COUNT,
			COLLECTION_COUNT_FILTERED,
			PIPELINE_COMPUTE,
			SORT_EXPRESSION,
			GROUP_EXPRESSION,
			SCORE,
			GEO_WITHIN,
			GEO_DISTANCE,
			FEATUREPATH_NESTED,
			TYPE_FILTER,
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
			case LOGICAL_AND_VALUE: return LOGICAL_AND;
			case LOGICAL_OR_VALUE: return LOGICAL_OR;
			case LOGICAL_NOT_VALUE: return LOGICAL_NOT;
			case WHERE_NE_VALUE: return WHERE_NE;
			case IS_NULL_VALUE: return IS_NULL;
			case IN_VALUE: return IN;
			case EXISTS_VALUE: return EXISTS;
			case FOR_ALL_VALUE: return FOR_ALL;
			case STRING_MATCH_CASE_INSENSITIVE_VALUE: return STRING_MATCH_CASE_INSENSITIVE;
			case FIELD_TO_FIELD_VALUE: return FIELD_TO_FIELD;
			case SORT_VALUE: return SORT;
			case LIMIT_VALUE: return LIMIT;
			case SKIP_VALUE: return SKIP;
			case DISTINCT_VALUE: return DISTINCT;
			case COUNT_VALUE: return COUNT;
			case PROJECTION_VALUE: return PROJECTION;
			case PROJECTION_NESTED_VALUE: return PROJECTION_NESTED;
			case EXPAND_VALUE: return EXPAND;
			case PIPELINE_VALUE: return PIPELINE;
			case GROUP_BY_VALUE: return GROUP_BY;
			case AGG_AVG_VALUE: return AGG_AVG;
			case AGG_MIN_VALUE: return AGG_MIN;
			case AGG_MAX_VALUE: return AGG_MAX;
			case AGG_SUM_VALUE: return AGG_SUM;
			case AGG_COUNT_VALUE: return AGG_COUNT;
			case AGG_COUNT_DISTINCT_VALUE: return AGG_COUNT_DISTINCT;
			case STRING_FUNCTIONS_VALUE: return STRING_FUNCTIONS;
			case ARITHMETIC_VALUE: return ARITHMETIC;
			case STRING_FUNCTIONS_EXTENDED_VALUE: return STRING_FUNCTIONS_EXTENDED;
			case NUMERIC_FUNCTIONS_VALUE: return NUMERIC_FUNCTIONS;
			case TEMPORAL_FUNCTIONS_VALUE: return TEMPORAL_FUNCTIONS;
			case TYPE_CAST_VALUE: return TYPE_CAST;
			case TYPE_CHECK_VALUE: return TYPE_CHECK;
			case COLLECTION_COUNT_VALUE: return COLLECTION_COUNT;
			case COLLECTION_COUNT_FILTERED_VALUE: return COLLECTION_COUNT_FILTERED;
			case PIPELINE_COMPUTE_VALUE: return PIPELINE_COMPUTE;
			case SORT_EXPRESSION_VALUE: return SORT_EXPRESSION;
			case GROUP_EXPRESSION_VALUE: return GROUP_EXPRESSION;
			case SCORE_VALUE: return SCORE;
			case GEO_WITHIN_VALUE: return GEO_WITHIN;
			case GEO_DISTANCE_VALUE: return GEO_DISTANCE;
			case FEATUREPATH_NESTED_VALUE: return FEATUREPATH_NESTED;
			case TYPE_FILTER_VALUE: return TYPE_FILTER;
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
