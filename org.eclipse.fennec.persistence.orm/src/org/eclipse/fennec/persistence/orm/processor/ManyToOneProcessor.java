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

import static java.util.Objects.nonNull;
import static org.eclipse.fennec.persistence.orm.helper.MappingHelper.isOppositeRelation;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.persistence.eorm.BaseRef;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.JoinColumn;
import org.eclipse.fennec.persistence.eorm.ManyToOne;
import org.eclipse.fennec.persistence.eorm.OneToMany;
import org.eclipse.fennec.persistence.orm.MappingContext;
import org.eclipse.fennec.persistence.orm.MappingContext.MappedBy;

/**
 * One-To-Many mapping processor
 * @author Mark Hoffmann
 * @since 29.12.2024
 */
public class ManyToOneProcessor extends BaseReferenceProcessor<ManyToOne> {

	/**
	 * Creates a new instance.
	 * @param reference
	 * @param context
	 */
	public ManyToOneProcessor(EReference reference, MappingContext helper) {
		super(reference, helper);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.processor.BaseReferenceProcessor#canProcess()
	 */
	@Override
	public boolean canProcess() {
		// many-to-one cannot mapped in containments
		if (isContainmentOnlyMapping()) {
			return false;
		}
		/*
		 * Only map many-to-one if opposites are configured:
		 * - An opposite is needed, 
		 * - that is many and 
		 * - we are not many
		 */
		if (!isOppositeRelation(source) || 
				source.isMany() ||
				!source.getEOpposite().isMany()) {
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
		target.setOptional(!source.isRequired());
		/*
		 * Many-to-one usually always set the mappedBy
		 */
		if (isOppositeMapping()) {
			EReference opposite = source.getEOpposite();
			BaseRef mapping = context.getMapping(opposite);
			if (mapping instanceof OneToMany mbRef) {
				MappedBy mappedBy = context.getMappedBy(source);
				if (nonNull(mappedBy)) {
					mbRef.setMappedBy(mappedBy.mappedByName);
					setDelegate(true);
				}
			}
		} else {
			JoinColumn jc = createJoinColumn(source.getEReferenceType());
			if (nonNull(jc)) {
				/*
				 * Create a join column in the source table with a foreign key to the target/referenced 
				 * table id-column.
				 * We cover all cases where not EOpposite is defined, but a bi-directionallity exists 
				 * nevertheless.
				 * This setting is also correct for a containment reference, that has an EOpposite. So
				 * it acts as owning side, where the target side then uses the mappedBy setting, that is 
				 * done when you are in an opposite mapping (see above)
				 */
				jc.setTable(source.getEReferenceType().getName());
				target.setForeignKey(createForeignKey(jc.getName()));
				target.getJoinColumn().add(jc);
			}
		}
		setNeedMappedBy(false);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.processor.NamedBaseProcessor#createMapping()
	 */
	@Override
	ManyToOne createMapping() {
		return EORMFactory.eINSTANCE.createManyToOne();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.processor.NamedBaseProcessor#addMappingToEntity(org.eclipse.fennec.persistence.eorm.Entity)
	 */
	@Override
	void addMappingToEntity(Entity entity) {
		entity.getAttributes().getManyToOne().add(target);	
	}

}
