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
package org.eclipse.fennec.model.query.analysis;

import java.util.List;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * One feature path occurrence in a query, with the role it plays (issue #292). Intermediate
 * segments are the navigated references, the last segment is the feature the path addresses.
 *
 * @param role the role of the occurrence, never {@code null}
 * @param segments the navigation, root feature first
 * @author Mark Hoffmann
 * @since 23.09.2026
 */
public record PathUse(FeatureRole role, List<EStructuralFeature> segments) {

	/**
	 * Creates a path use.
	 */
	public PathUse {
		segments = List.copyOf(segments);
	}

	/**
	 * Returns the addressed feature.
	 *
	 * @return the last segment, or {@code null} for an empty path
	 */
	public EStructuralFeature feature() {
		return segments.isEmpty() ? null : segments.get(segments.size() - 1);
	}

}
