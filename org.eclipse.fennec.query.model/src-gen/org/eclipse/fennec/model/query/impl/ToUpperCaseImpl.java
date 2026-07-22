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
package org.eclipse.fennec.model.query.impl;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.fennec.model.query.QueryPackage;
import org.eclipse.fennec.model.query.ToUpperCase;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>To Upper Case</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class ToUpperCaseImpl extends StringOperationImpl implements ToUpperCase {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ToUpperCaseImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return QueryPackage.Literals.TO_UPPER_CASE;
	}

} //ToUpperCaseImpl
