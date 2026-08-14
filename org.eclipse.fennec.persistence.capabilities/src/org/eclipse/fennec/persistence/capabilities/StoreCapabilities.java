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

import java.util.Set;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The set of {@link StoreFeature}s a backend serves — store-dependent power that is neither
 * query vocabulary nor a command verb (issue #134, contract §5a).
 * <p>
 * This is the half of the declaration that the save path needs: {@code TRANSACTION_BRACKET}
 * decides whether cascade-delete has a convergence window (§4a), which has nothing to do with
 * holding a query or a command role.
 * <p>
 * A declaration states what the backend and flavor can do at all; a runtime probe may narrow it
 * for a concrete deployment, never widen it.
 *
 * @author Mark Hoffmann
 * @since 14.08.2026
 */
@ProviderType
public interface StoreCapabilities {

	/**
	 * @param feature the feature in question
	 * @return {@code true} if this backend serves {@code feature}
	 */
	boolean supports(StoreFeature feature);

	/**
	 * @return the immutable set of supported features
	 */
	Set<StoreFeature> supported();
}
