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
package org.eclipse.fennec.persistence.repository.mongo;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.emf.common.util.URI;
import org.eclipse.fennec.emf.osgi.ResourceSetFactory;
import org.eclipse.fennec.persistence.mongo.MongoPersistenceConstants;
import org.eclipse.fennec.persistence.query.QueryConstants;
import org.eclipse.fennec.persistence.query.api.QueryProcessor;
import org.eclipse.fennec.persistence.repository.spi.AbstractRepository;
import org.eclipse.fennec.persistence.repository.spi.AbstractRepositoryComponent;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.mongodb.client.MongoDatabase;

/**
 * Mongo flavour of the repository facade. One factory configuration
 * ({@code fennec.repository.mongo}) binds a {@link MongoDatabase} — selected via the
 * {@code database.target} filter, e.g. {@code (mongo.database.alias=assets)} — and
 * registers a repository over {@code mongodb://<alias>} URIs, dispatched by the
 * mongodb:// whiteboard (issue #90). The database service is gated by connection
 * liveness, so the repository inherits appear/disappear behavior through the DS cascade.
 *
 * @since 18.08.2026
 */
@Component(name = MongoRepositoryComponent.PID, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = MongoRepositoryComponent.Config.class, factory = true)
public class MongoRepositoryComponent extends AbstractRepositoryComponent {

	public static final String PID = "fennec.repository.mongo";

	@ObjectClassDefinition(name = "Fennec Mongo Repository",
			description = "Repository facade over one MongoDB database")
	public @interface Config {

		@AttributeDefinition(name = "Repository Id",
				description = "Stable repository id, unique per runtime")
		String repositoryId();

		@AttributeDefinition(name = "Read Only",
				description = "true registers only the read side (RepositoryService + ReadRepository)")
		boolean readOnly() default false;
	}

	private final String repositoryId;
	private final URI baseUri;
	private final ResourceSetFactory resourceSetFactory;
	private final AtomicReference<QueryProcessor> queryProcessor = new AtomicReference<>();

	@Activate
	public MongoRepositoryComponent(BundleContext context, Config config,
			@Reference(name = "database") ServiceReference<MongoDatabase> databaseReference,
			@Reference ResourceSetFactory resourceSetFactory) {
		this.resourceSetFactory = resourceSetFactory;
		this.repositoryId = config.repositoryId();
		if (!(databaseReference.getProperty(MongoPersistenceConstants.DATABASE_ALIAS) instanceof String alias)
				|| alias.isBlank()) {
			throw new IllegalStateException("Repository '" + repositoryId + "': the bound MongoDatabase carries no "
					+ MongoPersistenceConstants.DATABASE_ALIAS + " property");
		}
		this.baseUri = URI.createURI(MongoPersistenceConstants.URI_SCHEME + "://" + alias);
		register(context, repositoryId, baseUri, config.readOnly());
	}

	/**
	 * The backend's query processor, used for prepare-time validation. Optional and
	 * greedy like the whiteboard's: without it, prepare() skips validation and queries
	 * are validated by the resource on execution.
	 */
	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC,
			policyOption = ReferencePolicyOption.GREEDY,
			target = "(" + QueryConstants.BACKEND_PROPERTY + "=mongo)")
	void setQueryProcessor(QueryProcessor processor) {
		queryProcessor.set(processor);
	}

	void unsetQueryProcessor(QueryProcessor processor) {
		queryProcessor.compareAndSet(processor, null);
	}

	@Deactivate
	public void deactivate() {
		unregister();
	}

	@Override
	protected AbstractRepository createRepository() {
		return new AbstractRepository(repositoryId, baseUri, resourceSetFactory::createResourceSet,
				queryProcessor.get(), Map.of(), Map.of()) {
			// all behavior is generic; the flavour only wires lifecycle and base URI
		};
	}
}
