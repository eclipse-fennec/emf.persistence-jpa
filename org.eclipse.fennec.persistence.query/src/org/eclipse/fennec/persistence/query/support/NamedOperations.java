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
package org.eclipse.fennec.persistence.query.support;

import java.io.IOException;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Where named operations live (issue #203) — one contract instead of a convention per
 * backend.
 * <p>
 * Every backend used to invent its own home for them: mongo a private {@code fennec.queries}
 * collection, JPA a {@code FENNEC_QUERIES} table, and the Lucene backend was about to add a
 * third. They already agreed on the payload — {@code PersistedQueries} defines the canonical
 * XMI form — so what was missing is the lookup, not the format.
 * <p>
 * An operation is a {@link org.eclipse.fennec.model.query.Query} or a
 * {@link org.eclipse.fennec.model.command.Command}, and
 * both carry the name they are stored under, which is why nothing needs a wrapper around
 * them (issue #201). The contract deliberately speaks {@link EObject}: a catalog stores what
 * it is given and hands it back, and a caller that asked for a query knows what it asked
 * for.
 * <p>
 * Deliberately free of any registry dependency. The default implementation is backed by the
 * {@code emf.osgi} EObject registry, but a store-native catalog is an equally valid provider
 * — mongo's collection has the genuine charm that the catalog travels with the database.
 *
 * @author Mark Hoffmann
 * @since 22.08.2026
 */
@ProviderType
public interface NamedOperations {

	/** Service property naming the catalog, so a resource can be pointed at a specific one. */
	String CATALOG_NAME = "persistence.catalog.name";

	/**
	 * Looks up the operation stored under {@code name}.
	 *
	 * @param name the name it was stored under, must not be {@code null}
	 * @return the operation, or {@link Optional#empty()} when this catalog holds none — an
	 *         empty answer is not an error, since a caller may consult several catalogs
	 * @throws IOException when the catalog itself could not be read
	 */
	Optional<EObject> lookup(String name) throws IOException;

	/**
	 * Stores {@code operation} under {@code name}, replacing whatever was there — last write
	 * wins, as the saveQuery contract has it.
	 *
	 * @param name the name to store under, must not be {@code null}
	 * @param operation the query or command to store, must not be {@code null}
	 * @throws IOException when the catalog could not be written
	 */
	void store(String name, EObject operation) throws IOException;

	/**
	 * Removes whatever is stored under {@code name}; removing an absent name is not an error.
	 *
	 * @param name the name to remove, must not be {@code null}
	 * @throws IOException when the catalog could not be written
	 */
	void remove(String name) throws IOException;
}
