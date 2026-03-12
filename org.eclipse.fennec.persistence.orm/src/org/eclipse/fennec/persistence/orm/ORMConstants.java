/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
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
package org.eclipse.fennec.persistence.orm;

import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.persistence.eorm.EORMPackage;
import org.eclipse.fennec.persistence.epersistence.EPersistencePackage;

/**
 * Interface with constants
 * @author Mark Hoffmann
 * @since 25.09.2025
 */
public interface ORMConstants {
	
	public static final String ORM_LOADER_SERVICE_PID = "fennec.jpa.EORMLoader";
	public static final String ORM_MAPPING_SERVICE_PID = "fennec.jpa.EORMMappingService";
	public static final String EPERSISTENCE_MODEL_TARGET = "(&(" + EMFNamespaces.EMF_NAME + "=" + EPersistencePackage.eNAME + ")(" + EMFNamespaces.EMF_NAME + "=" + EORMPackage.eNAME + "))";
	public static final String PROPERTY_PREFIX = "fennec.jpa.eorm.";

}
