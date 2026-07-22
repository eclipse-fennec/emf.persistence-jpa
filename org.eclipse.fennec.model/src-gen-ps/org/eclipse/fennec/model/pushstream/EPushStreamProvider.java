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

import java.util.concurrent.BlockingQueue;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

import org.osgi.util.pushstream.PushEvent;
import org.osgi.util.pushstream.PushStream;
import org.osgi.util.pushstream.PushStreamBuilder;
import org.osgi.util.pushstream.SimplePushEventSource;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EPush Stream Provider</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see org.eclipse.fennec.model.pushstream.PushStreamPackage#getEPushStreamProvider()
 * @model interface="true" abstract="true"
 * @generated
 */
@ProviderType
public interface EPushStreamProvider extends EObject {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Creates a PushStream from the internal eventSource feature
	 * <!-- end-model-doc -->
	 * @model dataType="org.eclipse.fennec.model.pushstream.EPushStream" required="true"
	 * @generated
	 */
	PushStream<EObject> createPushStream();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Creates a new SimplePushEventSource from the given PushStreamProvider
	 * <!-- end-model-doc -->
	 * @model dataType="org.eclipse.fennec.model.pushstream.ESimplePushEventSource" required="true"
	 * @generated
	 */
	SimplePushEventSource<EObject> createSimplePushEventSource();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Creates a PushStream from the internal eventSource feature
	 * <!-- end-model-doc -->
	 * @model dataType="org.eclipse.fennec.model.pushstream.EPushStream" required="true"
	 * @generated
	 */
	PushStream<EObject> createPushStreamUnbuffered();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Creates a PushStreamBuilder to customize the settings
	 * <!-- end-model-doc -->
	 * @model dataType="org.eclipse.fennec.model.pushstream.PushStreamBuilder"
	 * @generated
	 */
	PushStreamBuilder<EObject, BlockingQueue<PushEvent<? extends EObject>>> createPushStreamBuilder();

} // EPushStreamProvider
