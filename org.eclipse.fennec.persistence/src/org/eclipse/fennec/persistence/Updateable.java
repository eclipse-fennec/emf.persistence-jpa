/**
 * Copyright (c) 2012 - 2024 Data In Motion and others.
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
package org.eclipse.fennec.persistence;

import java.util.Map;

/**
 * Interface to create and update data
 * @author Mark Hoffmann
 * @since 14.02.2023
 */
public interface Updateable {
	
	/**
	 * Executes a update operation
	 * @param properties additional update properties
	 * @throws PersistenceException thrown on lower level errors 
	 */
	void update(Map<Object, Object> properties) throws PersistenceException;
	
	/**
	 * Executes a create operation
	 * @param properties additional create properties
	 * @throws PersistenceException thrown on lower level errors 
	 */
	void create(Map<Object, Object> properties) throws PersistenceException;
	
}
