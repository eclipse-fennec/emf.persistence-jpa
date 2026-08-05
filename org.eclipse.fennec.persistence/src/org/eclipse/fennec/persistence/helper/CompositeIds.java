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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * The composite-id keyed-access contract (issue #109): the canonical fragment shape for
 * {@code Resource.getEObject} on EClasses with several {@code isID} attributes, shared
 * by every backend.
 * <p>
 * <b>Fragment shape:</b> {@code k1=v1,k2=v2} — one {@code name=value} pair per id
 * attribute, in <em>id-attribute declaration order</em> (all {@code isID} attributes in
 * {@code EClass.getEAllAttributes()} order — the same canonical ordering the eorm
 * composite-id analysis and the resulting descriptor primary-key fields use). Values are
 * the attributes' EMF string forms; {@code %}, {@code =} and {@code ,} inside a value
 * are percent-encoded ({@code %25}, {@code %3D}, {@code %2C}). Single-id classes keep
 * the existing bare-value fragment — this contract is additive.
 * <p>
 * OData's canonical composite-key syntax ({@code Set(k1=v1,k2=v2)}) maps 1:1 onto this
 * shape at the consumer boundary.
 *
 * @author Mark Hoffmann
 * @since 05.08.2026
 */
public final class CompositeIds {

	private CompositeIds() {
	}

	/**
	 * All {@code isID} attributes of the type, in the canonical (declaration) order the
	 * fragment contract and the eorm id analysis use.
	 *
	 * @param eClass the type, must not be {@code null}
	 * @return the id attributes; empty if the type declares none
	 */
	public static List<EAttribute> idAttributes(EClass eClass) {
		requireNonNull(eClass, "eClass must not be null");
		return eClass.getEAllAttributes().stream().filter(EAttribute::isID).toList();
	}

	/**
	 * @param eClass the type, must not be {@code null}
	 * @return {@code true} if the type declares more than one {@code isID} attribute
	 */
	public static boolean isComposite(EClass eClass) {
		return idAttributes(eClass).size() > 1;
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
		if (ids.size() < 2) {
			return EcoreUtil.getID(eObject);
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
		if (ids.size() < 2) {
			EcoreUtil.setID(eObject, fragment);
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
