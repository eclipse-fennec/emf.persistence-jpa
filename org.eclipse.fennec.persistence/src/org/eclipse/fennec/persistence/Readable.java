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

/**
 * Interface to read data
 * @author Mark Hoffmann
 * @since 14.02.2023
 */
public interface Readable {
	
	/**
	 * Executes a read operation
	 * @param properties additional read properties
	 * @throws PersistenceException thrown on lower level errors 
	 */
	void read(Map<Object, Object> properties) throws PersistenceException;
	
}
