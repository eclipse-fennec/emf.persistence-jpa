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
	private static final String IMAGE = System.getProperty("mongo.test.image", "docker.io/library/mongo:7");
	private static final String CLI_OVERRIDE = System.getProperty("mongo.container.cli");

	private static volatile String uri;
	private static volatile String containerId;
	private static volatile boolean initialized;
	private static volatile String cli;

	private MongoTestSupport() {
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
			String id = exec(180, resolveCli(), "run", "-d", "--rm",
					"-p", "127.0.0.1::27017", IMAGE);
			if (nonNull(id) && !id.isBlank()) {
				containerId = id.trim();
				String mapping = exec(20, resolveCli(), "port", containerId, "27017/tcp");
				if (nonNull(mapping) && mapping.contains(":")) {
					String port = mapping.trim().lines().findFirst().orElse("");
					port = port.substring(port.lastIndexOf(':') + 1);
					uri = "mongodb://127.0.0.1:" + port;
					Runtime.getRuntime().addShutdownHook(new Thread(MongoTestSupport::shutdown));
				}
			}
		} catch (Exception e) {
			LOG.log(Level.INFO, "No MongoDB available for TCK tests: " + e.getMessage());
		}
		return uri;
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
