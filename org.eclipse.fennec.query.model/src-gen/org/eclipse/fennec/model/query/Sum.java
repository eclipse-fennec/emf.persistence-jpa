/*
 * Copyright (c) 2012 - 2026 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.model.query;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Sum</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Sum aggregate function. On a subject it aggregates the addressed feature; without groupBy over the whole result set (single row).
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.model.query.QueryPackage#getSum()
 * @model
 * @generated
 */
@ProviderType
public interface Sum extends NumberOperation {
} // Sum
