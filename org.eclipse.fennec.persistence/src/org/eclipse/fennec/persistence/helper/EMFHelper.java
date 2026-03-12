/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.persistence.helper;

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
	 * @return the response map or a new onev
	 */
	@SuppressWarnings("unchecked")
	public static Map<Object, Object> getResponse(Map<?, ?> options) {
		Map<?, ?> response = options == null ? null : (Map<?, ?>)options.get(URIConverter.OPTION_RESPONSE);
		return response == null ? new HashMap<>() : (Map<Object, Object>) response;
	}

	/**
	 * Merges 2 maps, without changing any of them.  If map2 and map1
	 * have the same key for an entry, map1's value will be the one in
	 * the merged map.
	 */
	public static Map<?, ?> mergeMaps(Map<?, ?> map1, Map<?, ?> map2) {
		if (map1 == null || map1.isEmpty()) {
			return map2;
		} else if (map2 == null || map2.isEmpty()) {
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
	@SuppressWarnings("unchecked")
	public static Map<Object, Object> getEffectiveOptions(Map<?, ?> options, Map<?, ?> defaultOptions) {
		Map<Object, Object> effective = (Map<Object, Object>) mergeMaps(options, defaultOptions);
		effective.put(URIConverter.OPTION_RESPONSE, getResponse(options));
		return Collections.unmodifiableMap(effective);
	}

	public static EClass getEClassFromResourceSet(ResourceSet resourceSet, String eClassURI) {
		URI theUri = URI.createURI(eClassURI);
		String classifier = theUri.lastSegment();
		EPackage ePackage = resourceSet.getPackageRegistry().getEPackage(theUri.trimSegments(1).trimFragment().toString());
		if(ePackage != null) {
			EClassifier eClassifier = (EClassifier) ePackage.getEClassifier(classifier);
			if(eClassifier != null && eClassifier instanceof EClass) {
				return (EClass) eClassifier;
			}
		}

		return (EClass) resourceSet.getEObject(theUri, true);
	}

	public static EClass getEClass(ResourceSet resourceSet, String eClassURI, Map<String, EClass> eClassCache) {
		if (eClassCache != null) {
			synchronized (eClassCache) {
				EClass eClass = eClassCache.get(eClassURI);

				if (eClass == null) {
					eClass = getEClassFromResourceSet(resourceSet, eClassURI);
					eClassCache.put(eClassURI, eClass);
				}
				return eClass;
			}
		}
		return getEClassFromResourceSet(resourceSet, eClassURI);
	}

}
