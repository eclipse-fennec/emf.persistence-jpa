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
package org.eclipse.fennec.persistence.eclipselink.liveness;

import static java.util.Objects.nonNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.eclipse.fennec.persistence.liveness.ConnectionProbe;
import org.eclipse.fennec.persistence.liveness.LivenessConfig;
import org.eclipse.fennec.persistence.liveness.LivenessConstants;
import org.eclipse.fennec.persistence.liveness.LivenessGate;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Liveness-gated {@link DataSource} re-publisher. One factory configuration
 * ({@code persistence.jdbc.gate}) binds an upstream {@code DataSource} (selected via the
 * {@code dataSource.target} filter) and re-registers the same instance carrying the
 * marker property {@code fennec.liveness=checked} — but only while probes
 * ({@code Connection.isValid}) succeed.
 * <p>
 * Consumers opt in by targeting the marker, e.g. a persistence-unit configuration with
 * {@code fennec.jpa.dataSource.target=(&(fennec.liveness=checked)(name=mydb))}. All
 * public configuration properties except {@code liveness.*}, {@code *.target} and
 * framework-internal keys are forwarded to the gated registration for such filters.
 * <p>
 * The gate itself ({@link LivenessGate}) is backend-neutral and lives in the core bundle;
 * only this JDBC binding lives here, so that the mandatory {@code DataSource} service
 * requirement stays out of the backend-neutral base bundle.
 *
 * @author Mark Hoffmann
 * @since 20.07.2026
 */
@Component(name = LivenessConstants.JDBC_GATE_PID, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = GatedDataSourceComponent.GateConfig.class, factory = true)
public class GatedDataSourceComponent {

	@ObjectClassDefinition(name = "Gated DataSource",
			description = "Re-registers a DataSource only while its connection is verified")
	public @interface GateConfig {

		@AttributeDefinition(name = "Name",
				description = "Unique name of this gate, forwarded to the gated registration "
						+ "and used as condition id fennec.liveness.<name>")
		String name();

		@AttributeDefinition(name = "Liveness enabled",
				description = "false registers the DataSource immediately without probing")
		boolean liveness_enabled() default true;

		@AttributeDefinition(name = "Check interval (s)",
				description = "Probe period while UP; 0 disables periodic re-checks")
		long liveness_checkInterval() default LivenessConfig.DEFAULT_CHECK_INTERVAL_SECONDS;

		@AttributeDefinition(name = "Check timeout (s)",
				description = "Timeout per probe")
		long liveness_checkTimeout() default LivenessConfig.DEFAULT_CHECK_TIMEOUT_SECONDS;

		@AttributeDefinition(name = "Failure threshold",
				description = "Consecutive probe failures before the DataSource is unregistered")
		int liveness_failureThreshold() default LivenessConfig.DEFAULT_FAILURE_THRESHOLD;

		@AttributeDefinition(name = "Retry min (s)",
				description = "Lower bound of the retry backoff while DOWN")
		long liveness_retryMin() default LivenessConfig.DEFAULT_RETRY_MIN_SECONDS;

		@AttributeDefinition(name = "Retry max (s)",
				description = "Upper bound of the retry backoff while DOWN")
		long liveness_retryMax() default LivenessConfig.DEFAULT_RETRY_MAX_SECONDS;
	}

	private LivenessGate<DataSource> gate;

	@Activate
	public GatedDataSourceComponent(BundleContext context, GateConfig config,
			Map<String, Object> properties, @Reference(name = "dataSource") DataSource dataSource) {
		LivenessConfig livenessConfig = LivenessConfig.of(config.liveness_enabled(),
				config.liveness_checkInterval(), config.liveness_checkTimeout(),
				config.liveness_failureThreshold(), config.liveness_retryMin(),
				config.liveness_retryMax());
		gate = LivenessGate.builder(context, DataSource.class, dataSource)
				.ident(config.name())
				.backendType(LivenessConstants.BACKEND_JDBC)
				.serviceProperties(forwardedProperties(properties))
				.probe(probe(dataSource))
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
	}

	private static ConnectionProbe probe(DataSource dataSource) {
		return timeout -> {
			int seconds = (int) Math.min(Integer.MAX_VALUE, timeout.toSeconds());
			try (Connection connection = dataSource.getConnection()) {
				if (!connection.isValid(seconds)) {
					throw new SQLException("Connection reported invalid");
				}
			}
		};
	}

	/**
	 * Forwards all public configuration properties to the gated registration, except
	 * liveness settings, reference target filters and framework-internal keys, and adds
	 * the {@code fennec.liveness=checked} marker.
	 */
	private static Map<String, Object> forwardedProperties(Map<String, Object> properties) {
		Map<String, Object> forwarded = new LinkedHashMap<>();
		properties.forEach((key, value) -> {
			if (key.startsWith(".") || key.startsWith(LivenessConstants.PREFIX)
					|| key.startsWith("service.") || key.startsWith("component.")
					|| key.startsWith("felix.") || key.endsWith(".target")) {
				return;
			}
			forwarded.put(key, value);
		});
		forwarded.put(LivenessConstants.CHECKED_PROPERTY, LivenessConstants.CHECKED_VALUE);
		return forwarded;
	}
}
