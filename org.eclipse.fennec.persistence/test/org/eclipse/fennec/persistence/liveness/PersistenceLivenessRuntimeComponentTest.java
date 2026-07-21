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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.fennec.persistence.liveness.dto.LivenessRuntimeDTO;
import org.eclipse.fennec.persistence.liveness.impl.LivenessGateRegistry;
import org.eclipse.fennec.persistence.liveness.impl.PersistenceLivenessRuntimeComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.condition.Condition;

/**
 * Unit tests for {@link PersistenceLivenessRuntimeComponent}: DTO mapping and
 * {@code service.changecount} semantics.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PersistenceLivenessRuntimeComponentTest {

	@Mock
	private BundleContext context;
	@Mock
	private ServiceRegistration<PersistenceLivenessRuntime> runtimeRegistration;
	@Mock
	private ServiceRegistration<String> serviceRegistration;
	@Mock
	private ServiceRegistration<Condition> conditionRegistration;

	private final TestScheduler scheduler = new TestScheduler();
	private final LivenessGateRegistry registry = LivenessGateRegistry.newInstance();
	private final AtomicBoolean healthy = new AtomicBoolean(true);

	@BeforeEach
	void setUp() {
		when(context.registerService(eq(PersistenceLivenessRuntime.class),
				any(PersistenceLivenessRuntime.class), any())).thenReturn(runtimeRegistration);
		when(context.registerService(eq(String.class), eq("service"), any()))
				.thenReturn(serviceRegistration);
		when(context.registerService(eq(Condition.class), eq(Condition.INSTANCE), any()))
				.thenReturn(conditionRegistration);
	}

	private LivenessGate<String> gate(String ident) {
		return LivenessGate.builder(context, String.class, "service")
				.ident(ident)
				.backendType("jdbc")
				.serviceProperties(Map.of())
				.probe(timeout -> {
					if (!healthy.get()) {
						throw new IllegalStateException("down");
					}
				})
				.config(LivenessConfig.of(true, 30, 5, 1, 1, 8))
				.executor(scheduler)
				.registry(registry)
				.build();
	}

	@Test
	void runtimeDtoReflectsGatesAndTheirState() {
		PersistenceLivenessRuntimeComponent runtime =
				new PersistenceLivenessRuntimeComponent(context, registry);
		assertThat(runtime.getRuntimeDTO().gates).isEmpty();

		LivenessGate<String> gate = gate("db-one");
		gate.open();
		scheduler.advanceBy(0);

		LivenessRuntimeDTO dto = runtime.getRuntimeDTO();
		assertThat(dto.gates).hasSize(1);
		assertThat(dto.gates[0].ident).isEqualTo("db-one");
		assertThat(dto.gates[0].state).isEqualTo(LivenessConstants.STATE_UP);
		assertThat(dto.gates[0].probeCount).isEqualTo(1);

		healthy.set(false);
		scheduler.advanceBy(30_000);
		assertThat(runtime.getRuntimeDTO().gates[0].state).isEqualTo(LivenessConstants.STATE_DOWN);
		assertThat(runtime.getRuntimeDTO().gates[0].lastFailureMessage).isEqualTo("down");

		gate.close();
		assertThat(runtime.getRuntimeDTO().gates).isEmpty();
		runtime.deactivate();
	}

	@Test
	void changeCountIncreasesOnEveryTransition() {
		new PersistenceLivenessRuntimeComponent(context, registry);

		LivenessGate<String> gate = gate("db-one");
		gate.open(); // add
		scheduler.advanceBy(0); // DOWN -> UP
		healthy.set(false);
		scheduler.advanceBy(30_000); // UP -> DOWN (threshold 1)
		gate.close(); // remove

		ArgumentCaptor<Dictionary<String, Object>> props = ArgumentCaptor.captor();
		verify(runtimeRegistration, atLeastOnce()).setProperties(props.capture());
		List<Long> counts = props.getAllValues().stream()
				.map(dictionary -> (Long) dictionary.get(Constants.SERVICE_CHANGECOUNT))
				.toList();
		assertThat(counts).hasSize(4).isSorted().doesNotHaveDuplicates();
	}

	@Test
	void deactivateUnregistersAndDetaches() {
		PersistenceLivenessRuntimeComponent runtime =
				new PersistenceLivenessRuntimeComponent(context, registry);
		runtime.deactivate();
		verify(runtimeRegistration).unregister();

		// no further changecount updates after detach
		LivenessGate<String> gate = gate("db-one");
		gate.open();
		verify(runtimeRegistration, atLeastOnce()).unregister();
		gate.close();
	}
}
