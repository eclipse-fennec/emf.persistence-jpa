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
package org.eclipse.fennec.model.pushstream;

import org.eclipse.emf.ecore.EFactory;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.model.pushstream.PushStreamPackage
 * @generated
 */
@ProviderType
public interface PushStreamFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	PushStreamFactory eINSTANCE = org.eclipse.fennec.model.pushstream.impl.PushStreamFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Simple Push Stream Provider</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Simple Push Stream Provider</em>'.
	 * @generated
	 */
	SimplePushStreamProvider createSimplePushStreamProvider();

	/**
	 * Returns a new object of class '<em>Custom Push Stream Provider</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Custom Push Stream Provider</em>'.
	 * @generated
	 */
	CustomPushStreamProvider createCustomPushStreamProvider();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	PushStreamPackage getPushStreamPackage();

} //PushStreamFactory
