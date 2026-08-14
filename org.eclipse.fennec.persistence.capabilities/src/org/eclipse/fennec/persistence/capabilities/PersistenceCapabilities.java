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

import org.osgi.annotation.versioning.ProviderType;

/**
 * Everything a backend declares, in one place (issue #134, contract §5a) — the root a backend
 * answers, with query, command and store as views on it.
 * <p>
 * It exists because the previous arrangement had no such root: capabilities hung off query roles
 * ({@code QueryProcessor}, {@code CommandResource}), so a statement about the save path could
 * only be reached by holding a command role, and the parts with no query role at all had nowhere
 * to declare.
 * <p>
 * Read twice, for two kinds of truth. As a <b>declaration</b> it says what a backend and flavor
 * can do at all, is registered per {@code backend} × {@code flavor} and can be read without
 * opening a connection. As the answer of a live resource it is the <b>effective</b> set, where a
 * probe has narrowed the declaration to what this deployment actually serves — never widened it.
 *
 * @author Mark Hoffmann
 * @since 14.08.2026
 */
@ProviderType
public interface PersistenceCapabilities {

	/**
	 * @return the query expression vocabulary this backend translates natively
	 */
	QueryCapabilities query();

	/**
	 * @return the write commands this backend serves
	 */
	CommandCapabilities command();

	/**
	 * @return the store-dependent power outside query and command
	 */
	StoreCapabilities store();

	/**
	 * Assembles a declaration from its three views.
	 *
	 * @param query the query vocabulary
	 * @param command the command verbs
	 * @param store the store features
	 * @return the immutable aggregate
	 */
	static PersistenceCapabilities of(QueryCapabilities query, CommandCapabilities command,
			StoreCapabilities store) {
		return new ImmutablePersistenceCapabilities(query, command, store);
	}
}
