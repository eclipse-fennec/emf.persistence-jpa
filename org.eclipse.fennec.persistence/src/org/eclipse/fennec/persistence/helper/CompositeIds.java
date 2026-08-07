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

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * The composite-id keyed-access contract (issue #109): the canonical fragment shape for
 * {@code Resource.getEObject} on EClasses with a composite identity, shared by every
 * backend.
 * <p>
 * <b>Identity declaration (issue #115):</b> composite identity is declared explicitly
 * via the {@value #ANNOTATION_SOURCE} EAnnotation, detail {@value #ID_FEATURES} — a
 * comma-separated list of attribute names in canonical (key) order. Without the
 * annotation the identity is EMF's: the single {@code eID} attribute. Several
 * {@code isID} attributes without the annotation are <em>refused</em> — Ecore itself
 * allows at most one ({@code validateEClass_AtMostOneID}), so that shape is an invalid
 * model, not a composite declaration. The vocabulary deliberately mirrors the emf.codec
 * {@code idFeatures} annotation (source {@code http://eclipse.org/fennec/codec}) but
 * lives under its own source: how an object serializes to JSON and how it maps to a
 * database are separate concerns and need not be congruent.
 * <p>
 * <b>Fragment shape:</b> {@code k1=v1,k2=v2} — one {@code name=value} pair per id
 * attribute, in the canonical declaration order (the same ordering the eorm composite-id
 * analysis and the resulting descriptor primary-key fields use). Values are the
 * attributes' EMF string forms; {@code %}, {@code =} and {@code ,} inside a value are
 * percent-encoded ({@code %25}, {@code %3D}, {@code %2C}). Single-id classes keep the
 * existing bare-value fragment — this contract is additive.
 * <p>
 * OData's canonical composite-key syntax ({@code Set(k1=v1,k2=v2)}) maps 1:1 onto this
 * shape at the consumer boundary.
 *
 * @author Mark Hoffmann
 * @since 05.08.2026
 */
public final class CompositeIds {

	/** The annotation source of the persistence identity vocabulary (issue #115). */
	public static final String ANNOTATION_SOURCE = "http://eclipse.org/fennec/persistence/1.0";

	/** Annotation detail: comma-separated id attribute names in canonical (key) order. */
	public static final String ID_FEATURES = "idFeatures";

	private CompositeIds() {
	}

	/**
	 * The id attributes of the type in canonical order: the {@value #ID_FEATURES}
	 * annotation when declared, otherwise EMF's single {@code eID} attribute.
	 *
	 * @param eClass the type, must not be {@code null}
	 * @return the id attributes; empty if the type declares none
	 * @throws IllegalStateException if the annotation names an unknown or non-attribute
	 *         feature, or the type declares several {@code isID} attributes without the
	 *         annotation — invalid Ecore ({@code validateEClass_AtMostOneID}), not a
	 *         composite declaration
	 */
	public static List<EAttribute> idAttributes(EClass eClass) {
		requireNonNull(eClass, "eClass must not be null");
		String declared = annotatedIdFeatures(eClass);
		if (declared != null) {
			List<EAttribute> ids = new ArrayList<>();
			for (String name : declared.split(",")) {
				EStructuralFeature feature = eClass.getEStructuralFeature(name.trim());
				if (!(feature instanceof EAttribute attribute)) {
					throw new IllegalStateException("The " + ID_FEATURES + " annotation of '"
							+ eClass.getName() + "' names '" + name.trim()
							+ "' which is not an EAttribute of the type");
				}
				ids.add(attribute);
			}
			return List.copyOf(ids);
		}
		List<EAttribute> flagged = eClass.getEAllAttributes().stream().filter(EAttribute::isID).toList();
		if (flagged.size() > 1) {
			throw new IllegalStateException("EClass '" + eClass.getName() + "' declares "
					+ flagged.size() + " isID attributes — Ecore allows at most one"
					+ " (validateEClass_AtMostOneID). Declare composite identity via the '"
					+ ID_FEATURES + "' annotation (source " + ANNOTATION_SOURCE + ")");
		}
		return flagged;
	}

	/**
	 * @param eClass the type, must not be {@code null}
	 * @return {@code true} if the type's canonical identity has several components
	 */
	public static boolean isComposite(EClass eClass) {
		return idAttributes(eClass).size() > 1;
	}

	private static String annotatedIdFeatures(EClass eClass) {
		EAnnotation annotation = eClass.getEAnnotation(ANNOTATION_SOURCE);
		if (annotation == null) {
			return null;
		}
		String declared = annotation.getDetails().get(ID_FEATURES);
		return declared == null || declared.isBlank() ? null : declared;
	}

	/**
	 * The keyed-access fragment of the object: the bare id value for single-id classes
	 * (the existing contract, via {@link EcoreUtil#getID}), the {@code k1=v1,k2=v2}
	 * shape for composite-id classes.
	 *
	 * @param eObject the object, must not be {@code null}
	 * @return the fragment, or {@code null} if the type declares no id or a composite
	 *         component is unset
	 */
	public static String fragment(EObject eObject) {
		requireNonNull(eObject, "eObject must not be null");
		List<EAttribute> ids = idAttributes(eObject.eClass());
		if (ids.isEmpty()) {
			return EcoreUtil.getID(eObject);
		}
		if (ids.size() == 1) {
			// the annotation may declare a single id that is not the eID attribute;
			// eIsSet mirrors EcoreUtil.getID — an unset primitive id (e.g. a proxy
			// stub's int default) yields no fragment
			EAttribute id = ids.get(0);
			return eObject.eIsSet(id)
					? EcoreUtil.convertToString(id.getEAttributeType(), eObject.eGet(id))
					: null;
		}
		StringBuilder fragment = new StringBuilder();
		for (EAttribute id : ids) {
			Object value = eObject.eGet(id);
			if (value == null) {
				return null;
			}
			if (fragment.length() > 0) {
				fragment.append(',');
			}
			fragment.append(id.getName()).append('=')
					.append(encode(EcoreUtil.convertToString(id.getEAttributeType(), value)));
		}
		return fragment.toString();
	}

	/**
	 * Whether the fragment uses the composite {@code k1=v1,...} shape.
	 *
	 * @param fragment the fragment; may be {@code null}
	 * @return {@code true} for the composite shape
	 */
	public static boolean isCompositeFragment(String fragment) {
		return fragment != null && fragment.indexOf('=') >= 0;
	}

	/**
	 * Parses a composite fragment against the type: returns the <em>decoded string
	 * values</em> in canonical id-attribute order. Pairs may arrive in any order; every
	 * id attribute must be present exactly once.
	 *
	 * @param eClass the type, must not be {@code null}
	 * @param fragment the {@code k1=v1,k2=v2} fragment, must not be {@code null}
	 * @return the component values in {@link #idAttributes(EClass)} order
	 * @throws IllegalArgumentException if the fragment names unknown attributes, misses
	 *         one, or the type is not composite
	 */
	public static List<String> parse(EClass eClass, String fragment) {
		requireNonNull(fragment, "fragment must not be null");
		List<EAttribute> ids = idAttributes(eClass);
		if (ids.size() < 2) {
			throw new IllegalArgumentException("Type '" + eClass.getName()
					+ "' has no composite id — the fragment is the bare id value");
		}
		List<String> values = new ArrayList<>(ids.size());
		for (EAttribute id : ids) {
			values.add(null);
		}
		for (String pair : fragment.split(",", -1)) {
			int separator = pair.indexOf('=');
			if (separator <= 0) {
				throw new IllegalArgumentException("Malformed composite id fragment '" + fragment
						+ "' — expected k1=v1,k2=v2");
			}
			String name = pair.substring(0, separator);
			int index = indexOf(ids, name);
			if (index < 0) {
				throw new IllegalArgumentException("Composite id fragment names unknown id attribute '"
						+ name + "' of '" + eClass.getName() + "' (id attributes: "
						+ ids.stream().map(EAttribute::getName).toList() + ")");
			}
			if (values.get(index) != null) {
				throw new IllegalArgumentException("Composite id fragment repeats attribute '" + name + "'");
			}
			values.set(index, decode(pair.substring(separator + 1)));
		}
		int missing = values.indexOf(null);
		if (missing >= 0) {
			throw new IllegalArgumentException("Composite id fragment '" + fragment + "' misses id attribute '"
					+ ids.get(missing).getName() + "' of '" + eClass.getName() + "'");
		}
		return values;
	}

	/**
	 * Sets all id components on the object from a keyed-access fragment — the composite
	 * counterpart of {@link EcoreUtil#setID}; delegates to it for single-id classes.
	 *
	 * @param eObject the object, must not be {@code null}
	 * @param fragment the fragment, must not be {@code null}
	 */
	public static void setId(EObject eObject, String fragment) {
		requireNonNull(eObject, "eObject must not be null");
		List<EAttribute> ids = idAttributes(eObject.eClass());
		if (ids.isEmpty()) {
			EcoreUtil.setID(eObject, fragment);
			return;
		}
		if (ids.size() == 1) {
			EAttribute id = ids.get(0);
			eObject.eSet(id, EcoreUtil.createFromString(id.getEAttributeType(), fragment));
			return;
		}
		List<String> values = parse(eObject.eClass(), fragment);
		for (int i = 0; i < ids.size(); i++) {
			EAttribute id = ids.get(i);
			eObject.eSet(id, EcoreUtil.createFromString(id.getEAttributeType(), values.get(i)));
		}
	}

	private static int indexOf(List<EAttribute> ids, String name) {
		for (int i = 0; i < ids.size(); i++) {
			if (ids.get(i).getName().equals(name)) {
				return i;
			}
		}
		return -1;
	}

	private static String encode(String value) {
		return value.replace("%", "%25").replace("=", "%3D").replace(",", "%2C");
	}

	private static String decode(String value) {
		return value.replace("%2C", ",").replace("%3D", "=").replace("%25", "%");
	}
}
