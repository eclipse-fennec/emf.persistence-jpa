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
import static java.util.Objects.nonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.fennec.persistence.Options;
import org.eclipse.fennec.persistence.eclipselink.copying.ECopier;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicHelper;
import org.eclipse.fennec.persistence.resource.PersistenceResource;
import org.eclipse.fennec.persistence.resource.StreamingResource;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.queries.ScrollableCursor;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.server.Server;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

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
public class JPAResourceImpl extends ResourceImpl implements PersistenceResource, StreamingResource {

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
		getErrors().clear();
		getWarnings().clear();
		String entityName = getEntityName();
		if (isNull(entityName)) {
			getWarnings().add(new JPADiagnostic(
					"Resource URI has no entity segment — nothing to load", getURI()));
			return;
		}
		ClassDescriptor descriptor = getDescriptor(entityName);
		if (isNull(descriptor)) {
			getWarnings().add(new JPADiagnostic(
					"No descriptor found for entity '" + entityName + "'", getURI()));
			return;
		}
		// Use the validated alias from the descriptor to prevent JPQL injection
		String validatedAlias = descriptor.getAlias();
		if (!getContents().isEmpty()) {
			getContents().clear();
		}
		int pageSize = Options.getPageSize(options);
		try (EntityManager em = emf.createEntityManager()) {
			TypedQuery<?> query = em.createQuery(
					"SELECT e FROM " + validatedAlias + " e", descriptor.getJavaClass());
			if (pageSize > 0) {
				loadPaginated(query, pageSize);
			} else {
				addToContents(query.getResultList());
			}
		} catch (RuntimeException e) {
			getErrors().add(new JPADiagnostic(
					"Failed to load entity '" + entityName + "': " + e.getMessage(), getURI(), e));
			throw new IOException("Failed to load resource: " + getURI(), e);
		}
	}

	private void loadPaginated(TypedQuery<?> query, int pageSize) {
		int offset = 0;
		List<?> page;
		do {
			page = query.setFirstResult(offset).setMaxResults(pageSize).getResultList();
			addToContents(page);
			offset += page.size();
		} while (page.size() == pageSize);
	}

	private void addToContents(List<?> results) {
		for (Object obj : results) {
			if (obj instanceof EObject eo) {
				getContents().add(eo);
			}
		}
	}

	@Override
	public void save(Map<?, ?> options) throws IOException {
		doSave(null, options);
	}

	@Override
	protected void doSave(OutputStream outputStream, Map<?, ?> options) throws IOException {
		getErrors().clear();
		getWarnings().clear();
		Server server = getServer();
		// Pre-build the entity factory function once for all objects (avoids lambda allocation per object)
		Function<EObject, EObject> entityFactory = nonNull(server)
				? src -> {
					ClassDescriptor desc = server.getDescriptorForAlias(src.eClass().getName());
					return nonNull(desc) ? EDynamicHelper.createInstance(desc) : null;
				}
				: null;
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			applyCacheNewObjectsOption(em, options);
			try {
				for (EObject eo : getContents()) {
					EObject source = nonNull(server) ? toManagedEntity(eo, server, entityFactory) : eo;
					upsert(em, source, eo, server);
				}
				em.getTransaction().commit();
			} catch (RuntimeException e) {
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
				getErrors().add(new JPADiagnostic(
						"Failed to save resource: " + e.getMessage(), getURI(), e));
				throw new IOException("Failed to save resource: " + getURI(), e);
			}
		}
	}

	/**
	 * INSERT or UPDATE — chosen explicitly instead of relying on {@code em.merge} which
	 * does not cope well with detached graphs that contain our AP-46 lazy-proxies: merge
	 * cascades into the proxies and tries to INSERT them as new entities.
	 * <p>
	 * Strategy: if the entity has an id and a row exists, copy the source's attribute
	 * and (containment/explicit non-proxy) reference state onto the managed existing
	 * entity; otherwise persist the source as new. Non-containment references whose
	 * value is still an {@link EObject#eIsProxy() unresolved proxy} are left alone —
	 * the existing row's FK already points where the proxy points.
	 */
	private void upsert(EntityManager em, EObject source, EObject original, Server server) {
		ClassDescriptor descriptor = nonNull(server)
				? server.getDescriptorForAlias(source.eClass().getName())
				: null;
		if (isNull(descriptor)) {
			em.merge(source);
			return;
		}
		EAttribute idAttr = source.eClass().getEIDAttribute();
		Object id = nonNull(idAttr) ? source.eGet(idAttr) : null;
		if (isNull(id) || isDefaultIdValue(id)) {
			sanitizeNonContainmentReferences(source, original, server, em);
			em.persist(source);
			return;
		}
		Object existing = em.find(descriptor.getJavaClass(), id);
		if (existing instanceof EObject existingEO) {
			copyStateInto(source, existingEO, server, em);
		} else {
			sanitizeNonContainmentReferences(source, original, server, em);
			em.persist(source);
		}
	}

	/**
	 * Replaces non-containment reference values that are plain (unmanaged) EObjects with
	 * JPA-managed handles via {@code em.getReference} before persisting. Without this,
	 * commit's cascade-persist scan encounters unmanaged {@code DynamicEObjectImpl}s
	 * ("not a known Entity type") and foreign keys of new entities are not written.
	 * <p>
	 * Single-valued bidirectional references are additionally recovered from the
	 * {@code original} object: the standard EMF copier ({@code toManagedEntity}) omits
	 * bidirectional references whose target is not part of the copied tree.
	 * Values without a usable id are left alone.
	 */
	@SuppressWarnings("unchecked")
	private static void sanitizeNonContainmentReferences(EObject source, EObject original, Server server,
			EntityManager em) {
		if (isNull(server)) {
			return;
		}
		for (EReference ref : source.eClass().getEAllReferences()) {
			if (!ref.isChangeable() || ref.isDerived() || ref.isTransient() || ref.isContainment()) {
				continue;
			}
			ClassDescriptor refDescriptor = server.getDescriptorForAlias(ref.getEReferenceType().getName());
			if (isNull(refDescriptor)) {
				continue;
			}
			if (ref.isMany()) {
				List<EObject> values = (List<EObject>) source.eGet(ref);
				for (int i = 0; i < values.size(); i++) {
					EObject managed = managedHandle(values.get(i), refDescriptor, server, em);
					if (nonNull(managed)) {
						values.set(i, managed);
					}
				}
			} else {
				Object value = ((InternalEObject) source).eGet(ref, false);
				if (isNull(value) && nonNull(ref.getEOpposite()) && nonNull(original) && original != source
						&& original.eClass() == source.eClass()) {
					// Recover the value the EMF copier dropped for bidirectional refs.
					value = ((InternalEObject) original).eGet(ref, false);
				}
				if (value instanceof EObject eo) {
					EObject managed = managedHandle(eo, refDescriptor, server, em);
					if (nonNull(managed)) {
						source.eSet(ref, managed);
					}
				}
			}
		}
	}

	/**
	 * Returns a JPA-managed handle for a plain (unmanaged) EObject with a persisted id,
	 * or {@code null} when the value is already managed or carries no usable id.
	 */
	private static EObject managedHandle(EObject value, ClassDescriptor refDescriptor, Server server,
			EntityManager em) {
		if (isNull(value) || value.eIsProxy() || nonNull(server.getDescriptor(value.getClass()))) {
			return null;
		}
		EAttribute idAttr = value.eClass().getEIDAttribute();
		Object id = nonNull(idAttr) ? value.eGet(idAttr) : null;
		if (isNull(id) || isDefaultIdValue(id)) {
			return null;
		}
		try {
			Object managed = em.getReference(refDescriptor.getJavaClass(), id);
			return managed instanceof EObject managedEO ? managedEO : null;
		} catch (RuntimeException e) {
			LOG.log(Level.FINE, "em.getReference failed while sanitizing reference to "
					+ value.eClass().getName(), e);
			return null;
		}
	}

	private static boolean isDefaultIdValue(Object id) {
		if (id instanceof Number n) {
			return n.longValue() == 0L;
		}
		if (id instanceof String s) {
			return s.isEmpty();
		}
		return false;
	}

	/**
	 * Mirrors attribute values and containment references from {@code source} onto
	 * {@code target}. Non-containment singular references are updated via
	 * {@link EntityManager#getReference(Class, Object)} so that {@code target} ends up
	 * with a JPA-managed reference (hollow proxy) — never a detached {@link EObject}
	 * that commit's cascade-register-new scan would try to INSERT.
	 * <p>
	 * A non-containment ref is only rewritten when the id pointed to by {@code source}
	 * differs from the id currently held by {@code target}; otherwise the existing
	 * managed state is left alone, avoiding spurious dirty-tracking.
	 */
	private static void copyStateInto(EObject source, EObject target, Server server, EntityManager em) {
		for (EAttribute attr : source.eClass().getEAllAttributes()) {
			if (!attr.isChangeable() || attr.isDerived() || attr.isTransient()) {
				continue;
			}
			Object srcValue = source.eGet(attr);
			Object tgtValue = target.eGet(attr);
			if (!Objects.equals(srcValue, tgtValue)) {
				target.eSet(attr, srcValue);
			}
		}
		for (EReference ref : source.eClass().getEAllReferences()) {
			if (!ref.isChangeable() || ref.isDerived() || ref.isTransient()) {
				continue;
			}
			if (ref.isMany()) {
				continue; // collections handled by EclipseLink's IndirectList
			}
			Object srcValue = ((InternalEObject) source).eGet(ref, false);
			if (ref.isContainment()) {
				target.eSet(ref, srcValue);
				continue;
			}
			// Non-containment singular ref: use em.getReference to avoid handing a
			// detached/proxy EObject to EclipseLink's cascade-register-new.
			copyNonContainmentRef(target, ref, srcValue, server, em);
		}
	}

	private static void copyNonContainmentRef(EObject target, EReference ref, Object srcValue,
			Server server, EntityManager em) {
		EAttribute refIdAttr = ref.getEReferenceType().getEIDAttribute();
		if (isNull(refIdAttr)) {
			target.eSet(ref, srcValue);
			return;
		}
		Object srcRefId = srcValue instanceof EObject eo ? eo.eGet(refIdAttr) : null;
		if (isNull(srcRefId)) {
			target.eSet(ref, null);
			return;
		}
		// Always route through em.getReference so the attribute slot holds a
		// JPA-managed reference — never a detached or lazy-proxy EObject that
		// commit's cascade-register-new scan would mistake for a new entity.
		ClassDescriptor refDesc = nonNull(server)
				? server.getDescriptorForAlias(ref.getEReferenceType().getName())
				: null;
		if (isNull(refDesc)) {
			target.eSet(ref, srcValue instanceof EObject eo ? eo : null);
			return;
		}
		try {
			Object managed = em.getReference(refDesc.getJavaClass(), srcRefId);
			if (managed instanceof EObject managedEO) {
				target.eSet(ref, managedEO);
			}
		} catch (RuntimeException e) {
			LOG.log(Level.FINE, "em.getReference failed for ref " + ref.getName(), e);
		}
	}

	private void applyCacheNewObjectsOption(EntityManager em, Map<?, ?> options) {
		Boolean cacheNew = Options.getCacheNewObjects(options);
		if (isNull(cacheNew)) {
			return;
		}
		try {
			UnitOfWork uow = em.unwrap(UnitOfWork.class);
			if (nonNull(uow)) {
				uow.setShouldNewObjectsBeCached(cacheNew);
			}
		} catch (RuntimeException e) {
			LOG.log(Level.FINE, "Unable to unwrap UnitOfWork to apply cache-new-objects option", e);
		}
	}

	@Override
	public void delete(Map<?, ?> options) throws IOException {
		getErrors().clear();
		getWarnings().clear();
		try (EntityManager em = emf.createEntityManager()) {
			em.getTransaction().begin();
			try {
				for (EObject eo : getContents()) {
					Object merged = em.merge(eo);
					em.remove(merged);
				}
				em.getTransaction().commit();
				getContents().clear();
			} catch (RuntimeException e) {
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
				getErrors().add(new JPADiagnostic(
						"Failed to delete resource: " + e.getMessage(), getURI(), e));
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
			getWarnings().add(new JPADiagnostic(
					"No descriptor for entity '" + entityName + "' — cannot resolve fragment "
					+ uriFragment, getURI()));
			return null;
		}
		try (EntityManager em = emf.createEntityManager()) {
			Object typedId;
			try {
				typedId = convertId(idValue, descriptor);
			} catch (NumberFormatException e) {
				getWarnings().add(new JPADiagnostic(
						"Cannot convert id '" + idValue + "' for fragment " + uriFragment
						+ ": " + e.getMessage(), getURI(), e));
				return null;
			}
			Object result = em.find(descriptor.getJavaClass(), typedId);
			if (result instanceof EObject resolved) {
				// Add the resolved object to this resource's contents so it has
				// an eResource() and can be found on subsequent accesses without
				// hitting the database again. This is the standard EMF pattern
				// for proxy resolution via ResourceSet → Resource → getEObject.
				if (isNull(resolved.eResource()) && !getContents().contains(resolved)) {
					getContents().add(resolved);
				}
				return resolved;
			}
			return null;
		} catch (RuntimeException e) {
			getErrors().add(new JPADiagnostic(
					"Failed to resolve fragment " + uriFragment + ": " + e.getMessage(),
					getURI(), e));
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.resource.StreamingResource#stream()
	 */
	@Override
	public Stream<EObject> stream() throws IOException {
		return stream(null);
	}

	/**
	 * Streams all entities of this resource's type through an EclipseLink
	 * {@link ScrollableCursor} — rows are materialised one by one from an open JDBC
	 * result set instead of loading the full result list. The underlying cursor and
	 * {@link EntityManager} stay open until the returned stream is closed.
	 */
	@Override
	public Stream<EObject> stream(Map<?, ?> options) throws IOException {
		String entityName = getEntityName();
		if (isNull(entityName)) {
			return Stream.empty();
		}
		ClassDescriptor descriptor = getDescriptor(entityName);
		if (isNull(descriptor)) {
			return Stream.empty();
		}
		String validatedAlias = descriptor.getAlias();
		EntityManager em = emf.createEntityManager();
		try {
			TypedQuery<?> query = em.createQuery(
					"SELECT e FROM " + validatedAlias + " e", descriptor.getJavaClass());
			query.setHint(QueryHints.SCROLLABLE_CURSOR, HintValues.TRUE);
			int pageSize = Options.getPageSize(options);
			if (pageSize > 0) {
				query.setHint(QueryHints.JDBC_FETCH_SIZE, pageSize);
			}
			ScrollableCursor cursor = (ScrollableCursor) query.unwrap(JpaQuery.class).getResultCursor();
			Spliterator<Object> rows = Spliterators.spliteratorUnknownSize(new Iterator<Object>() {
				@Override
				public boolean hasNext() {
					return cursor.hasNext();
				}

				@Override
				public Object next() {
					return cursor.next();
				}
			}, Spliterator.ORDERED | Spliterator.NONNULL);
			return StreamSupport.stream(rows, false)
					.filter(EObject.class::isInstance)
					.map(EObject.class::cast)
					.onClose(() -> {
						cursor.close();
						em.close();
					});
		} catch (RuntimeException e) {
			em.close();
			throw new IOException("Failed to open stream on entity '" + entityName + "'", e);
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
			getWarnings().add(new JPADiagnostic(
					"Resource URI has no entity segment — count is 0", getURI()));
			return 0;
		}
		ClassDescriptor descriptor = getDescriptor(entityName);
		if (isNull(descriptor)) {
			getWarnings().add(new JPADiagnostic(
					"No descriptor for entity '" + entityName + "' — count is 0", getURI()));
			return 0;
		}
		// Use the validated alias from the descriptor to prevent JPQL injection
		String validatedAlias = descriptor.getAlias();
		try (EntityManager em = emf.createEntityManager()) {
			return em.createQuery("SELECT COUNT(e) FROM " + validatedAlias + " e", Long.class)
					.getSingleResult();
		} catch (RuntimeException e) {
			getErrors().add(new JPADiagnostic(
					"Failed to count entity '" + entityName + "': " + e.getMessage(), getURI(), e));
			throw new IOException("Failed to count entity '" + entityName + "' in " + getURI(), e);
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
		return nonNull(server) ? server.getDescriptorForAlias(entityName) : null;
	}

	/**
	 * Converts an EObject to a managed EclipseLink entity if necessary.
	 * If the object is already of the correct dynamic class (i.e. EclipseLink knows its Java class),
	 * it is returned as-is. Otherwise, a new entity is created via the descriptor's instantiation
	 * policy and all features are copied from the source using {@link ECopier}.
	 * This enables persisting plain {@code DynamicEObjectImpl} objects loaded from XMI or
	 * created outside EclipseLink.
	 */
	private EObject toManagedEntity(EObject source, Server server,
			Function<EObject, EObject> entityFactory) {
		// Fast path: EclipseLink already knows this object's class
		ClassDescriptor descriptor = server.getDescriptor(source.getClass());
		if (nonNull(descriptor)) {
			return source;
		}
		// Slow path: look up by EClass alias and convert
		descriptor = server.getDescriptorForAlias(source.eClass().getName());
		if (isNull(descriptor)) {
			String msg = "No descriptor found for EClass '" + source.eClass().getName()
					+ "' — passing object as-is";
			LOG.log(Level.WARNING, msg);
			getWarnings().add(new JPADiagnostic(msg, getURI()));
			return source;
		}
		EObject target = EDynamicHelper.createInstance(descriptor);
		ECopier copier = new ECopier(target, null);
		copier.setCopyContainments(true);
		copier.setCopyFunction(entityFactory);
		EObject result = copier.copy(source);
		copier.copyReferences();
		return result;
	}

	/**
	 * Converts a string ID value to the appropriate type for the descriptor's primary key.
	 * Uses the descriptor's primary key field type to determine the correct conversion.
	 */
	private Object convertId(String idValue, ClassDescriptor descriptor) {
		Class<?> pkType = descriptor.getPrimaryKeyFields().stream()
				.findFirst()
				.map(field -> field.getType())
				.orElse(null);
		if (isNull(pkType)) {
			return idValue;
		}
		if (Integer.class.isAssignableFrom(pkType) || int.class.equals(pkType)) {
			return Integer.valueOf(idValue);
		}
		if (Long.class.isAssignableFrom(pkType) || long.class.equals(pkType)) {
			return Long.valueOf(idValue);
		}
		// Default: return as String (UUID, etc.)
		return idValue;
	}
}
