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
 * Three flavors, selected with {@code -Djpa.test.flavor} — an unknown id fails loudly, because
 * silently testing H2 while believing it is something else would make a green run meaningless:
 * <ul>
 * <li>{@code h2} (default) — in-memory, one database per persistence unit, no container. The fast
 *     path the ordinary build uses.</li>
 * <li>{@code postgres} — a container started through {@link ContainerHarness}, with one
 *     <em>schema</em> per persistence unit for isolation. Externally supplied via
 *     {@code -Djpa.jdbc.url} when a server already exists.</li>
 * <li>{@code mariadb} (issue #158) — a container with one <em>database</em> per persistence unit,
 *     because a MariaDB schema <em>is</em> a database; the TCK connects as root so it can create
 *     them. Started with {@code ONLY_FULL_GROUP_BY} appended to the default sql_mode — MariaDB
 *     does not enable it by default, and it is the independent check on GROUP BY rendering the
 *     flavor was chosen for.</li>
 * </ul>
 * Flavors of one backend must not differ in core conformance; where they do, it is a bug in that
 * flavor's mapping rather than a capability (§6). This class is what makes that claim measurable.
 * <p>
 * When a flavor's server cannot be provided this is an <b>error</b> (issue #173). An
 * unreachable backend is not a capability statement: the matrix workflow provisions the
 * container, so "not reachable" there is infrastructure failing, and a run that skipped
 * everything is indistinguishable from one that verified everything.
 * <p>
 * A skip stays available for the developer without a container runtime, but by explicit
 * opt-in only: {@code -Djpa.test.optional=true} (or {@code JPA_TEST_OPTIONAL=true}) restores
 * the JUnit assumption, with a message naming the flavor and what failed. Note that
 * {@code h2} can neither skip nor fail: it needs nothing.
 *
 * @author Mark Hoffmann
 * @since 14.08.2026
 */
final class JpaTestSupport {

	private static final Logger LOG = Logger.getLogger(JpaTestSupport.class.getName());

	static final String FLAVOR_H2 = "h2";
	static final String FLAVOR_POSTGRES = "postgres";
	static final String FLAVOR_MARIADB = "mariadb";

	private static final String FLAVOR =
			System.getProperty("jpa.test.flavor", FLAVOR_H2).trim().toLowerCase();

	/**
	 * PostgreSQL 17 — the version the DocumentDB gateway flavor also runs on, so the two
	 * PostgreSQL-backed halves of the matrix do not drift apart for an unrelated reason.
	 * MariaDB 11 — the current LTS line (issue #158). {@code -Djpa.test.image} overrides
	 * whichever image the configured flavor would use.
	 */
	private static final String POSTGRES_IMAGE =
			System.getProperty("jpa.test.image", "docker.io/library/postgres:17");
	private static final String MARIADB_IMAGE =
			System.getProperty("jpa.test.image", "docker.io/library/mariadb:11");

	private static final int POSTGRES_PORT = 5432;
	private static final int MARIADB_PORT = 3306;
	private static final String POSTGRES_DB = "fennec_tck";
	private static final String POSTGRES_USER = "fennec";
	private static final String POSTGRES_PASSWORD = "fennec";
	/** Root, deliberately: database-per-unit isolation needs CREATE DATABASE on a throwaway server. */
	private static final String MARIADB_USER = "root";
	private static final String MARIADB_PASSWORD = "fennec";

	/**
	 * Whether a missing server may skip instead of failing (issue #173) — opt-in, because the
	 * default has to be the safe one: a skip is indistinguishable from a pass, and in CI the
	 * container is provisioned by the workflow, so its absence is infrastructure rather than a
	 * capability statement.
	 *
	 * @see MongoTestSupport for the same rationale
	 */
	private static final boolean OPTIONAL = Boolean.parseBoolean(System.getProperty("jpa.test.optional",
			System.getenv().getOrDefault("JPA_TEST_OPTIONAL", "false")));

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

	static boolean isMariaDb() {
		return FLAVOR_MARIADB.equals(FLAVOR);
	}

	private static boolean isContainerFlavor() {
		return isPostgres() || isMariaDb();
	}

	/**
	 * The JDBC and dialect properties for one persistence unit, isolated from every other unit:
	 * H2 gets its own in-memory database, PostgreSQL its own schema in the shared container,
	 * MariaDB its own database — a MariaDB schema <em>is</em> a database (issue #158), so the
	 * isolation strategy is per flavor, not per backend.
	 * <p>
	 * Fails (or skips, under {@code jpa.test.optional=true}) when the configured flavor has no server.
	 *
	 * @param puName the persistence unit name, used to make the database or schema recognisable
	 * @return properties to merge into the persistence unit configuration
	 */
	static Map<String, Object> jdbcProperties(String puName) {
		return switch (FLAVOR) {
		case FLAVOR_H2 -> h2Properties(puName);
		case FLAVOR_POSTGRES -> postgresProperties(puName);
		case FLAVOR_MARIADB -> mariadbProperties(puName);
		// loud, like the mongo binding: silently running H2 while believing it is another
		// flavor would make a green run meaningless (the #154 lesson, twice over)
		default -> throw new IllegalArgumentException("Unknown -Djpa.test.flavor=" + FLAVOR);
		};
	}

	private static Map<String, Object> h2Properties(String puName) {
		Map<String, Object> props = new HashMap<>();
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

	private static Map<String, Object> postgresProperties(String puName) {
		String url = requireBaseUrl();
		String schema = identifierFor(puName, 63);
		execute(url, POSTGRES_USER, POSTGRES_PASSWORD, "CREATE SCHEMA IF NOT EXISTS \"" + schema + "\"");
		Map<String, Object> props = new HashMap<>();
		props.put(PersistenceUnitProperties.JDBC_DRIVER, "org.postgresql.Driver");
		props.put(PersistenceUnitProperties.JDBC_URL, url + "?currentSchema=" + schema);
		props.put(PersistenceUnitProperties.JDBC_USER, POSTGRES_USER);
		props.put(PersistenceUnitProperties.JDBC_PASSWORD, POSTGRES_PASSWORD);
		// the dialect is the point of this flavor — Auto would hide which platform was chosen
		props.put(PersistenceUnitProperties.TARGET_DATABASE, "PostgreSQL");
		return props;
	}

	private static Map<String, Object> mariadbProperties(String puName) {
		String base = requireBaseUrl();
		String database = identifierFor(puName, 64);
		execute(base, MARIADB_USER, MARIADB_PASSWORD, "CREATE DATABASE IF NOT EXISTS `" + database + "`");
		Map<String, Object> props = new HashMap<>();
		props.put(PersistenceUnitProperties.JDBC_DRIVER, "org.mariadb.jdbc.Driver");
		// nullDatabaseMeansCurrent: EclipseLink's filtered table creator lists existing
		// tables via getTables(catalog=null, …); with Connector/J 3.x defaults that means
		// ALL databases, so the second persistence unit sees the first unit's tables,
		// skips its own DDL and fails on the first SELECT. The parameter restores
		// database-local metadata — the semantics the per-unit isolation relies on.
		props.put(PersistenceUnitProperties.JDBC_URL,
				base + database + "?nullDatabaseMeansCurrent=true");
		props.put(PersistenceUnitProperties.JDBC_USER, MARIADB_USER);
		props.put(PersistenceUnitProperties.JDBC_PASSWORD, MARIADB_PASSWORD);
		// EclipseLink has a dedicated MariaDBPlatform — the issue-#158 guess of 'MySQL'
		// would silently run the wrong dialect; JpaTckSupport asserts the platform chosen
		props.put(PersistenceUnitProperties.TARGET_DATABASE, "MariaDB");
		return props;
	}

	/**
	 * A schema/database name per persistence unit, truncated to the flavor's identifier limit
	 * (PostgreSQL 63 bytes, MariaDB 64) — the kind of scalar the flavor axis produces, and the
	 * reason {@code StoreLimits} is on the roadmap.
	 */
	private static String identifierFor(String puName, int limit) {
		String sanitized = puName.toLowerCase().replaceAll("[^a-z0-9_]", "_");
		String suffix = "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
		int room = limit - suffix.length();
		return (sanitized.length() > room ? sanitized.substring(0, room) : sanitized) + suffix;
	}

	private static void execute(String url, String user, String password, String ddl) {
		try (Connection connection = DriverManager.getConnection(url, user, password);
				Statement statement = connection.createStatement()) {
			statement.execute(ddl);
		} catch (Exception e) {
			throw new IllegalStateException("Cannot prepare unit isolation for the JPA TCK: " + ddl, e);
		}
	}

	/**
	 * Returns the base JDBC URL, starting the container on first use. Skips or fails when none
	 * can be provided — the decision belongs here rather than in every test class.
	 */
	private static String requireBaseUrl() {
		String url = baseUrl();
		if (isNull(url)) {
			if (OPTIONAL) {
				Assumptions.abort(unavailableMessage());
			}
			throw new IllegalStateException("No " + FLAVOR + " database could be provided: "
					+ unavailableReason
					+ " — an unreachable backend is an error, not a capability statement."
					+ " Set -Djpa.test.optional=true to skip instead (issue #173).");
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
			containerId = CONTAINERS.run(containerSpec());
			if (nonNull(containerId) && !containerId.isBlank()) {
				String port = CONTAINERS.publishedPort(containerId, containerPort());
				if (nonNull(port)) {
					awaitReady();
					// the MariaDB base ends open — the per-unit database name is appended
					// by mariadbProperties; PostgreSQL isolates by schema inside one database
					baseUrl = isMariaDb()
							? "jdbc:mariadb://127.0.0.1:" + port + "/"
							: "jdbc:postgresql://127.0.0.1:" + port + "/" + POSTGRES_DB;
					Runtime.getRuntime().addShutdownHook(new Thread(JpaTestSupport::shutdown));
				}
			}
		} catch (Exception e) {
			unavailableReason = describe(e);
		}
		if (isNull(baseUrl) && isNull(unavailableReason)) {
			unavailableReason = "container started but no published port for " + containerPort() + "/tcp";
		}
		if (isNull(baseUrl)) {
			// WARNING, not INFO: this line explains a whole skipped suite, and Gradle does not
			// surface INFO from a forked test JVM
			LOG.log(Level.WARNING, () -> "No " + FLAVOR + " database for the TCK: " + unavailableReason);
		}
		return baseUrl;
	}

	private static ContainerSpec containerSpec() {
		if (isMariaDb()) {
			return ContainerSpec.of(FLAVOR, MARIADB_IMAGE, MARIADB_PORT)
					.withEnv(Map.of("MARIADB_ROOT_PASSWORD", MARIADB_PASSWORD,
							"MARIADB_DATABASE", POSTGRES_DB))
					// ONLY_FULL_GROUP_BY is not in MariaDB's default sql_mode — appended
					// deliberately: it is the independent check on GROUP BY rendering this
					// flavor was chosen for (#156, #158)
					.withArguments("--sql-mode=STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,"
							+ "NO_ENGINE_SUBSTITUTION,ONLY_FULL_GROUP_BY");
		}
		return ContainerSpec.of(FLAVOR, POSTGRES_IMAGE, POSTGRES_PORT)
				.withEnv(Map.of("POSTGRES_DB", POSTGRES_DB,
						"POSTGRES_USER", POSTGRES_USER,
						"POSTGRES_PASSWORD", POSTGRES_PASSWORD));
	}

	private static int containerPort() {
		return isMariaDb() ? MARIADB_PORT : POSTGRES_PORT;
	}

	/**
	 * Waits until the server accepts connections. {@code run -d} returns when the container
	 * exists, not when the server is listening, and both images restart the server once during
	 * initialisation — so the probe has to hold twice in a row before the URL is handed out,
	 * or the first suite hits the shutdown between the two starts. The probes are the images'
	 * own: {@code pg_isready} on PostgreSQL, {@code healthcheck.sh} on MariaDB (which only
	 * reports healthy once InnoDB finished initialising).
	 */
	private static void awaitReady() throws Exception {
		long deadline = System.currentTimeMillis() + 120_000;
		int consecutive = 0;
		Exception last = null;
		while (System.currentTimeMillis() < deadline) {
			try {
				if (isMariaDb()) {
					CONTAINERS.execInContainer(20, containerId, "healthcheck.sh",
							"--connect", "--innodb_initialized");
				} else {
					CONTAINERS.execInContainer(20, containerId, "pg_isready", "-U", POSTGRES_USER,
							"-d", POSTGRES_DB);
				}
				if (++consecutive == 2) {
					return;
				}
			} catch (Exception e) {
				last = e;
				consecutive = 0;
			}
			Thread.sleep(1_000);
		}
		throw new IllegalStateException(FLAVOR + " did not become ready within 120s", last);
	}

	/**
	 * The skip reason handed to the JUnit assumption — names the flavor and what actually failed,
	 * so a skipped suite is diagnosable from the test report alone.
	 */
	static String unavailableMessage() {
		String reason = unavailableReason;
		return "No " + FLAVOR + " database available for the TCK"
				+ (isNull(reason) ? "" : ": " + reason)
				+ " (skipping because -Djpa.test.optional=true; set -Djpa.jdbc.url to run it)";
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
