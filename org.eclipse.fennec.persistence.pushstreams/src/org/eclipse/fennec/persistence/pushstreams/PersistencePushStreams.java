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
package org.eclipse.fennec.persistence.pushstreams;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.persistence.resource.StreamingResource;
import org.osgi.util.pushstream.PushEvent;
import org.osgi.util.pushstream.PushEventConsumer;
import org.osgi.util.pushstream.PushEventSource;
import org.osgi.util.pushstream.PushStream;
import org.osgi.util.pushstream.PushStreamProvider;

/**
 * First-class OSGi PushStream support for persistence resources.
 * <p>
 * Bridges the pull-based {@link StreamingResource#stream(Map)} of a backend into a
 * backpressured {@link PushStream}/{@link PushEventSource} — one generic adapter for
 * every backend (JPA, MongoDB, …) that implements {@link StreamingResource}.
 * <p>
 * Semantics:
 * <ul>
 * <li>The backend stream is opened lazily when a consumer connects and is
 *     <b>always closed</b> — on completion, error and cancellation (this releases the
 *     backend cursor and its underlying resources).</li>
 * <li>Backpressure is honored per the OSGi contract: a negative return of
 *     {@code PushEventConsumer.accept} stops delivery, a positive return pauses
 *     delivery for that many milliseconds.</li>
 * <li>Completion is signalled with {@code PushEvent.close()}, failures with
 *     {@code PushEvent.error(...)} — exactly one terminal event is delivered.</li>
 * </ul>
 * Consumers choose their own buffering and queue policies through the standard
 * {@link PushStreamProvider} builder API:
 * <pre>
 * PushStreamProvider psp = new PushStreamProvider();
 * try (PushStream&lt;EObject&gt; stream = psp.buildStream(
 *             PersistencePushStreams.createEventSource(resource, null, executor))
 *         .withBuffer(new ArrayBlockingQueue&lt;&gt;(1000))
 *         .build()) {
 *     stream.forEach(...);
 * }
 * </pre>
 *
 * @author Mark Hoffmann
 * @since 16.07.2026
 */
public final class PersistencePushStreams {

	private static final Logger LOG = Logger.getLogger(PersistencePushStreams.class.getName());

	private static volatile ExecutorService defaultExecutor;

	private PersistencePushStreams() {
	}

	/**
	 * Creates a {@link PushEventSource} over the resource's backend stream. Each
	 * {@code open(consumer)} call opens a fresh backend stream and delivers it on the
	 * given executor; the returned {@code AutoCloseable} cancels delivery and closes
	 * the backend stream.
	 *
	 * @param resource the streaming-capable persistence resource
	 * @param options backend stream options; may be {@code null}
	 * @param executor executor delivering the events; {@code null} for a shared default
	 * @return the event source
	 */
	public static PushEventSource<EObject> createEventSource(StreamingResource resource,
			Map<?, ?> options, ExecutorService executor) {
		requireNonNull(resource, "StreamingResource is required");
		ExecutorService effectiveExecutor = isNull(executor) ? defaultExecutor() : executor;
		return consumer -> {
			AtomicBoolean closed = new AtomicBoolean(false);
			Future<?> future = effectiveExecutor.submit(
					new StreamDeliveryTask(resource, options, consumer, closed));
			return () -> {
				closed.set(true);
				future.cancel(true);
			};
		};
	}

	/**
	 * Convenience: creates an unbuffered {@link PushStream} over the resource's backend
	 * stream with default policies. For custom buffering/backpressure policies use
	 * {@link #createEventSource(StreamingResource, Map, ExecutorService)} with
	 * {@link PushStreamProvider#buildStream(PushEventSource)}.
	 *
	 * @param resource the streaming-capable persistence resource
	 * @param options backend stream options; may be {@code null}
	 * @param provider the push stream provider; {@code null} for a new default provider
	 * @param executor executor delivering the events; {@code null} for a shared default
	 * @return the push stream; closing it stops delivery and releases backend resources
	 */
	public static PushStream<EObject> createPushStream(StreamingResource resource,
			Map<?, ?> options, PushStreamProvider provider, ExecutorService executor) {
		PushStreamProvider effectiveProvider = isNull(provider) ? new PushStreamProvider() : provider;
		return effectiveProvider.createStream(createEventSource(resource, options, executor));
	}

	private static ExecutorService defaultExecutor() {
		ExecutorService executor = defaultExecutor;
		if (isNull(executor)) {
			synchronized (PersistencePushStreams.class) {
				if (isNull(defaultExecutor)) {
					defaultExecutor = Executors.newCachedThreadPool(runnable -> {
						Thread thread = new Thread(runnable, "fennec-persistence-pushstream");
						thread.setDaemon(true);
						return thread;
					});
				}
				executor = defaultExecutor;
			}
		}
		return executor;
	}

	/**
	 * Pulls the backend stream and pushes each object to the consumer, honoring the
	 * OSGi backpressure contract. Guarantees exactly one terminal event and that the
	 * backend stream is closed on every exit path.
	 */
	private static final class StreamDeliveryTask implements Runnable {

		private final StreamingResource resource;
		private final Map<?, ?> options;
		private final PushEventConsumer<? super EObject> consumer;
		private final AtomicBoolean closed;

		StreamDeliveryTask(StreamingResource resource, Map<?, ?> options,
				PushEventConsumer<? super EObject> consumer, AtomicBoolean closed) {
			this.resource = resource;
			this.options = options;
			this.consumer = consumer;
			this.closed = closed;
		}

		@Override
		public void run() {
			try (Stream<EObject> stream = resource.stream(options)) {
				Iterator<EObject> iterator = stream.iterator();
				while (!closed.get() && !Thread.currentThread().isInterrupted() && iterator.hasNext()) {
					long backPressure = consumer.accept(PushEvent.data(iterator.next()));
					if (backPressure < 0) {
						break;
					}
					if (backPressure > 0) {
						Thread.sleep(backPressure);
					}
				}
				terminate(PushEvent.close());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				terminate(PushEvent.close());
			} catch (Exception e) {
				terminate(PushEvent.error(e));
			}
		}

		private void terminate(PushEvent<EObject> terminalEvent) {
			if (closed.getAndSet(true)) {
				return;
			}
			try {
				consumer.accept(terminalEvent);
			} catch (Exception e) {
				LOG.log(Level.FINE, "Consumer rejected terminal event", e);
			}
		}
	}
}
