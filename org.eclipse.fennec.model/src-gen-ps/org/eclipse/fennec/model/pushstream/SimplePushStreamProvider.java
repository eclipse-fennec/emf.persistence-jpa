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

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

import org.osgi.util.pushstream.PushStreamProvider;
import org.osgi.util.pushstream.SimplePushEventSource;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Simple Push Stream Provider</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.pushstream.SimplePushStreamProvider#getProvider <em>Provider</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.pushstream.SimplePushStreamProvider#getEventSource <em>Event Source</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.pushstream.PushStreamPackage#getSimplePushStreamProvider()
 * @model features="internalSource" 
 *        internalSourceDataType="org.eclipse.fennec.model.pushstream.ESimplePushEventSource" internalSourceTransient="true" internalSourceChangeable="false" internalSourceDerived="true" internalSourceSuppressedGetVisibility="true"
 * @generated
 */
@ProviderType
public interface SimplePushStreamProvider extends EPushStreamProvider {
	/**
	 * Returns the value of the '<em><b>Provider</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Provider</em>' attribute.
	 * @see #setProvider(PushStreamProvider)
	 * @see org.eclipse.fennec.model.pushstream.PushStreamPackage#getSimplePushStreamProvider_Provider()
	 * @model dataType="org.eclipse.fennec.model.pushstream.PushStreamProvider" required="true"
	 * @generated
	 */
	PushStreamProvider getProvider();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.pushstream.SimplePushStreamProvider#getProvider <em>Provider</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Provider</em>' attribute.
	 * @see #getProvider()
	 * @generated
	 */
	void setProvider(PushStreamProvider value);

	/**
	 * Returns the value of the '<em><b>Event Source</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * We generate the code for the getter and lazy intialize the internalSource feature
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Event Source</em>' attribute.
	 * @see org.eclipse.fennec.model.pushstream.PushStreamPackage#getSimplePushStreamProvider_EventSource()
	 * @model dataType="org.eclipse.fennec.model.pushstream.ESimplePushEventSource" required="true" transient="true" changeable="false" volatile="true" derived="true"
	 * @generated
	 */
	SimplePushEventSource<EObject> getEventSource();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Publishes an EObject into the internal eventSource
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	void publishEObject(EObject eObject);

} // SimplePushStreamProvider
