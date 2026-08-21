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
package org.eclipse.fennec.persistence.capabilities;

import static java.util.Objects.requireNonNull;

import org.osgi.annotation.versioning.ProviderType;

/**
 * What one <b>backend × flavor</b> declares it can do (issue #172).
 * <p>
 * The capability vocabulary is closed and the same everywhere; what differs is who serves
 * which part of it, and that answer is not per backend but per <em>flavor</em>: PostgreSQL
 * can serve fuzzy matching where H2 cannot, FerretDB serves every query MongoDB does but no
 * multi-document transaction. A declaration therefore names both axes and answers with the
 * full {@link PersistenceCapabilities} — query, command and store in one place, rather than
 * a query set here and a hand-assembled command set in whoever consumes it.
 * <p>
 * Plain Java on purpose. The declarations are consumed by the TCK, which runs as ordinary
 * JUnit outside any framework, as well as by OSGi components; an implementation may
 * additionally be registered as a service carrying {@link #BACKEND_PROPERTY} and
 * {@link #FLAVOR_PROPERTY} so consumers can select one by filter.
 *
 * @author Mark Hoffmann
 * @since 21.08.2026
 */
@ProviderType
public interface CapabilityDeclaration {

	/** Service property naming the backend of a registered declaration. */
	String BACKEND_PROPERTY = "persistence.backend";

	/** Service property naming the flavor of a registered declaration. */
	String FLAVOR_PROPERTY = "persistence.flavor";

	/**
	 * @return the backend id this declaration describes ({@code jpa}, {@code mongo}, …),
	 *         matching the {@code persistence.query.backend} property of the corresponding
	 *         query processor
	 */
	String backend();

	/**
	 * @return the flavor id within that backend ({@code h2}, {@code postgres},
	 *         {@code ferretdb}, …) — never {@code null}, since every deployment is some
	 *         concrete flavor even when a backend has only one
	 */
	String flavor();

	/**
	 * @return what this backend and flavor serve, never {@code null}
	 */
	PersistenceCapabilities capabilities();

	/**
	 * Creates a declaration from its three parts.
	 *
	 * @param backend the backend id, must not be {@code null}
	 * @param flavor the flavor id, must not be {@code null}
	 * @param capabilities what the pair serves, must not be {@code null}
	 * @return the declaration
	 */
	static CapabilityDeclaration of(String backend, String flavor, PersistenceCapabilities capabilities) {
		requireNonNull(backend, "backend must not be null");
		requireNonNull(flavor, "flavor must not be null");
		requireNonNull(capabilities, "capabilities must not be null");
		return new CapabilityDeclaration() {

			@Override
			public String backend() {
				return backend;
			}

			@Override
			public String flavor() {
				return flavor;
			}

			@Override
			public PersistenceCapabilities capabilities() {
				return capabilities;
			}

			@Override
			public String toString() {
				return "CapabilityDeclaration[" + backend + " × " + flavor + "]";
			}
		};
	}
}
