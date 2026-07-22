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
package org.eclipse.fennec.model.pushstream.impl;

import java.lang.reflect.InvocationTargetException;

import java.util.concurrent.BlockingQueue;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.model.pushstream.PushStreamPackage;
import org.eclipse.fennec.model.pushstream.SimplePushStreamProvider;

import org.osgi.util.pushstream.PushEvent;
import org.osgi.util.pushstream.PushEventSource;
import org.osgi.util.pushstream.PushStream;
import org.osgi.util.pushstream.PushStreamBuilder;
import org.osgi.util.pushstream.PushStreamProvider;
import org.osgi.util.pushstream.SimplePushEventSource;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Simple Push Stream Provider</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.pushstream.impl.SimplePushStreamProviderImpl#getProvider <em>Provider</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.pushstream.impl.SimplePushStreamProviderImpl#getInternalSource <em>Internal Source</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.pushstream.impl.SimplePushStreamProviderImpl#getEventSource <em>Event Source</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SimplePushStreamProviderImpl extends MinimalEObjectImpl.Container implements SimplePushStreamProvider {
	/**
	 * The default value of the '{@link #getProvider() <em>Provider</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProvider()
	 * @generated
	 * @ordered
	 */
	protected static final PushStreamProvider PROVIDER_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getProvider() <em>Provider</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProvider()
	 * @generated
	 * @ordered
	 */
	protected PushStreamProvider provider = PROVIDER_EDEFAULT;

	/**
	 * The cached value of the '{@link #getInternalSource() <em>Internal Source</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInternalSource()
	 * @generated
	 * @ordered
	 */
	protected SimplePushEventSource<EObject> internalSource;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SimplePushStreamProviderImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PushStreamPackage.Literals.SIMPLE_PUSH_STREAM_PROVIDER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PushStreamProvider getProvider() {
		return provider;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setProvider(PushStreamProvider newProvider) {
		PushStreamProvider oldProvider = provider;
		provider = newProvider;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PushStreamPackage.SIMPLE_PUSH_STREAM_PROVIDER__PROVIDER, oldProvider, provider));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SimplePushEventSource<EObject> getInternalSource() {
		return internalSource;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SimplePushEventSource<EObject> getEventSource() {
		if (internalSource == null) {
			internalSource = createSimplePushEventSource();
		}
		return internalSource;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void publishEObject(final EObject eObject) {
		SimplePushEventSource<EObject> es = getEventSource();
		if (eObject != null && es != null) {
			es.publish(eObject);
		};
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PushStream<EObject> createPushStream() {
		PushStreamProvider psp = getProvider();
		if (psp == null) {
			throw new IllegalArgumentException("PushStreamProvider must not be null to build a PushStream");
		}
		PushEventSource<EObject> es = getEventSource();
		if (es == null) {
			throw new IllegalArgumentException("SimpleEventSource must not be null to build a PushStream");
		}
		return psp.buildStream(es).build();
		
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SimplePushEventSource<EObject> createSimplePushEventSource() {
		PushStreamProvider psp = getProvider();
		if (psp == null) {
			throw new IllegalArgumentException("PushStreamProvider must not be null to create a SimplePushEventSource");
		}
		return psp.buildSimpleEventSource(EObject.class).build();
		
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PushStream<EObject> createPushStreamUnbuffered() {
		PushStreamProvider psp = getProvider();
		if (psp == null) {
			throw new IllegalArgumentException("PushStreamProvider must not be null to build a PushStream");
		}
		PushEventSource<EObject> es = getEventSource();
		if (es == null) {
			throw new IllegalArgumentException("SimpleEventSource must not be null to build a PushStream");
		}
		return psp.buildStream(es).unbuffered().build();
		
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PushStreamBuilder<EObject, BlockingQueue<PushEvent<? extends EObject>>> createPushStreamBuilder() {
		PushStreamProvider psp = getProvider();
		if (psp == null) {
			throw new IllegalArgumentException("PushStreamProvider must not be null to build a PushStream");
		}
		PushEventSource<EObject> es = getEventSource();
		if (es == null) {
			throw new IllegalArgumentException("SimpleEventSource must not be null to build a PushStream");
		}
		return psp.buildStream(es);
		
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case PushStreamPackage.SIMPLE_PUSH_STREAM_PROVIDER__PROVIDER:
				return getProvider();
			case PushStreamPackage.SIMPLE_PUSH_STREAM_PROVIDER__INTERNAL_SOURCE:
				return getInternalSource();
			case PushStreamPackage.SIMPLE_PUSH_STREAM_PROVIDER__EVENT_SOURCE:
				return getEventSource();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case PushStreamPackage.SIMPLE_PUSH_STREAM_PROVIDER__PROVIDER:
				setProvider((PushStreamProvider)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case PushStreamPackage.SIMPLE_PUSH_STREAM_PROVIDER__PROVIDER:
				setProvider(PROVIDER_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case PushStreamPackage.SIMPLE_PUSH_STREAM_PROVIDER__PROVIDER:
				return PROVIDER_EDEFAULT == null ? provider != null : !PROVIDER_EDEFAULT.equals(provider);
			case PushStreamPackage.SIMPLE_PUSH_STREAM_PROVIDER__INTERNAL_SOURCE:
				return internalSource != null;
			case PushStreamPackage.SIMPLE_PUSH_STREAM_PROVIDER__EVENT_SOURCE:
				return getEventSource() != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		switch (operationID) {
			case PushStreamPackage.SIMPLE_PUSH_STREAM_PROVIDER___PUBLISH_EOBJECT__EOBJECT:
				publishEObject((EObject)arguments.get(0));
				return null;
			case PushStreamPackage.SIMPLE_PUSH_STREAM_PROVIDER___CREATE_PUSH_STREAM:
				return createPushStream();
			case PushStreamPackage.SIMPLE_PUSH_STREAM_PROVIDER___CREATE_SIMPLE_PUSH_EVENT_SOURCE:
				return createSimplePushEventSource();
			case PushStreamPackage.SIMPLE_PUSH_STREAM_PROVIDER___CREATE_PUSH_STREAM_UNBUFFERED:
				return createPushStreamUnbuffered();
			case PushStreamPackage.SIMPLE_PUSH_STREAM_PROVIDER___CREATE_PUSH_STREAM_BUILDER:
				return createPushStreamBuilder();
		}
		return super.eInvoke(operationID, arguments);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (provider: ");
		result.append(provider);
		result.append(", internalSource: ");
		result.append(internalSource);
		result.append(')');
		return result.toString();
	}

} //SimplePushStreamProviderImpl
