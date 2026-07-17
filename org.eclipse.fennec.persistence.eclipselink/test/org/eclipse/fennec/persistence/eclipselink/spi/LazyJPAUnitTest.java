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
package org.eclipse.fennec.persistence.eclipselink.spi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.eclipse.fennec.persistence.eclipselink.spi.JPAUnit.Lease;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Lifecycle tests for {@link LazyJPAUnit}: lazy build, lease counting, idle/immediate/never
 * close, and transparent recreation on the next lease (issue #20).
 */
class LazyJPAUnitTest {

	/** Supplier that hands out a fresh open mock EMF per call and records them. */
	private static final class EmfSupplier implements Supplier<EntityManagerFactory> {
		final List<EntityManagerFactory> built = new ArrayList<>();
		final AtomicInteger calls = new AtomicInteger();

		@Override
		public EntityManagerFactory get() {
			calls.incrementAndGet();
			EntityManagerFactory emf = mock(EntityManagerFactory.class);
			when(emf.isOpen()).thenReturn(true);
			when(emf.createEntityManager()).thenReturn(mock(EntityManager.class));
			built.add(emf);
			return emf;
		}
	}

	private static void awaitFactoryClosed(LazyJPAUnit unit) throws InterruptedException {
		long deadline = System.currentTimeMillis() + 3000;
		while (unit.isFactoryOpen() && System.currentTimeMillis() < deadline) {
			Thread.sleep(20);
		}
	}

	@Test
	void testFactoryBuiltLazilyOnFirstLease() {
		EmfSupplier supplier = new EmfSupplier();
		LazyJPAUnit unit = new LazyJPAUnit("test", supplier, -1);
		try {
			// Construction must not build the heavyweight factory.
			assertThat(supplier.calls).hasValue(0);
			assertThat(unit.isFactoryOpen()).isFalse();

			try (Lease lease = unit.lease()) {
				assertThat(supplier.calls).as("first lease builds the factory").hasValue(1);
				assertThat(unit.isFactoryOpen()).isTrue();
				assertThat(lease.createEntityManager()).isNotNull();
			}
		} finally {
			unit.dispose();
		}
	}

	@Test
	void testConcurrentOpenLeasesShareOneFactory() {
		EmfSupplier supplier = new EmfSupplier();
		LazyJPAUnit unit = new LazyJPAUnit("test", supplier, -1);
		try {
			Lease a = unit.lease();
			Lease b = unit.lease();
			assertThat(supplier.calls).as("second lease reuses the factory").hasValue(1);
			assertThat(unit.activeLeaseCount()).isEqualTo(2);
			a.close();
			assertThat(unit.isFactoryOpen()).as("factory stays open while a lease is held").isTrue();
			b.close();
		} finally {
			unit.dispose();
		}
	}

	@Test
	void testImmediateCloseRecreatesOnNextLease() {
		EmfSupplier supplier = new EmfSupplier();
		LazyJPAUnit unit = new LazyJPAUnit("test", supplier, 0); // 0 = close immediately
		try {
			try (Lease lease = unit.lease()) {
				assertThat(lease.createEntityManager()).isNotNull();
			}
			// Last lease closed → factory closed synchronously.
			assertThat(unit.isFactoryOpen()).isFalse();
			verify(supplier.built.get(0)).close();

			try (Lease lease = unit.lease()) {
				// A brand-new factory is built.
				assertThat(supplier.calls).hasValue(2);
				assertThat(supplier.built).hasSize(2);
			}
		} finally {
			unit.dispose();
		}
	}

	@Test
	void testIdleTimeoutCloses() throws InterruptedException {
		EmfSupplier supplier = new EmfSupplier();
		LazyJPAUnit unit = new LazyJPAUnit("test", supplier, 120); // 120ms idle
		try {
			try (Lease lease = unit.lease()) {
				assertThat(unit.isFactoryOpen()).isTrue();
			}
			// Not closed instantly — the idle timer is still running.
			assertThat(unit.isFactoryOpen()).isTrue();
			awaitFactoryClosed(unit);
			assertThat(unit.isFactoryOpen()).as("factory closes after the idle timeout").isFalse();
			verify(supplier.built.get(0)).close();

			// Next lease rebuilds.
			try (Lease lease = unit.lease()) {
				assertThat(supplier.calls).hasValue(2);
			}
		} finally {
			unit.dispose();
		}
	}

	@Test
	void testNeverCloseUntilDispose() {
		EmfSupplier supplier = new EmfSupplier();
		LazyJPAUnit unit = new LazyJPAUnit("test", supplier, -1); // never auto-close
		try (Lease lease = unit.lease()) {
			// nothing
		}
		assertThat(unit.isFactoryOpen()).as("never mode keeps the factory open").isTrue();

		unit.dispose();
		assertThat(unit.isFactoryOpen()).isFalse();
		verify(supplier.built.get(0)).close();
	}

	@Test
	void testDisposedUnitRejectsLease() {
		LazyJPAUnit unit = new LazyJPAUnit("test", new EmfSupplier(), -1);
		unit.dispose();
		org.assertj.core.api.Assertions.assertThatThrownBy(unit::lease)
				.isInstanceOf(IllegalStateException.class);
	}
}
