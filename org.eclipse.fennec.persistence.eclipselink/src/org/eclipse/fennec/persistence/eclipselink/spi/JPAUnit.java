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

import org.eclipse.persistence.sessions.server.Server;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Narrow capability a {@code jpa://} resource needs from a persistence unit — deliberately
 * <em>not</em> the full {@link EntityManagerFactory}. A resource only ever needs to
 * <ul>
 * <li>create a fresh {@link EntityManager} per operation, and</li>
 * <li>read the EclipseLink {@link Server} session (descriptors / alias lookup), which is a
 *     thread-safe read.</li>
 * </ul>
 * Exposing this instead of the raw EMF lets an implementation keep the heavyweight
 * EclipseLink factory private and lazily managed (build on first use, close on idle,
 * recreate on demand) — see issue #20. It also avoids the {@code JpaHelper.getServerSession}
 * cast problem: the unit hands out the server session itself rather than a wrapper EMF.
 * <p>
 * Every use goes through a {@link Lease}: the real factory is guaranteed alive for the
 * duration of the lease, and the unit's idle bookkeeping reflects real activity. Callers
 * must {@link Lease#close() close} the lease — one lease per resource operation.
 *
 * @author Mark Hoffmann
 * @since 20.07.2026
 */
public interface JPAUnit {

	/**
	 * Service property carrying the persistence-unit name a {@code JPAUnit} service is
	 * registered with — the same key the OSGi JPA service specification uses for
	 * {@link EntityManagerFactory} services. The URI authority of a {@code jpa://} URI is
	 * matched against this property.
	 */
	String UNIT_NAME = "osgi.unit.name";

	/**
	 * Borrows the unit for one resource operation. The returned lease guarantees a live
	 * EclipseLink factory for its whole duration and must be closed when the operation ends.
	 *
	 * @return a lease; never {@code null}
	 */
	Lease lease();

	/**
	 * Releases all resources held by the unit (closes the underlying factory, stops any
	 * idle scheduler). Called when the owning persistence-unit configuration goes away.
	 */
	default void dispose() {
		// no-op for non-owning adapters
	}

	/**
	 * A borrowed handle onto a live EclipseLink factory. {@link AutoCloseable} so callers
	 * use it in try-with-resources; {@link #close()} is idempotent and does not throw.
	 */
	interface Lease extends AutoCloseable {

		/** @return a fresh, non-shared {@link EntityManager}. */
		EntityManager createEntityManager();

		/**
		 * @return the EclipseLink {@link Server} session for descriptor/alias lookup, or
		 *         {@code null} when the factory is not EclipseLink-backed.
		 */
		Server getServerSession();

		/**
		 * Returns the live underlying {@link EntityManagerFactory} — an interop escape
		 * hatch for consumers that need the real EclipseLink factory (e.g. the
		 * {@code EntityManagerFactory} whiteboard service). The instance is only
		 * guaranteed alive for the duration of this lease; callers must neither close
		 * nor cache it beyond the lease.
		 *
		 * @return the live underlying factory
		 */
		EntityManagerFactory getEntityManagerFactory();

		@Override
		void close();
	}

	/**
	 * Adapts an existing {@link EntityManagerFactory} as a {@code JPAUnit} without any lazy
	 * or idle management — the factory is used as-is and is <em>not</em> owned (never closed
	 * by the unit). This is the non-OSGi / test path where the caller manages the EMF
	 * lifecycle.
	 *
	 * @param emf the factory to adapt
	 * @return an eager, non-owning unit
	 */
	static JPAUnit of(EntityManagerFactory emf) {
		return new EagerJPAUnit(emf);
	}

	/**
	 * Returns a unit whose {@link #lease()} always fails with the given reason. Used by the
	 * whiteboard resource factory when a {@code jpa://} URI names a persistence unit for
	 * which no {@code JPAUnit} service is available — the resource is still created, and
	 * the failure surfaces as a clear diagnostic on {@code load}/{@code getEObject}.
	 *
	 * @param reason the message of the {@link IllegalStateException} thrown on lease
	 * @return a unit that cannot be leased
	 */
	static JPAUnit unavailable(String reason) {
		return () -> {
			throw new IllegalStateException(reason);
		};
	}
}
