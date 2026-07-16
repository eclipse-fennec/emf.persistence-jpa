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

import static java.util.Objects.nonNull;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.fennec.persistence.mongo.MongoPersistenceConstants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Configurable {@link MongoClient} provider. One component instance per factory
 * configuration ({@code persistence.mongo.client}) creates a client and registers it as
 * an OSGi service carrying the {@code mongo.client.ident} property, so
 * {@link MongoDatabaseComponent} instances can bind by target filter.
 *
 * @author Mark Hoffmann
 * @since 16.07.2026
 */
@Component(name = MongoPersistenceConstants.CLIENT_PID,
		configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = MongoClientComponent.ClientConfig.class, factory = true)
public class MongoClientComponent {

	@ObjectClassDefinition(name = "Mongo Client",
			description = "Configuration for a MongoDB client connection")
	public @interface ClientConfig {

		@AttributeDefinition(name = "Identifier",
				description = "Unique identifier of this client, used as service property "
						+ MongoPersistenceConstants.CLIENT_IDENT)
		String ident();

		@AttributeDefinition(name = "Connection String",
				description = "MongoDB connection string, e.g. mongodb://localhost:27017")
		String connectionString();
	}

	private MongoClient client;
	private ServiceRegistration<MongoClient> registration;

	@Activate
	public MongoClientComponent(BundleContext context, ClientConfig config) {
		client = MongoClients.create(config.connectionString());
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(MongoPersistenceConstants.CLIENT_IDENT, config.ident());
		registration = context.registerService(MongoClient.class, client, properties);
	}

	@Deactivate
	public void deactivate() {
		if (nonNull(registration)) {
			registration.unregister();
			registration = null;
		}
		if (nonNull(client)) {
			client.close();
			client = null;
		}
	}
}
