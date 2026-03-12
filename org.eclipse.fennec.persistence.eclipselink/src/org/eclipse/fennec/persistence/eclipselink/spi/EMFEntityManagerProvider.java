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
package org.eclipse.fennec.persistence.eclipselink.spi;

import javax.sql.DataSource;

import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;

import jakarta.persistence.EntityManagerFactory;

/**
 * Interface for  an EMF entity manager provider, that configures and registers the {@link EntityManagerFactory} 
 * @author Mark Hoffmann
 * @since 12.12.2024
 */
public interface EMFEntityManagerProvider {
	
	/**
	 * Returns the mapped data source
	 * @return the mapped data source
	 */
	DataSource getDataSource();
	
	/**
	 * Returns the loaded persistence unit
	 * @return the loaded persistence unit
	 */
	PersistenceUnit getPersistenceUnit();
	

}
