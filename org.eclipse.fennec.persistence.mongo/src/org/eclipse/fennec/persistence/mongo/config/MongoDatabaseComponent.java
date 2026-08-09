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
package org.eclipse.fennec.persistence.mongo.config;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.fennec.persistence.mongo.MongoFlavor;
import org.eclipse.fennec.persistence.mongo.MongoPersistenceConstants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * Configurable {@link MongoDatabase} provider. One component instance per factory
 * configuration ({@code persistence.mongo.database}) resolves a database from the bound
 * {@link MongoClient} (selected via the {@code client.target} filter) and registers it
 * as an OSGi service carrying the {@code mongo.database.alias} property.
 *
 * @author Mark Hoffmann
 * @since 16.07.2026
 */
@Component(name = MongoPersistenceConstants.DATABASE_PID,
		configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = MongoDatabaseComponent.DatabaseConfig.class, factory = true)
public class MongoDatabaseComponent {

	@ObjectClassDefinition(name = "Mongo Database",
			description = "Configuration for a MongoDB database")
	public @interface DatabaseConfig {

		@AttributeDefinition(name = "Alias",
				description = "Alias of this database, used as service property "
						+ MongoPersistenceConstants.DATABASE_ALIAS)
		String alias();

		@AttributeDefinition(name = "Database",
				description = "Name of the MongoDB database")
		String database();
	}

	private final BundleContext context;
	private final ServiceReference<MongoClient> clientReference;
	private ServiceRegistration<MongoDatabase> registration;

	/**
	 * The {@link MongoClient} is injected as a {@link ServiceReference} rather than as the
	 * service object so the client's {@code mongo.flavor} property can be read and passed on
	 * (issue #118): the flavor is configured once, on the client, because it describes the
	 * server — this component propagates it so the resource factory can derive the query
	 * capabilities per alias without a second configuration to keep in sync.
	 */
	@Activate
	public MongoDatabaseComponent(BundleContext context, DatabaseConfig config,
			@Reference(name = "client") ServiceReference<MongoClient> clientReference) {
		this.context = context;
		this.clientReference = clientReference;
		MongoClient client = context.getService(clientReference);
		if (isNull(client)) {
			throw new IllegalStateException("MongoClient service for database alias '" + config.alias()
					+ "' vanished before the database could be resolved");
		}
		MongoDatabase database = client.getDatabase(config.database());
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(MongoPersistenceConstants.DATABASE_ALIAS, config.alias());
		properties.put(MongoPersistenceConstants.FLAVOR, flavorOf(clientReference));
		registration = context.registerService(MongoDatabase.class, database, properties);
	}

	/**
	 * The flavor id carried by the client service; {@link MongoFlavor#MONGO} when the client
	 * predates the property or does not declare one.
	 */
	private static String flavorOf(ServiceReference<MongoClient> reference) {
		return reference.getProperty(MongoPersistenceConstants.FLAVOR) instanceof String flavor && !flavor.isBlank()
				? flavor
				: MongoFlavor.MONGO.id();
	}

	@Deactivate
	public void deactivate() {
		if (nonNull(registration)) {
			registration.unregister();
			registration = null;
		}
		context.ungetService(clientReference);
	}
}
