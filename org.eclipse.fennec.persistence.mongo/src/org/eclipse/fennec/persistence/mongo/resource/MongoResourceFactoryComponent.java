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
package org.eclipse.fennec.persistence.mongo.resource;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.emf.osgi.annotation.ConfiguratorType;
import org.eclipse.fennec.emf.osgi.annotation.provide.EMFConfigurator;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.eclipse.fennec.persistence.mongo.MongoFlavor;
import org.eclipse.fennec.persistence.mongo.MongoPersistenceConstants;
import org.eclipse.fennec.persistence.mongo.MongoResourceFactory;
import org.eclipse.fennec.persistence.mongo.query.MongoQueryProcessor;
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

import com.mongodb.client.MongoDatabase;

/**
 * The single {@link Resource.Factory} for the {@code mongodb} URI scheme — an emf.osgi
 * whiteboard over all {@link MongoDatabase} services (issue #90), the Mongo counterpart
 * of the JPA {@code JPAResourceFactoryComponent}.
 * <p>
 * The emf.osgi resource-factory registry holds exactly <em>one</em> factory per protocol,
 * so this one factory dispatches by URI: {@code mongodb://<alias>/<collection>} → the
 * {@link MongoDatabase} service whose {@code mongo.database.alias} property matches the
 * URI authority (registered by {@code MongoDatabaseComponent}, factory PID
 * {@code persistence.mongo.database}).
 * <p>
 * Databases are tracked as {@link ServiceReference}s and their alias is read <em>from the
 * reference properties</em>; the service object is resolved (and cached) only when a URI
 * actually hits the alias. A URI addressing an unknown alias still yields a resource — it
 * fails with a clear diagnostic on load/save instead of returning {@code null} (no silent
 * fallback to another database).
 * <p>
 * Resource construction is delegated to {@link MongoResourceFactory} — the same factory
 * non-OSGi consumers instantiate directly — so both modes share one construction path.
 *
 * @author Mark Hoffmann
 * @since 04.08.2026
 */
@Component(name = "MongoResourceFactory", service = Resource.Factory.class, immediate = true)
@EMFConfigurator(configuratorName = "mongodb", configuratorType = ConfiguratorType.RESOURCE_FACTORY,
		protocol = MongoPersistenceConstants.URI_SCHEME)
public class MongoResourceFactoryComponent implements Resource.Factory {

	private static final Logger LOG = Logger.getLogger(MongoResourceFactoryComponent.class.getName());

	private final BundleContext ctx;
	private final MetadataService metadataService;
	/** Alias → service reference; populated from reference properties only (no getService). */
	private final Map<String, ServiceReference<MongoDatabase>> databaseRefs = new ConcurrentHashMap<>();
	/** Alias → resolved service object; filled lazily on the first URI hit per alias. */
	private final Map<String, MongoDatabase> resolvedDatabases = new ConcurrentHashMap<>();
	/** The processor handed to created resources; empty = resources use their local default. */
	private final AtomicReference<QueryProcessor> queryProcessor = new AtomicReference<>();
	/** The value registry handed to created resources; empty = resources decode without one. */
	private final AtomicReference<CodecValueRegistry> valueRegistry = new AtomicReference<>();

	@Activate
	public MongoResourceFactoryComponent(BundleContext ctx, @Reference MetadataService metadataService) {
		this.ctx = ctx;
		this.metadataService = metadataService;
	}

	/**
	 * The {@link QueryProcessor} handed to created resources (issue #61). Optional and
	 * greedy: without a matching service the resources fall back to their local default
	 * processor; a registered {@code mongo}-backend service (e.g. reconfigured, decorated
	 * or higher-ranked) takes precedence for all subsequently created resources.
	 */
	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC,
			policyOption = ReferencePolicyOption.GREEDY,
			target = "(" + QueryConstants.BACKEND_PROPERTY + "=" + MongoQueryProcessor.BACKEND + ")")
	void bindQueryProcessor(QueryProcessor processor) {
		queryProcessor.set(processor);
	}

	void unbindQueryProcessor(QueryProcessor processor) {
		// on a greedy rebind DS binds the replacement first — only clear if the
		// departing service is still the bound one
		queryProcessor.compareAndSet(processor, null);
	}

	/**
	 * The {@link CodecValueRegistry} handed to created resources. Optional and greedy —
	 * {@link MongoResourceFactory} copies it per resource; without one the resources
	 * work registry-less.
	 */
	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC,
			policyOption = ReferencePolicyOption.GREEDY)
	void bindValueRegistry(CodecValueRegistry registry) {
		valueRegistry.set(registry);
	}

	void unbindValueRegistry(CodecValueRegistry registry) {
		valueRegistry.compareAndSet(registry, null);
	}

	@Deactivate
	void deactivate() {
		resolvedDatabases.clear();
		databaseRefs.values().forEach(ctx::ungetService);
		databaseRefs.clear();
	}

	/**
	 * Whiteboard: tracks every {@link MongoDatabase} service by its
	 * {@code mongo.database.alias} property. Only the reference is stored — the service
	 * object stays untouched until a URI addresses the alias.
	 */
	@Reference(service = MongoDatabase.class, cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	void addDatabase(ServiceReference<MongoDatabase> reference) {
		String alias = alias(reference);
		if (isNull(alias)) {
			LOG.log(Level.WARNING, "Ignoring MongoDatabase service without ''{0}'' property: {1}",
					new Object[] { MongoPersistenceConstants.DATABASE_ALIAS, reference });
			return;
		}
		ServiceReference<MongoDatabase> previous = databaseRefs.put(alias, reference);
		if (nonNull(previous)) {
			LOG.log(Level.WARNING, "Multiple MongoDatabase services for alias ''{0}'' — using the newest one", alias);
			resolvedDatabases.remove(alias);
			ctx.ungetService(previous);
		}
	}

	void removeDatabase(ServiceReference<MongoDatabase> reference) {
		String alias = alias(reference);
		if (isNull(alias)) {
			return;
		}
		if (databaseRefs.remove(alias, reference) && nonNull(resolvedDatabases.remove(alias))) {
			ctx.ungetService(reference);
		}
	}

	private static String alias(ServiceReference<MongoDatabase> reference) {
		return reference.getProperty(MongoPersistenceConstants.DATABASE_ALIAS) instanceof String s ? s : null;
	}

	/**
	 * Creates a resource for the database aliased by the URI authority. Unknown or
	 * no-longer-available aliases yield a resource backed by an unavailable database —
	 * operations on it fail with a diagnostic naming the missing alias.
	 */
	@Override
	public Resource createResource(URI uri) {
		String alias = uri.authority();
		if (isNull(alias) || alias.isEmpty()) {
			return newResource(uri, unavailable(
					"URI '" + uri + "' does not name a database alias (expected mongodb://<alias>/<collection>)"),
					MongoFlavor.MONGO);
		}
		MongoDatabase database = resolveDatabase(alias);
		if (isNull(database)) {
			return newResource(uri, unavailable(
					"No MongoDB database with alias '" + alias + "' is available for URI '" + uri + "'"),
					MongoFlavor.MONGO);
		}
		return newResource(uri, database, flavor(alias));
	}

	private Resource newResource(URI uri, MongoDatabase database, MongoFlavor flavor) {
		return new MongoResourceFactory(database, metadataService, valueRegistry.get(), queryProcessor.get(), null,
				flavor).createResource(uri);
	}

	/**
	 * The server flavor declared by the aliased database service (issue #118), propagated
	 * there from the client configuration. An unknown id is reported and falls back to
	 * {@link MongoFlavor#MONGO} — the resource stays usable, and the baseline is the honest
	 * choice here: it claims what this translation can express, so a genuine gateway gap
	 * surfaces as a driver error the logged warning explains, rather than as queries
	 * mysteriously refused.
	 */
	private MongoFlavor flavor(String alias) {
		ServiceReference<MongoDatabase> reference = databaseRefs.get(alias);
		if (isNull(reference)) {
			return MongoFlavor.MONGO;
		}
		String id = reference.getProperty(MongoPersistenceConstants.FLAVOR) instanceof String value ? value : null;
		return MongoFlavor.byId(id).orElseGet(() -> {
			LOG.log(Level.WARNING,
					"MongoDatabase alias ''{0}'' declares unknown flavor ''{1}'' — using ''{2}'' capabilities",
					new Object[] { alias, id, MongoFlavor.MONGO.id() });
			return MongoFlavor.MONGO;
		});
	}

	/**
	 * Resolves (and caches) the service object for the aliased database. The
	 * {@code MongoDatabase} object itself is cheap — the driver connects lazily on the
	 * first actual operation of a resource.
	 */
	private MongoDatabase resolveDatabase(String alias) {
		MongoDatabase resolved = resolvedDatabases.get(alias);
		if (nonNull(resolved)) {
			return resolved;
		}
		ServiceReference<MongoDatabase> reference = databaseRefs.get(alias);
		if (isNull(reference)) {
			return null;
		}
		MongoDatabase database = ctx.getService(reference);
		if (isNull(database)) {
			// Stale reference — the service vanished between lookup and resolution.
			databaseRefs.remove(alias, reference);
			return null;
		}
		MongoDatabase previous = resolvedDatabases.putIfAbsent(alias, database);
		if (nonNull(previous)) {
			// Lost a benign race — another thread resolved first; release our extra get.
			ctx.ungetService(reference);
			return previous;
		}
		return database;
	}

	/**
	 * A {@link MongoDatabase} that throws an {@link IllegalStateException} carrying
	 * {@code reason} on every operation — the Mongo analogue of
	 * {@code JPAUnit.unavailable}: the resource is created, the failure surfaces as a
	 * clear diagnostic on load/save.
	 */
	private static MongoDatabase unavailable(String reason) {
		return (MongoDatabase) Proxy.newProxyInstance(MongoDatabase.class.getClassLoader(),
				new Class<?>[] { MongoDatabase.class }, (proxy, method, args) -> switch (method.getName()) {
					case "toString" -> "Unavailable MongoDatabase: " + reason;
					case "hashCode" -> System.identityHashCode(proxy);
					case "equals" -> proxy == args[0];
					default -> throw new IllegalStateException(reason);
				});
	}
}
