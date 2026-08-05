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

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Score</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * The relevance score of the row under the query's predicate (issue #100) — a leaf value expression, usable as a sort key (OrderBy.key, issue #84) and as a computation source. Only ranking backends declare the SCORE capability (first consumer: the Lucene backend); there is deliberately no reference semantics — a score without a text-scoring model would be false precision — so conformance is ordinal at best and lives with the declaring backend.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getScore()
 * @model
 * @generated
 */
@ProviderType
public interface Score extends Expression {
} // Score
