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

import java.util.Map;

import org.eclipse.fennec.persistence.resource.PersistenceResource;

/**
 * Implementation specific engine, that does persistence with EMF
 * @author Mark Hoffmann
 * @since 10.12.2024
 */
public interface PersistenceEngine {
	
	/**
	 * Sets the resource
	 * @param resource the {@link PersistenceResource} to be used for configuration
	 */
	void setResource(PersistenceResource resource);
	
	/**
	 * Returns the {@link PersistenceResource}
	 * @return the {@link PersistenceResource}
	 */
	PersistenceResource getResource();
	
	/**
	 * Returns the mergedOptions.
	 * @return the mergedOptions
	 */
	public Map<Object, Object> getMergedOptions();
	
	/**
	 * returns the response optins map
	 * @return the response optins map
	 */
	public Map<Object, Object> getResponse();
	
	/**
	 * Releases a resources
	 */
	void dispose();

}
