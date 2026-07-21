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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.fennec.persistence.liveness.LivenessGate;

/**
 * Core-internal registry connecting {@link LivenessGate} instances with the liveness
 * runtime. Gates add/remove themselves on open/close and report state transitions;
 * the runtime component listens to bump its {@code service.changecount}. Not exported —
 * only the read-only runtime service is API.
 *
 * @author Mark Hoffmann
 * @since 20.07.2026
 */
public final class LivenessGateRegistry {

	private static final LivenessGateRegistry INSTANCE = new LivenessGateRegistry();

	private final CopyOnWriteArrayList<LivenessGate<?>> gates = new CopyOnWriteArrayList<>();
	private volatile Runnable changeListener;

	private LivenessGateRegistry() {
	}

	public static LivenessGateRegistry getInstance() {
		return INSTANCE;
	}

	/**
	 * Creates a fresh, isolated registry — for tests only.
	 */
	public static LivenessGateRegistry newInstance() {
		return new LivenessGateRegistry();
	}

	public void add(LivenessGate<?> gate) {
		gates.addIfAbsent(gate);
		notifyChanged();
	}

	public void remove(LivenessGate<?> gate) {
		if (gates.remove(gate)) {
			notifyChanged();
		}
	}

	/**
	 * Called by gates on every state transition (and on add/remove above).
	 */
	public void notifyChanged() {
		Runnable listener = changeListener;
		if (nonNull(listener)) {
			listener.run();
		}
	}

	/**
	 * Returns the currently known gates in registration order.
	 */
	public List<LivenessGate<?>> gates() {
		return List.copyOf(gates);
	}

	/**
	 * Sets or clears ({@code null}) the single change listener — the runtime component.
	 */
	public void setChangeListener(Runnable listener) {
		this.changeListener = listener;
	}
}
