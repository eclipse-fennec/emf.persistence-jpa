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
package org.eclipse.fennec.persistence.processor;

/**
 * Simple processor interface
 * @param <T> the target type
 * @param <S> the source type
 * @author Mark Hoffmann
 * @since 29.12.2024
 */
public interface Processor<T, S> {
	
	/**
	 * Returns the source object
	 * @return the source object
	 */
	S getSource();
	
	/**
	 * Returns the target object
	 * @return the target object
	 */
	T getTarget();
	
	/**
	 * Returns <code>true</code>, if the accessor can be processed, otherwise <code>false</code>
	 * @return <code>true</code>, if the accessor can be processed, otherwise <code>false</code>
	 */
	boolean canProcess();
	
	/**
	 * Executes the processing
	 * @return the processor instance for lambda use
	 */
	Processor<T, S> process();
	
	/**
	 * Executes a re-processing. This should only work when isProcessed returns <code>true</code>
	 * @return the processor instance for lambda use
	 */
	Processor<T, S> reProcess();
	
	/**
	 * Returns <code>true</code>, if processing was successful, otherwise <code>false</code>
	 * @return <code>true</code>, if processing was successful, otherwise <code>false</code>
	 */
	boolean isProcessed();

}
