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
package org.eclipse.fennec.persistence.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;

import javax.sql.DataSource;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAUnit;
import org.eclipse.fennec.persistence.eorm.EntityMappings;
import org.eclipse.fennec.persistence.liveness.LivenessConstants;
import org.eclipse.fennec.persistence.liveness.PersistenceLivenessRuntime;
import org.eclipse.fennec.persistence.liveness.dto.GateDTO;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.fennec.persistence.test.annotations.TestAnnotations;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.condition.Condition;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.Property.TemplateArgument;
import org.osgi.test.common.annotation.Property.ValueSource;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;

/**
 * OSGi integration test for the connection-liveness gate (concept:
 * {@code docs/concept-connection-liveness.md}): a {@code DataSource} gated by
 * {@code persistence.jdbc.gate} must only be registered while the database is actually
 * reachable — verified against an H2 TCP server that is stopped and restarted — and the
 * DS reference cascade must propagate that to the {@link JPAUnit} service. Alongside the
 * functional service the per-gate {@link Condition} and the
 * {@link PersistenceLivenessRuntime} DTOs are checked.
 */
public class ConnectionLivenessTest extends EPersistenceBase {

	private static final String UPSTREAM_MARKER = "h2.liveness.upstream";
	private static final String GATE_NAME = "livedb";
	private static final long WAIT_APPEAR_MILLIS = 15_000;
	private static final long WAIT_DISAPPEAR_MILLIS = 15_000;

	private Server server;
	private int port = -1;
	private ServiceRegistration<DataSource> upstreamRegistration;

	@Override
	protected EntityMappings setupMappings(EntityMapper mapper, EPackage ePackage) {
		EClass personEClass = (EClass) ePackage.getEClassifier("Person");
		assertNotNull(personEClass);
		return mapper.createMappings(List.of(personEClass));
	}

	@AfterEach
	public void cleanupLiveness() {
		if (Objects.nonNull(upstreamRegistration)) {
			try {
				upstreamRegistration.unregister();
			} catch (IllegalStateException e) {
				// already unregistered
			}
			upstreamRegistration = null;
		}
		stopServer();
		port = -1;
	}

	private void startServer() throws Exception {
		if (port < 0) {
			try (ServerSocket socket = new ServerSocket(0)) {
				port = socket.getLocalPort();
			}
		}
		File baseDir = new File(modelPath, "h2tcp");
		server = Server.createTcpServer("-tcpPort", String.valueOf(port), "-tcpAllowOthers",
				"-baseDir", baseDir.getAbsolutePath(), "-ifNotExists").start();
	}

	private void stopServer() {
		if (Objects.nonNull(server)) {
			server.stop();
			server = null;
		}
	}

	private void registerUpstreamDataSource(BundleContext context) {
		JdbcDataSource dataSource = new JdbcDataSource();
		dataSource.setURL("jdbc:h2:tcp://127.0.0.1:" + port + "/liveness");
		dataSource.setUser("sa");
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(UPSTREAM_MARKER, "true");
		upstreamRegistration = context.registerService(DataSource.class, dataSource, properties);
	}

	private static void waitUntil(String description, BooleanSupplier check, long timeoutMillis)
			throws InterruptedException {
		long deadline = System.currentTimeMillis() + timeoutMillis;
		while (System.currentTimeMillis() < deadline) {
			if (check.getAsBoolean()) {
				return;
			}
			Thread.sleep(200);
		}
		fail("Timed out after " + timeoutMillis + "ms waiting until: " + description);
	}

	private static GateDTO gateDTO(PersistenceLivenessRuntime runtime, String ident) {
		return Arrays.stream(runtime.getRuntimeDTO().gates)
				.filter(gate -> ident.equals(gate.ident))
				.findFirst()
				.orElse(null);
	}

	/**
	 * The gated {@code DataSource}, the per-gate {@link Condition} and the runtime DTO
	 * must follow the availability of the database: present while the H2 TCP server
	 * runs, gone after it stops, back after it restarts.
	 */
	@Test
	@TestAnnotations.DefaultEPersistenceSetup
	@WithFactoryConfiguration(factoryPid = LivenessConstants.JDBC_GATE_PID, name = "livegate", location = "?", properties = {
			@Property(key = "name", value = GATE_NAME),
			@Property(key = "dataSource.target", value = "(" + UPSTREAM_MARKER + "=true)"),
			@Property(key = "liveness.checkInterval", value = "1"),
			@Property(key = "liveness.checkTimeout", value = "2"),
			@Property(key = "liveness.failureThreshold", value = "1"),
			@Property(key = "liveness.retryMin", value = "1"),
			@Property(key = "liveness.retryMax", value = "1")
	})
	public void gatedDataSourceFollowsDatabaseAvailability(
			@InjectService(cardinality = 0) ServiceAware<DataSource> gatedAware,
			@InjectService(cardinality = 0, filter = "(osgi.condition.id=fennec.liveness." + GATE_NAME + ")")
			ServiceAware<Condition> conditionAware,
			@InjectService ServiceAware<PersistenceLivenessRuntime> runtimeAware) throws Exception {
		PersistenceLivenessRuntime runtime = runtimeAware.waitForService(5000);
		assertNotNull(runtime);

		startServer();
		registerUpstreamDataSource(bctx);

		// UP: gated DataSource, condition and DTO in lockstep
		waitUntil("gated DataSource is registered",
				() -> hasGatedDataSource(gatedAware), WAIT_APPEAR_MILLIS);
		waitUntil("liveness condition is registered",
				() -> !conditionAware.isEmpty(), WAIT_APPEAR_MILLIS);
		GateDTO up = gateDTO(runtime, GATE_NAME);
		assertThat(up).isNotNull();
		assertThat(up.backendType).isEqualTo(LivenessConstants.BACKEND_JDBC);
		assertThat(up.state).isEqualTo(LivenessConstants.STATE_UP);
		assertThat(up.lastSuccess).isPositive();
		Long changeCountUp = (Long) runtimeAware.getServiceReference()
				.getProperty(Constants.SERVICE_CHANGECOUNT);
		assertThat(changeCountUp).isNotNull();

		// DOWN: stopping the server must unregister service and condition
		stopServer();
		waitUntil("gated DataSource is unregistered after server stop",
				() -> !hasGatedDataSource(gatedAware), WAIT_DISAPPEAR_MILLIS);
		waitUntil("liveness condition is unregistered after server stop",
				conditionAware::isEmpty, WAIT_DISAPPEAR_MILLIS);
		GateDTO down = gateDTO(runtime, GATE_NAME);
		assertThat(down).isNotNull();
		assertThat(down.state).isEqualTo(LivenessConstants.STATE_DOWN);
		assertThat(down.lastFailure).isPositive();
		assertThat(down.lastFailureMessage).isNotBlank();
		Long changeCountDown = (Long) runtimeAware.getServiceReference()
				.getProperty(Constants.SERVICE_CHANGECOUNT);
		assertThat(changeCountDown).isGreaterThan(changeCountUp);

		// RECOVERY: restarting the server must re-register both
		startServer();
		waitUntil("gated DataSource is re-registered after server restart",
				() -> hasGatedDataSource(gatedAware), WAIT_APPEAR_MILLIS);
		waitUntil("liveness condition is re-registered after server restart",
				() -> !conditionAware.isEmpty(), WAIT_APPEAR_MILLIS);
		assertThat(gateDTO(runtime, GATE_NAME).state).isEqualTo(LivenessConstants.STATE_UP);
	}

	/**
	 * The full DS cascade of the concept: a persistence unit targeting the gated
	 * {@code DataSource} via {@code (fennec.liveness=checked)} — its {@link JPAUnit}
	 * service must appear, disappear and re-appear with the database, without any
	 * liveness code in the configurator.
	 */
	@Test
	@TestAnnotations.DefaultEPersistenceSetup
	@WithFactoryConfiguration(factoryPid = LivenessConstants.JDBC_GATE_PID, name = "livegate", location = "?", properties = {
			@Property(key = "name", value = GATE_NAME),
			@Property(key = "dataSource.target", value = "(" + UPSTREAM_MARKER + "=true)"),
			@Property(key = "liveness.checkInterval", value = "1"),
			@Property(key = "liveness.checkTimeout", value = "2"),
			@Property(key = "liveness.failureThreshold", value = "1"),
			@Property(key = "liveness.retryMin", value = "1"),
			@Property(key = "liveness.retryMax", value = "1")
	})
	@WithFactoryConfiguration(factoryPid = "fennec.jpa.PersistenceUnit", name = "livecascade", properties = {
			@Property(key = "fennec.jpa.model", value = "(emf.name=fennec.persistence.model)"),
			@Property(key = "fennec.jpa.mappingFile", value = "%s", templateArguments =
					@TemplateArgument(source = ValueSource.SystemProperty, value = TestAnnotations.PROP_MODEL_FILE_PATH)),
			@Property(key = "fennec.jpa.persistenceUnitName", value = "livecascade"),
			@Property(key = "fennec.jpa.dataSource.target",
					value = "(&(" + LivenessConstants.CHECKED_PROPERTY + "=checked)(name=" + GATE_NAME + "))")
	})
	public void persistenceUnitCascadesWithGatedDataSource(
			@InjectService(cardinality = 0, filter = "(osgi.unit.name=livecascade)")
			ServiceAware<JPAUnit> unitAware) throws Exception {
		startServer();
		registerUpstreamDataSource(bctx);

		// database reachable: gate registers the DataSource, the configurator activates
		// and the JPAUnit service appears
		assertNotNull(unitAware.waitForService(WAIT_APPEAR_MILLIS));

		// database gone: the gate unregisters, DS deactivates the configurator, the
		// JPAUnit service disappears
		stopServer();
		waitUntil("JPAUnit is unregistered after server stop",
				unitAware::isEmpty, WAIT_DISAPPEAR_MILLIS);

		// database back: the whole chain comes back
		startServer();
		waitUntil("JPAUnit is re-registered after server restart",
				() -> !unitAware.isEmpty(), WAIT_APPEAR_MILLIS);
	}

	private static boolean hasGatedDataSource(ServiceAware<DataSource> gatedAware) {
		return gatedAware.getServiceReferences().stream()
				.anyMatch(reference -> LivenessConstants.CHECKED_VALUE
						.equals(reference.getProperty(LivenessConstants.CHECKED_PROPERTY)));
	}
}
