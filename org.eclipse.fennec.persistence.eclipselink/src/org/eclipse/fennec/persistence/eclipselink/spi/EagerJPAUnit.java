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

import static java.util.Objects.requireNonNull;

import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.sessions.server.Server;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * {@link JPAUnit} adapter over a pre-existing {@link EntityManagerFactory}. The factory is
 * used directly and is <em>not owned</em> — {@link #dispose()} does not close it (the caller
 * that created the EMF owns its lifecycle). Used on the non-OSGi / test path.
 *
 * @author Mark Hoffmann
 * @since 20.07.2026
 */
final class EagerJPAUnit implements JPAUnit {

	private final EntityManagerFactory emf;

	EagerJPAUnit(EntityManagerFactory emf) {
		this.emf = requireNonNull(emf, "EntityManagerFactory is required");
	}

	@Override
	public Lease lease() {
		return new Lease() {
			@Override
			public EntityManager createEntityManager() {
				return emf.createEntityManager();
			}

			@Override
			public Server getServerSession() {
				return JpaHelper.getServerSession(emf);
			}

			@Override
			public EntityManagerFactory getEntityManagerFactory() {
				return emf;
			}

			@Override
			public void close() {
				// nothing to release — the factory is externally owned
			}
		};
	}
}
