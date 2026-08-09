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

import static java.util.Objects.requireNonNull;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.eclipse.fennec.persistence.mongo.query.MongoQueryProcessor;
import org.eclipse.fennec.persistence.mongo.resource.MongoResourceImpl;
import org.eclipse.fennec.persistence.query.api.QueryProcessor;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * Factory for creating {@link MongoResourceImpl} instances.
 * <p>
 * Register with EMF's ResourceSet for the {@code mongodb} URI scheme:
 * <pre>
 * resourceSet.getResourceFactoryRegistry()
 *     .getProtocolToFactoryMap()
 *     .put("mongodb", new MongoResourceFactory(database, metadataService, null));
 * </pre>
 *
 * @author Mark Hoffmann
 * @since 16.07.2026
 */
public class MongoResourceFactory implements Resource.Factory {

	private static final Logger LOG = Logger.getLogger(MongoResourceFactory.class.getName());

	private final MongoDatabase database;
	private final MetadataService metadataService;
	private final CodecValueRegistry valueRegistry;
	private final QueryProcessor queryProcessor;
	private final MongoClient client;
	private final MongoFlavor flavor;

	public MongoResourceFactory(MongoDatabase database, MetadataService metadataService,
			CodecValueRegistry valueRegistry) {
		this(database, metadataService, valueRegistry, null);
	}

	/**
	 * Variant with an explicit {@link QueryProcessor} handed to every created resource
	 * (issue #61, e.g. the {@code mongo}-backend service from the OSGi registry);
	 * {@code null} keeps the resources' local default processor.
	 */
	public MongoResourceFactory(MongoDatabase database, MetadataService metadataService,
			CodecValueRegistry valueRegistry, QueryProcessor queryProcessor) {
		this(database, metadataService, valueRegistry, queryProcessor, null);
	}

	/**
	 * Variant with a session-capable {@link MongoClient} (issue #112): resources created
	 * by this factory serve {@code CommandResource.begin()} with real multi-document
	 * transactions on replica-set/mongos deployments. Without the client, command
	 * transactions are refused.
	 */
	public MongoResourceFactory(MongoDatabase database, MetadataService metadataService,
			CodecValueRegistry valueRegistry, QueryProcessor queryProcessor, MongoClient client) {
		this(database, metadataService, valueRegistry, queryProcessor, client, MongoFlavor.MONGO);
	}

	/**
	 * Variant declaring the server {@link MongoFlavor} behind the database (issue #118).
	 * <p>
	 * The wire protocol is also served by gateways over PostgreSQL, which cover fewer
	 * operators and stages. Translation is unaffected — the generated filters and pipelines
	 * are identical — so the flavor only narrows the <em>declared query capabilities</em>, so
	 * that an unsupported construct is refused with a Diagnostic up front instead of failing
	 * inside the driver.
	 *
	 * @param flavor the server flavor; {@code null} means {@link MongoFlavor#MONGO}
	 */
	public MongoResourceFactory(MongoDatabase database, MetadataService metadataService,
			CodecValueRegistry valueRegistry, QueryProcessor queryProcessor, MongoClient client,
			MongoFlavor flavor) {
		requireNonNull(database, "MongoDatabase is required");
		requireNonNull(metadataService, "MetadataService is required");
		this.database = database;
		this.metadataService = metadataService;
		this.valueRegistry = valueRegistry;
		this.client = client;
		this.flavor = flavor == null ? MongoFlavor.MONGO : flavor;
		this.queryProcessor = processorFor(queryProcessor, this.flavor);
	}

	/** The processor handed to created resources after flavor reconciliation; may be null. */
	QueryProcessor queryProcessor() {
		return queryProcessor;
	}

	/** The server flavor this factory declares capabilities for. */
	MongoFlavor flavor() {
		return flavor;
	}

	/**
	 * Reconciles an externally supplied processor with the flavor.
	 * <p>
	 * For {@link MongoFlavor#MONGO} nothing changes. For a gateway flavor the stock
	 * processor is replaced by one declaring that flavor's capabilities — but a
	 * <em>foreign</em> processor (decorated or higher-ranked, the point of issue #61) is left
	 * alone: it publishes its own capabilities and overriding them here would silently undo a
	 * deliberate customization. That combination is logged, because the configured flavor
	 * then has no effect on validation.
	 */
	private static QueryProcessor processorFor(QueryProcessor supplied, MongoFlavor flavor) {
		if (flavor == MongoFlavor.MONGO) {
			return supplied;
		}
		if (supplied == null) {
			return new MongoQueryProcessor(flavor);
		}
		if (supplied instanceof MongoQueryProcessor stock) {
			return stock.flavor() == flavor ? stock : new MongoQueryProcessor(flavor);
		}
		LOG.log(Level.WARNING,
				"A custom QueryProcessor ({0}) is bound while flavor ''{1}'' is configured — the processor''s own"
						+ " capability declaration applies, the flavor does not narrow it",
				new Object[] { supplied.getClass().getName(), flavor.id() });
		return supplied;
	}

	@Override
	public Resource createResource(URI uri) {
		MongoResourceImpl resource = new MongoResourceImpl(uri, database, metadataService,
				valueRegistry != null ? valueRegistry.copy() : null);
		if (queryProcessor != null) {
			resource.setQueryProcessor(queryProcessor);
		}
		if (client != null) {
			resource.setClient(client);
		}
		return resource;
	}
}
