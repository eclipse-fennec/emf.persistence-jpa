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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Starts and reaps throwaway server containers for the TCK, through a local container CLI —
 * no client-library dependency needed (issue #134).
 * <p>
 * Backend-neutral on purpose. It grew inside {@code MongoTestSupport} and is factored out here
 * because the {@code backend × flavor} matrix needs the same three hard-won behaviours for the
 * JPA side: CLI resolution across docker and podman, a published-port lookup, and label-based
 * reaping of containers an earlier run leaked. What stays with each backend is only what is
 * genuinely specific — which image, which port, which readiness probe, how a connection string
 * is spelled.
 * <p>
 * The CLI is resolved once: the {@code <backend>.container.cli} system property wins, otherwise
 * {@code docker} is tried first and {@code podman} as fallback (relevant on macOS/Windows, where
 * podman ships without a docker shim).
 *
 * @author Mark Hoffmann
 * @since 14.08.2026
 */
final class ContainerHarness {

	private static final Logger LOG = Logger.getLogger(ContainerHarness.class.getName());

	/** Marks containers started by this harness, so orphans can be identified and reaped. */
	private static final String LABEL_OWNER = "fennec.persistence.tck";

	/** Carries the start time in epoch millis — the age filter of the reaper depends on it. */
	private static final String LABEL_STARTED = "fennec.persistence.tck.started";

	/**
	 * How long an orphan is left alone. Containers are removed on JVM shutdown, but a hard kill
	 * (a build timeout, {@code ^C}, an OOM) skips the hook and {@code --rm} only applies on
	 * stop — so the container keeps running, sometimes for days. Reaping is therefore done at
	 * startup, and the grace period keeps a concurrent run of the same flavor from having its
	 * server pulled out from under it: a suite takes minutes, never an hour.
	 */
	private static final long ORPHAN_GRACE_MILLIS = 60 * 60 * 1000L;

	private final String cliOverride;
	private volatile String cli;

	/**
	 * @param cliProperty name of the system property that pins the container CLI, e.g.
	 *            {@code mongo.container.cli}
	 */
	ContainerHarness(String cliProperty) {
		this.cliOverride = System.getProperty(cliProperty);
	}

	/**
	 * @return the usable container CLI
	 * @throws IllegalStateException if neither the override nor docker nor podman answers
	 */
	String cli() {
		if (nonNull(cli)) {
			return cli;
		}
		String[] candidates = nonNull(cliOverride) && !cliOverride.isBlank()
				? new String[] { cliOverride.trim() }
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

	/**
	 * Starts a detached container with a randomly published port, labelled so
	 * {@link #reapOrphans(String)} can find it again.
	 *
	 * @param spec what to start
	 * @return the container id
	 * @throws Exception if the CLI call fails or times out
	 */
	String run(ContainerSpec spec) throws Exception {
		List<String> command = new ArrayList<>(List.of(cli(), "run", "-d", "--rm",
				"-p", "127.0.0.1::" + spec.port(),
				"--label=" + LABEL_OWNER + "=" + spec.flavor(),
				"--label=" + LABEL_STARTED + "=" + System.currentTimeMillis()));
		for (Map.Entry<String, String> variable : spec.env().entrySet()) {
			command.add("-e");
			command.add(variable.getKey() + "=" + variable.getValue());
		}
		command.add(spec.image());
		command.addAll(spec.arguments());
		String id = exec(spec.startTimeoutSeconds(), command.toArray(String[]::new));
		return nonNull(id) ? id.trim() : null;
	}

	/**
	 * @param containerId the container to inspect
	 * @param containerPort the port inside the container
	 * @return the host port the container port is published on, or {@code null} if unpublished
	 * @throws Exception if the CLI call fails
	 */
	String publishedPort(String containerId, int containerPort) throws Exception {
		String mapping = exec(20, cli(), "port", containerId, containerPort + "/tcp");
		if (!nonNull(mapping) || !mapping.contains(":")) {
			return null;
		}
		String line = mapping.trim().lines().findFirst().orElse("");
		return line.substring(line.lastIndexOf(':') + 1);
	}

	/**
	 * Runs a command inside a started container — how readiness gets probed with tooling the
	 * image ships.
	 *
	 * @param timeoutSeconds how long to wait
	 * @param containerId the container to run in
	 * @param command the command and its arguments
	 * @return stdout
	 * @throws Exception if the command fails or times out
	 */
	String execInContainer(int timeoutSeconds, String containerId, String... command) throws Exception {
		List<String> full = new ArrayList<>(List.of(cli(), "exec", containerId));
		full.addAll(List.of(command));
		return exec(timeoutSeconds, full.toArray(String[]::new));
	}

	/**
	 * Removes containers this harness leaked in earlier runs for {@code flavor}, older than the
	 * grace period.
	 * <p>
	 * Best-effort throughout: reaping is housekeeping, so a failure here must never fail a test
	 * run — every error is logged at FINE and the suite proceeds with a fresh container. A
	 * container whose start-time label is missing or unparsable is left alone rather than
	 * guessed about.
	 *
	 * @param flavor the flavor whose leftovers to remove
	 */
	void reapOrphans(String flavor) {
		try {
			String listed = exec(30, cli(), "ps", "--all", "--quiet",
					"--filter", "label=" + LABEL_OWNER + "=" + flavor);
			long now = System.currentTimeMillis();
			listed.lines().map(String::trim).filter(id -> !id.isEmpty()).forEach(id -> {
				try {
					String startedAt = exec(20, cli(), "inspect", id,
							"--format", "{{index .Config.Labels \"" + LABEL_STARTED + "\"}}").trim();
					if (now - Long.parseLong(startedAt) < ORPHAN_GRACE_MILLIS) {
						return;
					}
					exec(30, cli(), "rm", "--force", id);
					LOG.log(Level.INFO, () -> "Removed leaked " + flavor + " test container " + id
							+ " from an earlier run");
				} catch (Exception e) {
					LOG.log(Level.FINE, () -> "Could not reap container " + id + ": " + e.getMessage());
				}
			});
		} catch (Exception e) {
			LOG.log(Level.FINE, () -> "Orphan reaping skipped: " + e.getMessage());
		}
	}

	/**
	 * Removes a container, best-effort — a leftover is reaped by the next run.
	 *
	 * @param containerId the container to remove
	 */
	void remove(String containerId) {
		try {
			exec(30, cli(), "rm", "-f", containerId);
		} catch (Exception e) {
			LOG.log(Level.FINE, "Failed to remove test container", e);
		}
	}

	/**
	 * @param timeoutSeconds how long the command may take
	 * @param command the command and its arguments
	 * @return stdout
	 * @throws Exception if the command exits non-zero or times out — stderr travels in the message
	 */
	String exec(int timeoutSeconds, String... command) throws Exception {
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
