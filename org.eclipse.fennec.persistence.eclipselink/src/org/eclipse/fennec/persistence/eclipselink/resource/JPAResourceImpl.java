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
package org.eclipse.fennec.persistence.eclipselink.resource;

import static java.util.Objects.isNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.fennec.persistence.eclipselink.copying.ECopier;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicHelper;
import org.eclipse.fennec.persistence.engine.PersistenceEngine;
import org.eclipse.fennec.persistence.resource.PersistenceResource;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.sessions.server.Server;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * EMF Resource backed by JPA/EntityManager.
 * <p>
 * URI scheme: {@code jpa://puName/EntityName}
 * <p>
 * Supports:
 * <ul>
 * <li>{@link #doLoad} — loads all entities of the specified type from the database</li>
 * <li>{@link #doSave} — persists/merges all resource contents to the database</li>
 * <li>{@link #getEObject(String)} — resolves proxy fragments ({@code //refName/idAttr/idValue})</li>
 * <li>{@link #count()} — counts entities of this type</li>
 * </ul>
 *
 * @author Mark Hoffmann
 * @since 13.04.2026
 */
public class JPAResourceImpl extends ResourceImpl implements PersistenceResource {

	private static final Logger LOG = Logger.getLogger(JPAResourceImpl.class.getName());

	private final EntityManagerFactory emf;

	public JPAResourceImpl(URI uri, EntityManagerFactory emf) {
		super(uri);
		this.emf = emf;
	}

	@Override
	public void load(Map<?, ?> options) throws IOException {
		if (isLoaded) {
			return;
		}
		doLoad(null, options);
		isLoaded = true;
	}

	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		String entityName = getEntityName();
		if (isNull(entityName)) {
			return;
		}
		ClassDescriptor descriptor = getDescriptor(entityName);
		if (isNull(descriptor)) {
			LOG.log(Level.WARNING, "No descriptor found for entity ''{0}''", entityName);
			return;
		}
		// Use the validated alias from the descriptor to prevent JPQL injection
		String validatedAlias = descriptor.getAlias();
		if (!getContents().isEmpty()) {
			getContents().clear();
		}
		try (EntityManager em = emf.createEntityManager()) {
			List<?> results = em.createQuery(
					"SELECT e FROM " + validatedAlias + " e", descriptor.getJavaClass())
					.getResultList();
			for (Object obj : results) {
				if (obj instanceof EObject eo) {
					getContents().add(eo);
				}
			}
		}
	}

	@Override
	public void save(Map<?, ?> options) throws IOException {
		doSave(null, options);
	}

	@Override
	protected void doSave(OutputStream outputStream, Map<?, ?> options) throws IOException {
		Server server = getServer();
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			try {
				for (EObject eo : getContents()) {
					EObject managed = server != null ? toManagedEntity(eo, server) : eo;
					em.merge(managed);
				}
				em.getTransaction().commit();
			} catch (Exception e) {
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
				throw new IOException("Failed to save resource: " + getURI(), e);
			}
		}
	}

	@Override
	public void delete(Map<?, ?> options) throws IOException {
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			try {
				for (EObject eo : getContents()) {
					Object merged = em.merge(eo);
					em.remove(merged);
				}
				em.getTransaction().commit();
				getContents().clear();
			} catch (Exception e) {
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
				throw new IOException("Failed to delete resource: " + getURI(), e);
			}
		}
	}

	@Override
	protected void doUnload() {
		isLoaded = false;
		getContents().clear();
	}

	@Override
	public EObject getEObject(String uriFragment) {
		if (isNull(uriFragment) || !uriFragment.startsWith("//")) {
			return super.getEObject(uriFragment);
		}
		// Fragment format: //refName/idAttrName/idValue
		String[] parts = uriFragment.substring(2).split("/");
		if (parts.length < 3) {
			return super.getEObject(uriFragment);
		}
		String idValue = parts[2];
		String entityName = getEntityName();
		ClassDescriptor descriptor = getDescriptor(entityName);
		if (isNull(descriptor)) {
			return null;
		}
		try (EntityManager em = emf.createEntityManager()) {
			Object typedId = convertId(idValue, descriptor);
			Object result = em.find(descriptor.getJavaClass(), typedId);
			if (result instanceof EObject resolved) {
				// Add the resolved object to this resource's contents so it has
				// an eResource() and can be found on subsequent accesses without
				// hitting the database again. This is the standard EMF pattern
				// for proxy resolution via ResourceSet → Resource → getEObject.
				if (resolved.eResource() == null && !getContents().contains(resolved)) {
					getContents().add(resolved);
				}
				return resolved;
			}
			return null;
		}
	}

	@Override
	public long count() throws IOException {
		return count(null);
	}

	@Override
	public long count(Map<?, ?> options) throws IOException {
		String entityName = getEntityName();
		if (isNull(entityName)) {
			return 0;
		}
		ClassDescriptor descriptor = getDescriptor(entityName);
		if (isNull(descriptor)) {
			return 0;
		}
		// Use the validated alias from the descriptor to prevent JPQL injection
		String validatedAlias = descriptor.getAlias();
		try (EntityManager em = emf.createEntityManager()) {
			return em.createQuery("SELECT COUNT(e) FROM " + validatedAlias + " e", Long.class)
					.getSingleResult();
		}
	}

	@Override
	public boolean exist() throws IOException {
		return exist(null);
	}

	@Override
	public boolean exist(Map<?, ?> options) throws IOException {
		return count(options) > 0;
	}

	@Override
	public PersistenceEngine getEngine() {
		throw new UnsupportedOperationException("JPAResourceImpl does not use a PersistenceEngine — persistence is managed directly via EntityManagerFactory");
	}

	@Override
	public void updateDefaultOptions(Map<Object, Object> options, ActionType... types) {
		// No-op for initial implementation
	}

	@Override
	public void close() throws Exception {
		unload();
	}

	/**
	 * Extracts the entity name from the resource URI.
	 * For {@code jpa://puName/EntityName}, returns "EntityName".
	 */
	private String getEntityName() {
		URI uri = getURI();
		if (isNull(uri) || uri.segmentCount() == 0) {
			return null;
		}
		return uri.lastSegment();
	}

	/**
	 * Returns the EclipseLink Server session, or null if the EMF is not EclipseLink-backed.
	 */
	private Server getServer() {
		try {
			return JpaHelper.getServerSession(emf);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * Finds the EclipseLink descriptor for the given entity name.
	 */
	ClassDescriptor getDescriptor(String entityName) {
		if (isNull(entityName)) {
			return null;
		}
		Server server = getServer();
		return server != null ? server.getDescriptorForAlias(entityName) : null;
	}

	/**
	 * Converts an EObject to a managed EclipseLink entity if necessary.
	 * If the object is already of the correct dynamic class (i.e. EclipseLink knows its Java class),
	 * it is returned as-is. Otherwise, a new entity is created via the descriptor's instantiation
	 * policy and all features are copied from the source using {@link ECopier}.
	 * This enables persisting plain {@code DynamicEObjectImpl} objects loaded from XMI or
	 * created outside EclipseLink.
	 */
	private EObject toManagedEntity(EObject source, Server server) {
		// Fast path: EclipseLink already knows this object's class
		ClassDescriptor descriptor = server.getDescriptor(source.getClass());
		if (descriptor != null) {
			return source;
		}
		// Slow path: look up by EClass alias and convert
		descriptor = server.getDescriptorForAlias(source.eClass().getName());
		if (descriptor == null) {
			LOG.log(Level.WARNING, "No descriptor found for EClass ''{0}'' — passing object as-is", source.eClass().getName());
			return source;
		}
		EObject target = EDynamicHelper.createInstance(descriptor);
		ECopier copier = new ECopier(target, null);
		copier.setCopyContainments(true);
		copier.setCopyFunction(src -> {
			ClassDescriptor childDesc = server.getDescriptorForAlias(src.eClass().getName());
			if (childDesc != null) {
				return EDynamicHelper.createInstance(childDesc);
			}
			return null;
		});
		EObject result = copier.copy(source);
		copier.copyReferences();
		return result;
	}

	/**
	 * Converts a string ID value to the appropriate type for the descriptor's primary key.
	 * Uses the descriptor's primary key field type to determine the correct conversion.
	 */
	private Object convertId(String idValue, ClassDescriptor descriptor) {
		if (!descriptor.getPrimaryKeyFields().isEmpty()) {
			Class<?> pkType = descriptor.getPrimaryKeyFields().get(0).getType();
			if (pkType != null) {
				if (Integer.class.isAssignableFrom(pkType) || int.class.equals(pkType)) {
					return Integer.valueOf(idValue);
				}
				if (Long.class.isAssignableFrom(pkType) || long.class.equals(pkType)) {
					return Long.valueOf(idValue);
				}
			}
		}
		// Default: return as String (UUID, etc.)
		return idValue;
	}
}
