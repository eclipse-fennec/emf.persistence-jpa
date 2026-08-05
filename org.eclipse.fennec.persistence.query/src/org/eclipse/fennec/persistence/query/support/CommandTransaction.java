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

import org.osgi.annotation.versioning.ProviderType;

/**
 * A cross-command transaction bracket (issue #108): opened via
 * {@code CommandResource.begin()}, it groups every subsequent {@code execute()} call on
 * the same resource until {@link #commit()} makes them effective atomically —
 * all-or-nothing, the OData {@code $batch} atomicity-group contract
 * ([OData-Protocol] 11.7.4).
 * <p>
 * The handle is {@link AutoCloseable}: {@link #close()} without a preceding commit
 * rolls back, so a try-with-resources bracket is safe by construction. Resources are
 * not thread-safe (standard EMF semantics) — a bracket belongs to the thread driving
 * the resource, and at most one bracket may be open per resource.
 *
 * @author Mark Hoffmann
 * @since 05.08.2026
 */
@ProviderType
public interface CommandTransaction extends AutoCloseable {

	/**
	 * Makes every command executed in this bracket effective atomically.
	 *
	 * @throws IOException if the backend commit fails — the bracket is rolled back and
	 *         closed in that case
	 */
	void commit() throws IOException;

	/** Discards every command executed in this bracket; idempotent after commit/close. */
	void rollback();

	/** Rolls back if the bracket was neither committed nor rolled back. */
	@Override
	void close();
}
