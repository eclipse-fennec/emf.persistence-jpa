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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.namespace.PackageNamespace;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.test.common.annotation.InjectBundleContext;

/**
 * OSGi integration test for the package wiring between the Mongo bundle and the codec
 * (issue #117).
 * <p>
 * {@code MongoResourceImpl} bridges BsonDocument reading and writing through
 * {@code FormatDelegateGenerator}/{@code FormatDelegateParser}. Those types moved from
 * {@code org.eclipse.fennec.codec.format.impl} to the exported
 * {@code org.eclipse.fennec.codec.format.jackson} in eclipse-fennec/emf.codec#59, and
 * {@code format.impl} lost its export in the same step.
 * <p>
 * A compile against a stale snapshot, or a codec that hides the package again, breaks this
 * silently: the bundle simply fails to resolve, its DS components are never registered, and
 * every other test only reports a service that did not appear. This test names the actual
 * cause instead - it asserts the wire, its provider, and that the classes really load.
 */
public class MongoCodecWiringTest {

	private static final String MONGO_BSN = "org.eclipse.fennec.persistence.mongo";
	private static final String CODEC_BSN = "org.eclipse.fennec.codec";
	private static final String JACKSON_PACKAGE = "org.eclipse.fennec.codec.format.jackson";
	private static final String IMPL_PACKAGE = "org.eclipse.fennec.codec.format.impl";

	@Test
	public void mongoBundleWiresTheCodecJacksonPackage(@InjectBundleContext BundleContext ctx) throws Exception {
		Bundle mongo = bundle(ctx, MONGO_BSN);
		assertNotNull(mongo, "The " + MONGO_BSN + " bundle must be part of the runtime");

		BundleWiring wiring = mongo.adapt(BundleWiring.class);
		assertNotNull(wiring, "The " + MONGO_BSN + " bundle is not resolved (state "
				+ mongo.getState() + ") - an Import-Package requirement is unsatisfied");

		List<BundleWire> packageWires = wiring.getRequiredWires(PackageNamespace.PACKAGE_NAMESPACE);

		Optional<BundleWire> jackson = wire(packageWires, JACKSON_PACKAGE);
		assertTrue(jackson.isPresent(), "The Mongo bundle must import " + JACKSON_PACKAGE
				+ ", but its codec package wires are: " + codecPackages(packageWires));
		assertEquals(CODEC_BSN, jackson.get().getProviderWiring().getBundle().getSymbolicName(),
				JACKSON_PACKAGE + " must be provided by " + CODEC_BSN);

		assertTrue(wire(packageWires, IMPL_PACKAGE).isEmpty(),
				IMPL_PACKAGE + " is no longer exported by the codec - nothing may wire to it");

		// the wire alone says the resolver was happy; loading proves the types are really there
		ClassLoader loader = wiring.getClassLoader();
		assertNotNull(loader.loadClass(JACKSON_PACKAGE + ".FormatDelegateGenerator"));
		assertNotNull(loader.loadClass(JACKSON_PACKAGE + ".FormatDelegateParser"));
	}

	private static Bundle bundle(BundleContext ctx, String symbolicName) {
		for (Bundle candidate : ctx.getBundles()) {
			if (symbolicName.equals(candidate.getSymbolicName())) {
				return candidate;
			}
		}
		return null;
	}

	private static Optional<BundleWire> wire(List<BundleWire> packageWires, String packageName) {
		return packageWires.stream()
				.filter(w -> packageName.equals(
						w.getCapability().getAttributes().get(PackageNamespace.PACKAGE_NAMESPACE)))
				.findFirst();
	}

	private static String codecPackages(List<BundleWire> packageWires) {
		return packageWires.stream()
				.map(w -> String.valueOf(
						w.getCapability().getAttributes().get(PackageNamespace.PACKAGE_NAMESPACE)))
				.filter(name -> name.startsWith(CODEC_BSN))
				.sorted()
				.collect(Collectors.joining(", "));
	}
}
