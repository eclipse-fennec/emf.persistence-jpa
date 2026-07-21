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

import static java.util.Objects.nonNull;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Starts and stops MongoDB containers for the liveness integration tests through a local
 * container CLI — no client-library dependency needed. The CLI is resolved once:
 * {@code -Dmongo.container.cli=<name>} wins, otherwise {@code docker} is tried first and
 * {@code podman} as fallback (relevant on macOS/Windows, where podman ships without a
 * docker shim; the arguments are identical). Unlike the TCK support this variant
 * publishes on an explicit host port, so a container can be killed and replaced by a new
 * one on the same address. When no CLI is available the tests are skipped via JUnit
 * assumptions.
 */
final class MongoContainerSupport {

	private static final Logger LOG = Logger.getLogger(MongoContainerSupport.class.getName());
	private static final String IMAGE = System.getProperty("mongo.test.image", "docker.io/library/mongo:7");
	private static final String CLI_OVERRIDE = System.getProperty("mongo.container.cli");
	private static final String CLI_NONE = "";

	private static volatile String cli;

	private MongoContainerSupport() {
	}

	/**
	 * Returns {@code true} if a working container CLI (engine included) is available.
	 */
	static boolean containerCliAvailable() {
		return !CLI_NONE.equals(resolveCli());
	}

	private static synchronized String resolveCli() {
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
				LOG.log(Level.INFO, () -> "Using container CLI '" + candidate + "' for Mongo liveness tests");
				return cli;
			} catch (Exception e) {
				LOG.log(Level.FINE, () -> "Container CLI '" + candidate + "' not usable: " + e.getMessage());
			}
		}
		LOG.log(Level.INFO, "No container CLI available for Mongo liveness tests - tests will be skipped");
		cli = CLI_NONE;
		return cli;
	}

	/**
	 * Starts a MongoDB container publishing on {@code 127.0.0.1:<hostPort>} and returns
	 * the container id. Blocks until the container is created (including an image pull
	 * on first use).
	 */
	static String start(int hostPort) throws Exception {
		String id = exec(180, resolveCli(), "run", "-d", "--rm",
				"-p", "127.0.0.1:" + hostPort + ":27017", IMAGE);
		return id.trim();
	}

	/**
	 * Force-removes the container, freeing its published port.
	 */
	static void remove(String containerId) {
		if (nonNull(containerId)) {
			try {
				exec(30, resolveCli(), "rm", "-f", containerId);
			} catch (Exception e) {
				LOG.log(Level.FINE, "Failed to remove mongo container", e);
			}
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
