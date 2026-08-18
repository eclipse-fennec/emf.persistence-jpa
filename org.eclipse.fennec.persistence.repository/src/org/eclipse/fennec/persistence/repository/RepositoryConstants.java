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
package org.eclipse.fennec.persistence.repository;

/**
 * Constants of the repository API that cannot be carried by the generated API model
 * (EMF does not model interface constants).
 *
 * Successors of the Gecko {@code EMFRepository} service properties
 * ({@code repo_id}, {@code base_uri}, {@code contentType}, {@code loadOptions}).
 *
 * @since 18.08.2026
 */
public final class RepositoryConstants {

	private RepositoryConstants() {
	}

	/**
	 * OSGi service property carrying the stable repository id, unique per runtime.
	 * Answered by {@code RepositoryService.id()}.
	 */
	public static final String REPOSITORY_ID = "persistence.repository.id";

	/**
	 * Configuration and service property carrying the base URI the repository is
	 * bound to. Every URI the repository creates or resolves is rooted here.
	 * Answered by {@code RepositoryService.baseUri()}.
	 */
	public static final String REPOSITORY_BASE_URI = "persistence.repository.baseUri";

	/**
	 * Optional configuration property carrying default load options applied to
	 * every read operation unless overridden per call.
	 */
	public static final String REPOSITORY_DEFAULT_LOAD_OPTIONS = "persistence.repository.loadOptions";

	/**
	 * Optional configuration property carrying default save options applied to
	 * every write operation unless overridden per call.
	 */
	public static final String REPOSITORY_DEFAULT_SAVE_OPTIONS = "persistence.repository.saveOptions";

	/**
	 * Service property carrying the backend flavour this repository is bound to,
	 * derived from the base URI scheme — e.g. {@code "jpa"}, {@code "mongodb"},
	 * {@code "lucene"}.
	 */
	public static final String REPOSITORY_BACKEND = "persistence.repository.backend";

	/**
	 * Configuration and service property: {@code true} registers only the read side
	 * ({@code RepositoryService} + {@code ReadRepository}); the write interfaces are
	 * withheld entirely instead of failing at call time.
	 */
	public static final String REPOSITORY_READ_ONLY = "persistence.repository.readOnly";

	/**
	 * Per-call option for {@code find(name, ...)} / {@code prepare(name)}: the root
	 * {@code EClass} of the persisted query. Needed because the backend query catalog
	 * currently offers no load-back API — without this hint, only names whose root type
	 * the repository has already seen (via a saveQuery execution) can be resolved.
	 */
	public static final String OPTION_QUERY_ROOT = "persistence.repository.query.root";
}
