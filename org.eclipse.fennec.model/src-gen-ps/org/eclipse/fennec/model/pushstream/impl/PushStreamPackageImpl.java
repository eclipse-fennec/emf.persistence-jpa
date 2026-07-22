/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 * 
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 */
package org.eclipse.fennec.model.pushstream.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.fennec.model.pushstream.CustomPushStreamProvider;
import org.eclipse.fennec.model.pushstream.EPushStreamProvider;
import org.eclipse.fennec.model.pushstream.PushStreamFactory;
import org.eclipse.fennec.model.pushstream.PushStreamPackage;
import org.eclipse.fennec.model.pushstream.SimplePushStreamProvider;

import org.osgi.util.pushstream.PushEventSource;
import org.osgi.util.pushstream.PushStream;
import org.osgi.util.pushstream.PushStreamBuilder;
import org.osgi.util.pushstream.PushStreamProvider;
import org.osgi.util.pushstream.SimplePushEventSource;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class PushStreamPackageImpl extends EPackageImpl implements PushStreamPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass ePushStreamProviderEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass simplePushStreamProviderEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass customPushStreamProviderEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType ePushStreamEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType pushStreamProviderEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType pushStreamBuilderEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType eSimplePushEventSourceEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType ePushEventSourceEDataType = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.fennec.model.pushstream.PushStreamPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private PushStreamPackageImpl() {
		super(eNS_URI, PushStreamFactory.eINSTANCE);
	}
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link PushStreamPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static PushStreamPackage init() {
		if (isInited) return (PushStreamPackage)EPackage.Registry.INSTANCE.getEPackage(PushStreamPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredPushStreamPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		PushStreamPackageImpl thePushStreamPackage = registeredPushStreamPackage instanceof PushStreamPackageImpl ? (PushStreamPackageImpl)registeredPushStreamPackage : new PushStreamPackageImpl();

		isInited = true;

		// Create package meta-data objects
		thePushStreamPackage.createPackageContents();

		// Initialize created meta-data
		thePushStreamPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		thePushStreamPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(PushStreamPackage.eNS_URI, thePushStreamPackage);
		return thePushStreamPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEPushStreamProvider() {
		return ePushStreamProviderEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getEPushStreamProvider__CreatePushStream() {
		return ePushStreamProviderEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getEPushStreamProvider__CreateSimplePushEventSource() {
		return ePushStreamProviderEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getEPushStreamProvider__CreatePushStreamUnbuffered() {
		return ePushStreamProviderEClass.getEOperations().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getEPushStreamProvider__CreatePushStreamBuilder() {
		return ePushStreamProviderEClass.getEOperations().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSimplePushStreamProvider() {
		return simplePushStreamProviderEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSimplePushStreamProvider_Provider() {
		return (EAttribute)simplePushStreamProviderEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSimplePushStreamProvider_InternalSource() {
		return (EAttribute)simplePushStreamProviderEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSimplePushStreamProvider_EventSource() {
		return (EAttribute)simplePushStreamProviderEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getSimplePushStreamProvider__PublishEObject__EObject() {
		return simplePushStreamProviderEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCustomPushStreamProvider() {
		return customPushStreamProviderEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCustomPushStreamProvider_EventSource() {
		return (EAttribute)customPushStreamProviderEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCustomPushStreamProvider_Provider() {
		return (EAttribute)customPushStreamProviderEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getEPushStream() {
		return ePushStreamEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getPushStreamProvider() {
		return pushStreamProviderEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getPushStreamBuilder() {
		return pushStreamBuilderEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getESimplePushEventSource() {
		return eSimplePushEventSourceEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getEPushEventSource() {
		return ePushEventSourceEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PushStreamFactory getPushStreamFactory() {
		return (PushStreamFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		ePushStreamProviderEClass = createEClass(EPUSH_STREAM_PROVIDER);
		createEOperation(ePushStreamProviderEClass, EPUSH_STREAM_PROVIDER___CREATE_PUSH_STREAM);
		createEOperation(ePushStreamProviderEClass, EPUSH_STREAM_PROVIDER___CREATE_SIMPLE_PUSH_EVENT_SOURCE);
		createEOperation(ePushStreamProviderEClass, EPUSH_STREAM_PROVIDER___CREATE_PUSH_STREAM_UNBUFFERED);
		createEOperation(ePushStreamProviderEClass, EPUSH_STREAM_PROVIDER___CREATE_PUSH_STREAM_BUILDER);

		simplePushStreamProviderEClass = createEClass(SIMPLE_PUSH_STREAM_PROVIDER);
		createEAttribute(simplePushStreamProviderEClass, SIMPLE_PUSH_STREAM_PROVIDER__PROVIDER);
		createEAttribute(simplePushStreamProviderEClass, SIMPLE_PUSH_STREAM_PROVIDER__INTERNAL_SOURCE);
		createEAttribute(simplePushStreamProviderEClass, SIMPLE_PUSH_STREAM_PROVIDER__EVENT_SOURCE);
		createEOperation(simplePushStreamProviderEClass, SIMPLE_PUSH_STREAM_PROVIDER___PUBLISH_EOBJECT__EOBJECT);

		customPushStreamProviderEClass = createEClass(CUSTOM_PUSH_STREAM_PROVIDER);
		createEAttribute(customPushStreamProviderEClass, CUSTOM_PUSH_STREAM_PROVIDER__EVENT_SOURCE);
		createEAttribute(customPushStreamProviderEClass, CUSTOM_PUSH_STREAM_PROVIDER__PROVIDER);

		// Create data types
		ePushStreamEDataType = createEDataType(EPUSH_STREAM);
		pushStreamProviderEDataType = createEDataType(PUSH_STREAM_PROVIDER);
		pushStreamBuilderEDataType = createEDataType(PUSH_STREAM_BUILDER);
		eSimplePushEventSourceEDataType = createEDataType(ESIMPLE_PUSH_EVENT_SOURCE);
		ePushEventSourceEDataType = createEDataType(EPUSH_EVENT_SOURCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		simplePushStreamProviderEClass.getESuperTypes().add(this.getEPushStreamProvider());
		customPushStreamProviderEClass.getESuperTypes().add(this.getEPushStreamProvider());

		// Initialize classes, features, and operations; add parameters
		initEClass(ePushStreamProviderEClass, EPushStreamProvider.class, "EPushStreamProvider", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEOperation(getEPushStreamProvider__CreatePushStream(), this.getEPushStream(), "createPushStream", 1, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getEPushStreamProvider__CreateSimplePushEventSource(), this.getESimplePushEventSource(), "createSimplePushEventSource", 1, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getEPushStreamProvider__CreatePushStreamUnbuffered(), this.getEPushStream(), "createPushStreamUnbuffered", 1, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getEPushStreamProvider__CreatePushStreamBuilder(), this.getPushStreamBuilder(), "createPushStreamBuilder", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(simplePushStreamProviderEClass, SimplePushStreamProvider.class, "SimplePushStreamProvider", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSimplePushStreamProvider_Provider(), this.getPushStreamProvider(), "provider", null, 1, 1, SimplePushStreamProvider.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSimplePushStreamProvider_InternalSource(), this.getESimplePushEventSource(), "internalSource", null, 0, 1, SimplePushStreamProvider.class, IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getSimplePushStreamProvider_EventSource(), this.getESimplePushEventSource(), "eventSource", null, 1, 1, SimplePushStreamProvider.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		EOperation op = initEOperation(getSimplePushStreamProvider__PublishEObject__EObject(), null, "publishEObject", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "eObject", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(customPushStreamProviderEClass, CustomPushStreamProvider.class, "CustomPushStreamProvider", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getCustomPushStreamProvider_EventSource(), this.getEPushEventSource(), "eventSource", null, 1, 1, CustomPushStreamProvider.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCustomPushStreamProvider_Provider(), this.getPushStreamProvider(), "provider", null, 1, 1, CustomPushStreamProvider.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize data types
		initEDataType(ePushStreamEDataType, PushStream.class, "EPushStream", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS, "org.osgi.util.pushstream.PushStream<org.eclipse.emf.ecore.EObject>");
		initEDataType(pushStreamProviderEDataType, PushStreamProvider.class, "PushStreamProvider", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(pushStreamBuilderEDataType, PushStreamBuilder.class, "PushStreamBuilder", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS, "org.osgi.util.pushstream.PushStreamBuilder<org.eclipse.emf.ecore.EObject, java.util.concurrent.BlockingQueue<org.osgi.util.pushstream.PushEvent<? extends org.eclipse.emf.ecore.EObject>>>");
		initEDataType(eSimplePushEventSourceEDataType, SimplePushEventSource.class, "ESimplePushEventSource", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS, "org.osgi.util.pushstream.SimplePushEventSource<org.eclipse.emf.ecore.EObject>");
		initEDataType(ePushEventSourceEDataType, PushEventSource.class, "EPushEventSource", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS, "org.osgi.util.pushstream.PushEventSource<org.eclipse.emf.ecore.EObject>");

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// Version
		createVersionAnnotations();
	}

	/**
	 * Initializes the annotations for <b>Version</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createVersionAnnotations() {
		String source = "Version";
		addAnnotation
		  (this,
		   source,
		   new String[] {
			   "value", "1.2"
		   });
	}

} //PushStreamPackageImpl
