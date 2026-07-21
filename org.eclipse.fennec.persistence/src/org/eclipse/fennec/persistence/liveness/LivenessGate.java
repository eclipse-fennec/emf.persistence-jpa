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
package org.eclipse.fennec.persistence.liveness;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.fennec.persistence.liveness.dto.GateDTO;
import org.eclipse.fennec.persistence.liveness.impl.LivenessGateRegistry;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.condition.Condition;

/**
 * Gates the registration of a connection service behind a liveness probe: the service
 * (and a {@link Condition} with id {@code fennec.liveness.<ident>}) is only registered
 * while the connection is verified.
 * <p>
 * State machine: the gate starts DOWN and probes with exponential backoff
 * ({@code retryMin..retryMax}) until the first success registers the service (UP).
 * While UP it re-probes every {@code checkInterval}; {@code failureThreshold}
 * consecutive failures unregister the service again. {@link #probeNow()} triggers an
 * immediate probe, e.g. from a driver topology listener. All probing runs on a
 * gate-owned single daemon thread — never on the caller/DS thread — so a slow probe
 * only affects its own gate.
 * <p>
 * With {@code liveness.enabled=false} the service is registered immediately and no
 * probing happens.
 *
 * @param <S> the service type being gated
 * @author Mark Hoffmann
 * @since 20.07.2026
 */
public final class LivenessGate<S> implements AutoCloseable {

	private static final Logger LOG = Logger.getLogger(LivenessGate.class.getName());
	private static final AtomicInteger THREAD_COUNTER = new AtomicInteger();

	private final BundleContext context;
	private final Class<S> serviceType;
	private final S service;
	private final String ident;
	private final String backendType;
	private final Map<String, Object> serviceProperties;
	private final ConnectionProbe probe;
	private final LivenessConfig config;
	private final ScheduledExecutorService executor;
	private final boolean executorOwned;
	private final LivenessGateRegistry registry;

	private final Object lock = new Object();
	private ServiceRegistration<S> serviceRegistration;
	private ServiceRegistration<Condition> conditionRegistration;
	private ScheduledFuture<?> pendingProbe;
	private long probeGeneration;
	private boolean opened;
	private boolean closed;
	private boolean up;
	private long backoffSeconds;
	private int consecutiveFailures;
	private long probeCount;
	private long lastSuccess;
	private long lastFailure;
	private String lastFailureMessage;

	private LivenessGate(Builder<S> builder) {
		this.context = builder.context;
		this.serviceType = builder.serviceType;
		this.service = builder.service;
		this.ident = builder.ident;
		this.backendType = builder.backendType;
		this.serviceProperties = Map.copyOf(builder.serviceProperties);
		this.probe = builder.probe;
		this.config = builder.config;
		this.registry = builder.registry;
		if (nonNull(builder.executor)) {
			this.executor = builder.executor;
			this.executorOwned = false;
		} else {
			ThreadFactory factory = runnable -> {
				Thread thread = new Thread(runnable,
						"fennec-liveness-" + builder.backendType + "-" + builder.ident
								+ "-" + THREAD_COUNTER.incrementAndGet());
				thread.setDaemon(true);
				return thread;
			};
			this.executor = Executors.newSingleThreadScheduledExecutor(factory);
			this.executorOwned = true;
		}
	}

	/**
	 * Creates a builder. The given service instance is registered under the given type
	 * whenever the gate is UP.
	 */
	public static <S> Builder<S> builder(BundleContext context, Class<S> serviceType, S service) {
		return new Builder<>(context, serviceType, service);
	}

	/**
	 * Starts the gate: adds it to the liveness runtime and schedules the initial probe
	 * (or registers immediately when probing is disabled). May be called once.
	 */
	public void open() {
		synchronized (lock) {
			if (closed) {
				throw new IllegalStateException("Gate '" + ident + "' is already closed");
			}
			if (opened) {
				return;
			}
			opened = true;
			backoffSeconds = config.retryMinSeconds();
			if (!config.enabled()) {
				register();
				up = true;
				LOG.log(Level.INFO, () -> describe() + " liveness disabled - registered without probing");
			} else {
				scheduleProbe(0);
			}
		}
		registry.add(this);
	}

	/**
	 * Triggers an immediate probe, replacing the currently scheduled one. Intended for
	 * external liveness signals (e.g. a Mongo {@code ClusterListener}) to expedite both
	 * recovery and failure detection. No-op when closed or probing is disabled.
	 */
	public void probeNow() {
		synchronized (lock) {
			if (closed || !opened || !config.enabled()) {
				return;
			}
			scheduleProbe(0);
		}
	}

	/**
	 * Stops probing, unregisters service and condition and removes the gate from the
	 * liveness runtime.
	 */
	@Override
	public void close() {
		synchronized (lock) {
			if (closed) {
				return;
			}
			closed = true;
			if (nonNull(pendingProbe)) {
				pendingProbe.cancel(false);
				pendingProbe = null;
			}
			unregister();
			up = false;
			if (executorOwned) {
				executor.shutdownNow();
			}
		}
		registry.remove(this);
	}

	/**
	 * Returns the gate identifier.
	 */
	public String getIdent() {
		return ident;
	}

	/**
	 * Returns a snapshot of the gate state for the liveness runtime.
	 */
	public GateDTO toDTO() {
		synchronized (lock) {
			GateDTO dto = new GateDTO();
			dto.ident = ident;
			dto.backendType = backendType;
			dto.state = up ? LivenessConstants.STATE_UP : LivenessConstants.STATE_DOWN;
			dto.lastSuccess = lastSuccess;
			dto.lastFailure = lastFailure;
			dto.lastFailureMessage = lastFailureMessage;
			dto.consecutiveFailures = consecutiveFailures;
			dto.probeCount = probeCount;
			dto.config = config.asMap();
			return dto;
		}
	}

	private void runProbe(long generation) {
		synchronized (lock) {
			if (closed || generation != probeGeneration) {
				return;
			}
		}
		boolean success;
		String failureMessage = null;
		try {
			probe.ping(config.checkTimeout());
			success = true;
		} catch (Exception e) {
			success = false;
			failureMessage = isNull(e.getMessage()) ? e.getClass().getName() : e.getMessage();
			if (LOG.isLoggable(Level.FINE)) {
				LOG.log(Level.FINE, e, () -> describe() + " probe failed");
			}
		}
		boolean transitioned = false;
		synchronized (lock) {
			// A newer probe was scheduled (probeNow) or the gate closed while pinging:
			// discard this result, the newer chain takes over.
			if (closed || generation != probeGeneration) {
				return;
			}
			probeCount++;
			if (success) {
				lastSuccess = System.currentTimeMillis();
				lastFailureMessage = null;
				consecutiveFailures = 0;
				backoffSeconds = config.retryMinSeconds();
				if (!up) {
					register();
					up = true;
					transitioned = true;
					LOG.log(Level.INFO, () -> describe() + " connection verified - service registered");
				}
				if (config.checkIntervalSeconds() > 0) {
					scheduleProbe(TimeUnit.SECONDS.toMillis(config.checkIntervalSeconds()));
				}
			} else {
				lastFailure = System.currentTimeMillis();
				lastFailureMessage = failureMessage;
				consecutiveFailures++;
				if (up) {
					if (consecutiveFailures >= config.failureThreshold()) {
						unregister();
						up = false;
						transitioned = true;
						String message = failureMessage;
						LOG.log(Level.INFO, () -> describe() + " connection lost ("
								+ message + ") - service unregistered");
						scheduleProbe(TimeUnit.SECONDS.toMillis(backoffSeconds));
						advanceBackoff();
					} else {
						String message = failureMessage;
						LOG.log(Level.WARNING, () -> describe() + " probe failed (" + message + "), "
								+ consecutiveFailures + "/" + config.failureThreshold()
								+ " - service stays registered");
						if (config.checkIntervalSeconds() > 0) {
							scheduleProbe(TimeUnit.SECONDS.toMillis(config.checkIntervalSeconds()));
						}
					}
				} else {
					scheduleProbe(TimeUnit.SECONDS.toMillis(backoffSeconds));
					advanceBackoff();
				}
			}
		}
		if (transitioned) {
			registry.notifyChanged();
		}
	}

	private void advanceBackoff() {
		backoffSeconds = Math.min(Math.max(1, backoffSeconds * 2), config.retryMaxSeconds());
	}

	private void scheduleProbe(long delayMillis) {
		if (nonNull(pendingProbe)) {
			pendingProbe.cancel(false);
		}
		probeGeneration++;
		long generation = probeGeneration;
		pendingProbe = executor.schedule(() -> runProbe(generation), delayMillis, TimeUnit.MILLISECONDS);
	}

	private void register() {
		Dictionary<String, Object> props = new Hashtable<>(serviceProperties);
		serviceRegistration = context.registerService(serviceType, service, props);
		Dictionary<String, Object> conditionProps = new Hashtable<>();
		conditionProps.put(Condition.CONDITION_ID, LivenessConstants.CONDITION_ID_PREFIX + ident);
		conditionRegistration = context.registerService(Condition.class, Condition.INSTANCE, conditionProps);
	}

	private void unregister() {
		if (nonNull(conditionRegistration)) {
			safeUnregister(conditionRegistration);
			conditionRegistration = null;
		}
		if (nonNull(serviceRegistration)) {
			safeUnregister(serviceRegistration);
			serviceRegistration = null;
		}
	}

	private void safeUnregister(ServiceRegistration<?> registration) {
		try {
			registration.unregister();
		} catch (IllegalStateException e) {
			// already unregistered, e.g. because the bundle is stopping
		}
	}

	private String describe() {
		return "Liveness gate [" + backendType + "/" + ident + "]";
	}

	/**
	 * Builder for {@link LivenessGate}.
	 */
	public static final class Builder<S> {

		private final BundleContext context;
		private final Class<S> serviceType;
		private final S service;
		private String ident;
		private String backendType;
		private Map<String, Object> serviceProperties = Collections.emptyMap();
		private ConnectionProbe probe;
		private LivenessConfig config = LivenessConfig.defaults();
		private ScheduledExecutorService executor;
		private LivenessGateRegistry registry = LivenessGateRegistry.getInstance();

		private Builder(BundleContext context, Class<S> serviceType, S service) {
			this.context = requireNonNull(context, "BundleContext is required");
			this.serviceType = requireNonNull(serviceType, "Service type is required");
			this.service = requireNonNull(service, "Service is required");
		}

		/** Unique gate identifier; also forms the condition id {@code fennec.liveness.<ident>}. */
		public Builder<S> ident(String ident) {
			this.ident = ident;
			return this;
		}

		/** Backend type reported in the runtime DTO, e.g. {@code "mongo"} or {@code "jdbc"}. */
		public Builder<S> backendType(String backendType) {
			this.backendType = backendType;
			return this;
		}

		/** Properties of the gated service registration. */
		public Builder<S> serviceProperties(Map<String, Object> serviceProperties) {
			this.serviceProperties = requireNonNull(serviceProperties, "Service properties must not be null");
			return this;
		}

		/** The backend-specific connectivity check. */
		public Builder<S> probe(ConnectionProbe probe) {
			this.probe = probe;
			return this;
		}

		/** The liveness configuration; defaults to {@link LivenessConfig#defaults()}. */
		public Builder<S> config(LivenessConfig config) {
			this.config = requireNonNull(config, "Config must not be null");
			return this;
		}

		/** Overrides the probe executor — intended for tests; the gate will not shut it down. */
		Builder<S> executor(ScheduledExecutorService executor) {
			this.executor = executor;
			return this;
		}

		/** Overrides the gate registry — intended for tests. */
		Builder<S> registry(LivenessGateRegistry registry) {
			this.registry = requireNonNull(registry, "Registry must not be null");
			return this;
		}

		/**
		 * Builds the gate. Call {@link LivenessGate#open()} to start it.
		 */
		public LivenessGate<S> build() {
			requireNonNull(ident, "Ident is required");
			requireNonNull(backendType, "Backend type is required");
			requireNonNull(probe, "Probe is required");
			return new LivenessGate<>(this);
		}
	}
}
