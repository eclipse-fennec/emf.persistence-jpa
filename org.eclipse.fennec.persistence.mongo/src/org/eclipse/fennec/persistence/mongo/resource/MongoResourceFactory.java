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
package org.eclipse.fennec.persistence.mongo.resource;

import static java.util.Objects.requireNonNull;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.eclipse.fennec.persistence.query.api.QueryProcessor;

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

	private final MongoDatabase database;
	private final MetadataService metadataService;
	private final CodecValueRegistry valueRegistry;
	private final QueryProcessor queryProcessor;

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
		requireNonNull(database, "MongoDatabase is required");
		requireNonNull(metadataService, "MetadataService is required");
		this.database = database;
		this.metadataService = metadataService;
		this.valueRegistry = valueRegistry;
		this.queryProcessor = queryProcessor;
	}

	@Override
	public Resource createResource(URI uri) {
		MongoResourceImpl resource = new MongoResourceImpl(uri, database, metadataService,
				valueRegistry != null ? valueRegistry.copy() : null);
		if (queryProcessor != null) {
			resource.setQueryProcessor(queryProcessor);
		}
		return resource;
	}
}
