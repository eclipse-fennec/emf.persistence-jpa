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
package org.eclipse.fennec.persistence.eclipselink.spi;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.sessions.server.Server;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * {@link JPAUnit} that owns a heavyweight EclipseLink {@link EntityManagerFactory} and
 * manages it lazily (issue #20):
 * <ul>
 * <li>the real factory is built on first {@link #lease()} (deferring the expensive
 *     EclipseLink deploy/DDL until a URI actually hits the unit);</li>
 * <li>active leases are counted; the factory stays alive while any lease is open;</li>
 * <li>when the last lease closes an idle timer is armed — on expiry the factory is closed
 *     (releasing session/cache/connection pool);</li>
 * <li>the next lease transparently rebuilds it.</li>
 * </ul>
 * The idle timeout is a single knob:
 * <ul>
 * <li>{@code > 0} — close after that many milliseconds of no open lease (avoids rebuild
 *     churn under bursty load);</li>
 * <li>{@code 0} — close immediately when the last lease closes;</li>
 * <li>{@code < 0} — never auto-close; only {@link #dispose()} closes the factory.</li>
 * </ul>
 * Never closes the factory while a lease is open (the timer only fires at zero leases).
 *
 * @author Mark Hoffmann
 * @since 20.07.2026
 */
public final class LazyJPAUnit implements JPAUnit {

	private static final Logger LOG = Logger.getLogger(LazyJPAUnit.class.getName());

	private final String name;
	private final Supplier<EntityManagerFactory> factoryBuilder;
	private final long idleTimeoutMillis;
	private final ScheduledExecutorService scheduler;
	private final boolean ownScheduler;

	private EntityManagerFactory emf;
	private int activeLeases;
	private ScheduledFuture<?> pendingClose;
	private boolean disposed;

	/**
	 * Creates a unit with a private daemon scheduler.
	 *
	 * @param name the persistence-unit name (for logging)
	 * @param factoryBuilder builds a fresh real EMF on demand
	 * @param idleTimeoutMillis {@code >0} idle delay, {@code 0} immediate, {@code <0} never
	 */
	public LazyJPAUnit(String name, Supplier<EntityManagerFactory> factoryBuilder, long idleTimeoutMillis) {
		this(name, factoryBuilder, idleTimeoutMillis, defaultScheduler(name), true);
	}

	/**
	 * Creates a unit with a caller-supplied scheduler (not shut down on {@link #dispose()}).
	 * Intended for tests that need a controllable scheduler.
	 */
	public LazyJPAUnit(String name, Supplier<EntityManagerFactory> factoryBuilder, long idleTimeoutMillis,
			ScheduledExecutorService scheduler) {
		this(name, factoryBuilder, idleTimeoutMillis, scheduler, false);
	}

	private LazyJPAUnit(String name, Supplier<EntityManagerFactory> factoryBuilder, long idleTimeoutMillis,
			ScheduledExecutorService scheduler, boolean ownScheduler) {
		this.name = isNull(name) ? "<unnamed>" : name;
		this.factoryBuilder = requireNonNull(factoryBuilder, "factory builder is required");
		this.idleTimeoutMillis = idleTimeoutMillis;
		this.scheduler = requireNonNull(scheduler, "scheduler is required");
		this.ownScheduler = ownScheduler;
	}

	private static ScheduledExecutorService defaultScheduler(String name) {
		ThreadFactory tf = r -> {
			Thread t = new Thread(r, "jpa-unit-idle-" + name);
			t.setDaemon(true);
			return t;
		};
		return Executors.newSingleThreadScheduledExecutor(tf);
	}

	@Override
	public synchronized Lease lease() {
		if (disposed) {
			throw new IllegalStateException("JPAUnit '" + name + "' has been disposed");
		}
		EntityManagerFactory active = acquire();
		return new Lease() {
			private boolean closed;

			@Override
			public EntityManager createEntityManager() {
				return active.createEntityManager();
			}

			@Override
			public Server getServerSession() {
				return JpaHelper.getServerSession(active);
			}

			@Override
			public EntityManagerFactory getEntityManagerFactory() {
				return active;
			}

			@Override
			public void close() {
				if (!closed) {
					closed = true;
					release();
				}
			}
		};
	}

	private synchronized EntityManagerFactory acquire() {
		cancelPendingClose();
		if (isNull(emf) || !emf.isOpen()) {
			LOG.log(Level.FINE, "Building EntityManagerFactory for unit ''{0}''", name);
			emf = requireNonNull(factoryBuilder.get(), "factory builder returned null");
		}
		activeLeases++;
		return emf;
	}

	private synchronized void release() {
		if (activeLeases > 0) {
			activeLeases--;
		}
		if (activeLeases == 0) {
			scheduleClose();
		}
	}

	/** Must be called while holding the monitor and with {@code activeLeases == 0}. */
	private void scheduleClose() {
		cancelPendingClose();
		if (idleTimeoutMillis < 0 || disposed) {
			return; // never auto-close (or already disposing)
		}
		if (idleTimeoutMillis == 0) {
			closeFactory();
			return;
		}
		pendingClose = scheduler.schedule(this::idleClose, idleTimeoutMillis, TimeUnit.MILLISECONDS);
	}

	private synchronized void idleClose() {
		pendingClose = null;
		if (activeLeases == 0) {
			closeFactory();
		}
	}

	private void cancelPendingClose() {
		if (nonNull(pendingClose)) {
			pendingClose.cancel(false);
			pendingClose = null;
		}
	}

	/** Must be called while holding the monitor. */
	private void closeFactory() {
		if (nonNull(emf)) {
			EntityManagerFactory toClose = emf;
			emf = null;
			try {
				if (toClose.isOpen()) {
					toClose.close();
				}
			} catch (RuntimeException e) {
				LOG.log(Level.WARNING, e, () -> "Failed to close EntityManagerFactory for unit '" + name + "'");
			}
		}
	}

	@Override
	public synchronized void dispose() {
		disposed = true;
		cancelPendingClose();
		closeFactory();
		if (ownScheduler) {
			scheduler.shutdownNow();
		}
	}

	/** Test/introspection hook: whether the real EMF is currently built and open. */
	synchronized boolean isFactoryOpen() {
		return nonNull(emf) && emf.isOpen();
	}

	/** Test/introspection hook: current number of open leases. */
	synchronized int activeLeaseCount() {
		return activeLeases;
	}
}
