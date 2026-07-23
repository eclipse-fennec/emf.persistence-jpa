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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.emf.osgi.annotation.ConfiguratorType;
import org.eclipse.fennec.emf.osgi.annotation.provide.EMFConfigurator;
import org.eclipse.fennec.persistence.eclipselink.query.JpaQueryProcessor;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAUnit;
import org.eclipse.fennec.persistence.query.QueryConstants;
import org.eclipse.fennec.persistence.query.api.QueryProcessor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * The single {@link Resource.Factory} for the {@code jpa} URI scheme — an emf.osgi
 * whiteboard over all {@link JPAUnit} services (issue #20).
 * <p>
 * The emf.osgi resource-factory registry holds exactly <em>one</em> factory per protocol
 * ({@code protocolToFactoryMap.put} is last-wins), so multiple per-database factories for
 * the {@code jpa} scheme would overwrite each other. Instead this one factory dispatches by
 * URI: {@code jpa://<puName>/<Entity>} → the {@link JPAUnit} service whose
 * {@code osgi.unit.name} matches the URI authority.
 * <p>
 * Units are tracked as {@link ServiceReference}s and their name is read <em>from the
 * reference properties</em>; the service object is resolved (and cached) only when a URI
 * actually hits the unit. A persistence unit that is never addressed by any URI therefore
 * never builds its EclipseLink factory. A URI addressing an unknown unit still yields a
 * resource — it fails with a clear diagnostic on load/resolve instead of returning
 * {@code null} (no silent fallback to another unit).
 *
 * @author Mark Hoffmann
 * @since 20.07.2026
 */
@Component(name = "JPAResourceFactory", service = Resource.Factory.class, immediate = true)
@EMFConfigurator(configuratorName = "jpa", configuratorType = ConfiguratorType.RESOURCE_FACTORY, protocol = "jpa")
public class JPAResourceFactoryComponent implements Resource.Factory {

	private static final Logger LOG = Logger.getLogger(JPAResourceFactoryComponent.class.getName());

	private final BundleContext ctx;
	/** Unit name → service reference; populated from reference properties only (no getService). */
	private final Map<String, ServiceReference<JPAUnit>> unitRefs = new ConcurrentHashMap<>();
	/** Unit name → resolved service object; filled lazily on the first URI hit per unit. */
	private final Map<String, JPAUnit> resolvedUnits = new ConcurrentHashMap<>();
	/** The processor handed to created resources; empty = resources use their local default. */
	private final AtomicReference<QueryProcessor> queryProcessor = new AtomicReference<>();

	@Activate
	public JPAResourceFactoryComponent(BundleContext ctx) {
		this.ctx = ctx;
	}

	/**
	 * The {@link QueryProcessor} handed to created resources (issue #61). Optional and
	 * greedy: without a matching service the resources fall back to their local default
	 * processor; a registered {@code jpa}-backend service (e.g. reconfigured, decorated
	 * or higher-ranked) takes precedence for all subsequently created resources.
	 */
	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC,
			policyOption = ReferencePolicyOption.GREEDY,
			target = "(" + QueryConstants.BACKEND_PROPERTY + "=" + JpaQueryProcessor.BACKEND + ")")
	void bindQueryProcessor(QueryProcessor processor) {
		queryProcessor.set(processor);
	}

	void unbindQueryProcessor(QueryProcessor processor) {
		// on a greedy rebind DS binds the replacement first — only clear if the
		// departing service is still the bound one
		queryProcessor.compareAndSet(processor, null);
	}

	@Deactivate
	void deactivate() {
		resolvedUnits.clear();
		unitRefs.values().forEach(ctx::ungetService);
		unitRefs.clear();
	}

	/**
	 * Whiteboard: tracks every {@link JPAUnit} service by its {@code osgi.unit.name}
	 * property. Only the reference is stored — the service object (and with it the unit's
	 * EclipseLink factory) stays untouched until a URI addresses the unit.
	 */
	@Reference(service = JPAUnit.class, cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	void addUnit(ServiceReference<JPAUnit> reference) {
		String name = unitName(reference);
		if (isNull(name)) {
			LOG.log(Level.WARNING, "Ignoring JPAUnit service without ''{0}'' property: {1}",
					new Object[] { JPAUnit.UNIT_NAME, reference });
			return;
		}
		ServiceReference<JPAUnit> previous = unitRefs.put(name, reference);
		if (nonNull(previous)) {
			LOG.log(Level.WARNING, "Multiple JPAUnit services for unit ''{0}'' — using the newest one", name);
			resolvedUnits.remove(name);
			ctx.ungetService(previous);
		}
	}

	void removeUnit(ServiceReference<JPAUnit> reference) {
		String name = unitName(reference);
		if (isNull(name)) {
			return;
		}
		if (unitRefs.remove(name, reference) && nonNull(resolvedUnits.remove(name))) {
			ctx.ungetService(reference);
		}
	}

	private static String unitName(ServiceReference<JPAUnit> reference) {
		return reference.getProperty(JPAUnit.UNIT_NAME) instanceof String s ? s : null;
	}

	/**
	 * Creates a {@link JPAResourceImpl} for the unit named by the URI authority. Unknown or
	 * no-longer-available units yield a resource backed by {@link JPAUnit#unavailable} —
	 * operations on it fail with a diagnostic naming the missing unit.
	 */
	@Override
	public Resource createResource(URI uri) {
		String puName = uri.authority();
		if (isNull(puName) || puName.isEmpty()) {
			return newResource(uri, JPAUnit.unavailable(
					"URI '" + uri + "' does not name a persistence unit (expected jpa://<unitName>/<Entity>)"));
		}
		JPAUnit unit = resolveUnit(puName);
		if (isNull(unit)) {
			return newResource(uri, JPAUnit.unavailable(
					"No persistence unit '" + puName + "' is available for URI '" + uri + "'"));
		}
		return newResource(uri, unit);
	}

	private Resource newResource(URI uri, JPAUnit unit) {
		JPAResourceImpl resource = new JPAResourceImpl(uri, unit);
		QueryProcessor processor = queryProcessor.get();
		if (nonNull(processor)) {
			resource.setQueryProcessor(processor);
		}
		return resource;
	}

	/**
	 * Resolves (and caches) the service object for the named unit. The {@code JPAUnit}
	 * service object itself is cheap — resolving it does <em>not</em> build the unit's
	 * EclipseLink factory; that happens on the first leased operation of a resource.
	 */
	private JPAUnit resolveUnit(String puName) {
		JPAUnit resolved = resolvedUnits.get(puName);
		if (nonNull(resolved)) {
			return resolved;
		}
		ServiceReference<JPAUnit> reference = unitRefs.get(puName);
		if (isNull(reference)) {
			return null;
		}
		JPAUnit unit = ctx.getService(reference);
		if (isNull(unit)) {
			// Stale reference — the service vanished between lookup and resolution.
			unitRefs.remove(puName, reference);
			return null;
		}
		JPAUnit previous = resolvedUnits.putIfAbsent(puName, unit);
		if (nonNull(previous)) {
			// Lost a benign race — another thread resolved first; release our extra get.
			ctx.ungetService(reference);
			return previous;
		}
		return unit;
	}
}
