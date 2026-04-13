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

import static org.eclipse.fennec.persistence.orm.helper.MappingHelper.isOppositeRelation;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.JoinTable;
import org.eclipse.fennec.persistence.eorm.ManyToMany;
import org.eclipse.fennec.persistence.orm.MappingContext;

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
		if (isContainmentOnlyMapping() || context.containsMapping(source)) {
			return false;
		}
		// In Stage 5 (opposite mapping), we must allow processing even if the
		// reference was registered as opposite — that is exactly what we want to process.
		if (!isOppositeMapping() && context.containsOpposite(source)) {
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
			// This is the inverse/non-owning side of a bidirectional M2M.
			// mappedBy points to the owning side's attribute name (the opposite reference).
			EReference opposite = source.getEOpposite();
			target.setMappedBy(opposite.getName());
			// No JoinTable needed — the owning side already has it.
			// Do NOT setDelegate — this mapping must be added to the entity
			// so that EclipseLink can create a ManyToManyMapping for the reverse direction.
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
