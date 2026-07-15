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

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.queries.DirectReadQuery;

/**
 * {@link ManyToManyMapping} variant that is aware of EMF lazy proxies (AP-47).
 * <p>
 * Provides the ID-only relation-table query backing element-level lazy loading and
 * skips unresolved EMF proxies in the commit-time cascade traversals — a proxy
 * represents an <em>existing</em> row and must never be persisted as a new entity.
 * The relation-table rows themselves are still written from the proxy's id, and
 * non-proxy (really new) elements cascade as usual.
 *
 * @author Mark Hoffmann
 * @since 15.07.2026
 */
public class EManyToManyMapping extends ManyToManyMapping implements EMFConfigurableMapping, ETargetIdQuerySupport {

	/** serialVersionUID */
	private static final long serialVersionUID = -8397929385921305821L;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.eclipselink.mappings.ETargetIdQuerySupport#buildTargetIdQuery(org.eclipse.persistence.internal.sessions.AbstractSession)
	 */
	@Override
	public DirectReadQuery buildTargetIdQuery(AbstractSession session) {
		return EMappingSupport.buildRelationTableIdQuery(this, session);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.mappings.CollectionMapping#cascadeDiscoverAndPersistUnregisteredNewObjects(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.eclipse.persistence.internal.sessions.UnitOfWorkImpl, java.util.Set)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void cascadeDiscoverAndPersistUnregisteredNewObjects(Object object, Map newObjects,
			Map unregisteredExistingObjects, Map visitedObjects, UnitOfWorkImpl uow, Set cascadeErrors) {
		if (!EMappingSupport.cascadeDiscoverSkippingProxies(this, object, newObjects, unregisteredExistingObjects,
				visitedObjects, uow, cascadeErrors)) {
			super.cascadeDiscoverAndPersistUnregisteredNewObjects(object, newObjects, unregisteredExistingObjects,
					visitedObjects, uow, cascadeErrors);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.persistence.mappings.CollectionMapping#cascadeRegisterNewIfRequired(java.lang.Object, org.eclipse.persistence.internal.sessions.UnitOfWorkImpl, java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
		EMappingSupport.cascadeRegisterNewSkippingProxies(this, object, uow, visitedObjects);
	}
}
