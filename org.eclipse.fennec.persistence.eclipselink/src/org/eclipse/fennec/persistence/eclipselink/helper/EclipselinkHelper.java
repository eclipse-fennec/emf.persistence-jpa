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
package org.eclipse.fennec.persistence.eclipselink.helper;

import java.util.Objects;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.persistence.eclipselink.mappings.EReferenceAccessor;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * Helper class for our EMF Eclipselink extension classes
 * @author Mark Hoffmann
 * @since 22.01.2025
 */
public class EclipselinkHelper {
	
	/**
	 * Returns <code>true</code>, if the given mapping is a reference mapping in EMF sense
	 * @param mapping the {@link DatabaseMapping}
	 * @return <code>true</code>, if mapping is {@link EReference} mapping, otherwise <code>false</code>
	 */
	public static boolean isReferenceMapping(DatabaseMapping mapping) {
		Objects.requireNonNull(mapping);
		return mapping.getAttributeAccessor() instanceof EReferenceAccessor;
	}
	
	/**
	 * Returns the {@link EReference} for the given mapping, if it is a {@link EReference}, otherwise <code>null</code> will returned
	 * @param mapping the {@link DatabaseMapping}
	 * @return the {@link EReference} for the given mapping, if it is a {@link EReference}, otherwise <code>null</code> 
	 */
	public static EReference getReferenceFromMapping(DatabaseMapping mapping) {
		Objects.requireNonNull(mapping);
		if (mapping.getAttributeAccessor() instanceof EReferenceAccessor era) {
			return era.getReference();
		}
		return null;
	}

}
