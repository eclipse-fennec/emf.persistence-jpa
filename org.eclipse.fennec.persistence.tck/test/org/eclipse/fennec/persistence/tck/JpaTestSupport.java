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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.jupiter.api.Assumptions;

/**
 * Provides the database the JPA side of the TCK runs against — the {@code flavor} half of the
 * {@code backend × flavor} matrix (issue #134, contract §6).
 * <p>
 * Two flavors, selected with {@code -Djpa.test.flavor}:
 * <ul>
 * <li>{@code h2} (default) — in-memory, one database per persistence unit, no container. The fast
 *     path the ordinary build uses.</li>
 * <li>{@code postgres} — a container started through {@link ContainerHarness}, with one
 *     <em>schema</em> per persistence unit for isolation. Externally supplied via
 *     {@code -Djpa.jdbc.url} (plus {@code -Djpa.jdbc.user} / {@code -Djpa.jdbc.password}) when a
 *     server already exists.</li>
 * </ul>
 * Flavors of one backend must not differ in core conformance; where they do, it is a bug in that
 * flavor's mapping rather than a capability (§6). This class is what makes that claim measurable.
 * <p>
 * When a flavor's server cannot be provided the JPA tests are <b>skipped</b> through a JUnit
 * assumption naming what failed — and {@code -Djpa.require=true} (or {@code JPA_REQUIRE=true})
 * turns that into a hard failure, because a silently skipped suite is indistinguishable from a
 * passing one (issue #132). Note that {@code h2} can never skip: it needs nothing.
 *
 * @author Mark Hoffmann
 * @since 14.08.2026
 */
final class JpaTestSupport {

	private static final Logger LOG = Logger.getLogger(JpaTestSupport.class.getName());

	static final String FLAVOR_H2 = "h2";
	static final String FLAVOR_POSTGRES = "postgres";

	private static final String FLAVOR =
			System.getProperty("jpa.test.flavor", FLAVOR_H2).trim().toLowerCase();

	/**
	 * PostgreSQL 17 — the version the DocumentDB gateway flavor also runs on, so the two
	 * PostgreSQL-backed halves of the matrix do not drift apart for an unrelated reason.
	 */
	private static final String POSTGRES_IMAGE =
			System.getProperty("jpa.test.image", "docker.io/library/postgres:17");

	private static final int POSTGRES_PORT = 5432;
	private static final String POSTGRES_DB = "fennec_tck";
	private static final String POSTGRES_USER = "fennec";
	private static final String POSTGRES_PASSWORD = "fennec";

	/** @see MongoTestSupport for the same rationale — a skip must be distinguishable from a pass */
	private static final boolean REQUIRED = Boolean.parseBoolean(
			System.getProperty("jpa.require", System.getenv().getOrDefault("JPA_REQUIRE", "false")));

	private static final ContainerHarness CONTAINERS = new ContainerHarness("jpa.container.cli");

	private static volatile String baseUrl;
	private static volatile String containerId;
	private static volatile boolean initialized;
	private static volatile String unavailableReason;

	private JpaTestSupport() {
	}

	/** @return the flavor id under test, as configured by {@code -Djpa.test.flavor} */
	static String flavor() {
		return FLAVOR;
	}

	static boolean isPostgres() {
		return FLAVOR_POSTGRES.equals(FLAVOR);
	}

	/**
	 * The JDBC and dialect properties for one persistence unit, isolated from every other unit:
	 * H2 gets its own in-memory database, PostgreSQL its own schema in the shared container.
	 * <p>
	 * Skips (or fails, under {@code jpa.require=true}) when the configured flavor has no server.
	 *
	 * @param puName the persistence unit name, used to make the database or schema recognisable
	 * @return properties to merge into the persistence unit configuration
	 */
	static Map<String, Object> jdbcProperties(String puName) {
		Map<String, Object> props = new HashMap<>();
		if (!isPostgres()) {
			props.put(PersistenceUnitProperties.JDBC_DRIVER, "org.h2.Driver");
			props.put(PersistenceUnitProperties.JDBC_URL,
					"jdbc:h2:mem:" + puName + "_" + UUID.randomUUID());
			props.put(PersistenceUnitProperties.JDBC_USER, "sa");
			props.put(PersistenceUnitProperties.JDBC_PASSWORD, "");
			// H2 stays on Auto: it is the flavor every other suite has always run on, and
			// pinning the platform here would change the baseline rather than the new flavor
			props.put(PersistenceUnitProperties.TARGET_DATABASE, "Auto");
			return props;
		}
		String url = requireBaseUrl();
		String schema = schemaFor(puName);
		createSchema(url, schema);
		props.put(PersistenceUnitProperties.JDBC_DRIVER, "org.postgresql.Driver");
		props.put(PersistenceUnitProperties.JDBC_URL, url + "?currentSchema=" + schema);
		props.put(PersistenceUnitProperties.JDBC_USER, POSTGRES_USER);
		props.put(PersistenceUnitProperties.JDBC_PASSWORD, POSTGRES_PASSWORD);
		// the dialect is the point of this flavor — Auto would hide which platform was chosen
		props.put(PersistenceUnitProperties.TARGET_DATABASE, "PostgreSQL");
		return props;
	}

	/**
	 * A schema name per persistence unit. Truncated to stay inside PostgreSQL's 63-byte
	 * identifier limit — the kind of scalar the flavor axis produces, and the reason
	 * {@code StoreLimits} is on the roadmap.
	 */
	private static String schemaFor(String puName) {
		String sanitized = puName.toLowerCase().replaceAll("[^a-z0-9_]", "_");
		String suffix = "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
		int room = 63 - suffix.length();
		return (sanitized.length() > room ? sanitized.substring(0, room) : sanitized) + suffix;
	}

	private static void createSchema(String url, String schema) {
		try (Connection connection = DriverManager.getConnection(url, POSTGRES_USER, POSTGRES_PASSWORD);
				Statement statement = connection.createStatement()) {
			statement.execute("CREATE SCHEMA IF NOT EXISTS \"" + schema + "\"");
		} catch (Exception e) {
			throw new IllegalStateException("Cannot create schema " + schema + " for the JPA TCK", e);
		}
	}

	/**
	 * Returns the base JDBC URL, starting the container on first use. Skips or fails when none
	 * can be provided — the decision belongs here rather than in every test class.
	 */
	private static String requireBaseUrl() {
		String url = baseUrl();
		if (isNull(url)) {
			if (REQUIRED) {
				throw new IllegalStateException("jpa.require=true but no " + FLAVOR
						+ " database could be provided: " + unavailableReason);
			}
			Assumptions.abort(unavailableMessage());
		}
		return url;
	}

	private static synchronized String baseUrl() {
		if (initialized) {
			return baseUrl;
		}
		initialized = true;
		String external = System.getProperty("jpa.jdbc.url", System.getenv("JPA_JDBC_URL"));
		if (nonNull(external) && !external.isBlank()) {
			baseUrl = external;
			return baseUrl;
		}
		try {
			CONTAINERS.reapOrphans(FLAVOR);
			containerId = CONTAINERS.run(ContainerSpec.of(FLAVOR, POSTGRES_IMAGE, POSTGRES_PORT)
					.withEnv(Map.of("POSTGRES_DB", POSTGRES_DB,
							"POSTGRES_USER", POSTGRES_USER,
							"POSTGRES_PASSWORD", POSTGRES_PASSWORD)));
			if (nonNull(containerId) && !containerId.isBlank()) {
				String port = CONTAINERS.publishedPort(containerId, POSTGRES_PORT);
				if (nonNull(port)) {
					awaitReady();
					baseUrl = "jdbc:postgresql://127.0.0.1:" + port + "/" + POSTGRES_DB;
					Runtime.getRuntime().addShutdownHook(new Thread(JpaTestSupport::shutdown));
				}
			}
		} catch (Exception e) {
			unavailableReason = describe(e);
		}
		if (isNull(baseUrl) && isNull(unavailableReason)) {
			unavailableReason = "container started but no published port for " + POSTGRES_PORT + "/tcp";
		}
		if (isNull(baseUrl)) {
			// WARNING, not INFO: this line explains a whole skipped suite, and Gradle does not
			// surface INFO from a forked test JVM
			LOG.log(Level.WARNING, () -> "No " + FLAVOR + " database for the TCK: " + unavailableReason);
		}
		return baseUrl;
	}

	/**
	 * Waits until PostgreSQL accepts connections. {@code run -d} returns when the container
	 * exists, not when the server is listening, and the image restarts the server once during
	 * initialisation — so {@code pg_isready} has to hold twice in a row before the URL is handed
	 * out, or the first suite hits the shutdown between the two starts.
	 */
	private static void awaitReady() throws Exception {
		long deadline = System.currentTimeMillis() + 120_000;
		int consecutive = 0;
		Exception last = null;
		while (System.currentTimeMillis() < deadline) {
			try {
				CONTAINERS.execInContainer(20, containerId, "pg_isready", "-U", POSTGRES_USER,
						"-d", POSTGRES_DB);
				if (++consecutive == 2) {
					return;
				}
			} catch (Exception e) {
				last = e;
				consecutive = 0;
			}
			Thread.sleep(1_000);
		}
		throw new IllegalStateException("PostgreSQL did not become ready within 120s", last);
	}

	/**
	 * The skip reason handed to the JUnit assumption — names the flavor and what actually failed,
	 * so a skipped suite is diagnosable from the test report alone.
	 */
	static String unavailableMessage() {
		String reason = unavailableReason;
		return "No " + FLAVOR + " database available for the TCK"
				+ (isNull(reason) ? "" : ": " + reason)
				+ " (set -Djpa.jdbc.url, or -Djpa.require=true to fail instead of skipping)";
	}

	/** Unwraps the cause chain — the useful text is usually in the innermost message. */
	private static String describe(Throwable failure) {
		StringBuilder text = new StringBuilder(String.valueOf(failure.getMessage()));
		for (Throwable cause = failure.getCause(); nonNull(cause); cause = cause.getCause()) {
			text.append(" <- ").append(cause.getMessage());
		}
		return text.toString();
	}

	static synchronized void shutdown() {
		if (nonNull(containerId)) {
			CONTAINERS.remove(containerId);
			containerId = null;
		}
	}
}
