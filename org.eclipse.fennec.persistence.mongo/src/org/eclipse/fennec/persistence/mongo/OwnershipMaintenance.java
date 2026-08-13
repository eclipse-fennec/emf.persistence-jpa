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
package org.eclipse.fennec.persistence.mongo;

import java.io.IOException;

/**
 * Reclaims cross-document containment children that were orphaned by an interrupted write
 * (issue #140) — the convergence backstop for deployments without multi-document
 * transactions.
 * <p>
 * A save writes the owning root first and removes what it no longer owns afterwards, so that
 * a crash in between leaves a recoverable orphan rather than a root pointing at documents
 * that no longer exist. Where {@code TRANSACTION_BRACKET} is available — MongoDB as a replica
 * set — the two steps are atomic and this window does not exist. The PostgreSQL-backed wire
 * gateways have no transactions, so there the window is real and closing it is what keeps
 * cascade-delete a guarantee rather than a best effort.
 * <p>
 * The ownership records make the orphan <em>re-derivable</em> rather than merely lost: the
 * record still names an owner that no longer references the child, or one whose document is
 * gone entirely. That is exactly what {@link #sweepOwnership()} looks for, which is why it can
 * run at any later point and converge on the same result.
 * <p>
 * Deliberately an explicit operation rather than a scheduled one. The ordinary reconciliation
 * on save already cleans up every owner it touches, so a sweep is only ever needed for owners
 * that are never saved again — a rare failure mode, and not worth the lifecycle complexity of
 * a background timer. On a transactional deployment it is harmless and finds nothing.
 * <p>
 * Obtain it by casting a {@code mongodb://} resource:
 *
 * <pre>
 * Resource resource = resourceSet.createResource(URI.createURI("mongodb://app/Library"));
 * long reclaimed = ((OwnershipMaintenance) resource).sweepOwnership();
 * </pre>
 *
 * @author Mark Hoffmann
 * @since 13.08.2026
 */
public interface OwnershipMaintenance {

	/**
	 * Deletes the cross-document containment children whose owner in this resource's
	 * collection no longer claims them, together with their ownership records.
	 * <p>
	 * Scoped to the owners of <em>this</em> resource's collection, so the cost of a run stays
	 * predictable and a sweep of one type never walks the whole database. An owner is
	 * considered to have released a child when its document no longer references it, or when
	 * the owner document is gone altogether.
	 *
	 * @return the number of child documents reclaimed
	 * @throws IOException if the sweep could not be completed; partial progress is durable,
	 *                     since a repeated run converges on the same result
	 */
	long sweepOwnership() throws IOException;
}
