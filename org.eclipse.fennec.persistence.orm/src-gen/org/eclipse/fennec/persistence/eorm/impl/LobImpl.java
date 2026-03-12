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
package org.eclipse.fennec.persistence.eorm.impl;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.eorm.Lob;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Lob</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class LobImpl extends MinimalEObjectImpl.Container implements Lob {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LobImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EORMPackage.eINSTANCE.getLob();
	}

} //LobImpl
