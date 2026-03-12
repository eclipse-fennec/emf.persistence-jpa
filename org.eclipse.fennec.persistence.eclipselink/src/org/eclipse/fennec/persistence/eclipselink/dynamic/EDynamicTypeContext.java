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
import java.util.function.BiFunction;
import java.util.function.Function;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.fennec.persistence.api.ConverterService;
import org.eclipse.fennec.persistence.api.TypeConverter;
import org.eclipse.fennec.persistence.eorm.Entity;
import org.eclipse.fennec.persistence.processor.ProcessingContext;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.sessions.Session;

/**
 * Cache objects that holds various objects during setup
 * @author Mark Hoffmann
 * @since 21.12.2024
 */
public class EDynamicTypeContext extends ConcurrentHashMap<Entity, EDynamicTypeBuilder> implements ProcessingContext {
	
	/** serialVersionUID */
	private static final long serialVersionUID = 1090088991386752211L;
	private final Map<EClassifier, Entity> entityMap = new ConcurrentHashMap<>();
	private ConverterService converter;
	private URI baseURI;
	private Session session;
	private DynamicClassLoader classloader;
	
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
	
	/* 
	 * (non-Javadoc)
	 * @see java.util.concurrent.ConcurrentHashMap#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public EDynamicTypeBuilder put(Entity key, EDynamicTypeBuilder value) {
		EDynamicTypeBuilder r = super.put(key, value);
		entityMap.put(key.getClass_(), key);
		return r;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.util.concurrent.ConcurrentHashMap#putIfAbsent(java.lang.Object, java.lang.Object)
	 */
	@Override
	public EDynamicTypeBuilder putIfAbsent(Entity key, EDynamicTypeBuilder value) {
		EDynamicTypeBuilder r = super.putIfAbsent(key, value);
		entityMap.put(key.getClass_(), key);
		return r;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.util.concurrent.ConcurrentHashMap#compute(java.lang.Object, java.util.function.BiFunction)
	 */
	@Override
	public EDynamicTypeBuilder compute(Entity key,
			BiFunction<? super Entity, ? super EDynamicTypeBuilder, ? extends EDynamicTypeBuilder> remappingFunction) {
		EDynamicTypeBuilder r = super.compute(key, remappingFunction);
		entityMap.put(key.getClass_(), key);
		return r;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.util.concurrent.ConcurrentHashMap#computeIfAbsent(java.lang.Object, java.util.function.Function)
	 */
	@Override
	public EDynamicTypeBuilder computeIfAbsent(Entity key,
			Function<? super Entity, ? extends EDynamicTypeBuilder> mappingFunction) {
		EDynamicTypeBuilder r = super.computeIfAbsent(key, mappingFunction);
		entityMap.put(key.getClass_(), key);
		return r;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.util.concurrent.ConcurrentHashMap#computeIfPresent(java.lang.Object, java.util.function.BiFunction)
	 */
	@Override
	public EDynamicTypeBuilder computeIfPresent(Entity key,
			BiFunction<? super Entity, ? super EDynamicTypeBuilder, ? extends EDynamicTypeBuilder> remappingFunction) {
		EDynamicTypeBuilder r = super.computeIfPresent(key, remappingFunction);
		entityMap.put(key.getClass_(), key);
		return r;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.util.concurrent.ConcurrentHashMap#remove(java.lang.Object)
	 */
	@Override
	public EDynamicTypeBuilder remove(Object key) {
		EDynamicTypeBuilder r = super.remove(key);
		entityMap.remove(key);
		return r;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.util.concurrent.ConcurrentHashMap#clear()
	 */
	@Override
	public void clear() {
		super.clear();
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
			return get(e);
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
	public DynamicClassLoader getClassloader() {
		return classloader;
	}

	/**
	 * Sets the classloader.
	 * @param classloader the classloader to set
	 */
	void setClassloader(DynamicClassLoader classloader) {
		this.classloader = classloader;
	}

}
