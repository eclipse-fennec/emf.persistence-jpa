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
package org.eclipse.fennec.persistence.repository.spi;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.model.command.Command;
import org.eclipse.fennec.model.command.DeleteCommand;
import org.eclipse.fennec.model.command.InsertCommand;
import org.eclipse.fennec.model.command.UpdateCommand;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.capabilities.PersistenceCapabilities;
import org.eclipse.fennec.persistence.helper.CompositeIds;
import org.eclipse.fennec.persistence.query.api.CommandResource;
import org.eclipse.fennec.persistence.query.api.QueryProcessor;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.eclipse.fennec.persistence.query.api.QueryableResource;
import org.eclipse.fennec.persistence.repository.RepositoryConstants;
import java.util.Optional;
import org.eclipse.fennec.persistence.query.support.NamedOperations;
import org.eclipse.fennec.persistence.query.support.PersistedQueries;
import org.eclipse.fennec.persistence.repository.api.PreparedQuery;
import org.eclipse.fennec.persistence.repository.api.Repository;
import org.eclipse.fennec.persistence.resource.PersistenceResource;
import org.eclipse.fennec.persistence.resource.PersistenceResource.ActionType;

/**
 * Backend-neutral implementation of the full {@link Repository} facade. All data access
 * happens through the backend's whiteboard resource layer: URIs are
 * {@code <baseUri>/<EClassName>[#<idFragment>]}, single objects are addressed by the
 * URI <b>fragment</b> (the only shape all backends implement), the fragment itself is
 * {@link CompositeIds#fragment(EObject)}.
 * <p>
 * Flavour bundles subclass (usually anonymously) and provide the base URI and a supplier
 * for backend-configured {@link ResourceSet}s. A repository instance owns one
 * ResourceSet and is not thread-safe — register instances with PROTOTYPE scope (see
 * {@link AbstractRepositoryComponent}) so every consumer holds its own.
 *
 * @since 18.08.2026
 */
public abstract class AbstractRepository implements Repository {

	private final String id;
	private volatile NamedOperations namedOperations;
	private final URI baseUri;
	private final Supplier<ResourceSet> resourceSets;
	private final QueryProcessor queryProcessor;
	private final Map<Object, Object> defaultLoadOptions;
	private final Map<Object, Object> defaultSaveOptions;
	/** Root types of saveQuery executions seen by this instance, for find(name, ...). */
	private final Map<String, EClass> namedQueryRoots = new ConcurrentHashMap<>();
	private ResourceSet resourceSet;
	private PersistenceCapabilities capabilities;
	private volatile boolean disposed;

	/**
	 * @param id the configured repository id, never null
	 * @param baseUri the backend location, e.g. {@code jpa://shop}, without trailing separator
	 * @param resourceSets supplier of backend-configured resource sets, never null
	 * @param queryProcessor the backend's query processor for prepare-time validation, may be null
	 * @param defaultLoadOptions defaults applied to reads, may be null
	 * @param defaultSaveOptions defaults applied to writes, may be null
	 */
	protected AbstractRepository(String id, URI baseUri, Supplier<ResourceSet> resourceSets,
			QueryProcessor queryProcessor, Map<Object, Object> defaultLoadOptions,
			Map<Object, Object> defaultSaveOptions) {
		this.id = requireNonNull(id, "repository id must not be null");
		this.baseUri = requireNonNull(baseUri, "base URI must not be null");
		this.resourceSets = requireNonNull(resourceSets, "resource set supplier must not be null");
		this.queryProcessor = queryProcessor;
		this.defaultLoadOptions = isNull(defaultLoadOptions) ? Map.of() : Map.copyOf(defaultLoadOptions);
		this.defaultSaveOptions = isNull(defaultSaveOptions) ? Map.of() : Map.copyOf(defaultSaveOptions);
	}

	/*
	 * ==================== RepositoryService ====================
	 */

	@Override
	public String id() {
		return id;
	}

	@Override
	public URI baseUri() {
		return baseUri;
	}

	@Override
	public synchronized PersistenceCapabilities capabilities() {
		checkNotDisposed();
		if (isNull(capabilities)) {
			// any resource of this backend answers; capabilities never touch the store
			Resource probe = getResourceSet().createResource(baseUri.appendSegment("FennecRepositoryProbe"));
			try {
				capabilities = persistence(probe, "capabilities").capabilities();
			} catch (IOException e) {
				throw new IllegalStateException("Backend of repository '" + id + "' answers no capabilities", e);
			} finally {
				probe.getResourceSet().getResources().remove(probe);
			}
		}
		return capabilities;
	}

	@Override
	public boolean isDisposed() {
		return disposed;
	}

	@Override
	public synchronized void dispose() {
		if (disposed) {
			return;
		}
		disposed = true;
		if (nonNull(resourceSet)) {
			resourceSet.getResources().forEach(Resource::unload);
			resourceSet.getResources().clear();
			resourceSet = null;
		}
	}

	@Override
	public void close() {
		dispose();
	}

	@Override
	public synchronized ResourceSet getResourceSet() {
		checkNotDisposed();
		if (isNull(resourceSet)) {
			resourceSet = resourceSets.get();
		}
		return resourceSet;
	}

	@Override
	public ResourceSet createResourceSet() {
		checkNotDisposed();
		return resourceSets.get();
	}

	@Override
	public URI createUri(EObject object) {
		requireNonNull(object, "object must not be null");
		String fragment;
		try {
			fragment = CompositeIds.fragment(object);
		} catch (IllegalStateException e) {
			return null;
		}
		return isNull(fragment) ? null : collectionUri(object.eClass()).appendFragment(fragment);
	}

	@Override
	public URI createUri(EObject object, Map<?, ?> options) {
		// options are reserved for id overrides/type hints; none defined yet
		return createUri(object);
	}

	@Override
	public URI createUri(EClass eClass, Object id) {
		requireNonNull(eClass, "eClass must not be null");
		requireNonNull(id, "id must not be null");
		return collectionUri(eClass).appendFragment(String.valueOf(id));
	}

	@Override
	public EObject createProxy(EClass eClass, Object id) {
		EObject proxy = EcoreUtil.create(eClass);
		((InternalEObject) proxy).eSetProxyURI(createUri(eClass, id));
		return proxy;
	}

	@Override
	public void proxify(EObject object) {
		requireNonNull(object, "object must not be null");
		URI uri = createUri(object);
		if (isNull(uri)) {
			throw new IllegalStateException("Cannot proxify an object without a determinable id: " + object);
		}
		((InternalEObject) object).eSetProxyURI(uri);
		detach(object);
	}

	@Override
	public Resource attach(EObject object) {
		checkNotDisposed();
		requireNonNull(object, "object must not be null");
		Resource existing = object.eResource();
		if (nonNull(existing)) {
			return existing;
		}
		Resource resource = getOrCreate(getResourceSet(), collectionUri(object.eClass()));
		resource.getContents().add(object);
		return resource;
	}

	@Override
	public Resource attach(EObject object, Map<?, ?> options) {
		// options are reserved; none defined yet
		return attach(object);
	}

	@Override
	public EObject detach(EObject object) {
		requireNonNull(object, "object must not be null");
		Resource resource = object.eResource();
		if (isNull(resource)) {
			return object;
		}
		resource.getContents().remove(object);
		if (resource.getContents().isEmpty() && nonNull(resource.getResourceSet())) {
			resource.getResourceSet().getResources().remove(resource);
		}
		return object;
	}

	/*
	 * ==================== ReadRepository ====================
	 */

	@Override
	public Resource getResource(URI uri, boolean loadOnDemand) throws IOException {
		checkNotDisposed();
		requireNonNull(uri, "uri must not be null");
		try {
			return getResourceSet().getResource(uri, loadOnDemand);
		} catch (WrappedException e) {
			throw io("Failed to get resource " + uri, e);
		}
	}

	@Override
	public EObject getEObject(URI uri) throws IOException {
		return getEObject(uri, null);
	}

	@Override
	public EObject getEObject(URI uri, Map<?, ?> options) throws IOException {
		checkNotDisposed();
		requireNonNull(uri, "uri must not be null");
		if (isNull(uri.fragment())) {
			return null;
		}
		try {
			Resource resource = getOrCreate(getResourceSet(), uri.trimFragment());
			applyOptions(resource, options, ActionType.LOAD);
			return resource.getEObject(uri.fragment());
		} catch (RuntimeException e) {
			throw io("Failed to load " + uri, e);
		}
	}

	@Override
	public EObject getEObject(EClass eClass, Object id) throws IOException {
		return getEObject(createUri(eClass, id), null);
	}

	@Override
	public EObject getEObject(EClass eClass, Object id, Map<?, ?> options) throws IOException {
		return getEObject(createUri(eClass, id), options);
	}

	@Override
	public Stream<EObject> getAllEObjects(EClass eClass) throws IOException {
		return getAllEObjects(eClass, null);
	}

	@Override
	public Stream<EObject> getAllEObjects(EClass eClass, Map<?, ?> options) throws IOException {
		requireNonNull(eClass, "eClass must not be null");
		QueryResult result = find(QueryBuilder.from(eClass).build(), null, options);
		try {
			return result.objects().onClose(result::close);
		} catch (RuntimeException e) {
			result.close();
			throw io("Failed to stream all " + eClass.getName(), e);
		}
	}

	@Override
	public long count(EClass eClass) throws IOException {
		return count(eClass, null);
	}

	@Override
	public long count(EClass eClass, Map<?, ?> options) throws IOException {
		checkNotDisposed();
		requireNonNull(eClass, "eClass must not be null");
		Resource resource = getOrCreate(getResourceSet(), collectionUri(eClass));
		return persistence(resource, "count").count(effective(options, defaultLoadOptions));
	}

	@Override
	public boolean exist(URI uri) throws IOException {
		checkNotDisposed();
		requireNonNull(uri, "uri must not be null");
		if (nonNull(uri.fragment())) {
			return nonNull(getEObject(uri));
		}
		Resource resource = getOrCreate(getResourceSet(), uri);
		return persistence(resource, "exist").exist(effective(null, defaultLoadOptions));
	}

	@Override
	public boolean exist(EClass eClass, Object id) throws IOException {
		return exist(createUri(eClass, id));
	}

	@Override
	public void reload(EObject object) throws IOException {
		checkNotDisposed();
		requireNonNull(object, "object must not be null");
		URI uri = createUri(object);
		if (isNull(uri)) {
			throw io("Cannot reload an object without a determinable id: " + object, null);
		}
		EObject fresh;
		try {
			// keyed read in a scratch set: the owned set's collection resource may serve
			// the cached instance — the very object being reloaded
			Resource resource = getOrCreate(createResourceSet(), uri.trimFragment());
			applyOptions(resource, null, ActionType.LOAD);
			fresh = resource.getEObject(uri.fragment());
		} catch (RuntimeException e) {
			throw io("Failed to reload " + uri, e);
		}
		if (isNull(fresh)) {
			throw io("Cannot reload " + uri + ": the object no longer exists in the backend", null);
		}
		copyState(fresh, object);
	}

	/**
	 * Replaces the target's persistent state with the fresh read, in place: attribute and
	 * reference values are taken over feature by feature (containment children move from
	 * the fresh instance to the target), derived/transient/unchangeable features and the
	 * container are left alone — so the target keeps its identity, resource attachment
	 * and container.
	 */
	private static void copyState(EObject fresh, EObject target) {
		for (EStructuralFeature feature : target.eClass().getEAllStructuralFeatures()) {
			if (feature.isDerived() || feature.isTransient() || !feature.isChangeable()) {
				continue;
			}
			if (feature instanceof EReference reference && reference.isContainer()) {
				continue;
			}
			if (!fresh.eIsSet(feature)) {
				target.eUnset(feature);
			} else if (feature.isMany()) {
				target.eSet(feature, new ArrayList<>((Collection<?>) fresh.eGet(feature)));
			} else {
				target.eSet(feature, fresh.eGet(feature));
			}
		}
	}

	@Override
	public QueryResult find(Query query) throws IOException {
		return find(query, null, null);
	}

	@Override
	public QueryResult find(Query query, Map<String, Object> parameters, Map<?, ?> options) throws IOException {
		checkNotDisposed();
		requireNonNull(query, "query must not be null");
		EClass root = query.getFrom();
		if (isNull(root)) {
			throw io("Query has no root type (from)", null);
		}
		if (query.isSaveQuery() && nonNull(query.getName())) {
			namedQueryRoots.put(query.getName(), root);
		}
		return queryableFor(root).query(query, parameters, effective(options, defaultLoadOptions));
	}

	@Override
	public QueryResult find(String name, Map<String, Object> parameters, Map<?, ?> options) throws IOException {
		checkNotDisposed();
		requireNonNull(name, "query name must not be null");
		return queryableFor(namedRoot(name, options)).query(name, parameters, effective(options, defaultLoadOptions));
	}

	@Override
	public long count(Query query) throws IOException {
		Query counted = query.isCountOnly() ? query : countOnlyCopy(query);
		try (QueryResult result = find(counted, null, null)) {
			return result.count();
		}
	}

	@Override
	public PreparedQuery prepare(Query query) throws IOException {
		checkNotDisposed();
		requireNonNull(query, "query must not be null");
		Query copy = EcoreUtil.copy(query);
		if (isNull(copy.getFrom())) {
			throw io("Query has no root type (from)", null);
		}
		if (nonNull(queryProcessor)) {
			Diagnostic diagnostic = queryProcessor.validate(copy, copy.getFrom());
			if (diagnostic.getSeverity() >= Diagnostic.ERROR) {
				throw io("Query is not servable by backend '" + queryProcessor.backend() + "': "
						+ diagnostic.getMessage(), null);
			}
		}
		return new DefaultPreparedQuery(this, copy);
	}

	@Override
	public PreparedQuery prepare(String name) throws IOException {
		checkNotDisposed();
		requireNonNull(name, "query name must not be null");
		NamedOperations catalog = namedOperations;
		if (nonNull(catalog)) {
			// with a catalog the query itself is available, so the handle knows its
			// parameters and its root type — the limitation that made OPTION_QUERY_ROOT
			// necessary was the catalog having no way to hand the object back (issue #203)
			Optional<EObject> found = catalog.lookup(name);
			if (found.isPresent()) {
				if (found.get() instanceof Query query) {
					return new DefaultPreparedQuery(this, PersistedQueries.forExecution(query));
				}
				throw io("'" + name + "' is not a query but a "
						+ found.get().eClass().getName(), null);
			}
		}
		return new DefaultPreparedQuery(this, name);
	}

	/**
	 * Points this repository at a named-operation catalog (issue #203). Set, {@code prepare}
	 * resolves a name to the query object itself, which is what lets a prepared handle answer
	 * {@code parameterDeclarations()} — a caller can then see which values it must supply
	 * without having seen the query.
	 *
	 * @param namedOperations the catalog, or {@code null} to resolve names in the backend
	 */
	public void setNamedOperations(NamedOperations namedOperations) {
		this.namedOperations = namedOperations;
	}

	/*
	 * ==================== WriteRepository ====================
	 */

	@Override
	public void save(EObject object) throws IOException {
		save(object, (Map<?, ?>) null);
	}

	@Override
	public void save(EObject object, Map<?, ?> options) throws IOException {
		checkNotDisposed();
		requireNonNull(object, "object must not be null");
		saveIsolated(List.of(object), effective(options, defaultSaveOptions));
	}

	@Override
	public void save(EObject object, URI uri) throws IOException {
		save(object, uri, null);
	}

	@Override
	public void save(EObject object, URI uri, Map<?, ?> options) throws IOException {
		checkNotDisposed();
		requireNonNull(object, "object must not be null");
		requireNonNull(uri, "uri must not be null");
		Resource resource = getOrCreate(getResourceSet(), uri.trimFragment());
		if (object.eResource() != resource) {
			resource.getContents().add(object);
		}
		resource.save(effective(options, defaultSaveOptions));
	}

	@Override
	public void saveAll(Collection<EObject> objects) throws IOException {
		saveAll(objects, null);
	}

	@Override
	public void saveAll(Collection<EObject> objects, Map<?, ?> options) throws IOException {
		checkNotDisposed();
		requireNonNull(objects, "objects must not be null");
		if (objects.isEmpty()) {
			return;
		}
		saveIsolated(objects, effective(options, defaultSaveOptions));
	}

	/**
	 * Saves exactly the given objects — the old one-resource-per-object save semantics on
	 * top of the collection-resource mechanic. The objects are moved into per-type scratch
	 * resources (Resource.save writes all contents, so siblings sharing a loaded
	 * collection resource must not be aboard), saved with one backend save per type, and
	 * then restored to their previous attachment; previously unattached objects end up
	 * attached to their collection resource in the owned ResourceSet, matching the old
	 * repository contract. Generated ids are written back by the backends while the
	 * objects sit in the scratch resources.
	 */
	private void saveIsolated(Collection<EObject> objects, Map<?, ?> options) throws IOException {
		Map<EObject, Resource> origins = new LinkedHashMap<>();
		ResourceSet scratch = createResourceSet();
		try {
			for (EObject object : objects) {
				requireNonNull(object, "objects must not contain null");
				if (origins.containsKey(object)) {
					continue;
				}
				origins.put(object, object.eResource());
				getOrCreate(scratch, collectionUri(object.eClass())).getContents().add(object);
			}
			for (Resource resource : List.copyOf(scratch.getResources())) {
				resource.save(options);
			}
		} finally {
			for (Map.Entry<EObject, Resource> origin : origins.entrySet()) {
				if (nonNull(origin.getValue())) {
					origin.getValue().getContents().add(origin.getKey());
				} else {
					// leave the scratch resource first — attach() would otherwise answer it
					detach(origin.getKey());
					attach(origin.getKey());
				}
			}
		}
	}

	@Override
	public void delete(EObject object) throws IOException {
		delete(object, null);
	}

	@Override
	public void delete(EObject object, Map<?, ?> options) throws IOException {
		checkNotDisposed();
		requireNonNull(object, "object must not be null");
		// Resource.delete removes exactly its contents — isolate the object first so
		// nothing else that happens to share a loaded collection resource is deleted.
		ResourceSet scratch = createResourceSet();
		Resource resource = scratch.createResource(collectionUri(object.eClass()));
		resource.getContents().add(object);
		resource.delete(effective(options, Map.of()));
	}

	@Override
	public void delete(URI uri) throws IOException {
		delete(uri, null);
	}

	@Override
	public void delete(URI uri, Map<?, ?> options) throws IOException {
		checkNotDisposed();
		EObject object = getEObject(uri, options);
		if (nonNull(object)) {
			delete(object, options);
		}
	}

	@Override
	public long execute(Command command) throws IOException {
		return execute(command, null, null);
	}

	@Override
	public long execute(Command command, Map<String, Object> parameters, Map<?, ?> options)
			throws IOException {
		checkNotDisposed();
		requireNonNull(command, "command must not be null");
		EClass root = commandRoot(command);
		Resource resource = getOrCreate(getResourceSet(), collectionUri(root));
		if (resource instanceof CommandResource commands) {
			return commands.execute(command, parameters, effective(options, Map.of()));
		}
		throw io("Backend of repository '" + id + "' does not support commands", null);
	}

	/*
	 * ==================== internals ====================
	 */

	/** The collection URI for a type: {@code <baseUri>/<EClassName>}. */
	protected URI collectionUri(EClass eClass) {
		return baseUri.appendSegment(eClass.getName());
	}

	private static Resource getOrCreate(ResourceSet set, URI uri) {
		Resource resource = set.getResource(uri, false);
		if (isNull(resource)) {
			resource = set.createResource(uri);
		}
		if (isNull(resource)) {
			throw new IllegalStateException("No resource factory is registered for " + uri
					+ " — is the backend's whiteboard resource factory available?");
		}
		return resource;
	}

	private QueryableResource queryableFor(EClass root) throws IOException {
		Resource resource = getOrCreate(getResourceSet(), collectionUri(root));
		if (resource instanceof QueryableResource queryable) {
			return queryable;
		}
		throw io("Backend of repository '" + id + "' does not support queries", null);
	}

	private PersistenceResource persistence(Resource resource, String operation) throws IOException {
		if (resource instanceof PersistenceResource persistence) {
			return persistence;
		}
		throw io("Backend of repository '" + id + "' answers no PersistenceResource for " + operation
				+ " (uri " + resource.getURI() + ")", null);
	}

	/**
	 * Resolves the root type of a persisted query: an explicit
	 * {@link RepositoryConstants#OPTION_QUERY_ROOT} option wins, then roots remembered
	 * from saveQuery executions on this instance. The backend catalog has no load-back
	 * API yet, so an unknown name is refused with guidance.
	 */
	private EClass namedRoot(String name, Map<?, ?> options) throws IOException {
		if (nonNull(options) && options.get(RepositoryConstants.OPTION_QUERY_ROOT) instanceof EClass hinted) {
			return hinted;
		}
		EClass remembered = namedQueryRoots.get(name);
		if (nonNull(remembered)) {
			return remembered;
		}
		throw io("Cannot resolve the root type of persisted query '" + name
				+ "': pass option " + RepositoryConstants.OPTION_QUERY_ROOT
				+ " or execute the saving query through this repository first", null);
	}

	private static Query countOnlyCopy(Query query) {
		Query copy = EcoreUtil.copy(query);
		copy.setCountOnly(true);
		return copy;
	}

	private static EClass commandRoot(Command command) throws IOException {
		if (command instanceof InsertCommand insert) {
			if (insert.getObjects().isEmpty()) {
				throw io("InsertCommand carries no objects", null);
			}
			return insert.getObjects().get(0).eClass();
		}
		if (command instanceof DeleteCommand delete && nonNull(delete.getSelector())) {
			return requireRoot(delete.getSelector());
		}
		if (command instanceof UpdateCommand update && nonNull(update.getSelector())) {
			return requireRoot(update.getSelector());
		}
		throw io("Cannot determine the target type of command " + command.eClass().getName(), null);
	}

	private static EClass requireRoot(Query selector) throws IOException {
		EClass root = selector.getFrom();
		if (isNull(root)) {
			throw io("Command selector has no root type (from)", null);
		}
		return root;
	}

	private void applyOptions(Resource resource, Map<?, ?> options, ActionType... types) throws IOException {
		Map<Object, Object> merged = effective(options, defaultLoadOptions);
		if (nonNull(merged)) {
			persistence(resource, "options").updateDefaultOptions(merged, types);
		}
	}

	/**
	 * Per-call options over the configured defaults. Deliberately not
	 * {@code EMFHelper.getEffectiveOptions}: that helper takes ownership of its input
	 * maps (it may return and then mutate one of them), while both of ours are shared —
	 * the backend resource merges with its own defaults and adds the response map itself.
	 */
	private static Map<Object, Object> effective(Map<?, ?> options, Map<?, ?> defaults) {
		if ((isNull(options) || options.isEmpty()) && defaults.isEmpty()) {
			return null;
		}
		Map<Object, Object> merged = new HashMap<>(defaults);
		if (nonNull(options)) {
			merged.putAll(options);
		}
		return merged;
	}

	private void checkNotDisposed() {
		if (disposed) {
			throw new IllegalStateException("Repository '" + id + "' is disposed");
		}
	}

	private static IOException io(String message, Throwable cause) {
		return isNull(cause) ? new IOException(message) : new IOException(message, cause);
	}
}
