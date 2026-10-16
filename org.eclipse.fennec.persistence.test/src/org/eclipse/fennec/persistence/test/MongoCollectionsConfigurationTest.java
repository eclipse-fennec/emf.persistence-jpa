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
package org.eclipse.fennec.persistence.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.net.ServerSocket;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.BooleanSupplier;

import org.eclipse.fennec.persistence.mongo.MongoPersistenceConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.dictionary.Dictionaries;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * The {@code collections} key of a database configuration (issue #290): a valid mapping
 * travels on the database service to the resource factory, a malformed one keeps the
 * database from registering at all — so a typo is a missing service, never a repository
 * that answers every read with an empty collection.
 *
 * @since 16.10.2026
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
public class MongoCollectionsConfigurationTest {

	private static final String CLIENT_IDENT = "collectionsmongo";
	private static final String DB_ALIAS = "collectionsmongodb";
	private static final long WAIT_APPEAR_MILLIS = 60_000;
	/** Outlasts configuration delivery and DS activation, see MongoFlavorConfigurationTest. */
	private static final long WAIT_ABSENT_MILLIS = 10_000;

	private String containerId;

	@AfterEach
	public void cleanupContainer() {
		MongoContainerSupport.remove(containerId);
		containerId = null;
	}

	private static void waitUntil(String description, BooleanSupplier check, long timeoutMillis)
			throws InterruptedException {
		long deadline = System.currentTimeMillis() + timeoutMillis;
		while (System.currentTimeMillis() < deadline) {
			if (check.getAsBoolean()) {
				return;
			}
			Thread.sleep(200);
		}
		fail("Timed out after " + timeoutMillis + "ms waiting until: " + description);
	}

	private static void assertStaysTrue(String description, BooleanSupplier check, long millis)
			throws InterruptedException {
		long deadline = System.currentTimeMillis() + millis;
		while (System.currentTimeMillis() < deadline) {
			if (!check.getAsBoolean()) {
				fail("No longer true while waiting " + millis + "ms: " + description);
			}
			Thread.sleep(200);
		}
	}

	/** Starts a container and configures a client for it; returns the client configuration. */
	private Configuration startClient(ConfigurationAdmin configAdmin, String name) throws Exception {
		int port;
		try (ServerSocket socket = new ServerSocket(0)) {
			port = socket.getLocalPort();
		}
		containerId = MongoContainerSupport.start(port);
		Configuration clientConfig = configAdmin.getFactoryConfiguration(
				MongoPersistenceConstants.CLIENT_PID, name, "?");
		clientConfig.update(Dictionaries.asDictionary(Map.of(
				"ident", CLIENT_IDENT,
				"connectionString", "mongodb://127.0.0.1:" + port
						+ "/?serverSelectionTimeoutMS=2000&heartbeatFrequencyMS=500",
				"liveness.checkInterval", "1",
				"liveness.checkTimeout", "2",
				"liveness.failureThreshold", "1",
				"liveness.retryMin", "1",
				"liveness.retryMax", "2")));
		return clientConfig;
	}

	private static Hashtable<String, Object> databaseProperties(String database, String... collections) {
		Hashtable<String, Object> properties = new Hashtable<>();
		properties.put("alias", DB_ALIAS);
		properties.put("database", database);
		properties.put("client.target", "(" + MongoPersistenceConstants.CLIENT_IDENT + "=" + CLIENT_IDENT + ")");
		properties.put("collections", collections);
		return properties;
	}

	@Test
	public void collectionMappingTravelsOnTheDatabaseService(
			@InjectService ConfigurationAdmin configAdmin,
			@InjectService(cardinality = 0, filter = "(" + MongoPersistenceConstants.CLIENT_IDENT + "="
					+ CLIENT_IDENT + ")") ServiceAware<MongoClient> clientAware,
			@InjectService(cardinality = 0, filter = "(" + MongoPersistenceConstants.DATABASE_ALIAS + "="
					+ DB_ALIAS + ")") ServiceAware<MongoDatabase> databaseAware) throws Exception {
		assumeTrue(MongoContainerSupport.containerCliAvailable(),
				"no container CLI (docker/podman) available - skipping");
		Configuration clientConfig = startClient(configAdmin, "collectionsclient");
		Configuration databaseConfig = configAdmin.getFactoryConfiguration(
				MongoPersistenceConstants.DATABASE_PID, "collectionsdatabase", "?");
		try {
			databaseConfig.update(databaseProperties("collectionstest",
					"BikeParking=bikeparking", " Station = stations "));

			waitUntil("MongoClient is registered", () -> !clientAware.isEmpty(), WAIT_APPEAR_MILLIS);
			waitUntil("MongoDatabase is registered", () -> !databaseAware.isEmpty(), WAIT_APPEAR_MILLIS);

			// normalized: the whitespace around an entry is configuration noise, not a name
			assertThat((String[]) databaseAware.getServiceReference()
					.getProperty(MongoPersistenceConstants.COLLECTIONS))
					.containsExactlyInAnyOrder("BikeParking=bikeparking", "Station=stations");
		} finally {
			databaseConfig.delete();
			clientConfig.delete();
		}
	}

	@Test
	public void withoutAMappingTheDatabaseCarriesNoCollectionsProperty(
			@InjectService ConfigurationAdmin configAdmin,
			@InjectService(cardinality = 0, filter = "(" + MongoPersistenceConstants.CLIENT_IDENT + "="
					+ CLIENT_IDENT + ")") ServiceAware<MongoClient> clientAware,
			@InjectService(cardinality = 0, filter = "(" + MongoPersistenceConstants.DATABASE_ALIAS + "="
					+ DB_ALIAS + ")") ServiceAware<MongoDatabase> databaseAware) throws Exception {
		assumeTrue(MongoContainerSupport.containerCliAvailable(),
				"no container CLI (docker/podman) available - skipping");
		Configuration clientConfig = startClient(configAdmin, "nocollectionsclient");
		Configuration databaseConfig = configAdmin.getFactoryConfiguration(
				MongoPersistenceConstants.DATABASE_PID, "nocollectionsdatabase", "?");
		try {
			databaseConfig.update(databaseProperties("nocollectionstest"));

			waitUntil("MongoClient is registered", () -> !clientAware.isEmpty(), WAIT_APPEAR_MILLIS);
			waitUntil("MongoDatabase is registered", () -> !databaseAware.isEmpty(), WAIT_APPEAR_MILLIS);

			assertThat(databaseAware.getServiceReference().getProperty(MongoPersistenceConstants.COLLECTIONS))
					.isNull();
		} finally {
			databaseConfig.delete();
			clientConfig.delete();
		}
	}

	/**
	 * Two types onto one collection is the mapping error that would read silently wrong — each
	 * type would see the other's documents — so it must cost the database service, while the
	 * client, configured correctly, stays.
	 */
	@Test
	public void aMalformedMappingRegistersNoDatabase(
			@InjectService ConfigurationAdmin configAdmin,
			@InjectService(cardinality = 0, filter = "(" + MongoPersistenceConstants.CLIENT_IDENT + "="
					+ CLIENT_IDENT + ")") ServiceAware<MongoClient> clientAware,
			@InjectService(cardinality = 0, filter = "(" + MongoPersistenceConstants.DATABASE_ALIAS + "="
					+ DB_ALIAS + ")") ServiceAware<MongoDatabase> databaseAware) throws Exception {
		assumeTrue(MongoContainerSupport.containerCliAvailable(),
				"no container CLI (docker/podman) available - skipping");
		Configuration clientConfig = startClient(configAdmin, "badcollectionsclient");
		Configuration databaseConfig = configAdmin.getFactoryConfiguration(
				MongoPersistenceConstants.DATABASE_PID, "badcollectionsdatabase", "?");
		try {
			waitUntil("MongoClient is registered", () -> !clientAware.isEmpty(), WAIT_APPEAR_MILLIS);
			databaseConfig.update(databaseProperties("badcollectionstest",
					"BikeParking=parking", "CarParking=parking"));

			assertStaysTrue("no MongoDatabase is registered for a malformed collection mapping",
					databaseAware::isEmpty, WAIT_ABSENT_MILLIS);
			assertThat(clientAware.isEmpty()).isFalse();
		} finally {
			databaseConfig.delete();
			clientConfig.delete();
		}
	}
}
