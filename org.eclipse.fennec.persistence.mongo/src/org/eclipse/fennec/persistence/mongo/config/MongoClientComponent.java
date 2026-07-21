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

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.eclipse.fennec.persistence.liveness.LivenessConfig;
import org.eclipse.fennec.persistence.liveness.LivenessConstants;
import org.eclipse.fennec.persistence.liveness.LivenessGate;
import org.eclipse.fennec.persistence.mongo.MongoPersistenceConstants;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.connection.ClusterDescription;
import com.mongodb.connection.ServerDescription;
import com.mongodb.event.ClusterDescriptionChangedEvent;
import com.mongodb.event.ClusterListener;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Configurable {@link MongoClient} provider. One component instance per factory
 * configuration ({@code persistence.mongo.client}) creates a client; the OSGi service
 * (carrying the {@code mongo.client.ident} property, so {@link MongoDatabaseComponent}
 * instances can bind by target filter) is registered through a {@link LivenessGate}:
 * creating a Mongo client performs no I/O, so the service only appears once a
 * {@code ping} probe verifies the connection, and disappears again when the connection
 * breaks. A driver {@link ClusterListener} triggers immediate probes on topology
 * changes, so both recovery and failure detection do not wait for the next poll.
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
						+ MongoPersistenceConstants.CLIENT_IDENT
						+ " and as condition id fennec.liveness.<ident>")
		String ident();

		@AttributeDefinition(name = "Connection String",
				description = "MongoDB connection string, e.g. mongodb://localhost:27017")
		String connectionString();

		@AttributeDefinition(name = "Liveness enabled",
				description = "false registers the client immediately without probing")
		boolean liveness_enabled() default true;

		@AttributeDefinition(name = "Check interval (s)",
				description = "Probe period while UP; 0 disables periodic re-checks")
		long liveness_checkInterval() default LivenessConfig.DEFAULT_CHECK_INTERVAL_SECONDS;

		@AttributeDefinition(name = "Check timeout (s)",
				description = "Timeout per probe")
		long liveness_checkTimeout() default LivenessConfig.DEFAULT_CHECK_TIMEOUT_SECONDS;

		@AttributeDefinition(name = "Failure threshold",
				description = "Consecutive probe failures before the client is unregistered")
		int liveness_failureThreshold() default LivenessConfig.DEFAULT_FAILURE_THRESHOLD;

		@AttributeDefinition(name = "Retry min (s)",
				description = "Lower bound of the retry backoff while DOWN")
		long liveness_retryMin() default LivenessConfig.DEFAULT_RETRY_MIN_SECONDS;

		@AttributeDefinition(name = "Retry max (s)",
				description = "Upper bound of the retry backoff while DOWN")
		long liveness_retryMax() default LivenessConfig.DEFAULT_RETRY_MAX_SECONDS;
	}

	private static final Document PING = new Document("ping", 1);

	private MongoClient client;
	private volatile LivenessGate<MongoClient> gate;

	@Activate
	public MongoClientComponent(BundleContext context, ClientConfig config) {
		MongoClientSettings settings = MongoClientSettings.builder()
				.applyConnectionString(new ConnectionString(config.connectionString()))
				.applyToClusterSettings(cluster -> cluster.addClusterListener(new GateClusterListener()))
				.build();
		client = MongoClients.create(settings);
		LivenessConfig livenessConfig = LivenessConfig.of(config.liveness_enabled(),
				config.liveness_checkInterval(), config.liveness_checkTimeout(),
				config.liveness_failureThreshold(), config.liveness_retryMin(),
				config.liveness_retryMax());
		gate = LivenessGate.builder(context, MongoClient.class, client)
				.ident(config.ident())
				.backendType(LivenessConstants.BACKEND_MONGO)
				.serviceProperties(Map.of(MongoPersistenceConstants.CLIENT_IDENT, config.ident()))
				// withTimeout (CSOT) bounds the whole probe including server selection -
				// without it a probe against an unreachable server would block for the
				// full serverSelectionTimeout (default 30s)
				.probe(timeout -> client.getDatabase("admin")
						.withTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
						.runCommand(PING))
				.config(livenessConfig)
				.build();
		gate.open();
	}

	@Deactivate
	public void deactivate() {
		if (nonNull(gate)) {
			gate.close();
			gate = null;
		}
		if (nonNull(client)) {
			client.close();
			client = null;
		}
	}

	/**
	 * Bridges driver topology events into the gate: whenever server reachability flips,
	 * an immediate probe replaces the scheduled one. The gate field may still be unset
	 * while {@code MongoClients.create} fires initial events.
	 */
	private final class GateClusterListener implements ClusterListener {

		@Override
		public void clusterDescriptionChanged(ClusterDescriptionChangedEvent event) {
			boolean wasReachable = hasReachableServer(event.getPreviousDescription());
			boolean isReachable = hasReachableServer(event.getNewDescription());
			if (wasReachable != isReachable) {
				LivenessGate<MongoClient> currentGate = gate;
				if (nonNull(currentGate)) {
					currentGate.probeNow();
				}
			}
		}

		private boolean hasReachableServer(ClusterDescription description) {
			return description.getServerDescriptions().stream().anyMatch(ServerDescription::isOk);
		}
	}
}
