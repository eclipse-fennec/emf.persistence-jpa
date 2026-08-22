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
import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.capabilities.CapabilityDeclaration;
import org.eclipse.fennec.persistence.eclipselink.JpaFlavor;
import org.eclipse.fennec.persistence.eclipselink.query.JpaQueryProcessor;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAUnit;
import org.eclipse.fennec.persistence.query.QueryConstants;
import org.eclipse.fennec.persistence.query.api.QueryProcessor;
import org.eclipse.fennec.persistence.query.support.NamedOperations;
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
	/** The converter service handed to created resources; empty = the stock converter set. */
	private final AtomicReference<ConverterService> converterService = new AtomicReference<>();

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

	/**
	 * The {@link ConverterService} handed to created resources (issue #164) — the same
	 * whiteboard the persistence unit mappings are built with, so query-side value
	 * conversion matches the columns. Optional: without it resources use the stock
	 * converter set.
	 */
	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC,
			policyOption = ReferencePolicyOption.GREEDY)
	void bindConverterService(ConverterService converters) {
		converterService.set(converters);
	}

	void unbindConverterService(ConverterService converters) {
		converterService.compareAndSet(converters, null);
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

	/**
	 * The shared named-operation catalog handed to created resources (issue #203). Optional
	 * and greedy, like the processor: without one the resources keep resolving names through
	 * the {@code FENNEC_QUERIES} table, which is what every deployment does today.
	 */
	private final AtomicReference<NamedOperations> namedOperations = new AtomicReference<>();

	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC,
			policyOption = ReferencePolicyOption.GREEDY)
	void bindNamedOperations(NamedOperations operations) {
		namedOperations.set(operations);
	}

	void unbindNamedOperations(NamedOperations operations) {
		namedOperations.compareAndSet(operations, null);
	}

	private Resource newResource(URI uri, JPAUnit unit) {
		JPAResourceImpl resource = new JPAResourceImpl(uri, unit);
		NamedOperations catalog = namedOperations.get();
		if (nonNull(catalog)) {
			resource.setNamedOperations(catalog);
		}
		QueryProcessor processor = processorFor(uri.authority());
		if (nonNull(processor)) {
			resource.setQueryProcessor(processor);
		}
		ConverterService converters = converterService.get();
		if (nonNull(converters)) {
			resource.setConverterService(converters);
		}
		return resource;
	}

	/**
	 * The processor for a unit's resources, matched to the unit's database flavor
	 * (issue #172).
	 * <p>
	 * A bound service is used as it is when it already declares that flavor — the ordinary
	 * case, since the flavor-less registration declares the portable baseline and a
	 * decorator would be bound deliberately. Otherwise a processor for the unit's flavor is
	 * created, so what a resource refuses matches the database behind it rather than the
	 * union of every relational database.
	 *
	 * @param puName the persistence unit name from the URI authority; may be {@code null}
	 * @return the processor to hand to the resource, or {@code null} to leave its default
	 */
	private QueryProcessor processorFor(String puName) {
		QueryProcessor bound = queryProcessor.get();
		JpaFlavor flavor = flavorOf(puName);
		if (isNull(flavor)) {
			return bound;
		}
		if (bound instanceof JpaQueryProcessor jpa) {
			return jpa.flavor() == flavor ? jpa : new JpaQueryProcessor(flavor);
		}
		return isNull(bound) ? new JpaQueryProcessor(flavor) : bound;
	}

	/**
	 * @param puName the unit name; may be {@code null}
	 * @return the flavor the unit's service publishes, or {@code null} when the unit is
	 *         unknown or predates the property
	 */
	private JpaFlavor flavorOf(String puName) {
		if (isNull(puName)) {
			return null;
		}
		ServiceReference<JPAUnit> reference = unitRefs.get(puName);
		if (isNull(reference)) {
			return null;
		}
		return reference.getProperty(CapabilityDeclaration.FLAVOR_PROPERTY) instanceof String id
				? JpaFlavor.byId(id).orElse(null)
				: null;
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
