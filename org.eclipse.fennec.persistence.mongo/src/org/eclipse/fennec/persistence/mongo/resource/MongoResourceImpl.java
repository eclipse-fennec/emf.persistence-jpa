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
package org.eclipse.fennec.persistence.mongo.resource;

import static com.mongodb.client.model.Filters.eq;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.bson.BsonFormatDelegate;
import org.eclipse.fennec.codec.bson.BsonFormatReaderDelegate;
import org.eclipse.fennec.codec.config.ConfigProperty;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.context.ContextHelper;
import org.eclipse.fennec.codec.deser.DeserializationState.UnresolvedReference;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.format.jackson.FormatDelegateGenerator;
import org.eclipse.fennec.codec.format.jackson.FormatDelegateParser;
import org.eclipse.fennec.codec.module.CodecModule;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.eclipse.fennec.model.command.Command;
import org.eclipse.fennec.model.command.DeleteCommand;
import org.eclipse.fennec.model.command.InsertCommand;
import org.eclipse.fennec.model.command.UpdateCommand;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.persistence.Options;
import org.eclipse.fennec.persistence.capabilities.CommandCapabilities;
import org.eclipse.fennec.persistence.capabilities.CommandCapabilitiesBuilder;
import org.eclipse.fennec.persistence.capabilities.CommandFeature;
import org.eclipse.fennec.persistence.diagnostic.PersistenceDiagnostic;
import org.eclipse.fennec.persistence.helper.CompositeIds;
import org.eclipse.fennec.persistence.mongo.MongoPersistenceConstants;
import org.eclipse.fennec.persistence.mongo.OwnershipMaintenance;
import org.eclipse.fennec.persistence.mongo.query.BsonValues;
import org.eclipse.fennec.persistence.mongo.query.MongoQueries;
import org.eclipse.fennec.persistence.mongo.query.MongoQueryPlan;
import org.eclipse.fennec.persistence.mongo.query.MongoQueryProcessor;
import org.eclipse.fennec.persistence.query.QueryException;
import org.eclipse.fennec.persistence.query.api.CommandResource;
import org.eclipse.fennec.persistence.query.api.QueryProcessor;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.eclipse.fennec.persistence.query.api.QueryResultRow;
import org.eclipse.fennec.persistence.query.api.QueryShape;
import org.eclipse.fennec.persistence.query.api.QueryableResource;
import org.eclipse.fennec.persistence.query.support.ChangeTemplates;
import org.eclipse.fennec.persistence.query.support.CommandTransaction;
import org.eclipse.fennec.persistence.query.support.PersistedQueries;
import org.eclipse.fennec.persistence.query.support.QueryResultRows;
import org.eclipse.fennec.persistence.query.support.QueryResults;
import org.eclipse.fennec.persistence.query.support.ReferenceResolver;
import org.eclipse.fennec.persistence.resource.PersistenceResource;
import org.eclipse.fennec.persistence.resource.StreamingResource;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.ClientSession;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.WriteModel;

import tools.jackson.core.ErrorReportConfiguration;
import tools.jackson.core.JsonEncoding;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.StreamWriteConstraints;
import tools.jackson.core.io.ContentReference;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.util.BufferRecycler;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.json.JsonMapper;

/**
 * EMF Resource backed by MongoDB.
 * <p>
 * URI scheme: {@code mongodb://<db>/<collection>[/<id>]} — the URI carries addressing
 * only; the connection ({@link MongoDatabase}) is injected by the factory.
 * <p>
 * Serialization is <b>BsonDocument-direct</b>: EObjects are written into / read from
 * {@link BsonDocument}s through the codec's {@link BsonFormatDelegate} /
 * {@link BsonFormatReaderDelegate} bridged via {@link FormatDelegateGenerator} /
 * {@link FormatDelegateParser} — no byte stream is involved.
 * <p>
 * Identity: the EMF ID attribute ({@code eClass().getEIDAttribute()}) maps to the Mongo
 * {@code _id}. When the attribute is unset on save and String-typed, a new
 * {@link ObjectId} hex string is generated and <b>written back</b> into the EObject.
 * <p>
 * Cross-document references follow the framework-wide proxy contract: unresolved
 * references decoded by the codec become EMF proxies whose URIs resolve through the
 * {@link ResourceSet} back to {@link #getEObject(String)}.
 *
 * @author Mark Hoffmann
 * @since 16.07.2026
 */
public class MongoResourceImpl extends CodecResource implements PersistenceResource, StreamingResource, QueryableResource, CommandResource, OwnershipMaintenance {

	private static final Logger LOG = Logger.getLogger(MongoResourceImpl.class.getName());

	/**
	 * Bookkeeping collection for containment ownership across document boundaries (issue #139).
	 * Deliberately a collection of its own rather than a field inside the child documents: the
	 * codec owns their shape, and an injected key would read as an unknown feature there
	 * (eclipse-fennec/emf.codec#151).
	 */
	static final String OWNERSHIP_COLLECTION = "_fennec_ownership";

	/** Diagnostic source of this resource layer (issue #19): the bundle namespace. */
	static final String DIAGNOSTIC_SOURCE = "org.eclipse.fennec.persistence.mongo";

	private final MongoDatabase database;
	private final CodecValueRegistry valueRegistry;
	/** Lazily populated per-EClass id configuration for composite-id classes (issue #110). */
	private final Map<EClass, Map<String, Object>> compositeIdConfigs;
	private volatile ObjectMapper mongoMapper;
	private volatile QueryProcessor queryProcessor = new MongoQueryProcessor();
	/** Session-capable client for command transactions (issue #112); optional. */
	private volatile MongoClient client;
	/** The open command bracket (issue #112); resources are single-threaded per EMF semantics. */
	private MongoCommandTransaction activeTransaction;
	/** Options captured by {@link #load(Map)} for the deferred population (issue #146). */
	private Map<?, ?> loadOptions;
	/**
	 * {@code true} once {@link #load(Map)} was called — the trigger for deferred population.
	 * Deliberately distinct from EMF's {@code isLoaded}: attaching a single keyed-resolved
	 * object to the contents flips {@code isLoaded} via {@code ContentsEList.loaded()}, which
	 * must not cause the whole collection to be read on a later {@link #getContents()}.
	 */
	private boolean loadRequested;
	/** {@code true} once the collection-wide population has run. */
	private boolean contentsPopulated;
	/** Re-entrancy guard so internal contents access during population does not recurse. */
	private boolean populating;

	/** Cached hello-probe result (issue #114); {@code null} until first successful probe. */
	private volatile Boolean transactionalDeployment;
	/** Model annotations win over the static composite id policy (issue #115); sticky per resource. */
	private volatile boolean idConfigFromModel;

	public MongoResourceImpl(URI uri, MongoDatabase database, MetadataService metadataService,
			CodecValueRegistry valueRegistry) {
		this(uri, database, metadataService, valueRegistry, new ConcurrentHashMap<>());
	}

	private MongoResourceImpl(URI uri, MongoDatabase database, MetadataService metadataService,
			CodecValueRegistry valueRegistry, Map<EClass, Map<String, Object>> compositeIdConfigs) {
		// the per-EClass id configuration rides the resolver's resource plane (issue
		// #110): the map reference is shared and populated lazily — always BEFORE the
		// first encode/decode of the class, so the resolver's per-class cache is warm
		// with the right values
		super(uri, metadataService, ConfigurationResolver.defaults().toBuilder()
				.resourceProperties(Map.of(ConfigProperty.ECLASS_CONFIG.getKey(), compositeIdConfigs))
				.build(), valueRegistry, null, null);
		requireNonNull(database, "MongoDatabase is required");
		this.database = database;
		this.valueRegistry = valueRegistry;
		this.compositeIdConfigs = compositeIdConfigs;
	}

	/** Reads {@code OPTION_ID_CONFIG_FROM_MODEL} (issue #115); the flag sticks per resource. */
	private void applyIdConfigOption(Map<?, ?> options) {
		Object value = isNull(options) ? null
				: options.get(MongoPersistenceConstants.OPTION_ID_CONFIG_FROM_MODEL);
		if (value instanceof Boolean flag) {
			idConfigFromModel = flag;
		} else if (value instanceof String text) {
			idConfigFromModel = Boolean.parseBoolean(text);
		}
	}

	/**
	 * Registers the codec id configuration for a composite-id EClass (issue #110,
	 * decision: STRUCTURED + BOTH, composite classes only): the id features derive from
	 * the {@code isID} attributes in declaration order (the codec requires the explicit
	 * list — its own fallback is the single eID attribute, emf.codec#99), {@code _id}
	 * serialises as the structured sub-document, and the components stay in the payload
	 * so component predicates keep addressing plain fields.
	 */
	private void ensureCompositeIdConfig(EClass eClass) {
		if (idConfigFromModel || isNull(eClass) || !CompositeIds.isComposite(eClass)) {
			// OPTION_ID_CONFIG_FROM_MODEL (issue #115): the model's codec annotations
			// decide the serialization plane — no per-class override is injected
			return;
		}
		compositeIdConfigs.computeIfAbsent(eClass, ec -> Map.of(
				ConfigProperty.ID_FEATURES.getKey(),
				CompositeIds.idAttributes(ec).stream().map(EAttribute::getName).toList(),
				ConfigProperty.ID_FORMAT.getKey(), "STRUCTURED",
				ConfigProperty.ID_KEY_MODE.getKey(), "BOTH"));
	}

	/**
	 * The Jackson mapper for the BsonDocument-direct paths. {@code CodecResource} builds
	 * its mapper per save/load operation through private machinery; this mirrors that
	 * construction with the public codec API ({@link CodecModule#builder()}) using the
	 * resolver's global configuration. The codec writes the type discriminator
	 * ({@code _type} by default) into every document and prefers it over the
	 * {@code EXPECTED_TYPE} hint on decode — the hint is only the fallback for
	 * documents without one. No custom {@code TypeDiscriminatorService} is wired;
	 * it is only needed for custom discriminator mappings, not for the generic
	 * type field (issue #88).
	 */
	/**
	 * Extends the caller options with the codec configuration resolver so the query
	 * translation can resolve the effective type-discriminator config (issue #88).
	 */
	private Map<Object, Object> queryOptions(Map<?, ?> options) {
		Map<Object, Object> effective = new HashMap<>();
		if (nonNull(options)) {
			effective.putAll(options);
		}
		effective.put(MongoPersistenceConstants.OPTION_CODEC_RESOLVER, getResolver());
		return effective;
	}

	protected ObjectMapper mongoMapper() {
		ObjectMapper mapper = mongoMapper;
		if (nonNull(mapper)) {
			return mapper;
		}
		synchronized (this) {
			if (isNull(mongoMapper)) {
				ConfigurationResolver resolver = getResolver();
				CodecModule.Builder moduleBuilder = CodecModule.builder()
						.resolver(resolver)
						.metadataService(getMetadataService())
						.globalIgnoreFeatures(resolver.getGlobalProperty(ConfigProperty.IGNORE_FEATURES))
						.smartCompression(resolver.getGlobalProperty(ConfigProperty.SMART_COMPRESSION))
						.useNamesFromExtendedMetaData(
								resolver.getGlobalProperty(ConfigProperty.USE_NAMES_FROM_EXTENDED_METADATA))
						.dateFormat(resolver.getGlobalProperty(ConfigProperty.DATE_FORMAT));
				if (nonNull(valueRegistry)) {
					moduleBuilder.valueRegistry(valueRegistry);
				}
				mongoMapper = JsonMapper.builder()
						.addModule(moduleBuilder.build())
						.build();
			}
			return mongoMapper;
		}
	}

	// ------------------------------------------------------------------- load

	/**
	 * Lazy load (issue #146): marks the resource loaded and remembers the options, but does
	 * <em>not</em> run the collection-wide {@code find()}. The full population is deferred to
	 * the first {@link #getContents()} access.
	 * <p>
	 * This is what keeps demand-load-driven proxy resolution bounded. EMF resolves a proxy via
	 * {@code ResourceSet.getEObject(uri, true)} → {@code getResource(trimFragment, true)} →
	 * {@code demandLoad} → {@code load()}. With eager loading that step materialised the entire
	 * target collection before the fragment — which already carries the target id — was ever
	 * looked at, so referencing one object in a million-document collection read all million.
	 * Now {@code load()} is a no-op marker and the keyed find in {@link #getEObject(String)}
	 * does the work. Mirrors the JPA backend's fix for the same defect (issue #17).
	 */
	@Override
	public void load(Map<?, ?> options) throws IOException {
		if (isLoaded) {
			return;
		}
		// Deferring the data must not defer the diagnosis: an unresolvable database (missing
		// authority, unknown alias, withdrawn service) has to surface here as a checked
		// IOException, not later as an unchecked WrappedException out of getContents(). The
		// handle is touched, never queried — MongoDatabase.getName() is a field read on a real
		// database and throws on the factory's unavailable() proxy, so this costs no I/O.
		try {
			database.getName();
		} catch (RuntimeException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE,
					"Failed to load resource: " + e.getMessage(), getURI(), e));
			throw new IOException("Failed to load resource: " + getURI(), e);
		}
		this.loadOptions = options;
		this.loadRequested = true;
		isLoaded = true;
	}

	/**
	 * Returns the contents, running the deferred population on first access of a loaded but
	 * not yet populated resource. Internal callers that must not trigger it — fragment
	 * resolution, the population itself, {@code doUnload} — use {@code super.getContents()}.
	 */
	@Override
	public EList<EObject> getContents() {
		populateIfNeeded();
		return super.getContents();
	}

	/**
	 * Runs the deferred population exactly once. Guarded against re-entrancy so that
	 * {@code super.getContents()} calls made from within the population do not recurse; a
	 * failure surfaces as a {@link WrappedException} because {@link #getContents()} cannot
	 * throw a checked {@link IOException}, and is also recorded in {@link #getErrors()}.
	 */
	private void populateIfNeeded() {
		if (!loadRequested || contentsPopulated || populating) {
			return;
		}
		populating = true;
		try {
			doLoad((InputStream) null, loadOptions);
			contentsPopulated = true;
		} catch (IOException e) {
			throw new WrappedException(e);
		} finally {
			populating = false;
		}
	}

	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		getErrors().clear();
		getWarnings().clear();
		applyIdConfigOption(options);
		String collectionName = getCollectionName(options);
		if (isNull(collectionName)) {
			getWarnings().add(PersistenceDiagnostic.warning(DIAGNOSTIC_SOURCE,
					"Resource URI has no collection segment — nothing to load", getURI()));
			return;
		}
		// Raw access throughout: this IS the population, so it must not re-enter the hook.
		// Objects already attached by a keyed fragment resolution are KEPT — incoming
		// documents carrying an id that is already present are skipped below, so identity
		// survives for anyone already holding a reference (mirrors the JPA backend).
		Set<String> present = new HashSet<>();
		for (EObject existing : super.getContents()) {
			String existingId = CompositeIds.fragment(existing);
			if (nonNull(existingId)) {
				present.add(existingId);
			}
		}
		try {
			MongoCollection<BsonDocument> collection = getCollection(collectionName);
			FindIterable<BsonDocument> find = collection.find();
			String id = getIdSegment();
			EClass eClass = resolveEClass(collectionName, options);
			if (nonNull(id)) {
				find = collection.find(eq(MongoPersistenceConstants.ID_FIELD, toBsonId(id, eClass)));
			}
			int pageSize = Options.getPageSize(options);
			if (pageSize > 0) {
				loadPaginated(collection, find, eClass, pageSize, present);
			} else {
				decodeInto(find, eClass, super.getContents(), present);
			}
		} catch (RuntimeException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, 
					"Failed to load resource: " + e.getMessage(), getURI(), e));
			throw new IOException("Failed to load resource: " + getURI(), e);
		}
	}

	private void loadPaginated(MongoCollection<BsonDocument> collection, FindIterable<BsonDocument> find,
			EClass eClass, int pageSize, Set<String> present) throws IOException {
		int offset = 0;
		while (true) {
			List<EObject> page = new ArrayList<>(pageSize);
			int decoded = decodeInto(find.skip(offset).limit(pageSize), eClass, page, present);
			super.getContents().addAll(page);
			if (decoded < pageSize) {
				return;
			}
			offset += decoded;
		}
	}

	/**
	 * Decodes the cursor into {@code target}, skipping documents whose EMF id is already
	 * attached to this resource. Returns the number of documents seen — not the number added
	 * — so paging keeps advancing correctly when duplicates are skipped.
	 */
	private int decodeInto(FindIterable<BsonDocument> find, EClass eClass, List<EObject> target,
			Set<String> present) throws IOException {
		int seen = 0;
		try (MongoCursor<BsonDocument> cursor = find.iterator()) {
			while (cursor.hasNext()) {
				BsonDocument document = cursor.next();
				seen++;
				BsonValue rawId = document.get(MongoPersistenceConstants.ID_FIELD);
				if (nonNull(rawId) && rawId.isString() && present.contains(rawId.asString().getValue())) {
					continue;
				}
				EObject eObject = decode(document, eClass);
				if (nonNull(eObject)) {
					target.add(eObject);
				}
			}
		}
		return seen;
	}

	// ------------------------------------------------------------------- save

	@Override
	public void save(Map<?, ?> options) throws IOException {
		doSave((OutputStream) null, options);
	}

	@Override
	protected void doSave(OutputStream outputStream, Map<?, ?> options) throws IOException {
		getErrors().clear();
		getWarnings().clear();
		applyIdConfigOption(options);
		String collectionName = getCollectionName(options);
		if (isNull(collectionName)) {
			getWarnings().add(PersistenceDiagnostic.warning(DIAGNOSTIC_SOURCE, 
					"Resource URI has no collection segment — nothing to save", getURI()));
			return;
		}
		try {
			MongoCollection<BsonDocument> collection = getCollection(collectionName);
			// snapshot: encoding may resolve proxies, and a keyed-find resolution against
			// this very resource attaches its result to the contents (issue #107)
			List<EObject> roots = List.copyOf(getContents());
			List<WriteModel<BsonDocument>> writes = writeModels(roots);
			if (!writes.isEmpty()) {
				collection.bulkWrite(writes);
			}
			// After the roots are written: whatever they no longer own is deleted, and the
			// ownership records are brought in line (issue #139). Deliberately after, so a
			// crash leaves a recoverable orphan rather than a root referencing a deleted
			// document — and the records make that orphan re-derivable for the sweep (#140).
			reconcileOwnership(roots, collectionName);
		} catch (RuntimeException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, 
					"Failed to save resource: " + e.getMessage(), getURI(), e));
			throw new IOException("Failed to save resource: " + getURI(), e);
		}
	}

	/** The upsert write models of the save pipeline, shared with bracketed inserts (issue #112). */
	private List<WriteModel<BsonDocument>> writeModels(List<EObject> objects) throws IOException {
		List<WriteModel<BsonDocument>> writes = new ArrayList<>(objects.size());
		ReplaceOptions upsert = new ReplaceOptions().upsert(true);
		for (EObject eObject : objects) {
			BsonValue id = ensureId(eObject);
			BsonDocument document = encode(eObject);
			document.put(MongoPersistenceConstants.ID_FIELD, id);
			writes.add(new ReplaceOneModel<>(
					eq(MongoPersistenceConstants.ID_FIELD, id), document, upsert));
		}
		return writes;
	}

	// -------------------------------------------------- delete / count / exist

	@Override
	public void delete(Map<?, ?> options) throws IOException {
		getErrors().clear();
		getWarnings().clear();
		String collectionName = getCollectionName(options);
		if (isNull(collectionName)) {
			return;
		}
		try {
			MongoCollection<BsonDocument> collection = getCollection(collectionName);
			// Collect the owned cross-document documents BEFORE anything is removed: they are
			// only discoverable from the roots that own them, so deleting the roots first
			// would lose the information (issue #138).
			Map<String, Set<BsonValue>> owned = new LinkedHashMap<>();
			for (EObject eObject : getContents()) {
				collectOwnedDocuments(eObject, owned);
			}
			List<BsonValue> deletedIds = new ArrayList<>();
			for (EObject eObject : getContents()) {
				BsonValue id = extractId(eObject);
				if (nonNull(id)) {
					deletedIds.add(id);
					collection.deleteOne(eq(MongoPersistenceConstants.ID_FIELD, id));
				}
			}
			// Owned children go after their roots: a crash then leaves a recoverable orphan
			// rather than a root pointing at documents that no longer exist. One deleteMany
			// per collection, never one per child.
			deleteOwned(owned);
			// the roots are gone, so their ownership bookkeeping goes too (issue #139)
			removeOwnershipRecords(owned);
			removeOwnershipOf(deletedIds, collectionName);
			getContents().clear();
		} catch (RuntimeException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, 
					"Failed to delete resource: " + e.getMessage(), getURI(), e));
			throw new IOException("Failed to delete resource: " + getURI(), e);
		}
	}

	@Override
	public long count() throws IOException {
		return count(null);
	}

	@Override
	public long count(Map<?, ?> options) throws IOException {
		String collectionName = getCollectionName(options);
		if (isNull(collectionName)) {
			return 0;
		}
		try {
			return getCollection(collectionName).countDocuments();
		} catch (RuntimeException e) {
			throw new IOException("Failed to count collection '" + collectionName + "'", e);
		}
	}

	@Override
	public boolean exist() throws IOException {
		return exist(null);
	}

	@Override
	public boolean exist(Map<?, ?> options) throws IOException {
		String collectionName = getCollectionName(options);
		if (isNull(collectionName)) {
			return false;
		}
		try {
			return getCollection(collectionName)
					.countDocuments(new BsonDocument(), new CountOptions().limit(1)) > 0;
		} catch (RuntimeException e) {
			throw new IOException("Failed to check existence of collection '" + collectionName + "'", e);
		}
	}

	// -------------------------------------------------------------- querying

	/**
	 * Overrides the {@link QueryProcessor} used by {@link #query(Query, Map, Map)} —
	 * intended for OSGi wiring through the factory; defaults to a local
	 * {@link MongoQueryProcessor} instance (processors are stateless).
	 */
	public void setQueryProcessor(QueryProcessor queryProcessor) {
		this.queryProcessor = requireNonNull(queryProcessor, "queryProcessor must not be null");
	}

	/** The effective processor — package-private so tests can assert the wiring. */
	QueryProcessor queryProcessor() {
		return queryProcessor;
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
		String collectionName = getCollectionName(options);
		if (isNull(collectionName)) {
			throw new IOException("Resource URI has no collection segment — cannot query: " + getURI());
		}
		EClass eClass = resolveEClass(collectionName, options);
		MongoQueryPlan plan;
		try {
			plan = MongoQueries.translate(queryProcessor, query, eClass, parameters, queryOptions(options));
		} catch (QueryException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, "Query rejected: " + e.getMessage(), getURI(), e));
			throw new IOException("Query rejected for collection '" + collectionName + "': " + e.getMessage(), e);
		}
		try {
			return execute(plan, collectionName, eClass, options);
		} catch (RuntimeException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, "Query execution failed: " + e.getMessage(), getURI(), e));
			throw new IOException("Query execution failed on collection '" + collectionName + "'", e);
		}
	}

	private QueryResult execute(MongoQueryPlan plan, String collectionName, EClass eClass, Map<?, ?> options) {
		MongoCollection<BsonDocument> collection = getCollection(collectionName);
		Bson filter = plan.filter() == null ? Filters.empty() : plan.filter();
		if (plan.shape() == QueryShape.COUNT) {
			return QueryResults.count(collection.countDocuments(filter));
		}
		if (plan.aggregation()) {
			AggregateIterable<BsonDocument> aggregate = collection.aggregate(plan.pipeline());
			MongoCursor<BsonDocument> cursor = aggregate.iterator();
			Stream<QueryResultRow> rows = cursorStream(cursor)
					.map(document -> toRow(document, plan));
			return QueryResults.rows(plan.shape(), rows);
		}
		FindIterable<BsonDocument> find = collection.find(filter);
		if (plan.sort() != null) {
			find = find.sort(plan.sort());
		}
		if (plan.skip() > 0) {
			find = find.skip(plan.skip());
		}
		if (plan.limit() > 0) {
			find = find.limit(plan.limit());
		}
		int pageSize = Options.getPageSize(options);
		if (pageSize > 0) {
			find = find.batchSize(pageSize);
		}
		MongoCursor<BsonDocument> cursor = find.iterator();
		Stream<EObject> objects = cursorStream(cursor)
				.map(document -> decodeUnchecked(document, eClass))
				.filter(java.util.Objects::nonNull);
		return QueryResults.objects(objects);
	}

	// ------------------------------------------------------- persisted queries

	/** The query catalog collection (concept.md §14 saveQuery): _id = name, xmi = payload. */
	static final String QUERY_CATALOG_COLLECTION = "fennec.queries";

	private void saveNamedQuery(String name, Query query) throws IOException {
		try {
			BsonDocument document = new BsonDocument();
			document.put("_id", new BsonString(name));
			document.put("xmi", new BsonString(PersistedQueries.toXmi(query)));
			getCollection(QUERY_CATALOG_COLLECTION).replaceOne(eq("_id", new BsonString(name)),
					document, new ReplaceOptions().upsert(true));
		} catch (QueryException | RuntimeException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, 
					"Failed to persist query '" + name + "': " + e.getMessage(), getURI(), e));
			throw new IOException("Failed to persist query '" + name + "'", e);
		}
	}

	private Query loadNamedQuery(String name) throws IOException {
		BsonDocument document;
		try {
			document = getCollection(QUERY_CATALOG_COLLECTION)
					.find(eq("_id", new BsonString(name))).first();
		} catch (RuntimeException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, 
					"Failed to load persisted query '" + name + "': " + e.getMessage(), getURI(), e));
			throw new IOException("Failed to load persisted query '" + name + "'", e);
		}
		if (isNull(document) || !document.containsKey("xmi")) {
			throw new IOException("No persisted query named '" + name + "'");
		}
		try {
			return PersistedQueries.fromXmi(name, document.getString("xmi").getValue(), packageRegistry());
		} catch (QueryException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, 
					"Cannot load persisted query '" + name + "': " + e.getMessage(), getURI(), e));
			throw new IOException("Cannot load persisted query '" + name + "': " + e.getMessage(), e);
		}
	}

	private EPackage.Registry packageRegistry() {
		return isNull(getResourceSet()) ? EPackage.Registry.INSTANCE
				: getResourceSet().getPackageRegistry();
	}

	private Stream<BsonDocument> cursorStream(MongoCursor<BsonDocument> cursor) {
		Spliterator<BsonDocument> documents = Spliterators.spliteratorUnknownSize(
				cursor, Spliterator.ORDERED | Spliterator.NONNULL);
		return StreamSupport.stream(documents, false).onClose(cursor::close);
	}

	private QueryResultRow toRow(BsonDocument document, MongoQueryPlan plan) {
		List<Object> values = new ArrayList<>(plan.rowKeys().size());
		for (String key : plan.rowKeys()) {
			values.add(BsonValues.toJava(document.get(key)));
		}
		return QueryResultRows.of(plan.rowAliases(), values);
	}

	// -------------------------------------------------------------- commands

	@Override
	public long execute(Command command) throws IOException {
		requireNonNull(command, "command must not be null");
		if (command instanceof InsertCommand insert) {
			for (EObject payload : insert.getObjects()) {
				ensureCommandSupported(CommandFeature.INSERT, payload.eClass());
			}
			return executeInsert(insert);
		}
		if (command instanceof DeleteCommand delete) {
			ensureCommandSupported(CommandFeature.DELETE_BY_SELECTOR, delete.getSelector().getFrom());
			return executeDelete(delete);
		}
		if (command instanceof UpdateCommand update) {
			ensureCommandSupported(CommandFeature.UPDATE_BY_SELECTOR, update.getSelector().getFrom());
			return executeUpdate(update);
		}
		throw new IOException("Unsupported command " + command.eClass().getName());
	}

	/**
	 * The write commands this resource serves (issue #114) — per resource instance:
	 * {@code TRANSACTION_BRACKET} depends on the deployment (replica set/mongos) and
	 * the factory-injected session-capable client, not just the backend.
	 */
	@Override
	public CommandCapabilities capabilities() {
		CommandCapabilitiesBuilder builder = CommandCapabilitiesBuilder.create()
				.support(CommandFeature.INSERT, CommandFeature.DELETE_BY_SELECTOR,
						CommandFeature.UPDATE_BY_SELECTOR);
		if (nonNull(client) && transactionalDeployment()) {
			builder.support(CommandFeature.TRANSACTION_BRACKET);
		}
		return builder.build();
	}

	/** Refuses an undeclared command feature before any work (issue #114). */
	private void ensureCommandSupported(CommandFeature feature, EClass target) throws IOException {
		if (!capabilities().supports(feature, target)) {
			throw refused(feature, "for EClass '" + target.getName() + "'");
		}
	}

	/**
	 * The issue-#114 refusal contract: a Diagnostic naming the {@link CommandFeature}
	 * lands in the resource errors before the IOException — 'refused because this
	 * backend cannot' stays distinguishable from 'failed while trying'.
	 */
	private IOException refused(CommandFeature feature, String detail) {
		String message = "Command feature " + feature.getName() + " is not supported by this"
				+ " mongo resource: " + detail;
		getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, message, getURI(), null));
		return new IOException(message);
	}

	/** Injects the session-capable client — wired by the {@code MongoResourceFactory} (issue #112). */
	public void setClient(MongoClient client) {
		this.client = client;
	}

	/**
	 * Opens a command bracket (issue #112) over a {@link ClientSession} transaction.
	 * Requires a session-capable {@link MongoClient} (factory-injected) and a
	 * replica-set/mongos deployment — both are probed and refused honestly, no
	 * pretend-bracket that commits per command.
	 */
	@Override
	public CommandTransaction begin() throws IOException {
		if (nonNull(activeTransaction)) {
			throw new IOException("A command transaction is already open on this resource"
					+ " — commit or close it before opening another");
		}
		if (isNull(client)) {
			throw refused(CommandFeature.TRANSACTION_BRACKET, "no session-capable MongoClient"
					+ " — construct the MongoResourceFactory with the client (issue #112)");
		}
		if (!transactionalDeployment()) {
			throw refused(CommandFeature.TRANSACTION_BRACKET, "transactions require a replica set"
					+ " or mongos — this MongoDB deployment is standalone (issue #112)");
		}
		ClientSession session;
		try {
			session = client.startSession();
			session.startTransaction();
		} catch (RuntimeException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE,
					"Cannot open command transaction: " + e.getMessage(), getURI(), e));
			throw new IOException("Cannot open command transaction: " + e.getMessage(), e);
		}
		activeTransaction = new MongoCommandTransaction(session);
		return activeTransaction;
	}

	/**
	 * The hello-probe of issue #112, cached on success: replica set ({@code setName})
	 * or mongos ({@code isdbgrid}) unlock multi-document transactions. A failed probe
	 * is NOT cached — a transient error must not stick as 'standalone'.
	 */
	private boolean transactionalDeployment() {
		Boolean probed = transactionalDeployment;
		if (isNull(probed)) {
			try {
				BsonDocument hello = database.runCommand(new BsonDocument("hello", new BsonInt32(1)),
						BsonDocument.class);
				probed = hello.containsKey("setName")
						|| "isdbgrid".equals(hello.containsKey("msg") ? hello.getString("msg").getValue() : null);
				transactionalDeployment = probed;
			} catch (RuntimeException e) {
				LOG.log(Level.FINE, "Transaction-support probe failed", e);
				return false;
			}
		}
		return probed;
	}

	/** The session the open bracket rides, or {@code null} outside a bracket. */
	private ClientSession activeSession() {
		return nonNull(activeTransaction) ? activeTransaction.session : null;
	}

	/** The Mongo command bracket (issue #112): one client-session transaction per bracket. */
	private final class MongoCommandTransaction implements CommandTransaction {

		private final ClientSession session;
		private boolean closed;

		private MongoCommandTransaction(ClientSession session) {
			this.session = session;
		}

		@Override
		public void commit() throws IOException {
			if (closed) {
				throw new IOException("The command transaction is already closed");
			}
			try {
				session.commitTransaction();
			} catch (RuntimeException e) {
				abortQuietly();
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
			abortQuietly();
			cleanup();
		}

		@Override
		public void close() {
			if (!closed) {
				rollback();
			}
		}

		private void abortQuietly() {
			try {
				if (session.hasActiveTransaction()) {
					session.abortTransaction();
				}
			} catch (RuntimeException e) {
				LOG.log(Level.FINE, "Command transaction abort failed", e);
			}
		}

		private void cleanup() {
			closed = true;
			try {
				session.close();
			} catch (RuntimeException e) {
				LOG.log(Level.FINE, "Closing the client session failed", e);
			}
			activeTransaction = null;
		}
	}

	/**
	 * Insert = the resource's save semantics over copies of the contained payload.
	 * Non-containment references to EXISTING targets bind by id (issue #107): verified
	 * via keyed find and rebound as canonical proxies — without this, encode would
	 * serialise a detached stub as its bare TYPE URI and silently lose the id.
	 */
	private long executeInsert(InsertCommand insert) throws IOException {
		EcoreUtil.Copier copier = new EcoreUtil.Copier();
		List<EObject> copies = new ArrayList<>(copier.copyAll(insert.getObjects()));
		copier.copyReferences();
		try {
			ChangeTemplates.bindInsertReferences(copier, referenceResolver());
		} catch (QueryException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, "Insert rejected: " + e.getMessage(), getURI(), e));
			throw new IOException("Insert rejected: " + e.getMessage(), e);
		}
		ClientSession session = activeSession();
		if (nonNull(session)) {
			// bracketed insert (issue #112): write through the bracket's session
			String collectionName = getCollectionName(null);
			if (isNull(collectionName)) {
				throw new IOException("Resource URI has no collection segment — cannot insert: " + getURI());
			}
			try {
				List<WriteModel<BsonDocument>> writes = writeModels(copies);
				if (!writes.isEmpty()) {
					getCollection(collectionName).bulkWrite(session, writes);
				}
				return copies.size();
			} catch (RuntimeException e) {
				getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, "Insert failed: " + e.getMessage(), getURI(), e));
				throw new IOException("Insert failed on collection '" + collectionName + "'", e);
			}
		}
		getContents().addAll(copies);
		try {
			save(null);
		} finally {
			getContents().clear();
		}
		return copies.size();
	}

	/** Delete = selector-scoped deleteMany (concept §14: Delete = query selector). */
	private long executeDelete(DeleteCommand delete) throws IOException {
		String collectionName = getCollectionName(null);
		if (isNull(collectionName)) {
			throw new IOException("Resource URI has no collection segment — cannot delete: " + getURI());
		}
		MongoQueryPlan plan;
		try {
			guardPlainSelector(delete.getSelector());
			plan = MongoQueries.translate(queryProcessor, delete.getSelector(),
					delete.getSelector().getFrom(), null, queryOptions(null));
		} catch (QueryException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, "Delete selector rejected: " + e.getMessage(), getURI(), e));
			throw new IOException("Delete selector rejected: " + e.getMessage(), e);
		}
		try {
			Bson filter = plan.filter() == null ? Filters.empty() : plan.filter();
			ClientSession session = activeSession();
			MongoCollection<BsonDocument> collection = getCollection(collectionName);
			return (nonNull(session)
					? collection.deleteMany(session, filter)
					: collection.deleteMany(filter)).getDeletedCount();
		} catch (RuntimeException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, "Delete failed: " + e.getMessage(), getURI(), e));
			throw new IOException("Delete failed on collection '" + collectionName + "'", e);
		}
	}

	/**
	 * Update = selector + ChangeSet template per match (concept §14, patch-apply engine
	 * §18.1): decode each matched document, patch it, replace it under its {@code _id}.
	 */
	private long executeUpdate(UpdateCommand update) throws IOException {
		String collectionName = getCollectionName(null);
		if (isNull(collectionName)) {
			throw new IOException("Resource URI has no collection segment — cannot update: " + getURI());
		}
		EClass eClass = update.getSelector().getFrom();
		MongoQueryPlan plan;
		try {
			guardPlainSelector(update.getSelector());
			ChangeTemplates.validate(update.getTemplate(), eClass);
			plan = MongoQueries.translate(queryProcessor, update.getSelector(), eClass, null, queryOptions(null));
		} catch (QueryException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, "Update rejected: " + e.getMessage(), getURI(), e));
			throw new IOException("Update rejected: " + e.getMessage(), e);
		}
		try {
			Bson filter = plan.filter() == null ? Filters.empty() : plan.filter();
			ClientSession session = activeSession();
			MongoCollection<BsonDocument> collection = getCollection(collectionName);
			long applied = 0;
			try (MongoCursor<BsonDocument> cursor = (nonNull(session)
					? collection.find(session, filter)
					: collection.find(filter)).iterator()) {
				while (cursor.hasNext()) {
					BsonDocument document = cursor.next();
					EObject eObject = decodeUnchecked(document, eClass);
					if (isNull(eObject)) {
						continue;
					}
					ChangeTemplates.apply(update.getTemplate(), eObject, referenceResolver());
					BsonValue id = document.get(MongoPersistenceConstants.ID_FIELD);
					BsonDocument replacement = encode(eObject);
					replacement.put(MongoPersistenceConstants.ID_FIELD, id);
					if (nonNull(session)) {
						collection.replaceOne(session, eq(MongoPersistenceConstants.ID_FIELD, id), replacement);
					} else {
						collection.replaceOne(eq(MongoPersistenceConstants.ID_FIELD, id), replacement);
					}
					applied++;
				}
			}
			return applied;
		} catch (QueryException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, "Update failed: " + e.getMessage(), getURI(), e));
			throw new IOException("Update failed on collection '" + collectionName + "': " + e.getMessage(), e);
		} catch (RuntimeException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE, "Update failed: " + e.getMessage(), getURI(), e));
			throw new IOException("Update failed on collection '" + collectionName + "'", e);
		}
	}

	/**
	 * The Mongo {@link ReferenceResolver} (issue #107): verifies the target exists via a
	 * keyed find in the target type's collection, then binds a canonical proxy
	 * ({@code mongodb://<db>/<Type>#<id>}) — exactly the shape decode produces for
	 * cross-document references, so encode writes the reference correctly.
	 */
	private ReferenceResolver referenceResolver() {
		return (reference, id) -> {
			EClass targetType = reference.getEReferenceType();
			if (targetType.isAbstract() || targetType.isInterface()) {
				throw new QueryException("Reference target type '" + targetType.getName()
						+ "' is abstract — cannot bind by id");
			}
			try {
				// inside a bracket the keyed find rides the session, so targets inserted
				// earlier in the same (uncommitted) transaction resolve (issue #112)
				ClientSession session = activeSession();
				MongoCollection<BsonDocument> targets = getCollection(targetType.getName());
				Bson byId = eq(MongoPersistenceConstants.ID_FIELD, toBsonId(id, targetType));
				BsonDocument existing = (nonNull(session)
						? targets.find(session, byId)
						: targets.find(byId)).first();
				if (isNull(existing)) {
					return null;
				}
			} catch (RuntimeException e) {
				throw new QueryException("Keyed find for '" + targetType.getName() + "' id '" + id
						+ "' failed: " + e.getMessage(), e);
			}
			EObject proxy = EcoreUtil.create(targetType);
			((InternalEObject) proxy).eSetProxyURI(toProxyUri(id, targetType));
			return proxy;
		};
	}

	/** Command selectors are plain filters — everything shape-changing is refused. */
	private static void guardPlainSelector(org.eclipse.fennec.model.query.Query selector) throws QueryException {
		if (!selector.getSelect().isEmpty() || selector.getApply() != null || !selector.getOrderBy().isEmpty()
				|| selector.getTop() > 0 || selector.getSkip() > 0 || selector.isDistinct()
				|| selector.isCountOnly() || !selector.getExpand().isEmpty()) {
			throw new QueryException(
					"Command selectors must be plain filters — projection/aggregation/ordering/paging are not allowed");
		}
	}

	// -------------------------------------------------------------- streaming

	@Override
	public Stream<EObject> stream() throws IOException {
		return stream(null);
	}

	@Override
	public Stream<EObject> stream(Map<?, ?> options) throws IOException {
		String collectionName = getCollectionName(options);
		if (isNull(collectionName)) {
			return Stream.empty();
		}
		EClass eClass = resolveEClass(collectionName, options);
		try {
			FindIterable<BsonDocument> find = getCollection(collectionName).find();
			int pageSize = Options.getPageSize(options);
			if (pageSize > 0) {
				find = find.batchSize(pageSize);
			}
			MongoCursor<BsonDocument> cursor = find.iterator();
			Spliterator<BsonDocument> documents = Spliterators.spliteratorUnknownSize(
					cursor, Spliterator.ORDERED | Spliterator.NONNULL);
			return StreamSupport.stream(documents, false)
					.map(document -> decodeUnchecked(document, eClass))
					.filter(java.util.Objects::nonNull)
					.onClose(cursor::close);
		} catch (RuntimeException e) {
			throw new IOException("Failed to open stream on collection '" + collectionName + "'", e);
		}
	}

	private EObject decodeUnchecked(BsonDocument document, EClass eClass) {
		try {
			return decode(document, eClass);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void updateDefaultOptions(Map<Object, Object> options, ActionType... types) {
		// No-op for the initial implementation (mirrors JPAResourceImpl)
	}

	@Override
	public void close() throws Exception {
		unload();
	}

	@Override
	protected void doUnload() {
		isLoaded = false;
		loadRequested = false;
		contentsPopulated = false;
		loadOptions = null;
		super.getContents().clear();
	}

	// ------------------------------------------------------- containment ownership

	/**
	 * Collects the documents a root owns <b>outside its own document</b>, grouped by
	 * collection: cross-document containment children, transitively (issue #138).
	 * <p>
	 * Containment is ownership, but a cross-document child lives in its own document, so
	 * deleting the root's document alone leaves it behind. The collection is deliberately done
	 * from the object graph rather than from the stored document, and deliberately without
	 * resolving anything that does not have to be resolved:
	 * <ul>
	 * <li>an <b>unresolved proxy</b> already carries collection and id in its URI, so it is
	 *     recorded without a single query;</li>
	 * <li>a <b>resolved child in another resource</b> is recorded from that resource's URI;</li>
	 * <li>an <b>embedded child</b> is not recorded — it goes with the root's document — but it
	 *     is walked, because it may own cross-document children of its own.</li>
	 * </ul>
	 * Recursion into a target only happens when the target's type can own containment at all,
	 * which the metamodel answers statically. For a leaf child type the whole collection
	 * therefore costs zero reads.
	 */
	private void collectOwnedDocuments(EObject object, Map<String, Set<BsonValue>> owned) {
		for (EReference ref : object.eClass().getEAllReferences()) {
			if (!ref.isContainment() || ref.isDerived() || ref.isTransient()) {
				continue;
			}
			Object raw = ((InternalEObject) object).eGet(ref, false);
			if (isNull(raw)) {
				continue;
			}
			if (ref.isMany()) {
				for (Object element : (List<?>) raw) {
					collectOwnedChild(element, ref, owned);
				}
			} else {
				collectOwnedChild(raw, ref, owned);
			}
		}
	}

	private void collectOwnedChild(Object value, EReference reference, Map<String, Set<BsonValue>> owned) {
		if (!(value instanceof EObject child)) {
			return;
		}
		URI documentUri = ownedDocumentUri(child);
		if (isNull(documentUri)) {
			// embedded: part of the root's own document, but it may own children itself
			collectOwnedDocuments(child, owned);
			return;
		}
		String collectionName = collectionOf(documentUri);
		String id = documentUri.fragment();
		EClass targetType = reference.getEReferenceType();
		if (nonNull(collectionName) && nonNull(id)) {
			owned.computeIfAbsent(collectionName, key -> new LinkedHashSet<>())
					.add(toBsonId(id, targetType));
		}
		if (!ownsContainment(targetType)) {
			// leaf type — nothing deeper can exist, so it need not be read at all
			return;
		}
		// Deeper cross-document ownership is only visible in the child's own document, so this
		// is the one case that costs a read. Resolving through the ResourceSet keeps identity
		// and reuses the target resource.
		EObject resolved = child.eIsProxy() ? EcoreUtil.resolve(child, getResourceSet()) : child;
		if (!resolved.eIsProxy()) {
			collectOwnedDocuments(resolved, owned);
		}
	}

	/**
	 * The URI of the child's <em>own</em> document, or {@code null} when it has none — i.e.
	 * when it is embedded in its parent's document.
	 */
	private URI ownedDocumentUri(EObject child) {
		if (child.eIsProxy()) {
			return ((InternalEObject) child).eProxyURI();
		}
		Resource childResource = ((InternalEObject) child).eDirectResource();
		if (isNull(childResource) || childResource == this) {
			return null;
		}
		String fragment = childResource.getURIFragment(child);
		return isNull(fragment) ? null : childResource.getURI().appendFragment(fragment);
	}

	/** Whether instances of this type can own containment at all — a static metamodel question. */
	private static boolean ownsContainment(EClass eClass) {
		return eClass.getEAllReferences().stream().anyMatch(EReference::isContainment);
	}

	/** The collection a document URI addresses: {@code mongodb://<db>/<collection>[#id]}. */
	private static String collectionOf(URI documentUri) {
		URI trimmed = documentUri.trimFragment();
		return trimmed.segmentCount() > 0 ? trimmed.segment(0) : null;
	}

	/**
	 * Deletes the collected owned documents, one {@code deleteMany} with an {@code $in} per
	 * collection — never one delete per child.
	 */
	private void deleteOwned(Map<String, Set<BsonValue>> owned) {
		for (Map.Entry<String, Set<BsonValue>> entry : owned.entrySet()) {
			if (entry.getValue().isEmpty()) {
				continue;
			}
			getCollection(entry.getKey())
					.deleteMany(Filters.in(MongoPersistenceConstants.ID_FIELD, entry.getValue()));
		}
	}

	/**
	 * Reconciles the ownership records of the given roots and deletes what they no longer own
	 * (issue #139).
	 * <p>
	 * The delete path can rediscover a root's owned documents by walking it (#138); an
	 * <b>update</b> cannot — a dropped subtree is simply gone from the graph, and nothing in the
	 * new document says it was ever there. Transitively it is worse: the orphan may hang off an
	 * intermediate node that no longer exists, so no future save of that root could ever notice.
	 * <p>
	 * Hence a record per owned child document, in {@value #OWNERSHIP_COLLECTION}:
	 * <pre>{ _id: { c: &lt;childCollection&gt;, id: &lt;childId&gt; }, owner: { c: …, id: … } }</pre>
	 * The child is the primary key, which makes the store enforce EMF's single-container
	 * invariant — a child cannot have two owners — and makes <b>re-parenting</b> correct for
	 * free: when another root takes the child over, its save rewrites the record's owner, and
	 * the former owner's reconciliation no longer sees the child as its own and leaves it alone.
	 * A read-before-write diff of the stored document cannot do that; it would see "gone from
	 * me" and delete a child that now belongs elsewhere.
	 * <p>
	 * Cost: one indexed query and one bulk write per save, and only for types that can own
	 * containment at all — a static metamodel question. Types without containment references
	 * skip this entirely.
	 */
	private void reconcileOwnership(List<EObject> roots, String collectionName) {
		List<EObject> owners = roots.stream().filter(root -> ownsContainment(root.eClass())).toList();
		if (owners.isEmpty()) {
			return;
		}
		Map<BsonValue, Map<String, Set<BsonValue>>> ownedByRoot = new LinkedHashMap<>();
		List<BsonValue> ownerIds = new ArrayList<>();
		for (EObject root : owners) {
			BsonValue ownerId = extractId(root);
			if (isNull(ownerId)) {
				continue;
			}
			ownerIds.add(ownerId);
			Map<String, Set<BsonValue>> owned = new LinkedHashMap<>();
			collectOwnedDocuments(root, owned);
			ownedByRoot.put(ownerId, owned);
		}
		if (ownerIds.isEmpty()) {
			return;
		}
		MongoCollection<BsonDocument> records = getCollection(OWNERSHIP_COLLECTION);
		// one query for every root of this save, not one per root
		Map<BsonValue, Map<String, Set<BsonValue>>> storedByRoot = new LinkedHashMap<>();
		try (MongoCursor<BsonDocument> cursor = records
				.find(Filters.and(eq("owner.c", collectionName), Filters.in("owner.id", ownerIds)))
				.iterator()) {
			while (cursor.hasNext()) {
				BsonDocument record = cursor.next();
				BsonDocument key = record.getDocument(MongoPersistenceConstants.ID_FIELD);
				BsonValue ownerId = record.getDocument("owner").get("id");
				storedByRoot.computeIfAbsent(ownerId, k -> new LinkedHashMap<>())
						.computeIfAbsent(key.getString("c").getValue(), k -> new LinkedHashSet<>())
						.add(key.get("id"));
			}
		}

		// The union across ALL roots of this save, not per root: a child handed from one root
		// to another within the same save is re-parented, not orphaned, and the losing root
		// must not delete it (issue #139). Across saves the same case is already correct,
		// because the record's owner has been rewritten and the former owner's query no longer
		// returns the child at all.
		Map<String, Set<BsonValue>> stillOwned = new LinkedHashMap<>();
		ownedByRoot.values().forEach(owned -> owned.forEach((childCollection, ids) ->
				stillOwned.computeIfAbsent(childCollection, k -> new LinkedHashSet<>()).addAll(ids)));

		Map<String, Set<BsonValue>> orphans = new LinkedHashMap<>();
		List<WriteModel<BsonDocument>> recordWrites = new ArrayList<>();
		ReplaceOptions upsert = new ReplaceOptions().upsert(true);
		for (Map.Entry<BsonValue, Map<String, Set<BsonValue>>> entry : ownedByRoot.entrySet()) {
			BsonValue ownerId = entry.getKey();
			Map<String, Set<BsonValue>> owned = entry.getValue();
			Map<String, Set<BsonValue>> stored = storedByRoot.getOrDefault(ownerId, Map.of());
			// no longer owned by anyone in this save -> the child document and its record go
			stored.forEach((childCollection, ids) -> {
				for (BsonValue id : ids) {
					if (!stillOwned.getOrDefault(childCollection, Set.of()).contains(id)) {
						orphans.computeIfAbsent(childCollection, k -> new LinkedHashSet<>()).add(id);
					}
				}
			});
			owned.forEach((childCollection, ids) -> {
				for (BsonValue id : ids) {
					BsonDocument key = new BsonDocument("c", new BsonString(childCollection))
							.append("id", id);
					BsonDocument record = new BsonDocument(MongoPersistenceConstants.ID_FIELD, key)
							.append("owner", new BsonDocument("c", new BsonString(collectionName))
									.append("id", ownerId));
					recordWrites.add(new ReplaceOneModel<>(
							eq(MongoPersistenceConstants.ID_FIELD, key), record, upsert));
				}
			});
		}
		deleteOwned(orphans);
		removeOwnershipRecords(orphans);
		if (!recordWrites.isEmpty()) {
			records.bulkWrite(recordWrites);
		}
	}

	/** Drops the ownership records of the given child documents. */
	private void removeOwnershipRecords(Map<String, Set<BsonValue>> children) {
		List<BsonDocument> keys = new ArrayList<>();
		children.forEach((childCollection, ids) -> ids.forEach(id -> keys.add(
				new BsonDocument("c", new BsonString(childCollection)).append("id", id))));
		if (!keys.isEmpty()) {
			getCollection(OWNERSHIP_COLLECTION)
					.deleteMany(Filters.in(MongoPersistenceConstants.ID_FIELD, keys));
		}
	}

	/** Drops the ownership records owned by the given roots of this collection. */
	private void removeOwnershipOf(List<BsonValue> ownerIds, String collectionName) {
		if (ownerIds.isEmpty()) {
			return;
		}
		getCollection(OWNERSHIP_COLLECTION).deleteMany(
				Filters.and(eq("owner.c", collectionName), Filters.in("owner.id", ownerIds)));
	}

	/**
	 * The convergence backstop of issue #140: reclaims children whose owner in this
	 * collection no longer claims them.
	 * <p>
	 * Two shapes of orphan, both re-derivable from the records alone, which is why a repeated
	 * run is idempotent:
	 * <ul>
	 * <li>the <b>owner document is gone</b> — an interrupted delete removed the root but not
	 *     its children;</li>
	 * <li>the <b>owner exists but no longer references the child</b> — an interrupted update
	 *     replaced the root without the subtree.</li>
	 * </ul>
	 * Ordered so the cheap discriminator runs first: one query for the records of this
	 * collection, one for which of those owners still exist, and only the surviving owners are
	 * read and walked. Deletion is one {@code deleteMany} per collection.
	 */
	@Override
	public long sweepOwnership() throws IOException {
		String collectionName = getCollectionName(null);
		if (isNull(collectionName)) {
			return 0;
		}
		try {
			MongoCollection<BsonDocument> records = getCollection(OWNERSHIP_COLLECTION);
			Map<BsonValue, Map<String, Set<BsonValue>>> recordedByOwner = new LinkedHashMap<>();
			try (MongoCursor<BsonDocument> cursor =
					records.find(eq("owner.c", collectionName)).iterator()) {
				while (cursor.hasNext()) {
					BsonDocument record = cursor.next();
					BsonDocument key = record.getDocument(MongoPersistenceConstants.ID_FIELD);
					recordedByOwner
							.computeIfAbsent(record.getDocument("owner").get("id"),
									k -> new LinkedHashMap<>())
							.computeIfAbsent(key.getString("c").getValue(),
									k -> new LinkedHashSet<>())
							.add(key.get("id"));
				}
			}
			if (recordedByOwner.isEmpty()) {
				return 0;
			}

			MongoCollection<BsonDocument> collection = getCollection(collectionName);
			EClass eClass = resolveEClass(collectionName, null);
			Map<String, Set<BsonValue>> orphans = new LinkedHashMap<>();
			// which owners still exist — one query, so a vanished root is settled without
			// reading anything
			Set<BsonValue> existingOwners = new LinkedHashSet<>();
			try (MongoCursor<BsonDocument> cursor = collection
					.find(Filters.in(MongoPersistenceConstants.ID_FIELD, recordedByOwner.keySet()))
					.iterator()) {
				while (cursor.hasNext()) {
					BsonDocument ownerDocument = cursor.next();
					BsonValue ownerId = ownerDocument.get(MongoPersistenceConstants.ID_FIELD);
					existingOwners.add(ownerId);
					// the owner survived, so only what it no longer references is orphaned
					Map<String, Set<BsonValue>> stillOwned = new LinkedHashMap<>();
					EObject owner = decode(ownerDocument, eClass);
					if (nonNull(owner)) {
						collectOwnedDocuments(owner, stillOwned);
					}
					collectReleased(recordedByOwner.get(ownerId), stillOwned, orphans);
				}
			}
			for (Map.Entry<BsonValue, Map<String, Set<BsonValue>>> entry : recordedByOwner.entrySet()) {
				if (!existingOwners.contains(entry.getKey())) {
					// owner gone: everything it was recorded to own is orphaned
					collectReleased(entry.getValue(), Map.of(), orphans);
				}
			}
			long reclaimed = orphans.values().stream().mapToLong(Set::size).sum();
			deleteOwned(orphans);
			removeOwnershipRecords(orphans);
			return reclaimed;
		} catch (RuntimeException e) {
			getErrors().add(PersistenceDiagnostic.error(DIAGNOSTIC_SOURCE,
					"Ownership sweep failed: " + e.getMessage(), getURI(), e));
			throw new IOException("Ownership sweep failed on " + getURI(), e);
		}
	}

	/** Adds every recorded child that {@code stillOwned} does not contain to {@code orphans}. */
	private static void collectReleased(Map<String, Set<BsonValue>> recorded,
			Map<String, Set<BsonValue>> stillOwned, Map<String, Set<BsonValue>> orphans) {
		recorded.forEach((childCollection, ids) -> {
			for (BsonValue id : ids) {
				if (!stillOwned.getOrDefault(childCollection, Set.of()).contains(id)) {
					orphans.computeIfAbsent(childCollection, k -> new LinkedHashSet<>()).add(id);
				}
			}
		});
	}

	// -------------------------------------------------------- proxy resolution

	/**
	 * Resolves proxy fragments. Two fragment shapes are supported:
	 * <ul>
	 * <li>{@code //refName/idAttr/idValue} — the persistence proxy format shared with
	 *     the JPA backend</li>
	 * <li>{@code <idValue>} — a plain id (codec-created proxies use the document id)</li>
	 * </ul>
	 */
	/**
	 * The URI fragment of a contained object is its EMF id — this is what other
	 * resources (and the codec) embed as the reference target, making written
	 * references id-based ({@code mongodb://<db>/<collection>#<id>}).
	 */
	@Override
	public String getURIFragment(EObject eObject) {
		// composite-id types use the k1=v1,k2=v2 shape (issue #109/#110)
		String id = CompositeIds.fragment(eObject);
		return nonNull(id) ? id : super.getURIFragment(eObject);
	}

	@Override
	public EObject getEObject(String uriFragment) {
		if (isNull(uriFragment) || uriFragment.isEmpty()) {
			return super.getEObject(uriFragment);
		}
		// the in-memory lookup comes FIRST (issue #116): it preserves identity for
		// already-loaded objects — including embedded containment children, which a
		// keyed find can never see — where the keyed find would decode a fresh twin
		// and attach it beside the original
		if (uriFragment.startsWith("/") && !uriFragment.startsWith("//")) {
			// XMI-style path fragment ("/0", "/1/@children.0"): positional, so it needs the
			// populated contents by definition — hand it to EMF unchanged.
			return super.getEObject(uriFragment);
		}
		String idValue = uriFragment;
		if (uriFragment.startsWith("//")) {
			// Fragment format: //refName/idAttrName/idValue
			String[] parts = uriFragment.substring(2).split("/");
			if (parts.length < 3) {
				return null;
			}
			idValue = parts[2];
		}
		// The in-memory lookup comes FIRST (issue #116): it preserves identity for objects
		// already present — including embedded containment children, which a keyed find can
		// never see — where the keyed find would decode a fresh twin beside the original.
		// Deliberately NOT via super.getEObject: that routes through getEObjectByID, which
		// iterates getContents() and would populate the whole collection (issue #146) —
		// exactly what the lazy load exists to avoid.
		EObject loaded = findInRawContents(idValue);
		if (nonNull(loaded)) {
			return loaded;
		}
		String collectionName = getCollectionName(null);
		if (isNull(collectionName)) {
			return null;
		}
		EClass eClass = resolveEClass(collectionName, null);
		try {
			BsonDocument document = getCollection(collectionName)
					.find(eq(MongoPersistenceConstants.ID_FIELD, toBsonId(idValue, eClass)))
					.first();
			if (isNull(document)) {
				// No document of its own — so this is very likely an EMBEDDED containment
				// child, whose id lives inside some other document and which no keyed find
				// can ever see (issue #116). That case genuinely needs the documents that
				// might contain it, so fall back to the collection-wide population exactly
				// here, once, rather than paying for it on every resolution (issue #146).
				if (loadRequested && !contentsPopulated) {
					populateIfNeeded();
					return findInRawContents(idValue);
				}
				return null;
			}
			EObject resolved = decode(document, eClass);
			if (nonNull(resolved) && isNull(resolved.eResource())
					&& !super.getContents().contains(resolved)) {
				// Standard EMF pattern: attach the resolved object so it has an
				// eResource() and subsequent accesses need no further round trip. Raw, so a
				// keyed resolution does not drag in the whole collection (issue #146).
				super.getContents().add(resolved);
			}
			return resolved;
		} catch (RuntimeException | IOException e) {
			getWarnings().add(PersistenceDiagnostic.warning(DIAGNOSTIC_SOURCE,
					"Failed to resolve fragment " + uriFragment + ": " + e.getMessage(), getURI(), e));
			return null;
		}
	}

	/**
	 * Searches the already-materialised object graph for an EMF id, without resolving proxies
	 * and without touching {@link #getContents()} — so a fragment resolution never triggers
	 * the deferred population (issue #146).
	 */
	private EObject findInRawContents(String idValue) {
		for (EObject root : super.getContents()) {
			if (idValue.equals(CompositeIds.fragment(root))) {
				return root;
			}
			for (Iterator<EObject> it = EcoreUtil.getAllProperContents(root, false); it.hasNext();) {
				EObject candidate = it.next();
				if (!candidate.eIsProxy() && idValue.equals(CompositeIds.fragment(candidate))) {
					return candidate;
				}
			}
		}
		return null;
	}

	// ------------------------------------------------------------ codec bridge

	/**
	 * Encodes a single EObject into a {@link BsonDocument} — BsonDocument-direct via the
	 * codec's format-delegate bridge (mirrors {@code CodecResource.doSaveWithFormat}).
	 */
	protected BsonDocument encode(EObject eObject) throws IOException {
		ensureCompositeIdConfig(eObject.eClass());
		BsonFormatDelegate delegate = new BsonFormatDelegate(new BsonDocument());
		try (FormatDelegateGenerator<BsonDocument> generator = FormatDelegateGenerator.create(
				ObjectWriteContext.empty(), newIOContext(false), delegate)) {
			ObjectWriter writer = mongoMapper().writerFor(EObject.class);
			writer.writeValue(generator, eObject);
		} catch (RuntimeException e) {
			throw new IOException("Failed to encode EObject of type "
					+ eObject.eClass().getName(), e);
		}
		return delegate.getTarget();
	}

	/**
	 * Decodes a single {@link BsonDocument} into an EObject (mirrors
	 * {@code CodecResource.doLoadWithFormat}); unresolved references become EMF proxies
	 * whose URIs are resolved against this resource's URI.
	 */
	protected EObject decode(BsonDocument document, EClass eClassHint) throws IOException {
		ensureCompositeIdConfig(eClassHint);
		DiagnosticCollector diagnostics = new DiagnosticCollector();
		List<UnresolvedReference> unresolved = new ArrayList<>();
		ObjectReader reader = mongoMapper().readerFor(EObject.class)
				.withAttribute(ContextHelper.UNRESOLVED_REFERENCES, unresolved)
				.withAttribute(ContextHelper.DIAGNOSTIC_COLLECTOR, diagnostics)
				.withAttribute(ContextHelper.RESOURCE, this)
				.without(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
		if (nonNull(eClassHint)) {
			reader = reader.withAttribute(ContextHelper.EXPECTED_TYPE, eClassHint);
		}
		EObject result;
		BsonFormatReaderDelegate delegate = new BsonFormatReaderDelegate(document);
		try (FormatDelegateParser<BsonDocument> parser = FormatDelegateParser.create(
				ObjectReadContext.empty(), newIOContext(true), delegate)) {
			JsonToken firstToken = parser.nextToken();
			if (firstToken != JsonToken.START_OBJECT) {
				LOG.warning(() -> "Unexpected token at document root: " + firstToken);
				return null;
			}
			result = reader.readValue(parser);
		} catch (RuntimeException e) {
			throw new IOException("Failed to decode document from collection '"
					+ getCollectionName(null) + "'", e);
		}
		if (!unresolved.isEmpty()) {
			resolveUnresolvedReferences(unresolved, diagnostics);
		}
		diagnostics.addToResource(this);
		return result;
	}

	/**
	 * Replicates {@code CodecResource.resolveReferences} (private there): unresolved
	 * references are resolved within this resource where possible, otherwise turned
	 * into EMF proxies carrying the target URI (relative URIs resolved against this
	 * resource's URI).
	 */
	@SuppressWarnings("unchecked")
	private void resolveUnresolvedReferences(List<UnresolvedReference> unresolvedReferences,
			DiagnosticCollector diagnostics) {
		Map<String, EObject> proxyCache = new HashMap<>();
		for (UnresolvedReference unresolvedRef : unresolvedReferences) {
			String targetUri = unresolvedRef.getTargetUri();
			EObject target = proxyCache.computeIfAbsent(targetUri,
					uri -> createProxyFor(unresolvedRef, diagnostics));
			if (isNull(target)) {
				diagnostics.addWarning("Could not create proxy for reference "
						+ unresolvedRef.getReference().getName() + " -> " + targetUri,
						MongoResourceImpl.class.getSimpleName());
				continue;
			}
			EObject source = unresolvedRef.getSource();
			EReference reference = unresolvedRef.getReference();
			if (unresolvedRef.isMultiValued()) {
				List<EObject> list = (List<EObject>) source.eGet(reference);
				int index = unresolvedRef.getIndex();
				if (index < list.size()) {
					list.set(index, target);
				} else {
					list.add(target);
				}
			} else {
				source.eSet(reference, target);
			}
		}
	}

	private EObject createProxyFor(UnresolvedReference unresolvedRef, DiagnosticCollector diagnostics) {
		EClass eClass = unresolvedRef.getEffectiveType();
		if (isNull(eClass) || eClass.isAbstract() || eClass.isInterface()) {
			return null;
		}
		try {
			EObject proxy = EcoreUtil.create(eClass);
			((InternalEObject) proxy).eSetProxyURI(toProxyUri(unresolvedRef.getTargetUri(), eClass));
			return proxy;
		} catch (RuntimeException e) {
			diagnostics.addWarning("Error creating proxy for "
					+ unresolvedRef.getTargetUri() + ": " + e.getMessage(),
					MongoResourceImpl.class.getSimpleName());
			return null;
		}
	}

	/**
	 * Builds the proxy URI for an unresolved reference. The codec serialises
	 * cross-document references as the bare target id; the target collection comes from
	 * the reference's effective type: {@code mongodb://<db>/<TargetType>#<id>}. Absolute
	 * or path-carrying target URIs are resolved against this resource's URI as-is.
	 */
	private URI toProxyUri(String targetUri, EClass targetType) {
		URI raw = URI.createURI(targetUri);
		if (nonNull(raw.scheme()) || targetUri.contains("/") || isNull(getURI())) {
			return raw.isRelative() && nonNull(getURI()) ? raw.resolve(getURI()) : raw;
		}
		URI base = getURI();
		return base.trimFragment()
				.trimSegments(base.segmentCount())
				.appendSegment(targetType.getName())
				.appendFragment(targetUri);
	}

	private IOContext newIOContext(boolean forReading) {
		return new IOContext(
				StreamReadConstraints.defaults(),
				StreamWriteConstraints.defaults(),
				ErrorReportConfiguration.defaults(),
				new BufferRecycler(),
				ContentReference.unknown(), forReading, JsonEncoding.UTF8);
	}

	// ------------------------------------------------------------- id handling

	/**
	 * Returns the {@code _id} value for the EObject, generating (and writing back) a new
	 * {@link ObjectId} hex string when the String-typed EMF id attribute is unset.
	 */
	protected BsonValue ensureId(EObject eObject) throws IOException {
		if (CompositeIds.isComposite(eObject.eClass())) {
			// compound _id sub-document in canonical (declaration) order (issue #110);
			// composite components are never generated (issue #111 discipline)
			BsonDocument compound = new BsonDocument();
			for (EAttribute id : CompositeIds.idAttributes(eObject.eClass())) {
				Object value = eObject.eGet(id);
				if (isNull(value)) {
					throw new IOException("Composite id component '" + id.getName() + "' of '"
							+ eObject.eClass().getName() + "' is unset — composite ids are assigned, never generated");
				}
				compound.put(id.getName(), toBsonValue(value));
			}
			return compound;
		}
		EAttribute idAttribute = eObject.eClass().getEIDAttribute();
		if (isNull(idAttribute)) {
			throw new IOException("EClass '" + eObject.eClass().getName()
					+ "' has no ID attribute — cannot map to Mongo _id");
		}
		Object value = eObject.eGet(idAttribute);
		if (isUnsetId(value)) {
			Class<?> instanceClass = idAttribute.getEAttributeType().getInstanceClass();
			if (instanceClass != String.class) {
				throw new IOException("EObject of type '" + eObject.eClass().getName()
						+ "' has no id value and the id attribute is not String-typed — "
						+ "cannot generate an ObjectId");
			}
			String generated = new ObjectId().toHexString();
			eObject.eSet(idAttribute, generated);
			return new BsonString(generated);
		}
		return toBsonValue(value);
	}

	/** Returns the {@code _id} value of the EObject or {@code null} if unset. */
	protected BsonValue extractId(EObject eObject) {
		if (CompositeIds.isComposite(eObject.eClass())) {
			BsonDocument compound = new BsonDocument();
			for (EAttribute id : CompositeIds.idAttributes(eObject.eClass())) {
				Object value = eObject.eGet(id);
				if (isNull(value)) {
					return null;
				}
				compound.put(id.getName(), toBsonValue(value));
			}
			return compound;
		}
		EAttribute idAttribute = eObject.eClass().getEIDAttribute();
		if (isNull(idAttribute)) {
			return null;
		}
		Object value = eObject.eGet(idAttribute);
		return isUnsetId(value) ? null : toBsonValue(value);
	}

	private static boolean isUnsetId(Object value) {
		if (isNull(value)) {
			return true;
		}
		if (value instanceof String s) {
			return s.isEmpty();
		}
		if (value instanceof Number n) {
			return n.longValue() == 0L;
		}
		return false;
	}

	private static BsonValue toBsonValue(Object value) {
		if (value instanceof Integer i) {
			return new BsonInt32(i);
		}
		if (value instanceof Long l) {
			return new BsonInt64(l);
		}
		return new BsonString(String.valueOf(value));
	}

	/** Converts a string id from a URI fragment to the typed {@code _id} representation. */
	private BsonValue toBsonId(String idValue, EClass eClass) {
		if (nonNull(eClass)) {
			if (CompositeIds.isComposite(eClass)) {
				// compound _id sub-document from the k1=v1,k2=v2 fragment contract (issue #110)
				List<EAttribute> idAttributes = CompositeIds.idAttributes(eClass);
				List<String> components = CompositeIds.parse(eClass, idValue);
				BsonDocument compound = new BsonDocument();
				for (int i = 0; i < idAttributes.size(); i++) {
					compound.put(idAttributes.get(i).getName(), toTypedBson(idAttributes.get(i), components.get(i)));
				}
				return compound;
			}
			EAttribute idAttribute = eClass.getEIDAttribute();
			if (nonNull(idAttribute)) {
				return toTypedBson(idAttribute, idValue);
			}
		}
		return new BsonString(idValue);
	}

	/** Converts one string id component to the attribute's instance type in BSON. */
	private static BsonValue toTypedBson(EAttribute idAttribute, String idValue) {
		Class<?> instanceClass = idAttribute.getEAttributeType().getInstanceClass();
		try {
			if (instanceClass == Integer.class || instanceClass == int.class) {
				return new BsonInt32(Integer.parseInt(idValue));
			}
			if (instanceClass == Long.class || instanceClass == long.class) {
				return new BsonInt64(Long.parseLong(idValue));
			}
		} catch (NumberFormatException e) {
			LOG.log(Level.FINE, "Cannot convert id '" + idValue + "' to " + instanceClass, e);
		}
		return new BsonString(idValue);
	}

	// -------------------------------------------------------------- addressing

	/**
	 * Resolves the collection name: explicit table EClass option first, else the first
	 * URI segment after the database authority ({@code mongodb://<db>/<collection>}).
	 */
	protected String getCollectionName(Map<?, ?> options) {
		EClass tableEClass = nonNull(options) ? Options.getTableEClass(options) : null;
		if (nonNull(tableEClass)) {
			return tableEClass.getName();
		}
		URI uri = getURI();
		if (isNull(uri) || uri.segmentCount() == 0) {
			return null;
		}
		return uri.segment(0);
	}

	/** Returns the optional id segment ({@code mongodb://<db>/<collection>/<id>}). */
	protected String getIdSegment() {
		URI uri = getURI();
		if (isNull(uri) || uri.segmentCount() < 2) {
			return null;
		}
		return uri.segment(1);
	}

	/**
	 * Resolves the EClass for the collection: explicit option first, then a lookup by
	 * classifier name across the packages registered with the ResourceSet (and the
	 * global package registry).
	 */
	protected EClass resolveEClass(String collectionName, Map<?, ?> options) {
		EClass tableEClass = nonNull(options) ? Options.getTableEClass(options) : null;
		if (nonNull(tableEClass)) {
			return tableEClass;
		}
		ResourceSet resourceSet = getResourceSet();
		if (nonNull(resourceSet)) {
			EClass fromLocal = lookupEClass(resourceSet.getPackageRegistry(), collectionName);
			if (nonNull(fromLocal)) {
				return fromLocal;
			}
		}
		return lookupEClass(EPackage.Registry.INSTANCE, collectionName);
	}

	private EClass lookupEClass(EPackage.Registry registry, String name) {
		for (Object value : registry.values()) {
			if (value instanceof EPackage ePackage) {
				EClassifier classifier = ePackage.getEClassifier(name);
				if (classifier instanceof EClass eClass) {
					return eClass;
				}
			}
		}
		return null;
	}

	protected MongoCollection<BsonDocument> getCollection(String collectionName) {
		return database.getCollection(collectionName, BsonDocument.class);
	}

	protected MongoDatabase getDatabase() {
		return database;
	}
}
