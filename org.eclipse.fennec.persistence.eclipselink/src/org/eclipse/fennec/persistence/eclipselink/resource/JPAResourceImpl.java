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
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import java.util.Arrays;
import java.util.Collections;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.persistence.Options;
import org.eclipse.fennec.persistence.diagnostic.PersistenceDiagnostic;
import org.eclipse.fennec.persistence.eclipselink.descriptors.EClassDescriptor;
import org.eclipse.fennec.persistence.eclipselink.query.JpaQueries;
import org.eclipse.fennec.persistence.eclipselink.query.JpaQueryPlan;
import org.eclipse.fennec.persistence.eclipselink.query.JpaQueryProcessor;
import org.eclipse.fennec.persistence.orm.helper.EORMHelper;
import org.eclipse.fennec.persistence.query.QueryException;
import org.eclipse.fennec.persistence.query.api.QueryProcessor;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.eclipse.fennec.persistence.query.api.QueryResultRow;
import org.eclipse.fennec.persistence.query.api.QueryShape;
import org.eclipse.fennec.model.command.Command;
import org.eclipse.fennec.model.command.DeleteCommand;
import org.eclipse.fennec.model.command.InsertCommand;
import org.eclipse.fennec.model.command.UpdateCommand;
import org.eclipse.fennec.persistence.query.api.CommandResource;
import org.eclipse.fennec.persistence.query.api.QueryableResource;
import org.eclipse.fennec.persistence.query.support.ChangeTemplates;
import org.eclipse.fennec.persistence.query.support.CommandTransaction;
import org.eclipse.fennec.persistence.query.support.PersistedQueries;
import org.eclipse.fennec.persistence.query.support.ReferenceResolver;
import org.eclipse.fennec.persistence.query.support.QueryResultRows;
import org.eclipse.fennec.persistence.query.support.QueryResults;
import org.eclipse.fennec.persistence.eclipselink.copying.ECopier;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicHelper;
import org.eclipse.fennec.persistence.eclipselink.dynamic.EDynamicType;
import org.eclipse.fennec.persistence.helper.CompositeIds;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAUnit;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAUnit.Lease;
import org.eclipse.fennec.persistence.resource.PersistenceResource;
import org.eclipse.fennec.persistence.resource.StreamingResource;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
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
public class JPAResourceImpl extends ResourceImpl implements PersistenceResource, StreamingResource, QueryableResource, CommandResource {

	private static final Logger LOG = Logger.getLogger(JPAResourceImpl.class.getName());

	/** Diagnostic source of this resource layer (issue #19): the bundle namespace. */
	static final String DIAGNOSTIC_SOURCE = "org.eclipse.fennec.persistence.eclipselink";

	private final JPAUnit unit;
	private volatile QueryProcessor queryProcessor = new JpaQueryProcessor();

	/** Options captured by {@link #load(Map)} for the deferred full population. */
	private Map<?, ?> loadOptions;
	/**
	 * {@code true} once {@link #load(Map)} was called — the trigger for deferred population.
	 * Deliberately distinct from EMF's {@code isLoaded}: adding a single keyed-resolved
	 * object to the contents (see {@link #getEObject(String)}) flips {@code isLoaded} via
	 * {@code ContentsEList.loaded()}, which must <em>not</em> cause the whole table to be
	 * loaded on a later {@link #getContents()}.
	 */
	private boolean loadRequested;
	/** {@code true} once the full {@code SELECT e FROM Entity e} population has run. */
	private boolean contentsPopulated;
	/** Re-entrancy guard so internal contents access during population does not recurse. */
	private boolean populating;

	/**
	 * Creates a resource backed by a {@link JPAUnit} — the narrow capability that hands out
	 * fresh {@link EntityManager}s and the EclipseLink server session while keeping the
	 * heavyweight factory private and lazily managed (issue #20).
	 */
	public JPAResourceImpl(URI uri, JPAUnit unit) {
		super(uri);
		this.unit = requireNonNull(unit, "JPAUnit is required");
	}

	/**
	 * Convenience for the non-OSGi / test path: adapts a caller-owned
	 * {@link EntityManagerFactory} as a {@link JPAUnit}.
	 */
	public JPAResourceImpl(URI uri, EntityManagerFactory emf) {
		this(uri, JPAUnit.of(emf));
	}

	/**
	 * Lazy load: marks the resource loaded and remembers the options, but does
	 * <em>not</em> run the full {@code SELECT e FROM Entity e}. The full population is
	 * deferred to the first {@link #getContents()} iteration.
	 * <p>
	 * This keeps demand-load-driven proxy resolution cheap: EMF resolves a proxy via
	 * {@code ResourceSet.getEObject(uri, true)} → {@code getResource(trimFragment, true)}
	 * → {@code demandLoad} → {@code load()}. With eager loading that step materialised the
	 * whole target table before the fragment (which already carries the target id) was ever
	 * looked at. Now {@code load()} is a no-op and the keyed {@code em.find} in
	 * {@link #getEObject(String)} is the only DB round-trip. See issue #17.
	 */
	@Override
	public void load(Map<?, ?> options) throws IOException {
		if (isLoaded) {
			return;
		}
		this.loadOptions = options;
		this.loadRequested = true;
		isLoaded = true;
	}

	/**
	 * Returns the resource contents, running the deferred full population on first access
	 * of a loaded (but not yet populated) resource. Internal callers that must not trigger
	 * the full table load — {@link #getEObject(String)} caching, {@link #populateContents}
	 * itself — use {@code super.getContents()} directly to bypass this hook.
	 */
	@Override
	public EList<EObject> getContents() {
		populateIfNeeded();
		return super.getContents();
	}

	/**
	 * Runs the deferred full population exactly once for a loaded resource. Guarded against
	 * re-entrancy so that {@code super.getContents()} calls made from within
	 * {@link #populateContents} do not recurse. A load failure surfaces as a
	 * {@link WrappedException} because {@link #getContents()} cannot throw a checked
	 * {@link IOException}; the failure is also recorded in {@link #getErrors()}.
	 */
	private void populateIfNeeded() {
		if (!loadRequested || contentsPopulated || populating) {
			return;
		}
		populating = true;
		try {
			populateContents(loadOptions);
			contentsPopulated = true;
		} catch (IOException e) {
			throw new WrappedException(e);
		} finally {
			populating = false;
		}
	}

	/**
	 * Full population: {@code SELECT e FROM Entity e}, materialising every row of the
	 * target table into this resource's contents. Objects already present (e.g. resolved
	 * earlier by {@link #getEObject(String)}) are kept — incoming rows carrying an EMF id
	 * that is already present are skipped so identity is preserved and no duplicates are
	 * added.
	 */
	private void populateContents(Map<?, ?> options) throws IOException {
		getErrors().clear();
		getWarnings().clear();
		String entityName = getEntityName();
		if (isNull(entityName)) {
			getWarnings().add(PersistenceDiagnostic.warning(DIAGNOSTIC_SOURCE, 
					"Resource URI has no entity segment — nothing to load", getURI()));
			return;
		}
		try (Lease lease = leaseChecked()) {
			ClassDescriptor descriptor = getDescriptor(entityName);
			if (isNull(descriptor)) {
				getWarnings().add(PersistenceDiagnostic.warning(DIAGNOSTIC_SOURCE, 
						"No descriptor found for entity '" + entityName + "'", getURI()));
				return;
			}
			// Use the validated alias from the descriptor to prevent JPQL injection
			String validatedAlias = descriptor.getAlias();
			int pageSize = Options.getPageSize(options);
			try (EntityManager em = lease.createEntityManager()) {
				TypedQuery<?> query = em.createQuery(
						"SELECT e FROM " + validatedAlias + " e", descriptor.getJavaClass());
				if (pageSize > 0) {
					loadPaginated(query, pageSize);
				} else {
					addToContents(query.getResultList());
				}
			} catch (RuntimeException e) {
				getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, 
						"Failed to load entity '" + entityName + "': " + e.getMessage(), getURI(), e));
				throw new IOException("Failed to load resource: " + getURI(), e);
			}
		}
	}

	/**
	 * Opens the per-operation lease on the unit — the single point where an unavailable
	 * persistence unit surfaces. Failures are recorded as an error diagnostic and rethrown
	 * as {@link IOException}, so consumers always get a clear "unit not available" signal
	 * instead of a bare runtime exception (no silent fallback — see issue #20).
	 */
	private Lease leaseChecked() throws IOException {
		try {
			return unit.lease();
		} catch (RuntimeException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, 
					"Persistence unit not available: " + e.getMessage(), getURI(), e));
			throw new IOException("Persistence unit not available for " + getURI(), e);
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

	/**
	 * Appends query results to the raw contents list, skipping any object whose EMF id is
	 * already present (added by an earlier keyed {@link #getEObject(String)} resolution).
	 * Uses {@code super.getContents()} to avoid re-triggering {@link #populateIfNeeded()}.
	 */
	private void addToContents(List<?> results) {
		EList<EObject> raw = super.getContents();
		Set<String> existingIds = new HashSet<>();
		for (EObject eo : raw) {
			// composite-aware dedup key (issue #109): getID alone would collide two
			// rows that differ only in a later id component
			String id = CompositeIds.fragment(eo);
			if (nonNull(id)) {
				existingIds.add(id);
			}
		}
		for (Object obj : results) {
			if (obj instanceof EObject eo) {
				String id = CompositeIds.fragment(eo);
				if (nonNull(id) && !existingIds.add(id)) {
					continue;
				}
				raw.add(eo);
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
		try (Lease lease = leaseChecked()) {
			saveWithLease(lease, options);
		}
	}

	private void saveWithLease(Lease lease, Map<?, ?> options) throws IOException {
		Server server = serverOf(lease);
		// Pre-build the entity factory function once for all objects (avoids lambda allocation per object)
		Function<EObject, EObject> entityFactory = nonNull(server)
				? src -> {
					ClassDescriptor desc = server.getDescriptorForAlias(src.eClass().getName());
					return nonNull(desc) ? EDynamicHelper.createInstance(desc) : null;
				}
				: null;
		try (EntityManager em = lease.createEntityManager()) {
			em.getTransaction().begin();
			applyCacheNewObjectsOption(em, options);
			try {
				List<EObject[]> managedPairs = new ArrayList<>();
				for (EObject eo : getContents()) {
					EObject source = nonNull(server) ? toManagedEntity(eo, server, entityFactory) : eo;
					upsert(em, source, eo, server);
					if (source != eo) {
						managedPairs.add(new EObject[] { eo, source });
					}
				}
				em.getTransaction().commit();
				writeBackGeneratedIds(managedPairs);
			} catch (RuntimeException e) {
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
				getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, 
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
		Object id = findKey(source);
		if (isNull(id) || (!(id instanceof Object[]) && isDefaultIdValue(id))) {
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
	 * The {@code em.find}/{@code em.getReference} key of an object: the scalar id value,
	 * or an {@code Object[]} in id-attribute (= PK-field) order for composite-id types
	 * (issue #109); {@code null} when any component is unset.
	 */
	private static Object findKey(EObject source) {
		List<EAttribute> ids = CompositeIds.idAttributes(source.eClass());
		if (ids.isEmpty()) {
			return null;
		}
		if (ids.size() == 1) {
			return source.eGet(ids.get(0));
		}
		Object[] key = new Object[ids.size()];
		for (int i = 0; i < ids.size(); i++) {
			key[i] = source.eGet(ids.get(i));
			if (isNull(key[i])) {
				return null;
			}
		}
		return key;
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
		Object id = findKey(value);
		if (isNull(id) || (!(id instanceof Object[]) && isDefaultIdValue(id))) {
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

	/**
	 * Mirrors generated ids back onto the caller's EObjects: when a plain EMF object was
	 * converted to a managed entity for saving ({@code toManagedEntity}), a sequence-
	 * generated id lands only on the managed copy — the resource contract (parity with
	 * the Mongo backend) is that the saved EObject carries its id after {@code save}.
	 */
	private static void writeBackGeneratedIds(List<EObject[]> managedPairs) {
		for (EObject[] pair : managedPairs) {
			EObject original = pair[0];
			EObject managed = pair[1];
			EAttribute idAttr = original.eClass().getEIDAttribute();
			if (isNull(idAttr)) {
				continue;
			}
			Object originalId = original.eGet(idAttr);
			Object managedId = managed.eGet(idAttr);
			if ((isNull(originalId) || isDefaultIdValue(originalId))
					&& nonNull(managedId) && !isDefaultIdValue(managedId)) {
				original.eSet(idAttr, managedId);
			}
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
		try (Lease lease = leaseChecked(); EntityManager em = lease.createEntityManager()) {
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
				getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, 
						"Failed to delete resource: " + e.getMessage(), getURI(), e));
				throw new IOException("Failed to delete resource: " + getURI(), e);
			}
		}
	}

	@Override
	protected void doUnload() {
		isLoaded = false;
		loadRequested = false;
		contentsPopulated = false;
		loadOptions = null;
		// Raw list access — a resource being unloaded must not first re-populate.
		super.getContents().clear();
	}

	/**
	 * The URI fragment of a contained object is its EMF id — this is what other
	 * resources (XMI hrefs, codec-based backends like Mongo) embed as the reference
	 * target, making written cross-backend references id-based
	 * ({@code jpa://<pu>/<Entity>#<id>}).
	 */
	@Override
	public String getURIFragment(EObject eObject) {
		// composite-id types use the k1=v1,k2=v2 shape (issue #109) — EcoreUtil.getID
		// would return only the first component and could not round-trip
		String id = CompositeIds.fragment(eObject);
		return nonNull(id) ? id : super.getURIFragment(eObject);
	}

	/**
	 * Resolves proxy fragments. Two fragment shapes are supported:
	 * <ul>
	 * <li>{@code //refName/idAttr/idValue} — the persistence proxy format</li>
	 * <li>{@code <idValue>} — a plain id (written by {@link #getURIFragment}, e.g. in
	 *     cross-backend references from Mongo or XMI documents)</li>
	 * </ul>
	 * Path fragments ({@code /0}, …) are delegated to the EMF default.
	 */
	@Override
	public EObject getEObject(String uriFragment) {
		if (isNull(uriFragment) || uriFragment.isEmpty()) {
			return super.getEObject(uriFragment);
		}
		String idValue;
		if (uriFragment.startsWith("//")) {
			// Fragment format: //refName/idAttrName/idValue
			String[] parts = uriFragment.substring(2).split("/");
			if (parts.length < 3) {
				return super.getEObject(uriFragment);
			}
			idValue = parts[2];
		} else if (uriFragment.startsWith("/")) {
			// Path-based fragment — EMF default semantics
			return super.getEObject(uriFragment);
		} else {
			idValue = uriFragment;
		}
		String entityName = getEntityName();
		try (Lease lease = unit.lease()) {
			ClassDescriptor descriptor = getDescriptor(entityName);
			if (isNull(descriptor)) {
				getWarnings().add(PersistenceDiagnostic.warning(DIAGNOSTIC_SOURCE, 
						"No descriptor for entity '" + entityName + "' — cannot resolve fragment "
						+ uriFragment, getURI()));
				return null;
			}
			Object typedId;
			try {
				typedId = convertId(idValue, descriptor);
			} catch (NumberFormatException e) {
				getWarnings().add(PersistenceDiagnostic.warning(DIAGNOSTIC_SOURCE, 
						"Cannot convert id '" + idValue + "' for fragment " + uriFragment
						+ ": " + e.getMessage(), getURI(), e));
				return null;
			}
			EntityManager em = lease.createEntityManager();
			try {
				return findAndCache(em, descriptor, typedId);
			} finally {
				em.close();
			}
		} catch (RuntimeException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, 
					"Failed to resolve fragment " + uriFragment + ": " + e.getMessage(),
					getURI(), e));
			return null;
		}
	}

	/**
	 * Keyed {@code em.find} plus the standard EMF caching pattern: the resolved object is
	 * attached to this resource's <em>raw</em> contents so it has an {@code eResource()}
	 * and later accesses need no DB round-trip.
	 */
	private EObject findAndCache(EntityManager em, ClassDescriptor descriptor, Object typedId) {
		Object result = em.find(descriptor.getJavaClass(), typedId);
		if (result instanceof EObject resolved) {
			// Add the resolved object to this resource's contents so it has
			// an eResource() and can be found on subsequent accesses without
			// hitting the database again. This is the standard EMF pattern
			// for proxy resolution via ResourceSet → Resource → getEObject.
			// Use the raw contents list (super.getContents()) so caching a
			// single keyed resolution never triggers the deferred full-table
			// population — that is the whole point of the lazy resource (#17).
			EList<EObject> raw = super.getContents();
			if (isNull(resolved.eResource()) && !raw.contains(resolved)) {
				raw.add(resolved);
			}
			return resolved;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.persistence.resource.StreamingResource#stream()
	 */
	// -------------------------------------------------------------- commands

	@Override
	public long execute(Command command) throws IOException {
		requireNonNull(command, "command must not be null");
		if (command instanceof InsertCommand insert) {
			return executeInsert(insert);
		}
		if (command instanceof DeleteCommand delete) {
			return executeDelete(delete);
		}
		if (command instanceof UpdateCommand update) {
			return executeUpdate(update);
		}
		throw new IOException("Unsupported command " + command.eClass().getName());
	}

	/** The open command bracket (issue #108); resources are single-threaded per EMF semantics. */
	private JpaCommandTransaction activeTransaction;

	@Override
	public CommandTransaction begin() throws IOException {
		if (nonNull(activeTransaction)) {
			throw new IOException("A command transaction is already open on this resource"
					+ " — commit or close it before opening another");
		}
		Lease lease = leaseChecked();
		try {
			EntityManager em = lease.createEntityManager();
			em.getTransaction().begin();
			activeTransaction = new JpaCommandTransaction(lease, em);
			return activeTransaction;
		} catch (RuntimeException e) {
			lease.close();
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE,
					"Cannot open command transaction: " + e.getMessage(), getURI(), e));
			throw new IOException("Cannot open command transaction: " + e.getMessage(), e);
		}
	}

	/**
	 * The JPA command bracket (issue #108): one {@code EntityTransaction} spans every
	 * command executed until commit/rollback — the OData {@code $batch} atomicity-group
	 * contract. Types whose references were patched are remembered for the shared-cache
	 * identity-map drop (issue #107), which must happen only on commit.
	 */
	private final class JpaCommandTransaction implements CommandTransaction {

		private final Lease lease;
		private final EntityManager em;
		private final Set<Class<?>> referencePatchedTypes = new HashSet<>();
		private boolean closed;

		private JpaCommandTransaction(Lease lease, EntityManager em) {
			this.lease = lease;
			this.em = em;
		}

		@Override
		public void commit() throws IOException {
			if (closed) {
				throw new IOException("The command transaction is already closed");
			}
			try {
				em.getTransaction().commit();
				for (Class<?> type : referencePatchedTypes) {
					// see executeUpdate: collection merges accumulate, removals need a
					// fresh build from the database (issue #107)
					getServer().getIdentityMapAccessor().initializeIdentityMap(type);
				}
			} catch (RuntimeException e) {
				rollbackQuietly();
				getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE,
						"Command transaction commit failed: " + e.getMessage(), getURI(), e));
				throw new IOException("Command transaction commit failed: " + e.getMessage(), e);
			} finally {
				cleanup();
			}
		}

		@Override
		public void rollback() {
			if (closed) {
				return;
			}
			rollbackQuietly();
			cleanup();
		}

		@Override
		public void close() {
			if (!closed) {
				rollback();
			}
		}

		private void rollbackQuietly() {
			try {
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
			} catch (RuntimeException e) {
				LOG.log(Level.FINE, "Command transaction rollback failed", e);
			}
		}

		private void cleanup() {
			closed = true;
			try {
				em.close();
			} catch (RuntimeException e) {
				LOG.log(Level.FINE, "Closing the command transaction EntityManager failed", e);
			}
			lease.close();
			activeTransaction = null;
		}
	}

	/**
	 * Insert = the resource's save semantics over copies of the contained payload.
	 * Non-containment references to EXISTING targets bind by id (issue #107): verified
	 * via keyed find, rebound on the copies — including the bidirectional references
	 * the EMF copier drops; unknown or id-less targets refuse.
	 */
	private long executeInsert(InsertCommand insert) throws IOException {
		EcoreUtil.Copier copier = new EcoreUtil.Copier();
		List<EObject> copies = new ArrayList<>(copier.copyAll(insert.getObjects()));
		copier.copyReferences();
		if (nonNull(activeTransaction)) {
			// bracketed insert (issue #108): persist through the bracket's EntityManager
			EntityManager em = activeTransaction.em;
			try {
				ChangeTemplates.bindInsertReferences(copier, insertBindingResolver(em));
				Server server = serverOf(activeTransaction.lease);
				Function<EObject, EObject> entityFactory = entityFactory(server);
				for (EObject copy : copies) {
					EObject source = nonNull(server) ? toManagedEntity(copy, server, entityFactory) : copy;
					upsert(em, source, copy, server);
				}
				return copies.size();
			} catch (QueryException | RuntimeException e) {
				getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, "Insert rejected: " + e.getMessage(), getURI(), e));
				throw new IOException("Insert rejected: " + e.getMessage(), e);
			}
		}
		try (Lease lease = leaseChecked(); EntityManager em = lease.createEntityManager()) {
			ChangeTemplates.bindInsertReferences(copier, insertBindingResolver(em));
		} catch (QueryException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, "Insert rejected: " + e.getMessage(), getURI(), e));
			throw new IOException("Insert rejected: " + e.getMessage(), e);
		}
		getContents().addAll(copies);
		try {
			save(null);
		} finally {
			getContents().clear();
		}
		return copies.size();
	}

	/** Delete = selector-scoped bulk DELETE (concept §14: Delete = query selector). */
	private long executeDelete(DeleteCommand delete) throws IOException {
		JpaQueryPlan plan;
		try {
			guardPlainSelector(delete.getSelector());
			plan = JpaQueries.translate(queryProcessor, delete.getSelector(),
					delete.getSelector().getFrom(), null, null);
		} catch (QueryException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, "Delete selector rejected: " + e.getMessage(), getURI(), e));
			throw new IOException("Delete selector rejected: " + e.getMessage(), e);
		}
		if (nonNull(activeTransaction)) {
			try {
				return deleteCore(plan, activeTransaction.em);
			} catch (RuntimeException e) {
				getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, "Delete failed: " + e.getMessage(), getURI(), e));
				throw new IOException("Delete failed for selector on '" + plan.jpql() + "'", e);
			}
		}
		try (Lease lease = leaseChecked(); EntityManager em = lease.createEntityManager()) {
			em.getTransaction().begin();
			try {
				long deleted = deleteCore(plan, em);
				em.getTransaction().commit();
				return deleted;
			} catch (RuntimeException e) {
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
				getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, "Delete failed: " + e.getMessage(), getURI(), e));
				throw new IOException("Delete failed for selector on '" + plan.jpql() + "'", e);
			}
		}
	}

	/**
	 * Loads the matches and removes children-first: a JPQL bulk DELETE bypasses cascade
	 * semantics and trips containment FK constraints — the entities are EObjects, so the
	 * containment tree is generically walkable.
	 */
	private long deleteCore(JpaQueryPlan plan, EntityManager em) {
		jakarta.persistence.Query select = em.createQuery(plan.jpql());
		plan.parameters().forEach(select::setParameter);
		List<?> matches = select.getResultList();
		for (Object match : matches) {
			if (match instanceof EObject eObject) {
				removeChildrenFirst(em, eObject);
			}
		}
		return matches.size();
	}

	/** Update = selector + ChangeSet template per match (concept §14, patch-apply engine §18.1). */
	private long executeUpdate(UpdateCommand update) throws IOException {
		JpaQueryPlan plan;
		try {
			guardPlainSelector(update.getSelector());
			ChangeTemplates.validate(update.getTemplate(), update.getSelector().getFrom());
			plan = JpaQueries.translate(queryProcessor, update.getSelector(),
					update.getSelector().getFrom(), null, null);
		} catch (QueryException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, "Update rejected: " + e.getMessage(), getURI(), e));
			throw new IOException("Update rejected: " + e.getMessage(), e);
		}
		if (nonNull(activeTransaction)) {
			try {
				// the identity-map drop belongs to the bracket's COMMIT (issue #108)
				return updateCore(update, plan, activeTransaction.em,
						activeTransaction.referencePatchedTypes::add);
			} catch (QueryException | RuntimeException e) {
				getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, "Update failed: " + e.getMessage(), getURI(), e));
				throw new IOException("Update failed for selector on '" + plan.jpql() + "': "
						+ e.getMessage(), e);
			}
		}
		try (Lease lease = leaseChecked(); EntityManager em = lease.createEntityManager()) {
			em.getTransaction().begin();
			try {
				Set<Class<?>> patchedTypes = new HashSet<>();
				long applied = updateCore(update, plan, em, patchedTypes::add);
				em.getTransaction().commit();
				for (Class<?> type : patchedTypes) {
					// the accessor's collection writes accumulate by design (AP-47 proxy
					// rebuild paths), so member REMOVALS can never reach the shared-cache
					// original — and invalidation alone refreshes IN PLACE through the
					// same accumulating accessor. Drop the cached instances entirely; the
					// next read builds fresh objects from the database.
					getServer().getIdentityMapAccessor().initializeIdentityMap(type);
				}
				return applied;
			} catch (QueryException | RuntimeException e) {
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
				getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, "Update failed: " + e.getMessage(), getURI(), e));
				throw new IOException("Update failed for selector on '" + plan.jpql() + "': "
						+ e.getMessage(), e);
			}
		}
	}

	/**
	 * Loads the matches and patches them managed — the template addresses features
	 * generically, so EclipseLink's change detection persists the delta on commit.
	 * Reference-patched entity classes are reported for the post-commit identity-map
	 * drop (issue #107).
	 */
	private long updateCore(UpdateCommand update, JpaQueryPlan plan, EntityManager em,
			java.util.function.Consumer<Class<?>> referencePatched) throws QueryException {
		jakarta.persistence.Query select = em.createQuery(plan.jpql());
		plan.parameters().forEach(select::setParameter);
		List<?> matches = select.getResultList();
		ReferenceResolver resolver = referenceResolver(em);
		long applied = 0;
		for (Object match : matches) {
			if (match instanceof EObject eObject) {
				ChangeTemplates.apply(update.getTemplate(), eObject, resolver);
				applied++;
			}
		}
		if (applied > 0 && hasReferenceEntries(update.getTemplate(), update.getSelector().getFrom())) {
			referencePatched.accept(matches.get(0).getClass());
		}
		return applied;
	}

	/** The per-EClass entity factory of the save pipeline, reused by bracketed inserts. */
	private static Function<EObject, EObject> entityFactory(Server server) {
		return nonNull(server)
				? src -> {
					ClassDescriptor desc = server.getDescriptorForAlias(src.eClass().getName());
					return nonNull(desc) ? EDynamicHelper.createInstance(desc) : null;
				}
				: null;
	}

	/** Whether any template entry addresses an {@link EReference} of the type (issue #107). */
	private static boolean hasReferenceEntries(org.eclipse.fennec.model.stream.ChangeSet template, EClass type) {
		return template.getEntries().stream()
				.anyMatch(entry -> type.getEStructuralFeature(entry.getFeatureId()) instanceof EReference);
	}

	/**
	 * The insert-binding variant of the resolver (issue #107): verifies existence via
	 * the keyed find, but binds a <em>plain</em> id stub instead of the found entity —
	 * a detached EclipseLink instance would be class-detected as "already managed" by
	 * the save pipeline's sanitizing pass and cascade-inserted as a duplicate, while a
	 * plain stub with a usable id takes the {@code em.getReference} FK path.
	 */
	private ReferenceResolver insertBindingResolver(EntityManager em) {
		ReferenceResolver managed = referenceResolver(em);
		return (reference, id) -> {
			if (isNull(managed.resolve(reference, id))) {
				return null;
			}
			EObject stub = EcoreUtil.create(reference.getEReferenceType());
			CompositeIds.setId(stub, id);
			return stub;
		};
	}

	/**
	 * The JPA {@link ReferenceResolver} (issue #107): a keyed find in the command's own
	 * {@link EntityManager}, so the bound target is managed and EclipseLink's change
	 * detection persists the FK on commit.
	 */
	private ReferenceResolver referenceResolver(EntityManager em) {
		return (reference, id) -> {
			String targetType = reference.getEReferenceType().getName();
			ClassDescriptor descriptor = getDescriptor(targetType);
			if (isNull(descriptor)) {
				throw new QueryException("Reference target type '" + targetType
						+ "' is not a known entity of this unit");
			}
			Object found = em.find(descriptor.getJavaClass(), convertId(id, descriptor));
			return found instanceof EObject eObject ? eObject : null;
		};
	}

	private void removeChildrenFirst(EntityManager em, EObject object) {
		for (EObject child : List.copyOf(object.eContents())) {
			removeChildrenFirst(em, child);
		}
		em.remove(object);
	}

	/** Command selectors are plain filters — everything shape-changing is refused. */
	private static void guardPlainSelector(Query selector) throws QueryException {
		if (!selector.getSelect().isEmpty() || selector.getApply() != null || !selector.getOrderBy().isEmpty()
				|| selector.getTop() > 0 || selector.getSkip() > 0 || selector.isDistinct()
				|| selector.isCountOnly() || !selector.getExpand().isEmpty()) {
			throw new QueryException(
					"Command selectors must be plain filters — projection/aggregation/ordering/paging are not allowed");
		}
	}

	// -------------------------------------------------------------- querying

	/**
	 * Overrides the {@link QueryProcessor} used by {@link #query(Query, Map, Map)} —
	 * intended for OSGi wiring through the factory; defaults to a local
	 * {@link JpaQueryProcessor} instance (processors are stateless).
	 */
	public void setQueryProcessor(QueryProcessor queryProcessor) {
		this.queryProcessor = requireNonNull(queryProcessor, "queryProcessor must not be null");
	}

	@Override
	public QueryResult query(Query query) throws IOException {
		return query(query, null, null);
	}

	@Override
	public QueryResult query(String name, Map<String, Object> parameters, Map<?, ?> options) throws IOException {
		requireNonNull(name, "query name must not be null");
		return query(loadNamedQuery(name), parameters, options);
	}

	@Override
	public QueryResult query(Query query, Map<String, Object> parameters, Map<?, ?> options) throws IOException {
		requireNonNull(query, "query must not be null");
		String catalogName;
		try {
			catalogName = PersistedQueries.catalogName(query);
		} catch (QueryException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, "Query rejected: " + e.getMessage(), getURI(), e));
			throw new IOException("Query rejected: " + e.getMessage(), e);
		}
		if (catalogName != null) {
			saveNamedQuery(catalogName, query);
		}
		String entityName = getEntityName();
		if (isNull(entityName)) {
			throw new IOException("Resource URI has no entity segment — cannot query: " + getURI());
		}
		Lease lease = leaseChecked();
		ClassDescriptor descriptor;
		try {
			descriptor = getDescriptor(entityName);
		} catch (RuntimeException e) {
			lease.close();
			throw new IOException("Failed to resolve entity '" + entityName + "'", e);
		}
		if (isNull(descriptor)) {
			lease.close();
			throw new IOException("No descriptor found for entity '" + entityName + "' in " + getURI());
		}
		EClass eClass = descriptor instanceof EClassDescriptor ecd ? EORMHelper.getEClass(ecd.getEntity()) : null;
		if (isNull(eClass)) {
			lease.close();
			throw new IOException("No EClass known for entity '" + entityName + "' in " + getURI());
		}
		JpaQueryPlan plan;
		try {
			plan = JpaQueries.translate(queryProcessor, query, eClass, parameters, options);
		} catch (QueryException e) {
			lease.close();
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, "Query rejected: " + e.getMessage(), getURI(), e));
			throw new IOException("Query rejected for entity '" + entityName + "': " + e.getMessage(), e);
		}
		if (plan.shape() == QueryShape.COUNT) {
			return executeCount(plan, lease, entityName);
		}
		return executeCursor(plan, lease, entityName);
	}

	// ------------------------------------------------------- persisted queries

	/** The query catalog table (concept.md §14 saveQuery): name → XMI payload. */
	static final String QUERY_CATALOG_TABLE = "FENNEC_QUERIES";

	private void saveNamedQuery(String name, Query query) throws IOException {
		String xmi;
		try {
			xmi = PersistedQueries.toXmi(query);
		} catch (QueryException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, "Cannot persist query '" + name + "': " + e.getMessage(), getURI(), e));
			throw new IOException("Cannot persist query '" + name + "': " + e.getMessage(), e);
		}
		try (Lease lease = leaseChecked(); EntityManager em = lease.createEntityManager()) {
			em.getTransaction().begin();
			try {
				ensureCatalogTable(em);
				em.createNativeQuery("DELETE FROM " + QUERY_CATALOG_TABLE + " WHERE NAME = ?1")
						.setParameter(1, name).executeUpdate();
				em.createNativeQuery("INSERT INTO " + QUERY_CATALOG_TABLE + " (NAME, XMI) VALUES (?1, ?2)")
						.setParameter(1, name).setParameter(2, xmi).executeUpdate();
				em.getTransaction().commit();
			} catch (RuntimeException e) {
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
				throw e;
			}
		} catch (RuntimeException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, 
					"Failed to persist query '" + name + "': " + e.getMessage(), getURI(), e));
			throw new IOException("Failed to persist query '" + name + "'", e);
		}
	}

	private Query loadNamedQuery(String name) throws IOException {
		List<?> rows;
		try (Lease lease = leaseChecked(); EntityManager em = lease.createEntityManager()) {
			em.getTransaction().begin();
			try {
				ensureCatalogTable(em);
				rows = em.createNativeQuery("SELECT XMI FROM " + QUERY_CATALOG_TABLE + " WHERE NAME = ?1")
						.setParameter(1, name).getResultList();
				em.getTransaction().commit();
			} catch (RuntimeException e) {
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
				throw e;
			}
		} catch (RuntimeException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, 
					"Failed to load persisted query '" + name + "': " + e.getMessage(), getURI(), e));
			throw new IOException("Failed to load persisted query '" + name + "'", e);
		}
		if (rows.isEmpty()) {
			throw new IOException("No persisted query named '" + name + "'");
		}
		try {
			return PersistedQueries.fromXmi(name, clobText(rows.get(0)), packageRegistry());
		} catch (QueryException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, 
					"Cannot load persisted query '" + name + "': " + e.getMessage(), getURI(), e));
			throw new IOException("Cannot load persisted query '" + name + "': " + e.getMessage(), e);
		}
	}

	/** The catalog lives outside the mapped model — plain DDL, created on first use. */
	private void ensureCatalogTable(EntityManager em) {
		em.createNativeQuery("CREATE TABLE IF NOT EXISTS " + QUERY_CATALOG_TABLE
				+ " (NAME VARCHAR(255) NOT NULL PRIMARY KEY, XMI CLOB NOT NULL)").executeUpdate();
	}

	private static String clobText(Object value) {
		if (value instanceof Clob clob) {
			try {
				return clob.getSubString(1, (int) clob.length());
			} catch (SQLException e) {
				throw new WrappedException(e);
			}
		}
		return String.valueOf(value);
	}

	private EPackage.Registry packageRegistry() {
		return getResourceSet() == null ? EPackage.Registry.INSTANCE
				: getResourceSet().getPackageRegistry();
	}

	private QueryResult executeCount(JpaQueryPlan plan, Lease lease, String entityName) throws IOException {
		try (lease; EntityManager em = lease.createEntityManager()) {
			TypedQuery<Long> countQuery = em.createQuery(plan.jpql(), Long.class);
			plan.parameters().forEach(countQuery::setParameter);
			return QueryResults.count(countQuery.getSingleResult());
		} catch (RuntimeException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, 
					"Failed to execute count query on '" + entityName + "': " + e.getMessage(), getURI(), e));
			throw new IOException("Failed to execute count query on '" + entityName + "'", e);
		}
	}

	/**
	 * Executes an OBJECTS or row-shaped plan over a scrollable cursor. The
	 * {@link EntityManager} and lease stay open until the returned result is closed.
	 * <p>
	 * An equality predicate on the entity's ID compiles to an EclipseLink
	 * {@code ReadObjectQuery}, which rejects the scrollable-cursor hint (issue #91) —
	 * such plans fetch their at-most-one result eagerly instead. The branch tests the
	 * <em>produced</em> query type, not the predicate shape, so an AND over a full
	 * composite key that EclipseLink's JPQL compiler also turns into a
	 * {@code ReadObjectQuery} lands here by construction — the selector guarantee of
	 * issue #109 (TCK: {@code compositeIdSelectorResolvesASingleObject}).
	 */
	private QueryResult executeCursor(JpaQueryPlan plan, Lease lease, String entityName) throws IOException {
		EntityManager em = lease.createEntityManager();
		try {
			TypedQuery<?> typedQuery = em.createQuery(plan.jpql(), resultType(plan));
			plan.parameters().forEach(typedQuery::setParameter);
			if (plan.shape() == QueryShape.OBJECTS && !plan.batchFetchPaths().isEmpty()) {
				// to-many expand levels batch-fetch instead of fetch-joining (issue #95);
				// IN batching is cursor-compatible (each row feeds the batch policy)
				typedQuery.setHint(QueryHints.BATCH_TYPE, BatchFetchType.IN);
				for (String path : plan.batchFetchPaths()) {
					typedQuery.setHint(QueryHints.BATCH, path);
				}
			}
			if (plan.skip() > 0) {
				typedQuery.setFirstResult(plan.skip());
			}
			if (plan.limit() > 0) {
				typedQuery.setMaxResults(plan.limit());
			}
			Stream<Object> rows;
			DatabaseQuery databaseQuery = typedQuery.unwrap(JpaQuery.class).getDatabaseQuery();
			if (databaseQuery.isReadAllQuery() || databaseQuery.isDataReadQuery()) {
				typedQuery.setHint(QueryHints.SCROLLABLE_CURSOR, HintValues.TRUE);
				ScrollableCursor cursor = (ScrollableCursor) typedQuery.unwrap(JpaQuery.class).getResultCursor();
				rows = cursorStream(cursor).onClose(() -> {
					cursor.close();
					em.close();
					lease.close();
				});
			} else {
				rows = typedQuery.getResultList().stream().map(Object.class::cast).onClose(() -> {
					em.close();
					lease.close();
				});
			}
			if (plan.shape() == QueryShape.OBJECTS) {
				return QueryResults.objects(rows
						.filter(EObject.class::isInstance)
						.map(EObject.class::cast));
			}
			return QueryResults.rows(plan.shape(), rows.map(row -> toRow(row, plan)));
		} catch (RuntimeException e) {
			em.close();
			lease.close();
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, 
					"Failed to execute query on '" + entityName + "': " + e.getMessage(), getURI(), e));
			throw new IOException("Failed to execute query on '" + entityName + "'", e);
		}
	}

	private Class<?> resultType(JpaQueryPlan plan) {
		if (plan.shape() == QueryShape.OBJECTS) {
			return Object.class;
		}
		return plan.rowKeys().size() > 1 ? Object[].class : Object.class;
	}

	private Stream<Object> cursorStream(ScrollableCursor cursor) {
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
		return StreamSupport.stream(rows, false);
	}

	private QueryResultRow toRow(Object row, JpaQueryPlan plan) {
		List<Object> values;
		if (row instanceof Object[] cells) {
			values = Arrays.asList(cells);
		} else {
			values = Collections.singletonList(row);
		}
		return QueryResultRows.of(plan.rowAliases(), values);
	}

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
		// The lease (and its EntityManager) stay open until the returned stream is closed;
		// both are released in onClose / the failure paths.
		Lease lease = leaseChecked();
		ClassDescriptor descriptor;
		try {
			descriptor = getDescriptor(entityName);
		} catch (RuntimeException e) {
			lease.close();
			throw new IOException("Failed to open stream on entity '" + entityName + "'", e);
		}
		if (isNull(descriptor)) {
			lease.close();
			return Stream.empty();
		}
		String validatedAlias = descriptor.getAlias();
		EntityManager em = lease.createEntityManager();
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
						lease.close();
					});
		} catch (RuntimeException e) {
			em.close();
			lease.close();
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
			getWarnings().add(PersistenceDiagnostic.warning(DIAGNOSTIC_SOURCE, 
					"Resource URI has no entity segment — count is 0", getURI()));
			return 0;
		}
		try (Lease lease = leaseChecked()) {
			ClassDescriptor descriptor = getDescriptor(entityName);
			if (isNull(descriptor)) {
				getWarnings().add(PersistenceDiagnostic.warning(DIAGNOSTIC_SOURCE, 
						"No descriptor for entity '" + entityName + "' — count is 0", getURI()));
				return 0;
			}
			// Use the validated alias from the descriptor to prevent JPQL injection
			String validatedAlias = descriptor.getAlias();
			try (EntityManager em = lease.createEntityManager()) {
				return em.createQuery("SELECT COUNT(e) FROM " + validatedAlias + " e", Long.class)
						.getSingleResult();
			} catch (RuntimeException e) {
				getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, 
						"Failed to count entity '" + entityName + "': " + e.getMessage(), getURI(), e));
				throw new IOException("Failed to count entity '" + entityName + "' in " + getURI(), e);
			}
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
	 * Returns the EclipseLink Server session, or null if the unit is not EclipseLink-backed.
	 * A short lease keeps the factory alive for the lookup. Callers must already hold their
	 * own operation lease (see {@link #leaseChecked()}) so an unavailable unit surfaces
	 * there with a diagnostic, not here.
	 */
	private Server getServer() {
		try (Lease lease = unit.lease()) {
			return serverOf(lease);
		}
	}

	/** Null-safe server-session lookup on an existing lease (non-EclipseLink → null). */
	private static Server serverOf(Lease lease) {
		try {
			return lease.getServerSession();
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
			getWarnings().add(PersistenceDiagnostic.warning(DIAGNOSTIC_SOURCE, msg, getURI()));
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
	 * Converts a keyed-access fragment to the {@code em.find} key: the typed scalar for
	 * single-id descriptors, an {@code Object[]} in primary-key-field order for the
	 * composite {@code k1=v1,k2=v2} shape (issue #109 — the descriptor's PK fields are
	 * collected in id-attribute declaration order by the {@code IdConfigurator}, so the
	 * canonical fragment order and the key order coincide; EclipseLink's
	 * {@code DynamicIdentityPolicy} accepts {@code Object[]} for multi-PK descriptors).
	 */
	private Object convertId(String idValue, ClassDescriptor descriptor) {
		EClass eClass = eClassOf(descriptor);
		boolean composite = nonNull(eClass) && CompositeIds.isComposite(eClass);
		if (composite) {
			if (!CompositeIds.isCompositeFragment(idValue)) {
				throw new IllegalArgumentException("Type '" + eClass.getName()
						+ "' has a composite id — the keyed-access fragment is k1=v1,k2=v2 (issue #109), got '"
						+ idValue + "'");
			}
			List<String> parts = CompositeIds.parse(eClass, idValue);
			List<org.eclipse.persistence.internal.helper.DatabaseField> pkFields = descriptor.getPrimaryKeyFields();
			Object[] key = new Object[parts.size()];
			for (int i = 0; i < parts.size(); i++) {
				key[i] = convertScalar(parts.get(i), i < pkFields.size() ? pkFields.get(i).getType() : null);
			}
			return key;
		}
		Class<?> pkType = descriptor.getPrimaryKeyFields().stream()
				.findFirst()
				.map(field -> field.getType())
				.orElse(null);
		return convertScalar(idValue, pkType);
	}

	private static Object convertScalar(String idValue, Class<?> pkType) {
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

	/** The EClass behind a fennec dynamic descriptor ({@code EDynamicType} property). */
	private static EClass eClassOf(ClassDescriptor descriptor) {
		return descriptor.getProperty(DynamicType.DESCRIPTOR_PROPERTY) instanceof EDynamicType dynamicType
				? dynamicType.getEClass()
				: null;
	}
}
