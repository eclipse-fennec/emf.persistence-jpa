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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.fennec.model.expression.ExpressionPackage;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.QueryPackage;
import org.eclipse.fennec.persistence.query.QueryException;

/**
 * The catalog payload of persisted queries (concept.md §14, {@code saveQuery}): a query
 * serializes to an XMI document stored under its name; metamodel references (root type,
 * path segments, type hints) are written as nsURI-based hrefs and resolve against the
 * consumer's {@link EPackage.Registry} on load. The storage location is backend-specific
 * (Mongo collection, JPA table); the payload format is not.
 *
 * @author Juergen Albert
 * @since 28.07.2026
 */
public final class PersistedQueries {

	private PersistedQueries() {
	}

	/**
	 * The name under which the query is to be persisted, or {@code null} if the query
	 * does not request cataloguing.
	 *
	 * @param query the query, must not be {@code null}
	 * @return the catalog name, or {@code null} when {@code saveQuery} is unset
	 * @throws QueryException if {@code saveQuery} is set without a usable name
	 */
	public static String catalogName(Query query) throws QueryException {
		if (!query.isSaveQuery()) {
			return null;
		}
		String name = query.getName();
		if (name == null || name.isBlank()) {
			throw new QueryException("saveQuery requires a non-blank query name — use QueryBuilder.named(name)");
		}
		return name;
	}

	/**
	 * Serializes the query to its XMI catalog payload. The query is copied first — the
	 * caller's instance is not touched. References into metamodels are replaced by
	 * proxies with nsURI-based URIs so the payload stays machine- and
	 * location-independent (no file URIs, no dangling references for packages that live
	 * only in a registry).
	 *
	 * @param query the query to serialize, must not be {@code null}
	 * @return the XMI document as a UTF-8 string
	 * @throws QueryException if serialization fails
	 */
	public static String toXmi(Query query) throws QueryException {
		XMIResourceImpl resource = new XMIResourceImpl(URI.createURI("fennec-query:/catalog"));
		Query copy = EcoreUtil.copy(query);
		resource.getContents().add(copy);
		externalizeMetamodelReferences(copy);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			resource.save(out, Map.of(XMLResource.OPTION_ENCODING, StandardCharsets.UTF_8.name()));
		} catch (IOException e) {
			throw new QueryException("Cannot serialize query '" + query.getName() + "' to XMI: " + e.getMessage(), e);
		}
		return out.toString(StandardCharsets.UTF_8);
	}

	/**
	 * Parses a catalog payload back into a {@link Query}. Metamodel references resolve
	 * against the given registry (falling back to the global registry); the query and
	 * expression model packages are always available.
	 *
	 * @param name the catalog name, for error messages
	 * @param xmi the XMI payload
	 * @param registry the package registry holding the referenced metamodels; may be
	 *        {@code null} to use only the global registry
	 * @return the loaded query
	 * @throws QueryException if the payload does not parse, is no query, or references
	 *         a metamodel the registry does not know
	 */
	public static Query fromXmi(String name, String xmi, EPackage.Registry registry) throws QueryException {
		CatalogResourceSet resourceSet = new CatalogResourceSet();
		resourceSet.setPackageRegistry(new EPackageRegistryImpl(
				registry != null ? registry : EPackage.Registry.INSTANCE));
		resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(QueryPackage.eNS_URI, QueryPackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(ExpressionPackage.eNS_URI, ExpressionPackage.eINSTANCE);
		XMIResourceImpl resource = new XMIResourceImpl(URI.createURI("fennec-query:/catalog"));
		resourceSet.getResources().add(resource);
		try {
			resource.load(new ByteArrayInputStream(xmi.getBytes(StandardCharsets.UTF_8)), Map.of());
		} catch (IOException e) {
			throw new QueryException("Cannot parse persisted query '" + name + "': " + e.getMessage(), e);
		}
		if (resource.getContents().isEmpty() || !(resource.getContents().get(0) instanceof Query query)) {
			throw new QueryException("Persisted payload of '" + name + "' is no query");
		}
		EcoreUtil.resolveAll(query);
		TreeIterator<EObject> contents = query.eAllContents();
		while (contents.hasNext()) {
			for (EObject target : contents.next().eCrossReferences()) {
				if (target.eIsProxy()) {
					throw new QueryException("Persisted query '" + name + "' references an unresolvable"
							+ " metamodel element " + EcoreUtil.getURI(target)
							+ " — register the model package before executing named queries");
				}
			}
		}
		return query;
	}

	/**
	 * Replaces every cross-reference into a metamodel (any target whose root container
	 * is an {@link EPackage} with an nsURI) by a proxy carrying the canonical
	 * {@code nsURI#fragment} URI. This makes the XMI writer emit stable hrefs regardless
	 * of whether the package lives in a file resource, a registry-only instance, or a
	 * generated package.
	 */
	private static void externalizeMetamodelReferences(Query copy) {
		List<EObject> all = new ArrayList<>();
		all.add(copy);
		copy.eAllContents().forEachRemaining(all::add);
		for (EObject source : all) {
			for (EReference reference : source.eClass().getEAllReferences()) {
				if (reference.isContainment() || reference.isDerived() || !source.eIsSet(reference)) {
					continue;
				}
				if (reference.isMany()) {
					@SuppressWarnings("unchecked")
					List<EObject> values = (List<EObject>) source.eGet(reference);
					for (int i = 0; i < values.size(); i++) {
						EObject proxy = metamodelProxy(values.get(i));
						if (proxy != null) {
							values.set(i, proxy);
						}
					}
				} else if (source.eGet(reference) instanceof EObject value) {
					EObject proxy = metamodelProxy(value);
					if (proxy != null) {
						source.eSet(reference, proxy);
					}
				}
			}
		}
	}

	private static EObject metamodelProxy(EObject target) {
		if (target == null || target.eIsProxy()
				|| !(EcoreUtil.getRootContainer(target) instanceof EPackage ePackage)
				|| ePackage.getNsURI() == null) {
			return null;
		}
		InternalEObject proxy = (InternalEObject) EcoreUtil.create(target.eClass());
		proxy.eSetProxyURI(URI.createURI(ePackage.getNsURI())
				.appendFragment(fragmentPath(ePackage, target)));
		return proxy;
	}

	/** The resource-style fragment of a metamodel element relative to its root package. */
	private static String fragmentPath(EPackage root, EObject target) {
		if (target == root) {
			return "/";
		}
		StringBuilder path = new StringBuilder();
		EObject current = target;
		while (current != root && current.eContainer() != null) {
			InternalEObject container = (InternalEObject) current.eContainer();
			path.insert(0, container.eURIFragmentSegment(current.eContainingFeature(), current))
					.insert(0, '/');
			current = current.eContainer();
		}
		return "/" + path;
	}

	/**
	 * Resolves nsURI-based metamodel hrefs strictly via the package registry: packages
	 * without a containing resource resolve by fragment navigation, and nothing is ever
	 * demand-loaded (an unknown nsURI must fail fast instead of being fetched as a URL).
	 */
	private static final class CatalogResourceSet extends ResourceSetImpl {

		@Override
		public Resource getResource(URI uri, boolean loadOnDemand) {
			return super.getResource(uri, false);
		}

		@Override
		public EObject getEObject(URI uri, boolean loadOnDemand) {
			EObject resolved = super.getEObject(uri, false);
			if (resolved != null) {
				return resolved;
			}
			EPackage ePackage = getPackageRegistry().getEPackage(uri.trimFragment().toString());
			return ePackage == null ? null : fragmentTarget(ePackage, uri.fragment());
		}

		private static EObject fragmentTarget(EPackage ePackage, String fragment) {
			if (fragment == null || !fragment.startsWith("//")) {
				return "/".equals(fragment) ? ePackage : null;
			}
			InternalEObject current = (InternalEObject) ePackage;
			for (String segment : fragment.substring(2).split("/")) {
				if (current == null) {
					return null;
				}
				current = (InternalEObject) current.eObjectForURIFragmentSegment(segment);
			}
			return current;
		}
	}
}
