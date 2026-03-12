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
package org.eclipse.fennec.persistence.processor;

/**
 * Base interface for the processors context
 * @param <T> the target type
 * @param <S> the source types
 * @param <P> the processor type
 * @author Mark Hoffmann
 * @since 30.12.2024
 */
public interface ProcessorFactory<T, S, P extends Processor<T, S>> {
	
	/**
	 * Creates a processor instance
	 * @param source the source object
	 * @param targetType the target type
	 * @return the processor
	 */
	P createProcessor(S source, Class<T> targetType);

}
