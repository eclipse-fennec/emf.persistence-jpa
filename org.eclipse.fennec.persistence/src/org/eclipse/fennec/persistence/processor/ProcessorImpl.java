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

/**
 * Base processor that wires a source object, a context and a target together,
 * enforces the single-process contract (a processor runs at most once) and
 * provides hooks for subclasses to create the target instance and execute the
 * transformation logic.
 *
 * @param <C> the context type
 * @param <T> the target type
 * @param <S> the source type
 * @author Mark Hoffmann
 * @since 29.12.2024
 */
public abstract class ProcessorImpl<C extends ProcessingContext, T, S> implements Processor<T, S> {

	protected final S source;
	protected final T target;
	protected final C context;
	protected boolean processed = false;
	private boolean delegate = false;
	private boolean strict = false;
	
	/**
	 * Creates a new instance.
	 */
	public ProcessorImpl(S source, C context) {
		this.context = context;
		this.source = source;
		this.target = createTarget();
		requireNonNull(this.target);
	}
	
	
	/**
	 * Returns <code>true</code>, if the processor runs in strict mode.
	 * This indicated to take the model as it is. 
	 * @return <code>true</code>, if the processor runs in strict mode 
	 */
	public boolean isStrict() {
		return strict;
	}
	
	public void setStrict(boolean strict) {
		this.strict = strict;
	}
	
	/**
	 * Creates the target instance
	 * @return the target instance
	 */
	protected abstract T createTarget();
	
	/**
	 * Does the processing
	 * @return this processor for lambda uses
	 */
	protected abstract void doProcess();
	
	/**
	 * Sets <code>true</code> to indicate, not to used the original mapping
	 * @param delegate if processing is delegating mode
	 */
	protected void setDelegate(boolean delegate) {
		this.delegate = delegate;
	}
	
	/**
	 * Returns if delegate is active or not.
	 * @return if delegate is active or not.
	 */
	protected boolean isDelegate() {
		return delegate;
	}
	
	/**
	 * Does the re-processing
	 * @return this processor for lambda uses
	 */
	protected void doReProcess() {
		// Nothing to do here
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.helper.accessor.Processor#getSource()
	 */
	@Override
	public S getSource() {
		return source;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.helper.accessor.Processor#getTarget()
	 */
	@Override
	public T getTarget() {
		T value = delegate ? null : target;
		return isProcessed() ? value : null;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.helper.accessor.Processor#process()
	 */
	@Override
	public Processor<T, S> process() {
		if (isProcessed()) {
			return this;
		}
		if (canProcess()) {
			doProcess();
			processed = true;
		}
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.helper.accessor.Processor#reProcess()
	 */
	@Override
	public Processor<T, S> reProcess() {
		if (isProcessed()) {
			doReProcess();
		}
		return this;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.helper.accessor.Processor#canProcess()
	 */
	@Override
	public boolean canProcess() {
		return true;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.helper.accessor.Processor#isProcessed()
	 */
	@Override
	public boolean isProcessed() {
		return processed;
	}

}
