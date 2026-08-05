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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.helper.CompositeIds;
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
 * Supported kinds: {@link DeltaKind#SET SET} / {@link DeltaKind#UNSET UNSET} on
 * single-valued features, {@link DeltaKind#ADD ADD} / {@link DeltaKind#REMOVE REMOVE}
 * on many-valued features, {@link DeltaKind#MOVE MOVE} on many-valued attributes.
 * <p>
 * <b>Reference patching (issue #107):</b> entries addressing a non-containment
 * {@link EReference} carry the <em>target's id</em> as their value — the stream model's
 * values are "encoded literals or object IDs" by design — and resolve through the
 * backend's {@link ReferenceResolver}: SET binds the resolved target (unresolvable id →
 * refusal), ADD appends the resolved member, REMOVE removes <em>by id identity</em>
 * ({@code valueOld}), never by index — robust against concurrent reordering. Containment
 * references are object lifecycle and stay refused (Insert/Delete commands), as do MOVE
 * on references and the map/array kinds — never silently skipped. {@code valueOld} is
 * <em>not</em> evaluated as an optimistic guard: a template applies to many matches
 * whose current values differ by design.
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
	 * Applies every entry of the template to the target, in template order. Templates
	 * containing reference entries need the resolver overload
	 * ({@link #apply(ChangeSet, EObject, ReferenceResolver)}).
	 *
	 * @param template the change template, must not be {@code null}
	 * @param target the matched object to patch, must not be {@code null}
	 * @throws QueryException if an entry is not applicable to the target's type or a
	 *         value literal cannot be decoded
	 */
	public static void apply(ChangeSet template, EObject target) throws QueryException {
		apply(template, target, (reference, id) -> {
			throw new QueryException("Reference '" + reference.getName()
					+ "' needs id resolution, but no ReferenceResolver is available"
					+ " — use apply(template, target, resolver)");
		});
	}

	/**
	 * Applies every entry of the template to the target, in template order, resolving
	 * reference-target ids through the backend's keyed-find contract (issue #107).
	 *
	 * @param template the change template, must not be {@code null}
	 * @param target the matched object to patch, must not be {@code null}
	 * @param resolver resolves reference-target ids, must not be {@code null}
	 * @throws QueryException if an entry is not applicable to the target's type, a value
	 *         literal cannot be decoded or a reference target cannot be resolved
	 */
	public static void apply(ChangeSet template, EObject target, ReferenceResolver resolver)
			throws QueryException {
		requireNonNull(template, "template must not be null");
		requireNonNull(target, "target must not be null");
		requireNonNull(resolver, "resolver must not be null");
		for (ChangeEntry entry : template.getEntries()) {
			applyEntry(entry, target, resolver);
		}
	}

	private static void applyEntry(ChangeEntry entry, EObject target, ReferenceResolver resolver)
			throws QueryException {
		EStructuralFeature feature = checkEntry(entry, target.eClass());
		if (feature instanceof EReference reference) {
			applyReferenceEntry(entry, target, reference, resolver);
			return;
		}
		EAttribute attribute = (EAttribute) feature;
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

	/** Reference entries carry target ids and bind via the resolver (issue #107). */
	private static void applyReferenceEntry(ChangeEntry entry, EObject target, EReference reference,
			ReferenceResolver resolver) throws QueryException {
		switch (entry.getKind()) {
		case SET -> {
			if (entry.getValueNew() == null) {
				target.eSet(reference, null);
			} else {
				target.eSet(reference, resolve(reference, entry.getValueNew(), resolver));
			}
		}
		case UNSET -> target.eUnset(reference);
		case ADD -> {
			List<Object> members = manyValues(target, reference);
			if (entry.getValueNew() == null) {
				throw new QueryException("ADD on reference '" + reference.getName()
						+ "' requires the member's id as valueNew");
			}
			EObject member = resolve(reference, entry.getValueNew(), resolver);
			if (entry.getIndex() >= 0) {
				members.add(entry.getIndex(), member);
			} else {
				members.add(member);
			}
		}
		case REMOVE -> {
			// by id identity, never by index — robust against concurrent reordering
			if (entry.getValueOld() == null) {
				throw new QueryException("REMOVE on reference '" + reference.getName()
						+ "' is by id — set the member's id as valueOld (index removal is"
						+ " not supported for references)");
			}
			List<Object> members = manyValues(target, reference);
			boolean removed = members.removeIf(member -> member instanceof EObject object
					&& entry.getValueOld().equals(idOf(object)));
			if (!removed) {
				throw new QueryException("REMOVE on '" + reference.getName() + "': no member with id '"
						+ entry.getValueOld() + "'");
			}
		}
		default -> throw new QueryException("Unsupported template kind " + entry.getKind()
				+ " for reference '" + reference.getName() + "'");
		}
	}

	private static EObject resolve(EReference reference, String id, ReferenceResolver resolver)
			throws QueryException {
		EObject resolved = resolver.resolve(reference, id);
		if (resolved == null) {
			throw new QueryException("Reference '" + reference.getName() + "': no "
					+ reference.getEReferenceType().getName() + " with id '" + id + "'");
		}
		return resolved;
	}

	/**
	 * The id of a (possibly proxy) reference target: the EMF id attribute if set,
	 * otherwise the tail of the proxy URI fragment — covers both the Mongo shape
	 * ({@code #<id>}) and the JPA indirection shape ({@code #//<ref>/<idAttr>/<id>}).
	 * Shared by the patch engine's REMOVE-by-id matching and the backends'
	 * insert-binding walk (issue #107).
	 *
	 * @param object the reference target; must not be {@code null}
	 * @return the id, or {@code null} if the object carries none
	 */
	public static String idOf(EObject object) {
		// composite-id types yield the k1=v1,k2=v2 fragment (issue #109)
		String id = CompositeIds.fragment(object);
		if (id == null && object.eIsProxy()) {
			String fragment = ((InternalEObject) object).eProxyURI().fragment();
			if (fragment == null) {
				return null;
			}
			int slash = fragment.lastIndexOf('/');
			return slash >= 0 ? fragment.substring(slash + 1) : fragment;
		}
		return id;
	}

	/**
	 * Validates a single entry against the type and resolves its feature. Returns the
	 * resolved feature (never {@code null}).
	 */
	private static EStructuralFeature checkEntry(ChangeEntry entry, EClass type) throws QueryException {
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
		if (feature instanceof EReference reference) {
			if (reference.isContainment()) {
				throw new QueryException("Reference '" + reference.getName() + "' of '" + type.getName()
						+ "' is a containment — contained objects are lifecycle, use Insert/Delete commands");
			}
			if (kind == DeltaKind.MOVE) {
				throw new QueryException("MOVE is not supported on reference '" + reference.getName()
						+ "' — reference membership is patched by id (ADD/REMOVE), not by position");
			}
		}
		if (!feature.isChangeable()) {
			throw new QueryException("Feature '" + feature.getName() + "' of '" + type.getName()
					+ "' is not changeable");
		}
		boolean many = feature.isMany();
		if (many && (kind == DeltaKind.SET || kind == DeltaKind.UNSET)) {
			throw new QueryException(kind + " addresses the many-valued feature '"
					+ feature.getName() + "' — use ADD/REMOVE" + (feature instanceof EAttribute ? "/MOVE" : ""));
		}
		if (!many && (kind == DeltaKind.ADD || kind == DeltaKind.REMOVE || kind == DeltaKind.MOVE)) {
			throw new QueryException(kind + " addresses the single-valued feature '"
					+ feature.getName() + "' — use SET/UNSET");
		}
		return feature;
	}

	/**
	 * Binds the non-containment references of an insert payload to EXISTING targets by
	 * id (issue #107): reference values pointing outside the payload resolve through the
	 * backend's keyed-find contract and are replaced on the copies with the resolved
	 * handle — including the bidirectional references the EMF copier drops (it reads the
	 * originals, not the copies). Targets without an id and unresolvable ids refuse.
	 *
	 * @param copies the copier's original→copy map, covering every object of the payload
	 *        trees ({@code EcoreUtil.Copier} after {@code copyReferences()})
	 * @param resolver the backend's keyed-find contract
	 * @throws QueryException if an external target carries no id or does not exist
	 */
	public static void bindInsertReferences(Map<EObject, EObject> copies, ReferenceResolver resolver)
			throws QueryException {
		requireNonNull(copies, "copies must not be null");
		requireNonNull(resolver, "resolver must not be null");
		for (Map.Entry<EObject, EObject> pair : copies.entrySet()) {
			bindObject(pair.getKey(), pair.getValue(), copies, resolver);
		}
	}

	private static void bindObject(EObject original, EObject copy, Map<EObject, EObject> copies,
			ReferenceResolver resolver) throws QueryException {
		for (EReference reference : original.eClass().getEAllReferences()) {
			if (reference.isContainment() || reference.isContainer() || !reference.isChangeable()
					|| reference.isDerived() || reference.isTransient() || reference.isVolatile()
					|| !original.eIsSet(reference)) {
				continue;
			}
			if (reference.isMany()) {
				@SuppressWarnings("unchecked")
				List<EObject> members = (List<EObject>) original.eGet(reference, false);
				List<Object> copyMembers = manyValues(copy, reference);
				List<EObject> rebound = new ArrayList<>(members.size());
				for (EObject member : members) {
					rebound.add(insertHandle(member, reference, copies, resolver));
				}
				copyMembers.clear();
				copyMembers.addAll(rebound);
			} else if (original.eGet(reference, false) instanceof EObject member) {
				copy.eSet(reference, insertHandle(member, reference, copies, resolver));
			}
		}
	}

	private static EObject insertHandle(EObject member, EReference reference, Map<EObject, EObject> copies,
			ReferenceResolver resolver) throws QueryException {
		EObject copied = copies.get(member);
		if (copied != null) {
			return copied; // payload-internal — inserted together
		}
		String id = idOf(member);
		if (id == null) {
			throw new QueryException("Insert payload references an external '"
					+ reference.getEReferenceType().getName() + "' via '" + reference.getName()
					+ "' that carries no id — existing targets bind by id");
		}
		return resolve(reference, id, resolver);
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
	private static List<Object> manyValues(EObject target, EStructuralFeature feature) {
		return (List<Object>) target.eGet(feature);
	}
}
