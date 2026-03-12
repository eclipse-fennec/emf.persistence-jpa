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

import static java.util.Objects.nonNull;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.persistence.eorm.CollectionTable;
import org.eclipse.fennec.persistence.eorm.Column;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.ElementCollection;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.JoinColumn;
import org.eclipse.fennec.persistence.orm.MappingContext;

/**
 * Processor for {@link ElementCollection} elements
 * @author Mark Hoffmann
 * @since 29.12.2024
 */
public class ElementCollectionProcessor extends BaseProcessor<ElementCollection> {

	/**
	 * Creates a new instance.
	 * @param feature
	 * @param helper
	 */
	public ElementCollectionProcessor(EAttribute feature, MappingContext helper) {
		super(feature, helper);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.processor.ProcessorImpl#doProcess()
	 */
	@Override
	protected void doProcess() {
		EClass c = source.getEContainingClass();
		String tableName = c.getName().toUpperCase() + "__" + source.getName().toUpperCase();
		String valueColumn = "VAL_" + source.getName().toUpperCase();
		Column col = target.getColumn();
		col.setName(valueColumn);
		CollectionTable ct = EORMFactory.eINSTANCE.createCollectionTable();
		ct.setName(tableName);
		JoinColumn jc = createJoinColumn(c);
		if (nonNull(jc)) {
			jc.setTable(tableName);
			ct.setForeignKey(createForeignKey(jc.getName()));
			ct.getJoinColumn().add(jc);
		}
		target.setCollectionTable(ct);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.processor.NamedBaseProcessor#createMapping()
	 */
	@Override
	ElementCollection createMapping() {
		return EORMFactory.eINSTANCE.createElementCollection();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.processor.NamedBaseProcessor#addMappingToEntity(org.eclipse.fennec.persistence.eorm.Entity)
	 */
	@Override
	void addMappingToEntity(Entity entity) {
		entity.getAttributes().getElementCollection().add(target);
	}

}
