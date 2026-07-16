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
package org.eclipse.fennec.persistence.tck;

import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.UUID;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.model.metadata.service.MetadataServiceImpl;
import org.eclipse.fennec.persistence.mongo.resource.MongoResourceFactory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * TCK binding for the MongoDB backend. Requires a reachable MongoDB — either via
 * {@code -Dmongo.uri} / {@code MONGO_URI} or a Docker/Podman-started container
 * (see {@link MongoTestSupport}); otherwise the tests are skipped.
 *
 * @author Mark Hoffmann
 * @since 16.07.2026
 */
class MongoPersistenceTckTest extends AbstractPersistenceTCK {

	private MongoClient client;
	private MongoDatabase database;
	private MetadataServiceImpl metadataService;
	private String databaseName;

	@Override
	protected void setUpBackend(EPackage tckPackage) {
		String connectionString = MongoTestSupport.connectionString();
		assumeTrue(nonNull(connectionString),
				"No MongoDB available (set -Dmongo.uri or provide docker/podman)");
		metadataService = new MetadataServiceImpl();
		metadataService.registerPackage(tckPackage);
		client = MongoClients.create(connectionString);
		databaseName = "tck_" + UUID.randomUUID().toString().replace("-", "");
		database = client.getDatabase(databaseName);
	}

	@Override
	protected void tearDownBackend() {
		if (nonNull(database)) {
			database.drop();
			database = null;
		}
		if (nonNull(client)) {
			client.close();
			client = null;
		}
	}

	@Override
	protected ResourceSet createBackendResourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(tckPackage.getNsURI(), tckPackage);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("mongodb", new MongoResourceFactory(database, metadataService, null));
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new XMIResourceFactoryImpl());
		return resourceSet;
	}

	@Override
	protected URI uriFor(String typeName) {
		return URI.createURI("mongodb://" + databaseName + "/" + typeName);
	}
}
