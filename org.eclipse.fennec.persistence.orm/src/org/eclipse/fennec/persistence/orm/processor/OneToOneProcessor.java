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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.eclipse.fennec.persistence.orm.helper.MappingHelper.isOppositeRelation;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.persistence.eorm.BaseRef;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.JoinColumn;
import org.eclipse.fennec.persistence.eorm.OneToOne;
import org.eclipse.fennec.persistence.orm.MappingContext;
import org.eclipse.fennec.persistence.orm.MappingContext.MappedBy;

/**
 * One-To-One mapping processor
 * @author Mark Hoffmann
 * @since 29.12.2024
 */
public class OneToOneProcessor extends BaseReferenceProcessor<OneToOne> {

	private static final Logger LOG = Logger.getLogger(OneToOneProcessor.class.getName());

	/**
	 * Creates a new instance.
	 * @param reference
	 * @param context
	 */
	public OneToOneProcessor(EReference reference, MappingContext helper) {
		super(reference, helper);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.processor.BaseReferenceProcessor#canProcess()
	 */
	@Override
	public boolean canProcess() {
		if (source.isMany()) {
			return false;
		}
		
		if (isOppositeRelation(source) && source.getEOpposite().isMany()) {
			return false;
		}
			
		if (isContainmentOnlyMapping() && isOppositeMapping()) {
			return false;
		}
		if (isOpposite() && isContainmentOnlyMapping()) {
			return false;
		}
		if (!isContainment() && isContainmentOnlyMapping()) {
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
		target.setOrphanRemoval(source.isContainment());
		if (isOppositeMapping()) {
			EReference opposite = source.getEOpposite();
			BaseRef mapping = context.getMapping(source);
			MappedBy mappedBy = context.getMappedBy(opposite);
			if (mapping instanceof OneToOne mbRef && nonNull(mappedBy)) {
				mbRef.setMappedBy(mappedBy.mappedByName);
				// Clean up redundant owning-side info — inverse only needs mappedBy
				mbRef.getJoinColumn().clear();
				mbRef.setForeignKey(null);
				mbRef.setJoinTable(null);
			} else {
				LOG.log(Level.SEVERE,
						"Cannot wire bidirectional pair {0} <-> {1}: own mapping is {2}",
						new Object[] { source.getName(), opposite.getName(),
								isNull(mapping) ? "missing" : mapping.eClass().getName() });
			}
			// Always delegate in the opposite pass — this processor must never emit an
			// own (blank) mapping; the owning mapping was created in stage 4.
			setDelegate(true);
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
		setNeedMappedBy(isOppositeMapping() ? false : nonNull(source.getEOpposite()));
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.processor.NamedBaseProcessor#createMapping()
	 */
	@Override
	OneToOne createMapping() {
		return EORMFactory.eINSTANCE.createOneToOne();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.processor.NamedBaseProcessor#addMappingToEntity(org.eclipse.fennec.persistence.eorm.Entity)
	 */
	@Override
	void addMappingToEntity(Entity entity) {
		entity.getAttributes().getOneToOne().add(target);
	}

}
