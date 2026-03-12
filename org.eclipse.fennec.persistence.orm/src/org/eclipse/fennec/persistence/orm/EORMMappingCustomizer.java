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
package org.eclipse.fennec.persistence.orm;

import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * Interface to be used to customize the auto-generated mapping
 * @author Mark Hoffmann
 * @since 25.09.2025
 */
@ConsumerType
public interface EORMMappingCustomizer {
	
	/**
	 * Customizes the mapping 
	 * @param mapping th mapping that is the source
	 * @return the mapping that is customized an ready to register
	 */
	EntityMappings customizeMapping(EntityMappings mapping);

}
