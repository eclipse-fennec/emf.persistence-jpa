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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Provides a MongoDB instance for the TCK:
 * <ol>
 * <li>an externally supplied one via {@code -Dmongo.uri=...} / {@code MONGO_URI}, or</li>
 * <li>a container started through a local container CLI — no client-library dependency
 *     needed. The CLI is resolved once: {@code -Dmongo.container.cli=<name>} wins,
 *     otherwise {@code docker} is tried first and {@code podman} as fallback (relevant
 *     on macOS/Windows, where podman ships without a docker shim).</li>
 * </ol>
 * When neither is available this is an <b>error</b> (issue #173). An unreachable backend is
 * not a capability statement: the matrix workflow provisions the container, so "not reachable"
 * there is infrastructure failing, and a suite that skipped everything is indistinguishable
 * from one that verified everything.
 * <p>
 * A skip stays available for the developer without a container runtime, but by explicit
 * opt-in only: {@code -Dmongo.test.optional=true} (or {@code MONGO_TEST_OPTIONAL=true})
 * restores the JUnit assumption, with a message naming the flavor and what actually failed.
 *
 * @author Mark Hoffmann
 * @since 16.07.2026
 */
final class MongoTestSupport {

	private static final Logger LOG = Logger.getLogger(MongoTestSupport.class.getName());

	/**
	 * Container plumbing shared with the JPA side of the {@code backend × flavor} matrix — CLI
	 * resolution, published-port lookup and orphan reaping live there now (issue #134).
	 */
	private static final ContainerHarness CONTAINERS = new ContainerHarness("mongo.container.cli");

	/**
	 * The server flavor under test (issues #119/#122): {@code mongo} (default),
	 * {@code ferretdb} or {@code documentdb-pg}. The wire protocol is identical, so the whole
	 * suite runs unchanged — what differs is how the container starts, on which port, whether
	 * it needs TLS, and which capabilities the backend may declare.
	 */
	private static final String FLAVOR = System.getProperty("mongo.test.flavor", "mongo").trim().toLowerCase();

	static final String FLAVOR_MONGO = "mongo";
	static final String FLAVOR_FERRETDB = "ferretdb";
	static final String FLAVOR_DOCUMENTDB_PG = "documentdb-pg";

	/**
	 * FerretDB 2.x bundles PostgreSQL, the DocumentDB extension and the wire gateway in this
	 * single evaluation image, which keeps the harness at one container.
	 */
	private static final String FERRETDB_IMAGE = "ghcr.io/ferretdb/ferretdb-eval:2";

	/** Mandatory for the FerretDB image — without it the container exits immediately. */
	private static final String FERRETDB_PASSWORD = "fennec";

	/**
	 * The Microsoft/Linux-Foundation DocumentDB emulator: PostgreSQL 17 with the DocumentDB
	 * extension plus the {@code documentdb_gateway}.
	 */
	private static final String DOCUMENTDB_IMAGE = "ghcr.io/microsoft/documentdb/documentdb-local:latest";

	/** The gateway's own port — not 27017. */
	private static final int DOCUMENTDB_PORT = 10260;

	/** Image defaults; the gateway creates this role on first start. */
	private static final String DOCUMENTDB_USER = "default_user";
	private static final String DOCUMENTDB_PASSWORD = "Admin100";

	private static final String IMAGE = System.getProperty("mongo.test.image", defaultImage());

	/** The container port the wire protocol is served on, per flavor. */
	private static final int WIRE_PORT = isDocumentDbPg() ? DOCUMENTDB_PORT : 27017;

	/**
	 * Turns "no server" from a skip into a failure (issue #132). A skipped suite and a
	 * passing one are indistinguishable in a green build, so CI — and anyone who wants to
	 * know the suite really ran — sets this and gets an exception instead of assumptions.
	 */
	/**
	 * Whether a missing server may skip instead of failing (issue #173) — opt-in, because a
	 * skip is indistinguishable from a pass and CI provisions the container itself.
	 */
	private static final boolean OPTIONAL = Boolean.parseBoolean(System.getProperty("mongo.test.optional",
			System.getenv().getOrDefault("MONGO_TEST_OPTIONAL", "false")));

	private static volatile String uri;
	private static volatile String containerId;
	private static volatile boolean initialized;
	/** Why {@link #connectionString()} gave up, for the diagnostic the skip message carries. */
	private static volatile String unavailableReason;

	private MongoTestSupport() {
	}

	/** @return the flavor id under test, as configured by {@code -Dmongo.test.flavor} */
	static String flavor() {
		return FLAVOR;
	}

	static boolean isFerretDb() {
		return FLAVOR_FERRETDB.equals(FLAVOR);
	}

	static boolean isDocumentDbPg() {
		return FLAVOR_DOCUMENTDB_PG.equals(FLAVOR);
	}

	/** True for any PostgreSQL-backed wire gateway — never a replica set, so no transactions. */
	static boolean isGateway() {
		return isFerretDb() || isDocumentDbPg();
	}

	private static String defaultImage() {
		if (isFerretDb()) {
			return FERRETDB_IMAGE;
		}
		return isDocumentDbPg() ? DOCUMENTDB_IMAGE : "docker.io/library/mongo:7";
	}


	/**
	 * Returns the connection string, starting a container on first use; {@code null} if
	 * unavailable under {@code mongo.test.optional=true}; otherwise throws. The failure is raised on
	 * <em>every</em> call rather than only the first, so a broken setup fails the whole
	 * suite uniformly instead of reddening one test and skipping the rest.
	 */
	static synchronized String connectionString() {
		if (initialized) {
			return requireIfDemanded();
		}
		initialized = true;
		String external = System.getProperty("mongo.uri", System.getenv("MONGO_URI"));
		if (nonNull(external) && !external.isBlank()) {
			uri = external;
			return uri;
		}
		try {
			CONTAINERS.reapOrphans(FLAVOR);
			String id = startContainer();
			if (nonNull(id) && !id.isBlank()) {
				containerId = id.trim();
				String port = CONTAINERS.publishedPort(containerId, WIRE_PORT);
				if (nonNull(port)) {
					if (isGateway()) {
						awaitWireProtocol();
					} else {
						initiateReplicaSet();
					}
					uri = connectionUri(port);
					Runtime.getRuntime().addShutdownHook(new Thread(MongoTestSupport::shutdown));
				}
			}
		} catch (Exception e) {
			unavailableReason = describe(e);
		}
		if (isNull(uri) && isNull(unavailableReason)) {
			// startContainer returned nothing usable without throwing — say so rather than
			// leaving the caller with an unexplained skip
			unavailableReason = "container started but no published port for " + WIRE_PORT + "/tcp";
		}
		if (isNull(uri)) {
			// WARNING, not INFO: this is the line that explains a whole skipped suite, and
			// Gradle does not surface INFO from a forked test JVM
			LOG.log(Level.WARNING, () -> "No " + FLAVOR + " server for the TCK: " + unavailableReason);
			return requireIfDemanded();
		}
		return uri;
	}

	/**
	 * Returns {@code uri}, or throws when no server could be provided (issue #173). Only
	 * under {@code mongo.test.optional=true} does the {@code null} travel on to the
	 * assumption, which skips with {@link #unavailableMessage()}.
	 */
	private static String requireIfDemanded() {
		if (isNull(uri) && !OPTIONAL) {
			throw new IllegalStateException("No " + FLAVOR + " server could be provided: "
					+ unavailableReason
					+ " — an unreachable backend is an error, not a capability statement."
					+ " Set -Dmongo.test.optional=true to skip instead (issue #173).");
		}
		return uri;
	}

	/**
	 * The skip reason handed to the JUnit assumption — names the flavor and what actually
	 * failed, so a skipped suite is diagnosable from the test report alone.
	 */
	static String unavailableMessage() {
		String reason = unavailableReason;
		return "No " + FLAVOR + " server available for the TCK"
				+ (isNull(reason) ? "" : ": " + reason)
				+ " (skipping because -Dmongo.test.optional=true; set -Dmongo.uri to run it)";
	}

	/** Unwraps the cause chain — the useful text is usually in the innermost message. */
	private static String describe(Throwable failure) {
		StringBuilder text = new StringBuilder(String.valueOf(failure.getMessage()));
		for (Throwable cause = failure.getCause(); nonNull(cause); cause = cause.getCause()) {
			text.append(" <- ").append(cause.getMessage());
		}
		return text.toString();
	}

	/**
	 * Starts the server for the configured flavor.
	 * <p>
	 * MongoDB runs as a single-node replica set: functionally identical for plain operations,
	 * and it unlocks multi-document transactions for the command-bracket TCK cases
	 * (issue #112). The gateways get no replica set — they are a single logical server, so
	 * {@code rs.initiate()} does not apply, which is exactly why {@code TRANSACTION_BRACKET}
	 * stays undeclared there (the runtime probe in {@code MongoResourceImpl.capabilities()}
	 * handles that on its own).
	 */
	private static String startContainer() throws Exception {
		ContainerSpec spec = ContainerSpec.of(FLAVOR, IMAGE, WIRE_PORT);
		if (isFerretDb()) {
			return CONTAINERS.run(spec.withEnv(Map.of("POSTGRES_PASSWORD", FERRETDB_PASSWORD)));
		}
		if (isDocumentDbPg()) {
			// ALLOW_EXTERNAL_CONNECTIONS defaults to false — without it the gateway binds
			// container-locally and the published port refuses every connection
			return CONTAINERS.run(spec.withEnv(Map.of("ALLOW_EXTERNAL_CONNECTIONS", "true",
					"ENFORCE_SSL", "false")));
		}
		return CONTAINERS.run(spec.withArguments("--replSet", "rs0").withStartTimeout(180));
	}


	/**
	 * The driver connection string for the started container.
	 * <p>
	 * Both gateways authenticate: FerretDB against the PostgreSQL role, the DocumentDB
	 * emulator against a role it creates on first start. The DocumentDB gateway additionally
	 * enforces TLS ({@code ENFORCE_SSL=true}) with a self-signed certificate, hence
	 * {@code tlsAllowInvalidCertificates} — acceptable for a throwaway test container, and
	 * called out in the user guide so nobody copies it into production.
	 */
	private static String connectionUri(String port) {
		if (isFerretDb()) {
			return "mongodb://postgres:" + FERRETDB_PASSWORD + "@127.0.0.1:" + port + "/";
		}
		if (isDocumentDbPg()) {
			// No TLS: the container runs with ENFORCE_SSL=false. The gateway's default
			// certificate is self-signed, and the Java driver validates the chain regardless
			// of connection-string options — tlsInsecure and tlsAllowInvalidCertificates do
			// not disable PKIX validation for it (the latter is not even a Java driver
			// option), so trusting it would mean a truststore. For a throwaway test container
			// turning TLS off is the honest simplification; the user guide shows the
			// certificate route for real deployments.
			// serverSelectionTimeoutMS keeps a broken setup a fast failure instead of a
			// half-hour hang across the suite.
			return "mongodb://" + DOCUMENTDB_USER + ":" + DOCUMENTDB_PASSWORD + "@127.0.0.1:" + port
					+ "/?directConnection=true&serverSelectionTimeoutMS=5000";
		}
		return "mongodb://127.0.0.1:" + port + "/?directConnection=true";
	}

	/**
	 * Waits until the gateway answers on the wire. Both images start PostgreSQL, install the
	 * DocumentDB extension and only then open the wire port, so a connection attempt right
	 * after {@code run} is refused.
	 * <p>
	 * Probed with {@code mongosh} inside the container for FerretDB, which ships it. The
	 * DocumentDB image does not, so its readiness is probed from the outside with the driver
	 * itself — which also proves the TLS handshake works before the suite starts.
	 */
	private static void awaitWireProtocol() throws Exception {
		long deadline = System.currentTimeMillis() + 180_000;
		Exception last = null;
		while (System.currentTimeMillis() < deadline) {
			try {
				if (isFerretDb()) {
					CONTAINERS.execInContainer(20, containerId, "mongosh",
							"mongodb://postgres:" + FERRETDB_PASSWORD + "@127.0.0.1:27017/", "--quiet",
							"--eval", "db.adminCommand({ping:1})");
				} else {
					probeWithDriver();
				}
				return;
			} catch (Exception e) {
				last = e;
				Thread.sleep(2_000);
			}
		}
		throw new IllegalStateException(FLAVOR + " did not answer on the wire within 180s", last);
	}

	/**
	 * Waits until mongod accepts connections, before {@code rs.initiate()} is attempted.
	 * <p>
	 * {@code docker run -d} returns as soon as the container exists, not when the process
	 * inside it is listening, so initiating the replica set right away raced mongod's
	 * startup and failed with {@code ECONNREFUSED 127.0.0.1:27017}. The gateways always had
	 * {@link #awaitWireProtocol()}; plain MongoDB had no such wait because no CI job ever
	 * ran it (issue #132) — locally the image is warm and the race rarely lost.
	 * <p>
	 * A {@code ping} against {@code admin} is the right probe: a mongod started with
	 * {@code --replSet} answers it while still refusing writes, which is exactly the state
	 * {@code rs.initiate()} needs.
	 */
	private static void awaitMongodListening() throws Exception {
		long deadline = System.currentTimeMillis() + 120_000;
		Exception last = null;
		while (System.currentTimeMillis() < deadline) {
			try {
				CONTAINERS.execInContainer(20, containerId, "mongosh", "--quiet",
						"--eval", "db.adminCommand({ping:1})");
				return;
			} catch (Exception e) {
				last = e;
				Thread.sleep(1_000);
			}
		}
		throw new IllegalStateException("mongod did not accept connections within 120s", last);
	}

	/** Pings the published port with the Mongo driver, TLS handshake included. */
	private static void probeWithDriver() throws Exception {
		String port = CONTAINERS.publishedPort(containerId, WIRE_PORT);
		try (MongoClient probe = MongoClients.create(connectionUri(port))) {
			probe.getDatabase("admin").runCommand(new Document("ping", 1));
		}
	}

	/** Initiates the single-node replica set and waits until the node is PRIMARY. */
	private static void initiateReplicaSet() throws Exception {
		awaitMongodListening();
		CONTAINERS.execInContainer(60, containerId, "mongosh", "--quiet", "--eval", "rs.initiate()");
		long deadline = System.currentTimeMillis() + 60_000;
		while (System.currentTimeMillis() < deadline) {
			String primary = CONTAINERS.execInContainer(20, containerId, "mongosh", "--quiet",
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
			CONTAINERS.remove(containerId);
			containerId = null;
		}
	}
}
