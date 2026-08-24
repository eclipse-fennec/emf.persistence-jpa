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
package org.eclipse.fennec.persistence.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.model.command.CommandFactory;
import org.eclipse.fennec.model.command.UpdateCommand;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.model.stream.ChangeEntry;
import org.eclipse.fennec.model.stream.ChangeSet;
import org.eclipse.fennec.model.stream.DeltaKind;
import org.eclipse.fennec.model.stream.StreamFactory;
import org.eclipse.fennec.persistence.query.api.CommandResource;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * What a save costs in <em>read</em> queries before it writes anything (issue #226).
 * <p>
 * {@code upsert} decides INSERT vs. UPDATE by probing for the existing row, and the containment
 * adoption of issue #130 does the same per child — one {@code em.find} each. Saving a hundred
 * objects therefore cost a hundred selects before the first write, while the ids were all known
 * up front and one {@code IN} query answers the same question.
 * <p>
 * The shared cache is switched off deliberately. With it on, a second save of the same objects
 * answers from the L2 cache and the probes never reach the database, which would make this
 * measurement pass no matter what the code does — the cost only shows on a cold cache, which is
 * also the case that hurts in production (a fresh process importing a batch).
 *
 * @author Mark Hoffmann
 * @since 24.08.2026
 */
class NonOsgiSaveProbeCostTest extends NonOsgiPersistenceTestBase {

	private EPackage modelPackage;
	private EClass personEClass;
	private EStructuralFeature idFeature;
	private EStructuralFeature nameFeature;

	/** One entry per read query EclipseLink sent while the counter was armed. */
	private final List<String> reads = new ArrayList<>();
	private final AtomicInteger armed = new AtomicInteger();

	@Override
	protected Map<String, Object> defaultProperties() {
		Map<String, Object> props = super.defaultProperties();
		props.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "false");
		return props;
	}

	@BeforeEach
	void setUp() {
		modelPackage = loadEcore("data/model.ecore");
		personEClass = (EClass) modelPackage.getEClassifier("Person");
		idFeature = personEClass.getEStructuralFeature("id");
		nameFeature = personEClass.getEStructuralFeature("stringDefault");
		bootstrapPersistence("person", List.of(personEClass));
		serverSession.getEventManager().addListener(new SessionEventAdapter() {
			@Override
			public void preExecuteQuery(SessionEvent event) {
				if (armed.get() == 0 || event.getQuery() == null) {
					return;
				}
				if (event.getQuery().isReadObjectQuery() || event.getQuery().isReadAllQuery()) {
					synchronized (reads) {
						reads.add(event.getQuery().getClass().getSimpleName() + "/" + event.getQuery().getReferenceClassName());
					}
				}
			}
		});
	}

	/**
	 * The probe count is flat in the number of objects saved.
	 * <p>
	 * Twelve roots used to mean twelve keyed selects; the answer "which of these already exist"
	 * is one query. Asserting equality between two sizes rather than an absolute number keeps
	 * the case about the growth, which is the defect, and leaves room for the one warm-up query
	 * itself.
	 */
	@Test
	void theExistenceProbeDoesNotScaleWithTheNumberOfRoots() throws Exception {
		int forThree = readsForSecondSaveOf(3);
		int forTwelve = readsForSecondSaveOf(12);

		System.out.printf("### save-probe: 3 -> %d, 12 -> %d, detail=%s%n", forThree, forTwelve, reads);
		assertThat(forTwelve)
				.as("four times the objects must not mean four times the existence probes")
				.isEqualTo(forThree);
	}

	/**
	 * Writes {@code count} people, then saves the very same objects again and returns how many
	 * read queries that second save issued. The second save is the one that probes: every object
	 * has an id and a row, so {@code upsert} takes the "does it exist" path for each of them.
	 */
	private int readsForSecondSaveOf(int count) throws Exception {
		ResourceSet writeSet = newJpaResourceSet();
		Resource resource = writeSet.createResource(
				URI.createURI("jpa://" + persistenceUnitName + "/Person"));
		for (int i = 0; i < count; i++) {
			EObject person = EcoreUtil.create(personEClass);
			person.eSet(idFeature, "probe-" + count + "-" + i);
			person.eSet(nameFeature, "P" + i);
			resource.getContents().add(person);
		}
		resource.save(null);

		synchronized (reads) {
			reads.clear();
		}
		armed.incrementAndGet();
		try {
			resource.save(null);
		} finally {
			armed.decrementAndGet();
		}
		synchronized (reads) {
			return reads.size();
		}
	}

	/**
	 * A set-based update loads nothing (issue #228).
	 * <p>
	 * The TCK asserts that such a template stores the right values; it would do so whether the
	 * statement or the load path produced them. Here the read counter says which: a template of
	 * plain attribute assignments must load <b>no</b> object at all. What does show up is one
	 * {@code ReportQuery} — the {@code COUNT} the statement pays so the match count means the
	 * same thing on every flavor (MariaDB reports rows changed, not rows matched). It is counted
	 * here because EclipseLink's ReportQuery extends ReadAllQuery, and it is named rather than
	 * filtered out silently: it is the one read this path is allowed to make, and if a second
	 * one ever appears the case should fail.
	 */
	@Test
	void aSetBasedUpdateLoadsNothing() throws Exception {
		ResourceSet writeSet = newJpaResourceSet();
		Resource resource = writeSet.createResource(
				URI.createURI("jpa://" + persistenceUnitName + "/Person"));
		for (int i = 0; i < 6; i++) {
			EObject person = EcoreUtil.create(personEClass);
			person.eSet(idFeature, "upd-" + i);
			person.eSet(nameFeature, "P" + i);
			resource.getContents().add(person);
		}
		resource.save(null);

		ChangeEntry rename = StreamFactory.eINSTANCE.createChangeEntry();
		rename.setKind(DeltaKind.SET);
		rename.setFeatureId(personEClass.getFeatureID(nameFeature));
		rename.setValueNew("Renamed");
		ChangeSet template = StreamFactory.eINSTANCE.createChangeSet();
		template.getEntries().add(rename);
		UpdateCommand update = CommandFactory.eINSTANCE.createUpdateCommand();
		update.setSelector(QueryBuilder.from(personEClass).build());
		update.setTemplate(template);

		CommandResource commands = (CommandResource) newJpaResourceSet()
				.createResource(URI.createURI("jpa://" + persistenceUnitName + "/Person"));
		synchronized (reads) {
			reads.clear();
		}
		armed.incrementAndGet();
		long affected;
		try {
			affected = commands.execute(update);
		} finally {
			armed.decrementAndGet();
		}

		List<String> observed;
		synchronized (reads) {
			observed = new ArrayList<>(reads);
		}
		System.out.printf("### set-based-update: affected=%d reads=%s%n", affected, observed);
		assertThat(affected).as("every row matched").isEqualTo(6);
		assertThat(observed)
				.as("the COUNT for a flavor-independent match count, and nothing else")
				.singleElement().asString().startsWith("ReportQuery/");
		assertThat(observed).as("no object is loaded to be patched")
				.noneMatch(read -> read.startsWith("ReadObjectQuery"));
	}
}
