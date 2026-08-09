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

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;
import org.eclipse.fennec.persistence.liveness.LivenessConfig;
import org.eclipse.fennec.persistence.liveness.LivenessConstants;
import org.eclipse.fennec.persistence.liveness.LivenessGate;
import org.eclipse.fennec.persistence.mongo.MongoFlavor;
import org.eclipse.fennec.persistence.mongo.MongoPersistenceConstants;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

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

		@AttributeDefinition(name = "Flavor",
				description = "Server implementation behind the wire protocol: 'mongo' (default), "
						+ "'ferretdb' or 'documentdb-pg'. Selects the query capability set; "
						+ "verified against the server on connect. Configured here because the "
						+ "flavor is a property of the connection, and propagated to the database "
						+ "services from here",
				options = {
						@Option(label = "MongoDB", value = "mongo"),
						@Option(label = "FerretDB", value = "ferretdb"),
						@Option(label = "DocumentDB (PostgreSQL)", value = "documentdb-pg") })
		String flavor() default "mongo";

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

	private static final Logger LOG = Logger.getLogger(MongoClientComponent.class.getName());

	private static final Document PING = new Document("ping", 1);
	private static final Document BUILD_INFO = new Document("buildInfo", 1);

	private MongoClient client;
	private volatile LivenessGate<MongoClient> gate;
	/** The configured flavor whose capability set the resources will use (issue #118). */
	private final MongoFlavor flavor;
	/** Guards the one-shot handshake verification — it runs on the first successful probe. */
	private final AtomicBoolean flavorVerified = new AtomicBoolean();

	@Activate
	public MongoClientComponent(BundleContext context, ClientConfig config) {
		flavor = MongoFlavor.byId(config.flavor())
				.orElseThrow(() -> new IllegalArgumentException("Unknown mongo flavor '" + config.flavor()
						+ "' for client '" + config.ident() + "' — expected one of mongo, ferretdb, documentdb-pg"));
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
				.serviceProperties(Map.of(MongoPersistenceConstants.CLIENT_IDENT, config.ident(),
						MongoPersistenceConstants.FLAVOR, flavor.id()))
				// withTimeout (CSOT) bounds the whole probe including server selection -
				// without it a probe against an unreachable server would block for the
				// full serverSelectionTimeout (default 30s)
				.probe(timeout -> {
					client.getDatabase("admin")
							.withTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
							.runCommand(PING);
					verifyFlavor(timeout);
				})
				.config(livenessConfig)
				.build();
		gate.open();
	}

	/**
	 * Verifies the configured {@link MongoFlavor} against the server, once, on the first
	 * successful probe (issue #118).
	 * <p>
	 * The flavor has to be <em>configured</em> — the capability set must be known before any
	 * connection exists, since {@code validate()} may be called first. Detection therefore
	 * does not replace the configuration, it checks it, and a mismatch is reported rather
	 * than silently corrected: swapping the capability set underneath a running resource
	 * would change which queries are legal mid-flight.
	 * <p>
	 * A wrong declaration is not cosmetic in either direction. Claiming MongoDB on a gateway
	 * lets unsupported operators through to a driver error deep inside a pipeline; claiming a
	 * gateway on real MongoDB refuses queries the server would happily serve.
	 * <p>
	 * Failures of the {@code buildInfo} command itself are swallowed deliberately: liveness
	 * is decided by the {@code ping} above, and a server that answers ping but refuses
	 * buildInfo must not be reported as down.
	 */
	private void verifyFlavor(Duration timeout) {
		if (!flavorVerified.compareAndSet(false, true)) {
			return;
		}
		Document buildInfo;
		try {
			buildInfo = client.getDatabase("admin")
					.withTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
					.runCommand(BUILD_INFO);
		} catch (RuntimeException e) {
			LOG.log(Level.FINE, e, () -> "Could not read buildInfo to verify the configured flavor '"
					+ flavor.id() + "' — the flavor stays as configured");
			flavorVerified.set(false);
			return;
		}
		Optional<MongoFlavor> detected = MongoFlavor.detect(buildInfo);
		if (detected.isPresent()) {
			if (detected.get() != flavor) {
				LOG.log(Level.WARNING,
						"Configured mongo flavor ''{0}'' does not match the server, which identifies as ''{1}'' —"
								+ " the declared query capabilities do not describe this server",
						new Object[] { flavor.id(), detected.get().id() });
			}
			return;
		}
		if (flavor != MongoFlavor.MONGO) {
			// No gateway marker means "indistinguishable from MongoDB", not "is MongoDB" -
			// a future gateway may simply not announce itself, so this stays a warning.
			LOG.log(Level.WARNING,
					"Configured mongo flavor ''{0}'' but the server carries no gateway marker in buildInfo"
							+ " (version ''{1}'') — verify the configuration if queries are refused unexpectedly",
					new Object[] { flavor.id(), buildInfo.get("version") });
		}
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
