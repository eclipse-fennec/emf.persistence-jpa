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
package org.eclipse.fennec.persistence.eclipselink.dynamic;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.persistence.dynamic.DynamicType;

/**
 * Interface for an EMF DynamicType
 * @author Mark Hoffmann
 * @since 16.12.2024
 */
public interface EDynamicType extends DynamicType {

	/**
	 * Return the {@link EClass}
	 * @return the {@link EClass}
	 */
	EClass getEClass();
	
	/**
	 * Returns the {@link Entity}
	 * @return the {@link Entity}
	 */
	Entity getEntity();
	
	/**
	 * Returns the type base {@link URI} for this {@link EDynamicType}
	 * @return the type base {@link URI}
	 */
	URI getBaseURI();
	
	/**
	 * Returns the {@link EDynamicTypeContext}
	 * @return the {@link EDynamicTypeContext}
	 */
	EDynamicTypeContext getContext();
	
}
