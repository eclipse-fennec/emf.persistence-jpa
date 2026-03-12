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
package org.eclipse.fennec.persistence.orm.processor;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.persistence.eorm.ENamedBase;
import org.eclipse.fennec.persistence.processor.Processor;

/**
 * Basic wrapper to process {@link EStructuralFeature} into mappings
 * @author Mark Hoffmann
 * @since 29.12.2024
 */
public interface EFeatureProcessor<T extends ENamedBase, F extends EStructuralFeature>  extends Processor<T, F>{
	
	/**
	 * Returns the concrete {@link EStructuralFeature} 
	 * @return the concrete {@link EStructuralFeature}
	 */
	F getFeature();

	/**
	 * Returns the processed mapping
	 * @return the processed mapping
	 */
	T getMapping();
	
}
