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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Deterministic single-threaded {@link ScheduledExecutorService} for unit tests: tasks
 * run synchronously from {@link #advanceBy(long)} on the caller thread, ordered by due
 * time. Only the API used by {@code LivenessGate} is implemented.
 */
final class TestScheduler extends AbstractExecutorService implements ScheduledExecutorService {

	private final List<TestTask> tasks = new ArrayList<>();
	private final AtomicLong sequence = new AtomicLong();
	private long nowMillis;
	private boolean shutdown;

	/**
	 * Advances the virtual clock and synchronously runs every due, non-cancelled task —
	 * including tasks scheduled by tasks run in this call, if they are due.
	 */
	void advanceBy(long millis) {
		nowMillis += millis;
		while (true) {
			Optional<TestTask> next;
			synchronized (tasks) {
				next = tasks.stream()
						.filter(t -> !t.cancelled && t.dueMillis <= nowMillis)
						.min(Comparator.comparingLong((TestTask t) -> t.dueMillis)
								.thenComparingLong(t -> t.order));
				next.ifPresent(tasks::remove);
			}
			if (next.isEmpty()) {
				return;
			}
			next.get().runnable.run();
		}
	}

	/** Number of scheduled, not yet executed, non-cancelled tasks. */
	int pendingTasks() {
		synchronized (tasks) {
			return (int) tasks.stream().filter(t -> !t.cancelled).count();
		}
	}

	@Override
	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		TestTask task = new TestTask(command, nowMillis + unit.toMillis(delay), sequence.incrementAndGet());
		synchronized (tasks) {
			tasks.add(task);
		}
		return task;
	}

	@Override
	public void execute(Runnable command) {
		schedule(command, 0, TimeUnit.MILLISECONDS);
	}

	@Override
	public void shutdown() {
		shutdown = true;
	}

	@Override
	public List<Runnable> shutdownNow() {
		shutdown = true;
		synchronized (tasks) {
			tasks.clear();
		}
		return List.of();
	}

	@Override
	public boolean isShutdown() {
		return shutdown;
	}

	@Override
	public boolean isTerminated() {
		return shutdown;
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) {
		return true;
	}

	@Override
	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
		throw new UnsupportedOperationException();
	}

	private final class TestTask implements ScheduledFuture<Object> {

		private final Runnable runnable;
		private final long dueMillis;
		private final long order;
		private volatile boolean cancelled;

		private TestTask(Runnable runnable, long dueMillis, long order) {
			this.runnable = runnable;
			this.dueMillis = dueMillis;
			this.order = order;
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			cancelled = true;
			return true;
		}

		@Override
		public boolean isCancelled() {
			return cancelled;
		}

		@Override
		public boolean isDone() {
			return cancelled;
		}

		@Override
		public Object get() throws InterruptedException, ExecutionException {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object get(long timeout, TimeUnit unit)
				throws InterruptedException, ExecutionException, TimeoutException {
			throw new UnsupportedOperationException();
		}

		@Override
		public long getDelay(TimeUnit unit) {
			return unit.convert(dueMillis - nowMillis, TimeUnit.MILLISECONDS);
		}

		@Override
		public int compareTo(Delayed other) {
			return Long.compare(getDelay(TimeUnit.MILLISECONDS), other.getDelay(TimeUnit.MILLISECONDS));
		}
	}
}
