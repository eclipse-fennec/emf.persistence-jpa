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

import org.osgi.annotation.versioning.ProviderType;

/**
 * The set of {@link QueryFeature}s a query processor serves natively, plus structural limits.
 * <p>
 * Used to validate a query before translation, so unsupported constructs are rejected with a
 * Diagnostic rather than silently post-filtered in memory.
 * <p>
 * Plain Java rather than a modelled EClass (issue #134, contract §5a): a capability is a value
 * that gets asked, not an EObject that gets loaded. Only the vocabulary it answers over —
 * {@link QueryFeature} — stays modelled, because that vocabulary is the contract and nobody
 * extends it from outside.
 *
 * @author Mark Hoffmann
 * @since 14.08.2026
 */
@ProviderType
public interface QueryCapabilities {

	/**
	 * @param feature the feature in question
	 * @return {@code true} if this backend serves {@code feature} natively
	 */
	boolean supports(QueryFeature feature);

	/**
	 * @return the immutable set of natively supported features
	 */
	Set<QueryFeature> supported();

	/**
	 * The maximum FeaturePath depth this backend can traverse in where/sort/projection — each
	 * segment beyond the first is a join.
	 *
	 * @return {@code 1} for local features only, {@code -1} for unlimited
	 */
	int maxFeaturePathDepth();
}
