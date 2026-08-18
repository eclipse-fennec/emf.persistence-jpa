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
package org.eclipse.fennec.persistence.eclipselink.spi;

import static java.util.Objects.requireNonNull;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.eclipselink.resource.JPAResourceImpl;

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
	private final ConverterService converters;

	public JPAResourceFactory(EntityManagerFactory emf) {
		this(emf, null);
	}

	/**
	 * Variant with an explicit {@link ConverterService} handed to every created resource
	 * (issue #164) — pass the service the persistence unit's mappings were built with, so
	 * query-side literal/parameter conversion matches the columns. {@code null} keeps the
	 * resources' stock converter set.
	 */
	public JPAResourceFactory(EntityManagerFactory emf, ConverterService converters) {
		requireNonNull(emf, "EntityManagerFactory is required");
		this.emf = emf;
		this.converters = converters;
	}

	@Override
	public Resource createResource(URI uri) {
		JPAResourceImpl resource = new JPAResourceImpl(uri, emf);
		if (converters != null) {
			resource.setConverterService(converters);
		}
		return resource;
	}
}
