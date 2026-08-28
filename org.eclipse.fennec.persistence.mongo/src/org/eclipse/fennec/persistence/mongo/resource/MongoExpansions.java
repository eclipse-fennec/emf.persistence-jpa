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
package org.eclipse.fennec.persistence.mongo.resource;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.model.expression.PropertyPath;
import org.eclipse.fennec.model.query.Expand;

/**
 * Batched proxy resolution for {@code expand} on the mongo backend (issue #254).
 * <p>
 * Non-containment references arrive as lightweight EMF proxies — {@code createProxyFor} sets an
 * {@code eProxyURI} and never reads the target. Navigating them resolves one at a time through
 * {@code MongoResourceImpl.getEObject}, which is a keyed find per proxy: the N+1 that expand
 * exists to remove. This class removes it by reading a whole level in one query.
 * <p>
 * <strong>Why not {@code $lookup}.</strong> It lives only in the aggregation pipeline, while
 * object queries run through {@code find}; it appears nowhere else in this backend, so it would
 * be new ground across three server flavors; and it embeds the target documents into the parent,
 * which would then have to be taken apart again to build separate EObjects. Reading the targets
 * by id with one {@code $in} per collection is what JPA already does for the same job
 * ({@code eclipselink.batch}, {@code BATCH_TYPE = IN}), needs no operator beyond {@code $in}, and
 * matches what an expansion means: resolve proxies, do not shape a result.
 * <p>
 * <strong>How the resolution finds the preloaded objects.</strong> Nothing on the resolution path
 * changes. {@code getEObject} consults {@code findInRawContents} <em>before</em> it queries, so
 * attaching the targets to their own resource is enough — the later navigation finds them in
 * memory, with identity preserved, and never reaches the database.
 * <p>
 * Roots are processed in chunks so the caller's stream stays lazy: a query over a million
 * documents must not materialise a million roots to expand them.
 *
 * @author Mark Hoffmann
 */
final class MongoExpansions {

	private MongoExpansions() {
		// no instances
	}

	/**
	 * Resolves the given expansions over one chunk of roots, level by level.
	 * <p>
	 * Each path segment is a level: the proxies reachable at that level are collected across
	 * <em>all</em> roots of the chunk, grouped by the resource that owns them, and read in one
	 * query per group. The objects resolved at one level are the input of the next, so a
	 * two-segment path costs two queries for the chunk rather than two per object.
	 *
	 * @param roots the chunk of root objects
	 * @param expansions the expansions of the query envelope
	 * @param resourceSet the resource set the proxies resolve against
	 */
	static void resolve(List<EObject> roots, Collection<Expand> expansions, ResourceSet resourceSet) {
		if (roots.isEmpty() || expansions.isEmpty() || isNull(resourceSet)) {
			return;
		}
		for (Expand expansion : expansions) {
			resolvePath(roots, expansion.getPath(), resourceSet);
		}
	}

	/** Walks one expand path, resolving a whole level per query. */
	private static void resolvePath(List<EObject> roots, PropertyPath path, ResourceSet resourceSet) {
		if (isNull(path) || path.getSegments().isEmpty()) {
			return;
		}
		List<EObject> level = roots;
		for (EStructuralFeature segment : path.getSegments()) {
			if (!(segment instanceof EReference reference) || level.isEmpty()) {
				return;
			}
			level = resolveLevel(level, reference, resourceSet);
		}
	}

	/**
	 * Resolves one reference across a whole level.
	 *
	 * @return the objects now reachable through {@code reference}, as the input of the next level
	 */
	private static List<EObject> resolveLevel(List<EObject> level, EReference reference,
			ResourceSet resourceSet) {
		// group the proxy fragments by the resource that owns them: one query per collection
		Map<URI, Set<String>> byResource = new LinkedHashMap<>();
		for (EObject owner : level) {
			for (EObject value : valuesOf(owner, reference)) {
				if (!value.eIsProxy()) {
					continue;
				}
				URI proxyUri = ((InternalEObject) value).eProxyURI();
				if (isNull(proxyUri) || isNull(proxyUri.fragment())) {
					continue;
				}
				byResource.computeIfAbsent(proxyUri.trimFragment(), key -> new LinkedHashSet<>())
						.add(proxyUri.fragment());
			}
		}
		byResource.forEach((resourceUri, fragments) -> preload(resourceUri, fragments, resourceSet));

		// touching the values now resolves them from memory — the preload put them there
		List<EObject> resolved = new ArrayList<>();
		for (EObject owner : level) {
			for (EObject value : valuesOf(owner, reference)) {
				EObject target = value.eIsProxy()
						? org.eclipse.emf.ecore.util.EcoreUtil.resolve(value, resourceSet)
						: value;
				if (!target.eIsProxy()) {
					resolved.add(target);
				}
			}
		}
		return resolved;
	}

	/** Hands the fragments to their own resource, which reads them in one query. */
	private static void preload(URI resourceUri, Set<String> fragments, ResourceSet resourceSet) {
		Resource resource;
		try {
			resource = resourceSet.getResource(resourceUri, true);
		} catch (RuntimeException e) {
			// an unresolvable target resource is not this query's failure: the proxies stay
			// proxies and navigating one reports the problem where it belongs
			return;
		}
		if (resource instanceof MongoResourceImpl mongo) {
			mongo.preloadByFragments(fragments);
		}
	}

	/** The reference's values as EObjects, single- or multi-valued, without resolving anything. */
	@SuppressWarnings("unchecked")
	private static List<EObject> valuesOf(EObject owner, EReference reference) {
		if (!owner.eClass().getEAllReferences().contains(reference)) {
			return List.of();
		}
		Object raw = ((InternalEObject) owner).eGet(reference, false);
		if (isNull(raw)) {
			return List.of();
		}
		if (reference.isMany()) {
			List<EObject> values = new ArrayList<>();
			for (Object element : (Collection<Object>) raw) {
				if (element instanceof EObject eObject) {
					values.add(eObject);
				}
			}
			return values;
		}
		return raw instanceof EObject eObject ? List.of(eObject) : List.of();
	}

	/** @return {@code true} if any expansion addresses at least one reference segment */
	static boolean hasWork(Collection<Expand> expansions) {
		return expansions.stream().anyMatch(expansion -> nonNull(expansion.getPath())
				&& !expansion.getPath().getSegments().isEmpty());
	}
}
