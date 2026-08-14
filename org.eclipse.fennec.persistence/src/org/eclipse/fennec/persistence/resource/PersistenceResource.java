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
package org.eclipse.fennec.persistence.resource;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.persistence.capabilities.PersistenceCapabilities;

/**
 * Resource extension for the persistence context
 * @author Mark Hoffmann
 * @since 10.12.2024
 */
public interface PersistenceResource extends Resource, AutoCloseable {
	
	public static enum ActionType {
		LOAD,
		SAVE,
		DELETE,
		COUNT,
		EXIST,
		ALL
	}
	
	/**
	 * Updates the default options of the {@link Resource} 
	 * @param options the default options
	 */
	void updateDefaultOptions(Map<Object, Object> options, ActionType... types);
	
	/**
	 * Counts the elements of this resource
	 * @return the number of elements 
	 * @throws IOException
	 */
	long count() throws IOException;
	
	/**
	 * Counts the elements of this resource
	 * @param options the count options map
	 * @return the number of elements 
	 * @throws IOException
	 */
	long count(Map<?, ?> options) throws IOException;
	
	/**
	 * Checks whether the resource content exists 
	 * @return <code>true</code>, if the resource content exists
	 * @throws IOException
	 */
	boolean exist() throws IOException;
	
	/**
	 * Checks whether the resource content exists
	 * @param options the exist options map
	 * @return <code>true</code>, if the resource content exists
	 * @throws IOException
	 */
	boolean exist(Map<?, ?> options) throws IOException;

	/**
	 * What this resource's backend can do — query vocabulary, command verbs and store features
	 * in one place (issue #134, contract §5a).
	 * <p>
	 * Answered here rather than on an optional query or command role, because a capability
	 * statement must be reachable without holding one: whether the store brackets writes
	 * atomically decides how cascade-delete converges on the <em>save</em> path (§4a).
	 * <p>
	 * These are the <b>effective</b> capabilities of this resource instance, so a deployment
	 * probe has already narrowed the backend's declaration to what is actually served here —
	 * mongo answers {@code TRANSACTION_BRACKET} per replica-set probe, for example. A probe only
	 * ever narrows.
	 *
	 * @return the effective capabilities, never {@code null}
	 */
	PersistenceCapabilities capabilities();

}
