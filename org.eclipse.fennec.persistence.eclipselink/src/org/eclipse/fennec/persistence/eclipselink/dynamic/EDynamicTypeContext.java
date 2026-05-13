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
package org.eclipse.fennec.persistence.eclipselink.dynamic;

import static java.util.Objects.nonNull;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.api.TypeConverter;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.processor.ProcessingContext;
import org.eclipse.persistence.sessions.Session;

/**
 * Cache objects that holds various objects during setup
 * @author Mark Hoffmann
 * @since 21.12.2024
 */
public class EDynamicTypeContext implements ProcessingContext {

	private final Map<Entity, EDynamicTypeBuilder> builders = new ConcurrentHashMap<>();
	private final Map<EClassifier, Entity> entityMap = new ConcurrentHashMap<>();
	private ConverterService converter;
	private URI baseURI;
	private Session session;
	private ClassLoader classloader;
	private boolean useDelimitedIdentifiers;

	/**
	 * Sets the session.
	 * @param session the session to set
	 */
	void setSession(Session session) {
		this.session = session;
	}

	/**
	 * Returns the session.
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Sets the converter.
	 * @param converter the converter to set
	 */
	void setConverter(ConverterService converter) {
		this.converter = converter;
	}

	/**
	 * Returns the converter.
	 * @param name the converter name
	 * @return the converter or <code>null</code>
	 */
	public TypeConverter getConverter(String name) {
		if (Objects.isNull(converter)) {
			return null;
		}
		return converter.getConverter(name);
	}

	/**
	 * Returns the converter.
	 * @param type the converter type
	 * @return the converter or <code>null</code>
	 */
	public TypeConverter getConverter(EClassifier type) {
		if (Objects.isNull(converter)) {
			return null;
		}
		return converter.getConverter(type);
	}

	/**
	 * Associates the given entity with the builder.
	 * @param key the entity
	 * @param value the builder
	 * @return the previous builder, or null
	 */
	public EDynamicTypeBuilder put(Entity key, EDynamicTypeBuilder value) {
		EDynamicTypeBuilder r = builders.put(key, value);
		entityMap.put(key.getClass_(), key);
		return r;
	}

	/**
	 * Returns the builder for the given entity, or null.
	 * @param key the entity
	 * @return the builder or null
	 */
	public EDynamicTypeBuilder get(Entity key) {
		return builders.get(key);
	}

	/**
	 * Associates the entity with the builder if not already present.
	 * @param key the entity
	 * @param mappingFunction function to create the builder if absent
	 * @return the existing or computed builder
	 */
	public EDynamicTypeBuilder computeIfAbsent(Entity key,
			Function<? super Entity, ? extends EDynamicTypeBuilder> mappingFunction) {
		EDynamicTypeBuilder r = builders.computeIfAbsent(key, mappingFunction);
		entityMap.put(key.getClass_(), key);
		return r;
	}

	/**
	 * Removes the builder for the given entity.
	 * @param key the entity
	 * @return the removed builder, or null
	 */
	public EDynamicTypeBuilder remove(Entity key) {
		EDynamicTypeBuilder r = builders.remove(key);
		if (nonNull(key.getClass_())) {
			entityMap.remove(key.getClass_());
		}
		return r;
	}

	/**
	 * Clears all builders and entity mappings.
	 */
	public void clear() {
		builders.clear();
		entityMap.clear();
	}

	/**
	 * Returns the {@link Entity} for a given {@link EClassifier} or <code>null</code>
	 * @param key the {@link EClassifier}
	 * @return the {@link Entity} or <code>null</code>
	 */
	public Entity getEntity(EClassifier key) {
		return entityMap.get(key);
	}

	/**
	 * Returns the {@link EDynamicTypeBuilder} for the given {@link EClassifier}
	 * or return <code>null</code>
	 * @param key the {@link EClassifier}
	 * @return the {@link EDynamicTypeBuilder} or <code>null</code>
	 */
	public EDynamicTypeBuilder getETypeBuilder(EClassifier key) {
		Entity e = entityMap.get(key);
		if (nonNull(e)) {
			return builders.get(e);
		}
		return null;
	}

	/**
	 * Returns a lambda friendly variant of the {@link EDynamicTypeBuilder}
	 * @see EDynamicTypeContext#getETypeBuilder(EClassifier)
	 * @param key the {@link EClassifier}
	 * @return the {@link Optional} with the {@link EDynamicTypeBuilder} or an nullable {@link Optional}
	 */
	public Optional<EDynamicTypeBuilder> getOptionalETypeBuilder(EClassifier key) {
		return Optional.ofNullable(getETypeBuilder(key));
	}

	/**
	 * Sets the baseURI.
	 * @param baseURI the baseURI to set
	 */
	void setBaseURI(URI baseURI) {
		this.baseURI = baseURI;
	}

	/**
	 * Returns the baseURI.
	 * @return the baseURI
	 */
	public URI getBaseURI() {
		return baseURI;
	}

	/**
	 * Returns the classloader.
	 * @return the classloader
	 */
	public ClassLoader getClassloader() {
		return classloader;
	}

	/**
	 * Sets the classloader.
	 * @param classloader the classloader to set
	 */
	void setClassloader(ClassLoader classloader) {
		this.classloader = classloader;
	}

	/**
	 * @return {@code true} if identifier quoting was requested via
	 *         {@code persistence-unit-metadata/persistence-unit-defaults/delimited-identifiers}.
	 */
	public boolean isUseDelimitedIdentifiers() {
		return useDelimitedIdentifiers;
	}

	void setUseDelimitedIdentifiers(boolean useDelimitedIdentifiers) {
		this.useDelimitedIdentifiers = useDelimitedIdentifiers;
	}

}
