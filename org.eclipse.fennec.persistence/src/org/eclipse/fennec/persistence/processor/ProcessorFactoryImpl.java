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

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Constructor;

/**
 * Basic implementation of a {@link ProcessorFactory}
 * @author Mark Hoffmann
 * @since 30.12.2024
 */
public abstract class ProcessorFactoryImpl<C extends ProcessingContext, T, S, P extends ProcessorImpl<C, T, S>> implements ProcessorFactory<T, S, P> {

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.processor.ProcessorFactory#createProcessor(java.lang.Object, java.lang.Class)
	 */
	@Override
	public P createProcessor(S source, Class<T> targetType) {
		requireNonNull(source);
		requireNonNull(targetType);
		Class<P> processorClass = getProcessorClass(targetType);
		requireNonNull(processorClass);
		C context = getContext();
		requireNonNull(context);
		try {
			Constructor<P> constructor = processorClass.getDeclaredConstructor(source.getClass(), context.getClass());
			requireNonNull(constructor);
			P processor = constructor.newInstance(source, context);
			return processor;
		} catch (Exception e) {
			throw new IllegalStateException("Error creating processor", e);
		}
	}
	
	abstract protected Class<P> getProcessorClass(Class<T> targetType);
	
	abstract protected C getContext();

}
