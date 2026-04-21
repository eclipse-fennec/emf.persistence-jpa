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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;

/**
 * EMF helper class
 * @author Mark Hoffmann
 * @since 15.02.2023
 */
public class EMFHelper {


	/**
	 * Returns the response map
	 * @param options the options to get the response from
	 * @return the response map, or a new empty map if none is present
	 */
	// EMF's URIConverter.OPTION_RESPONSE contract guarantees the value is a
	// Map<Object, Object>; we cannot express that in the public Map<?, ?> API.
	@SuppressWarnings("unchecked")
	public static Map<Object, Object> getResponse(Map<?, ?> options) {
		Map<?, ?> response = isNull(options) ? null : (Map<?, ?>)options.get(URIConverter.OPTION_RESPONSE);
		return isNull(response) ? new HashMap<>() : (Map<Object, Object>) response;
	}

	/**
	 * Merges 2 maps, without changing any of them.  If map2 and map1
	 * have the same key for an entry, map1's value will be the one in
	 * the merged map.
	 */
	public static Map<?, ?> mergeMaps(Map<?, ?> map1, Map<?, ?> map2) {
		if (isNull(map1) || map1.isEmpty()) {
			return map2;
		} else if (isNull(map2) || map2.isEmpty()) {
			return map1;
		} else {
			HashMap<Object, Object> map = new HashMap<>(map1.size() + map2.size());
			map.putAll(map1);
			map.putAll(map2);
			return map;
		}
	}

	/**
	 * Returns the effective options as an unmodifiable {@link Map}
	 * @param options external options
	 * @param defaultOptions internal, default options
	 * @return an unmodifiable merged {@link Map}
	 */
	// mergeMaps returns a fresh HashMap<Object, Object> in all non-trivial
	// cases; the trivial cases return one of the input maps which — per our
	// call contract — may not be written to outside of this helper's scope.
	@SuppressWarnings("unchecked")
	public static Map<Object, Object> getEffectiveOptions(Map<?, ?> options, Map<?, ?> defaultOptions) {
		Map<Object, Object> effective = (Map<Object, Object>) mergeMaps(options, defaultOptions);
		effective.put(URIConverter.OPTION_RESPONSE, getResponse(options));
		return Collections.unmodifiableMap(effective);
	}

	/**
	 * Resolves an {@link EClass} by its {@code ePackageNsURI#EClassName}-style URI,
	 * first by consulting the package registry and falling back to
	 * {@link ResourceSet#getEObject(URI, boolean)} for dynamically loaded packages.
	 *
	 * @param resourceSet the {@link ResourceSet} whose package registry is searched first
	 * @param eClassURI fully qualified URI — last segment is the classifier name,
	 *        everything before is the package namespace URI
	 * @return the {@link EClass}, or {@code null} if no such classifier exists
	 */
	public static EClass getEClassFromResourceSet(ResourceSet resourceSet, String eClassURI) {
		URI theUri = URI.createURI(eClassURI);
		String classifier = theUri.lastSegment();
		EPackage ePackage = resourceSet.getPackageRegistry().getEPackage(theUri.trimSegments(1).trimFragment().toString());
		if(nonNull(ePackage)) {
			EClassifier eClassifier = (EClassifier) ePackage.getEClassifier(classifier);
			if(eClassifier instanceof EClass eClass) {
				return eClass;
			}
		}

		return (EClass) resourceSet.getEObject(theUri, true);
	}

	/**
	 * Cache-aware variant of {@link #getEClassFromResourceSet(ResourceSet, String)}.
	 * Returns the cached entry if present; otherwise resolves through the
	 * ResourceSet and stores the result in the cache. Thread-safe via internal
	 * synchronisation on the supplied cache map.
	 *
	 * @param resourceSet the {@link ResourceSet} to resolve against
	 * @param eClassURI fully qualified EClass URI (see
	 *        {@link #getEClassFromResourceSet(ResourceSet, String)})
	 * @param eClassCache external cache map, may be {@code null} to bypass caching
	 * @return the resolved {@link EClass}, or {@code null} if none found
	 */
	public static EClass getEClass(ResourceSet resourceSet, String eClassURI, Map<String, EClass> eClassCache) {
		if (nonNull(eClassCache)) {
			synchronized (eClassCache) {
				EClass eClass = eClassCache.get(eClassURI);

				if (isNull(eClass)) {
					eClass = getEClassFromResourceSet(resourceSet, eClassURI);
					eClassCache.put(eClassURI, eClass);
				}
				return eClass;
			}
		}
		return getEClassFromResourceSet(resourceSet, eClassURI);
	}

}
