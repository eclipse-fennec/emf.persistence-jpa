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
package org.eclipse.fennec.persistence.tck;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.model.command.CommandFactory;
import org.eclipse.fennec.model.command.DeleteCommand;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.builder.Expressions;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.eclipse.fennec.persistence.orm.EntityMapper;
import org.eclipse.fennec.persistence.repository.spi.AbstractRepository;
import org.eclipse.fennec.persistence.resource.PersistenceResource;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.JpaHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.persistence.EntityManagerFactory;

/**
 * The lifecycle of contained objects whose class has no id attribute (issue #403): the derived
 * mapping gives them a synthetic {@code pk_<Class>} key that lives on an adapter, not in a
 * feature (issue #184). EclipseLink's backup clone is built by copying the features, so it went
 * without the key, the commit saw the key change from {@code null} to its value, and wrote an
 * UPDATE of a primary key column — {@code EclipseLink-7251}. Every write of a loaded container
 * failed that way, a delete as much as a save.
 * <p>
 * The shape is the one of emf.streaming that found it: a container with an id, contained
 * children without one, and grandchildren without one. Every case runs with the shared cache
 * on and off, and every assertion reads the store through {@link JpaStoreProbe}.
 *
 * @author Mark Hoffmann
 * @since 08.11.2026
 */
class JpaSyntheticKeyLifecycleTest {

	static {
		// Same doctrine as AbstractPersistenceTCK (issue #79): H2 caches the JVM zone
		// statically at first use, and this class does not extend the abstract TCK.
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static final String PU_NAME = "synthkey";

	private EPackage batchPackage;
	private EClass batchClass;
	private EClass entryClass;
	private EClass slotClass;
	private EAttribute batchId;
	private EAttribute entryLabel;
	private EAttribute slotValue;
	private EReference batchEntries;
	private EReference entrySlots;

	private EntityManagerFactory emf;
	private JpaStoreProbe probe;

	private void bootstrap(boolean sharedCache) {
		buildModel();
		List<EClassifier> eClasses = List.of(batchClass, entryClass, slotClass);
		Map<String, Object> jdbcProperties = new HashMap<>(JpaTestSupport.jdbcProperties(PU_NAME));
		jdbcProperties.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, String.valueOf(sharedCache));
		emf = JpaTckSupport.bootstrap(PU_NAME, new EntityMapper().createMappings(new ArrayList<>(eClasses)),
				jdbcProperties, "create-or-extend-tables");
		probe = new JpaStoreProbe(JpaHelper.getServerSession(emf), jdbcProperties);
	}

	@AfterEach
	void tearDown() {
		if (nonNull(emf)) {
			emf.close();
			emf = null;
		}
	}

	private void buildModel() {
		EcoreFactory ecore = EcoreFactory.eINSTANCE;
		slotClass = ecore.createEClass();
		slotClass.setName("Slot");
		slotValue = addAttribute(slotClass, "value", false);

		entryClass = ecore.createEClass();
		entryClass.setName("Entry");
		entryLabel = addAttribute(entryClass, "label", false);
		entrySlots = addContainment(entryClass, "slots", slotClass);

		batchClass = ecore.createEClass();
		batchClass.setName("Batch");
		batchId = addAttribute(batchClass, "bid", true);
		batchEntries = addContainment(batchClass, "entries", entryClass);

		batchPackage = ecore.createEPackage();
		batchPackage.setName("synthkey");
		batchPackage.setNsURI("urn:synthkey:jpa/1.0");
		batchPackage.setNsPrefix("synthkey");
		batchPackage.getEClassifiers().addAll(List.of(batchClass, entryClass, slotClass));
	}

	private static EAttribute addAttribute(EClass eClass, String name, boolean id) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(EcorePackage.Literals.ESTRING);
		attribute.setID(id);
		eClass.getEStructuralFeatures().add(attribute);
		return attribute;
	}

	private static EReference addContainment(EClass eClass, String name, EClass type) {
		EReference reference = EcoreFactory.eINSTANCE.createEReference();
		reference.setName(name);
		reference.setEType(type);
		reference.setContainment(true);
		reference.setUpperBound(-1);
		eClass.getEStructuralFeatures().add(reference);
		return reference;
	}

	// ------------------------------------------------------------------ the shape

	/** The fixture really has synthetic keys, two levels deep — otherwise every case is moot. */
	@ParameterizedTest(name = "sharedCache={0}")
	@ValueSource(booleans = { true, false })
	void theChildrenHaveSyntheticKeys(boolean sharedCache) throws Exception {
		bootstrap(sharedCache);
		saveBatch("b1", 2, 3);

		assertThat(probe.ids(entryClass)).hasSize(2).doesNotContainNull();
		assertThat(probe.ids(slotClass)).hasSize(6).doesNotContainNull();
	}

	// ------------------------------------------------------------------ delete

	/** The emf.streaming flow: find through the repository, delete through the repository. */
	@ParameterizedTest(name = "sharedCache={0}")
	@ValueSource(booleans = { true, false })
	void deletingAFoundContainerDeletesItsChildrenWithoutAnId(boolean sharedCache) throws Exception {
		bootstrap(sharedCache);
		saveBatch("b1", 2, 3);
		saveBatch("b2", 1, 1);
		AbstractRepository repository = repository();

		EObject found = repository.find(byId("b1")).objects().findFirst().orElseThrow();
		repository.delete(found);

		assertThat(probe.ids(batchClass)).containsExactly("b2");
		assertThat(probe.ids(entryClass)).as("the entries of b1 go, the one of b2 stays").hasSize(1);
		assertThat(probe.ids(slotClass)).as("the slots of b1 go, the one of b2 stays").hasSize(1);
	}

	/** The same through a delete command whose selector picks the container by its id. */
	@ParameterizedTest(name = "sharedCache={0}")
	@ValueSource(booleans = { true, false })
	void aDeleteCommandDeletesTheChildrenWithoutAnId(boolean sharedCache) throws Exception {
		bootstrap(sharedCache);
		saveBatch("b1", 2, 3);
		saveBatch("b2", 1, 1);

		DeleteCommand delete = CommandFactory.eINSTANCE.createDeleteCommand();
		delete.setSelector(byId("b1"));
		long affected = repository().execute(delete);

		assertThat(affected).isEqualTo(1);
		assertThat(probe.ids(batchClass)).containsExactly("b2");
		assertThat(probe.ids(entryClass)).hasSize(1);
		assertThat(probe.ids(slotClass)).hasSize(1);
	}

	/** Resource level: load the container through a resource of its own and delete it. */
	@ParameterizedTest(name = "sharedCache={0}")
	@ValueSource(booleans = { true, false })
	void deletingALoadedResourceDeletesTheChildrenWithoutAnId(boolean sharedCache) throws Exception {
		bootstrap(sharedCache);
		saveBatch("b1", 2, 3);

		Resource resource = resourceSet().getResource(uriFor("Batch"), true);
		assertThat(resource.getContents()).hasSize(1);
		((PersistenceResource) resource).delete(null);

		assertThat(probe.ids(batchClass)).isEmpty();
		assertThat(probe.ids(entryClass)).isEmpty();
		assertThat(probe.ids(slotClass)).isEmpty();
	}

	// ------------------------------------------------------------------ save

	/** A loaded container saved again unchanged keeps its rows — no key rewritten, no row added. */
	@ParameterizedTest(name = "sharedCache={0}")
	@ValueSource(booleans = { true, false })
	void savingAnUntouchedContainerKeepsTheKeys(boolean sharedCache) throws Exception {
		bootstrap(sharedCache);
		saveBatch("b1", 2, 3);
		Set<String> entries = probe.ids(entryClass);
		Set<String> slots = probe.ids(slotClass);
		AbstractRepository repository = repository();

		EObject found = repository.find(byId("b1")).objects().findFirst().orElseThrow();
		repository.save(found);

		assertThat(probe.ids(entryClass)).containsExactlyInAnyOrderElementsOf(entries);
		assertThat(probe.ids(slotClass)).containsExactlyInAnyOrderElementsOf(slots);
	}

	/** An edit of a child without an id updates that row in place, under the same key. */
	@ParameterizedTest(name = "sharedCache={0}")
	@ValueSource(booleans = { true, false })
	void editingAChildWithoutAnIdUpdatesItsRow(boolean sharedCache) throws Exception {
		bootstrap(sharedCache);
		saveBatch("b1", 2, 3);
		Set<String> entries = probe.ids(entryClass);
		AbstractRepository repository = repository();

		EObject found = repository.find(byId("b1")).objects().findFirst().orElseThrow();
		EObject first = entries(found).get(0);
		first.eSet(entryLabel, "changed");
		slots(first).get(0).eSet(slotValue, "changed slot");
		repository.save(found);

		assertThat(probe.ids(entryClass)).containsExactlyInAnyOrderElementsOf(entries);
		assertThat(probe.ids(slotClass)).hasSize(6);
		EObject reloaded = repository().getEObject(batchClass, "b1");
		assertThat(entries(reloaded)).extracting(e -> e.eGet(entryLabel)).contains("changed");
		assertThat(entries(reloaded)).flatExtracting(e -> slots(e)).extracting(s -> s.eGet(slotValue))
				.contains("changed slot");
	}

	/** Removing a child without an id deletes its row and those of its own children (orphans). */
	@ParameterizedTest(name = "sharedCache={0}")
	@ValueSource(booleans = { true, false })
	void removingAChildWithoutAnIdDeletesItsRows(boolean sharedCache) throws Exception {
		bootstrap(sharedCache);
		saveBatch("b1", 2, 3);
		AbstractRepository repository = repository();

		EObject found = repository.find(byId("b1")).objects().findFirst().orElseThrow();
		entries(found).remove(0);
		repository.save(found);

		assertThat(probe.ids(batchClass)).containsExactly("b1");
		assertThat(probe.ids(entryClass)).as("one entry left").hasSize(1);
		assertThat(probe.ids(slotClass)).as("only the slots of the remaining entry").hasSize(3);
		EObject reloaded = repository().getEObject(batchClass, "b1");
		assertThat(entries(reloaded)).singleElement().satisfies(e -> assertThat(slots(e)).hasSize(3));
	}

	/** Adding a child to a loaded container inserts it, next to the children already stored. */
	@ParameterizedTest(name = "sharedCache={0}")
	@ValueSource(booleans = { true, false })
	void addingAChildWithoutAnIdInsertsIt(boolean sharedCache) throws Exception {
		bootstrap(sharedCache);
		saveBatch("b1", 1, 1);
		Set<String> before = probe.ids(entryClass);
		AbstractRepository repository = repository();

		EObject found = repository.find(byId("b1")).objects().findFirst().orElseThrow();
		entries(found).add(entry("added", 2));
		repository.save(found);

		assertThat(probe.ids(entryClass)).hasSize(2).containsAll(before);
		assertThat(probe.ids(slotClass)).hasSize(3);
	}

	// ------------------------------------------------------------------ helpers

	private Query byId(String id) {
		return QueryBuilder.from(batchClass).where(Expressions.path(batchId).eq(id)).build();
	}

	@SuppressWarnings("unchecked")
	private List<EObject> entries(EObject batch) {
		return (List<EObject>) batch.eGet(batchEntries);
	}

	@SuppressWarnings("unchecked")
	private List<EObject> slots(EObject entry) {
		return (List<EObject>) entry.eGet(entrySlots);
	}

	private AbstractRepository repository() {
		return new AbstractRepository(PU_NAME, uriFor("Batch").trimSegments(1), this::resourceSet, null, null,
				null) {
			// all behaviour is generic
		};
	}

	private ResourceSet resourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(batchPackage.getNsURI(), batchPackage);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("jpa", new JPAResourceFactory(emf));
		return resourceSet;
	}

	private URI uriFor(String typeName) {
		return URI.createURI("jpa://" + PU_NAME + "/" + typeName);
	}

	private EObject entry(String label, int slotCount) {
		EObject entry = EcoreUtil.create(entryClass);
		entry.eSet(entryLabel, label);
		for (int i = 0; i < slotCount; i++) {
			EObject slot = EcoreUtil.create(slotClass);
			slot.eSet(slotValue, label + "/" + i);
			slots(entry).add(slot);
		}
		return entry;
	}

	private void saveBatch(String id, int entryCount, int slotsPerEntry) throws Exception {
		EObject batch = EcoreUtil.create(batchClass);
		batch.eSet(batchId, id);
		for (int i = 0; i < entryCount; i++) {
			entries(batch).add(entry(id + "-" + i, slotsPerEntry));
		}
		Resource resource = resourceSet().createResource(uriFor("Batch"));
		resource.getContents().add(batch);
		resource.save(null);
	}
}
