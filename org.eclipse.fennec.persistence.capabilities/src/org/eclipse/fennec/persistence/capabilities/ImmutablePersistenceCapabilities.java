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

/**
 * The aggregate handed out by {@link PersistenceCapabilities#of}.
 * <p>
 * The three views are required rather than nullable: a backend that serves no commands declares
 * an empty {@link CommandCapabilities}, which is a statement. {@code null} would be the absence
 * of one, and a caller cannot tell the two apart.
 *
 * @author Mark Hoffmann
 * @since 14.08.2026
 */
record ImmutablePersistenceCapabilities(QueryCapabilities query, CommandCapabilities command,
		StoreCapabilities store) implements PersistenceCapabilities {

	ImmutablePersistenceCapabilities {
		requireNonNull(query, "query capabilities");
		requireNonNull(command, "command capabilities");
		requireNonNull(store, "store capabilities");
	}
}
