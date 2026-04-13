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
package org.eclipse.fennec.persistence.eclipselink.resource;

import static java.util.Objects.requireNonNull;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import jakarta.persistence.EntityManagerFactory;

/**
 * Factory for creating {@link JPAResourceImpl} instances.
 * <p>
 * Register with EMF's ResourceSet for the {@code jpa} URI scheme:
 * <pre>
 * resourceSet.getResourceFactoryRegistry()
 *     .getProtocolToFactoryMap()
 *     .put("jpa", new JPAResourceFactory(emf));
 * </pre>
 *
 * @author Mark Hoffmann
 * @since 13.04.2026
 */
public class JPAResourceFactory implements Resource.Factory {

	private final EntityManagerFactory emf;

	public JPAResourceFactory(EntityManagerFactory emf) {
		requireNonNull(emf, "EntityManagerFactory is required");
		this.emf = emf;
	}

	@Override
	public Resource createResource(URI uri) {
		return new JPAResourceImpl(uri, emf);
	}
}
