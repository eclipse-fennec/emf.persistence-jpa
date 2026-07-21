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
package org.eclipse.fennec.persistence.liveness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.fennec.persistence.liveness.impl.LivenessGateRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.condition.Condition;

/**
 * Unit tests for the {@link LivenessGate} state machine, using a deterministic scheduler
 * and a scripted probe.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LivenessGateTest {

	@Mock
	private BundleContext context;
	@Mock
	private ServiceRegistration<String> serviceRegistration;
	@Mock
	private ServiceRegistration<Condition> conditionRegistration;

	private final TestScheduler scheduler = new TestScheduler();
	private final LivenessGateRegistry registry = LivenessGateRegistry.newInstance();
	private final AtomicBoolean healthy = new AtomicBoolean(false);
	private final AtomicInteger pings = new AtomicInteger();
	private final ConnectionProbe probe = timeout -> {
		pings.incrementAndGet();
		if (!healthy.get()) {
			throw new IllegalStateException("connection refused");
		}
	};

	@BeforeEach
	void setUp() {
		when(context.registerService(eq(String.class), eq("service"), any()))
				.thenReturn(serviceRegistration);
		when(context.registerService(eq(Condition.class), eq(Condition.INSTANCE), any()))
				.thenReturn(conditionRegistration);
	}

	private LivenessGate<String> gate(LivenessConfig config) {
		return LivenessGate.builder(context, String.class, "service")
				.ident("testdb")
				.backendType("jdbc")
				.serviceProperties(Map.of("name", "testdb"))
				.probe(probe)
				.config(config)
				.executor(scheduler)
				.registry(registry)
				.build();
	}

	private LivenessConfig config() {
		// interval 30s, timeout 5s, threshold 3, backoff 1..8s
		return LivenessConfig.of(true, 30, 5, 3, 1, 8);
	}

	@Test
	void registersServiceAndConditionAfterFirstSuccessfulProbe() {
		healthy.set(true);
		try (LivenessGate<String> gate = gate(config())) {
			gate.open();
			verify(context, never()).registerService(eq(String.class), eq("service"), any());

			scheduler.advanceBy(0);

			assertThat(pings).hasValue(1);
			ArgumentCaptor<Dictionary<String, Object>> props = ArgumentCaptor.captor();
			verify(context).registerService(eq(String.class), eq("service"), props.capture());
			assertThat(props.getValue().get("name")).isEqualTo("testdb");

			ArgumentCaptor<Dictionary<String, Object>> conditionProps = ArgumentCaptor.captor();
			verify(context).registerService(eq(Condition.class), eq(Condition.INSTANCE),
					conditionProps.capture());
			assertThat(conditionProps.getValue().get(Condition.CONDITION_ID))
					.isEqualTo("fennec.liveness.testdb");

			assertThat(gate.toDTO().state).isEqualTo(LivenessConstants.STATE_UP);
			assertThat(registry.gates()).containsExactly(gate);
		}
	}

	@Test
	void staysDownWithExponentialBackoffUntilFirstSuccess() {
		try (LivenessGate<String> gate = gate(config())) {
			gate.open();
			scheduler.advanceBy(0);
			assertThat(pings).hasValue(1);

			// backoff 1s: not due before
			scheduler.advanceBy(999);
			assertThat(pings).hasValue(1);
			scheduler.advanceBy(1);
			assertThat(pings).hasValue(2);
			// backoff 2s
			scheduler.advanceBy(2000);
			assertThat(pings).hasValue(3);
			// backoff 4s
			scheduler.advanceBy(4000);
			assertThat(pings).hasValue(4);
			// backoff capped at 8s
			scheduler.advanceBy(8000);
			assertThat(pings).hasValue(5);

			verify(context, never()).registerService(eq(String.class), eq("service"), any());
			assertThat(gate.toDTO().state).isEqualTo(LivenessConstants.STATE_DOWN);
			assertThat(gate.toDTO().consecutiveFailures).isEqualTo(5);
			assertThat(gate.toDTO().lastFailureMessage).isEqualTo("connection refused");

			healthy.set(true);
			scheduler.advanceBy(8000);
			verify(context).registerService(eq(String.class), eq("service"), any());
			assertThat(gate.toDTO().state).isEqualTo(LivenessConstants.STATE_UP);
			assertThat(gate.toDTO().consecutiveFailures).isZero();
			assertThat(gate.toDTO().lastFailureMessage).isNull();
		}
	}

	@Test
	void unregistersOnlyAfterFailureThresholdAndRecovers() {
		healthy.set(true);
		try (LivenessGate<String> gate = gate(config())) {
			gate.open();
			scheduler.advanceBy(0);
			assertThat(gate.toDTO().state).isEqualTo(LivenessConstants.STATE_UP);

			healthy.set(false);
			// failures 1 and 2: below threshold, stays registered
			scheduler.advanceBy(30_000);
			scheduler.advanceBy(30_000);
			verify(serviceRegistration, never()).unregister();
			verify(conditionRegistration, never()).unregister();
			assertThat(gate.toDTO().state).isEqualTo(LivenessConstants.STATE_UP);
			assertThat(gate.toDTO().consecutiveFailures).isEqualTo(2);

			// failure 3: threshold reached, both registrations removed in lockstep
			scheduler.advanceBy(30_000);
			verify(serviceRegistration).unregister();
			verify(conditionRegistration).unregister();
			assertThat(gate.toDTO().state).isEqualTo(LivenessConstants.STATE_DOWN);

			// recovery after a single success, with fresh registrations
			healthy.set(true);
			scheduler.advanceBy(1000);
			verify(context, times(2)).registerService(eq(String.class), eq("service"), any());
			verify(context, times(2)).registerService(eq(Condition.class), eq(Condition.INSTANCE), any());
			assertThat(gate.toDTO().state).isEqualTo(LivenessConstants.STATE_UP);
		}
	}

	@Test
	void disabledLivenessRegistersImmediatelyWithoutProbing() {
		LivenessGate<String> gate = gate(LivenessConfig.of(false, 30, 5, 3, 1, 8));
		gate.open();

		verify(context).registerService(eq(String.class), eq("service"), any());
		verify(context).registerService(eq(Condition.class), eq(Condition.INSTANCE), any());
		assertThat(pings).hasValue(0);
		assertThat(scheduler.pendingTasks()).isZero();
		assertThat(gate.toDTO().state).isEqualTo(LivenessConstants.STATE_UP);

		gate.close();
		verify(serviceRegistration).unregister();
		verify(conditionRegistration).unregister();
	}

	@Test
	void closeUnregistersAndStopsProbing() {
		healthy.set(true);
		LivenessGate<String> gate = gate(config());
		gate.open();
		scheduler.advanceBy(0);
		assertThat(pings).hasValue(1);

		gate.close();
		verify(serviceRegistration).unregister();
		verify(conditionRegistration).unregister();
		assertThat(registry.gates()).isEmpty();

		scheduler.advanceBy(120_000);
		assertThat(pings).hasValue(1);

		assertThatThrownBy(gate::open).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void probeNowReplacesScheduledProbeWithoutDuplicatingTheChain() {
		try (LivenessGate<String> gate = gate(config())) {
			gate.open();
			scheduler.advanceBy(0);
			assertThat(pings).hasValue(1);

			// down, next probe due in 1s - probeNow supersedes it
			gate.probeNow();
			scheduler.advanceBy(0);
			assertThat(pings).hasValue(2);

			// exactly one probe chain remains
			assertThat(scheduler.pendingTasks()).isEqualTo(1);

			healthy.set(true);
			gate.probeNow();
			scheduler.advanceBy(0);
			assertThat(gate.toDTO().state).isEqualTo(LivenessConstants.STATE_UP);
			assertThat(scheduler.pendingTasks()).isEqualTo(1);
		}
	}

	@Test
	void checkIntervalZeroProbesOnlyOnceWhileUp() {
		healthy.set(true);
		try (LivenessGate<String> gate = gate(LivenessConfig.of(true, 0, 5, 3, 1, 8))) {
			gate.open();
			scheduler.advanceBy(0);
			assertThat(gate.toDTO().state).isEqualTo(LivenessConstants.STATE_UP);
			assertThat(scheduler.pendingTasks()).isZero();

			scheduler.advanceBy(600_000);
			assertThat(pings).hasValue(1);

			// external triggers still work
			gate.probeNow();
			scheduler.advanceBy(0);
			assertThat(pings).hasValue(2);
		}
	}

	@Test
	void registryIsNotifiedOnEveryTransition() {
		AtomicInteger changes = new AtomicInteger();
		registry.setChangeListener(changes::incrementAndGet);
		healthy.set(true);

		LivenessGate<String> gate = gate(config());
		gate.open();
		assertThat(changes).hasValue(1); // added

		scheduler.advanceBy(0);
		assertThat(changes).hasValue(2); // DOWN -> UP

		healthy.set(false);
		scheduler.advanceBy(30_000);
		scheduler.advanceBy(30_000);
		assertThat(changes).hasValue(2); // below threshold: no transition

		scheduler.advanceBy(30_000);
		assertThat(changes).hasValue(3); // UP -> DOWN

		gate.close();
		assertThat(changes).hasValue(4); // removed
	}

	@Test
	void dtoCarriesConfigurationAndCounters() {
		healthy.set(true);
		try (LivenessGate<String> gate = gate(config())) {
			gate.open();
			scheduler.advanceBy(0);
			scheduler.advanceBy(30_000);

			var dto = gate.toDTO();
			assertThat(dto.ident).isEqualTo("testdb");
			assertThat(dto.backendType).isEqualTo("jdbc");
			assertThat(dto.probeCount).isEqualTo(2);
			assertThat(dto.lastSuccess).isPositive();
			assertThat(dto.lastFailure).isZero();
			assertThat(dto.config)
					.containsEntry(LivenessConstants.CHECK_INTERVAL, "30")
					.containsEntry(LivenessConstants.FAILURE_THRESHOLD, "3")
					.containsEntry(LivenessConstants.RETRY_MAX, "8");
		}
	}
}
