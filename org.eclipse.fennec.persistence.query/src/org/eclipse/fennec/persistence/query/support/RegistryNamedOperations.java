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
package org.eclipse.fennec.persistence.query.support;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistry;
import org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryWriter;

/**
 * {@link NamedOperations} over an {@code emf.osgi} EObject registry (issue #203) — the
 * default home for named queries and commands.
 * <p>
 * The registry is exactly the shape this needs and already exists: string-keyed, provider-fed,
 * change-listening, with an explicit non-OSGi mode. Nothing here does more than adapt names
 * to keys, which is the point — a catalog is a lookup, and the fennec stack already has one
 * for model instances.
 * <p>
 * Reading needs only an {@link EObjectRegistry}. Writing needs the
 * {@link EObjectRegistryWriter} that owns it, and a <em>source</em> — the registry attributes
 * every entry to whoever contributed it, so entries can be replaced or withdrawn as a group.
 * Constructed without a writer, this catalog is read-only and says so rather than dropping
 * writes silently.
 *
 * @author Mark Hoffmann
 * @since 22.08.2026
 */
public class RegistryNamedOperations implements NamedOperations {

	/** The source name entries are attributed to when none is given. */
	public static final String DEFAULT_SOURCE = "fennec.persistence.named-operations";

	private final EObjectRegistry registry;
	private final EObjectRegistryWriter writer;
	private final String source;

	/**
	 * A read-only catalog over an existing registry.
	 *
	 * @param registry the registry to read from, must not be {@code null}
	 */
	public RegistryNamedOperations(EObjectRegistry registry) {
		this.registry = requireNonNull(registry, "registry must not be null");
		this.writer = null;
		this.source = DEFAULT_SOURCE;
	}

	/**
	 * A read-write catalog over the registry the writer owns.
	 *
	 * @param writer the writer whose registry this reads and writes, must not be {@code null}
	 */
	public RegistryNamedOperations(EObjectRegistryWriter writer) {
		this(writer, DEFAULT_SOURCE);
	}

	/**
	 * A read-write catalog attributing its entries to {@code source}.
	 *
	 * @param writer the writer whose registry this reads and writes, must not be {@code null}
	 * @param source the contributor name entries are recorded under, must not be {@code null}
	 */
	public RegistryNamedOperations(EObjectRegistryWriter writer, String source) {
		this.writer = requireNonNull(writer, "writer must not be null");
		this.registry = requireNonNull(writer.getRegistry(), "the writer has no registry");
		this.source = requireNonNull(source, "source must not be null");
	}

	@Override
	public Optional<EObject> lookup(String name) throws IOException {
		requireNonNull(name, "name must not be null");
		return registry.get(name);
	}

	@Override
	public void store(String name, EObject operation) throws IOException {
		requireNonNull(name, "name must not be null");
		requireNonNull(operation, "operation must not be null");
		requireWritable("store");
		writer.put(source, name, operation, Map.of());
	}

	@Override
	public void remove(String name) throws IOException {
		requireNonNull(name, "name must not be null");
		requireWritable("remove");
		writer.remove(source, name);
	}

	private void requireWritable(String what) throws IOException {
		if (writer == null) {
			throw new IOException("This named-operation catalog is read-only — cannot " + what
					+ ". Construct it with an EObjectRegistryWriter to write.");
		}
	}
}
