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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.model.query.NullPrecedence;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.SortDirection;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.eclipse.fennec.persistence.eclipselink.JpaFlavor;
import org.eclipse.fennec.persistence.eclipselink.spi.JPAResourceFactory;
import org.eclipse.fennec.persistence.query.QueryException;
import org.eclipse.fennec.persistence.query.api.QueryResult;
import org.eclipse.fennec.persistence.query.api.QueryableResource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManagerFactory;

/**
 * The null-order fallback against the database under test (issue #365, run per
 * {@code -Djpa.test.flavor}).
 * <p>
 * The TCK proves the placements with the fallback on and the flavor known — the default. This
 * covers the other two corners: the {@code CASE} key every database gets when the flavor is
 * unknown, which h2 and PostgreSQL otherwise never see, and the refusal when the fallback is
 * switched off, which has to come as a Diagnostic before the database is asked.
 *
 * @author Mark Hoffmann
 * @since 07.10.2026
 */
class JpaNullOrderFallbackTest {

	static {
		// Same doctrine as AbstractPersistenceTCK (issue #79).
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static final String PU_NAME = "nullorder";

	/** Alice and Carol have a birthday, Bob has none; Carol is the older. */
	private static final Instant ALICE_BIRTHDAY = Instant.parse("1996-03-15T10:30:45Z");
	private static final Instant CAROL_BIRTHDAY = Instant.parse("1976-12-31T00:00:05Z");

	private EPackage tckPackage;
	private EClass personClass;
	private EStructuralFeature personName;
	private EStructuralFeature personBirthday;

	private EntityManagerFactory emf;

	@BeforeEach
	void setUp() throws IOException {
		tckPackage = loadModel();
		personClass = (EClass) tckPackage.getEClassifier("Person");
		personName = personClass.getEStructuralFeature("name");
		personBirthday = personClass.getEStructuralFeature("birthday");

		List<EClassifier> eClasses = new ArrayList<>();
		eClasses.add(personClass);
		eClasses.add(tckPackage.getEClassifier("Address"));
		eClasses.add(tckPackage.getEClassifier("Company"));
		emf = JpaTckSupport.bootstrap(PU_NAME, eClasses);

		ResourceSet writeSet = resourceSet(null, true);
		Resource resource = writeSet.createResource(personUri());
		resource.getContents().add(newPerson(1, "Alice", ALICE_BIRTHDAY));
		resource.getContents().add(newPerson(2, "Bob", null));
		resource.getContents().add(newPerson(3, "Carol", CAROL_BIRTHDAY));
		resource.save(null);
	}

	@AfterEach
	void tearDown() {
		if (nonNull(emf)) {
			emf.close();
			emf = null;
		}
	}

	/** The CASE key is portable: an unknown flavor sorts all four placements right. */
	@Test
	void theCaseKeySortsEveryPlacementOnThisDatabase() throws Exception {
		assertOrder(JpaFlavor.UNKNOWN, true, SortDirection.ASC, NullPrecedence.FIRST, "Bob", "Carol", "Alice");
		assertOrder(JpaFlavor.UNKNOWN, true, SortDirection.DESC, NullPrecedence.LAST, "Alice", "Carol", "Bob");
		assertOrder(JpaFlavor.UNKNOWN, true, SortDirection.ASC, NullPrecedence.LAST, "Carol", "Alice", "Bob");
		assertOrder(JpaFlavor.UNKNOWN, true, SortDirection.DESC, NullPrecedence.FIRST, "Bob", "Alice", "Carol");
	}

	/**
	 * Without the fallback, the flavor under test serves what it spells by itself and refuses
	 * the rest with a Diagnostic — on MariaDB null above every value, on h2 and PostgreSQL
	 * nothing.
	 */
	@Test
	void withoutTheFallbackOnlyWhatTheDatabaseSpellsIsServed() throws Exception {
		JpaFlavor flavor = JpaFlavor.byId(JpaTestSupport.flavor()).orElseThrow();
		assertOrServedOrRefused(flavor, SortDirection.ASC, NullPrecedence.FIRST, "Bob", "Carol", "Alice");
		assertOrServedOrRefused(flavor, SortDirection.DESC, NullPrecedence.LAST, "Alice", "Carol", "Bob");
		assertOrServedOrRefused(flavor, SortDirection.ASC, NullPrecedence.LAST, "Carol", "Alice", "Bob");
		assertOrServedOrRefused(flavor, SortDirection.DESC, NullPrecedence.FIRST, "Bob", "Alice", "Carol");
	}

	/** An unknown flavor without the fallback cannot place null at all. */
	@Test
	void withoutTheFallbackAnUnknownFlavorRefusesEveryPlacement() {
		assertRefused(JpaFlavor.UNKNOWN, SortDirection.DESC, NullPrecedence.LAST);
		assertRefused(JpaFlavor.UNKNOWN, SortDirection.DESC, NullPrecedence.FIRST);
	}

	private void assertOrServedOrRefused(JpaFlavor flavor, SortDirection direction, NullPrecedence nulls,
			String... expected) throws Exception {
		boolean nullsLow = (nulls == NullPrecedence.FIRST) != (direction == SortDirection.DESC);
		if (flavor.needsNullCaseKey(nullsLow)) {
			assertRefused(flavor, direction, nulls);
		} else {
			assertOrder(flavor, false, direction, nulls, expected);
		}
	}

	private void assertOrder(JpaFlavor flavor, boolean fallback, SortDirection direction, NullPrecedence nulls,
			String... expected) throws Exception {
		try (QueryResult result = queryable(flavor, fallback).query(query(direction, nulls))) {
			assertThat(result.objects().map(person -> person.eGet(personName)))
					.as("%s, fallback %s: %s nulls %s", flavor.id(), fallback, direction, nulls)
					.containsExactly((Object[]) expected);
		}
	}

	private void assertRefused(JpaFlavor flavor, SortDirection direction, NullPrecedence nulls) {
		assertThatThrownBy(() -> queryable(flavor, false).query(query(direction, nulls)))
				.as("%s without fallback: %s nulls %s", flavor.id(), direction, nulls)
				.isInstanceOf(IOException.class)
				.satisfies(failure -> assertThat(diagnosticIn(failure)).as("refused with a Diagnostic").isTrue());
	}

	private static boolean diagnosticIn(Throwable failure) {
		for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
			if (cause instanceof QueryException queryException && queryException.getDiagnostic() != null) {
				return true;
			}
		}
		return false;
	}

	private Query query(SortDirection direction, NullPrecedence nulls) {
		return QueryBuilder.from(personClass).orderBy(direction, nulls, personBirthday).build();
	}

	private QueryableResource queryable(JpaFlavor flavor, boolean fallback) {
		return (QueryableResource) resourceSet(flavor, fallback).createResource(personUri());
	}

	private ResourceSet resourceSet(JpaFlavor flavor, boolean fallback) {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(tckPackage.getNsURI(), tckPackage);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("jpa", new JPAResourceFactory(emf, null, flavor, fallback));
		return resourceSet;
	}

	private URI personUri() {
		return URI.createURI("jpa://" + PU_NAME + "/Person");
	}

	private EObject newPerson(int id, String name, Instant birthday) {
		EObject person = EcoreUtil.create(personClass);
		person.eSet(personClass.getEStructuralFeature("pid"), id);
		person.eSet(personName, name);
		if (birthday != null) {
			person.eSet(personBirthday, Date.from(birthday));
		}
		return person;
	}

	private EPackage loadModel() throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new XMIResourceFactoryImpl());
		Resource resource = resourceSet.createResource(URI.createURI("tck.ecore"));
		try (InputStream stream = AbstractPersistenceTCK.class.getResourceAsStream("tck.ecore")) {
			resource.load(stream, null);
		}
		EPackage ePackage = (EPackage) resource.getContents().get(0);
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		return ePackage;
	}
}
