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
package org.eclipse.fennec.persistence;

import java.util.Map;

import org.eclipse.fennec.persistence.engine.PersistenceEngine;

/**
 * An interface that is optionally implemented by the {@link PersistenceEngine} to delete resources 
 * @author Mark Hoffmann
 * @since 14.02.2023
 */
public interface Deletable {
	
	/**
	 * Executes a deletion
	 * @param properties additional delete properties
	 * @return <code>true</code>, if deletion was successful, otherwise <code>false</code>
	 * @throws PersistenceException thrown on lower level errors 
	 */
	boolean delete(Map<Object, Object> properties) throws PersistenceException;

}
