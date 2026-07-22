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
package org.eclipse.fennec.persistence.mongo.query;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.model.utilities.FeaturePath;
import org.eclipse.fennec.persistence.mongo.MongoPersistenceConstants;

/**
 * Maps query model {@code FeaturePath}s to Mongo field names.
 * <p>
 * Rules, matching the codec/document layout of {@code MongoResourceImpl}:
 * <ul>
 * <li>The root object's EMF ID attribute is stored as {@code _id}; a depth-1 path
 * addressing an ID attribute therefore renders as {@code _id}.</li>
 * <li>Every other feature renders under its feature name.</li>
 * <li>Nested paths use Mongo dot notation — valid only through <em>containment</em>
 * references (embedded documents). Cross-document references are stored as proxy URIs
 * and cannot be traversed by a find filter (no join).</li>
 * </ul>
 *
 * @author Mark Hoffmann
 * @since 23.07.2026
 */
public final class MongoFieldNames {

	private MongoFieldNames() {
	}

	/**
	 * Renders a feature path as a Mongo field name in dot notation.
	 *
	 * @param path the path, must not be {@code null} or empty
	 * @return the field name, e.g. {@code "address.street"} or {@code "_id"}
	 */
	public static String render(FeaturePath path) {
		if (path == null || path.getFeature().isEmpty()) {
			throw new IllegalArgumentException("feature path must not be null or empty");
		}
		StringBuilder field = new StringBuilder();
		int size = path.getFeature().size();
		for (int i = 0; i < size; i++) {
			EStructuralFeature segment = path.getFeature().get(i);
			if (i > 0) {
				field.append('.');
			}
			field.append(fieldName(segment, i == 0 && size == 1));
		}
		return field.toString();
	}

	/**
	 * Renders a single feature as a Mongo field name (root level).
	 *
	 * @param feature the feature, must not be {@code null}
	 * @return {@code "_id"} for an ID attribute, the feature name otherwise
	 */
	public static String render(EStructuralFeature feature) {
		if (feature == null) {
			throw new IllegalArgumentException("feature must not be null");
		}
		return fieldName(feature, true);
	}

	/**
	 * Checks whether a path is traversable by a find filter: every intermediate segment
	 * must be a containment reference (embedded document).
	 *
	 * @param path the path to check
	 * @return {@code true} if all intermediate segments are containment references
	 */
	public static boolean isEmbeddedPath(FeaturePath path) {
		if (path == null) {
			return false;
		}
		for (int i = 0; i < path.getFeature().size() - 1; i++) {
			if (!(path.getFeature().get(i) instanceof EReference reference) || !reference.isContainment()) {
				return false;
			}
		}
		return true;
	}

	private static String fieldName(EStructuralFeature feature, boolean rootLevel) {
		if (rootLevel && feature instanceof EAttribute attribute && attribute.isID()) {
			return MongoPersistenceConstants.ID_FIELD;
		}
		return feature.getName();
	}
}
