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
package org.eclipse.fennec.persistence.engine;

import static java.util.Objects.nonNull;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.persistence.Options;
import org.eclipse.fennec.persistence.helper.EMFHelper;
import org.eclipse.fennec.persistence.resource.PersistenceResource;

/**
 * Base implementation for {@link PersistenceEngine} that manages
 * merged options, engine properties and the resource reference.
 * @author Mark Hoffmann
 * @since 08.04.2022
 */
public abstract class BasicPersistenceEngine implements PersistenceEngine {

	private final Map<Object, Object> mergedOptions = new HashMap<>();
	private final Map<Object, Object> engineProperties = new HashMap<>();
	private PersistenceResource resource;

	@Override
	public void setResource(PersistenceResource resource) {
		this.resource = resource;
		normalizeOptions(engineProperties);
	}

	@Override
	public void dispose() {
		mergedOptions.clear();
		engineProperties.clear();
		resource = null;
	}

	/**
	 * Returns the resource.
	 * @return the resource
	 */
	public PersistenceResource getResource() {
		return resource;
	}
	
	/**
	 * Returns the properties.
	 * @return the properties
	 */
	public Map<Object, Object> getProperties() {
		return engineProperties;
	}
	
	/**
	 * Returns the mergedOptions.
	 * @return the mergedOptions
	 */
	public Map<Object, Object> getMergedOptions() {
		return mergedOptions;
	}
	
	public Map<Object, Object> getResponse() {
		return EMFHelper.getResponse(getMergedOptions());
	}
	
	/**
	 * Normalizes the load options
	 * @param options the original options
	 */
	protected void normalizeOptions(Map<Object, Object> options) {
		mergedOptions.putAll(options);
		EClass collectionEClass = Options.getTableEClass(options);
		if (nonNull(collectionEClass) && !options.containsKey(Options.READ_FILTER_ECLASS)) {
			mergedOptions.put(Options.READ_FILTER_ECLASS, collectionEClass);
		}
	}
	
}
