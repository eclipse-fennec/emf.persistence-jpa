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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIOException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.model.command.CommandFactory;
import org.eclipse.fennec.model.command.DeleteCommand;
import org.eclipse.fennec.model.command.InsertCommand;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.capabilities.CommandCapabilities;
import org.eclipse.fennec.persistence.capabilities.PersistenceCapabilities;
import org.eclipse.fennec.persistence.capabilities.QueryCapabilities;
import org.eclipse.fennec.persistence.capabilities.StoreCapabilities;
import org.eclipse.fennec.persistence.query.api.CommandResource;
import org.eclipse.fennec.persistence.query.api.Hit;
import org.eclipse.fennec.persistence.query.api.QueryPlan;
import org.eclipse.fennec.persistence.query.api.QueryProcessor;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.eclipse.fennec.persistence.query.api.QueryResultRow;
import org.eclipse.fennec.persistence.query.api.QueryShape;
import org.eclipse.fennec.persistence.query.api.QueryableResource;
import org.eclipse.fennec.persistence.query.support.QueryResults;
import org.eclipse.fennec.persistence.repository.RepositoryConstants;
import org.eclipse.fennec.persistence.repository.api.PreparedQuery;
import org.eclipse.fennec.persistence.resource.PersistenceResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Plain-JUnit coverage of the delegation logic in {@link AbstractRepository}, using a
 * fake backend resource that records every call: options merging, query and command
 * routing by root type, delete isolation, named-root resolution, result/stream closing
 * and capabilities probing — everything the backend-free {@link AbstractRepositoryTest}
 * cannot reach. The real backend behavior is covered by the OSGi integration tests.
 */
public class AbstractRepositoryDelegationTest {

	private static final URI BASE = URI.createURI("test://unit");

	private EClass personClass;
	private EClass addressClass;
	private EAttribute idAttribute;
	/** Non-containment, so a stored value is a proxy that needs a resource to resolve (#280). */
	private EReference homeReference;
	private final List<FakeResource> created = new ArrayList<>();
	private FakeQueryProcessor queryProcessor;

	private AbstractRepository repository;

	@BeforeEach
	void setUp() {
		EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("test");
		ePackage.setNsPrefix("test");
		ePackage.setNsURI("http://fennec/test/delegation");
		personClass = eClassWithId(ePackage, "Person");
		idAttribute = (EAttribute) personClass.getEStructuralFeature("pid");
		addressClass = eClassWithId(ePackage, "Address");
		homeReference = EcoreFactory.eINSTANCE.createEReference();
		homeReference.setName("home");
		homeReference.setEType(addressClass);
		homeReference.setContainment(false);
		personClass.getEStructuralFeatures().add(homeReference);
		queryProcessor = new FakeQueryProcessor();
		repository = newRepository(Map.of(), Map.of());
		FakeResource.nextObjects = List.of();
		FakeResource.nextScored = false;
		FakeResource.lookup.clear();
	}

	private AbstractRepository newRepository(Map<Object, Object> loadDefaults, Map<Object, Object> saveDefaults) {
		return new AbstractRepository("delegation-repo", BASE, this::newResourceSet, queryProcessor, loadDefaults,
				saveDefaults) {
		};
	}

	private static EClass eClassWithId(EPackage ePackage, String name) {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(name);
		EAttribute id = EcoreFactory.eINSTANCE.createEAttribute();
		id.setName("pid");
		id.setEType(EcorePackage.Literals.ESTRING);
		id.setID(true);
		eClass.getEStructuralFeatures().add(id);
		EAttribute label = EcoreFactory.eINSTANCE.createEAttribute();
		label.setName("label");
		label.setEType(EcorePackage.Literals.ESTRING);
		eClass.getEStructuralFeatures().add(label);
		ePackage.getEClassifiers().add(eClass);
		return eClass;
	}

	private ResourceSet newResourceSet() {
		ResourceSetImpl set = new ResourceSetImpl();
		set.getResourceFactoryRegistry().getProtocolToFactoryMap().put("test", (Resource.Factory) uri -> {
			FakeResource resource = new FakeResource(uri);
			created.add(resource);
			return resource;
		});
		return set;
	}

	private EObject person(String id) {
		EObject person = personClass.getEPackage().getEFactoryInstance().create(personClass);
		person.eSet(idAttribute, id);
		return person;
	}

	private EObject address(String id, String label) {
		EObject address = addressClass.getEPackage().getEFactoryInstance().create(addressClass);
		address.eSet(addressClass.getEStructuralFeature("pid"), id);
		address.eSet(addressClass.getEStructuralFeature("label"), label);
		return address;
	}

	private static Object label(EObject object) {
		return object.eGet(object.eClass().getEStructuralFeature("label"));
	}

	private FakeResource resourceFor(String segment) {
		return created.stream().filter(r -> r.getURI().equals(BASE.appendSegment(segment))).findFirst().orElseThrow();
	}

	@Test
	void findRoutesToTheRootCollectionResourceAndMergesOptions() throws IOException {
		repository = newRepository(Map.of("default", "d"), Map.of());
		Query query = QueryBuilder.from(personClass).build();
		repository.find(query, Map.of("p", 1), Map.of("perCall", "c")).close();

		FakeResource persons = resourceFor("Person");
		assertThat(persons.lastQuery).isSameAs(query);
		assertThat(persons.lastParameters).isEqualTo(Map.of("p", 1));
		assertThat(persons.lastQueryOptions.get("default")).isEqualTo("d");
		assertThat(persons.lastQueryOptions.get("perCall")).isEqualTo("c");
	}

	@Test
	void saveQueryExecutionRemembersTheRootForByNameExecution() throws IOException {
		repository.find(QueryBuilder.from(personClass).named("byName").build(), null, null).close();
		repository.find("byName", Map.of("p", 2), null).close();

		FakeResource persons = resourceFor("Person");
		assertThat(persons.lastQueryName).isEqualTo("byName");
		assertThat(persons.lastParameters).isEqualTo(Map.of("p", 2));
	}

	@Test
	void findByNameAcceptsARootTypeHint() throws IOException {
		repository.find("unseen", null, Map.of(RepositoryConstants.OPTION_QUERY_ROOT, personClass)).close();
		assertThat(resourceFor("Person").lastQueryName).isEqualTo("unseen");
	}

	@Test
	void countExecutesACountOnlyCopyAndClosesTheResult() throws IOException {
		Query query = QueryBuilder.from(personClass).build();
		long count = repository.count(query);

		FakeResource persons = resourceFor("Person");
		assertThat(count).isEqualTo(FakeResource.COUNT_ANSWER);
		assertThat(persons.lastQuery).isNotSameAs(query);
		assertThat(persons.lastQuery.isCountOnly()).isTrue();
		assertThat(query.isCountOnly()).as("the caller's query must stay untouched").isFalse();
		assertThat(persons.lastResult.closed).isTrue();
	}

	@Test
	void getAllEObjectsClosesTheResultWithTheStream() throws IOException {
		FakeResource.nextObjects = List.of(person("p1"), person("p2"));
		try (Stream<EObject> all = repository.getAllEObjects(personClass)) {
			assertThat(all.count()).isEqualTo(2);
		}
		assertThat(resourceFor("Person").lastResult.closed)
				.as("closing the stream must close the QueryResult").isTrue();
	}

	// ------------------------------------------- find results are attached (#280)

	/**
	 * The blocker of issue #280: an object handed out by {@code find} had
	 * {@code eResource() == null}, so it had no ResourceSet either, so a non-containment
	 * reference stayed an unresolvable proxy for good — every attribute of the target read
	 * null, and the NPE surfaced far from here. {@code getEObject} never had the problem
	 * because it attaches what it resolves.
	 */
	@Test
	void aReferenceOnAFindResultResolves() throws IOException {
		EObject home = address("a1", "Baker Street");
		FakeResource.lookup.put("a1", home);
		EObject person = person("p1");
		person.eSet(homeReference, repository.createProxy(addressClass, "a1"));
		FakeResource.nextObjects = List.of(person);

		EObject found;
		try (QueryResult result = repository.find(QueryBuilder.from(personClass).build())) {
			found = result.objects().findFirst().orElseThrow();
		}

		EObject resolved = (EObject) found.eGet(homeReference);
		assertThat(resolved.eIsProxy()).as("the proxy resolved through the repository's set").isFalse();
		assertThat(label(resolved)).isEqualTo("Baker Street");
	}

	@Test
	void findResultsCarryTheCollectionResourceOfTheirType() throws IOException {
		FakeResource.nextObjects = List.of(person("p1"), person("p2"));

		try (QueryResult result = repository.find(QueryBuilder.from(personClass).build())) {
			assertThat(result.objects()).allSatisfy(object -> assertThat(object.eResource())
					.isNotNull()
					.extracting(Resource::getURI)
					.isEqualTo(BASE.appendSegment("Person")));
		}
	}

	/**
	 * Attachment rides the stream, so a query over a large collection does not materialise
	 * and retain the whole result at {@code find} time — the laziness the streaming API
	 * exists for.
	 */
	@Test
	void resultsAreAttachedAsTheStreamIsConsumed() throws IOException {
		FakeResource.nextObjects = List.of(person("p1"), person("p2"), person("p3"));

		try (QueryResult result = repository.find(QueryBuilder.from(personClass).build())) {
			assertThat(resourceFor("Person").getContents())
					.as("nothing attached before the first element is pulled").isEmpty();
			Stream<EObject> objects = result.objects();
			EObject first = objects.iterator().next();
			assertThat(resourceFor("Person").getContents()).containsExactly(first);
		}
	}

	@Test
	void anAlreadyAttachedResultKeepsItsResource() throws IOException {
		EObject person = person("p1");
		Resource origin = repository.attach(person);
		FakeResource.nextObjects = List.of(person);

		try (QueryResult result = repository.find(QueryBuilder.from(personClass).build())) {
			assertThat(result.objects()).singleElement()
					.satisfies(object -> assertThat(object.eResource()).isSameAs(origin));
		}
		assertThat(origin.getContents()).as("attached once, not twice").containsExactly(person);
	}

	@Test
	void getAllEObjectsAttachesItsResultsToo() throws IOException {
		FakeResource.nextObjects = List.of(person("p1"));

		try (Stream<EObject> all = repository.getAllEObjects(personClass)) {
			assertThat(all).allSatisfy(object -> assertThat(object.eResource()).isNotNull());
		}
	}

	@Test
	void findByNameAttachesItsResultsToo() throws IOException {
		FakeResource.nextObjects = List.of(person("p1"));
		repository.find(QueryBuilder.from(personClass).named("byName").build(), null, null).close();
		FakeResource.nextObjects = List.of(person("p2"));

		try (QueryResult result = repository.find("byName", null, null)) {
			assertThat(result.objects()).allSatisfy(object -> assertThat(object.eResource()).isNotNull());
		}
	}

	/** The scored view of the same cursor (issue #165) hands out the same objects. */
	@Test
	void scoredHitsAreAttachedToo() throws IOException {
		FakeResource.nextObjects = List.of(person("p1"));
		FakeResource.nextScored = true;

		try (QueryResult result = repository.find(QueryBuilder.from(personClass).build())) {
			assertThat(result.hits()).allSatisfy(hit -> assertThat(hit.object().eResource()).isNotNull());
		}
	}

	@Test
	void aCountResultIsUnaffected() throws IOException {
		Query counted = QueryBuilder.from(personClass).build();
		counted.setCountOnly(true);

		try (QueryResult result = repository.find(counted)) {
			assertThat(result.shape()).isEqualTo(QueryShape.COUNT);
			assertThat(result.count()).isEqualTo(FakeResource.COUNT_ANSWER);
			assertThatIllegalStateException().as("the shape contract still holds")
					.isThrownBy(result::objects);
		}
	}

	/**
	 * #280 expected {@code reload} to be broken on a find result too. It is not — the fresh
	 * read happens in a scratch set and {@code copyState} works in place, so attachment never
	 * mattered here. Kept as the guard that attaching the result does not change that.
	 */
	@Test
	void aFindResultCanBeReloaded() throws IOException {
		EObject person = person("p1");
		person.eSet(personClass.getEStructuralFeature("label"), "stale");
		FakeResource.nextObjects = List.of(person);
		EObject fresh = person("p1");
		fresh.eSet(personClass.getEStructuralFeature("label"), "fresh");
		FakeResource.lookup.put("p1", fresh);

		EObject found;
		try (QueryResult result = repository.find(QueryBuilder.from(personClass).build())) {
			found = result.objects().findFirst().orElseThrow();
		}
		repository.reload(found);

		assertThat(label(found)).isEqualTo("fresh");
	}

	@Test
	void saveMergesDefaultsWithPerCallOptions() throws IOException {
		repository = newRepository(Map.of(), Map.of("batch", 10));
		EObject person = person("p1");
		repository.save(person, Map.of("flush", true));

		FakeResource persons = resourceFor("Person");
		assertThat(persons.savedObjects).containsExactly(person);
		assertThat(persons.lastSaveOptions.get("batch")).isEqualTo(10);
		assertThat(persons.lastSaveOptions.get("flush")).isEqualTo(true);
	}

	@Test
	void saveAllGroupsByTypeAndSavesEachResourceOnce() throws IOException {
		EObject p1 = person("p1");
		EObject p2 = person("p2");
		EObject address = addressClass.getEPackage().getEFactoryInstance().create(addressClass);
		address.eSet(addressClass.getEStructuralFeature("pid"), "a1");
		repository.saveAll(List.of(p1, p2, address), null);

		assertThat(resourceFor("Person").saveCount).isEqualTo(1);
		assertThat(resourceFor("Person").savedObjects).containsExactly(p1, p2);
		assertThat(resourceFor("Address").saveCount).isEqualTo(1);
	}

	@Test
	void saveTouchesOnlyTheGivenObject() throws IOException {
		EObject p1 = person("p1");
		EObject p2 = person("p2");
		Resource loaded = repository.attach(p1);
		repository.attach(p2);

		repository.save(p1);

		FakeResource saved = created.stream().filter(r -> r.saveCount > 0).findFirst().orElseThrow();
		assertThat(saved).as("the loaded collection resource must not be saved").isNotSameAs(loaded);
		assertThat(saved.savedObjects).containsExactly(p1);
		assertThat(((FakeResource) loaded).saveCount).isZero();
		assertThat(loaded.getContents()).as("attachment must be restored").containsExactlyInAnyOrder(p1, p2);
	}

	@Test
	void saveAttachesAPreviouslyUnattachedObject() throws IOException {
		EObject p1 = person("p1");
		repository.save(p1);
		assertThat(p1.eResource()).isNotNull();
		assertThat(p1.eResource().getResourceSet()).isSameAs(repository.getResourceSet());
	}

	@Test
	void saveAllSavesExactlyTheGivenObjects() throws IOException {
		EObject p1 = person("p1");
		EObject p2 = person("p2");
		Resource loaded = repository.attach(p1);
		repository.attach(p2);

		repository.saveAll(List.of(p1));

		FakeResource saved = created.stream().filter(r -> r.saveCount > 0).findFirst().orElseThrow();
		assertThat(saved.savedObjects).containsExactly(p1);
		assertThat(((FakeResource) loaded).saveCount).isZero();
		assertThat(loaded.getContents()).containsExactlyInAnyOrder(p1, p2);
	}

	@Test
	void reloadReplacesTheStateInPlace() throws IOException {
		EAttribute labelAttribute = (EAttribute) personClass.getEStructuralFeature("label");
		EObject stale = person("p1");
		stale.eSet(labelAttribute, "locally-modified");
		Resource attached = repository.attach(stale);

		EObject fresh = person("p1");
		fresh.eSet(labelAttribute, "backend-state");
		FakeResource.lookup.put("p1", fresh);

		repository.reload(stale);

		assertThat(stale.eGet(labelAttribute)).isEqualTo("backend-state");
		assertThat(stale.eResource()).as("attachment must stay untouched").isSameAs(attached);
	}

	@Test
	void reloadRefusesWhenTheObjectIsGone() {
		EObject orphan = person("p1");
		assertThatIOException().isThrownBy(() -> repository.reload(orphan))
				.withMessageContaining("no longer exists");
	}

	@Test
	void reloadRefusesWithoutADeterminableId() {
		EObject noId = personClass.getEPackage().getEFactoryInstance().create(personClass);
		assertThatIOException().isThrownBy(() -> repository.reload(noId))
				.withMessageContaining("determinable id");
	}

	@Test
	void deleteIsolatesTheObjectFromItsLoadedResource() throws IOException {
		EObject p1 = person("p1");
		EObject p2 = person("p2");
		Resource loaded = repository.attach(p1);
		repository.attach(p2);

		repository.delete(p1);

		assertThat(loaded.getContents()).as("siblings must survive the delete").containsExactly(p2);
		FakeResource deletion = created.stream().filter(r -> !r.deletedObjects.isEmpty()).findFirst().orElseThrow();
		assertThat(deletion).isNotSameAs(loaded);
		assertThat(deletion.deletedObjects).containsExactly(p1);
	}

	@Test
	void deleteByUriResolvesTheObjectFirst() throws IOException {
		EObject p1 = person("p1");
		repository.attach(p1);

		repository.delete(repository.createUri(personClass, "p1"));

		FakeResource deletion = created.stream().filter(r -> !r.deletedObjects.isEmpty()).findFirst().orElseThrow();
		assertThat(deletion.deletedObjects).containsExactly(p1);
	}

	@Test
	void executeRoutesCommandsByTargetType() throws IOException {
		InsertCommand insert = CommandFactory.eINSTANCE.createInsertCommand();
		insert.getObjects().add(person("p1"));
		assertThat(repository.execute(insert)).isEqualTo(FakeResource.EXECUTE_ANSWER);
		assertThat(resourceFor("Person").lastCommand).isSameAs(insert);

		DeleteCommand delete = CommandFactory.eINSTANCE.createDeleteCommand();
		delete.setSelector(QueryBuilder.from(addressClass).build());
		repository.execute(delete);
		assertThat(resourceFor("Address").lastCommand).isSameAs(delete);
	}

	@Test
	void executeRefusesAnEmptyInsert() {
		InsertCommand empty = CommandFactory.eINSTANCE.createInsertCommand();
		assertThatIOException().isThrownBy(() -> repository.execute(empty))
				.withMessageContaining("no objects");
	}

	@Test
	void capabilitiesAreProbedOnceAndTheProbeIsCleanedUp() {
		PersistenceCapabilities first = repository.capabilities();
		PersistenceCapabilities second = repository.capabilities();
		assertThat(first).isSameAs(FakeResource.CAPABILITIES).isSameAs(second);
		assertThat(repository.getResourceSet().getResources())
				.as("the probe resource must not linger").isEmpty();
	}

	@Test
	void prepareValidatesThroughTheProcessorAndExecutesTheCopy() throws IOException {
		Query query = QueryBuilder.from(personClass).build();
		PreparedQuery prepared = repository.prepare(query);
		assertThat(queryProcessor.validated).isNotNull();

		prepared.execute(Map.of("p", 3)).close();
		FakeResource persons = resourceFor("Person");
		assertThat(persons.lastQuery).isNotSameAs(query);
		assertThat(persons.lastParameters).isEqualTo(Map.of("p", 3));
	}

	@Test
	void prepareRefusesOnValidationError() {
		queryProcessor.answer = new BasicDiagnostic(Diagnostic.ERROR, "test", 0, "unsupported construct", null);
		assertThatIOException().isThrownBy(() -> repository.prepare(QueryBuilder.from(personClass).build()))
				.withMessageContaining("fake-backend");
	}

	/*
	 * ==================== fakes ====================
	 */

	static final class FakeQueryProcessor implements QueryProcessor {
		Diagnostic answer = Diagnostic.OK_INSTANCE;
		Query validated;

		@Override
		public String backend() {
			return "fake-backend";
		}

		@Override
		public org.eclipse.fennec.persistence.capabilities.QueryCapabilities capabilities() {
			return null;
		}

		@Override
		public Diagnostic validate(Query query, EClass rootEClass) {
			validated = query;
			return answer;
		}

		@Override
		public QueryPlan translate(Query query, org.eclipse.fennec.persistence.query.api.QueryContext context) {
			throw new UnsupportedOperationException();
		}
	}

	static final class FakeResource extends ResourceImpl
			implements PersistenceResource, QueryableResource, CommandResource {

		static final long COUNT_ANSWER = 42L;
		static final long EXECUTE_ANSWER = 7L;
		static final PersistenceCapabilities CAPABILITIES = new PersistenceCapabilities() {
			@Override
			public QueryCapabilities query() {
				return null;
			}

			@Override
			public CommandCapabilities command() {
				return null;
			}

			@Override
			public StoreCapabilities store() {
				return null;
			}
		};
		static List<EObject> nextObjects = List.of();
		/** When set, query() answers a scored result so hits() is usable. */
		static boolean nextScored;
		/** Keyed-read answers by id fragment, consulted when the contents hold no match. */
		static final Map<String, EObject> lookup = new LinkedHashMap<>();

		Query lastQuery;
		String lastQueryName;
		Map<String, Object> lastParameters;
		Map<?, ?> lastQueryOptions;
		Map<?, ?> lastSaveOptions;
		List<EObject> savedObjects = List.of();
		List<EObject> deletedObjects = List.of();
		int saveCount;
		FakeResult lastResult;
		org.eclipse.fennec.model.command.Command lastCommand;
		java.util.Map<String, Object> lastCommandParameters;

		FakeResource(URI uri) {
			super(uri);
		}

		@Override
		public EObject getEObject(String uriFragment) {
			return getContents().stream().filter(o -> uriFragment.equals(EcoreUtil.getID(o))).findFirst()
					.orElseGet(() -> lookup.get(uriFragment));
		}

		@Override
		public void save(Map<?, ?> options) {
			lastSaveOptions = options;
			savedObjects = List.copyOf(getContents());
			saveCount++;
		}

		@Override
		public void delete(Map<?, ?> options) {
			deletedObjects = List.copyOf(getContents());
			getContents().clear();
		}

		@Override
		public void load(Map<?, ?> options) {
			setLoaded(true);
		}

		@Override
		public QueryResult query(Query query) {
			return query(query, null, null);
		}

		@Override
		public QueryResult query(Query query, Map<String, Object> parameters, Map<?, ?> options) {
			lastQuery = query;
			lastParameters = parameters;
			lastQueryOptions = options;
			lastResult = new FakeResult(query.isCountOnly() ? QueryShape.COUNT : QueryShape.OBJECTS,
					nextObjects, nextScored);
			return lastResult;
		}

		@Override
		public QueryResult query(String name, Map<String, Object> parameters, Map<?, ?> options) {
			lastQueryName = name;
			lastParameters = parameters;
			lastQueryOptions = options;
			lastResult = new FakeResult(QueryShape.OBJECTS, nextObjects, nextScored);
			return lastResult;
		}

		@Override
		public long execute(org.eclipse.fennec.model.command.Command command) {
			return execute(command, null, null);
		}

		@Override
		public long execute(org.eclipse.fennec.model.command.Command command,
				java.util.Map<String, Object> parameters, java.util.Map<?, ?> options) {
			lastCommand = command;
			lastCommandParameters = parameters;
			return EXECUTE_ANSWER;
		}

		@Override
		public org.eclipse.fennec.persistence.query.support.CommandTransaction begin() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void updateDefaultOptions(Map<Object, Object> options, ActionType... types) {
			// recorded implicitly via lastQueryOptions/lastSaveOptions
		}

		@Override
		public long count() {
			return COUNT_ANSWER;
		}

		@Override
		public long count(Map<?, ?> options) {
			return COUNT_ANSWER;
		}

		@Override
		public boolean exist() {
			return true;
		}

		@Override
		public boolean exist(Map<?, ?> options) {
			return true;
		}

		@Override
		public PersistenceCapabilities capabilities() {
			return CAPABILITIES;
		}

		@Override
		public void close() {
			// nothing held
		}
	}

	static final class FakeResult implements QueryResult {
		final QueryShape shape;
		final List<EObject> objects;
		final boolean scored;
		boolean closed;

		FakeResult(QueryShape shape, List<EObject> objects) {
			this(shape, objects, false);
		}

		FakeResult(QueryShape shape, List<EObject> objects, boolean scored) {
			this.shape = shape;
			this.objects = objects;
			this.scored = scored;
		}

		@Override
		public QueryShape shape() {
			return shape;
		}

		@Override
		public Stream<EObject> objects() {
			if (shape != QueryShape.OBJECTS) {
				throw new IllegalStateException("objects() is only valid for OBJECTS, this is " + shape);
			}
			return objects.stream();
		}

		@Override
		public Stream<QueryResultRow> rows() {
			throw new UnsupportedOperationException();
		}

		@Override
		public long count() {
			return FakeResource.COUNT_ANSWER;
		}

		@Override
		public Stream<Hit> hits() {
			if (!scored) {
				throw new IllegalStateException("unscored");
			}
			return objects.stream().map(object -> QueryResults.hit(object, 1.0d));
		}

		@Override
		public Map<String, Double> scores() {
			return new LinkedHashMap<>();
		}

		@Override
		public void close() {
			closed = true;
		}
	}
}
