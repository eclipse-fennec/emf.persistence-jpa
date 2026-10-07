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
import org.eclipse.fennec.persistence.eclipselink.JpaFlavor;
import org.eclipse.fennec.persistence.eclipselink.query.JpaQueryProcessor;
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
	private final JpaFlavor flavor;
	private final boolean nullOrderFallback;

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
		this(emf, converters, null);
	}

	/**
	 * Variant that names the database flavor behind {@code emf}, so the resources declare and
	 * render for that database (issues #172, #365) — what the OSGi factory takes from the
	 * unit's service properties. {@code null} keeps the portable baseline, which renders
	 * everything in a form every targeted database understands.
	 */
	public JPAResourceFactory(EntityManagerFactory emf, ConverterService converters, JpaFlavor flavor) {
		this(emf, converters, flavor, true);
	}

	/**
	 * Variant that also sets the null-order fallback (issue #365): {@code false} refuses a null
	 * placement the database cannot spell instead of sorting it through a {@code CASE} key —
	 * the counterpart of the unit configuration {@code nullOrderFallback}.
	 */
	public JPAResourceFactory(EntityManagerFactory emf, ConverterService converters, JpaFlavor flavor,
			boolean nullOrderFallback) {
		requireNonNull(emf, "EntityManagerFactory is required");
		this.emf = emf;
		this.converters = converters;
		this.flavor = flavor;
		this.nullOrderFallback = nullOrderFallback;
	}

	@Override
	public Resource createResource(URI uri) {
		JPAResourceImpl resource = new JPAResourceImpl(uri, emf);
		if (converters != null) {
			resource.setConverterService(converters);
		}
		if (flavor != null || !nullOrderFallback) {
			resource.setQueryProcessor(new JpaQueryProcessor(flavor, nullOrderFallback));
		}
		return resource;
	}
}
