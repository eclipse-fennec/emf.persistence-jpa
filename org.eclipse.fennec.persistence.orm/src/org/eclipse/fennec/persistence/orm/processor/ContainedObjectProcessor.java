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

import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.persistence.converter.ContainedObjectConverter;
import org.eclipse.fennec.persistence.eorm.Basic;
import org.eclipse.fennec.persistence.eorm.Convert;
import org.eclipse.fennec.persistence.eorm.EORMFactory;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.eorm.FetchType;
import org.eclipse.fennec.persistence.orm.MappingContext;
import org.eclipse.fennec.persistence.orm.helper.MappingHelper;

/**
 * Maps a containment reference whose target class is no entity of the unit as one encoded column
 * of its parent (issue #363): a {@link Basic} on the reference, a {@code Lob}, converted by the
 * {@link ContainedObjectConverter} of the reference's cardinality.
 * <p>
 * Without it the child had no table to go to and was skipped when the unit was built — the
 * geometry of a model referencing the GeoJSON package vanished on every save. The child now
 * travels with its parent as an atomic value; it cannot be queried into, as a row of its own
 * could, and it needs no key, as a row of its own would.
 *
 * @author Juergen Albert
 * @since 01.10.2026
 */
public class ContainedObjectProcessor extends NamedBaseProcessor<Basic, EReference> {

	/**
	 * Creates a new instance.
	 * @param reference the containment reference
	 * @param context the mapping context
	 */
	public ContainedObjectProcessor(EReference reference, MappingContext context) {
		super(reference, context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.orm.processor.NamedBaseProcessor#internalProcess()
	 */
	@Override
	protected boolean internalProcess() {
		MappingHelper.createBase(target, source, isStrict(), context);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.processor.ProcessorImpl#doProcess()
	 */
	@Override
	protected void doProcess() {
		target.setOptional(!source.isRequired());
		target.setFetch(FetchType.EAGER);
		target.setLob(EORMFactory.eINSTANCE.createLob());
		Convert convert = EORMFactory.eINSTANCE.createConvert();
		convert.setConverter(source.isMany() ? ContainedObjectConverter.NAME_MANY : ContainedObjectConverter.NAME);
		target.setConvert(convert);
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
	 * @see org.eclipse.fennec.persistence.orm.processor.NamedBaseProcessor#doPostProcess()
	 */
	@Override
	void doPostProcess() {
		// nothing to do: the column is complete after doProcess
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
