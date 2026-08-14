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
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.eclipse.emf.ecore.EClass;

/**
 * Builds immutable {@link CommandCapabilities} declarations for a backend (issue #114).
 * <p>
 * Two-level contract: {@code supports(feature)} is the backend-wide answer;
 * {@code supports(feature, eClass)} is the routing truth and defaults to the
 * backend-wide answer unless the feature was declared with a {@link #narrow(
 * CommandFeature, Predicate) narrowing} predicate (e.g. update only for classes whose
 * mapping materializes the document). A narrowed feature still counts as backend-wide
 * supported — the conservative alternative would hide cases the backend serves.
 *
 * @author Mark Hoffmann
 * @since 06.08.2026
 */
public final class CommandCapabilitiesBuilder {

	private final EnumSet<CommandFeature> features = EnumSet.noneOf(CommandFeature.class);
	private final Map<CommandFeature, Predicate<EClass>> narrowings = new EnumMap<>(CommandFeature.class);

	private CommandCapabilitiesBuilder() {
	}

	/**
	 * @return a fresh builder with no supported features
	 */
	public static CommandCapabilitiesBuilder create() {
		return new CommandCapabilitiesBuilder();
	}

	/**
	 * Declares the given features as supported for every EClass.
	 *
	 * @param toSupport the features to add
	 * @return this builder
	 */
	public CommandCapabilitiesBuilder support(CommandFeature... toSupport) {
		for (CommandFeature feature : toSupport) {
			features.add(feature);
		}
		return this;
	}

	/**
	 * Declares the feature as backend-wide supported but narrowed per EClass: the
	 * routing answer {@code supports(feature, eClass)} follows the predicate.
	 *
	 * @param feature the feature to declare
	 * @param perClass the per-EClass routing predicate
	 * @return this builder
	 */
	public CommandCapabilitiesBuilder narrow(CommandFeature feature, Predicate<EClass> perClass) {
		features.add(feature);
		narrowings.put(feature, perClass);
		return this;
	}

	/**
	 * @return the immutable capability declaration
	 */
	public CommandCapabilities build() {
		Set<CommandFeature> supported = features.isEmpty()
				? Collections.emptySet()
				: Collections.unmodifiableSet(EnumSet.copyOf(features));
		Map<CommandFeature, Predicate<EClass>> perClass = Map.copyOf(narrowings);
		return new CommandCapabilities() {

			@Override
			public boolean supports(CommandFeature feature) {
				return supported.contains(feature);
			}

			@Override
			public boolean supports(CommandFeature feature, EClass eClass) {
				if (!supported.contains(feature)) {
					return false;
				}
				Predicate<EClass> narrowing = perClass.get(feature);
				return narrowing == null || narrowing.test(eClass);
			}

			@Override
			public Set<CommandFeature> supported() {
				return supported;
			}
		};
	}
}
