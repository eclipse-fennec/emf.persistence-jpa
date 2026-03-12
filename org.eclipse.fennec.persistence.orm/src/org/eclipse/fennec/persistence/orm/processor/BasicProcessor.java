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
package org.eclipse.fennec.persistence.orm.processor;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.fennec.persistence.eorm.Basic;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.orm.MappingContext;

/**
 * Processor for {@link Basic} elements
 * @author Mark Hoffmann
 * @since 29.12.2024
 */
public class BasicProcessor extends BaseProcessor<Basic> {

	/**
	 * Creates a new instance.
	 * @param feature
	 * @param helper
	 */
	public BasicProcessor(EAttribute feature, MappingContext helper) {
		super(feature, helper);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.processor.ProcessorImpl#doProcess()
	 */
	@Override
	protected void doProcess() {
		target.setOptional(!source.isRequired());
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.processor.NamedBaseProcessor#createMapping()
	 */
	@Override
	Basic createMapping() {
		return EORMFactory.eINSTANCE.createBasic();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.processor.NamedBaseProcessor#addMappingToEntity(org.eclipse.fennec.persistence.eorm.Entity)
	 */
	@Override
	void addMappingToEntity(Entity entity) {
		entity.getAttributes().getBasic().add(target);
	}

}
