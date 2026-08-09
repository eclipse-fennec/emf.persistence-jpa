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
import java.util.Arrays;
import java.util.Map;
import java.util.function.BooleanSupplier;

import org.eclipse.fennec.persistence.liveness.PersistenceLivenessRuntime;
import org.eclipse.fennec.persistence.liveness.dto.GateDTO;
import org.eclipse.fennec.persistence.mongo.MongoFlavor;
import org.eclipse.fennec.persistence.mongo.MongoPersistenceConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.condition.Condition;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.dictionary.Dictionaries;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * OSGi integration test for the server-flavor configuration (issue #118), the companion of
 * {@link MongoLivenessTest} on the configuration plane.
 * <p>
 * "Presence indicates functionality" (see {@code docs/concept-connection-liveness.md}) has to
 * hold for a misconfiguration too, and the two failure classes must stay
 * <em>distinguishable</em>:
 * <ul>
 * <li><b>Connection failure</b> — the server is unreachable. The component activates, a gate
 * exists and reports DOWN with a diagnosable message, and no service is registered.
 * {@link MongoLivenessTest} covers that.</li>
 * <li><b>Configuration failure</b> — the flavor id is unknown. The component must not activate
 * at all, so there is <em>no gate</em> and no service. Covered here.</li>
 * </ul>
 * Without that distinction an operator cannot tell "my database is down" from "my config is
 * wrong" — both look like a missing service. The gate is what separates them.
 * <p>
 * The negative case needs no container. The propagation case does, and skips without one.
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
public class MongoFlavorConfigurationTest {

	private static final String BAD_FLAVOR_IDENT = "badflavormongo";
	private static final String FLAVOR_IDENT = "flavormongo";
	private static final String DB_ALIAS = "flavormongodb";
	private static final long WAIT_APPEAR_MILLIS = 60_000;
	/**
	 * How long a service is given to NOT appear. A misconfigured component fails on activation,
	 * i.e. immediately — but the check has to outlast configuration delivery and DS activation,
	 * otherwise it would pass simply for being early.
	 */
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

	/** Asserts the condition holds for the whole period, not just once at the end. */
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

	private static GateDTO gateDTO(PersistenceLivenessRuntime runtime, String ident) {
		return Arrays.stream(runtime.getRuntimeDTO().gates)
				.filter(gate -> ident.equals(gate.ident))
				.findFirst()
				.orElse(null);
	}

	/**
	 * An unknown flavor id must keep the client unregistered — and unlike an unreachable
	 * server, it must not even produce a gate.
	 * <p>
	 * {@code postgres} is the deliberate choice of wrong value: it is the id a user is most
	 * likely to try, since FerretDB and the DocumentDB gateway both run on PostgreSQL. Accepting
	 * it silently as "mongo" would hand out MongoDB's capability declaration for a server that
	 * is not MongoDB — the exact failure mode flavors exist to prevent — so the configuration is
	 * refused instead. The connection string points at a port nobody serves, which does not
	 * matter here: activation fails before any I/O, which is precisely what the missing gate
	 * proves.
	 */
	@Test
	@WithFactoryConfiguration(factoryPid = MongoPersistenceConstants.CLIENT_PID, name = "badflavorclient", location = "?", properties = {
			@Property(key = "ident", value = BAD_FLAVOR_IDENT),
			@Property(key = "connectionString",
					value = "mongodb://127.0.0.1:1/?serverSelectionTimeoutMS=1000&heartbeatFrequencyMS=500"),
			@Property(key = "flavor", value = "postgres"),
			@Property(key = "liveness.checkInterval", value = "1"),
			@Property(key = "liveness.checkTimeout", value = "1"),
			@Property(key = "liveness.failureThreshold", value = "1"),
			@Property(key = "liveness.retryMin", value = "1"),
			@Property(key = "liveness.retryMax", value = "1")
	})
	public void unknownFlavorRegistersNoClientAndNoGate(
			@InjectService(cardinality = 0, filter = "(" + MongoPersistenceConstants.CLIENT_IDENT + "="
					+ BAD_FLAVOR_IDENT + ")") ServiceAware<MongoClient> clientAware,
			@InjectService(cardinality = 0, filter = "(osgi.condition.id=fennec.liveness." + BAD_FLAVOR_IDENT + ")")
			ServiceAware<Condition> conditionAware,
			@InjectService ServiceAware<PersistenceLivenessRuntime> runtimeAware) throws Exception {
		PersistenceLivenessRuntime runtime = runtimeAware.waitForService(5000);

		// stays absent for the whole window — a single check right after configuration would
		// pass just for being faster than DS
		assertStaysTrue("no MongoClient is registered for an unknown flavor", clientAware::isEmpty,
				WAIT_ABSENT_MILLIS);
		assertThat(conditionAware.isEmpty()).isTrue();

		// the distinguishing assertion: no gate at all. An unreachable server WOULD have one,
		// reporting DOWN (MongoLivenessTest) — here activation never got that far.
		assertThat(gateDTO(runtime, BAD_FLAVOR_IDENT)).isNull();
	}

	/**
	 * A configured flavor is propagated as a service property from the client to the database
	 * service, which is what lets the resource factory pick the right capability set per alias
	 * without a second configuration to keep in sync.
	 * <p>
	 * Deliberately configures {@code ferretdb} against a real MongoDB: the flavor is a
	 * <em>declaration</em>, and the point here is that the declaration travels. The handshake
	 * notices the mismatch and logs a warning — it must not silently correct the value, because
	 * swapping the capability set underneath a running resource would change which queries are
	 * legal mid-flight.
	 */
	@Test
	public void flavorPropagatesFromClientToDatabase(
			@InjectService ConfigurationAdmin configAdmin,
			@InjectService(cardinality = 0, filter = "(" + MongoPersistenceConstants.CLIENT_IDENT + "="
					+ FLAVOR_IDENT + ")") ServiceAware<MongoClient> clientAware,
			@InjectService(cardinality = 0, filter = "(" + MongoPersistenceConstants.DATABASE_ALIAS + "="
					+ DB_ALIAS + ")") ServiceAware<MongoDatabase> databaseAware) throws Exception {
		assumeTrue(MongoContainerSupport.containerCliAvailable(),
				"no container CLI (docker/podman) available - skipping");

		int port;
		try (ServerSocket socket = new ServerSocket(0)) {
			port = socket.getLocalPort();
		}
		containerId = MongoContainerSupport.start(port);

		Configuration clientConfig = configAdmin.getFactoryConfiguration(
				MongoPersistenceConstants.CLIENT_PID, "flavorclient", "?");
		Configuration databaseConfig = configAdmin.getFactoryConfiguration(
				MongoPersistenceConstants.DATABASE_PID, "flavordatabase", "?");
		try {
			clientConfig.update(Dictionaries.asDictionary(Map.of(
					"ident", FLAVOR_IDENT,
					"connectionString", "mongodb://127.0.0.1:" + port
							+ "/?serverSelectionTimeoutMS=2000&heartbeatFrequencyMS=500",
					"flavor", MongoFlavor.FERRETDB.id(),
					"liveness.checkInterval", "1",
					"liveness.checkTimeout", "2",
					"liveness.failureThreshold", "1",
					"liveness.retryMin", "1",
					"liveness.retryMax", "2")));
			databaseConfig.update(Dictionaries.asDictionary(Map.of(
					"alias", DB_ALIAS,
					"database", "flavortest",
					"client.target", "(" + MongoPersistenceConstants.CLIENT_IDENT + "=" + FLAVOR_IDENT + ")")));

			waitUntil("MongoClient is registered", () -> !clientAware.isEmpty(), WAIT_APPEAR_MILLIS);
			waitUntil("MongoDatabase is registered", () -> !databaseAware.isEmpty(), WAIT_APPEAR_MILLIS);

			assertThat(clientAware.getServiceReference().getProperty(MongoPersistenceConstants.FLAVOR))
					.isEqualTo(MongoFlavor.FERRETDB.id());
			// the propagated copy is what the resource factory reads
			assertThat(databaseAware.getServiceReference().getProperty(MongoPersistenceConstants.FLAVOR))
					.isEqualTo(MongoFlavor.FERRETDB.id());
		} finally {
			databaseConfig.delete();
			clientConfig.delete();
		}
	}

	/**
	 * Without a {@code flavor} property the services still carry one — {@code mongo}. An absent
	 * property would force every reader to re-implement the default, and a reader that forgets
	 * would fall back to "no flavor" rather than to MongoDB.
	 */
	@Test
	public void defaultFlavorIsPropagatedExplicitly(
			@InjectService ConfigurationAdmin configAdmin,
			@InjectService(cardinality = 0, filter = "(" + MongoPersistenceConstants.CLIENT_IDENT + "="
					+ FLAVOR_IDENT + ")") ServiceAware<MongoClient> clientAware,
			@InjectService(cardinality = 0, filter = "(" + MongoPersistenceConstants.DATABASE_ALIAS + "="
					+ DB_ALIAS + ")") ServiceAware<MongoDatabase> databaseAware) throws Exception {
		assumeTrue(MongoContainerSupport.containerCliAvailable(),
				"no container CLI (docker/podman) available - skipping");

		int port;
		try (ServerSocket socket = new ServerSocket(0)) {
			port = socket.getLocalPort();
		}
		containerId = MongoContainerSupport.start(port);

		Configuration clientConfig = configAdmin.getFactoryConfiguration(
				MongoPersistenceConstants.CLIENT_PID, "defaultflavorclient", "?");
		Configuration databaseConfig = configAdmin.getFactoryConfiguration(
				MongoPersistenceConstants.DATABASE_PID, "defaultflavordatabase", "?");
		try {
			clientConfig.update(Dictionaries.asDictionary(Map.of(
					"ident", FLAVOR_IDENT,
					"connectionString", "mongodb://127.0.0.1:" + port
							+ "/?serverSelectionTimeoutMS=2000&heartbeatFrequencyMS=500",
					"liveness.checkInterval", "1",
					"liveness.checkTimeout", "2",
					"liveness.failureThreshold", "1",
					"liveness.retryMin", "1",
					"liveness.retryMax", "2")));
			databaseConfig.update(Dictionaries.asDictionary(Map.of(
					"alias", DB_ALIAS,
					"database", "defaultflavortest",
					"client.target", "(" + MongoPersistenceConstants.CLIENT_IDENT + "=" + FLAVOR_IDENT + ")")));

			waitUntil("MongoClient is registered", () -> !clientAware.isEmpty(), WAIT_APPEAR_MILLIS);
			waitUntil("MongoDatabase is registered", () -> !databaseAware.isEmpty(), WAIT_APPEAR_MILLIS);

			assertThat(clientAware.getServiceReference().getProperty(MongoPersistenceConstants.FLAVOR))
					.isEqualTo(MongoFlavor.MONGO.id());
			assertThat(databaseAware.getServiceReference().getProperty(MongoPersistenceConstants.FLAVOR))
					.isEqualTo(MongoFlavor.MONGO.id());
		} finally {
			databaseConfig.delete();
			clientConfig.delete();
		}
	}
}
