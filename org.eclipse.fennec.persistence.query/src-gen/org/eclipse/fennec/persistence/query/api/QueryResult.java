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

import java.lang.AutoCloseable;

import java.util.Map;

import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Query Result</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * The outcome of executing a QueryPlan. The accessor that matches shape() is valid; the others throw IllegalStateException. A result may hold backend resources (cursors, connections) and must be closed by the caller — use try-with-resources. Closing also closes any stream obtained from it.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.persistence.query.api.QueryApiPackage#getQueryResult()
 * @model interface="true" abstract="true" superTypes="org.eclipse.fennec.persistence.query.api.AutoCloseable"
 * @generated
 */
@ProviderType
public interface QueryResult extends AutoCloseable {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The shape of this result, determining which accessor is valid.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	QueryShape shape();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Valid for OBJECTS: the selected entities, streamed lazily. Closed together with this result. Throws IllegalStateException for any other shape.
	 * <!-- end-model-doc -->
	 * @model dataType="org.eclipse.fennec.persistence.query.api.EObjectStream"
	 * @generated
	 */
	Stream<EObject> objects();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Valid for PROJECTION and AGGREGATION: the result rows. Closed together with this result. Throws IllegalStateException for any other shape.
	 * <!-- end-model-doc -->
	 * @model dataType="org.eclipse.fennec.persistence.query.api.RowStream"
	 * @generated
	 */
	Stream<QueryResultRow> rows();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Valid for COUNT: the cardinality. Throws IllegalStateException for any other shape.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	long count();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Valid for OBJECTS results of a withScores query (issue #165): the hits paired with their scores, streamed lazily. hits() and objects() are two views of the same underlying cursor — consume one of them. Under withScores the iteration order is rank order unless an explicit sort says otherwise. Throws IllegalStateException for other shapes or when the query did not request scores.
	 * <!-- end-model-doc -->
	 * @model dataType="org.eclipse.fennec.persistence.query.api.HitStream"
	 * @generated
	 */
	Stream<Hit> hits();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The metadata-only view of the hits (issue #165): object id → score, complete at query() time — before any object is materialized — so a consumer can inspect the score distribution and then decide what to consume. Filled when the query carried withScores and the backend declares SCORE, empty otherwise. Never throws and never null: unlike the shape accessors this is a soft side channel, because a result without scores is not an invalid access, just an unscored one.
	 * <!-- end-model-doc -->
	 * @model dataType="org.eclipse.fennec.persistence.query.api.ScoreMap"
	 * @generated
	 */
	Map<String, Double> scores();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Releases all backend resources held by this result. Never throws.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	void close();

} // QueryResult
