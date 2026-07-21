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
import java.util.Objects;
import java.util.function.BooleanSupplier;

import org.eclipse.fennec.persistence.liveness.LivenessConstants;
import org.eclipse.fennec.persistence.liveness.PersistenceLivenessRuntime;
import org.eclipse.fennec.persistence.liveness.dto.GateDTO;
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
 * OSGi integration test for the liveness-gated MongoDB components (concept:
 * {@code docs/concept-connection-liveness.md}): {@code MongoClient} — and through the DS
 * reference cascade {@code MongoDatabase} — must only be registered while the server is
 * actually reachable. The container part runs against a MongoDB started through the
 * local docker CLI and is skipped when docker is unavailable; the unreachable-server
 * part needs no docker and always runs.
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
public class MongoLivenessTest {

	private static final String IDENT = "livemongo";
	private static final String DOWN_IDENT = "downmongo";
	private static final String DB_ALIAS = "livemongodb";
	private static final long WAIT_APPEAR_MILLIS = 60_000;
	private static final long WAIT_DISAPPEAR_MILLIS = 20_000;

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

	private static GateDTO gateDTO(PersistenceLivenessRuntime runtime, String ident) {
		return Arrays.stream(runtime.getRuntimeDTO().gates)
				.filter(gate -> ident.equals(gate.ident))
				.findFirst()
				.orElse(null);
	}

	/**
	 * A client configuration pointing at an unreachable server must not register any
	 * {@code MongoClient} service (creating a Mongo client performs no I/O — exactly the
	 * gap the gate closes), while the liveness runtime makes the failing gate visible.
	 * Runs without docker.
	 */
	@Test
	@WithFactoryConfiguration(factoryPid = MongoPersistenceConstants.CLIENT_PID, name = "downclient", location = "?", properties = {
			@Property(key = "ident", value = DOWN_IDENT),
			@Property(key = "connectionString",
					value = "mongodb://127.0.0.1:1/?serverSelectionTimeoutMS=1000&heartbeatFrequencyMS=500"),
			@Property(key = "liveness.checkInterval", value = "1"),
			@Property(key = "liveness.checkTimeout", value = "1"),
			@Property(key = "liveness.failureThreshold", value = "1"),
			@Property(key = "liveness.retryMin", value = "1"),
			@Property(key = "liveness.retryMax", value = "1")
	})
	public void unreachableServerRegistersNoClientButReportsGateDown(
			@InjectService(cardinality = 0, filter = "(" + MongoPersistenceConstants.CLIENT_IDENT + "=" + DOWN_IDENT + ")")
			ServiceAware<MongoClient> clientAware,
			@InjectService(cardinality = 0, filter = "(osgi.condition.id=fennec.liveness." + DOWN_IDENT + ")")
			ServiceAware<Condition> conditionAware,
			@InjectService ServiceAware<PersistenceLivenessRuntime> runtimeAware) throws Exception {
		PersistenceLivenessRuntime runtime = runtimeAware.waitForService(5000);

		// the gate exists and has probed, but stays DOWN with a diagnosable failure
		waitUntil("gate " + DOWN_IDENT + " reports DOWN with a failure message", () -> {
			GateDTO gate = gateDTO(runtime, DOWN_IDENT);
			return Objects.nonNull(gate)
					&& LivenessConstants.STATE_DOWN.equals(gate.state)
					&& gate.probeCount > 0
					&& Objects.nonNull(gate.lastFailureMessage);
		}, WAIT_DISAPPEAR_MILLIS);

		GateDTO gate = gateDTO(runtime, DOWN_IDENT);
		assertThat(gate.backendType).isEqualTo(LivenessConstants.BACKEND_MONGO);
		assertThat(gate.lastSuccess).isZero();

		// presence indicates functionality: neither service nor condition may exist
		assertThat(clientAware.isEmpty()).isTrue();
		assertThat(conditionAware.isEmpty()).isTrue();
	}

	/**
	 * Full lifecycle against a real MongoDB container: client and database services
	 * appear once the server is reachable, disappear (client gate + DS cascade to the
	 * database component) when the container is killed, and return when a new container
	 * comes up on the same address. Skipped when docker is unavailable.
	 */
	@Test
	public void clientAndDatabaseFollowServerAvailability(
			@InjectService ConfigurationAdmin configAdmin,
			@InjectService(cardinality = 0, filter = "(" + MongoPersistenceConstants.CLIENT_IDENT + "=" + IDENT + ")")
			ServiceAware<MongoClient> clientAware,
			@InjectService(cardinality = 0, filter = "(" + MongoPersistenceConstants.DATABASE_ALIAS + "=" + DB_ALIAS + ")")
			ServiceAware<MongoDatabase> databaseAware,
			@InjectService(cardinality = 0, filter = "(osgi.condition.id=fennec.liveness." + IDENT + ")")
			ServiceAware<Condition> conditionAware,
			@InjectService ServiceAware<PersistenceLivenessRuntime> runtimeAware) throws Exception {
		assumeTrue(MongoContainerSupport.containerCliAvailable(),
				"no container CLI (docker/podman) available - skipping");
		PersistenceLivenessRuntime runtime = runtimeAware.waitForService(5000);

		int port;
		try (ServerSocket socket = new ServerSocket(0)) {
			port = socket.getLocalPort();
		}
		containerId = MongoContainerSupport.start(port);

		Configuration clientConfig = configAdmin.getFactoryConfiguration(
				MongoPersistenceConstants.CLIENT_PID, "liveclient", "?");
		Configuration databaseConfig = configAdmin.getFactoryConfiguration(
				MongoPersistenceConstants.DATABASE_PID, "livedatabase", "?");
		try {
			clientConfig.update(Dictionaries.asDictionary(Map.of(
					"ident", IDENT,
					"connectionString", "mongodb://127.0.0.1:" + port
							+ "/?serverSelectionTimeoutMS=2000&heartbeatFrequencyMS=500",
					"liveness.checkInterval", "1",
					"liveness.checkTimeout", "2",
					"liveness.failureThreshold", "1",
					"liveness.retryMin", "1",
					"liveness.retryMax", "2")));
			databaseConfig.update(Dictionaries.asDictionary(Map.of(
					"alias", DB_ALIAS,
					"database", "livenesstest",
					"client.target", "(" + MongoPersistenceConstants.CLIENT_IDENT + "=" + IDENT + ")")));

			// UP: client, cascaded database and condition appear
			waitUntil("MongoClient is registered", () -> !clientAware.isEmpty(), WAIT_APPEAR_MILLIS);
			waitUntil("MongoDatabase is registered", () -> !databaseAware.isEmpty(), WAIT_APPEAR_MILLIS);
			waitUntil("liveness condition is registered", () -> !conditionAware.isEmpty(), WAIT_APPEAR_MILLIS);
			GateDTO up = gateDTO(runtime, IDENT);
			assertThat(up).isNotNull();
			assertThat(up.state).isEqualTo(LivenessConstants.STATE_UP);
			assertThat(up.backendType).isEqualTo(LivenessConstants.BACKEND_MONGO);
			assertThat(up.lastSuccess).isPositive();

			// DOWN: killing the container unregisters client, database and condition
			MongoContainerSupport.remove(containerId);
			containerId = null;
			waitUntil("MongoClient is unregistered after container kill",
					clientAware::isEmpty, WAIT_DISAPPEAR_MILLIS);
			waitUntil("MongoDatabase is unregistered after container kill",
					databaseAware::isEmpty, WAIT_DISAPPEAR_MILLIS);
			waitUntil("liveness condition is unregistered after container kill",
					conditionAware::isEmpty, WAIT_DISAPPEAR_MILLIS);
			GateDTO down = gateDTO(runtime, IDENT);
			assertThat(down).isNotNull();
			assertThat(down.state).isEqualTo(LivenessConstants.STATE_DOWN);
			assertThat(down.lastFailureMessage).isNotBlank();

			// RECOVERY: a new container on the same address brings everything back
			containerId = MongoContainerSupport.start(port);
			waitUntil("MongoClient is re-registered after container restart",
					() -> !clientAware.isEmpty(), WAIT_APPEAR_MILLIS);
			waitUntil("MongoDatabase is re-registered after container restart",
					() -> !databaseAware.isEmpty(), WAIT_APPEAR_MILLIS);
			assertThat(gateDTO(runtime, IDENT).state).isEqualTo(LivenessConstants.STATE_UP);
		} finally {
			databaseConfig.delete();
			clientConfig.delete();
		}
	}
}
