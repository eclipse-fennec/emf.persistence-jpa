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
package org.eclipse.fennec.persistence.eclipselink.dynamic;

import static java.util.Objects.nonNull;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.persistence.eorm.GenerationType;
import org.eclipse.fennec.persistence.eorm.Id;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.UUIDSequence;

/**
 * Configures primary key fields and sequencing on EclipseLink descriptors.
 * Supports single IDs, composite IDs, UUID generation, and sequence generators.
 *
 * @author Mark Hoffmann
 * @since 15.04.2026
 */
class IdConfigurator {

	private static final Logger LOG = Logger.getLogger(IdConfigurator.class.getName());

	private final BuilderOperations ops;

	IdConfigurator(BuilderOperations ops) {
		this.ops = ops;
	}

	/**
	 * Configures ID fields — dispatches to single or composite ID handling.
	 * @param eDynamicType the dynamic type
	 * @param ids a list of IDs
	 */
	void configureIds(EDynamicType eDynamicType, List<Id> ids) {
		EClass eClass = eDynamicType.getEClass();

		if (ids.isEmpty()) {
			LOG.log(Level.WARNING, "No IDs specified for entity {0}", eClass.getName());
			return;
		}

		if (ids.size() == 1) {
			configureSingleId(eDynamicType, ids.get(0));
		} else {
			configureCompositeIds(eDynamicType, ids);
		}
	}

	private void configureSingleId(EDynamicType eDynamicType, Id id) {
		EClass eClass = eDynamicType.getEClass();
		String seqName = "SEQ_" + eClass.getName().toUpperCase();
		String idName = nonNull(id.getColumn()) ? id.getColumn().getName() : id.getName();
		String seqGenName = null;
		Sequence sequence = null;

		if (nonNull(id.getSequenceGenerator())) {
			seqGenName = id.getSequenceGenerator().getSequenceName();
		}
		if (nonNull(id.getGeneratedValue()) &&
				GenerationType.UUID.equals(id.getGeneratedValue().getStrategy())) {
			sequence = new UUIDSequence();
		}

		ops.setPrimaryKeyFields(idName);
		ops.configureSequencing("SEQ_GEN", idName);
		if (nonNull(seqGenName)) {
			ops.configureSequencing(seqGenName, idName);
			if (nonNull(sequence)) {
				ops.configureSequencing(sequence, seqName, idName);
			}
		}

		LOG.log(Level.FINE, "Configured single ID: {0} for entity {1}", new Object[]{idName, eClass.getName()});
	}

	private void configureCompositeIds(EDynamicType eDynamicType, List<Id> ids) {
		EClass eClass = eDynamicType.getEClass();
		LOG.log(Level.FINE, "Configuring composite ID with {0} fields for entity {1}",
			new Object[]{ids.size(), eClass.getName()});

		String[] idFieldNames = new String[ids.size()];
		for (int i = 0; i < ids.size(); i++) {
			Id id = ids.get(i);
			String fieldName = nonNull(id.getColumn()) ? id.getColumn().getName() : id.getName();
			idFieldNames[i] = fieldName;

			LOG.log(Level.FINER, "ID field {0}: {1}", new Object[]{i + 1, fieldName});

			configureIdSequencing(id, fieldName, eClass);
		}

		ops.setPrimaryKeyFields(idFieldNames);

		LOG.log(Level.FINE, "Successfully configured composite primary key: [{0}] for {1}",
			new Object[]{String.join(", ", idFieldNames), eClass.getName()});
	}

	private void configureIdSequencing(Id id, String fieldName, EClass eClass) {
		if (nonNull(id.getSequenceGenerator())) {
			String seqGenName = id.getSequenceGenerator().getSequenceName();
			ops.configureSequencing(seqGenName, fieldName);
			LOG.log(Level.FINER, "Configured sequence: {0} for field {1}", new Object[]{seqGenName, fieldName});
		}

		if (nonNull(id.getGeneratedValue()) &&
			GenerationType.UUID.equals(id.getGeneratedValue().getStrategy())) {

			String seqName = "SEQ_" + eClass.getName().toUpperCase() + "_" + fieldName.toUpperCase();
			Sequence sequence = new UUIDSequence();
			ops.configureSequencing(sequence, seqName, fieldName);
			LOG.log(Level.FINER, "Configured UUID generation for field {0}", fieldName);
		}
	}
}
