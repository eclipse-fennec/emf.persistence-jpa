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
package org.eclipse.fennec.model.stream;

import org.eclipse.emf.ecore.EFactory;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.model.stream.StreamPackage
 * @generated
 */
@ProviderType
public interface StreamFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	StreamFactory eINSTANCE = org.eclipse.fennec.model.stream.impl.StreamFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Change Set</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Change Set</em>'.
	 * @generated
	 */
	ChangeSet createChangeSet();

	/**
	 * Returns a new object of class '<em>Change Entry</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Change Entry</em>'.
	 * @generated
	 */
	ChangeEntry createChangeEntry();

	/**
	 * Returns a new object of class '<em>Slot Value</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Slot Value</em>'.
	 * @generated
	 */
	SlotValue createSlotValue();

	/**
	 * Returns a new object of class '<em>Context Snapshot</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Context Snapshot</em>'.
	 * @generated
	 */
	ContextSnapshot createContextSnapshot();

	/**
	 * Returns a new object of class '<em>Composite Snapshot</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Composite Snapshot</em>'.
	 * @generated
	 */
	CompositeSnapshot createCompositeSnapshot();

	/**
	 * Returns a new object of class '<em>Package Fingerprint</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Package Fingerprint</em>'.
	 * @generated
	 */
	PackageFingerprint createPackageFingerprint();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	StreamPackage getStreamPackage();

} //StreamFactory
