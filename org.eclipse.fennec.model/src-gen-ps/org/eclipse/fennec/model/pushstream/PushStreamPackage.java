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


import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;

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
 * @see org.eclipse.fennec.model.pushstream.PushStreamFactory
 * @model kind="package"
 *        annotation="Version value='1.2'"
 * @generated
 */
@ProviderType
@EPackage(uri = PushStreamPackage.eNS_URI, genModel = "/model/pushstream.genmodel", genModelSourceLocations = {"model/pushstream.genmodel","org.eclipse.fennec.model/model/pushstream.genmodel"}, ecore = "/model/pushstream.ecore", ecoreSourceLocations = "/model/pushstream.ecore")
public interface PushStreamPackage extends org.eclipse.emf.ecore.EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "pushstream";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://org.eclipse/fennec/pushstream/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "pushstream";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	PushStreamPackage eINSTANCE = org.eclipse.fennec.model.pushstream.impl.PushStreamPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.pushstream.EPushStreamProvider <em>EPush Stream Provider</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.pushstream.EPushStreamProvider
	 * @see org.eclipse.fennec.model.pushstream.impl.PushStreamPackageImpl#getEPushStreamProvider()
	 * @generated
	 */
	int EPUSH_STREAM_PROVIDER = 0;

	/**
	 * The number of structural features of the '<em>EPush Stream Provider</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EPUSH_STREAM_PROVIDER_FEATURE_COUNT = 0;

	/**
	 * The operation id for the '<em>Create Push Stream</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EPUSH_STREAM_PROVIDER___CREATE_PUSH_STREAM = 0;

	/**
	 * The operation id for the '<em>Create Simple Push Event Source</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EPUSH_STREAM_PROVIDER___CREATE_SIMPLE_PUSH_EVENT_SOURCE = 1;

	/**
	 * The operation id for the '<em>Create Push Stream Unbuffered</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EPUSH_STREAM_PROVIDER___CREATE_PUSH_STREAM_UNBUFFERED = 2;

	/**
	 * The operation id for the '<em>Create Push Stream Builder</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EPUSH_STREAM_PROVIDER___CREATE_PUSH_STREAM_BUILDER = 3;

	/**
	 * The number of operations of the '<em>EPush Stream Provider</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EPUSH_STREAM_PROVIDER_OPERATION_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.pushstream.impl.SimplePushStreamProviderImpl <em>Simple Push Stream Provider</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.pushstream.impl.SimplePushStreamProviderImpl
	 * @see org.eclipse.fennec.model.pushstream.impl.PushStreamPackageImpl#getSimplePushStreamProvider()
	 * @generated
	 */
	int SIMPLE_PUSH_STREAM_PROVIDER = 1;

	/**
	 * The feature id for the '<em><b>Provider</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_PUSH_STREAM_PROVIDER__PROVIDER = EPUSH_STREAM_PROVIDER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Internal Source</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_PUSH_STREAM_PROVIDER__INTERNAL_SOURCE = EPUSH_STREAM_PROVIDER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Event Source</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_PUSH_STREAM_PROVIDER__EVENT_SOURCE = EPUSH_STREAM_PROVIDER_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Simple Push Stream Provider</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_PUSH_STREAM_PROVIDER_FEATURE_COUNT = EPUSH_STREAM_PROVIDER_FEATURE_COUNT + 3;

	/**
	 * The operation id for the '<em>Create Push Stream</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_PUSH_STREAM_PROVIDER___CREATE_PUSH_STREAM = EPUSH_STREAM_PROVIDER___CREATE_PUSH_STREAM;

	/**
	 * The operation id for the '<em>Create Simple Push Event Source</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_PUSH_STREAM_PROVIDER___CREATE_SIMPLE_PUSH_EVENT_SOURCE = EPUSH_STREAM_PROVIDER___CREATE_SIMPLE_PUSH_EVENT_SOURCE;

	/**
	 * The operation id for the '<em>Create Push Stream Unbuffered</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_PUSH_STREAM_PROVIDER___CREATE_PUSH_STREAM_UNBUFFERED = EPUSH_STREAM_PROVIDER___CREATE_PUSH_STREAM_UNBUFFERED;

	/**
	 * The operation id for the '<em>Create Push Stream Builder</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_PUSH_STREAM_PROVIDER___CREATE_PUSH_STREAM_BUILDER = EPUSH_STREAM_PROVIDER___CREATE_PUSH_STREAM_BUILDER;

	/**
	 * The operation id for the '<em>Publish EObject</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_PUSH_STREAM_PROVIDER___PUBLISH_EOBJECT__EOBJECT = EPUSH_STREAM_PROVIDER_OPERATION_COUNT + 0;

	/**
	 * The number of operations of the '<em>Simple Push Stream Provider</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_PUSH_STREAM_PROVIDER_OPERATION_COUNT = EPUSH_STREAM_PROVIDER_OPERATION_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.pushstream.impl.CustomPushStreamProviderImpl <em>Custom Push Stream Provider</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.pushstream.impl.CustomPushStreamProviderImpl
	 * @see org.eclipse.fennec.model.pushstream.impl.PushStreamPackageImpl#getCustomPushStreamProvider()
	 * @generated
	 */
	int CUSTOM_PUSH_STREAM_PROVIDER = 2;

	/**
	 * The feature id for the '<em><b>Event Source</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CUSTOM_PUSH_STREAM_PROVIDER__EVENT_SOURCE = EPUSH_STREAM_PROVIDER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Provider</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CUSTOM_PUSH_STREAM_PROVIDER__PROVIDER = EPUSH_STREAM_PROVIDER_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Custom Push Stream Provider</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CUSTOM_PUSH_STREAM_PROVIDER_FEATURE_COUNT = EPUSH_STREAM_PROVIDER_FEATURE_COUNT + 2;

	/**
	 * The operation id for the '<em>Create Push Stream</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CUSTOM_PUSH_STREAM_PROVIDER___CREATE_PUSH_STREAM = EPUSH_STREAM_PROVIDER___CREATE_PUSH_STREAM;

	/**
	 * The operation id for the '<em>Create Simple Push Event Source</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CUSTOM_PUSH_STREAM_PROVIDER___CREATE_SIMPLE_PUSH_EVENT_SOURCE = EPUSH_STREAM_PROVIDER___CREATE_SIMPLE_PUSH_EVENT_SOURCE;

	/**
	 * The operation id for the '<em>Create Push Stream Unbuffered</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CUSTOM_PUSH_STREAM_PROVIDER___CREATE_PUSH_STREAM_UNBUFFERED = EPUSH_STREAM_PROVIDER___CREATE_PUSH_STREAM_UNBUFFERED;

	/**
	 * The operation id for the '<em>Create Push Stream Builder</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CUSTOM_PUSH_STREAM_PROVIDER___CREATE_PUSH_STREAM_BUILDER = EPUSH_STREAM_PROVIDER___CREATE_PUSH_STREAM_BUILDER;

	/**
	 * The number of operations of the '<em>Custom Push Stream Provider</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CUSTOM_PUSH_STREAM_PROVIDER_OPERATION_COUNT = EPUSH_STREAM_PROVIDER_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '<em>EPush Stream</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.osgi.util.pushstream.PushStream
	 * @see org.eclipse.fennec.model.pushstream.impl.PushStreamPackageImpl#getEPushStream()
	 * @generated
	 */
	int EPUSH_STREAM = 3;

	/**
	 * The meta object id for the '<em>Provider</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.osgi.util.pushstream.PushStreamProvider
	 * @see org.eclipse.fennec.model.pushstream.impl.PushStreamPackageImpl#getPushStreamProvider()
	 * @generated
	 */
	int PUSH_STREAM_PROVIDER = 4;

	/**
	 * The meta object id for the '<em>Builder</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.osgi.util.pushstream.PushStreamBuilder
	 * @see org.eclipse.fennec.model.pushstream.impl.PushStreamPackageImpl#getPushStreamBuilder()
	 * @generated
	 */
	int PUSH_STREAM_BUILDER = 5;

	/**
	 * The meta object id for the '<em>ESimple Push Event Source</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.osgi.util.pushstream.SimplePushEventSource
	 * @see org.eclipse.fennec.model.pushstream.impl.PushStreamPackageImpl#getESimplePushEventSource()
	 * @generated
	 */
	int ESIMPLE_PUSH_EVENT_SOURCE = 6;

	/**
	 * The meta object id for the '<em>EPush Event Source</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.osgi.util.pushstream.PushEventSource
	 * @see org.eclipse.fennec.model.pushstream.impl.PushStreamPackageImpl#getEPushEventSource()
	 * @generated
	 */
	int EPUSH_EVENT_SOURCE = 7;


	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.pushstream.EPushStreamProvider <em>EPush Stream Provider</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EPush Stream Provider</em>'.
	 * @see org.eclipse.fennec.model.pushstream.EPushStreamProvider
	 * @generated
	 */
	EClass getEPushStreamProvider();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.pushstream.EPushStreamProvider#createPushStream() <em>Create Push Stream</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Create Push Stream</em>' operation.
	 * @see org.eclipse.fennec.model.pushstream.EPushStreamProvider#createPushStream()
	 * @generated
	 */
	EOperation getEPushStreamProvider__CreatePushStream();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.pushstream.EPushStreamProvider#createSimplePushEventSource() <em>Create Simple Push Event Source</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Create Simple Push Event Source</em>' operation.
	 * @see org.eclipse.fennec.model.pushstream.EPushStreamProvider#createSimplePushEventSource()
	 * @generated
	 */
	EOperation getEPushStreamProvider__CreateSimplePushEventSource();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.pushstream.EPushStreamProvider#createPushStreamUnbuffered() <em>Create Push Stream Unbuffered</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Create Push Stream Unbuffered</em>' operation.
	 * @see org.eclipse.fennec.model.pushstream.EPushStreamProvider#createPushStreamUnbuffered()
	 * @generated
	 */
	EOperation getEPushStreamProvider__CreatePushStreamUnbuffered();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.pushstream.EPushStreamProvider#createPushStreamBuilder() <em>Create Push Stream Builder</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Create Push Stream Builder</em>' operation.
	 * @see org.eclipse.fennec.model.pushstream.EPushStreamProvider#createPushStreamBuilder()
	 * @generated
	 */
	EOperation getEPushStreamProvider__CreatePushStreamBuilder();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.pushstream.SimplePushStreamProvider <em>Simple Push Stream Provider</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Simple Push Stream Provider</em>'.
	 * @see org.eclipse.fennec.model.pushstream.SimplePushStreamProvider
	 * @generated
	 */
	EClass getSimplePushStreamProvider();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.pushstream.SimplePushStreamProvider#getProvider <em>Provider</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Provider</em>'.
	 * @see org.eclipse.fennec.model.pushstream.SimplePushStreamProvider#getProvider()
	 * @see #getSimplePushStreamProvider()
	 * @generated
	 */
	EAttribute getSimplePushStreamProvider_Provider();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.pushstream.SimplePushStreamProvider <em>Internal Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Internal Source</em>'.
	 * @see org.eclipse.fennec.model.pushstream.SimplePushStreamProvider
	 * @see #getSimplePushStreamProvider()
	 * @generated
	 */
	EAttribute getSimplePushStreamProvider_InternalSource();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.pushstream.SimplePushStreamProvider#getEventSource <em>Event Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Event Source</em>'.
	 * @see org.eclipse.fennec.model.pushstream.SimplePushStreamProvider#getEventSource()
	 * @see #getSimplePushStreamProvider()
	 * @generated
	 */
	EAttribute getSimplePushStreamProvider_EventSource();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.pushstream.SimplePushStreamProvider#publishEObject(org.eclipse.emf.ecore.EObject) <em>Publish EObject</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Publish EObject</em>' operation.
	 * @see org.eclipse.fennec.model.pushstream.SimplePushStreamProvider#publishEObject(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	EOperation getSimplePushStreamProvider__PublishEObject__EObject();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.pushstream.CustomPushStreamProvider <em>Custom Push Stream Provider</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Custom Push Stream Provider</em>'.
	 * @see org.eclipse.fennec.model.pushstream.CustomPushStreamProvider
	 * @generated
	 */
	EClass getCustomPushStreamProvider();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.pushstream.CustomPushStreamProvider#getEventSource <em>Event Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Event Source</em>'.
	 * @see org.eclipse.fennec.model.pushstream.CustomPushStreamProvider#getEventSource()
	 * @see #getCustomPushStreamProvider()
	 * @generated
	 */
	EAttribute getCustomPushStreamProvider_EventSource();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.pushstream.CustomPushStreamProvider#getProvider <em>Provider</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Provider</em>'.
	 * @see org.eclipse.fennec.model.pushstream.CustomPushStreamProvider#getProvider()
	 * @see #getCustomPushStreamProvider()
	 * @generated
	 */
	EAttribute getCustomPushStreamProvider_Provider();

	/**
	 * Returns the meta object for data type '{@link org.osgi.util.pushstream.PushStream <em>EPush Stream</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>EPush Stream</em>'.
	 * @see org.osgi.util.pushstream.PushStream
	 * @model instanceClass="org.osgi.util.pushstream.PushStream&lt;org.eclipse.emf.ecore.EObject&gt;"
	 * @generated
	 */
	EDataType getEPushStream();

	/**
	 * Returns the meta object for data type '{@link org.osgi.util.pushstream.PushStreamProvider <em>Provider</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Provider</em>'.
	 * @see org.osgi.util.pushstream.PushStreamProvider
	 * @model instanceClass="org.osgi.util.pushstream.PushStreamProvider"
	 * @generated
	 */
	EDataType getPushStreamProvider();

	/**
	 * Returns the meta object for data type '{@link org.osgi.util.pushstream.PushStreamBuilder <em>Builder</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Builder</em>'.
	 * @see org.osgi.util.pushstream.PushStreamBuilder
	 * @model instanceClass="org.osgi.util.pushstream.PushStreamBuilder&lt;org.eclipse.emf.ecore.EObject, java.util.concurrent.BlockingQueue&lt;org.osgi.util.pushstream.PushEvent&lt;? extends org.eclipse.emf.ecore.EObject&gt;&gt;&gt;"
	 * @generated
	 */
	EDataType getPushStreamBuilder();

	/**
	 * Returns the meta object for data type '{@link org.osgi.util.pushstream.SimplePushEventSource <em>ESimple Push Event Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>ESimple Push Event Source</em>'.
	 * @see org.osgi.util.pushstream.SimplePushEventSource
	 * @model instanceClass="org.osgi.util.pushstream.SimplePushEventSource&lt;org.eclipse.emf.ecore.EObject&gt;"
	 * @generated
	 */
	EDataType getESimplePushEventSource();

	/**
	 * Returns the meta object for data type '{@link org.osgi.util.pushstream.PushEventSource <em>EPush Event Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>EPush Event Source</em>'.
	 * @see org.osgi.util.pushstream.PushEventSource
	 * @model instanceClass="org.osgi.util.pushstream.PushEventSource&lt;org.eclipse.emf.ecore.EObject&gt;"
	 * @generated
	 */
	EDataType getEPushEventSource();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	PushStreamFactory getPushStreamFactory();

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
		 * The meta object literal for the '{@link org.eclipse.fennec.model.pushstream.EPushStreamProvider <em>EPush Stream Provider</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.pushstream.EPushStreamProvider
		 * @see org.eclipse.fennec.model.pushstream.impl.PushStreamPackageImpl#getEPushStreamProvider()
		 * @generated
		 */
		EClass EPUSH_STREAM_PROVIDER = eINSTANCE.getEPushStreamProvider();

		/**
		 * The meta object literal for the '<em><b>Create Push Stream</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation EPUSH_STREAM_PROVIDER___CREATE_PUSH_STREAM = eINSTANCE.getEPushStreamProvider__CreatePushStream();

		/**
		 * The meta object literal for the '<em><b>Create Simple Push Event Source</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation EPUSH_STREAM_PROVIDER___CREATE_SIMPLE_PUSH_EVENT_SOURCE = eINSTANCE.getEPushStreamProvider__CreateSimplePushEventSource();

		/**
		 * The meta object literal for the '<em><b>Create Push Stream Unbuffered</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation EPUSH_STREAM_PROVIDER___CREATE_PUSH_STREAM_UNBUFFERED = eINSTANCE.getEPushStreamProvider__CreatePushStreamUnbuffered();

		/**
		 * The meta object literal for the '<em><b>Create Push Stream Builder</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation EPUSH_STREAM_PROVIDER___CREATE_PUSH_STREAM_BUILDER = eINSTANCE.getEPushStreamProvider__CreatePushStreamBuilder();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.pushstream.impl.SimplePushStreamProviderImpl <em>Simple Push Stream Provider</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.pushstream.impl.SimplePushStreamProviderImpl
		 * @see org.eclipse.fennec.model.pushstream.impl.PushStreamPackageImpl#getSimplePushStreamProvider()
		 * @generated
		 */
		EClass SIMPLE_PUSH_STREAM_PROVIDER = eINSTANCE.getSimplePushStreamProvider();

		/**
		 * The meta object literal for the '<em><b>Provider</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SIMPLE_PUSH_STREAM_PROVIDER__PROVIDER = eINSTANCE.getSimplePushStreamProvider_Provider();

		/**
		 * The meta object literal for the '<em><b>Internal Source</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SIMPLE_PUSH_STREAM_PROVIDER__INTERNAL_SOURCE = eINSTANCE.getSimplePushStreamProvider_InternalSource();

		/**
		 * The meta object literal for the '<em><b>Event Source</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SIMPLE_PUSH_STREAM_PROVIDER__EVENT_SOURCE = eINSTANCE.getSimplePushStreamProvider_EventSource();

		/**
		 * The meta object literal for the '<em><b>Publish EObject</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation SIMPLE_PUSH_STREAM_PROVIDER___PUBLISH_EOBJECT__EOBJECT = eINSTANCE.getSimplePushStreamProvider__PublishEObject__EObject();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.pushstream.impl.CustomPushStreamProviderImpl <em>Custom Push Stream Provider</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.pushstream.impl.CustomPushStreamProviderImpl
		 * @see org.eclipse.fennec.model.pushstream.impl.PushStreamPackageImpl#getCustomPushStreamProvider()
		 * @generated
		 */
		EClass CUSTOM_PUSH_STREAM_PROVIDER = eINSTANCE.getCustomPushStreamProvider();

		/**
		 * The meta object literal for the '<em><b>Event Source</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CUSTOM_PUSH_STREAM_PROVIDER__EVENT_SOURCE = eINSTANCE.getCustomPushStreamProvider_EventSource();

		/**
		 * The meta object literal for the '<em><b>Provider</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CUSTOM_PUSH_STREAM_PROVIDER__PROVIDER = eINSTANCE.getCustomPushStreamProvider_Provider();

		/**
		 * The meta object literal for the '<em>EPush Stream</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.osgi.util.pushstream.PushStream
		 * @see org.eclipse.fennec.model.pushstream.impl.PushStreamPackageImpl#getEPushStream()
		 * @generated
		 */
		EDataType EPUSH_STREAM = eINSTANCE.getEPushStream();

		/**
		 * The meta object literal for the '<em>Provider</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.osgi.util.pushstream.PushStreamProvider
		 * @see org.eclipse.fennec.model.pushstream.impl.PushStreamPackageImpl#getPushStreamProvider()
		 * @generated
		 */
		EDataType PUSH_STREAM_PROVIDER = eINSTANCE.getPushStreamProvider();

		/**
		 * The meta object literal for the '<em>Builder</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.osgi.util.pushstream.PushStreamBuilder
		 * @see org.eclipse.fennec.model.pushstream.impl.PushStreamPackageImpl#getPushStreamBuilder()
		 * @generated
		 */
		EDataType PUSH_STREAM_BUILDER = eINSTANCE.getPushStreamBuilder();

		/**
		 * The meta object literal for the '<em>ESimple Push Event Source</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.osgi.util.pushstream.SimplePushEventSource
		 * @see org.eclipse.fennec.model.pushstream.impl.PushStreamPackageImpl#getESimplePushEventSource()
		 * @generated
		 */
		EDataType ESIMPLE_PUSH_EVENT_SOURCE = eINSTANCE.getESimplePushEventSource();

		/**
		 * The meta object literal for the '<em>EPush Event Source</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.osgi.util.pushstream.PushEventSource
		 * @see org.eclipse.fennec.model.pushstream.impl.PushStreamPackageImpl#getEPushEventSource()
		 * @generated
		 */
		EDataType EPUSH_EVENT_SOURCE = eINSTANCE.getEPushEventSource();

	}

} //PushStreamPackage
