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
package org.eclipse.fennec.persistence.helper;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * Recognising an {@code EMap} and reading its parts — the one answer every layer needs and
 * none may guess differently (contract §9.2, issue #185).
 * <p>
 * A map is not a distinct construct in Ecore. It is a containment-many {@link EReference}
 * whose type is an {@link EClass} carrying {@code key} and {@code value} features and the
 * instance class name {@value #MAP_ENTRY_CLASS_NAME}. The instance class name is not
 * decoration: {@code EStructuralFeatureImpl} picks the {@code EMAP} setting delegate on
 * {@code dataClass == Map.Entry.class}, so without it the very same model is a plain list
 * and {@code eGet} hands out an {@code EList} rather than an {@code EMap}.
 * <p>
 * The fallback on {@code key}/{@code value} features mirrors what the codec does
 * ({@code EMapHelper} in emf.codec), so a model the codec treats as a map is a map here too.
 *
 * @author Mark Hoffmann
 * @since 19.08.2026
 */
public final class EMaps {

	/** The instance class name EMF puts on a map entry class. */
	public static final String MAP_ENTRY_CLASS_NAME = "java.util.Map$Entry";

	/** The name of the key feature of a map entry class. */
	public static final String KEY_FEATURE = "key";

	/** The name of the value feature of a map entry class. */
	public static final String VALUE_FEATURE = "value";

	private EMaps() {
	}

	/**
	 * Whether the given feature is a map — a containment-many reference to a map entry class.
	 *
	 * @param feature the feature to test; may be {@code null}
	 * @return {@code true} if the feature carries an {@code EMap}
	 */
	public static boolean isMap(EStructuralFeature feature) {
		return feature instanceof EReference reference
				&& reference.isMany()
				&& reference.isContainment()
				&& isMapEntry(reference.getEReferenceType());
	}

	/**
	 * Whether the given class is a map entry class.
	 *
	 * @param eClass the class to test; may be {@code null}
	 * @return {@code true} for a {@code Map.Entry} typed class, or one carrying both a
	 *         {@code key} and a {@code value} feature
	 */
	public static boolean isMapEntry(EClass eClass) {
		if (eClass == null) {
			return false;
		}
		if (MAP_ENTRY_CLASS_NAME.equals(eClass.getInstanceClassName())) {
			return true;
		}
		return eClass.getEStructuralFeature(KEY_FEATURE) != null
				&& eClass.getEStructuralFeature(VALUE_FEATURE) != null;
	}

	/**
	 * The key feature of a map entry class.
	 *
	 * @param entryClass the entry class; may be {@code null}
	 * @return the {@code key} feature, or {@code null} when there is none
	 */
	public static EStructuralFeature keyFeature(EClass entryClass) {
		return entryClass == null ? null : entryClass.getEStructuralFeature(KEY_FEATURE);
	}

	/**
	 * The value feature of a map entry class.
	 *
	 * @param entryClass the entry class; may be {@code null}
	 * @return the {@code value} feature, or {@code null} when there is none
	 */
	public static EStructuralFeature valueFeature(EClass entryClass) {
		return entryClass == null ? null : entryClass.getEStructuralFeature(VALUE_FEATURE);
	}

	/**
	 * The entry class of a map feature.
	 *
	 * @param feature the map feature; may be {@code null}
	 * @return the entry class, or {@code null} when the feature is not a map
	 */
	public static EClass entryClass(EStructuralFeature feature) {
		return isMap(feature) ? ((EReference) feature).getEReferenceType() : null;
	}

	/**
	 * Renders a key value into its stored form — the name under which the entry is addressed
	 * (a field name on document stores, a column value on relational ones). The inverse of
	 * {@link #parseKey(EClass, String)}.
	 *
	 * @param entryClass the entry class, must not be {@code null}
	 * @param key the key value; may be {@code null}
	 * @return the string form, or {@code null} for a {@code null} key
	 */
	public static String renderKey(EClass entryClass, Object key) {
		if (key == null) {
			return null;
		}
		EStructuralFeature keyFeature = keyFeature(entryClass);
		if (keyFeature instanceof EAttribute attribute) {
			return EcoreUtil.convertToString(attribute.getEAttributeType(), key);
		}
		return key.toString();
	}

	/**
	 * Parses a stored key back into the key feature's type. Returns {@code null} when the key
	 * type cannot make that round trip — the caller refuses with a diagnostic rather than
	 * storing an approximation (contract §9.2).
	 *
	 * @param entryClass the entry class, must not be {@code null}
	 * @param key the stored key form; may be {@code null}
	 * @return the typed key value, or {@code null} when it cannot be parsed
	 */
	public static Object parseKey(EClass entryClass, String key) {
		if (key == null) {
			return null;
		}
		EStructuralFeature keyFeature = keyFeature(entryClass);
		if (!(keyFeature instanceof EAttribute attribute)) {
			return key;
		}
		try {
			return EcoreUtil.createFromString(attribute.getEAttributeType(), key);
		} catch (RuntimeException e) {
			return null;
		}
	}
}
