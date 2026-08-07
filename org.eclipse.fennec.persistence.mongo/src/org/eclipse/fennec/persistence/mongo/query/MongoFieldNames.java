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
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.persistence.helper.CompositeIds;
import org.eclipse.fennec.persistence.mongo.MongoPersistenceConstants;

/**
 * Maps expression-IR {@link PropertyPath}s to Mongo field names.
 * <p>
 * Rules, matching the codec/document layout of {@code MongoResourceImpl}:
 * <ul>
 * <li>The root object's EMF ID attribute is stored as {@code _id}; a depth-1 root path
 * addressing an ID attribute renders as {@code _id}.</li>
 * <li>Every other feature renders under its feature name; nested paths use dot notation
 * and are valid only through <em>containment</em> references (embedded documents).</li>
 * <li>Variable-based paths (inside quantifier predicates) render relative to the
 * element — no root prefix, no {@code _id} mapping.</li>
 * </ul>
 *
 * @author Mark Hoffmann
 * @since 24.07.2026
 */
public final class MongoFieldNames {

	private MongoFieldNames() {
	}

	/**
	 * Renders a root-based path as a Mongo field name in dot notation.
	 *
	 * @param path the path, must not be {@code null} or empty
	 * @return the field name, e.g. {@code "address.street"} or {@code "_id"}
	 */
	public static String render(PropertyPath path) {
		if (path == null || path.getSegments().isEmpty()) {
			throw new IllegalArgumentException("property path must not be null or empty");
		}
		boolean relative = path.getBase() != null;
		StringBuilder field = new StringBuilder();
		int size = path.getSegments().size();
		for (int i = 0; i < size; i++) {
			EStructuralFeature segment = path.getSegments().get(i);
			if (i > 0) {
				field.append('.');
			}
			field.append(fieldName(segment, !relative && i == 0 && size == 1));
		}
		return field.toString();
	}

	/**
	 * Checks whether a path is traversable by a find filter: every intermediate segment
	 * must be a containment reference (embedded document).
	 *
	 * @param path the path to check
	 * @return {@code true} if all intermediate segments are containment references
	 */
	public static boolean isEmbeddedPath(PropertyPath path) {
		if (path == null) {
			return false;
		}
		for (int i = 0; i < path.getSegments().size() - 1; i++) {
			if (!(path.getSegments().get(i) instanceof EReference reference) || !reference.isContainment()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether a quantifier source is an embedded (containment) collection.
	 *
	 * @param path the quantifier source path
	 * @return {@code true} if every segment, including the last, is a containment reference
	 */
	public static boolean isEmbeddedCollection(PropertyPath path) {
		if (path == null || path.getSegments().isEmpty()) {
			return false;
		}
		for (EStructuralFeature segment : path.getSegments()) {
			if (!(segment instanceof EReference reference) || !reference.isContainment()) {
				return false;
			}
		}
		return true;
	}

	private static String fieldName(EStructuralFeature feature, boolean rootLevel) {
		if (rootLevel && feature instanceof EAttribute attribute && attribute.isID()
				&& !CompositeIds.isComposite(attribute.getEContainingClass())) {
			// only the single id maps onto _id — composite components (issue #110,
			// idKeyMode BOTH) stay plain payload fields; collapsing them onto _id
			// would silently mis-filter
			return MongoPersistenceConstants.ID_FIELD;
		}
		return feature.getName();
	}
}
