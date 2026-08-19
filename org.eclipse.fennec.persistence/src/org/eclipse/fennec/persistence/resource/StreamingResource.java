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
package org.eclipse.fennec.persistence.resource;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;

/**
 * Optional capability of a {@link PersistenceResource}: streams the persisted objects
 * of this resource lazily from the backend — objects are materialised one by one while
 * the stream is consumed, without loading them into {@link org.eclipse.emf.ecore.resource.Resource#getContents()}.
 * <p>
 * The returned stream holds backend resources (cursors, connections). Callers
 * <b>must close it</b> — use try-with-resources:
 * <pre>
 * try (Stream&lt;EObject&gt; stream = resource.stream()) {
 *     stream.forEach(...);
 * }
 * </pre>
 * Closing the stream releases all underlying backend resources; this also happens when
 * the stream terminates exceptionally inside a try-with-resources block.
 * <p>
 * Streamed objects follow the same reference contract as loaded ones: non-containment
 * references hold EMF proxies that resolve through the {@code ResourceSet}.
 * <p>
 * They are handed out <b>detached</b>: they are not added to
 * {@link org.eclipse.emf.ecore.resource.Resource#getContents()}, and which resource a streamed
 * object reports is not part of the contract — do not ask. Mutating one writes nothing;
 * persisting it means adding it to a resource and saving that resource, like any other object.
 * <p>
 * Streaming is a declarable capability, not part of the conformance core (contract §9.4): a
 * backend that cannot iterate without materialising the whole result set says so by not
 * declaring it, rather than by pretending to stream.
 *
 * @author Mark Hoffmann
 * @since 16.07.2026
 */
public interface StreamingResource {

	/**
	 * Streams all persisted objects of this resource.
	 * @return the stream; must be closed by the caller
	 * @throws IOException on backend errors while opening the stream
	 */
	Stream<EObject> stream() throws IOException;

	/**
	 * Streams the persisted objects of this resource.
	 * @param options backend options (e.g. batch size); may be {@code null}
	 * @return the stream; must be closed by the caller
	 * @throws IOException on backend errors while opening the stream
	 */
	Stream<EObject> stream(Map<?, ?> options) throws IOException;
}
