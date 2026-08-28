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
package org.eclipse.fennec.persistence.eclipselink.resource;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.eclipselink.query.JpaExpandPlan;

import jakarta.persistence.EntityManager;

/**
 * Executes the filtered expansions of a query (issue #238), one chunk of roots at a time.
 * <p>
 * A plain expansion needs nothing here — it rides on the fetch joins and batch-fetch hints of
 * issue #95. A filtered one does, because the hint has no room for a predicate. The keyed query
 * {@link JpaExpandPlan} carries is run for the chunk, which registers the matching targets in the
 * persistence context; the proxies naming those targets are then resolved, and answer from the
 * context without a further round trip — the mechanism issue #226 introduced for the save path.
 * <p>
 * <strong>Only the matching proxies are resolved</strong>, which is decision D1: the reference
 * keeps every child the store has, so the object never misreports it and writing it back loses
 * nothing. What the expansion delivered is exactly what is no longer a proxy (D1b).
 * <p>
 * The collections themselves are cheap to touch: the to-many levels of an expansion already carry
 * {@code eclipselink.batch} with {@code BATCH_TYPE = IN}, and this backend's indirection policy
 * substitutes an id-only query, so building the lists of proxies costs one query for the batch
 * rather than one per root.
 *
 * @author Mark Hoffmann
 */
final class JpaExpansions {

	private JpaExpansions() {
		// no instances
	}

	/**
	 * Runs the filtered expansions over one chunk of roots.
	 *
	 * @param roots the chunk
	 * @param plans the filtered expansions of the query
	 * @param em the entity manager the main query runs on
	 * @param resourceSet the resource set the proxies resolve against
	 */
	static void resolve(List<EObject> roots, List<JpaExpandPlan> plans, EntityManager em,
			ResourceSet resourceSet) {
		if (roots.isEmpty() || plans.isEmpty() || isNull(em)) {
			return;
		}
		List<Object> keys = keysOf(roots);
		if (keys.isEmpty()) {
			return;
		}
		for (JpaExpandPlan plan : plans) {
			// a plain expansion selects nothing: every proxy on the path belongs to it
			Set<String> matching = plan.filtered() ? matchingIds(plan, keys, em) : null;
			if (nonNull(matching) && matching.isEmpty()) {
				continue;
			}
			resolveMatching(roots, plan, matching, resourceSet);
		}
	}

	/** Runs the keyed query and collects the ids of the targets it matched. */
	private static Set<String> matchingIds(JpaExpandPlan plan, List<Object> keys, EntityManager em) {
		Set<String> ids = new LinkedHashSet<>();
		jakarta.persistence.Query query = em.createQuery(plan.jpql());
		query.setParameter(JpaExpandPlan.KEY_PARAMETER, keys);
		plan.parameters().forEach(query::setParameter);
		for (Object row : query.getResultList()) {
			if (row instanceof EObject target) {
				String id = idValueOf(target);
				if (nonNull(id)) {
					ids.add(id);
				}
			}
		}
		return ids;
	}

	/**
	 * Resolves exactly the proxies the expansion matched, walking the path level by level.
	 * <p>
	 * Resolution happens <strong>in place</strong>, through the feature's own accessor:
	 * {@code EList.get(index)} on a resolving list replaces the proxy with the resolved object,
	 * and a resolving {@code eGet} does the same for a single-valued reference.
	 * {@code EcoreUtil.resolve} would not — it returns the resolved object and leaves the entry
	 * in the list untouched, so the collection would still report nothing but proxies and D1b
	 * ("what is delivered is exactly the selected set") would be unobservable.
	 * <p>
	 * Intermediate levels resolve whole: they are the route to the filtered one, and a filter
	 * addresses the last segment only.
	 */
	private static void resolveMatching(List<EObject> roots, JpaExpandPlan plan, Set<String> matching,
			ResourceSet resourceSet) {
		List<EObject> level = roots;
		List<EReference> path = plan.path();
		for (int i = 0; i < path.size(); i++) {
			boolean last = i == path.size() - 1;
			List<EObject> next = new ArrayList<>();
			for (EObject owner : level) {
				resolveInto(owner, path.get(i), last ? matching : null, next, resourceSet);
			}
			level = next;
		}
	}

	/**
	 * Resolves one owner's values of {@code reference} in place, collecting what came out.
	 *
	 * @param selected the ids to resolve, or {@code null} to resolve every value
	 */
	@SuppressWarnings("unchecked")
	private static void resolveInto(EObject owner, EReference reference, Set<String> selected,
			List<EObject> resolved, ResourceSet resourceSet) {
		if (!owner.eClass().getEAllReferences().contains(reference)) {
			return;
		}
		if (!reference.isMany()) {
			Object raw = owner.eGet(reference, false);
			if (!(raw instanceof EObject value)) {
				return;
			}
			if (value.eIsProxy() && nonNull(selected) && !selected.contains(proxyIdOf(value))) {
				return;
			}
			EObject target = value.eIsProxy() ? EcoreUtil.resolve(value, resourceSet) : value;
			if (!target.eIsProxy()) {
				if (target != value) {
					owner.eSet(reference, target);
				}
				resolved.add(target);
			}
			return;
		}
		// the list has to be asked for with a resolving eGet: this backend's indirection policy
		// builds it lazily from an id-only query, and an un-triggered read would find it empty.
		// Building it is already batched — the to-many levels carry eclipselink.batch.
		if (!(owner.eGet(reference) instanceof List<?> list)) {
			return;
		}
		List<EObject> values = (List<EObject>) list;
		for (int index = 0; index < values.size(); index++) {
			// basicGet does NOT resolve, which is what lets the filter decide per element
			EObject value = values instanceof InternalEList<?> internal
					? (EObject) internal.basicGet(index)
					: values.get(index);
			if (value.eIsProxy() && nonNull(selected) && !selected.contains(proxyIdOf(value))) {
				// not part of this expansion: it stays a proxy, and stays navigable
				continue;
			}
			EObject target = value.eIsProxy() ? EcoreUtil.resolve(value, resourceSet) : value;
			if (!target.eIsProxy()) {
				if (target != value) {
					// the list does not resolve on access — EcoreEList.Dynamic keeps whatever it
					// holds — so the entry is replaced here. Without it the expansion would have
					// read the target and thrown it away, and D1b ("what is delivered is exactly
					// the selected set", discriminated by eIsProxy) would be unobservable.
					values.set(index, target);
				}
				resolved.add(target);
			}
		}
	}

	/** The id values of the chunk's roots, for the keyed query. */
	private static List<Object> keysOf(List<EObject> roots) {
		List<Object> keys = new ArrayList<>();
		for (EObject root : roots) {
			EAttribute id = root.eClass().getEIDAttribute();
			if (isNull(id)) {
				continue;
			}
			Object value = root.eGet(id);
			if (nonNull(value)) {
				keys.add(value);
			}
		}
		return keys;
	}

	/** The EMF id of a materialised object, as text. */
	private static String idValueOf(EObject object) {
		EAttribute id = object.eClass().getEIDAttribute();
		if (isNull(id)) {
			return null;
		}
		Object value = object.eGet(id);
		return isNull(value) ? null : String.valueOf(value);
	}

	/** The id a proxy names, from the {@code //ref/idAttr/id} fragment its URI carries. */
	private static String proxyIdOf(EObject proxy) {
		URI uri = ((InternalEObject) proxy).eProxyURI();
		if (isNull(uri) || isNull(uri.fragment())) {
			return null;
		}
		String fragment = uri.fragment();
		if (!fragment.startsWith("//")) {
			return fragment;
		}
		String[] parts = fragment.substring(2).split("/");
		return parts.length < 3 ? null : parts[2];
	}

	/**
	 * The reference's values, single- or multi-valued, <strong>without resolving any of
	 * them</strong>.
	 * <p>
	 * The many case has to ask for the list with a resolving {@code eGet}: this backend's
	 * indirection policy builds it lazily from an id-only query, and a non-resolving read would
	 * find it un-triggered and empty — the expansion would then see nothing to do. Building it
	 * is cheap and already batched, because the to-many levels of an expansion carry
	 * {@code eclipselink.batch}. Its <em>elements</em> must still not be resolved, or every
	 * proxy would be fetched one by one and the filter would select from a collection that had
	 * already paid for itself; {@link InternalEList#basicIterator()} walks them as they are.
	 */
	private static List<EObject> valuesOf(EObject owner, EReference reference) {
		if (!owner.eClass().getEAllReferences().contains(reference)) {
			return List.of();
		}
		if (!reference.isMany()) {
			Object raw = owner.eGet(reference, false);
			return raw instanceof EObject eObject ? List.of(eObject) : List.of();
		}
		Object raw = owner.eGet(reference);
		if (!(raw instanceof Collection<?> collection)) {
			return List.of();
		}
		List<EObject> values = new ArrayList<>();
		Iterator<?> elements = collection instanceof InternalEList<?> internal
				? internal.basicIterator()
				: collection.iterator();
		while (elements.hasNext()) {
			if (elements.next() instanceof EObject eObject) {
				values.add(eObject);
			}
		}
		return values;
	}
}
