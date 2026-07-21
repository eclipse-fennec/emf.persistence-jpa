/********************************************************************
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
 ********************************************************************/
package org.eclipse.fennec.persistence.liveness.impl;

import static java.util.Objects.nonNull;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.fennec.persistence.liveness.LivenessGate;
import org.eclipse.fennec.persistence.liveness.PersistenceLivenessRuntime;
import org.eclipse.fennec.persistence.liveness.dto.GateDTO;
import org.eclipse.fennec.persistence.liveness.dto.LivenessRuntimeDTO;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import aQute.bnd.annotation.service.ServiceCapability;

/**
 * Always-on singleton implementation of {@link PersistenceLivenessRuntime}. The service
 * is registered manually so its {@code service.changecount} property can be bumped on
 * every gate state transition reported through the {@link LivenessGateRegistry}.
 *
 * @author Mark Hoffmann
 * @since 20.07.2026
 */
// service = {}: the service is registered manually (with service.changecount), DS must
// not additionally register this component under its implemented interface
@ServiceCapability(PersistenceLivenessRuntime.class)
@Component(immediate = true, service = {})
public class PersistenceLivenessRuntimeComponent implements PersistenceLivenessRuntime {

	private final LivenessGateRegistry registry;
	private final AtomicLong changeCount = new AtomicLong();
	private volatile ServiceRegistration<PersistenceLivenessRuntime> registration;

	@Activate
	public PersistenceLivenessRuntimeComponent(BundleContext context) {
		this(context, LivenessGateRegistry.getInstance());
	}

	/**
	 * Visible for tests — allows using an isolated registry.
	 */
	public PersistenceLivenessRuntimeComponent(BundleContext context, LivenessGateRegistry registry) {
		this.registry = registry;
		registration = context.registerService(PersistenceLivenessRuntime.class, this,
				changeCountProperties());
		registry.setChangeListener(this::changed);
	}

	@Deactivate
	public void deactivate() {
		registry.setChangeListener(null);
		if (nonNull(registration)) {
			try {
				registration.unregister();
			} catch (IllegalStateException e) {
				// already unregistered, e.g. because the bundle is stopping
			}
			registration = null;
		}
	}

	@Override
	public LivenessRuntimeDTO getRuntimeDTO() {
		List<LivenessGate<?>> gates = registry.gates();
		LivenessRuntimeDTO dto = new LivenessRuntimeDTO();
		dto.gates = gates.stream().map(LivenessGate::toDTO).toArray(GateDTO[]::new);
		return dto;
	}

	private void changed() {
		changeCount.incrementAndGet();
		ServiceRegistration<PersistenceLivenessRuntime> reg = registration;
		if (nonNull(reg)) {
			try {
				reg.setProperties(changeCountProperties());
			} catch (IllegalStateException e) {
				// already unregistered, e.g. because the bundle is stopping
			}
		}
	}

	private Dictionary<String, Object> changeCountProperties() {
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(Constants.SERVICE_CHANGECOUNT, changeCount.get());
		return properties;
	}
}
