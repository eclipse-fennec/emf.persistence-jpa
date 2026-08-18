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
package org.eclipse.fennec.persistence.repository.api;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Repository</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * The full repository combining read and write side — the primary user-facing service, successor of the Gecko EMFRepository. Adds no operations of its own.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.persistence.repository.api.RepositoryApiPackage#getRepository()
 * @model interface="true" abstract="true"
 * @generated
 */
@ProviderType
public interface Repository extends ReadRepository, WriteRepository {
} // Repository
