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

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Query Result Row</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * A single projection or aggregation row: cells addressed either by the subject alias or by ordinal position. Values are already typed via the ConverterService.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.persistence.query.api.QueryApiPackage#getQueryResultRow()
 * @model interface="true" abstract="true"
 * @generated
 */
@ProviderType
public interface QueryResultRow {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The cell value for the subject alias (QSubject.alias), or null if absent.
	 * <!-- end-model-doc -->
	 * @model aliasRequired="true"
	 * @generated
	 */
	Object get(String alias);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The cell value at the zero-based ordinal position.
	 * <!-- end-model-doc -->
	 * @model indexRequired="true"
	 * @generated
	 */
	Object get(int index);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The cell values in subject order, as an immutable list.
	 * <!-- end-model-doc -->
	 * @model dataType="org.eclipse.fennec.persistence.query.api.ObjectList"
	 * @generated
	 */
	List<Object> values();

} // QueryResultRow
