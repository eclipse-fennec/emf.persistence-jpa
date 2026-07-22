/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 * 
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 */
package org.eclipse.fennec.model.collection;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>ECollection</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see org.eclipse.fennec.model.collection.CollectionPackage#getECollection()
 * @model interface="true" abstract="true"
 * @generated
 */
@ProviderType
public interface ECollection extends EObject {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation" dataType="org.eclipse.fennec.model.collection.EList" unique="false"
	 * @generated
	 */
	EList<EObject> getValues();

} // ECollection
