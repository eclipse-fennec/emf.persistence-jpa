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

import org.osgi.util.pushstream.PushEventSource;
import org.osgi.util.pushstream.PushStreamProvider;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Custom Push Stream Provider</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.pushstream.CustomPushStreamProvider#getEventSource <em>Event Source</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.pushstream.CustomPushStreamProvider#getProvider <em>Provider</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.pushstream.PushStreamPackage#getCustomPushStreamProvider()
 * @model
 * @generated
 */
@ProviderType
public interface CustomPushStreamProvider extends EPushStreamProvider {
	/**
	 * Returns the value of the '<em><b>Event Source</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Event Source</em>' attribute.
	 * @see #setEventSource(PushEventSource)
	 * @see org.eclipse.fennec.model.pushstream.PushStreamPackage#getCustomPushStreamProvider_EventSource()
	 * @model dataType="org.eclipse.fennec.model.pushstream.EPushEventSource" required="true" transient="true"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel"
	 * @generated
	 */
	PushEventSource<EObject> getEventSource();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.pushstream.CustomPushStreamProvider#getEventSource <em>Event Source</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Event Source</em>' attribute.
	 * @see #getEventSource()
	 * @generated
	 */
	void setEventSource(PushEventSource<EObject> value);

	/**
	 * Returns the value of the '<em><b>Provider</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Provider</em>' attribute.
	 * @see #setProvider(PushStreamProvider)
	 * @see org.eclipse.fennec.model.pushstream.PushStreamPackage#getCustomPushStreamProvider_Provider()
	 * @model dataType="org.eclipse.fennec.model.pushstream.PushStreamProvider" required="true"
	 * @generated
	 */
	PushStreamProvider getProvider();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.pushstream.CustomPushStreamProvider#getProvider <em>Provider</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Provider</em>' attribute.
	 * @see #getProvider()
	 * @generated
	 */
	void setProvider(PushStreamProvider value);

} // CustomPushStreamProvider
