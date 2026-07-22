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
 * A representation of the model object '<em><b>Count Operation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Count aggregate function (per group, or over the whole result set without groupBy). Named CountOperation to avoid clashing with Query.count, which requests a count-only result.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.model.query.QueryPackage#getCountOperation()
 * @model
 * @generated
 */
@ProviderType
public interface CountOperation extends NumberOperation {
} // CountOperation
