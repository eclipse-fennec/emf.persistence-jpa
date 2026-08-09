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

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides a MongoDB instance for the TCK:
 * <ol>
 * <li>an externally supplied one via {@code -Dmongo.uri=...} / {@code MONGO_URI}, or</li>
 * <li>a container started through a local container CLI — no client-library dependency
 *     needed. The CLI is resolved once: {@code -Dmongo.container.cli=<name>} wins,
 *     otherwise {@code docker} is tried first and {@code podman} as fallback (relevant
 *     on macOS/Windows, where podman ships without a docker shim).</li>
 * </ol>
 * When neither is available the Mongo TCK tests are skipped via JUnit assumptions.
 *
 * @author Mark Hoffmann
 * @since 16.07.2026
 */
final class MongoTestSupport {

	private static final Logger LOG = Logger.getLogger(MongoTestSupport.class.getName());
	private static final String CLI_OVERRIDE = System.getProperty("mongo.container.cli");

	/**
	 * The server flavor under test (issue #119): {@code mongo} (default) or {@code ferretdb}.
	 * The wire protocol is identical, so the whole suite runs unchanged — what differs is how
	 * the container starts and which query capabilities the backend may declare.
	 */
	private static final String FLAVOR = System.getProperty("mongo.test.flavor", "mongo").trim().toLowerCase();

	static final String FLAVOR_MONGO = "mongo";
	static final String FLAVOR_FERRETDB = "ferretdb";

	/**
	 * FerretDB 2.x bundles PostgreSQL, the DocumentDB extension and the wire gateway in this
	 * single evaluation image, which keeps the harness at one container. Pinned: an unpinned
	 * tag would make capability drift look like a random test failure.
	 */
	private static final String FERRETDB_IMAGE = "ghcr.io/ferretdb/ferretdb-eval:2";

	/** Mandatory for the FerretDB image — without it the container exits immediately. */
	private static final String FERRETDB_PASSWORD = "fennec";

	private static final String IMAGE = System.getProperty("mongo.test.image",
			isFerretDb() ? FERRETDB_IMAGE : "docker.io/library/mongo:7");

	private static volatile String uri;
	private static volatile String containerId;
	private static volatile boolean initialized;
	private static volatile String cli;

	private MongoTestSupport() {
	}

	/** @return the flavor id under test, as configured by {@code -Dmongo.test.flavor} */
	static String flavor() {
		return FLAVOR;
	}

	static boolean isFerretDb() {
		return FLAVOR_FERRETDB.equals(FLAVOR);
	}

	private static String resolveCli() {
		if (nonNull(cli)) {
			return cli;
		}
		String[] candidates = nonNull(CLI_OVERRIDE) && !CLI_OVERRIDE.isBlank()
				? new String[] { CLI_OVERRIDE.trim() }
				: new String[] { "docker", "podman" };
		for (String candidate : candidates) {
			try {
				exec(15, candidate, "version");
				cli = candidate;
				return cli;
			} catch (Exception e) {
				LOG.log(Level.FINE, () -> "Container CLI '" + candidate + "' not usable: " + e.getMessage());
			}
		}
		throw new IllegalStateException("No container CLI (docker/podman) available");
	}

	/** Returns the connection string, starting a container on first use; {@code null} if unavailable. */
	static synchronized String connectionString() {
		if (initialized) {
			return uri;
		}
		initialized = true;
		String external = System.getProperty("mongo.uri", System.getenv("MONGO_URI"));
		if (nonNull(external) && !external.isBlank()) {
			uri = external;
			return uri;
		}
		try {
			String id = isFerretDb() ? startFerretDb() : startMongo();
			if (nonNull(id) && !id.isBlank()) {
				containerId = id.trim();
				String mapping = exec(20, resolveCli(), "port", containerId, "27017/tcp");
				if (nonNull(mapping) && mapping.contains(":")) {
					String port = mapping.trim().lines().findFirst().orElse("");
					port = port.substring(port.lastIndexOf(':') + 1);
					if (isFerretDb()) {
						awaitWireProtocol();
						// the gateway authenticates against the Postgres role
						uri = "mongodb://postgres:" + FERRETDB_PASSWORD + "@127.0.0.1:" + port + "/";
					} else {
						initiateReplicaSet();
						uri = "mongodb://127.0.0.1:" + port + "/?directConnection=true";
					}
					Runtime.getRuntime().addShutdownHook(new Thread(MongoTestSupport::shutdown));
				}
			}
		} catch (Exception e) {
			LOG.log(Level.INFO, "No " + FLAVOR + " server available for TCK tests: " + e.getMessage());
		}
		return uri;
	}

	/**
	 * Starts MongoDB as a single-node replica set: functionally identical for plain
	 * operations, and it unlocks multi-document transactions for the command-bracket TCK
	 * cases (issue #112).
	 */
	private static String startMongo() throws Exception {
		return exec(180, resolveCli(), "run", "-d", "--rm", "-p", "127.0.0.1::27017", IMAGE, "--replSet", "rs0");
	}

	/**
	 * Starts the FerretDB evaluation image. No replica set: the gateway is a single logical
	 * server, so {@code rs.initiate()} does not apply — which is exactly why the
	 * {@code TRANSACTION_BRACKET} command feature stays undeclared there (issue #112's
	 * runtime probe in {@code MongoResourceImpl.capabilities()} handles that on its own).
	 */
	private static String startFerretDb() throws Exception {
		return exec(300, resolveCli(), "run", "-d", "--rm", "-p", "127.0.0.1::27017",
				"-e", "POSTGRES_PASSWORD=" + FERRETDB_PASSWORD, IMAGE);
	}

	/**
	 * Waits until the gateway answers on the wire. The image starts PostgreSQL, installs the
	 * DocumentDB extension and only then opens the Mongo port, so a connection attempt right
	 * after {@code run} is refused.
	 */
	private static void awaitWireProtocol() throws Exception {
		long deadline = System.currentTimeMillis() + 120_000;
		Exception last = null;
		while (System.currentTimeMillis() < deadline) {
			try {
				exec(20, resolveCli(), "exec", containerId, "mongosh",
						"mongodb://postgres:" + FERRETDB_PASSWORD + "@127.0.0.1:27017/", "--quiet",
						"--eval", "db.adminCommand({ping:1})");
				return;
			} catch (Exception e) {
				last = e;
				Thread.sleep(2_000);
			}
		}
		throw new IllegalStateException("FerretDB did not answer on the wire within 120s", last);
	}

	/** Initiates the single-node replica set and waits until the node is PRIMARY. */
	private static void initiateReplicaSet() throws Exception {
		exec(60, resolveCli(), "exec", containerId, "mongosh", "--quiet", "--eval", "rs.initiate()");
		long deadline = System.currentTimeMillis() + 60_000;
		while (System.currentTimeMillis() < deadline) {
			String primary = exec(20, resolveCli(), "exec", containerId, "mongosh", "--quiet",
					"--eval", "db.hello().isWritablePrimary");
			if (nonNull(primary) && primary.trim().endsWith("true")) {
				return;
			}
			Thread.sleep(500);
		}
		throw new IllegalStateException("Replica set did not reach PRIMARY within 60s");
	}

	static synchronized void shutdown() {
		if (nonNull(containerId)) {
			try {
				exec(30, resolveCli(), "rm", "-f", containerId);
			} catch (Exception e) {
				LOG.log(Level.FINE, "Failed to remove mongo container", e);
			}
			containerId = null;
		}
	}

	private static String exec(int timeoutSeconds, String... command) throws Exception {
		Process process = new ProcessBuilder(command).redirectErrorStream(false).start();
		if (!process.waitFor(timeoutSeconds, TimeUnit.SECONDS)) {
			process.destroyForcibly();
			throw new IllegalStateException("Command timed out: " + String.join(" ", command));
		}
		String stdout = new String(process.getInputStream().readAllBytes());
		if (process.exitValue() != 0) {
			String stderr = new String(process.getErrorStream().readAllBytes());
			throw new IllegalStateException("Command failed (" + process.exitValue() + "): "
					+ String.join(" ", command) + " — " + stderr.trim());
		}
		return stdout;
	}
}
