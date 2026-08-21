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
package org.eclipse.fennec.persistence.capabilities;


import org.eclipse.emf.ecore.EEnum;

import org.eclipse.fennec.emf.osgi.annotation.provide.EPackage;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * The closed feature vocabulary a backend declares against (issue #134). Only the enums live here: the vocabulary is the contract, so nobody extends it from outside, and it is the one half of the declaration surface that stays modelled. The capability types a backend answers with - QueryCapabilities, CommandCapabilities - are plain Java in the same bundle, because they are values that get asked rather than EObjects that get loaded. Lives outside the query bundle so that declaring something has no dependency on the query model. See docs/unified-persistence/conformance-and-capabilities.md §5a.
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.persistence.capabilities.CapabilitiesFactory
 * @model kind="package"
 *        annotation="Version value='1.0'"
 * @generated
 */
@ProviderType
@EPackage(uri = CapabilitiesPackage.eNS_URI, fingerprint = "fp1:d61f4b6f2cca1e12795ca901e34f12ce28fbd4f5cf3d167420d2ed1f828c07d6", genModel = "/model/capabilities.genmodel", genModelSourceLocations = {"model/capabilities.genmodel","org.eclipse.fennec.persistence.capabilities/model/capabilities.genmodel"}, ecore = "/model/capabilities.ecore", ecoreSourceLocations = "/model/capabilities.ecore")
public interface CapabilitiesPackage extends org.eclipse.emf.ecore.EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "capabilities";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://eclipse.org/fennec/persistence/capabilities/1.0.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "persistence.capabilities";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	CapabilitiesPackage eINSTANCE = org.eclipse.fennec.persistence.capabilities.impl.CapabilitiesPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.capabilities.QueryFeature <em>Query Feature</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.capabilities.QueryFeature
	 * @see org.eclipse.fennec.persistence.capabilities.impl.CapabilitiesPackageImpl#getQueryFeature()
	 * @generated
	 */
	int QUERY_FEATURE = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.capabilities.CommandFeature <em>Command Feature</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.capabilities.CommandFeature
	 * @see org.eclipse.fennec.persistence.capabilities.impl.CapabilitiesPackageImpl#getCommandFeature()
	 * @generated
	 */
	int COMMAND_FEATURE = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.persistence.capabilities.StoreFeature <em>Store Feature</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.persistence.capabilities.StoreFeature
	 * @see org.eclipse.fennec.persistence.capabilities.impl.CapabilitiesPackageImpl#getStoreFeature()
	 * @generated
	 */
	int STORE_FEATURE = 2;


	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.persistence.capabilities.QueryFeature <em>Query Feature</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Query Feature</em>'.
	 * @see org.eclipse.fennec.persistence.capabilities.QueryFeature
	 * @generated
	 */
	EEnum getQueryFeature();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.persistence.capabilities.CommandFeature <em>Command Feature</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Command Feature</em>'.
	 * @see org.eclipse.fennec.persistence.capabilities.CommandFeature
	 * @generated
	 */
	EEnum getCommandFeature();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.persistence.capabilities.StoreFeature <em>Store Feature</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Store Feature</em>'.
	 * @see org.eclipse.fennec.persistence.capabilities.StoreFeature
	 * @generated
	 */
	EEnum getStoreFeature();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	CapabilitiesFactory getCapabilitiesFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.persistence.capabilities.QueryFeature <em>Query Feature</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.capabilities.QueryFeature
		 * @see org.eclipse.fennec.persistence.capabilities.impl.CapabilitiesPackageImpl#getQueryFeature()
		 * @generated
		 */
		EEnum QUERY_FEATURE = eINSTANCE.getQueryFeature();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.persistence.capabilities.CommandFeature <em>Command Feature</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.capabilities.CommandFeature
		 * @see org.eclipse.fennec.persistence.capabilities.impl.CapabilitiesPackageImpl#getCommandFeature()
		 * @generated
		 */
		EEnum COMMAND_FEATURE = eINSTANCE.getCommandFeature();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.persistence.capabilities.StoreFeature <em>Store Feature</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.persistence.capabilities.StoreFeature
		 * @see org.eclipse.fennec.persistence.capabilities.impl.CapabilitiesPackageImpl#getStoreFeature()
		 * @generated
		 */
		EEnum STORE_FEATURE = eINSTANCE.getStoreFeature();

	}

} //CapabilitiesPackage
