/*
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
 */
package org.eclipse.fennec.persistence.capabilities;

import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.osgi.annotation.versioning.ProviderType;

/**
 * The set of {@link CommandFeature}s a command resource serves (issue #114).
 * <p>
 * Two-level contract: {@link #supports(CommandFeature)} is the backend-wide answer — TRUE means
 * the feature exists in this backend/deployment at all;
 * {@link #supports(CommandFeature, EClass)} is the routing truth and may narrow it per type
 * (decision 2026-08-06: a backend that can serve a feature for some classes only declares it
 * backend-wide and narrows per EClass instead of answering conservatively). An undeclared command
 * is refused before any work with a Diagnostic naming the CommandFeature — 'refused' and 'failed'
 * stay distinguishable.
 * <p>
 * Plain Java rather than a modelled EClass — see {@link QueryCapabilities} for why.
 *
 * @author Mark Hoffmann
 * @since 14.08.2026
 */
@ProviderType
public interface CommandCapabilities {

	/**
	 * @param feature the feature in question
	 * @return {@code true} if this resource serves {@code feature} at all
	 */
	boolean supports(CommandFeature feature);

	/**
	 * The per-type routing answer. Defaults to the backend-wide answer; backends narrow it where
	 * the mapping decides — for example update only for materialized classes.
	 *
	 * @param feature the feature in question
	 * @param eClass the type the command targets
	 * @return {@code true} if this resource serves {@code feature} for {@code eClass}
	 */
	boolean supports(CommandFeature feature, EClass eClass);

	/**
	 * @return the immutable set of backend-wide supported features
	 */
	Set<CommandFeature> supported();
}
