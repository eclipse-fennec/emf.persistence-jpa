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
package org.eclipse.fennec.persistence.query.support;

import static java.util.Objects.requireNonNull;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.model.stream.ChangeEntry;
import org.eclipse.fennec.model.stream.ChangeSet;
import org.eclipse.fennec.model.stream.DeltaKind;
import org.eclipse.fennec.persistence.query.QueryException;

/**
 * The patch-apply engine behind {@code UpdateCommand} (concept §14, §18.1): applies a
 * {@link ChangeSet} <em>template</em> to one matched {@link EObject}.
 * <p>
 * Template semantics differ from change-stream replay: entry coordinates address
 * features of the <em>matched object's</em> type — {@code objectId} is resolved per
 * match and therefore ignored here, {@code featureId} is the EMF feature id within the
 * target's {@link EClass} (see {@link EClass#getFeatureID(EStructuralFeature)}), and
 * values travel as EMF string literals decoded through the type's
 * {@code EFactory#createFromString}.
 * <p>
 * Supported kinds (v1): {@link DeltaKind#SET SET} / {@link DeltaKind#UNSET UNSET} on
 * single-valued attributes, {@link DeltaKind#ADD ADD} / {@link DeltaKind#REMOVE REMOVE}
 * / {@link DeltaKind#MOVE MOVE} on many-valued attributes. Reference patching and the
 * map/array kinds need id resolution respectively addressed storage and are refused with
 * a precise message — never silently skipped. {@code valueOld} is <em>not</em> evaluated
 * as an optimistic guard: a template applies to many matches whose current values differ
 * by design.
 *
 * @author Mark Hoffmann
 * @since 24.07.2026
 */
public final class ChangeTemplates {

	private ChangeTemplates() {
	}

	/**
	 * Pre-flight check of a template against the selector's root type, so commands are
	 * refused before any match is loaded.
	 *
	 * @param template the change template, must not be {@code null}
	 * @param type the root type the selector matches, must not be {@code null}
	 * @throws QueryException if any entry is not applicable to {@code type}
	 */
	public static void validate(ChangeSet template, EClass type) throws QueryException {
		requireNonNull(template, "template must not be null");
		requireNonNull(type, "type must not be null");
		if (template.getEntries().isEmpty()) {
			throw new QueryException("Update template has no entries — nothing to apply");
		}
		for (ChangeEntry entry : template.getEntries()) {
			checkEntry(entry, type);
		}
	}

	/**
	 * Applies every entry of the template to the target, in template order.
	 *
	 * @param template the change template, must not be {@code null}
	 * @param target the matched object to patch, must not be {@code null}
	 * @throws QueryException if an entry is not applicable to the target's type or a
	 *         value literal cannot be decoded
	 */
	public static void apply(ChangeSet template, EObject target) throws QueryException {
		requireNonNull(template, "template must not be null");
		requireNonNull(target, "target must not be null");
		for (ChangeEntry entry : template.getEntries()) {
			applyEntry(entry, target);
		}
	}

	private static void applyEntry(ChangeEntry entry, EObject target) throws QueryException {
		EAttribute attribute = checkEntry(entry, target.eClass());
		switch (entry.getKind()) {
		case SET -> target.eSet(attribute, decode(attribute, entry.getValueNew()));
		case UNSET -> target.eUnset(attribute);
		case ADD -> {
			List<Object> values = manyValues(target, attribute);
			Object value = decode(attribute, entry.getValueNew());
			if (entry.getIndex() >= 0) {
				values.add(entry.getIndex(), value);
			} else {
				values.add(value);
			}
		}
		case REMOVE -> {
			List<Object> values = manyValues(target, attribute);
			if (entry.getIndex() >= 0) {
				values.remove(entry.getIndex());
			} else if (!values.remove(decode(attribute, entry.getValueOld()))) {
				throw new QueryException("REMOVE on '" + attribute.getName() + "': value '"
						+ entry.getValueOld() + "' is not present");
			}
		}
		case MOVE -> {
			if (entry.getIndex() < 0 || entry.getToIndex() < 0) {
				throw new QueryException("MOVE on '" + attribute.getName()
						+ "' requires index and toIndex");
			}
			List<Object> values = manyValues(target, attribute);
			values.add(entry.getToIndex(), values.remove(entry.getIndex()));
		}
		default -> throw new QueryException("Unsupported template kind " + entry.getKind());
		}
	}

	/**
	 * Validates a single entry against the type and resolves its feature. Returns the
	 * resolved attribute (never {@code null}).
	 */
	private static EAttribute checkEntry(ChangeEntry entry, EClass type) throws QueryException {
		DeltaKind kind = entry.getKind();
		switch (kind) {
		case SET, UNSET, ADD, REMOVE, MOVE -> { /* supported below */ }
		default -> throw new QueryException("Unsupported template kind " + kind
				+ " — templates patch features; object lifecycle belongs to Insert/Delete commands");
		}
		EStructuralFeature feature = type.getEStructuralFeature(entry.getFeatureId());
		if (feature == null) {
			throw new QueryException("Unknown feature id " + entry.getFeatureId()
					+ " for type '" + type.getName() + "'");
		}
		if (!(feature instanceof EAttribute attribute)) {
			throw new QueryException("Feature '" + feature.getName() + "' of '" + type.getName()
					+ "' is a reference — reference patching is not supported yet");
		}
		if (!feature.isChangeable()) {
			throw new QueryException("Feature '" + feature.getName() + "' of '" + type.getName()
					+ "' is not changeable");
		}
		boolean many = feature.isMany();
		if (many && (kind == DeltaKind.SET || kind == DeltaKind.UNSET)) {
			throw new QueryException(kind + " addresses the many-valued feature '"
					+ feature.getName() + "' — use ADD/REMOVE/MOVE");
		}
		if (!many && (kind == DeltaKind.ADD || kind == DeltaKind.REMOVE || kind == DeltaKind.MOVE)) {
			throw new QueryException(kind + " addresses the single-valued feature '"
					+ feature.getName() + "' — use SET/UNSET");
		}
		return attribute;
	}

	private static Object decode(EAttribute attribute, String literal) throws QueryException {
		if (literal == null) {
			return null;
		}
		try {
			return EcoreUtil.createFromString(attribute.getEAttributeType(), literal);
		} catch (RuntimeException e) {
			throw new QueryException("Cannot decode literal '" + literal + "' for feature '"
					+ attribute.getName() + "': " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private static List<Object> manyValues(EObject target, EAttribute attribute) {
		return (List<Object>) target.eGet(attribute);
	}
}
