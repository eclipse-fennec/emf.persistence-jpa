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
 * A representation of the model object '<em><b>For All</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Every element satisfies the predicate (OCL forAll, SQL NOT EXISTS with negated predicate). Vacuously true on empty collections.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.model.expression.ExpressionPackage#getForAll()
 * @model
 * @generated
 */
@ProviderType
public interface ForAll extends Quantifier {
} // ForAll
