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
package org.eclipse.fennec.persistence.repository.spi;

import static java.util.Objects.nonNull;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.fennec.persistence.repository.RepositoryConstants;
import org.eclipse.fennec.persistence.repository.api.PreparedQuery;
import org.eclipse.fennec.persistence.repository.api.ReadRepository;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Publishes one named query as a ready-to-run service (issue #204).
 * <p>
 * The consumer configures the query's name, binds the resulting service and supplies the
 * values — a named query with {@code from} and {@code to} becomes an injected handle you
 * call with those two bindings. Nothing about the query, its root type or its backend
 * reaches the consuming code.
 * <p>
 * A {@link PreparedQuery} rather than a repository, deliberately. A repository configured
 * for a single query would still carry {@code getEObject}, {@code getAllEObjects},
 * {@code count} and {@code reload}, none of which mean anything here — while
 * {@code PreparedQuery} is exactly "this operation, bound, run it with values", including
 * {@link PreparedQuery#parameterDeclarations()} so a caller can see what it must supply.
 * That the API anticipated this is not a coincidence: its documentation already described a
 * prepared query "registered as a configured OSGi service carrying the repository id and
 * query name as service properties".
 * <p>
 * Validation happens once, here: {@code prepare} checks the query against the backend's
 * capabilities at activation, so a query the backend cannot serve fails at configuration
 * time and never becomes a service. An unknown name fails the same way — a configuration
 * naming a query nobody deposited yields nothing rather than a handle that throws later.
 *
 * @author Mark Hoffmann
 * @since 22.08.2026
 */
@Designate(factory = true, ocd = PreparedQueryComponent.Config.class)
@Component(name = PreparedQueryComponent.PID, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class PreparedQueryComponent {

	/** Factory PID of a configured prepared query. */
	public static final String PID = "fennec.repository.preparedquery";

	/** Service property carrying the configured query name. */
	public static final String QUERY_NAME = "persistence.repository.query.name";

	@ObjectClassDefinition
	public @interface Config {

		@AttributeDefinition(name = "Prepared query id",
				description = "Stable id of this prepared query, published as persistence.repository.id")
		String repositoryId();

		@AttributeDefinition(name = "Query name",
				description = "The name the query is deposited under in the named-operation catalog")
		String queryName();
	}

	@Reference
	private ReadRepository repository;

	private ServiceRegistration<PreparedQuery> registration;

	@Activate
	void activate(BundleContext context, Config config) throws IOException {
		String id = config.repositoryId();
		String queryName = config.queryName();
		if (isBlank(id) || isBlank(queryName)) {
			throw new IllegalArgumentException(
					"A prepared query needs both repositoryId and queryName");
		}
		// prepare validates against the backend now, so a query it cannot serve never becomes
		// a service — the consumer finds out at configuration time, not at first call
		PreparedQuery prepared = repository.prepare(queryName);

		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(RepositoryConstants.REPOSITORY_ID, id);
		properties.put(QUERY_NAME, queryName);
		registration = context.registerService(PreparedQuery.class, prepared, properties);
	}

	@Deactivate
	void deactivate() {
		if (nonNull(registration)) {
			registration.unregister();
			registration = null;
		}
	}

	private static boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
