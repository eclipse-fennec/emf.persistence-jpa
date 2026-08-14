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
package org.eclipse.fennec.persistence.capabilities;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Builds immutable {@link StoreCapabilities} declarations (issue #134).
 * <p>
 * {@link #from(StoreCapabilities)} plus {@link #exclude(StoreFeature...)} is the shape a probe
 * uses: start from what the flavor declares and take away what this deployment cannot do. The
 * builder offers no way to add to a declaration it started from, because a probe may only
 * narrow — see {@link StoreCapabilities}.
 *
 * @author Mark Hoffmann
 * @since 14.08.2026
 */
public final class StoreCapabilitiesBuilder {

	private final EnumSet<StoreFeature> features = EnumSet.noneOf(StoreFeature.class);

	private StoreCapabilitiesBuilder() {
	}

	/**
	 * @return a fresh builder with no supported features
	 */
	public static StoreCapabilitiesBuilder create() {
		return new StoreCapabilitiesBuilder();
	}

	/**
	 * @param base the declaration to start from
	 * @return a builder carrying everything {@code base} declares
	 */
	public static StoreCapabilitiesBuilder from(StoreCapabilities base) {
		StoreCapabilitiesBuilder builder = new StoreCapabilitiesBuilder();
		builder.features.addAll(base.supported());
		return builder;
	}

	/**
	 * @param toSupport the features to declare
	 * @return this builder
	 */
	public StoreCapabilitiesBuilder support(StoreFeature... toSupport) {
		for (StoreFeature feature : toSupport) {
			features.add(feature);
		}
		return this;
	}

	/**
	 * Takes features away — how a probe narrows a declaration for the deployment at hand.
	 *
	 * @param toExclude the features this deployment does not serve
	 * @return this builder
	 */
	public StoreCapabilitiesBuilder exclude(StoreFeature... toExclude) {
		for (StoreFeature feature : toExclude) {
			features.remove(feature);
		}
		return this;
	}

	/**
	 * @return the immutable capability declaration
	 */
	public StoreCapabilities build() {
		Set<StoreFeature> supported = features.isEmpty()
				? Collections.emptySet()
				: Collections.unmodifiableSet(EnumSet.copyOf(features));
		return new StoreCapabilities() {

			@Override
			public boolean supports(StoreFeature feature) {
				return supported.contains(feature);
			}

			@Override
			public Set<StoreFeature> supported() {
				return supported;
			}
		};
	}
}
