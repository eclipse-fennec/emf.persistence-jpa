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

import static java.util.Objects.requireNonNull;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.persistence.eorm.BaseRef;
import org.eclipse.fennec.persistence.eorm.CascadeType;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.EmptyType;
import org.eclipse.fennec.persistence.orm.MappingContext;
import org.eclipse.fennec.persistence.orm.helper.MappingHelper;

/**
 * 
 * @author mark
 * @since 29.12.2024
 */
public abstract class BaseReferenceProcessor<T extends BaseRef> extends NamedBaseProcessor<T, EReference> {
	
	private boolean needMappedBy = false;
	private boolean containmentOnly = false;
	private boolean doOppositeMapping = false;
	
	/**
	 * Creates a new instance.
	 */
	BaseReferenceProcessor(EReference reference, MappingContext helper) {
		super(reference, helper);
	}
	
	public BaseReferenceProcessor<T> withContaintmentOnly() {
		containmentOnly = true;
		return this;
	}
	
	public BaseReferenceProcessor<T> withOppositeMapping() {
		doOppositeMapping = true;
		return this;
	}
	
	public BaseReferenceProcessor<T> withMappedBy() {
		needMappedBy = true;
		return this;
	}
	
	/**
	 * Sets the needMappedBy.
	 * @param needMappedBy the needMappedBy to set
	 */
	public void setNeedMappedBy(boolean needMappedBy) {
		this.needMappedBy = needMappedBy;
	}
	
	/**
	 * Returns the needMappedBy.
	 * @return the needMappedBy
	 */
	public boolean isNeedMappedBy() {
		return needMappedBy;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.processor.ProcessorImpl#canProcess()
	 */
	@Override
	public boolean canProcess() {
		if (isContainmentOnlyMapping()) {
			return isContainment();
		}
		return super.canProcess();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.processor.NamedBaseProcessor#internalProcess()
	 */
	@Override
	protected boolean internalProcess() {
		boolean ip = super.internalProcess();
		createBaseRef();
		calculateCascadeType();
		return ip;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.processor.ProcessorImpl#doReProcess()
	 */
	@Override
	protected void doReProcess() {
		// Nothing to do here
	}
	
	/**
	 * Returns the doOppositeMapping.
	 * @return the doOppositeMapping
	 */
	boolean isOppositeMapping() {
		return doOppositeMapping;
	}

	/**
	 * Returns the containmentOnly.
	 * @return the containmentOnly
	 */
	boolean isContainmentOnlyMapping() {
		return containmentOnly;
	}

	boolean isOpposite() {
		return context.containsOpposite(source);
	}
	
	boolean isContainment() {
		return source.isContainment();
	}
	
	/**
	 * Creates a {@link BaseRef} out of an {@link EStructuralFeature}
	 * @param <T> the base type
	 * @return the {@link BaseRef} instance
	 */
	T createBaseRef() {
		return MappingHelper.createBaseRef(target, source);
	}
	
	/**
	 * Calculates the {@link CascadeType} for the mapping
	 */
	void calculateCascadeType() {
		requireNonNull(source);
		CascadeType cascade = EORMFactory.eINSTANCE.createCascadeType();
		EmptyType empty = EORMFactory.eINSTANCE.createEmptyType();
		if (source.isContainment()) {
			// Containment: Complete lifecycle management
		    // Parent deletion -> children deletion
			cascade.setCascadeAll(empty);
		} else {
			// Non-Containment: Only operational cascades, NO remove cascade
		    // Referenced objects should survive parent deletion
		    cascade.setCascadePersist(EcoreUtil.copy(empty));
		    cascade.setCascadeDetach(EcoreUtil.copy(empty));
		    cascade.setCascadeRefresh(EcoreUtil.copy(empty));
		    // Intentionally NO setCascadeRemove() for non-containment references
		}
		target.setCascade(cascade);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.processor.NamedBaseProcessor#doPostProcess()
	 */
	@Override
	void doPostProcess() {
		// only register, if there is no delegate mapping used
		if (!isDelegate()) {
			context.registerRefMapping(source, this);
			context.registerRefMapping(source, target);
		}
	}
	
}
