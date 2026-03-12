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

import static org.eclipse.fennec.persistence.orm.helper.MappingHelper.isOppositeRelation;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.persistence.eorm.BaseRef;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.JoinTable;
import org.eclipse.fennec.persistence.eorm.ManyToMany;
import org.eclipse.fennec.persistence.orm.MappingContext;
import org.eclipse.fennec.persistence.orm.MappingContext.MappedBy;

/**
 * One-To-Many mapping processor
 * @author Mark Hoffmann
 * @since 29.12.2024
 */
public class ManyToManyProcessor extends BaseReferenceProcessor<ManyToMany> {
	
	/**
	 * Creates a new instance.
	 * @param reference
	 * @param context
	 */
	public ManyToManyProcessor(EReference reference, MappingContext helper) {
		super(reference, helper);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.processor.BaseReferenceProcessor#canProcess()
	 */
	@Override
	public boolean canProcess() {
		if (!source.isMany()) {
			return false;
		}
		// many-to-many cannot mapped in containments
		if (isOppositeRelation(source) && (!source.isMany() || !source.getEOpposite().isMany())) {
			return false;
		}
		if (isContainmentOnlyMapping() || context.containsMapping(source) || context.containsOpposite(source)) {
			return false;
		}
		return super.canProcess();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.processor.ProcessorImpl#doProcess()
	 */
	@Override
	protected void doProcess() {
		if (isOppositeMapping()) {
			EReference opposite = source.getEOpposite();
			BaseRef mapping = context.getMapping(opposite);
			if (mapping instanceof ManyToMany mbRef) {
				MappedBy mappedBy = context.getMappedBy(source);
				mbRef.setMappedBy(mappedBy.mappedByName);
				setDelegate(true);
			}
		} else {
			JoinTable jt = createJoinTable(source);
			target.setJoinTable(jt);
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.processor.BaseReferenceProcessor#createMapping()
	 */
	@Override
	ManyToMany createMapping() {
		return EORMFactory.eINSTANCE.createManyToMany();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.processor.NamedBaseProcessor#addMappingToEntity(org.eclipse.fennec.persistence.eorm.Entity)
	 */
	@Override
	void addMappingToEntity(Entity entity) {
		entity.getAttributes().getManyToMany().add(target);	
	}

}
