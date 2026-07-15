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
package org.eclipse.fennec.persistence.eclipselink.mappings;

import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.ManyToOneMapping;

/**
 * {@link ManyToOneMapping} variant that is aware of EMF lazy proxies (AP-48) — see
 * {@link EOneToOneMapping} for the semantics.
 *
 * @author Mark Hoffmann
 * @since 15.07.2026
 */
public class EManyToOneMapping extends ManyToOneMapping implements EMFConfigurableMapping {

	/** serialVersionUID */
	private static final long serialVersionUID = -1736558141287168157L;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.mappings.ObjectReferenceMapping#cascadeDiscoverAndPersistUnregisteredNewObjects(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.eclipse.persistence.internal.sessions.UnitOfWorkImpl, java.util.Set)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void cascadeDiscoverAndPersistUnregisteredNewObjects(Object object, Map newObjects,
			Map unregisteredExistingObjects, Map visitedObjects, UnitOfWorkImpl uow, Set cascadeErrors) {
		if (EMappingSupport.holdsUnresolvedProxy(this, object)) {
			return;
		}
		super.cascadeDiscoverAndPersistUnregisteredNewObjects(object, newObjects, unregisteredExistingObjects,
				visitedObjects, uow, cascadeErrors);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.mappings.ObjectReferenceMapping#cascadeRegisterNewIfRequired(java.lang.Object, org.eclipse.persistence.internal.sessions.UnitOfWorkImpl, java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
		if (EMappingSupport.holdsUnresolvedProxy(this, object)) {
			return;
		}
		super.cascadeRegisterNewIfRequired(object, uow, visitedObjects);
	}
}
