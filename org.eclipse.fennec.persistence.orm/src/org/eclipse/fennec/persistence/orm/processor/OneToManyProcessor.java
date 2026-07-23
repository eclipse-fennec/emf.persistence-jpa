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

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.persistence.eorm.BaseRef;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.Id;
import org.eclipse.fennec.persistence.eorm.JoinColumn;
import org.eclipse.fennec.persistence.eorm.JoinTable;
import org.eclipse.fennec.persistence.eorm.MappedByRef;
import org.eclipse.fennec.persistence.eorm.OneToMany;
import org.eclipse.fennec.persistence.orm.MappingContext;
import org.eclipse.fennec.persistence.orm.MappingContext.MappedBy;

/**
 * One-To-Many mapping processor that correctly handles EMF containment semantics.
 * 
 * <p>This processor distinguishes between containment and non-containment OneToMany relationships:</p>
 * <ul>
 * <li><b>Containment relationships</b> use JoinColumn (foreign key in child table) for proper EMF containment semantics</li>
 * <li><b>Non-containment relationships</b> use JoinTable (separate association table) for loose associations</li>
 * <li><b>Bidirectional relationships</b> are handled via mappedBy references to the owning side</li>
 * </ul>
 * 
 * <p>The generated EORM mappings are later converted to EclipseLink mappings by EDynamicTypeBuilder,
 * which uses UnidirectionalOneToManyMapping for containment relationships to ensure proper 
 * foreign key management during cascade persistence operations.</p>
 * 
 * @author Mark Hoffmann
 * @since 29.12.2024
 */
public class OneToManyProcessor extends BaseReferenceProcessor<OneToMany> {


	/**
	 * Creates a new instance.
	 * @param reference
	 * @param context
	 */
	public OneToManyProcessor(EReference reference, MappingContext helper) {
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
		// use many-to-many instead
		if (isOppositeRelation(source) && source.getEOpposite().isMany()) {
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
			MappedBy mappedBy = context.getMappedBy(source);
			if (mapping instanceof MappedByRef mbRef && nonNull(mappedBy)) {
				mbRef.setMappedBy(mappedBy.mappedByName);
				// Clean up redundant owning-side info — inverse only needs mappedBy
				if (mapping instanceof OneToMany o2m) {
					o2m.getJoinColumn().clear();
				}
				mapping.setForeignKey(null);
				mapping.setJoinTable(null);
			} else {
				context.error(MappingContext.DIAGNOSTIC_SOURCE,
						String.format("Cannot wire bidirectional pair %s <-> %s: opposite mapping is %s",
								source.getName(), opposite.getName(),
								isNull(mapping) ? "missing" : mapping.eClass().getName()),
						source);
			}
			// Always delegate in the opposite pass — this processor must never emit an
			// own (blank) mapping; the owning mapping was created in stage 4.
			setDelegate(true);
		} else if (source.isContainment()) {
			// CONTAINMENT: Use JoinColumn (FK in target table)
			// OrphanRemoval=true: children are deleted when removed from parent collection
			target.setOrphanRemoval(true);
			JoinColumn jc = createContainmentJoinColumn(source);
			if (nonNull(jc)) {
				target.getJoinColumn().add(jc);
				target.setForeignKey(createForeignKey(jc.getName()));
			}
		} else {
			// NON-CONTAINMENT: Use JoinTable
			// OrphanRemoval=false: referenced objects survive removal from collection
			target.setOrphanRemoval(false);
			JoinTable jt = createJoinTable(source);
			target.setJoinTable(jt);
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.processor.NamedBaseProcessor#createMapping()
	 */
	@Override
	OneToMany createMapping() {
		return EORMFactory.eINSTANCE.createOneToMany();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.processor.NamedBaseProcessor#addMappingToEntity(org.eclipse.fennec.persistence.eorm.Entity)
	 */
	@Override
	void addMappingToEntity(Entity entity) {
		entity.getAttributes().getOneToMany().add(target);	
	}

	/**
	 * Creates a specialized JoinColumn for containment relationships.
	 * For containment, we need the foreign key to point from the child table to the parent,
	 * using a column name like "parent_id".
	 * 
	 * @param reference the containment reference
	 * @return the JoinColumn or null
	 */
	private JoinColumn createContainmentJoinColumn(EReference reference) {
		EClass parentClass = reference.getEContainingClass();
		EClass childClass = reference.getEReferenceType();
		
		if (isNull(parentClass) || isNull(childClass)) {
			return null;
		}
		
		Entity parentEntity = context.getEntity(parentClass);
		if (isNull(parentEntity) || parentEntity.getAttributes().getId().isEmpty()) {
			return null;
		}
		
		JoinColumn jc = EORMFactory.eINSTANCE.createJoinColumn();
		Id parentId = parentEntity.getAttributes().getId().get(0);
		
		// For containment: FK column is "parent_id" in child table
		String fkColumnName = parentClass.getName().toLowerCase() + "_id";
		jc.setName(fkColumnName);
		
		// Referenced column is just the parent's primary key column name
		jc.setReferencedColumnName(parentId.getColumn().getName());
		
		// For containment relationships, the FK should not be nullable
		jc.setNullable(false);
		
		// Ensure the column is insertable and updatable (defaults should be true but let's be explicit)
		jc.setInsertable(true);
		jc.setUpdatable(true);
		
		return jc;
	}


}
