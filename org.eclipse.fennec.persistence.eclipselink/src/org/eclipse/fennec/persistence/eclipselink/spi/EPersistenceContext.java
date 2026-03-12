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
package org.eclipse.fennec.persistence.eclipselink.spi;

import java.net.URL;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.epersistence.PersistenceUnit;

/**
 * Provider for ORM related information like {@link PersistenceUnit} or {@link EntityMappings},
 * as well the resolved, referenced {@link EPackage} and {@link EClassifier}.
 * These information are unmodifieable
 * @author Mark Hoffmann
 * @since 12.12.2024
 */
public interface EPersistenceContext {
	
	PersistenceUnit getPersistenceUnit();
	
	List<EntityMappings> getMappings();
	
	String getPersistenceUnitName();
	
	URL getMetadataUrl();
	
	List<EClassifier> getClassifiers();
	
	List<EPackage> getEPackages();
	
	String getId();
	
}
