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
package org.eclipse.fennec.persistence.resource;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.persistence.engine.PersistenceEngine;

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
	 * Checks weather the resource content exists 
	 * @return <code>true</code>, if the resource content exists
	 * @throws IOException
	 */
	boolean exist() throws IOException;
	
	/**
	 * Checks weather the resource content exists 
	 * @param options the exist options map
	 * @return <code>true</code>, if the resource content exists
	 * @throws IOException
	 */
	boolean exist(Map<?, ?> options) throws IOException;
	
	/**
	 * Returns the persistence engine. Must not return <code>null</code>
	 * @return the persistence engine
	 */
	PersistenceEngine getEngine();
	
}
