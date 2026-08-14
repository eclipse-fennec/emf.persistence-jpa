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

import java.util.List;
import java.util.Map;

/**
 * What {@link ContainerHarness#run(ContainerSpec)} needs to start one throwaway server
 * (issue #134).
 *
 * @param flavor the flavor id this container serves — also the reap label, so leftovers of one
 *            flavor never take another flavor's server down
 * @param image the image reference, digest-pinned in CI and floating on the weekly drift run
 * @param port the port inside the container that gets published to a random host port
 * @param env environment variables the image requires
 * @param arguments arguments appended after the image, for a server that needs a flag
 * @param startTimeoutSeconds how long {@code run} may take — a cold pull of a database image is
 *            minutes, not seconds
 * @author Mark Hoffmann
 * @since 14.08.2026
 */
record ContainerSpec(String flavor, String image, int port, Map<String, String> env,
		List<String> arguments, int startTimeoutSeconds) {

	/**
	 * @param flavor the flavor id
	 * @param image the image reference
	 * @param port the container port to publish
	 * @return a spec with no environment, no arguments and a five-minute start budget
	 */
	static ContainerSpec of(String flavor, String image, int port) {
		return new ContainerSpec(flavor, image, port, Map.of(), List.of(), 300);
	}

	/**
	 * @param variables the environment to pass
	 * @return a copy carrying {@code variables}
	 */
	ContainerSpec withEnv(Map<String, String> variables) {
		return new ContainerSpec(flavor, image, port, Map.copyOf(variables), arguments, startTimeoutSeconds);
	}

	/**
	 * @param args the arguments to append after the image
	 * @return a copy carrying {@code args}
	 */
	ContainerSpec withArguments(String... args) {
		return new ContainerSpec(flavor, image, port, env, List.of(args), startTimeoutSeconds);
	}

	/**
	 * @param seconds the start budget
	 * @return a copy with that budget
	 */
	ContainerSpec withStartTimeout(int seconds) {
		return new ContainerSpec(flavor, image, port, env, arguments, seconds);
	}
}
