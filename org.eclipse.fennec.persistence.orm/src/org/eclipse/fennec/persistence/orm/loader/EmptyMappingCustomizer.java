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
package org.eclipse.fennec.persistence.orm.loader;

import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.orm.EORMMappingCustomizer;
import org.osgi.service.component.annotations.Component;

/**
 * An empty customizer
 * @author Mark Hoffmann
 * @since 25.09.2025
 */
@Component
public class EmptyMappingCustomizer implements EORMMappingCustomizer {

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.EORMMappingCustomizer#customizeMapping(org.eclipse.fennec.persistence.eorm.EntityMappings)
	 */
	@Override
	public EntityMappings customizeMapping(EntityMappings mapping) {
		return mapping;
	}

}
